<%@page import="com.zetra.econsig.values.TipoArquivoEnum"%>
<%@page import="com.zetra.econsig.helper.upload.UploadHelper"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@page import="com.zetra.econsig.helper.texto.DateHelper"%>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.persistence.entity.DashboardFlex" %>
<%@ page import="com.zetra.econsig.persistence.entity.DashboardFlexConsulta" %>
<%@ page import="com.zetra.econsig.persistence.entity.DashboardFlexToolbar" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.dto.web.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String licencaFlexMonster = (String) request.getAttribute("licencaFlexMonster");
List<DashboardFlex> listDashboardFlex = (List<DashboardFlex>) request.getAttribute("listDashboardFlex");
List<DashboardFlexConsulta> listDashboardFlexConsultasTodas = (List<DashboardFlexConsulta>) request.getAttribute("listDashboardFlexConsultasTodas");
HashMap<String, List<DashboardFlexConsulta>> hashDashFlexConsulta  = (HashMap<String, List<DashboardFlexConsulta>>) request.getAttribute("hashDashFlexConsulta");
HashMap<String, List<DashboardFlexToolbar>> hashDashFlexToolbar  = (HashMap<String, List<DashboardFlexToolbar>>) request.getAttribute("hashDashFlexToolbar");
boolean activeNav = false;
boolean activePane = false;
String dflCodigoAtivo = listDashboardFlex.get(0).getDflCodigo();
%>
<c:set var="title">
   <hl:message key="rotulo.dashboardflex.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-bi"></use>
