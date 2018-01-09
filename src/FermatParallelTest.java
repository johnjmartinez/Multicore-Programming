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
public class FermatParallelTest implements IPrimalityTest, IPrimeGenerator{
	private int maxIterations;
	ExecutorService executor;
	List<BigInteger> primeList = new ArrayList<BigInteger>();
	
	public FermatParallelTest(int maxIterations) {
		this.maxIterations = maxIterations;
	}

    @Override
    public boolean IsPrime(BigInteger n, int numThread) {
    	executor = Executors.newFixedThreadPool(numThread);
    	FermatParallel[] fps = new FermatParallel[numThread];
    	for (int i = 0; i < numThread; i++) {
    		// divide up the search space of max maxIterations into
    		// equal portions for each thread plus the modulus that's left over
    		FermatParallel fp = new FermatParallel(n, maxIterations/numThread + (maxIterations % numThread));
    		fp.start();
    		fps[i] = fp;
    	}
    	for (FermatParallel fp : fps) {
    		try {
				fp.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return FermatParallel.primeFound;
    }
    
    @Override
    public List<BigInteger> GeneratePrimes(BigInteger n, int numThread) {
    	BigList<Future<BigInteger>> futureList = new BigList<Future<BigInteger>>(n);
    	Callable<BigInteger> fp = new FermatParallel(maxIterations);
    	BigInteger c = new BigInteger("2");
    	for (int i = 0; i < futureList.size() + 1; i++) {
    		for (int j = 0; j < Integer.MAX_VALUE && c.compareTo(n) < 0; j++) {
    			Future<BigInteger> future = executor.submit(fp);
    			futureList.list.get(i).add(future);
    			c = c.add(BigInteger.ONE);
    		}
    	}
    	for (int i = 0; i < futureList.size() + 1; i++) {
	    	for(Future<BigInteger> fut : futureList.list.get(i)){
	    		try {
	    			if (fut.get().compareTo(BigInteger.ZERO) > 0) primeList.add(fut.get());
	    		} catch (InterruptedException | ExecutionException e) {
	    			e.printStackTrace();
	    		}
	    	}
    	}
    	executor.shutdown();
    	return primeList;
    }
    
    public static void main(String[] args) {
//		final BigInteger n = new BigInteger("9999999999999999999999999999999999999983");
//		final int numThread = 12;
		
//    	// parallel
//    	TestingTimer parallel = new TestingTimer()
//    	{
//    		FermatParallelTest fp = new FermatParallelTest(70000);
//    		
//    		@Override
//    		Object functionToRun() {
//    			return fp.IsPrime(n, numThread);
//    		}
//    		
//    	};
    	
//    	Object parallelResult = parallel.timeFunction();
//    	System.out.format("Parallel \t %s probable prime? %s\t runtime: %d ms \n", n.toString(), parallelResult, parallel.getLatestTime(TimeUnit.MILLISECONDS));
		
		RangeTester rt = new RangeTester();
		try {
			for (BigInteger i : rt.testRange(new BigInteger("99999"), 1, 12, new FermatParallelTest(24))) {
				System.out.println(i.toString());
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }


}
