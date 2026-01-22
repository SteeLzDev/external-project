<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="fl" uri="/function-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="show" uri="/showfield-lib"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.regex.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.persistence.entity.TipoConsignante"%>
<%@ page import="com.zetra.econsig.persistence.entity.Banco"%>
<%@ page import="com.zetra.econsig.web.filter.XSSPreventionFilter"%>
<%
	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
	List<TipoConsignante> listTipoCSE = (List<TipoConsignante>) request.getAttribute("listTipoCSE");
	List<TransferObject> lstOcorrencias = (List<TransferObject>) request.getAttribute("lstOcorrencias");
	List<Banco> listBanco = (List<Banco>) request.getAttribute("listBanco");
    Boolean listBancoVazia = (Boolean) request.getAttribute("listBancoVazia");

	Date cseDataCobranca = (Date) request.getAttribute("cseDataCobranca");
	Short cse_ativo = (Short) request.getAttribute("cse_ativo");
	String cse_codigo = (String) request.getAttribute("cse_codigo");
	String cse_nome = (String) request.getAttribute("cse_nome");

	ConsignanteTransferObject consignante = (ConsignanteTransferObject) request.getAttribute("consignante");
	Boolean podeEditarConsignante = (Boolean) request.getAttribute("podeEditarConsignante");
	Boolean podeConsultarUsuarios = (Boolean) request.getAttribute("podeConsultarUsuarios");
	Boolean podeCriarUsuarios = (Boolean) request.getAttribute("podeCriarUsuarios");
	Boolean podeConsultarPerfilUsu = (Boolean) request.getAttribute("podeConsultarPerfilUsu");
	Boolean podeConsParamSistCse = (Boolean) request.getAttribute("podeConsParamSistCse");
	Boolean podeEditarEnderecoAcesso = (Boolean) request.getAttribute("podeEditarEnderecoAcesso");
	Boolean habilitarCodigoFolha = (Boolean) request.getAttribute("habilitarCodigoFolha");

	String periodoEnvioEmailAudit = (String) ParamSist.getInstance()
			.getParam(CodedValues.TPC_PERIODO_ENVIO_EMAIL_AUDITORIA_CSE_ORG, responsavel);

	Boolean podeEditarFuncoesEditaveis = responsavel.isCseSup()
			&& responsavel.temPermissao(CodedValues.FUN_USUARIO_AUDITOR) && (periodoEnvioEmailAudit != null
					&& !periodoEnvioEmailAudit.equals(CodedValues.PER_ENV_EMAIL_AUDIT_DESABILITADO));

	Boolean podeExibirMaisAcoes = podeEditarConsignante || podeConsultarUsuarios || podeCriarUsuarios
			|| podeConsultarPerfilUsu || podeConsParamSistCse || podeEditarFuncoesEditaveis;
