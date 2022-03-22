package com.polovyi.ivan.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.polovyi.ivan.entity.converter.LocalDateTimeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "customers")
public class CustomerEntity {

    @DynamoDBHashKey(attributeName = "email")
    private String email;

    private String id;

    private String fullName;

    private String phoneNumber;

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    private LocalDate createdAt;

    public static CustomerEntity valueOf(String[] customerFields) {
        return builder()
                .id(UUID.randomUUID().toString())
                .fullName(customerFields[0])
                .phoneNumber(customerFields[1])
                .email(customerFields[2])
                .createdAt(LocalDate.now())
                .build();
    }
}