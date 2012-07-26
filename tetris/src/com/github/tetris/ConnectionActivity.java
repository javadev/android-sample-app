package com.github.tetris;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ConnectionActivity extends Activity {
	final Activity me = this;
	public static final String TAG = "TetrisBlast";
    
	 // Intent request codes
    private static final int REQUEST_DISCOVER_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    
    
    // Member object for the chat services
    private BluetoothMsgService mChatService = null;
    
	// Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection);
        Log.d(TAG, "Connection Activity Created");
        //getWindow().setBackgroundDrawableResource(R.drawable.tetris_bg);//Draw background
        Button hostBtn = (Button)findViewById(R.id.btn_host);
        Button joinBtn = (Button)findViewById(R.id.btn_join);
                
        hostBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "ensure discoverable");
				//TODO pass parameter Game Mode to TetrisBalst Activity
				if (mBluetoothAdapter.getScanMode() !=
						BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
					Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
					discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
					startActivityForResult(discoverableIntent, REQUEST_DISCOVER_DEVICE);
				}
				else {
					Intent intt = new Intent(me, TetriBlastActivity.class);
					startActivity(intt);
				}
			    
			}
		});
        
        joinBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Launch the DeviceListActivity to see devices and do scan
	            Intent serverIntent = new Intent(me, DeviceListActivity.class);
	            startActivityForResult(serverIntent, REQUEST_DISCOVER_DEVICE);
			}
		});
        
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
    }
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_DISCOVER_DEVICE:
          	// When the request to enable Bluetooth returns
            if (resultCode != 0) {
            	Intent intt = new Intent(me, TetriBlastActivity.class);
				startActivity(intt);
            } else {
                // User did not enable Bluetooth or an error occured
                Log.d(TAG, "Device not Discoverable");
                Toast.makeText(this, "Device not Discoverable" , Toast.LENGTH_SHORT).show();
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            } else {
                // User did not enable Bluetooth or an error occured
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, "BT not enabled" , Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    }
    
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        //sendMessage(message);
        
        // Initialize the BluetoothChatService to perform bluetooth connections
        //mChatService = new BluetoothMsgService(this, mHandler);
    }

}