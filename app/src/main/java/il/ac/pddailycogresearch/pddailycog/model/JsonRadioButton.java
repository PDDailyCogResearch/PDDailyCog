package il.ac.pddailycogresearch.pddailycog.model;

import android.text.style.QuoteSpan;

/**
 * Created by ggrot on 07/02/2018.
 */

public class JsonRadioButton {
    private String question;
    private String instr;
    private String answer;

    public JsonRadioButton(String question, String instr, String answer) {
        this.question = question;
        this.instr = instr;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getInstr() {
        return instr;
    }

    public void setInstr(String instr) {
        this.instr = instr;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


}
