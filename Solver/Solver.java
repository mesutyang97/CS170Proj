package Solver;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.LinkedList;
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

        P = convertDouble(_input.next());
        M = convertDouble(_input.next());
        N = _input.nextInt();
        NameTable = new Hashtable<>(N);
        C = _input.nextInt();

        Pattern item_pat = Pattern.compile("(.*?); (.*?); (.*?); (.*?); (.*?) .*?");

        for (int i = 0; i < N; i += 1) {
            Matcher m = item_pat.matcher(_input.nextLine());
            m.matches();
            String name = m.group(1);
            int cls = Integer.parseInt(m.group(2));
            long wt = convertDouble(m.group(3));
            long cost = convertDouble(m.group(4));
            long val = convertDouble(m.group(5));

            NameTable.put(i, name);
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
        readInputInit();
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

    /** Mapping from item index to its name. */
    Hashtable<Integer, String> NameTable;

    /** Number of constrains. */
    private int C;






}
