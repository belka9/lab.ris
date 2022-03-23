import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import java.util.HashMap;
import java.util.Map;

public class NodeProcessor implements IElementProcessor{
    private final static String INFO_DELIMITER = "/";

    private int nodeCounter = 0;

    private final QName uidAttr;
    private final QName userAttr;
    private final QName changeSetAttr;

    private boolean elementIsProcessed = false;

    public NodeProcessor() {
        uidAttr = new QName("uid");
        userAttr = new QName("user");
        changeSetAttr = new QName("changeset");
    }

    @Override
    public void processElement(Map<String, Map<String, Integer>> statToUpdate, StartElement startElement, String parentElementName) {
        elementIsProcessed = false;
        Attribute uid = startElement.getAttributeByName(uidAttr);
        Attribute user = startElement.getAttributeByName(userAttr);
        Attribute changeSet = startElement.getAttributeByName(changeSetAttr);

        String id = uid.getValue() + INFO_DELIMITER + user.getValue();
        statToUpdate.putIfAbsent(id, new HashMap<>());
        statToUpdate.get(id).merge(changeSet.toString(), 1, Integer::sum);

        elementIsProcessed = true;
        nodeCounter++;
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