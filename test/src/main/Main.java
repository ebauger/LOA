package main;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

	private Grid mGrid;
	private static Client mClient;

	private static Messages mMessages;

	/**
	 * @param args
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		/* Grid.init();
		//Grid.print();
		 Grid whitePlayer = new StratMass1(
				 "01111110" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "01111110", 1,Messages.White);
		 Grid blackPlayer = new StratMass1(
				 "01111110" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "20000002" +
				 "01111110", 1,Messages.Black);
		 whitePlayer.printGame();
		 loop : while(!whitePlayer.gameOver()){
			
			long whiteMove = whitePlayer.getBestMove(5);
			whitePlayer.MakeMvtAndUpdate(whiteMove);
			blackPlayer.coupAdvAndUpdate(whiteMove);
			whitePlayer.printGame();
			if(whitePlayer.gameOver())
				break loop;
			long blackMove = blackPlayer.getBestMove(7);
			blackPlayer.MakeMvtAndUpdate(blackMove);
			whitePlayer.coupAdvAndUpdate(blackMove);
			whitePlayer.printGame();
		 }
		 whitePlayer.printGame();*/
		
		/*HandlerThread ht;
		
		Thread t1 = new Thread(){
			public void run() {
				while (true){
					
				}
			};
		};
		Thread t2 = new Thread(){
			public void run() {
				while (true){
					
				}
			};
		};
		Thread t3 = new Thread(){
			public void run() {
				while (true){
					
				}
			};
		};
		Thread t4 = new Thread(){
			public void run() {
				while (true){
					
				}
			};
		};
		
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		*/
		//Avg avg = new Avg();
		ExecutorService es = Executors.newFixedThreadPool(3);
	    Future<Double> f = es.submit(new Avg());
	    Future<Integer> f2 = es.submit(new Factorial());
	    
		System.out.println(f.get());
		System.out.println(f2.get());
		System.out.println("fin");
	   
	    es.shutdown();
	}
	static class Avg implements Callable<Double> {
		  Avg() {
		  }

		  public Double call() {
			  int i = 0;
			  do{
				 ++i;
			  }while (i < 1000000000);
		    return 0.0;
		  }
		}
		static class Factorial implements Callable<Integer> {
		  Factorial() {
		  }

		  public Integer call() {
			  int i = 0;
			  
			  while (i < 2000000000){
				 ++i; 
			  }
		    return 1;
		  }
		}

}
