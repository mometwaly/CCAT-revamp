/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.ccat.ods_service.services;

import com.asset.ccat.ods_service.database.dao.AccountHistoryDao;
import com.asset.ccat.ods_service.exceptions.ODSException;
import com.asset.ccat.ods_service.logger.CCATLogger;
import com.asset.ccat.ods_service.models.SubscriberActivityModel;
import com.asset.ccat.ods_service.models.requests.AccountHistoryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * @author wael.mohamed
 */
@Service
public class AccountHistoryService {
    private final AccountHistoryDao accountHistoryDao;

    @Autowired
    public AccountHistoryService(AccountHistoryDao accountHistoryDao) {
        this.accountHistoryDao = accountHistoryDao;
    }

    public List<SubscriberActivityModel> getAccountHistory(AccountHistoryRequest request) throws ODSException {
        List<SubscriberActivityModel> activityModels = accountHistoryDao.getAccountHistory(request.getMsisdn(), request.getDateFrom(), request.getDateTo());
        sortByDateDesc(activityModels);
        return activityModels;
    }

    private void sortByDateDesc(List<SubscriberActivityModel> activityModels) {
        try {
            CCATLogger.DEBUG_LOGGER.debug("Number of activities = {}", activityModels.size());
            activityModels.sort(Comparator.comparing(SubscriberActivityModel::getDate).reversed());
        } catch (Exception ex) {
            CCATLogger.DEBUG_LOGGER.error("Exception while sorting activities.. ", ex);
            CCATLogger.ERROR_LOGGER.error("Exception while sorting activities.. ", ex);
        }
    }

}
