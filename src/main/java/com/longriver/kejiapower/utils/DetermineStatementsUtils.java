package com.longriver.kejiapower.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Program:DetermineStatementsUtils
 * Description:
 * Creation Time: 2020/11/19 20:51
 * author wangqi
 * Email:wangq.cn@hotmail.com
 * Since kejiapower
 */
public class DetermineStatementsUtils {
    private Map<Integer, Class<? extends Object>> map = new HashMap<>();

    private static void init() {
//        map.put(TYPE_TITLE, Title.class);
//        map.put(TYPE_CONTENT, Content.class);
//        map.put(TYPE_LINK, Link.class);
    }

    public Object createNewItem(int type) {
        try {
            Class<? extends Object> NewItemClass = map.get(type);
            return NewItemClass.newInstance();
        } catch (Exception e) {
            return new Object(); // 返回默认父类，不要返回null
        }
    }


}
