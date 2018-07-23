package com.lemonrat.youdaofanyidemo.mvp;

public interface MvpMainView extends MvpLoadingView {

    void showToast(String msg);
    void updateView();


}
