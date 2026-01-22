<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean readOnly = "true".equalsIgnoreCase((String) request.getAttribute("readOnly"));
List listaSvcBloqueaveisServidor = (List) request.getAttribute("listaSvcBloqueaveisServidor");

String rseCodigo = (String) request.getAttribute("rseCodigo");
String rseMatricula = (String) request.getAttribute("rseMatricula");
String serNomeCodificado = (String) request.getAttribute("serNomeCodificado");
String serNome = (String) request.getAttribute("serNome");

// Obtem os valores dos bloqueios por serviços
List servicosBloqueados = (List) request.getAttribute("servicosBloqueados");

boolean exigeMotivo = (boolean) request.getAttribute("exigeMotivo");
%>
<c:set var="javascript">
<% if (!responsavel.isSer()) { %>
<hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.bloqueio.servico.rse", responsavel)%>" scriptOnly="true" />
<% } %>
<script language="JavaScript" type="text/JavaScript">
var f0 = document.forms[0];
</script>
<script language="JavaScript" type="text/JavaScript" src="../js/scripts_2810.js"></script>
<script language="JavaScript" type="text/JavaScript" src="../js/xbdhtml.js"></script>
<script language="JavaScript" type="text/JavaScript">
function formLoad() {
<% if (!readOnly) { %>
  focusFirstField();
<% } %>
}
function copia_qdte(campoQtd, campoObs) {
  // Navega em todos os campos do formulário
  for (i = 0; i < f0.elements.length; i++) {
    var e = f0.elements[i];
    
    if (e.name.indexOf('svc_') == 0) {
      // Se é campo de Qtd, atribui valor do campo padrão
      e.value = campoQtd.value;
    } else if (e.name.indexOf('svc2_') == 0) {
      // Se é campo de Obs, atribui valor do campo padrão
      e.value = campoObs.value;
    }
  }
}

function checkObs(svc) {
  qtdField = getElt('svc_' + svc);
  obsField = getElt('svc2_' + svc);
  
  if (qtdField.value == '') {
    obsField.value = '';
  }
}

function validaForm(){
	<% if (!responsavel.isSer()) { %>
    var tmoCodigo = getElt('TMO_CODIGO');
//    Se exige motivo, tem que validar a seleção de um motivo e o preenchimento ou não da observação; 
	  if (<%=exigeMotivo %>) {
          if(tmoCodigo.value && confirmaAcaoConsignacao()) {
           f0.submit();
//           Se exige motivo o motivo tem que estar preenchido, do contrário manda alerta para o usuário
           } else if (!tmoCodigo.value){
            	  alert('<hl:message key="mensagem.motivo.operacao.obrigatorio"/>');
                  return false;
              }
	  } else {
//         Se NÃO exige motivo, se o motivo não foi selecionado, pode dar submit
           if (!tmoCodigo.value) {
                f0.submit();
           } 
//         porém se o motivo foi selecionado, tem que verificar se o motivo exige obs
           else if (tmoCodigo.value && confirmaAcaoConsignacao()) {
               f0.submit();
           } 
	    }
	<% } else { %>
		f0.submit();
	<% } %>
}
</script>
</c:set>
<c:set var="title">
  <hl:message key="rotulo.servidor.listar.servicos.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
