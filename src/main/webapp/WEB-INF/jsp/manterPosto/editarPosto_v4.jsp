<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%
String pos_codigo = (String) request.getAttribute("pos_codigo");
String pos_descricao = (String) request.getAttribute("pos_descricao");
String pos_identificador = (String) request.getAttribute("pos_identificador");
String pos_valor_soldo = (String) request.getAttribute("pos_valor_soldo");
String pos_perc_tx_uso = (String) request.getAttribute("pos_perc_tx_uso");
String pos_perc_tx_uso_cond = (String) request.getAttribute("pos_perc_tx_uso_cond");
String linkRet = (String) request.getAttribute("linkRet");
String msgErro = (String) request.getAttribute("msgErro");
%>
<c:set var="title">
  <hl:message key="rotulo.editar.posto.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <form method="post" action="../v3/manterPosto?acao=editar&pos=<%=TextHelper.forHtmlAttribute(pos_codigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.editar.grid"/></h2>
      </div>
      <div class="card-body">
          <fieldset>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="posIdentificador"><hl:message key="rotulo.posto.codigo"/></label>
                <hl:htmlinput name="posIdentificador" type="text" classe="form-control" value="<%=TextHelper.forHtmlAttribute(pos_identificador)%>" mask="#A40"/>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="posDescricao"><hl:message key="rotulo.posto.descricao"/></label>
                <hl:htmlinput name="posDescricao" type="text" classe="form-control" value="<%=TextHelper.forHtmlAttribute(pos_descricao)%>" mask="#*100"/>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="posVlrSoldo"><hl:message key="rotulo.posto.soldo"/></label>
                <hl:htmlinput name="posVlrSoldo" type="text" classe="form-control" value="<%=TextHelper.forHtmlAttribute(NumberHelper.reformat(pos_valor_soldo, "en", NumberHelper.getLang()))%>" mask="#F11" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" others="onChange=\"recalcularTaxas()\""/>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="perTxUso"><hl:message key="rotulo.posto.taxa.uso.percentual"/></label>
                <hl:htmlinput name="perTxUso" type="text" classe="form-control" value="<%=TextHelper.forHtmlAttribute(NumberHelper.reformat(pos_perc_tx_uso, "en", NumberHelper.getLang()))%>" mask="#F5" others="onChange=\"recalcularTaxas()\""/>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="txUso"><hl:message key="rotulo.posto.taxa.uso"/></label>
                <hl:htmlinput name="txUso" type="text" classe="form-control" value="" mask="#F11" others="disabled"/>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="perTxUsoCond"><hl:message key="rotulo.posto.taxa.uso.condominio.percentual"/></label>
                <hl:htmlinput name="perTxUsoCond" type="text" classe="form-control" value="<%=TextHelper.forHtmlAttribute(NumberHelper.reformat(pos_perc_tx_uso_cond, "en", NumberHelper.getLang()))%>" mask="#F5" others="onChange=\"recalcularTaxas()\"" nf="Salvar"/>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="txUsoCond"><hl:message key="rotulo.posto.taxa.uso.condominio"/></label>
                <hl:htmlinput name="txUsoCond" type="text" classe="form-control" value="" mask="#F11" others="disabled"/>                
              </div>
            </div>
          </fieldset>
      </div>
    </div>
    <hl:htmlinput name="POS_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(pos_codigo)%>"/>  
    <hl:htmlinput name="MM_update"  type="hidden" value="form1" />
    <div class="btn-action">
      <a class="btn btn-outline-danger" href="#no-back" onClick="<%=(String)"postData('" + linkRet + "'); return false;"%>"><hl:message key="rotulo.botao.cancelar"/></a>
      <a class="btn btn-primary" href="#no-back" id="Salvar" onClick="f0.submit(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
    </div>
  </form>   
        
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript">
  	var f0 = document.forms[0];
  	
  	window.onload = formLoad; 

    function formLoad() {
      focusFirstField();
      recalcularTaxas();
    }
    function recalcularTaxas() {
      with (document.forms[0]) {
        var vlrSoldo = parseFloat(parse_num(posVlrSoldo.value));
        var vlrPerTxUso = parseFloat(parse_num(perTxUso.value));
        var vlrPerTxUsoCond = parseFloat(parse_num(perTxUsoCond.value));
    
        txUso.value = FormataContabilEx(vlrSoldo * vlrPerTxUso * 0.01, 2);
        txUsoCond.value = FormataContabilEx(vlrSoldo * vlrPerTxUsoCond * 0.01, 2);
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