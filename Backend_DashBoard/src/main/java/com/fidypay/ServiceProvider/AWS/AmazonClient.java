package com.fidypay.ServiceProvider.AWS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fidypay.entity.CredDetails;
import com.fidypay.repo.CredDetailsRepository;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;

@Service
public class AmazonClient {

	@Autowired
	private CredDetailsRepository credDetailsRepository;

	private AmazonS3 s3client;

	@Value("${amazonProperties.endpointUrl}")
	private String endpointUrl;
	@Value("${s3.bucket}")
	private String bucketName;

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

	private String generateFileName(MultipartFile multiPart) {
		String profilePictureAwsKey = ImageUtils.getUniqueId();
		return profilePictureAwsKey.replace(" ", "_");
	}

	private void uploadFileTos3bucket(String fileName, File file) {
		s3client.putObject(
				new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.Private));
	}

	public String uploadFile(MultipartFile multipartFile) {

		String fileUrl = "";
		String fileName = "";
		try {
			File file = convertMultiPartToFile(multipartFile);
			fileName = generateFileName(multipartFile);
			fileUrl = endpointUrl + "/" + fileName;
			uploadFileTos3bucket(fileName, file);
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}

	public String deleteFileFromS3Bucket(String key) {
		s3client.deleteObject(new DeleteObjectRequest(bucketName, key));
		return "Successfully deleted";
	}

	public List<AmazonObjects> listFiles() {

		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName);

		List<AmazonObjects> keys = new ArrayList<AmazonObjects>();

		ObjectListing objects = s3client.listObjects(listObjectsRequest);

		while (true) {
			List<S3ObjectSummary> objectSummaries = objects.getObjectSummaries();
			if (objectSummaries.size() < 1) {
				break;
			}
			String key = "";
			String objectUrl = "";
			for (S3ObjectSummary item : objectSummaries) {
				AmazonObjects amazonObjects = new AmazonObjects();
				if (!item.getKey().endsWith("/"))
					key = item.getKey();

				URL s3Url = s3client.getUrl(bucketName, key);
				System.out.println("s3Url " + s3Url);
				objectUrl = s3Url.toString();
				amazonObjects.setKey(key);
				amazonObjects.setObjectURL(objectUrl);
				keys.add(amazonObjects);

			}

			objects = s3client.listNextBatchOfObjects(objects);
		}

		return keys;
	}

	public Map<String, Object> getObject(String key) {
		Map<String, Object> map = new HashedMap<>();

		GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();

		URL s3Url = s3client.getUrl(bucketName, key);
		String objectUrl = s3Url.toString();
		String keyRes = getObjectRequest.key();

		map.put("key", keyRes);
		map.put("objectUrl", objectUrl);
		return map;
	}
}
