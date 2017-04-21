package Solver;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Solver.SolverException.*;

/**
 * Created by yxiaocheng1997 on 4/19/17.
 */
public class Solver {

    public static void main(String... args) {
        try {
            new Solver(args).process();
            return;
        } catch (SolverException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Solver(String[] args) {
        if (args.length != 2) {
            throw error("Only 2 command-line arguments allowed");
        }
        _input = getInput(args[0]);
        _output = getOutput(args[1]);

    }


    /** Convert Double in a String to Long that has no decimal (multiply by 100).*/
    private long convertDouble(String s) {
        return (long) (Double.parseDouble(s)*100);
    }


    /** Read the information from the input file. **/
    private void readInputInit() {

        //Pattern digit = Pattern.compile("([0-9]+.[0-9]{2})");

        P = convertDouble(_input.nextLine());
        M = convertDouble(_input.nextLine());
        N = Integer.parseInt(_input.nextLine());

        ClsN = 0;

        // NameTable = new Hashtable<>(N);
        // ClassTable = new Hashtable<>(N);

        NameArr = new String[N];
        ClassArr = new int[N];
        WeightArr = new long[N];
        CostArr = new long[N];
        RevArr = new long[N];


        //ClassIdxTable = new Hashtable<>(N);
        ClassIdxArr = new HashSet[N];

        //ClassIncTable = new Hashtable<>(N);
        ClassIncArr = new HashSet[N];

        /** Initializing all class to be an empty HSet. */
        for (int cls = 0; cls < N; cls += 1) {
            //ClassIdxTable.put(cls, new HashSet<>());
            ClassIdxArr[cls] = new HashSet<>();
        }

        /** Initializing all class to be an empty HSet. */
        for (int cls = 0; cls < N; cls += 1) {
            //ClassIncTable.put(cls, new HashSet<>());
            ClassIncArr[cls] = new HashSet<>();
        }


        C = Integer.parseInt(_input.nextLine());


        Pattern item_pat = Pattern.compile("(.*?); (.*?); (.*?); (.*?); (.*?)");

        for (int i = 0; i < N; i += 1) {
            Matcher m = item_pat.matcher(_input.nextLine());
            if (!m.matches()) {
                error("Stuck on the %d line", (i+5));
            }


            String name = "";
            try {
                name = m.group(1);
            } catch (java.lang.IllegalStateException e) {
                System.out.println("Stuck on the %d line" + (i+5));
                System.exit(1);
            }

            int cls = Integer.parseInt(m.group(2));
            long wt = convertDouble(m.group(3));
            long cost = convertDouble(m.group(4));
            long val = convertDouble(m.group(5));

            if (val > cost && wt < 429496729600L && cost < 429496729600L) {
                //NameTable.put(i, name);
                //ClassTable.put(i, cls);
                NameArr[i] = name;
                ClassArr[i] = cls;
                WeightArr[i] = wt;
                CostArr[i] = cost;
                RevArr[i] = val - cost;

                if (ClassIdxArr[cls].isEmpty()) {
                    ClsN += 1;
                }

                ClassIdxArr[cls].add(i);
                //ClassIdxTable.get(cls).add(i);
            }
        }

        for (int j = 0; j < C; j += 1) {
            ArrayList<Integer> cstrList = new ArrayList<>(2);
            Scanner cstrSC = new Scanner(_input.nextLine() + ",");
            while (cstrSC.hasNext()) {
                String s = cstrSC.next();
                cstrList.add(Integer.parseInt(s.substring(0,s.length() - 1)));
            }

            int b = cstrList.size();

            /*Add incompatible class L into the blacklist of the class K.*/
            for (int k = 0; k < b; k += 1) {
                for (int l = 0; l < b; l += 1) {
                    if (l != k) {
                        //ClassIncTable.get(k).add(l);
                        ClassIncArr[cstrList.get(k)].add(cstrList.get(l));
                    }
                }
            }


        }

        System.out.println("Done");


    }





    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }


    private void printResult(boolean[] choosenArr) {
        for (int i = 0; i < N; i += 1) {
            if (choosenArr[i]) {
                if (NameArr[i] != null) {
                    _output.println(NameArr[i]);
                }
            }
        }
    }

    /** process the input and makes the output. */
    private void process(){
        readInputInit();
        SimAnSolver sol = new SimAnSolver(P, M, N, ClassArr, WeightArr, CostArr, RevArr, ClassIncArr);
        printResult(sol.getOptSolution());
    }


    /** Source of input. */
    private Scanner _input;

    /** File for encoded/decoded messages. */
    private PrintStream _output;


    /** Number of Pounds. */
    private long P;

    /** Budget. */
    private long M;

    /** Number of items in sourcesFile. */
    private int N;


    /** Number of Classes we consider. */
    private int ClsN;

    /** Mapping from item index to its name. */
    private String[] NameArr;
    //Hashtable<Integer, String> NameTable;

    /** Mapping from item index to its class. */
    private int[] ClassArr;
    //Hashtable<Integer, Integer> ClassTable;

    /** Mapping from item index to its weight. */
    private long[] WeightArr;


    /** Mapping from item index to its weight. */
    private long[] CostArr;


    /** Mapping from item index to its weight. */
    private long[] RevArr;

    /** Mapping from class index to item index. */
    private HashSet<Integer>[] ClassIdxArr;
    //Hashtable<Integer, HashSet<Integer>> ClassIdxTable;

    /** Mapping from class index to HSet of class index that is incompatible. */
    private HashSet<Integer>[] ClassIncArr;
    //Hashtable<Integer, HashSet<Integer>> ClassIncTable;


    /** Number of constrains. */
    private int C;






}
