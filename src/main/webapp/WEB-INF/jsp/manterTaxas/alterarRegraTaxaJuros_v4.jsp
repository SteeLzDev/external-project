<%--
* <p>Title: alterardefinicaoTaxaJuros_v4</p>
* <p>Description: Alterar calculo benefício v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: larissa.silva $
* $Revision: 24740 $
* $Date: 2018-07-04 00:00:00 -0300 (Qua, 04 jun 2018) $
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.zetra.econsig.persistence.entity.DefinicaoTaxaJuros"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper"%>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
Boolean podeEditar = (Boolean) request.getAttribute("podeEditar");
Boolean novo = (Boolean) request.getAttribute("novo");
String csaCodigo = (String) request.getAttribute("csaCodigo");
String dtjCodigo = (String) request.getAttribute("dtj_codigo");
DefinicaoTaxaJuros definicaoTaxaJuros = (DefinicaoTaxaJuros) request.getAttribute("definicaoTaxaJuros");
List<TransferObject> orgaos = (List) request.getAttribute("orgaos");
List<TransferObject> servicos = (List) request.getAttribute("servicos");
List<TransferObject> funcoes = (List) request.getAttribute("funcoes");
Boolean aplicarRegraCETTaxaJuros = (Boolean) request.getAttribute("aplicarRegraCETTaxaJuros");
Boolean temLimiteTaxa = (Boolean) request.getAttribute("temLimiteTaxa");
Boolean exibeCETMinMax = (Boolean) request.getAttribute("exibeCETMinMax");
List<TransferObject> limitesTaxa = (List<TransferObject>) request.getAttribute("limitesTaxa");
%>
<c:set var="title">
  <% if (novo) { %>
      <hl:message key="rotulo.regra.taxa.juros.inclusao" />
  <%} else if (podeEditar) {%>
      <hl:message key="rotulo.regra.taxa.juros.editar.titulo" />
  <%} else {%>
      <hl:message key="rotulo.regra.taxa.juros.listar.titulo" />
  <%}%>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <form method="post"
    action="../v3/editarRegraTaxaJuros?acao=salvar&_skip_history_=true&csaCodigo=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&dtjCodigo=<%=TextHelper.forJavaScriptAttribute(dtjCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>"
    name="form1" id="form1">
      <div class="card">
        <div class="card-header">
          <h3 class="card-header-title">
            <% if (novo) { %>
                <hl:message key="rotulo.regra.taxa.juros.inclusao" />
            <% } else if (podeEditar) {%>
                <hl:message key="rotulo.regra.taxa.juros.editar.titulo" />
            <%} else {%>
                <hl:message key="rotulo.regra.taxa.juros.listar.titulo" />
            <%}%>
          </h3>
        </div>
        <hl:htmlinput type="hidden" name="dtjCodigo" di="dtjCodigo"
          value="<%=TextHelper.forHtmlAttribute(dtjCodigo)%>" />
        <hl:htmlinput type="hidden" name="CSA_CODIGO" di="CSA_CODIGO"
          value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>" />
        <div class="card-body">
          <div class="row">
            <div class="form-group col-sm">
              <label for="orgCodigo"><hl:message
                  key="rotulo.regra.taxa.juros.orgao" /></label>
              <%if (podeEditar) {%>
                  <%=JspHelper.geraCombo(orgaos, "orgCodigo", Columns.ORG_CODIGO, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && definicaoTaxaJuros.getOrgao() != null ? definicaoTaxaJuros.getOrgao().getOrgCodigo() : "", null, false, "form-control")%>
              <%} else {%>
                  <%=JspHelper.geraCombo(orgaos, "orgCodigo", Columns.ORG_CODIGO, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && definicaoTaxaJuros.getOrgao() != null ? definicaoTaxaJuros.getOrgao().getOrgCodigo() : "", null, true, "form-control")%>
              <%}%>
            </div>
            <div class="form-group col-sm">
              <label for="svcCodigo"><hl:message key="rotulo.servico.singular" /></label>
              <% if(aplicarRegraCETTaxaJuros) { %>
	              <%if (novo && podeEditar) {%>
	                  <%=JspHelper.geraCombo(servicos, "svcCodigo", Columns.SVC_CODIGO + ";" + Columns.SVC_DESCRICAO, Columns.SVC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 5, null, null, false, "form-control")%>
	              <%} else if(!novo && podeEditar){%>
	                  <%=JspHelper.geraCombo(servicos, "svcCodigo", Columns.SVC_CODIGO + ";" + Columns.SVC_DESCRICAO, Columns.SVC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && definicaoTaxaJuros.getServico() != null ? definicaoTaxaJuros.getServico().getSvcCodigo() : "", null, false, "form-control")%>
	              <%} else { %>
	              	<%=JspHelper.geraCombo(servicos, "svcCodigo", Columns.SVC_CODIGO + ";" + Columns.SVC_DESCRICAO, Columns.SVC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && definicaoTaxaJuros.getServico() != null ? definicaoTaxaJuros.getServico().getSvcCodigo() : "", null, true, "form-control")%>
	              <% } %>
              <% } else { %>
	              <%if (podeEditar) {%>
	                  <%=JspHelper.geraCombo(servicos, "svcCodigo", Columns.SVC_CODIGO, Columns.SVC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && definicaoTaxaJuros.getServico() != null ? definicaoTaxaJuros.getServico().getSvcCodigo() : "", null, false, "form-control")%>
	              <%} else {%>
	                  <%=JspHelper.geraCombo(servicos, "svcCodigo", Columns.SVC_CODIGO, Columns.SVC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && definicaoTaxaJuros.getServico() != null ? definicaoTaxaJuros.getServico().getSvcCodigo() : "", null, true, "form-control")%>
	              <%}%>
              <% } %>
            </div>
            <div class="form-group col-sm">
              <label for="funCodigo"><hl:message
                  key="rotulo.regra.taxa.juros.funcao"/></label>
              <%if (novo && podeEditar) {%>
                  <%=JspHelper.geraCombo(funcoes, "funCodigo", Columns.FUN_CODIGO, Columns.FUN_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, true, 5, !novo && definicaoTaxaJuros.getFuncao() != null ? definicaoTaxaJuros.getFuncao().getFunCodigo() : "", "verificarExibicaoCamposTaxaJuros()", false, "form-control", "funCodigo")%>
              <%} else if (!novo && podeEditar) {%>
                  <%=JspHelper.geraCombo(funcoes, "funCodigo", Columns.FUN_CODIGO, Columns.FUN_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && definicaoTaxaJuros.getFuncao() != null ? definicaoTaxaJuros.getFuncao().getFunCodigo() : "", "verificarExibicaoCamposTaxaJuros()", false, "form-control", "funCodigo")%>
              <%} else {%>
                  <%=JspHelper.geraCombo(funcoes, "funCodigo", Columns.FUN_CODIGO, Columns.FUN_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, !novo && definicaoTaxaJuros.getFuncao() != null ? definicaoTaxaJuros.getFuncao().getFunCodigo() : "", "verificarExibicaoCamposTaxaJuros()", true, "form-control", "funCodigo")%>
              <%}%>
            </div>
          </div>
          <% if (ShowFieldHelper.showField(FieldKeysConstants.DEFINICAO_TAXA_JUROS_FAIXA_TEMP_SERVICO, responsavel)) { %>
              <fieldset>
                <h3 class="legend">
                  <span><hl:message key="rotulo.regra.taxa.juros.faixa.tempo.servico"/></span>
                </h3>
                <div class="row">
                  <div class="form-group col-sm-3">
                    <label for="faixaTempoServicoInicial"><hl:message
                        key="rotulo.regra.taxa.juros.faixa.tempo.servico.inicial" />
                    </label>
                    <hl:htmlinput name="faixaTempoServicoInicial" type="text"
                      classe="Edit form-control"
                      others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>" 
                      onBlur=" validarIntervalo('faixaTempoServico', this);"
                      di="faixaTempoServicoInicial" size="12" mask="#D3"
                      value="<%=!novo && definicaoTaxaJuros.getDtjFaixaTempServicoIni() != null ? TextHelper.forHtmlAttribute(definicaoTaxaJuros.getDtjFaixaTempServicoIni()) : ""%>"/>
                  </div>
                  <div class="form-group col-sm-3">
                    <label for="faixaTempoServicoFinal"><hl:message
                        key="rotulo.regra.taxa.juros.faixa.tempo.servico.final" />
                    </label>
                    <hl:htmlinput name="faixaTempoServicoFinal" type="text"
                      classe="Edit form-control"
                      others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>"
                      onBlur=" validarIntervalo('faixaTempoServico', this);"
                      di="faixaTempoServicoFinal" size="12" mask="#D3"
                      value="<%=!novo && definicaoTaxaJuros.getDtjFaixaTempServicoFim() != null ? TextHelper.forHtmlAttribute(definicaoTaxaJuros.getDtjFaixaTempServicoFim()) : ""%>"/>
                  </div>
                </div>
              </fieldset>
          <%} %>
          <% if (ShowFieldHelper.showField(FieldKeysConstants.DEFINICAO_TAXA_JUROS_FAIXA_SALARIO, responsavel)) { %>
              <fieldset>
                <h3 class="legend">
                  <span><hl:message key="rotulo.regra.taxa.juros.faixa.salarial"/> (<hl:message key="rotulo.moeda" />)</span>
                </h3>
                <div class="row">
                  <div class="form-group col-sm-3">
                    <label for=faixaSalarialInicial><hl:message
                        key="rotulo.regra.taxa.juros.faixa.salarial.inicial" />
                      (<hl:message key="rotulo.moeda" />)</label>
                    <hl:htmlinput name="faixaSalarialInicial" type="text"
                      classe="Edit form-control"
                      others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>"
                      di="faixaSalarialInicial" size="12" mask="#F15"
                      onBlur="if (this.value != '') { if (!validarIntervalo('faixaSalarial', this) || !validaNegativo(this)) { return false;} else { this.value = FormataContabil(parse_num(this.value), 2);}  }" 
                      value="<%=!novo && definicaoTaxaJuros.getDtjFaixaSalarioIni() != null ? NumberHelper.format(definicaoTaxaJuros.getDtjFaixaSalarioIni().doubleValue(), NumberHelper.getLang()) : ""%>" />
                  </div>
                  <div class="form-group col-sm-3">
                    <label for="faixaSalarialFinal"><hl:message
                        key="rotulo.regra.taxa.juros.faixa.salarial.final" />
                      (<hl:message key="rotulo.moeda" />)</label>
                    <hl:htmlinput name="faixaSalarialFinal" type="text"
                      classe="Edit form-control"
                      others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>"
                      di="faixaSalarialFinal" size="12" mask="#F15"
                      onBlur="if (this.value != '') { if (!validarIntervalo('faixaSalarial', this) || !validaNegativo(this)) { return false;} else { this.value = FormataContabil(parse_num(this.value), 2);}  }"
                      value="<%=!novo && definicaoTaxaJuros.getDtjFaixaSalarioFim() != null ? NumberHelper.format(definicaoTaxaJuros.getDtjFaixaSalarioFim().doubleValue(), NumberHelper.getLang()) : ""%>" />
                  </div>
                </div>
              </fieldset>
          <%} %>
          <% if (ShowFieldHelper.showField(FieldKeysConstants.DEFINICAO_TAXA_JUROS_FAIXA_ETARIA, responsavel)) { %>
              <fieldset>
                <h3 class="legend">
                  <span><hl:message key="rotulo.regra.taxa.juros.faixa.etaria"/></span>
                </h3>
                <div class="row">
                  <div class="form-group col-sm-3">
                    <label for="faixaEtariaInicial"><hl:message
                        key="rotulo.regra.taxa.juros.faixa.etaria.inicial" />&nbsp;</label>
                    <hl:htmlinput classe="Edit form-control" type="text"
                      name="faixaEtariaInicial" di="faixaEtariaInicial"
                      value="<%=!novo && definicaoTaxaJuros.getDtjFaixaEtariaIni() != null ? TextHelper.forHtmlAttribute(definicaoTaxaJuros.getDtjFaixaEtariaIni()) : ""%>"
                      size="32"
                      mask="#D3"                
                      onBlur=" validarIntervalo('faixaEtaria', this);"                
                      others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>" 
                      />
                  </div>
                  <div class="form-group col-sm-3">
                    <label for="faixaEtariaFinal"><hl:message
                        key="rotulo.regra.taxa.juros.faixa.etaria.final" />&nbsp;</label>
                    <hl:htmlinput classe="Edit form-control" type="text"
                      name="faixaEtariaFinal" di="faixaEtariaFinal"
                      value="<%=!novo && definicaoTaxaJuros.getDtjFaixaEtariaFim() != null ? TextHelper.forHtmlAttribute(definicaoTaxaJuros.getDtjFaixaEtariaFim()) : ""%>"
                      size="32"
                      mask="#D3"  
                      onBlur=" validarIntervalo('faixaEtaria', this);"                
                      others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>"
                      />
                  </div>
                </div>
              </fieldset>
          <%} %>
          <% if (ShowFieldHelper.showField(FieldKeysConstants.DEFINICAO_TAXA_JUROS_FAIXA_MARGEM, responsavel)) { %>
              <fieldset>
                <h3 class="legend">
                  <span><hl:message key="rotulo.regra.taxa.juros.faixa.margem"/> (<hl:message key="rotulo.moeda" />)</span>
                </h3>
                <div class="row">
                  <div class="form-group col-sm-3">
                    <label for="faixaMargemInicial"><hl:message
                        key="rotulo.regra.taxa.juros.faixa.margem.inicial" />&nbsp;</label>
                    <hl:htmlinput name="faixaMargemInicial" type="text"
                      classe="Edit form-control"
                      others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>"
                      di="faixaMargemInicial" size="12" mask="#F15"
                      onBlur="if (this.value != '') { if (!validarIntervalo('faixaMargem', this) || !validaNegativo(this)) { return false;} else { this.value = FormataContabil(parse_num(this.value), 2);}  }"
                      value="<%=!novo && definicaoTaxaJuros.getDtjFaixaMargemIni() != null ? NumberHelper.format(definicaoTaxaJuros.getDtjFaixaMargemIni().doubleValue(), NumberHelper.getLang()) : ""%>" />
                  </div>
                  <div class="form-group col-sm-3">
                    <label for="faixaMargemFinal"><hl:message
                        key="rotulo.regra.taxa.juros.faixa.margem.final" />&nbsp;</label>
                    <hl:htmlinput name="faixaMargemFinal" type="text"
                      classe="Edit form-control"
                      others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>"
                      di="faixaMargemFinal" size="12" mask="#F15"
                      onBlur="if (this.value != '') { if (!validarIntervalo('faixaMargem', this) || !validaNegativo(this)) { return false;} else { this.value = FormataContabil(parse_num(this.value), 2);}  }"
                      value="<%=!novo && definicaoTaxaJuros.getDtjFaixaMargemFim() != null ? NumberHelper.format(definicaoTaxaJuros.getDtjFaixaMargemFim().doubleValue(), NumberHelper.getLang()) : ""%>" />
                  </div>
                </div>
              </fieldset>
          <%} %>
          <% if (ShowFieldHelper.showField(FieldKeysConstants.DEFINICAO_TAXA_JUROS_FAIXA_VLR_TOTAL, responsavel)) { %>
              <fieldset>
                <h3 class="legend">
                  <span><hl:message key="rotulo.regra.taxa.juros.faixa.valor.total"/> (<hl:message key="rotulo.moeda" />)</span>
                </h3>
                <div class="row">
                  <div class="form-group col-sm-3">
                    <label for="faixaValorTotalInicial"><hl:message
                        key="rotulo.regra.taxa.juros.faixa.valor.total.inicial" />
                      (<hl:message key="rotulo.moeda" />)</label>
                    <hl:htmlinput name="faixaValorTotalInicial" type="text"
                      classe="Edit form-control"
                      others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>"
                      di="faixaValorTotalInicial" size="12" mask="#F15"
                      onBlur="if (this.value != '') { if (!validarIntervalo('faixaValorTotal', this) || !validaNegativo(this)) { return false;} else { this.value = FormataContabil(parse_num(this.value), 2);}  }"
                      value="<%=!novo && definicaoTaxaJuros.getDtjFaixaValorTotalIni() != null ? NumberHelper.format(definicaoTaxaJuros.getDtjFaixaValorTotalIni().doubleValue(), NumberHelper.getLang()): ""%>" />
                  </div>
                  <div class="form-group col-sm-3">
                    <label for="faixaValorTotalFinal"><hl:message
                        key="rotulo.regra.taxa.juros.faixa.valor.total.final" />
                      (<hl:message key="rotulo.moeda" />)</label>
                    <hl:htmlinput name="faixaValorTotalFinal" type="text"
                      classe="Edit form-control"
                      others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>"
                      di="faixaValorTotalFinal" size="12" mask="#F15"
                      onBlur="if (this.value != '') { if (!validarIntervalo('faixaValorTotal', this) || !validaNegativo(this)) { return false;} else { this.value = FormataContabil(parse_num(this.value), 2);}  }"
                      value="<%=!novo && definicaoTaxaJuros.getDtjFaixaValorTotalFim() != null ? NumberHelper.format(definicaoTaxaJuros.getDtjFaixaValorTotalFim().doubleValue(), NumberHelper.getLang()) : ""%>" />
                  </div>
                </div>
              </fieldset>
          <%} %>
          <% if (ShowFieldHelper.showField(FieldKeysConstants.DEFINICAO_TAXA_JUROS_FAIXA_VLR_CONTRATO, responsavel)) { %>
              <fieldset>
                <h3 class="legend">
                  <span><hl:message key="rotulo.regra.taxa.juros.faixa.valor.contrato"/> (<hl:message key="rotulo.moeda" />)</span>
                </h3>
                <div class="row">
                  <div class="form-group col-sm-3">
                    <label for="faixaValorContratoInicial"><hl:message
                        key="rotulo.regra.taxa.juros.faixa.valor.contrato.inicial" />
                      (<hl:message key="rotulo.moeda" />)</label>
                    <hl:htmlinput name="faixaValorContratoInicial" type="text"
                      classe="Edit form-control"
                      others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>"
                      di="faixaValorContratoInicial" size="12" mask="#F15"
                      onBlur="if (this.value != '') { if (!validarIntervalo('faixaValorContrato', this) || !validaNegativo(this)) { return false;} else { this.value = FormataContabil(parse_num(this.value), 2);}  }"
                      value="<%=!novo && definicaoTaxaJuros.getDtjFaixaValorTotalIni() != null ? NumberHelper.format(definicaoTaxaJuros.getDtjFaixaValorTotalIni().doubleValue(), NumberHelper.getLang()) : ""%>" />
                  </div>
                  <div class="form-group col-sm-3">
                    <label for="faixaValorContratoFinal"><hl:message
                        key="rotulo.regra.taxa.juros.faixa.valor.contrato.final" />
                      (<hl:message key="rotulo.moeda" />)</label>
                    <hl:htmlinput name="faixaValorContratoFinal" type="text"
                      classe="Edit form-control"
                      others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>"
                      di="faixaValorContratoFinal" size="12" mask="#F15"
                      onBlur="if (this.value != '') { if (!validarIntervalo('faixaValorContrato', this) || !validaNegativo(this)) { return false;} else { this.value = FormataContabil(parse_num(this.value), 2);}  }"
                      value="<%=!novo && definicaoTaxaJuros.getDtjFaixaValorTotalFim() != null ? NumberHelper.format(definicaoTaxaJuros.getDtjFaixaValorTotalFim().doubleValue(), NumberHelper.getLang()) : ""%>" />
                  </div>
                </div>
              </fieldset>
          <%} %>
          <fieldset>
            <h3 class="legend">
              <span><hl:message key="rotulo.regra.taxa.juros.faixa.prazo"/></span>
            </h3>
            <div class="row">
              <div class="form-group col-sm-3">
                <label for="faixaPrazoInicial"><hl:message
                    key="rotulo.regra.taxa.juros.faixa.prazo.inicial" />&nbsp;</label>
                <hl:htmlinput name="faixaPrazoInicial" type="text"
                  classe="Edit form-control"
                  others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>"
                  onBlur=" validarIntervalo('faixaPrazo', this);"
                  di="faixaPrazoInicial" size="12" mask="#D3"                
                  value="<%=!novo && definicaoTaxaJuros.getDtjFaixaPrazoIni() != null ? TextHelper.forHtmlAttribute(definicaoTaxaJuros.getDtjFaixaPrazoIni()) : ""%>" />
              </div>
              <div class="form-group col-sm-3">
                <label for="faixaPrazoFinal"><hl:message
                    key="rotulo.regra.taxa.juros.faixa.prazo.final" />&nbsp;</label>
                <hl:htmlinput name="faixaPrazoFinal" type="text"
                  classe="Edit form-control"
                  others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>"
                  onBlur=" validarIntervalo('faixaPrazo', this);"
                  di="faixaPrazoFinal" size="12" mask="#D3"
                  value="<%=!novo && definicaoTaxaJuros.getDtjFaixaPrazoFim() != null ? TextHelper.forHtmlAttribute(definicaoTaxaJuros.getDtjFaixaPrazoFim()) : ""%>" />
              </div>
            </div>
          </fieldset>
          <fieldset>
            <h3 class="legend">
              <span><hl:message key="rotulo.regra.taxa.juros.taxa.juros"/></span>
            </h3>
			<div id="camposTaxaMinMax" style="display: none;">
				<div class="row">
	              <div class="form-group col-sm-3">
	                <label for="taxaJurosMinima"><hl:message key="rotulo.regra.taxa.juros.taxa.juros.minima" />&nbsp;</label>
	                <hl:htmlinput name="taxaJurosMinima" type="text"
	                  classe="Edit form-control"
	                  others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>"
	                  di="taxaJurosMinima" size="12" mask="#F15"
	                  onBlur="if (this.value != '') { if (!validaNegativo(this)) { return false; } else {  this.value = FormataContabil(parse_num(this.value), 2);} }"
	                  value="<%=!novo && definicaoTaxaJuros.getDtjTaxaJurosMinima() != null ? NumberHelper.format(definicaoTaxaJuros.getDtjTaxaJurosMinima().doubleValue(), NumberHelper.getLang()): ""%>" />
	              </div>
	              <div class="form-group col-sm-3">
	                <label for="taxaJurosMaxima"><hl:message key="rotulo.regra.taxa.juros.taxa.juros.maxima" />&nbsp;</label>
	                <hl:htmlinput name="taxaJurosMaxima" type="text"
	                  classe="Edit form-control"
	                  others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>"
	                  di="taxaJurosMaxima" size="12" mask="#F15"
	                  onBlur="if (this.value != '') { if (!validaNegativo(this)) { return false; } else {  this.value = FormataContabil(parse_num(this.value), 2);} }"
	                  value="<%=!novo && definicaoTaxaJuros.getDtjTaxaJuros() != null ? NumberHelper.format(definicaoTaxaJuros.getDtjTaxaJuros().doubleValue(), NumberHelper.getLang()): ""%>" />
	              </div>
	            </div>
			</div>
			<div id="campoTaxaUnica">
				<div class="row">
	              <div class="form-group col-sm-3">
	                <label for="taxaJuros"><hl:message key="rotulo.regra.taxa.juros.taxa.juros" />&nbsp;</label>
	                <hl:htmlinput name="taxaJuros" type="text"
	                  classe="Edit form-control"
	                  others="<%=podeEditar ? \"\" : \"disabled='disabled'\"%>"
	                  di="taxaJuros" size="12" mask="#F15"
	                  onBlur="if (this.value != '') { if (!validaNegativo(this)) { return false; } else {  this.value = FormataContabil(parse_num(this.value), 2);} }"
	                  value="<%=!novo && definicaoTaxaJuros.getDtjTaxaJuros() != null ? NumberHelper.format(definicaoTaxaJuros.getDtjTaxaJuros().doubleValue(), NumberHelper.getLang()): ""%>" />
	              </div>
	            </div>
			</div>
          </fieldset>
        </div>
      </div>
      <div class="btn-action col-sm">
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/editarRegraTaxaJuros?acao=iniciar&_skip_history_=true&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;"><hl:message key="rotulo.botao.voltar" /></a>
        <% if(podeEditar) { %>
        <a class="btn btn-primary" href="#no-back" onClick="salvar(); return false;"><hl:message key="rotulo.botao.salvar" /></a> &nbsp;&nbsp;&nbsp;
        <% } %>
      </div>
  </form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">

