/**
Suppose n-1 = FR, where F>R, gcd(F,R)=1 and the factorization of F is known.
If for every prime factor q of F there is an integer a>1 such that
a^n-1 = 1 (mod n), and
gcd(a^((n-1)/q)-1,n) = 1;
then n is prime.
**/

import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class PocklingtonParallel extends FrequentlyUsed implements Callable<Boolean>, IPrimalityTest {

    BigInteger n, nMinusOne;
    AtomicBigInteger curr = new AtomicBigInteger(ZERO);

    ArrayList<BigInteger> fctrs = new ArrayList<>();
    BigInteger num_fctrs;

    @Override
    public boolean IsPrime(BigInteger n, int numThreads) {

        //check if multiple of 2
        if(!n.testBit(0))
            return false;
            
        //Check Fermat's condition with base 2, 2^n-1 % n  == 1
        if (!TWO.modPow(n.subtract(ONE), n).equals(ONE))
            return false;

        ExecutorService tPool = Executors.newFixedThreadPool(numThreads);
        List<Future<Boolean>> list = new ArrayList<>();

        this.n = n;
        this.nMinusOne = n.subtract(ONE); //will be composite of 2
        BigInteger nMinusOnedivTwo = nMinusOne.divide(TWO);

        //for (int i=0; i<known_primes.length; i++) {
        for (int i=0; i<primes_list.length; i++) {
            BigInteger f = BigInteger.valueOf(primes_list[i]); //known_primes[i]
            if (nMinusOnedivTwo.mod(f).equals(ZERO)) //f is a factor of nMinusOne
                fctrs.add(f);
        }

        this.num_fctrs = BigInteger.valueOf(fctrs.size());

        //BigInteger iterations = nMinusOne.multiply(num_fctrs);
        BigInteger iterations = nMinusOne;
        //System.out.println("num iter:"+iterations);

        for (BigInteger i=ZERO; i.compareTo(iterations)<0; i=i.add(ONE)) {
            Callable<Boolean> test = this; //new PocklingtonParallel();
            list.add(tPool.submit(test));
        }

        try {
            for (Future<Boolean> result: list ) {
                if (result.get()) {
                    tPool.shutdownNow();
                    return true;
                }
            }
        }
        catch (Exception e) {
            System.err.println(e);
        }

        tPool.shutdownNow();
        return false;
    }

    @Override
    public Boolean call() throws Exception {
        return pockTest();
    }

    public boolean pockTest() {

        BigInteger local_curr = curr.getAndIncrement();
        BigInteger local_base = local_curr.add(TWO); 
        //BigInteger local_factr = local_curr.mod(num_fctrs); //divideAndRemainder(num_fctrs);

        //Check Fermat's condition first, local_base^n-1 % n = 1
        BigInteger x = local_base.modPow(nMinusOne, n);
        if (!x.equals(ONE)) return false; //not prime

        // since n is odd, then n-1=2F, F>2, gcd(F,2)=1
        BigInteger f = fctrs.get(num_fctrs.intValue()-1);
        //DEBUG
        System.out.println(" b:"+local_base+", f:"+f);//+"\t"+this);

        x = pow(local_base, nMinusOne.divide(f)).subtract(ONE); //x=b^(n-1/f)-1, f>2
        //x = local_base[0].pow(nMinusOne.divide(f).intValue()).subtract(ONE);

        if (n.gcd(x).equals(ONE))
            return true;

        return false;
    }


    public static void main(String[] args) {

        final BigInteger suspect = args.length < 1 ?
                new BigInteger("999983") : new BigInteger(args[0]); // checking for 999983 by default, which is prime
        final int numThreads = args.length < 2 ? 8 : new Integer(args[1]); // numThreads=8 is default
        boolean flag;

        PocklingtonParallel pockTest = new PocklingtonParallel();
        long startTime = System.nanoTime();
        flag = pockTest.IsPrime(suspect, numThreads);
        long runTime = System.nanoTime() - startTime;

        if (flag) System.out.println(suspect + " is prime");
        else System.out.println(suspect + " is not prime");

        System.out.println("Time: " + runTime / 1000.0 + "us");
    }

}




