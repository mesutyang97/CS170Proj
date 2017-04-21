package Solver;

/**
 * Created by yxiaocheng1997 on 4/20/17.
 */
public class Statistics {
    long[] data;
    int validEntry;

    public Statistics(long[] data)
    {
        this.data = data;
    }


    public long getMean()
    {
        long sum = 0;
        int counter = 0;
        for(long a : data) {
            if (a > 0) {
                sum += a;
                counter += 1;
            }
        }
        validEntry = counter;
        return sum/counter;
    }

    public long getVariance()
    {
        /* Each entry is alotted an amount
        that they are allowed to contribute to TEMP.*/
        long SqrtBound =
                (long) (Math.sqrt(9223372036854775807L/validEntry));
        int boundBit = 0;
        while (SqrtBound >0) {
            SqrtBound = SqrtBound >>> 1;
            boundBit += 1;
        }
        //System.out.println("BoundBit it: " + boundBit);


        long mean = getMean();
        if (mean >>> boundBit > 0) {
            return -1;
        }
        long temp = 0;
        int counter = 0;

        for(long a :data) {
            if (a > 0 && ((a - mean) >>> boundBit == 0 || (mean - a) >>> boundBit == 0 )) {
                temp += (a-mean)*(a-mean);
                //System.out.println(temp);
                counter += 1;
            }
        }
        return temp/counter;
    }
}
