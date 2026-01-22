<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="fl" uri="/function-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="show" uri="/showfield-lib"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.regex.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.values.TipoFiltroPesquisaFluxoEnum"%>
<%@ page import="com.zetra.econsig.helper.markdown.Markdown4jProcessorExtended"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
String nFiltrarPor = request.getParameter("nFiltrarPor");

String image = TextHelper.encode64Binary((byte[]) request.getAttribute("nseImagem"));
String nseDescricaoPortal = (String) request.getAttribute("nseDescricaoPortal");
String nseTituloDetalheTopo = (String) request.getAttribute("nseTituloDetalheTopo");
String nseTextoDetalheTopo = (String) request.getAttribute("nseTextoDetalheTopo");
String nseTituloDetalheRodape = (String) request.getAttribute("nseTituloDetalheRodape");
String nseTextoDetalheRodape = (String) request.getAttribute("nseTextoDetalheRodape");
String nseTituloCarouselProvedor = (String) request.getAttribute("nseTituloCarouselProvedor");

List<TransferObject> lstBannersTo = (List<TransferObject>) request.getAttribute("itensBannersCarousel");
%>

<c:set var="title">
  <%=TextHelper.forHtmlAttribute(nseDescricaoPortal)%>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-home"></use>
</c:set>
<c:set var="bodyContent">
    <div class="card">
        <div class="card-header">
            <h2 class="card-header-title text-uppercase"><%=TextHelper.forHtmlAttribute(nseDescricaoPortal)%></h2>
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
                                        <select class="form-control form-select select custom-select" id="iFiltrarPor" name="nFiltrarPor">
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
                                                <button class="btn btn-primary btn-pesquisa ml-0">
                                                  <svg width="17"><use xlink:href="#i-consultar"></use></svg>
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
            <div class="row d-flex justify-content-center">
              <div class="col-sm-2">
                  <img class="rounded img-thumbnail mr-2 ml-2" src="data:image/jpeg;charset=utf-8;base64,<%=TextHelper.forHtmlAttribute(image)%>" alt="<%=TextHelper.forHtmlAttribute(nseDescricaoPortal)%>">
              </div>
              <div class="col-sm">
                  <% if (!TextHelper.isNull(nseTituloDetalheTopo)) { %>
                    <div class="legend">
                        <span><%=TextHelper.forHtmlAttribute(nseTituloDetalheTopo)%></span>
                    </div>
                  <% } %>

                  <% if (!TextHelper.isNull(nseTextoDetalheTopo)) { %>
                    <p><%=new Markdown4jProcessorExtended().process(nseTextoDetalheTopo).toString()%></p>
                  <% } %>

                  <% if (!TextHelper.isNull(nseTituloDetalheRodape)) { %>
                  <div class="legend">
                      <span><%=TextHelper.forHtmlAttribute(nseTituloDetalheRodape)%></span>
                  </div>
                  <% } %>

                  <%-- // cadastrar como tag <a> e <img> --%>
                  <% if (!TextHelper.isNull(nseTextoDetalheRodape)) { %>
                      <p><%=new Markdown4jProcessorExtended().process(nseTextoDetalheRodape).toString()%></p>
                  <% } %>
              </div>
            </div>
        </div>
    </div>

    <%  
    if (lstBannersTo != null && !lstBannersTo.isEmpty()) {
    %>
    <div class="card">
        <div class="card-header">
            <h2 class="card-header-title text-uppercase"><%=TextHelper.forHtmlAttribute(nseTituloCarouselProvedor)%></h2>
        </div>
        <div class="row">
            <div class="card-body">
                <div id="multi-item-banners" class="carousel slide carousel-multi-item" data-ride="carousel">
                    <!--Indicators-->
                    <ol class="carousel-indicators">                    
                      <%
                         int numPgCarouselBanners = (Integer) request.getAttribute("numPgCarouselBanners"); 
                         int itensPorPgCarouselBanners = (Integer) request.getAttribute("itensPorPgCarouselBanners");
                         for (int i = 1; i <= numPgCarouselBanners; i++) {%>
                           <li data-bs-target="#multi-item-banners" data-bs-slide-to="#banners_slide_<%=i%>" <%if (i == 1) {%> class="active"<%} %>></li>
                      <% } %>
                    </ol>
                    <!--/.Indicators-->
                    <div class="row">
                      <div class="carousel-inner" role="listbox">
                        <!--First slide-->
                        <%int itemCarouselCount = 0;
                          for (int i = 1; i <= numPgCarouselBanners; i++) { %>                    
                           <div id="banners_slide_<%=i%>" class="carousel-item <%if (i == 1) { %> active<% } %>">
                             <div class="row d-flex justify-content-center">
                               <%for (int j = 1; j <= itensPorPgCarouselBanners; j++) { %>
                               
                                 <%if (itemCarouselCount < lstBannersTo.size()) {
                                   TransferObject bannersTO = lstBannersTo.get(itemCarouselCount);
                                 %>
                                   <div class="col-sm-3">
                                     <div class="card card-image image-2 card-carossel mb-5" onClick="javascript: window.open('<%=TextHelper.forHtmlAttribute(bannersTO.getAttribute(Columns.BPU_URL_SAIDA))%>');">
                                         <img id="img_car_banners_<%=TextHelper.forHtmlAttribute(bannersTO.getAttribute(Columns.BPU_CODIGO))%>" class="card-img-top only" src="data:image/jpeg;charset=utf-8;base64,<%=TextHelper.forHtmlAttribute(bannersTO.getAttribute(Columns.ARQ_CONTEUDO))%>" alt="<%=TextHelper.forHtmlAttribute(bannersTO.getAttribute(Columns.BPU_DESCRICAO))%>">
                                         <div class="card-body text-center">
                                            <b><%= TextHelper.forHtml(bannersTO.getAttribute(Columns.BPU_DESCRICAO))%></b>
                                         </div>
                                     </div>
                                   </div>
                                   <%itemCarouselCount++;%>
                                 <% } %>
                               
                               <% } %>
                             </div>
                           </div>
                        <% } %>
                      </div>                  
                    </div>                            
                    <a class="carousel-control-prev" href="#multi-item-banners" role="button" data-bs-slide="prev">
                      <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                      <span class="sr-only">Previous</span>
                    </a>
                    <a class="carousel-control-next" href="#multi-item-banners" role="button" data-bs-slide="next">
                      <span class="carousel-control-next-icon" aria-hidden="true"></span>
                      <span class="sr-only">Next</span>
                    </a>
                </div>
            </div>
        </div>
    </div>
    <%  
    }
    %>
    
    <div class="float-end">
        <div class="btn-action">
            <a class="btn btn-outline-danger" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
        </div>
    </div>
</c:set>
<c:set var="javascript">
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
