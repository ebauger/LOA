package main;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.sql.PooledConnection;

public class Grid {

	private static Map<Integer,Long> MASK;
	
//	private static Map<Byte,Integer> MASK_NB_BITS;
	
	private long mPions = 0;
	private long mPionsAdv = 0;
	
	private static final int LINES = 0;
	private static final int COLUMNS = 1;
	private static final int DIAGONAL_RIGHT = 2;  // '/'
	private static final int DIAGONAL_LEFT = 3; // '\'
	
	//byte[][] mOrthogonals  = {{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0}};
	//byte[][] mDiagonals = {{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};
	byte[][] mLinesOfActon;
	private static final Map<Integer, ReferencedByte> LOA_LIGNES = new HashMap<Integer, ReferencedByte>(64);
	private static final Map<Integer, ReferencedByte> LOA_COLUMNS = new HashMap<Integer, ReferencedByte>(64);
	private static final Map<Integer, ReferencedByte> LOA_DIAGONAL_RIGHT = new HashMap<Integer, ReferencedByte>(64);
	private static final Map<Integer, ReferencedByte> LOA_DIAGONAL_LEFT = new HashMap<Integer, ReferencedByte>(64);
	
	
	private static final Map<Long, Long> MASK_MOVEMENT = new HashMap<Long, Long>(2016);
	
	public Grid(String str){
		init();
		int offset = 0;
		for (char c : str.toCharArray()) {
			if(c == '1'){
				mPions |= (long)1<<63-offset;
//				++mOrthogonals[LINES][(63-offset)/8]; 
//				++mOrthogonals[COLUMNS][(63-offset)%8];
//				++mDiagonals[DIAGONAL_RIGHT][((63-offset)/8) + ((63-offset)%8)];
//				++mDiagonals[DIAGONAL_LEFT][((63-offset)/8) + (7-((63-offset)%8))];
//				++mLinesOfActon[63-offset][LINES];
//				++mLinesOfActon[63-offset][COLUMNS];
//				++mLinesOfActon[63-offset][DIAGONAL_RIGHT];
//				++mLinesOfActon[63-offset][DIAGONAL_LEFT];
				++LOA_LIGNES.get(63-offset).value;
				++LOA_COLUMNS.get(63-offset).value;
				++LOA_DIAGONAL_RIGHT.get(63-offset).value;
				++LOA_DIAGONAL_LEFT.get(63-offset).value;
				
				
				
				
			}	
			else if(c == '2')
			{
				mPionsAdv  |= (long)1<<63-offset;
//				++mOrthogonals[LINES][(63-offset)/8]; 
//				++mOrthogonals[COLUMNS][(63-offset)%8];		
//				++mDiagonals[DIAGONAL_RIGHT][((63-offset)/8) + ((63-offset)%8)];
//				++mDiagonals[DIAGONAL_LEFT][((63-offset)/8) + (7-((63-offset)%8))];
				++LOA_LIGNES.get(63-offset).value;
				++LOA_COLUMNS.get(63-offset).value;
				++LOA_DIAGONAL_RIGHT.get(63-offset).value;
				++LOA_DIAGONAL_LEFT.get(63-offset).value;
			}

			++offset;
		}
		MASK = new HashMap<Integer, Long>();
		for(int i = 0; i < 64; ++i){
			if(i%8 == 0){ //bord de droite
				MASK.put(i, 1L<<i+1 | 3L <<i+7 | ((i-8 >= 0)? 3L << i-8: 0));
			}else if((i+1)%8 == 0){// bord de gauche
				MASK.put(i, (i+7<64?3L <<i+7:0) | 1L<<i-1 | 3L<< i-9);
			}else{//general
				MASK.put(i, 1L<<i+1 | (i+7<64? 7L<<i+7:0L) | 1L<<i-1 | (i-9>=0?7L<< i-9:0));
			}
				
		}
			
		//MASK.put(64, (long)1<<63 | 1 << 1);
		
//		MASK_NB_BITS = new HashMap<Byte, Integer>(256);
//		for (int i = 0; i < 256; ++i) 
//		{
//			MASK_NB_BITS.put((byte) i, Integer.bitCount(i));
//			System.out.println(Integer.toBinaryString(i)+" : " + MASK_NB_BITS.get((byte)i) );
//		}
	}
	
	
	public boolean isConnected(){
		return isConnected(mPions);
	}
	
	private boolean isConnected(long pions){
		
		if(pions != 0){
			int i = 63 - Long.numberOfLeadingZeros(pions);
			
			
			long nexts = 1L<<i;
			while(nexts !=0 ){
				
				long comboMask = 0;

				pions ^= nexts;
				while (nexts != 0) {
					int j = 63 - Long.numberOfLeadingZeros(nexts);
					comboMask |= MASK.get(j);
					nexts ^= 1L << j;
				}
				
				
				nexts = pions & comboMask;
				
			}
			
		}
		
		
		
		
		return pions == 0;
	}
	
