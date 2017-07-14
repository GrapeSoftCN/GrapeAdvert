package interfaceApplication;

import java.util.HashMap;

import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import JGrapeSystem.jGrapeFW_Message;
import database.db;
import model.AdvertModel1;

/**
 * 广告管理
 * 
 *
 */
public class Advert1 {
	private AdvertModel1 model = new AdvertModel1();
	private JSONObject _obj = new JSONObject();
	private HashMap<String, Object> map = new HashMap<>();

	public Advert1() {
		map.put("r", 1000);
		map.put("u", 2000);
		map.put("d", 3000);
	}

	private db getdb() {
		return model.getdb();
	}

	public String AddAD(String adsInfo) {
		JSONObject object = model.check(adsInfo, map);
		if (object == null) {
			return resultMessage(1);
		}
		if (object.containsKey("adsid")) {
			String adsid = object.get("adsid").toString();
			if (SearchByid(adsid) != null) {
				return resultMessage(2, "");
			}
		}
		String info = getdb().data(object).insertOnce().toString();
		return (!info.equals("")) ? resultMessage(FindByID(info)) : resultMessage(99);
	}

	/*// 修改广告
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
*/
	// 根据广告位id查询广告
	public String SearchByid(String adsid) {
		JSONObject object = model.getdb().eq("adsid", adsid).find();
		return object != null ? object.toString() : "";
	}
//	// 分页
//	public String PageAD(int idx, int pageSize) {
//		return ads.page(idx, pageSize);
//	}
//
//	// 条件分页
//	public String PageByAD(int idx, int pageSize, String adsInfo) {
//		return ads.page(idx, pageSize, JSONHelper.string2json(adsInfo));
//	}
//
//	// 根据广告类型查询广告信息
//	public String FindByType(int typeid) {
//		return ads.FindBytype(typeid);
//	}

	// 广告id查询广告
	private JSONObject FindByID(String asid) {
		JSONObject object = getdb().eq("_id", new ObjectId(asid)).find();
		return object != null ? object : null;
	}

	private String resultMessage(int num) {
		return resultMessage(0, "");
	}

	@SuppressWarnings("unchecked")
	private String resultMessage(JSONObject object) {
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
