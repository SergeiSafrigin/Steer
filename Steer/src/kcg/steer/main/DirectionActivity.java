package kcg.steer.main;

import kcg.steer.R;
import kcg.steer.compass.Compass;
import kcg.steer.compass.CompassSensor;
import kcg.steer.gui.CameraPreview;
import kcg.steer.gui.DirectionView;
import kcg.steer.logic.StaticValues;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class DirectionActivity extends Activity{
	private DirectionView directionView;
	private RelativeLayout mainLayout;
	private CameraPreview cameraPreview;
	private CompassSensor compassSensor;
	private Compass compass;
	private Button saveButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
				
		setContentView(R.layout.activity_direction);
		
		init(this);
	}
	
	private void init(Context context){
		saveButton = (Button) findViewById(R.id.directionSaveButton);
		cameraPreview = new CameraPreview(context);
		directionView = new DirectionView(context);
		mainLayout = (RelativeLayout) findViewById(R.id.directionMainLayout);
		
		mainLayout.addView(cameraPreview, 0);
		mainLayout.addView(directionView, 1);
		
		compass = new Compass();
		compassSensor = new CompassSensor(context, compass);
		
		saveButton.setOnClickListener(saveButtonListener);
	}
	
	public OnClickListener saveButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			StaticValues.direction = compass;
			finish();
		}
	};
	
	@Override
	public void onResume(){
		super.onResume();
		
		compassSensor.onResume();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		
		cameraPreview.onPause();
		compassSensor.onStopOrPause();
	}
}
