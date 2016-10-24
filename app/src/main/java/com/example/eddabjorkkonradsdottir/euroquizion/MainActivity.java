package com.example.eddabjorkkonradsdottir.euroquizion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;

import logic.GameHandler;
import logic.InstanceHandler;
import ui.QuestionScreen;
import ui.ResultScreen;
import ui.StartScreen;

public class MainActivity extends Activity {
    private GameHandler gameHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new InstanceHandler(this, getAssets());
        InstanceHandler.getFragmentManager().displayFragment(StartScreen.class);
        gameHandler = InstanceHandler.getGameHandler();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            final QuestionScreen questionScreen = (QuestionScreen) getFragmentManager()
                    .findFragmentByTag(QuestionScreen.class.getSimpleName());
            ResultScreen resultScreen = (ResultScreen) getFragmentManager()
                    .findFragmentByTag(ResultScreen.class.getSimpleName());

            if (questionScreen != null && questionScreen.isVisible()) {
                showQuitGameDialog(questionScreen);
            } else if (resultScreen != null && resultScreen.isVisible()) {
                showGoHomeDialog();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void showQuitGameDialog(final QuestionScreen questionScreen) {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.quit_game_dialog_title))
                .setMessage(getResources().getString(R.string.quit_game_dialog_message))
                .setPositiveButton(getResources().getString(R.string.quit_game_dialog_pos_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                questionScreen.setQuitGame(true);
                                gameHandler.quit();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.quit_game_dialog_neg_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                .show();
    }

    private void showGoHomeDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.quit_result_dialog_title))
                .setMessage(getResources().getString(R.string.quit_result_dialog_message))
                .setPositiveButton(getResources().getString(R.string.quit_result_dialog_pos_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                gameHandler.quit();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.quit_result_dialog_neg_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                .show();
    }
}
