package com.example.samlsp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class SAMLSPPropertiesSSO {
	
	private static SAMLSPPropertiesSSO instance = null;
	private Properties prop = null;
	
	private SAMLSPPropertiesSSO() {
		carregaPropriedadesPadrao();
	}
	
	public String getPropertyFileName() {
		return "samlsp.onelogin.saml.properties";
	}

	public static SAMLSPPropertiesSSO getInstance(){
		if (instance==null){
			instance = new SAMLSPPropertiesSSO();
		}
		return instance;
	}

	/**
	 * Carrega propriedades padrao do arquivo onelogin.saml.properties da pasta resources 
	 * e carrega as configuracoes default para o arquivo externo
	 */
	public void carregaPropriedadesPadrao() {
		
		prop = new java.util.Properties();
		try {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(getPropertyFileName());
			prop.load(is);
			
			String ext_prop = prop.getProperty("ext.properties");
			if (ext_prop != null) {
				
				File file = new File(ext_prop);
				
				if (file.exists()) {
					is = new FileInputStream(ext_prop);
					prop = new Properties(prop);
					prop.load(is);
				}

			}
			
		} catch (Exception ex) {
			prop = null;
			throw new RuntimeException("Nao conseguiu carregar o properties");
		}
	}

	public Properties getProperties() {
		
		if (prop == null) {
			carregaPropriedadesPadrao();
		}
		
		return (Properties) prop.clone();
	}
	
}