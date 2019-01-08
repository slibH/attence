package com.xy.attence.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xy.attence.work.bus.AttenceService;
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

    /*@GetMapping("getAttenceList")
    public String getAttenceList() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String checkDateFrom = df.format(DateUtil.todayFirstDate());
        String checkDateTo = df.format(new Date());
        //获取access_token
        String accessToken = AttenceService.getAccessToken(corpid, secret);
        //获取部门ID
        JSONArray json = AttenceService.getDepartment(accessToken);
        String[] whiteLists = whiteList.split(",");
        JSONArray result = new JSONArray();

        //根据部门来获取部门用户签到记录
        //剔除昆明雄越科技有限公司和江西分公司、长沙分公司
        for (int i = 0; i < json.size(); i++) {
            JSONObject obj = (JSONObject) json.get(i);
            //不在白名单的进行查询打卡记录
            if (!Arrays.asList(whiteLists).contains(obj.get("name").toString())) {
                //获取部门人员
                List list = AttenceService.getDeptMember(accessToken, obj.get("id").toString());
                List staff = AttenceService.getSimplelist(accessToken, obj.get("id").toString());

                if (null != list && list.size() > 0) {
                    //每次最多能查50个所以要分割
                    for (int j = 0; j < list.size(); j++) {
                        List listData = (List) list.get(j);
                        JSONArray cardList = AttenceService.getCardList(accessToken, checkDateFrom, checkDateTo, listData);
                        //处理数据
                        DataCenter.addProperties(cardList, obj.get("name").toString(), staff);
                        result.addAll(cardList);
                    }
                }
            }
        }

        return result.toString();
    }*/

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

    /*@GetMapping("getAttenceListByDepart")
    public String getAttenceListByDepart(@RequestParam("type") String type, @RequestParam("name") String name) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String checkDateFrom = df.format(DateUtil.todayFirstDate());
        String checkDateTo = df.format(new Date());
        //获取access_token
        String accessToken = AttenceService.getAccessToken(corpid, secret);

        JSONArray result = new JSONArray();

        //根据部门来获取部门用户签到记录
        //获取部门人员
        List list = AttenceService.getDeptMember(accessToken, type);
        List staff = AttenceService.getSimplelist(accessToken, type);
        if (null != list && list.size() > 0) {
            //每次最多能查50个所以要分割
            for (int j = 0; j < list.size(); j++) {
                List listData = (List) list.get(j);
                JSONArray cardList = AttenceService.getCardList(accessToken, checkDateFrom, checkDateTo, listData);
                //处理数据
                DataCenter.addProperties(cardList, name, staff);
                result.addAll(cardList);
            }
        }

        return result.toString();
    }*/

    @GetMapping("getAttenceList")
    public String getAttenceList() {
        JSONArray result = new JSONArray();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String checkDateFrom = df.format(DateUtil.todayFirstDate());
        String checkDateTo = df.format(new Date());
        //获取access_token
        String accessToken = AttenceService.getAccessToken(corpid, secret);
        Map<String, List> staffMap = getStaffMap(accessToken);
        List list = getUserIds(accessToken);

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
        //Date date = DateUtil.initDateByDay();
        //Date nextDay = DateUtil.addDay(date, 1);
        //Long endTime = nextDay.getTime();//当前时间的Unix时间戳
        Long endTime = System.currentTimeMillis();
        Long tempTime = System.currentTimeMillis() / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
        Long startTime = tempTime;

        JSONArray result = new JSONArray();
        //获取access_token
        String accessToken = AttenceService.getAccessToken(corpid, secret);
        List list = getUserIds(accessToken);
        Map<String, List> staffMap = getStaffMap(accessToken);

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

    public List getUserIds(String accessToken) {
        List list = new ArrayList();
        /**
         * 获取所有员工的ID
         */
        for (int i = 0; i < 1000; i++) {
            int offset = 0;
            offset = 20 * i;

            List userIds = AttenceService.employee(accessToken, offset, 20);
            if (userIds.size() > 0) {
                list.addAll(userIds);
            } else {
                break;
            }
        }

        return list;
    }

    public Map<String, List> getStaffMap(String accessToken) {
        //获取部门ID
        JSONArray json = AttenceService.getDepartment(accessToken);
        Map<String, List> staffMap = new HashMap<>();

        for (int i = 0; i < json.size(); i++) {
            JSONObject obj = (JSONObject) json.get(i);
            List staff = AttenceService.getSimplelist(accessToken, obj.get("id").toString());
            if (staff.size() > 0) {
                staffMap.put(obj.get("name").toString(), staff);
            } else {
                continue;
            }
        }
        return staffMap;
    }
}
