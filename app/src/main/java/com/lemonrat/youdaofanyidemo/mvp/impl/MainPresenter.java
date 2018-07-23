package com.lemonrat.youdaofanyidemo.mvp.impl;

import android.text.TextUtils;
import android.util.Log;

import com.lemonrat.youdaofanyidemo.Constant;
import com.lemonrat.youdaofanyidemo.Utils;
import com.lemonrat.youdaofanyidemo.business.HttpUtils;
import com.lemonrat.youdaofanyidemo.model.Word;
import com.lemonrat.youdaofanyidemo.mvp.MvpMainView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainPresenter extends BasePresenter {
    public static final String TAG = "MainPresenter";
    MvpMainView mMvpMainView;
    Word mWord;
    String sign;
    boolean isChinese = false;

    public MainPresenter(MvpMainView mainView) {
        mMvpMainView = mainView;
    }

    public Word getWordInfo() {
        return mWord;
    }

    public void searchWordInfo(String word) {
        String str;
        if (TextUtils.isEmpty(word)) {
            mMvpMainView.showToast("输入为空！");
            return;
        }

        mMvpMainView.showLoading();
        //生成sign, sign:md5(appKey+q+salt+应用密钥)生成(q:要查找的单词，salt:随机整数)
        str = Constant.APPKEY + word + Constant.SALT + Constant.SECRETKEY;
        sign = md5(str);
        //写上http请求逻
        sendHttp(word);

    }



    /**
     * 生成32位MD5摘要
     * @param string
     * @return) {
    if(string == null){
    return null;
     */
    public static String md5(String string) {
        if(string == null){
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};

        try{
            byte[] btInput = string.getBytes("utf-8");
            /** 获得MD5摘要算法的 MessageDigest 对象 */
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            /** 使用指定的字节更新摘要 */
            mdInst.update(btInput);
            /** 获得密文 */
            byte[] md = mdInst.digest();
            /** 把密文转换成十六进制的字符串形式 */
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        }catch(NoSuchAlgorithmException | UnsupportedEncodingException e){
            return null;
        }
    }

    private void sendHttp(String word) {
        Map<String, String> map = new HashMap<>();
        map.put("sign", sign);
        map.put("q", word);

        HttpUtils httpUtils = new HttpUtils(new HttpUtils.HttpResponse() {
            @Override
            public void onSuccess(Object object) {
                String json = object.toString();
                mWord = parseModeWithOrgJson(json);

                mMvpMainView.hidenLoading();
                mMvpMainView.updateView();
            }

            @Override
            public void onFail(String error) {
                mMvpMainView.showToast(error);
                mMvpMainView.hidenLoading();
            }
        });

        if (Utils.isEnglish(word)) {
            httpUtils.sendGetHttp(Constant.EN_TO_CHS_BASE_URL, map);
            isChinese = false;
            Log.d(TAG, "英文转中文");
        } else {
            httpUtils.sendGetHttp(Constant.CHS_TO_EN_BASE_URL, map);
            isChinese = true;
            Log.d(TAG, "中文转英文");
        }

    }

    private Word parseModeWithOrgJson(String json) {
        Word word = new Word();
        JSONObject basicObject = null;
        String [] value;
        ArrayList<String> list;
        Map<String, ArrayList<String>> map = new HashMap<>();
        String chs_phonetic = null;
        String chs_phonetic_url = null;
        Log.d(TAG, "word: "+json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            basicObject = jsonObject.getJSONObject("basic");

            JSONArray jsonArray = jsonObject.getJSONArray("translation");
            Log.d(TAG, "translation jsonArray: "+jsonArray);
            value = new String[10];
            getAllString(value, jsonArray);
            word.setTranslation(value);




            jsonArray = basicObject.getJSONArray("explains");
            Log.d(TAG, "explains jsonArray: "+jsonArray);
            value = new String[10];
            getAllString(value, jsonArray);
            word.setExplains(value);

            jsonArray = jsonObject.getJSONArray("web");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String key = object.getString("key");
                Log.d(TAG, "web key: "+key);
                JSONArray array = object.getJSONArray("value");
                Log.d(TAG, "web key jsonArray: "+ array);
                list = new ArrayList<>();
                getValueToList(list,array);
                map.put(key, list);

            }
            word.setWeb(map);


            if (!isChinese) {
                String us_phonetic = basicObject.getString("us-phonetic");
                String uk_phonetic = basicObject.getString("uk-phonetic");
                String us_speech = basicObject.getString("us-speech");
                String uk_speech = basicObject.getString("uk-speech");
                Log.d(TAG, "uk-speech: "+uk_speech);
                word.setUs_phonetic_url(us_speech);
                word.setUk_phonetic_url(uk_speech);
                word.setUs_phonetic(us_phonetic);
                word.setUk_phonetic(uk_phonetic);
                word.setIsChinese(false);
            } else {

                chs_phonetic = basicObject.getString("phonetic");
                Log.d(TAG, "中文 phonetic:"+chs_phonetic);
                chs_phonetic_url = jsonObject.getString("speakUrl");
                Log.d(TAG, "speakUrl: "+chs_phonetic_url);
                word.setChs_phonetic(chs_phonetic);
                word.setChs_phonetic_url(chs_phonetic_url);
                word.setIsChinese(true);

            }

        } catch (JSONException e) {
            e.printStackTrace();
            if(basicObject == null) word = null;
            else {
                Log.d(TAG, "异常中文 phonetic:"+chs_phonetic);
                word.setChs_phonetic(chs_phonetic);
                word.setChs_phonetic_url(chs_phonetic_url);
                word.setIsChinese(true);
            }

        }

        return word;

    }

    private void getValueToList(ArrayList<String> list, JSONArray array) {
        for (int i = 0; i < array.length(); i++) {

            try {
                list.add(array.get(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void getAllString(String[] value, JSONArray jsonArray) throws JSONException {
        for(int i = 0; i < jsonArray.length(); i++) {
            value[i] = jsonArray.get(i).toString();
        }
    }


}
