import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Example implimentation
 */

public class CountThriteens extends DistributableTask<ArrayList<Integer>, ArrayList<Integer>, Integer>{
    private int CHUNK_SIZE = 100000;
    private int startingIndex = 0;
    private boolean finished = false;
    private int numThirteens;
    
    public ArrayList<Integer> loadData(){
        Path fileName = Path.of("randNum.txt");
        ArrayList<Integer> data = new ArrayList<>();
        try {
            String strList = Files.readString(fileName);
            String[] list = strList.split(",");
            for (String string : list) {
                data.add(Integer.parseInt(string));
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }
    
    public ArrayList<Integer> getNextChunk(){

        ArrayList<Integer> loadedData = getLoadedData();
        int endIndex = startingIndex + CHUNK_SIZE;
        
        if (endIndex >= loadedData.size()){
            endIndex = loadedData.size();
            finished = true;
        }

        ArrayList<Integer> task = new ArrayList<Integer>(loadedData.subList(startingIndex, endIndex));
        startingIndex += CHUNK_SIZE;

        return task;
    }
    public Integer transform(ArrayList<Integer> inputChunk){
        int numThirteens = 0;
        for (int i : inputChunk) {
            if (i==13){
                numThirteens++;
            }
        }

        return numThirteens;
    }
    public Integer consolidate(Integer output){
        numThirteens += output;
        return numThirteens;
    }

    public boolean isFinished(){
        return finished;
    }


    
}
