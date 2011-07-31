package at.abraxas.amarino.db;

public interface DBConfig {
	public abstract String getDBName();

	public abstract int getDBVersion();

	public abstract void setDBName(String name);

	public abstract String getDevicesTableName();

	public abstract void setDevicesTableName(String name);

	public abstract String getEventsTableName();

	public abstract void setEventsTableName(String name);
}