package com.draznel.bomberboy.net;

import static com.draznel.bomberboy.tiles.Tile.ID_SPAWN;
import static com.draznel.bomberboy.tiles.Tile.ID_STONE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.draznel.bomberboy.items.ItemID;
import com.draznel.bomberboy.packets.Packet;
import com.draznel.bomberboy.packets.Packet.PacketTypes;
import com.draznel.bomberboy.packets.Packet00Login;
import com.draznel.bomberboy.packets.Packet01Disconnect;
import com.draznel.bomberboy.packets.Packet02Move;
import com.draznel.bomberboy.packets.Packet03ChatMessage;
import com.draznel.bomberboy.packets.Packet04Ping;
import com.draznel.bomberboy.packets.Packet05ReadyState;
import com.draznel.bomberboy.packets.Packet06MapChosen;
import com.draznel.bomberboy.packets.Packet07Host;
import com.draznel.bomberboy.packets.Packet08GameStart;
import com.draznel.bomberboy.packets.Packet09TileChanged;
import com.draznel.bomberboy.packets.Packet10BombPlaced;
import com.draznel.bomberboy.packets.Packet11PlayerKilled;
import com.draznel.bomberboy.packets.Packet12Victory;
import com.draznel.bomberboy.packets.Packet13Items;
//import com.draznel.bomberboy.tiles.Tile;
import com.draznel.bomberboy.packets.Packet14ItemPickup;

public class GameServer extends Thread {

	private static final int DEFAULT_SERVER_PORT = 7331;
	
	public DatagramSocket socket;
	List<PlayerClient> connectedPlayers = new ArrayList<PlayerClient>();
	
	public int SERVER_PORT = DEFAULT_SERVER_PORT;
	public boolean running = false;
	
	public String currentHost = "";
	
	public boolean inGame = false;
	
	ArrayList<Vector2> mapCoords = new ArrayList<Vector2>(); 
	public String mapName = "";
	public byte[] map = new byte[1];
	public int mapWidth = 0;
	public int mapHeight = 0;
	
	public byte[] items = new byte[1];
	
//	ArrayList<BombClient> bombs = new ArrayList<BombClient>();
	
	public GameTCPServer TCPServer;
	
