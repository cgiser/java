package org.bocai.dag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * directed acyclic graph
 * 
 * @author yikebocai@gmail.com
 */
public class DAG {

    private Map<String, DagNode> map = new HashMap<String, DagNode>();

    public DAG(List<Rule> rules) {
        // ���һ��������ڵ㣬���⹹�����ͼ
        DagNode dummyRootNode = new DagNode();
        map.put(DagUtil.DUMMY_ROOT_KEY, dummyRootNode);

        // ���������޻�ͼ
        for (Rule rule : rules) {
            List<Condition> conditions = rule.getConditions();
            for (int i = 0; i < conditions.size(); i++) {
                Condition parent = conditions.get(i);
                switch (rule.getCondOperator()) {
                    case AND:
                        Condition child = null;
                        if (i + 1 < conditions.size()) {
                            child = conditions.get(i + 1);
                        }

                        addNode(parent, child);
                        break;
                    case OR:
                        addNode(parent, null);
                }

            }
        }
    }

    public Map<String, DagNode> getMap() {
        return map;
    }

    public void setMap(Map<String, DagNode> map) {
        this.map = map;
    }

    private DagNode getRootNode() {
        return map.get(DagUtil.DUMMY_ROOT_KEY);
    }

    public void addNode(Condition parent, Condition child) {
        // ����Ҫ��һ������
        if (parent != null) {
            DagNode parentNode = map.get(parent.getPrefix());

            // �����ڵ㣬�������ӹ�ϵ
            if (parentNode == null) {
                parentNode = DagNodeFactory.create(parent.getOperator());
                parentNode.addCondition(parent);

                // ���parentû�и��ڵ㣬�����ŵ���ٸ��ڵ�����
                DagNode rootNode = getRootNode();
                rootNode.addChild(parent.getPrefix());
                map.put(parent.getPrefix(), parentNode);
            }

            if (child != null) {
                //���ǰ��������ǰ׺��ȣ��պϲ���һ���ڵ���
                String prefix = child.getPrefix();
                if (prefix.equals(parent.getPrefix())) {
                    parentNode.addCondition(child);
                } else {
                    parentNode.addChild(prefix);
                    DagNode childNode = map.get(prefix);
                    if (childNode == null) {
                        childNode = DagNodeFactory.create(child.getOperator());
                        childNode.addCondition(child);
                    }
                    childNode.addParent(parent.getPrefix());
                    map.put(prefix, childNode);
                }

            }

        }
    }

    public boolean found(Integer id) {
        if (id != null) {
            if (map.get(id) != null) return true;
        }
        return false;
    }

    public void attachNode(String parentId, Condition child) {
        Set<Condition> children = new HashSet<Condition>();
        children.add(child);
        attachNode(parentId, children);
    }

    public void attachNode(String parentId, Set<Condition> children) {
        if (parentId != null) {
            DagNode node = map.get(parentId);
            if (node != null) {
                Iterator itr = children.iterator();
                while (itr.hasNext()) {
                    Condition child = (Condition) itr.next();
                    if (child != null) {
                        String id = child.getPrefix();
                        node.addChild(id);
                        if (map.get(id) == null) {
                            DagNode childNode = DagNodeFactory.create(child.getOperator());
                            childNode.addCondition(child);
                            map.put(id, childNode);
                        }
                    }
                }
            }
        }

    }

    /**
     * ����ͼ���ҵ�����ƥ��Ľڵ㣬�Ӹ��ڵ㿪ʼ����ȱ���������в�ƥ��Ľڵ㼴�ɻ���
     * 
     * @param fact
     * @return
     */
    public Set<Integer> traverse(Fact fact) {
        Set<Integer> result = new HashSet<Integer>();
        Set<String> matched = new HashSet<String>();
        traverse2(matched, DagUtil.DUMMY_ROOT_KEY, fact);

        Iterator itr = matched.iterator();
        while (itr.hasNext()) {
            String key = (String) itr.next();
            DagNode node = map.get(key);
            if (node != null) {
                result.addAll(node.getMatched());
            }
        }
        return result;
    }

    private void traverse2(Set<String> matched, String id, Fact fact) {
        DagNode node = map.get(id);

        if (DagUtil.DUMMY_ROOT_KEY.equals(id) || node != null && node.match(fact)) {

            matched.add(id);

            Set<String> children = node.getChildren();
            if (children != null && children.size() > 0) {
                Iterator itr = children.iterator();
                while (itr.hasNext()) {
                    // �ݹ�
                    String next = (String) itr.next();
                    if (next != null) {
                        traverse2(matched, next, fact);
                    }
                }
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        Iterator itr = map.entrySet().iterator();
        while (itr.hasNext()) {
            Entry entry = (Entry) itr.next();
            String key = (String) entry.getKey();
            DagNode node = (DagNode) entry.getValue();
            sb.append(key).append("->").append(node).append("\n");
        }

        return sb.toString();
    }
}
