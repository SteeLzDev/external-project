<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

List<TransferObject> lstResultado = (List<TransferObject>) request.getAttribute("lstResultado");
%>
<link rel="stylesheet" href="../css/impromptu.css?<hl:message key="release.tag"/>" type="text/css">
<c:set var="imageHeader">
    <use xlink:href="#i-mensagem"></use>
</c:set>
<c:set var="title">
   <hl:message key="rotulo.acompanhamento.leilao.solicitacao.titulo"/>
</c:set>
<c:set var="bodyContent">
<%if(!responsavel.isSer()) {%>             
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.filtro.leilao.solicitacao.pesquisa"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.filtro.leilao.solicitacao.descricao"/></th>
            <th scope="col"><hl:message key="rotulo.filtro.leilao.solicitacao.qtde.propostas"/></th>
            <th scope="col"><hl:message key="rotulo.filtro.leilao.solicitacao.qtde.propostas.informadas"/></th>
            <th scope="col"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody>
          <%=JspHelper.msgRstVazio(lstResultado.isEmpty(), "5", "lp")%>
          <% 
          String flsCodigo, descricao, filtro, qtde1, qtde2, data;
          String risco, pontuacao, dataIni, dataFim, horas, matricula, cpf, cidadeCod, cidadeNome, posto, margemLivreMax;
          TransferObject resultado = null;
          int rowCount = 0;
          Iterator<TransferObject> it = lstResultado.iterator();
          while (it.hasNext()) {
            resultado = (TransferObject) it.next();
          
            flsCodigo = resultado.getAttribute(Columns.FLS_CODIGO).toString();
            filtro = resultado.getAttribute(Columns.FLS_TIPO_PESQUISA) != null ? resultado.getAttribute(Columns.FLS_TIPO_PESQUISA).toString() : "0";
            descricao = resultado.getAttribute(Columns.FLS_DESCRICAO).toString();
            risco = resultado.getAttribute(Columns.FLS_ANALISE_RISCO) != null ? resultado.getAttribute(Columns.FLS_ANALISE_RISCO).toString() : "";
            pontuacao = resultado.getAttribute(Columns.FLS_PONTUACAO_MIN) != null ? resultado.getAttribute(Columns.FLS_PONTUACAO_MIN).toString() : "";
            dataIni = resultado.getAttribute(Columns.FLS_DATA_ABERTURA_INI) != null ? DateHelper.format((Date)resultado.getAttribute(Columns.FLS_DATA_ABERTURA_INI), LocaleHelper.getDatePattern()) : "";
            dataFim = resultado.getAttribute(Columns.FLS_DATA_ABERTURA_FIM) != null ? DateHelper.format((Date)resultado.getAttribute(Columns.FLS_DATA_ABERTURA_FIM), LocaleHelper.getDatePattern()) : "";
            horas = resultado.getAttribute(Columns.FLS_HORAS_ENCERRAMENTO) != null ?resultado.getAttribute(Columns.FLS_HORAS_ENCERRAMENTO).toString() : "";
            matricula = resultado.getAttribute(Columns.FLS_MATRICULA) != null ? (String) resultado.getAttribute(Columns.FLS_MATRICULA) : "";
            cpf = resultado.getAttribute(Columns.FLS_CPF) != null ? (String) resultado.getAttribute(Columns.FLS_CPF) : "";
            cidadeCod = resultado.getAttribute(Columns.FLS_CID_CODIGO) != null ? (String) resultado.getAttribute(Columns.FLS_CID_CODIGO) : "";
            cidadeNome = resultado.getAttribute(Columns.CID_NOME) != null ? (String) resultado.getAttribute(Columns.CID_NOME) : "";
            posto = resultado.getAttribute(Columns.FLS_POS_CODIGO) != null ? (String) resultado.getAttribute(Columns.FLS_POS_CODIGO) : "";
            margemLivreMax = resultado.getAttribute(Columns.FLS_MARGEM_LIVRE_MAX) != null ? resultado.getAttribute(Columns.FLS_MARGEM_LIVRE_MAX).toString() : "";
            qtde1 = resultado.getAttribute("QTDE_0").toString();
            qtde2 = resultado.getAttribute("QTDE_1").toString();
          %>
          <tr>
            <td><%=TextHelper.forHtmlContent(descricao)%></td>
            <td><%=TextHelper.forHtmlContent(qtde1)%></td>
            <td><%=TextHelper.forHtmlContent(qtde2)%></td>
            <td>
              <div class="actions">
                <div class="dropdown">
                  <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <div class="form-inline">
                      <span class="mr-1" data-bs-toggle="tooltip" aria-label="Opções" title="" data-original-title="Opções"><svg>
                      <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg></span><hl:message key="rotulo.botao.opcoes"/>
                    </div>
                  </a>
                  <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                    <a class="dropdown-item" href="#no-back" onClick="doIt('p', '<%=TextHelper.forJavaScript(flsCodigo)%>','<%=TextHelper.forJavaScript(filtro)%>','<%=TextHelper.forJavaScript(risco)%>','<%=TextHelper.forJavaScript(pontuacao)%>','<%=TextHelper.forJavaScript(dataIni)%>','<%=TextHelper.forJavaScript(dataFim)%>','<%=TextHelper.forJavaScript(horas)%>','<%=TextHelper.forJavaScript(matricula)%>','<%=TextHelper.forJavaScript(cpf)%>','<%=TextHelper.forJavaScript(cidadeCod)%>','<%=TextHelper.forJavaScript(cidadeNome)%>','<%=TextHelper.forJavaScript(posto)%>', '<%=TextHelper.forJavaScript(margemLivreMax)%>');">Consultar</a>
                    <a class="dropdown-item" href="#no-back" onClick="doIt('e', '<%=TextHelper.forJavaScript(flsCodigo)%>');"><hl:message key="rotulo.acoes.remover"/></a> 
                  </div>
                </div>
              </div>
            </td>
          </tr>
          <%                
          }
          %>
        </tbody>
      </table>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/acompanharLeilao?acao=iniciar')"><hl:message key="rotulo.botao.cancelar"/></a>
  </div>
<%} %>      
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
  function doIt(opt, codigo, filtro, risco, pontuacao, dataIni, dataFim, horas, matricula, cpf, cidadeCod, cidadeNome, posto, margemLivreMax) {
      var qs = '&codigo=' + codigo + '&<%SynchronizerToken.saveToken(request);out.print(SynchronizerToken.generateToken4URL(request));%>';
      var msg = '';
      var link = '';
      switch (opt) {
        case 'e':
            link = '../v3/acompanharLeilao?acao=excluirFiltro&<%=SynchronizerToken.generateToken4URL(request)%>';
            msg = '<hl:message key="mensagem.confirmacao.exclusao.filtro"/>';
            break;
        case 'p':
            link = '../v3/acompanharLeilao?acao=pesquisar&filtro=' + filtro + '&ARR_RISCO=' + risco + '&RSE_PONTUACAO=' + pontuacao + '&dataAberturaIni=' + dataIni + '&dataAberturaFim=' + dataFim + '&horasFimLeilao=' + horas + '&RSE_MATRICULA=' + matricula + '&SER_CPF=' + cpf + '&CID_CODIGO=' + cidadeCod + '&CID_NOME=' + cidadeNome +'&<%=FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR%>=' + posto  + '&RSE_MARGEM_LIVRE=' + margemLivreMax;
            break;
        default:
            return false;
            break;  
      } 
  
      // filtro, ARR_RISCO, RSE_PONTUACAO, dataAberturaIni, dataAberturaFim, horasFimLeilao, RSE_MATRICULA, SER_CPF, CID_CODIGO, FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR
  
      if (msg == "" || confirm(msg)) {
        postData(link + qs);
      }
    }
  
  </script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>