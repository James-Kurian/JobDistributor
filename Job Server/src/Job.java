import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class Job implements Serializable{
    private Object data;
    private Object output;
    private UUID jobID;
    private int strike;
    private enum Status {
        COMPLETE, INPROGRESS, FAILED
    }
    private Status jobStatus = Status.INPROGRESS;
    private static DistributableTask<?, ?, ?> methodCaller;

    public Job(UUID jobID, Object data, UUID assignedWorkerID){
        this.jobID = jobID;
        this.data = data;
    }
    public void compute() throws IllegalAccessException, InvocationTargetException{
        output = methodCaller.runTransform(data);
        clearData();
    }

    public Object getOutput(){
        return output;
    }
    public UUID getJobID(){
        return jobID;
    }
    private void clearData(){
        data = null;
    }
    public static void setMethodCaller(DistributableTask<?, ?, ?> mc){
        methodCaller = mc;
    }
    public void freeMemory() {
        methodCaller.freeMemory();
    }
    public void setComplete() {
        jobStatus = Status.COMPLETE;
    }
    public void setInProgress() {
        jobStatus = Status.INPROGRESS;
    }
    public void setFailed() {
        if (!(jobStatus == Status.FAILED)){
            strike++;
            jobStatus = Status.FAILED;
        }

    }
    public boolean isComplete(){
        return jobStatus == Status.COMPLETE;
    }
    public boolean isFailed(){
        return jobStatus == Status.FAILED;
    }
    public int getNumStrikes(){
        return strike;
    }

    
}
