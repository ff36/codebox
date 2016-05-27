package co.ff36.util;

import co.ff36.pojo.Archive;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

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
        String accessKey, secretKey;

        Map<String, String> settings = new SettingUtil().load();
        accessKey = settings.get(SettingUtil.AWS_PUBLIC_KEY);
        secretKey = settings.get(SettingUtil.AWS_PRIVATE_KEY);
        bucketName = settings.get(SettingUtil.AWS_BUCKET_KEY);

        assert accessKey != null;
        assert secretKey != null;
        awsCreds = new BasicAWSCredentials(accessKey, secretKey);
    }

    /**
     * Upload an archive
     * @param uploadFileName The file to upload
     * @param keyName The name of the file to save it to S3
     *
     */
    @SuppressWarnings("deprecation")
    public Upload Upload(String uploadFileName, String keyName) {

        AmazonS3 s3Client = new AmazonS3Client(awsCreds);
        s3Client.setEndpoint("https://s3.eu-central-1.amazonaws.com");
        TransferManager tm = new TransferManager(s3Client);
        try {
            File file = new File(uploadFileName);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyName, file);
            putObjectRequest.setProgressListener(event -> {});

            return tm.upload(putObjectRequest);

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
        return null;
    }

    /**
     * Read a collection of objets from S3
     * @return A list of objects in S3 bucket
     */
    public ObservableList<Archive> list() {
        ObservableList<Archive> result = FXCollections.observableArrayList();
        AmazonS3 s3Client = new AmazonS3Client(awsCreds);
        s3Client.setEndpoint("https://s3.eu-central-1.amazonaws.com");
        try {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName);
            ObjectListing objectListing;
            do {
                objectListing = s3Client.listObjects(listObjectsRequest);
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
    @SuppressWarnings("deprecation")
    public Download download(String key) {
        AmazonS3 s3Client = new AmazonS3Client(awsCreds);
        s3Client.setEndpoint("https://s3.eu-central-1.amazonaws.com");
        TransferManager tm = new TransferManager(s3Client);
        try {

            Map<String, String> settings = new SettingUtil().load();
            String download = settings.get(SettingUtil.DOWNLOAD_FILE_KEY);
            File file = new File(download + "/" + key);

            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            getObjectRequest.setProgressListener(event -> {});

            return tm.download(getObjectRequest, file);

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
        }
        return null;
    }

}
