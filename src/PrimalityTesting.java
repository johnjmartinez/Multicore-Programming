import java.math.BigInteger;
import java.util.List;


public class PrimalityTesting
{

    
    public static void main(String[] args)
    {
    	// largest primes for each n-digit to be use for benchmarks,
    	// use each algorithm to loop through these to check if they're prime
    	// testing both serial and concurrent methods
    	BigInteger[] primes = new BigInteger[40];
    	primes[0] = new BigInteger("7");
    	primes[1] = new BigInteger("97");
    	primes[2] = new BigInteger("997");
    	primes[3] = new BigInteger("9973");
    	primes[4] = new BigInteger("99991");
    	primes[5] = new BigInteger("999983");
    	primes[6] = new BigInteger("9999991");
    	primes[7] = new BigInteger("99999989");
    	primes[8] = new BigInteger("999999937");
    	primes[9] = new BigInteger("9999999967");
    	primes[10] = new BigInteger("99999999977");
    	primes[11] = new BigInteger("999999999989");
    	primes[12] = new BigInteger("9999999999971");
    	primes[13] = new BigInteger("99999999999973");
    	primes[14] = new BigInteger("999999999999989");
    	primes[15] = new BigInteger("9999999999999937");
    	primes[16] = new BigInteger("99999999999999997");
    	primes[17] = new BigInteger("999999999999999989");
    	primes[18] = new BigInteger("9999999999999999961");
    	primes[19] = new BigInteger("99999999999999999989");
    	primes[20] = new BigInteger("999999999999999999899");
    	primes[21] = new BigInteger("9999999999999999999973");
    	primes[22] = new BigInteger("99999999999999999999977");
    	primes[23] = new BigInteger("999999999999999999999743");
    	primes[24] = new BigInteger("9999999999999999999999877");
    	primes[25] = new BigInteger("99999999999999999999999859");
    	primes[26] = new BigInteger("999999999999999999999999901");
    	primes[27] = new BigInteger("9999999999999999999999999791");
    	primes[28] = new BigInteger("99999999999999999999999999973");
    	primes[29] = new BigInteger("999999999999999999999999999989");
    	primes[30] = new BigInteger("9999999999999999999999999999973");
    	primes[31] = new BigInteger("99999999999999999999999999999979");
    	primes[32] = new BigInteger("999999999999999999999999999999991");
    	primes[33] = new BigInteger("9999999999999999999999999999999589");
    	primes[34] = new BigInteger("99999999999999999999999999999999977");
    	primes[35] = new BigInteger("999999999999999999999999999999999841");
    	primes[36] = new BigInteger("9999999999999999999999999999999999919");
    	primes[37] = new BigInteger("99999999999999999999999999999999999941");
    	primes[38] = new BigInteger("999999999999999999999999999999999999943");
    	primes[39] = new BigInteger("9999999999999999999999999999999999999983");
    }

}
