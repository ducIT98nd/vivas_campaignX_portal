package com.vivas.campaignx.controllers;

import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.config.UserPrinciple;
import com.vivas.campaignx.dto.NotifyTargetGroupDTO;
import com.vivas.campaignx.dto.SimpleResponseDTO;
import com.vivas.campaignx.entity.Notify;
import com.vivas.campaignx.repository.NotifyRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class NotifyController {
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    @Autowired
    private NotifyRepository notifyRepository;

    @RequestMapping(value = "/NotifyController/getNotify", method = RequestMethod.GET)
    public @ResponseBody
    String getNotify() {
        logger.info(String.format("action get notify"));
        SimpleResponseDTO res = new SimpleResponseDTO();
        String result = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Long userID  = ((UserPrinciple) principal).getUserId();
        if(userID != null) {
            List<Notify> list = notifyRepository.findAllNotifyNotReadToUserId(userID);
            if(list != null && list.size() < 5){
                int tmp = 5 - list.size();
                List<Notify> listTmp =  notifyRepository.findAllNotifyReadToUserId(PageRequest.of(0, tmp), userID);
                list.addAll(listTmp);
            }
            List<NotifyTargetGroupDTO> contents = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Notify obj = list.get(i);
                NotifyTargetGroupDTO notiObj = new NotifyTargetGroupDTO();
                String content = obj.getContent();
                notiObj.setSubject(obj.getSubject());
                notiObj.setContent(content);
                notiObj.setCreatedDate(AppUtils.formatDefaultDatetime(obj.getCreatedDate()));
                if (obj.getStatus() == 0) {
                    notiObj.setStatus(0l);
                } else notiObj.setStatus(1l);
                contents.add(notiObj);
            }
            res.setCode(0);
            res.setData(contents);
            result = AppUtils.ObjectToJsonResponse(res);
        }
        return result;
    }

    @RequestMapping(value = "/NotifyController/markAllAsRead", method = RequestMethod.GET)
    public @ResponseBody
    String markAllAsRead() {
        logger.info(String.format("action mark All As Read"));
        SimpleResponseDTO res = new SimpleResponseDTO();
        String result = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Long userID  = ((UserPrinciple) principal).getUserId();
        if(userID != null) {
            notifyRepository.updateMarkAsReadByUserID(userID);
        }
        res.setCode(0);
        result = AppUtils.ObjectToJsonResponse(res);
        return result;
    }
}
