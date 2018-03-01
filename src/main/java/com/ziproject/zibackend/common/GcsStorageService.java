package com.ziproject.zibackend.common;

import com.google.appengine.tools.cloudstorage.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class GcsStorageService {

  private static final String DEFAULT_BUCKET_NAME = "ziproject-gcloud.appspot.com";
  private static final String MIME_TYPE_TEXT = "text/plain";
  private final GcsService gcsService;
  private static GcsStorageService gcsStorageService = null;

  private GcsStorageService() {
    gcsService = GcsServiceFactory.createGcsService();
  }

  public static GcsStorageService getGcsStorageService() {
    if (gcsStorageService == null) {
      gcsStorageService = new GcsStorageService();
    }
    return gcsStorageService;
  }

  private GcsFileMetadata getFileMetadata(String fileName)
      throws IOException {
    GcsFileMetadata metadata =
      gcsService.getMetadata(new GcsFilename(DEFAULT_BUCKET_NAME, fileName));
    if (metadata == null) {
      throw new FileNotFoundException();
    }
    return metadata;
  }

  public String fetchTextFileMetadataString(String fileName) throws IOException {
    return getFileMetadata(fileName).toString();
  }

  public String fetchTextFileContentString(String fileName) throws IOException {
    GcsFileMetadata metadata = getFileMetadata(fileName);
    String mimeType = metadata.getOptions().getMimeType();
    if (!mimeType.contains(MIME_TYPE_TEXT)) {
      throw new InvalidObjectException(
        String.format("File mime type: want %s but was %s", MIME_TYPE_TEXT, mimeType));
    }

    GcsInputChannel readChannel = gcsService.openReadChannel(
      new GcsFilename(DEFAULT_BUCKET_NAME, fileName), 0);
    int bufferSize = 1024;
    ByteBuffer readBuffer = ByteBuffer.allocate(bufferSize);
    int bytesRead = readChannel.read(readBuffer);
    return new String(readBuffer.array(), 0, bytesRead, StandardCharsets.UTF_8);
  }
}
