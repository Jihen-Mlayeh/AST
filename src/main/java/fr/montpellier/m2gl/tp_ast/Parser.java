package fr.montpellier.m2gl.tp_ast;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class Parser {
	
	// CHANGE CE CHEMIN vers le projet que tu veux analyser
	public static final String projectPath = "C:\\Users\\Jihen\\eclipse-workspace\\ProjetTest";
	public static final String projectSourcePath = projectPath + "\\src";
	public static final String jrePath = ""; // Laisse vide pour Java 9+

	public static void main(String[] args) throws IOException {

		// read java files
		final File folder = new File(projectSourcePath);
		ArrayList<File> javaFiles = listJavaFilesForFolder(folder);

		System.out.println("Nombre de fichiers Java trouvés : " + javaFiles.size());

		// Analyse chaque fichier Java
		for (File fileEntry : javaFiles) {
			String content = new String(Files.readAllBytes(fileEntry.toPath()), "UTF-8");
			
			CompilationUnit parse = parse(content.toCharArray());

			// print methods info
			printMethodInfo(parse);

			// print variables info
			printVariableInfo(parse);
			
			// print method invocations
			printMethodInvocationInfo(parse);
		}
	}

	// read all java files from specific folder
	public static ArrayList<File> listJavaFilesForFolder(final File folder) {
		ArrayList<File> javaFiles = new ArrayList<File>();
		
		if (folder.listFiles() == null) {
			System.err.println("Le dossier n'existe pas ou est vide : " + folder.getAbsolutePath());
			return javaFiles;
		}
		
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				javaFiles.addAll(listJavaFilesForFolder(fileEntry));
			} else if (fileEntry.getName().endsWith(".java")) {
				javaFiles.add(fileEntry);
			}
		}

		return javaFiles;
	}

	// create AST
	@SuppressWarnings("unchecked")
	private static CompilationUnit parse(char[] classSource) {
		ASTParser parser = ASTParser.newParser(AST.JLS8); // Java 8
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setBindingsRecovery(true);
 
		Map<String, String> options = JavaCore.getOptions();
		parser.setCompilerOptions(options);
		parser.setUnitName("");
 
		String[] sources = { projectSourcePath }; 
		String[] classpath = {}; // Vide car pas de rt.jar sur Java 9+
 
		parser.setEnvironment(classpath, sources, new String[] { "UTF-8" }, true);
		parser.setSource(classSource);
		
		return (CompilationUnit) parser.createAST(null);
	}

	// navigate method information
	public static void printMethodInfo(CompilationUnit parse) {
		MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
		parse.accept(visitor);

		for (MethodDeclaration method : visitor.getMethods()) {
			System.out.println("Method name: " + method.getName()
					+ " Return type: " + method.getReturnType2());
		}
	}

	// navigate variables inside method
	public static void printVariableInfo(CompilationUnit parse) {
		MethodDeclarationVisitor visitor1 = new MethodDeclarationVisitor();
		parse.accept(visitor1);
		
		for (MethodDeclaration method : visitor1.getMethods()) {
			VariableDeclarationFragmentVisitor visitor2 = new VariableDeclarationFragmentVisitor();
			method.accept(visitor2);

			for (VariableDeclarationFragment variableDeclarationFragment : visitor2.getVariables()) {
				System.out.println("variable name: "
						+ variableDeclarationFragment.getName()
						+ " variable Initializer: "
						+ variableDeclarationFragment.getInitializer());
			}
		}
	}
	
	// navigate method invocations inside method
	public static void printMethodInvocationInfo(CompilationUnit parse) {
		MethodDeclarationVisitor visitor1 = new MethodDeclarationVisitor();
		parse.accept(visitor1);
		
		for (MethodDeclaration method : visitor1.getMethods()) {
			MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
			method.accept(visitor2);

			for (MethodInvocation methodInvocation : visitor2.getMethods()) {
				System.out.println("method " + method.getName() + " invoc method "
						+ methodInvocation.getName());
			}
		}
	}
}