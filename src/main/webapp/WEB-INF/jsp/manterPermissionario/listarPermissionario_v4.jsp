<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
boolean podeExcluir = responsavel.temPermissao(CodedValues.FUN_EXC_PERMISSIONARIO);
boolean podeEditar = responsavel.temPermissao(CodedValues.FUN_EDT_PERMISSIONARIO) ;
boolean podeConsultar = responsavel.temPermissao(CodedValues.FUN_CONS_PERMISSIONARIO) ;
List<TransferObject> lstPermissionarios = (List<TransferObject>) request.getAttribute("lstPermissionarios");
String filtro = (String) request.getAttribute("filtro");
int filtro_tipo = (Integer) request.getAttribute("filtro_tipo");
%>
<c:set var="title">
  <hl:message key="<%=TextHelper.forHtml("rotulo.listar.permissionario.titulo")%>"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <%if (podeEditar) {%>
  <div class="row">
    <div class="col-sm-12 col-md-12 mb-2">
      <div class="float-end">
        <div class="btn-action">
          <a class="btn btn-primary" href="#no-back" onClick="postData('<%=TextHelper.forJavaScript(SynchronizerToken.updateTokenInURL("../v3/manterPermissionario?acao=iniciar", request))%>')"><hl:message key="rotulo.permissionario.novo"/></a>
        </div>
      </div>
    </div>
  </div>
  <%}%>
  
  <div class="row">
    <div class="col-sm-5 col-md-4">
        <div class="card">
          <div class="card-header hasIcon pl-3">
            <h2 class="card-header-title"><hl:message key="rotulo.botao.pesquisar"/></h2>
          </div>
          <div class="card-body">
            <form name="form1" method="post" action="../v3/manterPermissionario?acao=listar&<%=SynchronizerToken.generateToken4URL(request)%>">
              <input type="hidden" name="tipo" value="consultar">
              <div class="row">
                <div class="form-group col-sm">
                  <label for="iFiltro"><hl:message key="rotulo.plano.filtro"/></label>
                  <input type="text" class="form-control" id="iFiltro" name="FILTRO" value="<%=TextHelper.forHtmlAttribute(filtro)%>" onFocus="SetarEventoMascara(this,'#*200',true);"  onBlur="fout(this);ValidaMascara(this);" placeholder="<hl:message key="rotulo.acao.digite.filtro"/>">
                </div>
              </div>
                <div class="row">
                  <div class="form-group col-sm">
                    <label for="FILTRO_TIPO"><hl:message key="rotulo.acao.filtrar.por"/></label>
                      <select name="FILTRO_TIPO" class="form-control form-select select" onFocus="SetarEventoMascara(this,'#*200',true);"  onBlur="fout(this);ValidaMascara(this);">
                        <optgroup>
                          <option VALUE=""   <%=(String)((filtro_tipo == -1) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.sem.filtro"/></option>
                          <option VALUE="02" <%=(String)((filtro_tipo ==  2) ? "SELECTED" : "")%>><hl:message key="rotulo.servidor.matricula"/></option>
                          <option VALUE="03" <%=(String)((filtro_tipo ==  3) ? "SELECTED" : "")%>><hl:message key="rotulo.permissionario.cpf"/></option>
                          <option VALUE="04" <%=(String)((filtro_tipo ==  4) ? "SELECTED" : "")%>><hl:message key="rotulo.permissionario.singular"/></option>
                          <option VALUE="05" <%=(String)((filtro_tipo ==  5) ? "SELECTED" : "")%>><hl:message key="rotulo.endereco.singular"/></option>
                        </optgroup>
                      </select>
                  </div>
                </div>
              <div class="btn-action d-print-none">
                <a class="btn btn-primary" href="#no-back" onClick="filtrar();">
                  <svg width="20">
                    <use xlink:href="#i-consultar"></use></svg>
                  <hl:message key="rotulo.botao.pesquisar"/>
                </a>
              </div>
            </form>
          </div>
        </div>
    </div>
    <div class="col-sm-7 col-md-8">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.listar.permissionario.titulo"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
        <table class="table table-striped table-hover">
           <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.servidor.matricula"/></th>
                <th scope="col"><hl:message key="rotulo.permissionario.singular"/></th>
                <th scope="col"><hl:message key="rotulo.posto.singular"/></th>
                <th scope="col"><hl:message key="rotulo.permissionario.tipo"/></th>
                <th scope="col"><hl:message key="rotulo.permissionario.status"/></th>
                <th scope="col"><hl:message key="rotulo.endereco.singular"/></th>
                <th scope="col"><hl:message key="rotulo.permissionario.complemento"/></th>
                <th scope="col"><hl:message key="rotulo.acoes"/></th>
              </tr>
           </thead>
            <tbody>
              <%=JspHelper.msgRstVazio(lstPermissionarios.size()==0, 13, responsavel)%>
              <%
              Iterator<TransferObject> it = lstPermissionarios.iterator();
              String prm_codigo, rse_codigo, rse_matricula, ser_nome, posDescricao, trsDescricao, srsDescricao, endereco, prmComplemento, cpf, prm_DataDesocupacaoStr;
              Date prm_dataDesocupacao;
              while (it.hasNext()) {
                TransferObject permissionario = it.next();
                prm_codigo = (String)permissionario.getAttribute(Columns.PRM_CODIGO);
                rse_codigo = (String)permissionario.getAttribute(Columns.RSE_CODIGO);
                rse_matricula = (String)permissionario.getAttribute(Columns.RSE_MATRICULA);
                ser_nome = (String)permissionario.getAttribute(Columns.SER_NOME);
                posDescricao = !TextHelper.isNull(permissionario.getAttribute(Columns.POS_DESCRICAO)) ? permissionario.getAttribute(Columns.POS_DESCRICAO).toString() : "";
                trsDescricao = !TextHelper.isNull(permissionario.getAttribute(Columns.TRS_DESCRICAO)) ? permissionario.getAttribute(Columns.TRS_DESCRICAO).toString() : "";
                srsDescricao = !TextHelper.isNull(permissionario.getAttribute(Columns.SRS_DESCRICAO)) ? permissionario.getAttribute(Columns.SRS_DESCRICAO).toString() : "";
                endereco = (String)permissionario.getAttribute(Columns.ECH_DESCRICAO);
                prmComplemento = (String)permissionario.getAttribute(Columns.PRM_COMPL_ENDERECO);
                cpf = (String)permissionario.getAttribute(Columns.SER_CPF);
                prm_dataDesocupacao = (Date)permissionario.getAttribute(Columns.PRM_DATA_DESOCUPACAO);
                prm_DataDesocupacaoStr = prm_dataDesocupacao != null ? new SimpleDateFormat(LocaleHelper.getDatePattern()).format(prm_dataDesocupacao) : null;
                
              %>
              <tr>
                <td><%=TextHelper.forHtmlContent(rse_matricula)%></td>
                <td><%=TextHelper.forHtmlContent(cpf)%>&nbsp;-&nbsp;<%=TextHelper.forHtmlContent(ser_nome)%></td>
                <td><%=TextHelper.forHtmlContent(posDescricao)%></td>
                <td><%=TextHelper.forHtmlContent(trsDescricao)%></td>
                <td><%=TextHelper.forHtmlContent(srsDescricao)%></td>
                <td><%=TextHelper.forHtmlContent(endereco)%></td>
                <td><%=TextHelper.forHtmlContent(prmComplemento)%></td>                
                <td>
                  <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="Mais ações" aria-label="Mais ações"> <svg>
                              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                          </span> <hl:message key="rotulo.botao.opcoes"/>
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                        <% if (podeEditar) { %>                  
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterPermissionario?acao=editar&PRM_CODIGO=<%=TextHelper.forJavaScriptAttribute(prm_codigo)%>&RSE_CODIGO=<%=TextHelper.forJavaScript(rse_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a>
                        <% if (podeExcluir) { %>
                          <a class="dropdown-item" href="#no-back" onClick="excluirPermissionario('<%=TextHelper.forJavaScript(prm_codigo)%>', '../v3/manterPermissionario?acao=excluir&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(ser_nome)%>', '<%=TextHelper.forJavaScript(prm_DataDesocupacaoStr)%>')"><hl:message key="rotulo.acoes.excluir"/></a>
                        <% } %>
                        <% } else if (podeConsultar) { %>
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterPermissionario?acao=consultar&PRM_CODIGO=<%=TextHelper.forJavaScriptAttribute(prm_codigo)%>&RSE_CODIGO=<%=TextHelper.forJavaScript(rse_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.consultar"/></a>
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
              <tr>
                <td colspan="12">
                  <%=ApplicationResourcesHelper.getMessage("mensagem.listagem.permissionarios", responsavel) + " - "%>
                  <span class="font-italic"> <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/></span>
                </td>                
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
      <div class="card-footer">
        <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
      </div>
    </div>   
  </div>
  <div class="btn-action mt-3">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;" ><hl:message key="rotulo.botao.voltar"/></a>
  </div>
  
  <div class="modal fade" id="modalExcluir" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header pb-0">
          <h5 class="modal-title about-title mb-0" id="modalTitulo">
            <hl:message key='rotulo.permissionario.excluir' />
          </h5>
          <button type="button" class="logout mr-2" data-bs-dismiss="modal" aria-label="<hl:message key='rotulo.botao.fechar'/>">
            <span aria-hidden="true">×</span>
          </button>
        </div>
        <div class="modal-body pb-1 pt-1">
          <p id="textoExclusao">
          </p>
          <form>
            <div class="form-group mb-0">
              <div class="form-check">
                <label for="dtDesocupacao">
                  <hl:message key="rotulo.permissionario.data.desocupacao.formato" />
                </label>
                </br>
                <textarea name="dtDesocupacao" id="dtDesocupacao" class="form-control" rows="1" cols="25" placeHolder="<hl:message key='mensagem.placeholder.digite.data'/>" value=""></textarea>
              </div>
                <input type="hidden" value="" id="PRM_CODIGO_EXCLUIR">
                <input type="hidden" value="" id="prm_data_desocupacao">
            </div>
          </form>
        </div>
        <div class="modal-footer pt-0">
          <div class="btn-action mt-2 mb-0">
            <a class="btn btn-outline-danger" data-bs-dismiss="modal" href="#">
              <hl:message key="rotulo.botao.cancelar" />
            </a>
            <a class="btn btn-primary" data-bs-dismiss="modal" href="#" onclick="efetivaExclusão();" >
              <hl:message key="rotulo.botao.confirmar" />
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
  
</c:set>

<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
  <script type="text/JavaScript">
f0 = document.forms[0];

function excluirPermissionario(codigo, alink, desc, valorDefault) {
	$('#modalExcluir').modal('show');
	f1 = document.forms[1];
	f1.PRM_CODIGO_EXCLUIR.value = codigo;
	
	var textoExclusao = document.getElementById('textoExclusao');
	textoExclusao.textContent = '<hl:message key="mensagem.confirmacao.exclusao.permissionario"/>'.replace("{0}", desc);
	
	var dtDesocupacao = document.getElementById('dtDesocupacao');
	var vlrDataDesocupacao = (valorDefault == '' || valorDefault == null || valorDefault == 'null') ? '<%=(String)(new SimpleDateFormat(LocaleHelper.getDatePattern()).format(new Date()))%>' : valorDefault;
	dtDesocupacao.textContent = vlrDataDesocupacao;
}

function efetivaExclusão() {
	var codigo = document.getElementById('PRM_CODIGO_EXCLUIR').value;
	var dtaDesocupacao = document.getElementById('dtDesocupacao').textContent;
  	postData('../v3/manterPermissionario?acao=excluir&&PRM_CODIGO=' + codigo + '&prm_data_desocupacao=' + dtaDesocupacao + '&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>');
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