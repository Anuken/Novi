package io.anuke.novi.modules;

import com.badlogic.gdx.utils.Array;

import io.anuke.novi.Novi;
import io.anuke.novi.entities.player.Player;
import io.anuke.novi.ui.Marker;
import io.anuke.ucore.modules.Module;

//TODO remove this pointless module, why the heck does it exist?
public class ClientData extends Module<Novi>{
	public Player player;
	public Array<Marker> map = new Array<Marker>();

	public ClientData(){
		player = new Player();
		player.set(World.size/2, World.size/2);
		player.client = true;
	}
}
