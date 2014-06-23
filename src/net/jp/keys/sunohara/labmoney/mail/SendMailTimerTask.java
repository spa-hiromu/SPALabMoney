package net.jp.keys.sunohara.labmoney.mail;

import java.util.TimerTask;

import android.os.Handler;

public class SendMailTimerTask extends TimerTask {
    Handler mHandler;

    public SendMailTimerTask() {
        mHandler = new Handler();
    }

    @Override
    public void run() {

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                (new Thread(new Runnable() {

                    @Override
                    public void run() {
                        sendGmail sendGmail = new sendGmail("h1r0e44b11uk@gmail.com");

                    }
                })).start();

            }
        });

    }
}
