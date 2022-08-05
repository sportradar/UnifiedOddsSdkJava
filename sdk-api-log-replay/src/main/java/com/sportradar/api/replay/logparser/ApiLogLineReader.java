package com.sportradar.api.replay.logparser;

import com.sportradar.api.replay.logparser.listener.ApiLogListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ApiLogLineReader {

  private final List<File> logFiles;
  private final ApiLogListener listener;
  private final Consumer<String> consumer;
  private int index;
  private BufferedReader reader;

  @Setter
  private Charset charset = StandardCharsets.UTF_8;

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
    consumer.accept(line);
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
      File file = logFiles.get(index++);
      Path path = file.toPath();
      try {
        reader = Files.newBufferedReader(path, charset);
        listener.onNewLogfile(file);
        return reader;
      } catch (IOException e) {
        throw new RuntimeException("Error reading logfile! : " + path);
      }
    }
  }
}
