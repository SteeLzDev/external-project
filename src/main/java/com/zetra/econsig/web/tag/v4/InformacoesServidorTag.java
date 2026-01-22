package com.zetra.econsig.web.tag.v4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.BoletoServidorControllerException;
import com.zetra.econsig.exception.LeilaoSolicitacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.compra.MontaCriterioAcompanhamentoCompra;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.boleto.BoletoServidorController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.tag.ConfigSistemaTag;
import com.zetra.econsig.web.tag.ZetraTagSupport;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * <p>Title: InformacoesServidorTag</p>
 * <p>Description: Tag para impressão de informações do servidor.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class InformacoesServidorTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(InformacoesServidorTag.class);

    @Autowired
    private BoletoServidorController boletoServidorController;

    @Autowired
    private LeilaoSolicitacaoController leilaoSolicitacaoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private UsuarioController usuarioController;

    protected AcessoSistema responsavel;

    protected String rseCodigo;

    protected boolean exibeMargemServidor;

    protected boolean usaSenhaAutorizacao;

    protected boolean consomeSenhaAutorizacao;

    protected boolean usaMultiplasSenhasAut;

    protected String dataHoraSistema = null;

    protected List<ConfigSistemaTag.ConfiguracaoModulosSistema> configuracaoModulosSistema = null;

    protected TransferObject usuarioSer;

    protected List<MargemTO> margens;

    protected List<TransferObject> senhasAutorizacao;

    /**
     * Carrega as informacoes a serem exibidas.
     */
    protected void carregaInformacoes() {
        responsavel = JspHelper.getAcessoSistema((HttpServletRequest) pageContext.getRequest());

        // Limpas as informações anteriormente carregadas
        usuarioSer = null;
        margens = null;
        senhasAutorizacao = null;

        try {
            carregaInformacaoServidor();
        } catch (final ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Carrega as informacoes sobre o servidor.
     * @throws ViewHelperException
     */
    private void carregaInformacaoServidor() throws ViewHelperException {

        final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        // Se o usuário não é de servidor, não há o que ser carregado
        if (!responsavel.isSer()) {
            return;
        }

        // Obtém a data e hora do sistema
        dataHoraSistema = DateHelper.toDateTimeString(DateHelper.getSystemDatetime());

        // Parâmetro de sistema para exibir a margem do servidor na tela inicial do módulo do servidor
        exibeMargemServidor = ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGEM_SERVIDOR_TELA_PRINCIPAL, CodedValues.TPC_SIM, responsavel);
        try {
            final RegistroServidorTO registroServidor = servidorController.findRegistroServidor(responsavel.getRseCodigo(), true, responsavel);
            exibeMargemServidor = exibeMargemServidor && (!registroServidor.isBloqueado() || (registroServidor.isBloqueado() && ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGEM_SERVIDORES_BLOQUEADOS_SER, CodedValues.TPC_SIM, responsavel)));
            if (exibeMargemServidor) {
                margens =  request.getAttribute("margensServidor") != null ? (List<MargemTO>) request.getAttribute("margensServidor") : null;
            }
        } catch (final ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        usaSenhaAutorizacao = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel);
        if (usaSenhaAutorizacao) {
            try {
                // Se usa senha de autorização, então busca os dados da senha do servidor
                usuarioSer = usuarioController.getSenhaServidor(rseCodigo, responsavel);
                // Se consome senha no deferimento ou na inclusão, habilita exibição de operações válidas para a senha
                consomeSenhaAutorizacao = ParamSist.paramEquals(CodedValues.TPC_DEFERIMENTO_CONSOME_SENHA_AUT_DESC, CodedValues.TPC_SIM, responsavel) || ParamSist.paramEquals(CodedValues.TPC_INCLUSAO_CONSOME_SENHA_AUT_DESC, CodedValues.TPC_SIM, responsavel);
                // Se utiliza múltiplas senhas de autorização, busca os registros de senhas válidas para este servidor
                usaMultiplasSenhasAut = ParamSist.paramEquals(CodedValues.TPC_USA_MULTIPLAS_SENHAS_AUTORIZACAO_SERVIDOR, CodedValues.TPC_SIM, responsavel);
                if (usaMultiplasSenhasAut) {
                    senhasAutorizacao = usuarioController.lstSenhaAutorizacaoServidor((String) usuarioSer.getAttribute(Columns.USU_CODIGO), responsavel);
                }
            } catch (final UsuarioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

    }

    @Override
    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().print(generateHtml());
        } catch (final IOException ex) {
            throw new JspException(ex.getMessage());
        }
        // Continue processing this page
        return (EVAL_PAGE);
    }

    public String generateHtml() {
        // Obtém as informações
        carregaInformacoes();
        final String eConsigPageToken = SynchronizerToken.generateToken4URL((HttpServletRequest) pageContext.getRequest());
        final StringBuilder html = new StringBuilder();

        gerarHtmlConfiguracoesSistema(html);

        gerarHtmlModulosSistema(html);

        if (responsavel.isSer() && exibeMargemServidor) {
            geraHtmlExibeMargemSer(html);
        }

        gerarHtmlServicosPendentes(eConsigPageToken, html);

        geraHtmlBoletoServidor(eConsigPageToken, html);

        gerarHtmlExisteComunicaoServidor(eConsigPageToken, html);

        if (usaSenhaAutorizacao && responsavel.isSer() && responsavel.temPermissao(CodedValues.FUN_ALTERAR_SENHA_AUTORIZACAO_USU_SER)) {
            geraHtmlSenhaAutorizacaoSer(eConsigPageToken, html);
        }

        return html.toString();
    }

    protected void gerarHtmlConfiguracoesSistema(StringBuilder html) {
        html.append("<div class=\"row\">");
            html.append("<div class=\"col-sm\">");
                html.append("<div class=\"card\">");
                    html.append("<div class=\"card-header hasIcon\">");
                        html.append("<span class=\"card-header-icon\"><svg width=\"26\">");
                            html.append("<use xlink:href=\"../img/sprite.svg#i-sistema\"></use></svg>");
                        html.append("</span>");
                        html.append("<h2 class=\"card-header-title\">");
                            html.append(ApplicationResourcesHelper.getMessage("rotulo.sistema.configuracoes", responsavel));
                        html.append("</h2>");
                    html.append("</div>");
                    html.append("<div class=\"card-body\">");
                        html.append("<dl class=\"row data-list\">");
                            html.append("<dt class=\"col-6\">");
                                html.append(ApplicationResourcesHelper.getMessage("rotulo.sistema.data.hora.sistema", responsavel));
                            html.append("</dt>");
                            html.append("<dd class=\"col-6\">");
                                html.append(TextHelper.forHtmlContent(dataHoraSistema));
                            html.append("</dd>");
                        html.append("</dl>");
                    html.append("</div>");
                html.append("</div>");
            html.append("</div>");
        html.append("</div>");
    }

    protected void gerarHtmlModulosSistema(StringBuilder html) {
        html.append("<div class=\"row\">");
            html.append("<div class=\"col-sm\">");
                html.append("<div class=\"card\">");
                    html.append("<div class=\"card-header hasIcon\">");
                        html.append("<span class=\"card-header-icon\"><svg width=\"36\">");
                            html.append("<use xlink:href=\"../img/sprite.svg#i-sistema\"></use></svg>");
                        html.append("</span>");
                        html.append("<h2 class=\"card-header-title\">");
                            html.append(ApplicationResourcesHelper.getMessage("rotulo.sistema.modulos.sistema", responsavel));
                        html.append("</h2>");
                    html.append("</div>");
                    html.append("<div class=\"card-body table-responsive p-0\">");
                        html.append("<table id=\"tableModulosSistema\" class=\"table table-striped table-hover\">");
                        // Não funciona tfoot aqui, incluído via javascript
                        html.append("</table>");
                    html.append("</div>");
                html.append("</div>");
            html.append("</div>");
        html.append("</div>");
    }

    protected void geraHtmlSenhaAutorizacaoSer(String eConsigPageToken, StringBuilder html) {

        final String rotuloSenhaAut = ApplicationResourcesHelper.getMessage("rotulo.senha.servidor.autorizacao.singular", responsavel);
        final String acaoGerarSenhaAut = ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.acao.gerar", responsavel);
        final String mensagemSenhaNaoCadastrada = ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.nao.encontrada", responsavel);
        final String acaoCancelarSenhaAut = ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.acao.cancelar", responsavel);
        final int senhasDisponiveis = senhasAutorizacao != null ? senhasAutorizacao.size() : 0;

        html.append("<div class=\"card\">");
        html.append("<div class=\"card-header hasIcon\">");
        html.append("<span class=\"card-header-icon\">");
        html.append("<svg width=\"26\">");
        html.append("<use xlink:href=\"#i-usuario\"></use>");
        html.append("</svg></span>");
        html.append("<div class=\"row\">");
        html.append("<div class=\"col-sm\">");
        html.append("<h2 class=\"card-header-title\">");
        html.append(rotuloSenhaAut);
        html.append("</h2>");
        html.append("</div>");
        html.append("<div class=\"col-sm\">");
        if (senhasDisponiveis > 0) {
            html.append("<span class=\"ultima-edicao\">");
            html.append(ApplicationResourcesHelper.getMessage("rotulo.senha.servidor.disponiveis", responsavel));
            html.append(": " + senhasDisponiveis);
        }
        html.append("</span>");
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("<div class=\"card-body table-responsive p-0\">");
        html.append("<table class=\"table table-striped table-hover\">");

        // Se utiliza múltiplas senhas de autorização, recupera o registro de senha
        if (usaMultiplasSenhasAut) {
            if ((senhasDisponiveis <= 0)) {
                html.append("<div class=\"alert-info p-2\">").append(mensagemSenhaNaoCadastrada).append("</div>");
            }

            if (senhasDisponiveis > 0) {

                html.append("<thead>");
                html.append("<tr>");
                html.append("<th scope=\"col\" width=\"10%\">").append(ApplicationResourcesHelper.getMessage("rotulo.listar.senha.autorizacao.servidor.numero", responsavel)).append("</th>");
                html.append("<th scope=\"col\" width=\"30%\">").append(ApplicationResourcesHelper.getMessage("rotulo.listar.senha.autorizacao.servidor.data.geracao", responsavel)).append("</th>");
                html.append("<th scope=\"col\" width=\"30%\">").append(ApplicationResourcesHelper.getMessage("rotulo.listar.senha.autorizacao.servidor.validade", responsavel)).append("</th>");
                html.append("<th scope=\"col\" width=\"10%\">").append(ApplicationResourcesHelper.getMessage("rotulo.listar.senha.autorizacao.servidor.operacoes", responsavel)).append("</th>");
                html.append("<th scope=\"col\" width=\"10%\">").append(ApplicationResourcesHelper.getMessage("rotulo.listar.senha.autorizacao.servidor.acoes", responsavel)).append("</th>");
                html.append("</tr>");
                html.append("</thead>");
                html.append("<tbody>");

                int count = 0;
                for (final TransferObject senhaAutorizacao : senhasAutorizacao) {
                    final Short qtdOperacoesSenhaAutorizacao = (Short) senhaAutorizacao.getAttribute(Columns.SAS_QTD_OPERACOES);
                    final Date dataExpSenhaAutorizacao = (Date) senhaAutorizacao.getAttribute(Columns.SAS_DATA_EXPIRACAO);
                    final Date dataCriacaoSenhaAutorizacao = (Date) senhaAutorizacao.getAttribute(Columns.SAS_DATA_CRIACAO);
                    final String senhaCrypt = (String) senhaAutorizacao.getAttribute(Columns.SAS_SENHA);

                    html.append("<tr>");
                    html.append("<td>").append(++count).append("</td>");
                    html.append("<td>").append(DateHelper.toDateTimeString(dataCriacaoSenhaAutorizacao)).append("</td>");
                    html.append("<td>").append(DateHelper.toDateString(dataExpSenhaAutorizacao)).append("</td>");
                    html.append("<td>").append(qtdOperacoesSenhaAutorizacao).append("</td>");
                    html.append("<td>").append("<a href=\"#no-back\"").append("title=\"").append(acaoCancelarSenhaAut).append("\" onClick=\"postData('../v3/modificarSenhaAutorizacao?acao=cancelar&").append(eConsigPageToken).append("&SENHA=").append(senhaCrypt).append("')\">");
                    html.append(ApplicationResourcesHelper.getMessage("rotulo.botao.cancelar", responsavel));
                    html.append("</td>");
                    html.append("</tr>");
                }
                html.append("</tbody>");

            }
        } else {
            final String senhaAutorizacao = ((usuarioSer != null) && (usuarioSer.getAttribute(Columns.USU_SENHA_2) != null) ? (String) usuarioSer.getAttribute(Columns.USU_SENHA_2) : null);
            final Short qtdOperacoesSenhaAutorizacao = ((usuarioSer != null) && (usuarioSer.getAttribute(Columns.USU_OPERACOES_SENHA_2) != null) ? (Short) usuarioSer.getAttribute(Columns.USU_OPERACOES_SENHA_2) : 0);
            final Date dataExpSenhaAutorizacao = ((usuarioSer != null) && (usuarioSer.getAttribute(Columns.USU_DATA_EXP_SENHA_2) != null) ? (Date) usuarioSer.getAttribute(Columns.USU_DATA_EXP_SENHA_2) : null);
            final boolean senhaValida = ((senhaAutorizacao != null) && (qtdOperacoesSenhaAutorizacao > 0));

            html.append("<thead>");
            if (senhaValida) {
                html.append("<tr>");
                // Tem senha válida
                if (dataExpSenhaAutorizacao != null) {
                    html.append("<th scope=\"col\" width=\"40%\">").append(ApplicationResourcesHelper.getMessage("rotulo.senha.servidor.data.validade", responsavel)).append("</th>");
                }
                if (consomeSenhaAutorizacao) {
                    html.append("<th scope=\"col\" width=\"30%\">").append(ApplicationResourcesHelper.getMessage("rotulo.senha.servidor.validade.operacoes", responsavel)).append("</th>");
                }
                html.append("<th scope=\"col\" width=\"30%\">").append(ApplicationResourcesHelper.getMessage("rotulo.listar.senha.autorizacao.servidor.acoes", responsavel)).append("</th>");
                html.append("</tr>");
            } else {
                // Não tem senha válida
                html.append("<div class=\"alert-info p-2\">");
                html.append(mensagemSenhaNaoCadastrada);
                html.append("</div>");
            }
            html.append("</thead>");
            html.append("<tbody>");
            html.append("<tr>");

            if (dataExpSenhaAutorizacao != null) {
                html.append("<td>").append(DateHelper.toDateString(dataExpSenhaAutorizacao)).append("</td>");
            }
            if (consomeSenhaAutorizacao && (qtdOperacoesSenhaAutorizacao > 0)) {
                html.append("<td>").append(qtdOperacoesSenhaAutorizacao).append("</td>");
            }
            if (senhaValida) {
                html.append("<td><a href=\"#no-back\"").append("title=\"").append(acaoCancelarSenhaAut).append("\" onClick=\"postData('../v3/modificarSenhaAutorizacao?acao=cancelar&").append(eConsigPageToken).append("')\">");
                html.append(ApplicationResourcesHelper.getMessage("rotulo.botao.cancelar", responsavel));
                html.append("</td>");
                html.append("</tr>");
            }
            html.append("</tr>");
            html.append("</tbody>");
        }

        html.append("<tfoot>");
        html.append("<td colspan=\"4\">").append(ApplicationResourcesHelper.getMessage("rotulo.listagem.senha.autorizacao", responsavel)).append("</td>");
        html.append("</tfoot>");
        html.append("</table>");
        html.append("</div>");
        //FAZER BOTAO de GERAR SENHA
        html.append("<div class=\"btn-action\">");
        if((ParamSist.paramEquals(CodedValues.TPC_RECONHECIMENTO_FACIAL_ACESSO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) && (ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel))) {
            html.append("<a id=\"btnGerarSenha\" class=\"btn btn-primary\" href=\"#no-back\"").append("title=\"").append(acaoGerarSenhaAut).append("\" onClick=\"iniciarInstrucoes()\">");
        }else {
            html.append("<a id=\"btnGerarSenha\" class=\"btn btn-primary\" href=\"#no-back\"").append("title=\"").append(acaoGerarSenhaAut).append("\" onClick=\"postData('../v3/modificarSenhaAutorizacao?acao=alterar&").append(eConsigPageToken).append("')\">");
        }

        html.append(ApplicationResourcesHelper.getMessage("rotulo.botoes.acao.gerar.senha", responsavel)).append("</a>");
        html.append("</div>");
        html.append("</div>");
    }

    protected void geraHtmlExibeMargemSer(StringBuilder html) {
        // Exibe as margens do servidor
        html.append("<div class=\"card\">");
        html.append("<div class=\"card-header hasIcon\">");
        html.append("<span class=\"card-header-icon\">");
        html.append("<svg width=\"30\">");
        html.append("<use xlink:href=\"#i-menu-margem\"></use>");
        html.append("</svg></span>");
        html.append("<h2 class=\"card-header-title\">");
        html.append(ApplicationResourcesHelper.getMessage("rotulo.informacao.servidor.margem", responsavel));
        html.append("</h2>");
        html.append("<span class=\"card-header-icon-ocultar-margem\">");
        html.append("<a href=\"#\" id=\"olhoCard\" onclick=\"ocultaMargemCard()\">");
        html.append("<svg width=\"30\" height=\"60\">");
        html.append("<use xlink:href=\"#i-eye-regular\"></use>");
        html.append("</svg>");
        html.append("</a>");
        html.append("<a href=\"#\" id=\"olhoCardOculto\" class=\"d-none\" onclick=\"ocultaMargemCard()\">");
        html.append("<svg width=\"30\" height=\"60\">");
        html.append("<use xlink:href=\"#i-eye-slash-regular\"></use>");
        html.append("</svg>");
        html.append("</a>");
        html.append("</span>");
        html.append("</div>");
        html.append("<div id=\"ocultaMargemCard\" class=\"d-none\">");
        html.append("<dl class=\"row data-list\">");
        html.append("<dt class=\"col-6\">");
        html.append(ApplicationResourcesHelper.getMessage("rotulo.margem.1", responsavel));
        html.append("</dt>");
        html.append("<dd class=\"col-6\">");
        html.append(ApplicationResourcesHelper.getMessage("rotulo.margem.moeda", responsavel) + " ********");
        html.append("</dd>");
        html.append("</dl>");
        html.append("</div>");
        if(margens != null) {
        html.append("<div id=\"exibeMargemCard\" class=\"card-body\">");
        html.append("<dl class=\"row data-list\">");
            final Iterator<MargemTO> itMargens = margens.iterator();
            MargemTO margem = null;
            while (itMargens.hasNext()) {
                margem = itMargens.next();
                if (margem.getMarDescricao() != null) {
                    html.append("<dt class=\"col-6\">");
                    html.append(TextHelper.forHtmlContent(margem.getMarDescricao()) + ":");
                    html.append("</dt>");
                    html.append("<dd class=\"col-6\">");
                    if (margem.getMrsMargemRest() == null) {
                        html.append(" ");
                    } else {
                        final String labelTipoVlr = ParamSvcTO.getDescricaoTpsTipoVlr(margem.getMarTipoVlr() != null ? margem.getMarTipoVlr().toString() : CodedValues.TIPO_VLR_FIXO);
                        final String vlrMargem = NumberHelper.format(margem.getMrsMargemRest().doubleValue(), NumberHelper.getLang());
                        final String obsMargem = (!TextHelper.isNull(margem.getObservacao()) ? " (" + margem.getObservacao() + ")" : "");
                        html.append(labelTipoVlr + " " + TextHelper.forHtmlContent(vlrMargem) + TextHelper.forHtmlContent(obsMargem));
                    }
                    html.append("</dd>");
                }
            }
            html.append("</dl>");
            html.append("</div>");
        }
        html.append("</div>");
    }

    protected void gerarHtmlServicosPendentes(String eConsigPageToken, StringBuilder html) {

        if ((ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, responsavel) || ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel) || (ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavel) && responsavel.temPermissao(CodedValues.FUN_APROVAR_SALDO_DEVEDOR))) && responsavel.isSer()) {

            html.append("<div class=\"card\">");
                html.append("<div class=\"card-header hasIcon\">");
                    html.append("<span class=\"card-header-icon\">");
                        html.append("<svg width=\"26\"><use xlink:href=\"#i-box\"></use></svg>");
                    html.append("</span>");
                    html.append("<h2 class=\"card-header-title\">").append(ApplicationResourcesHelper.getMessage("rotulo.servicos.pendentes.solicitados", responsavel)).append("</h2>");
                html.append("</div>");

                html.append("<div class=\"card-body\">");
                    html.append("<ul class=\"list-links\">");

                    final boolean aprovacaoSaldoSerCompra = geraHtmlAprovacaoSaldoSerCompra(eConsigPageToken, html);
                    final boolean financiamentoDividaCartao = geraHtmlFinanciamentoDividaCartao(eConsigPageToken, html);
                    final boolean leilaoViaSimulacao = geraHtmlLeilaoViaSimulacao(html);

                    if ((!aprovacaoSaldoSerCompra && !financiamentoDividaCartao && !leilaoViaSimulacao)) {
                        html.append(ApplicationResourcesHelper.getMessage("rotulo.mensagem.nenhum.servico.pendente", responsavel));
                    }
                    html.append("</ul>");
                html.append("</div>");
            html.append("</div>");
        }
    }

    protected boolean geraHtmlLeilaoViaSimulacao(StringBuilder html) {
        boolean temProcessoLeilao = false;
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, responsavel) && responsavel.isSer()) {
            try {
                final TransferObject criteriosPesquisa = new CustomTransferObject();
                // Todos os leilões abertos
                criteriosPesquisa.setAttribute("filtro", "4");
                final int total = leilaoSolicitacaoController.contarLeilaoSolicitacao(criteriosPesquisa, responsavel);

                if (total > 0) {

                    html.append("<li>");
                    if (responsavel.temPermissao(CodedValues.FUN_ACOMPANHAR_LEILAO_VIA_SIMULACAO)) {
                        html.append("<a href=\"#no-back\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.visualizar.solicitacoes.leilao.clique.aqui", responsavel));
                        html.append("\" onClick=\"postData('../v3/acompanharLeilao?acao=iniciar&pesquisar=true&filtro=4')\">");
                        html.append(ApplicationResourcesHelper.getMessage("mensagem.existem.leiloes.solicitacao.em.andamento.servidor", responsavel)).append("</a>");
                    }
                    html.append("</li>");
                    temProcessoLeilao = true;
                }
            } catch (final LeilaoSolicitacaoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
        return temProcessoLeilao;
    }

    protected boolean geraHtmlFinanciamentoDividaCartao(String eConsigPageToken, StringBuilder html) {
        boolean temContratosCartaoAtivos = false;
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel) && responsavel.isSer() && responsavel.temPermissao(CodedValues.FUN_CONS_CONSIGNACAO)) {
            // Verifica se o servidor é beneficiário do processo de financiamento de dívida
            boolean beneficiario = false;

            try {
                final RegistroServidorTO rse = servidorController.findRegistroServidor(rseCodigo, responsavel);
                beneficiario = ((rse != null) && (rse.getRseBeneficiarioFinanDvCart() != null) && "S".equals(rse.getRseBeneficiarioFinanDvCart()));
            } catch (final ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            final List<String> svcCodigos = new ArrayList<>();
            if (beneficiario) {
                try {
                    // Busca os serviços de origem de relacionamento de financiamento de dívida (CARTÃO)
                    final List<TransferObject> servicos = parametroController.getRelacionamentoSvc(CodedValues.TNT_FINANCIAMENTO_DIVIDA, null, null, responsavel);
                    if ((servicos != null) && (servicos.size() > 0)) {
                        for (final TransferObject servico : servicos) {
                            svcCodigos.add(servico.getAttribute(Columns.RSV_SVC_CODIGO_ORIGEM).toString());
                        }

                        // Pesquisa as consignações abertas dos serviços origem do relacionamento
                        final int count = pesquisarConsignacaoController.countPesquisaAutorizacao(responsavel.getTipoEntidade(), responsavel.getUsuCodigo(), rseCodigo, null, null, CodedValues.SAD_CODIGOS_ATIVOS, svcCodigos, null, responsavel);
                        temContratosCartaoAtivos = (count > 0);
                    }
                } catch (ParametroControllerException | AutorizacaoControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }

            if (temContratosCartaoAtivos) {
                final StringBuilder link = new StringBuilder("../v3/consultarConsignacao?acao=pesquisarConsignacao&subtipo=financiamento&").append(eConsigPageToken);
                for (final String svc : svcCodigos) {
                    link.append("&SVC_CODIGO=").append(svc);
                }

                html.append("<li>");
                html.append("<a href=\"#no-back\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.alerta.servidor.beneficiarioFinanDvCart.detalhes", responsavel));
                html.append("\" onClick=\"postData('").append(link.toString()).append("')\">");
                html.append(ApplicationResourcesHelper.getMessage("mensagem.alerta.servidor.beneficiarioFinanDvCart", responsavel)).append("</a>");
                html.append("</li>");
            }
        }
        return temContratosCartaoAtivos;
    }

    protected boolean geraHtmlAprovacaoSaldoSerCompra(String eConsigPageToken, StringBuilder html) {
        boolean temPendencias = false;
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavel) && responsavel.isSer() && responsavel.temPermissao(CodedValues.FUN_APROVAR_SALDO_DEVEDOR)) {
            // Verifica se o servidor possui pendência de aprovação de saldo para compra
            int totalPendencias = 0;

            try {
                final TransferObject criterios = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaPendenciaAprovacaoSaldoDevedor(null);
                totalPendencias = pesquisarConsignacaoController.contarCompraContratos(criterios, null, null, null, responsavel);
            } catch (final AutorizacaoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            if (totalPendencias > 0) {
                html.append("<li>");
                html.append("<a href=\"#no-back\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.visualizar.pendencias.clique.aqui", responsavel));
                html.append("\" onClick=\"postData('../v3/acompanharPortabilidade?acao=acompanhar&").append(eConsigPageToken).append("')\">");
                html.append(ApplicationResourcesHelper.getMessage("mensagem.erro.processos.compra.pendencia.aprovacao.saldo.devedor", responsavel)).append("</a>");
                html.append("</li>");
                temPendencias = true;
            }
        }
        return temPendencias;
    }

    protected void geraHtmlBoletoServidor(String eConsigPageToken, StringBuilder html) {
        if (responsavel.temPermissao(CodedValues.FUN_CONSULTAR_BOLETO)) {
            // Verifica se o servidor possui boletos não baixados
            int totalBoletos = 0;

            try {
                final TransferObject criterio = new CustomTransferObject();
                criterio.setAttribute("SOMENTE_NAO_BAIXADO", true);

                totalBoletos = boletoServidorController.countBoletoServidor(criterio, responsavel);
            } catch (final BoletoServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            if (totalBoletos > 0) {
                html.append("<div class=\"card\">");
                    html.append("<div class=\"card-header hasIcon\">");
                        html.append("<span class=\"card-header-icon\">");
                            html.append("<svg width=\"26\"><use xlink:href=\"#i-box\"></use></svg>");
                        html.append("</span>");
                        html.append("<h2 class=\"card-header-title\">").append(ApplicationResourcesHelper.getMessage("rotulo.boleto.servidor.nao.visualizado.titulo", responsavel)).append("</h2>");
                    html.append("</div>");
                    html.append("<div class=\"card-body\">");
                        html.append("<ul class=\"list-links\">");
                            html.append("<li>");
                                html.append("<a href=\"#no-back\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.visualizar.boletos.clique.aqui", responsavel));
                                html.append("\" onClick=\"postData('../v3/consultarBoleto?acao=listarPendentes&").append(eConsigPageToken).append("')\">");
                                html.append(ApplicationResourcesHelper.getMessage("mensagem.info.boleto.servidor.nao.visualizado", responsavel)).append("</a>");
                            html.append("</li>");
                        html.append("</ul>");
                    html.append("</div>");
                html.append("</div>");
            }
        }
    }

    protected void gerarHtmlExisteComunicaoServidor(String eConsigPageToken, StringBuilder html) {
        final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        final boolean existeComunicaoServidor = request.getAttribute("existeComunicacao") != null && (boolean) request.getAttribute("existeComunicacao");

        if (existeComunicaoServidor) {
            html.append("<div class=\"card\">");
                html.append("<div class=\"card-header hasIcon\">");
                    html.append("<span class=\"card-header-icon\">");
                        html.append("<svg width=\"26\"><use xlink:href=\"#i-mensagem\"></use></svg>");
                    html.append("</span>");
                    html.append("<h2 class=\"card-header-title\">").append(ApplicationResourcesHelper.getMessage("rotulo.servidor.principal.existem.comunicacao", responsavel)).append("</h2>");
                html.append("</div>");
                html.append("<div class=\"card-body\">");
                    html.append("<ul class=\"list-links\">");
                        html.append("<li>");
                            html.append("<a href=\"#no-back\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.existe.comunicacao.servidor", responsavel));
                            html.append("\" onClick=\"postData('../v3/enviarComunicacao?acao=listar&").append(eConsigPageToken).append("')\">");
                            html.append(ApplicationResourcesHelper.getMessage("mensagem.existe.comunicacao.servidor", responsavel)).append("</a>");
                        html.append("</li>");
                    html.append("</ul>");
                html.append("</div>");
            html.append("</div>");
        }
    }

    public String getRseCodigo() {
        return rseCodigo;
    }

    public void setRseCodigo(String rseCodigo) {
        this.rseCodigo = rseCodigo;
    }
}
