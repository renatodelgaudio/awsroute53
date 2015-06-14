# AWS Route53 record update
Do you use **aws** (amazon web service) as DNS for your domains?

Are you looking for an easy way to update your aws route53 record with your public IP without logging into the aws management console?

This is the driver that motivated me to develop this small project as I did not find any tool out there accomplishing this task.

This is a **Java** based program to install on any machine that will detect its public IP to update a certain aws route53 record (**type A**) of your AWS account.

**As simple as that!**

E-mail notification feature is available as well so that you'll get emails upon the events that matters (e.g. DNS update, general software errors and so on)

## Requirements
In order to use this tool you will need the following

- Active aws account
- The hosted zone configured within aws Route53
- Valid aws credentials (**secretKey** and **accessKey**) with write access to your aws Route53 service
- Java installed on the machine where you intend to run this utility

In case the configured record name is not present within the aws hosted zone, it will be created (type A) otherwise it will be simply updated with the new detected public IP.

## Download and Install
Download the latest release from [Releases repository](https://github.com/renatodelgaudio/renatodelgaudio.github.io/tree/master/IpUpdaterReleases "Releases repository")

At the moment the following platform packages are available:
- **Windows** (file name IpUpdater-XXX-win-package)
- **Linux/Mac** (file name IpUpdater-XXX-linux-mac-package)

1. Unzip the package in any directory
2. Locate the file named **aws.properties** and provide the correct values
3. Run the launcher (e.g. run.sh or rub.bat)

More details on installation steps are available on the wiki
[Linux manual installation steps](https://github.com/renatodelgaudio/awsroute53/wiki/Linux-manual-installation-steps "Linux manual installation steps")

## Reports
The releases reports are viewable from http://renatodelgaudio.github.io/IpUpdaterReleases/versionxxx/reports/

e.g. http://renatodelgaudio.github.io/IpUpdaterReleases/1.0.3/reports/


Feel free to create a Pull request and I will be happy to integrate it.

Renato Del Gaudio
