import java.math.BigInteger;
import java.util.Random;

/*
 * Adaptation of http://stackoverflow.com/questions/4027225/implementation-of-fermats-primality-test
 */
public class FermatSerial
{

    private final static Random rand = new Random();

    private static BigInteger getRandomFermatBase(BigInteger n)
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

    public static boolean checkPrime(BigInteger n, int maxIterations)
    {
        if (n.equals(BigInteger.ONE))
            return false;

        for (int i = 0; i < maxIterations; i++)
        {
            BigInteger a = getRandomFermatBase(n);
            a = a.modPow(n.subtract(BigInteger.ONE), n);

            if (!a.equals(BigInteger.ONE))
                return false;
        }

        return true;
    }

    public static void main(String[] args)
    {
//    	BigInteger n = new BigInteger("88136543423525"); // not a prime
    	BigInteger n = new BigInteger("81173951511377"); // is prime
//    	BigInteger range = new BigInteger("999999");
    	int maxIterations = 500000;
    	
    	long startTime = System.currentTimeMillis();
//    	for (BigInteger n = new BigInteger("1"); n.compareTo(range) < 0; n = n.add(BigInteger.ONE)) {
    		if (checkPrime(n, maxIterations)) System.out.println(n + " is prime");
			else System.out.println(n + " is not prime");
//    		checkPrime(n, maxIterations);
//    	}
        System.out.println(System.currentTimeMillis() - startTime + " ms");
    }
}
