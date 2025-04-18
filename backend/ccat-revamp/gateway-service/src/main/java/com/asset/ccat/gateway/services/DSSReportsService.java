/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.ccat.gateway.services;

import com.asset.ccat.gateway.exceptions.GatewayException;
import com.asset.ccat.gateway.logger.CCATLogger;
import com.asset.ccat.gateway.models.customer_care.DSSReportModel;
import com.asset.ccat.gateway.models.requests.customer_care.history.DSSReportRequest;
import com.asset.ccat.gateway.models.requests.report.GetContractBillReportRequest;
import com.asset.ccat.gateway.models.requests.report.GetDssReportRequest;
import com.asset.ccat.gateway.models.shared.PaginationModel;
import com.asset.ccat.gateway.proxy.DSSProxy;
import com.asset.ccat.gateway.redis.model.DSSRedisModel;
import com.asset.ccat.gateway.redis.repository.DSSRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

/**
 * @author wael.mohamed
 */
@Component
public class DSSReportsService {

    private final DSSRepository dssRepository;

    private final DSSProxy dSSProxy;

    public DSSReportsService(DSSRepository dssRepository, DSSProxy dSSProxy) {
        this.dssRepository = dssRepository;
        this.dSSProxy = dSSProxy;
    }

    public DSSReportModel getTrafficBehaviorReport(DSSReportRequest request) throws GatewayException, JsonProcessingException {
        DSSReportModel report = dSSProxy.getTrafficBehaviorReport(request);
        return checkPagination(request.getPagination(), report);
    }

    public DSSReportModel getBtiVrReport(DSSReportRequest request) throws GatewayException, JsonProcessingException {
        DSSReportModel report = dSSProxy.getBtiVrReport(request);
        return checkPagination( request.getPagination(),report);
    }

    public DSSReportModel getUSSDReport(DSSReportRequest request) throws GatewayException, JsonProcessingException {
        DSSReportModel report = dSSProxy.getUSSDReport(request);
        return checkPagination( request.getPagination() ,report);
    }

    public DSSReportModel getVodafoneOneRedeemReport(DSSReportRequest request) throws GatewayException, JsonProcessingException {
        DSSReportModel report = dSSProxy.getVodafoneOneRedeemReport(request);
        return checkPagination( request.getPagination() ,report);
    }

	public DSSReportModel getContractBillReport(GetContractBillReportRequest request) throws GatewayException, JsonProcessingException {
        DSSReportModel report = dSSProxy.getContractBillReport(request);
        return checkPagination( request.getPagination() ,report);
    }
    public DSSReportModel getVodafoneOneProfileReport(DSSReportRequest request) throws GatewayException, JsonProcessingException {
        DSSReportModel report = dSSProxy.getVodafoneOneProfileReport(request);
        return checkPagination( request.getPagination() ,report);
    }
    public DSSReportModel getContractBalanceReport(DSSReportRequest request) throws GatewayException, JsonProcessingException {
        DSSReportModel report = dSSProxy.getContractBalanceReport(request);
        return checkPagination( request.getPagination() ,report);
    }
    public DSSReportModel getContractBalanceTransferReport(DSSReportRequest request) throws GatewayException, JsonProcessingException {
        DSSReportModel report = dSSProxy.getContractBalanceTransferReport(request);
        return checkPagination( request.getPagination() ,report);
    }
	public DSSReportModel getVisitedUrlReport(DSSReportRequest request) throws GatewayException, JsonProcessingException {
        DSSReportModel report = dSSProxy.getVisitedUrlReport(request);
        return checkPagination( request.getPagination() ,report);
    }

    public DSSReportModel getComplaintViewReport(DSSReportRequest request) throws GatewayException, JsonProcessingException {
        DSSReportModel report = dSSProxy.getComplaintViewReport(request);
        return checkPagination( request.getPagination() ,report);
    }
    public DSSReportModel getETopUpTransactionsReport(DSSReportRequest request) throws GatewayException, JsonProcessingException {
        DSSReportModel report = dSSProxy.getETopUpTransactionsReport(request);
        return checkPagination( request.getPagination() ,report);
    }

