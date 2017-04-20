package interfaceApplication;

import java.util.HashMap;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;

import esayhelper.JSONHelper;
import esayhelper.TimeHelper;
import model.AdvertModel;

/**
 * 广告管理
 * 
 *
 */
public class Advert {
	private AdvertModel ads = new AdvertModel();
	private HashMap<String, Object> map = new HashMap<>();
	private JSONObject _obj = new JSONObject();

	public Advert() {
		map.put("adid", ads.getID());
		map.put("addesp", "");
		map.put("adtype", 0);
		map.put("adcreatetime", TimeHelper.nowSecond()+"");
	}

	@SuppressWarnings("unchecked")
	public String AddAD(String adsInfo) {
		JSONObject object = ads.AddMap(map, JSONHelper.string2json(adsInfo));
		_obj.put("records", JSONHelper.string2json(ads.add(object)));
		return ads.resultMessage(0, _obj.toString());
	}

	// 修改广告
	public String UpdateAD(String mid, String msgInfo) {
		return ads.resultMessage(ads.updateMessage(mid, JSONHelper.string2json(msgInfo)),
				"留言修改成功");
	}

	// 删除广告
	public String DeleteAD(String mid) {
		return ads.resultMessage(ads.deleteMessage(mid), "删除留言成功");
	}

	// 批量删除广告
	public String DeleteBatchAD(String mids) {
		return ads.resultMessage(ads.deleteMessage(mids.split(",")), "批量删除留言成功");
	}

	// 广告搜索
	@SuppressWarnings("unchecked")
	public String SearchAD(String msgInfo) {
		_obj.put("records", ads.find(JSONHelper.string2json(msgInfo)));
		return ads.resultMessage(0, _obj.toString());
	}

	// 分页
	@SuppressWarnings("unchecked")
	public String PageAD(int idx, int pageSize) {
		_obj.put("records", ads.page(idx, pageSize));
		return ads.resultMessage(0, _obj.toString());
	}

	// 条件分页
	@SuppressWarnings("unchecked")
	public String PageByAD(int idx, int pageSize, String adsInfo) {
		_obj.put("records", ads.page(idx, pageSize, JSONHelper.string2json(adsInfo)));
		return ads.resultMessage(0, _obj.toString());
	}

	//根据广告类型查询广告信息
	@SuppressWarnings("unchecked")
	public String FindByType(int typeid) {
		_obj.put("records", ads.FindBytype(typeid));
		return ads.resultMessage(0, _obj.toString());
	}
}
