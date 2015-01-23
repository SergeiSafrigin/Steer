package kcg.steer.main;

import kcg.steer.R;
import kcg.steer.gui.CameraPreview;
import kcg.steer.logic.StaticValues;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Context context;
	private RelativeLayout mainLayout;
	private CameraPreview cameraPreview;
	private Button distanceButton, directionButton, navigateButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);
		
		init(this);
	}


	private void init(Context context){
		this.context = context;
		cameraPreview = new CameraPreview(context);
		distanceButton = (Button) findViewById(R.id.distanceButton);
		directionButton = (Button) findViewById(R.id.directionButton);
		navigateButton = (Button) findViewById(R.id.navigateButton);

		mainLayout = (RelativeLayout) findViewById(R.id.directionMainLayout);
		mainLayout.addView(cameraPreview, 0);
		
		distanceButton.setOnClickListener(distanceButtonListener);
		directionButton.setOnClickListener(directionButtonListener);
		navigateButton.setOnClickListener(navigateButtonListener);
	}

	OnClickListener directionButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, DirectionActivity.class);
			startActivity(intent);
		}
	};

	OnClickListener distanceButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, DistanceActivity.class);
			startActivity(intent);
		}
	};

	OnClickListener navigateButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (StaticValues.direction == null)
				Toast.makeText(context, "Set Direction First", Toast.LENGTH_LONG).show();
			else if (StaticValues.distanceType == null)
				Toast.makeText(context, "Set Distance First", Toast.LENGTH_LONG).show();
			else if (StaticValues.targetDistance <= 0)
				Toast.makeText(context, "Distance Must Be Higher Than 0", Toast.LENGTH_LONG).show();
			else {
				Intent intent = new Intent(context, NavigationActivity.class);
				startActivity(intent);
			}
		}
	};
	
	@Override
	public void onResume(){
		super.onResume();
		
//		if (StaticValues.distanceType != null)
//			Toast.makeText(context, "distance = "+StaticValues.targetDistance, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		
		cameraPreview.onPause();
	}
}
