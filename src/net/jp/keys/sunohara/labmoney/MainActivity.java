/**メインのアクティビティ
 * Fragmentの管理を行う
 * @author hiromu
 * */

package net.jp.keys.sunohara.labmoney;

import java.util.Timer;

import net.jp.keys.sunohara.labmoney.DataBase.DBManager;
import net.jp.keys.sunohara.labmoney.Fragments.SelectFragment;
import net.jp.keys.sunohara.labmoney.mail.SendMailTimerTask;
import net.jp.keys.sunohara.labmoney.utils.myFilter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends Activity implements OnClickListener, OnEditorActionListener {
    private static final String mailFilter = "([a-zA-Z0-9][a-zA-Z0-9_.+\\-]*)@(([a-zA-Z0-9][a-zA-Z0-9_\\-]+\\.)+[a-zA-Z]{2,6})";
    private String uID;
    private String DBuID;
    private String mUserName;
    private DBManager mDbManager;
    private EditText nameEditText;
    private EditText mailEditText;
    private Timer mTimer;
    private SendMailTimerTask sendMailTimerTask;
    private AlertDialog mAlertDialog;
    private TextView warnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbManager = new DBManager(getApplicationContext());
        /** NFCの読み込み部分 */
        NFCInit();
        // mTimer = new Timer();
        // sendMailTimerTask = new SendMailTimerTask();

        // mTimer.schedule(sendMailTimerTask, 1000, 60000);

        /**
         * DBからユーザデータを読み出して登録済みかをチェックする
         * ここでmUserNameに値が入る(登録済み)か入らない(未登録)かによって処理が変わる
         */
        mUserName = checkID();
        /** 未登録の場合 */
        if (mUserName.equals("")) {
            mAlertDialog = DialogInit();
            mAlertDialog.show();
            mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        } else {
            /** 登録済みの場合 */
            FragmentInit(mUserName);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private String bytesToText(byte[] bytes) {
        StringBuilder buffer = new StringBuilder();
        for (byte b : bytes) {
            String hex = String.format("%02X", b);
            buffer.append(hex).append(" ");
        }
        return buffer.toString().trim();
    }

    /** IDがDBに登録されているかチェックする */
    private String checkID() {
        String userName = "";
        /** 登録済みかを確認する */
        Cursor cursor = mDbManager.mySearch(DBManager.USER_TABLE, new String[] {
                DBManager.UID_COLUMN, DBManager.USER_COLUMN, DBManager.MAIL_COlUMN
        });
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            DBuID = cursor.getString(0);
            Log.d("DBuID", DBuID);
            if (DBuID.equals(uID)) {
                userName = cursor.getString(1);
                Log.d("mUserName", userName);
            }
        }
        return userName;
    }

    private AlertDialog DialogInit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        LinearLayout dialogLayout = new LinearLayout(this);
        LinearLayout.LayoutParams templateLayout = new LinearLayout.LayoutParams(500, 100);
        TextView nameTextView = new TextView(this);
        TextView mailTextView = new TextView(this);
        warnTextView = new TextView(this);

        nameEditText = new EditText(this);
        mailEditText = new EditText(this);
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
        return alertDialog;
    }

    private void FragmentInit(String uName) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SelectFragment selectFragment = new SelectFragment();
        Bundle bundle = new Bundle();
        selectFragment.setArguments(bundle);
        bundle.putString("USER_NAME", uName);
        fragmentTransaction.replace(android.R.id.content, selectFragment);
        fragmentTransaction.commit();
    }

    private void NFCInit() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action.equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {
            byte[] rawID = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            String text = bytesToText(rawID);
            uID = text;
            Log.d("NFC-ID:", text);
        }
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
                mDbManager.insert(uID, nameString, mailString);
                FragmentInit(nameString);
            }
        }
    }

    public int updatePrice(String column, int value, String UID) {
        return mDbManager.updatePrice(column, value, UID);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.main);
        Log.d("Window_width", String.valueOf(rl.getWidth()));
        Log.d("Window_height", String.valueOf(rl.getHeight()));
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        String mailAddress;
        String name;
        if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            mailAddress = mailEditText.getText().toString();
            name = nameEditText.getText().toString();
            if (name.equals("")) {
                mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                warnTextView.setText("名前を入力してください");
                return false;
            } else if (mailAddress.equals("")) {
                warnTextView.setText("正しくメールアドレスを入力してください");
                mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                return false;
            } else {
                mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
            }
        }
        return false;
    }

    /** テキストがメールアドレスかを正規表現でチェックする */
    public boolean isMailAddress(String address) {
        return address.matches(mailFilter) ? true : false;
    }

}