</c:set>
<c:set var="bodyContent">
  <ul class="nav nav-tabs" role="tablist">
      <% for (DashboardFlex dashboardFlex : listDashboardFlex) { 
            String codigoDash = dashboardFlex.getDflCodigo();
            String nomeDash = dashboardFlex.getDflNome();
        %>
          <li class="nav-item">
            <a class="nav-link <%= !activeNav ? "active" : ""%>" id="nav<%=codigoDash%>-tab" href="#nav<%=codigoDash%>" role="tab"  data-bs-target="#nav<%=codigoDash%>" data-bs-toggle="tab" aria-controls="nav<%=codigoDash%>" aria-selected="true"
            onclick="carregarDash('<%=codigoDash%>')"><%=TextHelper.forHtmlAttribute(nomeDash)%></a>
          </li>
      <%
            if (!activeNav) {
                activeNav = true;
            }
        } %>
  </ul>
    <div class="tab-content table-responsive">
        <% for (DashboardFlex dashboardFlex : listDashboardFlex){ 
            String codigoDash = dashboardFlex.getDflCodigo();
            List<DashboardFlexConsulta> dashFlexConsultas = hashDashFlexConsulta.get(codigoDash);
        %>  
            <div class="tab-pane fade <%=!activePane ? "show active" : ""%>" role="tabpanel" id="nav<%=codigoDash%>" role="tabpanel" aria-labelledby="nav<%=codigoDash%>-tab">
                <div class="card">
                    <div class="row">
                        <% if (dashFlexConsultas.size() > 1) { 
                                for (DashboardFlexConsulta dashConsulta : dashFlexConsultas) {
                            %>
                                    <div class="col-6">
                                        <div id="pivot-container-<%=dashConsulta.getDfoCodigo()%>"></div>
                                    </div>
                                <% } %>
                        <% } else {%>
                            <div class="col">
                                <div id="pivot-container-<%=dashFlexConsultas.get(0).getDfoCodigo()%>"></div>
                            </div>
                        <% }%>
                    </div>
                </div>
            </div>
        <%
            if (!activePane) {
                activePane = true;
            }
        } %>
  </div>
  <div class="btn-action">
      <a class="btn btn-outline-danger mt-2" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<c:set var="javascript">
  <link rel="stylesheet" type="text/css" href="../node_modules/flexmonster/flexmonster.css"/>
  <script  src="../node_modules/flexmonster/flexmonster.js"></script>
  <script type="text/javascript">
    const baseUrl = window.location.origin + "<%=request.getContextPath()%>";
    <% for (DashboardFlexConsulta dashFlexConsulta : listDashboardFlexConsultasTodas) { 
        String dflCodigo = dashFlexConsulta.getDflCodigo();
        String dfoCodigo = dashFlexConsulta.getDfoCodigo();
        boolean usaToolbar = dashFlexConsulta.getDfoUsaToolbar().equals(CodedValues.STS_ATIVO);
        String indexConsulta = dashFlexConsulta.getDfoIndex();
        boolean consultaDashUrl = dashFlexConsulta.getDfoTipoIndex().equals(CodedValues.CONSULTA_DASHBOARD_BI_URL);
        boolean usaSlice = !TextHelper.isNull(dashFlexConsulta.getDfoSlice()) && dashFlexConsulta.getDfoSlice().contains("slice") ;
        boolean filtraToolbar = hashDashFlexToolbar.get(dfoCodigo) != null;
        List<DashboardFlexToolbar> listDashToolbar = filtraToolbar ? hashDashFlexToolbar.get(dfoCodigo) : new ArrayList<>();
        String extensaoArquivo ="";
        if (!consultaDashUrl) {
            String[] parteArquivo = indexConsulta.split("\\.");
            extensaoArquivo = parteArquivo[1];
        }
    %>
    function carregarFlex<%=dfoCodigo%>() {
        var pivot<%=dfoCodigo%> = new Flexmonster({
            container: "pivot-container-<%=dfoCodigo%>",
            licenseKey: "<%=licencaFlexMonster%>",
            global: {
                localization: "localizations/pt.json"
            },
            componentFolder: "../node_modules/flexmonster/",
            <% if (usaToolbar) {%>
                toolbar: true,
            <% } %>
            <% if (filtraToolbar) {%>
                beforetoolbarcreated: filtraToolbar<%=dfoCodigo%>,
            <% } %>
            report: {
                dataSource: {
                    <% if (consultaDashUrl) { %>
                        type: "api",                        
                        url: "../v3/dashboardFlex",
                        index: "<%=indexConsulta%>"
                    <% } else { %>
                        type: "<%=extensaoArquivo%>", 
                        filename: baseUrl + "<%="/v3/dashboardFlex?acao=recuperaArquivo&dfoCodigo=" + dfoCodigo%>"
                    <% } %>
                }
                <% if (usaSlice) { %>
                    , <%= dashFlexConsulta.getDfoSlice() %>
                <% } %>
            }
        });
        <% if (filtraToolbar) { %>
            function filtraToolbar<%=dfoCodigo%>(toolbar) {
                let tabs = toolbar.getTabs();
                tabs = tabs.filter(tab => tab.id != "fm-tab-connect");
                toolbar.getTabs = function () {
                    <% for (DashboardFlexToolbar dashFlexToolbar : listDashToolbar) { %>
                        tabs = tabs.filter(tab => tab.id != "<%=dashFlexToolbar.getDftItem()%>");
                    <% }%>    
                return tabs;
                }
            }
       <% } %>
        }
    <% } %>


  const _flexDfoInitialized = new Set();

  function carregarDash(dflCodigo) {
    const pane = document.querySelector('#nav' + dflCodigo);
    if (!pane) return;

    pane.querySelectorAll('[id^="pivot-container-"]').forEach(c => {
      const dfo = c.id.replace('pivot-container-', '');
      if (_flexDfoInitialized.has(dfo)) return;

      const initFn = window['carregarFlex' + dfo];
      if (typeof initFn === 'function') {
        try {
          initFn();                
          _flexDfoInitialized.add(dfo);
        } catch (e) {
          console.error('Erro', dfo, e);
        }
      } else {
        console.warn(dfo);
      }
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    const firstActivePane = document.querySelector('.tab-pane.show.active');
    if (firstActivePane) {
      const paneId = firstActivePane.id;
      const dflCodigo = paneId.replace('nav', '');
      carregarDash(dflCodigo);
    }
  });

  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
