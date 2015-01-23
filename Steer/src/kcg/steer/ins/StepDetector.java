package kcg.steer.ins;

import kcg.steer.compass.Compass;
import kcg.steer.logic.Data;
import kcg.steer.logic.Point3d;
import kcg.steer.main.DistanceLeftViewUpdater;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import core.ins.Ins;
import core.lights.LightsInfo;
import core.sensors.OnSensorEventListener;
import core.utils.Approx_location;

public class StepDetector implements OnSensorEventListener{
	private static final float STEP_LENGTH = 0.5f;
	
	private Data data;
	private Compass compass;
	private DistanceLeftViewUpdater distanceLeftViewUpdater;
	private Ins ins;
	
	private Handler GuiUpdater;
	
	public StepDetector(Context context, Data data, DistanceLeftViewUpdater distanceLeftViewUpdater){
		this.data = data;
		compass = data.getCompass();
		this.distanceLeftViewUpdater = distanceLeftViewUpdater;
		ins = new Ins(context);
		
		GuiUpdater = new Handler();
	}
	
	@Override
	public void onStepDetected(float azimuth) {
		double yaw;
		synchronized (compass) {
			yaw = compass.getYaw();
		}
		
		Point3d location = data.getLocation();
		Point3d targetLocation = data.getTargetLocation();
		
		double stepX = STEP_LENGTH * Math.sin(Math.toRadians(yaw));
		double stepY = STEP_LENGTH * Math.cos(Math.toRadians(yaw));
		
		synchronized (location) {
			location.setX(location.getX() + stepX);
			location.setY(location.getY() + stepY);
			
			synchronized (data) {
				data.setTargetDistance(Point3d.distance2d(data.getLocation(), data.getTargetLocation()));
			}
		}
		
		Compass direction = data.getDirection();
		double x;
		double y;
		
		synchronized (location) {
			synchronized (targetLocation) {
				x = targetLocation.getX() - location.getX();
				y = targetLocation.getY() - location.getY();					
			}
		}
		
		double targetAngle = Math.toDegrees(Math.atan2(y, x));
		targetAngle = (450 - targetAngle)%360;
				
		synchronized (direction) {
			direction.setYaw(targetAngle);
		}
		
		GuiUpdater.post(guiUpdateRunnable);
	}
	
	Runnable guiUpdateRunnable = new Runnable() {
		@Override
		public void run() {
			distanceLeftViewUpdater.updateDistaceLeftTextView();
		}
	};
	
	public void onResume(){
		ins.registerListener(this, null);
	}
	
	public void onPause(){
		ins.unregisterListener();
	}

	@Override public void onBTResult() {}
	@Override public void onFloorChanged(double arg0) {}
	@Override public void onGoogleGeofence(String arg0, int arg1) {}
	@Override public void onGoogleLocation(Location arg0) {}
	@Override public void onGooogleActivity(String arg0, int arg1) {}
	@Override public void onLightsEvent(LightsInfo arg0, core.lights.Point3d arg1) {}
	@Override public void onWiFiResult(Approx_location arg0) {}
}
