<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean omiteMatriculaServidor = (boolean) request.getAttribute("omiteMatriculaServidor");
boolean omiteCpfServidor = (boolean) request.getAttribute("omiteCpfServidor");
List lstServidor = (List) request.getAttribute("lstServidor");
String offset = request.getParameter("offset");
Date dataAtual = DateHelper.getSystemDatetime();
%>

<c:set var="title">
  <c:choose>
   <c:when test="${responsavel.isCsa()}">
    <hl:message key="rotulo.listar.servidor.pendente.titulo"/>
   </c:when>   
   <c:otherwise>
    <hl:message key="rotulo.validar.servidor.titulo"/>
   </c:otherwise>
  </c:choose>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="bodyContent">
<form onsubmit="return vfRseMatricula(true);" action="../v3/validarServidor" method="post" name="form1">
  <div class="card">
    <div class="card-header hasIcon">
      <span class="card-header-icon">
        <svg width="26"><use xlink:href="../img/sprite.svg#i-servidor"></use></svg></span>
        <h2 class="card-header-title"><hl:message key="rotulo.validar.servidor.listagem"/></h2>
    </div>
    <div class="card-body table-responsive">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
          <%if (lstServidor != null) { %>
          	<th scope="col" width="10%" style="display: none;">
              <div class="form-check"><hl:message key="rotulo.acoes.aprovar"/><br>
                <input type="checkbox" class="form-check-input ml-0" id="checkAllAprovar" name="checkAll_checkAprovar" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.validar.servidor.selecione.todos", responsavel) %>" title="" data-bs-toggle="tooltip" data-original-title="<%=ApplicationResourcesHelper.getMessage("mensagem.validar.servidor.selecione.todos", responsavel) %>">
              </div>                  
            </th>
            
            <th scope="col" width="10%" style="display: none;">
              <div class="form-check"><hl:message key="rotulo.acoes.rejeitar"/><br>              
                <input type="checkbox" class="form-check-input ml-0" id="checkAllRejeitar" name="checkAll_checkReprovar" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.rejeitar.servidor.selecione.todos", responsavel) %>" title="" data-bs-toggle="tooltip" data-original-title="<%=ApplicationResourcesHelper.getMessage("mensagem.rejeitar.servidor.selecione.todos", responsavel) %>">
              </div>                  
            </th>
          <%} %>
         	<show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.VALIDAR_SERVIDOR_NOME)%>">       
              <th scope="col"><hl:message key="rotulo.servidor.nome"/></th>
            </show:showfield>
            <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.VALIDAR_SERVIDOR_CPF)%>">        
              <th scope="col"><hl:message key="rotulo.servidor.cpf"/></th>
            </show:showfield>
            <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.VALIDAR_SERVIDOR_DATA_NASC)%>">
              <th scope="col"><hl:message key="rotulo.servidor.dataNasc"/></th>
            </show:showfield>
            <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.VALIDAR_SERVIDOR_ESTABELECIMENTO)%>">
              <th scope="col"><hl:message key="rotulo.estabelecimento.singular"/></th>
            </show:showfield>
             <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.VALIDAR_SERVIDOR_ORGAO)%>">
              <th scope="col"><hl:message key="rotulo.orgao.singular"/></th>
            </show:showfield>
            <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.VALIDAR_SERVIDOR_MATRICULA)%>">
              <%
                  if (!omiteMatriculaServidor) {
              %>
                <th scope="col"><hl:message key="rotulo.servidor.matricula"/></th>
              <%
                  }
              %>
            </show:showfield>
            <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.VALIDAR_SERVIDOR_DATA_INCLUSAO)%>">
              <th scope="col"><hl:message key="rotulo.servidor.data.inclusao"/></th>
            </show:showfield>
            <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.VALIDAR_SERVIDOR_SALARIO)%>">
              <th scope="col"><hl:message key="rotulo.servidor.salario"/></th>
            </show:showfield>
            <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.VALIDAR_SERVIDOR_PROVENTOS)%>">
              <th scope="col"><hl:message key="rotulo.servidor.proventos"/></th>
            </show:showfield>
            <c:if test="${!responsavel.isCsa()}">
               <th scope="col"><hl:message key="rotulo.acoes"/></th>
            </c:if>
          </tr>
        </thead>
        <tbody>
          <%=JspHelper.msgRstVazio(lstServidor == null || lstServidor.size() == 0, 9, responsavel)%>        
          <%
                      if (lstServidor != null) {
                      Iterator it = lstServidor.iterator();
                      while (it.hasNext()) {
                          TransferObject serTO = (TransferObject) it.next();
                          String rseCodigo = (String) serTO.getAttribute(Columns.RSE_CODIGO);
                          String salario = (!TextHelper.isNull(serTO.getAttribute(Columns.RSE_SALARIO)) ? NumberHelper.reformat(serTO.getAttribute(Columns.RSE_SALARIO).toString(), "en", NumberHelper.getLang()) : "");
                          String proventos = (!TextHelper.isNull(serTO.getAttribute(Columns.RSE_PROVENTOS)) ? NumberHelper.reformat(serTO.getAttribute(Columns.RSE_PROVENTOS).toString(), "en", NumberHelper.getLang()) : "");
                  %>
              <tr class="selecionarLinha">
               <td scope="col" width="10%" class="ocultarColunaDupla" style="display: none;">
                  <div class="form-check">              
                    <input type="checkbox" class="form-check-input ml-0" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>" name="checkAprovar" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.acoes.aprovar", responsavel)%>" title="" data-bs-toggle="tooltip" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.acoes.aprovar", responsavel)%>">
                  </div>                  
                </td>
              	<td scope="col" width="10%" class="ocultarColunaDupla" style="display: none;">
                  <div class="form-check">              
                    <input type="checkbox" class="form-check-input ml-0" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>" name="checkRejeitar" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.acoes.rejeitar", responsavel)%>" title="" data-bs-toggle="tooltip" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.acoes.rejeitar", responsavel)%>">
                  </div>                  
                </td>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.VALIDAR_SERVIDOR_NOME)%>">
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(serTO.getAttribute(Columns.SER_NOME))%></td>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.VALIDAR_SERVIDOR_CPF)%>">
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(serTO.getAttribute(Columns.SER_CPF))%></td>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.VALIDAR_SERVIDOR_DATA_NASC)%>">
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(serTO.getAttribute(Columns.SER_DATA_NASC) != null ? DateHelper.format((Date) serTO.getAttribute(Columns.SER_DATA_NASC), LocaleHelper.getDatePattern()) : "")%></td>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.VALIDAR_SERVIDOR_ESTABELECIMENTO)%>">
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(serTO.getAttribute(Columns.EST_NOME))%></td>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.VALIDAR_SERVIDOR_ORGAO)%>">
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(serTO.getAttribute(Columns.ORG_NOME))%></td>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.VALIDAR_SERVIDOR_MATRICULA)%>">
                <%
                    if (!omiteMatriculaServidor) {
                %>
                  <td class="selecionarColuna"><%=TextHelper.forHtmlContent(serTO.getAttribute(Columns.RSE_MATRICULA))%></td>
                <%
                    }
                %>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.VALIDAR_SERVIDOR_DATA_INCLUSAO)%>">
                  <td class="selecionarColuna"><%=!TextHelper.isNull(serTO.getAttribute("DATA_INCLUSAO")) ? TextHelper.forHtmlContent(DateHelper.format((Date) serTO.getAttribute("DATA_INCLUSAO"), LocaleHelper.getDatePattern())) : ""%></td>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.VALIDAR_SERVIDOR_SALARIO)%>">
                  <td class="selecionarColuna"><hl:message key="rotulo.moeda"/>&nbsp;<%=TextHelper.forHtmlContent(salario)%></td>
                </show:showfield>
                <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.VALIDAR_SERVIDOR_PROVENTOS)%>">
                  <td class="selecionarColuna"><hl:message key="rotulo.moeda"/>&nbsp;<%=TextHelper.forHtmlContent(proventos)%></td>
                </show:showfield>
                <c:if test="${!responsavel.isCsa()}">
                 <td>
                   <div class="actions">
                     <div class="dropdown">
                       <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                         <div class="form-inline">
                           <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.botao.opcoes"/>" aria-label="<hl:message key="rotulo.botao.opcoes"/>"><svg>
                               <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-engrenagem"></use></svg>
                           </span> <hl:message key="rotulo.botao.opcoes"/>
                         </div>
                       </a>
                       <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                         <a class="dropdown-item" href="#no-back" aria-label="<hl:message key="rotulo.acoes.aprovar"/> <%=TextHelper.forHtmlContent(serTO.getAttribute(Columns.SER_NOME))%>" name="selecionaAcaoAprovar"><hl:message key="rotulo.acoes.aprovar"/></a> 
                         <a class="dropdown-item" href="#no-back" aria-label="<hl:message key="rotulo.acoes.rejeitar"/> <%=TextHelper.forHtmlContent(serTO.getAttribute(Columns.SER_NOME))%>" name="selecionaAcaoRejeitar"><hl:message key="rotulo.acoes.rejeitar"/></a>
                         <a class="dropdown-item" href="#no-back" aria-label="<hl:message key="rotulo.acoes.editar"/> <%=TextHelper.forHtmlContent(serTO.getAttribute(Columns.SER_NOME))%>" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/validarServidor?acao=editar&rseCodigo=" + rseCodigo + "&offset=" + offset, request))%>'); return false;"><hl:message key="rotulo.acoes.editar"/></a>
                       </div>
                     </div>
                   </div>
                 </td>
                </c:if>
              </tr>
          <%
              }
                    }
          %>  
        </tbody>
        <tfoot>
          <tr><td colspan="6"><hl:message key="rotulo.validar.servidor.listagem.footer" arg0="${_paginacaoSubTitulo}" /></td></tr>
        </tfoot>
      </table>
    </div>
    <div class="card-footer">
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
    </div> 
  </div>
  <div class="btn-action">        
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL((paramSession.getLastHistory()), request))%>');"><hl:message key="rotulo.botao.voltar"/></a>
    <a class="btn btn-primary" HREF="#no-back" onClick="validaCheckBox();">
	    <svg width="17">
	      <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar"></use>
      </svg>
      <hl:message key="rotulo.botao.confirmar"/>
    </a>
  </div>

 <div class="card">
  <div class="card-header hasIcon">
      <span class="card-header-icon">
        <svg width="26">
            <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-consultar"></use>
        </svg>
      </span>
      <h2 class="card-header-title"><hl:message key="rotulo.validar.servidor.pesquisa.titulo"/></h2>
  </div>
  <div class="card-body">
    <form onsubmit="return vfRseMatricula(true);" action="../v3/validarServidor" method="post" name="form1">
       <input type="hidden" name="acao" value="iniciar">    
       <%=SynchronizerToken.generateHtmlToken(request)%>    
       <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.VALIDAR_SERVIDOR_FILTRO_NOME)%>" >
         <div class="row">
            <div class="form-group col-sm-6">
              <label for="serNome"><hl:message key="rotulo.servidor.nome"/></label>
              <hl:htmlinput name="serNome"
                            di="serNome"
                            type="text"
                            classe="form-control"
                            size="20"      
                            value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "serNome"))%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.nome", responsavel)%>"
              />
            </div>
         </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.VALIDAR_SERVIDOR_FILTRO_SOBRENOME)%>" >  
         <div class="row"> 
            <div class="form-group col-sm-6">
              <label for="serSobrenome"><hl:message key="rotulo.servidor.sobrenome"/></label>
              <hl:htmlinput name="serSobrenome"
                            di="serSobrenome"
                            type="text"
                            classe="form-control"
                            size="20"
                            value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "serSobrenome"))%>"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.sobrenome", responsavel)%>"
              />
            </div>
         </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.VALIDAR_SERVIDOR_FILTRO_CPF)%>" >
         <div class="row"> 
            <div class="form-group col-sm-6">
              <hl:campoCPFv4 configKey="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.VALIDAR_SERVIDOR_FILTRO_CPF)%>"
                       description="<%=ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel)%>" 
                       placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel)%>"/>
            </div>
         </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.VALIDAR_SERVIDOR_FILTRO_DATA_NASC)%>" >    
         <div class="row"> 
            <div class="form-group col-sm-6">
              <label for="serDataNasc"><hl:message key="rotulo.servidor.dataNasc"/></label>
              <hl:htmlinput name="serDataNasc" di="serDataNasc"
                type="text" classe="form-control" size="10"
                mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "serDataNasc"))%>" 
                placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"/>
            </div>
         </div>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.VALIDAR_SERVIDOR_FILTRO_ESTABELECIMENTO)%>" >
         <%
             List lstEstabelecimentos = (List) request.getAttribute("lstEstabelecimento");
         %>
         <%
             if (lstEstabelecimentos != null && !lstEstabelecimentos.isEmpty()) {
         %>
         <div class="row">
           <div class="form-group col-sm-6">
              <label for="EST_CODIGO"><hl:message key="rotulo.estabelecimento.singular"/></label>
              <%=JspHelper.geraCombo(lstEstabelecimentos, "EST_CODIGO", Columns.EST_CODIGO, Columns.EST_NOME + ";" + Columns.EST_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, JspHelper.verificaVarQryStr(request, "EST_CODIGO"), false)%>
           </div>
         </div>
         <%
             }
         %>
       </show:showfield>
       <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.VALIDAR_SERVIDOR_FILTRO_ORGAO)%>" >
         <%
             List lstOrgaos = (List) request.getAttribute("lstOrgaos");
         %>
         <%
             if (lstOrgaos != null && !lstOrgaos.isEmpty()) {
         %>             
         <div class="row"> 
            <div class="form-group col-sm-6">
              <label for="ORG_CODIGO"><hl:message key="rotulo.orgao.singular"/></label>
              <%=JspHelper.geraCombo(lstOrgaos, "ORG_CODIGO", Columns.ORG_CODIGO, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, JspHelper.verificaVarQryStr(request, "ORG_CODIGO"), null, false, "form-control")%>
            </div>
         </div>
         <%
             }
         %>
       </show:showfield>  
       <show:showfield key="<%=TextHelper.forHtmlAttribute( FieldKeysConstants.VALIDAR_SERVIDOR_FILTRO_MATRICULA)%>" >
                
            <%
                                request.setAttribute("configKey", FieldKeysConstants.VALIDAR_SERVIDOR_FILTRO_MATRICULA);
                            %>      
         <div class="row"> 
            <div class="form-group col-sm-6">
              <hl:campoMatriculav4 placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula", responsavel)%>" />
            </div>
         </div>
       </show:showfield>
    </form>
  </div>
 </div>
 <div class="btn-action">
   <a class="btn btn-outline-danger" href="javascript:void(0);" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.voltar"/></a>
   <a class="btn btn-primary" href="javascript:void(0);"  onClick="if(validaSubmit()){document.forms[0].submit();}">
      <svg width="20">
        <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-consultar"></use>
      </svg><hl:message key="rotulo.botao.pesquisar"/>
   </a>
 </div>
 </form>
