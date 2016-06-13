package com.oo58.livevideoassist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oo58.livevideoassist.activity.BaseActivity;
import com.oo58.livevideoassist.common.AppAction;
import com.oo58.livevideoassist.rpc.RpcEvent;
import com.oo58.livevideoassist.util.ThreadPoolWrap;
import com.oo58.livevideoassist.util.Util;

import org.json.JSONObject;

import cn.nodemedia.nodemediaclient.R;

/**
 * @author zhongxf
 * @Description  启动闪频
 * @Date 2016/5/9.
 */
public class SplashActivity extends BaseActivity {

    private static final int M_Handler_GetUrlSuccess = 1 ;//获取启动页地址成功
    private static final int M_Handler_GetUrlFailed = 2 ;//获取启动页地址失败
    private static final int M_Handler_LogoOver = 3 ;//logo显示结束

    private  ImageView mImageLogo ;
    private Bitmap img_logo ;//本地闪屏图片

    ImageLoader  mImageLoader;
    DisplayImageOptions  mOptions;
    @Override
    protected void initContentView(Bundle savedInstanceState) {

        setContentView(R.layout.splash);
        mImageLogo = (ImageView)findViewById(R.id.imageView_logo) ;
//        img_logo = BitmapFactory.decodeResource(getResources(),R.mipmap.splashlogo);
//        mImageLogo.setImageBitmap(img_logo);
    }

    @Override
    protected void initData() {


//        ImageLoaderConfiguration localImageLoaderConfiguration = new ImageLoaderConfiguration.Builder(
//                SplashActivity.this).threadPoolSize(1)
//                .memoryCache(new WeakMemoryCache()).build();
//        mImageLoader = ImageLoader.getInstance();
//        mImageLoader.init(localImageLoaderConfiguration);
//        mOptions = new DisplayImageOptions.Builder()
//                .bitmapConfig(Bitmap.Config.RGB_565)
//                .showStubImage(R.mipmap.splashlogo)
//                .showImageForEmptyUri(R.mipmap.splashlogo)
//                .showImageOnFail(R.mipmap.splashlogo)
//                .showImageOnLoading(R.mipmap.splashlogo).cacheOnDisc()
//                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

//        getSplashLogo() ;
        startLogo();
    }

    @Override
    protected void handler(Message msg) {
        switch (msg.what){
            case M_Handler_GetUrlSuccess:
            {
                //下载闪屏图片
                String url = (String)msg.obj;
                mImageLoader.displayImage(url, mImageLogo, mOptions);
            }
            break;
            case M_Handler_GetUrlFailed:

                break;
            case M_Handler_LogoOver: //闪屏结束
                doJumpToMainActivity() ;
                break;
        }
    }

    @Override
    protected void onClickView(View v) {

    }


    /**
     * 获取启动页图片地址
     */
    private void getSplashLogo(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String result = Util.addRpcEvent(RpcEvent.GetSplashUrl);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if(s == 1){
                        String url = obj.getString("img");
                        Message msg = new Message() ;
                        msg.what = M_Handler_GetUrlSuccess;
                        msg.obj = url ;
                        handler.sendMessage(msg);
                    }else{
                        handler.sendEmptyMessage(M_Handler_GetUrlFailed) ;
                    }
                }catch (Exception e){
                    handler.sendEmptyMessage(M_Handler_GetUrlFailed) ;
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

    /**
     * 启动lolo  倒计时
     */
    private void startLogo(){
        new CountDownTimer(1000, 1000) {
            @Override
            public void onFinish() {
                handler.sendEmptyMessage(M_Handler_LogoOver);
            }
            @Override
            public void onTick(long arg0) {
            }

        }.start();
    }


    /**
     * 跳转到主界面
     */
    private  void doJumpToMainActivity(){
        startActivity(new Intent(AppAction.ACTION_LOGIN));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(img_logo!=null){
            img_logo.recycle();
            img_logo = null ;
        }
    }


}
