<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
   AcessoSistema responsavelAnexadoPage = JspHelper.getAcessoSistema(request);
   String obrAnexadoPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   String paramAnexadoDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramAnexadoDisabled) && paramAnexadoDisabled.equals("true")) ? true:false;
%>
              <fieldset class="col-sm-12 col-md-6">
                <div class="legend">
                  <span>${descricoes[recurso]}</span>
                </div>
                <div class="row">
                  <div class="col-sm-12 col-md-6">
                    <div class="form-check form-check-inline">
                      <input class="form-check-input ml-1" type="radio" name="anexado" id="anexadoSim" title='<hl:message key="rotulo.sim"/>' value="true" <%= JspHelper.verificaVarQryStr(request, "anexado").equals("true") ? "checked" : "" %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);" onChange="habilitaDesabilitaAnexado();" <%=(String)(desabilitado ? "disabled='disabled'" : "")%>>
                      <label class="form-check-label labelSemNegrito ml-1 pr-4" for="anexadoSim"><hl:message key="rotulo.sim"/></label>
                    </div>
                      <div class="form-check form-check-inline">
                      <input class="form-check-input ml-1" type="radio" name="anexado" id="anexadoNao" title='<hl:message key="rotulo.nao"/>' value="false" <%=!JspHelper.verificaVarQryStr(request, "anexado").equals("true") ? "checked" : "" %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);" onChange="habilitaDesabilitaAnexado();" <%=(String)(desabilitado ? "disabled='disabled'" : "")%>>
                      <label class="form-check-label labelSemNegrito ml-1 pr-4" for="anexadoNao"><hl:message key="rotulo.nao"/></label>
                    </div>
                  </div>
                </div>
              </fieldset>

    <% if (obrAnexadoPage.equals("true")) { %>
        <script type="text/JavaScript">
         function funAnexadoPage() {
            camposObrigatorios = camposObrigatorios + 'anexado,';

            msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.anexado"/>,';
         }
        addLoadEvent(funAnexadoPage);     
        </script>        
    <% } %>             

        <script type="text/JavaScript">
         function valida_campo_tem_anexo() {
             return true;
         }
        </script>        

