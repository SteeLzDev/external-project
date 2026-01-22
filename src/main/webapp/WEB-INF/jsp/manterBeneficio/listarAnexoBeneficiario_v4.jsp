<%--
* <p>Title: listarAnexoBeneficiario_v4</p>
* <p>Description: Listar anexo de beneficiário v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: larissa.silva $
* $Revision: 25018 $
* $Date: 2018-07-18 14:43:42 -0300 (Ter, 18 jul 2018) $
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean podeEditar = (boolean) request.getAttribute("podeEditar");

//Pega dados vindo do webController
String codigo = responsavel.getCodigoEntidade();
List<TransferObject> anexos = (List<TransferObject>) request.getAttribute("anexos");
String bfc_codigo = (String) request.getAttribute(Columns.BFC_CODIGO);
String rse_codigo = (String) request.getAttribute(Columns.RSE_CODIGO);
%>
<c:set var="javascript">
  <script>
    function confirmarExclusao(link){
  		if (confirm('<hl:message key="mensagem.confirmacao.exclusao.anexo.beneficio"/>')){
  	        postData(link);
  		}
  		return false;
  	}
  </script>
</c:set>
<c:set var="title">
  ${tituloPagina}
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent">
  <% if (podeEditar) {%>  
  <div class="btn-action col-sm">
      <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/alterarAnexoBeneficiario?acao=novo&_skip_history_=true&<%=Columns.getColumnName(Columns.BFC_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>')">
      <hl:message key="rotulo.anexo.beneficiario.novo" /></a>
  </div>
  <% } %>
  <div class="col-sm">
    <div class="card">
      <div class="card-header">
        <hl:message key="rotulo.lista.anexo.beneficiario.titulo" />
      </div>
      <div class="card-body table-responsive">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
            	<th ROWSPAN="2" class="tabelatopo"><hl:message key="rotulo.anexo.beneficiario.tipo.documento"/></th>
              <th ROWSPAN="2" class="tabelatopo"><hl:message key="rotulo.anexo.beneficiario.descricao"/></th>
              <th ROWSPAN="2" class="tabelatopo"><hl:message key="rotulo.anexo.beneficiario.data.criacao"/></th>
              <th ROWSPAN="2" class="tabelatopo"><hl:message key="rotulo.anexo.beneficiario.data.validade"/></th>
              <th width="10%" align="center" class="tabelatopoAcoes" colspan="5"><hl:message key="rotulo.acoes"/></th>
            </tr>
          </thead>
          <tbody>
          <%=JspHelper.msgRstVazio(anexos.size()==0, "13", "lp")%>
          <%
          Iterator it = anexos.iterator();
          while (it.hasNext()) {
            CustomTransferObject anexo = (CustomTransferObject)it.next();
            String abf_nome = (String) anexo.getAttribute(Columns.ABF_NOME);
            String abf_descricao = (String) anexo.getAttribute(Columns.ABF_DESCRICAO);
            String tar_descricao = (String) anexo.getAttribute(Columns.TAR_DESCRICAO);
            String abf_data = DateHelper.format((Date)anexo.getAttribute(Columns.ABF_DATA), "dd/MM/yyyy");
            String data = DateHelper.format((Date)anexo.getAttribute(Columns.ABF_DATA), "yyyyMMdd");
            String abf_data_validade = DateHelper.format((Date)anexo.getAttribute(Columns.ABF_DATA_VALIDADE), "dd/MM/yyyy");
          %>
            <tr>
              <td><%=TextHelper.forHtmlContent(tar_descricao)%></td>
              <td><%=TextHelper.forHtmlContent(abf_descricao)%></td>
              <td><%=TextHelper.forHtmlContent(abf_data)%></td>
              <td><%=TextHelper.forHtmlContent(abf_data_validade)%></td>
              <td>
                <div class="actions">
                  <div class="dropdown">
                    <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <div class="form-inline">
                      <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"> <svg>
                          <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                      </span>
                      <hl:message key="rotulo.acoes.lst.arq.generico.opcoes" />
                    </div>
                    </a>
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                      <% if(podeEditar) {%>
                      <a href="#no-back" class="dropdown-item" onClick="postData('../v3/alterarAnexoBeneficiario?acao=editar&_skip_history_=true&<%=Columns.getColumnName(Columns.BFC_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=Columns.getColumnName(Columns.ABF_NOME) %>=<%=TextHelper.forJavaScriptAttribute(abf_nome)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                        <hl:message key="rotulo.acoes.editar" />
                      </a>
                      <a href="#no-back" class="dropdown-item" onClick="confirmarExclusao('../v3/alterarAnexoBeneficiario?acao=excluir&_skip_history_=true&<%=Columns.getColumnName(Columns.BFC_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=Columns.getColumnName(Columns.ABF_NOME) %>=<%=TextHelper.forJavaScriptAttribute(abf_nome)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');">
                        <hl:message key="rotulo.acoes.excluir" />
                      </a>
                      <% } %>
                      <a href="#no-back" class="dropdown-item" onClick="postData('../v3/downloadArquivo?tipo=beneficiario&entidade=&_skip_history_=true&data=<%=TextHelper.forJavaScriptAttribute(data)%>&arquivo_nome=<%=TextHelper.forJavaScriptAttribute(abf_nome)%>&<%=Columns.getColumnName(Columns.BFC_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                        <hl:message key="rotulo.acoes.lst.arq.generico.download" />
                      </a> 
                    </div>
                  </div>
                </div>
              </td>
            </tr>
            <%
              }
              %>  
          </tbody>
        </table>
      </div>
    </div>
  </div>
  <div class="btn-action col-sm">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/listarBeneficiarios?acao=listar&_skip_history_=true&<%=Columns.getColumnName(Columns.RSE_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;"  value="Cancelar"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>