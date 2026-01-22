<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String org_codigo = (String) request.getAttribute("org_codigo");
String msgErro = (String) request.getAttribute("msgErro");
String org_ativo = (String) request.getAttribute("org_ativo");
OrgaoTransferObject orgao = (OrgaoTransferObject) request.getAttribute("orgao");
String org_nome = (String) request.getAttribute("org_nome");
List<?> estabelecimentos = (List<?>) request.getAttribute("estabelecimentos");
List<?> orgaos = (List<?>) request.getAttribute("orgaos");

boolean isOrg = (boolean) request.getAttribute("isOrg");
boolean podeEditarOrgaos = (boolean) request.getAttribute("podeEditarOrgaos");
boolean podeConsultarPerfilUsu = (boolean) request.getAttribute("podeConsultarPerfilUsu");
boolean podeConsultarUsu = (boolean) request.getAttribute("podeConsultarUsu");
boolean podeCriarUsu = (boolean) request.getAttribute("podeCriarUsu");
boolean habilitarCodigoFolha = (boolean) request.getAttribute("habilitarCodigoFolha");
boolean podeEditarEnderecoAcesso = (boolean) request.getAttribute("podeEditarEnderecoAcesso");
boolean podeConsultarParamOrgao = (boolean) request.getAttribute("podeConsultarParamOrgao");
//Exibe Botao Rodapé
boolean exibeBotaoRodape = (boolean) (request.getAttribute("exibeBotaoRodape"));
%>

<c:set var="title">
<%if (org_codigo.equals("")) {%>
  <hl:message key="rotulo.criar.orgao.titulo"/>
<%} else {%>
  <hl:message key="rotulo.editar.orgao.titulo"/>
<%}%>
</c:set>

<c:set var="imageHeader">
    <use xlink:href="#i-manutencao"></use>
</c:set>

