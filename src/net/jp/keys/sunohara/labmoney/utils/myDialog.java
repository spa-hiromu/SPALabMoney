package net.jp.keys.sunohara.labmoney.utils;

import java.util.EventListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


public class myDialog implements OnEditorActionListener, OnClickListener {
    private static final String mailFilter = "([a-zA-Z0-9][a-zA-Z0-9_.+\\-]*)@(([a-zA-Z0-9][a-zA-Z0-9_\\-]+\\.)+[a-zA-Z]{2,6})";

    private AlertDialog.Builder builder;
    private LinearLayout dialogLayout;
    private EditText nameEditText;
    private EditText mailEditText;
    private TextView warnTextView;
    private AlertDialog alertDialog;
    private OnClickDialogButtonListener mListener;

    public myDialog(Context context) {
        LinearLayout.LayoutParams templateLayout = new LinearLayout.LayoutParams(500, 100);
        TextView nameTextView = new TextView(context);
        TextView mailTextView = new TextView(context);

        builder = new AlertDialog.Builder(context);
        alertDialog = builder.create();
        dialogLayout = new LinearLayout(context);


        warnTextView = new TextView(context);

        nameEditText = new EditText(context);
        mailEditText = new EditText(context);
        InputFilter[] mFilter = {
                new InputFilter.LengthFilter(20),
                new myFilter()
        };

        mailEditText.setOnEditorActionListener(this);
        nameTextView.setText("名前");
        mailTextView.setText("メールアドレス");
        alertDialog.setMessage("名前とメールアドレスを入力してください");
        warnTextView.setTextColor(Color.RED);
        mailEditText.setFilters(mFilter);

        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.addView(nameTextView, templateLayout);
        dialogLayout.addView(nameEditText, templateLayout);
        dialogLayout.addView(mailTextView, templateLayout);
        dialogLayout.addView(mailEditText, templateLayout);
        dialogLayout.addView(warnTextView, templateLayout);
        alertDialog.setView(dialogLayout);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", this);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancell", this);
    }

    public void showDialog() {
        alertDialog.show();
    }

    public void setPositiveButtonEnabled(boolean enabled) {
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(enabled);
    }
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        String mailAddress;
        String name;
        if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            mailAddress = mailEditText.getText().toString();
            name = nameEditText.getText().toString();
            if (name.equals("")) {
                setPositiveButtonEnabled(false);
                warnTextView.setText("名前を入力してください");
                return false;
            } else if (mailAddress.equals("")) {
                warnTextView.setText("正しくメールアドレスを入力してください");
                setPositiveButtonEnabled(false);
                return false;
            } else {
                setPositiveButtonEnabled(true);
            }
        }
        return false;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_NEGATIVE) {
            dialog.dismiss();
        }
        if (which == DialogInterface.BUTTON_POSITIVE) {
            String nameString = nameEditText.getText().toString();
            String mailString = mailEditText.getText().toString();
            /** 入力されたテキストがメールアドレスかチェックする */
            if (isMailAddress(mailString)) {
                mListener.OnClickDialogButton();
            }
        }

    }

    public String getUserName() {
        return nameEditText.getText().toString();
    }

    public String getMailAddress() {
        return mailEditText.getText().toString();
    }
    /** テキストがメールアドレスかを正規表現でチェックする */
    public boolean isMailAddress(String address) {
        return address.matches(mailFilter) ? true : false;
    }

    public void setOnClickDialogButtonListener(OnClickDialogButtonListener listener) {
        mListener = listener;
    }

    /**
     * ダイアログのポジティブボタンが押された事を通知するinterface HACK:インターフェイス名が適切でないので後で変える
     */
    public interface OnClickDialogButtonListener extends EventListener {
        public void OnClickDialogButton();
    }

}
