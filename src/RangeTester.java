import static java.math.BigInteger.ONE;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
public class RangeTester
{
    
    private static BigInteger TWO = FrequentlyUsed.TWO;
    private class WorkerTest implements Callable<Boolean>
    {
        IPrimalityTest test;
        BigInteger n;
        int numThread;
        public WorkerTest(IPrimalityTest t, BigInteger m, int threads)
        {
            test = t;
            n = m;
            numThread = threads;
        }
        
        @Override
        public Boolean call() throws Exception
        {
            return test.IsPrime(n, numThread);
        }
        
    }
    
    /**
     * Tests a range for primes.
     * Important note: This will potentially create M * N threads.
     * 
     * @param upto  The algorith will search from  2..upto for primes.
     * @param M The number of threads to use in the for loop.
     * @param N The number of threads to use in the IPrimalityTest.
     * @param test The IPrimalityTest to run.
     * @return The list of primes.
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public final List<BigInteger> testRange(BigInteger upto, int M, int N, IPrimalityTest test) throws InterruptedException, ExecutionException
    {
        final List<BigInteger> result = new LinkedList<BigInteger>();
        
        ExecutorService service = Executors.newFixedThreadPool(M);
        HashMap<BigInteger, Future<Boolean>> answers = new HashMap<BigInteger, Future<Boolean>>();
        for (BigInteger i = TWO;
                i.compareTo(upto.add(ONE)) < 0;
                i = i.add(ONE))
        {
            answers.put(i, service.submit(new WorkerTest(test, i, N)));
        }
        
        for (Entry<BigInteger, Future<Boolean>> e : answers.entrySet())
        {
            if (e.getValue().get())
            {
                result.add(e.getKey());
            }
        }
        
        service.shutdown();
        service.awaitTermination(1L, TimeUnit.DAYS);
        
        return result;
    }
    
    
}