<c:set var="bodyContent">
    <form method="post" action="../v3/editarOrgao?<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" >
      <%
      org_ativo = orgao!=null ? (orgao.getOrgAtivo() != null ? orgao.getOrgAtivo().toString() :"1"):"";
      String msgBloquearDesbloquear = "";
      String msgStatusOrg = "";
      if (org_ativo.equals("1")) {
          msgBloquearDesbloquear = ApplicationResourcesHelper.getMessage("mensagem.bloquear.orgao.clique.aqui", responsavel);
          msgStatusOrg = ApplicationResourcesHelper.getMessage("mensagem.orgao.status.desbloqueado", responsavel);
      } else {
          msgBloquearDesbloquear = ApplicationResourcesHelper.getMessage("mensagem.desbloquear.orgao.clique.aqui", responsavel);
          msgStatusOrg = ApplicationResourcesHelper.getMessage("mensagem.orgao.status.bloqueado", responsavel);                    
      }
      %>
      <div class="row">
        <%if (!org_codigo.equals("")) {%>
        <div class="col-sm-12 col-md-12 mb-2 pr-0">
          <div class="float-end">
            <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit"><hl:message key="rotulo.mais.acoes"/></button>
            <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes">
            <% if (!isOrg && podeEditarOrgaos) { %>
              <a class="dropdown-item" href="#no-back" onClick="BloquearEntidade(<%=TextHelper.forJavaScript(org_ativo)%>, '<%=TextHelper.forJavaScript(org_codigo)%>', 'ORG', '../v3/editarOrgao?acao=bloquearOrgao&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(org_nome)%>')" alt="<%=TextHelper.forHtmlAttribute(msgBloquearDesbloquear)%>" title="<%=TextHelper.forHtmlAttribute(msgBloquearDesbloquear)%>"><hl:message key="rotulo.acao.bloquear.desbloquear"/></a>
              <div class="dropdown-divider" role="separator"></div>
            <% } %>
            <% if (podeConsultarPerfilUsu) { %>
          	   <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarPerfilOrg?acao=listar&codigo=<%=TextHelper.forJavaScriptAttribute(org_codigo)%>&titulo=<%=TextHelper.forJavaScript(org_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" alt="<hl:message key="mensagem.listar.perfil.orgao.clique.aqui"/>" title="<hl:message key="mensagem.listar.perfil.orgao.clique.aqui"/>"><hl:message key="rotulo.acao.listar.perfil.usuario"/></a>
            <% } %>
            <% if (podeConsultarUsu) { %>
               <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarUsuarioOrg?acao=listar&codigo=<%=TextHelper.forJavaScriptAttribute(org_codigo)%>&titulo=<%=TextHelper.encode64(org_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" alt="<hl:message key="mensagem.listar.usuario.orgao.clique.aqui"/>" title="<hl:message key="mensagem.listar.usuario.orgao.clique.aqui"/>"><hl:message key="rotulo.usuario.plural"/></a>
            <% } %>
            <% if (podeCriarUsu) { %>
               <a class="dropdown-item" href="#no-back" onClick="postData('../v3/inserirUsuarioOrg?acao=iniciar&codigo=<%=TextHelper.forJavaScriptAttribute(org_codigo)%>&titulo=<%=TextHelper.encode64(org_nome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" alt="<hl:message key="mensagem.criar.usuario.orgao.clique.aqui"/>" title="<hl:message key="mensagem.criar.usuario.orgao.clique.aqui"/>"><hl:message key="rotulo.novo.usuario"/></a>
            <% } %>
            <% 
               String periodoEnvioEmailAudit = (String) ParamSist.getInstance().getParam(CodedValues.TPC_PERIODO_ENVIO_EMAIL_AUDITORIA_CSE_ORG, responsavel);
               if (responsavel.isCseSupOrg() && responsavel.temPermissao(CodedValues.FUN_USUARIO_AUDITOR) && (periodoEnvioEmailAudit != null && !periodoEnvioEmailAudit.equals(CodedValues.PER_ENV_EMAIL_AUDIT_DESABILITADO))) { 
            %>
               <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterFuncoesAuditaveis?acao=iniciar&codigo=<%=TextHelper.forJavaScriptAttribute(org_codigo)%>&descricao=<%=TextHelper.forJavaScript(org_nome)%>&tipo=<%=TextHelper.forJavaScript(AcessoSistema.ENTIDADE_ORG)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" alt="<hl:message key="mensagem.auditoria.orgao.clique.aqui"/>" title="<hl:message key="mensagem.auditoria.orgao.clique.aqui"/>"><hl:message key="rotulo.consignante.configurar.auditoria" /></a>
            <% } %>
            <% if (podeConsultarParamOrgao) { %>
               <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterParamOrgao?acao=iniciar&codigo=<%=TextHelper.forJavaScriptAttribute(org_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')" alt="<hl:message key="mensagem.consultar.parametro.orgao.clique.aqui"/>" title="<hl:message key="mensagem.consultar.parametro.orgao.clique.aqui"/>"><hl:message key="rotulo.consultar.parametro.orgao.opcao"/></a>
            <% } %>
            </div>
          </div>
        </div>
        <%}%>
        <div class="card">
          <div class="card-header">
            <h2 class="card-header-title"><hl:message key="rotulo.orgao.dados"/></h2>
          </div>
          <div class="card-body">
              <fieldset>
                <div class="row">
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_IDENTIFICADOR)%>">
                    <div class="form-group col-sm-12 col-md-6">
                      <label for="ORG_IDENTIFICADOR"><hl:message key="rotulo.orgao.codigo"/></label>
                      <hl:htmlinput name="ORG_IDENTIFICADOR"
                        di="ORG_IDENTIFICADOR"
                        type="text"
                        classe="form-control"
                        value="<%=TextHelper.forHtmlAttribute(orgao != null ? (orgao.getOrgIdentificador()!=null ? orgao.getOrgIdentificador() : JspHelper.verificaVarQryStr(request, \"ORG_IDENTIFICADOR\")) : \"\" )%>"
                        size="32"
                        mask="#*40"
                        others="<%=TextHelper.forHtmlAttribute( responsavel.isOrg() || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_IDENTIFICADOR, responsavel) ? "disabled" : !podeEditarOrgaos ? "disabled" : "")%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.codigo", responsavel).toLowerCase()) %>"
                      />
                      <%=JspHelper.verificaCampoNulo(request, "ORG_IDENTIFICADOR")%>
                    </div>
                  </show:showfield>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_NOME)%>">
                  <div class="form-group col-sm-12 col-md-6">
                    <label for="ORG_NOME"><hl:message key="rotulo.orgao.nome"/></label>
                    <hl:htmlinput name="ORG_NOME"
                      di="ORG_NOME"
                      type="text"
                      classe="form-control"
                      value="<%=TextHelper.forHtmlAttribute(org_nome)%>"
                      size="69"
                      mask="#*100"
                      others="<%=TextHelper.forHtmlAttribute(responsavel.isOrg() || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_NOME, responsavel) ? "disabled" : !podeEditarOrgaos ? "disabled" : "")%>"
                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.nome", responsavel).toLowerCase())%>"
                    />
                    <%=JspHelper.verificaCampoNulo(request, "ORG_NOME")%>
                  </div>
                  </show:showfield>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_ESTABELECIMENTO)%>">
                  <div class="form-group col-sm-12 col-md-6">
                    <label for="EST_CODIGO"><hl:message key="rotulo.estabelecimento.singular"/></label>
                    <select name="EST_CODIGO" id="EST_CODIGO" CLASS="form-control form-select" onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);" <%=(String)((!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_ESTABELECIMENTO, responsavel)) ? "DISABLED" : "")%>>
                      <option value="" ><hl:message key="rotulo.campo.selecione"/></option>
                    <%
                        Iterator<?> it = estabelecimentos.iterator();
                                while (it.hasNext()) {
                                  CustomTransferObject est = (CustomTransferObject)it.next();
                                  String estCodigo = est.getAttribute(Columns.EST_CODIGO).toString();
                                  String estNome = est.getAttribute(Columns.EST_NOME).toString();
                    %>
                        <option value="<%=TextHelper.forHtmlAttribute(estCodigo)%>" <%=(String)(orgao!=null ? (estCodigo.equals(orgao.getEstCodigo())?"SELECTED":""):(JspHelper.verificaVarQryStr(request, "EST_CODIGO").equals(estCodigo)?"SELECTED":""))%> ><%=TextHelper.forHtmlContent(estNome)%></option>
                    <%
                        }
                    %>
                    </select>
                    <%=JspHelper.verificaCampoNulo(request, "EST_CODIGO")%>
                  </div>
                  </show:showfield>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_CNPJ)%>">
                  <div class="form-group col-sm-12 col-md-6">
                    <label for="ORG_CNPJ"><hl:message key="rotulo.orgao.cnpj"/></label>
                    <hl:htmlinput name="ORG_CNPJ"
                      di="ORG_CNPJ"
                      type="text"
                      classe="form-control"
                      value="<%=TextHelper.forHtmlAttribute(orgao != null ? (orgao.getOrgCnpj() !=null ? orgao.getOrgCnpj() : JspHelper.verificaVarQryStr(request,\"ORG_CNPJ\")):\"\")%>"
                      mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjMask())%>"
                      size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjSize())%>"
                      maxlength="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjMaxLenght())%>"              
                      others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_CNPJ, responsavel) ? "disabled" : "")%>"
                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.cnpj", responsavel).toLowerCase())%>"
                    />
                  </div>
                  </show:showfield>
                </div>
                <div class="legend"></div>
                <div class="row">
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_RESPONSAVEL)%>">
                  <div class="form-group col-sm-4">
                    <label for="ORG_RESPONSAVEL"><hl:message key="rotulo.orgao.responsavel.1"/></label>
                    <hl:htmlinput name="ORG_RESPONSAVEL"
                                  di="ORG_RESPONSAVEL"
                                  type="text"
                                  classe="form-control"
                                  value="<%=TextHelper.forHtmlAttribute( orgao != null ? (orgao.getOrgResponsavel()!=null ? orgao.getOrgResponsavel():JspHelper.verificaVarQryStr(request,\"ORG_RESPONSAVEL\")) :\"\")%>"
                                  size="32"
                                  mask="#*100"
                                  others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_RESPONSAVEL, responsavel) ?  "disabled" : "")%>"
                                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.responsavel.1", responsavel).toLowerCase())%>"
                    />
                  </div>
                  </show:showfield>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_RESP_CARGO)%>">
                  <div class="form-group col-sm-4">
                    <label for="ORG_RESP_CARGO"><hl:message key="rotulo.orgao.cargo"/></label>
                    <hl:htmlinput name="ORG_RESP_CARGO"
                                  di="ORG_RESP_CARGO"
                                  type="text"
                                  classe="form-control"
                                  value="<%=TextHelper.forHtmlAttribute(orgao != null ? (orgao.getOrgRespCargo()!=null ? orgao.getOrgRespCargo():JspHelper.verificaVarQryStr(request,\"ORG_RESP_CARGO\")) :\"\")%>"
                                  size="32"
                                  mask="#*100"
                                  others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_RESP_CARGO, responsavel) ? "disabled" : "")%>"
                                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.cargo", responsavel).toLowerCase())%>"
                    />
                  </div>
                  </show:showfield>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_RESP_TELEFONE)%>">
                  <div class="form-group col-sm-4">
                    <label for="ORG_RESP_TELEFONE"><hl:message key="rotulo.orgao.telefone"/></label>
                    <hl:htmlinput name="ORG_RESP_TELEFONE"
                                  di="ORG_RESP_TELEFONE"
                                  type="text"
                                  classe="form-control"
                                  value="<%=TextHelper.forHtmlAttribute(orgao != null ? (orgao.getOrgRespTelefone()!=null ? orgao.getOrgRespTelefone():JspHelper.verificaVarQryStr(request,\"ORG_RESP_TELEFONE\")) :\"\")%>"
                                  size="32"
                                  mask="#*100"
                                  others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_RESP_TELEFONE, responsavel) ? "disabled" : "")%>"
                                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.telefone", responsavel).toLowerCase())%>"
                    />
                  </div>
                  </show:showfield>
                </div>
                <div class="row">
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_RESPONSAVEL_2)%>">
                  <div class="form-group col-sm-4">
                    <label for="ORG_RESPONSAVEL_2"><hl:message key="rotulo.orgao.responsavel.2"/></label>
                    <hl:htmlinput name="ORG_RESPONSAVEL_2"
                                  di="ORG_RESPONSAVEL_2"
                                  type="text"
                                  classe="form-control"
                                  value="<%=TextHelper.forHtmlAttribute(orgao != null ? (orgao.getOrgResponsavel2() != null ? orgao.getOrgResponsavel2():JspHelper.verificaVarQryStr(request,\"ORG_RESPONSAVEL_2\")):\"\")%>"
                                  size="32"
                                  mask="#*100"
                                  others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_RESPONSAVEL_2, responsavel) ? "disabled" : "")%>"
                                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.responsavel.2", responsavel).toLowerCase())%>"
                    />
                  </div>
                  </show:showfield>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_RESP_CARGO_2)%>">
                  <div class="form-group col-sm-4">
                    <label for="ORG_RESP_CARGO_2"><hl:message key="rotulo.orgao.cargo"/></label>
                    <hl:htmlinput name="ORG_RESP_CARGO_2"
                                  di="ORG_RESP_CARGO_2"
                                  type="text"
                                  classe="form-control"
                                  value="<%=TextHelper.forHtmlAttribute(orgao != null ? (orgao.getOrgRespCargo2()!=null ? orgao.getOrgRespCargo2():JspHelper.verificaVarQryStr(request,\"ORG_RESP_CARGO_2\")) :\"\")%>"
                                  size="32"
                                  mask="#*100"
                                  others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_RESP_CARGO_2, responsavel) ? "disabled" : "")%>"
                                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.cargo", responsavel).toLowerCase())%>"
                    />
                  </div>
                  </show:showfield>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_RESP_TELEFONE_2)%>">
                  <div class="form-group col-sm-4">
                    <label for="ORG_RESP_TELEFONE_2"><hl:message key="rotulo.orgao.telefone"/></label>
                    <hl:htmlinput name="ORG_RESP_TELEFONE_2"
                                  di="ORG_RESP_TELEFONE_2"
                                  type="text"
                                  classe="form-control"
                                  value="<%=TextHelper.forHtmlAttribute(orgao != null ? (orgao.getOrgRespTelefone2() != null ? orgao.getOrgRespTelefone2():JspHelper.verificaVarQryStr(request,\"ORG_RESP_TELEFONE_2\")):\"\")%>"
                                  size="32"
                                  mask="#*100"
                                  others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_RESP_TELEFONE_2, responsavel) ? "disabled" : "")%>"
                                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.telefone", responsavel).toLowerCase())%>"
                    />
                  </div>
                  </show:showfield>
                </div>
                <div class="row">
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_RESPONSAVEL_3)%>">
                  <div class="form-group col-sm-4">
                    <label for="ORG_RESPONSAVEL_3"><hl:message key="rotulo.orgao.responsavel.3"/></label>
                    <hl:htmlinput name="ORG_RESPONSAVEL_3"
                                  di="ORG_RESPONSAVEL_3"
                                  type="text"
                                  classe="form-control"
                                  value="<%=TextHelper.forHtmlAttribute(orgao != null ? (orgao.getOrgResponsavel3() != null ? orgao.getOrgResponsavel3():JspHelper.verificaVarQryStr(request,\"ORG_RESPONSAVEL_3\")):\"\")%>"
                                  size="32"
                                  mask="#*100"
                                  others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_RESPONSAVEL_3, responsavel) ? "disabled" : "")%>"
                                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.responsavel.3", responsavel).toLowerCase())%>"
                    />
                  </div>
                  </show:showfield>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_RESP_CARGO_3)%>">
                  <div class="form-group col-sm-4">
                    <label for="ORG_RESP_CARGO_3"><hl:message key="rotulo.orgao.cargo"/></label>
                    <hl:htmlinput name="ORG_RESP_CARGO_3"
                                  di="ORG_RESP_CARGO_3"
                                  type="text"
                                  classe="form-control"
                                  value="<%=TextHelper.forHtmlAttribute(orgao != null ? (orgao.getOrgRespCargo3()!=null ? orgao.getOrgRespCargo3():JspHelper.verificaVarQryStr(request,\"ORG_RESP_CARGO_3\")) :\"\")%>"
                                  size="32"
                                  mask="#*100"
                                  others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_RESP_CARGO_3, responsavel) ? "disabled" : "")%>"
                                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.cargo", responsavel).toLowerCase())%>"
                    /> 
                  </div>
                  </show:showfield>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_RESP_TELEFONE_3)%>">
                  <div class="form-group col-sm-4">
                    <label for="ORG_RESP_TELEFONE_3"><hl:message key="rotulo.orgao.telefone"/></label>
                    <hl:htmlinput name="ORG_RESP_TELEFONE_3"
                                  di="ORG_RESP_TELEFONE_3"
                                  type="text"
                                  classe="form-control"
                                  value="<%=TextHelper.forHtmlAttribute(orgao != null ? (orgao.getOrgRespTelefone3()!=null ? orgao.getOrgRespTelefone3():JspHelper.verificaVarQryStr(request,\"ORG_RESP_TELEFONE_3\")) :\"\")%>"
                                  size="32"
                                  mask="#*100"
                                  others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_RESP_TELEFONE_3, responsavel) ? "disabled" : "")%>"
                                  placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.telefone", responsavel).toLowerCase())%>"
                    />
                  </div>
                  </show:showfield>
                </div>
                <fieldset>
                  <h3 class="legend">
                    <span><hl:message key="rotulo.orgao.endereco"/></span>
                  </h3>
                  <div class="row">
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_LOGRADOURO)%>">
                    <div class="form-group col-sm-6">
                      <label for="ORG_LOGRADOURO"><hl:message key="rotulo.orgao.logradouro"/></label>
                      <hl:htmlinput name="ORG_LOGRADOURO"
                        di="ORG_LOGRADOURO"
                        type="text"
                        classe="form-control"
                        value="<%=TextHelper.forHtmlAttribute(orgao != null ? (orgao.getOrgLogradouro()!=null ? orgao.getOrgLogradouro():JspHelper.verificaVarQryStr(request,\"ORG_LOGRADOURO\")) :\"\")%>"
                        size="69"
                        mask="#*100"
                        others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_LOGRADOURO, responsavel)  ? "disabled" : "")%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.logradouro", responsavel).toLowerCase())%>"
                      />
                    </div>
                    </show:showfield>
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_NRO)%>" >
                      <div class="form-group col-sm-3">
                        <label for="ORG_NRO"><hl:message key="rotulo.orgao.numero"/></label>
                        <hl:htmlinput name="ORG_NRO"
                          di="ORG_NRO"
                          type="text"
                          classe="form-control"
                          value="<%=TextHelper.forHtmlAttribute(orgao!=null ? (orgao.getOrgNro()!=null?orgao.getOrgNro().toString():""):JspHelper.verificaVarQryStr(request, "ORG_NRO"))%>"
                          size="32"
                          mask="#D5"
                          others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_NRO, responsavel) ? "disabled" : "")%>"
                          placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.numero", responsavel).toLowerCase())%>"
                        />
                      </div>
                    </show:showfield>
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_COMPLEMENTO)%>" >
                      <div class="form-group col-sm-3">
                        <label for="ORG_COMPL"><hl:message key="rotulo.orgao.complemento"/></label>
                        <hl:htmlinput name="ORG_COMPL"
                          di="ORG_COMPL"
                          type="text"
                          classe="form-control"
                          value="<%=TextHelper.forHtmlAttribute(orgao != null ? (orgao.getOrgCompl()!=null ? orgao.getOrgCompl():JspHelper.verificaVarQryStr(request, "ORG_COMPL")) : "")%>"
                          size="32"
                          mask="#*40"
                          others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_COMPLEMENTO, responsavel) ? "disabled" : "")%>"
                          placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.complemento", responsavel).toLowerCase())%>"
                        />
                      </div>
                    </show:showfield>
                  </div>
                  <div class="row">
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_BAIRRO)%>" >
                    <div class="form-group col-sm-4">
                      <label for="ORG_BAIRRO"><hl:message key="rotulo.orgao.bairro"/></label>
                      <hl:htmlinput name="ORG_BAIRRO"
                        di="ORG_BAIRRO"
                        type="text"
                        classe="form-control"
                        value="<%=TextHelper.forHtmlAttribute( orgao != null ? (orgao.getOrgBairro()!=null ? orgao.getOrgBairro():JspHelper.verificaVarQryStr(request, "ORG_BAIRRO")) : "")%>"
                        size="32"
                        mask="#*40"
                        others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_BAIRRO, responsavel) ? "disabled" : "")%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.bairro", responsavel).toLowerCase())%>"
                      />
                    </div>
                    </show:showfield>
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_CIDADE)%>" >
                    <div class="form-group col-sm-4">
                      <label for="ORG_CIDADE"><hl:message key="rotulo.orgao.cidade"/></label>
                      <hl:htmlinput name="ORG_CIDADE"
                        di="ORG_CIDADE"
                        type="text"
                        classe="form-control"
                        value="<%=TextHelper.forHtmlAttribute(orgao != null ? (orgao.getOrgCidade()!=null ? orgao.getOrgCidade():JspHelper.verificaVarQryStr(request,\"ORG_CIDADE\")) :\"\")%>"
                        size="32"
                        mask="#*40"
                        others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_CIDADE, responsavel) ? "disabled" : "")%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.cidade", responsavel).toLowerCase())%>"
                      />
                    </div>
                    </show:showfield>
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_UF)%>" >
                    <div class="form-group col-sm-4">
                      <label for="ORG_UF"><hl:message key="rotulo.orgao.uf"/></label>
                      <hl:campoUFv4 name="ORG_UF"
                        di="ORG_UF"
                        rotuloUf="rotulo.orgao.uf"
                        valorCampo="<%=TextHelper.forHtmlAttribute(orgao != null ? (orgao.getOrgUf() != null ? orgao.getOrgUf() : JspHelper.verificaVarQryStr(request,\"ORG_UF\")) : "")%>"
                        desabilitado="<%=!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_UF, responsavel)%>"
                        classe="form-control"
                      />
                    </div>
                    </show:showfield>
                  </div>
                  <div class="row">
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_CEP)%>" >
                    <div class="form-group col-sm-4">
                      <label for="ORG_CEP"><hl:message key="rotulo.orgao.cep"/></label>
                      <hl:htmlinput name="ORG_CEP"
                          di="ORG_CEP"
                          type="text"
                          classe="form-control"
                          value="<%=TextHelper.forHtmlAttribute(orgao != null ? (orgao.getOrgCep()!=null ? orgao.getOrgCep():JspHelper.verificaVarQryStr(request, "ORG_CEP")):\"\")%>"
                          size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCepSize())%>"
                          mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCepMask())%>"
                          others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_CEP, responsavel) ? "disabled" : "")%>"
                          placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.cep", responsavel).toLowerCase())%>"
                        />
                    </div>
                    </show:showfield>
                  </div>
                </fieldset>
                <fieldset>
                  <h3 class="legend">
                    <span><hl:message key="rotulo.orgao.informacoes.contato"/></span>
                  </h3>
                  <div class="row">
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_TELEFONE)%>" >
                    <div class="form-group col-sm-4">
                      <label for="ORG_TEL"><hl:message key="rotulo.orgao.telefone"/></label>
                      <hl:htmlinput  name="ORG_TEL"
                        di="ORG_TEL"
                        type="text"
                        classe="form-control"
                        value="<%=TextHelper.forHtmlAttribute(orgao != null ? (orgao.getOrgTel()!=null ? orgao.getOrgTel():JspHelper.verificaVarQryStr(request,\"ORG_TEL\")) :\"\")%>"
                        size="32"
                        mask="<%=LocaleHelper.getMultiplosTelefonesMask()%>"
                        others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_TELEFONE, responsavel) ? "disabled" : "")%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.telefone", responsavel).toLowerCase())%>"
                      />
                    </div>
                    </show:showfield>
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_FAX)%>" >
                    <div class="form-group col-sm-4">
                      <label for="ORG_FAX"><hl:message key="rotulo.orgao.fax"/></label>
                      <hl:htmlinput name="ORG_FAX"
                        di="ORG_FAX"
                        type="text"
                        classe="form-control"
                        value="<%=TextHelper.forHtmlAttribute(orgao != null ? (orgao.getOrgFax()!=null ? orgao.getOrgFax():JspHelper.verificaVarQryStr(request,\"ORG_FAX\")) :\"\")%>"
                        size="32"
                        mask="<%=LocaleHelper.getMultiplosTelefonesMask()%>"
                        others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_FAX, responsavel) ? "disabled" : "")%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.fax", responsavel).toLowerCase())%>"
                      />
                    </div>
                    </show:showfield>
                  </div>
                  <div class="row">
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_EMAIL)%>" >
                    <div class="form-group col-sm-4">
                      <label for="iEmail"><hl:message key="rotulo.orgao.email"/></label>
                      <hl:htmlinput name="ORG_EMAIL"
                        di="ORG_EMAIL"
                        type="textarea"
                        classe="form-control"                
                        onBlur="formataEmails('ORG_EMAIL')"
                        value="<%=TextHelper.forHtmlAttribute(orgao != null ? (orgao.getOrgEmail()!=null ? orgao.getOrgEmail():JspHelper.verificaVarQryStr(request,\"ORG_EMAIL\")):\"\")%>"
                        rows="3"
                        cols="67"
                        others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_EMAIL, responsavel) ? "disabled" : "")%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.email", responsavel).toLowerCase())%>"
                      />
                    </div>
                    </show:showfield>
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_EMAIL_INTEGRA_FOLHA)%>" >
                    <div class="form-group col-sm-4">
                      <label for="ORG_EMAIL_FOLHA"><hl:message key="rotulo.orgao.email.folha"/></label>
                      <hl:htmlinput 
                        name="ORG_EMAIL_FOLHA"                
                        di="ORG_EMAIL_FOLHA"
                        onBlur="formataEmails('ORG_EMAIL_FOLHA')"
                        type="textarea"
                        classe="form-control"
                        value="<%=TextHelper.forHtmlAttribute(orgao != null ? (orgao.getOrgEmailFolha()!=null ? orgao.getOrgEmailFolha():JspHelper.verificaVarQryStr(request,\"ORG_EMAIL_FOLHA\")):\"\")%>"
                        rows="3"
                        cols="67"
                        others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_EMAIL_INTEGRA_FOLHA, responsavel) ? "disabled" : "")%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.email.folha", responsavel).toLowerCase())%>"
                      />
                    </div>
                    </show:showfield>
                    <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_EMAIL_VALIDA_SERVIDOR)%>" >
                    <div class="form-group col-sm-4">
                      <label for="ORG_EMAIL_VALIDAR_SERVIDOR"><hl:message key="rotulo.orgao.email.validar.servidor"/></label>
                      <hl:htmlinput name="ORG_EMAIL_VALIDAR_SERVIDOR"
                        di="ORG_EMAIL_VALIDAR_SERVIDOR"
                        onBlur="formataEmails('ORG_EMAIL_VALIDAR_SERVIDOR')"
                        type="textarea"
                        classe="form-control"
                        value="<%=TextHelper.forHtmlAttribute(orgao != null ? (orgao.getOrgEmailValidarServidor()!=null ? orgao.getOrgEmailValidarServidor():JspHelper.verificaVarQryStr(request,\"ORG_EMAIL_VALIDAR_SERVIDOR\")):\"\")%>"
                        rows="3"
                        cols="67"
                        others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_EMAIL_VALIDA_SERVIDOR, responsavel) ? "disabled" : "")%>"
                        placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.email.validar.servidor", responsavel).toLowerCase())%>"
                      />
                    </div>
                    </show:showfield>
                  </div>
                </fieldset>
                <div class="legend"></div>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_DIA_REPASSE)%>" >
                <div class="row">
                  <div class="form-group col-sm-6">
                    <label for="ORG_DIA_REPASSE"><hl:message key="rotulo.orgao.dia.repasse"/></label>
                    <hl:htmlinput name="ORG_DIA_REPASSE"
                      di="ORG_DIA_REPASSE"
                      type="text"
                      classe="form-control"
                      value="<%=TextHelper.forHtmlAttribute(orgao!=null ? (orgao.getOrgDiaRepasse()!= null ? orgao.getOrgDiaRepasse().toString():""):JspHelper.verificaVarQryStr(request, "ORG_DIA_REPASSE"))%>"
                      size="2"
                      mask="#D2"
                      nf="submit"
                      others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_DIA_REPASSE, responsavel) ? "disabled" : "")%>"
                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.dia.repasse", responsavel).toLowerCase())%>"
                    />
                  </div>
                </div>
                </show:showfield>
                <%
                    if (habilitarCodigoFolha) {
                %>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_COD_FOLHA)%>" >
                <div class="row">
                  <div class="form-group col-sm-6">
                    <label for="ORG_FOLHA"><hl:message key="rotulo.orgao.folha"/></label>
                    <hl:htmlinput name="ORG_FOLHA"
                      di="ORG_FOLHA"
                      type="text"
                      classe="form-control"
                      value="<%=TextHelper.forHtmlAttribute(orgao!=null ? (orgao.getOrgFolha()!= null ? orgao.getOrgFolha().toString():""):JspHelper.verificaVarQryStr(request, "ORG_FOLHA"))%>"
                      size="40"
                      nf="submit"
                      others="<%=TextHelper.forHtmlAttribute(!podeEditarOrgaos || ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_COD_FOLHA, responsavel) ? "disabled" : "")%>"
                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.folha", responsavel).toLowerCase())%>"
                    />
                  </div>
                </div>
                </show:showfield>
               <%
                   }
               %>
                <div class="legend"></div>
              <%
                  if (!ParamSist.paramEquals(CodedValues.TPC_PERMITE_BLOQUEIO_ACESSO_POR_IP, CodedValues.TPC_NAO, responsavel)) {
              %>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_IP_ACESSOS)%>" >
                <jsp:include page="../manterEntidades/incluirCampoAcesso_v4.jsp">
                  <jsp:param name="tipo_endereco" value="numero_ip"/>
                  <jsp:param name="nome_campo" value="novoIp"/>
                  <jsp:param name="nome_lista" value="listaIps"/>
                  <jsp:param name="lista_resultado" value="org_ip_acesso"/>
                  <jsp:param name="label" value="rotulo.usuario.ips.acesso"/>
                  <jsp:param name="mascara" value="#I30"/>
                  <jsp:param name="pode_editar" value="<%=(boolean)podeEditarEnderecoAcesso && !ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_IP_ACESSOS, responsavel)%>"/>              
                  <jsp:param name="bloquear_ip_interno" value="false"/> 
                  <jsp:param name="placeHolder" value="mensagem.placeholder.digite.ip.acesso"/>
                </jsp:include>
                </show:showfield>
              <%
                  }
              %>
              <%
                  if (!ParamSist.paramEquals(CodedValues.TPC_PERMITE_BLOQUEIO_ACESSO_POR_DNS, CodedValues.TPC_NAO, responsavel)) {
              %>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_ORG_DDNS_ACESSOS)%>" >
                <jsp:include page="../manterEntidades/incluirCampoAcesso_v4.jsp">
                  <jsp:param name="tipo_endereco" value="url"/>
                  <jsp:param name="nome_campo" value="novoDDNS"/>
                  <jsp:param name="nome_lista" value="listaDDNSs"/>
                  <jsp:param name="lista_resultado" value="org_ddns_acesso"/>
                  <jsp:param name="label" value="rotulo.usuario.enderecos.acesso"/>
                  <jsp:param name="mascara" value="#*100"/>
                  <jsp:param name="pode_editar" value="<%=(boolean)podeEditarEnderecoAcesso && !ShowFieldHelper.isDisabled(FieldKeysConstants.EDITAR_ORG_DDNS_ACESSOS, responsavel)%>"/> 
                  <jsp:param name="placeHolder" value="mensagem.placeholder.digite.endereco.acesso"/>             
                </jsp:include>
                </show:showfield>
              <% } %>
              </fieldset>
          </div>
        </div>
        <%if(org_codigo == null || org_codigo.equals("")) { %>
        <div class="col-sm-12">
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.criar.orgao.convenio.subtitulo"/></h2>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="form-group col-sm">
                  <label for="cria_convenio"><hl:message key="rotulo.orgao.criar.convenios"/></label>
                  <input class="form-check-input ml-1" type="checkbox" name="cria_convenio" id="cria_convenio" onclick="atribuiValor();">
                </div>
              </div>
              <div class="row">
                <div class="form-group col-sm-6">
                  <label for="copia_cnv"><hl:message key="rotulo.orgao.copiar.convenios"/></label>
                  <select class="form-control form-select" id="copia_cnv" name="copia_cnv" onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                    <option value="" selected><hl:message key="mensagem.orgao.selecione.copia.convenio"/></option>
                  <%
                  Iterator<?> itOrg = orgaos.iterator();
                  CustomTransferObject orgTO = null;
                  while(itOrg.hasNext()) {
                      orgTO = (CustomTransferObject)itOrg.next();%>
                    <option value="<%=TextHelper.forHtmlAttribute((orgTO.getAttribute(Columns.ORG_CODIGO)))%>"><%=TextHelper.forHtml((orgTO.getAttribute(Columns.ORG_IDENTIFICADOR)))%> - <%=TextHelper.forHtml((orgTO.getAttribute(Columns.ORG_NOME)))%></option>
                <%}%>
                  </select>
                </div>
              </div>
            </div>
          </div>
        </div>
        <%}%>
      </div>
      <div id="actions" class="btn-action">
      <% if(podeEditarOrgaos || podeEditarEnderecoAcesso){%>
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
        <a class="btn btn-primary" href="#no-back" onClick="montaListaIps('org_ip_acesso','listaIps'); montaListaIps('org_ddns_acesso','listaDDNSs'); habilitaCampos(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
      <% } else { %>
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
      <% } %>
      </div>
    <% if (org_codigo!=null) { %>
      <hl:htmlinput name="ORG_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(org_codigo)%>" size="32"/>
      <hl:htmlinput name="acao" type="hidden" value="<%= podeEditarOrgaos ? "editarOrgao" : (podeEditarEnderecoAcesso ? "editarIp" : "consultarOrgao")%>" size="32"/>
      <hl:htmlinput name="org" type="hidden" value="<%=TextHelper.forHtmlAttribute(org_codigo)%>" size="32"/>
      <hl:htmlinput name="ip_list"  di="ip_list" type="hidden" value="<%=TextHelper.forHtmlAttribute(orgao!=null && orgao.getOrgIPAcesso() != null ? orgao.getOrgIPAcesso() : "" )%>" />
      <hl:htmlinput name="ddns_list" di="ddns_list" type="hidden" value="<%=TextHelper.forHtmlAttribute(orgao!=null && orgao.getOrgDDNSAcesso() != null ? orgao.getOrgDDNSAcesso() : "" )%>" />
    <% } %>
      <hl:htmlinput name="MM_update" type="hidden" value="form1"/>
    </form>
  <% if (exibeBotaoRodape) { %>
	<div id="btns">
	  <a id="page-up" onclick="up()">
        <svg xmlns="http://www.w3.org/2000/svg" width="147.344" height="147.344" viewBox="0 0 147.344 147.344">
		  <path id="União_3" data-name="União 3" d="M-20,60.672a73.672,73.672,0,1,1,73.672,73.672A73.66,73.66,0,0,1-20,60.672ZM9.61,16.61a62.252,62.252,0,0,0,0,88.124,62.252,62.252,0,0,0,88.124,0,62.252,62.252,0,0,0,0-88.124,62.252,62.252,0,0,0-88.124,0ZM49.174,88.087,15.278,54.3a6.85,6.85,0,0,1,9.67-9.706L53.672,73.6,82.362,45.336A6.825,6.825,0,1,1,92,55.005L58.6,88.3a6.827,6.827,0,0,1-9.421-.212Z" transform="translate(20 13)"/>
		</svg>              
	  </a>
	  <a id="page-down" onclick="down()">
        <svg xmlns="http://www.w3.org/2000/svg" width="147.344" height="147.344" viewBox="0 0 147.344 147.344">
		  <path id="União_3" data-name="União 3" d="M-20,60.672a73.672,73.672,0,1,1,73.672,73.672A73.66,73.66,0,0,1-20,60.672ZM9.61,16.61a62.252,62.252,0,0,0,0,88.124,62.252,62.252,0,0,0,88.124,0,62.252,62.252,0,0,0,0-88.124,62.252,62.252,0,0,0-88.124,0ZM49.174,88.087,15.278,54.3a6.85,6.85,0,0,1,9.67-9.706L53.672,73.6,82.362,45.336A6.825,6.825,0,1,1,92,55.005L58.6,88.3a6.827,6.827,0,0,1-9.421-.212Z" transform="translate(20 13)"/>
		</svg>
	  </a>
	  <a id="page-actions" onclick="toActionBtns()">
		<svg xmlns="http://www.w3.org/2000/svg" width="145.344" height="145.344" viewBox="0 0 145.344 145.344">
		  <path id="União_1" data-name="União 1" d="M-20,59.672a72.672,72.672,0,1,1,72.671,72.672A72.671,72.671,0,0,1-20,59.672Zm10.164,0A62.508,62.508,0,1,0,52.672-2.836,62.579,62.579,0,0,0-9.836,59.672Zm82.6,40.182H24.545A12.069,12.069,0,0,1,12.49,87.8V31.544A12.069,12.069,0,0,1,24.545,19.49h44.2a4.014,4.014,0,0,1,2.841,1.177L91.678,40.757A4.019,4.019,0,0,1,92.855,43.6V87.8A12.069,12.069,0,0,1,80.8,99.854Zm0-40.182a4.018,4.018,0,0,1,4.019,4.018V91.817H80.8A4.023,4.023,0,0,0,84.818,87.8V45.263L67.081,27.526H36.6V39.58H64.727a4.019,4.019,0,0,1,0,8.037H32.581A4.018,4.018,0,0,1,28.563,43.6V27.526H24.545a4.023,4.023,0,0,0-4.018,4.019V87.8a4.023,4.023,0,0,0,4.018,4.018h4.019V63.689a4.018,4.018,0,0,1,4.018-4.018ZM36.6,91.817H68.745V67.708H36.6Z" transform="translate(20 13)"/>
		</svg>
	  </a>
	</div>
  <% }%>
