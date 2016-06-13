package com.oo58.livevideoassist.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import cn.nodemedia.nodemediaclient.R;
import com.oo58.livevideoassist.common.APPConstants;
import com.oo58.livevideoassist.common.AppUrl;
import com.oo58.livevideoassist.db.GlobalData;
import com.oo58.livevideoassist.rpc.RpcEvent;
import com.oo58.livevideoassist.util.ImageUtil;
import com.oo58.livevideoassist.util.ThreadPoolWrap;
import com.oo58.livevideoassist.util.Util;
import com.oo58.livevideoassist.view.CircleImageView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;

/**
 * @author zhongxf
 * @Description 直播结束的弹出框
 * @Date 2016/5/12.
 */
public class FinishLivingDialog {

    private static final int M_Handler_GetInfoSuccess = 1;//获取结束信息成功
    private static final int M_Handler_GetInfoFailed = 2;//获取结束信息失败

    private Dialog dialog;
    private TextView showGuanzhognNum;//显示观众数
    private TextView addBeans;//显示添新增加的红豆
    private CircleImageView face;//头像
    private TextView name;//名字
    private DisplayImageOptions mOptions;
    private Context cxt;

    private String mUserName; //玩家昵称
    private String mFansCount;//粉丝数
    private String mBenas;//乐币

    /**
     * 显示一个提示框
     */
    public void show(Context context) {
        cxt = context;
        mOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showStubImage(R.mipmap.loginlogo)
                .showImageForEmptyUri(R.mipmap.loginlogo)
                .showImageOnFail(R.mipmap.loginlogo)
                .showImageOnLoading(R.mipmap.loginlogo).cacheOnDisc()
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        creatDialog(context);
        loadInfo();
    }

    //加载主播信息
    private void loadInfo() {
        String headUrl = GlobalData.getInstance(cxt).getSharedPreferences().getString(APPConstants.USER_ICON, "");
        String url = AppUrl.USER_LOGO_ROOT + headUrl;
        if (url != null && !"".equals(url)) {
            GlobalData.getInstance(cxt).getmImageLoader().displayImage(url, face, mOptions, null);
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String uid = GlobalData.getInstance(cxt).getUID();
                    String usc = GlobalData.getInstance(cxt).getUSecert();
                    String roomid = GlobalData.getInstance(cxt).getURoomId();

                    String result = Util.addRpcEvent(RpcEvent.GetResultInfoByEnd, uid, usc, roomid);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        JSONObject info = obj.getJSONObject("data");
                        mFansCount = info.getString("onlines");
                        mBenas = info.getString("beans");
                        mUserName = GlobalData.getInstance(cxt).getUName();

                        handler.sendEmptyMessage(M_Handler_GetInfoSuccess);
                    } else {
                        handler.sendEmptyMessage(M_Handler_GetInfoFailed);
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(M_Handler_GetInfoFailed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case M_Handler_GetInfoSuccess:
                    name.setText(mUserName);
                    showGuanzhognNum.setText(mFansCount);
                    addBeans.setText(mBenas);
                    break;
                case M_Handler_GetInfoFailed:

                    break;
            }
        }
    };

    private void creatDialog(final Context context) {
        dialog = new Dialog(context, R.style.show_dialog);// 创建自定义样式dialog
        View main = LayoutInflater.from(context).inflate(
                R.layout.finish_living, null);
        showGuanzhognNum = (TextView) main.findViewById(R.id.guanzhong_num);
        addBeans = (TextView) main.findViewById(R.id.add_beans);
        face = (CircleImageView) main.findViewById(R.id.showHead);
        name = (TextView) main.findViewById(R.id.name);

        Button returnBtn = (Button) main.findViewById(R.id.return_btn);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ((Activity) context).finish();
            }
        });

        String headUrl = GlobalData.getInstance(context).getSharedPreferences().getString(APPConstants.USER_ICON, "");
        String realPath = AppUrl.USER_LOGO_ROOT + headUrl;
        File file = ImageLoader.getInstance().getDiskCache().get(realPath);
        if (file.exists()) {
            ImageView bg = (ImageView) main.findViewById(R.id.bg);
            Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
            if(bm!=null){
                Drawable drawable = ImageUtil.boxBlurFilter(bm);
                bg.setImageDrawable(drawable);
            }
        }

        LinearLayout.LayoutParams fill_parent = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        fill_parent.width = context.getResources().getDisplayMetrics().widthPixels;
        dialog.setContentView(main, fill_parent);// 设置布局
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    /**
     * 关闭提示框
     */
    public void closeDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

}
