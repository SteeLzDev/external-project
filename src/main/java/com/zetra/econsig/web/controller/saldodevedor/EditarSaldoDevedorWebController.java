package com.zetra.econsig.web.controller.saldodevedor;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.PropostaPagamentoDividaTO;
import com.zetra.econsig.dto.entidade.SaldoDevedorTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.FinanciamentoDividaControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.SaldoDevedorControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.VerbaRescisoriaRse;
import com.zetra.econsig.persistence.entity.VerbaRescisoriaRseHome;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.financiamentodivida.FinanciamentoDividaController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.rescisao.VerbaRescisoriaController;
import com.zetra.econsig.service.saldodevedor.SaldoDevedorController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusPropostaEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: EditarSaldoDevedorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Editar Saldo Devedor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: isaac.abreu $
 * $Revision: 23893 $
 * $Date: 2018-03-13 11:42:02 -0300 (ter, 13 mar 2018) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarSaldoDevedor" })
public class EditarSaldoDevedorWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarSaldoDevedorWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private EditarAnexoConsignacaoController editarAnexoConsignacaoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private SaldoDevedorController saldoDevedorController;

    @Autowired
    private FinanciamentoDividaController financiamentoDividaController;

    @Autowired
    VerbaRescisoriaController verbaRescisoriaController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(@RequestParam(value = "ADE_CODIGO", required = true, defaultValue = "") String ADE_CODIGO, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ParseException, ServicoControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String adeCodigo = request.getParameter("ADE_CODIGO");
        final String tipo = JspHelper.verificaVarQryStr(request, "tipo");
        final boolean isCompra = "compra".equalsIgnoreCase(tipo);
        final boolean isSolicitacaoSaldo = !isCompra;

        // Busca os parâmetros de sistema necessários
        final boolean exigeMultiplosSaldos = ParamSist.paramEquals(CodedValues.TPC_EXIGE_MULTIPLOS_SALDOS_DEVEDORES, CodedValues.TPC_SIM, responsavel);
        boolean infSaldoDevedorOpcional = false;
        boolean showInfBancarias = true;

        if (isCompra) {
            infSaldoDevedorOpcional = ParamSist.paramEquals(CodedValues.TPC_INF_SALDO_DEVEDOR_OPCIONAL_COMPRA, CodedValues.INF_SALDO_DEVEDOR_COMPRA_AUSENTE, responsavel) || ParamSist.paramEquals(CodedValues.TPC_INF_SALDO_DEVEDOR_OPCIONAL_COMPRA, CodedValues.INF_SALDO_DEVEDOR_COMPRA_OPCIONAL, responsavel);
            showInfBancarias = !ParamSist.paramEquals(CodedValues.TPC_INF_SALDO_DEVEDOR_OPCIONAL_COMPRA, CodedValues.INF_SALDO_DEVEDOR_COMPRA_AUSENTE, responsavel);
        } else if (isSolicitacaoSaldo) {
            infSaldoDevedorOpcional = ParamSist.paramEquals(CodedValues.TPC_INF_SALDO_DEVEDOR_OPCIONAL_SERVIDOR, CodedValues.TPC_SIM, responsavel);
        }

        final boolean exibeCamposdvLinkBoleto = ParamSist.paramEquals(CodedValues.TPC_EXIBE_LINK_BOLETO_SALDO_DEVEDOR, CodedValues.TPC_SIM, responsavel);
        final boolean exigeAnexoDsdSaldo = ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_DSD_SALDO_SERVIDOR, CodedValues.TPC_SIM, responsavel);
        boolean exigeAnexoBoletoSaldo = ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_BOLETO_SALDO_SERVIDOR, CodedValues.TPC_SIM, responsavel);
        final boolean exigeAnexoDsdSaldoCompra = ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_DSD_SALDO_DEVEDOR_COMPRA, CodedValues.TPC_SIM, responsavel);
        final boolean exigeAnexoBoletoSaldoCompra = ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_BOLETO_SALDO_DEVEDOR_COMPRA, CodedValues.TPC_SIM, responsavel);
        final boolean temModuloFinancDividaCartao = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel);
        final boolean exibePropostaRefinaciamento = !ParamSist.paramEquals(CodedValues.TPC_OMITIR_PROPOSTA_REFINANCIAMENTO_EDT_SLD_DEVEDOR, CodedValues.TPC_SIM, responsavel);

        try {
            if (saldoDevedorController.temSolicitacaoSaldoInformacaoApenas(adeCodigo, responsavel)) {
                exigeAnexoBoletoSaldo = false;
            }
        } catch (final SaldoDevedorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Busca os dados da consignação
        CustomTransferObject autdes = null;
        try {
            autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
            session.setAttribute(CodedValues.MSG_ERRO, "");
        } catch (final AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
        final String csaCodigo = autdes.getAttribute(Columns.CSA_CODIGO).toString();
        final String orgCodigo = autdes.getAttribute(Columns.ORG_CODIGO).toString();

        SaldoDevedorTransferObject saldoDevedorTO = null;
        TransferObject saldosDevedoresMultiplos = null;

        // Busca os parâmetros de serviço necessários
        ParamSvcTO paramSvc = null;
        try {
            paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Verifica se o valor com desconto é obrigatório
        final boolean exigeValorComDesconto = temModuloFinancDividaCartao && isSolicitacaoSaldo && !TextHelper.isNull(paramSvc.getTpsPercentualMinimoDescontoVlrSaldo());
        // Verifica exigência de informação de propostas de pagamento do saldo
        int qtdMinPropostas = 0;
        int qtdMaxPropostas = 9;
        if (temModuloFinancDividaCartao && isSolicitacaoSaldo && !TextHelper.isNull(paramSvc.getTpsQtdPropostasPagamentoParcelSaldo())) {
            try {
                qtdMinPropostas = Integer.parseInt(paramSvc.getTpsQtdPropostasPagamentoParcelSaldo());
            } catch (final NumberFormatException ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.valor.incorreto.param.svc", responsavel, CodedValues.TPS_QTD_PROPOSTAS_PAGAMENTO_PARCEL_SALDO, svcCodigo));
            }
            if (!TextHelper.isNull(paramSvc.getTpsQtdPropostasPagamentoParcelSaldoRef())) {
                try {
                    qtdMaxPropostas = Integer.parseInt(paramSvc.getTpsQtdPropostasPagamentoParcelSaldoRef());
                } catch (final NumberFormatException ex) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.valor.ref.incorreto.param.svc", responsavel, CodedValues.TPS_QTD_PROPOSTAS_PAGAMENTO_PARCEL_SALDO, svcCodigo));
                }
            }
        }
        if (qtdMinPropostas > 0) {
            // Se o serviço requer informação de propostas, verifica se a consignatária possui
            // convênio com o serviço relacionado para financiamento.
            final List<String> svcCodigosDestino = servicoController.obtemServicoRelacionadoComConvenioAtivo(svcCodigo, csaCodigo, orgCodigo, CodedValues.TNT_FINANCIAMENTO_DIVIDA, responsavel);
            if ((svcCodigosDestino == null) || (svcCodigosDestino.size() == 0)) {
                qtdMinPropostas = 0;
            }
        }

        // Configurações para o campo de número de contrato.
        // - A exigência do número de contrato é configurada via parâmetro de serviço.
        final boolean numeroContratoObrigatorio = isCompra ? paramSvc.isTpsExigeNroContratoInfSaldoDevedorCompra() : (isSolicitacaoSaldo ? paramSvc.isTpsExigeNroContratoInfSaldoDevedorSolicSaldo() : false);
        // Se não é solicitação de saldo devedor pelo servidor, então não exige anexos no cadastro de saldo
        final boolean exigeAnexoBoleto = (!isSolicitacaoSaldo ? exigeAnexoBoletoSaldoCompra : exigeAnexoBoletoSaldo);
        // Se não é compra, então não exige anexo do DSD para compra
        final boolean exigeAnexoDsd = (!isCompra ? exigeAnexoDsdSaldo : exigeAnexoDsdSaldoCompra);

        List<?> anexos = null;

        if (exigeAnexoBoleto || exigeAnexoDsd) {
            try {
                final List<String> tarCodigos = new ArrayList<>();
                if (exigeAnexoDsd) {
                    tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_DSD.getCodigo());
                }
                if (exigeAnexoBoleto) {
                    tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_BOLETO.getCodigo());
                }
                final CustomTransferObject cto = new CustomTransferObject();
                cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);
                cto.setAttribute(Columns.AAD_TAR_CODIGO, tarCodigos);
                cto.setAttribute(Columns.AAD_ATIVO, CodedValues.STS_ATIVO);
                anexos = editarAnexoConsignacaoController.lstAnexoAutorizacaoDesconto(cto, -1, -1, responsavel);
            } catch (final AutorizacaoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        // As informações sobre o cálculo do saldo informado pela consignatária serão obrigatórios
        // se ao validar o saldo for detectado que este está fora da faixa limite, e o sistema
        // permite o saldo fora, desde que seja detalhado o cálculo.
        final String detalheInfSaldo = request.getParameter("detalheInfSaldo");
        boolean detalheInfSaldoObrigatorio = false;
        if (!TextHelper.isNull(detalheInfSaldo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.saldo.devedor.limite.invalido", responsavel));
            detalheInfSaldoObrigatorio = true;
        }

        // Busca as informações do saldo devedor da consignação
        try {
            if (!detalheInfSaldoObrigatorio) {
                saldoDevedorTO = saldoDevedorController.getSaldoDevedor(adeCodigo, responsavel);
                if (exigeMultiplosSaldos) {
                    saldosDevedoresMultiplos = saldoDevedorController.recuperaDadosSaldosDevedoresMultiplos(adeCodigo);
                }
            }
        } catch (final SaldoDevedorControllerException ex) {
            LOG.error(ex.getMessage());
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Busca as propostas de pagamento
        Map<Object, TransferObject> propostas = null;
        try {
            if (temModuloFinancDividaCartao) {
                propostas = new HashMap<>();
                final List<?> lstPropostas = financiamentoDividaController.lstPropostaPagamentoDivida(adeCodigo, responsavel.getCsaCodigo(), StatusPropostaEnum.AGUARDANDO_APROVACAO.getCodigo(), responsavel);
                if ((lstPropostas != null) && (lstPropostas.size() > 0)) {
                    final Iterator<?> it = lstPropostas.iterator();
                    while (it.hasNext()) {
                        final TransferObject proposta = (TransferObject) it.next();
                        propostas.put(proposta.getAttribute(Columns.PPD_NUMERO), proposta);
                    }
                }
            }
        } catch (final FinanciamentoDividaControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        final String valorZeroInicial = NumberHelper.format(0, NumberHelper.getLang());

        // Inserção ou alteração de saldo devedor
        final boolean opInserir = (saldoDevedorTO == null);
        // Define os valores da conta de depósito, caso seja alteração
        final String codBancoSaldoDevedor = (opInserir || (saldoDevedorTO.getBcoCodigo() == null)) ? "" : saldoDevedorTO.getBcoCodigo().toString();
        final String codAgenciaSaldoDevedor = opInserir ? "" : saldoDevedorTO.getSdvAgencia();
        final String codContaSaldoDevedor = opInserir ? "" : saldoDevedorTO.getSdvConta();
        final String nomeFavorecidoSdv = opInserir ? "" : saldoDevedorTO.getSdvNomeFavorecido();
        final String cnpjFavorecidoSdv = opInserir ? "" : saldoDevedorTO.getSdvCnpj();
        final String valorSaldoDevedor = opInserir ? valorZeroInicial : NumberHelper.format(saldoDevedorTO.getSdvValor().doubleValue(), NumberHelper.getLang());
        final String valorSaldoDevedorDesc = (opInserir || (saldoDevedorTO.getSdvValorComDesconto() == null)) ? valorZeroInicial : NumberHelper.format(saldoDevedorTO.getSdvValorComDesconto().doubleValue(), NumberHelper.getLang());
        final String numeroContrato = opInserir ? "" : saldoDevedorTO.getSdvNumeroContrato();
        final String observacao = opInserir ? "" : saldoDevedorTO.getObs();
        final String linkBoleto = opInserir ? "" : saldoDevedorTO.getSdvLinkBoletoQuitacao();

        // Valida se o registro servidor está incluso no processo de rescisão
        List<VerbaRescisoriaRse> verbaRescisoriaRse = null;
        try {
            verbaRescisoriaRse = VerbaRescisoriaRseHome.findByPrimaryRseCodigo(autdes.getAttribute(Columns.RSE_CODIGO).toString());
        } catch (final FindException e) {
        }
        final boolean isLimitaSaldoDevedor = paramSvc.isTpsLimitaSaldoDevedorCadastrado();
        final boolean isSaldoRescisao = (verbaRescisoriaRse != null) && !verbaRescisoriaRse.isEmpty();
        final boolean exibeCampoTaxaJurosContratoSaldoDevedor = isLimitaSaldoDevedor && !isSaldoRescisao;

        String taxaJurosContratoSaldoDevedor = null;
        if(exibeCampoTaxaJurosContratoSaldoDevedor) {
            String taxaJuros = "";
            if(autdes.getAttribute(Columns.ADE_TAXA_JUROS) != null) {
                taxaJuros =  NumberHelper.format(Double.parseDouble(autdes.getAttribute(Columns.ADE_TAXA_JUROS).toString()), NumberHelper.getLang());
            }
            taxaJurosContratoSaldoDevedor = opInserir ? "" : taxaJuros;
        }

        String qtdePrestacoes = "";
        String dataSaldoDevedor1 = "";
        String valorSaldoDevedor1 = "";
        String dataSaldoDevedor2 = "";
        String valorSaldoDevedor2 = "";
        String dataSaldoDevedor3 = "";
        String valorSaldoDevedor3 = "";

        if (saldosDevedoresMultiplos != null) {
            qtdePrestacoes = saldosDevedoresMultiplos.getAttribute(CodedValues.TDA_SDV_QTDE_PRESTACOES) != null ? saldosDevedoresMultiplos.getAttribute(CodedValues.TDA_SDV_QTDE_PRESTACOES).toString() : "";
            dataSaldoDevedor1 = saldosDevedoresMultiplos.getAttribute(CodedValues.TDA_SDV_DATA_VCTO1) != null ? saldosDevedoresMultiplos.getAttribute(CodedValues.TDA_SDV_DATA_VCTO1).toString() : "";
            valorSaldoDevedor1 = saldosDevedoresMultiplos.getAttribute(CodedValues.TDA_SDV_VALOR_VCTO1) != null ? NumberHelper.reformat(saldosDevedoresMultiplos.getAttribute(CodedValues.TDA_SDV_VALOR_VCTO1).toString(), "en", NumberHelper.getLang()) : "";
            dataSaldoDevedor2 = saldosDevedoresMultiplos.getAttribute(CodedValues.TDA_SDV_DATA_VCTO2) != null ? saldosDevedoresMultiplos.getAttribute(CodedValues.TDA_SDV_DATA_VCTO2).toString() : "";
            valorSaldoDevedor2 = saldosDevedoresMultiplos.getAttribute(CodedValues.TDA_SDV_VALOR_VCTO2) != null ? NumberHelper.reformat(saldosDevedoresMultiplos.getAttribute(CodedValues.TDA_SDV_VALOR_VCTO2).toString(), "en", NumberHelper.getLang()) : "";
            dataSaldoDevedor3 = saldosDevedoresMultiplos.getAttribute(CodedValues.TDA_SDV_DATA_VCTO3) != null ? saldosDevedoresMultiplos.getAttribute(CodedValues.TDA_SDV_DATA_VCTO3).toString() : "";
            valorSaldoDevedor3 = saldosDevedoresMultiplos.getAttribute(CodedValues.TDA_SDV_VALOR_VCTO3) != null ? NumberHelper.reformat(saldosDevedoresMultiplos.getAttribute(CodedValues.TDA_SDV_VALOR_VCTO3).toString(), "en", NumberHelper.getLang()) : "";
        }

        // Busca os parâmetros de serviço da consignatária
        String codBancoPadraoCsa = "";
        String codAgenciaPadraoCsa = "";
        String codContaPadraoCsa = "";
        String nomeFavorecidoPadraoCsa = "";
        String cnpjFavorecidoPadraoCsa = "";
        try {
            final List<String> tpsCodigos = new ArrayList<>();
            // Parâmetros da conta padrão de depósito
            tpsCodigos.add(CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR);
            tpsCodigos.add(CodedValues.TPS_AGENCIA_DEPOSITO_SALDO_DEVEDOR);
            tpsCodigos.add(CodedValues.TPS_CONTA_DEPOSITO_SALDO_DEVEDOR);
            tpsCodigos.add(CodedValues.TPS_NOME_FAVORECIDO_DEPOSITO_SDV);
            tpsCodigos.add(CodedValues.TPS_CNPJ_FAVORECIDO_DEPOSITO_SDV);

            // Busca os parâmetros
            final List<?> parametros = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, false, responsavel);

            final Iterator<?> itParametros = parametros.iterator();
            CustomTransferObject next = null;
            while (itParametros.hasNext()) {
                next = (CustomTransferObject) itParametros.next();
                if (CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    codBancoPadraoCsa = (String) next.getAttribute(Columns.PSC_VLR);
                } else if (CodedValues.TPS_AGENCIA_DEPOSITO_SALDO_DEVEDOR.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    codAgenciaPadraoCsa = (String) next.getAttribute(Columns.PSC_VLR);
                } else if (CodedValues.TPS_CONTA_DEPOSITO_SALDO_DEVEDOR.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    codContaPadraoCsa = (String) next.getAttribute(Columns.PSC_VLR);
                } else if (CodedValues.TPS_NOME_FAVORECIDO_DEPOSITO_SDV.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    nomeFavorecidoPadraoCsa = (String) next.getAttribute(Columns.PSC_VLR);
                } else if (CodedValues.TPS_CNPJ_FAVORECIDO_DEPOSITO_SDV.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                    cnpjFavorecidoPadraoCsa = (String) next.getAttribute(Columns.PSC_VLR);
                }
            }
        } catch (final ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        model.addAttribute("exibePropostaRefinaciamento", exibePropostaRefinaciamento);
        model.addAttribute("tipo", tipo);
        model.addAttribute("showInfBancarias", showInfBancarias);
        model.addAttribute("exigeMultiplosSaldos", exigeMultiplosSaldos);
        model.addAttribute("exigeValorComDesconto", exigeValorComDesconto);
        model.addAttribute("numeroContratoObrigatorio", numeroContratoObrigatorio);
        model.addAttribute("infSaldoDevedorOpcional", infSaldoDevedorOpcional);
        model.addAttribute("exibeCamposdvLinkBoleto", exibeCamposdvLinkBoleto);
        model.addAttribute("exibeCampoTaxaJurosContratoSaldoDevedor", exibeCampoTaxaJurosContratoSaldoDevedor);
        model.addAttribute("opInserir", opInserir);
        model.addAttribute("exigeAnexoBoleto", exigeAnexoBoleto);
        model.addAttribute("exigeAnexoDsd", exigeAnexoDsd);
        model.addAttribute("isCompra", isCompra);
        model.addAttribute("detalheInfSaldoObrigatorio", detalheInfSaldoObrigatorio);
        model.addAttribute("qtdMinPropostas", qtdMinPropostas);
        model.addAttribute("qtdMaxPropostas", qtdMaxPropostas);
        model.addAttribute("anexos", anexos);
        model.addAttribute("propostas", propostas);
        model.addAttribute("codBancoPadraoCsa", codBancoPadraoCsa);
        model.addAttribute("codAgenciaPadraoCsa", codAgenciaPadraoCsa);
        model.addAttribute("codContaPadraoCsa", codContaPadraoCsa);
        model.addAttribute("nomeFavorecidoPadraoCsa", nomeFavorecidoPadraoCsa);
        model.addAttribute("cnpjFavorecidoPadraoCsa", cnpjFavorecidoPadraoCsa);
        model.addAttribute("codBancoSaldoDevedor", codBancoSaldoDevedor);
        model.addAttribute("adeCodigo", adeCodigo);
        model.addAttribute("autdes", autdes);
        model.addAttribute("taxaJurosContratoSaldoDevedor", taxaJurosContratoSaldoDevedor);
        model.addAttribute("valorSaldoDevedor", valorSaldoDevedor);
        model.addAttribute("valorSaldoDevedor1", valorSaldoDevedor1);
        model.addAttribute("valorSaldoDevedor2", valorSaldoDevedor2);
        model.addAttribute("dataSaldoDevedor1", dataSaldoDevedor1);
        model.addAttribute("valorSaldoDevedor3", valorSaldoDevedor3);
        model.addAttribute("dataSaldoDevedor2", dataSaldoDevedor2);
        model.addAttribute("dataSaldoDevedor3", dataSaldoDevedor3);
        model.addAttribute("qtdePrestacoes", qtdePrestacoes);
        model.addAttribute("valorSaldoDevedorDesc", valorSaldoDevedorDesc);
        model.addAttribute("codAgenciaSaldoDevedor", codAgenciaSaldoDevedor);
        model.addAttribute("codContaSaldoDevedor", codContaSaldoDevedor);
        model.addAttribute("nomeFavorecidoSdv", nomeFavorecidoSdv);
        model.addAttribute("cnpjFavorecidoSdv", cnpjFavorecidoSdv);
        model.addAttribute("numeroContrato", numeroContrato);
        model.addAttribute("linkBoleto", linkBoleto);
        model.addAttribute("observacao", observacao);
        model.addAttribute("isSaldoRescisao", isSaldoRescisao);
        model.addAttribute("urlEditarSaldoDevedor", getUrlEditarSaldoDevedor());

        return viewRedirect("jsp/acompanharPortabilidade/editarSaldoDevedor", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(@RequestParam(value = "ADE_CODIGO", required = true, defaultValue = "") String ADE_CODIGO, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServicoControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String adeCodigo = request.getParameter("ADE_CODIGO");

        // Busca as informações sobre a consignação
        CustomTransferObject autdes = null;
        try {
            autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
        } catch (final AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
        final String csaCodigo = autdes.getAttribute(Columns.CSA_CODIGO).toString();
        final String orgCodigo = autdes.getAttribute(Columns.ORG_CODIGO).toString();

        // Variáveis necessárias:
        final String tipo = JspHelper.verificaVarQryStr(request, "tipo");
        final boolean isCompra = "compra".equalsIgnoreCase(tipo);
        final boolean isSolicitacaoSaldo = !isCompra;
        final boolean exigeAnexoBoletoSaldoCompra = ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_BOLETO_SALDO_DEVEDOR_COMPRA, CodedValues.TPC_SIM, responsavel);
        boolean exigeAnexoBoletoSaldo = ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_BOLETO_SALDO_SERVIDOR, CodedValues.TPC_SIM, responsavel);

        try {
            if (saldoDevedorController.temSolicitacaoSaldoInformacaoApenas(adeCodigo, responsavel)) {
                exigeAnexoBoletoSaldo = false;
            }
        } catch (final SaldoDevedorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final boolean exigeAnexoBoleto = (!isSolicitacaoSaldo ? exigeAnexoBoletoSaldoCompra : exigeAnexoBoletoSaldo);
        final boolean exigeAnexoDsdSaldo = ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_DSD_SALDO_SERVIDOR, CodedValues.TPC_SIM, responsavel);
        final boolean exigeAnexoDsdSaldoCompra = ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_DSD_SALDO_DEVEDOR_COMPRA, CodedValues.TPC_SIM, responsavel);
        final boolean exigeAnexoDsd = (!isCompra ? exigeAnexoDsdSaldo : exigeAnexoDsdSaldoCompra);
        final String paramTamMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_CONTRATO, responsavel);
        final int tamMaxArqAnexo = (!TextHelper.isNull(paramTamMaxArqAnexo) ? Integer.parseInt(paramTamMaxArqAnexo) : 200);
        final boolean exigeMultiplosSaldos = ParamSist.paramEquals(CodedValues.TPC_EXIGE_MULTIPLOS_SALDOS_DEVEDORES, CodedValues.TPC_SIM, responsavel);
        final boolean temModuloFinancDividaCartao = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel);

        // Busca os parâmetros de serviço necessários
        ParamSvcTO paramSvc = null;
        try {
            paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        final boolean numeroContratoObrigatorio = isCompra ? paramSvc.isTpsExigeNroContratoInfSaldoDevedorCompra() : (isSolicitacaoSaldo ? paramSvc.isTpsExigeNroContratoInfSaldoDevedorSolicSaldo() : false);
        final boolean exigeValorComDesconto = temModuloFinancDividaCartao && isSolicitacaoSaldo && !TextHelper.isNull(paramSvc.getTpsPercentualMinimoDescontoVlrSaldo());
        // Valida se o registro servidor está incluso no processo de rescisão
        List<VerbaRescisoriaRse> verbaRescisoriaRse = null;
        final String rseCodigo = autdes.getAttribute(Columns.RSE_CODIGO).toString();
        try {
            verbaRescisoriaRse = VerbaRescisoriaRseHome.findByPrimaryRseCodigo(rseCodigo);
        } catch (final FindException e) {
        }
        final boolean isLimitaSaldoDevedor = paramSvc.isTpsLimitaSaldoDevedorCadastrado();
        final boolean isSaldoRescisao = (verbaRescisoriaRse != null) && !verbaRescisoriaRse.isEmpty();
        final boolean exigeCampoTaxaJurosContratoSaldoDevedor = isLimitaSaldoDevedor && !isSaldoRescisao;

        SaldoDevedorTransferObject saldoDevedorTO = null;
        TransferObject saldosDevedoresMultiplos = null;

        // Verifica exigência de informação de propostas de pagamento do saldo
        int qtdMinPropostas = 0;
        int qtdMaxPropostas = 9;
        if (temModuloFinancDividaCartao && isSolicitacaoSaldo && !TextHelper.isNull(paramSvc.getTpsQtdPropostasPagamentoParcelSaldo())) {
            try {
                qtdMinPropostas = Integer.parseInt(paramSvc.getTpsQtdPropostasPagamentoParcelSaldo());
            } catch (final NumberFormatException ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.valor.incorreto.param.svc", responsavel, CodedValues.TPS_QTD_PROPOSTAS_PAGAMENTO_PARCEL_SALDO, svcCodigo));
            }
            if (!TextHelper.isNull(paramSvc.getTpsQtdPropostasPagamentoParcelSaldoRef())) {
                try {
                    qtdMaxPropostas = Integer.parseInt(paramSvc.getTpsQtdPropostasPagamentoParcelSaldoRef());
                } catch (final NumberFormatException ex) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.valor.ref.incorreto.param.svc", responsavel, CodedValues.TPS_QTD_PROPOSTAS_PAGAMENTO_PARCEL_SALDO, svcCodigo));
                }
            }
        }
        if (qtdMinPropostas > 0) {
            // Se o serviço requer informação de propostas, verifica se a consignatária possui
            // convênio com o serviço relacionado para financiamento.
            final List<String> svcCodigosDestino = servicoController.obtemServicoRelacionadoComConvenioAtivo(svcCodigo, csaCodigo, orgCodigo, CodedValues.TNT_FINANCIAMENTO_DIVIDA, responsavel);
            if ((svcCodigosDestino == null) || (svcCodigosDestino.size() == 0)) {
                qtdMinPropostas = 0;
            }
        }

        Map<?, ?> novosAnexos = null;
        List<?> anexos = null;

        if (exigeAnexoBoleto || exigeAnexoDsd) {
            try {
                final List<String> tarCodigos = new ArrayList<>();
                if (exigeAnexoDsd) {
                    tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_DSD.getCodigo());
                }
                if (exigeAnexoBoleto) {
                    tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_BOLETO.getCodigo());
                }
                final CustomTransferObject cto = new CustomTransferObject();
                cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);
                cto.setAttribute(Columns.AAD_TAR_CODIGO, tarCodigos);
                cto.setAttribute(Columns.AAD_ATIVO, CodedValues.STS_ATIVO);
                anexos = editarAnexoConsignacaoController.lstAnexoAutorizacaoDesconto(cto, -1, -1, responsavel);
            } catch (final AutorizacaoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        // As informações sobre o cálculo do saldo informado pela consignatária serão obrigatórios
        // se ao validar o saldo for detectado que este está fora da faixa limite, e o sistema
        // permite o saldo fora, desde que seja detalhado o cálculo.
        boolean detalheInfSaldoObrigatorio = false;

        // Salva a inserção/alteração
        try {
            UploadHelper uploadHelper = null;
            if (exigeAnexoBoleto || exigeAnexoDsd) {
                // Se exige anexos no cadastro de saldo devedor, cria o upload helper para tratar os
                // campos do formulário, incluindo os campos de arquivo.
                uploadHelper = new UploadHelper();
                uploadHelper.processarRequisicao(request.getServletContext(), request, tamMaxArqAnexo * 1024);
            }

            final String codBancoSaldoDevedor = JspHelper.verificaVarQryStr(request, uploadHelper, "banco");
            final String codAgenciaSaldoDevedor = JspHelper.verificaVarQryStr(request, uploadHelper, "agencia");
            final String codContaSaldoDevedor = JspHelper.verificaVarQryStr(request, uploadHelper, "conta");
            final String nomeFavorecidoSdv = JspHelper.verificaVarQryStr(request, uploadHelper, "nomeFavorecido");
            final String cnpjFavorecidoSdv = JspHelper.verificaVarQryStr(request, uploadHelper, "cnpjFavorecido");
            final String valorSaldoDevedor = JspHelper.verificaVarQryStr(request, uploadHelper, (exigeMultiplosSaldos ? "valorSaldoDevedor1" : "valorSaldoDevedor"));
            final String valorSaldoDevedorDesc = JspHelper.verificaVarQryStr(request, uploadHelper, "valorSaldoDevedorDesc");
            final String observacao = JspHelper.verificaVarQryStr(request, uploadHelper, "obs");
            String detalhe = JspHelper.verificaVarQryStr(request, uploadHelper, "detalhe");
            final String numeroContrato = JspHelper.verificaVarQryStr(request, uploadHelper, "numeroContrato");
            final String sdvLinkBoleto = JspHelper.verificaVarQryStr(request, uploadHelper, "sdvLinkBoleto");
            final String taxaJurosContratoSdv = JspHelper.verificaVarQryStr(request, uploadHelper, "taxaJurosContratoSaldoDevedor");

            if (numeroContratoObrigatorio && TextHelper.isNull(numeroContrato)) {
                throw new SaldoDevedorControllerException("mensagem.informe.numero.contrato", responsavel);
            }

            BigDecimal sdvValor = null;
            try {
                sdvValor = new BigDecimal(NumberHelper.reformat(valorSaldoDevedor, NumberHelper.getLang(), "en"));
            } catch (final ParseException ex) {
                throw new SaldoDevedorControllerException("mensagem.erro.saldo.devedor.valor.incorreto", responsavel, ex);
            }
            if (sdvValor.signum() <= 0) {
                throw new SaldoDevedorControllerException("mensagem.erro.saldo.devedor.maior.zero", responsavel);
            }

            BigDecimal sdvValorComDesconto = null;
            if (exigeValorComDesconto) {
                try {
                    sdvValorComDesconto = new BigDecimal(NumberHelper.reformat(valorSaldoDevedorDesc, NumberHelper.getLang(), "en"));
                } catch (final ParseException ex) {
                    throw new SaldoDevedorControllerException("mensagem.erro.saldo.devedor.desconto.valor.incorreto", responsavel, ex);
                }
                if (sdvValorComDesconto.signum() <= 0) {
                    throw new SaldoDevedorControllerException("mensagem.erro.saldo.devedor.desconto.maior.zero", responsavel);
                }
            }

            BigDecimal taxaJurosContratoSaldoDevedor = null;
            if(exigeCampoTaxaJurosContratoSaldoDevedor) {
                if (TextHelper.isNull(taxaJurosContratoSdv)) {
                    throw new SaldoDevedorControllerException("mensagem.informe.taxa.juros.contrato", responsavel);
                }
                try {
                    taxaJurosContratoSaldoDevedor = new BigDecimal(NumberHelper.reformat(taxaJurosContratoSdv, NumberHelper.getLang(), "en"));
                } catch (final ParseException ex) {
                    throw new SaldoDevedorControllerException("mensagem.erro.taxa.juros.contrato.incorreto", responsavel, ex);
                }
                if (taxaJurosContratoSaldoDevedor.signum() <= 0) {
                    throw new SaldoDevedorControllerException("mensagem.erro.taxa.juros.contrato.maior.zero", responsavel);
                }

                // Atualiza ade taxa de juros
                saldoDevedorController.alterarAdeTaxaJuros(adeCodigo, taxaJurosContratoSaldoDevedor, responsavel);
            }

            // Cria TO com os dados do saldo devedor
            saldoDevedorTO = new SaldoDevedorTransferObject();
            saldoDevedorTO.setAdeCodigo(adeCodigo);
            saldoDevedorTO.setUsuCodigo((responsavel != null ? responsavel.getUsuCodigo() : null));
            saldoDevedorTO.setBcoCodigo(!TextHelper.isNull(codBancoSaldoDevedor) ? Short.valueOf(codBancoSaldoDevedor) : null);
            saldoDevedorTO.setSdvAgencia(codAgenciaSaldoDevedor != null ? codAgenciaSaldoDevedor : "");
            saldoDevedorTO.setSdvConta(codContaSaldoDevedor != null ? codContaSaldoDevedor : "");
            saldoDevedorTO.setSdvNomeFavorecido(nomeFavorecidoSdv != null ? nomeFavorecidoSdv : "");
            saldoDevedorTO.setSdvCnpj(cnpjFavorecidoSdv != null ? cnpjFavorecidoSdv : "");
            saldoDevedorTO.setObs(observacao);
            saldoDevedorTO.setSdvValor(sdvValor);
            saldoDevedorTO.setSdvValorComDesconto(sdvValorComDesconto);
            saldoDevedorTO.setSdvLinkBoletoQuitacao(sdvLinkBoleto);
            saldoDevedorTO.setSdvNumeroContrato(!TextHelper.isNull(numeroContrato) ? numeroContrato : null);

            // Cria TO com os dados dos múltiplos saldos devedores
            if (exigeMultiplosSaldos) {
                final String dataCadastro = DateHelper.toDateTimeString(DateHelper.getSystemDatetime());
                final String qtdePrestacoes = JspHelper.verificaVarQryStr(request, uploadHelper, "qtdePrestacoes");
                String valorSaldoDevedor1 = JspHelper.verificaVarQryStr(request, uploadHelper, "valorSaldoDevedor1");
                final String dataSaldoDevedor1 = JspHelper.verificaVarQryStr(request, uploadHelper, "dataSaldoDevedor1");
                String valorSaldoDevedor2 = JspHelper.verificaVarQryStr(request, uploadHelper, "valorSaldoDevedor2");
                final String dataSaldoDevedor2 = JspHelper.verificaVarQryStr(request, uploadHelper, "dataSaldoDevedor2");
                String valorSaldoDevedor3 = JspHelper.verificaVarQryStr(request, uploadHelper, "valorSaldoDevedor3");
                final String dataSaldoDevedor3 = JspHelper.verificaVarQryStr(request, uploadHelper, "dataSaldoDevedor3");

                final StringBuilder obs = new StringBuilder();
                obs.append("<BR>").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.saldo.devedor.qtde.prd.liquidada", responsavel)).append(":").append(qtdePrestacoes);
                obs.append("<BR>").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.saldo.devedor.vencimento", responsavel, dataSaldoDevedor1, valorSaldoDevedor1));
                obs.append("<BR>").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.saldo.devedor.vencimento", responsavel, dataSaldoDevedor2, valorSaldoDevedor2));
                obs.append("<BR>").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.saldo.devedor.vencimento", responsavel, dataSaldoDevedor3, valorSaldoDevedor3));
                if (!"".equals(observacao)) {
                    obs.append("<BR>").append(ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.observacao.abreviado", responsavel).toUpperCase()).append(": ").append(observacao);
                }

                // Valores a serem usados na rotina padrão.
                saldoDevedorTO.setObs(obs.toString());

                // Salva as informações na DadosAutorizacaoDesconto
                valorSaldoDevedor1 = NumberHelper.reformat(valorSaldoDevedor1, NumberHelper.getLang(), "en");
                valorSaldoDevedor2 = NumberHelper.reformat(valorSaldoDevedor2, NumberHelper.getLang(), "en");
                valorSaldoDevedor3 = NumberHelper.reformat(valorSaldoDevedor3, NumberHelper.getLang(), "en");

                saldosDevedoresMultiplos = new CustomTransferObject();
                saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_DATACADASTRO, dataCadastro);
                saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_QTDE_PRESTACOES, qtdePrestacoes);
                saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_DATA_VCTO1, dataSaldoDevedor1);
                saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_VALOR_VCTO1, valorSaldoDevedor1);
                saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_DATA_VCTO2, dataSaldoDevedor2);
                saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_VALOR_VCTO2, valorSaldoDevedor2);
                saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_DATA_VCTO3, dataSaldoDevedor3);
                saldosDevedoresMultiplos.setAttribute(CodedValues.TDA_SDV_VALOR_VCTO3, valorSaldoDevedor3);
            }

            // Cria lista com as propostas para pagamento do saldo
            List<PropostaPagamentoDividaTO> propostasPgtSaldo = null;
            if (qtdMinPropostas > 0) {
                propostasPgtSaldo = new ArrayList<>();
                for (int i = 1; i <= qtdMaxPropostas; i++) {
                    final String cdgProposta = JspHelper.verificaVarQryStr(request, uploadHelper, "cdgProposta" + i);
                    final String przProposta = JspHelper.verificaVarQryStr(request, uploadHelper, "przProposta" + i);
                    final String vlrProposta = JspHelper.verificaVarQryStr(request, uploadHelper, "vlrProposta" + i);
                    if (!TextHelper.isNull(przProposta) && !TextHelper.isNull(vlrProposta)) {
                        try {
                            final PropostaPagamentoDividaTO proposta = new PropostaPagamentoDividaTO();
                            proposta.setPpdCodigo((!TextHelper.isNull(cdgProposta)) ? cdgProposta : null);
                            proposta.setAdeCodigo(adeCodigo);
                            proposta.setCsaCodigo(csaCodigo);
                            proposta.setUsuCodigo(responsavel.getUsuCodigo());
                            proposta.setPpdNumero(i);
                            proposta.setPpdValorDivida(exigeValorComDesconto ? sdvValorComDesconto : sdvValor);
                            proposta.setPpdValorParcela(new BigDecimal(NumberHelper.reformat(vlrProposta, NumberHelper.getLang(), "en")));
                            proposta.setPpdPrazo(Integer.valueOf(przProposta));
                            propostasPgtSaldo.add(proposta);
                        } catch (ParseException | NumberFormatException ex) {
                            throw new SaldoDevedorControllerException("mensagem.log.erro.valor.incorreto.proposta", responsavel, ex, String.valueOf(i));
                        }
                    }
                }
                if (propostasPgtSaldo.size() < qtdMinPropostas) {
                    throw new SaldoDevedorControllerException("mensagem.log.erro.qtde.proposta.minimo", responsavel, String.valueOf(qtdMinPropostas));
                } else if (propostasPgtSaldo.size() > qtdMaxPropostas) {
                    throw new SaldoDevedorControllerException("mensagem.log.erro.qtde.proposta.maximo", responsavel, String.valueOf(qtdMaxPropostas));
                }
            }

            // Se limite saldo cadastrado e permite saldo fora da faixa limite, então chama rotina para validação
            // de saldo, e caso retorne falso, o saldo está acima da faixa limite, portanto a consignatária
            // deve informar os detalhes do cálculo do saldo.
            final boolean limitaSaldoDevedor = responsavel.isCseSupOrg() ? paramSvc.isTpsLimitaSaldoDevedorCadastradoCseOrgSup() : paramSvc.isTpsLimitaSaldoDevedorCadastrado();
            if ((limitaSaldoDevedor && paramSvc.isTpsPermiteSaldoForaFaixaLimite()) && !saldoDevedorController.validarSaldoDevedor(saldoDevedorTO, saldosDevedoresMultiplos, propostasPgtSaldo, responsavel)) {
                if (TextHelper.isNull(detalhe)) {
                    // Se não foi informado os detalhes, então retorna à interface de informação de saldo
                    // para que o usuário possa preencher este campo.
                    detalheInfSaldoObrigatorio = true;
                    saldoDevedorTO.setObs(observacao); // Volta obs original
                } else {
                    // Se já foi informado o detalhe, então prossegue a atualização das informações
                    detalhe = detalhe.replace("\r\n", "<BR>").replace("\n", "<BR>");
                    saldoDevedorTO.setObs(saldoDevedorTO.getObs() + "<BR><B>" + ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.detalhe", responsavel).toUpperCase() + ":</B> " + detalhe);
                }
            }
            final String path = "anexo" + File.separatorChar + DateHelper.format(((Date) autdes.getAttribute(Columns.ADE_DATA)), "yyyyMMdd") + File.separatorChar + adeCodigo;

            if (!detalheInfSaldoObrigatorio) {
                if (exigeAnexoBoleto || exigeAnexoDsd) {
                    novosAnexos = uploadHelper.salvarArquivos(path, UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_SALDO_DEVEDOR, null, null, false);
                    boolean possuiAnexoDsd = false;
                    boolean possuiAnexoBoleto = false;
                    if (anexos != null) {
                        final Iterator<?> iteAnexos = anexos.iterator();
                        while (iteAnexos.hasNext()) {
                            final TransferObject anexo = (TransferObject) iteAnexos.next();

                            final String tarCodigo = anexo.getAttribute(Columns.TAR_CODIGO).toString();
                            if (exigeAnexoDsd && tarCodigo.equals(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_DSD.getCodigo())) {
                                possuiAnexoDsd = true;
                            }
                            if (exigeAnexoBoleto && tarCodigo.equals(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_BOLETO.getCodigo())) {
                                possuiAnexoBoleto = true;
                            }
                        }
                    }
                    // Se exige boleto e o boleto não foi anexado, então retorna erro
                    if (exigeAnexoBoleto && !possuiAnexoBoleto && ((novosAnexos == null) || TextHelper.isNull(novosAnexos.get("anexo_boleto")))) {
                        throw new SaldoDevedorControllerException("mensagem.informe.anexo.saldo.boleto", responsavel);
                    }
                    // Se exige DSD e o DSD não foi anexado, então retorna erro
                    if (exigeAnexoDsd && !possuiAnexoDsd && ((novosAnexos == null) || TextHelper.isNull(novosAnexos.get("anexo_dsd")))) {
                        throw new SaldoDevedorControllerException("mensagem.informe.anexo.saldo.dsd", responsavel);
                    }

                    if (exigeAnexoBoleto && ((novosAnexos != null) && (novosAnexos.size() >= 1) && !TextHelper.isNull(novosAnexos.get("anexo_boleto")))) {
                        // Se os anexos foram informados, inclui no objeto de saldo devedor para serem salvos
                        saldoDevedorTO.setAnexoBoleto((File) novosAnexos.get("anexo_boleto"));
                    }
                    if (exigeAnexoDsd && ((novosAnexos != null) && (novosAnexos.size() >= 1) && !TextHelper.isNull(novosAnexos.get("anexo_dsd")))) {
                        // Se o anexo do DSD foi informados, inclui no objeto de saldo devedor para ser salvo
                        saldoDevedorTO.setAnexoDsd((File) novosAnexos.get("anexo_dsd"));
                    }
                }

                final String textoRefinanciamentoParcela = JspHelper.verificaVarQryStr(request, "exibeRefinancimanetoReducaoParcelasText");
                final CustomTransferObject novaCmn = new CustomTransferObject();

                if (!TextHelper.isNull(textoRefinanciamentoParcela)) {

                    final String serEmail = autdes.getAttribute(Columns.SER_EMAIL) != null ? autdes.getAttribute(Columns.SER_EMAIL).toString() : "";

                    final StringBuilder textoComunicacao = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.info.saldo.devedor.refinanciamento.parcelas",responsavel,autdes.getAttribute(Columns.SER_NOME).toString(),autdes.getAttribute(Columns.CSA_NOME).toString())).append("<br/>\n<br/>\n");

                    textoComunicacao.append(textoRefinanciamentoParcela);
                    // Cria comunicacao
                    novaCmn.setAttribute(Columns.PAP_CODIGO, CodedValues.PAP_SERVIDOR);
                    novaCmn.setAttribute(Columns.CMN_TEXTO, textoComunicacao.toString());
                    novaCmn.setAttribute(Columns.CSA_CODIGO, csaCodigo);
                    novaCmn.setAttribute(Columns.SER_CODIGO, autdes.getAttribute(Columns.SER_CODIGO).toString());
                    novaCmn.setAttribute(Columns.CMN_ASC_CODIGO, CodedValues.ASSUNTO_REFINANCIAMENTO_PROPOSTA);
                    novaCmn.setAttribute(Columns.CMN_IP_ACESSO, JspHelper.getRemoteAddr(request));
                    novaCmn.setAttribute(Columns.RSE_CODIGO, autdes.getAttribute(Columns.RSE_CODIGO).toString());
                    novaCmn.setAttribute(Columns.SER_EMAIL, serEmail);
                    novaCmn.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                }

                // Atualiza as informações
                if (saldoDevedorController.getSaldoDevedor(adeCodigo, responsavel) == null) {
                    saldoDevedorController.createSaldoDevedor(saldoDevedorTO, saldosDevedoresMultiplos, propostasPgtSaldo, isCompra, novaCmn, responsavel);
                } else {
                    saldoDevedorController.updateSaldoDevedor(saldoDevedorTO, saldosDevedoresMultiplos, propostasPgtSaldo, isCompra, novaCmn, responsavel);
                }

                // Verifica se a consignatária pode ser desbloqueada automaticamente
                if (consignatariaController.verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel)) {
                    session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.informacao.csa.desbloqueada.automaticamente", responsavel));
                }

                final boolean cadastroTaxaJurosObrigatorio = paramSvc.isTpsExigeCadastroTaxaJurosParaCet();
                final boolean criaAutoContratoRescisaoAposSdv = ParamSist.paramEquals(CodedValues.TPC_CRIA_AUTO_CONTRATO_RESCISAO_APOS_INFO_SALDO_DEVEDOR, CodedValues.TPC_SIM, responsavel);
                final String permitirEditarSaldoDevedorNovamenteValue = JspHelper.verificaVarQryStr(request, uploadHelper, "permitirEditarSaldoDevedorNovamente");
                final boolean permitirEditarSaldoDevedorNovamente = !TextHelper.isNull(permitirEditarSaldoDevedorNovamenteValue) && Boolean.valueOf(permitirEditarSaldoDevedorNovamenteValue);
                boolean isSdvInformadoMenorIgualQueSaldoCalculado = false;

                if(cadastroTaxaJurosObrigatorio) {
                    final BigDecimal calculoSaldoDevedor = saldoDevedorController.calcularSaldoDevedor(adeCodigo, cadastroTaxaJurosObrigatorio, responsavel);
                    isSdvInformadoMenorIgualQueSaldoCalculado = sdvValor.compareTo(calculoSaldoDevedor) <= 0;
                }

                if((!permitirEditarSaldoDevedorNovamente || isSdvInformadoMenorIgualQueSaldoCalculado) && criaAutoContratoRescisaoAposSdv) {
                    verbaRescisoriaController.confirmarVerbaRescisoria(sdvValor, adeCodigo, responsavel);

                    final int totalContratosNaoPossuemRelacionamentoVerbaRescisoria = pesquisarConsignacaoController.contaContratosNaoPossuemRelacionamentoVerbaRescisoria(rseCodigo, responsavel);
                    //Se todos os contratos de empréstimo e cartão abertos do registro servidor possuem relacionamento natureza (54), conclui a verba
                    if(totalContratosNaoPossuemRelacionamentoVerbaRescisoria == 0) {
                        verbaRescisoriaController.concluirVerbaRescisoria(verbaRescisoriaRse, responsavel);
                    }
                }

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.saldo.devedor.sucesso", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

            } else {
                request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                return "forward:" + getUrlEditarSaldoDevedor() + "?acao=iniciar&ADE_CODIGO="+adeCodigo+"&detalheInfSaldo=true&_skip_history_=true";
            }

        } catch (ZetraException | ParseException ex) {
            // Em caso de erro, verifica se existem anexos, e caso existam, realiza a remoção
            if ((novosAnexos != null) && (novosAnexos.size() > 0)) {
                final Iterator<?> arquivosAnexo = novosAnexos.values().iterator();
                while (arquivosAnexo.hasNext()) {
                    final File anexo = (File) arquivosAnexo.next();
                    anexo.delete();
                }
            }
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=consultarCalculoSaldoDevedor" })
    @ResponseBody
    public ResponseEntity<?> getCalculoSaldoDevedor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServicoControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        final String adeCodigo = request.getParameter("adeCodigo");
        try {
            final BigDecimal calculoSaldoDevedor = saldoDevedorController.calcularSaldoDevedor(adeCodigo, true, responsavel);
            return new ResponseEntity<>(calculoSaldoDevedor, HttpStatus.OK);
        } catch (final SaldoDevedorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            final Map<String, String> erro = new HashMap<>();
            erro.put("mensagem", ApplicationResourcesHelper.getMessage("mensagem.erro.saldo.devedor.rescicao.calculo", responsavel));
            return new ResponseEntity<>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    protected String getUrlEditarSaldoDevedor() {
    	return "/v3/editarSaldoDevedor";
    }
}
