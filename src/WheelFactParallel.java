import java.util.List;
import java.util.LinkedList;
import java.math.BigInteger ;
import java.util.concurrent.Callable;

public class WheelFactParallel extends FrequentlyUsed implements Callable<BigInteger> {
   
    static AtomicBigInteger LOCAL_INT = new AtomicBigInteger("2"); // starts at 2

    //skip array up to 210 (2x3x5x7)
    //                                  11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89 97 101 103  
    private static final int[] WHEEL1 = {4, 2, 4, 2, 4, 6, 2, 6, 4, 2, 4, 6, 6, 2, 6, 4, 2, 6, 4, 6, 8, 4,  2,  
                                4,  2,  4,  14, 4,  4,  6,  10, 2,  6,  6,  4,  6,  6,  2,  10, 2,  4,  2};
    //                          107 109 113 127 131 137 139 149 151 157 163 167 173 179 181 191 193 197 199 211

    //private static final int[] WHEEL2 = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79,
    private static final int[] WHEEL2 = { 1, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79,
     83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199}; //211
     
    private static final int WL1 = WHEEL1.length;
    private static final int WL2 = WHEEL2.length;
    private static final BigInteger BWL1 = BigInteger.valueOf(WL1);
    private static final BigInteger BWL2 = BigInteger.valueOf(WL2);

    volatile static Boolean notPrime = false;
    private BigInteger suspect, root, offset, start;
        
    //GENERATE_PRIME LIST     
    public WheelFactParallel() { 
    }
    
    @Override
    public BigInteger call() throws Exception {
    	return generatePrimes();
    }

    //CHECK_PRIME N
    public WheelFactParallel(BigInteger n, int id, int numT) { 
        
    	this.suspect = n;
        this.root = sqrt(n);
        this.start = BigInteger.valueOf(id+1);
    	this.offset = BigInteger.valueOf(numT);
        //System.out.println(id+" "+numT+" "+notPrime);
    }

    @Override
    public void run() {
    	checkPrime();
    }
    
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
    private boolean checkWheel(BigInteger num) { //returns true if num is prime
    
        BigInteger tmp;  
        BigInteger track = _210_.multiply(num.divide(_210_));
        for (int i = 0; i < WL2 ; i++) {
        
            tmp = track.add(BigInteger.valueOf(WHEEL2[i%WL2]));
            if (num.compareTo(tmp) == 0) return true;
        } 
        //System.out.println("NUM+SPOKE+LAST_TMP "+num+" "+spoke+" "+tmp);
        return false;  	
	}
    
    private BigInteger grabFromWheel(BigInteger num) {  //returns num at wheel spoke      
        
        BigInteger spoke = _210_.multiply(num.divide(BWL2));
        int i = num.mod(BWL2).intValue();
        spoke = spoke.add(BigInteger.valueOf(WHEEL2[i]));
        return spoke;  	
	}
    
    public void checkPrime() { //notPrime might be set by a thread 
         
        if (!quickCompare(suspect)) {
            for (BigInteger i=start; !notPrime && i.compareTo(root) < 0; i=i.add(offset)) {
                if (suspect.mod(grabFromWheel(i)).equals(ZERO)) //check if suspect has factor in wheel
                    notPrime = true;
            } 
        }    
        return;
    }    
    
    public BigInteger generatePrimes() {   
     
    	BigInteger thread_int = LOCAL_INT.getAndIncrement();     
        
        if (quickCompare(thread_int)) 
            return thread_int;
        else {
            if (checkWheel(thread_int))
                return thread_int;
        }           
        
        return ZERO;
    }

}

