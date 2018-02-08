package il.ac.pddailycogresearch.pddailycog.model;

import java.util.ArrayList;

/**
 * Created by ggrot on 07/02/2018.
 */

public class JsonRadioButton {
    private String question;
    private String instruction;
    private ArrayList<String> answer;

    public JsonRadioButton(String question, String instr, ArrayList<String> answer) {
        this.question = question;
        this.instruction = instr;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public ArrayList<String> getAnswer() {
        return answer;
    }

    public void setAnswer(ArrayList<String> answer) {
        this.answer = answer;
    }


}
