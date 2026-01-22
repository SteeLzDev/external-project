package com.zetra.econsig.web.tag.v4;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.helper.consignacao.PriceHelper.Parcela;
import com.zetra.econsig.helper.consignacao.PriceHelper.TabelaPrice;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.tag.ZetraTagSupport;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

public class TabelaPriceTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TabelaPriceTag.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    // Nome do atributo que contém os dados dao consignação
    protected String name;

    // Escopo do atributo que contém os dados da consignação
    protected String scope;

    public void setName(String name) {
        this.name = name;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            // Obtém o DTO com os dados da consignação
            CustomTransferObject autdes = (CustomTransferObject) pageContext.getAttribute(name, getScopeAsInt(scope));
            TabelaPrice tabelaPrice = (TabelaPrice) pageContext.getAttribute("tabelaPrice", getScopeAsInt(scope));

            pageContext.getOut().print(getParcelas(autdes, tabelaPrice, responsavel));

            return EVAL_PAGE;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

    public String getParcelas(CustomTransferObject autdes, TabelaPrice tabelaPrice, AcessoSistema responsavel) throws ParseException, IOException {
        List<Parcela> parcelas = (tabelaPrice != null ? tabelaPrice.getParcelas() : null);

        // Inicia geração do código HTML
        StringBuilder code = new StringBuilder();

        if (parcelas != null && !parcelas.isEmpty()) {
            Object metodo = ParamSist.getInstance().getParam(CodedValues.TPC_METODO_CALCULO_SIMULACAO, responsavel);
            boolean metodoMexicano = !TextHelper.isNull(metodo) && CodedValues.MCS_MEXICANO.equals(metodo);
            boolean metodoIndiano = !TextHelper.isNull(metodo) && CodedValues.MCS_INDIANO.equals(metodo);
            boolean simulacaoRenegociacao = TextHelper.isNull(TextHelper.forHtmlContent(autdes.getAttribute(Columns.ADE_NUMERO)));

            if (!simulacaoRenegociacao) {
                code.append("  <div class=\"row firefox-print-fix\">");
                //1.inicio coluna 1 (dados da consignacao)
                code.append("    <div class=\"col-md-12 col-lg-6\">");
            }
            code.append("      <div class=\"card\" id=\"tabelaPrice\">");
            code.append("        <div class=\"card-header\">");
            code.append("          <h2 class=\"card-header-title\">");
            code.append(ApplicationResourcesHelper.getMessage("rotulo.efetiva.acao.consignacao.dados.consignacao", responsavel));
            code.append("          </h2>");
            code.append("        </div>");
            code.append("        <div class =\"card-body\">");
            code.append("        <dl class=\"row data-list firefox-print-fix\">");

            if (!simulacaoRenegociacao) {
                //Nº ADE
                code.append("<dt class='col-6'>").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel)).append("</dt>");
                code.append("<dd class='col-6'>").append(TextHelper.forHtmlContent(autdes.getAttribute(Columns.ADE_NUMERO))).append("</dd>");

            }

            String adeData = DateHelper.toDateTimeString((java.util.Date) autdes.getAttribute(Columns.ADE_DATA));
            code.append("<dt class='col-6'>").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.reserva", responsavel)).append("</dt>");
            code.append("<dd class='col-6'>").append(adeData).append("</dd>");

            //continua a tabela acima
            BigDecimal vlrLiqLiberado = metodoIndiano ? tabelaPrice.getValorLiquidoLiberado().setScale(0, RoundingMode.HALF_UP) : tabelaPrice.getValorLiquidoLiberado();
            code.append("<dt class='col-6'>").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.liquido.liberado", responsavel)).append("</dt>");
            code.append("<dd class='col-6'>").append(TextHelper.forHtmlContent(NumberHelper.format(vlrLiqLiberado.doubleValue(), NumberHelper.getLang()))).append("</dd>");

            BigDecimal vlrPrestacao = metodoIndiano ? tabelaPrice.getPrestacao().setScale(2, RoundingMode.HALF_DOWN) : tabelaPrice.getPrestacao();
            code.append("<dt class='col-6'>").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela", responsavel)).append("</dt>");
            code.append("<dd class='col-6'>").append(TextHelper.forHtmlContent(NumberHelper.format(vlrPrestacao.doubleValue(), NumberHelper.getLang()))).append("</dd>");

            BigDecimal totalPrestacao = metodoIndiano ? tabelaPrice.getTotalPrestacao().setScale(0, RoundingMode.HALF_UP) : tabelaPrice.getTotalPrestacao();
            code.append("<dt class='col-6'>").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.prazo", responsavel)).append("</dt>");
            code.append("<dd class='col-6'>").append(TextHelper.forHtmlContent(NumberHelper.format(tabelaPrice.getPrazo().intValue(), NumberHelper.getLang(), 0, 0))).append("</dd>");

            code.append("<dt class='col-6'>").append(ApplicationResourcesHelper.getMessage(metodoIndiano ? "rotulo.consignacao.taxa.juros.anual" : "rotulo.consignacao.taxa.juros.mensal", responsavel)).append("</dt>");
            code.append("<dd class='col-6'>").append(TextHelper.forHtmlContent(NumberHelper.format(tabelaPrice.getTaxaJuros().doubleValue(), NumberHelper.getLang()))).append("</dd>");

            if (metodoMexicano) {
                //taxa de juros
                code.append("<dt class='col-6'>").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros.mensal.sem.iva", responsavel)).append("</dt>");
                code.append("<dd class='col-6'>").append(TextHelper.forHtmlContent(NumberHelper.format(tabelaPrice.getTaxaJurosSemIva().doubleValue(), NumberHelper.getLang()))).append("</dd>");

                //taxa iva
                code.append("<dt class='col-6'>").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.iva.abreviado", responsavel)).append("</dt>");
                code.append("<dd class='col-6'>").append(TextHelper.forHtmlContent(NumberHelper.format(tabelaPrice.getTaxaIva().doubleValue(), NumberHelper.getLang()))).append("</dd>");
            }
            code.append("<dt class='col-6'>").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.prz.multiplicado.prd.moeda", responsavel)).append("</dt>");
            code.append("<dd class='col-6'>").append(TextHelper.forHtmlContent(NumberHelper.format((totalPrestacao.doubleValue()), NumberHelper.getLang()))).append("</dd>");

            code.append("</dl>");
            code.append("</div>");
            code.append("</div>");
            if (!simulacaoRenegociacao) {
                code.append("</div>");
                //2.inicio coluna 2 (consignante e servidor)
                code.append("    <div class=\"col-md-12 col-lg-6\">");
                //consignante
                code.append("      <div class=\"card\">");
                code.append("        <div class=\"card-header\">");
                code.append("          <h2 class=\"card-header-title\">");
                code.append(ApplicationResourcesHelper.getMessage("rotulo.consignante.dados.consignante", responsavel));
                code.append("          </h2>");
                code.append("        </div>");
                code.append("        <div class=\"card-body\">");
                code.append("        <dl class=\"row data-list firefox-print-fix\">");
                //Estabelecimento
                String estNome = TextHelper.forHtmlContent((!TextHelper.isNull(autdes.getAttribute(Columns.EST_IDENTIFICADOR)) && !TextHelper.isNull(autdes.getAttribute(Columns.EST_NOME))) ? autdes.getAttribute(Columns.EST_IDENTIFICADOR) + " - " + autdes.getAttribute(Columns.EST_NOME) : "");
                code.append("<dt class='col-6'>").append(ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.singular", responsavel)).append("</dt>");
                code.append("<dd class='col-6'>").append(TextHelper.forHtmlContent(estNome)).append("</dd>");
                //órgão
                String orgNome = TextHelper.forHtmlContent((!TextHelper.isNull(autdes.getAttribute(Columns.ORG_IDENTIFICADOR)) && !TextHelper.isNull(autdes.getAttribute(Columns.ORG_NOME))) ? autdes.getAttribute(Columns.ORG_IDENTIFICADOR) + " - " + autdes.getAttribute(Columns.ORG_NOME) : "");
                code.append("<dt class='col-6'>").append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel)).append("</dt>");
                code.append("<dd class='col-6'>").append(TextHelper.forHtmlContent(orgNome)).append("</dd>");
                code.append("</dl>");
                code.append("</div>");
                code.append("</div>");
                //servidor
                code.append("      <div class=\"card\">");
                code.append("        <div class=\"card-header\">");
                code.append("          <h2 class=\"card-header-title\">");
                code.append(ApplicationResourcesHelper.getMessage("rotulo.validar.servidor.dados.servidor", responsavel));
                code.append("          </h2>");
                code.append("        </div>");
                code.append("        <div class=\"card-body\">");
                code.append("        <dl class=\"row data-list firefox-print-fix\">");

                //DADOS DO SERVIDOR
                String rseMatricula = TextHelper.forHtmlContent(autdes.getAttribute(Columns.RSE_MATRICULA));
                String serNome = TextHelper.forHtmlContent(autdes.getAttribute(Columns.SER_NOME) != null ? autdes.getAttribute(Columns.SER_NOME).toString() : "");
                String textoMatriculaNome = (!(TextHelper.isNull(rseMatricula)) ? rseMatricula + " - " + serNome : serNome);
                String serCpf = TextHelper.forHtmlContent(autdes.getAttribute(Columns.SER_CPF) != null ? autdes.getAttribute(Columns.SER_CPF).toString() : "");
                String serDataNasc = "";
                if (!TextHelper.isNull(autdes.getAttribute(Columns.SER_DATA_NASC))) {
                    serDataNasc = TextHelper.forHtmlContent(autdes.getAttribute(Columns.SER_DATA_NASC) != null ? autdes.getAttribute(Columns.SER_DATA_NASC).toString() : "");
                    serDataNasc = (serDataNasc.equals("0000-00-00") || serDataNasc.equals("0001-01-01") || serDataNasc.equals("1753-01-01")) ? "" : DateHelper.reformat(serDataNasc, "yyyy-MM-dd", LocaleHelper.getDatePattern());
                }
                String serNroIdt = TextHelper.forHtmlContent(autdes.getAttribute(Columns.SER_NRO_IDT) != null ? autdes.getAttribute(Columns.SER_NRO_IDT).toString() : "");
                String serEmisIdt = TextHelper.forHtmlContent(autdes.getAttribute(Columns.SER_EMISSOR_IDT) != null ? autdes.getAttribute(Columns.SER_EMISSOR_IDT).toString() : "");
                String serUfIdt = TextHelper.forHtmlContent(autdes.getAttribute(Columns.SER_UF_IDT) != null ? autdes.getAttribute(Columns.SER_UF_IDT).toString() : "");
                String serDataIdt = "";
                if (!TextHelper.isNull(autdes.getAttribute(Columns.SER_DATA_IDT))) {
                    try {
                        serDataIdt = DateHelper.reformat(autdes.getAttribute(Columns.SER_DATA_IDT).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern());
                    } catch (Exception e) {
                        serDataIdt = TextHelper.forHtmlContent(autdes.getAttribute(Columns.SER_DATA_IDT));
                    }
                }
                String serIdt = TextHelper.forHtmlContent(serNroIdt + (!serEmisIdt.equals("") ? " - " + serEmisIdt : "") + (!serUfIdt.equals("") ? " - " + serUfIdt: "") + (!serDataIdt.equals("") ? " - " + serDataIdt: ""));

                String rotuloCpf = ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel);
                String rotuloIdentidade = ApplicationResourcesHelper.getMessage("rotulo.servidor.cartIdentidade", responsavel);
                String rotuloDataNasc = ApplicationResourcesHelper.getMessage("rotulo.servidor.dataNasc", responsavel);

                //Servidor
                code.append("<dt class='col-6'>").append(ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel)).append("</dt>");
                code.append("<dd class='col-6'>").append(textoMatriculaNome).append("</dd>");

                //Data de nascimento -CPF
                if (!serDataNasc.equals("")) {
                    code.append("<dt class='col-6'>").append(rotuloDataNasc + " - " + rotuloCpf).append("</dt>");
                    code.append("<dd class='col-6'>").append(TextHelper.forHtmlContent(serDataNasc + " - " + serCpf)).append("</dd>");
                }else {
                    code.append("<dt class='col-6'>").append(rotuloCpf).append("</dt>");
                    code.append("<dd class='col-6'>").append(TextHelper.forHtmlContent(serCpf)).append("</dd>");
                }


                //Nº identidade
                if (!serIdt.equals("")) {
                    code.append("<dt class='col-6'>").append(rotuloIdentidade).append("</dt>");
                    code.append("<dd class='col-6'>").append(TextHelper.forHtmlContent(serIdt)).append("</dd>");
                }

                //Data de admissão - Categoria
                String rseDataAdmissao = "";
                if (!TextHelper.isNull(autdes.getAttribute(Columns.RSE_DATA_ADMISSAO))) {
                    rseDataAdmissao = TextHelper.forHtmlContent(!TextHelper.isNull(autdes.getAttribute(Columns.RSE_DATA_ADMISSAO)) ? DateHelper.format((java.sql.Date) autdes.getAttribute(Columns.RSE_DATA_ADMISSAO), LocaleHelper.getDatePattern()) : "");
                }
                String rseTipo = TextHelper.forHtmlContent(!TextHelper.isNull(autdes.getAttribute(Columns.RSE_TIPO)) ? autdes.getAttribute(Columns.RSE_TIPO).toString() : "");
                String rseClt = TextHelper.forHtmlContent(!TextHelper.isNull(autdes.getAttribute(Columns.RSE_CLT)) ? (autdes.getAttribute(Columns.RSE_CLT).toString().equalsIgnoreCase("S") ? ApplicationResourcesHelper.getMessage("rotulo.servidor.clt", responsavel) : "") : "");
                String rsePrazo = TextHelper.forHtmlContent(!TextHelper.isNull(autdes.getAttribute(Columns.RSE_PRAZO)) ? autdes.getAttribute(Columns.RSE_PRAZO).toString() + " " + ApplicationResourcesHelper.getMessage("rotulo.meses", responsavel) : "");
                String rotuloAdm = "";
                String dadosAdm = "";

                if (!rseDataAdmissao.equals("") || !rseTipo.equals("") || !rseClt.equals("") || !rsePrazo.equals("")) {
                    String rotuloCategoria = ApplicationResourcesHelper.getMessage("rotulo.servidor.categoria", responsavel);
                    String rotuloDataAdmissao = ApplicationResourcesHelper.getMessage("rotulo.servidor.dataAdmissao", responsavel);
                    if (!rseDataAdmissao.equals("")) {

                        if (!rseTipo.equals("")) {
                            rotuloAdm = rotuloDataAdmissao + " - " + rotuloCategoria;
                            dadosAdm = rseDataAdmissao + " - " + rseTipo;
                        } else {
                            rotuloAdm = rotuloDataAdmissao;
                            dadosAdm = rseDataAdmissao;
                        }
                        if (!rseClt.equals("")) {
                            dadosAdm += " - " + rseClt;
                        }
                        if (!rsePrazo.equals("")) {
                            dadosAdm += " - " + rsePrazo;
                        }

                    } else if (!rseTipo.equals("")){
                        rotuloAdm = rotuloCategoria;
                        dadosAdm = rseTipo;
                        if (!rseClt.equals("")) {
                            dadosAdm += " - " + rseClt;
                        }
                        if (!rsePrazo.equals("")) {
                            dadosAdm += " - " + rsePrazo;
                        }
                    }
                    if (!dadosAdm.equals("")) {
                        code.append("<dt class='col-6'>").append(rotuloAdm).append("</dt>");
                        code.append("<dd class='col-6'>").append(TextHelper.forHtmlContent(dadosAdm)).append("</dd>");
                    }
                }

                code.append("</dl>");
                code.append("</div>");
                code.append("</div>");

                code.append("</div>");
                code.append("  </div>");
                //fim da parte de cima da tela
            }
            
            BigDecimal totalPrestacoes = BigDecimal.ZERO;
            BigDecimal totalJuros = BigDecimal.ZERO;
            BigDecimal totalIva = BigDecimal.ZERO;
            BigDecimal totalAmortizacao = BigDecimal.ZERO;
            BigDecimal totalSaldoDevedor = BigDecimal.ZERO;

            code.append("<div class=\"row firefox-print-fix\">");
            code.append("<div class=\"col-sm-12 col-md-12\">");
            code.append("<div class=\"card\">");
            code.append("<div class=\"card-header\">");
            code.append("<h2 class=\"card-header-title\">");
            code.append(ApplicationResourcesHelper.getMessage("rotulo.tabela.price.titulo.parcela", responsavel));
            code.append("</h2>");
            code.append("</div>");
            code.append("<div class=\"card-body table-responsive p-0\">");

            code.append("<table class=\"table table-striped table-hover\">");
            //cabeçalho
            code.append("<thead>");
            code.append("<tr>");
            code.append("<th scope=\"col\">").append(ApplicationResourcesHelper.getMessage("rotulo.parcela.numero", responsavel)).append("</th>");

            StringBuilder rotuloVlrParcela = new StringBuilder();
            rotuloVlrParcela.append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela", responsavel));
            rotuloVlrParcela.append("(").append(ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel)).append(")");

            code.append("<th scope=\"col\">").append(rotuloVlrParcela.toString()).append("</th>");


            StringBuilder rotuloJuros = new StringBuilder();
            rotuloJuros.append(ApplicationResourcesHelper.getMessage("rotulo.tabela.price.juros", responsavel));
            rotuloJuros.append("(").append(ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel)).append(")");

            code.append("<th scope=\"col\">").append(rotuloJuros.toString()).append("</th>");

            if (metodoMexicano) {
                StringBuilder rotuloIva = new StringBuilder();
                rotuloIva.append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.iva.abreviado", responsavel));
                rotuloIva.append("(").append(ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel)).append(")");
                code.append("<th scope=\"col\">").append(rotuloIva.toString()).append("</th>");
            }

            StringBuilder rotuloAmortizacao = new StringBuilder();
            rotuloAmortizacao.append(ApplicationResourcesHelper.getMessage("rotulo.tabela.price.amortizacao", responsavel));
            rotuloAmortizacao.append("(").append(ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel)).append(")");
            code.append("<th scope=\"col\">").append(rotuloAmortizacao.toString()).append("</th>");

            StringBuilder rotuloSaldoDevedor = new StringBuilder();
            rotuloSaldoDevedor.append(ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.singular", responsavel));
            rotuloSaldoDevedor.append("(").append(ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel)).append(")");
            code.append("<th scope=\"col\">").append(rotuloSaldoDevedor.toString()).append("</th>");

            //fim cabeçalho
            code.append("</thead>");

            //inicio listagem com os dados
            code.append("<tbody>");
            for (Parcela historico : parcelas) {
                BigDecimal saldoDevedorHistorico = metodoIndiano ? historico.getSaldoDevedor().setScale(2, RoundingMode.HALF_UP) : historico.getSaldoDevedor();
                BigDecimal prestacaoHistorico = metodoIndiano ? historico.getPrestacao().setScale(2, RoundingMode.HALF_DOWN) : historico.getPrestacao();
                BigDecimal amortizacaoHistorico = metodoIndiano ? historico.getAmortizacao().setScale(1, RoundingMode.HALF_UP) : historico.getAmortizacao();

                String sequencia = NumberHelper.format(historico.getSequencia(), NumberHelper.getLang(), 0, 0);
                String prestacao = NumberHelper.format(prestacaoHistorico.doubleValue(), NumberHelper.getLang());
                String juros = NumberHelper.format(historico.getJuros().doubleValue(), NumberHelper.getLang());
                String iva = "";
                if (metodoMexicano) {
                    iva = NumberHelper.format(historico.getIva().doubleValue(), NumberHelper.getLang());
                }
                String amortizacao = NumberHelper.format(amortizacaoHistorico.doubleValue(), NumberHelper.getLang());
                String saldoDevedor = NumberHelper.format(saldoDevedorHistorico.doubleValue(), NumberHelper.getLang());

                totalPrestacoes = totalPrestacoes.add(prestacaoHistorico);
                totalJuros = totalJuros.add(historico.getJuros());
                if (metodoMexicano) {
                    totalIva = totalIva.add(historico.getIva());
                }
                totalAmortizacao = totalAmortizacao.add(amortizacaoHistorico);


                StringBuilder colunaDetalhes = new StringBuilder();
                colunaDetalhes.append(geraColunaSimples(TextHelper.forHtmlContent(sequencia)));
                colunaDetalhes.append(geraColunaSimples(TextHelper.forHtmlContent(prestacao)));
                colunaDetalhes.append(geraColunaSimples(TextHelper.forHtmlContent(juros)));
                if (metodoMexicano) {
                    colunaDetalhes.append(geraColunaSimples(TextHelper.forHtmlContent(iva)));
                }
                colunaDetalhes.append(geraColunaSimples(TextHelper.forHtmlContent(amortizacao)));
                colunaDetalhes.append(geraColunaSimples(TextHelper.forHtmlContent(saldoDevedor)));

                code.append("<tr>");
                code.append(colunaDetalhes);
                code.append("</tr>");

            }

            // Totalizadores
            code.append("<tr>");

            code.append(geraColunaSimples(ApplicationResourcesHelper.getMessage("rotulo.tabela.price.total", responsavel)));
            code.append(geraColunaSimples(TextHelper.forHtmlContent(NumberHelper.format(totalPrestacoes.doubleValue(), NumberHelper.getLang()))));
            code.append(geraColunaSimples(TextHelper.forHtmlContent(NumberHelper.format(totalJuros.doubleValue(), NumberHelper.getLang()))));

            if (metodoMexicano) {
                code.append(geraColunaSimples(TextHelper.forHtmlContent(NumberHelper.format(totalIva.doubleValue(), NumberHelper.getLang()))));
            }

            code.append(geraColunaSimples(TextHelper.forHtmlContent(NumberHelper.format(totalAmortizacao.doubleValue(), NumberHelper.getLang()))));
            code.append(geraColunaSimples(TextHelper.forHtmlContent(NumberHelper.format(totalSaldoDevedor.doubleValue(), NumberHelper.getLang()))));

            code.append("</tr>");

            //fim dados
            code.append("</tbody>");
            code.append("</table>");
            //listagem de parcelas - fim
            code.append("</div>");
            code.append("</div>");
        }

        return code.toString();
    }

    private String geraColunaSimples(String valor) {
        StringBuilder colunaSimples = new StringBuilder();
        colunaSimples.append("<td>").append(valor).append("</td>");
        return colunaSimples.toString();
    }

}
