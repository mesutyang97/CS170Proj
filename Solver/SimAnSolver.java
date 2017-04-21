package Solver;

import com.sun.tools.javah.Util;

import java.util.HashSet;
import java.util.Random;

/**
 * Created by yxiaocheng1997 on 4/20/17.
 */
public class SimAnSolver {

    //private final double confidence = 0.8;
    //private final double blockingPerc = 0.9;
    private final int alarmLmt = 40;
    private final double tempChange = 0.98;
    private final int MAXITER = 2000;
    private final double percentKicked = 0.1;
    private double initTemp;


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
            p_r = SolI.p_r;
            m_r = SolI.m_r;
            choosenArr = new boolean[N];
            System.arraycopy(SolI.choosenArr, 0, choosenArr, 0, N);
            containedArr = new int[N];

            int n_c = 0;
            for (int i = 0; i < N; i += 1) {
                if (SolI.choosenArr[i]) {
                    containedArr[n_c] = i;
                    n_c += 1;
                }
            }

            numContained = SolI.numContained;

            //DELETE ME
            if (numContained != n_c) {
                System.out.println("numContained is bad in the initilizer. " +
                "Old SolI has " + numContained + " but the loop gives " + n_c);
                System.exit(1);
            }

            blockedClsArr = new int[N];
            System.arraycopy(SolI.blockedClsArr, 0, blockedClsArr, 0, N);

            classTrack = new int[N];
            System.arraycopy(SolI.classTrack, 0, classTrack, 0, N);



/*
             boolean[] result = new boolean[N];
             for (int i = 0; i < N; i += 1) {
                if (SolI.choosenArr[i]) {
                    result[i] = true;
                    containedArr[n_c] = i;
                    n_c += 1;
                    classTrack[ClassArr[i]] += 1;
                    for (int e_block : ClassIncArr[i]) {
                        blockedClsArr[e_block] += 1;
                    }
                }
            }*/

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

    public SimAnSolver(long p, long m, int n,
                       int[] ClsArr, long[]WArr, long[]CArr,
                       long[]RArr, HashSet<Integer>[] classIncArr) {

        P = p;
        M = m;
        N = n;
        ClassArr = ClsArr;
        WeightArr = WArr;
        CostArr = CArr;
        RevArr = RArr;
        ClassIncArr = classIncArr;

        initTemp = (double)(p);


        //ClsN = clsN;
        temp = initTemp;
        //BlockedClsSet = new HashSet<>();
        //boundInit();
        Solve();
    }

    public void Solve() {
        System.out.println("Entering Solve....");
        temp = initTemp;
        CurSol = CreateInitialSolution();
        //CurChoosenArr = CurSol.choosenArr;
        BestSol = CurSol;
        //BestChoosenArr = CurChoosenArr;
        for (int i = 0; i < MAXITER; i += 1) {
            SolInstance Sol_i = CreateNeighborSolution(CurSol);

            System.out.println(Sol_i.tVal);

            long val_i = Sol_i.tVal;
            long val_cur = CurSol.tVal;
            if (val_i > val_cur) {
                temp = temp * 0.95;
                CurSol = Sol_i;
                if (val_i > BestSol.tVal) {
                    BestSol = Sol_i;
                    System.out.println("The Best so far is " + Sol_i.tVal);
                }
            } else if (Math.exp((Sol_i.tVal - CurSol.tVal)/temp) > Math.random()) {
                CurSol = Sol_i;
            }
        }
        System.out.println(BestSol.tVal);
    }



    private void addItem(SolInstance S_in) {
        //System.out.println("pBound: " + pBound);
        //System.out.println("mBound: " + mBound);

        rd = new Random(System.currentTimeMillis());
        // Alarm to prevent overflow;
        int alarm1 = 0;
        //int blockBound = (int) blockingPerc * ClsN;

        OuterLoop:
        while (alarm1 < alarmLmt && S_in.p_r >= 0 && S_in.m_r >= 0) {
            int alarm2 = 0;
            int eleAdd = rd.nextInt(N);
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
        }


    }


    private SolInstance CreateInitialSolution() {
        SolInstance initSolution;
        initSolution = new SolInstance();
        //boolean[] initSolution = new boolean[N];
        //int[] BlockedClsArr = new int[N];
        //int numBlocked = 0;


        /*rd = new Random(System.currentTimeMillis());
        *//* alarm to prevent overflow *//*
        int alarm1 = 0;
        //int blockBound = (int) blockingPerc * ClsN;

        while (alarm1 < alarmLmt && initSolution.p_r > pBound
                && initSolution.m_r > mBound) {
            int alarm2 = 0;
            int eleAdd = rd.nextInt(N);
            while (initSolution.blockedClsArr[ClassArr[eleAdd]] > 0
                    && alarm2 < alarmLmt) {
                eleAdd = rd.nextInt(N);
                alarm2 += 1;
            }

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
        }*/
        addItem(initSolution);
        return initSolution;
    }


