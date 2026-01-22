<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String tipo = (String) request.getAttribute("tipo");
String codigo = (String) request.getAttribute("codigo");
String tituloPagina = (String) request.getAttribute("tituloPagina");
List<TransferObject> funcoesAuditaveis = (List<TransferObject>) request.getAttribute("funcoesAuditaveis");
List<UsuarioTransferObject> usuariosAuditores = (List<UsuarioTransferObject>) request.getAttribute("usuariosAuditores");
List<Integer> inicio_grupo = new ArrayList<>();
%>
<c:set var="title">
   <%=TextHelper.forHtmlContent(tituloPagina.toUpperCase())%>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
<form action="../v3/manterFuncoesAuditaveis?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>"  method="POST" name="form1">
  <input type="hidden" name="codigo" value="<%=TextHelper.forHtmlAttribute(codigo)%>">
  <INPUT TYPE="hidden" NAME="tipo" VALUE="<%=TextHelper.forHtmlAttribute(tipo)%>">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.auditoria.usuarios.auditores"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.auditoria.usuario"/></th>
            <th scope="col"><hl:message key="rotulo.auditoria.nome"/></th>
            <th scope="col"><hl:message key="rotulo.auditoria.email"/></th>
          </tr>
        </thead>
        <tbody>
        <%
        String usu_nome, usu_login, usu_email;
        
        Iterator<UsuarioTransferObject> it = usuariosAuditores.iterator();
        while (it.hasNext()) {
          UsuarioTransferObject next = it.next();
          usu_nome = next.getAttribute(Columns.USU_NOME).toString();
          usu_login = next.getAttribute(Columns.USU_LOGIN).toString();
          usu_email = next.getUsuEmail();
        %>
         <tr>
           <td><%=TextHelper.forHtmlContent(usu_login)%></td>
           <td><%=TextHelper.forHtmlContent(usu_nome.toUpperCase())%></td>
           <td><%=TextHelper.forHtmlContent(usu_email)%></td>
         </tr>
        <%
        }
        %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="3">
              <hl:message key="rotulo.listagem.edicao.auditoria"/> - 
              <span class="font-italic"> <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/></span>
            </td>
          </tr>
        </tfoot>
      </table>
    </div>
    <div class="card-footer">
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
    </div>
  </div>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.auditoria.funcoes.disponiveis"/></h2>
    </div>
    <div class="card-body">
    <%
    String funcao = "", fun_codigo = "";
    String grf_codigo = "";
    String grf_descricao = "";
    
    List<TransferObject> funcoes_grf = new ArrayList<>();
    List<String> fun_codigos = new ArrayList<>();
    
    
    int fim_grupo = -1;
    int num_grupo = -1;
    boolean selecionado = false;
    
    Iterator<TransferObject> it2 = funcoesAuditaveis.iterator();
    TransferObject customs;
    while (it2.hasNext()) {
      customs = it2.next();
      
      if (!customs.getAttribute(Columns.FUN_GRF_CODIGO).toString().equals(grf_codigo)) {
        if (!grf_codigo.equals("")) {
          %>
          <div class="row">
            <div class="col-sm-12 col-md-12">
              <h3 class="legend">
                <span id="0"><%=TextHelper.forHtmlContent(grf_descricao)%></span>
              </h3>
              <div class="form-check">
                <div class="row" role="group" aria-labelledby="GERAL">
                <%                 
                int meio = Math.round(funcoes_grf.size() / 2f); 
                for (int i=0; i<meio; i++) {
                 TransferObject custom = funcoes_grf.get(i);
                %>
                <%
                 funcao = custom.getAttribute(Columns.FUN_DESCRICAO).toString();
                 fun_codigo = custom.getAttribute(Columns.FUN_CODIGO).toString();
                 selecionado = custom.getAttribute("CHECKED").equals("1") ? true:false;
                %>
                  <div class="col-sm-12 col-md-6">
                    <input class="form-check-input ml-1" type="checkbox" name="funcao" id="funcao<%=TextHelper.forHtmlAttribute(fun_codigo)%>" value="<%=TextHelper.forHtmlAttribute(fun_codigo)%>" <%=(String)(selecionado ? "CHECKED" : "")%>>
                    <label class="form-check-label" for="funcao<%=TextHelper.forHtmlAttribute(fun_codigo)%>">
                      <span class="text-nowrap align-text-top"><%=TextHelper.forHtmlContent(funcao)%></span>
                    </label>
                  </div>
                  <%
                   if (i+meio < funcoes_grf.size()) {
                     custom = funcoes_grf.get(i+meio);
                     funcao = custom.getAttribute(Columns.FUN_DESCRICAO).toString();
                     fun_codigo = custom.getAttribute(Columns.FUN_CODIGO).toString();
                     selecionado = custom.getAttribute("CHECKED").equals("1") ? true:false;
                  %>
                  <div class="col-sm-12 col-md-6">
                    <input class="form-check-input ml-1" type="checkbox" name="funcao" id="funcao<%=TextHelper.forHtmlAttribute(fun_codigo)%>" value="<%=TextHelper.forHtmlAttribute(fun_codigo)%>" <%=(String)(selecionado ? "CHECKED" : "")%>>
                    <label class="form-check-label" for="funcao<%=TextHelper.forHtmlAttribute(fun_codigo)%>">
                      <span class="text-nowrap align-text-top"><%=TextHelper.forHtmlContent(funcao)%></span>
                    </label>
                  </div>
                  <%
                   }
                }
                %>
                  <div class="col-sm-12 col-md-6">
                    <input class="form-check-input ml-1" type="checkbox" name="checkGrupo" id="checkGrupo<%=TextHelper.forHtmlAttribute(fun_codigo)%>" onClick="check_uncheck_grupo(<%=TextHelper.forJavaScript((inicio_grupo.get(num_grupo)))%>,<%=(int)fim_grupo%>,<%=(int)num_grupo%>);">
                    <label class="form-check-label" for="checkGrupo<%=TextHelper.forHtmlAttribute(fun_codigo)%>">
                      <span class="text-nowrap align-text-top font-weight-bold"><hl:message key="rotulo.campo.todos.simples"/></span>
                    </label>
                  </div>
                </div>
              </div>
            </div>
          </div>
            <%
            }
        
            funcoes_grf.clear();  
        
            grf_codigo = (String) customs.getAttribute(Columns.FUN_GRF_CODIGO);
            grf_descricao = (String) customs.getAttribute(Columns.GRF_DESCRICAO);
        
            if (!grf_codigo.equals("")) {
              num_grupo++;
              inicio_grupo.add(new Integer(fim_grupo + 1));
            } else {
              break;
            }
          }
        
          fun_codigos.add(customs.getAttribute(Columns.FUN_CODIGO).toString());
          fim_grupo ++;  
          funcoes_grf.add(customs);
        }
        inicio_grupo.add(new Integer(fim_grupo + 1));
        %>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
    <a class="btn btn-primary" href="#"  onClick="envia(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
  </div>
</form>
</c:set>
<c:set var="javascript">  
<script type="text/JavaScript">

function formLoad() {
  inicializa_grupos();
}

function check_uncheck_grupo(inicio, fim, grupo) {
    if (f0.checkGrupo[grupo].checked == true){
    for (i=inicio; i <= fim; i++) {
      f0.funcao[i].checked = true;
    }
  } else {
    for (i=inicio; i <= fim; i++) {
      f0.funcao[i].checked = false;
    }
  }
}

function envia() {
  f0.submit();
}
</script>
<script type="text/JavaScript">
var f0 = document.forms[0];

function inicializa_grupos() {
  var check;
  <% for (int i = 0; i < inicio_grupo.size() - 1; i++) { %>
    check = true;
    for (j = <%=TextHelper.forJavaScriptBlock(inicio_grupo.get(i))%>; j <= <%=TextHelper.forJavaScriptBlock((((Integer)inicio_grupo.get(i+1)).intValue() - 1))%>; j++){
      if (f0.funcao[j] != undefined && f0.funcao[j].checked == false) {
        check = false;
      }
    }
    if (f0.checkGrupo[<%=(int)i%>] != undefined) {
      f0.checkGrupo[<%=(int)i%>].checked = check;
    }
  <% } %>
}

</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>