import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;


public class BruteForceParallel implements Runnable, IPrimalityTest {
	static AtomicInteger stepOneSwitch, stepTwoSwitch, stepThreeSwitch, resultFound; // switches to allow only one thread to execute
	static CyclicBarrier barrier;
	static volatile List<Part> partList;
	static volatile boolean isPrime;
	
	BigInteger n, root;
	
	@Override
	public void run() {
		// only one thread can execute this once
		if (stepOneSwitch.compareAndSet(0, 1) && resultFound.get() == -1) {
			if (n.compareTo(BigInteger.valueOf(2)) < 0) {
				resultFound.compareAndSet(-1, 0);
			}
		}

		// only one thread can execute this once
		if (stepTwoSwitch.compareAndSet(0, 1) && resultFound.get() == -1) {
			if (n.compareTo(BigInteger.valueOf(2)) == 0 || n.compareTo(BigInteger.valueOf(3)) == 0) {
				resultFound.compareAndSet(-1, 1);
			}
		}
		
		// only one thread can execute this once
		// filter out evens
		if (stepThreeSwitch.compareAndSet(0, 1) && resultFound.get() == -1) {
			if (n.mod(BigInteger.valueOf(2)) == BigInteger.ZERO) {
				resultFound.compareAndSet(-1, 0);
			}
		}

		// Theorem: If n is a positive composite integer, then n has a prime
		// divisor less than or equal to sqrt(n).
		// multiple threads can execute
		BigInteger start = new BigInteger("0");
		BigInteger end = new BigInteger("0");
		for (Part p : partList) {
			if (p.id == Thread.currentThread().getId()) {
				start = p.start;
				end = p.end;
			}
		}
		
		for (BigInteger i = start; i.compareTo(end) < 0 && resultFound.get() == -1; i = i.add(FrequentlyUsed.TWO)) {
			if (n.mod(i) == BigInteger.ZERO) {
				resultFound.compareAndSet(-1, 0);
			}
		}

		// waits for all steps to finish
		try {
			barrier.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		
		// declare prime
		if (resultFound.get() == -1 || resultFound.get() == 1) isPrime = true;
	}

	BigInteger sqrt(BigInteger n) {
		BigInteger a = BigInteger.ONE;
		BigInteger b = new BigInteger(n.shiftRight(5).add(new BigInteger("8")).toString());
		while (b.compareTo(a) >= 0) {
			BigInteger mid = new BigInteger(a.add(b).shiftRight(1).toString());
			if (mid.multiply(mid).compareTo(n) > 0)
				b = mid.subtract(BigInteger.ONE);
			else
				a = mid.add(BigInteger.ONE);
		}
		return a.subtract(BigInteger.ONE);
	}
	
	@Override
	public boolean IsPrime(BigInteger n, int numThread) {
		// initialize
		stepOneSwitch = new AtomicInteger(0);
		stepTwoSwitch = new AtomicInteger(0);
		stepThreeSwitch = new AtomicInteger(0);
		resultFound = new AtomicInteger(-1); // -1 = initial state, 0 = composite, 1 = prime
		barrier = new CyclicBarrier(numThread);
		partList = new ArrayList<Part>();
		isPrime = false;
		
		this.n = n;
		root = sqrt(n).add(BigInteger.ONE);
		
		BigInteger partSize = root.divide(BigInteger.valueOf(numThread));
		
		// create threads and run
		Thread[] threads = new Thread[numThread];
		
		// split search space into parts
		for (int i = 0; i < numThread; i++) {
			threads[i] = new Thread(this);
			Part part = new Part();
			part.id = threads[i].getId();
			if (i == 0) part.start = new BigInteger("3");
			else part.start = partList.get(i - 1).start.add(partSize).mod(FrequentlyUsed.TWO) == BigInteger.ZERO ? 
					partList.get(i - 1).start.add(partSize).subtract(BigInteger.ONE) : partList.get(i - 1).start.add(partSize);
			if (i == numThread - 1) part.end = root;
			else part.end = part.start.add(partSize);
			partList.add(part);
		}
		
		for (Thread thread : threads) {
			thread.start();
		}
		
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if (isPrime) return true;
		
		return false;
	}
	
	public class Part {
		long id;
		BigInteger start, end;
	}
}
