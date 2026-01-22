<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="java.math.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  String readOnly = !responsavel.temPermissao(CodedValues.FUN_EDT_COEFICIENTE_CORRECAO) ? "true" : "false";
  
  List<CoeficienteCorrecaoTransferObject> listaCoeficientesCorrecao = (List<CoeficienteCorrecaoTransferObject>) request.getAttribute("listaCoeficientesCorrecao");
  int tamanhoLista = request.getAttribute("tamanhoLista") != null ? (int) request.getAttribute("tamanhoLista") : 0;
  String ccrTccCodigo = request.getAttribute("ccrTccCodigo") != null ? (String) request.getAttribute("ccrTccCodigo") : "";
  String tccDescricao = request.getAttribute("tccDescricao") != null ? (String) request.getAttribute("tccDescricao") : "";
  String formaCalc = request.getAttribute("tccFormaCalc") != null ? (String) request.getAttribute("tccFormaCalc") : "";
  String formaCalcSelec = JspHelper.verificaVarQryStr(request, "formaCalcSelec");
  if (formaCalcSelec.equals("")){
      formaCalcSelec = formaCalc;
  } else {
      formaCalc = formaCalcSelec;
  }
  String ccrMes, ccrAno, ccrVlr, ccrVlrAcumulado;
  CoeficienteCorrecaoTransferObject ccto = null;
%>

<c:set var="title">
  <% if (!TextHelper.isNull(ccrTccCodigo) && !readOnly.equals("true")){ %>        
  <hl:message key="rotulo.coeficiente.correcao.editar"/>
  <%} else { %>
  <hl:message key="rotulo.coeficiente.correcao.listar"/>
  <%}%>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
