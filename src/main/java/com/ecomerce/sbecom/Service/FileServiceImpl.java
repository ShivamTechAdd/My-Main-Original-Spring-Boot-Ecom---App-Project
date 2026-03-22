package com.ecomerce.sbecom.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        // File Names fo current file / original file.
        String originalFileName = file.getOriginalFilename();

        //Genrate a Unique File name (Random UUID)
        String randomId = UUID.randomUUID().toString();
        String uniqueFileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        String filePath = path + File.separator + uniqueFileName;

        // Check if path exist or create;
        File folder = new File(path);
        if(!folder.exists())
            folder.mkdir();

        // Upload to server.
        Files.copy(file.getInputStream(), Paths.get(filePath));

        // return file to server.
        return uniqueFileName;
    }
}

