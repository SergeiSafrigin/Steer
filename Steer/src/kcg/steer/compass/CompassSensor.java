package kcg.steer.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class CompassSensor {
	private static final String TAG = "Sensor Scanner";
	private Context context;
	private Compass compass;
	private SensorManager sensorManager;
	private Sensor sensor;
	private float[] rotationMatrix;
	private float[] angles;

	public CompassSensor(Context context, Compass compass){
		this.context = context;
		this.compass = compass;

		rotationMatrix = new float[16];
		angles = new float[3];
	}

	public void startSensorManager(){
		sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		if (sensor != null)
			sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
	}

	SensorEventListener sensorListener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
				SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
				SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, rotationMatrix);
				SensorManager.getOrientation(rotationMatrix, angles);
				
				synchronized (compass) {
					compass.set(calcYaw(), calcPitch(), calcRoll());
				}
			}
		}
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	};

	public float calcYaw(){
		float ans = (angles[0] * (180 / 3.14159265f)) - 90;
		if(ans<0){
			ans+=360;
		}
		return ans;
	}

	public float calcPitch(){
		return angles[2] * (180 / 3.14159265f);
	}

	public float calcRoll(){
		return angles[1] * (180 / 3.14159265f);
	}

	public void onResume(){
		startSensorManager();
	}

	public void onStopOrPause() {
		if (sensorManager != null){
			sensorManager.unregisterListener(sensorListener);
			sensorManager = null;
		}
	}
}
