<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
   String obrCompMargemPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   String [] comprometimentoCompMargemPage = request.getParameterValues("comprometimentoMargem");
   List<String> valueListCompMargemPage = null;
   if (comprometimentoCompMargemPage != null) {
      valueListCompMargemPage = Arrays.asList(comprometimentoCompMargemPage);
   }
   
   String paramDisabledCompMargemPage = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitadoCompMargemPage = (!TextHelper.isNull(paramDisabledCompMargemPage) && paramDisabledCompMargemPage.equals("true")) ? true:false;
%>
     <div class="col-sm-12">  
       <fieldset>
         <div class="legend">
           <span>${descricoes[recurso]}</span>
         </div>
         <div class="form-check">
           <div class="row">
             <div class="col-sm-12 col-md-12">
               <div class="form-group mb-1" role="radiogroup">
                 <div class="form-check pt-2">
                  <div class="row">
                    <div class="col-sm-2 col-md-2">
                      <input class="form-check-input ml-1" type="checkbox" name="comprometimentoMargem" id="comprometimentoMargem1" title="<hl:message key="rotulo.relatorio.filtro.comprometimento.margem.menor.zero"/>" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);" <%if (comprometimentoCompMargemPage != null && valueListCompMargemPage.contains(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_MENOR_ZERO)) {%> checked disabled <%} else if (desabilitadoCompMargemPage) {%> disabled <%} %> value="<%=CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_MENOR_ZERO%>">
                      <label class="form-check-label labelSemNegrito ml-1" for="comprometimentoMargem1"><hl:message key="rotulo.relatorio.filtro.comprometimento.margem.menor.zero"/></label>
                    </div>
                    <div class="col-sm-2 col-md-2">
                      <input class="form-check-input ml-1" type="checkbox" name="comprometimentoMargem" id="comprometimentoMargem2" title="<hl:message key="rotulo.relatorio.filtro.comprometimento.margem.0.a.10"/>" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);" <%if (comprometimentoCompMargemPage != null && valueListCompMargemPage.contains(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_0_A_10)) {%> checked disabled <%} else if (desabilitadoCompMargemPage) {%> disabled <%} %> value="<%=CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_0_A_10%>">
                      <label class="form-check-label labelSemNegrito ml-1" for="comprometimentoMargem2"><hl:message key="rotulo.relatorio.filtro.comprometimento.margem.0.a.10"/></label>
                    </div>
                    <div class="col-sm-2 col-md-2">
                      <input class="form-check-input ml-1" type="checkbox" name="comprometimentoMargem" id="comprometimentoMargem3" title="<hl:message key="rotulo.relatorio.filtro.comprometimento.margem.10.a.20"/>" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);" <%if (comprometimentoCompMargemPage != null && valueListCompMargemPage.contains(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_10_A_20)) {%> checked disabled <%} else if (desabilitadoCompMargemPage) {%> disabled <%} %> value="<%=CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_10_A_20%>">
                      <label class="form-check-label labelSemNegrito ml-1" for="comprometimentoMargem3"><hl:message key="rotulo.relatorio.filtro.comprometimento.margem.10.a.20"/></label>
                    </div>
                    <div class="col-sm-2 col-md-2">
                      <input class="form-check-input ml-1" type="checkbox" name="comprometimentoMargem" id="comprometimentoMargem4" title="<hl:message key="rotulo.relatorio.filtro.comprometimento.margem.20.a.30"/>" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);" <%if (comprometimentoCompMargemPage != null && valueListCompMargemPage.contains(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_20_A_30)) {%> checked disabled <%} else if (desabilitadoCompMargemPage) {%> disabled <%} %> value="<%=CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_20_A_30%>">
                      <label class="form-check-label labelSemNegrito ml-1" for="comprometimentoMargem4"><hl:message key="rotulo.relatorio.filtro.comprometimento.margem.20.a.30"/></label>
                    </div>
                  </div>
                 </div>
               </div>
               <div class="form-group mb-1" role="radiogroup">
                 <div class="form-check pt-2">
                  <div class="row">
                    <div class="col-sm-2 col-md-2">
                      <input class="form-check-input ml-1" type="checkbox" name="comprometimentoMargem" id="comprometimentoMargem5" title="<hl:message key="rotulo.relatorio.filtro.comprometimento.margem.30.a.40"/>" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);" <%if (comprometimentoCompMargemPage != null && valueListCompMargemPage.contains(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_30_A_40)) {%> checked disabled <%} else if (desabilitadoCompMargemPage) {%> disabled <%} %> value="<%=CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_30_A_40%>">
                      <label class="form-check-label labelSemNegrito ml-1" for="comprometimentoMargem5"><hl:message key="rotulo.relatorio.filtro.comprometimento.margem.30.a.40"/></label>
                    </div>
                    <div class="col-sm-2 col-md-2">
                      <input class="form-check-input ml-1" type="checkbox" name="comprometimentoMargem" id="comprometimentoMargem6" title="<hl:message key="rotulo.relatorio.filtro.comprometimento.margem.40.a.50"/>" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);" <%if (comprometimentoCompMargemPage != null && valueListCompMargemPage.contains(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_40_A_50)) {%> checked disabled <%} else if (desabilitadoCompMargemPage) {%> disabled <%} %> value="<%=CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_40_A_50%>">
                      <label class="form-check-label labelSemNegrito ml-1" for="comprometimentoMargem6"><hl:message key="rotulo.relatorio.filtro.comprometimento.margem.40.a.50"/></label>
                    </div>
                    <div class="col-sm-2 col-md-2">
                      <input class="form-check-input ml-1" type="checkbox" name="comprometimentoMargem" id="comprometimentoMargem7" title="<hl:message key="rotulo.relatorio.filtro.comprometimento.margem.50.a.60"/>" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);" <%if (comprometimentoCompMargemPage != null && valueListCompMargemPage.contains(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_50_A_60)) {%> checked disabled <%} else if (desabilitadoCompMargemPage) {%> disabled <%} %> value="<%=CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_50_A_60%>">
                      <label class="form-check-label labelSemNegrito ml-1" for="comprometimentoMargem7"><hl:message key="rotulo.relatorio.filtro.comprometimento.margem.50.a.60"/></label>
                    </div>
                    <div class="col-sm-2 col-md-2">
                      <input class="form-check-input ml-1" type="checkbox" name="comprometimentoMargem" id="comprometimentoMargem8" title="<hl:message key="rotulo.relatorio.filtro.comprometimento.margem.60.a.70"/>" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);" <%if (comprometimentoCompMargemPage != null && valueListCompMargemPage.contains(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_60_A_70)) {%> checked disabled <%} else if (desabilitadoCompMargemPage) {%> disabled <%} %> value="<%=CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_60_A_70%>">
                      <label class="form-check-label labelSemNegrito ml-1" for="comprometimentoMargem8"><hl:message key="rotulo.relatorio.filtro.comprometimento.margem.60.a.70"/></label>
                    </div>
                  </div>
                 </div>
               </div>
               <div class="form-group mb-1" role="radiogroup">
                 <div class="form-check pt-2">
                  <div class="row">
                    <div class="col-sm-2 col-md-2">
                      <input class="form-check-input ml-1" type="checkbox" name="comprometimentoMargem" id="comprometimentoMargem9" title="<hl:message key="rotulo.relatorio.filtro.comprometimento.margem.70.a.80"/>" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);" <%if (comprometimentoCompMargemPage != null && valueListCompMargemPage.contains(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_70_A_80)) {%> checked disabled <%} else if (desabilitadoCompMargemPage) {%> disabled <%} %> value="<%=CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_70_A_80%>">
                      <label class="form-check-label labelSemNegrito ml-1" for="comprometimentoMargem9"><hl:message key="rotulo.relatorio.filtro.comprometimento.margem.70.a.80"/></label>
                    </div>
                    <div class="col-sm-2 col-md-2">
                      <input class="form-check-input ml-1" type="checkbox" name="comprometimentoMargem" id="comprometimentoMargem10" title="<hl:message key="rotulo.relatorio.filtro.comprometimento.margem.80.a.90"/>" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);" <%if (comprometimentoCompMargemPage != null && valueListCompMargemPage.contains(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_80_A_90)) {%> checked disabled <%} else if (desabilitadoCompMargemPage) {%> disabled <%} %> value="<%=CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_80_A_90%>">
                      <label class="form-check-label labelSemNegrito ml-1" for="comprometimentoMargem10"><hl:message key="rotulo.relatorio.filtro.comprometimento.margem.80.a.90"/></label>
                    </div>
                    <div class="col-sm-2 col-md-2">
                      <input class="form-check-input ml-1" type="checkbox" name="comprometimentoMargem" id="comprometimentoMargem11" title="<hl:message key="rotulo.relatorio.filtro.comprometimento.margem.90.a.100"/>" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);" <%if (comprometimentoCompMargemPage != null && valueListCompMargemPage.contains(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_90_A_100)) {%> checked disabled <%} else if (desabilitadoCompMargemPage) {%> disabled <%} %> value="<%=CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_90_A_100%>">
                      <label class="form-check-label labelSemNegrito ml-1" for="comprometimentoMargem11"><hl:message key="rotulo.relatorio.filtro.comprometimento.margem.90.a.100"/></label>
                    </div>
                    <div class="col-sm-2 col-md-2">
                      <input class="form-check-input ml-1" type="checkbox" name="comprometimentoMargem" id="comprometimentoMargem12" title="<hl:message key="rotulo.relatorio.filtro.comprometimento.margem.maior.cem"/>" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);" <%if (comprometimentoCompMargemPage != null && valueListCompMargemPage.contains(CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_MAIOR_CEM)) {%> checked disabled <%} else if (desabilitadoCompMargemPage) {%> disabled <%} %> value="<%=CodedValues.REL_FILTRO_COMPROMETIMENTO_MARGEM_MAIOR_CEM%>">
                      <label class="form-check-label labelSemNegrito ml-1" for="comprometimentoMargem12"><hl:message key="rotulo.relatorio.filtro.comprometimento.margem.maior.cem"/></label>
                    </div>
                  </div>
                 </div>
               </div>
             </div>
           </div>
         </div>
       </fieldset>
     </div>
          
        <script type="text/JavaScript">
         function valida_campo_comprometimento_margem() {
           <% if (obrCompMargemPage.equals("true")) { %>
            var tam = document.forms[0].comprometimentoMargem.length;
            var qtd = 0;
            for (var i = 0; i < tam; i++) {
              if (document.forms[0].comprometimentoMargem[i].checked == true) {
                qtd++;
              }
            }
            if (qtd <= 0) {
              alert('<hl:message key="mensagem.informe.comprometimento.margem"/>');
              return false;
            }                         
           <% } %>            
            return true;
         }
        </script>          