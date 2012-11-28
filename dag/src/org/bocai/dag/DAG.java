package org.bocai.dag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * directed acyclic graph
 * 
 * @author yikebocai@gmail.com
 */
public class DAG {

    private Set<Integer>          roots = new HashSet<Integer>();         // ���ڵ�����ж��
    private Map<Integer, DagNode> map   = new HashMap<Integer, DagNode>();

    public Set<Integer> getRoots() {
        return roots;
    }

    public void setRoots(Set<Integer> roots) {
        this.roots = roots;
    }

    public Map<Integer, DagNode> getMap() {
        return map;
    }

    public void setMap(Map<Integer, DagNode> map) {
        this.map = map;
    }

    public void addNode(Condition parent, Condition child) {

        DagNode parentNode = new DagNode();
        parentNode.setCondition(parent);
        parentNode.addChild(child.getId());
        map.put(parent.getId(), parentNode);

        // ���û�и��ڵ㣬�����ŵ�roots�б���
        roots.add(parent.getId());

        DagNode childNode = new DagNode();
        childNode.setCondition(child);
        map.put(child.getId(), childNode);

        // ����и��ڵ㣬������roots��ɾ��
        roots.remove(child.getId());
    }

    public boolean found(Integer parentId) {
        if (parentId != null) {
            if (map.get(parentId) != null) return true;
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

                        // ����и��ڵ㣬������roots��ɾ��
                        roots.remove(id);
                    }
                }
            }
        }

    }

    public void merge(DAG dag) {
        if (dag != null) {
            Iterator itr = dag.getMap().entrySet().iterator();
            while (itr.hasNext()) {
                Entry entry = (Entry) itr.next();
                Integer id = (Integer) entry.getKey();
                DagNode node = (DagNode) entry.getValue();

                // ��������µ�ͼ���ҵ����������ڵ�ϲ����µ�ͼ�У���ɾ�����������ӽڵ�����ͼ�еĴ洢
                if (found(id)) {
                    Iterator itr2 = node.getChildren().iterator();
                    while (itr2.hasNext()) {
                        Integer childId = (Integer) itr2.next();
                        attachNode(id, dag.getMap().get(childId).getCondition());
                        dag.remove(childId);
                    }

                    dag.remove(id);
                }
            }
        }

    }

    private void remove(Integer condId) {
        map.remove(condId);

    }

    public int size() {

        return map.size();
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

    /**
     * ����ͼ���ҵ�����ƥ��Ľڵ㣬�Ӹ��ڵ㿪ʼ����ȱ���������в�ƥ��Ľڵ㼴�ɻ���
     * 
     * @param fact
     * @return
     */
    public Set<Integer> traverse(Fact fact) {
        Set<Integer> matched = new HashSet<Integer>();

        Iterator itr = roots.iterator();
        while (itr.hasNext()) {
            Integer next = (Integer) itr.next();
            traverse2(matched, next,fact);
        }
        return matched;
    }

    /**
     * @param matched
     * @param itr
     */
    private void traverse2(Set<Integer> matched, Integer id,Fact fact) {
        DagNode node = map.get(id);

        if (node != null && node.getCondition().match(fact)) {
            
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
}
