package com.polovyi.ivan.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Event;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.polovyi.ivan.entity.CustomerEntity;
import com.polovyi.ivan.repository.CustomerRepository;
import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public record S3EventListenerService(AmazonS3 s3Client, CustomerRepository repository) {

    @SqsListener(value = "${cloud.aws.sqs.url}")
    public void s3ObjectCreationListener(S3EventNotification s3EventNotificationRecord) {
        List<S3EventNotification.S3EventNotificationRecord> records = s3EventNotificationRecord.getRecords();
        records.forEach(
                record -> {
                    log.info("Processing S3 event of type {}", record.getEventNameAsEnum());

                    if (S3Event.ObjectCreatedByPut.equals(record.getEventNameAsEnum())) {
                        String fileContent = readS3File(record.getS3().getBucket().getName(),
                                record.getS3().getObject().getKey());
                        List<String> fileRows = List.of(fileContent.split(System.lineSeparator()));
                        saveToDB(fileRows);
                    }
                });
    }

    private void saveToDB(List<String> fileRows) {
        log.info("Creating entities from the rows, and saving");
        List<CustomerEntity> customerEntities = fileRows.stream()
                .map(row -> row.split(";"))
                .map(CustomerEntity::valueOf)
                .collect(Collectors.toList());
        repository.saveAll(customerEntities);
    }

    @SneakyThrows
    public String readS3File(String bucketName, String key) {
        log.info("Reading file with a key {} from S3 bucket {}", key, bucketName);
        S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, key));

        byte[] bytes = IOUtils.toByteArray(new ByteArrayInputStream(object.getObjectContent().readAllBytes()));

        return new String(bytes, StandardCharsets.UTF_8);
    }
}
