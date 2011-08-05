/**
 * 
 */
package com.longevitysoft.android.rgbledsliders;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longevitysoft.android.rgbledsliders.ColorPickerView.OnColorChangedListener;

/**
 * @author fbeachler
 * 
 */
public class Slider extends Fragment {

	private int ledIndex;
	private OnColorChangedListener onChangeListener;

	/**
	 * @return the ledIndex
	 */
	public int getLedIndex() {
		return ledIndex;
	}

	/**
	 * @param ledIndex the ledIndex to set
	 */
	public void setLedIndex(int ledIndex) {
		this.ledIndex = ledIndex;
	}

	/**
	 * @return the onChangeListener
	 */
	public OnColorChangedListener getOnChangeListener() {
		return onChangeListener;
	}

	/**
	 * @param onChangeListener
	 *            the onChangeListener to set
	 */
	public void setOnChangeListener(OnColorChangedListener onChangeListener) {
		this.onChangeListener = onChangeListener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		ColorPickerView ret = new ColorPickerView(this.getActivity().getBaseContext(),
				onChangeListener, 0xFF0000);
		ret.setLedIndex(getLedIndex());
		// View ret = inflater.inflate(R.layout.rgb_slider, container, false);
		return ret;

	}

}
