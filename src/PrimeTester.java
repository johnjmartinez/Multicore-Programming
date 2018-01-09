import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class PrimeTester extends Thread {
	BigInteger start;
	BigInteger end;
	int test;
	int numThread = 4;
	static volatile String bfpResult = "";
	static volatile String aksResult = "";
	static volatile String fpResult = "";
	static volatile String sstResult = "";
	static volatile String bptResult = "";
	static volatile String mrpResult = "";
	
	public PrimeTester (int test, String start, String end) {
		this.test = test;
		this.start = new BigInteger(start);
		this.end = new BigInteger(end);
	}
	
	public void run() {
		switch (test) {
		case 0:
			BruteForceParallel bfp = new BruteForceParallel();
			for (BigInteger i = start; i.compareTo(end) < 0; i = i.add(BigInteger.ONE)) {
				if (bfp.IsPrime(i, numThread)) bfpResult = i.toString();
				print();
			}
			break;
		case 1:
			AKSParallel aks = new AKSParallel();
			for (BigInteger i = start; i.compareTo(end) < 0; i = i.add(BigInteger.ONE)) {
				if (aks.IsPrime(i, numThread)) aksResult = i.toString();
				print();
			}
			break;
		case 2:
			FermatParallelTest fp = new FermatParallelTest(256);
			for (BigInteger i = start; i.compareTo(end) < 0; i = i.add(BigInteger.ONE)) {
				if (fp.IsPrime(i, numThread)) fpResult = i.toString();
				print();
			}
			break;
		case 3:
			SolovayStrassenTest sst = new SolovayStrassenTest();
			for (BigInteger i = start; i.compareTo(end) < 0; i = i.add(BigInteger.ONE)) {
				if (sst.IsPrime(i, numThread)) sstResult = i.toString();
				print();
			}
			break;
		case 4:
			BailliePswTest bpt = new BailliePswTest();
			for (BigInteger i = start; i.compareTo(end) < 0; i = i.add(BigInteger.ONE)) {
				if (bpt.IsPrime(i, numThread)) bptResult = i.toString();
				print();
			}
			break;
		}
	}
	
	private void print() {
		System.out.format("BF: %s\t\t AKS: %s\t\t Fermat: %s\t\t SS: %s\t\t BPSW: %s\t\t\n", 
				bfpResult, aksResult, fpResult, sstResult, bptResult);
	}
	
    public static void main(String[] args) {
    	String start = "";
    	String end = "";
    	if (args.length == 2) {
    		start = args[0];
    		end = args[1];
    	} else {
    		start = "2";
    		end = "9999999999999999999999999999999999999983";
    	}
    	
    	PrimeTester[] pt = new PrimeTester[5];
    	for (int i = 0; i < 5; i++) {
    		pt[i] = new PrimeTester(i, start, end);
    		pt[i].start();
    	}
    	
    	for (int i = 0; i < 5; i++) {
    		try {
				pt[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	
//    	TestingTimer parallel = new TestingTimer()
//    	{
//    		RangeTester rt = new RangeTester();
//    		
//    		@Override
//    		Object functionToRun() {
//    			try {
//					return rt.testRange(new BigInteger("9999"), 1, 8, new SolovayStrassenTest());
//				} catch (InterruptedException | ExecutionException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//    			return null;
//    		}
//    		
//    	};
//    	
//    	Object list = parallel.timeFunction();
//    	for (BigInteger i : (List<BigInteger>) list) {
//    		System.out.println(i.toString());
//    	}
//    	System.out.format("Executed in %d ms", parallel.getLatestTime(TimeUnit.MILLISECONDS));
    	
    	// deterministic
//    	final BigInteger n = new BigInteger("9999999999999999999999999999999999999983");
//    	final int numThread = 1;
//
//    	// parallel
//		TestingTimer parallel = new TestingTimer()
//		{
//			BailliePswTest test = new BailliePswTest();
//			
//			@Override
//			Object functionToRun() {
//				return test.IsPrime(n, numThread);
//			}
//			
//		};
//		
//		Object parallelResult = parallel.timeFunction();
//		System.out.format("Parallel \t %s probable prime? %s\t runtime: %d ms\n", n.toString(),
//				parallelResult, parallel.getLatestTime(TimeUnit.MILLISECONDS));
    	
//    	// probabilistic
//		final int numThread = 4;
//    	final BigInteger n = new BigInteger("100000");
//		
//		for (int i = 1; i < 13; i++) {
//			final int j = i;
//			// parallel
//			TestingTimer parallel = new TestingTimer()
//			{
//				WheelFactParallelTest test = new WheelFactParallelTest(j);
//				
//				@Override
//				Object functionToRun() {
//					return test.GeneratePrimes(n, j);
//				}
//				
//			};
//			
//			Object parallelResult = parallel.timeFunction();
//			System.out.format("Parallel \t %s probable prime? \t runtime: %d ms with %d thread\n", n.toString(), 
//					parallel.getLatestTime(TimeUnit.MILLISECONDS), j);
//		}
    }
}
