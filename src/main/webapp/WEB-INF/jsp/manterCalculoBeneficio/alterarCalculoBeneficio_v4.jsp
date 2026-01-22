<%--
* <p>Title: alterarCalculoBeneficio_v4</p>
* <p>Description: Alterar calculo benefício v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: larissa.silva $
* $Revision: 24740 $
* $Date: 2018-07-04 00:00:00 -0300 (Qua, 04 jun 2018) $
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.zetra.econsig.persistence.entity.CalculoBeneficio"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.values.TipoBeneficiarioEnum"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  Boolean podeEditar = (Boolean) request.getAttribute("podeEditar");
  Boolean novo = (Boolean) request.getAttribute("novo");
  String clbCodigo = (String) request.getAttribute("clbCodigo");
  CalculoBeneficio calculoBeneficio = (CalculoBeneficio) request.getAttribute("calculoBeneficio");
  List<TransferObject> orgaos = (List) request.getAttribute("orgaos");
  List<TransferObject> beneficios = (List) request.getAttribute("beneficios");
  List<TransferObject> tipoBeneficiarios = (List) request.getAttribute("tipoBeneficiarios");
  List<TransferObject> grauParentesco = (List) request.getAttribute("grauParentesco");
  List<TransferObject> motivoDependencia = (List) request.getAttribute("motivoDependencia");

%>
<c:set var="javascript">
  <script type="text/JavaScript">
    function formLoad() {
      f0 = document.forms[0];
    }

    window.onload = formLoad;
  
    function verificaCampos() {
      var controles = new Array("benCodigo", "clbValorMensalidade");
      var msgs = new Array("<hl:message key='mensagem.beneficio.informar'/>", "<hl:message key='mensagem.beneficio.mensalidade.valor'/>");
  
      if (!ValidaCampos(controles, msgs)) {
        return false;
      }
      f0.submit();
      return true;
    }
    
    function analisaGrauParentesco(selectObject) {
  	  let value = selectObject.value
  	  let grauP = document.getElementById("grpCodigo")
  	  if (<%=TipoBeneficiarioEnum.TITULAR.tibCodigo%> == value ) {
  		  grauP.setAttribute("disabled","");
  	  } else {
  		  grauP.removeAttribute("disabled","");
  	  }
    }
    
  </script>
