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

public class GraphView extends Visualizer {
	
	private static final String TAG = "GraphView";

	private Bitmap  mBitmap;
    private Canvas  mCanvas = new Canvas();
    
	private float   mSpeed = 1f;
	private float   mLastX;
    private float[] mLastValue = new float[10]; // 10 should be more than enough
    private int[]   mColor = new int[4];
   
    public GraphView(Context context) {
        super(context);
        init();
    }
    
    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init(){
    	mColor[0] = Color.argb(255, 100, 255, 100); // g
    	mColor[1] = Color.argb(255, 255, 255, 100); // y
    	mColor[2] = Color.argb(255, 255, 100, 100); // r
    	mColor[3] = Color.argb(255, 100, 255, 255); // c
    	
    	mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }
    

    public void setData(float value){
    	addDataPoint(value, mColor[0], mLastValue[0], 0);
    	invalidate();
    }
    
    public void setData(float[] values){
    	final int length = values.length;
    	try {
	    	for (int i=0;i<length;i++){
	    		addDataPoint(values[i], mColor[i%4], mLastValue[i], i);
	    	}
    	} catch (ArrayIndexOutOfBoundsException e){ 
    		/* mLastValue might run into this in extreme situations */
    		// but then we just do not want to support more than 10 values in our little graph
    		Logger.d(TAG, "Too many data points for our little graph");
    	}
    	invalidate();
    }
    
    private void addDataPoint(float value, final int color, final float lastValue, final int pos){
    	value += minValue;
    	
        final Paint paint = mPaint;
        float newX = mLastX + mSpeed;
        final float v = mYOffset + value * mScaleY;
        
        paint.setColor(color);
        mCanvas.drawLine(mLastX, lastValue, newX, v, paint);
        mLastValue[pos] = v;
        if (pos == 0)
        	mLastX += mSpeed;
        
		
    }
    
   
    public void setSpeed(float speed){
    	mSpeed = speed;
    }
    
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	super.onSizeChanged(w, h, oldw, oldh);
    	// TODO when screen size changes sometimes w or h == 0 -> Exception
    	Logger.d(TAG, "w: " + w + " h: " + h);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        mCanvas.setBitmap(mBitmap);
        mCanvas.drawColor(0xFF111111);
        mLastX = mWidth;
        
        // set origin to zero
        for (int i=0;i<mLastValue.length;i++)
    		mLastValue[i] = minValue;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        synchronized (this) {
            if (mBitmap != null) {
            	final Paint paint = mPaint;
            	final Canvas cavas = mCanvas;
            	
                if (mLastX >= mWidth) {
                	mLastX = 0;
                    cavas.drawColor(0xFF111111);

                    int x = 20;
                    paint.setColor(0x33DDFFDD);
                    while (x < mWidth){
                    	cavas.drawLine(x, mYOffset, x, 0, paint);
                    	x+=20;
                    }
                    
                    final float v = mYOffset + minValue * mScaleY;
                    // draw the zero line
                    paint.setColor(0xFF779977);
                    cavas.drawLine(0, v, mWidth, v, paint);
                    
                }
                paint.setColor(0xaa996666);
                cavas.drawText(min, 1, mYOffset-1, paint);
            	cavas.drawText(max, 1, textHeight-1, paint);
            	
                canvas.drawBitmap(mBitmap, 0, 0, null);
            }
        } 
    }
    
    
    
    
}
