import java.io.IOException;
import java.util.ArrayList;


public class Branching {
	

	public final int maxNumOfNodes=202;
	ArrayList<SubProblem> SbPbs ; 
	public double incumbent;
	public int numOfUpdateIncumbent;
	public int numOfLbGeZbest;
	public int numOfLbEqUb;
	public SubProblem bestSbPb;
	public Min_Max_Heap heap;
	public int numOfNodes;
	public LogicalTests logicalTests = new LogicalTests();
	public Bounds bounds = new Bounds();
	
	
	
	public Branching (){
		
		incumbent=0;
		bestSbPb=null;
		heap = new Min_Max_Heap();
		numOfNodes=0;
		numOfUpdateIncumbent=0;
		numOfLbGeZbest=0;
		numOfLbEqUb=0;
	}
	public String branch(Orthogonal_List oList) throws IOException{
		
		SubProblem sub = new SubProblem();
		numOfNodes++;
		sub.num_of_SbPb=numOfNodes;		
		sub.orthogonal_list = oList;
		
		//apply logical test 1
		if(logicalTests.logicalTest1(oList)==true) 
		{
		  System.out.println("This problem set is not a feasible one!!");
	       return null;
		}
		

		//apply logical test 2
		sub = logicalTests.logicalTest2(sub);

		//apply logical test 3
		sub = logicalTests.logicalTest3(sub);

		//apply logical test 4
		sub = logicalTests.logicalTest4(sub);

		
		//calculate duals_LB-UB
		sub = bounds.calculateDuals_LB_UB_(sub);
		sub.depth = 0;
		if (sub.LB==sub.UB){
			System.out.println("The first Node is optimally solved!!");
			return null;
		}
		
		incumbent =sub.UB;
		bestSbPb=sub;
		
		printNode(sub);
		
		// create heap node
		HeapNode heapNode = new HeapNode(sub.LB,sub);		
		heap.insert(heapNode);
		WriterToFile.writeln(sub.num_of_SbPb + " was added to the heap");
		sub = null;
		SubProblem currentSbPb;
		int rowNum;
		ArrayList<Integer> cols;
		HeapNode minHeapNode;
		while(!heap.isEmpty())
		{
			
			if (numOfNodes>maxNumOfNodes)
				break;
			// remove the min from the heap
			minHeapNode = heap.getMinimum();
			heap.removeMinimum();
			WriterToFile. writeln(minHeapNode.pointer.num_of_SbPb + " was removed from the heap");
			
			currentSbPb = minHeapNode.pointer;
			WriterToFile.writeln("Sum_of_duals="+currentSbPb.sumOfDuals_upToNow);
			// find the related row			
			rowNum=findBestRow(currentSbPb);
			WriterToFile. writeln(" Row Max: "+rowNum);
			
			
			
			
			cols=getSortedRedCostList(currentSbPb.orthogonal_list,rowNum, currentSbPb.duals);
			//cols = currentSbPb.orthogonal_list.getColumnsInRow(rowNum);
			
			
			WriterToFile. writeln(" Num of children: "+cols.size());
			//cols = getSortedColumns(sub.orthogonal_list,rowNum);
			
			// for each child node do:
			SubProblem childSbPb;
			
			
			for(int i=0;i<cols.size();i++)
			{
				childSbPb = new SubProblem();
				//copy the parent to the child
				childSbPb = currentSbPb.copySbPb();
				childSbPb.depth++;
				numOfNodes++;
				if (numOfNodes+1>maxNumOfNodes)
					break;
				childSbPb.num_of_SbPb = numOfNodes;
				
				//------------- find the variables set to one and zero 
			
				childSbPb.variable_one.add(cols.get(i));
				
				
				ArrayList<Integer> tempRow =childSbPb.orthogonal_list.getRowsInColumn(cols.get(i));
				// remove the rows of the col set to one
				for(int k=0;k<tempRow.size();k++)
				{
					childSbPb.orthogonal_list.removeRow(tempRow.get(k));
					childSbPb.sumOfDuals_upToNow =childSbPb.sumOfDuals_upToNow+ currentSbPb.duals.get(tempRow.get(k));
					childSbPb.removed_rows.add(tempRow.get(k));
				}
				// remove the col
				childSbPb.orthogonal_list.removeColumn(cols.get(i));
				
				for(int j=0;j<i;j++)
				{
					childSbPb.variable_zero.add(cols.get(j));
					//remove cols set to zero
					childSbPb.orthogonal_list.removeColumn(cols.get(j));
				}
				
				//----logical tests
				if(logicalTests.logicalTest1(childSbPb.orthogonal_list)==true)
				{					
					// do nothing, this child is infeasible
				}
				else // child is feasible
				{
					//WriterToFile. write("before calculating logical test 2 3 4 !");
					//apply logical test 2
					childSbPb = logicalTests.logicalTest2(childSbPb);
					//apply logical test 3
					childSbPb = logicalTests.logicalTest3(childSbPb);
					//apply logical test 4
					childSbPb = logicalTests.logicalTest4(childSbPb);
					
					//calculate duals_LB-UB
					//WriterToFile. write("before calculating bounds!");
					childSbPb = bounds.calculateDuals_LB_UB_(childSbPb);
					
					
					
				printNode(childSbPb );
				WriterToFile.writeln("*****Prunning Tests*****");
					// pruning Tests
					//--------- pruning test- step 1
					if(childSbPb.UB<incumbent)
					{
						WriterToFile. writeln("Prunning Test1 was applied to "+childSbPb.num_of_SbPb);
						incumbent = childSbPb.UB;
						bestSbPb = childSbPb;
						numOfUpdateIncumbent++;
								while(heap.getMaximum()!=null && heap.getMaximum().value>=incumbent )
								{
									
									   WriterToFile. writeln(heap.getMaximum().pointer.num_of_SbPb + " was removed from the heap");
									   heap.removeMaximum();
									   //numOfLbGeZBest++;
									
								}
						
					}
					
					//-------prunning test-step 2
					if(childSbPb.LB>=incumbent)
					{
						// this child should be pruned
						// do nothing
						numOfLbGeZbest++;
						
						WriterToFile. writeln("Prunning Test2 was applied to "+childSbPb.num_of_SbPb);
					}
					else
					{						
						//----prunning test- step 3
						if(childSbPb.LB==childSbPb.UB)
						{
							// do nothing
							numOfLbEqUb++;
							WriterToFile. writeln("Prunning Test3 was applied to "+childSbPb.num_of_SbPb);
						}
						else
						{ 						
							//----prunning test- step 4
							HeapNode childHeapNode = new HeapNode(childSbPb.LB,childSbPb);
							heap.insert(childHeapNode);
							WriterToFile. writeln(childHeapNode.pointer.num_of_SbPb + " was added to the heap");
							
						}
						WriterToFile.writeln("\n");
						
						
						WriterToFile.writeln("current incumbent value:"+incumbent);
						WriterToFile.writeln("current best node: "+bestSbPb.num_of_SbPb);
						
						
					}			
				
				}
			}			
		}
		
		String result = "lowerBound = "+bestSbPb.LB + "\tupperBound="+ bestSbPb.UB +"\toptimal result is " + incumbent+ "\t";
		result += "Node # "+bestSbPb.num_of_SbPb;
		return result;
	}
	
	
	private int findBestRow(SubProblem sub){
		int rowMax=0;
		int valMax=0;
		int noCols= 0;
		int numOfRows = sub.orthogonal_list.getNumberOfRows();
		for(int i = 0;i<numOfRows;i++)
		{
			if(sub.orthogonal_list.getRowFlag(i))
			{
				noCols = (sub.orthogonal_list.getColumnsInRow(i)).size();
				if((--noCols)*sub.duals.get(i)>valMax)
				{
					valMax = noCols*sub.duals.get(i);
					rowMax = i;
				}
			}
		}
		return rowMax;		
		
	}
	
