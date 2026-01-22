<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TransferObjectHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.ParamSvcTO"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean podeCriarComunicacao  = (boolean) request.getAttribute("podeCriarComunicacao");

List consignatarias = (List) request.getAttribute("listConsignatarias");
List orgaosTO       = (List) request.getAttribute("orgaosTO");
List assuntos       = (List) request.getAttribute("assuntos");
List comunicacoes   = (List) request.getAttribute("comunicacoes");

String tituloPagina = (String) request.getAttribute("tituloPagina");
String rotuloSim    = (String) request.getAttribute("rotuloSim");
String rotuloNao    = (String) request.getAttribute("rotuloNao");
String csaCodigo    = (String) request.getAttribute("csaCodigo");
String corCodigo    = (String) request.getAttribute("corCodigo");
String orgCodigo    = (String) request.getAttribute("orgCodigo");
String serCodigo    = (String) request.getAttribute("serCodigo");
String pendencia    = (String) request.getAttribute("pendencia");
String exibeSomenteCse    = (String) request.getAttribute("exibeSomenteCse");
String filtroLeitura      = (String) request.getAttribute("filtroLeitura");
String filtroRelacaoAde = (String) request.getAttribute("filtroRelacaoAde");
String ascCodigo     = (String) request.getAttribute("ascCodigo");
String filtroDataFim = (String) request.getAttribute("filtroDataFim");
String filtroDataIni = (String) request.getAttribute("filtroDataIni");

%>
<c:set var="title">
  <%=TextHelper.forHtml(tituloPagina)%>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set> 
