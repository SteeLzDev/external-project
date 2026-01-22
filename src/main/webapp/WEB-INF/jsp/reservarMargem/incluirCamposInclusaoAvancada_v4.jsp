<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.parametros.ReservarMargemParametros"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<% 
AcessoSistema _responsavel = JspHelper.getAcessoSistema(request); 

boolean desabilitaOpcoesAvancadas = (request.getAttribute("desabilitaOpcoesAvancadas") != null);
Boolean _validaMargemAvancado = (Boolean) request.getAttribute("validaMargemAvancado");
Boolean _validaTaxaAvancado = (Boolean) request.getAttribute("validaTaxaAvancado");
Boolean _validaPrazoAvancado = (Boolean) request.getAttribute("validaPrazoAvancado");
Boolean _validaDadosBancariosAvancado = (Boolean) request.getAttribute("validaDadosBancariosAvancado");
Boolean _validaSenhaServidorAvancado = (Boolean) request.getAttribute("validaSenhaServidorAvancado");
Boolean _validaBloqSerCnvCsaAvancado = (Boolean) request.getAttribute("validaBloqSerCnvCsaAvancado");
Boolean _validaDataNascAvancado = (Boolean) request.getAttribute("validaDataNascAvancado");
Boolean _validaLimiteAdeAvancado = (Boolean) request.getAttribute("validaLimiteAdeAvancado");

