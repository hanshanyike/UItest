package org.example.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.example.parser.LayoutIdParser;
import org.example.parser.ManifestParser;

public class ADBAutomator {

    private String apkPath = ApkSigner.signedApkPath;
    private String packageName = ManifestParser.packageName;
    private String fakeMainActivityName = "FakeMainActivity";
    private String KEYCODE_FORWARD = "KEYCODE_FORWARD";
    private String KEYCODE_BACK = "KEYCODE_BACK";

    private String outputRootPath = "UIOutput";
    private String outputApkPath = null;

    private int exceptionTime = 0;
    private int MAX_EXCEPTION_TIME = 8;

    private void init() {
        apkPath = ApkSigner.signedApkPath;
        packageName = ManifestParser.packageName;
        exceptionTime = 0;

        File outputRoot = new File(outputRootPath);
        if (!outputRoot.exists())
            outputRoot.mkdir();

        outputApkPath = outputRootPath + "/" + packageName;
        File outputApk = new File(outputApkPath);
        if (!outputApk.exists())
            outputApk.mkdir();
    }

    private int currentIdIndex = -1; // index == -1 对应的input会被drop掉
    public void exec() throws IOException, InterruptedException, RuntimeException {
        init();
        // ADBCmd.adbConnect();
        ADBCmd.adbApkInstall(apkPath);
        ADBCmd.adbStartActivity(packageName, fakeMainActivityName);
        ADBCmd.adbCreateDirectory(packageName);

        ArrayList<Integer> idValueList = LayoutIdParser.sortIdValue(LayoutIdParser.idValueSet);
        for (int i = currentIdIndex; i < idValueList.size(); i++) {
            ADBCmd.adbRetrieveFocusedActivity();
            ADBCmd.adbInputKeyevent(KEYCODE_FORWARD);
            if (i != -1) {
                System.out.println("layout id: " + idValueList.get(i));
                ADBCmd.adbDumpView(packageName, Integer.toString(idValueList.get(i)));
                ADBCmd.adbPullView(packageName, Integer.toString(idValueList.get(i)), outputApkPath);
                ADBCmd.adbDumpSnapshot(packageName, Integer.toString(idValueList.get(i)));
                ADBCmd.adbPullSnapshot(packageName, Integer.toString(idValueList.get(i)), outputApkPath);
            }
            currentIdIndex++;
        }
    }

    public void reexec() {
        exceptionTime++;
        if (exceptionTime >= MAX_EXCEPTION_TIME) {
            return;
        }

        try {
            currentIdIndex++;
            //
            for (int x = 1800 - 192; x <= 1800 + 192; x = x + 48) {
                for (int  y = 2070 - 160; y <= 2070 + 160; y = y + 40) {
                    ADBCmd.adbInputTap(x, y);
                }
            }
            //
			/*
			for (int x = 873 - 108; x <= 873 + 108; x = x + 36) {
				for (int  y = 1012 - 171; y <= 1012 + 171; y = y + 57) {
					ADBCmd.adbInputTap(x, y);
				}
			}
			*/

            ADBCmd.adbStartActivity(packageName, fakeMainActivityName);

            ArrayList<Integer> idValueList = LayoutIdParser.sortIdValue(LayoutIdParser.idValueSet);
            for (int i = currentIdIndex; i < idValueList.size(); i++) {
                ADBCmd.adbRetrieveFocusedActivity();
                ADBCmd.adbInputKeyevent(KEYCODE_FORWARD);
                System.out.println("layout id: " + idValueList.get(i));
                ADBCmd.adbDumpView(packageName, Integer.toString(idValueList.get(i)));
                ADBCmd.adbPullView(packageName, Integer.toString(idValueList.get(i)), outputApkPath);
                ADBCmd.adbDumpSnapshot(packageName, Integer.toString(idValueList.get(i)));
                ADBCmd.adbPullSnapshot(packageName, Integer.toString(idValueList.get(i)), outputApkPath);
                currentIdIndex++;
            }
        } catch (IOException ioe) {
            reexec();
        } catch (InterruptedException ie) {
            reexec();
        } catch (RuntimeException re) {
            reexec();
        }
    }

    public void execAfterException() throws IOException, InterruptedException, RuntimeException {
        reexec();
    }

    // for activity
    private int currentActivityIndex = 0;
    private boolean flag = false; // 记录activity的异常是否由activity导致, 避免exec的异常影响currentActivityIndex的计数
    public void execActivity() throws IOException, InterruptedException, RuntimeException {
        // ADBCmd.adbConnect();
        flag = true;
        ArrayList<String> activityList = ManifestParser.activityList;
        for (int i = currentActivityIndex; i < activityList.size(); i++) {
            ADBCmd.adbStopApplication(packageName);
            ADBCmd.adbStartActivityFull(packageName, activityList.get(i));
            Thread.sleep(3000);
            ADBCmd.adbRetrieveFocusedActivity(activityList.get(i));
            System.out.println("activity name: " + activityList.get(i));
            ADBCmd.adbDumpView(packageName, activityList.get(i));
            ADBCmd.adbPullView(packageName, activityList.get(i), outputApkPath);
            ADBCmd.adbDumpSnapshot(packageName, activityList.get(i));
            ADBCmd.adbPullSnapshot(packageName, activityList.get(i), outputApkPath);
            currentActivityIndex++;
        }
    }

    public void reexecActivity() {
        try {
            if (flag == true) {
                currentActivityIndex++;
                ADBCmd.adbInputKeyevent(KEYCODE_BACK);
                //
                for (int x = 1800 - 192; x <= 1800 + 192; x = x + 48) {
                    for (int  y = 2070 - 160; y <= 2070 + 160; y = y + 40) {
                        ADBCmd.adbInputTap(x, y);
                    }
                }
                //
				/*
				for (int x = 873 - 108; x <= 873 + 108; x = x + 36) {
					for (int  y = 1012 - 171; y <= 1012 + 171; y = y + 57) {
						ADBCmd.adbInputTap(x, y);
					}
				}
				*/
            }

            flag = true;
            ArrayList<String> activityList = ManifestParser.activityList;
            for (int i = currentActivityIndex; i < activityList.size(); i++) {
                ADBCmd.adbStopApplication(packageName);
                ADBCmd.adbStartActivityFull(packageName, activityList.get(i));
                Thread.sleep(3000);
                ADBCmd.adbRetrieveFocusedActivity(activityList.get(i));
                System.out.println("activity name: " + activityList.get(i));
                ADBCmd.adbDumpView(packageName, activityList.get(i));
                ADBCmd.adbPullView(packageName, activityList.get(i), outputApkPath);
                ADBCmd.adbDumpSnapshot(packageName, activityList.get(i));
                ADBCmd.adbPullSnapshot(packageName, activityList.get(i), outputApkPath);
                currentActivityIndex++;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            reexecActivity();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            reexecActivity();
        } catch (RuntimeException re) {
            re.printStackTrace();
            reexecActivity();
        }
    }

    public void execAfterExceptionActivity() throws IOException, InterruptedException, RuntimeException {
        reexecActivity();
    }

    public void clearApk() throws IOException, InterruptedException, RuntimeException {
        ADBCmd.adbRemoveDirectory(packageName);
        ADBCmd.adbApkUninstall(packageName);
        ADBCmd.adbDisconnect();
        currentIdIndex = -1;
        currentActivityIndex = 0;
        flag = false;
    }

}

