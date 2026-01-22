<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TransferObjectHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.values.NaturezaConsignatariaEnum"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String csaIdentificadorInterno = (String) request.getAttribute("csaIdentificadorInterno");
boolean podeEditarCsa = (Boolean) request.getAttribute("podeEditarCsa");
boolean podeEditarEnderecoAcesso = (Boolean) request.getAttribute("podeEditarEnderecoAcesso");
boolean podeEditarEnderecosCsa = (Boolean) request.getAttribute("podeEditarEnderecosCsa");
String csaCodigo = (String) request.getAttribute("csaCodigo");
String csaAtivo = (String) request.getAttribute("csaAtivo");
String msgErro = (String) request.getAttribute("msgErro");
String csaNome = (String) request.getAttribute("csaNome");
boolean temCET = (Boolean) request.getAttribute("temCET");
boolean podeEditarContrato = (Boolean) request.getAttribute("podeEditarContrato");
boolean podeVisualizarContrato = (Boolean) request.getAttribute("podeVisualizarContrato");
boolean podeConsultarPerfilUsu = (Boolean) request.getAttribute("podeConsultarPerfilUsu");
boolean podeConsultarUsu = (Boolean) request.getAttribute("podeConsultarUsu");
boolean podeCriarUsu = (Boolean) request.getAttribute("podeCriarUsu");
boolean podeEditarParamCsa = (Boolean) request.getAttribute("podeEditarParamCsa");
String cancelar = (String) request.getAttribute("cancelar");
ConsignatariaTransferObject consignataria = (ConsignatariaTransferObject) request.getAttribute("consignataria");
boolean permiteCadIpInternoCsaCor = (Boolean) request.getAttribute("permiteCadIpInternoCsaCor");
String paramVrfIpAcesso = (String) request.getAttribute("paramVrfIpAcesso");
List<?> lstOcorrencias = (List<?>) request.getAttribute("lstOcorrencias");
String csa_ip_acesso = (String) request.getAttribute("csa_ip_acesso");
String btnCancelar = (String) request.getAttribute("btnCancelar");
String csa_ddns_acesso = (String) request.getAttribute("csa_ddns_acesso");
String csa_nome_abrev_script = (String) request.getAttribute("csa_nome_abrev_script");
String csa_ativo = (String) request.getAttribute("csa_ativo");
String mensagemDesbloqueio = (String) request.getAttribute("mensagemDesbloqueio");
List<?> lstGrupoConsignataria = (List<?>) request.getAttribute("lstGrupoConsignataria");
List<?> lstNatureza = (List<?>) request.getAttribute("lstNatureza");
boolean habilitaCampoCsaCodigoAns = (boolean) request.getAttribute("habilitaCampoCsaCodigoAns");
boolean possibilitaCredenciamento = request.getAttribute("possibilitaCredenciamento") != null;
String podeApi = request.getAttribute("habilitaApi") != null ? (String) request.getAttribute("habilitaApi") : "N";
int permiteConsultarReservarPortabilidadeCodigo = (int) request.getAttribute("permiteConsultarReservarPortabilidadeCodigo");

//Exibe Botao Rodapé
boolean exibeBotaoRodape = (boolean) (request.getAttribute("exibeBotaoRodape"));

