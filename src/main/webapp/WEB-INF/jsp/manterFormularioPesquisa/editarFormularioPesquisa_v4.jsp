<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%

AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

FormularioPesquisaTO formularioPesquisa = (FormularioPesquisaTO) request.getAttribute("formularioPesquisaTO");
String fpeCodigo  = formularioPesquisa != null ? formularioPesquisa.getFpeCodigo() : null;

String dtFimReformated = "";
if (formularioPesquisa != null){
  if (formularioPesquisa.getFpeDtFim() != null) {
    dtFimReformated = DateHelper.reformat(formularioPesquisa.getFpeDtFim().toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern());
  }
}

String licencaSurveyJS = (String) request.getAttribute("licencaSurveyJS");

%>
<c:set var="title">
  <hl:message key="rotulo.edt.form.pesquisa.manutencao"/>
</c:set>

<c:set var="imageHeader">
    <use xlink:href="#i-manutencao"></use>
</c:set>

<c:set var="bodyContent">
  <form method="post" action="../v3/formularioPesquisa?<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.editar.grid" /></h2>
      </div>
      <div class="card-body">
        <div class="row">
          <div class="form-group col-sm-6">
            <label for="fpeNome"><hl:message key="rotulo.edt.form.pesquisa.nome"/></label>
            <input id="fpeNome" type="text" placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.form.pesquisa.digite.nome", responsavel)%>" class="form-control" type="text" name="fpeNome" value="<%=TextHelper.forHtmlAttribute(formularioPesquisa != null? formularioPesquisa.getFpeNome(): "")%>" size="32" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
            <%=JspHelper.verificaCampoNulo(request, "fpeNome")%>
          </div>
        </div>
        <div class="row">
          <div class="form-group col-sm-6">
            <label id="lblDtFim" for="fpeDtFim"><hl:message key="rotulo.edt.form.pesquisa.data.fim"/></label>
            <hl:htmlinput name="fpeDtFim" di="fpeDtFim" type="text" classe="form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(dtFimReformated)%>" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"/>
          </div>
        </div>
        <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <div>
              <span id="descricao"><hl:message key="rotulo.edt.form.pesquisa.bloqueia.sistema"/></span>
            </div>
            <div class="form-check form-check-inline mt-0" >
              <input class="form-check-input" type="radio" id="bloqueiaSim" name="fpeBloqueiaSistema" value="true" <%=formularioPesquisa != null && formularioPesquisa.isFpeBloqueiaSistema() ? "checked" : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
              <label class="form-check-label labelSemNegrito pr-3" for="bloqueiaSim"><hl:message key="rotulo.sim"/></label>
            </div>
            <div class="form-check form-check-inline mt-0">
              <input class="form-check-input" type="radio" id="bloqueiaNao" name="fpeBloqueiaSistema" value="false" <%=(formularioPesquisa != null && !formularioPesquisa.isFpeBloqueiaSistema() ) || formularioPesquisa == null ? "checked" : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
              <label class="form-check-label labelSemNegrito" for="bloqueiaNao"><hl:message key="rotulo.nao"/></label>
            </div>
          </div>
        </div>
        <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <div>
              <span id="descricao"><hl:message key="rotulo.edt.form.pesquisa.publicado"/></span>
            </div>
            <div class="form-check form-check-inline mt-0" >
              <input class="form-check-input" type="radio" id="publicadoSim" name="fpePublicado" value="true" <%=formularioPesquisa != null && formularioPesquisa.isFpePublicado() ? "checked" : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
              <label class="form-check-label labelSemNegrito pr-3" for="publicadoSim"><hl:message key="rotulo.sim"/></label>
            </div>
            <div class="form-check form-check-inline mt-0">
              <input class="form-check-input" type="radio" id="publicadoNao" name="fpePublicado" value="false" <%=(formularioPesquisa != null && !formularioPesquisa.isFpePublicado() ) || formularioPesquisa == null ? "checked" : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
              <label class="form-check-label labelSemNegrito" for="publicadoNao"><hl:message key="rotulo.nao"/></label>
            </div>
          </div>
        </div>
        <div class="row">
          <!-- SurveJS -->
          <div id="surveyCreator"></div>
        </div>
      </div>
    </div>

    <div class="btn-action mt-3">
      <a class="btn btn-outline-danger" href="#no-back" onClick="javascript:postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/formularioPesquisa?acao=listar", request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
      <a class="btn btn-primary" href="#no-back" onClick="javascript:salvarFormularioPesquisa()"><hl:message key="rotulo.botao.salvar"/></a>
      <input type="hidden" name="acao" value="salvar">
      <input type="hidden" name="fpeCodigo" value="<%=TextHelper.forHtmlAttribute(fpeCodigo != null ? fpeCodigo: "")%>">
      <input type="hidden" name="fpeJson" id="fpeJson"/>
    </div>
 </form>
