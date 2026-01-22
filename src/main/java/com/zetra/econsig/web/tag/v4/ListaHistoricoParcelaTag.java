package com.zetra.econsig.web.tag.v4;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.tag.ZetraTagSupport;

/**
 * <p>Title: ListaHistoricoParcelaTag</p>
 * <p>Description: Tag para listagem dos históricos de parcela de uma consignação no leiaute v4.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaHistoricoParcelaTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaHistoricoParcelaTag.class);

    // Indica se <table></table> deve ser impresso
    private boolean table;
    // Nome do atributo que contém os dados dao consignação
    private String name;
    // Escopo do atributo que contém os dados da consignação
    private String scope;

    public void setTable(boolean table) {
        this.table = table;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int doEndTag() throws JspException {
        try {
            // Inicia geração do código HTML
            StringBuilder code = new StringBuilder();

            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            // Obtém a lista com os anexos da consignação
            List<TransferObject> parcelas = (List<TransferObject>) pageContext.getAttribute(name, getScopeAsInt(scope));

            if (parcelas != null && parcelas.size() > 0) {
                if (table) {
                    code.append(abrirTabela(responsavel));
                }

                code.append(montarCabecalho(responsavel));
                code.append("<tbody>");

                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_MULTIPLAS_LINHAS_RETORNO_MESMA_PRD, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                    parcelas = TextHelper.groupConcat(parcelas, new String[]{Columns.PRD_NUMERO, Columns.OCP_DATA}, new String[]{Columns.OCP_OBS}, "<br>", false, false);
                }

                Iterator<TransferObject> it = parcelas.iterator();
                TransferObject parcela = null;
                String prdNumero, prdDataDesconto, prdVlrRealizado, spdDescricao, ocpData, usuLogin, ocpObs;
                BigDecimal vlrPrevisto, vlrRealizado;

                while (it.hasNext()) {
                    parcela = it.next();

                    parcela = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) parcela, null, responsavel);

                    vlrPrevisto = (BigDecimal) parcela.getAttribute(Columns.PRD_VLR_PREVISTO);
                    vlrRealizado = (BigDecimal) parcela.getAttribute(Columns.PRD_VLR_REALIZADO);

                    prdNumero = parcela.getAttribute(Columns.PRD_NUMERO) != null ? parcela.getAttribute(Columns.PRD_NUMERO).toString(): "";
                    prdDataDesconto = parcela.getAttribute(Columns.PRD_DATA_DESCONTO) != null ? DateHelper.toPeriodString((java.util.Date) parcela.getAttribute(Columns.PRD_DATA_DESCONTO)) : "";
                    prdVlrRealizado = vlrRealizado != null ? NumberHelper.reformat(vlrRealizado.toString(), "en", NumberHelper.getLang()) : "";
                    spdDescricao = parcela.getAttribute(Columns.SPD_DESCRICAO) != null ? parcela.getAttribute(Columns.SPD_DESCRICAO).toString(): "";
                    ocpData = parcela.getAttribute(Columns.OCP_DATA) != null ? DateHelper.toDateString((java.util.Date) parcela.getAttribute(Columns.OCP_DATA)) : "";
                    usuLogin = parcela.getAttribute(Columns.USU_LOGIN) != null ? parcela.getAttribute(Columns.USU_LOGIN).toString(): "";
                    usuLogin = (usuLogin.equals(parcela.getAttribute(Columns.USU_CODIGO)) && parcela.getAttribute(Columns.USU_TIPO_BLOQ) != null) ? (parcela.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)") : usuLogin;
                    ocpObs = parcela.getAttribute(Columns.OCP_OBS) != null ? parcela.getAttribute(Columns.OCP_OBS).toString(): "";

                    // Concatena rótulo de (Parcial) à frente do status da parcela caso o valor realizado seja maior que Zero e menor que o Previsto
                    if (vlrRealizado != null && vlrRealizado.signum() > 0 && vlrRealizado.compareTo(vlrPrevisto) < 0) {
                        spdDescricao += " (" + ApplicationResourcesHelper.getMessage("rotulo.parcial", responsavel) + ")";
                    }

                    if (parcela.getAttribute(Columns.SPD_DESCRICAO).equals(ApplicationResourcesHelper.getMessage("rotulo.em.aberto", responsavel)) || parcela.getAttribute(Columns.SPD_DESCRICAO).equals(ApplicationResourcesHelper.getMessage("rotulo.em.processamento", responsavel))) {
                        prdVlrRealizado = vlrPrevisto != null ? NumberHelper.reformat(vlrPrevisto.toString(), "en", NumberHelper.getLang()) : "";
                    }

                    code.append(montarLinhaLista(prdNumero, prdDataDesconto, prdVlrRealizado, spdDescricao, ocpData, usuLogin, ocpObs, responsavel));
                }

                code.append("</tbody>");
                if (table) {
                    code.append(fecharTabela(responsavel));
                }
            }

            pageContext.getOut().print(code.toString());

            return EVAL_PAGE;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

    private String abrirTabela(AcessoSistema responsavel) {
        return "<table class=\"table table-striped table-hover table-responsive\">\n";
    }

    private String fecharTabela(AcessoSistema responsavel) {
        String dataAtual = DateHelper.format(DateHelper.getSystemDatetime(), LocaleHelper.getDateTimePattern());
        String textoRodape = ApplicationResourcesHelper.getMessage("mensagem.detalhe.consignacao.historico.prd", responsavel, dataAtual);
        return  "<tfoot>\n" +
                "  <tr><td colspan=\"7\">" + textoRodape + "</td></tr>" +
                "</tfoot>\n" +
                "</table>";
    }

    private String montarCabecalho(AcessoSistema responsavel) {
        return "<thead>\n" +
               "  <tr>\n" +
               "    <th id=\"numeroParcela\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.parcela.numero", responsavel) + "</th>\n" +
               "    <th id=\"dataDesconto\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.parcela.data.desconto", responsavel) + "</th>\n" +
               "    <th id=\"valor\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.parcela.valor", responsavel) + "</th>\n" +
               "    <th id=\"situacao\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.parcela.situacao", responsavel) + "</th>\n" +
               "    <th id=\"dataOcorrencia\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.parcela.data.ocorrencia", responsavel) + "</th>\n" +
               "    <th id=\"responsavelOcorrencia\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.parcela.responsavel", responsavel) + "</th>\n" +
               "    <th id=\"ocorrencias\">" + ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.parcela.subtitulo", responsavel) + "</th>\n" +
               "  </tr>\n" +
               "</thead>\n"
               ;
    }

    private String montarLinhaLista(String prdNumero, String prdDataDesconto, String prdVlrRealizado, String spdDescricao, String ocpData, String usuLogin, String ocpObs, AcessoSistema responsavel) {
        return "  <tr>\n" +
               "    <td header=\"numeroParcela\">" + TextHelper.forHtmlContent(prdNumero) + "</td>\n" +
               "    <td header=\"dataDesconto\">" + TextHelper.forHtmlContent(prdDataDesconto) + "</td>\n" +
               "    <td header=\"valor\" class=\"text-right\">" + TextHelper.forHtmlContent(prdVlrRealizado) + "</td>\n" +
               "    <td header=\"situacao\">" + TextHelper.forHtmlContent(spdDescricao) + "</td>\n" +
               "    <td header=\"dataOcorrencia\">" + TextHelper.forHtmlContent(ocpData) + "</td>\n" +
               "    <td header=\"responsavelOcorrencia\">" + TextHelper.forHtmlContent(usuLogin) + "</td>\n" +
               "    <td header=\"observacaoOcorrencia\">" + TextHelper.forHtmlContentComTags(ocpObs) + "</td>\n" +
               "  </tr>\n"
               ;
    }
}
