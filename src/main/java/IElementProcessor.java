import javax.xml.stream.events.StartElement;
import java.util.Map;

public interface IElementProcessor {
    void processElement(Map<String, Map<String, Integer>> statToUpdate, StartElement startElement, String parentElementName);
    boolean elementIsProcessed();
    int getElementCounter();
}
