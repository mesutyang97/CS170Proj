package Solver;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

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
        if (args.length > 2) {
            throw error("Only 1 or 2 command-line arguments allowed");
        }
        if (args.length == 2) {
            _input = getInput(args[0]);
            _output = getOutput(args[1]);
        } else if (args.length == 1) {
            _input = new Scanner(args[0]);
            _output = System.out;
        }
        else {
            //_input = new Scanner(System.in);
            _output = System.out;
        }

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

    /** process the input and makes the output. */
    private void process(){
    }


    /** Source of input. */
    private Scanner _input;

    /** File for encoded/decoded messages. */
    private PrintStream _output;



}
