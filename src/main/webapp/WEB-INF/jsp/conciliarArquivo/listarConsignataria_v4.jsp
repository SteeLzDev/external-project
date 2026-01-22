<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String filtro = (String) request.getAttribute("filtro");
int filtro_tipo = (int) request.getAttribute("filtro_tipo");
List<?> consignatarias = (List<?>) request.getAttribute("consignatarias");
%>
<c:set var="title">
   <hl:message key="rotulo.processar.arquivo.conciliacao.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>  
</c:set>
<c:set var="bodyContent">
  <div class="row firefox-print-fix">
    <div class="col-sm-5 col-md-4 d-print-none">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.acao.pesquisar"/></h2>
        </div>
        <div class="card-body">
          <form NAME="form1" METHOD="post" ACTION="../v3/conciliarArquivo?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>"> 
            <div class="row">
              <div class="form-group col-sm">
                <label for="FILTRO"><hl:message key="rotulo.conciliacao.filtro"/></label>
                <input type="text" class="form-control" id="FILTRO" name="FILTRO" placeholder="<hl:message key='rotulo.acao.digite.filtro'/>" VALUE="<%=TextHelper.forHtmlAttribute(filtro)%>" onFocus="SetarEventoMascara(this,'#*200',true);"  onBlur="fout(this);ValidaMascara(this);">  
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <label for="FILTRO_TIPO"><hl:message key="rotulo.acao.filtrar.por"/></label>
                <select class="form-control form-select select" id="FILTRO_TIPO" name="FILTRO_TIPO" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);">
                  <optgroup label="<%=ApplicationResourcesHelper.getMessage("rotulo.filtro.plural", responsavel)%>:">
                    <OPTION VALUE=""   <%=(String)((filtro_tipo == -1) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.sem.filtro"/></OPTION>
                    <OPTION VALUE="02" <%=(String)((filtro_tipo ==  2) ? "SELECTED" : "")%>><hl:message key="rotulo.conciliacao.codigo"/></OPTION>
                    <OPTION VALUE="03" <%=(String)((filtro_tipo ==  3) ? "SELECTED" : "")%>><hl:message key="rotulo.conciliacao.nome"/></OPTION>
                  </optgroup>
                </select><INPUT NAME="Filtrar" ID="Filtrar" TYPE="HIDDEN"  VALUE="<%=ApplicationResourcesHelper.getMessage("rotulo.conciliacao.filtrar", responsavel)%>" title="<%=ApplicationResourcesHelper.getMessage("rotulo.conciliacao.filtrar", responsavel)%>" WIDTH="49" HEIGHT="25">
              </div>
            </div>
          </form>        
        </div>
      </div>
      <div class="btn-action">
        <button class="btn btn-primary" name="Filtrar" id="Filtrar"  onClick="filtrar();">
          <svg width="20">
            <use xlink:href="../img/sprite.svg#i-consultar"></use>
          </svg> <hl:message key="rotulo.acao.pesquisar"/>
        </button>
      </div>
    </div>
    <div class="col-sm-7 col-md-8"> 
      <div class="card"> 
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="rotulo.processar.conciliacao.titulo"/></h2>
        </div>
        <div class="card-body table-responsive ">
            <table class="table table-striped table-hover">
              <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.conciliacao.codigo"/></th>
                <th scope="col"><hl:message key="rotulo.conciliacao.nome"/></th>
                <th scope="col"><hl:message key="rotulo.conciliacao.nome.abreviado"/></th>
                <th scope="col" width="15%"><hl:message key="rotulo.acoes"/></th>
              </tr>
             </thead>
            <%=JspHelper.msgRstVazio(consignatarias.size()==0, "13", "lp")%>
            <%
            Iterator<?> it = consignatarias.iterator();
            while (it.hasNext()) {
              CustomTransferObject consignataria = (CustomTransferObject)it.next();
              String csa_codigo = (String)consignataria.getAttribute(Columns.CSA_CODIGO);
              String csa_nome = (String)consignataria.getAttribute(Columns.CSA_NOME);
              String csa_identificador = (String)consignataria.getAttribute(Columns.CSA_IDENTIFICADOR);
              String csa_ativo = consignataria.getAttribute(Columns.CSA_ATIVO) != null ? consignataria.getAttribute(Columns.CSA_ATIVO).toString() : "1";
            
              String csa_nome_abrev = (String)consignataria.getAttribute(Columns.CSA_NOME_ABREV);
              if (csa_nome_abrev == null || csa_nome_abrev.trim().length() == 0)
                csa_nome_abrev = csa_nome;
            %>
              <tr>
                <td><%=TextHelper.forHtmlContent(csa_identificador)%></td>
                <td><%=TextHelper.forHtmlContent(csa_nome.toUpperCase())%></td>
                <td><%=TextHelper.forHtmlContent(consignataria.getAttribute(Columns.CSA_NOME_ABREV) != null ? consignataria.getAttribute(Columns.CSA_NOME_ABREV) : "")%></td>
                <td align="center" width="5%"><a href="#no-back" onClick="postData('../v3/conciliarArquivo?acao=listarXml&tipo=listar&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.selecionar"/></a></td>
              </tr>
             <%}%>
            <tfoot>
              <tr>
                <td>
                  <hl:message key="rotulo.arquivos.conciliacao.listagem"/>
                  <span class="font-italic"> - 
                    <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/>
                  </span>
                </td>
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
    <a class="btn btn-outline-danger" id="btnVoltar" href="#" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<c:set var="javascript">
  <script src="../js/usuario.js?<hl:message key="release.tag"/>"></script>
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