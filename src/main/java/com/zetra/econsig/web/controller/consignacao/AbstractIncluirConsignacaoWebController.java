package com.zetra.econsig.web.controller.consignacao;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.PrazoTransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.entidade.TermoAdesaoServicoTO;
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.dto.parametros.AlongarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.RenegociarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.dto.web.ResultadoSimulacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.consignacao.AutorizacaoHelper;
import com.zetra.econsig.helper.consignacao.GAPHelper;
import com.zetra.econsig.helper.consignacao.ReservaMargemHelper;
import com.zetra.econsig.helper.criptografia.JCryptOld;
import com.zetra.econsig.helper.margem.CasamentoMargem;
import com.zetra.econsig.helper.margem.ControleConsulta;
import com.zetra.econsig.helper.margem.ExibeMargem;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.markdown.Markdown4jProcessorExtended;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.processareserva.ProcessaReservaMargem;
import com.zetra.econsig.helper.processareserva.ProcessaReservaMargemFactory;
import com.zetra.econsig.helper.seguranca.AcessoFuncaoServico;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.GeraTelaSegundaSenhaHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.usuario.KYCHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.Beneficiario;
import com.zetra.econsig.persistence.entity.Calendario;
import com.zetra.econsig.service.beneficios.BeneficiarioController;
import com.zetra.econsig.service.calendario.CalendarioController;
import com.zetra.econsig.service.consignacao.AlongarConsignacaoController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignacao.RenegociarConsignacaoController;
import com.zetra.econsig.service.consignacao.ReservarMargemController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.financiamentodivida.FinanciamentoDividaController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.indice.IndiceController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.sdp.PostoRegistroServidorController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.service.sistema.TermoAdesaoServicoController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.InformacaoSerCompraEnum;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AbstractIncluirConsignacaoWebController</p>
 * <p>Description: Controlador Web base para o casos de uso de inclusão de consignação.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class AbstractIncluirConsignacaoWebController extends AbstractConsultarConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AbstractIncluirConsignacaoWebController.class);

    @Autowired
    private AlongarConsignacaoController alongarConsignacaoController;

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private CalendarioController calendarioController;

    @Autowired
    private PostoRegistroServidorController postoRegistroServidorController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private FinanciamentoDividaController financiamentoDividaController;

    @Autowired
    private IndiceController indiceController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PeriodoController periodoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    @Qualifier("renegociarConsignacaoController")
    private RenegociarConsignacaoController renegociarConsignacaoController;

    @Autowired
    @Qualifier("reservarMargemController")
    private ReservarMargemController reservarMargemController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private SimulacaoController simulacaoController;

    @Autowired
    private SistemaController sistemaController;

    @Autowired
    private TermoAdesaoServicoController termoAdesaoController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private BeneficiarioController beneficiarioController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (responsavel.isCseSupOrg()) {
            carregarListaConsignataria(request, session, model, responsavel);
        }

        final boolean listagemDinamicaDeServicos = temListagemDinamicaDeServicos(responsavel);
        model.addAttribute("listagemDinamicaDeServicos", listagemDinamicaDeServicos);
        if (listagemDinamicaDeServicos) {
            model.addAttribute("lstServico", new ArrayList<>());
        } else {
            carregarListaServico(request, session, model, responsavel);
        }

        try {
            // Omite campo de ADE_NUMERO
            model.addAttribute("omitirAdeNumero", Boolean.TRUE);

            // Exibe inclusão avançada
            if (responsavel.temPermissao(CodedValues.FUN_INCLUSAO_AVANCADA_CONSIGNACAO) && CodedValues.FUN_RES_MARGEM.equals(responsavel.getFunCodigo())) {
                model.addAttribute("exibirInclusaoAvancada", Boolean.TRUE);

                model.addAttribute("lstMtvOperacao", tipoMotivoOperacaoController.lstMotivoOperacaoConsignacao(CodedValues.STS_ATIVO, responsavel));

                model.addAttribute("lstTipoJustica", sistemaController.lstTipoJustica(responsavel));
            }

            final boolean geraSenhaAutOtp = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel);
            boolean exigeSenha = false;

            // Se é validação de digital, realiza a validação após selecionar o servidor, então não deve pedir senha
            // Se não tem validação de digital, e a senha é obrigatória para consultar margem, então deve exibir campo de senha
            if ((!geraSenhaAutOtp && !ParamSist.paramEquals(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, CodedValues.TPC_SIM, responsavel)) && parametroController.senhaServidorObrigatoriaConsultaMargem(null, responsavel)) {
                model.addAttribute("exibirCampoSenha", Boolean.TRUE);
                exigeSenha = true;
            }

            // Se a senha é obrigatória para exibir dados cadastrais, então habilita exibição do campo de senha
            if (!geraSenhaAutOtp && ParamSist.paramEquals(CodedValues.TPC_SENHA_SER_OBRIGATORIA_EXIBIR_DADOS_CSA_COR, CodedValues.TPC_SIM, responsavel) && responsavel.isCsaCor() && CodedValues.FUN_RES_MARGEM.equals(responsavel.getFunCodigo())) {
                model.addAttribute("exibirCampoSenha", Boolean.TRUE);
                model.addAttribute("senhaObrigatoriaConsulta", Boolean.TRUE);
                exigeSenha = true;
            }

            // Quando não exige senha, porém o parâmetro de validação digital esta ativo, precisamos fazer verificação se exigirá ou não a senha depois que selecionar o servidor
            if ((exigeSenha || ParamSist.paramEquals(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, CodedValues.TPC_SIM, responsavel)) && responsavel.isCsa() && (ParamSist.getIntParamSist(CodedValues.TPC_DIAS_VALIDADE_AUTORIZACAO_SERVIDOR_CON_MAR_POR_COD, 0, responsavel) > 0)) {
                final List<String> funcoesAutorizacaoSemSenha = new ArrayList<>();
                funcoesAutorizacaoSemSenha.add(CodedValues.FUN_RES_MARGEM);
                funcoesAutorizacaoSemSenha.add(CodedValues.FUN_AUT_RESERVA);
                funcoesAutorizacaoSemSenha.add(CodedValues.FUN_COMP_CONTRATO);

                if (funcoesAutorizacaoSemSenha.contains(responsavel.getFunCodigo())) {
                    model.addAttribute("verificaAutorizacaoSemSenha", Boolean.TRUE);
                }
            }
        } catch (TipoMotivoOperacaoControllerException | ParametroControllerException | ConsignanteControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return super.iniciar(request, response, session, model);
    }

    protected boolean redirecionarTermoAdesao(HttpServletRequest request, HttpSession session) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            final String svcCodigo = request.getParameter("SVC_CODIGO");
            if (TextHelper.isNull(svcCodigo)) {
                return false;
            }

            final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            // Se exige aceite do termo de adesão + o parâmetro de serviço TPS_EXIGE_TERMO_DE_ADESAO_RESERVAR_MARGEM_ANTES_DE_VALORES_OPERACAO for TRUE -> redireciona para a página com o termo antes dos valores da operação.
            return ParamSist.paramEquals(CodedValues.TPC_TEM_TERMO_ADESAO, CodedValues.TPC_SIM, responsavel) && paramSvcCse.isTpsExigeAceiteTermoAdesao() && paramSvcCse.isTpsExigeAceiteTermoAdesaoAntesValores();

        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return false;
        }
    }

    protected boolean temListagemDinamicaDeServicos(AcessoSistema responsavel) {
        return false;
    }

    @Override
    protected String continuarOperacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "ADE_NUMERO", required = true, defaultValue = "") String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        boolean exigeSenhaConsultaMargem = false;
        try {
            exigeSenhaConsultaMargem = parametroController.senhaServidorObrigatoriaConsultaMargem(null, responsavel);

            // Se a senha é obrigatória para exibir dados cadastrais, então habilita exibição do campo de senha
            if (ParamSist.paramEquals(CodedValues.TPC_SENHA_SER_OBRIGATORIA_EXIBIR_DADOS_CSA_COR, CodedValues.TPC_SIM, responsavel) && responsavel.isCsaCor() && CodedValues.FUN_RES_MARGEM.equals(responsavel.getFunCodigo())) {
                exigeSenhaConsultaMargem = true;
            }

            final String svcCodigo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SVC_CODIGO")) ? JspHelper.verificaVarQryStr(request, "SVC_CODIGO") : null;
            exigeSenhaConsultaMargem = parametroController.verificaAutorizacaoReservaSemSenha(rseCodigo, svcCodigo, exigeSenhaConsultaMargem, adeNumero, responsavel);
        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (!responsavel.isSer() && ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel) && exigeSenhaConsultaMargem) {

            // Se utiliza otp como senha de autorizção e exige senha para consulta de margem, redireciona para validação de digital do servidor
            return validarOtp(rseCodigo, request, response, session, model, true);
        } else if (!responsavel.isSer() && ParamSist.getBoolParamSist(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, responsavel) && exigeSenhaConsultaMargem) {
            // Se exige validação de digital do servidor e exige senha para consulta de margem, redireciona para validação de digital do servidor
            return validarDigital(rseCodigo, request, response, session, model);
        } else if (redirecionarTermoAdesao(request, session)) {
            // Se exige aceite do termo de adesão + o parâmetro de serviço TPS_EXIGE_TERMO_DE_ADESAO_RESERVAR_MARGEM_ANTES_DE_VALORES_OPERACAO for TRUE -> redireciona para a página com o termo antes dos valores da operação.
            return aceitarTermoAdesao(rseCodigo, request, response, session, model);
        } else {
            return reservarMargem(rseCodigo, request, response, session, model);
        }
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        final HttpSession session = request.getSession();

        boolean exigeSenhaConsultaMargem = false;
        try {
            exigeSenhaConsultaMargem = parametroController.senhaServidorObrigatoriaConsultaMargem(null, responsavel);
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        if (!responsavel.isSer() && ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel) && exigeSenhaConsultaMargem) {
            // Se exige validação de digital do servidor e exige senha para consulta de margem, redireciona para validação de digital do servidor
            return "validarOtp";
        } else if (!responsavel.isSer() && ParamSist.getBoolParamSist(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, responsavel) && exigeSenhaConsultaMargem) {
            // Se exige validação de digital do servidor e exige senha para consulta de margem, redireciona para validação de digital do servidor
            return "validarDigital";
        } else if (redirecionarTermoAdesao(request, session)) {
            // Se exige aceite do termo de adesão + o parâmetro de serviço TPS_EXIGE_TERMO_DE_ADESAO_RESERVAR_MARGEM_ANTES_DE_VALORES_OPERACAO for TRUE -> redireciona para a página com o termo antes dos valores da operação.
            return "aceitarTermoAdesao";
        } else {
            return "reservarMargem";
        }
    }

    /**
     * Retorna qual a função de permissão para o caso de uso que implementa a inclusão de consignação.
     * @return
     */
    protected abstract String getFunCodigo();

    protected abstract String validarServicoOperacao(String svcCodigo, String rseCodigo, Map<String, String> parametrosPlano, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException;

    protected String validarConsignatariaOperacao(String csaCodigo, String rseCodigo, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ConsignatariaControllerException {
        csaCodigo = request.getParameter("CSA_CODIGO");
        if (responsavel.isCsaCor()) {
            csaCodigo = responsavel.getCsaCodigo();
        } else {
            final ConsignatariaTransferObject consignataria = consignatariaController.findConsignataria(csaCodigo, responsavel);

            final String csaNome = consignataria.getCsaIdentificador() + " - " + (!TextHelper.isNull(consignataria.getCsaNomeAbreviado()) ? consignataria.getCsaNomeAbreviado() : consignataria.getCsaNome());
            model.addAttribute("csaNome", csaNome);
        }

        return csaCodigo;

    }

    protected boolean possuiVariacaoMargem(AcessoSistema responsavel) {
        // Parâmetro para exibição de variação de margem
        return (responsavel.isCseSupOrg() && ParamSist.getBoolParamSist(CodedValues.TPC_MOSTRA_VARIACAO_MARGEM_CSE_ORG, responsavel)) || (responsavel.isCsaCor() && ParamSist.getBoolParamSist(CodedValues.TPC_MOSTRA_VARIACAO_MARGEM_CSA_COR, responsavel));
    }

    protected boolean possuiComposicaoMargem(AcessoSistema responsavel) {
        // Parametro que mostra a composição da margem do servidor na reserva.
        return (ParamSist.getInstance().getParam(CodedValues.TPC_MOSTRA_COMPOSICAO_MARGEM, responsavel) != null) && CodedValues.TPC_SIM.equals(ParamSist.getInstance().getParam(CodedValues.TPC_MOSTRA_COMPOSICAO_MARGEM, responsavel).toString());
    }

    protected boolean temControleCompulsorios(AcessoSistema responsavel) {
        // Parâmetro para controle de compulsórios
        return ParamSist.getBoolParamSist(CodedValues.TPC_TEM_CONTROLE_DE_ESTOQUE, responsavel) && ParamSist.getBoolParamSist(CodedValues.TPC_TEM_CONTROLE_DE_COMPULSORIOS, responsavel);
    }

    protected BigDecimal buscarTaxaCadastrada(Integer adePrazo, AcessoSistema responsavel, HttpServletRequest request) throws SimulacaoControllerException {
        // Busca taxa cadastrada para o prazo informado (usado na solicitação de reserva de cartão pelo servidor para buscar a taxa)
        return null;
    }

    @RequestMapping(params = { "acao=reservarMargem" })
    public String reservarMargem(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            final CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            if (servidor == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            model.addAttribute("servidor", servidor);

            // verifica se há permissão para parâmetros de inclusão avançada
            final boolean usuPossuiIncAvancadaAde = responsavel.temPermissao(CodedValues.FUN_INCLUSAO_AVANCADA_CONSIGNACAO);

            // Quando a inclusão é de uma decisão judicial, é como se fosse uma inclusão avançada, desabilitando todas as validações
            final boolean inclusaoJudicial = request.getParameter("inclusaoJudicial") != null;

            // Verifica opções para inclusão avançada de contrato
            final boolean validaDadosBancariosAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaDadosBancarios")) ? Boolean.parseBoolean(request.getParameter("validaDadosBancarios")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_DADOS_BANCARIOS;
            final boolean validaSenhaServidorAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaSenhaServidor")) ? Boolean.parseBoolean(request.getParameter("validaSenhaServidor")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_SENHA_SERVIDOR;
            final boolean validaTaxaAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaTaxa")) ? Boolean.parseBoolean(request.getParameter("validaTaxa")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_TAXA_JUROS;
            final boolean validaPrazoAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaPrazo")) ? Boolean.parseBoolean(request.getParameter("validaPrazo")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_TAXA_JUROS;
            final boolean validaBloqSerCnvCsaAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaBloqSerCnvCsa")) ? Boolean.parseBoolean(request.getParameter("validaBloqSerCnvCsa")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_BLOQ_SER_CNV_CSA;
            final boolean validaDataNascAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaDataNascimento")) ? Boolean.parseBoolean(request.getParameter("validaDataNascimento")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_DATA_NASCIMENTO;
            final boolean validaLimiteAdeAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaLimiteAde")) ? Boolean.parseBoolean(request.getParameter("validaLimiteAde")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_LIMITE_ADE;
            final boolean validaMargemAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaMargem")) ? Boolean.parseBoolean(request.getParameter("validaMargem")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_MARGEM;

            // Obtém os parâmetros de plano de desconto com base no plano/serviço selecionado
            final Map<String, String> parametrosPlano = new HashMap<>();
            final String svcCodigo = validarServicoOperacao(null, rseCodigo, parametrosPlano, request, session, model, responsavel);

            final String rseMatricula = servidor.getAttribute(Columns.RSE_MATRICULA).toString();
            final String orgCodigo = servidor.getAttribute(Columns.ORG_CODIGO).toString();
            final String serCodigo = servidor.getAttribute(Columns.SER_CODIGO).toString();
            final String serCpf = servidor.getAttribute(Columns.SER_CPF).toString();

            if (!AcessoFuncaoServico.temAcessoFuncao(request, getFunCodigo(), responsavel.getUsuCodigo(), svcCodigo)) {
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String csaCodigo = null;
            String corCodigo = null;
            boolean portalBeneficio = false;
            if (!"".equals(JspHelper.verificaVarQryStr(request, "PORTAL_BENEFICIO")) && !"".equals(JspHelper.verificaVarQryStr(request, "COR_CODIGO"))) {
                corCodigo = JspHelper.verificaVarQryStr(request, "COR_CODIGO");
                final CorrespondenteTransferObject correspondente = consignatariaController.findCorrespondente(corCodigo, responsavel);
                csaCodigo = !TextHelper.isNull(correspondente) ? correspondente.getCsaCodigo() : null;
                portalBeneficio = true;

                model.addAttribute("portalBeneficio", true);
                model.addAttribute("corCodigo", corCodigo);
            }

            csaCodigo = TextHelper.isNull(csaCodigo) ? validarConsignatariaOperacao(null, rseCodigo, request, session, model, responsavel) : csaCodigo;
            corCodigo = TextHelper.isNull(corCodigo) ? responsavel.getCorCodigo() : corCodigo;

            CustomTransferObject convenio = null;
            try {
                // verfica se o servico e consignataria escolhida pode reservar uma margem para este servidor
                convenio = convenioController.getParamCnv(csaCodigo, orgCodigo, svcCodigo, validaBloqSerCnvCsaAvancado, validaBloqSerCnvCsaAvancado, responsavel);
            } catch (final ConvenioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
            if (convenio == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.convenio.inexistente.ser", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            final String cnvCodigo = convenio.getAttribute(Columns.CNV_CODIGO).toString();

            // Verifica quantidade de contratos por grupo de serviço e numero de consignatarias
            try {
                final boolean validaEntidades = validaBloqSerCnvCsaAvancado;

                final boolean telaConfirmacaoDuplicidade = "S".equals(request.getParameter("telaConfirmacaoDuplicidade"));
                autorizacaoController.podeReservarMargem(cnvCodigo, corCodigo, rseCodigo, validaEntidades, true, validaEntidades, null, null, null, null, 0, null, null, null, "RESERVAR", validaLimiteAdeAvancado, telaConfirmacaoDuplicidade, responsavel);
            } catch (final AutorizacaoControllerException ex) {
                final String messageKey = ex.getMessageKey();
                if ("mensagem.erro.ade.duplicidade.bloqueada.ate.data.limite".equals(messageKey)) {
                    return verificarPossibilidadePermitirDuplicidade(request, session, model, responsavel, cnvCodigo, "reservarMargem", ex);
                } else if (responsavel.isCsaCor() && ParamSist.getBoolParamSist(CodedValues.TPC_LST_SERVICOS_CSA_COR_ABAIXO_LIMITE_CONTRATOS, responsavel) &&
                        ("mensagem.qtdMaxContratosExcedida".equals(messageKey) || "mensagem.erro.nao.possivel.inserir.ou.alterar.reserva.pois.excede.limite.contratos.do.servidor.para.este.servico".equals(messageKey)
                                || "mensagem.erro.nao.possivel.inserir.ou.alterar.reserva.pois.excede.limite.contratos.do.servidor.para.esta.natureza.servico".equals(messageKey))) {

                    final List<TransferObject> lstConvenio = autorizacaoController.verificaLimiteServicosNaoAtigindos(rseCodigo, responsavel);
                    if((lstConvenio != null) && (lstConvenio.size() > 1)) {
                        final List<TransferObject> lstServico = TextHelper.groupConcat(lstConvenio, new String[]{Columns.SVC_DESCRICAO,Columns.SVC_CODIGO}, new String[]{Columns.CNV_COD_VERBA}, ",", true, true);

                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.limite.max.servico.reserva", responsavel));
                        session.setAttribute("lstServico", lstServico);
                        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL("../v3/reservarMargem?acao=iniciar&RSE_CODIGO="+rseCodigo+"&RSE_MATRICULA="+rseMatricula+"&SER_CPF="+serCpf, request)));
                        return "jsp/redirecionador/redirecionar";
                    }
                }
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            boolean exigeSenhaConsultaMargem = parametroController.senhaServidorObrigatoriaConsultaMargem(null, responsavel);
            exigeSenhaConsultaMargem = parametroController.verificaAutorizacaoReservaSemSenha(rseCodigo, svcCodigo, exigeSenhaConsultaMargem, null, responsavel);
            // Se exige senha para consulta de margem, valida senha caso informada
            if (!responsavel.isSer() && exigeSenhaConsultaMargem) {
                try {
                    // Se envia senha de autorização OTP para o servidor, inclui span para exibir mensagem de sucesso.
                    final boolean geraSenhaAutOtp = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel);
                    SenhaHelper.validarSenha(request, rseCodigo, svcCodigo, false, false, geraSenhaAutOtp, responsavel);
                } catch (final ViewHelperException ex) {
                    // Paraná: ao receber 'senha expirada' a CSA poderá ativar a senha.
                    if (ex.getMessageKey().indexOf("mensagem.erro.senha.expirada.certifique.ativacao") != -1) {
                        session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.expirada.ativar", responsavel));
                        // Redireciona para JSP específico de ativação de senha eConsig PR
                        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL("../v3/ativarSenhaServidor?acao=iniciar", request)));
                        return "jsp/redirecionador/redirecionar";
                    } else {
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        return iniciar(request, response, session, model);
                    }
                }
            }

            // Seleciona os vínculos que não podem reservar margem para este csa e svc
            autorizacaoController.verificaBloqueioVinculoCnvAlertaSessao(session, csaCodigo, svcCodigo, (String) servidor.getAttribute(Columns.RSE_VRS_CODIGO), responsavel);

            final String serDataNasc = servidor.getAttribute(Columns.SER_DATA_NASC) != null ? DateHelper.reformat(servidor.getAttribute(Columns.SER_DATA_NASC).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern()) : "";
            final String numBanco = JCryptOld.crypt("IB", servidor.getAttribute(Columns.RSE_BANCO_SAL) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_BANCO_SAL).toString()) : "");
            final String numAgencia = JCryptOld.crypt("IB", servidor.getAttribute(Columns.RSE_AGENCIA_SAL) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_AGENCIA_SAL).toString()) : "");
            final int sizeNumAgencia = servidor.getAttribute(Columns.RSE_AGENCIA_SAL) == null ? 0 : servidor.getAttribute(Columns.RSE_AGENCIA_SAL).toString().length();
            final String numConta = servidor.getAttribute(Columns.RSE_CONTA_SAL) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_CONTA_SAL).toString()) : "";

            String numConta1 = "";
            String numConta2 = "";

            if (numConta.length() > 0) {
                numConta1 = numConta.substring(0, numConta.length() / 2);
                numConta2 = numConta.substring(numConta.length() / 2, numConta.length());
            } else {
                numConta1 = numConta2 = numConta;
            }

            numConta1 = JCryptOld.crypt("IB", numConta1);
            numConta2 = JCryptOld.crypt("IB", numConta2);

            // Conta salário alternativa
            final String numBancoAlt = JCryptOld.crypt("IB", servidor.getAttribute(Columns.RSE_BANCO_SAL_2) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_BANCO_SAL_2).toString()) : "");
            final String numAgenciaAlt = JCryptOld.crypt("IB", servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2).toString()) : "");
            final String numContaAlt = servidor.getAttribute(Columns.RSE_CONTA_SAL_2) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_CONTA_SAL_2).toString()) : "";

            String numContaAlt1 = "";
            String numContaAlt2 = "";

            if (numContaAlt.length() > 0) {
                numContaAlt1 = numContaAlt.substring(0, numContaAlt.length() / 2);
                numContaAlt2 = numContaAlt.substring(numContaAlt.length() / 2, numContaAlt.length());
            } else {
                numContaAlt1 = numContaAlt2 = numContaAlt;
            }

            numContaAlt1 = JCryptOld.crypt("IB", numContaAlt1);
            numContaAlt2 = JCryptOld.crypt("IB", numContaAlt2);

            final boolean rseTemInfBancaria = (!TextHelper.isNull(servidor.getAttribute(Columns.RSE_BANCO_SAL)) && !TextHelper.isNull(servidor.getAttribute(Columns.RSE_AGENCIA_SAL)) && !TextHelper.isNull(servidor.getAttribute(Columns.RSE_CONTA_SAL))) || (!TextHelper.isNull(servidor.getAttribute(Columns.RSE_BANCO_SAL_2)) && !TextHelper.isNull(servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2)) && !TextHelper.isNull(servidor.getAttribute(Columns.RSE_CONTA_SAL_2)));
            if (!rseTemInfBancaria) {
                model.addAttribute("rseNaoTemInfBancaria", Boolean.TRUE);
            }

            final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
            final boolean servicoTipoGAP = paramSvcCse.isTpsServicoTipoGap();

            // Se serviço do tipo GAP, redireciona para página com tratamento especifico
            if (servicoTipoGAP) {
                // Guarda na request parâmetros já pesquisados
                request.setAttribute("convenio", convenio);
                request.setAttribute("paramSvc", paramSvcCse);

                // Repassa o token salvo, pois o método irá revalidar o token
                request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                request.setAttribute("_skip_history_", Boolean.TRUE);

                // Redireciona para jsp específico de tratamento da margem do GAP
                return iniciarReservaGap(rseCodigo, request, response, session, model);
            }

            // Se tem simulação, seleciona lista de prazos para o serviço
            final Set<Integer> prazosPossiveisMensal = new TreeSet<>();
            Set<Integer> prazosPossiveisPeriodicidadeFolha = new TreeSet<>();

            final boolean temSimulacaoConsignacao = ParamSist.getBoolParamSist(CodedValues.TPC_SIMULACAO_CONSIGNACAO, responsavel);

            if (validaPrazoAvancado && (temSimulacaoConsignacao || paramSvcCse.isTpsValidarTaxaJuros())) {
                // Seleciona prazos ativos.
                try {
                    // pega os prazos referente ao numero de prestacoes
                    final int dia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                    final List<TransferObject> prazos = simulacaoController.getPrazoCoeficiente(svcCodigo, csaCodigo, orgCodigo, dia, validaBloqSerCnvCsaAvancado, true, false, responsavel);
                    // Durante implantação do sistema, se CSA não cadastrou prazo e usuário possui permissão de reservar margem
                    // o método acima retorna uma lista vazia.
                    // Verifica se a CSA cadastrou taxa somente se o serviço estiver configurado para Validar taxa de juros anunciada
                    if (((prazos == null) || prazos.isEmpty()) && paramSvcCse.isTpsValidarTaxaJuros()) {
                        // Se o serviço possui prazo, então a CSA ainda não cadastrou taxa.
                        final List<PrazoTransferObject> prazosServico = simulacaoController.findPrazoByServico(svcCodigo, responsavel);
                        if (!prazosServico.isEmpty()) {
                            throw new SimulacaoControllerException("mensagem.erro.prazo.com.taxa.inexistente", responsavel);
                        }
                    }
                    if ((prazos != null) && !prazos.isEmpty()) {
                        prazos.forEach(p -> prazosPossiveisMensal.add(Integer.valueOf(p.getAttribute(Columns.PRZ_VLR).toString())));
                        if (!PeriodoHelper.folhaMensal(responsavel)) {
                            prazosPossiveisPeriodicidadeFolha = PeriodoHelper.converterListaPrazoMensalEmPeriodicidade(prazos, responsavel);
                        }
                    }
                } catch (final SimulacaoControllerException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            //***********************************************/
            // Parâmetros de sistema
            // Verifica se permite a escolha de periodicidade da folha diferente da que está configurada no sistema
            final boolean permiteEscolherPeriodicidade = ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODICIDADE_FOLHA, CodedValues.TPC_SIM, responsavel);
            if (permiteEscolherPeriodicidade && !PeriodoHelper.folhaMensal(responsavel)) {
                model.addAttribute("exibirCampoPeriodicidade", Boolean.TRUE);
            }

            // Parametro que mostra a composição da margem do servidor na reserva.
            final boolean possuiComposicaoMargem = possuiComposicaoMargem(responsavel);

            // Parâmetro para exibição de variação de margem
            final boolean possuiVariacaoMargem = possuiVariacaoMargem(responsavel);

            // Parametro de sistema que exige ou não a senha para visualizar margem
            final boolean exigeSenha = parametroController.senhaServidorObrigatoriaConsultaMargem(rseCodigo, responsavel);

            // Verifica se a senha para consulta de margem foi digitada e validada corretamente
            final boolean senhaServidorOK = request.getAttribute("senhaServidorOK") != null;

            // Permite cadastro de indice.
            final boolean permiteCadIndice = ParamSist.paramEquals(CodedValues.TPC_PERMITE_CAD_INDICE, CodedValues.TPC_SIM, responsavel);

            // Parâmetro de SVC/CSA que determina se permite valor negativo de contrato
            final boolean permiteVlrNegativo = parametroController.permiteContratoValorNegativo(csaCodigo, svcCodigo, responsavel);

            // Índice cadastrado automaticamente
            final boolean indiceSomenteAutomatico = ParamSist.paramEquals(CodedValues.TPC_INDICE_SOMENTE_AUTOMATICO, CodedValues.TPC_SIM, responsavel);

            // Define se o indice eh numero ou nao (true numero) se !existe =null
            final boolean indiceNumerico = (ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_NUMERICO, responsavel) != null) && CodedValues.TPC_SIM.equals(ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_NUMERICO, responsavel).toString());

            // Limite numérico do indice
            final int limiteIndice = (ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel) != null) && !"".equals(ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel)) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel).toString()) : 99;
            final String mascaraIndice = (indiceNumerico ? "#D" : "#A") + String.valueOf(limiteIndice).length();

            // Parâmetro para controle de compulsórios
            final boolean temControleCompulsorios = temControleCompulsorios(responsavel);

            final String indicePadrao = ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_PADRAO, responsavel) != null ? ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_PADRAO, responsavel).toString() : null;

            // Parâmetro para exibição do histórico de liquidação/interrupções antecipadas
            final boolean exibeHistLiqAntecipadas = !responsavel.isSer() && ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_HISTORICO_LIQUIDACOES_ANTECIPADAS, responsavel);
            final int numAdeHistLiqAntecipadas = (paramSvcCse.getTpsNumAdeHistLiquidacoesAntecipadas() != null) && !"".equals(paramSvcCse.getTpsNumAdeHistLiquidacoesAntecipadas()) ? Integer.parseInt(paramSvcCse.getTpsNumAdeHistLiquidacoesAntecipadas()) : 0;

            // Paramêtro para margem limite por consignatária
            final Short codMargemLimitePorCsa = (ParamSist.getInstance().getParam(CodedValues.TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA, responsavel) != null) && !"".equals(ParamSist.getInstance().getParam(CodedValues.TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA, responsavel)) ? Short.parseShort(ParamSist.getInstance().getParam(CodedValues.TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA, responsavel).toString()) : 0;

            final String tpaModalidadeOperacao = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INF_MODALIDADE_OPERACAO_OBRIGATORIO, responsavel);
            final boolean exigeModalidadeOperacao = !TextHelper.isNull(tpaModalidadeOperacao) && "S".equals(tpaModalidadeOperacao);
            if (responsavel.isCsaCor() && exigeModalidadeOperacao) {
                model.addAttribute("exigeModalidadeOperacao", Boolean.TRUE);
            }

            final String tpaMatriculaSerCsa = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFORMAR_MATRICULA_NA_CSA_OBRIGATORIO, responsavel);
            final boolean exigeMatriculaSerCsa = !TextHelper.isNull(tpaMatriculaSerCsa) && "S".equals(tpaMatriculaSerCsa);
            if (responsavel.isCsaCor() && exigeMatriculaSerCsa) {
                model.addAttribute("exigeMatriculaSerCsa", Boolean.TRUE);
            }

            final String tpaVisualizaInformacaoCsaServidor = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_VISUALIZA_INFO_CSA_SERVIDOR_RESERVA_MARGEM, responsavel);
            final boolean exibeInformacaoCsaServidor = !TextHelper.isNull(tpaVisualizaInformacaoCsaServidor) && "S".equals(tpaVisualizaInformacaoCsaServidor);
            if (responsavel.isCsaCor() && exibeInformacaoCsaServidor) {
                model.addAttribute("exibeInformacaoCsaServidor", Boolean.TRUE);
            }

            //***********************************************/
            // Parâmetros de Serviço

            Short incMargem = paramSvcCse.getTpsIncideMargem(); // Incide na margem 1, 2 ou 3
            final Short intFolha = paramSvcCse.getTpsIntegraFolha(); // Integra folha sim ou não
            final String tipoVlr = paramSvcCse.getTpsTipoVlr(); // Tipo do valor: F (Fixo) / P (Percentual) / T (Total da Margem)
            boolean alteraAdeVlr = paramSvcCse.isTpsAlteraAdeVlr(); // Habilita ou nao campo de valor da reserva, campo ja vem preenchido
            if (alteraAdeVlr && parametrosPlano.containsKey(CodedValues.TPP_VLR_FIXO_PLANO)) {
                alteraAdeVlr = CodedValues.PLANO_VALOR_ALTERAVEL.equals(parametrosPlano.get(CodedValues.TPP_VLR_FIXO_PLANO)); // Habilita ou nao campo de valor da reserva dependendo da configuração do plano
            }
            String adeVlrPadrao = (paramSvcCse.getTpsAdeVlr() != null) && !"".equals(paramSvcCse.getTpsAdeVlr()) ? NumberHelper.reformat(paramSvcCse.getTpsAdeVlr(), "en", NumberHelper.getLang()) : "0"; // Valor da prestação fixo para o serviço
            if (paramSvcCse.isTpsAlteraAdeVlr() && parametrosPlano.containsKey(CodedValues.TPP_VLR_PLANO)) {
                final String valorPlano = parametrosPlano.get(CodedValues.TPP_VLR_PLANO);
                adeVlrPadrao = !TextHelper.isNull(valorPlano) ? NumberHelper.reformat(valorPlano, "en", NumberHelper.getLang()) : adeVlrPadrao; // Valor da prestação fixo para o plano
            }
            final String vlrLimite = (paramSvcCse.getTpsVlrLimiteAdeSemMargem() != null) && !"".equals(paramSvcCse.getTpsVlrLimiteAdeSemMargem()) ? paramSvcCse.getTpsVlrLimiteAdeSemMargem() : "0";
            final int carenciaMinCse = (paramSvcCse.getTpsCarenciaMinima() != null) && !"".equals(paramSvcCse.getTpsCarenciaMinima()) ? Integer.parseInt(paramSvcCse.getTpsCarenciaMinima()) : 0;
            final int carenciaMaxCse = (paramSvcCse.getTpsCarenciaMaxima() != null) && !"".equals(paramSvcCse.getTpsCarenciaMaxima()) ? Integer.parseInt(paramSvcCse.getTpsCarenciaMaxima()) : 99;
            boolean permiteCadVlrTac = paramSvcCse.isTpsCadValorTac(); // VALOR TAC
            boolean permiteCadVlrIof = paramSvcCse.isTpsCadValorIof(); // VALOR IOF
            boolean permiteCadVlrLiqLib = paramSvcCse.isTpsCadValorLiquidoLiberado(); // VALOR LIQUIDO LIBERADO
            boolean permiteCadVlrMensVinc = paramSvcCse.isTpsCadValorMensalidadeVinc(); // VALOR MENSALIDADE VINCULADA
            boolean permiteCadVlrLiqTxJuros = paramSvcCse.isTpsVlrLiqTaxaJuros();
            boolean prazoFixo = paramSvcCse.isTpsPrazoFixo();
            if (!prazoFixo && parametrosPlano.containsKey(CodedValues.TPP_PRAZO_FIXO_PLANO)) {
                prazoFixo = CodedValues.PLANO_PRAZO_FIXO_SIM.equals(parametrosPlano.get(CodedValues.TPP_PRAZO_FIXO_PLANO));
            }
            if (prazoFixo) {
                prazosPossiveisMensal.clear();
            }
            String maxPrazo = validaPrazoAvancado && (paramSvcCse.getTpsMaxPrazo() != null) && !"".equals(paramSvcCse.getTpsMaxPrazo()) ? paramSvcCse.getTpsMaxPrazo() : "-1";
            if ("-1".equals(maxPrazo) || (!paramSvcCse.isTpsPrazoFixo() && prazoFixo)) {
                final String maxPrazoPlano = parametrosPlano.get(CodedValues.TPP_PRAZO_MAX_PLANO);
                maxPrazo = validaPrazoAvancado && !TextHelper.isNull(maxPrazoPlano) ? maxPrazoPlano : "-1";
            }
            boolean permiteCadVlrSegPrestamista = paramSvcCse.isTpsExigeSeguroPrestamista();
            final boolean servicoCompulsorio = temControleCompulsorios && paramSvcCse.isTpsServicoCompulsorio();
            final boolean serInfBancariaObrigatoria = parametroController.verificaAutorizacaoReservaSemSenha(rseCodigo, svcCodigo, paramSvcCse.isTpsInfBancariaObrigatoria(), null, responsavel);
            final boolean validarInfBancaria = validaDadosBancariosAvancado && paramSvcCse.isTpsValidarInfBancariaNaReserva();
            boolean validarDataNasc = paramSvcCse.isTpsValidarDataNascimentoNaReserva();
            final boolean possuiCorrecaoVlrPresente = paramSvcCse.isTpsPossuiCorrecaoValorPresente();
            final double maxTacCse = (paramSvcCse.getTpsValorMaxTac() != null) && !"".equals(paramSvcCse.getTpsValorMaxTac()) ? Double.parseDouble(paramSvcCse.getTpsValorMaxTac()) : Double.MAX_VALUE;
            final String mascaraAdeIdentificador = paramSvcCse.getTpsMascaraIdentificadorAde();

            //Busca parâmetro de Controle de Valor máximo de desconto
            final boolean controlaSaldoDevedor = paramSvcCse.isTpsControlaSaldo();
            final boolean possuiControleVlrMaxDesconto = controlaSaldoDevedor && paramSvcCse.isTpsControlaVlrMaxDesconto();
            BigDecimal vlrMaxParcelaSaldoDevedor = null;
            if (possuiControleVlrMaxDesconto) {
                vlrMaxParcelaSaldoDevedor = autorizacaoController.calcularValorDescontoParcela(rseCodigo, svcCodigo, null);
            }

            // se usuário gestor e com permissão de inclusão avançada, prevalesce a opção avançada
            if (responsavel.isCseSup() && usuPossuiIncAvancadaAde) {
                validarDataNasc &= validaDataNascAvancado;
            }

            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_DESCONTO_EM_FILA, CodedValues.TPC_SIM, responsavel) && (!TextHelper.isNull(paramSvcCse.getTpsBaseCalcDescontoEmFila()) && !TextHelper.isNull(paramSvcCse.getTpsPercentualBaseCalcDescontoEmFila()))) {
                // Se o sistema permite módulo de desconto em fila e o serviço está configurado para realizar a fila
                // define a não incidência de margem e o prazo fixo igual a 1
                incMargem = CodedValues.INCIDE_MARGEM_NAO;
                prazoFixo = true;
                maxPrazo = "1";
            }

            // Parâmetro com nome da classe java, que implementa a interface ProcessaReservaMargem
            final String classeProcReserva = paramSvcCse.getTpsClasseJavaProcEspecificoReserva();

            final boolean servidorDeveSerKYCComplaint = paramSvcCse.isTpsServidorDeveSerKYCComplaint();
            // Se for o servidor fazendo solicitação, e ele não for KYC Compliante deve ser redirecionado para a tela de validação de KYC
            if (servidorDeveSerKYCComplaint) {
                final KYCHelper kycHelper = new KYCHelper(servidor.getAttribute(Columns.SER_CODIGO).toString(), responsavel);
                final boolean servidorValidouKYC = kycHelper.validou();
                model.addAttribute("servidorValidouKYC", servidorValidouKYC);
                if (!servidorValidouKYC && responsavel.isSer()) {
                    final String panNumber = kycHelper.getPanNumber();
                    if (TextHelper.isNull(panNumber)) {
                        return viewRedirect("jsp/reservarMargem/iniciarKYC", request, session, model, responsavel);
                    } else {
                        model.addAttribute("servidorPanNumber", panNumber);
                        String status = kycHelper.checkKYC(kycHelper.getStatus());
                        if (KYCHelper.CHECK_KYC_STATUS_PENDING.equals(status)) {
                            model.addAttribute("linkExternoKYC", ParamSist.getInstance().getParam(CodedValues.TPC_KYC_URL_JORNADA_VALIDACAO, responsavel));
                            return viewRedirect("jsp/reservarMargem/finalizarKYC", request, session, model, responsavel);
                        } else if (KYCHelper.CHECK_KYC_STATUS_VALID.equals(status)) {
                            // KYC é válido, verificar se precisa salvar o novo dado e então pode continuar para a reserva
                            kycHelper.validar();
                        } else {
                            // Faz uma segunta tentativa conforme solicitado pelo cliente.
                            status = kycHelper.checkKYC(kycHelper.getStatus());
                            if (KYCHelper.CHECK_KYC_STATUS_PENDING.equals(status)) {
                                model.addAttribute("linkExternoKYC", ParamSist.getInstance().getParam(CodedValues.TPC_KYC_URL_JORNADA_VALIDACAO, responsavel));
                                return viewRedirect("jsp/reservarMargem/finalizarKYC", request, session, model, responsavel);
                            } else if (KYCHelper.CHECK_KYC_STATUS_VALID.equals(status)) {
                                // KYC é válido, verificar se precisa salvar o novo dado e então pode continuar para a reserva
                                kycHelper.validar();
                            } else {
                                // Segunda tentativa deu erro. Mostra mensagem de erro para o usuário/servidor.
                                kycHelper.enviarNotificacaoErroKYC(csaCodigo, panNumber, status);
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.kyc.falha.na.requisicao", responsavel, status));
                                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                            }
                        }
                    }
                }
            }

            //***********************************************/
            // Se é o servidor que está solicitando, deixa os parâmetros
            // listados abaixo com os valores default
            if (responsavel.isSer()) {
                permiteCadVlrTac = false;
                permiteCadVlrIof = false;
                permiteCadVlrLiqLib = false;
                permiteCadVlrMensVinc = false;
                permiteCadVlrLiqTxJuros = false;
                permiteCadVlrSegPrestamista = false;
                validarDataNasc = false;
            }

            //***********************************************/
            // Parâmetros de Convênio

            final String svcPrioridade = convenio.getAttribute(Columns.SVC_PRIORIDADE) != null ? convenio.getAttribute(Columns.SVC_PRIORIDADE).toString() : "";
            final String svcIdentificador = convenio.getAttribute(Columns.SVC_IDENTIFICADOR) != null ? convenio.getAttribute(Columns.SVC_IDENTIFICADOR).toString() : "";
            final String svcDescricao = convenio.getAttribute(Columns.SVC_DESCRICAO) != null ? convenio.getAttribute(Columns.SVC_DESCRICAO).toString() : "";
            final String cnvCodVerba = convenio.getAttribute(Columns.CNV_COD_VERBA) != null ? convenio.getAttribute(Columns.CNV_COD_VERBA).toString() : "";
            final String cnvDescricao = (cnvCodVerba.length() > 0 ? cnvCodVerba : svcIdentificador) + " - " + svcDescricao;
            model.addAttribute("cnvDescricao", cnvDescricao);

            // Prazo minimo de carencia para comecar a contar.
            final int carenciaMinima = (convenio.getAttribute("CARENCIA_MINIMA") != null) && !"".equals(convenio.getAttribute("CARENCIA_MINIMA")) ? Integer.parseInt(convenio.getAttribute("CARENCIA_MINIMA").toString()) : 0;

            // Prazo maximo de carencia que o usuario pode informar.
            final int carenciaMaxima = (convenio.getAttribute("CARENCIA_MAXIMA") != null) && !"".equals(convenio.getAttribute("CARENCIA_MAXIMA")) ? Integer.parseInt(convenio.getAttribute("CARENCIA_MAXIMA").toString()) : 99;

            // Permite fazer contrato com prazo maior que o contrato do servidor com o órgão
            final String permitePrazoMaiorContSer = (convenio.getAttribute("PERMITE_PRAZO_MAIOR_RSE_PRAZO") != null) && "S".equals(convenio.getAttribute("PERMITE_PRAZO_MAIOR_RSE_PRAZO")) ? "true" : "false";

            // Cadastro de indices
            String indPadCsa = (convenio.getAttribute("VLR_INDICE") != null) && !"".equals(convenio.getAttribute("VLR_INDICE")) ? convenio.getAttribute("VLR_INDICE").toString() : "";

            // Se houver índice padrão cadastrado para o plano, utiliza o esse padrão ao invés do padrão do convênio
            if (parametrosPlano.containsKey(CodedValues.TPP_INDICE_PLANO)) {
                indPadCsa = !TextHelper.isNull(parametrosPlano.get(CodedValues.TPP_INDICE_PLANO)) ? parametrosPlano.get(CodedValues.TPP_INDICE_PLANO).toString() : indPadCsa;
            }

            // Define os valores de carência mínimo e máximo
            final int[] carenciaPermitida = ReservaMargemHelper.getCarenciaPermitida(carenciaMinima, carenciaMaxima, carenciaMinCse, carenciaMaxCse);
            final int carenciaMinPermitida = carenciaPermitida[0];
            final int carenciaMaxPermitida = carenciaPermitida[1];

            // Parâmetro de identificador ADE obrigatório
            final boolean identificadorAdeObrigatorio = !TextHelper.isNull(convenio.getAttribute("IDENTIFICADOR_ADE_OBRIGATORIO")) ? "S".equals(convenio.getAttribute("IDENTIFICADOR_ADE_OBRIGATORIO")) : paramSvcCse.isTpsIdentificadorAdeObrigatorio();

            //***********************************************/
            // Verifica se pode mostrar margem
            boolean exigeCaptcha = false;
            MargemDisponivel margemDisponivel = null;
            try {
                if (responsavel.isSer()) {
                        boolean exibeCaptcha = false;
                        boolean exibeCaptchaAvancado = false;
                        boolean exibeCaptchaDeficiente = false;
                        final String validaRecaptcha = "S".equals(JspHelper.verificaVarQryStr(request, "validaCaptchaReservar")) && !"S".equals(JspHelper.verificaVarQryStr(request, "validaCaptchaTopo")) ? JspHelper.verificaVarQryStr(request, "validaCaptchaReservar") : "N";
                        final boolean podeConsultar = ControleConsulta.getInstance().podeConsultarMargemSemCaptchaSer(responsavel.getUsuCodigo());

                        final boolean defVisual = responsavel.isDeficienteVisual();
                        if (!defVisual) {
                            exibeCaptchaAvancado = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                            exibeCaptcha = !exibeCaptchaAvancado;
                        } else {
                            exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                        }
                        if (!podeConsultar && "S".equals(validaRecaptcha)) {
                            if (!defVisual) {
                                if (exibeCaptcha) {
                                    if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                                            && !ImageCaptchaServlet.validaCaptcha(session.getId(), JspHelper.verificaVarQryStr(request, "codigoCapReservar"))) {
                                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                        exigeCaptcha = true;
                                    } else {
                                        session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
                                        ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                                    }
                                } else if (exibeCaptchaAvancado) {
                                    final String remoteAddr = request.getRemoteAddr();

                                    if (!isValidCaptcha(request.getParameter("g-recaptcha-response_reservar"), remoteAddr, responsavel)) {
                                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                        exigeCaptcha = true;
                                    } else {
                                        ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                                    }
                                }
                            } else {
                                final boolean exigeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                                if (exigeCaptchaDeficiente) {
                                    final String captchaAnswer = JspHelper.verificaVarQryStr(request, "codigoCapReservar");

                                    if (captchaAnswer == null) {
                                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                        exigeCaptcha = true;
                                    }

                                    final String captchaCode = (String) session.getAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                                    if ((captchaCode == null) || !captchaCode.equalsIgnoreCase(captchaAnswer)) {
                                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                        exigeCaptcha = true;
                                    } else {
                                        session.removeAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                                        ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                                    }
                                }
                            }
                        } else if (podeConsultar) {
                            ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                        } else {
                            exigeCaptcha = true;
                        }
                        margemDisponivel = new MargemDisponivel(rseCodigo, csaCodigo, svcCodigo, incMargem, responsavel);
                        model.addAttribute("exigeCaptcha", exigeCaptcha);
                        model.addAttribute("exibeCaptcha", exibeCaptcha);
                        model.addAttribute("exibeCaptchaAvancado", exibeCaptchaAvancado);
                        model.addAttribute("exibeCaptchaDeficiente", exibeCaptchaDeficiente);
                } else {
                    margemDisponivel = new MargemDisponivel(rseCodigo, csaCodigo, svcCodigo, incMargem, responsavel);
                }
            } catch (final ViewHelperException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            final boolean podeMostrarMargem = margemDisponivel.getExibeMargem().isExibeValor();

            // Verifica se pode mostrar margem limite por csa
            MargemTO margemLimiteDisponivel = null;
            boolean podeMostrarMargemLimite = false;
            ExibeMargem exibeMargemLimite = null;
            BigDecimal margemLimiteConsignavel = new BigDecimal("0.00");
            if ((codMargemLimitePorCsa != null) && !codMargemLimitePorCsa.equals(CodedValues.INCIDE_MARGEM_NAO)) {
                margemLimiteDisponivel = consultarMargemController.consultarMargemLimitePorCsa(rseCodigo, csaCodigo, codMargemLimitePorCsa, null, responsavel);
                if (margemLimiteDisponivel != null) {
                    exibeMargemLimite = new ExibeMargem(margemLimiteDisponivel, responsavel);
                    podeMostrarMargemLimite = exibeMargemLimite.isExibeValor();
                    margemLimiteConsignavel = margemLimiteDisponivel.getMrsMargemRest();
                }
            }

            // Mostra a Margem
            if (!exigeCaptcha && podeMostrarMargem && (!exigeSenha || (exigeSenha && senhaServidorOK))) {
                model.addAttribute("exibirValorMargem", Boolean.TRUE);
            }

            // Mostra a Margem Limite
            if (!exigeCaptcha && podeMostrarMargemLimite && (!exigeSenha || (exigeSenha && senhaServidorOK))) {
                model.addAttribute("exibirValorMargemLimite", Boolean.TRUE);
            }

            final List<MargemTO> margensIncidentes = parametroController.lstMargensIncidentes(null, csaCodigo, orgCodigo, null, null, responsavel);
            boolean exibeAlgumaMargem = false;
            for (final MargemTO margem : margensIncidentes) {
                exibeAlgumaMargem |= new ExibeMargem(margem, responsavel).isExibeValor();
            }

            BigDecimal margemConsignavel = margemDisponivel.getMargemRestante();

            // Calcula o valor dos contratos de serviço com tratamento especial de margem para exibição de mensagem para servidor.
            // Para que um contrato tenha tratamento especial de margem, não deve incidir sobre nenhuma margem e seu serviço deve ter TPS_CODIGO=224 habilitado.
            final BigDecimal somaValorContratosTratamentoEspecial = responsavel.isCsaCor() ? consultarMargemController.somarContratosTratamentoEspecialMargem(rseCodigo, responsavel) : new BigDecimal("0");
            final BigDecimal margemTratamentoEspecial = margemConsignavel.subtract(somaValorContratosTratamentoEspecial);

            // Se for o servidor fazendo solicitação, verifica se ele possui margem
            // disponível para o serviço selecionado
            // Se a margem restante não é positiva
            if (responsavel.isSer() && (margemConsignavel.signum() != 1)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.margem.insuficiente.svc", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            //DESENV-17017: Verifica a obrigatoriedade de informações do servidor.
            final List<String> tpsCodigos = new ArrayList<>();
            tpsCodigos.add(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO);
            tpsCodigos.add(CodedValues.TPS_VALOR_SVC_FIXO_POSTO);

            final List<TransferObject> paramSvcCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, false, responsavel);
            boolean enderecoObrigatorio = false;
            boolean celularObrigatorio = false;
            boolean enderecoCelularObrigatorio = false;
            for (final TransferObject param2 : paramSvcCsa) {
                final CustomTransferObject param = (CustomTransferObject) param2;
                if ((param != null) && (param.getAttribute(Columns.PSC_VLR) != null)) {
                    if (CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO.equals(param.getAttribute(Columns.TPS_CODIGO))){
                        final String pscVlr = !param.getAttribute(Columns.PSC_VLR).toString().isEmpty() ? param.getAttribute(Columns.PSC_VLR).toString() : "";
                        // DESENV-17017 - Por decisão do setor de segurança (LGPD) essa implementação não deve ser liberada, por isso os valores são setados como false
                        if("E".equals(pscVlr)) {
                            enderecoObrigatorio = true;
                            model.addAttribute("enderecoObrigatorio", enderecoObrigatorio);
                        } else if ("C".equals(pscVlr)) {
                            celularObrigatorio = true;
                            model.addAttribute("celularObrigatorio", celularObrigatorio);
                        } else if ("EC".equals(pscVlr)) {
                            enderecoCelularObrigatorio = true;
                            model.addAttribute("enderecoCelularObrigatorio", enderecoCelularObrigatorio);
                        }
                    }

                    if (CodedValues.TPS_VALOR_SVC_FIXO_POSTO.equals(param.getAttribute(Columns.TPS_CODIGO))) {
                        final String pscVlr = !param.getAttribute(Columns.PSC_VLR).toString().isEmpty() ? param.getAttribute(Columns.PSC_VLR).toString() : "";
                        if(CodedValues.TPC_SIM.equals(pscVlr)) {
                            final TransferObject valorSvcByPostoAndCsa = postoRegistroServidorController.findValorFixoByCsaSvcPos(svcCodigo, csaCodigo, servidor.getAttribute(Columns.POS_CODIGO).toString(), responsavel);
                            if (!alteraAdeVlr && !TextHelper.isNull(valorSvcByPostoAndCsa)) {
                                adeVlrPadrao = valorSvcByPostoAndCsa.getAttribute(Columns.PSP_PPO_VALOR).toString();
                                model.addAttribute("disabledVlrFixoPosto", true);
                            } else if (!alteraAdeVlr && TextHelper.isNull(valorSvcByPostoAndCsa)) {
                                LOG.error("Valor Fixo do serviço pelo posto não cadastrado");
                                throw new AutorizacaoControllerException("mensagem.erro.inserir.vlr.posto.fixo", responsavel, servidor.getAttribute(Columns.POS_DESCRICAO).toString());
                            }
                        }
                    }
                }
            }



            //Caso o parâmento de serviço 277 esteja como S é pulada a etapa de informação de valor e é criada uma consignação com valor 1,00
            //e prazo indeterminado
            if (responsavel.isSer()) {
                final CustomTransferObject naturezaSvc = servicoController.findNaturezaServico(svcCodigo, responsavel);

                final boolean params = paramSvcCse.isTpsPulaInformacaoValorPrazoFluxoReserva() && (naturezaSvc != null) && !TextHelper.isNull(naturezaSvc.getAttribute(Columns.NSE_CODIGO)) && !CodedValues.NSE_EMPRESTIMO.equals(naturezaSvc.getAttribute(Columns.NSE_CODIGO).toString());

                final String adePeriodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);

                String linkRet = "../v3/reservarMargem$acao(selecionarCsa|SVC_CODIGO(" + svcCodigo + "|SVC_DESCRICAO(" + svcDescricao;

                if (params) {

                    String link = "&RSE_CODIGO=" + rseCodigo + "&adeVlr=1,00&CSA_CODIGO=" + csaCodigo + "&CNV_CODIGO=" + convenio.getAttribute(Columns.CNV_CODIGO).toString() + "&adePeriodicidade=" + adePeriodicidade;

                    if(portalBeneficio && !TextHelper.isNull(corCodigo)) {
                        final ParamSession paramSession = ParamSession.getParamSession(session);
                        final String linkRetorno = TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));
                        linkRet = linkRetorno.replace('?', '$').replace('=', '(').replace("x26", "|");
                        link += "&linkRet=" + linkRet + "&PORTAL_BENEFICIO=true&COR_CODIGO="+corCodigo;
                    } else {
                        link += "&linkRet=" + linkRet;
                    }
                    link +="&SVC_CODIGO=" + svcCodigo + "&tps_277=true";

                    if(enderecoObrigatorio) {
                        link += "&enderecoObrigatorio=" + enderecoObrigatorio;
                    } else if (celularObrigatorio) {
                        link += "&celularObrigatorio=" + celularObrigatorio;
                    } else if (enderecoCelularObrigatorio) {
                        link += "&enderecoCelularObrigatorio=" + enderecoCelularObrigatorio;
                    }

                    if (ParamSist.paramEquals(CodedValues.TPC_TEM_TERMO_ADESAO, CodedValues.TPC_SIM, responsavel) && paramSvcCse.isTpsExigeAceiteTermoAdesao() && !paramSvcCse.isTpsExigeAceiteTermoAdesaoAntesValores()) {
                        link = "../v3/reservarMargem?acao=aceitarTermoAdesao" + link;
                        link += "&proximaOperacao=aceitarTermoAdesao";
                    } else {
                        link = "../v3/reservarMargem?acao=autorizarReserva" + link;
                    }

                    link = TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request));

                    request.setAttribute("url64", link);
                    return "jsp/redirecionador/redirecionar";
                }
            }

            // Se tipo valor igual a margem total, coloca no campo de adeVlr o
            // valor da margem disponível para o serviço
            if (CodedValues.TIPO_VLR_TOTAL_MARGEM.equals(tipoVlr)) {
                adeVlrPadrao = NumberHelper.format(margemConsignavel.doubleValue(), NumberHelper.getLang());
            }

            // Monta a lista de selecao de correspondentes
            if (responsavel.isCseSupOrg() || responsavel.isCsa()) {
                final CorrespondenteTransferObject cor = new CorrespondenteTransferObject();
                cor.setCsaCodigo(csaCodigo);
                if (validaBloqSerCnvCsaAvancado) {
                    cor.setCorAtivo(CodedValues.STS_ATIVO);
                }
                final List<TransferObject> correspondentes = consignatariaController.lstCorrespondentes(cor, responsavel);
                if ((correspondentes != null) && (correspondentes.size() > 0)) {
                    model.addAttribute("lstCorrespondentes", correspondentes);
                }
            }

            // Se o serviço é compulsório, busca o valor dos contratos que podem
            // dar lugar para inclusão deste contrato, e adiciona este valor à margem
            if (servicoCompulsorio) {
                try {
                    final boolean controlaMargem = !ParamSist.paramEquals(CodedValues.TPC_ZERA_MARGEM_USADA, CodedValues.TPC_SIM, responsavel);
                    final BigDecimal margemDisponivelCompulsorio = consultarMargemController.getMargemDisponivelCompulsorio(rseCodigo, svcCodigo, svcPrioridade, incMargem, controlaMargem, null, responsavel);
                    if (margemDisponivelCompulsorio != null) {
                        margemConsignavel = margemDisponivelCompulsorio;
                    }
                } catch (final ServidorControllerException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    LOG.error(ex.getMessage(), ex);
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            // Se o serviço possui processamento específico de reserva, cria a classe de execução
            if (classeProcReserva != null) {
                try {
                    final ProcessaReservaMargem processador = ProcessaReservaMargemFactory.getProcessador(classeProcReserva);
                    model.addAttribute("processaReservaMargem", processador.incluirPasso1(request));
                } catch (final ViewHelperException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }
            }

            if (responsavel.isSer() && !TextHelper.isNull(paramSvcCse.getTpsMsgExibirSolicitacaoServidor())) {
                // Se é o servidor que está solicitando, então exibe a mensagem do parâmetro
                JspHelper.addMsgSession(session, CodedValues.MSG_ALERT, paramSvcCse.getTpsMsgExibirSolicitacaoServidor());
            } else if (responsavel.isCsaCor() && !TextHelper.isNull(paramSvcCse.getTpsMsgExibirInclusaoAlteracaoAdeCsa())) {
                // Se é consignatária ou correspondente que está reservando margem, então exibe a mensagem do parâmetro
                JspHelper.addMsgSession(session, CodedValues.MSG_ALERT, paramSvcCse.getTpsMsgExibirInclusaoAlteracaoAdeCsa());
            }

            // Se é usuário de CSA/COR e o parâmetro de exibição de mensagem estiver habilitado, verifica se o servidor já possui contrato ativo
            // para a mesma verba da nova inclusão
            if (responsavel.isCsaCor() && paramSvcCse.isTpsExibeMsgReservaMesmaVerbaCsaCor()) {
                // verifica se o usuário já possui contrato ativo para o convênio informado
                final CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute("TIPO_OPERACAO", "consultar");
                criterio.setAttribute(Columns.SVC_CODIGO, svcCodigo);
                criterio.setAttribute(Columns.CNV_COD_VERBA, cnvCodVerba);
                final int count = pesquisarConsignacaoController.countPesquisaAutorizacao(AcessoSistema.ENTIDADE_CSA, csaCodigo, rseCodigo, null, null, CodedValues.SAD_CODIGOS_ATIVOS, null, criterio, responsavel);
                if (count > 0) {
                    JspHelper.addMsgSession(session, CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.alerta.reservar.margem.contrato.ativo.mesmo.servidor.csa.verba", responsavel));
                }
            }

            // Se exige aceite do termo de adesão, e se o aceite ainda não foi dado, redireciona para a página com o termo.
            if (ParamSist.paramEquals(CodedValues.TPC_TEM_TERMO_ADESAO, CodedValues.TPC_SIM, responsavel) && paramSvcCse.isTpsExigeAceiteTermoAdesao() && !paramSvcCse.isTpsExigeAceiteTermoAdesaoAntesValores()) {
                model.addAttribute("proximaOperacao", "aceitarTermoAdesao");
            }

            if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_ANEXO_INCLUSAO_CONTRATOS, CodedValues.TPC_SIM, responsavel) && parametroController.isObrigatorioAnexoInclusao(svcCodigo, responsavel) && !responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.permissao.anexo.reserva", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String[] extensoes = UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO;
            if (TextHelper.isNull(extensoes)) {
                extensoes = "txt,zip".split(",");
            } else {
                extensoes = TextHelper.join(extensoes, ",").replaceAll("[.]", "").split(",");
            }

            final Set<Date> periodos = periodoController.listarPeriodosPermitidos(orgCodigo, null, responsavel);
            if ((periodos != null) && !periodos.isEmpty()) {
                model.addAttribute("lstPeriodos", periodos);
            }

            final List<TransferObject> tdaList = autorizacaoController.lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.WEB, svcCodigo, csaCodigo, responsavel);
            if ((tdaList != null) && !tdaList.isEmpty()) {
                model.addAttribute("lstTipoDadoAdicional", tdaList);

                final Map<String, String> dadosAutorizacao = new HashMap<>();
                for (final TransferObject tda : tdaList) {
                    final String tdaCodigo = (String) tda.getAttribute(Columns.TDA_CODIGO);
                    final String tdaValor = autorizacaoController.getValorDadoServidor(serCodigo, tdaCodigo, responsavel);
                    dadosAutorizacao.put(tdaCodigo, tdaValor);
                }
                model.addAttribute("dadosAutorizacao", dadosAutorizacao);
            }

            if (!responsavel.isSer() && (permiteCadIndice && !indiceSomenteAutomatico)) {
                model.addAttribute("exibirCampoIndice", Boolean.TRUE);

                boolean geraCombo = false;
                boolean vlrIndiceDisabled = false;
                String vlrIndice = null;

                final CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.IND_SVC_CODIGO, svcCodigo);
                criterio.setAttribute(Columns.IND_CSA_CODIGO, csaCodigo);

                // Verifica a existencia de registros de indice ja cadastrados pela csa ou cse
                final List<TransferObject> indices = indiceController.selectIndices(-1, -1, criterio, responsavel);

                if (indices.isEmpty()) {
                    // Se não existir nenhum indice cadastrado, então utilizar parâmetro de convenio 41 OU
                    // parâmetro de sistema 79 (que é sobreposto pelo 41)
                    if (!TextHelper.isNull(indPadCsa)) {
                        vlrIndice = indPadCsa;
                        // Se existir um valor padrão para o parâmetro então o campo estará desabilitado
                        vlrIndiceDisabled = true;
                    } else {
                        vlrIndice = indicePadrao;
                    }
                } else // Se existir um registro apenas, exibir este registro no campo de indice
                if (indices.size() == 1) {
                    final TransferObject c0 = indices.get(0);
                    vlrIndice = c0.getAttribute(Columns.IND_CODIGO).toString();
                    vlrIndiceDisabled = true;
                } else {
                    // Se existir mais de um, exibir um combo de seleção com as possibilidades existentes
                    geraCombo = true;
                }

                if (!geraCombo) {
                    model.addAttribute("vlrIndice", vlrIndice);
                    model.addAttribute("vlrIndiceDisabled", vlrIndiceDisabled);
                    model.addAttribute("mascaraIndice", mascaraIndice);
                } else {
                    model.addAttribute("lstIndices", indices);
                }
            }

            if ((responsavel.temPermissao(CodedValues.FUN_INCLUSAO_AVANCADA_CONSIGNACAO) && CodedValues.FUN_RES_MARGEM.equals(responsavel.getFunCodigo())) || inclusaoJudicial) {
                model.addAttribute("tmoDescricao", ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel));
                if (!TextHelper.isNull(request.getParameter("tmoCodigo"))) {
                    final TipoMotivoOperacaoTransferObject motivo = tipoMotivoOperacaoController.findMotivoOperacao(request.getParameter("tmoCodigo"), responsavel);
                    if (motivo != null) {
                        model.addAttribute("tmoCodigo", motivo.getTmoCodigo());
                        model.addAttribute("tmoDescricao", motivo.getTmoDescricao());
                    }
                }

                model.addAttribute("lstTipoJustica", sistemaController.lstTipoJustica(responsavel));
                if (inclusaoJudicial) {
                    model.addAttribute("inclusaoJudicial", inclusaoJudicial);
                    model.addAttribute("lstMtvOperacao", tipoMotivoOperacaoController.lstMotivoOperacaoConsignacao(CodedValues.STS_ATIVO, responsavel));
                }
            }

            final boolean exibeMaisAcoes = (podeMostrarMargem && possuiComposicaoMargem && !responsavel.isSer()) || (possuiVariacaoMargem && exibeAlgumaMargem && !responsavel.isSer()) || (exibeHistLiqAntecipadas && (numAdeHistLiqAntecipadas > 0) && !responsavel.isSer()) || (responsavel.isCsaCor() && exibeInformacaoCsaServidor);

            // Define se deve validar a margem via javascript, caso seja exibida
            boolean validaMargemViaJavascript = podeMostrarMargem && (!exigeSenha || (exigeSenha && senhaServidorOK));
            if (validaMargemViaJavascript && ParamSist.paramEquals(CodedValues.TPC_MARGEM_ORIGINAL_EXCEDE_ATE_MARGEM_LATERAL, CodedValues.TPC_SIM, responsavel) && ((incMargem.equals(CodedValues.INCIDE_MARGEM_SIM) && ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_LATERAL, CodedValues.TPC_SIM, responsavel)) || CasamentoMargem.getInstance().temCasamentoDoTipo(CasamentoMargem.LATERAL))) {
                validaMargemViaJavascript = false;
            }

            // DESENV-14337: Necessário exibir o termo de consentimento também para serviços que não fazem parte de simulação.
            if (responsavel.isSer() && ParamSist.paramEquals(CodedValues.TPC_EXIGE_CONFIRMACAO_TERMO_CONSENTIMENTO_DADOS_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                model.addAttribute("termoConsentimentoDadosServidor", montarTermoConsentimentoDadosServidor(responsavel));
            }

            // Quando o parâmetro de sistema 578 e 870 estão habilitados todos os serviços que são de natureza 4 e 9 (SAÚDE e ODONTO)
            // serão incluídos com como contratos de saúde sem as regras do módulo de benefício sáude é obrigatório existir beneficiário ativo para vinculação de plano e forma de pagamento.
            if (parametroController.isReservaSaudeSemModulo(svcCodigo, responsavel)) {
                final CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
                final List<TransferObject> beneficiarios = beneficiarioController.listarBeneficiarios(criterio, responsavel);
                if((beneficiarios == null) || beneficiarios.isEmpty()) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.beneficiario.inexistente.reserva.saude.sem.modulo", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
                model.addAttribute("beneficiarios", beneficiarios);

                if(!TextHelper.isNull(paramSvcCse.getTpsPermiteDescontoViaBoleto()) &&
                    (CodedValues.PAGAMENTO_VIA_BOLETO_OPICIONAL.equals(paramSvcCse.getTpsPermiteDescontoViaBoleto()) || CodedValues.PAGAMENTO_VIA_BOLETO_OBRIGATORIO.equals(paramSvcCse.getTpsPermiteDescontoViaBoleto()))) {
                    model.addAttribute("descontoViaBoleto", paramSvcCse.getTpsPermiteDescontoViaBoleto());
                }
            }

            String txtExplicativo = "";
            if (responsavel.isSer()){
                 txtExplicativo = TextHelper.isNull(paramSvcCse.getTpsExibeTxtExplicativoValorPrestacao()) ? "" : paramSvcCse.getTpsExibeTxtExplicativoValorPrestacao();
            }

            final String nomeArqFotoServidor = JspHelper.getPhoto(servidor.getAttribute(Columns.SER_CPF).toString(), rseCodigo, responsavel);
            if (!TextHelper.isNull(nomeArqFotoServidor)) {
                model.addAttribute("nomeArqFotoServidor", nomeArqFotoServidor);
            }
            
            //Carrega informação csa servidor
            if (responsavel.isCsaCor() && exibeInformacaoCsaServidor) {
                List<TransferObject> informacaoServidor = null;
                final CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.ICS_CSA_CODIGO, csaCodigo);
                criterio.setAttribute(Columns.ICS_SER_CODIGO, serCodigo);
                
                informacaoServidor = consignatariaController.lstInformacaoCsaServidor(criterio, -1, -1, responsavel);
                final ServidorTransferObject servidorInformacaoCsa = servidorController.findServidor(serCodigo, responsavel);

                model.addAttribute("informacaoServidor", informacaoServidor);
                model.addAttribute("nomeServidorInformacaoCsa", servidorInformacaoCsa.getSerNome());
            }

            // Adiciona ao modelo acessível ao JSP as configurações necessárias
            model.addAttribute("paramSvcCse", paramSvcCse);
            model.addAttribute("prazosPossiveisMensal", prazosPossiveisMensal);
            model.addAttribute("prazosPossiveisPeriodicidadeFolha", prazosPossiveisPeriodicidadeFolha);
            model.addAttribute("exibeMargemLimite", exibeMargemLimite);
            model.addAttribute("margemDisponivel", margemDisponivel);
            model.addAttribute("margemLimiteDisponivel", margemLimiteDisponivel);
            model.addAttribute("margemLimiteConsignavel", margemLimiteConsignavel);
            model.addAttribute("margemConsignavel", margemConsignavel);
            model.addAttribute("margemTratamentoEspecial", margemTratamentoEspecial);
            model.addAttribute("somaValorContratosTratamentoEspecial", somaValorContratosTratamentoEspecial);
            model.addAttribute("serCodigo", serCodigo);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("cnvCodigo", cnvCodigo);
            model.addAttribute("svcCodigo", svcCodigo);
            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("svcDescricao", svcDescricao);
            model.addAttribute("svcPrioridade", svcPrioridade);
            model.addAttribute("servicoCompulsorio", servicoCompulsorio);
            model.addAttribute("validaMargemViaJavascript", validaMargemViaJavascript);
            model.addAttribute("txtExplicativo", txtExplicativo);
            model.addAttribute("isServidor", responsavel.isSer());

            // Parâmetros
            model.addAttribute("serInfBancariaObrigatoria", serInfBancariaObrigatoria);
            model.addAttribute("validarInfBancaria", validarInfBancaria);
            model.addAttribute("validarDataNasc", validarDataNasc);
            model.addAttribute("permiteCadVlrLiqTxJuros", permiteCadVlrLiqTxJuros);
            model.addAttribute("permiteCadVlrTac", permiteCadVlrTac);
            model.addAttribute("permiteCadVlrIof", permiteCadVlrIof);
            model.addAttribute("permiteCadVlrLiqLib", permiteCadVlrLiqLib);
            model.addAttribute("permiteCadVlrMensVinc", permiteCadVlrMensVinc);
            model.addAttribute("permiteCadVlrSegPrestamista", permiteCadVlrSegPrestamista);
            model.addAttribute("possuiCorrecaoVlrPresente", possuiCorrecaoVlrPresente);
            model.addAttribute("possuiControleVlrMaxDesconto", possuiControleVlrMaxDesconto);
            model.addAttribute("possuiComposicaoMargem", possuiComposicaoMargem);
            model.addAttribute("possuiVariacaoMargem", possuiVariacaoMargem);
            model.addAttribute("podeMostrarMargem", podeMostrarMargem);
            model.addAttribute("exigeSenha", exigeSenha);
            model.addAttribute("senhaServidorOK", senhaServidorOK);
            model.addAttribute("exibeAlgumaMargem", exibeAlgumaMargem);
            model.addAttribute("exibeHistLiqAntecipadas", exibeHistLiqAntecipadas);
            model.addAttribute("intFolha", intFolha);
            model.addAttribute("incMargem", incMargem);
            model.addAttribute("prazoFixo", prazoFixo);
            model.addAttribute("maxPrazo", maxPrazo);
            model.addAttribute("adeVlrPadrao", adeVlrPadrao);
            model.addAttribute("carenciaMinPermitida", carenciaMinPermitida);
            model.addAttribute("carenciaMaxPermitida", carenciaMaxPermitida);
            model.addAttribute("permitePrazoMaiorContSer", permitePrazoMaiorContSer);
            model.addAttribute("tipoVlr", tipoVlr);
            model.addAttribute("alteraAdeVlr", alteraAdeVlr);
            model.addAttribute("permiteVlrNegativo", permiteVlrNegativo);
            model.addAttribute("vlrLimite", vlrLimite);
            model.addAttribute("vlrMaxParcelaSaldoDevedor", vlrMaxParcelaSaldoDevedor);
            model.addAttribute("identificadorAdeObrigatorio", identificadorAdeObrigatorio);
            model.addAttribute("mascaraAdeIdentificador", mascaraAdeIdentificador);
            model.addAttribute("numAdeHistLiqAntecipadas", numAdeHistLiqAntecipadas);
            model.addAttribute("maxTacCse", maxTacCse);
            model.addAttribute("exibeMaisAcoes", exibeMaisAcoes);
            model.addAttribute("servidorDeveSerKYCComplaint", servidorDeveSerKYCComplaint);

            // Dados de servidor e registro servidor necessários
            model.addAttribute("rseMatricula", rseMatricula);
            model.addAttribute("serDataNasc", serDataNasc);
            model.addAttribute("numBanco", numBanco);
            model.addAttribute("numAgencia", numAgencia);
            model.addAttribute("numConta1", numConta1);
            model.addAttribute("numConta2", numConta2);
            model.addAttribute("numBancoAlt", numBancoAlt);
            model.addAttribute("numAgenciaAlt", numAgenciaAlt);
            model.addAttribute("numContaAlt1", numContaAlt1);
            model.addAttribute("numContaAlt2", numContaAlt2);
            model.addAttribute("sizeNumAgencia", sizeNumAgencia);

            // Opções de inclusão avançada
            model.addAttribute("desabilitaOpcoesAvancadas", Boolean.TRUE);
            model.addAttribute("validaMargemAvancado", validaMargemAvancado);
            model.addAttribute("validaTaxaAvancado", validaTaxaAvancado);
            model.addAttribute("validaPrazoAvancado", validaPrazoAvancado);
            model.addAttribute("validaDadosBancariosAvancado", validaDadosBancariosAvancado);
            model.addAttribute("validaSenhaServidorAvancado", validaSenhaServidorAvancado);
            model.addAttribute("validaBloqSerCnvCsaAvancado", validaBloqSerCnvCsaAvancado);
            model.addAttribute("validaDataNascAvancado", validaDataNascAvancado);
            model.addAttribute("validaLimiteAdeAvancado", validaLimiteAdeAvancado);

            // Verificamos quais Consignatárias permitem ser contactadas
            final List<String> csaCodigos = new ArrayList<>();
            csaCodigos.add(csaCodigo);

            final List<TransferObject> listaCsaPermiteContato = consignatariaController.listaCsaPermiteContato(csaCodigos, responsavel);
            final HashMap<String, TransferObject> hashCsaPermiteContato = new HashMap<>();

            for (final TransferObject csaPermiteContato : listaCsaPermiteContato) {
                hashCsaPermiteContato.put((String) csaPermiteContato.getAttribute(Columns.CSA_CODIGO), csaPermiteContato);
            }
            model.addAttribute("hashCsaPermiteContato", hashCsaPermiteContato);
            model.addAttribute("anexoObrigatorio", parametroController.isObrigatorioAnexoInclusao(svcCodigo, responsavel));
            model.addAttribute("qtdeMinAnexos", paramSvcCse.getTpsQuantidadeMinimaInclusaoContratos());

            String msgPertenceCategoria = null;
            final RegistroServidorTO registroServidor = servidorController.findRegistroServidor(rseCodigo, responsavel);
            final String paramCsa = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_CATEGORIAS_PARA_EXIBIR_MENSAGEM_RESERVA_CONSULTA_MARGEM, responsavel);
            if(!TextHelper.isNull(paramCsa)) {
                if(paramCsa.contains(registroServidor.getRseTipo())) {
                    msgPertenceCategoria = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_MENSAGEM_EXIBIDA_CSA_COR, responsavel);
                    model.addAttribute("msgPertenceCategoria", msgPertenceCategoria);
                    model.addAttribute("exibeAlertaMsgPertenceCategoria", true);
                }
            } else {
                model.addAttribute("exibeAlertaMsgPertenceCategoria", false);
            }
        } catch (NumberFormatException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/reservarMargem/reservar", request, session, model, responsavel);
    }

    @Override
    protected String executarFuncaoAposDuplicidade(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServidorControllerException, ParametroControllerException {
        final String rseCodigo = request.getParameter("RSE_CODIGO");
        return autorizarReserva(rseCodigo, request, response, session, model);
    }

    @RequestMapping(params = { "acao=autorizarReserva" })
    public String autorizarReserva(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            // verifica se há permissão para parâmetros de inclusão avançada
            final boolean usuPossuiIncAvancadaAde = responsavel.temPermissao(CodedValues.FUN_INCLUSAO_AVANCADA_CONSIGNACAO);

            // Quando a inclusão é de uma decisão judicial, é como se fosse uma inclusão avançada, desabilitando todas as validações
            final boolean inclusaoJudicial = request.getParameter("inclusaoJudicial") != null;

            // Verifica opções para inclusão avançada de contrato
            final boolean validaDadosBancariosAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaDadosBancarios")) ? Boolean.parseBoolean(request.getParameter("validaDadosBancarios")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_DADOS_BANCARIOS;
            final boolean validaSenhaServidorAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaSenhaServidor")) ? Boolean.parseBoolean(request.getParameter("validaSenhaServidor")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_SENHA_SERVIDOR;
            final boolean validaTaxaAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaTaxa")) ? Boolean.parseBoolean(request.getParameter("validaTaxa")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_TAXA_JUROS;
            final boolean validaPrazoAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaPrazo")) ? Boolean.parseBoolean(request.getParameter("validaPrazo")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_TAXA_JUROS;
            final boolean validaBloqSerCnvCsaAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaBloqSerCnvCsa")) ? Boolean.parseBoolean(request.getParameter("validaBloqSerCnvCsa")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_BLOQ_SER_CNV_CSA;
            final boolean validaDataNascAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaDataNascimento")) ? Boolean.parseBoolean(request.getParameter("validaDataNascimento")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_DATA_NASCIMENTO;
            final boolean validaLimiteAdeAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaLimiteAde")) ? Boolean.parseBoolean(request.getParameter("validaLimiteAde")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_LIMITE_ADE;
            final boolean validaMargemAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaMargem")) ? Boolean.parseBoolean(request.getParameter("validaMargem")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_MARGEM;

            String csaNome = "", csaCodigo = "";
            if (responsavel.isCseSupOrg() || responsavel.isSer()) {
                csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
                if (!"".equals(csaCodigo)) {
                    final ConsignatariaTransferObject consignataria = consignatariaController.findConsignataria(csaCodigo, responsavel);
                    csaNome = consignataria.getCsaIdentificador() + " - " + (!TextHelper.isNull(consignataria.getCsaNomeAbreviado()) ? consignataria.getCsaNomeAbreviado() : consignataria.getCsaNome());
                }
            } else if (responsavel.isCsaCor()) {
                csaCodigo = responsavel.getCsaCodigo();
                if (responsavel.isCsa()) {
                    csaNome = responsavel.getNomeEntidade();
                } else {
                    csaNome = responsavel.getNomeEntidadePai();
                }
            }
            if (!TextHelper.isNull(csaCodigo)) {
                model.addAttribute("csaCodigo", csaCodigo);
                model.addAttribute("csaNome", csaNome);
            }

            String corNome = "", corCodigo = "";
            if (responsavel.isCseSupOrg() || responsavel.isCsa()) {
                corCodigo = JspHelper.verificaVarQryStr(request, "COR_CODIGO");
                if (!"".equals(corCodigo)) {
                    final String cor[] = corCodigo.split(";");
                    corNome = cor[1] + " - " + cor[2];
                    corCodigo = cor[0];
                }
            } else if (responsavel.isCor()) {
                corCodigo = responsavel.getCodigoEntidade();
                corNome = responsavel.getNomeEntidade();
            } else if (!"".equals(JspHelper.verificaVarQryStr(request, "PORTAL_BENEFICIO")) && !"".equals(JspHelper.verificaVarQryStr(request, "COR_CODIGO"))) {
                corCodigo = JspHelper.verificaVarQryStr(request, "COR_CODIGO");
                final CorrespondenteTransferObject correspondente = consignatariaController.findCorrespondente(corCodigo, responsavel);
                corNome = !TextHelper.isNull(correspondente) ? correspondente.getCorNome() : corNome;

                model.addAttribute("portalBeneficio", true);
                model.addAttribute("COR_CODIGO",corCodigo);
            }
            if (!TextHelper.isNull(corCodigo)) {
                model.addAttribute("corCodigo", corCodigo);
                model.addAttribute("corNome", corNome);
            }

            if (responsavel.isSer()) {
                rseCodigo = responsavel.getRseCodigo();
            }

            final String cnvCodigo = JspHelper.verificaVarQryStr(request, "CNV_CODIGO");
            final String adePrazo = JspHelper.verificaVarQryStr(request, "adePrazo");
            final String adeVlrLiquido = JspHelper.verificaVarQryStr(request, "adeVlrLiquido");
            String adeVlr = JspHelper.verificaVarQryStr(request, "adeVlr");
            final char separadorDecimal = LocaleHelper.getDecimalFormat().getDecimalFormatSymbols().getDecimalSeparator();
            if (adeVlr.indexOf(separadorDecimal) == -1) {
                adeVlr += separadorDecimal + "00";
            }

            final String numBanco = JspHelper.verificaVarQryStr(request, "numBanco");
            final String numAgencia = JspHelper.verificaVarQryStr(request, "numAgencia");
            final String numConta = JspHelper.verificaVarQryStr(request, "numConta");

            // Cadastro de índice
            String adeIndice = JspHelper.verificaVarQryStr(request, "adeIndice");
            String adeIndiceDescricao = "";
            if (adeIndice.indexOf(";") != -1) {
                adeIndiceDescricao = " - " + adeIndice.substring(adeIndice.indexOf(";") + 1);
                adeIndice = adeIndice.substring(0, adeIndice.indexOf(";"));
            }

            final java.sql.Date ocaPeriodo = !TextHelper.isNull(request.getParameter("ocaPeriodo")) ? DateHelper.toSQLDate(DateHelper.parse(request.getParameter("ocaPeriodo"), "yyyy-MM-dd")) : null;
            Integer adeCarencia = TextHelper.isNum(JspHelper.verificaVarQryStr(request, "adeCarencia")) ? Integer.valueOf(JspHelper.verificaVarQryStr(request, "adeCarencia")) : 0;
            final String adeResponsavel = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel).getUsuLogin();
            final String adePeriodicidade = JspHelper.verificaVarQryStr(request, "adePeriodicidade");
            //Verifica se permite a escolha de periodicidade da folha diferente da que está configurada no sistema
            final boolean permiteEscolherPeriodicidade = ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODICIDADE_FOLHA, CodedValues.TPC_SIM, responsavel);
            if (permiteEscolherPeriodicidade && !PeriodoHelper.folhaMensal(responsavel)) {
                model.addAttribute("exibirCampoPeriodicidade", Boolean.TRUE);
            }

            final Integer prazo = !TextHelper.isNull(adePrazo) ? Integer.valueOf(adePrazo) : null;
            final BigDecimal valor = !TextHelper.isNull(adeVlr) ? new BigDecimal(String.valueOf(NumberHelper.parse(adeVlr, NumberHelper.getLang()))) : null;
            final BigDecimal liberado = !TextHelper.isNull(adeVlrLiquido) ? new BigDecimal(String.valueOf(NumberHelper.parse(adeVlrLiquido, NumberHelper.getLang()))) : null;

            // Verifica se as entidades não estão bloqueadas
            try {
                final boolean telaConfirmacaoDuplicidade = "S".equals(request.getParameter("telaConfirmacaoDuplicidade"));
                autorizacaoController.podeReservarMargem(cnvCodigo, corCodigo, rseCodigo, validaBloqSerCnvCsaAvancado, true, validaBloqSerCnvCsaAvancado, null, valor, liberado, prazo, adeCarencia, adePeriodicidade, null, null, "RESERVAR", validaLimiteAdeAvancado, telaConfirmacaoDuplicidade, responsavel);
            } catch (final AutorizacaoControllerException ex) {
                final String messageKey = ex.getMessageKey();
                if ("mensagem.erro.ade.duplicidade.bloqueada.ate.data.limite".equals(messageKey)) {
                    return verificarPossibilidadePermitirDuplicidade(request, session, model, responsavel, cnvCodigo, "autorizarReserva", ex);
                } else if (responsavel.isCsaCor() && ParamSist.getBoolParamSist(CodedValues.TPC_LST_SERVICOS_CSA_COR_ABAIXO_LIMITE_CONTRATOS, responsavel) && ("mensagem.qtdMaxContratosExcedida".equals(messageKey)
                        || "mensagem.erro.nao.possivel.inserir.ou.alterar.reserva.pois.excede.limite.contratos.do.servidor.para.este.servico".equals(messageKey)
                        || "mensagem.erro.nao.possivel.inserir.ou.alterar.reserva.pois.excede.limite.contratos.do.servidor.para.esta.natureza.servico".equals(messageKey))) {

                } else {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            // Busca o Convênio
            CustomTransferObject convenio = null;
            try {
                convenio = convenioController.getParamCnv(cnvCodigo, validaBloqSerCnvCsaAvancado, validaBloqSerCnvCsaAvancado, responsavel);
            } catch (final ConvenioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            if (convenio == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.convenio.inexistente.ser", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final String svcDescricao = convenio.getAttribute(Columns.SVC_DESCRICAO).toString();
            final String svcCodigo = convenio.getAttribute(Columns.SVC_CODIGO).toString();
            final String orgCodigo = convenio.getAttribute(Columns.CNV_ORG_CODIGO).toString();

            model.addAttribute("svcDescricao", svcDescricao);

            // Valida os parâmetros de plano de desconto com base no plano/serviço selecionado
            validarServicoOperacao(svcCodigo, rseCodigo, null, request, session, model, responsavel);

            java.sql.Date adeAnoMesIni = null;
            java.sql.Date adeAnoMesFim = null;

            adeCarencia = parametroController.calcularAdeCarenciaDiaCorteCsa(adeCarencia, csaCodigo, orgCodigo, responsavel);

            final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            try {
                adeAnoMesIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, ocaPeriodo, adeCarencia, adePeriodicidade, responsavel);

                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_PERIODO_ACEITA_APENAS_REDUCOES, CodedValues.TPC_SIM, responsavel)) {
                    final java.sql.Date adeAnoMesIniValido = PeriodoHelper.getInstance().validarAdeAnoMesIni(orgCodigo, adeAnoMesIni, responsavel);
                    if (!adeAnoMesIniValido.equals(adeAnoMesIni)) {
                        session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.alerta.reservar.margem.data.inicial.ajustada.periodo.apenas.reducoes", responsavel));
                        adeAnoMesIni = adeAnoMesIniValido;
                    }
                }

                final java.sql.Date dataInicioFimAde = autorizacaoController.calcularDataIniFimMargemExtra(rseCodigo, adeAnoMesIni, paramSvcCse.getTpsIncideMargem(), true, false, responsavel);
                boolean mensagemAlertaAlteracaoDataInicio = false;
                if((dataInicioFimAde != null) && (dataInicioFimAde.compareTo(adeAnoMesIni) > 0)) {
                    adeAnoMesIni = dataInicioFimAde;
                    adeCarencia = 0;
                    mensagemAlertaAlteracaoDataInicio = true;
                }

                adeAnoMesFim = PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, adeAnoMesIni, prazo, adePeriodicidade, responsavel);
                autorizacaoController.calcularDataIniFimMargemExtra(rseCodigo, adeAnoMesFim, paramSvcCse.getTpsIncideMargem(), false, true, responsavel);
                if(mensagemAlertaAlteracaoDataInicio) {
                    session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.info.calcular.data.inicio.fim.margem.extra.rse.data.margem.maior.web", responsavel));
                }

            } catch (final PeriodoException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Busca o servidor
            final CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            if (servidor == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            model.addAttribute("servidor", servidor);
            final String nomeArqFotoServidor = JspHelper.getPhoto(servidor.getAttribute(Columns.SER_CPF).toString(), rseCodigo, responsavel);
            if (!TextHelper.isNull(nomeArqFotoServidor)) {
                model.addAttribute("nomeArqFotoServidor", nomeArqFotoServidor);
            }

            final String serDataNasc = servidor.getAttribute(Columns.SER_DATA_NASC) != null ? DateHelper.reformat(servidor.getAttribute(Columns.SER_DATA_NASC).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern()) : "";
            final String serCodigo = servidor.getAttribute(Columns.SER_CODIGO) != null ? servidor.getAttribute(Columns.SER_CODIGO).toString() : "";
            final Integer rsePrazo = (Integer) servidor.getAttribute(Columns.RSE_PRAZO);
            final String rseBancoSal = servidor.getAttribute(Columns.RSE_BANCO_SAL) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_BANCO_SAL).toString(), "0", JspHelper.ESQ) : "";
            final String rseAgenciaSal = servidor.getAttribute(Columns.RSE_AGENCIA_SAL) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_AGENCIA_SAL).toString(), "0", JspHelper.ESQ) : "";
            final String rseContaSal = servidor.getAttribute(Columns.RSE_CONTA_SAL) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_CONTA_SAL).toString(), "0", JspHelper.ESQ) : "";

            final String rseBancoSalAlt = servidor.getAttribute(Columns.RSE_BANCO_SAL_2) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_BANCO_SAL_2).toString(), "0", JspHelper.ESQ) : "";
            final String rseAgenciaSalAlt = servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2).toString(), "0", JspHelper.ESQ) : "";
            final String rseContaSalAlt = servidor.getAttribute(Columns.RSE_CONTA_SAL_2) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_CONTA_SAL_2).toString(), "0", JspHelper.ESQ) : "";

            final String adeDataIni = adeAnoMesIni != null ? DateHelper.toPeriodString(adeAnoMesIni) : "";
            final String adeDataFim = adeAnoMesFim != null ? DateHelper.toPeriodString(adeAnoMesFim) : "";

            boolean validarMargemReserva = true;
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_DESCONTO_EM_FILA, CodedValues.TPC_SIM, responsavel) && (!TextHelper.isNull(paramSvcCse.getTpsBaseCalcDescontoEmFila()) && !TextHelper.isNull(paramSvcCse.getTpsPercentualBaseCalcDescontoEmFila()))) {
                // Se o sistema permite módulo de desconto em fila e o serviço está configurado para realizar a fila
                // então não realiza validação de margem na reserva, pois esta não irá incidir na margem
                validarMargemReserva = false;
            }

            if(!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "reservaSaudeSemRegras"))) {
                final String codigoDependente = JspHelper.verificaVarQryStr(request, "codigoDependente");
                final String nomeDependente = JspHelper.verificaVarQryStr(request, "nomeDependente");
                if(!TextHelper.isNull(codigoDependente)) {
                    model.addAttribute("nomeDependente", nomeDependente);
                    model.addAttribute("codigoDependente", codigoDependente);
                }

                final String permiteDescontoViaBoleto = JspHelper.verificaVarQryStr(request, "permiteDescontoViaBoleto");
                if(!TextHelper.isNull(permiteDescontoViaBoleto)) {
                    if("S".equals(permiteDescontoViaBoleto)) {
                        validarMargemReserva = false;
                    }
                    model.addAttribute("permiteDescontoViaBoleto", permiteDescontoViaBoleto);
                }
                model.addAttribute("reservaSaudeSemRegras",true);
            }
            // Valida a reserva de Margem
            try {
                final CustomTransferObject reserva = new CustomTransferObject();
                reserva.setAttribute("ADE_PRAZO", adePrazo);
                reserva.setAttribute("ADE_PERIODICIDADE", adePeriodicidade);
                reserva.setAttribute("ADE_CARENCIA", adeCarencia);
                reserva.setAttribute("RSE_PRAZO", rsePrazo);
                reserva.setAttribute("ADE_VLR", NumberHelper.reformat(adeVlr, NumberHelper.getLang(), "en"));
                reserva.setAttribute("RSE_CODIGO", rseCodigo);
                reserva.setAttribute("CSE_CODIGO", CodedValues.CSE_CODIGO_SISTEMA);
                reserva.setAttribute("SVC_CODIGO", svcCodigo);

                if (usuPossuiIncAvancadaAde) {
                    final Map<String, Object> paramIncAvancada = new HashMap<>();
                    paramIncAvancada.put(CodedValues.PARAM_INC_AVANCADA_VALIDA_MARGEM, Boolean.valueOf(validaMargemAvancado));
                    paramIncAvancada.put(CodedValues.PARAM_INC_AVANCADA_VALIDA_TAXA_JUROS, Boolean.valueOf(validaTaxaAvancado));
                    paramIncAvancada.put(CodedValues.PARAM_INC_AVANCADA_VALIDA_PRAZO, Boolean.valueOf(validaPrazoAvancado));
                    paramIncAvancada.put(CodedValues.PARAM_INC_AVANCADA_VALIDA_DADOS_BANCARIOS, Boolean.valueOf(validaDadosBancariosAvancado));
                    paramIncAvancada.put(CodedValues.PARAM_INC_AVANCADA_VALIDA_SENHA_SERVIDOR, Boolean.valueOf(validaSenhaServidorAvancado));
                    paramIncAvancada.put(CodedValues.PARAM_INC_AVANCADA_VALIDA_BLOQ_SER_CNV_CSA, Boolean.valueOf(validaBloqSerCnvCsaAvancado));
                    paramIncAvancada.put(CodedValues.PARAM_INC_AVANCADA_VALIDA_LIMITE_ADE, Boolean.valueOf(validaLimiteAdeAvancado));

                    reserva.setAtributos(paramIncAvancada);
                }

                ReservaMargemHelper.validaReserva(convenio, reserva, responsavel, false, validarMargemReserva, validaBloqSerCnvCsaAvancado);

            } catch (final ViewHelperException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final String tipoVlr = paramSvcCse.getTpsTipoVlr();
            final int maxPrazo = (paramSvcCse.getTpsMaxPrazo() != null) && !"".equals(paramSvcCse.getTpsMaxPrazo()) ? Integer.parseInt(paramSvcCse.getTpsMaxPrazo()) : -1;
            final String labelTipoValor = ParamSvcTO.getDescricaoTpsTipoVlr(tipoVlr);
            final String rotuloPrzIndet = ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel);
            final String labelAdePrazo = maxPrazo == 0 ? rotuloPrzIndet : adePrazo;
            final String mascaraAdeIdentificador = paramSvcCse.getTpsMascaraIdentificadorAde();

            // Parâmetro com nome da classe java, que implementa a interface ProcessaReservaMargem
            final String classeProcReserva = paramSvcCse.getTpsClasseJavaProcEspecificoReserva();

            // Quantidade de consignatárias permitidas no simulador
            final int qtdeConsignatariasSimulacao = paramSvcCse.getTpsQtdCsaPermitidasSimulador();
            final boolean bloqueiaReservaLimiteSimulador = paramSvcCse.isTpsBloqueiaReservaLimiteSimulador();

            boolean permiteCadVlrTac = paramSvcCse.isTpsCadValorTac();
            boolean permiteCadVlrIof = paramSvcCse.isTpsCadValorIof();
            boolean permiteCadVlrLiqLib = paramSvcCse.isTpsCadValorLiquidoLiberado();
            boolean permiteCadVlrMensVinc = paramSvcCse.isTpsCadValorMensalidadeVinc();
            boolean permiteCadVlrLiqTxJuros = paramSvcCse.isTpsVlrLiqTaxaJuros();
            boolean permiteCadVlrSegPrestamista = paramSvcCse.isTpsExigeSeguroPrestamista();
            final boolean possuiCorrecaoVlrPresente = paramSvcCse.isTpsPossuiCorrecaoValorPresente();
            boolean validarDataNasc = paramSvcCse.isTpsValidarDataNascimentoNaReserva();
            final boolean validarInfBancaria = validaDadosBancariosAvancado && paramSvcCse.isTpsValidarInfBancariaNaReserva();
            final boolean serInfBancariaObrigatoria = validaDadosBancariosAvancado && paramSvcCse.isTpsInfBancariaObrigatoria();
            final boolean serSenhaObrigatoria = validaSenhaServidorAvancado && parametroController.senhaServidorObrigatoriaReserva(rseCodigo, svcCodigo, csaCodigo, responsavel);
            final boolean exibeRanking = paramSvcCse.isTpsExibeRankingConfirmacaoReserva();
            final boolean exibirTabelaPrice = paramSvcCse.isTpsExibeTabelaPrice();

            // Se é o servidor que está solicitando, deixa os parâmetros
            // listados abaixo com os valores default
            if (responsavel.isSer()) {
                permiteCadVlrTac = false;
                permiteCadVlrIof = false;
                permiteCadVlrLiqLib = false;
                permiteCadVlrMensVinc = false;
                permiteCadVlrLiqTxJuros = false;
                permiteCadVlrSegPrestamista = false;
                validarDataNasc = false;
            }

            //se usuário gestor e com permissão de inclusão avançada, prevalesce a opção avançada
            if (responsavel.isCseSup() && usuPossuiIncAvancadaAde) {
                validarDataNasc &= validaDataNascAvancado;
            }

            if (validarDataNasc) {
                // Valida a data de nascimento do servidor de acordo com a data informada pelo usuário
                final String paramDataNasc = JspHelper.verificaVarQryStr(request, "dataNasc");
                if (!paramDataNasc.equals(serDataNasc)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.dataNascNaoConfere", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            // Valida informações bancárias
            if (serInfBancariaObrigatoria) {
                // Somente mostra mensagem se houver informações bancárias cadastradas para o (registro) servidor.
                if ((!TextHelper.isNull(rseBancoSal) && !TextHelper.isNull(rseAgenciaSal) && !TextHelper.isNull(rseContaSal)) || (!TextHelper.isNull(rseBancoSalAlt) && !TextHelper.isNull(rseAgenciaSalAlt) && !TextHelper.isNull(rseContaSalAlt))) {
                    // Se as informações bancárias são obrigatórias e devem ser válidas,
                    // então valida as informações digitadas pelo usuário
                    String msgInfBancarias = "";
                    final boolean naoConferem = (!TextHelper.formataParaComparacao(rseBancoSal).equals(TextHelper.formataParaComparacao(numBanco)) || !TextHelper.formataParaComparacao(rseAgenciaSal).equals(TextHelper.formataParaComparacao(numAgencia)) || !TextHelper.formataParaComparacao(rseContaSal).equals(TextHelper.formataParaComparacao(numConta)))
                            && (!TextHelper.formataParaComparacao(rseBancoSalAlt).equals(TextHelper.formataParaComparacao(numBanco)) || !TextHelper.formataParaComparacao(rseAgenciaSalAlt).equals(TextHelper.formataParaComparacao(numAgencia)) || !TextHelper.formataParaComparacao(rseContaSalAlt).equals(TextHelper.formataParaComparacao(numConta)));
                    if (naoConferem) {
                        if (validarInfBancaria) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informacaoBancariaIncorreta", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        } else {
                            msgInfBancarias = JspHelper.msgGenerica(ApplicationResourcesHelper.getMessage("rotulo.atencao", responsavel) + ": " + ApplicationResourcesHelper.getMessage("mensagem.informacaoBancariaIncorreta", responsavel), "100%", CodedValues.MSG_ALERT);
                        }
                    } else {
                        msgInfBancarias = JspHelper.msgGenerica(ApplicationResourcesHelper.getMessage("mensagem.informacaoBancariaCorreta", responsavel), "100%", CodedValues.MSG_INFO);
                    }
                    model.addAttribute("msgInfBancarias", msgInfBancarias);
                }
            }

            final String tpaModalidadeOperacao = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INF_MODALIDADE_OPERACAO_OBRIGATORIO, responsavel);
            final boolean exigeModalidadeOperacao = !TextHelper.isNull(tpaModalidadeOperacao) && "S".equals(tpaModalidadeOperacao);
            if (responsavel.isCsaCor() && exigeModalidadeOperacao) {
                model.addAttribute("exigeModalidadeOperacao", Boolean.TRUE);
            }

            final String tpaMatriculaSerCsa = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFORMAR_MATRICULA_NA_CSA_OBRIGATORIO, responsavel);
            final boolean exigeMatriculaSerCsa = !TextHelper.isNull(tpaMatriculaSerCsa) && "S".equals(tpaMatriculaSerCsa);
            if (responsavel.isCsaCor() && exigeMatriculaSerCsa) {
                model.addAttribute("exigeMatriculaSerCsa", Boolean.TRUE);
            }

            // Se o serviço possui processamento específico de reserva,
            // cria a classe de execução
            ProcessaReservaMargem processador = null;
            if (classeProcReserva != null) {
                try {
                    processador = ProcessaReservaMargemFactory.getProcessador(classeProcReserva);
                } catch (final ViewHelperException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }
            }
            // Executa validação do passo 1
            if (processador != null) {
                try {
                    processador.validarPasso1(request);
                } catch (final ViewHelperException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            // Informações para simulação por taxa de juros
            String cftCodigo = "";
            String dtjCodigo = "";

            BigDecimal adeVlrCorrigido = null;
            if (possuiCorrecaoVlrPresente) {
                final String dataEvento = JspHelper.verificaVarQryStr(request, "dataEvento");
                if (!"".equals(dataEvento)) {
                    try {
                        adeVlrCorrigido = new BigDecimal(NumberHelper.parse(adeVlr, NumberHelper.getLang()));
                        adeVlrCorrigido = autorizacaoController.corrigirValorPresente(adeVlrCorrigido, DateHelper.parse(dataEvento, LocaleHelper.getDatePattern()), svcCodigo, responsavel);
                    } catch (final AutorizacaoControllerException ex) {
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }
            }

            if (ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel) && (adePrazo != null) && !"".equals(adePrazo)) {

                try {
                    // Pega os coeficientes ativos
                    final short dia = (short) Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                    short prazoCft = Short.parseShort(adePrazo);

                    if (CodedValues.PERIODICIDADE_FOLHA_QUINZENAL.equals(adePeriodicidade)) {
                        prazoCft = (short) Math.round(prazoCft / 2.0);
                    }

                    final BigDecimal vlrParcela = new BigDecimal(NumberHelper.reformat(adeVlr, NumberHelper.getLang(), "en"));
                    final List<TransferObject> cft = simulacaoController.getCoeficienteAtivo(csaCodigo, svcCodigo, orgCodigo, rseCodigo, prazoCft, dia, validaBloqSerCnvCsaAvancado, vlrParcela, liberado, responsavel);
                    if ((cft != null) && (cft.size() > 0)) {
                        final TransferObject coeficiente = cft.get(0);
                        cftCodigo = coeficiente.getAttribute(Columns.CFT_CODIGO).toString();
                        if (coeficiente.getAtributos().containsKey(Columns.DTJ_CODIGO)) {
                            dtjCodigo = coeficiente.getAttribute(Columns.DTJ_CODIGO).toString();
                        }
                    }
                } catch (final SimulacaoControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                if (exibeRanking) {
                    try {
                        final BigDecimal vlrParcela = new BigDecimal(NumberHelper.reformat(adeVlr, NumberHelper.getLang(), "en"));
                        List<TransferObject> lstSimulacao = simulacaoController.simularConsignacao(svcCodigo, orgCodigo, rseCodigo, vlrParcela, null, Short.parseShort(adePrazo), adeAnoMesIni, validaBloqSerCnvCsaAvancado, adePeriodicidade, responsavel);

                        if (bloqueiaReservaLimiteSimulador && (qtdeConsignatariasSimulacao != Integer.MAX_VALUE)) {
                            // Se tem limite de consignatárias no ranking, e novas reservas também devem ser bloqueadas, então
                            // seleciona as linhas que podem ser usadas de acordo com a posição no ranking
                            lstSimulacao = simulacaoController.selecionarLinhasSimulacao(lstSimulacao, rseCodigo, null, qtdeConsignatariasSimulacao, true, false, responsavel);
                        }

                        if ((lstSimulacao != null) && !lstSimulacao.isEmpty()) {
                            final String qtdeColunasSimulacao = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_COLUNAS_SIMULACAO, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_COLUNAS_SIMULACAO, responsavel).toString() : "4";
                            int nroColunas = Integer.parseInt(qtdeColunasSimulacao);
                            nroColunas = nroColunas < 1 ? 1 : nroColunas > 4 ? 4 : nroColunas;
                            model.addAttribute("nroColunasSimulacao", nroColunas);

                            final List<ResultadoSimulacao> resultadoSimulacao = new ArrayList<>();
                            for (final TransferObject linhaSimulacao : lstSimulacao) {
                                final ResultadoSimulacao linhaResultadoSimulacao = new ResultadoSimulacao().carregarValores(linhaSimulacao);
                                resultadoSimulacao.add(linhaResultadoSimulacao);

                                final String csaCodigoLst = (String) linhaSimulacao.getAttribute(Columns.CSA_CODIGO);
                                if (csaCodigo.equals(csaCodigoLst)) {
                                    model.addAttribute("resultadoSimulacaoCSA", linhaResultadoSimulacao);
                                    if (bloqueiaReservaLimiteSimulador && validaBloqSerCnvCsaAvancado && (qtdeConsignatariasSimulacao != Integer.MAX_VALUE) && !((Boolean) linhaSimulacao.getAttribute("OK")).booleanValue()) {
                                        // Verifica se a consignatária pode incluir a reserva, caso haja no sistema limite de consignatárias no ranking do simulador
                                        // Se não esta validando bloqueio de consignatária é inclusão avançada
                                        model.addAttribute("msgErroReserva", ApplicationResourcesHelper.getMessage("mensagem.erro.limite.csa.ranking", responsavel, String.valueOf(linhaResultadoSimulacao.getRanking()), String.valueOf(qtdeConsignatariasSimulacao)));
                                    }
                                }
                            }
                            model.addAttribute("resultadoSimulacao", resultadoSimulacao);

                        }
                    } catch (final SimulacaoControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }

            final List<TransferObject> tdaList = autorizacaoController.lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.WEB, svcCodigo, csaCodigo, responsavel);
            if ((tdaList != null) && !tdaList.isEmpty()) {
                model.addAttribute("lstTipoDadoAdicional", tdaList);
            }

            if (processador != null) {
                model.addAttribute("processaReservaMargem", processador.incluirPasso2(request));
            }

            if (exibirTabelaPrice && !TextHelper.isNull(liberado) && !TextHelper.isNull(valor) && !TextHelper.isNull(prazo) && !TextHelper.isNull(cftCodigo)) {

                final TransferObject cft = simulacaoController.getCoeficienteAtivo(cftCodigo);

                final CustomTransferObject dadosTabelaPrice = new CustomTransferObject();
                dadosTabelaPrice.setAttribute(Columns.ADE_CODIGO, "");
                dadosTabelaPrice.setAttribute(Columns.CSA_CODIGO, csaCodigo);
                dadosTabelaPrice.setAttribute(Columns.ORG_CODIGO, orgCodigo);
                dadosTabelaPrice.setAttribute(Columns.RSE_CODIGO, rseCodigo);
                dadosTabelaPrice.setAttribute(Columns.SVC_CODIGO, svcCodigo);
                dadosTabelaPrice.setAttribute(Columns.CFT_CODIGO, cftCodigo);
                dadosTabelaPrice.setAttribute(Columns.ADE_VLR, valor);
                dadosTabelaPrice.setAttribute(Columns.ADE_VLR_LIQUIDO, liberado);
                dadosTabelaPrice.setAttribute(Columns.ADE_PRAZO, prazo);
                dadosTabelaPrice.setAttribute(Columns.ADE_DATA, DateHelper.getSystemDatetime());
                dadosTabelaPrice.setAttribute(Columns.ADE_ANO_MES_INI, adeAnoMesIni);
                dadosTabelaPrice.setAttribute(Columns.ADE_ANO_MES_FIM, adeAnoMesFim);
                dadosTabelaPrice.setAttribute(Columns.CFT_VLR, cft.getAttribute(Columns.CFT_VLR));
                dadosTabelaPrice.setAttribute(Columns.ADE_CARENCIA, adeCarencia);

                model.addAttribute("dadosTabelaPrice", dadosTabelaPrice);
            }

            if ((responsavel.temPermissao(CodedValues.FUN_INCLUSAO_AVANCADA_CONSIGNACAO) && CodedValues.FUN_RES_MARGEM.equals(responsavel.getFunCodigo())) || inclusaoJudicial) {
                model.addAttribute("tmoDescricao", ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel));
                if (!TextHelper.isNull(request.getParameter("tmoCodigo"))) {
                    final TipoMotivoOperacaoTransferObject motivo = tipoMotivoOperacaoController.findMotivoOperacao(request.getParameter("tmoCodigo"), responsavel);
                    if (motivo != null) {
                        model.addAttribute("tmoCodigo", motivo.getTmoCodigo());
                        model.addAttribute("tmoDescricao", motivo.getTmoDescricao());
                    }
                }

                model.addAttribute("lstTipoJustica", sistemaController.lstTipoJustica(responsavel));

                if (inclusaoJudicial) {
                    model.addAttribute("inclusaoJudicial", inclusaoJudicial);
                    model.addAttribute("lstMtvOperacao", tipoMotivoOperacaoController.lstMotivoOperacaoConsignacao(CodedValues.STS_ATIVO, responsavel));
                }
            }

            // Adiciona ao modelo acessível ao JSP as configurações necessárias
            model.addAttribute("paramSvcCse", paramSvcCse);
            model.addAttribute("permiteCadVlrLiqTxJuros", permiteCadVlrLiqTxJuros);
            model.addAttribute("permiteCadVlrTac", permiteCadVlrTac);
            model.addAttribute("permiteCadVlrIof", permiteCadVlrIof);
            model.addAttribute("permiteCadVlrLiqLib", permiteCadVlrLiqLib);
            model.addAttribute("permiteCadVlrMensVinc", permiteCadVlrMensVinc);
            model.addAttribute("permiteCadVlrSegPrestamista", permiteCadVlrSegPrestamista);
            model.addAttribute("possuiCorrecaoVlrPresente", possuiCorrecaoVlrPresente);
            model.addAttribute("serSenhaObrigatoria", serSenhaObrigatoria);
            model.addAttribute("bloqueiaReservaLimiteSimulador", bloqueiaReservaLimiteSimulador);
            model.addAttribute("qtdeConsignatariasSimulacao", qtdeConsignatariasSimulacao);
            model.addAttribute("mascaraAdeIdentificador", mascaraAdeIdentificador);
            model.addAttribute("labelTipoValor", labelTipoValor);
            model.addAttribute("labelAdePrazo", labelAdePrazo);

            // Opções de inclusão avançada
            model.addAttribute("desabilitaOpcoesAvancadas", Boolean.TRUE);
            model.addAttribute("validaMargemAvancado", validaMargemAvancado);
            model.addAttribute("validaTaxaAvancado", validaTaxaAvancado);
            model.addAttribute("validaPrazoAvancado", validaPrazoAvancado);
            model.addAttribute("validaDadosBancariosAvancado", validaDadosBancariosAvancado);
            model.addAttribute("validaSenhaServidorAvancado", validaSenhaServidorAvancado);
            model.addAttribute("validaBloqSerCnvCsaAvancado", validaBloqSerCnvCsaAvancado);
            model.addAttribute("validaDataNascAvancado", validaDataNascAvancado);
            model.addAttribute("validaLimiteAdeAvancado", validaLimiteAdeAvancado);

            // Demais valores necessários
            model.addAttribute("cnvCodigo", cnvCodigo);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("serCodigo", serCodigo);
            model.addAttribute("orgCodigo", orgCodigo);
            model.addAttribute("svcCodigo", svcCodigo);
            model.addAttribute("cftCodigo", cftCodigo);
            model.addAttribute("dtjCodigo", dtjCodigo);
            model.addAttribute("serDataNasc", serDataNasc);
            model.addAttribute("numBanco", numBanco);
            model.addAttribute("numAgencia", numAgencia);
            model.addAttribute("numConta", numConta);
            model.addAttribute("adeVlrCorrigido", adeVlrCorrigido);
            model.addAttribute("adePeriodicidade", adePeriodicidade);
            model.addAttribute("adeVlr", adeVlr);
            model.addAttribute("adePrazo", adePrazo);
            model.addAttribute("adeCarencia", adeCarencia);
            model.addAttribute("adeIndice", adeIndice);
            model.addAttribute("adeIndiceDescricao", adeIndiceDescricao);
            model.addAttribute("adeResponsavel", adeResponsavel);
            model.addAttribute("adeDataIni", adeDataIni);
            model.addAttribute("adeDataFim", adeDataFim);
            model.addAttribute("ocaPeriodo", ocaPeriodo);

            //usando para esconder
            final String tps_277 = request.getParameter("tps_277");
            model.addAttribute("tps_277", tps_277);

            if(!TextHelper.isNull(request.getParameter("enderecoObrigatorio"))) {
                model.addAttribute("enderecoObrigatorio", request.getParameter("enderecoObrigatorio"));
            } else if (!TextHelper.isNull(request.getParameter("celularObrigatorio"))) {
                model.addAttribute("celularObrigatorio", request.getParameter("celularObrigatorio"));
            } else if (!TextHelper.isNull(request.getParameter("enderecoCelularObrigatorio"))) {
                model.addAttribute("enderecoCelularObrigatorio", request.getParameter("enderecoCelularObrigatorio"));
            }

            //Envia o código de autorização enviado por SMS ao Servidor.
            final boolean exigeCodAutorizacaoSMS = ParamSist.paramEquals(CodedValues.TPC_ENVIA_SMS_CODIGO_UNICO_AUTORIZACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel);
            if (responsavel.isSer() && exigeCodAutorizacaoSMS) {
                usuarioController.enviarCodigoAutorizacaoSms(rseCodigo, responsavel);
            }
            model.addAttribute("exigeCodAutorizacaoSMS", exigeCodAutorizacaoSMS);

            // Valida a quantidade máxima de anexos permitidos por contrato
            final String paramQtdeMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_QTE_MAX_ARQ_ANEXO_CONTRATO, responsavel);
            final int qtdeMaxArqAnexo = !TextHelper.isNull(paramQtdeMaxArqAnexo) ? Integer.parseInt(paramQtdeMaxArqAnexo) : 10;
            final String nomeAnexo = JspHelper.verificaVarQryStr(request, "FILE1");
            final int totalAnexos = nomeAnexo.split(";").length;
            if (totalAnexos > qtdeMaxArqAnexo) {
                throw new AutorizacaoControllerException("mensagem.erro.quantidade.maxima.anexos.por.contrato.atingida", responsavel);
            }

            if(parametroController.isExigeReconhecimentoFacialServidor(svcCodigo, responsavel)) {
                model.addAttribute("exigeReconhecimentoFacil", "true");
            }
        } catch (NumberFormatException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/reservarMargem/autorizar", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=validarOtp" })
    public String validarOtp(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, boolean encontrouUmServidor) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Volta um elemento no topo da pilha para que ao voltar, o usuário caia no início da operação, caso usuário tenha selecionado servidor na listagem de servidores
        final ParamSession paramSession = ParamSession.getParamSession(session);
        if (encontrouUmServidor) {
            paramSession.halfBack();
        }

        // Se a senha é obrigatória para exibir dados cadastrais, então habilita exibição do campo de senha
        if (ParamSist.paramEquals(CodedValues.TPC_SENHA_SER_OBRIGATORIA_EXIBIR_DADOS_CSA_COR, CodedValues.TPC_SIM, responsavel) && responsavel.isCsaCor() && CodedValues.FUN_RES_MARGEM.equals(responsavel.getFunCodigo())) {
            model.addAttribute("exibirCampoSenha", Boolean.TRUE);
            model.addAttribute("senhaObrigatoriaConsulta", Boolean.TRUE);
        }

        return validarDigital(rseCodigo, request, response, session, model);
    }

    @RequestMapping(params = { "acao=validarDigital" })
    public String validarDigital(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Omite campo de ADE_NUMERO
        model.addAttribute("omitirAdeNumero", Boolean.TRUE);

        final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
        String csaCodigo = null;

        if (responsavel.isCsa()) {
            // Recuperando as informaçoes da consignatária pela sessão do usuário
            csaCodigo = responsavel.getCodigoEntidade();
        } else if (responsavel.isCor()) {
            // Recuperando as informaçoes da consignatária pela sessão do usuário
            csaCodigo = responsavel.getCodigoEntidadePai();
        } else {
            // Se é servidor, CSE ou ORG, obtém as informações pela requisição
            csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
        }
        if (TextHelper.isNull(csaCodigo) || TextHelper.isNull(svcCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (redirecionarTermoAdesao(request, session)) {
            model.addAttribute("proximaOperacao", "aceitarTermoAdesao");
        } else if (CodedValues.FUN_RES_MARGEM.equals(responsavel.getFunCodigo())) {
            model.addAttribute("proximaOperacao", "reservarMargem");
        } else {
            model.addAttribute("proximaOperacao", "pesquisarConsignacao");
        }
        model.addAttribute("rseCodigo", rseCodigo);

        // Monta lista de parâmetros através dos parâmetros de request
        final Set<String> params = new HashSet<>(request.getParameterMap().keySet());

        // Ignora os parâmetros abaixo
        params.remove("eConsig.page.token");
        params.remove("acao");
        params.remove("back");
        params.remove("tokenLeitor");
        params.remove("RSE_CODIGO");
        params.remove("RSE_MATRICULA");
        params.remove("SER_CPF");
        params.remove("ADE_NUMERO");
        params.remove("_skip_history_");

        try {
            if (parametroController.senhaServidorObrigatoriaConsultaMargem(rseCodigo, responsavel)) {
                model.addAttribute("exibirCampoSenha", Boolean.TRUE);
            }
        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final List<String> requestParams = new ArrayList<>(params);
        model.addAttribute("requestParams", requestParams);

        if (responsavel.temPermissao(CodedValues.FUN_INCLUSAO_AVANCADA_CONSIGNACAO) && CodedValues.FUN_RES_MARGEM.equals(responsavel.getFunCodigo())) {
            try {
                model.addAttribute("tmoDescricao", ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel));
                if (!TextHelper.isNull(request.getParameter("tmoCodigo"))) {
                    final TipoMotivoOperacaoTransferObject motivo = tipoMotivoOperacaoController.findMotivoOperacao(request.getParameter("tmoCodigo"), responsavel);
                    if (motivo != null) {
                        model.addAttribute("tmoCodigo", motivo.getTmoCodigo());
                        model.addAttribute("tmoDescricao", motivo.getTmoDescricao());
                    }
                }

                model.addAttribute("lstTipoJustica", sistemaController.lstTipoJustica(responsavel));
            } catch (TipoMotivoOperacaoControllerException | ConsignanteControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        if (responsavel.temPermissao(CodedValues.FUN_COMP_CONTRATO) && CodedValues.FUN_COMP_CONTRATO.equals(responsavel.getFunCodigo())) {
            try {
                final InformacaoSerCompraEnum exigeInfCompra = parametroController.senhaServidorObrigatoriaCompra(svcCodigo, rseCodigo, responsavel);
                if (InformacaoSerCompraEnum.SENHA.equals(exigeInfCompra)) {
                    model.addAttribute("exibirCampoSenhaAutorizacao", Boolean.TRUE);
                } else if (InformacaoSerCompraEnum.CONTA_BANCARIA.equals(exigeInfCompra)) {
                    model.addAttribute("exibirCampoInfBancaria", Boolean.TRUE);
                }
            } catch (final ParametroControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        // Opções de inclusão avançada
        model.addAttribute("desabilitaOpcoesAvancadas", Boolean.TRUE);

        return viewRedirect("jsp/consultarServidor/validarDigitalServidor", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=aceitarTermoAdesao" })
    public String aceitarTermoAdesao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
        String csaCodigo = null;

        if (responsavel.isCsa()) {
            // Recuperando as informaçoes da consignatária pela sessão do usuário
            csaCodigo = responsavel.getCodigoEntidade();
        } else if (responsavel.isCor()) {
            // Recuperando as informaçoes da consignatária pela sessão do usuário
            csaCodigo = responsavel.getCodigoEntidadePai();
        } else {
            // Se é servidor, CSE ou ORG, obtém as informações pela requisição
            csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
        }
        if (TextHelper.isNull(csaCodigo) || TextHelper.isNull(svcCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            final TermoAdesaoServicoTO terAdsTO = termoAdesaoController.findTermoAdesaoServico(new TermoAdesaoServicoTO(csaCodigo, svcCodigo, null), responsavel);
            final String terAdsTexto = terAdsTO.getTasTexto();
            final String textoFinal = new Markdown4jProcessorExtended().process(TextHelper.forHtmlContent(terAdsTexto));
            model.addAttribute("textoTermoAdesao", textoFinal);
        } catch (final IOException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (redirecionarTermoAdesao(request, session)) {
            model.addAttribute("proximaOperacao", "reservarMargem");
            model.addAttribute("rseCodigo", rseCodigo);
        } else {
            model.addAttribute("proximaOperacao", "autorizarReserva");
        }

        // Monta lista de parâmetros através dos parâmetros de request
        final Set<String> params = new HashSet<>(request.getParameterMap().keySet());

        // Ignora os parâmetros abaixo
        params.remove("eConsig.page.token");
        params.remove("acao");

        final List<String> requestParams = new ArrayList<>(params);
        model.addAttribute("requestParams", requestParams);

        return viewRedirect("jsp/reservarMargem/aceitarTermo", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=incluirReserva" })
    public String incluirReserva(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.reservar.margem.reserva.ja.inserida", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            // Sincroniza a sessão do usuário para evitar duplo request
            synchronized (session) {
                String corCodigo = null;
                if ((responsavel.isCseSupOrg() || responsavel.isCsa()) && !"".equals(JspHelper.verificaVarQryStr(request, "COR_CODIGO"))) {
                    corCodigo = JspHelper.verificaVarQryStr(request, "COR_CODIGO");
                } else if (responsavel.isCor()) {
                    corCodigo = responsavel.getCodigoEntidade();
                } else if (!"".equals(JspHelper.verificaVarQryStr(request, "PORTAL_BENEFICIO")) && !"".equals(JspHelper.verificaVarQryStr(request, "COR_CODIGO"))) {
                    corCodigo = JspHelper.verificaVarQryStr(request, "COR_CODIGO");
                }

                String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
                if (responsavel.isCsaCor()) {
                    csaCodigo = responsavel.getCsaCodigo();
                }

                String orgCodigo = JspHelper.verificaVarQryStr(request, "ORG_CODIGO");
                if (responsavel.isSer()) {
                    rseCodigo = responsavel.getRseCodigo();
                    orgCodigo = responsavel.getOrgCodigo();
                }

                String cnvCodigo = JspHelper.verificaVarQryStr(request, "CNV_CODIGO");
                final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
                Object adeValor = JspHelper.verificaVarQryStr(request, "adeVlr");
                final String[] incMargemGap = request.getParameterValues("incMargem");
                BigDecimal adeVlrCorrecao = null;
                final boolean tpcSolicitarPortabilidadeRanking = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "TPC_SOLICITAR_PORTABILIDADE_RANKING"));

                // recupera a permissão corrente do caso de uso
                String permissaoCorrente = getFunCodigo();

                if(tpcSolicitarPortabilidadeRanking) {
                    permissaoCorrente = CodedValues.FUN_SOLICITAR_PORTABILIDADE;

                    // Busca os dados do convênio
                    final CustomTransferObject convenio = convenioController.getParamCnv(csaCodigo, orgCodigo, svcCodigo, responsavel);
                    cnvCodigo = convenio.getAttribute(Columns.CNV_CODIGO).toString();
                }

                if(tpcSolicitarPortabilidadeRanking) {
                    permissaoCorrente = CodedValues.FUN_SOLICITAR_PORTABILIDADE;

                    // Busca os dados do convênio
                    final CustomTransferObject convenio = convenioController.getParamCnv(csaCodigo, orgCodigo, svcCodigo, responsavel);
                    cnvCodigo = convenio.getAttribute(Columns.CNV_CODIGO).toString();
                }

                if (!AcessoFuncaoServico.temAcessoFuncao(request, getFunCodigo(), responsavel.getUsuCodigo(), svcCodigo)) {
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                validarValoresObrigatorios(request, rseCodigo, csaCodigo, orgCodigo, cnvCodigo, svcCodigo, adeValor, responsavel);
                if (responsavel.isSer()) {
                    validaInformacoesServidorObrigatorias(request, svcCodigo, csaCodigo, responsavel);
                }

                String tdaModalidadeOp = null;
                String tdaMatriculaCsa = null;
                if (responsavel.isCsaCor()) {
                    final String tpaModalidadeOperacao = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INF_MODALIDADE_OPERACAO_OBRIGATORIO, responsavel);
                    final boolean exigeModalidadeOperacao = !TextHelper.isNull(tpaModalidadeOperacao) && "S".equals(tpaModalidadeOperacao);

                    if (exigeModalidadeOperacao) {
                        tdaModalidadeOp = JspHelper.verificaVarQryStr(request, "tdaModalidadeOp");

                        if (TextHelper.isNull(tdaModalidadeOp)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.modalidade.operacao.obrigatorio", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
                    }

                    final String tpaMatriculaSerCsa = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFORMAR_MATRICULA_NA_CSA_OBRIGATORIO, responsavel);
                    final boolean exigeMatriculaSerCsa = !TextHelper.isNull(tpaMatriculaSerCsa) && "S".equals(tpaMatriculaSerCsa);

                    if (exigeMatriculaSerCsa) {
                        tdaMatriculaCsa = JspHelper.verificaVarQryStr(request, "tdaMatriculaCsa");

                        if (TextHelper.isNull(tdaMatriculaCsa)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.matricula.csa.obrigatoria", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
                    }
                }

                // verifica se há permissão para parâmetros de inclusão avançada
                final boolean usuPossuiIncAvancadaAde = responsavel.temPermissao(CodedValues.FUN_INCLUSAO_AVANCADA_CONSIGNACAO);

                final boolean inclusaoJudicial = request.getParameter("inclusaoJudicial") != null;

                // Verifica opções para inclusão avançada de contrato
                final boolean validaDadosBancariosAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaDadosBancarios")) ? Boolean.parseBoolean(request.getParameter("validaDadosBancarios")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_DADOS_BANCARIOS;
                final boolean validaSenhaServidorAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaSenhaServidor")) ? Boolean.parseBoolean(request.getParameter("validaSenhaServidor")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_SENHA_SERVIDOR;
                final boolean validaTaxaAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaTaxa")) ? Boolean.parseBoolean(request.getParameter("validaTaxa")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_TAXA_JUROS;
                final boolean validaPrazoAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaPrazo")) ? Boolean.parseBoolean(request.getParameter("validaPrazo")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_TAXA_JUROS;
                final boolean validaBloqSerCnvCsaAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaBloqSerCnvCsa")) ? Boolean.parseBoolean(request.getParameter("validaBloqSerCnvCsa")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_BLOQ_SER_CNV_CSA;
                final boolean validaDataNascAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaDataNascimento")) ? Boolean.parseBoolean(request.getParameter("validaDataNascimento")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_DATA_NASCIMENTO;
                final boolean validaLimiteAdeAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaLimiteAde")) ? Boolean.parseBoolean(request.getParameter("validaLimiteAde")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_LIMITE_ADE;
                final boolean validaMargemAvancado = usuPossuiIncAvancadaAde && !TextHelper.isNull(request.getParameter("validaMargem")) ? Boolean.parseBoolean(request.getParameter("validaMargem")) : inclusaoJudicial ? false : ReservarMargemParametros.PADRAO_VALIDA_MARGEM;

                // Valida a reserva de Margem
                if (!TextHelper.isNull(adeValor)) {
                    try {
                        final String valor = NumberHelper.reformat(adeValor.toString(), NumberHelper.getLang(), "en");
                        final BigDecimal adeVlr = new BigDecimal(valor);
                        AutorizacaoHelper.validarValorAutorizacao(adeVlr, svcCodigo, csaCodigo, responsavel);
                    } catch (final ViewHelperException ex) {
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }

                final boolean temSimulacaoConsignacao = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel);

                Integer adePrazo = null;
                Integer adeCarencia = null;
                Short adeIncMargem = null;
                Short adeIntFolha = null;

                boolean exibeBoleto = true;
                if (responsavel.isCsaCor()) {
                    final List<String> tpsCodigo = new ArrayList<>();
                    tpsCodigo.add(CodedValues.TPS_EXIBE_BOLETO);

                    final List<TransferObject> paramSvcCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigo, false, responsavel);
                    for (final TransferObject param : paramSvcCsa) {
                        if ((param != null) && CodedValues.TPS_EXIBE_BOLETO.equals(param.getAttribute(Columns.TPS_CODIGO)) && (param.getAttribute(Columns.PSC_VLR) != null)) {
                            exibeBoleto = !"N".equals(param.getAttribute(Columns.PSC_VLR).toString());
                        }
                    }
                }

                final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                boolean permiteCadVlrLiqLib = paramSvcCse.isTpsCadValorLiquidoLiberado();
                boolean permiteCadVlrMensVinc = paramSvcCse.isTpsCadValorMensalidadeVinc();
                boolean permiteVlrLiqTxJuros = paramSvcCse.isTpsVlrLiqTaxaJuros();
                boolean boolTpsSegPrestamista = paramSvcCse.isTpsExigeSeguroPrestamista();
                boolean possuiCorrecaoVlrPresente = paramSvcCse.isTpsPossuiCorrecaoValorPresente();
                final boolean corrigeSaldoDevedorOutroServico = CodedValues.CORRECAO_SALDO_DEVEDOR_EM_OUTRO_SERVICO.equals(paramSvcCse.getTpsPossuiCorrecaoSaldoDevedor());
                final boolean boletoExterno = paramSvcCse.isTpsBuscaBoletoExterno();
                boolean validarDataNasc = paramSvcCse.isTpsValidarDataNascimentoNaReserva();
                final boolean deferimentoAutoSolicSer = paramSvcCse.isTpsDeferimentoAutoSolicitacaoServidor();
                boolean serInfBancariaObrigatoria = validaDadosBancariosAvancado && paramSvcCse.isTpsInfBancariaObrigatoria();
                boolean validarInfBancaria = validaDadosBancariosAvancado && paramSvcCse.isTpsValidarInfBancariaNaReserva();
                boolean serSenhaObrigatoria = validaSenhaServidorAvancado && parametroController.senhaServidorObrigatoriaReserva(rseCodigo, svcCodigo, csaCodigo, responsavel);
                final String classeProcReserva = paramSvcCse.getTpsClasseJavaProcEspecificoReserva();
                final boolean servidorDeveSerKYCComplaint = paramSvcCse.isTpsServidorDeveSerKYCComplaint();
                final boolean servicoTipoGAP = paramSvcCse.isTpsServicoTipoGap();
                final String servicoPagamentoViaBoleto = paramSvcCse.getTpsPermiteDescontoViaBoleto();

                if (CodedValues.FUN_COMP_CONTRATO.equals(permissaoCorrente)) {
                    final InformacaoSerCompraEnum exigeInfCompra = parametroController.senhaServidorObrigatoriaCompra(svcCodigo, rseCodigo, responsavel);
                    if (InformacaoSerCompraEnum.CONTA_BANCARIA.equals(exigeInfCompra)) {
                        serInfBancariaObrigatoria = true;
                        validarInfBancaria = true;
                    }
                }

                final String ppdCodigo = request.getParameter("ppd");
                if ((CodedValues.FUN_RENE_CONTRATO.equals(permissaoCorrente) || CodedValues.FUN_COMP_CONTRATO.equals(permissaoCorrente)) && ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)) {
                    // Verifica se o serviço do novo contrato "svcCodigo" é destino de um relacionamento de financiamento de dívida.
                    // Caso seja, uma proposta deve ser informada. Caso a proposta informada esteja aprovada, a senha não é obrigatória
                    final List<TransferObject> servicos = parametroController.getRelacionamentoSvc(CodedValues.TNT_FINANCIAMENTO_DIVIDA, null, svcCodigo, responsavel);
                    if ((servicos != null) && !servicos.isEmpty()) {
                        if (TextHelper.isNull(ppdCodigo)) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.proposta.pagamento.obrigatoria.operacao", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        } else if (financiamentoDividaController.propostaAprovada(ppdCodigo, responsavel)) {
                            serSenhaObrigatoria = false;
                        }
                    }
                }

                // Se é o servidor que está solicitando, deixa os parâmetros
                // listados abaixo com os valores default
                if (responsavel.isSer()) {
                    permiteCadVlrLiqLib = false;
                    permiteCadVlrMensVinc = false;
                    permiteVlrLiqTxJuros = false;
                    boolTpsSegPrestamista = false;
                    // serInfBancariaObrigatoria = false;
                    // validarInfBancaria = false;
                    validarDataNasc = false;
                }

                //se usuário gestor e com permissão de inclusão avançada, prevalesce a opção avançada
                if (responsavel.isCseSup() && usuPossuiIncAvancadaAde) {
                    validarDataNasc &= validaDataNascAvancado;
                }

                // Data e Hora da ocorrencia
                Timestamp adeDtHrOcorrencia = null;

                // Verifica se possui correção do valor presente.
                // Se possuir pega data do evento e salva na tupla de data e hora de ocorrência.
                final String dataEvento = JspHelper.verificaVarQryStr(request, "dataEvento");
                if (possuiCorrecaoVlrPresente && !TextHelper.isNull(dataEvento)) {
                    final String strDataEvento = DateHelper.reformat(dataEvento, LocaleHelper.getDatePattern(), "yyyy-MM-dd HH:mm:ss");
                    final java.text.SimpleDateFormat sdfDataEvento = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    final Date dtDataEvento = sdfDataEvento.parse(strDataEvento);
                    adeDtHrOcorrencia = new Timestamp(dtDataEvento.getTime());
                }

                // Informações Financeiras
                Object adeVlrTac = !"".equals(JspHelper.verificaVarQryStr(request, "adeVlrTac")) ? JspHelper.verificaVarQryStr(request, "adeVlrTac") : null;
                Object adeVlrIof = !"".equals(JspHelper.verificaVarQryStr(request, "adeVlrIof")) ? JspHelper.verificaVarQryStr(request, "adeVlrIof") : null;
                Object adeVlrLiquido = !"".equals(JspHelper.verificaVarQryStr(request, "adeVlrLiquido")) ? JspHelper.verificaVarQryStr(request, "adeVlrLiquido") : null;
                Object adeVlrMensVinc = !"".equals(JspHelper.verificaVarQryStr(request, "adeVlrMensVinc")) ? JspHelper.verificaVarQryStr(request, "adeVlrMensVinc") : null;
                final String adeIndice = !"".equals(JspHelper.verificaVarQryStr(request, "adeIndice")) ? JspHelper.verificaVarQryStr(request, "adeIndice") : null;
                Object adeVlrSegPrestamista = !"".equals(JspHelper.verificaVarQryStr(request, "adeVlrSegPrestamista")) ? JspHelper.verificaVarQryStr(request, "adeVlrSegPrestamista") : null;

                // Informações sobre a simulação
                BigDecimal vlrLiberado = null;
                Short ranking = null;
                String cftCodigo = null;
                String dtjCodigo = null;

                // Taxa de Juros
                Object adeTaxaJuros = !"".equals(JspHelper.verificaVarQryStr(request, "adeTaxaJuros")) ? JspHelper.verificaVarQryStr(request, "adeTaxaJuros") : null;

                final String numBanco = !"".equals(JspHelper.verificaVarQryStr(request, "numBanco")) ? JspHelper.verificaVarQryStr(request, "numBanco") : null;
                final String numAgencia = !"".equals(JspHelper.verificaVarQryStr(request, "numAgencia")) ? JspHelper.verificaVarQryStr(request, "numAgencia") : null;
                final String numConta = !"".equals(JspHelper.verificaVarQryStr(request, "numConta")) ? JspHelper.verificaVarQryStr(request, "numConta") : null;

                final String adeTipoVlr = paramSvcCse.getTpsTipoVlr();
                String adeIdentificador = JspHelper.verificaVarQryStr(request, "adeIdentificador");
                final String adePeriodicidade = JspHelper.verificaVarQryStr(request, "adePeriodicidade");

                // Dados da consignação
                final List<TransferObject> tdaList = autorizacaoController.lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.WEB, svcCodigo, csaCodigo, responsavel);
                final Map<String, String> dadosAutorizacao = new HashMap<>();
                for (final TransferObject tda : tdaList) {
                    final String tdaCodigo = (String) tda.getAttribute(Columns.TDA_CODIGO);
                    dadosAutorizacao.put(tdaCodigo, JspHelper.parseValor(request, null, "TDA_" + tdaCodigo, (String) tda.getAttribute(Columns.TDA_DOMINIO)));
                }

                // Busca o servidor
                CustomTransferObject servidor = null;
                try {
                    servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
                } catch (final ServidorControllerException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                final String codigoDependente = JspHelper.verificaVarQryStr(request, "codigoDependente");
                if(!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "reservaSaudeSemRegras"))) {
                    // Insere dados da consignação para o dependente
                    if(!TextHelper.isNull(codigoDependente)) {
                        dadosAutorizacao.put(CodedValues.TDA_BENEFICIARIO_DEPENTENTE, codigoDependente);
                    }

                    final String permiteDescontoViaBoleto = JspHelper.verificaVarQryStr(request, "permiteDescontoViaBoleto");

                    if(!TextHelper.isNull(servicoPagamentoViaBoleto) && CodedValues.PAGAMENTO_VIA_BOLETO_OBRIGATORIO.equals(servicoPagamentoViaBoleto) && TextHelper.isNull(permiteDescontoViaBoleto)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.reservar.margem.via.boleto.permitido", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }

                    if (!TextHelper.isNull(permiteDescontoViaBoleto) && "S".equals(permiteDescontoViaBoleto)) {
                        dadosAutorizacao.put(CodedValues.TDA_FORMA_PAGAMENTO, CodedValues.FORMA_PAGAMENTO_BOLETO);
                        // DESENV-18035: Necessário verificar quando o desconto é por poleto se a classificação do beneficiário permitie.
                        if(!TextHelper.isNull(codigoDependente)) {
                            final Beneficiario beneficiario = beneficiarioController.buscaBeneficiarioBfcCodigo(codigoDependente);
                            if(!TextHelper.isNull(beneficiario.getBfcClassificacao()) && CodedValues.BFC_CLASSIFICACAO_ESPECIAL.equals(beneficiario.getBfcClassificacao())) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.reservar.margem.dependente.via.boleto.nao.permitido", responsavel));
                                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                            }
                        }
                    } else {
                        dadosAutorizacao.put(CodedValues.TDA_FORMA_PAGAMENTO, CodedValues.FORMA_PAGAMENTO_FOLHA);
                    }
                }

                // DESENV-13592 : se o servidor não fez o KYC, e não for ele mesmo fazendo a reserva,
                // inclui marcação que o usuário está ciente disto se houver o parâmetro confirmando que ele viu a mensagem e está de acordo.
                if (servidorDeveSerKYCComplaint && !responsavel.isSer() && (CodedValues.FUN_RES_MARGEM.equals(permissaoCorrente) || CodedValues.FUN_SIM_CONSIGNACAO.equals(permissaoCorrente) || CodedValues.FUN_INCLUIR_CONSIGNACAO.equals(permissaoCorrente))) {
                    final KYCHelper kycHelper = new KYCHelper(servidor.getAttribute(Columns.SER_CODIGO).toString(), responsavel);
                    if (!kycHelper.validou() && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "cienteKYCNaoFinalizado"))) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }

                final String serDataNasc = servidor.getAttribute(Columns.SER_DATA_NASC) != null ? DateHelper.reformat(servidor.getAttribute(Columns.SER_DATA_NASC).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern()) : "";
                if (validarDataNasc) {
                    // Valida a data de nascimento do servidor de acordo com a data informada pelo usuário
                    final String paramDataNasc = JspHelper.verificaVarQryStr(request, "dataNasc");
                    if (!paramDataNasc.equals(serDataNasc)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.dataNascNaoConfere", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }

                final String rseBancoSal = servidor.getAttribute(Columns.RSE_BANCO_SAL) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_BANCO_SAL).toString(), "0", JspHelper.ESQ) : "";
                final String rseAgenciaSal = servidor.getAttribute(Columns.RSE_AGENCIA_SAL) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_AGENCIA_SAL).toString(), "0", JspHelper.ESQ) : "";
                final String rseContaSal = servidor.getAttribute(Columns.RSE_CONTA_SAL) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_CONTA_SAL).toString(), "0", JspHelper.ESQ) : "";
                final String rseBancoSalAlt = servidor.getAttribute(Columns.RSE_BANCO_SAL_2) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_BANCO_SAL_2).toString(), "0", JspHelper.ESQ) : "";
                final String rseAgenciaSalAlt = servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2).toString(), "0", JspHelper.ESQ) : "";
                final String rseContaSalAlt = servidor.getAttribute(Columns.RSE_CONTA_SAL_2) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_CONTA_SAL_2).toString(), "0", JspHelper.ESQ) : "";
                if (!responsavel.isSer() && serInfBancariaObrigatoria && validarInfBancaria) {
                    // Se as informações bancárias são obrigatórias e devem ser válidas,
                    // então valida as informações digitadas pelo usuário
                    if ((!TextHelper.formataParaComparacao(rseBancoSal).equals(TextHelper.formataParaComparacao(numBanco)) || !TextHelper.formataParaComparacao(rseAgenciaSal).equals(TextHelper.formataParaComparacao(numAgencia)) || !TextHelper.formataParaComparacao(rseContaSal).equals(TextHelper.formataParaComparacao(numConta))) && // somente se a duas não forem iguais
                            (!TextHelper.formataParaComparacao(rseBancoSalAlt).equals(TextHelper.formataParaComparacao(numBanco)) || !TextHelper.formataParaComparacao(rseAgenciaSalAlt).equals(TextHelper.formataParaComparacao(numAgencia)) || !TextHelper.formataParaComparacao(rseContaSalAlt).equals(TextHelper.formataParaComparacao(numConta)))) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informacaoBancariaIncorreta", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }

                // Se o serviço possui processamento específico de reserva,
                // cria a classe de execução
                ProcessaReservaMargem processador = null;
                if (classeProcReserva != null) {
                    try {
                        processador = ProcessaReservaMargemFactory.getProcessador(classeProcReserva);
                    } catch (final ViewHelperException ex) {
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    }
                }
                // Executa validação do passo 2
                if (processador != null) {
                    try {
                        processador.validarPasso2(request);
                    } catch (final ViewHelperException ex) {
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }

                try {
                    if (!TextHelper.isNull(adeValor)) {
                        adeValor = NumberHelper.reformat(adeValor.toString(), NumberHelper.getLang(), "en");
                        adeValor = new BigDecimal(adeValor.toString());

                        if (possuiCorrecaoVlrPresente) {
                            final String adeVlrCorrigido = JspHelper.verificaVarQryStr(request, "adeVlrCorrigido");
                            if (!"".equals(adeVlrCorrigido)) {
                                // Se possui correção de valor presente e a correção deve ser na mesma ADE,
                                // então seta o valor da consignação igual ao valor corrigido
                                if (!corrigeSaldoDevedorOutroServico) {
                                    adeValor = new BigDecimal(NumberHelper.reformat(adeVlrCorrigido, NumberHelper.getLang(), "en"));
                                } else {
                                    adeVlrCorrecao = new BigDecimal(NumberHelper.reformat(adeVlrCorrigido, NumberHelper.getLang(), "en")).subtract((BigDecimal) adeValor);
                                }
                            } else {
                                possuiCorrecaoVlrPresente = false;
                            }
                        }
                    }

                    if (!TextHelper.isNull(request.getParameter("adeCarencia"))) {
                        adeCarencia = Integer.valueOf(request.getParameter("adeCarencia"));
                    }
                    adeIncMargem = paramSvcCse.getTpsIncideMargem();
                    adeIntFolha = paramSvcCse.getTpsIntegraFolha();

                    if (!"".equals(JspHelper.verificaVarQryStr(request, "adePrazo"))) {
                        adePrazo = Integer.valueOf(JspHelper.verificaVarQryStr(request, "adePrazo"));
                    }
                    if ((adeVlrTac != null) && !"".equals(adeVlrTac.toString())) {
                        adeVlrTac = NumberHelper.reformat(adeVlrTac.toString(), NumberHelper.getLang(), "en");
                        adeVlrTac = new BigDecimal(adeVlrTac.toString());
                    }
                    if ((adeVlrIof != null) && !"".equals(adeVlrIof.toString())) {
                        adeVlrIof = NumberHelper.reformat(adeVlrIof.toString(), NumberHelper.getLang(), "en");
                        adeVlrIof = new BigDecimal(adeVlrIof.toString());
                    }
                    if (permiteCadVlrMensVinc && (adeVlrMensVinc != null)) {
                        adeVlrMensVinc = NumberHelper.reformat(adeVlrMensVinc.toString(), NumberHelper.getLang(), "en");
                        adeVlrMensVinc = new BigDecimal(adeVlrMensVinc.toString());
                    }
                    if (permiteCadVlrLiqLib && (adeVlrLiquido != null)) {
                        adeVlrLiquido = NumberHelper.reformat(adeVlrLiquido.toString(), NumberHelper.getLang(), "en");
                        adeVlrLiquido = new BigDecimal(adeVlrLiquido.toString());
                    }
                    if (boolTpsSegPrestamista && (adeVlrSegPrestamista != null)) {
                        adeVlrSegPrestamista = NumberHelper.reformat(adeVlrSegPrestamista.toString(), NumberHelper.getLang(), "en");
                        adeVlrSegPrestamista = new BigDecimal(adeVlrSegPrestamista.toString());
                    }
                    if (permiteVlrLiqTxJuros && (adeTaxaJuros != null)) {
                        adeTaxaJuros = NumberHelper.reformat(adeTaxaJuros.toString(), NumberHelper.getLang(), "en");
                        adeTaxaJuros = new BigDecimal(adeTaxaJuros.toString());
                    } else {
                        adeTaxaJuros = buscarTaxaCadastrada(adePrazo, responsavel, request);
                    }

                    // Se existe simulação de consignação, obtém do request os dados provenientes da simulação, caso existam
                    if (temSimulacaoConsignacao && (adePrazo != null) && (adePrazo > 0)) {
                        if (!"".equals(JspHelper.verificaVarQryStr(request, "vlrLiberado"))) {
                            vlrLiberado = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "vlrLiberado"), NumberHelper.getLang(), "en"));
                        }
                        if (!"".equals(JspHelper.verificaVarQryStr(request, "ranking"))) {
                            ranking = Short.valueOf(JspHelper.verificaVarQryStr(request, "ranking"));
                        }
                        if (!"".equals(JspHelper.verificaVarQryStr(request, "CFT_CODIGO"))) {
                            cftCodigo = JspHelper.verificaVarQryStr(request, "CFT_CODIGO");
                        }
                        if (!"".equals(JspHelper.verificaVarQryStr(request, "DTJ_CODIGO"))) {
                            dtjCodigo = JspHelper.verificaVarQryStr(request, "DTJ_CODIGO");
                        }
                    }
                    if ((CodedValues.FUN_SIMULAR_RENEGOCIACAO.equals(permissaoCorrente) || tpcSolicitarPortabilidadeRanking) && (adeVlrLiquido == null)) {
                        adeVlrLiquido = vlrLiberado;
                    }
                } catch (ParseException | NumberFormatException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                String sadCodigo = null;
                boolean comSerSenha = false;
                String senhaAberta = null;

                if (!responsavel.isSer() || (responsavel.isSer() && ParamSist.getBoolParamSist(CodedValues.TPC_SOLICITA_SENHA_AUTORIZACAO_OPE_REALIZADAS_SER, responsavel))) {
                    // Se valida senha do servidor, recupera e seta na sessão para ser validada posteriormente
                    final String senha = JspHelper.verificaVarQryStr(request, JspHelper.verificaVarQryStr(request, "cryptedPasswordFieldName"));
                    final String serLogin = JspHelper.verificaVarQryStr(request, "serLogin");
                    if (!TextHelper.isNull(senha)) {
                        session.setAttribute("serAutorizacao", senha);
                    }
                    if (!TextHelper.isNull(serLogin)) {
                        session.setAttribute("serLogin", serLogin);
                    }

                    try {
                        SenhaHelper.validarSenha(request, rseCodigo, svcCodigo, serSenhaObrigatoria, true, responsavel);
                        senhaAberta = (String) request.getAttribute("senhaServidorOK");
                        comSerSenha = !TextHelper.isNull(senhaAberta);
                    } catch (final ViewHelperException ex) {
                        // Paraná: ao receber 'senha expirada' a CSA poderá ativar a senha.
                        if (ex.getMessageKey().indexOf("mensagem.erro.senha.expirada.certifique.ativacao") != -1) {
                            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.expirada.ativar", responsavel));
                            // Redireciona para JSP específico de ativação de senha eConsig PR
                            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL("../v3/ativarSenhaServidor?acao=iniciar", request)));
                            return "jsp/redirecionador/redirecionar";
                        } else {
                            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
                    }

                } else {
                    // Se é usuário servidor
                    comSerSenha = true;
                    // Status de solicitação
                    sadCodigo = CodedValues.SAD_SOLICITADO;
                    if (deferimentoAutoSolicSer) {
                        sadCodigo = CodedValues.SAD_DEFERIDA;
                    }

                    // Identificador da solicitação
                    adeIdentificador = ApplicationResourcesHelper.getMessage("rotulo.consignacao.identificador.solicitacao", responsavel);

                    // Atualiza os dados do servidor
                    final String serCodigo = responsavel.getCodigoEntidade();
                    final ServidorTransferObject servidorUpd = new ServidorTransferObject(serCodigo);
                    final java.sql.Date dataNascimento = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_DATA_NASC")) ? DateHelper.toSQLDate(DateHelper.parse(JspHelper.verificaVarQryStr(request, "SER_DATA_NASC"), LocaleHelper.getDatePattern())) : null;
                    final java.sql.Date dataEmissaoIdt = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_DATA_IDT")) ? DateHelper.toSQLDate(DateHelper.parse(JspHelper.verificaVarQryStr(request, "SER_DATA_IDT"), LocaleHelper.getDatePattern())) : null;

                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel)) {
                        servidorUpd.setSerEnd(JspHelper.verificaVarQryStr(request, "SER_END"));
                    }
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel)) {
                        servidorUpd.setSerCompl(JspHelper.verificaVarQryStr(request, "SER_COMPL"));
                    }
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel)) {
                        servidorUpd.setSerBairro(JspHelper.verificaVarQryStr(request, "SER_BAIRRO"));
                    }
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel)) {
                        servidorUpd.setSerCidade(JspHelper.verificaVarQryStr(request, "SER_CIDADE"));
                    }
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel)) {
                        servidorUpd.setSerUf(JspHelper.verificaVarQryStr(request, "SER_UF"));
                    }
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel)) {
                        servidorUpd.setSerCep(JspHelper.verificaVarQryStr(request, "SER_CEP"));
                    }
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_NASCIMENTO, responsavel)) {
                        servidorUpd.setSerDataNasc(dataNascimento);
                    }
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_SEXO, responsavel)) {
                        servidorUpd.setSerSexo(JspHelper.verificaVarQryStr(request, "SER_SEXO"));
                    }
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO_IDT, responsavel)) {
                        servidorUpd.setSerNroIdt(JspHelper.verificaVarQryStr(request, "SER_NRO_IDT"));
                    }
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_IDT, responsavel)) {
                        servidorUpd.setSerDataIdt(dataEmissaoIdt);
                    }
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel)) {
                        servidorUpd.setSerCelular(JspHelper.verificaVarQryStr(request, "SER_CEL"));
                    }
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NACIONALIDADE, responsavel)) {
                        servidorUpd.setSerNacionalidade(JspHelper.verificaVarQryStr(request, "SER_NACIONALIDADE"));
                    }
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NATURALIDADE, responsavel)) {
                        servidorUpd.setSerCidNasc(JspHelper.verificaVarQryStr(request, "SER_NATURALIDADE"));
                    }
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF_NASCIMENTO, responsavel)) {
                        servidorUpd.setSerUfNasc(JspHelper.verificaVarQryStr(request, "SER_UF_NASCIMENTO"));
                    }
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel)) {
                        servidorUpd.setSerTel(JspHelper.verificaVarQryStr(request, "SER_TEL"));
                    }
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel)) {
                        servidorUpd.setSerNro(JspHelper.verificaVarQryStr(request, "SER_NRO"));
                    } else {
                        servidorUpd.setSerNro(null);
                    }
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_EMAIL, responsavel)) {
                        servidorUpd.setSerEmail(JspHelper.verificaVarQryStr(request, "SER_EMAIL"));
                    }
                    servidorController.updateServidor(servidorUpd, responsavel);

                    // registro servidor
                    final RegistroServidorTO registroServidorUpd = new RegistroServidorTO(rseCodigo);
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_IBAN, responsavel)) {
                        registroServidorUpd.setRseAgenciaSalAlternativa(JspHelper.verificaVarQryStr(request, "SER_IBAN"));
                    }
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_SALARIO, responsavel)) {
                        registroServidorUpd.setRseSalario(new BigDecimal(!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_SALARIO")) ? NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "SER_SALARIO").toString(), NumberHelper.getLang(), "en") : "0.00"));
                    }
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_ADMISSAO, responsavel)) {
                        final java.sql.Date dataAdmissaoSql = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_DATA_ADMISSAO")) ? DateHelper.toSQLDate(DateHelper.parse(JspHelper.verificaVarQryStr(request, "SER_DATA_ADMISSAO"), LocaleHelper.getDatePattern())) : null;
                        Timestamp dataAdmissao = null;
                        if (dataAdmissaoSql != null) {
                            dataAdmissao = new Timestamp(dataAdmissaoSql.getTime());
                            registroServidorUpd.setRseDataAdmissao(dataAdmissao);
                        }
                    }
                    if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_MUNICIPIO_LOTACAO, responsavel)) {
                        registroServidorUpd.setRseMunicipioLotacao(JspHelper.verificaVarQryStr(request, "RSE_MUNICIPIO_LOTACAO"));
                    }
                    if (registroServidorUpd != null) {
                        servidorController.updateRegistroServidor(registroServidorUpd, false, false, false, responsavel);
                    }
                }

                if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_DESCONTO_EM_FILA, CodedValues.TPC_SIM, responsavel) && (!TextHelper.isNull(paramSvcCse.getTpsBaseCalcDescontoEmFila()) && !TextHelper.isNull(paramSvcCse.getTpsPercentualBaseCalcDescontoEmFila()))) {
                    // Se o sistema permite módulo de desconto em fila e o serviço está configurado para realizar a fila
                    // define a não incidência de margem, não integração com a folha, prazo fixo igual a 1 e status Aguard. Margem
                    sadCodigo = CodedValues.SAD_AGUARD_MARGEM;
                    adeIncMargem = CodedValues.INCIDE_MARGEM_NAO;
                    adeIntFolha = CodedValues.INTEGRA_FOLHA_NAO;
                    adePrazo = 1;
                }

                // Se for uma reserva do exército conforme DESENV-13535, o status da aut desconto deve ser deferida
                if (CodedValues.FUN_INCLUIR_TERMO_GARANTIA_ALUGUEL.equals(permissaoCorrente)) {
                    sadCodigo = CodedValues.SAD_DEFERIDA;
                }

                // verifica anexo obrigatorio
                final String nomeAnexo = JspHelper.verificaVarQryStr(request, "FILE1");
                final String idAnexo = session.getId();
                final String aadDescricao = JspHelper.verificaVarQryStr(request, "AAD_DESCRICAO");

                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_ANEXO_INCLUSAO_CONTRATOS, CodedValues.TPC_SIM, responsavel) && responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO) && parametroController.isObrigatorioAnexoInclusao(svcCodigo, responsavel) && TextHelper.isNull(nomeAnexo)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.anexo.obrigatorio.svc", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                String adeCodigoNovo = null;
                java.sql.Date ocaPeriodoDate = null;

                if (servicoTipoGAP && CodedValues.FUN_RES_MARGEM.equals(permissaoCorrente)) {
                    final List<Short> marCodigos = new ArrayList<>();
                    for (final String element : incMargemGap) {
                        marCodigos.add(Short.valueOf(element));
                    }

                    final List<String> adeCodigos = reservarMargemController.reservarMargemGap(rseCodigo, orgCodigo, cnvCodigo, svcCodigo, corCodigo, marCodigos, adeIdentificador, senhaAberta, comSerSenha, numAgencia, numBanco, numConta, responsavel);

                    if ((adeCodigos != null) && !adeCodigos.isEmpty()) {
                        adeCodigoNovo = adeCodigos.get(0);

                        if (adeCodigos.size() > 1) {
                            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.gap.prestacoes.selecionadas.inseridas.sucesso", responsavel));
                        }
                    }

                } else if (CodedValues.FUN_INCLUIR_CONSIGNACAO.equals(permissaoCorrente) || CodedValues.FUN_RES_MARGEM.equals(permissaoCorrente) || CodedValues.FUN_INC_DESPESA_INDIVIDUAL.equals(permissaoCorrente) || CodedValues.FUN_INCLUIR_TERMO_GARANTIA_ALUGUEL.equals(permissaoCorrente) || inclusaoJudicial) {
                    //Valida o código de autorização enviado por SMS ao Servidor.
                    final boolean exigeCodAutorizacaoSMS = ParamSist.paramEquals(CodedValues.TPC_ENVIA_SMS_CODIGO_UNICO_AUTORIZACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel);
                    if (responsavel.isSer() && exigeCodAutorizacaoSMS) {
                        final String codAut = JspHelper.verificaVarQryStr(request, "codAutorizacao");
                        usuarioController.validarCodigoAutorizacaoSms(codAut, responsavel);
                    }

                    // Cria objeto de parâmetro da reserva de margem
                    final ReservarMargemParametros rmParam = new ReservarMargemParametros();

                    rmParam.setRseCodigo(rseCodigo);
                    rmParam.setAdeVlr((BigDecimal) adeValor);
                    rmParam.setAdePrazo(adePrazo);
                    rmParam.setAdeCarencia(adeCarencia);
                    rmParam.setAdeIdentificador(adeIdentificador);
                    rmParam.setCnvCodigo(cnvCodigo);
                    rmParam.setSadCodigo(sadCodigo);
                    rmParam.setCorCodigo(corCodigo);
                    rmParam.setSerSenha(senhaAberta);
                    rmParam.setComSerSenha(comSerSenha);
                    rmParam.setAdeTipoVlr(adeTipoVlr);
                    rmParam.setAdeIntFolha(adeIntFolha);
                    rmParam.setAdeIncMargem(adeIncMargem);
                    rmParam.setAdeIndice(adeIndice);
                    rmParam.setAdeVlrTac((BigDecimal) adeVlrTac);
                    rmParam.setAdeVlrIof((BigDecimal) adeVlrIof);
                    rmParam.setAdeVlrLiquido((BigDecimal) adeVlrLiquido);
                    rmParam.setAdeVlrMensVinc((BigDecimal) adeVlrMensVinc);
                    rmParam.setAdeTaxaJuros((BigDecimal) adeTaxaJuros);
                    rmParam.setAdeVlrSegPrestamista((BigDecimal) adeVlrSegPrestamista);
                    rmParam.setAdeDtHrOcorrencia(adeDtHrOcorrencia);
                    rmParam.setValidar(Boolean.FALSE);
                    rmParam.setPermitirValidacaoTaxa(Boolean.TRUE);
                    rmParam.setSerAtivo(Boolean.TRUE);
                    rmParam.setCnvAtivo(Boolean.TRUE);
                    rmParam.setSerCnvAtivo(Boolean.TRUE);
                    rmParam.setSvcAtivo(Boolean.TRUE);
                    rmParam.setCsaAtivo(Boolean.TRUE);
                    rmParam.setOrgAtivo(Boolean.TRUE);
                    rmParam.setEstAtivo(Boolean.TRUE);
                    rmParam.setCseAtivo(Boolean.TRUE);
                    rmParam.setAcao("RESERVAR");
                    rmParam.setCftCodigo(cftCodigo);
                    rmParam.setDtjCodigo(dtjCodigo);
                    rmParam.setCdeVlrLiberado(vlrLiberado);
                    rmParam.setCdeRanking(ranking);
                    rmParam.setCdeTxtContato("");
                    rmParam.setAdeBanco(numBanco);
                    rmParam.setAdeAgencia(numAgencia);
                    rmParam.setAdeConta(numConta);
                    rmParam.setNomeResponsavel(responsavel.getUsuNome());
                    rmParam.setAdePeriodicidade(adePeriodicidade);
                    rmParam.setNomeAnexo(nomeAnexo);
                    rmParam.setIdAnexo(idAnexo);
                    rmParam.setAadDescricao(aadDescricao);

                    // Seta os dados genéricos que o responsável tem permissão de alterar
                    rmParam.setDadosAutorizacaoMap(dadosAutorizacao);

                    final String ocaPeriodo = JspHelper.verificaVarQryStr(request, "ocaPeriodo");
                    if (!TextHelper.isNull(ocaPeriodo)) {
                        ocaPeriodoDate = DateHelper.toSQLDate(DateHelper.parse(ocaPeriodo, "yyyy-MM-dd"));
                        rmParam.setOcaPeriodo(ocaPeriodo);
                    }

                    // dados de autorização se aplicam apenas a reservas que não são solicitações de servidor
                    if (responsavel.isCsaCor()) {
                        rmParam.setTdaModalidadeOperacao(tdaModalidadeOp);
                        rmParam.setTdaMatriculaSerCsa(tdaMatriculaCsa);
                    }

                    // Verifica se exige cadastro de telefone na solicitação
                    final boolean exigeTelefone = CodedValues.SAD_SOLICITADO.equals(sadCodigo) && ParamSist.paramEquals(CodedValues.TPC_REQUER_TEL_SER_SOLIC_EMPRESTIMO, CodedValues.TPC_SIM, responsavel);
                    if (exigeTelefone) {
                        if (TextHelper.isNull(JspHelper.verificaVarQryStr(request, "TDA_25"))) {
                            throw new AutorizacaoControllerException("mensagem.informe.servidor.telefone.solicitacao", responsavel);
                        }
                        final String tdaTelSolicitacaoSer = JspHelper.verificaVarQryStr(request, "TDA_25");
                        rmParam.setTdaTelSolicitacaoSer(tdaTelSolicitacaoSer);
                        if (rmParam.existsDadoAutorizacao(CodedValues.TDA_SOLICITACAO_TEL_SERVIDOR) && TextHelper.isNull(rmParam.getDadoAutorizacao(CodedValues.TDA_SOLICITACAO_TEL_SERVIDOR))) {
                            rmParam.setDadoAutorizacao(CodedValues.TDA_SOLICITACAO_TEL_SERVIDOR, tdaTelSolicitacaoSer);
                        }

                    }

                    // verifica se há parâmetros de inclusão avançada
                    if (usuPossuiIncAvancadaAde || inclusaoJudicial) {
                        rmParam.setValidaMargem(validaMargemAvancado);
                        rmParam.setValidaTaxaJuros(validaTaxaAvancado);
                        rmParam.setValidaPrazo(validaPrazoAvancado);
                        rmParam.setValidaDadosBancarios(validaDadosBancariosAvancado);
                        rmParam.setValidaSenhaServidor(validaSenhaServidorAvancado);
                        rmParam.setValidaBloqSerCnvCsa(validaBloqSerCnvCsaAvancado);
                        rmParam.setValidaDataNascimento(validaDataNascAvancado);
                        rmParam.setValidaLimiteAde(validaLimiteAdeAvancado);

                        final String tmoCodigo = JspHelper.verificaVarQryStr(request, "tmoCodigo");
                        if (!TextHelper.isNull(tmoCodigo)) {
                            rmParam.setTmoCodigo(tmoCodigo);
                        }

                        final String ocaObs = JspHelper.verificaVarQryStr(request, "adeObs");
                        if (!TextHelper.isNull(ocaObs)) {
                            rmParam.setOcaObs(ocaObs);
                        }

                        if (inclusaoJudicial) {
                            rmParam.setInclusaoJucicial(inclusaoJudicial);
                        }

                        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) {
                            // Dados de decisão judicial
                            final String tjuCodigo = JspHelper.verificaVarQryStr(request, "tjuCodigo");
                            final String cidCodigo = JspHelper.verificaVarQryStr(request, "cidCodigo");
                            final String djuNumProcesso = JspHelper.verificaVarQryStr(request, "djuNumProcesso");
                            final String djuData = JspHelper.verificaVarQryStr(request, "djuData");
                            final String djuTexto = JspHelper.verificaVarQryStr(request, "djuTexto");

                            if (!TextHelper.isNull(tjuCodigo) && !TextHelper.isNull(djuTexto) && !TextHelper.isNull(djuData)) {
                                // Se informado, pelo menos tipo de justiça, texto e data devem ser informados. Os demais são opcionais.
                                rmParam.setTjuCodigo(tjuCodigo);
                                rmParam.setCidCodigo(cidCodigo);
                                rmParam.setDjuNumProcesso(djuNumProcesso);
                                rmParam.setDjuData(DateHelper.parse(djuData, LocaleHelper.getDatePattern()));
                                rmParam.setDjuTexto(djuTexto);
                            } else if ((!TextHelper.isNull(tjuCodigo) && TextHelper.isNull(djuTexto)) || (!TextHelper.isNull(tjuCodigo) && TextHelper.isNull(djuData)) || (!TextHelper.isNull(djuData) && TextHelper.isNull(djuTexto)) || (!TextHelper.isNull(djuData) && TextHelper.isNull(tjuCodigo)) || (!TextHelper.isNull(djuTexto) && TextHelper.isNull(djuData)) || (!TextHelper.isNull(djuTexto) && TextHelper.isNull(tjuCodigo))) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.inclusao.avancada.decisao.judicial.dados.minimos", responsavel));
                                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                            }
                        }
                    }

                    if (possuiCorrecaoVlrPresente) {
                        final Object adeVlrOriginal = JspHelper.verificaVarQryStr(request, "adeVlr");

                        rmParam.setAdeVlrOriginal(new BigDecimal(NumberHelper.reformat(adeVlrOriginal.toString(), NumberHelper.getLang(), "en")));
                        rmParam.setAdeVlrCorrecao(adeVlrCorrecao);
                    }

                    rmParam.setTelaConfirmacaoDuplicidade("S".equals(request.getParameter("telaConfirmacaoDuplicidade")));
                    rmParam.setChkConfirmarDuplicidade(!TextHelper.isNull(request.getParameter("chkConfirmarDuplicidade")));
                    rmParam.setMotivoOperacaoCodigoDuplicidade(request.getParameter("TMO_CODIGO"));
                    rmParam.setMotivoOperacaoObsDuplicidade(request.getParameter("ADE_OBS"));

                    adeCodigoNovo = incluirReservaMargem(rmParam, request, session, responsavel);

                    // Invalidar o token enviado via SMS
                    if (responsavel.isSer() && exigeCodAutorizacaoSMS) {
                        final TransferObject usuario = new CustomTransferObject();
                        usuario.setAttribute(Columns.USU_CODIGO, responsavel.getUsuCodigo());
                        usuarioController.limparDadosOTP(usuario, responsavel);
                    }

                } else if (CodedValues.FUN_RENE_CONTRATO.equals(permissaoCorrente) || CodedValues.FUN_COMP_CONTRATO.equals(permissaoCorrente) || CodedValues.FUN_SIMULAR_RENEGOCIACAO.equals(permissaoCorrente) || CodedValues.FUN_SOLICITAR_PORTABILIDADE.equals(permissaoCorrente)) {
                    List<String> adeCodigos = null;
                    final List<String> adeCodigosAntigos = new ArrayList<>();
                    if (!"".equals(JspHelper.verificaVarQryStr(request, "chkADE"))) {
                        adeCodigos = Arrays.asList(JspHelper.verificaVarQryStr(request, "chkADE").split(","));
                    } else if (tpcSolicitarPortabilidadeRanking && (request.getParameter("ADE_CODIGO_PORTABILIDADE") != null)) {
                        adeCodigos = new ArrayList<>();
                        adeCodigos.add(request.getParameter("ADE_CODIGO_PORTABILIDADE"));
                    }

                    if ((adeCodigos == null) || adeCodigos.isEmpty()) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                    adeCodigosAntigos.addAll(adeCodigos);

                    try {
                        final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();
                        renegociarParam.setTipo(responsavel.getTipoEntidade());
                        renegociarParam.setRseCodigo(rseCodigo);
                        renegociarParam.setAdeVlr((BigDecimal) adeValor);
                        renegociarParam.setCorCodigo(corCodigo);
                        renegociarParam.setAdePrazo(adePrazo);
                        renegociarParam.setAdeCarencia(adeCarencia);
                        renegociarParam.setAdeIdentificador(adeIdentificador);
                        renegociarParam.setCnvCodigo(cnvCodigo);
                        renegociarParam.setSerSenha(senhaAberta);
                        renegociarParam.setComSerSenha(comSerSenha);
                        renegociarParam.setAdeIndice(adeIndice);
                        renegociarParam.setAdeVlrTac((BigDecimal) adeVlrTac);
                        renegociarParam.setAdeVlrIof((BigDecimal) adeVlrIof);
                        renegociarParam.setAdeVlrLiquido((BigDecimal) adeVlrLiquido);
                        renegociarParam.setAdeVlrMensVinc((BigDecimal) adeVlrMensVinc);
                        renegociarParam.setAdeTaxaJuros((BigDecimal) adeTaxaJuros);
                        renegociarParam.setAdeVlrSegPrestamista((BigDecimal) adeVlrSegPrestamista);
                        renegociarParam.setAdeDtHrOcorrencia(adeDtHrOcorrencia);
                        renegociarParam.setAdeCodigosRenegociacao(adeCodigos);
                        renegociarParam.setCftCodigo(cftCodigo);
                        renegociarParam.setDtjCodigo(dtjCodigo);
                        renegociarParam.setCdeVlrLiberado(vlrLiberado);
                        renegociarParam.setCdeRanking(ranking);
                        renegociarParam.setCdeTxtContato("");
                        renegociarParam.setAdeBanco(numBanco);
                        renegociarParam.setAdeAgencia(numAgencia);
                        renegociarParam.setAdeConta(numConta);
                        renegociarParam.setPpdCodigo(ppdCodigo);
                        renegociarParam.setAdePeriodicidade(adePeriodicidade);
                        renegociarParam.setNomeAnexo(nomeAnexo);
                        renegociarParam.setIdAnexo(idAnexo);
                        renegociarParam.setAadDescricao(aadDescricao);

                        // Seta os dados genéricos que o responsável tem permissão de alterar
                        renegociarParam.setDadosAutorizacaoMap(dadosAutorizacao);

                        // Se operação de renegociação e a CSA tem corte diferente do sistema ela pode escolher em alterar a data de encerramento do contrato antigo
                        renegociarParam.setAlterarDataEncerramento(CodedValues.FUN_RENE_CONTRATO.equals(permissaoCorrente) && "S".equals(request.getParameter("alterarDataEncerramento")));

                        // dados de autorização se aplicam apenas a reservas que não são solicitações de servidor
                        if (responsavel.isCsaCor()) {
                            renegociarParam.setTdaModalidadeOperacao(tdaModalidadeOp);
                            renegociarParam.setTdaMatriculaSerCsa(tdaMatriculaCsa);
                        }

                        if (CodedValues.FUN_COMP_CONTRATO.equals(permissaoCorrente)) {
                            if (ParamSist.paramEquals(CodedValues.TPC_INFORMA_NUM_PORTABILIDADE_CIP_COMPRA, CodedValues.NUM_PORTABILIDADE_CIP_COMPRA_OPCIONAL, responsavel) || ParamSist.paramEquals(CodedValues.TPC_INFORMA_NUM_PORTABILIDADE_CIP_COMPRA, CodedValues.NUM_PORTABILIDADE_CIP_COMPRA_OBRIGATORIO, responsavel)) {
                                final String numCipCompra = JspHelper.verificaVarQryStr(request, "numCipCompra");
                                renegociarParam.setNumCipCompra(numCipCompra);
                            }

                            if (ParamSist.paramEquals(CodedValues.TPC_INFORMA_ANEXO_ADE_DOC_ADICIONAL_COMPRA, CodedValues.ANEXO_ADE_DOC_ADICIONAL_COMPRA_OPCIONAL, responsavel) || ParamSist.paramEquals(CodedValues.TPC_INFORMA_ANEXO_ADE_DOC_ADICIONAL_COMPRA, CodedValues.ANEXO_ADE_DOC_ADICIONAL_COMPRA_OBRIGATORIO, responsavel)) {
                                final String anexoDocAdicionalCompra = JspHelper.verificaVarQryStr(request, "FILE_DOC_ADICIONAL_COMPRA");

                                if (!TextHelper.isNull(anexoDocAdicionalCompra) && !TextHelper.isNull(nomeAnexo) && anexoDocAdicionalCompra.equals(nomeAnexo)) {
                                    // Se os dois anexos tem o mesmo nome, reporta erro para o usuário escolher arquivos diferentes
                                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.consignacao.anexo.diferentes", responsavel));
                                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                                }

                                renegociarParam.setAnexoDocAdicionalCompra(anexoDocAdicionalCompra);
                            }
                        }

                        renegociarParam.setTelaConfirmacaoDuplicidade("S".equals(request.getParameter("telaConfirmacaoDuplicidade")));
                        renegociarParam.setChkConfirmarDuplicidade(!TextHelper.isNull(request.getParameter("chkConfirmarDuplicidade")));
                        renegociarParam.setMotivoOperacaoCodigoDuplicidade(request.getParameter("TMO_CODIGO"));
                        renegociarParam.setMotivoOperacaoObsDuplicidade(request.getParameter("ADE_OBS"));

                        if (CodedValues.FUN_RENE_CONTRATO.equals(permissaoCorrente) || CodedValues.FUN_SIMULAR_RENEGOCIACAO.equals(permissaoCorrente)) {
                            renegociarParam.setCompraContrato(Boolean.FALSE);
                            adeCodigoNovo = renegociarConsignacaoController.renegociar(renegociarParam, responsavel);
                        } else {
                            renegociarParam.setCompraContrato(Boolean.TRUE);
                            renegociarParam.setSerEmail(JspHelper.verificaVarQryStr(request, "serEmail"));
                            adeCodigoNovo = renegociarConsignacaoController.renegociar(renegociarParam, responsavel);
                        }

                        // Remove chave de senha da sessão
                        session.removeAttribute("senhaServidorRenegOK");
                    } catch (final AutorizacaoControllerException mae) {
                        final String messageKey = mae.getMessageKey();
                        if ("mensagem.erro.ade.duplicidade.bloqueada.ate.data.limite".equals(messageKey)) {
                            return verificarPossibilidadePermitirDuplicidade(request, session, model, responsavel, cnvCodigo, "incluirReserva", mae);
                        } else {
                            session.setAttribute(CodedValues.MSG_ERRO, mae.getMessage());
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
                    }
                } else if (CodedValues.FUN_ALONGAR_CONTRATO.equals(permissaoCorrente)) {
                    final String adeCodigo = JspHelper.verificaVarQryStr(request, "ADE_CODIGO");
                    if ("".equals(adeCodigo)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                    final List<String> adeCodigosAntigos = new ArrayList<>();
                    adeCodigosAntigos.add(adeCodigo);

                    try {
                        final AlongarConsignacaoParametros alongarParam = new AlongarConsignacaoParametros();

                        alongarParam.setAdeCodigo(adeCodigo);
                        alongarParam.setRseCodigo(rseCodigo);
                        alongarParam.setAdeVlr((BigDecimal) adeValor);
                        alongarParam.setCorCodigo(corCodigo);
                        alongarParam.setAdePrazo(adePrazo);
                        alongarParam.setAdeCarencia(adeCarencia);
                        alongarParam.setAdeIdentificador(adeIdentificador);
                        alongarParam.setCnvCodigo(cnvCodigo);
                        alongarParam.setSerSenha(senhaAberta);
                        alongarParam.setComSerSenha(comSerSenha);
                        alongarParam.setAdeIndice(adeIndice);
                        alongarParam.setAdeVlrTac((BigDecimal) adeVlrTac);
                        alongarParam.setAdeVlrIof((BigDecimal) adeVlrIof);
                        alongarParam.setAdeVlrLiquido((BigDecimal) adeVlrLiquido);
                        alongarParam.setAdeVlrMensVinc((BigDecimal) adeVlrMensVinc);
                        alongarParam.setAdeTaxaJuros((BigDecimal) adeTaxaJuros);
                        alongarParam.setCftCodigo(cftCodigo);
                        alongarParam.setCdeVlrLiberado(vlrLiberado);
                        alongarParam.setCdeRanking(ranking);
                        alongarParam.setCdeTxtContato("");
                        alongarParam.setAdeBanco(numBanco);
                        alongarParam.setAdeAgencia(numAgencia);
                        alongarParam.setAdeConta(numConta);
                        alongarParam.setAdePeriodicidade(adePeriodicidade);

                        alongarParam.setNomeAnexo(nomeAnexo);
                        alongarParam.setIdAnexo(idAnexo);
                        alongarParam.setAadDescricao(aadDescricao);

                        // Seta os dados genéricos que o responsável tem permissão de alterar
                        alongarParam.setDadosAutorizacaoMap(dadosAutorizacao);

                        alongarParam.setTelaConfirmacaoDuplicidade("S".equals(request.getParameter("telaConfirmacaoDuplicidade")));
                        alongarParam.setChkConfirmarDuplicidade(!TextHelper.isNull(request.getParameter("chkConfirmarDuplicidade")));
                        alongarParam.setMotivoOperacaoCodigoDuplicidade(request.getParameter("TMO_CODIGO"));
                        alongarParam.setMotivoOperacaoObsDuplicidade(request.getParameter("ADE_OBS"));

                        adeCodigoNovo = alongarConsignacaoController.alongar(alongarParam, responsavel);

                    } catch (final AutorizacaoControllerException mae) {
                        final String messageKey = mae.getMessageKey();
                        if ("mensagem.erro.ade.duplicidade.bloqueada.ate.data.limite".equals(messageKey)) {
                            return verificarPossibilidadePermitirDuplicidade(request, session, model, responsavel, cnvCodigo, "incluirReserva", mae);
                        } else {
                            session.setAttribute(CodedValues.MSG_ERRO, mae.getMessage());
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
                    }
                }

                if (adeCodigoNovo != null) {
                    if (processador != null) {
                        try {
                            processador.finalizar(request, adeCodigoNovo);
                        } catch (final ViewHelperException ex) {
                            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
                    }
                    // DESENV-13592 : se o servidor não fez o KYC, e não for ele mesmo fazendo a reserva,
                    // inclui marcação que o usuário está ciente disto se houver o parâmetro confirmando que ele viu a mensagem e está de acordo.
                    if (servidorDeveSerKYCComplaint && !responsavel.isSer()) {
                        final KYCHelper kycHelper = new KYCHelper(servidor.getAttribute(Columns.SER_CODIGO).toString(), responsavel);
                        if (!kycHelper.validou()) {
                            autorizacaoController.setDadoAutDesconto(adeCodigoNovo, CodedValues.TDA_CIENTE_KYC_NAO_FINALIZADO, "S", responsavel);
                        }
                    }

                    session.removeAttribute(CodedValues.MSG_ERRO);

                    final RegistroServidorTO registroServidor = servidorController.findRegistroServidor(rseCodigo, responsavel);
                    final String paramCsa = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_CATEGORIAS_PARA_EXIBIR_MENSAGEM_RESERVA_CONSULTA_MARGEM, responsavel);
                    if((responsavel.isCsaCor() && !TextHelper.isNull(paramCsa)) && paramCsa.contains(registroServidor.getRseTipo())) {
                        autorizacaoController.criaOcorrenciaADE(adeCodigoNovo, CodedValues.TOC_CONFIRMACAO_MENSAGEM_RESERVA_CATEGORIA_QUE_DEVE_EXIBIR_ALERTA, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.mensagem.confirmada.reservar.margem", responsavel), responsavel);
                    }

                    if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                        autorizacaoController.criaOcorrenciaADE(adeCodigoNovo, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO, (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA), ocaPeriodoDate, (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                        session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
                        session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
                    }

                    if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_PERIODO_ACEITA_APENAS_REDUCOES, CodedValues.TPC_SIM, responsavel)) {
                        // Inclui alerta na sessão do usuário se o período usado só permite reduções
                        final java.sql.Date adeAnoMesIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, ocaPeriodoDate, adeCarencia, adePeriodicidade, responsavel);
                        final java.sql.Date adeAnoMesIniValido = PeriodoHelper.getInstance().validarAdeAnoMesIni(orgCodigo, adeAnoMesIni, responsavel);
                        if (!adeAnoMesIniValido.equals(adeAnoMesIni)) {
                            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.alerta.reservar.margem.data.inicial.ajustada.periodo.apenas.reducoes", responsavel));
                        }
                    }

                    if (ParamSist.getBoolParamSist(CodedValues.TPC_BLOQUEIA_CSA_ADE_SEM_MIN_ANEXOS, responsavel) && responsavel.isCsaCor()) {
                        final Short diasParaAnexarArqNecessarios = paramSvcCse.getTpsQtdMinAnexosAdeVlr();
                        final Short numAnexosMin = paramSvcCse.getTpsQtdMinAnexosAdeVlrRef();

                        if ((diasParaAnexarArqNecessarios != null) && (numAnexosMin != null) && (diasParaAnexarArqNecessarios.shortValue() > 0) && (numAnexosMin.shortValue() > 0)) {
                            int numAnexosFaltantes = 0;
                            final List<Calendario> diasUteis = calendarioController.lstCalendariosAPartirDe(DateHelper.getSystemDate(), true, diasParaAnexarArqNecessarios.intValue());

                            if (!TextHelper.isNull(nomeAnexo)) {
                                numAnexosFaltantes = numAnexosMin.intValue() - nomeAnexo.split(";").length;
                            } else {
                                numAnexosFaltantes = numAnexosMin.intValue();
                            }

                            if (numAnexosFaltantes > 0) {
                                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.alerta.reservar.margem.anexos.minimos", responsavel, Integer.valueOf(numAnexosMin).toString(), DateHelper.format(diasUteis.get(diasUteis.size() - 1).getCalData(), LocaleHelper.getDatePattern()) + " " + DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm"), Integer.toString(numAnexosFaltantes)));
                            }
                        }
                    }

                    // Se existe simulação de consignação redireciona para a página do boleto
                    // caso o contrato seja um contrato de empréstimo: Verifica pelo Coeficiente de Desconto
                    CustomTransferObject cde = null;
                    if (temSimulacaoConsignacao) {
                        try {
                            cde = simulacaoController.findCdeByAdeCodigo(adeCodigoNovo, responsavel);
                        } catch (final SimulacaoControllerException ex) {
                            // Não há coeficiente.
                            cde = null;
                        }
                    }

                    // Repassa o token salvo, pois o método irá revalidar o token
                    request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

                    // Vai para detalhe ou boleto da consignação, com botão voltar redirecionando para a página inicial.
                    model.addAttribute("botaoVoltarPaginaInicial", Boolean.TRUE);

                    // Por padrão, quando o serviço possui cadastro de prazos e taxas, e existe boleto personalizado, o sistema
                    // redireciona automaticamente para o boleto. Porém pelo parâmetro de serviço de consignatária esta pode optar
                    // por redirecionar à tela de detalhe
                    if (responsavel.isCsaCor() && !exibeBoleto) {
                        return detalharConsignacao(adeCodigoNovo, request, response, session, model);
                    } else if (boletoExterno) {
                        return emitirBoletoExterno(adeCodigoNovo, request, response, session, model);
                    } else if (cde != null) {
                        return emitirBoleto(adeCodigoNovo, request, response, session, model);
                    } else {

                        String link = "../v3/reservarMargem?acao=detalharConsignacao&back=1&botaoVoltarPaginaInicial=true&ADE_CODIGO=" + adeCodigoNovo;
                        link = TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request));

                        request.setAttribute("url64", link);
                        return "jsp/redirecionador/redirecionar";
                    }
                }
            }
        } catch (NumberFormatException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
    }

    protected void validarValoresObrigatorios(HttpServletRequest request, String rseCodigo, String csaCodigo, String orgCodigo, String cnvCodigo, String svcCodigo, Object adeValor, AcessoSistema responsavel) throws ViewHelperException {
        if ("".equals(cnvCodigo) || "".equals(csaCodigo) || "".equals(rseCodigo) || "".equals(svcCodigo) || "".equals(orgCodigo) || "".equals(adeValor)) {
            throw new ViewHelperException("mensagem.erro.interno.contate.administrador", responsavel);
        }

        if (((request.getParameter("adePrazo") == null) && (request.getParameter("adeSemPrazo") == null)) || (request.getParameter("adeCarencia") == null)) {
            throw new ViewHelperException("mensagem.erro.interno.contate.administrador", responsavel);
        }
    }

    /**
     * Inclui a reserva de margem e retorna o código da nova consignação
     * @param rmParam
     * @param request
     * @param session
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    protected String incluirReservaMargem(ReservarMargemParametros rmParam, HttpServletRequest request, HttpSession session, AcessoSistema responsavel) throws ViewHelperException {
        try {
            return reservarMargemController.reservarMargem(rmParam, responsavel);
        } catch (final AutorizacaoControllerException ex) {
            throw new ViewHelperException(ex);
        }
    }

    private String montarTermoConsentimentoDadosServidor(AcessoSistema responsavel) {
        String termoConsentimento = "";
        if (ParamSist.paramEquals(CodedValues.TPC_EXIGE_CONFIRMACAO_TERMO_CONSENTIMENTO_DADOS_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
            String absolutePath = ParamSist.getDiretorioRaizArquivos();
            absolutePath += File.separatorChar + "termo_de_uso" + File.separatorChar;
            absolutePath += CodedNames.TEMPLATE_TERMO_CONSENTIMENTO_DADOS_SERVIDOR;

            final File file = new File(absolutePath);
            if ((file != null) && file.isFile() && file.exists()) {
                termoConsentimento = FileHelper.readAll(absolutePath).replaceAll("\\r\\n|\\r|\\n", "");
            }
        }
        return termoConsentimento;
    }

    private String iniciarReservaGap(String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            final CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            if (servidor == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            model.addAttribute("servidor", servidor);

            // Valida o token de sessão para evitar a chamada direta à operação
            if ((servidor == null) || !SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final String rseMatricula = servidor.getAttribute(Columns.RSE_MATRICULA).toString();
            final String svcCodigo = request.getParameter("SVC_CODIGO");

            final String titulo = request.getAttribute("titulo") != null ? request.getAttribute("titulo").toString() : "";

            if (!AcessoFuncaoServico.temAcessoFuncao(request, CodedValues.FUN_RES_MARGEM, responsavel.getUsuCodigo(), svcCodigo)) {
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String csaCodigo = "";
            String csaNome = "";
            if (responsavel.getCsaCodigo() != null) {
                csaCodigo = responsavel.getCsaCodigo();
            } else if (request.getParameter("CSA_CODIGO") != null) {
                csaCodigo = request.getParameter("CSA_CODIGO");
                final ConsignatariaTransferObject csaTO = consignatariaController.findConsignataria(csaCodigo, responsavel);
                csaNome = csaTO.getCsaIdentificador() + " - " + csaTO.getCsaNome();
            }

            String estNome = servidor.getAttribute(Columns.EST_IDENTIFICADOR) + " - " + servidor.getAttribute(Columns.EST_NOME);
            if (estNome.length() > 50) {
                estNome = estNome.substring(0, 47) + "...";
            }
            String orgNome = servidor.getAttribute(Columns.ORG_IDENTIFICADOR) + " - " + servidor.getAttribute(Columns.ORG_NOME);
            if (orgNome.length() > 50) {
                orgNome = orgNome.substring(0, 47) + "...";
            }
            String serNome = servidor.getAttribute(Columns.RSE_MATRICULA) + " - " + servidor.getAttribute(Columns.SER_NOME);
            if (serNome.length() > 50) {
                serNome = serNome.substring(0, 47) + "...";
            }

            final String serCpf = servidor.getAttribute(Columns.SER_CPF).toString();
            String categoria = servidor.getAttribute(Columns.RSE_TIPO) != null ? servidor.getAttribute(Columns.RSE_TIPO).toString() : "";
            final String codCargo = servidor.getAttribute(Columns.CRS_IDENTIFICADOR) != null ? servidor.getAttribute(Columns.CRS_IDENTIFICADOR).toString() : "";
            final String cargo = servidor.getAttribute(Columns.CRS_DESCRICAO) != null ? servidor.getAttribute(Columns.CRS_DESCRICAO).toString() : "";
            final String codPadrao = servidor.getAttribute(Columns.PRS_IDENTIFICADOR) != null ? servidor.getAttribute(Columns.PRS_IDENTIFICADOR).toString() : "";
            final String padrao = servidor.getAttribute(Columns.PRS_DESCRICAO) != null ? servidor.getAttribute(Columns.PRS_DESCRICAO).toString() : "";
            final String codSubOrgao = servidor.getAttribute(Columns.SBO_IDENTIFICADOR) != null ? servidor.getAttribute(Columns.SBO_IDENTIFICADOR).toString() : "";
            final String subOrgao = servidor.getAttribute(Columns.SBO_DESCRICAO) != null ? servidor.getAttribute(Columns.SBO_DESCRICAO).toString() : "";
            final String codUnidade = servidor.getAttribute(Columns.UNI_IDENTIFICADOR) != null ? servidor.getAttribute(Columns.UNI_IDENTIFICADOR).toString() : "";
            final String unidade = servidor.getAttribute(Columns.UNI_DESCRICAO) != null ? servidor.getAttribute(Columns.UNI_DESCRICAO).toString() : "";
            final String clt = servidor.getAttribute(Columns.RSE_CLT) != null ? "S".equalsIgnoreCase(servidor.getAttribute(Columns.RSE_CLT).toString()) ? ApplicationResourcesHelper.getMessage("rotulo.servidor.clt", responsavel) : "" : "";
            final String rsePrazo = servidor.getAttribute(Columns.RSE_PRAZO) != null ? servidor.getAttribute(Columns.RSE_PRAZO).toString() : "";
            final String dataAdmissao = servidor.getAttribute(Columns.RSE_DATA_ADMISSAO) != null ? DateHelper.reformat(servidor.getAttribute(Columns.RSE_DATA_ADMISSAO).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern()) : "";
            final String serDataNasc = servidor.getAttribute(Columns.SER_DATA_NASC) != null ? DateHelper.reformat(servidor.getAttribute(Columns.SER_DATA_NASC).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern()) : "";

            if (!"".equals(categoria) && !"".equals(clt)) {
                categoria += " - " + clt;
            } else if (!"".equals(clt)) {
                categoria = clt;
            }

            if (!"".equals(categoria) && !"".equals(rsePrazo)) {
                categoria += " - " + rsePrazo + " " + ApplicationResourcesHelper.getMessage("rotulo.meses", responsavel);
            } else if (!"".equals(rsePrazo)) {
                categoria = rsePrazo + " " + ApplicationResourcesHelper.getMessage("rotulo.meses", responsavel);
            }

            // Informação bancária do servidor
            final String numBanco = JCryptOld.crypt("IB", servidor.getAttribute(Columns.RSE_BANCO_SAL) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_BANCO_SAL).toString()) : "");
            final String numAgencia = JCryptOld.crypt("IB", servidor.getAttribute(Columns.RSE_AGENCIA_SAL) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_AGENCIA_SAL).toString()) : "");
            final int sizeNumAgencia = servidor.getAttribute(Columns.RSE_AGENCIA_SAL) == null ? 0 : servidor.getAttribute(Columns.RSE_AGENCIA_SAL).toString().length();
            final String numConta = servidor.getAttribute(Columns.RSE_CONTA_SAL) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_CONTA_SAL).toString()) : "";

            String numConta1 = "";
            String numConta2 = "";

            if (numConta.length() > 0) {
                numConta1 = numConta.substring(0, numConta.length() / 2);
                numConta2 = numConta.substring(numConta.length() / 2, numConta.length());
            } else {
                numConta1 = numConta2 = numConta;
            }

            numConta1 = JCryptOld.crypt("IB", numConta1);
            numConta2 = JCryptOld.crypt("IB", numConta2);

            // Combo de correspondentes
            String corCodigo = null;
            List<TransferObject> correspondentes = null;
            if (!responsavel.isCor()) {
                final CorrespondenteTransferObject cor = new CorrespondenteTransferObject();
                cor.setCsaCodigo(csaCodigo);
                cor.setCorAtivo(CodedValues.STS_ATIVO);
                correspondentes = consignatariaController.lstCorrespondentes(cor, responsavel);
            } else {
                corCodigo = responsavel.getCodigoEntidade();
            }

            // Parâmetros do convênio
            final CustomTransferObject convenio = (CustomTransferObject) request.getAttribute("convenio");
            final String svcIdentificador = convenio.getAttribute(Columns.SVC_IDENTIFICADOR) != null ? convenio.getAttribute(Columns.SVC_IDENTIFICADOR).toString() : "";
            final String svcDescricao = convenio.getAttribute(Columns.SVC_DESCRICAO) != null ? convenio.getAttribute(Columns.SVC_DESCRICAO).toString() : "";
            final String cnvCodVerba = convenio.getAttribute(Columns.CNV_COD_VERBA) != null ? convenio.getAttribute(Columns.CNV_COD_VERBA).toString() : "";
            final String descricao = (cnvCodVerba.length() > 0 ? cnvCodVerba : svcIdentificador) + " - " + svcDescricao;
            final String cnvCodigo = convenio.getAttribute(Columns.CNV_CODIGO).toString();
            final String orgCodigo = convenio.getAttribute(Columns.CNV_ORG_CODIGO).toString();

            //Verifica quantidade de contratos por grupo de serviço e numero de consignatarias
            try {
                autorizacaoController.podeReservarMargem(cnvCodigo, corCodigo, rseCodigo, true, true, true, null, null, null, null, 0, null, null, null, "RESERVAR", true, false, responsavel);
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final ParamSvcTO paramSvcCse = (ParamSvcTO) request.getAttribute("paramSvc");

            // Parâmetro de incidência da margem
            final Short incMargem = paramSvcCse.getTpsIncideMargem();

            // Indica se deve validar a data de nascimento do servidor.
            final boolean validarDataNasc = paramSvcCse.isTpsValidarDataNascimentoNaReserva();
            final boolean hasValidacaoDataNasc = parametroController.hasValidacaoDataNasc(responsavel);

            // Pega o parâmetro que diz se o Banco/Conta/Agencia do servidor é obrigatória
            final boolean serInfBancariaObrigatoria = parametroController.verificaAutorizacaoReservaSemSenha(rseCodigo, svcCodigo, paramSvcCse.isTpsInfBancariaObrigatoria(), null, responsavel);

            // Indica se deve validar as informações bancárias.
            final boolean validarInfBancaria = paramSvcCse.isTpsValidarInfBancariaNaReserva();

            // Máscara de identificador do contrato
            final String mascaraAdeIdentificador = paramSvcCse.getTpsMascaraIdentificadorAde();

            // Mês de início de desconto
            final Integer mesInicioDesconto = (paramSvcCse.getTpsMesInicioDescontoGap() != null) && !"".equals(paramSvcCse.getTpsMesInicioDescontoGap()) ? Integer.valueOf(paramSvcCse.getTpsMesInicioDescontoGap()) : null;

            // Busca as margens para o registro servidor associadas ao serviço
            List<TransferObject> lstMargem = null;
            try {
                lstMargem = GAPHelper.lstMargemReservaGap(rseCodigo, orgCodigo, incMargem, mesInicioDesconto, responsavel);
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            model.addAttribute("rseMatricula", rseMatricula);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("svcCodigo", svcCodigo);
            model.addAttribute("titulo", titulo);
            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("csaNome", csaNome);
            model.addAttribute("estNome", estNome);
            model.addAttribute("orgNome", orgNome);
            model.addAttribute("serNome", serNome);
            model.addAttribute("serCpf", serCpf);
            model.addAttribute("categoria", categoria);
            model.addAttribute("codCargo", codCargo);
            model.addAttribute("cargo", cargo);
            model.addAttribute("codPadrao", codPadrao);
            model.addAttribute("padrao", padrao);
            model.addAttribute("codSubOrgao", codSubOrgao);
            model.addAttribute("subOrgao", subOrgao);
            model.addAttribute("codUnidade", codUnidade);
            model.addAttribute("unidade", unidade);
            model.addAttribute("clt", clt);
            model.addAttribute("rsePrazo", rsePrazo);
            model.addAttribute("dataAdmissao", dataAdmissao);
            model.addAttribute("serDataNasc", serDataNasc);
            model.addAttribute("numBanco", numBanco);
            model.addAttribute("numAgencia", numAgencia);
            model.addAttribute("sizeNumAgencia", sizeNumAgencia);
            model.addAttribute("numConta", numConta);
            model.addAttribute("numConta1", numConta1);
            model.addAttribute("numConta2", numConta2);
            model.addAttribute("correspondentes", correspondentes);
            model.addAttribute("svcIdentificador", svcIdentificador);
            model.addAttribute("svcDescricao", svcDescricao);
            model.addAttribute("cnvCodVerba", cnvCodVerba);
            model.addAttribute("descricao", descricao);
            model.addAttribute("cnvCodigo", cnvCodigo);
            model.addAttribute("orgCodigo", orgCodigo);
            model.addAttribute("incMargem", incMargem);
            model.addAttribute("validarDataNasc", validarDataNasc);
            model.addAttribute("hasValidacaoDataNasc", hasValidacaoDataNasc);
            model.addAttribute("serInfBancariaObrigatoria", serInfBancariaObrigatoria);
            model.addAttribute("validarInfBancaria", validarInfBancaria);
            model.addAttribute("mascaraAdeIdentificador", mascaraAdeIdentificador);
            model.addAttribute("mesInicioDesconto", mesInicioDesconto);
            model.addAttribute("lstMargem", lstMargem);

            return viewRedirect("jsp/reservarMargemGap/iniciarReservaGap", request, session, model, responsavel);

        } catch (NumberFormatException | ConsignatariaControllerException | ParametroControllerException | ServidorControllerException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    private void validaInformacoesServidorObrigatorias(HttpServletRequest request ,String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws ZetraException {
        boolean celularObrigatorio = false;
        boolean enderecoObrigatorio = false;
        boolean enderecoCelularObrigatorio = false;

        final List<String> tpsCodigos = new ArrayList<>();
        tpsCodigos.add(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO);

        // DESENV-17017 - Por decisão do setor de segurança (LGPD) essa implementação não deve ser liberado, por isso os valores são setados como false
        List<TransferObject> paramSvcCsa;
        try {
            paramSvcCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, false, responsavel);
            for (final TransferObject param2 : paramSvcCsa) {
                final CustomTransferObject param = (CustomTransferObject) param2;
                if (((param != null) && (param.getAttribute(Columns.PSC_VLR) != null)) && CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO.equals(param.getAttribute(Columns.TPS_CODIGO))){
                    final String pscVlr = !param.getAttribute(Columns.PSC_VLR).toString().isEmpty() ? param.getAttribute(Columns.PSC_VLR).toString() : "";
                    if("E".equals(pscVlr)) {
                       enderecoObrigatorio = true;
                    } else if ("C".equals(pscVlr)) {
                       celularObrigatorio = true;
                    } else if ("EC".equals(pscVlr)) {
                        enderecoCelularObrigatorio = true;
                    }
                }
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel) || (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel) && (celularObrigatorio|| enderecoCelularObrigatorio) && TextHelper.isNull(JspHelper.verificaVarQryStr(request,"SER_TEL")))) {
               throw new ViewHelperException("mensagem.informe.servidor.telefone",responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel)|| (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel)&& (enderecoObrigatorio|| enderecoCelularObrigatorio) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_END")))) {
                throw new ViewHelperException("mensagem.informe.servidor.logradouro",responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel)|| (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel) && (enderecoObrigatorio || enderecoCelularObrigatorio) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_NRO")))) {
                throw new ViewHelperException("mensagem.informe.servidor.numero",responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel)|| (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel)&& (enderecoObrigatorio|| enderecoCelularObrigatorio) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_COMPL")))) {
                throw new ViewHelperException("mensagem.informe.servidor.complemento",responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel)|| (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel)&& (enderecoObrigatorio|| enderecoCelularObrigatorio) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_BAIRRO")))) {
                throw new ViewHelperException("mensagem.informe.servidor.bairro",responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel)|| (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel)&& (enderecoObrigatorio|| enderecoCelularObrigatorio) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_CIDADE")))) {
                throw new ViewHelperException("mensagem.informe.servidor.cidade",responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel)|| (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel)&& (enderecoObrigatorio|| enderecoCelularObrigatorio) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_CEP")))) {
                throw new ViewHelperException("mensagem.informe.servidor.cep",responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel)|| (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel)&& (enderecoObrigatorio|| enderecoCelularObrigatorio) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_UF")))) {
                throw new ViewHelperException("mensagem.informe.servidor.estado",responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel)|| (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel)&& (celularObrigatorio|| enderecoCelularObrigatorio) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_CEL")))) {
                throw new ViewHelperException("mensagem.informe.servidor.celular",responsavel);
            }
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException(ex.getMessageKey(),responsavel);
        }
    }
}
