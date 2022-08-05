package com.sportradar.api.replay.apiserver.handlers.canned;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.SneakyThrows;

class FileLoader {

  @SneakyThrows
  public String load(String filepath) {
    return doLoad(filepath);
  }

  private String doLoad(String filepath) throws IOException {
    FileLoader app = new FileLoader();
    InputStream inputStream = app.getFileFromResourceAsStream(filepath);

    InputStreamReader isReader = new InputStreamReader(inputStream);
    BufferedReader reader = new BufferedReader(isReader);
    StringBuffer sb = new StringBuffer();
    String str;
    while ((str = reader.readLine()) != null) {
      sb.append(str);
    }
    return sb.toString();
  }

  // get a file from the resources folder
  // works everywhere, IDEA, unit test and JAR file.
  private InputStream getFileFromResourceAsStream(String fileName) {

    // The class loader that loaded the class
    ClassLoader classLoader = getClass().getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream(fileName);

    // the stream holding the file content
    if (inputStream == null) {
      throw new IllegalArgumentException("file not found! " + fileName);
    } else {
      return inputStream;
    }
  }
}
