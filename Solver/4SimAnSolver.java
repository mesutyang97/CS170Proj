package Solver;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/**
 * Created by yxiaocheng1997 on 4/20/17.
 */
public class SimAnSolver {

    //private final double confidence = 0.8;
    //private final double blockingPerc = 0.9;

    private final long TIMEOUTSEC = 800;
    private final int alarmLmt = 500;
    private final double tempChange = 0.95;
    private final int MAXITER = 300;
    private final int MAXOUTER = 60000;
    private final double percentKicked = 0.3;
    private double initTemp;



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
/*            if (numContained != n_c) {
                System.out.println("numContained is bad in the initilizer. " +
                "Old SolI has " + numContained + " but the loop gives " + n_c);
                System.exit(1);
            }*/

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
        //temp = initTemp;
        try {
            timingSolve();
        } catch (Exception e) {
            System.out.println("Timer Fail :(");
        }

    }



    /**Timer method. */
    public void timingSolve() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new Task());

        try {
            //System.out.println("Started..");
            System.out.println(future.get(TIMEOUTSEC, TimeUnit.SECONDS));
            //System.out.println("Finished!");
        } catch (TimeoutException e) {
            future.cancel(true);
            //System.out.println("Terminated!");
        }

        executor.shutdownNow();
    }



    /** Wrapping Solve() */
    class Task implements Callable<String> {
        @Override
        public String call() throws Exception {
            Solve();
            return "Ready!";
        }
    }


    public void Solve() {
        System.out.println("Entering Solve....");
        overallTemp = 1;
        BestSol = CreateInitialSolution();
        for (int out = 0; out < MAXOUTER; out += 1) {
            if (out == MAXOUTER/10) {
                System.out.println("10% done");
            } else if (out == MAXOUTER/4) {
                System.out.println("25% done.");
            } else if (out == MAXOUTER/2) {
                System.out.println("50% done.");
            } else if (out == 3*MAXOUTER/4) {
                System.out.println("75% done.");
            }

            if (overallTemp < Math.random()) {
                continue;
            }


            SolInstance CurSol = CreateInitialSolution();
            for (int i = 0; i < MAXITER; i += 1) {
                SolInstance Sol_i = CreateNeighborSolution(CurSol);

                //System.out.println(Sol_i.tVal);

                double temp = initTemp;

                long val_i = Sol_i.tVal;
                long val_cur = CurSol.tVal;
                if (val_i > val_cur) {
                    //System.out.println("Local improvement from " + val_cur + " to " + val_i);
                    temp = temp * tempChange;
                    CurSol = Sol_i;
                    if (val_i > BestSol.tVal) {
                        BestSol = Sol_i;
                        System.out.println("Global improvement. Best so far is " + Sol_i.tVal);
                    }
                } else if (Math.exp((Sol_i.tVal - CurSol.tVal)/temp) > 0.998) {
                    break;
                } else if (Math.exp((Sol_i.tVal - CurSol.tVal)/temp) > Math.random()) {
                    CurSol = Sol_i;
                    //System.out.println("Random re-shuffle" + val_cur + " to " + val_i);
                }

                //System.out.println("Finish an OuterLoop with" + CurSol.tVal);
                //System.out.println("-------------------------------");
            }
            if (CurSol.tVal == BestSol.tVal) {
                overallTemp *= 0.95;
            } else if (CurSol.tVal > BestSol.tVal) {
                BestSol = CurSol;
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
        addItem(initSolution);
        return initSolution;
    }


    private SolInstance CreateNeighborSolution(SolInstance S_cur) {
        rd = new Random(System.currentTimeMillis());

        SolInstance S_new = new SolInstance(S_cur);

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

    public long getOptVal() {return BestSol.tVal;}


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

    private long M;

    private int N;


    /** The mean of valid weight. */
    private long w_mean;

    /** The variance of valid weight. */
    private long w_var;

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


    /** The Best Choosen Solution Instance. */
    private SolInstance BestSol;


    /** Mapping from class index to HSet of class index that is incompatible. */
    private HashSet<Integer>[] ClassIncArr;

    /** The random number generator of the random functions. */
    private Random rd;


    /** The Temperature. */
    private double overallTemp;


}
