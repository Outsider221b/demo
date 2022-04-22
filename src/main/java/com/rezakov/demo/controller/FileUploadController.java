package com.rezakov.demo.controller;

import com.rezakov.demo.classes.Data;
import com.rezakov.demo.service.FileStorageService;
import com.rezakov.demo.valueobject.Message;
import com.rezakov.demo.valueobject.UploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@RestController
public class FileUploadController {

    @Autowired
    FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadAndRead(@RequestParam("file") MultipartFile file){
        try {
            fileStorageService.save(file);

            //start
            String path = file.getOriginalFilename();
            File doc = new File("fileStorage/" + path);
            Scanner scanner = new Scanner(file.getInputStream());
            //Scanner scanner = new Scanner(doc);
            ArrayList<Data> results = new ArrayList<>();
            int id=1;
            int parent=0;
            int tempParent=0;
            long occurencesCount=0;
            int[] levels = new int[20];
            while (scanner.hasNextLine()) {
                String temp = scanner.nextLine();

                occurencesCount = temp.chars().filter(ch -> ch == '#').count();
                if(occurencesCount == 0) {
                    parent = tempParent;
                }
                else {
                    levels[(int)occurencesCount] = id;
                    parent = levels[(int)occurencesCount - 1];
                    tempParent = id;
                }

                results.add(new Data(id, temp, parent));
                id++;
            }
            //end
            return ResponseEntity.ok(results);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new Message("Could not upload the file:"+file.getOriginalFilename()));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<UploadFile>> files(){
        List<UploadFile> files = fileStorageService.load()
                .map(path -> {
                    String fileName = path.getFileName().toString();
                    String url = MvcUriComponentsBuilder
                            .fromMethodName(FileUploadController.class,
                                    "getFile",
                                    path.getFileName().toString()
                            ).build().toString();
                    return new UploadFile(fileName,url);
                }).collect(Collectors.toList());
        return ResponseEntity.ok(files);
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable("filename")String filename){
        Resource file = fileStorageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=\""+file.getFilename()+"\"")
                .body(file);
    }
}