	 public DSSReportModel getOutgoingViewReport(DSSReportRequest request) throws GatewayException, JsonProcessingException {
        DSSReportModel report = dSSProxy.getOutgoingViewReport(request);
        return checkPagination( request.getPagination() ,report);
    }
	    public DSSReportModel checkPagination(PaginationModel paginationModel , DSSReportModel report){
        List<Map<Integer, String>> data = report.getData();
        report.setTotalNumberOfActivities(report.getData().size());
        if(paginationModel!= null){
            if(paginationModel.getOrder() != null){
                data = sortData(report , paginationModel);
            }
            int offset = paginationModel.getOffset() == null ? 0 : paginationModel.getOffset();
            int fetchCount = paginationModel.getFetchCount() == null ? report.getData().size() : paginationModel.getFetchCount();


            //validate that offset is less than or equal to the size of the list
            offset = Math.max(0, Math.min(offset, report.getData().size()));
            //validate that fetchCount is less than the list size
            fetchCount = Math.max(0, Math.min(fetchCount, report.getData().size() - offset));

            CCATLogger.DEBUG_LOGGER.debug("Start get Data from List with offset: " + offset + ", fetchCount: " + fetchCount );
            data = report.getData().subList(offset, Math.min(report.getData().size(), offset + fetchCount));
            CCATLogger.DEBUG_LOGGER.debug("Size of Data: " + data.size());
            report.setData(data);
        }

        return report;
    }

    public List<Map<Integer, String>> sortData(DSSReportModel report, PaginationModel paginationModel) {
        int index = -1;
        String sortedBy = paginationModel.getSortedBy();
        index = report.getHeaders().entrySet().stream()
                .filter(entry -> entry.getValue().equals(sortedBy))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(-1);

        if (index != -1) {
            int finalIndex = index;
            Comparator<Map<Integer, String>> comparator = Comparator.comparing(
                    map -> map.get(finalIndex),
                    Comparator.nullsFirst((a, b) -> {
                        boolean isANumeric = isNumeric(a);
                        boolean isBNumeric = isNumeric(b);

                        if (isANumeric && isBNumeric) {
                            // Both are numeric, compare numerically
                            return Integer.compare(Integer.parseInt(a), Integer.parseInt(b));
                        } else if (isANumeric) {
                            // Numeric values come before non-numeric values
                            return -1;
                        } else if (isBNumeric) {
                            return 1;
                        } else {
                            // Both are non-numeric, compare lexicographically
                            return a.compareTo(b);
                        }
                    })
            );

            if (paginationModel.getOrder() == 1) {
                report.getData().sort(comparator);
            } else if (paginationModel.getOrder() == 2) {
                report.getData().sort(comparator.reversed());
            }
        }
        return report.getData();
    }

