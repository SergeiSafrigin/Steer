package kcg.steer.logic;

public class Point3d {
	private double x,y,z;
	
	public Point3d(){
		x = 0;
		y = 0;
		z = 0;
	}
	
	public Point3d(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point3d(double x, double y){
		this.x = x;
		this.y = y;
		this.z = 0;
	}
	
	public Point3d(Point3d p){
		set(p);
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
	
	public synchronized void set(Point3d p){
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
	}
	
	public static double distance2d(Point3d p1, Point3d p2){
		return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
	}
	
	public static double distance3d(Point3d p1, Point3d p2){
		return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2) + Math.pow(p1.z - p2.z, 2));
	}
}
