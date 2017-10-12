import static org.junit.Assert.*;

public class BiPhraseTest {

    private static String sourceEN = "This report was prepared in connection with work carried out by Centre for Information  Technology Innovation (CITI) of Industry Canada.";
    private static String sourceFR = "Ce document fait état de travaux de recherche réalisés dans le cadre des activités du Centre  d'innovation en technologies de l'information (CITI).";


    public static void main(String[] args) throws Exception{

        BiPhrase bp = new BiPhrase(sourceEN, sourceFR);


    }
}