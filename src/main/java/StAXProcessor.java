import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class StAXProcessor {
    private static final String MAX_DEFINED_LIMIT = "all";
    private static final int SIZE_OF_BUFFER = 4096 * 32;
    private static final String ENCODING = "UTF-8";
    private static final String NODE_ELEMENT = "node";
    private static final String TAG_ELEMENT = "tag";
    private static final String COUNTED_ELEMENT_NAME = NODE_ELEMENT;
    private static final String UNKNOWN_PARENT = "";

    private final int limit;
    private final String xmlFilePath;

    private final Map<String, Map<String, Integer>> stat;
    private long timeSpent;
    private int elementsCounter = 0;
    private int specifiedNodesCounter = 0;

    private static final Map<String, IElementProcessor> processors = new HashMap<>();

    static {
        processors.put(NODE_ELEMENT, new NodeProcessor());
        processors.put(TAG_ELEMENT, new TagProcessor());
    }

    public StAXProcessor(String limitStr, String xmlFilePath) {

        this.limit = limitStr.equals(MAX_DEFINED_LIMIT) ? Integer.MAX_VALUE : Integer.parseInt(limitStr);
        this.xmlFilePath = xmlFilePath;
        stat = new HashMap<>();
        elementsCounter = processors.get(COUNTED_ELEMENT_NAME).getElementCounter();
        specifiedNodesCounter = processors.get(TAG_ELEMENT).getElementCounter();

    }

    public void calcStat() throws XMLStreamException, IOException {
        Slf4jLogger.logInfo("Started calculating statistic.");
        long start = System.currentTimeMillis();
        readData();
        Slf4jLogger.logInfo("Finished calculating statistic.");
        long end = System.currentTimeMillis();
        timeSpent = end - start;
    }

    public int getLimit() {
        return limit;
    }

    public String getXMLfilePath() {
        return xmlFilePath;
    }

    private void readData() throws IOException, XMLStreamException {
        try (InputStream in = new BZip2CompressorInputStream(new BufferedInputStream(new FileInputStream(xmlFilePath), SIZE_OF_BUFFER))) {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(in, ENCODING);

            String parentElementName = UNKNOWN_PARENT;
            while (elementsCounter < limit && reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    String startElementName = startElement.getName().getLocalPart();
                    if (startElementName.equals(COUNTED_ELEMENT_NAME)) {
                        Slf4jLogger.logInfo("Started processing new element: " + startElementName);
                        parentElementName = startElementName;
                    }
                    try {
                        IElementProcessor currentProcessor = processors.get(startElementName);
                        currentProcessor.processElement(stat, startElement, parentElementName);
                        if (currentProcessor.elementIsProcessed() && startElementName.equals(TAG_ELEMENT)) {
                            while (!(nextEvent.isEndElement() && nextEvent.asEndElement().getName().getLocalPart().equals(parentElementName))
                                && reader.hasNext()) {
                                Slf4jLogger.logInfo("Skipping child (" + nextEvent + ") for parent " + parentElementName);
                                nextEvent = reader.nextEvent();
                            }
                            //Slf4jLogger.logInfo("Finished processing element:" + nextEvent.asEndElement().getName().getLocalPart());
                        }
                    } catch (NullPointerException e) {
                        //log this info!
                        //no such processor found!
                        //System.out.println("Processor for " + startElementName + " is not found.");
                        Slf4jLogger.logWarning("Processor for " + startElementName + " is not found.");
                        continue;
                    }
                } else if (nextEvent.isEndElement() && nextEvent.asEndElement().getName().getLocalPart().equals(parentElementName)) {
                    Slf4jLogger.logInfo("Finished processing element:" + nextEvent.asEndElement().getName().getLocalPart());
                    parentElementName = UNKNOWN_PARENT;//конец ноды
                }
                elementsCounter = processors.get(COUNTED_ELEMENT_NAME).getElementCounter();
            }
            specifiedNodesCounter = processors.get(TAG_ELEMENT).getElementCounter();
        }
    }

    public long getTimeSpent() {
        return timeSpent;
    }

    public Map<String, Map<String, Integer>> getUsersChangesetsStat() {
        return stat;
    }

    public int getElementsCounter() {
        return elementsCounter;
    }

    public int getSpecifiedNodesCounter() {
        return specifiedNodesCounter;
    }
}