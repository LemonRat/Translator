package com.lemonrat.youdaofanyidemo.mvp.impl;

import android.content.Context;

public class BasePresenter {

    Context mContext;

    public void attach(Context context){
        mContext = context;
    }
    public void onResume(){}
    public void onPouse(){}
    public void onDestroy(){
        mContext = null;
    }
}
