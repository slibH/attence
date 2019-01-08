package com.xy.attence.work.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 数据中心，用于处理数据
 */
public class DataCenter {
    /**
     * 添加特性数据：姓名，部门
     *
     * @param sourceData
     * @param departmentName
     * @param staff
     * @return
     */
   /* public static void addProperties(JSONArray sourceData, String departmentName, List staff) {
        if (null != sourceData && sourceData.size() > 0) {
            for (int i = 0; i < sourceData.size(); i++) {
                JSONObject obj = (JSONObject) sourceData.get(i);
                obj.put("departName", departmentName);//部门名称

                //用户打卡有经纬度
                if ("USER".equals(obj.get("sourceType").toString())) {
                    obj.put("lat", "userLatitude");
                    obj.put("lng", "userLongitude");
                    obj.put("value", 1);//气泡图级别
                    obj.put("type", 2);//气泡图类型
                }

                if (null != obj.get("locationResult") && "Normal".equals(obj.get("locationResult").toString())) {
                    obj.put("locationResult", "范围内");
                } else {
                    obj.put("locationResult", "范围外");
                }

                //数据来源处理
                switch (obj.get("sourceType").toString()) {
                    case "ATM":
                        obj.put("sourceType", "考勤机");
                        break;
                    case "BEACON":
                        obj.put("sourceType", "IBeacon");
                        break;
                    case "DING_ATM":
                        obj.put("sourceType", "钉钉考勤机");
                        break;
                    case "USER":
                        obj.put("sourceType", "用户打卡");
                        break;
                    case "BOSS":
                        obj.put("sourceType", "老板改签");
                        break;
                    case "APPROVE":
                        obj.put("sourceType", "审批系统");
                        break;
                    case "SYSTEM":
                        obj.put("sourceType", "考勤系统");
                        break;
                    case "AUTO_CHECK":
                        obj.put("sourceType", "自动打卡");
                        break;
                    default:
                        break;
                }

                if (null != obj.get("timeResult")) {
                    //打卡结果
                    switch (obj.get("timeResult").toString()) {
                        case "Normal":
                            obj.put("timeResult", "正常");
                            break;
                        case "Early":
                            obj.put("timeResult", "早退");
                            break;
                        case "Late":
                            obj.put("timeResult", "迟到");
                            break;
                        case "SeriousLate":
                            obj.put("timeResult", "严重迟到");
                            break;
                        case "Absenteeism":
                            obj.put("timeResult", "旷工迟到");
                            break;
                        case "NotSigned":
                            obj.put("timeResult", "未打卡");
                            break;
                        default:
                            break;
                    }
                }

                String userId = obj.get("userId").toString();
                for (int j = 0; j < staff.size(); j++) {
                    Map map = (Map) staff.get(j);
                    if (userId.equals(map.get("userid").toString())) {
                        obj.put("name", map.get("name").toString());
                    }
                }
            }
        }
    }*/

