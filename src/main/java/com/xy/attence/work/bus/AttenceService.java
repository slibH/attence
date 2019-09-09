package com.xy.attence.work.bus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xy.attence.work.util.DataCenter;
import com.xy.attence.work.util.HttpUtils;

import java.util.*;

public class AttenceService {
    /**
     * 获取钉钉的access_token
     *
     * @param corpid
     * @param secret
     * @return
     */
    public static String getAccessToken(String corpid, String secret) {
        String requsetUrl = "https://oapi.dingtalk.com/gettoken?corpid=" + corpid + "&corpsecret=" + secret;
        String result = HttpUtils.doGet(requsetUrl);
        String accessToken = null;
        JSONObject jsonObject = JSON.parseObject(result);
        String msg = (String) jsonObject.get("errmsg");
        if ("ok".equals(msg)) {
            accessToken = (String) jsonObject.get("access_token");
        }

        return accessToken;
    }

    /**
     * 获取打卡详细
     *
     * @param accessToken
     * @param checkDateFrom 查询考勤打卡记录的起始工作日。格式为“yyyy-MM-dd hh:mm:ss”
     * @param checkDateTo   查询考勤打卡记录的结束工作日。格式为“yyyy-MM-dd hh:mm:ss”
     * @param userIds       企业内的员工id列表，最多不能超过50个
     * @return
     */
    public static JSONArray getCardList(String accessToken, String checkDateFrom, String checkDateTo, List userIds) {
        String recordUrl = "https://oapi.dingtalk.com/attendance/listRecord?access_token=" + accessToken;//这是获取打卡详细
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userIds", userIds);
        jsonObject.put("checkDateFrom", checkDateFrom);
        jsonObject.put("checkDateTo", checkDateTo);
        jsonObject.put("isI18n", "false");//是否为海外企业使用，true：海外平台使用,false：国内平台使用，默认
        String result = HttpUtils.doPost(recordUrl, jsonObject);
        JSONObject resultJSON = JSONObject.parseObject(result);
        String msg = (String) resultJSON.get("errmsg");
        JSONArray jsonArray = null;
        if ("ok".equals(msg)) {
            jsonArray = (JSONArray) resultJSON.get("recordresult");
        }
        return jsonArray;
    }


    /**
     * 打卡结果
     *
     * @param accessToken
     * @param workDateFrom
     * @param workDateTo
     * @param userIds
     * @param offset
     * @param limit
     * @return
     */
    public static JSONArray getCard(String accessToken, String workDateFrom, String workDateTo, List userIds, String offset, String limit) {
        String recordUrl = "https://oapi.dingtalk.com/attendance/list?access_token=" + accessToken;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("workDateFrom", workDateFrom);
        jsonObject.put("workDateTo", workDateTo);
        jsonObject.put("offset", offset);
        jsonObject.put("userIdList", userIds);
        jsonObject.put("limit", limit);
        String result = HttpUtils.doPost(recordUrl, jsonObject);
        JSONObject resultJSON = JSONObject.parseObject(result);
        String msg = (String) resultJSON.get("errmsg");
        JSONArray jsonArray = null;
        if ("ok".equals(msg)) {
            jsonArray = (JSONArray) resultJSON.get("recordresult");
        }
        return jsonArray;
    }

    /**
     * 根据部门id和开始时间，结束时间获取部门的用户签到记录
     *
     * @param accessToken
     * @param start_time
     * @param end_time
     * @param department_id
     * @return
     */
    public static JSONArray getCheckinRecord(String accessToken, long start_time, long end_time, String department_id) {
        String requestUrl = "https://oapi.dingtalk.com/checkin/record?access_token=" + accessToken;
        requestUrl += "&department_id=" + department_id + "&start_time=" + start_time + "&end_time=" + end_time;
        String result = HttpUtils.doGet(requestUrl);
        JSONArray data = new JSONArray();
        JSONObject jsonObject = JSON.parseObject(result);
        String msg = (String) jsonObject.get("errmsg");
        if ("ok".equals(msg)) {
            data = (JSONArray) jsonObject.get("data");
        }

        return data;
    }

    /**
     * 获取部门用户userid列表
     * @param accessToken
     * @param department_id
     * @return
     */
    public static List getDeptMember(String accessToken, String department_id) {
        String requestUrl = "https://oapi.dingtalk.com/user/getDeptMember?access_token=" + accessToken + "&deptId=" + department_id;
        String result = HttpUtils.doGet(requestUrl);
        List list = new ArrayList();
        JSONObject jsonObject = JSON.parseObject(result);
        String msg = (String) jsonObject.get("errmsg");
        if ("ok".equals(msg)) {
            JSONArray json = (JSONArray) jsonObject.get("userIds");
            if (json.size() > 0) {
                List tempList = JSONObject.parseArray(json.toString());
                //一次查询不能超过50个用户
                list = DataCenter.listSplit(tempList, 49);
            }
        }

        return list;
    }

