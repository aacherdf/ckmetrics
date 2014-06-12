package metrics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ast.ClassObject;
import ast.FieldInstructionObject;
import ast.MethodInvocationObject;
import ast.MethodObject;
import ast.SuperFieldInstructionObject;
import ast.SuperMethodInvocationObject;
import ast.SystemObject;
import ast.TypeObject;

public class CBO {
	
	private Map<String, Set<String>> cboMap;
	private Map<String, Integer> cboValueMap;
	
	public CBO(SystemObject system) {
		cboMap = new HashMap<String, Set<String>>();
		cboValueMap = new HashMap<String, Integer>();
		
        Set<ClassObject> classes = system.getClassObjects();
        //int cboValue;
        Set<String> coupledClassesSet = new HashSet<String>();
		for(ClassObject classObject : classes) {
			coupledClassesSet = computeCBO(classObject, system);
			//System.out.println("CBO for Class " + classObject.getName() + " is " + cboValue);
			cboMap.put(classObject.getName(), coupledClassesSet);
		}
		
	    computeImportCoupling(cboMap, classes);
		
	    for(Map.Entry<String,Set<String>> mapEntry: cboMap.entrySet())
		{
	    	cboValueMap.put(mapEntry.getKey(), mapEntry.getValue().size());
		}
	}
	
	private void computeImportCoupling(Map<String,Set<String>> cboMap, Set<ClassObject> classSet) {
		
		for(Map.Entry<String,Set<String>> mapEntry: cboMap.entrySet())
		{
			for(Map.Entry<String,Set<String>> mapEntry2: cboMap.entrySet())
			{
				if(!mapEntry2.getKey().equalsIgnoreCase(mapEntry.getKey()))
				{
					Set<String> valueSet = mapEntry2.getValue();
					if(valueSet.contains(mapEntry.getKey()))
					{
						Set<String> cboMapValueSet = cboMap.get(mapEntry.getKey());
						cboMapValueSet.add(mapEntry2.getKey());
					}
				}
			}
		}
	}

	private Set<String> computeCBO(ClassObject classObject, SystemObject system) {
	
		List<MethodObject> methods = classObject.getMethodList();
		Set<String> coupledClasses = new HashSet<String>();
		List<String> systemClasses = system.getClassNames();
		
	
		for(int i=0; i<methods.size(); i++)
		{
			
			List<MethodInvocationObject> methodInvocations = methods.get(i).getMethodInvocations();
			List<FieldInstructionObject> fieldAccesses = methods.get(i).getFieldInstructions();
			List<SuperMethodInvocationObject> superClassMethodInvocations = methods.get(i).getSuperMethodInvocations();
			List<SuperFieldInstructionObject> superClassFieldAccesses  = methods.get(i).getSuperFieldInstructions();
			
			String methodReturnTypes = methods.get(i).getReturnType().getClassType();
			List<TypeObject> methodParameterTypeList = methods.get(i).getParameterTypeList();
			
			
			// For Coupling through Methods
			for(int j=0; j<methodInvocations.size();j++)
			{
				String classesCoupledThroughMethods = methodInvocations.get(j).getOriginClassName();
				if(!classesCoupledThroughMethods.equalsIgnoreCase(classObject.getName()) && systemClasses.contains(classesCoupledThroughMethods))
				{
					coupledClasses.add(classesCoupledThroughMethods);
				}
			}
			
			// For Coupling through Field Access
			for(int j=0; j<fieldAccesses.size();j++)
			{
				String classesCoupledThroughFields = fieldAccesses.get(j).getOwnerClass();
				if(!classesCoupledThroughFields.equalsIgnoreCase(classObject.getName()) && systemClasses.contains(classesCoupledThroughFields))
				{
					coupledClasses.add(classesCoupledThroughFields);
				}
			}
			
			// For Coupling through Super Class Method Calls
			for(int j=0; j<superClassMethodInvocations.size();j++)
			{
				String classesCoupledThroughSuperClassMethodCalls = superClassMethodInvocations.get(j).getOriginClassName();
				if(!classesCoupledThroughSuperClassMethodCalls.equalsIgnoreCase(classObject.getName()) && systemClasses.contains(classesCoupledThroughSuperClassMethodCalls))
				{
					coupledClasses.add(classesCoupledThroughSuperClassMethodCalls);
				}
			}
			// For Coupling through Super Class Field Accesses
			for(int j=0; j<superClassFieldAccesses.size();j++)
			{
				String classesCoupledThroughSuperClassFieldAccesses = superClassFieldAccesses.get(j).getOwnerClass();
				if(!classesCoupledThroughSuperClassFieldAccesses.equalsIgnoreCase(classObject.getName()) && systemClasses.contains(classesCoupledThroughSuperClassFieldAccesses))
				{
					coupledClasses.add(classesCoupledThroughSuperClassFieldAccesses);
				}
			}
			
			// For Coupling through Return Type
			if(!methodReturnTypes.equalsIgnoreCase(classObject.getName()) && systemClasses.contains(methodReturnTypes))
			{
				coupledClasses.add(methodReturnTypes);
			}
			
			// For Coupling through Parameters
			for(int j=0; j<methodParameterTypeList.size();j++)
			{
				String classesCoupledThroughParameters = methodParameterTypeList.get(j).getClassType();
				if(!classesCoupledThroughParameters.equalsIgnoreCase(classObject.getName()) && systemClasses.contains(classesCoupledThroughParameters))
				{
					coupledClasses.add(classesCoupledThroughParameters);
				}
			}
		
		}
		
		/**
		 * For Debugging
			for(String s:coupledClasses)
			{
			//System.out.println("The Coupled Classes Set for " + classObject.getName() + " is :" + s);
			}
		*/
		
		return coupledClasses;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(String key : cboValueMap.keySet()) {
			sb.append(key).append("\t").append(cboValueMap.get(key)).append("\n");
		}
		return sb.toString();
	}
}
