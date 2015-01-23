package kcg.steer.gui;

import kcg.steer.main.HeightBarChangeListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class HeightBarsView extends View{
	private HeightBarChangeListener heightBarChangeListener;
	private ImageView leftHeightBar1, leftHeightBar2, rightHeightBar1, rightHeightBar2;
	private int height, width;
	private Paint linePaint;
	private Paint linePaintStroke;
	private double vCameraAngle;

	public HeightBarsView(Context context, HeightBarChangeListener heightBarChangeListener, ImageView leftHeightBar1, 
			ImageView leftHeightBar2, ImageView rightHeightBar1, ImageView rightHeightBar2,
			int height, int width, double vCameraAngle) {
		super(context);
		
		this.heightBarChangeListener = heightBarChangeListener;
		
		this.leftHeightBar1 = leftHeightBar1;
		this.leftHeightBar2 = leftHeightBar2;
		this.rightHeightBar1 = rightHeightBar1;
		this.rightHeightBar2 = rightHeightBar2;

		this.height = height;
		this.width = width;
		
		this.vCameraAngle = vCameraAngle;

		init();
	}

	private void init(){
		leftHeightBar1.setY(height * (float)(0.2));
		leftHeightBar2.setY(height - (float)(height * (0.2)));
		rightHeightBar1.setY(height * (float)(0.2));
		rightHeightBar2.setY(height - (float)(height * (0.2)));

		leftHeightBar1.setOnTouchListener(heightBarTouchListener);
		leftHeightBar2.setOnTouchListener(heightBarTouchListener);
		rightHeightBar1.setOnTouchListener(heightBarTouchListener);
		rightHeightBar2.setOnTouchListener(heightBarTouchListener);

		linePaint = new Paint();
		linePaint.setColor(Color.BLACK);
		linePaint.setStrokeWidth(7);	

		linePaintStroke = new Paint();
		linePaintStroke.setColor(Color.WHITE);
		linePaintStroke.setStrokeWidth(11);
	}

	OnTouchListener heightBarTouchListener = new OnTouchListener() {
		@SuppressLint("ClickableViewAccessibility") @Override
		public boolean onTouch(View v, MotionEvent e) {
			float y = e.getY();

			switch (e.getAction()) {
			case MotionEvent.ACTION_MOVE:
				if (v.getY() + e.getY() < height - 65){
					if (v.getY() == leftHeightBar1.getY()){
						leftHeightBar1.setY(leftHeightBar1.getY() + y);
						rightHeightBar1.setY(rightHeightBar1.getY() + y);
					}
					if (v.getY() == leftHeightBar2.getY()){
						leftHeightBar2.setY(leftHeightBar2.getY() + y);
						rightHeightBar2.setY(rightHeightBar2.getY() + y);
					}			
					
					heightBarChangeListener.onHeightBarChangeListener();
					
					invalidate();
				}
				return true;

			case MotionEvent.ACTION_DOWN:
				return true;
			case MotionEvent.ACTION_UP:
				return true;
			}

			return false;
		}
	};

	@Override
	public void draw(Canvas canvas){
		canvas.drawLine(0, leftHeightBar1.getY()+27, width, leftHeightBar1.getY()+27, linePaintStroke);
		canvas.drawLine(0, leftHeightBar1.getY()+27, width, leftHeightBar1.getY()+27, linePaint);

		canvas.drawLine(0, leftHeightBar2.getY()+27, width, leftHeightBar2.getY()+27, linePaintStroke);
		canvas.drawLine(0, leftHeightBar2.getY()+27, width, leftHeightBar2.getY()+27, linePaint);
	}
	
	public float getSelectedAngle(){
		return (float)((vCameraAngle/height) * Math.abs((leftHeightBar1.getY()+27)-(leftHeightBar2.getY()+27)));
	}
}
