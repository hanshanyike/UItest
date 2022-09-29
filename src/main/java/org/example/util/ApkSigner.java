package org.example.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.example.soot.SootEnvironment;
import org.example.Main;

public class ApkSigner {

    private String unsignedApkPath = SootEnvironment.outputApkDirPath + SootEnvironment.getApkName() + ".apk";
    public static String signedApkPath = SootEnvironment.outputApkDirPath + SootEnvironment.getApkName() + ".s.apk";
    private String signJarPath = Main.projectPath+"/lib/sign.jar";

    public void exec() throws IOException, InterruptedException {

        unsignedApkPath = SootEnvironment.outputApkDirPath + SootEnvironment.getApkName() + ".apk";
        signedApkPath = SootEnvironment.outputApkDirPath + SootEnvironment.getApkName() + ".s.apk";

        ArrayList<String> commands = new ArrayList<String>();
//        String[] commonCmd = { "/bin/sh", "-c" };
//        commands.addAll(Arrays.asList(commonCmd));
        commands.add(String.format("%s %s %s %s",
                "java", "-jar", signJarPath,
                unsignedApkPath));
        BashRunner bash = new BashRunner(commands, false);
        bash.run();
    }

}

