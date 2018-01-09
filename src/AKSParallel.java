import java.math.BigInteger;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

/************************************************************************
 * A parallel adaptation of 
 * Reference: Hossain, Abdullah A.Z. (2007) - Implementation of the AKS Primality Testing Algorithm
 ************************************************************************/
//http://www.articulatelogic.com/getfile.php?f=Implementation_of_AKS_Algorithm.pdf
// http://www.articulatelogic.com/download/Implementation_of_AKS_Algorithm

public class AKSParallel implements Runnable, IPrimalityTest {
	static AtomicInteger stepOneSwitch, rFound, endRLoop, tr, aAI, resultFound;
	static AtomicBigInteger abR;
	
	static CyclicBarrier rBarrier, loopBarrier, stepThreeBarrier, finalBarrier;
	
	static volatile boolean isPrime;
	
	BigInteger n;
	
	// algorithm variables
	private int N;
	private int l;
	private boolean comp[];
	
	public AKSParallel() {
		Sieve();
	}
	
	/*
	 * Algorithm
	 * 
	 */
	public boolean isSmPrime(int n) {
		if (comp[n] == false)
			return true;
		else
			return false;
	}
	
	public int largestFact(int n) {
		int i;
		i = n;
		if (i == 1)
			return i;
		while (i > 1) {
			while (comp[i] == true)
				i--;
			if (n % i == 0)
				return i;
			i--;
		}
		return n;
	}

	public double bigLog(BigInteger s) {
		String t;
		int l;
		double d, r;
		t = "." + s.toString();
		l = t.length() - 1;
		d = Double.parseDouble(t);
		r = Math.log10(d) + l;
		return r;
	}
	
	public boolean isPowerOf(BigInteger n, int i) {
		int l;
		double len;
		BigInteger low, high, mid, res;
		low = new BigInteger("10");
		high = new BigInteger("10");
		len = (n.toString().length()) / i;
		l = (int) Math.ceil(len);
		low = low.pow(l - 1);
		high = high.pow(l).subtract(BigInteger.ONE);
		while (low.compareTo(high) <= 0) {
			mid = low.add(high);
			mid = mid.divide(new BigInteger("2"));
			res = mid.pow(i);
			if (res.compareTo(n) < 0) {
				low = mid.add(BigInteger.ONE);
			} else if (res.compareTo(n) > 0) {
				high = mid.subtract(BigInteger.ONE);
			} else if (res.compareTo(n) == 0) {
				return true;
			}
		}
		return false;
	}
	
	boolean isPower(BigInteger n) {
		int l, i;
		l = (int) bigLog(n);
		for (i = 2; i < l; i++) {
			if (isPowerOf(n, i)) {
				return true;
			}
		}
		return false;
	}
	
	BigInteger mPower(BigInteger x, BigInteger y, BigInteger n) {
		BigInteger m, p, z, two;
		m = y;
		p = BigInteger.ONE;
		z = x;
		two = new BigInteger("2");
		while (m.compareTo(BigInteger.ZERO) > 0) {
			while (((m.mod(two)).compareTo(BigInteger.ZERO)) == 0) {
				m = m.divide(two);
				z = (z.multiply(z)).mod(n);
			}
			m = m.subtract(BigInteger.ONE);
			p = (p.multiply(z)).mod(n);
		}
		return p;
	}
	
	public void Sieve() {
		int i, j;
		N = 1000000;
		comp = new boolean[N + 1];
		comp[1] = true;
		for (i = 2; i * i <= N; i++) {
			if (comp[i] != true) {
				for (j = i * i; j <= N; j += i) {
					comp[j] = true;
				}
			}
		}
	}
	
