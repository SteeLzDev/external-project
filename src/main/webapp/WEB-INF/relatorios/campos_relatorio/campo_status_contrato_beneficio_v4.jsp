<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@page import="com.zetra.econsig.persistence.entity.StatusContratoBeneficio"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
   AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
   String obrScvPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   String [] scbCodigo = request.getParameterValues("scbCodigo");
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
   List<StatusContratoBeneficio> statusContratoBeneficioList = (List<StatusContratoBeneficio>) request.getAttribute("listaStatusContratoBeneficio");
%>

     <div class="col-sm-12">  
       <fieldset>
         <div class="legend">
           <span>${descricoes[recurso]}</span>
         </div>
         
         <% 
         int index = 0;
         for(StatusContratoBeneficio scb : statusContratoBeneficioList){ 
         if(index == 0 || index%3 == 0){ 
         %>
          <div class="row">
        <%}index++;%>
           <div class="col-md-4">
             <div class="form-group mb-1" role="radiogroup" aria-labelledby="scbCodigo">
               <div class="form-check pt-2">
                 <input class="form-check-input ml-1" type="checkbox" name="scbCodigo" id="<%=scb.getScbCodigo()%>" <% if (desabilitado) {%> disabled <%} %> value="<%=scb.getScbCodigo() + ";" + scb.getScbDescricao()%>" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                 <label class="form-check-label labelSemNegrito ml-1" for="<%=scb.getScbCodigo()%>"><%=scb.getScbDescricao()%></label>
               </div>
             </div>
           </div>
          <% if (index%3 == 0) { %>
          </div>
          <% }
          }%>
       </fieldset>
     </div>
     
      <script type="text/JavaScript">
              function valida_campo_status_contrato_beneficio() {
                 return true;
              }
      </script>