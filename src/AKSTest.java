import java.math.BigInteger;
import java.util.concurrent.TimeUnit;


public class AKSTest {
	public static void main(String[] args) {
//		final BigInteger n = new BigInteger("2425967623052370772757633156976982469681");
		final BigInteger n = new BigInteger("9999999999999999999999999999999999999983");
//		final int numThread = 12;
		
		
//		final BigInteger[] primes = new BigInteger[40];
//    	primes[0] = new BigInteger("7");
//    	primes[1] = new BigInteger("97");
//    	primes[2] = new BigInteger("997");
//    	primes[3] = new BigInteger("9973");
//    	primes[4] = new BigInteger("99991");
//    	primes[5] = new BigInteger("999983");
//    	primes[6] = new BigInteger("9999991");
//    	primes[7] = new BigInteger("99999989");
//    	primes[8] = new BigInteger("999999937");
//    	primes[9] = new BigInteger("9999999967");
//    	primes[10] = new BigInteger("99999999977");
//    	primes[11] = new BigInteger("999999999989");
//    	primes[12] = new BigInteger("9999999999971");
//    	primes[13] = new BigInteger("99999999999973");
//    	primes[14] = new BigInteger("999999999999989");
//    	primes[15] = new BigInteger("9999999999999937");
//    	primes[16] = new BigInteger("99999999999999997");
//    	primes[17] = new BigInteger("999999999999999989");
//    	primes[18] = new BigInteger("9999999999999999961");
//    	primes[19] = new BigInteger("99999999999999999989");
//    	primes[20] = new BigInteger("999999999999999999899");
//    	primes[21] = new BigInteger("9999999999999999999973");
//    	primes[22] = new BigInteger("99999999999999999999977");
//    	primes[23] = new BigInteger("999999999999999999999743");
//    	primes[24] = new BigInteger("9999999999999999999999877");
//    	primes[25] = new BigInteger("99999999999999999999999859");
//    	primes[26] = new BigInteger("999999999999999999999999901");
//    	primes[27] = new BigInteger("9999999999999999999999999791");
//    	primes[28] = new BigInteger("99999999999999999999999999973");
//    	primes[29] = new BigInteger("999999999999999999999999999989");
//    	primes[30] = new BigInteger("9999999999999999999999999999973");
//    	primes[31] = new BigInteger("99999999999999999999999999999979");
//    	primes[32] = new BigInteger("999999999999999999999999999999991");
//    	primes[33] = new BigInteger("9999999999999999999999999999999589");
//    	primes[34] = new BigInteger("99999999999999999999999999999999977");
//    	primes[35] = new BigInteger("999999999999999999999999999999999841");
//    	primes[36] = new BigInteger("9999999999999999999999999999999999919");
//    	primes[37] = new BigInteger("99999999999999999999999999999999999941");
//    	primes[38] = new BigInteger("999999999999999999999999999999999999943");
//    	primes[39] = new BigInteger("9999999999999999999999999999999999999983");
    	
//    	for (final BigInteger n : primes) {
//    		// serial
//    		TestingTimer serial = new TestingTimer()
//            {
//    			AKSSerial aks = new AKSSerial();
//    
//                @Override
//                Object functionToRun()
//                {
//                    return aks.IsPrime(n, numThread);
//                }
//            };        
//    		Object serialResult = serial.timeFunction();
//    		System.out.format("Serial \t\t %s prime? %s\t runtime: %d ms \n", n.toString(), serialResult, serial.getLatestTime(TimeUnit.MILLISECONDS));
//    	}
//    	
//    	for (final BigInteger n : primes) {
//    		// parallel
//    		TestingTimer parallel = new TestingTimer()
//            {
//    			AKSParallel aksP = new AKSParallel();
//    
//                @Override
//                Object functionToRun()
//                {
//                    return aksP.IsPrime(n, numThread);
//                }
//            };
//            Object parallelResult = parallel.timeFunction();
//            System.out.format("Parallel \t %s prime? %s\t runtime: %d ms \n", n.toString(), parallelResult, parallel.getLatestTime(TimeUnit.MILLISECONDS));
//    	}
		
		// serial
//		TestingTimer serial = new TestingTimer()
//        {
//			AKSSerial aks = new AKSSerial();
//
//            @Override
//            Object functionToRun()
//            {
//                return aks.IsPrime(n, numThread);
//            }
//        };        
//		Object serialResult = serial.timeFunction();
//		System.out.format("Serial \t\t %s prime? %s\t runtime: %d ms \n", n.toString(), serialResult, serial.getLatestTime(TimeUnit.MILLISECONDS));
		
		for (int i = 2; i < 13; i++) {
			final int numThread = i;
			// parallel
			TestingTimer parallel = new TestingTimer()
			{
				AKSParallel aksP = new AKSParallel();
				
				@Override
				Object functionToRun()
				{
					return aksP.IsPrime(n, numThread);
				}
			};
			Object parallelResult = parallel.timeFunction();
			System.out.format("Parallel \t %s prime? %s\t runtime: %d ms \n", n.toString(), parallelResult, parallel.getLatestTime(TimeUnit.MILLISECONDS));
		}
		
		
//		// finding largest prime for each digit
//		BigInteger n = new BigInteger("99999999999999999999999999999999999999999");
//		final BigInteger z =new BigInteger("999999999999989");
//		final int numThread = 8;
//		
//		while (n.compareTo(z) > 0) {
//			BigInteger i = n;
//			while (true) {
//				AKSParallel aksP = new AKSParallel();
//				if (aksP.IsPrime(i, numThread)) {
//					System.out.format("%s is prime \n", i.toString());
//					break;
//				}
//				i = i.subtract(BigInteger.ONE);
//			}
//			n = n.divide(BigInteger.valueOf(10));
//		}
	}
}
