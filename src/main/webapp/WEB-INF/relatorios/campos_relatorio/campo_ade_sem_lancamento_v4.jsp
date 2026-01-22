<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
   AcessoSistema responsavelAnexadoPage = JspHelper.getAcessoSistema(request);
   String obrAdeSemLancamentoPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   String paramAdeSemLancamentoDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramAdeSemLancamentoDisabled) && paramAdeSemLancamentoDisabled.equals("true")) ? true:false;
%>
              <fieldset class="col-sm-12 col-md-12">
                <div class="legend">
                  <span>${descricoes[recurso]}</span>
                </div>
                <div class="row">
                  <div class="col-sm-12 col-md-6">
                    <div class="form-check form-check-inline">
                      <input class="form-check-input ml-1" type="radio" name="adeSemLancamento" id="adeSemLancamentoSim" title='<hl:message key="rotulo.sim"/>' value="true" <%= JspHelper.verificaVarQryStr(request, "adeSemLancamento").equals("true") ? "checked" : "" %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);" <%=(String)(desabilitado ? "disabled='disabled'" : "")%>>
                      <label class="form-check-label labelSemNegrito ml-1 pr-4" for="adeSemLancamentoSim"><hl:message key="rotulo.sim"/></label>
                    </div>
                      <div class="form-check form-check-inline">
                      <input class="form-check-input ml-1" type="radio" name="adeSemLancamento" id="adeSemLancamentoNao" title='<hl:message key="rotulo.nao"/>' value="false" <%=!JspHelper.verificaVarQryStr(request, "adeSemLancamento").equals("true") ? "checked" : "" %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);" <%=(String)(desabilitado ? "disabled='disabled'" : "")%>>
                      <label class="form-check-label labelSemNegrito ml-1 pr-4" for="adeSemLancamentoNao"><hl:message key="rotulo.nao"/></label>
                    </div>
                  </div>
                </div>
              </fieldset>

    <% if (obrAdeSemLancamentoPage.equals("true")) { %>
        <script type="text/JavaScript">
         function funAdeSemLancamentoPage() {
            camposObrigatorios = camposObrigatorios + 'adeSemLancamento,';
            msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.ade.sem.lancamento"/>,';
         }
        addLoadEvent(funAdeSemLancamentoPage);     
        </script>        
    <% } %>             

        <script type="text/JavaScript">
         function valida_campo_ade_sem_lancamento() {
             return true;
         }
        </script>        