    /**
     * 签到数据处理
     *
     * @param sourceData
     */
    public static void checkinDataProcessing(JSONArray sourceData) {
        if (null != sourceData && sourceData.size() > 0) {
            for (int i = 0; i < sourceData.size(); i++) {
                JSONObject obj = (JSONObject) sourceData.get(i);

                Long timestamp = Long.parseLong(obj.get("timestamp").toString());
                String chekinDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(timestamp));
                obj.put("checkinDate", chekinDate);
                obj.put("lat", obj.get("latitude"));
                obj.put("lng", obj.get("longitude"));
                obj.put("value", 1);//气泡图级别
                obj.put("type", 2);//气泡图类型
            }
        }
    }

    public static void dataProcessing(JSONArray sourceData, Map<String, List> map) {
        if (null != sourceData && sourceData.size() > 0) {
            for (int i = 0; i < sourceData.size(); i++) {
                JSONObject obj = (JSONObject) sourceData.get(i);

                Long timestamp = Long.parseLong(obj.get("userCheckTime").toString());
                String chekinDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(timestamp));
                obj.put("checkinDate", chekinDate);

                if (null != obj.get("locationResult") && "Normal".equals(obj.get("locationResult").toString())) {
                    obj.put("locationResult", "范围内");
                } else {
                    obj.put("locationResult", "范围外");
                }

                //数据来源处理
                switch (obj.get("sourceType").toString()) {
                    case "ATM":
                        obj.put("sourceType", "考勤机");
                        break;
                    case "BEACON":
                        obj.put("sourceType", "IBeacon");
                        break;
                    case "DING_ATM":
                        obj.put("sourceType", "钉钉考勤机");
                        break;
                    case "USER":
                        obj.put("sourceType", "用户打卡");
                        break;
                    case "BOSS":
                        obj.put("sourceType", "老板改签");
                        break;
                    case "APPROVE":
                        obj.put("sourceType", "审批系统");
                        break;
                    case "SYSTEM":
                        obj.put("sourceType", "考勤系统");
                        break;
                    case "AUTO_CHECK":
                        obj.put("sourceType", "自动打卡");
                        break;
                    default:
                        break;
                }

                if (null != obj.get("timeResult")) {
                    //打卡结果
                    switch (obj.get("timeResult").toString()) {
                        case "Normal":
                            obj.put("timeResult", "正常");
                            break;
                        case "Early":
                            obj.put("timeResult", "早退");
                            break;
                        case "Late":
                            obj.put("timeResult", "迟到");
                            break;
                        case "SeriousLate":
                            obj.put("timeResult", "严重迟到");
                            break;
                        case "Absenteeism":
                            obj.put("timeResult", "旷工迟到");
                            break;
                        case "NotSigned":
                            obj.put("timeResult", "未打卡");
                            break;
                        default:
                            break;
                    }
                }

                String userId = obj.get("userId").toString();
                //添加部门和人员名称
                for (String key : map.keySet()) {//keySet获取map集合key的集合  然后在遍历key即可
                    List staff = map.get(key);
                    for (int j = 0; j < staff.size(); j++) {
                        Map staffMap = (Map) staff.get(j);
                        if (userId.equals(staffMap.get("userid").toString())) {
                            obj.put("name", staffMap.get("name").toString());
                            obj.put("departName", key);
                        }
                    }
                }
            }
        }
    }

    public static void dataHandling(JSONArray sourceData, Map<String, List> map) {
        if (null != sourceData && sourceData.size() > 0) {
            for (int i = 0; i < sourceData.size(); i++) {
                JSONObject obj = (JSONObject) sourceData.get(i);

                Long startTime = Long.parseLong(obj.get("start_time").toString());
                String startTm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(startTime));
                obj.put("startTm", startTm);

                Long endTime = Long.parseLong(obj.get("end_time").toString());
                String endTm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(endTime));
                obj.put("endTm", endTm);

                String userId = obj.get("userid").toString();
                //添加部门和人员名称
                for (String key : map.keySet()) {//keySet获取map集合key的集合  然后在遍历key即可
                    List staff = map.get(key);
                    for (int j = 0; j < staff.size(); j++) {
                        Map staffMap = (Map) staff.get(j);
                        if (userId.equals(staffMap.get("userid").toString())) {
                            obj.put("name", staffMap.get("name").toString());
                            obj.put("departName", key);
                        }
                    }
                }
            }
        }
    }

    public static List removeDuplicate(List list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    /**
     * 将List拆分为多个子list
     *
     * @param list
     * @param size
     * @return
     */
    public static List listSplit(List list, int size) {
        List result = new ArrayList();
        int startIndex = 0, endIndex = size;
        while (startIndex < list.size()) {
            if (endIndex > list.size()) {
                result.add(list.subList(startIndex, list.size()));
                break;
            } else {
                result.add(list.subList(startIndex, endIndex));
                startIndex = endIndex;
                endIndex += size;
            }
        }
        return result;
    }
}
