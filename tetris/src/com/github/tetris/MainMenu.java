package com.github.tetris;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainMenu extends Activity{
	private final Activity me = this;
	public static final String TAG = "TetrisBlast";
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        Button singleBtn = (Button)findViewById(R.id.btn_main_menu_single);
        Button multiBtn = (Button)findViewById(R.id.btn_main_menu_multi);
        Button manageBtn = (Button)findViewById(R.id.btn_main_menu_manage);
        Button settingBtn = (Button)findViewById(R.id.btn_main_menu_settings);
        Button leaderBtn = (Button)findViewById(R.id.btn_main_menu_leader);
        Button helpBtn = (Button)findViewById(R.id.btn_main_menu_help);
        TextView verTxt = (TextView)findViewById(R.id.txt_main_menu_ver);
        verTxt.setText(verTxt.getText().toString() + getString(R.string.ver_num));
        //Button Listenerses
        singleBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intt = new Intent(me, NewGameActivity.class);
				startActivity(intt);
			}
		});
        
        multiBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intt = new Intent(me, ConnectionActivity.class);
				startActivity(intt);
			}
		});
        
        manageBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intt = new Intent(me, ManageProfile.class);
				startActivity(intt);
			}
		});
    }
}
