
import java.math.BigInteger;


public class SerialLucasLehmer implements IPrimalityTest
{
    private BigInteger ZERO = BigInteger.ZERO;
    private BigInteger ONE = BigInteger.ONE;
    private BigInteger TWO = ONE.add(ONE);
    private BigInteger FOUR = TWO.add(TWO);
    @Override
    public boolean IsPrime(BigInteger n, int numThreads)
    {
        BigInteger s = FOUR;
        BigInteger mp = TWO.pow(n.intValue()).subtract(ONE);
        for (int i =  2; i < n.intValue(); i++)
        {
            s = s.multiply(s).subtract(TWO).mod(mp);
        }
        return s.mod(mp).equals(ZERO);
    }
    
    public static void main(String[] args)
    {
        
    }

}