function formLoad() {
	f0 = document.forms[0];
	verificarExibicaoCamposTaxaJuros();
}

window.onload = formLoad;

function deveExibirTaxaMinMax() {
	const funCodigo = document.getElementById("funCodigo").value;
    const funcoesPermitidas = ["<%= CodedValues.FUN_SIM_CONSIGNACAO %>", "<%= CodedValues.FUN_SIMULAR_RENEGOCIACAO %>", "<%= CodedValues.FUN_SOLICITAR_PORTABILIDADE %>"];

	return <%= exibeCETMinMax %> && funcoesPermitidas.includes(funCodigo);
}


function verificarExibicaoCamposTaxaJuros() {
    const exibiTaxaMinMax = deveExibirTaxaMinMax();

    document.getElementById("camposTaxaMinMax").style.display = exibiTaxaMinMax ? "block" : "none";
    document.getElementById("campoTaxaUnica").style.display = exibiTaxaMinMax ? "none" : "block";
}

async function salvar() {
	//Campos obrigatórios
    var idValidacaoObrigatorios = new Array("svcCodigo","faixaPrazoInicial", "faixaPrazoFinal");

    //Mensagens de erro
    var Msgs = new Array('<hl:message key="mensagem.preenchimento.regra.taxa.juros.servico"/>',
            '<hl:message key="mensagem.preenchimento.regra.taxa.juros.prazoIni"/>',
            '<hl:message key="mensagem.preenchimento.regra.taxa.juros.prazoFim"/>');

	const exibiTaxaMinMax = deveExibirTaxaMinMax();
	if (exibiTaxaMinMax) {
        idValidacaoObrigatorios.push("taxaJurosMinima");
		idValidacaoObrigatorios.push("taxaJurosMaxima");
        Msgs.push('<hl:message key="mensagem.preenchimento.regra.taxa.juros.taxaJurosMinima"/>');
		Msgs.push('<hl:message key="mensagem.preenchimento.regra.taxa.juros.taxaJurosMaxima"/>');
		document.forms[0].taxaJuros.value = document.forms[0].taxaJurosMaxima.value;
    } else {
		idValidacaoObrigatorios.push("taxaJuros");
		Msgs.push('<hl:message key="mensagem.preenchimento.regra.taxa.juros.taxaJuros"/>');
	}

    if(ValidaCamposV4(idValidacaoObrigatorios, Msgs)) {
		<% if (aplicarRegraCETTaxaJuros && temLimiteTaxa) { %>
			let limiteValidado = await validaLimiteTaxaJuros();
			if(!limiteValidado){
				f0.submit();
		}
		<% } else { %>
			f0.submit();
		<% } %>
    }
}

