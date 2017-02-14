package io.anuke.novi.entities.base;

public class ShipBase extends Base{
	float speed = 0.6f;
	
	{
		texture = "titanship";
		velocity.set(0, 0.1f);
	}
	
	@Override
	public void generateBlocks(){
		
	}
	
	@Override
	public void behaviorUpdate(){
		super.behaviorUpdate();
		
		velocity.setLength(speed);
		velocity.rotate(0.1f);
		rotation = velocity.angle()-90;
	}
}
