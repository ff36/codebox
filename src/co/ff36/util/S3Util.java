package co.ff36.util;

import co.ff36.archive.Archive;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Amazon S3 utility class to upload list and download archives
 * Created by tarka on 28/03/2016.
 */
public class S3Util {

    private BasicAWSCredentials awsCreds;
    private static String bucketName;

    /**
     * Construct a new S3Util object to perform AWS S3 based functions.
     * @throws IOException
     */
    public S3Util() throws IOException {
        String accessKey = null, secretKey = null;
        File file = new File(System.getProperty("user.home") + "/Documents/codebox/settings");
        List<String> contents = FileUtils.readLines(file);
        for (String line : contents) {
            String[] split = line.split("=");
            if (split[0].equals("aws_bucket")) bucketName = split[1];
            if (split[0].equals("aws_key")) accessKey = split[1];
            if (split[0].equals("aws_secret")) secretKey = split[1];
        }
        assert accessKey != null;
        assert secretKey != null;
        awsCreds = new BasicAWSCredentials(accessKey, secretKey);
    }

    /**
     * Upload an archive
     * @param uploadFileName The file to upload
     * @param keyName The name of the file to save it to S3
     *
     * TODO This needs to be multi-threaded and needs progress bar
     */
    public void Upload(String uploadFileName, String keyName) {

        AmazonS3 s3client = new AmazonS3Client(awsCreds);
        try {
            System.out.println("Uploading a new object to S3 from a file\n");
            File file = new File(uploadFileName);
            s3client.putObject(new PutObjectRequest(bucketName, keyName, file));

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

    /**
     * Read a collection of objets from S3
     * @return A list of objects in S3 bucket
     */
    public ObservableList<Archive> list() {
        ObservableList<Archive> result = FXCollections.observableArrayList();
        AmazonS3 s3client = new AmazonS3Client(awsCreds);
        try {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName);
            ObjectListing objectListing;
            do {
                objectListing = s3client.listObjects(listObjectsRequest);
                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                    result.add(new Archive(objectSummary.getKey(), objectSummary.getSize()));
                }
                listObjectsRequest.setMarker(objectListing.getNextMarker());
            } while (objectListing.isTruncated());
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, " +
                    "which means your request made it " +
                    "to Amazon S3, but was rejected with an error response " +
                    "for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, " +
                    "which means the client encountered " +
                    "an internal error while trying to communicate" +
                    " with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Download an object form S3
     * @param key The key name of the file to download.
     */
    public void download(String key) {
        AmazonS3 s3Client = new AmazonS3Client(awsCreds);
        try {
            S3Object s3object = s3Client.getObject(new GetObjectRequest(bucketName, key));
            S3ObjectInputStream inputStream = s3object.getObjectContent();

            // TODO this doesn't work from French OS!
            String home = System.getProperty("user.home");
            File file = new File(home + "/Downloads/" + key);

            FileUtils.copyInputStreamToFile(inputStream, file);

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which" +
                    " means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means"+
                    " the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
