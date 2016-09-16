package phn.nts.ams.fe.util;


import java.io.File;
import java.io.IOException;

import org.springframework.core.io.ClassPathResource;

import phn.com.nts.util.common.Helpers;
import phn.com.nts.util.log.Logit;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

/**
 * @description
 * @version TDSBO1.0
 * @CrBy Nguyen Xuan Bach
 * @CrDate Feb 24, 2012
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class FreeMarkerConfig {
	private static final Logit logger = Logit.getInstance(FreeMarkerConfig.class);
	public static final int KEYWORD_LIMIT = 10;
	private String freeMarkerTemplatePath;
	private static Configuration cfg;
	
	public final Configuration getCfg() {
		if (cfg == null) {
			initFreeMarkerConfig();
		}
		return cfg;
	}

	public String getFreeMarkerTemplatePath() {
		return freeMarkerTemplatePath;
	}

	public void setFreeMarkerTemplatePath(String freeMarkerTemplatePath) {
		this.freeMarkerTemplatePath = freeMarkerTemplatePath;
	}

	public static Integer getShowMorePageSize() {
		return 10;
	}

	public static Integer getInitPageSize() {
		return 15;
	}

	public void initFreeMarkerConfig() {
		System.out.println("AppConfig.initFreeMarkerConfig()");
		cfg = new Configuration();
		try {
			String configPath = System.getProperty("configPath");
			File file = new File( (configPath == null ? "" : configPath + "/") +  getFreeMarkerTemplatePath());					
			cfg.setDirectoryForTemplateLoading(file);
		} catch (IOException e) {
			logger.error("Unable to find freemarker template path: "
					+ freeMarkerTemplatePath, e);
		}
		cfg.setObjectWrapper(new DefaultObjectWrapper());
	}
}
