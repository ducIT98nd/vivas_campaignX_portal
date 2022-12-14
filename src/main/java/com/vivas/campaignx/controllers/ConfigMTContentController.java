package com.vivas.campaignx.controllers;
import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.dto.SimpleResponseDTO;
import com.vivas.campaignx.entity.ConfigMtContent;
import com.vivas.campaignx.service.ConfigMtContentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "config-mt")
public class ConfigMTContentController {

    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    private ConfigMtContentService configMtContentService;

    public ConfigMTContentController (ConfigMtContentService configMtContentService){
        this.configMtContentService = configMtContentService;
    }

    @PreAuthorize("hasAnyAuthority('list:config-mt-content')")
    @RequestMapping(value = "")
    public String getSendingLimit(Model model) {

        List<ConfigMtContent> listMT = configMtContentService.getListMT();
        model.addAttribute("listMT", listMT);
        return "sendingConfig/config-mt-content";
    }

    @PreAuthorize("hasAnyAuthority('update:config-mt-content')")
    @PostMapping(value = "/update")
    @ResponseBody
    public String updateSendingLimit(@RequestParam Integer actionId, @RequestParam String mtContent) {

        SimpleResponseDTO res = new SimpleResponseDTO();
        try{
            Optional<ConfigMtContent> optional = configMtContentService.getMtContentByAction(actionId);
            if (optional.isPresent()){
                logger.info("Found action id = {}", actionId );
               // logger.info("Found number characters = {}", numberCharacters );
                ConfigMtContent configMt = optional.get();
                configMt.setContent(mtContent);
//                configMt.setNumberMt(numberMt);
//                configMt.setNumberCharacters(numberCharacters);
                configMtContentService.updateMtContent(configMt);
                res.setCode(AppUtils.successCode);
                res.setMessage("Ch???nh s???a tin ph???n h???i t??? ?????ng th??nh c??ng.");
                return AppUtils.ObjectToJsonResponse(res);
            }
            logger.info("Not found action = {}", actionId);
        } catch (Exception ex){
            logger.error("Error while find user: ", ex);
        }
        res.setCode(AppUtils.errorCode);
        res.setMessage("C?? l???i x???y ra khi c???p nh???t n???i dung MT");
        return AppUtils.ObjectToJsonResponse(res);
    }

}

