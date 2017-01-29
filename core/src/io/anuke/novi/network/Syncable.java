package io.anuke.novi.network;

import java.lang.annotation.*;

public interface Syncable{
	public SyncData writeSync();
	public void readSync(SyncData buffer);
	
	@Retention(RetentionPolicy.RUNTIME)
	@interface GlobalSyncable{}
}
