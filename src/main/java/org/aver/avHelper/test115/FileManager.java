package org.aver.avHelper.test115;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;

public class FileManager {

    public static void main(String[] args) {
        String url = "https://aps.115.com/natsort/files.php?";
        HashMap<String, String> map = new HashMap<>();
        map.put("aid", "1");
        map.put("cid", "2695424509184508062");
        map.put("o", "file_name");
        map.put("asc", "1");
        map.put("offset", "0");
        map.put("show_dir", "1");
        map.put("limit", "115");
        map.put("code", "");
        map.put("scid", "");
        map.put("snap", "0");
        map.put("natsort", "1");
        map.put("record_open_time", "1");
        map.put("count_folders", "1");
        map.put("source", "");
        map.put("format", "json");
        map.put("fc_mix", "1");
        map.put("type", "");
        map.put("star", "");
        map.put("is_share", "");
        map.put("suffix", "");
        map.put("custom_order", "");
        map.put("is_q", "");
        HttpRequest request = HttpRequest.get(url + HttpUtil.toParams(map))
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .header("Connection", "keep-alive")
                .header("Cookie", "USERSESSIONID=3055eb2021c7c979375e198f64d8ef1a9df0737e5ab502bd27d5977105f2bb83; UID=592424034_A1_1698539062; CID=59964abc96fe97e9c41429ae326cc545; SEID=4b11b1fb311eb049f929e6a425ee6ba851318b04f0a9e73add4efb25e5942a8ab32c116817c9d08b5b7cc3ab853d9213204a721a95d581bcbefd54e1; HWWAFSESID=bbd3c7cd153bf4abdb; HWWAFSESTIME=1698539063330; 115_lang=zh; ACT_ACCESS_SOURCE=ad1; ACT_ACCESS_CLIENT=2; PHPSESSID=2k6emjp6a2d8pf53kad4qvq4rk")
                .header("Host", "aps.115.com")
                .header("Origin", "https://115.com")
                .header("Referer", "https://115.com/")
//                .header("sec-ch-ua", """
//                        "Chromium";v="13", " Not;A Brand";v="99"
//                        """)
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"Linux\"")
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "same-site")
                .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36");
        HttpResponse response = request.execute();
        JSONObject json = JSONUtil.parseObj(response.body());
        JSONArray path = json.getJSONArray("path");
        for (int i = 0; i < path.size(); i++) {
            JSONObject subPath = path.getJSONObject(i);
            System.out.println(subPath.getStr("name"));
        }
        System.out.println(response);

    }
}
