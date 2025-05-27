package com.fidypay.controller;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fidypay.service.FileUploadService;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/fileUpload")
public class FileUploadController {

	@Autowired
	private FileUploadService fileUploadService;

	@ApiOperation(value = "File Upload")
	@PostMapping("/fileUpload")
	public Map<String, Object> fileUpload(@RequestPart(value = "file") @NotNull MultipartFile file) throws Exception {
		return fileUploadService.fileUpload(file);
	}

}
