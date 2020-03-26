package com.example.serviceapplist;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Intent it = new Intent(getApplicationContext(), QueryAppService.class);
        startService(it);
    }

    /**
     * 存放service获取到的全局appBean，当调用searchView搜索符合要求的应用时会从这里获取全部的应用数据过滤后更新adapter
     */
    List<AppBean> beans;

    public List<AppBean> getBeans() {
        return beans;
    }

    public void setBeans(List<AppBean> beans) {
        this.beans = beans;
    }

    /**
     * 模拟器退出程序会调用
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        Intent it = new Intent(getApplicationContext(), QueryAppService.class);
        stopService(it);
    }
}

