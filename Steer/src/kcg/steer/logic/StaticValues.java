package kcg.steer.logic;

import kcg.steer.compass.Compass;

public class StaticValues {
	public static final int MAX_DELTA_YAW_FOR_FORWARD = 10;
	public static final int MAX_DELTA_YAW_FOR_FORWARD_LEFT_OR_RIGHT = 45;
	
	public static enum GPS_STATUS {FIX, OFF, SEARCHING};
	
	public static final float HUMAN_DISTANCE = 1.7f;
	public static final float HOUSE_DISTANCE = 10f;
	public static enum DISTANCE_TYPE {MANUAL, HEIGHT};
	public static enum HEIGHT_TYPE {MANUAL, HOUSE, HUMAN};
	public static DISTANCE_TYPE distanceType;
	public static HEIGHT_TYPE heightType;
	
//	public static boolean NETWORK_ENABLED = true;
	
	public static Compass direction;
	public static double targetDistance;
	public static double targetHeight;
}