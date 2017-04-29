package Solver;

import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.*;
import java.util.Comparator;

/**
 * Created by yxiaocheng1997 on 4/20/17.
 */
public class SimAnSolverHeuristic {

    //private final double confidence = 0.8;
    //private final double blockingPerc = 0.9;

    private boolean bucketStarted;
    private int bucketbit;
    private int bucketTicker;
    private int bucketsize;

    /** how many item has been put into buckets. */

    private boolean bumpingItem;
    private long bucketedItem;
    private final long TIMEOUTSEC = 900;
    private int alarmLmt;
    private final double tempChange = 0.995;
    private final double RepeatTempChange = 1;
    private int MAXITER;
    private int TICKER;
    private final int MAXOUTER = 200;
    private double percentKicked = 0.2;
    private final double percentKickedCls = 0.1;




    /*
    private final long TIMEOUTSEC = 30;
    private final int alarmLmt = 80;
    private final double tempChange = 0.95;
    private final int MAXITER = 400;
    private final int MAXOUTER = 600;
    private final double percentKicked = 0.3;
    private double initTemp;
    */


    private class SolInstance {
        public SolInstance() {
            tVal = M;
            p_r = P;
            m_r = M;
            choosenArr = new boolean[N];
            containedArr = new int[N];
            blockedClsArr = new int[N];
            classTrack = new int[N];
            numContained = 0;
        }

        public SolInstance(SolInstance SolI) {
            tVal = SolI.tVal;
            p_r = P;
            m_r = M;
            choosenArr = new boolean[N];
            System.arraycopy(SolI.choosenArr, 0, choosenArr, 0, N);
            containedArr = new int[N];

            int n_c = 0;
            for (int i = 0; i < N; i += 1) {
                if (SolI.choosenArr[i]) {
                    containedArr[n_c] = i;
                    n_c += 1;
                    p_r -= WeightArr[i];
                    m_r -= CostArr[i];
                }
            }

            numContained = SolI.numContained;

            blockedClsArr = new int[N];
            System.arraycopy(SolI.blockedClsArr, 0, blockedClsArr, 0, N);

            classTrack = new int[N];
            System.arraycopy(SolI.classTrack, 0, classTrack, 0, N);

        }

        public long p_r;
        public long m_r;
        private long tVal;
        public int numContained;
        public boolean[] choosenArr;
        public int[] blockedClsArr;
        public int[] containedArr;
        public int[] classTrack;

    }

    public SimAnSolverHeuristic(long p, long m, int n,
                                int[] ClsArr, long[]WArr, long[]CArr,
                                long[]RArr, HashSet<Integer>[] classIncArr, String fn) {
        System.out.println("Using Heuristic");


        alarmLmt = n*2;
        MAXITER = n*100;
        TICKER = n/2;

        P = p;
        M = m;
        N = n;
        ClassArr = ClsArr;
        WeightArr = WArr;
        CostArr = CArr;
        RevArr = RArr;
        ClassIncArr = classIncArr;
        F = fn;

        //bucketStarted = false;
        
        Solve();
        verifyResult();

    }


