import java.math.BigInteger;

public class BruteForceSerial implements IPrimalityTest {
	
	@Override
	public boolean IsPrime(BigInteger n, int numThread) {
		if (n.compareTo(BigInteger.valueOf(2)) < 0) {
			return false;
		}

		if (n.compareTo(BigInteger.valueOf(2)) == 0) {
			return true;
		}

		// filter out evens
		if (n.mod(BigInteger.valueOf(2)) == BigInteger.ZERO) {
			return false;
		}

		// Theorem: If n is a positive composite integer, then n has a prime
		// divisor less than or equal to sqrt(n).
		BigInteger root = sqrt(n).add(BigInteger.ONE);
		for (BigInteger i = new BigInteger("3"); i.compareTo(root) < 0; i = i.add(FrequentlyUsed.TWO)) {
			if (n.mod(i) == BigInteger.ZERO) {
				return false;
			}
		}

		return true;
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
}