</c:set>
<c:set var="javascript">
<hl:campoMatriculav4 placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula", responsavel)%>" scriptOnly="true" />
<script type="text/JavaScript">
//#DESENV-7169 Se existe parametro, deve-se voltar para a tela de listagem listando todos os registros 
function verificarParametroExistente () {
	 
	 var possuiParametroPesquisa = <%=request.getAttribute("possuiParametroPesquisa")%>;
	 
	 if (possuiParametroPesquisa) {
         document.getElementById("serNome").value = null;
     	 document.getElementById("serSobrenome").value = null;
     	 document.getElementById("SER_CPF").value = null;
     	 document.getElementById("serDataNasc").value = null;
     	 document.getElementById("EST_CODIGO").value = null;
     	 document.getElementById("ORG_CODIGO").value = null;
     	 document.getElementById("RSE_MATRICULA").value = null;   	 
   	     document.forms[0].submit();
   	 
	 } else {
		 postData('../v3/carregarPrincipal'); 
		 return false;
	 }
}

function escolheRse(chk) {
    if (chk.checked) {
          uncheckAll(f0, chk.value);
          chk.checked = true;
   } else {
      var campoCheckAll = document.getElementById("checkAll_" + chk.name);
      if (campoCheckAll) {
         campoCheckAll.checked = false;
     }
  }
}

