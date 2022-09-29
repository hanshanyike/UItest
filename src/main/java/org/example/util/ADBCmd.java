package org.example.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ADBCmd {

    public static void adbConnect() throws IOException, InterruptedException {
        ArrayList<String> commands = new ArrayList<String>();
//        String[] commonCmd = { "/bin/sh", "-c" };
//        commands.addAll(Arrays.asList(commonCmd));
        commands.add(String.format("%s %s", "adb", "shell"));
        BashRunner bash = new BashRunner(commands, false);
        bash.run();
    }

    public static void adbDisconnect() throws IOException, InterruptedException {
        ArrayList<String> commands = new ArrayList<String>();
//        String[] commonCmd = { "/bin/sh", "-c" };
//        commands.addAll(Arrays.asList(commonCmd));
        commands.add(String.format("%s %s %s", "adb", "shell", "exit"));
        BashRunner bash = new BashRunner(commands, false);
        bash.run();
    }

    public static void adbApkInstall(String apkPath) throws IOException, InterruptedException, RuntimeException {
        ArrayList<String> commands = new ArrayList<String>();
//        String[] commonCmd = { "/bin/sh", "-c" };
//        commands.addAll(Arrays.asList(commonCmd));
        commands.add(String.format("%s %s %s", "adb", "install", apkPath));
        BashRunner bash = new BashRunner(commands, false);
        String output = bash.run();
        String[] outputSplit = output.split("\n");
//        if (!output.split("\n")[output.split("\n").length - 2].contains("Success"))
//            throw new RuntimeException(output.split("\n")[output.split("\n").length - 2]);
        if (!outputSplit[outputSplit.length - 1].contains("Success"))
            throw new RuntimeException(outputSplit[outputSplit.length - 1]);
    }

    public static void adbApkUninstall(String packageName) throws IOException, InterruptedException, RuntimeException {
        ArrayList<String> commands = new ArrayList<String>();
//        String[] commonCmd = { "/bin/sh", "-c" };
//        commands.addAll(Arrays.asList(commonCmd));
        commands.add(String.format("%s %s %s", "adb", "uninstall", packageName));
        BashRunner bash = new BashRunner(commands, false);
        String output = bash.run();
//        if (!output.split("\n")[output.split("\n").length - 2].contains("Success"))
//            throw new RuntimeException(output.split("\n")[output.split("\n").length - 2]);
        String[] outputSplit = output.split("\n");
        if (!outputSplit[outputSplit.length - 1].contains("Success"))
            throw new RuntimeException(outputSplit[outputSplit.length - 1]);
    }

    public static void adbStartActivity(String packageName, String activityName) throws IOException, InterruptedException {
        ArrayList<String> commands = new ArrayList<String>();
//        String[] commonCmd = { "/bin/sh", "-c" };
//        commands.addAll(Arrays.asList(commonCmd));
        commands.add(String.format("%s %s %s %s %s %s", "adb", "shell", "am", "start", "-n", packageName + "/." + activityName));
        BashRunner bash = new BashRunner(commands, false);
        bash.run();
    }

    public static void adbStartActivityFull(String packageName, String activityFullName) throws IOException, InterruptedException {
        ArrayList<String> commands = new ArrayList<String>();
//        String[] commonCmd = { "/bin/sh", "-c" };
//        commands.addAll(Arrays.asList(commonCmd));
        commands.add(String.format("%s %s %s %s %s %s", "adb", "shell", "am", "start", "-n", packageName + "/" + activityFullName));
        BashRunner bash = new BashRunner(commands, false);
        bash.run();
    }

    public static void adbStopApplication(String packageName) throws IOException, InterruptedException {
        ArrayList<String> commands = new ArrayList<String>();
//        String[] commonCmd = { "/bin/sh", "-c" };
//        commands.addAll(Arrays.asList(commonCmd));
        commands.add(String.format("%s %s %s %s %s", "adb", "shell", "am", "force-stop", packageName));
        BashRunner bash = new BashRunner(commands, false);
        bash.run();
    }

    public static void adbInputKeyevent(String keycode) throws IOException, InterruptedException {
        ArrayList<String> commands = new ArrayList<String>();
//        String[] commonCmd = { "/bin/sh", "-c" };
//        commands.addAll(Arrays.asList(commonCmd));
        commands.add(String.format("%s %s %s %s %s", "adb", "shell", "input", "keyevent", keycode));
        BashRunner bash = new BashRunner(commands, false);
        bash.run();
    }

    public static void adbInputTap(int x, int y) throws IOException, InterruptedException {
        ArrayList<String> commands = new ArrayList<String>();
//        String[] commonCmd = { "/bin/sh", "-c" };
//        commands.addAll(Arrays.asList(commonCmd));
        commands.add(String.format("%s %s %s %s %s %s", "adb", "shell", "input", "tap", x, y));
        BashRunner bash = new BashRunner(commands, false);
        bash.run();
    }

    public static void adbDumpView(String packageName, String index) throws IOException, InterruptedException {
        ArrayList<String> commands = new ArrayList<String>();
//        String[] commonCmd = { "/bin/sh", "-c" };
//        commands.addAll(Arrays.asList(commonCmd));
        commands.add(String.format("%s %s %s %s %s", "adb", "shell", "uiautomator", "dump", "/sdcard/" + packageName + "/" + index + ".xml"));
        BashRunner bash = new BashRunner(commands, false);
        bash.run();
    }

    public static void adbPullView(String packageName, String index, String outputDirectory) throws IOException, InterruptedException {
        ArrayList<String> commands = new ArrayList<String>();
//        String[] commonCmd = { "/bin/sh", "-c" };
//        commands.addAll(Arrays.asList(commonCmd));
        commands.add(String.format("%s %s %s %s", "adb", "pull", "/sdcard/" + packageName + "/" + index + ".xml", outputDirectory));
        BashRunner bash = new BashRunner(commands, false);
        bash.run();
    }

    public static void adbDumpSnapshot(String packageName, String index) throws IOException, InterruptedException {
        ArrayList<String> commands = new ArrayList<String>();
//        String[] commonCmd = { "/bin/sh", "-c" };
//        commands.addAll(Arrays.asList(commonCmd));
        commands.add(String.format("%s %s %s %s %s", "adb", "shell", "screencap", "-p", "/sdcard/" + packageName + "/" + index + ".png"));
        BashRunner bash = new BashRunner(commands, false);
        bash.run();
    }

    public static void adbPullSnapshot(String packageName, String index, String outputDirectory) throws IOException, InterruptedException {
        ArrayList<String> commands = new ArrayList<String>();
//        String[] commonCmd = { "/bin/sh", "-c" };
//        commands.addAll(Arrays.asList(commonCmd));
        commands.add(String.format("%s %s %s %s", "adb", "pull", "/sdcard/" + packageName + "/" + index + ".png", outputDirectory));
        BashRunner bash = new BashRunner(commands, false);
        bash.run();
    }

    public static void adbCreateDirectory(String packageName) throws IOException, InterruptedException {
        ArrayList<String> commands = new ArrayList<String>();
//        String[] commonCmd = { "/bin/sh", "-c" };
//        commands.addAll(Arrays.asList(commonCmd));
        commands.add(String.format("%s %s %s %s", "adb", "shell", "mkdir", "/sdcard/" + packageName));
        BashRunner bash = new BashRunner(commands, false);
        bash.run();
    }

    public static void adbRemoveDirectory(String packageName) throws IOException, InterruptedException {
        ArrayList<String> commands = new ArrayList<String>();
//        String[] commonCmd = { "/bin/sh", "-c" };
//        commands.addAll(Arrays.asList(commonCmd));
        commands.add(String.format("%s %s %s %s %s", "adb", "shell", "rm", "-rf", "/sdcard/" + packageName));
        BashRunner bash = new BashRunner(commands, false);
        bash.run();
    }

    public static void adbRetrieveFocusedActivity() throws IOException, InterruptedException {
        ArrayList<String> commands = new ArrayList<String>();
//        String[] commonCmd = { "/bin/sh", "-c" };
//        commands.addAll(Arrays.asList(commonCmd));
        commands.add(String.format("%s %s %s %s %s %s %s", "adb", "shell", "dumpsys", "activity", "|", "grep", "mFocusedActivity"));
        BashRunner bash = new BashRunner(commands, false);
        String output = bash.run();

        if (!output.contains(".FakeMainActivity"))
            throw new RuntimeException("focused activity is not the \"FakeMainActivity\"");
    }

    public static void adbRetrieveFocusedActivity(String activityName) throws IOException, InterruptedException {
        ArrayList<String> commands = new ArrayList<String>();
//        String[] commonCmd = { "/bin/sh", "-c" };
//        commands.addAll(Arrays.asList(commonCmd));
        commands.add(String.format("%s %s %s %s %s %s %s", "adb", "shell", "dumpsys", "activity", "|", "grep", "mFocusedActivity"));
        BashRunner bash = new BashRunner(commands, false);
        String output = bash.run();

        if (!output.contains(activityName.split("\\.")[activityName.split("\\.").length - 1]))
            throw new RuntimeException("focused activity is not the \"" + activityName + "\"");
    }

}