package org.demkiv;

import org.demkiv.domain.Config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        Path path = Files.createTempDirectory("temp_photo");
        System.out.println(path);
    }
}
