package InputWriter;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.Hashtable;
import java.util.LinkedList;


/** JUnit tests for the InputWriter class.
 *  @author mesutyang97
 */

public class WITest {
    @Rule
    public Timeout globalTimeout = Timeout.seconds(10);

    @Test
    public void testSplitCallNum() {
        int v2 = (1159 + 1) + (int)((1150)*Math.random());
        System.out.println(v2);
    }

    @Test
    public void testRVA() {
        //WriteInput.RValueAssignments();
    }

}
