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

    private Map<Integer, DagNode> map = new HashMap<Integer, DagNode>();

    public DAG(List<Rule> rules) {
        // ���һ��������ڵ㣬���⹹�����ͼ
        DagNode dummyRootNode = new DagNode();
        Condition cond = new Condition(0, "1", Operator.STRING_EQUAL, "1");
        dummyRootNode.setCondition(cond);
        map.put(0, dummyRootNode);

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

    public Map<Integer, DagNode> getMap() {
        return map;
    }

    public void setMap(Map<Integer, DagNode> map) {
        this.map = map;
    }

    private DagNode getRootNode() {
        return map.get(0);
    }

    public void addNode(Condition parent, Condition child) {
        // ����Ҫ��һ������
        if (parent != null) {
            DagNode parentNode = map.get(parent.getId());

            // �����ڵ㣬�������ӹ�ϵ
            if (parentNode == null) {
                parentNode = new DagNode();
                parentNode.setCondition(parent);

                // ���parentû�и��ڵ㣬�����ŵ���ٸ��ڵ�����
                DagNode rootNode = getRootNode();
                rootNode.addChild(parent.getId());
                map.put(parent.getId(), parentNode);
            }

            if (child != null) {
                parentNode.addChild(child.getId());

                DagNode childNode = map.get(child.getId());
                if (childNode == null) {
                    childNode = new DagNode();
                    childNode.setCondition(child);
                }
                childNode.addParent(parent.getId());
                map.put(child.getId(), childNode);
            }

        }
    }

    public boolean found(Integer id) {
        if (id != null) {
            if (map.get(id) != null) return true;
        }
        return false;
    }

    public void attachNode(Integer parentId, Condition child) {
        Set<Condition> children = new HashSet<Condition>();
        children.add(child);
        attachNode(parentId, children);
    }

    public void attachNode(Integer parentId, Set<Condition> children) {
        if (parentId != null) {
            DagNode node = map.get(parentId);
            if (node != null) {
                Iterator itr = children.iterator();
                while (itr.hasNext()) {
                    Condition child = (Condition) itr.next();
                    if (child != null) {
                        Integer id = child.getId();
                        node.addChild(id);
                        if (map.get(id) == null) {
                            DagNode childNode = new DagNode();
                            childNode.setCondition(child);
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
        Set<Integer> matched = new HashSet<Integer>();

        traverse2(matched, 0, fact);

        return matched;
    }

    private void traverse2(Set<Integer> matched, Integer id, Fact fact) {
        DagNode node = map.get(id);

        if (id == 0 || node != null && node.getCondition().match(fact)) {

            matched.add(id);

            Set<Integer> children = node.getChildren();
            if (children != null && children.size() > 0) {
                Iterator itr = children.iterator();
                while (itr.hasNext()) {
                    // �ݹ�
                    Integer next = (Integer) itr.next();
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
            Integer condId = (Integer) entry.getKey();
            DagNode node = (DagNode) entry.getValue();
            sb.append(node.getCondition()).append("->").append(node.getChildren()).append("\n");
        }

        return sb.toString();
    }
}
