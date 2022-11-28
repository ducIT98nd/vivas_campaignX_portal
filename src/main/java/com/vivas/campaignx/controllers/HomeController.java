package com.vivas.campaignx.controllers;

import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.config.UserPrinciple;
import com.vivas.campaignx.dto.BigdataDTO;
import com.vivas.campaignx.dto.NotifyTargetGroupDTO;
import com.vivas.campaignx.dto.SimpleResponseDTO;
import com.vivas.campaignx.entity.Notify;
import com.vivas.campaignx.entity.TargetGroup;
import com.vivas.campaignx.repository.NotifyRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {
    @Value("${superset.url}")
    private String supersetUrl;

    @RequestMapping("/home")
    public String home(Model model) {
        model.addAttribute("supersetUrl", supersetUrl);
        return "home";
    }

}
