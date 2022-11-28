package com.vivas.campaignx.config;

import java.util.*;
import java.util.stream.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.util.*;

import com.vivas.campaignx.entity.*;
import com.vivas.campaignx.repository.*;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    protected final Logger logger = LogManager.getLogger(this.getClass().getName());
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("check permission user name {}", username);
        if (StringUtils.isEmpty(username)) {
            throw new UsernameNotFoundException("username must not be empty");
        }
        Optional<Users> usersOptional = usersRepository.findByUsernameIgnoreCase(username.toLowerCase());
        Users users = new Users();
        if (!usersOptional.isPresent()) {
            throw new UsernameNotFoundException("Not found user with username: " + username);
        } else users = usersOptional.get();
        if(users.getStatus() == 0) throw new UsernameNotFoundException("User not active");
        List<String> privileges = this.getUserPermissions(users);
        Collection<GrantedAuthority> grantedAuthorities = privileges.stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        String permissionGroupName;
        if (users.getRoot() != null && users.getRoot()) {
            permissionGroupName = "Super Admin";
        }
        else {
            permissionGroupName = users.getUserRole().getRole().getRoleName();
        }
        return new UserPrinciple(users,(long) users.getUserId(), users.getUsername(), users.getPassword(), grantedAuthorities, permissionGroupName);
    }

    private List<String> getUserPermissions(Users users) {
        List<String> permissions;
        if (users.getRoot() != null && users.getRoot()) {
            permissions = this.permissionRepository.findAllpermissionKey();
        } else {
            permissions = this.permissionRepository.getAllPrivilegesByUsername(users.getUsername());
        }
        return permissions;
    }
}
