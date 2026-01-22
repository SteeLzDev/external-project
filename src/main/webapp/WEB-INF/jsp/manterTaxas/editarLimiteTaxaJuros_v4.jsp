<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*, java.io.*, java.math.*"%>
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
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String titulo = (String) request.getAttribute("titulo");
String msgErro = (String) request.getAttribute("msgErro");
String ltjPrazoRef = (String) request.getAttribute("ltjPrazoRef");
String rotuloLimitePrazoRef = (String) request.getAttribute("rotuloLimitePrazoRef");
String rotuloLimiteJurosMax = (String) request.getAttribute("rotuloLimiteJurosMax");
String rotuloDataFimVigencia = (String) request.getAttribute("rotuloDataFimVigencia");
String svcCodigo = (String) request.getAttribute("svcCodigo");
String svcDescricao = (String) request.getAttribute("svcDescricao");
String ltjCodigo = (String) request.getAttribute("LTJ_CODIGO");
boolean temCET = (Boolean) request.getAttribute("temCET");
boolean podeEditar = (Boolean) request.getAttribute("podeEditar");
BigDecimal ltjJurosMax = (BigDecimal) request.getAttribute("ltjJurosMax");
TransferObject limite = (TransferObject) request.getAttribute("limite");
List<TransferObject> lstTaxaSuperior = (List<TransferObject>) request.getAttribute("lstTaxaSuperior");
List<TransferObject> lstRegraJurosSuperior = (List<TransferObject>) request.getAttribute("lstRegraJurosSuperior");
boolean temLimiteTaxaJurosComposicaoCET = (boolean) request.getAttribute("temLimiteTaxaJurosComposicaoCET");
BigDecimal ltjVlrRef = (BigDecimal) request.getAttribute("ltjVlrRef");
%>

<c:set var="title">
  <%=TextHelper.forHtmlContent(titulo)%>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
