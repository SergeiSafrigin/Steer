package kcg.steer.gui;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
	private static final String TAG = "Preview";
	private SurfaceHolder holder;
	private Camera camera;
	public static double vCameraAngle;
	
	@SuppressWarnings("deprecation")
	public CameraPreview(Context context) {
		super(context);
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		this.holder = getHolder(); //gets a holder pointer from parent (surfaceView)
		this.holder.addCallback(this); //Install a SurfaceHolder.Callback so we get notified when the underlying surface is created, changed or destroyed

		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where
		// to draw.
		this.camera = Camera.open();
		
		try {
			camera.setDisplayOrientation(90);
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			camera.release();
			camera = null;
			Toast.makeText(this.getContext(), "Error "+e.toString(), Toast.LENGTH_LONG).show();
		}
						
	}


	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		//Start the preview after we get and set the best size for the preview
		Camera.Parameters parameters = camera.getParameters();
		List<Size> sizes = parameters.getSupportedPreviewSizes();
		Size optimalSize = getOptimalPreviewSize(sizes, w, h);
		parameters.setPreviewSize(optimalSize.width, optimalSize.height);
		camera.setParameters(parameters);
		camera.startPreview();
		
		Camera.Parameters p = camera.getParameters();
		int zoom = p.getZoomRatios().get(p.getZoom()).intValue();
		Camera.Size sz = p.getPreviewSize();
		double aspect = (double) sz.width / (double) sz.height;
		double thetaV = Math.toRadians(p.getVerticalViewAngle());
		double thetaH = 2d * Math.atan(aspect * Math.tan(thetaV / 2));
		thetaV = 2d * Math.atan(100d * Math.tan(thetaV / 2d) / zoom);
		thetaH = 2d * Math.atan(100d * Math.tan(thetaH / 2d) / zoom);
		
		
		vCameraAngle = Math.toDegrees(thetaH);
	}


	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's very
		// important to release it when the activity is paused.
		if(camera != null){
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.05;
		double targetRatio = (double) w / h;
		if (sizes == null) return null;
		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;
		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}
	
	public int closest(List<Camera.Size> sizes, int width, int height) {
		int best = -1;
		int bestScore = Integer.MAX_VALUE;

		for (int i = 0; i < sizes.size(); i++) {
			Camera.Size s = sizes.get(i);

			int dx = s.width - width;
			int dy = s.height - height;

			int score = dx * dx + dy * dy;
			if (score < bestScore) {
				best = i;
				bestScore = score;
			}
		}

		return best;
	}
	
	public double getVAngle(){
		return vCameraAngle;
	}

	public void resumeCamera(){
		surfaceCreated(getHolder());
	}
	
	public void onPause(){
		surfaceDestroyed(this.holder);
	}
}
