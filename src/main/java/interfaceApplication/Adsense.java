package interfaceApplication;

import java.util.HashMap;

import org.json.simple.JSONObject;

import authority.privilige;
import esayhelper.JSONHelper;
import model.AdsenseModel;

public class Adsense {
	private AdsenseModel ads = new AdsenseModel();
	private HashMap<String, Object> map = new HashMap<>();
	private JSONObject _obj = new JSONObject();
//	private static privilige privilige;
//
//	static{
//		privilige = new privilige("");  //userid
//	}
	public Adsense() {
		map.put("adsdesp", "");
		map.put("adswidth", "100");
		map.put("adsheight", "100");
		map.put("adstype", 1);
		map.put("iseffect", 0); // 广告位是否生效，默认为0，不生效
		map.put("rPlv", 1000);
		map.put("uPlv", 2000);
		map.put("dPlv", 3000);
	}

	@SuppressWarnings("unchecked")
	public String AddADS(String adsInfo) {
//		String code = execRequest._run("GrapeAuth/Auth/InsertPLV", null).toString();
//		if (!"0".equals(code)) {
//			return rolesModel.resultMessage(3, "");
//		}
		JSONObject object = ads.AddMap(map, JSONHelper.string2json(adsInfo));
		_obj.put("records", JSONHelper.string2json(ads.add(object)));
		return ads.resultMessage(0, _obj.toString());
	}

	// 修改广告位
	public String UpdateADS(String mid, String msgInfo) {
		return ads.resultMessage(ads.updateMessage(mid, JSONHelper.string2json(msgInfo)),
				"广告位修改成功");
	}

	// 删除广告位
	public String DeleteADS(String mid) {
		return ads.resultMessage(ads.deleteMessage(mid), "删除广告位成功");
	}

	// 批量删除广告位
	public String DeleteBatchADS(String mids) {
		return ads.resultMessage(ads.deleteMessage(mids.split(",")), "批量删除广告位成功");
	}

	// 广告位搜索
	@SuppressWarnings("unchecked")
	public String SearchADS(String msgInfo) {
		_obj.put("records", ads.find(JSONHelper.string2json(msgInfo)));
		return ads.resultMessage(0, _obj.toString());
	}

	// 分页
	@SuppressWarnings("unchecked")
	public String PageADS(int idx, int pageSize) {
		_obj.put("records", ads.page(idx, pageSize));
		return ads.resultMessage(0, _obj.toString());
	}

	// 条件分页
	@SuppressWarnings("unchecked")
	public String PageByADS(int idx, int pageSize, String adsInfo) {
		_obj.put("records", ads.page(idx, pageSize, JSONHelper.string2json(adsInfo)));
		return ads.resultMessage(0, _obj.toString());
	}

	// 设置广告位是否生效
	public String SetEffect(String adsid, int num) {
		return ads.resultMessage(ads.seteffect(adsid, num), "广告位状态设置成功");
	}
	
}
