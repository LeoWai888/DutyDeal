package com.gd.icbc.dutydeal.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.gd.icbc.dutydeal.R;
import com.libface.bh.library.LibLiveDetect;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created on 2016/10/18 17:34.
 *
 * @author Han Xu
 */
public enum Settings {
    INSTANCE;
    public int[] getSequencesInt(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DetectListConstants.DETECTLIST_FILE, MODE_PRIVATE);
        String input = preferences.getString(DetectListConstants.DETECTLIST, DetectListConstants.DEFAULTDETECTLIST);
        String[] sequencesString = input.split("\\s+");
        int[] sequences = new int[sequencesString.length];
        for (int i = 0; i < sequencesString.length; i ++) {
            if (context.getString(R.string.blink).equals(sequencesString[i])) {
                sequences[i] = LibLiveDetect.EYEBLINK;
            } else if (context.getString(R.string.mouth).equals(sequencesString[i])) {
                sequences[i] = LibLiveDetect.OPENMOUTH;
            } else if (context.getString(R.string.yaw).equals(sequencesString[i])) {
                sequences[i] = LibLiveDetect.YAW;
            } else if (context.getString(R.string.nod).equals(sequencesString[i])) {
                sequences[i] = LibLiveDetect.DOWN_PITCH;
            }
        }
        return sequences;
    }
}