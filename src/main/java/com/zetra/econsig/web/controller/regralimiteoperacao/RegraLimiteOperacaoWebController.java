package com.zetra.econsig.web.controller.regralimiteoperacao;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.PostoRegistroServidorControllerException;
import com.zetra.econsig.exception.RegraLimiteOperacaoControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.limiteoperacao.RegraLimiteOperacaoCache;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.RegraLimiteOperacao;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.limiteoperacao.RegraLimiteOperacaoController;
import com.zetra.econsig.service.sdp.PostoRegistroServidorController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(method = {RequestMethod.POST}, value = {"/v3/regrasLimiteOperacao"})
public class RegraLimiteOperacaoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RegraLimiteOperacaoWebController.class);

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private PostoRegistroServidorController postoRegistroServidorController;

    @Autowired
    private RegraLimiteOperacaoController regraLimiteOperacaoController;

    @RequestMapping(params = {"acao=listarRegras"})
    public String listarRegras(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        List<TransferObject> regraLimiteOperacoes = new ArrayList<>();
        try {

            // Monta a paginação
            final int size = JspHelper.LIMITE;
            int total = 0;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (final Exception ex) {
            }
            regraLimiteOperacoes = regraLimiteOperacaoController.lstRegrasLimiteOperacaoFilter(responsavel.isCsa() ? responsavel.getCsaCodigo() : null, size, offset, responsavel);
            total = regraLimiteOperacaoController.countRegrasLimiteOperacaoFilter(responsavel.isCsa() ? responsavel.getCsaCodigo() : null, responsavel);


            // Monta lista de parâmetros através dos parâmetros de request
            final Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
            params.remove("offset");
            params.remove("back");
            params.remove("linkRet");
            params.remove("linkRet64");
            params.remove("eConsig.page.token");
            params.remove("_skip_history_");
            params.remove("pager");
            params.remove("acao");

            final List<String> requestParams = new ArrayList<>(params);

            final String linkListagem = "../v3/regrasLimiteOperacao?acao=listarRegras";
            configurarPaginador(linkListagem, "rotulo.regra.limite.operacao.titulo.paginacao", total, size, requestParams, false, request, model);

        } catch (RegraLimiteOperacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        final boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);

        model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);
        model.addAttribute("regraLimiteOperacoes", regraLimiteOperacoes);
        return viewRedirect("jsp/regrasLimiteOperacao/listarRegrasLimiteOperacao", request, session, model, responsavel);
    }

    @RequestMapping(params = {"acao=criarEditarRegra"})
    public String novaRegra(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ZetraException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        List<TransferObject> orgaos = null;
        List<TransferObject> servicos = null;
        List<TransferObject> consignatarias = null;
        List<TransferObject> funcoes = null;
        List<TransferObject> correspondentes = null;
        List<TransferObject> subOrgaos = null;
        List<TransferObject> unidades = null;
        List<TransferObject> estabelecimentos = null;
        List<TransferObject> naturezaServicos = null;
        List<TransferObject> naturezaConsignatarias = null;
        List<TransferObject> cargosRegistroServidor = null;
        List<TransferObject> capacidadesRegistroSer = null;
        List<TransferObject> padroesRegistroSer = null;
        List<TransferObject> postosRegistroSer = null;
        List<TransferObject> statusRegistroSer = null;
        List<TransferObject> tiposRegistroSer = null;
        List<TransferObject> vinculosRegistroSer = null;
        boolean novo = request.getParameter("NOVO") != null && request.getParameter("NOVO").equals("S");
        TransferObject regraLimiteOperacao = new CustomTransferObject();
        if (!novo) {
            String rloCodigo = request.getParameter("RLO_CODIGO");
            regraLimiteOperacao = regraLimiteOperacaoController.findRegra(rloCodigo, responsavel);
        }

        if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_ORGAO, responsavel)) {
            try {
                orgaos = convenioController.getOrgCnvAtivo(null, null, responsavel);
            } catch (final ConvenioControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }
        if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_CONSIGNATARIA, responsavel)) {
            try {
                if (responsavel.isCsa()) {
                    TransferObject criterio = new CustomTransferObject();
                    criterio.setAttribute(Columns.CSA_CODIGO, responsavel.getCsaCodigo());
                    consignatarias = consignatariaController.lstConsignatarias(criterio, responsavel);
                } else {
                    if (!novo && !TextHelper.isNull(regraLimiteOperacao.getAttribute(Columns.RLO_CSA_CODIGO))) {
                        TransferObject criterio = new CustomTransferObject();
                        criterio.setAttribute(Columns.CSA_CODIGO, regraLimiteOperacao.getAttribute(Columns.RLO_CSA_CODIGO));
                        consignatarias = consignatariaController.lstConsignatarias(criterio, responsavel);
                    }
                }
            } catch (final ConsignatariaControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }
        if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_FUNCAO, responsavel)) {
            try {
                String funCodigos = String.join(",", CodedValues.FUN_ALONGAR_CONTRATO, CodedValues.FUN_RES_MARGEM, CodedValues.FUN_SOLICITAR_PORTABILIDADE,
                        CodedValues.FUN_SOL_EMPRESTIMO, CodedValues.FUN_RENE_CONTRATO);
                funcoes = usuarioController.findFuncoesRegraTaxa(funCodigos, responsavel);
            } catch (final UsuarioControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }
        if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_SERVICO, responsavel)) {
            try {
                if (responsavel.isCsa()) {
                    servicos = servicoController.selectServicosCsa(responsavel.getCsaCodigo(), responsavel);
                } else {
                    if (!novo && !TextHelper.isNull(regraLimiteOperacao.getAttribute(Columns.RLO_CSA_CODIGO))) {
                        TransferObject criterio = new CustomTransferObject();
                        criterio.setAttribute(Columns.COR_CSA_CODIGO, regraLimiteOperacao.getAttribute(Columns.RLO_CSA_CODIGO));
                        servicos = convenioController.lstServicos(criterio, responsavel);
                    }
                }
            } catch (ServicoControllerException | ConvenioControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }
        if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_CORRESPONDENTE, responsavel)) {
            try {
                if (responsavel.isCsa()) {
                    TransferObject criterio = new CustomTransferObject();
                    criterio.setAttribute(Columns.COR_CSA_CODIGO, responsavel.getCsaCodigo());
                    correspondentes = consignatariaController.lstCorrespondentes(criterio, responsavel);
                } else {
                    if (!novo && !TextHelper.isNull(regraLimiteOperacao.getAttribute(Columns.RLO_CSA_CODIGO))) {
                        TransferObject criterio = new CustomTransferObject();
                        criterio.setAttribute(Columns.COR_CSA_CODIGO, regraLimiteOperacao.getAttribute(Columns.RLO_CSA_CODIGO));
                        correspondentes = consignatariaController.lstCorrespondentes(criterio, responsavel);
                    }
                }
            } catch (final ConsignatariaControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }
        if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_SUBORGAO, responsavel)) {
            try {
                subOrgaos = servidorController.lstSubOrgao(responsavel, null);
            } catch (final ServidorControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }
        if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_UNIDADE, responsavel)) {
            try {
                unidades = servidorController.lstUnidadeSubOrgao(responsavel);
            } catch (final ServidorControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }
        if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_ESTABELECIMENTO, responsavel)) {
            try {
                estabelecimentos = consignanteController.lstEstabelecimentos(null, responsavel);
            } catch (final ConsignanteControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }
        if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_NATUREZA_SVC, responsavel)) {
            try {
                naturezaServicos = servicoController.lstNaturezasServicos(false);
            } catch (final ServicoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }
        if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_NATUREZA_CSA, responsavel)) {
            try {
                naturezaConsignatarias = consignatariaController.lstNatureza();
            } catch (final ConsignatariaControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }
        if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_CARGO, responsavel)) {
            try {
                cargosRegistroServidor = servidorController.lstCargo(responsavel);
            } catch (final ServidorControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }
        if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_CAPACIDADE, responsavel)) {
            try {
                capacidadesRegistroSer = servidorController.lstCapacidadeCivil(responsavel);
            } catch (final ServidorControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }
        if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_PADRAO, responsavel)) {
            try {
                padroesRegistroSer = servidorController.lstPadrao(responsavel);
            } catch (final ServidorControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }
        if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_POSTO, responsavel)) {
            try {
                postosRegistroSer = postoRegistroServidorController.lstPostoRegistroServidor(null, -1, -1, responsavel);
            } catch (final PostoRegistroServidorControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }
        if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_STATUS, responsavel)) {
            try {
                statusRegistroSer = servidorController.lstStatusRegistroServidor(false, false, responsavel);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }
        if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_TIPO, responsavel)) {
            try {
                tiposRegistroSer = servidorController.lstTipoRegistroServidor(responsavel);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }
        if (ShowFieldHelper.showField(FieldKeysConstants.REGRA_LIMITE_OPERACAO_VINCULO, responsavel)) {
            try {
                vinculosRegistroSer = servidorController.selectVincRegistroServidor(true, responsavel);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }

        final boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);

        model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);
        model.addAttribute("novo", novo);
        model.addAttribute("orgaos", orgaos);
        model.addAttribute("unidades", unidades);
        model.addAttribute("funcoes", funcoes);
        model.addAttribute("servicos", servicos);
        model.addAttribute("subOrgaos", subOrgaos);
        model.addAttribute("consignatarias", consignatarias);
        model.addAttribute("correspondentes", correspondentes);
        model.addAttribute("estabelecimentos", estabelecimentos);
        model.addAttribute("naturezaServicos", naturezaServicos);
        model.addAttribute("tiposRegistroSer", tiposRegistroSer);
        model.addAttribute("postosRegistroSer", postosRegistroSer);
        model.addAttribute("statusRegistroSer", statusRegistroSer);
        model.addAttribute("padroesRegistroSer", padroesRegistroSer);
        model.addAttribute("vinculosRegistroSer", vinculosRegistroSer);
        model.addAttribute("regraLimiteOperacao", regraLimiteOperacao);
        model.addAttribute("naturezaConsignatarias", naturezaConsignatarias);
        model.addAttribute("cargosRegistroServidor", cargosRegistroServidor);
        model.addAttribute("capacidadesRegistroSer", capacidadesRegistroSer);

        return viewRedirect("jsp/regrasLimiteOperacao/editarRegraLimiteOperacao", request, session, model, responsavel);
    }

    @RequestMapping(params = {"acao=salvar"})
    public String editarRegra(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws RegraLimiteOperacaoControllerException, ParseException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        final boolean novo = request.getParameter("NOVO") != null && request.getParameter("NOVO").equals("S");

        RegraLimiteOperacao regraLimiteOperacao = new RegraLimiteOperacao();
        if (!novo) {
            String rloCodigo = JspHelper.verificaVarQryStr(request, "RLO_CODIGO");
            regraLimiteOperacao = regraLimiteOperacaoController.findRegraByPrimaryKey(rloCodigo, responsavel);
        }
        regraLimiteOperacao.setCapCodigo(JspHelper.verificaVarQryStr(request, "capCodigo").equals("") ? null : JspHelper.verificaVarQryStr(request, "capCodigo"));
        if (responsavel.isCsa()) {
            regraLimiteOperacao.setCsaCodigo(responsavel.getCsaCodigo());
        } else {
            regraLimiteOperacao.setCsaCodigo(JspHelper.verificaVarQryStr(request, "csaCodigo").equals("") ? null : JspHelper.verificaVarQryStr(request, "csaCodigo"));
        }
        regraLimiteOperacao.setCorCodigo(JspHelper.verificaVarQryStr(request, "corCodigo").equals("") ? null : JspHelper.verificaVarQryStr(request, "corCodigo"));
        regraLimiteOperacao.setCrsCodigo(JspHelper.verificaVarQryStr(request, "crsCodigo").equals("") ? null : JspHelper.verificaVarQryStr(request, "crsCodigo"));
        regraLimiteOperacao.setEstCodigo(JspHelper.verificaVarQryStr(request, "estCodigo").equals("") ? null : JspHelper.verificaVarQryStr(request, "estCodigo"));
        regraLimiteOperacao.setFunCodigo(JspHelper.verificaVarQryStr(request, "funCodigo").equals("") ? null : JspHelper.verificaVarQryStr(request, "funCodigo"));
        regraLimiteOperacao.setNcaCodigo(JspHelper.verificaVarQryStr(request, "ncaCodigo").equals("") ? null : JspHelper.verificaVarQryStr(request, "ncaCodigo"));
        regraLimiteOperacao.setNseCodigo(JspHelper.verificaVarQryStr(request, "nseCodigo").equals("") ? null : JspHelper.verificaVarQryStr(request, "nseCodigo"));
        regraLimiteOperacao.setOrgCodigo(JspHelper.verificaVarQryStr(request, "orgCodigo").equals("") ? null : JspHelper.verificaVarQryStr(request, "orgCodigo"));
        regraLimiteOperacao.setPosCodigo(JspHelper.verificaVarQryStr(request, "posCodigo").equals("") ? null : JspHelper.verificaVarQryStr(request, "posCodigo"));
        regraLimiteOperacao.setPrsCodigo(JspHelper.verificaVarQryStr(request, "prsCodigo").equals("") ? null : JspHelper.verificaVarQryStr(request, "prsCodigo"));
        regraLimiteOperacao.setSboCodigo(JspHelper.verificaVarQryStr(request, "sboCodigo").equals("") ? null : JspHelper.verificaVarQryStr(request, "sboCodigo"));
        regraLimiteOperacao.setSrsCodigo(JspHelper.verificaVarQryStr(request, "srsCodigo").equals("") ? null : JspHelper.verificaVarQryStr(request, "srsCodigo"));
        regraLimiteOperacao.setSvcCodigo(JspHelper.verificaVarQryStr(request, "svcCodigo").equals("") ? null : JspHelper.verificaVarQryStr(request, "svcCodigo"));
        regraLimiteOperacao.setVrsCodigo(JspHelper.verificaVarQryStr(request, "vrsCodigo").equals("") ? null : JspHelper.verificaVarQryStr(request, "vrsCodigo"));
        regraLimiteOperacao.setUniCodigo(JspHelper.verificaVarQryStr(request, "uniCodigo").equals("") ? null : JspHelper.verificaVarQryStr(request, "uniCodigo"));
        regraLimiteOperacao.setTrsCodigo(JspHelper.verificaVarQryStr(request, "trsCodigo").equals("") ? null : JspHelper.verificaVarQryStr(request, "trsCodigo"));
        regraLimiteOperacao.setRloDataVigenciaIni(JspHelper.verificaVarQryStr(request, "rloDataVigenciaInicial").equals("") ? DateHelper.getSystemDatetime() : DateHelper.parse(JspHelper.verificaVarQryStr(request, "rloDataVigenciaInicial"), LocaleHelper.getDateTimePattern()));
        regraLimiteOperacao.setRloDataVigenciaFim(JspHelper.verificaVarQryStr(request, "rloDataVigenciaFinal").equals("") ? null : DateHelper.parse(JspHelper.verificaVarQryStr(request, "rloDataVigenciaFinal"), LocaleHelper.getDateTimePattern()));
        regraLimiteOperacao.setRloFaixaEtariaIni(JspHelper.verificaVarQryStr(request, "rloFaixaEtariaInicial").equals("") ? null : Short.parseShort(JspHelper.verificaVarQryStr(request, "rloFaixaEtariaInicial")));
        regraLimiteOperacao.setRloFaixaEtariaFim(JspHelper.verificaVarQryStr(request, "rloFaixaEtariaFinal").equals("") ? null : Short.parseShort(JspHelper.verificaVarQryStr(request, "rloFaixaEtariaFinal")));
        regraLimiteOperacao.setRloFaixaSalarioIni(JspHelper.verificaVarQryStr(request, "rloFaixaSalarioInicial").equals("") ? null : BigDecimal.valueOf(NumberHelper.parse(JspHelper.verificaVarQryStr(request, "rloFaixaSalarioInicial"), NumberHelper.getLang())));
        regraLimiteOperacao.setRloFaixaSalarioFim(JspHelper.verificaVarQryStr(request, "rloFaixaSalarioFinal").equals("") ? null : BigDecimal.valueOf(NumberHelper.parse(JspHelper.verificaVarQryStr(request, "rloFaixaSalarioFinal"), NumberHelper.getLang())));
        regraLimiteOperacao.setRloFaixaMargemFolhaIni(JspHelper.verificaVarQryStr(request, "rloFaixaMargemInicial").equals("") ? null : BigDecimal.valueOf(NumberHelper.parse(JspHelper.verificaVarQryStr(request, "rloFaixaMargemInicial"), NumberHelper.getLang())));
        regraLimiteOperacao.setRloFaixaMargemFolhaFim(JspHelper.verificaVarQryStr(request, "rloFaixaMargemFinal").equals("") ? null : BigDecimal.valueOf(NumberHelper.parse(JspHelper.verificaVarQryStr(request, "rloFaixaMargemFinal"), NumberHelper.getLang())));
        regraLimiteOperacao.setRloFaixaTempoServicoIni(JspHelper.verificaVarQryStr(request, "rloFaixaTempoServicoInicial").equals("") ? null : Short.parseShort(JspHelper.verificaVarQryStr(request, "rloFaixaTempoServicoInicial")));
        regraLimiteOperacao.setRloFaixaTempoServicoFim(JspHelper.verificaVarQryStr(request, "rloFaixaTempoServicoFinal").equals("") ? null : Short.parseShort(JspHelper.verificaVarQryStr(request, "rloFaixaTempoServicoFinal")));
        regraLimiteOperacao.setRloLimitePrazo(JspHelper.verificaVarQryStr(request, "rloLimitePrazo").equals("") ? null : Short.parseShort(JspHelper.verificaVarQryStr(request, "rloLimitePrazo")));
        regraLimiteOperacao.setRloLimiteDataFimAde(JspHelper.verificaVarQryStr(request, "rloDataLimiteAde").equals("") ? null : DateHelper.parse(JspHelper.verificaVarQryStr(request, "rloDataLimiteAde"), LocaleHelper.getDatePattern()));
        regraLimiteOperacao.setRloLimiteValorLiberado(JspHelper.verificaVarQryStr(request, "rloLimiteVlrLiberado").equals("") ? null : BigDecimal.valueOf(NumberHelper.parse(JspHelper.verificaVarQryStr(request, "rloLimiteVlrLiberado"), NumberHelper.getLang())));
        regraLimiteOperacao.setRloLimiteQuantidade(JspHelper.verificaVarQryStr(request, "rloLimiteQuantidade").equals("") ? null : Short.parseShort(JspHelper.verificaVarQryStr(request, "rloLimiteQuantidade")));
        regraLimiteOperacao.setRloLimiteCapitalDevido(JspHelper.verificaVarQryStr(request, "rloLimiteCapitalDevido").equals("") ? null : BigDecimal.valueOf(NumberHelper.parse(JspHelper.verificaVarQryStr(request, "rloLimiteCapitalDevido"), NumberHelper.getLang())));
        regraLimiteOperacao.setRloLimiteValorParcela(JspHelper.verificaVarQryStr(request, "rloLimiteVlrParcela").equals("") ? null : BigDecimal.valueOf(NumberHelper.parse(JspHelper.verificaVarQryStr(request, "rloLimiteVlrParcela"), NumberHelper.getLang())));
        regraLimiteOperacao.setRloMensagemErro(JspHelper.verificaVarQryStr(request, "rloMensagemErro").equals("") ? null : JspHelper.verificaVarQryStr(request, "rloMensagemErro"));
        regraLimiteOperacao.setRloPadraoMatricula(JspHelper.verificaVarQryStr(request, "rloPadraoMatricula").equals("") ? null : JspHelper.verificaVarQryStr(request, "rloPadraoMatricula"));
        regraLimiteOperacao.setRloPadraoCategoria(JspHelper.verificaVarQryStr(request, "rloPadraoCategoria").equals("") ? null : JspHelper.verificaVarQryStr(request, "rloPadraoCategoria"));
        regraLimiteOperacao.setRloPadraoVerba(JspHelper.verificaVarQryStr(request, "rloPadraoVerba").equals("") ? null : JspHelper.verificaVarQryStr(request, "rloPadraoVerba"));
        regraLimiteOperacao.setRloPadraoVerbaRef(JspHelper.verificaVarQryStr(request, "rloPadraoVerbaRef").equals("") ? null : JspHelper.verificaVarQryStr(request, "rloPadraoVerbaRef"));

        if (!novo) {
            try {
                regraLimiteOperacaoController.updateRegra(regraLimiteOperacao, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("rotulo.regra.limite.operacao.regra.editada.sucesso", responsavel));
            } catch (final RegraLimiteOperacaoControllerException ex) {
                throw new RegraLimiteOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        } else {
            regraLimiteOperacao.setUsuCodigo(responsavel.getUsuCodigo());
            regraLimiteOperacao.setRloDataCadastro(DateHelper.getSystemDatetime());
            try {
                regraLimiteOperacaoController.createRegra(regraLimiteOperacao, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("rotulo.regra.limite.operacao.regra.criada.sucesso", responsavel));
            } catch (final RegraLimiteOperacaoControllerException ex) {
                throw new RegraLimiteOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }

        RegraLimiteOperacaoCache.reset();
        return listarRegras(request, response, session, model);
    }

    @RequestMapping(params = {"acao=excluir"})
    public String excluirRegra(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws RegraLimiteOperacaoControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        final String rloCodigo = JspHelper.verificaVarQryStr(request, "RLO_CODIGO");

        try {
            regraLimiteOperacaoController.removeRegra(rloCodigo, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("rotulo.regra.limite.operacao.regra.excluida.sucesso", responsavel));
        } catch (final RegraLimiteOperacaoControllerException ex) {
            throw new RegraLimiteOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        RegraLimiteOperacaoCache.reset();
        return listarRegras(request, response, session, model);
    }

    @RequestMapping(method = {RequestMethod.POST}, params = {"acao=filtroCampoSelect"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TransferObject> filterSelect(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignatariaControllerException, ServicoControllerException, ServidorControllerException {
        List<TransferObject> result = new ArrayList<>();
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final String tipo = request.getParameter("tipo");
        final String codeFilterOne = request.getParameter("codeFilterOne");
        final String codeFilterTwo = request.getParameter("codeFilterTwo");
        if (tipo.equals("csaCor")) {
            TransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.COR_CSA_CODIGO, codeFilterOne);
            result = consignatariaController.lstCorrespondentes(criterio, responsavel);
        } else if (tipo.equals("nseSvc")) {
            result = servicoController.lstServicosByNseCsa(codeFilterOne, codeFilterTwo, responsavel);
        } else if (tipo.equals("csaSvc")) {
            result = servicoController.lstServicosByNseCsa(null, codeFilterOne, responsavel);
        } else if (tipo.equals("ncaCsa")) {
            TransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.CSA_NCA_NATUREZA, codeFilterOne);
            result = consignatariaController.lstConsignatarias(criterio, responsavel);
        } else if (tipo.equals("orgSbo")) {
            result = servidorController.lstSubOrgao(responsavel, codeFilterOne);
        }

        return result;
    }
}
