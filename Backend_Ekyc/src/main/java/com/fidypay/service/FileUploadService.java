package com.fidypay.service;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

	Map<String, Object> fileUpload(@NotNull MultipartFile file);

}
