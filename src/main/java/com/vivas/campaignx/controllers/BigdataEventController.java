package com.vivas.campaignx.controllers;

import com.vivas.campaignx.dto.EventDataDTO;
import com.vivas.campaignx.entity.BigdataEvent;
import com.vivas.campaignx.entity.EventCondition;
import com.vivas.campaignx.service.BigdataEventService;
import com.vivas.campaignx.service.EventConditionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/event")
public class BigdataEventController {

    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    @Autowired
    private BigdataEventService bigdataEventService;

    @Autowired
    private EventConditionService eventConditionService;

    @RequestMapping(value = "/get-list-active", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getListEvent() {
        List<BigdataEvent> sendingAccountList = bigdataEventService.findByStatus(1);
        ResponseEntity responseEntity = new ResponseEntity<>(sendingAccountList, HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(value = "/get-data", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getEventCondition(@RequestParam Long eventId) {

        EventDataDTO eventDataDTO = new EventDataDTO();
        BigdataEvent bigdataEvent = bigdataEventService.findById(eventId).get();
        eventDataDTO.setEventName(bigdataEvent.getEventName());
        eventDataDTO.setEventQueueName(bigdataEvent.getQueueName());
        List<EventCondition> eventConditions = eventConditionService.findByStatusAndBigdataEventId(1, eventId);
        eventDataDTO.setEventConditions(eventConditions);;
        ResponseEntity responseEntity = new ResponseEntity<>(eventDataDTO, HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(value = "/load-condition-selectbox", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getConditionHtml() {

        return "event-campaign/event-condition";
    }
}
