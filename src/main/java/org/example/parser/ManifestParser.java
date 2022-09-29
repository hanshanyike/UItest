package org.example.parser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jadx.api.JadxArgs;
import jadx.api.JadxDecompiler;
import org.apache.commons.io.FileUtils;
import org.jdom2.Attribute;
import org.jdom2.AttributeType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import brut.androlib.Androlib;
import brut.androlib.AndrolibException;
import brut.androlib.ApkDecoder;
import brut.directory.ExtFile;
import org.example.Main;
import org.example.soot.SootEnvironment;
import org.example.util.BashRunner;
//import hk.polyu.MainImpl;
//import hk.polyu.soot.SootEnvironment;
//import hk.polyu.util.BashRunner;

public class ManifestParser {

    private String apkPath = Main.apkPath;
    public static String packageName = null;
    public static ArrayList<String> activityList = new ArrayList<String>();
    private Integer minSdkVersion = -1;
    private Integer targetSdkVersion = 30;
    public static String resourcesOutputDirectoryPath = "ResourcesOutput/";
    public static String resourcesResultDirectoryPath = "ResourcesResult/";

    public void exec() throws IOException, JDOMException, InterruptedException, AndrolibException {
        init();
        retrieveResourcesFile();
        retrieveLayoutIds();
        modifyManifestFile();
        compressResourcesFile();
    }

    private void init() throws IOException {
        apkPath = Main.apkPath;
        packageName = null;
        activityList = new ArrayList<String>();
        minSdkVersion = -1;
        targetSdkVersion = 30;

        File resourcesOutputDirectory = new File(resourcesOutputDirectoryPath);
        if (resourcesOutputDirectory.exists()) {
            FileUtils.deleteDirectory(resourcesOutputDirectory);
        }
        resourcesOutputDirectory.mkdir();

        File resourcesResultDirectory = new File(resourcesResultDirectoryPath);
        if (resourcesResultDirectory.exists()) {
            FileUtils.deleteDirectory(resourcesResultDirectory);
        }
        resourcesResultDirectory.mkdir();
    }

    private void retrieveResourcesFile() throws AndrolibException, IOException {
        File apkFile = new File(apkPath);
        if (!apkFile.exists()) {
            System.err.println("Apk path " + apkPath + " is not valid ...");
            System.exit(0);
        }

        ApkDecoder decoder = new ApkDecoder(apkFile);
        if (decoder.hasResources()) {
            decoder.setTargetSdkVersion();
            decoder.setAnalysisMode(false);
            if (decoder.hasManifest()) {
                Androlib mAndrolib = decoder.getMAndrolib();
                ExtFile mApkFile = decoder.getMApkFile();
                File manifestOutputDirectory = new File(resourcesOutputDirectoryPath);
                mAndrolib.decodeResourcesFull(mApkFile, manifestOutputDirectory, decoder.getResTable());
                mAndrolib.decodeManifestWithResources(mApkFile, manifestOutputDirectory, decoder.getResTable());
            }
        }

        Map<String, String> sdkInfo = decoder.getResTable().getSdkInfo();
        if (sdkInfo.containsKey("minSdkVersion")) {
            minSdkVersion = Integer.parseInt(sdkInfo.get("minSdkVersion"));
        }
        if (sdkInfo.containsKey("targetSdkVersion")) {
            targetSdkVersion = Integer.parseInt(sdkInfo.get("targetSdkVersion"));
        }
    }
    public  void deleteDollar() {

    }

    private void retrieveLayoutIds() {
//        String publicFilePath = resourcesOutputDirectoryPath + "res/values/public.xml";
        String publicFilePath = resourcesOutputDirectoryPath + "res/values/public.xml";
        File publicFile = new File(publicFilePath);
        try {
            BufferedReader br = new BufferedReader(new FileReader(publicFile));
            String content = null;
            while ((content = br.readLine()) != null) {
                if (content.contains("type=\"layout\"")) {
                    String idHexStr = content.split("id=\"")[1].split("\" ")[0];
                    LayoutIdParser.idValueSet.add(Integer.parseInt(idHexStr.substring(2), 16));
                }
            }
        } catch (FileNotFoundException fnfe) {
            System.err.println("public.xml does not exist");
            System.exit(0);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(0);
        }
    }

    private String ANDROID_NAMESPACE_PREFIX = "android";
    private String ANDROID_NAMESPACE_URI = "http://schemas.android.com/apk/res/android";
    private Namespace ANDROID_NAMESPACE = Namespace.getNamespace(ANDROID_NAMESPACE_PREFIX, ANDROID_NAMESPACE_URI);

