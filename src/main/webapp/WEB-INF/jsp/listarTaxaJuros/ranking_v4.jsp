<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.*" %>
<%@ page import="com.zetra.econsig.helper.financeiro.CDCHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ taglib prefix="t"     tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"    uri="/html-lib" %>
<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%

AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

List<?> prazos = (List<?>) request.getAttribute("prazos");
String tipo = (String) request.getAttribute("tipo");
String svcCodigo = (String) request.getAttribute("svcCodigo");
String titulo = (String) request.getAttribute("titulo");
boolean temCET = (boolean) request.getAttribute("temCET");
String periodoE = (String) request.getAttribute("periodoE");
String periodo = (String) request.getAttribute("periodo");

Map<?, List<?>> rankings = (Map<?, List<?>>) request.getAttribute("rankings");

int maxSizeRanking = (int) request.getAttribute("maxSizeRanking");

%>

<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="title">
  <hl:message key="rotulo.taxa.juros.ranking"/>
</c:set>

<c:set var="bodyContent">
  <FORM NAME="form1" METHOD="post" ACTION="">
    <div class="card-body">
    </div>
      <% if (prazos.size() == 0) { %>
    <div class="card">
      <div class="card-header hasIcon">
        <span class="card-header-icon"><svg width="25"><use xlink:href="#i-simular-config"></use></svg></span>
        <h2 class="card-header-title"><%=ApplicationResourcesHelper.getMessage("rotulo.taxa.juros.ranking.titulo", responsavel, titulo.toUpperCase())%></h2>
      </div>
      <div class="card-body table-responsive p-0">
        <table class="table table-striped table-hover table-ranking">
          <thead>
           <tr>
            <th scope="col" width="4%"><%=ApplicationResourcesHelper.getMessage("rotulo.prazo.plural", responsavel)%></th>
            <th scope="col"><%=ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)%></th>
            <th scope="col"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros", responsavel)%></th>
           </tr>
          </thead>
          <tbody>
            <tr>
              <td colspan="3"><hl:message key="mensagem.taxa.juros.ranking.prazo.nao.encontrado"/></td>
            </tr>
          </tbody>
          <tfoot>
            <tr>
              <td colspan="3">
                <hl:message key="rotulo.taxa.juros.listagem.ranking"/>
              </td>
            </tr>
         </tfoot>
        </table>
       </div>
     </div>
      <% } else {%>
      <div class="row firefox-print-fix">
          <% if (responsavel.isCseSupOrg()) { %>
              <div class="col-md-4 d-print-none">
                <div class="card">
                  <div class="card-header hasIcon pl-3">
                    <h2 class="card-header-title"><hl:message key="rotulo.taxa.juros.ranking.periodo.subtitulo"/></h2>
                  </div>
                  <div class="card-body">
                    <div class="row">
                      <div class="form-group col-sm-12 col-md-12">
                        <div class="row mt-2" role="group" aria-labelledby="De">
                          <div class="form-check pt-2 col-sm-12 col-md-11">
                            <hl:htmlinput name="periodoIni"
                                di="periodoIni"
                                placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.taxa.juros.digite.periodo", responsavel) %>"  
                                type="text"
                                classe="form-control w-100"
                                size="12"
                                mask="<%=LocaleHelper.getPeriodoPlaceHolder()%>"
                             />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="btn-action">
                  <a class="btn btn-primary ml-1" onClick = "doIt(); return false;" href="#no-back"><hl:message key="rotulo.acao.pesquisar"/></a>
                </div>
              </div>
              <div class="col-sm-7 col-md-8">
                <div class="card">
                  <div class="card-header hasIcon pl-3">
                    <h2 class="card-header-title"><%=ApplicationResourcesHelper.getMessage("rotulo.taxa.juros.ranking.titulo", responsavel, titulo.toUpperCase())%><%= " - " + TextHelper.forHtmlContent(periodoE)%></h2>
                  </div>
                  <div class="card-body table-responsive p-0">
                    <table class="table table-striped table-hover">
                    <%
                      Short prz_vlr = null;
                      PrazoTransferObject pto = null;
                      List<?> coeficientes = null;
                      Iterator<?> it = null;
                      
                      it = prazos.iterator();
                      
                      Set<?> keySet = rankings.keySet();
                      it = keySet.iterator();
                      
                      while (it.hasNext()) {
                        prz_vlr = (Short) it.next();
                    %><tr>
                        <thead>
                          <tr>
                          <%
                            String csa_nome_lst , str_cft_vlr, cetAnual;
                            BigDecimal cft_vlr;
                            CustomTransferObject coeficiente = null;
                          
                            int maxSize = prazos.size() - 1;
                          
                              for (int i=0;i < prazos.size(); i++) {
                                pto = (PrazoTransferObject)prazos.get(i);
                                if (i==0) { %>
                                    <th scope="col" width="4%"><%=ApplicationResourcesHelper.getMessage("rotulo.prazo.plural", responsavel)%></th>
                                  	<th scope="col"><%=ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)%></th>
                                <% if (temCET) { %>
                                    <th scope="col"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet", responsavel)%></th>
                                    <th scope="col"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet.anual", responsavel)%></th>
                                  <% } else { %> 
                                    <th scope="col"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros", responsavel)%></th>
                                  <% } %>
                                   </tr>
                                </thead>
                                <tbody>
                                  <tr>
                                  <td align="center" class="no-hover table-secundary" <%=(maxSizeRanking == 0 ? "" : "rowspan=\"" + maxSizeRanking + 1 + "\"")%>><%=TextHelper.forHtmlContent(prz_vlr)%></td>  
                              	  <% if (maxSizeRanking == 0) { %>
                              	  <td></td>
                              	  <td></td>
                              	  <% } %>
                              <%} %>
                             <%
                            coeficientes = (List<?>)rankings.get(prz_vlr);
                            if (i<coeficientes.size()) {
                              coeficiente = (CustomTransferObject)coeficientes.get(i);
                              csa_nome_lst = (String)coeficiente.getAttribute(Columns.CSA_NOME_ABREV);
                              if (TextHelper.isNull(csa_nome_lst)) {
                                csa_nome_lst = (String)coeficiente.getAttribute(Columns.CSA_NOME);
                                if (csa_nome_lst.length() > 20) {
                                  csa_nome_lst = csa_nome_lst.substring(0,17) + "...";
                                }
                              }
                              cft_vlr = new BigDecimal(coeficiente.getAttribute(Columns.CFT_VLR).toString());
                              if (cft_vlr.compareTo(new BigDecimal("0.00")) == 0) {
                                str_cft_vlr = "";
                                cetAnual = "";
                              } else {
                                str_cft_vlr = NumberHelper.format(cft_vlr.doubleValue(), NumberHelper.getLang(), 2, 8);
                                cetAnual = CDCHelper.getStrTaxaEquivalenteAnual(str_cft_vlr);
                              }
                              if (i==0) { %>
                              </tr>
                           <% } %>
                           <tr>
                               <td><%=csa_nome_lst.toUpperCase()%></td>
                               <td><%=str_cft_vlr%></td>
                           <% if (temCET) { %>
                              <td><%=cetAnual%></td>
                           <% } 
                            } else {
                              csa_nome_lst = "";
                              str_cft_vlr = "";
                              cetAnual = "";
                            }
                      
                            if (i==maxSize) { %>
                              </tr>
                              </tbody>
                             <%  }
                        } %>
                      
                      <%} %>
                 <tfoot>
                  <tr>
                    <td colspan="3">
                      <hl:message key="rotulo.taxa.juros.listagem.ranking"/>
                    </td>
                  </tr>
                </tfoot>
              </table>      
           </div>
         </div>
        </div>
                      <%} %>
      </div>
                      <%
                         }
                      %>
     <hl:htmlinput type="hidden" name="SVC_CODIGO" value="<%=TextHelper.forHtmlAttribute(svcCodigo)%>" />
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</FORM>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
  
  function doIt() {
   var periodo = f0.periodoIni.value;
   var qs = 'tipo=<%=TextHelper.forJavaScriptBlock(tipo)%>&SVC_CODIGO=<%=TextHelper.forJavaScriptBlock(svcCodigo)%>&titulo=<%=TextHelper.forJavaScriptBlock(titulo)%>&<%=SynchronizerToken.generateToken4URL(request)%>';
   if (periodo != '') {
     if (verificaPeriodo(periodo)) {
       qs += '&periodoIni=' + periodo;
       postData('../v3/listarTaxaJuros?acao=iniciar&' + qs);
     } else {
       f0.periodoIni.focus();
     }
   } else {
     postData('../v3/listarTaxaJuros?acao=iniciar&' + qs);
   }
  }
  
  function formLoad() {
    if (f0.periodoIni != null) {
      f0.periodoIni.focus();
    }
  }
  </script>
  <script type="text/JavaScript">
    var f0 = document.forms[0];
    window.onload = formLoad;
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>