import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Example implimentation with sendable class
 */

public class BogoSort extends DistributableTask<Bogo, ClassWrapper, int[]>{
    private boolean found;
    private int[] sorted;

    @Override
    public Bogo loadData() {
        return new Bogo(new int[] {4,3,7,3,7,1,7,5,3,45,6,87,2}, 10000000);
    }

    @Override
    public ClassWrapper getNextChunk() {
        try {
            return new ClassWrapper(Bogo.class, getLoadedData());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int[] transform(ClassWrapper input) {
        try {
            input.runMethod("trySort");
            input.runMethod("checkSorted");
            int[] output = (int[]) input.runMethod("getSortedArr");
            
            return output;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
        
        
    }

    @Override
    public int[] consolidate(int[] output) {
        if (!found && output != null){
            sorted = output;
            found = true;
        }
        return sorted;
    }

    @Override
    public boolean isFinished() {
        return found;
    }


}
