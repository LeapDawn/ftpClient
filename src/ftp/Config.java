package ftp;

public class Config {

	private String ftpHost; // ftpIP
	private int ftpPort; // ftp端口
	private String ftpUser; // ftp用户名
	private String ftpPassword; // ftp密码
	private String ftpPath; // ftp目录路径

	public Config() {
		init();
	}

	public String getFtpHost() {
		return ftpHost;
	}

	public void setFtpHost(String ftpHost) {
		if (ftpHost != null && !ftpHost.trim().equals("")) {
			this.ftpHost = ftpHost.trim();
		}
	}

	public int getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(int ftpPort) {
		if (ftpPort > 0) {
			this.ftpPort = ftpPort;
		}
	}

	public String getFtpUser() {
		return ftpUser;
	}

	public void setFtpUser(String ftpUser) {
		if (ftpUser != null && !ftpUser.trim().equals("")) {
			this.ftpUser = ftpUser.trim();
		}
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		if (ftpPassword != null && !ftpPassword.trim().equals("")) {
			this.ftpPassword = ftpPassword.trim();
		}
	}

	public String getFtpPath() {
		return ftpPath;
	}

	public void setFtpPath(String ftpPath) {
		this.ftpPath = ftpPath.endsWith("/") ? ftpPath : ftpPath + "/";
	}

	public void init() {
		this.ftpHost = "127.0.0.1";
		this.ftpPort = 21;
		this.ftpUser = "janvier";
		this.ftpPassword = "matt10801";
		this.ftpPath = "/";
	}
}
