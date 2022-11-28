package com.vivas.campaignx.rabbitMQ.consumer;

import com.vivas.campaignx.common.AppUtils;
import com.vivas.campaignx.config.UserPrinciple;
import com.vivas.campaignx.dto.BigdataDTO;
import com.vivas.campaignx.dto.CountMSISDNDTO;
import com.vivas.campaignx.dto.NotifyTargetGroupDTO;
import com.vivas.campaignx.entity.Notify;
import com.vivas.campaignx.repository.NotifyRepository;
import com.vivas.campaignx.service.UsersService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConsumerClickHouseAdapter {

    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private NotifyRepository notifyRepository;

    @Autowired
    private UsersService usersService;
    /*@Autowired
    @Qualifier(value="taskExec")
    Executor executor;*/

    @RabbitListener(queues = "#{queueNotify}")
    public void processQueueNotify(String data) {
        logger.info("receive from rabbitMQ: " + data);
        try {
            JSONObject jsonData = new JSONObject(data);
            String key = jsonData.getString("key");
            if (key.equals("notify")) {
                try {
                    BigdataDTO bigdataDTO = new BigdataDTO();
                    this.template.convertAndSend("/topic/notify", bigdataDTO);
                } catch (Exception e) {
                    logger.error("error: ", e);
                }

            } else if (key.equals("countMsisdn")) {
                try {
                    Long targetGroupId = jsonData.getLong("targetGroupID");
                    Long count = jsonData.getLong("data");
                    Double ratio = jsonData.getDouble("ratio");
                    CountMSISDNDTO dto = new CountMSISDNDTO();
                    dto.setTargetGroupID(targetGroupId);
                    dto.setCount(count);
                    dto.setRatio(ratio);
                    this.template.convertAndSend("/topic/countMSISDN", dto);
                } catch (Exception e) {
                    logger.error("error: ", e);
                }
            }
        }catch (Exception e){
            logger.error("error: ", e);
        }
    }



}
