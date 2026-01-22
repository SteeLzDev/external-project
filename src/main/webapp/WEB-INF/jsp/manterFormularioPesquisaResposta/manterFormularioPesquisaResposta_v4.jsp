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
        FormularioPesquisaTO formulario = request.getAttribute("formulario") != null ? (FormularioPesquisaTO) request.getAttribute("formulario") : null;
        String formJson = formulario != null ? formulario.getFpeJson() : null;
        String fpeCodigo = formulario != null ? formulario.getFpeCodigo() : null;
        boolean obrigatorio = request.getAttribute("obrigatorio") != null ? (boolean) request.getAttribute("obrigatorio") : false;
    %>
    <c:set var="title">
    </c:set>
    <c:set var="imageHeader">
      <use xlink:href="#i-home"></use>
    </c:set>
    <c:set var="bodyContent">
    <div class="card">
    	<div class="card-header">
    		<h2 class="card-header-title"><hl:message key="mensagem.formulario.resposta"/></h2>
    	</div>
    	<div class="card-body">
    		<div class="row">
                <!-- SurveJS -->
                <div id="surveyContainer"></div>
            </div>
             <% if(!obrigatorio) { %>
                 <div class="btn-action mt-3">
                      <a class="btn btn-primary" href="#no-back" onClick="javascript:postData('../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true'); return false;"><hl:message key="mensagem.formulario.responder.depois"/></a>
                 </div>
                <% } %>
    	</div>
    </div>
    </c:set>
    <c:set var="javascript">
      <script language="JavaScript" type="text/JavaScript" src="../js/surveyjs.js?<hl:message key="release.tag"/>"></script>
      <!-- SurveyJS Form Library resources -->
      <link href="../node_modules/survey-core/survey-core.min.css" type="text/css" rel="stylesheet">
      <script language="JavaScript" type="text/JavaScript" src="../node_modules/survey-core/survey.core.min.js"></script>
      <script language="JavaScript" type="text/JavaScript" src="../node_modules/survey-core/themes/index.min.js"></script>
      <script language="JavaScript" type="text/JavaScript" src="../node_modules/survey-js-ui/survey-js-ui.min.js"></script>
      <script type="text/javascript">
       window.onload = loadContent();
       function configLocale(){
              const enLocale = Survey.getLocaleStrings("en");
              enLocale.pagePrevText="<hl:message key='rotulo.botao.voltar'/>";
              enLocale.pageNextText="<hl:message key='rotulo.botao.proximo'/>";
              enLocale.completeText="<hl:message key='rotulo.botao.concluir'/>";
              enLocale.completingSurvey="<hl:message key='mensagem.informe.form.pesquisa.completar'/>";
              enLocale.loadingSurvey="<hl:message key='mensagem.informe.form.pesquisa.carregando' />";
              enLocale.completingSurveyBefore="<hl:message key='mensagem.informe.form.pesquisa.respondido' />";
              enLocale.requiredError="<hl:message key='mensagem.formulario.resposta.obrigatoria' />"
       }

       function loadContent () {
          configLocale();

          const survey = new Survey.Model(<%=formJson%>);

          survey.onComplete.add(function (survey) {
                                    const fpeCodigo = "<%=TextHelper.forJavaScript(fpeCodigo)%>";
                                    $.ajax({
                                      url: "../v3/formularioResposta/salvarResposta?<%=SynchronizerToken.generateToken4URL(request)%>",
                                      method: 'POST',
                                      data: {
                                        'json': JSON.stringify(survey.data),
                                        'fpeCodigo': fpeCodigo
                                      },
                                      success: function (data) {
                                        postData('../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true');
                                      },
                                      error: function (error) {
                                        console.log('Erro:', JSON.stringify(survey.data));
                                      }
                                    });
          });

          survey.applyTheme(eConsigTheme);

          survey.render(document.getElementById("surveyContainer"));
        };
      </script>
    </c:set>
    <t:page_v4>
      <jsp:attribute name="header">${title}</jsp:attribute>
      <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
      <jsp:attribute name="javascript">${javascript}</jsp:attribute>
      <jsp:body>${bodyContent}</jsp:body>
    </t:page_v4>
