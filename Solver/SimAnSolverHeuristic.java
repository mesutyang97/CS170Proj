package Solver;

import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.*;
import java.util.Comparator;
import java.util.ArrayDeque;

/**
 * Created by yxiaocheng1997 on 4/20/17.
 */
public class SimAnSolverHeuristic {

    private final long TIMEOUTSEC = 100;
    private final int pres = 100;
    private int lastPres;
    private int alarmLmt;
    private final double tempChange = 0.997;
    private final double RepeatTempChange = 0.95;
    private int MAXITER;
    private int TICKER;
    private final int MAXOUTER = 1000;
    private double percentKicked = 0.20;
    private double percentKickedCls = 0.15;



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
            costCo = -1;
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
            costCo = SolI.costCo;
        }
        
        public long p_r;
        public long m_r;
        private long tVal;
        public int numContained;
        public boolean[] choosenArr;
        public int[] blockedClsArr;
        public int[] containedArr;
        public int[] classTrack;
        public double costCo;
        
    }

    public SimAnSolverHeuristic(long p, long m, int n,
                                int[] ClsArr, long[]WArr, long[]CArr,
                                long[]RArr, HashSet<Integer>[] classIncArr, String fn) {
        System.out.println("Using Heuristic");

        goodRatArr = new double[4000];
        numGoodRat = 0;
        
        s = new ArrayDeque();
        double candid = 0;
        for (int i = 0; i < (pres/2 -1) ; i += 1) {
            s.add(1 - candid);
            s.add(candid);
            candid += (1.0/pres);
        }
        s.add(0.5);
        
        
        alarmLmt = n*10;
        //alarmLmt = n/1000;
        MAXITER = n*1;
        //MAXITER = 5;
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
        int tick = 0;
        
        for (int out = 0; out < MAXOUTER; out += 1) {
            if (System.currentTimeMillis() > End1 || overallTemp < 0.5) {
                break;
            }


            SolInstance CurSol = CreateGreedyInitial();


            double temp = 1;
            for (int i = 0; i < MAXITER; i += 1) {
                if (temp < 0.5) {
                    break;
                }
                
                SolInstance Sol_i = CreateNeighborSolution(CurSol);
                long val_i = Sol_i.tVal;
                long val_cur = CurSol.tVal;
                if (val_i > val_cur) {
                    CurSol = Sol_i;
                    if (val_i > BestSol.tVal) {
                        temp = 1;
                        overallTemp = 1;
                        BestSol = Sol_i;
                        System.out.println(F + " Actual global improvement: " + CurSol.tVal);
                        if (tick > 1) {
                            if (MAXITER <= 80000000) {
                                MAXITER *= 2;
                            }
                        }
                    }
                    Sol_i = null;

                }
                //else {
                //    temp = temp*tempChange;
                //}

            }

            if (CurSol.tVal == BestSol.tVal) {
                if (alarmLmt < 2000000) {
                    alarmLmt*=2;
                }
                overallTemp *= RepeatTempChange;
                goodRatArr[numGoodRat] = CurSol.costCo;
                numGoodRat += 1;
                System.out.println("-------------------------------!");
            } else if (CurSol.tVal > BestSol.tVal) {
                System.out.println(F + " Simply with Greedy, global improvement: " + CurSol.tVal);
                System.out.println("Using CostC: " + CurSol.costCo);
                BestSol = CurSol;
                tick += 1;
                if (tick > 5 && percentKickedCls < 0.30) {
                    percentKickedCls *= 1.5;
                }
                goodRatArr[numGoodRat] = CurSol.costCo;
                numGoodRat += 1;
            }
            CurSol = null;
        }
        
        System.out.println("Entering Bump Item. ");

        
        for (int out = 0; out < MAXOUTER; out += 1) {
            if (System.currentTimeMillis() > End2 || overallTemp < 0.5) {
                break;
            }
            
            
            SolInstance CurSol = CreateGreedyInitial();
            
            
            double temp = 1;
            for (int i = 0; i < MAXITER; i += 1) {
                if (temp < 0.5) {
                    break;
                }
                
                SolInstance Sol_i = CreateNeighborSolution(CurSol);
                long val_i = Sol_i.tVal;
                long val_cur = CurSol.tVal;
                if (val_i > val_cur) {
                    CurSol = Sol_i;
                    if (val_i > BestSol.tVal) {
                        temp = 1;
                        overallTemp = 1;
                        BestSol = Sol_i;
                        System.out.println(F + " Actual global improvement: " + CurSol.tVal);
                    }
                    Sol_i = null;
                    
                }
                //else {
                //    temp = temp*tempChange;
                //}
            }
            if (CurSol.tVal == BestSol.tVal) {
                overallTemp *= RepeatTempChange;
                goodRatArr[numGoodRat] = CurSol.costCo;
                numGoodRat += 1;
                System.out.println("-------------------------------!");
            } else if (CurSol.tVal > BestSol.tVal) {
                System.out.println(F + "Simply with Greedy, global improvement: " + CurSol.tVal);
                BestSol = CurSol;
                goodRatArr[numGoodRat] = CurSol.costCo;
                numGoodRat += 1;
            }
            CurSol = null;
        }
        System.out.println(BestSol.tVal);
    }


    private void addItem(SolInstance S_in) {

        rd = new Random(System.currentTimeMillis());
        // Alarm to prevent overflow;
        int alarm1 = 0;
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
            int candIdx = rd.nextInt(runningIdx);
            int eleAdd = candidateArr[candIdx];
            while (eleAdd == -1 || S_in.blockedClsArr[ClassArr[eleAdd]] > 0 || WeightArr[eleAdd] > S_in.p_r
                   || CostArr[eleAdd] > S_in.m_r || S_in.choosenArr[eleAdd]) {
                if (alarm2 > alarmLmt) {
                    break OuterLoop;
                }
                candIdx = rd.nextInt(runningIdx);
                eleAdd = candidateArr[candIdx];
                alarm2 += 1;
            }
            
            S_in.choosenArr[eleAdd] = true;
            S_in.numContained += 1;
            
            S_in.p_r -= WeightArr[eleAdd];
            
            S_in.m_r -= CostArr[eleAdd];
            S_in.tVal += RevArr[eleAdd];
            
            int clsAdd = ClassArr[eleAdd];
            S_in.classTrack[clsAdd] += 1;
            
            if (S_in.classTrack[clsAdd] == 1) {
                for (int e_block : ClassIncArr[clsAdd]) {
                    S_in.blockedClsArr[e_block] += 1;
                }
            }
            candidateArr[candIdx] = -1;
        }
        
        S_in.containedArr = new int[N];
        
        int n_k = 0;
        for (int i = 0; i < N; i += 1) {
            if (S_in.choosenArr[i]) {
                S_in.containedArr[n_k] = i;
                n_k += 1;
            }
        }
        S_in.numContained = n_k;

    }



    private SolInstance CreateInitialSolution() {
        SolInstance initSolution;
        initSolution = new SolInstance();
        addItem(initSolution);
        return initSolution;
    }
    
    
    private SolInstance CreateGreedyInitial() {
        double costC = 0;
        if (!s.isEmpty()) {
            costC = s.poll();
        } else {
            System.out.println("Depleted s");
            if (Math.random() < 0.9 && numGoodRat > 0) {
                costC = goodRatArr[numGoodRat - 1];
            } else {
                costC = Math.random();
            }
        }
        
        
        
        PriorityQueue<Integer> RevRatPQ = FillRevRatioPQ(costC);
        SolInstance initSolution;
        initSolution = new SolInstance();
        initSolution.costCo = costC;
        
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
                
                if (initSolution.classTrack[clsAdd] == 1) {
                    for (int e_block : ClassIncArr[clsAdd]) {
                        initSolution.blockedClsArr[e_block] += 1;
                    }
                }
            }
        }
        
        
        initSolution.containedArr = new int[N];
        
        int n_k = 0;
        for (int i = 0; i < N; i += 1) {
            if (initSolution.choosenArr[i]) {
                initSolution.containedArr[n_k] = i;
                n_k += 1;
            }
        }
        initSolution.numContained = n_k;
        
        return initSolution;
    }


    private SolInstance CreateNeighborSolution(SolInstance S_cur) {
        rd = new Random(System.currentTimeMillis());

        SolInstance S_new = new SolInstance(S_cur);

        if (Math.random() < 0.5) {
            bumpItem(S_new);
        } else {
            bumpClass(S_new);
        }
        
        addItem(S_new);

        return S_new;
    }


    /** Bump S_new by item. */
    private void bumpItem(SolInstance S_new) {
        int[] contArr = S_new.containedArr;

        if (s.isEmpty()) {
            percentKicked = Math.random() * 0.3;
        }
        
        int n_c = S_new.numContained;
        if (n_c > 0) {
            int bumping = Math.max(1, (int)(percentKicked* (double) n_c));

            while(bumping > 0){
                int bump = (int)(Math.random() * n_c);
                int bumpIdx = contArr[bump];
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
    
    
    
    
    private void checkTrack(SolInstance S) {
        for (int i = 0; i < N; i += 1) {
            if (S.choosenArr[i]) {
                int cls_i = ClassArr[i];
                if (S.classTrack[cls_i] == 0) {
                    System.out.println("Item " + i + "s class went untracked.");
                    System.exit(1);
                }
                
            }
        }
    }

    /** Bump S_new by class. */
    private void bumpClass(SolInstance S_new) {
        boolean[] classCn = new boolean[N];
        ArrayList<Integer> clsExistList = new ArrayList<>();

        int[] contArr = S_new.containedArr;

        for (int i = 0; i < N; i += 1) {
            if (S_new.choosenArr[i]) {
                int cls_i = ClassArr[i];
                if (!classCn[cls_i]) {
                    classCn[cls_i] = true;
                    clsExistList.add(cls_i);
                }
            }
        }

        int numCls = clsExistList.size();

        
        if (s.isEmpty()) {
            percentKickedCls = Math.random() * 0.3;
        }
        
        
        
        if (numCls > 0) {
            int bumpingCls = Math.max(1, (int)(percentKickedCls* (double) numCls));

            while(bumpingCls > 0){
                int bumpCIndex = (int)(Math.random() * numCls);
                int bumpC = clsExistList.get(bumpCIndex);
                if (bumpC != -1) {
                    
                    for (int i = 0; i < S_new.numContained; i += 1) {
                        int itemIdx = contArr[i];
                        if (itemIdx != -1 && ClassArr[itemIdx] == bumpC) {
                            S_new.containedArr[i] = -1;
                            S_new.p_r += WeightArr[itemIdx];
                            S_new.m_r += CostArr[itemIdx];
                            S_new.tVal -= RevArr[itemIdx];
                            S_new.choosenArr[itemIdx] = false;
                        }
                    }
                    S_new.classTrack[bumpC] = 0;
                    kickRestriction(bumpC, S_new.blockedClsArr);
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
            if(blockedClsArr[e_release] >0) {
                blockedClsArr[e_release] -=1;
            }
        }
    }



    /** Return the Optimal Solution, marked by TRUE if the item is in. */
    public boolean[] getOptSolution() {
        return BestSol.choosenArr;
    }

    public long getOptVal() {return BestSol.tVal;}


    /** Comparator for revenue/cost ratio. */
    class RevComparator implements Comparator<Integer> {
        // Return -1 if obj1 is more profitable than obj2
        RevComparator(double c, double w) {
            costCoef = c;
            weightCoef = w;
        }
        
        @Override
        public int compare(Integer obj1, Integer obj2) {
            //double revRatio1 = (RevArr[obj1] / (costCoef * (double) CostArr[obj1] + weightCoef * (double) WeightArr[obj1] + 1));
            //double revRatio2 = (RevArr[obj2] / (costCoef * (double) CostArr[obj2] + weightCoef * (double) WeightArr[obj2] + 1));
            double revRatio1 = RevArr[obj1];
            double revRatio2 = RevArr[obj2];
            if (revRatio1 > revRatio2) {
                return -1;
            } else {
                return 1;
            }
        }
        
        double costCoef;
        double weightCoef;
    }

    /** Fill the Rev Ratio PQ for initial processing,
     * which will gurantee the most profitable items are at least
     * considered once.
     */
    private PriorityQueue<Integer> FillRevRatioPQ(double costC) {
        double weightC = 1 - costC;

        //System.out.println("Using CostC: " + costC);
        PriorityQueue<Integer> RevRatioPQ = new PriorityQueue<>(N, new RevComparator(costC, weightC));
        for (int i = 0; i < N; i+= 1) {
            if (RevArr[i] != 0) {
                RevRatioPQ.add(i);
            }
        }
        return RevRatioPQ;
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

    
    ArrayDeque<Double> s;
    
    /** Array that keep track of all good ratios that either produce a maximum or keep one. */
    private double[] goodRatArr;
    
    /** Number of good costCoef recorded. */
    private int numGoodRat;

    /** The Temperature. */
    private double overallTemp;

    /** Input File Name. */
    private String F;


}