    public void Solve() {
        System.out.println("Entering Solve....");
        overallTemp = 1;
        BestSol = CreateInitialSolution();
        long Start = System.currentTimeMillis();
        long End1 = Start + 1000 * TIMEOUTSEC;
        long End2 = End1 + 1000 * TIMEOUTSEC;
        
        bumpingItem = false;
        int tick = 0;
        
        for (int out = 0; out < MAXOUTER; out += 1) {
            if (System.currentTimeMillis() > End1 || overallTemp < 0.5) {
                break;
            } else if (out == MAXOUTER/10) {
                System.out.println("10% done");
            } else if (out == MAXOUTER/4) {
                System.out.println("25% done.");
            } else if (out == MAXOUTER/2) {
                System.out.println("50% done.");
            } else if (out == 3*MAXOUTER/4) {
                System.out.println("75% done.");
            }


            SolInstance CurSol = CreateGreedyInitial();
            //SolInstance CurBestSol = CurSol;


/*            if (bucketStarted) {
                int bucketIdx = (int) (CurSol.tVal >>> bucketbit);
                edBuckets[bucketIdx] += 1;
                bucketedItem += 1;
                if (edBuckets[bucketIdx]> (2* bucketedItem/ (long)bucketsize)){
                    System.out.println("Discarting " + CurSol.tVal + ".");
                    continue;
                }

            }*/


            double temp = 1;
            for (int i = 0; i < MAXITER; i += 1) {
                if (temp < 0.5) {
                    break;
                }
                
                SolInstance Sol_i = CreateNeighborSolution(CurSol);
                long val_i = Sol_i.tVal;
                long val_cur = CurSol.tVal;
                if (val_i > val_cur) {
                    //System.out.println("Local improvement from " + val_cur + " to " + val_i);
                    CurSol = Sol_i;
                    //System.out.println(F + " Semi-global improvement: " + Sol_i.tVal);
                    if (val_i > BestSol.tVal) {
                        temp = 1;
                        overallTemp = 1;
                        BestSol = Sol_i;
                        System.out.println(F + " Actual global improvement: " + CurSol.tVal);
                        if (tick > 1) {
                            if (MAXITER <= 20000000) {
                                MAXITER *= 2;
                            }
                        }
                    }
                    Sol_i = null;

/*                    if (val_i > CurSol.tVal) {
                        CurSol = Sol_i;
                        System.out.println(F + " Semi-global improvement: " + Sol_i.tVal);
                        if (val_i > BestSol.tVal) {
                            BestSol = Sol_i;
                            System.out.println(F + " Actual global improvement: " + CurSol.tVal);
                        }
                        bucketTicker += 1;
                        if (bucketTicker == 4) {
                            System.out.println("Ticking bucket with value " + Sol_i.tVal);
                            initBuckets(Sol_i.tVal);
                        }

                    }*/

                } else {
                    temp = temp*tempChange;
                }
                /*else if (temp > Math.random()) {
                    CurSol = Sol_i;
                    Sol_i = null;
                    //System.out.println("Random re-shuffle" + val_cur + " to " + val_i);
                }*/



                //System.out.println("Finish an OuterLoop with" + CurSol.tVal);

            }
/*            if (CurBestSol.tVal > BestSol.tVal) {
                BestSol = CurBestSol;
                System.out.println(F + " Actual global improvement: " + CurSol.tVal);
                      bucketTicker += 1;
                        if (bucketTicker == 4) {
                            System.out.println("Ticking bucket with value " + Sol_i.tVal);
                            initBuckets(Sol_i.tVal);
                        }

            }*/
            if (CurSol.tVal == BestSol.tVal) {
                if (percentKicked < 0.1 && alarmLmt < 2000000) {
                    percentKicked *= 2;
                    alarmLmt*=2;
                }
                overallTemp *= RepeatTempChange;
                System.out.println("-------------------------------!");
            } else if (CurSol.tVal > BestSol.tVal) {
                System.out.println(F + " Simply with Greedy, global improvement: " + CurSol.tVal);
                BestSol = CurSol;
                tick += 1;
                if (tick > 2 && percentKicked < 0.20) {
                    percentKicked *= 1.5;
                }
            }
            CurSol = null;
        }
        
        System.out.println("Entering Bump Item. ");
        
        bumpingItem = true;
        
        for (int out = 0; out < MAXOUTER; out += 1) {
            if (System.currentTimeMillis() > End2 || overallTemp < 0.5) {
                break;
            } else if (out == MAXOUTER/10) {
                System.out.println("10% done");
            } else if (out == MAXOUTER/4) {
                System.out.println("25% done.");
            } else if (out == MAXOUTER/2) {
                System.out.println("50% done.");
            } else if (out == 3*MAXOUTER/4) {
                System.out.println("75% done.");
            }
            
            
            SolInstance CurSol = CreateGreedyInitial();
            //SolInstance CurBestSol = CurSol;
            
            
            /*            if (bucketStarted) {
             int bucketIdx = (int) (CurSol.tVal >>> bucketbit);
             edBuckets[bucketIdx] += 1;
             bucketedItem += 1;
             if (edBuckets[bucketIdx]> (2* bucketedItem/ (long)bucketsize)){
             System.out.println("Discarting " + CurSol.tVal + ".");
             continue;
             }
             
             }*/
            
            
            double temp = 1;
            for (int i = 0; i < MAXITER; i += 1) {
                if (temp < 0.5) {
                    break;
                }
                
                SolInstance Sol_i = CreateNeighborSolution(CurSol);
                long val_i = Sol_i.tVal;
                long val_cur = CurSol.tVal;
                if (val_i > val_cur) {
                    //System.out.println("Local improvement from " + val_cur + " to " + val_i);
                    CurSol = Sol_i;
                    //System.out.println(F + " Semi-global improvement: " + Sol_i.tVal);
                    if (val_i > BestSol.tVal) {
                        temp = 1;
                        overallTemp = 1;
                        BestSol = Sol_i;
                        System.out.println(F + " Actual global improvement: " + CurSol.tVal);
                    }
                    Sol_i = null;
                    
                    /*                    if (val_i > CurSol.tVal) {
                     CurSol = Sol_i;
                     System.out.println(F + " Semi-global improvement: " + Sol_i.tVal);
                     if (val_i > BestSol.tVal) {
                     BestSol = Sol_i;
                     System.out.println(F + " Actual global improvement: " + CurSol.tVal);
                     }
                     bucketTicker += 1;
                     if (bucketTicker == 4) {
                     System.out.println("Ticking bucket with value " + Sol_i.tVal);
                     initBuckets(Sol_i.tVal);
                     }
                     
                     }*/
                    
                } else {
                    temp = temp*tempChange;
                }
                /*else if (temp > Math.random()) {
                 CurSol = Sol_i;
                 Sol_i = null;
                 //System.out.println("Random re-shuffle" + val_cur + " to " + val_i);
                 }*/
                
                
                
                //System.out.println("Finish an OuterLoop with" + CurSol.tVal);
                
            }
            /*            if (CurBestSol.tVal > BestSol.tVal) {
             BestSol = CurBestSol;
             System.out.println(F + " Actual global improvement: " + CurSol.tVal);
             bucketTicker += 1;
             if (bucketTicker == 4) {
             System.out.println("Ticking bucket with value " + Sol_i.tVal);
             initBuckets(Sol_i.tVal);
             }
             
             }*/
            if (CurSol.tVal == BestSol.tVal) {
                overallTemp *= RepeatTempChange;
                System.out.println("-------------------------------!");
            } else if (CurSol.tVal > BestSol.tVal) {
                System.out.println(F + "Simply with Greedy, global improvement: " + CurSol.tVal);
                BestSol = CurSol;
            }
            CurSol = null;
        }
        
        
        System.out.println(BestSol.tVal);
    }






/*    private void initBuckets(long bucketRef) {
        bucketedItem = 0;

        bucketbit = 0;
        while(bucketRef > (long)16283) {
            bucketbit += 1;
            bucketRef = bucketRef >>> 1;
        }
        bucketsize = (int) (bucketRef >>> bucketbit) << 2;
        System.out.println("The bucketsize is destined to be: " + bucketsize);
        edBuckets = new long[bucketsize];

        bucketStarted = true;
    }*/