<form method="post" action="../v3/listarServicoServidor?acao=editar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><%=TextHelper.forHtmlContent(rseMatricula)%> - <%=TextHelper.forHtmlContent(serNome)%></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col" width="10%"><hl:message key="rotulo.servidor.listar.servicos.codigo"/></th>
            <th scope="col" width="35%"><hl:message key="rotulo.servidor.listar.servicos.descricao"/></th>
            <th scope="col" width="11%"><hl:message key="rotulo.servidor.listar.servicos.quantidade"/></th>
            <th scope="col" width="44%"><hl:message key="rotulo.servidor.listar.servicos.observacao"/></th>
          </tr>
        </thead>
        <tbody>
        <%=JspHelper.msgRstVazio(servicosBloqueados.size()==0, "13", "lp")%>
        <%
          boolean primeiro = true;
          StringBuffer svcs = new StringBuffer();
          CustomTransferObject servico = null;
          String svc_codigo, svc_descricao, svc_identificador, psr_vlr, psr_obs, psr_alterado_pelo_servidor;
          List listaSvcAlterados = new ArrayList();
          Iterator it = servicosBloqueados.iterator();
          while (it.hasNext()) {
            servico = (CustomTransferObject)it.next();
            svc_codigo = (String)servico.getAttribute(Columns.SVC_CODIGO);
            svc_descricao = (String)servico.getAttribute(Columns.SVC_DESCRICAO);
            svc_identificador = (String)servico.getAttribute(Columns.SVC_IDENTIFICADOR);
            psr_vlr = (servico.getAttribute(Columns.PSR_VLR) != null) ? servico.getAttribute(Columns.PSR_VLR).toString() : "";
            psr_obs = (servico.getAttribute(Columns.PSR_OBS) != null) ? servico.getAttribute(Columns.PSR_OBS).toString() : "";
            psr_alterado_pelo_servidor = (String)servico.getAttribute(Columns.PSR_ALTERADO_PELO_SERVIDOR);
          
            // Lista de servicos (svcs) a serem atualizados. Se responsavel for servidor, pode atualizar somente
            // servicos bloqueaveis pelo servidor e que nao tenham sido bloqueados pelo gestor
            if (!responsavel.isSer() || (listaSvcBloqueaveisServidor.contains(svc_codigo) && 
                    (TextHelper.isNull(psr_alterado_pelo_servidor) || psr_alterado_pelo_servidor.equals("S"))) ) {
                svcs.append(svc_codigo).append(",");
                listaSvcAlterados.add(svc_codigo);
            }
            
            String rowspan = "1";
            if (primeiro && servicosBloqueados.size() > 1 && !readOnly && !responsavel.isSer()) {
                rowspan = "2";
            }  
        %>
          <tr>
            <td><%=TextHelper.forHtmlContent(svc_identificador.toUpperCase())%></td>
            <td><%=TextHelper.forHtmlContent(svc_descricao.toUpperCase())%></td>
            <% if (!readOnly && (!responsavel.isSer() || listaSvcAlterados.contains(svc_codigo))) { %>
            <td><input type="text" name="svc_<%=TextHelper.forHtmlAttribute(svc_codigo)%>"  id="svc_<%=TextHelper.forHtmlAttribute(svc_codigo)%>"  class="form-control mt-1 pl-1 pr-1" value="<%=TextHelper.forHtmlAttribute(psr_vlr)%>" size="2"  onFocus="SetarEventoMascara(this,'#D2',true);"    onBlur="fout(this);ValidaMascara(this);" onChange="checkObs('<%=TextHelper.forJavaScript(svc_codigo)%>')"></td>
            <% if (primeiro && servicosBloqueados.size() > 1 && !responsavel.isSer()) { %>
            <td><div class="col-sm-12">
                <div class="row">
                  <div class="col-md-12 col-lg-7 mt-1 pl-0 pr-1">
                    <input type="text" name="svc2_<%=TextHelper.forHtmlAttribute(svc_codigo)%>" id="svc2_<%=TextHelper.forHtmlAttribute(svc_codigo)%>" class="form-control" value="<%=TextHelper.forHtmlAttribute(psr_obs)%>" size="30" onFocus="SetarEventoMascara(this,'#*1000',true);" onBlur="fout(this);ValidaMascara(this);">
                  </div>
                  <div class="col-md-12 col-lg-5 mt-1 pl-1">
                    <div class="btn-action text-center mb-0 pl-0 pr-1">
                      <a class="btn btn-primary ml-0" href="#" onClick="copia_qdte(f0.svc_<%=TextHelper.forJavaScript(svc_codigo)%>, f0.svc2_<%=TextHelper.forJavaScript(svc_codigo)%>);"><hl:message key="mensagem.servidor.convenio.copiar.para.todos"/></a>
                    </div>
                  </div>
                </div>
              </div>
            </td>
            <% } else { %>
            <td><input type="text" name="svc2_<%=TextHelper.forHtmlAttribute(svc_codigo)%>" id="svc2_<%=TextHelper.forHtmlAttribute(svc_codigo)%>" class="form-control" value="<%=TextHelper.forHtmlAttribute(psr_obs)%>" size="30" onFocus="SetarEventoMascara(this,'#*1000',true);" onBlur="fout(this);ValidaMascara(this);"></td>
            <% } 
             } else { %>
            <td><input type="text" name="svc_<%=TextHelper.forHtmlAttribute(svc_codigo)%>"  class="form-control pl-1 pr-1" value="<%=TextHelper.forHtmlAttribute(psr_vlr)%>" size="2"  disabled></td>
            <td><input type="text" name="svc2_<%=TextHelper.forHtmlAttribute(svc_codigo)%>" class="form-control" value="<%=TextHelper.forHtmlAttribute(psr_obs)%>" size="30" disabled></td>
            <% } %>
          </tr>
          <%
              primeiro = false;
            }
          %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="4"><hl:message key="rotulo.servidor.listagem.servicos"/></td>
          </tr>
        </tfoot>
      </table>
    </div>
  </div>
  <% if (!responsavel.isSer()) { %>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.motivo.operacao.singular"/></h2>
    </div>
    <div class="card-body">
      <%-- Utiliza a tag library EfetivaAcaoMotivoOperacaoTag.java para exibir os dados do Tipo de Motivo da Operação --%>
      <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.bloqueio.servico.rse", responsavel)%>" inputSizeCSS="col-sm-12"/>
      <%-- Fim dos dados do Motivo da Operação --%>
    </div>
  </div>
  <% } %>
  <div class="btn-action">
    <% if (readOnly || (responsavel.isSer() && listaSvcBloqueaveisServidor.isEmpty())) { %>
      <a class="btn btn-outline-danger" href="#" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" title="<hl:message key="rotulo.botao.voltar"/>"><hl:message key="rotulo.botao.voltar"/></a>
    <% } else { %>
      <a class="btn btn-outline-danger" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" href="#"><hl:message key="rotulo.botao.cancelar"/></a>
      <a class="btn btn-primary" id="btnEnvia" href="#" onClick="validaForm(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
      <INPUT TYPE="hidden" NAME="RSE_CODIGO"         VALUE="<%=TextHelper.forHtmlAttribute(rseCodigo)%>">    
      <INPUT TYPE="hidden" NAME="tipo"               VALUE="editar">
      <INPUT TYPE="hidden" NAME="RSE_MATRICULA"      VALUE="<%=TextHelper.forHtmlAttribute(rseMatricula)%>">
      <INPUT TYPE="hidden" NAME="SER_NOME"           VALUE="<%=TextHelper.forHtmlAttribute(serNomeCodificado)%>">
      <INPUT TYPE="hidden" NAME="servicosBloqueados" VALUE="<%=TextHelper.forHtmlAttribute((svcs))%>">
    <% } %>
  </div>
</form>  
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>