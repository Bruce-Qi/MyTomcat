package cn.tf.mytomcat;

import java.io.IOException;
import java.util.Properties;

public class Config extends Properties{
	
	private static Config config=new Config();
	
	private Config(){
		try {
			load(Config.class.getClassLoader().getResourceAsStream("web.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static Config getConfig(){
		if(config==null){
			config=new Config();
		}
		return config;
	}

}
