
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

public class DataDistributor extends Thread{
    private static ArrayList<Worker> workers = new ArrayList<>();
    private ConnectionHandeler connectionHandeler;
    private boolean isFinished;
    private Object output;
    private ArrayList<Job> assignedJobs = new ArrayList<>();
    private ArrayList<Job> failedJobs = new ArrayList<>();
    private ArrayList<Job> abortedJobs = new ArrayList<>();
    private int redistributedJobs;
    private int maxJobStrikes = 3;
    private static Object lock = new Object();
    private static DistributableTask<?, ?, ?> task;
    public DataDistributor(DistributableTask<?, ?, ?> task, ConnectionHandeler connectionHandeler) {
        DataDistributor.task = task;
        this.connectionHandeler = connectionHandeler;
    }
   
    @Override
    public void run(){
        while(!isFinished){
            workers = connectionHandeler.getWorkers();
            for (int i = assignedJobs.size() - 1; i >= 0 ; i--) {
                Job job = assignedJobs.get(i);
                if (job.isComplete()){
                    assignedJobs.remove(i);
                } else if (job.isFailed()){
                    assignedJobs.remove(i);
                    if (job.getNumStrikes() >= maxJobStrikes){
                        abortedJobs.add(job);
                        redistributedJobs -= maxJobStrikes - 1;
                    }else{
                        redistributedJobs++;
                        failedJobs.add(job);
                    }
                }
            }
            boolean workerWasFree = false;
            for (Worker w : workers) {
                if (task.isFinished() && failedJobs.size() == 0){
                    workerWasFree = true;
                    freeAllWorkers();
                    output = task.getOutput();
                    isFinished = true;
                    Server.verbose("The task has been complete");
                    Server.verbose(redistributedJobs + " Jobs corrected");
                    Server.verbose(abortedJobs.size() + " Jobs aborted");
                    Server.setTaskIsFinished(true);
                    Server.setOutput(output);
                    break;
                } else if (w.isFree()){
                    Job job;
                    workerWasFree = true;
                    if (failedJobs.size() != 0){
                        job = failedJobs.remove(0);
                        job.setInProgress();
                    }else {
                        Object dataChunk = task.getNextChunk();
                        UUID jobID = UUID.randomUUID();
                        job = new Job(jobID, dataChunk, w.getID());
                    }
                    assignedJobs.add(job);
                    w.assign(job);
                }
            }
            if (!workerWasFree){
                try {
                    synchronized(lock){
                        lock.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }
    public static Method getMethodByName(Class<?> clazz, String methodName) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }
    private void freeAllWorkers() {
        for (Worker w : workers) {
            try {
                w.getThread().join();
                w.request(new Request("TASK_COMPLETE"));
                w.close();
            } catch (InterruptedException | IOException e) {} 
        }
    }
    public Object getOutput(){
        return output;
    }
    public static synchronized void unlock(){
        synchronized(lock){
            lock.notify();
        }
    }

    public static void consolidate(Object data){
        task.updateOutput(data);
        
        
        
    }
    public void close(){
        unlock();
        isFinished = true;
    }
}
