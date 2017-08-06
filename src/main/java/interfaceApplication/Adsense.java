package interfaceApplication;

import java.util.HashMap;

import org.json.simple.JSONObject;

import json.JSONHelper;
import model.AdsenseModel;
import rpc.execRequest;
import session.session;
import time.TimeHelper;

public class Adsense {
	private session se;
	private AdsenseModel ads = new AdsenseModel();
	private HashMap<String, Object> map = new HashMap<>();
	private JSONObject UserInfo = new JSONObject();
	private String sid = null;

	public Adsense() {
		se = new session();
		sid = (String) execRequest.getChannelValue("sid");
		if (sid != null) {
			UserInfo = se.getSession(sid);
		}
		map.put("adsdesp", "");
		map.put("adswidth", "100");
		map.put("adsheight", "100");
		map.put("createtime", TimeHelper.nowMillis());
		map.put("iseffect", 0); // 广告位是否生效，默认为0，不生效
		map.put("r", 1000);
		map.put("u", 2000);
		map.put("d", 3000);
		map.put("wbid", (UserInfo != null && UserInfo.size() != 0) ? UserInfo.get("currentWeb").toString() : ""); // 所属网站
	}

	// 新增广告位
	public String AddADS(String adsInfo) {
		JSONObject object = ads.AddMap(map, JSONHelper.string2json(adsInfo));
		return ads.add(object);
	}

	// 修改广告位
	public String UpdateADS(String mid, String msgInfo) {
		return ads.update(mid, JSONHelper.string2json(msgInfo));
	}

	// 删除广告位
	public String DeleteADS(String mid) {
		return ads.delete(mid);
	}

	// 批量删除广告位
	public String DeleteBatchADS(String mids) {
		return ads.delete(mids.split(","));
	}

	// 广告位搜索
	public String SearchADS(String msgInfo) {
		return ads.find(JSONHelper.string2json(msgInfo));
	}

	// 分页
	public String PageADS(int idx, int pageSize) {
		return ads.page(idx, pageSize);
	}

	// 条件分页
	public String PageByADS(int idx, int pageSize, String adsInfo) {
		return ads.page(idx, pageSize, JSONHelper.string2json(adsInfo));
	}

	public String PageADSBack(int idx, int pageSize) {
		return ads.page(idx, pageSize);
	}

	// 条件分页
	public String PageByADSBack(int idx, int pageSize, String adsInfo) {
		return ads.page(idx, pageSize, JSONHelper.string2json(adsInfo));
	}

	// 设置广告位是否生效
	public String SetEffect(String adsid, int num) {
		return ads.seteffect(adsid, num);
	}

	public String GetAds(String id) {
		JSONObject object = ads.FindByID(id);
		return object != null ? object.toString() : "";
	}
}
