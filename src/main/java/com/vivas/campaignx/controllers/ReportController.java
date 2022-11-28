package com.vivas.campaignx.controllers;

import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.dto.CampaignManagerDto;
import com.vivas.campaignx.dto.CmsReportByCampTypeCampCodeDto;
import com.vivas.campaignx.entity.*;
import com.vivas.campaignx.export.ExcelFileExporter;
import com.vivas.campaignx.repository.CmsReportByCampTypeCampCodeRepository;
import com.vivas.campaignx.service.OverviewService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/ReportController")
public class ReportController {
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());
    @Autowired
    private OverviewService overviewService;

    @Autowired
    private CmsReportByCampTypeCampCodeRepository cmsReportByCampTypeCampCodeRepository;

    @Value("${superset.url}")
    private String supersetUrl;

    @GetMapping("/findAll")
    public String findAll(Model model, @RequestParam(required = false) Integer type,
                          @RequestParam(required = false) String campaignName,
                          @RequestParam(required = false) String startDate,
                          @RequestParam(required = false) String endDate,
                          @RequestParam(required = false) Integer status
    ) {
        List<CampaignManagerDto> campaignManagerDtoList = new ArrayList<>();
        if (type == null && campaignName == "" && startDate == null && endDate == null && status == null) {
            campaignManagerDtoList = overviewService.findAll();
        } else {
            campaignManagerDtoList = overviewService.findByCampaign(type, campaignName, startDate, endDate, status);
        }
        model.addAttribute("campaignManagerDtoList", campaignManagerDtoList);
        model.addAttribute("size", campaignManagerDtoList.size());
        model.addAttribute("supersetUrl", supersetUrl);
        logger.info("size:", campaignManagerDtoList.size());
        return "report/listCampaign";

    }

    @GetMapping("/changeCampaign")
    public String changeCampaign(Model model, @RequestParam(required = false) Integer type,
                                 @RequestParam(required = false) Integer status
    ) {
        List<CampaignManagerDto> campaignManagerDtoList = new ArrayList<>();
        if (type == null && status == null) {
            campaignManagerDtoList = overviewService.findAll();
        } else {
            campaignManagerDtoList = overviewService.changeCampaign(type, status);
        }
        model.addAttribute("campaignManagerDtoList", campaignManagerDtoList);
        model.addAttribute("size", campaignManagerDtoList.size());
        model.addAttribute("supersetUrl", supersetUrl);
        logger.info("size:", campaignManagerDtoList.size());
        return "report/changeCampaign";

    }

    @PreAuthorize("hasAnyAuthority('report:reportallcampaign','report:reportonecampaign')")
    @GetMapping("/overview")
    public String view(Model model, @RequestParam(required = false) String campaignName,
                       @RequestParam(required = false) String startDate,
                       @RequestParam(required = false) String startDateMonth,
                       @RequestParam(required = false) String endDate,
                       @RequestParam(required = false) String endDateMonth,
                       @RequestParam(required = false) Integer type,
                       @RequestParam(required = false) Integer status,
                       @RequestParam(required = false) Integer chartType,
                       @RequestParam(required = false) Integer pageSize,
                       @RequestParam(required = false) Integer currentPage,
                       @RequestParam(required = false) Integer checkCampaign
    ) {
        if (pageSize == null)
            pageSize = 20;
        if (currentPage == null)
            currentPage = 1;
        List<CampaignManagerDto> campaignManagerDtoList = overviewService.findAll();
        Page<CmsReportByCampTypeCampCode> dataPage = null;
        CmsReportByCampTypeCampCodeDto campTypeCampCodeDto = null;
        if(startDate == null && endDate == null) {
            campTypeCampCodeDto = overviewService.sumAll();
            dataPage = overviewService.findByReportOverview(type, status, campaignName, startDate, endDate, pageSize, currentPage);
        }
        else if(startDate != null && endDate != null) {
            campTypeCampCodeDto = overviewService.sum(type,status,campaignName,startDate,endDate);
            dataPage = overviewService.findByReportOverview(type, status, campaignName, startDate, endDate, pageSize, currentPage);
        }
        else if(startDateMonth != null && endDateMonth != null) {
            campTypeCampCodeDto = overviewService.sumMonth(type,status,campaignName,startDate,endDate);
            dataPage = overviewService.findByReportOverviewMonth(type, status, campaignName, startDate, endDate, pageSize, currentPage);

        }
        model.addAttribute("dataPage", dataPage);
        model.addAttribute("totalPages", dataPage.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("campaignName", campaignName);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("campaignManagerDtoList", campaignManagerDtoList);
        model.addAttribute("size", campaignManagerDtoList.size());
        model.addAttribute("chartType", chartType);
        model.addAttribute("status", status);
        model.addAttribute("type", type);
        model.addAttribute("supersetUrl", supersetUrl);
        model.addAttribute("checkCampaign", checkCampaign);
        model.addAttribute("sumInvitationMessage", campTypeCampCodeDto.getSUM_INVITATION_MESSAGE());
        model.addAttribute("sumSuccessMessage", campTypeCampCodeDto.getSUM_SUCCESS_MESSAGE());
        model.addAttribute("sumRegisterMessage", campTypeCampCodeDto.getSUM_REGISTER_MESSAGE());
        model.addAttribute("sumSuccessRegisterMessage", campTypeCampCodeDto.getSUM_SUCCESS_REGISTER_MESSAGE());
        model.addAttribute("sumRevenue", campTypeCampCodeDto.getSUM_REVENUE());
        return "report/reportOverview";
    }


    @PreAuthorize("hasAuthority('report:packdata')")
    @GetMapping("/overviewPackage")
    public String viewPackager(Model model,@RequestParam(required = false) String name,
                               @RequestParam(required = false) String startDate,
                               @RequestParam(required = false) String endDate,
                               @RequestParam(required = false) String startDateMonth,
                               @RequestParam(required = false) String endDateMonth,
                               @RequestParam(required = false) Integer chartType,
                               @RequestParam(required = false) Integer date,
                               @RequestParam(required = false) Integer pageSize,
                               @RequestParam(required = false) Integer currentPage) {
        if (pageSize == null)
            pageSize = 20;
        if (currentPage == null)
            currentPage = 1;
        List<PackageData> packageDataList = overviewService.findAllPackageData();
        Page<CmsReportByCampTypeCampCodePackage> dataPage = null;
        CmsReportByCampTypeCampCodeDto campTypeCampCodeDto = null;
        if (startDate == null && endDate == null) {
            dataPage = overviewService.findAll(pageSize,currentPage);
            campTypeCampCodeDto = overviewService.exportAllSumPackage();
        } else if(startDate != null && endDate != null) {
            dataPage = overviewService.findByReportPackage(name,startDate,endDate,pageSize,currentPage);
            campTypeCampCodeDto = overviewService.exportSumPackage(name,startDate,endDate);
        } else if(startDateMonth != null && endDateMonth != null) {
            dataPage = overviewService.findByReportPackageMonth(name,startDateMonth,endDateMonth,pageSize,currentPage);
            campTypeCampCodeDto = overviewService.exportSumPackageMonth(name,startDateMonth,endDateMonth);
        }
        model.addAttribute("packageDataList", packageDataList);
        model.addAttribute("supersetUrl", supersetUrl);
        model.addAttribute("sumInvitationMessage", campTypeCampCodeDto.getSUM_INVITATION_MESSAGE());
        model.addAttribute("sumSuccessMessage", campTypeCampCodeDto.getSUM_SUCCESS_MESSAGE());
        model.addAttribute("sumRegisterMessage", campTypeCampCodeDto.getSUM_REGISTER_MESSAGE());
        model.addAttribute("sumSuccessRegisterMessage", campTypeCampCodeDto.getSUM_SUCCESS_REGISTER_MESSAGE());
        model.addAttribute("sumRevenue", campTypeCampCodeDto.getSUM_REVENUE());
        model.addAttribute("chartType", chartType);
        model.addAttribute("dataPage", dataPage);
        model.addAttribute("totalPages", dataPage.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("name", name);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("date", date);
        model.addAttribute("startDateMonth", startDateMonth);
        model.addAttribute("endDateMonth", endDateMonth);
        return "report/reportPackage";
    }

    @PreAuthorize("hasAuthority('report:downloadcampaign')")
    @RequestMapping(value = "/exportReport")
    public void exportReport(HttpServletResponse response,
                             @RequestParam(required = false) Integer type,
                             @RequestParam(required = false) Integer status,
                             @RequestParam(required = false) String name,
                             @RequestParam(required = false) String startDate,
                             @RequestParam(required = false) String endDate) throws IOException {

        logger.info(String.format("export info: type=%s, status=%s, name=%s,startDate=%s, endDate=%s", type, status, name, startDate, endDate));
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dateString = df.format(date);
        String fileName = "Danh sach nhom doi tuong" + "_" + dateString + ".xlsx";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        Date createdDate1 = null;
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        List<CmsReportByCampTypeCampCode> cmsReportByCampTypeCampCodeList = null;
        CmsReportByCampTypeCampCodeDto cmsReportByCampTypeCampCode = null;
        if ((name == null || name.equals("")) && startDate.equals("") && endDate.equals("")) {
            cmsReportByCampTypeCampCodeList = overviewService.exportAll();
            cmsReportByCampTypeCampCode = overviewService.sumAll();
        } else {
            cmsReportByCampTypeCampCodeList = overviewService.export(type, status, name, startDate, endDate);
            cmsReportByCampTypeCampCode = overviewService.sum(type, status, name, startDate, endDate);
        }
        logger.info("sum:" + cmsReportByCampTypeCampCode.getSUM_INVITATION_MESSAGE());
        ByteArrayInputStream stream = ExcelFileExporter.reportListToExcelFile(cmsReportByCampTypeCampCodeList, cmsReportByCampTypeCampCode);
        IOUtils.copy(stream, response.getOutputStream());
    }

    @PreAuthorize("hasAuthority('report:downloadpackdata')")
    @RequestMapping(value = "/exportPackage")
    public void exportPackage(HttpServletResponse response,
                              @RequestParam(required = false) String name,
                              @RequestParam(required = false) String startDate,
                              @RequestParam(required = false) String endDate
                              ) throws IOException {

        logger.info(String.format("export info: name=%s,startDate=%s, endDate=%s", name, startDate, endDate));
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dateString = df.format(date);
        String fileName = "Danh sach nhom doi tuong" + "_" + dateString + ".xlsx";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        Date createdDate1 = null;
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        List<CmsReportByCampTypeCampCodePackage> cmsReportByCampTypeCampCodePackages = null;
        CmsReportByCampTypeCampCodeDto cmsReportByCampTypeCampCode = null;
        if (name == null) {
            cmsReportByCampTypeCampCodePackages = overviewService.exportAllPackage();
            cmsReportByCampTypeCampCode = overviewService.exportAllSumPackage();
        } else {
            cmsReportByCampTypeCampCodePackages = overviewService.exportPackage(name, startDate, endDate);
            cmsReportByCampTypeCampCode = overviewService.exportSumPackage(name, startDate, endDate);
        }
//        logger.info("sum:" +cmsReportByCampTypeCampCode.getSUM_INVITATION_MESSAGE());
        ByteArrayInputStream stream = ExcelFileExporter.reportPackageToExcelFile(cmsReportByCampTypeCampCodePackages, cmsReportByCampTypeCampCode);
        IOUtils.copy(stream, response.getOutputStream());
    }

    @PreAuthorize("hasAuthority('report:runner')")
    @GetMapping("/running")
    public String viewRunning(Model model) {
        List<CampaignManagerDto> campaignManagerDtoList = overviewService.findAll();
        model.addAttribute("campaignManagerDtoList", campaignManagerDtoList);
        model.addAttribute("supersetUrl", supersetUrl);
        return "report/runningScheduleOverview";
    }


}
