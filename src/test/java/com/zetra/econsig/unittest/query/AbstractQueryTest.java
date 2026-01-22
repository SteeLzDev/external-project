package com.zetra.econsig.unittest.query;

import java.net.InetAddress;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.config.ContextSpringConfiguration;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.service.SistemaService;

public abstract class AbstractQueryTest extends ContextSpringConfiguration {

    @Autowired
    private SistemaService sistemaService;

    protected void executarConsulta(HQuery query) throws HQueryException {
        sistemaService.executarConsulta(query);
    }
    
    /**
     * 
     * @return Retorna o endereço IP de loopback (localhost/127.0.0.1) do host.
     */
    protected String getLoopbackAddress() {
    	//Obtém o endereço IP de loopback (127.0.0.1)
    	InetAddress loopbackAddress = InetAddress.getLoopbackAddress();
    	//Retorna o IP em formato de string
    	String ipString = loopbackAddress.getHostAddress();

    	return ipString;

    }
    
}
