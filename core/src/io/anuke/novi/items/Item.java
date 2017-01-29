package io.anuke.novi.items;

public abstract class Item{
	protected String name;
	protected int id;

	public Item(String itemName, int itemId){
		name = itemName;
		id = itemId;
	}

	public String getName(){
		return name;
	}

	public int getId(){
		return id;
	}
}
