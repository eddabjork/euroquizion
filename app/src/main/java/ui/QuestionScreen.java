package ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.eddabjorkkonradsdottir.euroquizion.R;

import java.util.ArrayList;
import java.util.List;

import entities.Answer;
import entities.Question;
import logic.GameHandler;
import logic.ResultsHandler;

/**
 * @author eddabjorkkonradsdottir on 17/10/16.
 */
public class QuestionScreen extends BaseFragment implements View.OnClickListener {
    private static int MILLI_IN_SEC = 1000;
    private static int COUNT_DOWN_DURATION = 4 * MILLI_IN_SEC;
    private static int ANIMATION_DURATION = 1 * MILLI_IN_SEC;
    private static int ANIMATION_START_DELAY = 2 * MILLI_IN_SEC;
    private static int PROGRESS_BAR_MAX = 1000;
    private static int PROGRESS_BAR_ANIMATION_DURATION = 10 * MILLI_IN_SEC;

    private ProgressBar progressBar;
    private Question currentQuestion;
    private TextView question;
    private TextView answer1;
    private TextView answer2;
    private TextView answer3;
    private TextView answer4;
    private TextView answer5;
    private RelativeLayout countDown;
    private TextView countDownText;
    private TextView questionProgress;

    private List<TextView> answers;
    private TextView answerClicked;
    private List<TextView> viewsAnimatedToRight = new ArrayList<TextView>();
    private List<TextView> viewsAnimatedToLeft = new ArrayList<TextView>();

    private ObjectAnimator progressBarAnimator;
    private boolean animationsWasCanceled;
    private boolean quitGame;

    private GameHandler gameHandler = getGameHandler();
    private ResultsHandler resultsHandler = getResultsHandler();
    private MediaPlayer mediaPlayer;

    private List<Animation> answerAnimations;
    private ObjectAnimator questionAnimator;

    public QuestionScreen() {
        super(R.layout.question_screen);
    }
    @Override
    protected void setUpView(View view) {
        view.setKeepScreenOn(true);

        gameHandler.initPoints();
        currentQuestion = gameHandler.getRandomQuestion();

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        question = (TextView) view.findViewById(R.id.question);
        answer1 = (TextView) view.findViewById(R.id.answer1);
        answer2 = (TextView) view.findViewById(R.id.answer2);
        answer3 = (TextView) view.findViewById(R.id.answer3);
        answer4 = (TextView) view.findViewById(R.id.answer4);
        answer5 = (TextView) view.findViewById(R.id.answer5);
        countDown = (RelativeLayout) view.findViewById(R.id.count_down);
        countDownText = (TextView) view.findViewById(R.id.count_down_text);
        questionProgress = (TextView) view.findViewById(R.id.question_progress);

        answerAnimations = new ArrayList<Animation>();
        viewsAnimatedToRight.add(answer1);
        viewsAnimatedToRight.add(answer3);
        viewsAnimatedToRight.add(answer5);
        viewsAnimatedToLeft.add(answer2);
        viewsAnimatedToLeft.add(answer4);

        answers = new ArrayList<TextView>();
        answers.add(answer1);
        answers.add(answer2);
        answers.add(answer3);
        answers.add(answer4);
        answers.add(answer5);

        answer1.setOnClickListener(this);
        answer2.setOnClickListener(this);
        answer3.setOnClickListener(this);
        answer4.setOnClickListener(this);
        answer5.setOnClickListener(this);

        countDown.setVisibility(View.VISIBLE);
        updateView();
        startCountDown();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();
    }

    @Override
    public String getFragmentTag() {
        return getClass().getSimpleName();
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

        if (questionAnimator != null) {
            questionAnimator.cancel();
        }
        if (progressBarAnimator != null) {
            progressBarAnimator.cancel();
        }
        for (Animation animation : answerAnimations) {
            animation.cancel();
        }
    }

