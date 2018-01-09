import java.math.BigInteger;
import java.util.Random;


public class QuadraticFrobenius implements IPrimalityTest {
	Random random = new Random();
	
	@Override
	public boolean IsPrime(BigInteger n) {
		if (n.and(BigInteger.ONE) == BigInteger.ZERO) return false;
		if (n.compareTo(BigInteger.ONE) == 0 || n.compareTo(BigInteger.valueOf(2)) == 0) return true;
		getAB(n);
		
		return false;
	}
	
	private BigInteger[] getAB(BigInteger n) {
		BigInteger a = randomBigInteger(n);
		BigInteger b = randomBigInteger(n);
		System.out.println(a + " " + b);
		System.out.println(isSqrt(n));
		return null;
	}
	
	private BigInteger randomBigInteger(BigInteger n) {
        BigInteger r;
		do {
			r = new BigInteger(n.bitLength(), random);
		} while (r.compareTo(n) >= 0 || r.compareTo(BigInteger.ZERO) <= 0);
		
        return r;
    }
	
	private boolean isSqrt(BigInteger n)
	{
		final int bitLength = n.bitLength();
		BigInteger root = BigInteger.ONE.shiftLeft(bitLength / 2);
		final BigInteger lowerBound = root.pow(2);
		final BigInteger upperBound = root.add(BigInteger.ONE).pow(2);
		return lowerBound.compareTo(n) <= 0
			&& n.compareTo(upperBound) < 0;
	}
	
	public static void main(String[] args) {
		QuadraticFrobenius quad = new QuadraticFrobenius();
		for (int i = 0; i < 100; i++) {
			quad.IsPrime(BigInteger.valueOf(10));
		}
	}

}
