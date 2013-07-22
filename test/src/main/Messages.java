package main;

public class Messages {

	public final static int White = 4;
	public final static int Black = 2;

	private Grid mGrid;
	private Client mClient;

	public Messages() {
		mClient = new Client(this);

		mClient.start();
	}

	public void playWith(String pions, int color) {
		mGrid =  new StratNMMassThreadFixed(pions, Grid.TYPE_DECODE_SERVER, color);

	}

	public void getCoup() {

		Thread r = new Rototo(this);
		r.start();

		

	}

	public void setCoup(String coup) {

		mClient.envoieCoup(coup);

	}

	public void setCoupAdversaire(String moveString) {
		// D6 - D5
		long move = 0;
		move = 1L << ('H' - (moveString.charAt(1)) + (moveString.charAt(2) - '1') * 8);
		move |= 1L << (('H' - moveString.charAt(6)) + (moveString.charAt(7) - '1') * 8);

		// mGrid.printBits(move);

		mGrid.coupAdvAndUpdate(move);

	}

	/*
	 * public void printlnLOAs(){ mGrid.pringLOAs(); }
	 */
	private class Rototo extends Thread {

		Messages msg;

		private Rototo(Messages msg) {
			this.msg = msg;
		}

		@Override
		public void run() {
			// mGrid.getBestMove(3);
			msg.setCoup(mGrid.getBestMoveAsString(3));
		}
	}
}