    private void startTimer() {
        animationsWasCanceled = false;
        progressBarAnimator = ObjectAnimator.ofInt(progressBar, "progress", PROGRESS_BAR_MAX, 0);
        progressBarAnimator.setDuration(PROGRESS_BAR_ANIMATION_DURATION);
        progressBarAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                countDown.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setAnswersClickable(false);

                if (!animationsWasCanceled && !quitGame) {
                    showCorrectAnswer();
                    resultsHandler.setResultForQuestion(currentQuestion.questionNumber, false);
                    resultsHandler.setAnswerIndex(currentQuestion, -1);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        progressBarAnimator.start();
    }

    private void startCountDown() {
        new CountDownTimer(COUNT_DOWN_DURATION, 1) {
            @Override
            public void onTick(long milliSecLeft) {
                int secTilStart = (int) milliSecLeft / MILLI_IN_SEC;
                if (!quitGame) {
                    if (secTilStart == 0) {
                        countDownText.setText(getResources().getString(R.string.count_down_go));

                    } else
                    {
                        countDownText.setText(Integer.toString(secTilStart));
                    }
                }
            }

            @Override
            public void onFinish() {
                mediaPlayer.stop();
                playGameSound();
                startTimer();
            }
        }.start();
        playCountDown();
    }

    private void nextQuestion() {
        TranslateAnimation leftAnimation = getAnimationToLeft();
        TranslateAnimation rightAnimation = getAnimationToRight();
        answerAnimations.add(leftAnimation);
        answerAnimations.add(rightAnimation);

        for (TextView answerView : viewsAnimatedToRight) {
            answerView.startAnimation(rightAnimation);
        }
        for (TextView answerView : viewsAnimatedToLeft) {
            answerView.startAnimation(leftAnimation);
        }

        animateQuestion();
    }

    private void updateView() {
        Typeface font = Typeface.createFromAsset(getResources().getAssets(), "fonts/Futura.ttc");

        // question
        question.setText(currentQuestion.text);
        question.setTypeface(font);

        // answers
        List<Answer> answers = currentQuestion.answers;
        for (int i = 0; i < answers.size(); i++) {
            this.answers.get(i).setText(answers.get(i).text);
            this.answers.get(i).setTypeface(font);
        }

        // text that shows which question you're on, i.e. '3/10'
        questionProgress.setText(getResources()
                .getString(R.string.question_progress, currentQuestion.questionNumber));
        questionProgress.setTypeface(font);
    }

    private void showResults() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gameHandler.showResults();
            }
        }, ANIMATION_START_DELAY);
    }

    @Override
    public void onClick(View view) {
        answerClicked = (TextView) view;
        animationsWasCanceled = true;
        progressBarAnimator.cancel();

        boolean answeredCorrectly = answers.get(currentQuestion.correctAnswerIndex).equals(view);
        if (!answeredCorrectly) {
            view.setBackground(getResources().getDrawable(R.drawable.wrong_answer));
        }
        resultsHandler.setResultForQuestion(currentQuestion.questionNumber, answeredCorrectly);
        resultsHandler.setAnswerIndex(currentQuestion, answers.indexOf(view));
        gameHandler.updatePoints(answeredCorrectly);

        showCorrectAnswer();
    }

    private void showCorrectAnswer() {
        answers.get(currentQuestion.correctAnswerIndex)
                .setBackground(getResources().getDrawable(R.drawable.correct_answer));

        if (currentQuestion.questionNumber == 10) {
            showResults();
        } else {
            nextQuestion();
        }
    }

    private void setAnswersClickable(boolean clickable) {
        for (TextView answer : answers) {
            answer.setClickable(clickable);
        }
    }

    private void initAnswerButton(TextView answer) {
        answer.setBackground(getResources().getDrawable(R.drawable.answer_button));
    }

    private void animateQuestion() {
        questionAnimator = ObjectAnimator.ofFloat(question, "alpha", 1, 0);
        questionAnimator.setDuration(1000);
        questionAnimator.setStartDelay(2000);
        questionAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animator = ObjectAnimator.ofFloat(question, "alpha", 0, 1);
                animator.setDuration(1000);
                animator.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        questionAnimator.start();
    }

    private TranslateAnimation getAnimationToRight() {
        // use the width of some answer, doesn't matter which one, they're all the same
        TranslateAnimation animation = getAnimation(answer1.getWidth());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!quitGame) {
                    initAnswerButton(answers.get(currentQuestion.correctAnswerIndex));
                    if (animationsWasCanceled) {
                        initAnswerButton(answerClicked);
                    }
                    currentQuestion = gameHandler.getRandomQuestion();
                    updateView();

                    // animate back to original position
                    animation = new TranslateAnimation(answer1.getWidth(), 0, 0, 0);
                    animation.setDuration(ANIMATION_DURATION);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            setAnswersClickable(true);
                            progressBar.setProgress(progressBar.getMax());
                            startTimer();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    for (TextView view : viewsAnimatedToRight) {
                        view.startAnimation(animation);
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        return animation;
    }

    private TranslateAnimation getAnimationToLeft() {
        // use the width of some answer, doesn't matter which one, they're all the same
        TranslateAnimation animation = getAnimation(-answer1.getWidth());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation = new TranslateAnimation(-answer1.getWidth(), 0, 0, 0);
                animation.setDuration(ANIMATION_DURATION);
                for (TextView view : viewsAnimatedToLeft) {
                    view.startAnimation(animation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        return animation;
    }

    private TranslateAnimation getAnimation(final float deltaX) {
        TranslateAnimation animation = new TranslateAnimation(0, deltaX, 0, 0);
        animation.setDuration(ANIMATION_DURATION);
        animation.setStartOffset(ANIMATION_START_DELAY);

        return animation;
    }

    public void setQuitGame(boolean quitGame) {
        this.quitGame = quitGame;
    }

    private void playCountDown() {
        mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.count_down);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.start();
    }

    private void playGameSound() {
        if (getActivity() != null) {
            mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.exciting_soundtrack);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }
    }
}