List<TransferObject> lstTipoJustica = (List<TransferObject>) request.getAttribute("lstTipoJustica");
%>
<div class="opcoes-avancadas">
  <a class="opcoes-avancadas-head" href="#faq1" data-bs-toggle="collapse" aria-expanded="false" aria-controls="faq1" aria-label='<hl:message key="mensagem.inclusao.avancada.clique.aqui"/>'><hl:message key="rotulo.avancada.opcoes"/></a>
  <div class="collapse" id="faq1">
    <div class="opcoes-avancadas-body">
      <div class="row">
        <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="validaMargemReserva">
          <div class="form-group my-0">
            <span id="validaMargemReserva"><hl:message key="rotulo.inclusao.avancada.validaMargem"/></span>
          </div>
          <div class="form-check form-check-inline mt-2">
            <% if (desabilitaOpcoesAvancadas) { %>
            <input class="form-check-input ml-1" type="radio" name="_validaMargem" id="_validaMargem_Sim" value="true" <%=(String)(request.getParameter("validaMargem").equalsIgnoreCase("true") ? "checked=\"checked\"" : "")%> disabled="disabled">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaMargem"/>' for="_validaMargem_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="_validaMargem" id="_validaMargem_Nao" value="false" <%=(String)(request.getParameter("validaMargem").equalsIgnoreCase("false") ? "checked=\"checked\"" : "")%> disabled="disabled">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaMargem"/>' for="_validaMargem_Nao"><hl:message key="rotulo.nao"/></label>
            <% } else { %>
            <input class="form-check-input ml-1" type="radio" name="validaMargem" id="validaMargem_Sim" value="true" checked="checked">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaMargem"/>' for="validaMargem_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaMargem" id="validaMargem_Nao" value="false">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaMargem"/>' for="validaMargem_Nao"><hl:message key="rotulo.nao"/></label>
            <% } %>
          </div>
        </div>
        <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="cetReserva">
          <div class="form-group my-0">
            <span id="cetReserva"><hl:message key="rotulo.inclusao.avancada.validaTaxaJuros"/></span>
          </div>
          <div class="form-check form-check-inline mt-2">
            <% if (desabilitaOpcoesAvancadas) { %>
            <input class="form-check-input ml-1" type="radio" name="_validaTaxa" id="_validaTaxa_Sim" value="true" <%=(String)(request.getParameter("validaTaxa").equalsIgnoreCase("true") ? "checked=\"checked\"" : "")%> disabled="disabled">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaTaxaJuros"/>' for="_validaTaxa_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="_validaTaxa" id="_validaTaxa_Nao" value="false" <%=(String)(request.getParameter("validaTaxa").equalsIgnoreCase("false") ? "checked=\"checked\"" : "")%> disabled="disabled">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaTaxaJuros"/>' for="_validaTaxa_Nao"><hl:message key="rotulo.nao"/></label>
            <% } else { %>
            <input class="form-check-input ml-1" type="radio" name="validaTaxa" id="validaTaxa_Sim" value="true" checked="checked">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaTaxaJuros"/>' for="validaTaxa_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaTaxa" id="validaTaxa_Nao" value="false">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaTaxaJuros"/>' for="validaTaxa_Nao"><hl:message key="rotulo.nao"/></label>
            <% } %>
          </div>
        </div>
        <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="prazoReserva">
          <div class="form-group my-0">
            <span id="prazoReserva"><hl:message key="rotulo.inclusao.avancada.validaPrazo"/></span>
          </div>
          <div class="form-check form-check-inline mt-2">
            <% if (desabilitaOpcoesAvancadas) { %>
            <input class="form-check-input ml-1" type="radio" name="_validaPrazo" id="_validaPrazo_Sim" value="true" <%=(String)(request.getParameter("validaPrazo").equalsIgnoreCase("true") ? "checked=\"checked\"" : "")%> disabled="disabled">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaPrazo"/>' for="_validaPrazo_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="_validaPrazo" id="_validaPrazo_Nao" value="false" <%=(String)(request.getParameter("validaPrazo").equalsIgnoreCase("false") ? "checked=\"checked\"" : "")%> disabled="disabled">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaPrazo"/>' for="_validaPrazo_Nao"><hl:message key="rotulo.nao"/></label>
            <% } else { %>
            <input class="form-check-input ml-1" type="radio" name="validaPrazo" id="validaPrazo_Sim" value="true" checked="checked">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaPrazo"/>' for="validaPrazo_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaPrazo" id="validaPrazo_Nao" value="false">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaPrazo"/>' for="validaPrazo_Nao"><hl:message key="rotulo.nao"/></label>
            <% } %>
          </div>
        </div>
        <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="dadosBancarios">
          <div class="form-group my-0">
            <span id="dadosBancarios"><hl:message key="rotulo.inclusao.avancada.validaDadosBancarios"/></span>
          </div>
          <div class="form-check form-check-inline mt-2">
            <% if (desabilitaOpcoesAvancadas) { %>
            <input class="form-check-input ml-1" type="radio" name="_validaDadosBancarios" id="_validaDadosBancarios_Sim" value="true" <%=(String)(request.getParameter("validaDadosBancarios").equalsIgnoreCase("true") ? "checked=\"checked\"" : "")%> disabled="disabled">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaDadosBancarios"/>' for="_validaDadosBancarios_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="_validaDadosBancarios" id="_validaDadosBancarios_Nao" value="false" <%=(String)(request.getParameter("validaDadosBancarios").equalsIgnoreCase("false") ? "checked=\"checked\"" : "")%> disabled="disabled">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaDadosBancarios"/>' for="_validaDadosBancarios_Nao"><hl:message key="rotulo.nao"/></label>
            <% } else { %>
            <input class="form-check-input ml-1" type="radio" name="validaDadosBancarios" id="validaDadosBancarios_Sim" value="true" checked="checked">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaDadosBancarios"/>' for="validaDadosBancarios_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaDadosBancarios" id="validaDadosBancarios_Nao" value="false">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaDadosBancarios"/>' for="validaDadosBancarios_Nao"><hl:message key="rotulo.nao"/></label>
            <% } %>
          </div>
        </div>
        <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="senhaServidor">
          <div class="form-group my-0">
            <span id="senhaServidor"><hl:message key="rotulo.inclusao.avancada.validaSenhaServidor"/></span>
          </div>
          <div class="form-check form-check-inline mt-2">
            <% if (desabilitaOpcoesAvancadas) { %>
            <input class="form-check-input ml-1" type="radio" name="_validaSenhaServidor" id="_validaSenhaServidor_Sim" value="true" <%=(String)(request.getParameter("validaSenhaServidor").equalsIgnoreCase("true") ? "checked=\"checked\"" : "")%> disabled="disabled">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaSenhaServidor"/>' for="_validaSenhaServidor_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="_validaSenhaServidor" id="_validaSenhaServidor_Nao" value="false" <%=(String)(request.getParameter("validaSenhaServidor").equalsIgnoreCase("false") ? "checked=\"checked\"" : "")%> disabled="disabled">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaSenhaServidor"/>' for="_validaSenhaServidor_Nao"><hl:message key="rotulo.nao"/></label>
            <% } else { %>
            <input class="form-check-input ml-1" type="radio" name="validaSenhaServidor" id="validaSenhaServidor_Sim" value="true" checked="checked">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaSenhaServidor"/>' for="validaSenhaServidor_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaSenhaServidor" id="validaSenhaServidor_Nao" value="false">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaSenhaServidor"/>' for="validaSenhaServidor_Nao"><hl:message key="rotulo.nao"/></label>
            <% } %>
          </div>
        </div>
        <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="bloqueioServidor">
          <div class="form-group my-0">
            <span id="bloqueioServidor"><hl:message key="rotulo.inclusao.avancada.validaBloqSerCnvCsa"/></span>
          </div>
          <div class="form-check form-check-inline mt-2">
            <% if (desabilitaOpcoesAvancadas) { %>
            <input class="form-check-input ml-1" type="radio" name="_validaBloqSerCnvCsa" id="_validaBloqSerCnvCsa_Sim" value="true" <%=(String)(request.getParameter("validaBloqSerCnvCsa").equalsIgnoreCase("true") ? "checked=\"checked\"" : "")%> disabled="disabled">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaBloqSerCnvCsa"/>' for="_validaBloqSerCnvCsa_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="_validaBloqSerCnvCsa" id="_validaBloqSerCnvCsa_Nao" value="false" <%=(String)(request.getParameter("validaBloqSerCnvCsa").equalsIgnoreCase("false") ? "checked=\"checked\"" : "")%> disabled="disabled">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaBloqSerCnvCsa"/>' for="_validaBloqSerCnvCsa_Nao"><hl:message key="rotulo.nao"/></label>
            <% } else { %>
            <input class="form-check-input ml-1" type="radio" name="validaBloqSerCnvCsa" id="validaBloqSerCnvCsa_Sim" value="true" checked="checked">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaBloqSerCnvCsa"/>' for="validaBloqSerCnvCsa_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaBloqSerCnvCsa" id="validaBloqSerCnvCsa_Nao" value="false">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaBloqSerCnvCsa"/>' for="validaBloqSerCnvCsa_Nao"><hl:message key="rotulo.nao"/></label>
            <% } %>
          </div>
        </div>
        <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="validaDataNascimento">
          <div class="form-group my-0">
            <span id="validaDataNascimento"><hl:message key="rotulo.inclusao.avancada.validaDataNascimento"/></span>
          </div>
          <div class="form-check form-check-inline mt-2">
            <% if (desabilitaOpcoesAvancadas) { %>
            <input class="form-check-input ml-1" type="radio" name="_validaDataNascimento" id="_validaDataNascimento_Sim" value="true" <%=(String)(request.getParameter("validaDataNascimento").equalsIgnoreCase("true") ? "checked=\"checked\"" : "")%> disabled="disabled">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaDataNascimento"/>' for="_validaDataNascimento_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="_validaDataNascimento" id="_validaDataNascimento_Nao" value="false" <%=(String)(request.getParameter("validaDataNascimento").equalsIgnoreCase("false") ? "checked=\"checked\"" : "")%> disabled="disabled">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaDataNascimento"/>' for="_validaDataNascimento_Nao"><hl:message key="rotulo.nao"/></label>
            <% } else { %>
            <input class="form-check-input ml-1" type="radio" name="validaDataNascimento" id="validaDataNascimento_Sim" value="true" checked="checked">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaDataNascimento"/>' for="validaDataNascimento_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaDataNascimento" id="validaDataNascimento_Nao" value="false">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaDataNascimento"/>' for="validaDataNascimento_Nao"><hl:message key="rotulo.nao"/></label>
            <% } %>
          </div>
        </div>
        <div class=" col-md-6 form-check mt-2" role="radiogroup" aria-labelledby="validaLimiteContrato">
          <div class="form-group my-0">
            <span id="validaLimiteContrato"><hl:message key="rotulo.inclusao.avancada.validaLimiteAde"/></span>
          </div>
          <div class="form-check form-check-inline mt-2">
            <% if (desabilitaOpcoesAvancadas) { %>
            <input class="form-check-input ml-1" type="radio" name="_validaLimiteAde" id="_validaLimiteAde_Sim" value="true" <%=(String)(request.getParameter("validaLimiteAde").equalsIgnoreCase("true") ? "checked=\"checked\"" : "")%> disabled="disabled">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaLimiteAde"/>' for="_validaLimiteAde_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="_validaLimiteAde" id="_validaLimiteAde_Nao" value="false" <%=(String)(request.getParameter("validaLimiteAde").equalsIgnoreCase("false") ? "checked=\"checked\"" : "")%> disabled="disabled">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaLimiteAde"/>' for="_validaLimiteAde_Nao"><hl:message key="rotulo.nao"/></label>
            <% } else { %>
            <input class="form-check-input ml-1" type="radio" name="validaLimiteAde" id="validaLimiteAde_Sim" value="true" checked="checked">
            <label class="form-check-label formatacao ml-1 pr-4 text-nowrap align-text-top" aria-label='<hl:message key="rotulo.inclusao.avancada.validaLimiteAde"/>' for="validaLimiteAde_Sim"><hl:message key="rotulo.sim"/></label>
          </div>
          <div class="form-check form-check-inline mt-2">
            <input class="form-check-input ml-1" type="radio" name="validaLimiteAde" id="validaLimiteAde_Nao" value="false">
            <label class="form-check-label formatacao ml-1" aria-label='<hl:message key="rotulo.inclusao.avancada.nao.validaLimiteAde"/>' for="validaLimiteAde_Nao"><hl:message key="rotulo.nao"/></label>
            <% } %>
          </div>
        </div>
        <div class="form-group col-sm-12 mb-2">
          <% if (desabilitaOpcoesAvancadas) { %>
            <label for="tmoDescricao"><hl:message key="rotulo.efetiva.acao.consignacao.dados.tipo.mtv.cancelamento"/></label>
            <input class="form-control" type="text" name="tmoDescricao" id="tmoDescricao" value="<%=TextHelper.forHtmlAttribute(request.getAttribute("tmoDescricao"))%>" disabled="disabled"/>
          <% } else { %>
            <label for="tmoCodigo"><hl:message key="rotulo.efetiva.acao.consignacao.dados.tipo.mtv.cancelamento"/></label>
            <hl:htmlcombo listName="lstMtvOperacao" di="tmoCodigo" name="tmoCodigo" fieldValue="<%=Columns.TMO_CODIGO%>" fieldLabel="<%=Columns.TMO_DESCRICAO%>" notSelectedLabel='<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", _responsavel)%>' autoSelect="true" classe="form-control form-select"/>
          <% } %>
        </div>
        <div class="form-group col-sm-12">
         <% if (desabilitaOpcoesAvancadas) { %>
           <label for="observacao"><hl:message key="rotulo.efetiva.acao.consignacao.dados.observacao"/></label>
           <textarea name="observacao"  id="observacao" class="form-control" rows="6" disabled="disabled"><%=TextHelper.forHtmlContent(JspHelper.verificaVarQryStr(request, "adeObs"))%></textarea>
         <% } else { %>
           <label for="adeObs"><hl:message key="rotulo.efetiva.acao.consignacao.dados.observacao"/></label>
           <textarea id="adeObs" name="adeObs" placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.obs",_responsavel)%>' class="form-control" rows="6" onFocus="SetarEventoMascaraV4(this,'#*10000',true);" onBlur="fout(this);ValidaMascaraV4(this);"></textarea>
         <% } %>
        </div>

        <% if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, _responsavel)) { %>
        <fieldset>
          <h3 class="legend"><span><hl:message key="rotulo.avancada.decisao.judicial.titulo"/></span></h3>
        
          <div class="row pl-3">
            <div class="form-group col-sm-6">
              <label for="tjuCodigo"><hl:message key="rotulo.avancada.decisao.judicial.tipo.justica"/></label>
              <%=JspHelper.geraCombo(lstTipoJustica, "tjuCodigo", Columns.TJU_CODIGO, Columns.TJU_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", _responsavel), "class=\"form-control form-select\"", true, 1, JspHelper.verificaVarQryStr(request, "tjuCodigo"), desabilitaOpcoesAvancadas)%>
              <% if (desabilitaOpcoesAvancadas) { %>
                <hl:htmlinput name="tjuCodigo" di="tjuCodigo" type="hidden" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "tjuCodigo"))%>" />
              <% } %>
            </div>
            
            <div class="form-group col-sm-6">
              <label for="djuEstado"><hl:message key="rotulo.avancada.decisao.judicial.estado"/></label>
              <%= JspHelper.geraComboUF("djuEstado", "djuEstado", JspHelper.verificaVarQryStr(request, "djuEstado"), desabilitaOpcoesAvancadas, "form-control", _responsavel) %>
            </div>
            
            <div class="form-group col-sm-6">
              <label for="djuComarca"><hl:message key="rotulo.avancada.decisao.judicial.comarca"/></label>
              <hl:htmlinput name="djuComarca" di="djuComarca" type="text" classe="form-control" size="40" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "djuComarca"))%>" readonly="<%=String.valueOf(desabilitaOpcoesAvancadas)%>"/>
              <hl:htmlinput name="cidCodigo" di="cidCodigo" type="hidden" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "cidCodigo"))%>" />
            </div>
            
            <div class="form-group col-sm-6">
              <label for="djuNumProcesso"><hl:message key="rotulo.avancada.decisao.judicial.numero.processo"/></label>
              <hl:htmlinput name="djuNumProcesso" di="djuNumProcesso" type="text" classe="form-control" size="40" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "djuNumProcesso"))%>" readonly="<%=String.valueOf(desabilitaOpcoesAvancadas)%>"/>
            </div>
            
            <div class="form-group col-sm-6">
              <label for="djuData"><hl:message key="rotulo.avancada.decisao.judicial.data"/></label>
              <hl:htmlinput name="djuData" di="djuData" type="text" classe="form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "djuData"))%>" readonly="<%=String.valueOf(desabilitaOpcoesAvancadas)%>"/>
            </div>
            
            <div class="form-group col-sm-12">
              <label for="djuTexto"><hl:message key="rotulo.avancada.decisao.judicial.texto"/></label>
              <% if (desabilitaOpcoesAvancadas) { %>
                <%=TextHelper.forHtmlContent(java.net.URLDecoder.decode(JspHelper.verificaVarQryStr(request, "djuTexto"), "UTF-8"))%>
                <hl:htmlinput type="hidden" name="djuTexto" value="<%=TextHelper.forHtmlAttribute(java.net.URLDecoder.decode(JspHelper.verificaVarQryStr(request, "djuTexto"), "UTF-8"))%>"/>
              <% } else { %>
                <textarea name="djuTexto" id="djuTexto" class="form-control" cols="32" rows="5" onFocus="SetarEventoMascaraV4(this,'#*10000',true);" onBlur="fout(this);ValidaMascaraV4(this);"></textarea>
              <% } %>
            </div>
          </div>
        </fieldset>
        <% } %>

      </div>
    </div>
  </div>