function escolheAcaoRse(campo1, campo2, chkValor) {
	var acaoCampo1 = document.getElementsByName(campo1);
	var marcado = false;
    for (var i=0; i < acaoCampo1.length; i++) {
    	if (acaoCampo1[i].value == chkValor) {
    		marcado = acaoCampo1[i].checked;
            if (marcado) {
            	// desmarcar o campo1
              	acaoCampo1[i].checked = false; 
            } else {
                // marcar o campo 1
                acaoCampo1[i].checked = true;
            	// desmarcar o campo2
            	var acaoCampo2 = document.getElementsByName(campo2);
            	for (var j=0; j < acaoCampo2.length; j++) {
            		if (acaoCampo2[j].value == chkValor) {
            			acaoCampo2[j].checked = false;
            		}        	
            	}
            }
        }    	
    }
}
    
function checkUnCheckFieldAll(campo1, campo2, marcado) {
	var campoCheckAll = document.getElementsByName(campo1);
	var campoUnCheckAll = document.getElementsByName(campo2);
    for (var i=0; i < campoCheckAll.length; i++) {
        campoCheckAll[i].checked = marcado;
    }
    if (marcado) {
    	for (var i=0; i < campoUnCheckAll.length; i++) {
            campoUnCheckAll[i].checked = false;
        }
    		var campoUnCheckAll = document.getElementById("checkAll_" + campo2);
        	if (campoUnCheckAll) {
        	campoUnCheckAll.checked = false;
       }
       
    }
}

