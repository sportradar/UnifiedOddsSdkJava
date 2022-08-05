package com.sportradar.unifiedodds.example.player;

import com.sportradar.unifiedodds.example.player.exceptions.LogFileNotFound;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileValidator {

  public List<File> validate(List<String> filepaths) {
    List<File> files = new ArrayList<>();

    for (String filepath : filepaths) {
      try {
        Path path = provideValidPath(filepath);
        files.add(path.toFile());
      } catch (LogFileNotFound e) {
        throw new IllegalArgumentException("Not a valid filepath! : " + filepath);
      }
    }
    return files;
  }

  public Path provideValidPath(String filePath) throws LogFileNotFound {
    try {
      URI fullUriPath = Optional
          .ofNullable(getClass().getClassLoader().getResource(filePath))
          .orElseThrow(() -> new IllegalArgumentException("Log file not found ~> " + filePath))
          .toURI();

      return Paths.get(fullUriPath);
    } catch (URISyntaxException e) {
      throw new LogFileNotFound("Invalid log URI path ~> " + filePath, e);
    }
  }
}
