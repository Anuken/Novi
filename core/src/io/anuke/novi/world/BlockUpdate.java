package io.anuke.novi.world;

public class BlockUpdate{
	public Block block;

	public BlockUpdate(){

	}

	public BlockUpdate(Block block){
		this.block = block;
	}
	
	public void apply(Block[][] blocks){
		blocks[block.x][block.y] = block;
	}
}
