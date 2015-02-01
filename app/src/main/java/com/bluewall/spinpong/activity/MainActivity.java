package com.bluewall.spinpong.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageView;

import com.bluewall.spinpong.R;
import com.bluewall.spinpong.View.OnReleaseListener;
import com.bluewall.spinpong.View.ReleaseImageButton;

/**
 * Created by david on 12/13/14.
 */
public class MainActivity extends Activity {

    private ImageView background;
    private ReleaseImageButton buttonSinglePlayer;
    private ReleaseImageButton buttonMultiPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        background = (ImageView) findViewById(R.id.background);
        setBackgroundImage(getResources().getConfiguration().orientation);

        buttonSinglePlayer = (ReleaseImageButton) findViewById(R.id.buttonSinglePlayer);
        buttonMultiPlayer = (ReleaseImageButton) findViewById(R.id.buttonMultiPlayer);
        buttonSinglePlayer.setImages(R.drawable.button_single_player, R.drawable.button_single_player_down);
        buttonMultiPlayer.setImages(R.drawable.button_multi_player, R.drawable.button_multi_player_down);

        buttonSinglePlayer.setOnReleaseListener(new OnReleaseListener() {
            @Override
            public void onRelease() {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setBackgroundImage(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            background.setImageResource(R.drawable.horizontal);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT){
            background.setImageResource(R.drawable.vertical);
        }
    }

}
