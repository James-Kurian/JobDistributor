import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Bogo implements Serializable{
    private int numAttemps;
    private int[] sortedArr;
    private int[] data;
    int[][] attemps;
    public Bogo(int[] data, int numAttemps){
        this.data = data;
        this.numAttemps = numAttemps;
    }
    public void trySort(){
        attemps = new int[numAttemps][];
        for (int i = 0; i < numAttemps; i++) {
            List<Integer> dataList = Arrays.asList(Arrays.stream(data).boxed().toArray(Integer[]::new));
            Collections.shuffle(dataList);

            Integer[] shuffledDataList = dataList.toArray(new Integer[0]);
            attemps[i] = Arrays.stream(shuffledDataList).mapToInt(Integer::intValue).toArray();
            
        }
    }
    public void checkSorted(){
        for (int i = 0; i < attemps.length; i++) {
            int[] arrInQuesiton = attemps[i]; 
            int prevNum = Integer.MIN_VALUE;
            boolean found = true;
            for (int j : arrInQuesiton) {
                if (j < prevNum){
                    found = false;
                    break;
                }
                prevNum = j;
            }
            if (found){
                sortedArr = arrInQuesiton;
                break;
            }
        }
    }
    public boolean isSorted(){
        return sortedArr != null;
    }
    public int[] getSortedArr(){
        return sortedArr;
    }
}
