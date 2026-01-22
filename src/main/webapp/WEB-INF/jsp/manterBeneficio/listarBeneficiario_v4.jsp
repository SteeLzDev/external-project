<%--
* <p>Title: listarBeneficiario_v4</p>
* <p>Description: Listar beneficiários v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: larissa.silva $
* $Revision: 24740 $
* $Date: 2018-07-18 14:14:41 -0300 (Qua, 18 jul 2018) $
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean podeEditar = (boolean) request.getAttribute("podeEditar");
boolean editarAnexo = responsavel.temPermissao(CodedValues.FUN_CONSULTAR_ANEXO_BENEFICIARIOS);

//Pega dados vindo do webController
String codigo = responsavel.getCodigoEntidade();
String filtro = (String) request.getAttribute("filtro");
int filtro_tipo = (int) request.getAttribute("filtro_tipo");
List beneficiarios = (List) request.getAttribute("beneficiarios");
String ser_codigo = (String) request.getAttribute(Columns.SER_CODIGO);
String rse_codigo = (String) request.getAttribute(Columns.RSE_CODIGO);
boolean titularExiste = (boolean) request.getAttribute("titularExiste");
%>
<c:set var="title">
  <hl:message key="rotulo.lista.beneficiarios.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent">
  <% if (podeEditar && responsavel.temPermissao(CodedValues.FUN_CONSULTAR_ENDERECO_SERVIDOR) && responsavel.temPermissao(CodedValues.FUN_EDT_ENDERECO_SERVIDOR)) {%>  

  <div class="btn-action col-sm">    
    <button type="button" class="btn btn-primary btn-mais-opcoes" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
      <hl:message key="rotulo.mais.acoes"/>
    </button>
    <ul class="dropdown-menu dropdown-menu-right">
      <li>
        <a href="#no-back" class="dropdown-item" onClick="verificaTitular('../v3/alterarBeneficiarios?acao=novo&reserva=N&_skip_history_=true&<%=Columns.getColumnName(Columns.SER_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>');"><hl:message key="rotulo.botao.novo.beneficiario" /></a>
      </li>           
      <li>
        <a href="#no-back" class="dropdown-item" onClick="postData('../v3/editarServidor?acao=listarEndereco&SER_CODIGO=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.acao.editar.endereco.servidor" /></a>
      </li>            
    </ul>
  </div>

  <% } else if(responsavel.temPermissao(CodedValues.FUN_CONSULTAR_ENDERECO) && responsavel.temPermissao(CodedValues.FUN_EDT_ENDERECO)){%>
  <div class="btn-action col-sm">
    <a href="#no-back" class="btn btn-primary" onClick="postData('../v3/editarServidor?acao=listarEndereco&SER_CODIGO=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.acao.editar.endereco.servidor" /></a>
  </div>
  <%} else if(podeEditar){ %>
  <div class="btn-action col-sm">
    <a href="#no-back" class="btn btn-primary" onClick="postData('../v3/alterarBeneficiarios?acao=novo&reserva=N&_skip_history_=true&<%=Columns.getColumnName(Columns.SER_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.botao.novo.beneficiario" /></a>
  </div>
  <%} %>

  <div class="col-sm">
    <div class="card">
      <div class="card-header">
        <hl:message key="rotulo.lista.beneficiarios.titulo"/>
      </div>
      <div class="card-body table-responsive">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th><hl:message key="rotulo.beneficiario.tipo.beneficiario"/></th>
              <%if(responsavel.isSup()) {%>
              <th><hl:message key="rotulo.beneficiario.ordem.dependencia"/></th>
              <%} %>
              <th><hl:message key="rotulo.beneficiario.nome"/></th>
              <th><hl:message key="rotulo.beneficiario.cpf"/></th>
              <th><hl:message key="rotulo.beneficiario.data.nascimento"/></th>
              <th><hl:message key="rotulo.beneficiario.telefone"/></th>
              <th><hl:message key="rotulo.beneficiario.celular"/></th>
              <th><hl:message key="rotulo.beneficiario.situacao"/></th>
              <th><hl:message key="rotulo.acoes"/></th>
            </tr>
          </thead>
          <tbody>
            <%=JspHelper.msgRstVazio(beneficiarios.size()==0, "13", "lp")%>
            <% Iterator it = beneficiarios.iterator();
              while (it.hasNext()) {
                CustomTransferObject beneficiario = (CustomTransferObject)it.next();
                String bfc_codigo = (String)beneficiario.getAttribute(Columns.BFC_CODIGO);
                String tib_descricao = (String)beneficiario.getAttribute(Columns.TIB_DESCRICAO);
                String bfc_ordem_dependencia = beneficiario.getAttribute(Columns.BFC_ORDEM_DEPENDENCIA) != null ? beneficiario.getAttribute(Columns.BFC_ORDEM_DEPENDENCIA).toString() : "";
                String bfc_nome = (String)beneficiario.getAttribute(Columns.BFC_NOME);
                String bfc_cpf = (String)beneficiario.getAttribute(Columns.BFC_CPF);
                String bfc_data_nascimento = DateHelper.format((Date)beneficiario.getAttribute(Columns.BFC_DATA_NASCIMENTO), "dd/MM/yyyy");
                String bfc_telefone = (String)beneficiario.getAttribute(Columns.BFC_TELEFONE);
                String bfc_celular = (String)beneficiario.getAttribute(Columns.BFC_CELULAR);
                String bfc_sbe_descricao = (String)beneficiario.getAttribute(Columns.SBE_DESCRICAO);%>
            <tr>
              <td><%=tib_descricao != null ? TextHelper.forHtmlContent(tib_descricao) : ""%></td>
              <%if(responsavel.isSup()) {%>
              	<td><%=bfc_ordem_dependencia != null ? TextHelper.forHtmlContent(bfc_ordem_dependencia) : ""%></td>
              <%} %>
              <td><%=bfc_nome != null ? TextHelper.forHtmlContent(bfc_nome) : ""%></td>
              <td><%=bfc_cpf != null ? TextHelper.forHtmlContent(bfc_cpf) : ""%></td>
              <td><%=bfc_data_nascimento != null ? TextHelper.forHtmlContent(bfc_data_nascimento) : ""%></td>
              <td><%=bfc_telefone != null ? TextHelper.forHtmlContent(bfc_telefone) : ""%></td>
              <td><%=bfc_celular != null ? TextHelper.forHtmlContent(bfc_celular) : ""%></td>
              <td><%=bfc_sbe_descricao != null ? TextHelper.forHtmlContent(bfc_sbe_descricao) : ""%></td>
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
                      <a class="dropdown-item" href="#no-back" onClick="postData('../v3/alterarBeneficiarios?acao=editar&<%=Columns.getColumnName(Columns.BFC_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=Columns.getColumnName(Columns.SER_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                        <hl:message key="rotulo.acoes.editar" />
                      </a> 
                      <a class="dropdown-item" href="#no-back" onClick="confirmarExclusao('../v3/alterarBeneficiarios?acao=excluir&_skip_history_=true&<%=Columns.getColumnName(Columns.BFC_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=Columns.getColumnName(Columns.SER_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');">
                        <hl:message key="rotulo.acoes.excluir" />
                      </a>
                      <% } else { %>
                      <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarBeneficiarios?acao=visualizar&_skip_history_=true&<%=Columns.getColumnName(Columns.BFC_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=Columns.getColumnName(Columns.SER_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                        <hl:message key="rotulo.acoes.detalhar" />
                      </a> 
                      <% } if(editarAnexo) { %>
                      <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarAnexoBeneficiario?acao=listar&_skip_history_=true&<%=Columns.getColumnName(Columns.BFC_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(bfc_codigo)%>&<%=Columns.getColumnName(Columns.SER_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO) %>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                        <hl:message key="rotulo.acoes.anexar" />
                      </a> 
                      <% } %>
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
          </tfoot>
        </table>
      </div>
      <div class="card-footer">
      	<%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
      </div>
    </div>  
  </div>
  <div class="btn-action col-sm">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"  value="Cancelar"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<c:set var="javascript">
  <script>
    function confirmarExclusao(link){
      if (confirm('<hl:message key="mensagem.confirmacao.exclusao.beneficiario"/>')){
            postData(link);
      }
      return false;
    }
    function verificaTitular(link){
        if (<%=titularExiste%>){
              postData(link);
        } else {
        	alert('<hl:message key="mensagem.erro.beneficiario.titular.nao.existe"/>');            
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