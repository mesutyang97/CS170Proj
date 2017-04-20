package Solver;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/** JUnit tests for the Solver Package.
 *  @author mesutyang97
 */

public class SolverTest {
    @Rule
    public Timeout globalTimeout = Timeout.seconds(10);

    @Test
    public void testSplitCallNum() {

        Hashtable<Integer, LinkedList> extreme = new Hashtable<>(200000);
        for (int i = 0; i <= 200000; i += 1) {
            extreme.put(i, new LinkedList());
        }

        Double ff = Double.parseDouble("4294967295.23");
        Long l = (long) (ff*100);
        System.out.println(l);
    }


    private long convertDouble(String s) {
        return (long) (Double.parseDouble(s)*100);
    }


    @Test
    public void testParse() {
        Pattern item_pat = Pattern.compile("(.*?); (.*?); (.*?); (.*?); (.*?)");

        Matcher m = item_pat.matcher("item_35369; 1526; 504125.56; 26071.3; 33521.0");


        m.matches();
        String name = m.group(1);
        int cls = Integer.parseInt(m.group(2));
        long wt = convertDouble(m.group(3));
        long cost = convertDouble(m.group(4));
        long val = convertDouble(m.group(5));

        System.out.println(name +" " + cls + " "+wt + " "+ cost+" "+ val);
        //WriteInput.RValueAssignments();
    }





}