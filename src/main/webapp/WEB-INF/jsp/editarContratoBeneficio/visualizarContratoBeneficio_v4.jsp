<%--
* <p>Title: visualizarContratoBeneficio_v4.jsp</p>
* <p>Description: Simular Beneficios v4</p>
* <p>Copyright: Copyright (c) 2018</p>
* <p>Company: Nostrum Consultoria e Projetos</p>
* $Author$
* $Revision$
* $Date$
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session"
  class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  TransferObject contratoBeneficio = (TransferObject) request.getAttribute("contratoBeneficio");
  boolean funEditarContratoBeneficioAvancado = (boolean) request.getAttribute("funEditarContratoBeneficioAvancado");
  boolean funEditarContratoBeneficio = (boolean) request.getAttribute("funEditarContratoBeneficio");
  String mascaraNumeroContratoBeneficio = (String) request.getAttribute("tpsMascaraNumeroContratoBeneficio");
  
  String benContribuiuPlano = (String) contratoBeneficio.getAttribute(Columns.DAD_VALOR + CodedValues.TDA_BENEFICIARIO_CONTRIBUIU_PARA_PLANO);
  String benAdesaoPlanoExFuncionario = (String) contratoBeneficio.getAttribute(Columns.DAD_VALOR + CodedValues.TDA_BEN_ADESAO_PLANO_EX_FUNCIONARIO);
  String cso = ApplicationResourcesHelper.getMessage("rotulo.contrato.beneficio.contribuiu.para.plano.opcao.cso", responsavel);
  String csa = ApplicationResourcesHelper.getMessage("rotulo.contrato.beneficio.contribuiu.para.plano.opcao.csa", responsavel);
  String cao = ApplicationResourcesHelper.getMessage("rotulo.contrato.beneficio.contribuiu.para.plano.opcao.cao", responsavel);
  String nao = ApplicationResourcesHelper.getMessage("rotulo.contrato.beneficio.contribuiu.para.plano.opcao.nao", responsavel);