//Valida valor negativo depois de finalizar o preenchimento do campo
function validaNegativo(campo) {
    let valor = campo.value;
    if (valor < 0) 
    {
      alert("<hl:message key='rotulo.regra.taxa.juros.validacao.negativo'/>");
      campo.value = '';
      campo.focus();
      return false;
    }

    return true;
}

function validarIntervalo(idCampo, campo) {
    let campoInicial = document.getElementById(idCampo + 'Inicial');
    let faixaInicio = campoInicial.value;
    let faixaFim = document.getElementById(idCampo + 'Final').value;

    if ((faixaInicio != '' && faixaInicio != null) && (faixaFim != '' && faixaFim != null)) {
    	faixaInicio = faixaInicio.replace(',', '.');
    	faixaFim = faixaFim.replace(',', '.');                    
        if (Number(faixaInicio) >= Number(faixaFim)) {
        	alert("<hl:message key='rotulo.regra.taxa.juros.validacao.final.inicial'/>");                    	
            if (campo != undefined && campo != null) {
            	campo.value = '';
                campo.focus();
                return false;
            } else {
             	document.getElementById(idCampo + 'Inicial').value = '';                    	
        	    campoInicial.focus();
                return false;
            }
        }
    }

    return true;                
}

<% if (aplicarRegraCETTaxaJuros && temLimiteTaxa) { %>

async function validaLimiteTaxaJuros(){
	$("#limiteExcedido").remove();
	let taxaJuros = $("#taxaJuros").val().replace(/,(?=[^,]*$)/, '.');
	let faixaPrazoInicial = $("#faixaPrazoInicial").val();
	let faixaPrazoFinal = $("#faixaPrazoFinal").val();
	 <%if (novo && podeEditar) {%>
	 let servicosSelecionados = $("#svcCodigo").val();
	 servicosSelecionados = servicosSelecionados.filter(Boolean);
	<% } else { %>
	 	let servicosSelecionados = [];
		servicosSelecionados[0] = $("#svcCodigo").val();
	<% } %>
	let limite = null;
	let html = '<div id="limiteExcedido" class="alert alert-danger mb-1" role="alert"><p class="mb-0"><hl:message key="mensagem.erro.taxa.juros.limite.excedido"/></p>';
	let limiteExcedido = false;
	
	for(servico of servicosSelecionados){
		try{
			svcCodigo = servico.split(";");
			limite = await getLimite(taxaJuros, faixaPrazoInicial, faixaPrazoFinal, svcCodigo[0]);
			limite = JSON.parse(limite);
			if(limite.length > 0){
				limiteExcedido = true;
				html = html + '<br><p class="mb-0"><hl:message key="mensagem.informacao.rotulo.servico"/> : ' +  svcCodigo[1] + '</p>';

				limite.forEach(item => {
					  const prazoRef = item['tb_limite_taxa_juros.ltj_prazo_ref'];
					  const jurosMax = item['tb_limite_taxa_juros.ltj_juros_max'].toString().replace('.', ',');

					  html += '<p class="mb-0"><hl:message key="mensagem.informacao.taxa.juros.limite.maximo.item" arg0="' + jurosMax + '" arg1="' + prazoRef + '"/></p>';
					});
			}
		}catch(error){
			html = '<div id="limiteExcedido" class="alert alert-danger mb-1" role="alert"><p class="mb-0"><hl:message key="mensagem.erro.taxa.juros.limite.excedido.erro"/></p></div>';
			$(".main-content").prepend(html);
			return true;
		}
	}
	
	html = html + '<br><p class="mb-0"><hl:message key="mensagem.erro.taxa.juros.limite.excedido.alterar"/></p></div>';
	
	if(limiteExcedido){
		$(".main-content").prepend(html);
		return limiteExcedido;
	}else{
		return limiteExcedido;
	}
}

function getLimite(taxaJuros, faixaPrazoInicial, faixaPrazoFinal, svcCodigo){
	var request = $.ajax({
		type: 'POST',
	    url: '../v3/editarRegraTaxaJuros/getTaxaLimite?<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&_skip_history_=true',
	    data: {
	    	'taxaJuros': taxaJuros,
	    	'faixaPrazoInicial' : faixaPrazoInicial,
	    	'faixaPrazoFinal' : faixaPrazoFinal,
	    	'svcCodigo' : svcCodigo
		    }
	});
	
	return request;
}

function exibeLimiteTaxaJuros(){
	$("#temLimiteTaxa").remove();
	$("#limiteExcedido").remove();
	<%if (novo && podeEditar) {%>
		let servicosSelecionados = $("#svcCodigo").val();
		servicosSelecionados = servicosSelecionados.filter(Boolean);
		if(servicosSelecionados.length > 0){
	<% } else { %>
	 	let servicosSelecionados = [];
		servicosSelecionados[0] = $("#svcCodigo").val();
		if(servicosSelecionados[0].length > 0){
	<% } %>
		var html = '<div id="temLimiteTaxa" class="alert alert-warning mb-1" role="alert"><p class="mb-0"><hl:message key="mensagem.informacao.taxa.juros.limite.maximo"/>:</p>';

		$.each(servicosSelecionados, function(index, element){
			servico = element.split(";");
			var possuiServico = false;
			html = html + '<br><p class="mb-0"><hl:message key="mensagem.informacao.rotulo.servico"/> : ' +  servico[1] + '</p>';
			<%for (TransferObject ctoLimiteTaxa : limitesTaxa) {
			    String przLimiteTaxa = ctoLimiteTaxa.getAttribute(Columns.LTJ_PRAZO_REF).toString();
	            String vlrLimiteTaxa = ctoLimiteTaxa.getAttribute(Columns.LTJ_JUROS_MAX).toString();
	            String svcDescricao = ctoLimiteTaxa.getAttribute(Columns.SVC_DESCRICAO).toString();
			%>
				if(servico[0] == '<%=ctoLimiteTaxa.getAttribute(Columns.LTJ_SVC_CODIGO)%>'){
					html = html + '<p class="mb-0"><%=ApplicationResourcesHelper.getMessage("mensagem.informacao.taxa.juros.limite.maximo.item",
							responsavel, NumberHelper.reformat(vlrLimiteTaxa, "en", NumberHelper.getLang(), 2, 4), przLimiteTaxa)%></p>';
					possuiServico = true;
				}
			<%}%>
			
			if(!possuiServico){
				html = html + '<p class="mb-0"><hl:message key="mensagem.informacao.taxa.juros.sem.cadastro"/></p>';
			}
		});
		
		html = html + "</div>";
		$(".main-content").prepend(html);
	}
}

$('#svcCodigo').change(function() {
	exibeLimiteTaxaJuros();
});

$(document).ready(function() {
	exibeLimiteTaxaJuros();
});
<% } %>
</script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
