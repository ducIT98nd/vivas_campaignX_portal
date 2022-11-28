package com.vivas.campaignx.service;

import com.vivas.campaignx.entity.Role;
import com.vivas.campaignx.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> findAllRoleActive(){
        return roleRepository.findAllRoleActive();
    }
}
