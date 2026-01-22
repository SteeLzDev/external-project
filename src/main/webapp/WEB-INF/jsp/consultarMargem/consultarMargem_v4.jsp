<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
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

<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />

<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

  String tipo = (String) request.getAttribute("tipo");
  String rseCodigo = (String) request.getAttribute("RSE_CODIGO");

  // quando a chamada não vem da tela inicial de pesquisa de margem
  String linkRet = (String) request.getAttribute("linkRetHistoricoFluxo");

  String rotuloBotaoCancelar = ApplicationResourcesHelper.getMessage("rotulo.botao.cancelar", responsavel);
  String rotuloBotaoPesquisar = ApplicationResourcesHelper.getMessage("rotulo.botao.pesquisar", responsavel);

  // Exibição da permissão para visualização de margem para servidores bloqueados
  boolean permissaoVisualizarMargemServidoresBloqueados = (request.getAttribute("permissaoVisualizarMargemServidoresBloqueados") != null && ((boolean) request.getAttribute("permissaoVisualizarMargemServidoresBloqueados")));

  // Exibição de margem
  boolean podeMostrarMargem = (request.getAttribute("podeMostrarMargem") != null);
  boolean exibeAlgumaMargem = (request.getAttribute("exibeAlgumaMargem") != null);

  // Verifica se a senha para consulta de margem foi digitada e validada corretamente
  boolean senhaServidorOK = (request.getAttribute("senhaServidorOK") != null);

  // Mostra os convenios bloqueados
  boolean temConvenioBloqueado = (request.getAttribute("temConvenioBloqueado") != null);

  // Parâmetro para exibição do histórico de liquidação/interrupções antecipadas
  boolean exibeHistLiqAntecipadas = (request.getAttribute("exibeHistLiqAntecipadas") != null);
  String svcCodigo = (String) request.getAttribute("svcCodigo");

  // Parâmetro de obrigatoriedade de CPF e Matrícula
  boolean requerMatriculaCpf = (request.getAttribute("requerMatriculaCpf") != null);

  //Parâmetro de obrigatoriedade de data de nascimento
  boolean requerDataNascimento = (request.getAttribute("requerDataNascimento") != null);
  
  // Parametro de sistema que exige ou não a senha para visualizar margem
  boolean exigeSenhaConsultaMargem = (request.getAttribute("exigeSenhaConsultaMargem") != null);
  boolean senhaObrigatoriaConsulta = (request.getAttribute("senhaObrigatoriaConsulta") != null);

  // Parâmetro que habilita geração de OTP quando senha for obrigatória. Aqui irá omitir
  // o campo de senha, pois não é possível gerar o OTP sem saber quem é
  boolean geraSenhaAutOtp = (request.getAttribute("geraSenhaAutOtp") != null);

  // Parametro que mostra a composição da margem do servidor na reserva.
  boolean boolTpcPmtCompMargem = (boolean) request.getAttribute("boolTpcPmtCompMargem");

  // Parametro para exibição de análise de risco cadastrado pela CSA
  boolean temRiscoPelaCsa = (boolean) request.getAttribute("temRiscoPelaCsa");
  String riscoCsa = (String) request.getAttribute("ARR_RISCO");

  // Parâmetro para exibição de variação de margem
  boolean possuiVariacaoMargem = (boolean) request.getAttribute("possuiVariacaoMargem");
  
  //Parâmetro para exibir o filtro vínculo
  boolean exibeFiltroVinculo = (Boolean) request.getAttribute("exibeFiltroVinculo");
  
  // Permissão para bloquear/desbloquear convênios
  boolean podeBloquearDesbloquearConvenios = (request.getAttribute("podeBloquearDesbloquearConvenios") != null && ((boolean) request.getAttribute("podeBloquearDesbloquearConvenios")));

  boolean exibeCaptcha = (Boolean) request.getAttribute("exibeCaptcha");
  boolean exibeCaptchaAvancado = (Boolean) request.getAttribute("exibeCaptchaAvancado");
  boolean exibeCaptchaDeficiente = (Boolean) request.getAttribute("exibeCaptchaDeficiente");
  String infoMotivoBloqueio = (String) request.getAttribute("infoMotivoBloqueio");
  String msgPertenceCategoria = (String) request.getAttribute("msgPertenceCategoria");
  String infoMotivoJudicial = (String) request.getAttribute("infoMotivoJudicial");
  boolean verificaAutorizacaoSemSenha = request.getAttribute("verificaAutorizacaoSemSenha") != null;
  boolean naoExigeVlrParcelaCseOrg = responsavel.isCseOrg() && ParamSist.getBoolParamSist(CodedValues.TPC_SEMPRE_EXIBIR_VALOR_MARGEM_CSE_ORG, responsavel);
%>

<c:set var="title">
  <hl:message key="rotulo.consultar.margem.titulo"/>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="bodyContent">
