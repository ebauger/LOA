package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class NMMassBlockTTWhioutThsV2 extends Grid {
	
//private boolean playingForWhite = true;
	
	private static final Object lock = new Object();
	private static Map<Integer, Map<Long, TableTransInfo>> mtabTrans;
	
	private static void setTabTrans(int firstkey, long secondKey, TableTransInfo info){
		Map<Long,TableTransInfo> get = mtabTrans.get(firstkey);
		synchronized (lock) {
			get.put(secondKey, info);
		}
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
				mtabTrans.put((white<<5)+black, new HashMap<Long,TableTransInfo>());
			}
				
		}
	}
	

	public NMMassBlockTTWhioutThsV2(String str, int type, boolean myColor) {
		super(str, type, myColor);
		
	}

	public NMMassBlockTTWhioutThsV2(Grid g, long mvt, boolean inverse) {
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
				++nbRepris;
				if(lvlsDone % 2 == 1)
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
				NMMassBlockTTWhioutThsV2 advGrid = new NMMassBlockTTWhioutThsV2(
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

			if (lvlsDone % 2 == 1)
				setTabTrans(fkey, mPions | mPionsAdv, new TableTransInfo(alpha,
						lvlsDone));
			else
				setTabTrans(fkey, mPions | mPionsAdv, new TableTransInfo(
						-alpha, lvlsDone));
			return alpha;

		}

	}
	
	private static int nbRepris = 0;

	@Override
	protected long getBestMove(int lvl) {
		nbRepris = 0;
		int fkey;
		//System.out.println(playingWhite);
		if (playingWhite) {
			fkey = (mNbPions << 5) + mNbPionsAdv;
		} else {
			fkey = (mNbPionsAdv << 5) + mNbPions;
		}
		
		ArrayList<Long> moves = generatePossibleMvt();
		long bestMvt = moves.get(0);
		int alpha = INT_MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		// int best = INT_MIN_VALUE;

		for (Long move : moves) {
			NMMassBlockTTWhioutThsV2 advGrid = new NMMassBlockTTWhioutThsV2(this,
					move, true);
			int val = -advGrid.negaMax(lvl, 0, -beta, -alpha);
			// System.out.println(val);
			if (val > alpha){
				alpha = val;
				bestMvt = move;
				/*setTabTrans(fkey, mPions | mPionsAdv, new TableTransInfo(val,
						0));*/
			
				
			}
			if (alpha >= beta) {
				return move;
				
			}
		}

		System.out.println("nb repris="+nbRepris);
		setTabTrans(fkey, mPions | mPionsAdv, new TableTransInfo(alpha,
				0));
		return bestMvt;
	}

}
