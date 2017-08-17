package ftp;

public class Command {

	private String command;    // 命令
	private String arg1;       // 参数1
	private String arg2;       // 参数2
	private String pwd;        // 当前工作目录
	
	public Command() {
		super();
	}
	
	public Command(String inputStr, String pwd) {
		if (inputStr != null && !inputStr.trim().equals("")) {
			String[] split = inputStr.split(" ");
			String[] tmp = new String[]{"","",""};
			int tindex = 0;
			for (int i = 0; i < split.length && tindex < tmp.length; i++) {
				if (!"".equals(split[i])) {
					tmp[tindex] = split[i];
					tindex++;
				}
			}
			this.command = tmp[0];
			this.arg1 = tmp[1];
			this.arg2 = tmp[2];
		}
		String temp = pwd != null ? pwd.trim() : "/";
		this.pwd = temp.endsWith("/") ? temp :temp + "/";
 	}
	
	public String getCommand() {
		return command != null ? command.trim() : "";
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getArg1() {
		if (arg1 == null) {
			return "";
		} else if (arg1.startsWith("/")) {
			return arg1.trim();
		} else {
			return pwd + arg1.trim(); 
		}
	}

	public void setArg1(String arg1) {
		this.arg1 = arg1;
	}

	public String getArg2() {
		return arg2 != null ? arg2.trim() : "";
	}

	public void setArg2(String arg2) {
		this.arg2 = arg2;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		String temp = pwd != null ? pwd.trim() : "/";
		this.pwd = temp.endsWith("/") ? temp :temp + "/";
	}
}
