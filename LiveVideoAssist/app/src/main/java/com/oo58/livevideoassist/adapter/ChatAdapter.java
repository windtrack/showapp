package com.oo58.livevideoassist.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import cn.nodemedia.nodemediaclient.R;
import com.oo58.livevideoassist.common.AppUrl;
import com.oo58.livevideoassist.db.GlobalData;
import com.oo58.livevideoassist.entity.ChatMessage;
import com.oo58.livevideoassist.util.ImageUrl;
import com.oo58.livevideoassist.util.SmilyParse;
import com.oo58.livevideoassist.util.Util;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * Desc: 聊天消息适配
 * Created by sunjinfang on 2016/5/17 .
 */
public class ChatAdapter extends BaseAdapter {
	private Context context;
	private List<ChatMessage> messages;
	public static DisplayImageOptions mOptions;
	private ImageLoader mImageLoader = null;
	private SmilyParse sp;
	private SharedPreferences sharedPreferences;

	public ChatAdapter() {
		super();
	}

	public ChatAdapter(Context context, List<ChatMessage> messages) {
		super();
		this.context = context;
		this.messages = messages;
		sp = new SmilyParse(context);
		mOptions = new DisplayImageOptions.Builder()
				.showStubImage(R.mipmap.loading_img).cacheInMemory()
				.cacheOnDisc().build();
		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
		sharedPreferences = GlobalData.getInstance(context).getSharedPreferences() ;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return messages.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return messages.get(position);
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 5;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		if (messages.get(position).getTid() == 30
				|| messages.get(position).getTid() == 29
				|| messages.get(position).getTid() == 8
				|| messages.get(position).getTid() == 3
				|| messages.get(position).getTid() == 230) {
			return 0;
		} else if (messages.get(position).getTid() == 7
				|| messages.get(position).getTid() == 42) {
			return 2;
		} else if (messages.get(position).getTid() == 33) {
			return 4;
		} else {
			return 1;
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		ViewHolder2 viewHolder2 = null;
		ViewHolder3 viewHolder3 = null;
		ViewHolder4 viewHolder4 = null;
		int m = messages.get(position).getTid();
		int tid = getItemViewType(position);
		if (convertView == null) {
			switch (tid) {
			case 1:
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.chat_message_item, parent, false);
				holder.say = (TextView) convertView
						.findViewById(R.id.message_say);
				holder.context = (TextView) convertView
						.findViewById(R.id.message_content);
				holder.imgView_cost = (ImageView) convertView
						.findViewById(R.id.imageView_chat_cost);
				holder.imgView_vip = (ImageView) convertView
						.findViewById(R.id.imageView_chat_vip);
				convertView.setTag(holder);
				break;
			case 0:
				viewHolder2 = new ViewHolder2();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.chat_message_item2, parent, false);
				viewHolder2.say2 = (TextView) convertView
						.findViewById(R.id.message_say_2);
				viewHolder2.context_img = (ImageView) convertView
						.findViewById(R.id.message_img);
				convertView.setTag(viewHolder2);
				break;
			case 2:
				viewHolder3 = new ViewHolder3();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.chat_message_item3, parent, false);
				viewHolder3.say3 = (TextView) convertView
						.findViewById(R.id.message_say_3);
				convertView.setTag(viewHolder3);
				break;
			case 4:
				viewHolder4 = new ViewHolder4();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.chat_message_item_xinyungift, parent, false);
				viewHolder4.message_say_5 = (TextView) convertView
						.findViewById(R.id.message_say_5);
				convertView.setTag(viewHolder4);
				break;
			default:
				break;
			}
		} else {
			switch (tid) {
			case 1:
				holder = (ViewHolder) convertView.getTag();
				break;
			case 0:
				viewHolder2 = (ViewHolder2) convertView.getTag();
				break;
			case 2:
				viewHolder3 = (ViewHolder3) convertView.getTag();
				break;
			case 4:
				viewHolder4 = (ViewHolder4) convertView.getTag();
				break;
			default:
				break;
			}
		}
		ChatMessage chatMessage = messages.get(position) ;
		switch (tid) {
		case 1:
			String s = messages.get(position).getSay();
			String msg = messages.get(position).getContent();
//			CharSequence charSequence = msg;
//			CharSequence charSequence = sp.compileStringToDisply(msg);

			CharSequence charSequence = sp.compileStringToDisply(msg);
			holder.context.setText(charSequence);

//			holder.context.setText(charSequence);

			Util.setCostLv(chatMessage.getCost_level(),holder.imgView_cost,context);
			Util.setVipLv(chatMessage.getVip_lv(),holder.imgView_vip,context);

			// 是否是守护（不为空）
			if (!Util.isEmpty(messages.get(position).getIs_shouhu())) {
				SpannableString s3 = new SpannableString(s);
				String nickname = GlobalData.getInstance(context).getUName() ;
				if (Util.isEmpty(messages.get(position).getT_id())
						|| messages.get(position).getT_id().length() < 3) {
					if (messages.get(position).getSname().equals(nickname)) {
						s3.setSpan(new ForegroundColorSpan(context
								.getResources().getColor(R.color.zijicolor)),
								0, messages.get(position).getSname().length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					} else {
						s3.setSpan(new ClickableSpan() {
							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								//对某人私聊删除
//								SiLiao s = new SiLiao();
//								s.setName(messages.get(position).getSname());
//								s.setId(messages.get(position).getS_id());
//								LiveRoomActivity.showprichat(s);
							}
						}, 0, messages.get(position).getSname().length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						s3.setSpan(
								new ForegroundColorSpan(context.getResources()
										.getColor(R.color.chat_zi_color)), 0,
								messages.get(position).getSname().length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}

				} else {
					if (messages.get(position).getSname().equals(nickname)) {
						s3.setSpan(new ForegroundColorSpan(context
								.getResources().getColor(R.color.zijicolor)),
								0, messages.get(position).getSname().length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					} else {
						s3.setSpan(new ClickableSpan() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
//								SiLiao s = new SiLiao();
//								s.setName(messages.get(position).getSname());
//								s.setId(messages.get(position).getS_id());
//								LiveRoomActivity.showprichat(s);
							}
						}, 0, messages.get(position).getSname().length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						s3.setSpan(
								new ForegroundColorSpan(context.getResources()
										.getColor(R.color.chat_zi_color)), 0,
								messages.get(position).getSname().length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					if (messages.get(position).getTname().equals(nickname)) {
						s3.setSpan(
								new ForegroundColorSpan(context.getResources()
										.getColor(R.color.zijicolor)),
								(messages.get(position).getSname().length() + " 对 "
										.length()),
								(messages.get(position).getSname().length()
										+ " 对 ".length() + messages
										.get(position).getTname().length()),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					} else {
						s3.setSpan(
								new ClickableSpan() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
//										SiLiao s = new SiLiao();
//										s.setName(messages.get(position)
//												.getTname());
//										s.setId(messages.get(position)
//												.getT_id());
//										LiveRoomActivity.showprichat(s);
									}
								},
								(messages.get(position).getSname().length() + " 对 "
										.length()),
								(messages.get(position).getSname().length()
										+ " 对 ".length() + messages
										.get(position).getTname().length()),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						s3.setSpan(
								new ForegroundColorSpan(context.getResources()
										.getColor(R.color.chat_zi_color)),
								(messages.get(position).getSname().length() + " 对 "
										.length()),
								(messages.get(position).getSname().length()
										+ " 对 ".length() + messages
										.get(position).getTname().length()),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}

				if ("1".equals(messages.get(position).getIs_shouhu())) {

					s3.setSpan(new TextAppearanceSpan(context, R.style.style1),
							0, (messages.get(position).getSname().length()),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					holder.say.setText(s3);
					holder.context.setTextColor(context.getResources()
							.getColor(R.color.systemmsg));
				} else if ("2".equals(messages.get(position).getIs_shouhu())) {
					s3.setSpan(new TextAppearanceSpan(context, R.style.style1),
							0, (messages.get(position).getSname().length()),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					holder.say.setText(s3);
					holder.context.setTextColor(context.getResources()
							.getColor(R.color.white));
				} else {
					holder.say.setText(s3);
					holder.context.setTextColor(context.getResources()
							.getColor(R.color.white));
				}




				holder.say.setMovementMethod(LinkMovementMethod.getInstance());
			} else {
				// 不是守护
				SpannableString s3 = new SpannableString(s);
				String nickname = GlobalData.getInstance(context).getUName();
				String uid = GlobalData.getInstance(context).getUID();
				if (Util.isEmpty(messages.get(position).getT_id())
						|| messages.get(position).getT_id().length() < 3) {
					if (messages.get(position).getSname().equals(nickname)) {
						s3.setSpan(new ForegroundColorSpan(context
								.getResources().getColor(R.color.zijicolor)),
								0, messages.get(position).getSname().length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					} else {
						s3.setSpan(new ClickableSpan() {
							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
//								SiLiao s = new SiLiao();
//								s.setName(messages.get(position).getSname());
//								s.setId(messages.get(position).getS_id());
//								LiveRoomActivity.showprichat(s);
							}
						}, 0, messages.get(position).getSname().length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						s3.setSpan(
								new ForegroundColorSpan(context.getResources()
										.getColor(R.color.loginbtnnormal)), 0,
								messages.get(position).getSname().length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}

				} else {
					if (messages.get(position).getSname().equals(nickname)) {
						s3.setSpan(new ForegroundColorSpan(context
								.getResources().getColor(R.color.loginbtnnormal)),
								0, messages.get(position).getSname().length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					} else {
						s3.setSpan(new ClickableSpan() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
//								SiLiao s = new SiLiao();
//								s.setName(messages.get(position).getSname());
//								s.setId(messages.get(position).getS_id());
//								LiveRoomActivity.showprichat(s);
							}
						}, 0, messages.get(position).getSname().length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						s3.setSpan(
								new ForegroundColorSpan(context.getResources()
										.getColor(R.color.loginbtnnormal)), 0,
								messages.get(position).getSname().length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					//是 我 说
					if ((messages.get(position).getT_id()+"").equals(uid) && chatMessage.isPrivateChat()) {
						s3.setSpan(
								new ForegroundColorSpan(context.getResources().getColor(R.color.listViewFgx)),
								(messages.get(position).getSname().length() + " 悄悄的对 ".length()),
								(messages.get(position).getSname().length() + " 悄悄的对 ".length() + messages.get(position).getTname().length()),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					} else {
						s3.setSpan(
								new ClickableSpan() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
//										SiLiao s = new SiLiao();
//										s.setName(messages.get(position)
//												.getTname());
//										s.setId(messages.get(position)
//												.getT_id());
//										LiveRoomActivity.showprichat(s);
									}
								},
								(messages.get(position).getSname().length() + " 对 ".length()),
								(messages.get(position).getSname().length() + " 对 ".length() + messages.get(position).getTname().length()),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						s3.setSpan(
								new ForegroundColorSpan(
										context.getResources().getColor(R.color.white)),
										(messages.get(position).getSname().length() + " 对 ".length()),
										(messages.get(position).getSname().length() + " 对 ".length() + messages.get(position).getTname().length()),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
				holder.say.setText(s3);
				holder.context.setTextColor(context.getResources().getColor(
						R.color.white));
				holder.say.setMovementMethod(LinkMovementMethod.getInstance());
			}
			break;
		case 0:
			// 座驾
			if (messages.get(position).getTid() == 8) {
				String nickname = GlobalData.getInstance(context).getUName();
				String say = messages.get(position).getSay();
				if (!Util.isEmpty(messages.get(position).getIs_shouhu())) {
					if ("1".equals(messages.get(position).getIs_shouhu())
							|| "2".equals(messages.get(position).getIs_shouhu())) {
						SpannableString s2 = new SpannableString(say);
						if (messages.get(position).getSname().equals(nickname)) {
							s2.setSpan(
									new ForegroundColorSpan(context
											.getResources().getColor(
													R.color.zijicolor)),
									0,
									(messages.get(position).getSname().length()),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						} else {
							s2.setSpan(
									new ClickableSpan() {
										@Override
										public void onClick(View arg0) {
											// TODO Auto-generated method stub
//											SiLiao s = new SiLiao();
//											s.setName(messages.get(position)
//													.getSname());
//											s.setId(messages.get(position)
//													.getS_id());
//											LiveRoomActivity.showprichat(s);
										}
									}, 0, (messages.get(position).getSname()
											.length()),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							s2.setSpan(new TextAppearanceSpan(context,
									R.style.style1), 0, (messages.get(position)
									.getSname().length()),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}

						viewHolder2.say2.setText(s2);
						viewHolder2.say2.setMovementMethod(LinkMovementMethod
								.getInstance());
					} else {
						SpannableString s3 = new SpannableString(say);
						if (messages.get(position).getSname().equals(nickname)) {
							s3.setSpan(
									new ForegroundColorSpan(context
											.getResources().getColor(
													R.color.zijicolor)),
									0,
									(messages.get(position).getSname().length()),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						} else {
							s3.setSpan(
									new ClickableSpan() {
										@Override
										public void onClick(View arg0) {
											// TODO Auto-generated method stub
//											SiLiao s = new SiLiao();
//											s.setName(messages.get(position)
//													.getSname());
//											s.setId(messages.get(position)
//													.getS_id());
//											LiveRoomActivity.showprichat(s);
										}
									}, 0, (messages.get(position).getSname()
											.length()),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							s3.setSpan(
									new ForegroundColorSpan(context
											.getResources().getColor(
													R.color.chat_zi_color)),
									0,
									(messages.get(position).getSname().length()),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						viewHolder2.say2.setText(s3);
						viewHolder2.say2.setMovementMethod(LinkMovementMethod
								.getInstance());
					}
				} else {
					SpannableString s3 = new SpannableString(say);
					if (messages.get(position).getSname().equals(nickname)) {
						s3.setSpan(new ForegroundColorSpan(context
								.getResources().getColor(R.color.zijicolor)),
								0,
								(messages.get(position).getSname().length()),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					} else {
						s3.setSpan(new ClickableSpan() {
							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
//								SiLiao s = new SiLiao();
//								s.setName(messages.get(position).getSname());
//								s.setId(messages.get(position).getS_id());
//								LiveRoomActivity.showprichat(s);
							}
						}, 0, messages.get(position).getSname().length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						s3.setSpan(
								new ForegroundColorSpan(context.getResources()
										.getColor(R.color.chat_zi_color)), 0,
								messages.get(position).getSname().length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					viewHolder2.say2.setText(s3);
					viewHolder2.say2.setMovementMethod(LinkMovementMethod
							.getInstance());
				}
				String str1 = AppUrl.CAR_PATH + messages.get(position).getIcon()
						+ "?v=1";
				RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) viewHolder2.context_img
						.getLayoutParams();
				viewHolder2.context_img.getLayoutParams();
				lParams.width = lParams.WRAP_CONTENT;
				lParams.height = lParams.WRAP_CONTENT;
				viewHolder2.context_img.setLayoutParams(lParams);
				mImageLoader.displayImage(str1, viewHolder2.context_img,
						mOptions);
			} else if (messages.get(position).getTid() == 3) {
				// 红豆
				String sname2 = messages.get(position).getSname();
				if (!Util.isEmpty(messages.get(position).getIs_shouhu())) {
					if ("1".equals(messages.get(position).getIs_shouhu())
							|| "2".equals(messages.get(position).getIs_shouhu())) {
						SpannableString s2 = new SpannableString(sname2 + "送:");
						s2.setSpan(new TextAppearanceSpan(context,
								R.style.style1), 0, (messages.get(position)
								.getSname().length()),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						viewHolder2.say2.setText(s2);
					} else {
						viewHolder2.say2.setText(sname2 + " 送:");
					}

				} else {
					viewHolder2.say2.setText(sname2 + " 送:");
				}

				RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) viewHolder2.context_img
						.getLayoutParams();
				viewHolder2.context_img.getLayoutParams();
				lParams.width = lParams.WRAP_CONTENT;
				lParams.height = lParams.WRAP_CONTENT;
				viewHolder2.context_img.setLayoutParams(lParams);
				viewHolder2.context_img.setImageResource(R.mipmap.red_bean);
			}
			// 抽取魔幻卡牌获奖
			else if (messages.get(position).getTid() == 230) {
				String s0 = messages.get(position).getSay();
				String str1 = AppUrl.GIFT_PATH + messages.get(position).getTname();
				SpannableString s2 = new SpannableString(s0);
				s2.setSpan(new TextAppearanceSpan(context, R.style.style1), 0,
						s0.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				viewHolder2.say2.setText(s2);
				RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) viewHolder2.context_img
						.getLayoutParams();
				viewHolder2.context_img.getLayoutParams();
				lParams.width = lParams.WRAP_CONTENT;
				lParams.height = lParams.WRAP_CONTENT;
				viewHolder2.context_img.setLayoutParams(lParams);
				mImageLoader.displayImage(str1, viewHolder2.context_img,
						mOptions);
			} else {
				// 骰子和猜拳
				String s0 = messages.get(position).getSay();
				viewHolder2.say2.setText(s0);
				RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) viewHolder2.context_img
						.getLayoutParams();
				viewHolder2.context_img.getLayoutParams();
				lParams.width = lParams.WRAP_CONTENT;
				lParams.height = lParams.WRAP_CONTENT;
				viewHolder2.context_img.setLayoutParams(lParams);
				if (messages.get(position).getTid() == 29) {
					int index = Integer.parseInt(messages.get(position).getContent());
					viewHolder2.context_img.setImageResource(ImageUrl.Img_Diec[index-1]);
				} else {
					if (messages.get(position).getContent().equals("1")) {
						viewHolder2.context_img
								.setImageResource(R.mipmap.shitou);
					} else if (messages.get(position).getContent().equals("2")) {
						viewHolder2.context_img
								.setImageResource(R.mipmap.jiandao);
					} else {
						viewHolder2.context_img.setImageResource(R.mipmap.bu);
					}
				}
			}
			break;
		case 2:
			if (messages.get(position).getTid() == 7) {
				String say = messages.get(position).getSay();
				// 如果是游客
				if (!Util.isEmpty(messages.get(position).getUser_type())
						&& "guest"
								.equals(messages.get(position).getUser_type())) {
					SpannableString s2 = new SpannableString(say);
					s2.setSpan(new ForegroundColorSpan(context.getResources()
							.getColor(R.color.chat_zi_color)), "欢迎  ".length(),
							("欢迎  ".length() + messages.get(position)
									.getSname().length()),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					viewHolder3.say3.setText(s2);
				} else {
					if (!Util.isEmpty(messages.get(position).getIs_shouhu())) {
						if ("1".equals(messages.get(position).getIs_shouhu())
								|| "2".equals(messages.get(position)
										.getIs_shouhu())) {
							SpannableString s2 = new SpannableString(say);
							s2.setSpan(new TextAppearanceSpan(context,
									R.style.style1), 3, (messages.get(position)
									.getSname().length() + 4),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							viewHolder3.say3.setText(s2);
						} else {
							viewHolder3.say3.setText(say);
						}

					} else {
						SpannableString s3 = new SpannableString(say);
						String nickname = GlobalData.getInstance(context).getUName();
						String sname = messages.get(position).getSname();
						if (nickname.equals(messages.get(position).getSname())) {
							s3.setSpan(
									new ForegroundColorSpan(context
											.getResources().getColor(
													R.color.zijicolor)), "欢迎  "
											.length(),
									("欢迎  ".length() + messages.get(position)
											.getSname().length()),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						} else {
							s3.setSpan(
									new ClickableSpan() {
										@Override
										public void onClick(View arg0) {
											// TODO Auto-generated method stub
//											SiLiao s = new SiLiao();
//											s.setName(messages.get(position)
//													.getSname());
//											s.setId(messages.get(position)
//													.getS_id());
//											LiveRoomActivity.showprichat(s);
										}
									}, "欢迎  ".length(),
									("欢迎  ".length() + messages.get(position)
											.getSname().length()),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							s3.setSpan(
									new ForegroundColorSpan(context
											.getResources().getColor(
													R.color.systemmsg)),
									"欢迎  ".length(),
									("欢迎  ".length() + messages.get(position)
											.getSname().length()),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						viewHolder3.say3.setText(s3);
						viewHolder3.say3.setMovementMethod(LinkMovementMethod
								.getInstance());

					}
				}
			} else {
				String say = messages.get(position).getSay();
				SpannableString s1 = new SpannableString(say);
				s1.setSpan(new TextAppearanceSpan(context, R.style.style1), 0,
						(say.length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				viewHolder3.say3.setText(s1);
			}

			break;
		case 4:
			if (!Util.isEmpty(messages.get(position).getTname())) {
				viewHolder4.message_say_5.setText(messages.get(position)
						.getTname());
			}
			break;
		default:
			break;
		}
		return convertView;
	}

	class ViewHolder {
		TextView say;
		TextView context;
		ImageView imgView_cost;
		ImageView imgView_vip;
	}

	class ViewHolder2 {
		TextView say2;
		ImageView context_img;
	}

	class ViewHolder3 {
		TextView say3;
	}

	class ViewHolder4 {
		TextView message_say_5;
	}


	public void doAutoDelete(){
		if(getCount()>=30){
			messages.remove(0);
		}
	}

}
