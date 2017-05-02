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
import esayhelper.formHelper;
import esayhelper.jGrapeFW_Message;
import esayhelper.formHelper.formdef;
import rpc.execRequest;

public class AdvertModel {
	private static DBHelper ad;
	private static formHelper _form;
	static {
		ad = new DBHelper("mongodb", "advert");
		_form = ad.getChecker();
	}

	public AdvertModel() {
		_form.putRule("adname", formdef.notNull);
	}

	public String add(JSONObject object) {
		if (!_form.checkRuleEx(object)) {
			return resultMessage(1, ""); // 必填字段没有填
		}
		String info = ad.data(object).insertOnce().toString();
		return FindByID(info).toString();
	}

	public int updateMessage(String mid, JSONObject object) {
		return ad.eq("_id", new ObjectId(mid)).data(object).update() != null ? 0 : 99;
	}

	public int delete(String mid) {
		return ad.eq("_id", new ObjectId(mid)).delete() != null ? 0 : 99;
	}

	public int delete(String[] mids) {
		ad.or();
		int len = mids.length;
		for (int i = 0; i < len; i++) {
			ad.eq("_id", new ObjectId(mids[i]));
		}
		return ad.deleteAll() == len ? 0 : 99;
	}

	public JSONArray find(JSONObject fileInfo) {
		for (Object object2 : fileInfo.keySet()) {
			ad.eq(object2.toString(), fileInfo.get(object2.toString()));
		}
		return geturl(ad.limit(30).select());
	}

	@SuppressWarnings("unchecked")
	public JSONObject page(int idx, int pageSize) {
		JSONArray array = ad.page(idx, pageSize);
		JSONObject object = new JSONObject();
		object.put("totalSize", (int) Math.ceil((double) ad.count() / pageSize));
		object.put("currentPage", idx);
		object.put("pageSize", pageSize);
		object.put("data", geturl(array));
		return object;
	}

	@SuppressWarnings("unchecked")
	public JSONObject page(int idx, int pageSize, JSONObject fileInfo) {
		for (Object object2 : fileInfo.keySet()) {
			ad.eq(object2.toString(), fileInfo.get(object2.toString()));
		}
		JSONArray array = ad.page(idx, pageSize);
		JSONObject object = new JSONObject();
		object.put("totalSize", (int) Math.ceil((double) ad.count() / pageSize));
		object.put("currentPage", idx);
		object.put("pageSize", pageSize);
		object.put("data", geturl(array));
		return object;
	}

	@SuppressWarnings("unchecked")
	public JSONObject FindByID(String asid) {
		JSONObject object = ad.eq("_id", new ObjectId(asid)).find();
		String imgurl = execRequest._run("GrapeFile/Files/geturl/s:" + object.get("img").toString(), null).toString();
		object.put("img", imgurl);
		return object;
	}
	public JSONArray search(String asid,int no) {
		JSONArray array = ad.eq("idsid", asid).limit(no).select();
		return array;
	}

	public JSONArray FindBytype(int tid) {
		return geturl(ad.eq("adtype", tid).limit(20).select());
	}

	// 设置广告位（广告id，广告位id）
	@SuppressWarnings("unchecked")
	public int setads(String adid, String adsid) {
		JSONObject object = new JSONObject();
		object.put("adsid", adsid);
		return ad.eq("_id", new ObjectId(adid)).data(object).update() != null ? 0 : 99;
	}

	// 根据img中的文件id，获取图片地址
	@SuppressWarnings("unchecked")
	public JSONArray geturl(JSONArray array) {
		JSONArray array2 = new JSONArray();
		for (int i = 0, len = array.size(); i < len; i++) {
			JSONObject object = (JSONObject) array.get(i);
			String imgurl = execRequest._run("GrapeFile/Files/geturl/s:" + object.get("img").toString(), null)
					.toString();
			object.put("img", imgurl);
			array2.add(object);
		}
		return array2;
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

	public String resultMessage(JSONObject object) {
		return resultMessage(0, object.toString());
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