function validaSubmit(){  
  if(typeof vfRseMatricula === 'function')
    return vfRseMatricula(true);
    
  else
    return true; 
}

function uncheckAll(form, chkValor) {
  for (i=0; i < form.elements.length; i++) {
    var e = form.elements[i];
    if (((e.type == 'check') || (e.type == 'checkbox')) && 
    		(e.value == chkValor || chkValor == null)) {
      e.checked = false;
    }
  }
}

function validaCheckBox() {
	var inputs = document.querySelectorAll("input[type='checkbox']");
	 for(var i = 0; i < inputs.length; i++) { 
		if (inputs[i].checked) {
			aprovarRejeitarCadServidor();
			return;
		}
	 }
	 alert('<hl:message key="mensagem.selecione.servidor"/>');
}


function aprovarRejeitarCadServidor() {
    var inputs = document.querySelectorAll("input[type='checkbox']");
    var lstCheckAprovar = [];
    var lstCheckRejeitar = [];
    for(var i = 0; i < inputs.length; i++) {
    	if (inputs[i].checked && inputs[i].name == 'checkAprovar') {
    	    lstCheckAprovar.push(inputs[i].value);
        } else if (inputs[i].checked && inputs[i].name == 'checkRejeitar') {
        	lstCheckRejeitar.push(inputs[i].value);
        }
    }
    postData('../v3/validarServidor?acao=aprovarRejeitarCadServidor&checkAprovar=' + lstCheckAprovar + '&checkRejeitar=' + lstCheckRejeitar + '&<%out.print(SynchronizerToken.generateToken4URL(request));%>');
}

