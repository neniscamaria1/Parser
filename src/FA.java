import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class FA {
    private String filename;
    private Set<String> setOfStates;
    private Set<String> alphabet;
    private String initialState;
    private Set<String> finalStates;
    private Set<Transition> transitions;

    public FA(String filename) {
        this.filename = filename;
        setOfStates = new HashSet<>();
        alphabet = new HashSet<>();
        finalStates = new HashSet<>();
        transitions = new HashSet<>();
        initialize();
    }

    private void initialize() {
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            int lineNo = 0;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] dataSplit = data.split(" ");
                if (lineNo == 0) {
                    Collections.addAll(setOfStates, dataSplit);
                }
                if (lineNo == 1) {
                    Collections.addAll(alphabet, dataSplit);
                }
                if (lineNo == 2) {
                    initialState = dataSplit[0];
                }
                if (lineNo == 3) {
                    Collections.addAll(finalStates, dataSplit);
                }
                if (lineNo > 3) {
                    Transition transition = new Transition(dataSplit[0], dataSplit[1], dataSplit[2]);
                    transitions.add(transition);
                }
                lineNo++;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public Set<String> getSetOfStates() {
        return setOfStates;
    }

    public Set<String> getAlphabet() {
        return alphabet;
    }

    public String getInitialState() {
        return initialState;
    }

    public Set<String> getFinalStates() {
        return finalStates;
    }

    public Set<Transition> getTransitions() {
        return transitions;
    }

    private Set<Transition> transitionsFromState(String state){
        Set<Transition> transitions = new HashSet<>();
        this.transitions.forEach(transition -> {
            if(transition.getInitialState().equals(state))
                transitions.add(transition);
        });
        return transitions;
    }

    private Transition findNextTransition(Set<Transition> trans, String nonterminal){
        AtomicReference<Transition> transition = new AtomicReference<>();
        trans.forEach(t -> {
            if(t.getNonterminal().equals(nonterminal))
                transition.set(t);
        });
        return transition.get();
    }
    public boolean isSequenceAccepted(String sequence){
        if(isDFA()){
            //start from initial states all possibilities
            boolean deadEnd = false;
            boolean reachedFinalState = false;
            Set<Transition> nextTransitions = transitionsFromState(initialState);
            while(!deadEnd && !sequence.equals("") && !reachedFinalState){
                Transition currentTransition =
                        findNextTransition(nextTransitions,
                                Character.toString(sequence.charAt(0)));
                if(currentTransition!=null){
                    //remove first char from word
                    sequence = sequence.substring(1);
                    if(sequence.equals(""))
                        reachedFinalState = finalStates.contains(currentTransition.getFinalState());
                    //get the next state
                    nextTransitions=transitionsFromState(currentTransition.getFinalState());
                }else //no more
                    deadEnd=true;
            }
            return !deadEnd;
        }
        return false;
    }

    private boolean isDFA() {
        AtomicBoolean foundDuplicate = new AtomicBoolean(false);
        transitions.forEach(t1 -> {
            transitions.forEach(t2->{
                if(!t1.getFinalState().equals(t2.getFinalState()) && t1.getInitialState().equals(t2.getInitialState()) && t1.getNonterminal().equals(t2.getNonterminal())) {
                    foundDuplicate.set(true);
                }
            });
        });
        return !foundDuplicate.get();
    }
}