    private SolInstance CreateNeighborSolution(SolInstance S_cur) {
        rd = new Random(System.currentTimeMillis());

        SolInstance S_new = new SolInstance(S_cur);


/*        int[] containedArr = new int[N];
        int n_c = 0;

        int[] BlockedClsArr = new int[N];

        int[] classTrack = new int[N];

        boolean[] result = new boolean[N];
        for (int i = 0; i < N; i += 1) {
            if (S_cur[i]) {
                result[i] = true;
                containedArr[n_c] = i;
                n_c += 1;
                classTrack[ClassArr[i]] += 1;
                for (int e_block : ClassIncArr[i]) {
                    BlockedClsArr[e_block] += 1;
                }
            }
        }*/

        boolean[] result = S_new.choosenArr;
        int[] contArr = S_new.containedArr;

        int n_c = S_new.numContained;
        if (n_c > 0) {
            int bumping = Math.max(1, (int)(percentKicked* (double) n_c));

            while(bumping > 0){
                int bump = (int)(Math.random() * n_c);
                int bumpIdx = contArr[bump];
                //int bump = rd.nextInt(n_c);
                if (result[bumpIdx]) {
                    result[bumpIdx] = false;
                    S_new.numContained -= 1;

                    S_new.p_r += WeightArr[bumpIdx];
                    S_new.m_r += CostArr[bumpIdx];
                    S_new.tVal -= RevArr[bumpIdx];

                    int clsBump = ClassArr[bumpIdx];
                    S_new.classTrack[clsBump] -= 1;
                    if (S_new.classTrack[clsBump] == 0) {
                        kickRestriction(clsBump, S_new.blockedClsArr);
                    }
                    bumping -= 1;
                }
            }
        }


        addItem(S_new);

        /*int alarm1 = 0;

        while (alarm1 < alarmLmt) {

            int alarm2 = 0;
            int eleAdd = rd.nextInt(N);
            while (S_new.blockedClsArr[ClassArr[eleAdd]] > 0 && alarm2 < alarmLmt) {
                eleAdd = rd.nextInt(N);
                alarm2 += 1;
            }

            S_new.choosenArr[eleAdd] = true;
            S_new.numContained += 1;

            S_new.p_r -= WeightArr[eleAdd];
            S_new.m_r -= CostArr[eleAdd];
            S_new.tVal += RevArr[eleAdd];

            int clsAdd = ClassArr[eleAdd];
            S_new.classTrack[clsAdd] += 1;

            for (int e_block : ClassIncArr[clsAdd]) {
                S_new.blockedClsArr[e_block] += 1;
            }
        }*/

        return S_new;
    }


    private void kickRestriction(int clsKicked, int[] blockedClsArr) {
        for (int e_release : ClassIncArr[clsKicked]) {
            blockedClsArr[e_release] -= 1;
            if (blockedClsArr[e_release] < 0) {
                System.out.println("Uhhhhhhhh. Kicked to the negative");
            }
        }
    }



    /** Return the Optimal Solution, marked by TRUE if the item is in. */
    public boolean[] getOptSolution() {
        return BestSol.choosenArr;
    }


    /** Compute the Bounds for considering whether to continue. */
    /*private void boundInit() {
        Statistics WStat = new Statistics(WeightArr);
        w_mean = WStat.getMean();
        w_var = WStat.getVariance();
        if (w_var != -1) {
            pBound = (long) (w_mean + Math.sqrt(w_var/(1 - confidence)));
        } else {
            pBound = 0;
            System.out.println("The Variance for P is Bad.");
        }

        Statistics CStat = new Statistics(CostArr);
        c_mean = CStat.getMean();
        c_var = CStat.getVariance();
        if (c_var != -1) {
            mBound = (long) (c_mean + Math.sqrt(c_var/(1 - confidence)));
        } else {
            mBound = 0;
            System.out.println("The Variance for M is Bad.");
        }
    }*/




    private long P;

    /** The remaining P. */
    //private long p_r;

    private long M;

    /** The remaining M. */
    //private long m_r;

    private int N;

    /** Number of Classes we consider. */
    private int ClsN;

    /* Number of classes blocked before stopping. */
    private int blkNum;


    /** The Total Value: M + Revenual. */
    private long bestVal;


    /** The mean of valid weight. */
    private long w_mean;

    /** The variance of valid weight. */
    private long w_var;


    /** The Miminum Bound for P_R to continue to consider. */
    //private long pBound;


    /** The mean of valid cost. */
    private long c_mean;

    /** The variance of valid cost. */
    private long c_var;

    /** The Miminum Bound for M_R to  continue to consider. */
    //private long mBound;


    /** Mapping from item index to its class. */
    private int[] ClassArr;
    //Hashtable<Integer, Integer> ClassTable;

    /** Mapping from item index to its weight. */
    private long[] WeightArr;


    /** Mapping from item index to its weight. */
    private long[] CostArr;


    /** Mapping from item index to its weight. */
    private long[] RevArr;


    /** The Best Choosen boolean array that record if
     * each indexed ITEM is selected. */
    //private boolean[] BestChoosenArr;

    private SolInstance BestSol;


    /** The Current Choosen boolean array that record if
     * each indexed ITEM is selected. */
    //private boolean[] CurChoosenArr;

    private SolInstance CurSol;


    /** The HashSet that record if each indexed CLASS is BLOOKED. */
    //private HashSet<Integer> BlockedClsSet;




    /** The array that keep track of how many existing
     * class is blocking a particular class. */
    //private int[] BlockedClsArr;


    /** Mapping from class index to HSet of class index that is incompatible. */
    private HashSet<Integer>[] ClassIncArr;
    //Hashtable<Integer, HashSet<Integer>> ClassIncTable;

    /** The random number generator of the random functions. */
    private Random rd;


    /** The Temperature. */
    private double temp;


}
