package busdriver.com.vidriver.controller;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import busdriver.com.vidriver.R;
import busdriver.com.vidriver.dialog.ErrorDialogFragment;


/**
 * A convenience class to handle displaying error dialogs.
 */
public class ErrorDialogHandler {

    FragmentManager mFragmentManager;

    public ErrorDialogHandler(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    public void showError(String errorMessage) {
        DialogFragment fragment = ErrorDialogFragment.newInstance(
                R.string.validationErrors, errorMessage);
        fragment.show(mFragmentManager, "error");
    }
}
