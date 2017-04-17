package InputWriter;

import java.io.*;
import java.util.LinkedList;
import java.util.HashSet;
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


    /** Construct the alphabet. */
    private String[] ConstructAlphabet(Scanner sc) {
        //FIXME
        return new String[] {"a", "b"};
    }






    private void process(){
        readInputInit();
        RValueAssignments();
        generateName();
        writeInputFile();


    }


    /** Read the information from the input file. **/
    private void readInputInit() {
        String firstLine = _input.nextLine();

        Pattern title_p = Pattern.compile("p edge ([0-9]+) ([0-9]+) .*?");
        Matcher title_m = title_p.matcher(firstLine);
        if (!title_m.matches()) {
            throw error("Setup does not match! " + firstLine);
        }
        N = Integer.parseInt(title_m.group(1));
        extraN = capN - N - 1;
        C = Integer.parseInt(title_m.group(2));
        extraC = capC - C - 1;
    }



    /** AssignVals. */
    public void RValueAssignments() {

        int seed = 5;
        long weightBudget = P/(capN /20);
        long unitBuget = M/(capN / 120);
        long unitProfit = unitBuget*3;
        long unitBugetE = (long)(unitBuget / 1.1);
        long unitProfitE = unitBugetE*2;

        weightArr = new String[capN];
        costArr = new String[capN];
        valArr = new String[capN];

        for (int i = 1; i <= N; i += 1) {

            int shuf = (int)((Math.random()*seed));

            for (int j = 0; j < (shuf + 4); j += 1) {
                Math.random();
            }
            String wIntPart = Long.toString((long)(weightBudget*Math.random()));

            for (int j = 0; j < shuf; j += 1) {
                Math.random();
            }
            long cost1 = (long)(unitBuget*Math.random());
            String intPart1 = Long.toString(cost1);

            for (int j = 0; j < (shuf + 3); j += 1) {
                Math.random();
            }
            long cost2 = (long)(unitProfit*Math.random());
            String intPart2 = Long.toString(cost1+cost2);


            for (int j = 0; j < (shuf + 1); j += 1) {
                Math.random();
            }
            String wDecPart = Integer.toString((int)(100*Math.random()));

            for (int j = 0; j < (shuf + 2); j += 1) {
                Math.random();
            }
            String decPart1 = Integer.toString((int)(100*Math.random()));

            for (int j = 0; j < (shuf + 1); j += 1) {
                Math.random();
            }
            String decPart2 = Integer.toString((int)(100*Math.random()));


            String finalWeight = wIntPart + "." + wDecPart;
            String finalCost = intPart1 + "."+ decPart1;
            String finalVal = intPart2 + "."+ decPart2;

            weightArr[i] = finalWeight;
            costArr[i] = finalCost;
            valArr[i] = finalVal;
        }


        for (int i = N + 1; i < capN; i += 1) {

            int shuf = (int)((Math.random()*(seed - 1)));

            for (int j = 0; j < (shuf + 4); j += 1) {
                Math.random();
            }
            String wIntPart = Long.toString((long) (weightBudget*Math.random()));

            for (int j = 0; j < shuf; j += 1) {
                Math.random();
            }
            long cost1 = (long)(unitBugetE*Math.random());
            String intPart1 = Long.toString(cost1);

            for (int j = 0; j < (shuf + 2); j += 1) {
                Math.random();
            }
            long cost2 = (long)(unitProfitE*Math.random());
            String intPart2 = Long.toString(cost1+cost2);


            for (int j = 0; j < (shuf + 1); j += 1) {
                Math.random();
            }
            String wDecPart = Integer.toString((int)(100*Math.random()));

            for (int j = 0; j < (shuf + 2); j += 1) {
                Math.random();
            }
            String decPart1 = Integer.toString((int)(100*Math.random()));

            for (int j = 0; j < (shuf + 1); j += 1) {
                Math.random();
            }
            String decPart2 = Integer.toString((int)(100*Math.random()));


            String finalWeight = wIntPart + "." + wDecPart;
            String finalCost = intPart1 + "."+ decPart1;
            String finalVal = intPart2 + "."+ decPart2;

            weightArr[i] = finalWeight;
            costArr[i] = finalCost;
            valArr[i] = finalVal;
        }

    }


    /** HHH. */
    public void generateName() {
        if (ALPHABET == null) {
            //ALPHABET = ConstructAlphabet(_input);
        }

        int alphLen = ALPHABET.length;

        int po = 1;
        int counter = 1;

        while (counter < capN) {
            counter *= (alphLen + 1);
            po += 1;
        }

        nameArr = new String[counter + 1];
        nameArr[0] = "";


        int runningCounter = 0;
        int prevCounter = 0;
        int index = 0;

        for (int curPow = 1; curPow <po; curPow += 1) {
            int counterThisPow = (int) Math.pow(alphLen, curPow);
            for (int j = 0; j < alphLen; j += 1) {
                String front = ALPHABET[j];
                for (int k = prevCounter; k <= runningCounter; k += 1) {
                    index += 1;
                    String last = nameArr[k];
                    nameArr[index] = front + last;
                }
            }
            runningCounter += counterThisPow;
            prevCounter += (int) Math.pow(alphLen, curPow - 1);
        }
    }


    public void writeInputFile() {
        _output.println(P +"."+ (int)(100*Math.random()));
        _output.println(M +"."+  (int)(100*Math.random()));
        _output.println(capN);
        generateExtraEdges();
        _output.println(capC);
        writeItems();
        writeEdges();
    }


    public void writeItems() {
        _output.println("TABanana; 0; 106842.03; 1000080.01; 1530000.00");

        String itemFormat = "%s; %s; %s; %s; %s";
        for (int i = 1; i <=N; i += 1) {
            _output.println(String.format(itemFormat, nameArr[i],
                    Integer.toString(i), weightArr[i], costArr[i], valArr[i]));
        }

        for (int i = N + 1; i < capN; i += 1) {
            _output.println(String.format(itemFormat, nameArr[i],
                    Integer.toString((i% N)* 2), weightArr[i], costArr[i], valArr[i]));
            //_output.println(String.format(itemFormat, nameArr[i],
                    //Integer.toString((i)), weightArr[i], costArr[i], valArr[i]));
        }
    }

    public void writeEdges() {
        while (_input.hasNextLine()) {
            extraCUsed = 0;
            String nextEdge = _input.nextLine();
            Pattern title_p = Pattern.compile("e ([0-9]+) ([0-9]+)");
            Matcher title_m = title_p.matcher(nextEdge);
            if (!title_m.matches()) {
                throw error("Edges does not match!");
            }
            _output.println(title_m.group(1) + ", " + title_m.group(2));
            if (extraCUsed < extraC && (int)(C*Math.random()) < N ) {
                Integer[] extraP = extraEArr[extraCUsed];
                _output.println(extraP[0] + ", " + extraP[1]);
                extraCUsed += 1;
            }
        }
        while (extraCUsed < extraC) {
            Integer[] extraP = extraEArr[extraCUsed];
            _output.println(extraP[0] + ", " + extraP[1]);
            extraCUsed += 1;
        }


    }


    public void generateExtraEdges() {
        HashSet<Integer[]> extraESET = new HashSet<>();
        while (extraESET.size() < (extraC/2)) {
            int v1 = (int)(N*Math.random());
            int v2 = (N + 1) + (int)((N)*Math.random());
            //int v2 = (N + 1) + (int)((extraC)*Math.random());
            extraESET.add(new Integer[]{v1, v2});
        }

        while (extraESET.size() < extraC) {
            int v1 = (int)(2*N*Math.random());
            //int v1 = (int)((capC - 1)*Math.random());
            int v2 = N + 1 + (int)(N*Math.random());
            //int v2 = (N + 1) + (int)((extraC)*Math.random());
            extraESET.add(new Integer[]{v1, v2});
        }

        extraEArr = new Integer[extraC][];

        LinkedList<Integer[]> extraEList = new LinkedList<>();

        for (Integer[] x : extraESET) {
            extraEList.add(x);
        }
        for (int i = 0; i < extraC; i += 1) {
            extraEArr[i] = extraEList.get(i);
        }


    }



    /** Source of input. */
    private Scanner _input;

    /** File for encoded/decoded messages. */
    private PrintStream _output;


    /** Array for names of items. */
    private String[] nameArr;

    /** Array for weight of items. */
    private String[] weightArr;

    /** Array for cost of items. */
    private String[] costArr;

    /** Array for values of items. */
    private String[] valArr;

    /** Number of Pounds. */
    private long P = 489271L;

    /** Budget. */
    private long M = 59435L;

    /** The maximum cap for N. */
    private final int capN = 85000;

    /** The maximum cap for C. */
    private final int capC = 130000;

    /** Number of items in sourcesFile. */
    private int N;

    /** Number of extra imaginary items. */
    private int extraN;

    /** Number of constrains. */
    private int C;

    /** Number of extra constrains. */
    private int extraC;

    /** Number of extra constrain used. */
    private int extraCUsed;

    /** Array of extra constrain. */
    private Integer[][] extraEArr;


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