	public void printNode(SubProblem sub)
	{
		String str = "";
		System.out.println("Writing Node#"+sub.num_of_SbPb+" to file...");
		try {
		WriterToFile.writeln("------------------------------------------------------------------------------");
		WriterToFile. writeln("Node#: "+sub.num_of_SbPb+" LB: "+sub.LB+" UB: "+sub.UB);
		WriterToFile.writeln("\n");
		//WriterToFile.writeln("num of Variables set to one: "+sub.variable_one.size());
		//WriterToFile.writeln("Variables set to one: ");
		for (int i=0;i<sub.variable_one.size();i++)
		{
			WriterToFile.write(sub.variable_one.get(i)+"-");			
		}
		
		//WriterToFile.writeln("\n");
		//WriterToFile.writeln("\n");
		WriterToFile.writeln("num of Variables set to zero : "+sub.variable_zero.size());
		//WriterToFile.writeln("Variables set to zero are : "+sub.variable_zero);
		//WriterToFile.writeln("num of Variables set to one : "+sub.variable_one.size());
		//WriterToFile.writeln("Variables set to one : "+sub.variable_one);
		WriterToFile.write("The best answer (X*) yielding the best UB ==> " );
		int counter = 0; 
		for(int i=0; i<sub.x.size(); i++) 
			if (sub.x.get(i)==1)
				{
					//WriterToFile.write(i+1+", ");
					counter++;
				}//if
		//for (int m=0; m< sub.variable_one.size(); ++m)
		//{
			//WriterToFile.write((sub.variable_one.get(m)).toString()+", ");
		//}
		WriterToFile.writeln("The number of Variabes set to one in the best answer(X*) is " + counter+sub.variable_one.size() );
		WriterToFile.writeln("Incumbent Value has been updated "+numOfUpdateIncumbent + " times");
		WriterToFile.writeln(numOfLbGeZbest+" nodes has been pruned due to LB >= ZBest ");
		WriterToFile.writeln(numOfLbEqUb+" nodes has been pruned due to LB = UB ");
		
		WriterToFile.writeln("");
		
		/*
		for (int i=0;i<sub.variable_zero.size();i++)
		{
			WriterToFile.write(sub.variable_zero.get(i)+"-");
			
		}
		*/
		WriterToFile.writeln("\n");
		WriterToFile.writeln("\n");
		
		WriterToFile.writeln(" num of constraints solved: "+sub.removed_rows.size());
		WriterToFile.writeln("\n");
		
	/*
		int sumOfU=0;
		for(int m=0;m<sub.duals.size();m++)
		{
			WriterToFile.write(sub.duals.get(m).toString()+"-");
			sumOfU = sumOfU+sub.duals.get(m);
		}
		
		WriterToFile.writeln("sum of duals:"+sumOfU);
		WriterToFile.writeln("dual size:"+ sub.duals.size());
	*/	
			//WriterToFile.writeln("------------------------------------------------------------------------------");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private ArrayList<Integer> getSortedRedCostList(Orthogonal_List oList, int selectedRow, ArrayList<Integer> u)
	{
		ArrayList<Integer> sortedList = new ArrayList<Integer>();
		ArrayList<Integer> row = oList.getColumnsInRow(selectedRow);
		int pairList[][] = new int[row.size()][2];
	
		for(int i=0; i<row.size(); i++) //access each column of selected Row
	   	{
			
	   		 int col = row.get(i);
		 	 int cost = oList.getCost(col);
		 	 ArrayList<Integer> iRow = oList.getRowsInColumn(col);
		 	 for(int m=0; m<iRow.size(); m++)	
		 		 cost -= u.get( iRow.get(m)); 
		 	 pairList[i][0] = col;	
		 	 pairList[i][1] = cost;	
	   	}//for
		pairList = mergeSort(pairList);
		for(int i=0; i<pairList.length; i++)
			sortedList.add(pairList[i][0]);
		//System.out.println("sortedList="+sortedList);
		
	    String se="";
	    for(int i=0;i<sortedList.size();i++)
	    {
			se=se+pairList[i][1]+"-";
	    
	    }
		WriterToFile.writeln("col reduced cost are: "+se);		

		
		
		
		return sortedList;
	}//getSortedRedCostList()
	
	
	
	public int[][] mergeSort(int array[][])
	{
		// if the array has more than 1 element, we need to split it and merge the sorted halves
		if(array.length > 1)
		{
			int elementsInA1 = array.length/2;
			int elementsInA2 = elementsInA1;
			if((array.length % 2) == 1)
				elementsInA2 += 1;
			int arr1[][] = new int[elementsInA1][2];
			int arr2[][] = new int[elementsInA2][2];
			for(int i = 0; i < elementsInA1; i++)
			{
				arr1[i][0] = array[i][0];
				arr1[i][1] = array[i][1];
			}
			for(int i = elementsInA1; i < elementsInA1 + elementsInA2; i++)
			{
				arr2[i - elementsInA1][0] = array[i][0];
				arr2[i - elementsInA1][1] = array[i][1];
			}
			arr1 = mergeSort(arr1);
			arr2 = mergeSort(arr2);
			int i = 0, j = 0, k = 0;
			while(arr1.length != j && arr2.length != k)
			{
				if(arr1[j][1] < arr2[k][1])
				{
					array[i][0] = arr1[j][0];
					array[i][1] = arr1[j][1];
					i++;
					j++;
				}
				else
				{
					array[i][0] = arr2[k][0];
					array[i][1] = arr2[k][1];
					i++;
					k++;
				}
			}
			while(arr1.length != j)
			{
				array[i][0] = arr1[j][0];
				array[i][1] = arr1[j][1];
				i++;
				j++;
			}
			while(arr2.length != k)
			{
				array[i][0] = arr2[k][0];
				array[i][1] = arr2[k][1];
				i++;
				k++;
			}
		}
		return array;
	}//mergeSort 


	
	
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			WriterToFile.setWriterToFile();

			double timeBefore, timeAfter;
			//Start the timer
			timeBefore = System.currentTimeMillis();
			Orthogonal_List oList = new Orthogonal_List();

			oList.buildList("scp41.txt");

			Branching branching = new Branching();
			branching.branch(oList);
			//stop the timer
			timeAfter = System.currentTimeMillis();
			System.out.println("finished");
			System.out.println("The overal time: "+ (timeAfter-timeBefore));
			WriterToFile.write("The overal time: "+ (timeAfter-timeBefore));
			WriterToFile.bufWriter.close();
			WriterToFile.writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
