package entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author eddabjorkkonradsdottir on 18/10/16.
 */
public class Question {
    public int questionNumber;
    @SerializedName("question")
    public String text;
    public List<Answer> answers;
    @SerializedName("correct_answer_index")
    public int correctAnswerIndex;
    public int answerIndex;
}
