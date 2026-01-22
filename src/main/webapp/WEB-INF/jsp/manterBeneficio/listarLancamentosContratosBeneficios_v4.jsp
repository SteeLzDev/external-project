<%--
* <p>Title: consultarRelacaoBeneficios</p>
* <p>Description: Consultar relação benefícios v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>

<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper"%>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.Date"%>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.values.Columns"%>
<%@ page import="java.util.List"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

	List<TransferObject> lancamentos = (List<TransferObject>) request.getAttribute("lancamentos");
	List<Date> listaPeriodos = (List<Date>) request.getAttribute("listaPeriodos");
	TransferObject lancamentosInfo = (TransferObject) request.getAttribute("lancamentosInfo");
    String cbe_codigo = (String) request.getAttribute(Columns.CBE_CODIGO);
    String rse_codigo = (String) request.getAttribute(Columns.RSE_CODIGO);
    String bfc_codigo = (String) request.getAttribute(Columns.BFC_CODIGO);
    String ser_codigo = (String) request.getAttribute(Columns.SER_CODIGO);
    String tib_codigo = (String) request.getAttribute(Columns.TIB_CODIGO);
    String ben_codigo = (String) request.getAttribute(Columns.BEN_CODIGO);
    String contratosAtivos = (String) request.getAttribute("contratosAtivos");
    
    String prd_data_desconto = (String) request.getAttribute("prd_data_desconto");
%>


<script type="text/JavaScript">

function sendPeriodo(cbcPeriodo) {
    postData("../v3/listarLancamentosContratosBeneficios?acao=listar&contratosAtivos=<%=contratosAtivos%>&_skip_history_=true&<%=Columns.getColumnName(Columns.BEN_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ben_codigo)%>&<%=Columns.getColumnName(Columns.SER_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&<%=Columns.getColumnName(Columns.CBE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(cbe_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=Columns.getColumnName(Columns.PRD_DATA_DESCONTO)%>=" + cbcPeriodo);
}

</script>