%>
<c:set var="title">
  <hl:message key="rotulo.consignante.pagina.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-12 col-md-12 mb-2">
      <div class="float-end">
        <div class="btn-action">
          <%
          	if (podeExibirMaisAcoes) {
          %>
          <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit">
            <hl:message key="rotulo.mais.acoes" />
          </button>
          &nbsp;&nbsp;&nbsp;&nbsp;
          <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes">
            <c:choose>
              <c:when test="${podeEditarConsignante}">
                <a class="dropdown-item" href="#no-back"
                  onClick="BloquearDesBloquearSistema(<%=TextHelper.forJavaScript(cse_ativo.toString())%>, '<%=TextHelper.forJavaScript(cse_codigo)%>', 'CSE', '../v3/editarConsignante?acao=editar')">
                  <%
                  	if (cse_ativo == 1) {
                  %>
                  <hl:message key='rotulo.consignante.bloquear.consignante' />
                  <%
                  	} else {
                  %>
                  <hl:message key='rotulo.consignante.desbloquear.consignante' />
                  <%
                  	}
                  %>
                </a>
                <div class="dropdown-divider" role="separator"></div>
              </c:when>
              <c:otherwise></c:otherwise>
            </c:choose>
            <c:if test="${podeConsultarPerfilUsu || podeConsultarUsuarios || podeCriarUsuarios}">
              <c:if test="${podeConsultarPerfilUsu}">
                <a class="dropdown-item" href="#no-back"
                  onClick="postData('../v3/listarPerfilCse?acao=listar&codigo=<%=TextHelper.forJavaScriptAttribute(cse_codigo)%>&titulo=<%=TextHelper.forJavaScript(cse_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                  <hl:message key="rotulo.consignante.listar.perfil" />
                </a>
              </c:if>
              <c:if test="${podeConsultarUsuarios}">
                <a class="dropdown-item" href="#no-back"
                  onClick="postData('../v3/listarUsuarioCse?acao=listar&codigo=<%=TextHelper.forJavaScriptAttribute(cse_codigo)%>&titulo=<%=TextHelper.encode64(cse_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                  <hl:message key="rotulo.consignante.listar.usuarios" />
                </a>
              </c:if>
               <% if (responsavel.temPermissao(CodedValues.FUN_CONSULTAR_FUNCOES_ENVIO_EMAIL)) { %>
                  <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarFuncoesEnvioEmail?acao=iniciar&CSE_CODIGO=<%=TextHelper.forJavaScriptAttribute(cse_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acao.funcoes.envio.email"/></a>
            	<% } %>
                <a class="dropdown-item" href="#no-back"
                  onClick="postData('../v3/editarParamMargem?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>&titulo=<%=TextHelper.forJavaScript(cse_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                  <hl:message key="rotulo.consignante.alterar.configuracoes.margem" />
                </a>
              <c:if test="${podeCriarUsuarios}">
                <a class="dropdown-item" href="#no-back"
                  onClick="postData('../v3/inserirUsuarioCse?acao=iniciar&codigo=<%=TextHelper.forJavaScriptAttribute(cse_codigo)%>&titulo=<%=TextHelper.encode64(cse_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                  <hl:message key="rotulo.consignante.inserir.novos.usuarios" />
                </a>
              </c:if>
              <div class="dropdown-divider" role="separator"></div>
            </c:if>
            <c:if test="${podeConsParamSistCse}">
              <a class="dropdown-item" href="#no-back"
                onClick="postData('../v3/manterParamConsignante?acao=iniciar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')">
                <hl:message key="rotulo.consignante.alterar.parametros" />
              </a>
              <div class="dropdown-divider" role="separator"></div>
            </c:if>
            <%
            	if (podeEditarFuncoesEditaveis) {
            %>
            <a class="dropdown-item" href="#no-back"
              onClick="postData('../v3/manterFuncoesAuditaveis?acao=iniciar&codigo=<%=TextHelper.forJavaScriptAttribute(cse_codigo)%>&descricao=<%=TextHelper.forJavaScript(cse_nome)%>&tipo=<%=TextHelper.forJavaScript(
  								responsavel.isSup() ? AcessoSistema.ENTIDADE_SUP : AcessoSistema.ENTIDADE_CSE)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
              <hl:message key="rotulo.consignante.configurar.auditoria" />
            </a>
            <%
            	}
            %>
          </div>
          <%
          	}
          %>
        </div>
      </div>
    </div>
  </div>
  <!-- Formulário -->
  <form method="POST" action="../v3/editarConsignante?<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" onSubmit="return vf_cadastro_cse();">
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title">
          <hl:message key="rotulo.consignante.dados.consignante" />
        </h2>
      </div>
      <div class="card-body">
        <fieldset>
         <div class="row">
            <div class="form-group col-sm-12 col-md-2">
              <label><hl:message key="rotulo.situacao" /></label>
               <% String valor = "";
                  if (cse_ativo == 1) {
                      valor = (String) ApplicationResourcesHelper.getMessage("rotulo.situacao.ativo.singular",
      						responsavel);
                  } else {
                      valor = (String) ApplicationResourcesHelper.getMessage("rotulo.situacao.bloqueado.singular",
      						responsavel);
                  }
                %>
                <hl:htmlinput name="situacao" type="text" classe="form-control" di="situacao" value="<%=valor%>" others="disabled" />
            </div>
            <div class="form-group col-sm-12 col-md-2">
              <label for="CSE_IDENTIFICADOR">
                *
                <hl:message key="rotulo.consignante.codigo" />
              </label>
              <hl:htmlinput name="CSE_IDENTIFICADOR" type="text" classe="form-control"
                placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.correspondente.digite.codigo",
            responsavel)%>" di="CSE_IDENTIFICADOR"
                value="<%=TextHelper.forHtmlAttribute(consignante.getCseIdentificador())%>" size="32" mask="#A40" others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
            <div class="form-group col-sm-12 col-md-4">
              <label for="CSE_NOME">
                *
                <hl:message key="rotulo.consignante.descricao" />
              </label>
              <hl:htmlinput name="CSE_NOME" type="text" di="CSE_NOME" placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.nome",
						responsavel)%>"
                classe="form-control" value="<%=TextHelper.forHtmlAttribute(consignante.getCseNome())%>" size="32" mask="#*100" others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
            <div class="form-group col-sm-12 col-md-4">
              <label for="CSE_CNPJ">
                <hl:message key="rotulo.consignante.cnpj" />
              </label>
              <hl:htmlinput name="CSE_CNPJ" type="text" classe="form-control" placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.placeholder.cnpj", responsavel)%>" di="CSE_CNPJ"
                value="<%=TextHelper.forHtmlAttribute(consignante.getCseCnpj())%>" mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjMask())%>"
                size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjSize())%>" maxlength="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjMaxLenght())%>"
                others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
          </div>
          <%
          	if (responsavel.isSup()) {
          %>
          <div class="row">
            <div class="form-group col-sm-4 col-md-4">
              <label for="TCE_CODIGO">
                <hl:message key="rotulo.tipo.consignante.singular" />
              </label>
              <%
              	StringBuffer comboTipoCse = new StringBuffer();
              			comboTipoCse.append("<select name=\"TCE_CODIGO\" class=\"form-control form-select\"");
              			comboTipoCse.append(" onFocus=\"SetarEventoMascara(this,'#*200',true);\"");
              			comboTipoCse.append(" onBlur=\"fout(this);ValidaMascara(this);\"");
              			comboTipoCse.append(" >");

              			if (consignante.getTipoConsignante() == null) {
              				comboTipoCse.append("<option value=\"\" selected>");
              			} else {
              				comboTipoCse.append("<option value=\"\">");
              			}

              			comboTipoCse.append(ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel))
              					.append("</option>");

              			for (TipoConsignante tipoCse : listTipoCSE) {
              				comboTipoCse.append("<option value=\"");
              				comboTipoCse.append(TextHelper.forHtmlAttribute(tipoCse.getTceCodigo()));
              				if (tipoCse.getTceCodigo().equals(consignante.getTipoConsignante())) {
              					comboTipoCse.append("\" selected>");
              				} else {
              					comboTipoCse.append("\">");
              				}
              				comboTipoCse.append(TextHelper.forHtmlContent(tipoCse.getTceDescricao()));
              				comboTipoCse.append("</option>");
              			}
              			comboTipoCse.append("</select>");
              %>
              <%=comboTipoCse.toString()%>
            </div>
            <div class="form-group col-sm-4">
              <label for="CSE_SISTEMA_FOLHA">
                <hl:message key="rotulo.consignante.sistema.folha" />
              </label>
              <hl:htmlinput name="CSE_SISTEMA_FOLHA" type="text" classe="form-control"
                placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.placeholder.cse.sistema.folha",
                responsavel)%>" di="CSE_SISTEMA_FOLHA"
                value="<%=TextHelper.forHtmlAttribute(consignante.getCseSistemaFolha())%>" size="32" mask="#*100" others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
           <div class="form-group col-sm-4 col-md-4">
              <label for="BCO_CODIGO">
                <hl:message key="rotulo.consignante.banco.folha" />
              </label>
              <%
                    StringBuffer comboBancoFolha = new StringBuffer();
                    comboBancoFolha.append("<select name=\"BCO_CODIGO\" class=\"form-control form-select\"");
                    comboBancoFolha.append(" multiple size=\"4\" onFocus=\"SetarEventoMascara(this,'#*200',true);\"");
                    comboBancoFolha.append(" onBlur=\"fout(this);ValidaMascara(this);\"");
                    comboBancoFolha.append(" >");

                    if (listBancoVazia) {
                      comboBancoFolha.append("<option value=\"\" selected>");
                    } else {
                      comboBancoFolha.append("<option value=\"\">");
                    }

                    comboBancoFolha.append(ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel))
                        .append("</option>");

                    for (Banco banco : listBanco) {
                      comboBancoFolha.append("<option value=\"");
                      comboBancoFolha.append(TextHelper.forHtmlAttribute(banco.getBcoCodigo()));
                      if(banco.getBcoFolha().equals("S")){
                        comboBancoFolha.append("\" selected>");
                      } else {
                        comboBancoFolha.append("\">");
                      }
                      comboBancoFolha.append(TextHelper.forHtmlContent(banco.getBcoDescricao()));
                      comboBancoFolha.append("</option>");
                    }
                    comboBancoFolha.append("</select>");
              %>
              <%=comboBancoFolha.toString()%>
            </div>
          </div>
          <%
          	}
          %>
          <div class="legend"></div>
          <fieldset>
          <div class="row">
            <div class="form-group col-sm-4">
              <label for="CSE_RESPONSAVEL">
                <hl:message key="rotulo.consignante.responsavel" />
              </label>
              <hl:htmlinput name="CSE_RESPONSAVEL" type="text" classe="form-control"
                placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.placeholder.primeiro.responsavel",
						responsavel)%>" di="CSE_RESPONSAVEL"
                value="<%=TextHelper.forHtmlAttribute(consignante.getCseResponsavel())%>" size="32" mask="#*100" others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
            <div class="form-group col-sm-4">
              <label for="CSE_RESP_CARGO">
                <hl:message key="rotulo.consignante.cargo" />
              </label>
              <hl:htmlinput name="CSE_RESP_CARGO" type="text" placeHolder="<%=(String) ApplicationResourcesHelper
						.getMessage("mensagem.placeholder.primeiro.responsavel.cargo", responsavel)%>"
                classe="form-control" di="CSE_RESP_CARGO" value="<%=TextHelper.forHtmlAttribute(consignante.getCseRespCargo())%>" size="32" mask="#*100"
                others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
            <div class="form-group col-sm-4">
              <label for="CSE_RESP_TELEFONE">
                <hl:message key="rotulo.consignante.telefone" />
              </label>
              <hl:htmlinput name="CSE_RESP_TELEFONE" type="text" classe="form-control"
                placeHolder="<%=(String) ApplicationResourcesHelper
						.getMessage("mensagem.placeholder.primeiro.responsavel.telefone", responsavel)%>" di="CSE_RESP_TELEFONE"
                value="<%=TextHelper.forHtmlAttribute(consignante.getCseRespTelefone())%>" size="32" mask="#*100" others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
          </div>
          <div class="row">
            <div class="form-group col-sm-4">
              <label for="CSE_RESPONSAVEL_2">
                <hl:message key="rotulo.consignante.responsavel2" />
              </label>
              <hl:htmlinput name="CSE_RESPONSAVEL_2" type="text" placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.placeholder.segundo.responsavel",
						responsavel)%>"
                classe="form-control" di="CSE_RESPONSAVEL_2" value="<%=TextHelper.forHtmlAttribute(consignante.getCseResponsavel2())%>" size="32" mask="#*100"
                others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
            <div class="form-group col-sm-4">
              <label for="CSE_RESP_CARGO_2">
                <hl:message key="rotulo.consignante.cargo" />
              </label>
              <hl:htmlinput name="CSE_RESP_CARGO_2" type="text" placeHolder="<%=(String) ApplicationResourcesHelper
						.getMessage("mensagem.placeholder.segundo.responsavel.cargo", responsavel)%>"
                classe="form-control" di="CSE_RESP_CARGO_2" value="<%=TextHelper.forHtmlAttribute(consignante.getCseRespCargo2())%>" size="32" mask="#*100"
                others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
            <div class="form-group col-sm-4">
              <label for="CSE_RESP_TELEFONE_2">
                <hl:message key="rotulo.consignante.telefone" />
              </label>
              <hl:htmlinput name="CSE_RESP_TELEFONE_2" type="text" placeHolder="<%=(String) ApplicationResourcesHelper
						.getMessage("mensagem.placeholder.segundo.responsavel.telefone", responsavel)%>"
                classe="form-control" di="CSE_RESP_TELEFONE_2" value="<%=TextHelper.forHtmlAttribute(consignante.getCseRespTelefone2())%>" size="32" mask="#*100"
                others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
          </div>
          <div class="row">
            <div class="form-group col-sm-4">
              <label for="CSE_RESPONSAVEL_3">
                <hl:message key="rotulo.consignante.responsavel3" />
              </label>
              <hl:htmlinput name="CSE_RESPONSAVEL_3" type="text" placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.placeholder.terceiro.responsavel",
						responsavel)%>"
                classe="form-control" di="CSE_RESPONSAVEL_3" value="<%=TextHelper.forHtmlAttribute(consignante.getCseResponsavel3())%>" size="32" mask="#*100"
                others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
            <div class="form-group col-sm-4">
              <label for="CSE_RESP_CARGO_3">
                <hl:message key="rotulo.consignante.cargo" />
              </label>
              <hl:htmlinput name="CSE_RESP_CARGO_3" type="text" placeHolder="<%=(String) ApplicationResourcesHelper
						.getMessage("mensagem.placeholder.terceiro.responsavel.cargo", responsavel)%>"
                classe="form-control" di="CSE_RESP_CARGO_3" value="<%=TextHelper.forHtmlAttribute(consignante.getCseRespCargo3())%>" size="32" mask="#*100"
                others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
            <div class="form-group col-sm-4">
              <label for="CSE_RESP_TELEFONE_3">
                <hl:message key="rotulo.consignante.telefone" />
              </label>
              <hl:htmlinput name="CSE_RESP_TELEFONE_3" type="text" placeHolder="<%=(String) ApplicationResourcesHelper
						.getMessage("mensagem.placeholder.terceiro.responsavel.telefone", responsavel)%>"
                classe="form-control" di="CSE_RESP_TELEFONE_3" value="<%=TextHelper.forHtmlAttribute(consignante.getCseRespTelefone3())%>" size="32" mask="#*100"
                others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
          </div>
        </fieldset>
        <fieldset>
          <h3 class="legend">
            <span>
              <hl:message key="rotulo.endereco" />
            </span>
          </h3>
          <div class="row">
            <div class="form-group col-sm-6">
              <label for="CSE_LOGRADOURO">
                <hl:message key="rotulo.consignante.logradouro" />
              </label>
              <hl:htmlinput name="CSE_LOGRADOURO" type="text" placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.placeholder.logradouro", responsavel)%>" classe="form-control"
                di="CSE_LOGRADOURO" value="<%=TextHelper.forHtmlAttribute(consignante.getCseLogradouro())%>" size="32" mask="#*100" others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
            <div class="form-group col-sm-3">
              <label for="CSE_NRO">
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_CONSIGNANTE_NRO)%>">
                  <hl:message key="rotulo.consignante.numero.abreviado" />
                </show:showfield>
              </label>
              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_CONSIGNANTE_NRO)%>">
                <hl:htmlinput name="CSE_NRO" type="text" placeHolder="<%=(String) ApplicationResourcesHelper
              .getMessage("mensagem.placeholder.digite.numero.logradouro", responsavel)%>"
                  classe="form-control" di="CSE_NRO" value="<%=TextHelper.forHtmlAttribute(consignante.getCseNro())%>" size="32" mask="#D5"
                  others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
              </show:showfield>
            </div>
            <div class="form-group col-sm-3">
              <label for="CSE_COMPL">
                <hl:message key="rotulo.consignante.complemento" />
              </label>
              <hl:htmlinput name="CSE_COMPL" type="text" placeHolder="<%=(String) ApplicationResourcesHelper
						.getMessage("mensagem.placeholder.digite.complemento.logradouro", responsavel)%>"
                classe="form-control" di="CSE_COMPL" value="<%=TextHelper.forHtmlAttribute(consignante.getCseCompl())%>" size="32" mask="#*40"
                others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
          </div>
          <div class="row">
            <div class="form-group col-sm-4">
              <label for="CSE_BAIRRO">
                <hl:message key="rotulo.consignante.bairro" />
              </label>
              <hl:htmlinput name="CSE_BAIRRO" type="text" placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.bairro",
						responsavel)%>" classe="form-control"
                di="CSE_BAIRRO" value="<%=TextHelper.forHtmlAttribute(consignante.getCseBairro())%>" size="32" mask="#*40" others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
            <div class="form-group col-sm-4">
              <label for="CSE_CIDADE">
                <hl:message key="rotulo.consignante.cidade" />
              </label>
              <hl:htmlinput name="CSE_CIDADE" type="text" placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.cidade",
						responsavel)%>" classe="form-control"
                di="CSE_CIDADE" value="<%=TextHelper.forHtmlAttribute(consignante.getCseCidade())%>" size="32" mask="#*40" others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
            <div class="form-group col-sm-4">
              <label for="CSE_UF">
                <hl:message key="rotulo.consignante.uf" />
              </label>
              <hl:campoUFv4 name="CSE_UF" classe="form-control" di="CSE_UF" rotuloUf="rotulo.consignante.uf"
                placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.uf", responsavel)%>"
                valorCampo="<%=TextHelper.forHtmlAttribute(
						consignante != null && consignante.getCseUf() != null ? consignante.getCseUf() : "")%>" desabilitado="<%=!podeEditarConsignante%>" />
            </div>
          </div>
          <div class="row">
            <div class="form-group col-sm-4">
              <label for="CSE_CEP">
                <hl:message key="rotulo.consignante.cep" />
              </label>
              <hl:htmlinput name="CSE_CEP" type="text" placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.cep", responsavel)%>"
                value="<%=TextHelper.forHtmlAttribute(consignante.getCseCep())%>" size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCepSize())%>" classe="form-control" di="CSE_CEP"
                mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCepMask())%>" others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
          </div>
        </fieldset>
        <fieldset>
          <h3 class="legend">
            <span>
              <hl:message key="rotulo.consignante.informacoes.contato" />
            </span>
          </h3>
          <div class="row">
            <div class="form-group col-sm-4">
              <label for="CSE_TEL">
                <hl:message key="rotulo.consignante.telefone" />
              </label>
              <hl:htmlinput name="CSE_TEL" type="text" placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.telefone",
						responsavel)%>" classe="form-control"
                di="CSE_TEL" value="<%=TextHelper.forHtmlAttribute(consignante.getCseTel())%>" size="32" mask="<%=LocaleHelper.getMultiplosTelefonesMask()%>"
                others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
            <div class="form-group col-sm-4">
              <label for="CSE_FAX">
                <hl:message key="rotulo.consignante.fax" />
              </label>
              <hl:htmlinput name="CSE_FAX" type="text" placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.fax", responsavel)%>" classe="form-control"
                di="CSE_FAX" value="<%=TextHelper.forHtmlAttribute(consignante.getCseFax())%>" size="32" mask="<%=LocaleHelper.getMultiplosTelefonesMask()%>"
                others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
          </div>
          <div class="row">
            <div class="form-group col-sm-4">
              <label for="CSE_EMAIL">
                <hl:message key="rotulo.consignante.email" />
              </label>
              <hl:htmlinput name="CSE_EMAIL" type="textarea" placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.email",
						responsavel)%>" classe="form-control"
                di="CSE_EMAIL" onBlur="formataEmails('CSE_EMAIL')" rows="3" cols="67" value="<%=TextHelper.forHtmlAttribute(consignante.getCseEmail())%>" size="32"
                others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
            <div class="form-group col-sm-4">
              <label for="CSE_EMAIL_FOLHA">
                <hl:message key="rotulo.consignante.email.integracao" />
              </label>
              <hl:htmlinput name="CSE_EMAIL_FOLHA" type="textarea" placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.email",
						responsavel)%>"
                classe="form-control" di="CSE_EMAIL_FOLHA" onBlur="formataEmails('CSE_EMAIL_FOLHA')" rows="3" cols="67" value="<%=TextHelper.forHtmlAttribute(consignante.getCseEmailFolha())%>"
                size="32" others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
            <div class="form-group col-sm-4">
              <label for="CSE_EMAIL_VALIDAR_SERVIDOR">
                <hl:message key="rotulo.consignante.email.validar.servidor" />
              </label>
              <hl:htmlinput name="CSE_EMAIL_VALIDAR_SERVIDOR" type="textarea" placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.email",
						responsavel)%>"
                classe="form-control" di="CSE_EMAIL_VALIDAR_SERVIDOR" onBlur="formataEmails('CSE_EMAIL_VALIDAR_SERVIDOR')" rows="3" cols="67"
                value="<%=TextHelper.forHtmlAttribute(consignante.getCseEmailValidarServidor())%>" size="32" others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" />
            </div>
          </div>
        </fieldset>
        <fieldset>
          <div class="legend"></div>
          <%
          	if (responsavel.isSup()) {
          %>
          <div class="row">
            <div class="form-group col-sm-6">
              <label for="CSE_DATA_COBRANCA">
                <hl:message key="rotulo.consignante.data.cobranca" />
              </label>
              <input type="text" placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.data",
							responsavel)%>" class="form-control"
                onFocus="SetarEventoMascara(this,'<%=LocaleHelper.getDateJavascriptPattern()%>',true);" onBlur="fout(this);ValidaMascara(this);" type="text" CLASS="form-control"
                name="CSE_DATA_COBRANCA" id="CSE_DATA_COBRANCA" value="<%=TextHelper.forHtmlAttribute(
							!TextHelper.isNull(cseDataCobranca) ? DateHelper.toDateString(cseDataCobranca) : "")%>" size="15">
            </div>
          </div>
          <%
          	}
          %>
          <%
          	if (habilitarCodigoFolha) {
          %>
          <div class="row">
            <div class="form-group col-sm-6">
              <label for="CSE_FOLHA">
                <hl:message key="rotulo.consignante.folha" />
              </label>
              <hl:htmlinput name="CSE_FOLHA" placeHolder="<%=(String) ApplicationResourcesHelper.getMessage("mensagem.correspondente.digite.codigo",
							responsavel)%>" type="text" classe="form-control"
                di="CSE_FOLHA" value="<%=TextHelper.forHtmlAttribute(consignante.getCseFolha())%>" size="40" others="<%=(String) (!podeEditarConsignante ? "disabled" : "")%>" nf="submit" />
            </div>
          </div>
          <%
          	}
          %>
          <%if(responsavel.isSup()){ %>
            <%
               String partProjInadimplencia = consignante != null && !TextHelper.isNull(consignante.getCseProjetoInadimplencia()) ? consignante.getCseProjetoInadimplencia() : "N";
            %>
          <div class="col-sm-12 col-md-6">
            <span id="descricao"><hl:message key="rotulo.consignante.projeto.inadimplencia"/></span>
              <div class="form-group">
              <div class="form-check form-check-inline" role="radiogroup" aria-labelledby="partProgIn">
                  <input class="form-check-input ml-1" type="radio" name="CSE_PROJETO_INADIMPLENCIA" value="S" id="inadSim" <%=(String)(partProjInadimplencia.equals("S") ? "checked" : "")%> value="S">
                  <label class="form-check-label labelSemNegrito pr-3" for="inadSim">
                    <hl:message key="rotulo.sim"/>
                  </label>
              </div>
                <div class="form-check-inline form-check">
                  <input class="form-check-input ml-1" type="radio" value="N" name="CSE_PROJETO_INADIMPLENCIA" id="inadNao" <%=(String)(partProjInadimplencia.equals("N") ? "checked" : "")%> value="N">
                  <label class="form-check-label labelSemNegrito" for="inadNao">
                    <hl:message key="rotulo.nao"/>
                  </label>
                  <input type="hidden" name="cse_projeto_inadimplencia_old" value="<%=TextHelper.forHtmlAttribute(partProjInadimplencia)%>">
              </div>
              </div>
          </div>
          <%}%>
          <%
          	if (!ParamSist.paramEquals(CodedValues.TPC_PERMITE_BLOQUEIO_ACESSO_POR_IP, CodedValues.TPC_NAO, responsavel)) {
          %>
          <jsp:include page="../manterEntidades/incluirCampoAcesso_v4.jsp">
            <jsp:param name="tipo_endereco" value="numero_ip" />
            <jsp:param name="nome_campo" value="novoIp" />
            <jsp:param name="nome_lista" value="listaIps" />
            <jsp:param name="lista_resultado" value="cse_ip_acesso" />
            <jsp:param name="label" value="rotulo.usuario.ips.acesso" />
            <jsp:param name="mascara" value="#I30" />
            <jsp:param name="pode_editar" value="<%=(boolean) podeEditarEnderecoAcesso%>" />
            <jsp:param name="bloquear_ip_interno" value="false" />
            <jsp:param name="placeHolder" value="mensagem.placeholder.digite.ip.acesso" />
          </jsp:include>
          <%
          	}
          %>
          <%
          	if (!ParamSist.paramEquals(CodedValues.TPC_PERMITE_BLOQUEIO_ACESSO_POR_DNS, CodedValues.TPC_NAO, responsavel)) {
          %>
          <jsp:include page="../manterEntidades/incluirCampoAcesso_v4.jsp">
            <jsp:param name="tipo_endereco" value="url" />
            <jsp:param name="nome_campo" value="novoDDNS" />
            <jsp:param name="nome_lista" value="listaDDNSs" />
            <jsp:param name="lista_resultado" value="cse_ddns_acesso" />
            <jsp:param name="label" value="rotulo.usuario.enderecos.acesso" />
            <jsp:param name="mascara" value="#*100" />
            <jsp:param name="pode_editar" value="<%=(boolean) podeEditarEnderecoAcesso%>" />
            <jsp:param name="placeHolder" value="mensagem.placeholder.digite.endereco.acesso" />
          </jsp:include>
          <%
          	}
          %>
          </fieldset>
        </div>
      </div>
      <%
      	Iterator itHistorico = lstOcorrencias.iterator();
      		if (itHistorico.hasNext()) {
      %>
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title">
            <hl:message key="rotulo.consignante.historico" />
          </h2>
        </div>
        <div class="card-body table-responsive ">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col" width="20%"><hl:message key="rotulo.consignante.data" /></th>
                <th scope="col" width="10%"><hl:message key="rotulo.consignante.responsavel" /></th>
                <th scope="col" width="20%"><hl:message key="rotulo.consignante.tipo" /></th>
                <th scope="col" width="30%"><hl:message key="rotulo.consignante.descricao" /></th>
                <th scope="col" width="20%"><hl:message key="rotulo.consignante.ip.acesso" /></th>
              </tr>
            </thead>
            <tbody>
              <%
              	int i = 0;
              			while (itHistorico.hasNext()) {
              				CustomTransferObject cto = (CustomTransferObject) itHistorico.next();
              				String oceData = DateHelper.toDateTimeString((Date) cto.getAttribute(Columns.OCE_DATA));
              				String loginOceResponsavel = cto.getAttribute(Columns.USU_LOGIN) != null
              						? cto.getAttribute(Columns.USU_LOGIN).toString()
              						: "";
              				String oceResponsavel = (loginOceResponsavel
              						.equalsIgnoreCase((String) cto.getAttribute(Columns.USU_CODIGO))
              						&& cto.getAttribute(Columns.USU_TIPO_BLOQ) != null)
              								? cto.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)"
              								: loginOceResponsavel;
              				String oceTipo = cto.getAttribute(Columns.TOC_DESCRICAO).toString();
              				String oceObs = cto.getAttribute(Columns.OCE_OBS).toString();
              				String oceIpAcesso = cto.getAttribute(Columns.OCE_IP_ACESSO) != null
              						? cto.getAttribute(Columns.OCE_IP_ACESSO).toString()
              						: "";
              %>
              <tr class="<%=(String) (i++ % 2 == 0 ? "Li" : "Lp")%>">
                <td><%=TextHelper.forHtmlContent(oceData)%></td>
                <td><%=TextHelper.forHtmlContent(oceResponsavel)%></td>
                <td><%=TextHelper.forHtmlContent(oceTipo)%></td>
                <td><%=TextHelper.forHtmlContent(oceObs)%></td>
                <td><%=TextHelper.forHtmlContent(oceIpAcesso)%></td>
              </tr>
              <%
              	}
              %>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="50"><hl:message key="rotulo.consignante.margem" />
                  <span class="font-italic">
                    - <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}" />
                  </span>
                </td>
              </tr>
            </tfoot>
          </table>
        </div>
        <div class="card-footer">
          <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp"%>
        </div>
      </div>
      <%
      	}
      %>
      <hl:htmlinput name="ip_list" di="ip_list" type="hidden" value="<%=TextHelper
						.forHtmlAttribute(consignante.getCseIPAcesso() != null ? consignante.getCseIPAcesso() : "")%>" />
      <hl:htmlinput name="ddns_list" di="ddns_list" type="hidden" value="<%=TextHelper.forHtmlAttribute(
						consignante.getCseDDNSAcesso() != null ? consignante.getCseDDNSAcesso() : "")%>" />
      <%
      	if (podeEditarConsignante || podeEditarEnderecoAcesso) {
      %>
      <hl:htmlinput name="CSE_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(consignante.getCseCodigo())%>" />
      <hl:htmlinput name="MM_update" type="hidden" value="form1" />
      <hl:htmlinput name="acao" type="hidden" value="<%=podeEditarConsignante
							? "editar"
							: (podeEditarEnderecoAcesso ? "editarEnderecoAcesso" : "iniciar")%>" />
    <div>
      <div class="btn-action mr-3 pr-3">
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;">
          <hl:message key="rotulo.botao.cancelar" />
        </a>
        <a class="btn btn-primary" data-bs-dismiss="modal" href="#no-back" onClick="vf_cadastro_cse();">
          <hl:message key="rotulo.botao.salvar" />
        </a>
      </div>
      <%
      } else {
      %>
      <div class="btn-action mr-3 pr-3">
        <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;">
          <hl:message key="rotulo.botao.voltar" />
        </a>
      </div>
      <%
        }
      %>
    </div>

  </form>
  <div class="modal fade" id="bloquearModal" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header pb-0">
          <h5 class="modal-title about-title mb-0" id="modalTitulo">
            <%
              if (cse_ativo == 1) {
            %>
            <hl:message key='rotulo.consignante.bloquear.consignante' />
            <%
            } else {
            %>
            <hl:message key="rotulo.consignante.desbloquear.consignante" />
            <%
              }
            %>

          </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body pb-1 pt-1">
          <p>
            <%
            	if (cse_ativo == 1) {
            %>
            <hl:message key="mensagem.confirmacao.bloqueio.consignante" arg0="<%=TextHelper.forHtmlAttribute(cse_nome)%>" />
            <%
            	} else {
            %>
            <hl:message key="mensagem.confirmacao.desbloqueio.consignante" arg0="<%=TextHelper.forHtmlAttribute(cse_nome)%>" />
            <%
            	}
            %>
          </p>
          <p>
          <form>
            <div class="form-group m-0 p-0">
	          <%
	          	if (cse_ativo == 1) {
	          %>
                <label for="motivo">
                  <hl:message key="rotulo.consignante.motivo" />
                </label>
	          <%
	          	}
	          %>
                </br>
                <input type="hidden" value="<%=TextHelper.forHtmlAttribute(cse_ativo.toString())%>" id="status">
                <input type="hidden" value="<%=TextHelper.forHtmlAttribute(cse_nome)%>" id="desc">
                <input type="hidden" value="" id="url">
	          <%
	          	if (cse_ativo == 1) {
	          %>
                <textarea name="motivo" id="motivo" class="form-control" rows="5" cols="25" placeHolder="<hl:message key='rotulo.consignante.digite.motivo'/>" value=""></textarea>
	          <%
	          	}
	          %>
            </div>
          </form>
        </div>
          <div class="modal-footer pt-0">
              <div class="btn-action mt-2 mb-0">
                  <a class="btn btn-outline-danger" data-bs-dismiss="modal" href="#">
                      <hl:message key="rotulo.botao.cancelar" />
                  </a>
                  <a class="btn btn-primary" href="#no-back" onclick="efetivaBloqueioDesbloqueio();" data-bs-dismiss="modal">
                      <hl:message key="rotulo.botao.confirmar" />
                  </a>
              </div>
          </div>
      </div>
    </div>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/listagem.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript">

  var f0 = document.forms[0];

  function formLoad() {
    focusFirstField();
  }

  function BloquearDesBloquearSistema(status, codigo, tipo, alink) {
    $('#bloquearModal').modal('show');
    var url = alink + (alink.indexOf('?') == -1 ? "?" : "&") + "status=" + status + "&codigo=" + codigo + '&<%=SynchronizerToken.generateToken4URL(request)%>';
    $("#url").val(url);

  }

  function efetivaBloqueioDesbloqueio(){
    var url = $('#url').val();
    postData(url + '&motivo=' + $('#motivo').val());
    $('#bloquearModal').modal('hide');
  }

  function vf_cadastro_cse() {
    var Controles = new Array("CSE_IDENTIFICADOR", "CSE_NOME");
    var Msgs = new Array("<hl:message key='mensagem.informe.cse.identificador'/>","<hl:message key='mensagem.informe.cse.nome'/>");
    if (ValidaCamposV4(Controles, Msgs)) {
      montaListaIps('cse_ip_acesso','listaIps');
      montaListaIps('cse_ddns_acesso','listaDDNSs');

      formataEmails("CSE_EMAIL");
      formataEmails("CSE_EMAIL_FOLHA");
      formataEmails("CSE_EMAIL_VALIDAR_SERVIDOR");

      f0.submit();
    }
  }

  //field pode ser ID ou NAME
  function formataEmails(field) {

      var emailsTag = document.getElementById(field);

      if(!!emailsTag)
      {
          document.getElementById(field).value = replaceCaracteresInvalidos(emailsTag.value.trim());
      }
      else
      {
      	var emails = document.getElementsByName(field)[0];
      	if(!!emailsTag)
  		{
      		document.getElementById(field).value = replaceCaracteresInvalidos(emails.value.trim());
  		}
      }
  }

  function replaceCaracteresInvalidos(emailString)
  {
      var emails = emailString;
      var separador = ',';

      emails = replaceAll(emails,' ', separador);
      emails = replaceAll(emails,';', separador);
      emails = replaceAll(emails,'\n',separador);
      emails = replaceAll(emails,'\t',separador);

      while(emails.indexOf(",,") !== -1){
          emails = replaceAll(emails,",,",separador);
      }

      if(emails[0] === separador){    // Se o primeiro caracter é vírgula ',' então
          emails = emails.slice(1);   // remove o primeiro caracter.
      }

      if(emails.slice(-1) === separador){ // Se o último caracter é vírgula ',' então
          emails = emails.slice(0,-1);    // remove o último caracter.
      }

      return emails;
  }


  function replaceAll(str, find, replace) {
  	return str.replace(new RegExp(find, 'g'), replace);
  }

  window.onload = formLoad;

  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
