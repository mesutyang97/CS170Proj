package Solver;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;



/** JUnit tests for the Solver Package.
 *  @author mesutyang97
 */

public class StatTest {
    @Rule
    public Timeout globalTimeout = Timeout.seconds(10);

    @Test
    public void testStat() {
        long[] dt = new long[]{3249827L, 42949672960L, 45389573L, 4758357L, 0L, 73289416L};


        Statistics st = new Statistics(dt);
        long a = st.getMean();
        long b = st.getVariance();


        System.out.println(a +" " + b);
    }





}
