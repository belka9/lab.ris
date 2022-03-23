import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {

            Options options = new Options();
            options.addOption("f", "file", true, "XML file with data");
            options.addOption("l", "limit", true, "Limit number of XML file");
            DefaultParser parser = new DefaultParser();
            CommandLine cmdLine = null;
            try {
                cmdLine = parser.parse(options, args);
            } catch (ParseException e) {
                e.printStackTrace();
                System.out.println("Wrong arguments. Usage: -f\n" +
                        "path/to/file/to/be/parsed.bz2 (xml-archive)" +
                        "-l\n" +
                        "limit_of_xml_elements_to_be_processed");
                Slf4jLogger.logError("Unable to parse program arguments.");
                return;
            }
            assert cmdLine != null;
            String limitStr = cmdLine.getOptionValue("l", "all");
            String filePath = cmdLine.getOptionValue("f");

            if (filePath != null) {
                StAXProcessor xmlProcessor = new StAXProcessor(limitStr, filePath);
                System.out.println("Limit of elements to process: " + xmlProcessor.getLimit());
                System.out.println("Input xml file: " + xmlProcessor.getXMLfilePath());
                System.out.println("Started counting...Please wait");

                try {
                    xmlProcessor.calcStat();
                } catch (XMLStreamException | IOException ex) {
                    ex.printStackTrace();
                    Slf4jLogger.logError("Error while caclulating statistic.");
                    return;
                }

                ResultPrinter resultPrinter = new ResultPrinter(xmlProcessor);
                resultPrinter.printDelimiter();
                resultPrinter.printUsersChangesets();
                resultPrinter.printDelimiter();
                resultPrinter.printNumberOfSpecifiedNodes();
                resultPrinter.printTimeSpent();
                resultPrinter.printNumberOfAllProcessedNodes();
            }
        } catch (Exception e){
            e.printStackTrace();

        }
    }

}
