package com.vivas.campaignx.service;

import com.vivas.campaignx.common.CampaignXUtils;
import com.vivas.campaignx.common.VNCharacterUtils;
import com.vivas.campaignx.entity.*;
import com.vivas.campaignx.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
public class PermissionService {
    private static final String PERMISSION_NAME_EXISTS = "Tên nhóm quyền đã tồn tại";
    private static final String CREATE_PERMISSION_SUCCESSFULLY = "Tạo mới nhóm quyền thành công!";
    private static final String UPDATE_PERMISSION_SUCCESSFULLY = "Cập nhật thông tin nhóm quyền thành công!";
    private static final String DELETE_PERMISSION_SUCCESSFULLY = "Xóa nhóm quyền thành công";
    private static final String SYSTEM_ERROR = "Không thể kết nối tới máy chủ do hệ thống đang bận. Vui lòng thử lại!";

    @Autowired
    private PermissionRepository permissionRepository;


    @Autowired
    private GroupPermissionRepository groupPermissionRepository;

    private String formatPermissionName(String permissionName) {
        String permissionNameFormat;
        if (StringUtils.isEmpty(permissionName)) {
            return permissionName;
        }
        String permissionTrim = permissionName.trim();
        String permissionLowerCase = VNCharacterUtils.removeAccent(permissionTrim).toLowerCase();
        permissionNameFormat = CampaignXUtils.getInstance().defaultLikeQueryEscape(permissionLowerCase);
        return permissionNameFormat;
    }

    private Short getStatusByParam(String status) {
        if (StringUtils.isEmpty(status))
            return null;
        return Short.decode(status);
    }
    private void deleteAllGroupPermissionByPermission(Permission permission) {
        List<GroupPermission> groupPermissionList = this.groupPermissionRepository.getAllByPermission(permission);
        this.groupPermissionRepository.deleteAll(groupPermissionList);
    }
    
}
