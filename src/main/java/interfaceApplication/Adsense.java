package interfaceApplication;

import java.util.HashMap;

import org.json.simple.JSONObject;

import esayhelper.JSONHelper;
import esayhelper.TimeHelper;
import model.AdsenseModel;

public class Adsense {
	private AdsenseModel ads = new AdsenseModel();
	private HashMap<String, Object> map = new HashMap<>();
	public Adsense() {
		map.put("adsdesp", "");
		map.put("adswidth", "100");
		map.put("adsheight", "100");
		map.put("createtime", TimeHelper.nowMillis()+"");
		map.put("iseffect", 0); // 广告位是否生效，默认为0，不生效
		map.put("rPlv", 1000);
		map.put("uPlv", 2000);
		map.put("dPlv", 3000);
	}

	//新增广告位
	public String AddADS(String adsInfo) {
		JSONObject object = ads.AddMap(map, JSONHelper.string2json(adsInfo));
		return ads.resultMessage(JSONHelper.string2json(ads.add(object)));
	}

	// 修改广告位
	public String UpdateADS(String mid, String msgInfo) {
		return ads.resultMessage(ads.update(mid, JSONHelper.string2json(msgInfo)),
				"广告位修改成功");
	}

	// 删除广告位
	public String DeleteADS(String mid) {
		return ads.resultMessage(ads.delete(mid), "删除广告位成功");
	}

	// 批量删除广告位
	public String DeleteBatchADS(String mids) {
		return ads.resultMessage(ads.delete(mids.split(",")), "批量删除广告位成功");
	}

	// 广告位搜索
	public String SearchADS(String msgInfo) {
		return ads.resultMessage(ads.find(JSONHelper.string2json(msgInfo)));
	}

	// 分页
	public String PageADS(int idx, int pageSize) {
		return ads.resultMessage(ads.page(idx, pageSize));
	}

	// 条件分页
	public String PageByADS(int idx, int pageSize, String adsInfo) {
		return ads.resultMessage(ads.page(idx, pageSize, JSONHelper.string2json(adsInfo)));
	}

	// 设置广告位是否生效
	public String SetEffect(String adsid, int num) {
		return ads.resultMessage(ads.seteffect(adsid, num), "广告位状态设置成功");
	}
	
	public String GetAds(String id) {
		return ads.FindByID(id).toString();
	}
}
