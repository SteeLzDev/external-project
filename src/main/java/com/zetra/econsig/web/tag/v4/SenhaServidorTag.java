package com.zetra.econsig.web.tag.v4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcCseTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ArquivoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.arquivo.ArquivoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.MetodoSenhaExternaEnum;
import com.zetra.econsig.values.ParamSenhaExternaEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.controller.servidor.AutenticarServidorOAuth2WebController;
import com.zetra.econsig.web.tag.HTMLInputTag;
import com.zetra.econsig.web.tag.HTMLPasswordTag;
import com.zetra.econsig.web.tag.ZetraTagSupport;

/**
 * <p>Title: SenhaServidorTag</p>
 * <p>Description: Tag para campo de senha do servidor, com rótulo de acordo com a senha a ser validada para layout v4.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SenhaServidorTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SenhaServidorTag.class);

    @Autowired
    private ArquivoController arquivoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private UsuarioController usuarioController;

    private boolean senhaParaAutorizacaoReserva = false;
    private boolean senhaObrigatoria = false;
    private String nf = "submit";
    private String nomeCampoSenha = "senha";
    private String nomeCampoSenhaCriptografada = "senhaRSA";
    private String svcCodigo = null;
    private String rseCodigo = null;
    private String nomeCampoMatricula = "RSE_MATRICULA";
    private String nomeCampoCPF = "SER_CPF";
    private String classe = "Edit";
    private boolean exibirQuandoOpcional = false;
    private boolean scriptOnly = false;
    private String inputSizeCSS = "col-6 mt-3";
    private boolean separador2pontos = true;
    private boolean comTagDD = true;

    public void setSenhaParaAutorizacaoReserva(String senhaParaAutorizacaoReserva) {
        this.senhaParaAutorizacaoReserva = senhaParaAutorizacaoReserva != null && senhaParaAutorizacaoReserva.equalsIgnoreCase("true");
    }

    public void setSenhaObrigatoria(String senhaObrigatoria) {
        this.senhaObrigatoria = senhaObrigatoria != null && senhaObrigatoria.equalsIgnoreCase("true");
    }

    public void setNf(String nf) {
        this.nf = nf;
    }

    public void setNomeCampoSenha(String nomeCampoSenha) {
        this.nomeCampoSenha = nomeCampoSenha;
    }

    public void setNomeCampoSenhaCriptografada(String nomeCampoSenhaCriptografada) {
        this.nomeCampoSenhaCriptografada = nomeCampoSenhaCriptografada;
    }

    public void setSvcCodigo(String svcCodigo) {
        this.svcCodigo = svcCodigo;
    }

    public void setRseCodigo(String rseCodigo) {
        this.rseCodigo = rseCodigo;
    }

    public void setNomeCampoMatricula(String nomeCampoMatricula) {
        this.nomeCampoMatricula = nomeCampoMatricula;
    }

    public void setNomeCampoCPF(String nomeCampoCPF) {
        this.nomeCampoCPF = nomeCampoCPF;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public void setExibirQuandoOpcional(boolean exibirQuandoOpcional) {
        this.exibirQuandoOpcional = exibirQuandoOpcional;
    }

    public void setScriptOnly(boolean scriptOnly) {
        this.scriptOnly = scriptOnly;
    }

    public void setComTagDD(boolean comTagDD) {
        this.comTagDD = comTagDD;
    }

    public void setSeparador2pontos(boolean separador2pontos) {
        this.separador2pontos = separador2pontos;
    }

    public void setInputSizeCSS(String inputSizeCSS) {
        this.inputSizeCSS = inputSizeCSS;
    }

    @Override
    public int doEndTag() throws JspException {
        HttpSession session = ((HttpServletRequest) pageContext.getRequest()).getSession();
        try {
            AcessoSistema responsavel = JspHelper.getAcessoSistema((HttpServletRequest) pageContext.getRequest());
            pageContext.getOut().print(generateHtml(responsavel));
        } catch (IOException | InstantiationException | IllegalAccessException | UsuarioControllerException | ServidorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            throw new JspException(ex.getMessage());
        }
        // Continue processing this page
        return (EVAL_PAGE);
    }

    private String generateHtml(AcessoSistema responsavel) throws InstantiationException, IllegalAccessException, UsuarioControllerException, ServidorControllerException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        HttpSession session = ((HttpServletRequest) pageContext.getRequest()).getSession();

        boolean omitirCampoSenhaOpcional = !exibirQuandoOpcional && ParamSist.paramEquals(CodedValues.TPC_OMITIR_CAMPO_SENHA_OPCIONAL, CodedValues.TPC_SIM, responsavel);

        // Define o nome do rótulo do campo de senha.
        String rotuloSenhaServidor = null;
        String maxlength = null;

        boolean usaSenhaAutorizacaoSer = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel);
        boolean usaSenhaAutorizacaoTodasOpe = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_TODAS_OPERACOES, CodedValues.TPC_SIM, responsavel);

        // Verifica se existe utiliza senha de consulta na reserva de margem caso o sistema utilize senha de autorização mas não utilize para todas as operações
        boolean usaSenhaConsultaReservaMargem = false;
        if (!TextHelper.isNull(svcCodigo) && usaSenhaAutorizacaoSer && !usaSenhaAutorizacaoTodasOpe) {
            try {
                ParamSvcCseTO pse = parametroController.findParamSvcCse(new ParamSvcCseTO(CodedValues.TPS_USA_SENHA_CONSULTA_RESERVA_MARGEM, CodedValues.CSE_CODIGO_SISTEMA, svcCodigo), responsavel);
                usaSenhaConsultaReservaMargem = (pse != null && pse.getPseVlr() != null && pse.getPseVlr().trim().equals("1"));
            } catch (ParametroControllerException ex) {
                // Não existe o parâmetro para serviço
            }
        }

        // Se valida senha externa via OAuth2, não cria campo de senha, mas sim um botão para abrir uma nova janela para autenticação
        // e um campo hidden para receber a chave pós autenticação do usuário no sistema externo
        boolean senhaExternaOAuth2 =  (ParamSist.paramEquals(CodedValues.TPC_SENHA_EXTERNA, CodedValues.TPC_SIM, responsavel) &&
                MetodoSenhaExternaEnum.OAUTH2.getMetodo().equals(ParamSenhaExternaEnum.METODO.getValor()));

        if (senhaExternaOAuth2 && !usaSenhaAutorizacaoSer) {
            rotuloSenhaServidor = ApplicationResourcesHelper.getMessage("rotulo.senha.servidor.oauth2.token", responsavel);
        } else if (usaSenhaAutorizacaoSer && (senhaParaAutorizacaoReserva || usaSenhaAutorizacaoTodasOpe) && !usaSenhaConsultaReservaMargem) {
            rotuloSenhaServidor = ApplicationResourcesHelper.getMessage("rotulo.senha.servidor.autorizacao.composto", responsavel);
            maxlength = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_AUT_SERVIDOR, responsavel)) ? (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_AUT_SERVIDOR, responsavel) : "8";
        } else {
            rotuloSenhaServidor = ApplicationResourcesHelper.getMessage("rotulo.senha.servidor.consulta.composto", responsavel);
            maxlength = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel)) ? (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel) : "8";
        }

        // Define o campo de senha.
        HTMLPasswordTag campoSenha = new HTMLPasswordTag();
        if (senhaObrigatoria || (!senhaObrigatoria && !omitirCampoSenhaOpcional)) {
            campoSenha.setIsSenhaServidor("true");
            campoSenha.setName(nomeCampoSenha);
            campoSenha.setCryptedfield(nomeCampoSenhaCriptografada);
            campoSenha.setClasse(classe);
            campoSenha.setDi(nomeCampoSenha);
            campoSenha.setSize("8");
            campoSenha.setMask("#*200");
            campoSenha.setNf(nf);
            campoSenha.setMaxlength(maxlength);
            campoSenha.setPlaceHolder(ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.senha.autorizacao.servidor", responsavel));
        }
        boolean dispensaValidacaoDigital = false;
        ServidorTransferObject servidorTO = null;
        if (ParamSist.getBoolParamSist(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, responsavel) && !TextHelper.isNull(rseCodigo)) {
            servidorTO = servidorController.findServidorByRseCodigo(rseCodigo, responsavel);
            dispensaValidacaoDigital = true;//!TextHelper.isNull(servidorTO.getSerDispensaDigital()) && servidorTO.getSerDispensaDigital().equals(CodedValues.TPC_SIM);
        }
        boolean validaDigitais = ParamSist.getBoolParamSist(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, responsavel) && !dispensaValidacaoDigital;

        // Constrói HTML correspondente a uma linha de tabela com duas colunas.
        StringBuilder html = new StringBuilder();

        if (scriptOnly) {
            // Gera javascript para envio de senha de autorizacao otp
            html.append(geraJavaScriptOTP(responsavel));

            return html.toString();
        }

        html.append(abreLinha(rotuloSenhaServidor, responsavel));

        if (senhaExternaOAuth2 && !usaSenhaAutorizacaoSer) {
            // Relaxa a regra de segurança de Cross-Origin-Opener-Policy de modo que a janela aberta possa manipular a janela pai
            AutenticarServidorOAuth2WebController.disableCrossOriginOpenerPolicy((HttpServletResponse) pageContext.getResponse());

            // Cria um componente com botão para abrir a página de autenticação externa
            html.append("<span id=\"checkOAuth2\" style=\"width:28px; height:24px; margin-bottom:8px; display: none;\"><svg style=\"width:28px; height:24px;\" class=\"i-disponivel\"><use xlink:href=\"#i-confirmar\"></use></svg></span>");
            html.append("<a class=\"btn btn-primary w-100\" href=\"#no-back\" onclick=\"window.open('../v3/redirecionarOAuth2?acao=autorizar'); return false;\">");
            html.append(ApplicationResourcesHelper.getMessage("rotulo.botao.autenticar", responsavel));
            html.append("</a>");
            html.append("<input type=\"hidden\" name=\"tokenOAuth2\" id=\"tokenOAuth2\" value=\"\">");
            html.append("<input type=\"hidden\" name=\"rfcOAuth2\" id=\"rfcOAuth2\" value=\"").append(senhaObrigatoria ? "51M" : "N40").append("\">");

        } else if (!validaDigitais) {
            if (senhaObrigatoria || (!senhaObrigatoria && !omitirCampoSenhaOpcional)) {
                html.append(campoSenha.generateHtml(pageContext, responsavel));
            }
            if (ParamSist.getBoolParamSist(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, responsavel)) {
                List<TransferObject> arquivos = null;
                try {
                    List<String> tarCodigos = new ArrayList<>();
                    tarCodigos.add(TipoArquivoEnum.ARQUIVO_DISPENSA_VALIDACAO_DIGITAL_SER.getCodigo());
                    if (servidorTO != null) {
                        arquivos = arquivoController.listArquivoServidor(servidorTO.getSerCodigo(), tarCodigos, responsavel);
                    }
                } catch (ArquivoControllerException ex) {
                    // Nenhum arquivo encontrado
                    LOG.error(ex.getMessage(), ex);
                }

                if (arquivos != null && !arquivos.isEmpty()) {
                    String acaoDispensaDigital = getAcaoDispensaDigital(responsavel);
                    String msgAlt = ApplicationResourcesHelper.getMessage("mensagem.servidor.visualizar.arquivos.dispensa.validacao.digital.clique.aqui", responsavel);
                    String link = "../v3/listarArquivoDispensaDigitalServidor?acao=listar" + acaoDispensaDigital + "&RSE_CODIGO=" + TextHelper.forJavaScript(rseCodigo);
                    link += "&linkAction=" + TextHelper.forJavaScript(acaoDispensaDigital);
                    String textoMatriculaNome = ApplicationResourcesHelper.getMessage("rotulo.servidor.visualizar.arquivos.dispensa.validacao.digital", responsavel);
                    html.append("<a href=\"#no-back\" onClick=\"postData('").append(SynchronizerToken.updateTokenInURL(link, request)).append("')\" id=\"btnArqDispensaDigital\" aria-label=\"").append(msgAlt).append("\"><span class=\"icon-menu listarArqDispensaDigital\">").append(textoMatriculaNome).append("</a>");
                }
            }

        } else {
            session.removeAttribute(CodedNames.ATTR_SESSION_SER_DIGITAL_VALIDA);
            session.removeAttribute(CodedNames.ATTR_SESSION_SER_DIGITAL_TENTATIVAS);

            HTMLInputTag tokenLeitor = new HTMLInputTag();
            tokenLeitor.setName("tokenLeitor");
            tokenLeitor.setClasse(classe);
            tokenLeitor.setType("text");
            tokenLeitor.setSize("8");
            tokenLeitor.setMask("#*100");
            tokenLeitor.setMaxlength("100");
            tokenLeitor.setNf(nf);
            tokenLeitor.setValue(getTokenLeitor(responsavel));

            String msgAguardeValidacao = ApplicationResourcesHelper.getMessage("mensagem.digital.aguarde.validacao", responsavel);

            if (senhaObrigatoria || (!senhaObrigatoria && !omitirCampoSenhaOpcional)) {
                html.append("<span id=\"divCampoSenha\" name=\"divCampoSenha\" style=\"display:none\">");
                html.append(campoSenha.generateHtml(pageContext, responsavel));
                html.append("</span>\n");
            }

            html.append("<span id=\"divCampoDigital\" name=\"divCampoDigital\">\n");

            html.append(geraJavaScriptToken(responsavel));

            html.append(geraCssToken());

            html.append("  ");
            html.append("  ");
            html.append("<span>");
            html.append(ApplicationResourcesHelper.getMessage("mensagem.instrucao.validacao.digital", responsavel));
            html.append("</span><br><br>&nbsp;<span class=\"rotulo\">");
            html.append(ApplicationResourcesHelper.getMessage("rotulo.token.leitor", responsavel));
            html.append(": </span>");
            html.append(tokenLeitor.generateHtml(responsavel));
            html.append(insereBotao(responsavel));
            html.append(ApplicationResourcesHelper.getMessage("rotulo.validar.digital", responsavel));
            html.append("</a>");
            html.append("</span>\n");
            html.append("<span id=\"divValidandoDigital\" name=\"divValidandoDigital\" style=\"display:none\">");
            html.append("<img src=\"../img/hourglass.gif\" alt=\"").append(msgAguardeValidacao).append("\" title=\"").append(msgAguardeValidacao).append("\">");
            html.append(msgAguardeValidacao);
            html.append("</span>\n");
            html.append("<span id=\"divDigitalOk\" name=\"divDigitalOk\" style=\"display:none\">");
            html.append(ApplicationResourcesHelper.getMessage("mensagem.digital.validada", responsavel));
            html.append("</span>\n");
        }
        html.append(fechaLinha(responsavel));

        return html.toString();
    }

    private String getTokenLeitor(AcessoSistema responsavel) throws InstantiationException, IllegalAccessException, UsuarioControllerException {
        HttpSession session = ((HttpServletRequest) pageContext.getRequest()).getSession();
        String tokenLeitor = (String) session.getAttribute(CodedNames.ATTR_SESSION_TOKEN_LEITOR);
        if (TextHelper.isNull(tokenLeitor)) {
            tokenLeitor = usuarioController.findDeviceToken(responsavel.getUsuCodigo(), responsavel);
        }
        return tokenLeitor;
    }

    private String geraCssToken() {
        StringBuilder html = new StringBuilder();
        html.append("<style type=\"text/css\">\n");
        html.append("    #btnValidarDigital {\n");
        html.append("        color: #FFFFFF;\n");
        html.append("        background-color: #791501;\n");
        html.append("        padding: 4px 6px;\n");
        html.append("        border-radius: 12px;\n");
        html.append("        text-decoration: none;\n");
        html.append("        margin-left: 10px;\n");
        html.append("        font-weight: bold;\n");
        html.append("        white-space: nowrap;\n");
        html.append("    }\n");
        html.append("    #tokenLeitor {");
        html.append("        margin-bottom: 10px;\n");
        html.append("    }\n");
        html.append("    .rotulo {");
        html.append("        font-weight: bold;\n");
        html.append("    }\n");
        html.append("</style>");
        return html.toString();
    }

    private String geraJavaScriptOTP(AcessoSistema responsavel) throws InstantiationException, IllegalAccessException, UsuarioControllerException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        boolean geraSenhaAutOtp = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel);
        boolean omitirCampoSenhaOpcional = !exibirQuandoOpcional && ParamSist.paramEquals(CodedValues.TPC_OMITIR_CAMPO_SENHA_OPCIONAL, CodedValues.TPC_SIM, responsavel);

        if (!geraSenhaAutOtp || (!senhaObrigatoria && omitirCampoSenhaOpcional)) {
            return "";
        }

        String msgEnvioSenhaAutOtp = JspHelper.getMsgEnvioSenhaAutorizacaoOtp(rseCodigo, responsavel);

        if (TextHelper.isNull(msgEnvioSenhaAutOtp)) {
            return "";
        }

        String acaoFormulario = (String) request.getAttribute("acaoFormulario");

        if (TextHelper.isNull(acaoFormulario)) {
            if (CodedValues.FUN_CONF_SOLICITACAO.equals(responsavel.getFunCodigo())) {
                acaoFormulario = "../v3/confirmarSolicitacao";
            } else if (CodedValues.FUN_CONF_SOLICITACAO.equals(responsavel.getFunCodigo())) {
                acaoFormulario = "../v3/cancelarRenegociacao";
            } else {
                acaoFormulario = (String) request.getAttribute("linkAcao");
            }
        }

        StringBuilder html = new StringBuilder();
        html.append("<script language=\"JavaScript\" type=\"text/JavaScript\">\n");
        html.append("$(document).ready(function() {\n");
        html.append("      gerarSenhaOtp();\n");
        html.append("});\n");

        html.append("function gerarSenhaOtp() {\n");
        html.append("    var msg = '").append(msgEnvioSenhaAutOtp).append("';\n");
        html.append("    if (confirm(msg)) {\n");
        html.append("      var parametros = \"acao=gerarSenhaAutorizacaoOtp\" + \"&RSE_CODIGO=").append(TextHelper.forHtmlAttribute(rseCodigo)).append("&_skip_history_=1\";\n");
        html.append("        $.post(\"").append(TextHelper.forHtmlAttribute(acaoFormulario)).append("\", parametros, function(dataAjax) {\n");
        html.append("              try {\n");
        html.append("                  var dataTrim = $.trim(JSON.stringify(dataAjax));\n");
        html.append("                  var objeto = JSON.parse(dataTrim);\n");
        html.append("                  if (typeof objeto.mensagem != 'undefined' && objeto.mensagem != null && objeto.mensagem != '') {\n");
        html.append("                    var mySpan = document.getElementById('msgGeraSenhaAutorizacaoOtp');\n");
        html.append("                    mySpan.textContent = objeto.mensagem;\n");
        html.append("                    mySpan.style.display = \"\";\n");
        html.append("                  }\n");
        html.append("              } catch(err) {\n");
        html.append("                  var mySpan = document.getElementById('msgGeraSenhaAutorizacaoOtp');\n");
        html.append("                  mySpan.textContent = objeto.mensagem;\n");
        html.append("                  mySpan.style.display = \"\";\n");
        html.append("              }\n");
        html.append("          }, \"json\")\n");
        html.append("          .fail(function(err) { ");
        html.append("              var mySpan = document.getElementById('msgGeraSenhaAutorizacaoOtp');\n");
        html.append("              mySpan.textContent = err.responseJSON.mensagem;\n");
        html.append("              mySpan.style.display = \"\";\n");
        html.append("          });\n");
        html.append("        return false;\n");
        html.append("    }\n");
        html.append("}\n");
        html.append("</script>\n");

        return html.toString();
    }

    private String geraJavaScriptToken(AcessoSistema responsavel) {
        StringBuilder html = new StringBuilder();
        html.append("  <script language=\"JavaScript\" type=\"text/JavaScript\">\n");
        html.append("    function validarDigital() {\n");
        html.append("        if (f0.tokenLeitor != null && f0.tokenLeitor.value == '') {\n");
        html.append("            f0.tokenLeitor.focus();\n");
        html.append("            alert(mensagem('mensagem.informe.token.leitor'));\n");
        html.append("            return false;\n");
        html.append("        }\n");
        html.append("        var rseMatricula = '';\n");
        html.append("        var serCPF = '';\n");
        html.append("        var parametros = '_skip_history_=1&TOKEN_LEITOR=' + f0.tokenLeitor.value;\n");
        if (!TextHelper.isNull(rseCodigo)) {
            html.append("        parametros += '&RSE_CODIGO=").append(TextHelper.forJavaScript(rseCodigo)).append("';\n");
        } else {
            html.append("        if (f0.").append(nomeCampoMatricula).append(" != null) {\n");
            html.append("            rseMatricula = f0.").append(nomeCampoMatricula).append(".value;\n");
            html.append("        }\n");
            html.append("        if (f0.").append(nomeCampoCPF).append(" != null) {\n");
            html.append("            serCPF = f0.").append(nomeCampoCPF).append(".value;\n");
            html.append("        }\n");
            html.append("        if (rseMatricula === '' && serCPF === '') {\n");
            html.append("            alert(mensagem('mensagem.informe.campo'));\n");
            html.append("            return false;\n");
            html.append("        }\n");
            html.append("        parametros += '&SRS_CODIGO=1&RSE_MATRICULA=' + rseMatricula + '&SER_CPF=' + f0.").append(nomeCampoCPF).append(".value;\n");
        }
        html.append("        jQuery('#divValidandoDigital').show();\n");
        html.append("        jQuery('#divCampoDigital').hide();\n");
        html.append("        $.post(\"../digital/validarDigital.jsp\", parametros, function(data) {\n");
        html.append("        try {\n");
        html.append("            var trimData = $.trim(JSON.stringify(data));\n");
        html.append("            var obj = JSON.parse(trimData);\n");
        html.append("            jQuery('#divValidandoDigital').hide();\n");
        html.append("            if (obj.success == '0') {\n");
        html.append("               jQuery('#divDigitalOk').show();\n");
        html.append("               jQuery('#").append(nomeCampoSenha).append("').val('_DIGITAL_');\n");
        html.append("            } else if (obj.success == '1') {\n");
        html.append("               alert('").append(ApplicationResourcesHelper.getMessage("mensagem.erro.digital.invalida.tentar.novamente", responsavel)).append("');\n");
        html.append("               jQuery('#divCampoDigital').show();\n");
        html.append("            } else if (obj.success == '2') {\n");
        html.append("               alert('").append(ApplicationResourcesHelper.getMessage("mensagem.erro.digital.invalida.tentativas.excedidas", responsavel)).append("');\n");
        html.append("               jQuery('#divCampoSenha').show();\n");
        html.append("            } else {\n");
        html.append("               jQuery('#divCampoDigital').show();\n");
        html.append("            }\n");
        html.append("        } catch(err) {\n");
        html.append("  alert(err);\n");
        html.append("            jQuery('#divCampoDigital').show();\n");
        html.append("        }\n");
        html.append("    }, \"json\").fail(function(error) { console.log(error); });\n");
        html.append("}\n");
        html.append("</script>\n");

        return html.toString();
    }

    private String getAcaoDispensaDigital(AcessoSistema responsavel) {
        String retorno = null;

        if (responsavel.getFunCodigo().equals(CodedValues.FUN_RES_MARGEM)) {
            retorno = "ReservarMargem";
        } else if (responsavel.getFunCodigo().equals(CodedValues.FUN_CONS_MARGEM)) {
            retorno = "ConsultarMargem";
        } else if (responsavel.getFunCodigo().equals(CodedValues.FUN_COMP_CONTRATO)) {
            retorno = "ComprarConsignacao";
        } else if (responsavel.getFunCodigo().equals(CodedValues.FUN_RENE_CONTRATO)) {
            retorno = "RenegociarConsignacao";
        } else if (responsavel.getFunCodigo().equals(CodedValues.FUN_AUT_RESERVA)) {
            retorno = "AutorizarConsignacao";
        } else if (responsavel.getFunCodigo().equals(CodedValues.FUN_CANC_RENEGOCIACAO)) {
            retorno = "CancelarRenegociacao";
        } else if (responsavel.getFunCodigo().equals(CodedValues.FUN_ALONGAR_CONTRATO)) {
            retorno = "AlongarConsignacao";
        } else if (responsavel.getFunCodigo().equals(CodedValues.FUN_ATUALIZAR_PROCESSO_COMPRA)) {
            retorno = "AtualizarCompra";
        }

        return retorno;
    }

    private String abreLinha(String rotuloSenhaServidor, AcessoSistema responsavel) {
        boolean omitirCampoSenhaOpcional = !exibirQuandoOpcional && ParamSist.paramEquals(CodedValues.TPC_OMITIR_CAMPO_SENHA_OPCIONAL, CodedValues.TPC_SIM, responsavel);

        StringBuilder html = new StringBuilder();
        if (senhaObrigatoria || (!senhaObrigatoria && !omitirCampoSenhaOpcional)) {
            if (comTagDD) {
                html.append("<dt class=\"" + inputSizeCSS + "\">");
            }

            html.append("<label for=\"").append(nomeCampoSenha).append("\">");
            html.append(rotuloSenhaServidor).append(senhaObrigatoria ? "" : " " + ApplicationResourcesHelper.getMessage("rotulo.campo.opcional", responsavel)).append(separador2pontos ? ":" : "&nbsp;");
            html.append("</label>");

            // Se envia senha de autorização OTP para o servidor, inclui span para exibir mensagem de sucesso.
            if (ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) &&
                ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel)) {

                html.append("<span id=\"msgGeraSenhaAutorizacaoOtp\" class=\"ultima-edicao\" style=\"display:none\"></span>");
            }

            if (comTagDD) {
                html.append("</dt>");
                html.append("<dd class=\"" + inputSizeCSS + "\">");
            }
        }
        return html.toString();
    }

    private String insereBotao(AcessoSistema responsavel) {
        StringBuilder html = new StringBuilder();
        html.append("<a class=\"btn btn-primary mt-3 float-end\" name=\"btnValidarDigital\" href=\"#no-back\" onclick=\"validarDigital()\">");
        return html.toString();
    }

    private String fechaLinha(AcessoSistema responsavel) {
        boolean omitirCampoSenhaOpcional = !exibirQuandoOpcional && ParamSist.paramEquals(CodedValues.TPC_OMITIR_CAMPO_SENHA_OPCIONAL, CodedValues.TPC_SIM, responsavel);

        StringBuilder html = new StringBuilder();
        if (senhaObrigatoria || (!senhaObrigatoria && !omitirCampoSenhaOpcional)) {
            if (comTagDD) {
                html.append("</dd>");
            }
        }
        return html.toString();
    }
}
