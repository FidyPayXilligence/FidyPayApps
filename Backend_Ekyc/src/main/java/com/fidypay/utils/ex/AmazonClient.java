package com.fidypay.utils.ex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fidypay.entity.CredDetails;
import com.fidypay.repo.CredDetailsRepository;
import com.fidypay.request.S3ObjectRequest;
import com.fidypay.service.S3ObjectServices;

@Service
public class AmazonClient {

	private AmazonS3 s3client;
	
	@Autowired
	private CredDetailsRepository credDetailsRepository;

	@Autowired
	private S3ObjectServices s3ObjectServices;

	@Value("${amazonProperties.endpointUrl}")
	private String endpointUrl;
	@Value("${s3.bucket}")
	private String bucketName;

//	@Value("${aws.accessKeyId}")
//	private String accessKey;
//	@Value("${aws.secretAccessKey}")
//	private String secretKey;

	@PostConstruct
	private void initializeAmazon() {
		String accessKey = "NA";
		String secretAccessKey = "NA";

		CredDetails credDetails = credDetailsRepository.findByName("FCp+nhI04y0zDsGYsSDYIA==");
		if (credDetails != null) {
			accessKey = credDetails.getAccesskey();
			secretAccessKey = credDetails.getSecretAccesskey();
		}

		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretAccessKey);
		this.s3client = new AmazonS3Client(credentials);
	}


	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

	private String generateFileName() {
		String profilePictureAwsKey = ImageUtils.getUniqueId();
		return profilePictureAwsKey.replace(" ", "_");
	}

	private void uploadFileTos3bucket(String fileName, File file) {
		s3client.putObject(
				new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
	}

	public String uploadFile(MultipartFile multipartFile, Long merchantId, String serviceName) {

		String fileUrl = "";
		try {
			File file = convertMultiPartToFile(multipartFile);
			String fileName = generateFileName();
			fileUrl = endpointUrl + "/" + fileName;
			uploadFileTos3bucket(fileName, file);
			file.delete();

			S3ObjectRequest s3ObjectRequest = new S3ObjectRequest();
			s3ObjectRequest.setBucketName(bucketName);
			s3ObjectRequest.setMerchantId(merchantId);
			s3ObjectRequest.setObjectKey(fileName);
			s3ObjectRequest.setObjectUrl(fileUrl);
			s3ObjectRequest.setServiceName(serviceName);

			s3ObjectServices.saveS3Object(s3ObjectRequest);

		} catch (Exception e) {

		}
		return fileUrl;
	}

	public String uploadFileV2(MultipartFile multipartFile) {

		String fileUrl = "";
		try {
			File file = convertMultiPartToFile(multipartFile);
			String fileName = generateFileName();
			fileUrl = endpointUrl + "/" + fileName;
			uploadFileTos3bucket(fileName, file);
			file.delete();

			S3ObjectRequest s3ObjectRequest = new S3ObjectRequest();
			s3ObjectRequest.setBucketName(bucketName);
			s3ObjectRequest.setMerchantId(0);
			s3ObjectRequest.setObjectKey(fileName);
			s3ObjectRequest.setObjectUrl(fileUrl);
			s3ObjectRequest.setServiceName("File Upload");

			s3ObjectServices.saveS3Object(s3ObjectRequest);

			JSONObject object = new JSONObject();
			object.put("url", fileUrl);
			object.put("objectKey", fileName);

			return object.toString();

		} catch (Exception e) {

		}
		return fileUrl;
	}
	
	public String uploadFileV2ForBulk(File file, Long merchantId, String serviceName) {

		String fileUrl = "";
		try {
			String fileName = file.getName();
//			String fileName = generateFileNameForFile();
			fileUrl = endpointUrl + "/" + fileName;
			uploadExcelFileTos3bucket(fileName, file);
			file.delete();

			S3ObjectRequest S3ObjectRequest = new S3ObjectRequest();
			S3ObjectRequest.setBucketName(bucketName);
			S3ObjectRequest.setMerchantId(merchantId);
			S3ObjectRequest.setObjectKey(fileName);
			S3ObjectRequest.setObjectUrl(fileUrl);
			S3ObjectRequest.setServiceName(serviceName);

			s3ObjectServices.saveS3Object(S3ObjectRequest);

		} catch (Exception e) {
		}
		return fileUrl;
	}
	
	private void uploadExcelFileTos3bucket(String fileName, File file) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

		s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
				.withCannedAcl(CannedAccessControlList.PublicRead).withMetadata(objectMetadata));
	}
	
	

}
