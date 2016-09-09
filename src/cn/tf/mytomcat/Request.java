package cn.tf.mytomcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Request {
	
	private String url;  //请求的资源地址
	private String method;  //请求方式
	private String protocolVersion;   //请求的协议版本
	//请求的参数列表
	private Map<String,String>  parameter=new HashMap<String,String>();
	private InputStream is;
	
	public Request(InputStream is){
		this.is=is;
		try {
			parse();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void parse() throws IOException{
		BufferedReader  br=new BufferedReader(new InputStreamReader(is));
		//一行一行的读
		StringBuffer sbf=new StringBuffer();   
		String line;
		//先取出第一行GET
		//while((line=br.readLine())!=null){
		if((line=br.readLine())!=null){
			if(sbf.length()==0){
				sbf.append(line);
				parseCommandLine(line);
			}else{
				sbf.append(line);
			}
		}
	}

	//解析请求头信息
	private void parseCommandLine(String command) {
		if(command!=null && !"".equals(command)){
			String[] strs=command.split(" ");
			this.method=strs[0];
			this.protocolVersion=strs[2];
			if("GET".equals(method)){
				doGet(strs[1]);
			}else{
				throw new RuntimeException("http协议错误");
			}
		}	
	}
	
	
	
	
	private void doGet(String str){
		
		if(str.contains("?")){
			String  params=str.substring(str.indexOf("?")+1);
			String[] param=params.split("&");
			String[] temp;
			for(String s:param){
				temp=s.split("=");
				this.parameter.put(temp[0], temp[1]);
			}
			url=str.substring(0,str.indexOf("?"));
		}else{
			url=str;
		}
	}
	
	private void doPost(String str){
		
		
	}
	
	

	public String getUrl() {
		return url;
	}

	public String getMethod() {
		return method;
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public Map<String, String> getParameter() {
		return parameter;
	}
	

}
