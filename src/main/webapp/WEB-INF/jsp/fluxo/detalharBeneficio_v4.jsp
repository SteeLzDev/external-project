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
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.helper.markdown.Markdown4jProcessorExtended"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%

String nseDescricao = (String) request.getAttribute("nseDescricao");
String provedorLink = (String) request.getAttribute("provedorLink");

TransferObject provedorBeneficio = (TransferObject) request.getAttribute("provedorBeneficio");

List<TransferObject> beneficios = (List<TransferObject>) request.getAttribute("beneficios");

String proTituloDetalheTopo = (String) provedorBeneficio.getAttribute(Columns.PRO_TITULO_DETALHE_TOPO);
String proTextoDetalheTopo = (String) provedorBeneficio.getAttribute(Columns.PRO_TEXTO_DETALHE_TOPO);

String proTituloListaBeneficio = (String) provedorBeneficio.getAttribute(Columns.PRO_TITULO_LISTA_BENEFICIO);

String proTituloDetalheRodape = (String) provedorBeneficio.getAttribute(Columns.PRO_TITULO_DETALHE_RODAPE);
String proTextoDetalheRodape = (String) provedorBeneficio.getAttribute(Columns.PRO_TEXTO_DETALHE_RODAPE);

String proLinkBeneficio = (String) provedorBeneficio.getAttribute(Columns.PRO_LINK_BENEFICIO);

String image = TextHelper.encode64Binary((byte[]) provedorBeneficio.getAttribute(Columns.PRO_IMAGEM_BENEFICIO));

String entPaiProvedorNome = !TextHelper.isNull(provedorBeneficio.getAttribute(Columns.COR_NOME)) ? (String) provedorBeneficio.getAttribute(Columns.COR_NOME) : (String) provedorBeneficio.getAttribute(Columns.CSA_NOME);

boolean provedorAgrupa = request.getAttribute("provedorAgrupa") != null && request.getAttribute("provedorAgrupa").toString().equals("true");
List<TransferObject> provedorCorrespondentes = (List<TransferObject>) request.getAttribute("provedorCorrespondentes");
List<TransferObject> beneficiosCorrespondentes = (List<TransferObject>) request.getAttribute("beneficiosCorrespondentes");

