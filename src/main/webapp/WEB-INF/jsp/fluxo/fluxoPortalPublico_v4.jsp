<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="fl" uri="/function-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="show" uri="/showfield-lib"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.regex.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.values.TipoFiltroPesquisaFluxoEnum"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%

String nseDescricao = (String) request.getAttribute("nseDescricao");

List<TransferObject> consignatarias = (List<TransferObject>) request.getAttribute("consignatarias");
String linkSimuleAgora = (String) request.getAttribute("linkSimuleAgora");
String nseCodigo = (String) request.getAttribute("nse_codigo");
String nFiltrarPor = request.getParameter("nFiltrarPor");

%>
<c:set var="title">
  <%=TextHelper.forHtmlAttribute(nseDescricao)%>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-home"></use>
</c:set>
<c:set var="bodyContent">


        <div class="card">
            <div class="card-header hasIcon">
                <h2 class="card-header-title"><%=TextHelper.forHtmlAttribute(nseDescricao)%></h2>
            </div>
            <div class="card-body">
                <div class="row d-flex justify-content-center z-index-position">
                    <div class="col-sm mr-4 ml-4">
                        <div class="row">
                            <div class="col-sm-12 col-md-12 mb-4 ">
                                <form action="../v3/fluxoPortal?acao=pesquisar&<%=SynchronizerToken.generateToken4URL(request)%>" method="post" class="search-form">
                                    <input type="hidden" name="nse_codigo" value="${param.nse_codigo}">
                                    <input type="hidden" name="_skip_history_" value="true">
                                    <div class="row">
                                        <div class="form-group col-sm-2 ml-0 pl-0 mr-0 pr-0">
                                            <select class="form-control select form-select custom-select" id="iFiltrarPor" name="nFiltrarPor">
                                                <optgroup label="<hl:message key="rotulo.fluxo.portal.filtros"/>">
                                                <% for (TipoFiltroPesquisaFluxoEnum tipo : TipoFiltroPesquisaFluxoEnum.values()) { %>
                                                    <option value="<%=tipo.getCodigo()%>" <%=tipo.getCodigo().equals(nFiltrarPor) ? "selected" : "" %>><hl:message key="<%=tipo.getRotulo()%>"/></option>
                                                <% } %>
                                                </optgroup>
                                            </select>
                                        </div>
                                        <div class="form-group col-sm ml-0 pl-0 mr-0 pr-0">
                                            <input type="text" class="form-control" id="iFiltro" name="nFiltro" value="${param.nFiltro}" placeholder="<hl:message key="rotulo.fluxo.portal.oque.esta.procurando"/>" maxlength="100">
                                        </div>
                                        <div class="col-sm-0  ml-0 pl-0 mr-0 pr-0">
                                            <div class="float-end">
                                                <div class="btn-action ml-0">
                                                    <button class="btn btn-primary ">
                                                      <svg width="17"><use xlink:href="#i-consultar"></use></svg>
                                                      <hl:message key="rotulo.botao.pesquisar"/>
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <%
                if (!consignatarias.isEmpty()) {
                %>  
                <div id="multi-item-example" class="carousel slide carousel-multi-item" data-ride="carousel">
                    <ol class="carousel-indicators">
                        <%
                          int numItens = consignatarias.size() / 3;
                          if (consignatarias.size() % 3 != 0) {
                              numItens ++;
                          }
                        %>
                        <% for (int i = 0; i < numItens; i++) { %>
                            <li data-bs-target="#multi-item-example" data-bs-slide-to="<%=i%>" class="<%=i == 0 ? "active" : ""%>"></li>
                        <% } %>
                    </ol>
                    <div class="carousel-inner" role="listbox">
                    
                        <div class="carousel-item active">
                            <div class="row d-flex justify-content-center">
                        <%
                        for (int i = 0; i < consignatarias.size(); i++) {
                            TransferObject consignataria  = consignatarias.get(i);
                            
                            String image = TextHelper.encode64Binary((byte[]) consignataria.getAttribute(Columns.PRO_IMAGEM_BENEFICIO));
                            String proCodigo = (String) consignataria.getAttribute(Columns.PRO_CODIGO);
                            String csaCodigo = (String) consignataria.getAttribute(Columns.CSA_CODIGO);
                            boolean proAgrupa = consignataria.getAttribute(Columns.PRO_AGRUPA) != null && consignataria.getAttribute(Columns.PRO_AGRUPA).toString().equals("S");
                            
                            if (i != 0 && i % 3 == 0) {
                        %>
                            </div>
                        </div>
                        <div class="carousel-item">
                            <div class="row d-flex justify-content-center">
                        <%  } %>
                            
                                <div class="col-sm-3">
                                    <div class="card card-image image-2 card-carossel-fluxo mb-5" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/fluxoPortal?acao=detalharBeneficio&pro_codigo=" + proCodigo + "&nse_codigo=" + nseCodigo + "&csa_codigo=" + csaCodigo + "&pro_agrupa=" + proAgrupa, request))%>')">
                                        <img class="card-img-top" src="data:image/jpeg;charset=utf-8;base64,<%=TextHelper.forHtmlAttribute(image)%>" alt="<%=TextHelper.forHtmlAttribute(consignataria.getAttribute(Columns.CSA_NOME))%>">
                                        <div class="card-body text-center carossel-body">
                                            <%=TextHelper.forHtmlAttribute(consignataria.getAttribute(Columns.PRO_TEXTO_CARD_BENEFICIO))%>
                                        </div>
                                    </div>
                                </div>
                            
                        <% } %>
                            </div>
                        </div>
                    </div>
                    <a class="carousel-control-prev" href="#multi-item-example" role="button" data-bs-slide="prev">
                        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                        <span class="sr-only">Previous</span>
                    </a>
                    <a class="carousel-control-next" href="#multi-item-example" role="button" data-bs-slide="next">
                        <span class="carousel-control-next-icon" aria-hidden="true"></span>
                        <span class="sr-only">Next</span>
                    </a>
                </div>
                <%
                } else {
                %>
                <div class="row">
                  <div class="col-sm-12 col-md-12 mb-4">
                    <p class="text-center font-italic"><hl:message key="mensagem.datatables.info.empty"/></p>
                  </div>
                </div>
                <% 
                } 
                %>
            </div>
        </div>
        <div class="float-end">
            <div class="btn-action">
                <a class="btn btn-outline-danger" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
                <% if (!TextHelper.isNull(linkSimuleAgora)) { %>
                <a class="btn btn-success" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(linkSimuleAgora, request))%>')"><hl:message key="rotulo.botao.simule.agora"/></a>
                <% } %>
            </div>
        </div>


</c:set>
<c:set var="javascript">
  <script type="text/javascript">
    <%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    %>
    <% if (responsavel.isSer()) { %>
    var rseCodigo = '<%=TextHelper.forJavaScript(responsavel.getRseCodigo())%>';
    var pageToken = '<%=SynchronizerToken.getSessionToken(request)%>';
    <%}%>
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
