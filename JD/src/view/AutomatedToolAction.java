package view;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import util.WriteOutputToFile;

import ast.ASTReader;
import ast.ClassObject;
import ast.CompilationUnitCache;
import ast.SystemObject;
import automatedtool.SearchForTestClasses;

public class AutomatedToolAction implements IObjectActionDelegate {

	private IWorkbenchPart part;
	private ISelection selection;
	
	private IJavaProject selectedProject;
	private IPackageFragmentRoot selectedPackageFragmentRoot;
	private IPackageFragment selectedPackageFragment;
	private ICompilationUnit selectedCompilationUnit;
	private IType selectedType;
	private IMethod selectedMethod;
	
	public void run(IAction arg0) {
		try {
			CompilationUnitCache.getInstance().clearCache();
			if(selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection)selection;
				Object element = structuredSelection.getFirstElement();
				if(element instanceof IJavaProject) {
					selectedProject = (IJavaProject)element;
					selectedPackageFragmentRoot = null;
					selectedPackageFragment = null;
					selectedCompilationUnit = null;
					selectedType = null;
					selectedMethod = null;
				}
				else if(element instanceof IPackageFragmentRoot) {
					IPackageFragmentRoot packageFragmentRoot = (IPackageFragmentRoot)element;
					selectedProject = packageFragmentRoot.getJavaProject();
					selectedPackageFragmentRoot = packageFragmentRoot;
					//System.out.println("The selected package fragment root is : " + selectedPackageFragmentRoot);
					selectedPackageFragment = null;
					selectedCompilationUnit = null;
					selectedType = null;
					selectedMethod = null;
				}
				else if(element instanceof IPackageFragment) {
					IPackageFragment packageFragment = (IPackageFragment)element;
					selectedProject = packageFragment.getJavaProject();
					selectedPackageFragment = packageFragment;
					//System.out.println("The selected package fragment is : " + selectedPackageFragment);
					selectedPackageFragmentRoot = null;
					selectedCompilationUnit = null;
					selectedType = null;
					selectedMethod = null;
				}
				else if(element instanceof ICompilationUnit) {
					ICompilationUnit compilationUnit = (ICompilationUnit)element;
					selectedProject = compilationUnit.getJavaProject();
					selectedCompilationUnit = compilationUnit;
					selectedPackageFragmentRoot = null;
					selectedPackageFragment = null;
					selectedType = null;
					selectedMethod = null;
				}
				else if(element instanceof IType) {
					IType type = (IType)element;
					selectedProject = type.getJavaProject();
					selectedType = type;
					selectedPackageFragmentRoot = null;
					selectedPackageFragment = null;
					selectedCompilationUnit = null;
					selectedMethod = null;
				}
				else if(element instanceof IMethod) {
					IMethod method = (IMethod)element;
					selectedProject = method.getJavaProject();
					selectedMethod = method;
					selectedPackageFragmentRoot = null;
					selectedPackageFragment = null;
					selectedCompilationUnit = null;
					selectedType = null;
				}
				IWorkbench wb = PlatformUI.getWorkbench();
				IProgressService ps = wb.getProgressService();
				ps.busyCursorWhile(new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						if(ASTReader.getSystemObject() != null && selectedProject.equals(ASTReader.getExaminedProject())) {
							new ASTReader(selectedProject, ASTReader.getSystemObject(), monitor);
						}
						else {
							new ASTReader(selectedProject, monitor);
						}
						
						System.out.println("Automated Tool is invoked ... ");
						//System.out.println("The selected Package Fragment is :" + selectedPackageFragmentRoot + " : " + selectedPackageFragment);
						
						if(selectedPackageFragmentRoot != null) {
							// package fragment root selected
							
							try {
								IPackageFragmentRoot[] packageArray = selectedProject.getPackageFragmentRoots();
								List<String> srcClassesWithTest = new ArrayList<String>();
								for(IPackageFragmentRoot s: packageArray)
								{	
									if(s.toString().contains("main"))
									{
										//System.out.println("Packages inside : " + s.toString());
										SearchForTestClasses sftc = new SearchForTestClasses();
										Set<ClassObject> classObjectsFromFolder = ASTReader.getSystemObject().getClassObjects(s);
										srcClassesWithTest = sftc.appendTestToSourceClasses(classObjectsFromFolder);
										/*for(String str: srcClassesWithTest)
										{
											System.out.println("Source Classes with Test " + str);
										}*/
									}
								}	
								for(IPackageFragmentRoot s: packageArray)
								{
									if(s.toString().contains("test") && srcClassesWithTest != null && !srcClassesWithTest.isEmpty())
									{
										//System.out.println("Packages inside : " + s.toString());
										Set<ClassObject> classObjectsFromFolder = ASTReader.getSystemObject().getClassObjects(s);
										SearchForTestClasses sftc = new SearchForTestClasses();
										sftc.findAssertStatements(classObjectsFromFolder, srcClassesWithTest);
										
										// Output To Console
										System.out.println("Assert Statements Output for Test Classes :\n");
										System.out.println(sftc.toString());
										
										// Output to File
										String outputDataToFile = "Assert Statements Output for Test Classes :" + System.getProperty("line.separator") +
												System.getProperty("line.separator") + sftc.toString().replaceAll("\n", System.getProperty("line.separator"));
										
										WriteOutputToFile wof = new WriteOutputToFile();
										wof.writeOutput(outputDataToFile, "AssertCount.txt");
									}
								}
							} catch (JavaModelException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							//Set<ClassObject> classObjectsFromFolder = ASTReader.getSystemObject().getClassObjects();
							//SearchForTestClasses sftc = new SearchForTestClasses(classObjectsFromFolder);
						}
						else if(selectedPackageFragment != null) {
							// package fragment selected
						}
						else if(selectedCompilationUnit != null) {
							// compilation unit selected
						}
						else if(selectedType != null) {
							// type selected
						}
						else if(selectedMethod != null) {
							// method selected
						}
						else {
							// java project selected
						}
					}
				});
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.part = targetPart;
	}
}
