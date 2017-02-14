package io.anuke.novi.network;

public class SyncData{
	public long id;
	public Object[] objects;
	
	/**Creates a dictionary of names and values, using an alternating pattern (name-value, name-value, etc)*/
	public SyncData(long id, Object... objects){
		this.id = id;
		
		this.objects = objects;
	}
	
	public <T> T get(int index){
		return (T)objects[index];
	}
	
	private SyncData(){}
}
