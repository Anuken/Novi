package io.anuke.novi.server;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import io.anuke.novi.Novi;
import io.anuke.novi.entities.Entities;
import io.anuke.novi.entities.Entity;
import io.anuke.novi.entities.base.Player;
import io.anuke.novi.entities.enemies.GunBase;
import io.anuke.novi.modules.Network;
import io.anuke.novi.modules.World;
import io.anuke.novi.network.Registrator;
import io.anuke.novi.network.packets.*;
import io.anuke.novi.systems.CollisionSystem;
import io.anuke.novi.systems.SpatialSystem;
import io.anuke.novi.systems.SyncSystem;

public class NoviServer{
	private static NoviServer instance;
	public static final int port = 7576;
	public Server server;
	public HashMap<Integer, Long> players = new HashMap<Integer, Long>(); //used for getting entities from connections
	public NoviUpdater updater; //this runs and updates the game objects
	
	void createSystems(){
		Entities.addSystem(new SpatialSystem());
		Entities.addSystem(new CollisionSystem());
		Entities.addSystem(new SyncSystem());
	}
	
	void createServer(){
		instance = this;
		
		createSystems();
		addEntities();
		
		try{
			server = new Server(16384, 16384 * (int)Math.pow(2, 10));
			Registrator.register(server.getKryo());
			server.addListener(new Listener.LagListener(Network.ping,Network.ping,new Listen(this)));
			server.start();
			server.bind(Network.port, Network.port);
			Novi.log("Server up.");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		createUpdater();
	}

	void createUpdater(){
		updater = new NoviUpdater(this);
		new Thread(new Runnable(){
			public void run(){
				updater.run();
			}
		}).start();
	}

	public float delta(){
		return updater.delta();
	}

	class Listen extends Listener{
		NoviServer novi;

		public Listen(NoviServer n){
			novi = n;
		}

		@Override
		public void disconnected(Connection connection){
			if( !players.containsKey(connection.getID())){
				Novi.log("An unknown player has disconnected.");
				return;
			}
			Novi.log(getPlayer(connection.getID()).name + " has disconnected.");
			removeEntity(getPlayer(connection.getID()));
			players.remove(connection.getID());
		}

		@Override
		public void received(Connection connection, Object object){
			try{
				if(object instanceof ConnectPacket){
					try{
						ConnectPacket connect = (ConnectPacket)object;
						Player player = new Player();
						player.connection = connection;
						player.name = connect.name;
						DataPacket data = new DataPacket();
						data.playerid = player.getID();
						data.entities = new ArrayList<Entity>(Entities.list());
						int size = connection.sendTCP(data);
						Novi.log("Data size: " + size);
						server.sendToAllExceptTCP(connection.getID(), player.add());
						players.put(connection.getID(), player.getID());
						Novi.log("player id: " + player.getID() + " connection id: " + connection.getID());
						Novi.log(player.name + " has joined.");
					}catch(Exception e){
						e.printStackTrace();
						Novi.log("Critical error: failed sending player!");
						System.exit(1);
					}
				}else if(object instanceof InputPacket){
					InputPacket packet = (InputPacket)object;
					getPlayer(connection.getID()).input.inputEvent(packet.input);
				}else if(object instanceof PositionPacket){
					PositionPacket position = (PositionPacket)object;
					Player player = getPlayer(connection.getID());
					player.set(position.x, position.y);
					player.rotation = position.rotation;
					player.velocity = position.velocity;
				}
			}catch(Exception e){
				e.printStackTrace();
				Novi.log("Packet recieve error!");
			}
		}
	}

	public Player getPlayer(int cid){
		return (Player)Entities.get(players.get(cid));
	}

	public void removeEntity(Entity entity){
		EntityRemovePacket remove = new EntityRemovePacket();
		remove.id = entity.getID();
		server.sendToAllTCP(remove);
		entity.remove();
	}

	public void removeEntity(long entityid){
		EntityRemovePacket remove = new EntityRemovePacket();
		remove.id = entityid;
		server.sendToAllTCP(remove);
		Entities.remove(entityid);
	}

	private void addEntities(){
		//new Base().setPosition(10, 100).AddSelf();
		for(int i = 0;i < 1;i ++){
			new GunBase().set(100+ MathUtils.random(World.worldSize-100), 100 + MathUtils.random(World.worldSize-100)).add();
			
		}
	}
	
	public static NoviServer instance(){
		return instance;
	}
	
	public static boolean active(){
		return instance != null;
	}
	
	public static void main(String[] args){
		new NoviServer().createServer();
	}
}
