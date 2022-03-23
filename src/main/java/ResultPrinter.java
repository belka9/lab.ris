import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ResultPrinter {
    private static final int CHANGES_NUMBER_INDEX = 0;
    private static final String KEY_VALUE_DELIMITER = " - ";

    private final StAXProcessor processor;

    public ResultPrinter(StAXProcessor processor) {
        this.processor = processor;
    }
    public void printUsersChangesets() {
        Map<String, Map<String, Integer>> result = processor.getUsersChangesetsStat();
        System.out.println("uid/username" + KEY_VALUE_DELIMITER + "number of changes");
        result.entrySet().stream()
                .map(entry -> {
                    String key = entry.getKey();
                    Map<String, Integer> changes = entry.getValue();
                    int sum = changes.values().stream().mapToInt(Integer::intValue).sum();
                    return new AbstractMap.SimpleEntry<>(key, new int[]{changes.size(), sum});
                })
                .sorted(Comparator.comparingInt(a -> -a.getValue()[CHANGES_NUMBER_INDEX]))
                .forEach(entry -> {
                    System.out.println(entry.getKey()  + KEY_VALUE_DELIMITER + entry.getValue()[CHANGES_NUMBER_INDEX]);
                });
    }

    public void printTimeSpent() {
        long time = processor.getTimeSpent();
        System.out.printf("Time spent: %d min, %d sec%n",
                TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
        );
    }

    public void printDelimiter() {
        System.out.println("-------------------------------------------------------------------------------------");
    }

    public void printNumberOfAllProcessedNodes() {
        System.out.println("Read elememts: " + processor.getElementsCounter() + " of " + processor.getLimit());
    }

    public void printNumberOfSpecifiedNodes() {
        System.out.println("Number of nodes with tag \"name\": " + processor.getSpecifiedNodesCounter());
    }
}