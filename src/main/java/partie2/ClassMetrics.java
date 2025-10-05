package partie2;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public class ClassMetrics {

	  String className;
	    String packageName;
	    int methodCount;
	    int fieldCount;
	    int linesOfCode;
	    List<Integer> methodLines = new ArrayList<>();
	    List<Integer> methodParameters = new ArrayList<>();
	    List<MethodDeclaration> methods = new ArrayList<>();
	}