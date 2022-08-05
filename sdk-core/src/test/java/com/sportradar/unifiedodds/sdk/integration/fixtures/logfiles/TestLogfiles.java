package com.sportradar.unifiedodds.sdk.integration.fixtures.logfiles;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestLogfiles {

  private TestLogfiles() {
  }

  public static File logfileFromTestFolder(String testLogfile) {
    Path path = toResourcesFolderPath(testLogfile);
    File file = path.toFile();
    if (!file.isDirectory() && file.getName().endsWith(".log")) {
      return file;
    } else {
      throw new IllegalArgumentException("Must be a .log file!");
    }
  }

  public static List<File> logsFromTestFolder(String resourcesFolderName) {
    Path path = toResourcesFolderPath(resourcesFolderName);
    return logsFromFolder(path.toFile());
  }

  public static List<File> logsFromFolder(File logFileFolder) {
    if (!logFileFolder.isDirectory()) {
      throw new IllegalArgumentException("Not a folder! : " + logFileFolder);
    }

    File[] fileArray = logFileFolder.listFiles((dir, name) -> name.endsWith(".log"));
    if (fileArray.length == 0) {
      throw new IllegalStateException("No .log files found! : " + logFileFolder);
    }

    List<File> files = Arrays.asList(fileArray);
    files.sort(Comparator.comparing(File::getName));
    return files;
  }

  private static Path toResourcesFolderPath(String filename) {
    try {
      ClassLoader classLoader = TestLogfiles.class.getClassLoader();
      URL resourceURL = classLoader.getResource(filename);
      URI fullUriPath = resourceURL.toURI();
      return Paths.get(fullUriPath);
    } catch (URISyntaxException e) {
      log.error("File not found: {}", filename, e);
      throw new RuntimeException("File not found " + filename, e);
    }
  }
}