<c:set var="bodyContent">
    <%if (podeCriarComunicacao) {%>
      <div class="btn-action">
        <a class="btn btn-primary" href="#" onClick="postData('../v3/enviarComunicacao?acao=enviar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.criar.comunicacao.titulo"/></a>
      </div>
    <%}%>
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="mensagem.pesquisa.informe.dados"/></h2>
        </div>
        <div class="card-body">
           <form action="../v3/enviarComunicacao?acao=listar&<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="form1">
           <input type="hidden" name="pesquisar" value="true" />
           
              <%
              if (responsavel.isCseSup()) {
              %>
               <div class="form-group" role="radiogroup" aria-labelledby="exibirComunicacaoDoGestor">
                   <div><span id="exibirComunicacaoDoGestor"><hl:message key="rotulo.consultar.comunicacao.somente.gestor"/></span>
                   </div>
                   <div class="form-check form-check-inline">
                       <input class="form-check-input ml-1" TYPE="radio" NAME="exibeSomenteCse" VALUE="1" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)((!TextHelper.isNull(exibeSomenteCse) && exibeSomenteCse.equals("1")) ? "CHECKED":"")%>>
                       <label class="form-check-label labelSemNegrito ml-1 pl-0 pr-1" for="exibirSim"><%=(String)rotuloSim%></label>
                   </div>
                   <div class="form-check form-check-inline">
                       <input class="form-check-input ml-1" TYPE="radio" NAME="exibeSomenteCse" VALUE="0" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)((!TextHelper.isNull(exibeSomenteCse) && exibeSomenteCse.equals("0")) ? "CHECKED":"")%>>
                       <label class="form-check-label labelSemNegrito ml-1 pl-0 pr-1" for="exibirNao"><%=(String)rotuloNao%></label>
                   </div>
               </div>
              <%
              }
              %>
              <%
              if (responsavel.isCseSupOrg() || responsavel.isSer()) { //mostra combo para selecao de consignataria
              %>
              <div class="row">
                <div class="form-group col-sm-12  col-md-6">
                  <label for="consignataria"><hl:message key="rotulo.consignataria.singular"/></label>
                    <select class="form-control form-select" name="CSA_CODIGO" id="CSA_CODIGO" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);">
                      <option value="" selected><hl:message key="rotulo.campo.todas"/></option>
                      <%
                       Iterator it = consignatarias.iterator();
                       CustomTransferObject csa = null;
                       String csaCodigo2, csaNome, csaId;
                       String selected = csaCodigo;
                       while (it.hasNext()) {
                         csa = (CustomTransferObject)it.next();
                         csaCodigo2 = (String)csa.getAttribute(Columns.CSA_CODIGO);
                         csaId = (String) csa.getAttribute(Columns.CSA_IDENTIFICADOR);
                         csaNome = (String) csa.getAttribute(Columns.CSA_NOME);
                         if (csaNome.length() > 50){
                            csaNome = csaNome.substring(0, 47) + "...";
                         }
                      %>
                           <option value="<%=TextHelper.forHtmlAttribute(csaCodigo2)%>" <%=(String)((selected.equals(csaCodigo2)) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(csaNome)%> - <%=TextHelper.forHtmlContent(csaId)%></option>
                      <% } %>
                    </select>
                  </div>
              </div>
              <%}
              %>
              <%
                  // combo para seleção de órgão
                  if (responsavel.isCsaCor() || responsavel.isCseSup()) {
              %>
                 <div class="row">
                    <div class="form-group col-sm-12  col-md-6">
                      <label for="orgao"><hl:message key="rotulo.orgao.singular"/></label>
                         <%=JspHelper.geraCombo(orgaosTO, "ORG_CODIGO", Columns.ORG_CODIGO, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, orgCodigo, "", false, "form-control") %>
                    </div>          
                  </div>       
              <%
                  }
              %>                           
              <div class="row">
                <div class="form-group col-sm-12  col-md-6">
                  <label for="categoriaAssunto"><hl:message key="rotulo.comunicacao.categoria"/></label>
                  
                   <select class="form-control form-select" name="ASC_CODIGO" id="ASC_CODIGO" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);" >
                      <option value="" selected><hl:message key="rotulo.campo.todas"/></option>
                      <%
                       Iterator iteratorAssunto = assuntos.iterator();
                       CustomTransferObject asc = null;
                       String ascDescricao;
                       String selectedAssunto = JspHelper.verificaVarQryStr(request, "ASC_CODIGO");
                       while (iteratorAssunto.hasNext()) {
                         asc = (CustomTransferObject)iteratorAssunto.next();
                         ascCodigo = (String)asc.getAttribute(Columns.ASC_CODIGO);
                         ascDescricao = asc.getAttribute(Columns.ASC_DESCRICAO).toString();
                       %>
                         <option value="<%=TextHelper.forHtmlAttribute(ascCodigo)%>" <%=(String)((selectedAssunto.equals(ascCodigo)) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(ascDescricao)%></option>
                    <% } %>
                    </select>
                  </div>
                </div>
                                    
                <div class="row">
                  <div class="form-group col-sm-12 col-md-6">
                    <label for="dataDeConfirmacaoDaLeitura labelSemNegrito ml-1"><hl:message key="rotulo.consultar.comunicacao.data"/></label>    
                    <div class="row mt-2" role="group" aria-labelledby="dataDeConfirmacao">
                      <div class="form-check pt-2 col-sm-12 col-md-1">
                        <div class="float-left align-middle mt-4 form-control-label">
                          <label for="dataDeComunicacaoDE" class="labelSemNegrito"><hl:message key="rotulo.data.de"/></label>
                          </div>
                        </div>            
                        <div class="form-check pt-2 col-sm-12 col-md-5">
                          <hl:htmlinput name="periodoIni" di="periodoIni" type="text" classe="form-control w-100" size="10" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(filtroDataIni)%>" />
                        </div>           
                        <div class="form-check pt-2 col-sm-12 col-md-1">
                          <div class="float-left align-middle mt-4 form-control-label">
                            <label for="dataDeComunicacaoA" class="labelSemNegrito"><hl:message key="rotulo.data.ate"/></label>
                          </div>
                        </div>
                        <div class="form-check pt-2 col-sm-12 col-md-5">
                          <hl:htmlinput name="periodoFim" di="periodoFim" type="text" classe="form-control w-100" size="10" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(filtroDataFim)%>" />
                      </div>
                    </div>
                  </div>
                </div>

<!--               Inclui o campo de matrícula -->
              <%if (!responsavel.isSer()) { %>
                <div class="row">
                  <div class="col-sm-6">
                   <%@ include file="../consultarMargem/include_campo_matricula_v4.jsp" %>
                  </div>
                </div>
                <div class="row">
                 <div class="form-group col-sm-6">  
                  <hl:campoCPFv4
                   placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf.completo", responsavel) %>"
                  />
                 </div>
                </div>
              <%} %>


              <div class="row">
                <div class="form-group col-sm-6">
                  <label for="identificadorComunicacao"><hl:message key="rotulo.consultar.comunicacao.identificador"/></label>
                  <hl:htmlinput name="CMN_NUMERO"
                                di="CMN_NUMERO" 
                                type="text" 
                                classe="form-control"
                                mask="#D20" 
                                size="20"
                                placeHolder="<%=TextHelper.forHtmlAttribute(ApplicationResourcesHelper.getMessage("mensagem.comunicacao.digite.identificador", responsavel))%>"
                                value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "CMN_NUMERO"))%>" 
                                nf="btnEnvia" 
                                
                     />
                </div>
              </div>
              <div class="form-group" role="radiogroup" aria-labelledby="comunicacaoPendente">
                  <div>
                  <span id="exibirComunicacaoDoGestor"><hl:message key="rotulo.consultar.comunicacao.pendente"/></span>
                  </div>
                  <div class="form-check form-check-inline pt-2">
                    <input class="form-check-input ml-1" type="radio" name="pendencia" id="pendenteSim" value="1" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)((!TextHelper.isNull(pendencia) && pendencia.equals("1")) ? "CHECKED":"")%>>
                    <label class="form-check-label labelSemNegrito ml-1 pl-4 pr-1" for="pendenteSim"><%=(String)rotuloSim%></label>
                    </div>
                    <div class="form-check form-check-inline pt-2">
                    <input class="form-check-input ml-1" type="radio" name="pendencia" id="pendenteNao" value="0" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)((!TextHelper.isNull(pendencia) && pendencia.equals("0")) ? "CHECKED":"")%>>
                    <label class="form-check-label labelSemNegrito ml-1 pl-4 pr-1" for="pendenteNao"><%=(String)rotuloNao%></label>
                    </div>
                    <div class="form-check form-check-inline pt-2">
                    <input class="form-check-input ml-1" type="radio" name="pendencia" id="pendenteTodos" value="2" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)((TextHelper.isNull(pendencia) || pendencia.equals("2")) ? "CHECKED":"")%>>
                    <label class="form-check-label labelSemNegrito ml-1 pl-4 pr-1" for="pendenteTodos"><hl:message key="rotulo.campo.todos.simples"/></label>
                  </div>
               </div>
               
               <div class="form-group" role="radiogroup" aria-labelledby="comunicacaoPendente">
                <div>
                   <span id="exibirComunicacaoDoGestor"><hl:message key="rotulo.consultar.comunicacao.lida"/></span>
                </div>
                    <div class="form-check form-check-inline pt-2">
                      <input class="form-check-input ml-1" type="radio" name="lida" id="lidaSim" value="1" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)((!TextHelper.isNull(filtroLeitura) && filtroLeitura.equals("1")) ? "CHECKED":"")%>>
                      <label class="form-check-label labelSemNegrito ml-1 pl-4 pr-1" for="lidaSim"><%=(String)rotuloSim%></label>
                      </div>
                      <div class="form-check form-check-inline pt-2">
                      <input class="form-check-input ml-1" type="radio" name="lida" id="lidaNao" value="0" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)((!TextHelper.isNull(filtroLeitura) && filtroLeitura.equals("0")) ? "CHECKED":"")%>>
                      <label class="form-check-label labelSemNegrito ml-1 pl-4 pr-1" for="lidaNao"><%=(String)rotuloNao%></label>
                      </div>
                      <div class="form-check form-check-inline pt-2">
                      <input class="form-check-input ml-1" type="radio" name="lida" id="lidaTodos" VALUE="2" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)((TextHelper.isNull(filtroLeitura) || filtroLeitura.equals("2")) ? "CHECKED":"")%>>
                      <label class="form-check-label labelSemNegrito ml-1 pl-4 pr-1" for="lidaTodos"><hl:message key="rotulo.campo.todos.simples"/></label>
                    </div>
               </div>
               
               <div class="form-group" role="radiogroup" aria-labelledby="comunicacaoRelacionadaAde">
                <div>
                   <span id="exibircomunicacaoRelacionadaAde"><hl:message key="rotulo.consultar.comunicacao.relacionada.ade"/></span>
                </div>
                    <div class="form-check form-check-inline pt-2">
                      <input class="form-check-input ml-1" type="radio" name="existeAdeRelacionada" id="existeAdeRelacionadaSim" value="1" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)((!TextHelper.isNull(filtroRelacaoAde) && filtroRelacaoAde.equals("1")) ? "CHECKED":"")%>>
                      <label class="form-check-label labelSemNegrito ml-1 pl-4 pr-1" for="existeAdeRelacionadaSim"><%=(String)rotuloSim%></label>
                      </div>
                      <div class="form-check form-check-inline pt-2">
                      <input class="form-check-input ml-1" type="radio" name="existeAdeRelacionada" id="existeAdeRelacionadaNao" value="0" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)((!TextHelper.isNull(filtroRelacaoAde) && filtroRelacaoAde.equals("0")) ? "CHECKED":"")%>>
                      <label class="form-check-label labelSemNegrito ml-1 pl-4 pr-1" for="existeAdeRelacionadaNao"><%=(String)rotuloNao%></label>
                      </div>
                      <div class="form-check form-check-inline pt-2">
                      <input class="form-check-input ml-1" type="radio" name="existeAdeRelacionada" id="existeAdeRelacionadaTodos" VALUE="2" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" <%=(String)((TextHelper.isNull(filtroRelacaoAde) || filtroRelacaoAde.equals("2")) ? "CHECKED":"")%>>
                      <label class="form-check-label labelSemNegrito ml-1 pl-4 pr-1" for="existeAdeRelacionadaTodos"><hl:message key="rotulo.campo.todos.simples"/></label>
                    </div>
               </div>
            </form>
         </div>
      </div>
     <div class="btn-action">
      <a class="btn btn-outline-danger" href="#" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.cancelar"/></a>
        <a class="btn btn-primary" name="btnEnvia"  id="btnEnvia" href="#" onClick="return validForm();">
          <svg width="20">
            <use  xlink:href="#i-consultar"></use>
          </svg> <hl:message key="rotulo.botao.pesquisar"/>
        </a>
      </div>
  
  <div class="card">
        <div class="card-header">
          <h2 class="card-header-title">${title}</h2>
        </div>
        <div class="card-body table-responsive">
          <table class="table table-striped table-hover">
            <thead>                   
              <tr>
                <th scope="col"><hl:message key="rotulo.comunicacao.identificador"/></th>
                <th scope="col"><hl:message key="rotulo.comunicacao.remetente"/></th>
                <th scope="col"><hl:message key="rotulo.usuario.singular"/></th>
                <th scope="col"><hl:message key="rotulo.comunicacao.destinatario"/></th>
                <th scope="col"><hl:message key="rotulo.comunicacao.data"/></th>
                <th scope="col"><hl:message key="rotulo.comunicacao.pendente"/></th>                                   
                <th scope="col"><hl:message key="rotulo.comunicacao.mensagem"/></th>
                <th scope="col"><hl:message key="rotulo.comunicacao.acoes"/></th>
             </tr>
           </thead>
                    <%=JspHelper.msgRstVazio(comunicacoes.size()==0, "8", "lp")%>
                    <%
                        String remetente = "", destinatario = "";
                           String usuNome = "", cmnCodigo = "", matricula = "";
                           String data = "";
                           String msg = "";
                           String tipoRemetente = "", tipoDestinatario = "";
                           long leituras = 0L;
                           
                           Iterator it = comunicacoes.iterator();
                           while (it.hasNext()) {
                              TransferObject next = (TransferObject)it.next();
                              
                              next = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject)next, null, responsavel);
                              
                              long cmnNumero = ((Long) next.getAttribute(Columns.CMN_NUMERO)).longValue();
                              tipoRemetente = (String) next.getAttribute("TIPO_ENTIDADE_REMETENTE");
                              tipoDestinatario = (String) next.getAttribute("TIPO_ENTIDADE_DESTINATARIO");
                              
                              remetente = "<B>";
                              destinatario = "<B>";
                              // Concatena o tipo do remetente ao remetente
                              if(!TextHelper.isNull(tipoRemetente)){
                                if (tipoRemetente.equals(AcessoSistema.ENTIDADE_CSE)) {
                                    remetente += ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel);
                                } else if (tipoRemetente.equals(AcessoSistema.ENTIDADE_CSA)) {
                                    remetente += ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel);
                                } else if (tipoRemetente.equals(AcessoSistema.ENTIDADE_ORG)) {
                                    remetente += ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel);
                                } else if (tipoRemetente.equals(AcessoSistema.ENTIDADE_SER)) {
                                    remetente += ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel);
                                }
                              }
                              
                              // Concatena o tipo do destinatário ao destinatário
                              if(!TextHelper.isNull(tipoDestinatario)){
                                if (tipoDestinatario.equals(AcessoSistema.ENTIDADE_CSE)) {
                                    destinatario += ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel);
                                } else if (tipoDestinatario.equals(AcessoSistema.ENTIDADE_CSA)) {
                                    destinatario += ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel);
                                } else if (tipoDestinatario.equals(AcessoSistema.ENTIDADE_ORG)) {
                                    destinatario += ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel);
                                } else if (tipoDestinatario.equals(AcessoSistema.ENTIDADE_SER)) {
                                    destinatario += ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel);
                                }
                              }

                              
                              remetente += next.getAttribute("NOME_ENTIDADE_REMETENTE") != null ? ":</B> " + TextHelper.forHtmlContent(next.getAttribute("NOME_ENTIDADE_REMETENTE")) : "";
                              destinatario += next.getAttribute("NOME_ENTIDADE_DESTINATARIO") !=null ? ":</B> " + TextHelper.forHtmlContent(next.getAttribute("NOME_ENTIDADE_DESTINATARIO")) : "";
                              matricula = (String) next.getAttribute(Columns.RSE_MATRICULA);
                              cmnCodigo = (String) next.getAttribute(Columns.CMN_CODIGO);
                              usuNome = (String) next.getAttribute(Columns.USU_LOGIN) + " - " + (String) next.getAttribute(Columns.USU_NOME);
                              data = DateHelper.toDateTimeString((Date) next.getAttribute(Columns.CMN_DATA));                          
                              msg = (String) next.getAttribute(Columns.CMN_TEXTO);
                              leituras = ((Long) next.getAttribute("COUNT_LEITURAS")).longValue();
                              boolean cmnPendente = ((Boolean) next.getAttribute(Columns.CMN_PENDENCIA));
                              
                              boolean lida = (leituras > 0) ? true:false;
                              String displayMsg = (msg.length() > 100) ? msg.substring(0, 100) + "..." :msg;
                    %>
                <tr>
                    <td align="center"><%=TextHelper.forHtmlContent(cmnNumero)%></td>
                    <td><%=((String)remetente).trim()%></td>
                    <td><%=TextHelper.forHtmlContent(usuNome)%></td>
                    <td><%=((String)destinatario).trim()%></td>                                
                    <td><%=TextHelper.forHtmlContent(data)%></td>
                    <td><%if (cmnPendente) { %><%=(String)rotuloSim.toUpperCase()%><%} else {%><%=(String)rotuloNao.toUpperCase()%><%} %></td>
                    <td><%if (!lida) { %> <B> <% }%><%=TextHelper.forHtmlContent(displayMsg)%><%if (!lida) { %> </B> <% }%></td>                    
                    <td><a  href="#no-back" title='<hl:message key="mensagem.ler.comunicacao.clique.aqui"/>' onClick="lerCmn('<%=TextHelper.forJavaScript(cmnCodigo )%>'); return false;"><hl:message key="rotulo.comunicacao.ler"/></a></td>
                </tr>
               <%
                 }
               %>
             </tbody>
             <tfoot>
              <tr>
                <td colspan="8">
                  <hl:message key="rotulo.listagem.comunicacao"/> - 
                  <span class="font-italic"> <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/></span>
                </td>
              </tr>
            </tfoot>
            </table>
          </div>
          <div class="card-footer">
            <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>        
          </div>
        </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
	f0 = document.forms[0];
    // valida o formulário antes do envio do submit
    function validForm() {
      var msg = '';
      var periodoIni = document.getElementById('periodoIni');
      var periodoFim = document.getElementById('periodoFim');
      var campoErrado = false;

      fout(periodoIni);
       if (!ValidaMascara(periodoIni)) {
          msg = '<hl:message key="mensagem.erro.data.invalida"/>';
          periodoIni.value = '';                             
          periodoIni.focus(); 
          campoErrado = true;          
       }       
       
       fout(periodoFim);
       if (!ValidaMascara(periodoFim)) {
        msg = '<hl:message key="mensagem.erro.data.invalida"/>';
          periodoFim.value = '';                            
          periodoFim.focus();
          campoErrado = true;                                          
       }     
       
       // valida se as datas estão preenchidas corretamente
       if (periodoIni.value != '' && periodoFim.value != '' && !campoErrado) {  
            var PartesData = new Array();
            PartesData = obtemPartesData(periodoIni.value);
            var DiaIni = PartesData[0];
            var MesIni = PartesData[1];
            var AnoIni = PartesData[2];
            PartesData = obtemPartesData(periodoFim.value);
            var DiaFim = PartesData[0];
            var MesFim = PartesData[1];
            var AnoFim = PartesData[2];
            if (!VerificaPeriodoExt(DiaIni, MesIni, AnoIni, DiaFim, MesFim, AnoFim, 30)) {
              periodoIni.focus();
              return false;
            }
       }
                           
      if (msg != '') {
        alert(msg);
        return false;
      } else {
      
        if(typeof vfRseMatricula === 'function')
          {
            if(vfRseMatricula(true))
            {
              f0.submit();
            }
          }
          else
          {
            f0.submit();
          }
      }
   }
     
  function lerCmn(cmnCodigo) {
      var url = "../v3/enviarComunicacao?acao=editar&cmn_codigo=" + cmnCodigo + "&<%=SynchronizerToken.generateToken4URL(request)%>" ;
      postData(url);
  }
  
  function testDate(campo) {                    
     fout(campo);
     if (!ValidaMascara(campo)) {
        alert('<hl:message key="mensagem.erro.data.invalida"/>');
        campo.value = '';
        with(document.form1) {
          campo.name.focus();                
        }
        return false;
     }       
  }
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
