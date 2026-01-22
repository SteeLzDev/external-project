<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.helper.periodo.PeriodoHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*"%>

<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  String tipo = (String) request.getAttribute("tipo");
  String csaNome = (String) request.getAttribute("csaNome");
  String echCodigo = (String) request.getAttribute("echCodigo");
  String plaCodigo = (String) request.getAttribute("plaCodigo");
  String posCodigo = (String) request.getAttribute("posCodigo");
  String svcDescricao = (String) request.getAttribute("svcDescricao");
  Date decData = (Date) request.getAttribute("decData");
  String adePrazo = (String) request.getAttribute("adePrazo");
  String adeSemPrazo = (String) request.getAttribute("adeSemPrazo");
  String adeValor = (String) request.getAttribute("adeValor");
  String adeIdentificador = (String) request.getAttribute("adeIdentificador");
  String adeCarencia = (String) request.getAttribute("adeCarencia");
  String indice = (String) request.getAttribute("indice");
  String planoPorDesconto = (String) request.getAttribute("planoPorDesconto");
  String rateio = (String) request.getAttribute("rateio");
  List<?> permissionarios = (List<?>) request.getAttribute("permissionarios");
  String postoDescricao = (String) request.getAttribute("postoDescricao");
  String endDescricao = (String) request.getAttribute("endDescricao");
  String plaDescricao = (String) request.getAttribute("plaDescricao");
  String labelTipoValor = (String) request.getAttribute("labelTipoValor");
  String labelAdePrazo  = (String) request.getAttribute("labelAdePrazo");
  String mascaraAdeIdentificador = (String) request.getAttribute("mascaraAdeIdentificador");
  TransferObject endereco = (TransferObject) request.getAttribute("endereco");
%>

<c:set var="title">
  <hl:message key="rotulo.listar.permissionario.titulo"/>
</c:set>

