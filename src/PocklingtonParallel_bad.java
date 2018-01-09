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

/** https://github.com/dvberkel/pocklington/blob/master/pocklington/criteria.py **/
public class PocklingtonParallel_bad extends FrequentlyUsed implements Callable<BigInteger>, IPrimalityTest {

    BigInteger p, pMinusOne;
    BigInteger MAX = BigInteger.valueOf(1000000);
    AtomicBigInteger a = new AtomicBigInteger(TWO);;

    private void setSuspect(BigInteger q) {
        // p = 2q+1 satisfies q | p-1 (all prime factors q for p-1)
        p = q.multiply(TWO).add(ONE);
        pMinusOne = p.subtract(ONE);
        if (MAX.compareTo(p) < 0) MAX = p;
    }

    @Override
    public BigInteger call() throws Exception {
    //previously: BigInteger candidateTo(BigInteger p)
    // s satisfies a^(p-1) % p == 1 mod p

        BigInteger local = a.getAndAdd(ONE);
        //DEBUG System.out.println("local:"+local+", p:"+p);
        if (local.modPow(pMinusOne, p).equals(ONE)) // prev: while( a^(p-1) mod p != 1) , a++;
            return local;  // Return  a  s.t.  a^(p-1) % p = 1
        return ZERO;
    }

    @Override
    public boolean IsPrime(BigInteger n) {
        setSuspect(n);
        return isGermainPrime();
    }


    // p is a Sophie Germain prime if 2p+1 is also prime
    // check if 2p+1 is prime; if so, then p is prime too
    public boolean isGermainPrime() {

        BigInteger candidate = ZERO; // candidateTo(p);
        ExecutorService tPool = Executors.newFixedThreadPool(8);
        List<Future<BigInteger>> list = new ArrayList<>();

        for (BigInteger i = ONE; i.compareTo(MAX) < 0; i=i.add(ONE)) {
            Callable<BigInteger> test = this;
            list.add(tPool.submit(test));
        }

        try {
            for (Future<BigInteger> result: list ) {
                candidate = result.get();
                if (!candidate.equals(ZERO)) break;
            }
        }
        catch (Exception e) {
            System.err.println(e);
        }

        tPool.shutdownNow();
        System.out.println("candidate:" + candidate + ", counter:" + a.intValue() + ", germain number:" + p);
        if (candidate.equals(ZERO)) return false;

        return p.gcd(candidate.pow(2).subtract(ONE)).equals(ONE);
    }


    public static void main(String[] args) {

        final BigInteger n = args.length < 1 ?
                new BigInteger("999999937") : new BigInteger(args[0]);

        long startTime = System.nanoTime();

        //BigInteger germain = n.multiply(TWO).add(ONE);
        PocklingtonParallel PP = new PocklingtonParallel();
        boolean flag = PP.IsPrime(n.subtract(ONE).divide(TWO));

        long runTime = System.nanoTime() - startTime;
        //String out = "G"+n+" = "+PP.getGermain();

        if (flag) System.out.println(n + " is prime");
        else System.out.println(n + " is not prime");

        System.out.println("Time: " + runTime / 1000.0 + "us");
    }



}




