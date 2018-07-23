package com.lemonrat.youdaofanyidemo.model;

import java.util.ArrayList;
import java.util.Map;

public class Word {


    boolean isChinese;
    String [] translation;
    String chs_phonetic;
    String chs_phonetic_url;
    String uk_phonetic;
    String uk_phonetic_url;
    String us_phonetic;
    String us_phonetic_url;
    String [] explains;
    Map<String, ArrayList<String>> web;

    public String getChs_phonetic_url() {
        return chs_phonetic_url;
    }

    public void setChs_phonetic_url(String chs_phonetic_url) {
        this.chs_phonetic_url = chs_phonetic_url;
    }

    public String getUk_phonetic_url() {
        return uk_phonetic_url;
    }

    public void setUk_phonetic_url(String uk_phonetic_url) {
        this.uk_phonetic_url = uk_phonetic_url;
    }

    public String getUs_phonetic_url() {
        return us_phonetic_url;
    }

    public void setUs_phonetic_url(String us_ponetic_url) {
        this.us_phonetic_url = us_ponetic_url;
    }

    public boolean getIsChinese() {
        return isChinese;
    }

    public void setIsChinese(boolean isChinese) {
        this.isChinese = isChinese;
    }

    public Map<String, ArrayList<String>> getWeb() {
        return web;
    }

    public void setWeb(Map<String, ArrayList<String>> web) {
        this.web = web;
    }

    public String[] getTranslation() {
        return translation;
    }

    public void setTranslation(String[] translation) {
        this.translation = translation;
    }

    public String getChs_phonetic() {
        return chs_phonetic;
    }

    public void setChs_phonetic(String chs_phone) {
        this.chs_phonetic = chs_phone;
    }

    public String getUk_phonetic() {
        return uk_phonetic;
    }

    public void setUk_phonetic(String uk_phonetic) {
        this.uk_phonetic = uk_phonetic;
    }

    public String getUs_phonetic() {
        return us_phonetic;
    }

    public void setUs_phonetic(String us_phonetic) {
        this.us_phonetic = us_phonetic;
    }

    public String[] getExplains() {
        return explains;
    }

    public void setExplains(String[] explains) {
        this.explains = explains;
    }


}
