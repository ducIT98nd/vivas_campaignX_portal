package com.vivas.campaignx.entity;

import javax.persistence.*;

@Entity(name = "mapping_criteria")
public class MappingCriteria {
    @Id
    @SequenceGenerator(name = "mappingCriteriaGenerator", sequenceName = "SEQ_MAPPING_CRITERIA", allocationSize = 1)
    @GeneratedValue(generator = "mappingCriteriaGenerator", strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @Column(name = "ID_BIGDATA_CRITERIA")
    private Long idBigdataCriteria;
    @Column(name = "ID_TARGET_GROUP")
    private Long idTargetGroup;
    @Column(name = "ID_SUB_TARGET_GROUP")
    private Long idSubTargetGroup;
    @Column(name = "SELECTED_VALUE")
    private String selectedValue;
    @Column(name = "TYPE_INPUT")
    private String typeInput;
    @Column(name = "CRITERIA_NAME")
    private String criteriaName;
    @Column(name = "UNIT")
    private String unit;
    @Column(name = "POSITION")
    private Long position;
    @Column(name = "PARENT_ID")
    private Long parentId;
    @Column(name = "TYPE")
    private Integer type;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "LEVEL_CRITERIA")
    private Integer levelCriteria;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdBigdataCriteria() {
        return idBigdataCriteria;
    }

    public void setIdBigdataCriteria(Long idBigdataCriteria) {
        this.idBigdataCriteria = idBigdataCriteria;
    }

    public Long getIdTargetGroup() {
        return idTargetGroup;
    }

    public void setIdTargetGroup(Long idTargetGroup) {
        this.idTargetGroup = idTargetGroup;
    }

    public Long getIdSubTargetGroup() {
        return idSubTargetGroup;
    }

    public void setIdSubTargetGroup(Long idSubTargetGroup) {
        this.idSubTargetGroup = idSubTargetGroup;
    }

    public String getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
    }

    public String getTypeInput() {
        return typeInput;
    }

    public void setTypeInput(String typeInput) {
        this.typeInput = typeInput;
    }

    public String getCriteriaName() {
        return criteriaName;
    }

    public void setCriteriaName(String criteriaName) {
        this.criteriaName = criteriaName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public Integer getLevelCriteria() {
        return levelCriteria;
    }

    public void setLevelCriteria(Integer levelCriteria) {
        this.levelCriteria = levelCriteria;
    }
}
