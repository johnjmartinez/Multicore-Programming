

import static primes.FrequentlyUsed.ONE;
import static primes.FrequentlyUsed.THREE;
import static primes.FrequentlyUsed.TWO;
import static primes.FrequentlyUsed.ZERO;
import static primes.FrequentlyUsed.sqrt;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
public class SieveEratosthenes implements IPrimeGenerator
{
    private static final Map<BigInteger, Boolean> primes = new ConcurrentHashMap<BigInteger, Boolean>();
    
    public SieveEratosthenes(BigInteger n)
    {
        init(n);
    }
    
    private static void init(BigInteger n)
    {
        for (BigInteger i = THREE;
                n.compareTo(i) > 0;
                i = i.add(TWO))
        {
            primes.put(i, true);
        }
    }
    
    private List<BigInteger> grabPrimesOnly(BigInteger n)
    {
        final List<BigInteger> result = new LinkedList<BigInteger>();
        result.add(TWO);
        for (BigInteger i = THREE;
                n.compareTo(i) > 0;
                i = i.add(TWO))
        {
            if (primes.containsKey(i) && primes.get(i))
            {
                result.add(i);
            }
        }
        
        return result;
    }
    
    private class Worker implements Runnable
    {
        private BigInteger start;
        private BigInteger end;
        private BigInteger max;
        public Worker(BigInteger s, BigInteger e, BigInteger m)
        {
            start = s;
            end = e;
            max = m;
        }
        @Override
        public void run()
        {
            for (BigInteger i = start; 
                    end.compareTo(i) > 0;
                    i = i.add(ONE))
            {
                if (primes.containsKey(i) && primes.get(i))
                {
                    for (BigInteger j = i.multiply(i);
                            max.compareTo(j) > 0;
                            j = j.add(i))
                    {
                        if (j.compareTo(ZERO) == 0) break;
                        primes.put(j, false);
                    }
                }
            }
        }
    }
    
    @Override
    public List<BigInteger> GeneratePrimes(BigInteger n, int numThreads)
    {
        final ExecutorService e = Executors.newFixedThreadPool(numThreads);
        
        final BigInteger sqrt = sqrt(n).add(ONE); 
        final BigInteger interval = sqrt.divide(BigInteger.valueOf(numThreads));
        
        for(int i = 0; i < numThreads; i++)
        {
            BigInteger start = i == 0 ? THREE : interval.multiply(BigInteger.valueOf(i));
            BigInteger end = i == numThreads - 1 ? sqrt : interval.multiply(BigInteger.valueOf(i+1));
            Worker w = new Worker(start, end, n);
            e.execute(w);
        }
        
        try
        {

            e.shutdown();
            e.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e1)
        {
            e1.printStackTrace();
        }
        return grabPrimesOnly(n);
    }
    
    public static void display(List<BigInteger> nums)
    {
        for (BigInteger b : nums)
        {
            System.out.println(b);
        }
    }

    public static void main(String[] args)
    {
        String val = "10000000";
        init(new BigInteger(val));
        TestingTimer t = new TestingTimer()
        {
            @Override
            Object functionToRun()
            {
                String val = "10000000";
                SieveEratosthenes s = new SieveEratosthenes(new BigInteger(val));
                return s.GeneratePrimes(new BigInteger(val), 8);
            }
        };
        
        Object obj = t.timeFunction();

        List<BigInteger> primes = (List<BigInteger>) obj;
        BigInteger last = null;
        for (BigInteger x : primes)
        {
            last = x;
        }
        System.out.println(String.format("Execution time: %d milliseconds", t.getLatestTime(TimeUnit.MILLISECONDS)));
        System.out.println(String.format("Number of primes found: %d", primes.size()));
        System.out.println(String.format("Last prime: %s", last.toString()));
    }
}
