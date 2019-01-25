package com.gd.icbc.dutydeal.utils;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.AudioManager;

public class Mp3Play {
    private final Context context;
    private MediaPlayer mMediaPlayer = null;

    public Mp3Play(Context context) {
        this.context=context;
    }

    // 音乐播放方法
    public void play(Context context, int soundId) {
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
    // 停止播放音乐
    public void play_release() {
        if (mMediaPlayer == null) {
            return;
        }
        mMediaPlayer.setOnPreparedListener(null);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    public boolean isPlaying()
    {
        return mMediaPlayer.isPlaying();
    }
}
