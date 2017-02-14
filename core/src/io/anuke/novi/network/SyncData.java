package io.anuke.novi.network;

/**This class has two uses: 1) for storing regular entity updates for things such as position in a WorldUpdatePacket,
 * and 2) acting as data individual entity events, sent directly to the client*/
public class SyncData{
	public long id;
	public Object[] objects;
	
	public SyncData(long id, Object... objects){
		this.id = id;
		this.objects = objects;
	}
	
	private SyncData(){}
	
	public <T> T get(int index){
		return (T)objects[index];
	}
}
