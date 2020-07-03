import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Machine {

    private static Machine machine = null;
    private ConcurrentHashMap<String, Integer> ingredientsMap;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> reciepesMap;

    // private constructor restricted to this class itself
    private Machine() {
        // Called only first time when coffee Machine is started
        initializeCoffeeMachine();
    }

    private void initializeCoffeeMachine() {
        initializeIngredients();
        initializeReciepes();
    }

    // Initialize when Coffee Machine is started for the first time after the we can add new ingredient directly without
    // need of reinitialization
    // Reading values from resource ingredients.json time of initialization
    private void initializeIngredients() {
        ingredientsMap = new ConcurrentHashMap<>();

        ObjectMapper mapper = new ObjectMapper();
        ingredientsMap = new ConcurrentHashMap<>();
        try {
            ingredientsMap = mapper.readValue(new File(
                    "./src/main/resources/ingredients.json"), new TypeReference<ConcurrentHashMap<String, Integer>>() {
            });
        } catch (IOException e)
        {
            System.out.println(e.getMessage() + " ::: " + e.getStackTrace().toString());
        }
    }

    // Initialization Recipes when Coffee machine is started first time or Reinitialize recipes when new recipes are added.
    // Reading recipes from Resource recipes.json
    private void initializeReciepes() {
        ObjectMapper mapper = new ObjectMapper();
        reciepesMap = new ConcurrentHashMap<>();
        try {
            reciepesMap = mapper.readValue(new File(
                    "./src/main/resources/recipes.json"), new TypeReference<ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>>() {
            });
        } catch (IOException e)
        {
            System.out.println(e.getMessage() + " ::: " + e.getStackTrace().toString());
        }
    }

    // static method to create instance of Singleton class Machine
    public static Machine getInstance()
    {
        if (machine == null)
            machine = new Machine();

        return machine;
    }

    // Main function which as used while preparing any beverage to check the available quantities
    public void prepareBeverage(String beverage) {
        ConcurrentHashMap<String, Integer> beverageRecipe = reciepesMap.getOrDefault(beverage, null);
        if( beverageRecipe == null ) {
            System.out.println("Recipe for beverage : " + beverage + " is not available...");
            return;
        }

        synchronized (ingredientsMap) {

            // First checking if all ingredients are available or not for beverage
            // If any ingredients are missing return from here
            for (Map.Entry<String, Integer> entry : beverageRecipe.entrySet()) {
                String ingredient = entry.getKey();
                int ingredientQuantity = entry.getValue();
                int availableQuantiy = ingredientsMap.getOrDefault(ingredient, 0);
                if (availableQuantiy == 0) {
                    System.out.println("Ingredient : " + ingredient + " is not availabe for Beverage : " + beverage);
                    return;
                } else if (availableQuantiy < ingredientQuantity) {
                    System.out.println("Beverage : " + beverage + " count not be prepared as not enough of ingredient : " + ingredient + " is available");
                    return;
                }
            }

            // If it reaches here means all ingredients are available and Beverage can be made
            // Make beverage and update ingredients quantity used
            for (Map.Entry<String, Integer> entry : beverageRecipe.entrySet()) {
                String ingredient = entry.getKey();
                int ingredientQuantity = entry.getValue();

                int availableQuantiy = ingredientsMap.getOrDefault(ingredient, 0);

                ingredientsMap.put(ingredient, availableQuantiy - ingredientQuantity);
            }
        }
        System.out.println("Beverage : " + beverage + " prepared successfully!");

    }

    // Reinitializing CoffeeMachine when new recipes are added or we are resetting restocking ingredients quantities.
    // We can just update new Recipes in recipe.json or update ingredients.json and reinitialize CoffeeMachine
    public void reInitializeCoffeeMachine() {
        initializeCoffeeMachine();
    }

    // Add new ingredients or ingredients quantity while machine is on without restarting it.
    public void addIngredient(String ingredientName, int quantity) {
        ingredientsMap.put(ingredientName, ingredientsMap.getOrDefault(ingredientName, 0) + quantity);
    }

}
