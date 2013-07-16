package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

class Client extends Thread {

	// private final Callback mCallback;

	private final Messages mMessages;

	private Socket MyClient;
	private BufferedInputStream input;
	private BufferedOutputStream output;

	public Client(Messages mess) {
		mMessages = mess;

		try {
			MyClient = new Socket("localhost", 8888);
			input = new BufferedInputStream(MyClient.getInputStream());
			output = new BufferedOutputStream(MyClient.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		// int[][] board = new int[8][8];
		try {

			// BufferedReader console = new BufferedReader(new
			// InputStreamReader(
			// System.in));
			while (true) {
				char cmd = 0;

				cmd = (char) input.read();

				// Début de la partie en joueur blanc
				if (cmd == '1') {
					byte[] aBuffer = new byte[1024];

					int size = input.available();
					// System.out.println("size " + size);
					input.read(aBuffer, 0, size);
					String s = new String(aBuffer).trim();
					String boardValues;
					boardValues = s.replace(" ", "");
					System.out.println("Nouvelle partie! Vous jouer blanc, \nEntrez votre coup : ");
					
					System.out.println("plateau envoyé : " + s);
					mMessages.playWith(boardValues, Grid.WHITE);

					

					mMessages.getCoup(); // null;
					// move = console.readLine();
					// output.write(move.getBytes(),0,move.length());
					// output.flush();

				}
				// Début de la partie en joueur Noir
				if (cmd == '2') {
					System.out
							.println("Nouvelle partie! Vous jouer noir, \nattendez le coup des blancs");
					byte[] aBuffer = new byte[1024];

					int size = input.available();
					// System.out.println("size " + size);
					input.read(aBuffer, 0, size);
					String s = new String(aBuffer).trim();
					System.out.println("plateau envoyé : " + s);
					String boardValues = s.replace(" ", "");

					mMessages.playWith(boardValues, Grid.BLACK);

					// String[] boardValues;
					// boardValues = s.split(" ");
					// int x=0,y=0;
					// for(int i=0; i<boardValues.length;i++){
					// board[x][y] = Integer.parseInt(boardValues[i]);
					// x++;
					// if(x == 8){
					// x = 0;
					// y++;
					// }
					// }
				}

				// Le serveur demande le prochain coup
				// Le message contient aussi le dernier coup joué.
				if (cmd == '3') {
					byte[] aBuffer = new byte[16];

					int size = input.available();
					// System.out.println("size " + size);
					input.read(aBuffer, 0, size);

					String s = new String(aBuffer);
					System.out.println("coup de l'adversaire :\n" + s);

					mMessages.setCoupAdversaire(s);

					System.out.println("Entrez votre coup : ");
					mMessages.getCoup();
					// move = console.readLine();
					// output.write(move.getBytes(),0,move.length());
					// output.flush();

				}
				// Le dernier coup est invalide
				if (cmd == '4') {
					System.err.println("Coup invalide, entrez un nouveau coup : ");
					// String move = null;
					// mMessages.printlnLOAs();
					mMessages.getCoup();

				}
			}
		} catch (IOException e) {
			System.out.println(e);
		}

	}

	public void envoieCoup(String coup) {
		try {
			System.out.println(coup);
			output.write(coup.getBytes(), 0, coup.length());
			output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
