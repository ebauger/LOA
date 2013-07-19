package main;

import java.util.ArrayList;

public class StratMass1 extends Grid{
	
	public StratMass1(String str, int type, int myColor) {
		super(str, type, myColor);
	}
	public StratMass1(Grid g, long mvt, boolean inverse) {
		super(g, mvt, inverse);
	}

	
	public static final int INT_MIN_VALUE = Integer.MIN_VALUE + 1;
	
	@Override
	public long getBestMove(int lvl) {
		ArrayList<Long> moves = generatePossibleMvt(); //Moves de MAX
		
		
		
		int best = INT_MIN_VALUE;
		long bestMvt = moves.get(0);
		int alpha = INT_MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		loop : for(long mvt: moves){
			
			Grid advGrid = new StratMass1(this, mvt, true); //
			
			//System.out.println(mvt);
			int val = -((StratMass1)advGrid).getBestMove(lvl-1, 0, -beta, -alpha); 
			//System.out.println(val);
			if(val == Integer.MAX_VALUE){
				bestMvt = mvt;
				
				break loop;
				
			}else if(val > best){
				best = val;
				bestMvt = mvt;
				if(best > alpha){
					alpha = best;
					
					if(alpha >= beta){
						bestMvt = mvt;
						break loop;
					}
				}
			}
			//System.out.println(alpha+" "+best);
			//System.out.println("-----");
			
		}
		return bestMvt;
	}

	private int massHeuristic(){
		int sumX = 0;
		int sumY = 0;
		//int sumXA = 0;
		//int sumYA = 0;
		int cpt = 0;
		for(int i = 0; i< 64; ++i){
			if((1L << i & mPionsAdv) != 0){
				++cpt;
				sumX += i%8;
				sumY += i/8;
			}
			
		}
		int massX = sumX / cpt;
		int massY = sumY / cpt;
		int gap = 0;
		for(int i = 0; i< 64; ++i){
			if((1L << i & mPionsAdv) != 0){
				gap += 1000 - Math.sqrt(Math.pow(massX- i%8, 2) + Math.pow(massY- i/8, 2));
				//gap += Math.abs(massX- i%8) + Math.abs(massY- i/8);
			}
			
		}
		
		return gap;
		
	}

	
	
	private int getBestMove( int lvl, int lvlsDone, int alpha, int beta){

		boolean connect = isConnected();
		boolean advConnect = advIsConnected();
		if(connect && advConnect)
			return 50 - lvlsDone;
		if(connect){
			//System.out.println("Connect");
			return Integer.MAX_VALUE - lvlsDone;
			
		}
		if(advConnect){
			//System.out.println("Connect");
			return INT_MIN_VALUE + lvlsDone;
		}
		if(lvl == 0){
			//inverse();
			//int mvtsAdv = generatePossibleMvt().size();
			//inverse();
			//return generatePossibleMvt().size();
			return -massHeuristic();
		}
		

		else{
			int best = INT_MIN_VALUE;
		
			ArrayList<Long> moves = generatePossibleMvt();
			
			for(Long mvt: moves){
				Grid advGrid = new StratMass1(this, mvt, true);
				int val = -((StratMass1)advGrid).getBestMove(lvl-1, lvlsDone+1, -beta, -alpha);
				if(val > best){
					best = val;
					if(best > alpha){
						alpha = best;
						if(alpha >= beta){
							return best;
						}
					}
				}
			}
			return best;
			
		}
//		fonction ALPHABETA(P, A, B) /* A < B */
//		   si P est une feuille alors
//		       retourner la valeur de P
//		   sinon
//		       Meilleur = ÐINFINI
//		       pour tout fils Pi de P faire
//		           Val = -ALPHABETA(Pi,-B,-A)
//		           si Val > Meilleur alors
//		               Meilleur = Val
//		               si Meilleur > A alors
//		                      A = Meilleur
//		                   si A ³ B alors
//		                       retourner Meilleur
//		                   finsi
//		               finsi
//		           finsi 
//		       finpour 
//		       retourner Meilleur
//		   finsi
//		fin

	}

	

}