    private void addItem(SolInstance S_in) {

        rd = new Random(System.currentTimeMillis());
        // Alarm to prevent overflow;
        int alarm1 = 0;
        //int blockBound = (int) blockingPerc * ClsN;

        int runningIdx = 0;
        int[] candidateArr = new int[N];
        for (int i = 0; i < N; i += 1) {
            if (S_in.blockedClsArr[ClassArr[i]] == 0 && !S_in.choosenArr[i] &&
                WeightArr[i] < S_in.p_r &&  CostArr[i] < S_in.m_r) {
                candidateArr[runningIdx] = i;
                runningIdx += 1;
            }
        }
        if (runningIdx == 0) {
            return;
        }
        
    OuterLoop:
        while (alarm1 < alarmLmt) {
            int alarm2 = 0;
            int eleAdd = candidateArr[rd.nextInt(runningIdx)];
            while (S_in.blockedClsArr[ClassArr[eleAdd]] > 0 || WeightArr[eleAdd] > S_in.p_r
                   || CostArr[eleAdd] > S_in.m_r || S_in.choosenArr[eleAdd]) {
                if (alarm2 > alarmLmt) {
                    break OuterLoop;
                }
                eleAdd = rd.nextInt(N);
                alarm2 += 1;
            }
            
            S_in.choosenArr[eleAdd] = true;
            S_in.numContained += 1;
            
            S_in.p_r -= WeightArr[eleAdd];
            
            S_in.m_r -= CostArr[eleAdd];
            S_in.tVal += RevArr[eleAdd];
            
            int clsAdd = ClassArr[eleAdd];
            S_in.classTrack[clsAdd] += 1;
            
            for (int e_block : ClassIncArr[clsAdd]) {
                S_in.blockedClsArr[e_block] += 1;
            }
            if (S_in.p_r < 0 ||S_in.m_r < 0) {
                System.out.println("When adding item " + eleAdd + " something teeeerible happended.");
            }
        }


    }



