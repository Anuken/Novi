package io.anuke.novi.network;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface Syncable{
	public SyncData writeSync();
	public void readSync(SyncData buffer);
	
	/**All changes get synced to all players.*/
	@Retention(RetentionPolicy.RUNTIME)
	@interface GlobalSyncable{}
}
