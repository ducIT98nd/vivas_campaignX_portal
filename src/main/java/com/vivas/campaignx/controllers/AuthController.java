package com.vivas.campaignx.controllers;

import com.vivas.campaignx.common.AttributeConstants;
import com.vivas.campaignx.common.CodeConstants;
import com.vivas.campaignx.common.MessageConstants;
import com.vivas.campaignx.common.ResponseEntity;
import com.vivas.campaignx.entity.Users;
import com.vivas.campaignx.repository.UsersRepository;
import com.vivas.campaignx.service.AuthService;
import com.vivas.campaignx.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;

@Controller
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/index")
    public String index() {
        return "redirect:/home";
    }

    @GetMapping({"/", "/login"})
    public String login(@RequestParam(value = "errorCode", required = false) Integer errorCode, Model model) {
        if (Objects.nonNull(errorCode)) {
            if (Objects.equals(CodeConstants.BAD_CREDENTIALS, errorCode)) {
                model.addAttribute(AttributeConstants.ERROR_LOGIN, MessageConstants.BAD_CREDENTIALS);
            } else if (Objects.equals(CodeConstants.NOT_ACTIVE, errorCode)) {
                model.addAttribute(AttributeConstants.ERROR_LOGIN, MessageConstants.NOT_ACTIVE);
            } else if (Objects.equals(CodeConstants.USER_BLOCK, errorCode)) {
                model.addAttribute(AttributeConstants.ERROR_LOGIN, MessageConstants.NOT_ACTIVE);
            } else {
                model.addAttribute(AttributeConstants.ERROR_LOGIN, MessageConstants.SYSTEM_ERROR);
            }
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return "redirect:/index";
        } else {
            return "auth/login";
        }
    }

    @GetMapping("/accessDenied")
    public String accessDeniedPage(Model model) {
        model.addAttribute(AttributeConstants.ERROR_FORBIDDEN, MessageConstants.FORBIDDEN);
        return "auth/accessDenied";
    }

    @GetMapping("/page-forgot-password")
    public String pageForgotPassword() {

        return "auth/forgotPassword";
    }

    @RequestMapping(value = "/forgot-password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity forgotPassword(@RequestParam String email) {
        return authService.forgotPassword(email);
    }

    @RequestMapping(value = "auth/containEmailLock", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> containEmailLock(Model model, @RequestParam String email) {
        logger.info("action count user by email: " + email);
        Map<String, Object> map = new HashMap<String, Object>();
        Long count = usersService.countEmailIgnoreStatus(email);
        if (count > 0) map.put("result", true);
        else map.put("result", false);
        return map;
    }


    @GetMapping("/page-set-password")
    public String pageSetPassword(@RequestParam String token, Model model, RedirectAttributes redirectAttributes) {
        String decodeToken = new String(Base64.getDecoder().decode(token));
        if (usersService.validateOtpToken(decodeToken)) {
            Users users = usersService.findByTokenIgnoreCase(decodeToken).get();
            model.addAttribute("username", users.getUsername());
            return "auth/setPassword";
        }
        redirectAttributes.addAttribute("result", "fail");
        return "redirect:/page-forgot-password";
    }

    public void handleResetPasswordFail(RedirectAttributes redirectAttributes) {
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setCode(CodeConstants.BAD_REQUEST);
        responseEntity.setMessage(MessageConstants.RESET_PASSWORD_FAIL);

        ServletUriComponentsBuilder uriComponentsBuilder = ServletUriComponentsBuilder.fromCurrentContextPath();
        uriComponentsBuilder.path("/login");
        responseEntity.setPath(uriComponentsBuilder.build().toUriString());
        redirectAttributes.addFlashAttribute("result", "modelvalue");
    }

    @PostMapping(value = "/set-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity setPassword(@RequestBody Map<String, String> params) {
        ResponseEntity responseEntity = new ResponseEntity();
        String otpToken = params.get("otpToken");
        String otpTokenDecode = new String(Base64.getDecoder().decode(otpToken));
        String newPassword = params.get("password");
        if (usersService.validateOtpToken(otpTokenDecode)) {
            Users users = usersService.findByTokenIgnoreCase(otpTokenDecode).get();
            users.setPassword(passwordEncoder.encode(newPassword));
            users.setOtpToken(null);
            users.setForgotPasswordDatetime(null);
            usersRepository.save(users);

            responseEntity.setCode(CodeConstants.OK);
            responseEntity.setMessage(MessageConstants.RESET_PASSWORD_SUCCESS);
        }
        return responseEntity;
    }

    @RequestMapping(value = "auth/checkEmail", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Boolean> checkTargetGroupNameUpdate(Model model, @RequestParam("email") String email) {
        logger.info(String.format("action check user email: email=%s", email));
        Map<String, Boolean> result = new HashMap<>();
        Optional<Users> usersOptional = authService.findByEmail(email);
        if (usersOptional.isPresent()) {
            result.put("data", true);
        } else result.put("data", false);

        return result;
    }
}
