package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;




public class NMMassBlockTTThs extends Grid {
	
//private boolean playingForWhite = true;
	
	private static final Object lock = new Object();
	private static Map<Integer, Map<Long, TableTransInfo>> mtabTrans;
	
	private static void setTabTrans(int firstkey, long secondKey, TableTransInfo info){
		Map<Long,TableTransInfo> get = mtabTrans.get(firstkey);
		//synchronized (lock) {
		
			get.put(secondKey, info);
		//}
		//System.out.println("Put");
		
		
	}
	
	private static TableTransInfo getInfoTT(int firstkey, long secondKey){
		Map<Long,TableTransInfo> get = mtabTrans.get(firstkey);
		/*synchronized (lock) {
			 get = (ConcurrentHashMap<Long, TableTransInfo>) mtabTrans.get(firstkey);
		}*/
		//System.out.println("get");
		if(get.containsKey(secondKey))
			return get.get(secondKey);
		else
			return null;
	}
	 private class TableTransInfo{
		private int alpha;
		//private int beta;
		private int lvl;
		
		private TableTransInfo(int alpha, int lvl){
			this.alpha = alpha;
			//this.beta = beta;
			this.lvl = lvl;
		}
		
	}
	
	
	public static void init2(){
		 mtabTrans = new HashMap<Integer, Map<Long,TableTransInfo>>();
		for(int white = 1; white < 13; ++white){
			for(int black = 1; black < 13; ++black){
				mtabTrans.put((white<<5)+black, new ConcurrentHashMap<Long,TableTransInfo>());
			}
				
		}
	}
	

	public NMMassBlockTTThs(String str, int type, boolean myColor) {
		super(str, type, myColor);
		
	}

