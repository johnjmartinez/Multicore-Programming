/**
 Input:
 n > 2, an odd Boolean to be tested for primality
 k, a parameter that determines the accuracy of the test

 Output:
 composite if n is composite, otherwise probably prime

 write n-1 as 2s^d with d odd by factoring powers of 2 from n-1

 LOOP: repeat k times:
    pick a, randomly in the range [2, n - 1]
    x <- a^d mod n
    if x=1 or x=n-1 then do next LOOP
    repeat for s-1 times:
        x <- x^2 mod n
        if x=1 then return composite
        if x=n-1 then do next LOOP
    return composite
 return probably prime
 **/

import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class MRParallel extends FrequentlyUsed implements Callable<Boolean>, IPrimalityTest {

    BigInteger n, nMinusOne;
    final static int ITERATIONS = 256;
    final static int CONFIDENCE_THRESHOLD = ITERATIONS/75;

    public MRParallel () {}

    @Override
    public Boolean call() throws Exception {

        BigInteger rndm, x;

        //Choose a random num smaller than n
        do {
            rndm = new BigInteger(n.bitLength() - 1, new Random());
        } while (rndm.compareTo(TWO)<=0);

        //Check Fermat's condition first, rndm^n-1 % n  == 1
        x = rndm.modPow(nMinusOne, n);
        if (!x.equals(ONE)) return false; //not prime

        /*
         Divide/Mod n-1 by 2
          dr[0]=nMinusOne/2, dr[1]=nMInusOne%2
         */
        BigInteger[] dr = nMinusOne.divideAndRemainder(TWO);

        //Perform the root tests by incrementally dividing by 2 until mod = 0
        while (dr[1].equals(ZERO)) { //(n-1)%2=0

            x = rndm.modPow(dr[0], n); // x=rndm^{(n-1)/2}%n

            //if x=-1, this is a PASS, get out
            if (x.equals(MINUSONE)||x.equals(nMinusOne)) break; //pass

            //Now, if x not -1 or 1, this is a FAIL
            if (!x.equals(ONE)) return false; //not prime

            //If its x=1, so far it's a PASS
            //We can continue with the test divide by 2
            dr = dr[0].divideAndRemainder(TWO); //(n-1)/(2^2)
        }

        return true;
    }

    @Override
    public boolean IsPrime(BigInteger n, int numThreads) {

        if(!n.testBit(0)) //check if multiple of 2
            return false;

        //Check Fermat's condition with base 2, 2^n-1 % n  == 1
        if (!TWO.modPow(n.subtract(ONE), n).equals(ONE))
            return false;

        this.n = n;
        this.nMinusOne = n.subtract(ONE);

        int positive_tests = 0;

        List<Future<Boolean>> list = new ArrayList<>();
        ExecutorService tPool = Executors.newFixedThreadPool(numThreads);

        for (int i=0; i<ITERATIONS; i++) {
            Callable<Boolean> test = this;
            list.add(tPool.submit(test));
        }

        try {
            for (Future<Boolean> result: list ) {

                if (result.get()) {
                    positive_tests++;
                    if (positive_tests > CONFIDENCE_THRESHOLD) {
                        tPool.shutdownNow();
                        return true; // will go through a percentage of positive iterations
                    }
                }
            }
        }
        catch (Exception e) {
            System.err.println(e);
        }
        tPool.shutdownNow();
        return false; // will go through ALL iterations
    }

    public static void main(String[] args) {

        final BigInteger suspect = args.length < 1 ?
                new BigInteger("81173951511377") : new BigInteger(args[0]);
        final int numThreads = args.length < 2 ? 8 : new Integer(args[1]); // numThreads=8 is default
        boolean flag;

        MRParallel MRtest = new MRParallel();
        long startTime = System.nanoTime();
        flag = MRtest.IsPrime(suspect, numThreads);
        long runTime = System.nanoTime() - startTime;

        if (flag) System.out.println(suspect + " is very probably prime");
        else System.out.println(suspect + " is not prime");

        System.out.println("Time: " + runTime / 1000.0 + "us");
    }

}

