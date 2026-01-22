<%--
* <p>Title: ReajustarContratos</p>
* <p>Description: Contem formulario de reajuste de contratos</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: anderson.assis $
* $Revision: $
* $Date: 2019-08-08 11:59:25 -0300 (qui, 08 ago 2019) $
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String csa_codigo = (String) request.getAttribute("csa_codigo");
String linkRefresh = (String) request.getAttribute("linkRefresh");
String path = (String) request.getAttribute("path");
boolean temProcessoRodando = (boolean) request.getAttribute("temProcessoRodando");
List<?> servicos = (List<?>) request.getAttribute("servicos");
List<?> consignatarias = (List<?>) request.getAttribute("consignatarias");
List<?> arquivos = (List<?>) request.getAttribute("arquivos");
%>

<c:set var="title">
  <hl:message key="rotulo.reajuste.contratos.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <form name="form1" method="post" action="../v3/reajustarContratos?acao=iniciar">
    <% out.print(SynchronizerToken.generateHtmlToken(request)); %>
    <input name="hdAcao" type="hidden" id="hdAcao" value="">
    <% if(!temProcessoRodando){ %>
      <div class="card">
        <div class="card-header">
        <% if(responsavel.isCseSupOrg()){%>
          <h2 class="card-header-title"><hl:message key="rotulo.reajuste.contratos.titulo.card"/></h2>
        <% } else{%>
          <h2 class="card-header-title"> <%=responsavel.getIdEntidade() != null ? TextHelper.forHtmlContent(responsavel.getIdEntidade()) + " - " : ""%><%=TextHelper.forHtmlContent(responsavel.getNomeEntidade())%> </h2>
        <%} %>
        </div>
        <div class="card-body">
          <div class="legend">
            <span><hl:message key="rotulo.reajuste.sessao.contrato"/></span>
          </div>
                    <% if (responsavel.isCseSup()) { %>
          <div class="row">
            <div class="form-group col-sm-6">
              <label for="csa_codigo" class="mb-2"><hl:message key="rotulo.consignataria.singular"/></label>              
              <select class="form-control form-select select"
                      id="csa_codigo"
                      name="csa_codigo"
                      onFocus="SetarEventoMascaraV4(this,'#*200',true);"
                      onBlur="fout(this);ValidaMascaraV4(this);"
                      onChange="AlteraCsa()">
                <option value=""><hl:message key="rotulo.campo.selecione"/></option>
                <%
                  Iterator it = consignatarias.iterator();
                  while (it.hasNext()) {
                    CustomTransferObject consignataria = (CustomTransferObject)it.next();
                    String codigo = (String)consignataria.getAttribute(Columns.CSA_CODIGO);
                    String nome = (!TextHelper.isNull(consignataria.getAttribute(Columns.CSA_NOME_ABREV))) ? (String)consignataria.getAttribute(Columns.CSA_NOME_ABREV) : (String)consignataria.getAttribute(Columns.CSA_NOME);
                %>
                <option value="<%=TextHelper.forHtmlAttribute(codigo)%>" <%=(csa_codigo.equals(codigo)) ? "selected" : ""%>><%=TextHelper.forHtmlContent(nome)%></option>
                <%
                  }
                %>
              </select> 
            </div>
          </div>
          <% } else {%>
          <input type="hidden" name="csa_codigo" value="<%=TextHelper.forHtmlAttribute(csa_codigo)%>">
          <% } %>
          <div class="row">
            <div class="form-group col-sm-6">
              <label for="vlr_reajuste" class="mb-2"><hl:message key="rotulo.aplicar.reajuste.nos.seguintes.contratos"/></label>              
              <hl:htmlinput classe="form-control"
                            di="vlr_reajuste"
                            name="vlr_reajuste"
                            type="text"
                            value='<%=TextHelper.forHtml(JspHelper.verificaVarQryStr(request, "vlr_reajuste"))%>'
                            size="10"
                            onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.reajuste.placeholder", responsavel)%>"/>
            </div>
            <div class="form-group col-sm-3 mt-4 pt-1">
              <select class="form-control form-select" id="selecaoDeTipoDeValor" name="tipo_reajuste">
                <option value=""><hl:message key="rotulo.campo.selecione"/></option>
                <option value="percentual" <%=(JspHelper.verificaVarQryStr(request, "tipo_reajuste").equals("percentual")) ? "selected" : ""%>><hl:message key="rotulo.porcentagem"/></option>
                <option value="R" <%=(JspHelper.verificaVarQryStr(request, "tipo_reajuste").equals("R")) ? "selected" : ""%>><hl:message key="rotulo.moeda"/></option>
              </select> 
            </div>
          </div>
          <div class="legend">
            <span><hl:message key="rotulo.reajuste.sessao.regras"/></span>
          </div>
          <div class="row ">
            <div class="form-group col-sm-6">
              <label for="vlr_igual" class="mb-2"><hl:message key="rotulo.reajuste.valor.desconto.folha.igual"/></label>
              <input class="form-control w-100"
                     id="vlr_igual" 
                     name="vlr_igual"
                     type="text"
                     value='<%=TextHelper.forHtml(JspHelper.verificaVarQryStr(request, "vlr_igual"))%>'
                     size="10"
                     onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                     placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.reajuste.digite.o.valor.em.reais", responsavel)%>"
                     aria-label="valorDesconto">
            </div>
            <div class="col-sm-6">
              <div class="text-nowrap align-text-top mt-3">
                <div class="form-check pt-4">
                  <input class="form-check-input ml-1" type="checkbox" value="S" name="chkvlr_igual" id="chkvlr_igual" onClick="HabilitaRegra(this , document.forms[0].vlr_igual);" <%= JspHelper.verificaVarQryStr(request, "chkvlr_igual").equals("S") ? " checked " : "" %>>
                  <label class="form-check-label labelSemNegrito ml-1" for="chkvlr_igual"><hl:message key="rotulo.reajuste.ativar.regra"/></label>
                </div>
              </div>
            </div>
          </div>
          <div class="row">
            <div class="form-group col-sm-6">
              <label for="vlr_maior_igual" class="mb-2"><hl:message key="rotulo.reajuste.valor.desconto.folha.maior"/></label>            
              <input class="form-control w-100"
                            id="vlr_maior_igual"
                            name="vlr_maior_igual"
                            type="text"
                            value='<%=TextHelper.forHtml(JspHelper.verificaVarQryStr(request, "vlr_maior_igual"))%>'
                            size="10"
                            onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                            placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.reajuste.digite.o.valor.em.reais", responsavel)%>"
                            aria-label="vlr_maior_igual"/>
            </div>
            <div class="col-sm-6">
              <div class="text-nowrap align-text-top mt-3">
                <div class="form-check pt-4">
                  <input class="form-check-input ml-1" type="checkbox" name="chkvlr_maior_igual" id="chkvlr_maior_igual" value="S" onClick="HabilitaRegra(this , document.forms[0].vlr_maior_igual);" <%= JspHelper.verificaVarQryStr(request, "chkvlr_maior_igual").equals("S") ? " checked " : "" %>>
                  <label class="form-check-label labelSemNegrito ml-1" for="chkvlr_maior_igual"><hl:message key="rotulo.reajuste.ativar.regra"/></label>
                </div>
              </div>
          </div>    
          </div>
          <div class="row">
            <div class="form-group col-sm-6">
              <label for="vlr_menor_igual" class="mb-2"><hl:message key="rotulo.reajuste.valor.desconto.folha.menor"/></label>
              <input type="text"
                     class="form-control w-100"
                     id="vlr_menor_igual"
                     name="vlr_menor_igual"
                     value='<%=TextHelper.forHtml(JspHelper.verificaVarQryStr(request, "vlr_menor_igual"))%>'
                     size="10"
                     onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                      placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.reajuste.digite.o.valor.em.reais", responsavel)%>"
                     aria-label="vlr_maior_igual">                    
            </div>
            <div class="col-sm-6">
              <div class="text-nowrap align-text-top mt-3">
                <div class="form-check pt-4">
                  <input class="form-check-input ml-1" type="checkbox" name="chkvlr_menor_igual" id="chkvlr_menor_igual" value="S" onClick="HabilitaRegra(this , document.forms[0].vlr_menor_igual)" <%= JspHelper.verificaVarQryStr(request, "chkvlr_menor_igual").equals("S") ? " checked " : "" %>>
                  <label class="form-check-label labelSemNegrito ml-1" for="chkvlr_menor_igual"><hl:message key="rotulo.reajuste.ativar.regra"/></label>
                </div>
              </div>
            </div>
          </div>
          <div class="row">
            <div class="form-group col-sm-6 pt-1">
              <label for="limitado_tipo" class="mb-2"><hl:message key="rotulo.reajuste.limitando.valor"/> </label>
              <select class="form-select form-control" id="limitado_tipo" name="limitado_tipo">
                <option value="desconto" <%=(JspHelper.verificaVarQryStr(request, "limitado_tipo").equals("desconto")) ? "selected" : ""%>><hl:message key="rotulo.novo.desconto.reajustado"/></option>
                <option value="reajuste" <%=(JspHelper.verificaVarQryStr(request, "limitado_tipo").equals("reajuste")) ? "selected" : ""%>><hl:message key="rotulo.reajuste.singular"/></option>
              </select>
            </div>
            <div class="form-check pt-2  col-sm-1 col-md-1">
              <div class="text-center mt-4 form-control-label">
                <label for="limitado_vlr" class="labelSemNegrito pt-4"><hl:message key="rotulo.reajuste.a.moeda"/></label>
              </div>
            </div>
            <div class="form-check pt-2 col-sm-12 col-md-2  pt-1">
              <input type="text"
                     class="form-control w-100 mt-4"
                     id="limitado_vlr"
                     name="limitado_vlr"
                     value='<%=TextHelper.forHtml(JspHelper.verificaVarQryStr(request, "limitado_vlr"))%>'
                     onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                     placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.reajuste.valor.em.reais.placeholder", responsavel)%>"
                     aria-label="limitado_vlr">
            </div>
              <div class="align-text-top col-md-2">
                <div class="form-check pt-4 mt-4">
                  <input type="checkbox"
                         id="chklimitado_vlr"
                         class="form-check-input ml-1"
                         name="chklimitado_vlr">
                  <label class="form-check-label labelSemNegrito ml-1" for="chklimitado_vlr"><hl:message key="rotulo.reajuste.ativar.regra"/></label>
                </div>
              </div>
          </div>          
          <div class="row">            
            <div class="form-group col-sm-6 mt-1 pt-1">
            <label for="verba" class="mb-2"><hl:message key="rotulo.reajuste.verba.igual"/></label>
                <select class="form-control form-select" id="verba" name="verba" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                  <option value=""><hl:message key="rotulo.campo.selecione"/></option>
                    <%
                      Iterator it = servicos.iterator();
                      while (it.hasNext()) {
                        CustomTransferObject servico = (CustomTransferObject)it.next();
                        String cnvCodVerba = (String)servico.getAttribute(Columns.CNV_COD_VERBA);
                        String svcDescricao = (String)servico.getAttribute(Columns.SVC_DESCRICAO);
                        String svcCodigo = (String)servico.getAttribute(Columns.SVC_CODIGO);
                        if (cnvCodVerba != null && !cnvCodVerba.equals("")) {
                    %>
                  <option value="<%=TextHelper.forHtmlAttribute(cnvCodVerba)+";"+TextHelper.forHtmlAttribute(svcCodigo)%>" <%=(JspHelper.verificaVarQryStr(request, "verba").equals(cnvCodVerba+";"+svcCodigo)) ? "selected" : ""%>> <%=TextHelper.forHtmlContent(cnvCodVerba + " - " + svcDescricao)%></option>
                    <%
                        }
                      }
                    %>
                </select>
              </div>
              <div class="col-sm-6">
                <div class="text-nowrap align-text-top col-md-2">
                  <div class="form-check pt-4 mt-4">
                    <input class="form-check-input ml-1"
                           type="checkbox"
                           name="chkverba"
                           id="chkverba"
                           value="S"
                           onClick="HabilitaRegra(this , document.forms[0].verba)" <%= JspHelper.verificaVarQryStr(request, "chkverba").equals("S") ? " checked " : "" %>>
                    <label class="form-check-label labelSemNegrito ml-1" for="chkverba"><hl:message key="rotulo.reajuste.ativar.regra"/></label>
                  </div>
                </div>
              </div> 
          </div>
          <%
          boolean possuiCadIndice = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_CAD_INDICE, responsavel);
          %>          
          <div class="row">
            <div class="form-group col-sm-6">
              <label for="padrao_verba" class="mb-2"> <%=possuiCadIndice ? ApplicationResourcesHelper.getMessage("mensagem.reajuste.verba.indice.padrao", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.reajuste.verba.padrao", responsavel)%> </label>
              <input type="text"
                     id="padrao_verba"
                     name="padrao_verba"
                     class="form-control w-100"
                     value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "padrao_verba"))%>"
                     placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.reajuste.digite.o.valor.em.reais", responsavel)%>"
                     aria-label="padrao_verba">
            </div>
            <div class="form-forup col-sm-2 mt-2 pt-1">
              <% if (possuiCadIndice) { %>
              <input name="padrao_indice"
                     type="text"
                     class="form-control mt-3"
                     value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "padrao_indice"))%>"
                     placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.reajuste.indice.placeholder", responsavel)%>">
              <% } %>
            </div>           
            <div class="text-nowrap align-text-top col-md-2 mt-3">
              <div class="form-check pt-4">
                <input class="form-check-input"
                       type="checkbox"
                       name="chkpadrao_verba"
                       id="chkpadrao_verba"
                       onClick="HabilitaRegra(this , document.forms[0].padrao_verba)" <%= JspHelper.verificaVarQryStr(request, "chkpadrao_verba").equals("S") ? " checked " : "" %>>                                      
                <label class="form-check-label labelSemNegrito ml-1" for="chkpadrao_verba"><hl:message key="rotulo.reajuste.ativar.regra"/></label>
              </div>
            </div>
          </div>
              <% } %>   
        </div>
      </div>
    </form>    
    <div class="btn-action">
      <% if(!temProcessoRodando){ %>
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
      <a class="btn btn-primary" href="#no-back" onClick="return ValidaValores('validar');"><hl:message key="rotulo.botao.validar"/></a>
      <a class="btn btn-primary" href="#no-back" onClick="return ValidaValores('aplicar');" ><hl:message key="rotulo.botao.aplicar"/></a>
      <% } else { %>
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
      <% } %>
    </div>
    
  <%

  %>
  
  <% if(!temProcessoRodando) { %>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="mensagem.reajuste.arquivos.critica.disponiveis.download"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col" width="50%"><hl:message key="rotulo.reajuste.nome"/></th>
            <th scope="col" width="10%"><hl:message key="rotulo.reajuste.tamanho"/></th>
            <th scope="col" width="15%"><hl:message key="rotulo.reajuste.data"/></th>
            <th scope="col" width="10%"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody>
        <%if (arquivos == null || arquivos.size() == 0){ %>
        <tr>
          <td colspan="6"><hl:message key="rotulo.lst.arq.generico.encontrado"/></td>
        </tr>
        <%
          } else {
            Iterator iter = arquivos.iterator();
            String data, nome, formato, conversor;
            while (iter.hasNext()) {
              File arquivo = (File)iter.next();
              String tam = "";
              if (arquivo.length() > 1024.00) {
                tam = Math.round(arquivo.length() / 1024.00) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
              } else {
                tam = arquivo.length() + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
              }
              data = DateHelper.toDateTimeString(new java.sql.Date(arquivo.lastModified()));
              nome = arquivo.getPath().substring(path.length() + 1);
              formato = "";
              conversor = null;
              if (nome.toLowerCase().endsWith(".pdf")) {
                formato = "pdf.gif";
              } else if (nome.toLowerCase().endsWith(".txt")) {
                formato = "text.gif";
                conversor = "zip.gif";
              } else if (nome.toLowerCase().endsWith(".htm")) {
                formato = "html.gif";
              } else if (nome.toLowerCase().endsWith(".zip")) {
                formato = "zip.gif";
                conversor = "text.gif";
              }              
        %>
        <tr>  
          <td><%=TextHelper.forHtmlContent(nome) %></td>            
          <td><%=TextHelper.forHtmlContent(tam)%></td>
          <td><%=TextHelper.forHtmlContent(data)%></td>
          <td>
            <div class="actions">
              <div class="dropdown">
                <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                  <div class="form-inline">
                    <span class="mr-1" data-bs-toggle="tooltip" aria-label="<hl:message key="rotulo.faturamento.beneficios.opcoes" />" title="" data-original-title="<hl:message key="rotulo.faturamento.beneficios.opcoes" />"><svg>
                    <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg></span>
                    <hl:message key="rotulo.reajuste.opcoes" />
                  </div>
                </a>
                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                  <a class="dropdown-item" href="#no-back" onClick="javascript:fazDownload('<%=TextHelper.forJavaScript(nome)%>', '<%=TextHelper.forJavaScript(csa_codigo)%>');"><hl:message key="rotulo.reajuste.download"/></a>
                  <a class="dropdown-item" href="#no-back" onClick="javascript:doIt('e', '<%=TextHelper.forJavaScript(arquivo.getName())%>','<%=TextHelper.forJavaScript(nome)%>'); return false;"><hl:message key="rotulo.reajuste.excluir"/></a>
                </div>
              </div>
            </div>
          </td>
        </tr>        
        </tbody> 
        <%
            }
          }
        %>
      <tfoot>
        <tr>
          <td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.reajuste.arquivos.critica.disponiveis.download", responsavel) + " - " %><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td>
        </tr>
      </tfoot>
      </table>
      <div class="card-footer">
        <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
      </div>   
    </div>
  </div>
  <% } %> 
