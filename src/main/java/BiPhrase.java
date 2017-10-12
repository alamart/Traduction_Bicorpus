import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BiPhrase {

    private List<String> source;
    private List<String> target;

    public List<String> getSource() {
        return source;
    }

    public void setSource(List<String> source) {
        this.source = source;
    }

    public List<String> getTarget() {
        return target;
    }

    public void setTarget(List<String> target) {
        this.target = target;
    }

    public BiPhrase(List<String> source , List<String> target){
        this.setSource(source);
        this.setTarget(target);
    }

    public BiPhrase(String sourcePhrase, String targetPhrase){
        String[] arraySourcePhrase = sourcePhrase.split(" ");
        String[] arrayTargetPhrase = targetPhrase.split(" ");

        this.setSource(Arrays.asList(arraySourcePhrase));
        this.setTarget(Arrays.asList(arrayTargetPhrase).stream().collect(Collectors.toList()));
    }

}
