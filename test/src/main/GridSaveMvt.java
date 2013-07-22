/**
 * 
 */
package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * @author Charly
 *
 */
public abstract class GridSaveMvt extends Grid {

	
//	protected static ArrayList<ArrayList<ArrayList<HashMap<Long, ArrayList<Long>>>>> moves ;
	
	

	public static void init()
	{
		Grid.init();
				
//		moves = new ArrayList<ArrayList<ArrayList<HashMap<Long,ArrayList<Long>>>>>();
//		
//		for (int i = 0; i < 12; i++) {
//			ArrayList<ArrayList<HashMap<Long, ArrayList<Long>>>> arr = new ArrayList<ArrayList<HashMap<Long, ArrayList<Long>>>>();
//			for (int j = 0; j < 12; j++) {
//				ArrayList<HashMap<Long, ArrayList<Long>>> arr2 = new ArrayList<HashMap<Long,ArrayList<Long>>>();
//				for (int k = 0; k < Main.MAX_MVT_SAVE_LVL ; k++) {
//					arr2.add(new HashMap<Long, ArrayList<Long>>());
//				}
//				arr.add(arr2);
//			}
//			moves.add(arr);
//		}
		
	}
	
	public GridSaveMvt(Grid g) {
		super(g);
		this.nbMPions = g.nbMPions;
		this.nbPionsAdv = g.nbPionsAdv;
	}

	public GridSaveMvt(String str, int type, int myColor) {
		super(str, type, myColor);
	}

	@Override
	public ArrayList<Long> generatePossibleMvt() {
		
//		long key = mPions | mPionsAdv;
		
		ArrayList<Long> mvt = null;
		
//		 if(!moves.get(nbMPions-1).isEmpty())
//			 if(!moves.get(nbMPions-1).get(nbPionsAdv-1).isEmpty())
//				 if(!moves.get(nbMPions-1).get(nbPionsAdv-1).get(lvlparcouru%(Main.MAX_MVT_SAVE_LVL)).isEmpty())
//						mvt = moves.get(nbMPions-1).get(nbPionsAdv-1).get(lvlparcouru%(Main.MAX_MVT_SAVE_LVL)).get(key);
//		
//		 
		if(mvt == null){
			
			mvt = super.generatePossibleMvt();
						
//			moves.get(nbMPions-1).get(nbPionsAdv-1).get(lvlparcouru%(Main.MAX_MVT_SAVE_LVL)).put(key, mvt);
			
		}
			
		return mvt;
		
	}
	
	public void MakeMvtAndUpdate(long move,boolean clean)

	{
		long from = mPions & move;
	
		long to = move ^ from;
		
		 mPions ^= move;
		
		 if(mPions == 0L)
		 {
			 System.out.println(Long.toBinaryString(mPions));
			 System.out.println(Long.toBinaryString(move));
			 System.out.println(Long.toBinaryString(from));
			 System.out.println(Long.toBinaryString(to));
			 
		 }

		long mPionsAdvTmp = mPionsAdv & (-1L ^ to);
		
		
		++lvlparcouru;
		
//		if(lvlparcouru >= Main.MAX_MVT_SAVE_LVL && clean)
//			if(!moves.get(nbMPions-1).isEmpty())
//				 if(!moves.get(nbMPions-1).get(nbPionsAdv-1).isEmpty())
//					 if(!moves.get(nbMPions-1).get(nbPionsAdv-1).get(lvlparcouru%(Main.MAX_MVT_SAVE_LVL)).isEmpty())
//						 	moves.get(nbMPions-1).get(nbPionsAdv-1).get(lvlparcouru%Main.MAX_MVT_SAVE_LVL).clear();
//		
		if(mPionsAdvTmp != mPionsAdv)
		{
			--nbPionsAdv;
//			if(clean)
//			{
//				for (HashMap<Long, ArrayList<Long>> hs : moves.get(nbMPions-1).get(nbPionsAdv-1)) {
//					hs.clear();
//				} 
//			}
		}
			
		mPionsAdv = mPionsAdvTmp;
		
	}

//	public void coupAdvAndUpdate(long move,boolean clean) {
//		
//		long from = mPionsAdv & move;
//		
//		long to = move ^ from;
//		
//		mPionsAdv ^= move;
//		
//
//		long mPionsTmp = mPions & (-1L ^ to);
//		
//		++lvlparcouru;
//		
////		if(lvlparcouru >= Main.MAX_MVT_SAVE_LVL && clean)
////			if(!moves.get(nbPionsAdv-1).isEmpty())
////				 if(!moves.get(nbPionsAdv-1).get(nbMPions-1).isEmpty())
////					 if(!moves.get(nbPionsAdv-1).get(nbMPions-1).get(lvlparcouru%(Main.MAX_MVT_SAVE_LVL)).isEmpty())
////						 moves.get(nbPionsAdv-1).get(nbMPions-1).get(lvlparcouru%Main.MAX_MVT_SAVE_LVL).clear();
////		
//		if(mPionsTmp != mPions)
//		{
//			--nbMPions;
////			if(clean)
////			{
////				for (HashMap<Long, ArrayList<Long>> hs : moves.get(nbPionsAdv-1).get(nbMPions-1)) {
////					hs.clear();
////				} 
////			}
//		}
//		
//		
//
//		mPions = mPionsTmp;
//		
//		
//		
//		
//	}

	@Override
	protected void inverse() {
		super.inverse();
		
		int nbMPionsTmp = nbMPions;
		
		nbMPionsTmp = nbPionsAdv;
		nbPionsAdv = nbMPionsTmp;
		
	}

}
