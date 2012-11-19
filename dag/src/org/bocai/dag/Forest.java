package org.bocai.dag;

import java.util.ArrayList;
import java.util.List;

public class Forest {
	private List<DAG> graphs = new ArrayList<DAG>();

	public List<DAG> getGraphs() {
		return graphs;
	}

	public void setGraphs(List<DAG> graphs) {
		this.graphs = graphs;
	}

	public void addNode(String parent, String child) {
		//���������е�ͼ�в��ң��Ƿ��иýڵ���ڣ�������ڣ��Ͱ��ӽڵ����ӵ����ڵ���
		boolean isFound=false;//
		for (DAG dag : graphs) {
			if (dag.found(parent)) {
				dag.attachNode(parent,child);
				isFound=true;
				break;
			} 
		}
		
		//����Ҳ������´���һ��ͼ�������һ���µĽڵ��ȥ
		if(!isFound){
			DAG dag = new DAG();
			dag.addNode(parent, child); 
			graphs.add(dag);
		}
	}

	public void merge() {
		 for(int i=1;i<graphs.size();i++){
			 DAG dag=graphs.get(i);
			 for(int j=0;j<i;j++){
				 DAG dag2=graphs.get(j);
				 dag2.merge(dag);
			 }
			 
			 if(dag.size()==0){
				 graphs.remove(i--);
			 }
		 }
		
	}

}
