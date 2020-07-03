import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoffeeMakerFunctionalTests {

    public static void testForNBeveragesInParallel(int n, List<String> beverages) {
        // Creating thread Pool of N threads
        // So it will act as N outlets of CoffeeMachine and we can make N beverages in parallel
        ExecutorService executorService = Executors.newFixedThreadPool(n);


        // If beverage.size() is greater than N, available threads in ThreadPool
        // Then it will start preparing N beverages in parallel and wait for any thread to finish its execution
        // and remaining beverages will be picked up one by one as threads finish their execution.
        for (int i = 0; i < beverages.size(); i++) {
            Runnable worker = new RunMachine(beverages.get(i));
            executorService.execute(worker);//calling execute method of ExecutorService
        }

        // Waiting for already submitted beverage tasks to finish before shutting down executorService and destorying ThreadPool
        executorService.shutdown();

        // Wait while executorService is still running before returning from function.
        while (!executorService.isTerminated()) {   }
    }
}


class RunMachine implements Runnable {
    private String beverage;
    public RunMachine(String beverage) {
        this.beverage = beverage;
    }
    public void run()
    {
        try
        {
            Machine machine = Machine.getInstance();
            machine.prepareBeverage(beverage);
        }
        catch (Exception e)
        {
            // Throwing an exception
            System.out.println ("Exception is caught");
        }
    }
}