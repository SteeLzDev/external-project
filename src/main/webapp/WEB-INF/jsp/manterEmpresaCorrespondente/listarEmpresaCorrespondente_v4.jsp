<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);  
  List<?> empresas = (List<?>) request.getAttribute("empresas");  
  String filtro2 = (String) request.getAttribute("filtro2");
  String titulo = (String) request.getAttribute("titulo");
  String filtro = (String) request.getAttribute("filtro");  
  int filtro_tipo = (int) request.getAttribute("filtro_tipo");
  
  boolean podeEditar = (boolean) request.getAttribute("podeEditar");
  boolean podeConsultar = (boolean) request.getAttribute("podeConsultar");
%>
<c:set var="title">
  <hl:message key="rotulo.lista.empresa.correspondente.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <% if(podeEditar){ %>
  <div class="btn-action">
    <button class="btn btn-primary" onClick="postData('../v3/manterEmpresaCorrespondente?acao=editar&_skip_history_=true&tipo=consultar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.botao.nova.empresa.correspondente"/></button>
  </div>
  <% } %>
  <div class="row">
    <div class="col-sm-5 col-md-4">
      <form name="form1" method="post" action="manterEmpresaCorrespondente?acao=iniciar&tipo=consultar&<%=SynchronizerToken.generateToken4URL(request)%>">
        <div class="card">
          <div class="card-header hasIcon pl-3">
            <h2 class="card-header-title">
              <hl:message key="rotulo.acao.pesquisar" />
            </h2>
          </div>
          <div class="card-body">
            <div class="row">
              <div class="form-group col-sm">
                <label for="iFiltro"><hl:message key="rotulo.filtro.empresa.correspondente"/></label>
                <input type="text" name="FILTRO" class="form-control" size="10" value="<%=TextHelper.forHtmlAttribute(filtro)%>" onFocus="SetarEventoMascara(this,'#*200',true);"  onBlur="fout(this);ValidaMascara(this);">
              </div>            
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <label for="filtroTipo"><hl:message key="rotulo.acao.filtrar.por" /></label>
                <select name="FILTRO_TIPO" class="form-control form-select select" onFocus="SetarEventoMascara(this,'#*200',true);"  onBlur="fout(this);ValidaMascara(this);" nf="Filtrar">
                  <optgroup label="<hl:message key='rotulo.usuario.lista.filtros'/>">
                    <option VALUE=""   <%=(String)((filtro_tipo == -1) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.sem.filtro"/></option>
                    <option VALUE="02" <%=(String)((filtro_tipo ==  2) ? "SELECTED" : "")%>><hl:message key="rotulo.codigo.empresa.correspondente"/></option>
                    <option VALUE="03" <%=(String)((filtro_tipo ==  3) ? "SELECTED" : "")%>><hl:message key="rotulo.nome.empresa.correspondente"/></option>
                    <option VALUE="00" <%=(String)((filtro_tipo ==  0) ? "SELECTED" : "")%>><hl:message key="rotulo.bloqueado.empresa.correspondente"/></option>
                    <option VALUE="01" <%=(String)((filtro_tipo ==  1) ? "SELECTED" : "")%>><hl:message key="rotulo.desbloqueado.empresa.correspondente"/></option>
                    <option VALUE="04" <%=(String)((filtro_tipo ==  4) ? "SELECTED" : "")%>><hl:message key="rotulo.cnpj.empresa.correspondente"/></option>
                  </optgroup>
                </select>               
              </div>
            </div>
          </div>
        </div>
        <div class="btn-action">
          <a class="btn btn-primary" href="#no-back" onClick="form1.submit(); return false;">
            <svg width="20"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-consultar"></use></svg>
            <hl:message key="rotulo.acao.pesquisar" />
          </a>
        </div>
      </form>        
    </div>
    <div class="col-sm-7 col-md-8">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title">
            <hl:message key="rotulo.filtrar.inicial.nome" />
          </h2>
        </div>
        <div class="card-body table-responsive p-0">
          <div class="row mr-0 pl-3 pr-3 pt-2 pb-0 d-print-none">
             <div class="col-sm-12">
               <div class="form-group mb-1">
                 <span><hl:message key="rotulo.filtrar.inicial.nome"/></span>
               </div>
             </div>
             <div class="col-sm-12">
                <%
                   for (int i='A'; i <='Z'; i++) {
                      out.print((filtro2.equals(String.valueOf((char)i)) ? String.valueOf((char)i) : "<a href=\"#no-back\" onClick=\"postData('../v3/manterEmpresaCorrespondente?acao=iniciar&FILTRO2=" + String.valueOf((char)i) + "')\">" + String.valueOf((char)i) + "</a>") + " - ");
                   }
                   out.print(filtro2.equals("") ? ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel) : "<a href=\"#no-back\" onClick=\"postData('../v3/manterEmpresaCorrespondente?acao=iniciar')\">" + ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel) + "</a>");
                %>
             </div>
          </div>
          <div class="pt-3 table-responsive"> 
            <table class="table table-striped table-hover">
              <thead>
                <tr>
                  <th scope="col"><hl:message key="rotulo.codigo.empresa.correspondente"/></th>
                  <th scope="col"><hl:message key="rotulo.nome.empresa.correspondente"/></th>
                  <th scope="col"><hl:message key="rotulo.cnpj.empresa.correspondente"/></th>
                  <th scope="col"><hl:message key="rotulo.empresa.correspondente.status"/></th>
                  <th scope="col"><hl:message key="rotulo.acoes"/></th>
                </tr>
              </thead>
              <tbody>
  <%
    Iterator<?> it = empresas.iterator();
    while (it.hasNext()) {
      String classStatusEmpCor ="";
      TransferObject empresa = (TransferObject) it.next();
      String eco_codigo = (String) empresa.getAttribute(Columns.ECO_CODIGO);
      String eco_nome = empresa.getAttribute(Columns.ECO_NOME)!= null? (String) empresa.getAttribute(Columns.ECO_NOME): "";
      String eco_cnpj = empresa.getAttribute(Columns.ECO_CNPJ)!= null? (String) empresa.getAttribute(Columns.ECO_CNPJ): "";
      String eco_ativo = empresa.getAttribute(Columns.ECO_ATIVO) != null ? empresa.getAttribute(Columns.ECO_ATIVO).toString() : "1";
      String msgBloquearDesbloquear = "";
      String msgStatusEmpCor = "";
      if (eco_ativo.equals("1")) {
          msgBloquearDesbloquear = ApplicationResourcesHelper.getMessage("mensagem.empresa.correspondente.bloquear.clique.aqui", responsavel);
          msgStatusEmpCor = ApplicationResourcesHelper.getMessage("rotulo.desbloqueado.empresa.correspondente", responsavel);
      } else {
          classStatusEmpCor ="block";
          msgBloquearDesbloquear = ApplicationResourcesHelper.getMessage("mensagem.empresa.correspondente.desbloquear.clique.aqui", responsavel);
          msgStatusEmpCor = ApplicationResourcesHelper.getMessage("rotulo.bloqueado.empresa.correspondente", responsavel);
      }
    
      if (!eco_ativo.equals("2")) {
  %>
                <tr>
                  <td><%=TextHelper.forHtmlContent(empresa.getAttribute(Columns.ECO_IDENTIFICADOR))%></td>
                  <td><%=TextHelper.forHtmlContent(eco_nome.toUpperCase())%></td>
                  <td><%=TextHelper.forHtmlContent(empresa.getAttribute(Columns.ECO_CNPJ))%></td>
                  <td class="<%=TextHelper.forHtmlAttribute(classStatusEmpCor)%>"><%=TextHelper.forHtmlContent(msgStatusEmpCor)%></td>
                  <td>
                    <div class="actions">
                      <div class="dropdown">
                        <a class="dropdown-toggle ico-action" href="#" role="button" id="MASTER" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                          <div class="form-inline">
                            <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.botao.opcoes" />" aria-label="<hl:message key="rotulo.botao.opcoes" />"> <svg>
                                <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                            </span> <hl:message key="rotulo.botao.opcoes" />
                          </div>
                        </a>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
  <%    if (podeEditar) { %>
                          <a class="dropdown-item" href="#no-back" onClick="vf_bloqueio_empresa('<%=TextHelper.forJavaScript(eco_codigo)%>','<%=TextHelper.forJavaScript(eco_nome)%>','<%=TextHelper.forJavaScript(eco_ativo)%>');"><hl:message key="rotulo.acoes.bloquear.desbloquear"/></a>
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterEmpresaCorrespondente?acao=detalhar&tipo=consultar&ECO_CODIGO=<%=TextHelper.forJavaScriptAttribute(eco_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a>
                          <a class="dropdown-item" href="#no-back" onClick="vf_exclusao_empresa('<%=TextHelper.forJavaScript(eco_codigo)%>','<%=TextHelper.forJavaScript(eco_nome)%>');"><hl:message key="rotulo.acoes.excluir"/></a>
                         <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterEmpresaCorrespondente?acao=detalharAssociacao&tipo=consultar&ECO_CODIGO=<%=TextHelper.forJavaScriptAttribute(eco_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acao.empresa.correspondente.associar"/></a>
  <%    } else if (podeConsultar) {%>
                          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterEmpresaCorrespondente?acao=detalhar&tipo=consultar&ECO_CODIGO=<%=TextHelper.forJavaScriptAttribute(eco_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.consultar"/></a>
  <%    } %>
                        </div>                     
                      </div>                      
                    </div>
                  </td>
                </tr>
  <%        
      }
    }
  %>
              </tbody>
              <tfoot>
                <tr>
                  <td colspan="12">
                    <%=ApplicationResourcesHelper.getMessage("mensagem.listagem.plano.desconto", responsavel) + " - "%>
                    <span class="font-italic"> <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/></span>
                  </td>                
                </tr>
              </tfoot>
            </table>
          </div>         
        </div>
        <div class="card-footer">
          <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
        </div>
      </div>      
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar" /></a>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
  <script type="text/JavaScript">
    function vf_bloqueio_empresa(ecoCodigo, ecoNome, ecoAtivo) {
      var mensagemAlert = ecoAtivo == '1'? '<hl:message key="mensagem.empresa.correspondente.bloqueio.confirma"/>': '<hl:message key="mensagem.empresa.correspondente.desbloqueio.confirma"/>';
      if (confirm(mensagemAlert.replace('{0}',ecoNome))) {
         postData('../v3/manterEmpresaCorrespondente?acao=modificaEmpresa&ECO_CODIGO=' + ecoCodigo + '&ECO_ATIVO=' + ecoAtivo + '&operacao=bloquear&tipo=editar&<%=SynchronizerToken.generateToken4URL(request)%>');
      } 
    }
    function vf_exclusao_empresa(ecoCodigo, ecoNome) {
        url = '../v3/manterEmpresaCorrespondente?acao=modificaEmpresa&ECO_CODIGO=' + ecoCodigo + '&operacao=excluir&tipo=editar&<%=SynchronizerToken.generateToken4URL(request)%>';
        return ConfirmaUrl('<hl:message key="mensagem.confirmacao.exclusao.entidade"/>'.replace('{0}', ecoNome), url);
    }
  </script>
  <script type="text/JavaScript">
  	var f0 = document.forms[0];
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>