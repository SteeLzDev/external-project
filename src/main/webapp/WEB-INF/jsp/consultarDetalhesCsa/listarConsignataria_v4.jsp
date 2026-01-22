<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.dto.web.DadosConsignataria"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.values.NaturezaConsignatariaEnum"%>
<%@ page import="com.zetra.econsig.values.StatusConsignatariaEnum"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="show" uri="/showfield-lib" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean podeConsultarCsa = (Boolean) request.getAttribute("podeConsultarCsa");

int filtroTipo = (int) request.getAttribute("filtroTipo");

List consignatarias = (List) request.getAttribute("consignatarias");
String filtro = (String) request.getAttribute("filtro");
String filtroAlfabeto = (String) request.getAttribute("filtroAlfabeto");
List lstNatureza = (List) request.getAttribute("lstNatureza");
String ncaCodigoSelecionado = (String) request.getAttribute("ncaCodigoSelecionado") != null ? (String) request.getAttribute("ncaCodigoSelecionado") : "";
%>
<c:set var="title">
  <hl:message key="rotulo.listar.consignataria.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <!-- Pesquisar -->
    <div class="col-sm-5 col-md-4">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title">
            <hl:message key="rotulo.acao.pesquisar" />
          </h2>
        </div>
        <div class="card-body">
          <FORM NAME="form1" METHOD="post" ACTION="../v3/consultarDetalhesCsa?acao=listar">
            <div class="row">
              <div class="form-group col-sm">
                <label for="iFiltro"><hl:message
                    key="rotulo.listar.consignataria.filtro" /></label> <input
                  type="text" class="form-control" id="FILTRO"
                  name="FILTRO"
                  placeholder="<hl:message key='rotulo.acao.digite.filtro' />"
                  SIZE="10"
                  VALUE="<%=filtroTipo != 4 ? TextHelper.forHtmlAttribute(filtro) : ""%>"
                  onFocus="SetarEventoMascara(this,'#*200',true);"
                  onBlur="fout(this);ValidaMascara(this);"
                  <%=filtroTipo != 4 ? "" : "disabled" %>>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <label for="filtroTipo"><hl:message key="rotulo.acao.filtrar.por" /></label> 
                    <select class="form-control select" id="filtroTipo" name="filtroTipo" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);" onchange="validaFiltroTipo()">
                  <optgroup label="<hl:message key='rotulo.usuario.lista.filtros'/>">
                    <OPTION VALUE=""  <%=(String) ((filtroTipo == -1) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.sem.filtro" /></OPTION>
                    <OPTION VALUE="02" <%=(String) ((filtroTipo == 2) ? "SELECTED" : "")%>><hl:message key="rotulo.consignataria.codigo" /></OPTION>
                    <OPTION VALUE="03" <%=(String) ((filtroTipo == 3) ? "SELECTED" : "")%>><hl:message key="rotulo.consignataria.nome" /></OPTION>
                        <% if (!lstNatureza.isEmpty()) { %>
                          <OPTION VALUE="04" <%=(String)((filtroTipo ==  4) ? "SELECTED" : "")%>><hl:message key="rotulo.consignataria.natureza"/></OPTION>
                        <% } %>
                  </optgroup>
                </select>
              </div>
            </div>
              <%
                  boolean habilitaModuloSdp = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_SDP, CodedValues.TPC_SIM, responsavel);
                  CustomTransferObject ctoNaturezaConsignataria = new CustomTransferObject();
                  // Pega codigo da consignatária
                  String ncaCodigo, ncaDescricao = null;
              %>
                  <div class="row" id="rowNcaCodigo" style="display: <%=filtroTipo == 4 ? "" : "none" %>">
                     <div class="form-group col-sm">
                       <label for="NCA_CODIGO"><hl:message key="rotulo.consignataria.natureza"/></label>
                       <select class="form-control" id="NCA_CODIGO" name="NCA_CODIGO">
                          <OPTION VALUE=""><hl:message key="rotulo.campo.selecione"/></OPTION>
                          <%
                             Iterator itNaturezaConsignataria = lstNatureza.iterator();
                             while(itNaturezaConsignataria.hasNext()){
                               ctoNaturezaConsignataria = (CustomTransferObject)itNaturezaConsignataria.next();
                               ncaCodigo = ctoNaturezaConsignataria.getAttribute(Columns.NCA_CODIGO).toString();
                               ncaDescricao  = ctoNaturezaConsignataria.getAttribute(Columns.NCA_DESCRICAO).toString();
                               if (!habilitaModuloSdp && ncaCodigo.equals(NaturezaConsignatariaEnum.PREFEITURA_AERONAUTICA.getCodigo())) {
                                  // Só exibe opção de natureza de Prefeitura de AER se tiver módulo de SDP habilitado
                                  continue;
                               } 
                          %>
                             <OPTION VALUE="<%=TextHelper.forHtmlAttribute(ncaCodigo)%>" <%=(String)(ncaCodigo.equals(ncaCodigoSelecionado) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(ncaDescricao)%></OPTION>
                          <% } %>
                       </select>
                     </div>
                 </div>
          </form>
        </div>
      </div>
      <div class="btn-action">
        <a class="btn btn-primary" href="#no-back" onClick="form1.submit(); return false;">
        <svg width="20"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-consultar"></use></svg>
          <hl:message key="rotulo.acao.pesquisar" />
        </a>
      </div>
    </div>
    <!-- Consignatarias -->
    <div class="col-sm-7 col-md-8">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title">
            <hl:message key="rotulo.consignataria.plural" />
          </h2>
        </div>
        <div class="card-body">
          <div class="row pl-3 pr-3 pt-2 pb-0">
            <div class="col-sm-12">
              <div class="form-group mb-1">
                <span><hl:message
                    key="rotulo.filtrar.inicial.nome" /></span>
              </div>
            </div>
            <div class="col-sm-12">
              <%
                  for (int i = 'A'; i <= 'Z'; i++) {
                          out.print((filtroAlfabeto.equals(String.valueOf((char) i)) ? String.valueOf((char) i) : "<a href=\"#no-back\" onClick=\"postData('../v3/consultarDetalhesCsa?acao=listar&filtroAlfabeto=" + String.valueOf((char) i) + "')\">" + String.valueOf((char) i) + "</a>") + " - ");
                      }
                      out.print(filtroAlfabeto.equals("") ? ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase() : "<a href=\"#no-back\" onClick=\"postData('../v3/consultarDetalhesCsa?acao=listar')\">" + ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase() + "</a>");
              %>
            </div>
          </div>
          <div class="pt-3 table-responsive">
            <table class="table table-striped table-hover">
              <thead>
                <tr>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_IDENTIFICADOR)%>">
                    <th scope="col"><hl:message key="rotulo.consignataria.codigo" /></th>
                  </show:showfield>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_NOME)%>">
                    <th scope="col"><hl:message key="rotulo.consignataria.nome" /></th>
                  </show:showfield>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_NOME_ABREV)%>">
                    <th scope="col"><hl:message key="rotulo.consignataria.nome.abreviado" /></th>
                  </show:showfield>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_CNPJ)%>">
                    <th scope="col"><hl:message key="rotulo.consignataria.cnpj" /></th>
                  </show:showfield>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_SITUACAO)%>">
                    <th scope="col"><hl:message key="rotulo.consignataria.status" /></th>
                  </show:showfield>
                  <% if (podeConsultarCsa) { %>
                  <th scope="col"><hl:message key="rotulo.acoes" /></th>
                  <% } %>
                </tr>
              </thead>
              <tbody>
                <%
                    Iterator <DadosConsignataria> it = consignatarias.iterator();
                    while (it.hasNext()) {
                        DadosConsignataria consignataria = (DadosConsignataria)it.next();
                        String csaCodigo = consignataria.getCsaCodigo();
                        String csaNome = consignataria.getCsaNome();
                        String csaNomeAbrev = consignataria.getCsaNomeAbrev();
                        String csaIdentificador = consignataria.getCsaIdentificador();
                        String csaAtivo = consignataria.getCsaAtivo();
                        String descStatus = consignataria.getCsaStatusDescricao();
                        String csaCnpj = consignataria.getCsaCnpj();
                %>
                <tr>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_IDENTIFICADOR)%>">
                    <td><%=TextHelper.forHtmlContent(csaIdentificador)%></td>
                  </show:showfield>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_NOME)%>">
                    <td><%=TextHelper.forHtmlContent(csaNome.toUpperCase())%></td>
                  </show:showfield>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_NOME_ABREV)%>">
                    <td><%=TextHelper.forHtmlContent(csaNomeAbrev)%></td>
                  </show:showfield>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_CNPJ)%>">
                    <td><%=TextHelper.forHtmlContent(csaCnpj)%></td>
                  </show:showfield>
                  <show:showfield key="<%=TextHelper.forHtmlAttribute(FieldKeysConstants.CONSULTAR_CONSIGNATARIAS_SER_CSA_SITUACAO)%>">
                    <td <%=StatusConsignatariaEnum.recuperaStatusConsignataria(csaAtivo).isBloqueado() ? "class=\"block\"" : ""%>><%=TextHelper.forHtmlContent(descStatus)%></td>
                  </show:showfield>

                  <% if (podeConsultarCsa) { %>
                    <td><a href="#no-back" onClick="postData('../v3/consultarDetalhesCsa?acao=visualizar&csa=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>')"><hl:message key="rotulo.conf.banner.visualizar" /></a></td>
                  <% }%>
                </tr>
                <%
                    }
                %>
              </tbody>
              <tfoot>
                <tr>
                  <% if (podeConsultarCsa) { %><td colspan="4"><% } else {%><td colspan="3"><% }%>
                     <hl:message key="rotulo.lote.listagem.consignatarias" /> 
                     <span class="font-italic"> - <%=request.getAttribute("_paginacaoSubTitulo")%></span>
                  </td>
                </tr>
              </tfoot>
            </table>
            <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
          </div>
          <div class="btn-action">
            <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar" /></a>
          </div>
        </div>
      </div>
    </div>
  </div>
<script>
function validaFiltroTipo() {
     var valorFiltro = document.getElementById("filtroTipo").value;
     // Se o valor do filtro for 4, ou seja, Natureza de consignataria, habilita o campo select e disabilita o input de tipo texto
     if (valorFiltro == "04") {
        document.getElementById("FILTRO").disabled = true;
        document.getElementById("FILTRO").value = '';
        document.getElementById("rowNcaCodigo").style.display = '';
    } else {
            document.getElementById("FILTRO").disabled = false;
            document.getElementById("rowNcaCodigo").style.display = 'none';
        }
  }
</script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>