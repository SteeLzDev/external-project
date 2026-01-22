<%--
* <p>Title: iniciarReservaGap.jsp</p>
* <p>Description: Página de reserva do acordo GAP</p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: alexandre $
* $Revision: 31212 $
* $Date: 2021-01-28 15:37:52 -0300 (qui, 28 jan 2021) $
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %><%@ page import="java.math.BigDecimal"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.criptografia.JCryptOld"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.consignacao.GAPHelper"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String rseMatricula = (String) request.getAttribute("rseMatricula");
String rseCodigo = (String) request.getAttribute("rseCodigo");
String svcCodigo = (String) request.getAttribute("svcCodigo");

String titulo =  (String) request.getAttribute("titulo");

String csaCodigo = (String) request.getAttribute("csaCodigo");
String csaNome = (String) request.getAttribute("csaNome");
String estNome = (String) request.getAttribute("estNome");
String orgNome = (String) request.getAttribute("orgNome");
String serNome = (String) request.getAttribute("serNome");
String serCpf = (String) request.getAttribute("serCpf");
String categoria = (String) request.getAttribute("categoria");
String codCargo = (String) request.getAttribute("codCargo");
String cargo = (String) request.getAttribute("cargo");
String codPadrao = (String) request.getAttribute("codPadrao");
String padrao = (String) request.getAttribute("padrao");
String codSubOrgao = (String) request.getAttribute("codSubOrgao");
String subOrgao = (String) request.getAttribute("subOrgao");
String codUnidade = (String) request.getAttribute("codUnidade");
String unidade = (String) request.getAttribute("unidade");
String clt = (String) request.getAttribute("clt");
String rsePrazo = (String) request.getAttribute("rsePrazo");
String dataAdmissao = (String) request.getAttribute("dataAdmissao");
String serDataNasc = (String) request.getAttribute("serDataNasc");

// Informação bancária do servidor
String numBanco = (String) request.getAttribute("numBanco");
String numAgencia = (String) request.getAttribute("numAgencia");
int sizeNumAgencia = (int) request.getAttribute("sizeNumAgencia");
String numConta = (String) request.getAttribute("numConta");

String numConta1 = (String) request.getAttribute("numConta1");
String numConta2 = (String) request.getAttribute("numConta2");

// Combo de correspondentes
List<TransferObject> correspondentes = (List<TransferObject>) request.getAttribute("correspondentes");

// Parâmetros do convênio
String svcIdentificador = (String) request.getAttribute("svcIdentificador");
String svcDescricao = (String) request.getAttribute("svcDescricao");
String cnvCodVerba = (String) request.getAttribute("cnvCodVerba");
String descricao = (String) request.getAttribute("descricao");
String cnvCodigo = (String) request.getAttribute("cnvCodigo");
String orgCodigo = (String) request.getAttribute("orgCodigo");

// Parâmetro de incidência da margem
Short incMargem = (Short) request.getAttribute("serCodigo");

// Indica se deve validar a data de nascimento do servidor.
boolean validarDataNasc = (boolean) request.getAttribute("validarDataNasc");
boolean hasValidacaoDataNasc = (boolean) request.getAttribute("hasValidacaoDataNasc");

// Pega o parâmetro que diz se o Banco/Conta/Agencia do servidor é obrigatória
boolean serInfBancariaObrigatoria = (boolean) request.getAttribute("serInfBancariaObrigatoria");

// Indica se deve validar as informações bancárias.
boolean validarInfBancaria = (boolean) request.getAttribute("validarInfBancaria");

// Máscara de identificador do contrato
String mascaraAdeIdentificador = (String) request.getAttribute("mascaraAdeIdentificador");

// Mês de início de desconto
Integer mesInicioDesconto = (Integer) request.getAttribute("mesInicioDesconto");

// Busca as margens para o registro servidor associadas ao serviço
List<TransferObject> lstMargem = (List<TransferObject>) request.getAttribute("lstMargem");

