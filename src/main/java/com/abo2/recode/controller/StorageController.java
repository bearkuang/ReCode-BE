package com.abo2.recode.controller;

import com.abo2.recode.dto.file.FileInfo;
import com.abo2.recode.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
public class StorageController {

    @Autowired
    private StorageService service;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file) {
        String fileName = service.uploadFile(file);
        return ResponseEntity.ok(fileName);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
        byte[] data = service.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        return new ResponseEntity<>(service.deleteFile(fileName), HttpStatus.OK);
    }

    @GetMapping("/info/{fileName}")
    public ResponseEntity<FileInfo> getFileDetails(@PathVariable String fileName) {
        FileInfo fileInfo = service.getFileDetails(fileName);
        return new ResponseEntity<>(fileInfo, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchFiles(@RequestParam String keyword) {
        List<String> matchingFiles = service.searchFilesByKeyword(keyword);
        return new ResponseEntity<>(matchingFiles, HttpStatus.OK);
    }
}
