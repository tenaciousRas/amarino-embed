package at.abraxas.amarino.db;

/**
 * @author Free Beachler
 */
public class DefaultDBConfig implements DBConfig {

	public static final int DATABASE_VERSION = 3;
	public static final String DATABASE_NAME = "amarino_3.db";
	public static final String DEVICES_TABLE_NAME = "devices_tbl";
	public static final String EVENTS_TABLE_NAME = "events_tbl";

	@Override
	public String getDBName() {
		return DATABASE_NAME;
	}

	@Override
	public int getDBVersion() {
		return DATABASE_VERSION;
	}

	@Override
	public String getDevicesTableName() {
		return DEVICES_TABLE_NAME;
	}

	@Override
	public String getEventsTableName() {
		return EVENTS_TABLE_NAME;
	}

}