/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.ccat.ods_service.controllers;

import com.asset.ccat.ods_service.defines.Defines;
import com.asset.ccat.ods_service.defines.ErrorCodes;
import com.asset.ccat.ods_service.exceptions.ODSException;
import com.asset.ccat.ods_service.logger.CCATLogger;
import com.asset.ccat.ods_service.models.requests.DSSReportRequest;
import com.asset.ccat.ods_service.models.responses.BaseResponse;
import com.asset.ccat.ods_service.models.responses.DSSResponseModel;
import com.asset.ccat.ods_service.services.TrafficBehaviorService;
import com.asset.ccat.ods_service.services.TrafficBehaviourService;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

/**
 *
 * @author wael.mohamed
 */
@RestController
@RequestMapping(value = Defines.ContextPaths.DSS_REPORT)
public class TrafficBehaviorController {

    @Autowired
    private TrafficBehaviourService trafficBehaviorService;

    @PostMapping(value = Defines.ContextPaths.TRAFFIC_BEHAVIOR + Defines.WEB_ACTIONS.GET_ALL)
    public BaseResponse<DSSResponseModel> getTrafficReport(@RequestBody DSSReportRequest request) throws ODSException, SQLException {
        ThreadContext.put("sessionId", request.getSessionId());
        ThreadContext.put("requestId", request.getRequestId());
        CCATLogger.DEBUG_LOGGER.debug("Start getting Traffic Behaviour for MSISDN = {} -- DateFrom = {}, DateTo = {}", request.getMsisdn(), request.getDateFrom(), request.getDateTo());
        DSSResponseModel result = trafficBehaviorService.getReport(request);
        CCATLogger.DEBUG_LOGGER.debug("Get Contract Balance request ended.");
        return new BaseResponse<>(ErrorCodes.SUCCESS.SUCCESS, "Success", Defines.SEVERITY.CLEAR, result);
    }

}
