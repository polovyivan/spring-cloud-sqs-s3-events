package com.polovyi.ivan.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.polovyi.ivan.entity.CustomerEntity;
import com.polovyi.ivan.repository.CustomerRepository;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
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

    @SqsListener("${cloud.aws.sqs.url}")
    public void s3ObjectCreationListener(S3EventNotification s3EventNotificationRecord) {
        List<S3EventNotification.S3EventNotificationRecord> records = s3EventNotificationRecord.getRecords();
        records.forEach(
                record -> {
                    String fileContent = readS3File(record.getS3().getBucket().getName(),
                            record.getS3().getObject().getKey());
                    List<String> split = List.of(fileContent.split(System.lineSeparator()));

                    List<CustomerEntity> customerEntities = split.stream().map(row -> row.split(";"))
                            .map(CustomerEntity::valueOf)
                            .collect(Collectors.toList());
                    repository.saveAll(customerEntities);
                });

    }

    @SneakyThrows
    public String readS3File(String bucketName, String key) {

        S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, key));

        byte[] bytes = IOUtils.toByteArray(new ByteArrayInputStream(object.getObjectContent().readAllBytes()));

        String s = new String(bytes, StandardCharsets.UTF_8);
        System.out.println("s = " + s);
        return s;
    }

}
