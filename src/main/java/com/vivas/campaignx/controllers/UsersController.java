package com.vivas.campaignx.controllers;

import com.vivas.campaignx.common.AppException;
import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.common.enums.ErrorCodeEnum;
import com.vivas.campaignx.config.UserPrinciple;
import com.vivas.campaignx.dto.SimpleResponseDTO;
import com.vivas.campaignx.dto.UserDTO;
import com.vivas.campaignx.dto.UserTempDTO;
import com.vivas.campaignx.entity.*;
import com.vivas.campaignx.export.ExcelFileExporter;
import com.vivas.campaignx.repository.NotifyRepository;
import com.vivas.campaignx.service.GroupService;
import com.vivas.campaignx.service.RoleService;
import com.vivas.campaignx.service.UsersService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.util.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.JstlUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping(value = "UsersController")
public class UsersController {

    protected final Logger logger = LogManager.getLogger(this.getClass().getName());
    private UsersService usersService;
    private final Integer SYSTEM_ROLE_ADMIN = 0;

    @Autowired
    public UsersController(UsersService usersService, GroupService groupService) {
        this.usersService = usersService;
    }

    @Autowired
    private RoleService roleService;

    @Autowired
    private NotifyRepository notifyRepository;

    @Value("${default_page_size}")
    private Integer defaultPageSize;

    /*@Value("${notify.content.changeUserProfile}")*/
    private final String notifyContentChangeUserProfile = "Thông tin cá nhân của bạn đã được thay đổi bởi Quản trị viên. Vui lòng kiểm tra lại thông tin và thông báo tới Quản trị viên nếu có sai sót";

    @Autowired
    private SimpMessagingTemplate template;

    @PreAuthorize("hasAuthority('create:user')")
    @RequestMapping(value = "/submitCreateUser", method = RequestMethod.POST)
    public String submitCreateUser(Model model, @RequestParam(required = false) String username,
                                   @RequestParam(required = false) String password,
                                   @RequestParam(required = false) String name,
                                   @RequestParam(required = false) String email,
                                   @RequestParam(required = false) Long roleId,
                                   @RequestParam(required = false) Long status,
                                   @RequestParam(required = false) String confirmPassword,
                                   RedirectAttributes redirectAttributes) {

        Users user1 = new Users();
        logger.info("action insert new user with info: username: " + username + " password: " + password + " name: " + name + "roleID: " + roleId + " status: " + status);
        user1.setUsername(username);
        user1.setPassword(password);
        user1.setName(name);
        user1.setEmail(email);
        try {

            Optional<Users> user = usersService.findUserByUsernameAndStatus(username);
            if (user.isPresent()) {
                model.addAttribute("errorMessage", "Tên đăng nhập đã tồn tại!");
                model.addAttribute("user", user1);
                return "user/createUser";
            }
            HashMap<String, String> reqParams = new HashMap<>();
            reqParams.put("username", username);
            reqParams.put("password", password);
            reqParams.put("confirmPassword", confirmPassword);
            reqParams.put("name", name);
            reqParams.put("email", email);
            reqParams.put("roleId", String.valueOf(roleId));
            reqParams.put("status", String.valueOf(status));

            UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            usersService.createUser(reqParams, currentUser.getUsername());
            redirectAttributes.addFlashAttribute("alertMessage", "Thêm mới tài khoản thành công");
            return "redirect:/UsersController/usersManager";
        } catch (Exception ex) {
            logger.error("Error while create user: ", ex);
            Throwable rootcause = AppUtils.getrootcause(ex);
            if (rootcause instanceof AppException) {
                AppException apex = (AppException) rootcause;
                model.addAttribute("errorMessage", apex.getMessage());
            } else {
                model.addAttribute("errorMessage", "Có lỗi xảy ra khi tạo tài khoản, vui lòng liên hệ ban quản trị!");
            }
            model.addAttribute("user", user1);
            return "redirect:/UsersController/usersManager";
        }
    }

    @PreAuthorize("hasAnyAuthority('search:view:user')")
    @GetMapping("/usersManager")
    public String usersManager(Model model, @RequestParam(required = false) Integer pageSize,
                               @RequestParam(required = false) Integer currentPage, @RequestParam(required = false) String username,
                               @RequestParam(required = false) String email, @RequestParam(required = false) Long roleID, @RequestParam(required = false) Long status) {
        logger.info("find user with info: username:" + username + " email: " + email + " roleId: " + roleID + " status: " + status);
        if (pageSize == null) pageSize = defaultPageSize;
        if (currentPage == null) currentPage = 1;
        Long valueStatus;
        if (status == null || status == 2) valueStatus = null;
        else valueStatus = status;
        String usernameSearch = null;
        String emailSearch = null;
        if(username != null && username.length() > 0) usernameSearch = username.toLowerCase(Locale.ROOT);
        if(email != null && email.length() > 0) emailSearch = email.toLowerCase(Locale.ROOT);
        Page<UserDTO> usersPage = usersService.findUsers(pageSize, currentPage, usernameSearch, emailSearch, roleID, valueStatus);
        logger.info("Found users size = {}", usersPage.getNumberOfElements());
        List<Role> listRole = roleService.findAllRoleActive();
        model.addAttribute("roles", listRole);
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("roleID", roleID);
        model.addAttribute("status", status);
        return "user/usersManager";
    }

