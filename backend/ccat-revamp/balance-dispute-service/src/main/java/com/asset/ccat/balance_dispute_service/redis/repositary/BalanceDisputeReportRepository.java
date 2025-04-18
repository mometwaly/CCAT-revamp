package com.asset.ccat.balance_dispute_service.redis.repositary;

import com.asset.ccat.balance_dispute_service.dto.responses.BalanceDisputeReportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class BalanceDisputeReportRepository {

    private final String TABLE_KEY = "BALANCE_DISPUTE_REPORT";
    private final HashOperations<String, Integer, BalanceDisputeReportResponse> hashOperations;
    private final RedisTemplate<String, BalanceDisputeReportResponse> redisTemplate;

    @Autowired
    public BalanceDisputeReportRepository(RedisTemplate<String, BalanceDisputeReportResponse> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public List<BalanceDisputeReportResponse> findBySubscriber(String msisdn) {
        return hashOperations.values(TABLE_KEY + "_" + msisdn);
    }

    public BalanceDisputeReportResponse findById(String msisdn, Integer id) {
        return hashOperations.get(TABLE_KEY + "_" + msisdn, id);
    }

//    public List<SubscriberActivityModel> findBySubscriberAndActivityType(String subscriber, String activityType){
//        hashOperations.g
//    }

    public void saveAll(String msisdn, HashMap<Integer, BalanceDisputeReportResponse> history) {
        hashOperations.putAll(TABLE_KEY + "_" + msisdn, history);
    }

    public void deleteBySubscriber(String msisdn) {
        redisTemplate.delete(TABLE_KEY + "_" + msisdn);
    }


}