<%
if (permissaoVisualizarMargemServidoresBloqueados) {
%>
  <div class="alert alert-warning" role="alert" id="idInfoMotivoBloqueio">
    <%if(!TextHelper.isNull(infoMotivoBloqueio) && responsavel.isCseSupOrg()){ %>
        <span name="bloqueio" id="idMsgInfoSession"><%=infoMotivoBloqueio%></span>
    <%} else { %>
        <span name="bloqueio" id="idMsgInfoSession"><hl:message key="mensagem.informacao.situacao.servidor.bloqueado.papel.permitido.gerar.margem"/></span>
    <%} %>
  </div>
<%
}
if (!TextHelper.isNull(msgPertenceCategoria) && responsavel.isCsaCor()) {
%>
	<div class="alert alert-warning" role="alert">
	    <span id="idMsgInfoSession"><%=msgPertenceCategoria%></span>
	</div>
<%
}
if (!TextHelper.isNull(infoMotivoJudicial) && responsavel.isCseSupOrg()) {
%>
	<div id="idInfoMotivoJudicial" style="display: none;">
	    <span name="bloqueio"><%=infoMotivoJudicial%></span>
	</div>
<%
}
%>
<form action="../v3/consultarMargem?<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="form1">
<%if(verificaAutorizacaoSemSenha) { %>
      <%--Modal Senha Servidor --%>
		<div class="modal fade" id="modalSenhaSer" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true" >
		   <div class="modal-dialog" role="document">
		     <div class="modal-content">
		       <div class="modal-header pb-0">
		         <span class="modal-title about-title mb-0" id="exampleModalLabel"><hl:message key="mensagem.reservar.margem.senha.servidor.obrigatoria"/></span>
		         <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
		           <span aria-hidden="true">&times;</span>
		         </button>
		       </div>
		      <div class="modal-body">
			      <div class="alert alert-warning mb-3" id="msgAlertFim" role="alert" style="display: none;">
		    	 	 <p class="mb-1"><hl:message key="mensagem.consulta.margem.sem.senha.retirado"/></p>
		    	  </div>

		      	  <hl:senhaServidorv4 senhaObrigatoria="<%=String.valueOf(senhaObrigatoriaConsulta)%>"
		                                   senhaParaAutorizacaoReserva="false"
		                                   nomeCampoSenhaCriptografada="serAutorizacao"
		                                   nf="btnPesquisar"
		                                   classe="form-control"
		                                   separador2pontos="false"
		                                   comTagDD="false"/>
		      </div>
		      <div class="modal-footer pt-0">
		        <div class="btn-action mt-2 mb-0">
		          <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#" alt="<hl:message key="rotulo.botao.cancelar"/>" title="<hl:message key="rotulo.botao.cancelar"/>">
		            <hl:message key="rotulo.botao.cancelar" />
		          </a>
                  <a class="btn btn-primary" id="btnPesquisar" href="#no-back"
                     onClick="if(validaSubmit(true)); return false;"><hl:message key="rotulo.botao.confirmar"/></a>
		        </div>
		      </div>
		    </div>
		  </div>
		</div>
		<% } %>
      	<div class="modal fade" id="modalDetalheMargem" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true" >
		   <div class="modal-dialog" role="document">
		     <div class="modal-content">
		       <div class="modal-header pb-0">
		         <div  id="txtModalMargem"></div>
		         <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
		           <span aria-hidden="true">&times;</span>
		         </button>
		       </div>
		      <div class="modal-body">
		      <dl class="row data-list">
		      <dt class="col-6" id="marFolhaTexto"><hl:message key="rotulo.extrato.margem.folha"/>:</dt>
		      <dd class="col-6" id="marFolhaVlr">&nbsp;</dd>
		      <dt class="col-6" id="marUsadaTexto"><hl:message key="rotulo.extrato.margem.usada"/>:</dt>
		      <dd class="col-6" id="marUsadaVlr">&nbsp;</dd>
		      </dl>
		      </div>
		      <div class="modal-footer pt-0">
		        <div class="btn-action mt-2">
		          <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#" alt="<hl:message key="rotulo.botao.cancelar"/>" title="<hl:message key="rotulo.botao.cancelar"/>">
		            <hl:message key="rotulo.botao.cancelar" />
		          </a>
                </div>
		      </div>
		    </div>
		  </div>
		</div>
