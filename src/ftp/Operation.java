package ftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TimeZone;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class Operation {

	private Config config;
	private FTPClient ftpClient;

	public Operation() {
		super();
		config = new Config();
		ftpClient = new FTPClient();
	}

	public Operation(Config config) {
		super();
		this.config = config != null ? config : new Config();
		this.ftpClient = new FTPClient();
	}

	/**
	 * 登录FTP服务器
	 * 
	 * @return
	 * @throws FTPException
	 */
	public boolean connect() throws FTPException {
		FTPClientConfig ftpConfig = new FTPClientConfig();
		ftpConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
		ftpClient.setControlEncoding("GBK");
		ftpClient.configure(ftpConfig);
		try {
			// 尝试连接FTP
			if (config.getFtpPort() > 0) {
				ftpClient.connect(config.getFtpHost(), config.getFtpPort());
			} else {
				ftpClient.connect(config.getFtpHost());
			}

			int reply = this.ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				this.ftpClient.disconnect(); // 登录FTP服务失败！
				throw new FTPException("连接FTP服务失败,检查IP和端口是否正确");
			}

			if (!ftpClient.login(config.getFtpUser(), config.getFtpPassword())) {
				throw new FTPException("登录FTP服务失败,检查用户名和密码是否正确");
			}

			this.ftpClient.enterLocalPassiveMode();  //被动模式
//			this.ftpClient.enterLocalActiveMode();   //主动模式
			this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

			this.ftpClient.setBufferSize(1024 * 2);
			this.ftpClient.setDataTimeout(30 * 1000);
			return true;
		} catch (FTPException e) {
			throw e;
		} catch (Exception e) {
			throw new FTPException("登录FTP服务失败");
		}
	}

	/**
	 * 关闭FTP服务连接
	 * 
	 * @throws FTPException
	 */
	public void disconnect() throws FTPException {
		if (ftpClient.isConnected()) {
			try {
				this.ftpClient.logout();
			} catch (IOException e) {
				throw new FTPException("退出FTP服务异常");
			} finally {
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					throw new FTPException("关闭FTP服务连接异常");
				}
			}
		}
	}

	/**
	 * 上传文件
	 * 
	 * @param localFile
	 * @param romotUpLoadePath
	 * @throws FTPException
	 */
	public void uploadFile(String localFile, String romotUpLoadePath) throws FTPException {
		InputStream input = null;
		InputStream fileInput = null;
		try {
			changeWorkingDirectory(romotUpLoadePath);
			File file = new File(localFile);
			fileInput = new FileInputStream(file);
			input = new BufferedInputStream(fileInput);
			ftpClient.storeFile(file.getName(), input);
		} catch (FileNotFoundException e) {
			throw new FTPException("该文件不存在");
		} catch (IOException e) {
			throw new FTPException("上传文件失败");
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if (fileInput != null) {
					fileInput.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 下载FTP文件
	 * 
	 * @param remoteDownLoadPath
	 * @param remoteFileName
	 * @param localDires
	 * @throws FTPException
	 */
	public void downloadFile(String remoteDownLoadPath, String remoteFileName, String localDires) throws FTPException {
		String localFilePath = localDires + remoteFileName;
		OutputStream out = null;
		OutputStream fileOut = null;
		try {
			ftpClient.changeWorkingDirectory(remoteDownLoadPath);
			fileOut = new FileOutputStream(localFilePath);
			out = new BufferedOutputStream(fileOut);
			ftpClient.retrieveFile(remoteFileName, out);
		} catch (IOException e) {
			throw new FTPException("下载文件失败");
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (fileOut != null) {
					fileOut.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 删除ftp文件
	 * 
	 * @param remoteDirectoryPath
	 * @param remoteFileName
	 * @throws FTPException
	 */
	public void deleteFile(String remoteDirectoryPath, String remoteFileName) throws FTPException {
		try {
			ftpClient.changeWorkingDirectory(remoteDirectoryPath);
			ftpClient.deleteFile(remoteFileName);
		} catch (IOException e) {
			throw new FTPException("删除文件失败");
		}
	}

	/**
	 * 上传文件夹
	 * @param localDirectory
	 * @param remoteDirectoryPath
	 * @throws FTPException
	 */
	public void uploadDir(String localDirectory, String remoteDirectoryPath) throws FTPException {
		File src = new File(localDirectory);
		remoteDirectoryPath = remoteDirectoryPath + src.getName() + "/";
		
		File[] listFiles = src.listFiles();
		if (listFiles == null) {
			if (!src.exists()) {
				throw new FTPException("上传目录不存在");
			} else if (!src.isDirectory()) {
				throw new FTPException(localDirectory + "不是一个目录");
			} else {
				throw new FTPException("本地目录错误");
			}
		}
		
		try {
			ftpClient.makeDirectory(remoteDirectoryPath);
		} catch (IOException e) {
			throw new FTPException("创建目录失败");
		}
		
		try {
			for (File file : listFiles) {
				if (!file.isDirectory()) {
					uploadFile(file.getPath(), remoteDirectoryPath);
				} else {
					uploadDir(file.getPath(), remoteDirectoryPath);
				}
			}
		} catch (FTPException e) {
			throw e;
		}
	}

	/**
	 * 下载文件夹
	 * @param localDirectoryPath
	 * @param remoteDirectory
	 * @throws FTPException
	 */
	public void downloadDir(String localDirectoryPath,String remoteDirectory) throws FTPException{
		 String fileName = new File(remoteDirectory).getName();  
		 localDirectoryPath = localDirectoryPath + fileName + "//"; 
		 new File(localDirectoryPath).mkdirs();
		 try {
			FTPFile[] listFile = this.listFile(remoteDirectory);
			 for (FTPFile ftpFile : listFile) {
				if (!ftpFile.isDirectory()) {
					downloadFile(remoteDirectory, ftpFile.getName(), localDirectoryPath);
				} else {
					downloadDir(localDirectoryPath, remoteDirectory + "/" + ftpFile.getName());
				}
			}
		} catch (FTPException e) {
			throw e;
		} catch (Exception e) {
			throw new FTPException("下载文件夹失败");
		}
	}
	
	/**
	 * 删除文件夹
	 * @param remoteDirectoryPath
	 * @throws FTPException
	 */
	public void removeDir(String remoteDirectoryPath) throws FTPException {
		try {
			FTPFile[] listFile = this.listFile(remoteDirectoryPath);
			for (FTPFile ftpFile : listFile) {
				if (!ftpFile.isDirectory()) {
					deleteFile(remoteDirectoryPath, ftpFile.getName());
				} else {
					removeDir(remoteDirectoryPath + ftpFile.getName() + "/");
				}
			}
			ftpClient.removeDirectory(remoteDirectoryPath);
		} catch (FTPException e) {
			throw e;
		} catch (IOException e) {
			throw new FTPException("删除目录失败");
		}
	}
	
	/**
	 * 创建目录
	 * @param remoteDirectoryPath
	 * @throws FTPException
	 */
	public void mkdir(String remoteDirectoryPath) throws FTPException {
		try {
			ftpClient.makeDirectory(remoteDirectoryPath);
		} catch (IOException e) {
			throw new FTPException("创建目录失败");
		}
	}

	/**
	 * 获取目录下的文件列表
	 * @param remoteDirectoryPath
	 * @return
	 * @throws FTPException
	 */
	public FTPFile[] listFile(String remoteDirectoryPath) throws FTPException {
		try {
			ftpClient.setControlEncoding("UTF-8");
			ftpClient.enterLocalPassiveMode();
			return ftpClient.listFiles(remoteDirectoryPath);
		} catch (IOException e) {
			throw new FTPException("获取目录内容失败");
		}
	}
	
	private void changeWorkingDirectory(String romotUpLoadePath) throws FTPException {
		try {
			boolean success = ftpClient.changeWorkingDirectory(romotUpLoadePath);
			if (!success) {
				throw new FTPException(romotUpLoadePath + "是一个错误的路径");
			}
		} catch (IOException e) {
			throw new FTPException("操作失败");
		}
	}
	
	public void setConfig(Config config) {
		this.config = config;
	}

	public Config getConfig() {
		return config;
	}

	public FTPClient getFtpClient() {
		return ftpClient;
	}

	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}
}
