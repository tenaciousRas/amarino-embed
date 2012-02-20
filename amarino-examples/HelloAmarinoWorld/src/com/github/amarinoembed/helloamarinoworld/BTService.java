/**
 * 
 */
package com.github.amarinoembed.helloamarinoworld;

import at.abraxas.amarino.service.AmarinoService;

/**
 * @author fbeachler
 * 
 */
public class BTService extends AmarinoService {

	public static final String TAG = "BTService";

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.abraxas.amarino.service.AmarinoService#onCreate()
	 */
	@Override
	public void onCreate() {
		super.setIntentConfig(new ServiceIntentConfig());
		super.setNotifLaunchIntentClass(HelloAmarinoWorld.class);
		super.onCreate();
	}

}
