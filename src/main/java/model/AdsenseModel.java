package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import JGrapeSystem.jGrapeFW_Message;
import apps.appsProxy;
import check.formHelper;
import check.formHelper.formdef;
import database.DBHelper;
import database.db;

import nlogger.nlogger;

/**
 * 广告位
 * 
 *
 */
public class AdsenseModel {
	private static DBHelper ads;
	private static formHelper form;
	private JSONObject _obj = new JSONObject();

	static {
		ads = new DBHelper(appsProxy.configValue().get("db").toString(), "adsense");
		form = ads.getChecker();
	}

	private db bind(){
		return ads.bind(String.valueOf(appsProxy.appid()));
	}
	public AdsenseModel() {
		form.putRule("adsname", formdef.notNull);
	}

	public String add(JSONObject object) {
		String info = "";
		try {
			if (object != null) {
				if (!form.checkRuleEx(object)) {
					return resultMessage(1, ""); // 必填字段没有填
				}
				info = bind().data(object).insertOnce().toString();
			}
		} catch (Exception e) {
			nlogger.logout(e);
			info = "";
		}
		if (("").equals(info)) {
			return resultMessage(99);
		}
		JSONObject object2 = FindByID(info);
		return resultMessage(object2);
	}

	public String update(String mid, JSONObject object) {
		//获取该条数据权限
		
		JSONObject obj = bind().eq("_id", new ObjectId(mid)).data(object).update();
		return obj != null ? resultMessage(0, "广告位修改成功") : resultMessage(99);
	}

	public String delete(String mid) {
		if (mid.contains(",")) {
			return resultMessage(99);
		}
		JSONObject obj = bind().eq("_id", new ObjectId(mid)).delete();
		return obj != null ? resultMessage(0, "广告位删除成功") : resultMessage(99);
	}

	public String delete(String[] mids) {
		bind().or();
		for (int i = 0; i < mids.length; i++) {
			bind().eq("_id", new ObjectId(mids[i]));
		}
		return bind().deleteAll() == mids.length ? resultMessage(0, "广告位删除成功") : resultMessage(99);
	}

	public String find(JSONObject fileInfo) {
		JSONArray array = null;
		if (fileInfo != null) {
			try {
				array = new JSONArray();
				for (Object object2 : fileInfo.keySet()) {
					if (object2.toString().equals("_id")) {
						bind().eq("_id", new ObjectId(fileInfo.get("_id").toString()));
					} else {
						bind().eq(object2.toString(), fileInfo.get(object2.toString()));
					}
				}
				array = bind().limit(30).select();
			} catch (Exception e) {
				nlogger.logout(e);
				array = null;
			}
		}
		return resultMessage(array);
	}

	@SuppressWarnings("unchecked")
	public String page(int idx, int pageSize) {
		JSONObject object = null;
		try {
			object = new JSONObject();
			JSONArray array = bind().page(idx, pageSize);
			object.put("totalSize", (int) Math.ceil((double) bind().count() / pageSize));
			object.put("currentPage", idx);
			object.put("pageSize", pageSize);
			object.put("data", array);
		} catch (Exception e) {
			nlogger.logout(e);
			object = null;
		}
		return resultMessage(object);
	}

	@SuppressWarnings("unchecked")
	public String page(int idx, int pageSize, JSONObject fileInfo) {
		JSONObject object = null;
		if (fileInfo != null) {
			try {
				object = new JSONObject();
				for (Object object2 : fileInfo.keySet()) {
					if (fileInfo.containsKey("_id")) {
						bind().eq("_id", new ObjectId(fileInfo.get("_id").toString()));
					}
					bind().eq(object2.toString(), fileInfo.get(object2.toString()));
				}
				JSONArray array = bind().page(idx, pageSize);
				object.put("totalSize", (int) Math.ceil((double) bind().count() / pageSize));
				object.put("currentPage", idx);
				object.put("pageSize", pageSize);
				object.put("data", array);
			} catch (Exception e) {
				nlogger.logout(e);
				object = null;
			}
		}
		return resultMessage(object);
	}

	public JSONObject FindByID(String asid) {
		JSONObject object = bind().eq("_id", new ObjectId(asid)).find();
		return object != null ? object : null;
	}

	@SuppressWarnings("unchecked")
	public String seteffect(String adsid, int effect) {
		JSONObject object = new JSONObject();
		object.put("iseffect", effect);
		JSONObject object2 = bind().eq("adsid", new ObjectId(adsid)).data(object).update();
		return  object2!= null ? resultMessage(0,"广告位已生效") : resultMessage(99);
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
		if (object==null) {
			return null;
		}
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

	private String resultMessage(int num) {
		return resultMessage(num, "");
	}

	@SuppressWarnings("unchecked")
	private String resultMessage(JSONArray array) {
		if (array==null) {
			array = new JSONArray();
		}
		_obj.put("records", array);
		return resultMessage(0, _obj.toString());
	}

	@SuppressWarnings("unchecked")
	private String resultMessage(JSONObject object) {
		if (object==null) {
			object = new JSONObject();
		}
		_obj.put("records", object);
		return resultMessage(0, _obj.toString());
	}

	private String resultMessage(int num, String message) {
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
