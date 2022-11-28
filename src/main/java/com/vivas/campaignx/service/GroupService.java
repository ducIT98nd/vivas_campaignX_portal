package com.vivas.campaignx.service;

import com.vivas.campaignx.entity.Group;
import com.vivas.campaignx.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;

    public List<Group> findAll() {
        return groupRepository.findAll();
    }
}
