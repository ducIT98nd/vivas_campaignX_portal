package com.vivas.campaignx.common;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.PropertySource;


@PropertySource("file:../../../../../../../application.properties")
public class FTPUtils {
	private static Logger logger = LogManager.getLogger(FTPUtils.class);

	public static void uploadFile( String file, String servers, String port, String user, String pass) {
		logger.info("list server ftp: " + servers);
		String server[] = servers.split(";");
		logger.info("Info FTP server " + server.length);
		int lastPos = file.lastIndexOf("/");
		String dirPath = file.substring(15, lastPos);
		String fileName= file.substring(lastPos + 1);
		logger.info("dirPath: " + dirPath);
		logger.info("fileName: " + fileName);
		for(int i = 0; i < server.length; i++) {
			logger.info("ip " + server[i]);
			logger.info("port " + port);
			logger.info("user " + user);
			logger.info("pass " + pass);
			FTPClient ftpClient = new FTPClient();
	        try {
	 
	            ftpClient.connect(server[i], Integer.parseInt(port));
	            ftpClient.login(user, pass);
	            ftpClient.enterLocalPassiveMode();
	            
	            
	            if(makeDirectories(ftpClient, dirPath)) {
	                    logger.info("Successfully changed working directory.");
	                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	               	 
	    	            // APPROACH #1: uploads first file using an InputStream
	    	            File firstLocalFile = new File(file);
	    	 
	    	            String firstRemoteFile = fileName;
	    	            InputStream inputStream = new FileInputStream(firstLocalFile);
	    	 
	    	            logger.info("Start uploading file: " + file + " to server: " + server[i] + " file in server: " + firstRemoteFile);
	    	            boolean done = ftpClient.storeFile(firstRemoteFile, inputStream);
	    	            inputStream.close();
	    	            logger.info("result upload: " + done);
	    	            if (done) {
	    	            	 logger.info("The file is uploaded successfully.");
	    	            }
	            }
	        }catch (Exception e) {
	        	logger.info("The file is uploaded failed with error: ");
	        	logger.info(e);
			}finally {
	            try {
	                if (ftpClient.isConnected()) {
	                    ftpClient.logout();
	                    ftpClient.disconnect();
	                }
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        }
		} 
	}

	public static void uploadFileVoiceConfig( String file, String servers, String port, String user, String pass) {
		logger.info("list server ftp: " + servers);
		String server[] = servers.split(";");
		logger.info("Info FTP server " + server.length);
		int lastPos = file.lastIndexOf("/");
		String fileName= file.substring(lastPos + 1);
		logger.info("fileName: " + fileName);
		for(int i = 0; i < server.length; i++) {
			logger.info("ip " + server[i]);
			logger.info("port " + port);
			logger.info("user " + user);
			logger.info("pass " + pass);
			FTPClient ftpClient = new FTPClient();
			try {
				ftpClient.connect(server[i], Integer.parseInt(port));
				ftpClient.login(user, pass);
				ftpClient.enterLocalPassiveMode();

				logger.info("Successfully changed working directory.");
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

				File firstLocalFile = new File(file);

				logger.info("Delete file name " + fileName);
				logger.info("Delete file dir " + ftpClient.printWorkingDirectory() );
				AppUtils.deleteFtpFile(ftpClient, ftpClient.printWorkingDirectory() + "/" + fileName);
				
				InputStream inputStream = new FileInputStream(firstLocalFile);

				logger.info("Start uploading file: " + file + " to server: " + server[i] + " file in server: " + fileName);
				boolean done = ftpClient.storeFile(fileName, inputStream);
				String reply = ftpClient.getReplyString();
				inputStream.close();
				logger.info("result upload: {}, reason: {}", done, reply);
				if (done) {
					logger.info("The file is uploaded successfully.");
				}
			}catch (Exception e) {
				logger.error("The file is uploaded failed with error: ", e);
			}finally {
				try {
					if (ftpClient.isConnected()) {
						ftpClient.logout();
						ftpClient.disconnect();
					}
				} catch (IOException ex) {
					logger.error("Error close FTP: ", ex);
				}
			}
		}
	}
	
	public static boolean makeDirectories(FTPClient ftpClient, String dirPath) throws IOException {
		String[] pathElements = dirPath.split("/");
		if (pathElements != null && pathElements.length > 0) {
			for (int i = 1; i < pathElements.length; i++) {
				String singleDir = pathElements[i];
				boolean existed = ftpClient.changeWorkingDirectory(singleDir);
				if (!existed) {
					boolean created = ftpClient.makeDirectory(singleDir);
					if (created) {
						logger.info("CREATED directory: " + singleDir);
						ftpClient.changeWorkingDirectory(singleDir);
					} else {
						logger.info("COULD NOT create directory: " + singleDir);
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public static void main(String[] args) {
//		uploadFile("D:\\Work\\Project\\MMP_IVR\\test.ulaw","10.84.70.144;", "21", "manhdt", "vivas123");
		String file = "/opt/FileUpload/IVR/ConvertRecordAction/alooo.ulaw";
		int lastPos = file.lastIndexOf("/");
		String dirPath = file.substring(15);
		String fileName= file.substring(lastPos + 1);
		System.out.println(dirPath);
		System.out.println(fileName);
	}
}
