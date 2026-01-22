<%@page import="com.zetra.econsig.dto.web.RankServicoDTO"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.math.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.dto.entidade.PrazoTransferObject"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%

AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List prazos = (List) request.getAttribute("prazos");
boolean simulacaoPorTaxaJuros = (boolean) request.getAttribute("simulacaoPorTaxaJuros");
boolean temCET = (boolean) request.getAttribute("temCET");
boolean simulacaoMetodoMexicano = (boolean) request.getAttribute("simulacaoMetodoMexicano");
boolean simulacaoMetodoBrasileiro = (boolean) request.getAttribute("simulacaoMetodoBrasileiro");
String svc_codigo = (String) request.getAttribute("svc_codigo");
String titulo = (String) request.getAttribute("titulo");
int maxSizeRanking = (int) (request.getAttribute("maxSizeRanking") != null ? request.getAttribute("maxSizeRanking") : 0);
ArrayList<ArrayList<RankServicoDTO>> listaSeparada = (ArrayList<ArrayList<RankServicoDTO>>) request.getAttribute("listaSeparada");

//Exibe Botao Rodapé
boolean exibeBotaoRodape = request.getAttribute("exibeBotaoRodape") != null && (boolean) request.getAttribute("exibeBotaoRodape");
%>
<c:set var="imageHeader">
    <use xlink:href="#i-manutencao"></use>  
</c:set>
<c:set var="title">
    <hl:message key="rotulo.taxa.juros.ranking.simulacao.titulo"/>
</c:set>
<c:set var="bodyContent">

