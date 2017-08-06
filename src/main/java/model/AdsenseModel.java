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
import authority.privilige;
import check.formHelper;
import check.formHelper.formdef;
import database.DBHelper;
import database.db;
import nlogger.nlogger;
import rpc.execRequest;
import session.session;

/**
 * 广告位
 * 
 *
 */
public class AdsenseModel {
	private session se;
	private DBHelper ads;
	private formHelper form;
	private JSONObject _obj = new JSONObject();
	private JSONObject UserInfo = new JSONObject();
	private String sid = null;
	private String appid = appsProxy.appidString();

	public AdsenseModel() {
		ads = new DBHelper(appsProxy.configValue().get("db").toString(), "adsense");
		se = new session();
		sid = (String) execRequest.getChannelValue("sid");
		if (sid != null) {
			UserInfo = se.getSession(sid);
		}
		form = ads.getChecker();
		form.putRule("adsname", formdef.notNull);
	}
	
	private db bind(){
		return ads.bind(String.valueOf(appsProxy.appid()));
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

	public void pages(String wbid,int idx,int pageSize,String condString) {
		int role = getRoleSign();
		JSONArray condArray = JSONArray.toJSONArray(condString);
		db db = bind();
		if (condArray!=null && condArray.size()!=0) {
			db.where(condArray);
			if (role == 2 || role == 3 || role == 7) {
				
			}
		}
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
	/**
	 * 根据角色plv，获取角色级别
	 * 
	 * @project GrapeSuggest
	 * @package interfaceApplication
	 * @file Suggest.java
	 * 
	 * @return
	 *
	 */
	private int getRoleSign() {
		int roleSign = 0; // 游客
		if (sid != null) {
			try {
				privilige privil = new privilige(sid);
				int roleplv = privil.getRolePV(appid);
				if (roleplv >= 1000 && roleplv < 3000) {
					roleSign = 1; // 普通用户即企业员工
				}
				if (roleplv >= 3000 && roleplv < 5000) {
					roleSign = 2; // 栏目管理员
				}
				if (roleplv >= 5000 && roleplv < 8000) {
					roleSign = 3; // 企业管理员
				}
				if (roleplv >= 8000 && roleplv < 10000) {
					roleSign = 4; // 监督管理员
				}
				if (roleplv >= 10000 && roleplv < 12000) {
					roleSign = 5; // 总管理员
				}
				if (roleplv >= 12000 && roleplv < 14000) {
					roleSign = 6; // 总管理员，只读权限
				}
				if (roleplv >= 14000 && roleplv < 16000) {
					roleSign = 7; // 栏目编辑人员
				}
			} catch (Exception e) {
				nlogger.logout(e);
				roleSign = 0;
			}
		}
		return roleSign;
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
