package model.logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by marcin on 12.06.2017.
 */
public class Logger {
    private String initTime;
    private String fileName;
    private Path file;

    public Logger (String fileName, String folderName) {
        this.initTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHssSSS"));
        this.fileName = fileName + "_" + initTime.toString() + ".txt";
        this.file = Paths.get(folderName + "/" + this.fileName);
        try {
            Files.createDirectory(file.getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLine (List<String> content) {
        byte[] buffer = (String.join(";", content) + System.lineSeparator()).getBytes();
        try {
            Files.write(file, buffer, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
