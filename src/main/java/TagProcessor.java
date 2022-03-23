import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import java.util.Map;

public class TagProcessor implements IElementProcessor {

    private static final String KEY_TO_FIND = "name";

    private boolean elementIsProcessed = false;

    private int nodeCounter = 0;

    @Override
    public void processElement(Map<String, Map<String, Integer>> statToUpdate, StartElement startElement, String parentElementName) {
        elementIsProcessed = false;

        QName tagKeyAttr = new QName("k");
        Attribute tagKey = startElement.getAttributeByName(tagKeyAttr);

        String tagKeyValue = tagKey.getValue();
        //if key name contains "name";
        if(tagKeyValue.lastIndexOf(KEY_TO_FIND) != -1) {
            Slf4jLogger.logInfo("Found " + KEY_TO_FIND + " at " + startElement.getName().getLocalPart());
            elementIsProcessed = true;
            nodeCounter++;
        }
    }

    @Override
    public boolean elementIsProcessed() {
        return elementIsProcessed;
    }

    @Override
    public int getElementCounter() {
        return nodeCounter;
    }
}