package com.vivas.campaignx.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "package_code_cdr")
public class PackageCodeCDR implements Serializable {

    @Id
    @Column(name = "id", unique = true, precision = 10)
    @SequenceGenerator(name = "packageCodeGenerator", sequenceName = "SEQ_PACKAGE_CODE", allocationSize = 1)
    @GeneratedValue(generator = "packageCodeGenerator", strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "package_code_name")
    private String packageCodeName;

    @Column(name = "package_data_id")
    private Long packageDataId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageCodeName() {
        return packageCodeName;
    }

    public void setPackageCodeName(String packageCodeName) {
        this.packageCodeName = packageCodeName;
    }

    public Long getPackageDataId() {
        return packageDataId;
    }

    public void setPackageDataId(Long packageDataId) {
        this.packageDataId = packageDataId;
    }
}