<form NAME="form1" METHOD="post" ACTION="../v3/manterCoeficienteCorrecao?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>">
  <div class="card">
    <div class="card-header">                 
      <h2 class="card-header-title"><hl:message key="rotulo.opcoes.coeficiente"/></h2>
    </div>
    <div class="card-body">
      <div class="row">
        <div class="form-group col-sm-6">
          <label for="iDescricao"><hl:message key="rotulo.descricao.coeficiente.correcao"/></label>                 
          <hl:htmlinput classe="form-control" di="iDescricao" name="tccDescricao" type="text" value="<%=TextHelper.forHtmlAttribute(tccDescricao)%>" others="<%=TextHelper.forHtmlAttribute(readOnly.equals("true") ? "disabled" : "")%>" placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.descricao.coeficiente.placeholder", responsavel)%>" size="32" mask="#A40" />
        </div>
        <div class="form-group col-sm-6" role="radio-group" area-labeldbay="iCalculaValorAcumulado">
          <div><label id="iCalculaValorAcumulado"><hl:message key="rotulo.calcular.valor.acumulado.coeficiente.correcao"/></label></div>
          <div class="form-check form-check-inline">
            <input class="form-check-input mt-1 ml-1" type="radio" id="iCalculaValorAcumuladoSim" type="radio"  name="formaCalcSelec" value="<%=(String)CodedValues.FORMA_CALCULO_PADRAO%>" <%=(String)(CodedValues.FORMA_CALCULO_PADRAO.equals(formaCalcSelec) ? "CHECKED" : "")%> onFocus="SetarEventoMascara(this,'#*100',true); document.getElementById('ccrVlrAcumulado0').style.visibility='hidden';" onBlur="fout(this);ValidaMascara(this);" <%=(String)((!readOnly.equals("true") ? "" : "disabled"))%> >
            <label class="form-check-label labelSemNegrito ml-1 pr-4 pt-1" for="iCalculaValorAcumuladoSim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline">
            <input class="form-check-input mt-1 ml-1" type="radio" id="iCalculaValorAcumuladoNao" type="radio"  name="formaCalcSelec" value="<%=(String)CodedValues.FORMA_CALCULO_OUTRO%>"  <%=(String)((CodedValues.FORMA_CALCULO_OUTRO.equals(formaCalcSelec) || TextHelper.isNull(formaCalcSelec)) ? "CHECKED" : "")%> onFocus="SetarEventoMascara(this,'#*100',true); document.getElementById('ccrVlrAcumulado0').style.visibility='visible';" onBlur="fout(this);ValidaMascara(this);" <%=(String)((!readOnly.equals("true")) ? "" : "disabled")%> >
            <label class="form-check-label labelSemNegrito ml-1 pr-4 pt-1" for="iCalculaValorAcumuladoNao"><hl:message key="rotulo.nao"/></label>
          </div>
      	</div>
      </div>
    </div>
  </div>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.novos.coeficientes"/></h2>
    </div>
    <div class="card-body">
      <%
      int j = 1;
      if (!TextHelper.isNull(ccrTccCodigo)){
          Iterator it = listaCoeficientesCorrecao.iterator();
          while (it.hasNext()) {
            ccto = (CoeficienteCorrecaoTransferObject) it.next();
            ccrMes = ((ccto != null && ccto.getCcrMes() != null) ? ccto.getCcrMes().toString() : JspHelper.verificaVarQryStr(request, ("ccrMes")));
            ccrAno = ((ccto != null && ccto.getCcrAno() != null) ? ccto.getCcrAno().toString() : JspHelper.verificaVarQryStr(request, ("ccrAno" + Integer.toString(j))));
            ccrVlr = (ccto != null ? ccto.getCcrVlr().toString() : JspHelper.verificaVarQryStr(request, ("ccrVlr" + Integer.toString(j))));
            ccrVlr = NumberHelper.reformat(ccrVlr, "en", NumberHelper.getLang(), 9, 20);
            ccrVlrAcumulado = (ccto != null && ccto.getCcrVlrAcumulado() != null ? ccto.getCcrVlrAcumulado().toString() : "");
            ccrVlrAcumulado = !ccrVlrAcumulado.equals("") ? NumberHelper.reformat(ccrVlrAcumulado, "en", NumberHelper.getLang(), 9, 20) : "";
      %>        
      <div class="row" id="teste<%=j%>">
        <div class="form-group col-sm-2">
          <label for="<%=TextHelper.forHtmlAttribute("ccrMes" + Integer.toString(j))%>"><hl:message key="rotulo.mes.coeficiente.correcao"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute("ccrMes" + Integer.toString(j))%>"
                        di="<%=TextHelper.forHtmlAttribute("ccrMes" + Integer.toString(j))%>" 
                        type="text" 
                        classe="form-control" 
                        mask="#D2" 
                        size="8" 
                        others="readonly" 
                        value="<%=TextHelper.forHtmlAttribute(ccrMes)%>" 
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.coeficiente.placeholder.mes", responsavel)%>"/>
        </div>
        <div class="form-group col-sm-2">
          <label for="<%=TextHelper.forHtmlAttribute("ccrAno" + Integer.toString(j))%>"><hl:message key="rotulo.ano.coeficiente.correcao"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute("ccrAno" + Integer.toString(j))%>" 
                        di="<%=TextHelper.forHtmlAttribute("ccrAno" + Integer.toString(j))%>"
                        type="text" 
                        classe="form-control"
                        mask="#D4"  
                        size="8" 
                        others="readonly" 
                        value="<%=TextHelper.forHtmlAttribute(ccrAno)%>" 
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.coeficiente.placeholder.ano", responsavel)%>"/>
        </div>
        <div class="form-group col-sm-3">
          <label for="<%=TextHelper.forHtmlAttribute("ccrVlr" + Integer.toString(j))%>"><hl:message key="rotulo.valor.coeficiente.correcao"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute("ccrVlr" + Integer.toString(j))%>" 
                        di="<%=TextHelper.forHtmlAttribute("ccrVlr" + Integer.toString(j))%>" 
                        type="text" 
                        classe="form-control" 
                        mask="#F15" 
                        size="15" 
                        others="readonly" 
                        value="<%=TextHelper.forHtmlAttribute(ccrVlr)%>" 
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.coeficiente.placeholder.valor", responsavel)%>"/>
        </div>
        <div class="form-group col-sm-3">
          <label for="<%=TextHelper.forHtmlAttribute("ccrVlrAcumulado" + Integer.toString(j))%>"><hl:message key="rotulo.valor.acumulado.coeficiente.correcao"/></label>
          <hl:htmlinput name="<%=TextHelper.forHtmlAttribute("ccrVlrAcumulado" + Integer.toString(j))%>" 
                        di="<%=TextHelper.forHtmlAttribute("ccrVlrAcumulado" + Integer.toString(j))%>" 
                        type="text" 
                        classe="form-control" 
                        mask="#F15" 
                        size="15" 
                        others="readonly" 
                        value="<%=TextHelper.forHtmlAttribute(ccrVlrAcumulado)%>" 
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.coeficiente.placeholder.valor.acumulado", responsavel)%>"/>
                        
        </div>
        <% if (tamanhoLista == 1) { %> 
          <div class="col-sm-1 mt-3 pt-2">
            <div class="btn-action ">
              <a class="btn btn-outline-danger pr-0" href="javascript:void(0)"  onClick="ExcluirCoeficiente('<%=TextHelper.forJavaScript(ccrTccCodigo)%>', '', '','', '','CCR', '../v3/manterCoeficienteCorrecao?acao=excluir&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(tccDescricao)%>')" id="removeInput0">
                <svg class="excluir" width="20">
                  <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-status-x"></use>
                </svg>
              </a>
            </div>
          </div>
        <%} else if (!readOnly.equals("true")) { %>         
            <div class="col-sm-1 mt-3 pt-2">
              <div class="btn-action ">
                <a class="btn btn-outline-danger pr-0" href="javascript:void(0)"  onClick="ExcluirCoeficiente('<%=TextHelper.forJavaScript(ccrTccCodigo)%>', '<%=TextHelper.forJavaScript(ccrMes)%>', '<%=TextHelper.forJavaScript(ccrAno)%>', '<%=TextHelper.forJavaScript(ccrVlr )%>' , '<%=TextHelper.forJavaScript(ccrVlrAcumulado)%>','CCR', '../v3/manterCoeficienteCorrecao?acao=excluir&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(tccDescricao)%>' , '<%=TextHelper.forJavaScript(formaCalc)%>')" id="removeInput0">
                  <svg class="excluir" width="20">
                    <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-status-x"></use>
                  </svg>
                </a>
              </div>
            </div>
        <%}%>    
      </div>
      <%
          j++;
          }
        }
      %>
      <%if (!readOnly.equals("true")){ %>
       
      <div class="row" id="teste0">
        <div class="form-group col-sm-2">
          <label for="ccrMes0"><hl:message key="rotulo.mes.coeficiente.correcao"/></label>
          <hl:htmlinput classe="form-control"
                        di="ccrMes0" 
                        name="ccrMes0"
                        type="text"
                        size="8"
                        mask="#D2"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.coeficiente.placeholder.mes", responsavel)%>"/>           
        </div>
        <div class="form-group col-sm-2">
          <label for="ccrAno0"><hl:message key="rotulo.ano.coeficiente.correcao"/></label>
          <hl:htmlinput classe="form-control"
                        di="ccrAno0"
                        name="ccrAno0"
                        type="text" 
                        size="8"                         
                        mask="#D4"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.coeficiente.placeholder.ano", responsavel)%>"/>
        </div>
        <div class="form-group col-sm-3">
          <label for="ccrVlr0"><hl:message key="rotulo.valor.coeficiente.correcao"/></label>
          <hl:htmlinput classe="form-control"
                        di="ccrVlr0"
                        name="ccrVlr0"
                        type="text"
                        size="15"
                        mask="#F20"
                        nf="salvar"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.coeficiente.placeholder.valor", responsavel)%>"/>
        </div>
        <div class="form-group col-sm-3">
          <label for="ccrVlrAcumulado0"><hl:message key="rotulo.valor.acumulado.coeficiente.correcao"/></label>
          <hl:htmlinput classe="form-control"
                        name="ccrVlrAcumulado0"
                        type="text"
                        size="15"
                        value=""
                        mask="#F20"
                        di="ccrVlrAcumulado0"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.coeficiente.placeholder.valor.acumulado", responsavel)%>"/>
        </div>
        <%if(TextHelper.isNull(formaCalc) || !formaCalc.equals(CodedValues.FORMA_CALCULO_PADRAO)) {//aparece o campo de texto para valor acumulado%>
      	 <script> document.getElementById('ccrVlrAcumulado0').style.visibility='visible';</script>
        <% } else {%>
      	 <script> document.getElementById('ccrVlrAcumulado0').style.visibility='hidden';</script>
      	<%}%>
        
        <% if (!TextHelper.isNull(ccrTccCodigo)) { %>         
        <div class="col-sm-1 mt-3 pt-2">
          <div class="btn-action ">
            <a class="btn btn-outline-danger pr-0" href="javascript:void(0)" id="removeInput">
              <svg class="excluir" width="20">
                <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-status-x"></use></svg>
            </a>
          </div>
        </div>
        <%}%>    
      </div>       
      <%}%>
    </div>  
  </div>    
    <div class="btn-action mr-1">
      <input type="hidden" name="ALTERA_CCR" value="0">
      <input type="hidden" name="OPERACAO" value="SALVAR">
      <input type="hidden" name="MM_update" value="form1">
      <input type="hidden" name="tipo" value="editar">
      <hl:htmlinput name="ccrTccCodigo" type="hidden" value="<%=TextHelper.forHtmlAttribute(ccrTccCodigo)%>" />
      <% if (!readOnly.equals("true")){ %>
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/manterCoeficienteCorrecao?acao=iniciar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>'); return false;" aria-label="Voltar"><hl:message key="rotulo.coeficiente.botao.cancelar"/></a>
      <a class="btn btn-primary" href="#no-back" onClick="if (validarCC()) {f0.submit(); return false;}" id="salvar"><hl:message key="rotulo.coeficiente.botao.salvar"/></a>
      <%} else {%>
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/manterCoeficienteCorrecao?acao=iniciar'); return false;"><hl:message key="rotulo.coeficiente.botao.voltar"/></a>
      <%}%>  
    </div> 
