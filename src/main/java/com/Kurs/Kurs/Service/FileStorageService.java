package com.Kurs.Kurs.Service;

import com.Kurs.Kurs.Models.FileDB;
import com.Kurs.Kurs.Models.UserTask;
import com.Kurs.Kurs.Repository.FileDBRepository;
import com.Kurs.Kurs.Repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;

@Service
public class FileStorageService {
    @Autowired
    FileDBRepository fileDBRepository;

    public void addFile(MultipartFile file, UserTask userTask) throws IOException{
        String name= file.getOriginalFilename();
        FileDB fileDB=new FileDB(name,file.getContentType(),file.getBytes());
        fileDBRepository.save(fileDB);
        userTask.getFiles().add(fileDB);
    }
    public FileDB findFileById(Long id){
        return fileDBRepository.findById(id).get();
    }
}
