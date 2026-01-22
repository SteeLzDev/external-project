<%--
* <p>Title: simulacaoAlteracaoBeneficiosSelecionarBeneficio_v4</p>
* <p>Description: Simular Beneficios v4</p>
* <p>Copyright: Copyright (c) 2018</p>
* <p>Company: Nostrum Consultoria e Projetos</p>
* $Author$
* $Revision$
* $Date$
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session"
  class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  String rseCodigo = (String) request.getAttribute("rseCodigo");
  List<TransferObject> naturezasServico = (List) request.getAttribute("naturezasServico");
  boolean temContratoEmAndamento = (boolean) request.getAttribute("temContratoEmAndamento");
%>
<c:set var="title">
  <hl:message key="rotulo.simulacao.alteracao.beneficio.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent">

    <div class="modal spinner-one" id="modalSimularBeneficio" tabindex="-1" role="dialog" data-bs-backdrop="static" data-bs-keyboard="false" aria-labelledby="modalSimularBeneficioLabel" aria-hidden="true">
        <div class="modal-dialog spinner-one modal-dialog-centered">
            <div class="modal-content spinner-two">
                <div class="modal-body spinner-three" >
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden"></span>
                    </div>
                </div>
            </div>
        </div>
    </div>
  
  <% if (!temContratoEmAndamento) { %>
  <div class="alert alert-warning" role="alert">
    <hl:message key="mensagem.warning.simulacao.alteracao.nao.existe.contratos.em.andamento" />
  </div>
  <% } %>

  <div class="row">
      <div class="col-sm">
          <div class="card text-center">
              <div class="card-header">
                  <h2 class="card-header-title">
                   Selecione o tipo de beneficios desejado
                </h2>
              </div>
              <div class="card-body table-responsive">
                  <div class="mx-auto w-75">
                    <%
                      for (TransferObject ctoNaturezaServico : naturezasServico) {
                          String nseCodigo = ctoNaturezaServico.getAttribute(Columns.NSE_CODIGO).toString();
                          String nseDescricao = ctoNaturezaServico.getAttribute(Columns.NSE_DESCRICAO).toString();
                          boolean ativo = (boolean) ctoNaturezaServico.getAttribute("ativo");
                    %>
                    <button type="button" class="btn btn-primary btn-lg btn-block" id="<%=TextHelper.forHtmlAttribute(nseCodigo)%>" onClick="iniciarSimulacao(this.id)" <%= ativo ? "" : "disabled"%>>
                      <%=TextHelper.forHtmlContent(TextHelper.capitailizeFirstLetter(nseDescricao))%>
                    </button>
                    <%
                      }
                    %>
                  </div>
              </div>
          </div>
      </div>
  </div>

  <div class="btn-action col-sm">
    <a class="btn btn-primary" href="#no-back" onClick="fluxoAddBeneficiario()"><hl:message key="rotulo.botao.novo.beneficiario" /></a>
    <a class="btn btn-outline-danger" href="#no-back" onClick="fluxoVoltar()"><hl:message key="rotulo.botao.voltar" /></a>
  </div>
</c:set>
<c:set var="javascript">
  <script>
  
  function iniciarSimulacao(nseCodigo) {
	  postData('../v3/simulacaoAlteracaoBeneficios?acao=simular&rseCodigo=<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>&nseCodigo='+nseCodigo);  
  }
  
  function fluxoAddBeneficiario() { 
    postData('../v3/listarBeneficiarios?acao=listar&RSE_CODIGO=<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>')
  }
  
  function fluxoVoltar() { 
    <%
    if (responsavel.isSer()) { 
    %>
      postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>');
    <%
    } else { 
    %>
      postData('../v3/simulacaoAlteracaoBeneficios?acao=iniciar');
    <%
    } 
    %>
  }
   
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>