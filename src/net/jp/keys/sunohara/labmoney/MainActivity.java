/**メインのアクティビティ
 * Fragmentの管理を行う
 * @author hiromu
 * */

package net.jp.keys.sunohara.labmoney;

import java.util.Timer;

import net.jp.keys.sunohara.labmoney.DataBase.DBManager;
import net.jp.keys.sunohara.labmoney.Fragments.SelectFragment;
import net.jp.keys.sunohara.labmoney.mail.SendMailTimerTask;
import net.jp.keys.sunohara.labmoney.utils.myDialog;
import net.jp.keys.sunohara.labmoney.utils.myDialog.OnClickDialogButtonListener;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;

public class MainActivity extends Activity implements OnClickDialogButtonListener {
    private String uID;
    private String mUserName;
    private DBManager mDbManager;
    private Timer mTimer;
    private SendMailTimerTask sendMailTimerTask;
    private myDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbManager = new DBManager(getApplicationContext());
        mDialog = new myDialog(this);
        mDialog.setOnClickDialogButtonListener(this);

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
            mDialog.showDialog();
            mDialog.setPositiveButtonEnabled(false);
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
        String DBuID = "";
        String userName = "";
        /** 登録済みかを確認する */
        Cursor cursor = mDbManager.mySearch(DBManager.USER_TABLE, new String[] {
                DBManager.UID_COLUMN, DBManager.USER_COLUMN, DBManager.MAIL_COlUMN
        });
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            DBuID = cursor.getString(0);
            Log.d("DBuID", DBuID);
            if (uID.equals(DBuID)) {
                userName = cursor.getString(1);
                Log.d("mUserName", userName);
            }
        }
        return userName;
    }

    private void FragmentInit(String uName) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SelectFragment selectFragment = new SelectFragment();
        Bundle bundle = new Bundle();
        selectFragment.setArguments(bundle);
        bundle.putString("UID", uID);
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

    /** priceテーブルの値を更新する */
    public int updatePrice(String priceColumn, int price, String countColumn, int count, String UID) {
        return mDbManager.updatePrice(priceColumn, price, countColumn, count, UID);
    }

    @Override
    public void OnClickDialogButton() {
        mDbManager.insert(uID, mDialog.getUserName(), mDialog.getMailAddress());
        FragmentInit(mDialog.getUserName());
    }

    public int getPriceTableValue(String UID, String column) {
        int value = -1;
        Cursor cursor = mDbManager.mySearch(DBManager.PRICE_TABLE, new String[] {
                DBManager.UID_COLUMN, column
        });

        if (cursor == null) {
            mDbManager.insertPrice(UID);
            return getPriceTableValue(UID, column);
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(0).equals(UID)) {
                value = cursor.getInt(1);
                break;
            }
            cursor.moveToNext();
        }
        return value;
    }
}
