package logic;

import java.util.HashMap;

import entities.Question;

/**
 * @author eddabjorkkonradsdottir on 21/10/16.
 */
public class ResultsHandler {
    HashMap<Integer, Boolean> resultMap;

    public ResultsHandler() {
        resultMap = new HashMap<Integer, Boolean>();
    }

    public void setResultForQuestion(int questionNr, boolean wasAnsweredCorrectly) {
        resultMap.put(questionNr, wasAnsweredCorrectly);
    }

    public boolean wasQuestionAnsweredCorrectly(int questionNr) {
        return resultMap != null ? resultMap.get(questionNr) : false;
    }

    public void setAnswerIndex(Question question, int answerIndex) {
        question.answerIndex = answerIndex;
    }
}
