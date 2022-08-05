package com.sportradar.unifiedodds.sdk.logs;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ApiLogLineReader {

  private final List<Path> logFiles;
  private final ApiLogListener listener;
  private final Consumer<LineFileModel> consumer;
  private int index;
  private BufferedReader reader;

  @Setter
  private Charset charset = StandardCharsets.UTF_8;

  @Getter
  private String fileName;

  public String nextLogLine() {
    String line;

    try {
      BufferedReader reader = currentReader();
      if (reader == null) {
        return null;
      }
      line = reader.readLine();
      while (line == null) {
        this.reader = nextReader();
        if (this.reader == null) {
          listener.onComplete();
          return null;
        } else {
          line = this.reader.readLine();
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("Error reading logfile! ", e);
    }
    LineFileModel lineFileModel = new LineFileModel(line, fileName);
    consumer.accept(lineFileModel);
    return line;
  }

  public void close() {
    if (reader != null) {
      try {
        reader.close();
      } catch (IOException e) {
        throw new RuntimeException("Error closing log reader!", e);
      }
    }
  }

  private BufferedReader currentReader() {
    if (reader == null) {
      reader = nextReader();
    }
    return reader;
  }

  private BufferedReader nextReader() {
    close();
    if (index >= logFiles.size()) {
      return null;
    } else {
      Path path = logFiles.get(index++);
      //Path path = file.toPath();
      try {
        fileName = path.toFile().getName();
        reader = Files.newBufferedReader(path, charset);
        listener.onNewLogfile(path);
        return reader;
      } catch (IOException e) {
        throw new RuntimeException("Error reading logfile! : " + path);
      }
    }
  }
}
