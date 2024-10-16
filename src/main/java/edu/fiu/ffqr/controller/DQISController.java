package edu.fiu.ffqr.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Math;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.fiu.ffqr.models.DQISCategory;
import edu.fiu.ffqr.models.FoodCategoryRecommendation;
import edu.fiu.ffqr.models.FoodItem;

import edu.fiu.ffqr.models.FoodItemInput;
import edu.fiu.ffqr.models.DQIS;
import edu.fiu.ffqr.models.FoodRecommendationRange;
import edu.fiu.ffqr.models.FoodType;
import edu.fiu.ffqr.models.Result;
import edu.fiu.ffqr.models.SysFoodRecommendation;
import edu.fiu.ffqr.service.FFQFoodEntryService;
import edu.fiu.ffqr.service.ResultsService;
import edu.fiu.ffqr.service.SysFoodItemRecommendationService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/DQIS")
public class DQISController {

    // Autowired will change depending on needs
    @Autowired
    private SysFoodItemRecommendationService SysFoodItemRecomService;

    @Autowired
    private ResultsService resultsService;

    @Autowired
    private FFQFoodEntryService foodItemService;

    // Empty Constructor
    public DQISController() {
    }

    @GetMapping("/calculate/{questionnaireID}")
    public DQIS calculateDQIS(@PathVariable("questionnaireID") String questionnaireID) throws Exception {
        String foodItemName = "";
        String nutrientListID = "";
        String portionSize = "";
        String category = "";
        int infantAge = 0;
        String gender = "";
        String ageRange = "";
        double totalPoints = 0.0;
        Double calculatedAmount = 0.0;
        boolean breastMilkFlag = false; // set breastMilkFlag, if baby is taking breast milk, true
        boolean breastMilkCheck = false;
        boolean proteinFlag = false;
        boolean wGrainsFlag = false;
        boolean rGrainsFlag = false;
        boolean vegetableFlag = false;
        boolean fruitsFlag = false;
        boolean juicesFlag = false;
        boolean sBeveragesFlag = false;
        boolean sweetsFlag = false;
        boolean snacksFlag = false;
        boolean otherMilkFlag = false;
        boolean enteredPartial = false;
        boolean enteredOther = false;
        boolean enteredBreast = false;
        Double formulaMilkAmount = 0.0;
        boolean labelAdequate = false; // if the baby is having both breastmilk & formula but amount is still equal to
                                       // recommmendation, then true
        Map<String, Double> categoryValueMap = new HashMap<String, Double>();
        boolean exclusivelyBreastfed = true; // boolean for if baby is exclusively breastfed. if another fooditem is
                                             // passed as having a value it will turn false

        // get results for given questionnaire
        Result result = resultsService.getResultByQuestionnaireID(questionnaireID);

        infantAge = result.getAgeInMonths();
        gender = result.getGender();
        ArrayList<FoodItemInput> userChoices = result.getUserChoices();

        DQIS dqis = new DQIS();
        dqis.setQuestionnaireId(questionnaireID);
        dqis.setPatientAgeInMonths(infantAge);

        List<SysFoodRecommendation> SysFoodItemRecommendations = SysFoodItemRecomService.getAll();

        // populate the category-value map with the food item categories
        for (SysFoodRecommendation sysFoodItemRecommendation : SysFoodItemRecommendations) {
            categoryValueMap.put(sysFoodItemRecommendation.getCategoryName(), 0.0);
        }

        for (FoodItemInput foodItem : userChoices) {

            foodItemName = foodItem.getName();
            nutrientListID = foodItem.getNutrientListID();
            portionSize = foodItem.getServing();

            // find the food item
            FoodItem f = foodItemService.getEntryWithName(foodItemName);

            // find the food item type category
            for (FoodType foodType : f.getFoodTypes()) {
                if (foodType.getNutrientListID().equalsIgnoreCase(nutrientListID)) {
                    category = foodType.getCategory();
                }
            }

            for (SysFoodRecommendation sysFoodItemRecommendation : SysFoodItemRecommendations) {

                String categoryName = sysFoodItemRecommendation.getCategoryName();

                if (category.equalsIgnoreCase(categoryName)) {
                    double currentTotal = 0.0;
                    if (foodItem.getServing() == null) {
                        if (nutrientListID.equalsIgnoreCase("form")) {
                            otherMilkFlag = true;
                        }
                        if (nutrientListID.equalsIgnoreCase("milkcow")) {
                            otherMilkFlag = true;
                        }
                        if (nutrientListID.equalsIgnoreCase("milkchoc")) {
                            otherMilkFlag = true;
                        }
                        if (nutrientListID.equalsIgnoreCase("milkothe")) {
                            otherMilkFlag = true;
                        }
                        if (nutrientListID.equalsIgnoreCase("pds")) {
                            otherMilkFlag = true;
                        }
                        if (nutrientListID.equalsIgnoreCase("chee")) {

                            currentTotal = (25.2 * foodItem.getFrequency() / 28.35);

                            if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
                                currentTotal = currentTotal / 7;
                            }
                            exclusivelyBreastfed = false;
                            categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) +
                                    currentTotal);
                        } else if (nutrientListID.equalsIgnoreCase("yogu")) {

                            currentTotal = (113.4 * foodItem.getFrequency() / 28.35);

                            if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
                                currentTotal = currentTotal / 7;
                            }
                            exclusivelyBreastfed = false;
                            categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) +
                                    currentTotal);
                        } else if (nutrientListID.equalsIgnoreCase("soyp")) {

                            currentTotal = (28.4 * foodItem.getFrequency() / 28.35);

                            if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
                                currentTotal = currentTotal / 7;
                            }
                            exclusivelyBreastfed = false;
                            categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) +
                                    currentTotal);
                        } else if (nutrientListID.equalsIgnoreCase("icec")) {

                            currentTotal = (29.5 * foodItem.getFrequency()
                                    * parseServingAmount(foodItem.getServing()) / 28.35); // 1 ice cream
                                                                                                        // serving =
                                                                                                        // 29.5 grams,
                                                                                                        // defined by PO

                            if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
                                currentTotal = currentTotal / 7;
                            }
                            exclusivelyBreastfed = false;
                            categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) +
                                    currentTotal);
                        } else if (nutrientListID.equalsIgnoreCase("brea")) {
                            breastMilkFlag = true;
                            currentTotal = foodItem.getFrequency() * 3;
                            if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
                                currentTotal /= 7;
                            }
                            formulaMilkAmount = currentTotal; // temporarily save bresatmilk amount here
                            categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) +
                                    currentTotal);
                        } else {
                            currentTotal = foodItem.getFrequency();
                            if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
                                currentTotal = currentTotal / 7;
                            }
                            exclusivelyBreastfed = false;
                            categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) +
                                    currentTotal);
                        }
                    } else {
                        if (nutrientListID.equalsIgnoreCase("form")) {
                            otherMilkFlag = true;
                        }
                        if (nutrientListID.equalsIgnoreCase("milkcow")) {
                            otherMilkFlag = true;
                        }
                        if (nutrientListID.equalsIgnoreCase("milkchoc")) {
                            otherMilkFlag = true;
                        }
                        if (nutrientListID.equalsIgnoreCase("milkothe")) {
                            otherMilkFlag = true;
                        }
                        if (nutrientListID.equalsIgnoreCase("pds")) {
                            otherMilkFlag = true;
                        }
                        if (nutrientListID.equalsIgnoreCase("pancrefi")) {

                            currentTotal = (45.8 * foodItem.getFrequency()
                                    * parseServingAmount(foodItem.getServing()) / 28.35); // 1 refined
                                                                                                        // pancake =
                                                                                                        // 45.8 grams,
                                                                                                        // defined by PO

                            if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
                                currentTotal = currentTotal / 7;
                            }

                            exclusivelyBreastfed = false;
                            categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) +
                                    currentTotal);
                        } else if (nutrientListID.equalsIgnoreCase("pancwhol")) {

                            currentTotal = (49.7 * foodItem.getFrequency()
                                    * parseServingAmount(foodItem.getServing()) / 28.35); // 1 whole
                                                                                                        // pancake =
                                                                                                        // 49.7 grams,
                                                                                                        // defined by PO

                            if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
                                currentTotal = currentTotal / 7;
                            }
                            exclusivelyBreastfed = false;
                            categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) +
                                    currentTotal);
                        } else if (nutrientListID.equalsIgnoreCase("hone")) {

                            currentTotal = (0.5 * foodItem.getFrequency()
                                    * parseServingAmount(foodItem.getServing())); //

                            if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
                                currentTotal = currentTotal / 7;
                            }
                            exclusivelyBreastfed = false;
                            categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) +
                                    currentTotal);
                        } else if (nutrientListID.equalsIgnoreCase("cook")) {

                            currentTotal = (10.8 * foodItem.getFrequency()
                                    * parseServingAmount(foodItem.getServing()) / 28.35); //

                            if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
                                currentTotal = currentTotal / 7;
                            }
                            exclusivelyBreastfed = false;
                            categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) +
                                    currentTotal);
                        } else {
                            currentTotal = (foodItem.getFrequency()
                                    * parseServingAmount(foodItem.getServing())); //

                            if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
                                currentTotal = currentTotal / 7;
                            }
                            exclusivelyBreastfed = false;
                            categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) +
                                    currentTotal);
                        }
                    }
                } // end of main if
            } // end of loop
        }
        for (SysFoodRecommendation sysFoodItemRecommendation : SysFoodItemRecommendations) {
            String categoryName = sysFoodItemRecommendation.getCategoryName();

            DQISCategory foodItemRec = new DQISCategory();
            foodItemRec.setCategoryName(sysFoodItemRecommendation.getCategoryName());
            foodItemRec.setLabel("");
            calculatedAmount = categoryValueMap.get(sysFoodItemRecommendation.getCategoryName());
            // 2/20/2022 WENJIA update
            // SET breastMilkFlag
            // if the baby is taking breast milk, then the calculated amount should be the
            // standard amount
            // 4/18/2022 wenjia update
            // add new logic: if a baby is having formula more than the maximum of
            // standdard, ex. 6 month baby is having formula > 28.9
            // then the total number of milk = formula + breastmilk + cow milk
            double recommendAmount = setCalculatedAmountForBreastMilk(infantAge);
            formulaMilkAmount = calculatedAmount - formulaMilkAmount;

            if (breastMilkFlag &&
                    sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase(
                            "Breastmilk/Formula/Cows Milk/Other milks")
                    && formulaMilkAmount <= recommendAmount) {
                foodItemRec.setCalculatedAmount(recommendAmount);
                labelAdequate = true;
            } else {
                foodItemRec.setCalculatedAmount(calculatedAmount);
            }
            if (infantAge > 0 && infantAge < 6) {
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Breastmilk/Formula/Cows Milk/Other milks")) {
                    if (!nutrientListID.equalsIgnoreCase("brea")) {
                        otherMilkFlag = true;
                    }
                    if (otherMilkFlag && breastMilkFlag && !enteredPartial) { // partial
                        enteredPartial = true;
                            foodItemRec.setCalculatedAmount(15.0);
                            totalPoints += 15.0;
                        }
                    else if (otherMilkFlag && !breastMilkFlag && !enteredOther) { // other
                        enteredOther = true;
                            foodItemRec.setCalculatedAmount(5.0);
                            totalPoints += 5.0;
                        }
                    else if (exclusivelyBreastfed && !otherMilkFlag && !enteredPartial) { // breast
                        foodItemRec.setCalculatedAmount(15.0);
                        totalPoints += 15.0;
                        enteredBreast = true;
                    }
                }
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Whole Grains")) {
                    wGrainsFlag = true;
                    if ((foodItemRec.getCalculatedAmount() == 0.00)) {
                        foodItemRec.setCalculatedAmount(5.0);
                        totalPoints += 5.0;
                    }
                    else{
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Refined Grains")) {
                    rGrainsFlag = true;
                    if ((foodItemRec.getCalculatedAmount() == 0.00)) {
                        foodItemRec.setCalculatedAmount(5.0);
                        totalPoints += 5.0;
                    }
                    else{
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
                // Proteins
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Proteins")) {
                    proteinFlag = true;
                    if ((foodItemRec.getCalculatedAmount() == 0.00)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    }
                    else{
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
                // Vegetables
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Vegetables")) {
                    fruitsFlag = true;
                    if ((foodItemRec.getCalculatedAmount() == 0.00)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    }
                    else{
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
                // Fruits
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Fruits")) {
                    fruitsFlag = true;
                    if ((foodItemRec.getCalculatedAmount() == 0.00)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    }
                    else{
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
                // Juices
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("100% Juices")) {
                    juicesFlag = true;
                    if ((foodItemRec.getCalculatedAmount() == 0.00)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    }
                    else{
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
                // Sugary Bevs
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Sugary Beverages")) {
                    sBeveragesFlag = true;
                    if ((foodItemRec.getCalculatedAmount() == 0.00)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    }
                    else{
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
                // Sweets
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Sweets")) {
                    sweetsFlag = true;
                    if ((foodItemRec.getCalculatedAmount() == 0.00)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    }
                    else{
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
                // Salty Snacks
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Salty Snacks")) {
                    snacksFlag = true;
                    if ((foodItemRec.getCalculatedAmount() == 0.00)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    }
                    else{
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
            }
            if (infantAge > 5 && infantAge < 12) {
                if (sysFoodItemRecommendation.getCategoryName()
                        .equalsIgnoreCase("Breastmilk/Formula/Cows Milk/Other milks")) {
                    if (nutrientListID.equalsIgnoreCase("form")) {
                        otherMilkFlag = true;
                    }
                    if (nutrientListID.equalsIgnoreCase("milkcow")) {
                        otherMilkFlag = true;
                    }
                    if (nutrientListID.equalsIgnoreCase("milkchoc")) {
                        otherMilkFlag = true;
                    }
                    if (nutrientListID.equalsIgnoreCase("milkothe")) {
                        otherMilkFlag = true;
                    }
                    if (nutrientListID.equalsIgnoreCase("pds")) {
                        otherMilkFlag = true;
                    }
                    if (otherMilkFlag && breastMilkFlag && !enteredPartial) { // partial
                        enteredPartial = true;
                        if (foodItemRec.getCalculatedAmount() > 0.00 && foodItemRec.getCalculatedAmount() <= 28.0) {
                            foodItemRec.setCalculatedAmount(10.0);
                        } else if (foodItemRec.getCalculatedAmount() > 28.0
                                && foodItemRec.getCalculatedAmount() <= 35.0) {
                            foodItemRec.setCalculatedAmount(7.5);
                        } else if (foodItemRec.getCalculatedAmount() == 0.00 || foodItemRec.getCalculatedAmount() > 35.0) {
                            foodItemRec.setCalculatedAmount(5.0);
                        }
                    } else if (otherMilkFlag && !breastMilkFlag && !enteredOther) { // other
                        enteredOther = true;
                        if (foodItemRec.getCalculatedAmount() > 0.00 && foodItemRec.getCalculatedAmount() <= 28.0) {
                            foodItemRec.setCalculatedAmount(5.00);
                        } else if (foodItemRec.getCalculatedAmount() > 28.0
                                && foodItemRec.getCalculatedAmount() <= 35.0) {
                            foodItemRec.setCalculatedAmount(2.5);
                        } else if (foodItemRec.getCalculatedAmount() == 0.00 || foodItemRec.getCalculatedAmount() > 35.0) {
                            foodItemRec.setCalculatedAmount(0.0);
                        }
                    } else if (breastMilkFlag && !otherMilkFlag && !enteredPartial) { // breast
                        foodItemRec.setCalculatedAmount(15.0);
                        enteredBreast = true;
                    }
                }
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Whole Grains")) {
                    wGrainsFlag = true;
                    if ((foodItemRec.getCalculatedAmount() >= 0.1 && foodItemRec.getCalculatedAmount() <= 3.5)) {
                        foodItemRec.setCalculatedAmount(2.5);
                    } else if ((foodItemRec.getCalculatedAmount() >= 3.6 && foodItemRec.getCalculatedAmount() <= 8.0)) {
                        foodItemRec.setCalculatedAmount(1.25);
                    } else if ((foodItemRec.getCalculatedAmount() == 0.00 || foodItemRec.getCalculatedAmount() > 8.0)) {
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Refined Grains")) {
                    wGrainsFlag = true;
                    if ((foodItemRec.getCalculatedAmount() >= 0.00 && foodItemRec.getCalculatedAmount() <= 1.5)) {
                        foodItemRec.setCalculatedAmount(2.5);
                    } else if ((foodItemRec.getCalculatedAmount() >= 1.6 && foodItemRec.getCalculatedAmount() <= 3.5)) {
                        foodItemRec.setCalculatedAmount(1.25);
                    } else if (foodItemRec.getCalculatedAmount() > 3.5) {
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }

                // Proteins
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Proteins")) {
                    proteinFlag = true;
                    if ((foodItemRec.getCalculatedAmount() > 0.1 && foodItemRec.getCalculatedAmount() <= 6.0)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    } else if ((foodItemRec.getCalculatedAmount() > 6.0 && foodItemRec.getCalculatedAmount() <= 10.0)) {
                        foodItemRec.setCalculatedAmount(2.5);
                    } else if ((foodItemRec.getCalculatedAmount() == 0.00 || foodItemRec.getCalculatedAmount() > 10.0)) {
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
                // Vegetables
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Vegetables")) {
                    fruitsFlag = true;
                    if ((foodItemRec.getCalculatedAmount() >= 2.0)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    } else if ((foodItemRec.getCalculatedAmount() > 0.0 &&
                            foodItemRec.getCalculatedAmount() < 2.0)) {
                        foodItemRec.setCalculatedAmount(2.5);
                    } else if ((foodItemRec.getCalculatedAmount() == 0.00)) {
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
                // Fruits
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Fruits")) {
                    fruitsFlag = true;
                    if ((foodItemRec.getCalculatedAmount() >= 2.0)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    } else if ((foodItemRec.getCalculatedAmount() > 0.00 &&
                            foodItemRec.getCalculatedAmount() < 2.0)) {
                        foodItemRec.setCalculatedAmount(2.5);
                    } else if ((foodItemRec.getCalculatedAmount() == 0.00)) {
                        foodItemRec.setCalculatedAmount(0.0);
                    }

                } // Juices
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("100% Juices")) {
                    juicesFlag = true;
                    if ((foodItemRec.getCalculatedAmount() == 0.00)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    } else if ((foodItemRec.getCalculatedAmount() > 0.00 &&
                            foodItemRec.getCalculatedAmount() <= 6.0)) {
                        foodItemRec.setCalculatedAmount(2.5);
                    } else if ((foodItemRec.getCalculatedAmount() > 6.0)) {
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
                // Sugary Bevs
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Sugary Beverages")) {
                    sBeveragesFlag = true;
                    if ((foodItemRec.getCalculatedAmount() == 0.00)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    } else if ((foodItemRec.getCalculatedAmount() > 0.00 &&
                            foodItemRec.getCalculatedAmount() <= 4.0)) {
                        foodItemRec.setCalculatedAmount(2.5);
                    } else if ((foodItemRec.getCalculatedAmount() > 4.0)) {
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
                // Sweets
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Sweets")) {
                    sweetsFlag = true;
                    if ((foodItemRec.getCalculatedAmount() == 0.00)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    } else if ((foodItemRec.getCalculatedAmount() > 0.00 && foodItemRec.getCalculatedAmount() <= 1.0)) {
                        foodItemRec.setCalculatedAmount(2.5);
                    } else if ((foodItemRec.getCalculatedAmount() > 1.0)) {
                        foodItemRec.setCalculatedAmount(0.0);

                    }
                }
                // Salty Snacks
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Salty Snacks")) {
                    snacksFlag = true;
                    if ((foodItemRec.getCalculatedAmount() == 0.00)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    } else if ((foodItemRec.getCalculatedAmount() > 0.00 &&
                            foodItemRec.getCalculatedAmount() <= 1.0)) {
                        foodItemRec.setCalculatedAmount(2.5);
                    } else if ((foodItemRec.getCalculatedAmount() > 1.0)) {
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
            }
            //System.out.print(nutrientListID + " ");
            if (infantAge >= 12 && infantAge <= 36) {
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Breastmilk/Formula/Cows Milk/Other milks")) {
                    if (otherMilkFlag && breastMilkFlag && !enteredPartial){ // partial
                        if (foodItemRec.getCalculatedAmount() > 0.00 && foodItemRec.getCalculatedAmount() <= 18.0) {
                            foodItemRec.setCalculatedAmount(10.0);
                        } else if (foodItemRec.getCalculatedAmount() > 18.0
                                && foodItemRec.getCalculatedAmount() <= 24.0) {
                            foodItemRec.setCalculatedAmount(7.5);
                        } else if (foodItemRec.getCalculatedAmount() == 0.00 || foodItemRec.getCalculatedAmount() > 24.0) {
                            foodItemRec.setCalculatedAmount(5.0);
                        }
                    } else if (otherMilkFlag && !breastMilkFlag && !enteredOther) { // other
                        enteredOther = true;
                        if (foodItemRec.getCalculatedAmount() > 0.00 && foodItemRec.getCalculatedAmount() <= 18.0) {
                            foodItemRec.setCalculatedAmount(5.00);
                        } else if (foodItemRec.getCalculatedAmount() > 18.0
                                && foodItemRec.getCalculatedAmount() <= 24.0) {
                            foodItemRec.setCalculatedAmount(2.5);
                        } else if (foodItemRec.getCalculatedAmount() == 0.00 || foodItemRec.getCalculatedAmount() > 24.0) {
                            foodItemRec.setCalculatedAmount(0.0);
                        }
                    } else if (breastMilkFlag && !otherMilkFlag && !enteredPartial) { // breast
                        
                        foodItemRec.setCalculatedAmount(15.0);
                        enteredBreast = true;
                    }
                }
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Whole Grains")) {
                    wGrainsFlag = true;
                    if ((foodItemRec.getCalculatedAmount() >= 0.1 && foodItemRec.getCalculatedAmount() <= 5.5)) {
                        foodItemRec.setCalculatedAmount(2.5);
                    } else if ((foodItemRec.getCalculatedAmount() >= 5.6 && foodItemRec.getCalculatedAmount() <= 8.0)) {
                        foodItemRec.setCalculatedAmount(1.25);
                    } else if ((foodItemRec.getCalculatedAmount() == 0.00 || foodItemRec.getCalculatedAmount() > 8.0)) {
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Refined Grains")) {
                    wGrainsFlag = true;
                    if ((foodItemRec.getCalculatedAmount() >= 0.00 && foodItemRec.getCalculatedAmount() <= 1.80)) {
                        foodItemRec.setCalculatedAmount(2.5);
                    } else if ((foodItemRec.getCalculatedAmount() >= 1.90 && foodItemRec.getCalculatedAmount() <= 4.2)) {
                        foodItemRec.setCalculatedAmount(1.25);
                    } else if ((foodItemRec.getCalculatedAmount() > 4.2)) {
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
                // Proteins
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Proteins")) {
                    proteinFlag = true;
                    if ((foodItemRec.getCalculatedAmount() >= 0.1 && foodItemRec.getCalculatedAmount() <= 3.0)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    } else if ((foodItemRec.getCalculatedAmount() >= 3.1 && foodItemRec.getCalculatedAmount() <= 6.0)) {
                        foodItemRec.setCalculatedAmount(2.5);
                    } else if ((foodItemRec.getCalculatedAmount() == 0.00 || foodItemRec.getCalculatedAmount() > 6.0)) {
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
                // Vegetables
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Vegetables")) {
                    fruitsFlag = true;
                    if ((foodItemRec.getCalculatedAmount() >= 8.00)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    } else if ((foodItemRec.getCalculatedAmount() > 0.0 &&
                            foodItemRec.getCalculatedAmount() < 8.0)) {
                        foodItemRec.setCalculatedAmount(2.5);
                    } else if ((foodItemRec.getCalculatedAmount() == 0.00)) {
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
                // Fruits
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Fruits")) {
                    fruitsFlag = true;
                    if ((foodItemRec.getCalculatedAmount() >= 8.0)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    } else if ((foodItemRec.getCalculatedAmount() > 0.00 &&
                            foodItemRec.getCalculatedAmount() < 8.0)) {
                        foodItemRec.setCalculatedAmount(2.5);
                    } else if ((foodItemRec.getCalculatedAmount() == 0.00)) {
                        foodItemRec.setCalculatedAmount(0.0);
                    }

                } // Juices
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("100% Juices")) {
                    juicesFlag = true;
                    if ((foodItemRec.getCalculatedAmount() >= 0.00 && foodItemRec.getCalculatedAmount() <= 4.0)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    } else if ((foodItemRec.getCalculatedAmount() > 4.0 &&
                            foodItemRec.getCalculatedAmount() <= 6.0)) {
                        foodItemRec.setCalculatedAmount(2.5);
                    } else if ((foodItemRec.getCalculatedAmount() > 6.0)) {
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
                // Sugary Bevs
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Sugary Beverages")) {
                    sBeveragesFlag = true;
                    if ((foodItemRec.getCalculatedAmount() == 0.00)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    } else if ((foodItemRec.getCalculatedAmount() > 0.00 &&
                            foodItemRec.getCalculatedAmount() <= 4.0)) {
                        foodItemRec.setCalculatedAmount(2.5);
                    } else if ((foodItemRec.getCalculatedAmount() > 4.0)) {
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
                // Sweets
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Sweets")) {
                    sweetsFlag = true;
                    if ((foodItemRec.getCalculatedAmount() == 0.00)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    }
                    else if ((foodItemRec.getCalculatedAmount() > 0.00 && foodItemRec.getCalculatedAmount() <= 1.0)) {
                        foodItemRec.setCalculatedAmount(2.5);

                    } else if ((foodItemRec.getCalculatedAmount() > 1.00)) {
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
                // Salty Snacks
                if (sysFoodItemRecommendation.getCategoryName().equalsIgnoreCase("Salty Snacks")) {
                    snacksFlag = true;
                    if ((foodItemRec.getCalculatedAmount() == 0.00)) {
                        foodItemRec.setCalculatedAmount(5.0);
                    } else if ((foodItemRec.getCalculatedAmount() > 0.00 &&
                            foodItemRec.getCalculatedAmount() <= 1.0)) {
                        foodItemRec.setCalculatedAmount(2.5);
                    } else if ((foodItemRec.getCalculatedAmount() > 1.0)) {
                        foodItemRec.setCalculatedAmount(0.0);
                    }
                }
            }
            foodItemRec.setTotalPoints(totalPoints);
            if (infantAge >= 0 && infantAge <= 5) {
                ageRange = "0-5";
            } else if (infantAge >= 6 && infantAge <= 12) {
                ageRange = "6-12";
            } else if (infantAge >= 13 && infantAge <= 24) {
                ageRange = "13-24";
            } else
                throw new Exception("There are no recommendations available for infants of age over 24 months");

            List<FoodRecommendationRange> rangeList = sysFoodItemRecommendation.getRecommendationsByAge().get(ageRange);

            boolean notFound = true;
            double compareValue = Math.floor(foodItemRec.getCalculatedAmount() * 10) / 10.0;
            // compareValue is used to account for the grey areas in the payload. it rounds
            // down the calculated amount to 1 decimal place
            // so all food categories will get a proper label
            for (FoodRecommendationRange range : rangeList) {
                if (compareValue >= range.getFrom() && compareValue <= range.getTo() && notFound) {
                    // if statement checks first to see if exclusively breastfed is true. if so, it
                    // will manually make the label 'adequate'
                    // since babies that are exclusively breastfed are always getting adequate milk
                    // according to the PO
                    if (exclusivelyBreastfed && (sysFoodItemRecommendation.getCategoryName()
                            .equalsIgnoreCase("Breastmilk/Formula/Cows Milk/Other milks")) || labelAdequate) {
                        foodItemRec.setLabel("Adequate");
                    } else {
                        foodItemRec.setLabel(range.getLabel());
                    }
                    foodItemRec.setRangeFrom(range.getFrom());
                    foodItemRec.setRangeTo(range.getTo());
                    notFound = false;
                }
            }
            dqis.getFoodCategoryRecList().add(foodItemRec);
        }
        return dqis;

    }

    private static double parseServingAmount(String serving) {
        String[] parts = serving.split(" ");
        String amountStr = parts[0];
        
        // Handle fractions
        if (amountStr.contains("/")) {
            String[] fractionParts = amountStr.split("/");
            return Double.parseDouble(fractionParts[0]) / Double.parseDouble(fractionParts[1]);
        }
        
        // Handle special characters
        switch (amountStr) {
            case "¼":
                return 0.25;
            case "½":
                return 0.5;
            case "¾":
                return 0.75;
            default:
                return Double.parseDouble(amountStr);
        }
    }

    public static double setCalculatedAmountForBreastMilk(int babyAgeInMonth) {
        double oneMonthInfantBreastMilkVolume = 21.1;
        double twoMonthInfantBreastMilkVolume = 23.8;
        double threeMonthInfantBreastMilkVolume = 24.9;
        double fourMonthInfantBreastMilkVolume = 25.1;
        double fiveMonthInfantBreastMilkVolume = 25.9;
        double sixMonthInfantBreastMilkVolume = 24.7;
        double sevenMonthInfantBreastMilkVolume = 24.1;
        double eightMonthInfantBreastMilkVolume = 23.5;
        double nineMonthInfantBreastMilkVolume = 22.7;
        double tenMonthInfantBreastMilkVolume = 21.9;
        double elevenMonthInfantBreastMilkVolume = 21.0;
        double twelveMonthInfantBreastMilkVolume = 20.1;
        double thirteenMonthInfantBreastMilkVolume = 19.0;
        double fourteenMonthInfantBreastMilkVolume = 18.0;
        double fifteenMonthInfantBreastMilkVolume = 17.0;
        double sixteenMonthInfantBreastMilkVolume = 15.9;
        double seveteenMonthInfantBreastMilkVolume = 14.8;
        double eighteenMonthInfantBreastMilkVolume = 13.7;
        double nineteenMonthInfantBreastMilkVolume = 12.5;
        double twentyMonthInfantBreastMilkVolume = 11.4;
        double twentyoneMonthInfantBreastMilkVolume = 10.2;
        double twentytwoMonthInfantBreastMilkVolume = 9.0;
        double twentythreeMonthInfantBreastMilkVolume = 7.8;
        double twentyfourMonthInfantBreastMilkVolume = 6.6;
        // static final double ouncesToMilliliter = 29.5735;
        double finalAmount = 0.0;
        switch (babyAgeInMonth) {
            case 1:
                finalAmount = oneMonthInfantBreastMilkVolume;
                break;
            case 2:
                finalAmount = twoMonthInfantBreastMilkVolume;
                break;
            case 3:
                finalAmount = threeMonthInfantBreastMilkVolume;
                break;
            case 4:
                finalAmount = fourMonthInfantBreastMilkVolume;
                break;
            case 5:
                finalAmount = fiveMonthInfantBreastMilkVolume;
                break;
            case 6:
                finalAmount = sixMonthInfantBreastMilkVolume;
                break;
            case 7:
                finalAmount = sevenMonthInfantBreastMilkVolume;
                break;
            case 8:
                finalAmount = eightMonthInfantBreastMilkVolume;
                break;
            case 9:
                finalAmount = nineMonthInfantBreastMilkVolume;
                break;
            case 10:
                finalAmount = tenMonthInfantBreastMilkVolume;
                break;
            case 11:
                finalAmount = elevenMonthInfantBreastMilkVolume;
                break;
            case 12:
                finalAmount = twelveMonthInfantBreastMilkVolume;
                break;
            case 13:
                finalAmount = thirteenMonthInfantBreastMilkVolume;
                break;
            case 14:
                finalAmount = fourteenMonthInfantBreastMilkVolume;
                break;
            case 15:
                finalAmount = fifteenMonthInfantBreastMilkVolume;
                break;
            case 16:
                finalAmount = sixteenMonthInfantBreastMilkVolume;
                break;
            case 17:
                finalAmount = seveteenMonthInfantBreastMilkVolume;
                break;
            case 18:
                finalAmount = eighteenMonthInfantBreastMilkVolume;
                break;
            case 19:
                finalAmount = nineteenMonthInfantBreastMilkVolume;
                break;
            case 20:
                finalAmount = twentyMonthInfantBreastMilkVolume;
                break;
            case 21:
                finalAmount = twentyoneMonthInfantBreastMilkVolume;
                break;
            case 22:
                finalAmount = twentytwoMonthInfantBreastMilkVolume;
                break;
            case 23:
                finalAmount = twentythreeMonthInfantBreastMilkVolume;
                break;
            default:
                finalAmount = twentyfourMonthInfantBreastMilkVolume;
        }
        return finalAmount;
    }
}