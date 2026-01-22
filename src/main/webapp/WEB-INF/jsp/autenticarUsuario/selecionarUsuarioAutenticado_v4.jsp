<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="java.util.stream.Collectors"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.persistence.entity.Papel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> usuarioList = (List<TransferObject>) request.getAttribute("usuarioList");
List<Papel> papeis  = (List<Papel>) request.getAttribute("papeis");
String usuCentralizador = request.getParameter("usuCentralizador") != null ? request.getParameter("usuCentralizador") : "";
String urlCentralizadorAcesso = request.getParameter("urlCentralizadorAcesso") != null ? request.getParameter("urlCentralizadorAcesso") : ""; 
session.setAttribute("usuarioList", usuarioList);
%>
<c:set var="bodyContent">
   <form>      
      <div class="alert alert-warning" role="alert">
              <hl:message key="rotulo.tela.login.mensagem.selecione.usuario"/>
      </div>
      <div class="form-group">
          <label for="iUsuario"><hl:message key="rotulo.usuario.singular"/></label>
          <select class="form-control" id="iUsuario" name="iUsuario">
                 <option value=""><hl:message key="rotulo.campo.selecione"/></option>
                 <% int i = 0;
                    for (TransferObject usuario: usuarioList) { %>
                      <option value="<%=usuario.getAttribute(Columns.USU_CODIGO) %>" <%if(i ==0){ %>selected<%} %>><%=TextHelper.forHtmlContent(usuario.getAttribute(Columns.USU_LOGIN)) %></option>
                 <% i++;
                    } %>
          </select>     
      </div>
      <div class="form-group">
        <label for="iNome"><hl:message key="rotulo.nome"/></label>
        <input type="text" class="form-control" id="iNome" name="nNome" value="<%=TextHelper.forHtmlContent(usuarioList.get(0).getAttribute(Columns.USU_NOME)) %>" disabled>
      </div>
            <div class="form-group">
              <label for="iPapel"><hl:message key="mensagem.menu.papel.usuario.sso"/></label>
              <%  
                  TransferObject usuario = usuarioList.get(0);
                  String csaNome = (String) usuario.getAttribute(Columns.CSA_NOME);
                  String cseNome = (String) usuario.getAttribute(Columns.CSE_NOME);
                  String corNome = (String) usuario.getAttribute(Columns.COR_NOME);
                  String orgNome = (String) usuario.getAttribute(Columns.ORG_NOME);
                  String serNome = (String) usuario.getAttribute(Columns.SER_NOME);
                  String supNome = (String) usuario.getAttribute("SUP_NOME");
              
                  List<Papel> papelFiltrado = papeis.stream().filter(pap -> (!TextHelper.isNull(csaNome)) ? pap.getPapCodigo().equals(CodedValues.PAP_CONSIGNATARIA) :
                  (!TextHelper.isNull(cseNome)) ? pap.getPapCodigo().equals(CodedValues.PAP_CONSIGNANTE) :
                  (!TextHelper.isNull(corNome)) ? pap.getPapCodigo().equals(CodedValues.PAP_CORRESPONDENTE) :
                  (!TextHelper.isNull(orgNome)) ? pap.getPapCodigo().equals(CodedValues.PAP_ORGAO) :
                  (!TextHelper.isNull(serNome)) ? pap.getPapCodigo().equals(CodedValues.PAP_SERVIDOR) : 
                  pap.getPapCodigo().equals(CodedValues.PAP_SUPORTE)).collect(Collectors.toList());
                  
                  String perDescricao = (!TextHelper.isNull(usuario.getAttribute(Columns.PER_DESCRICAO))) ? (String) usuario.getAttribute(Columns.PER_DESCRICAO) : 
                      ApplicationResourcesHelper.getMessage("rotulo.usuario.perfil.personalizado", responsavel);
                  
                  String entidadeNome = (!TextHelper.isNull(csaNome)) ? csaNome : (!TextHelper.isNull(cseNome)) ? cseNome : (!TextHelper.isNull(corNome)) ? 
                          corNome : (!TextHelper.isNull(orgNome)) ? orgNome : (!TextHelper.isNull(serNome)) ? serNome : supNome;
              %>
              <input type="text" class="form-control" id="iPapel" name="nPapel" value="<%=TextHelper.forHtmlAttribute(papelFiltrado.get(0).getPapDescricao()) %>" disabled>
            </div>
            <div class="form-group">
              <label for="iPerfil"><hl:message key="rotulo.perfil.singular"/></label>
              <input type="text" class="form-control" id="iPerfil" name="nPerfil" value="<%=TextHelper.forHtmlAttribute(perDescricao) %>" disabled>
            </div>
            <div class="form-group">
              <label for="iNomeEntidade"><hl:message key="rotulo.usuario.entidade"/></label>
              <input type="text" class="form-control" id="iNomeEntidade" name="nNomeEntidade" value="<%=TextHelper.forHtmlAttribute(entidadeNome) %>" disabled>
            </div>
      <div class="clearfix">
          <button class="btn btn-primary" onclick="selecionaUsuario(); return false;"><svg width="17"><use xlink:href="#i-avancar"></use></svg><hl:message key="rotulo.acoes.selecionar"/></button>
          </div>  
      <input name="usuCentralizador" type="hidden" value="<%=usuCentralizador%>">
      <input name="urlCentralizadorAcesso" type="hidden" value="<%=urlCentralizadorAcesso%>">
  </form>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
     var usuarios = {
    	 <%int j = 0;
           for (TransferObject usuario: usuarioList) {
    	     String csaNome = (String) usuario.getAttribute(Columns.CSA_NOME);
             String cseNome = (String) usuario.getAttribute(Columns.CSE_NOME);
             String corNome = (String) usuario.getAttribute(Columns.COR_NOME);
             String orgNome = (String) usuario.getAttribute(Columns.ORG_NOME);
             String serNome = (String) usuario.getAttribute(Columns.SER_NOME);
             String supNome = (String) usuario.getAttribute("SUP_NOME");
             String entidadeNome = (!TextHelper.isNull(csaNome)) ? csaNome : (!TextHelper.isNull(cseNome)) ? cseNome : (!TextHelper.isNull(corNome)) ? 
                     corNome : (!TextHelper.isNull(orgNome)) ? orgNome : (!TextHelper.isNull(serNome)) ? serNome : supNome;
             
    	     List<Papel> papelFiltrado = papeis.stream().filter(pap -> (!TextHelper.isNull(csaNome)) ? pap.getPapCodigo().equals(CodedValues.PAP_CONSIGNATARIA) :
                 (!TextHelper.isNull(cseNome)) ? pap.getPapCodigo().equals(CodedValues.PAP_CONSIGNANTE) :
                 (!TextHelper.isNull(corNome)) ? pap.getPapCodigo().equals(CodedValues.PAP_CORRESPONDENTE) :
                 (!TextHelper.isNull(orgNome)) ? pap.getPapCodigo().equals(CodedValues.PAP_ORGAO) :
                 (!TextHelper.isNull(serNome)) ? pap.getPapCodigo().equals(CodedValues.PAP_SERVIDOR) : 
                 pap.getPapCodigo().equals(CodedValues.PAP_SUPORTE)).collect(Collectors.toList());
           
    	     String perDescricao = (!TextHelper.isNull(usuario.getAttribute(Columns.PER_DESCRICAO))) ? (String) usuario.getAttribute(Columns.PER_DESCRICAO) : 
                 ApplicationResourcesHelper.getMessage("rotulo.usuario.perfil.personalizado", responsavel);
           
    	 %>
              <%if (j > 1){%>,<%}%>"<%=usuario.getAttribute(Columns.USU_CODIGO)%>" : ["<%=TextHelper.forJavaScript(usuario.getAttribute(Columns.USU_NOME))%>"
                                                                , "<%=TextHelper.forJavaScript(papelFiltrado.get(0).getPapDescricao())%>", "<%=TextHelper.forJavaScript(perDescricao)%>"
                                                                , "<%=TextHelper.forJavaScript(entidadeNome)%>"]<%if (j == 0){%>,<%}%>
         <%j++;
           }%>};

         $(document).ready(function() {
        	 $('#iUsuario').on('change', function (e) {
        		 $('#iNome').val(usuarios[$(this).val()][0]);
        		 $('#iPapel').val(usuarios[$(this).val()][1]);
        		 $('#iPerfil').val(usuarios[$(this).val()][2]);
        		 $('#iNomeEntidade').val(usuarios[$(this).val()][3]);
     		  });
         });

      function selecionaUsuario() {
          var usuSelecionado = document.forms[0].iUsuario.value;
          var usuCentralizador = document.forms[0].usuCentralizador.value;
          var urlCentralizadorAcesso = document.forms[0].urlCentralizadorAcesso.value;
    	  postData('../v3/autenticarUsuario?acao=finalizarAutenticacao&usuCodigo=' + usuSelecionado + '&usuCentralizador=' + usuCentralizador + '&urlCentralizadorAcesso=' + urlCentralizadorAcesso + '&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>');
      }       
  </script>
</c:set>
<t:empty_v4>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:empty_v4>