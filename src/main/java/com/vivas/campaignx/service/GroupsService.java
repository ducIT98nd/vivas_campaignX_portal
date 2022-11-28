package com.vivas.campaignx.service;

import com.vivas.campaignx.common.AppException;
import com.vivas.campaignx.common.CampaignXUtils;
import com.vivas.campaignx.common.VNCharacterUtils;
import com.vivas.campaignx.entity.Group;
import com.vivas.campaignx.entity.GroupPermission;
import com.vivas.campaignx.entity.Permission;
import com.vivas.campaignx.repository.GroupPermissionRepository;
import com.vivas.campaignx.repository.GroupRepository;
import com.vivas.campaignx.repository.PermissionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GroupsService {
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    GroupPermissionRepository groupPermissionRepository;

    @Autowired
    PermissionRepository permissionRepository;

    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    public void saveUserGroup(List<Integer> permission, String groupName, String groupDescription, String activateNewUserGroup) throws AppException {
        try {
            Group group = new Group();
            group.setGroupName(groupName);
            group.setDescription(groupDescription);
            if (activateNewUserGroup != null && activateNewUserGroup.equals("on")) {
                group.setStatus(true);
            } else group.setStatus(false);
            Group entity = groupRepository.save(group);
            List<GroupPermission> lst = new ArrayList<>();
            for (int i = 0; i < permission.size(); i++) {
                GroupPermission obj = new GroupPermission();
                Optional<Permission> optionalPermission = permissionRepository.findById((long) permission.get(i));
                if (optionalPermission.isPresent()) {
                    obj.setGroup(entity);
                    obj.setPermission(optionalPermission.get());
                    lst.add(obj);
                } else {
                    throw new AppException("Nhóm quyền không tồn tại");
                }
            }
            groupPermissionRepository.saveAll(lst);
        }catch(Exception e){
            logger.error("error insert new group user: ", e);
        }
    }

    public void updateUserGroup(List<Integer> permission, Long groupID, String groupName, String groupDescription, String activateNewUserGroup) throws AppException {
        try {
            Group group = new Group();
            group.setGroupID(groupID);
            group.setGroupName(groupName);
            group.setDescription(groupDescription);
            if (activateNewUserGroup != null && activateNewUserGroup.equals("on")) {
                group.setStatus(true);
            } else group.setStatus(false);
            Group entity = groupRepository.save(group);

            groupPermissionRepository.deleteByGroupID(entity.getGroupID());

            List<GroupPermission> lst = new ArrayList<>();
            for (int i = 0; i < permission.size(); i++) {
                GroupPermission obj = new GroupPermission();
                Optional<Permission> optionalPermission = permissionRepository.findById((long) permission.get(i));
                if (optionalPermission.isPresent()) {
                    obj.setGroup(entity);
                    obj.setPermission(optionalPermission.get());
                    lst.add(obj);
                } else {
                    throw new AppException("Nhóm quyền không tồn tại");
                }
            }
            groupPermissionRepository.saveAll(lst);
        }catch(Exception e){
            logger.error("error insert new group user: ", e);
        }
    }

    public Page<Group> getAllGroup(int pageSize, int currentPage, String userGroupName, Integer status){
        Sort.Order orderBy = new Sort.Order(Sort.Direction.DESC, "GROUP_ID");
        Pageable paging = PageRequest.of(currentPage-1, pageSize);
        String userGroupNameFormat = this.formatuserGroupName(userGroupName);
        return groupRepository.getAllGroups(userGroupNameFormat, status, paging);
    }

    private String formatuserGroupName(String userGroupName) {
        String userGroupNameFormat;
        if (StringUtils.isEmpty(userGroupName)) {
            return userGroupName;
        }
        String userGroupNameTrim = userGroupName.trim();
        String userGroupNameLowerCase = VNCharacterUtils.removeAccent(userGroupNameTrim).toLowerCase();
        userGroupNameFormat = CampaignXUtils.getInstance().defaultLikeQueryEscape(userGroupNameLowerCase);
        return userGroupNameFormat;
    }

    public long countGroupByName(String groupName){
        return groupRepository.countByGroupName(groupName);
    }

    public long countGroupByNameAndID(String groupName, Long groupID){
        return groupRepository.countByGroupNameAndGroupIDNotIn(groupName, groupID);
    }

    public Group findByID(Long groupID){
        Optional<Group> optionalGroup = groupRepository.findById(groupID);
        if(optionalGroup.isPresent()) return optionalGroup.get();
        else return null;
    }

    public void deleteByGroupID(Long groupID){
        groupRepository.deleteByGroupID(groupID);
    }
}
