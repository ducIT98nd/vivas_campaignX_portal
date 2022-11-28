package com.vivas.campaignx.controllers;

import com.vivas.campaignx.common.AppException;
import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.common.enums.ErrorCodeEnum;
import com.vivas.campaignx.dto.SimpleResponseDTO;
import com.vivas.campaignx.entity.BlackListFile;
import com.vivas.campaignx.service.BlackListFileService;
import com.vivas.campaignx.service.PermissionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/blacklist")
public class    BlackListController {
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());
    @Autowired
    private BlackListFileService blackListFileService;

    @Autowired
    private PermissionUtils permissionUtils;

    @PreAuthorize("hasAuthority('search:view:blacklist')")
    @GetMapping
    public String view(Model model, @RequestParam(required = false) Integer pageSize,
                       @RequestParam(required = false) Integer currentPage,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) Integer status) {
        if (pageSize == null)
            pageSize = 20;
        if (currentPage == null)
            currentPage = 1;
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Page<BlackListFile> dataPage = null;
        if (name == null && status == null) {
            dataPage = blackListFileService.getAll(pageSize, currentPage);
        } else dataPage = blackListFileService.getBlackList(pageSize, currentPage, name, status);
        String roleName = permissionUtils.getUserRole();
        String userName = currentUser.getUsername();
        model.addAttribute("dataPage", dataPage);
        model.addAttribute("totalPages", dataPage.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("roleName", roleName);
        model.addAttribute("userName", userName);
        model.addAttribute("name", name);
        model.addAttribute("status", status);
        return "blacklist/blacklistManager";
    }

    @PreAuthorize("hasAuthority('create:blacklist')")
    @PostMapping("/submitBlackList")
    public String create(Model model, @RequestParam(required = false) String nameBlackList,
                         @RequestParam(required = false, name = "dataBlackList") MultipartFile dataBlackList,
                         @RequestParam(required = false) String status, RedirectAttributes redirectAttributes) {
        BlackListFile blackListFile = new BlackListFile();
        try {
            UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            logger.info("action insert new black list with info: nameBlackList: " + nameBlackList + " status: " + status);
            blackListFileService.saveBlackList(nameBlackList, dataBlackList, status, currentUser.getUsername());
            redirectAttributes.addFlashAttribute("alertMessage", "Thêm mới danh sách thuê bao loại trừ thành công.");
            return "redirect:/blacklist";
        } catch (Exception ex) {
            logger.error("Error while create blacklist: ", ex);
            Throwable rootcause = AppUtils.getrootcause(ex);
            if (rootcause instanceof AppException) {
                AppException apex = (AppException) rootcause;
                model.addAttribute("errorMessage", apex.getMessage());
            } else {
                model.addAttribute("errorMessage", "Có lỗi xảy ra khi tạo tài khoản, vui lòng liên hệ ban quản trị!");
            }
        }
        model.addAttribute("blackList", blackListFile);
        return "redirect:/blacklist";
    }

    @RequestMapping(value = "/containBlacklistName", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> containBlacklistName(Model model, @RequestParam String name) {
        logger.info("action count name by blacklistName: " + name);
        Map<String, Object> map = new HashMap<String, Object>();
        Long count = blackListFileService.countByNameAndStatus(name);
        logger.info("count: " + count);
        if (count > 0) map.put("result", true);
        else map.put("result", false);
        return map;
    }

    @RequestMapping(value = "/containBlacklistNameNotId/{id}", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> containBlacklistNameNotId(Model model, @RequestParam String name, @PathVariable Long id) {
        logger.info("action count name by blacklistName: " + name);
        Map<String, Object> map = new HashMap<String, Object>();
        Long count = blackListFileService.countByNameAndStatusAndIdIsNot(name,id);
        logger.info("count: " + count);
        if (count > 0) map.put("result", true);
        else map.put("result", false);
        return map;
    }

    @PreAuthorize("hasAuthority('detail:blacklist')")
    @GetMapping(value = "/{id}")
    @ResponseBody
    public String findById(@PathVariable Long id) {
        SimpleResponseDTO res = new SimpleResponseDTO();
        try {
            Optional<BlackListFile> blackListOptional = blackListFileService.findById(id);
            if (blackListOptional.isPresent()) {
                logger.info("Found black list+ id = {}", id);
                res.setCode(ErrorCodeEnum.SUCCESS.getCode());
                res.setData(blackListOptional.get());
            }

        } catch (Exception ex) {
            logger.error("Error while find user: ", ex);
        }
        res.setCode(ErrorCodeEnum.SUCCESS.getCode());
        res.setMessage("Lấy thông tin danh sách thuê bao loại trừ thành công");
        return AppUtils.ObjectToJsonResponse(res);
    }

    @GetMapping("/downloadFileUpload/{id}")
    public void downloadFileUpload(@PathVariable("id") Long id, HttpServletResponse response) {
        Optional<BlackListFile> blackListOptional = blackListFileService.findById(id);
        BlackListFile blackListFile = new BlackListFile();
        if (blackListOptional.isPresent()) {
            blackListFile = blackListOptional.get();
        }
        File file = null;
        try {
            file = new File(blackListFile.getBlackListPathFile());
        } catch (Exception e) {
            logger.info("file not found!");
            e.printStackTrace();
        }
        byte[] data;
        try {
            Date dateN = new Date();
            String date = AppUtils.convertDateToString(new Date(), "dd/MM/yyyy hhmmss");
            data = FileUtils.readFileToByteArray(file);
            String typeFile = "Danh_sach_blacklist_" + blackListFile.getName() + "_" + date + ".csv";
            String filename = "";
            filename += typeFile + ".csv";
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            response.setContentLength(data.length);
            InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(data));
            FileCopyUtils.copy(inputStream, response.getOutputStream());

        } catch (Exception e) {
            logger.info("error when download file template!");
            logger.info(e);
        }
    }

    @PreAuthorize("hasAuthority('update:blacklist')")
    @RequestMapping(value = "/update", method = RequestMethod.POST, consumes = {"multipart/form-data"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String update(@RequestParam(required = false) Long blacklistId,
                         @RequestParam(required = false) String nameBlackList,
                         @RequestParam(required = false) String status,
                         @RequestParam(required = false, name = "dataBlackList") MultipartFile dataBlackList
    ) {
        SimpleResponseDTO res = new SimpleResponseDTO();
        HashMap<String, String> reqParams = new HashMap<>();
        reqParams.put("id", String.valueOf(blacklistId));
        reqParams.put("name", nameBlackList);
        reqParams.put("status", status);
        try {
            Optional<BlackListFile> blackListOptional = blackListFileService.findById(blacklistId);
            if (blackListOptional.isPresent()) {
                UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                blackListFileService.editBlackList(blackListOptional.get(), reqParams, dataBlackList, currentUser.getUsername());
                res.setCode(ErrorCodeEnum.SUCCESS.getCode());
                String rawString = "Chỉnh sửa danh sách thuê bao loại trừ thành công";
                byte[] bytes = rawString.getBytes(StandardCharsets.UTF_8);

                String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);
                res.setMessage(utf8EncodedString);
                return AppUtils.ObjectToJsonResponse(res);
            }
        } catch (Exception ex) {
            logger.error("Error while update user: ", ex);
            Throwable rootcause = AppUtils.getrootcause(ex);
            if (rootcause instanceof AppException) {
                AppException apex = (AppException) rootcause;
                res.setMessage(apex.getMessage());
            } else {
                res.setMessage("Có lỗi xảy ra, chỉnh sửa danh sách thuê bao loại trừ không thành công");
            }
        }
        res.setCode(ErrorCodeEnum.SUCCESS.getCode());
        logger.error("Khong tim thay black list id={}", blacklistId);
        return AppUtils.ObjectToJsonResponse(res);
    }
}
