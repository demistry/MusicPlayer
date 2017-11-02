package com.android.dummymusicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ILENWABOR DAVID on 01/11/2017.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener,
                    MediaPlayer.OnErrorListener{
    private MediaPlayer mediaPlayer;
    private ArrayList<SongModel> songs;
    private int songPosition;
    public static final int NOTIF_ID = 100;
    private boolean shuffle;
    private Random random = new Random();
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
        this.songPosition=position;
        SongModel songPlayed = songs.get(position);
        long songId = songPlayed.getSongUniqueId();
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
        try{
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.prepareAsync();
        }
        catch (IOException e){
            Log.e("MusicService", "Error setting player data source", e);
            Toast.makeText(getApplicationContext(), "Error playing file \nFile corrupt", Toast.LENGTH_SHORT).show();
        }
        catch (IllegalStateException e){
            Log.e("MusicService", "Error setting player data source", e);
            //Toast.makeText(getBaseContext(), "Error playing file \nFile corrupt", Toast.LENGTH_SHORT).show();
        }

    }

    public void setShuffle(){
        if (shuffle){
            shuffle = false;
        }
        else shuffle = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    public void playNext(){
        if (shuffle)
        {
            int shuffledSong = songPosition;
            while (shuffledSong == songPosition){
                shuffledSong = random.nextInt(songs.size());
            }
            songPosition = shuffledSong;
            playSong(songPosition);
        }
        else {
            songPosition++;
            if (songPosition<songs.size())playSong(songPosition);
        }

    }
    public void playPrev(){
        if(songPosition>0) songPosition--;
        playSong(songPosition);
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        if(mediaPlayer.getCurrentPosition()!=0)
        {
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Intent notifIntent = new Intent(this, MainActivity.class);
        notifIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.no_image)
                .setOngoing(true)
                .setTicker(songs.get(songPosition).getSongTitle())
                .setContentIntent(pendingIntent)
                .setContentText(songs.get(songPosition).getSongTitle());
        startForeground(NOTIF_ID, builder.build());
    }
    public int getPosn(){
        return mediaPlayer.getCurrentPosition();
    }

    public int getDur(){
        return mediaPlayer.getDuration();
    }

    public boolean isPng(){
        return mediaPlayer.isPlaying();
    }

    public void pausePlayer(){
        mediaPlayer.pause();
    }

    public void seek(int posn){
        mediaPlayer.seekTo(posn);
    }

    public void go(){
        mediaPlayer.start();
    }
}