	public static void main(String[] args) {
		GameServer GS = null;
		if (args.length > 0) {
			int port = Integer.parseInt(args[0]);
			if (port > 0 && port < 10000) {
				GS = new GameServer(port);
			}
		} 
		if (GS == null) {
			GS = new GameServer();
		}
//		TCPServer = new GameTCPServer(GS);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		
		while (GS.running) {
			// TODO: Packet for force exit(ServerShutDown, ...)
			
			// Listen for commands
			try {
				line = br.readLine();
//				System.out.println("read: " + line);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (line.length() < 0) continue;
			
			String[] param = line.split(" ");
			
			if (param[0].equalsIgnoreCase("quit")) {
				// send to all that they are kicked with message of server shutdown
//				GS.running = false;
				// TODO: Make it happen
			} else if (param[0].equalsIgnoreCase("say")) {
				if (param.length < 1 || line.substring(4).trim().length() > 0) {
					GS.sendDataToAllClients((new Packet03ChatMessage("@Server", "@Server: " + line.substring(4).trim()).getData()));
				} else {
					System.out.println("Incorrect usage! \"say [message]\"");
				}
			} else if (param[0].equals("whisper")) {
				if (param.length >= 3) {
					boolean canSend = false;
					PlayerClient PC = null;
					for (int i = 0; i < GS.connectedPlayers.size(); i++) {
						if (GS.connectedPlayers.get(i).getUsername().equals(param[1])) {
							canSend = true;
							PC = GS.connectedPlayers.get(i);
						}
					}
					if (canSend) {
						String message = "@Server whispers:";
						for (int i = 2; i < param.length; i++) {
							message += " " + param[i];
						}
						GS.sendData(new Packet03ChatMessage("@Server whispers:", message).getData(), PC.ipAddress, PC.port);
						
					} else {
						System.out.println("User \"" + param[1] + "\" is not online.");
					}
				} else {
					System.out.println("Incorrect usage! \"whisper [username] [message]\"");
				}
			} else if (param[0].equalsIgnoreCase("kick")) {
				if (param.length == 2) {
					GS.kick(param[1]);
				} else {
					System.out.println("Incorrect usage! \"kick [username]\"");
				}
			} else if (param[0].equalsIgnoreCase("online")) {
				System.out.println("Online players(" + GS.connectedPlayers.size() + "):");
				for (int i = 0; i < GS.connectedPlayers.size(); i++) {
					System.out.println(GS.connectedPlayers.get(i).getUsername());
				}
			}
			
		}
		// send a packet to close the server
		try {
			GS.sendData("-1x".getBytes(), InetAddress.getLocalHost(), GS.SERVER_PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
	}
	
	public GameServer() {
		try {
			this.socket = new DatagramSocket(SERVER_PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		running = true;
		this.start();
		
		startTCPServer();
	}
	
	public GameServer(int port){
		this.SERVER_PORT = port;
		try {
			this.socket = new DatagramSocket(SERVER_PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		running = true;
		this.start();

		startTCPServer();
	}
	
	private void startTCPServer() {
		TCPServer = new GameTCPServer(this);
		TCPServer.start();
	}
	
	public void run() {
		System.out.println("Server started " + socket.getInetAddress() + ":" + socket.getLocalPort());
		createPingThread();
		while (running) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);				
//				System.out.println("R: " + new String(packet.getData()).trim());
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());	
		}
	}
	
	private void createPingThread() {
		Thread pingThread = new Thread("pingThread") {
			public void run() {
				while (running) {
					try {
						for (int i = 0; i < connectedPlayers.size(); i++) {
							PlayerClient player = connectedPlayers.get(i);
							if (player.pingTimeout >= 5) {
								Packet01Disconnect dcPacket = new Packet01Disconnect(player.getUsername(), Packet01Disconnect.REASON_TIMED_OUT);
								sendDataToAllClients(dcPacket.getData());				
								System.out.println("[" + player.ipAddress.toString() + ":" + player.port + "] " + dcPacket.getUsername() + " timed out.");		
								removeConnection(dcPacket);
								i--;
							} else {
								Packet04Ping packetPing = new Packet04Ping(player.getUsername(), System.currentTimeMillis(), connectedPlayers.get(i).getDelta());
								sendData(packetPing.getData(), player.ipAddress, player.port);
								player.pingTimeout++;
							}
						}
						
						Thread.sleep(2000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		pingThread.start();
		
	}
	
	public void parsePacket(byte[] data, InetAddress address, int port) {
		long receivedPacketAt = System.currentTimeMillis();
		String message = new String(data).trim();
		PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
		Packet packet = null;
		switch (type) {
		default:
		case INVALID:
			break;
		case LOGIN:
			packet = new Packet00Login(data);
			System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet00Login)packet).getUsername() + " has connected.");
			PlayerClient player = new PlayerClient(((Packet00Login)packet).getUsername(), ((Packet00Login) packet).getX(), ((Packet00Login) packet).getY(), receivedPacketAt - ((Packet00Login) packet).getTimeMillis(), address, port);
			this.addConnection(player, (Packet00Login)packet);
//			System.out.println(((Packet00Login)packet).getUsername() + " delta: " + (receivedPacketAt - ((Packet00Login) packet).getTimeMillis()));
			break;
		case DISCONNECT:
			packet = new Packet01Disconnect(data);
			if (isConnected(((Packet01Disconnect) packet).getUsername())) {
				if (((Packet01Disconnect) packet).getReason() == Packet01Disconnect.REASON_TIMED_OUT) {					
					System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet01Disconnect)packet).getUsername() + " timed out.");
				} else if (((Packet01Disconnect) packet).getReason() == Packet01Disconnect.REASON_DC) {					
					System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet01Disconnect)packet).getUsername() + " has left.");
				} else if (((Packet01Disconnect) packet).getReason() == Packet01Disconnect.REASON_KICKED) {
					System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet01Disconnect)packet).getUsername() + " has been kicked.");					
				}
				this.removeConnection((Packet01Disconnect)packet);
			}
			break;
		case MOVE:
			packet = new Packet02Move(data);
			if (isConnected(((Packet02Move) packet).getUsername())) {
				this.handleMove(((Packet02Move) packet));
			}
			break;
		case CHATMESSAGE:
			packet = new Packet03ChatMessage(data);
			if (isConnected(((Packet03ChatMessage) packet).getUsername())) {
				this.handleMessage((Packet03ChatMessage) packet);
			}
			break;
		case PING:
			packet = new Packet04Ping(data);
			this.handlePing((Packet04Ping) packet);
			break;
		case READYSTATE:
			packet = new Packet05ReadyState(data);
			this.handleReady((Packet05ReadyState) packet);
			break;
		case MAPCHOSEN:
			packet = new Packet06MapChosen(data);
			this.handleMapChosen((Packet06MapChosen) packet);
			break;
		case HOST:
			packet = new Packet07Host(data);
			currentHost = ((Packet07Host) packet).getUsername();
			packet.writeData(this);
			for (int i = 0; i < connectedPlayers.size(); i++) {
				connectedPlayers.get(i).ready = false;
			}
			break;
		case GAMESTART:
			packet = new Packet08GameStart(data);
			this.handleGameStart((Packet08GameStart) packet);
			break;
		case TILECHANGED:
			packet = new Packet09TileChanged(data);
			this.handleTileChanged((Packet09TileChanged) packet);
		case BOMBPLACED:
			packet = new Packet10BombPlaced(data);
			this.handleBombPlaced((Packet10BombPlaced) packet);
			break;
		case PLAYERKILLED:
			packet = new Packet11PlayerKilled(data);
			this.handlePlayerKilled((Packet11PlayerKilled) packet);
			break;
		case VICTORY:
			// Nothing really should be done.
			// Only server should send this packet.
			break;
		case ITEMS:
			// Server shouldn't get this message.
			break;
		case ITEMPICKUP:
			packet = new Packet14ItemPickup(data);
			this.handleItemPickup((Packet14ItemPickup) packet);
			break;
		}
	}

	public boolean isConnected(String username) {
		for (int i = 0; i < connectedPlayers.size(); i++) {
			if (username.equalsIgnoreCase(connectedPlayers.get(i).getUsername())) {
				return true;
			}
		}
		return false;
	}

	public void addConnection(PlayerClient player, Packet00Login packet) {
		boolean nameAvailable = true;
		for (int i = 0; i < this.connectedPlayers.size(); i++) {
			if (connectedPlayers.get(i).getUsername().equalsIgnoreCase(player.getUsername())) {
				nameAvailable = false;
			}
		}
		if (!nameAvailable) {
			Packet00Login packetT = new Packet00Login(packet.getUsername(), packet.getX(), packet.getY(), packet.getTimeMillis(), false);
			sendData(packetT.getData(), player.ipAddress, player.port);
			return;
		}
		boolean alreadyConnected = false;
		PlayerClient p;
		for (int i = 0; i < this.connectedPlayers.size(); i++) {
			p = this.connectedPlayers.get(i);
			if (player.getUsername().equalsIgnoreCase(p.getUsername())){
				if (p.ipAddress == null) {
					p.ipAddress = player.ipAddress;
				}
				
				if (p.port == -1) {
					p.port = player.port;
				}
				alreadyConnected = true;
			} else {
				//relay to the current connected player that there is a new player
				sendData(packet.getData(), p.ipAddress, p.port);
				//relay to the new player that the currently connected player exists
				Packet00Login packetT = new Packet00Login(p.getUsername(), p.getX(), p.getY(), p.getDelta(), true);
				sendData(packetT.getData(), player.ipAddress, player.port);
			}
		}
		if(!alreadyConnected){
			this.connectedPlayers.add(player);
			Packet00Login packetTemp = new Packet00Login(player.getUsername(), player.getX(), player.getY(), player.getDelta(), true);
			sendData(packetTemp.getData(), player.ipAddress, player.port);
			
			// Who is host?
			if (connectedPlayers.size() == 1) {
				currentHost = connectedPlayers.get(0).getUsername();
			}
			Packet07Host hostPacket = new Packet07Host(currentHost);
			sendData(hostPacket.getData(), player.ipAddress, player.port);
//			packet.writeData(this);
		}
	}
	
	public void removeConnection(Packet01Disconnect packet) {
		String username = packet.getUsername();
		this.connectedPlayers.remove(getPlayerClientIndex(username));
		packet.writeData(this);
		if (connectedPlayers.size() == 0) {
			mapName = "";
			map = new byte[1];
			mapWidth = 0;
			mapHeight = 0;			
			currentHost = "";
			inGame = false;
			mapCoords.clear();
			
			for (int i = 0; i < 5; i++) {
				System.out.println("----------");
			}
			System.out.println("0 Players online. Server map resetted.");
//			System.out.println("Map changed to <null>");
//			System.out.println("Host changed to <null>");
		} else if (currentHost.equals(username)) {
			// Find new host!
			Random rand = new Random();
			int index = Math.abs(rand.nextInt()) % connectedPlayers.size();
			currentHost = connectedPlayers.get(index).getUsername();
			Packet07Host hostPacket = new Packet07Host(currentHost);
			hostPacket.writeData(this);
			for (int i = 0; i < connectedPlayers.size(); i++) {
				connectedPlayers.get(i).ready = false;
			}
		}
	}
	
	public PlayerClient getPlayerClient(String username){
		PlayerClient player;
		for (int i = 0; i < this.connectedPlayers.size(); i++) {
			player = this.connectedPlayers.get(i);
			if (player.getUsername().equals(username)){
				return player;
			}
		}
		return null;
	}
	
	public int getPlayerClientIndex(String username){
		int index = 0;
		for (PlayerClient player: this.connectedPlayers){
			if (player.getUsername().equals(username)){
				return index;
			}
			index++;
		}
		return index;
	}
	
	public void sendData(byte[] data, InetAddress ipAddress, int port){
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
//		System.out.println("[" + new String(data).trim() + "] @ " + ipAddress + ":" + port);
		try {
			this.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendDataToAllClients(byte[] data) {
		PlayerClient p;
		for (int i = 0; i < this.connectedPlayers.size(); i++) {
			p = this.connectedPlayers.get(i);
			sendData(data, p.ipAddress, p.port);
		}
	}
	
	private void handleMove(Packet02Move packet){
		if (getPlayerClient(packet.getUsername()) != null){
//			System.out.println(packet.getCurrentFrame());
			int index = getPlayerClientIndex(packet.getUsername());
			PlayerClient player = this.connectedPlayers.get(index);
			player.setX(packet.getX());
			player.setY(packet.getY());
//			player.innerAnimation = packet.getCurrentAnimation();
//			player.innerFrame = packet.getCurrentFrame();
			
//			player.getAnimation().setCurrentAnimation(packet.getCurrentAnimation());
//			player.getAnimation().setCurrentFrame(packet.getCurrentFrame());
			packet.writeData(this);
		}
	}
	
	private void handleMessage(Packet03ChatMessage packet) {
		// Send to all for now
		System.out.println(packet.getMessage());
		packet.writeData(this);
	}
	
	private void handlePing(Packet04Ping packet) {
		long time = System.currentTimeMillis();
		PlayerClient p;
		
		for (int i = 0; i < connectedPlayers.size(); i++) {
			p = connectedPlayers.get(i);
			if (p.getUsername().equalsIgnoreCase(packet.getUsername())) {
				p.pingTimeout = 0;
				p.setDelta(time - packet.getTimeSent());
			}
		}
	}
	
	private void handleReady(Packet05ReadyState packet) {
		PlayerClient PC = getPlayerClient(packet.getUsername());
		if (PC != null) {
			PC.ready = packet.getReady();
			packet.writeData(this);
		}
	}
	
	private void handleMapChosen(Packet06MapChosen packet) {
		if (true) {
			if (!inGame) {
				if (packet.getMapName().length() == 0) {
					// Requesting current map
					PlayerClient PC;
					for (int i = 0; i < connectedPlayers.size(); i++) {
						PC = connectedPlayers.get(i);
						if (PC.getUsername().equalsIgnoreCase(packet.getSenderName())) {
							String tilesToString = "";
							for (int i1 = 0; i1 < map.length; i1++) {
								char a = (char) (map[i1] + 'A');
								tilesToString += a;
							}
							Packet06MapChosen returnPacket = new Packet06MapChosen("System", mapName, mapWidth, mapHeight, tilesToString);
							TCPServer.sendTCPData(returnPacket.getData(), PC.ipAddress, PC.port);
						}
					}
					
				} else {
					for (int i = 0; i < connectedPlayers.size(); i++) {
						connectedPlayers.get(i).ready = false;
						Packet05ReadyState playerReady = new Packet05ReadyState(connectedPlayers.get(i).getUsername(), false);
						playerReady.writeData(this);
					}
					
					mapName = packet.getMapName();
					mapWidth = packet.getWidth();
					mapHeight = packet.getHeight();
					
					map = new byte[mapWidth * mapHeight];
					String tilesString = packet.getTiles();
					int maxIndex = tilesString.length();
					for (int i = 0; i < maxIndex; i++) {
//						System.ouErt.println(tilesString.length() + " : " + tilesString.charAt(0) + " " + tilesString.charAt(tilesString.length() - 2) + " " + tilesString.charAt(tilesString.length() - 1));
//						System.out.println("map-length: " + map.length + " tiles-length: " + tilesString.length() + "  with current i: " + i);
						map[i] = (byte) (tilesString.charAt(i) - 'A');
					}
					System.out.println("Map changed to " + mapName);
					
//					System.out.println("Tiles byte array length: " + map.length);
//					System.out.println("Packet string data: " + packet.getTiles());
					
					packet.writeData(this);
					
					Packet05ReadyState readyPacket = new Packet05ReadyState("@Server", false);
					readyPacket.writeData(this);
//					TCPServer.sendTCPDataToAll(packet.getData());
				}
			}
		}
	}
	
	public void handleGameStart(Packet08GameStart packet) {
		if (inGame) {
			System.out.println("Server status is already in game.");
			return;
		}
		
		PlayerClient PC;
		boolean everyoneReady = true;
		for (int i = 0; i < connectedPlayers.size(); i++) {
			PC = connectedPlayers.get(i);
			if (!PC.getUsername().equals(currentHost)) {
				if (!PC.ready) {
					everyoneReady = false;
					System.out.println(PC.getUsername() + " is not ready.");
				}
			}
		}
		if (!everyoneReady) {
			if (packet.getUsername().equals(currentHost)) {
				PC = getPlayerClient(currentHost);
				Packet03ChatMessage msg = new Packet03ChatMessage("@Server", "Some people are not ready.");
				sendTCPData(msg.getData(), PC.ipAddress, PC.port);
			}
			return;
		}
		if (!everyoneReady) return;
		
		// START GAME
		if (packet.getUsername().equals(currentHost) && !inGame) {
			
		// Create items
		items = new byte[mapWidth * mapHeight];
		for (int i = 0; i < items.length; i++) {
			items[i] = (byte) 0;
		}
		Random random = new Random();
		for (int y = 0; y < mapHeight; y++) {
			for (int x = 0; x < mapWidth; x++) {
				if (map[x + y * mapWidth] == ID_STONE) {
					int item = random.nextInt() % ItemID.ITEM_AMOUNT;
					int correction = random.nextInt() % 15;
//					item *= (correction == 0 ? 0 : 1); // 1/15 that it will be empty. TODO  fix it.
					item *= (correction == 0 ? 1 : 0);
					item = Math.abs(item);
					items[x + y * mapWidth] = (byte) item;
				}
			}
		}
		
		String itemString = "";
		for (int i = 0; i < items.length; i++) {
			char a = (char) (items[i] + 'A');
			itemString += a;
		}
		Packet13Items itemPacket = new Packet13Items(itemString);
		itemPacket.writeData(this);
		
		
		// Send positions
			
			int maxPlayers = getMaxPlayers();
			if (maxPlayers < 2) {
				PC = getPlayerClient(currentHost);
				Packet03ChatMessage msg = new Packet03ChatMessage("@Server", "Map max players count is less than 2.");
				sendTCPData(msg.getData(), PC.ipAddress, PC.port);
				return;
			}
			
			Collections.shuffle(mapCoords);
			
			// First to host!!!
			PC = getPlayerClient(currentHost);
			PC.alive = true;
			PC.setX(mapCoords.get(0).x);
			PC.setY(mapCoords.get(0).y);
			mapCoords.remove(0);
			Packet08GameStart packetToHostStart = new Packet08GameStart(PC.getUsername(), PC.getX(), PC.getY(), true);
			packetToHostStart.writeData(this);
			
			Packet08GameStart packetToClientsStart;
			for (int i = 0; i < connectedPlayers.size(); i++) {
				PC = connectedPlayers.get(i);
				if (!PC.getUsername().equals(currentHost)) {
					if (mapCoords.size() >= 1) {
						PC.alive = true;
						PC.setX(mapCoords.get(0).x);
						PC.setY(mapCoords.get(0).y);
						mapCoords.remove(0);
						packetToClientsStart = new Packet08GameStart(PC.getUsername(), PC.getX(), PC.getY(), true);
					} else {
						PC.alive = false;
						packetToClientsStart = new Packet08GameStart(PC.getUsername(), mapWidth / 2, mapHeight / 2, false);
					}
					packetToClientsStart.writeData(this);
				}
				
			}
			inGame = true;
				
		}
	}
	
	public void handleTileChanged(Packet09TileChanged packet) {
		// Handle!!!
		map[packet.getX() + packet.getY() * mapWidth] = packet.getId();
		sendTCPDataToAllClients(packet.getData());
	}
	
	public void handleBombPlaced(Packet10BombPlaced packet) {
//		BombClient bomb = new BombClient(packet, System.currentTimeMillis());
//		bombs.add(bomb);
		long delta = getPlayerClient(packet.getPlacedBy()).getDelta();
		PlayerClient PC;
		for (int i = 0; i < connectedPlayers.size(); i++) {
			PC = connectedPlayers.get(i);
			Packet10BombPlaced serverBomb = new Packet10BombPlaced(packet.getType(), packet.getPlacedBy(), packet.getX(), packet.getY(), packet.getExplodeTimer(), packet.getTimePlaced() - delta + PC.getDelta());
			sendTCPData(serverBomb.getData(), PC.ipAddress, PC.port);
		}
		
	}
	
	public void handlePlayerKilled(Packet11PlayerKilled packet) {
		PlayerClient PC = getPlayerClient(packet.getUsername());
		if (PC.alive) {
			PC.alive = false;
			packet.writeData(this);
			System.out.println(PC.getUsername() + " has been killed.");
			
			Packet03ChatMessage message = new Packet03ChatMessage("@Server", PC.getUsername() + " has been killed!");
			message.writeData(this);
			checkVictoryCondition();
		}
	}
	
	public void handleItemPickup(Packet14ItemPickup packet) {
		items[packet.getX() + packet.getY() * mapWidth] = packet.getIdByte();
		packet.writeData(this);
		
	}
	
	public void checkVictoryCondition() {
		int alivePlayers = 0;
		PlayerClient PC;

		condition_normal: {
			for (int i = 0; i < connectedPlayers.size(); i++) {
				PC = connectedPlayers.get(i);
				if (PC.alive) {
					alivePlayers++;
				}
				if (alivePlayers > 1) {
					break condition_normal;
				}
			}
			
			if (alivePlayers <= 1) {
				String name = "@Server";
				for (int i = 0; i < connectedPlayers.size(); i++) {
					PC = connectedPlayers.get(i);
					if (PC.alive) {
						name = PC.getUsername();
						break;
					}
				}
				Packet12Victory packet = new Packet12Victory(name);
				packet.writeData(this);
				afterGameMethod();
			}
		}
	}
	
	public int getMaxPlayers() {
		int counter = 0;
		
		for (int y = 0; y < mapHeight; y++) {
			for (int x = 0; x < mapWidth; x++) {
				int tileByte = (int) map[x + y * mapWidth];
				if (tileByte == ID_SPAWN) {
					mapCoords.add(new Vector2(x, y));
					counter++;
				}
			}
		}
//		System.out.println("Max players: " + counter);
		return counter;
	}
	
	public void kick(String username) {
		PlayerClient p;
		for (int i = 0; i < this.connectedPlayers.size(); i++) {
			p = this.connectedPlayers.get(i);
			if (p.getUsername().equalsIgnoreCase(username)) {
				// Handle Kick
				Packet01Disconnect packet = new Packet01Disconnect(p.getUsername(), Packet01Disconnect.REASON_KICKED);
				removeConnection(packet);
				sendData(packet.getData(), p.ipAddress, p.port);
				System.out.println(p.getUsername() + " has been kicked.");		
				return;
			}
		}
		System.out.println("Player " + username + " is not online.");				
	}
	
	public void afterGameMethod() {
		mapCoords.clear();
		inGame = false;
		items = new byte[0];
		
		PlayerClient PC;
		for (int i = 0; i < connectedPlayers.size(); i++) {
			PC = connectedPlayers.get(i);
			PC.alive = false;
			PC.spectator = false;
			PC.blocksDestroyed = 0;
			
			PC.ready = false;
			Packet05ReadyState readyPacket = new Packet05ReadyState(PC.getUsername(), PC.ready);
			readyPacket.writeData(this);
		}
	}

	public void sendTCPDataToAllClients(byte[] data) {
		TCPServer.sendTCPDataToAll(data);
		
	}
	
	public void sendTCPData(byte[] data, InetAddress ip, int port) {
		TCPServer.sendTCPData(data, ip, port);
	}
	
}
