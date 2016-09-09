package cn.tf.mytomcat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class Response {
	
	private OutputStream  out;
	String projectName = null;
	
	
	public Response(OutputStream out){
		this.out=out;
	}
	
	
	public void redirect(String path){
		String flag=path.substring(1);	
		String url=null;
		
		if(flag.contains("/")  && flag.indexOf("/")!=flag.length()-1){
			projectName=path.substring(0,flag.indexOf("/"));
			 url=flag.substring(flag.indexOf("/"));  //请求的资源地址
			
			//查看当前项目中有没有配置web.xml文件
			List<ServletMapper>  servletMappers=WebxmlObject.webObject.get(projectName);
			if(servletMappers==null || servletMappers.size()<=0){
				//说明没有配置servlet信息
				//将当前资源路径当成静态资源返回
				String filePath=Config.getConfig().getProperty("path")+"\\"+flag.replace("/", "\\");
				
				
				if(flag.endsWith(".jpg")){
					send(filePath,"application/x-jpg");
				}else if(flag.endsWith(".jpeg") || flag.endsWith(".jpe")){
					send(filePath,"image/jpeg");
				}else if(flag.endsWith(".gif")){
					send(filePath,"image/gif");
				}else if(flag.endsWith(".css")){
					send(filePath,"text/css");
				}else if(flag.endsWith(".js")){
					send(filePath,"application/x-javascript");
				}else if(flag.endsWith(".jsp") || flag.endsWith(".htm")){
					send(filePath,"text/html;charset=utf-8");
				}else if(flag.endsWith(".swf")){
					send(filePath,"application/x-shockwave-flash");
				}else{
					sendFile(getFileToString(filePath));
				}
				
				
				
			}else{
				//说明有配置项
				boolean bl=false;
				for(ServletMapper  mapper:servletMappers){
					if(url.equals(mapper.getServletUrl())){
						bl=true;
						break;
					}
				}
				
				if(!bl){
					//没有找到就当成静态资源读取返回

				}
			}	
		}else{
			searchDefaultPage(flag);
		}
	}
	
	
	//检索默认路径并返回
	private void searchDefaultPage(String flag){
		
		//如果没有配置，则默认读取tomcat配置的默认页面，能读到则返回该页面信息
		projectName=flag.replace("/", "");
		
		Map<String,List<String>> welcomeList=WebxmlObject.welcomeList;
		if(welcomeList!=null && welcomeList.size()>0){
			
			//判断该项目有没有在web.xml中配置默认访问页面
			if(welcomeList.containsKey(projectName)){  //说明这个项目在web.xml中配置了默认页面
				List<String> list=welcomeList.get(projectName);
				
				String fileInfo=null;
				for(String str:list){
					fileInfo=this.getFileToString(Config.getConfig().getProperty("path")+"\\"+projectName+"\\"+str);
					if(fileInfo==null){
						continue;
					}else{
						break;
					}
				}
				
				if(fileInfo!=null){
					sendFile(fileInfo);
				}else{
					sendTomcatDefault();
				}
				
			}else{
				//说明没有在web.xml中配置默认页面
				sendTomcatDefault();
			}
		}else{
			//说明没有在web.xml中配置默认页面，则检索服务器中是否配置了默认页面
			sendTomcatDefault();	
		}
	}
	
	
	private void sendTomcatDefault(){
		String url=Config.getConfig().getProperty("defaultPage");
		
		//服务器中配置了多个默认首页
		if(url.contains(",")){
			String[] urls=url.split(",");
			String filePage=null;
			int i=0,len=0;
			for( i=0,len=urls.length;i<len;i++){
				filePage=getFileToString(Config.getConfig().getProperty("path")+"\\"+projectName+"\\"+urls[i]);
				if(filePage!=null){
					break;
				}
			}
			if(i>len){
				error404();
			}else{
				sendFile(filePage);
			}
			
		}else{
			//到服务器中查找该页面是否存在
			String filePage=getFileToString(Config.getConfig().getProperty("path")+"\\"+projectName+"\\"+url);
			if(filePage!=null){
				sendFile(filePage);
			}else{
				error404();
			}
		}
	}
	
	
	//判断默认首页的文件是否存在
	private String getFileToString(String path){
		File fl=new File(path);
		if(fl.exists()){
			FileInputStream  fis=null;
			StringBuffer sbf=new StringBuffer();
			int len=-1;
			byte[] bt=new byte[1024];
					
			try {
				fis=new FileInputStream(fl);
				
				while((len=fis.read(bt))!=-1){
					sbf.append(new String(bt,0,len));
				}
				return sbf.toString();
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(fis!=null){
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else {
			error404();
		}
		return null;
	}
	
	//发送页面文件,参数1：要发送的文件内容，参数2：访问路径
	private void sendFile(String str){
		//需要考虑请求的资源类型
		//String msg="HTTP/1.1 200 OK\r\nContent-Type:text/html\r\nContent-Length:"+str.getBytes().length+"\r\n\r\n"+str;
		String msg="HTTP/1.1 200 OK\r\nContent-Type:text/html;charset=utf-8\r\n\r\n"+str;
		
		try {
			out.write(msg.getBytes());
			out.flush();
			out.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void send(String path,String contentType){
		
		File fl=new File(path);
		if(fl.exists()){
			FileInputStream fis = null;
			try {
				fis=new FileInputStream(fl);
				byte[] bt=new byte[fis.available()];
				fis.read(bt);
				String msg="HTTP/1.1 200 OK\r\nContent-Type:"+contentType+"\r\n\r\n";
				out.write(msg.getBytes());
				out.write(bt);
				out.flush();
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(fis!=null){
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			error404();
		}
	}
	
	private void error404(){
		try {
			String err="<h1>File Not Found</h1>";
			String message="HTTP/1.1 404 File Not Found\r\nContent-Type:text/html;charset=utf-8\r\nContent-Length:"+err.length()+"\r\n\r\n"+err;
			out.write(message.getBytes());
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