<% if (request.getAttribute("servidor") != null) { %>
    <div class="page-title">
      <div class="row d-print-none">
        <div class="col-sm-12 col-md-12 mb-2">
          <div class="float-end">                                    
            <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit" ><hl:message key="rotulo.acoes"/></button>
            <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes">
              
              <%-- Extrato de margem --%>              
              <% if (responsavel.isCseSupOrg() && responsavel.temPermissao(CodedValues.FUN_CONS_EXTRATO_MARGEM)) { %>
                 <a class="dropdown-item" href="#no-back" onClick="postData('../v3/consultarExtratoMargem?acao=iniciar&RSE_CODIGO=<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                    <hl:message key="rotulo.extrato.margem.acao"/>
                 </a>
              <% } %>
              
              <%-- Histórico de margem --%>
              <% if (responsavel.isCseSupOrg() && responsavel.temPermissao(CodedValues.FUN_CONS_HISTORICO_MARGEM)) { %>
                 <a class="dropdown-item" href="#no-back" onClick="postData('../v3/visualizarHistorico?acao=iniciar&RSE_CODIGO=<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                    <hl:message key="rotulo.historico.margem.acao"/>
                 </a>
              <% } %>
              
              <%-- Composição de margem --%>
              <% if (podeMostrarMargem && boolTpcPmtCompMargem) { %>
                <% if (senhaServidorOK || !exigeSenhaConsultaMargem) { %>              
                 <a class="dropdown-item" href="#no-back" onclick="postData('../v3/consultarComposicaoMargem?acao=comp_margem&RSE_CODIGO=<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');">
                  <hl:message key="rotulo.composicao.margem.acao"/>
                </a>
                <% } else { %>                  
                    <a class="dropdown-item" href="#no-back" onclick="postData('../v3/consultarComposicaoMargem?acao=iniciar&RSE_MATRICULA=<%=TextHelper.forJavaScriptAttribute(JspHelper.verificaVarQryStr(request, "RSE_MATRICULA"))%>&<%=SynchronizerToken.generateToken4URL(request)%>');">
                      <hl:message key="rotulo.composicao.margem.acao"/>
                    </a>              
                <% } %>
              <% } %>
              
              <%-- Variação de margem --%>
              <% if (possuiVariacaoMargem && exibeAlgumaMargem) { %>
                <% if (senhaServidorOK || !exigeSenhaConsultaMargem) { %>
                  <a class="dropdown-item" href="#no-back" onclick="postData('../v3/consultarVariacaoMargem?acao=iniciarMargem&RSE_CODIGO=<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');">
                    <hl:message key="rotulo.variacao.margem.acao"/>
                  </a>                  
                <% } else { %>                  
                  <a class="dropdown-item" href="#no-back" onclick="postData('../v3/consultarVariacaoMargem?acao=iniciar&RSE_MATRICULA=<%=TextHelper.forJavaScriptAttribute(JspHelper.verificaVarQryStr(request, "RSE_MATRICULA"))%>&<%=SynchronizerToken.generateToken4URL(request)%>');">
                    <hl:message key="rotulo.variacao.margem.acao"/>
                  </a>              
                <% } %>
              <% } %>
            
            <%-- Histórico de liquidação antecipada --%>
            <% if (exibeHistLiqAntecipadas) { %>
               <a class="dropdown-item" href="#no-back" onclick="openModalSubAcesso('../v3/consultarMargem?RSE_MATRICULA=<%=TextHelper.forJavaScriptAttribute(JspHelper.verificaVarQryStr(request, "RSE_MATRICULA"))%>&RSE_CODIGO=<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>&SVC_CODIGO=<%=TextHelper.forJavaScriptAttribute(svcCodigo)%>&SER_NOME=<%=TextHelper.encode64(request.getAttribute("serNome").toString())%>&acao=listarHistLiquidacoesAntecipadas&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>')">
                  <hl:message key="rotulo.historico.liq.antecipada.acao"/>
               </a>
            <% } %>
            
            <%-- Mostra convenios bloqueados --%>
            <% if (temConvenioBloqueado) { %>
               <a class="dropdown-item" href="#no-back" onclick="postData('../v3/consultarConveniosBloqueados?acao=iniciar&RSE_CODIGO=<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');">
                  <hl:message key="rotulo.convenios.bloq.acao"/>
               </a>
            <% } %>
             
            <%-- Declaração de margem --%>
            <% if (responsavel.temPermissao(CodedValues.FUN_EMITIR_DECLARACAO_MARGEM)) { %>
               <a class="dropdown-item" href="#no-back" onClick="postData('../v3/declararMargem?acao=iniciar&RSE_CODIGO=<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                  <hl:message key="rotulo.declaracao.margem.acao"/>
               </a>
            <% } %>

            <%-- DESENV-16777 - Cascavel - Criar botão para bloquear servidor para uma determinada CSA.--%>
            <% if (podeBloquearDesbloquearConvenios) { %>
               <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarConvenioServidor?acao=iniciar&RSE_CODIGO=<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>&RSE_MATRICULA=<%=TextHelper.forJavaScriptAttribute(JspHelper.verificaVarQryStr(request, "RSE_MATRICULA"))%>&SER_NOME=<%=TextHelper.encode64(request.getAttribute("serNome").toString())%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                  <hl:message key="<%=TextHelper.forHtmlAttribute(request.getAttribute("msgBloquearDesbloquearConvenios"))%>"/>
               </a>
            <% } %>
            
            <%if (responsavel.temPermissao(CodedValues.FUN_ANALISAR_VARIACAO_MARGEM)) { %>
				<a class="dropdown-item" href="#no-back" onClick="postData('../v3/analisarVariacaoMargem?acao=iniciar&RSE_CODIGO=<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                  <hl:message key="rotulo.analisar.variacao.margem.acao"/>
              	</a>
			<%} %>
              <a class="dropdown-item" href="#no-back" onClick="gerarPDFConsultaMargem();"> <hl:message key="rotulo.botao.gerar.pdf"/> </a>

            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="row">
        <div class="col-sm-5 col-md-4" id="columnSearchDropdown">
          <div class="card">
            <div class="card-header pl-3" id="cardHeaderDropdown">
              <span class="card-header-icon-right" onclick="dropdownControl()" id="cardHeaderIconDropdown" style="cursor:pointer;">
                <svg width="26">
                  <use xlink:href="../img/sprite.svg#i-expand-arrows" id="svgUseDropdown"></use>
                </svg>
              </span>
              <h2 class="card-header-title" id="cardHeaderTitleDropdown"><hl:message key="rotulo.acao.pesquisar"/></h2>
            </div>
            <div class="card-body" id="bodyCardDropdown">
              <% if (!naoExigeVlrParcelaCseOrg && (!podeMostrarMargem || (exigeSenhaConsultaMargem && !senhaObrigatoriaConsulta && !geraSenhaAutOtp))) {%>
                <div class="row">
                  <div class="form-group col-sm">
                    <label for="ADE_VLR"><hl:message key="rotulo.consultar.margem.valor.parcela"/></label>
                    <hl:htmlinput name="ADE_VLR" 
                                  di="ADE_VLR" 
                                  placeHolder="<%= ApplicationResourcesHelper.getMessage("rotulo.consultar.margem.valor.parcela.placeholder", responsavel) %>"
                                  type="text" 
                                  classe="form-control"
                                  mask="#F11" 
                                  size="8"
                                  value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "ADE_VLR"))%>"
                                  onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"                                    
                     />            
                  </div>  
                </div>  
              <% } %>
              <div class="row">
              <div class="col-sm">
                  <%@ include file="include_campo_matricula_v4.jsp" %>
              </div>
              </div>
              <div class="row">
                <div class="form-group col-sm">
                  <hl:campoCPFv4 
                      nf="<%=TextHelper.forHtmlAttribute(!exigeSenhaConsultaMargem ? "btnEnvia" : "senha")%>"
                      placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf.completo", responsavel) %>"
                  />
                </div>
              </div>
			  <% if (requerDataNascimento) { %>
	          <div class="row">
	            <div class="form-group col-sm-6">
	              <label for="SER_DATA_NASC"><hl:message key="rotulo.servidor.dataNasc" fieldKey="SER_DATA_NASC"/></label>
	              <hl:htmlinput name="SER_DATA_NASC"
	                  di="SER_DATA_NASC"
	                  type="text"
	                  classe="form-control"
	                  value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "SER_DATA_NASC"))%>"
	                  mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
	                  placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"                             
	              />
	            </div>
	          </div>
	          <% } %>
              <% if (exibeFiltroVinculo) { %>
              <div class="row">
                <div class="form-group col-sm">
                  <label
                    for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_VINCULO)%>"><hl:message
                      key="rotulo.servidor.vinculo.opcional" /></label>
                  <hl:htmlcombo listName="listaVincRegSer"
                    name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_VINCULO)%>"
                    di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_VINCULO)%>"
                    fieldValue="<%=TextHelper.forHtmlAttribute(Columns.VRS_CODIGO)%>"
                    fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.VRS_DESCRICAO)%>"
                    notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                    configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_VINCULO)%>"
                    classe="form-control">
                  </hl:htmlcombo>
                </div>
              </div>
              <%
                  }
              %>
              <%
                  if (exigeSenhaConsultaMargem && !geraSenhaAutOtp) {
              %>
              <%
                  String mascaraLogin = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR, responsavel);
                        if (!TextHelper.isNull(mascaraLogin)) {
              %>
                <div class="row">
                  <div class="form-group col-sm">
                    <label for="serLogin"><hl:message key="rotulo.usuario.servidor.singular"/></label>              
                    <hl:htmlinput name="serLogin"
                                 di="serLogin"
                                 type="text"
                                 classe="form-control"
                                 size="8"
                                 mask="<%=TextHelper.forHtmlAttribute(mascaraLogin)%>"
                    />
                  </div>
                </div>
              <%
                  }
              %>
              <%if (!verificaAutorizacaoSemSenha) { %>
              <div class="row">
                <div class="form-group col-sm">
                  <hl:senhaServidorv4 senhaObrigatoria="<%=String.valueOf(senhaObrigatoriaConsulta)%>"
                                      senhaParaAutorizacaoReserva="false"
                                      nomeCampoSenhaCriptografada="serAutorizacao"
                                      rseCodigo="<%=rseCodigo%>"
                                      comTagDD="false"
                                      classe="form-control"
                                      nf="btnEnvia"
                                      exibirQuandoOpcional="true"

                  />
                </div>
              </div>
              <% } %>
              <%
                  }
              %>

              <%
                  if (exibeCaptcha || exibeCaptchaAvancado || exibeCaptchaDeficiente) {
              %>
                <div class="row"> 
                <%
                     if (!exibeCaptchaAvancado) {
                 %>
                  <div class="form-group col-md-12">
                    <label for="captcha"><hl:message key="rotulo.captcha.codigo"/>:</label>
                    <input type="text" class="form-control" id="captcha" name="captcha" placeholder='<hl:message key="mensagem.informacao.login.digite.codigo.acesso"/>'>
                  </div>
                <%
                    }
                %>
                  <div class="form-group col-sm-12 pl-0">
                    <div class="captcha pl-3">
                      <%
                          if (exibeCaptcha) {
                      %>
                        <img name="captcha_img" src="../captcha.jpg?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>" alt='<hl:message key="rotulo.captcha.codigo"/>' title='<hl:message key="rotulo.captcha.codigo"/>'/>
                          <div>
                          <a href="#no-back" onclick="reloadCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.codigo"/>' title='<hl:message key="rotulo.captcha.novo.codigo"/>' border="0"/></a>
                          <a href="#no-back" class="btn-i-right pr-1" data-bs-toggle="popover" title="<hl:message key="rotulo.ajuda" />"
                             data-bs-content='<hl:message key="mensagem.ajuda.captcha.operacao.imagem.v3"/>'
                             data-original-title=<hl:message key="rotulo.ajuda" />> 
                            <img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda" />' title='<hl:message key="rotulo.ajuda" />' border="0">
                          </a>
                          </div>
                      <%
                          } else if (exibeCaptchaAvancado) {
                      %>
                          <hl:recaptcha />
                      <%
                          } else if (exibeCaptchaDeficiente) {
                      %>
                        <div id="divCaptchaSound"></div>
                        <a href="#no-back" onclick="reloadSimpleCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.audio"/>' title='<hl:message key="rotulo.captcha.novo.audio"/>' border="0"/></a>
                        <a href="#no-back" onclick="helpCaptcha3();"><img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda"/>' title='<hl:message key="rotulo.ajuda"/>' border="0"/></a>
                      <%
                          }
                      %>
                    </div>
                  </div>
                </div>
              <%
                  }
              %>
              <div class="row">
                <div class="form-group col-sm">
                  <div class="alert alert-info">
                    <p class="mb-0"><hl:message key="<%=TextHelper.forHtmlAttribute(request.getAttribute("chaveTextoAjuda"))%>"/></p>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="btn-action" id="btnEnviaDropdown">
          <% if (verificaAutorizacaoSemSenha) { %>
            <a class="btn btn-primary" name="btnEnvia" id="btnEnvia" href="#no-back" onClick="validaAutorizacaoSemSenha();">
              <svg width="20">
                <use xlink:href="#i-consultar"></use>
              </svg>
              <hl:message key="rotulo.acao.pesquisar"/>
            </a>
          <% } else { %>
            <a class="btn btn-primary" name="btnEnvia" id="btnEnvia" href="#no-back" onClick="validaSubmit(<%=verificaAutorizacaoSemSenha%>);">
              <svg width="20">
                <use xlink:href="#i-consultar"></use>
              </svg>
              <hl:message key="rotulo.acao.pesquisar"/>
            </a>
            <% } %>
          </div>
          </div>
          <div class="col-sm-7 col-md-8" id="columnMargemDropdown">
            <div class="card d-print-none" id="consultaMargem">
              <div class="card-header pl-3">
                <h2 class="card-header-title"><hl:message key="rotulo.consultar.margem.resultado"/></h2>
              </div>
              <div class="card-body">
                <dl class="row data-list">
                <%-- Utiliza a tag library DetalheServidorTag.java para exibir os dados do servidor --%>
                  <hl:detalharServidorv4 name="servidor" margem="lstMargens" scope="request" exibeIconConSer="true" exibeBloqueioSer="<%=temConvenioBloqueado%>" complementos="true"/>
                <%-- Fim dos dados da ADE --%>
                </dl>
              </div>
            </div>
            <div class="btn-action">        
              <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(linkRet, request) )%>')">
                <hl:message key="rotulo.botao.cancelar"/>
              </a>
            </div>
          </div>