f0 = document.forms[0]; 


/* **Click na linha
 * 1- Mostrar as colunas de Deferir e Indeferir, quando se clica na linha.
*/

var clicklinha = false;

$(".selecionarColuna").click(function() {
	// 1- Seleciona a linha e mostrar as colunas dos checks
	
	var checked = $("table tbody tr input[type=checkbox]:checked").length;

	if (checked == 0) {

		if (clicklinha) {
			console.log(clicklinha);
			$("table th:nth-child(-n+2)").hide();
			$(".ocultarColunaDupla").hide();
		} else {
			$("table th:nth-child(-n+2)").show();
			$(".ocultarColunaDupla").show();
		}

		clicklinha = !clicklinha
		
		
	}
});

/* **Click no opções, Deferir
 * 1- Exibir colunas dos checkBoxes Deferir e Indeferir, independente de qual opção tenha sido escolhida
 * 2- Colorir a linha da opção desejada, marcar o checkBox da opção, e desmarca o da opção contrária
 * 3- Verificar se a coluna do checkBox correspondente está toda selecionada, para que se marque o checkAll do mesmo, e desmarque o outro. 
*/
$("[name='selecionaAcaoAprovar']").click(function() {
	// 1- Exibe as colunas dos checksboxes
	$("table th:nth-child(-n+2)").show();
	$(".ocultarColunaDupla").show();
	
	// 2- Colore a linha e marca o checkbox do deferir, e caso o check do indeferir esteja marcado será desmarcado
	$(this).parentsUntil("tbody",".selecionarLinha").addClass("table-checked");
	$(this).parentsUntil("tbody").find('input[name="checkRejeitar"]').prop("checked",false);
	$(this).parentsUntil("tbody").find('input[name="checkAprovar"]').prop("checked", true);
	$("#checkAllRejeitar").prop('checked', false);
	
	// 3- Verifica se todos os checkboxes do Deferir estão marcados, marca o checkAll do Deferir, e desmarca o do Indeferir
	var qtdCheckboxCheked = $("[name='checkAprovar']").not($("#checkAllAprovar")).filter(':checked').length;
	var qtdCheckbox = $("[name='checkAprovar']").not($("#checkAllAprovar")).length;
	if (qtdCheckbox == qtdCheckboxCheked) {
		$("#checkAllAprovar").prop('checked', true);
	} else if (qtdCheckbox != qtdCheckboxCheked) {
		$("#checkAllAprovar").prop('checked', false);
	}
 });

