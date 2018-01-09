

import java.math.BigInteger;

public abstract class PrimeTestTimer extends TestingTimer
{
    public int threads;
    public BigInteger n;
    public PrimeTestTimer(BigInteger value, int numThreads)
    {
        n = value;
        threads = numThreads;
    }
}
