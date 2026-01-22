<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%

AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

Boolean editar = (Boolean) request.getAttribute("editar");
Map svcBloqEdicaoTaxas = (Map) request.getAttribute("svcBloqEdicaoTaxas");
String svc_descricao = (String) request.getAttribute("svc_descricao");
String titulo = (String) request.getAttribute("titulo");
String svc_codigo = (String) request.getAttribute("svc_codigo");
Boolean temCET = (Boolean) request.getAttribute("temCET");
Boolean ocultarCamposTac = (Boolean) request.getAttribute("ocultarCamposTac");
String tac = (String) request.getAttribute("tac");
Boolean readOnly = (Boolean) request.getAttribute("readOnly");
String op = (String) request.getAttribute("op");
Boolean tipoMensal = (Boolean) request.getAttribute("tipoMensal");
List prazos = (List) request.getAttribute("prazos");
Integer minDia = (Integer) request.getAttribute("minDia");
Integer maxDia = (Integer) request.getAttribute("maxDia");
List coeficientes = (List) request.getAttribute("coeficientes");
String csa_codigo = (String) request.getAttribute("csa_codigo");
String tipo = (String) request.getAttribute("tipo");
String svcDescricao = (String) request.getAttribute("svcDescricao");

%>
<c:set var="title">
  <hl:message key="rotulo.coeficiente.lst.titulo.semarg"/> <%=TextHelper.forHtmlContent(svc_descricao.toUpperCase())%> - <%=TextHelper.forHtmlContent(titulo.toUpperCase())%>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
