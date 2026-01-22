package com.zetra.econsig.webservice.rest.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.ConvenioTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.ConsignatariaHome;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.ConsignatariaResquest;
import com.zetra.econsig.webservice.rest.request.ConvenioRequest;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

/**
 * <p>Title: ConsignatariaService</p>
 * <p>Description: Serviço REST para operações sobre entidade consignatária.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/consignataria")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ConsignatariaService extends RestService {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsignatariaService.class);

    @Context
    private SecurityContext securityContext;

    @POST
    @Secured
    @Path("/lstCsasCnv")
    public Response lstCsasCnv(ConvenioRequest cnvRequest) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_CONS_CONSIGNATARIAS), null);
        if (resultadoValidacao != null) {
            return resultadoValidacao;
        }

        if (cnvRequest == null) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        if (TextHelper.isNull(cnvRequest.orgCodigo)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.orgao", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        if (TextHelper.isNull(cnvRequest.svcCodigo)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.convenio.informar.servico", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        final ConvenioDelegate cnvDelegate = new ConvenioDelegate();
        final ParametroDelegate paramDelegate = new ParametroDelegate();

        try {
            final List<TransferObject> lstCsas = cnvDelegate.getCsaCnvAtivo(cnvRequest.svcCodigo, cnvRequest.orgCodigo, true, true, responsavel);
            final List<String> parametrosSvc = new ArrayList<>();
            parametrosSvc.add(CodedValues.TPS_PULA_INFORMACAO_VALOR_PRAZO_FLUXO_RESERVA);
            final ParamSvcTO paramSvcCse = paramDelegate.selectParamSvcCse(cnvRequest.svcCodigo, parametrosSvc, responsavel);

            final boolean paramSvc277 = paramSvcCse.isTpsPulaInformacaoValorPrazoFluxoReserva();
            final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();

            final List<TransferObject> lstRetorno = new ArrayList<>();
            final ParametroDelegate parDelegate = new ParametroDelegate();
            final List<String> tpsCsaCodigos = new ArrayList<>();
            tpsCsaCodigos.add(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES);

            for (final TransferObject toCsa : lstCsas) {
                try {
                    final String csaCodigo = (String) toCsa.getAttribute(Columns.CSA_CODIGO);
                    final ConvenioTransferObject cnvTo = cnvDelegate.findByUniqueKey(csaCodigo, cnvRequest.svcCodigo, cnvRequest.orgCodigo, responsavel);
                    toCsa.setAttribute(Columns.CNV_CODIGO, cnvTo.getCnvCodigo());
                    toCsa.setAttribute("PSC_277", paramSvc277);

                    boolean emailSolicitacao = false;
                    boolean obsSolicitacao = false;

                    final List<TransferObject> tdaList = adeDelegate.lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.WEB, cnvRequest.svcCodigo, csaCodigo, responsavel);
                    if ((tdaList != null) && !tdaList.isEmpty()) {
                        for (final TransferObject tda : tdaList) {
                            if(CodedValues.TDA_SOLICITACAO_EMAIL_SERVIDOR.equals(tda.getAttribute(Columns.TDA_CODIGO).toString())) {
                                emailSolicitacao = true;
                            } else if(CodedValues.TDA_SOLICITACAO_OBS_SERVIDOR.equals(tda.getAttribute(Columns.TDA_CODIGO).toString())) {
                                obsSolicitacao = true;
                            }
                        }
                    }

                    toCsa.setAttribute("emailSolicitacao", emailSolicitacao);
                    toCsa.setAttribute("obsSolicitacao", obsSolicitacao);

                    final ConsignatariaController csaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
                    final List<String> csaCodigos = new ArrayList<>();
                    csaCodigos.add(csaCodigo);

                    final List<TransferObject> contato = csaController.listaCsaPermiteContato(csaCodigos, responsavel);
                    if(!contato.isEmpty() ) {
                    	final TransferObject csaIsolada = contato.get(0);

                        final Object csa_whatsapp = csaIsolada.getAttribute(Columns.CSA_WHATSAPP);
                		final Object csa_contato_tel = csaIsolada.getAttribute(Columns.CSA_TEL);
                        final Object csa_email_contato = csaIsolada.getAttribute(Columns.CSA_EMAIL_CONTATO);
                        final Object pcs_vlr = csaIsolada.getAttribute(Columns.PCS_VLR);
                        toCsa.setAttribute("csa_whatsapp", csa_whatsapp != null ? csa_whatsapp.toString() : "");
                        toCsa.setAttribute("csa_contato_tel", csa_contato_tel != null ? csa_contato_tel.toString() : "");
                        toCsa.setAttribute("csa_email_contato", csa_email_contato != null ? csa_email_contato.toString() : "");
                        toCsa.setAttribute("pcs_vlr", pcs_vlr != null ? pcs_vlr.toString() : "");
                    }

                    final List<TransferObject> paramSvcCsa = parDelegate.selectParamSvcCsa(cnvRequest.svcCodigo, csaCodigo, tpsCsaCodigos, false, responsavel);

                    boolean exigeAssinaturaDigital = false;

                    for (final TransferObject vo : paramSvcCsa) {
                        if ((vo.getAttribute(Columns.PSC_VLR) != null) && !"".equals(vo.getAttribute(Columns.PSC_VLR))) {
                            String exige = null;
                            exige = vo.getAttribute(Columns.PSC_VLR).toString();
                            exigeAssinaturaDigital = (exige != null) && "S".equals(exige);
                        }
                    }
                    //busca permissão para anexar arquivos em consignação
                    toCsa.setAttribute("anexoGenerico", !exigeAssinaturaDigital && ParamSist.paramEquals(CodedValues.TPC_PERMITE_ANEXO_INCLUSAO_CONTRATOS, CodedValues.TPC_SIM, responsavel) && responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO));

                    final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
                    final String textoContato = csaDelegate.findConsignataria(csaCodigo, responsavel).getCsaTxtContato();
                    toCsa.setAttribute("csaTxtContato", textoContato);

                    lstRetorno.add(toCsa);
                } catch (ConvenioControllerException | ConsignatariaControllerException e) {
                    LOG.info(e.getMessage());
                }
            }

            final List<String> filter = Arrays.asList("cnv_codigo", "csa_codigo", "csa_nome", "PSC_277", "csa_identificador_interno", "csa_cnpj", "emailSolicitacao", "obsSolicitacao", "anexoGenerico", "csaTxtContato", "csa_whatsapp", "csa_contato_tel", "csa_email_contato", "pcs_vlr");

            return Response.status(Response.Status.OK).entity(transformTOs(lstRetorno, filter)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();

        } catch (ConvenioControllerException | ParametroControllerException | AutorizacaoControllerException e) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            LOG.error(e.getMessage(), e);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

    }

    @POST
    @Secured
    @Path("/{id}")
    public Response csa(@PathParam("id") String id){
        try {
            final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
            Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_CONS_CONSIGNATARIAS), null);
            if (resultadoValidacao != null) {
                return resultadoValidacao;
            }

            final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
            final ConsignatariaTransferObject csa = csaDelegate.findConsignataria(responsavel.getCodigoEntidade(), responsavel);
            final List<String> filter = Arrays.asList("cnv_codigo", "csa_codigo", "csa_nome", "csa_nome_abrev", "PSC_277", "csa_identificador_interno", "csa_cnpj");
            return Response.status(Response.Status.OK).entity(transformTO(csa, filter)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        } catch (final ConsignatariaControllerException e) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            LOG.error(e.getMessage(), e);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }
    }

    @POST
    @Secured
    @Path("/correspondente/{corCodigo}")
    public Response buscarCsaCorrespondente(@PathParam("corCodigo") String corCodigo){
        try {
            final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
            Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_CONS_CORRESPONDENTES), null);
            if (resultadoValidacao != null) {
                return resultadoValidacao;
            }

            final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
            final CorrespondenteTransferObject cor = csaDelegate.findCorrespondente(corCodigo, responsavel);
            final List<String> filter = Arrays.asList("cnv_codigo", "csa_codigo", "csa_nome", "csa_nome_abrev", "PSC_277", "csa_identificador_interno", "csa_cnpj",
                    "cor_identificador", "cor_codigo", "cor_nome");
            return Response.status(Response.Status.OK).entity(transformTO(cor, filter)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        } catch (final ConsignatariaControllerException e) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            LOG.error(e.getMessage(), e);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }
    }

    @GET
    @Secured
    @Path("/lstParamSvcCsa")
    public Response lstParamSvcCsa(@QueryParam("svcCodigo") String svcCodigo, @QueryParam("csaCodigo") String csaCodigo,
    		@QueryParam("orgCodigo") String orgCodigo, @QueryParam("csaIdentificador") String csaIdentificador) {
    	final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
    	final ParametroDelegate parDelegate = new ParametroDelegate();

    	final boolean temPermissaoSimulacao = responsavel.temPermissao(CodedValues.FUN_SIM_CONSIGNACAO);
    	final boolean temPermissaoReserva   = responsavel.temPermissao(CodedValues.FUN_RES_MARGEM);
    	final boolean temPermissaoSolicitacao = responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO);

    	if (!temPermissaoSimulacao && !temPermissaoReserva && !temPermissaoSolicitacao) {
    		final ResponseRestRequest responseError = new ResponseRestRequest();
    		responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.status.erro.acesso.negado", responsavel);
    		LOG.error(ApplicationResourcesHelper.getMessage("mensagem.status.erro.acesso.negado", responsavel));
    		return Response.status(Response.Status.FORBIDDEN).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    	}

    	if (TextHelper.isNull(csaCodigo) && !TextHelper.isNull(csaIdentificador)) {
			try {
				final Consignataria csa = ConsignatariaHome.findByIdn(csaIdentificador);
				csaCodigo = csa.getCsaCodigo();
			} catch (final FindException e) {
				final ResponseRestRequest responseError = new ResponseRestRequest();
	    		responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.consignataria.nao.encontrada", responsavel);
	    		LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.consignataria.nao.encontrada", responsavel));
	    		return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
			}
    	}

    	if (TextHelper.isNull(csaCodigo)) {
    		final ResponseRestRequest responseError = new ResponseRestRequest();
    		responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.consignataria.nao.encontrada", responsavel);
    		LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.consignataria.nao.encontrada", responsavel));
    		return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    	}

    	try {
    		final List<TransferObject> lstParamSvcCsa = parDelegate.selectParamSvcCsa(List.of(svcCodigo), List.of(csaCodigo), List.of(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES),
    				true, responsavel);

    		if (!lstParamSvcCsa.isEmpty()) {
    			final List<String> filter = List.of("tps_codigo", "psc_vlr", "psc_vlr_ref");
    			return Response.status(Response.Status.OK).entity(lstParamSvcCsa.stream().map(to -> transformTO(to, filter)).toList())
    					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    		}

    		return Response.status(Response.Status.OK).entity(lstParamSvcCsa).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    	} catch (final ParametroControllerException e) {
    		final ResponseRestRequest responseError = new ResponseRestRequest();
    		responseError.mensagem = e.getMessage();
    		LOG.error(e.getMessage(), e);
    		return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    	}
    }

    @POST
    @Secured
    @Path("/lstConsignatarias")
    public Response consultaCse(ConsignatariaResquest dados) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_CONS_CONSIGNATARIAS), null);
        if (resultadoValidacao != null) {
            return resultadoValidacao;
        }

        if (TextHelper.isNull(dados.csaCodigo) && TextHelper.isNull(dados.csaIdentificador) && TextHelper.isNull(dados.csaNatureza) && TextHelper.isNull(dados.csaNome) && TextHelper.isNull(dados.csaCnvCodVerba) && TextHelper.isNull(dados.csaBloqueada)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", responsavel);
            LOG.error(responseError.mensagem);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        } else {
            try {
                final String csaAtivo = TextHelper.isNull(dados.csaBloqueada) ? null : !TextHelper.isNull(dados.csaBloqueada) && dados.csaBloqueada.equals("true") ? CodedValues.STS_INATIVO.toString() : CodedValues.STS_ATIVO.toString();
                TransferObject criterio = new ConsignatariaTransferObject();
                criterio.setAttribute(Columns.CSA_ATIVO, csaAtivo);
                if (dados.csaCodigo != null) {
                    criterio.setAttribute(Columns.CSA_CODIGO, dados.csaCodigo);
                }
                if (dados.csaIdentificador != null) {
                    criterio.setAttribute(Columns.CSA_IDENTIFICADOR, dados.csaIdentificador);
                }
                if (dados.csaNome != null) {
                    criterio.setAttribute(Columns.CSA_NOME + CodedValues.OR_KEY + Columns.CSA_NOME_ABREV, CodedValues.LIKE_MULTIPLO + dados.csaNome + CodedValues.LIKE_MULTIPLO);
                }
                if (dados.csaNatureza != null) {
                    criterio.setAttribute(Columns.CSA_NCA_NATUREZA, dados.csaNatureza);
                }
                if (dados.csaCnvCodVerba != null) {
                    criterio.setAttribute(Columns.CNV_COD_VERBA, CodedValues.LIKE_MULTIPLO + dados.csaCnvCodVerba + CodedValues.LIKE_MULTIPLO);
                }
                final List<String> filter = List.of("csa_identificador", "csa_nome", "csa_cnpj", "nca_descricao", "csa_data_expiracao", "csa_resp_telefone", "csa_contato", "csa_email", "csa_ativo");

                ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
                List<TransferObject> lstCsa = consignatariaController.lstConsignatarias(criterio, responsavel);

                return Response.status(Response.Status.OK).entity(transformTOs(lstCsa, filter)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            } catch (ConsignatariaControllerException e) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = e.getMessage();
                LOG.error(e.getMessage(), e);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }
        }
    }
}
