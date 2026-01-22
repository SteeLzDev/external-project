package com.zetra.econsig.webservice.rest.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConvenioTransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.ReservaMargemHelper;
import com.zetra.econsig.helper.consignacao.SolicitacaoServidorHelper;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.financeiro.CDCHelper;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.InserirSolicitacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.CodigoAutorizacaoRestRequest;
import com.zetra.econsig.webservice.rest.request.ConsignacaoRestRequest;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;
import com.zetra.econsig.webservice.rest.request.ServicoRestRequest;
import com.zetra.econsig.webservice.rest.request.ServicosSolicitacaoRestRequest;

import jakarta.mail.MessagingException;
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
 * <p>Title: SimularConsignacaoService</p>
 * <p>Description: Serviço REST para simulação.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/simular")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class SimularConsignacaoService extends RestService {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SimularConsignacaoService.class);

    private static final String FORMATO_CHAVE_PARAM_SVC_CSA = "%s:%s";

    // Subdiretório onde os arquivos temporários são armazenados.
    private static final String SUBDIR_ARQUIVOS_TEMPORARIOS = "temp" + File.separator + "upload";

    // Filtro de campos para retorno das informações de prazo
    private static final List<String> FILTRO_CAMPOS_PRAZO = List.of("prz_codigo", "prz_ativo", "prz_vlr");

	@Context
	SecurityContext securityContext;

	@POST
	@Secured
	@Path("/servicos")
	public Response servicos(ServicoRestRequest dados) {
		final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

		final boolean temPermissaoSimulacao = responsavel.temPermissao(CodedValues.FUN_SIM_CONSIGNACAO);
	    final boolean temPermissaoReserva   = responsavel.temPermissao(CodedValues.FUN_RES_MARGEM);
	    final boolean temPermissaoSolicitacao = responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO);

        if (dados == null) {
            dados = new ServicoRestRequest();
        }

        String orgCodigo = dados.orgCodigo;
        String csaCodigo = dados.csaCodigo;

        if (responsavel.isSer()) {
            orgCodigo = responsavel.getOrgCodigo();
        } else if (responsavel.isCsaCor()) {
            csaCodigo = responsavel.getCsaCodigo();
        } else if (responsavel.isOrg()) {
            orgCodigo = responsavel.getOrgCodigo();
        }

	    try {
	        final SimulacaoController simulacaoController = ApplicationContextProvider.getApplicationContext().getBean(SimulacaoController.class);
	        final ConvenioController convenioController = ApplicationContextProvider.getApplicationContext().getBean(ConvenioController.class);
            final ParametroController parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);

            final String nseCodigo = TextHelper.isNull(dados.nseCodigo) ? CodedValues.NSE_EMPRESTIMO : dados.nseCodigo;
		    final List<ServicosSolicitacaoRestRequest> servicos = new ArrayList<>();


            final List<TransferObject> servicosSolicitacao;
            if (responsavel.isSer()) {
                // Lista de serviços que o servidor pode solicitar
                servicosSolicitacao = SolicitacaoServidorHelper.lstServicos(orgCodigo, null, csaCodigo, temPermissaoSimulacao, temPermissaoReserva, temPermissaoSolicitacao, (!TextHelper.isNull(nseCodigo) && CodedValues.NSE_EMPRESTIMO.equals(nseCodigo)), nseCodigo, responsavel);
            } else if (responsavel.isCsaCor()) {
                final List<TransferObject> lstConvenio = convenioController.lstCnvEntidade(responsavel.getCodigoEntidade(), responsavel.getTipoEntidade(), "reservar", responsavel);
                final List<TransferObject> lstServico = TextHelper.groupConcat(lstConvenio, new String[] { Columns.SVC_DESCRICAO, Columns.SVC_CODIGO }, new String[] { Columns.CNV_COD_VERBA }, ",", true, true);
                servicosSolicitacao = lstServico.stream().filter(servico -> servico.getAttribute(Columns.NSE_CODIGO).equals(nseCodigo)).collect(Collectors.toList());
            } else {
		        // Lista todos os serviços do convênio
		        final List<TransferObject> convenio = convenioController.lstCnvEntidade(responsavel.getCodigoEntidade(), responsavel.getTipoEntidade(), null, responsavel);

		        servicosSolicitacao = new ArrayList<>();
		        // Seleciona os serviços da natureza informada
		        if ((convenio != null) && !convenio.isEmpty()) {
		            for (final TransferObject cnv : convenio) {
                        if (!TextHelper.isNull(cnv.getAttribute(Columns.NSE_CODIGO)) && cnv.getAttribute(Columns.NSE_CODIGO).equals(nseCodigo)) {
                            servicosSolicitacao.add(cnv);
                        }
                    }
		        }
		    }

            //DESENV-6584: se a simulação for agrupada por natureza de serviço, busca os Min e Max de parâmetros de simulação
            //             mínimos e máximos, respectivamente, entre todos os serviços da natureza.
            final boolean simuladorPorNatureza = ParamSist.paramEquals(CodedValues.TPC_SIMULADOR_AGRUPADO_NATUREZA_SERVICO, CodedValues.TPC_SIM, responsavel);
            String maxPrazoNse = null;
            BigDecimal maxAdeVlrNse = null;
            BigDecimal minAdeVlrNse = null;
            if (simuladorPorNatureza) {
                // busca Min e Max para a natureza para param svc csa
                final List<String> tpsCodigos = new ArrayList<>();
                tpsCodigos.add(CodedValues.TPS_VLR_MINIMO_CONTRATO);
                tpsCodigos.add(CodedValues.TPS_VLR_MAXIMO_CONTRATO);
                final List<String> svcCodigos = servicosSolicitacao.stream().map(s -> s.getAttribute(Columns.SVC_CODIGO).toString()).toList();
                final List<TransferObject> paramSvcCsa = parametroController.selectParamSvcCsa(svcCodigos, Stream.ofNullable(csaCodigo).collect(Collectors.toList()), tpsCodigos, false, responsavel);
                for (final TransferObject vo : paramSvcCsa) {
                    try {
                        if (CodedValues.TPS_VLR_MINIMO_CONTRATO.equals(vo.getAttribute(Columns.TPS_CODIGO)) && !TextHelper.isNull(vo.getAttribute(Columns.PSC_VLR))) {
                            minAdeVlrNse = new BigDecimal(NumberHelper.reformat(vo.getAttribute(Columns.PSC_VLR).toString(), NumberHelper.getLang(), "en"));
                        }

                        if (CodedValues.TPS_VLR_MAXIMO_CONTRATO.equals(vo.getAttribute(Columns.TPS_CODIGO)) && !TextHelper.isNull(vo.getAttribute(Columns.PSC_VLR))) {
                            maxAdeVlrNse = new BigDecimal(NumberHelper.reformat(vo.getAttribute(Columns.PSC_VLR).toString(), NumberHelper.getLang(), "en"));
                        }
                    } catch (ParseException ex) {
                        LOG.error(ex.getMessage(), ex);
                        throw new ViewHelperException(ex);
                    }
                }

                //busca Min e Max para a natureza para param svc cse
                List<String> tpsCodigosCse = new ArrayList<>();
                tpsCodigosCse.add(CodedValues.TPS_MAX_PRAZO);
                tpsCodigosCse.add(CodedValues.TPS_VLR_MAXIMO_CONTRATO);

                final List<TransferObject> lstMaxPse = parametroController.listaLimitesMaxMinParamSvcCseNse(tpsCodigosCse, nseCodigo, false, responsavel);
                if ((lstMaxPse != null) && !lstMaxPse.isEmpty()) {
                    final List<TransferObject> applyFilterMaxPrazo = lstMaxPse.stream().filter(to -> CodedValues.TPS_MAX_PRAZO.equals(to.getAttribute(Columns.PSE_TPS_CODIGO))).collect(Collectors.toList());
                    maxPrazoNse = ((applyFilterMaxPrazo != null) && !applyFilterMaxPrazo.isEmpty() &&
                                        !TextHelper.isNull(applyFilterMaxPrazo.get(0).getAttribute(Columns.PSE_VLR))) ?
                                        applyFilterMaxPrazo.get(0).getAttribute(Columns.PSE_VLR).toString().split("[.]")[0] : null;

                    if(TextHelper.isNull(maxAdeVlrNse)) {
                        final List<TransferObject> applyFilterMaxAdeVlr = lstMaxPse.stream().filter(to -> CodedValues.TPS_VLR_MAXIMO_CONTRATO.equals(to.getAttribute(Columns.PSE_TPS_CODIGO))).collect(Collectors.toList());
                        if ((applyFilterMaxAdeVlr != null) && !applyFilterMaxAdeVlr.isEmpty()) {
                            final Object maxAdeVlrObjct = applyFilterMaxAdeVlr.get(0).getAttribute(Columns.PSE_VLR);
                            if (!TextHelper.isNull(maxAdeVlrObjct)) {
                                maxAdeVlrNse = (BigDecimal) maxAdeVlrObjct;
                            }
                        } else {
                            final Object maxAdeVlrObjct = ParamSist.getInstance().getParam(CodedValues.TPC_VLR_PADRAO_MAXIMO_CONTRATO, responsavel);
                            if (!TextHelper.isNull(maxAdeVlrObjct)) {
                                maxAdeVlrNse = new BigDecimal(maxAdeVlrObjct.toString().replace(',','.'));
                            }
                        }
                    }
                }

                if(TextHelper.isNull(minAdeVlrNse)) {
                    tpsCodigosCse = new ArrayList<>();
                    tpsCodigosCse.add(CodedValues.TPS_VLR_MINIMO_CONTRATO);

                    final List<TransferObject> lstMinPse = parametroController.listaLimitesMaxMinParamSvcCseNse(tpsCodigosCse, nseCodigo, true, responsavel);
                    if ((lstMinPse != null) && !lstMinPse.isEmpty()) {
                        final List<TransferObject> applyFilterMinAdeVlr = lstMinPse.stream().filter(to -> CodedValues.TPS_VLR_MINIMO_CONTRATO.equals(to.getAttribute(Columns.PSE_TPS_CODIGO))).collect(Collectors.toList());

                        Object minAdeVlrObjct = ((applyFilterMinAdeVlr != null) && !applyFilterMinAdeVlr.isEmpty() &&
                                                 !TextHelper.isNull(applyFilterMinAdeVlr.get(0).getAttribute(Columns.PSE_VLR))) ?
                                                 applyFilterMinAdeVlr.get(0).getAttribute(Columns.PSE_VLR) : null;

                        if (!TextHelper.isNull(minAdeVlrObjct)) {
                            minAdeVlrNse = (BigDecimal) minAdeVlrObjct;
                        } else {
                            minAdeVlrObjct = ParamSist.getInstance().getParam(CodedValues.TPC_VLR_PADRAO_MINIMO_CONTRATO, responsavel);

                            if (!TextHelper.isNull(minAdeVlrObjct)) {
                                minAdeVlrNse = new BigDecimal(minAdeVlrObjct.toString().replace(',','.'));
                            } else {
                                minAdeVlrNse = BigDecimal.valueOf(0.99);
                            }
                        }
                    }
                }
            }

            for (final TransferObject nse : servicosSolicitacao) {
                final String svcCodigoRegistro = nse.getAttribute(Columns.SVC_CODIGO).toString();
                final ParamSvcTO params = ParamSvcTO.getParamSvcTO(svcCodigoRegistro,  responsavel);
                final String maxPrazo = params.getTpsMaxPrazo();

                final ServicosSolicitacaoRestRequest item = new ServicosSolicitacaoRestRequest();
                item.svc_codigo = svcCodigoRegistro;
                item.svc_descricao = (String) nse.getAttribute(Columns.SVC_DESCRICAO);
                item.svc_identificador = (String) nse.getAttribute(Columns.SVC_IDENTIFICADOR);
                item.nse_codigo = !responsavel.isCsaCor() ? (String) nse.getAttribute(Columns.SVC_NSE_CODIGO) : (String) nse.getAttribute(Columns.NSE_CODIGO);
                item.indeterminado = (!TextHelper.isNull(maxPrazo) && (Integer.parseInt(maxPrazo) == 0));
                item.prazos = transformTOs(simulacaoController.findPrazoAtivoByServico(svcCodigoRegistro, responsavel), FILTRO_CAMPOS_PRAZO);
                item.tipoValor = params.getTpsTipoVlr();
                item.valorPadrao = params.getTpsAdeVlr();
                item.maxPrazo = params.getTpsMaxPrazo();
                item.prazoFixo = params.isTpsPrazoFixo();
                item.valorFixo = !params.isTpsAlteraAdeVlr();
                item.exigeTaxaJuros = params.isTpsVlrLiqTaxaJuros();
                item.exigeInfoBancaria = params.isTpsInfBancariaObrigatoria();
                item.exigeSeguroPrestamista = params.isTpsExigeSeguroPrestamista();
                item.possuiCorrecaoValorPresente = params.isTpsPossuiCorrecaoValorPresente();
                item.cadastraValorTac = params.isTpsCadValorTac();
                item.cadastraValorIof = params.isTpsCadValorIof();
                item.cadastraValorLiquidoLiberado = params.isTpsCadValorLiquidoLiberado();
                item.cadastraValorMensalidadeVinc = params.isTpsCadValorMensalidadeVinc();
                item.validaDataNascimentoReserva = params.isTpsValidarDataNascimentoNaReserva();
                item.senhaServidorObrigatoriaReserva = parametroController.senhaServidorObrigatoriaReserva(responsavel.getRseCodigo(), svcCodigoRegistro, csaCodigo, responsavel);
                item.exibeCidadeConfirmacaoSolicitacao = params.getTpsExibeCidadeConfirmacaoSolicitacao();
                item.incMargem = params.getTpsIncideMargem().toString();
                item.exigeReconhecimentoFacialServidorSolicitacao = params.isTpsRequerReconhecimentoFacilServidor();

                if (simuladorPorNatureza) {
                    if (TextHelper.isNum(maxPrazoNse)) {
                        item.maxPrazoNse = maxPrazoNse;
                    } else {
                        item.maxPrazoNse = item.maxPrazo;
                    }
                    if (minAdeVlrNse != null) {
                        item.minAdeVlr = minAdeVlrNse;
                    }
                    if (maxAdeVlrNse != null) {
                        item.maxAdeVlr = maxAdeVlrNse;
                    } else {
                        item.maxAdeVlr = getRseMargem(svcCodigoRegistro, csaCodigo, responsavel);
                    }
                }

                servicos.add(item);
            }

		    return Response.status(Response.Status.OK).entity(servicos).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();

	    } catch (ViewHelperException | ParametroControllerException | SimulacaoControllerException | ConvenioControllerException | ServidorControllerException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }
	}

	@POST
	@Secured
	@Path("/ranking")
	public Response simular(ConsignacaoRestRequest dados) {
		final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_SIM_CONSIGNACAO), null);
        if (resultadoValidacao != null) {
            return resultadoValidacao;
        }

        if (dados == null) {
            dados = new ConsignacaoRestRequest();
        }
		// Faz as validações
		try {
			validaValorAutorizacao(dados, responsavel);
			validaCodigoVerba(dados, responsavel);
		} catch (final ZetraException e) {
		    LOG.error(e.getMessage(), e);
			return genericError(e);
		}

		if ((dados.adePrazo == null) || (dados.adePrazo <= 0)) {
			return genericError(new ZetraException("mensagem.informe.ade.prazo", responsavel));
		}

		// Parâmetros de serviços necessários para a operação
        final ParamSvcTO paramSvcCse = ParamSvcTO.getParamSvcTO(dados.svcCodigo, responsavel);

        // Verifica se servidor está bloqueado, caso parâmetro esteja habilitado
        if (responsavel.isSer() && ParamSist.paramEquals(CodedValues.TPC_PERMITE_SER_BLOQUEADO_SIMULAR_SEM_CONCLUIR, CodedValues.TPC_NAO, responsavel)) {
            final String srsCodigo = responsavel.getSrsCodigo();
            if (CodedValues.SRS_INATIVOS.contains(srsCodigo)) {
                // Servidor excluído não pode fazer novas reservas.
                return genericError(new ZetraException("mensagem.servidorExcluido", responsavel));
            } else // Verifica se servidor bloqueado pode fazer nova reserva
            if (CodedValues.SRS_BLOQUEADOS.contains(srsCodigo) && !paramSvcCse.isTpsPermiteIncluirAdeRseBloqueado()) {
                return genericError(new ZetraException("mensagem.servidorBloqueado", responsavel));
            }
        }

		final List<TransferObject> retorno = new ArrayList<>();

		try {
            final String adePeriodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);

            final ParametroController parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);
            final SimulacaoController simulacaoController = ApplicationContextProvider.getApplicationContext().getBean(SimulacaoController.class);

            // Faz cache do parâmetro de SVC/CSA TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO
            final Map<String, String> paramSvcCsaInformacoes = new HashMap<>();
            final List<TransferObject> lstParamSvcCsa = parametroController.selectParamSvcCsa(Arrays.asList(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO), responsavel);
            for (final TransferObject paramSvcCsa : lstParamSvcCsa) {
                if ((paramSvcCsa != null) && (paramSvcCsa.getAttribute(Columns.PSC_VLR) != null)) {
                    final String svcCodigoParam = paramSvcCsa.getAttribute(Columns.PSC_SVC_CODIGO).toString();
                    final String csaCodigoParam = paramSvcCsa.getAttribute(Columns.PSC_CSA_CODIGO).toString();
                    // N = Nada obrigatório | E = Endereço obrigatório | C = Celular obrigatório | EC = Endereço e celular obrigatórios
                    final String pscVlr = (!paramSvcCsa.getAttribute(Columns.PSC_VLR).toString().isEmpty()) ? paramSvcCsa.getAttribute(Columns.PSC_VLR).toString() : "";
                    paramSvcCsaInformacoes.put(String.format(FORMATO_CHAVE_PARAM_SVC_CSA, svcCodigoParam, csaCodigoParam), pscVlr);
                }
            }

            final Map<String, TransferObject> cacheContatoCsa = new HashMap<>();
            final ConsignatariaController csaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
            final List<TransferObject> lstContatoCsa = csaController.listaCsaPermiteContato(null, responsavel);
            if ((lstContatoCsa != null) && !lstContatoCsa.isEmpty() ) {
                for (final TransferObject contatoCsa : lstContatoCsa) {
                    final String csaCodigoContato = contatoCsa.getAttribute(Columns.CSA_CODIGO).toString();
                    cacheContatoCsa.put(csaCodigoContato, contatoCsa);
                }
            }

            // Realiza a simulação
			List<TransferObject> simulacao = simulacaoController.simularConsignacao(dados.svcCodigo, responsavel.getOrgCodigo(),
					responsavel.getRseCodigo(), dados.adeVlr, dados.valorLiberado,
					Short.parseShort(dados.adePrazo != null ? dados.adePrazo.toString() : "0"), null, true,
					adePeriodicidade, responsavel);

			// Verifica se pode mostrar margem
	        final boolean permiteSimularSemMargem = ParamSist.paramEquals(CodedValues.TPC_PERMITE_SERVIDOR_SIMULAR_SEM_MARGEM, CodedValues.TPC_SIM, responsavel);
			final int qtdeConsignatariasSimulacao = paramSvcCse.getTpsQtdCsaPermitidasSimulador();
			final Short incMargem = paramSvcCse.getTpsIncideMargem();
			final MargemDisponivel margemDisponivel = new MargemDisponivel(responsavel.getRseCodigo(), null, dados.svcCodigo, incMargem, responsavel);
			final BigDecimal rseMargemRest = margemDisponivel.getMargemRestante();

			if (((dados.adeVlr != null) && (BigDecimal.ZERO.compareTo(dados.adeVlr) < 0)) && ((rseMargemRest.compareTo(dados.adeVlr) < 0) && !permiteSimularSemMargem)) {
            	return genericError(new ZetraException("mensagem.erro.margem.valor.prestacao.maior.margem.disponivel", responsavel));
            }
			simulacao = simulacaoController.selecionarLinhasSimulacao(simulacao, responsavel.getRseCodigo(), rseMargemRest,
					qtdeConsignatariasSimulacao, false, true, responsavel);

			// Retornar o prazo em horas para o fim do leilão
            final String horasEncerramentoLeilao = ParamSist.getInstance().getParam(CodedValues.TPC_MINUTOS_FECHAMENTO_LEILAO_VIA_SIMULACAO, responsavel).toString();

            // Retornar se o campo de telefone de conf. deve ser exibido
            final boolean exigeTelConfirmacao = ParamSist.paramEquals(CodedValues.TPC_REQUER_TEL_SER_SOLIC_EMPRESTIMO, CodedValues.TPC_SIM, responsavel);
            final boolean exibeTelConfirmacao = ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel);
            final boolean temCET = ParamSist.getBoolParamSist(CodedValues.TPC_TEM_CET, responsavel);

            for (final TransferObject coeficiente : simulacao) {
                if ((Boolean) coeficiente.getAttribute("OK")) {
                    final String svcCodigoCft = coeficiente.getAttribute(Columns.SVC_CODIGO).toString();
                    final String csaCodigoCft = coeficiente.getAttribute(Columns.CSA_CODIGO).toString();

                    final ParamSvcTO paramSvcCseEspec = ParamSvcTO.getParamSvcTO(svcCodigoCft, responsavel);

                    coeficiente.setAttribute("exibeCidadeConfirmacaoSolicitacao", paramSvcCseEspec.getTpsExibeCidadeConfirmacaoSolicitacao());
                    coeficiente.setAttribute("exigenciaConfirmacaoLeituraServidor", paramSvcCseEspec.isTpsExigenciaConfirmacaoLeituraServidor());
                    coeficiente.setAttribute("horasEncerramentoLeilao", !TextHelper.isNull(horasEncerramentoLeilao) ? String.valueOf(Integer.valueOf(horasEncerramentoLeilao) / 60) : horasEncerramentoLeilao);
                    coeficiente.setAttribute("exigeTelConfirmacao", exigeTelConfirmacao);
                    coeficiente.setAttribute("exibeTelConfirmacao", exibeTelConfirmacao);

                    // Busca o parâmetro TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO do cache
                    final String pscVlr = paramSvcCsaInformacoes.get(String.format(FORMATO_CHAVE_PARAM_SVC_CSA, svcCodigoCft, csaCodigoCft));
                    if (!TextHelper.isNull(pscVlr)) {
                        // N = Nada obrigatório | E = Endereço obrigatório | C = Celular obrigatório | EC = Endereço e celular obrigatórios
                        coeficiente.setAttribute("exigeCadastroEnderecoSolicitacaoEmprestimo", pscVlr);
                    }

                    final BigDecimal iof = (BigDecimal) coeficiente.getAttribute("IOF");
                    if (iof != null) {
                        coeficiente.setAttribute("iof", iof.toString());
                    }

                    final BigDecimal iva = (BigDecimal) coeficiente.getAttribute("IVA");
                    if (iva != null) {
                        coeficiente.setAttribute("iva", iva.toString());
                    }

                    if (!temCET) {
                        final BigDecimal tac = (BigDecimal) coeficiente.getAttribute("TAC_FINANCIADA");
                        if (tac != null) {
                            coeficiente.setAttribute("tacFinanciada", tac.toString());
                        }
                    }

                    final BigDecimal cftVlr = (BigDecimal) coeficiente.getAttribute(Columns.CFT_VLR);
                    if (cftVlr != null) {
                        // cftVlr = CET Mensal
                        coeficiente.setAttribute("cetAnual", CDCHelper.getStrTaxaEquivalenteAnual(NumberHelper.format(cftVlr.doubleValue(), NumberHelper.getLang(), 2, 8)));
                    }

                    if (temCET && ParamSist.paramEquals(CodedValues.TPC_HABILITA_CADASTRO_TAXA_JUROS_EDITAR_CET_MANUTENCAO_CSA, CodedValues.TPC_SIM, responsavel)) {
                        // Taxa de Juros Mensal
                        final BigDecimal cftVlrRef = !TextHelper.isNull(coeficiente.getAttribute(Columns.CFT_VLR_REF)) ? new BigDecimal(coeficiente.getAttribute(Columns.CFT_VLR_REF).toString()) : null;
                        final BigDecimal cftVlrRef79 =  !TextHelper.isNull(coeficiente.getAttribute("CFT_VLR_FUN_" + CodedValues.FUN_SIM_CONSIGNACAO)) ? new BigDecimal(coeficiente.getAttribute("CFT_VLR_FUN_" + CodedValues.FUN_SIM_CONSIGNACAO).toString()) : null;
                        final BigDecimal cftVlrRef303 =  !TextHelper.isNull(coeficiente.getAttribute("CFT_VLR_FUN_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO)) ? new BigDecimal(coeficiente.getAttribute("CFT_VLR_FUN_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO).toString()) : null;
                        final BigDecimal cftVlrRef532 =  !TextHelper.isNull(coeficiente.getAttribute("CFT_VLR_FUN_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE)) ? new BigDecimal(coeficiente.getAttribute("CFT_VLR_FUN_" + CodedValues.FUN_SOLICITAR_PORTABILIDADE).toString()) : null;
                        String taxaAnual = null;
                        if (!TextHelper.isNull(cftVlrRef)) {
                            taxaAnual = CDCHelper.getStrTaxaEquivalenteAnual(NumberHelper.format(cftVlrRef.doubleValue(), NumberHelper.getLang(), 2, 8));
                            coeficiente.setAttribute("taxaJurosMensal", cftVlrRef.toString());
                            coeficiente.setAttribute("taxaJurosAnual", NumberHelper.reformat(taxaAnual, NumberHelper.getLang(), "en"));
                        }
                        if (!TextHelper.isNull(cftVlrRef79)) {
                            taxaAnual = CDCHelper.getStrTaxaEquivalenteAnual(NumberHelper.format(cftVlrRef79.doubleValue(), NumberHelper.getLang(), 2, 8));
                            coeficiente.setAttribute("taxaJurosMensal79", cftVlrRef79.toString());
                            coeficiente.setAttribute("taxaJurosAnual79", NumberHelper.reformat(taxaAnual, NumberHelper.getLang(), "en"));
                        }
                        if (!TextHelper.isNull(cftVlrRef303)) {
                            taxaAnual = CDCHelper.getStrTaxaEquivalenteAnual(NumberHelper.format(cftVlrRef303.doubleValue(), NumberHelper.getLang(), 2, 8));
                            coeficiente.setAttribute("taxaJurosMensal303", cftVlrRef303.toString());
                            coeficiente.setAttribute("taxaJurosAnual303", NumberHelper.reformat(taxaAnual, NumberHelper.getLang(), "en"));
                        }
                        if (!TextHelper.isNull(cftVlrRef532)) {
                            taxaAnual = CDCHelper.getStrTaxaEquivalenteAnual(NumberHelper.format(cftVlrRef532.doubleValue(), NumberHelper.getLang(), 2, 8));
                            coeficiente.setAttribute("taxaJurosMensal532", cftVlrRef532.toString());
                            coeficiente.setAttribute("taxaJurosAnual532", NumberHelper.reformat(taxaAnual, NumberHelper.getLang(), "en"));
                        }
                    }

                    coeficiente.setAttribute("temCET", temCET);

                    // Realiza busca dos contatos da CSA
                    final TransferObject contatoCsa = cacheContatoCsa.get(csaCodigoCft);
                    if (contatoCsa != null) {
                        final Object csa_whatsapp = contatoCsa.getAttribute(Columns.CSA_WHATSAPP);
                		final Object csa_contato_tel = contatoCsa.getAttribute(Columns.CSA_TEL);
                        final Object csa_email_contato = contatoCsa.getAttribute(Columns.CSA_EMAIL_CONTATO);
                        final Object pcs_vlr = contatoCsa.getAttribute(Columns.PCS_VLR);
                        coeficiente.setAttribute("csa_whatsapp", csa_whatsapp != null ? csa_whatsapp.toString() : "");
                        coeficiente.setAttribute("csa_contato_tel", csa_contato_tel != null ? csa_contato_tel.toString() : "");
                        coeficiente.setAttribute("csa_email_contato", csa_email_contato != null ? csa_email_contato.toString() : "");
                        coeficiente.setAttribute("pcs_vlr", pcs_vlr != null ? pcs_vlr.toString() : "");
                    }

				    retorno.add(coeficiente);
				}
			}

		} catch (final Exception ex) {
		    LOG.error(ex.getMessage(), ex);
			return genericError(ex);
		}

		if (retorno.size() == 0) {
			return genericError(new ZetraException("mensagem.erro.nenhuma.consignataria.encontrada.prazo", responsavel));
		}

		return Response.status(Response.Status.OK).entity(transformTOs(retorno, null)).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
	}

	@POST
	@Secured
	@Path("/solicitar")
	public Response inserirSolicitacao(ConsignacaoRestRequest dados, @Context HttpServletRequest request) throws ZetraException {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_SOL_EMPRESTIMO), null);
        if (resultadoValidacao != null) {
            return resultadoValidacao;
        }
        responsavel.setFunCodigo(CodedValues.FUN_SOL_EMPRESTIMO);

		if (dados == null) {
			dados = new ConsignacaoRestRequest();
		}

		if (dados.adeCarencia == null) {
			dados.adeCarencia = 0;
		}

		if (TextHelper.isNull(dados.adeIdentificador)) {
			dados.adeIdentificador = ApplicationResourcesHelper.getMessage("rotulo.consignacao.identificador.solicitacao", responsavel);
		}

		if (((dados.cnvCodVerba == null) || "".equals(dados.cnvCodVerba))	&& ((dados.svcIdentificador == null) || "".equals(dados.svcIdentificador))) {
			return genericError(new ZetraException("mensagem.informe.verba.ou.servico", responsavel));
		}

        // Valida o convenio
        final TransferObject convenio;
        try {
            convenio = validaCodigoVerba(dados, responsavel);
        } catch (final ZetraException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }

		if (dados.adeVlr.doubleValue() <= 0) {
			try {
		        final ParametroController parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);
				if (!parametroController.permiteContratoValorNegativo(dados.csaCodigo, dados.svcCodigo, responsavel)) {
					return genericError(new ZetraException("mensagem.valorParcelaMenorIgualZero", responsavel));
				}
			} catch (final ParametroControllerException e) {
			    LOG.error(e.getMessage(), e);
				return genericError(e);
			}
		}

		if ((dados.adePrazo == null) || (dados.adePrazo <= 0)) {
			return genericError(new ZetraException("mensagem.informe.ade.prazo", responsavel));
		}

		if (dados.adePrazo < 0) {
			return genericError(new ZetraException("mensagem.qtdParcelasInvalida", responsavel));
		}

		boolean exigeTelefone = ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel);
        if (exigeTelefone && TextHelper.isNull(dados.serTelefone)) {
            return genericError(new ZetraException("mensagem.informe.servidor.telefone", responsavel));
        }

        exigeTelefone = ParamSist.paramEquals(CodedValues.TPC_REQUER_TEL_SER_SOLIC_EMPRESTIMO, CodedValues.TPC_SIM, responsavel);
        if (exigeTelefone && TextHelper.isNull(dados.serTelefoneSolicitacao)) {
            return genericError(new ZetraException("mensagem.informe.servidor.telefone.solicitacao", responsavel));
        }

		final boolean exigeSMSAutorizacao = ParamSist.getBoolParamSist(CodedValues.TPC_ENVIA_SMS_CODIGO_UNICO_AUTORIZACAO_CONSIGNACAO, responsavel);

        if (exigeSMSAutorizacao && !dados.otpValidado) {

            if (TextHelper.isNull(dados.otp)) {
                return genericError(new ZetraException("mensagem.erro.codigo.autorizacao.invalido", responsavel));
            }

            final CodigoAutorizacaoService validador = new CodigoAutorizacaoService();
            final CodigoAutorizacaoRestRequest req = new CodigoAutorizacaoRestRequest();
            req.codAutorizacao = dados.otp;

            final Response result = validador.validarCodigoAutorizacao(req, request, responsavel);

            if (result.getStatus() != 200) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ((Map<String, String>) result.getEntity()).get("mensagem");
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }
        }

		final boolean exigeMunicipioLotacao = ParamSist.paramEquals(CodedValues.TPC_REQUER_MUN_LOTACAO_SER_SOLIC_EMPRESTIMO, CodedValues.TPC_SIM, responsavel);
		if (exigeMunicipioLotacao && TextHelper.isNull(dados.rseMunicipioLotacao)) {
			return genericError(new ZetraException("mensagem.informe.servidor.municipio.lotacao", responsavel));
		}

		if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel) && TextHelper.isNull(dados.serTelefone)) {
            return genericError(new ZetraException("mensagem.informe.servidor.telefone", responsavel));
        }
		if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel) && TextHelper.isNull(dados.serCelular)) {
            return genericError(new ZetraException("mensagem.informe.servidor.celular", responsavel));
        }
		if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel) && TextHelper.isNull(dados.serEndereco)) {
            return genericError(new ZetraException("mensagem.informe.servidor.logradouro", responsavel));
        }
		if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel) && TextHelper.isNull(dados.serNro)) {
            return genericError(new ZetraException("mensagem.informe.servidor.numero", responsavel));
        }
		if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel) && TextHelper.isNull(dados.serComplemento)) {
            return genericError(new ZetraException("mensagem.informe.servidor.complemento", responsavel));
        }
		if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel) && TextHelper.isNull(dados.serBairro)) {
            return genericError(new ZetraException("mensagem.informe.servidor.bairro", responsavel));
        }
		if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel) && TextHelper.isNull(dados.serCidade)) {
            return genericError(new ZetraException("mensagem.informe.servidor.cidade", responsavel));
        }
		if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel) && TextHelper.isNull(dados.serUf)) {
            return genericError(new ZetraException("mensagem.informe.servidor.estado", responsavel));
        }
		if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel) && TextHelper.isNull(dados.serCep)) {
            return genericError(new ZetraException("mensagem.informe.servidor.cep", responsavel));
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NOME, responsavel) && TextHelper.isNull(dados.serNome)) {
            return genericError(new ZetraException("mensagem.informe.servidor.nome", responsavel));
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_IBAN, responsavel) && TextHelper.isNull(dados.iban)) {
            return genericError(new ZetraException("mensagem.informe.servidor.iban", responsavel));
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_SEXO, responsavel) && TextHelper.isNull(dados.serSexo)) {
            return genericError(new ZetraException("mensagem.informe.servidor.sexo", responsavel));
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO_IDT, responsavel) && TextHelper.isNull(dados.serNroIdt)) {
            return genericError(new ZetraException("mensagem.informe.servidor.identidade", responsavel));
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_IDT, responsavel) && TextHelper.isNull(dados.serDataIdt)) {
            return genericError(new ZetraException("mensagem.informe.servidor.data.emissao.identidade", responsavel));
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CPF, responsavel) && TextHelper.isNull(dados.serCpf)) {
            return genericError(new ZetraException("mensagem.informe.servidor.cpf", responsavel));
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NACIONALIDADE, responsavel) && TextHelper.isNull(dados.serNacionalidade)) {
            return genericError(new ZetraException("mensagem.informe.servidor.nacionalidade", responsavel));
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NATURALIDADE, responsavel) && TextHelper.isNull(dados.serCidNasc)) {
            return genericError(new ZetraException("mensagem.informe.servidor.naturalidade", responsavel));
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF_NASCIMENTO, responsavel) && TextHelper.isNull(dados.serUfNasc)) {
            return genericError(new ZetraException("mensagem.informe.servidor.uf.nascimento", responsavel));
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_SALARIO, responsavel) && TextHelper.isNull(dados.rseSalario)) {
            return genericError(new ZetraException("mensagem.informe.servidor.salario", responsavel));
        }
        if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_ADMISSAO, responsavel) && TextHelper.isNull(dados.rseDataAdmissao)) {
            return genericError(new ZetraException("mensagem.informe.servidor.data.admissao", responsavel));
        }

        final ParamSvcTO paramSvc = ParamSvcTO.getParamSvcTO(dados.svcCodigo, responsavel);
        final String paramExibeCampoCidade = paramSvc.getTpsExibeCidadeConfirmacaoSolicitacao();
        final String paramObrigaInformacoesServidorSolicitacao = paramSvc.getTpsObrigaInformacoesServidorSolicitacao();
        final boolean exibeConfiguracoesDadosServidorSimulacao = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CONF_DADOS_SER_SIMULADOR, CodedValues.TPC_SIM, responsavel);
        if (!TextHelper.isNull(paramExibeCampoCidade) &&
                (CodedValues.EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OBRIGATORIO.equals(paramExibeCampoCidade) ||
                (CodedValues.EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OBRIGATORIO_LEILAO.equals(paramExibeCampoCidade) && dados.iniciarLeilaoReverso))) {
            if (TextHelper.isNull(dados.cidCod)) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.cidade.assinatura.contrato", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }
        }
        if (exibeConfiguracoesDadosServidorSimulacao && (!TextHelper.isNull(paramObrigaInformacoesServidorSolicitacao) && ("C".equals(paramObrigaInformacoesServidorSolicitacao) || "EC".equals(paramObrigaInformacoesServidorSolicitacao))) && dados.iniciarLeilaoReverso) {
            if (TextHelper.isNull(dados.serCelular)) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.celular", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }
        }
        if (exibeConfiguracoesDadosServidorSimulacao && (!TextHelper.isNull(paramObrigaInformacoesServidorSolicitacao) && ("E".equals(paramObrigaInformacoesServidorSolicitacao) || "EC".equals(paramObrigaInformacoesServidorSolicitacao))) && dados.iniciarLeilaoReverso) {
            if (TextHelper.isNull(dados.serEndereco) || TextHelper.isNull(dados.serNro) || TextHelper.isNull(dados.serComplemento) || TextHelper.isNull(dados.serBairro) || TextHelper.isNull(dados.serCidade) || TextHelper.isNull(dados.serUf) || TextHelper.isNull(dados.serCep)) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.endereco", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }
        }

        final ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);
		/*
		 * Consulta a Margem do Servidor. A reserva em si é valida a parcela em
		 * comparação com a margem restante do servidor. Mas no caso em que a
		 * parcela for maior que a margem, esta consulta prévia elimina todo o
		 * restante do processo de reserva, aumentando a performance da
		 * operação. No caso positivo, aquele que a parcela é menor que a
		 * margem, o acréscimo do tempo de resposta é muito menor do que o ganho
		 * no caso negativo.
		 */
		try {
			if (!consultarMargemController.servidorTemMargem(responsavel.getRseCodigo(), dados.adeVlr, dados.svcCodigo, true,
					responsavel)) {
				return genericError(new ZetraException("mensagem.margemInsuficiente", responsavel));
			}
		} catch (final ServidorControllerException e) {
		    LOG.error(e.getMessage(), e);
			return genericError(e);
		}

		// Se Não informou nenhuma consignatária, retorna o código de erro.
        if (TextHelper.isNull(dados.csaIdentificador)) {
            return genericError(new ZetraException("mensagem.erro.nenhuma.consignataria.encontrada", responsavel));
        }

        TransferObject boleto = new CustomTransferObject();
        try {
            validaReserva((CustomTransferObject) convenio, dados, responsavel);
            final String adeCodigo = realizaReserva(dados, responsavel);
            boleto = buscaNovaAutorizacao(adeCodigo, responsavel);
            if (!dados.iniciarLeilaoReverso) {
                EnviaEmailHelper.enviaBoleto(boleto, responsavel);
            }
        } catch (final MessagingException e) {
           //se ocorrer um erro no envio de email ignorar e deixar o processo seguir
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }

		return Response.status(Response.Status.OK).entity(transformTO(boleto, null)).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
	}

	private void validaReserva(CustomTransferObject convenio, ConsignacaoRestRequest dados, AcessoSistema responsavel)
			throws ZetraException {

		final CustomTransferObject reserva = new CustomTransferObject();
		reserva.setAttribute("ADE_PRAZO", dados.adePrazo);
		reserva.setAttribute("ADE_CARENCIA", dados.adeCarencia);
		reserva.setAttribute("RSE_PRAZO", responsavel.getRsePrazo());
		reserva.setAttribute("ADE_VLR", dados.adeVlr);
		reserva.setAttribute("RSE_CODIGO", responsavel.getRseCodigo());
		reserva.setAttribute("SVC_CODIGO", dados.svcCodigo);
		reserva.setAttribute("CSE_CODIGO", CodedValues.CSE_CODIGO_SISTEMA);
		reserva.setAttribute("ADE_IDENTIFICADOR", dados.adeIdentificador);
		reserva.setAttribute("OPERACAO", CodedValues.OP_INSERIR_SOLICITACAO);

		// Passa "validaMargem" como "false" pois as operações que usam este
		// método (ReservarMargemCommand, InserirSolicitacaoCommand)
		// É fazem validação da margem, e as operações de renegociação e compra
		// (RenegociarConsignacaoCommand) já passavam false para
		// a validação, tornando esta desnecessária.
		ReservaMargemHelper.validaReserva(convenio, reserva, responsavel, false, false, true);
	}

	private void validaValorAutorizacao(ConsignacaoRestRequest dados, AcessoSistema responsavel) throws ZetraException {
		final BigDecimal adeVlr = dados.adeVlr;
		final BigDecimal vlrLiberado = dados.valorLiberado;

		if (adeVlr == null) {
			if (vlrLiberado == null) {
				throw new ZetraException("mensagem.informe.valor.parcela.ou.valor.liberado", responsavel);
			} else if (vlrLiberado.doubleValue() <= 0) {
            	throw new ZetraException("mensagem.erro.valor.liberado.maior.zero", responsavel);
            }
		}
	}

	private String realizaReserva(ConsignacaoRestRequest dados, AcessoSistema responsavel) throws ZetraException, IOException {
	    final String adePeriodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);

		// Monta o objeto de parâmetro da reserva
		final ReservarMargemParametros reservaParam = new ReservarMargemParametros();

        final ParametroController parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);
        final ConvenioController convenioController = ApplicationContextProvider.getApplicationContext().getBean(ConvenioController.class);
		final ConvenioTransferObject convenio = convenioController.findByPrimaryKey(dados.cnvCodigo, responsavel);

		reservaParam.setAdePeriodicidade(adePeriodicidade);
		reservaParam.setRseCodigo(responsavel.getRseCodigo());
		reservaParam.setAdeVlr(dados.adeVlr);
		reservaParam.setCorCodigo(dados.corCodigo);
		reservaParam.setAdePrazo(dados.adePrazo);
		reservaParam.setAdeCarencia(parametroController.calcularAdeCarenciaDiaCorteCsa(dados.adeCarencia, convenio.getCsaCodigo(), convenio.getOrgCodigo(), responsavel));
		reservaParam.setAdeIdentificador(dados.adeIdentificador);
		reservaParam.setCnvCodigo(dados.cnvCodigo);
		reservaParam.setAdeIndice(dados.adeIndice);
		reservaParam.setAdeVlrTac(dados.adeVlrTac);
		reservaParam.setAdeVlrIof(dados.adeVlrIof);
		reservaParam.setExigenciaConfirmacaoLeitura(String.valueOf(dados.confirmacaoLeitura));
		final BigDecimal adeVlrLiquido = (dados.adeVlrLiquido != null) ? dados.adeVlrLiquido : dados.valorLiberado;
		reservaParam.setAdeVlrLiquido(adeVlrLiquido);
		reservaParam.setAdeVlrMensVinc(dados.adeVlrMensVinc);
		reservaParam.setSerAtivo(Boolean.TRUE);
		reservaParam.setCnvAtivo(Boolean.TRUE);
		reservaParam.setSerCnvAtivo(Boolean.TRUE);
		reservaParam.setSvcAtivo(Boolean.TRUE);
		reservaParam.setCsaAtivo(Boolean.TRUE);
		reservaParam.setOrgAtivo(Boolean.TRUE);
		reservaParam.setEstAtivo(Boolean.TRUE);
		reservaParam.setCseAtivo(Boolean.TRUE);
		reservaParam.setValidar(Boolean.FALSE);
		reservaParam.setAcao("RESERVAR");
		reservaParam.setAdeTaxaJuros(dados.adeTaxaJuros);
		reservaParam.setPermitirValidacaoTaxa(Boolean.TRUE);
		// reservaParam.setCftCodigo(cft_codigo);
		reservaParam.setCdeVlrLiberado(dados.valorLiberado);
		// reservaParam.setCdeRanking(ranking);
		reservaParam.setCdeTxtContato("");
		reservaParam.setAdeBanco(dados.numBanco);
		reservaParam.setAdeAgencia(dados.numAgencia);
		reservaParam.setAdeConta(dados.numConta);
		reservaParam.setComSerSenha(Boolean.FALSE);
		reservaParam.setValidaAnexo(dados.anexos != null);
		reservaParam.setDtjCodigo(dados.dtjCodigo);

		if (dados.anexos != null) {
			final String hashDir = UUID.randomUUID().toString();
			final byte[] arq = Base64.decodeBase64(dados.anexos[0].get("data"));
			final String path = ParamSist.getDiretorioRaizArquivos()
			        + File.separatorChar + SUBDIR_ARQUIVOS_TEMPORARIOS
			        + File.separatorChar + "anexo"
			        + File.separatorChar + hashDir
			        + File.separatorChar + dados.anexos[0].get("nome");
			final File arquivoAnexo = new File(path);

			if (arquivoAnexo.exists()) {
				throw new AutorizacaoControllerException("mensagem.erro.anexo.ja.existe", responsavel);
			}

			FileUtils.writeByteArrayToFile(arquivoAnexo, arq);
			reservaParam.setAnexo(arquivoAnexo);
			reservaParam.setIdAnexo(hashDir);
		}

		if (!TextHelper.isNull(dados.serTelefoneSolicitacao)) {
            // telefone informado na solicitação deve ser salvo como dado de autorização TDA_SOLICITACAO_TEL_SERVIDOR
            reservaParam.setTdaTelSolicitacaoSer(dados.serTelefoneSolicitacao);
        }

		//Parâmetro que insere ou Não a solicitação no leilão reverso.
		if (dados.iniciarLeilaoReverso) {
		    reservaParam.setSimulacaoPorAdeVlr(dados.simulacaoPorAdeVlr);
			reservaParam.setIniciarLeilaoReverso(true);
		}

		// cidade da ade
		if (dados.cidCod != null) {
		    reservaParam.setCidCodigo(dados.cidCod);
		}

		// Monta dados do servidor para ser alterado
		final String serCodigo = responsavel.getSerCodigo();
		final ServidorTransferObject servidorUpd = new ServidorTransferObject(serCodigo);
		if (dados.serEndereco != null) {
		    servidorUpd.setSerEnd(dados.serEndereco);
		}
		if (dados.serComplemento != null) {
		    servidorUpd.setSerCompl(dados.serComplemento);
		}
		if (dados.serBairro != null) {
		    servidorUpd.setSerBairro(dados.serBairro);
		}
		if (dados.serCidade != null) {
		    servidorUpd.setSerCidade(dados.serCidade);
		}
		if (dados.serUf != null) {
		    servidorUpd.setSerUf(dados.serUf);
		}
		if (dados.serCep != null) {
		    servidorUpd.setSerCep(dados.serCep);
		}
		if (dados.serNro != null) {
		    servidorUpd.setSerNro(dados.serNro);
		}
		if (!TextHelper.isNull(dados.serNome) && ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NOME, responsavel)) {
            servidorUpd.setSerNome(dados.serNome);
        }
		if (!TextHelper.isNull(dados.serSexo) && ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_SEXO, responsavel)) {
            servidorUpd.setSerSexo(dados.serSexo);
        }
		if (!TextHelper.isNull(dados.serNroIdt) && ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO_IDT, responsavel)) {
            servidorUpd.setSerNroIdt(dados.serNroIdt);
        }
		if (!TextHelper.isNull(dados.serDataIdt) && ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_IDT, responsavel)) {
            servidorUpd.setSerDataIdt(dados.serDataIdt);
        }
		if (!TextHelper.isNull(dados.serCpf) && ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CPF, responsavel)) {
            servidorUpd.setSerCpf(dados.serCpf);
        }
        if (!dados.iniciarLeilaoReverso && !TextHelper.isNull(dados.serTelefone) && ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel)) {
            servidorUpd.setSerTel(dados.serTelefone);
        }
		if (!TextHelper.isNull(dados.serCelular) && ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel)) {
            servidorUpd.setSerCelular(dados.serCelular);
        }
		if (!TextHelper.isNull(dados.serNacionalidade) && ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NACIONALIDADE, responsavel)) {
            servidorUpd.setSerNacionalidade(dados.serNacionalidade);
        }
		if (!TextHelper.isNull(dados.serCidNasc) && ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NATURALIDADE, responsavel)) {
            servidorUpd.setSerCidNasc(dados.serCidNasc);
        }
		if (!TextHelper.isNull(dados.serUfNasc) && ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF_NASCIMENTO, responsavel)) {
            servidorUpd.setSerUfNasc(dados.serUfNasc);
        }

		// Monta dados do registro servidor para ser alterado
		final Object rseMunicipioLotacao = dados.rseMunicipioLotacao;
		final Object iban = dados.iban;
		final Object rseSalario = dados.rseSalario;
		final Object rseDataAdmissao = dados.rseDataAdmissao;

		final RegistroServidorTO registroServidorUpd = new RegistroServidorTO(responsavel.getRseCodigo());

		if (rseMunicipioLotacao != null) {
            registroServidorUpd.setRseMunicipioLotacao((String) rseMunicipioLotacao);
        }
		if (!TextHelper.isNull(iban) && ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_IBAN, responsavel)) {
            registroServidorUpd.setRseAgenciaSalAlternativa((String) iban);
        }
		if (!TextHelper.isNull(rseSalario) && ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_SALARIO, responsavel)) {
            registroServidorUpd.setRseSalario((BigDecimal) rseSalario);
        }
		if (!TextHelper.isNull(rseDataAdmissao) && ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_ADMISSAO, responsavel)) {
            registroServidorUpd.setRseDataAdmissao((Timestamp) rseDataAdmissao);
        }

		// Faz a reserva de margem
		String adeCodigo;
		try {
	        final InserirSolicitacaoController inserirSolicitacaoController = ApplicationContextProvider.getApplicationContext().getBean(InserirSolicitacaoController.class);
	        final AutorizacaoController autorizacaoController = ApplicationContextProvider.getApplicationContext().getBean("autorizacaoController", AutorizacaoController.class);
			adeCodigo = inserirSolicitacaoController.solicitarReservaMargem(reservaParam, servidorUpd, registroServidorUpd,
					dados.svcCodigo, responsavel);
			// inclui ocorrência de operação
			autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_OPERACAO_REST,
					ApplicationResourcesHelper.getMessage("mensagem.informacao.insercao.solicitacao.rest", responsavel),
					responsavel);
		} catch (final AutorizacaoControllerException e) {
		    LOG.error(e.getMessage(), e);
			throw e;
		}

		return adeCodigo;
	}

	private BigDecimal getRseMargem(String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        final ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);
	    final List<MargemTO> lstMargens = consultarMargemController.consultarMargem(responsavel.getRseCodigo(), null, svcCodigo, csaCodigo, null, true, false, true, null, responsavel);

	    BigDecimal mrsMargemRest = BigDecimal.ZERO;
	    if ((lstMargens != null) && !lstMargens.isEmpty()) {
	        mrsMargemRest = lstMargens.get(0).getMrsMargemRest();
	    }

	    return mrsMargemRest;
	}
}
