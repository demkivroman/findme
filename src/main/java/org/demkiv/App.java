package org.demkiv;

import org.demkiv.domain.Config;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Config config = Config.getInstance();
        System.out.println(config.getS3AccessKey());
    }
}
