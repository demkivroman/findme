package org.demkiv.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.service.ProcessRunner;
import org.demkiv.domain.util.RunProcess;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.Writer;

@Slf4j
@Service
public class ProcessRunnerImpl implements ProcessRunner {
    @Override
    public int runProcess(File workingDir, String command, Writer output) throws Exception {
        return RunProcess.runProcess(workingDir, command, output);
    }
}
