package kcg.steer.compass;


public class Compass {
	private double yaw, pitch, roll;
	
	public Compass(){
		yaw = 0;
		pitch = 0;
		roll = 0;
	}
	
	public Compass(double yaw, double pitch, double roll){
		set(yaw, pitch, roll);
	}
	
	public synchronized void set(double yaw, double pitch, double roll){
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
	}
	
	public synchronized double getYaw() {
		return yaw;
	}

	public synchronized void setYaw(double yaw) {
		this.yaw = yaw;
	}

	public synchronized double getPitch() {
		return pitch;
	}

	public synchronized void setPitch(double pitch) {
		this.pitch = pitch;
	}

	public synchronized double getRoll() {
		return roll;
	}

	public synchronized void setRoll(double roll) {
		this.roll = roll;
	}
	
	@Override
	public String toString(){
		return "yaw = "+yaw+", pitch = "+pitch+", roll = "+roll;
	}
	
	public static double getYawDiference(double yaw1, double yaw2){
		boolean negative = false;
		double diff = Math.abs(yaw1 - yaw2);
		if (diff < 360-diff){
			if (yaw1 < yaw2)
				negative = true;
		} else {
			if (yaw1 > yaw2)
				negative = true;
		}
		
		diff = Math.min(diff, 360-diff);
		if (negative)
			return -diff;
		
		return diff;
	}
}
