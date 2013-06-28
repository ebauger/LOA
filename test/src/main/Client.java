package main;

import java.io.*;
import java.net.*;


class Client{
	public static void main(String[] args) {
         
	Socket MyClient;
	BufferedInputStream input;
	BufferedOutputStream output;
    int[][] board = new int[8][8];
	try {
		MyClient = new Socket("localhost", 8888);
	   	input    = new BufferedInputStream(MyClient.getInputStream());
		output   = new BufferedOutputStream(MyClient.getOutputStream());
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));  
	   	while(true){
			char cmd = 0;
		   	
            cmd = (char)input.read();
            		
            // Début de la partie en joueur blanc
            if(cmd == '1'){
            	startGameWith(cmd, input, output, console);
            }
            // Début de la partie en joueur Noir
            if(cmd == '2'){
            	startGameWith(cmd, input, output, console);
            }

			// Le serveur demande le prochain coup
			// Le message contient aussi le dernier coup joué.
			if(cmd == '3'){
				byte[] aBuffer = new byte[16];
				
				int size = input.available();
				//System.out.println("size " + size);
				input.read(aBuffer,0,size);
				
				String s = new String(aBuffer);
				System.out.println("Dernier coup : "+ s);
		       	System.out.println("Entrez votre coup : ");
				String move = null;
				move = console.readLine();
				output.write(move.getBytes(),0,move.length());
				output.flush();
				
			}
			// Le dernier coup est invalide
			if(cmd == '4'){
				System.err.println("Coup invalide, entrez un nouveau coup : ");
		       	String move = null;
				move = console.readLine();
				output.write(move.getBytes(),0,move.length());
				output.flush();
				
			}
        }
	}
	catch (IOException e) {
   		System.out.println(e);
	}
	
    }
	
	public static void startGameWith(char cmd, BufferedInputStream input, BufferedOutputStream output, BufferedReader console){ 

		byte[] aBuffer = new byte[1024];
		
		int size = input.available();
		//System.out.println("size " + size);
		input.read(aBuffer,0,size);
        String s = new String(aBuffer).trim();
        String[] boardValues;
        boardValues = s.split(" ");
        int x=0,y=0;
        for(int i=0; i<boardValues.length;i++){
            board[x][y] = Integer.parseInt(boardValues[i]);
            x++;
            if(x == 8){
                x = 0;
                y++;
            }
        }
        if(cmd=='1')
            System.out.println("Nouvelle partie! Vous jouez blanc, entrez votre premier coup : ");

        if(cmd=='2')
        	System.out.println("Nouvelle partie! Vous jouez noir, entrez votre premier coup : ");
        
        String move = null;
        move = console.readLine();
		output.write(move.getBytes(),0,move.length());
		output.flush();
	}
	
}