%>
<c:set var="title">
<hl:message key="rotulo.reservar.margem.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
<form action="../v3/reservarMargemGap?acao=confirmarReserva&<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="form1">
  <div class="row">
    <div class="col-sm-6">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.editar.servidor.grid"/></h2>
        </div>
        <div class="card-body">       
          <dl class="row data-list">
            <% if (responsavel.isCseSupOrg() || responsavel.isSer()) { %>
            <dt class="col-6"><hl:message key="rotulo.consignataria.singular"/></dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(csaNome)%></dd>
            <% } %>
            <dt class="col-6"><hl:message key="rotulo.estabelecimento.singular"/></dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(estNome)%></dd>
            <dt class="col-6"><hl:message key="rotulo.orgao.singular"/></dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(orgNome)%></dd>
            <% if (!codSubOrgao.equals("") || !subOrgao.equals("")) { %>
            <dt class="col-6"><hl:message key="rotulo.servidor.sub.orgao"/></dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(codSubOrgao)%>&nbsp;-&nbsp;<%=TextHelper.forHtmlContent(subOrgao)%></dd>
            <% } %>
            <% if (!codUnidade.equals("") || !unidade.equals("")) { %>
            <dt class="col-6"><hl:message key="rotulo.servidor.unidade"/></dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(codUnidade)%>&nbsp;-&nbsp;<%=TextHelper.forHtmlContent(unidade)%></dd>
            <% } %>
            <dt class="col-6"><hl:message key="rotulo.servidor.singular"/></dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(serNome)%></dd>
            <% if (!serDataNasc.equals("") || !serCpf.equals("") || validarDataNasc) { %>
            <dt class="col-6"><%=validarDataNasc || hasValidacaoDataNasc ? ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.gap.data.nascimento.cpf", responsavel)%></dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(validarDataNasc || hasValidacaoDataNasc ? "" : serDataNasc + " - ")%><%=TextHelper.forHtmlContent(serCpf)%></dd>
            <% } %>
            <% if (!dataAdmissao.equals("") || !categoria.equals("")) { %>
            <dt class="col-6"><hl:message key="rotulo.gap.data.admissao.categoria"/></dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(dataAdmissao)%>&nbsp;-&nbsp;<%=TextHelper.forHtmlContent(categoria)%></dd>
            <% } %>
            <% if (!codCargo.equals("") || !cargo.equals("")) { %>
            <dt class="col-6"><hl:message key="rotulo.servidor.cargo"/></dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(codCargo)%>&nbsp;-&nbsp;<%=TextHelper.forHtmlContent(cargo)%></dd>
            <% } %>
            <% if (!codPadrao.equals("") || !padrao.equals("")) { %>
            <dt class="col-6"><hl:message key="rotulo.servidor.padrao"/></dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(codPadrao)%>&nbsp;-&nbsp;<%=TextHelper.forHtmlContent(padrao)%></dd>
            <% } %>
            <dt class="col-6"><hl:message key="rotulo.servico.singular"/></dt>
            <dd class="col-6"><%=TextHelper.forHtmlContent(descricao)%></dd>
            <% if (validarDataNasc) { %>
            <dt class="col-6"><hl:message key="rotulo.gap.data.nascimento"/></dt>
            <dd class="col-6"><hl:htmlinput name="dataNasc" type="text" classe="Edit" di="dataNasc" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value=""/></dd>
            <% } %>
          </dl>
        </div>
      </div>
    </div>
    <div class="col-sm-6">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="mensagem.gap.informe.valores.autorizacao"/></h2>
        </div>
        <div class="card-body">
          <% // Mostra combo com os correspondentes
              if (correspondentes != null && correspondentes.size() > 0) {
                  Iterator<TransferObject> itCor = correspondentes.iterator();
              %>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="COR_CODIGO"><hl:message key="rotulo.correspondente.singular"/> <hl:message key="rotulo.campo.opcional"/></label>
                <select name="COR_CODIGO" id="COR_CODIGO" class="form-control"
                        onFocus="SetarEventoMascara(this,'#*200',true);"
                        onBlur="fout(this);ValidaMascara(this);">
                   <option value="" selected><hl:message key="rotulo.campo.selecione"/></option>
                   <%
                     TransferObject next = null;
                     String cor_nome = null;
                     String cor_codigo = null;
                     while (itCor.hasNext()) {
                       next = (TransferObject) itCor.next();
                       cor_codigo = next.getAttribute(Columns.COR_CODIGO) + ";" + next.getAttribute(Columns.COR_IDENTIFICADOR) + ";" + next.getAttribute(Columns.COR_NOME);
                       cor_nome = next.getAttribute(Columns.COR_IDENTIFICADOR) + " - " + next.getAttribute(Columns.COR_NOME);
                       if (cor_nome.length() > 35) {
                         cor_nome = cor_nome.substring(0, 32) + "...";
                       }
                       out.print("<option value=\"" + cor_codigo + "\">" + cor_nome + "</option>");
                     }
                   %>
                  </select>
              </div>
            </div>
            <% } %>
            <% if (serInfBancariaObrigatoria) { %>
            <h3 class="legend"><hl:message key="rotulo.servidor.informacoesbancarias"/></h3>
            <div class="row">
              <div class="form-group col-sm-4">
                <label for="numBanco"><hl:message key="rotulo.servidor.informacoesbancarias.banco.abreviado"/></label>
                <hl:htmlinput name="numBanco" type="text" classe="form-control" di="numBanco" size="3" mask="#D3" />
              </div>
              <div class="form-group col-sm-2">
                <label for="numAgencia"><hl:message key="rotulo.servidor.informacoesbancarias.agencia.abreviado"/></label>
                <hl:htmlinput name="numAgencia" type="text" classe="form-control" di="numAgencia" size="<%=(String)(sizeNumAgencia < 6 ? \"5\" : \"15\")%>" mask="#*30" />
              </div>
              <div class="form-group col-sm-6">
                <label for="numConta"><hl:message key="rotulo.servidor.informacoesbancarias.conta.abreviado"/></label>
                <hl:htmlinput name="numConta" type="text" classe="form-control" di="numConta" size="12" mask="#*40" />
              </div>
            </div>
            <% } %>
            <div class="row">
              <div class="form-group col-sm">
                <label for="incMargem"><hl:message key="mensagem.gap.selecione.prestacoes.envolvidas.contrato" arg0="<%=TextHelper.forHtmlAttribute(svcDescricao)%>"/></label>
                <% if (lstMargem != null && lstMargem.size() > 0) { %>
                  <%
                  int i = 0;
                  boolean temReserva = false;
                  boolean temMargemRest = false;
                  Iterator itMargem = lstMargem.iterator();
                  TransferObject margem = null;
                  while (itMargem.hasNext()) {
                      margem = (TransferObject) itMargem.next();
                      temReserva = (margem.getAttribute(Columns.ADE_CODIGO) != null && !margem.getAttribute(Columns.ADE_CODIGO).equals(""));
                      temMargemRest = (margem.getAttribute(Columns.MRS_MARGEM_REST) != null && 
                                  ((BigDecimal) margem.getAttribute(Columns.MRS_MARGEM_REST)).doubleValue() > 0);
                  %>
                  <div class="row">
                    <div class="col-sm-4">
                      <div class="form-check pt-2">
                        <input type="checkbox" class="form-check-input ml-1" name="incMargem" id="incMargem_<%= i %>" value="<%=TextHelper.forHtmlAttribute(margem.getAttribute(Columns.MAR_CODIGO))%>" <%=(String)((temReserva || !temMargemRest) ? "disabled" : "")%> onClick="return controlaMargemGap(<%=(int) i %>)" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);" />
                        <label for="incMargem_<%= i %>" class="form-check-label labelSemNegrito ml-1 pr-4 text-nowrap align-text-top"><hl:message key="rotulo.moeda"/> <%=TextHelper.forHtmlContent( margem.getAttribute(Columns.ADE_VLR) )%> - <hl:message key="rotulo.gap.pagamento"/>: <%=TextHelper.forHtmlContent( DateHelper.toPeriodString((java.util.Date) margem.getAttribute(Columns.ADE_ANO_MES_INI)) )%><%= temReserva ? " - " + ApplicationResourcesHelper.getMessage("rotulo.gap.ja.reservado", responsavel) : "&nbsp;" %></label>
                      </div>
                    </div>
                  </div>
                    <% i++; %>
                  <% } %>
              <% } %>
              
            </div>
          </div>
          <div class="row">
           <div class="form-group col-sm-6">
            <label for="adeIdentificador"><hl:message key="rotulo.consignacao.identificador"/> <hl:message key="rotulo.campo.opcional"/></label>
            <INPUT TYPE="text" NAME="adeIdentificador" ID="adeIdentificador" VALUE="" CLASS="form-control" SIZE="15" nf="btnEnvia" onFocus="SetarEventoMascara(this,'<%=TextHelper.forJavaScript(TextHelper.isNull(mascaraAdeIdentificador) ? "#*40":mascaraAdeIdentificador )%>',true);" onBlur="fout(this);ValidaMascara(this);">    
           </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  
  <hl:htmlinput type="hidden" name="CNV_CODIGO" value="<%=TextHelper.forHtmlAttribute(cnvCodigo)%>" />
  <hl:htmlinput type="hidden" name="RSE_CODIGO" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>" />
  <% if (responsavel.isCseSupOrg() || responsavel.isSer()) { %>
  <hl:htmlinput type="hidden" name="CSA_CODIGO" value="<%=TextHelper.forHtmlAttribute(request.getParameter("CSA_CODIGO"))%>" />
  <% } %>
  <div class="btn-action">
    <% if (!responsavel.isSer()) { %> 
    <a class="btn btn-outline-danger" HREF="#no-back" onClick="postData('../v3/reservarMargem?acao=iniciar&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
    <% } else { %>
    <a class="btn btn-outline-danger" HREF="#no-back" onClick="postData('../v3/reservarMargem?acao=selecionarCsa&SVC_CODIGO=<%=TextHelper.forJavaScriptAttribute(svcCodigo)%>&SVC_DESCRICAO=<%=TextHelper.forJavaScript(java.net.URLEncoder.encode(svcDescricao, "ISO-8859-1"))%>&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.botao.cancelar"/></a>
    <% } %>
    <a class="btn btn-primary" NAME="btnEnvia" ID="btnEnvia" HREF="#no-back" onClick="validarGap(); return false;"><hl:message key="rotulo.botao.confirmar"/></a>
  </div>
