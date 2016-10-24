package logic;

import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entities.Question;
import entities.Questions;
import ui.QuestionScreen;
import ui.ResultScreen;
import ui.StartScreen;

/**
 * @author eddabjorkkonradsdottir on 17/10/16.
 */
public class GameHandler {
    private static String LOGTAG = GameHandler.class.getSimpleName();
    private MyFragmentManager fragmentManager;
    private AssetManager assetManager;
    private int questionNr;

    private List<Question> unusedQuestions;
    private List<Question> allQuestions;

    private int accumulatedPoints;
    private int questionsAnsweredCorrectly;

    public GameHandler(MyFragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        assetManager = InstanceHandler.getAssetManager();
        unusedQuestions = new ArrayList<Question>();
        allQuestions = new ArrayList<Question>();
        initPoints();
        initQuestions();
    }

    public void startGame() {
        fragmentManager.displayFragment(QuestionScreen.class);
    }

    public void showResults() {
        fragmentManager.displayFragment(ResultScreen.class);
    }

    public void quit() {
        initQuestions();
        fragmentManager.displayFragment(StartScreen.class);
    }

    public void restart() {
        initQuestions();
        fragmentManager.displayFragment(QuestionScreen.class);
    }

    public Question getRandomQuestion() {
        Random random = new Random();
        int numberOfQuestions = unusedQuestions.size();
        int questionIndex = random.nextInt(numberOfQuestions);

        Question question = unusedQuestions.get(questionIndex);
        question.questionNumber = questionNr;
        questionNr++;

        unusedQuestions.remove(question);

        return question;
    }

    public Question getQuestion(int questionNr) {
        for (Question question : allQuestions) {
            if (question.questionNumber == questionNr) {
                return question;
            }
        }

        return null;
    }

    private void initQuestions() {
        unusedQuestions = new ArrayList<Question>();
        allQuestions = new ArrayList<Question>();
        Questions questions = new Questions();

        try {
            Gson gson = new Gson();
            Reader reader = new InputStreamReader(assetManager.open("data/questions.json"));
            questions = gson.fromJson(reader,
                    new TypeToken<Questions>(){}.getType());
        } catch (FileNotFoundException e) {
            Log.d(LOGTAG, e.getMessage());
        } catch (IOException e) {
            Log.d(LOGTAG, e.getMessage());
        }

        unusedQuestions.addAll(questions.questionList);
        allQuestions.addAll(questions.questionList);
        questionNr = 1;
    }

    public void updatePoints(boolean answeredCorrectly) {
        if (answeredCorrectly) {
            accumulatedPoints++;
            questionsAnsweredCorrectly++;
        }

        if (questionsAnsweredCorrectly == 10) {
            accumulatedPoints += 2;
        }
    }

    public void initPoints() {
        accumulatedPoints = 0;
        questionsAnsweredCorrectly = 0;
    }

    public int getTotalPoints() {
        return accumulatedPoints;
    }
}
