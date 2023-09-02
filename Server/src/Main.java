

public class Main {
    private static Server server;
    private static Gui gui;
    public static void main(String[] args) {
        gui = new Gui();
        // server = new Server(new CountThriteens(), 4445);
        server = new Server(new BogoSort(), 4445);
        server.start();
    }

    public static void runCommand(String command){
        command = command.toUpperCase();
        verbose("> " + command);
        switch (command){
            case "HELP":
                verbose("Commands: HELP, GET_STATS, START");
                break;
            case "STATS":
                verbose(server.getStats());
                break;
            case "START":
                if (!Server.taskIsFinished()){
                    server.runTask();
                } else{
                    verbose("Task has already been complete. Run OUTPUT to see the task output");
                }
                break;
            case "OUTPUT":
                if (Server.taskIsFinished()){
                    printOutput(Server.getOutput());
                } else {
                    verbose("Task has not been completed.");
                }
                break;
            case "EXIT":
                server.close();
                gui.close();

            default:
                verbose("Invalid Command. Type 'help' for a List of Commands");
        }
    }
    // private static void printOutput(Object output) {
    //     verbose(((int)output)+"");
    // }
    private static void printOutput(Object output) {
        int[] o = ((int[])output);
        String print = "[";
        for (int i = 0; i < o.length; i++) {
            print += (i == o.length-1) ? o[i] : o[i] + ",";
        }
        print += "]";
        verbose(print);
    }

    public static void verbose(String msg){
        gui.write(msg + "\n");
    }
}
