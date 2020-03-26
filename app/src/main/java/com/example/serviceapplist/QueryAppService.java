package com.example.serviceapplist;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

public class QueryAppService extends Service {
    private static final String TAG = "QueryAppService";

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 启动新的线程获取数据并发送广播
         * 这里有个大坑，最开始本来想在service里面直接封装List<AppBean>  结果发现Drawable没有实现序列化接口，所有数据都可以通过，唯独Drawable会报错 Parcelable encountered IOException writing serializable object
         * 虽然可以通过 单独传 Parcelable对象的方法，但是感觉很麻烦，于是想到了 把Drawable转换为byte[]的方法 这样就可以通过序列化，但是新的坑就出现了，确实是可以通过这种方式传递Drawable或者bitmap对象但是
         * 会报错android.os.TransactionTooLargeException: data parcel size 2073572 bytes 说图标文件太大了，直接崩溃，所以这里退而求其次 直接把packageinfo通过广播发出，解析部分交给activity中的broadcastReceiver
         * 因为广播接受者是直接运行在UI线程上面的，在这里定义更方便修改ui也就是更方便给listview提供adapter数据
         */
        new Thread(() -> {
            List<PackageInfo> datas = getPackageinfo();
            Intent broadIntent = new Intent(MainActivity.QUERY_ALL_ACTION);
            broadIntent.putExtra("list", (Serializable) datas);
            sendBroadcast(broadIntent);
        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public List<PackageInfo> getPackageinfo() {
        List<PackageInfo> packageInfos;
        PackageManager packageManager = getPackageManager();
        packageInfos = packageManager.getInstalledPackages(0);
        return packageInfos;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }
}
