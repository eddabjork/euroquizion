package logic;

import android.app.Activity;
import android.content.res.AssetManager;

/**
 * @author eddabjorkkonradsdottir on 18/10/16.
 */
public class InstanceHandler {
    private static GameHandler gameHandler;
    private static MyFragmentManager fragmentManager;
    private static AssetManager assetManager;
    private static ResultsHandler resultsHandler;

    public InstanceHandler(Activity activity, AssetManager assetManager) {
        InstanceHandler.assetManager = assetManager;
        fragmentManager = new MyFragmentManager(activity);
        gameHandler = new GameHandler(fragmentManager);
        resultsHandler = new ResultsHandler();
    }
    public static GameHandler getGameHandler() {
        return gameHandler;
    }

    public static MyFragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public static AssetManager getAssetManager() {
        return assetManager;
    }

    public static ResultsHandler getResultsHandler() {
        return resultsHandler;
    }
}
