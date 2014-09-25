package me.mattlogan.rxjavaimagesearch;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class BaseActivity extends Activity {

    protected void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
