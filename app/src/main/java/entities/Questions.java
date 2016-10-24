package entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author eddabjorkkonradsdottir on 20/10/16.
 */
public class Questions {
    @SerializedName("questions")
    public List<Question> questionList;
}
