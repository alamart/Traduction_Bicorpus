import java.util.ArrayList;
import java.util.HashMap;

public class Alignement {
    HashMap<String, ArrayList<WordScore>> alignements;

    public HashMap<String, ArrayList<WordScore>> getAlignements() {
        return alignements;
    }

    public Alignement setAlignements(HashMap<String, ArrayList<WordScore>> alignements) {
        this.alignements = alignements;
        return this;
    }


}
