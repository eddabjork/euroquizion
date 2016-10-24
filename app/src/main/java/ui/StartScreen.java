package ui;

import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.TextView;

import com.example.eddabjorkkonradsdottir.euroquizion.R;

import logic.GameHandler;

/**
 * @author eddabjorkkonradsdottir on 17/10/16.
 */
public class StartScreen extends BaseFragment {
    private TextView startButton;
    private GameHandler gameHandler = getGameHandler();
    private MediaPlayer mediaPlayer;

    public StartScreen() {
        super(R.layout.start_screen);
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
    protected void setUpView(View view) {
        startButton = (TextView) view.findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameHandler.startGame();
            }
        });

        Typeface font = Typeface.createFromAsset(getResources().getAssets(),"fonts/Futura.ttc");
        startButton.setText(getResources().getString(R.string.start_game));
        startButton.setTypeface(font);

        playEurovisionThemeSong();
    }

    @Override
    public String getFragmentTag() {
        return getClass().getSimpleName();
    }

    private void playEurovisionThemeSong() {
        mediaPlayer = MediaPlayer.create(getActivity().getBaseContext(), R.raw.eurovision_theme_song);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }
}
