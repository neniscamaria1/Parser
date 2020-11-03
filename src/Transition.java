public class Transition {
    private String initialState;
    private String nonterminal;
    private String finalState;

    public Transition(String initialState, String nonterminal, String finalState) {
        this.initialState = initialState;
        this.nonterminal = nonterminal;
        this.finalState = finalState;
    }

    public String getInitialState() {
        return initialState;
    }

    public String getNonterminal() {
        return nonterminal;
    }

    public String getFinalState() {
        return finalState;
    }

    @Override
    public String toString() {
        return "Transition{" +
                "initialState='" + initialState + '\'' +
                ", nonterminal='" + nonterminal + '\'' +
                ", finalState='" + finalState + '\'' +
                '}';
    }
}
