import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Process {
    private static final Logger logger = LogManager.getLogger(BiCorpus.class);
    private static double alpha = 10;
    private static double beta = 9;
    private static double gamma = 1;
    private static double sigma = 0.1;


    public static BiCorpus  init (String srcFilePath, String targetFilePath, String biCorpusName){
        long startTime = System.currentTimeMillis();


        logger.trace("Creating the Bicorpus \"{}\"", biCorpusName);

        BiCorpus bc = new BiCorpus();

        bc.setDisplayName(biCorpusName);
        bc.setCorpus(new ArrayList<BiPhrase>());
        bc.setCountSourceWords(new HashMap<String, Integer>());
        bc.setCountTargetWords(new HashMap<String, Integer>());
        bc.setCoocurencesTable(new HashMap<String, Cooccurences>());

        try {
            Path srcPath = Paths.get(Process.class.getResource(srcFilePath).toURI());
            Path targetPath = Paths.get(Process.class.getResource(targetFilePath).toURI());

            bc.setSrcLines( Files.lines(srcPath).collect(Collectors.toList()) );
            bc.setTargetLines( Files.lines(targetPath).collect(Collectors.toList()) );

            createCorpus(bc);
            logCorpusInfos(bc);


        }
        catch (IOException ioException){
            logger.error("Error during the reading of the files {} and {}", srcFilePath, targetFilePath);
            logger.error(ioException.getMessage());
        }
        catch (URISyntaxException uriException){
            logger.error(uriException.getMessage());
        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        logger.trace("Ending the creation of the Bicorpus \"{}\"", biCorpusName);
        logger.info("Execution time of the initialization of the Bicorpus \"{}\" : {} ms", biCorpusName, elapsedTime);

        return bc;

    }

    private static String getInfos(BiCorpus biCorpus){
        StringBuilder result = new StringBuilder("Information about The BiCorpus \"").append( biCorpus.getDisplayName()).append("\"").append("\n")
                .append("Number of Source lines : ").append(biCorpus.getSrcLines().size()).append("\n")
                .append("Number of Target lines : ").append(biCorpus.getTargetLines().size()).append("\n")
                .append("Number of BiPhrases : ").append(biCorpus.getCorpus().size());

        return result.toString();
    }

    private static void logCorpusInfos(BiCorpus biCorpus){
        logger.info(getInfos(biCorpus));
    }

    private static void createCorpus(BiCorpus biCorpus){
        String omittedCharacters = "[\\.-\\?!,()]";
        List<String> sourceSet = biCorpus.getSrcLines();
        List<String> targetSet = biCorpus.getTargetLines();
        HashMap<String, WordScore> alignements = new HashMap<>();

        ArrayList<BiPhrase> corpus = new ArrayList<BiPhrase>();

        for(int i = 0 ; i < Math.min(sourceSet.size(), targetSet.size()); i++){
            String sourcePhrase = sourceSet.get(i).replaceAll(omittedCharacters, " ");
            String targetPhrase = targetSet.get(i).replaceAll(omittedCharacters, " ");

            logger.trace("Creating BiPhrase src: {} AND target: {}", sourcePhrase, targetPhrase);


            String[] arraySourcePhrase = sourcePhrase.split(" ");
            String[] arrayTargetPhrase = targetPhrase.split(" ");

            BiPhrase bp = new BiPhrase(Arrays.asList(arraySourcePhrase), Arrays.asList(arrayTargetPhrase));

            addAlignementToAlignements(alignements, arraySourcePhrase, arrayTargetPhrase);

            biCorpus.setAlignements(alignements);

            corpus.add(bp);
            countAllWordsInBiPhrase(biCorpus, bp);

            logger.trace("End creation BiPhrase src: {} AND target: {}", sourcePhrase, targetPhrase);

        }

        biCorpus.setCorpus(corpus);

        logger.trace("Creating Coocurences Table");
        createCoocurencesTable(biCorpus);
        logger.trace("End creation coocurences Table");

    }

    private static void countAllWordsInBiPhrase(BiCorpus biCorpus, BiPhrase bp){
        HashSet<String> sourceSet=new HashSet<>(bp.getSource());
        HashSet<String> targetSet=new HashSet<>(bp.getTarget());

        HashMap<String, Integer> countSourceWords = biCorpus.getCountSourceWords();
        HashMap<String, Integer> countTargetWords = biCorpus.getCountTargetWords();

        for(String word : sourceSet){
            if (countSourceWords.containsKey(word)){
                countSourceWords.put(word, (countSourceWords.get(word)+1));
            }
            else{
                countSourceWords.put(word, 1);
            }
        }
        for(String word : targetSet){
            if (countTargetWords.containsKey(word)){
                countTargetWords.put(word, (countTargetWords.get(word)+1));
            }
            else{
                countTargetWords.put(word, 1);
            }
        }
    }

    private static void createCoocurencesTable(BiCorpus biCorpus){
        for (BiPhrase bp : biCorpus.getCorpus()){
            List<String> sourceBP = bp.getSource();
            List<String> targetBP = bp.getTarget();

            HashMap<String, Cooccurences> cooccurencesHashMap = biCorpus.getCoocurencesTable();
            for (String sourceWord : sourceBP){

                if(!cooccurencesHashMap.containsKey(sourceWord)){
                    cooccurencesHashMap.put(sourceWord, new Cooccurences());
                }
                for(String targetWord : targetBP){
                    Cooccurences coocs = cooccurencesHashMap.get(sourceWord);
                    if (!coocs.getCooc().containsKey(targetWord)){
                        coocs.getCooc().put(targetWord, 1);
                    }
                    else{
                        coocs.getCooc().put(targetWord, coocs.getCooc().get(targetWord) + 1);
                    }
                }
            }
        }
    }

    private static void addAlignementToAlignements(HashMap<String, WordScore> alignements, String[] source, String[] target){
        for (int i = 0; i < source.length; i++){
            for(int j = 0; j< target.length; j++){
                WordScore wordScoreWordI = new WordScore().setWordScore(new HashMap<String, Double>());
                Double score = 1d;
                if (alignements.containsKey(source[i])){
                    wordScoreWordI = alignements.get(source[i]);
                }
                if (wordScoreWordI.getWordScore().containsKey(target[j])){
                    score = wordScoreWordI.getWordScore().get(target[j]);
                }

                if (j == i )
                    wordScoreWordI.getWordScore().put(target[j], score * Process.alpha);
                else if ( Math.abs(i-j) == 3)
                    wordScoreWordI.getWordScore().put(target[j], score * Process.beta);
                else if ( Math.abs(i-j) == 4)
                    wordScoreWordI.getWordScore().put(target[j], score * Process.gamma);
                else
                    wordScoreWordI.getWordScore().put(target[j], score * Process.sigma);

                alignements.put(source[i], wordScoreWordI);
            }
        }
    }


    private static int getCoocurence(BiCorpus biCorpus, String sourceWord, String targetWord){
        Cooccurences cooc = biCorpus.getCoocurencesTable().get(sourceWord);
        if(cooc == null) {
            logger.error("The word \"{}\" is not in the corpus but is considered as a source word", sourceWord);
            return 0;
        }

        return cooc.getCooc().get(targetWord) == null ? 0 : cooc.getCooc().get(targetWord);
    }

    private static double calculMI(BiCorpus biCorpus, String sourceWord, String targetWord){
        Double score = biCorpus.getAlignements().get(sourceWord).getWordScore().get(targetWord);
        int n=biCorpus.getCorpus().size();
        int n1A=biCorpus.getCountSourceWords().get(sourceWord);
        int n1B=biCorpus.getCountTargetWords().get(targetWord);
        int n11=getCoocurence(biCorpus, sourceWord, targetWord);
        double[] nA = new double[]{n-n1A,n1A};
        double[] nB = new double[]{n-n1B,n1B};
        double[] nAB  = new double[] {n-n1A-n1B,n1A-n11,n1B-n11,n11};

        double sum=nAB[0]*Math.log(nAB[0]/(nA[0]*nB[0]))+
                nAB[1]*Math.log(1 + nAB[1]/(nA[1]*nB[0]))+
                nAB[2]*Math.log(1 + nAB[2]/(nA[0]*nB[1]))+
                nAB[3]*Math.log(1 + nAB[3]/(nA[1]*nB[1]));
        return -1 * score * sum;
    }

    public static List<Map.Entry<String, Double>> resultMI(BiCorpus biCorpus, String sourceWord){
        HashMap<String, Double> result=new HashMap<String, Double>();
        for (String coocurentWord :biCorpus.getCoocurencesTable().get(sourceWord).getCooc().keySet()){
            result.put(coocurentWord, calculMI(biCorpus, sourceWord, coocurentWord));

        }

        return MapUtil.sortByValue(result);
    }
}
