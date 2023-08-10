# JobDistributor
The Job Distributor is a Java-based project designed to allow users to define a task that can be distributed to multiple computers for faster processing. Lots of code is untested and will crash 👍.


Clone the repository
```
git clone https://github.com/James-Kurian/JobDistributor.git
cd .\JobDistributor\
```
The current set up has a lib folder which contains the necessary gson.jar file. This project requires [Gson](https://github.com/google/gson) to run.
Using the current setup requires the gson to be included as a reference library. You can do this in vscode through the settings.json file.
```json
{
    "java.project.referencedLibraries": [
        "lib/**/*.jar",
    ],
}
```
The compiled jar file may require preview features to be enabled.
```
java --enable-preview -jar .\(Server/Worker).jar
```

## Server


### Defining a task
To define a task, create a new class that extends **DistributableTask**. DistributableTask needs to be parameterized with three types. First is the Type of data that will be initially loaded. Second is the Type of data that wll be a chunk. Third is the Type of data a chunk will be transformed into. For example, a possible implementation that counts the number of thirteens in a large array would be declared like so:
```java
public class CountThriteens extends DistributableTask<ArrayList<Integer>, ArrayList<Integer>, Integer>{
  ...
}
```
In this example, a large ArrayList of Integers will load, a chunk will be a sublist of the larger ArrayList, and the ArrayList chunk will convert to an Integer that stores the number of thirteens found in the array.


After declaring the class, implement the five abstract methods **loadData()**, **getNextChunk()**, **transform()**, **consolidate()**, and **isFinished()**. The data returned from loadData() is stored in a variable called loadedData which can be accessed in any other method through a call to **getLoadedData()**, getNextChunk() will return a chunk of data to be processed by a Worker, transform will take a chunk of data from the getNextChunk method as input and return a transformed version of that input, consolidate will take an output chunk from the transform method and integrate it with other data if needed. The data returned by consolidate is the final output of the task if isFinished evaluates to true. The Server calls isFinished() once every time before calling getNextChunk().


You can add functions and variables if needed. To send an instance of a class for processing it must be wrapped in a **ClassWrapper** which takes a class and an instance of a class as input. This will be sent to a Worker where you can call methods from that instance through the ClassWrapper. See **BogoSort.java** for specific implementation.


Once you define a task, make sure to change the declaration of server in **Main.java**
```java
server = new Server(new YourTaskClassHere(), YourPortHere);
```


#### TL; DR
See the two example implementations of the Distributable task class:
1. CountThriteens.java
2. BogoSort.java

Check out the Main.java class.


### Usage

When you the start the Server a GUI will allow for the input of commands.
There are by default five commands:
1. HELP (prints list of commands)
2. STATS (prints number of workers connected and whether the task is running)
3. START (starts the task)
4. OUTPUT (prints the task output if the task is complete)
5. EXIT (closes the task and exits the program)


## Worker

### Usage
The Server prompts the user for a Server ip. Then the Server prompts the user for a port. Once filled in the program receives the DistributableTask class from the Server and waits for Jobs.
A Worker can disconnect at any time. The Server tracks each task and redistributes them if they fail. Server will abort a task if it fails 3 times. You can change this number in DataDistributor.java.
```java
int maxJobStrikes = 3;
```
The Worker is supposed to be dynamic enough that it only needs to compile once. All the defining should be done in the Server. That being said...


## Security Risks
There are an uncountable number of security risks associated with this program. Running code from a random source without checking it is generally not a good idea. The Workers never check any of the code they run. There is no namespace limiting, or encryption, or signing to verify that executable code did indeed come from the Server. This project is more of an exercise than something to actually be used.
