package com.njit.takeoutsystem.controller;

import com.njit.takeoutsystem.common.ApiResponse;
import com.njit.takeoutsystem.common.BusinessException;
import com.njit.takeoutsystem.utils.CurrentUserUtil;
import com.njit.takeoutsystem.vo.UploadVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    private static final long MAX_SIZE = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private final CurrentUserUtil currentUserUtil;

    public UploadController(CurrentUserUtil currentUserUtil) {
        this.currentUserUtil = currentUserUtil;
    }

    @PostMapping("/image")
    public ApiResponse<UploadVO> uploadImage(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam("file") MultipartFile file
    ) {
        currentUserUtil.requireAdmin(authorizationHeader);
        if (file.isEmpty()) {
            throw new BusinessException(400, "请选择图片文件");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException(400, "图片不能超过 5MB");
        }

        String extension = extensionOf(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(400, "仅支持 jpg、jpeg、png、webp 格式");
        }

        String fileName = UUID.randomUUID() + "." + extension;
        Path directory = Path.of("uploads", "dishes").toAbsolutePath();
        Path target = directory.resolve(fileName);

        try {
            Files.createDirectories(directory);
            file.transferTo(target);
        } catch (IOException exception) {
            throw new BusinessException(500, "图片上传失败");
        }

        return ApiResponse.success("上传成功", new UploadVO("/uploads/dishes/" + fileName, fileName));
    }

    private String extensionOf(String originalName) {
        if (originalName == null || !originalName.contains(".")) {
            throw new BusinessException(400, "图片文件名无效");
        }
        return originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
