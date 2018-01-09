/**
 * Lucas-Lehmer Test:
 * For p an odd prime,
 * the Mersenne number Mp = (2^p)-1 is prime if and only if
 * Mp divides S(p-1), where S(n+1) = (S(n)^2)-2, and S(1) = 4.
 * i.e Mp % S(p-1) = 0
 */

import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class LLParallel extends FrequentlyUsed implements Callable<BigInteger>, IPrimalityTest {

    public static ExecutorService tPool;
    BigInteger n, s, m_p;

    public LLParallel() {}

    public LLParallel(BigInteger level) {
        this.n=level;
    }

    private String getM_p(){
        return m_p.toString();
    }

    @Override
    public BigInteger call() throws Exception { //returns s(p-1)

        if (n.equals(ONE)) return FOUR; //S(1) = 4
        else if (n.equals(TWO)) return BIG13.add(ONE); //S(2) = S(1)^2-2 = 14
        else {
            //Same result from both threads since trying to divide the work among them
            Callable<BigInteger> l1 = new LLParallel(n.subtract(TWO));
            Callable<BigInteger> l2 = new LLParallel(n.subtract(TWO));

            Future<BigInteger> result1 = tPool.submit(l1);
            Future<BigInteger> result2 = tPool.submit(l2);

            BigInteger s_a = result1.get().pow(2).subtract(TWO);
            BigInteger s_b = result2.get().pow(2).subtract(TWO);

            //DEBUG System.out.println(" at level: "+n+"\t"+this);
            return s_a.multiply(s_b).subtract(TWO);
        }
    }

    @Override
    public boolean IsPrime(BigInteger n, int numThreads) {

        m_p = TWO.pow(n.intValue()).subtract(ONE); //set Mp

        if(!m_p.testBit(0)) //check if multiple of 2
            return false;

        //Check Fermat's condition first, 2^n-1 % n  == 1
        if (!TWO.modPow(n.subtract(ONE), n).equals(ONE))
            return false;

        tPool = Executors.newCachedThreadPool();
        //tPool = Executors.newFixedThreadPool(numThreads);
        ExecutorService es = Executors.newSingleThreadExecutor();

        try {

            Callable<BigInteger> lp_obj = new LLParallel(n.subtract(ONE));
            Future<BigInteger> internal = es.submit(lp_obj);

            s = internal.get(); //get S(p-1)
            //DEBUG System.out.println("S(p-1): S("+n.subtract(ONE)+") = "+s);
            //DEBUG System.out.println("Mp % S(p-1) = "+m_p.mod(s));

            es.shutdown();
            tPool.shutdown();
        }
        catch (Exception e) {
            System.err.println(e);
        }

        return s.mod(m_p).equals(ZERO);
    }

    public static void main(String[] args) {

        //requires prime as seed for Marsenne number, i.e M19 is prime, but M23 is not.
        final BigInteger suspect = args.length < 1 ?
                new BigInteger("19") : new BigInteger(args[0]); // checking for  M(19) =(2^19)-1 by default, which is prime
        final int numThreads = args.length < 2 ? 8 : new Integer(args[1]); // numThreads=8 is default
        boolean flag;

        LLParallel LLtest = new LLParallel();
        long startTime = System.nanoTime();
        flag = LLtest.IsPrime(suspect, numThreads); // numThreads will be ignored due to recursive nature of algorithm
        long runTime = System.nanoTime() - startTime;

        String out = "M"+suspect+" = "+LLtest.getM_p();
        if (flag) System.out.println(out + " is prime");
        else System.out.println(out + " is not prime");

        System.out.println("Time: " + runTime / 1000.0 + "us");
    }

}
