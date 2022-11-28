package com.vivas.campaignx.controllers;

import com.vivas.campaignx.common.CodeConstants;
import com.vivas.campaignx.common.CommonConstants;
import com.vivas.campaignx.common.MessageConstants;
import com.vivas.campaignx.common.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/package-codes")
@Controller
public class PackageCodeController {

    @Value("${campaignx.api.service_code}")
    private String packageCodeByData;

    @Value("${campaignx.api.current_package}")
    private String packageCodeByKmcb;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity getPackageCodeName(@RequestParam String packageGroup) {
        ResponseEntity responseEntity = new ResponseEntity();
        try {
            if (StringUtils.hasText(packageGroup) && CommonConstants.PACKAGE_GROUP_DATA.equalsIgnoreCase(packageGroup)) {
                Object packageCodeNames = restTemplate.getForObject(packageCodeByData, Object.class);
                responseEntity.setCode(CodeConstants.OK);
                responseEntity.setData(packageCodeNames);
            }
            if (StringUtils.hasText(packageGroup) && CommonConstants.PACKAGE_GROUP_KMCB.equalsIgnoreCase(packageGroup)) {
                Object packageCodeNames = restTemplate.getForObject(packageCodeByKmcb, Object.class);
                responseEntity.setCode(CodeConstants.OK);
                responseEntity.setData(packageCodeNames);
            }
        } catch (Exception e) {
            responseEntity.setCode(CodeConstants.INTERNAL_SERVER_ERROR);
            responseEntity.setMessage(MessageConstants.SYSTEM_ERROR);
        }
        return responseEntity;
    }
}
