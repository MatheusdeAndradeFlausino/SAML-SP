package com.example.samlsp;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.exception.Error;
import com.onelogin.saml2.exception.SettingsException;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.settings.SettingsBuilder;

@RestController
@RequestMapping("saml-sp")
public class SAMLSPController {

	@RequestMapping(value = "metadata", produces = {"application/xml"}, method = {RequestMethod.GET})
	public String metadata() throws Exception {
		
		Saml2Settings setttings = new SettingsBuilder().fromProperties(SAMLSPPropertiesSSO.getInstance().getProperties()).build();
		String metadata = setttings.getSPMetadata();
		
		List<String> errors = Saml2Settings.validateMetadata(metadata);
		
		if (errors.isEmpty()) {
			return metadata;
		} else {
			throw new UnexpectedException("Erro ao validar metadata!");
		}
	}
	
	@RequestMapping(value = "assertionConsumerService" , method = {RequestMethod.GET, RequestMethod.POST})
	public Map<String, List<String>> assertionConsumerService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Properties properties = SAMLSPPropertiesSSO.getInstance().getProperties();
		Saml2Settings settings = new SettingsBuilder().fromProperties(properties).build();
		
		Auth auth = new Auth(settings, request, response);
		auth.processResponse();
		if (!auth.isAuthenticated()) {
			throw new UnexpectedException("Você não está logado!");
		}
		List<String> errors = auth.getErrors();
		if (!errors.isEmpty()) {
			throw new UnexpectedException("Existem erros no seu login");
		} else {
			Map<String, List<String>> attributes = auth.getAttributes();
			String nameId = auth.getNameId();
			String nameIdFormat = auth.getNameIdFormat();
			String sessionIndex = auth.getSessionIndex();
			String nameidNameQualifier = auth.getNameIdNameQualifier();
			String nameidSPNameQualifier = auth.getNameIdSPNameQualifier();
			
			Map<String, Object> session = new HashMap<>();
			session.put("attributes", attributes);
			session.put("nameId", nameId);
			session.put("nameIdFormat", nameIdFormat);
			session.put("sessionIndex", sessionIndex);
			session.put("nameidNameQualifier", nameidNameQualifier);
			session.put("nameidSPNameQualifier", nameidSPNameQualifier);
			
			String relayState = request.getParameter("RelayState");
		
			return attributes;
		}
		
	}
	
	@GetMapping("login")
	public void login(HttpServletRequest request, HttpServletResponse response) throws IOException, SettingsException, Error {
	
		Properties properties = SAMLSPPropertiesSSO.getInstance().getProperties();
		
		Saml2Settings settings = new SettingsBuilder().fromProperties(properties).build();
		
		Auth auth = new Auth(settings, request, response);
		
		auth.login();
	}
	
}
