package com.github.tetris;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class NewGameActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
    final Activity me = this;
    TextView profileName;
    SeekBar diffLevel;
    TextView diffTxt;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_game);
        Button start = (Button) findViewById(R.id.btn_newgame_start);
        diffLevel = (SeekBar) findViewById(R.id.seekb_newgame_difficulty);
        diffLevel.setOnSeekBarChangeListener(this);
        profileName = (TextView) findViewById(R.id.txt_main_menu_profile);
        diffTxt = (TextView) findViewById(R.id.txt_newgame_difficulty);
        diffTxt.setText(
                getString(R.string.difficult) + "  " + Integer.toString(Profile.DEFAULT_DIFFICULT));
        diffLevel.setProgress(Profile.DEFAULT_DIFFICULT);
        start.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intt = new Intent(me, TetriBlastActivity.class);
                        startActivity(intt);
                    }
                });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        diffTxt.setText(getString(R.string.difficult) + "  " + Integer.toString(progress));
        // TODO add changes to the profile

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }
}
