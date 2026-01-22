<%--
* <p>Title: listarBeneficio_v4</p>
* <p>Description: Listar benefícios v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: larissa.silva $
* $Revision: 24740 $
* $Date: 2018-06-26 15:48:25 -0300 (Ter, 26 jun 2018) $
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean podeEditar = (boolean) request.getAttribute("podeEditar");

// Pega dados vindo do webController
String codigo = responsavel.getCodigoEntidade();
String filtro = (String) request.getAttribute("filtro");
int filtro_tipo = (int) request.getAttribute("filtro_tipo");
List beneficios = (List) request.getAttribute("beneficios");
%>
<c:set var="title">
  <hl:message key="rotulo.beneficio.lista" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent">
  <% if (podeEditar) { %>
    <div class="row  d-flex flex-row-reverse mr-0">
      <div class="pull-right">
        <div class="btn-action ">
          <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/alterarBeneficio?acao=novo&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message
              key="rotulo.botao.novo.beneficio" /></a>
        </div>
      </div>
    </div>
  <% } %>
  <div class="row">
    <div class="col-sm-6 col-md-4">
      <form name="form1" method="POST" action="${linkAction}">
        <div class="card">
          <div class="card-header hasIcon pl-3">
            <h3 class="card-header-title">
              <hl:message key="rotulo.botao.pesquisar" />
            </h3>
          </div>
          <div class="card-body">
            <div class="row">
              <div class="form-group col-sm">
                <label for="FILTRO"><hl:message
                    key="rotulo.filtro.correspondente" /></label> <INPUT
                  class="EditMinusculo form-control" type="text"
                  name="FILTRO"
                  value="<%=TextHelper.forHtmlAttribute(filtro)%>"
                  onFocus="SetarEventoMascara(this,'#*200',true);"
                  onBlur="fout(this);ValidaMascara(this);">&nbsp;
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <label for="FILTRO_TIPO"><hl:message
                    key="rotulo.acao.filtrar.por" /></label> <select
                  name="FILTRO_TIPO" class="Select form-select form-control"
                  onFocus="SetarEventoMascara(this,'#*200',true);"
                  onBlur="fout(this);ValidaMascara(this);" nf="Filtrar">
                  <option value=""
                    <%=(String) ((filtro_tipo == -1) ? "SELECTED" : "")%>>
                    <hl:message key="rotulo.campo.sem.filtro" />
                  </option>
                  <option value="02"
                    <%=(String) ((filtro_tipo == 2) ? "SELECTED" : "")%>>
                    <hl:message
                      key="rotulo.beneficio.operadora.singular" />
                  </option>
                  <option value="03"
                    <%=(String) ((filtro_tipo == 3) ? "SELECTED" : "")%>>
                    <hl:message key="rotulo.beneficio.natureza" />
                  </option>
                  <option value="04"
                    <%=(String) ((filtro_tipo == 4) ? "SELECTED" : "")%>>
                    <hl:message key="rotulo.beneficio.descricao" />
                  </option>
                  <option value="05"
                    <%=(String) ((filtro_tipo == 5) ? "SELECTED" : "")%>>
                    <hl:message key="rotulo.beneficio.codigo.plano" />
                  </option>
                  <option value="06"
                    <%=(String) ((filtro_tipo == 6) ? "SELECTED" : "")%>>
                    <hl:message key="rotulo.beneficio.codigo.registro" />
                  </option>
                  <option value="07"
                    <%=(String) ((filtro_tipo == 7) ? "SELECTED" : "")%>>
                    <hl:message key="rotulo.beneficio.codigo.contrato" />
                  </option>
                </select>
              </div>
            </div>
          </div>
        </div>
        <div class="btn-action d-print-none">
          <a class="btn btn-primary" href="#no-back" name="Filtrar"
            id="Filtrar" onClick="filtrar()"> <svg width="20">
              <use xlink:href="#i-consultar"></use></svg> <hl:message
              key="rotulo.botao.pesquisar" />
          </a>
        </div>
      </form>
    </div>
    <div class="col-sm-7 col-md-8">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title">
            <hl:message key="rotulo.beneficio.lista" />
          </h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.beneficio.operadora.singular"/></th>
                <th scope="col"><hl:message key="rotulo.beneficio.natureza"/></th>
                <th scope="col"><hl:message key="rotulo.beneficio.descricao"/></th>
                <th scope="col"><hl:message key="rotulo.beneficio.codigo.plano"/></th>
                <th scope="col"><hl:message key="rotulo.beneficio.codigo.registro"/></th>
                <th scope="col"><hl:message key="rotulo.beneficio.codigo.contrato"/></th>
                <th scope="col"><hl:message key="rotulo.situacao"/></th>
                <th scope="col"><hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>
              <%=JspHelper.msgRstVazio(beneficios.size() == 0, 13, responsavel)%>
              <%
              	Iterator it = beneficios.iterator();
              		while (it.hasNext()) {
              			CustomTransferObject beneficio = (CustomTransferObject) it.next();
              			String csa_nome = (String) beneficio.getAttribute(Columns.CSA_NOME);
              			String ben_codigo = (String) beneficio.getAttribute(Columns.BEN_CODIGO);
              			String nca_descricao = (String) beneficio.getAttribute(Columns.NSE_DESCRICAO);
              			String ben_descricao = (String) beneficio.getAttribute(Columns.BEN_DESCRICAO);
              			String ben_codPLano = (String) beneficio.getAttribute(Columns.BEN_CODIGO_PLANO);
              			String ben_codRegistro = (String) beneficio.getAttribute(Columns.BEN_CODIGO_REGISTRO);
              			String ben_codContrato = (String) beneficio.getAttribute(Columns.BEN_CODIGO_CONTRATO);
              			String ben_ativo = beneficio.getAttribute(Columns.BEN_ATIVO) != null ? beneficio.getAttribute(Columns.BEN_ATIVO).toString() : "1";
              %>
              <tr>
                <td><%=TextHelper.forHtmlContent(csa_nome.toUpperCase())%></td>
                <td><%=TextHelper.forHtmlContent(nca_descricao)%></td>
                <td><%=TextHelper.forHtmlContent(ben_descricao)%></td>
                <td><%=TextHelper.forHtmlContent(ben_codPLano)%></td>
                <td><%=TextHelper.forHtmlContent(ben_codRegistro)%></td>
                <td><%=TextHelper.forHtmlContent(ben_codContrato)%></td>
                <% String msgServicoBloqueadoDesbloqueado = ben_ativo.equals("1") ? ApplicationResourcesHelper.getMessage("rotulo.beneficio.status.desbloqueado", responsavel): ApplicationResourcesHelper.getMessage("rotulo.beneficio.status.bloqueado", responsavel); %>
                <td <%=ben_ativo.equals("0") ? "class=\"block\"" : ""%>><%=TextHelper.forHtmlAttribute(msgServicoBloqueadoDesbloqueado)%></td>
                <% if (podeEditar) { %>
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
                            aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"> <svg>
                              <use
                                xmlns:xlink="http://www.w3.org/1999/xlink"
                                xlink:href="#i-engrenagem"></use></svg>
                          </span>
                          <hl:message
                            key="rotulo.acoes.lst.arq.generico.opcoes" />
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right"
                        aria-labelledby="userMenu">
                        <a class="dropdown-item" href="#no-back"
                          onClick="postData('../v3/alterarBeneficio?acao=editar&ben_codigo=<%=TextHelper.forJavaScript(ben_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                          <hl:message key="rotulo.acoes.editar" />
                        </a> <a class="dropdown-item" href="#no-back"
                          onClick="confirmarExclusao('../v3/alterarBeneficio?acao=excluir&ben_codigo=<%=TextHelper.forJavaScript(ben_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');">
                          <hl:message key="rotulo.acoes.excluir" />
                        </a>
                        <a class="dropdown-item" href="#no-back" onClick="BloquearEntidade(<%=TextHelper.forJavaScript(ben_ativo)%>, '<%=TextHelper.forJavaScript(ben_ativo)%>', 'EST', '../v3/listarBeneficio?acao=bloquear&ben_codigo=<%=TextHelper.forJavaScript(ben_codigo)%>&csa_nome=<%=TextHelper.forJavaScript(csa_nome)%>&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(ben_descricao)%>')"><hl:message key="rotulo.acao.bloquear.desbloquear"/></a>                                     
                      </div>
                    </div>
                  </div>
                </td>
                <% } else { %>
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
                            aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"> <svg>
                              <use
                                xmlns:xlink="http://www.w3.org/1999/xlink"
                                xlink:href="#i-engrenagem"></use></svg>
                          </span>
                          <hl:message
                            key="rotulo.acoes.lst.arq.generico.opcoes" />
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right"
                        aria-labelledby="userMenu">
                        <a class="dropdown-item" href="#no-back"
                          onClick="postData('../v3/listarBeneficio?acao=visualizar&ben_codigo=<%=TextHelper.forJavaScript(ben_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                          <hl:message key="rotulo.acoes.detalhar" />
                        </a>
                      </div>
                    </div>
                  </div>
                </td>
                <% } %>
              </tr>
              <% } %>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.beneficios", responsavel) + " - "%>
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
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
	function confirmarExclusao(link) {
		if (confirm('<hl:message key="mensagem.confirmacao.exclusao.beneficio"/>')) {
			postData(link);
		}
		return false;
	}

	f0 = document.forms[0];

	function filtrar() {
		f0.submit();
	}
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>