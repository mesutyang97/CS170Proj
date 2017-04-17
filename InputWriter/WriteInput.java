package InputWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static InputWriter.InputWriterException.*;


/**
 * InputWriter.Main Class for Input.
 */
public class WriteInput {

    public static void main(String... args) {
        try {
            new WriteInput(args).process();
            return;
        } catch (InputWriterException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    WriteInput(String[] args) {
        if (args.length > 2) {
            throw error("Only 1 or 2 command-line arguments allowed");
        }
        if (args.length == 2) {
            _input = getInput(args[0]);
            _output = getOutput(args[1]);
        } else if (args.length == 1) {
            _input = new Scanner(args[1]);
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


    /** Construct the alphabet. */
    private String[] ConstructAlphabet(Scanner sc) {
        //FIXME
        return new String[] {"a", "b"};
    }






    private void process(){
        readInputInit();

    }

    /** HHH. */
    public static void generate() {
        if (ALPHABET == null) {
            //ALPHABET = ConstructAlphabet(_input);
        }

        int alphLen = ALPHABET.length;
        System.out.println(alphLen);

        int po = 1;
        int counter = 1;

        while (counter < 203) {
            counter *= (alphLen + 1);
            po += 1;
        }

        //ArrayList<String> varNameList = new ArrayList<>(counter);
        //varNameList.add("");
        String[] varNameArr = new String[counter + 1];
        varNameArr[0] = "";


        int runningCounter = 0;
        int prevCounter = 0;
        int index = 0;

        for (int curPow = 1; curPow <po; curPow += 1) {
            int counterThisPow = (int) Math.pow(alphLen, curPow);
            for (int j = 0; j < alphLen; j += 1) {
                String front = ALPHABET[j];
                for (int k = prevCounter; k <= runningCounter; k += 1) {
                    index += 1;
                    String last = varNameArr[k];
                    varNameArr[index] = front + last;
                    System.out.println(front + last);
                }
            }
            runningCounter += counterThisPow;
            prevCounter += (int) Math.pow(alphLen, curPow - 1);
        }

    }



    /** Read the information from the input file. **/
    private void readInputInit() {

        String firstLine = _input.nextLine();

        Pattern title_p = Pattern.compile("p edge ([0-9]+) ([0-9]+)");
        Matcher title_m = title_p.matcher(firstLine);
        if (!title_m.matches()) {
            throw error("Setup does not match!");
        }

        N = Integer.parseInt(title_m.group(0));
        P = Integer.parseInt(title_m.group(1));


        /*
        int sizeString = rotorsAndSetting.size();

        rotorsName = new String[sizeString - 1];

        for (int i = 0; i < (sizeString - 1); i += 1) {
            rotorsName[i] = rotorsAndSetting.get(i);
        }

        rotorSetting = rotorsAndSetting.get(sizeString - 1);


        String plugBoardCycle = readCycles(initSC);

        _plugBoard = new Permutation(plugBoardCycle, _alphabet);
        */
    }



    /** Source of input. */
    private Scanner _input;

    /** File for encoded/decoded messages. */
    private PrintStream _output;


    /** Number of items. */
    private int N;

    /** Number of constrains. */
    private int P;



    /** The set of all Moves other than pass, indexed by from and to row and
     *  column positions. */

    private static final String[] ALPHABET =

            new String[] {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                    "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
                    "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H",
                    "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
                    "U", "V", "W", "X", "Y", "Z"};

    //private static final String[] ALPHABET = new String[]{"a", "b", "c"};


}
