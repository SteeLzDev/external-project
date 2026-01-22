<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject" %>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TransferObjectHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.usuario.LoginHelper"%>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*"%>
<%@ page import="java.math.BigDecimal"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%          
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

UsuarioTransferObject usuario = (UsuarioTransferObject) request.getAttribute("usuario");
List hist = (List) request.getAttribute("hist");
String usuLogin = usuario.getStuCodigo().equals(CodedValues.STU_EXCLUIDO) ? usuario.getUsuTipoBloq() + "(*)" : usuario.getUsuLogin();
String usuCpf = usuario.getUsuCPF();
String ipAcesso = usuario.getUsuIpAcesso();
if (!TextHelper.isNull(ipAcesso)) {
    ipAcesso = ipAcesso.replaceAll(";", "<br>&nbsp;");
}
String ddnsAcesso =  usuario.getUsuDDNSAcesso();
if (!TextHelper.isNull(ipAcesso)) {
    ipAcesso = ipAcesso.replaceAll(";", "<br>&nbsp;");
}
int qtdColunas = Integer.valueOf(request.getAttribute("qtdColunas").toString());
%>
<c:set var="title">
  <hl:message key="rotulo.usuario.detalhe.titulo"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-5">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.usuario.titulo.dados.usuario"/></h2>
        </div>
        <div class="card-body">
          <dl class="row data-list">
            <dt class="col-5"><hl:message key="rotulo.consignante.singular"/>:</dt>
            <dd class="col-7"><%=TextHelper.forHtmlContent(LoginHelper.getCseNome(responsavel))%></dd>
            <dt class="col-5"><hl:message key="rotulo.usuario.nome"/>:</dt>
            <dd class="col-7"><%=TextHelper.forHtmlContent(usuario.getUsuNome())%></dd>
            <dt class="col-5"><hl:message key="rotulo.usuario.login"/>:</dt>
            <dd class="col-7"><%=TextHelper.forHtmlContent(usuLogin)%></dd>
            <% if (!TextHelper.isNull(usuCpf)) { %>
              <dt class="col-5"><hl:message key="rotulo.usuario.cpf"/>:</dt>
              <dd class="col-7"><%=usuCpf%></dd>
            <% } %>
            <% if (!TextHelper.isNull(ipAcesso)) { %>
              <dt class="col-5"><hl:message key="rotulo.usuario.ips.acesso"/>:</dt>
              <dd class="col-7"><%=ipAcesso%></dd>
            <% } %>
            <% if (!TextHelper.isNull(ddnsAcesso)) { %>
              <dt class="col-5"><hl:message key="rotulo.usuario.enderecos.acesso"/>:</dt>
              <dd class="col-7"><%=ddnsAcesso%></dd>
            <% } %>
            <dt class="col-5"><hl:message key="rotulo.usuario.email"/>:</dt>
            <dd class="col-7"><%=TextHelper.forHtmlContent(usuario.getUsuEmail())%></dd>
          </dl>
        </div>
      </div>
    </div>
    <div class="col-sm-7">
      <div class="card">
        <div class="card-header">
          <span class="card-header-icon"></span>
          <h2 class="card-header-title"><hl:message key="rotulo.usuario.detalhe.subtitulo"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.usuario.data"/></th>
                <th scope="col"><hl:message key="rotulo.usuario.responsavel"/></th>
                <th scope="col"><hl:message key="rotulo.usuario.tipo"/></th>
                <th scope="col"><hl:message key="rotulo.usuario.descricao"/></th>
                <th scope="col"><hl:message key="rotulo.usuario.ip.acesso"/></th>
              </tr>
            </thead>
            <tbody>
            <%
              Iterator it = hist.iterator();
               String ous_data, ous_responsavel, toc_descricao, ous_obs, ous_ip_acesso, tmoDescricao;
               CustomTransferObject cto = null;
               int i = 0;

               while (it.hasNext()) {
                  cto = (CustomTransferObject) it.next();
                  
                  cto = TransferObjectHelper.mascararUsuarioHistorico(cto, "USU_LOGIN_MOD", responsavel);
                  
                  ous_data = DateHelper.toDateTimeString((Date) cto.getAttribute(Columns.OUS_DATA));
                  ous_responsavel = cto.getAttribute("USU_LOGIN_MOD").toString();
                  toc_descricao = cto.getAttribute(Columns.TOC_DESCRICAO).toString();
                  ous_obs = cto.getAttribute(Columns.OUS_OBS).toString();
                  
                  tmoDescricao = cto.getAttribute(Columns.TMO_DESCRICAO) != null ?  cto.getAttribute(Columns.TMO_DESCRICAO).toString() : "";
                  if (!tmoDescricao.equals("")) {
                    String strComplemento = ous_obs.substring(ous_obs.indexOf(".") + 1, ous_obs.length());
                    ous_obs = ous_obs.substring(0, ous_obs.indexOf(".")) +
                              "<br> " + ApplicationResourcesHelper.getMessage("rotulo.servidor.usuario.detalhe.motivo", responsavel) + ": " + tmoDescricao +
                              "<br> " + ApplicationResourcesHelper.getMessage("rotulo.servidor.usuario.detalhe.observacao", responsavel) + ": " + strComplemento;            
                  }
                  
                  ous_ip_acesso = cto.getAttribute(Columns.OUS_IP_ACESSO) != null ? cto.getAttribute(Columns.OUS_IP_ACESSO).toString() : "";
            %>            
              <tr>
                <td><%=TextHelper.forHtmlContent(ous_data)%></td>
                <td><%=TextHelper.forHtmlContent(ous_responsavel)%></td>
                <td><%=TextHelper.forHtmlContent(toc_descricao)%></td>
                <td><%=TextHelper.forHtmlContentComTags(ous_obs)%></td>
                <td><%=TextHelper.forHtmlContent(ous_ip_acesso)%></td>
              </tr>
            <% } %>  
            </tbody>
            <tfoot>
              <tr>
                <td colspan="<%= qtdColunas %>"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.historico.usuario", responsavel) + " - " %><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td>
              </tr>
            </tfoot>
          </table>
        </div>
        <div class="card-footer">
          <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
        </div>                
      </div>
    </div>
  </div>  
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>');" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
f0 = document.forms[0];

function imprime() {
    window.print();
}

function filtrar() {
   f0.submit();
}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>