import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LanguageModel {
    //Language name
    private String languageName;
    //Language tokens and their frequencies
    private Map<String, Integer> histogram = new HashMap<>();
    //Class that had utility methods
    private Utils utils = new Utils();

    //Default constructor
    public LanguageModel() {
    }

    //Creates a language model by adding tokens and their frequencies to the histogram
    public synchronized void createLanguageModel(Path file, int n) throws IOException {
        List<String> tokens;
        Pattern pattern = Pattern.compile("\\s+");

        //Tokenizes each word
        tokens = Files.lines(file)
                .map(line -> line.replaceAll("(?!')\\p{P}", "").replaceAll("'", ""))
                .flatMap(line -> pattern.splitAsStream(line))
                .map(w -> tokenize(w.toLowerCase(), n))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        //Puts each token in the histogram
        tokens.stream()
                .forEach(t -> createHistogram(t));
    }

    //Calculates the frequency of each tokens
    public Map<String, Integer> createHistogram(String token) {
        int count = histogram.containsKey(token) ? histogram.get(token) : 0;
        histogram.put(token, count + 1);
        return histogram;    }

    //Creates the n-gram of each word
    public List<String> tokenize(String word, int n) {
        List<String> tokens = null;
        if (n == 1) {
            String[] unigram = word.split("");
            tokens = Arrays.stream(unigram)
                    .collect(Collectors.toList());
        } else if (n == 2) {
            if (word.length() == 1) {
                tokens = tokenize(word, 1);
            } else {
                tokens = IntStream.range(0, word.length() - n + 1)
                        .mapToObj(i -> word.substring(i, i + n))
                        .collect(Collectors.toList());
            }
        } else if (n == 3) {
            if (word.length() <= 2) {
                tokens = tokenize(word, 2);
            } else {
                tokens = IntStream.range(0, word.length() - n + 1)
                        .mapToObj(i -> word.substring(i, i + n))
                        .collect(Collectors.toList());
            }
        }
        return tokens;
    }

    //Calculates the dot products of two histograms
    public int dotProduct(Map<String, Integer> language) {
        Map<String, Integer> histogramCopy = new HashMap<>();
        histogramCopy.putAll(this.histogram);
        int result = IntStream.range(0, histogramCopy.size())
                .map(i -> utils.getDotProduct(histogramCopy, language, i))
                .reduce(0, Integer::sum);

        return result;
    }

    //Calculates the norm of histogram
    public double norm() {
        List<Map.Entry<String, Integer>> listOfEntries = this.histogram.entrySet().stream()
                .collect(Collectors.toList());
        int sumOfSquares = IntStream.range(0, this.histogram.size())
                .map(i -> (int) Math.pow(listOfEntries.get(i).getValue(), 2))
                .sum();
        double result = Math.sqrt(sumOfSquares);
        return result;
    }

    //Implements similarity formula
    public double similarity(int dotProcuct, double norm1, double norm2) {
        return dotProcuct / (norm1 * norm2);
    }

    //Calculates similarity of two language models
    public double calculateSimilarity(LanguageModel languageModel) {
        double mysteryNorm = this.norm();
        double languageNorm = languageModel.norm();
        int dotProduct = this.dotProduct(languageModel.getHistogram());
        return similarity(dotProduct, mysteryNorm, languageNorm);
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public Map<String, Integer> getHistogram() {
        return histogram;
    }

    @Override
    public String toString() {
        return "LanguageModel{" +
                "languageName='" + languageName + '\'' +
                ", histogram=" + histogram +
                '}';
    }
}
