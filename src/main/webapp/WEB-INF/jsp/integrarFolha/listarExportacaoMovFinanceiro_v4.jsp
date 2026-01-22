<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.folha.exportacao.ParametrosExportacao" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

List<?> estabelecimentos = (List<?>) request.getAttribute("estabelecimentos");
List<?> orgaos = (List<?>) request.getAttribute("orgaos");
boolean reexportar = (Boolean) request.getAttribute(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo());
String estCodigoUsuario = (String) request.getAttribute("estCodigoUsuario");
String rotuloEstabelecimento = (String) request.getAttribute("rotuloEstabelecimento");
String rotuloOrgao = (String) request.getAttribute("rotuloOrgao");
%>
<c:set var="title">
  <hl:message key="rotulo.folha.exportacao.movimento.financeiro.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">

  <form name="form1" action="../v3/processarMovimento?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>" method="post">
  <%if (!reexportar){%>
    <div class="btn-action">
      <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/exportarMovimento?acao=reexportar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.reexportar"/></a>
    </div>
  <% } %>
  
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title">
        <hl:message key="rotulo.folha.exportacao.movimento.financeiro.titulo"/>
      </h2>
    </div>
    <div class="card-body table-responsive p-0">
            <table class="table table-striped table-hover">        
  <%
     if (estabelecimentos != null && !estabelecimentos.isEmpty()){    
  %> 
        <thead>
          <tr>
            <th class="colunaUnica" id="chkColumn" scope="col" width="3%" title="Selecione todos os contratos para exportação">
              <div class="form-check">
                <input type="checkbox" class="form-check-input ml-0" name="checkAll" id="checkAll">
              </div>
            </th>
            <th scope="col" width="68%"><hl:message key="rotulo.estabelecimento.singular"/></th>
            <th scope="col"><hl:message key="rotulo.acoes"/></th>
          </tr>          
        </thead>
        <tbody>
          <%    
    Iterator<?> it = estabelecimentos.iterator();
    CustomTransferObject estabelecimento = null;
    String est_identificador, est_nome, est_codigo;
    while (it.hasNext()) {
        estabelecimento = (CustomTransferObject) it.next();
        est_identificador = estabelecimento.getAttribute(Columns.EST_IDENTIFICADOR).toString();
        est_nome = estabelecimento.getAttribute(Columns.EST_NOME).toString();
        est_codigo = estabelecimento.getAttribute(Columns.EST_CODIGO).toString();
  %>
            <tr class="selecionarLinha">
              <td class="colunaUnica">
                <div class="form-check">
                  <input type="checkbox" name="est_codigo" value="<%=TextHelper.forHtmlAttribute(est_codigo)%>">
                </div>
              </td>
              <td class="selecionarColuna"><%=TextHelper.forHtmlContent(est_identificador)%> - <%=TextHelper.forHtmlContent(est_nome)%></td>
              <td class="acoes selecionarColuna">
                <a href="#" name="selecionaAcaoSelecionar" ><hl:message key="rotulo.acoes.selecionar"/></a>
              </td>
            </tr>
  <%
      }
  %>
          
        </tbody>
      
  <% } else {%>
        <thead>
          <tr>
            <th class="colunaUnica" id="chkColumn" scope="col" width="5%" title="Selecione todos os contratos para exportação" style="display: none;">
              <div class="form-check">
                <input type="checkbox" class="form-check-input ml-0" name="checkAll" id="checkAll">
              </div>
            </th>
            <th width="15%" scope="col"><hl:message key="rotulo.folha.data.ultima.exportacao"/></th>
            <th width="68%" scope="col"><hl:message key="rotulo.orgao.singular"/></th>
            <th width="10%" scope="col"><hl:message key="rotulo.estabelecimento.abreviado"/></th>
            <th scope="col"><hl:message key="rotulo.acoes"/></th>
          </tr>          
        </thead>
        <tbody>
  <%
     CustomTransferObject orgao = null;
     String org_identificador, org_nome, org_codigo, est_codigo, est_identificador, hie_data;         

     Iterator<?> it = orgaos.iterator();
     while (it.hasNext()) {
        orgao = (CustomTransferObject) it.next();
        est_codigo = orgao.getAttribute(Columns.EST_CODIGO).toString();
        if (estCodigoUsuario == null || estCodigoUsuario.equals(est_codigo)) {
           est_identificador = orgao.getAttribute(Columns.EST_IDENTIFICADOR).toString();
           org_identificador = orgao.getAttribute(Columns.ORG_IDENTIFICADOR).toString();
           org_nome = orgao.getAttribute(Columns.ORG_NOME).toString();
           org_codigo = orgao.getAttribute(Columns.ORG_CODIGO).toString();
           hie_data = orgao.getAttribute(Columns.HIE_DATA).toString();
           if (hie_data.equals("00/00/0000")) {
               hie_data = "";
           }
  %>
          <tr class="selecionarLinha">
            <td class="colunaUnica" style="display: none;">
              <div class="form-check">
                <input type="checkbox" name="org_codigo" value="<%=TextHelper.forHtmlAttribute(org_codigo)%>">
              </div>
            </td>
            <td class="selecionarColuna"><%=TextHelper.forHtmlContent(hie_data)%></td>
            <td class="selecionarColuna"><%=TextHelper.forHtmlContent(org_identificador)%> - <%=TextHelper.forHtmlContent(org_nome)%></td>
            <td class="selecionarColuna"><%=TextHelper.forHtmlContent(est_identificador)%></td>
            <td class="acoes selecionarColuna">
              <a href="#" name="selecionaAcaoSelecionar" ><hl:message key="rotulo.acoes.selecionar"/></a>
            </td>
          </tr>
  <%
          }
      }
  %>
        </tbody>   
  <% } %>
      
      </table>    
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute((!reexportar) ? "../v3/carregarPrincipal" : "../v3/exportarMovimento?acao=iniciar&" + SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
    <a class="btn btn-primary" href="#no-back" onClick="if (vf_submit()) {f0.submit();} return false;"><hl:message key="rotulo.botao.continuar"/></a>   
    <input name="<%=ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo()%>" type="hidden" value="<%=TextHelper.forHtmlAttribute((!reexportar) ? "" : "true")%>">     
  </div>
  <div class="card">
     <hl:infoPeriodoV4 tipo="movimento"/>
  </div>
  </form>
</c:set>

<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript">
  	window.onload() = formload;
    function formLoad(){
    }
    
    function vf_submit() {
      var checked = false;
      for (i=0; i < f0.elements.length; i++) {
        var e = f0.elements[i];
        if (((e.type == 'check') || (e.type == 'checkbox')) && (e.checked == true)) {
          checked = true;
          break;
        }
      }
      if (!checked) {
        alert('<hl:message key="mensagem.folha.escolha.entidade" arg0="<%=TextHelper.forJavaScript((estabelecimentos != null && !estabelecimentos.isEmpty()) ?  rotuloEstabelecimento  : rotuloOrgao)%>"/>');
        return false;
      }
      return true;
    }
  </script>
  <script type="text/javascript">
    /* **Click na linha
     * 1- Mostrar a coluna de checkbox, quando se clica na linha.
    */
    var clicklinha = false;
  
    $(".selecionarColuna").click(function() {
      // 1- Seleciona a linha e mostra a coluna dos checks
      
      var checked = $("table tbody tr input[type=checkbox]:checked").length;
  
      if (checked == 0) {
  
        if (clicklinha) {
          $("#chkColumn").hide();
          $(".colunaUnica").hide();
        } else {
          $("#chkColumn").show();
          $(".colunaUnica").show();
        }
  
        clicklinha = !clicklinha;
      }
    });
  
    var verificarCheckbox = function () {
      var checked = $("table tbody tr input[type=checkbox]:checked").length;
      var total = $("table tbody tr input[type=checkbox]").length;
      $("input[id*=checkAll]").prop('checked', checked == total);
      if (checked == 0) {
        $("table thead tr th.colunaUnica, table tbody tr td.colunaUnica").hide();
      } else {
        $("table thead tr th.colunaUnica, table tbody tr td.colunaUnica").show();
      }
    };
  
    $("table tbody tr td").not("td.colunaUnica, td.selecioneCheckBox").click(function (e) {
      $(e.target).parents('tr').find('input[type=checkbox]').click();
    });
  
    function escolhechk(idchk,e) {
      $(e).parents('tr').find('input[type=checkbox]').click();
    }
  
    $("table tbody tr input[type=checkbox]").click(function (e) {
      verificarCheckbox();
      var checked = e.target.checked;
      if (checked) {
        $(e.target).parentsUntil("tbody",".selecionarLinha").addClass("table-checked");
      } else {
        $(e.target).parentsUntil("tbody",".selecionarLinha").removeClass("table-checked");
      }
    });
  
    $("input[id*=checkAll").click(function (e){
      var checked = e.target.checked;
      $('table tbody tr input[type=checkbox]').prop('checked', checked);
      if (checked) {
        $(".selecionarLinha").addClass("table-checked");
      } else {
        $(".selecionarLinha").removeClass("table-checked");
      }
      verificarCheckbox();
    });
  
  </script>
  <script type="text/JavaScript">
    var f0 = document.forms[0];
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>