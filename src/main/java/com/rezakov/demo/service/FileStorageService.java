package com.rezakov.demo.service;

import com.rezakov.demo.classes.TreeNode;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileStorageService {

    void init();

    void save(MultipartFile multipartFile);

    //TreeNode getTreeNode(MultipartFile multipartFile);

    Resource load(String filename);

    Stream<Path> load();

    void clear();
}
