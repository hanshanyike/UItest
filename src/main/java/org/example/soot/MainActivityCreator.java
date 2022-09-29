package org.example.soot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.example.parser.LayoutIdParser;
import org.example.parser.ManifestParser;
import soot.BooleanType;
import soot.IntType;
import soot.Local;
import soot.Modifier;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.VoidType;
import soot.javaToJimple.LocalGenerator;
import soot.jimple.AssignStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.StringConstant;
import soot.jimple.ThisRef;

public class MainActivityCreator {

    private String packageName = ManifestParser.packageName;

    public void exec() {
        init();
        createMainActivity();
        // showActivityContructor();
    }

    private void init() {
        packageName = ManifestParser.packageName;
    }

    private void createMainActivity() {
        SootClass ActivityClass = Scene.v().getSootClass("android.app.Activity");
        SootMethod ActivityConstructorMethod = ActivityClass.getMethod("void <init>()");

        SootClass fakeMainActivity = new SootClass(packageName + ".FakeMainActivity");
        Scene.v().addClass(fakeMainActivity); // addClass method will mark the added class as library class
        fakeMainActivity.setSuperclass(ActivityClass);
        fakeMainActivity.setModifiers(Modifier.PUBLIC);
        fakeMainActivity.setApplicationClass();

        SootClass StringClass = Scene.v().getSootClass("java.lang.String");
        SootField layoutIdsField = new SootField("layoutIds", StringClass.getType());
        fakeMainActivity.addField(layoutIdsField);
        layoutIdsField.setModifiers(Modifier.PUBLIC | Modifier.STATIC);

        // start creating -- "public void <init>()"
		/*
		public void <init>() {
        	hk.polyu.uiref.MainActivity $r0;
        	java.lang.String $r1;

        	$r0 := @this: hk.polyu.uiref.MainActivity;
        	specialinvoke $r0.<android.app.Activity: void <init>()>();
        	$r1 := "";
        	return;
    	}
 		*/
        SootMethod constructorMethod =
                new SootMethod("<init>", Arrays.asList(new Type[] {}), VoidType.v(), Modifier.CONSTRUCTOR | Modifier.PUBLIC);
        fakeMainActivity.addMethod(constructorMethod);

        JimpleBody constructorBody = Jimple.v().newBody(constructorMethod);
        constructorMethod.setActiveBody(constructorBody);

        LocalGenerator constructorLocalGenerator = new LocalGenerator(constructorMethod.getActiveBody());
        // $r0 := @this: hk.polyu.uiref.MainActivity;
        Local constructor_lhs_0 = constructorLocalGenerator.generateLocal(fakeMainActivity.getType());
        ThisRef constructor_rhs_0 = Jimple.v().newThisRef(fakeMainActivity.getType());
        IdentityStmt constructor_stmt_0 = Jimple.v().newIdentityStmt(constructor_lhs_0, constructor_rhs_0);
        // specialinvoke $r0.<android.app.Activity: void <init>()>();
        Local constructor_base_1 = constructor_lhs_0;
        SpecialInvokeExpr constructor_expr_1 = Jimple.v().newSpecialInvokeExpr(constructor_base_1, ActivityConstructorMethod.makeRef());
        InvokeStmt constructor_stmt_1 = Jimple.v().newInvokeStmt(constructor_expr_1);
        // $r1 = "";
        ArrayList<Integer> idValueList = LayoutIdParser.sortIdValue(LayoutIdParser.idValueSet);
        String layoutIds = "";
        for (Integer idValue : idValueList) {
            layoutIds += idValue.toString() + ", ";
        }
        AssignStmt constructor_stmt_2 = Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(layoutIdsField.makeRef()), StringConstant.v(layoutIds));
        // return;
        ReturnVoidStmt constructor_stmt_3 = Jimple.v().newReturnVoidStmt();

        List<Unit> constructorStmtList = new ArrayList<Unit>();
        constructorStmtList.add(constructor_stmt_0);
        constructorStmtList.add(constructor_stmt_1);
        constructorStmtList.add(constructor_stmt_2);
        constructorStmtList.add(constructor_stmt_3);

        constructorBody.getUnits().addAll(constructorStmtList);
        // finish creating -- "public void <init>()"

