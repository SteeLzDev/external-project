<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String nomeCampo =  JspHelper.verificaVarQryStr(request, "nome_campo");
String nomeLista =  JspHelper.verificaVarQryStr(request, "nome_lista");
String listaResult = JspHelper.verificaVarQryStr(request, "lista_resultado");
String label = JspHelper.verificaVarQryStr(request, "label");
String mascara = JspHelper.verificaVarQryStr(request, "mascara");
String tipoEndereco = JspHelper.verificaVarQryStr(request, "tipo_endereco");
String podeEditar = JspHelper.verificaVarQryStr(request, "pode_editar");
String bloquearIpInterno = JspHelper.verificaVarQryStr(request, "bloquear_ip_interno");
String placeHolder = JspHelper.verificaVarQryStr(request, "placeHolder");

if (!TextHelper.isNull(label)) {
    label = ApplicationResourcesHelper.getMessage(label, responsavel);
}
if (!TextHelper.isNull(placeHolder)) {
    placeHolder = ApplicationResourcesHelper.getMessage(placeHolder, responsavel);
}

boolean podeEditarEntidade = (podeEditar != null && podeEditar.equals("false"))? false: true;
String [] selectOptions = null;
if (!TextHelper.isNull(listaResult)) {
    String listaElementos = (!TextHelper.isNull(request.getParameter(listaResult))) ? request.getParameter(listaResult) :
                                                                                      (String) request.getAttribute(listaResult);
    if (!TextHelper.isNull(listaElementos)) {
        selectOptions = listaElementos.split(";");
    }
}
%>
<div class="row">
   <%if (podeEditarEntidade) { %>
     <div class="form-group col-sm-3">
       <label for="<%=TextHelper.forHtmlAttribute(nomeCampo )%>"><%=label %></label>
       <% if (!TextHelper.isNull(mascara)) { %>
          <input type="text" class="form-control w-100" onFocus="SetarEventoMascara(this,'<%=TextHelper.forJavaScript(mascara )%>',true);" 
                 onBlur="fout(this);ValidaMascara(this);" name="<%=TextHelper.forHtmlAttribute(nomeCampo )%>"
                 id="<%=TextHelper.forHtmlAttribute(nomeCampo )%>" placeholder="<%=placeHolder%>">
       <% } else { %>
          <input type="text" class="form-control w-100" onBlur="fout(this);" CLASS="EditNormal" name="<%=TextHelper.forHtmlAttribute(nomeCampo )%>"
                 name="<%=TextHelper.forHtmlAttribute(nomeCampo )%>" id="<%=TextHelper.forHtmlAttribute(nomeCampo )%>" value="" placeholder="<%=placeHolder%>">
       <% } %>
       <% if ("numero_ip".equals(tipoEndereco)) { 
             String numero = (JspHelper.getAcessoSistema(request).getIpUsuario() != null)? JspHelper.getAcessoSistema(request).getIpUsuario(): "";  
       %>
          <div class="m-1">
            <a class="btn btn-outline-danger d-print-none" href="#no-back" onClick="copiaIp('<%=TextHelper.forJavaScript(nomeCampo )%>','<%=TextHelper.forJavaScript(numero )%>'); return false;"><hl:message key="rotulo.usar.ip.atual"/></a>
          </div>
       <%
          }
       %>   
     </div>
     <div class="form-group col-sm-1 mt-4">
        <a class="btn btn-primary pr-0 mt-1 d-print-none" href="#no-back" onClick="insereIp('<%=TextHelper.forJavaScript( tipoEndereco )%>', '<%=TextHelper.forJavaScript(nomeCampo )%>','<%=TextHelper.forJavaScript(nomeLista )%>','<%=TextHelper.forJavaScript(bloquearIpInterno)%>'); return false;">
         <svg width="15">
           <use xlink:href="#i-avancar"></use>
         </svg>
        </a>
        <a class="btn btn-primary pr-0 mt-1 d-print-none" href="#no-back" onClick="removeDaLista('<%=TextHelper.forJavaScript(nomeLista )%>'); return false;">
         <svg width="15">
           <use xlink:href="#i-voltar"></use>
         </svg>  
        </a>
         <input type="hidden" id="<%=TextHelper.forHtmlAttribute(listaResult )%>" name="<%=TextHelper.forHtmlAttribute(listaResult )%>">
     </div>
   <%} else { %>
       <div class="form-group col-sm-3">
          <label for="<%=TextHelper.forHtmlAttribute(nomeLista )%>"><%=label %></label>
       </div> 
   <%} %>
   <div class="form-group col-sm-4 mt-4">
     <select class="form-control w-100" id="<%=TextHelper.forHtmlAttribute(nomeLista )%>" multiple="multiple" size="6" <% if (!podeEditarEntidade){ %> disabled <% } %>>
       <%if (selectOptions != null) {
            for (int i=0; i < selectOptions.length; i++) { %>
               <option value="<%= TextHelper.forHtmlAttribute(selectOptions[i].trim())%>"><%= TextHelper.forHtmlAttribute(selectOptions[i].trim())%></option>
       <%   }
         }%>       
     </select> 
   </div> 
    <% if (podeEditarEntidade) {
          if ("numero_ip".equals(tipoEndereco)) { %>
            <div class="form-group slider col-sm-4 col-md-4 mt-4 mb-2">
               <div class="tooltip-inner"><hl:message key="mensagem.informacao.incluir.ip.acesso.v4"/></div>
            </div>
       <% } else { %>
            <div class="form-group slider col-sm-4 col-md-4 mt-4 mb-2">
               <div class="tooltip-inner"><hl:message key="mensagem.informacao.incluir.endereco.acesso.v4"/></div>
            </div>         
    <%    } 
       }   %>
</div>