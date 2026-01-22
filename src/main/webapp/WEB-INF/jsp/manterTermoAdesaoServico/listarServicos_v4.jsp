<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

  List<TransferObject> servicos = (List <TransferObject>) request.getAttribute("servicos");

  String csaCodigo = (String) request.getAttribute("csaCodigo");
  String csaNome = (String) request.getAttribute("csaNome");
  String linkRet = (String) request.getAttribute("linkRet");
  String linkEdicao = (String) request.getAttribute("linkEdicao");
  String linkVisualizar = (String) request.getAttribute("linkVisualizar");
  String voltar = (String) request.getAttribute("voltar");
%>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript">
    function doIt(opt, svcCodigo, svcDescricao) {
      qs = '&CSA_CODIGO=<%=TextHelper.forJavaScriptBlock(csaCodigo)%>;;<%=TextHelper.forJavaScriptBlock(csaNome)%>&SVC_CODIGO=' + svcCodigo + '&<%=SynchronizerToken.generateToken4URL(request)%>';
      var msg = '', j;
      if (opt == 'r') {
        msg = '<hl:message key="mensagem.confirmacao.exclusao.termo.adesao"/>'.replace("{0}",svcDescricao);
        j = '../v3/manterTermoAdesaoServico?acao=excluirTermoAdesao&';
      } else {
        return;
      }
      if (msg != '') {
        ConfirmaUrl(msg, j + qs);
      } else {
        postData(j + qs + mmu);
      }
    }
  </script>
</c:set>  
<c:set var="title">
<hl:message key="rotulo.listar.servicos.titulo"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header hasIcon pl-3">
      <h2 class="card-header-title"><hl:message key="rotulo.termo.adesao.singular"/> <hl:message key="rotulo.listar.servicos.titulo"/><%=TextHelper.forHtmlContent(!csaNome.equals("") ? " - " + csaNome.toUpperCase() : "")%></h2>
    </div>
     <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.servico.identificador"/></th>
            <th scope="col"><hl:message key="rotulo.servico.descricao"/></th>
            <th scope="col"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody>
          <%=JspHelper.msgRstVazio(servicos.size()==0, "13", "lp")%>
          <%
            Iterator<TransferObject> it = servicos.iterator();
            String svc_codigo, svc_descricao, svc_identificador, cod_verba;
            TransferObject servico = null;
            while (it.hasNext()) {
              servico = it.next();
              svc_codigo = (String)servico.getAttribute(Columns.SVC_CODIGO);
              svc_descricao = (String)servico.getAttribute(Columns.SVC_DESCRICAO);
              svc_identificador = (String)servico.getAttribute(Columns.SVC_IDENTIFICADOR);
              cod_verba = (String)servico.getAttribute(Columns.CNV_COD_VERBA);
          %>
            <tr>
              <td><%=TextHelper.forHtmlContent(cod_verba)%></td>
              <td><%=TextHelper.forHtmlContent(svc_descricao.toUpperCase())%></td>
              <td>
                <div class="actions">
                  <div class="dropdown">
                    <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                      <div class="form-inline">
                        <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"> <svg>
                            <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                        </span> <hl:message key="rotulo.botao.opcoes"/>
                      </div>
                    </a>
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                      <a class="dropdown-item" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(linkEdicao)%>&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>;;<%=TextHelper.forJavaScriptAttribute(csaNome)%>&SVC_CODIGO=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&SVC_IDENTIFICADOR=<%=TextHelper.forJavaScriptAttribute(svc_identificador)%>&SVC_DESCRICAO=<%=TextHelper.forJavaScriptAttribute(svc_descricao)%>&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a>
                      <a class="dropdown-item" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(linkVisualizar)%>&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>;;<%=TextHelper.forJavaScriptAttribute(csaNome)%>&SVC_CODIGO=<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>&SVC_IDENTIFICADOR=<%=TextHelper.forJavaScriptAttribute(svc_identificador)%>&SVC_DESCRICAO=<%=TextHelper.forJavaScriptAttribute(svc_descricao)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.visualizar"/></a>
                      <a class="dropdown-item" href="#no-back" onClick="doIt('r', '<%=TextHelper.forJavaScriptAttribute(svc_codigo)%>', '<%=TextHelper.forJavaScriptAttribute(svc_descricao)%>');"><hl:message key="rotulo.acoes.excluir"/></a>
                    </div>
                   </div>
                 </div>
              </td>                           
              </tr>
              <%
            }
            %>
          </tbody>
          <tfoot>
            <tr>
              <td colspan="5"><%=ApplicationResourcesHelper.getMessage("rotulo.listar.servicos.titulo", responsavel) + " - " %>
                <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span>
              </td>
            </tr>
          </tfoot>  
        </table>
               
        <div class="btn-action">
          <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=voltar%>'); return false;" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
        </div>     
    </div>
  </div>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>