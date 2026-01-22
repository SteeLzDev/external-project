package com.zetra.econsig.web.tag.v4;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.tag.ZetraTagSupport;

/**
 * <p>Title: ListaAnexosComunicacaoTag</p>
 * <p>Description: Tag para listagem dos anexos de uma Comunicação.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class ListaAnexosComunicacaoTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaAnexosComunicacaoTag.class);

    // Indica se <table></table> deve ser impresso
    private boolean table;
    // Nome do atributo que contém os dados dao consignação
    private String name;
    // Escopo do atributo que contém os dados da consignação
    private String scope;

    private AcessoSistema responsavel;

    public void setName(String name) {
        this.name = name;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setTable(boolean table) {
        this.table = table;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int doEndTag() throws JspException {
        try {
            // Inicia geração do código HTML
            StringBuilder code = new StringBuilder();

            // Obtém a lista com os anexos da comunicacao
            List<TransferObject> anexos = (List<TransferObject>) pageContext.getAttribute(name, getScopeAsInt(scope));

            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            responsavel = JspHelper.getAcessoSistema(request);

            if (anexos != null && anexos.size() > 0) {
                if (table) {
                    code.append(abrirTabela());
                }

                code.append(montarCabecalho());

                Iterator<TransferObject> it = anexos.iterator();
                TransferObject anexo = null;
                String cmnCodigo, acmData, acmResponsavel, acmNome, acmDescricao, dirData;
                boolean acmAtivo;

                code.append("<tbody>");
                while (it.hasNext()) {
                    anexo = it.next();

                    anexo = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) anexo, null, responsavel);

                    cmnCodigo = anexo.getAttribute(Columns.ACM_CMN_CODIGO).toString();
                    acmData = DateHelper.toDateTimeString((Date) anexo.getAttribute(Columns.ACM_DATA));
                    dirData = DateHelper.format((Date) anexo.getAttribute(Columns.CMN_DATA), "yyyyMMdd");
                    acmResponsavel = anexo.getAttribute(Columns.USU_LOGIN).toString();
                    acmNome = anexo.getAttribute(Columns.ACM_NOME).toString();
                    acmAtivo = ((Short) anexo.getAttribute(Columns.ACM_ATIVO)).equals(CodedValues.STS_ATIVO);
                    acmDescricao = anexo.getAttribute(Columns.ACM_DESCRICAO).toString();

                    code.append(montarLinhaLista(dirData, cmnCodigo, acmData, acmResponsavel, acmNome, acmDescricao, acmAtivo, ""));
                }
                code.append("</tbody>");
                if (table) {
                    code.append("<tfoot><tr>").append("<td colspan=\"5\">").append(ApplicationResourcesHelper.getMessage("rotulo.listagem.comunicacao.anexo", responsavel)).append("</td></tr><tfoot>");
                    code.append(fecharTabela());
                    code.append("</div>");
                }
            }
            pageContext.getOut().print(code.toString());

            return EVAL_PAGE;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

    @Override
    protected String abrirTabela() {
        return "<div class=\"card-header\">" +
               "  <h2 class=\"card-header-title\">"+ ApplicationResourcesHelper.getMessage("rotulo.comunicacao.anexo.plural", responsavel) +"</h2>" +
               " </div>"
               ;
    }

    private String montarCabecalho() {
                return "<div class=\"card-body table-responsive p-0\">" +
                    "    <table class=\"table table-striped table-hover\">"+
                    "        <thead>"+
                    "            <tr>"+
                    "                <th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.anexo.data.anexo", responsavel) + "</th>" +
                    "                <th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.anexo.responsavel", responsavel) + "</td>" +
                    "                <th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.anexo.nome", responsavel) + "</td>" +
                    "                <th scope=\"col\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes", responsavel) + "</td>" +
                    "            </tr>"+
                    "        </thead>"
                    ;
    }


    private String montarLinhaLista(String dirData, String cmnCodigo, String acmData, String acmResponsavel, String acmNome, String acmDescricao, boolean aadAtivo, String cssLinha) {
        return
               "    <tr>"+
               "     <td>" + TextHelper.forHtmlContent(acmData) + "</td>\n" +
               "     <td>" + TextHelper.forHtmlContent(acmResponsavel) + "</td>\n" +
               "     <td>" + TextHelper.forHtmlContent(acmDescricao) + "</td>\n" +
               "     <td>"+
               "        <a href=\"#\" name=\"download\" onClick=\"postData('../v3/downloadArquivo?arquivo_nome=" + TextHelper.forJavaScriptAttribute(acmNome) + "&tipo=comunicacao&entidade=" + TextHelper.forJavaScriptAttribute(cmnCodigo) +"&data="+ TextHelper.forJavaScriptAttribute(dirData) + "&" + SynchronizerToken.generateToken4URL((HttpServletRequest) pageContext.getRequest()) + "&_skip_history_=true','download');\"; value=\"Download\" alt=\"" + ApplicationResourcesHelper.getMessage("mensagem.download.arquivo.anexo.clique.aqui", responsavel) + "\" title=\"" + ApplicationResourcesHelper.getMessage("mensagem.download.arquivo.anexo.clique.aqui", responsavel)+"\">" +
                        ApplicationResourcesHelper.getMessage("rotulo.acoes.download", responsavel)+"</a>" +
               "     </td>"+
               "    </tr>"
               ;
    }
}
