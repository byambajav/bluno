package com.hacku.mayowan;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends BlunoLibrary {
	private static final String TAG = MainActivity.class.getSimpleName();

	private Button buttonScan;
	private Button buttonSerialSend;
	private EditText serialSendText;
	private PlainProtocol mPlainProtocol= new PlainProtocol();

	// MayowanUI components
	private TextView mayowanDistance;
	private ImageView mayowanPic;
	private ImageView mayowanThumbnail;
	private TextView mayowanMsg;

	private int readRSSIInterval = 3000; // 3 seconds
	private Handler mHandler;

	Runnable rssiReader = new Runnable() {
		@Override 
		public void run() {
			if (mConnectionState == connectionStateEnum.isConnected) {
				mBluetoothLeService.readRemoteRSSI(); //this function can change value of mInterval.
				changeMayowanUI(mBluetoothLeService.currentRSSI);
				/*Toast.makeText(MainActivity.this, "RSSI: " + mBluetoothLeService.currentRSSI,
						Toast.LENGTH_LONG).show();*/
			}
			else {
				mBluetoothLeService.currentRSSI = 0;
			}
			
			mHandler.postDelayed(rssiReader, readRSSIInterval);
		}
	};

	void startRepeatingTask() {
		rssiReader.run(); 
	}

	void stopRepeatingTask() {
		mHandler.removeCallbacks(rssiReader);
	}

	/**
	 * Changes UI based on RSSI value.
	 */
	private void changeMayowanUI(int rssi){
		int distance;
		if (rssi == 0) {
			distance = 0; // Not connected yet
			mayowanDistance.setText(" ");
			mayowanThumbnail.setImageDrawable(getResources().getDrawable(R.drawable.thumb0));
			mayowanMsg.setText("          ");
			mayowanPic.setVisibility(View.INVISIBLE);
		}
		else if (rssi > -40) {
			distance = 1; // あんしんだわん
			mayowanDistance.setText(Html.fromHtml("<small>" + "だいたい " + "</small>" +  
					"<big>" + "1m" + "</big>"));
			mayowanThumbnail.setImageDrawable(getResources().getDrawable(R.drawable.thumb1));
			mayowanMsg.setText("あんしんだわん!");
			mayowanPic.setImageDrawable(getResources().getDrawable(R.drawable.img1));
			mayowanPic.setVisibility(View.VISIBLE);
		}
		else if (rssi > -50) {
			distance = 3; // ちょうどいいわん
			mayowanDistance.setText(Html.fromHtml("<small>" + "だいたい " + "</small>" +  
					"<big>" + "3m" + "</big>"));
			mayowanThumbnail.setImageDrawable(getResources().getDrawable(R.drawable.thumb2));
			mayowanMsg.setText("ちょうどいいわん!");
			mayowanPic.setImageDrawable(getResources().getDrawable(R.drawable.img2));
			mayowanPic.setVisibility(View.VISIBLE);
		}
		else if (rssi > -60) {
			distance = 10; // きをつけるわん
			mayowanDistance.setText(Html.fromHtml("<small>" + "だいたい " + "</small>" +  
					"<big>" + "10m" + "</big>"));
			mayowanThumbnail.setImageDrawable(getResources().getDrawable(R.drawable.thumb3));
			mayowanMsg.setText("きをつけるわん!");
			mayowanPic.setImageDrawable(getResources().getDrawable(R.drawable.img3));
			mayowanPic.setVisibility(View.VISIBLE);
		}
		else {
			distance = 50; // はなれすぎわん
			mayowanDistance.setText(Html.fromHtml("<small>" + "だいたい " + "</small>" +  
					"<big>" + "50m" + "</big>"));
			mayowanThumbnail.setImageDrawable(getResources().getDrawable(R.drawable.thumb4));
			mayowanMsg.setText("はなれすぎわん!");
			mayowanPic.setImageDrawable(getResources().getDrawable(R.drawable.img4));
			mayowanPic.setVisibility(View.VISIBLE);
		}

		// Send rssi data 
		serialSend(mPlainProtocol.write(BleCmd.Distance + distance, 0, 0));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create handler to run rssiReader
		mHandler = new Handler();
		startRepeatingTask();

		// change color
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF8800")));

		// MayowanUI components
		mayowanDistance = (TextView) findViewById(R.id.mayowan_distance);
		mayowanPic = (ImageView) findViewById(R.id.mayowan_pic);
		mayowanThumbnail = (ImageView) findViewById(R.id.mayowan_thumbnail);
		mayowanMsg = (TextView) findViewById(R.id.mayowan_msg);

		onCreateProcess(); //onCreate Process by BlunoLibrary

		buttonScan = (Button) findViewById(R.id.buttonScan); //initial the button for scanning the BLE device
		buttonScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonScanOnClickProcess(); //Alert Dialog for selecting the BLE device
			}
		});

		serialSendText = (EditText) findViewById(R.id.serialSendText); //initial the EditText of the sending data		

		buttonSerialSend = (Button) findViewById(R.id.buttonSerialSend); //initial the button for sending the data
		buttonSerialSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				serialSend(mPlainProtocol.write(BleCmd.Disp + serialSendText.getText(), 0,0));
			}
		});      
	}

	protected void onResume(){
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		onResumeProcess(); //onResume Process by BlunoLibrary
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResultProcess(requestCode, resultCode, data); //onActivityResult Process by BlunoLibrary
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		onPauseProcess(); //onPause Process by BlunoLibrary
	}

	protected void onStop() {
		super.onStop();
		onStopProcess(); //onStop Process by BlunoLibrary
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();	
		onDestroyProcess(); //onDestroy Process by BlunoLibrary
	}

	@Override
	public void onConnectionStateChange(connectionStateEnum theConnectionState) { //Once connection state changes, this function will be called
		switch (theConnectionState) { //Four connection state
		case isConnected:
			buttonScan.setText(getString(R.string.connected));
			break;
		case isConnecting:
			buttonScan.setText(R.string.connecting);
			break;
		case isToScan:
			buttonScan.setText(R.string.scan);
			break;
		case isScanning:
			buttonScan.setText(R.string.scanning);
			break;
		case isDisconnecting:
			buttonScan.setText(R.string.disconnecting);
			break;
		default:
			break;
		}
	}

	@Override
	public void onSerialReceived(String theString) { //Once connection data received, this function will be called
		// TODO Auto-generated method stub
		//serialReceivedText.append(theString + "\n"); //append the text into the EditText
		//The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.					
	}

}