import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

class Main {
    private static void resetFiles() throws IOException {
        //reset the files
        //negative constants
        FileWriter pifFile = new FileWriter("PIF.out");
        pifFile.write("Code : Position\n");
        FileWriter stFile = new FileWriter("ST.out");
        pifFile.write("");
        stFile.write("Data structure used: hash table with separate chaining.\n");
        pifFile.close();
        stFile.close();
    }

    private static void showMenu(){
        System.out.println("Choose one of the following for the FA for identifiers:");
        System.out.println("1.Show set of states");
        System.out.println("2.Show alphabet");
        System.out.println("3.Show initial state");
        System.out.println("4.Show final state");
        System.out.println("5.Show transitions");
        System.out.println("6.Check sequence");
        System.out.println("0.Exit");
    }

    public static void main(String[] args){

        FA fa_id = new FA("FA_id.in");
        FA fa_const_number = new FA("Fa_const_number.in");
        FA fa_const_string = new FA("FA_const_string.in");
        try {
            LexicalScanner lexicalScanner = new LexicalScanner(fa_id, fa_const_number, fa_const_string);
            resetFiles();
            lexicalScanner.scan("p1.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean finished = false;
        Scanner console = new Scanner(System.in);
        while(!finished){
            showMenu();
            System.out.println(">>");
            int choice = console.nextInt();
            switch (choice){
                case 0:
                    finished=true;
                    break;
                case 1:
                    System.out.println(fa_const_string.getSetOfStates());
                    break;
                case 2:
                    System.out.println(fa_const_string.getAlphabet());
                    break;
                case 3:
                    System.out.println(fa_const_string.getInitialState());
                    break;
                case 4:
                    System.out.println(fa_const_string.getFinalStates());
                    break;
                case 5:
                    fa_id.getTransitions().forEach(System.out::println);
                    break;
                case 6:
                    System.out.println("Sequence: ");
                    console.nextLine();
                    String sequence = console.nextLine();
                    System.out.println(fa_const_string.isSequenceAccepted(sequence));
                    break;
                default:
                    System.out.println("Wrong command");
            }
        }
    }
}