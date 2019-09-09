package com.xy.attence.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xy.attence.work.bus.AttenceService;
import com.xy.attence.work.util.Constants;
import com.xy.attence.work.util.DataCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: hexueyuan
 * @Name RefreshData
 * @Date: 2019/9/9 9:21
 * @Description: 更新数据
 * @Version 1.0
 */
@Component
public class RefreshData {
    private Map<String, List> staffMap = new HashMap<>();
    private List userIdList = new ArrayList();

    @Value("${corpid}")
    private String corpid;
    @Value("${secret}")
    private String secret;

    @Scheduled(cron = "0 0 0 * * ?") //每天晚上12点触发
    public void scheduled() throws ParseException {
        //获取access_token
        String accessToken = AttenceService.getAccessToken(corpid, secret);

        //获取部门ID
        JSONArray json = AttenceService.getDepartment(accessToken);

        for (int i = 0; i < json.size(); i++) {
            JSONObject obj = (JSONObject) json.get(i);
            List staff = AttenceService.getSimplelist(accessToken, obj.get("id").toString());
            if (staff.size() > 0) {
                staffMap.put(obj.get("name").toString(), staff);
            } else {
                continue;
            }
        }

        if (staffMap.size() > 0) {
            for (String key : staffMap.keySet()) {
                List derp = staffMap.get(key);
                for (int i = 0; i < derp.size(); i++) {
                    JSONObject ob = (JSONObject) derp.get(i);
                    String userid = ob.getString("userid");
                    userIdList.add(userid);
                }
            }
        }

        userIdList = DataCenter.removeDuplicate(userIdList);

        Map<String, Object> ecachMap = Constants.ecachMap;

        ecachMap.put("staff", staffMap);
        ecachMap.put("userIdList", userIdList);
    }
}
