package com.zetra.econsig.web.tag;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.SaldoDevedorTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CompraContratoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ParcelaControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.SaldoDevedorControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.consignacao.SimulacaoHelper;
import com.zetra.econsig.helper.financeiro.CDCHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodo;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacao;
import com.zetra.econsig.service.compra.CompraContratoController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.parcela.ParcelaController;
import com.zetra.econsig.service.saldodevedor.SaldoDevedorController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;

import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>Title: DetalheConsignacaoTag</p>
 * <p>Description: Tag para exibição dos detalhes de uma consignação.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class DetalheConsignacaoTag extends DetalheServidorTag {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DetalheConsignacaoTag.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private SaldoDevedorController saldoDevedorController;

    @Autowired
    private ParcelaController parcelaController;

    @Autowired
    private CompraContratoController compraContratoController;

    // Indica se <table></table> deve ser impresso
    private boolean table;

    // Nome do atributo que contém os dados dao consignação
    protected String name;

    // Escopo do atributo que contém os dados da consignação
    protected String scope;

    // Tipo de operação (consultar/alterar/...)
    protected String type;

    // Indica que a consignação está arquivada
    private boolean arquivado;

    protected boolean multiplosServidores;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTable(boolean table) {
        this.table = table;
    }

    public void setArquivado(boolean arquivado) {
        this.arquivado = arquivado;
    }

    protected StringBuilder geraDetalheConsignacao(Object autdesObject) throws ParseException, ParametroControllerException, AutorizacaoControllerException, ViewHelperException, SaldoDevedorControllerException, InstantiationException, IllegalAccessException, ServidorControllerException, ParcelaControllerException, PeriodoException, CompraContratoControllerException {
        int autCount = 1;
        int numContrato = 0;
        List<CustomTransferObject> autdesList = new ArrayList<>();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        boolean temValidacaoDataNasc = parametroController.hasValidacaoDataNasc(responsavel);

        // Inicia geração do código HTML
        StringBuilder code = new StringBuilder();

        // Tratamentos específicos por tipo
        boolean consultarConsignacao = !TextHelper.isNull(type) && type.equals("consultar");
        boolean editarPropostaFinanCartao = !TextHelper.isNull(type) && type.equals("edt_proposta");
        boolean editarPropostaLeilao = !TextHelper.isNull(type) && type.equals("leilao");
        boolean simularRenegociacao = !TextHelper.isNull(type) && type.equals("simular_renegociacao");
        boolean renegociarConsignacao = !TextHelper.isNull(type) && type.equals("renegociar_consignacao");
        boolean exibePrice = !TextHelper.isNull(type) && type.equals("price");
        // Na página renegociarConsignacao.jsp é usado desta forma:
        // <hl:detalharADE name="autdes" table="false" type="alterar" />
        // Como alterar não é um type tratado, segue o fluxo padrão, achei adequado usar o nome alterarSemAnexo
        // pois apenas desabilito a exibição do anexo no fluxo padrão.
        boolean alterarSemAnexo = !TextHelper.isNull(type) && type.equals("alterar_sem_anexo");

        // Obtém o DTO com os dados da consignação
        TransferObject autdesObjctOrigem = null;
        Object autdesObjct = pageContext.getAttribute(name, getScopeAsInt(scope));
        if (autdesObjct instanceof List) {
            autdesList = (List<CustomTransferObject>) autdesObjct;
        } else {
            autdesList.add((CustomTransferObject) autdesObjct);
            autdesObjctOrigem = ((TransferObject) pageContext.getAttribute("autdesOrigem")) != null ? (TransferObject) pageContext.getAttribute("autdesOrigem") : null;
        }

        // Recupera o 1o contrato da lista apenas para recuperar os valores comuns a todos os contratos
        multiplosServidores = false;
        CustomTransferObject firstAde = autdesList.get(0);
        for (CustomTransferObject nextAde : autdesList) {
            if (!firstAde.getAttribute(Columns.RSE_CODIGO).equals(nextAde.getAttribute(Columns.RSE_CODIGO))) {
                multiplosServidores = true;
                break;
            }
        }

        if (autdesList.size() > 1 && !simularRenegociacao) {
            //Monta table com dados comuns do servidor
            if (table) {
                // Se tem tabela muda o CSS para TLEDtopo e CEDtopo
                code.append(abrirTabela());
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel), TextHelper.forHtmlContent(LoginHelper.getCseNome(responsavel)), "TLEDtopo", "CEDtopo", FieldKeysConstants.DETALHE_CONSIGNACAO_CONSIGNANTE));
            } else {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel), TextHelper.forHtmlContent(LoginHelper.getCseNome(responsavel)), FieldKeysConstants.DETALHE_CONSIGNACAO_CONSIGNANTE));
            }
        }

        // Exibe os dados do servidor no topo para o caso de detalhe de mais de uma consignação
        if (!multiplosServidores && autdesList.size() > 1 && !simularRenegociacao) {
            code.append(geraDetalheServidor(firstAde, temValidacaoDataNasc, false, null, responsavel));
        }

        if (table && !simularRenegociacao) {
            if (autdesList.size() > 1) {
                code.append(fecharTabela());
            }
        }

        /**
         * Parâmetros de sistema necessários.
         */
        boolean permiteCadIndice = ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_CAD_INDICE, responsavel) != null && ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_CAD_INDICE, responsavel).toString().equals(CodedValues.TPC_SIM);
        boolean temSimulacaoConsignacao = ParamSist.getInstance().getParam(CodedValues.TPC_SIMULACAO_CONSIGNACAO, responsavel) != null && ParamSist.getInstance().getParam(CodedValues.TPC_SIMULACAO_CONSIGNACAO, responsavel).toString().equals(CodedValues.TPC_SIM);
        // Verifica se o sistema está configurado para trabalhar com taxa de juros.
        boolean utilizaTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);
        // Verifica se o sistema está configurado para trabalhar com o CET.
        boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
        boolean exibeCapitalDevido = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPITAL_DEVIDO, CodedValues.TPC_SIM, responsavel);

        // Verifica se o sistema permite deferimento de contratos pela CSA, e é um usuário de CSA que tenha permissão de deferir ou indeferir e esteja executando esta ação
        boolean deferimentoPelaCsa = (ParamSist.paramEquals(CodedValues.TPC_PERMITE_DEFERIMENTO_TERCEIROS_PELA_CSA, CodedValues.TPC_SIM, responsavel) && responsavel.isCsa() && ((responsavel.temPermissao(CodedValues.FUN_DEF_CONSIGNACAO) && CodedValues.FUN_DEF_CONSIGNACAO.equals(responsavel.getFunCodigo())) || (responsavel.temPermissao(CodedValues.FUN_INDF_CONSIGNACAO) && CodedValues.FUN_INDF_CONSIGNACAO.equals(responsavel.getFunCodigo()))));

        for (CustomTransferObject autdes : autdesList) {
            String adeCodigo = autdes.getAttribute(Columns.ADE_CODIGO).toString();
            String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
            String nseCodigo = autdes.getAttribute(Columns.NSE_CODIGO) != null ? autdes.getAttribute(Columns.NSE_CODIGO).toString() : (String) autdes.getAttribute(Columns.SVC_NSE_CODIGO);
            String csaCodigo = (String) autdes.getAttribute(Columns.CSA_CODIGO);
            BigDecimal adeVlrDecimal = autdes.getAttribute(Columns.ADE_VLR) != null ? new BigDecimal(autdes.getAttribute(Columns.ADE_VLR).toString()) : null;

            ParamSvcTO parSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            boolean permiteCadVlrTac = parSvcCse.isTpsCadValorTac();
            boolean permiteCadVlrIof = parSvcCse.isTpsCadValorIof();
            boolean permiteCadVlrLiqLib = parSvcCse.isTpsCadValorLiquidoLiberado();
            boolean permiteCadVlrMensVinc = parSvcCse.isTpsCadValorMensalidadeVinc();
            boolean boolTpsVlrDevido = parSvcCse.isTpsExibeCapitalDevido();
            boolean boolTpsSegPrestamista = parSvcCse.isTpsExigeSeguroPrestamista();
            boolean permiteVlrLiqTxJuros = parSvcCse.isTpsVlrLiqTaxaJuros();
            boolean boolPossuiCorrecaoVlrPresente = parSvcCse.isTpsPossuiCorrecaoValorPresente();
            boolean boolTpsNumeraContratosServidor = parSvcCse.isTpsNumerarContratosServidor();
            boolean boolExigeCodAutSolicitacao = parSvcCse.isTpsExigeCodAutorizacaoSolic();
            boolean boolExibeInfBancariaServidor = parSvcCse.isTpsExibeInfBancariaServidor();
            boolean boolVisualizaValorLiberadoCalc = parSvcCse.isTpsVisualizaValorLiberadoCalculado();

            List<String> tpsCodigosCsa = new ArrayList<>();
            tpsCodigosCsa.add(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL);

            boolean csaPermitePreservacaoParcela = false;

            List<TransferObject> listParSvcCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigosCsa, false, responsavel);
            for (TransferObject parSvcCsa : listParSvcCsa) {
                csaPermitePreservacaoParcela = parSvcCsa != null && parSvcCsa.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL) && parSvcCsa.getAttribute(Columns.PSC_VLR).toString().equals(CodedValues.PSC_BOOLEANO_SIM);
            }

            // Pega as informações de saldo devedor
            SaldoDevedorTransferObject saldoDevedorTO = null;
            try {
                saldoDevedorTO = saldoDevedorController.getSaldoDevedor(adeCodigo, arquivado, responsavel);
            } catch (SaldoDevedorControllerException ex) {
                // Não imprime mensagem de erro, pois a grande maiorida dos contratos
                // não possuem o cadastro de saldo devedor.
            }

            // Pega Atributos do coeficiente desconto
            BigDecimal vlrLiberado = null;
            Short ranking = null;
            BigDecimal vlrLiberadoCalc = null;
            if (temSimulacaoConsignacao) {
                vlrLiberado = (BigDecimal) autdes.getAttribute(Columns.CDE_VLR_LIBERADO);
                ranking = (Short) autdes.getAttribute(Columns.CDE_RANKING);
                vlrLiberadoCalc = (BigDecimal) autdes.getAttribute(Columns.CDE_VLR_LIBERADO_CALC);
            }

            String rotuloMoeda = ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel);
            String rotuloPrzIndet = ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel);

            // Pega os valores necessários
            String mensagem = TextHelper.forHtmlContent(!TextHelper.isNull(autdes.getAttribute(Columns.PRD_ADE_CODIGO)) ? ApplicationResourcesHelper.getMessage("mensagem.consignacao.possui.prd.processamento", responsavel) : "");
            String servico = TextHelper.forHtmlContent((autdes.getAttribute(Columns.CNV_COD_VERBA) != null ? autdes.getAttribute(Columns.CNV_COD_VERBA) : autdes.getAttribute(Columns.SVC_IDENTIFICADOR)) + " - " + autdes.getAttribute(Columns.SVC_DESCRICAO));
            String vlrSegPrestamista = ((autdes.getAttribute(Columns.ADE_VLR_SEG_PRESTAMISTA) != null) ? NumberHelper.format((new BigDecimal(autdes.getAttribute(Columns.ADE_VLR_SEG_PRESTAMISTA).toString())).doubleValue(), NumberHelper.getLang()) : "");
            String vlrLiquidoLiberado = ((autdes.getAttribute(Columns.ADE_VLR_LIQUIDO) != null) ? NumberHelper.format((new BigDecimal(autdes.getAttribute(Columns.ADE_VLR_LIQUIDO).toString())).doubleValue(), NumberHelper.getLang()) : "");
            String vlrMensalidadeVinc = ((autdes.getAttribute(Columns.ADE_VLR_MENS_VINC) != null) ? NumberHelper.format((new BigDecimal(autdes.getAttribute(Columns.ADE_VLR_MENS_VINC).toString())).doubleValue(), NumberHelper.getLang()) : "");
            String vlrLiqTaxaJuros = ((autdes.getAttribute(Columns.ADE_TAXA_JUROS) != null) ? NumberHelper.format((new BigDecimal(autdes.getAttribute(Columns.ADE_TAXA_JUROS).toString())).doubleValue(), NumberHelper.getLang()) : "");
            String vlrCoeficiente = ((autdes.getAttribute(Columns.CFT_VLR) != null) ? NumberHelper.format((new BigDecimal(autdes.getAttribute(Columns.CFT_VLR).toString())).doubleValue(), NumberHelper.getLang()) : "");
            String adeDataHoraOcorrencia = (autdes.getAttribute(Columns.ADE_DATA_HORA_OCORRENCIA) != null ? DateHelper.reformat(autdes.getAttribute(Columns.ADE_DATA_HORA_OCORRENCIA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDatePattern()) : "");
            String adePrdPagas = TextHelper.forHtmlContent(((autdes.getAttribute(Columns.ADE_PRD_PAGAS) != null) ? autdes.getAttribute(Columns.ADE_PRD_PAGAS).toString() : "0"));
            String adePrdPagasTotal = TextHelper.forHtmlContent(((autdes.getAttribute(Columns.ADE_PRD_PAGAS_TOTAL) != null) ? autdes.getAttribute(Columns.ADE_PRD_PAGAS_TOTAL).toString() : "0"));
            String adeIndice = TextHelper.forHtmlContent(((autdes.getAttribute(Columns.ADE_INDICE) != null) ? autdes.getAttribute(Columns.ADE_INDICE).toString() : ""));
            String adeCodReg = TextHelper.forHtmlContent(((autdes.getAttribute(Columns.ADE_COD_REG) != null && !autdes.getAttribute(Columns.ADE_COD_REG).equals("")) ? autdes.getAttribute(Columns.ADE_COD_REG).toString() : CodedValues.COD_REG_DESCONTO));
            String labelTipoValor = TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr((String) autdes.getAttribute(Columns.ADE_TIPO_VLR)));
            String adePrazo = TextHelper.forHtmlContent((autdes.getAttribute(Columns.ADE_PRAZO) == null) ? rotuloPrzIndet : autdes.getAttribute(Columns.ADE_PRAZO).toString());
            String adePrazoRef = TextHelper.forHtmlContent((autdes.getAttribute(Columns.ADE_PRAZO_REF) == null) ? rotuloPrzIndet : autdes.getAttribute(Columns.ADE_PRAZO_REF).toString());
            String adeCarencia = TextHelper.forHtmlContent(((autdes.getAttribute(Columns.ADE_CARENCIA) != null) ? autdes.getAttribute(Columns.ADE_CARENCIA).toString() : "0"));
            String anoMesIni = TextHelper.forHtmlContent(DateHelper.toPeriodString((java.util.Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI)));
            String anoMesFim = TextHelper.forHtmlContent(DateHelper.toPeriodString((java.util.Date) autdes.getAttribute(Columns.ADE_ANO_MES_FIM)));
            String adeVlr = TextHelper.forHtmlContent(NumberHelper.reformat(autdes.getAttribute(Columns.ADE_VLR).toString(), "en", NumberHelper.getLang()));
            String adeVlrRef = TextHelper.forHtmlContent((autdes.getAttribute(Columns.ADE_VLR_REF) != null) ? NumberHelper.reformat(autdes.getAttribute(Columns.ADE_VLR_REF).toString(), "en", NumberHelper.getLang()) : adeVlr);
            String sadCodigo = TextHelper.forHtmlContent((autdes.getAttribute(Columns.ADE_SAD_CODIGO) != null) ? autdes.getAttribute(Columns.ADE_SAD_CODIGO).toString() : (String) autdes.getAttribute(Columns.SAD_CODIGO));

            String adeTipoTaxa = TextHelper.forHtmlContent(autdes.getAttribute(Columns.ADE_TIPO_TAXA));
            String adeVlrFolha = TextHelper.forHtmlContent((autdes.getAttribute(Columns.ADE_VLR_FOLHA) != null) ? NumberHelper.reformat(autdes.getAttribute(Columns.ADE_VLR_FOLHA).toString(), "en", NumberHelper.getLang()) : "");

            String adeBanco = TextHelper.forHtmlContent(autdes.getAttribute(Columns.ADE_BANCO));
            String adeAgencia = TextHelper.forHtmlContent(autdes.getAttribute(Columns.ADE_AGENCIA));
            String adeConta = TextHelper.forHtmlContent(autdes.getAttribute(Columns.ADE_CONTA));

            //Convenio de origem do leilão
            String adePrazoOrigem = null;
            String adeVlrOrigem = null;
            String adePrdPagasTotalOrigem = null;
            String adeDataOrigem = null;
            String adeAnoMesIniOrigem = null;
            String adeAnoMesFimOrigem = null;

            if(autdesObjctOrigem != null) {
                adePrazoOrigem = TextHelper.forHtmlContent((autdesObjctOrigem.getAttribute(Columns.ADE_PRAZO)) != null ? TextHelper.forHtmlContent(autdesObjctOrigem.getAttribute(Columns.ADE_PRAZO)) : "");
                adeVlrOrigem = TextHelper.forHtmlContent((autdesObjctOrigem.getAttribute(Columns.ADE_VLR)) != null ? NumberHelper.reformat(autdesObjctOrigem.getAttribute(Columns.ADE_VLR).toString(), "en", NumberHelper.getLang()) : "");
                adePrdPagasTotalOrigem = TextHelper.forHtmlContent((autdesObjctOrigem.getAttribute(Columns.ADE_PRD_PAGAS_TOTAL)) != null ? autdesObjctOrigem.getAttribute(Columns.ADE_PRD_PAGAS_TOTAL) : "");
                adeDataOrigem = TextHelper.forHtmlContent((autdesObjctOrigem.getAttribute(Columns.ADE_DATA)) != null ? DateHelper.toDateTimeString((java.util.Date) autdesObjctOrigem.getAttribute(Columns.ADE_DATA)) : "");
                adeAnoMesIniOrigem = TextHelper.forHtmlContent((autdesObjctOrigem.getAttribute(Columns.ADE_ANO_MES_INI)) != null ? DateHelper.toDateString((java.util.Date) autdesObjctOrigem.getAttribute(Columns.ADE_ANO_MES_INI)) : "");
                adeAnoMesFimOrigem = TextHelper.forHtmlContent((autdesObjctOrigem.getAttribute(Columns.ADE_ANO_MES_FIM)) != null ? DateHelper.toDateString((java.util.Date) autdesObjctOrigem.getAttribute(Columns.ADE_ANO_MES_FIM)) : "");
            }

            String anoMesIniRef = TextHelper.forHtmlContent(DateHelper.toPeriodString((autdes.getAttribute(Columns.ADE_ANO_MES_INI_REF) != null ? (java.util.Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI_REF) : (java.util.Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI))));

            String anoMesFimRef = TextHelper.forHtmlContent(DateHelper.toPeriodString((autdes.getAttribute(Columns.ADE_ANO_MES_FIM_REF) != null ? (java.util.Date) autdes.getAttribute(Columns.ADE_ANO_MES_FIM_REF) : (java.util.Date) autdes.getAttribute(Columns.ADE_ANO_MES_FIM))));

            String csaNome = TextHelper.forHtmlContent(autdes.getAttribute(Columns.CSA_NOME_ABREV));
            if (csaNome == null || csaNome.isBlank()) {
                csaNome = TextHelper.forHtmlContent(autdes.getAttribute(Columns.CSA_NOME));
            }
            csaNome = TextHelper.forHtmlContent(autdes.getAttribute(Columns.CSA_IDENTIFICADOR)) + " - " + csaNome;
            if (csaNome.length() > 50) {
                csaNome = csaNome.substring(0, 47) + "...";
            }

            String corNome = TextHelper.forHtmlContent(autdes.getAttribute(Columns.COR_NOME));
            if (corNome != null && !corNome.equals("")) {
                if (autdes.getAttribute(Columns.COR_IDENTIFICADOR_ANTIGO) != null) {
                    corNome = TextHelper.forHtmlContent(autdes.getAttribute(Columns.COR_IDENTIFICADOR_ANTIGO)) + " - " + corNome;
                } else {
                    corNome = TextHelper.forHtmlContent(autdes.getAttribute(Columns.COR_IDENTIFICADOR)) + " - " + corNome;
                }
            } else {
                corNome = "";
            }
            if (corNome.length() > 50) {
                corNome = corNome.substring(0, 47) + "...";
            }

            String loginResponsavel = TextHelper.forHtmlContent(autdes.getAttribute(Columns.USU_LOGIN) != null ? autdes.getAttribute(Columns.USU_LOGIN).toString() : "");
            String adeResponsavel = TextHelper.forHtmlContent((loginResponsavel.equalsIgnoreCase((String) autdes.getAttribute(Columns.USU_CODIGO)) && autdes.getAttribute(Columns.USU_TIPO_BLOQ) != null) ? (autdes.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)") : loginResponsavel);

            // Somente CSE/SUP e CSA/COR ligada à consignação podem ver o campo ADE_IDENTIFICADOR
            boolean podeMostrarIdentificador = (responsavel.isCseSup() || (responsavel.isCsaCor() && responsavel.getCsaCodigo().equals(csaCodigo)));

            Collection<RelacionamentoAutorizacao> adeCompradas = compraContratoController.getRelacionamentoCompra(adeCodigo);
            if (!adeCompradas.isEmpty()) {
                String csaCodigoDest = adeCompradas.stream().iterator().next().getConsignatariaByCsaCodigoDestino().getCsaCodigo();
                podeMostrarIdentificador = podeMostrarIdentificador || (responsavel.isCsaCor() && responsavel.getCsaCodigo().equals(csaCodigoDest));
            }

            // Se um contrato de cartão possui taxa de juros cadastrada, então permite que seja exibida
            if (CodedValues.NSE_CARTAO.equals(nseCodigo) && !TextHelper.isNull(vlrLiqTaxaJuros)) {
                permiteVlrLiqTxJuros = true;
            }

            // Informações de saldo devedor de compra de contrato
            String valorSaldoDevedor = TextHelper.forHtmlContent((saldoDevedorTO != null) ? NumberHelper.format(saldoDevedorTO.getSdvValor().doubleValue(), NumberHelper.getLang()) : "");
            String valorSaldoDevedorDesc = TextHelper.forHtmlContent((saldoDevedorTO != null && saldoDevedorTO.getSdvValorComDesconto() != null) ? NumberHelper.format(saldoDevedorTO.getSdvValorComDesconto().doubleValue(), NumberHelper.getLang()) : null);
            String bancoSaldoDevedor = TextHelper.forHtmlContent((saldoDevedorTO != null && saldoDevedorTO.getBcoCodigo() != null) ? saldoDevedorTO.getBcoCodigo().toString() : "");
            String agenciaSaldoDevedor = TextHelper.forHtmlContent((saldoDevedorTO != null) ? saldoDevedorTO.getSdvAgencia() : "");
            String contaSaldoDevedor = TextHelper.forHtmlContent((saldoDevedorTO != null) ? saldoDevedorTO.getSdvConta() : "");
            String nomeFavorecidoSdv = TextHelper.forHtmlContent((saldoDevedorTO != null) ? saldoDevedorTO.getSdvNomeFavorecido() : "");
            String cnpjSaldoDevedor = TextHelper.forHtmlContent((saldoDevedorTO != null) ? saldoDevedorTO.getSdvCnpj() : "");
            String numeroContratoSdv = TextHelper.forHtmlContent((saldoDevedorTO != null) ? saldoDevedorTO.getSdvNumeroContrato() : "");

            String matriculaOri = TextHelper.forHtmlContent(autdes.getAttribute("RSE_CODIGO_ORI"));
            String matriculaDes = TextHelper.forHtmlContent(autdes.getAttribute("RSE_CODIGO_DES"));

            // Informação de saldo devedor de controle de saldo
            String valorSaldoDevedorControle = TextHelper.forHtmlContent(autdes.getAttribute(Columns.ADE_VLR_SDO_RET) != null ? NumberHelper.reformat(autdes.getAttribute(Columns.ADE_VLR_SDO_RET).toString(), "en", NumberHelper.getLang()) : "");

            // Informações da despesa individual
            String planoIdentificador = TextHelper.forHtmlContent(autdes.getAttribute(Columns.PLA_IDENTIFICADOR));
            String planoDescricao = TextHelper.forHtmlContent(autdes.getAttribute(Columns.PLA_DESCRICAO));
            String endDescricao = TextHelper.forHtmlContent(autdes.getAttribute(Columns.ECH_DESCRICAO));
            String endComplemento = TextHelper.forHtmlContent(autdes.getAttribute(Columns.PRM_COMPL_ENDERECO));

            // Recupera a ordem de inclusão deste contrato para o serviço e o servidor em questão
            if (boolTpsNumeraContratosServidor && !CodedValues.SAD_CODIGOS_INATIVOS.contains(sadCodigo)) {
                List<TransferObject> adeRseSvcList = pesquisarConsignacaoController.pesquisarContratosPorRseSvc((String) autdes.getAttribute(Columns.RSE_CODIGO), svcCodigo, responsavel);
                for (TransferObject rseContrato : adeRseSvcList) {
                    numContrato++;
                    if (rseContrato.getAttribute(Columns.ADE_CODIGO).equals(autdes.getAttribute(Columns.ADE_CODIGO))) {
                        break;
                    }
                }
            }

            // Recupera a lista de tda's.
            List<TransferObject> tdaList = autorizacaoController.lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.CONSULTA, VisibilidadeTipoDadoAdicionalEnum.WEB, svcCodigo, csaCodigo, responsavel);

            // Linha do Topo
            if (table) {
                // Se tem tabela muda o CSS para TLEDtopo e CEDtopo
                if (autdesList.size() > 1) {
                    code.append(abrirTabela());
                    code.append(montarLinha(null, ApplicationResourcesHelper.getMessage("rotulo.consignacao.singular", responsavel) + " " + autCount, null, "tabelatopo"));
                } else {
                    code.append(abrirTabela());
                }
            } else // Se não tem tabela o CSS é TLEDmeio e CEDmeio
            if (autdesList.size() > 1) {
                code.append(montarLinha(null, ApplicationResourcesHelper.getMessage("rotulo.consignacao.singular", responsavel) + " " + autCount, null, "tabelatopo"));
            }

            if (!mensagem.equals("") && consultarConsignacao) {
                // Exibe mensagem caso seja tipo consultar
                code.append(montarLinha(mensagem, null, "TLEDtopo", null, FieldKeysConstants.DETALHE_CONSIGNACAO_MSG_PARCELA_PROCESSAMENTO));
            }

            if (renegociarConsignacao) {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel), csaNome, FieldKeysConstants.DETALHE_CONSIGNACAO_CONSIGNATARIA));
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel), servico, FieldKeysConstants.DETALHE_CONSIGNACAO_SERVICO));
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela.novo", responsavel) + " (" + labelTipoValor + ")", adeVlr, FieldKeysConstants.DETALHE_CONSIGNACAO_VALOR_PARCELA_NOVO));
                if (!PeriodoHelper.folhaMensal(responsavel)) {
                    String adePeriodicidade = (String) autdes.getAttribute(Columns.ADE_PERIODICIDADE);
                    if (CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(adePeriodicidade)) {
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.periodicidade", responsavel), ApplicationResourcesHelper.getMessage("rotulo.consignacao.periodicidade.mensal", responsavel), FieldKeysConstants.DETALHE_CONSIGNACAO_PERIODICIDADE));
                    } else {
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.periodicidade", responsavel), ApplicationResourcesHelper.getMessage("rotulo.consignacao.periodicidade.quinzenal", responsavel), FieldKeysConstants.DETALHE_CONSIGNACAO_PERIODICIDADE));
                    }
                }
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.prazo.novo", responsavel), adePrazo, FieldKeysConstants.DETALHE_CONSIGNACAO_PRAZO_NOVO));
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.carencia.nova", responsavel), adeCarencia, FieldKeysConstants.DETALHE_CONSIGNACAO_CARENCIA_NOVA));
                if (permiteCadVlrTac || permiteCadVlrIof) {
                    String label = "";
                    String valor = "";

                    if (permiteCadVlrTac) {
                        label += ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.tac.novo", responsavel) + " (" + rotuloMoeda + ")";
                    }
                    if (permiteCadVlrTac && permiteCadVlrIof) {
                        label += " - ";
                    }
                    if (permiteCadVlrIof) {
                        label += ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.iof", responsavel) + " (" + rotuloMoeda + ")";
                    }

                    if (permiteCadVlrTac) {
                        valor += (autdes.getAttribute(Columns.ADE_VLR_TAC) != null) ? autdes.getAttribute(Columns.ADE_VLR_TAC).toString() : "";
                    }
                    if (permiteCadVlrTac && permiteCadVlrIof) {
                        valor += " - ";
                    }
                    if (permiteCadVlrIof) {
                        valor += (autdes.getAttribute(Columns.ADE_VLR_IOF) != null) ? autdes.getAttribute(Columns.ADE_VLR_IOF).toString() : "";
                    }

                    code.append(montarLinha(label, valor, FieldKeysConstants.DETALHE_CONSIGNACAO_VALOR_TAC_IOF_NOVO));
                }
                if (permiteCadVlrLiqLib && vlrLiquidoLiberado != null) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.liquido.liberado.novo", responsavel) + " (" + rotuloMoeda + ")", vlrLiquidoLiberado, FieldKeysConstants.DETALHE_CONSIGNACAO_VALOR_LIQUIDO_LIBERADO_NOVO));
                }
                if (permiteCadVlrMensVinc) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.mensalidade.vinc", responsavel) + " (" + rotuloMoeda + ")", vlrMensalidadeVinc, FieldKeysConstants.DETALHE_CONSIGNACAO_VALOR_MENSALIDADE_VINC));
                }
                if (boolTpsSegPrestamista) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.seguro.prestamista", responsavel), vlrSegPrestamista, FieldKeysConstants.DETALHE_CONSIGNACAO_VALOR_SEGURO_PRESTAMISTA));
                }
                if (permiteVlrLiqTxJuros) {
                    if (temCET) {
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet.novo", responsavel), vlrLiqTaxaJuros, FieldKeysConstants.DETALHE_CONSIGNACAO_CET_NOVO));
                    } else {
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros.nova", responsavel), vlrLiqTaxaJuros, FieldKeysConstants.DETALHE_CONSIGNACAO_TAXA_JUROS_NOVA));
                    }
                }
                if (permiteCadIndice) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.indice", responsavel), TextHelper.isNull(adeIndice) ? "" : adeIndice, FieldKeysConstants.DETALHE_CONSIGNACAO_INDICE));
                }
                if (podeMostrarIdentificador) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.identificador", responsavel), TextHelper.isNull(autdes.getAttribute(Columns.ADE_IDENTIFICADOR)) ? "" : autdes.getAttribute(Columns.ADE_IDENTIFICADOR), FieldKeysConstants.DETALHE_CONSIGNACAO_IDENTIFICADOR));
                }
                for (TransferObject tda : tdaList) {
                    if ((tda.getAttribute(Columns.TDA_TEN_CODIGO).equals(Log.AUTORIZACAO))) {
                        String valor = (String) autdes.getAttribute("TDA_" + tda.getAttribute(Columns.TDA_CODIGO));
                        code.append(montarLinha((String) tda.getAttribute(Columns.TDA_DESCRICAO), TextHelper.forHtmlContent(valor) == null ? "" : TextHelper.forHtmlContent(valor), FieldKeysConstants.DETALHE_CONSIGNACAO_DADOS_AUTORIZACAO));
                    }
                }
                if (!TextHelper.isNull(autdes.getAttribute(Columns.TDA_DESCRICAO))) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.modalidade.operacao", responsavel), autdes.getAttribute(Columns.TDA_DESCRICAO), FieldKeysConstants.DETALHE_CONSIGNACAO_DADOS_AUTORIZACAO));
                }
                if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILE1"))) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.anexo.arquivo", responsavel), JspHelper.verificaVarQryStr(request, "FILE1"), FieldKeysConstants.DETALHE_CONSIGNACAO_ANEXO));
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.anexo.arquivo.desc", responsavel), TextHelper.forHtmlContent(JspHelper.verificaVarQryStr(request, "AAD_DESCRICAO")), FieldKeysConstants.DETALHE_CONSIGNACAO_ANEXO_DESC));
                }

            } else if (!deferimentoPelaCsa && !editarPropostaLeilao && !exibePrice) {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel), autdes.getAttribute(Columns.ADE_NUMERO), FieldKeysConstants.DETALHE_CONSIGNACAO_NUMERO));

                if (table) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.reserva", responsavel), DateHelper.toDateTimeString((java.util.Date) autdes.getAttribute(Columns.ADE_DATA)), "TLEDtopo", "CEDtopo", FieldKeysConstants.DETALHE_CONSIGNACAO_DATA_RESERVA));
                } else {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.reserva", responsavel), DateHelper.toDateTimeString((java.util.Date) autdes.getAttribute(Columns.ADE_DATA)), FieldKeysConstants.DETALHE_CONSIGNACAO_DATA_RESERVA));
                }

                String statusAde = (String) autdes.getAttribute(Columns.SAD_DESCRICAO);
                if (autdes.getAttribute(Columns.ADE_DATA_STATUS) != null && !ParamSist.paramEquals(CodedValues.TPC_EXIBE_ADE_DATA_STATUS_CONSULTAR_CONSIGNACAO, CodedValues.TPC_NAO, responsavel)) {
                    String dataAtualizacao = DateHelper.toDateTimeString((Date) autdes.getAttribute(Columns.ADE_DATA_STATUS));
                    statusAde = String.format("%s (%s)", statusAde, dataAtualizacao);
                }
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.status", responsavel), statusAde, FieldKeysConstants.DETALHE_CONSIGNACAO_STATUS));
                if (autdesList.size() == 1) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel), TextHelper.forHtmlContent(LoginHelper.getCseNome(responsavel)), FieldKeysConstants.DETALHE_CONSIGNACAO_CONSIGNANTE));
                }

                // Exibe os dados do servidor de acordo com a classe DetalheServidorTag
                // para os casos em que detalha apenas uma consignação
                if ((autdesList.size() == 1 || multiplosServidores) && !simularRenegociacao) {
                    code.append(geraDetalheServidor(autdes, temValidacaoDataNasc, boolExibeInfBancariaServidor, null, responsavel));
                }

                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel), csaNome, FieldKeysConstants.DETALHE_CONSIGNACAO_CONSIGNATARIA));

                if (!corNome.equals("")) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel), corNome, FieldKeysConstants.DETALHE_CONSIGNACAO_CORRESPONDENTE));
                }
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel), servico + (adeCodReg.equals(CodedValues.COD_REG_ESTORNO) ? " - " + ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel) : ""), FieldKeysConstants.DETALHE_CONSIGNACAO_SERVICO));
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.folha.moeda", responsavel), adeVlrFolha, FieldKeysConstants.DETALHE_CONSIGNACAO_VALOR_FOLHA));

                if (!TextHelper.isNull(planoIdentificador) && !TextHelper.isNull(planoDescricao)) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.plano.singular", responsavel), planoIdentificador + " - " + planoDescricao, FieldKeysConstants.DETALHE_CONSIGNACAO_PLANO));
                }

                if (!TextHelper.isNull(endDescricao) && !TextHelper.isNull(endComplemento)) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.endereco.singular", responsavel), endDescricao + " - " + endComplemento, FieldKeysConstants.DETALHE_CONSIGNACAO_ENDERECO));
                }

                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela", responsavel) + " (" + labelTipoValor + ")", adeVlr, FieldKeysConstants.DETALHE_CONSIGNACAO_VALOR_PARCELA));
                if (!adeVlrRef.equals(adeVlr)) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela.ref", responsavel) + " (" + labelTipoValor + ")", adeVlrRef, FieldKeysConstants.DETALHE_CONSIGNACAO_VALOR_PARCELA_REF));
                }
                if (autdes.getAttribute(Columns.ADE_VLR_PARCELA_FOLHA) != null) {
                    String adeVlrParcelaFolha = TextHelper.forHtmlContent(NumberHelper.reformat(autdes.getAttribute(Columns.ADE_VLR_PARCELA_FOLHA).toString(), "en", NumberHelper.getLang()));
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela.desc.folha", responsavel) + " (" + labelTipoValor + ")", adeVlrParcelaFolha + " (*)", FieldKeysConstants.DETALHE_CONSIGNACAO_VALOR_PARCELA_DESC_FOLHA));
                }

                if (!PeriodoHelper.folhaMensal(responsavel)) {
                    String adePeriodicidade = (String) autdes.getAttribute(Columns.ADE_PERIODICIDADE);
                    if (CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(adePeriodicidade)) {
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.periodicidade", responsavel), ApplicationResourcesHelper.getMessage("rotulo.consignacao.periodicidade.mensal", responsavel), FieldKeysConstants.DETALHE_CONSIGNACAO_PERIODICIDADE));
                    } else {
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.periodicidade", responsavel), ApplicationResourcesHelper.getMessage("rotulo.consignacao.periodicidade.quinzenal", responsavel), FieldKeysConstants.DETALHE_CONSIGNACAO_PERIODICIDADE));
                    }
                }

                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.prazo", responsavel), adePrazo, FieldKeysConstants.DETALHE_CONSIGNACAO_PRAZO));
                if (!adePrazoRef.equals(adePrazo) && !simularRenegociacao) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.prazo.ref", responsavel), adePrazoRef, FieldKeysConstants.DETALHE_CONSIGNACAO_PRAZO_REF));
                }
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.carencia", responsavel), adeCarencia, FieldKeysConstants.DETALHE_CONSIGNACAO_CARENCIA));
                if (!anoMesIniRef.equals(anoMesIni)) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.inicial.ref", responsavel), anoMesIniRef, FieldKeysConstants.DETALHE_CONSIGNACAO_DATA_INICIAL_REF));
                }
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.inicial", responsavel), anoMesIni, FieldKeysConstants.DETALHE_CONSIGNACAO_DATA_INICIAL));
                if (!anoMesFimRef.equals(anoMesFim)) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.final.ref", responsavel), (anoMesFimRef.equals("") ? rotuloPrzIndet : anoMesFimRef), FieldKeysConstants.DETALHE_CONSIGNACAO_DATA_FINAL_REF));
                }
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.final", responsavel), (anoMesFim.equals("") ? rotuloPrzIndet : anoMesFim), FieldKeysConstants.DETALHE_CONSIGNACAO_DATA_FINAL));

                // DESENV-15606: Verificar se o sistema preserva parcela a fim de exibir o campo de data prevista para conclusão
                if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_DATA_PREVISTA_CONCLUSAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel) && csaPermitePreservacaoParcela && ParamSist.paramEquals(CodedValues.TPC_PRESERVA_PRD_REJEITADA, CodedValues.TPC_SIM, responsavel) && !anoMesFim.equals("")) {
                    int prazo = autdes.getAttribute(Columns.ADE_PRAZO) != null ? ((Integer) autdes.getAttribute(Columns.ADE_PRAZO)).intValue() : -1;
                    int pagas = autdes.getAttribute(Columns.ADE_PRD_PAGAS) != null ? ((Integer) autdes.getAttribute(Columns.ADE_PRD_PAGAS)).intValue() : 0;
                    int prazoRest = prazo - pagas;

                    List<ParcelaDescontoPeriodo> parcelasEmProcessamento = null;
                    if (ParamSist.getBoolParamSist(CodedValues.TPC_CONSIDERA_PARCELAS_AGUARD_PROCESSAMENTO, responsavel)) {
                        parcelasEmProcessamento = parcelaController.findByAutDescontoStatus(adeCodigo, CodedValues.SPD_EMPROCESSAMENTO, responsavel);
                        prazoRest -= parcelasEmProcessamento != null ? parcelasEmProcessamento.size() : 0;
                    }

                    String orgCodigo = autdes.getAttribute(Columns.ORG_CODIGO).toString();
                    Date adeAnoMesIni = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI);
                    Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
                    Date dataPrevistaConclusao = PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, adeAnoMesIni.compareTo(periodoAtual) > 0 ? adeAnoMesIni : periodoAtual, prazoRest, autdes.getAttribute(Columns.ADE_PERIODICIDADE).toString(), responsavel);

                    if (!TextHelper.isNull(dataPrevistaConclusao) && dataPrevistaConclusao.compareTo((Date) autdes.getAttribute(Columns.ADE_ANO_MES_FIM)) > 0) {
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.prevista.conclusao", responsavel), DateHelper.toPeriodString(dataPrevistaConclusao), FieldKeysConstants.DETALHE_CONSIGNACAO_DATA_PREVISTA_CONCLUSAO));
                    }
                }

                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.parcelas.pagas", responsavel), adePrdPagas, FieldKeysConstants.DETALHE_CONSIGNACAO_PARCELAS_PAGAS));
                if (!adePrdPagasTotal.equals(adePrdPagas) && !simularRenegociacao) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.parcelas.pagas.total", responsavel), adePrdPagasTotal, FieldKeysConstants.DETALHE_CONSIGNACAO_PARCELAS_PAGAS_TOTAL));
                }
                if (podeMostrarIdentificador) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.identificador", responsavel), autdes.getAttribute(Columns.ADE_IDENTIFICADOR), FieldKeysConstants.DETALHE_CONSIGNACAO_IDENTIFICADOR));
                }
                if (permiteCadIndice) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.indice", responsavel), adeIndice, FieldKeysConstants.DETALHE_CONSIGNACAO_INDICE));
                }

                if (ranking != null && !simularRenegociacao) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.ranking", responsavel), ranking + "&ordf;", FieldKeysConstants.DETALHE_CONSIGNACAO_RANKING));
                }
                if (vlrLiberado != null) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.liberado", responsavel) + " (" + rotuloMoeda + ")", NumberHelper.format(vlrLiberado.doubleValue(), NumberHelper.getLang()), FieldKeysConstants.DETALHE_CONSIGNACAO_VALOR_LIBERADO));
                }

                if (vlrLiberadoCalc != null && boolVisualizaValorLiberadoCalc) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.liberado.calc", responsavel) + " (" + rotuloMoeda + ")", NumberHelper.format(vlrLiberadoCalc.doubleValue(), NumberHelper.getLang()), FieldKeysConstants.DETALHE_CONSIGNACAO_VALOR_LIBERADO_CALC));
                }

                // Campos exibidos apenas na consulta de consignação
                if (consultarConsignacao) {
                    if (responsavel.isCseSup() && !TextHelper.isNull(adeBanco) && !TextHelper.isNull(adeAgencia) && !TextHelper.isNull(adeConta)) {
                        String texto = ApplicationResourcesHelper.getMessage("rotulo.consignacao.informacoesbancarias.banco", responsavel) + ": " + adeBanco + " - " + ApplicationResourcesHelper.getMessage("rotulo.consignacao.informacoesbancarias.agencia", responsavel) + ": " + adeAgencia + " - " + ApplicationResourcesHelper.getMessage("rotulo.consignacao.informacoesbancarias.conta", responsavel) + ": " + adeConta;
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.informacoesbancarias", responsavel), texto, FieldKeysConstants.DETALHE_CONSIGNACAO_INFORMACOES_BANCARIAS));
                    }

                    if (boolTpsVlrDevido || exibeCapitalDevido) {
                        String capitalDevido = null;

                        if (autdes.getAttribute(Columns.ADE_PRAZO) != null && adeVlrDecimal != null && (CodedValues.TIPO_VLR_FIXO.equals(autdes.getAttribute(Columns.ADE_TIPO_VLR)) || CodedValues.TIPO_VLR_TOTAL_MARGEM.equals(autdes.getAttribute(Columns.ADE_TIPO_VLR)))) {

                            // Trata o prazo e valor como String pois podem ser retornados tanto como numérico como texto (segunda_senha.jsp)
                            int pagas = (autdes.getAttribute(Columns.ADE_PRD_PAGAS) == null) ? 0 : Integer.parseInt(autdes.getAttribute(Columns.ADE_PRD_PAGAS).toString());
                            int prazo = (autdes.getAttribute(Columns.ADE_PRAZO) == null) ? 0 : Integer.parseInt(autdes.getAttribute(Columns.ADE_PRAZO).toString());
                            int prazoRestante = prazo - pagas;
                            if (prazoRestante >= 0) {
                                double valorParcela = ((java.math.BigDecimal) (autdes.getAttribute(Columns.ADE_VLR_PARCELA_FOLHA) != null ? autdes.getAttribute(Columns.ADE_VLR_PARCELA_FOLHA) : adeVlrDecimal)).doubleValue();
                                double valorCapitalDevido = valorParcela * prazoRestante;
                                capitalDevido = NumberHelper.format(valorCapitalDevido, NumberHelper.getLang());
                                if (autdes.getAttribute(Columns.ADE_VLR_PARCELA_FOLHA) != null) {
                                    capitalDevido += " (*)";
                                }
                            }
                        }

                        if (!TextHelper.isNull(capitalDevido)) {
                            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.capital.devido", responsavel) + " (" + rotuloMoeda + ")", capitalDevido, FieldKeysConstants.DETALHE_CONSIGNACAO_VALOR_CAPITAL_DEVIDO));
                        }
                    }

                    if (boolTpsSegPrestamista) {
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.seguro.prestamista", responsavel), vlrSegPrestamista, FieldKeysConstants.DETALHE_CONSIGNACAO_VALOR_SEGURO_PRESTAMISTA));
                    }

                    if (permiteCadVlrTac || permiteCadVlrIof) {
                        String label = "";
                        String valor = "";

                        if (permiteCadVlrTac) {
                            label += ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.tac", responsavel) + " (" + rotuloMoeda + ")";
                        }
                        if (permiteCadVlrTac && permiteCadVlrIof) {
                            label += " - ";
                        }
                        if (permiteCadVlrIof) {
                            label += ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.iof", responsavel) + " (" + rotuloMoeda + ")";
                        }

                        if (permiteCadVlrTac) {
                            valor += (autdes.getAttribute(Columns.ADE_VLR_TAC) != null) ? NumberHelper.format((new BigDecimal(autdes.getAttribute(Columns.ADE_VLR_TAC).toString())).doubleValue(), NumberHelper.getLang()) : "";
                        }
                        if (permiteCadVlrTac && permiteCadVlrIof) {
                            valor += " - ";
                        }
                        if (permiteCadVlrIof) {
                            valor += (autdes.getAttribute(Columns.ADE_VLR_IOF) != null) ? NumberHelper.format((new BigDecimal(autdes.getAttribute(Columns.ADE_VLR_IOF).toString())).doubleValue(), NumberHelper.getLang()) : "";
                        }

                        code.append(montarLinha(label, valor, FieldKeysConstants.DETALHE_CONSIGNACAO_VALOR_TAC_IOF));
                    }

                    if (permiteCadVlrLiqLib) {
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.liquido.liberado", responsavel) + " (" + rotuloMoeda + ")", vlrLiquidoLiberado, FieldKeysConstants.DETALHE_CONSIGNACAO_VALOR_LIQUIDO_LIBERADO));
                    }
                    if (permiteCadVlrMensVinc) {
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.mensalidade.vinc", responsavel) + " (" + rotuloMoeda + ")", vlrMensalidadeVinc, FieldKeysConstants.DETALHE_CONSIGNACAO_VALOR_MENSALIDADE_VINC));
                    }

                    // Se existe a informação de valor líquido de juros,
                    // ou alguma taxa/coeficiente está associada ao contrato, exibe a informação.
                    if (permiteVlrLiqTxJuros || !TextHelper.isNull(vlrCoeficiente)) {
                        String taxaMensal = permiteVlrLiqTxJuros ? vlrLiqTaxaJuros : vlrCoeficiente;
                        String taxaAnual = CDCHelper.getStrTaxaEquivalenteAnual(taxaMensal);
                        if (adeTipoTaxa != null) {
                            if (adeTipoTaxa.equals(CodedValues.TIPO_TAXA_CET)) {
                                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet", responsavel), taxaMensal, FieldKeysConstants.DETALHE_CONSIGNACAO_CET));
                                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet.anual", responsavel), (taxaAnual != null ? taxaAnual : ""), FieldKeysConstants.DETALHE_CONSIGNACAO_CET_ANUAL));
                            } else if (adeTipoTaxa.equals(CodedValues.TIPO_TAXA_JUROS)) {
                                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros", responsavel), taxaMensal, FieldKeysConstants.DETALHE_CONSIGNACAO_TAXA_JUROS));
                            } else if (adeTipoTaxa.equals(CodedValues.TIPO_TAXA_COEFICIENTE)) {
                                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.coeficiente", responsavel), taxaMensal, FieldKeysConstants.DETALHE_CONSIGNACAO_TAXA_COEFICIENTE));
                            } else {
                                LOG.warn("Tipo de Taxa não identificado para contrato " + adeCodigo);
                            }
                        } else if (temCET) {
                            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet", responsavel), taxaMensal, FieldKeysConstants.DETALHE_CONSIGNACAO_CET));
                            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet.anual", responsavel), (taxaAnual != null ? taxaAnual : ""), FieldKeysConstants.DETALHE_CONSIGNACAO_CET_ANUAL));
                        } else if (permiteVlrLiqTxJuros || utilizaTaxaJuros) {
                            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros", responsavel), taxaMensal, FieldKeysConstants.DETALHE_CONSIGNACAO_TAXA_JUROS));
                        } else {
                            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.coeficiente", responsavel), taxaMensal, FieldKeysConstants.DETALHE_CONSIGNACAO_TAXA_COEFICIENTE));
                        }

                        // Se exibe CET para contratos com Taxa de Juros: exibe CET se o sistema não trabalha com CET,
                        // ou caso trabalhe, o adeTipoTaxa informe que o contrato foi feito com Taxa de Juros
                        if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_CET_CONTRATOS_TAXA_JUROS, CodedValues.TPC_SIM, responsavel)) {
                            if ((!temCET && adeTipoTaxa == null) || (adeTipoTaxa != null && adeTipoTaxa.equals(CodedValues.TIPO_TAXA_JUROS))) {
                                BigDecimal vlrCetMensal = SimulacaoHelper.recuperaCetMensal(autdes, responsavel);
                                // Se o CET mensal calculado é maior que zero significa que os valores
                                // de prazo, prestação e vlr liberado estão de acordo.
                                if (vlrCetMensal != null && vlrCetMensal.signum() > 0) {
                                    // Formata os valores para exibição
                                    String cetMensal = NumberHelper.format(vlrCetMensal.doubleValue(), NumberHelper.getLang());
                                    String cetAnual = CDCHelper.getStrTaxaEquivalenteAnual(cetMensal);
                                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet", responsavel), cetMensal, FieldKeysConstants.DETALHE_CONSIGNACAO_CET));
                                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet.anual", responsavel), (cetAnual != null ? cetAnual : ""), FieldKeysConstants.DETALHE_CONSIGNACAO_CET_ANUAL));
                                }
                            }
                        }
                    }

                    if (boolPossuiCorrecaoVlrPresente) {
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.evento", responsavel), adeDataHoraOcorrencia, FieldKeysConstants.DETALHE_CONSIGNACAO_DATA_EVENTO));
                    }

                    if (saldoDevedorTO != null) {
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.valor", responsavel) + " (" + rotuloMoeda + ")", valorSaldoDevedor, FieldKeysConstants.DETALHE_CONSIGNACAO_SALDO_DEVEDOR_VALOR));
                        if (valorSaldoDevedorDesc != null) {
                            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.valor.com.desconto", responsavel) + " (" + rotuloMoeda + ")", valorSaldoDevedorDesc, FieldKeysConstants.DETALHE_CONSIGNACAO_SALDO_DEVEDOR_VALOR_COM_DESCONTO));
                        }

                        /**
                         * Se for servidor e o contrato estiver deferido ou em andamento e possuir solicitação de saldo respondido,
                         * ou se não for servidor, exibe informações bancárias para saldo devedor
                         */
                        List<String> sadAtivos = Arrays.asList(new String[] { CodedValues.SAD_DEFERIDA, CodedValues.SAD_EMANDAMENTO });
                        boolean exibeInfoBancariaSdv = !responsavel.isSer() || (responsavel.isSer() && sadAtivos.contains(sadCodigo) && saldoDevedorController.temSolicitacaoSaldoDevedorRespondida(adeCodigo, responsavel));
                        if (exibeInfoBancariaSdv) {
                            boolean temBanco = (!bancoSaldoDevedor.equals(""));
                            boolean temAgenc = (!agenciaSaldoDevedor.equals(""));
                            boolean temConta = (!contaSaldoDevedor.equals(""));

                            String infBancariasBanco = (temBanco) ? ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.informacoesbancarias.banco", responsavel) + ": " + bancoSaldoDevedor : "";
                            String infBancariasAgenc = (temAgenc) ? ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.informacoesbancarias.agencia", responsavel) + ": " + agenciaSaldoDevedor : "";
                            String infBancariasConta = (temConta) ? ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.informacoesbancarias.conta", responsavel) + ": " + contaSaldoDevedor : "";

                            String infBancarias = infBancariasBanco;
                            if (temAgenc || temConta) {
                                if (temBanco) {
                                    infBancarias += " - ";
                                }
                                if (temAgenc && temConta) {
                                    infBancarias += infBancariasAgenc + " - " + infBancariasConta;
                                } else if (temAgenc) {
                                    infBancarias += infBancariasAgenc;
                                } else {
                                    infBancarias += infBancariasConta;
                                }
                            }

                            if (!infBancarias.equals("")) {
                                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.informacoesbancarias", responsavel), infBancarias, FieldKeysConstants.DETALHE_CONSIGNACAO_SALDO_DEVEDOR_INFORMACOES_BANCARIAS));
                            }
                            String favorecido = nomeFavorecidoSdv + (!nomeFavorecidoSdv.equals("") && !cnpjSaldoDevedor.equals("") ? " - " : "") + cnpjSaldoDevedor;
                            if (!favorecido.equals("")) {
                                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.favorecido", responsavel), favorecido, FieldKeysConstants.DETALHE_CONSIGNACAO_SALDO_DEVEDOR_FAVORECIDO));
                            }
                            if (!responsavel.isSer() && !TextHelper.isNull(numeroContratoSdv)) {
                                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.numero.contrato", responsavel), numeroContratoSdv, FieldKeysConstants.DETALHE_CONSIGNACAO_SALDO_DEVEDOR_NUMERO_CONTRATO));
                            }
                        }
                    } else if (!TextHelper.isNull(valorSaldoDevedorControle)) {
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.valor", responsavel) + " (" + rotuloMoeda + ")", valorSaldoDevedorControle, FieldKeysConstants.DETALHE_CONSIGNACAO_SALDO_DEVEDOR_VALOR));
                    }

                    // Se a consignação está suspensa e possui data de reativação automática, exibe o campo na consulta
                    if (CodedValues.SAD_CODIGOS_SUSPENSOS.contains(sadCodigo) && autdes.getAttribute(Columns.ADE_DATA_REATIVACAO_AUTOMATICA) != null) {
                        String dataReativacaoAutomatica = DateHelper.toDateString((java.util.Date) autdes.getAttribute(Columns.ADE_DATA_REATIVACAO_AUTOMATICA));
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.reativacao.automatica", responsavel), dataReativacaoAutomatica, FieldKeysConstants.DETALHE_CONSIGNACAO_DATA_REATIVACAO_AUTOMATICA));
                    }
                }

                if (boolTpsNumeraContratosServidor && !CodedValues.SAD_CODIGOS_INATIVOS.contains(sadCodigo)) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.ordem.inclusao", responsavel), numContrato + "&ordm", FieldKeysConstants.DETALHE_CONSIGNACAO_ORDEM_INCLUSAO));
                }

                if (boolExigeCodAutSolicitacao && responsavel.isSer() && sadCodigo.equals(CodedValues.SAD_SOLICITADO)) {
                    String codigoAutSolic = autorizacaoController.getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_CODIGO_AUTORIZACAO_SOLICITACAO, arquivado, responsavel);
                    if (!TextHelper.isNull(codigoAutSolic)) {
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.codigo.autorizacao", responsavel), codigoAutSolic, FieldKeysConstants.DETALHE_CONSIGNACAO_CODIGO_AUTORIZACAO));
                    }
                }

                if (editarPropostaFinanCartao) {
                    if (saldoDevedorTO != null) {
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.valor", responsavel) + " (" + rotuloMoeda + ")", valorSaldoDevedor, FieldKeysConstants.DETALHE_CONSIGNACAO_SALDO_DEVEDOR_VALOR));
                        if (valorSaldoDevedorDesc != null) {
                            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.valor.com.desconto", responsavel) + " (" + rotuloMoeda + ")", valorSaldoDevedorDesc, FieldKeysConstants.DETALHE_CONSIGNACAO_SALDO_DEVEDOR_VALOR_COM_DESCONTO));
                        }
                    }
                }

                if (!TextHelper.isNull(matriculaOri)) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula.origem", responsavel), matriculaOri, FieldKeysConstants.DETALHE_CONSIGNACAO_MATRICULA_ORIGEM));
                }

                if (!TextHelper.isNull(matriculaDes)) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula.destino", responsavel), matriculaDes, FieldKeysConstants.DETALHE_CONSIGNACAO_MATRICULA_DESTINO));
                }

                // Preenche os valores dos dados da autorização aos quais tem permissão
                for (TransferObject tda : tdaList) {
                    if ((tda.getAttribute(Columns.TDA_TEN_CODIGO).equals(Log.AUTORIZACAO))) {
                        String valor = autorizacaoController.getValorDadoAutDesconto(adeCodigo, (String) tda.getAttribute(Columns.TDA_CODIGO), responsavel);
                        code.append(montarLinha((String) tda.getAttribute(Columns.TDA_DESCRICAO), autdes.getAttribute("TDA_" + tda.getAttribute(Columns.TDA_CODIGO)) != null ? autdes.getAttribute("TDA_" + tda.getAttribute(Columns.TDA_CODIGO)) : TextHelper.forHtmlContent(valor) == null ? "" : TextHelper.forHtmlContent(valor), FieldKeysConstants.DETALHE_CONSIGNACAO_DADOS_AUTORIZACAO));
                    }
                }

                // Exibe dados de autorização, se parâmetro estiver configurado para isso.
                String tpaModalidadeOperacao = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INF_MODALIDADE_OPERACAO_OBRIGATORIO, responsavel);
                boolean exigeModalidadeOperacao = (!TextHelper.isNull(tpaModalidadeOperacao) && tpaModalidadeOperacao.equals("S")) ? true : false;
                if (exigeModalidadeOperacao) {
                    String modalidadeOpVlr = autorizacaoController.getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_MODALIDADE_OPERACAO, responsavel);
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.modalidade.operacao", responsavel), !TextHelper.isNull(modalidadeOpVlr) ? modalidadeOpVlr : (!TextHelper.isNull(autdes.getAttribute(CodedValues.TDA_MODALIDADE_OPERACAO))) ? (String) autdes.getAttribute(CodedValues.TDA_MODALIDADE_OPERACAO) : "", FieldKeysConstants.DETALHE_CONSIGNACAO_DADOS_AUTORIZACAO));
                }

                String tpaMatriculaSerCsa = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFORMAR_MATRICULA_NA_CSA_OBRIGATORIO, responsavel);
                boolean exigeMatriculaSerCsa = (!TextHelper.isNull(tpaMatriculaSerCsa) && tpaMatriculaSerCsa.equals("S")) ? true : false;
                if (exigeMatriculaSerCsa) {
                    String matriculaSerCsaVlr = autorizacaoController.getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_MATRICULA_SER_NA_CSA, responsavel);
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.matricula.ser.csa", responsavel), !TextHelper.isNull(matriculaSerCsaVlr) ? matriculaSerCsaVlr : (!TextHelper.isNull(autdes.getAttribute(CodedValues.TDA_MATRICULA_SER_NA_CSA))) ? (String) autdes.getAttribute(CodedValues.TDA_MATRICULA_SER_NA_CSA) : "", FieldKeysConstants.DETALHE_CONSIGNACAO_DADOS_AUTORIZACAO));
                }

                if (ParamSist.paramEquals(CodedValues.TPC_INFORMA_NUM_PORTABILIDADE_CIP_COMPRA, CodedValues.NUM_PORTABILIDADE_CIP_COMPRA_OPCIONAL, responsavel) || ParamSist.paramEquals(CodedValues.TPC_INFORMA_NUM_PORTABILIDADE_CIP_COMPRA, CodedValues.NUM_PORTABILIDADE_CIP_COMPRA_OBRIGATORIO, responsavel)) {
                    String numCipCompra = autorizacaoController.getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_NUM_PORTABILIDADE_CIP, responsavel);
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.num.cip.compra", responsavel), !TextHelper.isNull(numCipCompra) ? numCipCompra : (!TextHelper.isNull(autdes.getAttribute(CodedValues.TDA_NUM_PORTABILIDADE_CIP))) ? (String) autdes.getAttribute(CodedValues.TDA_NUM_PORTABILIDADE_CIP) : "", FieldKeysConstants.DETALHE_CONSIGNACAO_DADOS_AUTORIZACAO));
                }

                if (table) {
                    if (!alterarSemAnexo && !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILE1")) && autdesList.size() == 1) {
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.responsavel", responsavel), adeResponsavel, FieldKeysConstants.DETALHE_CONSIGNACAO_RESPONSAVEL));
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.anexo.arquivo", responsavel), JspHelper.verificaVarQryStr(request, "FILE1"), FieldKeysConstants.DETALHE_CONSIGNACAO_ANEXO));
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.anexo.arquivo.desc", responsavel), TextHelper.forHtmlContent(JspHelper.verificaVarQryStr(request, "AAD_DESCRICAO")), "TLEDbase", "CEDbase", FieldKeysConstants.DETALHE_CONSIGNACAO_ANEXO_DESC));
                    } else {
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.responsavel", responsavel), adeResponsavel, "TLEDbase", "CEDbase", FieldKeysConstants.DETALHE_CONSIGNACAO_RESPONSAVEL));
                    }
                    code.append(fecharTabela());
                } else {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.responsavel", responsavel), adeResponsavel, FieldKeysConstants.DETALHE_CONSIGNACAO_RESPONSAVEL));
                    if (!alterarSemAnexo && !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILE1")) && autdesList.size() == 1) {
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.anexo.arquivo", responsavel), JspHelper.verificaVarQryStr(request, "FILE1"), FieldKeysConstants.DETALHE_CONSIGNACAO_ANEXO));
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.anexo.arquivo.desc", responsavel), TextHelper.forHtmlContent(JspHelper.verificaVarQryStr(request, "AAD_DESCRICAO")), FieldKeysConstants.DETALHE_CONSIGNACAO_ANEXO_DESC));
                    }
                }
            } else if (editarPropostaLeilao) {
                code.append(geraDetalheServidor(autdes, temValidacaoDataNasc, boolExibeInfBancariaServidor, null, responsavel));
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel), autdes.getAttribute(Columns.ADE_NUMERO), FieldKeysConstants.DETALHE_CONSIGNACAO_NUMERO));

                if (table) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.reserva", responsavel), DateHelper.toDateTimeString((java.util.Date) autdes.getAttribute(Columns.ADE_DATA)), "TLEDtopo", "CEDtopo", FieldKeysConstants.DETALHE_CONSIGNACAO_DATA_RESERVA));
                } else {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.reserva", responsavel), DateHelper.toDateTimeString((java.util.Date) autdes.getAttribute(Columns.ADE_DATA)), FieldKeysConstants.DETALHE_CONSIGNACAO_DATA_RESERVA));
                }

                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.liquido.liberado", responsavel) + " (" + rotuloMoeda + ")", vlrLiquidoLiberado, FieldKeysConstants.DETALHE_CONSIGNACAO_VALOR_LIQUIDO_LIBERADO));
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela", responsavel) + " (" + labelTipoValor + ")", adeVlr, FieldKeysConstants.DETALHE_CONSIGNACAO_VALOR_PARCELA));
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.prazo", responsavel), adePrazo, FieldKeysConstants.DETALHE_CONSIGNACAO_PRAZO));

                //Exibe os dados de origem da consignação
                if (autdesObjctOrigem != null) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.data", responsavel), adeDataOrigem, FieldKeysConstants.DETALHE_CONSIGNACAO_ORIGEM));
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.vlr.parcela", responsavel) + " (" + labelTipoValor + ")", adeVlrOrigem, FieldKeysConstants.DETALHE_CONSIGNACAO_ORIGEM));
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.prazo", responsavel), adePrazoOrigem, FieldKeysConstants.DETALHE_CONSIGNACAO_ORIGEM));
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.qnt.prc.pagas", responsavel), adePrdPagasTotalOrigem, FieldKeysConstants.DETALHE_CONSIGNACAO_ORIGEM));
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.data.inicial", responsavel), adeAnoMesIniOrigem, FieldKeysConstants.DETALHE_CONSIGNACAO_ORIGEM));
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.data.final", responsavel), adeAnoMesFimOrigem, FieldKeysConstants.DETALHE_CONSIGNACAO_ORIGEM));
                }
            } else if (exibePrice) {
                if (!TextHelper.isNull(autdes.getAttribute(Columns.ADE_NUMERO))) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel), autdes.getAttribute(Columns.ADE_NUMERO), FieldKeysConstants.DETALHE_CONSIGNACAO_NUMERO));
                }

                if (table) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.reserva", responsavel), DateHelper.toDateTimeString((java.util.Date) autdes.getAttribute(Columns.ADE_DATA)), "TLEDtopo", "CEDtopo", FieldKeysConstants.DETALHE_CONSIGNACAO_DATA_RESERVA));
                } else {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.reserva", responsavel), DateHelper.toDateTimeString((java.util.Date) autdes.getAttribute(Columns.ADE_DATA)), FieldKeysConstants.DETALHE_CONSIGNACAO_DATA_RESERVA));
                }

                code.append(geraDetalheServidor(autdes, temValidacaoDataNasc, false, null, responsavel));

            } else {
                if (table) {
                    if (!mensagem.equals("") && consultarConsignacao) {
                        // Exibe mensagem caso seja tipo consultar
                        code.append(montarLinha(mensagem, null, "TLEDtopo", null, FieldKeysConstants.DETALHE_CONSIGNACAO_MSG_PARCELA_PROCESSAMENTO));
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.reserva", responsavel), DateHelper.toDateTimeString((java.util.Date) autdes.getAttribute(Columns.ADE_DATA)), FieldKeysConstants.DETALHE_CONSIGNACAO_DATA_RESERVA));
                    } else {
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.reserva", responsavel), DateHelper.toDateTimeString((java.util.Date) autdes.getAttribute(Columns.ADE_DATA)), "TLEDtopo", "CEDtopo", FieldKeysConstants.DETALHE_CONSIGNACAO_DATA_RESERVA));
                    }
                } else {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.reserva", responsavel), DateHelper.toDateTimeString((java.util.Date) autdes.getAttribute(Columns.ADE_DATA)), FieldKeysConstants.DETALHE_CONSIGNACAO_DATA_RESERVA));
                }

                code.append(geraDetalheServidor(autdes, temValidacaoDataNasc, boolExibeInfBancariaServidor, null, responsavel));
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela", responsavel) + " (" + labelTipoValor + ")", adeVlr, FieldKeysConstants.DETALHE_CONSIGNACAO_VALOR_PARCELA));
            }

            Date dataPrevistaCancelamento = !TextHelper.isNull(autdes.getAttribute("dataPrevistaCancelamento")) ? (Date) autdes.getAttribute("dataPrevistaCancelamento") : null;

            if(!TextHelper.isNull(dataPrevistaCancelamento)) {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.detalhe.consignacao.data.cancelamento.automatico", responsavel), DateHelper.toDateString(dataPrevistaCancelamento), FieldKeysConstants.DETALHE_CONSIGNACAO_DATA_CANCELAMENTO_AUTO));
            }

            if(ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel)) {
                String statusSolicitacao = !TextHelper.isNull(autdes.getAttribute("statusSolicitacao")) ? (String) autdes.getAttribute("statusSolicitacao") : null;
                if(!TextHelper.isNull(statusSolicitacao)) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.detalhe.consignacao.status.soliciticacao.validacao", responsavel), statusSolicitacao, FieldKeysConstants.DETALHE_CONSIGNACAO_STATUS_SOLICITACAO));
                }
            }
            autCount++;
        }

        if (!alterarSemAnexo && !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILE1")) && autdesList.size() > 1) {
            if (table) {
                code.append(abrirTabela());
                code.append(montarLinha(null, ApplicationResourcesHelper.getMessage("mensagem.consignacao.anexo.info", responsavel), null, "tabelatopo", FieldKeysConstants.DETALHE_CONSIGNACAO_ANEXO));
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.anexo.arquivo", responsavel), JspHelper.verificaVarQryStr(request, "FILE1"), "TLEDtopo", "CEDtopo", FieldKeysConstants.DETALHE_CONSIGNACAO_ANEXO));
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.anexo.arquivo.desc", responsavel), TextHelper.forHtmlContent(JspHelper.verificaVarQryStr(request, "AAD_DESCRICAO")), "TLEDbase", "CEDbase", FieldKeysConstants.DETALHE_CONSIGNACAO_ANEXO_DESC));
                code.append(fecharTabela());
            } else {
                code.append(montarLinha(null, ApplicationResourcesHelper.getMessage("mensagem.consignacao.anexo.info", responsavel), null, "tabelatopo", FieldKeysConstants.DETALHE_CONSIGNACAO_ANEXO));
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.anexo.arquivo", responsavel), JspHelper.verificaVarQryStr(request, "FILE1"), FieldKeysConstants.DETALHE_CONSIGNACAO_ANEXO));
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.consignacao.anexo.arquivo.desc", responsavel), TextHelper.forHtmlContent(JspHelper.verificaVarQryStr(request, "AAD_DESCRICAO")), FieldKeysConstants.DETALHE_CONSIGNACAO_ANEXO_DESC));
            }
        }

        return code;
    }
}
