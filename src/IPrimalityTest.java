import java.math.BigInteger;

public interface IPrimalityTest
{
    /**
     * @param n The number to check for primality.
     * @return True if n is prime.
     */
    boolean IsPrime(BigInteger n, int numThread);
}
