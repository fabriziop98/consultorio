package com.fabrizio.consultorio.app.models.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

@Service
public class AmazonUpload {
	
	private Logger log = LoggerFactory.getLogger(AmazonUpload.class);

	
	public void upload(String foto) throws AmazonClientException {
		
		MultipartFile multipartFile = null;
		log.info("====================AMAZON UPLOAD FOTO=====================");
		
		if (foto != null && !foto.equals("")) {
			File file = new File(UUID.randomUUID().toString());
			String encodedImg = foto.split(",")[1];
			byte[] decodedBytes = Base64.getDecoder().decode(encodedImg.getBytes(StandardCharsets.UTF_8));
			try {
				FileUtils.writeByteArrayToFile(file, decodedBytes);
				FileInputStream input = new FileInputStream(file);
			    multipartFile = new MockMultipartFile("file", file.getName() + ".png", null ,input);
				Path path = Paths.get(file.getAbsolutePath());
				Files.deleteIfExists(path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIA3ETWK5VY2MF6Y7MS",
				"SIZUE5DGzvRqqXzw1RKC++4vq6j30x8e63t+8KL0");
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
//				.withRegion(Regions.US_EAST_1).build();
				
		String bucketName = "elasticbeanstalk-us-east-1-765826100593";
		String folderName = "consultorioFotos";
//		InputStream is;
		try {
//			is = IOUtils.toInputStream(foto,StandardCharsets.UTF_8.name());
			
			// guarda en s3 con acceso público
			 
//			s3Client.putObject(new PutObjectRequest(bucketName, foto, is, new ObjectMetadata())
//					.withCannedAcl(CannedAccessControlList.PublicRead));
			
			ObjectMetadata metadata = new ObjectMetadata();
	        metadata.setContentLength(multipartFile.getSize());
			s3Client.putObject(new PutObjectRequest(bucketName+"/"+folderName, multipartFile.getName(), multipartFile.getInputStream(), metadata));
			// obitene la referencia al objeto de la imagen
			S3Object s3object = s3Client.getObject(new GetObjectRequest(folderName+"/"+bucketName, foto));
			System.out.println(s3object.getObjectContent().getHttpRequest().getURI().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
public void uploadArchivo(MultipartFile archivo) throws AmazonClientException {
		
		log.info("====================AMAZON UPLOAD ARCHIVO=====================");
		
		BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIA3ETWK5VY2MF6Y7MS",
				"SIZUE5DGzvRqqXzw1RKC++4vq6j30x8e63t+8KL0");
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
//				.withRegion(Regions.US_EAST_1).build();
				
		String bucketName = "elasticbeanstalk-us-east-1-765826100593";
		String folderName = "consultorioInformes";
//		InputStream is;
		try {
//			is = IOUtils.toInputStream(foto,StandardCharsets.UTF_8.name());
			
			// guarda en s3 con acceso público
			 
//			s3Client.putObject(new PutObjectRequest(bucketName, foto, is, new ObjectMetadata())
//					.withCannedAcl(CannedAccessControlList.PublicRead));
			
			ObjectMetadata metadata = new ObjectMetadata();
	        metadata.setContentLength(archivo.getSize());
			s3Client.putObject(new PutObjectRequest(bucketName+"/"+folderName, archivo.getName(), archivo.getInputStream(), metadata));
			// obitene la referencia al objeto de la imagen
			S3Object s3object = s3Client.getObject(new GetObjectRequest(bucketName+"/"+folderName, archivo.getName()));
			System.out.println(s3object.getObjectContent().getHttpRequest().getURI().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void getArchivo(String fileName) {
		BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIA3ETWK5VY2MF6Y7MS",
		"SIZUE5DGzvRqqXzw1RKC++4vq6j30x8e63t+8KL0");
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1)
		.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
		String bucketName = "elasticbeanstalk-us-east-1-765826100593";
		String folderName = "consultorioInformes";
		@SuppressWarnings("unused")
		S3Object fullObject = s3Client.getObject(new GetObjectRequest(bucketName+"/"+folderName, fileName));
		
	}
}
	
	
	
	
	
	
	
//	FROM LOCAL
//	try {
//		// SECTION 1 OPTION 1: Create a S3 client with in-program credential
//		//
//		BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIA3ETWK5VY2MF6Y7MS",
//				"SIZUE5DGzvRqqXzw1RKC++4vq6j30x8e63t+8KL0");
//		// us-west-2 is AWS Oregon
//		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion("us-east-1")
//				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
//
//		// SECTION 1 OPTION 2: Create a S3 client with the aws credentials set in OS
//		// (require config and crendentials in .aws folder.) Demonstrate at the end of
//		// this video.
//		//
////					AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
//
//		// SECTION 2: Put file in S3 bucket
//		//
//		String bucketName = "elasticbeanstalk-us-east-1-765826100593";
//		String folderName = "consultorioFotos";
////		String fileNameInS3 = "facon.jpg";
////		String fileNameInLocalPC = "facon.jpg";
//
//		PutObjectRequest request = new PutObjectRequest(bucketName, folderName + "/" + fileNameInS3,
//				new File(fileNameInLocalPC));
//		s3Client.putObject(request);
//		System.out.println("--Uploading file done");
//
//		// SECTION 3: Get file from S3 bucket
//		//
//		@SuppressWarnings("unused")
//		S3Object fullObject;
//		fullObject = s3Client.getObject(new GetObjectRequest(bucketName, folderName + "/" + fileNameInS3));
//		System.out.println("--Downloading file done");
//
//	} catch (AmazonS3Exception e) {
//
//		e.printStackTrace();
//	}