<FORM NAME="form1" METHOD="post" ACTION="../v3/editarCoeficiente?<%=SynchronizerToken.generateToken4URL(request)%>">
  <% if (!temCET && !ocultarCamposTac) { %>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.coeficiente.taxas.fixas"/></h2>
    </div>
    <div class="card-body">
      <% if (editar) { %>            
      <div class="alert alert-warning mb-0" role="alert">
        <p class="mb-1"><hl:message key="mensagem.coeficiente.tac.op"/></p> 
      </div>
      <% } %>
      <div class="alert alert-warning mb-0" role="alert">
        <p class="mb-1"><hl:message key="mensagem.coeficiente.descricao.uso.parametros"/></p>
        <p class="mb-1 pl-2"><span class="font-weight-bold"><hl:message key="mensagem.coeficiente.descricao.parametro.valor.liquido.liberado"/></span></p>
        <p class="mb-1 pl-4"> <hl:message key="mensagem.coeficiente.formula.valor.prestacao"/></p>
        <p class="mb-1 pl-2"><span class="font-weight-bold"><hl:message key="mensagem.coeficiente.descricao.parametro.valor.prestacao"/></span></p>
        <p class="mb-1 pl-4"> <hl:message key="mensagem.coeficiente.formula.valor.liberado"/></p>
      </div>
      <% if (!editar && svcBloqEdicaoTaxas.containsKey(svc_codigo)) { %>  
      <div class="alert alert-warning" role="alert">
        <p class="mb-1"><hl:message key="mensagem.coeficiente.taxas.compartilhadas"/></p>       
        <% if (!TextHelper.isNull(svcDescricao)) { %>                
        <p class="mb-1"><hl:message key="mensagem.coeficiente.editar.taxas"/> <%=TextHelper.forHtmlContent(svcDescricao)%>.</p>
        <% } %>
      </div>
      <% } %>
      <div class="form-group mt-3">
        <div class="row">
          <div class="col-sm-6">
            <label for="tps_<%=(String)CodedValues.TPS_TAC_FINANCIADA%>"><hl:message key="rotulo.coeficiente.tac"/></label>
            <input id="tps_<%=(String)CodedValues.TPS_TAC_FINANCIADA%>" name="tps_<%=(String)CodedValues.TPS_TAC_FINANCIADA%>" placeholder='<hl:message key="rotulo.coeficiente.digite.tac"/>' value="<%=TextHelper.forHtmlAttribute(tac)%>" old="<%=TextHelper.forHtmlAttribute(tac)%>" type="text" onFocus="SetarEventoMascara(this,'#F7',true);" onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" CLASS="form-control" size="7" <%=(String)(readOnly ? "disabled" : "")%>>
          </div>
          <div class="col-sm-6">
            <label for="tps_<%=(String)CodedValues.TPS_OP_FINANCIADA%>"><hl:message key="rotulo.coeficiente.op"/></label>
            <INPUT id="tps_<%=(String)CodedValues.TPS_OP_FINANCIADA%>" name="tps_<%=(String)CodedValues.TPS_OP_FINANCIADA%>" placeholder='<hl:message key="rotulo.coeficiente.digite.op"/>' value="<%=TextHelper.forHtmlAttribute(op)%>" old="<%=TextHelper.forHtmlAttribute(op)%>" TYPE="text" onFocus="SetarEventoMascara(this,'#F7',true);" onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" CLASS="form-control" SIZE="7" <%=(String)(readOnly ? "disabled" : "")%>>
          </div>
        </div>
      </div>     
    </div>
  </div>  
  <% } %>
  
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.coeficiente.plural"/></h2>
    </div>
    <% if (!tipoMensal) { %>
    <div class="card-body table-responsive">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col" class="pt-0"><hl:message key="rotulo.coeficiente.dia"/></th>
            <%
              Iterator it = prazos.iterator();
              
              while (it.hasNext()) {
                 PrazoTransferObject pto = (PrazoTransferObject)it.next();
            %>
              <th scope="col" class="text-nowrap  text-center"><hl:message key="rotulo.coeficiente.prazo"/>  <%=TextHelper.forHtmlContent(pto.getPrzVlr())%></th>
            <%}%>
          </tr>
        </thead>
        <tbody>
          <%
            int j = 0;
            for (int i=minDia; i<=maxDia; i++) {
          %>
          <tr>
            <td class="font-weight-bold"><%=(int)i%></td>
            <% 
            it = prazos.iterator();
            while (it.hasNext()) {
              PrazoTransferObject pto = (PrazoTransferObject)it.next();
              Short prz_vlr = pto.getPrzVlr();
              String nome_campo = i + "_" + prz_vlr;
              CustomTransferObject cto = (coeficientes.size() > j) ? (CustomTransferObject)coeficientes.get(j) : null;
              StringBuffer hdrbotoes = new StringBuffer();
              hdrbotoes.append("<a HREF=\"#no-back\" class=\"btn btn-primary\" onClick=\"copiar(").append(pto.getPrzVlr()).append(");return false;\">").append(ApplicationResourcesHelper.getMessage("rotulo.coeficiente.copiar.todos", responsavel)).append("</a>");
              
              String cft_vlr = "";
              String cft_codigo = "";
              
                if (cto != null &&
                    cto.getAttribute(Columns.CFT_DIA).toString().equals(String.valueOf(i)) &&
                    cto.getAttribute(Columns.PRZ_VLR).toString().equals(prz_vlr.toString())) {
                  cft_vlr = NumberHelper.reformat(cto.getAttribute(Columns.CFT_VLR).toString(), "en", NumberHelper.getLang(), 2, 20);
                  cft_codigo = (cto.getAttribute(Columns.CFT_DATA_INI_VIG) == null && cto.getAttribute(Columns.CFT_CODIGO) != null) ? cto.getAttribute(Columns.CFT_CODIGO).toString() : "";
                  j++;
                }  
            %>
            
            <td align="center">
              <% if (i == minDia) { %>
              <div class="row justify-content-right">
                <div class="col-sm-12">
                  <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(\"cft_codigo_\" + nome_campo)%>" type="hidden" value="<%=TextHelper.forHtmlAttribute(cft_codigo)%>" />
                  <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(\"cft_\" + nome_campo)%>" placeHolder="<%= ApplicationResourcesHelper.getMessage("rotulo.coeficiente.digite.coeficiente", responsavel) %>" type="text" classe="form-control" mask="#F20" size="10" value="<%=TextHelper.forHtmlAttribute(cft_vlr)%>" others="<%=TextHelper.forHtmlAttribute(\"old='\" + cft_vlr + \"'\" + (readOnly ? \" disabled\" : \"\"))%>" />
                </div>
                <div class="col-sm-12">
                  <div class=" mt-2 mb-0 mt-2 mb-0">
                    <% out.println(hdrbotoes.toString()); %>
                  </div>
                </div>
              </div>
              <% } else { %>
              <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(\"cft_codigo_\" + nome_campo)%>" type="hidden" value="<%=TextHelper.forHtmlAttribute(cft_codigo)%>" />
              <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(\"cft_\" + nome_campo)%>" placeHolder="<%= ApplicationResourcesHelper.getMessage("rotulo.coeficiente.digite.coeficiente", responsavel) %>" type="text" classe="form-control" mask="#F20" size="10" value="<%=TextHelper.forHtmlAttribute(cft_vlr)%>" others="<%=TextHelper.forHtmlAttribute(\"old='\" + cft_vlr + \"'\" + (readOnly ? \" disabled\" : \"\"))%>" />
              <% } %>
            </td>
            <% } %>
          </tr>
          <% } %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="60"><hl:message key="rotulo.listagem.coeficientes"/></td>
          </tr>
        </tfoot>
      </table>
    </div>
    <% } else {%>
    <div class="card-body">
      <div class="form-group">
        <div class="row">
          <%
            Iterator it = prazos.iterator();
            int j = 0;
            for (int i=minDia; i<=maxDia; i++) {
          %>
          <% 
          it = prazos.iterator();
          while (it.hasNext()) {
            PrazoTransferObject pto = (PrazoTransferObject)it.next();
            Short prz_vlr = pto.getPrzVlr();
            String nome_campo = i + "_" + prz_vlr;
            CustomTransferObject cto = (coeficientes.size() > j) ? (CustomTransferObject)coeficientes.get(j) : null;
            StringBuffer hdrbotoes = new StringBuffer();
            hdrbotoes.append("<a HREF=\"#no-back\" class=\"btn btn-primary\" onClick=\"copiar(").append(pto.getPrzVlr()).append(");return false;\">").append(ApplicationResourcesHelper.getMessage("rotulo.coeficiente.copiar.todos", responsavel)).append("</a>");
            
            String cft_vlr = "";
            String cft_codigo = "";
            
              if (cto != null &&
                  cto.getAttribute(Columns.CFT_DIA).toString().equals(String.valueOf(i)) &&
                  cto.getAttribute(Columns.PRZ_VLR).toString().equals(prz_vlr.toString())) {
                cft_vlr = NumberHelper.reformat(cto.getAttribute(Columns.CFT_VLR).toString(), "en", NumberHelper.getLang(), 2, 20);
                cft_codigo = (cto.getAttribute(Columns.CFT_DATA_INI_VIG) == null && cto.getAttribute(Columns.CFT_CODIGO) != null) ? cto.getAttribute(Columns.CFT_CODIGO).toString() : "";
                j++;
              }  
          %>
          <div class="col-sm-3">
            <label class="ml-2" for="<%=TextHelper.forHtmlAttribute("cft_" + nome_campo)%>"><%=TextHelper.forHtmlContent(pto.getPrzVlr())%></label>
            <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(\"cft_codigo_\" + nome_campo)%>" type="hidden" value="<%=TextHelper.forHtmlAttribute(cft_codigo)%>" />
            <hl:htmlinput di="<%=TextHelper.forHtmlAttribute(\"cft_\" + nome_campo)%>" name="<%=TextHelper.forHtmlAttribute(\"cft_\" + nome_campo)%>" placeHolder="<%= ApplicationResourcesHelper.getMessage("rotulo.coeficiente.digite.coeficiente", responsavel) %>" type="text" classe="form-control" mask="#F20" size="10" value="<%=TextHelper.forHtmlAttribute(cft_vlr)%>" others="<%=TextHelper.forHtmlAttribute(\"old='\" + cft_vlr + \"'\" + (readOnly ? \" disabled\" : \"\"))%>" />
          </div>
          <% }
          }%>
        </div>
      </div>
    </div>
    <% } %>
  </div>
  <INPUT TYPE="hidden" NAME="ALTERA_CFT" VALUE="0">
  <INPUT TYPE="hidden" NAME="ALTERA_PSC" VALUE="0">
  <INPUT TYPE="hidden" NAME="acao" VALUE="salvar">
  <INPUT TYPE="hidden" NAME="SVC_CODIGO" VALUE="<%=TextHelper.forHtmlAttribute(svc_codigo)%>">
  <INPUT TYPE="hidden" NAME="SVC_DESCRICAO" VALUE="<%=TextHelper.forHtmlAttribute(svc_descricao)%>">
  <INPUT TYPE="hidden" NAME="CSA_CODIGO" VALUE="<%=TextHelper.forHtmlAttribute(csa_codigo)%>">
  <INPUT TYPE="hidden" NAME="titulo" VALUE="<%=TextHelper.forHtmlAttribute(titulo)%>">
  <INPUT TYPE="hidden" NAME="tipo" VALUE="<%=TextHelper.forHtmlAttribute(tipo)%>">
  <INPUT TYPE="hidden" NAME="MM_update" VALUE="form1">
  
  
  <div class="btn-action">
    <% if (!readOnly) { %>
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
    <a class="btn btn-primary" href="#no-back" onClick="vf_cadastro_coeficiente(); f0.submit(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
    <% } else { %>
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a> 
    <% } %>
  </div>
</FORM>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
var f0 = document.forms[0];

function vf_cadastro_coeficiente() {
  for (i=0; i < f0.elements.length; i++) {
    var e = f0.elements[i];
    if ((e.type == 'text') && (e.value != e.old)) {
      if (e.name.indexOf('cft_') != -1) {
        f0.ALTERA_CFT.value = "1";
      } else if (e.name.indexOf('tps_') != -1) {
        f0.ALTERA_PSC.value = "1";
      }
    }
  }
}

function copiar(prazo) {
 if (confirm('<hl:message key="mensagem.coeficiente.confirmacao.copia"/>')) {
   for (i=1;i<=31;i++) {
     nome_campo = "cft_" + i + "_" + prazo;
     campoDestino = $(nome_campo);
     campoOrigem = $("cft_1_" + prazo);
     campoDestino.val(campoOrigem.val());
   }
 }
}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>