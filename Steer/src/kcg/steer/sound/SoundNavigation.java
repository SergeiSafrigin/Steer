package kcg.steer.sound;

import java.util.Vector;

import kcg.steer.compass.Compass;
import kcg.steer.logic.Data;
import kcg.steer.logic.Point3d;
import kcg.steer.logic.StaticValues;
import android.R;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundNavigation {
	private static final int FINISH_LINE_DISTANCE = 3;
	private static final int FREQUENCY = 1;
	private static final int NORMAL_SOUND_INTERVAL = 600;
	private static final int FINISH_SOUND_INTERVAL = 2000;

	private Context context;
	private boolean finished = false;
	
	private Vector<Integer> sound_resources;
	private int forwardSound, rightSound, leftSound, finishSound;
	private SoundPool soundPool;
	
	private Data data;
	
		
	private static enum RESOURCES {
		FORWARD(0), RIGHT(1),LEFT(2), FINISH(3);
		private int id;
		private RESOURCES(int id){
			this.id = id;
		}
		public int getId(){
			return id;
		}
	};

	public SoundNavigation(Context context, Data data, Vector<Integer> sound_resources){
		this.context = context;
		this.data = data;
		this.sound_resources = sound_resources;
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		loadSounds();
	}

	private void loadSounds(){
		forwardSound = soundPool.load(context, sound_resources.get(RESOURCES.FORWARD.getId()), 2);
		rightSound = soundPool.load(context, sound_resources.get(RESOURCES.RIGHT.getId()), 1);
		leftSound = soundPool.load(context, sound_resources.get(RESOURCES.LEFT.getId()), 1);
		finishSound = soundPool.load(context, sound_resources.get(RESOURCES.FINISH.getId()), 3);
	}

	private void startSoundNavigation(){
		new Thread(){
			@Override
			public void run(){
				Compass compass = data.getCompass();
				Compass direction = data.getDirection();
				double dYaw;

				while(!finished){
					if (data.getTargetDistance() > FINISH_LINE_DISTANCE){
						synchronized (compass) {
							synchronized (direction) {
								dYaw = Compass.getYawDiference(compass.getYaw(), direction.getYaw());
							}
						}
						
						if (Math.abs(dYaw) <= StaticValues.MAX_DELTA_YAW_FOR_FORWARD){
							soundPool.play(forwardSound, 1, 1, 0, 0, FREQUENCY);
						}
						
						try {
							Thread.sleep(NORMAL_SOUND_INTERVAL);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						continue;
					}
					
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

		new Thread(){
			@Override
			public void run(){
				Compass compass = data.getCompass();
				Compass direction = data.getDirection();
				double dYaw;
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				while(!finished){
					if (data.getTargetDistance() > FINISH_LINE_DISTANCE){
						synchronized (compass) {
							synchronized (direction) {
								dYaw = Compass.getYawDiference(compass.getYaw(), direction.getYaw());
							}
						}
						
						if (Math.abs(dYaw) > StaticValues.MAX_DELTA_YAW_FOR_FORWARD){
							if (dYaw > 0){
								soundPool.play(rightSound, 0, 1, 0, 0, FREQUENCY);
							} else {
								soundPool.play(leftSound, 1, 0, 0, 0, FREQUENCY);
							}
						}
						
						try {
							Thread.sleep(NORMAL_SOUND_INTERVAL);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						soundPool.play(finishSound, 1, 1, 0, 0, FREQUENCY);
						
						try {
							Thread.sleep(FINISH_SOUND_INTERVAL);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}	
			}	
		}.start();
	}

	public void onResume(){
		finished = false;
		startSoundNavigation();
	}

	public void onPause(){
		finished = true;
		if (soundPool != null)
			soundPool.release();
	}
}
