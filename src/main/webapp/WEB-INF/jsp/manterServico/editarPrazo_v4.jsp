<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.io.*"%>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>

<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.entidade.PrazoTransferObject" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String svc_codigo = (String) request.getAttribute("svcCodigo");
String titulo = (String) request.getAttribute("titulo");
boolean podeEditarPrazo = (Boolean) request.getAttribute("podeEditarPrazo");
List<PrazoTransferObject> prazos = (List<PrazoTransferObject>) request.getAttribute("prazos");
%>
<c:set var="title">
  <%=podeEditarPrazo ? ApplicationResourcesHelper.getMessage("rotulo.prazo.edicao.titulo", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.prazo.consulta.titulo", responsavel) %><%=TextHelper.forHtmlContent(!titulo.equals("") ? " - " + titulo : "")%>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
<form action="../v3/editarPrazo?<%=SynchronizerToken.generateToken4URL(request)%>"  method="POST" name="form1">
<input type="hidden" name="SVC_CODIGO" value="<%=TextHelper.forHtmlAttribute(svc_codigo)%>">
<input type="hidden" name="titulo" value="<%=TextHelper.forHtmlAttribute(titulo)%>">
<input type="hidden" name="acao" value="">
  <div class="row">
    <% if (podeEditarPrazo) { %>
      <div class="col-sm-12 col-md-12 mb-2">
        <div class="float-end">
          <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit"><hl:message key="rotulo.mais.acoes"/></button>
          <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes" x-placement="bottom-end" style="position: absolute; transform: translate3d(1009px, 50px, 0px); top: 0px; left: 0px; will-change: transform;">
            <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarPrazo?acao=bloquearTudo&SVC_CODIGO=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&titulo=<%=TextHelper.forJavaScript(titulo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;"><hl:message key="rotulo.prazo.bloquear.todos.botao"/></a>
            <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarPrazo?acao=desbloquearTudo&SVC_CODIGO=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&titulo=<%=TextHelper.forJavaScript(titulo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;"><hl:message key="rotulo.prazo.desbloquear.todos.botao"/></a>
          </div>
        </div>
      </div>
      <div class="col-sm-5">
        <div class="card">
          <div class="card-header hasIcon pl-3">
            <h2 class="card-header-title"><hl:message key="rotulo.prazo.insercao.prazo"/></h2>
          </div>
          <div class="card-body">
            <div class="row">
              <div class="form-group col-sm-12 col-md-12">
                <div class="row mt-2" role="group" aria-labelledby="De">
                  <div class="form-check pt-2 col-sm-12 col-md-1 p-2">
                    <div class="float-left align-middle mt-4 form-control-label">
                      <label for="PRZ_VLR_INI"><hl:message key="rotulo.prazo.de"/></label>
                    </div>
                  </div>
                  <div class="form-check pt-2 col-sm-12 col-md-11">
                    <input type="text" class="form-control w-100" name="PRZ_VLR_INI" id="PRZ_VLR_INI" placeholder="<%=ApplicationResourcesHelper.getMessage("rotulo.prazo.de.placeholder", responsavel) %>" onFocus="SetarEventoMascara(this,'#D3',true);" onBlur="fout(this);ValidaMascara(this);vf_interval();" size="5" maxlength="3">
                  </div>
                  <div class="form-check pt-2 col-sm-12 col-md-1 p-2 ">
                    <div class="float-left align-middle mt-4 form-control-label">
                      <label for="PRZ_VLR_FIM"><hl:message key="rotulo.prazo.ate"/></label>
                    </div>
                  </div>
                  <div class="form-check pt-2 col-sm-12 col-md-11">
                    <input type="text" class="form-control w-100" id="PRZ_VLR_FIM" NAME="PRZ_VLR_FIM" placeholder="<%=ApplicationResourcesHelper.getMessage("rotulo.prazo.ate.placeholder", responsavel) %>" onFocus="SetarEventoMascara(this,'#D3',true);" onBlur="fout(this);ValidaMascara(this);vf_interval();" size="5" maxlength="3">
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="btn-action">
          <a class="btn btn-outline-danger ml-1" href="#no-back" onClick="vf_cadastro_prazo_bloqueio('desbloquear'); return false;"><hl:message key="rotulo.prazo.desbloquear"/></a>
          <a class="btn btn-outline-danger ml-1" href="#no-back" onClick="vf_cadastro_prazo_bloqueio('bloquear'); return false;"><hl:message key="rotulo.prazo.bloquear"/></a>
          <a class="btn btn-primary ml-1" onClick="vf_cadastro_prazo(); return false;" href="#no-back"><hl:message key="rotulo.prazo.inserir"/></a>
        </div>
      </div>
    <% } %>
    <div class="col-sm-7">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.prazo.disponiveis"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.prazo.singular"/></th>
                <th scope="col"><hl:message key="rotulo.prazo.situacao"/></th>
                <th scope="col"><hl:message key="rotulo.prazo.acoes"/></th>
              </tr>
            </thead>
            <tbody>
             <%
              if (prazos.size() == 0) {
                out.print("<tr><td colspan=\"3\">" + ApplicationResourcesHelper.getMessage("mensagem.prazo.nenhum.encontrado", responsavel) + "</td></tr>");
              } else {
                Iterator<PrazoTransferObject> it = prazos.iterator();
                PrazoTransferObject prazo = null;
                String prz_codigo, situacao;
                Short prz_ativo, prz_vlr;
              
                int i = 0;
                while (it.hasNext()) {
                  prazo = (PrazoTransferObject)it.next();
                  prz_codigo = prazo.getPrzCodigo();
                  prz_ativo = prazo.getPrzAtivo();
                  prz_vlr = prazo.getPrzVlr();
                  situacao = (prz_ativo.equals(CodedValues.STS_ATIVO) ? ApplicationResourcesHelper.getMessage("rotulo.prazo.situacao.desbloqueado", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.prazo.situacao.bloqueado", responsavel));
              %>
                <tr>
                  <td><%=TextHelper.forHtmlContent(prz_vlr)%></td>
                  <td <%=(prz_ativo.equals(CodedValues.STS_ATIVO) ? "" : "class=\"block\"" )%> ><%=TextHelper.forHtmlContent(situacao)%></td>
                  <% if (podeEditarPrazo) { %>
                    <td><a href="#no-back" onClick="postData('../v3/editarPrazo?acao=salvar&SVC_CODIGO=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&titulo=<%=TextHelper.forJavaScript(titulo)%>&PRZ_CODIGO=<%=TextHelper.forJavaScript(prz_codigo)%>&PRZ_ATIVO=<%=TextHelper.forJavaScript((prz_ativo.equals(CodedValues.STS_ATIVO) ? CodedValues.STS_INATIVO : CodedValues.STS_ATIVO))%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&_skip_history_=true')"><%=(prz_ativo.equals(CodedValues.STS_ATIVO) ? ApplicationResourcesHelper.getMessage("rotulo.prazo.bloquear", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.prazo.desbloquear", responsavel))%></a>
                  <% } else { %>
                    <td><%=(prz_ativo.equals(CodedValues.STS_ATIVO) ? ApplicationResourcesHelper.getMessage("rotulo.prazo.bloquear", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.prazo.desbloquear", responsavel)) %>
                  <% } %>
                </tr>
              <% }
              }%>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="3"><hl:message key="rotulo.prazo.listage.prazo.disponivel"/></td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/manterServico?acao=iniciar&<%=(String)(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
  	f0 = document.forms[0];
</script>
<script type="text/JavaScript">
function formLoad() {
  focusFirstField();
}

function vf_cadastro_prazo () {
  if (!ValidaMascara(f0.PRZ_VLR_INI) || !ValidaMascara(f0.PRZ_VLR_FIM)) {
    alert ('<hl:message key="mensagem.prazo.intervalo.invalido"/>');
    return false;
  } 
  if (f0.PRZ_VLR_INI.value == "") {
    alert ('<hl:message key="mensagem.prazo.preencha.novo.prazo.ser.inserido"/>');
    f0.PRZ_VLR_INI.focus();
    return false;
  }
  f0.acao.value = "inserir";
  f0.submit();
}

function vf_cadastro_prazo_bloqueio (evento) {
    var evntVlr = evento;
    if (!ValidaMascara(f0.PRZ_VLR_INI) || !ValidaMascara(f0.PRZ_VLR_FIM)) {
      alert ('<hl:message key="mensagem.prazo.intervalo.invalido"/>');
      return false;
    } 
    if ((f0.PRZ_VLR_INI.value == "") || (f0.PRZ_VLR_FIM.value == "")) {
      alert ('<hl:message key="mensagem.prazo.preencha.prazo.ser.bloqueado.desbloqueado"/>');
      f0.PRZ_VLR_INI.focus();
      return false;
    }
    if (evntVlr != null && evntVlr == 'bloquear') {
      f0.acao.value = "bloquear";
    } else if (evntVlr != null && evntVlr == 'desbloquear') {
      f0.acao.value = "desbloquear";
    }
    
    return f0.submit();
  }
  
function vf_interval () {
  var ini = Number(f0.PRZ_VLR_INI.value);
  var fim = Number(f0.PRZ_VLR_FIM.value);
  if (!ini == "" && !fim == "") {
    if (ini.valueOf() > fim.valueOf()) {
       f0.PRZ_VLR_INI.value = "";
       f0.PRZ_VLR_FIM.value = "";
       f0.PRZ_VLR_INI.focus();
       alert ('<hl:message key="mensagem.prazo.intervalo.invalido"/>');
    }     
    
    return false;
  }
  return true;
}

	window.onload = formLoad;

</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>