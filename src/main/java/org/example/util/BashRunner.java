package org.example.util;

import java.io.*;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;

public class BashRunner {

    private final ArrayList<String> bashCommands;
    private final boolean async;

    public BashRunner(ArrayList<String> bashCommands, boolean async) {
        this.bashCommands = bashCommands;
        this.async = async;
    }

    public String run() throws IOException, InterruptedException {
//        ProcessBuilder builder = new ProcessBuilder(this.bashCommands);
//        builder.redirectOutput(Redirect.PIPE);
//        builder.redirectError(Redirect.PIPE);
//        builder.redirectErrorStream();
//        Process process = builder.start();
//        // wait for thread if not asynchronous
//        if (!async)
//            process.waitFor();
//
//        byte[] buffer = new byte[4096 * 1024];
//        int bufferLength;
//        String pipeContent = "";
//        BufferedInputStream bis = new BufferedInputStream(process.getInputStream());
//        while((bufferLength = bis.read(buffer)) > 0) {
//            pipeContent += new String(buffer);
//        }
//
//        return pipeContent;
        try {
            //只能执行一条命令
            Process process = Runtime.getRuntime().exec(this.bashCommands.get(0));
            StringBuilder stringBuilder = new StringBuilder();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            while (line != null) {
                System.out.println(line);
                stringBuilder.append(line).append("\n");
                line = br.readLine();
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
