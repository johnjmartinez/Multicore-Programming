/**
limit := k    // arbitrary search limit

// set of wheel "hit" positions for a 2/3/5 wheel rolled twice as per the Atkin algorithm
s := {1,7,11,13,17,19,23,29,31,37,41,43,47,49,53,59}

// Initialize the sieve with enough wheels to include limit:
for n := 60w + x, w E {0,1,...,limit / 60}, x E s:
  is_prime(n) := false

// Put in candidate primes:
//  integers which have an odd number of
//  representations by certain quadratic forms.

// Algorithm step 3.1:
for n <= limit, n := 4x^2+y^2, x E {1,2,...} and y E {1,3,...} // all x's, odd y's
  if n mod 60 E {1,13,17,29,37,41,49,53}:
    is_prime(n) := !is_prime(n)  // toggle state
    
// Algorithm step 3.2:
for n <= limit, n := 3x^2+y^2, x E {1,3,...} and y E {2,4,...} // odd x's, even y's
  if n mod 60 E {7,19,31,43}:    
    is_prime(n) := !is_prime(n)  // toggle state
    
// Algorithm step 3.3:
for n <= limit, n := 3x^2-y^2, x E {2,3,...} and y E {x-1,x-3,...,1} //all even/odd
  if n mod 60 E {11,23,47,59}:   // odd/even combos
    is_prime(n) := !is_prime(n)  // toggle state

// Eliminate composites by sieving, only for those occurrences on the wheel:
for n^2 <= limit, n := 60w + x, w E {0,1,...}, x E s, n >= 7:
  if is_prime(n):
    // n is prime, omit multiples of its square; this is sufficient 
    // because square-free composites can't get on this list
    for c <= limit, c := n^2(60w + x), w E {0,1,...}, x E s:
      is_prime(c) := false

// one sweep to produce a sequential list of primes up to limit:
output 2, 3, 5
for 7 <= n <= limit, n := 60w + x, w E {0,1,...}, x E s:
  if is_prime(n): output n
  
**/

import java.util.*;
import java.math.BigInteger ;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SieveAtkinParallel extends FrequentlyUsed implements IPrimeGenerator, Callable<Void> {

    BigInteger max, max_root;
    HashMap<BigInteger, Boolean> map;
    ArrayList<BigInteger> results = new ArrayList<>();
    AtomicBigInteger a = new AtomicBigInteger(ZERO);

    boolean task;
    static final boolean QUADR = false;
    static final boolean CLEAN = true;

    private synchronized void flipKey (BigInteger i) {
        if (!map.containsKey(i))
            map.put(i, true);
        else {
            boolean v = map.get(i).booleanValue();
            map.put(i, !v);
        }
    }

    private synchronized void falseKey (BigInteger i) {
        map.put(i, false);
    }

    private synchronized boolean getKeyVal (BigInteger i) {
        if (!map.containsKey(i)) return false;
        return map.get(i);
    }

    @Override
    public List<BigInteger> GeneratePrimes(BigInteger n, int numThreads) {

        this.max = n;
        this.max_root = sqrt(max).add(TWO);

        results.add(TWO);
        results.add(THREE);

        map = new HashMap<>();

        List<Future<Void>> list = new ArrayList<>();
        ExecutorService tPool = Executors.newFixedThreadPool(numThreads);

        task = QUADR; // check quadratic
        for (BigInteger i=ZERO; i.compareTo(max)<0; i=i.add(ONE)) {
            Callable<Void> genT = this;
            list.add(tPool.submit(genT));
        }
        try {
            for (Future<Void> result: list )
                result.get();
        }
        catch (Exception e) {
            System.err.println(e+"\t QUADR"+this);
        }
        tPool.shutdownNow();

        task = CLEAN; //clean_perfect_squares
        a = new AtomicBigInteger(FIVE); //reset counter
        tPool = Executors.newFixedThreadPool(numThreads); //reset pool
        for (BigInteger i=FIVE; i.compareTo(max_root)<0; i=i.add(ONE)) {
            Callable<Void> genT = this;
            list.add(tPool.submit(genT));
        }
        try {
            for (Future<Void> result: list )
                result.get();
        }
        catch (Exception e) {
            System.err.println(e+"\tCLEAN "+this);
        }
        tPool.shutdownNow();

        //FLATTEN MAP
        for (BigInteger p: map.keySet()) {
            if(map.get(p))
                results.add(p);
        }

        results.remove(ONE);
        Collections.sort(results);
        return results;
    }

    @Override
    public Void call() throws Exception {

        BigInteger i = a.getAndIncrement();

        if (task == QUADR) {

            BigInteger x = i.divide(max_root);
            BigInteger y = i.mod(max_root);

            BigInteger q1 = multSquare(FOUR, x).add(y.pow(2));
            BigInteger q2 = multSquare(THREE, x).add(y.pow(2));
            BigInteger q3 = multSquare(THREE, x).subtract(y.pow(2));

            if (q1.compareTo(max) <= 0 && (q1.mod(BIG12).equals(ONE) || q1.mod(BIG12).equals(FIVE))) flipKey(q1);
            if (q2.compareTo(max) <= 0 && q2.mod(BIG12).equals(SEVEN) ) flipKey(q2);
            if (q3.compareTo(max) <= 0 && q3.mod(BIG12).equals(BIG11) && x.compareTo(y) > 0) flipKey(q3);

        }

        else if(task == CLEAN) {

            if (getKeyVal(i)) {
                BigInteger i2 = i.pow(2);
                for (BigInteger p = i2; p.compareTo(max) <= 0; p = p.add(i2))
                    falseKey(p);
            }
        }

        return null;
    }

    private void clean_perfect_squares() {
        for (BigInteger p=ZERO; p.compareTo(max_root)<0; p=p.add(ONE)) {
            if (map.containsKey(p) && map.get(p)) {
                BigInteger x = p.pow(2);
                for (BigInteger i = x; i.compareTo(max) <= 0; i = i.add(x))
                    map.put(i, false);
            }
        }
    }

    public void display(ArrayList<BigInteger> list) {
        System.out.print("\nPrimes = ");
        for (BigInteger p: list)
            System.out.print(p +" ");
        System.out.println();
    }

    public static void main(String[] args) throws InterruptedException {

        final BigInteger target = args.length < 1 ?
                BigInteger.valueOf(1_000_000) : new BigInteger(args[0]); // target is 1 million
        final int numThreads = args.length < 2 ? 8 : new Integer(args[1]); // numThreads=8 is default
        boolean flag;

        SieveAtkinParallel atkinsDiet = new SieveAtkinParallel();
        long startTime = System.nanoTime();
        ArrayList<BigInteger> list = (ArrayList<BigInteger>) atkinsDiet.GeneratePrimes(target, numThreads);
        long runTime = System.nanoTime() - startTime;
        atkinsDiet.display(list);

        System.out.println("Time: " + runTime / 1000.0 + "us");
	}
    
    
}