/* **Click no opções, Indeferir
 * 1- Exibir colunas dos checkBoxes Deferir e Indeferir, independente de qual opção tenha sido escolhida
 * 2- Colorir a linha da opção desejada, marcar o checkBox da opção, e desmarca o da opção contrária
 * 3- Verificar se a coluna do checkBox correspondente está toda selecionada, para que se marque o checkAll do mesmo, e desmarque o outro. 
*/
$("[name='selecionaAcaoRejeitar']").click(function() {
	// 1- Exibe as colunas dos checksboxes
	$("table th:nth-child(-n+2)").show();
	$(".ocultarColunaDupla").show();
	
	// 2- Colore a linha e marca o checkbox do indeferir, e caso o check do deferir esteja marcado será desmarcado
	$(this).parentsUntil("tbody",".selecionarLinha").addClass("table-checked");
	$(this).parentsUntil("tbody").find('input[name="checkAprovar"]').prop("checked",false);
	$(this).parentsUntil("tbody").find('input[name="checkRejeitar"]').prop("checked",true);
	$("#checkAllDeferir").prop('checked', false);
	
	// 3- Verifica se todos os checkboxes do Indeferir estão marcados, marca o checkAll do Indeferir, e desmarca o do Deferir
	var qtdCheckboxCheked = $("[name='checkRejeitar']").not($("#checkAllRejeitar")).filter(':checked').length;
	var qtdCheckbox = $("[name='checkRejeitar']").not($("#checkAllRejeitar")).length;
	if (qtdCheckbox == qtdCheckboxCheked) {
		$("#checkAllRejeitar").prop('checked', true);
	} else if (qtdCheckbox != qtdCheckboxCheked) {
		$("#checkAllRejeitar").prop('checked', false);
	}
	
});

/* **Click do check Deferir
 * 1- Ao ser selecionado, colorir a linha e ou ser desselecionado tirar a cor da linha
 * 2- Ao ser selecionado, caso complete a seleção de todos na coluna, marcar a opção checkall, caso quebre essa regra desmarcar o checkall
 * 3- Ao ser desselecionado, caso seja o único checkbox da página, esconder as colunas extras
 * 4- Ao ser selecionado, caso o checkall contrário esteja marcado, ele será desmarcado
 * 
 */
$("[name='checkAprovar']").click(function() {
	//1- colore a linha
	if ($(this).is(":checked")) {
		$(this).parentsUntil("tbody", ".selecionarLinha").addClass("table-checked");
		$(this).parentsUntil("tbody").find('input[name="checkRejeitar"]').prop("checked",false);
		
		//4- desmarca o checkall contrário
		$("#checkAllRejeitar").prop('checked', false);
	} else {
		$(this).parentsUntil("tbody", ".selecionarLinha").removeClass("table-checked");
	}
	
	//2- marca ou desmarca o checkall correspondente
	var qtdCheckboxCheked = $("[name='checkAprovar']").not($("#checkAllAprovar")).filter(':checked').length;
	var qtdCheckbox = $("[name='checkAprovar']").not($("#checkAllAprovar")).length;
	if (qtdCheckbox == qtdCheckboxCheked) {
		$("#checkAllAprovar").prop('checked', true);
	} else if (qtdCheckbox != qtdCheckboxCheked) {
		$("#checkAllAprovar").prop('checked', false);
	}
	
	//3- esconde as colunas
	if ($('input[type="checkbox"]').filter(':checked').length == 0) {
		$("table th:nth-child(-n+2)").hide();
		$(".ocultarColunaDupla").hide();
		clicklinha = false;
	}
});

/* **Click do check Indeferir
 * 1- Ao ser selecionado, colorir a linha e ou ser desselecionado tirar a cor da linha
 * 2- Ao ser selecionado, caso complete a seleção de todos na coluna, marcar a opção checkall, caso quebre essa regra desmarcar o checkall
 * 3- Ao ser desselecionado, caso seja o único checkbox da página, esconder as colunas extras
 * 4- Ao ser selecionado, caso o checkall contrário esteja marcado, ele será desmarcado
 * 
 */