</div>

<% if (desabilitaOpcoesAvancadas) { %>
  <hl:htmlinput type="hidden" name="validaMargem" value="<%=TextHelper.forHtmlAttribute((_validaMargemAvancado))%>"/>
  <hl:htmlinput type="hidden" name="validaTaxa" value="<%=TextHelper.forHtmlAttribute((_validaTaxaAvancado))%>"/>
  <hl:htmlinput type="hidden" name="validaPrazo" value="<%=TextHelper.forHtmlAttribute((_validaPrazoAvancado))%>"/>
  <hl:htmlinput type="hidden" name="validaDadosBancarios" value="<%=TextHelper.forHtmlAttribute((_validaDadosBancariosAvancado))%>"/>
  <hl:htmlinput type="hidden" name="validaSenhaServidor" value="<%=TextHelper.forHtmlAttribute((_validaSenhaServidorAvancado))%>"/>
  <hl:htmlinput type="hidden" name="validaBloqSerCnvCsa" value="<%=TextHelper.forHtmlAttribute((_validaBloqSerCnvCsaAvancado))%>"/>
  <hl:htmlinput type="hidden" name="validaDataNascimento" value="<%=TextHelper.forHtmlAttribute((_validaDataNascAvancado))%>"/>
  <hl:htmlinput type="hidden" name="validaLimiteAde" value="<%=TextHelper.forHtmlAttribute((_validaLimiteAdeAvancado))%>"/>
  <hl:htmlinput type="hidden" name="tmoCodigo" value="<%=TextHelper.forHtmlAttribute(request.getAttribute("tmoCodigo"))%>"/>
  <hl:htmlinput type="hidden" name="adeObs" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "adeObs"))%>"/>
