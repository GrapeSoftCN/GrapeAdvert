package interfaceApplication;

import java.util.HashMap;

import org.json.simple.JSONObject;

import esayhelper.JSONHelper;
import model.AdvertModel;

/**
 * 广告管理
 * 
 *
 */
public class Advert {
	private AdvertModel ads = new AdvertModel();
	private HashMap<String, Object> map = new HashMap<>();

	public Advert() {
		map.put("rPlv", 1000);
		map.put("uPlv", 2000);
		map.put("dPlv", 3000);
	}

	public String AddAD(String adsInfo) {
		JSONObject object = ads.AddMap(map, JSONHelper.string2json(adsInfo));
		return ads.add(object);
	}

	// 修改广告
	public String UpdateAD(String mid, String msgInfo) {
		return ads.update(mid, JSONHelper.string2json(msgInfo));
	}

	// 删除广告
	public String DeleteAD(String mid) {
		return ads.delete(mid);
	}

	// 批量删除广告
	public String DeleteBatchAD(String mids) {
		return ads.delete(mids.split(","));
	}

	// 广告搜索
	public String SearchAD(String msgInfo) {
		return ads.find(JSONHelper.string2json(msgInfo));
	}

	// 根据广告位id查询广告
	public String SearchByid(String adsid) {
		return ads.search(adsid);
	}

	// 分页
	public String PageAD(int idx, int pageSize) {
		return ads.page(idx, pageSize);
	}

	// 条件分页
	public String PageByAD(int idx, int pageSize, String adsInfo) {
		return ads.page(idx, pageSize, JSONHelper.string2json(adsInfo));
	}

	// 根据广告类型查询广告信息
	public String FindByType(int typeid) {
		return ads.FindBytype(typeid);
	}
}
