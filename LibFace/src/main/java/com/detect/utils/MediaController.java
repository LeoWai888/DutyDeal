package com.detect.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.facecore.bh.R;
import com.libface.bh.library.LibLiveDetect;

@SuppressLint("NewApi")
public final class MediaController {

    private MediaPlayer mMediaPlayer = null;

    public static MediaController getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void release() {
        if (mMediaPlayer == null) {
            return;
        }
        mMediaPlayer.setOnPreparedListener(null);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    public void playNotice(Context context, int motion) {
        switch(motion) {
            case LibLiveDetect.EYEBLINK:
                play(context, R.raw.notice_blink);
                break;
            case LibLiveDetect.OPENMOUTH:
                play(context, R.raw.notice_mouth);
                break;
            case LibLiveDetect.DOWN_PITCH:
                play(context, R.raw.notice_nod);
                break;
            case LibLiveDetect.YAW:
                play(context, R.raw.notice_yaw);
                break;
            default:
                break;
        }
    }

	private void play(Context context, int soundId) {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        AudioManager audioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
            }
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        mMediaPlayer = MediaPlayer.create(context, soundId);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    private MediaController() {
        // Do nothing.
    }

    private static class InstanceHolder {
        private static final MediaController INSTANCE = new MediaController();
    }
}