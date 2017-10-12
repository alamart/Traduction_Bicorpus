import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BiCorpus {


    //Stocke le corpus parallele
    private ArrayList<BiPhrase> corpus;
    private List<String> srcLines;
    private List<String> targetLines;
    private String displayName;
    private HashMap<String, Integer> countSourceWords;
    private HashMap<String, Integer> countTargetWords;
    private HashMap<String, Cooccurences> coocurencesTable;
    private HashMap<String, WordScore> alignements;

    public HashMap<String, WordScore> getAlignements() {
        return alignements;
    }

    public void setAlignements(HashMap<String, WordScore> alignements) {
        this.alignements = alignements;
    }


    public HashMap<String, Cooccurences> getCoocurencesTable() {
        return coocurencesTable;
    }

    public void setCoocurencesTable(HashMap<String, Cooccurences> coocurencesTable) {
        this.coocurencesTable = coocurencesTable;
    }


    public HashMap<String, Integer> getCountSourceWords() {
        return countSourceWords;
    }

    public void setCountSourceWords(HashMap<String, Integer> countSourceWords) {
        this.countSourceWords = countSourceWords;
    }

    public HashMap<String, Integer> getCountTargetWords() {
        return countTargetWords;
    }

    public void setCountTargetWords(HashMap<String, Integer> countTargetWords) {
        this.countTargetWords = countTargetWords;
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getSrcLines() {
        return srcLines;
    }

    public void setSrcLines(List<String> srcLines) {
        this.srcLines = srcLines;
    }

    public List<String> getTargetLines() {
        return targetLines;
    }

    public void setTargetLines(List<String> targetLines) {
        this.targetLines = targetLines;
    }

    public ArrayList<BiPhrase> getCorpus() {
        return corpus;
    }

    public void setCorpus(ArrayList<BiPhrase> corpus) {
        this.corpus = corpus;
    }


}
