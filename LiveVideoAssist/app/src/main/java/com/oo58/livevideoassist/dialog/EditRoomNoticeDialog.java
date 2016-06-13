package com.oo58.livevideoassist.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import cn.nodemedia.nodemediaclient.R;

/**
 * @author zhongxf
 * @Description 修改直播间公告的弹出框
 * @Date 2016/5/11.
 */
public class EditRoomNoticeDialog extends PopupWindow {
    private View conentView;//弹出框的布局
    private PopupWindow pop;//该Popuwindow对象
    private SendNoticeInterface sni;//发送通知接口
    private EditText editText;//通知输入框

    public EditRoomNoticeDialog(final Activity context, SendNoticeInterface sendNoticeInterface) {
        pop = this;
        this.sni = sendNoticeInterface;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.edit_room_notice, null);
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        LinearLayout rootView = (LinearLayout) conentView.findViewById(R.id.pop_root_view);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.black50));
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);


        editText = (EditText) conentView.findViewById(R.id.notice_input);
        Button sendBtn = (Button) conentView.findViewById(R.id.send_notice_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString();
                if (content != null && !"".equals(content)) {
                    sni.sendNotice(editText.getText().toString());
                    pop.dismiss();
                }

            }
        });
    }
    //显示弹出框
    public void showPopupWindow(View parent,String content) {
        if (!this.isShowing()) {
//            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 18);
            editText.setText(content);
            showAtLocation(parent, Gravity.TOP | Gravity.RIGHT, 0, 0);
        } else {
            this.dismiss();
        }
    }

}
