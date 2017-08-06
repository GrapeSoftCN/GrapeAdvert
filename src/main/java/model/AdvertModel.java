package model;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

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
import string.StringHelper;

public class AdvertModel {
	private session se;
	private DBHelper ad;
	private formHelper _form;
	private JSONObject _obj = new JSONObject();
	private JSONObject UserInfo = new JSONObject();
	private String sid = null;
	private String appid = appsProxy.appidString();

	public AdvertModel() {
		ad = new DBHelper(appsProxy.configValue().get("db").toString(), "advert");
		se = new session();
		sid = (String) execRequest.getChannelValue("sid");
		if (sid != null) {
			UserInfo = se.getSession(sid);
		}
		_form = ad.getChecker();
		_form.putRule("adname", formdef.notNull);
	}

	private db bind() {
		return ad.bind(appsProxy.appidString());
	}

	public String add(JSONObject object) {
		String info = "";
		if (object != null) {
			if (object.containsKey("adsid")) {
				String adsid = object.get("adsid").toString();
				if (search(adsid) != null) {
					return resultMessage(2, "");
				}
			}
			if (!_form.checkRuleEx(object)) {
				return resultMessage(1, ""); // 必填字段没有填
			}
			info = bind().data(object).insertOnce().toString();
		}
		if (("").equals(info)) {
			return resultMessage(99);
		}
		JSONObject object2 = FindByID(info);
		return resultMessage(object2);
	}

	@SuppressWarnings("unchecked")
	public String update(String mid, JSONObject object) {
		JSONObject obj = null;
		if (object != null) {
			try {
				obj = new JSONObject();
				if (object.containsKey("imgURL")) {
					String img = getImageUri(object.get("imgURL").toString());
					object.put("imgURL", img);
				}
				obj = bind().eq("_id", new ObjectId(mid)).data(object.toString()).update();
			} catch (Exception e) {
				nlogger.logout(e);
				obj = null;
			}
		}
		return obj != null ? resultMessage(0, "广告修改成功") : resultMessage(99);
	}

	public String delete(String mid) {
		if (mid.contains(",")) {
			return resultMessage(99);
		}
		JSONObject object = bind().eq("_id", new ObjectId(mid)).delete();
		return object != null ? resultMessage(0, "删除成功") : resultMessage(99);
	}

	public String delete(String[] mids) {
		bind().or();
		int len = mids.length;
		for (int i = 0; i < len; i++) {
			bind().eq("_id", new ObjectId(mids[i]));
		}
		return bind().deleteAll() == len ? resultMessage(0, "删除成功") : resultMessage(99);
	}

	public String find(JSONObject Info) {
		JSONArray array = null;
		if (Info != null) {
			try {
				array = new JSONArray();
				for (Object object2 : Info.keySet()) {
					if (object2.toString().equals("_id")) {
						bind().eq("_id", Info.get("_id").toString());
					} else {
						bind().eq(object2.toString(), Info.get(object2.toString()));
					}
				}
				array = bind().limit(30).select();
			} catch (Exception e) {
				nlogger.logout(e);
				array = null;
			}
		}
		return resultMessage(getImg(array));
	}

	@SuppressWarnings("unchecked")
	public String page(int idx, int pageSize) {
		JSONObject object = null;
		try {
			object = new JSONObject();
			JSONArray array = bind().page(idx, pageSize);
			if (array.size() != 0) {
				object.put("totalSize", (int) Math.ceil((double) bind().count() / pageSize));
				object.put("currentPage", idx);
				object.put("pageSize", pageSize);
				object.put("data", getImg(array));
			}
		} catch (Exception e) {
			nlogger.logout(e);
			object = null;
		}
		return resultMessage(object);
	}

	@SuppressWarnings("unchecked")
	public String page(int idx, int pageSize, JSONObject Info) {
		JSONObject object = null;
		try {
			db db = bind();
			JSONArray array = new JSONArray();
			object = new JSONObject();
			if (Info != null) {
				for (Object object2 : Info.keySet()) {
					db.eq(object2.toString(), Info.get(object2.toString()));
				}
				array = db.dirty().page(idx, pageSize);
				array = getImg(array);
				object.put("totalSize", (int) Math.ceil((double) db.count() / pageSize));
				db.clear();
				object.put("currentPage", idx);
				object.put("pageSize", pageSize);
				object.put("data", (array == null || array.size() ==0)?new JSONArray(): FillData(array));
			}
			db.clear();
		} catch (Exception e) {
			nlogger.logout(e);
			object = null;
		}
		return resultMessage(object);
	}