</c:set>
<c:set var="title">
<% if(novo) { %>
  <hl:message key="rotulo.calculo.beneficio.inclusao.minusculo"/>
<% } else if(podeEditar) { %>
  <hl:message key="rotulo.calculo.beneficio.edicao.minusculo"/>
<% } else { %>
  <hl:message key="rotulo.calculo.beneficio.visualizar.minusculo"/>
<% } %>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent">
  <form method="post" action="../v3/alterarCalculoBeneficio?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
          <% if(novo) { %>
            <hl:message key="rotulo.calculo.beneficio.inclusao.minusculo"/>
          <% } else if(podeEditar) { %>
            <hl:message key="rotulo.calculo.beneficio.edicao.minusculo"/>
          <% } else { %>
            <hl:message key="rotulo.calculo.beneficio.visualizar.minusculo"/>
          <% } %>
        </div>
        <input class="Edit" type="hidden" name="clbCodigo" value="<%=!novo ? TextHelper.forHtmlAttribute(clbCodigo) : ""%>" size="32" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
        <div class="card-body">
          <div class="row">
            <div class="form-group col-sm">
              <label for="orgCodigo"><hl:message key="rotulo.calculo.beneficio.orgao" /></label>
              <% if(podeEditar) { %>
              <%=JspHelper.geraCombo(orgaos, "orgCodigo", Columns.ORG_CODIGO, Columns.ORG_NOME, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && calculoBeneficio.getOrgao() != null ? calculoBeneficio.getOrgao().getOrgCodigo() : "", null, false, "form-control")%>
              <% } else { %>
              <%=JspHelper.geraCombo(orgaos, "orgCodigo", Columns.ORG_CODIGO, Columns.ORG_NOME, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && calculoBeneficio.getOrgao() != null ? calculoBeneficio.getOrgao().getOrgCodigo() : "", null, true, "form-control")%>
              <% } %>
            </div>
            <div class="form-group col-sm">
              <label for="benCodigo"><hl:message key="rotulo.calculo.beneficio.descricao" /></label>
              <% if(podeEditar) { %>
              <%=JspHelper.geraCombo(beneficios, "benCodigo", Columns.BEN_CODIGO, Columns.BEN_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo ? calculoBeneficio.getBeneficio().getBenCodigo() : "", null, false, "form-control")%>
              <% } else { %>
              <%=JspHelper.geraCombo(beneficios, "benCodigo", Columns.BEN_CODIGO, Columns.BEN_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo ? calculoBeneficio.getBeneficio().getBenCodigo() : "", null, true, "form-control")%>
              <% } %>
            </div>
            
          </div>
          <div class="row">
            <div class="form-group col-sm">
              <label for="tibCodigo"><hl:message key="rotulo.calculo.beneficio.tipo.beneficiario" /></label>
              <% if(podeEditar) { %>
              <%=JspHelper.geraCombo(tipoBeneficiarios, "tibCodigo", Columns.TIB_CODIGO, Columns.TIB_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && calculoBeneficio.getTipoBeneficiario() != null ? calculoBeneficio.getTipoBeneficiario().getTibCodigo() : "", "analisaGrauParentesco(this)", false, "form-control")%>
              <% } else { %>
              <%=JspHelper.geraCombo(tipoBeneficiarios, "tibCodigo", Columns.TIB_CODIGO, Columns.TIB_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && calculoBeneficio.getTipoBeneficiario() != null ? calculoBeneficio.getTipoBeneficiario().getTibCodigo() : "", "analisaGrauParentesco(this)", true, "form-control")%>
              <% } %>
            </div>
            <div class="form-group col-sm">
              <label for="grpCodigo"><hl:message key="rotulo.calculo.beneficio.grau.parentesco" /></label>
              <% if(podeEditar) { %>
                  <%=JspHelper.geraCombo(grauParentesco, "grpCodigo", Columns.GRP_CODIGO, Columns.GRP_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && calculoBeneficio.getGrauParentesco() != null ? calculoBeneficio.getGrauParentesco().getGrpCodigo() : "", null, false, "form-control")%>
              <% } else { %>
                  <%=JspHelper.geraCombo(grauParentesco, "grpCodigo", Columns.GRP_CODIGO, Columns.GRP_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && calculoBeneficio.getGrauParentesco() != null ? calculoBeneficio.getGrauParentesco().getGrpCodigo() : "", null, true, "form-control")%>
              <% } %>
            </div>
            <div class="form-group col-sm-6">
              <label for="grpCodigo"><hl:message key="rotulo.beneficiario.motivo.dependencia" /></label>
              <% if(podeEditar) { %>
                  <%=JspHelper.geraCombo(motivoDependencia, "mdeCodigo", Columns.MDE_CODIGO, Columns.MDE_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && calculoBeneficio.getMotivoDependencia() != null ? calculoBeneficio.getMotivoDependencia().getMdeCodigo() : "", null, false, "form-control")%>
              <% } else { %>
                  <%=JspHelper.geraCombo(motivoDependencia, "mdeCodigo", Columns.MDE_CODIGO, Columns.MDE_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && calculoBeneficio.getMotivoDependencia() != null ? calculoBeneficio.getMotivoDependencia().getMdeCodigo() : "", null, true, "form-control")%>
              <% } %>
            </div>
          </div>
          <div class="row">
            <div class="form-group col-sm-3">
              <label for="clbFaixaEtariaIni"><hl:message key="rotulo.calculo.beneficio.inicio.faixa.etaria" /></label>
              <INPUT min="0" class="Edit form-control" TYPE="number" NAME="clbFaixaEtariaIni"
                  VALUE="<%=!novo && calculoBeneficio.getClbFaixaEtariaIni() != null? TextHelper.forHtmlAttribute(calculoBeneficio.getClbFaixaEtariaIni()) : ""%>"
                  SIZE="32" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String) (!podeEditar ? "disabled" : "")%>>
            </div>
            <div class="form-group col-sm-3">
              <label for=""><hl:message key="rotulo.calculo.beneficio.fim.faixa.etaria" /></label>
              <INPUT min="0" class="Edit form-control" TYPE="number" NAME="clbFaixaEtariaFim"
              VALUE="<%=!novo && calculoBeneficio.getClbFaixaEtariaFim() != null ? TextHelper.forHtmlAttribute(calculoBeneficio.getClbFaixaEtariaFim()) : ""%>"
              SIZE="32" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String) (!podeEditar ? "disabled" : "")%>>
            </div>
            <div class="form-group col-sm-3">
              <label for="clbFaixaSalarialIni"><hl:message key="rotulo.calculo.beneficio.inicio.faixa.salarial" /> (<hl:message key="rotulo.moeda" />)</label>
              <hl:htmlinput name="clbFaixaSalarialIni" type="text" classe="Edit form-control" others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>" 
              di="clbFaixaSalarialIni" size="12" mask="#F15" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
              value="<%=!novo && calculoBeneficio.getClbFaixaSalarialIni() != null ? TextHelper.forHtmlAttribute(calculoBeneficio.getClbFaixaSalarialIni().toString().replace(".", ",")) : ""%>" />
            </div>
            <div class="form-group col-sm-3">
              <label for="clbFaixaSalarialFim"><hl:message key="rotulo.calculo.beneficio.fim.faixa.salarial" /> (<hl:message key="rotulo.moeda" />)</label>
              <hl:htmlinput name="clbFaixaSalarialFim" type="text" classe="Edit form-control" others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>"
              di="clbFaixaSalarialFim" size="12" mask="#F15" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
              value="<%=!novo && calculoBeneficio.getClbFaixaSalarialFim() != null ? TextHelper.forHtmlAttribute(calculoBeneficio.getClbFaixaSalarialFim().toString().replace(".", ",")) : ""%>" />
            </div>
          </div>
          <div class="row">
            <div class="form-group col-sm-3">
              <label for="clbValorMensalidade"><hl:message key="rotulo.calculo.beneficio.valor.beneficio" />&nbsp;(<hl:message key="rotulo.moeda" />)</label>
              <hl:htmlinput name="clbValorMensalidade" type="text" classe="Edit form-control" others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>"
              di="clbValorMensalidade" size="12" mask="#F15" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
              value="<%=!novo && calculoBeneficio.getClbValorMensalidade() != null ? TextHelper.forHtmlAttribute(calculoBeneficio.getClbValorMensalidade().toString().replace(".", ",")) : ""%>" />
            </div>
            <div class="form-group col-sm-3">
              <label for=""><hl:message key="rotulo.calculo.beneficio.valor.subsidio" />&nbsp;(<hl:message key="rotulo.moeda" />)</label>
              <hl:htmlinput name="clbValorSubsidio" type="text" classe="Edit form-control" others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>"
                    di="clbValorSubsidio" size="12" mask="#F15" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                    value="<%=!novo && calculoBeneficio.getClbValorSubsidio() != null ? TextHelper.forHtmlAttribute(calculoBeneficio.getClbValorSubsidio().toString().replace(".", ",")): ""%>" />
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="btn-action col-sm">
      <a class="btn btn-outline-danger" href="#no-back" name="Button" onClick="postData('../v3/consultarCalculoBeneficio?acao=consultar&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;" value="Cancelar"><hl:message key="rotulo.botao.cancelar" /></a> 
      <% if(podeEditar) { %>
      <a class="btn btn-primary" href="#no-back" name="submit2" id="submit2" value="Salvar" onClick="javascript: verificaCampos(); return false;"><hl:message key="rotulo.botao.salvar" /></a>
      <% } %>
    </div>
  </form>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
  <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
