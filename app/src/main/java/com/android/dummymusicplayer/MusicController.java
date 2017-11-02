package com.android.dummymusicplayer;

import android.content.Context;
import android.widget.MediaController;

/**
 * Created by ILENWABOR DAVID on 01/11/2017.
 */

public class MusicController extends MediaController {
    public MusicController(Context context) {
        super(context);
    }

    @Override
    public void show(int timeout) {
        super.show(0);
    }

    @Override
    public void hide() {
        super.show(0);
    }
}
