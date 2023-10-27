import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {

    public int getDotProduct(Map<String, Integer> histogram, Map<String, Integer> language, int index) {
        List<Map.Entry<String, Integer>> listOfEntries = histogram.entrySet()
                .stream()
                .collect(Collectors.toList());
        Map.Entry<String, Integer> current = listOfEntries.get(index);
        return current.getValue() * (language.containsKey(current.getKey()) ? language.get(current.getKey()) : 0);
    }

    public void printResults(List<Map.Entry<String, Double>> results) {
        Map.Entry<String, Double> biggestSimilarity = results.get(results.size() - 1);
        System.out.printf("The language model with the biggest similarity is: %s-> %.2f \n"
                , biggestSimilarity.getKey(), biggestSimilarity.getValue());
        results.remove(results.size()-1);
        System.out.println("Similarity with other languages:");
        results.stream()
                .forEach(r -> System.out.printf(r.getKey() + "-> %.2f \n", r.getValue()));

    }
}
