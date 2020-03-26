package com.example.serviceapplist;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    MyApplication app;
    private static final String TAG = "MainActivity";
    public static final String QUERY_ALL_ACTION = "com.example.serviceapplist.QUERY_ALL";
    ActivityBroadCast broadCast;
    MyDataAdapter adapter;
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.edit_search)
    SearchView searchView;
    List<AppBean> appBeans;
    List<AppBean> temps;
    List<PackageInfo> packageInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (MyApplication) getApplication();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        searchView.onActionViewExpanded();
        searchView.clearFocus();
        broadCast = new ActivityBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(QUERY_ALL_ACTION);
        registerReceiver(broadCast, filter);
        /**
         * 根据输入关键字查询符合条件的appList
         */
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                temps = new ArrayList<>();
                //直接从broadcast中去会出现莫名其妙的问题
                for (int i = 0; i < app.getBeans().size(); i++) {
                    if (app.getBeans().get(i).getAppName().contains(query)) {
                        temps.add(app.getBeans().get(i));
                    }
                }//通知adapter更新数据
                adapter.notifyChanged(temps);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    temps = new ArrayList<>();
                    for (int i = 0; i < app.getBeans().size(); i++) {
                        temps.add(app.getBeans().get(i));

                    }
                    adapter.notifyChanged(temps);
                }
                return false;
            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

    }


    // 自定义的BroadcastReceiver，负责监听从Service传回来的广播
    public class ActivityBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(QUERY_ALL_ACTION)) {
                packageInfos = (List<PackageInfo>) intent.getSerializableExtra("list");
                appBeans = new ArrayList<>();
                for (PackageInfo p : packageInfos) {
                    PackageManager packageManager = getPackageManager();
                    AppBean bean = new AppBean();
                    bean.setAppIcon((p.applicationInfo.loadIcon(packageManager)));
                    bean.setAppName(packageManager.getApplicationLabel(p.applicationInfo).toString());
                    File f = new File(p.applicationInfo.sourceDir);
                    bean.setAppSize(f.length());
                    appBeans.add(bean);
                }
            }
            app.setBeans(appBeans);
            adapter = new MyDataAdapter(MainActivity.this, appBeans);
            listView.setAdapter(adapter);
        }
    }

    //当退出应用程序时在application中启动的服务就没有用处了 所以在这里stopService防止内存泄露
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadCast);
        Intent it = new Intent(getApplicationContext(), QueryAppService.class);
        stopService(it);
    }
}
