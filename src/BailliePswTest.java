

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Some of this code has been based off of the python code provided by user
 * Primo on the codegolf stackexchange site. Link:
 * http://codegolf.stackexchange.com/a/10702/17291
 * 
 * @author Ryan Harrod
 *
 */
public class BailliePswTest implements IPrimalityTest
{
    private final BigInteger ZERO = BigInteger.ZERO;
    private final BigInteger ONE = BigInteger.ONE;
    private final BigInteger TWO = ONE.add(ONE);

    // private volatile boolean isPrimeFlag = false; // If ever set to true, the
    // number is definitely not prime.

    private class SmallDivisorWorker implements Callable<Boolean>
    {
        private BigInteger n;
        private BigInteger p;

        public SmallDivisorWorker(BigInteger m, BigInteger i)
        {
            n = m;
            p = i;
        }

        @Override
        public Boolean call() throws Exception
        {
            return n.mod(p) == ZERO;
        }

    }
    
    private class StrongProbablePrimeWorker implements Callable<Boolean>
    {
        private BigInteger n;
        
        public StrongProbablePrimeWorker(BigInteger m)
        {
            n = m;
        }
        
        @Override
        public Boolean call() throws Exception
        {
            return isStrongProbablePrime(n);
        }
    }
    
    @Override
    public boolean IsPrime(BigInteger n, int numThreads)
    {
        if (n.equals(ONE) || n.equals(ZERO))
        {
            return false;
        } else if (n.equals(TWO))
        {
            return true;
        }else if (n.mod(TWO).equals(ZERO))
        {
            return false;
        }

        ExecutorService ex = Executors.newFixedThreadPool(numThreads);
        List<Future<Boolean>> smallChecks = new ArrayList<Future<Boolean>>();
        
        BigInteger[] smallPrimes =
        { new BigInteger("2"),
                new BigInteger("3"),
                new BigInteger("5"),
                new BigInteger("7"),
                new BigInteger("11"),
                new BigInteger("13"),
                new BigInteger("17"),
                new BigInteger("19"),
                new BigInteger("23"),
                new BigInteger("29"),
                new BigInteger("31"),
                new BigInteger("37"),
                new BigInteger("41"),
                new BigInteger("43"),
                new BigInteger("47"),
                new BigInteger("53"),
                new BigInteger("59"),
                new BigInteger("61"),
                new BigInteger("67"),
                new BigInteger("71"),
                new BigInteger("73"),
                new BigInteger("79"),
                new BigInteger("83"),
                new BigInteger("89"),
                new BigInteger("97"),
                new BigInteger("101"),
                new BigInteger("103"),
                new BigInteger("107"),
                new BigInteger("109"),
                new BigInteger("113"),
                new BigInteger("127"),
                new BigInteger("131"),
                new BigInteger("137"),
                new BigInteger("139"),
                new BigInteger("149"),
                new BigInteger("151"),
                new BigInteger("157"),
                new BigInteger("163"),
                new BigInteger("167"),
                new BigInteger("173"),
                new BigInteger("179"),
                new BigInteger("181"),
                new BigInteger("191"),
                new BigInteger("193"),
                new BigInteger("197"),
                new BigInteger("199"),
                new BigInteger("211"),
                new BigInteger("223"),
                new BigInteger("227"),
                new BigInteger("229"),
                new BigInteger("233"),
                new BigInteger("239"),
                new BigInteger("241"),
                new BigInteger("251"),
                new BigInteger("257"),
                new BigInteger("263"),
                new BigInteger("269"),
                new BigInteger("271"),
                new BigInteger("277"),
                new BigInteger("281"),
                new BigInteger("283"),
                new BigInteger("293"),
                new BigInteger("307"),
                new BigInteger("311"),
                new BigInteger("313"),
                new BigInteger("317"),
                new BigInteger("331"),
                new BigInteger("337"),
                new BigInteger("347"),
                new BigInteger("349"),
                new BigInteger("353"),
                new BigInteger("359"),
                new BigInteger("367"),
                new BigInteger("373"),
                new BigInteger("379"),
                new BigInteger("383"),
                new BigInteger("389"),
                new BigInteger("397"),
                new BigInteger("401"),
                new BigInteger("409"),
                new BigInteger("419"),
                new BigInteger("421"),
                new BigInteger("431"),
                new BigInteger("433"),
                new BigInteger("439"),
                new BigInteger("443"),
                new BigInteger("449"),
                new BigInteger("457"),
                new BigInteger("461"),
                new BigInteger("463"),
                new BigInteger("467"),
                new BigInteger("479"),
                new BigInteger("487"),
                new BigInteger("491"),
                new BigInteger("499"),
                new BigInteger("503"),
                new BigInteger("509"),
                new BigInteger("521"),
                new BigInteger("523"),
                new BigInteger("541"),
                new BigInteger("547"),
                new BigInteger("557"),
                new BigInteger("563"),
                new BigInteger("569"),
                new BigInteger("571"),
                new BigInteger("577"),
                new BigInteger("587"),
                new BigInteger("593"),
                new BigInteger("599"),
                new BigInteger("601"),
                new BigInteger("607"),
                new BigInteger("613"),
                new BigInteger("617"),
                new BigInteger("619"),
                new BigInteger("631"),
                new BigInteger("641"),
                new BigInteger("643"),
                new BigInteger("647"),
                new BigInteger("653"),
                new BigInteger("659"),
                new BigInteger("661"),
                new BigInteger("673"),
                new BigInteger("677"),
                new BigInteger("683"),
                new BigInteger("691"),
                new BigInteger("701"),
                new BigInteger("709"),
                new BigInteger("719"),
                new BigInteger("727"),
                new BigInteger("733"),
                new BigInteger("739"),
                new BigInteger("743"),
                new BigInteger("751"),
                new BigInteger("757"),
                new BigInteger("761"),
                new BigInteger("769"),
                new BigInteger("773"),
                new BigInteger("787"),
                new BigInteger("797"),
                new BigInteger("809"),
                new BigInteger("811"),
                new BigInteger("821"),
                new BigInteger("823"),
                new BigInteger("827"),
                new BigInteger("829"),
                new BigInteger("839"),
                new BigInteger("853"),
                new BigInteger("857"),
                new BigInteger("859"),
                new BigInteger("863"),
                new BigInteger("877"),
                new BigInteger("881"),
                new BigInteger("883"),
                new BigInteger("887"),
                new BigInteger("907"),
                new BigInteger("911"),
                new BigInteger("919"),
                new BigInteger("929"),
                new BigInteger("937"),
                new BigInteger("941"),
                new BigInteger("947"),
                new BigInteger("953"),
                new BigInteger("967"),
                new BigInteger("971"),
                new BigInteger("977"),
                new BigInteger("983"),
                new BigInteger("991"),
                new BigInteger("997"), };
        
        for (BigInteger i : smallPrimes)
        {
            if (i.compareTo(n) == 0)
            {
                ex.shutdown();
                return true;
            }
            if (i.compareTo(n) < 0)
            {
                smallChecks.add(ex.submit(new SmallDivisorWorker(n, i)));
            }
        }

        boolean smallPrimeCheck = false;
        for (Future<Boolean> b : smallChecks)
        {
            try
            {
                if(b.get()) // some integer divides n evenly
                {
                    smallPrimeCheck = true;
                    break;
                }
                
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            } catch (ExecutionException e)
            {
                e.printStackTrace();
            }
        }
        ex.shutdown();
        try
        {
            ex.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e2)
        {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        if (smallPrimeCheck)
        {
            return false;
        }
        ex = Executors.newFixedThreadPool(1);
        Future<Boolean> isStrongPP = ex.submit(new StrongProbablePrimeWorker(n));
        BigInteger a = new BigInteger("5");
        BigInteger s = TWO;
        BigInteger subOne = n.subtract(ONE);
        while (legendre(a, n).compareTo(subOne) != 0)
        {
            s = s.negate();
            a = s.subtract(a);
        }
        
        boolean isSPP = true;
        try
        {
            isSPP = isStrongPP.get();
            ex.shutdown();
            ex.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
            return false;
        }
        if (!isSPP)
        {
            return false;
        }
        return isLucasProbablePrime(n, a);
    }

    private boolean isLucasProbablePrime(BigInteger n, BigInteger D)
    {
        BigInteger Q = (ONE.subtract(D).shiftRight(2));

        BigInteger s = n.add(ONE);
        BigInteger r = BigInteger.ZERO;

        while (!s.testBit(0))
        {
            r = r.add(ONE);
            s = s.shiftRight(1);
        }

        BigInteger t = ZERO;
        while (s.compareTo(ZERO) > 0)
        {
            if (s.testBit(0))
            {
                t = t.add(ONE);
                s = s.subtract(ONE);
            } else
            {
                t = t.shiftLeft(1);
                s = s.shiftRight(1);
            }
        }
        BigInteger U = ZERO;
        BigInteger V = TWO;
        BigInteger q = ONE;
        BigInteger inv_2 = n.add(ONE).shiftRight(1);

        while (t.compareTo(ZERO) > 0)
        {
            if (t.and(ONE).equals(ONE))
            {
                BigInteger tmpU = U;
                U = U.add(V).multiply(inv_2).mod(n);
                V = D.multiply(tmpU).add(V).multiply(inv_2).mod(n);
                q = q.multiply(Q).mod(n);
                t = t.subtract(ONE);
            } else
            {
                U = U.multiply(V).mod(n);
                BigInteger intermediate = TWO.multiply(q);
                V = V.multiply(V).subtract(intermediate).mod(n);
                q = q.modPow(TWO, n);
                t = t.shiftRight(1);
            }
        }

        while (r.compareTo(ZERO) > 0)
        {
            U = U.multiply(V).mod(n);
            BigInteger intermediate = TWO.multiply(q);
            V = V.multiply(V).subtract(intermediate).mod(n);
            q = q.modPow(TWO, n);
            r = r.subtract(ONE);
        }

        return U.equals(ZERO);
    }

    private boolean isStrongProbablePrime(BigInteger n)
    {
        BigInteger b = TWO;
        BigInteger nMinusOne = n.subtract(ONE);
        BigInteger d = nMinusOne;
        BigInteger s = ZERO;
        while (d.and(ONE).equals(ZERO))
        {
            s = s.add(ONE);
            d = d.shiftRight(1);
        }
        BigInteger x = b.modPow(d, n);
        if (x.equals(ONE) || x.equals(nMinusOne))
        {
            return true;
        }

        for (BigInteger i = ONE; i.compareTo(ZERO) > 0; i = i.add(ONE))
        {
            x = x.modPow(TWO, n);
            if (x.equals(ONE))
            {
                return false;
            } else if (x.equals(nMinusOne))
            {
                return true;
            }
        }

        return false;
    }

    private BigInteger legendre(BigInteger a, BigInteger m)
    {
        return a.modPow(m.subtract(ONE).shiftRight(1), m);
    }

    public static void main(String[] args)
    {
        BigInteger n = new BigInteger("34608828249085121524296039576741331672262866890023854779048928344500622080983411446436437554415370753366448674763505018641470709332373970608376690404229265789647993709760358469552319045484910050304149809818540283507159683562232941968059762281334544739720849260904855192770626054911793590389060795981163838721432994278763633095377438194844866471124967685798888172212033000821469684464956146997194126921284336206463313859537577200462442029064681326087558257488470489384243989270236884978643063093004422939603370010546595386302009073043944482202559097406700597330570799507832963130938739885080198416258635194522913042562936679859587495721031173747796418895060701941717506001937152430032363631934265798516236047451209089864707430780362298307038193445486493756647991804258775574973833903315735082891029392359352758617185019942554834671861074548772439880729606244911940066680112823824095816458261761861746604034802056466823143718255492784779380991749580255263323326536457743894150848953969902818530057870876229329803338285735419228259022169602665532210834789602051686546011466737981306056247480055071718250333737502267307344178512950738594330684340802698228963986562732597175372087295649072830289749771358330867951508710859216743218522918811670637448496498549094430541277444079407989539857469452772132166580885754360477408842913327292948696897496141614919739845432835894324473601387609643750514699215032683744527071718684091832170948369396280061184593746143589068811190253101873595319156107319196071150598488070027088705842749605203063194191166922106176157609367241948160625989032127984748081075324382632093913796444665700601391278360323002267434295194325607280661260119378719405151497555187549252134264394645963853964913309697776533329401822158003182889278072368602128982710306618115118964131893657845400296860012420391376964670183983594954112484565597312460737798777092071706710824503707457220155015899591766244957768006802482976673920392995410164224776445671222149803657927708412925555542817045572430846389988129960519227313987291200902060882060733762075892299473666405897427035811786879875694315078654420055603469625309399653955932310466430039146465805452965014040019423897552675534768248624631951431493188170905972588780111850281190559073677771187432814088678674286302108275149258477101296451833651979717375170900505673645964696355331369819296000267389583289299126738345726980325998955997501176664201042888546085699446442834195232948787488410595750197438786353119204210855804692460582533832967771946911459901921324984968810021189968284941331573164056304725480868921823442538199590383852412786840833479611419970101792978355653650755329138298654246225346827207503606740745956958127383748717825918527473164970582095181312905519242710280573023145554793628499010509296055849712377978984921839997037415897674154830708629145484724536724572622450131479992681684310464449439022250504859250834761894788889552527898400988196200014868575640233136509145628127191354858275083907891469979019426224883789463551");
        
        //BigInteger m = new BigInteger("1551197868099891386459896063244381932060770425565921999885096817830297496627504652115239001983985153119775350914638552307445919773021758654815641382344720913548160379485681746575245251059529720935264144339378936233043585239478807971817857394193701584822359805681429741446927344534491412763713568490429195862973508863067230162660278070962484418979417980291904500349345162151774412157280412235743457342694749679453616265540134456421369622519723266737913");
        //BigInteger n = new BigInteger("6864797660130609714981900799081393217269435300143305409394463459185543183397656052122559640661454554977296311391480858037121987999716643812574028291115057151");
        BailliePswTest bp = new BailliePswTest();
        int numThreads = 1;
        for (int i = 0; i < 100; i++)
        {
            System.out.println(String.format("%d: %s", i, bp.IsPrime(BigInteger.valueOf(i), numThreads)));
        }
        //System.out.println(bp.IsPrime(n, numThreads));
        //System.out.println(bp.IsPrime(m, numThreads));
    }
}
