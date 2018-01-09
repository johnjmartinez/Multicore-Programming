import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * For parallel search we will use ExecutorService and Future.
 * The number of primes we can test is limited by the size of Lists in a List
 * that stores the Future, which is Integer.MAX_VALUE^2.
 */
 
public class WheelFactParallelTest implements IPrimalityTest, IPrimeGenerator {

    //private int numThread;
	ExecutorService executor;
	List<BigInteger> primeList = new ArrayList<BigInteger> ();
	
	public WheelFactParallelTest(int ts) {
		executor = Executors.newFixedThreadPool(ts);
	}

    @Override
    public boolean IsPrime(BigInteger n, int numThreads) {
    
    	WheelFactParallel[] w_array = new WheelFactParallel[numThreads];
        
    	for (int i = 0; i < numThreads; i++)
            w_array[i] = new WheelFactParallel(n, i, numThreads);
    		
        for (WheelFactParallel w_thread : w_array) 
            w_thread.start();
        
    	for (WheelFactParallel w_thread : w_array) {
    		try {
				w_thread.join();
			} 
            catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return !WheelFactParallel.notPrime;
    }
    

    @Override
    public List<BigInteger> GeneratePrimes(BigInteger n, int numThreads) {
    
    	BigList<Future<BigInteger>> futureList = new BigList<Future<BigInteger>>(BigInteger.valueOf(numThreads));
    	Callable<BigInteger> w_thread = new WheelFactParallel();
    	BigInteger c = new BigInteger("2");
        
    	for (int i = 0; i < futureList.size(); i++) {
    		for (int j = 0; j < Integer.MAX_VALUE && c.compareTo(n) < 0; j++) {
            
    			Future<BigInteger> future = executor.submit(w_thread);
    			futureList.list.get(i).add(future);
    			c = c.add(BigInteger.ONE);
    		}
    	}
        
    	for (int i = 0; i < futureList.size(); i++) {
	    	for (Future<BigInteger> fut : futureList.list.get(i)) {
                try {
	    			if (!fut.get().equals(BigInteger.ZERO)) primeList.add(fut.get());
	    		} 
                catch (Exception e) {
	    			e.printStackTrace();
	    		}
	    	}
    	}
    	executor.shutdown();
    	return primeList;
    }

}