<% } %>

<script language="JavaScript" type="text/JavaScript">
<% if (!desabilitaOpcoesAvancadas) { %>
padraoValidaMargem = '<%=(boolean)ReservarMargemParametros.PADRAO_VALIDA_MARGEM %>';
padraoValidaTaxaJuros = '<%=(boolean)ReservarMargemParametros.PADRAO_VALIDA_TAXA_JUROS%>';
padraoValidaPrazo = '<%=(boolean)ReservarMargemParametros.PADRAO_VALIDA_PRAZO%>';
padraoValidaDadosBancarios = '<%=(boolean)ReservarMargemParametros.PADRAO_VALIDA_DADOS_BANCARIOS%>';
padraoValidaSenhaServidor = '<%=(boolean)ReservarMargemParametros.PADRAO_VALIDA_SENHA_SERVIDOR%>';
padraoValidaBloqSerCnvCsa = '<%=(boolean)ReservarMargemParametros.PADRAO_VALIDA_BLOQ_SER_CNV_CSA%>';
padraoValidaDataNasc = '<%=(boolean)ReservarMargemParametros.PADRAO_VALIDA_DATA_NASCIMENTO %>';
padraoValidaLimiteAde = '<%=(boolean)ReservarMargemParametros.PADRAO_VALIDA_LIMITE_ADE %>';

function validarOpcoesAvancadas() {
  if (verificarOpcoesAvancadasAlteradas()) {
    var ControlesAvancados = new Array("tmoCodigo", "adeObs");
    var MsgsAvancadas = new Array('<hl:message key="mensagem.motivo.operacao.obrigatorio"/>','<hl:message key="mensagem.informe.observacao"/>');
    if (!ValidaCampos(ControlesAvancados, MsgsAvancadas)) {
      return false;
    }
  }
  return true;
}

function verificarOpcoesAvancadasAlteradas() {  
    var validaMargem = getCheckedRadio('form1', 'validaMargem');    
    var validaTaxaJuros = getCheckedRadio('form1', 'validaTaxa');
    var validaPrazo = getCheckedRadio('form1', 'validaPrazo');
    var validaDadosBancarios = getCheckedRadio('form1', 'validaDadosBancarios');
    var validaSenhaServidor = getCheckedRadio('form1', 'validaSenhaServidor');
    var validaBloqSerCnvCsa = getCheckedRadio('form1', 'validaBloqSerCnvCsa');
    var validaDataNascimento = getCheckedRadio('form1', 'validaDataNascimento');
    var validaLimiteAde = getCheckedRadio('form1', 'validaLimiteAde');
    
    return validaMargem != padraoValidaMargem ||
           validaTaxaJuros != padraoValidaTaxaJuros ||
           validaPrazo != padraoValidaPrazo ||
           validaDadosBancarios != padraoValidaDadosBancarios ||
           validaSenhaServidor != padraoValidaSenhaServidor ||
           validaDataNascimento != padraoValidaDataNasc ||
           validaLimiteAde != padraoValidaLimiteAde ||
           validaBloqSerCnvCsa != padraoValidaBloqSerCnvCsa;
}

<% } %>
</script>
<% if (!desabilitaOpcoesAvancadas && ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, _responsavel)) { %>
<script src="<c:url value='/node_modules/jquery/dist/jquery.min.js'/>?<hl:message key='release.tag'/>"></script>
<script language="JavaScript" type="text/JavaScript">
$(document).ready(function() {
    $(function() {
        $("#djuComarca").autocomplete({
            source: function(request, response) {
            $.ajax({
            url: "../v3/listarCidades",
            type: "POST",
            dataType: "json",
            data: { "acao" : "reservarConsignacao", "name": request.term , "ufCod" : $("#djuEstado").val() },
                success: function( data ) {
                    response( $.map( data, function( item ) {
                    return {
                      label: item.atributos['<%=Columns.CID_NOME%>'] + ' - ' + item.atributos['<%=Columns.CID_UF_CODIGO%>'],
                      value: item.atributos['<%=Columns.CID_NOME%>'] + ' - ' + item.atributos['<%=Columns.CID_UF_CODIGO%>'],
                      value2: item.atributos['<%=Columns.CID_CODIGO%>'],
                    }
                    }));
                },
                error: function (error) {
                    $("[name='cidCodigo']").val("");       
                }
            });
            },
            select: function( event, ui ) {
               $("#djuComarca").val(ui.item.label);
               $("[name='cidCodigo']").val(ui.item.value2);
               return false;
            },
            minLength: 3
        });
    });
});
</script>
<% } %>
