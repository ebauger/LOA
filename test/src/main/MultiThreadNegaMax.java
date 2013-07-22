package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import main.NegaMaxPrudTranspositionTable.BestPathState;

public class MultiThreadNegaMax extends NegaMaxPrudTranspositionTable {

	public MultiThreadNegaMax(MultiThreadNegaMax nmp) {
		super(nmp);

	}

	public MultiThreadNegaMax(String str, int type, int myColor) {
		super(str, type, myColor);
		
	}
		
	
	private volatile static int bestAlpha = M_INFINITY;
	private static ExecutorService pool;
	private static final int MAX_THREAD = 4;
	private static Stack<Long> movments;
	private static long bestMvt;
	private static MultiThreadNegaMax workingGrid;
	private static Thread baseThread;
	private static int nbRunningThread;

	
	
	@Override
	public long getBestMove() {

		pool = Executors.newFixedThreadPool(4);
		
		workingGrid = this;
		
		ArrayList<Long> mvts = this.generatePossibleMvt();
		
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
		
		movments = new Stack<Long>();
		
		for (Long mvt : mvts) {
			movments.push(mvt);
		}
		
		
		Collections.shuffle(movments);
		
		bestAlpha = M_INFINITY;
		bestMove = movments.peek();
		
		
		baseThread = Thread.currentThread();
		
		int partieTerm = checkPartieTerm();
		
		if(partieTerm == PARTIE_GAGNE){
			
			System.out.println("PARTIE_GAGNE");
			return 0;
		}
		else if(partieTerm == PARTIE_PERDU){
			System.out.println("PARTIE_PERDU");
			return 0;
		}
		
		firstLaunch(0);
		
//		System.out.println("waiting");
		
		synchronized (baseThread) {
		    	  try {
					baseThread.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		 }
		
		
		System.out.println();
		if(bestAlpha !=-1)
		{
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
		}
		else
			System.out.println("Probleme avec l'heuristique = -1");
		
		System.out.println();
		
		return bestMvt;
		
	}
	
	private void firstLaunch(int startdepth)
	{
//		
//		int firstKey = whitePions?nbMPions<<3+nbPionsAdv:nbPionsAdv<<3+nbMPions;
//		
//		Long secondKey = mPions|mPionsAdv;
//		
//		HashMap<Long, BestPathState> tdt = tableDeTransposition.get(firstKey);
//		
//		BestPathState bps = null;
//		
//		if(tdt != null)
//		{
//			bps = tdt.get(secondKey);
//			
//			if(bps != null)
//			{
//				if(startdepth >= bps.depth)
//				{
//					nbEntryTransReuse++;	
//					int meilleurTdt = bps.meilleurNegaMax;
//					meilleurTdt *= whitePions?1:-1;
//					bestAlpha = meilleurTdt == PARTIE_PERDU? meilleurTdt + startdepth:meilleurTdt-startdepth;
//				}
//			}
//		}

		
		long mvt;
		for (int i = 1; i <= MAX_THREAD & !movments.isEmpty(); i++) {
			
			mvt = movments.pop();
			
			MultiThreadNegaMax nmp = new MultiThreadNegaMax(this) ;
			nmp.MakeMvtAndUpdate(mvt, false);
			nmp.inverse();
			
			MyThread th = new MyThread(nmp , mvt,startdepth+1);
			pool.execute(th);
			
			++nbRunningThread;
		}
		
		
		
	}
	
	private synchronized static boolean checkContinue(MyThread th)
	{
		--nbRunningThread;
		
		if(-th.getMeilleur() > bestAlpha){
			bestAlpha = -th.getMeilleur();
			
			bestMvt = th.getPrecedentMvt();
		}
		
		if(movments.isEmpty())
		{
			if(nbRunningThread == 0)
			{
				pool.shutdown();
				
//				System.out.println("pool ShutDown");
				
				synchronized (baseThread) {
					baseThread.notify();
				}
			}	
			return false;
		}
		long mvt =movments.pop();
		
		MultiThreadNegaMax nmp = new MultiThreadNegaMax(workingGrid) ;
		nmp.MakeMvtAndUpdate(mvt, false);
		nmp.inverse();
		
		th.reset(nmp, mvt, th.getStartDepth());
		
		++nbRunningThread;
		
		
		return true;
	}
	
	private class MyThread extends Thread
	{
		
		private int alpha;
		private int meilleur;
		private long precedentMvt;
		
		private MultiThreadNegaMax workingGrid;
		
		private int startDepth;
		
		
		public int getStartDepth() {
			return startDepth;
		}

		public int getMeilleur() {
			return meilleur;
		}

		public long getPrecedentMvt() {
			return precedentMvt;
		}
		
		public MyThread(MultiThreadNegaMax workingGrid,long precedentMvt,int startDepth) {
			super();
			this.alpha = M_INFINITY;
			this.meilleur = M_INFINITY;
			this.workingGrid = workingGrid;
			this.precedentMvt = precedentMvt;
			this.startDepth = startDepth;
		}
		
		
		public void reset(MultiThreadNegaMax workingGrid,long precedentMvt,int startDepth)
		{
			this.alpha = M_INFINITY;
			this.meilleur = M_INFINITY;
			this.workingGrid = workingGrid;
			this.precedentMvt = precedentMvt;
			this.startDepth = startDepth;
		}
		
		@Override
		public void run() 
		{
			
			while(true)
			{
				
				int firstKey = whitePions?nbMPions<<3+nbPionsAdv:nbPionsAdv<<3+nbMPions;
				
				Long secondKey = mPions|mPionsAdv;
				
				HashMap<Long, BestPathState> tdt = tableDeTransposition.get(firstKey);
				
				BestPathState bps = null;
				
				if(tdt != null)
				{
					bps = tdt.get(secondKey);
					
					if(bps != null)
					{
						if(this.startDepth >= bps.depth)
						{
							nbEntryTransReuse++;	
							int meilleurTdt = bps.meilleurNegaMax;
							meilleurTdt *= whitePions?1:-1;
							this.meilleur = meilleurTdt == PARTIE_PERDU? meilleurTdt + this.startDepth:meilleurTdt-this.startDepth;
						}
					}
				}
				
				
				int partieTerm = checkPartieTerm();
				if(partieTerm == PARTIE_GAGNE){
					++nbfeuilles;
					this.meilleur = PARTIE_GAGNE - this.startDepth;
				}
				else if(partieTerm == PARTIE_PERDU){
					++nbfeuilles;
					this.meilleur = PARTIE_PERDU + this.startDepth;
				}else if (this.startDepth == MaxLvl){
					++nbfeuilles;
					this.meilleur = calculeHeuristique() - this.startDepth;
				}else
				{
				
					ArrayList<Long> mvts = this.workingGrid.generatePossibleMvt();
					
					for (Long move : mvts) {
						MultiThreadNegaMax mtNM = new MultiThreadNegaMax(this.workingGrid);
						mtNM.MakeMvtAndUpdate(move, false);
						mtNM.inverse();
						
						int val = -mtNM.NegaMax(bestAlpha, -this.alpha, this.startDepth+1);
						
						if(val>this.meilleur)
						{
							this.meilleur = val;
							
							if(this.meilleur > alpha)
							{
								this.alpha = val;
								
								
								if(this.alpha>=bestAlpha)
								{
									
									break;
								}
							}	
						}
					}
					
					tdt =tableDeTransposition.get(firstKey);
					
					int meilleurReel = meilleur == PARTIE_PERDU?meilleur+this.startDepth:meilleur-this.startDepth;
					
					meilleurReel *= whitePions?1:-1;
					
					if(tdt == null)
					{
						bps = new BestPathState(mPions,mPionsAdv, meilleurReel, this.startDepth);
						
						HashMap<Long, BestPathState> hm = new HashMap<Long, NegaMaxPrudTranspositionTable.BestPathState>();
						hm.put(secondKey, bps);
						tableDeTransposition.put(firstKey, hm);
						
					}else{
						
						bps = tdt.get(secondKey);
						
						if(bps == null)
							tdt.put(secondKey, new BestPathState(mPions,mPionsAdv, meilleurReel, this.startDepth));
						else				
							if(this.startDepth < bps.depth){
								bps.meilleurNegaMax = meilleurReel;
							}
					}
					
	
					if(!checkContinue(this)){
						break;
					}
				}
			}
		}
		
		
	}
	
	
	
}
