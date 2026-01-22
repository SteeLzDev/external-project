<%--
* <p>Title: aplicarReajusteCalculoBeneficio_v4</p>
* <p>Description: Aplicar reajuste - Calculo benefício</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: larissa.silva $
* $Revision: 24740 $
* $Date: 2018-07-17 00:00:00 -0300 (Qua, 17 jul 2018) $
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

//Pega dados vindo do webController
List calculoBeneficios = (List) request.getAttribute("listaCalculoBeneficio");

List orgaos = (List) request.getAttribute("orgaos");
List beneficios = (List) request.getAttribute("beneficios");
List tipoBeneficiarios = (List) request.getAttribute("tipoBeneficiarios");
List grauParentesco = (List) request.getAttribute("grauParentesco");
List motivoDependencia = (List) request.getAttribute("motivoDependencia");

%>
<c:set var="javascript">
  <script type="text/JavaScript">

    f0 = document.getElementById('form1');
    f1 = document.getElementById('form2');
  
    function filtrar() {
  		f0.submit();
  	}

    function validaCamposObrigatorios() {
        if (typeof document.getElementById("valorReajuste").value == "undefined" || document.getElementById("valorReajuste").value == 0 || document.getElementById("valorReajuste").value == "") {
          alert('<hl:message key="mensagem.beneficio.reajuste.valor.informar"/>');
          return false;
        }             
        if (!$("#aplicarSobreBeneficio").is(":checked") && !$("#aplicarSobreSubsidio").is(":checked") && !$("#aplicarSobreFaixaSalarial").is(":checked")) {
           alert('<hl:message key="mensagem.beneficio.reajuste.campos.valores.informar"/>');
           return false;
        }
        return true;
    }
    function confirmarReajuste() {
    	if (validaCamposObrigatorios()) {
			// valida se marcou pelo menos um item da tabela de cálculo de benefício    		
            var tam = document.getElementsByName('chkAplicarReajuste').length;
            if (tam == undefined) {
                alert('<%=ApplicationResourcesHelper.getMessage("mensagem.beneficio.reajuste.calculo.beneficio.informar", responsavel)%>');
                return false;
            } else {
              var qtd = 0;
              for(var i = 0; i < tam; i++) {
                if (document.getElementsByName('chkAplicarReajuste')[i].checked == true) {
                  qtd++
                }
              }
              if (qtd <= 0) {
                  alert('<%=ApplicationResourcesHelper.getMessage("mensagem.beneficio.reajuste.calculo.beneficio.informar", responsavel)%>');
                  return false;
              }
            }
    		
    		if (confirm('<hl:message key="mensagem.confirmacao.reajuste.tabela.calculo.beneficio"/>')) {
                f1.submit();
                return true;
        	}
    	} 
    	return false;
    }

    function confirmarReajusteTodos(){
    	if (validaCamposObrigatorios()) {
            if (confirm('<hl:message key="mensagem.confirmacao.reajuste.tabela.calculo.beneficio"/>')) {
            	f1.APLICAR_REAJUSTE_TODOS.value = "TODOS";
            	var orgCodigo = f0.ORG_CODIGO.value;
            	var benCodigo = f0.BEN_CODIGO.value;
            	var tibCodigo = f0.TIB_CODIGO.value;
            	var grpCodigo = f0.GRP_CODIGO.value;
            	var mdeCodigo = f0.MDE_CODIGO.value;
                f1.action = "../v3/alterarCalculoBeneficio?acao=salvarReajuste&ORG_CODIGO="+orgCodigo+"&BEN_CODIGO="+benCodigo+"&TIB_CODIGO="+tibCodigo+"&GRP_CODIGO="+grpCodigo+"&MDE_CODIGO="+mdeCodigo+"&<%=SynchronizerToken.generateToken4URL(request)%>";
                f1.submit();
            	return true;
            }
    	}
        return false;
    }

  </script>
