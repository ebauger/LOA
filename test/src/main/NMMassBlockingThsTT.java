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

import javax.swing.table.TableStringConverter;

public class NMMassBlockingThsTT extends Grid {
	
	
	
	private boolean playingForWhite = true;
	
	//private static final Object lock = new Object();
	private static Map<Integer, Map<Long, TableTransInfo>> mtabTrans;
	
	private static void setTabTrans(int firstkey, long secondKey, TableTransInfo info){
		Map<Long,TableTransInfo> get = mtabTrans.get(firstkey);
		/*synchronized (lock) {
			 get = mtabTrans.get(firstkey);
		}*/
		//System.out.println("Put");
		get.put(secondKey, info);
		
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
	
	
	
	public static void init2(){
		 mtabTrans = new ConcurrentHashMap<Integer, Map<Long,TableTransInfo>>();
		for(int white = 2; white < 13; ++white){
			for(int black = 2; black < 13; ++black){
				mtabTrans.put((white<<5)+black, new ConcurrentHashMap<Long,TableTransInfo>());
			}
				
		}
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

	public NMMassBlockingThsTT(String str, int type, boolean myColor) {
		super(str, type, myColor);
		playingForWhite = myColor;
	}

	public NMMassBlockingThsTT(Grid g, long mvt, boolean inverse) {
		super(g, mvt, inverse);
		playingForWhite = !playingForWhite;
	}
	
	protected void inverse(){
		super.inverse();
		playingForWhite = !playingForWhite;
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
		if (isConnected())
			return Integer.MAX_VALUE - lvlsDone;
		if (advIsConnected())
			return INT_MIN_VALUE + lvlsDone;
		if (lvl == 0) {

			return massHeuristic() + evaluateBlocking() - lvlsDone;
		} else {
			ArrayList<Long> moves = generatePossibleMvt();
			for (Long move : moves) {
				NMMassBlockingThsTT advGrid = new NMMassBlockingThsTT(
						this, move, true);
				int fkey;
				if (advGrid.playingForWhite) {
					fkey = (advGrid.mNbPions << 5) + advGrid.mNbPionsAdv;
				} else {
					fkey = (advGrid.mNbPionsAdv << 5) + advGrid.mNbPions;
				}
				TableTransInfo info = getInfoTT(fkey, mPions | mPionsAdv);
				//TableTransInfo info = mtabTrans.
				
				
				if (info != null) {  //ETAT EXISTE <- CONSULTER
					if (info.lvl < lvl) {  //NIVEAU INFERIEUR -> CLACULER AVEC 
						int val = -advGrid
								.negaMax(lvl - 1, lvlsDone + 1, -beta, -alpha);
						if (val > alpha) {
							alpha = val;
							setTabTrans(fkey, mPions | mPionsAdv, new TableTransInfo(alpha, lvlsDone));
						}
						if (val >= beta){
							return val;
						}
						
					}else{ //NIVEAU SUPERIEUR
						
						return info.alpha+lvl-info.lvl;
					}
				
				
				}else{ // ETAT N'EXISTEPAS -> AJOUTER
					int val = -advGrid
							.negaMax(lvl - 1, lvlsDone + 1, -beta, -alpha);
					if (val > alpha) {
						alpha = val;
						setTabTrans(fkey, mPions | mPionsAdv, new TableTransInfo(alpha, lvlsDone));
					}
					if (val >= beta){
						return val;
					}
				}
			}
		}

		return alpha;
	}

	@Override
	protected long getBestMove(int lvl) {
		int NB_THREADS = 4;
		ArrayList<Long> moves = generatePossibleMvt();
		Collections.shuffle(moves);
		Stack<Long> stackMvts = new Stack<Long>();
		for (long mvt : moves) {
			stackMvts.push(mvt);
		}

		// ArrayList<GridWorker> workers = new ArrayList<GridWorker>();
		ExecutorService es = Executors.newFixedThreadPool(NB_THREADS);

		long bestMvt = stackMvts.peek();

		LinkedBlockingDeque<WorkerRes> resQueue = new LinkedBlockingDeque<WorkerRes>(
				100);
		

		for (int i = 0; i < NB_THREADS && !stackMvts.isEmpty(); ++i) {

			GridWorker worker = new GridWorker(this, lvl, INT_MIN_VALUE,
					Integer.MAX_VALUE, stackMvts.pop(), resQueue);
			
			es.execute(worker);

		}

		// resHandling.setPriority(Thread.MIN_PRIORITY);;
		GridHandler gh = new GridHandler(this, resQueue,
				stackMvts, es, lvl);
		try {
			bestMvt = gh.call();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*Future<Long> best = es.submit(new GridHandler(this, resQueue,
				stackMvts, es, lvl));
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
		// System.out.println("Shutingdown");

		return bestMvt;
	}

	private class GridHandler implements Callable<Long> {
		LinkedBlockingDeque<WorkerRes> queue;
		Stack<Long> moves;
		ExecutorService es;
		Grid grid;
		int lvl;

		private GridHandler(Grid g, LinkedBlockingDeque<WorkerRes> queue,
				Stack<Long> moves, ExecutorService es, int lvl) {
			this.grid = g;
			this.queue = queue;
			this.moves = moves;
			this.es = es;
			this.lvl = lvl;
		}

		@Override
		public Long call() throws Exception {

			int alpha = INT_MIN_VALUE;
			int beta = Integer.MAX_VALUE;
			boolean done = false;
			int val;
			long bestMvt = moves.peek();
			int nbThreads = 4;
			bigloop: while (!done) {
				WorkerRes wr = queue.take();
				val = wr.value;
				// System.out.println(val);
				if (val == beta) {

					bestMvt = wr.move;

					break bigloop;

				}
				if (val > alpha) {

					alpha = val;

					bestMvt = wr.move;

				}
				if (!moves.isEmpty()) {
					// System.out.println("retrieving mvt, ask for renewing");
					/*
					 * ((GridWorker) wr.th) .setMove(grid, alpha, beta,
					 * moves.pop());
					 */
					es.execute(new GridWorker(grid, lvl, alpha, beta, moves
							.pop(), queue));

				} else {
					// System.out.println("no more mvts, ask for interrupting");
					// wr.th.interrupt();
					--nbThreads;
				}
				if (nbThreads == 0)
					done = true;
			}

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
			this.grid = new StratMass1(grid, mvt, true);
			this.alpha = -beta; // alpha;
			this.beta = -alpha;// beta;
			this.lvl = lvl;
			this.movement = mvt;
			this.queue = queue;
			// System.out.println("TH created");
		}

		void setMove(Grid grid, int alpha, int beta, long mvt) {
			this.grid = new StratMass1(grid, mvt, true);
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

				ArrayList<Long> moves = grid.generatePossibleMvt();
				for (Long move : moves) {
					NMMassBlockingThsTT advGrid = new NMMassBlockingThsTT(grid,
							move, true);
					int fkey;
					if (advGrid.playingForWhite) {
						fkey = (advGrid.mNbPions << 5) + advGrid.mNbPionsAdv;
					} else {
						fkey = (advGrid.mNbPionsAdv << 5) + advGrid.mNbPions;
					}
					TableTransInfo info = getInfoTT(fkey, mPions | mPionsAdv);
					if (info != null) {
						if (info.lvl < lvl) {
							
							try {
								int val = -advGrid
										.negaMax(lvl - 1, 1, -beta, -alpha);
								if (val > alpha) {
									alpha = val;
									setTabTrans(fkey, mPions | mPionsAdv, new TableTransInfo(alpha, 1));
								}
								if (val >= beta){
									queue.put(new WorkerRes(val, movement, this));
									break loop;
								}
								
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else {
							int val = -advGrid.negaMax(lvl - 1, 1, -beta, -alpha);
							if (val >= beta) {
								try {
									queue.put(new WorkerRes(info.alpha, movement, this));
									break loop;
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							
						}
					}
					else{
						try {
							int val = -advGrid
									.negaMax(lvl - 1, 1, -beta, -alpha);
							if (val > alpha) {
								alpha = val;
								setTabTrans(fkey, mPions | mPionsAdv, new TableTransInfo(alpha, 1));
							}
							if (val >= beta){
								queue.put(new WorkerRes(val, movement, this));
								break loop;
							}
							
						} catch (InterruptedException e) {
							e.printStackTrace();
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
