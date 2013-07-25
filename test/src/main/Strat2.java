package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;




public class Strat2 extends Grid {
	
//private boolean playingForWhite = true;
	
	//private static final Object lock = new Object();
	private static Map<Integer, Map<Long, Long>> mtabTrans;
	
	private static synchronized void setTabTrans(int firstkey, long secondKey, Long info){
		
		//synchronized (lock) {
			Map<Long,Long> get = mtabTrans.get(firstkey);
			get.put(secondKey, info);
		//}
		
		
		
		
	}
	
	private static Long getInfo(int firstkey, long secondKey){
		//synchronized (lock) {
			//Map<Long,Long> get = mtabTrans.get(firstkey);
			//if(get.containsKey(secondKey))
				return mtabTrans.get(firstkey).get(secondKey);
			//else
				//return null;
		//}
	}
	
	
	
	
	public static void init2(){
		 mtabTrans = new HashMap<Integer, Map<Long,Long>>();
		for(int white = 1; white < 13; ++white){
			for(int black = 1; black < 13; ++black){
				mtabTrans.put((white<<5)+black, new HashMap<Long,Long>());
			}
				
		}
	}
	

	public Strat2(String str, int type, boolean myColor) {
		super(str, type, myColor);
		
	}

	public Strat2(Grid g, long mvt, boolean inverse) {
		super(g, mvt, inverse);
		playingWhite = !g.playingWhite;
		
	}
	
	protected void inverse(){
		super.inverse();
		playingWhite = !playingWhite;
	}

	private int massHeuristic() {
		int sumX = 0;
		int sumY = 0;
		int cpt = 0;

		for (int i = 0; i < 64; ++i) {
			if ((1L << i & mPions) != 0) {
				++cpt;
				sumX += i % 8;
				sumY += i / 8;
			}

		}
		sumX /= cpt;
		sumY /= cpt;
		int gap = 0;
		for (int i = 0; i < 64; ++i) {
			if ((1L << i & mPions) != 0) {
				gap += Math.pow(sumX - i % 8, 2) + Math.pow(sumY - i / 8, 2);
			}

		}

		return 1000000 - gap;

	}
	
	private int evaluateBlocking() {
		// System.out.println(mNbPions +" "+ mNbPionsAdv);
		int rapport = generatePossibleMvt().size() * 10000 / (mNbPions * 8);
		inverse();
		int rapportAdv = generatePossibleMvt().size() * 10000 / (mNbPions * 8);
		inverse();
		return rapport - rapportAdv;
	}

	private int negaMax(int lvl, int lvlsDone, int alpha, int beta) {
		int fkey;
		//System.out.println(playingWhite);
		if (playingWhite) {
			fkey = (mNbPions << 5) + mNbPionsAdv;
		} else {
			fkey = (mNbPionsAdv << 5) + mNbPions;
		}
		//Long info = mtabTrans.get(fkey).get( mPions | mPionsAdv);
		Long info = getInfo(fkey, mPions | mPionsAdv);
		if( info !=null){
		//if (info != null) {  //ETAT EXISTE <- CONSULTER
			
			if (info>>>32 <= lvlsDone) { //ON LA TROUVE AUN NIV INFERIEUR -> LE prendre
				//++nbRepris;
				if(lvlsDone % 2 == 0)
					return (int) ((info<<64)-lvlsDone+(info>>32));
				else
					return -(int) ((info<<64)-lvlsDone+(info>>32));
			}
			
			
		
		}
		
		if (isConnected())
			return Integer.MAX_VALUE - lvlsDone;
		if (advIsConnected())
			return INT_MIN_VALUE + lvlsDone;
		if (lvl == 0) {

			return massHeuristic() + evaluateBlocking() - lvlsDone;
		} else {

			// ETAT N'EXISTEPAS OU ON L'A TROUVE A UN NIV SUPERIEUR
			ArrayList<Long> moves = generatePossibleMvt();
			for (Long move : moves) {
				Strat2 advGrid = new Strat2(
						this, move, true);
				int val = -advGrid
						.negaMax(lvl - 1, lvlsDone + 1, -beta, -alpha);
				if (val > alpha) {
					alpha = val;
					/*if (lvlsDone % 2 == 1)
						setTabTrans(fkey, mPions | mPionsAdv,
								new TableTransInfo(alpha, lvlsDone));
					else
						setTabTrans(fkey, mPions | mPionsAdv,
								new TableTransInfo(-alpha, lvlsDone));*/
					if (alpha >= beta) {

						
						return alpha;
					}

				}

			}

			// TableTransInfo info = mtabTrans.

			if (lvlsDone % 2 == 0)
				setTabTrans(fkey, mPions | mPionsAdv, ((lvlsDone+1L))<<32|alpha);
			else
				setTabTrans(fkey, mPions | mPionsAdv, ((lvlsDone+1L)<<32)|-alpha);
			return alpha;

		}

	}
	
	//private static int nbRepris = 0;

