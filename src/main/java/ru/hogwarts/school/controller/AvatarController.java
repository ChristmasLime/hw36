package ru.hogwarts.school.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.services.AvatarService;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@RestController
@RequestMapping("/avatar")
public class AvatarController {
    public final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @GetMapping("/from-disk/{id}")
    public void fromDisk(@PathVariable Long id, HttpServletResponse response){
        Avatar avatar=avatarService.getById(id);
        response.setContentType(avatar.getMediaType());
        response.setContentLength((int)avatar.getFileSize());
        try(FileInputStream fis =new  FileInputStream(avatar.getFilePath())){
            fis.transferTo(response.getOutputStream());
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/from-db/{id}")
    public ResponseEntity<byte[]> fromDb(@PathVariable Long id){
        Avatar avatar=avatarService.getById(id);
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getFileSize());
        return ResponseEntity.status(200).headers(headers).body(avatar.getData());
    }

    @GetMapping("/paginate")
    public ResponseEntity<List<Avatar>> getAvatarsPaginated(@RequestParam ("page") Integer page ,@RequestParam ("size" )Integer size ) {
        List<Avatar> avatarsPage = avatarService.findAvatarsPaginated(page,size);
        return ResponseEntity.ok(avatarsPage);
    }
}


