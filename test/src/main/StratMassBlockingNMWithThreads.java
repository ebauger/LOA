package main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class StratMassBlockingNMWithThreads extends Grid {
	
	

	public StratMassBlockingNMWithThreads(String str, int type, int myColor) {
		super(str, type, myColor);
	}
	
	public StratMassBlockingNMWithThreads(Grid g, long mvt, boolean inverse) {
		super(g, mvt, inverse);
	}
	
	private int massHeuristic(){
		int sumX = 0;
		int sumY = 0;
		int cpt = 0;
		
		for(int i = 0; i< 64; ++i){
			if((1L << i & mPions) != 0){
				++cpt;
				sumX += i%8;
				sumY += i/8;
			}
			
		}
		sumX/= cpt;
		sumY/= cpt;
		int gap = 0;
		for(int i = 0; i< 64; ++i){
			if((1L << i & mPions) != 0){
				gap += Math.pow(sumX- i%8, 2) + Math.pow(sumY- i/8, 2);
			}
			
		}
		
		return 1000000-gap;
		
	}
	
	private int evaluateBlocking(){
		//System.out.println(mNbPions +" "+ mNbPionsAdv);
		int rapport = generatePossibleMvt().size()*100/(mNbPions*8);
		inverse();
		int rapportAdv = generatePossibleMvt().size()*100/(mNbPions*8);
		inverse();
		return rapport - rapportAdv;
	}
	
	private int negaMax(int lvl, int lvlsDone, int alpha, int beta){
		if(isConnected()) return Integer.MAX_VALUE-lvlsDone;
		if(advIsConnected()) return INT_MIN_VALUE+lvlsDone;
		if(lvl == 0){
			
			return massHeuristic()+evaluateBlocking()-lvlsDone;
		}
		else{
			ArrayList<Long> moves = generatePossibleMvt();
			for(Long move: moves){
				StratMassBlockingNMWithThreads advGrid = new StratMassBlockingNMWithThreads(this, move, true);
				int val = -advGrid.negaMax(lvl-1, lvlsDone+1, -beta, -alpha);
				if(val >= beta) return val;
				if(val > alpha){ 
					alpha = val;
					
				}
			}
		}
		
		/*function negamax(node, depth, α, β, color)
	    if node is a terminal node or depth = 0
	        return color * the heuristic value of node
	    else
	        foreach child of node
	            val := -negamax(child, depth - 1, -β, -α, -color)
	            if val ≥ β
	                return val
	            if val > α
	                α := val
	        return α*/
		
		return alpha;
	}
	
	
	
	@Override
	protected long getBestMove(int lvl) {
		
		
		int NB_THREADS = 3;
		ArrayList<Long> moves = generatePossibleMvt();
		Collections.shuffle(moves);
		Stack<Long> stackMvts = new Stack<Long>();
		for(long mvt: moves){
			stackMvts.push(mvt);
		}
		
		ArrayList<Future<WorkerRes>> futureResults = new ArrayList<Future<WorkerRes>>();
		ArrayList<GridWorker> workers = new ArrayList<GridWorker>();
		
		ExecutorService es = Executors.newFixedThreadPool(NB_THREADS);
		
		
		long bestMvt = stackMvts.peek();
		int alpha = INT_MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		
		 for(int i= 0; i < NB_THREADS && !stackMvts.isEmpty(); ++i){
		    	
		    	GridWorker worker = new GridWorker(this, lvl, alpha, beta ,stackMvts.pop());
		    	workers.add(worker);
		    	futureResults.add(es.submit(worker));
		    
		    }
		
		
		
		/*for(Long move: moves){
			StratSimpleNegaMaxMassThreads advGrid = new StratSimpleNegaMaxMassThreads(this, move, true);
			int val = -advGrid.negaMax(lvl, 0, -beta, -alpha);
			System.out.println(val);
			if(val >= beta) return move;
			if(val > alpha){ 
				alpha = val;
				bestMvt = move;
			}
		}*/
		// System.out.println("start "+futureResults.size()+" "+workers.size());
		boolean done = false;
		int val = 0;
		bigloop: while (!done) {
			
			for (int idx = 0; idx < NB_THREADS; ++idx) {
				// System.out.println(idx);
				if (futureResults.get(idx).isDone()) {
					//System.out.println(idx + " done");
					try {
						//val = -futureResults.get(idx).get();
						val = futureResults.get(idx).get().value;
						//System.out.println(idx + "->" + val);
					} catch (InterruptedException e) {
						System.out.println("INTERRUPTED EXCEPTION");
						e.printStackTrace();
					} catch (ExecutionException e) {
						System.out.println("EXECUTION EXCEPTION");
						e.printStackTrace();
					}
					
					if (val == beta) {
						//bestMvt = workers.get(idx).getMovement();
						try {
							bestMvt = futureResults.get(idx).get().move;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//System.out.println("val "+val+" >= beta "+beta);
						break bigloop;

					}
					if (val > alpha) {
						//System.out.println("val "+val+" > alpha "+alpha);
						alpha = val;
						//bestMvt = workers.get(idx).getMovement();
						try {
							bestMvt = futureResults.get(idx).get().move;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						 /* for (GridWorker worker : workers) {
						  worker.setAlpha(val);
						 
						  }*/
						 

					}
					if (!stackMvts.isEmpty()) {
						futureResults.remove(idx);
						workers.remove(idx);
						GridWorker worker = new GridWorker(this, lvl, alpha,
								beta, stackMvts.pop());
						workers.add(idx, worker);
						futureResults.add(es.submit(worker));

						// break forloop;
					} else {
						// System.out.println("removing "+idx);
						futureResults.remove(idx);
						workers.remove(idx);
						// break forloop;
						--NB_THREADS;
					}

				}/*else if(es.isShutdown()){
					System.out.println("shutdown");
				}else if(es.isTerminated()){
					System.out.println("terminated");
				}else if(es.)*/

				// ++idx;
			}

			if (futureResults.size() == 0)
				done = true;
		}

		es.shutdown();
		
		
		//printBits(bestMvt);
		return bestMvt;
	}
	
	private class GridWorker implements Callable<WorkerRes>{
		  Grid grid;
		  int lvl;
		  int alpha;
		  int beta;
		  long movement;
		 
		  GridWorker(Grid grid, int lvl, int alpha, int beta, long mvt) {
			  this.grid = new StratMass1(grid,mvt,true);
			  this.alpha = -beta; // alpha;
			  this.beta =-alpha;// beta;
			  this.lvl = lvl;
			  this.movement = mvt;
		  }
		  
		 /* public long getMovement(){
			  return movement;
		  }*/
		  /*public void setAlpha(int alpha){
			  this.alpha = alpha;
		  }*/
		
		  public WorkerRes call() {
			  /*if(grid.isConnected()) return Integer.MAX_VALUE;
				if(grid.advIsConnected()) return INT_MIN_VALUE;
				if(lvl == 0){
					return massHeuristic();
				}*/
				//{
					ArrayList<Long> moves = grid.generatePossibleMvt();
					for(Long move: moves){
						StratMassBlockingNMWithThreads advGrid = new StratMassBlockingNMWithThreads(grid, move, true);
						int val = -advGrid.negaMax(lvl-1, 1, -beta, -alpha);
						if(val >= beta){
							/*System.out.println(-val);
							printBits(movement);
							return val;*/
							return new WorkerRes(-val, movement);
						}
						if(val > alpha){ 
							alpha = val;
							
						}
					}
				//}
				
				/*function negamax(node, depth, α, β, color)
			    if node is a terminal node or depth = 0
			        return color * the heuristic value of node
			    else
			        foreach child of node
			            val := -negamax(child, depth - 1, -β, -α, -color)
			            if val ≥ β
			                return val
			            if val > α
			                α := val
			        return α*/
				/*System.out.println(-alpha);
				printBits(movement);
				return alpha;*/
				return new WorkerRes(-alpha, movement);
		}
	}
	
	private class WorkerRes {
	    private int value;
	    private long move;

	    private WorkerRes(int first, long second) {
	    	super();
	    	this.value = first;
	    	this.move = second;
	    }

	   /* private int hashCode() {
	    	int hashFirst = first != null ? first.hashCode() : 0;
	    	int hashSecond = second != null ? second.hashCode() : 0;

	    	return (hashFirst + hashSecond) * hashSecond + hashFirst;
	    }

	    public boolean equals(Object other) {
	    	if (other instanceof Pair) {
	    		Pair otherPair = (Pair) other;
	    		return 
	    		((  this.first == otherPair.first ||
	    			( this.first != null && otherPair.first != null &&
	    			  this.first.equals(otherPair.first))) &&
	    		 (	this.second == otherPair.second ||
	    			( this.second != null && otherPair.second != null &&
	    			  this.second.equals(otherPair.second))) );
	    	}

	    	return false;
	    }

	    public String toString()
	    { 
	           return "(" + first + ", " + second + ")"; 
	    }*/

	   /* public int getFirst() {
	    	return value;
	    }*/

	    /*public void setFirst(A first) {
	    	this.first = first;
	    }*/

	   /* public long getSecond() {
	    	return move;
	    }*/

	    /*public void setSecond(B second) {
	    	this.second = second;
	    }*/
	}

}