package com.oo58.livevideoassist.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

import cn.nodemedia.nodemediaclient.R;

/**
 * Desc: 秀场移植过来的聊天表情处理
 * Created by sunjinfang on 2016/5/17 .
 */
public class SmilyParse {
	private Context mContext;
	private Pattern mPattern;
	private String[] mResArrayText;
	private Map<String, Integer> mResToIcons;
	private List<Map<String, ?>> data = new ArrayList<Map<String, ?>>();
	private Map<String, Integer> gif_map = new HashMap<String, Integer>();

	public SmilyParse(Context context) {
		mContext = context;
		mResArrayText = context.getResources().getStringArray(
				Smily.DEFAULT_SMILY_TEXT);
		mResToIcons = buileResTomipmapMap();
		mPattern = buildPattern();
		data = buileMap();
	}

	private HashMap<String, Integer> buileResTomipmapMap() {

		if (mResArrayText.length != Smily.DEFAULT_SMILY_ICONS.length) {
			throw new IllegalStateException("length is Illegal");
		}
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < mResArrayText.length; i++) {
			map.put(mResArrayText[i], Smily.DEFAULT_SMILY_ICONS[i]);
		}
		return map;
	}

	static class Smily {
		public static final int DEFAULT_SMILY_TEXT = R.array.default_smiley_texts;
		private static final int[] DEFAULT_SMILY_ICONS = { R.mipmap.p0,
				R.mipmap.p1, R.mipmap.p2, R.mipmap.p3, R.mipmap.p4,
				R.mipmap.p5, R.mipmap.p6, R.mipmap.p7, R.mipmap.p8,
				R.mipmap.p9, R.mipmap.p10, R.mipmap.p11, R.mipmap.p12,
				R.mipmap.p13, R.mipmap.p14, R.mipmap.p15, R.mipmap.p16,
				R.mipmap.p17, R.mipmap.p18, R.mipmap.p19, R.mipmap.p20,
				R.mipmap.p21, R.mipmap.p22, R.mipmap.p23, R.mipmap.p24,
				R.mipmap.p25, R.mipmap.p26, R.mipmap.p27, R.mipmap.p28,
				R.mipmap.p29, R.mipmap.p30, R.mipmap.p31, R.mipmap.p32,
				R.mipmap.p33, R.mipmap.p34, R.mipmap.p35, R.mipmap.p36,
				R.mipmap.p37, R.mipmap.p38, R.mipmap.p39, R.mipmap.p40,
				R.mipmap.p41, R.mipmap.p42, R.mipmap.p43, R.mipmap.p44,
				R.mipmap.p45, R.mipmap.p46, R.mipmap.p47, R.mipmap.p48,
				R.mipmap.p49, R.mipmap.p50, R.mipmap.p51, R.mipmap.p52,
				R.mipmap.p53, R.mipmap.p54, R.mipmap.p55, R.mipmap.p56,
				R.mipmap.p57, R.mipmap.p58, R.mipmap.p59, R.mipmap.p60,
				R.mipmap.p61, R.mipmap.p62, R.mipmap.p63, R.mipmap.p64,
				R.mipmap.p65, R.mipmap.p66, R.mipmap.p67, R.mipmap.p68,
				R.mipmap.p69, R.mipmap.p70, R.mipmap.p71, R.mipmap.p72,
				R.mipmap.p73, R.mipmap.p74, R.mipmap.p75, R.mipmap.p76,
				R.mipmap.p77, R.mipmap.p78, R.mipmap.p79, R.mipmap.p80,
				R.mipmap.p81, R.mipmap.p82, R.mipmap.p83, R.mipmap.p84,
				R.mipmap.p85, R.mipmap.p86, R.mipmap.p87, R.mipmap.p88,
				R.mipmap.p89, R.mipmap.p90,

				R.mipmap.d0, R.mipmap.d1, R.mipmap.d2, R.mipmap.d3,
				R.mipmap.d4, R.mipmap.d5, R.mipmap.d6, R.mipmap.d7,
				R.mipmap.d8, R.mipmap.d9, R.mipmap.d10, R.mipmap.d11,
				R.mipmap.d12, R.mipmap.d13, R.mipmap.d14, R.mipmap.d15,
				R.mipmap.d16, R.mipmap.d17, R.mipmap.d18, R.mipmap.d19,
				R.mipmap.d20, R.mipmap.d21, R.mipmap.d22, R.mipmap.d23,
				R.mipmap.d24, R.mipmap.d25, R.mipmap.d26, R.mipmap.d28,
				R.mipmap.d29, R.mipmap.d30, R.mipmap.d31, R.mipmap.d32,
				R.mipmap.d33, R.mipmap.d34, R.mipmap.d35, R.mipmap.d36,
				R.mipmap.d37, R.mipmap.d38, R.mipmap.d39,

				R.mipmap.m0, R.mipmap.m1, R.mipmap.m2, R.mipmap.m3,
				R.mipmap.m4, R.mipmap.m5, R.mipmap.m6, R.mipmap.m7,
				R.mipmap.m8, R.mipmap.m9, R.mipmap.m10, R.mipmap.m11,
				R.mipmap.m12, R.mipmap.m13, R.mipmap.m14, R.mipmap.m15,
				R.mipmap.m16, R.mipmap.m17, R.mipmap.m18, R.mipmap.m19,
				R.mipmap.m20, R.mipmap.m21, R.mipmap.m22, R.mipmap.m23,
				R.mipmap.m24, R.mipmap.m25,

				R.mipmap.t1, R.mipmap.t2, R.mipmap.t3, R.mipmap.t4,
				R.mipmap.t5, R.mipmap.t6, R.mipmap.t7, R.mipmap.t8,
				R.mipmap.t9, R.mipmap.t10, R.mipmap.t11, R.mipmap.t12,
				R.mipmap.t13, R.mipmap.t14, R.mipmap.t15, R.mipmap.t16,
				R.mipmap.t17, R.mipmap.t18, R.mipmap.t19, R.mipmap.t20,
				R.mipmap.t21, R.mipmap.t22, R.mipmap.t23, R.mipmap.t24,
				R.mipmap.t25, R.mipmap.t26, R.mipmap.t27, R.mipmap.t28,
				R.mipmap.t29, R.mipmap.t30, R.mipmap.t31, R.mipmap.t32,
				R.mipmap.t33, R.mipmap.t34, R.mipmap.t35, R.mipmap.t36,
				R.mipmap.t37, R.mipmap.t38,

				R.mipmap.bff, R.mipmap.bf2, R.mipmap.bf3, R.mipmap.bf4,
				R.mipmap.bf5, R.mipmap.bf6, R.mipmap.bf7, R.mipmap.bf8,
				R.mipmap.bf9, R.mipmap.bf10, R.mipmap.bf11,
				R.mipmap.bf12, R.mipmap.bf13, R.mipmap.bf14,
				R.mipmap.bf15, R.mipmap.bf16, R.mipmap.bf17,
				R.mipmap.bf18, R.mipmap.bf19, R.mipmap.bf20,
				R.mipmap.bf21, R.mipmap.bf22, R.mipmap.bf23,
				R.mipmap.bf24, R.mipmap.bf25,

				R.mipmap.gzt1, R.mipmap.gzt2, R.mipmap.gzt3,
				R.mipmap.gzt4, R.mipmap.gzt5, R.mipmap.gzt6,
				R.mipmap.gzt7, R.mipmap.gzt8, R.mipmap.gzt9,
				R.mipmap.gzt10, R.mipmap.gzt11, R.mipmap.gzt12,
				R.mipmap.gzt13, R.mipmap.gzt14, R.mipmap.gzt15,
				R.mipmap.gzt16, R.mipmap.gzt17, R.mipmap.gzt18,
				R.mipmap.gzt19, R.mipmap.gzt20, R.mipmap.gzt21,
				R.mipmap.gzt22, R.mipmap.gzt23, R.mipmap.gzt24,
				R.mipmap.gzt25,

				R.mipmap.meml1, R.mipmap.meml2, R.mipmap.meml3,
				R.mipmap.meml4, R.mipmap.meml5, R.mipmap.meml6,
				R.mipmap.meml7, R.mipmap.meml8, R.mipmap.meml9,
				R.mipmap.meml10, R.mipmap.meml11, R.mipmap.meml12,
				R.mipmap.meml13, R.mipmap.meml14, R.mipmap.meml15,
				R.mipmap.meml16, R.mipmap.meml17, R.mipmap.meml18,
				R.mipmap.meml19, R.mipmap.meml20, R.mipmap.meml21,
				R.mipmap.meml22, R.mipmap.meml23,

				R.mipmap.pdd1, R.mipmap.pdd2, R.mipmap.pdd3,
				R.mipmap.pdd4, R.mipmap.pdd5, R.mipmap.pdd6,
				R.mipmap.pdd7, R.mipmap.pdd8, R.mipmap.pdd9,
				R.mipmap.pdd10, R.mipmap.pdd11, R.mipmap.pdd12,
				R.mipmap.pdd13, R.mipmap.pdd14, R.mipmap.pdd15,
				R.mipmap.pdd16, R.mipmap.pdd17, R.mipmap.pdd18,
				R.mipmap.pdd19, R.mipmap.pdd20, R.mipmap.pdd21,
				R.mipmap.pdd22, R.mipmap.pdd23, R.mipmap.pdd24,
				R.mipmap.pdd25,

				R.mipmap.yct1, R.mipmap.yct2, R.mipmap.yct3,
				R.mipmap.yct4, R.mipmap.yct5, R.mipmap.yct6,
				R.mipmap.yct7, R.mipmap.yct8, R.mipmap.yct9,
				R.mipmap.yct10, R.mipmap.yct11, R.mipmap.yct12,
				R.mipmap.yct13, R.mipmap.yct14, R.mipmap.yct15,
				R.mipmap.yct16, R.mipmap.yct17, R.mipmap.yct18,
				R.mipmap.yct19, R.mipmap.yct20, R.mipmap.yct21,
				R.mipmap.yct22, R.mipmap.yct23, R.mipmap.yct24,
				R.mipmap.yct25,

				R.mipmap.xj1, R.mipmap.xj2, R.mipmap.xj3, R.mipmap.xj4,
				R.mipmap.xj6, R.mipmap.xj7, R.mipmap.xj8, R.mipmap.xj9,
				R.mipmap.xj12, R.mipmap.xj13, R.mipmap.xj14,
				R.mipmap.xj15, R.mipmap.xj17, R.mipmap.xj19,
				R.mipmap.xj21, R.mipmap.xj22, R.mipmap.xj23,
				R.mipmap.xj24, R.mipmap.xj25, R.mipmap.xj26,
				R.mipmap.xj27, R.mipmap.xj28, R.mipmap.xj29,
				R.mipmap.xj30, R.mipmap.xj31, R.mipmap.xj32,
				R.mipmap.xj33, R.mipmap.xj35, R.mipmap.xj36,
				R.mipmap.xj37, R.mipmap.xj38, R.mipmap.xj39,
				R.mipmap.xj41, R.mipmap.xj42, R.mipmap.xj44,
				R.mipmap.xj45, R.mipmap.xj46, R.mipmap.xj47,
				R.mipmap.xj48, R.mipmap.xj49, R.mipmap.xj51,
				R.mipmap.xj52, R.mipmap.xj53, R.mipmap.xj54,
				R.mipmap.xj55, R.mipmap.xj56, R.mipmap.xj57,
				R.mipmap.xj58, R.mipmap.xj59,

				R.mipmap.qbl2, R.mipmap.qbl3, R.mipmap.qbl4,
				R.mipmap.qbl6, R.mipmap.qbl7, R.mipmap.qbl8,
				R.mipmap.qbl9, R.mipmap.qbl10, R.mipmap.qbl11,
				R.mipmap.qbl12, R.mipmap.qbl13, R.mipmap.qbl14,
				R.mipmap.qbl15, R.mipmap.qbl16, R.mipmap.qbl17,
				R.mipmap.qbl19, R.mipmap.qbl20, R.mipmap.qbl21,
				R.mipmap.qbl22, R.mipmap.qbl23, R.mipmap.qbl24,
				R.mipmap.qbl26, R.mipmap.qbl27, R.mipmap.qbl28,
				R.mipmap.qbl32, R.mipmap.qbl33, R.mipmap.qbl34,
				R.mipmap.qbl35, R.mipmap.qbl38, R.mipmap.qbl40,
				R.mipmap.qbl42, R.mipmap.qbl43, R.mipmap.qbl46,
				R.mipmap.qbl47, R.mipmap.qbl48, R.mipmap.qbl51,
				R.mipmap.qbl52, R.mipmap.qbl53, R.mipmap.qbl56,
				R.mipmap.qbl57, R.mipmap.qbl58, R.mipmap.qbl60,
				R.mipmap.qbl61, R.mipmap.qbl63, R.mipmap.qbl66,
				R.mipmap.qbl68, R.mipmap.qbl69, R.mipmap.qbl70,
				R.mipmap.qbl71, R.mipmap.qbl72, R.mipmap.qbl73,
				R.mipmap.qbl74, R.mipmap.qbl76, R.mipmap.qbl77,
				R.mipmap.qbl78, R.mipmap.qbl79, R.mipmap.qbl80,
				R.mipmap.qbl81, R.mipmap.qbl82, R.mipmap.qbl83,

		};
	}

	private List<Map<String, ?>> buileMap() {
		List<Map<String, ?>> listMap = new ArrayList<Map<String, ?>>();
		for (int i = 0; i < mResArrayText.length; i++) {
			HashMap<String, Object> entry = new HashMap<String, Object>();
			entry.put("icon", Smily.DEFAULT_SMILY_ICONS[i]);
			entry.put("text", mResArrayText[i]);
			listMap.add(entry);
		}
		return listMap;
	}

	private Pattern buildPattern() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for (int i = 0; i < mResArrayText.length; i++) {
			sb.append(Pattern.quote(mResArrayText[i]));
			sb.append('|');
		}
		sb.replace(sb.length() - 1, sb.length(), ")");
		return Pattern.compile(sb.toString());
	}

	public List<Map<String, ?>> getData() {
		return data;
	}

	public CharSequence compileStringToDisply(String text) {
		SpannableStringBuilder sb = new SpannableStringBuilder(text);
		Matcher m = mPattern.matcher(text);
		while (m.find()) {
			int resId = mResToIcons.get(m.group());
			sb.setSpan(new ImageSpan(mContext, resId), m.start(), m.end(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return sb;
	}

	public Map<String, Integer> getGif_map() {
		return gif_map;
	}

	public void setGif_map(Map<String, Integer> gif_map) {
		this.gif_map = gif_map;
	}

	public void initGif() {
		gif_map.put("[/狂笑]", R.mipmap.p0);
		gif_map.put("[/大笑]", R.mipmap.p1);
		gif_map.put("[/惊讶]", R.mipmap.p2);
		gif_map.put("[/害羞]", R.mipmap.p3);
		gif_map.put("[/偷笑]", R.mipmap.p4);
		gif_map.put("[/抓狂]", R.mipmap.p5);
		gif_map.put("[/大哭]", R.mipmap.p6);
		gif_map.put("[/色]", R.mipmap.p7);
		gif_map.put("[/坏笑]", R.mipmap.p8);
		gif_map.put("[/发怒]", R.mipmap.p9);
		gif_map.put("[/尴尬]", R.mipmap.p10);
		gif_map.put("[/阴险]", R.mipmap.p11);
		gif_map.put("[/鼓掌]", R.mipmap.p12);
		gif_map.put("[/再见]", R.mipmap.p13);
		gif_map.put("[/无语]", R.mipmap.p14);
		gif_map.put("[/挖鼻]", R.mipmap.p15);
		gif_map.put("[/顶]", R.mipmap.p16);
		gif_map.put("[/胜利]", R.mipmap.p17);
		gif_map.put("[/OK]", R.mipmap.p18);
		gif_map.put("[/拜托]", R.mipmap.p19);
		gif_map.put("[/囧]", R.mipmap.p20);
		gif_map.put("[/淡定]", R.mipmap.p21);
		gif_map.put("[/美女]", R.mipmap.p22);
		gif_map.put("[/靓仔]", R.mipmap.p23);
		gif_map.put("[/神马]", R.mipmap.p24);
		gif_map.put("[/开心]", R.mipmap.p25);
		gif_map.put("[/给力]", R.mipmap.p26);
		gif_map.put("[/飞吻]", R.mipmap.p27);
		gif_map.put("[/眨眼]", R.mipmap.p28);
		gif_map.put("[/V5]", R.mipmap.p29);
		gif_map.put("[/勾引]", R.mipmap.p30);
		gif_map.put("[/泪汪汪]", R.mipmap.p31);
		gif_map.put("[/骂]", R.mipmap.p32);
		gif_map.put("[/炸弹]", R.mipmap.p33);
		gif_map.put("[/菜刀]", R.mipmap.p34);
		gif_map.put("[/帅]", R.mipmap.p35);
		gif_map.put("[/审视]", R.mipmap.p36);
		gif_map.put("[/无语]", R.mipmap.p37);
		gif_map.put("[/无奈]", R.mipmap.p38);
		gif_map.put("[/亲亲]", R.mipmap.p39);
		gif_map.put("[/露大腿]", R.mipmap.p40);
		gif_map.put("[/呵呵]", R.mipmap.p41);
		gif_map.put("[/吐血]", R.mipmap.p42);
		gif_map.put("[/媚眼]", R.mipmap.p43);
		gif_map.put("[/愁人]", R.mipmap.p44);
		gif_map.put("[/肿么了]", R.mipmap.p45);
		gif_map.put("[/调戏]", R.mipmap.p46);
		gif_map.put("[/抽]", R.mipmap.p47);
		gif_map.put("[/哼哼]", R.mipmap.p48);
		gif_map.put("[/鄙视]", R.mipmap.p49);
		gif_map.put("[/围观]", R.mipmap.p50);
		gif_map.put("[/激动]", R.mipmap.p51);
		gif_map.put("[/口水]", R.mipmap.p52);
		gif_map.put("[/热汗]", R.mipmap.p53);
		gif_map.put("[/输了]", R.mipmap.p54);
		gif_map.put("[/石化]", R.mipmap.p55);
		gif_map.put("[/蔑视]", R.mipmap.p56);
		gif_map.put("[/哭]", R.mipmap.p57);
		gif_map.put("[/吐了]", R.mipmap.p58);
		gif_map.put("[/太委屈]", R.mipmap.p59);
		gif_map.put("[/捂脸]", R.mipmap.p60);
		gif_map.put("[/捂左脸]", R.mipmap.p61);
		gif_map.put("[/亲]", R.mipmap.p62);
		gif_map.put("[/吻]", R.mipmap.p63);
		gif_map.put("[/傻笑]", R.mipmap.p64);
		gif_map.put("[/闭眼]", R.mipmap.p65);
		gif_map.put("[/坏坏]", R.mipmap.p66);
		gif_map.put("[/跳跳]", R.mipmap.p67);
		gif_map.put("[/心碎]", R.mipmap.p68);
		gif_map.put("[/红唇]", R.mipmap.p69);
		gif_map.put("[/v5]", R.mipmap.p70);
		gif_map.put("[/八字胡]", R.mipmap.p71);
		gif_map.put("[/变脸]", R.mipmap.p72);
		gif_map.put("[/吃货]", R.mipmap.p73);
		gif_map.put("[/大笑1]", R.mipmap.p74);
		gif_map.put("[/大笑2]", R.mipmap.p75);
		gif_map.put("[/孤寂]", R.mipmap.p76);
		gif_map.put("[/落叶]", R.mipmap.p77);
		gif_map.put("[/哈皮]", R.mipmap.p78);
		gif_map.put("[/惊恐]", R.mipmap.p79);
		gif_map.put("[/囧1]", R.mipmap.p80);
		gif_map.put("[/拉轰]", R.mipmap.p81);
		gif_map.put("[/切克闹]", R.mipmap.p82);
		gif_map.put("[/惬意]", R.mipmap.p83);
		gif_map.put("[/热]", R.mipmap.p84);
		gif_map.put("[/太极]", R.mipmap.p85);
		gif_map.put("[/委屈]", R.mipmap.p86);
		gif_map.put("[/献花]", R.mipmap.p87);
		gif_map.put("[/笑]", R.mipmap.p88);
		gif_map.put("[/真棒]", R.mipmap.p89);
		gif_map.put("[/猪猪]", R.mipmap.p90);

		gif_map.put("[/p跳舞]", R.mipmap.d0);
		gif_map.put("[/p开火]", R.mipmap.d1);
		gif_map.put("[/p眼神]", R.mipmap.d2);
		gif_map.put("[/p唱歌]", R.mipmap.d3);
		gif_map.put("[/p爱心]", R.mipmap.d4);
		gif_map.put("[/p嗯嗯]", R.mipmap.d5);
		gif_map.put("[/p叫]", R.mipmap.d6);
		gif_map.put("[/p锤地]", R.mipmap.d7);
		gif_map.put("[/p街舞]", R.mipmap.d8);
		gif_map.put("[/p黑线]", R.mipmap.d9);
		gif_map.put("[/p喷血]", R.mipmap.d10);
		gif_map.put("[/p滚]", R.mipmap.d11);
		gif_map.put("[/p走路]", R.mipmap.d12);
		gif_map.put("[/p大笑]", R.mipmap.d13);
		gif_map.put("[/p听歌]", R.mipmap.d14);
		gif_map.put("[/p面壁]", R.mipmap.d15);
		gif_map.put("[/p女神]", R.mipmap.d16);
		gif_map.put("[/p鼓掌]", R.mipmap.d17);
		gif_map.put("[/p运动]", R.mipmap.d18);
		gif_map.put("[/p剪刀]", R.mipmap.d19);
		gif_map.put("[/p经过]", R.mipmap.d20);
		gif_map.put("[/p晕]", R.mipmap.d21);
		gif_map.put("[/p早]", R.mipmap.d22);
		gif_map.put("[/p叹气]", R.mipmap.d23);
		gif_map.put("[/p吉他]", R.mipmap.d24);
		gif_map.put("[/p坏笑]", R.mipmap.d25);
		gif_map.put("[/p潜水]", R.mipmap.d26);
		gif_map.put("[/p做梦]", R.mipmap.d28);
		gif_map.put("[/p弹指]", R.mipmap.d29);
		gif_map.put("[/p一指禅]", R.mipmap.d30);
		gif_map.put("[/p女生]", R.mipmap.d31);
		gif_map.put("[/p闪]", R.mipmap.d32);
		gif_map.put("[/p我来了]", R.mipmap.d33);
		gif_map.put("[/p无聊]", R.mipmap.d34);
		gif_map.put("[/p太极]", R.mipmap.d35);
		gif_map.put("[/p捏脸]", R.mipmap.d36);
		gif_map.put("[/p梦话]", R.mipmap.d37);
		gif_map.put("[/p登场]", R.mipmap.d38);
		gif_map.put("[/p哟西]", R.mipmap.d39);

		gif_map.put("[/y示爱]", R.mipmap.m0);
		gif_map.put("[/y再见]", R.mipmap.m1);
		gif_map.put("[/y伤心]", R.mipmap.m2);
		gif_map.put("[/y钱]", R.mipmap.m3);
		gif_map.put("[/y委屈]", R.mipmap.m4);
		gif_map.put("[/y汗]", R.mipmap.m5);
		gif_map.put("[/y爱心]", R.mipmap.m6);
		gif_map.put("[/y吃货]", R.mipmap.m7);
		gif_map.put("[/y黑线]", R.mipmap.m8);
		gif_map.put("[/y帅哥]", R.mipmap.m9);
		gif_map.put("[/y路过]", R.mipmap.m10);
		gif_map.put("[/y不]", R.mipmap.m11);
		gif_map.put("[/y不行]", R.mipmap.m12);
		gif_map.put("[/y爱情]", R.mipmap.m13);
		gif_map.put("[/y流鼻血]", R.mipmap.m14);
		gif_map.put("[/y龇牙]", R.mipmap.m15);
		gif_map.put("[/y晕]", R.mipmap.m16);
		gif_map.put("[/y挖鼻]", R.mipmap.m17);
		gif_map.put("[/y亲]", R.mipmap.m18);
		gif_map.put("[/y嗨]", R.mipmap.m19);
		gif_map.put("[/y人呢]", R.mipmap.m20);
		gif_map.put("[/y洗澡]", R.mipmap.m21);
		gif_map.put("[/y做坏事]", R.mipmap.m22);
		gif_map.put("[/y美女]", R.mipmap.m23);
		gif_map.put("[/y蔑视]", R.mipmap.m24);
		gif_map.put("[/y大笑]", R.mipmap.m25);

		gif_map.put("[/tBYE]", R.mipmap.t1);
		gif_map.put("[/t无所谓]", R.mipmap.t2);
		gif_map.put("[/t砖头]", R.mipmap.t3);
		gif_map.put("[/t听歌]", R.mipmap.t4);
		gif_map.put("[/t擦汗]", R.mipmap.t5);
		gif_map.put("[/t晕]", R.mipmap.t6);
		gif_map.put("[/t宝贝]", R.mipmap.t7);
		gif_map.put("[/t运动]", R.mipmap.t8);
		gif_map.put("[/t鄙视]", R.mipmap.t9);
		gif_map.put("[/t偷吃]", R.mipmap.t10);
		gif_map.put("[/t爱心]", R.mipmap.t11);
		gif_map.put("[/t流泪]", R.mipmap.t12);
		gif_map.put("[/t走你]", R.mipmap.t13);
		gif_map.put("[/t瞌睡]", R.mipmap.t14);
		gif_map.put("[/t烦死了]", R.mipmap.t15);
		gif_map.put("[/t机器人]", R.mipmap.t16);
		gif_map.put("[/t自豪]", R.mipmap.t17);
		gif_map.put("[/t梦游]", R.mipmap.t18);
		gif_map.put("[/t跳舞]", R.mipmap.t19);
		gif_map.put("[/t无语]", R.mipmap.t20);
		gif_map.put("[/t可爱]", R.mipmap.t21);
		gif_map.put("[/t撞墙]", R.mipmap.t22);
		gif_map.put("[/t开心]", R.mipmap.t23);
		gif_map.put("[/t亲]", R.mipmap.t24);
		gif_map.put("[/t烤串]", R.mipmap.t25);
		gif_map.put("[/t吃惊]", R.mipmap.t26);
		gif_map.put("[/t发疯]", R.mipmap.t27);
		gif_map.put("[/t奋斗]", R.mipmap.t28);
		gif_map.put("[/t虔诚]", R.mipmap.t29);
		gif_map.put("[/t了结你]", R.mipmap.t30);
		gif_map.put("[/t生日]", R.mipmap.t31);
		gif_map.put("[/t睡觉]", R.mipmap.t32);
		gif_map.put("[/t发抖]", R.mipmap.t33);
		gif_map.put("[/t施法]", R.mipmap.t34);
		gif_map.put("[/t困]", R.mipmap.t35);
		gif_map.put("[/t亲亲]", R.mipmap.t36);
		gif_map.put("[/t走你1]", R.mipmap.t37);
		gif_map.put("[/t挖鼻]", R.mipmap.t38);

		gif_map.put("[/f好的]", R.mipmap.bff);
		gif_map.put("[/f演讲]", R.mipmap.bf2);
		gif_map.put("[/f鬼脸]", R.mipmap.bf3);
		gif_map.put("[/f超人]", R.mipmap.bf4);
		gif_map.put("[/f口水]", R.mipmap.bf5);
		gif_map.put("[/f呲牙]", R.mipmap.bf6);
		gif_map.put("[/f打酱油]", R.mipmap.bf7);
		gif_map.put("[/f发怒]", R.mipmap.bf8);
		gif_map.put("[/f石头剪刀布]", R.mipmap.bf9);
		gif_map.put("[/f大吼]", R.mipmap.bf10);
		gif_map.put("[/f洒脱]", R.mipmap.bf11);
		gif_map.put("[/f思考]", R.mipmap.bf12);
		gif_map.put("[/f吐]", R.mipmap.bf13);
		gif_map.put("[/f冷]", R.mipmap.bf14);
		gif_map.put("[/f流泪]", R.mipmap.bf15);
		gif_map.put("[/f头晕]", R.mipmap.bf16);
		gif_map.put("[/f鼻血]", R.mipmap.bf17);
		gif_map.put("[/f电锯]", R.mipmap.bf18);
		gif_map.put("[/f雷劈]", R.mipmap.bf19);
		gif_map.put("[/f问好]", R.mipmap.bf20);
		gif_map.put("[/f你好]", R.mipmap.bf21);
		gif_map.put("[/f鼻涕]", R.mipmap.bf22);
		gif_map.put("[/f不知]", R.mipmap.bf23);
		gif_map.put("[/f爱心]", R.mipmap.bf24);
		gif_map.put("[/f巴掌]", R.mipmap.bf25);

		gif_map.put("[/g好的]", R.mipmap.gzt1);
		gif_map.put("[/g擦掌]", R.mipmap.gzt2);
		gif_map.put("[/g哭]", R.mipmap.gzt3);
		gif_map.put("[/g88]", R.mipmap.gzt4);
		gif_map.put("[/g笑]", R.mipmap.gzt5);
		gif_map.put("[/g打滚]", R.mipmap.gzt6);
		gif_map.put("[/g努力]", R.mipmap.gzt7);
		gif_map.put("[/g喇叭]", R.mipmap.gzt8);
		gif_map.put("[/g报警]", R.mipmap.gzt9);
		gif_map.put("[/g鼓掌]", R.mipmap.gzt10);
		gif_map.put("[/g鞭打]", R.mipmap.gzt11);
		gif_map.put("[/g扇]", R.mipmap.gzt12);
		gif_map.put("[/g怕]", R.mipmap.gzt13);
		gif_map.put("[/g无语]", R.mipmap.gzt14);
		gif_map.put("[/g照镜]", R.mipmap.gzt15);
		gif_map.put("[/g亲]", R.mipmap.gzt16);
		gif_map.put("[/g零食]", R.mipmap.gzt17);
		gif_map.put("[/g抱抱]", R.mipmap.gzt18);
		gif_map.put("[/g兔子]", R.mipmap.gzt19);
		gif_map.put("[/g坏笑]", R.mipmap.gzt20);
		gif_map.put("[/g冷]", R.mipmap.gzt21);
		gif_map.put("[/g发怒]", R.mipmap.gzt22);
		gif_map.put("[/g飘走]", R.mipmap.gzt23);
		gif_map.put("[/g捡钱]", R.mipmap.gzt24);
		gif_map.put("[/g舌头]", R.mipmap.gzt25);

		gif_map.put("[/m微笑]", R.mipmap.meml1);
		gif_map.put("[/m胜利]", R.mipmap.meml2);
		gif_map.put("[/m沮丧]", R.mipmap.meml3);
		gif_map.put("[/m踩]", R.mipmap.meml4);
		gif_map.put("[/m口水]", R.mipmap.meml5);
		gif_map.put("[/m胜利2]", R.mipmap.meml6);
		gif_map.put("[/m洗衣服]", R.mipmap.meml7);
		gif_map.put("[/m拍]", R.mipmap.meml8);
		gif_map.put("[/m哭]", R.mipmap.meml9);
		gif_map.put("[/m叹气]", R.mipmap.meml10);
		gif_map.put("[/m戳]", R.mipmap.meml11);
		gif_map.put("[/m坏笑]", R.mipmap.meml12);
		gif_map.put("[/m摇摆]", R.mipmap.meml13);
		gif_map.put("[/m激光]", R.mipmap.meml14);
		gif_map.put("[/m爱心]", R.mipmap.meml15);
		gif_map.put("[/m大笑]", R.mipmap.meml16);
		gif_map.put("[/m哈哈]", R.mipmap.meml17);
		gif_map.put("[/m生气]", R.mipmap.meml18);
		gif_map.put("[/m撒娇]", R.mipmap.meml19);
		gif_map.put("[/m怒气]", R.mipmap.meml20);
		gif_map.put("[/m眨眼]", R.mipmap.meml21);
		gif_map.put("[/m悲伤]", R.mipmap.meml22);
		gif_map.put("[/m生气2]", R.mipmap.meml23);

		gif_map.put("[/d钱]", R.mipmap.pdd1);
		gif_map.put("[/d心]", R.mipmap.pdd2);
		gif_map.put("[/d不]", R.mipmap.pdd3);
		gif_map.put("[/d大吼]", R.mipmap.pdd4);
		gif_map.put("[/d激光]", R.mipmap.pdd5);
		gif_map.put("[/d坏笑]", R.mipmap.pdd6);
		gif_map.put("[/d丑]", R.mipmap.pdd7);
		gif_map.put("[/d糊涂]", R.mipmap.pdd8);
		gif_map.put("[/d便便]", R.mipmap.pdd9);
		gif_map.put("[/d再见]", R.mipmap.pdd10);
		gif_map.put("[/d鄙视]", R.mipmap.pdd11);
		gif_map.put("[/d撒花]", R.mipmap.pdd12);
		gif_map.put("[/d卖萌]", R.mipmap.pdd13);
		gif_map.put("[/d来啊]", R.mipmap.pdd14);
		gif_map.put("[/d天使]", R.mipmap.pdd15);
		gif_map.put("[/d酷]", R.mipmap.pdd16);
		gif_map.put("[/d扎]", R.mipmap.pdd17);
		gif_map.put("[/d饮料]", R.mipmap.pdd18);
		gif_map.put("[/d哭]", R.mipmap.pdd19);
		gif_map.put("[/d鼓掌]", R.mipmap.pdd20);
		gif_map.put("[/d羞]", R.mipmap.pdd21);
		gif_map.put("[/d鬼怪]", R.mipmap.pdd22);
		gif_map.put("[/d光明]", R.mipmap.pdd23);
		gif_map.put("[/d爱心]", R.mipmap.pdd24);
		gif_map.put("[/d哀求]", R.mipmap.pdd25);

		gif_map.put("[/c掀桌]", R.mipmap.yct1);
		gif_map.put("[/c吐]", R.mipmap.yct2);
		gif_map.put("[/c大笑]", R.mipmap.yct3);
		gif_map.put("[/c哭]", R.mipmap.yct4);
		gif_map.put("[/c不甘]", R.mipmap.yct5);
		gif_map.put("[/c棒]", R.mipmap.yct6);
		gif_map.put("[/c压力]", R.mipmap.yct7);
		gif_map.put("[/c狂吐]", R.mipmap.yct8);
		gif_map.put("[/c奔跑]", R.mipmap.yct9);
		gif_map.put("[/c刀]", R.mipmap.yct10);
		gif_map.put("[/c抽烟]", R.mipmap.yct11);
		gif_map.put("[/c热]", R.mipmap.yct12);
		gif_map.put("[/c卖萌]", R.mipmap.yct13);
		gif_map.put("[/c哀求]", R.mipmap.yct14);
		gif_map.put("[/c抠鼻]", R.mipmap.yct15);
		gif_map.put("[/c抛眉]", R.mipmap.yct16);
		gif_map.put("[/c温泉]", R.mipmap.yct17);
		gif_map.put("[/c送花]", R.mipmap.yct18);
		gif_map.put("[/c泪奔]", R.mipmap.yct19);
		gif_map.put("[/c睡觉]", R.mipmap.yct20);
		gif_map.put("[/c冷]", R.mipmap.yct21);
		gif_map.put("[/c汗]", R.mipmap.yct22);
		gif_map.put("[/c酷]", R.mipmap.yct23);
		gif_map.put("[/c黄牌]", R.mipmap.yct24);
		gif_map.put("[/c88]", R.mipmap.yct25);

		gif_map.put("[/x美腿]", R.mipmap.xj1);
		gif_map.put("[/x媚娘]", R.mipmap.xj2);
		gif_map.put("[/x中刀]", R.mipmap.xj3);
		gif_map.put("[/x枣上好]", R.mipmap.xj4);
		gif_map.put("[/x开枪]", R.mipmap.xj6);
		gif_map.put("[/x跳]", R.mipmap.xj7);
		gif_map.put("[/x扯裤]", R.mipmap.xj8);
		gif_map.put("[/x打枪]", R.mipmap.xj9);
		gif_map.put("[/x打枪]", R.mipmap.xj10);
		gif_map.put("[/x打枪]", R.mipmap.xj11);
		gif_map.put("[/x撒钱]", R.mipmap.xj12);
		gif_map.put("[/x鼻血]", R.mipmap.xj13);
		gif_map.put("[/x拍砖]", R.mipmap.xj14);
		gif_map.put("[/x射击]", R.mipmap.xj15);
		gif_map.put("[/x射击]", R.mipmap.xj16);
		gif_map.put("[/x气功]", R.mipmap.xj17);
		gif_map.put("[/x气功]", R.mipmap.xj18);
		gif_map.put("[/x肌肉]", R.mipmap.xj19);
		gif_map.put("[/x肌肉]", R.mipmap.xj20);
		gif_map.put("[/x喝醉]", R.mipmap.xj21);
		gif_map.put("[/x唱歌]", R.mipmap.xj22);
		gif_map.put("[/x妩媚]", R.mipmap.xj23);
		gif_map.put("[/x哭]", R.mipmap.xj24);
		gif_map.put("[/x搞基]", R.mipmap.xj25);
		gif_map.put("[/x西瓜]", R.mipmap.xj26);
		gif_map.put("[/x炫富]", R.mipmap.xj27);
		gif_map.put("[/x拔毛]", R.mipmap.xj28);
		gif_map.put("[/x掉水里]", R.mipmap.xj29);
		gif_map.put("[/x痛苦]", R.mipmap.xj30);
		gif_map.put("[/x奥特曼]", R.mipmap.xj31);
		gif_map.put("[/xV5]", R.mipmap.xj32);
		gif_map.put("[/x苍蝇]", R.mipmap.xj33);
		gif_map.put("[/x苍蝇]", R.mipmap.xj34);
		gif_map.put("[/x抓2]", R.mipmap.xj35);
		gif_map.put("[/x吓人]", R.mipmap.xj36);
		gif_map.put("[/x苍蝇2]", R.mipmap.xj37);
		gif_map.put("[/x拱前]", R.mipmap.xj38);
		gif_map.put("[/x凹凸曼]", R.mipmap.xj39);
		gif_map.put("[/x凹凸曼]", R.mipmap.xj40);
		gif_map.put("[/x搞基2]", R.mipmap.xj41);
		gif_map.put("[/x龟派气功2]", R.mipmap.xj42);
		gif_map.put("[/x龟派气功2]", R.mipmap.xj43);
		gif_map.put("[/x牛郎织女]", R.mipmap.xj44);
		gif_map.put("[/x怕]", R.mipmap.xj45);
		gif_map.put("[/x喷火]", R.mipmap.xj46);
		gif_map.put("[/x碗上好]", R.mipmap.xj47);
		gif_map.put("[/x耍帅]", R.mipmap.xj48);
		gif_map.put("[/x多啦A梦]", R.mipmap.xj49);
		gif_map.put("[/x多啦A梦]", R.mipmap.xj50);
		gif_map.put("[/x球棒]", R.mipmap.xj51);
		gif_map.put("[/x恐惧]", R.mipmap.xj52);
		gif_map.put("[/x派钱]", R.mipmap.xj53);
		gif_map.put("[/x溺水]", R.mipmap.xj54);
		gif_map.put("[/x自拍]", R.mipmap.xj55);
		gif_map.put("[/x偷笑]", R.mipmap.xj56);
		gif_map.put("[/x洗澡]", R.mipmap.xj57);
		gif_map.put("[/x弹]", R.mipmap.xj58);
		gif_map.put("[/x送花]", R.mipmap.xj59);

		gif_map.put("[/q抛眉]", R.mipmap.qbl1);
		gif_map.put("[/q抛眉]", R.mipmap.qbl2);
		gif_map.put("[/q扇扇]", R.mipmap.qbl3);
		gif_map.put("[/q不知]", R.mipmap.qbl4);
		gif_map.put("[/q不知]", R.mipmap.qbl5);
		gif_map.put("[/q化石]", R.mipmap.qbl6);
		gif_map.put("[/q乱跳]", R.mipmap.qbl7);
		gif_map.put("[/q飞吻]", R.mipmap.qbl8);
		gif_map.put("[/q不耐烦]", R.mipmap.qbl9);
		gif_map.put("[/q烧香]", R.mipmap.qbl10);
		gif_map.put("[/q害羞]", R.mipmap.qbl11);
		gif_map.put("[/q很棒]", R.mipmap.qbl12);
		gif_map.put("[/q哈欠]", R.mipmap.qbl13);
		gif_map.put("[/q饱]", R.mipmap.qbl14);
		gif_map.put("[/q叹气]", R.mipmap.qbl15);
		gif_map.put("[/q发烧]", R.mipmap.qbl16);
		gif_map.put("[/q饿]", R.mipmap.qbl17);
		gif_map.put("[/q饿]", R.mipmap.qbl18);
		gif_map.put("[/q晚安]", R.mipmap.qbl19);
		gif_map.put("[/q偷笑]", R.mipmap.qbl20);
		gif_map.put("[/q生日快乐]", R.mipmap.qbl21);
		gif_map.put("[/q叉腰]", R.mipmap.qbl22);
		gif_map.put("[/q嚎叫]", R.mipmap.qbl23);
		gif_map.put("[/q变身]", R.mipmap.qbl24);
		gif_map.put("[/q变身]", R.mipmap.qbl25);
		gif_map.put("[/q擦泪]", R.mipmap.qbl26);
		gif_map.put("[/q头痛]", R.mipmap.qbl27);
		gif_map.put("[/qyes]", R.mipmap.qbl28);
		gif_map.put("[/qyes]", R.mipmap.qbl29);
		gif_map.put("[/qyes]", R.mipmap.qbl30);
		gif_map.put("[/qyes]", R.mipmap.qbl31);
		gif_map.put("[/q泪奔]", R.mipmap.qbl32);
		gif_map.put("[/q啦啦]", R.mipmap.qbl33);
		gif_map.put("[/q洗刷刷]", R.mipmap.qbl34);
		gif_map.put("[/q骑马舞]", R.mipmap.qbl35);
		gif_map.put("[/q骑马舞]", R.mipmap.qbl36);
		gif_map.put("[/q骑马舞]", R.mipmap.qbl37);
		gif_map.put("[/q没眼看]", R.mipmap.qbl38);
		gif_map.put("[/q没眼看]", R.mipmap.qbl39);
		gif_map.put("[/qNo]", R.mipmap.qbl40);
		gif_map.put("[/qNo]", R.mipmap.qbl41);
		gif_map.put("[/q拜拜]", R.mipmap.qbl42);
		gif_map.put("[/q哭泣]", R.mipmap.qbl43);
		gif_map.put("[/q哭泣]", R.mipmap.qbl44);
		gif_map.put("[/q哭泣]", R.mipmap.qbl45);
		gif_map.put("[/q失望]", R.mipmap.qbl46);
		gif_map.put("[/q吐舌]", R.mipmap.qbl47);
		gif_map.put("[/q摸头]", R.mipmap.qbl48);
		gif_map.put("[/q摸头]", R.mipmap.qbl49);
		gif_map.put("[/q摸头]", R.mipmap.qbl50);
		gif_map.put("[/q远望]", R.mipmap.qbl51);
		gif_map.put("[/q耶]", R.mipmap.qbl52);
		gif_map.put("[/q飘过]", R.mipmap.qbl53);
		gif_map.put("[/q飘过]", R.mipmap.qbl54);
		gif_map.put("[/q飘过]", R.mipmap.qbl55);
		gif_map.put("[/q打头]", R.mipmap.qbl56);
		gif_map.put("[/q抱抱]", R.mipmap.qbl57);
		gif_map.put("[/q悠闲]", R.mipmap.qbl58);
		gif_map.put("[/q悠闲]", R.mipmap.qbl59);
		gif_map.put("[/q减肥]", R.mipmap.qbl60);
		gif_map.put("[/q嚎哭]", R.mipmap.qbl61);
		gif_map.put("[/q嚎哭]", R.mipmap.qbl62);
		gif_map.put("[/q送花]", R.mipmap.qbl63);
		gif_map.put("[/q送花]", R.mipmap.qbl64);
		gif_map.put("[/q送花]", R.mipmap.qbl65);
		gif_map.put("[/q弹头]", R.mipmap.qbl66);
		gif_map.put("[/q耻笑]", R.mipmap.qbl67);
		gif_map.put("[/q耻笑]", R.mipmap.qbl68);
		gif_map.put("[/q揉捏]", R.mipmap.qbl69);
		gif_map.put("[/q拍手]", R.mipmap.qbl70);
		gif_map.put("[/q飘走]", R.mipmap.qbl71);
		gif_map.put("[/q口水]", R.mipmap.qbl72);
		gif_map.put("[/q舔]", R.mipmap.qbl73);
		gif_map.put("[/q呼啦圈]", R.mipmap.qbl74);
		gif_map.put("[/q呼啦圈]", R.mipmap.qbl75);
		gif_map.put("[/q捏]", R.mipmap.qbl76);
		gif_map.put("[/q思考]", R.mipmap.qbl77);
		gif_map.put("[/q走路1]", R.mipmap.qbl78);
		gif_map.put("[/q写作业]", R.mipmap.qbl79);
		gif_map.put("[/q噎住]", R.mipmap.qbl80);
		gif_map.put("[/qHi]", R.mipmap.qbl81);
		gif_map.put("[/q闻]", R.mipmap.qbl82);
		gif_map.put("[/q敬礼]", R.mipmap.qbl83);

	}
}