boolean podeNotificarCsaAlteracaoRegraConvenio = (Boolean) request.getAttribute("podeNotificarCsaAlteracaoRegraConvenio");
%>
<c:set var="title">
<% if (TextHelper.isNull(csaCodigo)) { %>
   <hl:message key="rotulo.criar.consignataria.titulo"/>
<% } else { %>
   <hl:message key="rotulo.manutencao.consignataria.titulo"/>
<% } %>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <% if (!TextHelper.isNull(csaCodigo)) { %>
    <div class="page-title d-print-none">
      <div class="row">
        <div class="col-sm-12 col-md-12 mb-2">
          <div class="float-end">
            <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit"><hl:message key="rotulo.acoes"/></button>
            <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes">
              <% if (responsavel.isCseSupOrg() && podeEditarCsa) { %>
                 <a class="dropdown-item" href="#no-back" onClick="BloquearEntidade(<%=TextHelper.forJavaScript(csa_ativo)%>, '<%=TextHelper.forJavaScript(csaCodigo)%>', 'CSA', '../v3/manterConsignataria?acao=bloquear&csa_nome=<%=TextHelper.forJavaScript(csaNome)%>&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(csa_nome_abrev_script )%>', '<%=TextHelper.forJavaScript(mensagemDesbloqueio != null ? mensagemDesbloqueio : "")%>')"><hl:message key="rotulo.acao.bloquear.desbloquear"/></a>
              <% } %>
              <% if (responsavel.temPermissao(CodedValues.FUN_EDT_COEFICIENTES) && 
                     ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel) && 
                     podeEditarCsa) { %>
                 <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarServicosCoeficiente?acao=editarCoeficiente&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&titulo=<%=TextHelper.forJavaScript(csaNome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.editar.coeficientes"/></a>

              <% } else if (responsavel.temPermissao(CodedValues.FUN_CONS_COEFICIENTES) && 
                            ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel) && 
                            podeEditarCsa) { %>
                 <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarServicosCoeficiente?acao=consultarCoeficiente&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&titulo=<%=TextHelper.forJavaScript(csaNome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.editar.coeficientes"/></a>
              <% } else if (responsavel.temPermissao(CodedValues.FUN_TAXA_JUROS) && podeEditarCsa) { %>
                 <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarServicos?acao=editarTaxa&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&titulo=<%=TextHelper.forJavaScript(csaNome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.editar.cet.plural"/></a>
              <% } else if (responsavel.temPermissao(CodedValues.FUN_CONS_TAXA_JUROS) && podeEditarCsa) { %>
                 <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarServicos?acao=consultarTaxa&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&titulo=<%=TextHelper.forJavaScript(csaNome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.editar.cet.plural"/></a>
              <% } %>
              <% if (responsavel.temPermissao(CodedValues.FUN_EDITAR_REGRAS_TAXA_DE_JUROS)) { %>
                 <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarRegraTaxaJuros?acao=iniciar&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&titulo=<%=TextHelper.forJavaScript(csaNome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.regra.taxa.juros.editar.titulo"/></a>
              <% } %>
              <% if (podeConsultarPerfilUsu) { %>
                 <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarPerfilCsa?acao=listar&codigo=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&titulo=<%=TextHelper.forJavaScript(csaNome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acao.listar.perfil.usuario"/></a>
              <% } %>
              <% if (podeEditarCsa && responsavel.isCsa()) { %>
                 <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterConsignataria?acao=listarServicos&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&titulo=<%=TextHelper.forJavaScript(csaNome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.editar.param.svc.csa"/></a>
              <% } %>
              <% if (podeConsultarUsu) { %>
                 <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarUsuarioCsa?acao=listar&codigo=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&titulo=<%=TextHelper.encode64(csa_nome_abrev_script)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.usuario.plural"/></a>
              <% } %>
              <% if (podeCriarUsu) { %>
                 <a class="dropdown-item" href="#no-back" onClick="postData('../v3/inserirUsuarioCsa?acao=iniciar&codigo=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&titulo=<%=TextHelper.encode64(csa_nome_abrev_script)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.novo.usuario"/></a>
              <% } %>
              <%
                 if ((responsavel.isCseSup() || responsavel.isCsa()) && podeEditarParamCsa) {                   
              %>
                 <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarParamConsignataria?acao=iniciar&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&titulo=<%=TextHelper.forJavaScript(csaNome)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.consignataria.editar.parametros.abrev"/></a>
              <% } %>
              <% if (!responsavel.isOrg() && ParamSist.paramEquals(CodedValues.TPC_TEM_TERMO_ADESAO, CodedValues.TPC_SIM, responsavel) && podeEditarCsa) { %>
                 <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterTermoAdesaoServico?acao=listarServicos&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>;<%=TextHelper.forJavaScript(csaNome)%>&cancelar=<%=TextHelper.forJavaScript(cancelar)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.editar.termos.adesao"/></a>
              <% } %>
              <% 
                 String periodoEnvioEmailAudit = (String) ParamSist.getInstance().getParam(CodedValues.TPC_PERIODO_ENVIO_EMAIL_AUDITORIA_CSA_COR, responsavel);
                 if (responsavel.isCsa() && responsavel.temPermissao(CodedValues.FUN_USUARIO_AUDITOR) && (periodoEnvioEmailAudit != null && !periodoEnvioEmailAudit.equals(CodedValues.PER_ENV_EMAIL_AUDIT_DESABILITADO))) { %>
                 <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterFuncoesAuditaveis?acao=iniciar&codigo=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&descricao=<%=TextHelper.forJavaScript(csaNome)%>&tipo=<%=TextHelper.forJavaScript(AcessoSistema.ENTIDADE_CSA)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.configurar.auditoria"/></a>
              <% } %>

              <% if (podeEditarEnderecosCsa) {%>
                  <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterEnderecosConsignataria?acao=iniciar&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&titulo=<%=TextHelper.forJavaScript(csa_nome_abrev_script)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.consignataria.editar.enderecos"/></a>
              <% } %>
              <% if ((responsavel.isSup() || responsavel.isCsa()) && responsavel.temPermissao(CodedValues.FUN_EDT_LIMITE_MARGEM_CSA_ORG)) {%>
                  <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterConsignataria?acao=edtLimiteMargemCsaOrg&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&skip_history=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.consignataria.acoes.edt.limita.margem"/></a>
              <% } %>
              <% if(ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_MODULO_CREDENCIAMENTO_CSA, responsavel) && (responsavel.isSup() || responsavel.isCse()) && responsavel.temPermissao(CodedValues.FUN_INICIA_PROCESSO_CREDENCIAMENTO)){ %>
                  <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterConsignataria?acao=iniciarCredenciamentoCsa&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&skip_history=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.consignataria.iniciar.credenciamento"/></a>
              <%} %>
              <% if ((responsavel.isCseSup() || responsavel.isCsa()) && responsavel.temPermissao(CodedValues.FUN_EDITAR_PARAM_POSTO_GRADUACAO)) { %>
                  <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterConsignataria?acao=editarVlrPostoFixo&POSTO_VOLTA=N&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="potulo.editar.valor.fixo.posto.csa.svc"/></a>
              <% } %>
              <% if (responsavel.temPermissao(CodedValues.FUN_CONSULTAR_FUNCOES_ENVIO_EMAIL)) { %>
                  <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarFuncoesEnvioEmail?acao=iniciar&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acao.funcoes.envio.email"/></a>
              <% } %>
                <% if(responsavel.temPermissao(CodedValues.FUN_UPLOAD_ANEXO_CSA) || responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNATARIA) || responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNATARIAS)){ %>
                <a class="dropdown-item" href="#no-back" onclick="postData('../v3/manterConsignataria?acao=uploadDownload&csaCodigo=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acao.funcoes.editar.anexo.csa"/></a>
                <% } %>
                <% if(responsavel.isCsa() || responsavel.isSup() && responsavel.temPermissao(CodedValues.FUN_VINCULO_CSA_RSE)){ %>
                <a class="dropdown-item" href="#no-back" onclick="postData('../v3/manterVinculoCsaRse?acao=iniciar&csaCodigo=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.vinculo.csa.rse"/></a>
                <% } %>
                <% if((responsavel.isCsa() || responsavel.isCseSup()) && responsavel.temPermissao(CodedValues.FUN_EDT_CNV_REG_SERVIDOR)){ %>
                <a class="dropdown-item" href="#no-back" onclick="postData('../v3/listarConvenioServidor?acao=bloqueios&csaCodigo=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.folha.lista.convenios.bloqueados.acao"/></a>
                <% } %>
                <div class="dropdown-divider" role="separator"></div>
              <a class="dropdown-item" href="#no-back" onClick="imprime();"><hl:message key="rotulo.botao.imprimir"/></a>
            </div>
          </div>
        </div>
      </div>
    </div>
  <% } %>
  <div class="firefox-print-fix">
     <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.editar.grid"/></h2>
        </div>
        <div class="card-body">
           <form class="needs-validation" method="post" action="../v3/manterConsignataria?acao=salvar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" >
           <hl:htmlinput name="_skip_history_" di="_skip_history_" type="hidden" value="true"/>
              <input type="hidden" name="tipo" value="<%= podeEditarCsa ? "editar" : (podeEditarEnderecoAcesso ? "edt_ip" : "consultar") %>">
              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_IDENTIFICADOR)%>">
              <div class="row">
                <div class="form-group col-sm-2">
                  <label for="CSA_IDENTIFICADOR"><hl:message key="rotulo.consignataria.codigo"/></label>
                  <hl:htmlinput name="CSA_IDENTIFICADOR"
                                di="CSA_IDENTIFICADOR"
                                type="text"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaIdentificador():JspHelper.verificaVarQryStr(request, \"CSA_IDENTIFICADOR\"))%>"                                
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.correspondente.digite.codigo", responsavel)%>"
                                others="<%=TextHelper.forHtmlAttribute( responsavel.isCsa() ? "disabled" : !podeEditarCsa ? "disabled" : "") + " required"%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_IDENTIFICADOR)%>"
                  />
                  <%=JspHelper.verificaCampoNulo(request, "CSA_IDENTIFICADOR")%>
                </div>
                <div class="invalid-feedback">
                  <hl:message key="mensagem.informe.csa.identificador"/>
                </div>

                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_NOME)%>">
                <div class="form-group col-sm-6">
                  <label for="CSA_NOME"><hl:message key="rotulo.consignataria.nome"/></label>
                  <hl:htmlinput name="CSA_NOME"
                                di="CSA_NOME"
                                type="text"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaNome():JspHelper.verificaVarQryStr(request, \"CSA_NOME\"))%>"
                                size="100"
                                mask="#*100"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.nome", responsavel)%>"
                                others="<%=TextHelper.forHtmlAttribute(responsavel.isCsa() || !podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_NOME)%>"
                  />
                  <%=JspHelper.verificaCampoNulo(request, "CSA_NOME")%>
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_NOME_ABREV)%>">
                <div class="form-group col-sm-4">
                  <label for="CSA_NOME_ABREV"><hl:message key="rotulo.consignataria.nome.abreviado"/></label>
                  <hl:htmlinput name="CSA_NOME_ABREV"
                                di="CSA_NOME_ABREV"
                                type="text"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? (consignataria.getCsaNomeAbreviado()==null?\"\":consignataria.getCsaNomeAbreviado()):JspHelper.verificaVarQryStr(request,\"CSA_NOME_ABREV\"))%>"
                                size="32"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.nome.abreviado", responsavel)%>"
                                mask="#*100"
                                others="<%=TextHelper.forHtmlAttribute(responsavel.isCsa() || !podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_NOME_ABREV)%>"
                  />
                </div>
                </show:showfield>
              </div>
              <div class="row">
              <%
                  if (!lstNatureza.isEmpty()) {
                        boolean habilitaModuloSdp = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_SDP, CodedValues.TPC_SIM, responsavel);
                        CustomTransferObject ctoNaturezaConsignataria = new CustomTransferObject();
                        // Pega codigo da consignatária
                        String ncaNaturezaConsignataria = (consignataria != null && consignataria.getCsaNcaNatureza() != null) ? consignataria.getCsaNcaNatureza() : "";
                        String ncaCodigo, ncaDescricao = null;
              %>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_NCA_CODIGO)%>">
                     <div class="form-group col-sm-6">
                       <label for="NCA_CODIGO"><hl:message key="rotulo.consignataria.natureza"/></label>
                       <select class="form-control form-select" id="NCA_CODIGO" name="NCA_CODIGO" onChange="validaNaturezaParaCodigoAns(this.value); setarMascaraCodInterno(this.value);" required>
                          <OPTION VALUE=""><hl:message key="rotulo.campo.selecione"/></OPTION>
                          <%
                              Iterator<?> itNaturezaConsignataria = lstNatureza.iterator();
                                               while(itNaturezaConsignataria.hasNext()){
                                                 ctoNaturezaConsignataria = (CustomTransferObject)itNaturezaConsignataria.next();
                                                 ncaCodigo = ctoNaturezaConsignataria.getAttribute(Columns.NCA_CODIGO).toString();
                                                 ncaDescricao  = ctoNaturezaConsignataria.getAttribute(Columns.NCA_DESCRICAO).toString();
                                                 if (!habilitaModuloSdp && ncaCodigo.equals(NaturezaConsignatariaEnum.PREFEITURA_AERONAUTICA.getCodigo())) {
                                                    // Só exibe opção de natureza de Prefeitura de AER se tiver módulo de SDP habilitado
                                                    continue;
                                                 }
                          %>
                             <OPTION VALUE="<%=TextHelper.forHtmlAttribute(ncaCodigo)%>" <%=(String)(ncaCodigo.equals(ncaNaturezaConsignataria) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(ncaDescricao)%></OPTION>
                          <%
                              }
                          %>
                       </select>
                     </div>
                 </show:showfield>
              <%
                  }
              %>
                <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.EDITAR_CSA_CODIGO_ANS)%>" >
                <div class="form-group col-sm-2">
                  <label for="CSA_CODIGO_ANS"><hl:message key="rotulo.consignataria.codigo.consignataria.ans"/></label>

                     <hl:htmlinput name="CSA_CODIGO_ANS"
                                   type="text"
                                   di="CSA_CODIGO_ANS"
                                   classe="form-control"
                                   placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.codigo.consignataria.ans", responsavel)%>"
                                   value="<%=TextHelper.forHtmlAttribute(consignataria != null ? (consignataria.getCsaCodigoAns() != null ? consignataria.getCsaCodigoAns() : "") : "")%>"
                                   size="15"
                                   mask="#D15"
                                   others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa || !habilitaCampoCsaCodigoAns ? "disabled" : "")%>"
                                   configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_CODIGO_ANS)%>"
                     />
                </div>
                </show:showfield>
               </div>
              </show:showfield>
              <%
                  if (!responsavel.isCsa() && ParamSist.paramEquals(CodedValues.TPC_GRUPO_CONSIGNATARIA, CodedValues.TPC_SIM, responsavel)) {
              %>
              <%
                  // Define variaveis de grupo
                        String tgcCodigo, tgcDescricao, tgcIdentificador;

                        // Pega codigo da consignatária
                        String tgcCodigoConsignataria = (consignataria != null && consignataria.getTgcCodigo() != null) ? consignataria.getTgcCodigo() : "";

                        if (!lstGrupoConsignataria.isEmpty()) {
                           CustomTransferObject ctoGrupoConsignataria = new CustomTransferObject();
              %>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_TGC_CODIGO)%>">
                  <div class="row">
                    <div class="form-group col-sm-6">
                      <label for="TGC_CODIGO"><hl:message key="rotulo.editar.csa.grupo.csa"/></label>
                      <select class="form-control form-select" id="TGC_CODIGO" name="TGC_CODIGO">
                        <option value=""><hl:message key="rotulo.campo.selecione"/></option>
                        <%
                            Iterator<?> itGrpConsignataria = lstGrupoConsignataria.iterator();
                                           while(itGrpConsignataria.hasNext()){
                                             ctoGrupoConsignataria = (CustomTransferObject)itGrpConsignataria.next();
                                             tgcCodigo = ctoGrupoConsignataria.getAttribute(Columns.TGC_CODIGO).toString();
                                             tgcDescricao  = ctoGrupoConsignataria.getAttribute(Columns.TGC_DESCRICAO).toString();
                                             tgcIdentificador = ctoGrupoConsignataria.getAttribute(Columns.TGC_IDENTIFICADOR).toString();
                        %>
                             <OPTION VALUE="<%=TextHelper.forHtmlAttribute(tgcCodigo)%>" <%=(String)(tgcCodigoConsignataria.equals(tgcCodigo) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(tgcDescricao)%></OPTION>
                        <%
                            }
                        %>
                      </select>
                    </div>
                  </div>
                  </show:showfield>
              <%
                  }
                      }
              %>
              <%
                  if (responsavel.isSup()) {
              %>
              <div class="row">
                <div class="form-group col-sm-6">
                  <label for="CSA_EMAIL_DESBLOQUEIO"><hl:message key="rotulo.consignataria.email.notificacao.esbloqueio"/></label>
                  <hl:htmlinput name="CSA_EMAIL_DESBLOQUEIO"
                                di="CSA_EMAIL_DESBLOQUEIO"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.email.notificacao.desbloqueio.csa", responsavel)%>"
                                type="textarea"
                                rows="3"
                                cols="67"   
                                onBlur="formataEmails('CSA_EMAIL_DESBLOQUEIO');"                             
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaEmailDesbloqueio(): JspHelper.verificaVarQryStr(request,\"CSA_EMAIL_DESBLOQUEIO\"))%>"
                                size="69"
                                mask="#*100"
                                others="<%=!podeEditarCsa ? "disabled" : ""%>"
                  />
                </div>
              </div>
              <% } %>

              <%
                  if (responsavel.isCseSup()) {
              %>
                <%
                    Short csaStatus = (consignataria != null && consignataria.getCsaAtivo() != null) ? consignataria.getCsaAtivo() : CodedValues.STS_ATIVO;
                %>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_STATUS)%>">
                  <div class="row">
                    <div class="form-group col-sm-6">
                      <label for="CSA_ATIVO"><hl:message key="rotulo.consignataria.status"/></label>
                      <select name="CSA_ATIVO" id="CSA_ATIVO" class="form-control form-select">
                        <option value="<%=CodedValues.STS_INATIVO%>" <%=csaStatus.equals(CodedValues.STS_INATIVO) ? " selected" : ""%>><hl:message key="rotulo.status.inativo"/></option>
                        <option value="<%=CodedValues.STS_ATIVO%>"   <%=csaStatus.equals(CodedValues.STS_ATIVO)   ? " selected" : ""%>><hl:message key="rotulo.status.ativo"/></option>
                        <%
                            if (csaStatus.equals(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA)) {
                        %>
                        <option value="<%=CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA%>"  selected><hl:message key="rotulo.status.bloqueado.seguranca"/></option>
                        <%
                            }
                        %>
                      </select>
                    </div>
                  </div>
                </show:showfield>
              <%
                  }
              %>

              <%
                      if (responsavel.isCseSup()) { 
                          String permiteIncluirAde = consignataria != null && !TextHelper.isNull(consignataria.getCsaPermiteIncluirAde()) ? consignataria.getCsaPermiteIncluirAde() : "S";
                  %>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_PERMITE_INCLUIR_ADE)%>">
               <div class="row">
                   <div class="col-sm-6 col-md-6 mb-2">
                       <div class="form-group mb-0">
                           <span><hl:message key="rotulo.consignataria.permite.incluir.ade"/></span>
                       </div>
                           <div class="form-check form-check-inline" role="radiogroup" aria-labelledby="permiteIncluirAde">
                               <input class="form-check-input ml-1" type="radio" name="CSA_PERMITE_INCLUIR_ADE"
                                      id="permiteIncluirAdeSim" <%=(String)(permiteIncluirAde.equals("S") ? "checked" : "")%>
                                      value="S">
                               <label class="form-check-label pr-3" for="permiteIncluirAdeSim">
                                   <span class="text-nowrap align-text-top"><hl:message
                                           key="rotulo.consignataria.permite.incluir.ade.sim"/></span>
                               </label>
                           </div>
                           <div class="form-check form-check-inline">
                               <input class="form-check-input ml-1" type="radio" name="CSA_PERMITE_INCLUIR_ADE"
                                      id="permiteIncluirAdeNao" <%=(String)(permiteIncluirAde.equals("N") ? "checked" : "")%>
                                      value="N">
                               <label class="form-check-label" for="permiteIncluirAdeNao">
                                   <span class="text-nowrap align-text-top"><hl:message
                                           key="rotulo.consignataria.permite.incluir.ade.nao"/></span>
                               </label>
                           </div>
                       </div>
                   </div>
                </show:showfield>
              <%
                  }
              %>
              <% if (permiteConsultarReservarPortabilidadeCodigo != 0 && responsavel.isSup()) { %>
                  <div class="row">
                   <div class="col-sm-6 col-md-6 mb-2">
                       <div class="form-group mb-0">
                           <span><hl:message key="rotulo.consignataria.consultar.margem.sem.senha"/></span>
                       </div>
                           <div class="form-check form-check-inline" role="radiogroup" aria-labelledby="csaConsultaMargemSemSenha">
                               <input class="form-check-input ml-1" type="radio" name="CSA_CONSULTA_MARGEM_SEM_SENHA"
                                      id="csaConsultaMargemSemSenhaSim" <%=(consignataria != null ? consignataria.getCsaConsultaMargemSemSenha().equals("S") ? "checked" : "" : "")%>
                                      value="S">
                               <label class="form-check-label pr-3" for="csaConsultaMargemSemSenhaSim">
                                   <span class="text-nowrap align-text-top"><hl:message
                                           key="rotulo.sim"/></span>
                               </label>
                           </div>
                           <div class="form-check form-check-inline">
                               <input class="form-check-input ml-1" type="radio" name="CSA_CONSULTA_MARGEM_SEM_SENHA"
                                      id="csaConsultaMargemSemSenhaNao" <%=(consignataria != null ? consignataria.getCsaConsultaMargemSemSenha().equals("N") ? "checked" : "" : "checked")%>
                                      value="N" >
                               <label class="form-check-label" for="csaConsultaMargemSemSenhaNao">
                                   <span class="text-nowrap align-text-top"><hl:message
                                           key="rotulo.nao"/></span>
                               </label>
                           </div>
                       </div>
                   </div>
                  <% } %>
				  <%
                    if (podeNotificarCsaAlteracaoRegraConvenio) {
                  %>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_EMAIL_NOTIFICACAO_RCO)%>" >
                  <div class="row">
                    <div class="form-group col-sm-6">
                      <label for="CSA_EMAIL_NOTIFICACAO_RCO"><hl:message key="rotulo.consignataria.emails.notificacao.rco"/></label>
                         <hl:htmlinput name="CSA_EMAIL_NOTIFICACAO_RCO"
                                       type="textarea"
                                       di="CSA_EMAIL_NOTIFICACAO_RCO"
                                       classe="form-control"
                                       rows="3" 
                                       cols="67"
                                       onBlur="formataEmails('CSA_EMAIL_NOTIFICACAO_RCO');"
                                       placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.email", responsavel)%>"
                                       value="<%=TextHelper.forHtmlAttribute(consignataria != null ? (consignataria.getCsaEmailNotificacaoRco() != null ? consignataria.getCsaEmailNotificacaoRco() : "") : "")%>"
                                       others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                       configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_EMAIL_NOTIFICACAO_RCO)%>"
                         />
                    </div>
                  </div>
                  </show:showfield>
				  <% } %>
              <div class="legend">
                <span><hl:message key="rotulo.contrato.singular"/></span>
              </div>
               <% if(podeVisualizarContrato) { %>
               <div class="row">
                   <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_DATA_INICIO_CONTRATO)%>">
                       <div class="form-group col-sm-4">
                           <label for="CSA_DATA_INICIO_CONTRATO"><hl:message key="rotulo.consignataria.dataInicio.contrato"/></label>
                           <hl:htmlinput name="CSA_DATA_INICIO_CONTRATO"
                                         di="CSA_DATA_INICIO_CONTRATO"
                                         placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.data.inicio.contrato.csa", responsavel)%>"
                                         type="text"
                                         classe="form-control"
                                         value="<%=TextHelper.forHtmlAttribute(consignataria != null ? DateHelper.toDateString(consignataria.getCsaDataInicioContrato()) : JspHelper.verificaVarQryStr(request,"CSA_DATA_INICIO_CONTRATO"))%>"
                                         size="12"
                                         mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                                         others="<%=TextHelper.forHtmlAttribute(responsavel.isCsa() || !podeEditarCsa ? "disabled" : "")%>"
                                         configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_DATA_INICIO_CONTRATO)%>"
                           />
                       </div>
                   </show:showfield>
                   <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_DATA_RENOVACAO_CONTRATUAL)%>">
                       <div class="form-group col-sm-4">
                           <label for="CSA_DATA_RENOVACAO_CONTRATUAL"><hl:message key="rotulo.consignataria.dataRenovacao.contratual"/></label>
                           <hl:htmlinput name="CSA_DATA_RENOVACAO_CONTRATUAL"
                                         di="CSA_DATA_RENOVACAO_CONTRATUAL"
                                         placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.data.renovacao.contrato.csa", responsavel)%>"
                                         type="text"
                                         classe="form-control"
                                         value="<%=TextHelper.forHtmlAttribute(consignataria != null ? DateHelper.toDateString(consignataria.getCsaDataRenovacaoContrato()) : JspHelper.verificaVarQryStr(request,"CSA_DATA_RENOVACAO_CONTRATUAL"))%>"
                                         size="12"
                                         mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                                         others="<%=TextHelper.forHtmlAttribute(!podeEditarContrato ? "disabled" : "")%>"
                                         configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_DATA_RENOVACAO_CONTRATUAL)%>"
                           />
                       </div>
                   </show:showfield>
                   <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_NUMERO_PROCESSO)%>">
                       <div class="form-group col-sm-4">
                           <label for="CSA_NUMERO_PROCESSO"><hl:message key="rotulo.consignataria.numero.processo"/></label>
                           <hl:htmlinput name="CSA_NUMERO_PROCESSO"
                                         type="text"
                                         di="CSA_NUMERO_PROCESSO"
                                         classe="form-control"
                                         value="<%=TextHelper.forHtmlAttribute(consignataria != null ? consignataria.getCsaNumeroProcessoContrato() : JspHelper.verificaVarQryStr(request, "CSA_NUMERO_PROCESSO"))%>"
                                         mask="#A20"
                                         placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.numero.processo.csa", responsavel)%>"
                                         others="<%=TextHelper.forHtmlAttribute(!podeEditarContrato ? "disabled" : "")%>"
                                         configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_NUMERO_PROCESSO)%>"
                           />
                       </div>
                   </show:showfield>
                   <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_OBS_CONTRATUAL)%>">
                       <div class="row">
                           <div class="form-group col-sm-6">
                               <label for="CSA_OBS_CONTRATUAL"><hl:message key="rotulo.consignataria.obs.contrato"/></label>
                               <hl:htmlinput name="CSA_OBS_CONTRATUAL"
                                             type="textarea"
                                             di="CSA_OBS_CONTRATUAL"
                                             value="<%=TextHelper.forHtmlAttribute(consignataria != null ? (consignataria.getCsaObsContrato() != null ? consignataria.getCsaObsContrato() : "") : "")%>"
                                             placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.obs.contrato.csa", responsavel)%>"
                                             classe="form-control"
                                             cols="32"
                                             rows="5"
                                             others="<%=(String)(!podeEditarContrato ? "disabled" : "")%>"
                                             onFocus="SetarEventoMascaraV4(this,'#*65000',true);"
                                             size="220"
                                             mask="#*100"
                                             onBlur="fout(this);ValidaMascaraV4(this);"
                                             configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_OBS_CONTRATUAL)%>"
                               />
                           </div>
                       </div>
                   </show:showfield>
               </div>
               <% } %>
              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_NRO_CONTRATO)%>">
              <div class="row">
                <div class="form-group col-sm-6">
                  <label for="CSA_NRO_CONTRATO"><hl:message key="rotulo.numero.contrato"/></label>
                  <hl:htmlinput name="CSA_NRO_CONTRATO"
                                di="CSA_NRO_CONTRATO"
                                type="text"
                                classe="form-control"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.numero.contrato", responsavel)%>"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaNroContrato() : JspHelper.verificaVarQryStr(request,\"CSA_NRO_CONTRATO\"))%>"
                                size="40"
                                mask="#*255"
                                others="<%=TextHelper.forHtmlAttribute(responsavel.isCsa() || !podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_NRO_CONTRATO)%>"
                  />
                </div>
              </div>
              </show:showfield>
              <div class="row">
                 <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_DATA_EXPIRACAO)%>">
                 <div class="form-group col-sm-6">
                  <label for="CSA_DATA_EXPIRACAO"><hl:message key="rotulo.consignataria.dataExpiracao"/></label>
                  <hl:htmlinput name="CSA_DATA_EXPIRACAO"
                                di="CSA_DATA_EXPIRACAO"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.data.expiracao.contratual", responsavel)%>"  
                                type="text"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? DateHelper.toDateString(consignataria.getCsaDataExpiracao()) : JspHelper.verificaVarQryStr(request,\"CSA_DATA_EXPIRACAO\"))%>"
                                size="12"
                                mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                                others="<%=TextHelper.forHtmlAttribute(responsavel.isCsa() || !podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_DATA_EXPIRACAO)%>"
                  />
                 </div>
                 </show:showfield>
                 <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_DATA_EXPIRACAO_CADASTRAL)%>">
                 <div class="form-group col-sm-6">
                  <label for="CSA_DATA_EXPIRACAO_CADASTRAL"><hl:message key="rotulo.consignataria.dataExpiracao.cadastral"/></label>
                  <hl:htmlinput name="CSA_DATA_EXPIRACAO_CADASTRAL"
                                di="CSA_DATA_EXPIRACAO_CADASTRAL"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.data.expiracao.cadastral", responsavel)%>"
                                type="text"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? DateHelper.toDateString(consignataria.getCsaDataExpiracaoCadastral()) : JspHelper.verificaVarQryStr(request,\"CSA_DATA_EXPIRACAO_CADASTRAL\"))%>"
                                size="12"
                                mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                                others="<%=TextHelper.forHtmlAttribute(responsavel.isCsa() || !podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_DATA_EXPIRACAO_CADASTRAL)%>"
                  />
                 </div>
                 </show:showfield>
                 <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_EMAIL_EXPIRACAO)%>">
                 <div class="form-group col-sm-6">
                  <label for="CSA_EMAIL_EXPIRACAO"><hl:message key="rotulo.consignataria.emailExpiracao"/></label>
                  <hl:htmlinput name="CSA_EMAIL_EXPIRACAO"
                                di="CSA_EMAIL_EXPIRACAO"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.email.notificacao.expiracao.cadastral", responsavel)%>"
                                type="textarea"
                                rows="3"
                                cols="67"   
                                onBlur="formataEmails('CSA_EMAIL_EXPIRACAO');"                             
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaEmailExpiracao(): JspHelper.verificaVarQryStr(request,\"CSA_EMAIL_EXPIRACAO\"))%>"
                                size="69"
                                mask="#*100"
                                others="<%=!podeEditarCsa ? "disabled" : ""%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_EMAIL_EXPIRACAO)%>"
                  />
                 </div>
                 </show:showfield>
              </div>
              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_CNPJ)%>">
              <div class="row">
                <div class="form-group col-sm-6">
                  <label for=CSA_CNPJ><hl:message key="rotulo.consignataria.cnpj"/></label>
                  <hl:htmlinput name="CSA_CNPJ"
                                di="CSA_CNPJ" 
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.cnpj", responsavel)%>"
                                type="text"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaCnpj() : JspHelper.verificaVarQryStr(request,\"CSA_CNPJ\"))%>"
                                mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjMask())%>"
                                size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjSize())%>"
                                maxlength="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjMaxLenght())%>"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_CNPJ)%>"
                  />
                </div>
              </div>
              </show:showfield>
              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_UNIDADE_ORGANIZACIONAL)%>">
              <div class="row">
                <div class="form-group col-sm-6">
                  <label for=CSA_UNIDADE_ORGANIZACIONAL><hl:message key="rotulo.consignataria.unidade.organizacional"/></label>
                  <hl:htmlinput name="CSA_UNIDADE_ORGANIZACIONAL"
                                type="text"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaUnidadeOrganizacional(): JspHelper.verificaVarQryStr(request,\"CSA_UNIDADE_ORGANIZACIONAL\"))%>"
                                size="69"
                                mask="#*100"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.unidade.organizacional", responsavel)%>"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_UNIDADE_ORGANIZACIONAL)%>"
                  />
                </div>
              </div>
              </show:showfield>
              <%
                  if (responsavel.isSup()) {
              %>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_NRO_CONTRATO_ZETRA)%>">
                <div class="row">
                  <div class="form-group col-sm-6">
                    <label for=CSA_NRO_CONTRATO_ZETRA><hl:message key="rotulo.consignataria.nrocontrato.zetra"/></label>
                    <hl:htmlinput name="CSA_NRO_CONTRATO_ZETRA"
                                  di="CSA_NRO_CONTRATO_ZETRA"
                                  placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.numero.contrato.zetra", responsavel)%>"
                                  type="text"
                                  classe="form-control"
                                  value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaNroContratoZetra(): JspHelper.verificaVarQryStr(request,\"CSA_NRO_CONTRATO_ZETRA\"))%>"                                  
                                  mask="#*40"
                                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_NRO_CONTRATO_ZETRA)%>"
                    />
                  </div>
                </div>
                </show:showfield>

                <%
                  String partProjInadimplencia = consignataria != null && !TextHelper.isNull(consignataria.getCsaProjetoInadimplencia()) ? consignataria.getCsaProjetoInadimplencia() : "N";
                %>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_PARTICIPA_INADIMPLENCIA)%>">
                  <div class="row">
                    <div class="col-sm-6 col-md-6 mb-2">
                      <div class="form-group mb-0">
                        <span id="partIna"><hl:message key="rotulo.consignataria.participa.inadimplencia"/></span>
                      </div>
                        <div class="form-check form-check-inline" role="radiogroup" aria-labelledby="partProgIn">
                          <input class="form-check-input ml-1" type="radio" name="participa_projeto_inadimplencia" value="S" id="inadSim" <%=(String)(partProjInadimplencia.equals("S") ? "checked" : "")%> value="S">
                          <label class="form-check-label labelSemNegrito" for="inadSim">
                            <hl:message key="rotulo.sim"/>
                          </label>
                        </div>
                        <div class="form-check form-check-inline">
                          <input class="form-check-input ml-1" type="radio" value="N" name="participa_projeto_inadimplencia" id="inadNao" <%=(String)(partProjInadimplencia.equals("N") ? "checked" : "")%> value="N">
                          <label class="form-check-label labelSemNegrito" for="inadNao">
                            <hl:message key="rotulo.nao"/>
                          </label>
                          <input type="hidden" name="participa_projeto_inadimplencia_old" value="<%=TextHelper.forHtmlAttribute(partProjInadimplencia)%>">
                        </div>
                      </div>
                   </div>
                </show:showfield>

                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_IDENTIFICADOR_INTERNO)%>">
                <div class="row">
                  <div class="form-group col-sm-3">
                    <label for=CSA_IDENTIFICADOR_INTERNO><hl:message key="rotulo.consignataria.codigo.zetrasoft"/></label>
                    <select class="form-control form-select" id="CSA_IDENTIFICADOR_INTERNO" name="CSA_IDENTIFICADOR_INTERNO" onchange="verificaCodigoZetra()">
                      <OPTION VALUE="" SELECTED><hl:message key="rotulo.campo.outro"/></OPTION>
                    </select>
                  </div>
                  <div class="form-group col-sm-3" id="divAjudaBanco">
                    <label for="ajudaBanco"><hl:message key="rotulo.consignataria.codigo.zetrasoft.outro"/></label>
                    <input type="text" placeholder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.codigo.zetrasoft", responsavel)%>" class="form-control" id="ajudaBanco" name="ajudaBanco" VALUE="<%=TextHelper.forHtmlAttribute(csaIdentificadorInterno)%>" onBlur="fout(this);ValidaMascaraV4(this);" onChange="validaCodigoInternoZetra(true);">
                  </div>
                </div>
                </show:showfield>
              <%
                  }
              %>
              <%
                  if (responsavel.isSup() || responsavel.isCsa()) {
              %>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_EMAIL_PROJ_INADIMPLENCIA)%>" >
                <div class="row">
                  <div class="form-group col-sm-4">
                    <label for="CSA_EMAIL_PROJ_INADIMPLENCIA"><hl:message key="rotulo.consignataria.email.inadimplencia"/></label>
                       <hl:htmlinput name="CSA_EMAIL_PROJ_INADIMPLENCIA"
                                     type="textarea"
                                     di="CSA_EMAIL_PROJ_INADIMPLENCIA"
                                     classe="form-control"
                                     rows="3" 
                                     cols="67"
                                     onBlur="formataEmails('CSA_EMAIL_PROJ_INADIMPLENCIA');"
                                     placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.email", responsavel)%>"
                                     value="<%=TextHelper.forHtmlAttribute(consignataria != null ? (consignataria.getCsaEmailProjInadimplencia() != null ? consignataria.getCsaEmailProjInadimplencia() : "") : "")%>"
                                     size="69"
                                     mask="#*100"
                                     others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                     configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_EMAIL_PROJ_INADIMPLENCIA)%>"
                       />
                  </div>
                </div>
                </show:showfield>
              <%
                  }
              %>
              <%
                  if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_NRO_BCO, responsavel) ||
                          ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_NRO_AGE, responsavel) ||
                          ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_NRO_CTA, responsavel) ||
                          ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_DIG_CTA, responsavel)) {
              %>
              <div class="legend">
                <span><hl:message key="rotulo.dados.bancarios"/></span>
              </div>
              <div class="row">
                 <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_NRO_BCO)%>">
                 <div class="form-group col-sm-3">
                  <label for="CSA_NRO_BCO"><hl:message key="rotulo.consignataria.nro.banco"/></label>
                  <hl:htmlinput name="CSA_NRO_BCO"
                                di="CSA_NRO_BCO"
                                type="text"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaNroBco():JspHelper.verificaVarQryStr(request, "CSA_NRO_BCO"))%>"                                
                                mask="#D3"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.numero.banco", responsavel)%>"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_NRO_BCO)%>"
                  />
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_NRO_AGE)%>">
                <div class="form-group col-sm-3">
                  <label for="CSA_NRO_AGE"><hl:message key="rotulo.consignataria.nro.agencia"/></label>
                  <hl:htmlinput name="CSA_NRO_AGE"
                                di="CSA_NRO_AGE"
                                type="text"
                                classe="form-control"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.numero.agencia", responsavel)%>"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaNroAge():JspHelper.verificaVarQryStr(request, "CSA_NRO_AGE"))%>"                                
                                mask="#D5"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_NRO_AGE)%>"
                  />
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_NRO_CTA)%>">
                <div class="form-group col-sm-3">
                  <label for="CSA_NRO_CTA"><hl:message key="rotulo.nro.conta"/></label>
                  <hl:htmlinput name="CSA_NRO_CTA"
                                type="text"
                                di="CSA_NRO_CTA"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaNroCta():JspHelper.verificaVarQryStr(request, "CSA_NRO_CTA"))%>"                                
                                mask="#A9"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.numero.conta", responsavel)%>"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_NRO_CTA)%>"
                   />
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_DIG_CTA)%>">
                <div class="form-group col-sm-3">
                  <label for="CSA_DIG_CTA"><hl:message key="rotulo.digito.conta"/></label>
                  <hl:htmlinput name="CSA_DIG_CTA"
                                di="CSA_DIG_CTA" 
                                type="text"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaDigCta():JspHelper.verificaVarQryStr(request,\"CSA_DIG_CTA\"))%>"
                                size="1"
                                mask="#A1"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.numero.digito", responsavel)%>"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_DIG_CTA)%>"
                  />
                </div>
                </show:showfield>
              </div>
              <%
                  }
              %>
              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_CNPJ_CTA)%>">
              <div class="row">
                <div class="form-group col-sm-4">
                  <label for="CSA_CNPJ_CTA"><hl:message key="rotulo.consignataria.cpnj.dados.bancarios"/></label>
                  <hl:htmlinput name="CSA_CNPJ_CTA"
                                type="text"
                                di="CSA_CNPJ_CTA"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaCnpjCta() : JspHelper.verificaVarQryStr(request,\"CSA_CNPJ_CTA\"))%>"
                                mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjMask())%>"
                                size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjSize())%>"
                                maxlength="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCnpjMaxLenght())%>"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.cnpj.banco", responsavel)%>"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_CNPJ_CTA)%>"
                  />
                </div>
              </div>
              </show:showfield>
              <%
                  if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_CONTATO, responsavel) || ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_CONTATO_TELEFONE, responsavel)) {
              %>
              <div class="row">
                 <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_CONTATO)%>">
                 <div class="form-group col-sm-4">
                   <label for="CSA_CONTATO"><hl:message key="rotulo.consignataria.contato"/></label>
                   <hl:htmlinput name="CSA_CONTATO"
                                 type="text"
                                 di="CSA_CONTATO"
                                 classe="form-control"
                                 value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaContato():JspHelper.verificaVarQryStr(request,\"CSA_CONTATO\"))%>"
                                 size="32"
                                 mask="#*2000"
                                 placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.contato", responsavel)%>"
                                 others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                 configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_CONTATO)%>"
                   />
                 </div>
                 </show:showfield>
                 <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_CONTATO_TELEFONE)%>">
                 <div class="form-group col-sm-4">
                  <label for="CSA_CONTATO_TEL"><hl:message key="rotulo.consignataria.telefone"/></label>
                  <hl:htmlinput name="CSA_CONTATO_TEL"
                                type="text"
                                di="CSA_CONTATO_TEL"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaContatoTel():JspHelper.verificaVarQryStr(request,\"CSA_CONTATO_TEL\"))%>"
                                size="32"
                                mask="#*2000"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.telefone", responsavel)%>"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_CONTATO_TELEFONE)%>"
                  />
                </div>
                </show:showfield>
              </div>
              <%
                  }
              %>
              <%
                  if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_RESPONSAVEL, responsavel) || 
                      ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_RESP_CARGO, responsavel) ||
                      ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_RESP_TELEFONE, responsavel) ||
                      ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_RESPONSAVEL_2, responsavel) || 
                      ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_RESP_CARGO_2, responsavel) ||
                      ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_RESP_TELEFONE_2, responsavel) ||
                      ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_RESPONSAVEL_3, responsavel) || 
                      ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_RESP_CARGO_3, responsavel) ||
                      ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_RESP_TELEFONE_3, responsavel)) {
              %>
              <div class="legend">
                <span><hl:message key="rotulo.responsavel.plural"/></span>
              </div>
              <div class="row">
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_RESPONSAVEL)%>">
                <div class="form-group col-sm-4">
                  <label for="CSA_RESPONSAVEL"><hl:message key="rotulo.consignataria.responsavel.1"/></label>
                  <hl:htmlinput name="CSA_RESPONSAVEL"
                                type="text"
                                di="CSA_RESPONSAVEL"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.primeiro.responsavel", responsavel)%>"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaResponsavel():JspHelper.verificaVarQryStr(request,\"CSA_RESPONSAVEL\"))%>"
                                size="32"
                                mask="#*100"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_RESPONSAVEL)%>"
                   />
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_RESP_CARGO)%>">
                <div class="form-group col-sm-4">
                  <label for="CSA_RESP_CARGO"><hl:message key="rotulo.cargo.singular.1"/></label>
                  <hl:htmlinput name="CSA_RESP_CARGO"
                               type="text"
                               di="CSA_RESP_CARGO"
                               placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.primeiro.responsavel.cargo", responsavel)%>"
                               classe="form-control"
                               value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaRespCargo():JspHelper.verificaVarQryStr(request,\"CSA_RESP_CARGO\"))%>"
                               size="32"
                               mask="#*100"
                               others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                               configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_RESP_CARGO)%>"
                  />
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_RESP_TELEFONE)%>">
                <div class="form-group col-sm-4">
                  <label for="CSA_RESP_TELEFONE"><hl:message key="rotulo.consignataria.responsavel.telefone.1"/></label>
                  <hl:htmlinput name="CSA_RESP_TELEFONE"
                                type="text"
                                di="CSA_RESP_TELEFONE"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.primeiro.responsavel.telefone", responsavel)%>"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaRespTelefone():JspHelper.verificaVarQryStr(request,\"CSA_RESP_TELEFONE\"))%>"
                                size="32"
                                mask="#*100"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_RESP_TELEFONE)%>"
                  />
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_RESPONSAVEL_2)%>">
                <div class="form-group col-sm-4">
                  <label for="CSA_RESPONSAVEL_2"><hl:message key="rotulo.consignataria.responsavel.nome.2"/></label>
                  <hl:htmlinput name="CSA_RESPONSAVEL_2"
                                type="text"
                                di="CSA_RESPONSAVEL_2"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.segundo.responsavel", responsavel)%>"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaResponsavel2():JspHelper.verificaVarQryStr(request,\"CSA_RESPONSAVEL_2\"))%>"
                                size="32"
                                mask="#*100"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_RESPONSAVEL_2)%>"
                  />
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_RESP_CARGO_2)%>">
                <div class="form-group col-sm-4">
                  <label for="CSA_RESP_CARGO_2"><hl:message key="rotulo.consignataria.responsavel.cargo.2"/></label>
                  <hl:htmlinput name="CSA_RESP_CARGO_2"
                                type="text"
                                classe="form-control"                                
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.segundo.responsavel.cargo", responsavel)%>"
                                di="CSA_RESP_CARGO_2"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaRespCargo2():JspHelper.verificaVarQryStr(request,\"CSA_RESP_CARGO_2\"))%>"
                                size="32"
                                mask="#*100"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_RESP_CARGO_2)%>"
                  />
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_RESP_TELEFONE_2)%>">
                <div class="form-group col-sm-4">
                  <label for="CSA_RESP_TELEFONE_2"><hl:message key="rotulo.consignataria.responsavel.telefone.2"/></label>
                  <hl:htmlinput name="CSA_RESP_TELEFONE_2"
                                type="text"
                                classe="form-control"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.segundo.responsavel.telefone", responsavel)%>"
                                di="CSA_RESP_TELEFONE_2"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaRespTelefone2():JspHelper.verificaVarQryStr(request,\"CSA_RESP_TELEFONE_2\"))%>"
                                size="32"
                                mask="#*100"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_RESP_TELEFONE_2)%>"
                  />
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_RESPONSAVEL_3)%>">
                <div class="form-group col-sm-4">
                  <label for="CSA_RESPONSAVEL_3"><hl:message key="rotulo.consignataria.responsavel.nome.3"/></label>
                  <hl:htmlinput name="CSA_RESPONSAVEL_3"
                                type="text"
                                classe="form-control"
                                di="CSA_RESPONSAVEL_3"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.terceiro.responsavel", responsavel)%>"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaResponsavel3(): JspHelper.verificaVarQryStr(request,\"CSA_RESPONSAVEL_3\"))%>"
                                size="32"
                                mask="#*100"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_RESPONSAVEL_3)%>"
                  />
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_RESP_CARGO_3)%>">
                <div class="form-group col-sm-4">
                  <label for="CSA_RESP_CARGO_3"><hl:message key="rotulo.consignataria.responsavel.cargo.3"/></label>
                  <hl:htmlinput name="CSA_RESP_CARGO_3"
                                type="text"
                                di="CSA_RESP_CARGO_3"
                                classe="form-control"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.terceiro.responsavel.cargo", responsavel)%>"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaRespCargo3():JspHelper.verificaVarQryStr(request,\"CSA_RESP_CARGO_3\"))%>"
                                size="32"
                                mask="#*100"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_RESP_CARGO_3)%>"
                  />
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_RESP_TELEFONE_3)%>">
                <div class="form-group col-sm-4">
                  <label for="CSA_RESP_TELEFONE_3"><hl:message key="rotulo.consignataria.responsavel.telefone.3"/></label>
                  <hl:htmlinput name="CSA_RESP_TELEFONE_3"
                                type="text"
                                di="CSA_RESP_TELEFONE_3"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.terceiro.responsavel.telefone", responsavel)%>"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaRespTelefone3():JspHelper.verificaVarQryStr(request,\"CSA_RESP_TELEFONE_3\"))%>"
                                size="32"
                                mask="#*100"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_RESP_TELEFONE_3)%>"
                  />
                </div>
                </show:showfield>
              </div>
              <%
                  }
              %>
              <%
                  if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_LOGRADOURO, responsavel) || 
                      ShowFieldHelper.showField(FieldKeysConstants.EDT_CONSIGNATARIA_NRO, responsavel) ||
                      ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_COMPLEMENTO, responsavel) ||
                      ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_BAIRRO, responsavel) || 
                      ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_CIDADE, responsavel) ||
                      ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_UF, responsavel) ||
                      ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_CEP, responsavel) || 
                      ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_ENDERECO_2, responsavel)) {
              %>
              <div class="legend">
                <span><hl:message key="rotulo.endereco"/></span>
              </div>
              <div class="row">
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_LOGRADOURO)%>">
                <div class="form-group col-sm-4">
                  <label for="CSA_LOGRADOURO"><hl:message key="rotulo.consignataria.logradouro"/></label>
                  <hl:htmlinput name="CSA_LOGRADOURO"
                                type="text"
                                di="CSA_LOGRADOURO"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.logradouro", responsavel)%>"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaLogradouro() : JspHelper.verificaVarQryStr(request,\"CSA_LOGRADOURO\"))%>"
                                size="32"
                                mask="#*100"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_LOGRADOURO)%>"
                  />
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.EDT_CONSIGNATARIA_NRO)%>" >
                <div class="form-group col-sm-2">
                  <label for="CSA_NRO"><hl:message key="rotulo.numero.abreviado"/></label>

                     <hl:htmlinput name="CSA_NRO"
                                   type="text"
                                   di="CSA_NRO"
                                   classe="form-control"
                                   placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.numero.logradouro", responsavel)%>"
                                   value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaNro(): JspHelper.verificaVarQryStr(request,"CSA_NRO"))%>"
                                   size="32"
                                   mask="#D5"
                                   others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                   configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDT_CONSIGNATARIA_NRO)%>"
                     />
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_COMPLEMENTO)%>">
                <div class="form-group col-sm-3">
                  <label for="CSA_COMPL"><hl:message key="rotulo.endereco.complemento"/></label>
                  <hl:htmlinput name="CSA_COMPL"
                                type="text"
                                di="CSA_COMPL"
                                classe="form-control"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.complemento.logradouro", responsavel)%>"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaCompl(): JspHelper.verificaVarQryStr(request,\"CSA_COMPL\"))%>"
                                size="32"
                                mask="#*40"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_COMPLEMENTO)%>"
                  />
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_BAIRRO)%>">
                <div class="form-group col-sm-3">
                  <label for="CSA_BAIRRO"><hl:message key="rotulo.consignataria.bairro"/></label>
                  <hl:htmlinput name="CSA_BAIRRO"
                                type="text"
                                di="CSA_BAIRRO"                                
                                classe="form-control"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.bairro", responsavel)%>"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaBairro(): JspHelper.verificaVarQryStr(request,\"CSA_BAIRRO\"))%>"
                                size="32"
                                mask="#*40"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_BAIRRO)%>"
                  />
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_CIDADE)%>">
                <div class="form-group col-sm-4">
                  <label for="CSA_CIDADE"><hl:message key="rotulo.consignataria.cidade"/></label>
                  <hl:htmlinput name="CSA_CIDADE"
                                type="text"
                                di="CSA_CIDADE"
                                classe="form-control"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.cidade", responsavel)%>"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaCidade(): JspHelper.verificaVarQryStr(request,\"CSA_CIDADE\"))%>"
                                size="32"
                                mask="#*40"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_CIDADE)%>"
                  />
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_UF)%>">
                <div class="form-group col-sm-4">
                  <label for="CSA_UF"><hl:message key="rotulo.consignataria.uf"/></label>
                  <hl:campoUFv4 name="CSA_UF"
                        classe="form-control"
                        di="CSA_UF"
                        rotuloUf="rotulo.consignataria.uf"
                        placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.uf", responsavel)%>"
                        valorCampo="<%=TextHelper.forHtmlAttribute(consignataria != null ? (consignataria.getCsaUf() != null ? consignataria.getCsaUf() : JspHelper.verificaVarQryStr(request,\"CSA_UF\")) : "")%>"
                        desabilitado = "<%=!podeEditarCsa%>"
                  />
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_CEP)%>">
                <div class="form-group col-sm-4">
                  <label for="CSA_CEP"><hl:message key="rotulo.consignataria.cep"/></label>
                  <hl:htmlinput name="CSA_CEP"
                                type="text"
                                di="CSA_CEP"
                                classe="form-control"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.cep", responsavel)%>"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaCep(): JspHelper.verificaVarQryStr(request,\"CSA_CEP\"))%>"
                                size="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCepSize())%>"
                                mask="<%=TextHelper.forHtmlAttribute(LocaleHelper.getCepMask())%>"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_CEP)%>"
                   />
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_ENDERECO_2)%>">
                <div class="form-group col-sm-3">
                  <label for="CSA_ENDERECO_2"><hl:message key="rotulo.consignataria.endereco.alternativo"/></label>
                  <hl:htmlinput name="CSA_ENDERECO_2"
                                type="text"
                                classe="form-control"
                                di="CSA_ENDERECO_2"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.endereco.2", responsavel)%>"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaEndereco2(): JspHelper.verificaVarQryStr(request,\"CSA_ENDERECO_2\"))%>"
                                size="32"
                                mask="#*2000"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_ENDERECO_2)%>"
                  />
                </div>
                </show:showfield>
              </div>
              <%
                  }
              %>
              <%
                  if (ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_TELEFONE, responsavel) || 
                      ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_FAX, responsavel) ||
                      ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_EMAIL, responsavel) ||
                      ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_WHATSAPP, responsavel) ||
                      ShowFieldHelper.showField(FieldKeysConstants.EDITAR_CSA_EMAIL_CONTATO, responsavel)) {
              %>
              <div class="legend">
                <span><hl:message key="rotulo.consignataria.contato"/></span>
              </div>
              <div class="row">
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_TELEFONE)%>">
                <div class="form-group col-sm-4">
	                <div class="row">
	                	<div class="col-sm-2">
	                        <label for="CSA_DDD_TEL"><hl:message key="rotulo.consignataria.telefone.ddd"/></label>
	                        <hl:htmlinput di="CSA_DDD_TEL" 
								name="CSA_DDD_TEL" 
								type="text" 
								classe="form-control"            
								placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.ddd", responsavel)%>"        
								value="<%=TextHelper.forHtmlAttribute(consignataria !=null && !TextHelper.isNull(consignataria.getCsaTel()) ? consignataria.getCsaTel().substring(0, 2): JspHelper.verificaVarQryStr(request,\"CSA_TEL\"))%>"
								mask="<%=LocaleHelper.getDDDMask()%>" 
								others="<%=TextHelper.forHtmlAttribute((!podeEditarCsa) ? "disabled" : "")%>" 
                          	/>
	                    </div>
	                    <div class="col-sm">
	                        <label for="CSA_TEL"><hl:message key="rotulo.consignataria.telefone"/></label>
	                  		<hl:htmlinput name="CSA_TEL"
	                            type="text"
	                            classe="form-control"
	                            di="CSA_TEL"
	                            placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.telefone", responsavel)%>"
	                            value="<%=TextHelper.forHtmlAttribute(consignataria !=null && !TextHelper.isNull(consignataria.getCsaTel()) && (consignataria.getCsaTel().length() == 11 || consignataria.getCsaTel().length() == 10) ? consignataria.getCsaTel().substring(2): JspHelper.verificaVarQryStr(request,\"CSA_TEL\"))%>"
	                            size="32"
	                            mask="<%=LocaleHelper.getCelularMask()%>"
	                            others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
	                            configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_TELEFONE)%>"
	                        />
	                    </div>
	                 </div>
                 </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_FAX)%>">
                <div class="form-group col-sm-4">
                  <label for="CSA_FAX"><hl:message key="rotulo.consignataria.fax"/></label>
                  <hl:htmlinput name="CSA_FAX"
                                type="text"
                                di="CSA_FAX"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.fax", responsavel)%>"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaFax(): JspHelper.verificaVarQryStr(request,\"CSA_FAX\"))%>"
                                size="32"
                                mask="#*30"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_FAX)%>"
                  />
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_EMAIL)%>">
                <div class="form-group col-sm-4 ">
                  <label for="CSA_EMAIL"><hl:message key="rotulo.consignataria.email"/></label>
                  <hl:htmlinput name="CSA_EMAIL"
                                type="textarea"
                                di="CSA_EMAIL"
                                rows="3" 
                                cols="67"
                                onBlur="formataEmails('CSA_EMAIL');"
                                classe="form-control"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.email", responsavel)%>"
                                value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaEmail(): JspHelper.verificaVarQryStr(request,\"CSA_EMAIL\"))%>"
                                size="69"
                                mask="#*100"
                                others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                                configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_EMAIL)%>"
                  />
                </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_WHATSAPP)%>">
                  <div class="form-group col-sm-4">
                    <div class="row">
                      <div class="col-sm-2">
                        <label for="CSA_DDD_TELEFONE"><hl:message key="rotulo.consignataria.whatsapp.ddd"/></label>
                        <hl:htmlinput di="CSA_DDD_TELEFONE" 
                          name="CSA_DDD_TELEFONE" 
                          type="text" 
                          classe="form-control"            
                          placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.consignataria.whatsapp.ddd.placeholder", responsavel)%>"        
                          value="<%=TextHelper.forHtmlAttribute(consignataria !=null && !TextHelper.isNull(consignataria.getCsaWhatsapp()) ? (!LocaleHelper.isWhatsapp0800(consignataria.getCsaWhatsapp()) ? consignataria.getCsaWhatsapp().substring(0, 2) : "") : JspHelper.verificaVarQryStr(request,\"CSA_WHATSAPP\"))%>"
                          mask="<%=LocaleHelper.getDDDMask()%>" 
                          others="<%=TextHelper.forHtmlAttribute((!podeEditarCsa) || LocaleHelper.isWhatsapp0800(consignataria != null ? consignataria.getCsaWhatsapp() : "") ? "disabled" : "")%>" />
                      </div>
                      <div class="col-sm">
                        <label for="CSA_WHATSAPP"><hl:message key="rotulo.consignataria.whatsapp"/></label>
                        <hl:htmlinput name="CSA_WHATSAPP"
                          type="text"
                          classe="form-control"
                          di="CSA_WHATSAPP"
                          placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.telefone", responsavel)%>"
                          value="<%=TextHelper.forHtmlAttribute(consignataria !=null && !TextHelper.isNull(consignataria.getCsaWhatsapp()) && consignataria.getCsaWhatsapp().length() == 11 ? (LocaleHelper.isWhatsapp0800(consignataria.getCsaWhatsapp()) ? consignataria.getCsaWhatsapp() : consignataria.getCsaWhatsapp().substring(2)) : JspHelper.verificaVarQryStr(request,\"CSA_WHATSAPP\"))%>"
                          size="32"
                          mask="<%=LocaleHelper.getWhatsappMask()%>"
                          others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                          onBlur="validaWhatsapp();"
                          configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_WHATSAPP)%>"
                        />
                      </div>
                    </div>
                  </div>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_EMAIL_CONTATO)%>">
                  <div class="form-group col-sm-4">
                    <label for="CSA_EMAIL_CONTATO"><hl:message key="rotulo.consignataria.email.contato"/></label>
                    <hl:htmlinput name="CSA_EMAIL_CONTATO"
                      type="text"
                      di="CSA_EMAIL_CONTATO"
                      onBlur="formataEmails('CSA_EMAIL_CONTATO');"
                      classe="form-control"
                      placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.email", responsavel)%>"
                      value="<%=TextHelper.forHtmlAttribute(consignataria !=null ? consignataria.getCsaEmailContato(): JspHelper.verificaVarQryStr(request,\"CSA_EMAIL_CONTATO\"))%>"
                      size="69"
                      mask="#*100"
                      others="<%=TextHelper.forHtmlAttribute(!podeEditarCsa ? "disabled" : "")%>"
                      configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_EMAIL_CONTATO)%>"
                    />
                  </div>
                </show:showfield>
              </div>
              <%
                  }
              %>
              <div class="legend"></div>
              <%
                  if (ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel)) {
              %>
              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_TXT_CONTATO)%>">
              <div class="row">
                <div class="form-group col-sm-6">
                  <label for="CSA_TXT_CONTATO"><hl:message key="rotulo.consignataria.instrucoes.contato.v4"/></label>
                  <textarea name="CSA_TXT_CONTATO" 
                            id="CSA_TXT_CONTATO" 
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.instrucoes", responsavel)%>"
                            class="form-control" cols="32" rows="5" <%=(String)(!podeEditarCsa ? "disabled" : "")%>
                            onFocus="SetarEventoMascaraV4(this,'#*65000',true);" 
                            onBlur="fout(this);ValidaMascaraV4(this);">
                            <%= consignataria != null && !TextHelper.isNull(consignataria.getCsaTxtContato()) ? consignataria.getCsaTxtContato() : "" %>
                  </textarea>
                </div>
              </div>
              </show:showfield>
              <%
                  }
              %>

              <%
                  if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_ASSINATURA_DIGITAL_CONSIGNACAO, CodedValues.TPC_SIM, responsavel) && responsavel.isCsa()) {
              %>
              <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_INSTRUCAO_ANEXO)%>">
              <div class="row">
                <div class="form-group col-sm-6">
                  <label for="CSA_INSTRUCAO_ANEXO"><hl:message key="rotulo.consignataria.instrucao.anexo.assinatura.digital.v4"/></label>
                  <textarea name="CSA_INSTRUCAO_ANEXO" 
                            id="CSA_INSTRUCAO_ANEXO" 
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.instrucoes.anexo", responsavel)%>"
                            class="form-control" cols="32" rows="5" <%=(String)(!podeEditarCsa ? "disabled" : "")%>
                            onFocus="SetarEventoMascaraV4(this,'#*65000',true);" 
                            onBlur="fout(this);ValidaMascaraV4(this);">
                            <%= consignataria != null && !TextHelper.isNull(consignataria.getCsaInstrucaoAnexo()) ? consignataria.getCsaInstrucaoAnexo() : "" %>
                  </textarea>
                </div>
              </div>
              </show:showfield>
              <%
                  }
              %>

            <%
                if (!ParamSist.paramEquals(CodedValues.TPC_PERMITE_BLOQUEIO_ACESSO_POR_IP, CodedValues.TPC_NAO, responsavel)) {
            %>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_IP_ACESSOS)%>">
                <jsp:include page="../manterEntidades/incluirCampoAcesso_v4.jsp">
                  <jsp:param name="tipo_endereco" value="numero_ip"/>
                  <jsp:param name="nome_campo" value="novoIp"/>
                  <jsp:param name="nome_lista" value="listaIps"/>
                  <jsp:param name="lista_resultado" value="csa_ip_acesso"/>
                  <jsp:param name="label" value="rotulo.usuario.ips.acesso"/>
                  <jsp:param name="mascara" value="#I30"/>
                  <jsp:param name="placeHolder" value="mensagem.placeholder.digite.ip.acesso"/>
                  <jsp:param name="pode_editar" value="<%=(boolean)podeEditarEnderecoAcesso%>"/>
                  <jsp:param name="bloquear_ip_interno" value="<%=(boolean)!permiteCadIpInternoCsaCor%>"/>
               </jsp:include>
               </show:showfield>
           <%
               }
           %>
           <%
               if (!ParamSist.paramEquals(CodedValues.TPC_PERMITE_BLOQUEIO_ACESSO_POR_DNS, CodedValues.TPC_NAO, responsavel)) {
           %>
               <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_DDNS_ACESSOS)%>">
               <jsp:include page="../manterEntidades/incluirCampoAcesso_v4.jsp">
                 <jsp:param name="tipo_endereco" value="url"/>
                 <jsp:param name="nome_campo" value="novoDDNS"/>
                 <jsp:param name="nome_lista" value="listaDDNSs"/>
                 <jsp:param name="lista_resultado" value="csa_ddns_acesso"/>
                 <jsp:param name="label" value="rotulo.usuario.enderecos.acesso"/>
                 <jsp:param name="mascara" value="#*100"/>
                 <jsp:param name="placeHolder" value="mensagem.placeholder.digite.endereco.acesso"/>
                 <jsp:param name="pode_editar" value="<%=(boolean)podeEditarEnderecoAcesso%>"/>
               </jsp:include>
               </show:showfield>
          <%
              }
          %>
             <%
                  if (csaCodigo != null) {
              %>
                <hl:htmlinput name="CSA_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>" />
                <hl:htmlinput name="csa" type="hidden" value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>"/>
                <input type="hidden" id="ip_list" name="ip_list" value="<%=TextHelper.forHtmlAttribute(csa_ip_acesso )%>">
                <input type="hidden" id="ddns_list" name="ddns_list" value="<%=TextHelper.forHtmlAttribute(csa_ddns_acesso )%>">
             <%
                     }
                 %>

             <%
                              if (responsavel.isCseSup()) {               
                                    if ( !ParamSist.paramEquals(CodedValues.TPC_PERMITE_BLOQUEIO_ACESSO_POR_IP, CodedValues.TPC_NAO, responsavel) 
                                        || !ParamSist.paramEquals(CodedValues.TPC_PERMITE_BLOQUEIO_ACESSO_POR_DNS, CodedValues.TPC_NAO, responsavel) ) {

                                       String exigeEnderecoOld = "";
                                       boolean exigeEndereco = false;
                                       if (consignataria != null && consignataria.getCsaExigeEnderecoAcesso() != null && !consignataria.getCsaExigeEnderecoAcesso().equals("")) {
                                          exigeEndereco = consignataria.getCsaExigeEnderecoAcesso().equals(CodedValues.TPC_SIM);
                                          exigeEnderecoOld = consignataria.getCsaExigeEnderecoAcesso();
                                       } else {
                                          exigeEndereco = (paramVrfIpAcesso != null && paramVrfIpAcesso.equals("S")) ? true : false;
                                       }
                          %>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.EDITAR_CSA_EXIGE_ENDERECO_ANEXO)%>">
                <div class="row">
                  <div class="col-sm-6 col-md-6 mb-2">
                    <div class="form-group mb-0">
                      <span id="verificaIP"><hl:message key="rotulo.consignataria.verifica.cadastro.ip"/></span>
                    </div>
                    <div class="form-check form-check-inline mt-2" role="radiogroup" aria-labelledby="verificaIP">
                      <input class="form-check-input ml-1" type="radio" name="csa_exige_endereco_acesso" value="S" id="vIPSim" <%=(String)(exigeEndereco ? "checked" : "")%> <%=(String)(!podeEditarCsa ? "disabled" : "")%>>
                      <label class="form-check-label labelSemNegrito pr-3" for="vIPSim">
                        <span class="text-nowrap align-text-top"><hl:message key="rotulo.sim"/></span>
                      </label>
                    </div>
                      <div class="form-check-inline form-check">
                      <input class="form-check-input ml-1" type="radio" value="N" name="csa_exige_endereco_acesso" id="vIPNão" <%=(String)(!exigeEndereco ? "checked" : "")%> <%=(String)(!podeEditarCsa ? "disabled" : "")%>>
                      <label class="form-check-label labelSemNegrito" for="vIPNão">
                        <hl:message key="rotulo.nao"/>
                      </label>
                      <input type="hidden" name="csa_exige_endereco_acesso_old" id="csa_exige_endereco_acesso_old" value="<%=TextHelper.forHtmlAttribute(exigeEnderecoOld )%>">
                    </div>
                  </div>
                </div>
                </show:showfield>
                <% } %>
              <% } %>
             <%if(possibilitaCredenciamento){ %>
                 <div class="row">
                  <div class="col-sm-6 col-md-6 mb-2">
                    <div class="form-group mb-0">
                      <span id="dpIniciaCredenciamento"><hl:message key="rotulo.consignataria.iniciar.credenciamento"/></span>
                    </div>
                    <div class="form-check form-check-inline mt-2" role="radiogroup" aria-labelledby="iniciaCredenciamento">
                      <input class="form-check-input ml-1" type="radio" name="iniciaCredenciamento" value="S" id="iniciaCredenciamentoS">
                      <label class="form-check-label pr-3" for="iniciaCredenciamentoS">
                        <hl:message key="rotulo.sim"/>
                      </label>
                        </div>
                      <div class="form-check form-check-inline">
                      <input class="form-check-input ml-1" type="radio" value="N" name="iniciaCredenciamento" id="iniciaCredenciamentoN" checked>
                      <label class="form-check-label labelSemNegrito" for="iniciaCredenciamentoN">
                        <hl:message key="rotulo.nao"/>
                      </label>
                    </div>
                  </div>
                </div>
             <%} %>
               <%
                   if (responsavel.isSup()) {
               %>
               <div class="row">
                   <div class="col-sm-6 col-md-6 mb-2">
                       <div class="form-group mb-0">
                           <span id="csaPermiteApi"><hl:message key="rotulo.consignataria.api.habilitada"/></span>
                       </div>
                       <div class="form-check form-check-inline mt-2" role="radiogroup" aria-labelledby="csaPermiteApi">
                           <input class="form-check-input ml-1" type="radio" name="CSA_PERMITE_API" value="S" id="permiteApiS" <%=podeApi.equals("S") ? "checked" : ""%>>
                           <label class="form-check-label pr-3" for="permiteApiS">
                               <hl:message key="rotulo.sim"/>
                           </label>
                       </div>
                       <div class="form-check form-check-inline">
                           <input class="form-check-input ml-1" type="radio" value="N" name="CSA_PERMITE_API" id="permiteApiN" <%=podeApi.equals("N") ? "checked" : ""%>>
                           <label class="form-check-label labelSemNegrito" for="permiteApiN">
                               <hl:message key="rotulo.nao"/>
                           </label>
                       </div>
                   </div>
               </div>
               <% } else { %>
               <input type="hidden" value="<%=podeApi%>" name="CSA_PERMITE_API">
               <% } %>
             <hl:htmlinput name="MM_update" type="hidden" value="form1"/>
           </form>
         </div>
        </div>
     </div>
    <%
      if (!TextHelper.isNull(csaCodigo)) {
        Iterator<?> itHistorico = lstOcorrencias.iterator();
        if (itHistorico.hasNext()) {
    %>
         <div class="col-sm-12 pl-0 pr-0">
           <div class="card">
             <div class="card-header hasIcon">
                <span class="card-header-icon"><svg width="26">
                  <use xlink:href="#i-consignacao"></use></svg></span>
                <h2 class="card-header-title"><hl:message key="rotulo.historico"/></h2>
             </div>
             <div class="card-body table-responsive">
               <table class="table table-striped table-hover">
                 <thead>
                   <tr>
                     <th scope="col" width="10%"><hl:message key="rotulo.ocorrencia.csa.data"/></th>
                     <th scope="col" width="10%"><hl:message key="rotulo.ocorrencia.csa.responsavel"/></th>
                     <th scope="col" width="10%"><hl:message key="rotulo.ocorrencia.csa.tipo"/></th>
                     <th scope="col" width="10%"><hl:message key="rotulo.ocorrencia.csa.descricao"/></th>
                     <th scope="col" width="10%"><hl:message key="rotulo.ocorrencia.csa.ip"/></th>
                   </tr>
                 </thead>
                 <tbody>
                 <%
                    int i = 0;
                    while (itHistorico.hasNext()) { 
                      CustomTransferObject cto = (CustomTransferObject) itHistorico.next();

                      cto = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) cto, null, responsavel);

                      String occData = DateHelper.toDateTimeString((Date) cto.getAttribute(Columns.OCC_DATA));

                      String loginOccResponsavel = cto.getAttribute(Columns.USU_LOGIN) != null ? cto.getAttribute(Columns.USU_LOGIN).toString() : "";
                      String occResponsavel = (loginOccResponsavel.equalsIgnoreCase((String) cto.getAttribute(Columns.USU_CODIGO)) && cto.getAttribute(Columns.USU_TIPO_BLOQ) != null) ? 
                              cto.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)" : loginOccResponsavel;
                      String occTipo = cto.getAttribute(Columns.TOC_DESCRICAO).toString();
                      String occObs = cto.getAttribute(Columns.OCC_OBS).toString();
                      String tpeDescricao = (String) cto.getAttribute(Columns.TPE_DESCRICAO);
                      String occIpAcesso = cto.getAttribute(Columns.OCC_IP_ACESSO) != null ? cto.getAttribute(Columns.OCC_IP_ACESSO).toString() : "";
                 %>
                    <tr>
                      <td><%=TextHelper.forHtmlContent(occData)%></td>
                      <td><%=TextHelper.forHtmlContent(occResponsavel)%></td>
                      <td><%=TextHelper.forHtmlContent(occTipo + (!TextHelper.isNull(tpeDescricao) ? ": " + tpeDescricao : ""))%></td>
                      <td><%=JspHelper.formataMsgOca(occObs)%></td>
                      <td><%=TextHelper.forHtmlContent(occIpAcesso)%></td>
                    </tr>
                 <% } %>
                 </tbody>
                 <tfoot>
                     <tr><td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.historico.disponivel", responsavel) + " - " %><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td></tr>
                 </tfoot>
               </table>
             </div>
             <div class="card-footer">
                <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
             </div>
           </div>
         </div>
    <%  
        }
      }   %>
    <div id="actions" class="btn-action">
      <%if (podeEditarCsa || podeEditarEnderecoAcesso) {%>
         <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(btnCancelar)%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
         <a class="btn btn-primary" href="#no-back" onClick="if (validaCodigoInternoZetra(false)) { montaListaIps('csa_ip_acesso','listaIps'); montaListaIps('csa_ddns_acesso','listaDDNSs'); habilitaCampos(); return false; }"><hl:message key="rotulo.botao.salvar"/></a>
      <%} else {%>
         <a class="btn btn-primary" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(btnCancelar)%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
      <%} %>
    </div>
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
<script type="text/JavaScript" src="../js/listagem.js"></script>
<script type="text/JavaScript">
f0 = document.forms[0];
var formValid = true;