%>
<c:set var="title">
  <hl:message key="rotulo.contrato.beneficio.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent">

  <div class="row">
      <div class="col-sm">
          <div class="card">
              <div class="card-header">
                  <h2 class="card-header-title"><%=TextHelper.forHtmlContent(contratoBeneficio.getAttribute(Columns.NSE_DESCRICAO)).toUpperCase()%></h2>
              </div>
              <div class="card-body">
                  <dl class="row data-list">
                      <dt class="col-6"><hl:message key="rotulo.contrato.beneficio.matricula.titular" />: </dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(contratoBeneficio.getAttribute(Columns.RSE_MATRICULA))%></dd>
                      <dt class="col-6"><hl:message key="rotulo.beneficio.codigo.plano" />: </dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(contratoBeneficio.getAttribute(Columns.BEN_CODIGO_PLANO))%></dd>
                      <dt class="col-6"><hl:message key="rotulo.relacao.beneficios.descricao"/>: </dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(contratoBeneficio.getAttribute(Columns.BEN_DESCRICAO))%></dd>
                      <dt class="col-6"><hl:message key="rotulo.relacao.beneficios.operadora"/>:</dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(contratoBeneficio.getAttribute(Columns.CSA_NOME))%></dd>
                      <dt class="col-6"><hl:message key="rotulo.beneficio.codigo.contrato" />: </dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(contratoBeneficio.getAttribute(Columns.BEN_CODIGO_CONTRATO))%></dd>
                      <dt class="col-6"><hl:message key="rotulo.beneficio.codigo.registro" />: </dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(contratoBeneficio.getAttribute(Columns.BEN_CODIGO_REGISTRO))%></dd>
                      <dt class="col-6"><hl:message key="rotulo.relacao.beneficios.situacao.contrato" />: </dt>
                      <dd class="col-6"><%=TextHelper.forHtmlContent(contratoBeneficio.getAttribute(Columns.SCB_DESCRICAO))%></dd>
                  </dl>
              </div>
          </div>
      </div>
  </div>
  
  <form method="POST"
    action="../v3/alterarContratoBeneficio?acao=salvar&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>"
    name="form1" id="form1">
    <hl:htmlinput type="hidden" name="<%=TextHelper.forHtmlAttribute(Columns.getColumnName(Columns.CBE_CODIGO))%>" value="<%=TextHelper.forHtmlAttribute(contratoBeneficio.getAttribute(Columns.CBE_CODIGO))%>" />
    <div class="card">
      <div class="card-header"><h2 class="card-header-title"><%=TextHelper.forHtmlContent(contratoBeneficio.getAttribute(Columns.BFC_NOME)).toUpperCase()%></h2></div>
      <div class="card-body">
        <div class="row">
          <div class="col-sm">

            <div class="row">
              <div class="form-group col-sm-4">
                <label for="<%=Columns.getColumnName(Columns.CBE_NUMERO)%>"> <hl:message
                    key="rotulo.contrato.beneficio.numero.contrato" />
                </label>
                <hl:htmlinput name="<%=Columns.getColumnName(Columns.CBE_NUMERO)%>"
                  di="<%=Columns.getColumnName(Columns.CBE_NUMERO)%>" type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(
						contratoBeneficio != null && contratoBeneficio.getAttribute(Columns.CBE_NUMERO) != null
								? contratoBeneficio.getAttribute(Columns.CBE_NUMERO).toString()
								: " ")%>"
                  size="41" mask="<%=mascaraNumeroContratoBeneficio%>"
                  others='<%=!funEditarContratoBeneficioAvancado ? "disabled" : ""%>' />
              </div>

              <div class="form-group col-sm-4">
                <label for="<%=Columns.getColumnName(Columns.CBE_VALOR_TOTAL)%>"> <hl:message
                    key="rotulo.contrato.beneficio.valor.total" />
                </label>
                <hl:htmlinput name="<%=Columns.getColumnName(Columns.CBE_VALOR_TOTAL)%>"
                  di="<%=Columns.getColumnName(Columns.CBE_VALOR_TOTAL)%>" type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(
						contratoBeneficio != null && contratoBeneficio.getAttribute(Columns.CBE_VALOR_TOTAL) != null
								? NumberHelper
										.format(((BigDecimal) contratoBeneficio.getAttribute(Columns.CBE_VALOR_TOTAL))
												.doubleValue(), NumberHelper.getLang())
								: "0")%>"
                  size="8" mask="#F11"
                  onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                  others='disabled' />
              </div>

              <div class="form-group col-sm-4">
                <label for="<%=Columns.getColumnName(Columns.CBE_VALOR_SUBSIDIO)%>"> <hl:message
                    key="rotulo.contrato.beneficio.valor.subsidio" />
                </label>
                <hl:htmlinput name="<%=Columns.getColumnName(Columns.CBE_VALOR_SUBSIDIO)%>"
                  di="<%=Columns.getColumnName(Columns.CBE_VALOR_SUBSIDIO)%>" type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(
						contratoBeneficio != null && contratoBeneficio.getAttribute(Columns.CBE_VALOR_SUBSIDIO) != null
								? NumberHelper.format(
										((BigDecimal) contratoBeneficio.getAttribute(Columns.CBE_VALOR_SUBSIDIO))
												.doubleValue(),
										NumberHelper.getLang())
								: "0")%>"
                  size="8" mask="#F11"
                  onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                  others='disabled' />
              </div>
            </div>

            <div class="row">
              <div class="form-group col-sm-3">
                <label for="<%=Columns.getColumnName(Columns.CBE_DATA_INCLUSAO)%>"> <hl:message
                    key="rotulo.contrato.beneficio.data.inclusao" />
                </label>
                <hl:htmlinput name="<%=Columns.getColumnName(Columns.CBE_DATA_INCLUSAO)%>"
                  di="<%=Columns.getColumnName(Columns.CBE_DATA_INCLUSAO)%>" type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(
						contratoBeneficio != null && contratoBeneficio.getAttribute(Columns.CBE_DATA_INCLUSAO) != null
								? DateHelper.format(((Date) contratoBeneficio.getAttribute(Columns.CBE_DATA_INCLUSAO)),
										LocaleHelper.getDatePattern())
								: " ")%>"
                  size="10"
                  mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                  others='disabled' />
              </div>

              <div class="form-group col-sm-3">
                <label for="<%=Columns.getColumnName(Columns.CBE_DATA_INICIO_VIGENCIA)%>">
                  <hl:message
                    key="rotulo.contrato.beneficio.data.inicio.vigencia" />
                </label>
                <hl:htmlinput
                  name="<%=Columns.getColumnName(Columns.CBE_DATA_INICIO_VIGENCIA)%>"
                  di="<%=Columns.getColumnName(Columns.CBE_DATA_INICIO_VIGENCIA)%>" type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(contratoBeneficio != null
						&& contratoBeneficio.getAttribute(Columns.CBE_DATA_INICIO_VIGENCIA) != null
								? DateHelper.format(
										((Date) contratoBeneficio.getAttribute(Columns.CBE_DATA_INICIO_VIGENCIA)),
										LocaleHelper.getDatePattern())
								: "")%>"
                  size="10"
                  mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                  others='<%=!funEditarContratoBeneficioAvancado ? "disabled" : ""%>' />
              </div>

              <div class="form-group col-sm-3">
                <label for="<%=Columns.getColumnName(Columns.CBE_DATA_FIM_VIGENCIA)%>">
                  <hl:message
                    key="rotulo.contrato.beneficio.data.fim.vigencia" />
                </label>
                <hl:htmlinput name="<%=Columns.getColumnName(Columns.CBE_DATA_FIM_VIGENCIA)%>"
                  di="<%=Columns.getColumnName(Columns.CBE_DATA_FIM_VIGENCIA)%>" type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(contratoBeneficio != null
						&& contratoBeneficio.getAttribute(Columns.CBE_DATA_FIM_VIGENCIA) != null
								? DateHelper.format(
										((Date) contratoBeneficio.getAttribute(Columns.CBE_DATA_FIM_VIGENCIA)),
										LocaleHelper.getDatePattern())
								: "")%>"
                  size="10"
                  mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                  others='<%=!funEditarContratoBeneficioAvancado ? "disabled" : ""%>' />
              </div>

              <div class="form-group col-sm-3">
                <label for="<%=Columns.getColumnName(Columns.CBE_DATA_CANCELAMENTO)%>">
                  <hl:message
                    key="rotulo.contrato.beneficio.data.cancelamento" />
                </label>
                <hl:htmlinput name="<%=Columns.getColumnName(Columns.CBE_DATA_CANCELAMENTO)%>"
                  di="<%=Columns.getColumnName(Columns.CBE_DATA_CANCELAMENTO)%>" type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(contratoBeneficio != null
						&& contratoBeneficio.getAttribute(Columns.CBE_DATA_CANCELAMENTO) != null
								? DateHelper.format(
										((Date) contratoBeneficio.getAttribute(Columns.CBE_DATA_CANCELAMENTO)),
										LocaleHelper.getDatePattern())
								: "")%>"
                  size="10"
                  mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                  others='<%=!funEditarContratoBeneficioAvancado ? "disabled" : ""%>' />
              </div>
            </div>

            <div class="row">
              <div class="form-group col-sm-3">
                <label
                  for="<%=Columns.getColumnName(Columns.DAD_VALOR) + CodedValues.TDA_BEN_ADESAO_PLANO_EX_FUNCIONARIO%>">
                  <hl:message
                    key="rotulo.contrato.beneficio.adesao.ao.plano.de.ex.funcionario" />
                </label>
               <div>           
                  <input type="radio" name="benAdesaoPlanoExFuncionario" value="<%=CodedValues.TPC_SIM%>" <%=(String)(benAdesaoPlanoExFuncionario !=null && benAdesaoPlanoExFuncionario.equals("S") ? "checked" : "")%> onclick="aderirPlanoExFuncionario(this.value)"><hl:message key="rotulo.sim"/><br/>
                  <input type="radio" name="benAdesaoPlanoExFuncionario" value="<%=CodedValues.TPC_NAO%>" <%=(String)(benAdesaoPlanoExFuncionario !=null && benAdesaoPlanoExFuncionario.equals("N") ? "checked" : "")%> onclick="aderirPlanoExFuncionario(this.value)"><hl:message key="rotulo.nao"/><br/>
                 </div>
              </div>
              
              <div class="form-group col-sm-3"> 
                <label 
                  for="<%=Columns.getColumnName(Columns.DAD_VALOR) + CodedValues.TDA_BENEFICIARIO_CONTRIBUIU_PARA_PLANO%>">
                  <hl:message
                    key="rotulo.contrato.beneficio.contribuiu.para.plano" />
                </label> 
                <div>
                  <SELECT NAME="benContribuiuPlano" id="benContribuiuPlano" CLASS="form-control" <%=(String)(benAdesaoPlanoExFuncionario !=null && benAdesaoPlanoExFuncionario.equals("S") ? "" : "disabled")%>>
                      <OPTION VALUE=""><hl:message key="rotulo.campo.selecione"/></OPTION>
                      <OPTION VALUE="<%=cso%>" <%=(String)(benContribuiuPlano !=null && benContribuiuPlano.equals(cso)?"SELECTED":"")%>><%=cso%></OPTION>
                      <OPTION VALUE="<%=cao%>" <%=(String)(benContribuiuPlano !=null && benContribuiuPlano.equals(cao)?"SELECTED":"")%>><%=cao%></OPTION>
                      <OPTION VALUE="<%=csa%>" <%=(String)(benContribuiuPlano !=null && benContribuiuPlano.equals(csa)?"SELECTED":"")%>><%=csa%></OPTION>
                      <OPTION VALUE="<%=nao%>" <%=(String)(benContribuiuPlano !=null && benContribuiuPlano.equals(nao)?"SELECTED":"")%>><%=nao%></OPTION>
                      
                  </SELECT>           
                </div>
              </div> 

              <div class="form-group col-sm-3">
                <label
                  for="<%=Columns.getColumnName(Columns.DAD_VALOR) + CodedValues.TDA_BEN_PERIODO_CONTRIBUICAO_PLANO%>">
                  <hl:message
                    key="rotulo.contrato.beneficio.periodo.de.contribuicao" />
                </label>
                <hl:htmlinput
                  name="<%=Columns.getColumnName(Columns.DAD_VALOR) + CodedValues.TDA_BEN_PERIODO_CONTRIBUICAO_PLANO%>"
                  di="<%=Columns.getColumnName(Columns.DAD_VALOR) + CodedValues.TDA_BEN_PERIODO_CONTRIBUICAO_PLANO%>"
                  type="text" classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(contratoBeneficio != null && contratoBeneficio
            .getAttribute(Columns.DAD_VALOR + CodedValues.TDA_BEN_PERIODO_CONTRIBUICAO_PLANO) != null
                ? contratoBeneficio.getAttribute(
                    Columns.DAD_VALOR + CodedValues.TDA_BEN_PERIODO_CONTRIBUICAO_PLANO)
                : " ")%>"
                  size="5"
                  mask="#D5"
                  others='<%=!funEditarContratoBeneficio ? "disabled" : ""%>' />
              </div>
                    
              <div class="form-group col-sm-3">
                <label
                  for="<%=Columns.getColumnName(Columns.DAD_VALOR) + CodedValues.TDA_BENEFICIARIO_VALOR_DA_CONTRIBUICAO%>">
                  <hl:message
                    key="rotulo.contrato.beneficio.valor.de.contribuicao" />
                </label>
                <hl:htmlinput
                  name="<%=Columns.getColumnName(Columns.DAD_VALOR) + CodedValues.TDA_BENEFICIARIO_VALOR_DA_CONTRIBUICAO%>"
                  di="<%=Columns.getColumnName(Columns.DAD_VALOR) + CodedValues.TDA_BENEFICIARIO_VALOR_DA_CONTRIBUICAO%>"
                  type="text" classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(contratoBeneficio != null && contratoBeneficio
            .getAttribute(Columns.DAD_VALOR + CodedValues.TDA_BENEFICIARIO_VALOR_DA_CONTRIBUICAO) != null
                ? contratoBeneficio.getAttribute(
                    Columns.DAD_VALOR + CodedValues.TDA_BENEFICIARIO_VALOR_DA_CONTRIBUICAO)
                : "")%>"
                  size="8" 
                  mask="#F11"
                  onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                  others='<%=!funEditarContratoBeneficio ? "disabled" : ""%>' />
              </div>     
            </div>
          </div>
        </div>
      </div>
    </div>
  </form>

  <div class="btn-action col-sm">
    <a class="btn btn-outline-danger" href="#no-back"
      onClick="fluxoVoltar()"><hl:message key="rotulo.botao.voltar" /></a>
    <a href="#" onClick="javascript: verificaCampos();"
      class="btn btn-primary"><hl:message key="rotulo.botao.salvar" /></a>

  </div>