<form NAME="form1" METHOD="post" ACTION="../v3/visualizarRankingServico?<%=SynchronizerToken.generateToken4URL(request)%>">
    <% if (prazos.size() == 0) { %>
    <div class="card">
        <div class="card-header hasIcon">
          <span class="card-header-icon"><svg width="25"><use xlink:href="#i-simular-config"></use></svg></span>
          <h2 class="card-header-title"><hl:message key="rotulo.taxa.juros.ranking.titulo"  arg0="<%=TextHelper.forHtmlAttribute(titulo.toUpperCase())%>"/> </h2>
        </div>
        <div class="card-body">
          <hl:message key="mensagem.taxa.juros.ranking.prazo.nao.encontrado"/>
      </div>
      </div>
    <% } else { %>
  <div class="card">
        <div class="card-header hasIcon">
          <span class="card-header-icon"><svg width="25"><use xlink:href="#i-simular-config"></use></svg></span>
          <h2 class="card-header-title"><hl:message key="rotulo.taxa.juros.ranking.titulo"  arg0="<%=TextHelper.forHtmlAttribute(titulo.toUpperCase())%>"/> </h2>
        </div>
        <div class="card-body table-responsive p-0">
            <div class="alert-info p-2"><hl:message key="mensagem.coeficiente.simulacao.valor.liberado"/></div>
          <table class="table table-striped table-hover table-ranking">
                  
                     <%
                     int prz_vlr = 0;
                     PrazoTransferObject pto = null;
                     for(int i  = 0; i < prazos.size(); i++){
                       pto = (PrazoTransferObject)prazos.get(i);  
                       prz_vlr = pto.getPrzVlr();
                       for(int j=0; j < listaSeparada.get(i).size();j++){
                           RankServicoDTO dto = listaSeparada.get(i).get(j);
                           %>
                         <tr>
                           <% if(j == 0){ %>
                              <thead>
                               <tr>
                                <th scope="col" width="4%"><%=ApplicationResourcesHelper.getMessage("rotulo.prazo.plural", responsavel)%></th>
                                <th scope="col"><%=ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)%></th>
                              <%if (!simulacaoPorTaxaJuros) {%>
                                  <th scope="col"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.coeficiente.abreviado", responsavel)%></th>
                              <%} else if (temCET) { %>
                                <th scope="col"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet", responsavel)%></th>
                                <th scope="col"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet.anual", responsavel)%></th>
                              <% } else {%>
                                <th scope="col"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros", responsavel)%></th>
                              <%}%>
                                <th scope="col"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela.abreviado.moeda", responsavel)%></th>
          
                               <%  if (!temCET && simulacaoPorTaxaJuros && (!simulacaoMetodoMexicano || !simulacaoMetodoBrasileiro)) {%>
                                <th scope="col"><%=ApplicationResourcesHelper.getMessage(simulacaoMetodoMexicano ? "rotulo.consignacao.valor.cat.abreviado" : "rotulo.consignacao.valor.tac.abreviado.moeda", responsavel)%></th>
                                <th scope="col"><%=ApplicationResourcesHelper.getMessage(simulacaoMetodoMexicano ? "rotulo.consignacao.valor.iva.abreviado" : "rotulo.consignacao.valor.iof.abreviado.moeda", responsavel)%></th>
                              <% }%>
                                </tr>
                              </thead>
                              <tbody>
                                <tr>
                                  <td align="center" class="no-hover table-secundary" rowspan="<%=maxSizeRanking%>"><b><%=prz_vlr%></b></td>
                              <% }%> 
                                   <td> <%=dto.getCsaNomeLst().toUpperCase()%></td>
                                   <td><%=dto.getStrCftVlr()%></td>
                                 <% if (temCET && simulacaoPorTaxaJuros) { %>
                                   <td><%=dto.getCetAnual() %></td>
                                <% }%>
                                   <td><%=dto.getVlrParcela() %></td>
                                 <% if (!temCET && simulacaoPorTaxaJuros && (!simulacaoMetodoMexicano || !simulacaoMetodoBrasileiro)) {%>
                                   <td><%=simulacaoMetodoMexicano ? dto.getCat() : dto.getTac()%></td>
                                   <td><%= simulacaoMetodoMexicano ? dto.getIva() : dto.getIof()%></td>
                                 <%} %>
                               </tr>
                               <%}  
                               }%>
                           </tbody>
                        <tfoot>
                          <tr>
                          <td colspan="6">
                          <hl:message key="rotulo.taxa.juros.listagem.ranking"/>
                          </td>
                        </tr>
                     </tfoot>
                   </table>
               </div>
           </div>
         <%} %>
       <div class="btn-action">
          <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
       </div>  
  </form>
    <% if (exibeBotaoRodape) { %>
    <div id="btns">
        <a id="page-up" onclick="up()">
            <svg xmlns="http://www.w3.org/2000/svg" width="147.344" height="147.344" viewBox="0 0 147.344 147.344">
                <path id="União_3" data-name="União 3" d="M-20,60.672a73.672,73.672,0,1,1,73.672,73.672A73.66,73.66,0,0,1-20,60.672ZM9.61,16.61a62.252,62.252,0,0,0,0,88.124,62.252,62.252,0,0,0,88.124,0,62.252,62.252,0,0,0,0-88.124,62.252,62.252,0,0,0-88.124,0ZM49.174,88.087,15.278,54.3a6.85,6.85,0,0,1,9.67-9.706L53.672,73.6,82.362,45.336A6.825,6.825,0,1,1,92,55.005L58.6,88.3a6.827,6.827,0,0,1-9.421-.212Z" transform="translate(20 13)"/>
            </svg>
        </a>
        <a id="page-down" onclick="down()">
            <svg xmlns="http://www.w3.org/2000/svg" width="147.344" height="147.344" viewBox="0 0 147.344 147.344">
                <path id="União_3" data-name="União 3" d="M-20,60.672a73.672,73.672,0,1,1,73.672,73.672A73.66,73.66,0,0,1-20,60.672ZM9.61,16.61a62.252,62.252,0,0,0,0,88.124,62.252,62.252,0,0,0,88.124,0,62.252,62.252,0,0,0,0-88.124,62.252,62.252,0,0,0-88.124,0ZM49.174,88.087,15.278,54.3a6.85,6.85,0,0,1,9.67-9.706L53.672,73.6,82.362,45.336A6.825,6.825,0,1,1,92,55.005L58.6,88.3a6.827,6.827,0,0,1-9.421-.212Z" transform="translate(20 13)"/>
            </svg>
        </a>
    </div>
    <% }%>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
<script>
    <% if (exibeBotaoRodape) { %>
    let btnDown = document.querySelector('#btns');
    const pageActions = document.querySelector('#page-actions');
    const pageSize = document.body.scrollHeight;

    function up(){
        window.scrollTo({
            top: 0,
            behavior: "smooth",
        });
    }

    function down(){
        let toDown = document.body.scrollHeight;
        window.scrollBy({
            top: toDown,
            behavior: "smooth",
        });
    }

    function btnTab(){
        let scrollSize = document.documentElement.scrollTop;

        if(scrollSize >= 300){
            btnDown.classList.add('btns-active');
        } else {
            btnDown.classList.remove('btns-active');
        }
    }


    window.addEventListener('scroll', btnTab);
    <% } %>
</script>
<script language="JavaScript" type="text/JavaScript">
var f0 = document.forms[0];
</script>
