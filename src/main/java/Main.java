import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
        String prefixPath = "";

        BiCorpus bcEN = Process.init(prefixPath + "corpus/BAF-full.en", prefixPath + "corpus/BAF-full.fr", "English To French");
        BiCorpus bcFR = Process.init(prefixPath + "corpus/BAF-full.fr", prefixPath + "corpus/BAF-full.en", "Français vers Anglais");

        Scanner sc=new Scanner(System.in);

        String stopLoop = null;
        while(stopLoop == null){
            System.out.println("anglais -->francais : 1");
            System.out.println("français -->anglais : 2");

            int choix=Integer.parseInt(sc.nextLine());
            System.out.println("Votre mot?");
            String mot=sc.nextLine();
            switch (choix) {
                case 0:
                    stopLoop = "STOP";
                    break;
                case 1:
                    System.out.println(Process.resultMI(bcEN, mot));
                    break;

                case 2:
                    System.out.println(Process.resultMI(bcFR, mot));
                    break;

                default:
                    System.out.println("Faites votre choix");
                    break;
            }

        }
    }
}
