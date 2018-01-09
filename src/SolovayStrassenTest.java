/**
 * This code is a modified form of the code found here:
 * https://code.google.com/p/elements-of-programming-interviews/source/browse/trunk/SolovayStrassen.java
 * 
 * Authors: Adnan Aziz, Tsung-Hsien Lee, Amit Prakash
 * 
 * Modified by: Ryan Harrod
 * 
 * The original code provides a serial implementation of the Solovay-Strassen primality test.  The modified code attempts
 * at speeding up the test through parallelization.
 */
import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
public class SolovayStrassenTest implements IPrimalityTest
{

 // constants used in computation of Jacobi symbol
    static final BigInteger ZERO = BigInteger.ZERO;
    static final BigInteger TWO = new BigInteger("2");
    static final BigInteger THREE = TWO.add(BigInteger.ONE);
    static final BigInteger FOUR = TWO.add(TWO);
    static final BigInteger SEVEN = FOUR.add(THREE);
    static final BigInteger EIGHT = FOUR.add(FOUR);

    // @include
    static int Jacobi(BigInteger m, BigInteger n)
    {
        if (m.compareTo(n) >= 0)
        {
            m = m.mod(n);
            return Jacobi(m, n);
        }
        if (n.equals(BigInteger.ONE) || m.equals(BigInteger.ONE))
        {
            return 1;
        }
        if (m.equals(BigInteger.ZERO))
        {
            return 0;
        }

        int twoCount = 0;
        while (m.mod(TWO) == BigInteger.ZERO)
        {
            twoCount++;
            m = m.divide(TWO);
        }
        int J2n = n.mod(EIGHT).equals(BigInteger.ONE) || n.mod(EIGHT).equals(SEVEN) ? 1 : -1;
        int rule8multiplier = (twoCount % 2 == 0) ? 1 : J2n;

        int tmp = Jacobi(n, m);
        int rule6multiplier = n.mod(FOUR).equals(BigInteger.ONE)
                || m.mod(FOUR).equals(BigInteger.ONE) ? 1 : -1;

        return tmp * rule6multiplier * rule8multiplier;
    }

    static int eulerCriterion(BigInteger p, BigInteger a)
    {
        BigInteger exponent = (p.subtract(BigInteger.ONE)).divide(TWO);
        BigInteger x = a.modPow(exponent, p);
        if (x.equals(BigInteger.ZERO) || x.equals(BigInteger.ONE))
        {
            return x.intValue();
        }
        BigInteger y = x.add(BigInteger.ONE).mod(p);
        return (y.equals(BigInteger.ZERO)) ? -1 : 2;
    }
    private volatile boolean isPrime = true;
    @Override
    public boolean IsPrime(BigInteger n, int numThread)
    {
        isPrime = true;
        Random r = new Random();
        final int numberOfTests = 256;
        ExecutorService service = Executors.newFixedThreadPool(numThread);
        
        for (int i = 0; i < numberOfTests; i++)
        {
            service.execute(new Worker(n, r));
        }
        
        service.shutdown();
        try
        {
            service.awaitTermination(1L, TimeUnit.DAYS);
        } catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return isPrime;
    }
    
    private class Worker implements Runnable
    {
        BigInteger n;
        Random r;
        int numBits;
        
        public Worker(BigInteger number, Random rand)
        {
            n = number;
            r = rand;
            numBits = n.bitLength();
        }
        @Override
        public void run()
        {
            if (n.equals(TWO))
            {
                isPrime = true;
                return;
            }
            if (n.mod(TWO).equals(ZERO))
            {
                isPrime = false;
            }
            if (!isPrime)
                return;
            BigInteger rBig = new BigInteger(numBits, r);
            int ec = eulerCriterion(n, rBig);
            int js = Jacobi(rBig, n);
            if (ec != js)
            {
                isPrime = false;
            }
        }
        
    }
    
