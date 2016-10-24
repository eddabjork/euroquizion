package ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eddabjorkkonradsdottir.euroquizion.R;

import entities.Answer;
import entities.Question;
import logic.GameHandler;
import logic.ResultsHandler;

/**
 * @author eddabjorkkonradsdottir on 19/10/16.
 */
public class ResultScreen extends BaseFragment implements View.OnClickListener {
    private TextView playAgainButton;
    private TextView title;
    private LinearLayout resultsView;

    private GameHandler gameHandler = getGameHandler();
    private ResultsHandler resultsHandler = getResultsHandler();
    private MediaPlayer mediaPlayer;

    public ResultScreen() {
        super(R.layout.result_screen);
    }

    @Override
    protected void setUpView(View view) {
        playAgainButton = (TextView) view.findViewById(R.id.play_again);
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameHandler.restart();
            }
        });

        title = (TextView) view.findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getResources().getAssets(), "fonts/Futura.ttc");
        title.setText(getResources().getString(R.string.results_screen_title,
                Integer.toString(gameHandler.getTotalPoints())+getPointString()));
        title.setTypeface(font);

        resultsView = (LinearLayout) view.findViewById(R.id.results);
        LinearLayout leftResultContainer = (LinearLayout) resultsView.getChildAt(0);
        LinearLayout rightResultContainer = (LinearLayout) resultsView.getChildAt(1);

        // using child count of leftResultContainer but can use rightResultContainer as well,
        // doesn't matter, they're the same
        for (int i = 0; i < leftResultContainer.getChildCount(); i++) {
            TextView leftQuestionNr = (TextView) leftResultContainer.getChildAt(i).findViewById(R.id.question_nr);
            ImageView leftResult = (ImageView) leftResultContainer.getChildAt(i).findViewById(R.id.result);

            TextView rightQuestionNr = (TextView) rightResultContainer.getChildAt(i).findViewById(R.id.question_nr);
            ImageView rightResult = (ImageView) rightResultContainer.getChildAt(i).findViewById(R.id.result);

            // questions 1-5
            leftQuestionNr.setText(Integer.toString(i + 1));
            setAnswerResult(leftResult, i + 1);
            leftResultContainer.getChildAt(i).setTag(i + 1);
            // questions 6-10
            rightQuestionNr.setText(Integer.toString(i + 6));
            setAnswerResult(rightResult, i + 6);
            rightResultContainer.getChildAt(i).setTag(i + 6);

            leftResultContainer.getChildAt(i).setOnClickListener(this);
            rightResultContainer.getChildAt(i).setOnClickListener(this);
        }

        playEurovisionThemeSong();
    }

    @Override
    public void onPause() {
        super.onPause();

        mediaPlayer.pause();
    }

    @Override
    public void onResume() {
        super.onResume();

        mediaPlayer.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mediaPlayer.stop();
    }

    @Override
    public String getFragmentTag() {
        return getClass().getSimpleName();
    }

    @Override
    public void onClick(View view) {
        int questionNr = (Integer) view.getTag();
        Question question = gameHandler.getQuestion(questionNr);
        Answer correctAnswer = question.answers.get(question.correctAnswerIndex);
        boolean ranOutOfTime = question.answerIndex == -1;

        if (wasQuestionAnsweredCorrectly(questionNr)) {
            showResultDialog(view.getContext(),
                    getResources().getString(R.string.result_dialog_correct_title, view.getTag()),
                    getResources().getString(R.string.result_dialog_correct_message, question.text,
                            correctAnswer.text),
                    getResources().getString(R.string.result_dialog_correct_button));
        } else if (ranOutOfTime) {
            showResultDialog(view.getContext(),
                    getResources().getString(R.string.result_dialog_out_of_time_title, view.getTag()),
                    getResources().getString(R.string.result_dialog_out_of_time_message, question.text,
                            correctAnswer.text),
                    getResources().getString(R.string.result_dialog_out_of_time_button));
        } else {
            Answer answer = question.answers.get(question.answerIndex);
            showResultDialog(view.getContext(),
                    getResources().getString(R.string.result_dialog_wrong_title, view.getTag()),
                    getResources().getString(R.string.result_dialog_wrong_message, question.text, answer.text,
                            correctAnswer.text),
                    getResources().getString(R.string.result_dialog_wrong_button));
        }
    }

    private boolean wasQuestionAnsweredCorrectly(int questionNr) {
        return resultsHandler.wasQuestionAnsweredCorrectly(questionNr);
    }

    private void setAnswerResult(ImageView resultContainer, int questionNr) {
        if (wasQuestionAnsweredCorrectly(questionNr)) {
            resultContainer.setImageDrawable(getResources().getDrawable(R.drawable.correct_check_mark));
        }
        else {
            resultContainer.setImageDrawable(getResources().getDrawable(R.drawable.wrong_x_mark));
        }
    }

    private String getPointString() {
        String pointString;
        if (gameHandler.getTotalPoints() == 1) {
            pointString = getResources().getString(R.string.one_point);
        } else {
            pointString = getResources().getString(R.string.zero_or_several_points);
        }

        return pointString;
    }

    private void playEurovisionThemeSong() {
        mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.eurovision_theme_song);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.start();
    }

    private void showResultDialog(Context context, String title, String message, String buttonMessage) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(buttonMessage, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                .show();
    }
}
