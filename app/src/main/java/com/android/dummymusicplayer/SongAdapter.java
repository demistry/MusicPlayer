package com.android.dummymusicplayer;

import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ILENWABOR DAVID on 01/11/2017.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private ArrayList<SongModel> songs;
    private ClickedSongInterface songInterface;
    public SongAdapter(ArrayList<SongModel> songs, ClickedSongInterface songInterface){
        this.songs = songs;
        this.songInterface = songInterface;
    }
    public interface ClickedSongInterface{
        void playClickedSong(int position);
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.local_item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SongViewHolder holder, int position) {
        if (songs.get(position).getSongAlbumCoverImage()!=null){
            holder.songAlbumCover.setImageBitmap(songs.get(position).getSongAlbumCoverImage());
        }

        holder.artistName.setText(songs.get(position).getArtistName());
        holder.songTitle.setSelected(true);
        holder.songTitle.setText(songs.get(position).getSongTitle());
        holder.songTime.setText(songs.get(position).getSongTime());
        holder.songItemRoot.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        songInterface.playClickedSong(holder.getAdapterPosition());
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

     class SongViewHolder extends RecyclerView.ViewHolder{
         TextView songTitle, artistName, songTime;
         ImageView songAlbumCover;
         RelativeLayout songItemRoot;
        public SongViewHolder(View view){
            super(view);
            songTitle = (TextView) view.findViewById(R.id.mini_song_title);
            artistName = (TextView) view.findViewById(R.id.mini_artist_name);
            songTime = (TextView) view.findViewById(R.id.mini_time);
            songAlbumCover = (ImageView) view.findViewById(R.id.mini_album_cover);
            songItemRoot = (RelativeLayout) view.findViewById(R.id.song_item_rootview);
        }
    }

}