</form>
</c:set>
<c:set var="javascript">
<script  type="text/JavaScript" src="../js/scripts_2810.js"></script>
<script  type="text/JavaScript" src="../js/validacoes.js"></script>
<script  type="text/JavaScript" src="../js/validaform.js"></script>
<script  type="text/JavaScript" src="../js/xbdhtml.js"></script>
<script  type="text/JavaScript" src="../js/javacrypt.js"></script>
<script type="text/JavaScript">
    window.onload = formLoad;

    var valor = 'IB';
    var validarInfBancaria = <%=TextHelper.forJavaScriptBlock(serInfBancariaObrigatoria && validarInfBancaria)%>;

    function formLoad() {
      <% if (correspondentes != null && correspondentes.size() > 0) { %>
        f0.cor_ajuda.focus();
      <% } else if (validarDataNasc) { %>
        f0.dataNasc.focus();
      <% } else if (serInfBancariaObrigatoria) { %>
        if (f0.numBanco != null && f0.numBanco.disabled == false) {
          f0.numBanco.focus();
        }
      <% } else { %>
        f0.adeIdentificador.focus();
      <% } %>
    }

    function verificaDataNasc() {
      <%if (validarDataNasc) { %>
        var dataNascBase = '<%=(String)JCryptOld.crypt("IB", TextHelper.isNull(serDataNasc) ? "vazio" : serDataNasc.replaceAll("/", ""))%>';
        var dataNasc = f0.dataNasc.value;
        if (dataNasc == '') { 
          alert('<hl:message key="mensagem.dataNascNaoInformada"/>');
          f0.dataNasc.focus();
          return false; 
        } else {
          dataNasc = dataNasc.replace(/\//g, '');
          dataNasc = Javacrypt.crypt(valor, dataNasc)[0];
          if (dataNasc != dataNascBase) {
            alert('<hl:message key="mensagem.dataNascNaoConfere"/>');
            return false;
          }
        }
      <% } %>
      return true;
    }

    function verificaInfBanco() {
      <% if (serInfBancariaObrigatoria) { %>
        var Controles = new Array("numBanco", "numAgencia", "numConta");
        var Msgs = new Array('<hl:message key="mensagem.informacaoBancariaObrigatoria"/>',
      	                   '<hl:message key="mensagem.informacaoBancariaObrigatoria"/>',
      		               '<hl:message key="mensagem.informacaoBancariaObrigatoria"/>');

        var banco = Javacrypt.crypt(valor, formataParaComparacao(f0.numBanco.value))[0];
        var agencia = Javacrypt.crypt(valor, formataParaComparacao(f0.numAgencia.value))[0];
 
        var conta = formataParaComparacao(f0.numConta.value);
        var pos = 0;
        var letra = conta.substr(pos, 1);
        while (letra == 0 && pos < conta.length) {
          pos++;
          letra = conta.substr(pos, 1)  ;
        }
    
        if (pos == conta.length) {
          alert('<hl:message key="mensagem.gap.valor.conta.diferente.zero"/>');
          f0.numConta.focus();
          return false;
        }
 
        conta = conta.substr(pos,conta.length);
        var conta1 = Javacrypt.crypt(valor, conta.substr(0, conta.length/2))[0];
        var conta2 = Javacrypt.crypt(valor, conta.substr(conta.length/2, conta.length))[0];
    
        if (ValidaCampos(Controles, Msgs)) {
          if ((banco != '<%=TextHelper.forJavaScriptBlock(numBanco)%>') || (agencia != '<%=TextHelper.forJavaScriptBlock(numAgencia)%>') || (conta1 != '<%=TextHelper.forJavaScriptBlock(numConta1)%>') || (conta2 != '<%=TextHelper.forJavaScriptBlock(numConta2)%>')) {
            if (validarInfBancaria) {
              alert('<hl:message key="mensagem.informacaoBancariaIncorreta"/>');
              return false;
            }
            if (confirm('<hl:message key="mensagem.informacaoBancariaIncorreta.continuar"/>')) {
              return true;
            } else {
              f0.numBanco.focus();
              return false;
            }
          }
          return true;
        }
        return false;
      <% } else { %>
        return true;
      <% } %>
    }

    function controlaMargemGap(i) {
      if (f0.incMargem.length != undefined) {
        // Garante que o usuário escolha as margens na sequência exibida 
        // na página.
        if (f0.incMargem[i].checked) {
          // Se estou selecionando, verifica se os anteriores também estão selecionados
          for (j = i-1; j >= 0; j--) {
            if (!f0.incMargem[j].disabled && !f0.incMargem[j].checked) {
              alert('<hl:message key="mensagem.gap.reservar.prestacoes.ordem.crescente"/>'.replace('{0}','<%=TextHelper.forJavaScriptBlock(svcDescricao)%>'));
              return false;
            }
          }
        } else {
          // Se estou deselecionando, verifica se os próximos também estão deselecionados
          for (j = i+1; j < f0.incMargem.length; j++) {
            if (!f0.incMargem[j].disabled && f0.incMargem[j].checked) {
              alert('<hl:message key="mensagem.gap.reservar.prestacoes.ordem.crescente"/>'.replace('{0}','<%=TextHelper.forJavaScriptBlock(svcDescricao)%>'));
              return false;
            }
          }
        }
      }
      return true;
    }

    function validarGap() {
      if (verificaDataNasc() && verificaInfBanco()) {
        fezEscolha = false;
        if (f0.incMargem.length != undefined) {
          for (i = 0; i < f0.incMargem.length; i++) {
            if (!f0.incMargem[i].disabled && f0.incMargem[i].checked) {
              fezEscolha = true;
            }
          }
        } else {
          fezEscolha = f0.incMargem.checked;
        }
        if (fezEscolha) {
          f0.submit();
        } else {
          alert('<hl:message key="mensagem.gap.escolha.minimo.uma.prestacao.nome"/>'.replace('{0}','<%=TextHelper.forJavaScriptBlock(svcDescricao)%>'));
        }
      }
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