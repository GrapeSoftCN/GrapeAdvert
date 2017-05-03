package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import esayhelper.DBHelper;
import esayhelper.formHelper;
import esayhelper.jGrapeFW_Message;
import esayhelper.formHelper.formdef;

public class AdvertModel {
	private static DBHelper ad;
	private static formHelper _form;
	private JSONObject _obj = new JSONObject();
	static {
		ad = new DBHelper("mongodb", "advert");
		_form = ad.getChecker();
	}

	public AdvertModel() {
		_form.putRule("adname", formdef.notNull);
	}

	@SuppressWarnings("unchecked")
	public String add(JSONObject object) {
		String adsid = object.get("adsid").toString();
		object.put("imgURL", object.get("imgURL").toString().split("webapps")[1]);
		if (search(adsid) != null) {
			return resultMessage(2, "");
		}
		if (!_form.checkRuleEx(object)) {
			return resultMessage(1, ""); // 必填字段没有填
		}
		String info = ad.data(object).insertOnce().toString();
		return FindByID(info).toString();
	}

	public int update(String mid, JSONObject object) {
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
			if (object2.toString().equals("_id")) {
				ad.eq("_id", fileInfo.get("_id").toString());
			} else {
				ad.eq(object2.toString(), fileInfo.get(object2.toString()));
			}
		}
		return getImg(ad.limit(30).select());
	}

	@SuppressWarnings("unchecked")
	public JSONObject page(int idx, int pageSize) {
		JSONArray array = ad.page(idx, pageSize);
		JSONObject object = new JSONObject();
		object.put("totalSize", (int) Math.ceil((double) ad.count() / pageSize));
		object.put("currentPage", idx);
		object.put("pageSize", pageSize);
		object.put("data", getImg(array));
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
		object.put("data", getImg(array));
		return object;
	}

	public JSONObject FindByID(String asid) {
		JSONObject object = ad.eq("_id", new ObjectId(asid)).find();
		return getImg(object);
	}

	// 根据广告位id查询广告
	public JSONObject search(String asid) {
		JSONObject object = ad.eq("adsid", asid).find();
		if (object == null) {
			return object;
		}
		JSONObject obj = getADS(object);
		return getImg(obj);
	}

	// 获取不同类型的广告数据
	private JSONObject getADS(JSONObject object) {
		int type = Integer.parseInt(object.get("adtype").toString());
		if (type == 1) {
			object.remove("text");
			object.remove("size");
			object.remove("attribute");
			object.remove("show");
		}
		if (type == 2) {
			object.remove("imgURL");
			object.remove("show");
		}
		return object;
	}

	public JSONArray FindBytype(int tid) {
		JSONArray array = ad.eq("adtype", tid).limit(20).select();
		return getImg(array);
	}

	// 设置广告位（广告id，广告位id）
	@SuppressWarnings("unchecked")
	public int setads(String adid, String adsid) {
		JSONObject object = new JSONObject();
		object.put("adsid", adsid);
		return ad.eq("_id", new ObjectId(adid)).data(object).update() != null ? 0 : 99;
	}

	// 获取图片广告内容
	@SuppressWarnings("unchecked")
	private JSONObject getImg(JSONObject object) {
		String imgURL = object.get("imgURL").toString();
		imgURL = "http://123.57.214.226:8080" + imgURL;
		object.put("imgURL", imgURL);
		return object;
	}

	@SuppressWarnings("unchecked")
	private JSONArray getImg(JSONArray array) {
		JSONArray array2 = new JSONArray();
		for (int i = 0, len = array.size(); i < len; i++) {
			JSONObject object = (JSONObject) array.get(i);
			String imgURL = object.get("imgURL").toString();
			imgURL = "http://123.57.214.226:8080" + imgURL;
			object.put("imgURL", imgURL);
			array2.add(object);
		}

		return array2;
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

	@SuppressWarnings("unchecked")
	public String resultMessage(JSONObject object) {
		_obj.put("records", object);
		return resultMessage(0, _obj.toString());
	}

	@SuppressWarnings("unchecked")
	public String resultMessage(JSONArray array) {
		_obj.put("records", array);
		return resultMessage(0, _obj.toString());
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
		case 2:
			msg = "该广告位已存在广告";
			break;
		default:
			msg = "其它异常";
			break;
		}
		return jGrapeFW_Message.netMSG(num, msg);
	}
}
