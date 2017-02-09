package io.anuke.novi.entities.enemies;

import io.anuke.novi.world.Material;

public class GunBase extends Base{
	
	@Override
	void generateBlocks(){
		for(int x = 0;x < size;x ++){
			for(int y = 0;y < size;y ++){
				blocks[x][y].setMaterial(Material.ironblock);
				
				blocks[size/2][0].setMaterial(Material.turret);
				blocks[size/2][size-1].setMaterial(Material.turret);
				blocks[0][size/2-1].setMaterial(Material.turret);
				blocks[size-1][size/2-1].setMaterial(Material.turret);
				
				blocks[size-1][size-1].setMaterial(Material.bigturret);
				blocks[0][size-1].setMaterial(Material.bigturret);
				blocks[size-1][0].setMaterial(Material.bigturret);
				blocks[0][0].setMaterial(Material.bigturret);
				
				blocks[size/2][size/2-1].setMaterial(Material.dronemaker);
			}
		}
	}
}
