#!/bin/bash
echo "########### Creating table with global secondary index ###########"
aws   --endpoint-url=http://localhost:4566 \
      dynamodb create-table \
         --table-name customers \
         --attribute-definitions \
           AttributeName=email,AttributeType=S \
        --key-schema AttributeName=email,KeyType=HASH \
        --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5

echo "########### Describing a table ###########"
aws   --endpoint-url=http://localhost:4566 \
      dynamodb describe-table --table-name customers --output table

echo "########### Inserting test data into a table ###########"
aws   --endpoint-url=http://localhost:4566 \
      dynamodb put-item --table-name customers --item "{\"id\":{\"S\":\"29ae2e26-76df-4211-a8e8-f26f11b11588\"},
                                                     \"phoneNumber\":{\"S\":\"1-962-894-4629\"},
                                                     \"fullName\":{\"S\":\"Jarrod Kub V\"},
                                                     \"email\":{\"S\":\"customer1@gmail.com\"},
                                                     \"createdAt\":{\"S\":\"2021-09-26\"}}"

echo "########### Selecting all data from a table ###########"
aws   --endpoint-url=http://localhost:4566 \
      dynamodb scan --table-name customers