%>
<c:set var="title">
<%=TextHelper.forHtmlAttribute(nseDescricao)%>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent">

        <div class="row">
            <div class="col-sm">
                <div class="card">
                    <div class="card-header hasIcon">
                        <h2 class="card-header-title"><%=TextHelper.forHtmlAttribute(entPaiProvedorNome)%></h2>
                    </div>
                    <div class="card-body d-flex justify-content-center">
                        <div class="row">
                            <div class="col-6">
                                <img class="rounded img-thumbnail float-end mr-2" src="data:image/jpeg;charset=utf-8;base64,<%=TextHelper.forHtmlAttribute(image)%>" alt="<%=TextHelper.forHtmlAttribute(entPaiProvedorNome)%>">
                            </div>
                            <div class="col-6">
                                <% if (!TextHelper.isNull(proTituloDetalheTopo)) {%>
                                <div class="legend">
                                    <span><%=TextHelper.forHtmlAttribute(proTituloDetalheTopo)%></span>
                                </div>
                                <% } %>
                                <% if (!TextHelper.isNull(proTextoDetalheTopo)) { %>
                                <p><%=new Markdown4jProcessorExtended().process(proTextoDetalheTopo).toString()%></p>
                                <% } %>
                                
                                <%-- a lista de beneficios só devem aparecer se contiver um ou mais beneficio --%>
                                <% if (!provedorAgrupa && beneficios != null && beneficios.size() >= 1) { %>
                                
                                <div class="legend">
                                    <span><%=TextHelper.forHtmlAttribute(proTituloListaBeneficio)%></span>
                                </div>
                                
                                <% for (TransferObject transferObject : beneficios) {  %>
                                <p><%=TextHelper.forHtmlAttribute(transferObject.getAttribute(Columns.BEN_DESCRICAO))%></p>
                                <% } %>
                                
                                <% } %>
                                
                                <% if (!TextHelper.isNull(proTituloDetalheRodape)) { %>
                                <div class="legend">
                                    <span><%=TextHelper.forHtmlAttribute(proTituloDetalheRodape)%></span>
                                </div>
                                <% } %>
                                <% if (!TextHelper.isNull(proTextoDetalheRodape)) { %>
                                <p><%=new Markdown4jProcessorExtended().process(proTextoDetalheRodape).toString()%></p>
                                <% } %>
                            </div>
                        </div>
                    </div>
                </div>
                <%if(provedorAgrupa){ 
                    boolean ativaTab = true;
                %>
                    <ul class="nav nav-tabs" role="tablist">
                        <%for (TransferObject provedorCorrespondente : provedorCorrespondentes){
                            String corCodigo = (String) provedorCorrespondente.getAttribute(Columns.COR_CODIGO);
                            String corNome = (String) provedorCorrespondente.getAttribute(Columns.COR_NOME);
                        %>
                          <li class="nav-item">
                            <a class="nav-link <%=ativaTab ? "active" : "" %>" href="#<%=corCodigo%>" role="tab"  data-bs-toggle="tab"><%=corNome%></a>
                          </li>
                        <% 
                        ativaTab = false;
                    } 
                    ativaTab = true;
                    %>
                    </ul>
                    <div class="tab-content">
                    <%for (TransferObject provedorCor : provedorCorrespondentes){
                            String corCodigoProvedor = (String) provedorCor.getAttribute(Columns.COR_CODIGO);
                            String proCorTituloDetalheTopo = (String) provedorCor.getAttribute(Columns.PRO_TITULO_DETALHE_TOPO);
                            String proCorTextoDetalheTopo = (String) provedorCor.getAttribute(Columns.PRO_TEXTO_DETALHE_TOPO);
                            String proCorTituloDetalheRodape = (String) provedorCor.getAttribute(Columns.PRO_TITULO_DETALHE_RODAPE);
                            String proCorTextoDetalheRodape = (String) provedorCor.getAttribute(Columns.PRO_TEXTO_DETALHE_RODAPE);
                            String proCorLinkBeneficio = (String) provedorCor.getAttribute(Columns.PRO_LINK_BENEFICIO);
                    %>
                        <div  role="tabpanel" class="tab-pane fade <%=ativaTab ? "show active" : "" %>" id="<%=corCodigoProvedor%>">
                        <%if (!TextHelper.isNull(proCorTituloDetalheTopo)) {%>
                              <div class="legend">
                                  <span><%=TextHelper.forHtmlAttribute(proCorTituloDetalheTopo)%></span>
                              </div>
                        <% } %>
                        <% if (!TextHelper.isNull(proCorTextoDetalheTopo)) { %>
                             <p><%=new Markdown4jProcessorExtended().process(proCorTextoDetalheTopo).toString()%></p>
                        <% } %>
                                
                        <% if (!TextHelper.isNull(proCorTituloDetalheRodape)) { %>
                              <div class="legend">
                                  <span><%=TextHelper.forHtmlAttribute(proCorTituloDetalheRodape)%></span>
                              </div>
                        <% } %>
                        
                        <% if (!TextHelper.isNull(proCorTextoDetalheRodape)) { %>
                             <p><%=new Markdown4jProcessorExtended().process(proCorTextoDetalheRodape).toString()%></p>
                      <% } %>
                      <%for (TransferObject beneficioCorrespondente : beneficiosCorrespondentes){ 
                          String corCodigo = (String) beneficioCorrespondente.getAttribute(Columns.COR_CODIGO);
                          String benCodigo = (String) beneficioCorrespondente.getAttribute(Columns.BEN_CODIGO);
                          String benDescricao = (String) beneficioCorrespondente.getAttribute(Columns.BEN_DESCRICAO);
                          String benTextoCor = (String) beneficioCorrespondente.getAttribute(Columns.BEN_TEXTO_COR);
                          String benImagemBeneficio = TextHelper.encode64Binary((byte[]) beneficioCorrespondente.getAttribute(Columns.BEN_IMAGEM_BENEFICIO));
                          String benLinkBeneficio = (String) beneficioCorrespondente.getAttribute(Columns.BEN_LINK_BENEFICIO);
                          String benTextoLinkBeneficio = (String) beneficioCorrespondente.getAttribute(Columns.BEN_TEXTO_LINK_BENEFICIO);
                          String benLinkSimulaReserva = (String) beneficioCorrespondente.getAttribute("benLinkSimulaReserva");
                      %>
                        <%if(corCodigo.equals(corCodigoProvedor)){ %>
                            <div class="question">
                              <a class="question-head" style="background-image: none" onclick="mostraCollpase();" href="#<%=benCodigo%>" data-bs-toggle="collapse" aria-expanded="false" aria-controls="<%=benCodigo%>">
                                <%=TextHelper.forHtmlContent(benDescricao)%>
                              </a>
                              <div class="collapse" id="<%=benCodigo%>">
                                <div class="question-body">
                                  <%if(!TextHelper.isNull(benImagemBeneficio)){ %>
                                    <img class="rounded img-thumbnail float-end mr-2" src="data:image/jpeg;charset=utf-8;base64,<%=TextHelper.forHtmlAttribute(benImagemBeneficio)%>" alt="<%=TextHelper.forHtmlAttribute(benDescricao)%>">
                                  <%} %>
                                  <p><%=benTextoCor%></p>
                                  <% if (!TextHelper.isNull(benLinkSimulaReserva)) { %>
                                      <a class="btn btn-success" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(benLinkSimulaReserva, request))%>')">
                                      <%=benLinkSimulaReserva.contains("simular") ? ApplicationResourcesHelper.getMessage("rotulo.botao.simule.agora", AcessoSistema.getAcessoUsuarioSistema()) : ApplicationResourcesHelper.getMessage("rotulo.botao.reservar.agora", AcessoSistema.getAcessoUsuarioSistema()) %>
                                      </a>
                                  <% } %>
                                  <%if(!TextHelper.isNull(benLinkBeneficio)){ %>
                                      <a class="btn btn-success" href="<%=TextHelper.forHtmlAttribute(benLinkBeneficio)%>" target="_blank">
                                        <svg width="17"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar"></use></svg>
                                        <%=!TextHelper.isNull(benTextoLinkBeneficio) ? benTextoLinkBeneficio : ApplicationResourcesHelper.getMessage("rotulo.botao.saiba.mais", AcessoSistema.getAcessoUsuarioSistema())%>
                                      </a>
                                  <%} else if(!TextHelper.isNull(proCorLinkBeneficio)){ %>
                                      <a class="btn btn-success" href="<%=TextHelper.forHtmlAttribute(proCorLinkBeneficio)%>" target="_blank">
                                        <svg width="17"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar"></use></svg>
                                        <hl:message key="rotulo.botao.saiba.mais"/> 
                                      </a>
                                  <%} %>
                               </div>
                              </div>
                            </div>
                        <%} %>
                       <%} %>
                      </div>
                    <%
                    ativaTab = false; 
                    } %>
                  </div>
                <%} %>
                <div class="float-end">
                    <div class="btn-action">
                        <a class="btn btn-outline-danger" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
                        
                        <%if(!provedorAgrupa){ %>
                          <% if (!TextHelper.isNull(provedorLink)) { %>
                          <a class="btn btn-success" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(provedorLink, request))%>')">
                              <%=provedorLink.contains("simular") ? ApplicationResourcesHelper.getMessage("rotulo.botao.simule.agora", AcessoSistema.getAcessoUsuarioSistema()) : ApplicationResourcesHelper.getMessage("rotulo.botao.reservar.agora", AcessoSistema.getAcessoUsuarioSistema()) %>
                          </a>
                          <% } %>
                          <% if (!TextHelper.isNull(proLinkBeneficio)) { %>
                          <a class="btn btn-success" href="<%=TextHelper.forHtmlAttribute(proLinkBeneficio)%>" target="_blank">
                              <svg width="17"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar"></use></svg>    
                              <hl:message key="rotulo.botao.saiba.mais"/> 
                          </a>
                          <% } %>
                        <%} %>
                    </div>
                </div>
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

    function mostraCollpase() {
  	  $('.collapse').collapse("hide");
    }
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
