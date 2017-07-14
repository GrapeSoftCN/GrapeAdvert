package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONObject;

import apps.appsProxy;
import check.formHelper;
import check.formHelper.formdef;
import database.DBHelper;
import database.db;
import json.JSONHelper;

public class AdvertModel1 {
	private static DBHelper advert;
	private static formHelper form;
	
	static{
		advert = new DBHelper(appsProxy.configValue().getString("db"),"advert");
		form = advert.getChecker();
	}
	public AdvertModel1() {
		form.putRule("adname", formdef.notNull);
	}
	public JSONObject check(String info,HashMap<String, Object> map) {
		JSONObject object = AddMap(map, info);
		return !form.checkRuleEx(object) ? null : object;
	}
	public db getdb() {
		return advert.bind(appsProxy.appidString());
	}
	/**
	 * 将map添加至JSONObject中
	 * 
	 * @param map
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject AddMap(HashMap<String, Object> map, String Info) {
		JSONObject object = JSONHelper.string2json(Info);
		if (object != null) {
			if (map.entrySet() != null) {
				Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator.next();
					if (!object.containsKey(entry.getKey())) {
						object.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}
		return object;
	}
}
