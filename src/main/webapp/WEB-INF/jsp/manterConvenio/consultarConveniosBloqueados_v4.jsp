<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    List<?> servicosBloqueados = (List<?>) request.getAttribute("servicosBloqueados");
    List<?> conveniosBloqueados = (List<?>) request.getAttribute("conveniosBloqueados");
    List<?> naturezasServicoBloqueados = (List<?>) request.getAttribute("naturezasServicoBloqueados");
    TransferObject servidor = (TransferObject) request.getAttribute("servidor");
    boolean cancelar = (Boolean) request.getAttribute("cancelar");
    String voltar = (String) request.getAttribute("destinoBotaoVoltar");
%>

<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="title">
    <hl:message key="rotulo.nome.sistema"/> - <hl:message key="rotulo.convenios.bloq.titulo"/>
</c:set>

<c:set var="bodyContent">
    <div class="main-content">
        <div class="card">
            <div class="card-header">
                <h2 class="card-header-title"><hl:message key="mensagem.convenios.bloq.listagem"/></h2>
            </div>
            <div class="card-body">
                <dl class="row data-list">
                        <%-- Utiliza a tag library DetalheServidorTag.java para exibir os dados do servidor --%>
                    <% pageContext.setAttribute("servidor", servidor); %>
                    <hl:detalharServidorv4 name="servidor"/>
                        <%-- Fim dos dados da ADE --%>
                </dl>
            </div>
        </div>
            <%-- NATUREZAS SERVIÇO BLOQUEADOS --%>
        <div class="card">
            <div class="card-header">
                <h2 class="card-header-title"><hl:message key="rotulo.natureza.servico.titulo"/></h2>
            </div>
            <div class="card-body table-responsive p-0">
                <table class="table table-striped table-hover">
                    <thead>
                    <tr>
                        <th style='width: 15%;' scope="col"><hl:message key="rotulo.natureza.servico.titulo"/></th>
                        <th style='width: 15%;' scope="col"><hl:message key="rotulo.convenios.bloq.qtd"/></th>
                        <th style='width: 15%;' scope="col"><hl:message key="rotulo.convenios.bloq.obs"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        Iterator<?> itNSB = naturezasServicoBloqueados.iterator();
                        CustomTransferObject nsbTO;
                        int countNSB = 0;
                        while (itNSB.hasNext()) {
                            nsbTO = (CustomTransferObject) itNSB.next();
                            if (!com.zetra.econsig.helper.texto.TextHelper.isNull(nsbTO.getAttribute(Columns.PNR_VLR))) {
                                countNSB++;
                    %>
                    <tr>
                        <td><%=TextHelper.forHtmlContent(nsbTO.getAttribute(Columns.NSE_CODIGO))%>
                            - <%=TextHelper.forHtmlContent(nsbTO.getAttribute(Columns.NSE_DESCRICAO))%>
                        </td>
                        <td><%=TextHelper.forHtmlContent(nsbTO.getAttribute(Columns.PNR_VLR))%>
                        </td>
                        <td><%=TextHelper.forHtmlContent(nsbTO.getAttribute(Columns.PNR_OBS) != null ? nsbTO.getAttribute(Columns.PNR_OBS).toString() : "")%>
                        </td>
                    </tr>
                    <%
                            }
                        }
                    %>
                    <%=JspHelper.msgRstVazio(countNSB == 0, 3, responsavel)%>
                    </tbody>
                    <tfoot>
                    <tr>
                        <td colspan="3"><hl:message key="mensagem.listagem.natureza.servico.bloqueados"/></td>
                    </tr>
                    </tfoot>
                </table>
            </div>
        </div>
            <%-- SERVICOS BLOQUEADOS --%>
        <div class="card">
            <div class="card-header">
                <h2 class="card-header-title"><hl:message key="rotulo.servico.plural"/></h2>
            </div>
                <%-- SERVIÇOS BLOQUEADOS --%>
            <div class="card-body table-responsive p-0">
                <table class="table table-striped table-hover">
                    <thead>
                    <tr>
                        <th style='width: 15%;' scope="col"><hl:message key="rotulo.servico.singular"/></th>
                        <th style='width: 15%;' scope="col"><hl:message key="rotulo.convenios.bloq.qtd"/></th>
                        <th style='width: 15%;' scope="col"><hl:message key="rotulo.convenios.bloq.obs"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        Iterator<?> itSB = servicosBloqueados.iterator();
                        CustomTransferObject sbTO;
                        int countSB = 0;
                        while (itSB.hasNext()) {
                            sbTO = (CustomTransferObject) itSB.next();
                            if (!com.zetra.econsig.helper.texto.TextHelper.isNull(sbTO.getAttribute(Columns.PSR_VLR))) {
                                countSB++;
                    %>
                    <tr>
                        <td><%=TextHelper.forHtmlContent(sbTO.getAttribute(Columns.SVC_IDENTIFICADOR))%>
                            - <%=TextHelper.forHtmlContent(sbTO.getAttribute(Columns.SVC_DESCRICAO))%>
                        </td>
                        <td><%=TextHelper.forHtmlContent(sbTO.getAttribute(Columns.PSR_VLR))%>
                        </td>
                        <td><%=TextHelper.forHtmlContent(sbTO.getAttribute(Columns.PSR_OBS) != null ? sbTO.getAttribute(Columns.PSR_OBS).toString() : "")%>
                        </td>
                    </tr>
                    <%
                            }
                        }
                    %>
                    <%=JspHelper.msgRstVazio(countSB == 0, 3, responsavel)%>
                    </tbody>
                    <tfoot>
                    <tr>
                        <td colspan="3"><hl:message key="mensagem.listagem.servicos.bloqueados"/></td>
                    </tr>
                    </tfoot>
                </table>
            </div>
        </div>
            <%-- CONVÊNIOS BLOQUEADOS --%>
        <div class="card">
            <div class="card-header">
                <h2 class="card-header-title"><hl:message key="rotulo.convenio.plural"/></h2>
            </div>
            <div class="card-body table-responsive p-0">
                <table class="table table-striped table-hover">
                    <thead>
                    <tr>
                        <th style='width: 15%;' scope="col"><hl:message key="rotulo.servico.singular"/></th>
                        <th style='width: 15%;' scope="col"><hl:message key="rotulo.consignataria.singular"/></th>
                        <th style='width: 15%;' scope="col"><hl:message key="rotulo.convenios.bloq.qtd"/></th>
                        <th style='width: 15%;' scope="col"><hl:message key="rotulo.convenios.bloq.obs"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        Iterator<?> itCB = conveniosBloqueados.iterator();
                        CustomTransferObject cbTO;
                        while (itCB.hasNext()) {
                            cbTO = (CustomTransferObject) itCB.next();
                    %>
                    <tr>
                        <td><%=TextHelper.forHtmlContent(cbTO.getAttribute(Columns.CNV_COD_VERBA))%>
                            - <%=TextHelper.forHtmlContent(cbTO.getAttribute(Columns.SVC_DESCRICAO))%>
                        </td>
                        <td><%=TextHelper.forHtmlContent(cbTO.getAttribute(Columns.CSA_NOME))%>
                        </td>
                        <td><%=TextHelper.forHtmlContent(cbTO.getAttribute(Columns.PCR_VLR))%>
                        </td>
                        <td><%=TextHelper.forHtmlContent(cbTO.getAttribute(Columns.PCR_OBS) != null ? cbTO.getAttribute(Columns.PCR_OBS).toString() : "")%>
                        </td>
                    </tr>
                    <%
                        }
                    %>
                    <%=JspHelper.msgRstVazio(conveniosBloqueados.size() == 0, 4, responsavel)%>
                    </tbody>
                    <tfoot>
                    <tr>
                        <td colspan="3"><hl:message key="mensagem.listagem.convenios.bloqueados"/></td>
                    </tr>
                    </tfoot>
                </table>
            </div>
        </div>
    </div>
    <div class="btn-action">
        <% if (cancelar) { %>
        <a class="btn btn-outline-danger" href="#no-back"
           onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
        <% } else if (responsavel.isSer()) { %>
        <a class="btn btn-outline-danger" href="#no-back"
           onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
        <% } else { %>
        <a class="btn btn-outline-danger" href="#no-back"
           onClick="postData('<%=TextHelper.forJavaScriptAttribute(voltar)%>'); return false;"><hl:message
                key="rotulo.botao.voltar"/></a>
        <% } %>
    </div>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>