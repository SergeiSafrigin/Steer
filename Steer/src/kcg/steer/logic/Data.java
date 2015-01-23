package kcg.steer.logic;

import kcg.steer.compass.Compass;

public class Data {
	private Point3d location;
	private Point3d targetLocation;
	private Compass compass;
	private Compass direction;
	private double targetDistance;
	
	public Data(){
		location = new Point3d();
		targetLocation = new Point3d();
		compass = new Compass();
	}
	
	public synchronized Point3d getLocation() {
		return location;
	}
	
	public synchronized Point3d getTargetLocation() {
		return targetLocation;
	}

	public synchronized Compass getCompass() {
		return compass;
	}

	public synchronized Compass getDirection() {
		return direction;
	}

	public synchronized void setDirection(Compass direction) {
		this.direction = direction;
	}

	public synchronized double getTargetDistance() {
		return targetDistance;
	}

	public synchronized void setTargetDistance(double targetDistance) {
		this.targetDistance = targetDistance;
	}
}
