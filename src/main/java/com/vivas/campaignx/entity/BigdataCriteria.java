package com.vivas.campaignx.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity(name = "bigdata_criteria")
public class BigdataCriteria {
	@Id
	@Column(name = "ID", unique = true)
	private Long id;
	
	@Column(name = "FIRST_LEVEL")
	private String firstLevel;
	
	@Column(name = "VALUE")
	private String value;
	
	@Column(name = "SECOND_LEVEL")
	private String secondLevel;
	
	@Column(name = "THIRD_LEVEL")
	private String thirdLevel;

	@Column(name = "FOURTH_LEVEL")
	private String fourthLevel;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstLevel() {
		return firstLevel;
	}

	public void setFirstLevel(String firstLevel) {
		this.firstLevel = firstLevel;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getSecondLevel() {
		return secondLevel;
	}

	public void setSecondLevel(String secondLevel) {
		this.secondLevel = secondLevel;
	}

	public String getThirdLevel() {
		return thirdLevel;
	}

	public void setThirdLevel(String thirdLevel) {
		this.thirdLevel = thirdLevel;
	}

	public String getFourthLevel() {
		return fourthLevel;
	}

	public void setFourthLevel(String fourthLevel) {
		this.fourthLevel = fourthLevel;
	}

	@Override
	public String toString() {
		return "BigdataCriteria [id=" + id + ", firstLevel=" + firstLevel + ", value=" + value + ", secondLevel="
				+ secondLevel + ", thirdLevel=" + thirdLevel + "]";
	}

}
