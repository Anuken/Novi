package io.anuke.novi.entities.base;

import io.anuke.novi.world.Material;

public class ShipBase extends Base{
	float speed = 0.6f;
	
	{
		texture = "titanship";
		velocity.set(0, 0.1f);
	}
	
	@Override
	public void generateBlocks(){
		int o = 3;
		blocks[o-1][o].setMaterial(Material.turret);
		blocks[size - o+1][o].setMaterial(Material.turret);
		blocks[o][size - o-1].setMaterial(Material.turret);
		blocks[size - o][size - o-1].setMaterial(Material.turret);
		blocks[size / 2][2].setMaterial(Material.dronemaker);
		blocks[size / 2][size / 2-1].setMaterial(Material.bigturret);
	}
	
	@Override
	public void behaviorUpdate(){
		super.behaviorUpdate();
		
		velocity.setLength(speed);
		velocity.rotate(0.1f);
		rotation = velocity.angle()-90;
	}
}
