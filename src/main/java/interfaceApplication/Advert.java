package interfaceApplication;

import java.util.HashMap;

import org.json.simple.JSONObject;

import json.JSONHelper;
import model.AdvertModel;
import rpc.execRequest;
import security.codec;
import session.session;

/**
 * 广告管理
 * 
 *
 */
public class Advert {
	private session se;
	private AdvertModel ads = new AdvertModel();
	private HashMap<String, Object> map = new HashMap<>();
	private JSONObject UserInfo = new JSONObject();
	private String sid = null;

	public Advert() {
		se = new session();
		sid = (String) execRequest.getChannelValue("sid");
		if (sid != null) {
			UserInfo = se.getSession(sid);
		}
		map.put("adname", "");
		map.put("adsid", "0");
		map.put("adwidth", "100");
		map.put("adheight", "100");
		map.put("imgURL", "");
		map.put("linkURL", "");
		map.put("adtype", ""); // 广告类型 1：图片；2：文字；3：图文；4:浮动
		map.put("r", 1000);
		map.put("u", 2000);
		map.put("d", 3000);
		map.put("wbid", (UserInfo != null && UserInfo.size() != 0) ? UserInfo.get("currentWeb").toString() : ""); // 所属网站
		map.put("data", "");
	}

	public String AddAD(String adsInfo) {
		JSONObject object = ads.AddMap(map, JSONHelper.string2json(adsInfo));
		return ads.add(object);
	}

	// 修改广告
	public String UpdateAD(String mid, String msgInfo) {
		msgInfo = codec.DecodeHtmlTag(msgInfo);
		msgInfo = codec.decodebase64(msgInfo);
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
		return ads.page(idx, pageSize,null);
	}

	// 条件分页
	public String PageByAD(int idx, int pageSize, String adsInfo) {
		return ads.page(idx, pageSize, JSONHelper.string2json(adsInfo));
	}

	/**--------------后台广告查询 ----------**/
	public String PageADBack(int idx, int pageSize) {
		return ads.pages(idx, pageSize, null);
	}

	// 条件分页
	public String PageByADBack(int idx, int pageSize, String adsInfo) {
		return ads.pages(idx, pageSize, adsInfo);
	}

	// 根据广告类型查询广告信息
	public String FindByType(int typeid) {
		return ads.FindBytype(typeid);
	}
}
