package com.example.initbackend.image.controller;

import com.example.initbackend.global.response.StatusEnum;
import com.example.initbackend.global.response.SuccessResponse;
import com.example.initbackend.image.service.ImageService;
import com.example.initbackend.image.vo.UploadImageResponseVo;
import lombok.Getter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Getter
@RestController
@RequestMapping("api/image")
public class ImageController {

    private final ImageService imageService;
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping({"/upload"})
    // dto 로 수정
    public SuccessResponse uploadImage(HttpServletRequest request, @RequestPart(value = "image") MultipartFile multipartFile) {
        UploadImageResponseVo uploadImageResponse = imageService.uploadImage(request, multipartFile);
        SuccessResponse res = SuccessResponse.builder()
                .status(StatusEnum.OK)
                .message("Uploaded Image")
                .data(uploadImageResponse)
                .build();

        return res;
    }
}
