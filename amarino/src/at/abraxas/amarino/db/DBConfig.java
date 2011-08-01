package at.abraxas.amarino.db;

/**
 * Provides a DB configuration for the {@link AmarinoService}. Using this you
 * can provide your own DB name and table names to record devices and events.
 * 
 * @author fbeachler
 */
public interface DBConfig {
	public abstract String getDBName();

	public abstract int getDBVersion();

	public abstract String getDevicesTableName();

	public abstract String getEventsTableName();
}