    private SolInstance CreateInitialSolution() {
        SolInstance initSolution;
        initSolution = new SolInstance();
        addItem(initSolution);
        return initSolution;
    }
    
    
    private SolInstance CreateGreedyInitial() {
        PriorityQueue<Integer> RevRatPQ = FillRevRatioPQ();
        SolInstance initSolution;
        initSolution = new SolInstance();
        
        while(!RevRatPQ.isEmpty()) {
            int eleAdd = RevRatPQ.remove();
            if (initSolution.blockedClsArr[ClassArr[eleAdd]] == 0 &&
                WeightArr[eleAdd] < initSolution.p_r &&  CostArr[eleAdd] < initSolution.m_r) {
                initSolution.choosenArr[eleAdd] = true;
                initSolution.numContained += 1;
                
                initSolution.p_r -= WeightArr[eleAdd];
                initSolution.m_r -= CostArr[eleAdd];
                initSolution.tVal += RevArr[eleAdd];
                
                int clsAdd = ClassArr[eleAdd];
                initSolution.classTrack[clsAdd] += 1;
                
                for (int e_block : ClassIncArr[clsAdd]) {
                    initSolution.blockedClsArr[e_block] += 1;
                }
            }
        }
        return initSolution;
    }


    private SolInstance CreateNeighborSolution(SolInstance S_cur) {
        rd = new Random(System.currentTimeMillis());

        SolInstance S_new = new SolInstance(S_cur);

        bumpItem(S_new);


        addItem(S_new);

        return S_new;
    }


    /** Bump S_new by item. */
    private void bumpItem(SolInstance S_new) {
        int[] contArr = S_new.containedArr;

        int n_c = S_new.numContained;
        if (n_c > 0) {
            int bumping = Math.max(1, (int)(percentKicked* (double) n_c));

            while(bumping > 0){
                int bump = (int)(Math.random() * n_c);
                int bumpIdx = contArr[bump];
                //int bump = rd.nextInt(n_c);
                if (bumpIdx != -1) {
                    S_new.choosenArr[bumpIdx] = false;

                    S_new.p_r += WeightArr[bumpIdx];
                    if (S_new.p_r > P) {
                        System.out.println("Exceed maximum P when bumping item with weight" + WeightArr[bumpIdx]);
                    }
                    S_new.m_r += CostArr[bumpIdx];
                    S_new.tVal -= RevArr[bumpIdx];

                    int clsBump = ClassArr[bumpIdx];
                    S_new.classTrack[clsBump] -= 1;
                    if (S_new.classTrack[clsBump] == 0) {
                        kickRestriction(clsBump, S_new.blockedClsArr);
                    }
                    bumping -= 1;
                    contArr[bump] = -1;
                }
            }
        }
        
        S_new.containedArr = new int[N];
        
        int n_k = 0;
        for (int i = 0; i < N; i += 1) {
            if (S_new.choosenArr[i]) {
                S_new.containedArr[n_k] = i;
                n_k += 1;
            }
        }
        S_new.numContained = n_k;
        
        
    }

    /** Bump S_new by class. */
    private void bumpClass(SolInstance S_new) {
        boolean[] result = S_new.choosenArr;
        //int[] classChosen = new int[N];
        boolean[] classCn = new boolean[N];
        ArrayList<Integer> clsExistList = new ArrayList<>();

        int[] contArr = S_new.containedArr;

        for (int i = 0; i < contArr.length; i += 1) {
            int cls_i = ClassArr[contArr[i]];
            //classChosen[n_i] = cls_i;
            if (!classCn[cls_i]) {
                classCn[cls_i] = true;
                clsExistList.add(cls_i);
            }
        }

        int numCls = clsExistList.size();

        if (numCls > 0) {
            int bumpingCls = Math.max(1, (int)(percentKickedCls* (double) numCls));

            while(bumpingCls > 0){
                int bumpCIndex = (int)(Math.random() * numCls);
                int bumpC = clsExistList.get(bumpCIndex);
                if (bumpC != -1 && classCn[bumpC]) {
                    for (int i = 0; i < contArr.length; i += 1) {
                        if (ClassArr[i] == bumpC && S_new.containedArr[i] != -1) {
                            S_new.containedArr[i] = -1;

                            S_new.p_r += WeightArr[i];
                            S_new.m_r += CostArr[i];
                            S_new.tVal -= RevArr[i];
                        }
                    }

                    S_new.classTrack[bumpC] = 0;
                    kickRestriction(bumpC, S_new.blockedClsArr);

                    classCn[bumpC] = false;

                    bumpingCls -= 1;
                    clsExistList.set(bumpCIndex, -1);
                }

            }
        }
        S_new.containedArr = new int[N];
        
        int n_c = 0;
        for (int i = 0; i < N; i += 1) {
            if (S_new.choosenArr[i]) {
                S_new.containedArr[n_c] = i;
                n_c += 1;
            }
        }
        S_new.numContained = n_c;

    }


