package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import esayhelper.DBHelper;
/**
 * 广告位
 *
 */
import esayhelper.formHelper;
import esayhelper.jGrapeFW_Message;
import esayhelper.formHelper.formdef;

public class AdsenseModel {
	private static DBHelper ads;
	private static formHelper form;
	static {
		ads = new DBHelper("mongodb", "adsense");
		form = ads.getChecker();
	}

	public AdsenseModel() {
		form.putRule("adsname", formdef.notNull);
	}

	public String add(JSONObject object) {
		if (!form.checkRuleEx(object)) {
			return resultMessage(1, ""); // 必填字段没有填
		}
		String info = ads.data(object).insertOnce().toString();
		return FindByID(info).toString();
	}

	public int updateMessage(String mid, JSONObject object) {
		return ads.eq("_id", new ObjectId(mid)).data(object).update() != null ? 0 : 99;
	}

	public int deleteMessage(String mid) {
		return ads.eq("_id", new ObjectId(mid)).delete() != null ? 0 : 99;
	}

	public int deleteMessage(String[] mids) {
		ads = (DBHelper) ads.or();
		for (int i = 0; i < mids.length; i++) {
			ads.eq("_id", new ObjectId(mids[i]));
		}
		return ads.delete() != null ? 0 : 99;
	}

	public JSONArray find(JSONObject fileInfo) {
		for (Object object2 : fileInfo.keySet()) {
			ads.eq(object2.toString(), fileInfo.get(object2.toString()));
		}
		return ads.limit(30).select();
	}

	@SuppressWarnings("unchecked")
	public JSONObject page(int idx, int pageSize) {
		JSONArray array = ads.page(idx, pageSize);
		JSONObject object = new JSONObject();
		object.put("totalSize", (int) Math.ceil((double) ads.count() / pageSize));
		object.put("currentPage", idx);
		object.put("pageSize", pageSize);
		object.put("data", array);
		return object;
	}

	@SuppressWarnings("unchecked")
	public JSONObject page(int idx, int pageSize, JSONObject fileInfo) {
		for (Object object2 : fileInfo.keySet()) {
			if (fileInfo.containsKey("_id")) {
				ads.eq("_id", new ObjectId(fileInfo.get("_id").toString()));
			}
			ads.eq(object2.toString(), fileInfo.get(object2.toString()));
		}
		JSONArray array = ads.page(idx, pageSize);
		JSONObject object = new JSONObject();
		object.put("totalSize", (int) Math.ceil((double) ads.count() / pageSize));
		object.put("currentPage", idx);
		object.put("pageSize", pageSize);
		object.put("data", array);
		return object;
	}

	public JSONObject FindByID(String asid) {
		return ads.eq("_id", new ObjectId(asid)).find();
	}

	@SuppressWarnings("unchecked")
	public int seteffect(String adsid, int effect) {
		JSONObject object = new JSONObject();
		object.put("iseffect", effect);
		return ads.eq("adsid", new ObjectId(adsid)).data(object).update() != null ? 0 : 99;
	}

	public String getID() {
		String str = UUID.randomUUID().toString();
		return str.replace("-", "");
	}

	/**
	 * 将map添加至JSONObject中
	 * 
	 * @param map
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject AddMap(HashMap<String, Object> map, JSONObject object) {
		if (map.entrySet() != null) {
			Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator.next();
				if (!object.containsKey(entry.getKey())) {
					object.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return object;
	}

	public String resultMessage(int num, String message) {
		String msg = "";
		switch (num) {
		case 0:
			msg = message;
			break;
		case 1:
			msg = "必填项没有填";
			break;
		default:
			msg = "其它异常";
			break;
		}
		return jGrapeFW_Message.netMSG(num, msg);
	}
}