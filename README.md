# AWS Route53 record update
Do you use **aws** (amazon web service) as DNS for your domains?

Are you looking for an easy way to update your aws route53 record with your public IP without logging into the aws management console?

This is the driver that motivated me to develop this small project as I did not find any tool out there accomplishing this task.

This is a **Java** based program to install on any machine that will detect its public IP to update a certain aws route53 record (**type A**) of your AWS account.

**As simple as that!**

## Requirements
In order to use this tool you will need the following

- Active aws account
- The hosted zone configured within aws Route53
- Valid aws credentials (**secretKey** and **accessKey**) with write access to your aws Route53 service
- Java installed on the machine where you intend to run this utility

In case the configured record name is not present within the aws hosted zone, it will be created (type A) otherwise it will be simply updated with the new detected public IP.

## Download and Install



