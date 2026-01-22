package com.zetra.econsig.webservice.soap.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.http.HttpServletConnection;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;

import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>Title: SoapMessageHelper</p>
 * <p>Description: Helper para métodos utilitários sobre pacotes SOAP.</p>
 * <p>Copyright: Copyright (c) 2002-2003</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor Lucas, Leonel Martins
 */
public class SoapMessageHelper {

    private SoapMessageHelper() {
    }

    /**
     *
     * @param context
     * @return All IP address in the chain of access
     */
    public static final String getRemoteAddr(TransportContext context) {
        final HttpServletConnection connection = (HttpServletConnection) context.getConnection();
        final HttpServletRequest request = connection.getHttpServletRequest();
        String remoteAddr = request.getRemoteAddr();

        if (!TextHelper.isNull(request.getHeader(CodedNames.HEADER_CLIENT_REMOTE_ADDR)) &&
        		enderecoPertencenteCentralizador(remoteAddr, AcessoSistema.getAcessoUsuarioSistema())) {
            remoteAddr += AcessoSistema.ADDRESS_SEPARATOR + request.getHeader(CodedNames.HEADER_CLIENT_REMOTE_ADDR);
        }

        return remoteAddr;
    }
    
    /**
     * 
     * @param request
     * @return - Retorna a porta lógica do usuário logado.
     */
    public static int getRemotePort(TransportContext context) {
    	String remoteAddr;
    	Integer remotePort = 0;
    	if (context != null) {
    		final HttpServletConnection connection = (HttpServletConnection) context.getConnection();
    		final HttpServletRequest request = connection.getHttpServletRequest();
    		remoteAddr = request.getRemoteAddr();
    		remotePort =  request.getRemotePort();

    		if (!TextHelper.isNull(request.getHeader(CodedNames.HEADER_CLIENT_REMOTE_PORT)) &&
    				enderecoPertencenteCentralizador(remoteAddr, AcessoSistema.getAcessoUsuarioSistema())) {
    			remotePort = Integer.valueOf(request.getHeader(CodedNames.HEADER_CLIENT_REMOTE_PORT));
    		}
    	}
    	return remotePort;
    }

    public static final String[] getAddresses(String address) {
       return (address == null) ? new String[]{} : TextHelper.split(address, AcessoSistema.ADDRESS_SEPARATOR);
    }

    private static final boolean enderecoPertencenteCentralizador(String endereco, AcessoSistema responsavel) {
        final Object paramUrlCentralizador = ParamSist.getInstance().getParam(CodedValues.TPC_URL_CENTRALIZADOR, responsavel);
        final String urlCentralizador = (paramUrlCentralizador != null) ? (String) paramUrlCentralizador : "";
        if (!"".equals(urlCentralizador)) {
            final List<String> urls = Arrays.asList(urlCentralizador.split(";"));
            if (!JspHelper.validaUrl(endereco, urls)) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }
}
