package com.rezakov.demo.service.impl;

import com.rezakov.demo.classes.Data;
import com.rezakov.demo.classes.TreeNode;
import com.rezakov.demo.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Stream;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path path = Paths.get("fileStorage");


    @Override
    public void init() {
        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public void save(MultipartFile multipartFile) {
        try {
            Files.copy(multipartFile.getInputStream(),this.path.resolve(multipartFile.getOriginalFilename()));
        } catch (IOException e) {
            throw new RuntimeException("Could not store the file. Error:"+e.getMessage());
        }
    }
/*
    @Override
    public TreeNode getTreeNode(MultipartFile multipartFile) {
        try{
            String path = multipartFile.getOriginalFilename();
            File doc = new File("fileStorage/" + path);
            //Scanner scanner = new Scanner(file.getInputStream());
            Scanner scanner = new Scanner(doc);
            ArrayList<Data> results = new ArrayList<>();
            int id=1;
            int parent=0;
            int tempParent=0;
            long occurencesCount=0;
            int[] levels = new int[20];
            while (scanner.hasNextLine()) {
                String temp = scanner.nextLine();
                occurencesCount = temp.chars().filter(ch -> ch == '#').count();
                if (occurencesCount == 0) {
                    parent = tempParent;
                } else {
                    levels[(int) occurencesCount] = id;
                    parent = levels[(int) occurencesCount - 1];
                    tempParent = id;
                }
                results.add(new Data(id, temp, parent));
                id++;
            }

        }
        catch (Exception e) {
            throw new RuntimeException("Could not read the file. Error:"+e.getMessage());
        }

    }

*/

    @Override
    public Resource load(String filename) {
        Path file = path.resolve(filename);
        try {
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()){
                return resource;
            }else{
                throw new RuntimeException("Could not read the file.");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error:"+e.getMessage());
        }
    }

    @Override
    public Stream<Path> load() {
        try {
            return Files.walk(this.path,1)
                    .filter(path -> !path.equals(this.path))
                    .map(this.path::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files.");
        }
    }
    @Override
    public void clear() {
        FileSystemUtils.deleteRecursively(path.toFile());
    }

}
