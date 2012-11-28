/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package org.bocai.dag;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * �Ѷ�����Ƶ���������ֵ�Ͳ�������ȣ����кϲ���ʹ��Hash�������жϡ����������������ֱ�Ϊ (a = "a"),(a = "b"),(a =
 * "c"),����ֵ����Hash����һ��Fact(a="b")ʱ��һ�β�ѯ�����жϳ����������Ƿ����
 * 
 * @author yikebocai@gmail.com Nov 27, 2012 11:38:37 PM
 */
public class HashDagNode extends DagNode {

    private Map<String, Set<Integer>> map = new HashMap<String, Set<Integer>>();

    public void addCondition(Condition cond) {
        if (cond != null) {
            super.addCondition(cond);

            String right = cond.getRight();
            if (right != null) {
                Set<Integer> ids = map.get(right);
                if (ids == null) {
                    ids = new HashSet<Integer>();
                }
                ids.add(cond.getId());

                map.put(right, ids);
            }
        }
    }

    public boolean match(Fact fact) {
        if (fact != null) {
            String left = getLeft();
            if (left != null) {
                try {
                    Field declaredField = fact.getClass().getDeclaredField(left);
                    declaredField.setAccessible(true);
                    String value = (String) declaredField.get(fact);

                    Set<Integer> ids = map.get(value);
                    if (ids != null && ids.size() > 0) {
                        addMatched(ids);
                        return true;
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

}
