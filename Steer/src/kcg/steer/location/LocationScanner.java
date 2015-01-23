package kcg.steer.location;

import kcg.steer.compass.Compass;
import kcg.steer.gui.OnFixStatusChangedListener;
import kcg.steer.logic.Data;
import kcg.steer.logic.Point3d;
import kcg.steer.logic.StaticValues;
import kcg.steer.main.DistanceLeftViewUpdater;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.UTMRef;
import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationScanner {
	private Context context;
	private static final String TAG = "Location Scanner";
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
	private static final long MIN_SNR_FOR_FIX = 30;
	private LocationManager locationManager;
	private OnFixStatusChangedListener onFixStatusChangedListener;
	private DistanceLeftViewUpdater distanceLeftViewUpdater;
	private Point3d myLocation;
	private Data data;
	private long lastLocationTime;
	private boolean isGPSFIX = false;

	public LocationScanner(Context context, Data data, DistanceLeftViewUpdater distanceLeftViewUpdater){
		this.context = context;
		this.data = data;
		this.distanceLeftViewUpdater = distanceLeftViewUpdater;
	}

	public void startLocationManager(){	
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager != null){
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MINIMUM_TIME_BETWEEN_UPDATES,MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,locationListener);

			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1,1, locationListener);
			locationManager.addGpsStatusListener(gpsStatusListener);
			
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
				updateGpsStatusImageView(StaticValues.GPS_STATUS.SEARCHING);
		}
	}

	LocationListener locationListener = new LocationListener(){
		@Override public void onStatusChanged(String s, int i, Bundle b) {}
		@Override public void onProviderDisabled(String s) {
			onFixStatusChangedListener.onChanged(StaticValues.GPS_STATUS.OFF);
			isGPSFIX = false;
			updateGpsStatusImageView(StaticValues.GPS_STATUS.OFF);
		}
		@Override public void onProviderEnabled(String s) {
			onFixStatusChangedListener.onChanged(StaticValues.GPS_STATUS.SEARCHING);
			updateGpsStatusImageView(StaticValues.GPS_STATUS.SEARCHING);
		}

		@Override
		public void onLocationChanged(Location location) {
			if (location.getAccuracy() > 10)
				return;
			Log.e(TAG,"new Location accuracy = "+location.getAccuracy());
			if (!isGPSFIX){
				onFixStatusChangedListener.onChanged(StaticValues.GPS_STATUS.FIX);
				isGPSFIX = true;
			}

			lastLocationTime = System.currentTimeMillis();

			Point3d currTargetLocation = data.getTargetLocation();
			Point3d currLocation = data.getLocation();

			if (myLocation == null){
				Point3d newTargerLocation = new Point3d();
				Point3d newLocation = fromLatLonToUTM(location.getLatitude(), location.getLongitude(), location.getAltitude());

				synchronized (currLocation) {
					synchronized (currTargetLocation) {
						newTargerLocation.setX(newLocation.getX() + (currTargetLocation.getX() - currLocation.getX()));
						newTargerLocation.setY(newLocation.getY() + (currTargetLocation.getY() - currLocation.getY()));
						newTargerLocation.setZ(newLocation.getZ());

						currTargetLocation.set(newTargerLocation);
					}
				}


				myLocation = newLocation;
			} else
				myLocation = fromLatLonToUTM(location.getLatitude(), location.getLongitude(), location.getAltitude());

			synchronized (data.getLocation()) {
				data.getLocation().set(myLocation);

				synchronized (data) {
					data.setTargetDistance(Point3d.distance2d(data.getLocation(), data.getTargetLocation()));
				}
			}

			Compass direction = data.getDirection();
			double x;
			double y;

			synchronized (currLocation) {
				synchronized (currTargetLocation) {
					x = currTargetLocation.getX() - currLocation.getX();
					y = currTargetLocation.getY() - currLocation.getY();					
				}
			}

			double targetAngle = Math.toDegrees(Math.atan2(y, x));
			targetAngle = (450 - targetAngle)%360;

			synchronized (direction) {
				direction.setYaw(targetAngle);
			}

			distanceLeftViewUpdater.updateDistaceLeftTextView();
		}
	};


	private GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
		@Override
		public void onGpsStatusChanged(int event) {
			if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS && myLocation != null){
				Log.e(TAG,"time passed = "+((System.currentTimeMillis() - lastLocationTime) / 1000));
				if (isGPSFIX && ((System.currentTimeMillis() - lastLocationTime)/1000 < 15))
					return;

				GpsStatus status = locationManager.getGpsStatus(null);
				Iterable<GpsSatellite> gpsSatellites = status.getSatellites();

				float maxSnr = 0;
				for(GpsSatellite satellite: gpsSatellites){
					if (satellite.getSnr() > maxSnr)
						maxSnr = satellite.getSnr();
				}

				boolean gpsFixed;
				StaticValues.GPS_STATUS gps_status;

				Log.e(TAG,"max snr = "+maxSnr);

				if (maxSnr >= MIN_SNR_FOR_FIX && ((System.currentTimeMillis()) - lastLocationTime)/1000 < 20){
					gpsFixed = true;
					gps_status = StaticValues.GPS_STATUS.FIX;
				}
				else {
					gpsFixed = false;
					gps_status = StaticValues.GPS_STATUS.SEARCHING;
				}

				updateGpsStatusImageView(gps_status);

				isGPSFIX = gpsFixed;
			}
		}
	};

	private Point3d fromLatLonToUTM(double lat, double lon, double alt){
		LatLng coordinates = new LatLng(lat, lon);
		UTMRef utm = coordinates.toUTMRef();
		return new Point3d(utm.getEasting(), utm.getNorthing(), alt);
	}

	public void setOnFixStatusChangedListener(OnFixStatusChangedListener onFixStatusChangedListener){
		this.onFixStatusChangedListener = onFixStatusChangedListener;
	}

	private void updateGpsStatusImageView(StaticValues.GPS_STATUS status){
		if (onFixStatusChangedListener != null)
			onFixStatusChangedListener.onChanged(status);
	}

	public void onResume(){
		startLocationManager();
	}

	public void stopLocationManager(){
		if (locationManager == null)
			return;

		locationManager.removeUpdates(locationListener);
	}

	public void onStopOrPause(){
		stopLocationManager();
	}	
}