</form>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
  function formLoad()
  {
  focusFirstField();
  }

  function validarCC()
  {
    if (<%=(boolean)((!TextHelper.isNull(ccrTccCodigo)) && (listaCoeficientesCorrecao != null) && (tamanhoLista > 0))%>){
      for (i = 1; i < <%=(int)tamanhoLista%> + 1; i++) {
        field = getElt("ccrVlr" + i);
        if (field.value == '') {
          alert('<hl:message key="mensagem.valor.coeficiente.correcao.obrigatorio"/>');
          return false;
        }
      }
    }
    field = getElt("tccDescricao");
    if (field.value == '') {
      alert('<hl:message key="mensagem.descricao.coeficiente.correcao.obrigatorio"/>');
      field.focus();
      return false;
    }
      
    field = getElt("ccrMes0");
    field2 = getElt("ccrAno0");
    field3 = getElt("ccrVlr0"); 
    field4 = getElt("formaCalcSelec"); 
    if (field4.value == '') {
      alert('<hl:message key="mensagem.forma.calculo.coeficiente.correcao.obrigatorio"/>');
      field4.focus();
      return false;
    }
    
    if ((parseInt(field.value) <= 0) || (parseInt(field.value) >= 13)){
          alert('<hl:message key="mensagem.mes.coeficiente.correcao.obrigatorio"/>');
          return false;
    }
    if ((parseInt(field2.value) <= 1900) || (parseInt(field2.value) >= 2100)){
          alert('<hl:message key="mensagem.ano.coeficiente.correcao.obrigatorio"/>');
          field2.focus();
          return false;
    }
    if ((field.value != '') || (field2.value != '') || (field3.value != '') || <%=(boolean)(TextHelper.isNull(ccrTccCodigo) || tamanhoLista == 0)%>){
      return vf_cadastro_ccr();  
    }
    
    
    return true;
  }

  function ExcluirCoeficiente(codigo, mes, ano, vlr, vlrAcumadlo,tipo, alink, tccDescricao, formaCalc)
  {
    var url = alink + (alink.indexOf('?') == -1 ? "?" : "&")  + "codigo=" + codigo + "&ccrAno=" + ano + "&ccrMes=" + mes + "&tccDescricao=" + tccDescricao
              + "&formaCalc=" + formaCalc + "&excluir=sim&ccrVlr=" + vlr + "&ccrVlrAcumulado=" + vlrAcumadlo + "&formaCalcSelec=<%=TextHelper.forJavaScriptBlock(formaCalcSelec)%>";
    return ConfirmaUrl('<hl:message key="mensagem.confirma.exclusao.coeficiente.correcao"/>', url);
  }
  
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