</c:set>
<c:set var="javascript"> 
  <script type="text/JavaScript">
	window.onload = doLoad(<%=(boolean)temProcessoRodando%>);
  
    function doIt(opt, arq, path) {
      var msg = '', j;
      if (opt == 'e') {
        msg = '<hl:message key="mensagem.confirmacao.lst.arq.generico.exclusao"/>'.replace('{0}', arq);
        j = '../v3/excluirArquivo?<%=SynchronizerToken.generateToken4URL(request)%>&arquivo_nome=' + encodeURIComponent(path) + '&entidade=csa&subtipo=<%=TextHelper.forJavaScriptBlock(csa_codigo)%>&tipo=reajuste';
      } else {
        return false;
      }
      if (msg != '') {
        ConfirmaUrl(msg, j);
      } else {
        postData(j);
      }
      return true;
    }
    
    function ValidaValores(opcao) {
      if (document.forms[0].csa_codigo.value == '' || document.forms[0].csa_codigo.value == null) {
        alert('<hl:message key="mensagem.informe.consignataria.reajuste"/>');
        document.forms[0].csa_codigo.focus();
        return false;
      }
    
      if (document.forms[0].vlr_reajuste.value == '' || document.forms[0].vlr_reajuste.value == null) {
        alert('<hl:message key="mensagem.informe.valor.reajuste"/>');
        document.forms[0].vlr_reajuste.focus();
        return false;
      }
    
      if (document.forms[0].tipo_reajuste.value == '' || document.forms[0].tipo_reajuste.value == null) {
        alert('<hl:message key="mensagem.informe.tipo.reajuste"/>');
        document.forms[0].tipo_reajuste.focus();
        return false;
      }
    
      if ((!document.forms[0].chkvlr_igual.checked) && (!document.forms[0].chkvlr_maior_igual.checked) &&
          (!document.forms[0].chkvlr_menor_igual.checked) && (!document.forms[0].chkverba.checked) &&
          (!document.forms[0].chkpadrao_verba.checked) && (!document.forms[0].chklimitado_vlr.checked)) {
        alert('<hl:message key="mensagem.informe.uma.regra.reajuste"/>');
        document.forms[0].vlr_igual.focus();
        return false;
      }
    
      if ((document.forms[0].chkvlr_igual.checked) && (document.forms[0].vlr_igual.value == '' || document.forms[0].vlr_igual.value == null)) {
        alert('<hl:message key="mensagem.informe.valor.regra.um.reajuste"/>');
        document.forms[0].vlr_igual.focus();
        return false;
      }
    
      if (!document.forms[0].chkvlr_igual.checked && document.forms[0].vlr_igual.value != '' && document.forms[0].vlr_igual.value != null) {
        alert('<hl:message key="mensagem.reajuste.valor.regra.um.nao.ativada"/>');
      document.forms[0].vlr_igual.focus();
      return false;
      }
        
      if ((document.forms[0].chkvlr_maior_igual.checked) && (document.forms[0].vlr_maior_igual.value == '' || document.forms[0].vlr_maior_igual.value == null)) {
        alert('<hl:message key="mensagem.informe.valor.regra.dois.reajuste"/>');
        document.forms[0].vlr_maior_igual.focus();
        return false;
      }
    
      if (!document.forms[0].chkvlr_maior_igual.checked && document.forms[0].vlr_maior_igual.value != '' && document.forms[0].vlr_maior_igual.value != null) {
      alert('<hl:message key="mensagem.reajuste.valor.regra.dois.nao.ativada"/>');
      document.forms[0].vlr_maior_igual.focus();
      return false;
      }
    
      if ((document.forms[0].chkvlr_menor_igual.checked) && (document.forms[0].vlr_menor_igual.value == '' || document.forms[0].vlr_menor_igual.value == null)) {
        alert('<hl:message key="mensagem.informe.valor.regra.tres.reajuste"/>');
        document.forms[0].vlr_menor_igual.focus();
        return false;
      }
    
      if (!document.forms[0].chkvlr_menor_igual.checked && document.forms[0].vlr_menor_igual.value != '' && document.forms[0].vlr_menor_igual.value != null) {
      alert('<hl:message key="mensagem.reajuste.valor.regra.tres.nao.ativada"/>');
      document.forms[0].vlr_menor_igual.focus();
      return false;
      }
        
      if (document.forms[0].chklimitado_vlr.checked) {
        if (document.forms[0].limitado_vlr.value == '' || document.forms[0].limitado_vlr.value == null) {
          alert('<hl:message key="mensagem.informe.valor.regra.quatro.reajuste"/>');
          document.forms[0].limitado_vlr.focus();
          return false;
        } else if (document.forms[0].limitado_vlr.value.charAt(0) == '-') {
          alert('<hl:message key="mensagem.reajuste.valor.regra.quatro.negativo"/>');
          document.forms[0].limitado_vlr.focus();
          return false;
        }
      }
    
      if (!document.forms[0].chklimitado_vlr.checked && document.forms[0].limitado_vlr.value != '' && document.forms[0].limitado_vlr.value != null) {
      alert('<hl:message key="mensagem.reajuste.valor.regra.quatro.nao.ativada"/>');
      document.forms[0].limitado_vlr.focus();
      return false;
      }
    
      if ((document.forms[0].chkverba.checked) && (document.forms[0].verba.value == '' || document.forms[0].verba.value == null)) {
        alert('<hl:message key="mensagem.informe.valor.regra.cinco.reajuste"/>');
        document.forms[0].verba.focus();
        return false;
      }
    
      if (!document.forms[0].chkverba.checked && document.forms[0].verba.value != '' && document.forms[0].verba.value != null) {
      alert('<hl:message key="mensagem.reajuste.valor.regra.cinco.nao.ativada"/>');
      document.forms[0].verba.focus();
      return false;
      }
        
      if ((document.forms[0].chkpadrao_verba.checked) && (document.forms[0].padrao_verba.value == '' || document.forms[0].padrao_verba.value == null)) {
        alert('<hl:message key="mensagem.informe.valor.regra.seis.reajuste"/>');
        document.forms[0].padrao_verba.focus();
        return false;
      }
    
      if (!document.forms[0].chkpadrao_verba.checked && ((document.forms[0].padrao_verba.value != '' && document.forms[0].padrao_verba.value != null) || (document.forms[0].padrao_indice != null && document.forms[0].padrao_indice.value != '' && document.forms[0].padrao_indice.value != null))) {
        alert('<hl:message key="mensagem.reajuste.valor.regra.seis.nao.ativada"/>');
        document.forms[0].padrao_verba.focus();
        return false;
      }
    
      if ((opcao == 'validar' && confirm('<hl:message key="mensagem.confirmacao.validar.reajuste.contratos"/>')) || 
          (opcao == 'aplicar' && confirm('<hl:message key="mensagem.confirmacao.aplicar.reajuste.contratos"/>'))) {
        document.forms[0].action = '../v3/reajustarContratos?acao=' + opcao;
        document.forms[0].submit();
      }
      return false;
    }
    
    function AlteraCsa() {
      document.forms[0].hdAcao.value = 'start';
      document.forms[0].submit();
      return true;
    }
    
    function HabilitaRegra(campocheck , campo) {
    //  campo.enabled = campocheck.checked;
    }
    
    function doLoad(reload) {
      if (reload) {
        setTimeout("refresh()", 15*1000);
      } else {
        document.forms[0].vlr_reajuste.focus();
      }
    }
    
    function refresh() {
      postData('<%=TextHelper.forJavaScriptBlock(linkRefresh)%>');
    }
    
    function fazDownload(nome, csaCodigo){
  	  postData('../v3/downloadArquivo?arquivo_nome=' + encodeURIComponent(nome) + '&entidade=csa&subtipo='+ csaCodigo + '&tipo=reajuste&<%=SynchronizerToken.generateToken4URL(request)%>','download');
  	}
  </script>
</c:set>
<t:page_v4>
   <jsp:attribute name="header">${title}</jsp:attribute>
   <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
   <jsp:attribute name="javascript">${javascript}</jsp:attribute>
   <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>