<%
     } else {
 %>
     <%if (!verificaAutorizacaoSemSenha){ %>
	    <%= SynchronizerToken.generateHtmlToken(request) %>
    <%} %>
  <div class="row justify-content-md-center">
    <div class=" col-sm-12 form-check mt-2 form-group">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.acao.pesquisar"/></h2>
        </div>
        <div class="card-body">
          <%
          		if (!naoExigeVlrParcelaCseOrg && (!podeMostrarMargem || (exigeSenhaConsultaMargem && !senhaObrigatoriaConsulta && !geraSenhaAutOtp))) {
          %>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="ADE_VLR"><hl:message key="rotulo.consultar.margem.valor.parcela"/></label>
                <hl:htmlinput name="ADE_VLR" 
                    di="ADE_VLR" 
                    placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.consultar.margem.valor.parcela.placeholder", responsavel)%>"
                    type="text" 
                    classe="form-control"
                    mask="#F11" 
                    size="8"
                    value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "ADE_VLR"))%>"
                    onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"                                    
                />            
              </div>  
            </div>  
          <%
                }
            %>      
          <div class="row">
             <div class="col-sm-6">
              <%@ include file="include_campo_matricula_v4.jsp" %>            
            </div>
          </div>
          <div class="row">
            <div class="form-group col-sm-6">            
              <hl:campoCPFv4 
                nf="<%=TextHelper.forHtmlAttribute(!exigeSenhaConsultaMargem ? "btnEnvia" : "senha")%>"              
                placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf.completo", responsavel)%>"
              />
            </div>
          </div>
		  <% if (requerDataNascimento) { %>
          <div class="row">
            <div class="form-group col-sm-6">
              <label for="SER_DATA_NASC"><hl:message key="rotulo.servidor.dataNasc" fieldKey="SER_DATA_NASC"/></label>
              <hl:htmlinput name="SER_DATA_NASC"
                  di="SER_DATA_NASC"
                  type="text"
                  classe="form-control"
                  value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "SER_DATA_NASC"))%>"
                  mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                  placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"                             
              />
            </div>
          </div>
          <% } %>
          <%
              if(exibeFiltroVinculo) {
          %>
            <div class="row">
              <div class="form-group col-sm-6">
                <label
                  for="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_VINCULO)%>"><hl:message
                    key="rotulo.servidor.vinculo.opcional" /></label>
                <hl:htmlcombo listName="listaVincRegSer"
                  name="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_VINCULO)%>"
                  di="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_VINCULO)%>"
                  fieldValue="<%=TextHelper.forHtmlAttribute(Columns.VRS_CODIGO)%>"
                  fieldLabel="<%=TextHelper.forHtmlAttribute(Columns.VRS_DESCRICAO)%>"
                  notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>"
                  configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CADASTRAR_SERVIDOR_VINCULO)%>"
                  classe="form-control">
                </hl:htmlcombo>
              </div>
            </div>
            <%} %>          
          <% if (exigeSenhaConsultaMargem && !geraSenhaAutOtp) { %>
            <%
              String mascaraLogin = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR, responsavel);
              if (!TextHelper.isNull(mascaraLogin)) {
            %>
              <div class="row">
                <div class="form-group col-sm-6">
                  <label for="serLogin" ><hl:message key="rotulo.usuario.servidor.singular"/></label>              
                  <hl:htmlinput name="serLogin"
                     di="serLogin"
                     type="text"
                     classe="form-control"
                     size="8"
                     mask="<%=TextHelper.forHtmlAttribute(mascaraLogin)%>"
                  />
                </div>
              </div>
            <% } %>
          <%if (!verificaAutorizacaoSemSenha) { %>
            <div class="row">
              <div class="form-group col-sm-6">
                <hl:senhaServidorv4 senhaObrigatoria="<%=String.valueOf(senhaObrigatoriaConsulta)%>"                    
                    senhaParaAutorizacaoReserva="false"
                    nomeCampoSenhaCriptografada="serAutorizacao"
                    rseCodigo="<%=rseCodigo%>"
                    comTagDD="false"
                    classe="form-control"
                    nf="btnEnvia"
                    exibirQuandoOpcional="true"       
                />
              </div>
            </div>
          <%} %>
          <% } %> 
          <% if (exibeCaptcha || exibeCaptchaAvancado || exibeCaptchaDeficiente) { %>
            <div class="row"> 
            <% if (!exibeCaptchaAvancado) {%>
              <div class="form-group col-md-5">
                <label for="captcha"><hl:message key="rotulo.captcha.codigo"/>:</label>
                <input type="text" class="form-control" id="captcha" name="captcha" placeholder='<hl:message key="mensagem.informacao.login.digite.codigo.acesso"/>'>
              </div>
            <% } %> 
              <div class="form-group col-sm-6">
                <div class="captcha">
                  <% if (exibeCaptcha) { %>
                    <img name="captcha_img" src="../captcha.jpg?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>" alt='<hl:message key="rotulo.captcha.codigo"/>' title='<hl:message key="rotulo.captcha.codigo"/>'/>
                      <a href="#no-back" onclick="reloadCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.codigo"/>' title='<hl:message key="rotulo.captcha.novo.codigo"/>' border="0"/></a>
                      <a href="#no-back" class="btn-i-right pr-1" data-bs-toggle="popover" title="<hl:message key="rotulo.ajuda" />"
                         data-bs-content='<hl:message key="mensagem.ajuda.captcha.operacao.imagem.v3"/>'
                         data-original-title=<hl:message key="rotulo.ajuda" />> 
                        <img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda" />' title='<hl:message key="rotulo.ajuda" />' border="0">
                      </a>
                  <% } else if (exibeCaptchaAvancado) { %>
                      <hl:recaptcha />
                  <% } else if (exibeCaptchaDeficiente) {%>
                    <div id="divCaptchaSound"></div>
                    <a href="#no-back" onclick="reloadSimpleCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.audio"/>' title='<hl:message key="rotulo.captcha.novo.audio"/>' border="0"/></a>
                    <a href="#no-back" onclick="helpCaptcha3();"><img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda"/>' title='<hl:message key="rotulo.ajuda"/>' border="0"/></a>
                  <% } %>
                </div>
              </div>
            </div>
          <% } %>
          <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <div class="alert alert-info">
                <p class="mb-0"><hl:message key="<%=TextHelper.forHtmlAttribute(request.getAttribute("chaveTextoAjuda"))%>"/></p>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(linkRet, request) )%>')">
          <hl:message key="rotulo.botao.cancelar"/>
        </a>
        <% if (verificaAutorizacaoSemSenha) { %>
        <a class="btn btn-primary" name="btnEnvia" id="btnEnvia" href="#no-back"
           onClick="validaAutorizacaoSemSenha();">
          <svg width="20">
            <use xlink:href="#i-consultar"></use>
          </svg>
          <hl:message key="rotulo.acao.pesquisar"/>
        </a>
        <% } else { %>
        <a class="btn btn-primary" name="btnEnvia" id="btnEnvia" href="#no-back"
           onClick="validaSubmit(<%=verificaAutorizacaoSemSenha%>)">
          <svg width="20">
            <use xlink:href="#i-consultar"></use>
          </svg>
          <hl:message key="rotulo.acao.pesquisar"/>
        </a>
        <% } %>
      </div>
    </div>
  </div>
  <% if (!TextHelper.isNull(rseCodigo)) { %>
  <hl:htmlinput name="RSE_CODIGO"      type="hidden" di="RSE_CODIGO"    value="<%=rseCodigo%>" />
  <% } %>
