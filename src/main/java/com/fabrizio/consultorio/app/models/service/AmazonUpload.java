package com.fabrizio.consultorio.app.models.service;

import java.io.File;

import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

@Service
public class AmazonUpload {
	public void upload(String fileNameInS3, String fileNameInLocalPC) {
		try {
			// SECTION 1 OPTION 1: Create a S3 client with in-program credential
			//
			BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIA3ETWK5VY2MF6Y7MS",
					"SIZUE5DGzvRqqXzw1RKC++4vq6j30x8e63t+8KL0");
			// us-west-2 is AWS Oregon
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion("us-east-1")
					.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();

			// SECTION 1 OPTION 2: Create a S3 client with the aws credentials set in OS
			// (require config and crendentials in .aws folder.) Demonstrate at the end of
			// this video.
			//
//						AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

			// SECTION 2: Put file in S3 bucket
			//
			String bucketName = "elasticbeanstalk-us-east-1-765826100593";
			String folderName = "consultorioFotos";
//			String fileNameInS3 = "facon.jpg";
//			String fileNameInLocalPC = "facon.jpg";

			PutObjectRequest request = new PutObjectRequest(bucketName, folderName + "/" + fileNameInS3,
					new File(fileNameInLocalPC));
			s3Client.putObject(request);
			System.out.println("--Uploading file done");

			// SECTION 3: Get file from S3 bucket
			//
			@SuppressWarnings("unused")
			S3Object fullObject;
			fullObject = s3Client.getObject(new GetObjectRequest(bucketName, folderName + "/" + fileNameInS3));
			System.out.println("--Downloading file done");

		} catch (AmazonS3Exception e) {

			e.printStackTrace();
		}
	}
}
