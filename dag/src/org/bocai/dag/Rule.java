package org.bocai.dag;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
/**
 * 
 * ����ָһ��������ҵ���ж��߼����ɶ��������ɣ����磺C1^C2|C3
 * @author yikebocai@gmail.com Nov 27, 2012 5:48:57 PM
 */
public class Rule {

    private Integer         id;
    private String          name;
    private CondOperator    condOperator;                           // ���������
    private List<Condition> conditions = new ArrayList<Condition>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CondOperator getCondOperator() {
        return condOperator;
    }

    public void setCondOperator(CondOperator condOperator) {
        this.condOperator = condOperator;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

 
    
    public boolean isTrue(Set<Integer> hitCondIds) {
        switch (condOperator) {
            case AND:
                for (Condition cond : conditions) {
                    if (!hitCondIds.contains(cond.getId())) return false;
                }
                return true;
            case OR:
                for (Condition cond : conditions) {
                    if (hitCondIds.contains(cond.getId())) return true;
                }
                return false;
            default:
                return false;
        }

    }

    /**
     * @param condition
     */
    public void addCondition(Condition condition) {
        conditions.add(condition);

    }

}
