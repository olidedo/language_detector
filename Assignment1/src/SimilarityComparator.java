import java.util.Comparator;
import java.util.Map;

public class SimilarityComparator implements Comparator<Map.Entry<String, Double>> {
    @Override
    public int compare(Map.Entry<String, Double> t1, Map.Entry<String, Double> t2) {
        if (t1.getValue() > t2.getValue()) {
            return 1;
        }
        if (t2.getValue() > t1.getValue()) {
            return -1;
        }
        return 0;
    }
}
