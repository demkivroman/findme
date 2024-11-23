package org.demkiv.domain.service;

import java.io.File;
import java.io.Writer;

public interface ProcessRunner {
    int runProcess(File workingDir, String command, Writer output) throws Exception;
}
