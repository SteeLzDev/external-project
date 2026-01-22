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
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  
  List<?> associacoes = (List<?>) request.getAttribute("associacoes");
  List<TransferObject> consignatarias = (List<TransferObject>) request.getAttribute("consignatarias");  
  List<TransferObject> correspondentes = (List<TransferObject>) request.getAttribute("correspondentes");  
  
  String titulo = (String) request.getAttribute("titulo");
  String eco_identificador = (String) request.getAttribute("eco_identificador");
  String eco_nome = (String) request.getAttribute("eco_nome");
  String csa_codigo = (String) request.getAttribute("csa_codigo");
  String cor_codigo = (String) request.getAttribute("cor_codigo");
  String eco_codigo = (String) request.getAttribute("eco_codigo");
  
  
  boolean podeEditar = (boolean) request.getAttribute("podeEditar");
  boolean escondeCampoId = (boolean) request.getAttribute("escondeCampoId");
  boolean podeEditarCor = (boolean) request.getAttribute("podeEditarCor");
  boolean podeConsultarCor = (boolean) request.getAttribute("podeConsultarCor");
%>
<c:set var="title">
  <%=TextHelper.forHtmlContent(titulo)%>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <form method="post" action="../v3/manterEmpresaCorrespondente?acao=associar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
    <div class="card">
        <div class="card-header">
          <h2 class="card-header-title">
            <%=TextHelper.forHtmlContent(eco_identificador + " - " + eco_nome)%>
          </h2>
        </div>
        <div class="card-body">
          <div class="form-group col-sm-6">
            <label for="consignataria"><hl:message key="rotulo.consignataria.singular"/></label>             
            <%=JspHelper.geraCombo(consignatarias, "CSA_CODIGO", Columns.CSA_CODIGO, Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "onChange=\"vf_combo_consignataria('"+ eco_codigo +"');\" class=\"form-control\"", false, 1, csa_codigo)%>
          </div>
          <div class="form-group col-sm-6">
            <label for="consignataria"><hl:message key="rotulo.correspondente.singular"/></label>
            <%=JspHelper.geraCombo(correspondentes, "COR_CODIGO", Columns.COR_CODIGO, Columns.COR_IDENTIFICADOR + ";" + Columns.COR_NOME, ApplicationResourcesHelper.getMessage("rotulo.campo.criar.novo", responsavel), "onChange=\"vf_combo_correspondente('"+ eco_codigo +"');\" class=\"form-control\"", false, 1, cor_codigo)%>
          </div>
  <% if (!escondeCampoId) { %>          
          <div class="form-group col-sm-6">
            <label for="consignataria"><hl:message key="rotulo.codigo.novo.empresa.correspondente"/></label>
            <hl:htmlinput name="COR_IDENTIFICADOR"
              type="text"
              classe="form-control"
              value="<%=TextHelper.forHtmlAttribute( JspHelper.verificaVarQryStr(request, \"COR_IDENTIFICADOR\"))%>"
              mask="#A40"
              others="<%=TextHelper.forHtmlAttribute( !podeEditar ? "disabled" : "")%>"
            />
          </div>
  <% } %>          
        </div>
    </div>
    <div class="btn-action">
      <hl:htmlinput name="tipo"      type="hidden"  value="editar" />
      <hl:htmlinput name="MM_update" type="hidden"  value="form1" />
  <%if (eco_codigo != null) {%>
      <hl:htmlinput name="ECO_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(eco_codigo)%>"/>
  <%}%>
    <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#no-back" onClick="<%="postData('" + TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)) + "');return false;"%>"><hl:message key="rotulo.botao.cancelar" /></a>        
  <%if (podeEditar) {%>
    <a class="btn btn-primary" href="#no-back" onClick="vf_associacao_empresa_cor(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
  <%}%>
    </div>
  </form>
  <div class="card">
    <div class="card-header hasIcone">
      <h2 class="card-header-title">
        <hl:message key="rotulo.consignataria.singular"/>
      </h2>
    </div>
    <div class="card-body p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.nome.empresa.correspondente"/></th>
            <th scope="col"><hl:message key="rotulo.cnpj.empresa.correspondente"/></th>
            <th scope="col"><hl:message key="rotulo.status.empresa.correspondente"/></th>
          </tr>
        </thead>  
        <tbody>
  <%=JspHelper.msgRstVazio(associacoes.size()==0, "10", "lp")%>
  <%
      Iterator<?> itCsa = associacoes.iterator();
      while (itCsa.hasNext()) {
        String classStatusCsa ="";
        TransferObject to = (TransferObject) itCsa.next();
        String csaCodigo = (String) to.getAttribute(Columns.CSA_CODIGO);
        String csaNome = to.getAttribute(Columns.CSA_NOME) != null? (String) to.getAttribute(Columns.CSA_NOME) : "";
        String csaCnpj = to.getAttribute(Columns.CSA_CNPJ) != null? (String) to.getAttribute(Columns.CSA_CNPJ) : "";
        String csaAtivo = to.getAttribute(Columns.CSA_ATIVO) != null? to.getAttribute(Columns.CSA_ATIVO).toString(): "";        
        String msgBloquearDesbloquear = "";
        String msgStatusCsa = "";
        if (csaAtivo.equals("1")) {
            msgBloquearDesbloquear = ApplicationResourcesHelper.getMessage("mensagem.empresa.correspondente.bloquear.clique.aqui", responsavel);
            msgStatusCsa = ApplicationResourcesHelper.getMessage("rotulo.desbloqueado.empresa.correspondente", responsavel);
        } else {
            classStatusCsa ="block";
            msgBloquearDesbloquear = ApplicationResourcesHelper.getMessage("mensagem.empresa.correspondente.desbloquear.clique.aqui", responsavel);
            msgStatusCsa = ApplicationResourcesHelper.getMessage("rotulo.bloqueado.empresa.correspondente", responsavel);
        }
    %>
          <tr>
            <td><%=(String) to.getAttribute(Columns.CSA_IDENTIFICADOR) + " - " + TextHelper.forHtmlContent(csaNome.toUpperCase())%></td>
            <td><%=TextHelper.forHtmlContent(csaCnpj)%></td>
            <td class="<%=TextHelper.forHtmlAttribute(classStatusCsa)%>"><%=TextHelper.forHtmlContent(msgStatusCsa)%></td>
          </tr>
    <% } %>                 
        
        </tbody>
      </table>
    </div>
  </div>
  <div class="card">
    <div class="card-header hasIcone">
      <h2 class="card-header-title">
        <hl:message key="rotulo.correspondente.singular"/>
      </h2>
    </div>
    <div class="card-body p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.nome.empresa.correspondente"/></th>
            <th scope="col"><hl:message key="rotulo.cnpj.empresa.correspondente"/></th>
            <th scope="col"><hl:message key="rotulo.status.empresa.correspondente"/></th>
            <th scope="col"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>  
        <tbody> 
     
  <%=JspHelper.msgRstVazio(associacoes.size()==0, "10", "lp")%>
  <%
    Iterator<?> it = associacoes.iterator();
    while (it.hasNext()) {
      String classStatusCor ="";
      TransferObject to = (TransferObject) it.next();
      String csaCodigo = (String) to.getAttribute(Columns.CSA_CODIGO);
      String csaNome = to.getAttribute(Columns.CSA_NOME) != null? (String) to.getAttribute(Columns.CSA_NOME) : "";
      String csaCnpj = to.getAttribute(Columns.CSA_CNPJ) != null? (String) to.getAttribute(Columns.CSA_CNPJ) : "";
      String csaAtivo = to.getAttribute(Columns.CSA_ATIVO) != null? to.getAttribute(Columns.CSA_ATIVO).toString(): "";
      String corCodigo = (String) to.getAttribute(Columns.COR_CODIGO);
      String corNome = to.getAttribute(Columns.COR_NOME) != null? (String) to.getAttribute(Columns.COR_NOME) : "";
      String corCnpj = to.getAttribute(Columns.COR_CNPJ) != null? (String) to.getAttribute(Columns.COR_CNPJ) : "";
      String corAtivo = to.getAttribute(Columns.COR_ATIVO) != null? to.getAttribute(Columns.COR_ATIVO).toString(): "";
      String msgBloquearDesbloquear = "";
      String msgStatusCor = "";
      if (corAtivo.equals("1")) {
          msgBloquearDesbloquear = ApplicationResourcesHelper.getMessage("mensagem.empresa.correspondente.bloquear.clique.aqui", responsavel);
          msgStatusCor = ApplicationResourcesHelper.getMessage("rotulo.desbloqueado.empresa.correspondente", responsavel);
      } else {
          classStatusCor ="block";
          msgBloquearDesbloquear = ApplicationResourcesHelper.getMessage("mensagem.empresa.correspondente.desbloquear.clique.aqui", responsavel);
          msgStatusCor = ApplicationResourcesHelper.getMessage("rotulo.bloqueado.empresa.correspondente", responsavel);
      }
  %>        
          <tr>
            <td><%=(String) to.getAttribute(Columns.COR_IDENTIFICADOR) + " - " + TextHelper.forHtmlContent(corNome.toUpperCase())%></td>
            <td><%=TextHelper.forHtmlContent(corCnpj)%></td>
            <td class="<%=TextHelper.forHtmlAttribute(classStatusCor)%>"><%=TextHelper.forHtmlContent(msgStatusCor)%></td>            
            <td>
              <div class="actions">
                <a class="dropdown-toggle ico-action" href="#" role="button" id="MASTER" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                  <div class="form-inline">
                    <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.botao.opcoes" />" aria-label="<hl:message key="rotulo.botao.opcoes" />"> <svg>
                        <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                    </span> <hl:message key="rotulo.botao.opcoes" />
                  </div>
                </a>
                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
  <%  if (podeEditarCor) { %>
                  <a class="dropdown-item" href="#no-back" onClick="BloquearEntidade(<%=TextHelper.forJavaScript(corAtivo)%>, '<%=TextHelper.forJavaScript(corCodigo)%>', 'COR', '../v3/manterCorrespondente?acao=bloquear&csa=<%=csaCodigo%>&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(corNome)%>')"><hl:message key="rotulo.acoes.bloquear.desbloquear"/></a>
                  <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterCorrespondente?acao=editar&cor=<%=TextHelper.forJavaScriptAttribute(corCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a>
  <%  } else { %>
  <%    if (podeConsultarCor) { %>
                  <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterCorrespondente?acao=consultar&cor=<%=TextHelper.forJavaScriptAttribute(corCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.consultar"/></a>
  <%    } %>                
  <%  } %>
  <%  if (podeEditar) { %>
                  <a class="dropdown-item" href="#no-back" onClick="vf_exclusao_associacao('<%=TextHelper.forJavaScript(eco_codigo)%>','<%=TextHelper.forJavaScript(csaNome)%>','<%=TextHelper.forJavaScript(corCodigo)%>');" <%=!podeEditar ? "disabled" : ""%>><hl:message key="rotulo.acoes.excluir"/></a>
  <%  } %>                  
                </div>
              </div>
            </td>
          </tr>
  <% } %>          
        </tbody>
      </table>
    </div>
  </div>          
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/validaform.js"></script>
  <script type="text/JavaScript" src="../js/validacoes.js"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
  <script type="text/JavaScript" src="../js/listagem.js"></script>
  <script type="text/JavaScript">
    function formLoad() {
      focusFirstField();
    }
    function vf_combo_consignataria(ecoCodigo) {
      f0.tipo.value = 'consultar';
        if (f0.CSA_CODIGO.value != null && f0.CSA_CODIGO.value != '') {
          f0.MM_update.value = 'carregar';      
          postData('../v3/manterEmpresaCorrespondente?acao=detalharAssociacao&tipo=consultar&MM_update=carregar&ECO_CODIGO=' + ecoCodigo + '&CSA_CODIGO=' + f0.CSA_CODIGO.value + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');
        } else {
          f0.MM_update.value = 'esconder';      
          f0.COR_CODIGO.value = '';
          postData('../v3/manterEmpresaCorrespondente?acao=detalharAssociacao&tipo=consultar&MM_update=esconder&ECO_CODIGO=' + ecoCodigo + '&CSA_CODIGO=' + f0.CSA_CODIGO.value + '&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');
        }
    }
    function vf_combo_correspondente(ecoCodigo) {
      f0.tipo.value = 'consultar';
        if (f0.COR_CODIGO.value != null && f0.COR_CODIGO.value != '') {
          f0.MM_update.value = 'esconder';   
          f0.COR_IDENTIFICADOR.value = '';
          postData('../v3/manterEmpresaCorrespondente?acao=detalharAssociacao&MM_update=esconder&tipo=consultar&ECO_CODIGO=' + ecoCodigo + '&COR_CODIGO=' + f0.COR_CODIGO.value + '&CSA_CODIGO=' + f0.CSA_CODIGO.value + '&<%=SynchronizerToken.generateToken4URL(request)%>');
        } else {
          f0.MM_update.value = 'carregar';      
          postData('../v3/manterEmpresaCorrespondente?acao=detalharAssociacao&MM_update=carregar&tipo=consultar&ECO_CODIGO=' + ecoCodigo + '&COR_CODIGO=' + f0.COR_CODIGO.value + '&CSA_CODIGO=' + f0.CSA_CODIGO.value + '&<%=SynchronizerToken.generateToken4URL(request)%>');
        }
    }
    function vf_associacao_empresa_cor() {
      var Controles = new Array("CSA_CODIGO");
      var Msgs = new Array('<hl:message key="mensagem.aviso.consignataria.obrigatoria.empresa.correspondente"/>');
      
      if (ValidaCampos(Controles, Msgs)) {
        <% 
        Iterator itAss = associacoes.iterator();
        while (itAss.hasNext()) {
          TransferObject to = (TransferObject) itAss.next();
          String codigoCsa = (String) to.getAttribute(Columns.CSA_CODIGO);
        %>  
          if (f0.CSA_CODIGO.value == '<%=TextHelper.forJavaScriptBlock(codigoCsa)%>') {
            alert('<hl:message key="mensagem.erro.consignataria.associada.empresa.correspondente"/>');
            f0.CSA_CODIGO.focus();
            return false;    
          }
        <% } %>
        if (f0.COR_CODIGO.value != null && f0.COR_CODIGO.value != '') {
          if (!confirm('<hl:message key="mensagem.empresa.correspondente.confirma"/>')) {
            return false;
          }  
        }
        f0.MM_update.value = 'salvar';       
        f0.submit();
      }
    }
    function vf_exclusao_associacao(ecoCodigo, csaNome, corCodigo) {
    	url = '../v3/manterEmpresaCorrespondente?acao=associar&ECO_CODIGO=' + ecoCodigo + '&COR_CODIGO=' + corCodigo + '&operacao=excluir&tipo=editar&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>';    
        return ConfirmaUrl('<hl:message key="mensagem.empresa.correspondente.exclusao.confirma"/>'.replace("{0}", csaNome), url);
    }
  </script>
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