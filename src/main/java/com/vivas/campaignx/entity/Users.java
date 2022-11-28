package com.vivas.campaignx.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity(name = "USERS")
public class Users implements Serializable {
    @Id
    @Column(name = "user_id", unique = true, precision = 10, scale = 0)
    @SequenceGenerator(name = "usersGenerator", sequenceName = "SEQ_USERS", allocationSize = 1)
    @GeneratedValue(generator = "usersGenerator", strategy = GenerationType.AUTO)
    private Integer userId;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    @JsonIgnore
    private String password;
    @Column(name = "name")
    private String name;
    @Column(name = "status")
    private Integer status;
    @Column(name = "created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss a")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdDate;
    @Column(name = "created_user")
    private String modifiedUser;
    @Column(name = "mobilenumber")
    private String mobileNumber;
    @Column(name = "email")
    private String email;
    
    @Column(name = "note")
    private String note;

    @Column(name = "IS_ROOT")
    private Boolean isRoot;

    @Column(name = "OTP_TOKEN")
    private String otpToken;

    @Column(name = "FORGOT_PASSWORD_DATETIME")
    private LocalDateTime forgotPasswordDatetime;

    @Column(name = "FAILED_ATTEMPT")
    private Integer failedAttempt;

    @Column(name = "LOCK_TIME")
    private Date lockTime;

    @OneToOne(mappedBy = "user")
    private UserRole userRole;


    public Integer getFailedAttempt() {
        return failedAttempt;
    }

    public void setFailedAttempt(Integer failedAttempt) {
        this.failedAttempt = failedAttempt;
    }

    public Date getLockTime() {
        return lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedUser() {
        return modifiedUser;
    }

    public void setModifiedUser(String modifiedUser) {
        this.modifiedUser = modifiedUser;
    }


    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public Boolean getRoot() {
        return isRoot;
    }

    public void setRoot(Boolean root) {
        isRoot = root;
    }

    public String getOtpToken() {
        return otpToken;
    }

    public void setOtpToken(String otpToken) {
        this.otpToken = otpToken;
    }

    public LocalDateTime getForgotPasswordDatetime() {
        return forgotPasswordDatetime;
    }

    public void setForgotPasswordDatetime(LocalDateTime forgotPasswordDatetime) {
        this.forgotPasswordDatetime = forgotPasswordDatetime;
    }

    /* @Override
	public String toString() {
		return "Users [userId=" + userId + ", username=" + username + ", password=" + password + ", name=" + name
				+ ", status=" + status + ", createdDate=" + createdDate
				+ ", mobileNumber=" + mobileNumber
				+ ", email=" + email
				+ ", note=" + note + ", userGroup=" + userGroup + "]";
	}*/


    
}
