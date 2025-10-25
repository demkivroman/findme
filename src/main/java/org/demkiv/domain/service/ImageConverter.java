package org.demkiv.domain.service;

import java.io.IOException;

public interface ImageConverter {

    public byte[] resizeKeepAspect(byte[] originalBytes, int targetWidth, int targetHeight) throws IOException;
    public byte[] cropToSquare(byte[] originalBytes, int size) throws IOException;
    public byte[] resizeWithPadding(byte[] originalBytes, int size) throws IOException;
}
