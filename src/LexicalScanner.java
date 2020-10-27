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
                ArrayList<String> words = Arrays.stream(data.split(" : "))
                        .map(String::trim)
                        .collect(Collectors.toCollection(ArrayList::new));
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

    private void writeSTToFile(boolean errorFound, int line) throws IOException {
        FileWriter fileWriter = new FileWriter("ST.out", true);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        tokensFromST.forEach(token -> {
            printWriter.println(symbolTable.findElement(token) + " : " + token);

        });

        if (errorFound)
            printWriter.println("Lexical error found at line " + line + ".");
        else
            printWriter.println("Lexically correct!");
        printWriter.close();
    }

    private void addTokenToTables(String token, boolean isConstantString) throws IOException {
        if (codes.containsKey(token)) {
            addToPIF(codes.get(token), -1);
        } else {
            tokensFromST.add(token);
            symbolTable.add(token);
            if (isConstantString)
                addToPIF(codes.get("constant"), symbolTable.findElement(token));
            else
                addToPIF(codes.get("identifier"), symbolTable.findElement(token));
        }
    }

    public void scan(String fileName) throws IOException {
        int lineNumber = 1;
        boolean errorFound = false;

        File myObj = new File(fileName);
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine() && !errorFound) {
            String data = myReader.nextLine();
            String word = "", number = "", symbol = "", constantString = "\"";
            boolean quotesFound = false, idOrConstFound = false;
            for (int i = 0; i < data.length(); i++) {
                char current = data.charAt(i);
                if (Character.toString(current).equals("\"")) {
                    quotesFound = !quotesFound;
                    if (!quotesFound) {//a constant string was found
                        constantString+="\"";
                        addTokenToTables(constantString, true);
                        constantString = "\"";
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
                        if(Character.toString(current).equals(",")){
                            if(!number.equals(""))
                                number+=",";
                        }else {
                            if (!number.equals("")) {
                                addTokenToTables(number, false);
                                number = "";
                            }
                        }
                        if (!Character.isWhitespace(current)) {
                            if (Character.toString(current).equals("-")) {
                                if (i + 1 < data.length()) {
                                    if (Character.isDigit(data.charAt(i + 1))) {
                                        if (Integer.parseInt(Character.toString(data.charAt(i + 1))) == 0) {
                                            errorFound = true;
                                            System.out.println("Invalid number -0 at line " + lineNumber + ".");
                                        } else {
                                            if(Character.toString(data.charAt(i - 1)).equals("="))
                                                number = "-";
                                            else{
                                                symbol+=current;
                                                System.out.println(symbol);
                                                addTokenToTables(symbol, false);
                                                symbol = "";
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (Character.toString(current).equals("+")) {
                                    if (i + 1 < data.length()) {
                                        if (Character.isDigit(data.charAt(i + 1))) {
                                            if (Integer.parseInt(Character.toString(data.charAt(i + 1))) == 0) {
                                                errorFound = true;
                                                System.out.println("Invalid number +0 at line " + lineNumber + ".");
                                            } else {
                                                if (Character.toString(data.charAt(i - 1)).equals("="))
                                                    number = "+";
                                                else{
                                                    symbol+=current;
                                                    System.out.println(symbol);
                                                    addTokenToTables(symbol, false);
                                                    symbol = "";
                                                }
                                            }
                                        }
                                    }
                                }else {
                                    if(Character.toString(current).equals(".")){
                                        if(i+2 < data.length()){
                                            if(Character.toString(data.charAt(i+1)).equals(".") && Character.toString(data.charAt(i+2)).equals(".")){
                                                symbol="...";
                                                addTokenToTables(symbol, false);
                                                symbol = "";
                                                i+=2;
                                            }else{
                                                System.out.println("Illegal character at line "+lineNumber);
                                                errorFound = true;
                                            }
                                        }else{
                                            System.out.println("Illegal character at line "+lineNumber);
                                            errorFound = true;
                                        }
                                    }else {
                                        if (!codes.containsKey(Character.toString(current))) {
                                            errorFound = true;
                                            System.out.println("Illegal character " + current + " at line " + lineNumber + ".");
                                        } else {
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
        if (!errorFound)
            System.out.println("Lexically correct!");
        writeSTToFile(errorFound, lineNumber - 1);

    }
}

