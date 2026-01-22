package com.zetra.econsig.web.tag;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.LimiteTaxaJurosControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.SegurancaControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.periodo.RepasseHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.calendario.CalendarioController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.juros.LimiteTaxaJurosController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.seguranca.SegurancaController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * <p>Title: ConfigSistemaTag</p>
 * <p>Description: Tag para impressão de informações básicas de configuração do sistema.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class ConfigSistemaTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConfigSistemaTag.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private LimiteTaxaJurosController limiteTaxaJurosController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private SegurancaController segurancaController;

    @Autowired
    private CalendarioController calendarioController;

    protected AcessoSistema responsavel;
    protected String nivelSeguranca = null;
    protected String periodoAtual = null;
    protected String dataHoraSistema = null;
    protected Integer diaCorte = null;
    protected Integer diaRepasse = null;
    protected Integer diaCorteCsa = null;
    protected Map<String, String> periodoOrgaos = null;
    protected Map<String, Object> diaCorteOrgaos = null;
    protected Map<String, Integer> diaRepasseOrgaos = null;
    protected List<ConfiguracaoTaxa> taxasServicos = null;
    protected List<ConfiguracaoServicoCancelamentoAutomatico> configuracaoServicosCancelamentoAutomatico = null;
    protected List<ConfiguracaoServicoModuloAvancadoCompra> configuracaoServicosModuloAvancadoCompra = null;
    protected List<ConfiguracaoServicoCompraContrato> configuracaoServicosCompraContrato = null;
    protected List<ConfiguracaoServicoRenegociacaoContrato> configuracaoServicosRenegociacaoContrato = null;
    protected List<ConfiguracaoModulosSistema> configuracaoModulosSistema = null;

    protected boolean cadastraTaxas = false;
    protected boolean temCET = false;
    protected boolean temSimulacao = false;
    protected boolean temModuloCompra = false;
    protected boolean temModuloAvancadoCompras = false;
    protected boolean usaDiasUteisControleCompra = false;
    protected boolean usaDiasUteisCancAutomatico = false;
    protected boolean exibeNivelSeguranca = false;
    protected boolean exibeDiaRepasse = true;
    protected boolean existeCalendarioFolhaCse = false;

    /**
     * Carrega a configuração atual do sistema.
     */
    protected void carregaConfiguracaoSistema() {
        responsavel = JspHelper.getAcessoSistema((HttpServletRequest) pageContext.getRequest());

        // Obtém a data e hora do sistema
        dataHoraSistema = DateHelper.toDateTimeString(DateHelper.getSystemDatetime());

        // Obtém o período atual de lançamento
        java.util.Date periodoAtualDate = null;
        try {
            periodoAtualDate = PeriodoHelper.getInstance().getPeriodoAtual(null, responsavel);
            periodoAtual = DateHelper.toPeriodString(periodoAtualDate);
        } catch (PeriodoException e) {
            LOG.error(e.getMessage(), e);
        }

        // Obtém o dia de corte.
        try {
            diaCorte = PeriodoHelper.getInstance().getProximoDiaCorte(null, responsavel);
        } catch (PeriodoException e) {
            LOG.error(e.getMessage(), e);
        }

        // Obtém o dia de repasse.
        try {
            diaRepasse = RepasseHelper.getDiaRepasse(null, periodoAtualDate, responsavel);
        } catch (ViewHelperException e) {
            LOG.error(e.getMessage(), e);
        }

        // Verifica se algum orgão tem corte e repasse diferente do cadastrado no sistema.
        periodoOrgaos = new HashMap<>();
        diaCorteOrgaos = new HashMap<>();
        diaRepasseOrgaos = new HashMap<>();
        try {
            // Verifica se existe calendário folha a nível de CSE
            existeCalendarioFolhaCse = calendarioController.existeCalendarioFolhaCse(responsavel);

            // Busca os órgãos e as configurações de período por órgão
            List<TransferObject> orgaos = consignanteController.lstOrgaos(null, responsavel);
            for (TransferObject orgao : orgaos) {
                String orgCodigo = orgao.getAttribute(Columns.ORG_CODIGO).toString();
                String orgNome = orgao.getAttribute(Columns.ORG_NOME).toString();
                try {
                    java.util.Date orgPeriodoDate = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
                    String orgPeriodo = DateHelper.toPeriodString(orgPeriodoDate);
                    if (!orgPeriodo.equals(periodoAtual) || !existeCalendarioFolhaCse) {
                        periodoOrgaos.put(orgNome, orgPeriodo);
                    }
                    int orgDiaCorte = PeriodoHelper.getInstance().getProximoDiaCorte(orgCodigo, responsavel);
                    if (orgDiaCorte != diaCorte || !existeCalendarioFolhaCse) {
                        diaCorteOrgaos.put(orgNome, DateHelper.format(PeriodoHelper.getInstance().getDataFimPeriodoAtual(orgCodigo, responsavel), LocaleHelper.getDatePattern()));
                    }
                    int orgDiaRepasse = RepasseHelper.getDiaRepasse(orgCodigo, orgPeriodoDate, responsavel);
                    if (orgDiaRepasse != diaRepasse || !existeCalendarioFolhaCse) {
                        diaRepasseOrgaos.put(orgNome, orgDiaRepasse);
                    }
                } catch (PeriodoException|ViewHelperException e) {
                    LOG.error(e.getMessage(), e);
                }
            }

            // Se não existe calendário folha de CSE, então grava NULL em periodoAtual, diaCorte e diaRepasse
            // para que não sejam exibidos como a configuração do sistema, deixando apenas a de órgão
            if (!existeCalendarioFolhaCse) {
                periodoAtual = null;
                diaCorte = null;
                diaRepasse = null;
            }
        } catch (ZetraException e) {
            LOG.error(e.getMessage(), e);
        }

        // Recupera a configuração que informa se o sistema opera com taxas de juros.
        cadastraTaxas = ParamSist.paramEquals(CodedValues.TPC_PER_CAD_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);

        // Verifica se o custo efetivo total está habilitado.
        temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

        // Verifica se o sistema tem módulo de simulação.
        temSimulacao = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel);

        // Verifica se o sistema tem módulo de compra.
        temModuloCompra = ParamSist.paramEquals(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, CodedValues.TPC_SIM, responsavel);

        // Verifica se o sistema tem módulo avançado de compras habilitado.
        temModuloAvancadoCompras = temModuloCompra && ParamSist.paramEquals(CodedValues.TPC_CONTROLE_DETALHADO_PROCESSO_COMPRA, CodedValues.TPC_SIM, responsavel);

        // Verifica se o sistema utiliza dias úteis no módulo avançado de compras.
        usaDiasUteisControleCompra = temModuloAvancadoCompras && ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_CONTROLE_COMPRA, CodedValues.TPC_SIM, responsavel);

        // Verifica se o sistema utiliza dias úteis no cancelamento automático
        usaDiasUteisCancAutomatico = ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_CANC_AUTOMATICO_ADE, CodedValues.TPC_SIM, responsavel);

        // Parâmetro de sistema para informar se exibe o nível de segurança na página inicial
        exibeNivelSeguranca = ParamSist.paramEquals(CodedValues.TPC_EXIBE_NIVEL_SEGURANCA_PAGINA_INICIAL, CodedValues.TPC_SIM, responsavel);

        // Parâmetro de sistema para informar se exibe o nível de segurança na página inicial
        exibeDiaRepasse = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_BRASILEIRO, responsavel);

        String csaCodigo = null;
        if (responsavel.isCsa()) {
            csaCodigo = responsavel.getCodigoEntidade();
        } else if (responsavel.isCor()) {
            csaCodigo = responsavel.getCodigoEntidadePai();
        }

        try {
            if(!TextHelper.isNull(csaCodigo)) {
                String strDiaCorteCsa = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_DIA_CORTE, responsavel);
                diaCorteCsa = !TextHelper.isNull(strDiaCorteCsa) ? Integer.parseInt(strDiaCorteCsa) : null;
                diaCorteCsa = diaCorteCsa != null && diaCorteCsa < diaCorte ? diaCorteCsa : null;
            }
        } catch (ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        // Carrega configurações dos módulos do sistema.
        carregaConfiguracaoModulosSistema();

        // Carrega configurações de taxas de juros de serviços.
        try {
            carregaConfiguracaoTaxasServicos();
        } catch (ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        // Carrega configurações de cancelamento automático.
        try {
            carregaConfiguracaoCancelamentoAutomatico();
        } catch (ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        // Carrega configurações do módulo avançado de compras.
        try {
            carregaConfiguracaoModuloAvancadoCompras();
        } catch (ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        // Carrega configurações de parâmetros de compra de contrato
        try {
            carregaConfiguracaoCompraContrato();
        } catch (ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        // Carrega configurações de parâmetros de renegociacao de contrato
        try {
            carregaConfiguracaoRenegociacaoContrato();
        } catch (ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        // Obtém o nível de segurança
        try {
            if (exibeNivelSeguranca && responsavel.isCseSup()) {
                nivelSeguranca = segurancaController.obtemNivelSeguranca(responsavel);
            }
        } catch (SegurancaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Carrega as configurações de módulos do sistema.
     */
    private void carregaConfiguracaoModulosSistema() {
        configuracaoModulosSistema = new ArrayList<>();

        if (ParamSist.getBoolParamSist(CodedValues.TPC_SIMULACAO_CONSIGNACAO, responsavel)) {
            ConfiguracaoModulosSistema configuracao = new ConfiguracaoModulosSistema(ApplicationResourcesHelper.getMessage("rotulo.sistema.modulo.simulador", responsavel));
            configuracaoModulosSistema.add(configuracao);
        }

        if (ParamSist.getBoolParamSist(CodedValues.TPC_POSSUI_PORTAL_SERVIDOR, responsavel)) {
            ConfiguracaoModulosSistema configuracao = new ConfiguracaoModulosSistema(ApplicationResourcesHelper.getMessage("rotulo.sistema.modulo.portal.servidor", responsavel));
            configuracaoModulosSistema.add(configuracao);
        }

        if (ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, responsavel)) {
            ConfiguracaoModulosSistema configuracao = new ConfiguracaoModulosSistema(ApplicationResourcesHelper.getMessage("rotulo.sistema.modulo.portabilidade.margem.consignavel", responsavel));
            configuracaoModulosSistema.add(configuracao);
        }

        if (ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, responsavel)) {
            ConfiguracaoModulosSistema configuracao = new ConfiguracaoModulosSistema(ApplicationResourcesHelper.getMessage("rotulo.sistema.modulo.leilao.reverso", responsavel));
            configuracaoModulosSistema.add(configuracao);
        }

        if (responsavel.temPermissao(CodedValues.FUN_CRIAR_COMUNICACAO)) {
            ConfiguracaoModulosSistema configuracao = new ConfiguracaoModulosSistema(ApplicationResourcesHelper.getMessage("rotulo.sistema.modulo.comunicacao", responsavel));
            configuracaoModulosSistema.add(configuracao);
        }
    }

    /**
     * Carrega as configurações de taxas de juros dos serviços.
     * @throws ViewHelperException
     */
    private void carregaConfiguracaoTaxasServicos() throws ViewHelperException {
        if (!cadastraTaxas) {
            // Se não há cadastro de taxas, limpas as configurações carregadas anteriormente e retorna.
            taxasServicos = null;
            return;
        }

        try {
            String csaCodigo = null;
            String orgCodigo = null;
            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCodigoEntidade();
            } else if (responsavel.isCor()) {
                csaCodigo = responsavel.getCodigoEntidadePai();
            } else if (responsavel.isOrg()) {
                orgCodigo = responsavel.getCodigoEntidade();
            }

            // Recupera os serviços que validam as taxas de juros
            List<TransferObject> servicos = servicoController.selectServicosComParametro(CodedValues.TPS_VALIDAR_TAXA_JUROS, orgCodigo, csaCodigo, "1", false, CodedValues.NSE_EMPRESTIMO, responsavel);

            taxasServicos = new ArrayList<>();

            if (servicos != null) {
                String dataLimite = null;
                String dataAbertura = null;
                String tipoDataAbertura = null;
                List<TransferObject> limites = null;

                // Para cada serviço.
                for (TransferObject servico : servicos) {
                    // Recupera os parâmetros do serviço referentes a taxa de juros.
                    ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO((String) servico.getAttribute(Columns.SVC_CODIGO), responsavel);

                    dataLimite = paramSvcCse.getTpsDataLimiteDigitTaxa();
                    dataAbertura = paramSvcCse.getTpsDataAberturaTaxa();
                    tipoDataAbertura = paramSvcCse.getTpsDataAberturaTaxaRef();

                    // Armazena o conjunto de configurações de parâmetros para o serviço.
                    ConfiguracaoTaxa confTaxa = new ConfiguracaoTaxa((String) servico.getAttribute(Columns.SVC_DESCRICAO), dataLimite, dataAbertura, tipoDataAbertura);

                    // Recupera os limites de taxa de juros do serviço
                    CustomTransferObject criterio = new CustomTransferObject();
                    criterio.setAttribute(Columns.LTJ_SVC_CODIGO, servico.getAttribute(Columns.SVC_CODIGO));

                    int total = limiteTaxaJurosController.countLimiteTaxaJuros(criterio, responsavel);
                    limites = limiteTaxaJurosController.listaLimiteTaxaJuros(criterio, 0, total, responsavel);

                    if (limites != null && limites.size() > 0) {
                        for (TransferObject limite : limites) {
                            confTaxa.incluirLimiteTaxa(limite.getAttribute(Columns.LTJ_JUROS_MAX).toString(), limite.getAttribute(Columns.LTJ_PRAZO_REF).toString());
                        }
                    }

                    taxasServicos.add(confTaxa);
                }
            }
        } catch (ServicoControllerException | ParametroControllerException | LimiteTaxaJurosControllerException e) {
            throw new ViewHelperException(e);
        }
    }

    /**
     * Carrega as configurações de cancelamento automático dos serviços
     * @throws ViewHelperException
     */
    private void carregaConfiguracaoCancelamentoAutomatico() throws ViewHelperException {
        try {
            String csaCodigo = null;
            String orgCodigo = null;
            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCodigoEntidade();
            } else if (responsavel.isCor()) {
                csaCodigo = responsavel.getCodigoEntidadePai();
            } else if (responsavel.isOrg()) {
                orgCodigo = responsavel.getCodigoEntidade();
            }

            // Recupera os serviços de empréstimo que possuem parâmetro de cancelamento automático.
            List<TransferObject> servicosCancelamentoAutomatico = servicoController.selectServicosCancelamentoAutomatico(orgCodigo, csaCodigo, CodedValues.NSE_EMPRESTIMO);

            configuracaoServicosCancelamentoAutomatico = new ArrayList<>();

            if (servicosCancelamentoAutomatico != null && servicosCancelamentoAutomatico.size() > 0) {
                for (TransferObject servico : servicosCancelamentoAutomatico) {
                    String nomeServico = (String) servico.getAttribute(Columns.SVC_DESCRICAO);
                    String prazoConfirmacaoSolicitacao = (String) servico.getAttribute("VLR_PRAZO_CONFIRMACAO_SOLICITACAO");
                    String prazoConfirmacaoReserva = (String) servico.getAttribute("VLR_PRAZO_CONFIRMACAO_RESERVA");
                    String prazoConfirmacaoCompra = (String) servico.getAttribute("VLR_PRAZO_CONFIRMACAO_COMPRA");

                    ConfiguracaoServicoCancelamentoAutomatico configuracao = new ConfiguracaoServicoCancelamentoAutomatico(nomeServico, prazoConfirmacaoSolicitacao, prazoConfirmacaoReserva, prazoConfirmacaoCompra);

                    configuracaoServicosCancelamentoAutomatico.add(configuracao);
                }
            }
        } catch (ServicoControllerException e) {
            throw new ViewHelperException(e);
        }
    }

    /**
     * Carrega as configurações do módulo de compras para os serviços
     * @throws ViewHelperException
     */
    private void carregaConfiguracaoModuloAvancadoCompras() throws ViewHelperException {
        // Se não há módulo avançado, limpa as configurações anteriormente carregadas e retorna.
        if (!temModuloAvancadoCompras) {
            configuracaoServicosModuloAvancadoCompra = null;
            return;
        }

        try {
            String csaCodigo = null;
            String orgCodigo = null;
            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCodigoEntidade();
            } else if (responsavel.isCor()) {
                csaCodigo = responsavel.getCodigoEntidadePai();
            } else if (responsavel.isOrg()) {
                orgCodigo = responsavel.getCodigoEntidade();
            }

            // Recupera os serviços que validam as taxas de juros
            List<TransferObject> servicosModuloCompra = servicoController.selectServicosModuloAvancadoCompras(orgCodigo, csaCodigo, CodedValues.NSE_EMPRESTIMO);

            configuracaoServicosModuloAvancadoCompra = new ArrayList<>();

            // Se há serviços configurados para o módulo avançado de compra
            if (servicosModuloCompra != null && servicosModuloCompra.size() > 0) {
                for (TransferObject servico : servicosModuloCompra) {
                    String nomeServico = (String) servico.getAttribute(Columns.SVC_DESCRICAO);
                    String prazoInformarSaldo = (String) servico.getAttribute("VLR_PRAZO_INFORMAR_SALDO");
                    String prazoEfetuarPagamento = (String) servico.getAttribute("VLR_PRAZO_EFETUAR_PAGAMENTO");
                    String prazoLiquidarContrato = (String) servico.getAttribute("VLR_PRAZO_LIQUIDAR_CONTRATO");

                    ConfiguracaoServicoModuloAvancadoCompra configuracao = new ConfiguracaoServicoModuloAvancadoCompra(nomeServico, prazoInformarSaldo, prazoEfetuarPagamento, prazoLiquidarContrato);

                    configuracaoServicosModuloAvancadoCompra.add(configuracao);
                }
            }
        } catch (ServicoControllerException e) {
            throw new ViewHelperException(e);
        }
    }

    /**
     * Carrega as configurações de parâmetros de compra de contrato
     * @throws ViewHelperException
     */
    private void carregaConfiguracaoCompraContrato() throws ViewHelperException {
        // Se não há módulo de compra, limpa as configurações anteriormente carregadas e retorna.
        if (!temModuloCompra) {
            configuracaoServicosCompraContrato = null;
            return;
        }

        try {
            String csaCodigo = null;
            String orgCodigo = null;
            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCodigoEntidade();
            } else if (responsavel.isCor()) {
                csaCodigo = responsavel.getCodigoEntidadePai();
            } else if (responsavel.isOrg()) {
                orgCodigo = responsavel.getCodigoEntidade();
            }

            // Recupera os serviços e seus parâmetros
            List<TransferObject> servicosParametroCompra = servicoController.selectServicosParametroCompra(orgCodigo, csaCodigo, CodedValues.NSE_EMPRESTIMO);

            configuracaoServicosCompraContrato = new ArrayList<>();

            // Se há serviços configurados de parametros para compra de contratos
            if (servicosParametroCompra != null && servicosParametroCompra.size() > 0) {

                for (TransferObject servico : servicosParametroCompra) {

                    String nomeServico = (String) servico.getAttribute(Columns.SVC_DESCRICAO);
                    String quantidadeMinParcelaPaga = (String) servico.getAttribute("VLR_QUANTIDADE_MIN_PARCELA_PAGA");
                    String percentualMinParcelaPaga = (String) servico.getAttribute("VLR_PERCENTUAL_MIN_PARCELA_PAGA");
                    String quantidadeMinVigencia = (String) servico.getAttribute("VLR_QUANTIDADE_MIN_VIGENCIA");
                    String percentualMinVigencia = (String) servico.getAttribute("VLR_PERCENTUAL_MIN_VIGENCIA");

                    ConfiguracaoServicoCompraContrato configuracao = new ConfiguracaoServicoCompraContrato(nomeServico, quantidadeMinParcelaPaga, percentualMinParcelaPaga, quantidadeMinVigencia, percentualMinVigencia);

                    configuracaoServicosCompraContrato.add(configuracao);

                }
            }
        } catch (ServicoControllerException e) {
            throw new ViewHelperException(e);
        }
    }

    /**
     * Carrega as configurações de parâmetros de renegociacao de contrato
     * @throws ViewHelperException
     */
    private void carregaConfiguracaoRenegociacaoContrato() throws ViewHelperException {
        try {
            String csaCodigo = null;
            String orgCodigo = null;
            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCodigoEntidade();
            } else if (responsavel.isCor()) {
                csaCodigo = responsavel.getCodigoEntidadePai();
            } else if (responsavel.isOrg()) {
                orgCodigo = responsavel.getCodigoEntidade();
            }

            // Recupera os serviços e seus parâmetros de renegociacao
            List<TransferObject> servicosParametroRenegociacao = servicoController.selectServicosParametroRenegociacao(orgCodigo, csaCodigo, CodedValues.NSE_EMPRESTIMO);

            configuracaoServicosRenegociacaoContrato = new ArrayList<>();

            // Se há serviços configurados de parametros para renegociacao de contratos
            if (servicosParametroRenegociacao != null && servicosParametroRenegociacao.size() > 0) {

                for (TransferObject servico : servicosParametroRenegociacao) {

                    String nomeServico = (String) servico.getAttribute(Columns.SVC_DESCRICAO);
                    String quantidadeMinParcelaPaga = (String) servico.getAttribute("VLR_QUANTIDADE_MIN_PARCELA_PAGA");
                    String percentualMinParcelaPaga = (String) servico.getAttribute("VLR_PERCENTUAL_MIN_PARCELA_PAGA");
                    String quantidadeMinVigencia = (String) servico.getAttribute("VLR_QUANTIDADE_MIN_VIGENCIA");
                    String percentualMinVigencia = (String) servico.getAttribute("VLR_PERCENTUAL_MIN_VIGENCIA");

                    ConfiguracaoServicoRenegociacaoContrato configuracao = new ConfiguracaoServicoRenegociacaoContrato(nomeServico, quantidadeMinParcelaPaga, percentualMinParcelaPaga, quantidadeMinVigencia, percentualMinVigencia);

                    configuracaoServicosRenegociacaoContrato.add(configuracao);

                }
            }
        } catch (ServicoControllerException e) {
            throw new ViewHelperException(e);
        }
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().print(generateHtml());
        } catch (IOException | ViewHelperException e) {
            throw new JspException(e.getMessage());
        }
        // Continue processing this page
        return (EVAL_PAGE);
    }

    public abstract String generateHtml() throws ViewHelperException;

    /**
     * Em sistemas quinzenais, imprime o formato do dia completo para o dia de corte.
     * Para mensais, apenas o dia
     * @param dia - objeto representando o dia de corte (se nulo, usa o dia padrão de corte)
     * @return
     * @throws ViewHelperException
     */
    protected String imprimeDiaCorte(Object dia) throws ViewHelperException {
        try {
            String dataAExibir = (dia != null) ? dia.toString() : DateHelper.format(PeriodoHelper.getInstance().getDataFimPeriodoAtual((responsavel.isOrg()) ?
                    responsavel.getCodigoEntidade() : null, responsavel), LocaleHelper.getDatePattern());
            return TextHelper.forHtmlContent(dataAExibir);
        } catch (PeriodoException e) {
            throw new ViewHelperException(e);
        }
    }

    /**
     * Armazena configurações formatadas de taxas de juros para um serviço.
     * @author marcelo
     */
    public static class ConfiguracaoTaxa {
        private String nomeServico = null;
		private String dataLimiteLancamento = null;
		private String dataAbertura = null;
		private String tipoDataAbertura = null;
		private List<String> limiteTaxa = null;

        public String getNomeServico() {
			return nomeServico;
		}

		public String getDataLimiteLancamento() {
			return dataLimiteLancamento;
		}

		public String getDataAbertura() {
			return dataAbertura;
		}

		public String getTipoDataAbertura() {
			return tipoDataAbertura;
		}

		public List<String> getLimiteTaxa() {
			return limiteTaxa;
		}

		public ConfiguracaoTaxa(String nomeServico, String dataLimiteLancamento, String dataAbertura, String tipoDataAbertura) {
            this.nomeServico = nomeServico;
            this.dataLimiteLancamento = dataLimiteLancamento;
            this.dataAbertura = dataAbertura;
            this.tipoDataAbertura = tipoDataAbertura;
        }

        public String getConfiguracoesDatas() {
            String textoData = "";

            if (!TextHelper.isNull(dataLimiteLancamento)) {
                textoData += ApplicationResourcesHelper.getMessage("rotulo.sistema.cadastro.ate.dia.arg0", (AcessoSistema) null, dataLimiteLancamento);
            }

            if (!TextHelper.isNull(dataAbertura)) {
                if (!TextHelper.isNull(dataLimiteLancamento)) {
                    textoData += ". ";
                }

                textoData += ApplicationResourcesHelper.getMessage("rotulo.sistema.abre.arg0", (AcessoSistema) null, dataAbertura);
                if (!TextHelper.isNull(tipoDataAbertura)) {
                    if (tipoDataAbertura.equals("D")) {
                        textoData += " " + ApplicationResourcesHelper.getMessage("rotulo.param.svc.data.abertura.dia.mes", (AcessoSistema) null);
                    } else if (tipoDataAbertura.equals("S")) {
                        textoData += " " + ApplicationResourcesHelper.getMessage("rotulo.param.svc.data.abertura.dia.semana", (AcessoSistema) null);
                    } else if (tipoDataAbertura.equals("U")) {
                        textoData += " " + ApplicationResourcesHelper.getMessage("rotulo.param.svc.data.abertura.dia.util", (AcessoSistema) null);
                    }
                }
            }

            return (!textoData.equals("") ? textoData : null);
        }

        public void incluirLimiteTaxa(String valorMaxTaxa, String limiteMeses) {
            if (!TextHelper.isNull(valorMaxTaxa) || !TextHelper.isNull(limiteMeses)) {
                String textoLimiteTaxa;
                if (limiteTaxa == null) {
                    limiteTaxa = new ArrayList<>();
                }

                // Formata o valor da taxa
                String valorMaxTaxaFormatado = null;
                if (!TextHelper.isNull(valorMaxTaxa) && !TextHelper.isNotNumeric(valorMaxTaxa)) {
                    try {
                        valorMaxTaxaFormatado = NumberHelper.reformat(valorMaxTaxa, "en", NumberHelper.getLang());
                    } catch (ParseException ex) {
                        LOG.error("Valor da taxa não é numérico.", ex);
                        valorMaxTaxaFormatado = "0";
                    }
                } else {
                    valorMaxTaxaFormatado = "0";
                }

                textoLimiteTaxa = ApplicationResourcesHelper.getMessage("rotulo.sistema.limite.taxa.arg0.arg1", (AcessoSistema) null,(TextHelper.isNull(valorMaxTaxa) ? "0" : valorMaxTaxaFormatado), (TextHelper.isNull(limiteMeses) ? "0" : limiteMeses));

                limiteTaxa.add(textoLimiteTaxa);
            }
        }
    }

    /**
     * Armazena configurações formatadas para informação dos módulos do sistema disponíveis ao usuário
     * @author lucas
     */
    public static class ConfiguracaoModulosSistema {
        private String nomeModulo = null;

        public String getNomeModulo() {
            return nomeModulo;
        }

        public ConfiguracaoModulosSistema(String nomeModulo) {
            this.nomeModulo = nomeModulo;
        }
    }

    /**
     * Armazena configurações formatadas para os parâmetros de cancelamento automático de um serviço
     * @author marcelo
     */
    public static class ConfiguracaoServicoCancelamentoAutomatico {
    	private String nomeServico = null;
    	private String prazoConfirmacaoSolicitacoes = null;
    	private String prazoConfirmacaoReservas = null;
    	private String prazoConfirmacaoCompras = null;

        public String getNomeServico() {
			return nomeServico;
		}

		public String getPrazoConfirmacaoSolicitacoes() {
			return prazoConfirmacaoSolicitacoes;
		}

		public String getPrazoConfirmacaoReservas() {
			return prazoConfirmacaoReservas;
		}

		public String getPrazoConfirmacaoCompras() {
			return prazoConfirmacaoCompras;
		}

		public ConfiguracaoServicoCancelamentoAutomatico(String nomeServico, String prazoConfirmacaoSolicitacoes, String prazoConfirmacaoReservas, String prazoConfirmacaoCompras) {
            this.nomeServico = nomeServico;
            this.prazoConfirmacaoSolicitacoes = prazoConfirmacaoSolicitacoes;
            this.prazoConfirmacaoReservas = prazoConfirmacaoReservas;
            this.prazoConfirmacaoCompras = prazoConfirmacaoCompras;
        }
    }

    /**
     * Armazena configurações formatadas do módulo avançado de compra para um serviço.
     * @author marcelo
     */
    public static class ConfiguracaoServicoModuloAvancadoCompra {
    	private String nomeServico = null;
		private String prazoInformarSaldo = null;
    	private String prazoEfetuarPagamento = null;
    	private String prazoLiquidarContrato = null;

    	public String getNomeServico() {
			return nomeServico;
		}

		public String getPrazoInformarSaldo() {
			return prazoInformarSaldo;
		}

		public String getPrazoEfetuarPagamento() {
			return prazoEfetuarPagamento;
		}

		public String getPrazoLiquidarContrato() {
			return prazoLiquidarContrato;
		}

        public ConfiguracaoServicoModuloAvancadoCompra(String nomeServico, String prazoInformarSaldo, String prazoEfetuarPagamento, String prazoLiquidarContrato) {
            this.nomeServico = nomeServico;
            this.prazoInformarSaldo = prazoInformarSaldo;
            this.prazoEfetuarPagamento = prazoEfetuarPagamento;
            this.prazoLiquidarContrato = prazoLiquidarContrato;
        }
    }

    /**
     * Armazena configurações formatadas para parâmetros de compras de contrato
     * @author rodrigo viana
     */
    public static class ConfiguracaoServicoCompraContrato {
    	private String nomeServico = null;
    	private String quantidadeMinParcelaPaga = null;
    	private String percentualMinParcelaPaga = null;
    	private String quantidadeMinVigencia = null;
    	private String percentualMinVigencia = null;

        public String getNomeServico() {
			return nomeServico;
		}

		public String getQuantidadeMinParcelaPaga() {
			return quantidadeMinParcelaPaga;
		}

		public String getPercentualMinParcelaPaga() {
			return percentualMinParcelaPaga;
		}

		public String getQuantidadeMinVigencia() {
			return quantidadeMinVigencia;
		}

		public String getPercentualMinVigencia() {
			return percentualMinVigencia;
		}

		public ConfiguracaoServicoCompraContrato(String nomeServico, String quantidadeMinParcelaPaga, String percentualMinParcelaPaga, String quantidadeMinVigencia, String percentualMinVigencia) {
            this.nomeServico = nomeServico;
            this.quantidadeMinParcelaPaga = quantidadeMinParcelaPaga;
            this.quantidadeMinVigencia = quantidadeMinVigencia;
            if (TextHelper.isNull(percentualMinParcelaPaga) || TextHelper.isNotNumeric(percentualMinParcelaPaga)) {
                this.percentualMinParcelaPaga = "";
            } else {
                try {
                    this.percentualMinParcelaPaga = NumberHelper.reformat(percentualMinParcelaPaga, "en", NumberHelper.getLang());
                } catch (ParseException ex) {
                    LOG.error("Percentual mínimo de parcela paga não é numérico.", ex);
                    this.percentualMinParcelaPaga = "";
                }
            }
            if (TextHelper.isNull(percentualMinVigencia) || TextHelper.isNotNumeric(percentualMinVigencia)) {
                this.percentualMinVigencia = "";
            } else {
                try {
                    this.percentualMinVigencia = NumberHelper.reformat(percentualMinVigencia, "en", NumberHelper.getLang());
                } catch (ParseException ex) {
                    LOG.error("Percentual mínimo de vigência não é numérico.", ex);
                    this.percentualMinVigencia = "";
                }
            }
        }
    }

    /**
     * Armazena configurações formatadas para parâmetros de renegociação de contrato
     * @author rodrigo viana
     */
    public static class ConfiguracaoServicoRenegociacaoContrato {
    	private String nomeServico = null;
		private String quantidadeMinParcelaPaga = null;
    	private String percentualMinParcelaPaga = null;
    	private String quantidadeMinVigencia = null;
    	private String percentualMinVigencia = null;

    	public String getNomeServico() {
			return nomeServico;
		}

        public String getQuantidadeMinParcelaPaga() {
			return quantidadeMinParcelaPaga;
		}

		public String getPercentualMinParcelaPaga() {
			return percentualMinParcelaPaga;
		}

		public String getQuantidadeMinVigencia() {
			return quantidadeMinVigencia;
		}

		public String getPercentualMinVigencia() {
			return percentualMinVigencia;
		}

		public ConfiguracaoServicoRenegociacaoContrato(String nomeServico, String quantidadeMinParcelaPaga, String percentualMinParcelaPaga, String quantidadeMinVigencia, String percentualMinVigencia) {
            this.nomeServico = nomeServico;
            this.quantidadeMinParcelaPaga = quantidadeMinParcelaPaga;
            this.quantidadeMinVigencia = quantidadeMinVigencia;
            if (TextHelper.isNull(percentualMinParcelaPaga) || TextHelper.isNotNumeric(percentualMinParcelaPaga)) {
                this.percentualMinParcelaPaga = "";
            } else {
                try {
                    this.percentualMinParcelaPaga = NumberHelper.reformat(percentualMinParcelaPaga, "en", NumberHelper.getLang());
                } catch (ParseException ex) {
                    LOG.error("Percentual mínimo de parcela paga não é numérico.", ex);
                    this.percentualMinParcelaPaga = "";
                }
            }
            if (TextHelper.isNull(percentualMinVigencia) || TextHelper.isNotNumeric(percentualMinVigencia)) {
                this.percentualMinVigencia = "";
            } else {
                try {
                    this.percentualMinVigencia = NumberHelper.reformat(percentualMinVigencia, "en", NumberHelper.getLang());
                } catch (ParseException ex) {
                    LOG.error("Percentual mínimo de vigência não é numérico.", ex);
                    this.percentualMinVigencia = "";
                }
            }
        }
    }
}