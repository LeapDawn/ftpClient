package ftp;

import org.apache.commons.net.ftp.FTPFile;

public class Controller {

	private Config config;
	private Operation ftpClient;
	
	public Controller() throws FTPException {
		super();
		this.config = new Config();
		this.ftpClient = new Operation(this.config);
		this.ftpClient.connect();
	}

	public Controller(Config config) throws FTPException {
		super();
		if (config != null) {
			this.config = config;
		} else {
			this.config = new Config();
		}
		this.ftpClient = new Operation(this.config);
		this.ftpClient.connect();
	}

	/**
	 * 执行命令
	 * @throws FTPException 
	 */
	public void execute(String inputStr) throws FTPException {
		Command command = new Command(inputStr, config.getFtpPath());
		String commandStr = command.getCommand();
		String remotePath = command.getArg1();
		String remoteDirectoryPath = null;
		String remoteFileName = null;
		int lastSpilt = 0;
		
		switch (commandStr) {
		case "get":
			if (remotePath.endsWith("/")) {
				throw new FTPException(remotePath + "不是一个文件");
			}
			lastSpilt = remotePath.lastIndexOf("/");
			remoteDirectoryPath = remotePath.substring(0, lastSpilt + 1);
			remoteFileName = remotePath.substring(lastSpilt + 1);
			ftpClient.downloadFile(remoteDirectoryPath, remoteFileName, command.getArg2());
			break;
		case "getdir":
			ftpClient.downloadDir(command.getArg2(), remotePath);
			break;
		case "upload":
			if (!remotePath.endsWith("/")) {
				throw new FTPException(remotePath + "不是一个目录");
			}
			ftpClient.uploadFile(command.getArg2(), remotePath);
			break;
		case "uploaddir":
			if (!remotePath.endsWith("/")) {
				throw new FTPException(remotePath + "不是一个目录");
			}
			ftpClient.uploadDir(command.getArg2(), remotePath);
			break;
		case "cd":
			if (remotePath.equals("/")) {
				config.setFtpPath("/");
				command.setPwd("/");
			} else {
				if (remotePath.endsWith("/")) {
					remotePath = remotePath.substring(0, remotePath.length() - 1);
				}
				lastSpilt = remotePath.lastIndexOf("/");
				remoteDirectoryPath = remotePath.substring(0, lastSpilt + 1);
				remoteFileName = remotePath.substring(lastSpilt + 1);
				FTPFile[] filelist = ftpClient.listFile(remoteDirectoryPath);
				if (filelist == null) {
					throw new FTPException(remotePath + "不是一个正确的路径");
				}
				boolean right = false;
				for (FTPFile ftpFile : filelist) {
//					System.out.println("----" + ftpFile.getName() + "--------" + remoteFileName);
					if (ftpFile.isDirectory() && ftpFile.getName().equals(remoteFileName)) {
						config.setFtpPath(remotePath);
						command.setPwd(remotePath);
						right = true;
						break;
					}
				}
				
				if (!right) {
					throw new FTPException(remotePath + "不是一个正确的路径");
				}
			}
			break;
		case "mkdir":
			ftpClient.mkdir(remotePath);
			break;
		case "rmdir":
			ftpClient.removeDir(remotePath);
			break;
		case "rm":
			if (remotePath.endsWith("/")) {
				throw new FTPException(remotePath + "不是一个文件");
			}
			lastSpilt = remotePath.lastIndexOf("/");
			remoteDirectoryPath = remotePath.substring(0, lastSpilt + 1);
			remoteFileName = remotePath.substring(lastSpilt + 1);
			ftpClient.deleteFile(remoteDirectoryPath, remoteFileName);
			break;
		case "config":
			System.out.println("当前FTP服务连接地址:" + config.getFtpHost());
			System.out.println("当前FTP服务连接端口:" + config.getFtpPort());
			System.out.println("当前FTP服务连接用户:" + config.getFtpUser());
			break;
		case "ls":
			if (!remotePath.endsWith("/")) {
				throw new FTPException(remotePath + "不是一个目录");
			}
			FTPFile[] listFile = ftpClient.listFile(remotePath);
			if (listFile == null) {
				throw new FTPException(remotePath + "不是一个正确的路径");
			}
			for (FTPFile ftpFile : listFile) {
				if (ftpFile.isDirectory()) {
					System.out.println(ftpFile.getName() + "/");
				} else {
					System.out.println(ftpFile.getName());
				}
			}
			break;
		default:
			System.out.println(commandStr + "不是一个有效的命令");
			break;
		}
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public Operation getFtpClient() {
		return ftpClient;
	}

	public void setFtpClient(Operation ftpClient) {
		this.ftpClient = ftpClient;
	}
}
