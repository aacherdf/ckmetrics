package automatedtool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ast.ClassObject;
import ast.MethodObject;
import ast.decomposition.AbstractStatement;
import ast.decomposition.CatchClauseObject;
import ast.decomposition.CompositeStatementObject;
import ast.decomposition.TryStatementObject;

public class SearchForTestClasses {
	
	private ArrayList<ArrayList<String>> assertClassesList;
	
	public SearchForTestClasses() {
		assertClassesList = new ArrayList<ArrayList<String>>();
	}
	
	public List<String> appendTestToSourceClasses(Set<ClassObject> classes)
	{
		List<String> testClassNamesList = new ArrayList<String>();
        
		for(ClassObject classObject : classes) {
			
			String classNameWithPackageName = classObject.getName();
			
			String[] splitClassName = classNameWithPackageName.split("\\.");
			
			//System.out.println("The class name is : " + splitClassName[splitClassName.length - 1]);
			
			String absoluteClassName = splitClassName[splitClassName.length - 1];
			
			StringBuffer classNameWithTest = new StringBuffer(absoluteClassName);
			classNameWithTest.append("Test");
			
			//System.out.println("The corresponding Test class name is : " + classNameWithTest);
			testClassNamesList.add(classNameWithTest.toString());
		}	
		
		return testClassNamesList;
	}
	
	public void findAssertStatements(Set<ClassObject> classes, List<String> srcClassesWithTest)
	{
		for(String srcClass: srcClassesWithTest)
		{
			for(ClassObject classObject : classes) {
				//StringBuffer outputForEachClass = new StringBuffer();
				ArrayList<String> outputForEachClass = new ArrayList<String>();
				
				String classNameWithPackageName = classObject.getName();
				
				String absoluteTestPackageName = getAbsolutePackageName(classNameWithPackageName);
				String absoluteTestClassName = getAbsoluteClassName(classNameWithPackageName);
				
				
				if(srcClass.equals(absoluteTestClassName))
				{
					//System.out.println("Matched Test classes for Source Class : " + absoluteTestClassName);
					//outputForEachClass.add(srcClass);
					outputForEachClass.add(absoluteTestPackageName);
					outputForEachClass.add(absoluteTestClassName);
					
					
					List<MethodObject> methodsList = classObject.getMethodList();
					List<CompositeStatementObject> totalAssertStatementListForEachClass = new ArrayList<CompositeStatementObject>();
					for(int i=0; i < methodsList.size(); i++)
					{
						CompositeStatementObject compStmtObj = methodsList.get(i).getMethodBody().getCompositeStatement();
						List<CompositeStatementObject> assertStmtList = compStmtObj.getAssertStatements();
						
						List<TryStatementObject> tryStatementObjectList = methodsList.get(i).getMethodBody().getTryStatements();
						for(int j=0; j< tryStatementObjectList.size(); j++)
						{
							assertStmtList.addAll(tryStatementObjectList.get(j).getAssertStatements());
							List<CatchClauseObject> catchClauseObjectList = tryStatementObjectList.get(j).getCatchClauses();
							for(int k=0;k < catchClauseObjectList.size(); k++)
							{
								assertStmtList.addAll(catchClauseObjectList.get(k).getBody().getAssertStatements());
							}
							if(tryStatementObjectList.get(j).getFinallyClause() != null)
							{
								assertStmtList.addAll(tryStatementObjectList.get(j).getFinallyClause().getAssertStatements());
							}
							
						}
						totalAssertStatementListForEachClass.addAll(assertStmtList);
					}
				
					
					outputForEachClass.add(Integer.toString(totalAssertStatementListForEachClass.size()));
					assertClassesList.add(outputForEachClass);
				}
			}
		}
	}
	
	private String getAbsoluteClassName(String classNameWithPackageName)
	{
		String[] splitClassName = classNameWithPackageName.split("\\.");
		String absoluteClassName = splitClassName[splitClassName.length - 1];
		
		return absoluteClassName;
	}
	
	private String getAbsolutePackageName(String classNameWithPackageName)
	{
		String[] splitClassName = classNameWithPackageName.split("\\.");
		StringBuffer packageName = new StringBuffer();
		
		for(int i = 0; i < splitClassName.length - 1; i++)
		{
			packageName.append(splitClassName[i]);
			packageName.append(".");
		}
		
		return packageName.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TestClassPackage").append("\t").append("TestClassName").append("\t").append("AssertCount").append("\n");
		for(int i=0; i < assertClassesList.size(); i++) {
			ArrayList<String> eachClassValueList = assertClassesList.get(i);
			for(int j=0; j < eachClassValueList.size(); j++)
			{
				sb.append(eachClassValueList.get(j));
				sb.append("\t");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
}
