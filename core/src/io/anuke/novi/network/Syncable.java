package io.anuke.novi.network;

public interface Syncable{
	public SyncData writeSync();
	public void readSync(SyncData buffer);
	public default boolean sync(){return true;};
	public default void onFinishSync(){};
}
