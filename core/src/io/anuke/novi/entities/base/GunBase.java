package io.anuke.novi.entities.base;

import io.anuke.novi.world.Material;

public class GunBase extends Base{
	
	@Override
	void generateBlocks(){
		
		
		for(int x = 0;x < size;x ++){
			for(int y = 0;y < size;y ++){
				blocks[x][y].setMaterial(Material.ironblock);
				
				
				
				
				
				//if(Math.random() < 0.1) blocks[x][y].setMaterial(Material.bigturret);
				//if(Math.random() < 0.1) blocks[x][y].setMaterial(Material.turret);
			}
		}
		
		addEntity(new BaseTurret(), size/2, size/2);
		
		addEntity(new BaseTurret(), size/2-1, size/2);
	}
}
