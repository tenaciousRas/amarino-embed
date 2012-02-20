/**
 * 
 */
package com.github.amarinoembed.helloamarinoworld;

import at.abraxas.amarino.intent.AmarinoServiceIntentConfig;

/**
 * @author fbeachler
 * 
 */
public class ServiceIntentConfig implements AmarinoServiceIntentConfig {
	public static final String DEFAULT_ACTION_CONNECT = "helloamarino.bt.intent.action.CONNECT";
	public static final String DEFAULT_ACTION_DISCONNECT = "helloamarino.bt.intent.action.DISCONNECT";
	public static final String DEFAULT_ACTION_SEND = "helloamarino.bt.intent.action.SEND";
	public static final String DEFAULT_ACTION_RECEIVED = "helloamarino.bt.intent.action.RECEIVED";
	public static final String DEFAULT_ACTION_CONNECTED = "helloamarino.bt.intent.action.CONNECTED";
	public static final String DEFAULT_ACTION_DISCONNECTED = "helloamarino.bt.intent.action.DISCONNECTED";
	public static final String DEFAULT_ACTION_CONNECTION_FAILED = "helloamarino.bt.intent.action.CONNECTION_FAILED";
	public static final String DEFAULT_ACTION_PAIRING_REQUESTED = "helloamarino.bt.intent.action.PAIRING_REQUESTED";
	public static final String DEFAULT_ACTION_GET_CONNECTED_DEVICES = "helloamarino.bt.intent.action.ACTION_GET_CONNECTED_DEVICES";
	public static final String DEFAULT_ACTION_CONNECTED_DEVICES = "helloamarino.bt.intent.action.ACTION_CONNECTED_DEVICES";
	public static final String DEFAULT_ACTION_ENABLE = "helloamarino.bt.intent.action.ENABLE";
	public static final String DEFAULT_ACTION_DISABLE = "helloamarino.bt.intent.action.DISABLE";
	public static final String DEFAULT_ACTION_DISABLE_ALL = "helloamarino.bt.intent.action.DISABLE_ALL";
	public static final String DEFAULT_ACTION_EDIT_PLUGIN = "helloamarino.bt.intent.action.EDIT_PLUGIN";

	@Override
	public String getIntentNameActionSend() {
		return DEFAULT_ACTION_SEND;
	}

	@Override
	public String getIntentNameActionReceived() {
		return DEFAULT_ACTION_RECEIVED;
	}

	@Override
	public String getIntentNameActionPairingRequested() {
		return DEFAULT_ACTION_PAIRING_REQUESTED;
	}

	@Override
	public String getIntentNameActionGetConnectedDevices() {
		return DEFAULT_ACTION_CONNECTED_DEVICES;
	}

	@Override
	public String getIntentNameActionEnable() {
		return DEFAULT_ACTION_ENABLE;
	}

	@Override
	public String getIntentNameActionEditPlugin() {
		return DEFAULT_ACTION_EDIT_PLUGIN;
	}

	@Override
	public String getIntentNameActionDisconnected() {
		return DEFAULT_ACTION_DISCONNECTED;
	}

	@Override
	public String getIntentNameActionDisconnect() {
		return DEFAULT_ACTION_DISCONNECT;
	}

	@Override
	public String getIntentNameActionDisableAll() {
		return DEFAULT_ACTION_DISABLE_ALL;
	}

	@Override
	public String getIntentNameActionDisable() {
		return DEFAULT_ACTION_DISABLE;
	}

	@Override
	public String getIntentNameActionConnectionFailed() {
		return DEFAULT_ACTION_CONNECTION_FAILED;
	}

	@Override
	public String getIntentNameActionConnectedDevices() {
		return DEFAULT_ACTION_CONNECTED_DEVICES;
	}

	@Override
	public String getIntentNameActionConnected() {
		return DEFAULT_ACTION_CONNECTED;
	}

	@Override
	public String getIntentNameActionConnect() {
		return DEFAULT_ACTION_CONNECT;
	}
}
