package busdriver.com.vidriver.controller;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import busdriver.com.vidriver.R;
import busdriver.com.vidriver.dialog.ProgressDialogFragment;


/**
 * Class used to show and hide the progress spinner.
 */
public class ProgressDialogController {

    private FragmentManager mFragmentManager;
    private ProgressDialogFragment mProgressFragment;

    public ProgressDialogController(@NonNull FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
        mProgressFragment = ProgressDialogFragment.newInstance(R.string.progressMessage);
    }

    void startProgress() {
        mProgressFragment.show(mFragmentManager, "progress");
    }

    void finishProgress() {
        mProgressFragment.dismiss();
    }
}
