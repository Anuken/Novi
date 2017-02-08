package io.anuke.novi.network;

/**Default entity event class.*/
public class EntityEvent{
	public long id;
	public Object[] data;
	
	public EntityEvent(long id, Object[] data){
		this.id = id;
		this.data = data;
	}
	
	private EntityEvent(){
		
	}
}
