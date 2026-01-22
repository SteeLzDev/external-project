package com.zetra.econsig.dto.web;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.RiscoRegistroServidorEnum;
import com.zetra.econsig.values.StatusSolicitacaoEnum;

/**
 * <p>Title: AcompanharLeilaoModel</p>
 * <p>Description: Model para exibição da tela de acompanhamento de leilão reverso.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: fagner.luiz $
 * $Revision: 28168 $
 * $Date: 2019-11-05 17:02:21 -0300 (ter, 05 nov 2019) $
 */
public class AcompanharLeilaoModel {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AcompanharLeilaoModel.class);

    private String filtro;

    private String tipo;

    private String dataAberturaIni;

    private String dataAberturaFim;

    private String rsePontuacaoFiltro;

    private boolean temRiscoPelaCsa;

    private String arrRiscoFiltro;

    private String rseMargemLivreFiltro;

    private List<TransferObject> lstResultado;

    private String linkPaginacao;

    private int colspan;

    private boolean podeEdtProposta;

    private boolean podecadastrarRisco;

    private boolean podeConsultarAde;

    private List<TransferObject> postos;

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDataAberturaIni() {
        return dataAberturaIni;
    }

    public void setDataAberturaIni(String dataAberturaIni) {
        this.dataAberturaIni = dataAberturaIni;
    }

    public String getDataAberturaFim() {
        return dataAberturaFim;
    }

    public void setDataAberturaFim(String dataAberturaFim) {
        this.dataAberturaFim = dataAberturaFim;
    }

    public String getRsePontuacaoFiltro() {
        return rsePontuacaoFiltro;
    }

    public void setRsePontuacaoFiltro(String rsePontuacaoFiltro) {
        this.rsePontuacaoFiltro = rsePontuacaoFiltro;
    }

    public boolean isTemRiscoPelaCsa() {
        return temRiscoPelaCsa;
    }

    public void setTemRiscoPelaCsa(boolean temRiscoPelaCsa) {
        this.temRiscoPelaCsa = temRiscoPelaCsa;
    }

    public String getArrRiscoFiltro() {
        return arrRiscoFiltro;
    }

    public void setArrRiscoFiltro(String arrRiscoFiltro) {
        this.arrRiscoFiltro = arrRiscoFiltro;
    }

    public String getRseMargemLivreFiltro() {
        return rseMargemLivreFiltro;
    }

    public void setRseMargemLivreFiltro(String rseMargemLivreFiltro) {
        this.rseMargemLivreFiltro = rseMargemLivreFiltro;
    }

    public List<TransferObject> getLstResultado() {
        return lstResultado;
    }

    public void setLstResultado(List<TransferObject> lstResultado) {
        this.lstResultado = lstResultado;
    }

    public String getLinkPaginacao() {
        return linkPaginacao;
    }

    public void setLinkPaginacao(String linkPaginacao) {
        this.linkPaginacao = linkPaginacao;
    }

    public int getColspan() {
        return colspan;
    }

    public void setColspan(int colspan) {
        this.colspan = colspan;
    }

    public boolean isPodeEdtProposta() {
        return podeEdtProposta;
    }

    public void setPodeEdtProposta(boolean podeEdtProposta) {
        this.podeEdtProposta = podeEdtProposta;
    }

    public boolean isPodecadastrarRisco() {
        return podecadastrarRisco;
    }

    public void setPodecadastrarRisco(boolean podecadastrarRisco) {
        this.podecadastrarRisco = podecadastrarRisco;
    }

    public boolean isPodeConsultarAde() {
        return podeConsultarAde;
    }

    public void setPodeConsultarAde(boolean podeConsultarAde) {
        this.podeConsultarAde = podeConsultarAde;
    }

    public List<TransferObject> getPostos() {
        return postos;
    }

    public void setPostos(List<TransferObject> postos) {
        this.postos = postos;
    }

    public LinhaAcompanhamentoLeilao gerarLinhaAcompanhamento(TransferObject registroLeilao, AcessoSistema responsavel) {
        LinhaAcompanhamentoLeilao linha = new LinhaAcompanhamentoLeilao();

        linha.textoStatusLeilao = "";
        linha.adeCodigo = registroLeilao.getAttribute(Columns.ADE_CODIGO).toString();
        linha.adeNumero = registroLeilao.getAttribute(Columns.ADE_NUMERO).toString();
        linha.adeTipoVlr = registroLeilao.getAttribute(Columns.ADE_TIPO_VLR).toString();
        linha.adeVlr = NumberHelper.format(((BigDecimal) registroLeilao.getAttribute(Columns.ADE_VLR)).doubleValue(), NumberHelper.getLang());
        linha.adeVlrLiberado = NumberHelper.format(((BigDecimal) registroLeilao.getAttribute(Columns.ADE_VLR_LIQUIDO)).doubleValue(), NumberHelper.getLang());
        linha.adePrazo = registroLeilao.getAttribute(Columns.ADE_PRAZO).toString();
        linha.rsePontuacao = !TextHelper.isNull(registroLeilao.getAttribute(Columns.RSE_PONTUACAO)) ? registroLeilao.getAttribute(Columns.RSE_PONTUACAO).toString() : "";
        linha.rseCodigo = registroLeilao.getAttribute(Columns.RSE_CODIGO).toString();
        linha.arrRisco = !TextHelper.isNull(registroLeilao.getAttribute(Columns.ARR_RISCO)) ? registroLeilao.getAttribute(Columns.ARR_RISCO).toString() : "";
        linha.statusSolicitacaoLeilao = (String) registroLeilao.getAttribute(Columns.SSO_CODIGO);

        if (linha.statusSolicitacaoLeilao.equals(StatusSolicitacaoEnum.PENDENTE.getCodigo())) {
            linha.textoStatusLeilao = ApplicationResourcesHelper.getMessage("mensagem.status.leilao.aguardando.aprovacao", responsavel);
        } else if (linha.statusSolicitacaoLeilao.equals(StatusSolicitacaoEnum.FINALIZADA.getCodigo())) {
            linha.textoStatusLeilao = ApplicationResourcesHelper.getMessage("mensagem.status.leilao.aprovada", responsavel);
        } else if (linha.statusSolicitacaoLeilao.equals(StatusSolicitacaoEnum.EXPIRADA.getCodigo())) {
            linha.textoStatusLeilao = ApplicationResourcesHelper.getMessage("mensagem.status.leilao.rejeitada", responsavel);
        }

        int pontuacao;
        if (!TextHelper.isNum(linha.rsePontuacao)) {
            pontuacao = -1;
            linha.rsePontuacao = ApplicationResourcesHelper.getMessage("rotulo.servidor.pontuacao.null", responsavel);
        } else {
            pontuacao = Integer.valueOf(linha.rsePontuacao);
        }
        if (pontuacao >= 0 && pontuacao <= 20) {
            linha.rsePontuacao = ApplicationResourcesHelper.getMessage("rotulo.servidor.pontuacao.0.20", responsavel);
        } else if (pontuacao >= 21 && pontuacao <= 40) {
            linha.rsePontuacao = ApplicationResourcesHelper.getMessage("rotulo.servidor.pontuacao.21.40", responsavel);
        } else if (pontuacao >= 41 && pontuacao <= 60) {
            linha.rsePontuacao = ApplicationResourcesHelper.getMessage("rotulo.servidor.pontuacao.41.60", responsavel);
        } else if (pontuacao >= 61 && pontuacao <= 80) {
            linha.rsePontuacao = ApplicationResourcesHelper.getMessage("rotulo.servidor.pontuacao.61.80", responsavel);
        } else if (pontuacao >= 81) {
            linha.rsePontuacao = ApplicationResourcesHelper.getMessage("rotulo.servidor.pontuacao.81.100", responsavel);
        }
        if (temRiscoPelaCsa) {
            linha.arrRisco = RiscoRegistroServidorEnum.recuperaDescricaoRisco(linha.arrRisco, responsavel);
        }
        linha.taxa = !TextHelper.isNull(registroLeilao.getAttribute(Columns.PLS_TAXA_JUROS)) ? NumberHelper.format(((BigDecimal) registroLeilao.getAttribute(Columns.PLS_TAXA_JUROS)).doubleValue(), NumberHelper.getLang()) : "";
        linha.taxaMin = !TextHelper.isNull(registroLeilao.getAttribute("PLS_TAXA_JUROS_MIN")) ? NumberHelper.format(((BigDecimal) registroLeilao.getAttribute("PLS_TAXA_JUROS_MIN")).doubleValue(), NumberHelper.getLang()) : "";

        linha.adeIncMargem = !TextHelper.isNull(registroLeilao.getAttribute(Columns.ADE_INC_MARGEM)) ? Short.valueOf(registroLeilao.getAttribute(Columns.ADE_INC_MARGEM).toString()) : Short.valueOf("0");
        if (linha.adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM) || linha.adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2) || linha.adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
            linha.rseVariacaoMargemLivre = !TextHelper.isNull(registroLeilao.getAttribute("VARIACAO_MARGEM_LIVRE")) ? new BigDecimal(registroLeilao.getAttribute("VARIACAO_MARGEM_LIVRE").toString().replaceAll(",", ".")) : new BigDecimal("0.00");
        } else if (!linha.adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
            linha.rseVariacaoMargemLivre = !TextHelper.isNull(registroLeilao.getAttribute("VARIACAO_MARGEM_EXTRA_LIVRE")) ? new BigDecimal(registroLeilao.getAttribute("VARIACAO_MARGEM_EXTRA_LIVRE").toString().replaceAll(",", ".")) : new BigDecimal("0.00");
        } else {
            linha.rseVariacaoMargemLivre = new BigDecimal("0.00");
        }
        linha.strVariacaoMargemLivre = "";

        if (!linha.adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
            if (linha.rseVariacaoMargemLivre.compareTo(new BigDecimal("10.00")) <= 0) {
                linha.strVariacaoMargemLivre = ApplicationResourcesHelper.getMessage("rotulo.servidor.variacao.margem.livre.0.10", responsavel);
            } else if (linha.rseVariacaoMargemLivre.compareTo(new BigDecimal("20.00")) <= 0) {
                linha.strVariacaoMargemLivre = ApplicationResourcesHelper.getMessage("rotulo.servidor.variacao.margem.livre.11.20", responsavel);
            } else if (linha.rseVariacaoMargemLivre.compareTo(new BigDecimal("30.00")) <= 0) {
                linha.strVariacaoMargemLivre = ApplicationResourcesHelper.getMessage("rotulo.servidor.variacao.margem.livre.21.30", responsavel);
            } else if (linha.rseVariacaoMargemLivre.compareTo(new BigDecimal("40.00")) <= 0) {
                linha.strVariacaoMargemLivre = ApplicationResourcesHelper.getMessage("rotulo.servidor.variacao.margem.livre.31.40", responsavel);
            } else if (linha.rseVariacaoMargemLivre.compareTo(new BigDecimal("50.00")) <= 0) {
                linha.strVariacaoMargemLivre = ApplicationResourcesHelper.getMessage("rotulo.servidor.variacao.margem.livre.41.50", responsavel);
            } else if (linha.rseVariacaoMargemLivre.compareTo(new BigDecimal("60.00")) <= 0) {
                linha.strVariacaoMargemLivre = ApplicationResourcesHelper.getMessage("rotulo.servidor.variacao.margem.livre.51.60", responsavel);
            } else if (linha.rseVariacaoMargemLivre.compareTo(new BigDecimal("70.00")) <= 0) {
                linha.strVariacaoMargemLivre = ApplicationResourcesHelper.getMessage("rotulo.servidor.variacao.margem.livre.61.70", responsavel);
            } else if (linha.rseVariacaoMargemLivre.compareTo(new BigDecimal("80.00")) <= 0) {
                linha.strVariacaoMargemLivre = ApplicationResourcesHelper.getMessage("rotulo.servidor.variacao.margem.livre.71.80", responsavel);
            } else if (linha.rseVariacaoMargemLivre.compareTo(new BigDecimal("90.00")) <= 0) {
                linha.strVariacaoMargemLivre = ApplicationResourcesHelper.getMessage("rotulo.servidor.variacao.margem.livre.81.90", responsavel);
            } else if (linha.rseVariacaoMargemLivre.compareTo(new BigDecimal("90.00")) > 0) {
                linha.strVariacaoMargemLivre = ApplicationResourcesHelper.getMessage("rotulo.servidor.variacao.margem.livre.91.100", responsavel);
            }
        }

        try {
            linha.soaData = DateHelper.reformat(registroLeilao.getAttribute(Columns.SOA_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        if (registroLeilao.getAttribute(Columns.SOA_DATA_VALIDADE) != null) {
        	try {
				linha.soaDataValidadeFim = DateHelper.reformat(registroLeilao.getAttribute(Columns.SOA_DATA_VALIDADE).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
			} catch (ParseException e) {
				LOG.error(e.getMessage(), e);
			}

            Date dtValidade = (Date) registroLeilao.getAttribute(Columns.SOA_DATA_VALIDADE);
            Date dataAtual = Calendar.getInstance().getTime();
            long diff = dtValidade.getTime() - dataAtual.getTime();
            if (diff > 0) {
                long diffSeconds = diff / 1000 % 60;
                long diffMinutes = diff / (60 * 1000) % 60;
                long diffHours = diff / (60 * 60 * 1000);
                linha.soaDataValidade = StringUtils.leftPad(Long.toString(diffHours), 2, "0") + ":" + StringUtils.leftPad(Long.toString(diffMinutes), 2, "0") + ":" + StringUtils.leftPad(Long.toString(diffSeconds), 2, "0");
            } else {
                linha.soaDataValidade = "00:00:00";
            }
        } else {
            linha.soaDataValidade = ApplicationResourcesHelper.getMessage("rotulo.indeterminado.abreviado", responsavel);
            linha.soaDataValidadeFim = ApplicationResourcesHelper.getMessage("rotulo.indeterminado.abreviado", responsavel);
        }

        linha.servidor = registroLeilao.getAttribute(Columns.RSE_MATRICULA) + " - " + registroLeilao.getAttribute(Columns.SER_CPF) + " - " + registroLeilao.getAttribute(Columns.SER_NOME);
        linha.cidadeUf = !TextHelper.isNull(registroLeilao.getAttribute(Columns.CID_NOME)) ? (registroLeilao.getAttribute(Columns.CID_NOME).toString() + "/" + registroLeilao.getAttribute(Columns.UF_COD).toString()) : "";

        return linha;
    }

    public class LinhaAcompanhamentoLeilao {
        public String adeCodigo, adeNumero, adeTipoVlr, adeVlr, adeVlrLiberado, adePrazo, rsePontuacao, rseCodigo, arrRisco, taxa, taxaMin;

        public String soaData, soaDataValidade, soaDataValidadeFim;

        public String servidor;

        public String cidadeUf;

        public Short adeIncMargem;

        public BigDecimal rseVariacaoMargemLivre;

        public String strVariacaoMargemLivre;

        public String statusSolicitacaoLeilao;

        public String textoStatusLeilao = null;
    }

}
