import java.math.BigInteger;
import java.util.List;


public class WFPrimTesting {

    public static void main(String[] args) {
    	// largest primes for each n-digit to be use for benchmarks,
    	// use each algorithm to loop through these to check if they're prime
    	// testing both serial and concurrent methods
        
    	BigInteger[] primes = new BigInteger[15];
        WheelFactParallelTest[] w = new WheelFactParallelTest[15];
        
    	primes[0] = new BigInteger("7");
    	primes[1] = new BigInteger("211");
    	primes[2] = new BigInteger("7901");
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
        primes[13] = new BigInteger("987654321357"); //NOT A PRIME
        //primes[13] = new BigInteger("99999999999973");
        primes[14] = new BigInteger("999999999999989");
        //primes[15] = new BigInteger("909090909090909091");
        
           
    	// generate list
    	BigInteger range = new BigInteger("8000");
        WheelFactParallelTest wt = new WheelFactParallelTest(8);

        long startTime = System.nanoTime();
        List<BigInteger> primeList = wt.GeneratePrimes(range, 8);
        long runTime = System.nanoTime() - startTime;
        
        for (BigInteger b : primeList) {
        	System.out.println(b);
        }
        System.out.println(runTime/1000.0 + " us\n");
        
        // test single value
        BigInteger n = new BigInteger("81173951511377");
        //BigInteger n = new BigInteger("331");
        WheelFactParallelTest w1 = new WheelFactParallelTest(8);
        
        startTime = System.nanoTime();
        System.out.print(w1.IsPrime(n, 8));
        runTime = System.nanoTime() - startTime;
        System.out.println("\t"+runTime/1000.0 + " us, for "+n);
        
        int i=0;
        for (BigInteger p : primes) {
                w[i] = new WheelFactParallelTest(8);               
                startTime = System.nanoTime();
                System.out.print(i+": "+w[i].IsPrime(p, 8));
                runTime = System.nanoTime() - startTime;
                System.out.println("\t"+ runTime/1000.0 + " us, for "+p);
                i++;
        }
                
        
    }

}
