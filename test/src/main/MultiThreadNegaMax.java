package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MultiThreadNegaMax extends NegaMaxPrudTranspositionTable {

	
	protected static int MaxLvlThread1 = 6;
	
	
	
	
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
		
		nbEntryTransReuse = 0;
		
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
					e.printStackTrace();
				}

		 }
		
		System.out.println("\ntaille table de transposage     = 	" + nbEntryTransSaved);
		System.out.println("	reutilisées      = 	" + nbEntryTransReuse);
		
		
		//System.out.println();
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
		
//		System.out.println("End : " + Thread.currentThread().getName());
		
		if(-th.alpha > bestAlpha){
			bestAlpha = -th.alpha;
			
			bestMvt = th.getPrecedentMvt();
//			System.out.println();
//			System.out.println("New BestAlpha Found = " + bestAlpha +" : "+ Thread.currentThread().getName());
//			System.out.println();
		}
		
		if(movments.isEmpty())
		{			
			
			if(nbRunningThread == 0)
			{				
				synchronized (baseThread) {
					baseThread.notify();
				}
				
				pool.shutdown();
			}	
			return false;
		}
		long mvt =movments.pop();
		
		MultiThreadNegaMax nmp = new MultiThreadNegaMax(workingGrid) ;
		nmp.MakeMvtAndUpdate(mvt, false);
		nmp.inverse();
		
		th.reset(nmp, mvt, th.getStartDepth());
		
//		System.out.println("Restart : " + Thread.currentThread().getName());
		
		++nbRunningThread;
				
		return true;
	}
	
	private class MyThread extends Thread
	{
		
		private int alpha;
		private long precedentMvt;
		
		private MultiThreadNegaMax workingGrid;
		
		private int startDepth;
		
		
		public int getStartDepth() {
			return startDepth;
		}

		public long getPrecedentMvt() {
			return precedentMvt;
		}
		
		public MyThread(MultiThreadNegaMax workingGrid,long precedentMvt,int startDepth) {
			super();
			this.alpha = M_INFINITY;
			this.workingGrid = workingGrid;
			this.precedentMvt = precedentMvt;
			this.startDepth = startDepth;
		}
		
		
		public void reset(MultiThreadNegaMax workingGrid,long precedentMvt,int startDepth)
		{
			//System.out.println("I'm reseted : " + this.getName());
			this.alpha = M_INFINITY;
			this.workingGrid = workingGrid;
			this.precedentMvt = precedentMvt;
			this.startDepth = startDepth;
		}
		
		@Override
		public void run() 
		{
			
			while (true) {

//				System.out.println("I'm doing a loop : " + Thread.currentThread().getName() +
//							"With:\n\talpha=" + this.alpha +" \n\tbeta: " + -bestAlpha +"\n");
				boolean breaked = false;

				int firstKey = whitePions?(nbMPions<<5)+nbPionsAdv:(nbPionsAdv<<5)+nbMPions;
				
				
				
				ConcurrentHashMap<Long, Long> tdt = tableDeTransposition.get(firstKey);
				

				if(tdt != null)
				{
					
					Long secondKey = mPions|mPionsAdv;
					
					Long bps = tdt.get(secondKey);
					
					if(bps != null)
					{
						
						int saveDepth = (int) (bps >> 32);
						
						if(this.startDepth >= saveDepth)
						{
							
							nbEntryTransReuse++;
							
							int saveNegaMax = (int) (bps << 0);
									
									
							int meilleurTdt = (saveNegaMax < 0)?saveNegaMax+this.startDepth-saveDepth:saveNegaMax-this.startDepth + saveDepth ;
							meilleurTdt *= whitePions?1:-1;
							this.alpha = meilleurTdt;
							breaked = false;
						}
					}
				}

				if (!breaked) {
					int partieTerm = checkPartieTerm();
					if (partieTerm == PARTIE_GAGNE) {
						++nbfeuilles;
						breaked = true;
						this.alpha = PARTIE_GAGNE - this.startDepth;
					} else if (partieTerm == PARTIE_PERDU) {
						++nbfeuilles;
						breaked = true;
						this.alpha = PARTIE_PERDU + this.startDepth;
					} else if (this.startDepth == MaxLvl) {
						++nbfeuilles;
						breaked = true;
						this.alpha = calculeHeuristique() - this.startDepth;
					} else {

						ArrayList<Long> mvts = this.workingGrid
								.generatePossibleMvt();

						for (Long move : mvts) {
							MultiThreadNegaMax mtNM = new MultiThreadNegaMax(
									this.workingGrid);
							mtNM.MakeMvtAndUpdate(move, false);
							mtNM.inverse();

							int val = -mtNM.NegaMax(bestAlpha, -this.alpha,	this.startDepth + 1);

//							System.out.println("val=" + val +" / Beta: " + -bestAlpha +" : " + Thread.currentThread().getName());
							
							if (val > alpha) {
								this.alpha = val;

								if (this.alpha >= -bestAlpha) {
									breaked = true;
									break;
								}
							}
							
							
						}
					}

					if (!breaked) {
						if(alpha != M_INFINITY)
						{
							long start = System.nanoTime();
							
							Long secondKey = mPions|mPionsAdv;
							
							int meilleurReel = alpha * (whitePions?1:-1);
							
							synchronized (tableDeTransposition) {
								
//								System.out.println("I'm checking Table to insert: " + Thread.currentThread().getName());
								
								tdt =tableDeTransposition.get(firstKey);
										
								if(tdt == null)
								{
									ConcurrentHashMap<Long, Long> hm = new ConcurrentHashMap<Long, Long>();
									
									long bps = this.startDepth;
									bps <<= 32;
									
									bps += meilleurReel;

									
									hm.put(secondKey, bps);
									
									
									tableDeTransposition.put(firstKey, hm);
									
									
									nbEntryTransSaved++;
									
								}else{
									
									Long bps = tdt.get(secondKey);
									
									if(bps == null)
									{	
										

										bps = new Long(this.startDepth);
										
										bps <<= 32;
										
										bps += meilleurReel;

										
										tdt.put(secondKey, bps);
										nbEntryTransSaved++;
									}
									else	
									{
										int saveDepth = (int) (bps >> 32);
										
										if(this.startDepth < saveDepth){
											
											long newbps = this.startDepth;
											newbps <<= 32;
											
											newbps += meilleurReel;

											tableDeTransposition.get(firstKey).put(secondKey, newbps);

										}
									}
								}				
							}
							
							 long end = System.nanoTime();
							 float time = end - start;
							
//							 System.out.println(Thread.currentThread().getName() + " time " + time + " nano");
							
						}
					}
				}
//				System.out.println("I'm checking continue: " + this.getName());
				
//				long start = System.nanoTime();
				
				if (!checkContinue(this)) {
					break;
				}
				
//				long end = System.nanoTime();
//				float time = end - start;
//				
//				System.out.println("Time to check : " + time/1000 + " : " + this.getName());
			}
		}
	}
	
}