</c:set>
<c:set var="javascript">
  <script language="JavaScript" type="text/JavaScript" src="../js/surveyjs.js?<hl:message key="release.tag"/>"></script>
  <!-- SurveyJS Form Library resources -->
  <link href="../node_modules/survey-core/survey-core.min.css" type="text/css" rel="stylesheet">
  <script language="JavaScript" type="text/JavaScript" src="../node_modules/survey-core/survey.core.min.js"></script>
  <script language="JavaScript" type="text/JavaScript" src="../node_modules/survey-js-ui/survey-js-ui.min.js"></script>
  <!-- Survey Creator resources -->
  <link href="../node_modules/survey-creator-core/survey-creator-core.min.css" type="text/css" rel="stylesheet">
  <script language="JavaScript" type="text/JavaScript" src="../node_modules/survey-creator-core/survey-creator-core.min.js"></script>
  <script language="JavaScript" type="text/JavaScript" src="../node_modules/survey-creator-core/survey-creator-core.i18n.min.js"></script>
  <script language="JavaScript" type="text/JavaScript" src="../node_modules/survey-creator-core/i18n/portuguese.js"></script>
  <script language="JavaScript" type="text/JavaScript" src="../node_modules/survey-creator-js/survey-creator-js.min.js"></script>
  <script type="text/JavaScript">
    function formLoad() {
      f0.fpeNome.focus();
      configSurveyJS();
    }

    window.onload = formLoad;

    const creatorOptions = {
      questionTypes:["radiogroup", "checkbox", "rating", "comment"],
      previewAllowSelectLanguage:false,
      showJSONEditorTab:false,
    // showPreviewTab:false
    };
    
    Survey.slk("<%=licencaSurveyJS %>");
    const creator = new SurveyCreator.SurveyCreator(creatorOptions);

    function configSurveyJS() {
      configLocale();
      creator.showCreatorThemeSettings=false;
      <%if (formularioPesquisa != null && !formularioPesquisa.getFpeJson().equals("")) {%>
        creator.JSON = <%=formularioPesquisa.getFpeJson()%>;
      <%}%>

      creator.applyCreatorTheme(eConsigTheme);
      creator.render(document.getElementById("surveyCreator"));
    }

    function configLocale(){
      const enLocale = Survey.getLocaleStrings("en");
      enLocale.pagePrevText="<hl:message key='rotulo.botao.voltar'/>";
      enLocale.pageNextText="<hl:message key='rotulo.botao.proximo'/>";
      enLocale.completeText="<hl:message key='rotulo.botao.concluir'/>";
      enLocale.completingSurvey="<hl:message key='mensagem.informe.form.pesquisa.completar'/>";
      enLocale.loadingSurvey="<hl:message key='mensagem.informe.form.pesquisa.carregando' />";
      enLocale.completingSurveyBefore="<hl:message key='mensagem.informe.form.pesquisa.respondido' />";
      creator.locale = "pt";
    }

    creator.onPreviewSurveyCreated()

    function verificaCampos() {
      var controles = new Array("fpeNome", "fpeDtFim");
      var msgs = new Array ("<hl:message key='mensagem.informe.form.pesquisa.nome'/>", 
                            "<hl:message key='mensagem.informe.form.pesquisa.dt.fim'/>");

      if (!ValidaCampos(controles, msgs) || !validaCampoDtFim() || !validaFormulario()) {
        return false;
      }

      return true;
    }

    function salvarFormularioPesquisa() {
      if(verificaCampos()) {
        document.getElementById("fpeJson").value = btoa(JSON.stringify(creator.JSON, null, 0));
        f0.submit();
        return true;
      }
      
      return false;
    }

    function validaFormulario() {
      if (JSON.stringify(creator.JSON, null, 0).indexOf("elements") === -1) {
        alert('<hl:message key="mensagem.erro.form.pesquisa.min.pergunta"/>');
        return false;
      }

      return true;
    }

    function validaCampoDtFim() {
      with (document.forms[0]) {
        var partesDtFim = obtemPartesData(fpeDtFim.value);

        var dia = partesDtFim[0];
        var mes = partesDtFim[1];
        var ano = partesDtFim[2];
        var dataExecucao = new Date(ano, mes - 1, dia);
        dataExecucao.setHours(0,0,0,0);
        var dataCorrente = new Date();
        dataCorrente.setHours(0,0,0,0);
        
        if (!verificaData(fpeDtFim.value)) {
          fpeDtFim.focus();
          return false;
        }
        if (dataExecucao.getTime() <= dataCorrente.getTime()) {
          fpeDtFim.focus();
          alert('<hl:message key="mensagem.erro.form.pesquisa.data.fim.maior.hoje"/>');
          return false;
        }
      }                                       
      return true;
    }
  </script>
  <script type="text/JavaScript">
    var f0 = document.forms[0];
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>