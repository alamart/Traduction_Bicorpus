import java.util.HashMap;

public class Cooccurences {

    private HashMap<String, Integer> cooc;

    public HashMap<String, Integer> getCooc() {
        return cooc;
    }

    public void setCooc(HashMap<String, Integer> cooc) {
        this.cooc = cooc;
    }

    public Cooccurences(){
        this.setCooc(new HashMap<String, Integer>());
    }
}
