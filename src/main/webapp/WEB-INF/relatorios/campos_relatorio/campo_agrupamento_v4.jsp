<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%
   AcessoSistema responsavelAgrupamentoPage = JspHelper.getAcessoSistema(request);
   String obrAgrupamentoPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   String paramDisabledAgrupamentoPage = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitadoAgrupamentoPage = (!TextHelper.isNull(paramDisabledAgrupamentoPage) && paramDisabledAgrupamentoPage.equals("true")) ? true:false;
%>
            <fieldset class="col-sm-12 col-md-12">
              <legend class="legend pt-2"><span><hl:message key="rotulo.relatorio.titulo.configuracao.agrupamento"/></span></legend>
                <div class="row">
                    <div class="form-group col-sm-12 col-md-6">
                        <div><span id="agDescricao">${descricoes[recurso]}</span></div>
                        <div class="form-group mb-1" role="radiogroup" aria-labelledby="agrupamentoDescricao">
                            <div class="form-check form-check-inline">
                                <input class="form-check-input ml-1" type="radio" name="agrupamento" id="agrupamentoSim"
                                       title='<hl:message key="rotulo.sim"/>'
                                       value="true" <%= !JspHelper.verificaVarQryStr(request, "agrupamento").equals("false") ? "checked" : "" %>
                                       onFocus="SetarEventoMascaraV4(this,'#*100',true);"
                                       onBlur="fout(this);ValidaMascaraV4(this);" <%=(String)(desabilitadoAgrupamentoPage ? "disabled='disabled'" : "")%>>
                                <label class="form-check-label labelSemNegrito ml-1 pr-4"
                                       for="agrupamentoSim"><hl:message key="rotulo.sim"/></label>
                            </div>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input ml-1" type="radio" name="agrupamento" id="agrupamentoNao"
                                       title='<hl:message key="rotulo.nao"/>'
                                       value="false" <%=JspHelper.verificaVarQryStr(request, "agrupamento").equals("false") ? "checked" : "" %>
                                       onFocus="SetarEventoMascaraV4(this,'#*100',true);"
                                       onBlur="fout(this);ValidaMascaraV4(this);" <%=(String)(desabilitadoAgrupamentoPage ? "disabled='disabled'" : "")%>>
                                <label class="form-check-label labelSemNegrito ml-1 pr-4"
                                       for="agrupamentoNao"><hl:message key="rotulo.nao"/></label>
                            </div>
                        </div>
                    </div>
                </div>
            </fieldset>
        <script type="text/JavaScript">
        <%if (obrAgrupamentoPage.equals("true")) {%>
        function funAgdPage() {
          camposObrigatorios = camposObrigatorios + 'agrupamento,';
          msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.agrupamento"/>,';
        }
        addLoadEvent(funAgdPage);
        <%}%>
        function valida_campo_agrupamento() {
          return true;
        }
        </script>
