package view;

import java.lang.reflect.InvocationTargetException;

import metrics.CBO;
import metrics.DIT;
import metrics.LCOM;
import metrics.NOC;
import metrics.RFC;
import metrics.WMC;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
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
import ast.CompilationUnitCache;
import ast.SystemObject;

public class MetricsAction  implements IObjectActionDelegate {
	
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
					selectedPackageFragment = null;
					selectedCompilationUnit = null;
					selectedType = null;
					selectedMethod = null;
				}
				else if(element instanceof IPackageFragment) {
					IPackageFragment packageFragment = (IPackageFragment)element;
					selectedProject = packageFragment.getJavaProject();
					selectedPackageFragment = packageFragment;
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
						SystemObject system = ASTReader.getSystemObject();
						
						/**
						 * Metric Calculation
						 */
						
						//LCOM Computation
						LCOM lcom = new LCOM(system);
						
						// RFC Computation
						RFC rfc = new RFC(system);
						
						// DIT Computation
						DIT dit = new DIT(system);
						
						//NOC Computation
						NOC noc = new NOC(system);
						
						//WMC Computation
						WMC wmc = new WMC(system);
						
						//CBO Computation
						CBO cbo = new CBO(system);
						
						String outputDataToConsole = "\n\n Calculation of CK Metrics \n" + "\n LCOM Metrics ...........\n" + lcom.toString() + 
											"\n RFC Metrics ............\n" + rfc.toString() + 
											"\n DIT Metrics ............\n" + dit.toString() +
											"\n NOC Metrics ............ \n" + noc.toString() +
											"\n WMC Metrics ............\n" + wmc.toString() +
											"\n CBO Metrics ............\n" + cbo.toString();
						
						// Write to Console
						System.out.println(outputDataToConsole);
						
						String outputDataToFile = "Calculation of CK Metrics " + System.getProperty("line.separator") + System.getProperty("line.separator") +
								"LCOM Metrics ..........." + System.getProperty("line.separator") + lcom.toString().replaceAll("\n", System.getProperty("line.separator")) + System.getProperty("line.separator") +
								"RFC Metrics ............" + System.getProperty("line.separator") + rfc.toString().replaceAll("\n", System.getProperty("line.separator")) + System.getProperty("line.separator") +
								"DIT Metrics ............" + System.getProperty("line.separator") + dit.toString().replaceAll("\n", System.getProperty("line.separator")) + System.getProperty("line.separator") +
								"NOC Metrics ............" + System.getProperty("line.separator") + noc.toString().replaceAll("\n", System.getProperty("line.separator")) + System.getProperty("line.separator") +
								"WMC Metrics ............" + System.getProperty("line.separator") + wmc.toString().replaceAll("\n", System.getProperty("line.separator")) + System.getProperty("line.separator") +
								"CBO Metrics ............" + System.getProperty("line.separator") + cbo.toString().replaceAll("\n", System.getProperty("line.separator")) + System.getProperty("line.separator");
						
						//Write to a file named "MetricsOutput.txt" on Desktop
						WriteOutputToFile wof = new WriteOutputToFile();
						wof.writeOutput(outputDataToFile, "MetricsOutput.txt");
						
						
						if(selectedPackageFragmentRoot != null) {
							// package fragment root selected
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
