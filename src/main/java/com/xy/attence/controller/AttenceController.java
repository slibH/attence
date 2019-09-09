package com.xy.attence.controller;

import com.alibaba.fastjson.JSONArray;
import com.xy.attence.work.bus.AttenceService;
import com.xy.attence.work.util.Constants;
import com.xy.attence.work.util.DataCenter;
import com.xy.attence.work.util.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping("attence")
public class AttenceController {
    @Value("${corpid}")
    private String corpid;
    @Value("${secret}")
    private String secret;

    /**
     * 获取在外签到的记录
     *
     * @return
     */
    @GetMapping("getAttence")
    public String getAttence() {
        Long currentTime = System.currentTimeMillis();//当前时间的Unix时间戳
        Long tempTime = System.currentTimeMillis() / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
        Long startTime = tempTime;//今天的00:00:00的Unix时间戳
        //获取access_token
        String accessToken = AttenceService.getAccessToken(corpid, secret);
        JSONArray result = new JSONArray();
        //查询所有的部门的签到记录,1为根部门
        JSONArray array = AttenceService.getCheckinRecord(accessToken, startTime, currentTime, "1");
        //处理数据
        DataCenter.checkinDataProcessing(array);
        result.addAll(array);

        return result.toString();
    }

    @GetMapping("getAttenceList")
    public String getAttenceList() {
        JSONArray result = new JSONArray();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String checkDateFrom = df.format(DateUtil.todayFirstDate());
        String checkDateTo = df.format(new Date());
        //获取access_token
        String accessToken = AttenceService.getAccessToken(corpid, secret);

        Map<String, Object> ecachMap = Constants.ecachMap;

        if(ecachMap.size() == 0) {
            return "";
        }

        Map<String, List> staffMap = (Map<String, List>) ecachMap.get("staff");
        List list = (List) ecachMap.get("userIdList");

        //删除重复的
        list = DataCenter.removeDuplicate(list);
        List tempList = DataCenter.listSplit(list, 49);

        for (int i = 0; i < tempList.size(); i++) {
            List listData = (List) tempList.get(i);
            JSONArray cardList = AttenceService.getCardList(accessToken, checkDateFrom, checkDateTo, listData);
            result.addAll(cardList);
        }

        DataCenter.dataProcessing(result, staffMap);

        return result.toString();
    }

    @GetMapping("getLeaveList")
    public String getLeaveList() {
        Long endTime = System.currentTimeMillis();
        Long tempTime = System.currentTimeMillis() / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
        Long startTime = tempTime;

        JSONArray result = new JSONArray();
        //获取access_token
        String accessToken = AttenceService.getAccessToken(corpid, secret);

        Map<String, Object> ecachMap = Constants.ecachMap;

        if(ecachMap.size() == 0) {
            return "";
        }

        Map<String, List> staffMap = (Map<String, List>) ecachMap.get("staff");
        List list = (List) ecachMap.get("userIdList");

        //删除重复的
        list = DataCenter.removeDuplicate(list);
        List tempList = DataCenter.listSplit(list, 99);

        for (int i = 0; i < tempList.size(); i++) {
            int offset = 0;
            if (0 == i) {
                offset = 20 * i;
            } else {
                offset = 20 * i + 1;
            }
            List listData = (List) tempList.get(i);
            String userIds = "";
            for (int j = 0; j < listData.size(); j++) {
                if (i == listData.size() - 1) {
                    userIds += listData.get(j).toString();
                } else {
                    userIds += listData.get(j).toString() + ",";
                }
            }
            JSONArray leaveList = AttenceService.getLeaveList(accessToken, offset, 20, startTime, endTime, userIds);
            result.addAll(leaveList);
        }

        DataCenter.dataHandling(result, staffMap);
        return result.toString();
    }
}
