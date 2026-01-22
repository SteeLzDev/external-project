<%--
* <p>Title: listarColaboradoresRescisao_v4</p>
* <p>Description: Listar colaboradores incluídos para rescisão contratual v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.values.StatusVerbaRescisoriaEnum"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

// Pega dados vindo do webController
String tituloPagina = (String) request.getAttribute("tituloPagina");
List<TransferObject> listaRseRescisao = (List<TransferObject>) request.getAttribute("listaRseRescisao");
boolean exibeProcessados = (Boolean) request.getAttribute("exibeProcessados");

%>
<c:set var="title">
   ${tituloPagina}
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-rescisao"></use>
</c:set>

<c:set var="bodyContent">
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title">
            ${tituloPagina}
        </h2>
      </div>
      <div class="card-body table-responsive p-0">
      	<label class="exibeHistOculto ms-2">
      		<input name="EXIBIR_OCULTOS" type="checkbox" onclick="if (this.checked) {doIt('eho')} else {doIt('oho')};" style="vertical-align: middle; margin: 1px;" <%=exibeProcessados ? "checked" : "" %>>
     			<hl:message key="rotulo.listar.colaborador.verba.rescisoria.exibir.processado"/>
   			</label>
     		<table id="dataTables" class="table table-striped table-hover w-100">
          <thead>
            <tr>
              <th scope="col"><hl:message key="rotulo.matricula.singular"/></th>
              <th scope="col"><hl:message key="rotulo.servidor.nome"/></th>
              <th scope="col"><hl:message key="rotulo.servidor.cpf"/></th>
              <th scope="col"><hl:message key="rotulo.orgao.singular"/></th>
              <th scope="col"><hl:message key="rotulo.listar.colaborador.verba.rescisoria.data.inicio"/></th>
              <th scope="col"><hl:message key="rotulo.listar.colaborador.verba.rescisoria.status"/></th>
              <th scope="col"><hl:message key="rotulo.listar.colaborador.verba.rescisoria.processado"/></th>
              <th scope="col"><hl:message key="rotulo.acoes"/></th>
            </tr>
          </thead>
          <tbody>
            <%=JspHelper.msgRstVazio(listaRseRescisao.size() == 0, 7, responsavel)%>
            <%
            Iterator<TransferObject> it = listaRseRescisao.iterator();
            while (it.hasNext()) {
              CustomTransferObject registroServidor = (CustomTransferObject) it.next();
              String processado = registroServidor.getAttribute(Columns.VRR_PROCESSADO).equals("S") ? "Sim" : "Não";
            	if (!exibeProcessados && processado.equals("Sim")) {
            		continue;
            	}
              String vrrCodigo = (String) registroServidor.getAttribute(Columns.VRR_CODIGO);
              String rseCodigo = (String) registroServidor.getAttribute(Columns.RSE_CODIGO);
              String rseMatricula = (String) registroServidor.getAttribute(Columns.RSE_MATRICULA);
              String serNome = (String) registroServidor.getAttribute(Columns.SER_NOME);
              String serCpf = (String) registroServidor.getAttribute(Columns.SER_CPF);
              String orgNome = (String) registroServidor.getAttribute(Columns.ORG_NOME);
              String orgIdentificador = (String) registroServidor.getAttribute(Columns.ORG_IDENTIFICADOR);
              String rseDataInicio = "";
              if (registroServidor.getAttribute(Columns.VRR_DATA_INI) != null) {
                  rseDataInicio = DateHelper.toDateString((Date) registroServidor.getAttribute(Columns.VRR_DATA_INI));
              }
              String status = (String) registroServidor.getAttribute(Columns.SVR_DESCRICAO);
              String svrCodigo = (String) registroServidor.getAttribute(Columns.SVR_CODIGO);
              boolean podeReterVerba = (boolean) registroServidor.getAttribute("PODE_RETER_VERBA");
              boolean podeEnviarEmailServidor = (boolean) registroServidor.getAttribute("PODE_ENVIAR_EMAIL_SER");
            %>
            <tr>
              <td><%=TextHelper.forHtmlContent(rseMatricula)%></td>
              <td><%=TextHelper.forHtmlContent(serNome)%></td>
              <td><%=TextHelper.forHtmlContent(serCpf)%></td>
              <td><%=TextHelper.forHtmlContent(orgNome + " - " + orgIdentificador)%></td>
              <td><%=TextHelper.forHtmlContent(rseDataInicio)%></td>
              <td><%=TextHelper.forHtmlContent(status)%></td>
              <td><%=TextHelper.forHtmlContent(processado)%></td>
              <td>
                <div class="actions">
                  <div class="dropdown">
                    <a class="dropdown-toggle ico-action" href="#"
                      role="button" id="userMenu"
                      data-bs-toggle="dropdown" aria-haspopup="true"
                      aria-expanded="false">
                      <div class="form-inline">
                        <span class="mr-1" data-bs-toggle="tooltip"
                          title="" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"
                          aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"> 
                          <svg><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                        </span>
                        <hl:message key="rotulo.botao.opcoes" />
                      </div>
                    </a>
                    <div class="dropdown-menu dropdown-menu-right"
                      aria-labelledby="userMenu">
                      <% if (podeReterVerba) { %>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarVerbaRescisoria?acao=iniciar&VRR_CODIGO=<%=TextHelper.forJavaScriptAttribute(vrrCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');">
                            <hl:message key="rotulo.acoes.informar.verba.rescisoria" />
                        </a>
                      <% } else if (StatusVerbaRescisoriaEnum.CONCLUIDO.getCodigo().equals(svrCodigo)) { %>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarVerbaRescisoria?acao=visualizar&vrrCodigo=<%=TextHelper.forJavaScriptAttribute(vrrCodigo)%>&rseCodigo=<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');">
                            <hl:message key="rotulo.acoes.visualizar" />
                        </a>
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarVerbaRescisoria?acao=visualizarComunicado&vrrCodigo=<%=TextHelper.forJavaScriptAttribute(vrrCodigo)%>&rseCodigo=<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');">
                            <hl:message key="rotulo.acoes.imprimir.comunicado.rescisao" />
                        </a>
                        <%if(ParamSist.getBoolParamSist(CodedValues.TPC_ENVIA_EMAIL_SER_AUT_PENDENTE_SALDO_INSUF_VERBA_RESCISORIA, responsavel) && podeEnviarEmailServidor) {%>
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarColaboradoresVerbaRescisoria?acao=enviaEmailSer&rseCodigo=<%=TextHelper.forJavaScriptAttribute(rseCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');">
                              <hl:message key="rotulo.verba.rescisoria.opcao.enviar.email.ser" />
                          </a>
                        <%} %>
                      <% } else { %>
                        <span class="dropdown-item">
                          <hl:message key="rotulo.acoes.nao.disponivel"/>
                        </span>
                      <% } %>
                    </div>
                  </div>
                </div>
              </td>
            </tr>
            <% } %>
          </tbody>
          <tfoot>
            <tr>
              <td colspan="5"><%=ApplicationResourcesHelper.getMessage("rotulo.listagem.colaborador.rescisao", responsavel) + " - "%>
                <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span>
              </td>
            </tr>
          </tfoot>
        </table>
      </div>
      <div class="card-footer">
        <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
      </div>
    </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
    f0 = document.forms[0];
    
    function doIt(opt, ade) {
           switch (opt) {
                case 'eho':
                    j   = '../v3/listarColaboradoresVerbaRescisoria?acao=iniciar';
                    qs = '&oculto=true';
                    break;
                case 'oho':
                    j   = '../v3/listarColaboradoresVerbaRescisoria?acao=iniciar';
                    qs = '&oculto=false';
                    break;
               } 
       
           qs = qs + '&<%out.print(SynchronizerToken.generateToken4URL(request));%>';
        
          postData(j + qs);
       }
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>