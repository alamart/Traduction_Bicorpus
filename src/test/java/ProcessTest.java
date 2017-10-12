import static org.junit.Assert.*;

public class ProcessTest {

    public static void main(String[] args) throws Exception{

        String prefixPath = "test_";

        BiCorpus bcEN = Process.init(prefixPath + "corpus/BAF-full.en", prefixPath + "corpus/BAF-full.fr", "English To French");
        BiCorpus bcFR = Process.init(prefixPath + "corpus/BAF-full.fr", prefixPath + "corpus/BAF-full.en", "Français vers Anglais");

        System.out.println(Process.resultMI(bcEN, "used"));

    }

}