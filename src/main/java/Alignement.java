public class Alignement {

    public BiPhrase getBiPhrase() {
        return biPhrase;
    }

    public Alignement setBiPhrase(BiPhrase biPhrase) {
        this.biPhrase = biPhrase;
        return this;
    }

    public Integer getSourceAlignement() {
        return sourceAlignement;
    }

    public Alignement setSourceAlignement(Integer sourceAlignement) {
        this.sourceAlignement = sourceAlignement;
        return this;
    }

    public Integer getTargetAlignement() {
        return targetAlignement;
    }

    public Alignement setTargetAlignement(Integer targetAlignement) {
        this.targetAlignement = targetAlignement;
        return this;
    }

    public Double getProbability() {
        return probability;
    }

    public Alignement setProbability(Double probability) {
        this.probability = probability;
        return this;
    }

    private BiPhrase biPhrase;
    private Integer sourceAlignement;
    private Integer targetAlignement;
    private Double probability;




}
