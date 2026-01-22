<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t"     tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"    uri="/html-lib" %>
<%@ taglib prefix="fl"    uri="/function-lib" %>
<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String filtro        = (String) request.getAttribute("filtro");
String csaCodigo     = (String) request.getAttribute("csaCodigo");
int filtro_tipo      = (int) request.getAttribute("filtro_tipo");
List correspondentes = (List) request.getAttribute("correspondentes");
%>
<c:set var="title">
  <hl:message key="rotulo.processar.lote.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
      <div class="row">
<!-- Filtro -->
        <div class="col-sm-5 col-md-4">
          <div class="card">
            <div class="card-header hasIcon pl-3">
              <h2 class="card-header-title"><hl:message key="rotulo.acao.pesquisar"/></h2>
            </div>
            <div class="card-body">
              <FORM NAME="form1" METHOD="post" ACTION="../v3/processarLote?acao=listarCorrespondente&<%=SynchronizerToken.generateToken4URL(request)%>">
                <div class="row">
                  <div class="form-group col-sm">
                    <label for="FILTRO"><hl:message key="rotulo.lote.filtrar.correspondente"/></label>
                    <input type="text" class="form-control" id="FILTRO" name="FILTRO" placeholder="<hl:message key='rotulo.acao.digite.filtro'/>" VALUE="<%=TextHelper.forHtmlAttribute(filtro)%>" onFocus="SetarEventoMascara(this,'#*200',true);"  onBlur="fout(this);ValidaMascara(this);">
                  </div>
                </div>
                <div class="row">
                  <div class="form-group col-sm">
                    <label for="FILTRO_TIPO"><hl:message key="rotulo.acao.filtrar.por"/></label>
                    <select class="form-control form-select select" id="FILTRO_TIPO" name="FILTRO_TIPO" onFocus="SetarEventoMascara(this,'#*200',true);"  onBlur="fout(this);ValidaMascara(this);" nf="Filtrar">
                      <optgroup label="<%=ApplicationResourcesHelper.getMessage("rotulo.filtro.plural", responsavel)%>:">
                        <OPTION VALUE=""   <%=(String)((filtro_tipo == -1) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.sem.filtro"/></OPTION>
                        <OPTION VALUE="02" <%=(String)((filtro_tipo ==  2) ? "SELECTED" : "")%>><hl:message key="rotulo.lote.codigo"/></OPTION>
                        <OPTION VALUE="03" <%=(String)((filtro_tipo ==  3) ? "SELECTED" : "")%>><hl:message key="rotulo.lote.nome"/></OPTION>
                      </optgroup>
                    </select><INPUT NAME="CSA_CODIGO" TYPE="HIDDEN" VALUE="<%=TextHelper.forHtmlAttribute(csaCodigo)%>" />
                  </div>
                </div>
                <div class="btn-action">
                  <button class="btn btn-primary">
                    <svg width="20">
                      <use xlink:href="#i-consultar"></use>
                    </svg>
                    <hl:message key="rotulo.acao.pesquisar"/>
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
<!-- lista de correspondentes -->
        <div class="col-sm-7 col-md-8">
          <div class="card">
            <div class="card-header hasIcon pl-3">
              <h2 class="card-header-title"><hl:message key="rotulo.correspondente.plural"/></h2>
            </div>
            <div class="card-body table-responsive p-0">
              <table class="table table-striped table-hover">
                <thead>
                  <tr>
                    <th scope="col"><hl:message key="rotulo.lote.codigo"/></th>
                    <th scope="col"><hl:message key="rotulo.lote.nome"/></th>
                    <th scope="col" width="20%"><hl:message key="rotulo.acoes"/></th>
                    <%=JspHelper.msgRstVazio(correspondentes.size()==0, "13", "lp")%>
                  </tr>
                </thead>
                <tbody>
                <%
                Iterator it = correspondentes.iterator();
                while (it.hasNext()) {
                  CustomTransferObject correspondente = (CustomTransferObject)it.next();
                  String csa_codigo = (String)correspondente.getAttribute(Columns.CSA_CODIGO);
                  String cor_codigo = (String)correspondente.getAttribute(Columns.COR_CODIGO);
                  String cor_nome   = (String)correspondente.getAttribute(Columns.COR_NOME);
                  String cor_identificador = (String)correspondente.getAttribute(Columns.COR_IDENTIFICADOR);
                %>
                  <tr>
                    <td><%=TextHelper.forHtmlContent(cor_identificador)%></td>
                    <td><%=TextHelper.forHtmlContent(cor_nome.toUpperCase())%></td>
                    <td>
                    <div class="actions">
                        <a class="ico-action" href="#" onClick="postData('../v3/processarLote?acao=listarXml&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&COR_CODIGO=<%=TextHelper.forJavaScript(cor_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" role="button">
                          <div class="form-inline">
                            <span class="mr-1" title="" aria-label="<hl:message key='rotulo.acoes.selecionar'/>">
                              <svg><use xlink:href="#i-confirmar"></use></svg>
                            </span> 
                            <hl:message key="rotulo.acoes.selecionar"/>
                          </div>
                        </a>
                      </div>
                    </td>
                  </tr>
                <%
                }
                %>
                </tbody>
                <tfoot>
                  <tr>
                    <td colspan="4">
                      <hl:message key="rotulo.lote.listagem.correspondente"/>
                      <span class="font-italic"> - 
                        <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/>
                      </span>
                    </td>
                  </tr>
                </tfoot>
              </table>
              <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
            </div>
          </div>
        </div>
      </div>
      <div class="btn-action">
        <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
      </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>