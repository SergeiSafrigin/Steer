package kcg.steer.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class DirectionView extends View{
	private Paint scopePaint;
	private Paint scopeOverlayPaint;
	
	public DirectionView(Context context) {
		super(context);
		
		init();
	}
	
	private void init(){
		scopePaint = new Paint();
		scopePaint.setColor(Color.WHITE);
		
		scopeOverlayPaint = new Paint();
		scopeOverlayPaint.setColor(Color.BLACK);
	}

	@Override
	public void draw(Canvas canvas) {
		drawScopeOverlay(canvas);
		drawScope(canvas);
	}
	
	private void drawScope(Canvas canvas){
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		
		int centerX = width/2;
		int centerY = height/2;
		int distance = 10; //distance between each line
		int i = centerX + distance, j = centerY + distance; // i: x, j:y for drawing the little lines on the scope
		int bigLineRate = 10; //at what rate should a big line spawn
		int lineSize = 5, bigLineSize = lineSize*4;
		int lines = 1; //number of lines drawn
			
		//width
		canvas.drawLine(0+5, centerY, width-5 , centerY, scopePaint);
		//height
		canvas.drawLine(centerX, 0, centerX, height, scopePaint);
		scopePaint.setStrokeWidth(3); //set the width of the little line to 1

		//draw horizontal little lines
		while (i < width){
			if(lines % bigLineRate == 0){
				canvas.drawLine(i, centerY-bigLineSize, i, centerY+bigLineSize , scopePaint);
				canvas.drawLine(centerX-(i-centerX), centerY-bigLineSize, centerX-(i-centerX), centerY+bigLineSize , scopePaint);
			}
			else{
				canvas.drawLine(i, centerY-lineSize, i, centerY+lineSize , scopePaint);
				canvas.drawLine(centerX-(i-centerX), centerY-lineSize, centerX-(i-centerX), centerY+lineSize , scopePaint);
			}
			lines++;
			i+= distance;
		}

		//draw vertical little lines
		lines = 1;
		while (j < width){
			if(lines % bigLineRate == 0){
				canvas.drawLine(centerX - bigLineSize, j, centerX + bigLineSize, j, scopePaint);
				canvas.drawLine(centerX - bigLineSize , centerY - (j - centerY), centerX + bigLineSize, centerY - (j - centerY), scopePaint);
			}
			else{
				canvas.drawLine(centerX - lineSize , j, centerX + lineSize, j, scopePaint);
				canvas.drawLine(centerX - lineSize , centerY - (j - centerY), centerX + lineSize, centerY - (j - centerY), scopePaint);
			}
			lines++;
			j+= distance;
		}

		//circle
		scopePaint.setStyle(Paint.Style.STROKE); //set the circle style for not filled
		canvas.drawCircle(centerX+1, centerY, (distance*2)+1, scopePaint); //draw a small circle at the middle

		scopePaint.setStrokeWidth(scopePaint.getStrokeWidth()+1); //set the width of the circle
		canvas.drawCircle(centerX, centerY, centerX-95, scopePaint); //draw the big circle
	}
	
	private void drawScopeOverlay(Canvas canvas){
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		
		int centerX = width/2;
		int centerY = height/2;
		int distance = 10; //distance between each line
		int i = centerX + distance, j = centerY + distance; // i: x, j:y for drawing the little lines on the scope
		int bigLineRate = 10; //at what rate should a big line spawn
		int lineSize = 5, bigLineSize = lineSize*4;
		int lines = 1; //number of lines drawn
			
		//width
		canvas.drawLine(0+5, centerY, width-5 , centerY, scopeOverlayPaint);
		//height
		canvas.drawLine(centerX, 0, centerX, height, scopeOverlayPaint);
		scopeOverlayPaint.setStrokeWidth(8); //set the width of the little line to 1

		//draw horizontal little lines
		while (i < width){
			if(lines % bigLineRate == 0){
				canvas.drawLine(i, centerY-bigLineSize, i, centerY+bigLineSize , scopeOverlayPaint);
				canvas.drawLine(centerX-(i-centerX), centerY-bigLineSize, centerX-(i-centerX), centerY+bigLineSize , scopeOverlayPaint);
			}
			else{
				canvas.drawLine(i, centerY-lineSize, i, centerY+lineSize , scopeOverlayPaint);
				canvas.drawLine(centerX-(i-centerX), centerY-lineSize, centerX-(i-centerX), centerY+lineSize , scopeOverlayPaint);
			}
			lines++;
			i+= distance;
		}

		//draw vertical little lines
		lines = 1;
		while (j < width){
			if(lines % bigLineRate == 0){
				canvas.drawLine(centerX - bigLineSize, j, centerX + bigLineSize, j, scopeOverlayPaint);
				canvas.drawLine(centerX - bigLineSize , centerY - (j - centerY), centerX + bigLineSize, centerY - (j - centerY), scopeOverlayPaint);
			}
			else{
				canvas.drawLine(centerX - lineSize , j, centerX + lineSize, j, scopeOverlayPaint);
				canvas.drawLine(centerX - lineSize , centerY - (j - centerY), centerX + lineSize, centerY - (j - centerY), scopeOverlayPaint);
			}
			lines++;
			j+= distance;
		}

		//circle
		scopeOverlayPaint.setStyle(Paint.Style.STROKE); //set the circle style for not filled
		canvas.drawCircle(centerX+1, centerY, (distance*2)+1, scopeOverlayPaint); //draw a small circle at the middle

		scopeOverlayPaint.setStrokeWidth(scopeOverlayPaint.getStrokeWidth()+1); //set the width of the circle
		canvas.drawCircle(centerX, centerY, centerX-95, scopeOverlayPaint); //draw the big circle
	}

}
