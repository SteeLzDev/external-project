<%--
* <p>Title: portabilidade</p>
* <p>Description: Página de listagem de csas que podem comprar contrato de portabilidade de cartao reserva</p>
* <p>Copyright: Copyright (c) 2024</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="java.util.List" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    List<TransferObject> csas = (List<TransferObject>) request.getAttribute("csas");
    String rseCodigo = (String) request.getAttribute("rseCodigo");
    String rseMatricula = (String) request.getAttribute("rseMatricula");
    String svcCodigo = (String) request.getAttribute("svcCodigo");
    String adeCodigo = (String) request.getAttribute("adeCodigo");
    boolean temCET = request.getAttribute("temCET") != null && (Boolean) request.getAttribute("temCET");
%>
<c:set var="title">
    <hl:message key="mensagem.solicitar.portabilidade.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
    <div class="card">
        <div class="card-header hasIcon">
    <span class="card-header-icon">
      <svg width="24">
          <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-consignacao"></use>
      </svg>
    </span>
            <h2 class="card-header-title">
                <hl:message key="rotulo.consignacao.plural"/>
            </h2>
        </div>
        <div class="tab-content table-responsive">
            <div role="tabpanel" class="tab-pane fade show active">
                <table class="table table-striped table-hover">
                    <thead>
                    <tr>
                        <th scope="col"><hl:message key="rotulo.consignacao.ranking"/></th>
                        <th><hl:message key="rotulo.consignataria.codigo"/></th>
                        <th><hl:message key="rotulo.consignataria.singular"/></th>
                        <% if (temCET) { %>
                        <th scope="col"><hl:message key="rotulo.consignacao.cet"/></th>
                        <% } else { %>
                        <th scope="col"><hl:message key="rotulo.consignacao.taxa.juros"/></th>
                        <% } %>
                        <th><hl:message key="rotulo.acoes"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        int ranking = 0;
                        String codigo;
                        String nome;
                        String csaCodigo;
                        String taxa;
                        for (TransferObject csa : csas) {
                            ranking++;
                            codigo = (String) csa.getAttribute(Columns.CSA_IDENTIFICADOR);
                            nome = (String) csa.getAttribute(Columns.CSA_NOME);
                            csaCodigo = (String) csa.getAttribute(Columns.CSA_CODIGO);
                            taxa = NumberHelper.format(((BigDecimal) csa.getAttribute(Columns.CFT_VLR)).doubleValue(), NumberHelper.getLang(), 2, 8);
                    %>
                    <tr>
                        <td>
                            <span class="p-2">
                                <svg class="i-disponivel">
                                    <use xlink:href="#i-status-v"></use>
                                </svg>
                                    <%=TextHelper.forHtmlContent(ranking)%>
                                    <span class="ordinal"></span>
                            </span>
                        </td>
                        <td><%=TextHelper.forHtmlContent(codigo)%></td>
                        <td><%=TextHelper.forHtmlContent(nome)%></td>
                        <td><%=TextHelper.forHtmlContent(taxa)%>%</td>
                        <td>
                            <div class="actions"><a class="ico-action" href="#" role="button" onclick="reservar('<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>');">
                                <div class="form-inline"><span class="mr-1"><svg><use
                                        xlink:href="#i-confirmar"></use></svg></span><hl:message key="rotulo.acoes.selecionar"/></div>
                            </a></div>
                        </td>
                    </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <div class="float-end">
        <div class="btn-action">
            <a class="btn btn-outline-danger" href="#no-back"
               onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message
                    key="rotulo.botao.voltar"/></a>
        </div>
    </div>
</c:set>
<c:set var="javascript">
    <script type="text/javascript">

        function reservar(csaCodigoDestino) {
            var URL = '../v3/solicitarPortabilidade?acao=solicitarPortabilidadeCartao'
                + '&CSA_CODIGO=' + csaCodigoDestino
                + '&SVC_CODIGO=<%=TextHelper.forJavaScriptBlock(svcCodigo)%>'
                + '&RSE_CODIGO=<%=TextHelper.forJavaScriptBlock(rseCodigo)%>'
                + '&RSE_MATRICULA=<%=TextHelper.forJavaScriptBlock(rseMatricula)%>'
                + '&ADE_CODIGO=<%=TextHelper.forJavaScriptBlock(adeCodigo)%>'
                + '&<%= SynchronizerToken.generateToken4URL(request) %>';
            postData(URL);
        }
    </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>