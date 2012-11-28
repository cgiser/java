package org.bocai.dag;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * ������
 * 
 * @author yikebocai@gmail.com
 */
public class DagUtil {

    public final static String DUMMY_ROOT_KEY = "@dummy-@";

    /**
     * �������е��������õ�ƥ��Ĺ����б�
     * 
     * @param rules
     * @param conds
     * @return
     */
    public static List<Rule> matchRule(List<Rule> rules, Set<Integer> conds) {
        List<Rule> matched = new ArrayList<Rule>();

        for (Rule rule : rules) {
            if (rule.isTrue(conds)) {
                matched.add(rule);
            }
        }
        return matched;
    }

}
