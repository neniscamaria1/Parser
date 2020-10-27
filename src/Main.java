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
        try {
            LexicalScanner lexicalScanner = new LexicalScanner();
            resetFiles();
            lexicalScanner.scan("p1err.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}