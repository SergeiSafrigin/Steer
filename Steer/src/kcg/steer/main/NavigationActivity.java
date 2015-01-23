package kcg.steer.main;

import java.util.Vector;

import kcg.steer.R;
import kcg.steer.compass.Compass;
import kcg.steer.compass.CompassSensor;
import kcg.steer.gui.CameraPreview;
import kcg.steer.gui.OnFixStatusChangedListener;
import kcg.steer.ins.StepDetector;
import kcg.steer.location.LocationScanner;
import kcg.steer.logic.Data;
import kcg.steer.logic.Point3d;
import kcg.steer.logic.StaticValues;
import kcg.steer.sound.SoundNavigation;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NavigationActivity extends Activity implements DistanceLeftViewUpdater {
	private RelativeLayout mainLayout;
	private TextView distanceLeftTextView;
	private ImageView arrowImageView, gpsStatusImageView;
	private CameraPreview cameraPreview;
	private CompassSensor compassSensor;
	private LocationScanner locationScanner;
	private SoundNavigation soundNavigation;
	private StepDetector stepDetector;
	private Data data;
	private Handler updateImageHandler;
	private boolean finished = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_navigation);
		
		init(this);
	}

	private void init(Context context){
		updateImageHandler = new Handler();
		
		data = new Data();
		data.setDirection(StaticValues.direction);
		data.setTargetDistance(StaticValues.targetDistance);
		
		soundNavigation = new SoundNavigation(context, data, getSoundResources());
		
		stepDetector = new StepDetector(context, data, this);
		
		cameraPreview = new CameraPreview(context);
		arrowImageView = (ImageView) findViewById(R.id.arrowImageView);
		gpsStatusImageView = (ImageView) findViewById(R.id.gpsStatusImageView);
		distanceLeftTextView = (TextView) findViewById(R.id.navigationDistanceNumberTextView);
		
		mainLayout = (RelativeLayout) findViewById(R.id.navigationMainLayout);

		mainLayout.addView(cameraPreview, 0);

		compassSensor = new CompassSensor(context, data.getCompass());
		locationScanner = new LocationScanner(context, data, this);
		locationScanner.setOnFixStatusChangedListener(gpsFixListener);
		
		updateTargetLocation();
		updateDistaceLeftTextView();
		
	}
	
	private Vector<Integer> getSoundResources(){
		Vector<Integer> resources = new Vector<Integer>();
		resources.add(R.raw.forward);
		resources.add(R.raw.left_en);
		resources.add(R.raw.right_en);
		resources.add(R.raw.finish_en);
		return resources;
	}

	private void updateTargetLocation(){
		Point3d targetLocation = data.getTargetLocation();
		Compass direction = StaticValues.direction;
		double distance = StaticValues.targetDistance;
		
		double x = distance * Math.sin(Math.toRadians(direction.getYaw()));
		double y = distance * Math.cos(Math.toRadians(direction.getYaw()));
				
		synchronized (targetLocation) {
			targetLocation.set(new Point3d(x,y));
		}
	}
	
	public void updateDistaceLeftTextView(){
		distanceLeftTextView.setText(String.format("%.2f", data.getTargetDistance()));
	}

	private void updateArrowImageView(){
		new Thread(){
			@Override
			public void run(){
				Compass compass = data.getCompass();
				Compass direction = StaticValues.direction;
				double dYaw;
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				while(!finished){
					synchronized (compass) {
						synchronized (direction) {
							dYaw = Compass.getYawDiference(compass.getYaw(), direction.getYaw());
						}
					}

					if (Math.abs(dYaw) <= StaticValues.MAX_DELTA_YAW_FOR_FORWARD)
						updateImageHandler.post(updateToUp);
					else if (Math.abs(dYaw) <= StaticValues.MAX_DELTA_YAW_FOR_FORWARD_LEFT_OR_RIGHT){
						if (dYaw > 0)
							updateImageHandler.post(updateToLeftUp);
						else
							updateImageHandler.post(updateToRightUp);
					} else {
						if (dYaw > 0)
							updateImageHandler.post(updateToLeft);
						else
							updateImageHandler.post(updateToRight);
					}
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();

		new Thread(){
			@Override
			public void run(){
				while(!finished){
					updateImageHandler.post(makeArrowVisible);

					try {
						Thread.sleep(800);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					updateImageHandler.post(makeArrowInvisible);
					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	private final Runnable updateToUp = new Runnable() {
		@Override
		public void run() {
			arrowImageView.setImageResource(R.drawable.arrow_up);
		}
	};

	private final Runnable updateToLeft = new Runnable() {
		@Override
		public void run() {
			arrowImageView.setImageResource(R.drawable.arrow_left);
		}
	};

	private final Runnable updateToRight = new Runnable() {
		@Override
		public void run() {
			arrowImageView.setImageResource(R.drawable.arrow_right);
		}
	};

	private final Runnable updateToLeftUp = new Runnable() {
		@Override
		public void run() {
			arrowImageView.setImageResource(R.drawable.arrow_left_up);
		}
	};

	private final Runnable updateToRightUp = new Runnable() {
		@Override
		public void run() {
			arrowImageView.setImageResource(R.drawable.arrow_right_up);
		}
	};
	
	private final Runnable makeArrowVisible = new Runnable() {
		@Override
		public void run() {
			arrowImageView.setVisibility(View.VISIBLE);
		}
	};

	private final Runnable makeArrowInvisible = new Runnable() {
		@Override
		public void run() {
			arrowImageView.setVisibility(View.INVISIBLE);
		}
	};
	
	private OnFixStatusChangedListener gpsFixListener = new OnFixStatusChangedListener(){
		@Override
		public void onChanged(StaticValues.GPS_STATUS status) {
			if (status == StaticValues.GPS_STATUS.FIX){
				gpsStatusImageView.setImageResource(R.drawable.gps_fix);
			} else if (status == StaticValues.GPS_STATUS.SEARCHING){
				gpsStatusImageView.setImageResource(R.drawable.gps_searching);
			} else { //status == StaticValues.GPS_STATUS.OFF
				gpsStatusImageView.setImageResource(R.drawable.gps_off);
			}
		}
	};

	@Override
	public void onResume(){
		super.onResume();
		finished = false;
		compassSensor.onResume();
		locationScanner.onResume();
		soundNavigation.onResume();
		stepDetector.onResume();
		updateArrowImageView();

	}

	@Override
	public void onPause(){
		super.onPause();
		finished = true;
		cameraPreview.onPause();
		compassSensor.onStopOrPause();
		locationScanner.onStopOrPause();
		soundNavigation.onPause();
		stepDetector.onPause();
	}

}
