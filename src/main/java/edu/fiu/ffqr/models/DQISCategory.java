package edu.fiu.ffqr.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DQISCategory implements Serializable {

	@JsonProperty("categoryName")
	private String categoryName;
	
	@JsonProperty("rangeFrom")
	private double rangeFrom;
	
	@JsonProperty("rangeTo")
	private double rangeTo;
	
	@JsonProperty("label")
	private String label;
	
	@JsonProperty("calculatedAmount")
	private Double calculatedAmount;

	@JsonProperty("totalPoints")
	private Double totalPoints;
	
    public DQISCategory(){}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public double getRangeFrom() {
		return rangeFrom;
	}

	public void setRangeFrom(double rangeFrom) {
		this.rangeFrom = rangeFrom;
	}

	public double getRangeTo() {
		return rangeTo;
	}

	public void setRangeTo(double rangeTo) {
		this.rangeTo = rangeTo;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Double getCalculatedAmount() {
		return calculatedAmount;
	}

	public void setCalculatedAmount(Double calculatedAmount) {
		this.calculatedAmount = calculatedAmount;
	}
	public double getTotalPoints(){
		return totalPoints;
	}
	public void setTotalPoints(Double totalPoints){
		this.totalPoints = totalPoints;
	} 
	
}
