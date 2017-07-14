package interfaceApplication;

import java.util.HashMap;

import org.json.simple.JSONObject;

import json.JSONHelper;
import model.AdsenseModel;
import time.TimeHelper;

public class Adsense {
	private AdsenseModel ads = new AdsenseModel();
	private HashMap<String, Object> map = new HashMap<>();

	public Adsense() {
		map.put("adsdesp", "");
		map.put("adswidth", "100");
		map.put("adsheight", "100");
		map.put("createtime", TimeHelper.nowMillis() + "");
		map.put("iseffect", 0); // 广告位是否生效，默认为0，不生效
		map.put("r", 1000);
		map.put("u", 2000);
		map.put("d", 3000);
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

	// 设置广告位是否生效
	public String SetEffect(String adsid, int num) {
		return ads.seteffect(adsid, num);
	}

	public String GetAds(String id) {
		JSONObject object = ads.FindByID(id);
		return object != null ? object.toString() : "";
	}
}
