import java.util.HashMap;

public class WordScore {
    public HashMap<String, Double> getWordScore() {
        return wordScore;
    }

    public WordScore setWordScore(HashMap<String, Double> wordScore) {
        this.wordScore = wordScore;
        return this;
    }

    private HashMap<String, Double> wordScore;

}
