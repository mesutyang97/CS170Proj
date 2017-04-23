package InputWriter;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static InputWriter.InputWriterException.error;


/**
 * InputWriter.Main Class for Input.
 */
public class WriteInputSmall {

    public static void main(String... args) {
        try {
            new WriteInputSmall(args).process();
            return;
        } catch (InputWriterException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    WriteInputSmall(String[] args) {
        if (args.length != 2) {
            throw error("Only 2 command-line arguments allowed");
        }
        _input = getInput(args[0]);
        _output = getOutput(args[1]);
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
        long weightBudget = P/(capN /24);
        long unitBuget = M/(capN /330);
        long unitProfit = (long)(unitBuget*0.3);
        long unitBugetE = (long)(unitBuget / 1.1);
        long unitProfitE = (long)(unitBugetE*0.2);

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
            //_output.println(String.format(itemFormat, nameArr[i],
                    //Integer.toString((i% N)* 2), weightArr[i], costArr[i], valArr[i]));
            _output.println(String.format(itemFormat, nameArr[i],
                    Integer.toString((i % (capN / facC))), weightArr[i], costArr[i], valArr[i]));
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
                //_output.println(extraP[0] + ", " + extraP[1]);
                _output.println(extraP[0] + ", " + extraP[1] + ", " + extraP[2] +
                        ", " + extraP[3] + ", " + extraP[4] + ", " + extraP[5]  );
                extraCUsed += 1;
            }
        }
        while (extraCUsed < extraC) {
            Integer[] extraP = extraEArr[extraCUsed];
            //_output.println(extraP[0] + ", " + extraP[1]);
            _output.println(extraP[0] + ", " + extraP[1] + ", " + extraP[2] +
                    ", " + extraP[3] + ", " + extraP[4] + ", " + extraP[5]  );
            extraCUsed += 1;
        }


    }


    public void generateExtraEdges() {
        HashSet<Integer[]> extraESET = new HashSet<>();
        int factedCap = ((capN - 1)/facC);

        while (extraESET.size() < (extraC/2)) {
            int v1 = (int)(N*Math.random());
            //int v2 = (N + 1) + (int)((N)*Math.random());
            int v2 = (N + 1) + (int)((extraC)*Math.random()/(double)facC);
            while (v1 == v2) {
                v2 = (N + 1) + (int)((extraC)*Math.random()/(double)facC);
            }
            int v3 = (int)(factedCap*Math.random());
            while (v3 == v2 || v3 == v1) {
                v3 = (int)(factedCap*Math.random());
            }
            int v4 = (int)(factedCap*Math.random());
            while (v4 == v1 || v4 == v2 || v4 == v3) {
                v4 = (int)(factedCap*Math.random());
            }
            int v5 = (int)(factedCap*Math.random());
            while (v5 == v1 || v5 == v2 || v5 == v3 || v5 == v4) {
                v5 = (int)(factedCap*Math.random());
            }

            int v6 = (int)(factedCap*Math.random());
            while (v6 == v1 || v6 == v2 || v6 == v3 || v6 == v4 || v6 == v5) {
                v6 = (int)(factedCap*Math.random());
            }

            extraESET.add(new Integer[]{v1, v2, v3, v4, v5, v6});
        }

        while (extraESET.size() < extraC) {
            //int v1 = (int)(2*N*Math.random());
            int v1 = factedCap;
            //int v2 = N + 1 + (int)(N*Math.random());
            int v2 = (N + 1) + (int)((extraC)*Math.random()/facC);

            while (v1 == v2) {
                v2 = (N + 1) + (int)((extraC)*Math.random()/facC);
            }
            int v3 = (int)(factedCap*Math.random());
            while (v3 == v2 || v3 == v1) {
                v3 = (int)(factedCap*Math.random());
            }
            int v4 = (int)(factedCap*Math.random());
            while (v4 == v1 || v4 == v2 || v4 == v3) {
                v4 = (int)(factedCap*Math.random());
            }
            int v5 = (int)(factedCap*Math.random());
            while (v5 == v1 || v5 == v2 || v5 == v3 || v5 == v4) {
                v5 = (int)(factedCap*Math.random());
            }

            int v6 = (int)(factedCap*Math.random());
            while (v6 == v1 || v6 == v2 || v6 == v3 || v6 == v4 || v6 == v5) {
                v6 = (int)(factedCap*Math.random());
            }

            extraESET.add(new Integer[]{v1, v2, v3, v4, v5, v6});
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

    /** File for outputting formated INPUT FILE. */
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
    private long P = 216L;

    /** Budget. */
    private long M = 250L;

    /** The maximum cap for N. */
    private final int capN = 723;

    /** The maximum cap for C. */
    private final int capC = 52;

    /** The factor we are shrinking capC by. */
    private final int facC = 2;

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
