<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.persistence.entity.NaturezaServico" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
boolean exibeSimulacaoPlano = request.getAttribute("exibeSimulacaoPlano") != null ? (Boolean) request.getAttribute("exibeSimulacaoPlano") : false;
boolean exibiConsultaExtratoPlanoSaude = request.getAttribute("exibiConsultaExtratoPlanoSaude") != null ? (Boolean) request.getAttribute("exibiConsultaExtratoPlanoSaude") : false;
boolean exibirSimularAlteracao = request.getAttribute("exibirSimularAlteracao") != null ? (Boolean) request.getAttribute("exibirSimularAlteracao") : false;
boolean isBeneficioSaude = request.getAttribute("isBeneficioSaude") != null ? (Boolean) request.getAttribute("isBeneficioSaude") : false;
String nseCodigoPai = (String) request.getAttribute("nseCodigoPai");
boolean isModuloBeneficiosSaudeHabilitado = request.getAttribute("isModuloBeneficiosSaudeHabilitado") != null ? (Boolean) request.getAttribute("isModuloBeneficiosSaudeHabilitado") : false;
boolean isPortalBeneficiosHabilitado = request.getAttribute("isPortalBeneficiosHabilitado") != null ? (Boolean) request.getAttribute("isPortalBeneficiosHabilitado") : false;

List<NaturezaServico> lstNseTo = (List<NaturezaServico>) request.getAttribute("lstNseTo");
session.setAttribute("naturezasFilhas", lstNseTo);
session.setAttribute("nse", request.getAttribute("nse"));
%>
<c:set var="title">
    <span class="company"><hl:message key="mensagem.principal.titulo"/></span><br><span class="page-title"><%=TextHelper.forHtmlContent(request.getAttribute("nseDescricaoPai")) %></span>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-beneficios"></use>  
</c:set>
<c:set var="bodyContent">
     <%if (isModuloBeneficiosSaudeHabilitado && isBeneficioSaude && (exibeSimulacaoPlano || exibiConsultaExtratoPlanoSaude || exibirSimularAlteracao)) {%>
       <div class="row shortcut-btns">
          <%if (exibeSimulacaoPlano) {%>
            <div class="col-md-6 col-lg-4 col-xl-3">
              <a class="btn" href="#no-back" onclick="javascript:postData('../v3/simulacaoBeneficios?acao=planoSaude')"> <svg width="47">
                <use xlink:href="#i-simular"></use>
                </svg> <hl:message key="rotulo.dashboard.simular.adesao.plano"/>
              </a>
            </div>
          <%} %>
          <%if (exibiConsultaExtratoPlanoSaude) {%>
            <div class="col-md-6 col-lg-4 col-xl-3">
              <a class="btn" href="#no-back" onclick="javascript:postData('../v3/relacaoBeneficios?acao=listar')"> <svg width="47">
                <use xlink:href="#i-consultar"></use>
                </svg> <hl:message key="rotulo.dashboard.consultar.planos"/>
              </a>
            </div>
          <%} %>
          <%if (exibeSimulacaoPlano) {%>
            <div class="col-md-6 col-lg-4 col-xl-3">
               <a class="btn" href="#no-back" onclick="javascript:postData('../v3/incluirBeneficiarioSimulacaoBeneficios?acao=planoSaude')"> <svg width="47">
                 <use xlink:href="#i-colaboradores"></use>
                 </svg> <hl:message key="rotulo.dashboard.incluir.beneficiarios"/>
               </a>
            </div>
          <%} %>
          <%if (exibirSimularAlteracao) {%>
            <div class="col-md-6 col-lg-4 col-xl-3">
              <a class="btn" href="#no-back" onclick="javascript:postData('../v3/simulacaoAlteracaoBeneficios?acao=selecionarBeneficio')"> <svg width="47">
                <use xlink:href="#i-editar"></use>
                </svg> <hl:message key="rotulo.dashboard.simular.alteracao.plano"/>
              </a>
            </div>
          <%} %>
       </div>
     <%} %>
     <%if (isPortalBeneficiosHabilitado) { %>
     <div class="row">
        <div class="col">
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><%=request.getAttribute("tituloCarousel") %></h2>
            </div>
            <div class="card-body pb-0 d-flex justify-content-center">
              <div id="multi-item-example" class="carousel slide carousel-multi-item" data-ride="carousel">
                  <!--Indicators-->
                  <ol class="carousel-indicators">                    
                    <%
                       int numPgCarousel = (Integer) request.getAttribute("numPgCarousel"); 
                       int itensPorPgCarousel = (Integer) request.getAttribute("itensPorPgCarousel");
                       for (int i = 1; i <= numPgCarousel; i++) {%>
                         <li data-bs-target="#multi-item-example" data-bs-slide-to="#natureza_slide_<%=i%>" <%if (i == 1) {%> class="active"<%} %>></li>
                    <% } %>
                  </ol>
                  <!--/.Indicators-->
                <div class="row">
                  <div class="carousel-inner" role="listbox">
                    <!--First slide-->
                    <%int itemCarouselCount = 0;
                      for (int i = 1; i <= numPgCarousel; i++) {%>                    
                       <div id="natureza_slide_<%=i%>" class="carousel-item <%if (i == 1) {%> active<%} %>">
                         <div class="row d-flex justify-content-center">
                           <%for (int j = 1; j <= itensPorPgCarousel; j++) {%>
                           
                             <%if (itemCarouselCount < lstNseTo.size()) {%>
                               <div class="col-sm-3">
                                 <div class="card card-image image-2 card-carossel mb-5" onClick="javascript:postData('../v3/fluxoPortal?nse_codigo=<%=TextHelper.forUriComponent(lstNseTo.get(itemCarouselCount).getNseCodigo())%>&<%=SynchronizerToken.generateToken4URL(request)%>')">
                                     <img id="img_car_<%=TextHelper.forHtmlAttribute(lstNseTo.get(itemCarouselCount).getNseCodigo())%>" class="card-img-top only" src="" alt="Card image cap">                                                                        
                                     <div class="card-body text-center">
                                       <b><%=TextHelper.forHtmlContent(lstNseTo.get(itemCarouselCount).getNseDescricao())%></b>
                                     </div>
                                 </div>
                               </div>
                               <%itemCarouselCount++; %>
                             <%} %>
                           
                           <%} %>
                         </div>
                       </div>  
                    <% }%>
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
            </div>                   
          </div>
          <div class="float-end">
            <div class="btn-action">
                <a class="btn btn-outline-danger" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>                
            </div>
          </div>
         </div>
     </div>
     <%} %>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
$(document).ready(function() {
      let idImg = '';
    <% for (NaturezaServico nse: lstNseTo) { %>
          idImg = <%=nse.getNseCodigo()%>;
          $.ajax({
            url: "../v3/imagemCarousel?nseCodigo=<%=nse.getNseCodigo()%>&_skip_history_=true",
            type: "POST",
            contentType: "image/jpeg",
            success: function(data){
               $('#img_car_<%=nse.getNseCodigo()%>').attr("src","data:image/jpeg;base64," + data); },                     
            });    
    <% } %>  
});
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