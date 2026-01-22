<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
   AcessoSistema responsavelAnexadoPage = JspHelper.getAcessoSistema(request);
   String obrAdePortabilidadeCartaoPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   String paramAdePortabilidadeCartaoDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramAdePortabilidadeCartaoDisabled) && paramAdePortabilidadeCartaoDisabled.equals("true")) ? true:false;
%>
              <fieldset class="col-sm-12 col-md-12">
                <div class="legend">
                  <span>${descricoes[recurso]}</span>
                </div>
                <div class="row">
                  <div class="col-sm-12 col-md-6">
                    <div class="form-check form-check-inline">
                      <input class="form-check-input ml-1" type="radio" name="adePortabilidadeCartao" id="adePortabilidadeCartaoSim" title='<hl:message key="rotulo.sim"/>' value="true" <%= JspHelper.verificaVarQryStr(request, "adePortabilidadeCartao").equals("true") ? "checked" : "" %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);" <%=(String)(desabilitado ? "disabled='disabled'" : "")%>>
                      <label class="form-check-label labelSemNegrito ml-1 pr-4" for="adePortabilidadeCartaoSim"><hl:message key="rotulo.sim"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                      <input class="form-check-input ml-1" type="radio" name="adePortabilidadeCartao" id="adePortabilidadeCartaoNao" title='<hl:message key="rotulo.nao"/>' value="false" <%=!JspHelper.verificaVarQryStr(request, "adePortabilidadeCartao").equals("true") ? "checked" : "" %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);" <%=(String)(desabilitado ? "disabled='disabled'" : "")%>>
                      <label class="form-check-label labelSemNegrito ml-1 pr-4" for="adePortabilidadeCartaoNao"><hl:message key="rotulo.nao"/></label>
                    </div>
                  </div>
                </div>
              </fieldset>

    <% if (obrAdePortabilidadeCartaoPage.equals("true")) { %>
        <script type="text/JavaScript">
         function funAdePortabilidadeCartaoPage() {
            camposObrigatorios = camposObrigatorios + 'adePortabilidadeCartao,';
            msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.ade.portabilidade.cartao"/>,';
         }
        addLoadEvent(funAdePortabilidadeCartaoPage);     
        </script>        
    <% } %>             

        <script type="text/JavaScript">
         function valida_campo_ade_portabilidade_cartao() {
             return true;
         }
        </script>        

