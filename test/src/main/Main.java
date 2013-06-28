package main;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import part1.MainClient;

public class Main {

	private static String ipserver; 
	private static int portNumber;
	private static Socket client;
	
	
	public static void main(String[] args) {
		ip = args[1];
		portNumber = Integer.decode(args[0]);
		
		
		Grid grid = new Grid(
				"10011100" +
				"01120010" +
				"00000012" +
				"00022001" +
				"00000000" +
				"00022000" +
				"00000000" +
				"00000002");
		grid.printBits();
		System.out.println(grid.isConnected());

	}
	
	public static void connectServerSocket(String ip, int portNumber){
		try{
			Main.portNumber = portNumber;
			client = new Socket(ip, portNumber);
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	public static void receiveData(){
		int MAXLENGTH = 1024;
		byte[] data = new byte[MAXLENGTH];
		try{
			InputStream in = client.getInputStream();
			in.read(data);
			String message = new String(data, "UTF-8");
			System.out.println(message);
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	public static void closeConnection(){
		try{
			client.close();
		}
		catch(IOException e){
			System.out.println(e);
		}
	}

}
