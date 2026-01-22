package com.zetra.econsig.webservice.rest.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleRestricaoAcesso;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.web.listener.SessionCounterListener;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.CamposRestRequest;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

/**
 * <p>Title: SistemaService</p>
 * <p>Description: Serviço REST para sistema.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/sistema")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class SistemaService extends RestService {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SistemaService.class);

    @Context
    SecurityContext securityContext;

    @POST
    @Secured
    @Path("/campos")
    public Response campos(CamposRestRequest dados) {
        // Carrega o cache de campos caso esteja vazio de modo que a tabela seja preenchida, caso ainda não esteja
        ShowFieldHelper.load();

        AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        List<TransferObject> campos;
        try {
            SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);
            campos = sistemaController.lstCampoSistema(dados != null ? dados.casChave : null, dados != null ? dados.somenteCamposEditaveis : false, responsavel);
        } catch (ConsignanteControllerException e) {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            LOG.error(e.getMessage(), e);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        List<String> filter = Arrays.asList("cas_chave", "cas_valor");
        return Response.status(Response.Status.OK).entity(transformTOs(campos, filter)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    }

    @POST
    @Path("/status")
    public Response status(@Context HttpServletRequest request) {
        String ipAcessoRequisicao = JspHelper.getRemoteAddr(request);
        if (permitirAcesso(ipAcessoRequisicao)) {
            try {
                AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
                ConsignanteTransferObject cto = new ConsignanteDelegate().findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
                boolean indisponivel = cto.getCseAtivo().equals(CodedValues.STS_INDISP) || (ControleRestricaoAcesso.possuiRestricaoAcesso(responsavel).getGrauRestricao() == ControleRestricaoAcesso.GrauRestricao.RestricaoGeral);
                Map<String, String> response = new HashMap<>();
                response.put("status", indisponivel ? "ERRO" : "OK");
                return Response.status(Response.Status.OK).entity(response).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
            } catch (ZetraException e) {
                ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = e.getMessage();
                LOG.error(e.getMessage(), e);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
            }
        } else {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("rotulo.endereco.acesso.invalido.ip", AcessoSistema.getAcessoUsuarioSistema(), ipAcessoRequisicao);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }
    }

    @POST
    @Path("/sessoes")
    public Response sessoes(@Context HttpServletRequest request) {
        String ipAcessoRequisicao = JspHelper.getRemoteAddr(request);
        if (permitirAcesso(ipAcessoRequisicao)) {
            SessionCounterListener sessionManagment = ApplicationContextProvider.getApplicationContext().getBean(SessionCounterListener.class);
            Map<String, String> response = new HashMap<>();
            response.put("total", String.valueOf(sessionManagment.getActiveSessions()));
            response.put("tempo", String.valueOf(sessionManagment.getSecondsWithoutUsers()));
            return Response.status(Response.Status.OK).entity(response).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        } else {
            ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("rotulo.endereco.acesso.invalido.ip", AcessoSistema.getAcessoUsuarioSistema(), ipAcessoRequisicao);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }
    }

    private boolean permitirAcesso(String ipAcessoRequisicao) {
        String ipsAcessoLiberado = (String) ParamSist.getInstance().getParam(CodedValues.TPC_IPS_LIBERADOS_PAGINA_ADMINISTRACAO, AcessoSistema.getAcessoUsuarioSistema());
        if (TextHelper.isNull(ipsAcessoLiberado)) {
            ipsAcessoLiberado = "127.0.0.1";
        }
        return JspHelper.validaDDNS(ipAcessoRequisicao, ipsAcessoLiberado);
    }
}
