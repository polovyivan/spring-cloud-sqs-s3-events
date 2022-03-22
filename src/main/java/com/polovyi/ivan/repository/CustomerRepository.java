package com.polovyi.ivan.repository;

import com.polovyi.ivan.entity.CustomerEntity;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface CustomerRepository extends CrudRepository<CustomerEntity, String> {

}
