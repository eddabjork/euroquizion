package logic;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;

import com.example.eddabjorkkonradsdottir.euroquizion.R;

import ui.BaseFragment;

/**
 * @author eddabjorkkonradsdottir on 17/10/16.
 */
public class MyFragmentManager {
    private static String LOGTAG = MyFragmentManager.class.getSimpleName();

    private FragmentManager fragmentManager;

    public MyFragmentManager(Activity activity)
    {
        fragmentManager = activity.getFragmentManager();
    }

    public void displayFragment(Class<? extends BaseFragment> fragmentClass)
    {
        try {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            BaseFragment fragment = fragmentClass.newInstance();
            fragmentTransaction.replace(R.id.screen, fragment, fragment.getFragmentTag());
            fragmentTransaction.commit();
        } catch (InstantiationException e) {
            Log.d(LOGTAG, e.getMessage());
        } catch (IllegalAccessException e) {
            Log.d(LOGTAG, e.getMessage());
        }

    }
}