    private void modifyManifestFile() throws IOException, JDOMException {
//        String manifestFilePath = resourcesOutputDirectoryPath + "AndroidManifest.xml";
        String manifestFilePath = resourcesOutputDirectoryPath + "AndroidManifest.xml";
        File manifestFile = new File(manifestFilePath);
        if (!manifestFile.exists()) {
            System.err.println("AndroidManiferst.xml does not exist");
            System.exit(0);
        }

        // gain the real NAMESPACE
        SAXBuilder builder = new SAXBuilder();
        Document manifestDoc = builder.build(manifestFile);

        Element manifestElement = manifestDoc.getRootElement();
        assert manifestElement.getName().equals("manifest");

        for (Namespace namespace : manifestElement.getNamespacesIntroduced()) {
            if (namespace.getURI().equals(ANDROID_NAMESPACE_URI)) {
                ANDROID_NAMESPACE_PREFIX = namespace.getPrefix();
                ANDROID_NAMESPACE = Namespace.getNamespace(ANDROID_NAMESPACE_PREFIX, ANDROID_NAMESPACE_URI);
                break;
            }
        }
        // gain the package name
        Attribute packageAttr = manifestElement.getAttribute("package", Namespace.NO_NAMESPACE);
        if (packageAttr != null) {
            packageName = packageAttr.getValue();
        }
        assert packageName != null;

        Element applicationElement = manifestElement.getChild("application");
        assert applicationElement != null;

        // 1. retrieve declared activities
        // 2. add exported attribute
        List<Element> activityElements = applicationElement.getChildren("activity");
        for (Element activityElement : activityElements) {
            Attribute activityNameAttr = activityElement.getAttribute("name", ANDROID_NAMESPACE);
            String activityName = activityNameAttr.getValue();
            if (activityName.startsWith(".")) {
                activityName = packageName + activityName;
            }
            if (!activityName.contains(".")) {
                activityName = packageName + "." + activityName;
            }
            activityList.add(activityName);

            Attribute activityExportAttr = activityElement.getAttribute("exported", ANDROID_NAMESPACE);
            if (activityExportAttr == null) {
                Attribute exportedAttr = new Attribute("exported", "true", AttributeType.CDATA, ANDROID_NAMESPACE);
                List<Attribute> attrList = activityElement.getAttributes();
                attrList.add(exportedAttr);
            } else {
                String exportedValue = activityExportAttr.getValue();
                if (exportedValue.equals("false")) {
                    activityExportAttr.setValue("true");
                }
            }
        }

        // remove LAUNCHER

        // register fakeMainActivity
        Element activityElement = new Element("activity");
        Attribute nameAttr = new Attribute("name", packageName + ".FakeMainActivity", AttributeType.CDATA, ANDROID_NAMESPACE);
        Attribute exportedAttr = new Attribute("exported", "true", AttributeType.CDATA, ANDROID_NAMESPACE);
        ArrayList<Attribute> attrList = new ArrayList<Attribute>();
        attrList.add(nameAttr);
        attrList.add(exportedAttr);
        activityElement.setAttributes(attrList);

        //
        Element intentFilterElement = new Element("intent-filter");
        activityElement.getChildren().add(intentFilterElement);

        Element actionElement = new Element("action");
        nameAttr = new Attribute("name", "android.intent.action.MAIN", AttributeType.CDATA, ANDROID_NAMESPACE);
        actionElement.setAttribute(nameAttr);

        Element categoryElement = new Element("category");
        nameAttr = new Attribute("name", "android.intent.category.LAUNCHER", AttributeType.CDATA, ANDROID_NAMESPACE);
        categoryElement.setAttribute(nameAttr);

        intentFilterElement.getChildren().add(actionElement);
        intentFilterElement.getChildren().add(categoryElement);
        //

        applicationElement.getChildren().add(activityElement);

        // write the modified AndroidManifest.xml
        manifestFile.delete();
        manifestFile.createNewFile();
        XMLOutputter xout = new XMLOutputter();
        FileWriter fw = new FileWriter(manifestFile);
        xout.output(manifestDoc, fw);
        fw.close();
    }

    private void compressResourcesFile() throws IOException, InterruptedException {
        ArrayList<String> commands = new ArrayList<String>();
//        String[] commonCmd = { "/bin/sh", "-c" };
//        commands.addAll(Arrays.asList(commonCmd));
//        String[] commonCmd = {"F:","cd F:\\Java_Workplace\\UItest"};
//        commands.addAll(Arrays.asList(commonCmd));
//        commands.add("aapt");
        if (minSdkVersion != -1) {
            commands.add(String.format("%s %s %s %s %s %s %s %s %s %s %s %s %s",
                    "aapt", "package", "-f",
                    "-M", Main.projectPath+"/"+resourcesOutputDirectoryPath + "AndroidManifest.xml",
                    "--target-sdk-version", targetSdkVersion,
                    "-I", SootEnvironment.platformPath + "/android-" + targetSdkVersion + "/android.jar",
                    "-S", Main.projectPath+"/"+resourcesOutputDirectoryPath +  "res/",
                    "-F", Main.projectPath+"/"+resourcesResultDirectoryPath + "/resources.zip"));
        } else {
            commands.add(String.format("%s %s %s %s %s %s %s %s %s %s %s %s %s %s %s",
                    "aapt", "package", "-f",
                    "--min-sdk-version", minSdkVersion,
                    "--target-sdk-version", targetSdkVersion,
                    "-M", Main.projectPath+"/"+resourcesOutputDirectoryPath + "AndroidManifest.xml",
                    "-I", SootEnvironment.platformPath + "/android-" + targetSdkVersion + "/android.jar",
                    "-S", Main.projectPath+"/"+resourcesOutputDirectoryPath + "res/",
                    "-F", Main.projectPath+"/"+resourcesResultDirectoryPath + "/resources.zip"));
        }
        BashRunner bash = new BashRunner(commands, false);
        bash.run();
    }

}
