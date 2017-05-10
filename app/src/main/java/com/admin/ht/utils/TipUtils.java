package com.admin.ht.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import java.io.IOException;

/**
 * Created by Spec_Inc on 5/7/2017.
 */

public class TipUtils {

    public static void tipMsg(Context context){

        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{300,500},-1);


        // 使用来电铃声的铃声路径
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(context, uri);
            player.setLooping(false); //循环播放
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
