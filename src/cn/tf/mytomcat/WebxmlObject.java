package cn.tf.mytomcat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;


public class WebxmlObject {
	//每个web.xml对象中的信息
	protected  static   Map<String,List<ServletMapper>>  webObject=new HashMap<String,List<ServletMapper>>();
	
	//默认页面信息
	protected  static Map<String,List<String>> welcomeList=new HashMap<String,List<String>>();

	
	@Test
	public void init() throws DocumentException{
		String basePath=Config.getConfig().getProperty("path");
		//System.out.println(basePath);
		
		//扫描整个文件夹下面的所有目录，因为在服务器中，每一个目录就是一个项目
		File file=new File(basePath);
		
		//获得当前目录下的所有子目录或文件
		File[] listFiles = file.listFiles();
		
		for (File fl : listFiles) {
			//System.out.println(fl.getName());
			if(fl.isDirectory()){
				//如果是一个目录就说明是一个项目，读取WEB-INF/web.xml文件
				parseXml(fl);

			}else{
				continue;
			}	
		}
		
		/*System.out.println(webObject);
		System.out.println(welcomeList);*/
		
	}
	
	public void parseXml(File file) throws DocumentException {
		File webFile=null;      //指向每一个web.xml文件
		List<ServletMapper>  list=null;  //存放每个web.xml中的所有servlet信息
		ServletMapper  mapper;  //存放每个servlet信息
		Map<String,String>  servletInfo = null;
		Map<String,String> servletMapping = null;
		List<String> welcomes =null;
		
		webFile=new File(file,"WEB-INF/web.xml");
		if(webFile.exists()){
			list=new ArrayList<ServletMapper>();
			SAXReader reader=new SAXReader();
			
			Map<String,String>  map=new HashMap<String,String>();
			map.put("design", "http://java.sun.com/xml/ns/javaee");   //命名空间的地址
			reader.getDocumentFactory().setXPathNamespaceURIs(map);
			
				Document doc=reader.read(webFile);
				List<Element>  nodes=doc.selectNodes("//design:servlet");
				
				//循环取出servlet中的配置信息
				if(nodes.size()>0){
					servletInfo=new HashMap<String,String>();
					String servletName;
					String servletClass;
					
					for(Element et:nodes){
						servletName=et.selectSingleNode("//design:servlet-name").getText().toString();
						servletClass=et.selectSingleNode("//design:servlet-class").getText().toString();
						servletInfo.put(servletName, servletClass);
					}				
				}
				//取servlet-mapping的配置信息
				
				nodes=doc.selectNodes("//design:servlet-mapping");
				
				//循环取出servlet中的配置信息
				if(nodes.size()>0){
					servletMapping=new HashMap<String,String>();
					String servletName;
					String urlPattern;
					
					for(Element et:nodes){
						servletName=et.selectSingleNode("design:servlet-name").getText().toString();
						urlPattern=et.selectSingleNode("design:url-pattern").getText().toString();
						servletMapping.put(servletName,urlPattern);
					}
				}
				
				//取出welcome-file-list
				
				nodes=doc.selectNodes("//design:welcome-file-list/design:welcome-file");
				
				//循环取出servlet中的配置信息
				if(nodes.size()>0){
					welcomes=new ArrayList<String>();

					for(Element et:nodes){
						welcomes.add(et.getTextTrim());
					}
				}
				
				if(servletMapping!=null && servletMapping.size()>0){
					Set<String> keys=servletMapping.keySet();
					for(String key:keys){
						if(servletInfo.containsKey(key)){
							mapper=new ServletMapper();
							mapper.setServletName(key);
							mapper.setServletClass(servletInfo.get(key));
							mapper.setServletUrl(servletMapping.get(key));
							list.add(mapper);
						}else{
							//如果在servlet中找不到对应的servlet-name
							throw new RuntimeException(servletMapping.get(key)+"没有对应的处理类");
						}
					}
				}
			} 
		
		welcomeList.put(file.getName(), welcomes);
		webObject.put(file.getName(),list);
		
	}	
}
