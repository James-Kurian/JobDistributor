import java.io.Serializable;

public abstract class DistributableTask<I, M, O> implements Serializable{
    private I loadedData;
    private O output;

    private M inputChunk;
    private O outputChunk;
    public abstract I loadData();

    public void runLoadData(){
        loadedData = loadData();

    }

    public abstract M getNextChunk();

    public abstract O transform(M input);

    @SuppressWarnings("unchecked")
    public O runTransform(Object input){
        inputChunk = (M)input;
        if (input instanceof ClassWrapper){
            ((ClassWrapper)input).loadClazz();
            ((ClassWrapper)input).loadInstance();
        }
        outputChunk = transform((M) input);
        return outputChunk;
    }

    public abstract O consolidate(O output);

    @SuppressWarnings("unchecked")
    public synchronized void updateOutput(Object output){
        this.output = consolidate((O) output);
    }

    public abstract boolean isFinished();

    public I getLoadedData(){
        return loadedData;
    }
    public void freeMemory(){
        if (inputChunk instanceof ClassWrapper){
            ((ClassWrapper)inputChunk).freeMemory();
        }
        if (outputChunk instanceof ClassWrapper){
            ((ClassWrapper)outputChunk).freeMemory();
        }
    }

    public O getOutput(){
        return output;
    }

}