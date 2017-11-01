package com.android.dummymusicplayer;

import android.graphics.Bitmap;

/**
 * Created by ILENWABOR DAVID on 01/11/2017.
 */

public class SongModel {
    private String songTitle;
    private String songTime;
    private String artistName;
    private Bitmap songAlbumCoverImage;
    private long songUniqueId;


    public SongModel(String songTitle, String songTime, String artistName, Bitmap songAlbumCoverImage, long songUniqueId){
        this.songTitle = songTitle;
        this.songTime = songTime;
        this.artistName = artistName;
        this.songAlbumCoverImage = songAlbumCoverImage;
        this.songUniqueId = songUniqueId;
    }

    public String getSongTitle() {
        return songTitle;
    }


    public String getSongTime() {
        return songTime;
    }

    public String getArtistName() {
        return artistName;
    }


    public Bitmap getSongAlbumCoverImage() {
        return songAlbumCoverImage;
    }

    public long getSongUniqueId() {
        return songUniqueId;
    }
}
