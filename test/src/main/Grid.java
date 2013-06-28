package main;

import java.util.HashMap;
import java.util.Map;

public class Grid {

	private static Map<Integer,Long> MASK;
	
	private long mPions = 0;
	private long mPionsAdv = 0;
	
	
	
	
	public Grid(String pions){
		int offset = 0;
		for (char c : pions.toCharArray()) {
			if(c == '1')
				mPions |= (long)1<<63-offset;
			if(c == '2')
				mPionsAdv |= (long)1<<63-offset;
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
			
		MASK.put(64, (long)1<<63 | 1 << 1);
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
		int i = Long.numberOfLeadingZeros(mPions);
		//System.out.println(i);
		while(i-- > 0){
			System.out.print("0");
		}
		System.out.println(Long.toBinaryString(mPions));
		i = Long.numberOfLeadingZeros(mPionsAdv);
		//System.out.println(i);
		while(i-- > 0){
			System.out.print("0");
		}
		System.out.println(Long.toBinaryString(mPionsAdv));
		
	}
	
	public void printGrid(){
		
	}
}
