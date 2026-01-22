<%--
* <p>Title: selecionarServidor_v4.jsp</p>
* <p>Description: seleção de servidor</p>
* <p>Copyright: Copyright (c) 2002-2017</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TransferObjectHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean omiteMatriculaServidor = ParamSist.paramEquals(CodedValues.TPC_SERVIDOR_POSSUI_MATRICULA, CodedValues.TPC_NAO, responsavel);
boolean omiteCpfServidor = ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel);
boolean filtroVinculo = ParamSist.paramEquals(CodedValues.TPC_FILTRO_VINCULO_CONSULTA_MARGEM, CodedValues.TPC_SIM, responsavel);
List<TransferObject> lstServidor = (List<TransferObject>) request.getAttribute("lstServidor");


String qs = SynchronizerToken.updateTokenInURL(request.getAttribute("queryString") != null ? "?" + request.getAttribute("queryString").toString() : "", request);
String linkAcao = request.getAttribute("acaoFormulario") + (request.getAttribute("acaoFormulario").toString().indexOf('?') >= 0 ? "&" : "?") + "acao=" + request.getAttribute("proximaAcao");
boolean verificaAutorizacaoSemSenha = !TextHelper.isNull(session.getAttribute("valida_autorizacao_novamente"));
boolean senhaObrigatoria = TextHelper.isNull(request.getAttribute("exibirCampoInfBancaria"));
%>
<c:set var="title">
    <%= TextHelper.forHtml(request.getAttribute("tituloPagina")) %>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#<%= TextHelper.forHtml(request.getAttribute("imageHeader")) != null ? TextHelper.forHtml(request.getAttribute("imageHeader")) : "i-manutencao"%>"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm">
      <div class="card">
        <div class="card-header hasIcon">
          <span class="card-header-icon"><svg width="26">
          <use xlink:href="../img/sprite.svg#i-servidor"></use></svg></span>
          <h2 class="card-header-title"><hl:message key="rotulo.selecionar.servidor.titulo"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.servidor.nome"/></th>
                <% if (!omiteCpfServidor) { %>
                <th scope="col"><hl:message key="rotulo.servidor.cpf"/></th>
                <% } %>
                <th scope="col"><hl:message key="rotulo.servidor.status"/></th>
                <% if (!omiteMatriculaServidor) { %>
                <th scope="col"><hl:message key="rotulo.servidor.matricula"/></th>
                <% } %>
                <th scope="col"><hl:message key="rotulo.orgao.singular"/></th>
                <th scope="col"><hl:message key="rotulo.estabelecimento.abreviado"/></th>
                <%if(filtroVinculo){ %>
                  <th scope="col" class="d-flex justify-content-center"><hl:message key="rotulo.servidor.vinculo"/></th>
                <%} %>
                <th scope="col"><hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>
            <%
            TransferObject servidor = null;

            String serNome, serCpf, vrsDescricao, rseMatricula, orgNome, serCodigo, rseCodigo, orgIdentificador, estIdentificador, serStatus;
            Iterator<TransferObject> it = lstServidor.iterator();
            while (it.hasNext()) {
              servidor = it.next();
              servidor = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject)servidor, null, responsavel);
              
              serNome  = (String)servidor.getAttribute(Columns.SER_NOME);  
              serCpf   = (String)servidor.getAttribute(Columns.SER_CPF);
              rseMatricula = (String)servidor.getAttribute(Columns.RSE_MATRICULA);
              orgNome   = (String)servidor.getAttribute(Columns.ORG_NOME);
              serCodigo = (String)servidor.getAttribute(Columns.SER_CODIGO);
              rseCodigo = (String)servidor.getAttribute(Columns.RSE_CODIGO);
              orgIdentificador = (String)servidor.getAttribute(Columns.ORG_IDENTIFICADOR);
              estIdentificador = (String)servidor.getAttribute(Columns.EST_IDENTIFICADOR);
              serStatus = (String)servidor.getAttribute(Columns.SRS_DESCRICAO);
              vrsDescricao = (String) servidor.getAttribute(Columns.VRS_DESCRICAO);
            %>
              <tr>
                <td><%=TextHelper.forHtmlContent(serNome)%></td>
                <% if (!omiteCpfServidor) { %>
                <td><%=TextHelper.forHtmlContent(serCpf)%></td>
                <% } %>
                <td><%=TextHelper.forHtmlContent(serStatus)%></td>
                <% if (!omiteMatriculaServidor) { %>
                <td><%=TextHelper.forHtmlContent(rseMatricula)%></td>
                <% } %>
                <td><%=TextHelper.forHtmlContent(orgNome + " - " + orgIdentificador)%></td>
                <td><%=TextHelper.forHtmlContent(estIdentificador)%></td>
                <%if(filtroVinculo){ %>
                  <td class="d-flex justify-content-center"><%=servidor.getAttribute(Columns.VRS_DESCRICAO) != null ? TextHelper.forHtmlContent(vrsDescricao) : " - "%></td>
                <%}%>
                <%if (verificaAutorizacaoSemSenha){ %>
	                <td><a href="#no-back" onClick="validaAutorizacaoSemSenha('<%=TextHelper.forJavaScript(rseCodigo)%>');" id="selecionaServidor" aria-label="<hl:message key="mensagem.selecionar.servidor.clique.aqui"/>"><hl:message key="rotulo.botao.selecionar"/></a></td>
                <%} else {%>
	                <td><a href="#no-back" onClick="doIt('s', '<%=TextHelper.forJavaScript(rseCodigo)%>', true);" id="selecionaServidor" aria-label="<hl:message key="mensagem.selecionar.servidor.clique.aqui"/>"><hl:message key="rotulo.botao.selecionar"/></a></td>
                <%} %>
              </tr>
            <%
            }
            %>     
            </tbody>
            <tfoot>
              <tr>
                <td colspan="7"><hl:message key="rotulo.paginacao.subtitulo.servidor"/> - <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td>
              </tr>
            </tfoot>
          </table>
        </div>
        <div class="card-footer">
          <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
        </div>
      </div>
      <div class="btn-action">
         <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
      </div>
    </div>
  </div>
  
  <%if (verificaAutorizacaoSemSenha){ %>
	    <%--Modal Senha Servidor --%>
	    <form name="form1" action="" method="post" >
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
			        
			        <hl:senhaServidorv4 senhaObrigatoria="<%= String.valueOf(senhaObrigatoria)%>" 
			                                   senhaParaAutorizacaoReserva="false"
			                                   nomeCampoSenhaCriptografada="serAutorizacao"
			                                   nf="btnPesquisar"
			                                   classe="form-control"
			                                   separador2pontos="false" 
			                                   comTagDD="false"/>
			                                   
                   <% if (request.getAttribute("exibirCampoInfBancaria") != null) { %>
		            <div class="row">
		              <div class="form-group-pass-info-bank">
		                <b><span id="informacoesBancarias"><hl:message key="rotulo.servidor.informacoesbancarias"/></span></b>
		                <div class="row" role="group" aria-labelledby="informacoesBancarias">
		                  <div class="form-group col-sm-12 col-md-1">
		                    <div class="float-left align-middle mt-4 form-control-label">
		                      <span><hl:message key="rotulo.servidor.informacoesbancarias.banco.abreviado"/></span>
		                    </div>
		                  </div>
		                  <div class="form-group col-sm-12 col-md-3">
		                    <hl:htmlinput name="numBanco"
		                                  type="text"
		                                  classe="form-control w-100"
		                                  di="numBanco"
		                                  size="3"
		                                  mask="#D3"
		                                  placeHolder="000"
		                                  value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "numBanco"))%>" />
		                  </div>
		                  <div class="form-group col-sm-12 col-md-1">
		                    <div class="float-left align-middle mt-4 form-control-label">
		                      <span><hl:message key="rotulo.servidor.informacoesbancarias.agencia.abreviado"/></span>
		                    </div>
		                  </div>
		                  <div class="form-group col-sm-12 col-md-3">
		                    <hl:htmlinput name="numAgencia"
		                                  type="text"
		                                  classe="form-control w-100"
		                                  di="numAgencia"
		                                  size="8"
		                                  mask="#*30"
		                                  placeHolder="00000"
		                                  value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "numAgencia"))%>"/>
		                  </div>
		                  <div class="form-group col-sm-12 col-md-1">
		                    <div class="float-left align-middle mt-4 form-control-label">
		                      <span><hl:message key="rotulo.servidor.informacoesbancarias.conta.abreviado"/></span>
		                    </div>
		                  </div>
		                  <div class="form-group col-sm-12 col-md-3">
		                    <hl:htmlinput name="numConta"
		                                  type="text"
		                                  classe="form-control w-100"
		                                  di="numConta"
		                                  size="12"
		                                  mask="#*40"
		                                  placeHolder="00000000"
		                                  value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "numConta"))%>"/>
		                  </div>
		                </div>
		              </div>
		            </div>                
		          <% } %>
			      </div>
			      <div class="modal-footer pt-0">
			        <div class="btn-action mt-2 mb-0">
			          <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#" alt="<hl:message key="rotulo.botao.cancelar"/>" title="<hl:message key="rotulo.botao.cancelar"/>">
			            <hl:message key="rotulo.botao.cancelar" />
			          </a>
			          <a class="btn btn-primary" id="btnConfirmarSenha" href="#no-back"><hl:message key="rotulo.botao.confirmar"/></a>
			        </div>
			      </div>
			    </div>
			  </div>
			</div>
		</form>	
    <%} %>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
