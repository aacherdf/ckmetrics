/**
 * Computes NOC for a selected project
 * 
 * This code is a part of a project for the course SOEN 6611 "Software Measurement"
 * taught by Dr. Nikolaos Tsantalis.
 * 
 * @copyright	March 2014 - Concordia University, Montreal, QC
 * 
 */
package metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ast.ClassObject;
import ast.SystemObject;
import ast.inheritance.InheritanceDetection;
import ast.inheritance.InheritanceTree;

public class NOC 
{
	private Map<String, Integer> nocMap;
	/**
	 * Constructor
	 * @param system The system object
	 */

	public NOC(SystemObject system) 
	{
		nocMap = new HashMap<String, Integer>();
		//Pass ClassObjects to a set
		Set<ClassObject> classes = system.getClassObjects();
        
		//Print Value of the Number Of Children for each Class
		for(ClassObject classObject : classes) 
		{
			//Number of Children value calculation from computeNOC
			int nocValue = computeNOC(system, classObject);
		    //System.out.println("The NOC Value of the " + classObject.getName()  + " is : " + nocValue);
		    nocMap.put(classObject.getName(), nocValue);
		}
		
	}
	
	/**
	 * Compute and get the NOC
	 * @param system the system object
	 * @param classObject the class object
	 */
	
	private int computeNOC(SystemObject system, ClassObject classObject) 
		{
			// Create new Inheritance Detection object
			InheritanceDetection inheritDet = new InheritanceDetection(system);
			// New Inheritance Tree
			InheritanceTree InherTree = inheritDet.getTree(classObject.getName());
			//Initialize the minimum Number of Children
			int childCount = 0;
			
				// Check if Inheritance tree contains NO children
				if(InherTree != null)
				{
					childCount = InherTree.getRootNode().getChildCount();
					/**
					* For Debugging
					* //System.out.println("The child Count of " + classObject.getName() + " is " + childCount);	
					*/
				}
				else
				{
					/**
					 * For Debugging
					 * //System.out.println("The child Count of " + classObject.getName() + " is 0"); 
					 */
				}
		
			return childCount;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(String key : nocMap.keySet()) {
			sb.append(key).append("\t").append(nocMap.get(key)).append("\n");
		}
		return sb.toString();
	}
	
}
