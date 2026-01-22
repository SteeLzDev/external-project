package com.zetra.econsig.webservice.rest.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConvenioTransferObject;
import com.zetra.econsig.dto.parametros.RenegociarConsignacaoParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.consignacao.AlongarConsignacaoControllerBean;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.ConsignacaoRestRequest;

import jakarta.mail.MessagingException;
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
 * <p>Title: RenegociarConsignacaoService</p>
 * <p>Description: Serviço REST para renegociação.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/renegociar")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class RenegociarConsignacaoService extends RestService {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RenegociarConsignacaoService.class);

    private static final List<String> filter = Arrays.asList("ade_numero", "ade_vlr", "ade_data", "ade_carencia", "ade_codigo", "cnv_cod_verba", "sad_codigo", "sad_descricao", "ade_prd_pagas", "csa_codigo", "ade_data", "csa_nome", "ade_identificador", "ade_ano_mes_ini", "ade_ano_mes_fim", "svc_codigo", "svc_descricao", "svc_identificador", "ade_vlr_liquido", "ade_prazo", "ade_taxa_juros", "ade_tipo_vlr", "cft_vlr", "cft_vlr_anual", "usu_login", "saldo_devedor", "tem_solicitacao_saldo_devedor", "tem_solicitacao_saldo_devedor_respondida", "permite_cadastro_saldo_devedor", "cnv_codigo");
    private static final String MEDIA_TYPE = MediaType.APPLICATION_JSON + "; charset=UTF-8";

	@Context
	SecurityContext securityContext;

	@POST
	@Secured
	@Path("/confirmar")
	public Response renegociar(ConsignacaoRestRequest dados) {
		final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

	    final boolean temPermissaoSolicitar = responsavel.temPermissao(CodedValues.FUN_SIMULAR_RENEGOCIACAO);
	    if(!temPermissaoSolicitar) {
	        return genericError(new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel));
	    }

		if (dados == null) {
			dados = new ConsignacaoRestRequest();
		}

		if (TextHelper.isNull(dados.adeCarencia)) {
            dados.adeCarencia = 0;
        }

        if (TextHelper.isNull(dados.adeIdentificador)) {
            dados.adeIdentificador = ApplicationResourcesHelper.getMessage("rotulo.consignacao.identificador.solicitacao", responsavel);
        }

		try {
            validaCamposObrigatorios(dados, responsavel);
        } catch (final ZetraException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }

        final ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);
		try {
			if (!consultarMargemController.servidorTemMargem(responsavel.getRseCodigo(), dados.adeVlr, dados.svcCodigo, true,
					responsavel)) {
				return genericError(new ZetraException("mensagem.margemInsuficiente", responsavel));
			}
		} catch (final ServidorControllerException e) {
		    LOG.error(e.getMessage(), e);
			return genericError(e);
		}

		String adeCodigo;
        TransferObject boleto = new CustomTransferObject();
        try {
            adeCodigo = realizaReserva(dados, responsavel);
            boleto = buscaNovaAutorizacao(adeCodigo, responsavel);
            EnviaEmailHelper.enviaBoleto(boleto, responsavel);
        } catch (final MessagingException e) {
           //se ocorrer um erro no envio de email ignorar e deixar o processo seguir
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }

		return Response.status(Response.Status.OK).entity(transformTO(boleto, null)).header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE).build();
	}

	private void validaCamposObrigatorios(ConsignacaoRestRequest dados, AcessoSistema responsavel) throws ZetraException {
		final BigDecimal adeVlr = dados.adeVlr;
		final BigDecimal vlrLiberado = dados.valorLiberado;

		if (adeVlr == null) {
			if (vlrLiberado == null) {
				throw new ZetraException("mensagem.informe.valor.parcela.ou.valor.liberado", responsavel);
			}
        	if (vlrLiberado.doubleValue() <= 0) {
        		throw new ZetraException("mensagem.erro.valor.liberado.maior.zero", responsavel);
        	}
		}

        if (((dados.cnvCodigo == null) && "".equals(dados.cnvCodigo)) && ((dados.svcCodigo == null) || "".equals(dados.svcCodigo))) {
            throw new ZetraException("mensagem.informe.verba.ou.servico", responsavel);
        }

        if(((dados.csaCodigo == null) && "".equals(dados.csaCodigo))){
            throw new ZetraException("mensagem.erro.nenhuma.consignataria.encontrada", responsavel);
        }

        if ((dados.adePrazo == null) || (dados.adePrazo <= 0)) {
            throw new ZetraException("mensagem.informe.ade.prazo", responsavel);
        }

        if (dados.adePrazo < 0) {
            throw new ZetraException("mensagem.qtdParcelasInvalida", responsavel);
        }

        if ((dados.adeCodigosRenegociacao == null) || dados.adeCodigosRenegociacao.isEmpty()) {
            throw new ZetraException("mensagem.erro.contratos.renegociados.obrigatorio.rest", responsavel);
        }
	}

	private String realizaReserva(ConsignacaoRestRequest dados, AcessoSistema responsavel) throws ZetraException {
        final String adePeriodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);

		final ConsignacaoDelegate consigDelegate = new ConsignacaoDelegate();

		final ConvenioController convenioController = ApplicationContextProvider.getApplicationContext().getBean(ConvenioController.class);
		ConvenioTransferObject convenio = null;
		if(!TextHelper.isNull(dados.cnvCodigo)) {
		    convenio = convenioController.findByPrimaryKey(dados.cnvCodigo, responsavel);
		} else {
		    convenio = convenioController.findByUniqueKey(dados.csaCodigo, dados.svcCodigo, responsavel.getOrgCodigo(), responsavel);
		}

        final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();
        renegociarParam.setTipo(responsavel.getTipoEntidade());
        renegociarParam.setRseCodigo(responsavel.getRseCodigo());
        renegociarParam.setAdeVlr(dados.adeVlr);
        renegociarParam.setCorCodigo(dados.corCodigo);
        renegociarParam.setAdePrazo(dados.adePrazo);
        renegociarParam.setAdeCarencia(dados.adeCarencia);
        renegociarParam.setAdeIdentificador(dados.adeIdentificador);
        renegociarParam.setCnvCodigo(convenio.getCnvCodigo());
        renegociarParam.setAdeIndice(dados.adeIndice);
        renegociarParam.setAdeVlrTac(dados.adeVlrTac);
        renegociarParam.setAdeVlrIof(dados.adeVlrIof);
        renegociarParam.setAdeVlrLiquido(dados.adeVlrLiquido);
        renegociarParam.setAdeVlrMensVinc(dados.adeVlrMensVinc);
        renegociarParam.setAdeTaxaJuros(dados.adeTaxaJuros);
        renegociarParam.setAdeCodigosRenegociacao(dados.adeCodigosRenegociacao);
        renegociarParam.setCftCodigo(dados.cftCodigo);
        renegociarParam.setCdeVlrLiberado(dados.valorLiberado);
        renegociarParam.setCdeTxtContato("");
        renegociarParam.setAdeBanco(dados.numBanco);
        renegociarParam.setAdeAgencia(dados.numAgencia);
        renegociarParam.setAdeConta(dados.numConta);
        renegociarParam.setAdePeriodicidade(adePeriodicidade);
        renegociarParam.setDtjCodigo(dados.dtjCodigo);
        renegociarParam.setComSerSenha(true);
        if (!TextHelper.isNull(dados.serTelefoneSolicitacao)) {
            renegociarParam.setTdaTelSolicitacaoSer(dados.serTelefoneSolicitacao);
        }

        renegociarParam.setCompraContrato(Boolean.FALSE);

        final SimulacaoController simulacaoController = ApplicationContextProvider.getApplicationContext().getBean(SimulacaoController.class);
        final List<TransferObject> simulacoes = simulacaoController.simularConsignacao(dados.svcCodigo, responsavel.getOrgCodigo(),
                responsavel.getRseCodigo(), dados.adeVlr, dados.valorLiberado,
                Short.parseShort(dados.adePrazo != null ? dados.adePrazo.toString() : "0"), null, true,
                adePeriodicidade, responsavel);

        if((simulacoes == null) || simulacoes.isEmpty()) {
            throw new ZetraException("mensagem.erro.contratos.renegociados.prazo.nao.cadastrado", responsavel);
        }

        for(final TransferObject simulacao : simulacoes) {
            final String csaCodigoSimu = (String) simulacao.getAttribute(Columns.CSA_CODIGO);
            final Short prazoSimu = (Short) simulacao.getAttribute(Columns.PRZ_VLR);

            if(TextHelper.isNull(dados.adeVlr) && !TextHelper.isNull(dados.valorLiberado) && convenio.getCsaCodigo().equals(csaCodigoSimu) && (Integer.valueOf(prazoSimu).equals(dados.adePrazo))) {
                renegociarParam.setAdeVlr((BigDecimal) simulacao.getAttribute("VLR_PARCELA"));
                break;
            } else if (!TextHelper.isNull(dados.adeVlr) && TextHelper.isNull(dados.valorLiberado) && convenio.getCsaCodigo().equals(csaCodigoSimu) && (Integer.valueOf(prazoSimu).equals(dados.adePrazo))) {
                renegociarParam.setAdeVlrLiquido((BigDecimal) simulacao.getAttribute("VLR_LIBERADO"));
                break;
            }
        }

        final AlongarConsignacaoControllerBean renegociarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(AlongarConsignacaoControllerBean.class);

		String adeCodigoNovo;
		try {
		    adeCodigoNovo = renegociarConsignacaoController.renegociar(renegociarParam, responsavel);
			// inclui ocorrência de operação
			consigDelegate.criaOcorrenciaADE(adeCodigoNovo, CodedValues.TOC_OPERACAO_REST, ApplicationResourcesHelper.getMessage("mensagem.informacao.insercao.renegociacao.rest", responsavel), responsavel);
		} catch (final AutorizacaoControllerException e) {
		    LOG.error(e.getMessage(), e);
			throw e;
		}
		return adeCodigoNovo;
	}

	@POST
    @Secured
    @Path("/listar")
    public Response listarPassiveisRenegociacao(ConsignacaoRestRequest dados) {
	    final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

	    if (dados == null) {
            dados = new ConsignacaoRestRequest();
        }

	    final String funCodigo = CodedValues.FUN_SIMULAR_RENEGOCIACAO;

        final boolean temPermissaoSolicitar = responsavel.temPermissao(funCodigo);
        if(!temPermissaoSolicitar) {
            return genericError(new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel));
        }

        if (TextHelper.isNull(dados.svcCodigo)) {
            return genericError(new ZetraException("mensagem.informe.svc.identificador", responsavel));
        }

        if (TextHelper.isNull(dados.csaCodigo)) {
            return genericError(new ZetraException("mensagem.informe.csa.identificador", responsavel));
        }

        final CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "simular_renegociacao");
        criterio.setAttribute(Columns.SVC_CODIGO, dados.svcCodigo);
        criterio.setAttribute(Columns.CSA_CODIGO, dados.csaCodigo);

        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigos.add(CodedValues.SAD_ESTOQUE);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
        if(ParamSist.getBoolParamSist(CodedValues.TPC_SUSPENDER_CONTRATO_PARCELA_REJEITADA_RETORNO, responsavel)) {
            sadCodigos.add(CodedValues.SAD_SUSPENSA);
        }

        final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);

        List<TransferObject> contratos;
        List<Map<String, Object>> contratosMap;

        // DESENV-20334
        final ParametroController parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);
        final List<Map<String, Object>> result = new ArrayList<>();
        CustomTransferObject paramSvcCse;

        try {
            paramSvcCse = parametroController.getParamSvcCse(dados.svcCodigo, CodedValues.TPS_QTDE_MAX_ADE_RENEGOCIACAO, responsavel);
            final Map<String, Object> tpsCse = new HashMap<>();
            tpsCse.put("tpsCse", !TextHelper.isNull(paramSvcCse) ? paramSvcCse.getAttribute(Columns.PSE_VLR).toString() : null);
            result.add(tpsCse);
        } catch (final ParametroControllerException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }

        try {
            contratos = pesquisarConsignacaoController.pesquisaAutorizacao(responsavel.getTipoEntidade(), responsavel.getUsuCodigo(), responsavel.getRseCodigo(), null, null, sadCodigos, null, -1, -1, criterio, responsavel);

            if (ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_PORTABILIDADE_CARTAO, responsavel)) {
                contratos.addAll(pesquisarConsignacaoController.lstContratosPortabilidadeCartao(dados.csaCodigo, responsavel));
            }

            contratosMap = transformTOs(contratos, filter);
            final Map<String, Object> contratosReturn = new HashMap<>();
            contratosReturn.put("contratos", contratosMap);
            result.add(contratosReturn);
        } catch (final AutorizacaoControllerException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }

        return Response.status(Response.Status.OK).entity(result).header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE).build();
	}
}