	public NMMassBlockTTThs(Grid g, long mvt, boolean inverse) {
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
		TableTransInfo info = getInfoTT(fkey, mPions | mPionsAdv);
		if (info != null) {  //ETAT EXISTE <- CONSULTER
			if (info.lvl <= lvlsDone) { //ON LA TROUVE AUN NIV INFERIEUR -> LE prendre
				//++nbRepris;
				if(lvlsDone % 2 == 0)
					return info.alpha-lvlsDone+info.lvl;
				else
					return -info.alpha+lvlsDone-info.lvl;
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
				NMMassBlockTTThs advGrid = new NMMassBlockTTThs(
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
				setTabTrans(fkey, mPions | mPionsAdv, new TableTransInfo(alpha,
						lvlsDone+1));
			else
				setTabTrans(fkey, mPions | mPionsAdv, new TableTransInfo(
						-alpha, lvlsDone+1));
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
		Stack<Long> stackMvts = new Stack<Long>();
		for (long mvt : moves) {
			stackMvts.push(mvt);
		}

		
		ExecutorService es = Executors.newFixedThreadPool(NB_THREADS);

		long bestMvt = stackMvts.peek();

		LinkedBlockingDeque<WorkerRes> resQueue = new LinkedBlockingDeque<WorkerRes>(
				100);
		

		for (int i = 0; i < NB_THREADS && !stackMvts.isEmpty(); ++i) {
			
			GridWorker worker = new GridWorker(this, lvl, INT_MIN_VALUE,
					Integer.MAX_VALUE, stackMvts.pop() , resQueue);
			
			es.execute(worker);

		}

		
		GridHandler gh = new GridHandler(this, resQueue,
				stackMvts, es, lvl, NB_THREADS);
		try {
			bestMvt = gh.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		

		es.shutdown();
		System.out.println(System.currentTimeMillis()-time);
		return bestMvt;
	}
	
	private class GridHandler implements Callable<Long> {
		LinkedBlockingDeque<WorkerRes> queue;
		Stack<Long> moves;
		ExecutorService es;
		Grid grid;
		int lvl;
		int nbThreads;

		private GridHandler(Grid g, LinkedBlockingDeque<WorkerRes> queue,
				Stack<Long> moves, ExecutorService es, int lvl, int nb_threads) {
			this.grid = g;
			this.queue = queue;
			this.moves = moves;
			this.es = es;
			this.lvl = lvl;
			this.nbThreads = nb_threads;
		}

		@Override
		public Long call() throws Exception {

			int alpha = INT_MIN_VALUE;
			int beta = Integer.MAX_VALUE;
			boolean done = false;
			int val;
			long bestMvt = moves.peek();
			
			//int nbThreads = 4;
			//long time = System.currentTimeMillis();
			int fkey;
			//System.out.println(playingWhite);
			if (grid.playingWhite) {
				fkey = (grid.mNbPions << 5) + grid.mNbPionsAdv;
			} else {
				fkey = (grid.mNbPionsAdv << 5) + grid.mNbPions;
			}
			
			
			bigloop: while (!done) {
				WorkerRes wr = queue.take();
				val = wr.value;
				 System.out.println(val);
				
				if (val > alpha) {

					alpha = val;

					bestMvt = wr.move;
					if (alpha == beta) {

						//bestMvt = wr.move;
						return bestMvt;
						//break bigloop;

					}

				}
				if (!moves.isEmpty()) {
					// System.out.println("retrieving mvt, ask for renewing");
					
					  /*((GridWorker) wr.th) .setMove(grid, alpha, beta,
					  moves.pop());*/
					 
					
					es.execute(new GridWorker(grid, lvl, alpha, beta, moves
							.pop() , queue));

				} else {
					// System.out.println("no more mvts, ask for interrupting");
					// wr.th.interrupt();
					--nbThreads;
				}
				if (nbThreads == 0)
					done = true;
			}
			setTabTrans(fkey, grid.mPions | grid.mPionsAdv, new TableTransInfo(alpha,
					0));
			return bestMvt;
		}

	}

	class GridWorker extends Thread {
		Grid grid;
		int lvl;
		int alpha;
		int beta;
		long movement;
		LinkedBlockingDeque<WorkerRes> queue;

		GridWorker(Grid grid, int lvl, int alpha, int beta, long mvt,
				LinkedBlockingDeque<WorkerRes> queue) {
			super();
			this.grid = new  NMMassBlockTTThs(grid, mvt, true);
			this.alpha = -beta; // alpha;
			this.beta = -alpha;// beta;
			this.lvl = lvl;
			this.movement = mvt;
			this.queue = queue;
			
			
		}

		void setMove(Grid grid, int alpha, int beta, long mvt) {
			this.grid = new  NMMassBlockTTThs(grid, mvt, true);
			this.alpha = -beta; // alpha;
			this.beta = -alpha;// beta;
			this.movement = mvt;
			// System.out.println("TH renewed");
			run();
		}

		void setAlpha(int alpha) {
			this.alpha = alpha;
		}

		public void run() {
			loop: {
			int fkey;
			//System.out.println(playingWhite);
			if (grid.playingWhite) {
				fkey = (grid.mNbPions << 5) + grid.mNbPionsAdv;
			} else {
				fkey = (grid.mNbPionsAdv << 5) + grid.mNbPions;
			}
			
			TableTransInfo info = getInfoTT(fkey, grid.mPions | grid.mPionsAdv);
			
			if (info != null) {  //ETAT EXISTE <- CONSULTER
				if (info.lvl <= 1) { //ON LA TROUVE AUN NIV INFERIEUR -> LE prendre
					try {
						// System.out.println("out "+-beta);
						queue.put(new WorkerRes(-info.alpha+1-info.lvl, movement, this));
						break loop;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
				
				
			
			}
			/*if (isConnected()) //NE PAS METTRE UN NIVEAU TOP BAS
				return Integer.MAX_VALUE - 1;
			if (advIsConnected())
				return INT_MIN_VALUE + +1;
			if (lvl == 0) {

				return massHeuristic() + evaluateBlocking() - lvlsDone;
			}*/ else {
				ArrayList<Long> moves = grid.generatePossibleMvt();
				for (Long move : moves) {
					 NMMassBlockTTThs advGrid = new NMMassBlockTTThs(
							grid, move, true);
					int val = -advGrid.negaMax(lvl-1, 2, -beta, -alpha);
					
					if (val > alpha) {
						alpha = val;
						if (alpha >= beta) {
							try {
								
								queue.put(new WorkerRes(-alpha, movement, this));
								break loop;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}

				try {
					setTabTrans(fkey, grid.mPions | grid.mPionsAdv, new TableTransInfo(
							-alpha, 1));
					queue.put(new WorkerRes(-alpha, movement, this));
					break loop;
				} catch (InterruptedException e) {
					e.printStackTrace();
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
