import java.net.ServerSocket;
import java.io.IOException;


public class Server{
    private DataDistributor dataDistributor;
    private ConnectionHandeler connectionHandeler;
    private DistributableTask<?, ?, ?> task;
    private int port;
    private static Object output;
    private static boolean taskIsFinished;
    public Server(DistributableTask<?, ?, ?> task, int port){
        this.task = task;
        this.port = port;
    }
    public void start(){
        try {
            task.runLoadData();
            SendableClass classLoader = new SendableClass();
            ConnectionHandeler.setMethodCaller(classLoader.getClassBytes(task.getClass().getName()));
            ServerSocket serverSocket = new ServerSocket(port);
            verbose("Server Started Successfully.");

            connectionHandeler = new ConnectionHandeler(serverSocket);
            connectionHandeler.start();

 
            } catch (IOException e) {}
            
    }  
    public void runTask(){
        if (connectionHandeler.getWorkers().size() == 0){
            verbose("There are no Workers online.");
            return;
        }
        verbose("Task has been started");
        dataDistributor = new DataDistributor(task, connectionHandeler);
        dataDistributor.start();

    }
    public static void verbose(String msg){
        Main.verbose(msg);
    }
    public static void setOutput(Object o){
        Server.output = o;
    }
    public static Object getOutput(){
        return Server.output;
    }
    public static void setTaskIsFinished(boolean isFinished){
        Server.taskIsFinished = isFinished;
    }
    public static boolean taskIsFinished(){
        return Server.taskIsFinished;
    }
    public String getStats() {
        String stats = "";
        stats += "Task is active: " + (dataDistributor != null && dataDistributor.isAlive()) + "\n";
        stats += "Number of workers: " + connectionHandeler.getWorkers().size();
        return stats;
    }
    public void close(){
        if (dataDistributor != null){
            dataDistributor.close();
        }
        connectionHandeler.close();
    }
}


