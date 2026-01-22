<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.dto.web.DadosConsignataria"%>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.StatusConsignatariaEnum"%>
<%@ page import="com.zetra.econsig.values.NaturezaConsignatariaEnum"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String filtro = (String) request.getAttribute("filtro");
String filtro2 = (String) request.getAttribute("filtro2");

boolean podeEditarCsa = (Boolean) request.getAttribute("podeEditarCsa");
boolean podeExcluirCsa = (Boolean) request.getAttribute("podeExcluirCsa");
boolean podeConsultarCsa = (Boolean) request.getAttribute("podeConsultarCsa");
boolean podeEditarEnderecoAcesso = (Boolean) request.getAttribute("podeEditarEnderecoAcesso");
boolean podeConsultarPerfilUsu = (Boolean) request.getAttribute("podeConsultarPerfilUsu");
boolean podeConsultarUsu = (Boolean) request.getAttribute("podeConsultarUsu");
boolean podeCriarUsu = (Boolean) request.getAttribute("podeCriarUsu");
boolean podeConsultarCor = (Boolean) request.getAttribute("podeConsultarCor");
boolean podeEditarCor = (Boolean) request.getAttribute("podeEditarCor");
boolean podeEditarSvc = (Boolean) request.getAttribute("podeEditarSvc");
boolean podeConsultarSvc = (Boolean) request.getAttribute("podeConsultarSvc");
boolean temPenalidade = (Boolean) request.getAttribute("temPenalidade");
boolean cadastraEmpCor = (Boolean) request.getAttribute("cadastraEmpCor");
boolean podeEditarParamCsa = (Boolean) request.getAttribute("podeEditarParamCsa");
boolean podeEditarEnderecosCsa = (Boolean) request.getAttribute("podeEditarEnderecosCsa");

String linkImpressao = (String) request.getAttribute("linkImpressao");

int filtro_tipo = (Integer) request.getAttribute("filtro_tipo");

