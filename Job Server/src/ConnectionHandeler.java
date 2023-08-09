import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

public class ConnectionHandeler extends Thread{
    private static ArrayList<Worker> workers = new ArrayList<>();
    private ServerSocket socket;
    private boolean isClosed; 
    private static byte[] methodCaller;
    public ConnectionHandeler(ServerSocket socket){
        this.socket = socket;
    }
    @Override
    public void run(){
        try {
            while(!isClosed){
                Socket connection = socket.accept();
                connection.setKeepAlive(true);
                ObjectOutputStream output = new ObjectOutputStream(connection.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(connection.getInputStream());
                Worker worker = new Worker(connection, input, output, UUID.randomUUID());
                worker.start();
                worker.request(new Request("GIVE_CLASS", methodCaller));
                workers.add(worker);
                DataDistributor.unlock();
                
            }
        } catch (IOException e) {}
    }
    public static void setMethodCaller(byte[] methodCaller) throws IOException{
        ConnectionHandeler.methodCaller = methodCaller;
    }
    public synchronized ArrayList<Worker> getWorkers(){
        refreshWorkers();
        return workers;
    }
    private static synchronized void refreshWorkers(){
        for (int i = 0; i < workers.size(); i++) {
            Worker w = workers.get(i);
            if (!w.isConnected()){
                w.close();
                workers.remove(i);
            }
        }
    }
    public void close(){
        for (Worker w : workers) {
            w.close();
        }
        try {
            socket.close();
        } catch (IOException e) {}
        isClosed = true;
    }
}
