package com.android.dummymusicplayer;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ILENWABOR DAVID on 01/11/2017.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener,
                    MediaPlayer.OnErrorListener{
    private MediaPlayer mediaPlayer;
    private ArrayList<SongModel> songs;
    private int songPosition;
    private final MusicBinder binder = new MusicBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        songPosition = 0;
        mediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        mediaPlayer.release();
        return false;
    }




    public void setSongs(ArrayList<SongModel> songs){
        this.songs = songs;
    }
    public class MusicBinder extends Binder {
        public MusicService getService(){
            return MusicService.this;
        }
    }

    public void initMusicPlayer(){
        mediaPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
    }
    public void playSong(int position){
        mediaPlayer.reset();
        SongModel songPlayed = songs.get(position);
        long songId = songPlayed.getSongUniqueId();
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
        try{
            mediaPlayer.setDataSource(getApplicationContext(), uri);
        }
        catch (IOException e){
            Log.e("MusicService", "Error setting player data source", e);
        }
        mediaPlayer.prepareAsync();
    }




    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
