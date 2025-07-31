package vn.baymax.fjob.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    @Value("${baymax.upload-file.base-uri}")
    private String baseURI;

    public void createDirectory(String folder) throws URISyntaxException {
        URI uri = new URI(folder);
        Path path = Paths.get(uri);
        File tmpDir = new File(path.toString());
        if (!tmpDir.isDirectory()) {
            try {
                Files.createDirectory(tmpDir.toPath());
                System.out.println("create a new directory cuccess");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("folder already exist");
        }
    }

    public String store(MultipartFile file, String folder) throws URISyntaxException, IOException {
        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        URI uri = new URI(baseURI + folder + "/" + fileName);
        Path path = Paths.get(uri);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }

        return fileName;
    }
}
