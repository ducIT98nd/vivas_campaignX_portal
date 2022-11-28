package com.vivas.campaignx.dto;





import com.vivas.campaignx.entity.FrequencyCampaign;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class FrequencyCampaignDTO extends FrequencyCampaign {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private String accountSendName;
    private String productName;



}

