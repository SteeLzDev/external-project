package com.zetra.econsig.helper;

import java.io.File;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class ConfigurationHelper {

	private CompositeConfiguration config = null;	

    public ConfigurationHelper() {
        config = new CompositeConfiguration();
        config.addConfiguration(new SystemConfiguration());
    }
    	 
    public ConfigurationHelper(String configFile) { 	
        try {

            //DESENV-23982: Criação de configurations com base em arquivos a partir da versão 2 é feita somente por meio de builders
            Parameters params = new Parameters();
            File propertiesFile = new File(configFile);
            FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
                    new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                            .configure(params.fileBased().setFile(propertiesFile));

            config = new CompositeConfiguration();
            config.addConfiguration(new SystemConfiguration());
            config.addConfiguration(builder.getConfiguration());
        } catch (Exception e) {
            log.warn("Nao foi possivel carregar o arquivo de configuracao {}", configFile);
        }
    }

    public String getProp(String key) {
        return getProp(key, "");
    }

    public String getProp(String key, String defaultValue) {
        String res = config.getString(key, defaultValue);
        if (log.isDebugEnabled()) {
            String msg = StringUtils.equals(config.getString(key), defaultValue) ? "[DEFAULT]" : "";
            log.debug("getProp(" + key + ")=" + res + " " + msg);
        }
        return res;
    }

    public int getIntProp(String key, int defaultValue) {
        int value = config.getInt(key, defaultValue);
        log.debug("getIntProp(" + key + ")=" + value);
        return value;
    }

}
