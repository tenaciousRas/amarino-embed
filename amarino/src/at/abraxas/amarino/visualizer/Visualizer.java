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
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public abstract class Visualizer extends View {
	
	Paint   mPaint = new Paint();
	float   maxValue = 1024f;
    float	minValue = 0f;
    float   mScaleX;
    float   mScaleY;
    float   mYOffset;
    float   mWidth;
    String 	min, max;
    float   textWidth = 0;
	float	textHeight = 0;
	public Visualizer(Context context) {
        super(context);
    }
    
    public Visualizer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
	
	public abstract void setData(float value);
	public abstract void setData(float[] values);
	
	
	public void setBoundaries(float min, float max){
		this.min = String.valueOf(min);
		this.max = String.valueOf(max);
		minValue = -min;
    	maxValue = max - min;
    	mScaleY = - (mYOffset * (1.0f / maxValue));
    	mScaleX = (mWidth * (1.0f / maxValue));
    	
    	float[] sizes = new float[this.max.length()];
    	textHeight = mPaint.getTextSize();
    	mPaint.getTextWidths(this.max, sizes);
    	for (float f : sizes)
    		textWidth += f;
	}
	
	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mYOffset = h;
		mWidth = w;
		
        mScaleY = - (h * (1.0f / maxValue));
        mScaleX = (w * (1.0f / maxValue));

        super.onSizeChanged(w, h, oldw, oldh);
	}
	
	
	/* ========= SIMPLE CAST FUNCTIONS ============ */

	public void setData(int value){
		setData((float)value);
    }
    
    public void setData(double value){
    	setData((float)value);
    }
    
    public void setData(short value){
    	setData((float)value);
    }
    
    public void setData(long value){
    	setData((float)value);
    }
    
    public void setData(byte value){
    	setData((float)value);
    }
    
    public void setData(boolean value){
    	if (value)
    		setData(1f);
    	else
    		setData(0f);
    }
    
    
	
	public void setData(int[] values){
    	final int length = values.length;
    	float[] arr = new float[length];
    	for (int i=0;i<length;i++)
    		arr[i] = values[i];
    	setData(arr);
    }
    
    public void setData(double[] values){
    	final int length = values.length;
    	float[] arr = new float[length];
    	for (int i=0;i<length;i++)
    		arr[i] = (float)values[i];
    	setData(arr);
    }
    
    public void setData(short[] values){
    	final int length = values.length;
    	float[] arr = new float[length];
    	for (int i=0;i<length;i++)
    		arr[i] = values[i];
    	setData(arr);
    }
    
    public void setData(long[] values){
    	final int length = values.length;
    	float[] arr = new float[length];
    	for (int i=0;i<length;i++)
    		arr[i] = values[i];
    	setData(arr);
    }
    
    public void setData(byte[] values){
    	final int length = values.length;
    	float[] arr = new float[length];
    	for (int i=0;i<length;i++)
    		arr[i] = values[i];
    	setData(arr);
    }
    
    public void setData(boolean[] values){
    	final int length = values.length;
    	float[] arr = new float[length];
    	for (int i=0;i<length;i++)
    		arr[i] = values[i] ? 1 : 0;
    	setData(arr);
    }
    
    public void setData(String[] values){
    	final int length = values.length;
    	float[] arr = new float[length];
    	for (int i=0;i<length;i++)
    		arr[i] = Float.valueOf(values[i]);
    	setData(arr);
    }
	

}