	public void printBits(){
		
		System.out.println(Long.toBinaryString(mPions));
		
		System.out.println(Long.toBinaryString(1L<<24|1L<<31| 1L<<63));
		System.out.println(Long.toBinaryString(MASK_MOVEMENT.get(1L<<24|1L<<31)| 1L<<63));
		System.out.println();
		
		System.out.println(Long.toBinaryString(1L<<5|1L<<1| 1L<<63));
		System.out.println(Long.toBinaryString(MASK_MOVEMENT.get(1L<<5|1L<<1)| 1L<<63));
		System.out.println();
		
		System.out.println(Long.toBinaryString(1L<<27|1L<<43| 1L<<63));
		System.out.println(Long.toBinaryString(MASK_MOVEMENT.get(1L<<27|1L<<43)| 1L<<63));
		System.out.println();
		
		System.out.println(Long.toBinaryString(1L<<26|1L<<2| 1L<<63));
		System.out.println(Long.toBinaryString(MASK_MOVEMENT.get(1L<<26|1L<<2)| 1L<<63));
		
//		System.out.println("lines");
//		for(int i = 0; i<8; ++i){
//			System.out.print(mOrthogonals[LINES][i]);
//		}
//		
//		System.out.println();
//		System.out.println("COLUMNS");
//		
//		for(int i = 0; i<8; ++i){
//			System.out.print(mOrthogonals[COLUMNS][i]);
//		}
//		
//		
//		System.out.println();
//		System.out.println("DIAG /");
//		for(int i = 0; i<15; ++i){
//			System.out.print(mDiagonals[DIAGONAL_RIGHT][i]);
//		}
//		
//		System.out.println();
//		System.out.println("DIAG \\");
//		
//		for(int i = 0; i<15; ++i){
//			System.out.print(mDiagonals[DIAGONAL_LEFT][i]);
//		}
		
	}

	/**
	 * 
	 * @param pions
	 * @return
	 * 	the List of possible move
	 */
	public List<Long> generatePossibleMvt(long pions)
	{
		List<Long> possMvt = new ArrayList<>();
		
		//parcour chaque bits (boucle de 64)
		//+ condition si 1L<<i et pions != 0

				
		for (int i = 63; i >=0 ; --i) {
			
			//long postionPion = 1L << i;
			
			long move;
			if((1L << i & pions)  != 0)
			{
				
				
				int position;
//				//lignes
//				//à droite
				position = i-LOA_LIGNES.get(i).value;
				if(position > 0){ //on dépasse pas
					
				}
				
				// à gauche
				position = i+LOA_LIGNES.get(i).value;
				if(position < 8){
					
				}
				
				
//				int position = 0;
//				position = i%8 - mOrthogonals[LINES][i/8];
//				if(position > 0 ){
//					long mvt = 1 << position;
//					if((pions & mvt) == 0){
//						i-1
//					}
//				}
//				// à gauche
//				if(i%8 + mOrthogonals[LINES][i/8] < 8 ){
//					
//				}
				
				
				
				
				
				
				
//				possMvt.add(maskPion);
				
			}
		}
		
		return possMvt;
		
	}
	
	public long getmPions() {
		return mPions;
	}
	
	public void init()
	{
		ArrayList<ReferencedByte> lignes = new ArrayList<ReferencedByte>(8);
		ArrayList<ReferencedByte> columns = new ArrayList<ReferencedByte>(8);
		ArrayList<ReferencedByte> diagR = new ArrayList<ReferencedByte>(15);
		ArrayList<ReferencedByte> diagL = new ArrayList<ReferencedByte>(15);
		for(int i = 0; i<8; ++i){
			lignes.add(new ReferencedByte());
			columns.add(new ReferencedByte());
		}
		
		for(int i = 0; i<15; ++i){
			diagR.add(new ReferencedByte());
			diagL.add(new ReferencedByte());
		}
		
		for(int i = 0; i<64; ++i){
			LOA_LIGNES.put(i, lignes.get(i/8));
			LOA_COLUMNS.put(i, columns.get(i%8));
			LOA_DIAGONAL_RIGHT.put(i, diagR.get((i/8) + (i%8)));
			LOA_DIAGONAL_LEFT.put(i, diagL.get((i/8) + (7-(i%8))));
		}
		long key = 0;
		long value = 0;
		for(int dep = 0; dep<64; ++dep){
			int x = dep%8;
			int y = dep/8;
			int end = 0;
			for(int i = 0; i < 8; ++i){
				if(i != x){
					end = y*8 + i;
					key = 1L<<dep| 1L<<end;
					value = dep<end?(1L<<end)-(1L<< 2*dep):(1L<<dep)-(1L<<2*end);
					MASK_MOVEMENT.put(key, value);
				}
			}
			for(int i = 0; i < 8; ++i){
				if(i != y){
					end = x + i*8;
					key = 1L<<dep| 1L<<end;
					value = 0;
					if(i<y){
						for(int idx = i+1; idx<y; ++idx  ){
							value |= 1L<<idx*8+x;
						}
					}else{
						for(int idx = y+1; idx<i; ++idx  ){
							value |= 1L<<idx*8+x;
						}
					}
					MASK_MOVEMENT.put(key, value);
					
				}
			}
		}
		
//		mLinesOfActon = new byte[64][4];
//		for(int i = 0; i<64; ++i){
//			mLinesOfActon[i][LINES] = 0;
//			mLinesOfActon[i][COLUMNS] = 0;
//			mLinesOfActon[i][DIAGONAL_RIGHT] = 0;
//			mLinesOfActon[i][DIAGONAL_LEFT] = 0;
//		}
		
	
		
		/*long completeGrid = mPions | mPionsAdv;
		System.out.println(Long.toBinaryString(completeGrid));
		
		for (int i = 7; i >=0; --i) {
			System.out.println(Long.toBinaryString((long)1<<63 |((completeGrid & (255L << i*8))>>>i*8)));
		}*/
	}
	
	
	public void update(long move)
	{
		
	}
	
	private class ReferencedByte{
		private byte value = 0;
		
		
	}
	
	
	
}
