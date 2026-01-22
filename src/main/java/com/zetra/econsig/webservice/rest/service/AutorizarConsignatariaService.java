package com.zetra.econsig.webservice.rest.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;


import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.entity.ConsultaMargemSemSenha;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.AutorizarConsignatariaResponse;
import com.zetra.econsig.webservice.rest.request.AutorizarMargemRequest;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.Response;

@Path("/autorizarConsignataria")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class AutorizarConsignatariaService extends RestService {

	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AutorizarConsignatariaService.class);
	
	@Context
	SecurityContext securityContext;
	
	@GET
	@Secured
	@Path("/consultar")
	public Response consulta() {
		try {
			final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
			
			 if (!responsavel.temPermissao(CodedValues.FUN_AUTORIZAR_MARGEM_CONSIGNATARIA)) {
				 final ResponseRestRequest responseError = new ResponseRestRequest();
		         responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.nao.autorizado.consulta",  (AcessoSistema) securityContext.getUserPrincipal());
		         return Response.status(Response.Status.FORBIDDEN).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
             }

			final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
			
			final CustomTransferObject criterio = new CustomTransferObject();
			criterio.setAttribute(Columns.CSA_CONSULTA_MARGEM_SEM_SENHA, CodedValues.TPC_SIM);
			
			final List<TransferObject> consignatarias = consignatariaController.lstConsignatarias(criterio, responsavel);
			if((consignatarias.isEmpty()) || consignatarias == null) {
				final ResponseRestRequest responseError = new ResponseRestRequest();				
				responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.consignatarias.consulta.margem.vazia",  (AcessoSistema) securityContext.getUserPrincipal());
	            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
			}
			
			final Map<String, ConsultaMargemSemSenha> mapConsulta = new HashMap<>();
			
			final List<ConsultaMargemSemSenha> listConsignatariasConsulta = consignatariaController.listaConsignatariaConsultaMargemSemSenhaByRseCodigo(responsavel.getRseCodigo(), responsavel);
			for(final ConsultaMargemSemSenha consignatariaConsulta : listConsignatariasConsulta) {
				mapConsulta.put(consignatariaConsulta.getCsaCodigo(), consignatariaConsulta);
			}
		
			 final List<AutorizarConsignatariaResponse> autorizarConsignatariaResponse = new ArrayList<>();
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		        for (TransferObject csa : consignatarias) {
		        	AutorizarConsignatariaResponse response = new AutorizarConsignatariaResponse();
		            String csaCodigo = (String) csa.getAttribute(Columns.CSA_CODIGO);
		            String csaNome = (String) csa.getAttribute(Columns.CSA_NOME);
		            String csaIdentificador = (String) csa.getAttribute(Columns.CSA_IDENTIFICADOR);
		            String csaNomeAbrev = (String) csa.getAttribute(Columns.CSA_NOME_ABREV);
		            
		            response.setCsaCodigo(csaCodigo);
		            response.setCsaNome(csaNome);
		            response.setCsaIdentificador(csaIdentificador);
		            response.setCsaNomeAbrev(csaNomeAbrev);

		            ConsultaMargemSemSenha consultaMargemSemSenha = mapConsulta.get(csaCodigo);
		            if (consultaMargemSemSenha != null) {
		                Date dataInicio = consultaMargemSemSenha.getCssDataIni();
		                Date dataFim = consultaMargemSemSenha.getCssDataFim();
		                if (dataInicio != null && dataFim != null) {
		                    response.setDataInicio(sdf.format(dataInicio));
		                    response.setDataFim(sdf.format(dataFim));
		                }
		            }
		            autorizarConsignatariaResponse.add(response);
		        }
			
			return Response.status(Response.Status.OK).entity(autorizarConsignatariaResponse).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
		} catch(final Exception ex) {
			
			LOG.error(ex.getMessage(), ex);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema",  (AcessoSistema) securityContext.getUserPrincipal());
            return Response.status(Response.Status.BAD_REQUEST).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }
	}
	
	
	@POST
	@Secured
	@Path("/autorizar")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response autorizarMargem(AutorizarMargemRequest request) {
	    try {
	        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
	        final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);

	        if (!responsavel.temPermissao(CodedValues.FUN_AUTORIZAR_MARGEM_CONSIGNATARIA)) {
	            final ResponseRestRequest responseError = new ResponseRestRequest();
	            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.nao.autorizado.consulta", (AcessoSistema) securityContext.getUserPrincipal());
	            return Response.status(Response.Status.FORBIDDEN).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
	        }

	        final int quantidadeDias = ParamSist.getIntParamSist(CodedValues.TPC_DIAS_VALIDADE_AUTORIZACAO_SERVIDOR_CON_MAR_POR_COD, 0, responsavel);
	        if (quantidadeDias <= 0) {
	            final ResponseRestRequest responseError = new ResponseRestRequest();
	            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.consulta.margem.tpc", null);
	            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
	        }

	        final List<ConsultaMargemSemSenha> autorizacoesExistentes = consignatariaController.listaConsignatariaConsultaMargemSemSenhaByRseCodigo(responsavel.getRseCodigo(), responsavel);

	        final Map<String, ConsultaMargemSemSenha> autorizacoesMap = autorizacoesExistentes.stream()
	            .collect(Collectors.toMap(auth -> auth.getConsignataria().getCsaCodigo(), auth -> auth));
	        
	        final Date dataAtual = DateHelper.getSystemDatetime();
	        final Date dataFim = DateHelper.addDays(dataAtual, quantidadeDias);
	        
	        for (String csaCodigo : request.getCsaCodigos()) {
	        	
	            // Verifica se já existe uma autorização para o rseCodigo e csaCodigo
	            ConsultaMargemSemSenha autorizacaoExistente = autorizacoesMap.get(csaCodigo);

	            if (autorizacaoExistente == null) {
	            	consignatariaController.createConsignatariaConsultaMargemSemSenha(responsavel.getRseCodigo(), csaCodigo, dataAtual, dataFim, responsavel);
	            }
	        }

	        return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
	    } catch (final Exception ex) {
	        LOG.error(ex.getMessage(), ex);
	        final ResponseRestRequest responseError = new ResponseRestRequest();
	        responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", (AcessoSistema) securityContext.getUserPrincipal());
	        return Response.status(Response.Status.BAD_REQUEST).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
	    }
	}
	
	
	@POST
	@Secured
	@Path("/revogar")
	public Response revogarAutorizacao(AutorizarMargemRequest request) {
	    try {
	        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
	        final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);

	        if (!responsavel.temPermissao(CodedValues.FUN_AUTORIZAR_MARGEM_CONSIGNATARIA)) {
	            final ResponseRestRequest responseError = new ResponseRestRequest();
	            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.nao.autorizado.revogar", responsavel);
	            return Response.status(Response.Status.FORBIDDEN).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
	        }

	        final List<ConsultaMargemSemSenha> autorizacoesExistentes = consignatariaController.listaConsignatariaConsultaMargemSemSenhaByRseCodigo(responsavel.getRseCodigo(), responsavel);
	        final Map<String, ConsultaMargemSemSenha> autorizacoesMap = autorizacoesExistentes.stream()
	            .collect(Collectors.toMap(auth -> auth.getConsignataria().getCsaCodigo(), auth -> auth));
	        
	        List<String> values = new ArrayList<>(); 
	        
	        Map<String, String> operacoesAtivasMap = consignatariaController.findCsasComOperacoesEmAndamentoByRseCodigo(responsavel);
	        
	        for (String csaCodigo : request.getCsaCodigos()) {
	        	boolean podeSerRevogado = true;
	        	
	            ConsultaMargemSemSenha autorizacaoExistente = autorizacoesMap.get(csaCodigo);
	            
	            if (!operacoesAtivasMap.isEmpty() && (operacoesAtivasMap.get(csaCodigo) != null)) {
	                values.add(operacoesAtivasMap.get(csaCodigo));
	                podeSerRevogado = false;
	            } 
	            
	            if (autorizacaoExistente != null && podeSerRevogado) {
	                String cssCodigo = autorizacaoExistente.getCssCodigo();
	                consignatariaController.updateConsignatariaConsultaMargemSemSenha(cssCodigo, "S", responsavel);
	            } 
	        }
	        
	        if (values.isEmpty()) {
	        	 return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
	        } else {
            	  ResponseRestRequest responseError = new ResponseRestRequest();
                  responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.sem.permissao.desautorizar.consignataria.modal", responsavel,  String.join(", ", values));
                  values.clear();
	              return Response.status(Response.Status.FORBIDDEN).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }
	       
	    } catch (final Exception ex) {
	        LOG.error(ex.getMessage(), ex);
	        final ResponseRestRequest responseError = new ResponseRestRequest();
	        responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", (AcessoSistema) securityContext.getUserPrincipal());
	        return Response.status(Response.Status.BAD_REQUEST).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
	    }
	}
}
