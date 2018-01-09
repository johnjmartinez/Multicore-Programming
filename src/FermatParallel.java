import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.Callable;

/*
 * Parallelized version from http://stackoverflow.com/questions/4027225/implementation-of-fermats-primality-test
 */
public class FermatParallel extends Thread implements Callable<BigInteger>
{
    static final Random rand = new Random();
    static AtomicBigInteger ab;
    private BigInteger n;
    private int maxIterations;
    volatile static Boolean primeFound = false;
    
    public FermatParallel(int maxIterations) {
    	this.maxIterations = maxIterations;
    	ab = new AtomicBigInteger("2"); // starts at 2
    	primeFound = false;
    }
    
    public FermatParallel(BigInteger n, int maxIterations) {
    	this.n = n;
    	this.maxIterations = maxIterations;
    }

    private BigInteger getRandomFermatBase(BigInteger n)
    {
        // Rejection method: ask for a random integer but reject it if it isn't
        // in the acceptable set.

        while (true)
        {
            final BigInteger a = new BigInteger (n.bitLength(), rand);
            // must have 1 <= a < n
            if (BigInteger.ONE.compareTo(a) <= 0 && a.compareTo(n) < 0)
            {
                return a;
            }
        }
    }

    public void checkPrime()
    {
        if (!primeFound && n.equals(BigInteger.ONE)) {
        	return;
        }

        for (int i = 0; !primeFound && i < maxIterations; i++)
        {
            BigInteger a = getRandomFermatBase(n);
            a = a.modPow(n.subtract(BigInteger.ONE), n);

            if (!a.equals(BigInteger.ONE)) {
            	return;
            }
        }
        
        primeFound = true;
        return;
    }
    
    public BigInteger generatePrimes()
    {
    	BigInteger n = ab.getAndIncrement();
    	
    	if (n.compareTo(BigInteger.ZERO) == 0 || n.compareTo(BigInteger.ONE) == 0) return BigInteger.ZERO;

    	for (int i = 0; i < maxIterations; i++)
    	{
    		BigInteger a = getRandomFermatBase(n);
    		a = a.modPow(n.subtract(BigInteger.ONE), n);
    		
    		if (!a.equals(BigInteger.ONE)) {
    			return BigInteger.ZERO;
    		}
    	}
    	
    	return n;
    }
    
    @Override
    public void run() {
    	primeFound = false;
    	checkPrime();
    }
    
    @Override
    public BigInteger call() throws Exception {
    	return generatePrimes();
    }
}