    private boolean isNumeric(String str) {
        if (str == null)
            return false;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // GENERIC METHOD
    // TODO :: TEST METHOD
    public DSSReportModel getReport(DSSReportRequest request, String pageName, Function<DSSReportRequest, DSSReportModel> fetchReportFunction)
            throws GatewayException, JsonProcessingException {
        DSSReportModel report;
        ObjectMapper mapper = new ObjectMapper();
        DSSRedisModel redisModel = dssRepository.findById(request.getMsisdn())
                .orElse(null);

        if (redisModel == null || pageName.equalsIgnoreCase(redisModel.getDssPageName())) {
            // Fetch report using the provided function
            report = fetchReportFunction.apply(request);
            redisModel = new DSSRedisModel(request.getMsisdn(), report);

            // Serialize data to JSON and cache it
            String json = mapper.writeValueAsString(report.getData());
            redisModel.setJsonData(json);
            redisModel.setDssPageName(pageName);
            dssRepository.save(redisModel);
        } else {
            // Retrieve the report from the cache
            report = redisModel.getDssReportModel();
            List<Map<Integer, String>> data = mapper.readValue(redisModel.getJsonData(),
                    new TypeReference<List<Map<Integer, String>>>() {});
            report.setData(data);
        }

        // Handle pagination
        if (request.getPagination() != null) {
            int offset = request.getPagination().getOffset() == null ? 0 : request.getPagination().getOffset();
            int fetchCount = request.getPagination().getFetchCount() == null ? report.getData().size() : request.getPagination().getFetchCount();

            //validate that offset is less than or equal to the size of the list
            offset = Math.max(0, Math.min(offset, report.getData().size()));
            //validate that fetchCount is less than the list size
            fetchCount = Math.max(0, Math.min(fetchCount, report.getData().size() - offset));

            List<Map<Integer, String>> data = report.getData().subList(offset, Math.min(report.getData().size(), offset + fetchCount));
            report.setData(data);
        }

        return report;
    }


    public DSSReportModel getGeneralReportData(GetDssReportRequest request) throws GatewayException {
        CCATLogger.DEBUG_LOGGER.debug("Get Dss Service Report Data Start");
        DSSReportModel vodafoneOneProfile = new DSSReportModel();
        vodafoneOneProfile.setExternalCode(1);
        vodafoneOneProfile.setExternalDescription("external description");
        Map<Integer, String> vodafoneOneProfileHeader = new HashMap<Integer, String>();
        vodafoneOneProfileHeader.put(1, "TRANSFER_DATE");
        vodafoneOneProfileHeader.put(2, "TRANSACTION_WAY");
        vodafoneOneProfileHeader.put(3, "TRANSACTION_TYPE");
        vodafoneOneProfileHeader.put(4, "TRANSITION_CATEGORY");
        vodafoneOneProfileHeader.put(5, "CHANNEL_TYPE");
        vodafoneOneProfileHeader.put(6, "SENDER_MSISDN");
        vodafoneOneProfileHeader.put(7, "RECEIVER_MSISDN");
        vodafoneOneProfileHeader.put(8, "SENDER_CATEGORY");
        vodafoneOneProfileHeader.put(9, "RECEIVER_CATEGORY");
        vodafoneOneProfileHeader.put(10, "TRANSFER_AMOUNT");
        vodafoneOneProfileHeader.put(11, "PRODUCT");
        vodafoneOneProfileHeader.put(12, "REQUEST_SOURCE");
        List<Map<Integer, String>> vodafoneOneProfileData = new ArrayList<Map<Integer, String>>() ;
        vodafoneOneProfile.setHeaders(vodafoneOneProfileHeader);
        CCATLogger.DEBUG_LOGGER.debug("Headers set");
        for(Integer i=1 ;i<5;i++){
            Map<Integer, String> vodafoneOneProfileDataTemp = new HashMap<Integer, String>();
            vodafoneOneProfileDataTemp.put(1, "1010101225");
            vodafoneOneProfileDataTemp.put(2, "50");
            vodafoneOneProfileDataTemp.put(3, "Cat1");
            vodafoneOneProfileDataTemp.put(4, "PRD1");
            vodafoneOneProfileDataTemp.put(5, "trxCategory1");
            vodafoneOneProfileDataTemp.put(6, "chnlType1");
            vodafoneOneProfileDataTemp.put(7, "req1");
            vodafoneOneProfileDataTemp.put(8, "1010101224");
            vodafoneOneProfileDataTemp.put(9, "2014-10-19 00:00:00.0");
            vodafoneOneProfileDataTemp.put(10, "Test1");
            vodafoneOneProfileDataTemp.put(11, "trxType1");
            vodafoneOneProfileDataTemp.put(12, "RCat1");
            vodafoneOneProfileData.add(vodafoneOneProfileDataTemp);
        }
        vodafoneOneProfile.setData(vodafoneOneProfileData);
        CCATLogger.DEBUG_LOGGER.debug("data set");

        vodafoneOneProfile.setTotalNumberOfActivities(100);
        CCATLogger.DEBUG_LOGGER.debug("total number of activities set");

        return vodafoneOneProfile;
    }

}
