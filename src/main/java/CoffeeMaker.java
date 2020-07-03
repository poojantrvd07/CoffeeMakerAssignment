import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class CoffeeMaker {
    public static void main(String args[]) throws InterruptedException {
        // Singleton instance of Machine to reinitialize Machine after every test
        Machine machine = Machine.getInstance();

        List<String> firstTestBeverages = new ArrayList<>(Arrays.asList("hot_coffee", "hot_tea", "black_tea"));
        List<String> secondTestBeverages = new ArrayList<>(Arrays.asList("hot_coffee", "hot_tea", "black_tea", "hot_coffee"));


        System.out.println("Test for 3 beverages in parallel with 3 outlets");
        machine.reInitializeCoffeeMachine();
        CoffeeMakerFunctionalTests.testForNBeveragesInParallel(3, firstTestBeverages);
        System.out.println("Successfully made 3 beverages with with 3 outlets in parallel\n");


        System.out.println("Test for 4 beverages in parallel with only 2 outlets with not enough Ingredients quantity");
        machine.reInitializeCoffeeMachine();
        CoffeeMakerFunctionalTests.testForNBeveragesInParallel(2, secondTestBeverages);
        System.out.println("Successfully tried to make 4 beverages in parallel with only 2 outlets but not enough ingredient available \n");


        System.out.println("Test for 4 beverages in parallel with only 2 outlets with enough Ingredient Quantity");
        machine.reInitializeCoffeeMachine();
        machine.addIngredient("sugar_syrup", 100);
        machine.addIngredient("hot_milk", 400);
        machine.addIngredient("hot_water", 200);
        machine.addIngredient("tea_leaves_syrup", 50);
        CoffeeMakerFunctionalTests.testForNBeveragesInParallel(2, secondTestBeverages);
        System.out.println("Successfully made 4 beverages in parallel with only 2 outlets\n");


        System.out.println("Test for beverage : hot_chocolate which does not have ingredients available");
        machine.reInitializeCoffeeMachine();
        CoffeeMakerFunctionalTests.testForNBeveragesInParallel(1, new ArrayList<>(Arrays.asList("hot_chocolate")));
        System.out.println("Successfully aborted making beverage : hot_chocolate with appropriate message\n");


        System.out.println("Test for beverage : hot_chocolate after adding ingredient");
        machine.reInitializeCoffeeMachine();
        CoffeeMakerFunctionalTests.testForNBeveragesInParallel(1, new ArrayList<>(Arrays.asList("hot_chocolate")));
        machine.addIngredient("chocolate_powder", 50);
        System.out.println("Added ingredient : chocolate_powder");
        CoffeeMakerFunctionalTests.testForNBeveragesInParallel(1, new ArrayList<>(Arrays.asList("hot_chocolate")));
        System.out.println("Successfully made beverage : hot_chocolate after adding ingredient");

    }
}

