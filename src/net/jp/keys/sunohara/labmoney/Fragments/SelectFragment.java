
package net.jp.keys.sunohara.labmoney.Fragments;

import net.jp.keys.sunohara.labmoney.MainActivity;
import net.jp.keys.sunohara.labmoney.R;
import net.jp.keys.sunohara.labmoney.DataBase.DBManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SelectFragment extends BaseFragment implements OnClickListener,
        android.content.DialogInterface.OnClickListener {
    private static final int JUICE_PRICE = 100;
    private static final int NUDLE_PRICE = 110;
    private String UID;
    private int totalJuicePrice;
    private int totalJuiceCount;
    private int totalNudlePrice;
    private int totalNudelCount;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment, container, false);
        TextView uIDTextView = (TextView) view.findViewById(R.id.nfcid);
        uIDTextView.setText("こんにちは" + getArguments().getString("USER_NAME") +
                "さん");
        UID = getArguments().getString("UID");
        Button juiceButton = (Button) view.findViewById(R.id.juiceButton);
        Button nudleButton = (Button) view.findViewById(R.id.nudleButton);


        juiceButton.setOnClickListener(this);
        nudleButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        getPriceTable();
        int price = 0;
        int count = 0;
        switch (v.getId()) {
            case R.id.juiceButton:
                price = totalJuicePrice + JUICE_PRICE;
                count = totalJuiceCount++;
                Log.d("juice", String.valueOf("count:" + count + ",price:" + price));
                mainActivity.updatePrice(DBManager.DRINK_PRICE_COLUMN, price,
                        DBManager.DRINK_COUNT_COLUMN, count,
                        UID);
                break;
            case R.id.nudleButton:
                price = totalNudlePrice + NUDLE_PRICE;
                count = totalNudelCount++;
                Log.d("juice", String.valueOf("count:" + count + ",price:" + price));
                mainActivity.updatePrice(DBManager.NUDLE_PRICE_COLUMN, price,
                        DBManager.NUDLE_COUNT_COLUMN, count,
                        UID);
                break;
            default:
                price = 0;
                count = 0;
        }
        showConfirmDialog(price);
    }

    private void showConfirmDialog(int price) {
        AlertDialog.Builder confirm = new AlertDialog.Builder(getActivity());
        confirm.setMessage("購入代金は" + String.valueOf(price) + "円です。");
        confirm.setPositiveButton("OK", this);
        confirm.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        getActivity().finish();
    }

    private void getPriceTable() {
        totalJuicePrice = mainActivity.getPriceTableValue(UID,
                DBManager.DRINK_PRICE_COLUMN);
        totalJuiceCount = mainActivity.getPriceTableValue(UID,
                DBManager.DRINK_COUNT_COLUMN);
        totalNudlePrice = mainActivity.getPriceTableValue(UID,
                DBManager.NUDLE_PRICE_COLUMN);
        totalNudelCount = mainActivity.getPriceTableValue(UID,
                DBManager.NUDLE_COUNT_COLUMN);
        Log.d("getPriceTable",
                String.valueOf("totalJuicePrice:" + totalJuicePrice + "totalJuiceCount:"
                        + totalJuiceCount));
    }
}
