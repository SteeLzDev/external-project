package com.zetra.econsig.webservice.rest.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.SaldoDevedorTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ParcelaControllerException;
import com.zetra.econsig.exception.SaldoDevedorControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.financeiro.CDCHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.CreateImageHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.job.process.ProcessaRelatorioBoletoADE;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.report.config.ConfigRelatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.CancelarConsignacaoController;
import com.zetra.econsig.service.consignacao.LiquidarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.parcela.ParcelaController;
import com.zetra.econsig.service.saldodevedor.SaldoDevedorController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.ListarConsigacaoRestRequest;
import com.zetra.econsig.webservice.rest.request.ParcelaRestRequest;
import com.zetra.econsig.webservice.rest.request.RecuperaDadosServidorRestRequest;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;
import com.zetra.econsig.webservice.rest.request.ResultadoConciliacaoRestRequest;
import com.zetra.econsig.webservice.rest.request.ShowRestRequest;
import com.zetra.econsig.webservice.rest.request.SolicitacaoSaldoDevedorRestRequest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

/**
 * <p>Title: ConsignacaoService</p>
 * <p>Description: Serviço REST para consignação.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/consignacao")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ConsignacaoService extends RestService {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsignacaoService.class);

    @Context
    private SecurityContext securityContext;

    @POST
    @Secured
    @Path("/cancelar/{id}")
    public Response cancelar(@PathParam("id") String adeCodigo) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        try {
            if (!responsavel.temPermissao(CodedValues.FUN_CANC_SOLICITACAO)) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }
            if (TextHelper.isNull(adeCodigo)) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }

            final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
            final TransferObject autdes = pesquisarConsignacaoController.findAutDesconto(adeCodigo, responsavel);

            if (!autdes.getAttribute(Columns.SAD_CODIGO).equals(CodedValues.SAD_SOLICITADO)) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.cancelar.situacao.situacao.nao.permite", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }

            final CancelarConsignacaoController cancelarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(CancelarConsignacaoController.class);
            cancelarConsignacaoController.cancelar(adeCodigo, null, responsavel);

            // verifica desbloqueio automático de consignatária no cancelamento manual
            final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
            consignatariaController.verificarDesbloqueioAutomaticoConsignatariaPorAdeCodigo(adeCodigo, responsavel);

            final ResponseRestRequest response = new ResponseRestRequest();
            response.mensagem = ApplicationResourcesHelper.getMessage("mensagem.cancelar.consignacao.concluido.sucesso", null);
            return Response.status(Response.Status.OK).entity(response).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();

        } catch (AutorizacaoControllerException | ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);

            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ex.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        }
    }

    @POST
    @Secured
    @Path("/cancelarDeferida/{id}")
    public Response cancelarConsignacaoDeferida(@PathParam("id") String adeCodigo) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        try {
            if ((responsavel.isSer() && !responsavel.temPermissao(CodedValues.FUN_CANC_SOLICITACAO)) || (!responsavel.isSer() && !responsavel.temPermissao(CodedValues.FUN_CANC_CONSIGNACAO))) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }

            if (TextHelper.isNull(adeCodigo)) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }

            final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
            final CustomTransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);

            // Permite o cancelamento apenas de consignações deferidas. Por exemplo, consignações originadas pelo aplicativo SalaryPay
            if (!CodedValues.SAD_DEFERIDA.equals(autdes.getAttribute(Columns.ADE_SAD_CODIGO))) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.cancelar.consignacao.diferente.deferida", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }

            // Permite cancelamento de consignações apenas de serviços da natureza SalaryPay card e pix ou Seguro
            String nseCodigo = (String) autdes.getAttribute(Columns.NSE_CODIGO);
            if (!(CodedValues.NSE_SALARYPAY.equals(nseCodigo) || CodedValues.NSE_SEGURO.equals(nseCodigo))) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.cancelar.consignacao.diferente.salarypay.seguro", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }

            boolean verificaStatusAde = true;
            // Verifica se a consignação foi a última transação realizada pelo servidor via aplicativo SalaryPay e se está dentro do prazo
            // limite definido para permitir o cancelamento
            try {
                final String svcCodigo = (String) autdes.getAttribute(Columns.SVC_CODIGO);
                final AutorizacaoController autorizacaoController = ApplicationContextProvider.getApplicationContext().getBean("autorizacaoController", AutorizacaoController.class);
                autorizacaoController.validarCancelamentoConsignacaoDentroPrazo(adeCodigo, svcCodigo, responsavel);
                verificaStatusAde = false;
            } catch (AutorizacaoControllerException ex) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.cancelar.consignacao.deferida.limite.excedido", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }

            final CancelarConsignacaoController cancelarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(CancelarConsignacaoController.class);
            cancelarConsignacaoController.cancelar(adeCodigo, verificaStatusAde, responsavel);

            // atualiza os dados da autorização para retorno
            final CustomTransferObject autdesAtualizada = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);

            final List<String> filter = Arrays.asList("ade_numero", "ade_vlr", "ade_data", "ade_codigo", "sad_codigo", "sad_descricao", "cor_codigo", "cor_nome", "cor_identificador", "dad_valor48");
            return Response.status(Response.Status.OK).entity(transformTO(autdesAtualizada, filter)).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        } catch (AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);

            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ex.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        }
    }

    @POST
    @Secured
    @Path("/descancelar/{id}")
    public Response descancelarConsignacao(@PathParam("id") String adeCodigo) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        try {
            if ((responsavel.isSer() && !responsavel.temPermissao(CodedValues.FUN_CANC_SOLICITACAO)) || (!responsavel.isSer() && !responsavel.temPermissao(CodedValues.FUN_CANC_CONSIGNACAO))) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }

            if (TextHelper.isNull(adeCodigo)) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }

            final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
            final CustomTransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);

            // Permite descancelar consignações apenas de serviços da natureza SalaryPay
            final String nseCodigo = (String) autdes.getAttribute(Columns.NSE_CODIGO);
            if (!CodedValues.NSE_SALARYPAY.equals(nseCodigo)) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }

            final CancelarConsignacaoController cancelarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(CancelarConsignacaoController.class);
            cancelarConsignacaoController.descancelar(adeCodigo, null, responsavel);

            return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        } catch (AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);

            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ex.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        }
    }

    @POST
    @Secured
    @Path("/saldoLiquidacao/{id}")
    public Response saldoLiquidacao(@PathParam("id") String id, SolicitacaoSaldoDevedorRestRequest dados) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        final ResponseRestRequest responseError = new ResponseRestRequest();
        try {
            if (!responsavel.temPermissao(CodedValues.FUN_SOLICITAR_SALDO_DEVEDOR_PARA_LIQ)) {
                return genericError(new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel));
            }

            final String requerTelSer = (String) ParamSist.getInstance().getParam(CodedValues.TPC_REQUER_TEL_SER_SOLIC_SALDO_DEVEDOR, responsavel);
            if (!TextHelper.isNull(requerTelSer) && requerTelSer.equalsIgnoreCase(CodedValues.TEL_SER_SOLIC_SALDO_DEVEDOR_OBRIGATORIO) && TextHelper.isNull(dados.serTelefone)) {
                return genericError(new ZetraException("mensagem.erro.solicitar.saldo.devedor.telefone.obrigatorio", responsavel));
            }

            // Valida se o usuário tem permissão para acessar dados da consignação
            final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
            final CustomTransferObject ade = pesquisarConsignacaoController.buscaAutorizacao(id, responsavel);

            String ocaObs = "";
            if (!TextHelper.isNull(dados.serTelefone)) {
                ocaObs = ApplicationResourcesHelper.getMessage("mensagem.solicitar.saldo.devedor.ocorrencia.obs.telefone", responsavel, responsavel.getUsuNome(), dados.serTelefone);
            } else {
                ocaObs = ApplicationResourcesHelper.getMessage("mensagem.solicitar.saldo.devedor.ocorrencia.obs.mobile", responsavel, responsavel.getUsuNome());
            }

            // Solicita saldo devedor para liquidação
            final SaldoDevedorController saldoDevedorController = ApplicationContextProvider.getApplicationContext().getBean(SaldoDevedorController.class);
            responseError.mensagem = saldoDevedorController.solicitarSaldoDevedor(ade.getAttribute(Columns.ADE_CODIGO).toString(), ocaObs, false, true, 0, responsavel);

        } catch (AutorizacaoControllerException | SaldoDevedorControllerException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }

        return Response.status(Response.Status.OK).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
    }

    @POST
    @Secured
    @Path("/show/{id}")
    public Response show(@PathParam("id") String id) {
        try {
            final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
            Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_CONS_CONSIGNACAO), null);
            if (resultadoValidacao != null) {
                return resultadoValidacao;
            }

            final ParametroController parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);
            final SaldoDevedorController saldoDevedorController = ApplicationContextProvider.getApplicationContext().getBean(SaldoDevedorController.class);
            final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
            final CustomTransferObject contrato = pesquisarConsignacaoController.buscaAutorizacao(id, responsavel);
            final String adeCodigo = contrato.getAttribute(Columns.ADE_CODIGO).toString();
            final String svcCodigo = contrato.getAttribute(Columns.SVC_CODIGO).toString();
            final String csaCodigo = contrato.getAttribute(Columns.CSA_CODIGO).toString();

            final String vlrCoeficiente = ((contrato.getAttribute(Columns.CFT_VLR) != null) ? NumberHelper.format((new BigDecimal(contrato.getAttribute(Columns.CFT_VLR).toString())).doubleValue(), NumberHelper.getLang()) : "");
            final String taxaAnual = CDCHelper.getStrTaxaEquivalenteAnual(vlrCoeficiente);
            contrato.setAttribute("cft_vlr_anual", taxaAnual);
            TransferObjectHelper.mascararUsuarioHistorico(contrato, null, responsavel);

            // 1.3) Chamar método SaldoDevedorDelegate.getSaldoDevedor() para recuperar o saldo devedor.
            final SaldoDevedorTransferObject saldoDevedor = saldoDevedorController.getSaldoDevedor(adeCodigo, responsavel);

            /**
             * 1.4) Chamar método SaldoDevedorDelegate.temSolicitacaoSaldoDevedor() para verificar
             *      se existe solicitação de saldo devedor pendente.
             */
            final boolean temSolicitacaoSaldoDevedor = saldoDevedorController.temSolicitacaoSaldoDevedor(adeCodigo, true, responsavel);

            /**
             * 1.5) Chamar método SaldoDevedorDelegate.temSolicitacaoSaldoDevedorLiquidacaoRespondida()
             *      para verificar se existe solicitação de saldo devedor respondida para exibir o valor do saldo devedor
             *      no detalhe da consignação.
             *      Solicitação não respondida igual a "true" sempre que tiver solicitação de saldo devedor pendente.
             */
            final boolean temSolicitacaoSaldoDevedorRespondida = temSolicitacaoSaldoDevedor ? !temSolicitacaoSaldoDevedor : saldoDevedorController.temSolicitacaoSaldoDevedorRespondida(adeCodigo, responsavel);

            /**
             * 1.6) Recuperar parâmetro de serviço (TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR = "126") que indica se é possível
             *      solicitar saldo devedor que será utilizado para exibir o botão "solicitar saldo".
             *      Para permitir a solicitação de saldo devedor o parâmetro de serviço deve estar cadastrado como:
             *      CodedValues.USUARIO_CADASTRA_SALDO_DEVEDOR ou CodedValues.CADASTRA_E_CALCULA_SALDO_DEVEDOR.
             */
            final List<String> parametros = new ArrayList<>();
            parametros.add(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR);
            final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
            // DESENV-19379 Priorizar parametro de servico de consignataria 126 TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR
            // e caso nao exista, ai sim continuar como o fluxo anterior de buscar o parametro de serviço de consignatnte
            final List<TransferObject> permiteCadastroSaldoDevedorCsaList = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, parametros, false, responsavel);
            final String paramSvc = getParam(permiteCadastroSaldoDevedorCsaList, paramSvcCse);
            boolean permiteCadastroSaldoDevedor = !TextHelper.isNull(paramSvc) && paramSvc.equals(CodedValues.USUARIO_CADASTRA_SALDO_DEVEDOR);
            permiteCadastroSaldoDevedor = permiteCadastroSaldoDevedor && responsavel.temPermissao(CodedValues.FUN_SOLICITAR_SALDO_DEVEDOR_PARA_LIQ);

            contrato.setAttribute("saldo_devedor", saldoDevedor != null ? saldoDevedor.getSdvValor() : null);
            contrato.setAttribute("tem_solicitacao_saldo_devedor", temSolicitacaoSaldoDevedor);
            contrato.setAttribute("tem_solicitacao_saldo_devedor_respondida", temSolicitacaoSaldoDevedorRespondida);
            contrato.setAttribute("permite_cadastro_saldo_devedor", permiteCadastroSaldoDevedor);

            List<String> filter = Arrays.asList("ade_numero", "ade_vlr", "ade_data", "ade_carencia", "ade_codigo", "cnv_cod_verba", "sad_codigo", "sad_descricao", "ade_prd_pagas", "csa_codigo", "ade_data", "csa_nome", "ade_identificador", "ade_ano_mes_ini", "ade_ano_mes_fim", "svc_codigo", "svc_descricao", "svc_identificador", "ade_vlr_liquido", "ade_prazo", "ade_taxa_juros", "ade_tipo_vlr", "cft_vlr", "cft_vlr_anual", "usu_login", "saldo_devedor", "tem_solicitacao_saldo_devedor", "tem_solicitacao_saldo_devedor_respondida", "permite_cadastro_saldo_devedor");
            return Response.status(Response.Status.OK).entity(transformTO(contrato, filter)).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();

        } catch (AutorizacaoControllerException | SaldoDevedorControllerException | ParametroControllerException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }
    }

    private static String getParam(List<TransferObject> permiteCadastroSaldoDevedorCsaList, ParamSvcTO paramSvcCse) {
        final TransferObject objectParam = permiteCadastroSaldoDevedorCsaList.isEmpty() ? null : permiteCadastroSaldoDevedorCsaList.get(0);
        final String permiteCadastroSaldoDevedorCsa = objectParam != null ? (String) objectParam.getAttribute(Columns.PSC_VLR) : null;
        final String permiteCadastroSaldoDv = permiteCadastroSaldoDevedorCsa != null ? permiteCadastroSaldoDevedorCsa : paramSvcCse.getTpsPermiteCadastrarSaldoDevedor();
        return permiteCadastroSaldoDv != null ? permiteCadastroSaldoDv : paramSvcCse.getTpsPermiteCadastrarSaldoDevedor();
    }

    @POST
    @Secured
    @Path("/show")
    public Response show(ListarConsigacaoRestRequest dados) {
        try {
            final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
            Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_CONS_CONSIGNACAO), null);
            if (resultadoValidacao != null) {
                return resultadoValidacao;
            }
            return Response.status(Response.Status.OK).entity(getPaginaDeContratos(dados).get("results")).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        } catch (AutorizacaoControllerException | ServidorControllerException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }
    }

    /**
     * usar a API /consignacoes ao invés desta que tem um thread sleep de 1 segundo específico para o app SalaryPay
     * @param dados
     * @return
     */
    @Deprecated
    @POST
    @Secured
    @Path("/showPage")
    public Response showPage(ListarConsigacaoRestRequest dados) {
        try {
            final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
            Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_CONS_CONSIGNACAO), null);
            if (resultadoValidacao != null) {
                return resultadoValidacao;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }
            return Response.status(Response.Status.OK).entity(getPaginaDeContratos(dados)).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        } catch (AutorizacaoControllerException | ServidorControllerException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }
    }

    @POST
    @Secured
    @Path("/consignacoes")
    public Response consignacoes(ListarConsigacaoRestRequest dados) {
        try {
            final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
            Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_CONS_CONSIGNACAO), null);
            if (resultadoValidacao != null) {
                return resultadoValidacao;
            }
            return Response.status(Response.Status.OK).entity(getPaginaDeContratos(dados)).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        } catch (AutorizacaoControllerException | ServidorControllerException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }
    }

    private Map<String, Object> getPaginaDeContratos(ListarConsigacaoRestRequest dados) throws AutorizacaoControllerException, ServidorControllerException {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        String usuCodigo = responsavel.getUsuCodigo();
        String rseCodigo = responsavel.getRseCodigo();

        if (responsavel.isSup() && TextHelper.isNull(dados.serCpf)) {
            throw new ServidorControllerException("mensagem.informe.servidor.cpf", responsavel);
        } else if (responsavel.isSup() && !TextHelper.isNull(dados.serCpf)) {
            // Busca o registro servidor pelo cpf
            final ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
            final List<TransferObject> lstRegistroServidor = servidorController.lstRegistroServidorPorCpf(dados.serCpf, null, AcessoSistema.getAcessoUsuarioSistema());
            if (lstRegistroServidor.isEmpty()) {
                throw new ServidorControllerException("mensagem.nenhumServidorEncontrado", responsavel);
            }
            rseCodigo = lstRegistroServidor.get(0).getAttribute(Columns.RSE_CODIGO).toString();

            final String rseMatricula = lstRegistroServidor.get(0).getAttribute(Columns.RSE_MATRICULA).toString();
            final String orgIdentificador = lstRegistroServidor.get(0).getAttribute(Columns.ORG_IDENTIFICADOR).toString();
            final String estIdentificador = lstRegistroServidor.get(0).getAttribute(Columns.EST_IDENTIFICADOR).toString();

            final PesquisarServidorController pesquisarServidorController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarServidorController.class);
            final CustomTransferObject usuarioServidor = pesquisarServidorController.buscaUsuarioServidor(null, null, rseMatricula, orgIdentificador, estIdentificador, responsavel);

            usuCodigo = usuarioServidor.getAttribute(Columns.USU_CODIGO).toString();
        }

        if (dados == null) {
            dados = new ListarConsigacaoRestRequest();
        }
        final boolean paginacao = (dados.size != null);
        if (dados.size == null) {
            dados.size = JspHelper.LIMITE;
        }
        if (dados.offset == null) {
            dados.offset = 0;
        }
        List<String> svcCodigos = null;
        if (dados.nseCodigo != null) {
            svcCodigos = new ArrayList<>();
            try {
                final ServicoController servicoController = ApplicationContextProvider.getApplicationContext().getBean(ServicoController.class);
                final List<Servico> servicos = servicoController.findByNseCodigo(dados.nseCodigo, responsavel);
                for (Servico servico : servicos) {
                    svcCodigos.add(servico.getSvcCodigo());
                }
            } catch (ServicoControllerException e) {
                LOG.error(e.getMessage(), e);
            }
        }

        final CustomTransferObject criterio = new CustomTransferObject();
        if (dados.IniData != null && dados.FimData != null) {
            criterio.setAttribute("periodoIni", dados.IniData);
            criterio.setAttribute("periodoFim", dados.FimData);
        }
        criterio.setAttribute("TIPO_OPERACAO", "consultar_historico_pagamento");

        final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
        final List<TransferObject> contratos = pesquisarConsignacaoController.pesquisaAutorizacao(AcessoSistema.ENTIDADE_SER, usuCodigo, rseCodigo, null, null, dados.sadCodigos, svcCodigos, dados.offset, dados.size, criterio, responsavel);

        for (TransferObject contrato : contratos) {
            final String vlrCoeficiente = ((contrato.getAttribute(Columns.CFT_VLR) != null) ? NumberHelper.format((new BigDecimal(contrato.getAttribute(Columns.CFT_VLR).toString())).doubleValue(), NumberHelper.getLang()) : "");
            final String taxaAnual = CDCHelper.getStrTaxaEquivalenteAnual(vlrCoeficiente);
            contrato.setAttribute("cft_vlr_anual", taxaAnual);
        }

        final List<String> filter = Arrays.asList("ade_codigo", "ade_numero", "ade_vlr", "ade_data", "ade_carencia", "cnv_cod_verba", "sad_codigo", "sad_descricao", "ade_prd_pagas", "csa_codigo", "csa_nome", "csa_nome_abrev", "ade_identificador", "ade_ano_mes_ini", "ade_ano_mes_fim", "svc_descricao", "svc_identificador", "ade_vlr_liquido", "ade_prazo", "ade_tipo_vlr", "ade_taxa_juros", "cft_vlr", "cft_vlr_anual", "cor_codigo", "cor_nome", "cor_identificador", "dad_valor48", "dad_valor90", "dad_valor91");

        final Map<String, Object> resultado = new HashMap<>();
        resultado.put("results", transformTOs(contratos, filter));
        if (paginacao) {
            final int totalResults = pesquisarConsignacaoController.countPesquisaAutorizacao(AcessoSistema.ENTIDADE_SER, usuCodigo, rseCodigo, null, null, null, svcCodigos, null, responsavel);
            final int totalPages = (totalResults / dados.size) + 1;
            final int page = (dados.offset / dados.size) + 1;
            resultado.put("page", page);
            resultado.put("total_results", totalResults);
            resultado.put("total_pages", totalPages);
        }
        return resultado;
    }

    @POST
    @Secured
    @Path("/showPageSeller")
    public Response showPageSeller(ListarConsigacaoRestRequest dados) {
        try {
            final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
            Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_CONS_CONSIGNACAO), List.of(AcessoSistema.ENTIDADE_COR, AcessoSistema.ENTIDADE_CSA));
            if (resultadoValidacao != null) {
                return resultadoValidacao;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }
            return Response.status(Response.Status.OK).entity(getPaginaDeContratosSeller(dados)).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        } catch (AutorizacaoControllerException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }
    }

    @POST
    @Secured
    @Path("/consignacoesServidores")
    public Response consignacoesServidores(ListarConsigacaoRestRequest dados) throws AutorizacaoControllerException, ServidorControllerException {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_CONS_CONSIGNACAO), List.of(AcessoSistema.ENTIDADE_CSE, AcessoSistema.ENTIDADE_SUP, AcessoSistema.ENTIDADE_ORG));
        if (resultadoValidacao != null) {
            return resultadoValidacao;
        }

        List<String> usuCodigo = new ArrayList<>();
        List<String> rseCodigo = new ArrayList<>();

        if (dados == null) {
            dados = new ListarConsigacaoRestRequest();
        }

        final List<String> filter = Arrays.asList("ade_codigo", "ade_numero", "ade_vlr", "ade_data", "ade_carencia", "cnv_cod_verba", "sad_codigo", "sad_descricao", "ade_prd_pagas", "csa_codigo", "csa_nome", "csa_nome_abrev", "ade_identificador", "ade_ano_mes_ini", "ade_ano_mes_fim", "svc_descricao", "svc_identificador", "ade_vlr_liquido", "ade_prazo", "ade_tipo_vlr", "ade_taxa_juros", "cft_vlr", "cft_vlr_anual", "cor_codigo", "cor_nome", "cor_identificador", "dad_valor48", "dad_valor90", "dad_valor91");
        final Map<String, Object> resultado = new HashMap<>();

        if (TextHelper.isNull(dados.serCpf) && TextHelper.isNull(dados.rseCodigo) && TextHelper.isNull(dados.adeNumero) && TextHelper.isNull(dados.matricula)) {
            throw new ServidorControllerException("mensagem.rest.parametros.ausente", responsavel);
        } else if (responsavel.isCseSupOrg() && !TextHelper.isNull(dados.adeNumero)) {
            final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
            TransferObject result = pesquisarConsignacaoController.findAutDescontoByAdeNumero(dados.adeNumero, responsavel);
            resultado.put("contrato", transformTO(result, filter));

            return Response.status(Response.Status.OK).entity(resultado).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        } else if (responsavel.isCseSupOrg() && !TextHelper.isNull(dados.matricula)) {
            final ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
            final List<TransferObject> lstRegistroServidor = servidorController.findRegistroServidoresByMatriculas(List.of(dados.matricula), AcessoSistema.getAcessoUsuarioSistema());

            if (lstRegistroServidor.isEmpty()) {
                throw new ServidorControllerException("mensagem.nenhumServidorEncontrado", responsavel);
            }

            final PesquisarServidorController pesquisarServidorController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarServidorController.class);

            if (lstRegistroServidor.size() > 1){
                for (TransferObject lst : lstRegistroServidor){
                    rseCodigo.add(lst.getAttribute(Columns.RSE_CODIGO).toString());
                    final String rseMatricula = lst.getAttribute(Columns.RSE_MATRICULA).toString();
                    final String orgIdentificador = lst.getAttribute(Columns.ORG_IDENTIFICADOR).toString();
                    final String estIdentificador = lst.getAttribute(Columns.EST_IDENTIFICADOR).toString();

                    final CustomTransferObject usuarioServidor = pesquisarServidorController.buscaUsuarioServidor(null, null, rseMatricula, orgIdentificador, estIdentificador, responsavel);

                    usuCodigo.add(usuarioServidor.getAttribute(Columns.USU_CODIGO).toString());
                }
            } else {
                rseCodigo.add(lstRegistroServidor.get(0).getAttribute(Columns.RSE_CODIGO).toString());
                final String rseMatricula = lstRegistroServidor.get(0).getAttribute(Columns.RSE_MATRICULA).toString();
                final String orgIdentificador = lstRegistroServidor.get(0).getAttribute(Columns.ORG_IDENTIFICADOR).toString();
                final String estIdentificador = lstRegistroServidor.get(0).getAttribute(Columns.EST_IDENTIFICADOR).toString();

                final CustomTransferObject usuarioServidor = pesquisarServidorController.buscaUsuarioServidor(null, null, rseMatricula, orgIdentificador, estIdentificador, responsavel);

                usuCodigo.add(usuarioServidor.getAttribute(Columns.USU_CODIGO).toString());
            }


        } else if (responsavel.isCseSupOrg() && !TextHelper.isNull(dados.serCpf)) {
            // Busca o registro servidor pelo cpf
            final ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
            final List<TransferObject> lstRegistroServidor = servidorController.lstRegistroServidorPorCpf(dados.serCpf, null, AcessoSistema.getAcessoUsuarioSistema());
            if (lstRegistroServidor.isEmpty()) {
                throw new ServidorControllerException("mensagem.nenhumServidorEncontrado", responsavel);
            }

            final PesquisarServidorController pesquisarServidorController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarServidorController.class);

            if (lstRegistroServidor.size() > 1) {
                for (TransferObject lst : lstRegistroServidor){
                    rseCodigo.add(lst.getAttribute(Columns.RSE_CODIGO).toString());
                    final String rseMatricula = lst.getAttribute(Columns.RSE_MATRICULA).toString();
                    final String orgIdentificador = lst.getAttribute(Columns.ORG_IDENTIFICADOR).toString();
                    final String estIdentificador = lst.getAttribute(Columns.EST_IDENTIFICADOR).toString();

                    final CustomTransferObject usuarioServidor = pesquisarServidorController.buscaUsuarioServidor(null, null, rseMatricula, orgIdentificador, estIdentificador, responsavel);

                    usuCodigo.add(usuarioServidor.getAttribute(Columns.USU_CODIGO).toString());
                }
            } else {
                rseCodigo.add(lstRegistroServidor.get(0).getAttribute(Columns.RSE_CODIGO).toString());

                final String rseMatricula = lstRegistroServidor.get(0).getAttribute(Columns.RSE_MATRICULA).toString();
                final String orgIdentificador = lstRegistroServidor.get(0).getAttribute(Columns.ORG_IDENTIFICADOR).toString();
                final String estIdentificador = lstRegistroServidor.get(0).getAttribute(Columns.EST_IDENTIFICADOR).toString();

                final CustomTransferObject usuarioServidor = pesquisarServidorController.buscaUsuarioServidor(null, null, rseMatricula, orgIdentificador, estIdentificador, responsavel);

                usuCodigo.add(usuarioServidor.getAttribute(Columns.USU_CODIGO).toString());
            }


        } else if (responsavel.isCseSupOrg() && !TextHelper.isNull(dados.rseCodigo)) {
            final ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);

            final RegistroServidorTO lstRegistroServidor = servidorController.findRegistroServidor(dados.rseCodigo, responsavel);
            if (TextHelper.isNull(lstRegistroServidor)) {
                throw new ServidorControllerException("mensagem.nenhumServidorEncontrado", responsavel);
            }

            rseCodigo.add(lstRegistroServidor.getRseCodigo());

            final PesquisarServidorController pesquisarServidorController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarServidorController.class);
            final CustomTransferObject usuarioServidor = pesquisarServidorController.buscaUsuarioServidorBySerCodigo(lstRegistroServidor.getSerCodigo(), responsavel);

            usuCodigo.add(usuarioServidor.getAttribute(Columns.USU_CODIGO).toString());
        }

        final boolean paginacao = (dados.size != null);
        if (dados.size == null) {
            dados.size = JspHelper.LIMITE;
        }
        if (dados.offset == null) {
            dados.offset = 0;
        }
        List<String> svcCodigos = null;
        if (dados.nseCodigo != null) {
            svcCodigos = new ArrayList<>();
            try {
                final ServicoController servicoController = ApplicationContextProvider.getApplicationContext().getBean(ServicoController.class);
                final List<Servico> servicos = servicoController.findByNseCodigo(dados.nseCodigo, responsavel);
                for (Servico servico : servicos) {
                    svcCodigos.add(servico.getSvcCodigo());
                }
            } catch (ServicoControllerException e) {
                LOG.error(e.getMessage(), e);
            }
        }

        final CustomTransferObject criterio = new CustomTransferObject();
        if (dados.IniData != null && dados.FimData != null) {
            criterio.setAttribute("periodoIni", dados.IniData);
            criterio.setAttribute("periodoFim", dados.FimData);
        }
        criterio.setAttribute("TIPO_OPERACAO", "consultar_historico_pagamento");
        final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);

        if(rseCodigo.size() > 1) {
            for (int i = 0; i < rseCodigo.size(); i++) {
                final List<TransferObject> contratos = pesquisarConsignacaoController.pesquisaAutorizacao(AcessoSistema.ENTIDADE_SER, usuCodigo.get(i), rseCodigo.get(i), null, null, dados.sadCodigos, svcCodigos, dados.offset, dados.size, criterio, responsavel);

                for (TransferObject contrato : contratos) {
                    final String vlrCoeficiente = ((contrato.getAttribute(Columns.CFT_VLR) != null) ? NumberHelper.format((new BigDecimal(contrato.getAttribute(Columns.CFT_VLR).toString())).doubleValue(), NumberHelper.getLang()) : "");
                    final String taxaAnual = CDCHelper.getStrTaxaEquivalenteAnual(vlrCoeficiente);
                    contrato.setAttribute("cft_vlr_anual", taxaAnual);
                }

                resultado.put("servidor" + "-" + rseCodigo.get(i), transformTOs(contratos, filter));
                if (paginacao) {
                    final int totalResults = pesquisarConsignacaoController.countPesquisaAutorizacao(AcessoSistema.ENTIDADE_SER, usuCodigo.get(i), rseCodigo.get(i), null, null, null, svcCodigos, null, responsavel);
                    final int totalPages = (totalResults / dados.size) + 1;
                    final int page = (dados.offset / dados.size) + 1;
                    resultado.put("page", page);
                    resultado.put("total_results", totalResults);
                    resultado.put("total_pages", totalPages);
                }
            }
        } else {
            final List<TransferObject> contratos = pesquisarConsignacaoController.pesquisaAutorizacao(AcessoSistema.ENTIDADE_SER, usuCodigo.get(0), rseCodigo.get(0), null, null, dados.sadCodigos, svcCodigos, dados.offset, dados.size, criterio, responsavel);
            for (TransferObject contrato : contratos) {
                final String vlrCoeficiente = ((contrato.getAttribute(Columns.CFT_VLR) != null) ? NumberHelper.format((new BigDecimal(contrato.getAttribute(Columns.CFT_VLR).toString())).doubleValue(), NumberHelper.getLang()) : "");
                final String taxaAnual = CDCHelper.getStrTaxaEquivalenteAnual(vlrCoeficiente);
                contrato.setAttribute("cft_vlr_anual", taxaAnual);
            }

            resultado.put("servidor", transformTOs(contratos, filter));
            if (paginacao) {
                final int totalResults = pesquisarConsignacaoController.countPesquisaAutorizacao(AcessoSistema.ENTIDADE_SER, usuCodigo.get(0), rseCodigo.get(0), null, null, null, svcCodigos, null, responsavel);
                final int totalPages = (totalResults / dados.size) + 1;
                final int page = (dados.offset / dados.size) + 1;
                resultado.put("page", page);
                resultado.put("total_results", totalResults);
                resultado.put("total_pages", totalPages);
            }
        }
        return Response.status(Response.Status.OK).entity(resultado).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
    }

    private Map<String, Object> getPaginaDeContratosSeller(ListarConsigacaoRestRequest dados) throws AutorizacaoControllerException {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        if (dados == null) {
            dados = new ListarConsigacaoRestRequest();
        }
        final boolean paginacao = (dados.size != null);
        if (dados.size == null) {
            dados.size = JspHelper.LIMITE;
        }
        if (dados.offset == null) {
            dados.offset = 0;
        }

        final CustomTransferObject criterio = new CustomTransferObject();
        if (dados.IniData != null && dados.FimData != null) {
            criterio.setAttribute("periodoIni", dados.IniData);
            criterio.setAttribute("periodoFim", dados.FimData);
        }

        List<String> svcCodigos = null;
        if (dados.nseCodigo != null) {
            svcCodigos = new ArrayList<>();
            try {
                final ServicoController servicoController = ApplicationContextProvider.getApplicationContext().getBean(ServicoController.class);
                final List<Servico> servicos = servicoController.findByNseCodigo(dados.nseCodigo, responsavel);
                for (Servico servico : servicos) {
                    svcCodigos.add(servico.getSvcCodigo());
                }
            } catch (ServicoControllerException e) {
                LOG.error(e.getMessage(), e);
            }
        }

        // Se usuário que consulta não tiver permissão master, retorna apenas os contratos dele próprio.
        if ((responsavel.isCor() || responsavel.isCsa()) && (!responsavel.temPermissao(CodedValues.FUN_CONS_VENDAS_TODOS_USUARIOS))) {
            criterio.setAttribute("adePropria", true);
        }

        final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
        final List<TransferObject> contratos = pesquisarConsignacaoController.pesquisaAutorizacao(AcessoSistema.ENTIDADE_CSA, responsavel.getCsaCodigo(), null, null, null, dados.sadCodigos, svcCodigos, dados.offset, dados.size, criterio, responsavel);

        for (TransferObject contrato : contratos) {
            final String vlrCoeficiente = ((contrato.getAttribute(Columns.CFT_VLR) != null) ? NumberHelper.format((new BigDecimal(contrato.getAttribute(Columns.CFT_VLR).toString())).doubleValue(), NumberHelper.getLang()) : "");
            final String taxaAnual = CDCHelper.getStrTaxaEquivalenteAnual(vlrCoeficiente);
            contrato.setAttribute("cft_vlr_anual", taxaAnual);
        }

        final List<String> filter = Arrays.asList("ade_codigo", "ade_numero", "ade_vlr", "ade_data", "ade_carencia", "cnv_cod_verba", "sad_codigo", "sad_descricao", "ade_prd_pagas", "csa_codigo", "csa_nome", "ade_identificador", "ade_ano_mes_ini", "ade_ano_mes_fim", "svc_descricao", "svc_identificador", "ade_vlr_liquido", "ade_prazo", "ade_tipo_vlr", "cft_vlr", "cft_vlr_anual", "usu_nome", "ser_nome");

        final Map<String, Object> resultado = new HashMap<>();
        resultado.put("results", transformTOs(contratos, filter));
        if (paginacao) {
            final int totalResults = pesquisarConsignacaoController.countPesquisaAutorizacao(AcessoSistema.ENTIDADE_CSA, responsavel.getCsaCodigo(), null, null, null, dados.sadCodigos, svcCodigos, criterio, responsavel);
            final int totalPages = (totalResults / dados.size) + 1;
            final int page = (dados.offset / dados.size) + 1;
            resultado.put("page", page);
            resultado.put("total_results", totalResults);
            resultado.put("total_pages", totalPages);
        }

        return resultado;
    }

    @POST
    @Secured
    @Path("/parcelas")
    public Response parcelas(ShowRestRequest dados) {
        try {
            final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

            if (dados == null) {
                dados = new ShowRestRequest();
            }

            if (dados.size == null) {
                dados.size = 20;
            }

            if (dados.offset == null) {
                dados.offset = 0;
            }

            List<Map<String, Object>> retorno = null;

            // faz verificações de permissão
            final AutorizacaoController autorizacaoController = ApplicationContextProvider.getApplicationContext().getBean("autorizacaoController", AutorizacaoController.class);
            if (autorizacaoController.usuarioPodeConsultarAde(dados.id, responsavel)) {
                final ParcelaController parcelaController = ApplicationContextProvider.getApplicationContext().getBean(ParcelaController.class);
                final List<TransferObject> parcelas = parcelaController.getHistoricoParcelas(dados.id, null, CodedValues.TOC_CODIGOS_RETORNO_PARCELA, false, dados.offset, dados.size, false, responsavel);

                final List<String> filter = Arrays.asList("prd_vlr_previsto", "prd_data_desconto", "spd_descricao", "prd_vlr_realizado", "prd_data_realizado", "ocp_obs", "prd_numero");
                retorno = transformTOs(parcelas, filter).stream().map(parcela -> {
                    parcela.put("prd_numero", (parcela.get("prd_numero") != null ? parcela.get("prd_numero").toString() : ""));
                    return parcela;
                }).collect(Collectors.toList());
            }

            return Response.status(Response.Status.OK).entity(retorno).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        } catch (ParcelaControllerException | AutorizacaoControllerException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }
    }

    @POST
    @Secured
    @Path("/parcelasByStatus")
    public Response parcelasByStatus(ParcelaRestRequest dados) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        if (!responsavel.temPermissao(CodedValues.FUN_CONS_PARCELAS_POR_STATUS)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        }

        if (dados == null) {
            dados = new ParcelaRestRequest();
        }

        if (TextHelper.isNull(dados.periodo)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        }

        Date prdDataDesconto = null;

        try {
            if (dados.periodo.toString().matches("([0-9]{2})/([0-9]{4})")) {
                prdDataDesconto = DateHelper.parsePeriodString(dados.periodo);
            } else {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.periodo.invalido", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }
        } catch (ParseException ex) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.periodo.invalido", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        }

        try {
            final ParcelaController parcelaController = ApplicationContextProvider.getApplicationContext().getBean(ParcelaController.class);

            List<String> spdCodigos = null;

            if (!TextHelper.isNull(dados.status)) {
                try {
                    parcelaController.findStatusParcelaDesconto(dados.status.trim(), responsavel);
                    spdCodigos = new ArrayList<>();
                    spdCodigos.add(dados.status.trim());
                } catch (Exception ex) {
                    final ResponseRestRequest responseError = new ResponseRestRequest();
                    responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.parcelas.status.nao.encontradas", null);
                    return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
                }
            }

            // faz verificações de permissão
            final List<TransferObject> parcelas = parcelaController.getHistoricoParcelas(prdDataDesconto, spdCodigos, responsavel);
            final List<String> filter = Arrays.asList("prd_numero", "prd_data_desconto", "prd_data_realizado", "prd_vlr_previsto", "prd_vlr_realizado", "spd_codigo", "ocp_obs", "ade_numero", "ade_vlr", "ade_prazo", "ade_prd_pagas", "ade_indice", "cnv_cod_verba", "rse_matricula", "ser_cpf", "ser_nome");
            return Response.status(Response.Status.OK).entity(transformTOs(parcelas, filter)).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();

        } catch (ParcelaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return genericError(ex);
        }
    }

    @POST
    @Secured
    @Path("/listStatusConsignacao")
    public Response listStatusConsignacaoPorServidor() {
        try {
            final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
            Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_CONS_CONSIGNACAO), null);
            if (resultadoValidacao != null) {
                return resultadoValidacao;
            }

            final AutorizacaoController autorizacaoController = ApplicationContextProvider.getApplicationContext().getBean("autorizacaoController", AutorizacaoController.class);
            final List<TransferObject> status = autorizacaoController.buscarStatusConsignacaoPorServidor(responsavel.getRseCodigo(), responsavel);
            final List<String> filter = Arrays.asList("sad_codigo", "sad_descricao", "sad_sequencia");

            return Response.status(Response.Status.OK).entity(transformTOs(status, filter)).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        } catch (AutorizacaoControllerException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }
    }

    @POST
    @Secured
    @Path("/listNaturezaConsignacao")
    public Response listNaturezaConsignacaoPorServidor() {
        try {
            final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
            Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_CONS_CONSIGNACAO), null);
            if (resultadoValidacao != null) {
                return resultadoValidacao;
            }

            final AutorizacaoController autorizacaoController = ApplicationContextProvider.getApplicationContext().getBean("autorizacaoController", AutorizacaoController.class);
            final List<TransferObject> status = autorizacaoController.buscarNaturezaConsignacaoPorServidor(responsavel.getRseCodigo(), responsavel);
            final List<String> filter = Arrays.asList("nse_codigo", "nse_descricao");

            return Response.status(Response.Status.OK).entity(transformTOs(status, filter)).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        } catch (AutorizacaoControllerException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }
    }

    @POST
    @Secured
    @Path("/registraDadoAdicional")
    public Response registraDadoAdicional(RecuperaDadosServidorRestRequest dadosServidor) {
        try {
            final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
            Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_CONS_CONSIGNACAO), null);
            if (resultadoValidacao != null) {
                return resultadoValidacao;
            }

            if (!responsavel.isSer()) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.sistema.tipo.entidade.invalido", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }
            if (TextHelper.isNull(dadosServidor.adeCodigo) || TextHelper.isNull(dadosServidor.tdaCodigo) || TextHelper.isNull(dadosServidor.dadValor)) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.erro.inclusao.dados.servidor", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }

            final AutorizacaoController autorizacaoController = ApplicationContextProvider.getApplicationContext().getBean("autorizacaoController", AutorizacaoController.class);
            autorizacaoController.setDadoAutDesconto(dadosServidor.adeCodigo, dadosServidor.tdaCodigo, dadosServidor.dadValor, responsavel);

            final ResponseRestRequest response = new ResponseRestRequest();
            response.mensagem = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.inclusao.dados.servidor", null);
            return Response.status(Response.Status.OK).entity(response).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        } catch (AutorizacaoControllerException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }
    }

    //DESENV-18004
    @GET
    @Secured
    @Path("/geraBoletoADE/{id}")
    public Response geraBoletoADE(@PathParam("id") String adeCodigo) throws java.io.IOException {
        FileInputStream boletoStream = null;

        try {
            final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
            Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_CONS_CONSIGNACAO), null);
            if (resultadoValidacao != null) {
                return resultadoValidacao;
            }

            if (TextHelper.isNull(adeCodigo)) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }

            final ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
            final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
            final CustomTransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);

            String absolutePath = ParamSist.getDiretorioRaizArquivos();

            final String caminhoBackgroudbase = absolutePath + File.separatorChar + "imagem" + File.separatorChar + "fundo_contracheque.png";
            final String caminhoBackgroud = CreateImageHelper.gerarImagemTransparente(caminhoBackgroudbase);

            com.zetra.econsig.report.config.Relatorio relatorio = ConfigRelatorio.getInstance().getRelatorio("boleto_ade");
            final Map<String, String[]> parameterMap = new HashMap<>();
            final String adeNumero = String.valueOf(autdes.getAttribute(Columns.ADE_NUMERO));
            parameterMap.put("CAMINHO_BACKGROUND", new String[] { caminhoBackgroud });
            parameterMap.put("ADE_NUMERO", new String[] { adeNumero });
            parameterMap.put("SER_NOME", new String[] { (String) autdes.getAttribute(Columns.SER_NOME) });
            parameterMap.put("SER_CPF", new String[] { (String) autdes.getAttribute(Columns.SER_CPF) });
            parameterMap.put("SER_NRO_IDT", new String[] { (String) autdes.getAttribute(Columns.SER_NRO_IDT) });
            parameterMap.put("SER_DATA_NASC", new String[] { DateHelper.format((Date) autdes.getAttribute(Columns.SER_DATA_NASC), "yyyyMMdd") });
            parameterMap.put("SER_END", new String[] { (String) autdes.getAttribute(Columns.SER_END) });
            parameterMap.put("SER_NRO", new String[] { (String) autdes.getAttribute(Columns.SER_NRO) });
            parameterMap.put("SER_COMPL", new String[] { (String) autdes.getAttribute(Columns.SER_COMPL) });
            parameterMap.put("SER_BAIRRO", new String[] { (String) autdes.getAttribute(Columns.SER_BAIRRO) });
            parameterMap.put("SER_CIDADE", new String[] { (String) autdes.getAttribute(Columns.SER_CIDADE) });
            parameterMap.put("SER_UF", new String[] { (String) autdes.getAttribute(Columns.SER_UF) });
            parameterMap.put("SER_CEP", new String[] { (String) autdes.getAttribute(Columns.SER_CEP) });
            parameterMap.put("SER_TEL", new String[] { (String) autdes.getAttribute(Columns.SER_TEL) });
            parameterMap.put("RSE_MATRICULA", new String[] { (String) autdes.getAttribute(Columns.RSE_MATRICULA) });
            parameterMap.put("RSE_DATA_ADMISSAO", new String[] { DateHelper.format((Date) autdes.getAttribute(Columns.RSE_DATA_ADMISSAO), "yyyyMMdd") });
            parameterMap.put("ORG_NOME", new String[] { (String) autdes.getAttribute(Columns.ORG_NOME) });
            parameterMap.put("RSE_MATRICULA", new String[] { String.valueOf(autdes.getAttribute(Columns.RSE_MATRICULA)) });
            final String estCvlDesc = servidorController.getEstCivil((String) autdes.getAttribute(Columns.SER_EST_CIVIL), responsavel);
            parameterMap.put("SER_EST_CIVIL", new String[] { estCvlDesc });
            parameterMap.put("CSA_NOME", new String[] { (String) autdes.getAttribute(Columns.CSA_NOME) });
            parameterMap.put("USU_LOGIN", new String[] { (String) autdes.getAttribute(Columns.USU_LOGIN) });
            final String cdeRanking = String.valueOf(autdes.getAttribute(Columns.CDE_RANKING));
            parameterMap.put("CDE_RANKING", new String[] { cdeRanking != "null" ? cdeRanking : null });
            parameterMap.put("SVC_DESCRICAO", new String[] { (String) autdes.getAttribute(Columns.SVC_DESCRICAO) });
            parameterMap.put("ADE_DATA", new String[] { DateHelper.format((Date) autdes.getAttribute(Columns.ADE_DATA), "yyyyMMdd") });
            parameterMap.put("ADE_NUMERO", new String[] { adeNumero != "null" ? adeNumero : null });
            final String adeVlr = String.valueOf(autdes.getAttribute(Columns.ADE_VLR));
            parameterMap.put("ADE_VLR", new String[] { adeVlr != "null" ? adeVlr : null });
            final String adePrazoRef = String.valueOf(autdes.getAttribute(Columns.ADE_PRAZO_REF));
            parameterMap.put("ADE_PRAZO_REF", new String[] { adePrazoRef != "null" ? adePrazoRef : null });
            parameterMap.put("ADE_ANO_MES_INI", new String[] { DateHelper.format((Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI), "yyyyMM") });
            parameterMap.put("ADE_ANO_MES_INI", new String[] { DateHelper.format((Date) autdes.getAttribute(Columns.ADE_ANO_MES_FIM), "yyyyMM") });
            parameterMap.put("CNV_COD_VERBA", new String[] { String.valueOf(autdes.getAttribute(Columns.CNV_COD_VERBA)) });

            final String vlLiberado = String.valueOf(autdes.getAttribute(Columns.CDE_VLR_LIBERADO));
            parameterMap.put("CDE_VLR_LIBERADO", new String[] { vlLiberado != "null" ? vlLiberado : null });

            final String svcCodigo = (String) autdes.getAttribute(Columns.SVC_CODIGO);
            final String svcDescricao = (String) autdes.getAttribute(Columns.SVC_DESCRICAO);
            final String csaNome = (String) autdes.getAttribute(Columns.CSA_NOME);

            final String boleto = CodedNames.TEMPLATE_BOLETO_AUT_DESCONTO;
            String boletoFile = absolutePath + File.separatorChar + "boleto" + File.separatorChar + svcCodigo + File.separatorChar + boleto;

            File arqBoleto = new File(boletoFile);
            if (!arqBoleto.exists()) {
                boletoFile = ParamSist.getDiretorioRaizArquivos();
                boletoFile += File.separatorChar + "boleto" + File.separatorChar + boleto;
                arqBoleto = new File(boletoFile);
            }

            final String msgBoleto = FileHelper.readAll(boletoFile).replaceAll("<SERVICO>", Matcher.quoteReplacement(svcDescricao.toUpperCase())).replaceAll("<CONSIGNATARIA>", Matcher.quoteReplacement(csaNome.toUpperCase()));

            parameterMap.put("MSG_BOLETO", new String[] { msgBoleto });

            final String nomeArquivo = "boleto_" + adeNumero + "_" + LocalDate.now();
            parameterMap.put(ReportManager.REPORT_FILE_NAME, new String[] { nomeArquivo });

            String exportDir = absolutePath + File.separatorChar + "temp" + File.separatorChar + "boleto_ade";

            File dir = new File(exportDir);
            if (!dir.exists()) {
                dir.mkdir();
            }

            exportDir += File.separatorChar + "adeNumero";

            dir = new File(exportDir);
            if (!dir.exists()) {
                dir.mkdir();
            }

            parameterMap.put(ReportManager.REPORT_DIR_EXPORT, new String[] { exportDir });

            final ProcessaRelatorioBoletoADE processaRelatorioBoleto = new ProcessaRelatorioBoletoADE(relatorio, parameterMap, null, responsavel);
            processaRelatorioBoleto.run();

            final File boletoADE = new File(exportDir + File.separatorChar + nomeArquivo + ".pdf");
            boletoStream = new FileInputStream(boletoADE);
            final byte ccData[] = new byte[(int) boletoADE.length()];
            boletoStream.read(ccData);

            final String ccBase64 = Base64.encodeBase64String(ccData);

            boletoADE.delete();

            final HashMap<String, String> retorno = new HashMap<>();
            retorno.put("titulo", nomeArquivo);
            retorno.put("conteudo", ccBase64);

            return Response.status(Response.Status.OK).entity(retorno).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        } catch (AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);

            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ex.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();

        } finally {
            if (boletoStream != null) {
                try {
                    boletoStream.close();
                } catch (IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
    }

    @GET
    @Secured
    @Path("/resultadoProcessamento")
    public Response resultadoProcessamento(ResultadoConciliacaoRestRequest resultadoConciliacaoRestRequest) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_CONS_CONSIGNACAO), List.of(AcessoSistema.ENTIDADE_CSA));
        if (resultadoValidacao != null) {
            return resultadoValidacao;
        }

        if (TextHelper.isNull(resultadoConciliacaoRestRequest.orgIdentificador)) {
            return genericError(new ZetraException("mensagem.erro.resultado.conciliacao.orgao.obrigatorio", responsavel));
        }

        if (TextHelper.isNull(resultadoConciliacaoRestRequest.periodo)) {
            return genericError(new ZetraException("mensagem.erro.resultado.conciliacao.periodo.obrigatorio", responsavel));
        }

        if (!TextHelper.isNull(resultadoConciliacaoRestRequest.statusPagamento) && !(CodedValues.STATUS_PAGAMENTO.contains(resultadoConciliacaoRestRequest.statusPagamento))) {
            return genericError(new ZetraException("mensagem.erro.resultado.conciliacao.status.pagamento", responsavel));
        }

        Date prdDataDesconto = null;

        try {
            if (resultadoConciliacaoRestRequest.periodo.toString().matches("([0-9]{2})/([0-9]{4})")) {
                prdDataDesconto = DateHelper.parsePeriodString(resultadoConciliacaoRestRequest.periodo);
            } else {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.periodo.invalido", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }
        } catch (ParseException ex) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.periodo.invalido", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        }

        try {
            final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
            final List<TransferObject> concialiacao = pesquisarConsignacaoController.pesquisarConsignacaoConciliacao(resultadoConciliacaoRestRequest.orgIdentificador, prdDataDesconto, resultadoConciliacaoRestRequest.cpf, resultadoConciliacaoRestRequest.adeNumero, resultadoConciliacaoRestRequest.adeIdentificador, resultadoConciliacaoRestRequest.statusPagamento, responsavel);
            final List<String> filter = Arrays.asList("ser_nome", "ser_cpf", "rse_matricula", "ade_numero", "ade_identificador", "ade_data", "ade_ano_mes_ini", "ade_ano_mes_fim", "ade_prazo", "ade_prd_pagas", "prd_vlr_previsto", "prd_vlr_realizado", "ocp_obs");

            return Response.status(Response.Status.OK).entity(transformTOs(concialiacao, filter)).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        } catch (AutorizacaoControllerException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }
    }

    @POST
    @Secured
    @Path("/liquidar/{id}")
    public Response liquidar(@PathParam("id") String adeCodigo) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        try {
            if (!responsavel.temPermissao(CodedValues.FUN_LIQ_CONTRATO)) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }

            if (TextHelper.isNull(adeCodigo)) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }

            final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
            final CustomTransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);

            if (!autdes.getAttribute(Columns.ADE_SAD_CODIGO).equals(CodedValues.SAD_DEFERIDA) &&
                    !autdes.getAttribute(Columns.ADE_SAD_CODIGO).equals(CodedValues.SAD_EMANDAMENTO) &&
                    !autdes.getAttribute(Columns.ADE_SAD_CODIGO).equals(CodedValues.SAD_ESTOQUE)) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.cancelar.situacao.situacao.nao.permite", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
            }

            final LiquidarConsignacaoController liquidarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(LiquidarConsignacaoController.class);
            liquidarConsignacaoController.liquidar(adeCodigo, null, null, responsavel);

            // verifica desbloqueio automático de consignatária no cancelamento manual
            final String csaCodigo = autdes.getAttribute(Columns.CSA_CODIGO).toString();
            final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
            consignatariaController.verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel);

            final ResponseRestRequest response = new ResponseRestRequest();
            response.mensagem = ApplicationResourcesHelper.getMessage("mensagem.liquidar.consignacao.concluido.sucesso", null);
            return Response.status(Response.Status.OK).entity(response).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();

        } catch (AutorizacaoControllerException | ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);

            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ex.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
        }
    }
}