</c:set>
<c:set var="title">
  <hl:message key="rotulo.calculo.beneficio.reajuste"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm">
      <form action="../v3/alterarCalculoBeneficio" method="post" id="form1" name="formPesquisar">
        <%=SynchronizerToken.generateHtmlToken(request)%>
      	<div class="card">
          <div class="card-header">
            <hl:message key="mensagem.pesquisa.informe.dados" />
          </div>
          <div class="card-body">
          <input type="hidden" name="acao" value="novoReajuste"/>
            <div class="row">
              <div class="form-group col-sm">
                <label for=""><hl:message key="rotulo.orgao.singular"/></label>
                <%=JspHelper.geraCombo(orgaos, "ORG_CODIGO", Columns.ORG_CODIGO, Columns.ORG_NOME, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, JspHelper.verificaVarQryStr(request, "ORG_CODIGO"), null, false, "form-control")%>
              </div>
              <div class="form-group col-sm">
                <label for=""><hl:message key="rotulo.beneficio.singular"/></label>
                <%=JspHelper.geraCombo(beneficios, "BEN_CODIGO", Columns.BEN_CODIGO, Columns.BEN_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, JspHelper.verificaVarQryStr(request, "BEN_CODIGO"), null, false, "form-control")%>
              </div>
              <div class="form-group col-sm">
                <label for=""><hl:message key="rotulo.calculo.beneficio.tipo.beneficiario"/></label>
                <%=JspHelper.geraCombo(tipoBeneficiarios, "TIB_CODIGO", Columns.TIB_CODIGO, Columns.TIB_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, JspHelper.verificaVarQryStr(request, "TIB_CODIGO"), null, false, "form-control")%>
              </div>
              <div class="form-group col-sm">
                <label for=""><hl:message key="rotulo.calculo.beneficio.grau.parentesco"/></label>
                <%=JspHelper.geraCombo(grauParentesco, "GRP_CODIGO", Columns.GRP_CODIGO, Columns.GRP_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, JspHelper.verificaVarQryStr(request, "GRP_CODIGO"), null, false, "form-control")%>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-3">
                <label for=""><hl:message key="rotulo.beneficiario.motivo.dependencia"/></label>
                <%=JspHelper.geraCombo(motivoDependencia, "MDE_CODIGO", Columns.MDE_CODIGO, Columns.MDE_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, JspHelper.verificaVarQryStr(request, "MDE_CODIGO"), null, false, "form-control")%>
              </div>
            </div>
          </div>
        </div>
        <div class="btn-action d-print-none">
          <a class="btn btn-primary" href="#no-back" name="Filtrar" id="Filtrar" onClick="filtrar()" alt="<hl:message key="rotulo.botao.pesquisar"/>" title="<hl:message key="rotulo.botao.pesquisar"/>"> 
            <svg width="20"><use xlink:href="#i-consultar"></use></svg> <hl:message key="rotulo.botao.pesquisar" />
          </a>
        </div>
      </form>
    </div>
  </div>
  <form action="../v3/alterarCalculoBeneficio" method="post" id="form2" name="salvarReajuste">
  <%=SynchronizerToken.generateHtmlToken(request)%>
    <div class="row">
      <div class="col-sm">
        <div class="card">
          <div class="card-header">
            <hl:message key="mensagem.informe.aplicar.reajuste"/>
          </div>
          <div class="card-body">
            <input type="hidden" name="acao" value="salvarReajuste"/>
            <div class="row">
              <div class="form-group col-sm">
                <label for=""><hl:message key="rotulo.reajuste.porcentagem.singular"/></label>
                <hl:htmlinput classe="Edit form-control" name="valorReajuste" 
                type="text" di="valorReajuste" size="10" mask="#F11" onBlur="SetarEventoMascaraV4(this,'#F11',true); if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                onFocus="SetarEventoMascaraV4(this,'#F11',true);"/>
              </div>
              <div class="form-group col-sm">
                <label for=""><hl:message key="rotulo.reajuste.aplicar.sobre"/></label>
                <div class="form-check mt-2">
                  <input type="checkbox" id="aplicarSobreBeneficio" class="form-check-input ml-1" name="aplicarSobreBeneficio" value="true" /><label class="form-check-label"><hl:message key="rotulo.calculo.beneficio.valor.beneficio"/></label>
                  <input type="checkbox" id="aplicarSobreSubsidio" class="form-check-input ml-1" name="aplicarSobreSubsidio" value="true" /><label class="form-check-label"><hl:message key="rotulo.calculo.beneficio.valor.subsidio"/></label>
                  <input type="checkbox" id="aplicarSobreFaixaSalarial" class="form-check-input ml-1" name="aplicarSobreFaixaSalarial" value="true" /><label class="form-check-label"><hl:message key="rotulo.calculo.beneficio.faixa.salarial"/></label>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="row">
      <div class="col-sm">
        <div class="card">
          <div class="card-header">
            <hl:message key="rotulo.calculo.beneficio.titulo" />
          </div>
          <div class="card-body table-responsive">
            <table class="table table-striped table-hover">
              <thead>
                <tr>
                  <th><hl:message key="rotulo.calculo.beneficio.orgao"/></th>
                  <th><hl:message key="rotulo.calculo.beneficio.descricao"/></th>
                  <th><hl:message key="rotulo.calculo.beneficio.tipo.beneficiario"/></th>
                  <th><hl:message key="rotulo.calculo.beneficio.grau.parentesco"/></th>
                  <th><hl:message key="rotulo.beneficiario.motivo.dependencia"/></th>
                  <th><hl:message key="rotulo.calculo.beneficio.inicio.faixa.salarial"/></th>
                  <th><hl:message key="rotulo.calculo.beneficio.fim.faixa.salarial"/></th>
                  <th><hl:message key="rotulo.calculo.beneficio.inicio.faixa.etaria"/></th>
                  <th><hl:message key="rotulo.calculo.beneficio.fim.faixa.etaria"/></th>
                  <th><hl:message key="rotulo.calculo.beneficio.valor.beneficio"/></th>
                  <th><hl:message key="rotulo.calculo.beneficio.valor.subsidio"/></th>
                  <th><input onclick="if (this.checked) {checkAll(f1, 'chkAplicarReajuste');} else {uncheckAll(f1, 'chkAplicarReajuste')}" type="checkbox" id="selectAll"></th>
                </tr>
              </thead>
              <tbody>
                <%=JspHelper.msgRstVazio(calculoBeneficios.size()==0, "13", "lp")%>
                <%
                Iterator it = calculoBeneficios.iterator();
                while (it.hasNext()) {
                  CustomTransferObject clb = (CustomTransferObject)it.next();
                  String clbCodigo = (String)clb.getAttribute(Columns.CLB_CODIGO);
                  Date dataAux = (Date) clb.getAttribute(Columns.CLB_VIGENCIA_INI);
                  String clbVigenciaIni = null;
                  if(!TextHelper.isNull(dataAux)){
                      clbVigenciaIni = DateHelper.reformat(dataAux.toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern());
                  }
                  Date dataAux2 = (Date) clb.getAttribute(Columns.CLB_VIGENCIA_FIM);
                  String clbVigenciaFim = null;
                  if(!TextHelper.isNull(dataAux2)){
                      clbVigenciaFim = DateHelper.reformat(dataAux2.toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern());    
                  }
                   String orgNome = (String)clb.getAttribute(Columns.ORG_NOME);
                  String benDescricao = (String)clb.getAttribute(Columns.BEN_DESCRICAO);
                  String tibDescricao = (String)clb.getAttribute(Columns.TIB_DESCRICAO);
                  String grpParentesco = (String)clb.getAttribute(Columns.GRP_DESCRICAO);
                  String mdeDescricao = (String)clb.getAttribute(Columns.MDE_DESCRICAO);
                  
                  String clbFaixaSalarialIni = null;
                  
                  if(!TextHelper.isNull(clb.getAttribute(Columns.CLB_FAIXA_SALARIAL_INI))){
                     clbFaixaSalarialIni = NumberHelper.reformat(clb.getAttribute(Columns.CLB_FAIXA_SALARIAL_INI).toString(), "en", NumberHelper.getLang(), 2, 20);
                  }
                  
                  String clbFaixaSalarialFim = null;
                  
                  if(!TextHelper.isNull(clb.getAttribute(Columns.CLB_FAIXA_SALARIAL_FIM))){
                      clbFaixaSalarialFim = NumberHelper.reformat(clb.getAttribute(Columns.CLB_FAIXA_SALARIAL_FIM).toString(), "en", NumberHelper.getLang(), 2, 20);
                  }
                  
                  String clbFaixaEtariaIni = null;
                  
                  if(!TextHelper.isNull(clb.getAttribute(Columns.CLB_FAIXA_ETARIA_INI))){
                      clbFaixaEtariaIni = clb.getAttribute(Columns.CLB_FAIXA_ETARIA_INI).toString();
                  }
                  
                  String clbFaixaEtariaFim = null;
                  
                  if(!TextHelper.isNull(clb.getAttribute(Columns.CLB_FAIXA_ETARIA_FIM))){
                      clbFaixaEtariaFim = clb.getAttribute(Columns.CLB_FAIXA_ETARIA_FIM).toString();
                  }
                  
                  String clbValorMensalidade = null;
                  
                  if(!TextHelper.isNull(clb.getAttribute(Columns.CLB_VALOR_MENSALIDADE))){
                      clbValorMensalidade = NumberHelper.reformat(clb.getAttribute(Columns.CLB_VALOR_MENSALIDADE).toString(), "en", NumberHelper.getLang(), 2, 20);
                  }
                 
                  String clbValorSubsidio = null;
                  
                  if(!TextHelper.isNull(clb.getAttribute(Columns.CLB_VALOR_SUBSIDIO))){
                      clbValorSubsidio = NumberHelper.reformat(clb.getAttribute(Columns.CLB_VALOR_SUBSIDIO).toString(), "en", NumberHelper.getLang(), 2, 20);
                  }
                %>
                <tr>
                  <td><%=!TextHelper.isNull(orgNome) ? TextHelper.forHtmlContent(orgNome): ""%></td>
                  <td><%=!TextHelper.isNull(benDescricao) ? TextHelper.forHtmlContent(benDescricao): ""%></td>
                  <td><%=!TextHelper.isNull(tibDescricao) ? TextHelper.forHtmlContent(tibDescricao): ""%></td>
                  <td><%=!TextHelper.isNull(grpParentesco) ? TextHelper.forHtmlContent(grpParentesco): ""%></td>
                  <td><%=!TextHelper.isNull(mdeDescricao) ? TextHelper.forHtmlContent(mdeDescricao): ""%></td>
                  <td><%=!TextHelper.isNull(clbFaixaSalarialIni) ? TextHelper.forHtmlContent(clbFaixaSalarialIni): ""%></td>
                  <td><%=!TextHelper.isNull(clbFaixaSalarialFim) ? TextHelper.forHtmlContent(clbFaixaSalarialFim): ""%></td>
                  <td><%=!TextHelper.isNull(clbFaixaEtariaIni) ? TextHelper.forHtmlContent(clbFaixaEtariaIni): ""%></td>
                  <td><%=!TextHelper.isNull(clbFaixaEtariaFim) ? TextHelper.forHtmlContent(clbFaixaEtariaFim): ""%></td>
                  <td><%=!TextHelper.isNull(clbValorMensalidade) ? TextHelper.forHtmlContent(clbValorMensalidade): ""%></td>
                  <td><%=!TextHelper.isNull(clbValorSubsidio) ? TextHelper.forHtmlContent(clbValorSubsidio): ""%></td>
                  <td align="center"><input type="checkbox" name="chkAplicarReajuste" id="chkAplicarReajuste" value="<%=clb.getAttribute(Columns.CLB_CODIGO)%>"></td>
                </tr>
                <%
                }
                %>
              </tbody>
            </table>  
            <input type="hidden" name="APLICAR_REAJUSTE_TODOS" value="">
          </div>
          <div class="card-footer">
            <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
          </div>
        </div>
      </div>
    </div>
    <div class="btn-action">
      <a name="Button" onClick="postData('../v3/consultarCalculoBeneficio?acao=consultar&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>'); return false;" class="btn btn-outline-danger" href="#no-back" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
      <a href="#no-back" class="btn btn-primary" name="submitTodos" onClick="confirmarReajusteTodos(); return false;"><hl:message key="rotulo.botao.aplicar.reajuste.todos" /></a>
      <a href="#no-back" class="btn btn-primary" name="submit" onClick="confirmarReajuste(); return false;"><hl:message key="rotulo.botao.aplicar.reajuste" /></a>
    </div>
  </form>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
  <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>