	@SuppressWarnings("unchecked")
	private JSONArray FillData(JSONArray array) {
		String type, data;
		long typeId;
		int l = array.size();
		JSONObject object;
		JSONArray arrays;
		if (l == 0) {
			return array;
		}
		for (int i = 0; i < l; i++) {
			object = (JSONObject) array.get(i);
			type = object.getString("adtype");
			if (type.contains("$numberLong")) {
				type = JSONObject.toJSON(type).getString("$numberLong");
			}
			typeId = Long.parseLong(type);
			if (typeId == 1) {
				data = object.getString("data");
				arrays = JSONArray.toJSONArray(data);
				object.put("data", getArticle(arrays));
			}
			array.set(i, object);
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	private JSONArray getArticle(JSONArray array) {
		JSONObject object,obj, tempObj = new JSONObject();
		String id,image;
		String oid = "";
		if (array != null && array.size() != 0) {
			int l = array.size();
			for (int i = 0; i < l; i++) {
				object = (JSONObject) array.get(i);
				id = object.getString("id");
				if (id != null && !id.equals("")) {
					oid += id + ",";
				}
			}
			if (oid.length() > 0) {
				oid = StringHelper.fixString(oid, ',');
			}
			nlogger.logout(oid);
			// 批量查询文章，mainName,_id,images
			String articles = appsProxy.proxyCall("/GrapeContent/Content/findArticles/" + oid).toString();
			obj = JSONObject.toJSON(articles);
			if (obj != null && obj.size() != 0) {
				for (int i = 0; i < l; i++) {
					object = (JSONObject) array.get(i);
					id = object.getString("id");
					tempObj = (JSONObject) obj.get(id);
					if (tempObj != null && tempObj.size() != 0) {
						image = tempObj.getString("image");
						object.put("text", tempObj.getString("mainName"));
						object.put("img",(!image.equals(""))? image:tempObj.getString("thumbnail"));
					}
					array.set(i, object);
				}
			}
		}
		return array;
	}

	public String pages(int idx, int pageSize, String condString) {
		JSONArray array = null;
		long total = 0, totalSize = 0;
		String wbid = (UserInfo != null && UserInfo.size() != 0) ? UserInfo.get("currentWeb").toString() : "";
		int role = getRoleSign();
		JSONArray condArray = JSONArray.toJSONArray(condString);
		db db = bind();
		if (condArray != null && condArray.size() != 0) {
			db.where(condArray);
			if (role == 2 || role == 3 || role == 7) {
				db.eq("wbid", wbid);
			}
		}
		array = db.dirty().page(idx, pageSize);
		total = db.dirty().count();
		totalSize = db.dirty().pageMax(pageSize);
		db.clear();
		return PageShow(array, idx, pageSize, total, totalSize);
	}

	@SuppressWarnings("unchecked")
	private String PageShow(JSONArray array, int idx, int pageSize, long total, long totalSize) {
		JSONObject object = new JSONObject();
		object.put("data", (array == null) ? new JSONArray() : array);
		object.put("currentPage", idx);
		object.put("pageSize", pageSize);
		object.put("total", total);
		object.put("totalSize", totalSize);
		return resultMessage(object);
	}

	public JSONObject FindByID(String asid) {
		JSONObject object = bind().eq("_id", new ObjectId(asid)).find();
		return object != null ? getImg(object) : null;
	}

	// 根据广告位id查询广告
	public String search(String asid) {
		JSONObject object = bind().eq("adsid", asid).find();
		if (object == null) {
			return resultMessage(99);
		}
		JSONObject obj = getADS(object);
		return resultMessage(getImg(obj));
	}

	public String FindBytype(int tid) {
		JSONArray array = null;
		try {
			array = new JSONArray();
			array = bind().eq("adtype", tid).limit(20).select();
		} catch (Exception e) {
			nlogger.logout(e);
			array = null;
		}
		return resultMessage(getImg(array));
	}

	// 设置广告位（广告id，广告位id）
	@SuppressWarnings("unchecked")
	public String setads(String adid, String adsid) {
		JSONObject object = new JSONObject();
		object.put("adsid", adsid);
		JSONObject object2 = bind().eq("_id", new ObjectId(adid)).data(object).update();
		return object2 != null ? resultMessage(0, "设置广告位成功") : resultMessage(99);
	}

	// 获取图片广告内容
	@SuppressWarnings("unchecked")
	private JSONObject getImg(JSONObject object) {
		if (object != null) {
			String imgURL = "";
			String host = "http://" + getAppIp("file").split("/")[1];
			if (object.containsKey("imgURL")) {
				imgURL = object.get("imgURL").toString();
				if (imgURL.contains("webapps")) {
					imgURL = imgURL.split("webapps")[1];
				}
				imgURL = host + imgURL;
			}
			object.put("imgURL", imgURL);
		}
		return object;
	}

	@SuppressWarnings("unchecked")

	private JSONArray getImg(JSONArray array) {
		if (array == null) {
			return array;
		}
		JSONArray array2 = new JSONArray();
		for (int i = 0, len = array.size(); i < len; i++) {
			JSONObject object = (JSONObject) array.get(i);
			object = getIMG(object);
			array2.add(object);
		}
		return array2;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getIMG(JSONObject object) {
		String FileHost = getFile(1);
		if (object == null) {
			return object;
		}
		String imgURL;
		for (int i = 0; i < 5; i++) {
			if (i == 0) {
				if (object.containsKey("imgURL")) {
					String img = object.get("imgURL").toString();
					img = img.contains("http") ? getImageUri(img) : img;
					object.put("imgURL", FileHost + img);
				}
			} else {
				if (!object.containsKey("imgURL" + i)) {
					continue;
				}
				imgURL = object.get("imgURL" + i).toString();
				imgURL = imgURL.contains("http") ? getImageUri(imgURL) : imgURL;
				imgURL = FileHost + imgURL;
				object.put("imgURL" + i, imgURL);
			}
		}
		return object;
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

	// 获取不同类型的广告数据
	private JSONObject getADS(JSONObject object) {
		if (object != null) {
			if (object.containsKey("adtype")) {
				int type = Integer.parseInt(object.get("adtype").toString());
				switch (type) {
				case 1:
					object.remove("text");
					object.remove("size");
					object.remove("attribute");
					object.remove("show");
					break;

				case 2:
					object.remove("imgURL");
					object.remove("show");
					break;
				}
			}
		}
		return object;
	}

	private String getImageUri(String imageURL) {
		// String rString = null;
		int i = 0;
		if (imageURL.contains("File//upload")) {
			i = imageURL.toLowerCase().indexOf("file//upload");
			imageURL = "\\" + imageURL.substring(i);
		}
		if (imageURL.contains("File\\upload")) {
			i = imageURL.toLowerCase().indexOf("file\\upload");
			imageURL = "\\" + imageURL.substring(i);
		}
		if (imageURL.contains("File/upload")) {
			i = imageURL.toLowerCase().indexOf("file/upload");
			imageURL = "\\" + imageURL.substring(i);
		}
		return imageURL;
	}

	private String getAppIp(String key) {
		String value = "";
		try {
			Properties pro = new Properties();
			pro.load(new FileInputStream("URLConfig.properties"));
			value = pro.getProperty(key);
		} catch (Exception e) {
			value = "";
		}
		return value;
	}

	private String getFile(int i) {
		return "http://" + getAppIp("file").split("/")[i];
	}

	private String resultMessage(int num) {
		return resultMessage(0, "");
	}

	@SuppressWarnings("unchecked")
	private String resultMessage(JSONObject object) {
		_obj.put("records", object);
		return resultMessage(0, _obj.toString());
	}

	@SuppressWarnings("unchecked")
	private String resultMessage(JSONArray array) {
		_obj.put("records", array);
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
