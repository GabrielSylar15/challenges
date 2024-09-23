package com.vinhnt.iolab.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.apache.commons.fileupload2.core.FileItemInputIterator;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@RestController
public class FileOperatorController {
    private static final String UPLOAD_DIRECTORY = "E:/tmp/hehe/";

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
}
