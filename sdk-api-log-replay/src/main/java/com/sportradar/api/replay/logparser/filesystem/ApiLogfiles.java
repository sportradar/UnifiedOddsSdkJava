package com.sportradar.api.replay.logparser.filesystem;

import com.sportradar.api.replay.logparser.LogParserException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiLogfiles {

  private ApiLogfiles() {
  }

  public static List<File> logsFromResourcesFolder(String resourcesFolderName) {
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
      ClassLoader classLoader = ApiLogfiles.class.getClassLoader();
      URL resourceURL = classLoader.getResource(filename);
      URI fullUriPath = resourceURL.toURI();
      return Paths.get(fullUriPath);
    } catch (URISyntaxException e) {
      log.error("File not found: {}", filename, e);
      throw new LogParserException("File not found " + filename, e);
    }
  }

  // FIXME remove
  public static void main(String[] args) {
    System.out.println("Files from src/test/resources path:");
    List<File> files = ApiLogfiles.logsFromResourcesFolder("");
    files.forEach(System.out::println);

    System.out.println("\nFiles from absolute path:");
    files = ApiLogfiles.logsFromFolder(new File(
        "/Users/h.odonnell.ext/IdeaProjects/uof-sdk-scratch/sdk-api-log-replay/src/test/resources"));
    files.forEach(System.out::println);
  }
}
