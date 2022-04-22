package com.rezakov.demo.classes;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class TextReader {

    File doc;

    String fileName;
    Scanner scanner;

    public ArrayList<Data> getDatas(MultipartFile multipartFile) throws Exception {
        doc = new File(multipartFile.getOriginalFilename());
        scanner = new Scanner(doc);
        ArrayList<Data> items=new ArrayList<>();

        while(scanner.hasNextLine()) {
            items.add(new Data(0,scanner.nextLine(), 0));
        }
        return items;
    }
}