<div class="col-sm">
 <form method="post" action="../v3/editarLimiteTaxas?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
  <input type="hidden" value="<%=TextHelper.forHtmlAttribute(svcDescricao)%>" name="titulo"/>
  <div class="card">
   <div class="card-header">
     <% if (limite != null) { %>
      <h2 class="card-header-title"><%=TextHelper.forHtmlContent(svcDescricao)%><%=" - "+TextHelper.forHtmlContent(ltjPrazoRef)%> <%if (ltjPrazoRef.equals("1")) { %> <hl:message key="rotulo.limite.taxa.juros.prazo.singular"/> <% } else { %> <hl:message key="rotulo.limite.taxa.juros.prazo.plural"/> <% } %> </h2>
    <% } else { %>
      <h2 class="card-header-title"><%=(temCET ? ApplicationResourcesHelper.getMessage("rotulo.criar.limite.cet.subtitulo", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.criar.limite.taxa.juros.subtitulo", responsavel))%></h2>
    <% } %>
   </div> 
     <div class="card-body">
        <div class="row">
          <div class="form-group col-sm-12 col-md-2">
              <label for="LTJ_PRAZO_REF"><%=TextHelper.forHtmlContent(rotuloLimitePrazoRef)%></label>
              <hl:htmlinput 
                name="LTJ_PRAZO_REF" 
                di="LTJ_PRAZO_REF"
                type="text" 
                classe="form-control"
                size="10"
                placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.limite.taxa.juros.prazo.ref", responsavel)%>"
                onFocus="SetarEventoMascara(this,'#D3',true);"
                value="<%=TextHelper.forHtmlAttribute(limite != null && (lstTaxaSuperior == null || lstTaxaSuperior.isEmpty()) ? ltjPrazoRef: JspHelper.verificaVarQryStr(request, \"LTJ_PRAZO_REF\"))%>"
                others="<%=TextHelper.forHtmlAttribute( !podeEditar || (lstTaxaSuperior != null && !lstTaxaSuperior.isEmpty()) ? "disabled" : "")%>"
              />
          </div>
            <%=JspHelper.verificaCampoNulo(request, "LTJ_PRAZO_REF")%>
          <div class="form-group col-sm-12 col-md-3">
              <label for="LTJ_JUROS_MAX"><%=TextHelper.forHtmlContent(rotuloLimiteJurosMax)%></label>
              <hl:htmlinput 
                name="LTJ_JUROS_MAX"
                di="LTJ_JUROS_MAX" 
                type="text" 
                classe="form-control"
                value="<%=TextHelper.forHtmlAttribute(limite != null && (lstTaxaSuperior == null || lstTaxaSuperior.isEmpty()) ? NumberHelper.format(ltjJurosMax.doubleValue(), NumberHelper.getLang(), 2, 8): JspHelper.verificaVarQryStr(request, \"LTJ_JUROS_MAX\"))%>"
                size="10"
                placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.limite.taxa.juros.juros.max", responsavel)%>"
                onFocus="SetarEventoMascara(this,'#F10',true);" 
                onBlur="fout(this);ValidaMascara(this);"
                others="<%=TextHelper.forHtmlAttribute( !podeEditar || !lstTaxaSuperior.isEmpty() ? "disabled" : "")%>"
              />
              <%=JspHelper.verificaCampoNulo(request, "LTJ_JUROS_MAX")%>
        </div>
        
        <%if(temLimiteTaxaJurosComposicaoCET) {%>
          <div class="form-group col-sm-12 col-md-3">
                <label for="LTJ_VLR_REF"><%=ApplicationResourcesHelper.getMessage("rotulo.limite.taxa.cet.juros.max", responsavel)%></label>
                <hl:htmlinput 
                  name="LTJ_VLR_REF"
                  di="LTJ_VLR_REF" 
                  type="text" 
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(limite != null && ltjVlrRef != null && (lstTaxaSuperior == null || lstTaxaSuperior.isEmpty()) ? NumberHelper.format(ltjVlrRef.doubleValue(), NumberHelper.getLang(), 2, 8): JspHelper.verificaVarQryStr(request, \"LTJ_VLR_REF\"))%>"
                  size="10"
                  placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.limite.taxa.cet.juros.max", responsavel)%>"
                  onFocus="SetarEventoMascara(this,'#F10',true);" 
                  onBlur="fout(this);ValidaMascara(this);"
                  others="<%=TextHelper.forHtmlAttribute( !podeEditar || !lstTaxaSuperior.isEmpty() ? "disabled" : "")%>"
                />
          </div>
        <%} %>
        
        
          <% if (lstTaxaSuperior != null && !lstTaxaSuperior.isEmpty() || (lstRegraJurosSuperior != null && !lstRegraJurosSuperior.isEmpty())) { %>
           <div class="form-group col-sm-12 col-md-2">
              <label for=CFT_DATA_FIM_VIG><%=TextHelper.forHtmlContent(rotuloDataFimVigencia)%></label>
              <hl:htmlinput name="CFT_DATA_FIM_VIG"
                di="CFT_DATA_FIM_VIG"
                type="text"
                classe="form-control"
                value=""
                size="10"
                mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
              />
            </div>
          <% } %>
      </div> 
      
      
      <% if (lstRegraJurosSuperior != null && !lstRegraJurosSuperior.isEmpty()) { %>
     <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>   
            <th scope="col" width="60%"><%=ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)%></th>
            <th scope="col" width="10%"><%=ApplicationResourcesHelper.getMessage("rotulo.funcao.singular", responsavel)%></th>
            <th scope="col" width="10%"><%=ApplicationResourcesHelper.getMessage("rotulo.prazo.inicial", responsavel)%></th>
            <th scope="col" width="10%"><%=ApplicationResourcesHelper.getMessage("rotulo.prazo.final", responsavel)%></th>
            <th scope="col" width="10%"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros", responsavel)%></th>
          </tr>
        </thead>
        <tbody>  
        <%
         String csa_nome, str_dtj_vlr, dtj_prazo_inicial,dtj_prazo_final , fun_nome;
         int rowCount = 0;
         BigDecimal dtj_vlr;
         for (TransferObject regraTaxaJuro : lstRegraJurosSuperior) {
             csa_nome = !TextHelper.isNull(regraTaxaJuro.getAttribute(Columns.CSA_NOME_ABREV)) ? (String) regraTaxaJuro.getAttribute(Columns.CSA_NOME_ABREV) : (String) regraTaxaJuro.getAttribute(Columns.CSA_NOME);
             fun_nome = !TextHelper.isNull(regraTaxaJuro.getAttribute(Columns.FUN_CODIGO)) ? (String) regraTaxaJuro.getAttribute(Columns.FUN_CODIGO) : "-";
             dtj_vlr = new BigDecimal(regraTaxaJuro.getAttribute(Columns.DTJ_TAXA_JUROS).toString());
             str_dtj_vlr = NumberHelper.format(dtj_vlr.doubleValue(), NumberHelper.getLang(), 2, 8);
             dtj_prazo_inicial = regraTaxaJuro.getAttribute(Columns.DTJ_FAIXA_PRAZO_INI).toString();
             dtj_prazo_final = regraTaxaJuro.getAttribute(Columns.DTJ_FAIXA_PRAZO_FIM).toString();
        %>
          <tr>
            <td><%=csa_nome.toUpperCase()%></td>
            <td><%=fun_nome%></td>
            <td align="center"><%=dtj_prazo_inicial%></td>
            <td align="center"><%=dtj_prazo_final%></td>
            <td align="center"><%=str_dtj_vlr%></td>
          </tr>
        <%
         }
        %>
       </tbody>
     </table>
   </div>
    <% } %>


    <% if (lstTaxaSuperior != null && !lstTaxaSuperior.isEmpty()) { %>
     <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>   
            <th scope="col" width="60%"><%=ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)%></th>
            <th scope="col" width="20%"><%=ApplicationResourcesHelper.getMessage("rotulo.prazo.singular", responsavel)%></th>
          <% if (temCET) { %>
                <th scope="col" width="20%"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet", responsavel)%></th>
          <% } else { %>
                <th scope="col" width="20%"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros", responsavel)%></th>
          <% } %>
          </tr>
        </thead>
        <tbody>  
        <%
         String csa_nome, str_cft_vlr, vlr_parcela, prz_vlr;
         int rowCount = 0;
         BigDecimal cft_vlr;
         for (TransferObject taxa : lstTaxaSuperior) {
             csa_nome = !TextHelper.isNull(taxa.getAttribute(Columns.CSA_NOME_ABREV)) ? (String) taxa.getAttribute(Columns.CSA_NOME_ABREV) : (String) taxa.getAttribute(Columns.CSA_NOME);
             cft_vlr = new BigDecimal(taxa.getAttribute(Columns.CFT_VLR).toString());
             str_cft_vlr = NumberHelper.format(cft_vlr.doubleValue(), NumberHelper.getLang(), 2, 8);
             prz_vlr = taxa.getAttribute(Columns.PRZ_VLR).toString();
        %>
          <tr>
            <td><%=csa_nome.toUpperCase()%></td>
            <td align="center"><%=prz_vlr%></td>
            <td align="center"><%=str_cft_vlr%></td>
          </tr>
        <%
         }
        %>
       </tbody>
     </table>
   </div>
    <% } %>
  </div>
</div>
    <hl:htmlinput name="SVC_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(svcCodigo)%>"/>
    <hl:htmlinput name="_skip_history_" type="hidden" value="true"/>
    <hl:htmlinput name="finalizarEdicao" type="hidden" value="false"/>        
    <%if (ltjCodigo != null) {%>
      <hl:htmlinput name="LTJ_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(ltjCodigo)%>"/>
    <%}%>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
      <%if (podeEditar) {%>
        <a class="btn btn-primary" href="#no-back" onClick="vf_cadastro_limite(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
      <%}%>
    </div> 
</form>
</div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
  	f0 = document.forms[0];
  </script>
  <script type="text/JavaScript">
  function formLoad() {
    focusFirstField();
  }
  function vf_cadastro_limite() {
  	f0 = document.forms[0];
  	var maiorValorPermitido = 99.99999999;
  	var jurosVlr = parseFloat(parse_num(f0.LTJ_JUROS_MAX.value));
  	
  	var Controles = new Array("LTJ_PRAZO_REF", "LTJ_JUROS_MAX");
  	var Msgs = new Array('<hl:message key="mensagem.informe.limite.taxa.juros.prazo"/>',
  						 '<hl:message key="mensagem.informe.limite.taxa.juros.maximo.cef"/>');
  	
  	if (ValidaCampos(Controles, Msgs)) {
  		if (f0.LTJ_PRAZO_REF.value == '0') {
  			alert('<hl:message key="mensagem.erro.limite.taxa.juros.prazo.invalido"/>');
  			f0.LTJ_PRAZO_REF.focus();
  			return false;
  		}
  		if (jurosVlr < 0) {
  			alert('<hl:message key="mensagem.erro.limite.taxa.juros.valor.negativo"/>');
  	  		f0.LTJ_JUROS_MAX.focus();
  	  		return false;
  		} else if (jurosVlr > maiorValorPermitido) {
  			alert('<hl:message key="mensagem.erro.limite.taxa.juros.valor.maximo"/>'.replace("{0}", FormataContabilEx(maiorValorPermitido,8,'')));	
  	  		f0.LTJ_JUROS_MAX.focus();
  	  		return false;
  		}
  		if (vf_data_fim_vigencia()) {
  	        enableAll();
  	  		f0.submit();
  		} else {
  	  		return false;
  		}
    	}   
  }
  
  function vf_data_fim_vigencia() {
    if (f0.CFT_DATA_FIM_VIG == null || f0.CFT_DATA_FIM_VIG == undefined) {
        return true;
    }
  
    f0.finalizarEdicao.value = 'true';
    if (f0.CFT_DATA_FIM_VIG.value.trim() == '') {
        return true;
    }
    var dataIni = new String(f0.CFT_DATA_FIM_VIG.value);
    var campos = obtemPartesData(dataIni);
    if (dataIni == '' || dataIni.length != 10 || campos.length != 3) {
      alert('<hl:message key="mensagem.erro.taxa.juros.data.vigencia"/>');
      return false;
    }
    if (verificaData(f0.CFT_DATA_FIM_VIG.value)) {
      var now = new Date();
      now.setHours(0);
      now.setMinutes(0);
      now.setSeconds(0);  
      now.setMilliseconds(0);  
      var then = new Date(campos[2], campos[1] - 1, campos[0]);
      if (then.getTime() < now.getTime()) {
        alert('<hl:message key="mensagem.erro.taxa.juros.data.fim.vigencia.invalida"/>');
        return false;
      }
      return true;
    }
  
    return false;
  }
  
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>