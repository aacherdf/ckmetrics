package metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ast.ClassObject;
import ast.MethodObject;
import ast.SystemObject;
import ast.decomposition.CompositeStatementObject;

public class WMC {

	private Map<String, Integer> wmcMap;
	
	public WMC(SystemObject system) {
		wmcMap = new HashMap<String, Integer>();
		
        Set<ClassObject> classes = system.getClassObjects();
        int wmcValue;
		for(ClassObject classObject : classes) {
			wmcValue = computeWMC(classObject);
			//System.out.println("WMC for Class " + classObject.getName() + " is " + wmcValue);
			wmcMap.put(classObject.getName(), wmcValue);
		}	
	}
	

	private int computeWMC(ClassObject classObject) {
	
		List<MethodObject> methodObjectList = classObject.getMethodList();
		int sumOfCCValueOfAllMethod = 0;
		
		for(int i=0; i < methodObjectList.size(); i++)
		{
			MethodObject methodObj = methodObjectList.get(i);
			
			 int ccValueForEachMethod = computeCyclomaticComplexity(methodObj);
			 
			 //System.out.println("The cyclomatic complexity of method " + methodObj.getName() + " in the class " + classObject.getName() + " is : " + ccValueForEachMethod);
			 
			 sumOfCCValueOfAllMethod = sumOfCCValueOfAllMethod + ccValueForEachMethod;
		}
		
		return sumOfCCValueOfAllMethod;
	}
	
	// CC = D + 1; where D is the no of Control Structures in a method
	private int computeCyclomaticComplexity(MethodObject methodObject) {
		
		int D, CC;
		if(methodObject.getMethodBody() != null)
		{
			CompositeStatementObject compStmtObj = methodObject.getMethodBody().getCompositeStatement();
			
			List<CompositeStatementObject> totalControlStatementList = new ArrayList<CompositeStatementObject>();
		
			List<CompositeStatementObject> ifStmtList = compStmtObj.getIfStatements();
			
			totalControlStatementList.addAll(ifStmtList);
			
			List<CompositeStatementObject> forStmtList = compStmtObj.getForStatements();
			
			totalControlStatementList.addAll(forStmtList);
			
			List<CompositeStatementObject> whileStmtList = compStmtObj.getWhileStatements();
			
			totalControlStatementList.addAll(whileStmtList);
			
			List<CompositeStatementObject> doStmtList = compStmtObj.getDoStatements();
			
			totalControlStatementList.addAll(doStmtList);
			
			List<CompositeStatementObject> caseStmtList = compStmtObj.getSwitchStatements();
			
			totalControlStatementList.addAll(caseStmtList);
			
			D = totalControlStatementList.size();
			
		    CC = D + 1;
		    
		    return CC;
		}
		
		return 0;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(String key : wmcMap.keySet()) {
			sb.append(key).append("\t").append(wmcMap.get(key)).append("\n");
		}
		return sb.toString();
	}
	
}
