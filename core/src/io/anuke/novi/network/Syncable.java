package io.anuke.novi.network;

import java.lang.annotation.*;

public interface Syncable{
	public SyncBuffer writeSync();
	public void readSync(SyncBuffer buffer);
	
	@Retention(RetentionPolicy.RUNTIME)
	@interface GlobalSyncable{}
}
