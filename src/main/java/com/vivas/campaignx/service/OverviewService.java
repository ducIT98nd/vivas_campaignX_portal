package com.vivas.campaignx.service;

import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.dto.CampaignDTO;
import com.vivas.campaignx.dto.CampaignManagerDto;
import com.vivas.campaignx.dto.CmsReportByCampTypeCampCodeDto;
import com.vivas.campaignx.entity.*;
import com.vivas.campaignx.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OverviewService {
    @Autowired
    private FrequencyCampaignRepository frequencyCampaignRepository;

    @Autowired
    private EventCampaignRepository eventCampaignRepository;

    @Autowired
    private CmsReportByCampTypeCampCodeRepository cmsReportByCampTypeCampCodeRepository;

    @Autowired
    private CmsReportByCampTypeCampCodePackageRepository cmsReportByCampTypeCampCodePackageRepository;

    @Autowired
    private PackageDataRepository packageDataRepository;

    public List<CampaignManagerDto> findAll() {
        List<Integer> list = Arrays.asList(0, 4, 6);
        List<EventCampaign> findAllEventCampaign = eventCampaignRepository.findAllByStatusIn(list);
        List<FrequencyCampaign> findAllFrequencyCampaign = frequencyCampaignRepository.findAllByStatusIn(list);
        List<CampaignManagerDto> campaignManagerDtoList = new ArrayList<>();

        for (EventCampaign eventCampaign : findAllEventCampaign) {
            CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
            convertCampaignEventToDto(campaignManagerDto, eventCampaign);
            campaignManagerDtoList.add(campaignManagerDto);
        }
        for (FrequencyCampaign frequencyCampaign : findAllFrequencyCampaign) {
            CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
            convertCampaignFrequency(campaignManagerDto, frequencyCampaign);
            campaignManagerDtoList.add(campaignManagerDto);
        }
        return campaignManagerDtoList;
    }


    public List<CampaignManagerDto> findByCampaign(Integer type, String campaignName,
                                                   String startDate, String endDate,
                                                   Integer status) {
        List<Integer> listStatus = new ArrayList<>();
        if (status == 10) {
            listStatus = Arrays.asList(0, 4, 6);
        }
        if (status == 0) listStatus.add(0);
        if (status == 4) listStatus.add(4);
        if (status == 6) listStatus.add(6);
        List<EventCampaign> findAllEventCampaign = eventCampaignRepository.searchReport(campaignName, startDate, endDate, listStatus);
        List<FrequencyCampaign> findAllFrequencyCampaign = frequencyCampaignRepository.searchReport(campaignName, startDate, endDate, listStatus);
        List<CampaignManagerDto> campaignManagerDtoList = new ArrayList<>();
        if (type == null || type == 0) {
            for (EventCampaign eventCampaign : findAllEventCampaign) {
                CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
                convertCampaignEventToDto(campaignManagerDto, eventCampaign);
                campaignManagerDtoList.add(campaignManagerDto);
            }
            for (FrequencyCampaign frequencyCampaign : findAllFrequencyCampaign) {
                CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
                convertCampaignFrequency(campaignManagerDto, frequencyCampaign);
                campaignManagerDtoList.add(campaignManagerDto);
            }
        } else if (type != null) {
            if (type == 1) {
                for (EventCampaign eventCampaign : findAllEventCampaign) {
                    CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
                    convertCampaignEventToDto(campaignManagerDto, eventCampaign);
                    campaignManagerDtoList.add(campaignManagerDto);
                }
            }
            if (type == 2) {
                for (FrequencyCampaign frequencyCampaign : findAllFrequencyCampaign) {
                    CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
                    convertCampaignFrequency(campaignManagerDto, frequencyCampaign);
                    campaignManagerDtoList.add(campaignManagerDto);
                }
            }
        }
        return campaignManagerDtoList;
    }


    public List<CampaignManagerDto> changeCampaign(Integer type,
                                                   Integer status) {
        List<Integer> listStatus = new ArrayList<>();
        if (status == 10) {
            listStatus = Arrays.asList(0, 4, 6);
        }
        if (status == 0) listStatus.add(0);
        if (status == 4) listStatus.add(4);
        if (status == 6) listStatus.add(6);
        List<EventCampaign> findAllEventCampaign = eventCampaignRepository.changeReport(listStatus);
        List<FrequencyCampaign> findAllFrequencyCampaign = frequencyCampaignRepository.changeReport(listStatus);
        List<CampaignManagerDto> campaignManagerDtoList = new ArrayList<>();
        if (type == null || type == 0) {
            for (EventCampaign eventCampaign : findAllEventCampaign) {
                CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
                convertCampaignEventToDto(campaignManagerDto, eventCampaign);
                campaignManagerDtoList.add(campaignManagerDto);
            }
            for (FrequencyCampaign frequencyCampaign : findAllFrequencyCampaign) {
                CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
                convertCampaignFrequency(campaignManagerDto, frequencyCampaign);
                campaignManagerDtoList.add(campaignManagerDto);
            }
        } else if (type != null) {
            if (type == 1) {
                for (EventCampaign eventCampaign : findAllEventCampaign) {
                    CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
                    convertCampaignEventToDto(campaignManagerDto, eventCampaign);
                    campaignManagerDtoList.add(campaignManagerDto);
                }
            }
            if (type == 2) {
                for (FrequencyCampaign frequencyCampaign : findAllFrequencyCampaign) {
                    CampaignManagerDto campaignManagerDto = new CampaignManagerDto();
                    convertCampaignFrequency(campaignManagerDto, frequencyCampaign);
                    campaignManagerDtoList.add(campaignManagerDto);
                }
            }
        }
        return campaignManagerDtoList;
    }

    public CampaignManagerDto convertCampaignEventToDto(CampaignManagerDto campaignManagerDto, EventCampaign eventCampaign) {
        campaignManagerDto.setId(eventCampaign.getCampaignId());
        campaignManagerDto.setName(eventCampaign.getCampaignName());
        campaignManagerDto.setStatus(eventCampaign.getStatus());
        campaignManagerDto.setStartDate(AppUtils.formatDefaultDate(eventCampaign.getStartDate()));
        if (eventCampaign.getEndDate() != null) {
            campaignManagerDto.setEndDate(AppUtils.formatDefaultDate(eventCampaign.getEndDate()));
        }
        campaignManagerDto.setCreatedDate(AppUtils.formatDefaultDate(eventCampaign.getCreatedDate()));
        campaignManagerDto.setCreatedDateConvert(AppUtils.formatDefaultDatetime1(eventCampaign.getCreatedDate()));
        campaignManagerDto.setUserName(eventCampaign.getCreatedUser());
        campaignManagerDto.setType(1);
        return campaignManagerDto;
    }

    public CampaignManagerDto convertCampaignFrequency(CampaignManagerDto campaignManagerDto, FrequencyCampaign frequencyCampaign) {
        campaignManagerDto.setId(frequencyCampaign.getCampaignId());
        campaignManagerDto.setName(frequencyCampaign.getCampaignName());
        campaignManagerDto.setStatus(frequencyCampaign.getStatus());
        campaignManagerDto.setStartDate(AppUtils.formatDefaultDate(frequencyCampaign.getStartDate()));
        if (frequencyCampaign.getEndDate() != null) {
            campaignManagerDto.setEndDate(AppUtils.formatDefaultDate(frequencyCampaign.getEndDate()));
        }
        campaignManagerDto.setCreatedDate(AppUtils.formatDefaultDate(frequencyCampaign.getCreatedDate()));
        campaignManagerDto.setCreatedDateConvert(AppUtils.formatDefaultDatetime1(frequencyCampaign.getCreatedDate()));
        campaignManagerDto.setUserName(frequencyCampaign.getCreatedUser());
        campaignManagerDto.setType(2);
        return campaignManagerDto;
    }

    public List<CmsReportByCampTypeCampCode> exportAll() {
        return cmsReportByCampTypeCampCodeRepository.exportAll();
    }

    public Page<CmsReportByCampTypeCampCode> findByReportOverview(Integer type, Integer status, String name, String startDate, String endDate, Integer pageSize, Integer currentPage) {
        Pageable paging = PageRequest.of(currentPage - 1, pageSize);
        List<Integer> listType = new ArrayList<>();
        List<Integer> listStatus = new ArrayList<>();
        if (type == null || type == 0) {
            listType = Arrays.asList(1, 2);
        } else if (type == 1) {
            listType.add(1);
        } else if (type == 2) {
            listType.add(2);
        }
        if (status == null || status == 10) {
            listStatus = Arrays.asList(0, 4, 6);
        } else if (status == 0) {
            listStatus = Collections.singletonList(0);
        } else if (status == 4) {
            listStatus = Collections.singletonList(4);
        } else if (status == 6) {
            listStatus = Collections.singletonList(6);
        }
        return cmsReportByCampTypeCampCodeRepository.findByReportOverview(listType, listStatus, name, startDate, endDate, paging);
    }

    public Page<CmsReportByCampTypeCampCode> findByReportOverviewMonth(Integer type, Integer status, String name, String startDate, String endDate, Integer pageSize, Integer currentPage) {
        Pageable paging = PageRequest.of(currentPage - 1, pageSize);
        List<Integer> listType = new ArrayList<>();
        List<Integer> listStatus = new ArrayList<>();
        if (type == null || type == 0) {
            listType = Arrays.asList(1, 2);
        } else if (type == 1) {
            listType.add(1);
        } else if (type == 2) {
            listType.add(2);
        }
        if (status == null || status == 10) {
            listStatus = Arrays.asList(0, 4, 6);
        } else if (status == 0) {
            listStatus = Collections.singletonList(0);
        } else if (status == 4) {
            listStatus = Collections.singletonList(4);
        } else if (status == 6) {
            listStatus = Collections.singletonList(6);
        }
        return cmsReportByCampTypeCampCodeRepository.findByReportOverviewMonth(listType, listStatus, name, startDate, endDate, paging);
    }

    public CmsReportByCampTypeCampCodeDto sumAll() {
        return cmsReportByCampTypeCampCodeRepository.sumAll();
    }

    public List<CmsReportByCampTypeCampCode> export(Integer type, Integer status, String name, String startDate, String endDate) {
        List<Integer> listType = new ArrayList<>();
        List<Integer> listStatus = new ArrayList<>();
        if (type == 0) {
            listType = Arrays.asList(1, 2);
        }
        if (type == 1) {
            listType.add(1);
        }
        if (type == 2) {
            listType.add(2);
        }
        if (status == 10) {
            listStatus = Arrays.asList(0, 4, 6);
        }
        if (status == 0) {
            listStatus = Collections.singletonList(0);
        }
        if (status == 4) {
            listStatus = Collections.singletonList(4);
        }
        if (status == 6) {
            listStatus = Collections.singletonList(6);
        }
        return cmsReportByCampTypeCampCodeRepository.export(listType, listStatus, name, startDate, endDate);
    }

    public CmsReportByCampTypeCampCodeDto sum(Integer type, Integer status, String name, String startDate, String endDate) {
        List<Integer> listType = new ArrayList<>();
        List<Integer> listStatus = new ArrayList<>();
        if (type == 0) {
            listType = Arrays.asList(1, 2);
        }
        if (type == 1) {
            listType.add(1);
        }
        if (type == 2) {
            listType.add(2);
        }
        if (status == 10) {
            listStatus = Arrays.asList(0, 4, 6);
        }
        if (status == 0) {
            listStatus = Collections.singletonList(0);
        }
        if (status == 4) {
            listStatus = Collections.singletonList(4);
        }
        if (status == 6) {
            listStatus = Collections.singletonList(6);
        }
        return cmsReportByCampTypeCampCodeRepository.sum(listType, listStatus, name, startDate, endDate);
    }

    public CmsReportByCampTypeCampCodeDto sumMonth(Integer type, Integer status, String name, String startDate, String endDate) {
        List<Integer> listType = new ArrayList<>();
        List<Integer> listStatus = new ArrayList<>();
        if (type == 0) {
            listType = Arrays.asList(1, 2);
        }
        if (type == 1) {
            listType.add(1);
        }
        if (type == 2) {
            listType.add(2);
        }
        if (status == 10) {
            listStatus = Arrays.asList(0, 4, 6);
        }
        if (status == 0) {
            listStatus = Collections.singletonList(0);
        }
        if (status == 4) {
            listStatus = Collections.singletonList(4);
        }
        if (status == 6) {
            listStatus = Collections.singletonList(6);
        }
        return cmsReportByCampTypeCampCodeRepository.sumMonth(listType, listStatus, name, startDate, endDate);
    }

    public List<PackageData> findAllPackageData() {
        return packageDataRepository.findAllByStatusNotOrderByCreatedDateDesc(99);
    }

    public List<CmsReportByCampTypeCampCodePackage> exportAllPackage() {
        return cmsReportByCampTypeCampCodePackageRepository.exportAll();
    }

    public List<CmsReportByCampTypeCampCodePackage> exportPackage(String name, String startDate, String endDate) {
        return cmsReportByCampTypeCampCodePackageRepository.export(name, startDate, endDate);
    }

    public CmsReportByCampTypeCampCodeDto exportAllSumPackage() {
        return cmsReportByCampTypeCampCodePackageRepository.sumAll();
    }

    public CmsReportByCampTypeCampCodeDto exportSumPackage(String name, String startDate, String endDate) {
        return cmsReportByCampTypeCampCodePackageRepository.sum(name, startDate, endDate);
    }

    public CmsReportByCampTypeCampCodeDto exportSumPackageMonth(String name, String startDate, String endDate) {
        return cmsReportByCampTypeCampCodePackageRepository.sumMonth(name, startDate, endDate);
    }

    public Page<CmsReportByCampTypeCampCodePackage> findByReportPackage(String name, String startDate, String endDate, Integer pageSize, Integer currentPage) {
        Pageable paging = PageRequest.of(currentPage - 1, pageSize);
        return cmsReportByCampTypeCampCodePackageRepository.findByCampTypeCampCodePackage(name,startDate,endDate,paging);
    }

    public Page<CmsReportByCampTypeCampCodePackage> findByReportPackageMonth(String name, String startDate, String endDate, Integer pageSize, Integer currentPage) {
        Pageable paging = PageRequest.of(currentPage - 1, pageSize);
        return cmsReportByCampTypeCampCodePackageRepository.findByCampTypeCampCodePackageMonth(name,startDate,endDate,paging);
    }

    public Page<CmsReportByCampTypeCampCodePackage> findAll(Integer pageSize, Integer currentPage) {
        Pageable paging = PageRequest.of(currentPage - 1, pageSize);
        return cmsReportByCampTypeCampCodePackageRepository.findAll(paging);
    }
}
