#!/bin/bash
echo "########### Setting SQS as env variables ###########"
export UPLOAD_FILE_EVENT_SQS=upload-file-event-sqs

echo "########### Creating upload file event SQS ###########"
aws --endpoint-url=http://localstack:4566 sqs create-queue --queue-name $UPLOAD_FILE_EVENT_SQS

echo "########### ARN for upload file event SQS ###########"
UPLOAD_FILE_EVENT_SQS_ARN=$(aws --endpoint-url=http://localstack:4566 sqs get-queue-attributes\
                  --attribute-name QueueArn --queue-url=http://localhost:4566/000000000000/"$UPLOAD_FILE_EVENT_SQS"\
                  |  sed 's/"QueueArn"/\n"QueueArn"/g' | grep '"QueueArn"' | awk -F '"QueueArn":' '{print $2}' | tr -d '"' | xargs)

echo "########### Listing queues ###########"
aws --endpoint-url=http://localhost:4566 sqs list-queues
