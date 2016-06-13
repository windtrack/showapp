package com.oo58.livevideoassist.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oo58.livevideoassist.activity.LoginActivity;
import com.oo58.livevideoassist.common.APPConstants;

import cn.nodemedia.nodemediaclient.R;

/**
 * 版本更新的控制
 */
public class UpdateManager {
    private LoginActivity mContext;
    // 提示语
    private String updateMsg = "有最新的软件包哦，亲快下载吧~,如果更新遇到问题,请从官网下载最新版本,卸载旧版本后,重新安装";
    // 返回的安装包url
    private String apkUrl = "http://down.0058.com/0058_Video.apk"; //下载地址
    private Dialog noticeDialog;
    private Dialog downloadDialog;
    /* 下载包安装路径 */
    private String savePath = "/updatepath/";
    private String saveFileName = "com.oo58.livevideoassist";
    private String saveFileNameExt = ".apk";
    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private int progress;
    private Thread downLoadThread;
    private boolean interceptFlag = false; //下载是否完成
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    break;
                case DOWN_OVER:
                    installApk();
                    mContext.doFinish();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public UpdateManager(LoginActivity context, String aurl) {
        this.mContext = context;
        this.apkUrl = aurl;
        saveFileName = saveFileName + APPConstants.serverVersionCode + saveFileNameExt;
        savePath = Environment.getExternalStorageDirectory() + savePath;
    }

    // 外部接口让主Activity调用
    public void checkUpdateInfo() {
        showNoticeDialog();
    }

    private Dialog dialog_re;


    /**
     * 显示提示更新界面
     */
    private void showNoticeDialog() {
         String str_updateInfo = APPConstants.updateTips;
        String realStr = str_updateInfo.replace("&", "\n");
        realStr += "\n";
        dialog_re = new Builder(mContext).create();
        dialog_re.show();
        Window localWindow = dialog_re.getWindow();
        localWindow.setContentView(LayoutInflater.from(mContext).inflate(R.layout.dialog_update, null));

        TextView myTextTitle = (TextView) localWindow.findViewById(R.id.text_updatetitle);
        TextView myTextView = (TextView) localWindow.findViewById(R.id.text_updateinfo);


        myTextTitle.setText("版本号："+APPConstants.serverVersionName);
        myTextView.setText(realStr);
        TextView bt_download = (TextView) localWindow.findViewById(R.id.layout_update_download);
        bt_download.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                showDownloadDialog();
            }
        });
        ImageButton bt_canacel = (ImageButton) localWindow.findViewById(R.id.layout_update_cancel);
        bt_canacel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog_re.dismiss();
                mContext.doFinish();
            }
        });
        dialog_re.setCancelable(false);
    }


    private void showDownloadDialog() {
        if (installApk()) {
            return;
        }

        downloadDialog = new Builder(mContext).create();
        downloadDialog.show();
        Window localWindow = downloadDialog.getWindow();
        localWindow.setContentView(LayoutInflater.from(mContext).inflate(R.layout.progress, null));
        mProgress = (ProgressBar) localWindow.findViewById(R.id.progress);
//        Builder builder = new Builder(mContext);
////        builder.setTitle("软件版本更新");
//        final LayoutInflater inflater = LayoutInflater.from(mContext);
//        View v = inflater.inflate(R.layout.progress, null);
//        mProgress = (ProgressBar) v.findViewById(R.id.progress);
//        builder.setView(v);
//        downloadDialog = builder.create();
//        downloadDialog.show();
        downloadDialog.setCancelable(false);
        downloadApk();
        dialog_re.dismiss();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                String apkFile = savePath + saveFileName;
                File ApkFile = new File(apkFile);
                if (ApkFile.exists()){
                    ApkFile.delete();
                }else{
                    ApkFile.createNewFile();
                }

                FileOutputStream fos = new FileOutputStream(ApkFile);
                int count = 0;
                byte buf[] = new byte[4096];
                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成改名字 防止 每次都要下载
                        interceptFlag = true ;
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消就停止下载.

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 下载apk
     *
     * @param
     */

    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     *
     * @param
     */
    private boolean installApk() {
        String apkFile = savePath + saveFileName;
        File apkfile = new File(apkFile);
        if (!apkfile.exists()) {
            return false;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        mContext.startActivity(i);
        return true ;
    }


    /**
     *  删除已有的apk
     */
    private void doDeletLocalApk(){

        String apkFile = savePath + saveFileName;
        File apkfile = new File(apkFile);
        if (apkfile.exists()) {
            apkfile.delete();
        }
    }

}
