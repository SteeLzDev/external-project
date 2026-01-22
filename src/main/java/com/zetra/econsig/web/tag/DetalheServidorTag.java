package com.zetra.econsig.web.tag;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.helper.parametro.ParamCsa;
import com.zetra.econsig.exception.LeilaoSolicitacaoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;

/**
 * <p>Title: DetalheServidorTag</p>
 * <p>Description: Tag para exibição de dados do servidor.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DetalheServidorTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DetalheServidorTag.class);

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private LeilaoSolicitacaoController leilaoSolicitacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ServidorController servidorController;

    // Nome do atributo que contém os dados dao consignação
    protected String name;

    // Nome do atributo de verificação se há ou não validação da data de nascimento na inclusão avançada
    protected String validaAvancadaDataNasc;

    // Escopo do atributo que contém os dados da consignação
    protected String scope;

    // Define se a tag exibirá complementos dos dados pessoais do servidor
    protected String complementos;

    // Nome do atributo que contém a lista de margens a ser exibida
    protected String margem;

    // Nome do atributo que exibe ou não o link para consulta do servidor/registro servidor
    protected boolean exibeIconConSer = false;

    // Nome do atributo que exibe ou não o link para consulta de bloqueios de verba/serviço do servidor
    protected boolean exibeBloqueioSer = false;

    protected static final int NUM_MAX_CHAR_LINHA_TABELA = 50;

    public void setName(String name) {
        this.name = name;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setValidaAvancadaDataNasc(String validaAvancadaDataNasc) {
        this.validaAvancadaDataNasc = validaAvancadaDataNasc;
    }

    public void setComplementos(String complementos) {
        this.complementos = complementos;
    }

    public void setMargem(String margem) {
        this.margem = margem;
    }

    public void setExibeIconConSer(boolean exibeIconConSer) {
        this.exibeIconConSer = exibeIconConSer;
    }

    public void setExibeBloqueioSer(boolean exibeBloqueioSer) {
        this.exibeBloqueioSer = exibeBloqueioSer;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            String csaCodigo = (String) request.getAttribute("csaCodigo");
            String rseCodigo = (String) request.getAttribute("rseCodigo");
            boolean exibeInfBancaria = false;

            if (!TextHelper.isNull(csaCodigo) && !TextHelper.isNull(rseCodigo)) {
                String infBancaria = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_EXIBE_DADOS_BANCARIOS, responsavel);

                List<TransferObject> consignacaoServidor = pesquisarConsignacaoController.pesquisaAutorizacaoRsePorCsa(rseCodigo, csaCodigo, responsavel);

                if (!TextHelper.isNull(infBancaria) && infBancaria.equalsIgnoreCase("S") && consignacaoServidor != null && consignacaoServidor.size() > 0) {
                    exibeInfBancaria = true;
                }
            }

            boolean temValidacaoDataNasc = parametroController.hasValidacaoDataNasc(responsavel);
            Boolean isValidAvancadaDataNasc = null;

            try {
                isValidAvancadaDataNasc = (Boolean) pageContext.getAttribute(validaAvancadaDataNasc, getScopeAsInt(scope));
            } catch (NullPointerException nex) {
                isValidAvancadaDataNasc = null;
            }

            if (!TextHelper.isNull(isValidAvancadaDataNasc)) {
                temValidacaoDataNasc &= isValidAvancadaDataNasc.booleanValue();
            }

            // Obtem os dados do servidor
            CustomTransferObject servidor = (CustomTransferObject) pageContext.getAttribute(name, getScopeAsInt(scope));

            // Obtem a lista de margens
            @SuppressWarnings("unchecked")
            List<MargemTO> margens = (!TextHelper.isNull(margem) ? (List<MargemTO>) pageContext.getAttribute(margem, getScopeAsInt(scope)) : null);

            // Gera o resultado
            pageContext.getOut().print(geraDetalheServidor(servidor, temValidacaoDataNasc, exibeInfBancaria, margens, responsavel));

            return EVAL_PAGE;

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

    protected String geraDetalheServidor(CustomTransferObject servidor, boolean temValidacaoDataNasc, boolean exibeInfBancaria, List<MargemTO> margens, AcessoSistema responsavel) throws ParseException, ServidorControllerException {
        boolean omiteCpfServidor = ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel);

        // Inicia geração do código HTML
        StringBuilder code = new StringBuilder();

        // Pega os valores necessários do Servidor
        String serNome = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_NOME) != null ? servidor.getAttribute(Columns.SER_NOME).toString() : "");
        String serCpf = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_CPF) != null && !omiteCpfServidor ? servidor.getAttribute(Columns.SER_CPF).toString() : "");
        String serDataNasc = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_DATA_NASC) != null ? servidor.getAttribute(Columns.SER_DATA_NASC).toString() : "");
        String serNroIdt = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_NRO_IDT) != null ? servidor.getAttribute(Columns.SER_NRO_IDT).toString() : "");
        String serEmisIdt = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_EMISSOR_IDT) != null ? servidor.getAttribute(Columns.SER_EMISSOR_IDT).toString() : "");
        String serUfIdt = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_UF_IDT) != null ? servidor.getAttribute(Columns.SER_UF_IDT).toString() : "");
        String serDataIdt = "";
        String serQtdFilhos = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_QTD_FILHOS) != null ? servidor.getAttribute(Columns.SER_QTD_FILHOS).toString() : "");
        String serTipoHabitacao = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_THA_CODIGO) != null ? servidor.getAttribute(Columns.SER_THA_CODIGO) : "");
        String serNivelEscolar = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_NES_CODIGO) != null ? servidor.getAttribute(Columns.SER_NES_CODIGO) : "");

        if (!TextHelper.isNull(servidor.getAttribute(Columns.SER_DATA_IDT))) {
            try {
                serDataIdt = DateHelper.reformat(servidor.getAttribute(Columns.SER_DATA_IDT).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern());
            } catch (Exception e) {
                serDataIdt = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_DATA_IDT));
            }
        }
        String serIdt = TextHelper.forHtmlContent(serNroIdt + (!serEmisIdt.equals("") ? " - " + serEmisIdt : "") + (!serUfIdt.equals("") ? " - " + serUfIdt : "") + (!serDataIdt.equals("") ? " - " + serDataIdt : ""));

        // Dados do Registro Servidor
        String rseCodigo = (String) servidor.getAttribute(Columns.RSE_CODIGO);
        String rseMatricula = TextHelper.forHtmlContent(servidor.getAttribute(Columns.RSE_MATRICULA));
        String rseMatriculaInst = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_MATRICULA_INST)) ? servidor.getAttribute(Columns.RSE_MATRICULA_INST).toString() : "");
        String estNome = TextHelper.forHtmlContent((!TextHelper.isNull(servidor.getAttribute(Columns.EST_IDENTIFICADOR)) && !TextHelper.isNull(servidor.getAttribute(Columns.EST_NOME))) ? servidor.getAttribute(Columns.EST_IDENTIFICADOR) + " - " + servidor.getAttribute(Columns.EST_NOME) : "");
        String orgNome = TextHelper.forHtmlContent((!TextHelper.isNull(servidor.getAttribute(Columns.ORG_IDENTIFICADOR)) && !TextHelper.isNull(servidor.getAttribute(Columns.ORG_NOME))) ? servidor.getAttribute(Columns.ORG_IDENTIFICADOR) + " - " + servidor.getAttribute(Columns.ORG_NOME) : "");
        String rseTipo = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_TIPO)) ? servidor.getAttribute(Columns.RSE_TIPO).toString() : "");
        String rseClt = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_CLT)) ? (servidor.getAttribute(Columns.RSE_CLT).toString().equalsIgnoreCase("S") ? ApplicationResourcesHelper.getMessage("rotulo.servidor.clt", responsavel) : "") : "");
        String rseDataAdmissao = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_DATA_ADMISSAO)) ? DateHelper.reformat(servidor.getAttribute(Columns.RSE_DATA_ADMISSAO).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern()) : "");
        String rsePrazo = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_PRAZO)) ? servidor.getAttribute(Columns.RSE_PRAZO).toString() + " " + ApplicationResourcesHelper.getMessage("rotulo.meses", responsavel) : "");
        String rseEstabilizado = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_ESTABILIZADO)) ? (servidor.getAttribute(Columns.RSE_ESTABILIZADO).toString().equalsIgnoreCase("S") ? ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel)) : "");
        String rseDataFimEngajamento = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_DATA_FIM_ENGAJAMENTO)) ? DateHelper.reformat(servidor.getAttribute(Columns.RSE_DATA_FIM_ENGAJAMENTO).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern()) : "");
        String rseDataLimitePermanencia = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_DATA_LIMITE_PERMANENCIA)) ? DateHelper.reformat(servidor.getAttribute(Columns.RSE_DATA_LIMITE_PERMANENCIA).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern()) : "");
        String rseMunicipioLotacao = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_MUNICIPIO_LOTACAO)) ? servidor.getAttribute(Columns.RSE_MUNICIPIO_LOTACAO).toString() : "");
        String rseSalario = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_SALARIO)) ? NumberHelper.format(((BigDecimal) servidor.getAttribute(Columns.RSE_SALARIO)).doubleValue(), NumberHelper.getLang()) : "");
        String rseProventos = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_PROVENTOS)) ? NumberHelper.format(((BigDecimal) servidor.getAttribute(Columns.RSE_PROVENTOS)).doubleValue(), NumberHelper.getLang()) : "");

        // Dados de Conta bancária
        String rseBancoSal = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_BANCO_SAL)) ? (String) servidor.getAttribute(Columns.RSE_BANCO_SAL) : "");
        String rseAgenciaSal = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_AGENCIA_SAL)) ? (String) servidor.getAttribute(Columns.RSE_AGENCIA_SAL) : "");
        String rseAgenciaDvSal = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_AGENCIA_DV_SAL)) ? (String) servidor.getAttribute(Columns.RSE_AGENCIA_DV_SAL) : "");
        String rseContaSal = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_CONTA_SAL)) ? (String) servidor.getAttribute(Columns.RSE_CONTA_SAL) : "");
        String rseContaDvSal = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_CONTA_DV_SAL)) ? (String) servidor.getAttribute(Columns.RSE_CONTA_DV_SAL) : "");

        // Dados das tabelas auxiliares (cargo, padrão, sub-orgao, unidade, capacidade, posto, tipo)
        String crsIdentificador = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.CRS_IDENTIFICADOR)) ? (String) servidor.getAttribute(Columns.CRS_IDENTIFICADOR) : "");
        String prsIdentificador = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.PRS_IDENTIFICADOR)) ? (String) servidor.getAttribute(Columns.PRS_IDENTIFICADOR) : "");
        String sboIdentificador = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.SBO_IDENTIFICADOR)) ? (String) servidor.getAttribute(Columns.SBO_IDENTIFICADOR) : "");
        String uniIdentificador = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.UNI_IDENTIFICADOR)) ? (String) servidor.getAttribute(Columns.UNI_IDENTIFICADOR) : "");
        String crsDescricao = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.CRS_DESCRICAO)) ? servidor.getAttribute(Columns.CRS_DESCRICAO).toString() : "");
        String prsDescricao = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.PRS_DESCRICAO)) ? servidor.getAttribute(Columns.PRS_DESCRICAO).toString() : "");
        String sboDescricao = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.SBO_DESCRICAO)) ? servidor.getAttribute(Columns.SBO_DESCRICAO).toString() : "");
        String uniDescricao = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.UNI_DESCRICAO)) ? servidor.getAttribute(Columns.UNI_DESCRICAO).toString() : "");
        String srsDescricao = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.SRS_DESCRICAO)) ? servidor.getAttribute(Columns.SRS_DESCRICAO).toString() : "");
        String capDescricao = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.CAP_DESCRICAO)) ? servidor.getAttribute(Columns.CAP_DESCRICAO).toString() : "");
        String posDescricao = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.POS_DESCRICAO)) ? servidor.getAttribute(Columns.POS_DESCRICAO).toString() : "");
        String trsDescricao = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.TRS_DESCRICAO)) ? servidor.getAttribute(Columns.TRS_DESCRICAO).toString() : "");
        String vrsDescricao = TextHelper.forHtmlContent(!TextHelper.isNull(servidor.getAttribute(Columns.VRS_DESCRICAO)) ? servidor.getAttribute(Columns.VRS_DESCRICAO).toString() : "");
        // Aplica estilo na descricao da capacidade civil quando for CURATELADO ou TUTELADO
        String capCodigo = !TextHelper.isNull(servidor.getAttribute(Columns.CAP_CODIGO)) ? servidor.getAttribute(Columns.CAP_CODIGO).toString() : "";
        if (!TextHelper.isNull(capDescricao) && !TextHelper.isNull(capCodigo) && (capCodigo.equals(CodedValues.CAP_CURATELADO) || capCodigo.equals(CodedValues.CAP_TUTELADO))) {
            capDescricao = "<font class=\"erro\">" + capDescricao + "</font>";
        }

        if (serNome.trim().length() > 50) {
            serNome = serNome.substring(0, 47) + "...";
        }
        if (estNome != null && estNome.length() > 50) {
            estNome = estNome.substring(0, 47) + "...";
        }
        if (orgNome != null && orgNome.length() > 50) {
            orgNome = orgNome.substring(0, 47) + "...";
        }
        if (!serDataNasc.equals("")) {
            try {
                serDataNasc = (serDataNasc.equals("0000-00-00") || serDataNasc.equals("0001-01-01") || serDataNasc.equals("1753-01-01")) ? "" : DateHelper.reformat(serDataNasc, "yyyy-MM-dd", LocaleHelper.getDatePattern());
            } catch (ParseException pe) {
                DateHelper.parse(serDataNasc, LocaleHelper.getDatePattern());
            }
        }
        if (rseTipo.equals("") && !rsePrazo.equals("")) {
            rseTipo = rsePrazo;
            rsePrazo = "";
        }

        // Gera tabela HTML com os dados do servidor / registro servidor
        if (!TextHelper.isNull(estNome)) {
            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.singular", responsavel), estNome, FieldKeysConstants.DETALHE_SERVIDOR_ESTABELECIMENTO));
        }
        if (!TextHelper.isNull(orgNome)) {
            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel), orgNome, FieldKeysConstants.DETALHE_SERVIDOR_ORGAO));
        }

        if (!sboIdentificador.equals("") && !sboDescricao.equals("")) {
            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.suborgao.singular", responsavel), sboIdentificador + " - " + sboDescricao, FieldKeysConstants.DETALHE_SERVIDOR_SUB_ORGAO));
        }
        if (!uniIdentificador.equals("") && !uniDescricao.equals("")) {
            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.unidade.singular", responsavel), uniIdentificador + " - " + uniDescricao, FieldKeysConstants.DETALHE_SERVIDOR_UNIDADE));
        }

        if (!TextHelper.isNull(rseMatricula) || !TextHelper.isNull(serNome)) {
            String textoMatriculaNome = (!(TextHelper.isNull(rseMatricula)) ? rseMatricula + " - " + serNome : serNome);

            if (exibeIconConSer) {
                // Link para consulta do servidor/registro servidor
                if (responsavel.isCseSupOrg() && !TextHelper.isNull(rseCodigo) && (responsavel.temPermissao(CodedValues.FUN_CONSULTAR_SERVIDOR) || responsavel.temPermissao(CodedValues.FUN_EDT_SERVIDOR))) {
                    textoMatriculaNome = montarLinkConsultarServidor(textoMatriculaNome, SynchronizerToken.updateTokenInURL("../v3/consultarServidor?acao=consultar&RSE_CODIGO=" + rseCodigo + "&detalheAut=S", (HttpServletRequest) pageContext.getRequest()), responsavel);
                }
            }

            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel), textoMatriculaNome, FieldKeysConstants.DETALHE_SERVIDOR_SERVIDOR_MATRICULA));
        }

        if (!rseMatriculaInst.equals("")) {
            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula.inst", responsavel), rseMatriculaInst, FieldKeysConstants.DETALHE_SERVIDOR_MATRICULA_INST));
        }

        if (!serDataNasc.equals("") || !serCpf.equals("") || !serNroIdt.equals("")) {
            String rotuloCpf = ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel);
            String rotuloIdentidade = ApplicationResourcesHelper.getMessage("rotulo.servidor.cartIdentidade", responsavel);
            String rotuloDataNasc = ApplicationResourcesHelper.getMessage("rotulo.servidor.dataNasc", responsavel);

            if (!temValidacaoDataNasc && !serDataNasc.equals("") && !serCpf.equals("") && !serNroIdt.equals("")) {
                code.append(montarLinha(rotuloDataNasc + " - " + rotuloCpf, serDataNasc + " - " + serCpf, FieldKeysConstants.DETALHE_SERVIDOR_DTNASC_CPF_IDENT));
                code.append(montarLinha(rotuloIdentidade, serIdt, FieldKeysConstants.DETALHE_SERVIDOR_DTNASC_CPF_IDENT));
            } else if (!temValidacaoDataNasc && !serDataNasc.equals("") && !serCpf.equals("")) {
                code.append(montarLinha(rotuloDataNasc + " - " + rotuloCpf, serDataNasc + " - " + serCpf, FieldKeysConstants.DETALHE_SERVIDOR_DTNASC_CPF_IDENT));
            } else if (!temValidacaoDataNasc && !serDataNasc.equals("") && !serNroIdt.equals("")) {
                code.append(montarLinha(rotuloDataNasc + " - " + rotuloIdentidade, serDataNasc + " - " + serIdt, FieldKeysConstants.DETALHE_SERVIDOR_DTNASC_CPF_IDENT));
            } else if (!temValidacaoDataNasc && !serDataNasc.equals("")) {
                code.append(montarLinha(rotuloDataNasc, serDataNasc, FieldKeysConstants.DETALHE_SERVIDOR_DTNASC_CPF_IDENT));
            } else if (!serCpf.equals("") && !serNroIdt.equals("")) {
                code.append(montarLinha(rotuloCpf + " - " + rotuloIdentidade, serCpf + " - " + serIdt, FieldKeysConstants.DETALHE_SERVIDOR_DTNASC_CPF_IDENT));
            } else if (!serCpf.equals("")) {
                code.append(montarLinha(rotuloCpf, serCpf, FieldKeysConstants.DETALHE_SERVIDOR_DTNASC_CPF_IDENT));
            } else if (!serNroIdt.equals("")) {
                code.append(montarLinha(rotuloIdentidade, serIdt, FieldKeysConstants.DETALHE_SERVIDOR_DTNASC_CPF_IDENT));
            }
        }

        boolean temModuloLeilao = ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, responsavel);

        if (temModuloLeilao) {
            try {
                boolean exibePosto = ShowFieldHelper.showField(FieldKeysConstants.DETALHE_SERVIDOR_POSTO_GRADUACAO, responsavel);
                if (exibePosto) {
                    String postoGraduacao = (String) servidor.getAttribute(Columns.POS_DESCRICAO);
                    if (!TextHelper.isNull(postoGraduacao)) {
                        code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.posto", responsavel), postoGraduacao, FieldKeysConstants.DETALHE_SERVIDOR_POSTO_GRADUACAO));
                    }
                }
            } catch (ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            try {
                boolean exibePerfil = ShowFieldHelper.showField(FieldKeysConstants.DETALHE_SERVIDOR_PERFIL_PRELIMINAR, responsavel);
                if (exibePerfil) {
                    String rsePontuacao = !TextHelper.isNull(servidor.getAttribute(Columns.RSE_PONTUACAO)) ? servidor.getAttribute(Columns.RSE_PONTUACAO).toString() : null;
                    int pontuacao;
                    if (rsePontuacao == null) {
                        pontuacao = -1;
                        rsePontuacao = ApplicationResourcesHelper.getMessage("rotulo.servidor.pontuacao.null", responsavel);
                    } else {
                        try {
                            pontuacao = Integer.valueOf(rsePontuacao);
                        } catch (NumberFormatException nfe) {
                            pontuacao = -1;
                            rsePontuacao = null;
                            LOG.error(nfe.getMessage(), nfe);
                        }
                    }
                    if (pontuacao >= 0 && pontuacao <= 20) {
                        rsePontuacao = ApplicationResourcesHelper.getMessage("rotulo.servidor.pontuacao.0.20", responsavel);
                    } else if (pontuacao >= 21 && pontuacao <= 40) {
                        rsePontuacao = ApplicationResourcesHelper.getMessage("rotulo.servidor.pontuacao.21.40", responsavel);
                    } else if (pontuacao >= 41 && pontuacao <= 60) {
                        rsePontuacao = ApplicationResourcesHelper.getMessage("rotulo.servidor.pontuacao.41.60", responsavel);
                    } else if (pontuacao >= 61 && pontuacao <= 80) {
                        rsePontuacao = ApplicationResourcesHelper.getMessage("rotulo.servidor.pontuacao.61.80", responsavel);
                    } else if (pontuacao >= 81) {
                        rsePontuacao = ApplicationResourcesHelper.getMessage("rotulo.servidor.pontuacao.81.100", responsavel);
                    }
                    if (!TextHelper.isNull(rsePontuacao)) {
                        code.append(montarLinhaTooltip(ApplicationResourcesHelper.getMessage("rotulo.servidor.pontuacao", responsavel), rsePontuacao, FieldKeysConstants.DETALHE_SERVIDOR_PERFIL_PRELIMINAR, ApplicationResourcesHelper.getMessage("mensagem.servidor.pontuacao.forma.calculo", responsavel)));
                    }
                }
            } catch (ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            try {
                boolean exibeQtdContratosIncluidos6Meses = ShowFieldHelper.showField(FieldKeysConstants.DETALHE_SERVIDOR_QTD_CONTRATOS_INCLUIDOS_ULTIMOS_6_MESES, responsavel);
                if (exibeQtdContratosIncluidos6Meses) {
                    int qtdeContratos;
                    try {
                        qtdeContratos = leilaoSolicitacaoController.qtdeContratos(rseCodigo, responsavel);
                    } catch (LeilaoSolicitacaoControllerException e) {
                        qtdeContratos = 0;
                    }
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.acompanhamento.leilao.qtde.ade.incluidos", responsavel), qtdeContratos, FieldKeysConstants.DETALHE_SERVIDOR_QTD_CONTRATOS_INCLUIDOS_ULTIMOS_6_MESES));
                }
            } catch (ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            try {
                boolean exibeQtdSolicitacaoLeilao6Meses = ShowFieldHelper.showField(FieldKeysConstants.DETALHE_SERVIDOR_QTD_SOLICITACAO_LEILAO_ULTIMOS_6_MESES, responsavel);
                if (exibeQtdSolicitacaoLeilao6Meses) {
                    int qtdeSolicitacaoLeilao;
                    try {
                        qtdeSolicitacaoLeilao = leilaoSolicitacaoController.qtdeSolicitacaoLeilao(rseCodigo, responsavel);
                    } catch (LeilaoSolicitacaoControllerException e) {
                        qtdeSolicitacaoLeilao = 0;
                    }
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.acompanhamento.leilao.qtde.solicitacao.leilao", responsavel), qtdeSolicitacaoLeilao, FieldKeysConstants.DETALHE_SERVIDOR_QTD_SOLICITACAO_LEILAO_ULTIMOS_6_MESES));
                }
            } catch (ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            try {
                boolean exibeQtdLeiloesConcretizados6Meses = ShowFieldHelper.showField(FieldKeysConstants.DETALHE_SERVIDOR_QTD_LEILOES_CONCRETIZADOS_ULTIMOS_6_MESES, responsavel);
                if (exibeQtdLeiloesConcretizados6Meses) {
                    int qtdeLeilaoConcretizado;
                    try {
                        qtdeLeilaoConcretizado = leilaoSolicitacaoController.qtdeLeilaoConcretizado(rseCodigo, responsavel);
                    } catch (LeilaoSolicitacaoControllerException e) {
                        qtdeLeilaoConcretizado = 0;
                    }
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.acompanhamento.leilao.qtde.solicitacao.leilao.concretizadas", responsavel), qtdeLeilaoConcretizado, FieldKeysConstants.DETALHE_SERVIDOR_QTD_LEILOES_CONCRETIZADOS_ULTIMOS_6_MESES));
                }
            } catch (ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        boolean temComplementos = (!TextHelper.isNull(complementos)) ? Boolean.valueOf(complementos).booleanValue() : false;
        if (temComplementos) {
            String serNomePai = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_NOME_PAI) != null ? servidor.getAttribute(Columns.SER_NOME_PAI).toString() : "");
            String serNomeMae = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_NOME_MAE) != null ? servidor.getAttribute(Columns.SER_NOME_MAE).toString() : "");
            String serSexo = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_SEXO) != null ? servidor.getAttribute(Columns.SER_SEXO).toString() : "");
            String serEstCivil = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_EST_CIVIL) != null ? servidor.getAttribute(Columns.SER_EST_CIVIL).toString() : "");
            String serNacionalidade = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_NACIONALIDADE) != null ? servidor.getAttribute(Columns.SER_NACIONALIDADE).toString() : "");
            String serCartProf = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_CART_PROF) != null ? servidor.getAttribute(Columns.SER_CART_PROF).toString() : "");
            String serPis = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_PIS) != null ? servidor.getAttribute(Columns.SER_PIS).toString() : "");
            String serEnd = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_END) != null ? servidor.getAttribute(Columns.SER_END).toString() : "");
            String serCompl = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_COMPL) != null ? servidor.getAttribute(Columns.SER_COMPL).toString() : "");
            String serBairro = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_BAIRRO) != null ? servidor.getAttribute(Columns.SER_BAIRRO).toString() : "");
            String serCidade = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_CIDADE) != null ? servidor.getAttribute(Columns.SER_CIDADE).toString() : "");
            String serUf = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_UF) != null ? servidor.getAttribute(Columns.SER_UF).toString() : "");
            String serTel = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_TEL) != null ? servidor.getAttribute(Columns.SER_TEL).toString() : "");
            String serEmail = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_EMAIL) != null ? servidor.getAttribute(Columns.SER_EMAIL).toString() : "");
            String rseMunLotacao = TextHelper.forHtmlContent(servidor.getAttribute(Columns.RSE_MUNICIPIO_LOTACAO) != null ? servidor.getAttribute(Columns.RSE_MUNICIPIO_LOTACAO).toString() : "");

            if (!TextHelper.isNull(serNomePai)) {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.nomePai", responsavel), serNomePai, FieldKeysConstants.DETALHE_SERVIDOR_NOME_PAI));
            }
            if (!TextHelper.isNull(serNomeMae)) {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.nomeMae", responsavel), serNomeMae, FieldKeysConstants.DETALHE_SERVIDOR_NOME_MAE));
            }
            if (!TextHelper.isNull(serSexo)) {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.sexo", responsavel), serSexo.toUpperCase().equals("M") ? ApplicationResourcesHelper.getMessage("rotulo.servidor.sexo.masculino", responsavel) : serSexo.toUpperCase().equals("F") ? ApplicationResourcesHelper.getMessage("rotulo.servidor.sexo.feminino", responsavel) : serSexo, FieldKeysConstants.DETALHE_SERVIDOR_SEXO));
            }
            if (!TextHelper.isNull(serEstCivil)) {
                String estCvlDesc = servidorController.getEstCivil(serEstCivil, responsavel);
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.estadoCivil", responsavel), !TextHelper.isNull(estCvlDesc) ? estCvlDesc : serEstCivil, FieldKeysConstants.DETALHE_SERVIDOR_ESTADO_CIVIL));
            }
            if (!TextHelper.isNull(serNacionalidade)) {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.nacionalidade", responsavel), serNacionalidade, FieldKeysConstants.DETALHE_SERVIDOR_NACIONALIDADE));
            }
            if (!TextHelper.isNull(serCartProf)) {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.cartTrabalho", responsavel), serCartProf, FieldKeysConstants.DETALHE_SERVIDOR_NUM_CART_PROF));
            }
            if (!TextHelper.isNull(serPis)) {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.pis", responsavel), serPis, FieldKeysConstants.DETALHE_SERVIDOR_NUM_PIS));
            }
            if (!TextHelper.isNull(serEnd)) {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.endereco.logradouro", responsavel), serEnd, FieldKeysConstants.DETALHE_SERVIDOR_LOGRADOURO));
            }
            if (!TextHelper.isNull(serCompl)) {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.endereco.numero", responsavel) + " - " + ApplicationResourcesHelper.getMessage("rotulo.endereco.complemento", responsavel), serCompl, FieldKeysConstants.DETALHE_SERVIDOR_COMPLEMENTO));
            }
            if (!TextHelper.isNull(serBairro)) {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.endereco.bairro", responsavel), serBairro, FieldKeysConstants.DETALHE_SERVIDOR_BAIRRO));
            }
            if (!TextHelper.isNull(serCidade)) {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.endereco.cidade", responsavel), serCidade, FieldKeysConstants.DETALHE_SERVIDOR_CIDADE));
            }
            if (!TextHelper.isNull(serUf)) {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.endereco.estado", responsavel), serUf, FieldKeysConstants.DETALHE_SERVIDOR_UF));
            }
            if (!TextHelper.isNull(serTel)) {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.telefone", responsavel), serTel, FieldKeysConstants.DETALHE_SERVIDOR_TELEFONE));
            }
            if (!TextHelper.isNull(serEmail)) {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.email", responsavel), serEmail, FieldKeysConstants.DETALHE_SERVIDOR_EMAIL));
            }
            if (!TextHelper.isNull(rseMunLotacao)) {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.municipioLotacao", responsavel), rseMunLotacao, FieldKeysConstants.DETALHE_SERVIDOR_MUNICIPIO_LOTACAO));
            }
        }

        String separador = " - ";
        if (!rsePrazo.equals("")) {
            rsePrazo = separador.concat(rsePrazo);
        }
        if (!rseClt.equals("")) {
            rseClt = separador.concat(rseClt);
        }
        if (!rseTipo.trim().equals("")) {
            rseTipo = rseTipo + separador + srsDescricao;
        } else {
            rseTipo = srsDescricao;
        }
        if (!rseDataAdmissao.equals("") || !rseTipo.equals("") || !rseClt.equals("") || !rsePrazo.equals("")) {
            String rotuloCategoria = ApplicationResourcesHelper.getMessage("rotulo.servidor.categoria", responsavel);
            String rotuloDataAdmissao = ApplicationResourcesHelper.getMessage("rotulo.servidor.dataAdmissao", responsavel);

            if (!rseDataAdmissao.equals("")) {
                if (!rseTipo.equals("")) {
                    rseTipo = separador.concat(rseTipo);
                    code.append(montarLinha(rotuloDataAdmissao + " - " + rotuloCategoria, rseDataAdmissao + rseTipo + rseClt + rsePrazo, FieldKeysConstants.DETALHE_SERVIDOR_DTADMISS_CATEGORIA));
                } else {
                    code.append(montarLinha(rotuloDataAdmissao, rseDataAdmissao + rseClt + rsePrazo, FieldKeysConstants.DETALHE_SERVIDOR_DTADMISS_CATEGORIA));
                }
            } else if (!rseTipo.equals("")) {
                code.append(montarLinha(rotuloCategoria, rseTipo + rseClt + rsePrazo, FieldKeysConstants.DETALHE_SERVIDOR_DTADMISS_CATEGORIA));
            }
        }

        if (!TextHelper.isNull(serQtdFilhos)) {
            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.quantidade.filhos", responsavel), serQtdFilhos, FieldKeysConstants.DETALHE_SERVIDOR_QTD_FILHOS));
        }

        try {
            if (!TextHelper.isNull(serNivelEscolar) && !serNivelEscolar.equals("") && ShowFieldHelper.showField(FieldKeysConstants.DETALHE_SERVIDOR_NIVEL_ESCOLARIDADE, responsavel)) {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.nivel.escolaridade", responsavel), servidorController.getNivelEscolaridade(serNivelEscolar, responsavel), FieldKeysConstants.DETALHE_SERVIDOR_NIVEL_ESCOLARIDADE));
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        try {
            if (!TextHelper.isNull(serTipoHabitacao) && !serTipoHabitacao.equals("") && ShowFieldHelper.showField(FieldKeysConstants.DETALHE_SERVIDOR_TIPO_HABITACAO, responsavel)) {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.tipo.habitacao", responsavel), servidorController.getTipoHabitacao(serTipoHabitacao, responsavel), FieldKeysConstants.DETALHE_SERVIDOR_TIPO_HABITACAO));
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        if (!TextHelper.isNull(rseMunicipioLotacao)) {
            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.municipioLotacao", responsavel), rseMunicipioLotacao, FieldKeysConstants.DETALHE_SERVIDOR_MUNICIPIO_LOTACAO));
        }
        if (!crsIdentificador.equals("") && !crsDescricao.equals("")) {
            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.cargo", responsavel), crsIdentificador + " - " + crsDescricao, FieldKeysConstants.DETALHE_SERVIDOR_CARGO));
        }
        if (!prsIdentificador.equals("") && !prsDescricao.equals("")) {
            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.padrao", responsavel), prsIdentificador + " - " + prsDescricao, FieldKeysConstants.DETALHE_SERVIDOR_PADRAO));
        }

        if (!trsDescricao.equals("") || !posDescricao.equals("") || !capDescricao.equals("")) {
            String rotuloTipo = ApplicationResourcesHelper.getMessage("rotulo.servidor.tipo", responsavel);
            String rotuloPosto = ApplicationResourcesHelper.getMessage("rotulo.servidor.posto", responsavel);
            String rotuloCapacidade = ApplicationResourcesHelper.getMessage("rotulo.servidor.capacidadeCivil", responsavel);

            if (!trsDescricao.equals("") && !posDescricao.equals("") && !capDescricao.equals("")) {
                code.append(montarLinha(rotuloTipo + " - " + rotuloPosto + " - " + rotuloCapacidade, trsDescricao + " - " + posDescricao + " - " + capDescricao, FieldKeysConstants.DETALHE_SERVIDOR_TIPO_POSTO_CAP));
            } else if (!trsDescricao.equals("") && !posDescricao.equals("")) {
                code.append(montarLinha(rotuloTipo + " - " + rotuloPosto, trsDescricao + " - " + posDescricao, FieldKeysConstants.DETALHE_SERVIDOR_TIPO_POSTO_CAP));
            } else if (!trsDescricao.equals("") && !capDescricao.equals("")) {
                code.append(montarLinha(rotuloTipo + " - " + rotuloCapacidade, trsDescricao + " - " + capDescricao, FieldKeysConstants.DETALHE_SERVIDOR_TIPO_POSTO_CAP));
            } else if (!posDescricao.equals("") && !capDescricao.equals("")) {
                code.append(montarLinha(rotuloPosto + " - " + rotuloCapacidade, posDescricao + " - " + capDescricao, FieldKeysConstants.DETALHE_SERVIDOR_TIPO_POSTO_CAP));
            } else if (!trsDescricao.equals("")) {
                code.append(montarLinha(rotuloTipo, trsDescricao, FieldKeysConstants.DETALHE_SERVIDOR_TIPO_POSTO_CAP));
            } else if (!posDescricao.equals("")) {
                code.append(montarLinha(rotuloPosto, posDescricao, FieldKeysConstants.DETALHE_SERVIDOR_TIPO_POSTO_CAP));
            } else if (!capDescricao.equals("")) {
                code.append(montarLinha(rotuloCapacidade, capDescricao, FieldKeysConstants.DETALHE_SERVIDOR_TIPO_POSTO_CAP));
            }
        }

        if (!rseEstabilizado.equals("")) {
            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.estabilizado", responsavel), rseEstabilizado, FieldKeysConstants.DETALHE_SERVIDOR_ESTABILIZADO));
        }
        if (!rseDataFimEngajamento.equals("")) {
            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.engajado", responsavel), rseDataFimEngajamento, FieldKeysConstants.DETALHE_SERVIDOR_DATA_FIM_ENGAJAMENTO));
        }
        if (!rseDataLimitePermanencia.equals("")) {
            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.dataLimitePermanencia", responsavel), rseDataLimitePermanencia, FieldKeysConstants.DETALHE_SERVIDOR_DATA_LIMITE_PERMANENCIA));
        }

        // Verifica se tem permissão para exibir os dados bancários
        boolean exibeLinhaDtInclusaoMatricula = false;
        try {
            exibeLinhaDtInclusaoMatricula = ShowFieldHelper.showField(FieldKeysConstants.DETALHE_SERVIDOR_DATA_INCLUSAO_MATRICULA, responsavel);
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        if (exibeLinhaDtInclusaoMatricula && !TextHelper.isNull(rseCodigo)) {
            try {
                Date orsData = servidorController.obtemDataInclusaoRegistroServidor(rseCodigo, responsavel);
                if (orsData != null) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.data.inclusao.matricula", responsavel), DateHelper.format(orsData, LocaleHelper.getDatePattern()), FieldKeysConstants.DETALHE_SERVIDOR_DATA_INCLUSAO_MATRICULA));
                }
            } catch (ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        // Imprime os dados bancários
        if (exibeInfBancaria) {
            boolean temBanco = (!rseBancoSal.equals(""));
            boolean temAgenc = (!rseAgenciaSal.equals(""));
            boolean temConta = (!rseContaSal.equals(""));

            String infBancariasBanco = (temBanco) ? ApplicationResourcesHelper.getMessage("rotulo.servidor.informacoesbancarias.banco", responsavel) + ": " + rseBancoSal : "";
            String infBancariasAgenc = (temAgenc) ? ApplicationResourcesHelper.getMessage("rotulo.servidor.informacoesbancarias.agencia", responsavel) + ": " + rseAgenciaSal + (!rseAgenciaDvSal.equals("") ? "-" + rseAgenciaDvSal : "") : "";
            String infBancariasConta = (temConta) ? ApplicationResourcesHelper.getMessage("rotulo.servidor.informacoesbancarias.conta", responsavel) + ": " + rseContaSal + (!rseContaDvSal.equals("") ? "-" + rseContaDvSal : "") : "";

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

            if (responsavel.getFunCodigo().equals(CodedValues.FUN_RES_MARGEM)) {
                if (!TextHelper.isNull(infBancarias)) {
                    code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.informacoesbancarias", responsavel), infBancarias, FieldKeysConstants.DETALHE_SERVIDOR_DADOS_BANCARIOS));
                }
            } else {
                code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.informacoesbancarias", responsavel), infBancarias, FieldKeysConstants.DETALHE_SERVIDOR_DADOS_BANCARIOS));
            }
        }

        String rotuloMoeda = ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel);
        if (!TextHelper.isNull(rseSalario)) {
            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.salario", responsavel) + " (" + rotuloMoeda + ")", rseSalario, FieldKeysConstants.DETALHE_SERVIDOR_SALARIO));
        }
        if (!TextHelper.isNull(rseProventos)) {
            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.proventos", responsavel) + " (" + rotuloMoeda + ")", rseProventos, FieldKeysConstants.DETALHE_SERVIDOR_PROVENTOS));
        }

        String rseDataSaida = (servidor.getAttribute(Columns.RSE_DATA_SAIDA) != null ? servidor.getAttribute(Columns.RSE_DATA_SAIDA).toString() : "");
        if (!TextHelper.isNull(rseDataSaida)) {
            rseDataSaida = DateHelper.reformat(rseDataSaida, "yyyy-MM-dd", LocaleHelper.getDatePattern());
            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.data.saida", responsavel), rseDataSaida, FieldKeysConstants.DETALHE_SERVIDOR_DATA_SAIDA));
        }

        String rseDataUltSalario = (servidor.getAttribute(Columns.RSE_DATA_ULT_SALARIO) != null ? servidor.getAttribute(Columns.RSE_DATA_ULT_SALARIO).toString() : "");
        if (!TextHelper.isNull(rseDataUltSalario)) {
            rseDataUltSalario = DateHelper.reformat(rseDataUltSalario, "yyyy-MM-dd", LocaleHelper.getDatePattern());
            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.data.ult.salario", responsavel), rseDataUltSalario, FieldKeysConstants.DETALHE_SERVIDOR_DATA_ULT_SALARIO));
        }

        String rsePedidoDemissao = (servidor.getAttribute(Columns.RSE_PEDIDO_DEMISSAO) != null ? servidor.getAttribute(Columns.RSE_PEDIDO_DEMISSAO).toString() : "");
        if (!TextHelper.isNull(rsePedidoDemissao)) {
            rsePedidoDemissao = (rsePedidoDemissao.equals("S") ? ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel));
            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.pedido.demissao", responsavel), rsePedidoDemissao, FieldKeysConstants.DETALHE_SERVIDOR_PEDIDO_DEMISSAO));
        }

        String rseDataRetorno = (servidor.getAttribute(Columns.RSE_DATA_RETORNO) != null ? servidor.getAttribute(Columns.RSE_DATA_RETORNO).toString() : "");
        if (!TextHelper.isNull(rseDataRetorno)) {
            rseDataRetorno = DateHelper.reformat(rseDataRetorno, "yyyy-MM-dd", LocaleHelper.getDatePattern());
            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.data.retorno", responsavel), rseDataRetorno, FieldKeysConstants.DETALHE_SERVIDOR_DATA_RETORNO));
        }

        String srsCodigo = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SRS_CODIGO) != null ? servidor.getAttribute(Columns.SRS_CODIGO).toString() : null);
        // possibilidade de exibir observação do registro servidor equivalente apenas se este estiver com status bloqueado
        if (!TextHelper.isNull(srsCodigo) && CodedValues.SRS_BLOQUEADOS.contains(srsCodigo)) {
            String rseObs = TextHelper.forHtmlContent(servidor.getAttribute(Columns.RSE_OBS) != null ? servidor.getAttribute(Columns.RSE_OBS).toString() : "");

            // insere quebras de linha caso observação seja muito grande
            if (!TextHelper.isNull(rseObs)) {
                String valorText = rseObs.toString();
                double linhas = valorText.length() / NUM_MAX_CHAR_LINHA_TABELA;
                int totalLinhas = Double.valueOf(Math.floor(linhas)).intValue();

                if (totalLinhas > 0) {
                    String[] linha = new String[totalLinhas];
                    for (int i = 0; i < linha.length; i++) {
                        int offset = (i + 1) * NUM_MAX_CHAR_LINHA_TABELA;
                        linha[i] = valorText.substring((i * NUM_MAX_CHAR_LINHA_TABELA), (offset < valorText.length()) ? offset : valorText.length());
                    }

                    StringBuilder concat = new StringBuilder();
                    for (String element : linha) {
                        if (!TextHelper.isNull(element)) {
                            concat.append(element).append("<br>");
                        }
                    }

                    rseObs = concat.toString();
                }
            }

            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.obs", responsavel), rseObs, FieldKeysConstants.DETALHE_SERVIDOR_OBSERVACAO));
        }

        // Recupera a lista de tda's.
        String serCodigo = TextHelper.forHtmlContent(servidor.getAttribute(Columns.SER_CODIGO) != null ? servidor.getAttribute(Columns.SER_CODIGO).toString() : null);

        List<TransferObject> lstSerDados = servidorController.lstDadosServidor(AcaoTipoDadoAdicionalEnum.CONSULTA, VisibilidadeTipoDadoAdicionalEnum.WEB, serCodigo, responsavel);

        for (TransferObject serDado : lstSerDados) {
            if ((serDado.getAttribute(Columns.TDA_TEN_CODIGO).equals(Log.SERVIDOR))) {
                String valor = (String) serDado.getAttribute(Columns.DAS_VALOR);
                code.append(montarLinha((String) serDado.getAttribute(Columns.TDA_DESCRICAO), TextHelper.forHtmlContent(valor) == null ? "" : TextHelper.forHtmlContent(valor), FieldKeysConstants.DETALHE_SERVIDOR_DADO_ADICIONAL));
            }
        }

        // Monta listagem das margens do servidor
        if (margens != null && margens.size() > 0) {
            String tipoVlrMargem = null;
            String vlrMargem = null;
            String obsMargem = null;
            String vlrUsado = null;
            String vlrRestante = null;
            for (MargemTO margem : margens) {
                if (margem.getMrsMargemRest() != null) {
                    // Se diferente de NULL, significa que exibe a margem para o usuário
                    tipoVlrMargem = ParamSvcTO.getDescricaoTpsTipoVlr(margem.getMarTipoVlr() != null ? margem.getMarTipoVlr().toString() : CodedValues.TIPO_VLR_FIXO);
                    vlrMargem = NumberHelper.format(margem.getMrsMargemRest().doubleValue(), NumberHelper.getLang());
                    obsMargem = (!TextHelper.isNull(margem.getObservacao()) ? " (" + margem.getObservacao() + ")" : "");
                    vlrRestante = NumberHelper.format(margem.getMrsMargem().doubleValue(), NumberHelper.getLang());
                    vlrUsado = NumberHelper.format(margem.getMrsMargemUsada().doubleValue(), NumberHelper.getLang());
                    if (retornarVlrFolhaVlrUsadoMargem(responsavel)) {
                        code.append(montarLinha(margem.getMarDescricao(), "<span class=\"Rotulo\">" + TextHelper.forHtmlContent(tipoVlrMargem) + "</span>&nbsp;" + TextHelper.forHtmlContent(vlrMargem) + TextHelper.forHtmlContent(obsMargem) + " <a class=\"btn-info\" href=\"#no-back\" onClick=\"modalMargemDetalhe('" + tipoVlrMargem + vlrUsado + "', '" + tipoVlrMargem + vlrRestante + "', '" + margem.getMarDescricao() + "');\"><i class=\"fa fa-exclamation-circle\" aria-hidden=\"true\"></i></a>", FieldKeysConstants.DETALHE_SERVIDOR_MARGEM));
                    } else {
                        code.append(montarLinha(margem.getMarDescricao(), "<span class=\"Rotulo\">" + TextHelper.forHtmlContent(tipoVlrMargem) + "</span>&nbsp;" + TextHelper.forHtmlContent(vlrMargem) + TextHelper.forHtmlContent(obsMargem), FieldKeysConstants.DETALHE_SERVIDOR_MARGEM));
                    }
                }
            }
        }

        boolean exibeFiltroVinculo = (ParamSist.paramEquals(CodedValues.TPC_FILTRO_VINCULO_CONSULTA_MARGEM, CodedValues.TPC_SIM, responsavel) && responsavel.temPermissao(CodedValues.FUN_CONS_MARGEM));
        if (exibeFiltroVinculo && !vrsDescricao.isEmpty()) {
            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.servidor.vinculo", responsavel), vrsDescricao, FieldKeysConstants.DETALHE_SERVIDOR_VINCULO));
        }

        if (exibeBloqueioSer) {
            String linkBloqServidor = montarLinkConsultarBloqueiosServidor(SynchronizerToken.updateTokenInURL("../v3/consultarConveniosBloqueados?acao=iniciar&RSE_CODIGO=" + rseCodigo, (HttpServletRequest) pageContext.getRequest()), responsavel);
            code.append(montarLinha(ApplicationResourcesHelper.getMessage("rotulo.convenios.bloq.acao", responsavel), linkBloqServidor, FieldKeysConstants.DETALHE_SERVIDOR_BLOQEIO_VERBAS_SERVICOS));
        }

        return code.toString();
    }

    protected String montarLinkConsultarServidor(String textoMatriculaNome, String link, AcessoSistema responsavel) {
        String msgAlt = ApplicationResourcesHelper.getMessage("mensagem.consultar.servidor.clique.aqui", responsavel);
        return textoMatriculaNome + "&nbsp;<a href=\"#no-back\" onClick=\"postData('" + link + "')\"><img src=\"../img/icones/usuario.gif\" id=\"btnEdtServidor\" alt=\"" + msgAlt + "\" title=\"" + msgAlt + "\" border=\"0\" width=\"9\" height=\"18\"></a>";
    }

    protected String montarLinkConsultarBloqueiosServidor(String link, AcessoSistema responsavel) {
        String msgAlt = ApplicationResourcesHelper.getMessage("mensagem.convenios.bloq.clique.aqui", responsavel);
        return "<a href=\"#no-back\" onClick=\"postData('" + link + "')\"><img src=\"../img/icones/convenio.gif\" id=\"btnBloqueioVerbaServidor\" alt=\"" + msgAlt + "\" title=\"" + msgAlt + "\" border=\"0\" width=\"9\" height=\"18\"></a>";
    }

    private static boolean retornarVlrFolhaVlrUsadoMargem(AcessoSistema responsavel) {
        if (responsavel != null) {
            if (responsavel.isCseSupOrg()) {
                return true;
            } else if (responsavel.isCsaCor()) {
                final String param = ParamCsa.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_RETORNA_VLR_FOLHA_E_VLR_USADO_CONSULTA_MARGEM_SOAP_WEB, responsavel);
                return CodedValues.TPA_SIM.equals(param);
            }
        }
        return false;
    }
}