$("[name='checkRejeitar']").click(function() {
	//1- colore a linha
	if ($(this).is(":checked")) {
		$(this).parentsUntil("tbody", ".selecionarLinha").addClass("table-checked");
		$(this).parentsUntil("tbody").find('input[name="checkAprovar"]').prop("checked",false);
		
		//4- marca o checkAll contrário
		$("#checkAllAprovar").prop('checked', false);
	} else {
		$(this).parentsUntil("tbody", ".selecionarLinha").removeClass("table-checked");
	}
	
	//2- marca ou desmarca o checkall correpondente
	var qtdCheckboxCheked = $("[name='checkRejeitar']").not($("#checkAllRejeitar")).filter(':checked').length;
	var qtdCheckbox = $("[name='checkRejeitar']").not($("#checkAllRejeitar")).length;
	if (qtdCheckbox == qtdCheckboxCheked) {
		$("#checkAllRejeitar").prop('checked', true);
	} else if (qtdCheckbox != qtdCheckboxCheked) {
		$("#checkAllRejeitar").prop('checked', false);
	}
	
	// 3- esconde as colunas
	if ($('input[type="checkbox"]').filter(':checked').length == 0) {
		$("table th:nth-child(-n+2)").hide();
		$(".ocultarColunaDupla").hide();
		clicklinha = false;
	}
});
  
/* **CheckAll Deferir
 * 1- Colorir todas as linhas, quando o checkAll Deferir for marcado, ou quando for desmarcado descolorir
 * 2- Ocultar as colunas dos checks quando não houver nenhum deles selecionados
 * 3- Desmarcar o checkAll contrário, quando o correspondente é marcado
*/
$("#checkAllAprovar").click(function() {
	$('input[name="checkAprovar"]').prop("checked",function(i, val) {
		if (!(i < 0)) {
			// 1- Colore as linhas, quando o checkAll está marcado ou quando for desmacado
			if ($("#checkAllAprovar").is(":checked")) {
				$(this).parentsUntil("tbody",".selecionarLinha").addClass("table-checked");
			} else {
				$(this).parentsUntil("tbody",".selecionarLinha").removeClass("table-checked");
			}
			return $("#checkAllAprovar").is(":checked");
		}
	});
	
	// 2- Oculta as colunas dos checks, quando não contém nenhum checkbox marcado
	if (!$("#checkAllAprovar").is(":checked")) {
		if($('input[type="checkbox"]').filter(':checked').length == 0) {
			$("table th:nth-child(-n+2)").hide();
			$(".ocultarColunaDupla").hide();
		}
	} else {
		//3- Desmarca o checkAll contrário
		$('input[name="checkRejeitar"]').prop("checked",function(i, val) {
			return false;
		});
		$("#checkAllRejeitar").prop("checked", false);
	}
});

/* **CheckAll Indeferir
 * 1- Colorir todas as linhas, quando o checkAll Indeferir for marcado, ou quando for desmarcado descolorir
 * 2- Ocultar as colunas dos checks quando não houver nenhum deles selecionados
 * 3- Desmarcar o checkAll contrário, quando o correspondente é marcado
*/
$("#checkAllRejeitar").click(function() {
	$('input[name="checkRejeitar"]').prop("checked",function(i, val) {
		if (!(i < 0)) {
			// 1- Colore as linhas, quando o checkAll está marcado
			if ($("#checkAllRejeitar").is(":checked")) {
				$(this).parentsUntil("tbody",".selecionarLinha").addClass("table-checked");
			} else {
				$(this).parentsUntil("tbody",".selecionarLinha").removeClass("table-checked");
			}
			return $("#checkAllRejeitar").is(":checked");
		}
	});
	
	// 2- Oculta as colunas dos checks, quando não contém nenhum checkbox marcado
	if (!$("#checkAllRejeitar").is(":checked")) {
		if($('input[type="checkbox"]').filter(':checked').length == 0) {
			$("table th:nth-child(-n+2)").hide();
			$(".ocultarColunaDupla").hide();
		}
	} else {
		//3- Desmarca o checkAll contrário
		$('input[name="checkAprovar"]').prop("checked",function(i, val) {
			return false;
		});
		$("#checkAllAprovar").prop("checked", false);
	}
});

//Oculta colula com checks
function ocultarColunaDupla() {
	//Oculta as duas colunas, incluindo os cabeçalhos de Deferir e Indeferir
	$("table th:nth-child(-n+2)").hide();
	$(".ocultarColunaDupla").hide();
	clicklinha = false;
}

</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>