<c:set var="title">
  <hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-6">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.beneficiario" /></h2>
        </div>
        <div class="card-body">
          <dl class="row data-list">
            <dt class="col-6"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.matricula.titular" />: </dt>
            <dd class="col-6"><%= lancamentosInfo.getAttribute(Columns.RSE_MATRICULA) %></dd>
            <dt class="col-6"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.nome.titular" />: </dt>
            <dd class="col-6"><%= TextHelper.forHtmlContent(lancamentosInfo.getAttribute(Columns.SER_NOME)) %></dd>
            <dt class="col-6"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.tipo.beneficiario"/>: </dt>
            <dd class="col-6"><%= TextHelper.forHtmlContent(lancamentosInfo.getAttribute(Columns.TIB_DESCRICAO)) %></dd>
            <dt class="col-6"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.beneficiario"/>: </dt>
            <dd class="col-6"><%= TextHelper.forHtmlContent(lancamentosInfo.getAttribute(Columns.BFC_NOME)) %></dd>
            <dt class="col-6"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.beneficiario.cpf"/>: </dt>
            <dd class="col-6"><%= lancamentosInfo.getAttribute(Columns.BFC_CPF) %></dd>
            <dt class="col-6"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.beneficiario.grau.parentesco"/>: </dt>
            <dd class="col-6"><%= lancamentosInfo.getAttribute(Columns.GRP_DESCRICAO) != null ? TextHelper.forHtmlContent(lancamentosInfo.getAttribute(Columns.GRP_DESCRICAO)) : ""  %></dd>
            <dt class="col-6"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.data.nascimento.beneficiario"/>: </dt>
            <dd class="col-6"><%= DateHelper.format((Date) lancamentosInfo.getAttribute(Columns.BFC_DATA_NASCIMENTO), LocaleHelper.getDatePattern()) %></dd>
          </dl>
        </div>
      </div>
    </div>
    <div class="col-sm-6">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.contrato.beneficio.titulo"/></h2>
        </div>
        <div class="card-body">
          <dl class="row data-list">
           <dt class="col-6"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.operadora" />: </dt>
            <dd class="col-6"><%= lancamentosInfo.getAttribute(Columns.CSA_IDENTIFICADOR) %> - <%= TextHelper.forHtmlContent(lancamentosInfo.getAttribute(Columns.CSA_NOME)) %></dd>
            <dt class="col-6"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.beneficio" />: </dt>
            <dd class="col-6"><%= TextHelper.forHtmlContent(lancamentosInfo.getAttribute(Columns.BEN_DESCRICAO)) %></dd>
            <dt class="col-6"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.codigo.contrato" />: </dt>
            <dd class="col-6"><%= lancamentosInfo.getAttribute(Columns.BEN_CODIGO_CONTRATO) %></dd>
            <dt class="col-6"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.codigo.registro" />: </dt>
            <dd class="col-6"><%= lancamentosInfo.getAttribute(Columns.BEN_CODIGO_REGISTRO) %></dd>
            <dt class="col-6"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.codigo.plano" />: </dt>
            <dd class="col-6"><%= lancamentosInfo.getAttribute(Columns.BEN_CODIGO_PLANO) %></dd>
            <dt class="col-6"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.numero.contrato" />: </dt>
            <dd class="col-6"><%= lancamentosInfo.getAttribute(Columns.CBE_NUMERO) %></dd>
            <dt class="col-6"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.data.inicio.vigencia"/></dt>
            <dd class="col-6"><%= DateHelper.format((Date) lancamentosInfo.getAttribute(Columns.CBE_DATA_INICIO_VIGENCIA), LocaleHelper.getDatePattern())%></dd>
          </dl>
        </div>
      </div>
    </div>
  </div>
  <div class="row">
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
            <h2 class="card-header-title"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.lancamentos"/></h2>
        </div>
        <div class="form-group col-sm-6 col-md-4"><br>
          <label for="adePeriodicidade"><hl:message key="rotulo.folha.periodo"/>:</label>
          <div class="form-check">
            <select name="CBC_PERIODO" id="CBC_PERIODO" onchange="sendPeriodo(this.value)" class="form-control" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);">
              <option value=""><hl:message key="rotulo.campo.selecione"/></option>
              <% for (Date periodo : listaPeriodos) { %>
                  <option <%= TextHelper.forHtmlAttribute(periodo).equals(prd_data_desconto) ? "selected" : ""%> value="<%=TextHelper.forHtmlAttribute(periodo)%>"><%=TextHelper.forHtmlContent(DateHelper.toPeriodString((Date) periodo))%></option>
              <% } %>
            </select>
          </div> 
        </div>
        <div class="card-body table-responsive ">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th class="ocultarColuna" scope="col" title="Selecione todos os beneficiários" style="display: none;">
                  <form class="form-check">
                    <input type="checkbox" class="form-check-input ml-0" name="checkAll">
                  </form>
                </th>
                <th scope="col"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.tipo.lancamento" /></th>
                <th scope="col"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.numero.ade"/></th>
                <th scope="col"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.data.ade" /></th>
                <th scope="col"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.parcela.data.desconto" /></th>
                <th scope="col"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.parcela.valor.previsto"/></th>
                <th scope="col"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.parcela.valor.realizado" /></th> 
                <th scope="col"><hl:message key="rotulo.relacao.lancamentos.contratos.beneficios.parcela.status"/></th>
              </tr>
            </thead>
            <% if(lancamentos.size() > 0){ %>
            <tbody>
             <% for(TransferObject lancamento : lancamentos){ %>
                <tr class="selecionarLinha">
                  <td class="ocultarColuna" style="display: none;">
                    <div class="form-check">
                      <input type="checkbox" class="form-check-input ml-0" name="selecionarCheckBox">
                    </div>
                  </td>
                  <td class="selecionarColuna"><%= TextHelper.forHtmlContent(lancamento.getAttribute(Columns.TLA_DESCRICAO)) %></td>
                  <td class="selecionarColuna"><%= lancamento.getAttribute(Columns.ADE_NUMERO) %></td>
                  <td class="selecionarColuna"><%= DateHelper.format((Date) lancamento.getAttribute(Columns.ADE_DATA), LocaleHelper.getDatePattern())%></td>
                  <td class="selecionarColuna"><%= DateHelper.format((Date) lancamento.getAttribute(Columns.PRD_DATA_DESCONTO), LocaleHelper.getDatePattern())%></td>
                  <td class="selecionarColuna"><%= NumberHelper.format(Double.parseDouble(lancamento.getAttribute(Columns.PRD_VLR_PREVISTO).toString()), NumberHelper.getLang()) %></td>
                  <td class="selecionarColuna"><%= NumberHelper.format(Double.parseDouble(lancamento.getAttribute(Columns.PRD_VLR_REALIZADO).toString()), NumberHelper.getLang()) %></td>
                  <td class="selecionarColuna"><%= TextHelper.forHtmlContent(lancamento.getAttribute(Columns.SPD_DESCRICAO)) %></td>
                </tr>
              <% } %>
            </tbody>
            <% } %>
            <% if(lancamentos.size() == 0){ %>
            <tbody>
                  <tr class="lp"><td colspan="13"><hl:message key="mensagem.erro.nenhum.registro.encontrado"/></td></tr>
            </tbody>
            <% } %>
          </table>
        </div>
      </div>
    </div>
  </div>
   <div class="float-end">
    <div class="btn-action">
      <a href="#no-back" class="btn btn-outline-danger" 
        onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"
      ><hl:message key="rotulo.botao.voltar"/></a>      
    </div>
  </div>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>