<c:set var="imageHeader">
	<use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="bodyContent">
  <form name="form1" method="post" action="../v3/lancarDespesaComum?acao=lancarDespesaComum&<%=SynchronizerToken.generateToken4URL(request)%>&_skip_history_=true">
    <div class="row firefox-print-fix">
      <div class="card-body">
        <div class="row firefox-print-fix">
          <div class="col-sm">
            <div class="card">
              <div class="card-header">
                <h2 class="card-header-title"><hl:message key="rotulo.despesa.comum.informacoes"/></h2>
              </div>
              <div class="card-body">
                <dl class="row data-list firefox-print-fix">
                  <dt class="col-6"><hl:message key="rotulo.consignataria.singular"/></dt>
                  <dd class="col-6"><%=TextHelper.forHtmlContent(csaNome)%></dd>
                  <dt class="col-6"><hl:message key="rotulo.endereco.singular"/></dt>
                  <dd class="col-6"><%=TextHelper.forHtmlContent(endDescricao)%></dd>
                  <dt class="col-6"><hl:message key="rotulo.plano.singular"/></dt>
                  <dd class="col-6"><%=TextHelper.forHtmlContent(plaDescricao)%></dd>
                  <dt class="col-6"><hl:message key="rotulo.servico.singular"/></dt>
                  <dd class="col-6"><%=TextHelper.forHtmlContent(svcDescricao)%></dd>
                  <%if(planoPorDesconto.equalsIgnoreCase("true")){ %>
                    <dt class="col-6"><hl:message key="rotulo.posto.singular"/></dt>
                    <dd class="col-6"><%=TextHelper.forHtmlContent(postoDescricao)%></dd>
                  <% } %>
                  <%if(!TextHelper.isNull(decData)){ %>
                    <dt class="col-6"><hl:message key="rotulo.despesa.comum.data"/></dt>
                    <dd class="col-6"><%=DateHelper.format(decData, LocaleHelper.getDatePattern())%></dd>
                  <% } %>
                  <dt class="col-6"><hl:message key="rotulo.valor.singular"/> (<%=TextHelper.forHtmlContent(labelTipoValor)%>)</dt>
                  <dd class="col-6"><hl:htmlinput name="adeVlr" type="text" classe="Edit" di="adeVlr" size="8" mask="#F11" value="<%=TextHelper.forHtmlAttribute(adeValor)%>" readonly="true" /></dd>
                  <dt class="col-6"><hl:message key="rotulo.consignacao.prazo"/></dt>
                  <dd class="col-6"><%=labelAdePrazo.equals("") ? ApplicationResourcesHelper.getMessage("rotulo.indeterminado.singular", responsavel) : TextHelper.forHtmlContent(labelAdePrazo)%></dd>
                  <hl:htmlinput name="adePrazo" type="hidden" di="adePrazo" value="<%=TextHelper.forHtmlAttribute(adePrazo)%>" />
                  <hl:htmlinput name="adeSemPrazo" type="hidden" di="adeSemPrazo" value="<%=TextHelper.forHtmlAttribute(adeSemPrazo)%>" />
                  
                  <dt class="col-6"><hl:message key="rotulo.consignacao.carencia"/></dt>
                  <dd class="col-6"><%=TextHelper.forHtmlContent(adeCarencia)%></dd>
                  <hl:htmlinput name="adeCarencia" type="hidden" di="adeCarencia" value="<%=TextHelper.forHtmlAttribute(adeCarencia)%>" />

                  <dt class="col-6"><hl:message key="rotulo.consignacao.identificador"/></dt>
                  <dd class="col-6"><hl:htmlinput name="adeIdentificador" type="text" classe="Edit" di="adeIdentificador" size="15" mask="<%=TextHelper.isNull(mascaraAdeIdentificador) ? "#*40":mascaraAdeIdentificador %>" nf="btnEnvia" readonly="true" value="<%=TextHelper.forHtmlAttribute(adeIdentificador)%>" /></dd>
                  <dt class="col-6"><hl:message key="rotulo.indice.singular"/></dt>
                  <dd class="col-6"><%=TextHelper.forHtmlContent(indice)%></dd>
                  <dt class="col-6"><hl:message key="rotulo.solicitacao.suporte.responsavel"/></dt>
                  <dd class="col-6"><%=TextHelper.forHtmlContent(responsavel.getUsuLogin())%></dd>
                </dl>
              </div>  
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="row">
      <div class="col-sm">
        <div class="card">
          <div class="card-header">
            <h2 class="card-header-title"><hl:message key="rotulo.despesa.comum"/></h2>
          </div>
          <div class="card-body table-responsive ">
            <table class="table table-striped table-hover">
              <thead>
                <tr>
                  <th scope="col"><hl:message key="rotulo.servidor.matricula"/></th>
                  <th scope="col"><hl:message key="rotulo.permissionario.singular"/></th>
                  <th scope="col"><hl:message key="rotulo.usuario.cpf"/></th>
                  <th scope="col"><hl:message key="rotulo.posto.singular"/></th>
                  <th scope="col"><hl:message key="rotulo.permissionario.tipo"/></th>
                  <th scope="col"><hl:message key="rotulo.despesa.comum.situacao"/></th>
                  <th scope="col"><hl:message key="rotulo.endereco.singular"/></th>
                  <th scope="col"><hl:message key="rotulo.permissionario.complemento"/></th>
                  <th scope="col"><hl:message key="rotulo.consignacao.valor.parcela.abreviado"/></th>
                  <th scope="col"><hl:message key="rotulo.despesa.comum.inicio"/></th>
                  <th scope="col"><hl:message key="rotulo.despesa.comum.fim"/></th>
                </tr>
              </thead>
              <tbody>
                <%=JspHelper.msgRstVazio(permissionarios.size()==0, 13, responsavel)%>
                <%
                  Iterator<?> it = permissionarios.iterator();
                  String prm_codigo, rse_codigo, rse_matricula, ser_nome, posDescricao, trsDescricao, srsDescricao, echDescricao, cpf, complemento, dataInicio, dataFim;
                  BigDecimal valor = NumberHelper.parseDecimal(adeValor);
                  Integer prazo;
                  
                  if(!rateio.isEmpty()) {
                      if(rateio.equalsIgnoreCase("uni")) {
                          int unidades = Integer.parseInt(endereco.getAttribute(Columns.ECH_QTD_UNIDADES).toString());
                          valor = valor.divide(new BigDecimal(unidades), 2, BigDecimal.ROUND_DOWN);
                      } else if(rateio.equalsIgnoreCase("perm")) {
                          valor = valor.divide(new BigDecimal(permissionarios.size()), 2, BigDecimal.ROUND_DOWN);
                      }
                  } 
                  
                  while (it.hasNext()) {
                    CustomTransferObject permissionario = (CustomTransferObject)it.next();
                    
                    prm_codigo = (String)permissionario.getAttribute(Columns.PRM_CODIGO);
                    rse_codigo = (String)permissionario.getAttribute(Columns.RSE_CODIGO);
                    rse_matricula = (String)permissionario.getAttribute(Columns.RSE_MATRICULA);
                    ser_nome = (String)permissionario.getAttribute(Columns.SER_NOME);
                    posDescricao = !TextHelper.isNull(permissionario.getAttribute(Columns.POS_DESCRICAO)) ? permissionario.getAttribute(Columns.POS_DESCRICAO).toString() : "";
                    trsDescricao = !TextHelper.isNull(permissionario.getAttribute(Columns.TRS_DESCRICAO)) ? permissionario.getAttribute(Columns.TRS_DESCRICAO).toString() : "";
                    srsDescricao = !TextHelper.isNull(permissionario.getAttribute(Columns.SRS_DESCRICAO)) ? permissionario.getAttribute(Columns.SRS_DESCRICAO).toString() : "";
                    echDescricao = (String)permissionario.getAttribute(Columns.ECH_DESCRICAO);
                    cpf = (String)permissionario.getAttribute(Columns.SER_CPF);
                    complemento = (String)permissionario.getAttribute(Columns.PRM_COMPL_ENDERECO);
                    
                    java.sql.Date adeAnoMesIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(permissionario.getAttribute(Columns.ORG_CODIGO).toString(), Integer.parseInt(adeCarencia), null, responsavel);
                    java.sql.Date adeAnoMesFim = !adePrazo.equals("") ? PeriodoHelper.getInstance().calcularAdeAnoMesFim(permissionario.getAttribute(Columns.ORG_CODIGO).toString(), adeAnoMesIni, Integer.parseInt(adePrazo), null, responsavel) : null;

                    dataInicio = (adeAnoMesIni != null ? DateHelper.toPeriodString(adeAnoMesIni) : "");
                    dataFim = (adeAnoMesFim != null ? DateHelper.toPeriodString(adeAnoMesFim) : "");
                %>
                  <tr>
                    <td><%=TextHelper.forHtmlContent(rse_matricula)%></td>
                    <td><%=TextHelper.forHtmlContent(ser_nome)%></td>
                    <td><%=TextHelper.forHtmlContent(cpf)%></td>
                    <td>
                      <%= planoPorDesconto.equalsIgnoreCase("true") ? TextHelper.forHtmlContent(posDescricao) : "" %>
                    </td>
                    <td><%=TextHelper.forHtmlContent(trsDescricao)%></td>
                    <td><%=TextHelper.forHtmlContent(srsDescricao)%></td>
                    <td><%=TextHelper.forHtmlContent(echDescricao)%></td>
                    <td><%=TextHelper.forHtmlContent(complemento)%></td>
                    <td><%=TextHelper.forHtmlContent(labelTipoValor)%> <%=NumberHelper.format(valor.doubleValue(), NumberHelper.getLang())%></td>
                    <td><%=TextHelper.forHtmlContent(dataInicio)%></td>
                    <td><%= labelAdePrazo.equals("") ? ApplicationResourcesHelper.getMessage("rotulo.plano.indeterminado", responsavel) : dataFim %></td>
                  </tr>
                <% } %>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
    <hl:htmlinput type="hidden" name="ECH_CODIGO" value="<%=TextHelper.forHtmlAttribute(echCodigo)%>" />
    <hl:htmlinput type="hidden" name="PLA_CODIGO" value="<%=TextHelper.forHtmlAttribute(plaCodigo)%>" />
    <hl:htmlinput type="hidden" name="POS_CODIGO" value="<%=TextHelper.forHtmlAttribute(posCodigo)%>" />
    <hl:htmlinput type="hidden" name="decData" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "decData"))%>" />
    <hl:htmlinput type="hidden" name="adePrazo" value="<%=TextHelper.forHtmlAttribute(adePrazo)%>" />
    <hl:htmlinput type="hidden" name="adeVlr" value="<%=TextHelper.forHtmlAttribute((valor))%>" />
    <hl:htmlinput type="hidden" name="adeIdentificador" value="<%=TextHelper.forHtmlAttribute(adeIdentificador)%>" />
    <hl:htmlinput type="hidden" name="tipo" value="<%=TextHelper.forHtmlAttribute(tipo)%>" />
  </form>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
    <a class="btn btn-primary" href="#no-back" onclick="if(confirm('<hl:message key="mensagem.confirmacao.insercao"/>')){f0.submit();} return false;"><hl:message key="rotulo.botao.confirmar"/></a>
  </div>
</c:set>

<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
  <script type="text/JavaScript">
    f0 = document.forms[0];
  </script>
</c:set>

<t:page_v4>
	<jsp:attribute name="header">${title}</jsp:attribute>
	<jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
	<jsp:attribute name="javascript">${javascript}</jsp:attribute>
	<jsp:body>${bodyContent}</jsp:body>
</t:page_v4>