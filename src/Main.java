import java.io.FileWriter;
import java.io.IOException;

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

    public static void main(String[] args){

        FA fa_id = new FA("FA_id.in");
        FA fa_const_number = new FA("Fa_const_number.in");
        FA fa_const_string = new FA("FA_const_string.in");
        try {
            LexicalScanner lexicalScanner = new LexicalScanner(fa_id, fa_const_number, fa_const_string);
            resetFiles();
            lexicalScanner.scan("p1err.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}