List <DadosConsignataria> consignatariaDTOs = (List <DadosConsignataria>) request.getAttribute("consignatariaDTOs");
List lstNatureza = (List) request.getAttribute("lstNatureza");
String ncaCodigoSelecionado = (String) request.getAttribute("ncaCodigoSelecionado") != null ? (String) request.getAttribute("ncaCodigoSelecionado") : "";
//Exibe Botao Rodapé
boolean exibeBotaoRodape = request.getAttribute("exibeBotaoRodape") != null && (boolean) request.getAttribute("exibeBotaoRodape");
String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
%>
<c:set var="title">
<hl:message key="rotulo.listar.consignataria.titulo"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent"> 
	<div id="header-print">
		<% if ("v4".equals(versaoLeiaute)) { %>
			<img src="../img/econsig-logo.svg">
		<% } else { %>
			<img src="../img/logo_sistema_v5.png">
		<%} %>
		<p id="date-time-print"></p>
	</div>
   <div class="page-title">
        <div class="row d-print-none">
          <div class="col-sm-12 col-md-12 mb-2">
            <div class="btn-action">
              <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit"><hl:message key="rotulo.mais.acoes"/></button>
              <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes">
                <a class="dropdown-item" HREF="#no-back" onClick="postData('../v3/manterConsignataria?acao=editarConsignataria&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.criar.nova.consignataria"/></a>
                <%if (responsavel.isCseSup() && responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNATARIAS)){ %>
                	<a class="dropdown-item" HREF="#no-back" onClick="postData('../v3/processarLoteCadastrarConsignatarias?acao=iniciar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.lote.criar.novas.consignatarias"/></a>
                <%} %>
                	<a class="dropdown-item" href="#no-back" onclick=" injectDate(); postData('<%=TextHelper.forJavaScriptAttribute(linkImpressao)%>'); return false;"><hl:message key="rotulo.botao.imprimir"/></a>
              </div>
            </div>
          </div>
        </div>
   </div>
   <div class="row firefox-print-fix">
      <div class="col-sm-5 col-md-4 d-print-none">                    
          <div class="card">
            <div class="card-header hasIcon pl-3">
              <h2 class="card-header-title"><hl:message key="rotulo.botao.pesquisar"/></h2>
            </div>
            <div class="card-body">
              <form NAME="form1" METHOD="post" ACTION="../v3/manterConsignataria">
                <input type="hidden" name="acao" value="iniciar">
                <div class="row">
                  <div class="form-group col-sm">
                    <label for="FILTRO"><hl:message key="rotulo.acoes.filtrar"/> <hl:message key="rotulo.consignataria.singular"/></label>
                    <input type="text" class="form-control" id="FILTRO" name="FILTRO" value="<%=filtro_tipo != 4 ? TextHelper.forHtmlAttribute(filtro) : ""%>" placeholder="<hl:message key="rotulo.acao.digite.filtro"/>" <%=filtro_tipo != 4 ? "" : "disabled" %>>
                  </div>
                </div>
                <div class="row">
                  <div class="form-group col-sm">
                    <label for="FILTRO_TIPO"><hl:message key="rotulo.acao.filtrar.por"/></label>
                    <select class="form-control form-select select" id="FILTRO_TIPO" name="FILTRO_TIPO" onchange="validaFiltroTipo()">
                      <optgroup label="<%=ApplicationResourcesHelper.getMessage("rotulo.filtro.plural", responsavel)%>:">
                        <option value="" <%=(String)((filtro_tipo == -1) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.sem.filtro"/></option>
                        <option VALUE="02" <%=(String)((filtro_tipo ==  2) ? "SELECTED" : "")%>><hl:message key="rotulo.consignataria.codigo"/></option>
                        <option VALUE="03" <%=(String)((filtro_tipo ==  3) ? "SELECTED" : "")%>><hl:message key="rotulo.consignataria.nome"/></option>
                        <% if (!lstNatureza.isEmpty()) { %>
                          <option VALUE="04" <%=(String)((filtro_tipo ==  4) ? "SELECTED" : "")%>><hl:message key="rotulo.consignataria.natureza"/></option>
                        <% } %>
                        <option VALUE="05" <%=(String)((filtro_tipo ==  5) ? "SELECTED" : "")%>><hl:message key="rotulo.consignataria.codigo.verba"/></option>
                        <option VALUE="00" <%=(String)((filtro_tipo ==  0) ? "SELECTED" : "")%>><hl:message key="rotulo.consignataria.filtro.bloqueado"/></option>
                        <option VALUE="01" <%=(String)((filtro_tipo ==  1) ? "SELECTED" : "")%>><hl:message key="rotulo.consignataria.filtro.desbloqueado"/></option>
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
              
                  <div class="row" id="rowNcaCodigo" style="display: <%=filtro_tipo == 4 ? "" : "none" %>">
                     <div class="form-group col-sm">
                       <label for="NCA_CODIGO"><hl:message key="rotulo.consignataria.natureza"/></label>
                       <select class="form-control form-select" id="NCA_CODIGO" name="NCA_CODIGO">
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
          <div class="btn-action d-print-none">
            <a class="btn btn-primary" href="#no-back" onClick="filtrar();">
              <svg width="20">
                <use xlink:href="#i-consultar"></use></svg>
              <hl:message key="rotulo.botao.pesquisar"/>
            </a>
          </div>
      </div>
      <div class="col-sm-7 col-md-8">             
             <div class="card">
                <div class="card-header hasIcon pl-3">
                  <h2 class="card-header-title"><hl:message key="rotulo.consignataria.plural"/></h2>
                </div>
                <div class="card-body table-responsive ">
                  <div class="row mr-0 pl-3 pr-3 pt-2 pb-0 d-print-none">
                     <div class="col-sm-12">
                       <div class="form-group mb-1">
                         <span><hl:message key="rotulo.filtrar.inicial.nome"/></span>
                       </div>
                     </div>
                     <div class="col-sm-12">
                        <%
                           for (int i='A'; i <='Z'; i++) {
                              out.print((filtro2.equals(String.valueOf((char)i)) ? String.valueOf((char)i) : "<a href=\"#no-back\" onClick=\"postData('../v3/manterConsignataria?acao=iniciar&FILTRO2=" + String.valueOf((char)i) + "')\">" + String.valueOf((char)i) + "</a>") + " - ");
                           }
                           out.print(filtro2.equals("") ? ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel) : "<a href=\"#no-back\" onClick=\"postData('../v3/manterConsignataria?acao=iniciar')\">" + ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel) + "</a>");
                        %>
                     </div>
                  </div>
                  <div class="pt-3 table-responsive">
                  <table class="table table-striped table-hover">
                     <thead>
                        <tr>
                          <th scope="col"><hl:message key="rotulo.consignataria.codigo"/></th>
                          <th scope="col"><hl:message key="rotulo.consignataria.nome"/></th>
                          <th scope="col"><hl:message key="rotulo.consignataria.nome.abreviado"/></th>
                          <th scope="col"><hl:message key="rotulo.consignataria.status"/></th>
                          <th scope="col"><hl:message key="rotulo.acoes"/></th>
                        </tr>
                     </thead>
                     <tbody>                      
                        <%=JspHelper.msgRstVazio(consignatariaDTOs.size() == 0, 5, responsavel)%>
                        <%              
                            Iterator <DadosConsignataria> it = consignatariaDTOs.iterator();
                            while (it.hasNext()) {
                               DadosConsignataria consignataria = (DadosConsignataria)it.next();
                               String csa_codigo = consignataria.getCsaCodigo();
                               String csa_nome = consignataria.getCsaNome();
                               String csa_nome_abrev = consignataria.getCsaNomeAbrev();
                               String csa_identificador = consignataria.getCsaIdentificador();
                               String csa_ativo = consignataria.getCsaAtivo();
                               String mensagemDesbloqueio = consignataria.getMensagemDesbloqueio();
                               String csa_nome_abrev_script = consignataria.getCsaNomeAbrevScript();
                               String descStatus = consignataria.getCsaStatusDescricao();
                        %>
                      <tr>  
                        <td><%=TextHelper.forHtmlContent(csa_identificador)%></td>
                        <td><%=TextHelper.forHtmlContent(csa_nome.toUpperCase())%></td>
                        <td><%=TextHelper.forHtmlContent(csa_nome_abrev)%></td>
                        <td <%=StatusConsignatariaEnum.recuperaStatusConsignataria(csa_ativo).isBloqueado() ? "class=\"block\"" : ""%>><%=TextHelper.forHtmlContent(descStatus)%></td>
                        <td>
                           <div class="actions">
                             <div class="dropdown">
                                <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                  <div class="form-inline">
                                     <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>"> <svg>
                                       <use xlink:href="#i-engrenagem"></use></svg>
                                     </span><hl:message key="rotulo.botao.opcoes"/>
                                  </div>
                                </a>
                                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                                  <% if (podeEditarCsa) { %>                                     
                                     <a class="dropdown-item" href="#no-back" onClick="BloquearEntidade(<%=TextHelper.forJavaScript(csa_ativo)%>, '<%=TextHelper.forJavaScript(csa_codigo)%>', 'CSA', '../v3/manterConsignataria?acao=bloquear&csa_nome=<%=TextHelper.forJavaScript(csa_nome)%>&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(csa_nome_abrev_script )%>', '<%=TextHelper.forJavaScript(mensagemDesbloqueio != null ? mensagemDesbloqueio : "")%>')"><hl:message key="rotulo.acao.bloquear.desbloquear"/></a>                                     
                                     <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterConsignataria?acao=editarConsignataria&csa=<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.editar"/></a>
                                     <% if (podeExcluirCsa) { %>
                                        <a class="dropdown-item" href="#no-back" onClick="ExcluirEntidade('<%=TextHelper.forJavaScript(csa_codigo)%>', 'CSA', '../v3/manterConsignataria?acao=excluir&csa_nome=<%=TextHelper.forJavaScript(csa_nome)%>&<%=SynchronizerToken.generateToken4URL(request)%>', '<%=TextHelper.forJavaScript(csa_nome_abrev_script)%>')"><hl:message key="rotulo.acoes.excluir"/></a>
                                     <% } %>
                                  <% } else if (podeConsultarCsa) { %>
                                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterConsignataria?acao=editarConsignataria&tipo=consultar&csa=<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                                           <% if (podeEditarEnderecoAcesso) { %>
                                              <hl:message key="rotulo.acoes.editar"/>
                                           <% } else { %>
                                              <hl:message key="rotulo.acao.pesquisar"/>
                                           <% } %>   
                                        </a>
                                  <% } %>
                                  <% if (podeConsultarPerfilUsu) { %>
                                     <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarPerfilCsa?acao=listar&codigo=<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>&titulo=<%=TextHelper.forJavaScript(csa_nome_abrev_script)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acao.listar.perfil.usuario"/></a>
                                  <% } %>
                                  <% if (podeConsultarUsu) { %>
                                     <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarUsuarioCsa?acao=listar&codigo=<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>&titulo=<%=TextHelper.encode64(csa_nome_abrev_script)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.usuario.plural"/></a>
                                  <% } %>
                                  <% if (podeCriarUsu) { %>
                                     <a class="dropdown-item" href="#no-back" onClick="postData('../v3/inserirUsuarioCsa?acao=iniciar&codigo=<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>&titulo=<%=TextHelper.encode64(csa_nome_abrev_script)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.novo.usuario"/></a>
                                  <% } %>
                                  <% if (podeConsultarCor || podeEditarCor) { %>
                                     <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterCorrespondente?acao=iniciar&csa=<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>&titulo=<%=TextHelper.forJavaScript(csa_nome_abrev_script)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.correspondente.reduzido"/></a>
                                  <% } %>
                                  <% if (podeEditarCor) {
                                        if (!cadastraEmpCor) {
                                  %>
                                           <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterCorrespondente?acao=consultar&csa=<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>&titulo=<%=TextHelper.forJavaScript(csa_nome_abrev_script)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.criar.correspondente.reduzido"/></a>
                                  <%    } else { %>
                                           <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterCorrespondente?acao=pesquisarEmpCorrespondente&tipo=consultar&csa=<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>&titulo=<%=TextHelper.forJavaScript(csa_nome_abrev_script)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.criar.correspondente.reduzido"/></a>
                                  <%    }
                                     }   
                                  %>
                                  <% if (podeEditarSvc||podeConsultarSvc) { %>
                                     <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterServico?acao=listarServicosCsa&csa=<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>&titulo=<%=TextHelper.forJavaScript(csa_nome_abrev_script)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.servico.plural"/></a>
                                  <% } %>
                                  <% if (podeEditarCsa && temPenalidade) { %>
                                     <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterConsignataria?codigo=<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>&CSA_NOME=<%=TextHelper.forJavaScript(csa_nome_abrev_script)%>&acao=penalizar&bloqueado=<%=TextHelper.forJavaScript(csa_ativo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.consignataria.penalidade"/></a>
                                  <% } %>
                                  <% if ((responsavel.isCseSup() || responsavel.isCsa()) && podeEditarParamCsa) { %>
                                     <a class="dropdown-item" href="#no-back" onClick="postData('../v3/editarParamConsignataria?acao=iniciar&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>&titulo=<%=TextHelper.forJavaScript(csa_nome_abrev_script)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.consignataria.editar.parametros.abrev"/></a>
                                  <% } %>
                                  <% if (podeEditarEnderecosCsa) { %>
                                     <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterEnderecosConsignataria?acao=iniciar&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>&titulo=<%=TextHelper.forJavaScript(csa_nome_abrev_script)%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.consignataria.editar.enderecos"/></a>
                                  <% } %>
                               </div>
                            </div>
                           </div>
                        </td>
                      </tr> 
                        <%              
                            }
                        %>
                     </tbody>
                     <tfoot>
                       <tr><td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.consignatarias", responsavel) + " - " %><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td></tr>
                     </tfoot>
                  </table>
                  </div> 
                </div>
                <div class="card-footer">
                  <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
                </div>                
             </div>
            <div class="btn-action">
               <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
            </div>   
          </div>
      </div>
	  <% if ("v4".equals(versaoLeiaute)) { %>
	    <div id="footer-print">
	  		<img src="../img/footer-logo.png">
	    </div>
	  <% } else { %>
	  	<div id="footer-print">
	  		<img src="../img/footer-logo-v5.png">
	  	</div>
	  <%} %>
    <% if (exibeBotaoRodape) { %>
    <div id="btns">
        <a id="page-up" onclick="up()">
            <svg xmlns="http://www.w3.org/2000/svg" width="147.344" height="147.344" viewBox="0 0 147.344 147.344">
                <path id="União_3" data-name="União 3" d="M-20,60.672a73.672,73.672,0,1,1,73.672,73.672A73.66,73.66,0,0,1-20,60.672ZM9.61,16.61a62.252,62.252,0,0,0,0,88.124,62.252,62.252,0,0,0,88.124,0,62.252,62.252,0,0,0,0-88.124,62.252,62.252,0,0,0-88.124,0ZM49.174,88.087,15.278,54.3a6.85,6.85,0,0,1,9.67-9.706L53.672,73.6,82.362,45.336A6.825,6.825,0,1,1,92,55.005L58.6,88.3a6.827,6.827,0,0,1-9.421-.212Z" transform="translate(20 13)"/>
            </svg>
        </a>
        <a id="page-down" onclick="down()">
            <svg xmlns="http://www.w3.org/2000/svg" width="147.344" height="147.344" viewBox="0 0 147.344 147.344">
                <path id="União_3" data-name="União 3" d="M-20,60.672a73.672,73.672,0,1,1,73.672,73.672A73.66,73.66,0,0,1-20,60.672ZM9.61,16.61a62.252,62.252,0,0,0,0,88.124,62.252,62.252,0,0,0,88.124,0,62.252,62.252,0,0,0,0-88.124,62.252,62.252,0,0,0-88.124,0ZM49.174,88.087,15.278,54.3a6.85,6.85,0,0,1,9.67-9.706L53.672,73.6,82.362,45.336A6.825,6.825,0,1,1,92,55.005L58.6,88.3a6.827,6.827,0,0,1-9.421-.212Z" transform="translate(20 13)"/>
            </svg>
        </a>
    </div>
    <% }%>
</c:set>
<c:set var="javascript">
	<style>
		  @media print {
			*{
				padding: 0;
				margin: 0;
				color: #000 !important;
			}
			body{color: #000 !important}
		    table th:last-child {display: none;}
		    table td:last-child {display: none;}
		  	#menuAcessibilidade {display: none;}
			#dataTables_length {display: none;}
			#dataTables_paginate {display: none;}
			#dataTables_filter {display: none;}	
			#dataTables_info {display: none;}
		    #active-buttons {display: none;}
		    #menuAcessibilidade {display: none;}
	        #header-print img{width: 10%;} 
	        #footer-print {position: absolute; bottom: 0;}   
		    .opcoes-avancadas {display: none;}
			.table thead th {
				padding: 0 .75rem;
			}
		
		    .table thead tr th, .table tbody tr td {
		      font-size: 12px;
		      line-height: 1.25;
		      padding-top: 0;
		      padding-bottom: 0;
		      padding-left: .25rem;
		      padding-left: .25rem;
		      color: #000 !important;
		      border-left: 1px solid #000 !important;
		    }
		  }
		@page{margin: 0.5cm;}
	</style>
<script type="text/JavaScript">
	f0 = document.forms[0];
	
	function injectDate(){
		const dateTimePrint = document.querySelector('#date-time-print');
		const printDate = new Date();
		printDate.toLocaleString("pt-br");
		dateTimePrint.innerHTML = new Intl.DateTimeFormat('pt-BR', {dateStyle:'short', timeStyle:'long'}).format(printDate);	
	}
	
	function filtrar() {
	   f0.submit();
	}
	
	function validaFiltroTipo() {
	   var valorFiltro = document.getElementById("FILTRO_TIPO").value;
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
<script>
    <% if (exibeBotaoRodape) { %>
        let btnDown = document.querySelector('#btns');
        const pageActions = document.querySelector('#page-actions');
        const pageSize = document.body.scrollHeight;

        function up(){
            window.scrollTo({
                top: 0,
                behavior: "smooth",
            });
        }

        function down(){
            let toDown = document.body.scrollHeight;
            window.scrollBy({
                top: toDown,
                behavior: "smooth",
            });
        }

        function btnTab(){
            let scrollSize = document.documentElement.scrollTop;
            if(scrollSize >= 100){
                btnDown.classList.add('btns-active');
            } else {
                btnDown.classList.remove('btns-active');
            }
        }

        window.addEventListener('scroll', btnTab);
        <% } %>
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>