	public void findR() {
		int tr, q, tm, o;
		BigInteger r, t;
		
		r = abR.getAndIncrement();
		while (endRLoop.get() == 0) {
			if (r.compareTo(n) < 0 && rFound.get() == 0 && resultFound.get() == -1) {
				if ((r.gcd(n)).compareTo(BigInteger.ONE) != 0)
					resultFound.compareAndSet(-1, 0);
				tr = r.intValue();
				if (isSmPrime(tr)) {
					q = largestFact(tr - 1);
					o = (int) (tr - 1) / q;
					tm = (int) (4 * (Math.sqrt(tr)) * l);
					t = mPower(n, new BigInteger("" + o), r);
					if (q >= tm && (t.compareTo(BigInteger.ONE)) != 0) {
						rFound.compareAndSet(0, 1);
						AKSParallel.tr.compareAndSet(-1, tr);
					}
				}
				
			} else {
				try {
					loopBarrier.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
				
				if ((n.subtract(r)).compareTo(BigInteger.ONE) == 0) AKSParallel.tr.compareAndSet(-1, r.intValue());
				
				endRLoop.compareAndSet(0, 1);
			}
			r = abR.getAndIncrement();
		}
		
		// waits for all threads to finish this method before setting
		// tr value to be used in stepThree if not done so in loop already
		try {
			rBarrier.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		
		// set tr if not already set in loop
		AKSParallel.tr.compareAndSet(-1, 2);
	}
	
	public void stepThree() {
		int ai, up;
		BigInteger x, lh, rh, yai;
		x = new BigInteger("2");
		up = (int) (2 * Math.sqrt(tr.get()) * l);
		ai = aAI.getAndIncrement();
		while (ai < up && resultFound.get() == -1) {
			yai = new BigInteger("" + ai);
			lh = (mPower(x.subtract(yai), n, n)).mod(n);
			rh = (mPower(x, n, n).subtract(yai)).mod(n);
			if (lh.compareTo(rh) != 0)
				resultFound.compareAndSet(-1, 0);
			ai = aAI.getAndIncrement();
		}
		
		// waits for all threads to finish this method before declaring
		// for sure number is prime
		try {
			stepThreeBarrier.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		
		// declare prime
		resultFound.compareAndSet(-1, 1);
	}
	
	@Override
	public void run() {
		// first thread to get to first step will execute it while closing the method for access for good afterwards
		if (stepOneSwitch.compareAndSet(1, 0)) {
			if (isPower(n)) resultFound.compareAndSet(-1, 0);
		}
		
		// multiple threads can enter this
		findR();
		
		// after r is found, can execute step 3
		stepThree();
		
		// waits for all steps to finish
		try {
			finalBarrier.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
	}
	
	private class PrimeTrue implements Runnable {
		// method that gets called by the last thread that breaks the lastBarrier
		@Override
		public void run() {
			switch (resultFound.get()) {
			case -1:
				try {
					throw new Exception("Algorithm inconclusive.");
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 0:
				isPrime = false;
				break;
			case 1:
				isPrime = true;
				break;
			}
		}
	}

	@Override
	public boolean IsPrime(BigInteger n, int numThread) {
		// initialize
		stepOneSwitch = new AtomicInteger(1); // 1 = available, 0 = unavailable
		
		rFound = new AtomicInteger(0);
		endRLoop = new AtomicInteger(0);
		
		tr = new AtomicInteger(-1);
		abR = new AtomicBigInteger(new BigInteger("2"));
		aAI = new AtomicInteger(1);
		
		rBarrier = new CyclicBarrier(numThread);
		loopBarrier = new CyclicBarrier(numThread);
		stepThreeBarrier = new CyclicBarrier(numThread);
		finalBarrier = new CyclicBarrier(numThread, new PrimeTrue());
		
		resultFound = new AtomicInteger(-1); // -1 = initial state, 0 = composite, 1 = prime
		isPrime = false;
		
		this.n = n;
		l = (int) bigLog(n);
		
		// create threads and run
		Thread[] threads = new Thread[numThread];
		
		for (int i = 0; i < numThread; i++) {
			threads[i] = new Thread(this);
			threads[i].start();
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
}
