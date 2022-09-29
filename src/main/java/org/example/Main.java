package org.example;

import brut.androlib.AndrolibException;
import io.appium.java_client.remote.MobileCapabilityType;
import org.jdom2.JDOMException;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

import org.example.parser.ManifestParser;
import org.example.parser.LayoutIdParser;
import org.example.soot.MainActivityCreator;
import org.example.soot.Repackage;
import org.example.soot.SootEnvironment;
import org.example.util.ApkSigner;
import org.example.util.ADBAutomator;
import org.example.util.IgnoreProcessedApks;


import io.appium.java_client.android.AndroidDriver;

public class Main {
    private static AndroidDriver driver;
    public static String apkPath = null;
    //需要修改为特定路径！
    public static String projectPath = "F:/Java_Workplace/UItest";
    public static String apkRootDirectoryPath = "F:/Java_Workplace/UItest/data/gui_app";
    public static String successProcessedFile = "success.txt";
    public static String errorProcessedFile = "error.txt";

    public static void main(String[] args) throws MalformedURLException {
        System.out.println("Hello world!");
        File apkRootDirectory = new File(apkRootDirectoryPath);
        if (!apkRootDirectory.exists()) {
            System.out.println("invalid apkRootDirectoryPath");
            System.exit(0);
        }
        File[] apkFiles = apkRootDirectory.listFiles();
        for (File apkFile : apkFiles) {
            System.gc();
            if (!apkFile.getName().endsWith(".apk"))
                continue;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            ADBAutomator automator = null;
            boolean execFlag = false;
            boolean execActivityFlag = false; // 两个标志位来确定是谁引发了异常
            try{
                apkPath = apkFile.getCanonicalPath();
                System.out.println(apkPath);
                LayoutIdParser.reset();

                ManifestParser manifestParser = new ManifestParser();
                manifestParser.exec();

                Repackage repackage = new Repackage();
                repackage.exec();

                SootEnvironment.instance().initSoot();

                LayoutIdParser idParser = new LayoutIdParser();
                idParser.exec();

                MainActivityCreator mainActivityCreator = new MainActivityCreator();
                mainActivityCreator.exec();

                SootEnvironment.instance().saveApk();

                ApkSigner signer = new ApkSigner();
                signer.exec();

                automator = new ADBAutomator();

                execFlag = true;
                execActivityFlag = false;
                automator.exec();
                execFlag = false;

                execActivityFlag = true;
                automator.execActivity();
                execActivityFlag = false;

                automator.clearApk();

                writeToFile(successProcessedFile, apkPath);

            } catch (IOException | JDOMException | InterruptedException | AndrolibException e) {
                if (e.getMessage().contains("INSTALL_FAILED"))
                    continue;

                writeToFile(errorProcessedFile, apkPath + "\n" + e.getMessage() + "\n");
                e.printStackTrace();

                if (automator != null) {
                    try {
                        if (execFlag) {
                            automator.execAfterException();
                            automator.execAfterExceptionActivity();
                        }
                        if (execActivityFlag) {
                            automator.execAfterExceptionActivity();
                        }

                        automator.clearApk();
                    } catch(Exception error) {
                        error.printStackTrace();
                    }
                }
                throw new RuntimeException(e);

            }
            System.gc();
        }


    }
    public void connectAppium() throws MalformedURLException{
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("appium:platformVersion", "11.0");
        desiredCapabilities.setCapability("appium:deviceName", "emulator-5554");
        desiredCapabilities.setCapability("appium:appPackage", "com.android.settings");
        desiredCapabilities.setCapability("appium:appActivity", ".Settings");
        desiredCapabilities.setCapability("appium:ensureWebviewsHavePages", true);
        desiredCapabilities.setCapability("appium:nativeWebScreenshot", true);
        desiredCapabilities.setCapability("appium:newCommandTimeout", 3600);
        desiredCapabilities.setCapability("appium:connectHardwareKeyboard", true);
        URL remoteUrl = new URL("http://127.0.0.1:4723/wd/hub");
        driver = new AndroidDriver(remoteUrl, desiredCapabilities);
        System.out.println(driver.getPageSource());
    }
    private static void writeToFile(String filePath, String content) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file, true);
            fw.write(content + "\n");
            fw.flush();
            fw.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}