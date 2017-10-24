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
    private static double beta = 10;
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
        bc.setAlignements(new HashSet<Alignement>());

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
        String omittedCharacters = "[\\.-\\?!,()']";
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

            biCorpus.setScoresByWord(alignements);

            corpus.add(bp);
            //createAlignement(biCorpus, bp);
            countAllWordsInBiPhrase(biCorpus, bp);

            logger.trace("End creation BiPhrase src: {} AND target: {}", sourcePhrase, targetPhrase);

        }

        biCorpus.setCorpus(corpus);


        logger.trace("Creating Coocurences Table");
        createCoocurencesTable(biCorpus);
        logger.trace("End creation coocurences Table");
        predictAlignment(biCorpus);

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

    private static void createAlignement(BiCorpus bc, BiPhrase bp){
        List<String> sourcePhrase = bp.getSource();
        List<String> targetPhrase = bp.getTarget();
        int i = 0;
        int j = 0;
        for (String sourceWord : sourcePhrase){
            for (String targetWor : targetPhrase){

                Alignement alignement = new Alignement();
                alignement.setBiPhrase(bp)
                          .setProbability(1./targetPhrase.size())
                          .setSourceAlignement(i)
                          .setTargetAlignement(j);
                bc.getAlignements().add(alignement);

                j++;
            }

            i++;
        }
    }

    static void predictAlignment(BiCorpus biCorpus){
        Random random = new Random();
        BiPhrase bp = biCorpus.getCorpus().get(random.nextInt(biCorpus.getCorpus().size()-1));
        List<String> sourcePhrase = bp.getSource();
        List<String> targetPhrase = bp.getTarget();
        List<String> resultPhrase = new ArrayList<>();
        for (int i = 0; i < sourcePhrase.size(); i++){
            List<Map.Entry<String, Double>> probableAlignedWords = resultMI(biCorpus, sourcePhrase.get(i));
            HashMap<String, Double> bestAlignedWord = getWordScoreFromAlignedWords(biCorpus, targetPhrase, sourcePhrase.get(i));

            String resultWord = bestAlignedWord.keySet().iterator().next();
            System.out.format("Le mot \"%s\" est aligné avec le mot \"%s\" pour un score -> %f\n",
                    sourcePhrase.get(i),
                    resultWord,
                    bestAlignedWord.get(resultWord)
            );
            resultPhrase.add(resultWord);
        }
        System.out.println("Phrase source : " + String.join(" ", sourcePhrase));
        System.out.println("Phrase target : " + String.join(" ", targetPhrase));
        System.out.println("Phrase estimee: " + String.join(" ", resultPhrase));

    }

    private static HashMap<String,Double> getWordScoreFromAlignedWords(BiCorpus biCorpus, List<String> phrase, String word){
        List<Map.Entry<String, Double>> probableAlignedWords = resultMI(biCorpus, word);
        Double bestScore = 0.;
        String bestScoreWord = "";
        for(String targetWord : phrase){
            for(Map.Entry<String, Double> wordScore : probableAlignedWords){

                if(targetWord.equals(wordScore.getKey())){
                    if (wordScore.getValue() > bestScore){
                        bestScore = wordScore.getValue();
                        bestScoreWord = targetWord;

                    }
                }
            }
        }

        HashMap<String,Double> result = new HashMap<>();
        result.put(bestScoreWord, bestScore);
        return result;
    }

    private static void addAlignementToAlignements(HashMap<String, WordScore> alignements, String[] source, String[] target){
        for (int i = 0; i < source.length; i++){
            for(int j = 0; j< target.length; j++){
                WordScore wordScoreWordI = new WordScore().setWordScore(new HashMap<String, Double>());
                Double score = 0d;
                if (alignements.containsKey(source[i])){
                    wordScoreWordI = alignements.get(source[i]);
                }
                if (wordScoreWordI.getWordScore().containsKey(target[j])){
                    score = wordScoreWordI.getWordScore().get(target[j]) ;
                }

                /*if (j == i )
                    wordScoreWordI.getWordScore().put(target[j], score * Process.alpha);
                else if ( Math.abs(i-j) <= 3)
                    wordScoreWordI.getWordScore().put(target[j], score * Process.beta);
                else if ( Math.abs(i-j) <= 5)
                    wordScoreWordI.getWordScore().put(target[j], score * Process.gamma);
                else
                    wordScoreWordI.getWordScore().put(target[j], score * Process.sigma);*/
                wordScoreWordI.getWordScore().put(target[j], score + 1./target.length);

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
        Double score = biCorpus.getScoresByWord().get(sourceWord).getWordScore().get(targetWord);
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

    private static Double evaluateBiPhrase(BiCorpus biCorpus, BiPhrase biPhrase){
        List<String> sourcePhrase = biPhrase.getSource();
        List<String> targetPhrase = biPhrase.getTarget();
        List<String> translatedPhrase = new ArrayList<>();
        int successfulTranslation = 0;

        for (String word : sourcePhrase){
            List<Map.Entry<String, Double>> resultTranslation = resultMI(biCorpus, word);
            if (!resultTranslation.isEmpty()){
                translatedPhrase.add(resultTranslation.get(0).getKey());
            }
        }

        for (int i= 0; i < Math.min(targetPhrase.size(), translatedPhrase.size()); i++){
            if(targetPhrase.get(i).toLowerCase().equals(translatedPhrase.get(i).toLowerCase()))
                successfulTranslation++;

        }

        return ((double)successfulTranslation)/((double)targetPhrase.size());
    }

    public static Double evaluate(BiCorpus biCorpus){
        Double result = 0d;
        logger.info("Starting Evaluation Bicorpus " + biCorpus.getDisplayName());

        for ( int i = 0; i < biCorpus.getCorpus().size(); i++){
            logger.info("Bicorpus " + biCorpus.getDisplayName()+ " -> Progression evaluation " + (i*100)/biCorpus.getCorpus().size() + "%");
            if(i == 0) {
                result = evaluateBiPhrase(biCorpus, biCorpus.getCorpus().get(0));
            }
            else{
                result = (result + evaluateBiPhrase(biCorpus, biCorpus.getCorpus().get(0)))/2;
            }
        }
        logger.info("End evaluation Bicorpus " + biCorpus.getDisplayName());
        return result;
    }



    public static List<Map.Entry<String, Double>> resultMI(BiCorpus biCorpus, String sourceWord){
        HashMap<String, Double> result=new HashMap<String, Double>();
        for (String coocurentWord :biCorpus.getCoocurencesTable().get(sourceWord).getCooc().keySet()){
            result.put(coocurentWord, calculMI(biCorpus, sourceWord, coocurentWord));

        }

        return MapUtil.sortByValue(result);
    }
}
