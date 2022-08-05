package com.sportradar.unifiedodds.sdk.logs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sportradar.unifiedodds.sdk.logs.config.ConfigModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomerLogAnalyser {
  private GeneralLogProcessor logProcessor;
  //private final RecordingApiLogProcessorListener listener = new RecordingApiLogProcessorListener();

  private final
  ConfigModel configModel;

  private GeneralLogProcessor createFromDir(String dir) throws IOException {
    File fileDir = new File(dir);
    if (!fileDir.isDirectory()) {
      throw new RuntimeException("Not a dir : " + dir);
    }
//    ArrayList<File> fileList = new ArrayList<>();
//    File[] files = fileDir.listFiles();
//    for (File f : files) {
//      if (f.isDirectory()) {
//        continue;
//      } else {
//        fileList.add(f);
//      }
//    }

    List<Path> paths = Files.walk(Paths.get(dir)).filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".log")).collect(Collectors.toList());

    System.out.println("-------------->");
    Files.walk(Paths.get(dir)).filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".log")).forEach(System.out::println);
    // Files.walk(Paths.get(dir)).filter(Files::isRegularFile).forEach(System.out::println);

    return new GeneralLogProcessor(paths, configModel);
  }

  public void execute() throws Exception {
    logProcessor = createFromDir("/home/mnegus/git/uof-sdk-scratch/sdk-core/uf-sdk-logs");
    //logProcessor = createFromDir("/home/mnegus/data/losdk/logs/kladonica/20220514/combined");
    //logProcessor = createFromDir("/home/mnegus/data/losdk/logs/eurobet/20220514");
    //   logProcessor = createFromDir("/home/mnegus/data/losdk/logs/eurobet/20220508/live/");
//    logProcessor = createFromDir("/home/mnegus/data/losdk/logs/eurobet/20220501/prematch/");
    logProcessor.run();

    logProcessor.dumpExceptionDataAsConfluenceTable();

//    Set<String> urls = logProcessor.getUniqueUrls();
//    urls.forEach((value) -> System.out.println(value));

    new WriteToInflux(configModel).write(logProcessor.getEventMap().values());
  }

  public static void main(String[] args) throws Exception {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    // ConfigModel configModel = mapper.readValue(EurobetAnalyser.class.getResourceAsStream("/eurobet.yaml"), ConfigModel.class);
    ConfigModel configModel = mapper.readValue(CustomerLogAnalyser.class.getResourceAsStream("/fixture-change.yaml"), ConfigModel.class);
    // Set the timezone
    if (configModel.getTimezone() != null) {
      System.setProperty("user.timezone", configModel.getTimezone());
    }

    new CustomerLogAnalyser(configModel).execute();
  }
}