<% if (responsavel.isSup()) { %>
  var arrayBancos = <%=(String)JspHelper.geraArrayBancos(true, responsavel)%>;
  var arrayNcaInstFinanceira = ['<%=NaturezaConsignatariaEnum.INSTITUICAO_FINANCEIRA.getCodigo()%>',
                                '<%=NaturezaConsignatariaEnum.INSTITUICAO_FINANCEIRA_PUBLICA.getCodigo()%>',
                                '<%=NaturezaConsignatariaEnum.INSTITUICAO_FINANCEIRA_MISTA.getCodigo()%>'];
<% } %>

  function formLoad() {
<% if (responsavel.isSup()) { %>
    // Filtra o combo de bancos com o valor pré-selecionado
    var banco = '<%=TextHelper.forJavaScriptBlock(csaIdentificadorInterno)%>';
    if (document.forms[0].CSA_IDENTIFICADOR_INTERNO != undefined) {
       AtualizaFiltraComboExt(document.forms[0].CSA_IDENTIFICADOR_INTERNO, arrayBancos, '', '', banco, false, true, '<%=ApplicationResourcesHelper.getMessage("rotulo.consignataria.codigo.zetrasoft.outro", responsavel)%>', '<%=ApplicationResourcesHelper.getMessage("rotulo.consignataria.codigo.zetrasoft.outro", responsavel)%>');
    }

    setarMascaraCodInterno('<%=(consignataria != null && consignataria.getCsaNcaNatureza() != null) ? consignataria.getCsaNcaNatureza() : ""%>');
<% } %>

    verificaCodigoZetra();
  }

    function setarMascaraCodInterno(ncaCodigo) {
  <% if (responsavel.isSup()) { %>
      var idInterno = document.forms[0].ajudaBanco;
      var codZetra = document.forms[0].CSA_IDENTIFICADOR_INTERNO;

      if (idInterno == undefined || idInterno == null) {
          return false;
      }

      if (ncaCodigo == null || ncaCodigo == '') {           
        idInterno.onkeypress = null;
        SetarEventoMascaraV4(idInterno,'UUDDDDD',true);
        fout(idInterno);
        ValidaMascara(idInterno);
      } else {
        var instFinanceira = false;
        var i = 0;
        while (i < arrayNcaInstFinanceira.length && !instFinanceira) {
          if (arrayNcaInstFinanceira[i] == ncaCodigo) {
            idInterno.onkeypress = null; 
            SetarEventoMascaraV4(idInterno,'UUDDDDD',true);
            fout(idInterno);
            ValidaMascara(idInterno);
            instFinanceira = true;
          }
          i++;
      }

      if (!instFinanceira) {
        idInterno.onkeypress = null;
        SetarEventoMascaraV4(idInterno,'UUDDDDD',true);
        fout(idInterno);
        ValidaMascara(idInterno); 
      }
    }
<% } %>
  }

  function validaCodigoInternoZetra(verificaCampo) {
      var codZetra = document.forms[0].CSA_IDENTIFICADOR_INTERNO;
      var ajudaBanco = document.forms[0].ajudaBanco;
      
      // Não valida se não preenche código
      if (codZetra == undefined || codZetra == null) {
          return true;
      }
      
	  if (verificaCampo && !IsNulo(ajudaBanco)) {
		  SelecionaComboMsg(codZetra, ajudaBanco.value, mensagem('mensagem.erro.banco.invalido'));
	  }
	  
      if (codZetra != undefined && codZetra != null && !IsNulo(ajudaBanco) &&
   		  codZetra.value == '<%=ApplicationResourcesHelper.getMessage("rotulo.consignataria.codigo.zetrasoft.outro", responsavel)%>' && ajudaBanco.value != '') {
		  var codInternoAntigo = '<%=TextHelper.forJavaScriptBlock(csaIdentificadorInterno)%>';
		  if (codInternoAntigo != '') {
			  codInternoAntigo = codInternoAntigo.replace(/^\D+/g, '');
		  }
		  var numInternoBanco = ajudaBanco.value.replace(/^\D+/g, '');
		  if (codInternoAntigo != numInternoBanco && numInternoBanco >= 80000) {
	  		alert("<hl:message key='mensagem.codigo.zetra.outro.maximo'/>");
	  		ajudaBanco.focus();
	 		return false;
		  }
	  }
      
      return true;
  }    

  function validaNaturezaParaCodigoAns(ncaCodigo) {
    if(<%=ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_CSA_CODIGO_ANS, responsavel)%>) {

      if (ncaCodigo == <%=CodedValues.NCA_CODIGO_OPERADORA_BENEFICIOS%> && <%=responsavel.isCseSup()%>) {
          f0.CSA_CODIGO_ANS.disabled = false;
	   } else {
             if (<%=!responsavel.isCsa()%>) {
                 f0.CSA_CODIGO_ANS.value = "";
               }
	        f0.CSA_CODIGO_ANS.disabled = true;
	     }
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

  function replaceCaracteresInvalidos(emailString)  {
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

  function habilitaCampos() {
      <% if (responsavel.isSup()) { %>
        <% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_CSA_IDENTIFICADOR_INTERNO, responsavel)) { %>
              var codZetra = f0.CSA_IDENTIFICADOR_INTERNO;
              var ajudaBanco = f0.ajudaBanco;

              if ((codZetra.value == null || codZetra.value == '')) {
                alert("<hl:message key='mensagem.informe.codigo.zetra'/>");
                codZetra.focus();
                return false;
              } else if (codZetra.value == "<%=ApplicationResourcesHelper.getMessage("rotulo.consignataria.codigo.zetrasoft.outro", responsavel)%>" && ajudaBanco.value == "") {
        	  		alert("<hl:message key='mensagem.informe.codigo.zetra.outro'/>");
        	  		ajudaBanco.focus();
            		return false;
              } else if (codZetra.value == "<%=ApplicationResourcesHelper.getMessage("rotulo.consignataria.codigo.zetrasoft.outro", responsavel)%>" && ajudaBanco.value != "") {
        		  var numInternoBanco = ajudaBanco.value.replace(/^\D+/g, '');
        		  if (numInternoBanco > 80000) {
        	  		alert("<hl:message key='mensagem.codigo.zetra.outro.maximo'/>");
        	  		ajudaBanco.focus();
        	 		return false;
        		  }
              }
        <% } 
        } %>

    <% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_CSA_CNPJ, responsavel)) { %>
    	if (f0.CSA_CNPJ.value == null || f0.CSA_CNPJ.value == '') {
    		  alert('<hl:message key="mensagem.informe.consignantaria.cnpj"/>');
    		  f0.CSA_CNPJ.focus();
    		  return false;
    	}
    <% } %>

    <% if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_CSA_CNPJ_CTA, responsavel)) { %>
        if (f0.CSA_CNPJ_CTA.value == null || f0.CSA_CNPJ_CTA.value == '') {
        	  alert('<hl:message key="mensagem.informe.consignantaria.cnpj.dados.bancarios"/>');
        	  f0.CSA_CNPJ_CTA.focus();
        	  return false;
        }
    <% } %>

    <%if(possibilitaCredenciamento) { %>
    	var iniciaCredenciamento = document.getElementById("iniciaCredenciamentoS").value;
    	var csaEmail = document.getElementById("CSA_EMAIL");
    	
    	if((iniciaCredenciamento.value != null || iniciaCredenciamento != '' || iniciaCredenciamento != 'undefined') && (csaEmail != null && csaEmail.value == null || csaEmail.value == '' || csaEmail.value == 'undefined')){
      	  alert('<hl:message key="mensagem.informe.email.consignataria.credenciamento"/>');
    	  return false;
    	}
    <%}%>
	
    let campoEmailNotificacaoRco = document.forms[0].CSA_EMAIL_NOTIFICACAO_RCO;
    if (<%=podeNotificarCsaAlteracaoRegraConvenio%> && !isEmailValid(campoEmailNotificacaoRco.value)) {
      alert('<hl:message key="mensagem.erro.email.invalido"/>');
      campoEmailNotificacaoRco.focus();
      return false;
    }

  	if (vf_cadastro_csa_v4()) {    
      enableAll();

      f0.submit();     
    } else {
      return false;
    }
  }

  function verificaCodigoZetra() {
	  if (typeof $("#CSA_IDENTIFICADOR_INTERNO").val() !== "undefined" && $("#CSA_IDENTIFICADOR_INTERNO").val() != "<%=ApplicationResourcesHelper.getMessage("rotulo.consignataria.codigo.zetrasoft.outro", responsavel)%>") {
		  document.getElementById("divAjudaBanco").setAttribute('class', 'form-group col-sm-3 d-none');
		  document.getElementById("ajudaBanco").setAttribute('disabled', 'disabled');
	  } else if (document.getElementById("ajudaBanco") != undefined) {
		  document.getElementById("ajudaBanco").removeAttribute('disabled');
		  document.getElementById("divAjudaBanco").removeAttribute('class');
		  document.getElementById("divAjudaBanco").setAttribute('class', 'form-group col-sm-3');
		  document.getElementById("divAjudaBanco").focus();
	  }
  }


  function imprime() {
	window.print();
  }
  
  function validaWhatsapp() {
  	var whatsapp = document.getElementById("CSA_WHATSAPP");
  	
  	if(!(/^(0[0-9]00)/.test(whatsapp.value))){
  		document.getElementById("CSA_DDD_TELEFONE").removeAttribute('disabled');
  		document.getElementById("CSA_DDD_TELEFONE").focus();
  	} else {
  		document.getElementById("CSA_DDD_TELEFONE").value = "";
  		document.getElementById("CSA_DDD_TELEFONE").setAttribute("disabled", true);
  	}
  		
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