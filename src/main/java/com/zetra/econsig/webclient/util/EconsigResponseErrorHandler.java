package com.zetra.econsig.webclient.util;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: EconsigResponseErrorHandler</p>
 * <p>Description: Error handler do sistema para RestTemplate.</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class EconsigResponseErrorHandler implements ResponseErrorHandler {
    public EconsigResponseErrorHandler(AcessoSistema responsavel) {
    }

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return ((httpResponse.getStatusCode().is4xxClientError()) || httpResponse.getStatusCode().is5xxServerError());
    }

	@Override
	public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
	    //
	}
}
