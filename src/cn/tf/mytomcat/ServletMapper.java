package cn.tf.mytomcat;

import java.util.Map;
//每个web.xml中的servlet映射对象
class ServletMapper {
	
	private String servletName;    //servlet的名称
	private String servletClass;   //servlet的处理类的路径
	private String servletUrl;    
	private Map<String,String>  initParams;
	protected String getServletName() {
		return servletName;
	}
	protected String getServletClass() {
		return servletClass;
	}
	protected String getServletUrl() {
		return servletUrl;
	}
	protected Map<String, String> getInitParams() {
		return initParams;
	}
	protected void setServletName(String servletName) {
		this.servletName = servletName;
	}
	protected void setServletClass(String servletClass) {
		this.servletClass = servletClass;
	}
	protected void setServletUrl(String servletUrl) {
		this.servletUrl = servletUrl;
	}
	protected void setInitParams(Map<String, String> initParams) {
		this.initParams = initParams;
	}
	@Override
	public String toString() {
		return "ServletMapper [servletName=" + servletName + ", servletClass="
				+ servletClass + ", servletUrl=" + servletUrl + ", initParams="
				+ initParams + "]";
	}
	
	

}
