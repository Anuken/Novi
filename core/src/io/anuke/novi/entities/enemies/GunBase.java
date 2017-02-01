package io.anuke.novi.entities.enemies;

import com.badlogic.gdx.math.Vector2;

import io.anuke.novi.world.Material;

public class GunBase extends Base{

	@Override
	void generateBlocks(){
		for(int x = 0;x < size;x ++){
			for(int y = 0;y < size;y ++){
				blocks[x][y].setMaterial(Material.ironblock);
				
				if(Vector2.dst(x, y, size/2f, size/2f) > size/2.2f) blocks[x][y].setMaterial(Material.turret);
				//if(Math.random() < 0.1) blocks[x][y].setMaterial(Material.bigturret);
				
				//if(Math.random() < 0.05) blocks[x][y].setMaterial(Material.dronemaker);
			}
		}
		
	}
}