    @PreAuthorize("hasAnyAuthority('search:view:user')")
    @GetMapping("/exportUser")
    public void exportUser(HttpServletResponse response,
                           @RequestParam(required = false) Integer currentPage, @RequestParam(required = false) String username,
                           @RequestParam(required = false) String email, @RequestParam(required = false) Long roleID, @RequestParam(required = false) Long status) {
        logger.info("export user with info: username:" + username + " email: " + email + " roleId: " + roleID + " status: " + status);
        String fileName = "Danh_sach_tai_khoan_" + AppUtils.convertDateToString(new Date(), "ddMMyyyy") + "_" + new Date().getHours() + ".xlsx";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        Long valueStatus;
        if (status == null || status == 2) valueStatus = null;
        else valueStatus = status;
        String usernameSearch = null;
        String emailSearch = null;
        if(username != null && username.length() > 0) usernameSearch = username.toLowerCase(Locale.ROOT);
        if(email != null && email.length() > 0) emailSearch = email.toLowerCase(Locale.ROOT);
        List<UserDTO> usersPage = usersService.findAllUser(usernameSearch, emailSearch, roleID, valueStatus);
        ByteArrayInputStream stream = ExcelFileExporter.userListToExcelFile(usersPage);
        try {
            IOUtils.copy(stream, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @PreAuthorize("hasAuthority('update:user')")
    @RequestMapping(value = "/getUserById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getUserById(@RequestParam Integer userId) {
        SimpleResponseDTO res = new SimpleResponseDTO();
        try {
            Optional<Users> user = usersService.findUserById(userId);
            UserRole userRole = usersService.findUserRoledByUserId(userId);
            if (user.isPresent()) {
                UserTempDTO u = new UserTempDTO();
                u.setUserId(user.get().getUserId());
                u.setUsername(user.get().getUsername());
                u.setName(user.get().getName());
                u.setEmail(user.get().getEmail());
                u.setStatus(user.get().getStatus());
                logger.info("Found user id = {}", userId);
                res.setCode(ErrorCodeEnum.SUCCESS.getCode());
                res.setData(u);
                logger.info("data= {}", user.get());
                JSONObject jsonObject = new JSONObject(AppUtils.ObjectToJsonResponse(res));
                jsonObject.put("roleId", userRole.getRole().getRoleId());
                logger.info("Jsonobj = {}", jsonObject.toString());
                return jsonObject.toString();
            }
            logger.info("Not found user id = {}", userId);
        } catch (Exception ex) {
            logger.error("Error while find user: ", ex);
        }
        res.setCode(ErrorCodeEnum.SUCCESS.getCode());
        res.setMessage("Có lỗi xảy ra khi lấy thông tin người dùng");
        return AppUtils.ObjectToJsonResponse(res);
    }

    @PreAuthorize("hasAuthority('detail:user')")
    @RequestMapping(value = "/getDetailUserById", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDetailUserById(@RequestParam Integer userId) {
        SimpleResponseDTO res = new SimpleResponseDTO();
        try {
                UserDTO user = usersService.findDetailUserById((long) userId);
                logger.info("Found user id = {}", userId);
                res.setCode(ErrorCodeEnum.SUCCESS.getCode());
                res.setData(user);
        } catch (Exception ex) {
            logger.error("Error while find user: ", ex);
        }
        res.setCode(ErrorCodeEnum.SUCCESS.getCode());
        res.setMessage("Lấy thông tin người dùng thành công");
        return AppUtils.ObjectToJsonResponse(res);
    }


    @PreAuthorize("hasAuthority('update:user')")
    @RequestMapping(value = "/updateUser", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String updateUser( @RequestParam(required = false) Long userId,
                              @RequestParam(required = false) String name,
                              @RequestParam(required = false) String email,
                              @RequestParam(required = false) Long roleId,
                              @RequestParam(required = false) Long status) {
        SimpleResponseDTO res = new SimpleResponseDTO();
        HashMap<String, String> reqParams = new HashMap<>();
        reqParams.put("userId", String.valueOf(userId));
        reqParams.put("name", name);
        reqParams.put("email", email);
        reqParams.put("roleId", String.valueOf(roleId));
        reqParams.put("status", String.valueOf(status));
        try {
            Optional<Users> user = usersService.findUserById(userId.intValue());

            if (user.isPresent()) {
                UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                usersService.updateUser(user.get(), reqParams, currentUser.getUsername());
                res.setCode(ErrorCodeEnum.SUCCESS.getCode());
                res.setMessage("Chỉnh sửa thông tin tài khoản thành công");
                Notify notify = new Notify();
                notify.setCreatedDate(new Date());
                notify.setStatus(0L);
                notify.setSubject("Thay đổi thông tin tài khoản");
                notify.setContent(notifyContentChangeUserProfile);
                notify.setNotifyToUserId(userId);
                notifyRepository.save(notify);
                JSONObject jsonObjectNotify = new JSONObject();
                jsonObjectNotify.put("action", "changeUser");
                template.convertAndSend("/topic/userAction", jsonObjectNotify.toString());
                return AppUtils.ObjectToJsonResponse(res);
            }
        } catch (Exception ex) {
            logger.error("Error while update user: ", ex);
            Throwable rootcause = AppUtils.getrootcause(ex);
            if (rootcause instanceof AppException) {
                AppException apex = (AppException) rootcause;
                res.setMessage(apex.getMessage());
            } else {
                res.setMessage("Có lỗi xảy ra, cập nhật thông tin tài khoản không thành công");
            }
        }
        res.setCode(ErrorCodeEnum.SUCCESS.getCode());
        logger.error("Khong tim thay user id={}", Integer.parseInt(reqParams.get("userId")));
        return AppUtils.ObjectToJsonResponse(res);
    }

    @PreAuthorize("hasAuthority('delete:user')")
    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteUser(@RequestParam Integer userId) {
        SimpleResponseDTO res = new SimpleResponseDTO();
        try {
            List<Long> listUserIdLogin = usersService.getAllLoggedInUsers();
            Optional<Users> user = usersService.findUserById(userId);
            if (user.isPresent()) {
                UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                usersService.deleteUser(user.get());
                logger.info("Xóa tài khoản thành công, user {}, nguoi xoa {}", user.get().getUsername(), currentUser);
                res.setCode(ErrorCodeEnum.SUCCESS.getCode());
                res.setMessage("Xóa tài khoản thành công");
                logger.info("list user login: " + listUserIdLogin);
                logger.info("userDelete: " + userId);
                if(listUserIdLogin.contains(new Long(userId))){
                    logger.info("user deleted login");
                    JSONObject jsonObjectNotify = new JSONObject();
                    jsonObjectNotify.put("action", "deleteUser");
                    jsonObjectNotify.put("userId", userId);
                    template.convertAndSend("/topic/userAction", jsonObjectNotify.toString());
                }
                return AppUtils.ObjectToJsonResponse(res);
            }
            logger.error("Khong tim thay user id={}", userId);
        } catch (Exception ex) {
            logger.error("Co loi xay ra view user: ", ex);
        }
        res.setCode(ErrorCodeEnum.SYSTEM_ERROR.getCode());
        res.setMessage("Có lỗi xảy ra, xóa tài khoản không thành công");
        return AppUtils.ObjectToJsonResponse(res);
    }

    @PreAuthorize("hasAuthority('renew:user')")
    @RequestMapping(value = "/renewPassword", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String renewPassword(@RequestParam Integer userId, @RequestParam String password, @RequestParam String passwordConfirm) {
        SimpleResponseDTO res = new SimpleResponseDTO();
        try {
            Optional<Users> user = usersService.findUserById(userId);
            if (user.isPresent()) {
                List<Long> list =  usersService.getAllLoggedInUsers();
                UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                usersService.renewPassword(user.get(), currentUser.getUsername(), password, passwordConfirm);
                res.setCode(ErrorCodeEnum.SUCCESS.getCode());
                res.setMessage("Cấp lại mật khẩu tài khoản thành công");
                if(list.contains(new Long(userId))){
                    logger.info("user renew password login");
                    JSONObject jsonObjectNotify = new JSONObject();
                    jsonObjectNotify.put("action", "renewPassword");
                    jsonObjectNotify.put("userId", userId);
                    template.convertAndSend("/topic/userAction", jsonObjectNotify.toString());
                }
                return AppUtils.ObjectToJsonResponse(res);
            }
            logger.error("Khong tim thay user id={}", userId);
            res.setMessage("Có lỗi xảy ra, cập nhật mật khẩu không thành công");
        } catch (Exception ex) {
            logger.error("Co loi xay ra: ", ex);
            Throwable rootcause = AppUtils.getrootcause(ex);
            if (rootcause instanceof AppException) {
                AppException apex = (AppException) rootcause;

                res.setMessage(apex.getMessage());
            } else res.setMessage("Có lỗi xảy ra, cập nhật mật khẩu không thành công");
        }
        res.setCode(ErrorCodeEnum.SUCCESS.getCode());
        return AppUtils.ObjectToJsonResponse(res);
    }
    @PreAuthorize("hasAuthority('change:password')")
    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    public String changePassword(Model model) {
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = currentUser.getUsername();
        Optional<Users> user = usersService.findUserByUsernameAndStatus(username);
        logger.info("action view form change password for userId: " + user.get().getUserId());
        model.addAttribute("username", username);
        return "user/changePassword";
    }
    @PreAuthorize("hasAuthority('change:password')")
    @RequestMapping(value = "/saveChangePassword", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String saveChangePassword(@RequestParam String oldPassword, @RequestParam String password, @RequestParam String passwordConfirm) {
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("action change password for username: " + currentUser.getUsername());
        SimpleResponseDTO res = new SimpleResponseDTO();
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        try {
            Optional<Users> user = usersService.findUserByUsernameAndStatus(currentUser.getUsername());
            if (user.isPresent()) {
                logger.info("old password input: " + oldPassword);
                if (!bcrypt.matches(oldPassword, user.get().getPassword())) {

                    res.setMessage("Mật khẩu cũ không đúng vui lòng thử lại.");
                    return AppUtils.ObjectToJsonResponse(res);
                }
                usersService.renewPassword(user.get(), currentUser.getUsername(), password, passwordConfirm);
                res.setCode(ErrorCodeEnum.SUCCESS.getCode());
                res.setMessage("Đổi mật khẩu thành công.");
                return AppUtils.ObjectToJsonResponse(res);
            }
            logger.error("Khong tim thay username={}", currentUser.getUsername());
            res.setMessage("Có lỗi xảy ra, Đổi mật khẩu  không thành công");
        } catch (Exception ex) {
            logger.error("Co loi xay ra: ", ex);
            Throwable rootcause = AppUtils.getrootcause(ex);
            if (rootcause instanceof AppException) {
                AppException apex = (AppException) rootcause;

                res.setMessage(apex.getMessage());
            } else res.setMessage("Có lỗi xảy ra, Đổi mật khẩu không thành công");
        }
        res.setCode(ErrorCodeEnum.SUCCESS.getCode());
        return AppUtils.ObjectToJsonResponse(res);
    }

    @RequestMapping(value = "/containUsername", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> containUsername(Model model, @RequestParam String username) {
        logger.info("action count user by username: " + username);
        Map<String,Object> map = new HashMap<String,Object>();
        Long count = usersService.countUsersByUsernameIgnoreStatusDelete(username);
        if(count > 0) map.put("result", true);
        else map.put("result", false);
        return map;
    }

    @RequestMapping(value = "/containEmailWithUserId", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> containEmailWithUserId(Model model, @RequestParam String email,  @RequestParam Long id) {
        logger.info("action count user by email {} and user id:{}",email, id);
        Map<String,Object> map = new HashMap<String,Object>();
        Long count = usersService.countEmailAndIdIgnoreStatusDelete(email, id);
        if(count > 0) map.put("result", true);
        else map.put("result", false);
        return map;
    }

    @RequestMapping(value = "/containEmail", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> containEmail(Model model, @RequestParam String email) {
        logger.info("action count user by email: " + email);
        Map<String,Object> map = new HashMap<String,Object>();
        Long count = usersService.countEmailIgnoreStatusDelete(email);
        if(count > 0) map.put("result", true);
        else map.put("result", false);
        return map;
    }

    @RequestMapping(value = "/verifyDeleteUser", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> verifyDeleteUser(@RequestParam Long userId) {
        logger.info("action verify userid: " + userId);
        UserPrinciple currentUser = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String,Object> map = new HashMap<String,Object>();
        if(Objects.equals(currentUser.getUserId(), userId)) map.put("result", true);
        else map.put("result", false);
        return map;
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        System.out.println("---------: " + bcrypt.encode("123456"));
        System.out.println("---------: " + bcrypt.matches("123456", "$2a$10$/ypjjcBFSdPEVgscNFrXKeO9Ra3NwI620UrNV46Ff29iZafGE8UHi"));

    }
    @RequestMapping(value = "/checkOldPassword", method = RequestMethod.POST)
    public @ResponseBody String checkOldPassword(@RequestParam String oldPassword) {
        SimpleResponseDTO res = new SimpleResponseDTO();
        try {
            UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = currentUser.getUsername();
            Optional<Users> user = usersService.findUserByUsernameAndStatus(username);
            logger.info("action check old password for userId: " + user.get().getUserId());
            BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
            if(bcrypt.matches(oldPassword, user.get().getPassword())){
                res.setCode(AppUtils.successCode);
                res.setData(true);
            }else {
                res.setCode(AppUtils.successCode);
                res.setData(false);
            }
        }catch(Exception e){
            logger.error("error: ", e);
            res.setCode(AppUtils.errorCode);
        }
        return AppUtils.ObjectToJsonResponse(res);
    }

}
