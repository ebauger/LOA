package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;


public class MultiThreadNegaMaxTableFeuille extends NegaMaxPrud {

	
	
	
	
	public MultiThreadNegaMaxTableFeuille(MultiThreadNegaMaxTableFeuille nmp) {
		super(nmp);

	}

	public MultiThreadNegaMaxTableFeuille(String str, int type, int myColor) {
		super(str, type, myColor);
		
	}
		
	
	private volatile static int bestAlpha = M_INFINITY;
	private static ExecutorService pool;

	private static long bestMvt;
	private static MultiThreadNegaMaxTableFeuille OriginGrid;
	private static int nbRunningThread;

	private static LinkedTransferQueue<MyCalculatorThread> terminatedThread; 
	
	
	public static void init()
	{
		NegaMaxPrud.init();
		
		
		int maxThread = Runtime.getRuntime().availableProcessors();
		pool = Executors.newFixedThreadPool(maxThread-1);
//		pool = Executors.newFixedThreadPool(600);
		
		
		terminatedThread= new LinkedTransferQueue<MyCalculatorThread>();
		
	}
	
	@Override
	public long getBestMove() {

		OriginGrid = this;
		
		
		
		int partie_Term = checkPartieTerm();
		
		if(partie_Term == PARTIE_GAGNE)
		{
			System.out.println("PARTIE_GAGNE");
			return 0;
		}else if(partie_Term == PARTIE_PERDU)
		{
			System.out.println("PARTIE_PERDU");
			return 0;
		}

		int partieTerm = checkPartieTerm();
		
		if(partieTerm == PARTIE_GAGNE){
			
			System.out.println("PARTIE_GAGNE");
			return 0;
		}
		
		
		if(partieTerm == PARTIE_PERDU){
			System.out.println("PARTIE_PERDU");
			return 0;
		}
		
		
		bestAlpha = M_INFINITY;
		
		launch(this,0);
		

		if(bestAlpha == PARTIE_GAGNE)
		{
			System.out.println(" PARTIE_GAGNE" );
			
		}
		else if(bestAlpha == PARTIE_PERDU)
		{
			System.out.println(" PARTIE_PERDU" );
		}
		else
		{
			System.out.println("heuristiqueTrouve = "+bestAlpha );
		}
		
		System.out.println();
		
		return bestMvt;
		
	}
	
	private void launch(MultiThreadNegaMaxTableFeuille workingGrid ,int startdepth)
	{
		
		
		ArrayList<Long> mvts = workingGrid.generatePossibleMvt();
		
		bestMvt = mvts.get(0);
		
		Collections.shuffle(mvts);
		
		for (Long mvt : mvts) {
			
			MyCalculatorThread th = new MyCalculatorThread(mvt,startdepth+1);
			pool.execute(th);
			
			++nbRunningThread;
		}
		
		
		
		while (nbRunningThread > 0) {
			
			try {
				MyCalculatorThread th = terminatedThread.take();
				
				if(-th.alpha > bestAlpha){
					bestAlpha = -th.alpha;
					bestMvt = th.precedentMvt;
				}
				
				--nbRunningThread;
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			
		}
		
		
			
	}

	private class MyCalculatorThread extends Thread
	{
		
		public int alpha;
		public long precedentMvt;
		private int startDepth;

		
		public MyCalculatorThread(long precedentMvt,int startDepth) {
			super();
			this.alpha = M_INFINITY;
			this.precedentMvt = precedentMvt;
			this.startDepth = startDepth;
		}
		
		@Override
		public void run() 
		{

//			System.out.println("I'm doing a loop : " + Thread.currentThread().getName() +
//							"With:\n\talpha=" + this.alpha +" \n\tbeta: " + -bestAlpha +"\n");
						
			MultiThreadNegaMaxTableFeuille workingGrid = new MultiThreadNegaMaxTableFeuille(OriginGrid) ;
			workingGrid.MakeMvtAndUpdate(this.precedentMvt, false);
			workingGrid.inverse();
			
			

			int partieTerm = checkPartieTerm();
			if (partieTerm == PARTIE_GAGNE) {
				++nbfeuilles;
				this.alpha = PARTIE_GAGNE - this.startDepth;
			} else if (partieTerm == PARTIE_PERDU) {
				++nbfeuilles;
				this.alpha = PARTIE_PERDU + this.startDepth;
			} else if (this.startDepth == MaxLvl) {
				++nbfeuilles;
				if(this.startDepth%2 == 1)
				{
					inverse();
					this.alpha = -(2*calculeHeuristique() + evaluateBlocking() - this.startDepth);
				}else{
					this.alpha = 2*calculeHeuristique() + evaluateBlocking() - this.startDepth;
				}
			} else {

				ArrayList<Long> mvts = workingGrid.generatePossibleMvt();

				for (Long move : mvts) {
					MultiThreadNegaMaxTableFeuille mtNM = new MultiThreadNegaMaxTableFeuille(workingGrid);
					mtNM.MakeMvtAndUpdate(move, false);
					mtNM.inverse();

					int val = -mtNM.NegaMax(bestAlpha, -this.alpha,	this.startDepth + 1);

//							System.out.println("val=" + val +" / Beta: " + -bestAlpha +" : " + Thread.currentThread().getName());
					
					if (val > alpha) {
						this.alpha = val;

						if (this.alpha >= -bestAlpha) {
							break;
						}
					}

				}
			}
			
			terminatedThread.offer(this);
			

//			System.out.println("Time to check : " + time/1000 + " mico-seconde : continue=" + Thread.currentThread().getName()+"\n");

		}
	}
	
}
