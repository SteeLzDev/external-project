package com.zetra.econsig.web.controller.compra;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.dto.web.ColunaListaConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.InformacaoSerCompraEnum;
import com.zetra.econsig.values.MotivoAdeNaoRenegociavelEnum;
import com.zetra.econsig.web.controller.renegociacao.RenegociarConsignacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ComprarConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso ComprarConsignacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/comprarConsignacao" })
public class ComprarConsignacaoWebController extends RenegociarConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ComprarConsignacaoWebController.class);

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (responsavel.isCseSupOrg()) {
            carregarListaConsignataria(request, session, model, responsavel);
        }

        final boolean geraSenhaAutOtp = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel);

        // Se é validação de digital ou gera senha OTP, realiza a validação após selecionar o servidor, então não deve pedir senha
        if (!geraSenhaAutOtp && !ParamSist.paramEquals(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
            try {
                final InformacaoSerCompraEnum exigeInfCompra = parametroController.senhaServidorObrigatoriaCompra(responsavel);
                if (InformacaoSerCompraEnum.SENHA.equals(exigeInfCompra)) {
                    model.addAttribute("exibirCampoSenhaAutorizacao", Boolean.TRUE);
                } else if (InformacaoSerCompraEnum.CONTA_BANCARIA.equals(exigeInfCompra)) {
                    model.addAttribute("exibirCampoInfBancaria", Boolean.TRUE);
                }
            } catch (final ParametroControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        return super.iniciar(request, response, session, model);
    }

    @Override
    protected void carregarListaServico(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        try {
            final List<TransferObject> lstConvenio = convenioController.lstCnvEntidade(responsavel.getCodigoEntidade(), responsavel.getTipoEntidade(), "comprar", responsavel);
            final List<TransferObject> lstServico = TextHelper.groupConcat(lstConvenio, new String[]{Columns.SVC_DESCRICAO,Columns.SVC_CODIGO}, new String[]{Columns.CNV_COD_VERBA}, ",", true, true);
            model.addAttribute("lstServico", lstServico);
        } catch (final ConvenioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @RequestMapping(params = { "acao=comprarConsignacao" })
    @Override
    protected String renegociarConsignacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        model.addAttribute("comprar", true);
        return super.renegociarConsignacao(request, response, session, model);
    }

    @Override
    protected boolean validarSenhaServidor(String rseCodigo, boolean consomeSenha, HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        try {
            final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");

            try {
                SenhaHelper.validarSenha(request, rseCodigo, svcCodigo, false, true, consomeSenha, responsavel);
            } catch (final ViewHelperException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            // Verifica se a senha foi digitada e validada corretamente
            boolean senhaServidorOK = (request.getAttribute("senhaServidorOK") != null);

            if (senhaServidorOK) {
                session.setAttribute("senhaServidorRenegOK", rseCodigo);
            } else if ((session.getAttribute("senhaServidorRenegOK") != null)
                    && session.getAttribute("senhaServidorRenegOK").equals(rseCodigo)) {
                senhaServidorOK = true;
            } else {
                session.removeAttribute("senhaServidorRenegOK");
            }

            // Verifica se senha ou conta bancária é obrigatória
            final InformacaoSerCompraEnum exigeInfCompra = parametroController.senhaServidorObrigatoriaCompra(svcCodigo, rseCodigo, responsavel);
            final boolean exigeSenhaAcesso = (InformacaoSerCompraEnum.SENHA.equals(exigeInfCompra));
            final boolean exigeInfBancaria = (InformacaoSerCompraEnum.CONTA_BANCARIA.equals(exigeInfCompra));

            if (exigeSenhaAcesso && !senhaServidorOK) {
                if (TextHelper.isNull(rseCodigo)) {
                    // Se não tem rseCodigo, significa que não foi informado matrícula/cpf para a pesquisa
                    if (parametroController.requerMatriculaCpf(responsavel)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.pesquisa.matricula.e.cpf.obrigatorios", responsavel));
                    } else {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.pesquisa.matricula.ou.cpf.obrigatorios", responsavel));
                    }
                } else {
                    // Se tem rseCodigo, significa que a senha é inválida mesmo
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.consulta.invalida", responsavel));
                }
                return false;
            }
            if (exigeInfBancaria && !TextHelper.isNull(rseCodigo)) {
                final RegistroServidorTO rseTO = servidorController.findRegistroServidor(rseCodigo, responsavel);
                final String rseBancoSal   = TextHelper.formataParaComparacao(rseTO.getRseBancoSal());
                final String rseAgenciaSal = TextHelper.formataParaComparacao(rseTO.getRseAgenciaSal());
                final String rseContaSal   = TextHelper.formataParaComparacao(rseTO.getRseContaSal());
                final String rseBancoSalAlt   = TextHelper.formataParaComparacao(rseTO.getRseBancoSalAlternativo());
                final String rseAgenciaSalAlt = TextHelper.formataParaComparacao(rseTO.getRseAgenciaSalAlternativa());
                final String rseContaSalAlt   = TextHelper.formataParaComparacao(rseTO.getRseContaSalAlternativa());

                final String numBancoSal = TextHelper.formataParaComparacao(JspHelper.verificaVarQryStr(request, "numBanco"));
                final String numAgenciaSal = TextHelper.formataParaComparacao(JspHelper.verificaVarQryStr(request, "numAgencia"));
                final String numContaSal = TextHelper.formataParaComparacao(JspHelper.verificaVarQryStr(request, "numConta"));

                // Somente se as informações estiverem preenchidas na registro_servidor
                if ((!TextHelper.isNull(rseBancoSal) && !TextHelper.isNull(rseAgenciaSal) && !TextHelper.isNull(rseContaSal)) ||
                        (!TextHelper.isNull(rseBancoSalAlt) && !TextHelper.isNull(rseAgenciaSalAlt) && !TextHelper.isNull(rseContaSalAlt))) {
                    if ((!numBancoSal.equals(rseBancoSal) || !numAgenciaSal.equals(rseAgenciaSal) || !numContaSal.equals(rseContaSal)) &&
                            (!numBancoSal.equals(rseBancoSalAlt) || !numAgenciaSal.equals(rseAgenciaSalAlt) || !numContaSal.equals(rseContaSalAlt))){
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informacaoBancariaIncorreta", responsavel));
                        return false;
                    }
                }
            }
        } catch (ParametroControllerException | ServidorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return false;
        }

        return true;
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        if (!responsavel.isSer() &&
                ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) &&
                ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel)) {
            // Se exige validação de digital do servidor e exige senha para consulta de margem, redireciona para validação de digital do servidor
            return "validarOtp";
        } else if (!responsavel.isSer() && ParamSist.getBoolParamSist(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, responsavel)) {
            // Se exige validação de digital do servidor e exige senha para consulta de margem, redireciona para validação de digital do servidor
            return "validarDigital";
        } else {
            return "pesquisarConsignacao";
        }
    }

    @Override
    protected String continuarOperacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "ADE_NUMERO", required = true, defaultValue = "") String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final boolean semSenhaSer = session.getAttribute("SEMSENHA_SER") != null;

        if (semSenhaSer) {
            session.removeAttribute("SEMSENHA_SER");
        }

        if (!responsavel.isSer() && !semSenhaSer &&
                ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) &&
                ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel)) {

                // Se utiliza otp como senha de autorizção e exige senha para consulta de margem, redireciona para validação de digital do servidor
                return validarOtp(rseCodigo, request, response, session, model, true);
            } else if (!responsavel.isSer() && !semSenhaSer && ParamSist.getBoolParamSist(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, responsavel)) {
            // Se exige validação de digital do servidor e exige senha para consulta de margem, redireciona para validação de digital do servidor
            return validarDigital(rseCodigo, request, response, session, model);
        } else {
            return pesquisarConsignacao(rseCodigo, adeNumero, request, response, session, model);
        }
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.comprar.consignacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/comprarConsignacao");
        model.addAttribute("imageHeader", "i-operacional");
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        return CodedValues.SAD_CODIGOS_PORTABILIDADE;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        final List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        String link = "../v3/comprarConsignacao?acao=comprarConsignacao";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel);
        String msgAlternativa = "";
        String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.multiplo.comprar", responsavel);
        String msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("COMP_CONTRATO", CodedValues.FUN_COMP_CONTRATO, descricao, descricaoCompleta, "renegociar_contrato.gif", "btnComprarConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null,"chkADE"));

        if (responsavel.isCsaCor() && ParamSist.paramEquals(CodedValues.TPC_LST_HIST_PARCELA_ADE_TERCEIRO_COMPRA, CodedValues.TPC_SIM, responsavel)) {
            // Adiciona o editar consignação
            link = "../v3/comprarConsignacao?acao=detalharConsignacao";
            descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.detalhar.abreviado", responsavel);
            descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.detalhar", responsavel);
            msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
            msgConfirmacao = "";
            msgAdicionalConfirmacao = "";

            acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta, "pesquisar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null, null));
        }

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        final TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "comprar");

        final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
        final String csaCodigo = (responsavel.isCsaCor()) ? responsavel.getCsaCodigo() : JspHelper.verificaVarQryStr(request, "CSA_CODIGO");

        criterio.setAttribute(Columns.SVC_CODIGO, svcCodigo);
        criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);

        if (responsavel.isCor() && !responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
            criterio.setAttribute(Columns.COR_CODIGO, responsavel.getCodigoEntidade());
        }

        return criterio;
    }

    @Override
    protected List<ColunaListaConsignacao> definirColunasListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        final boolean resultadoMultiplosServidores = (request.getAttribute("resultadoMultiplosServidores") != null);

        final List<ColunaListaConsignacao> colunas = new ArrayList<>();

        try {
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_CONSIGNATARIA, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_CONSIGNATARIA, ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_NUMERO, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_NUMERO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel), ColunaListaConsignacao.TipoValor.NUMERICO));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_SERVICO, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SERVICO, ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_SERVIDOR, responsavel) && resultadoMultiplosServidores) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SERVIDOR, ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_RESERVA, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_RESERVA, ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.inclusao", responsavel), ColunaListaConsignacao.TipoValor.DATA));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA, ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela.abreviado", responsavel), ColunaListaConsignacao.TipoValor.MONETARIO));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.prazo.abreviado", responsavel), ColunaListaConsignacao.TipoValor.NUMERICO));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_PARCELAS_PAGAS, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_PARCELAS_PAGAS, ApplicationResourcesHelper.getMessage("rotulo.consignacao.pagas", responsavel), ColunaListaConsignacao.TipoValor.NUMERICO));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_STATUS, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_STATUS, ApplicationResourcesHelper.getMessage("rotulo.consignacao.status", responsavel)));
            }
            if (ParamSist.paramEquals(CodedValues.TPC_LISTAR_MOTIVO_ADES_NAO_RENEGOCIAVEIS, CodedValues.TPC_SIM, responsavel)) {
                colunas.add(new ColunaListaConsignacao(MotivoAdeNaoRenegociavelEnum.CHAVE_MOTIVO_INDISPONIBILIDADE, ApplicationResourcesHelper.getMessage("mensagem.indisponibilidade.renegociacao.titulo", responsavel), ColunaListaConsignacao.TipoValor.TEXTO, true));
            }
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return colunas;
    }

    @Override
    protected void carregarInformacoesAcessorias(String rseCodigo, String adeNumero, List<TransferObject> lstConsignacao, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws AutorizacaoControllerException {
        // Verifica se as consignações pertencem ao mesmo registro servidor
        if (request.getAttribute("resultadoMultiplosServidores") != null) {
            throw new AutorizacaoControllerException("mensagem.erro.multiplo.servidor.nao.permitido", responsavel);
        }

        try {
            // Carrega parâmetro com a quantidade máxima de consignações permitidas para compra
            final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
            if (!TextHelper.isNull(svcCodigo)) {
                final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

                if (!TextHelper.isNull(paramSvcCse.getTpsQtdeMaxAdeCompra())) {
                    try {
                        // Valida, caso o parâmetro não seja numérico
                        final String qtdMaxCompra = String.valueOf(Integer.parseInt(paramSvcCse.getTpsQtdeMaxAdeCompra()));
                        model.addAttribute("qtdMaxSelecaoMultipla", qtdMaxCompra);
                        model.addAttribute("msgErroQtdMaxSelecaoMultiplaSuperada", ApplicationResourcesHelper.getMessage("mensagem.erro.limite.contratos.compra.operacao", responsavel, qtdMaxCompra));
                    } catch (final NumberFormatException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    protected String getFunCodigo() {
        return CodedValues.FUN_COMP_CONTRATO;
    }

    @Override
    @RequestMapping(params = { "acao=listarHistLiquidacoesAntecipadas" })
    public String listarHistLiquidacoesAntecipadas(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return super.listarHistLiquidacoesAntecipadas(request, response, session, model);
    }

    @Override
    @RequestMapping(params = { "acao=pesquisarConsignacao" })
    public String pesquisarConsignacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "ADE_NUMERO", required = true, defaultValue = "") String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
        CustomTransferObject paramSvcCondicionaPortabilidade = null;
        try {
            paramSvcCondicionaPortabilidade = parametroController.getParamSvcCse(svcCodigo, CodedValues.TPS_CONDICIONA_OPERACAO_PORTABILIDADE, responsavel);
        } catch (final ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String condicionaPortabilidade = paramSvcCondicionaPortabilidade != null ? (String) paramSvcCondicionaPortabilidade.getAttribute(Columns.PSE_VLR) : "N";

        if (CodedValues.TPC_NAO.equals(condicionaPortabilidade) || condicionaPortabilidade.isEmpty()) {

            return super.pesquisarConsignacao(rseCodigo, adeNumero, request, response, session, model);

        } else {


            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            try {
                String tipoEntidade = responsavel.getTipoEntidade();
                String codigoEntidade = responsavel.getCodigoEntidade();

                if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                    tipoEntidade = AcessoSistema.ENTIDADE_EST;
                    codigoEntidade = responsavel.getCodigoEntidadePai();
                } else if (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                    tipoEntidade = AcessoSistema.ENTIDADE_CSA;
                    codigoEntidade = responsavel.getCodigoEntidadePai();
                }

                final List<String> svcCodigos = definirSvcCodigoPesquisa(request, session, responsavel);
                final List<String> sadCodigos = definirSadCodigoPesquisa(request, session, responsavel);

                final CustomTransferObject criterio = new CustomTransferObject();
                final TransferObject criteriosPesqPadrao = recuperarCriteriosPesquisaPadrao(request, responsavel);
                if (criteriosPesqPadrao != null) {
                    criterio.setAtributos(criteriosPesqPadrao.getAtributos());

                    // TODO Remover quando as páginas das operações forem refatoradas, de modo a ficar independente do parâmetro tipo
                    if (criteriosPesqPadrao.getAttribute("TIPO_OPERACAO") != null) {
                        model.addAttribute("tipoOperacao", criteriosPesqPadrao.getAttribute("TIPO_OPERACAO").toString());
                    }
                }

                final int size = -1;
                int offset = (!TextHelper.isNull(request.getParameter("offset")) && TextHelper.isNum(request.getParameter("offset"))) ? Integer.parseInt(request.getParameter("offset")) : 0;
                offset = -1;
                List<TransferObject> lstConsignacao = new ArrayList<>();
                lstConsignacao = pesquisarConsignacaoController.pesquisaAutorizacao(tipoEntidade, codigoEntidade, rseCodigo, null, null, sadCodigos, svcCodigos, offset, size, criterio, responsavel);

                try {
                    lstConsignacao = parametroController.filtraAdeRestringePortabilidade(lstConsignacao, rseCodigo, svcCodigo, responsavel);
                } catch (final ParametroControllerException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                final int total = lstConsignacao.size();
                final List<AcaoConsignacao> listaAcoes = definirAcoesListaConsignacao(request, responsavel);

                final String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
                final String serCpf = JspHelper.verificaVarQryStr(request, "SER_CPF");

                if (total == 0) {
                    final StringBuilder msg = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.erro.nenhum.registro", responsavel)).append(":<br>");
                    if (!"".equals(rseMatricula)) {
                        msg.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula", responsavel)).append(": <span class=\"normal\">").append(rseMatricula).append("</span> ");
                    }
                    if (!"".equals(serCpf)) {
                        msg.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel)).append(": <span class=\"normal\">").append(serCpf).append("</span>");
                    }

                    // Se não é o servidor que está listando suas consignações e não é pesquisa avançada,
                    // se não encontrou nada, retorna para a página de pesquisa
                    if (TextHelper.isNull(session.getAttribute(CodedValues.MSG_INFO))) {
                        session.setAttribute(CodedValues.MSG_ERRO, msg.toString());

                        if (!TextHelper.isNull(request.getParameter("linkRetHistoricoFluxo"))) {
                            // TODO : Remover tratamento de link de retorno fixo.
                            final String linkRet = request.getParameter("linkRetHistoricoFluxo").replace('$', '?').replace('(', '=').replace('|', '&');
                            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(linkRet, request)));
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.nenhumaConsignacaoEncontrada", responsavel));
                            return "jsp/redirecionador/redirecionar";
                        } else {
                            return tratarConsignacaoNaoEncontrada(request, response, session, model);
                        }
                    }

                } else {
                    if (TextHelper.isNull(rseCodigo) && (lstConsignacao.size() > 0)) {
                        boolean resultadoMultiplosServidores = false;
                        String rseCodigoAtual = "";
                        String rseCodigoAnterior = "";
                        for (final TransferObject ade : lstConsignacao) {
                            rseCodigoAtual = ade.getAttribute(Columns.RSE_CODIGO).toString();
                            if (!TextHelper.isNull(rseCodigoAnterior) && !rseCodigoAtual.equals(rseCodigoAnterior)) {
                                resultadoMultiplosServidores = true;
                                break;
                            }
                            rseCodigoAnterior = rseCodigoAtual;
                        }

                        if (resultadoMultiplosServidores) {
                            // SE possuem múltiplos servidores, seta atributo na request
                            request.setAttribute("resultadoMultiplosServidores", Boolean.TRUE);
                        } else {
                            rseCodigo = rseCodigoAtual;
                        }
                    }

                    if ((!TextHelper.isNull(rseCodigo)) && (lstConsignacao.size() > 0)) {
                        final CustomTransferObject first = (CustomTransferObject) lstConsignacao.get(0);
                        String tituloResultado = null;
                        if (ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                            tituloResultado = first.getAttribute(Columns.RSE_MATRICULA) + " - " + first.getAttribute(Columns.SER_NOME);
                        } else {
                            tituloResultado = first.getAttribute(Columns.RSE_MATRICULA) + " - " + first.getAttribute(Columns.SER_CPF) + " - " + first.getAttribute(Columns.SER_NOME);
                        }

                        final String footerResultado = ApplicationResourcesHelper.getMessage("rotulo.consignacao.listagem.consignacao.servidor", responsavel, (String) first.getAttribute(Columns.SER_NOME));

                        model.addAttribute("footerResultado", footerResultado);
                        model.addAttribute("tituloResultado", tituloResultado);
                    }
                }

                // Valida a senha após a pesquisa, pois caso o RSE_CODIGO não tenha sido passado, será obtido da listagem
                final boolean geraSenhaAutOtp = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel);
                boolean exigeSenhaSer = true;
                try {
                    exigeSenhaSer = parametroController.verificaAutorizacaoReservaSemSenha(rseCodigo, svcCodigo, exigeSenhaSer, null, responsavel);
                } catch (final ParametroControllerException e) {
                    // Qualquer erro ao buscar essa validação por default a senha sempre é true para garantir a segurança do processo.
                }

                if (exigeSenhaSer && !validarSenhaServidor(rseCodigo, geraSenhaAutOtp, request, session, responsavel)) {
                    return iniciar(request, response, session, model);
                }

                model.addAttribute("lstConsignacao", lstConsignacao);

                /**
                 * Caso seja compra de contrato, seta os dados da operação após validar a senha do servidor para permitir que
                 * o responsável autorizado visualize os detalhes dos contratos disponíveis para compra.
                 */
                if (CodedValues.FUN_COMP_CONTRATO.equals(responsavel.getFunCodigo())) {
                    final List<String> adeCodigos = new ArrayList<>();
                    for (final TransferObject ade : lstConsignacao) {
                        adeCodigos.add(ade.getAttribute(Columns.ADE_CODIGO).toString());
                    }
                    responsavel.setDadosOperacao(rseCodigo, adeCodigos);
                }

                model.addAttribute("rseCodigo", rseCodigo);
                model.addAttribute("rseMatricula", rseMatricula);
                model.addAttribute("serCpf", serCpf);

                // Define clausulas nescesaria para pagina de listar consignação
                final boolean pesquisaAvancada = false;
                final boolean adeNumerosVazio = false;

                model.addAttribute("exibeAtivoInativo", false);
                model.addAttribute("exibeInativo", false);
                model.addAttribute("pesquisaAvancada", pesquisaAvancada);
                model.addAttribute("adeNumerosVazio", adeNumerosVazio);

                // Monta lista de parâmetros através dos parâmetros de request
                final Set<String> params = new HashSet<>(request.getParameterMap().keySet());

                // Ignora os parâmetros abaixo
                params.remove("exibeInativo");
                params.remove("offsetInativo");
                params.remove("senha");
                params.remove("serAutorizacao");
                params.remove("cryptedPasswordFieldName");
                params.remove("offset");
                params.remove("back");
                params.remove("linkRet");
                params.remove("linkRet64");
                params.remove("eConsig.page.token");
                params.remove("_skip_history_");
                params.remove("pager");
                params.remove("acao");

                final List<String> requestParams = new ArrayList<>(params);

                // Monta link de paginação
                final StringBuilder linkListagemAde = new StringBuilder().append(request.getRequestURI()).append("?acao=pesquisarConsignacao");
                String queryStringRseCodigo = "";
                if (TextHelper.isNull(request.getParameter("RSE_CODIGO")) && !TextHelper.isNull(rseCodigo)) {
                    // Necessário caso tenha vindo direto sem passar pela seleção de servidor
                    linkListagemAde.append("&RSE_CODIGO=").append(rseCodigo);
                    queryStringRseCodigo = "RSE_CODIGO=" + rseCodigo + "&";
                }

                configurarPaginador(linkListagemAde.toString(), "rotulo.paginacao.titulo.consignacao", total, size, requestParams, false, request, model);

                model.addAttribute("queryString", queryStringRseCodigo + getQueryString(requestParams, request));

                // Define lista de ações
                model.addAttribute("listaAcoes", listaAcoes);

                // Define lista de colunas
                final List<ColunaListaConsignacao> lstColunas = definirColunasListaConsignacao(request, responsavel);
                model.addAttribute("listaColunas", lstColunas);

                // Carrega informações acessórias
                carregarInformacoesAcessorias(rseCodigo, adeNumero, lstConsignacao, request, session, model, responsavel);

                // Formata os valores a serem exibidos
                formatarValoresListaConsignacao(lstConsignacao, lstColunas, request, session, responsavel);

                // Define se a coluna de CheckBox sera exibida no carregamento da pagina por padrao ou nao
                final boolean ocultarColunaCheckBox = ocultarColunaCheckBox(responsavel);
                model.addAttribute("ocultarColunaCheckBox", ocultarColunaCheckBox);

                // Redireciona para a página de listagem
                return viewRedirect("jsp/consultarConsignacao/listarConsignacao", request, session, model, responsavel);
            } catch (final AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            } catch (final NumberFormatException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

        }

    }
}
