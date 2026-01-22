package com.zetra.econsig.web.tag.v4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.tag.ZetraTagSupport;

public class InformacoesPeriodoTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(InformacoesPeriodoTag.class);

    @Autowired
    private PeriodoController periodoController;

    private AcessoSistema responsavel;
    private List<TransferObject> periodo;
    private String mensagem;
    private String titulo;


    private String tipo;

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Carrega as informacoes a serem exibidas.
     */
    public void carregaInformacoes() {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        responsavel = JspHelper.getAcessoSistema(request);

        // Carrega informações do período
        try {
            String estCodigo = JspHelper.verificaVarQryStr(request, "estCodigo");
            String orgCodigo = JspHelper.verificaVarQryStr(request, "orgCodigo");

            List<String> orgCodigos = null;
            List<String> estCodigos = null;
            if (responsavel.isOrg()) {
                if (responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                    if (!orgCodigo.equals("")) {
                        orgCodigos = new ArrayList<>();
                        orgCodigos.add(orgCodigo);
                    } else {
                        estCodigos = new ArrayList<>();
                        estCodigos.add(responsavel.getCodigoEntidadePai());
                    }
                } else {
                    orgCodigos = new ArrayList<>();
                    orgCodigos.add(responsavel.getCodigoEntidade());
                }
            } else if (responsavel.isCseSup()) {
                if (!estCodigo.equals("")) {
                    estCodigos = new ArrayList<>();
                    estCodigos.add(estCodigo);
                } else if (!orgCodigo.equals("")) {
                    orgCodigos = new ArrayList<>();
                    orgCodigos.add(orgCodigo);
                }
            }
            if (tipo != null) {
                if (tipo.equalsIgnoreCase("margem") || tipo.equalsIgnoreCase("margem_parcial")) {
                    periodo = periodoController.obtemPeriodoCalculoMargem(orgCodigos, estCodigos, false, responsavel);
                    titulo = ApplicationResourcesHelper.getMessage("rotulo.folha.periodo.recalculo.margem", responsavel);

                    if (ParamSist.paramEquals(CodedValues.TPC_SET_PERIODO_EXP_MOV_MES, CodedValues.TPC_NAO, responsavel)) {
                        mensagem = ApplicationResourcesHelper.getMessage("mensagem.folha.recalculo.margem.calculo.periodo.desabilitado", responsavel);
                    } else {
                        mensagem = ApplicationResourcesHelper.getMessage("mensagem.folha.recalculo.margem.ultimo.periodo", responsavel);
                    }

                    if (tipo.equalsIgnoreCase("margem")) {
                        mensagem += " " + ApplicationResourcesHelper.getMessage("mensagem.folha.recalculo.margem.clique.confirmar", responsavel);
                    } else {
                        mensagem += " " + ApplicationResourcesHelper.getMessage("mensagem.folha.recalculo.margem.instrucoes", responsavel);
                    }

                } else if (tipo.equalsIgnoreCase("retorno")) {
                    periodo = periodoController.obtemPeriodoImpRetorno(orgCodigos, estCodigos, false, responsavel);
                    titulo = ApplicationResourcesHelper.getMessage("rotulo.folha.periodo.importacao.retorno", responsavel);

                    if (ParamSist.paramEquals(CodedValues.TPC_SET_PERIODO_EXP_MOV_MES, CodedValues.TPC_NAO, responsavel)) {
                        mensagem = ApplicationResourcesHelper.getMessage("mensagem.folha.importacao.retorno.calculo.periodo.desabilitado", responsavel);
                    } else {
                        mensagem = ApplicationResourcesHelper.getMessage("mensagem.folha.importacao.retorno.ultimo.periodo", responsavel);
                    }

                    mensagem += " " + ApplicationResourcesHelper.getMessage("mensagem.folha.importacao.retorno.instrucoes", responsavel);

                } else if (tipo.equalsIgnoreCase("movimento")) {
                    periodo = periodoController.obtemPeriodoExpMovimento(orgCodigos, estCodigos, false, responsavel);
                    titulo = ApplicationResourcesHelper.getMessage("rotulo.folha.periodo.exportacao.movimento", responsavel);

                    if (ParamSist.paramEquals(CodedValues.TPC_SET_PERIODO_EXP_MOV_MES, CodedValues.TPC_NAO, responsavel)) {
                        mensagem = ApplicationResourcesHelper.getMessage("mensagem.folha.exportacao.movimento.calculo.periodo.desabilitado", responsavel);
                    } else {
                        mensagem = ApplicationResourcesHelper.getMessage("mensagem.folha.periodo.exportacao.movimento.ultimo.periodo", responsavel);
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().print(generateHtml());
        } catch (IOException ex) {
            throw new JspException(ex.getMessage());
        }
        // Continue processing this page
        return (EVAL_PAGE);
    }

    private String generateHtml() {
        // Obtém as informações
        carregaInformacoes();

        StringBuilder html = new StringBuilder();

        if (periodo != null && !periodo.isEmpty()) {
            html.append("              <div class='card-header'>\n");
            html.append("                <h2 class='card-header-title'>").append(titulo).append("</h2>\n");
            html.append("              </div>\n");
            html.append("              <div class='card-body table-responsive p-0'>\n");
            html.append("                <div class='alert alert-warning m-0' role='alert'>\n");
            html.append("                  <p class='mb-0'>").append(mensagem).append("</p>\n");
            html.append("                </div>\n");
            html.append("                <table class='table table-striped table-hover'>\n");
            html.append("                  <thead>\n");
            html.append("                    <tr>\n");
            html.append("                      <th scope='col'>").append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel)).append("</th>\n");
            html.append("                      <th scope='col'>").append(ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.abreviado", responsavel)).append("</th>\n");
            html.append("                      <th scope='col'>").append(ApplicationResourcesHelper.getMessage("rotulo.calendario.folha.periodo", responsavel)).append("</th>\n");
            html.append("                      <th scope='col'>").append(ApplicationResourcesHelper.getMessage("rotulo.calendario.folha.data.inicio", responsavel)).append("</th>\n");
            html.append("                      <th scope='col' width='15%'>").append(ApplicationResourcesHelper.getMessage("rotulo.calendario.folha.data.fim", responsavel)).append("</th>\n");
            html.append("                    </tr>\n");
            html.append("                  </thead>\n");
            html.append("                  <tbody>\n");

            for (TransferObject orgao : periodo) {
                String estIdentificador = (String) orgao.getAttribute(Columns.EST_IDENTIFICADOR);
                String orgIdentificador = (String) orgao.getAttribute(Columns.ORG_IDENTIFICADOR);
                String orgNome = (String) orgao.getAttribute(Columns.ORG_NOME);
                String pexPeriodo = DateHelper.toPeriodString((Date) orgao.getAttribute(Columns.PEX_PERIODO));
                String pexDataIni = DateHelper.toDateString((Date) orgao.getAttribute(Columns.PEX_DATA_INI));
                String pexDataFim = DateHelper.toDateString((Date) orgao.getAttribute(Columns.PEX_DATA_FIM));

                html.append("                    <tr>\n");
                html.append("                      <td>").append(TextHelper.forHtmlContent(orgNome)).append(" - ").append(TextHelper.forHtmlContent(orgIdentificador)).append("</td>\n");
                html.append("                      <td align=\"center\">").append(TextHelper.forHtmlContent(estIdentificador)).append("</td>\n");
                html.append("                      <td align=\"center\">").append(TextHelper.forHtmlContent(pexPeriodo)).append("</td>\n");
                html.append("                      <td align=\"center\">").append(TextHelper.forHtmlContent(pexDataIni)).append("</td>\n");
                html.append("                      <td align=\"center\">").append(TextHelper.forHtmlContent(pexDataFim)).append("</td>\n");
                html.append("                    </tr>\n");

            }
            html.append("                  </tbody>\n");
            html.append("                  <tfoot>\n");
            html.append("                    <tr>\n");
            html.append("                       <td colspan='5'>").append(ApplicationResourcesHelper.getMessage("rotulo.folha.importacao.retorno.periodo", responsavel)).append("\n");
            html.append("                       </td>\n");
            html.append("                     </tr>\n");
            html.append("                  </tfoot>\n");
            html.append("                </table>\n");
            html.append("              </div>\n");
        }

        return html.toString();
    }
}
