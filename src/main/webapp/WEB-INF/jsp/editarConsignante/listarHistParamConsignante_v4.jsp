<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.entidade.OcorrenciaParamSistCseTO"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
String filtro = (String) request.getAttribute("filtro");
int filtro_tipo = (int) request.getAttribute("filtro_tipo");
LinkedList<OcorrenciaParamSistCseTO> histParamCse = (LinkedList<OcorrenciaParamSistCseTO>) request.getAttribute("histParamCse");
%>
<c:set var="title">
  <hl:message key="rotulo.historico.parametro.consignante.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-5 col-md-4">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title">
            <hl:message key="rotulo.acao.pesquisar" />
          </h2>
        </div>
        <div class="card-body">
          <form NAME="form1" METHOD="post"
            ACTION="../v3/manterParamConsignante?acao=listarHistoricoParametro&<%=SynchronizerToken.generateToken4URL(request)%>">
            <div class="row">
              <div class="form-group col-sm">
                <label for="iFiltro"><hl:message
                    key="rotulo.usuario.lista.filtro" /></label> <input
                  placeholder="<hl:message key="rotulo.acao.digite.filtro"/>"
                  TYPE="text" NAME="FILTRO" class="form-control"
                  id="iFiltro" SIZE="10"
                  VALUE="<%=TextHelper.forHtmlAttribute(filtro)%>"
                  onFocus="SetarEventoMascara(this,'#*200',true);"
                  onBlur="fout(this);ValidaMascara(this);">
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <label for="iFiltrarPor"><hl:message
                    key="rotulo.acao.filtrar.por" /></label>
                <select NAME="FILTRO_TIPO" class="form-control select form-select"
                  id="iFiltrarPor"
                  onFocus="SetarEventoMascara(this,'#*200',true);"
                  onBlur="fout(this);ValidaMascara(this);">
                  <optgroup label="<%=ApplicationResourcesHelper.getMessage("rotulo.filtro.plural", responsavel)%>:">
                    <option value="" <%=(String) ((filtro_tipo == -1) ? "SELECTED" : "")%>><hl:message  key="rotulo.campo.sem.filtro" /></OPTION>
                    <option value="02"  <%=(String) ((filtro_tipo == 2) ? "SELECTED" : "")%>><hl:message key="rotulo.historico.alteracao.parametro.descricao" /></OPTION>
                    <option value="03" <%=(String) ((filtro_tipo == 3) ? "SELECTED" : "")%>><hl:message key="rotulo.historico.alteracao.parametro.usuario" /></OPTION>
                    <option value="00" <%=(String) ((filtro_tipo == 0) ? "SELECTED" : "")%>><hl:message key="rotulo.historico.alteracao.parametro.codigo" /></OPTION>
                  </optgroup>
                </select>
              </div>
            </div>
          </form>
        </div>
      </div>
      <div class="btn-action">
        <a class="btn btn-primary" href="#" onClick="f0.submit(); return false;"> <svg width="20"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-consultar"></use></svg> <hl:message key="rotulo.acao.pesquisar" /></a>
      </div>
    </div>
    <div class="col-sm-7 col-md-8">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title">
            <hl:message
              key="rotulo.historico.parametro.consignante.titulo" />
          </h2>
        </div>
        <div class="card-body table-responsive">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th><hl:message key="rotulo.historico.alteracao.parametro.data" /></th>
                <th><hl:message key="rotulo.historico.alteracao.parametro.usuario" /></th>
                <th><hl:message key="rotulo.historico.alteracao.parametro.codigo" /></th>
                <th><hl:message key="rotulo.historico.alteracao.parametro.descricao" /></th>
                <th><hl:message key="rotulo.historico.alteracao.parametro.observacao" /></th>
                <th><hl:message key="rotulo.historico.alteracao.parametro.ipacesso" /></th>
              </tr>
              <%
                  String usuLogin, tpcCodigo, tpcDescricao, opsData, opsCodigo, opsObs, opsIpAcesso;
                      Iterator<OcorrenciaParamSistCseTO> it = histParamCse.iterator();
                      while (it.hasNext()) {
                          OcorrenciaParamSistCseTO next = (OcorrenciaParamSistCseTO) it.next();
                          usuLogin = next.getUsuLogin();
                          tpcCodigo = next.getTpcCodigo();
                          tpcDescricao = next.getTpcDescricao();
                          opsData = DateHelper.reformat(next.getOpsData().toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
                          opsCodigo = next.getOpsCodigo();
                          opsObs = next.getOpsObs();
                          opsIpAcesso = next.getOpsIpAcesso() != null ? next.getOpsIpAcesso() : "";
              %>
            </thead>
            <tbody>
              <tr>
                <td align="center"><%=opsData%></td>
                <td><%=TextHelper.forHtmlContent(usuLogin)%></td>
                <td><%=TextHelper.forHtmlContent(tpcCodigo)%></td>
                <td><%=TextHelper.forHtmlContent(tpcDescricao)%></td>
                <td><%=TextHelper.forHtmlContentComTags(opsObs)%></td>
                <td><%=TextHelper.forHtmlContent(opsIpAcesso)%></td>
              </tr>
              <%
                  }
              %>
            </tbody>
            <tfoot>
                <tr>
                  <td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.historico.disponivel", responsavel) + " - " %><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td>
                </tr>
              </tfoot>
          </table>
            <div class="card-footer">
              <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
            </div>
        </div>

      </div>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back"
      onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message
        key="rotulo.botao.voltar" /></a>
  </div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
	var f0 = document.forms[0];
</script>
<script type="text/JavaScript"  src="../js/scripts_2810.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript"  src="../js/usuario.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript"  src="../js/xbdhtml.js?<hl:message key="release.tag"/>"></script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>