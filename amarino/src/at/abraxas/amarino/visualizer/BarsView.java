/*
  Amarino - A prototyping software toolkit for Android and Arduino
  Copyright (c) 2010 Bonifaz Kaufmann.  All right reserved.
  
  This application and its library is free software; you can redistribute
  it and/or modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 3 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
package at.abraxas.amarino.visualizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import at.abraxas.amarino.log.Logger;

public class BarsView extends Visualizer {
	
	private static final String TAG = "BarsView";
	private static final int NUM_BARS = 3;
	
	private int activeBars = 0;
	
	private Bitmap  mBitmap;
    private Canvas  mCanvas = new Canvas();
    private int[]   mColor = new int[3];
    private float 	barHeight;
    
    
	public BarsView(Context context) {
		super(context);
		init();
	}
	
	public BarsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init(){
		mColor[0] = Color.argb(255, 0, 200, 0); // g
    	mColor[1] = Color.argb(255, 200, 200, 0); // y
    	mColor[2] = Color.argb(255, 200, 0, 0); // r
    	
		activeBars = 1;
	}
	
	
	@Override
	public void setBoundaries(float min, float max) {
		super.setBoundaries(min, max);
		
	}

	@Override
	public void setData(float value) {
		mCanvas.drawColor(0xFF111111);
		updateBar(value, 0);
		invalidate();
	}

	@Override
	public void setData(float[] values) {
		mCanvas.drawColor(0xFF111111);
		int length = values.length;
		if (length > activeBars){
			activeBars = length;
		}
		for (int i=0; i<length; i++){
			updateBar(values[i], i);
		}
		invalidate();
	}
	
	private void updateBar(float value, final int pos){
		
		value += minValue;
		final Paint paint = mPaint;
        final float v = value * mScaleX;
        paint.setColor(mColor[pos]);

        mCanvas.drawRect(minValue * mScaleX, barHeight*pos, v, barHeight*(pos+1), paint);
        
	}

	
	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		barHeight = mYOffset / NUM_BARS;
		
    	// TODO when screen size changes sometimes w or h == 0 -> Exception
    	Logger.d(TAG, "w: " + w + " h: " + h);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        mCanvas.setBitmap(mBitmap);
        mCanvas.drawColor(0xFF111111);
        
    }

    @Override
    protected void onDraw(Canvas canvas) {
        synchronized (this) {
            if (mBitmap != null) {
            	// draw bars outline
            	final float v1 = minValue * mScaleX;
            	final Canvas cavas = mCanvas;
            	final Paint paint = mPaint;
            	
            	paint.setColor(0x44996666);
            	
            	for (int i=0; i<NUM_BARS;i++){
            		cavas.drawLine(0, barHeight*i, mWidth, barHeight*i, paint);
            	}
            	
            	paint.setColor(0xaa996666);
            	cavas.drawLine(v1, mYOffset, v1, 0, paint);
            	cavas.drawText(min, 1, mYOffset-1, paint);
            	cavas.drawText(max, mWidth-textWidth-1, mYOffset-1, paint);
            	
                canvas.drawBitmap(mBitmap, 0, 0, null);
            }
        } 
    }

	
	
	

}
