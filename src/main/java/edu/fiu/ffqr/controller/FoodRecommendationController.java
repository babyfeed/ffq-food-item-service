package edu.fiu.ffqr.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.fiu.ffqr.models.FoodCategoryRecommendation;
import edu.fiu.ffqr.models.FoodItem;
import edu.fiu.ffqr.models.FoodItemInput;
import edu.fiu.ffqr.models.FoodRecommendation;
import edu.fiu.ffqr.models.FoodRecommendationRange;
import edu.fiu.ffqr.models.FoodType;
import edu.fiu.ffqr.models.Result;

import edu.fiu.ffqr.models.SysFoodRecommendation;
import edu.fiu.ffqr.service.FFQFoodEntryService;
import edu.fiu.ffqr.service.ResultsService;
import edu.fiu.ffqr.service.SysFoodItemRecommendationService;

/*
 * Author: Dariana Gonzalez
 * Created: 11/2019
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/foodrecommendations")
public class FoodRecommendationController {

	@Autowired
	private SysFoodItemRecommendationService SysFoodItemRecomService;
	
	@Autowired
	private FFQFoodEntryService foodItemService;
	
	@Autowired
	private ResultsService resultsService;
	
	public FoodRecommendationController() {}
	
	@PostMapping("/create")
	public SysFoodRecommendation createSysFoodRecommendation(@RequestBody SysFoodRecommendation sysFoodRec) throws JsonProcessingException {
		
		if(SysFoodItemRecomService.getSysNutrientRecommendationByNutrientName(sysFoodRec.getCategoryName()) != null)
		{
			throw new IllegalArgumentException("System Nutrient Recommendation for nutrient " + sysFoodRec.getCategoryName() + " already exists.");			
		}
		else
			return SysFoodItemRecomService.create(sysFoodRec);
	}
	
	@GetMapping("/calculate/{questionnaireID}")
	  public FoodRecommendation calculateFoodRecommendations(@PathVariable("questionnaireID") String questionnaireID) throws Exception {
		
		String foodItemName = "";
		String nutrientListID = "";
		String category = "";
		int infantAge = 0;
		String gender = "";
		String ageRange = "";
		Double calculatedAmount = 0.0;		
		Map<String, Double> categoryValueMap = new HashMap<String, Double>();
		
		// get results for given questionnaire
		Result result = resultsService.getResultByQuestionnaireID(questionnaireID);
		
		infantAge = result.getAgeInMonths();
		gender = result.getGender();
		ArrayList<FoodItemInput> userChoices = result.getUserChoices();
		
		// instantiate Food Item Recommendation object
		FoodRecommendation foodItemRecommendation = new FoodRecommendation();
		
		foodItemRecommendation.setQuestionnaireId(questionnaireID);
		foodItemRecommendation.setPatientAgeInMonths(infantAge);
		foodItemRecommendation.setGender(gender);
			
		// get list of food items recommendations by age
		List<SysFoodRecommendation> SysFoodItemRecommendations = SysFoodItemRecomService.getAll();
		
		// populate the category-value map with the food item categories
		for (SysFoodRecommendation sysFoodItemRecommendation: SysFoodItemRecommendations) {
			categoryValueMap.put(sysFoodItemRecommendation.getCategoryName(), 0.0);
		}
		
		// iterate list of user choices for food items
		for (FoodItemInput foodItem: userChoices) {
		
			foodItemName = foodItem.getName();
			nutrientListID = foodItem.getNutrientListID();
			
			// find the food item
			FoodItem f = foodItemService.getEntryWithName(foodItemName);
			
			// find the food item type category
			for (FoodType foodType: f.getFoodTypes()) {
				if (foodType.getNutrientListID().equalsIgnoreCase(nutrientListID)) {
					category = foodType.getCategory();
				}
			}
			
			// calculate total amount for food items categories
			for (SysFoodRecommendation sysFoodItemRecommendation: SysFoodItemRecommendations) {
				
				String categoryName = sysFoodItemRecommendation.getCategoryName();
				
				if(category.equalsIgnoreCase(categoryName))
				{
					double currentTotal = 0.0;
					
					System.out.println(categoryValueMap.get(categoryName));
					
					if(foodItem.getServing() == null)
					{
						if(nutrientListID.equalsIgnoreCase("chee")) {
							
							currentTotal = (25.2 * foodItem.getFrequency() / 28.35);
							
							if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
								currentTotal = currentTotal / 7;
							}
							
							categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) + currentTotal);
						}
						else if(nutrientListID.equalsIgnoreCase("yogu")) {
							
							currentTotal = (113.4 * foodItem.getFrequency() / 28.35);
							
							if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
								currentTotal = currentTotal / 7;
							}
							
							categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) + currentTotal);
						}
						else if(nutrientListID.equalsIgnoreCase("soyp")) {
														
							currentTotal = (28.4 * foodItem.getFrequency() / 28.35);
							
							if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
								currentTotal = currentTotal / 7;
							}
							
							categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) + currentTotal);
						}
						else if(nutrientListID.equalsIgnoreCase("icec")) {
							
							currentTotal = (29.5 * foodItem.getFrequency() * Double.parseDouble(foodItem.getServing().split(" ")[0]) / 28.35); // 1 ice cream serving = 29.5 grams, defined by PO
							
							if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
								currentTotal = currentTotal / 7;
							}
							
							categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) + currentTotal);
						}
						else {
							currentTotal = foodItem.getFrequency();
							
							if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
								currentTotal = currentTotal / 7;
							}
							
							categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) + currentTotal);
						}												
					}
					else 
					{
					if(nutrientListID.equalsIgnoreCase("pancrefi")) {
							
							currentTotal = (45.8 * foodItem.getFrequency() * Double.parseDouble(foodItem.getServing().split(" ")[0]) / 28.35); // 1 refined pancake = 45.8 grams, defined by PO
							
							if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
								currentTotal = currentTotal / 7;
							}
							
							categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) + currentTotal);
					}
					else if(nutrientListID.equalsIgnoreCase("pancwhol")) {
							
							currentTotal = (49.7 * foodItem.getFrequency() * Double.parseDouble(foodItem.getServing().split(" ")[0]) / 28.35); // 1 whole pancake = 49.7 grams, defined by PO
							
							if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
								currentTotal = currentTotal / 7;
							}
							
							categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) + currentTotal);
					}
					else if(nutrientListID.equalsIgnoreCase("hone")) {
						
						currentTotal = (0.5 * foodItem.getFrequency() * Double.parseDouble(foodItem.getServing().split(" ")[0])); // 
						
						if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
							currentTotal = currentTotal / 7;
						}
						
						categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) + currentTotal);
					}
					else if(nutrientListID.equalsIgnoreCase("cook")) {
						
						currentTotal = (10.8 * foodItem.getFrequency() * Double.parseDouble(foodItem.getServing().split(" ")[0]) / 28.35); // 
						
						if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
							currentTotal = currentTotal / 7;
						}
						
						categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) + currentTotal);
					}
					else
					{
						currentTotal = (foodItem.getFrequency() * Double.parseDouble(foodItem.getServing().split(" ")[0])); // 
						
						if (foodItem.getFrequencyType().equalsIgnoreCase("Week")) {
							currentTotal = currentTotal / 7;
						}
						
						categoryValueMap.replace(categoryName, categoryValueMap.get(categoryName) + currentTotal);
					}
					}
				}
			}
		}
		
		for (SysFoodRecommendation sysFoodItemRecommendation: SysFoodItemRecommendations) {
			
			FoodCategoryRecommendation foodItemRec = new FoodCategoryRecommendation();
			foodItemRec.setCategoryName(sysFoodItemRecommendation.getCategoryName());
			foodItemRec.setLabel("");
			calculatedAmount = categoryValueMap.get(sysFoodItemRecommendation.getCategoryName());
			foodItemRec.setCalculatedAmount(calculatedAmount);
			
			if(infantAge >= 0 && infantAge <= 5){
				ageRange = "0-5";
			}
			else if(infantAge >= 6 && infantAge <= 12){
				ageRange = "6-12";
			}
			else if(infantAge >= 13 && infantAge <= 24){
				ageRange = "13-24";
			}
			else
				throw new Exception("There are no recommendations available for infants of age over 24 months");
			
			List<FoodRecommendationRange> rangeList = sysFoodItemRecommendation.getRecommendationsByAge().get(ageRange);
			
			boolean notFound = true;
			
			for (FoodRecommendationRange range: rangeList) {
				if (calculatedAmount >= range.getFrom() && calculatedAmount <= range.getTo() && notFound)
				{
					foodItemRec.setRangeFrom(range.getFrom());
					foodItemRec.setRangeTo(range.getTo());
					foodItemRec.setLabel(range.getLabel());
					notFound = false;
				}
			}	
			
			foodItemRecommendation.getFoodCategoryRecList().add(foodItemRec);
		}		
				
		return foodItemRecommendation;
	}
}
