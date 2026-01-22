<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  String mnu_codigo = (String) request.getAttribute("mnu_codigo");
  String mnu_descricao = (String) request.getAttribute("mnu_descricao");
  List<?> itens = (List<?>) request.getAttribute("itens");
  Map<?,?> submenus = (Map<?,?>) request.getAttribute("submenus");
  String listaSubItens = (String) request.getAttribute("listaSubItens");
%>
<c:set var="title">
  <hl:message key="rotulo.lst.itens.menu.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
  <form NAME="form1" METHOD="post" action="../v3/editarMenu?acao=salvarItemMenu&MNU_CODIGO=<%=TextHelper.forHtmlAttribute(mnu_codigo)%>&MNU_DESCRICAO=<%=TextHelper.forHtmlAttribute(mnu_descricao)%>&<%=SynchronizerToken.generateToken4URL(request)%>">
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><%=TextHelper.forHtmlContent(mnu_descricao)%></h2>
      </div>
      <div class="card-body table-responsive p-0">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th scope="col"><hl:message key="rotulo.lst.menu.sequencia"/></th>
              <th scope="col"><hl:message key="rotulo.lst.menu.descricao"/></th>
              <th scope="col"><hl:message key="rotulo.lst.itens.menu.separador"/></th>
              <th scope="col"><hl:message key="rotulo.lst.itens.menu.ativo"/></th>
            </tr>            
          </thead>
          <tbody>
  <%=JspHelper.msgRstVazio(itens.size()==0, 4, responsavel)%>
  <%
    String pai = "";
    int count = 0;              
    Iterator<?> itItens = itens.iterator();
    while (itItens.hasNext()) {
        TransferObject to = (TransferObject) itItens.next();
        String mnuCodigo = to.getAttribute(Columns.MNU_CODIGO).toString();
        String mnuDescricao = to.getAttribute(Columns.MNU_DESCRICAO).toString();
        String itmCodigo = to.getAttribute(Columns.ITM_CODIGO).toString();
        String itmDescricao = (String) to.getAttribute(Columns.ITM_DESCRICAO);
        String itmCodigoPai = (to.getAttribute(Columns.ITM_CODIGO_PAI) != null)? to.getAttribute(Columns.ITM_CODIGO_PAI).toString(): "";
        String itmSequencia = to.getAttribute(Columns.ITM_SEQUENCIA).toString();
        String itmAtivo = to.getAttribute(Columns.ITM_ATIVO).toString();                  
        String itmSeparador = to.getAttribute(Columns.ITM_SEPARADOR).toString();
  %>          
            <tr>
              <td>
                <% if (submenus.containsKey(itmCodigo)) {
                       List<?> lItens = (List<?>) submenus.get(itmCodigo);
                       if (lItens != null) {
                           Iterator<?> it = lItens.iterator();
                           while (it.hasNext()) {
                               String codigo = (String) it.next();
                               listaSubItens += codigo;
                               if (it.hasNext()) {
                                   listaSubItens += "-";
                               }
                           }
                       }
                   }
                %>
                <a href="#no-back" class="btn btn-primary btn-ordenacao pr-0" onClick="AlterarSequencia('subir','<%=TextHelper.forJavaScript(itmCodigo)%>','<%=(int)count%>','<%=(int)(itens.size() - 1)%>');">
                  <svg width="15">
                   <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-avancar"></use>
                  </svg>
                </a>
                <a href="#no-back" class="btn btn-primary btn-ordenacao pr-0" onClick="AlterarSequencia('descer','<%=TextHelper.forJavaScript(itmCodigo)%>','<%=(int)count%>','<%=(int)(itens.size() - 1)%>');">
                  <svg width="15">
                   <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-voltar"></use>
                  </svg>
                </a>
                <hl:htmlinput name="SUBITENS" type="hidden" value="<%=TextHelper.forHtmlAttribute(listaSubItens)%>" />              
              </td>
              <td>
                <hl:htmlinput name="ITM_DESCRICAO" type="text" classe="form-control" value="<%=TextHelper.forHtmlAttribute((!itmCodigoPai.equals(""))? (pai + " --> " + itmDescricao): itmDescricao)%>" size="70" mask="#*100"/>                    
                <hl:htmlinput name="ITM_DESCRICAO_OLD" type="hidden" value="<%=TextHelper.forHtmlAttribute(itmDescricao)%>" />
                <hl:htmlinput name="ITM_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(itmCodigo)%>" />
                <hl:htmlinput name="ITM_CODIGO_PAI" type="hidden" value="<%=TextHelper.forHtmlAttribute(itmCodigoPai)%>" />
              </td>
              <td><input name="ITM_SEPARADOR" type="checkbox" value="<%=(int) count%>" <%=(String)(itmSeparador.equals(CodedValues.TPC_SIM) ? "CHECKED" : "")%> /></td>
              <td><input name="ITM_ATIVO" type="checkbox" value="<%=(int) count%>" <%=(String)(itmAtivo.equals("1") ? "CHECKED" : "")%> onClick="javascript:VerificarItemAtivo('<%=(int)(count)%>');" /></td>
            </tr>
  <%
    pai = itmCodigoPai.equals("")? itmDescricao: pai;
    listaSubItens = "";
    count++;
    } 
  %>            
          </tbody>
        </table>
      </div>
    </div>
    <div class="btn-action">
      <hl:htmlinput name="MM_update" type="hidden"  value="form1" />
      <a class="btn btn-outline-danger" href="#no-back" onClick="<%="postData('" + TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)) + "'); return false;"%>"><hl:message key="rotulo.botao.cancelar"/></a>
  <% if(!submenus.isEmpty() || !itens.isEmpty()) {%>
      <a class="btn btn-primary" href="#no-back" onClick="javascript:f0.submit(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
  <%} %>
    </div>              
  </form> 
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
  <script type="text/JavaScript">
    function AlterarSequencia(acao, codigo, count, size) {
      var atual = (count - 0);
      var vetor = new Array();    
      var codigo = f0.ITM_CODIGO;
      var codigoPai = f0.ITM_CODIGO_PAI;
      var subitens = f0.SUBITENS;    
      var elemento = 0;
  
      if ((atual == 0 && acao == "subir") || (atual == size && acao == "descer")) {
          return;
      } else if (acao == "subir" && codigoPai[atual].value == "" && codigoPai[atual - 1].value != "") {
          var j = atual - 1;
          while (subitens[j].value == "") {
              j--;
          }    
          elemento = j;
      } else if (acao == "subir") {
          elemento = atual - 1;
      } else if (acao == "descer") {
          elemento = atual + 1;
      } 
  
      if (codigoPai[atual].value != "" && codigoPai[atual].value != codigoPai[elemento].value) { 
          return;
      }
        
      if (subitens[atual].value != "" || subitens[elemento].value != "") {
          var valores = (subitens[atual].value != "")? subitens[atual].value: subitens[elemento].value;
          vetor = valores.split('-');
          if (acao == "descer" && subitens[atual].value != "" && codigo[codigo.length - 1].value == vetor[vetor.length - 1]) {
              return;
          }
          var i = 0;        
          if (acao == "descer" && subitens[atual].value != "") { // Subir elemento que esta abaixo de todos 
              for (i = (atual + vetor.length + 1); i > atual; i--) {
                  TrocarElementos((i - 1), i);
              }
          } else if (acao == "subir" && subitens[atual].value != "") { // Descer elemento que esta acima de todos 
              for (i = atual; i <= (atual + vetor.length); i++) {
                  TrocarElementos((i - 1), i);
              }
          } else if (acao == "subir" && codigoPai[atual - 1].value != "") { // Subir elemento que esta abaixo de todos
              for (i = (elemento + vetor.length + 1); i > elemento; i--) {
                  TrocarElementos((i - 1), i);
              }
          } else if (acao == "descer" && subitens[atual + 1].value != "") {// Descer elemento que esta acima de todos 
              for (i = atual; i <= (atual + vetor.length); i++) {
                  TrocarElementos(i, (i + 1));
              }
          }
      } else {
          TrocarElementos(atual, elemento);
      }
    }
  
    function TrocarElementos(x, y) {
      var temp;    
      var descricao = f0.ITM_DESCRICAO;
      var descricaoOld = f0.ITM_DESCRICAO_OLD;
      var separador = f0.ITM_SEPARADOR;
      var ativo = f0.ITM_ATIVO;
      var codigo = f0.ITM_CODIGO;
      var codigoPai = f0.ITM_CODIGO_PAI;
      var subitens = f0.SUBITENS;
            
      temp = descricao[x].value;
      descricao[x].value = descricao[y].value;
      descricao[y].value = temp;
      temp = descricaoOld[x].value;
      descricaoOld[x].value = descricaoOld[y].value;
      descricaoOld[y].value = temp;
      temp = separador[x].checked;
      separador[x].checked = separador[y].checked;
      separador[y].checked = temp;
      temp = codigo[x].value;
      codigo[x].value = codigo[y].value;
      codigo[y].value = temp;
      temp = codigoPai[x].value;
      codigoPai[x].value = codigoPai[y].value;
      codigoPai[y].value = temp;
      temp = subitens[x].value;
      subitens[x].value = subitens[y].value;
      subitens[y].value = temp;
      temp = ativo[x].checked;
      ativo[x].checked = ativo[y].checked;
      ativo[y].checked = temp;
    }
  
    function VerificarItemAtivo(count) {
      if (f0.SUBITENS[count].value != "") {
        f0.ITM_ATIVO[count].checked = true;
      }
    }
  </script>
  <script>
  	var f0 = document.forms[0];
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>