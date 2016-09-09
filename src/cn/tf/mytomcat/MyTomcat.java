package cn.tf.mytomcat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyTomcat {
	public static void main(String[] args) throws IOException {
		
		ServerSocket  ssk=new ServerSocket(8088);
		System.out.println("服务器已启动");
		Socket sk=null;
		
		try {
			new WebxmlObject();
			while(true){
				sk=ssk.accept();
				System.out.println(sk.getRemoteSocketAddress()+"连接中");
				new Thread(new HttpSession(sk)).start();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
