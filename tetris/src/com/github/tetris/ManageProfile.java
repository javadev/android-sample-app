package com.github.tetris;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;

public class ManageProfile extends ListActivity {
    private final ListActivity me = this;
    private ListAdapter mAdapter = null;
    Button newProfile;
    Button selectProfile;
    Button deleteProfile;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_profile);

        Button newProfile = (Button) findViewById(R.id.btn_manage_new);

        newProfile.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intt = new Intent(me, TetriBlastActivity.class);
                        startActivity(intt);
                    }
                });
    }
}