<% } %>
<hl:htmlinput name="FORM"            type="hidden" di="FORM"          value="form1" />
<hl:htmlinput name="TIPO_LISTA"      type="hidden" di="TIPO_LISTA"    value="pesquisa" />
<hl:htmlinput name="acao"            type="hidden" di="acao"          value="${acao}" />
<hl:htmlinput name="linkRet"         type="hidden" di="linkRet"       value="" />
</form>
</c:set>
<c:set var="pageModals">
  <t:modalSubAcesso>
    <jsp:attribute name="titulo"><hl:message key="rotulo.historico.liq.antecipada.titulo"/></jsp:attribute>
  </t:modalSubAcesso>
</c:set>
<c:set var="javascript">

  <% if (exibeCaptchaAvancado) { %>
  <script src='https://www.google.com/recaptcha/api.js'></script>
  <% } %>
  <% if (exibeCaptchaDeficiente) {%>
  <script type="text/JavaScript">
    montaCaptchaSom();
  </script>
  <% } %>
  
  <script type="text/Javascript">


    if (typeof Cookies.get('dropdownSearch') !== 'undefined'){
      if(Cookies.get('dropdownSearch') == 'true'){
      	openSearchMenu();
      }else{
      	closeSearchMenu(); 
      }
    }else{
    	Cookies.set('dropdownSearch', 'true');
      openSearchMenu();  
    }
   
    function dropdownControl() {
      if(document.getElementById("bodyCardDropdown").classList.contains("hidden-search-dropdown")){
  		openSearchMenu();
      }else{
    	closeSearchMenu();
      }
    }
    
    function closeSearchMenu(){
      if(document.getElementById("bodyCardDropdown")){
        document.getElementById("bodyCardDropdown").classList.remove("show-search-dropdown");
        document.getElementById("bodyCardDropdown").classList.add("hidden-search-dropdown");
        document.getElementById("btnEnviaDropdown").classList.remove("show-search-dropdown");
        document.getElementById("btnEnviaDropdown").classList.add("hidden-search-dropdown");
        document.getElementById("cardHeaderTitleDropdown").classList.remove("show-search-dropdown");
        document.getElementById("cardHeaderTitleDropdown").classList.add("hidden-search-dropdown");
        document.getElementById("columnSearchDropdown").classList.remove("col-sm-5","col-md-4");
        document.getElementById("columnSearchDropdown").classList.add("col-auto");
        document.getElementById("columnMargemDropdown").classList.remove("col-sm-7", "col-md-8");
        document.getElementById("columnMargemDropdown").classList.add("col");
        document.getElementById("cardHeaderIconDropdown").classList.remove("card-header-icon-right");
        document.getElementById("cardHeaderIconDropdown").classList.add("card-header-icon");
        document.getElementById("cardHeaderDropdown").style.paddingRight = "38px";
        document.getElementById("cardHeaderDropdown").style.paddingTop = "40px";
        document.getElementById("cardHeaderDropdown").style.borderRadius = "0.625rem 0.625rem 0.625rem 0.625rem";
        document.getElementById("svgUseDropdown").setAttribute("xlink:href", "../img/sprite.svg#i-expand-arrows");    
        Cookies.set('dropdownSearch', 'false');
      }
    }
  
    function openSearchMenu(){
      if(document.getElementById("bodyCardDropdown")){
        document.getElementById("bodyCardDropdown").classList.remove("hidden-search-dropdown");
        document.getElementById("bodyCardDropdown").classList.add("show-search-dropdown");
        document.getElementById("cardHeaderTitleDropdown").classList.remove("hidden-search-dropdown");
        document.getElementById("cardHeaderTitleDropdown").classList.add("show-search-dropdown");
        document.getElementById("btnEnviaDropdown").classList.remove("hidden-search-dropdown");
        document.getElementById("btnEnviaDropdown").classList.add("show-search-dropdown");
        document.getElementById("columnSearchDropdown").classList.remove("col-auto");
        document.getElementById("columnSearchDropdown").classList.add("col-sm-5","col-md-4");
        document.getElementById("columnMargemDropdown").classList.remove("col");
        document.getElementById("columnMargemDropdown").classList.add("col-sm-7", "col-md-8");
        document.getElementById("cardHeaderIconDropdown").classList.remove("card-header-icon");
        document.getElementById("cardHeaderIconDropdown").classList.add("card-header-icon-right");
        document.getElementById("cardHeaderDropdown").style.paddingRight = "20px";
        document.getElementById("cardHeaderDropdown").style.paddingTop = "20px";
        document.getElementById("cardHeaderDropdown").style.borderRadius = "0.625rem 0.625rem 0rem 0rem";
        document.getElementById("svgUseDropdown").setAttribute("xlink:href", "../img/sprite.svg#i-colapse-arrows");
        Cookies.set('dropdownSearch', 'true');
      }
    }
  </script>
  
  <script type="text/JavaScript">
    f0 = document.forms[0];
    requerAmbos = <%=TextHelper.forJavaScriptBlock( requerMatriculaCpf )%>;
    requerDataNascimento = <%=TextHelper.forJavaScriptBlock( requerDataNascimento )%>;
    let retornoSenha = true;
    function campos(validaSenha, retornoSenha) {
      if((f0.ADE_VLR != null && f0.ADE_VLR.value == "") &&
         (f0.senha != null && f0.senha.value == "") &&
         (f0.serLogin != null && f0.serLogin.value == "")){
        alert ("<hl:message key="mensagem.consultar.margem.campo.obrigatorio"/>");
        f0.ADE_VLR.focus();
        return false;
      }
      
      if (requerDataNascimento && f0.SER_DATA_NASC != null) {
        // Se requer data de nascimento, verifica se foi informada
        var Controles = new Array("SER_DATA_NASC");
        var Msgs = new Array('<hl:message key="mensagem.informe.servidor.data.nascimento"/>');
        if (!ValidaCampos(Controles, Msgs)) {
          return false;
        }
      }

      var senhaObrigatoria = <%=senhaObrigatoriaConsulta%> && validaSenha && retornoSenha;
  
      if (f0.senha != null && f0.senha.value == '' && senhaObrigatoria) {
        alert("<hl:message key="mensagem.informe.ser.senha"/>");
        f0.senha.focus();
        return false;
      }
  
      if (f0.senha != null && f0.senha.value != ''){
        CriptografaSenha(f0.senha, f0.serAutorizacao, false);
        return true;
      } else {
        return true;
      }
    }

    function validaSubmit(validaSenha) {
      if (vf_pesquisa_servidor(requerAmbos) && campos(validaSenha, retornoSenha)) {
        if (typeof vfRseMatricula === 'function') {
          if (vfRseMatricula(true)) {
            f0.submit();
          } else {
            return false;
          }
        } else {
          f0.submit();
        }
      } else {
        return false;
      }
    }
  
    function gerarPDFConsultaMargem() {
      var dadosMargem = document.getElementById('consultaMargem').innerHTML;
      var temMotivoBloqueio = "<%= infoMotivoBloqueio %>";
      var temMotivoJudicial = "<%= infoMotivoJudicial %>";
	  
	  var mensagemBloqueioServidor = temMotivoBloqueio !== "null" && temMotivoBloqueio.lenght !== 0 ? document.getElementById('idInfoMotivoBloqueio').innerHTML : mensagemBloqueioServidor = "";
	  var mensagemDecisaoJudicial = temMotivoJudicial !== "null" && temMotivoJudicial.lenght !== 0 ? document.getElementById('idInfoMotivoJudicial').innerHTML : mensagemDecisaoJudicial = "";
      
      var doc = dadosMargem + mensagemBloqueioServidor + mensagemDecisaoJudicial;

      var dataToSend = JSON.stringify({'html': doc, 'rseCodigo': '<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>'});
      $.ajax({ url: "../v3/consultarMargem?acao=gerarPdf&<%=SynchronizerToken.generateToken4URL(request)%>&_skip_history_=true",
             type: "POST",
             contentType: "application/json; charset=utf-8",
             data: dataToSend ,
             responseType: 'arraybuffer',
             xhrFields: { responseType: 'blob' },
             success: function (response, status, xhr) {
                 var filename = "";
                 var disposition = xhr.getResponseHeader("Content-Disposition");
                 if (disposition && disposition.indexOf("filename") !== -1) {
                    var filenameRegex = /filename[^;=\n]*=(([""]).*?\2|[^;\n]*)/;
                    var matches = filenameRegex.exec(disposition);
                    if (matches != null && matches[1])
                        filename = matches[1].replace(/[""]/g, "");
                 }
                
                 var blob = new Blob([response], {type: 'application/pdf'});
                 var link = document.createElement('a');
                 link.href = window.URL.createObjectURL(blob);
                 link.download = filename;
                 link.click();
          	 },
             error: function (request, status, error) {
                 alert ("<hl:message key="mensagem.erro.download"/>");
          	 }
         });
    }

    <% if (verificaAutorizacaoSemSenha) { %>
    function validaAutorizacaoSemSenha() {
      var modalSenhaSer = new bootstrap.Modal(document.getElementById('modalSenhaSer'), {
        keyboard: false,
        backdrop: false
      });
      const matricula = f0.RSE_MATRICULA != null ? f0.RSE_MATRICULA.value : "";
      const cpf = f0.SER_CPF != null ? f0.SER_CPF.value : "";
      const parametrosValidacao = '&_skip_history_=true&RSE_MATRICULA=' + matricula + '&SER_CPF=' + cpf + '&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>';
      url = '../v3/consultarMargem?acao=validarAutorizacaoSemSenha' + parametrosValidacao;
      $.ajax({
        url: url,
        method: 'POST',
        dataType: 'json',
        success: function (data) {
          if (data.situacao === 'S') {
            retornoSenha = true;
            modalSenhaSer.show();
            return false;
          } else if (data.situacao === 'A') {
            retornoSenha = true;
            var msgAlertFim = document.getElementById("msgAlertFim");
            msgAlertFim.style.display = "block"

            modalSenhaSer.show();
            return false;
          } else {
            var form = document.getElementById("form1");
            var hiddenInput = document.getElementById("RSE_CODIGO");
            if (form != null && hiddenInput != null && hiddenInput != '') {
              form.removeChild(hiddenInput);
            } else if(form == null && hiddenInput != null && hiddenInput != '') {
            	hiddenInput.value = "";
            }
            retornoSenha = false;
            validaSubmit(true);
          }
        },
        error: function (error) {
          console.log('Erro:', error);
          validaSubmit(true);
        }
      });
    }
    <% } %>
  </script>
  <script type="text/javascript">
  function modalMargemDetalhe(marUsada, marRestante, txtNomeMargem) {
      $('#txtMargem').remove();
      $('#vlrFolha').remove();
      $('#vlrUsada').remove();
      $('#txtModalMargem').append("<span class=\"modal-title about-title mb-0\" id=\"txtMargem\">" + txtNomeMargem + "</span>")
      $('#marFolhaVlr').append("<span id=\"vlrFolha\">" + marRestante + "</span>");
      $('#marUsadaVlr').append("<span id=\"vlrUsada\">" + marUsada + "</span>");
       var modalDetalheMargem = new bootstrap.Modal(document.getElementById('modalDetalheMargem'), {
        keyboard: false,
        backdrop: false
      });

      modalDetalheMargem.show();
    }
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="pageModals">${pageModals}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
