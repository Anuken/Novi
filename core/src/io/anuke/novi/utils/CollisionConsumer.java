package io.anuke.novi.utils;

import io.anuke.novi.entities.SolidEntity;

@FunctionalInterface
public interface CollisionConsumer{
	public void accept(SolidEntity entity, float x, float y);
}
