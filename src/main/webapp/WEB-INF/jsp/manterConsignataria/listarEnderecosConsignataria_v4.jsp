<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.dto.web.DadosConsignataria"%>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<CustomTransferObject> lstEnderecos = (List<CustomTransferObject>) request.getAttribute("listaEnderecosConsignataria");
String csaCodigo = (String) request.getAttribute("CSA_CODIGO");
%>
<c:set var="title">
<hl:message key="rotulo.listar.enderecos.consignataria.titulo"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
    <div class="row  d-flex flex-row-reverse mr-0">
      <div class="pull-right">
        <div class="btn-action ">
          <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/manterEnderecosConsignataria?acao=editar&CSA_CODIGO=<%=csaCodigo%>&<%=SynchronizerToken.generateToken4URL(request)%>')">
            <hl:message key="rotulo.criar.novo.endereco.consignataria" /></a>
        </div>
      </div>
    </div>
  
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title">
          <hl:message key="rotulo.listar.enderecos.consignataria.titulo"/>
        </h2>
      </div>
          
      <div class="card-body table-responsive">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th scope="col"><hl:message key="rotulo.endereco.consignataria.tipo.endereco"/></th>
              <th scope="col"><hl:message key="rotulo.endereco.consignataria.cep"/></th>
              <th scope="col"><hl:message key="rotulo.endereco.consignataria.logradouro"/></th>
              <th scope="col"><hl:message key="rotulo.endereco.consignataria.numero"/></th>
              <th scope="col"><hl:message key="rotulo.endereco.consignataria.complemento"/></th>
              <th scope="col"><hl:message key="rotulo.endereco.consignataria.uf"/></th>
              <th scope="col"><hl:message key="rotulo.endereco.consignataria.municipio"/></th>
              <th scope="col"><hl:message key="rotulo.acoes"/></th>
            </tr>
          </thead>
          <tbody>
          <%=JspHelper.msgRstVazio(lstEnderecos.size() == 0, 8, responsavel)%>
          <%Iterator<CustomTransferObject> it = lstEnderecos.iterator();
            while (it.hasNext()) {
              CustomTransferObject endereco = (CustomTransferObject) it.next();
              String cep = (String) endereco.getAttribute(Columns.ENC_CEP);
              String logradouro = (String) endereco.getAttribute(Columns.ENC_LOGRADOURO);
              String numero = (String) endereco.getAttribute(Columns.ENC_NUMERO);
              String uf = (String) endereco.getAttribute(Columns.ENC_UF);
              String municipio = (String) endereco.getAttribute(Columns.ENC_MUNICIPIO);
              String tipoEndereco = (String) endereco.getAttribute(Columns.TIE_DESCRICAO);
              String complemento = (String) endereco.getAttribute(Columns.ENC_COMPLEMENTO);
              String codigo = (String) endereco.getAttribute(Columns.ENC_CODIGO);
          %>  
            <tr>
              <td><%=TextHelper.forHtmlContent(tipoEndereco) %></td>
              <td><%=TextHelper.forHtmlContent(cep) %></td>
              <td><%=TextHelper.forHtmlContent(logradouro) %></td>
              <td><%=TextHelper.forHtmlContent(numero) %></td>
              <td><%=TextHelper.forHtmlContent(complemento) %></td>
              <td><%=TextHelper.forHtmlContent(uf) %></td>
              <td><%=TextHelper.forHtmlContent(municipio) %></td>
              <td>
                <div class="actions">
                  <div class="dropdown">
                    <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                      <div class="form-inline">
                        <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>"> <svg>
                            <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                        </span> 
                        <hl:message key="rotulo.botao.opcoes"/>
                      </div>
                    </a>
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                      <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterEnderecosConsignataria?acao=editar&ENC_CODIGO=<%=TextHelper.forJavaScriptAttribute(codigo)%>&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.acoes.editar"/></a>
                      <a class="dropdown-item" href="#no-back" onClick="javascript: verificaExclusao('<%=TextHelper.forJavaScriptAttribute(logradouro)%>', '<%=TextHelper.forJavaScriptAttribute(codigo)%>');"><hl:message key="rotulo.acoes.excluir"/></a>
                    </div>
                  </div>
                </div>
              </td>
            </tr>
          <%}%>
          </tbody>
          <tfoot>
            <tr>
              <td colspan="4"><hl:message key="mensagem.endereco.consignataria.lista.disponiveis"/> - <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span>
              </td>
            </tr>
          </tfoot>
        </table>
      </div>
      <div class="card-footer">
        <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
      </div>
    </div>
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
    </div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
f0 = document.forms[0];

function verificaExclusao(logradouro, encCodigo) {
  if (confirm(mensagem("mensagem.confirmacao.endereco.consignataria.exclusao").replace('{0}', logradouro))) {
    postData('../v3/manterEnderecosConsignataria?acao=excluir&ENC_CODIGO='+encCodigo+'&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>');
  } else {
    return false;
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