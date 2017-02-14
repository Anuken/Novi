package io.anuke.novi.entities.base;

import io.anuke.novi.Novi;
import io.anuke.novi.entities.basic.Bullet;
import io.anuke.novi.items.ProjectileType;
import io.anuke.novi.world.Material;

public class BaseTurret extends BaseBlock{

	public BaseTurret(Base base, Material material, int blockx, int blocky) {
		super(base, material, blockx, blocky);
	}

	@Override
	public void behaviorUpdate(){
		if(target == null)
			return;

		rotation = autoPredictTargetAngle(x, y, 4f) + 90;
		reload += Novi.delta();

		if(reload >= 100){
			new Bullet(ProjectileType.redbullet, rotation + 90).setShooter(base).set(x, y).translate(3, 5).add().send();
			new Bullet(ProjectileType.redbullet, rotation + 90).setShooter(base).set(x, y).translate(-3, 5).add().send();

			reload = 0;
		}
	}
}