	@Override
	protected long getBestMove(int lvl) {
	//	nbRepris = 0;
		long time = System.currentTimeMillis();
		int NB_THREADS = 4;
		ArrayList<Long> moves = generatePossibleMvt();
		Collections.shuffle(moves);
		ConcurrentLinkedQueue<Long> inQueue = new ConcurrentLinkedQueue<Long>(
				moves);
		//Stack<Long> stackMvts = new Stack<Long>();
		/*for (long mvt : moves) {
			try {
				inQueue.put(mvt);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/

		
		ExecutorService es = Executors.newFixedThreadPool(NB_THREADS);

		
		long bestMvt = inQueue.peek();
		//System.out.println("inQueue Size="+inQueue.size());
			
		LinkedBlockingQueue<WorkerRes> resQueue = new LinkedBlockingQueue<WorkerRes>(
				100);
		
		

		for (int i = 0; i < NB_THREADS && !inQueue.isEmpty(); ++i) {
			
			GridWorker worker = new GridWorker(this, lvl, INT_MIN_VALUE,
					Integer.MAX_VALUE, resQueue, inQueue);
			
			es.execute(worker);

		}

		
		GridHandler gh = new GridHandler(this, resQueue,
				bestMvt, moves.size());
		try {
			bestMvt = gh.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		/*Future<Long> best = es.submit(gh);
		try {
			bestMvt = best.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		es.shutdown();
		System.out.println(System.currentTimeMillis()-time);
		return bestMvt;
	}
	
	private class GridHandler implements Callable<Long> {
		LinkedBlockingQueue<WorkerRes> queue;
		
		Grid grid;
		long defMvt;
		int nbResults;
		

		private GridHandler(Grid g, LinkedBlockingQueue<WorkerRes> queue,
				long defaultMvt, int nbResults) {
			this.grid = g;
			this.queue = queue;
			this.defMvt = defaultMvt;
			this.nbResults = nbResults;
			
		}

		@Override
		public Long call() throws Exception {

			int alpha = INT_MIN_VALUE;
			int beta = Integer.MAX_VALUE;
			
			int val;
			long bestMvt=defMvt;
			
			
			int fkey;
			
			if (grid.playingWhite) {
				fkey = (grid.mNbPions << 5) + grid.mNbPionsAdv;
			} else {
				fkey = (grid.mNbPionsAdv << 5) + grid.mNbPions;
			}
			
			
			while(nbResults > 0){ 
				WorkerRes wr = queue.take();
				--nbResults;
				val = wr.value;
				 System.out.println((nbResults+1) + "-> "+val);
				if(val<0)
					System.out.println("WTF VAL < 0");
				if (val > alpha) {

					alpha = val;

					bestMvt = wr.move;
					if (alpha == beta) {

						
						return bestMvt;
						//break bigloop;

					}
					

				}
				
			} 
			setTabTrans(fkey, grid.mPions | grid.mPionsAdv, (long)alpha);
			return bestMvt;
		}

	}

	class GridWorker extends Thread {
		Grid oldGrid;
		int lvl;
		int alpha;
		int beta;
		
		
		LinkedBlockingQueue<WorkerRes> queue;
		ConcurrentLinkedQueue<Long> inQueue;

		GridWorker(Grid grid, int lvl, int alpha, int beta,
				LinkedBlockingQueue<WorkerRes> queue, ConcurrentLinkedQueue<Long> inQueue) {
			super();
			
			this.oldGrid = grid;
			this.alpha = -beta; // alpha;
			this.beta = -alpha;// beta;
			this.lvl = lvl;
			
			this.queue = queue;
			this.inQueue = inQueue;
			
			
		}

		

		public void run() {
			
			runLoop: while (true) {// !inQueue.isEmpty()) {
				loop: {
					
					Long move = inQueue.poll();
					if (move == null) {
						
						break runLoop;
					} else {
						Grid grid = new Strat2(oldGrid, move, true);
						int fkey;

						if (grid.playingWhite) {
							fkey = (grid.mNbPions << 5) + grid.mNbPionsAdv;
						} else {
							fkey = (grid.mNbPionsAdv << 5) + grid.mNbPions;
						}

						
						Long info = getInfo(fkey, grid.mPions | grid.mPionsAdv);
						if (info != null && ((info >>> 32) <= 1)) { // ETAT EXISTE <- CONSULTER
							//if  { // ON LA TROUVE AUN NIV
													// INFERIEUR -> LE prendre
								
									queue.add(new WorkerRes(
											(int) ((info << 64) - 1 + (info >> 32)),
											move, this));

									break loop;
								

						}else {
							ArrayList<Long> moves = grid.generatePossibleMvt();
							for (Long mvt : moves) {
								Strat2 advGrid = new Strat2(grid, mvt, true);
								int val = -advGrid.negaMax(lvl - 1, 2, -beta,
										-alpha);

								if (val > alpha) {
									alpha = val;
									if (alpha >= beta) {
										
										
										queue.add(new WorkerRes(-alpha, move,
												this));
										break loop;
										
									}
								}
							}

							
							setTabTrans(fkey, grid.mPions | grid.mPionsAdv,
									(1L << 32) | -alpha);
							
							
							queue.add(new WorkerRes(-alpha, move, this));
							break loop;
							
						}

					}

				}
			}
			
		}
		

	}

	class WorkerRes {
		int value;
		long move;
		Thread th;
		

		WorkerRes(int first, long second, Thread th) {
			super();
			this.value = first;
			this.move = second;
			this.th = th;
			
		}

	}

}
