package org.example.soot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import soot.PackManager;
import soot.Scene;
import soot.options.Options;

public class SootEnvironment {

    public static String apkPath = Repackage.modifiedApkPath;
    //需要修改为特定路径！
    public static String platformPath = "D:/AndroidTool/AndroidSDK/platforms";
    public static String outputApkDirPath = "UnsignedApk/";

    public void setApkPath(String path) {
        apkPath = path;
    }
    public String getApkPath() {
        return apkPath;
    }

    public void setOutputApkDir(String dir) {
        outputApkDirPath = dir;
    }

    public static String getApkName() {
        return apkPath.split("/")[apkPath.split("/").length - 1].replace(".apk", "");
    }

    // single instance
    private static SootEnvironment instance = null;
    public static SootEnvironment instance() {
        if (instance == null) {
            instance = new SootEnvironment();
        }
        return instance;
    }

    public void initSoot() {
        soot.G.reset();

        apkPath = Repackage.modifiedApkPath;

        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_whole_program(true);
        Options.v().set_process_multiple_dex(true);
        Options.v().set_src_prec(Options.src_prec_apk_class_jimple);
        Options.v().set_output_format(Options.output_format_dex);
        List<String> processList = new ArrayList<String>();
        processList.add(apkPath);
        //需要修改为特定路径！
        processList.add("F:/Java_Workplace/UiRef_workspace/UiRefUtility/bin");
        Options.v().set_process_dir(processList);
        Options.v().set_android_jars(platformPath);
        Options.v().set_android_api_version(30);
        String sootClassPath = Options.v().android_jars();
        //Linux 分隔符为： Windows分隔符为;
//        sootClassPath += ":framework/framework.jar";
        sootClassPath += ";framework/framework.jar";
        Options.v().set_soot_classpath(sootClassPath);
        Options.v().set_keep_line_number(false);
        Options.v().set_keep_offset(false);
        Options.v().set_ignore_resolving_levels(true);

        Scene.v().loadNecessaryClasses();
    }

    public void saveApk() throws IOException {
        File outputApkDir = new File(outputApkDirPath);
        if (outputApkDir.exists()) {
            FileUtils.deleteDirectory(outputApkDir);
        }
        outputApkDir.mkdir();

        Options.v().set_output_dir(outputApkDirPath);
        PackManager.v().writeOutput();
    }

}
