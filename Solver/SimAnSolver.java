package Solver;

import java.util.HashSet;
import java.util.Random;

/**
 * Created by yxiaocheng1997 on 4/20/17.
 */
public class SimAnSolver {

    private final double confidence = 0.9;
    private final double blockingPerc = 0.9;
    private final int alarmLmt = 100;

    public SimAnSolver(int p, int m, int n, int clsN,
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


        ClsN = clsN;



        totalVal = 0;
        BlockedClsSet = new HashSet<>();
        boundInit();
    }



    public void CreateInitialSolution() {
        boolean[] initSolution = new boolean[N];
        int blockedNum = 0;
        rd = new Random(System.currentTimeMillis());
        /* alarm to prevent overflow */
        int alarm1 = 0;
        int blockBound = (int) blockingPerc * ClsN;
        while (BlockedClsSet.size() < blockBound && alarm1 < alarmLmt
                && p_r < pBound && m_r < mBound) {
            int alarm2 = 0;
            int eleAdd = rd.nextInt(N);
            while (BlockedClsSet.contains(ClassArr[eleAdd]) && alarm2 < alarmLmt) {
                eleAdd = rd.nextInt(N);
                alarm2 += 1;
            }
            int clsAdd = ClassArr[eleAdd];
            initSolution[eleAdd] = true;

            for (int e_block : ClassIncArr[clsAdd]) {
                if (!BlockedClsSet.contains(e_block)) {
                    BlockedClsSet.add(e_block);
                }
            }


        }



        BestChoosenArr = initSolution;
    }



    /** Compute the Bounds for considering whether to continue. */
    private void boundInit() {
        Statistics WStat = new Statistics(WeightArr);
        w_mean = WStat.getMean();
        w_var = WStat.getVariance();
        if (w_var != -1) {
            pBound = (long) (w_mean + Math.sqrt(w_var/(1 - confidence)));
        } else {
            pBound = -1;
            System.out.println("The Variance for P is Bad.");
        }

        Statistics CStat = new Statistics(CostArr);
        c_mean = CStat.getMean();
        c_var = CStat.getVariance();
        if (c_var != -1) {
            mBound = (long) (c_mean + Math.sqrt(c_var/(1 - confidence)));
        } else {
            mBound = -1;
            System.out.println("The Variance for M is Bad.");
        }
    }




    private long P;

    /** The remaining P. */
    private long p_r;

    private long M;

    /** The remaining M. */
    private long m_r;

    private int N;

    /** Number of Classes we consider. */
    private int ClsN;

    /* Number of classes blocked before stopping. */
    private int blkNum;


    /** The Total Value: M + Revenual. */
    private long totalVal;


    /** The mean of valid weight. */
    private long w_mean;

    /** The variance of valid weight. */
    private long w_var;


    /** The Miminum Bound for P_R to continue to consider. */
    private long pBound;


    /** The mean of valid cost. */
    private long c_mean;

    /** The variance of valid cost. */
    private long c_var;

    /** The Miminum Bound for M_R to  continue to consider. */
    private long mBound;


    /** Mapping from item index to its class. */
    private int[] ClassArr;
    //Hashtable<Integer, Integer> ClassTable;

    /** Mapping from item index to its weight. */
    private long[] WeightArr;


    /** Mapping from item index to its weight. */
    private long[] CostArr;


    /** Mapping from item index to its weight. */
    private long[] RevArr;


    /** The Best Choosen boolean array that record if each indexed ITEM is selected. */
    private boolean[] BestChoosenArr;

    /** The HashSet that record if each indexed CLASS is BLOOKED. */
    private HashSet<Integer> BlockedClsSet;


    /** Mapping from class index to item index. */
    private HashSet<Integer>[] ClassIdxArr;
    //Hashtable<Integer, HashSet<Integer>> ClassIdxTable;

    /** Mapping from class index to HSet of class index that is incompatible. */
    private HashSet<Integer>[] ClassIncArr;
    //Hashtable<Integer, HashSet<Integer>> ClassIncTable;

    /** The random number generator of the random functions. */
    private Random rd;




}
