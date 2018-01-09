import java.math.BigInteger;
import java.util.List;


public interface IPrimeGenerator
{
    /**
     * @param n The generation of primes will find all primes from 2..n, inclusive.
     * @return All primes from 2..n.
     */
    public List<BigInteger> GeneratePrimes(BigInteger n, int numThread);
}