</c:set>

<c:set var="javascript">
<script type="text/JavaScript" src="../js/listagem.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript">
var f0 = document.forms[0];
function formLoad() {
  focusFirstField();
  if (document.form1.copia_cnv != null) {
    document.form1.copia_cnv.disabled=true;
  }
}

function habilitaCampos() {
 if (vf_cadastro_org()) {
   enableAll();
   
   formataEmails('ORG_EMAIL');
   formataEmails('ORG_EMAIL_FOLHA');
   formataEmails('ORG_EMAIL_VALIDAR_SERVIDOR');
   
   f0.submit();
 }
}

function atribuiValor() {
   if (document.form1.cria_convenio.checked == true) {
     document.form1.cria_convenio.value='S';  
     document.form1.copia_cnv.disabled=false;
   } else {
     document.form1.cria_convenio.value='N';
     document.form1.copia_cnv.disabled=true;
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
      var emails = document.getElementsByName(field)[0].value;    
      if(!!emails)
    {           
        emails = replaceCaracteresInvalidos(emails.trim());
        document.getElementsByName(field)[0].value = emails;        
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
<script>
	let btnDown = document.querySelector('#btns');
	const pageActions = document.querySelector('#page-actions');
	const pageSize = document.body.scrollHeight;
	
	function up(){
		window.scrollTo({
			top: 0,
			behavior: "smooth",
		});
	}
	
	function down(){
		let toDown = document.body.scrollHeight;
		window.scrollBy({
			top: toDown,
			behavior: "smooth",
		});
	}

	function toActionBtns(){
		let save = document.querySelector('#actions').getBoundingClientRect().top;
		window.scrollBy({
			top: save,
			behavior: "smooth",
		});
	}
	
	function btnTab(){
	    let scrollSize = document.documentElement.scrollTop;
	    
	    if(scrollSize >= 300){
		    btnDown.classList.add('btns-active');    
	    } else {
		    btnDown.classList.remove('btns-active');
	    }
	}
	

	window.addEventListener('scroll', btnTab);
</script>
</c:set>

<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
