package com.zetra.econsig.webservice.rest.service;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.exception.ArquivoControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Arquivo;
import com.zetra.econsig.persistence.entity.ArquivoRse;
import com.zetra.econsig.service.arquivo.ArquivoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.AnexoRseRestResponse;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

/**
 * <p>Title: ConsultaAnexosServidorService</p>
 * <p>Description: Serviço REST para consulta de anexos do usuário de servidor.</p>
 * <p>Copyright: Copyright (c) 2021</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: ricardo.magno$
 * $Revision: $
 * $Date: $
 */
@Path("/anexosServidor")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ConsultaAnexosServidorService extends RestService {
	/** Log object for this class. */
	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsultaAnexosServidorService.class);


	@Context
	SecurityContext securityContext;

	private Response validarOperacao(AcessoSistema responsavel) {
		if (!responsavel.isSer() || TextHelper.isNull(responsavel.getRseCodigo())) {
			ResponseRestRequest responseError = new ResponseRestRequest();
			responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", null);
			return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
		}

		if (!responsavel.temPermissao(CodedValues.FUN_EDT_SERVIDOR)) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

		return null;
	}

	@POST
	@Secured
	@Path("/consultaAnexosServidor")
	public Response consultaAnexosServidor() {
		AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

		Response resultadoValidacao = validarOperacao(responsavel);
		if (resultadoValidacao != null) {
			return resultadoValidacao;
		}

		ArquivoController anexosServidorController = ApplicationContextProvider.getApplicationContext().getBean(ArquivoController.class);
		String rseCodigo = responsavel.getRseCodigo();

		try {
			List<ArquivoRse> anexosRse = anexosServidorController.lstArquivoRse(rseCodigo, responsavel);
			List<AnexoRseRestResponse> result = new ArrayList<>();
			for (ArquivoRse arquivoRse : anexosRse) {
				AnexoRseRestResponse item = new AnexoRseRestResponse();
				item.arqCodigo = arquivoRse.getArqCodigo();
				item.arqNome = arquivoRse.getArsNome();
				item.dataCriacao = DateHelper.format(arquivoRse.getArsDataCriacao(), LocaleHelper.getDatePattern());
				item.rseCodigo = rseCodigo;
				item.ipAcesso = arquivoRse.getArsIpAcesso();
				item.usuCodigo = arquivoRse.getUsuario().getUsuCodigo();
				result.add(item);
			}

			return Response.status(Response.Status.OK).entity(result).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
		} catch (ZetraException e) {
			ResponseRestRequest responseError = new ResponseRestRequest();
			responseError.mensagem = e.getMessage();
			LOG.error(e.getMessage(), e);
			return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
		}
	}

	@POST
	@Secured
	@Path("/download/{id}")
	public Response download(@PathParam("id") String codigo) {
		AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
		Response resultadoValidacao = validarOperacao(responsavel);

		if (resultadoValidacao != null) {
			return resultadoValidacao;
		}

		try {
			// Pesquisa o anexo pelo código
			ArquivoController arquivoController = ApplicationContextProvider.getApplicationContext().getBean(ArquivoController.class);
			Arquivo arquivo = arquivoController.getArquivoRse(codigo, responsavel);
			String arqConteudo = arquivo.getArqConteudo();

			// Gera log de download de arquivo
			LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DOWNLOAD_FILE, Log.LOG_INFORMACAO);
			log.setBoletoServidor(codigo);
			log.write();

			// Gera o resultado para o cliente
			AnexoRseRestResponse arquivoRse = new AnexoRseRestResponse();
			arquivoRse.conteudo = arqConteudo;

			return Response.status(Response.Status.OK).entity(arquivoRse).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();

		} catch (LogControllerException | ArquivoControllerException ex) {
			LOG.error(ex.getMessage(), ex);
			return genericError(ex);
		}
	}
}
