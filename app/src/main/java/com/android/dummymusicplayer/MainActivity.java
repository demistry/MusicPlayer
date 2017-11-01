package com.android.dummymusicplayer;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SongAdapter.ClickedSongInterface {
    private RecyclerView songRv;
    private SongAdapter songAdapter;
    private ArrayList<SongModel> songs;
    private MusicService musicService;
    private Intent musicIntent;
    private boolean isBound = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                return;
            }}
        songRv = (RecyclerView) findViewById(R.id.music_recycler_view);
        songs = new ArrayList<>();
        getSongListFromDirectory();
        songAdapter = new SongAdapter(songs, this);
        songRv.setLayoutManager(new LinearLayoutManager(this));
        songRv.setAdapter(songAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (musicIntent == null){
            musicIntent = new Intent(this, MusicService.class);
            bindService(musicIntent, serviceConnection, BIND_AUTO_CREATE);
            startService(musicIntent);
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder musicBinder = (MusicService.MusicBinder) service;
            musicService = musicBinder.getService();
            musicService.setSongs(songs);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = true;
        }
    };
    public void getSongListFromDirectory(){
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = musicResolver.query(musicUri,null,null,null,null,null);
        if (cursor!=null && cursor.moveToFirst()){
            int songId = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int songTitleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistNameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumCoverImage = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int songTime = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            do {
                long songUniqueId = cursor.getLong(songId);
                String songTitle = cursor.getString(songTitleColumn);
                String artistName = cursor.getString(artistNameColumn);
                long songTimeSeconds = cursor.getLong(songTime)/1000;
                long songTimeMinutes = (songTimeSeconds%3600)/60;
                String songDuration =  String.format(Locale.ENGLISH,"%02d:%02d",songTimeMinutes,songTimeSeconds%60);
                Bitmap albumCover = getAlbumImage(cursor.getLong(albumCoverImage));
                songs.add(new SongModel(songTitle,songDuration,artistName,albumCover, songUniqueId));
            }while(cursor.moveToNext());
            cursor.close();
        }

    }
    private Bitmap getAlbumImage(long albumId){
        Bitmap artwork = null;
        try {
            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri, albumId);
            ContentResolver res = this.getContentResolver();
            InputStream in = res.openInputStream(uri);
            artwork = BitmapFactory.decodeStream(in);

        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
        return artwork;
    }

    @Override
    public void playClickedSong(int position) {
        musicService.playSong(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.playback_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_shuffle:
                break;
            case R.id.action_end:
                stopService(musicIntent);
                musicService = null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        stopService(musicIntent);
        musicService = null;
        super.onDestroy();
    }
}
