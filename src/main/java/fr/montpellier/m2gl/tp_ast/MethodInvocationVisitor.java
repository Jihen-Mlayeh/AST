package fr.montpellier.m2gl.tp_ast;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

public class MethodInvocationVisitor extends ASTVisitor {
	private List<MethodInvocation> methods = new ArrayList<MethodInvocation>();
	private List<SuperMethodInvocation> superMethods = new ArrayList<SuperMethodInvocation>();
	
	public boolean visit(MethodInvocation node) {
		methods.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(SuperMethodInvocation node) {
		superMethods.add(node);
		return super.visit(node);
	}
	
	public List<MethodInvocation> getMethods() {
		return methods;
	}
	
	public List<SuperMethodInvocation> getSuperMethods() {
		return superMethods;
	}
}