        // start creating -- "protected void onCreate(android.os.Bundle)"
		/*
		protected void onCreate(android.os.Bundle) {
        	hk.polyu.uiref.MainActivity $r0;
        	android.os.Bundle $r1;

        	$r0 := @this: hk.polyu.uiref.MainActivity;
        	$r1 := @parameter0: android.os.Bundle;
        	specialinvoke $r0.<android.app.Activity: void onCreate(android.os.Bundle)>($r1);
        	staticinvoke <hk.polyu.uiref.UiTraversal: void init(android.app.Activity)>($r0);
        	return;
    	}
	    */
        SootMethod ActivityOnCreateMethod = ActivityClass.getMethod("void onCreate(android.os.Bundle)");
        SootClass BundleClass = Scene.v().getSootClass("android.os.Bundle");
        SootClass UiTraversalClass = Scene.v().getSootClass("hk.polyu.util.UiTraversal");
        SootMethod UiTraversalInitMethod = UiTraversalClass.getMethod("void init(android.app.Activity)");
        SootMethod onCreateMethod =
                new SootMethod("onCreate", Arrays.asList(new Type[] {BundleClass.getType()}), VoidType.v(), Modifier.PROTECTED);
        fakeMainActivity.addMethod(onCreateMethod);

        JimpleBody onCreateBody = Jimple.v().newBody(onCreateMethod);
        onCreateMethod.setActiveBody(onCreateBody);

        LocalGenerator onCreateLocalGenerator = new LocalGenerator(onCreateMethod.getActiveBody());
        // $r0 := @this: hk.polyu.uiref.MainActivity;
        Local oncreate_lhs_0 = onCreateLocalGenerator.generateLocal(fakeMainActivity.getType());
        ThisRef oncreate_rhs_0 = Jimple.v().newThisRef(fakeMainActivity.getType());
        IdentityStmt oncreate_stmt_0 = Jimple.v().newIdentityStmt(oncreate_lhs_0, oncreate_rhs_0);
        // $r1 := @parameter0: android.os.Bundle;
        Local oncreate_lhs_1 = onCreateLocalGenerator.generateLocal(BundleClass.getType());
        ParameterRef oncreate_rhs_1 = Jimple.v().newParameterRef(BundleClass.getType(), 0);
        IdentityStmt oncreate_stmt_1 = Jimple.v().newIdentityStmt(oncreate_lhs_1, oncreate_rhs_1);
        // specialinvoke $r0.<android.app.Activity: void onCreate(android.os.Bundle)>($r1);
        Local oncreate_base_2 = oncreate_lhs_0;
        Local oncreate_arg_2_0 = oncreate_lhs_1;
        SpecialInvokeExpr oncreate_expr_2 = Jimple.v().newSpecialInvokeExpr(oncreate_base_2, ActivityOnCreateMethod.makeRef(), oncreate_arg_2_0);
        InvokeStmt oncreate_stmt_2 = Jimple.v().newInvokeStmt(oncreate_expr_2);
        // staticinvoke <hk.polyu.uiref.UiTraversal: void init(android.app.Activity)>($r0);
        Local oncreate_arg_3_0 = oncreate_lhs_0;
        StaticInvokeExpr oncreate_expr_3 = Jimple.v().newStaticInvokeExpr(UiTraversalInitMethod.makeRef(), oncreate_arg_3_0);
        InvokeStmt oncreate_stmt_3 = Jimple.v().newInvokeStmt(oncreate_expr_3);
        // return;
        ReturnVoidStmt oncreate_stmt_4 = Jimple.v().newReturnVoidStmt();

        List<Unit> onCreateStmtList = new ArrayList<Unit>();
        onCreateStmtList.add(oncreate_stmt_0);
        onCreateStmtList.add(oncreate_stmt_1);
        onCreateStmtList.add(oncreate_stmt_2);
        onCreateStmtList.add(oncreate_stmt_3);
        onCreateStmtList.add(oncreate_stmt_4);

        onCreateBody.getUnits().addAll(onCreateStmtList);
        // finish creating -- "protected void onCreate(android.os.Bundle)"

