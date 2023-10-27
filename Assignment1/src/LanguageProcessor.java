import java.io.IOException;
import java.nio.file.Path;
public class LanguageProcessor implements Runnable {
    //The path of the text file
    private Path file;
    //The number of N-Gram
    private int n;
    //The language model
    private LanguageModel languageModel;

    //Default constructor
    public LanguageProcessor() {

    }

    //Constructor with fields as parameters
    public LanguageProcessor(Path file, int n, LanguageModel languageModel) {
        this.file = file;
        this.n = n;
        this.languageModel = languageModel;
    }

    @Override
    public void run() {
        try {
           //Creates language model while reading the files
           languageModel.createLanguageModel(file,n);
           languageModel.setLanguageName(file.getParent().getFileName().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "LanguageProcessor{" +
                "file=" + file +
                ", n=" + n +
                ", histogram=" + languageModel.getHistogram() +
                '}';
    }
}