</c:set>
<c:set var="javascript">
  <script>
	function fluxoVoltar() {
		postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>');
	}
		
	function verificaCampos() {
		// validação de datas
		var dataIniVigenciaStr = document.getElementById("<%=Columns.getColumnName(Columns.CBE_DATA_INICIO_VIGENCIA)%>");
  	  	var dataFimVigenciaStr = document.getElementById("<%=Columns.getColumnName(Columns.CBE_DATA_FIM_VIGENCIA)%>");
  	  	var dataCancelamentoStr = document.getElementById("<%=Columns.getColumnName(Columns.CBE_DATA_CANCELAMENTO)%>");
  	    var datas;
  	    var dataIniVigencia;
  	    var dataFimVigencia;
  	    var dataCancelamento;
  	    
  	  	if (dataIniVigenciaStr && dataIniVigenciaStr.value) {
  			datas = obtemPartesData(dataIniVigenciaStr.value); 
  			dataIniVigencia = new Date(datas[2], datas[1]-1, datas[0]); 

  			// data início vigência não pode ser maior que data fim vigência
  			if (dataFimVigenciaStr && dataFimVigenciaStr.value) {
  	  			datas = obtemPartesData(dataFimVigenciaStr.value); 
  	  			dataFimVigencia = new Date(datas[2], datas[1]-1, datas[0]);
  	  			if (dataIniVigencia > dataFimVigencia) {
  	  				alert("<hl:message key='mensagem.erro.alterar.contrato.beneficio.data.ini.maior.data.fim'/>");
  	  				return false;
  	  			}  				
  			}
  			// data início vigência não pode ser maior que data cancelamento
  			if (dataCancelamentoStr && dataCancelamentoStr.value) {
  	  			datas = obtemPartesData(dataCancelamentoStr.value);
  	  			dataCancelamento = new Date(datas[2], datas[1]-1, datas[0]);
  	  			if (dataIniVigencia > dataCancelamento) {
	  	  			alert("<hl:message key='mensagem.erro.alterar.contrato.beneficio.data.ini.maior.data.cancelamento'/>");
  	  				return false;
  	  			} 
  	  			// data de cancelamento não pode ser maior que a data de fim vigência
  	  			if (dataFimVigenciaStr && dataFimVigenciaStr.value) {
  	  				if (dataCancelamento > dataFimVigencia) {
  	  					alert("<hl:message key='mensagem.erro.alterar.contrato.beneficio.data.cancelamanto.maior.data.fim'/>");
  	  					return false;
  	  				}  
  	  			}  	  			
  			}  			
  	  	}
  	  	
  	  	// campos obrigatórios:
		// data de início de vigência  	  	
		var controles = new Array(
				"<%=Columns.getColumnName(Columns.CBE_DATA_INICIO_VIGENCIA)%>"
		);
		
		var msgs = new Array(
		        "<hl:message key='mensagem.contrato.beneficio.data.inicio.vigencia.informar'/>"
		);
  	  	
		// caso o campo "contrinuiu para o plano" seja "sim", os campos obrigatórios são:
		// data de início de vigência
		// valor da contribuição
  	  	var contribuiuPlano = document.getElementsByName("benContribuiuPlano");
  	  	if ($("[name='benContribuiuPlano']").val() != "") {
  	  		controles = new Array(
				"<%=Columns.getColumnName(Columns.CBE_DATA_INICIO_VIGENCIA)%>",
				"<%=Columns.getColumnName(Columns.DAD_VALOR) + CodedValues.TDA_BENEFICIARIO_VALOR_DA_CONTRIBUICAO%>"
			);
  			msgs = new Array(
		        "<hl:message key='mensagem.contrato.beneficio.data.inicio.vigencia.informar'/>",
		        "<hl:message key='mensagem.contrato.beneficio.valor.contribuicao.informar'/>"
			);
  	  	}

		if (ValidaCampos(controles, msgs)) {
			enableAll();
			document.getElementById("form1").submit();
		}		
	}
	
	function aderirPlanoExFuncionario(valor) {
		if (valor == "S") {
			$('[name=benContribuiuPlano]').attr("disabled", false);			
		} else {
			$('[name=benContribuiuPlano]').attr("disabled", true);
			$('[name=benContribuiuPlano]').val("");
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