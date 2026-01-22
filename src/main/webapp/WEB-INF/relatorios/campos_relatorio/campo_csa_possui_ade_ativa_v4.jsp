<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%
boolean desabilitado = (JspHelper.verificaVarQryStr(request, "disabled").equals("true"));
%>
            <fieldset class="col-sm-12 col-md-12">
              <legend class="legend pt-2"><span>${descricoes[recurso]}</span></legend>
              <div class="row">
                <div class="col-sm-12 col-md-6">
                  <div class="form-group mb-1" role="radiogroup" aria-labelledby="possuiAdeAtivaDescricao">
                      <div class="form-check form-check-inline">
                      <input class="form-check-input ml-1" type="radio" name="possuiAdeAtiva" id="possuiAdeAtivaSim" title='<hl:message key="rotulo.sim"/>' value="true"  <%=JspHelper.verificaVarQryStr(request, "possuiAdeAtiva").equals("true")  ? "checked" : "" %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);" onChange="habilitaDesabilitaAgendamento();" <%=(String)(desabilitado ? "disabled='disabled'" : "")%>>
                      <label class="form-check-label labelSemNegrito ml-1 pr-4" for="possuiAdeAtivaSim"><hl:message key="rotulo.sim"/></label>
                    </div>
                      <div class="form-check form-check-inline">
                      <input class="form-check-input ml-1" type="radio" name="possuiAdeAtiva" id="possuiAdeAtivaNao" title='<hl:message key="rotulo.nao"/>' value="false" <%=JspHelper.verificaVarQryStr(request, "possuiAdeAtiva").equals("false") ? "checked" : "" %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);" onChange="habilitaDesabilitaAgendamento();" <%=(String)(desabilitado ? "disabled='disabled'" : "")%>>
                      <label class="form-check-label labelSemNegrito ml-1 pr-4" for="possuiAdeAtivaNao"><hl:message key="rotulo.nao"/></label>
                    </div>
                  </div>
                </div>
              </div>
            </fieldset>
        <script type="text/JavaScript">
        function valida_campo_csa_possui_ade_ativa() {
          return true;
        }
        </script>
