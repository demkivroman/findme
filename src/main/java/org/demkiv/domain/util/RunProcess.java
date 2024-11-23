package org.demkiv.domain.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class RunProcess {
    public static int runProcess(File workingDir, String command, Writer output) throws IOException, InterruptedException {
        List<String> commands = new ArrayList<String>();
        commands.add("/bin/sh");
        commands.add("-c");
        commands.add(command);
        String[] commandStrArray = commands.toArray(new String[commands.size()]);
        ProcessBuilder pb = new ProcessBuilder(commandStrArray);
        pb.directory(workingDir);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        InputStream processOut = process.getInputStream();
        byte[] buffer = new byte[5000];
        for (; ;) {
            int nB = processOut.read(buffer);
            if (nB <= 0)
                break;
            if (output != null)
                output.append(new String(buffer, 0, nB));
        }
        return process.waitFor();
    }
}
