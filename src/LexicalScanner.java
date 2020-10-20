import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class LexicalScanner {
    private Map<String, Integer> codes;
    private Map<Integer, Integer> PIF;
    private SymbolTable symbolTable;
    private Set<String> tokensFromST;

    public LexicalScanner() {
        codes = new HashMap<>();
        PIF = new HashMap<>();
        tokensFromST = new HashSet<>();
        symbolTable = new SymbolTable();
        initializeCodes();
    }

    private void initializeCodes() {
        try {
            File file = new File("token.in");
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                ArrayList<String> words = new ArrayList<>(Arrays.asList(data.split(" : ")));
                words = (ArrayList<String>) words.stream().map(String::trim).collect(Collectors.toList());
                codes.put(words.get(0), Integer.parseInt(words.get(1)));
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void addToPIF(int code, int position) throws IOException {
        PIF.put(code, position);
        FileWriter fileWriter = new FileWriter("PIF.out", true);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println(code + " : " + position);
        printWriter.close();
    }

    private void writeSTToFile() throws IOException {
        tokensFromST.forEach(token -> {
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter("ST.out", true);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                printWriter.println(symbolTable.findElement(token) + " : " + token );
                printWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void addTokenToTables(String token, boolean isConstantString) throws IOException {
        if (codes.containsKey(token)) {
            addToPIF(codes.get(token), -1);
        } else {
            tokensFromST.add(token);
            symbolTable.add(token);
            if(isConstantString)
                addToPIF(codes.get("constant"), symbolTable.findElement(token));
            else
                addToPIF(codes.get("identifier"), symbolTable.findElement(token));
        }
    }

    public void scan(String fileName) throws IOException {
        int lineNumber = 1;
        boolean errorFound = false;
        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine() && !errorFound) {
                String data = myReader.nextLine();
                String word = "", number = "", symbol = "", constantString = "";
                boolean quotesFound = false, idOrConstFound = false;
                for (int i = 0; i < data.length(); i++) {
                    char current = data.charAt(i);
                    if (Character.toString(current).equals("\"")) {
                        quotesFound = !quotesFound;
                        if (!quotesFound) {//a constant string was found
                            addTokenToTables(constantString, true);
                            constantString = "";
                        }
                    }

                    if (quotesFound && !Character.toString(current).equals("\""))//keep adding to the constant string
                        constantString += current;
                    else {
                        if (Character.isAlphabetic(current)) {
                            if (!number.equals("")) {
                                System.out.println("Invalid identifier or constant at line " + lineNumber + ".");
                                i = data.length() + 2;
                                errorFound = true;
                            } else {
                                idOrConstFound = true;
                                word += current;
                            }
                        } else if (Character.isDigit(current)) {
                            if (idOrConstFound)
                                word += current;
                            else
                                number += current;
                        } else {
                            idOrConstFound = false;
                            if (!word.equals("")) {
                                addTokenToTables(word, false);
                                word = "";
                            }
                            if (!number.equals("")) {
                                addTokenToTables(number, false);
                                number = "";
                            }
                            if (!Character.isWhitespace(current)) {
                                symbol += current;
                                if (i + 1 < data.length()) {
                                    if (Character.toString(data.charAt(i + 1)).equals("=")) {
                                        symbol += data.charAt(i + 1);
                                        i++;
                                    }
                                }
                                addTokenToTables(symbol, false);
                                symbol = "";
                            }


                        }
                    }
                }
                if (quotesFound) {
                    System.out.println("Invalid constant string at line " + lineNumber + ".");
                    errorFound = true;
                }
                lineNumber++;
            }
            myReader.close();
            writeSTToFile();
            if(!errorFound)
                System.out.println("Lexically correct!");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}

