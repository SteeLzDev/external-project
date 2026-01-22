<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="descricao">${descricoes[recurso]}</c:set>
<%
     AcessoSistema responsavelPrazoPage = JspHelper.getAcessoSistema(request);
     String mensagemPrazo = ApplicationResourcesHelper.getMessage("mensagem.filtro.prazo", responsavelPrazoPage);
     String obrPrazoPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
     String descPrazoPage = pageContext.getAttribute("descricao").toString();
     String descPrazoMultiploPage = descPrazoPage + " " + ApplicationResourcesHelper.getMessage("rotulo.relatorio.multiplo.doze", responsavelPrazoPage);
     String prazoMultiploDoze = (String) JspHelper.verificaVarQryStr(request, "prazoMultiploDoze");
     String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
     boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
     
     String others = "";
     if (desabilitado) {
         others = "disabled";
     }

%>

     <div class="col-sm-12">
       <fieldset>
         <div class="legend">
           <span><%=TextHelper.forHtmlContent(descPrazoMultiploPage)%></span>
         </div>
         <div class="row">
           <div class="col-sm-12 col-md-6">
             <div class="form-group mb-1" role="radiogroup" aria-labelledby="PrDescricao">
               <div class="form-check form-check-inline pt-2">
                 <input class="form-check-input ml-1" type="radio" name="prazoMultiploDoze" id="prazoMultiploDoze2" title="<hl:message key="rotulo.sim"/>" value="true" <% if (!TextHelper.isNull(prazoMultiploDoze) && prazoMultiploDoze.equals("true")) { %> checked <% } %> <%if (desabilitado) {%> disabled <%} %> onclick="valida_campo_prazo_multiplo(this);" onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                 <label class="form-check-label labelSemNegrito ml-1 pr-4" for="prazoMultiploDoze2"><hl:message key="rotulo.sim"/></label>
               </div>
                 <div class="form-check-inline form-check">
                   <input class="form-check-input ml-1" type="radio" name="prazoMultiploDoze" id="prazoMultiploDoze1" title="<hl:message key="rotulo.nao"/>" value="false" <% if (TextHelper.isNull(prazoMultiploDoze) || prazoMultiploDoze.equals("false")) { %> checked <% } %> <%if (desabilitado) {%> disabled <%} %> onclick="valida_campo_prazo_multiplo(this);" onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                 <label class="form-check-label labelSemNegrito ml-1" for="prazoMultiploDoze1"><hl:message key="rotulo.nao"/></label>
               </div>
             </div>
           </div>
         </div>
       </fieldset>
      </div>

     <div class="form-group col-sm-12 col-md-6">
         <label id="lblPRAZO" for="PRAZO"><%=TextHelper.forHtmlContent(descPrazoPage)%></label>
         <hl:htmlinput name="PRAZO" 
                       di="PRAZO" 
                       type="text" 
                       classe="form-control"
                       size="60"
                       maxlength="180"
                       mask="#P180"
                       others="<%=TextHelper.forHtmlAttribute(others )%>"
                       value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "PRAZO"))%>" 
                       placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavelPrazoPage, descPrazoPage)%>"
         />
         <div class="slider col-sm-12 col-md-12 mt-2 pl-0 pr-0">
           <div class="tooltip-inner"><%=TextHelper.forHtmlContent(mensagemPrazo)%></div>
         </div>
     </div>

    <% if (obrPrazoPage.equals("true")) { %>
       <script type="text/JavaScript">
       function funPrazoPage() {
          camposObrigatorios = camposObrigatorios + 'Prazo,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.prazo"/>,';
       }
       addLoadEvent(funPrazoPage);     
       </script>
    <% } %>             

       <script type="text/JavaScript">
       var f0 = document.forms[0];   
       function valida_campo_prazo_multiplo(prazoMultiplo) {
    	   if (prazoMultiplo.value == 'true') {
    		   f0.PRAZO.value = '';
    		   f0.PRAZO.disabled = true;
    	   } else if (prazoMultiplo.value == 'false') {
    		   f0.PRAZO.disabled = false;
    		   f0.PRAZO.focus();
    	   }
    	   return true;
       }
       function valida_campo_prazo() {
           return true;
       }
       </script>        
