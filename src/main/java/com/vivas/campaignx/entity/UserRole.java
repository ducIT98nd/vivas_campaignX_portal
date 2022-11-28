package com.vivas.campaignx.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "user_role")
public class UserRole implements Serializable {

    @Id
    @Column(name = "id", unique = true, precision = 10, scale = 0)
    @SequenceGenerator(name = "userRoleGenerator", sequenceName = "SEQ_USER_ROLE", allocationSize = 1)
    @GeneratedValue(generator = "userRoleGenerator", strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "user_id")
    private Users user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false, referencedColumnName = "role_id")
    private Role role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
