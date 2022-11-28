package com.vivas.campaignx.service;

import com.vivas.campaignx.config.UserPrinciple;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PermissionUtils {

    public boolean checkCurrentUserIsSupperAdmin() {
        if (!isAuthenticated()) {
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        String supperAdmin = "";
        if (principal instanceof UserPrinciple) {
            supperAdmin = ((UserPrinciple) principal).getGroupName();
        }
        return !StringUtils.isEmpty(authentication.getName()) && "Super Admin".equalsIgnoreCase(supperAdmin);
    }

    public String getUserRole(){
        if (!isAuthenticated()) {
            return "Not login";
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrinciple) {
            return ((UserPrinciple) principal).getGroupName();
        }
        return "Unknown";
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null;
    }

}