        // start creating -- "public boolean onTouchEvent(android.view.MotionEvent)"
		/*
		public boolean onKeyDown(int,android.view.KeyEvent) {
        	hk.polyu.uiref.MainActivity $r0;
        	int $i0;
        	android.view.MotionEvent $r1;
        	boolean $z0;

        	$r0 := @this: hk.polyu.uiref.MainActivity;
        	$i0 := @parameter0: int;
        	$r1 := @parameter1: android.view.KeyEvent;
        	staticinvoke <hk.polyu.uiref.UiTraversal: void traverseLayout(android.app.Activity,int)>($r0, $i0);
        	$z0 = true;
        	return $z0;
    	}
		*/
        // SootMethod ActivityOnKeyDownMethod = ActivityClass.getMethod("boolean onKeyDown(int,android.view.KeyEvent)");
        SootMethod UiTraversalTraverseLayoutMethod = UiTraversalClass.getMethod("void traverseLayout(android.app.Activity,int)");
        SootClass KeyEventClass = Scene.v().getSootClass("android.view.KeyEvent");
        SootMethod onKeyDownMethod =
                new SootMethod("onKeyDown", Arrays.asList(new Type[] {IntType.v(), KeyEventClass.getType()}), BooleanType.v(), Modifier.PUBLIC);
        fakeMainActivity.addMethod(onKeyDownMethod);

        JimpleBody onKeyDownBody = Jimple.v().newBody(onKeyDownMethod);
        onKeyDownMethod.setActiveBody(onKeyDownBody);

        LocalGenerator onKeyDownLocalGenerator = new LocalGenerator(onKeyDownMethod.getActiveBody());
        // $r0 := @this: hk.polyu.uiref.MainActivity;
        Local onkeydown_lhs_0 = onKeyDownLocalGenerator.generateLocal(fakeMainActivity.getType());
        ThisRef onkeydown_rhs_0 = Jimple.v().newThisRef(fakeMainActivity.getType());
        IdentityStmt onkeydown_stmt_0 = Jimple.v().newIdentityStmt(onkeydown_lhs_0, onkeydown_rhs_0);
        // $i0 := @parameter0: int;
        Local onkeydown_lhs_1 = onKeyDownLocalGenerator.generateLocal(IntType.v());
        ParameterRef onkeydown_rhs_1 = Jimple.v().newParameterRef(IntType.v(), 0);
        IdentityStmt onkeydown_stmt_1 = Jimple.v().newIdentityStmt(onkeydown_lhs_1, onkeydown_rhs_1);
        // $r1 := @parameter1: android.view.KeyEvent;
        Local onkeydown_lhs_2 = onKeyDownLocalGenerator.generateLocal(KeyEventClass.getType());
        ParameterRef onkeydown_rhs_2 = Jimple.v().newParameterRef(KeyEventClass.getType(), 1);
        IdentityStmt onkeydown_stmt_2 = Jimple.v().newIdentityStmt(onkeydown_lhs_2, onkeydown_rhs_2);
        // staticinvoke <hk.polyu.uiref.UiTraversal: void traverseLayout(android.app.Activity,android.view.MotionEvent)>($r0, $i0);
        Local onkeydown_arg_3_0 = onkeydown_lhs_0;
        Local onkeydown_arg_3_1 = onkeydown_lhs_1;
        StaticInvokeExpr onkeydown_expr_3 = Jimple.v().newStaticInvokeExpr(UiTraversalTraverseLayoutMethod.makeRef(), onkeydown_arg_3_0, onkeydown_arg_3_1);
        InvokeStmt onkeydown_stmt_3 = Jimple.v().newInvokeStmt(onkeydown_expr_3);
        // $z0 = true;
        Local onkeydown_lhs_4 = onKeyDownLocalGenerator.generateLocal(BooleanType.v());
        AssignStmt onkeydown_stmt_4 = Jimple.v().newAssignStmt(onkeydown_lhs_4, IntConstant.v(1));
        // return $z0;
        Local onkeydown_arg_5_0 = onkeydown_lhs_4;
        ReturnStmt onkeydown_stmt_5 = Jimple.v().newReturnStmt(onkeydown_arg_5_0);

        List<Unit> onkeydownStmtList = new ArrayList<Unit>();
        onkeydownStmtList.add(onkeydown_stmt_0);
        onkeydownStmtList.add(onkeydown_stmt_1);
        onkeydownStmtList.add(onkeydown_stmt_2);
        onkeydownStmtList.add(onkeydown_stmt_3);
        onkeydownStmtList.add(onkeydown_stmt_4);
        onkeydownStmtList.add(onkeydown_stmt_5);

        onKeyDownBody.getUnits().addAll(onkeydownStmtList);
        // finish creating -- "public boolean onTouchEvent(android.view.MotionEvent)"
    }

	/*
	private void showActivityContructor() {
		for (SootClass sClass : Scene.v().getApplicationClasses()) {
			if (sClass.isConcrete()) {
				for (SootMethod sMethod : sClass.getMethods()) {
					System.out.println(sMethod.retrieveActiveBody().toString());
				}
			}
		}
		System.exit(0);
	}
	*/

}

