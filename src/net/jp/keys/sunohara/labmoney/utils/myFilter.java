
package net.jp.keys.sunohara.labmoney.utils;

import android.text.InputFilter;
import android.text.Spanned;

public class myFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
            int dend) {
        if (source.toString().matches("^[a-zA-Z0-9@.]+$")) {
            return source;
        } else {
            return "";
        }
    }

}
