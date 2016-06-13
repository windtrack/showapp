package com.oo58.livevideoassist.fragment;

import java.lang.reflect.Field;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import cn.nodemedia.nodemediaclient.R;
import com.oo58.livevideoassist.common.AppUrl;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PubChatFragment extends Fragment implements OnClickListener {
	private View rootView;
	private static ListView live_chat_list;
	private static ListView chat_gift_item;
	private static RelativeLayout chat_gift_donghua;
	private static ImageView gift_scroll_img;
	private static ImageView chat_gift_dh_icon1;
	private static TextView chat_gift_dh_say1;
	private static TextView chat_gift_dh_num1;
	private static int screenWidth = 0;
	private DisplayMetrics dm;
	private static RelativeLayout pub_chat_relativelayout;
	public static DisplayImageOptions mOptions;
	private static ImageLoader mImageLoader = null;
	private static String first_name = "";
	private static int num_zong = 0;
	private String g_s_liao = "1";

	public PubChatFragment() {

	}

	public static PubChatFragment getIntance() {
		PubChatFragment pubChatFragment = new PubChatFragment();
		return pubChatFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (rootView == null) {
			this.rootView = inflater.inflate(R.layout.fragment_pubchat,
					container, false);
			live_chat_list = (ListView) rootView
					.findViewById(R.id.live_chat_list);
			chat_gift_item = (ListView) rootView
					.findViewById(R.id.chat_gift_item);
			chat_gift_donghua = (RelativeLayout) rootView
					.findViewById(R.id.chat_gift_donghua);
			gift_scroll_img = (ImageView) rootView
					.findViewById(R.id.gift_scroll_img);
			chat_gift_dh_icon1 = (ImageView) rootView
					.findViewById(R.id.chat_gift_dh_icon1);
			chat_gift_dh_say1 = (TextView) rootView
					.findViewById(R.id.chat_gift_dh_say1);
			chat_gift_dh_num1 = (TextView) rootView
					.findViewById(R.id.chat_gift_dh_num1);
			pub_chat_relativelayout = (RelativeLayout) rootView
					.findViewById(R.id.pub_chat_relativelayout);
			mOptions = new DisplayImageOptions.Builder()
					.showStubImage(R.mipmap.loading_img).cacheInMemory()
					.cacheOnDisc().build();
			mImageLoader = ImageLoader.getInstance();
			mImageLoader.init(ImageLoaderConfiguration
					.createDefault(getActivity()));
			getScreenWH();
		}
		return rootView;
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		num_zong = 0;
		ViewGroup localViewGroup = (ViewGroup) this.rootView.getParent();
		if (localViewGroup != null) {
			localViewGroup.removeView(this.rootView);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		default:
			break;
		}
	}

	public static ListView getPubchatListView() {
		if (live_chat_list != null) {
			return live_chat_list;
		}
		return null;
	}

	public static ListView getChat_gift_item() {
		if (chat_gift_item != null) {
			return chat_gift_item;
		}
		return null;
	}

	public static void StartGiftAnimation(String user_name, String num,
			String icon, int num_aa) {
		if (first_name.equals(user_name)) {
			// Log.i("lvjian", "===========是否累计========>"+(num_zong+num_aa));
			// Log.i("lvjian", "===========num_zong========>"+num_zong);
			// Log.i("lvjian", "===========num_aa========>"+num_aa);
			chat_gift_dh_num1.setText("X  " + (num_zong + num_aa) + "个");
			num_zong = (num_zong + num_aa);
		} else {
			mImageLoader.displayImage(AppUrl.GIFT_PATH + icon, chat_gift_dh_icon1,
					mOptions);
			chat_gift_donghua.setVisibility(View.VISIBLE);
			chat_gift_dh_say1.setText(user_name + " 送: ");
			chat_gift_dh_num1.setText("X  " + num + "个");
			TranslateAnimation animation = new TranslateAnimation(500, 0, 0, 0);
			animation.setDuration(200);
			chat_gift_donghua.startAnimation(animation);
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					// // TODO Auto-generated method stub
	
				}
			});
			first_name = user_name;

		}

	}

	/**
	 * 获取实际屏幕的宽高
	 */
	public void getScreenWH() {
		dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
	}

	public static void setchatlistgone() {
		if (pub_chat_relativelayout != null) {
			pub_chat_relativelayout.setVisibility(View.GONE);
		}
	}

	public static void setchatlistshow() {
		if (pub_chat_relativelayout != null) {
			pub_chat_relativelayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
