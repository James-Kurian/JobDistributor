import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
public class Worker extends Thread{
    private Socket socket;
    private Job job;
    private ArrayList<Job> completedJobs = new ArrayList<>();
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Thread work;
    private UUID ID;
    private Map<UUID, Request> requests = new ConcurrentHashMap<>();
    public Worker(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream, UUID ID){
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.ID = ID;
    }
    public boolean isFree(){
        return job == null;
    }
    @Override
    public void run(){
        while (!socket.isClosed()){
            try {
                Request response  = (Request) inputStream.readObject();
                Request originalRequest = requests.get(response.ID());
                if (originalRequest != null){
                    originalRequest.storeData(response.getData());
                    requests.remove(response.ID());
                }
            } catch (ClassNotFoundException | IOException e) {
                for (Request r : requests.values()) {
                    r.storeData(null);
                }
                close();
            }
        }
    }

    public void assign(Job job){
        this.job = job;
        work = new Thread(() -> {
            try {
                Request r = new Request("GIVE_JOB", job);
                request(r);
                Job responce = (Job) r.waitForResponce();
                completedJobs.add(responce); //just for stats
                DataDistributor.consolidate(responce.getOutput());
                job.setComplete();
                this.job = null;
                DataDistributor.unlock();
            } catch (InterruptedException | ExecutionException | IOException | NullPointerException e) {
                job.setFailed();
                DataDistributor.unlock();
            }
        });
        work.start();
        

    }
    public synchronized void request(Request request) throws IOException {
        requests.put(request.ID(), request);
        outputStream.writeObject(request);
    }
    public void close(){
        if (!isFree()){
            job.setFailed();
        }
        if (!socket.isClosed()){
            try {
                inputStream.close();
                socket.close();
            } catch (IOException e) {}
        }
    }
    public ObjectInputStream getInputStream(){
        return inputStream;
    }
    public ObjectOutputStream getOutputStrem(){
        return outputStream;
    }
    public Thread getThread(){
        return work;
    }
    public UUID getID(){
        return ID;
    }
    public boolean isConnected() {
        try {
            Request r = new Request("IS_ALIVE");
            request(r);
            r.waitForResponce();

        } catch (IOException | InterruptedException | ExecutionException e) {
            return false;
        }
        return true;
    }

}
