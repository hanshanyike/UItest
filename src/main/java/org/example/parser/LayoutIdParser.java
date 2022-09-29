package org.example.parser;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import soot.Scene;
import soot.SootClass;
import soot.SootField;

public class LayoutIdParser {

    private String packageName = ManifestParser.packageName;

    public static HashSet<Integer> idValueSet = new HashSet<Integer>();

    public void exec() {
        init();
        retrieveLayoutId();
    }

    public static void reset() {
        idValueSet = new HashSet<Integer>();
    }

    private void init() {
        packageName = ManifestParser.packageName;
    }

    private void retrieveLayoutId() {
        for (SootClass sClass : Scene.v().getClasses()) {
            if (!sClass.isApplicationClass() || !sClass.isConcrete())
                continue;
            if (sClass.getName().contains(packageName + ".R$layout")) {
                // System.out.println(sClass.getName());
                for (SootField sField : sClass.getFields()) {
                    String idValueTag = sField.getTag("IntegerConstantValueTag").toString();
                    int idValue = Integer.parseInt(idValueTag.substring("ConstantValue: ".length()));
                    // System.out.println(idValue);
                    idValueSet.add(idValue);
                }
            }
        }
    }

    public static ArrayList<Integer> sortIdValue(HashSet<Integer> idSet) {
        ArrayList<Integer> sortedIdList = new ArrayList<Integer>();
        for (Integer idValue : idSet) {
            sortedIdList.add(idValue);
        }
        Collections.sort(sortedIdList);

        return sortedIdList;
    }

}
