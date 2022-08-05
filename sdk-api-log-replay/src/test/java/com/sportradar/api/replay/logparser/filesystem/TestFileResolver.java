package com.sportradar.api.replay.logparser.filesystem;

import com.sportradar.api.replay.logparser.LogParserException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TestFileResolver {

  private TestFileResolver() {
  }

  public static List<File> logsFromTestFolder(String resourcesFolderName) {
    File file = testResourcesFile(resourcesFolderName);
    return logsFromFolder(file);
  }

  public static File testResourcesFolderPath(String logFolderName) {
    File file = testResourcesFile(logFolderName);
    if (!file.isDirectory()) {
      throw new IllegalArgumentException(logFolderName + " is not a folder!");
    }
    return file;
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

  public static File testResourcesFile(String filename) {
    try {
      ClassLoader classLoader = TestFileResolver.class.getClassLoader();
      URL resourceURL = classLoader.getResource(filename);
      URI fullUriPath = resourceURL.toURI();
      return Paths.get(fullUriPath).toFile();
    } catch (URISyntaxException e) {
      throw new LogParserException("File not found " + filename, e);
    }
  }

  public static List<File> testResourcesFiles(String... filenames) {
    List<File> files = new ArrayList<>();
    for (String filename : filenames) {
      files.add(testResourcesFile(filename));
    }
    return files;
  }
}
