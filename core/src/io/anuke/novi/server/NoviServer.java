package io.anuke.novi.server;

import java.util.HashMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

import io.anuke.novi.Novi;
import io.anuke.novi.entities.Entity;
import io.anuke.novi.entities.Player;
import io.anuke.novi.modules.Network;
import io.anuke.novi.network.Registrator;
import io.anuke.novi.network.packets.*;
import io.anuke.novi.systems.CollisionSystem;
import io.anuke.novi.systems.SpatialSystem;
import io.anuke.novi.systems.SyncSystem;
import io.anuke.novi.utils.Loggy;

public class NoviServer{
	public static boolean active;
	public static final int port = 7576;
	public Server server;
	public HashMap<Integer, Long> players = new HashMap<Integer, Long>(); //used for getting entities from connections
	public NoviUpdater updater; //this runs and updates the game objects
	
	void createSystems(){
		Entity.addSystem(new SpatialSystem());
		Entity.addSystem(new CollisionSystem());
		Entity.addSystem(new SyncSystem());
	}
	
	void createServer(){
		createSystems();
		Entity.server = this;
		addEntities();
		try{
			server = new Server(16384 * 256, 16384 * 256);
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
						data.playerid = player.GetID();
						data.entities = Entity.entities;
						connection.sendTCP(data);
						server.sendToAllExceptTCP(connection.getID(), player.addSelf());
						players.put(connection.getID(), player.GetID());
						Novi.log("player id: " + player.GetID() + " connection id: " + connection.getID());
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
					player.setPosition(position.x, position.y);
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
		return (Player)Entity.getEntity(players.get(cid));
	}

	public void removeEntity(Entity entity){
		EntityRemovePacket remove = new EntityRemovePacket();
		remove.id = entity.GetID();
		server.sendToAllTCP(remove);
		entity.removeSelf();
	}

	public void removeEntity(long entityid){
		EntityRemovePacket remove = new EntityRemovePacket();
		remove.id = entityid;
		server.sendToAllTCP(remove);
		Entity.entities.remove(entityid);
	}

	public static void main(String[] args){
		active = true;
		new NoviServer().createServer();
	}

	void doLogging(){
		Log.DEBUG();
		Log.setLogger(new Loggy());
	}

	private void addEntities(){
		//new Base().setPosition(10, 100).AddSelf();
		for(int i = 1;i < 40;i ++){
			//new GunBase().setPosition(100+ MathUtils.random(World.worldSize-100), 100 + MathUtils.random(World.worldSize-100)).addSelf();
			//new ShipBase().setPosition(100+ MathUtils.random(World.worldSize-100), 100 + MathUtils.random(World.worldSize-100)).addSelf();
			
		}
	}
}