function doIt(opt, rse, verificaAutorizacao) {
 var qs = '<%=TextHelper.forJavaScriptBlock(qs)%>&RSE_CODIGO=' + rse;
 var msg = '', j;
 if (opt == 's') {
   j = '<%= TextHelper.forJavaScriptBlock(linkAcao) %>';
 } else {
   return false;
 }
 
<%if (verificaAutorizacaoSemSenha){ %>
	if ('validarOtp' == '<%=request.getAttribute("proximaAcao")%>' && verificaAutorizacao) {
		 qs += '&_skip_history_=true';
	}
<% } else {%>
	 if ('validarOtp' == '<%=request.getAttribute("proximaAcao")%>') {
		 qs += '&_skip_history_=true';
	 }
<% } %>

 if (j.indexOf("?") >= 0) {
   qs = qs.replace("?", "&");
 }

 if (verificaAutorizacao && (msg == "" || confirm(msg))) {
   postData(j + qs);
 } else {
     return true;
 }
}

<%if (verificaAutorizacaoSemSenha){ %>

function validaAutorizacaoSemSenha(registroSer) {
    var modalSenhaSer = new bootstrap.Modal(document.getElementById('modalSenhaSer'), {
        keyboard: false,
        backdrop: false
    });

    let url
    <% if (CodedValues.FUN_CONS_MARGEM.equals(responsavel.getFunCodigo())) { %>
    url = '../v3/consultarMargem?acao=validarAutorizacaoSemSenha'
    <% } else { %>
    url = '../v3/reservarMargem?acao=validarAutorizacaoSemSenha';
    <% } %>
    const form1 = document.forms["form1"];
    const inputHidden = document.createElement("input");
    inputHidden.type = "hidden";
    inputHidden.name = "RSE_CODIGO";
    inputHidden.value = registroSer;
    form1.appendChild(inputHidden);

    const queryString = '<%=TextHelper.forJavaScriptBlock(qs)%>';
    const parametrosQueryString = queryString.replace("?", "&");
    const parametrosValidacao = '&_skip_history_=true&RSE_CODIGO=' + registroSer;
    url = url + parametrosQueryString + parametrosValidacao;
    $.ajax({
        url: url,
        method: 'POST',
        dataType: 'json',
        success: function (data) {
            if (data.situacao === 'S') {
                if ('validarOtp' == '<%=request.getAttribute("proximaAcao")%>') {
                    validaCampoObrigatorio(registroSer, false, false);
                } else {
                    modalSenhaSer.show();
                    const btn = document.getElementById("btnConfirmarSenha");
                    btn.setAttribute("onClick", "validaCampoObrigatorio('" + registroSer + "', true, true)");
                    return true;
                }
            } else if (data.situacao === 'A') {
                if ('validarOtp' == '<%=request.getAttribute("proximaAcao")%>') {
                    validaCampoObrigatorio(registroSer, false, false);
                } else {
                    var msgAlertFim = document.getElementById("msgAlertFim");
                    msgAlertFim.style.display = "block"

                    modalSenhaSer.show();

                    const btn = document.getElementById("btnConfirmarSenha");
                    btn.setAttribute("onClick", "validaCampoObrigatorio('" + registroSer + "', true, true)");
                    return true;
                }
            } else {
                validaCampoObrigatorio(registroSer, false, true);
            }
        },
        error: function (error) {
            console.log('Erro:', error);
            const btn = document.getElementById("btnConfirmarSenha");
            btn.setAttribute("onClick", "validaCampoObrigatorio('" + registroSer + "', true)");

            modalSenhaSer.show();
            return true;
        }
    });
}

	function validaCampoObrigatorio(rse, validaPw, validarOtp){
		let form;
		exigeInfoBancaria = <%= !senhaObrigatoria %>
		if (validaPw) {
			form = document.forms["form1"];
		} else {
			  form = document.createElement('form');
		      form.setAttribute('id', 'form1');
		      form.setAttribute('method', 'POST');

		      document.body.appendChild(form);
		}
		if (validaPw && form.senha != null && form.senha.value == '' && !exigeInfoBancaria) {
		    alert("<hl:message key="mensagem.informe.ser.senha"/>");
		    form.senha.focus();
		    return false;
		} else if (exigeInfoBancaria && form.numBanco != null && form.numBanco.value == '') {
		    alert("<hl:message key="mensagem.informacaoBancariaObrigatoria"/>");
		    form.numBanco.focus();
		    return false;
	    } else if (exigeInfoBancaria && form.numAgencia != null && form.numAgencia.value == '') {
		    alert("<hl:message key="mensagem.informacaoBancariaObrigatoria"/>");
		    form.numAgencia.focus();
		    return false;
	    } else if (exigeInfoBancaria && form.numConta != null && form.numConta.value == '') {
		    alert("<hl:message key="mensagem.informacaoBancariaObrigatoria"/>");
		    form.numConta.focus();
		    return false;
		} else {
			if (validaPw && form.senha != null && form.senha.value != '') {
			    CriptografaSenha(form.senha, form.serAutorizacao, false);
			}
			
			let formAction = '<%= TextHelper.forJavaScriptBlock(linkAcao) %>';
            if ('validarDigital' == '<%=request.getAttribute("proximaAcao")%>') {
                formAction = formAction.replace("validarDigital", "pesquisarConsignacao");
            } else if (validaPw && 'validarOtp' == '<%=request.getAttribute("proximaAcao")%>') {
                formAction = formAction.replace("validarOtp", "pesquisarConsignacao");
            } else if(validarOtp) {
                formAction = formAction.replace("validarOtp", "consultar")
            }
			form.action = formAction;
			const queryString = '<%=TextHelper.forJavaScriptBlock(qs)%>&RSE_CODIGO=' + rse;
			const parametrosQueryString = queryString.replace("?","");
			
            const params = new URLSearchParams(parametrosQueryString);
            
            params.forEach((valor, chave) => {
                const inputHidden = document.createElement("input");
                inputHidden.type = "hidden";
                inputHidden.name = chave;
                inputHidden.value = valor;
                form.appendChild(inputHidden);
            });
            
			form.submit();
		  }
	}
<%} %>   
</script>
</c:set>
  <t:page_v4>
      <jsp:attribute name="header">${title}</jsp:attribute>
      <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
      <jsp:attribute name="javascript">${javascript}</jsp:attribute>
      <jsp:body>${bodyContent}</jsp:body>
  </t:page_v4>