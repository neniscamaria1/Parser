import java.io.FileWriter;
import java.io.IOException;

class Main {
    public static void main(String[] args) throws IOException {
        LexicalScanner lexicalScanner = new LexicalScanner();
        //reset the files
        FileWriter pifFile = new FileWriter("PIF.out");
        pifFile.write("Code : Position\n");
        FileWriter stFile = new FileWriter("ST.out");
        pifFile.write("");
        stFile.write("Data structure used: hash table with separate chaining.\n");
        pifFile.close();
        stFile.close();
        lexicalScanner.scan("p2.txt");
    }
}