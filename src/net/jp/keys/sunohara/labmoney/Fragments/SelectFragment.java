
package net.jp.keys.sunohara.labmoney.Fragments;

import net.jp.keys.sunohara.labmoney.BaseFragment;
import net.jp.keys.sunohara.labmoney.MainActivity;
import net.jp.keys.sunohara.labmoney.R;
import net.jp.keys.sunohara.labmoney.DataBase.DBManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        TextView uIDTextView = (TextView) view.findViewById(R.id.nfcid);
        uIDTextView.setText("こんにちは" + getArguments().getString("USER_NAME") +
                "さん");
        Button juiceButton = (Button) view.findViewById(R.id.juiceButton);
        Button nudleButton = (Button) view.findViewById(R.id.nudleButton);


        juiceButton.setOnClickListener(this);
        nudleButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int price = 0;
        switch (v.getId()) {
            case R.id.juiceButton:
                price = JUICE_PRICE;
                /** TODO:Countの処理も実装する */
                ((MainActivity) getActivity()).updatePrice(DBManager.DRINK_PRICE_COLUMN, price,
                        "test");
                break;
            case R.id.nudleButton:
                price = NUDLE_PRICE;
                /** TODO:Countの処理も実装する */
                ((MainActivity) getActivity()).updatePrice(DBManager.NUDLE_PRICE_COLUMN, price,
                        "test");
                break;
            default:
                price = 0;
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
}