    /**
     * 获取部门用户-带姓名
     * @param accessToken
     * @param department_id
     * @return
     */
    public static List getSimplelist(String accessToken, String department_id) {
        String requestUrl = "https://oapi.dingtalk.com/user/simplelist?access_token=" + accessToken + "&department_id=" + department_id;
        String result = HttpUtils.doGet(requestUrl);
        List list = new ArrayList();
        JSONObject jsonObject = JSON.parseObject(result);
        String msg = (String) jsonObject.get("errmsg");
        if ("ok".equals(msg)) {
            JSONArray json = (JSONArray) jsonObject.get("userlist");
            list = JSONObject.parseArray(json.toString());
        }

        return list;
    }

    /**
     * 获取根部门下的部门列表
     *
     * @param accessToken
     * @return
     */
    public static JSONArray getDepartment(String accessToken) {
        String requestUrl = "https://oapi.dingtalk.com/department/list?access_token=" + accessToken + "&fetch_child=true";//递归的查询根部门下级的部门
        String result = HttpUtils.doGet(requestUrl);
        JSONArray department = null;
        JSONObject jsonObject = JSON.parseObject(result);
        String msg = (String) jsonObject.get("errmsg");
        if ("ok".equals(msg)) {
            department = (JSONArray) jsonObject.get("department");
        }

        return department;
    }

    /**
     * 获取根部门下的部门列表
     *
     * @param accessToken
     * @return
     */
    public static List getDepartmentList(String accessToken) {
        String requestUrl = "https://oapi.dingtalk.com/department/list_ids?access_token=" + accessToken + "&id=1";//递归的查询根部门下级的部门
        String result = HttpUtils.doGet(requestUrl);
        List list = new ArrayList();
        JSONObject jsonObject = JSON.parseObject(result);
        String msg = (String) jsonObject.get("errmsg");
        if ("ok".equals(msg)) {
            JSONArray department = (JSONArray) jsonObject.get("sub_dept_id_list");
            list = JSONObject.parseArray(department.toString());
        }

        return list;
    }

    public static JSONArray listschedule(String accessToken) {
        String recordUrl = "https://oapi.dingtalk.com/topapi/attendance/listschedule?access_token=" + accessToken;
        JSONObject jsonObject = new JSONObject();
       // String currentDate = DateUtil.dateToDateString(new Date(), "yyyy-MM-dd HH:mm:ss");
        //jsonObject.put("workDate", DateUtil.parseDateTime(currentDate));
        jsonObject.put("workDate", new Date());
        String result = HttpUtils.doPost(recordUrl, jsonObject);
        JSONObject resultJSON = JSONObject.parseObject(result);
        String msg = (String) resultJSON.get("errmsg");
        JSONArray jsonArray = null;
        if ("ok".equals(msg)) {
            jsonArray = (JSONArray) resultJSON.get("schedules");
        }
        return jsonArray;
    }

    public static List employee(String accessToken, int offset, int size) {
        String recordUrl = "https://oapi.dingtalk.com/topapi/smartwork/hrm/employee/queryonjob?access_token=" + accessToken;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status_list", "2,3,5,-1");
        jsonObject.put("offset", offset);
        jsonObject.put("size", size);
        String result = HttpUtils.doPost(recordUrl, jsonObject);
        JSONObject resultJSON = JSONObject.parseObject(result);
        Boolean flag = (Boolean) resultJSON.get("success");
        JSONArray jsonArray = null;
        if (flag) {
           Map map = (Map) resultJSON.get("result");
           jsonArray = (JSONArray) map.get("data_list");
        }

        List list = JSONObject.parseArray(jsonArray.toString());
        return list;
    }

    public static JSONArray getLeaveList(String accessToken, int offset, int size, Long startTime, Long endTime, String urseIds) {
        String recordUrl = "https://oapi.dingtalk.com/topapi/attendance/getleavestatus?access_token=" + accessToken;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("start_time", startTime);
        jsonObject.put("end_time", endTime);
        jsonObject.put("userid_list", urseIds);
        jsonObject.put("offset", offset);
        jsonObject.put("size", size);
        String result = HttpUtils.doPost(recordUrl, jsonObject);
        JSONObject resultJSON = JSONObject.parseObject(result);
        Boolean flag = (Boolean) resultJSON.get("success");
        JSONArray jsonArray = null;
        if (flag) {
            Map map = (Map) resultJSON.get("result");
            jsonArray = (JSONArray) map.get("leave_status");
        }
        return jsonArray;
    }
}
