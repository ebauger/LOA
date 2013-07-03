package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Grid implements Runnable{

	private static Map<Integer,Long> MASK;
	public static int TYPE_DECODE_SERVER = 0;

	
	private long mPions = 0;
	private long mPionsAdv = 0;
	
/*	private static final int LINES = 0;
	private static final int COLUMNS = 1;
	private static final int DIAGONAL_RIGHT = 2;  // '/'
	private static final int DIAGONAL_LEFT = 3; // '\'
	
*/
	//byte[][] mLinesOfActon;
	private static final Map<Integer, ReferencedByte> LOA_LIGNES = new HashMap<Integer, ReferencedByte>(64);
	private static final Map<Integer, ReferencedByte> LOA_COLUMNS = new HashMap<Integer, ReferencedByte>(64);
	private static final Map<Integer, ReferencedByte> LOA_DIAGONAL_RIGHT = new HashMap<Integer, ReferencedByte>(64);
	private static final Map<Integer, ReferencedByte> LOA_DIAGONAL_LEFT = new HashMap<Integer, ReferencedByte>(64);
	
	
	private static final Map<Long, Long> MASK_MOVEMENT = new HashMap<Long, Long>(2016);
	
	public Grid(String str, int type,int myColor){
		init();
		int offset = 0;
		
		
		if(type == TYPE_DECODE_SERVER){
			
			long pionsWhite = 0;
			long pionsBlack = 0;
			
			for (char c : str.toCharArray()) {
				if(c == '2'){
					pionsWhite |= (long)1<<63-offset;
					++LOA_LIGNES.get(63-offset).value;
					++LOA_COLUMNS.get(63-offset).value;
					++LOA_DIAGONAL_RIGHT.get(63-offset).value;
					++LOA_DIAGONAL_LEFT.get(63-offset).value;

				}	
				else if(c == '4')
				{
					pionsBlack  |= (long)1<<63-offset;
					++LOA_LIGNES.get(63-offset).value;
					++LOA_COLUMNS.get(63-offset).value;
					++LOA_DIAGONAL_RIGHT.get(63-offset).value;
					++LOA_DIAGONAL_LEFT.get(63-offset).value;
				}

				++offset;
			}
			
			if(myColor == Messages.White)
			{
				mPions = pionsWhite;
				mPionsAdv = pionsBlack;
			}
			else
			{
				mPions = pionsBlack;
				mPionsAdv = pionsWhite ;
			}
			
		}else{
		
		
		
		for (char c : str.toCharArray()) {
			if(c == '1'){
				mPions |= (long)1<<63-offset;
				++LOA_LIGNES.get(63-offset).value;
				++LOA_COLUMNS.get(63-offset).value;
				++LOA_DIAGONAL_RIGHT.get(63-offset).value;
				++LOA_DIAGONAL_LEFT.get(63-offset).value;
				
				
				
				
			}	
			else if(c == '2')
			{
				mPionsAdv  |= (long)1<<63-offset;
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
		
		}	
	
	}
	
	public void printBits(Long arrayBits){
		int i = Long.numberOfLeadingZeros(arrayBits);
		
		while(i-- > 0){
			System.out.print("0");
		}
		System.out.println(Long.toBinaryString(arrayBits));
		

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
		
		
		
		
		//printBits(1L<<24|1L<<31)
		/*System.out.println(Long.toBinaryString(1L<<24|1L<<31| 1L<<63));
		System.out.println(Long.toBinaryString(MASK_MOVEMENT.get(1L<<24|1L<<31)| 1L<<63));
		System.out.println();
		
		System.out.println(Long.toBinaryString(1L<<5|1L<<1| 1L<<63));
		System.out.println(Long.toBinaryString(MASK_MOVEMENT.get(1L<<5|1L<<1)| 1L<<63));
		System.out.println();
		
		System.out.println(Long.toBinaryString(1L<<27|1L<<43| 1L<<63));
		System.out.println(Long.toBinaryString(MASK_MOVEMENT.get(1L<<27|1L<<43)| 1L<<63));
		System.out.println();				
		System.out.println(Long.toBinaryString(1L<<26|1L<<2| 1L<<63));
		System.out.println(Long.toBinaryString(MASK_MOVEMENT.get(1L<<26|1L<<2)| 1L<<63));*/
		
/*System.out.println();
		
		System.out.println(Long.toBinaryString(1L<<28|1L<<56| 1L<<63));
		System.out.println(Long.toBinaryString(MASK_MOVEMENT.get(1L<<28|1L<<56)| 1L<<63));
		
System.out.println();
		
		System.out.println(Long.toBinaryString(1L<<27|1L<<41| 1L<<63));
		System.out.println(Long.toBinaryString(MASK_MOVEMENT.get(1L<<27|1L<<41)| 1L<<63));
		
System.out.println();
		
		System.out.println(Long.toBinaryString(1L<<5|1L<<23| 1L<<63));
		System.out.println(Long.toBinaryString(MASK_MOVEMENT.get(1L<<5|1L<<23)| 1L<<63));
		
System.out.println();
		
		System.out.println(Long.toBinaryString(1L<<16|1L<<61| 1L<<63));
		System.out.println(Long.toBinaryString(MASK_MOVEMENT.get(1L<<16|1L<<61)| 1L<<63));
		
System.out.println(MASK_MOVEMENT.size());*/		
		

		
	}

	
	/**
	 * 
	 * @param pions
	 * @return
	 * 	the List of possible moves
	 */
	public ArrayList<Long> generatePossibleMvt()
	{
		ArrayList<Long> possMvt = new ArrayList<Long>();
		
		//parcour chaque bits (boucle de 64)
		//+ condition si 1L<<i et pions != 0

				
		for (int i = 63; i >=0 ; --i) {
			
			//long postionPion = 1L << i;
			
			//long move;
			if((1L << i & mPions)  != 0)
			{
				
				int currentY = i/8; 
				int currentX = i%8;
				int position;
//				//lignes
//				//à droite
				position = i-LOA_LIGNES.get(i).value;
				if(position >= currentY*8){ //on dépasse pas
					if((mPions & (1L << position)) == 0){
					//	System.out.println(i+" to "+position);
						if((mPionsAdv & MASK_MOVEMENT.get(1L<<i | 1L << position)) == 0){
							possMvt.add(1L<<i | 1L << position);
						}
					}
				}
				
				// à gauche
				position = i+LOA_LIGNES.get(i).value;
				if(position < currentY*8+8){
					if((mPions & (1L << position)) == 0){
						if((mPionsAdv & MASK_MOVEMENT.get(1L<<i | 1L << position)) == 0){
							possMvt.add(1L<<i | 1L << position);
						}
					}
				}
				
				
				//le long de la colonne
				//en haut
				position = i+LOA_COLUMNS.get(i).value*8;
				if(position < 64){
					if((mPions & (1L << position)) == 0){
						if((mPionsAdv & MASK_MOVEMENT.get(1L<<i | 1L << position)) == 0){
							possMvt.add(1L<<i | 1L << position);
						}
					}
				}
				//en bas
				position = i-LOA_COLUMNS.get(i).value*8;
				if(position >=0 ){
					if((mPions & (1L << position)) == 0){
						if((mPionsAdv & MASK_MOVEMENT.get(1L<<i | 1L << position)) == 0){
							possMvt.add(1L<<i | 1L << position);
						}
					}
				}
				
				//diagonale droite '/'
				//en haut droite
				int endX = currentX - LOA_DIAGONAL_RIGHT.get(i).value;
				int endY = currentY + LOA_DIAGONAL_RIGHT.get(i).value;
				if(endX >=0 && endY < 8){
					position = endY*8 + endX;
					if((mPions & (1L << position)) == 0){
						if((mPionsAdv & MASK_MOVEMENT.get(1L<<i | 1L << position)) == 0){
							possMvt.add(1L<<i | 1L << position);
						}
					}
				}
				
				//en bas gauche
				 endX = currentX + LOA_DIAGONAL_RIGHT.get(i).value;
				 endY = currentY - LOA_DIAGONAL_RIGHT.get(i).value;
				if(endX <8 && endY >= 0){
					position = endY*8 + endX;
					if((mPions & (1L << position)) == 0){
						if((mPionsAdv & MASK_MOVEMENT.get(1L<<i | 1L << position)) == 0){
							possMvt.add(1L<<i | 1L << position);
						}
					}
				}
				
				//Diagonale gauche '\'
				//en haut gauche
				
				 endX = currentX + LOA_DIAGONAL_RIGHT.get(i).value;
				 endY = currentY + LOA_DIAGONAL_RIGHT.get(i).value;
				if(endX <8 && endY < 8){
					position = endY*8 + endX;
					if((mPions & (1L << position)) == 0){
						if((mPionsAdv & MASK_MOVEMENT.get(1L<<i | 1L << position)) == 0){
							possMvt.add(1L<<i | 1L << position);
						}
					}
				}
				
				//en bas droite
				
				 endX = currentX - LOA_DIAGONAL_RIGHT.get(i).value;
				 endY = currentY - LOA_DIAGONAL_RIGHT.get(i).value;
				if(endX >=0 && endY >=0){
					position = endY*8 + endX;
					if((mPions & (1L << position)) == 0){
						if((mPionsAdv & MASK_MOVEMENT.get(1L<<i | 1L << position)) == 0){
							possMvt.add(1L<<i | 1L << position);
						}
					}
				}
				
				
				
				
				
				
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
			
			//masques de lignes
			for(int i = x+1; i < 8; ++i){
				if(i != x){
					end = y*8 + i;
					key = 1L<<dep| 1L<<end;
					value = (dep<end)?((1L<<end)-(1L<<dep)*2):((1L<<dep)-(1L<<end)*2);
					MASK_MOVEMENT.put(key, value);
				}
			}
			
			//masques de colonnes
			for(int i = y+1; i < 8; ++i){
				if(i != y ){
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
			
			
			
			//masques de diagonales droites /
		
			int endX = x-1; int endY = y + 1;
			while(endX >= 0 && endY <= 8){
				end = endX + endY*8;
				key = 1L<<dep| 1L<<end;
				value = 0;
				int interX = x-1; int interY = y + 1;
				while(interX > endX && interY < endY){
					value  |= 1L<<(interX+interY*8);
					--interX; ++interY;
				}
				MASK_MOVEMENT.put(key, value);
				
				--endX;
				++endY;
			}
			
			//masques de diagonales gauche \

			endX = x+1; endY = y + 1;
			while(endX <= 8 && endY <= 8){
				end = endX + endY*8;
				key = 1L<<dep| 1L<<end;
				value = 0;
				int interX = x+1; int interY = y + 1;
				while(interX < endX && interY < endY){
					value  |= 1L<<(interX+interY*8);
					++interX; ++interY;
				}
				MASK_MOVEMENT.put(key, value);
				
				++endX;
				++endY;
			}
			
		}
		

	}
	
	public void printGame(){
		System.out.println("---------------------");
		long totalPions = mPions | mPionsAdv;
		for(int i = 63; i>=0; --i){
			if((totalPions & (1l << i))==0){
				System.out.print("[ ]");
			}
			else if((mPions & (1l << i))==0){
				System.out.print("[x]");
			}else {
				System.out.print("[o]");
			}
			if(i%8 == 0){
				System.out.println();
			}
		}
	}
	
	
	public void MakeMvtAndUpdate(long move)

	{
		long from = mPions & move;
		long to = move^from;
		updateLOAs(63-Long.numberOfLeadingZeros(from), 63-Long.numberOfLeadingZeros(to));
		mPions ^= move;
		mPionsAdv &= (-1L^to);
	}
	
	public void coupAdvAndUpdate(long move){
//		inverse();
		MakeMvtAndUpdate(move);
//		inverse();
		run();
	}
	
	private void inverse(){
		long temp = mPions;
		mPions = mPionsAdv;
		mPions = temp;
	}
	
	
	public void updateLOAs(int from, int to){
		System.out.println("from="+(63-Long.numberOfLeadingZeros(from)));
		--LOA_LIGNES.get(from).value;
		--LOA_COLUMNS.get(from).value;
		--LOA_DIAGONAL_RIGHT.get(from).value;
		--LOA_DIAGONAL_LEFT.get(from).value;
		
		++LOA_LIGNES.get(to).value;
		++LOA_COLUMNS.get(to).value;
		++LOA_DIAGONAL_RIGHT.get(to).value;
		++LOA_DIAGONAL_LEFT.get(to).value;
	}
	
	
	
	private class ReferencedByte{
		private byte value = 0;
		
		
	}
	
	
	
	private long calcule(int lvl)
	{
		ArrayList<Long> coups = generatePossibleMvt();
		int taille = coups.size();
		return coups.get((int) (Math.random()*taille));
	}
	
	private static long bestMvt = 0;


	public String getBestMove()
	{
		printBits(bestMvt);
		int from = 63-Long.numberOfLeadingZeros(mPions & bestMvt);
		long to = 63-Long.numberOfLeadingZeros(bestMvt^from);
//		updateLOAs(63-Long.numberOfLeadingZeros(from), 63-Long.numberOfLeadingZeros(to));
//		mPions ^= bestMvt;
//		mPionsAdv &= (-1L^to);
		char[] res = new char[4];
		res[0] = (char) ('A' + (7-(from%8)));
		res[1] = (char) ('1' + (from/8));
		res[2] = (char) ('A' + (7-(to%8)));
		res[3] = (char) ('1' + (to/8));
		
		
		//String fromletter = String.valueOf(((char) ('H' - (from%8))) + ((char)'1' + (from/8)));
		//String toletter = String.valueOf(((char) ('H' - (to%8))) + ((char)'1' + (to/8)));

		return  "" + res[0] + res[1] +res[2] +res[3];
	}
	@Override
	public void run() {
		bestMvt = calcule(1);
		MakeMvtAndUpdate(bestMvt);
		
	}
	
}
