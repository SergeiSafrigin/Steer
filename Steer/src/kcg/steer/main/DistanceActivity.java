package kcg.steer.main;

import kcg.steer.R;
import kcg.steer.gui.CameraPreview;
import kcg.steer.gui.HeightBarsView;
import kcg.steer.logic.StaticValues;
import kcg.steer.logic.StaticValues.DISTANCE_TYPE;
import kcg.steer.logic.StaticValues.HEIGHT_TYPE;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DistanceActivity extends Activity implements HeightBarChangeListener{
	private Context context;
	private int height, width;
	private RelativeLayout mainLayout;
	private CameraPreview cameraPreview;
	private HeightBarsView heightBarsView;
	private TextView distanceTextView;
	private ImageView leftHeightBar1, leftHeightBar2, rightHeightBar1, rightHeightBar2;
	private Button saveButton, manualHeightButton, manualDistanceButton, houseButton, humanButton;
	private double targetDistance;
	private double targetHeight;
	
	private double lastManualHeight;
	private double lastManualDistance;
	
	private DISTANCE_TYPE distanceType;
	private HEIGHT_TYPE heightType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_distance);

		init(this);
	}

	private void init(Context context){
		this.context = context;
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		height = metrics.heightPixels;
		width = metrics.widthPixels;
		
		distanceTextView = (TextView) findViewById(R.id.DistanceCalculatedDistanceNumberTextView);
		
		saveButton = (Button) findViewById(R.id.distanceSaveButton);
		manualDistanceButton = (Button) findViewById(R.id.distanceManualDistanceButton);
		manualHeightButton = (Button) findViewById(R.id.distanceManualHeightButton);
		houseButton = (Button) findViewById(R.id.distanceHouseButton);
		humanButton = (Button) findViewById(R.id.distanceHumanButton);
		
		manualDistanceButton.setOnClickListener(manualDistanceButtonListener);
		manualHeightButton.setOnClickListener(manualHeightButtonListener);
		houseButton.setOnClickListener(houseButtonListener);
		humanButton.setOnClickListener(humanButtonListener);

		cameraPreview = new CameraPreview(context);
		mainLayout = (RelativeLayout) findViewById(R.id.distanceMainLayout);

		saveButton.setOnClickListener(saveButtonListener);

		mainLayout.addView(cameraPreview, 0);
		
		RelativeLayout.LayoutParams leftLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		leftLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		
		RelativeLayout.LayoutParams rightLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rightLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		
		leftHeightBar1 = new ImageView(context);
		leftHeightBar1.setImageResource(R.drawable.left_height_block);
		
		leftHeightBar2 = new ImageView(context);
		leftHeightBar2.setImageResource(R.drawable.left_height_block);
		
		rightHeightBar1 = new ImageView(context);
		rightHeightBar1.setImageResource(R.drawable.right_height_block);
		
		rightHeightBar2 = new ImageView(context);
		rightHeightBar2.setImageResource(R.drawable.right_height_block);
		
		mainLayout.addView(leftHeightBar1, leftLayoutParams);
		mainLayout.addView(leftHeightBar2, leftLayoutParams);
		mainLayout.addView(rightHeightBar1, rightLayoutParams);
		mainLayout.addView(rightHeightBar2, rightLayoutParams);
		
		heightBarsView = new HeightBarsView(context, this, leftHeightBar1, leftHeightBar2, rightHeightBar1, rightHeightBar2, height, width, cameraPreview.getVAngle());
		mainLayout.addView(heightBarsView, 1);
	}

	private OnClickListener manualDistanceButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			AlertDialog.Builder alert = new AlertDialog.Builder(context);

			alert.setTitle("Manual Distance");
			alert.setMessage("Enter Distance");

			// Set an EditText view to get user input 
			final EditText input = new EditText(context);
			input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
			if (lastManualDistance != 0)
				input.setText(String.format("%.2f", lastManualDistance));
			alert.setView(input);

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					distanceType = StaticValues.DISTANCE_TYPE.MANUAL;
					
					String value = input.getText().toString();
					if (!value.isEmpty() && value != "") {
						lastManualDistance = Integer.parseInt(value);
						targetDistance = lastManualDistance;
						calculateHeightAndDistance();
					}
				}
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});

			alert.show();
		}
	};
	
	private OnClickListener manualHeightButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			AlertDialog.Builder alert = new AlertDialog.Builder(context);

			alert.setTitle("Manual Height");
			alert.setMessage("Enter Height");

			// Set an EditText view to get user input 
			final EditText input = new EditText(context);
			input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
			if (lastManualHeight != 0)
				input.setText(String.format("%.2f", lastManualHeight));
			alert.setView(input);

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					distanceType = StaticValues.DISTANCE_TYPE.HEIGHT;
					heightType = StaticValues.HEIGHT_TYPE.MANUAL;
					
					String value = input.getText().toString();
					if (!value.isEmpty() && value != "") {
						lastManualHeight = Double.parseDouble(value);
						targetHeight = lastManualHeight;
						calculateHeightAndDistance();
					}
				}
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});

			alert.show();
		}
	};
	
	public OnClickListener houseButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			distanceType = StaticValues.DISTANCE_TYPE.HEIGHT;
			heightType = StaticValues.HEIGHT_TYPE.HOUSE;
			
			calculateHeightAndDistance();
		}
	};
	
	public OnClickListener humanButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			distanceType = StaticValues.DISTANCE_TYPE.HEIGHT;
			heightType = StaticValues.HEIGHT_TYPE.HUMAN;
			
			calculateHeightAndDistance();
		}
	};

	public OnClickListener saveButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			calculateHeightAndDistance();
			
			StaticValues.targetDistance = targetDistance;
			StaticValues.targetHeight = targetHeight;
			
			
			StaticValues.distanceType = distanceType;
			StaticValues.heightType = heightType;
			finish();
		}
	};
	
	public void calculateHeightAndDistance(){
		if (distanceType == StaticValues.DISTANCE_TYPE.HEIGHT){
			if (heightType == StaticValues.HEIGHT_TYPE.HOUSE){
				targetHeight = StaticValues.HOUSE_DISTANCE;
			} else if (heightType == StaticValues.HEIGHT_TYPE.HUMAN){
				targetHeight = StaticValues.HUMAN_DISTANCE;
			}
							
			float angle = heightBarsView.getSelectedAngle()/2;
			targetDistance = (targetHeight/2)/Math.tan(Math.toRadians(angle));
		}
		
		updateDistanceTextView();
	}
	
	public void updateDistanceTextView(){
		distanceTextView.setText(String.format("%.2f", targetDistance));
	}
	
	@Override
	public void onPause(){
		super.onPause();
		
		cameraPreview.onPause();
	}

	@Override
	public void onHeightBarChangeListener() {
		calculateHeightAndDistance();
		updateDistanceTextView();
	}
}
