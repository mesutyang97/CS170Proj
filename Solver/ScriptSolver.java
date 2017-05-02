package Solver;


import java.util.ArrayList;

import static Solver.SolverException.error;

/**
 * Created by yxiaocheng1997 on 4/21/17.
 */
public class ScriptSolver {
    public static void main(String... args) {
        try {
            new ScriptSolver(args).process();
            return;
        } catch (SolverException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    ScriptSolver(String[] args) {


        /*
        if (args.length != 2) {
            throw error("Only 2 command-line arguments allowed");
        }
        startIndex = Integer.parseInt(args[0]);
        endIndex = Integer.parseInt(args[1]);
        */

        if (args[0].equals("s")) {
            startIndex = Integer.parseInt(args[1]);
            endIndex = Integer.parseInt(args[2]);
            ITERATING = true;
        } else {
            queue = new ArrayList<>(args.length);
            for (int i = 0; i < args.length; i += 1) {
                queue.add(Integer.parseInt(args[i]));
            }
        }

    }

    private void process() {
        /*
        for (int i = startIndex; i < endIndex; i += 1) {
            String inFile = String.format(inputF, i);
            String outFile = String.format(outputF, i);
            Solver.main(inFile, outFile);

        }
        */

        if (ITERATING) {
            for (int i = startIndex; i < endIndex; i += 1) {
                String inFile = String.format(inputF, i);
                String outFile = String.format(outputF, i);
                Solver.main(inFile, outFile);

            }
        } else {
            for (Integer i: queue) {
                String inFile = String.format(inputF, i);
                String outFile = String.format(outputF, i);
                Solver.main(inFile, outFile);
            }
        }

    }




    private int startIndex;

    private int endIndex;

    private static String inputF = "project_instances/problem%d.in";
    private static String outputF = "project_out_overnight/problem%d.out";

    //private static String inputF = "new_problems/problem%d.in";
    //private static String outputF = "project_out_extracredit/problem%d.out";


    private boolean ITERATING;

    private ArrayList<Integer> queue;
}
