package main;

public class Messages {

	private NegaMaxPrud mGrid;
//	private MTDFNegaMaxPrudTranspositionTableProgressivelvl mGrid;
	//private NegaMaxPrudTranspositionTable mGridTT;
	
//	private MTDFNegaMaxPrudTranspositionTable mGrid;
	
	
	private Client mClient;
	
	
	
	public Messages() {
		mClient = new Client(this);

		mClient.start();
	}

	public void playWith(String pions, int color) {
//		mGrid = new MTDFNegaMaxPrudTranspositionTableProgressivelvl(pions, Grid.TYPE_DECODE_SERVER, color);
		mGrid = new NegaMaxPrudTranspositionTable(pions, Grid.TYPE_DECODE_SERVER, color);
//		mGrid = new MTDFNegaMaxPrudTranspositionTable(pions, Grid.TYPE_DECODE_SERVER, color);
//		mGrid = new NegaMaxPrud(pions, Grid.TYPE_DECODE_SERVER, color);
		
//		mGrid = new MultiThreadNegaMax(pions, Grid.TYPE_DECODE_SERVER, color);
		
		//mGridTT.printGame();
		mGrid.printGame();
		
	}

//	long start;

	public void getCoup() {

		Thread r = new Rototo(this);
		r.setPriority(10);
		r.start();

//		start = System.nanoTime();

	}

	public void setCoup(String coup) {

		mClient.envoieCoup(coup);

//		long end = System.nanoTime();
//		float time = end - start;
//
//		System.out.println("time s= " + time / 1000000000 + " mls=" + time
//				+ 1000000 + " mcs=" + time / 1000 + " ns=" + time);

	}

	public void setCoupAdversaire(String moveString) {
		// D6 - D5
		long move = 0;
		move = 1L << ('H' - (moveString.charAt(1)) + (moveString.charAt(2) - '1') * 8);
		move |= 1L << (('H' - moveString.charAt(6)) + (moveString.charAt(7) - '1') * 8);

		// mGrid.printBits(move);

		//mGridTT.coupAdvAndUpdate(move,true);

		mGrid.coupAdvAndUpdate(move,true);
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
			msg.setCoup(mGrid.getBestMoveAsString(Main.MAX_LVL));
			System.gc();
		}
	}
}
