package com.vinhnt.iolab.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.apache.commons.fileupload2.core.FileItemInputIterator;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// https://www.baeldung.com/java-download-file
@RestController
public class FileOperatorController {
    private static final String UPLOAD_DIRECTORY = "/home/vinhnt211/Downloads/test/";
    private static final String LARGE_FILE_1GB_LINK = "https://bit.ly/1GB-testfile";
    private static final String LARGE_FILE_5GB_LINK = "https://bit.ly/5GB-TESTFILE-ORG";
    private static final String IMAGE = "https://cache.giaohangtietkiem.vn/d/c0686705093c578ff383e477dbd39597.png";
    private static final List<String> ALL_FILES_TO_DOWNLOAD = List.of(
            IMAGE,
            LARGE_FILE_1GB_LINK,
            LARGE_FILE_5GB_LINK
    );
    private static final Logger log = LogManager.getLogger(FileOperatorController.class);

    @PostMapping("/api/upload")
    public String handleUpload(HttpServletRequest request) {
        JakartaServletFileUpload<DiskFileItem, DiskFileItemFactory> upload = new JakartaServletFileUpload<>();
        try {
            FileItemInputIterator iterStream = upload.getItemIterator(request);
            while (iterStream.hasNext()) {
                FileItemInput item = iterStream.next();
                InputStream stream = item.getInputStream();
                if (!item.isFormField()) {
                    String filename = item.getName();
                    OutputStream out = new FileOutputStream(UPLOAD_DIRECTORY + filename);
                    IOUtils.copy(stream, out);
                    stream.close();
                    out.close();
                }
            }
            return "success!";
        } catch (IOException ex) {
            return "failed: " + ex.getMessage();
        }
    }

    @PostMapping("/api/url-upload")
    public String handleUrlUpload() {
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        List<Future<?>> futureDownload = new ArrayList<>();
        for (String path : ALL_FILES_TO_DOWNLOAD) {
            futureDownload.add(executorService.submit(() -> {
                var filename = path.substring(path.lastIndexOf("/") + 1);

                try (BufferedInputStream in = new BufferedInputStream(new URI(path).toURL().openStream());
                     FileOutputStream fileOutputStream = new FileOutputStream(String.format("%s%s", UPLOAD_DIRECTORY, filename))) {

                    byte[] dataBuffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(dataBuffer, 0, 4096)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }
                } catch (IOException | URISyntaxException ex) {
                    log.error("Error ", ex);
                }
            }));

            futureDownload.forEach(item -> {
                try {
                    item.get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        executorService.close();
        return "success!";
    }

    @PostMapping("/api/download")
    public String handleDownload(@RequestParam("file") String file) {
        return "";
    }
}

