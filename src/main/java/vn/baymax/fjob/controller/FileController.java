package vn.baymax.fjob.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.baymax.fjob.dto.response.ResUploadFIleDTO;
import vn.baymax.fjob.service.FileService;
import vn.baymax.fjob.util.annotation.ApiMessage;
import vn.baymax.fjob.util.error.FileStorageException;

@RestController
@RequestMapping("/api/v1")
public class FileController {
    @Value("${baymax.upload-file.base-uri}")
    private String baseURI;

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage("upload single file")
    public ResponseEntity<ResUploadFIleDTO> upload(@RequestParam MultipartFile file,
            @RequestParam String folder) throws URISyntaxException, IOException, FileStorageException {
        // validate file
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("File is empty");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValidFile = allowedExtensions.stream()
                .anyMatch(item -> fileName.toLowerCase().endsWith(item));
        if (!isValidFile) {
            throw new FileStorageException(
                    "Invalid file extension, only allows \"pdf\", \"jpg\", \"jpeg\", \"png\", \"doc\", \"docx\"");
        }

        // create a directory if not exist
        this.fileService.createDirectory(baseURI + folder);
        // store file
        String finalName = this.fileService.store(file, folder);
        return ResponseEntity.ok().body(
                ResUploadFIleDTO.builder()
                        .fileName(finalName)
                        .uploadedAt(Instant.now())
                        .build());
    }
}
