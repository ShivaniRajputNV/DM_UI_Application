package com.portal.datamig.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/api")
public class CSVController {
    private static String UPLOADED_FOLDER ="/csvs";
    @GetMapping("/download/{name}")
        public ResponseEntity<Resource> download(@PathVariable("name") String name) throws IOException,FileNotFoundException {
            Resource resource = new ClassPathResource(UPLOADED_FOLDER+"/"+name+"Lookup"+".csv");
            File file = resource.getFile();
            // File file = new File(UPLOADED_FOLDER+"/"+name+".csv");
            Path path = Paths.get(file.getAbsolutePath());
            ByteArrayResource bresource = new ByteArrayResource(Files.readAllBytes(path));
            return ResponseEntity.ok().headers(this.headers(name))
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("aapplication/octed-stream")).body(bresource);
        }
        private HttpHeaders headers(String name) {
            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, 
                  "attachment; filename=" + name+".csv");
            header.add("Cache-Control", "no-cache, no-store,"
                  + " must-revalidate");
            header.add("Pragma", "no-cache");
            header.add("Expires", "0");
            return header;
         }
         //CSV READ DATA
         @GetMapping("/read/{name}")
        public Map<String, String> readCSVFile(@PathVariable("name") String name) throws IOException ,Exception{
            Resource resource = new ClassPathResource("csvs/"+name+"Lookup"+".csv");
            File file = resource.getFile();
            FileReader filereader = new FileReader(file);
            BufferedReader br = new BufferedReader(filereader);
            Map<String,String> map = new HashMap<String,String>();
            String[] groupArray = new String[1000];
                    String line;
                    int i = 0,k=0;
                    while((line = br.readLine()) != null) {
                        String[] split = line.split(",");
                        if(k==0){
                            k++;
                            continue;
                        }
                        groupArray[i] = split[0].trim();
                        groupArray[i+1] = (split.length > 1) ? split[1].trim() : "";
                        i += 2;
                    }
                    String keyname, keyvalue;
                    for(int j = 0; j < i; j += 2) {
                        keyname = groupArray[j];
                        keyvalue = groupArray[j+1];
                        map.put(keyname, keyvalue);
                    }
            for (String key : map.keySet()) {
                    System.out.println(key + " " + map.get(key));
                }
                // model.addAttribute("csvdata",map);
            return map;
    }
    
}
