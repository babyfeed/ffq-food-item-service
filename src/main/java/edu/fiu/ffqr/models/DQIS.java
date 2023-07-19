package edu.fiu.ffqr.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection="fooditem_recommendations")
public class DQIS implements Serializable {
	
	@JsonProperty("questionnaireId")
	private String questionnaireId; 
	@JsonProperty("patientAge")
	int patientAgeInMonths;	
	@JsonProperty("foodCategoryRecList")
	List <DQISCategory> foodCategoryRecList;
	@JsonProperty("gender")
	String gender;
	@JsonProperty
	double points;

	public DQIS() {
		foodCategoryRecList = new ArrayList<>();
	}

	public String getQuestionnaireId() {
		return questionnaireId;
	}

	public void setQuestionnaireId(String questionnaireId) {
		this.questionnaireId = questionnaireId;
	}

	public int getPatientAgeInMonths() {
		return patientAgeInMonths;
	}

	public void setPatientAgeInMonths(int patientAgeInMonths) {
		this.patientAgeInMonths = patientAgeInMonths;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public List<DQISCategory> getFoodCategoryRecList() {
		return foodCategoryRecList;
	}
	public double getPoints(){
		return points;
	}
	public void setPoints(double points){
		this.points = points;
	}

	public void setFoodCategoryRecList(List<DQISCategory> foodCategoryRecList) {
		this.foodCategoryRecList = foodCategoryRecList;
	}
}
