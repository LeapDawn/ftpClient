package ftp;

import java.util.Scanner;

public class Client {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String inputStr = null;
		Config config = new Client().config(scanner);
		Controller controller = null;
		if (config != null) {
			try {
				controller = new Controller(config);
			} catch (FTPException e) {
				e.printStackTrace();
			}

			while (controller != null) {
				System.out.print(config.getFtpPath() + "#");
				inputStr = scanner.nextLine();
				try {
					if (inputStr == null || inputStr.trim().equals("")) {
						continue;
					} else if (inputStr.trim().equals("exit")) {
						controller.getFtpClient().disconnect();
						config = new Client().config(scanner);
						if (config == null) {
							break;
						} else {
							controller = new Controller(config);
						}
					} else {
						controller.execute(inputStr);
					}
				} catch (FTPException e) {
					e.printStackTrace();
					System.out.println(e.getMessage());
				}
			}
		}
		scanner.close();
	}

	/**
	 * 填写ftp信息
	 * 
	 * @param scanner
	 * @return
	 */
	public Config config(Scanner scanner) {
		Config config = new Config();
		boolean finish = false;
		String inputStr = null;
		while (!finish) {
			System.out.print("输入ftp服务器IP:");
			inputStr = scanner.nextLine();
			config.setFtpHost(inputStr);
			System.out.print("输入ftp服务器端口:");
			inputStr = scanner.nextLine();
			try {
				config.setFtpPort(inputStr.trim().equals("") ? 0 : Integer.valueOf(inputStr));
			} catch (NumberFormatException e) {
				System.out.println("端口号错误，重新输入ftp配置信息");
				continue;
			}
			System.out.print("输入用户名:");
			inputStr = scanner.nextLine();
			config.setFtpUser(inputStr);
			System.out.print("输入密码:");
			inputStr = scanner.nextLine();
			config.setFtpPassword(inputStr);
			System.out.println("确认 ? Y/N");
			inputStr = scanner.nextLine();
			if (inputStr.trim().toUpperCase().equals("Y")) {
				finish = true;
			} else {
				config.init();
			}
		}
		return config;
	}
}