    public static void timed()
    {
        BigInteger n = new BigInteger("34608828249085121524296039576741331672262866890023854779048928344500622080983411446436437554415370753366448674763505018641470709332373970608376690404229265789647993709760358469552319045484910050304149809818540283507159683562232941968059762281334544739720849260904855192770626054911793590389060795981163838721432994278763633095377438194844866471124967685798888172212033000821469684464956146997194126921284336206463313859537577200462442029064681326087558257488470489384243989270236884978643063093004422939603370010546595386302009073043944482202559097406700597330570799507832963130938739885080198416258635194522913042562936679859587495721031173747796418895060701941717506001937152430032363631934265798516236047451209089864707430780362298307038193445486493756647991804258775574973833903315735082891029392359352758617185019942554834671861074548772439880729606244911940066680112823824095816458261761861746604034802056466823143718255492784779380991749580255263323326536457743894150848953969902818530057870876229329803338285735419228259022169602665532210834789602051686546011466737981306056247480055071718250333737502267307344178512950738594330684340802698228963986562732597175372087295649072830289749771358330867951508710859216743218522918811670637448496498549094430541277444079407989539857469452772132166580885754360477408842913327292948696897496141614919739845432835894324473601387609643750514699215032683744527071718684091832170948369396280061184593746143589068811190253101873595319156107319196071150598488070027088705842749605203063194191166922106176157609367241948160625989032127984748081075324382632093913796444665700601391278360323002267434295194325607280661260119378719405151497555187549252134264394645963853964913309697776533329401822158003182889278072368602128982710306618115118964131893657845400296860012420391376964670183983594954112484565597312460737798777092071706710824503707457220155015899591766244957768006802482976673920392995410164224776445671222149803657927708412925555542817045572430846389988129960519227313987291200902060882060733762075892299473666405897427035811786879875694315078654420055603469625309399653955932310466430039146465805452965014040019423897552675534768248624631951431493188170905972588780111850281190559073677771187432814088678674286302108275149258477101296451833651979717375170900505673645964696355331369819296000267389583289299126738345726980325998955997501176664201042888546085699446442834195232948787488410595750197438786353119204210855804692460582533832967771946911459901921324984968810021189968284941331573164056304725480868921823442538199590383852412786840833479611419970101792978355653650755329138298654246225346827207503606740745956958127383748717825918527473164970582095181312905519242710280573023145554793628499010509296055849712377978984921839997037415897674154830708629145484724536724572622450131479992681684310464449439022250504859250834761894788889552527898400988196200014868575640233136509145628127191354858275083907891469979019426224883789463551");
        
        int maxThreads = 1;
        for (int i = 1; i < maxThreads; i++)
        {
            PrimeTestTimer t = new PrimeTestTimer(n, i)
            {
                @Override
                Object functionToRun()
                {
                    SolovayStrassenTest s = new SolovayStrassenTest();
                    Boolean isPrime = s.IsPrime(n, threads);
                    return isPrime;
                }
            };
            Boolean isPrime = (Boolean) t.timeFunction();
            System.out.println("---------------------------------");
            System.out.println(n);
            System.out.println(String.format("Number of threads: %d", i));
            System.out.println(String.format("Execution time: %d milliseconds", t.getLatestTime(TimeUnit.MILLISECONDS)));
            System.out.println(String.format("Value checked: %s", t.n.toString()));
            String primeString = isPrime ? "Is probably prime." : "Is not prime.";
            System.out.println(primeString);
            System.out.println("---------------------------------");
            
        }
    }

    public static void main(String[] arg) throws InterruptedException, ExecutionException
    {
        SolovayStrassenTest s = new SolovayStrassenTest();
        RangeTester t = new RangeTester();
        List<BigInteger> results = t.testRange(new BigInteger("127"), 1, 1, s);
        for (BigInteger b : results)
        {
            System.out.println(b);
        }
    }
}