    private void kickRestriction(int clsKicked, int[] blockedClsArr) {
        for (int e_release : ClassIncArr[clsKicked]) {
            blockedClsArr[e_release] -= 1;
        }
    }



    /** Return the Optimal Solution, marked by TRUE if the item is in. */
    public boolean[] getOptSolution() {
        return BestSol.choosenArr;
    }

    public long getOptVal() {return BestSol.tVal;}


    /** Comparator for revenue/cost ratio. */
    class RevComparator implements Comparator<Integer> {
        // Return 1 if obj1 is more profitable than obj2
        RevComparator(int c, int w) {
            costCoef = c;
            weightCoef = w;
        }
        
        @Override
        public int compare(Integer obj1, Integer obj2) {
            double revRatio1 = (RevArr[obj1] / (costCoef * (double) CostArr[obj1] + weightCoef * (double) WeightArr[obj1] + 1));
            double revRatio2 = (RevArr[obj2] / (costCoef * (double) CostArr[obj2] + weightCoef * (double) WeightArr[obj2] + 1));
            if (revRatio1 > revRatio2) {
                return -1;
            } else {
                return 1;
            }
        }
        
        int costCoef;
        int weightCoef;
    }

    /** Fill the Rev Ratio PQ for initial processing,
     * which will gurantee the most profitable items are at least
     * considered once.
     */
    private PriorityQueue<Integer> FillRevRatioPQ() {
        //int costC = (int) (Math.random() * (40 + 1));
        int costC = 40;
        int weightC = 40 - costC;
        System.out.println("Using CostC: " + costC);
        
        
        PriorityQueue<Integer> RevRatioPQ = new PriorityQueue<>(N, new RevComparator(costC, weightC));
        for (int i = 0; i < N; i+= 1) {
            if (RevArr[i] != 0) {
                RevRatioPQ.add(i);
            }
        }
        return RevRatioPQ;
        /*
        while (!RevRatioPQ.isEmpty()) {
            Integer ej = RevRatioPQ.poll();
            long c = CostArr[ej];
            long r = RevArr[ej];
            double rat = r/((double) c + 0.01);
            //System.out.println("Ejecting item" + ej + " with revenue ratio" + rat );
        }*/
    }
    



    
    
    
    
    private void verifyResult() {
        /*Weight part. */
        long ActualPL = 0;
        for (int i = 0; i < N; i += 1) {
            if (BestSol.choosenArr[i]) {
                ActualPL += WeightArr[i];
                
            }
        }
        
        System.out.println("The Actual Weight is: " + ActualPL);
        if (ActualPL > P) {
            System.out.println("Uh oh. Exceeded Weight limit: " + P);
        }
        
        
        
        
        /*Cost part. */
        long ActualML = 0;
        for (int i = 0; i < N; i += 1) {
            if (BestSol.choosenArr[i]) {
                ActualML += CostArr[i];
            }
        }
        
        System.out.println("The Actual Cost is: " + ActualML);
        if (ActualML > M) {
            System.out.println("Uh oh. Exceeded Cost limit: " + M);
        }
        
        
        
        System.out.println("YOOOOOO.");
        
        
    }





    private long P;

    private long M;

    private int N;



    /** Mapping from item index to its class. */
    private int[] ClassArr;

    /** Mapping from item index to its weight. */
    private long[] WeightArr;


    /** Mapping from item index to its weight. */
    private long[] CostArr;


    /** Mapping from item index to its weight. */
    private long[] RevArr;


    /** PriorityQueue of Profits. */
    //PriorityQueue<Integer> RevRatioPQ;


    /** The Best Choosen Solution Instance. */
    private SolInstance BestSol;


    /** Mapping from class index to HSet of class index that is incompatible. */
    private HashSet<Integer>[] ClassIncArr;

    /** The random number generator of the random functions. */
    private Random rd;


    /** The Temperature. */
    private double overallTemp;

    /** Input File Name. */
    private String F;

    /** Educated buckets. */
    private long[] edBuckets;


}
