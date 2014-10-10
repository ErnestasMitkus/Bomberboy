package com.draznel.bomberboy.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.draznel.bomberboy.Main;
import com.draznel.bomberboy.entities.PlayerMP;
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
import com.draznel.bomberboy.packets.Packet14ItemPickup;
import com.draznel.bomberboy.screens.LobbyScreen;
import com.draznel.bomberboy.tiles.Tile;

public class GameClient extends Thread {

	public static final int DEFAULT_SERVER_PORT = 7331;
	
	public InetAddress ipAddress;
	private DatagramSocket socket;
	private Main game;
	
	public int port;
	public int SERVER_PORT = DEFAULT_SERVER_PORT;
	
	
	public boolean connected = false;
	public boolean nameAvailable = false;
	
	GameTCPClient TCPClient;
	
	public GameClient(Main game, String ipAddress) {
		this.game = game;
		try {
			this.socket = new DatagramSocket();
			port = socket.getLocalPort();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		try {
			this.ipAddress = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		TCPClient = new GameTCPClient(this);
		TCPClient.start();
		
	}
	
	public GameClient(Main game, String ipAddress, int port) {
		this(game, ipAddress);
		SERVER_PORT = port;
	}

	public void run() {
		while (true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
//				System.out.println("Listening on " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
//			System.out.println("GOT A PACKET: [" + new String(packet.getData()).trim() + "]");
			this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
		}
	}
	
	public void parsePacket(byte[] data, InetAddress address, int port) {
		String message = new String(data).trim();
		PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
		Packet packet = null;
		switch (type) {
		default:
		case INVALID:
			break;
		case LOGIN:
			packet = new Packet00Login(data);
			handleLogin((Packet00Login) packet, address, port);
			break;
		case DISCONNECT:
			packet = new Packet01Disconnect(data);
			if (((Packet01Disconnect) packet).getUsername().equals(game.getPlayer().getUsername())) {
				connected = false;
			} else {				
				System.out.println("[" + address.getHostAddress() + ":" + port + "] " + ((Packet01Disconnect)packet).getUsername() + " has left the world.");
			}
			game.getLevel().removePlayerMP(((Packet01Disconnect)packet).getUsername(), ((Packet01Disconnect)packet).getReason());
			break;
		case MOVE:
			packet = new Packet02Move(data);
			this.handleMove((Packet02Move)packet);
			break;
		case CHATMESSAGE:
			packet = new Packet03ChatMessage(data);
			this.handleMessage((Packet03ChatMessage)packet);
			break;
		case PING:
			packet = new Packet04Ping(data);
			this.handlePing((Packet04Ping)packet);
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
			game.getLevel().setHost((Packet07Host) packet);
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
			packet = new Packet12Victory(data);
			this.handleVictory((Packet12Victory) packet);
			break;
		case ITEMS:
			packet = new Packet13Items(data);
			this.handleItems((Packet13Items) packet);
			break;
		case ITEMPICKUP:
			packet = new Packet14ItemPickup(data);
			this.handleItemPickup((Packet14ItemPickup) packet);
			break;
		}
	}

	public void sendData(byte[] data){
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, SERVER_PORT);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handleLogin(Packet00Login packet, InetAddress address, int port){
//		System.out.println("[" + address.getHostAddress() + ":" + port + "] "
//				+ packet.getUsername() + " has joined the game.");
		if (packet.getUsername().equals(game.getPlayer().getUsername())) {
			if (packet.getNameAvailable()) {
				connected = true;				
				nameAvailable = true;
			} else {
				connected = true;				
				nameAvailable = false;
			}
		} else {
			PlayerMP player = new PlayerMP(packet.getUsername(), packet.getX(), packet.getY(), address, port);
			game.getLevel().addPlayerMP(player);
		}
	}
	
	private void handleMove(Packet02Move packet){
		game.getLevel().movePlayer(packet.getUsername(), packet.getX(), packet.getY(),
							packet.getCurrentAnimation(), packet.getCurrentFrame());
	}
	
	private void handleMessage(Packet03ChatMessage packet) {
		game.getScreenManager().getScreen().printMessage(packet.getMessage());
	}
	
	private void handlePing(Packet04Ping packet) {
		long time = System.currentTimeMillis();
		game.setPing(time - (packet.getTimeSent() - packet.getDelta()));
		Packet04Ping pong = new Packet04Ping(game.getPlayer().getUsername(), time, time - packet.getTimeSent());
		pong.writeData(this);
	}
	
	private void handleReady(Packet05ReadyState packet) {
		if (packet.getUsername().equalsIgnoreCase(game.getPlayer().getUsername()) ||
				packet.getUsername().equals("@Server")) {
			game.getPlayer().ready = packet.getReady();
		} else {
			int index = game.getLevel().getPlayerMPIndex(packet.getUsername());
			game.getLevel().getPlayerByIndex(index).ready = packet.getReady();
		}	
	}
	
	private void handleMapChosen(Packet06MapChosen packet) {
		if (!packet.getSenderName().equalsIgnoreCase(game.getPlayer().getUsername())) {
			if (packet.getMapName().length() == 0) {
				// Server has no map yet.
				if (game.getScreenManager().getScreen() instanceof LobbyScreen) {
					((LobbyScreen) game.getScreenManager().getScreen()).needToSendDefaultMap = true;
				}
				return;
			}
			game.getLevel().changeMap(packet.getMapName(), packet.getWidth(), packet.getHeight(), packet.getTiles());
			System.out.println("Map changed to " + packet.getMapName());
		} else {
			System.out.println("My map was changed to. It's name: " + packet.getMapName());			
		}
	}
	
	public void handleGameStart(Packet08GameStart packet) {
		if (packet.getUsername().equals(game.getPlayer().getUsername())) {
			game.getPlayer().spawnPlayer(game.getLevel(), packet.getX() * Tile.SIZE, Main.HEIGHT - (packet.getY() * Tile.SIZE), packet.getAlive());
			((LobbyScreen) game.getScreenManager().getScreen()).canLoadGame = true;
		} else {
			game.getLevel().spawnPlayer(packet.getUsername(), packet.getX() * Tile.SIZE, Main.HEIGHT - (packet.getY() * Tile.SIZE), packet.getAlive());
		}
	}
	
	public void handleTileChanged(Packet09TileChanged packet) {
		game.getLevel().alterTileQueue(packet.getX(), packet.getY(), packet.getId());
	}
	
	public void handleBombPlaced(Packet10BombPlaced packet) {
//		if (packet.getPlacedBy().equals(game.getPlayer().getUsername())) {
			game.getLevel().addBomb(packet.getType(), packet.getPlacedBy(), packet.getX(), packet.getY(), packet.getExplodeTimer(), packet.getTimePlaced());
//		}
	}
	
	public void handlePlayerKilled(Packet11PlayerKilled packet) {
		if (packet.getUsername().equals(game.getPlayer().getUsername())) {
			if (game.getPlayer().alive) {
				game.getPlayer().alive = false;
			}
		} else {			
			game.getLevel().getPlayerByIndex(game.getLevel().getPlayerMPIndex(packet.getUsername())).alive = false;
		}
	}
	
	public void handleVictory(Packet12Victory packet) {
		game.getScreenManager().victory(packet.getUsername());
	}

	public void handleItems(Packet13Items packet) {
		game.getLevel().makeItems(packet.getItems());
	}
	
	public void handleItemPickup(Packet14ItemPickup packet) {
		game.getLevel().handleItemPickup(packet.getUsername(), packet.getId(), packet.getX(), packet.getY());		
	}
	
	public void sendTCPData(byte[] data) {
		TCPClient.sendTCPData(data);
	}
	
	public boolean isRunning() {
		return game.isRunning();
	}
	
	
}
