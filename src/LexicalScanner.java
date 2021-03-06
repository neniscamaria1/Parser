import java.io.*;
import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;

public class LexicalScanner {
    private Map<String, Integer> codes;
    private Map<Integer, Integer> PIF;
    private SymbolTable symbolTable;
    private Set<String> tokensFromST;
    private FA fa_id;
    private FA fa_const_number;
    private FA fa_const_string;

    public LexicalScanner(FA fa_id,FA fa_const_number, FA fa_const_string) {
        codes = new HashMap<>();
        PIF = new HashMap<>();
        tokensFromST = new HashSet<>();
        symbolTable = new SymbolTable();
        this.fa_id = fa_id;
        this.fa_const_number = fa_const_number;
        this.fa_const_string = fa_const_string;
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

    private boolean addTokenToTables(String token, int lineNo) throws IOException {
        if (codes.containsKey(token)) {
            addToPIF(codes.get(token), -1);
        } else {
            tokensFromST.add(token);
            symbolTable.add(token);
            if(fa_id.isSequenceAccepted(token)) { //is id
                addToPIF(codes.get("identifier"), symbolTable.findElement(token));
            }else
                if(fa_const_number.isSequenceAccepted(token)) { //is constant number
                    addToPIF(codes.get("constant"), symbolTable.findElement(token));
                }else
                    if (fa_const_string.isSequenceAccepted(token)) { //is constant string
                        addToPIF(codes.get("constant"), symbolTable.findElement(token));
                    }
                    else{
                        System.out.println("Error at line "+lineNo+". Invalid token "+token+".");
                        return false;
                    }

        }
        return true;
    }

    public void scan(String fileName) throws IOException {
        int lineNumber = 1;
        boolean errorFound = false;

        File myObj = new File(fileName);
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine() && !errorFound) {
            String data = myReader.nextLine();
            String word = "", number = "", symbol = "";
            String constantString = "\"";
            boolean quotesFound = false, idOrConstFound = false;
            for (int i = 0; i < data.length(); i++) {
                char current = data.charAt(i);
                if (Character.toString(current).equals("\"")) {
                    quotesFound = !quotesFound;
                    if (!quotesFound) {//a constant string was found
                        constantString+="\"";
                        if(!errorFound)
                            errorFound = !addTokenToTables(constantString, lineNumber);
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
                            if(!errorFound)
                                errorFound = !addTokenToTables(word, lineNumber);
                            word = "";
                        }
                        if(Character.toString(current).equals(",")){
                            if(!number.equals(""))
                                number+=",";
                        }else {
                            if (!number.equals("")) {
                                if(!errorFound)
                                    errorFound = !addTokenToTables(number, lineNumber);
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
                                                if(!errorFound)
                                                    errorFound = !addTokenToTables(symbol, lineNumber);
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
                                                    if(!errorFound)
                                                        errorFound = !addTokenToTables(symbol, lineNumber);
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
                                                if(!errorFound)
                                                    errorFound = !addTokenToTables(symbol, lineNumber);
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
                                            if(!errorFound)
                                                errorFound = !addTokenToTables(symbol, lineNumber);
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

