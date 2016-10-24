package ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import logic.GameHandler;
import logic.InstanceHandler;
import logic.ResultsHandler;

/**
 * @author eddabjorkkonradsdottir on 17/10/16.
 */
public abstract class BaseFragment extends Fragment {
    private int layoutId;

    public BaseFragment(int layoutId) {
        this.layoutId = layoutId;
    }

    protected abstract void setUpView(View view);

    public abstract String getFragmentTag();

    protected GameHandler getGameHandler() {
        return InstanceHandler.getGameHandler();
    }

    protected ResultsHandler getResultsHandler() {
        return InstanceHandler.getResultsHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layoutId, container, false);
        setUpView(view);
        return view;
    }
}
