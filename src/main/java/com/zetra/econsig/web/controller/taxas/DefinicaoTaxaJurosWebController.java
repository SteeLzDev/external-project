package com.zetra.econsig.web.controller.taxas;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.DefinicaoTaxaJurosControllerException;
import com.zetra.econsig.exception.LimiteTaxaJurosControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.DefinicaoTaxaJuros;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.juros.DefinicaoTaxaJurosController;
import com.zetra.econsig.service.juros.LimiteTaxaJurosController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: DefinicaoTaxaJurosWebController</p>
 * <p>Description: Definir regra de taxa de juros(iniciar, salvar, editar, listar, novo, ativarTabela, excluirTabelaIniciada, iniciarTabela, visualizar e excluir)</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: rodrigo.rosa $
 * $Revision: 25753 $
 * $Date: 2019-04-10 17:27:43 -0200 (qua, 10 abr 2019) $
 */
@Controller
@RequestMapping(value = "/v3/editarRegraTaxaJuros")
public class DefinicaoTaxaJurosWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DefinicaoTaxaJurosWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private DefinicaoTaxaJurosController definicaoTaxaJurosController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private LimiteTaxaJurosController limiteTaxaJurosController;

    @Autowired
    private SimulacaoController simulacaoController;

    @Override
    public void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        final String titulo = JspHelper.verificaVarQryStr(request, "titulo");
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.regra.taxa.juros.listar.subtitulo", responsavel, titulo));
        model.addAttribute("linkAction", getLinkAction());
    }

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Carrega lista de órgão e de serviço
        carregarListaOrgao(request, session, model, responsavel);
        carregarListaServico(request, session, model, responsavel);

        // Recuperando o csaCodigo
        String csaCodigo;
        if (responsavel.isCsa()) {
            csaCodigo = responsavel.getCsaCodigo();
            model.addAttribute("csaCodigo", csaCodigo);
        } else if (responsavel.isSup()) {
            csaCodigo = request.getParameter("CSA_CODIGO");
            model.addAttribute("csaCodigo", csaCodigo);
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Seta verificação de permissão para alterar regra de taxa de juros
        final boolean alterarRegraTaxaJuros = responsavel.temPermissao(CodedValues.FUN_EDITAR_REGRAS_TAXA_DE_JUROS);
        model.addAttribute("podeAlterarRegraTaxaJuros", alterarRegraTaxaJuros);

        // Valida o token - Se estiver inválido retorna erro
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

        }

        try {
            final CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute("ORG_CODIGO", JspHelper.verificaVarQryStr(request, "ORG_CODIGO"));
            criterio.setAttribute("SVC_CODIGO", JspHelper.verificaVarQryStr(request, "SVC_CODIGO"));
            criterio.setAttribute(Columns.DTJ_CONSIGNATARIA, csaCodigo);

            if (JspHelper.verificaVarQryStr(request, "DATA") != null) {
                criterio.setAttribute("DATA", JspHelper.verificaVarQryStr(request, "DATA"));
            }

            final int size = JspHelper.LIMITE;
            int offset = 0;

            boolean ativarTabela = false;
            boolean tabelaVazia = false;
            boolean regrasAtivas = false;

            final CustomTransferObject criterioTabelaVazia = new CustomTransferObject();
            criterioTabelaVazia.setAttribute(Columns.DTJ_CONSIGNATARIA, csaCodigo);
            criterioTabelaVazia.setAttribute("pesquisaComDataVigenciaFim", false);
            final List<TransferObject> regrasGerais = definicaoTaxaJurosController.listaDefinicaoRegraTaxaJuros(criterioTabelaVazia, 0, 0, responsavel);

            if ((regrasGerais != null) && !regrasGerais.isEmpty()) {
                for (final TransferObject regraGeral : regrasGerais) {
                    if ((regraGeral.getAttribute(Columns.DTJ_DATA_VIGENCIA_INI) == null) && (regraGeral.getAttribute(Columns.DTJ_DATA_VIGENCIA_FIM) == null)) {
                        ativarTabela = true;
                    } else if ((regraGeral.getAttribute(Columns.DTJ_DATA_VIGENCIA_INI) != null) && (regraGeral.getAttribute(Columns.DTJ_DATA_VIGENCIA_FIM) == null)) {
                        regrasAtivas = true;
                    }
                }
            }

            if ((regrasGerais == null) || (regrasGerais.size() == 0)) {
                tabelaVazia = true;
            }

            model.addAttribute("ativarTabela", ativarTabela);
            model.addAttribute("tabelaVazia", tabelaVazia);

            //Se existir alguma regra de taxa de juros no banco de dados:
            if (regrasGerais.size() > 0) {
                //Condição: Se tiver tabela iniciada que pode ser ativada:
                if (JspHelper.verificaVarQryStr(request, "STATUS_REGRA").isEmpty() && ativarTabela) {
                    criterio.setAttribute("STATUS_REGRA", CodedValues.REGRA_NOVA_TABELA_INICIADA);
                    //Se tiver tabela com data vigencia ini preenchida aparece as regras ativas
                } else if (JspHelper.verificaVarQryStr(request, "STATUS_REGRA").isEmpty() && regrasAtivas) {
                    criterio.setAttribute("STATUS_REGRA", CodedValues.REGRA_TABELA_ATIVA);
                    //Se não tem regra vigente e nem em aberto - aparece regras expiradas (lembrando que este cenário é muito difícil de acontecer, apenas alterando banco de dados
                } else if (JspHelper.verificaVarQryStr(request, "STATUS_REGRA").isEmpty() && !regrasAtivas) {
                    criterio.setAttribute("STATUS_REGRA", CodedValues.REGRA_TABELA_VIGENCIA_EXPIRADA);
                }
            }
            // Se não tiver nenhuma regra de taxa de juros vem a informação do request.
            else {
                criterio.setAttribute("STATUS_REGRA", JspHelper.verificaVarQryStr(request, "STATUS_REGRA"));
            }

            if (request.getParameter("offset") != null) {
                try {
                    offset = Integer.parseInt(request.getParameter("offset"));
                } catch (final Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            final int total = definicaoTaxaJurosController.lstCountDefinicaoTaxaJuros(criterio, responsavel);
            final List<TransferObject> definicaoTaxaJuros = definicaoTaxaJurosController.listaDefinicaoRegraTaxaJuros(criterio, offset, size, responsavel);

            final Set<String> params = new HashSet<>(request.getParameterMap().keySet());
            final List<String> requestParams = new ArrayList<>(params);
            configurarPaginador(getLinkAction(), "rotulo.paginacao.titulo.usuario", total, size, requestParams, false, request, model);

            model.addAttribute("definicaoTaxaJuros", definicaoTaxaJuros);
            model.addAttribute("statusRegra", criterio.getAttribute("STATUS_REGRA"));

        } catch (final DefinicaoTaxaJurosControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/manterTaxas/listarRegraTaxaJuros", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvarDefinicaoTaxaJuros(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ParseException, InstantiationException, IllegalAccessException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token - Se estiver inválido retorna erro
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final String dtjCodigo = request.getParameter("dtjCodigo");
            model.addAttribute("dtjCodigo", dtjCodigo);

            final String csaCodigo = responsavel.isCsa() ? responsavel.getCsaCodigo() : JspHelper.verificaVarQryStr(request, "csaCodigo");
            final String orgCodigo = JspHelper.verificaVarQryStr(request, "orgCodigo");

            BigDecimal dtjTaxaJuros = null;
            BigDecimal dtjTaxaJurosMinima = null;
            Short dtjFaixaEtariaInicial = null;
            Short dtjFaixaEtariaFinal = null;
            Short dtjFaixaTempoServicoInicial = null;
            Short dtjFaixaTempoServicoFinal = null;
            BigDecimal dtjFaixaSalarialIni = null;
            BigDecimal dtjFaixaSalarialFim = null;
            BigDecimal dtjFaixaMargemIni = null;
            BigDecimal dtjFaixaMargemFim = null;
            BigDecimal dtjFaixaValorTotalIni = null;
            BigDecimal dtjFaixaValorTotalFim = null;
            BigDecimal dtjFaixaValorContratoIni = null;
            BigDecimal dtjFaixaValorContratoFim = null;
            Short dtjFaixaPrazoIni = null;
            Short dtjFaixaPrazoFim = null;
            final Date dtjDataCadastro = Calendar.getInstance().getTime();

            //Verificacao de todos os campos se estão vazios e comparando o mesmo campo inicial e final para impedir o prenchimento de um valor maior no inicial do que o final

            if (!JspHelper.verificaVarQryStr(request, "taxaJuros").isEmpty()) {
                dtjTaxaJuros = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "taxaJuros"), NumberHelper.getLang(), "en"));
            }
            
            boolean exibeCETMinMax = ParamSist.getBoolParamSist(CodedValues.TPC_CSA_PODE_CADASTRAR_CET_MININO_E_MAXIMO, responsavel);
            if (exibeCETMinMax && !JspHelper.verificaVarQryStr(request, "taxaJurosMinima").isEmpty()) {
            	dtjTaxaJurosMinima = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "taxaJurosMinima"), NumberHelper.getLang(), "en"));
            }

            if (!JspHelper.verificaVarQryStr(request, "faixaEtariaInicial").isEmpty()) {
                dtjFaixaEtariaInicial = Short.parseShort(JspHelper.verificaVarQryStr(request, "faixaEtariaInicial"));
            }

            if (!JspHelper.verificaVarQryStr(request, "faixaEtariaFinal").isEmpty()) {
                dtjFaixaEtariaFinal = Short.parseShort(JspHelper.verificaVarQryStr(request, "faixaEtariaFinal"));
            }

            if (!JspHelper.verificaVarQryStr(request, "faixaTempoServicoInicial").isEmpty()) {
                dtjFaixaTempoServicoInicial = Short.parseShort(JspHelper.verificaVarQryStr(request, "faixaTempoServicoInicial"));
            }

            if (!JspHelper.verificaVarQryStr(request, "faixaTempoServicoFinal").isEmpty()) {
                dtjFaixaTempoServicoFinal = Short.parseShort(JspHelper.verificaVarQryStr(request, "faixaTempoServicoFinal"));
            }

            if (!JspHelper.verificaVarQryStr(request, "faixaSalarialInicial").isEmpty()) {
                dtjFaixaSalarialIni = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "faixaSalarialInicial"), NumberHelper.getLang(), "en"));
            }

            if (!JspHelper.verificaVarQryStr(request, "faixaSalarialFinal").isEmpty()) {
                dtjFaixaSalarialFim = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "faixaSalarialFinal"), NumberHelper.getLang(), "en"));
            }

            if (!JspHelper.verificaVarQryStr(request, "faixaMargemInicial").isEmpty()) {
                dtjFaixaMargemIni = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "faixaMargemInicial"), NumberHelper.getLang(), "en"));
            }

            if (!JspHelper.verificaVarQryStr(request, "faixaMargemFinal").isEmpty()) {
                dtjFaixaMargemFim = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "faixaMargemFinal"), NumberHelper.getLang(), "en"));
            }

            if (!JspHelper.verificaVarQryStr(request, "faixaValorTotalInicial").isEmpty()) {
                dtjFaixaValorTotalIni = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "faixaValorTotalInicial"), NumberHelper.getLang(), "en"));
            }

            if (!JspHelper.verificaVarQryStr(request, "faixaValorTotalFinal").isEmpty()) {
                dtjFaixaValorTotalFim = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "faixaValorTotalFinal"), NumberHelper.getLang(), "en"));
            }

            if (!JspHelper.verificaVarQryStr(request, "faixaValorContratoInicial").isEmpty()) {
                dtjFaixaValorContratoIni = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "faixaValorContratoInicial"), NumberHelper.getLang(), "en"));
            }

            if (!JspHelper.verificaVarQryStr(request, "faixaValorContratoFinal").isEmpty()) {
                dtjFaixaValorContratoFim = new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "faixaValorContratoFinal"), NumberHelper.getLang(), "en"));
            }

            if (!JspHelper.verificaVarQryStr(request, "faixaPrazoInicial").isEmpty()) {
                dtjFaixaPrazoIni = Short.parseShort(JspHelper.verificaVarQryStr(request, "faixaPrazoInicial"));
            }

            if (!JspHelper.verificaVarQryStr(request, "faixaPrazoFinal").isEmpty()) {
                dtjFaixaPrazoFim = Short.parseShort(JspHelper.verificaVarQryStr(request, "faixaPrazoFinal"));
            }
            
            if ((dtjTaxaJurosMinima != null) && (dtjTaxaJuros != null) && (dtjTaxaJurosMinima.compareTo(dtjTaxaJuros) == 1)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.taxa.juros.minima.maior.taxa.juros.maxima", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if ((dtjFaixaSalarialIni != null) && (dtjFaixaSalarialFim != null) && (dtjFaixaSalarialIni.compareTo(dtjFaixaSalarialFim) == 1)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.faixa.salario.ini.maior.faixa.salario.fim", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if ((dtjFaixaEtariaInicial != null) && (dtjFaixaEtariaFinal != null) && (dtjFaixaEtariaInicial.compareTo(dtjFaixaEtariaFinal) == 1)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.faixa.etaria.ini.maior.faixa.etaria.fim", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if ((dtjFaixaTempoServicoInicial != null) && (dtjFaixaTempoServicoFinal != null) && (dtjFaixaTempoServicoInicial.compareTo(dtjFaixaTempoServicoFinal) == 1)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.faixa.etaria.ini.maior.faixa.etaria.fim", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if ((dtjFaixaMargemIni != null) && (dtjFaixaMargemFim != null) && (dtjFaixaMargemIni.compareTo(dtjFaixaMargemFim) == 1)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.faixa.etaria.ini.maior.faixa.etaria.fim", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if ((dtjFaixaValorTotalIni != null) && (dtjFaixaValorTotalFim != null) && (dtjFaixaValorTotalIni.compareTo(dtjFaixaValorTotalFim) == 1)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.faixa.etaria.ini.maior.faixa.etaria.fim", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if ((dtjFaixaValorContratoIni != null) && (dtjFaixaValorContratoFim != null) && (dtjFaixaValorContratoIni.compareTo(dtjFaixaValorContratoFim) == 1)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.faixa.etaria.ini.maior.faixa.etaria.fim", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if ((dtjFaixaPrazoIni != null) && (dtjFaixaPrazoFim != null) && (dtjFaixaPrazoIni.compareTo(dtjFaixaPrazoFim) == 1)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.faixa.etaria.ini.maior.faixa.etaria.fim", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Busca o DefinicaoTaxaJuros com a String dtjCodigo - Desta forma o objeto não fica com valor texto de NULL, caso seja um cadastro do zero
            final DefinicaoTaxaJuros definicaoTaxaJuros = definicaoTaxaJurosController.findDefinicaoByCodigo(dtjCodigo, responsavel);

            if (definicaoTaxaJuros != null) {
                final String funCodigo = JspHelper.verificaVarQryStr(request, "funCodigo");
                final String svcCodigo = JspHelper.verificaVarQryStr(request, "svcCodigo");

                if (!TextHelper.isNull(orgCodigo)) {
                    definicaoTaxaJuros.setOrgCodigo(orgCodigo);
                } else {
                    definicaoTaxaJuros.setOrgCodigo(null);
                }
                if (!TextHelper.isNull(funCodigo)) {
                    definicaoTaxaJuros.setFunCodigo(funCodigo);
                } else {
                    definicaoTaxaJuros.setFunCodigo(null);
                }
                if (svcCodigo.contains(";")) {
                    final String[] svcPartes = svcCodigo.split(";");
                    definicaoTaxaJuros.setSvcCodigo(svcPartes[0]);
                } else {
                    definicaoTaxaJuros.setSvcCodigo(svcCodigo);
                }

                definicaoTaxaJuros.setDtjTaxaJuros(dtjTaxaJuros);
                definicaoTaxaJuros.setDtjTaxaJurosMinima(dtjTaxaJurosMinima);
                definicaoTaxaJuros.setDtjFaixaEtariaIni(dtjFaixaEtariaInicial);
                definicaoTaxaJuros.setDtjFaixaEtariaFim(dtjFaixaEtariaFinal);
                definicaoTaxaJuros.setDtjFaixaTempServicoIni(dtjFaixaTempoServicoInicial);
                definicaoTaxaJuros.setDtjFaixaTempServicoFim(dtjFaixaTempoServicoFinal);
                definicaoTaxaJuros.setDtjFaixaSalarioIni(dtjFaixaSalarialIni);
                definicaoTaxaJuros.setDtjFaixaSalarioFim(dtjFaixaSalarialFim);
                definicaoTaxaJuros.setDtjFaixaMargemIni(dtjFaixaMargemIni);
                definicaoTaxaJuros.setDtjFaixaMargemFim(dtjFaixaMargemFim);
                definicaoTaxaJuros.setDtjFaixaValorTotalIni(dtjFaixaValorTotalIni);
                definicaoTaxaJuros.setDtjFaixaValorTotalFim(dtjFaixaValorTotalFim);
                definicaoTaxaJuros.setDtjFaixaValorContratoIni(dtjFaixaValorContratoIni);
                definicaoTaxaJuros.setDtjFaixaValorContratoFim(dtjFaixaValorContratoFim);
                definicaoTaxaJuros.setDtjFaixaPrazoIni(dtjFaixaPrazoIni);
                definicaoTaxaJuros.setDtjFaixaPrazoFim(dtjFaixaPrazoFim);

                // Atualiza uma regra de taxa de juros já cadastrada
                definicaoTaxaJurosController.update(definicaoTaxaJuros, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.regra.taxa.juros.salva.sucesso", responsavel));
            } else {
                final String[] svcCodigos = request.getParameterValues("svcCodigo");
                final String[] funCodigos = request.getParameterValues("funCodigo");

                if (svcCodigos != null && svcCodigos.length > 0) {
                    for (String svcCodigo : svcCodigos) {
                        if (svcCodigo.contains(";")) {
                            final String[] svcPartes = svcCodigo.split(";");
                            svcCodigo = svcPartes[0];
                        }

                        if (funCodigos != null && funCodigos.length > 0) {
                            for (String funCodigo : funCodigos) {
                                definicaoTaxaJurosController.create(csaCodigo, orgCodigo, svcCodigo, funCodigo, dtjFaixaEtariaInicial, dtjFaixaEtariaFinal, dtjFaixaTempoServicoInicial, dtjFaixaTempoServicoFinal, dtjFaixaSalarialIni, dtjFaixaSalarialFim, dtjFaixaMargemIni, dtjFaixaMargemFim, dtjFaixaValorTotalIni, dtjFaixaValorTotalFim, dtjFaixaValorContratoIni, dtjFaixaValorContratoFim, dtjFaixaPrazoIni, dtjFaixaPrazoFim, dtjTaxaJuros, dtjTaxaJurosMinima, dtjDataCadastro, responsavel);
                            }
                        } else {
                            definicaoTaxaJurosController.create(csaCodigo, orgCodigo, svcCodigo, null, dtjFaixaEtariaInicial, dtjFaixaEtariaFinal, dtjFaixaTempoServicoInicial, dtjFaixaTempoServicoFinal, dtjFaixaSalarialIni, dtjFaixaSalarialFim, dtjFaixaMargemIni, dtjFaixaMargemFim, dtjFaixaValorTotalIni, dtjFaixaValorTotalFim, dtjFaixaValorContratoIni, dtjFaixaValorContratoFim, dtjFaixaPrazoIni, dtjFaixaPrazoFim, dtjTaxaJuros, dtjTaxaJurosMinima, dtjDataCadastro, responsavel);
                        }
                    }
                }

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.regra.taxa.juros.criada.sucesso", responsavel));
            }

            // Repassa o token salvo, pois o método irá revalidar o token
            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

        } catch (final DefinicaoTaxaJurosControllerException ex) {
            ex.printStackTrace();
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editarDefinicaoTaxaJuros(@RequestParam(value = "dtjCodigo", required = true) String dtjCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConvenioControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final boolean aplicarRegraCETTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_APLICAR_REGRAS_LIMITE_VARIACAO_CET_PARA_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Recuperando o csaCodigo
        String csaCodigo;
        if (responsavel.isCsa()) {
            csaCodigo = responsavel.getCsaCodigo();
            model.addAttribute("csaCodigo", csaCodigo);
        } else if (responsavel.isSup()) {
            csaCodigo = request.getParameter("CSA_CODIGO");
            model.addAttribute("csaCodigo", csaCodigo);
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String dtj_codigo;
        // Variavel que vem do método salvar (Quando a regra de taxa de juros é nova)
        if ((dtjCodigo != null) && !"".equals(dtjCodigo)) {
            dtj_codigo = dtjCodigo;
        } else {
            //Alterando uma regra já existente
            dtj_codigo = JspHelper.verificaVarQryStr(request, "dtjCodigo");
        }

        DefinicaoTaxaJuros definicaoTaxaJuros = null;

        List<TransferObject> orgaos = null;
        List<TransferObject> servicos = null;
        List<TransferObject> limitesTaxa = null;
        List<TransferObject> funcoes = null;
        boolean temLimiteTaxa = false;

        try {
            //Busca a definicao taxa de juros pelo dtjCodigo
            try {
                definicaoTaxaJuros = definicaoTaxaJurosController.findDefinicaoByCodigo(dtjCodigo, responsavel);

            } catch (final DefinicaoTaxaJurosControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            //Carregando lista de serviços vinculados a CSA
            servicos = simulacaoController.lstServicosParaCadastroTaxas(csaCodigo, responsavel);

            //Carregando lista de orgaos vinculados a CSA
            final String corCodigo = (responsavel.isCor() && !responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) ? responsavel.getCodigoEntidade() : null;
            orgaos = convenioController.getOrgCnvAtivo(csaCodigo, corCodigo, responsavel);

            //Carregando Lista de funcoes da regra de taxa de juros
            final String tpaFunCodigos = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_FUNCOES_PARA_DEFINICAO_TAXA_JUROS, responsavel);
            funcoes = usuarioController.findFuncoesRegraTaxa(tpaFunCodigos, responsavel);

        } catch (SimulacaoControllerException | ConvenioControllerException | UsuarioControllerException | ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (aplicarRegraCETTaxaJuros) {
            try {
                //Carregando limites de cada serviço
                limitesTaxa = limiteTaxaJurosController.listaLimiteTaxaJurosPorServico(servicos, null, null, null, responsavel);
                temLimiteTaxa = !TextHelper.isNull(limitesTaxa);
            } catch (final LimiteTaxaJurosControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            model.addAttribute("temLimiteTaxa", temLimiteTaxa);
            model.addAttribute("limitesTaxa", limitesTaxa);
        }

        model.addAttribute("novo", false);
        model.addAttribute("podeEditar", true);
        model.addAttribute("dtj_codigo", dtj_codigo);
        model.addAttribute("definicaoTaxaJuros", definicaoTaxaJuros);
        model.addAttribute("orgaos", orgaos);
        model.addAttribute("servicos", servicos);
        model.addAttribute("funcoes", funcoes);
        model.addAttribute("aplicarRegraCETTaxaJuros", aplicarRegraCETTaxaJuros);
        
        boolean exibeCETMinMax = ParamSist.getBoolParamSist(CodedValues.TPC_CSA_PODE_CADASTRAR_CET_MININO_E_MAXIMO, responsavel);
        model.addAttribute("exibeCETMinMax", exibeCETMinMax);

        return viewRedirect("jsp/manterTaxas/alterarRegraTaxaJuros", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluirDefinicaoTaxaJuros(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        try {
            final String dtjCodigo = request.getParameter("dtjCodigo");
            final DefinicaoTaxaJuros definicaoTaxaJuros = new DefinicaoTaxaJuros();
            definicaoTaxaJuros.setDtjCodigo(dtjCodigo);
            definicaoTaxaJurosController.excluir(definicaoTaxaJuros, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.regra.taxa.juros.excluida.sucesso", responsavel));
        } catch (final DefinicaoTaxaJurosControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }
        return listar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=consultar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignanteControllerException, InstantiationException, IllegalAccessException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            SynchronizerToken.saveToken(request);

            // Recuperando o csaCodigo
            String csaCodigo;
            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCsaCodigo();
                model.addAttribute("csaCodigo", csaCodigo);
            } else if (responsavel.isSup()) {
                csaCodigo = request.getParameter("CSA_CODIGO");
                model.addAttribute("csaCodigo", csaCodigo);
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final boolean alterarRegraTaxaJuros = responsavel.temPermissao(CodedValues.FUN_EDITAR_REGRAS_TAXA_DE_JUROS);

            boolean ativarTabela = false;
            boolean tabelaVazia = false;
            boolean regrasAtivas = false;

            final CustomTransferObject criterioTabelaVazia = new CustomTransferObject();
            criterioTabelaVazia.setAttribute(Columns.DTJ_CONSIGNATARIA, csaCodigo);
            final List<TransferObject> regrasGerais = definicaoTaxaJurosController.listaDefinicaoRegraTaxaJuros(criterioTabelaVazia, 0, 0, responsavel);

            if ((regrasGerais == null) || (regrasGerais.size() == 0)) {
                tabelaVazia = true;
            }
            if ((regrasGerais != null) && !regrasGerais.isEmpty()) {
                for (final TransferObject regraGeral : regrasGerais) {
                    if ((regraGeral.getAttribute(Columns.DTJ_DATA_VIGENCIA_INI) == null) && (regraGeral.getAttribute(Columns.DTJ_DATA_VIGENCIA_FIM) == null)) {
                        ativarTabela = true;
                    } else if ((regraGeral.getAttribute(Columns.DTJ_DATA_VIGENCIA_INI) != null) && (regraGeral.getAttribute(Columns.DTJ_DATA_VIGENCIA_FIM) == null)) {
                        regrasAtivas = true;
                    }
                }
            }

            if ((regrasGerais == null) || (regrasGerais.size() == 0)) {
                tabelaVazia = true;
            }

            final String tipo = AcessoSistema.ENTIDADE_SUP;

            List<TransferObject> definicaoTaxaJuros = null;

            final CustomTransferObject criterio = new CustomTransferObject();

            try {
                criterio.setAttribute("ORG_CODIGO", JspHelper.verificaVarQryStr(request, "ORG_CODIGO"));
                criterio.setAttribute("SVC_CODIGO", JspHelper.verificaVarQryStr(request, "SVC_CODIGO"));
                criterio.setAttribute("STATUS_REGRA", JspHelper.verificaVarQryStr(request, "STATUS_REGRA"));
                criterio.setAttribute(Columns.DTJ_CONSIGNATARIA, csaCodigo);

                if (JspHelper.verificaVarQryStr(request, "DATA") != null) {
                    criterio.setAttribute("DATA", JspHelper.verificaVarQryStr(request, "DATA"));
                }

                final int size = JspHelper.LIMITE;
                int offset = 0;

                //Se existir alguma regra de taxa de juros no banco de dados:
                if (regrasGerais.size() > 0) {
                    //Condição: Se tiver tabela iniciada que pode ser ativada:
                    if (JspHelper.verificaVarQryStr(request, "STATUS_REGRA").isEmpty() && ativarTabela) {
                        criterio.setAttribute("STATUS_REGRA", CodedValues.REGRA_NOVA_TABELA_INICIADA);
                        //Se tiver tabela com data vigencia ini preenchida aparece as regras ativas
                    } else if (JspHelper.verificaVarQryStr(request, "STATUS_REGRA").isEmpty() && regrasAtivas) {
                        criterio.setAttribute("STATUS_REGRA", CodedValues.REGRA_TABELA_ATIVA);
                        //Se não tem regra vigente e nem em aberto - aparece regras expiradas (lembrando que este cenário é muito difícil de acontecer, apenas alterando banco de dados
                    } else if (JspHelper.verificaVarQryStr(request, "STATUS_REGRA").isEmpty() && !regrasAtivas) {
                        criterio.setAttribute("STATUS_REGRA", CodedValues.REGRA_TABELA_VIGENCIA_EXPIRADA);
                    }
                }
                // Se não tiver nenhuma regra de taxa de juros vem a informação do request.
                else {
                    criterio.setAttribute("STATUS_REGRA", JspHelper.verificaVarQryStr(request, "STATUS_REGRA"));
                }

                if (request.getParameter("offset") != null) {
                    try {
                        offset = Integer.parseInt(request.getParameter("offset"));
                    } catch (final Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }

                final int total = definicaoTaxaJurosController.lstCountDefinicaoTaxaJuros(criterio, responsavel);
                definicaoTaxaJuros = definicaoTaxaJurosController.listaDefinicaoRegraTaxaJuros(criterio, offset, size, responsavel);

                // Monta lista de parâmetros através dos parâmetros de request
                final Set<String> params = new HashSet<>(request.getParameterMap().keySet());
                params.remove("offset");
                final List<String> requestParams = new ArrayList<>(params);

                configurarPaginador(getLinkAction(), "rotulo.paginacao.titulo.usuario", total, size, requestParams, false, request, model);
            } catch (final DefinicaoTaxaJurosControllerException ex) {
                LOG.debug(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            // Carrega lista de órgão e de serviço
            carregarListaOrgao(request, session, model, responsavel);
            carregarListaServico(request, session, model, responsavel);

            model.addAttribute("podeAlterarRegraTaxaJuros", alterarRegraTaxaJuros);
            model.addAttribute("ativarTabela", ativarTabela);
            model.addAttribute("tabelaVazia", tabelaVazia);
            model.addAttribute("tipo", tipo);
            model.addAttribute("definicaoTaxaJuros", definicaoTaxaJuros);
            model.addAttribute("data", criterio.getAttribute("DATA"));
            model.addAttribute("statusRegra", criterio.getAttribute("STATUS_REGRA"));

        } catch (final DefinicaoTaxaJurosControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/manterTaxas/listarRegraTaxaJuros", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=iniciarTabela" })
    public String iniciarTabelaDefinicaoTaxaJuros(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignanteControllerException, DefinicaoTaxaJurosControllerException, InstantiationException, IllegalAccessException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String csaCodigo = responsavel.isCsa() ? responsavel.getCsaCodigo() : JspHelper.verificaVarQryStr(request, "CSA_CODIGO");

        try {
            definicaoTaxaJurosController.iniciarTabelaVigente(csaCodigo, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.iniciar.tabela.iniciada.regra.taxa.juros.sucesso", responsavel));
        } catch (final DefinicaoTaxaJurosControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return listar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=ativarTabela" })
    public String ativarTabelaDefinicaoTaxaJuros(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignanteControllerException, DefinicaoTaxaJurosControllerException, InstantiationException, IllegalAccessException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Recuperando o csaCodigo
        String csaCodigo;
        if (responsavel.isCsa()) {
            csaCodigo = responsavel.getCsaCodigo();
            model.addAttribute("csaCodigo", csaCodigo);
        } else if (responsavel.isSup()) {
            csaCodigo = request.getParameter("CSA_CODIGO");
            model.addAttribute("csaCodigo", csaCodigo);
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        try {
            definicaoTaxaJurosController.ativarTabelaIniciada(csaCodigo, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.ativar.tabela.iniciada.regra.taxa.juros.sucesso", responsavel));
        } catch (final DefinicaoTaxaJurosControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return listar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=excluirTabelaIniciada" })
    public String excluirTabelaIniciadaDefinicaoTaxaJuros(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws DefinicaoTaxaJurosControllerException, ConsignanteControllerException, InstantiationException, IllegalAccessException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Recuperando o csaCodigo
        String csaCodigo;
        if (responsavel.isCsa()) {
            csaCodigo = responsavel.getCsaCodigo();
            model.addAttribute("csaCodigo", csaCodigo);
        } else if (responsavel.isSup()) {
            csaCodigo = request.getParameter("CSA_CODIGO");
            model.addAttribute("csaCodigo", csaCodigo);
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        try {
            definicaoTaxaJurosController.excluirTabelaIniciada(csaCodigo, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.remover.tabela.iniciada.regra.taxa.juros.sucesso", responsavel));
        } catch (final DefinicaoTaxaJurosControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.remover.tabela.iniciada.regra.taxa.juros", responsavel));
            LOG.error(ex.getMessage(), ex);
        }

        return listar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=visualizar" })
    public String visualizarDefinicaoTaxaJuros(@RequestParam(value = "dtjCodigo", required = false, defaultValue = "") String dtjCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final boolean aplicarRegraCETTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_APLICAR_REGRAS_LIMITE_VARIACAO_CET_PARA_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Recuperando o csaCodigo
        String csaCodigo;
        if (responsavel.isCsa()) {
            csaCodigo = responsavel.getCsaCodigo();
            model.addAttribute("csaCodigo", csaCodigo);
        } else if (responsavel.isSup()) {
            csaCodigo = request.getParameter("CSA_CODIGO");
            model.addAttribute("csaCodigo", csaCodigo);
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String dtj_Codigo;
        // Nova regra de taxa de juros
        if ((dtjCodigo == null) || !"".equals(dtjCodigo)) {
            dtj_Codigo = dtjCodigo;
        } else {
            // Alterando uma regra de taxa de juros já existente
            dtj_Codigo = JspHelper.verificaVarQryStr(request, "dtjCodigo");
        }

        DefinicaoTaxaJuros definicaoTaxaJuros = null;
        List<TransferObject> orgaos = null;
        List<TransferObject> servicos = null;
        List<TransferObject> limitesTaxa = null;
        List<TransferObject> funcoes = null;
        boolean temLimiteTaxa = false;

        try {
            definicaoTaxaJuros = definicaoTaxaJurosController.findDefinicaoByCodigo(dtjCodigo, responsavel);

            //Busca lista de órgãos
            orgaos = consignanteController.lstOrgaos(new CustomTransferObject(), responsavel);

            //Busca lista de serviços
            servicos = convenioController.lstServicos(new CustomTransferObject(), responsavel);

            //Busca lista de Funcoes
            final String tpaFunCodigos = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_FUNCOES_PARA_DEFINICAO_TAXA_JUROS, responsavel);
            funcoes = usuarioController.findFuncoesRegraTaxa(tpaFunCodigos, responsavel);

        } catch (DefinicaoTaxaJurosControllerException | ConvenioControllerException | ConsignanteControllerException | UsuarioControllerException | ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (aplicarRegraCETTaxaJuros) {
            try {
                //Carregando limites de cada serviço
                limitesTaxa = limiteTaxaJurosController.listaLimiteTaxaJurosPorServico(servicos, null, null, null, responsavel);
                temLimiteTaxa = !TextHelper.isNull(limitesTaxa);
            } catch (final LimiteTaxaJurosControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            model.addAttribute("temLimiteTaxa", temLimiteTaxa);
            model.addAttribute("limitesTaxa", limitesTaxa);
        }

        model.addAttribute("novo", false);
        model.addAttribute("podeEditar", false);
        model.addAttribute("dtjCodigo", dtj_Codigo);
        model.addAttribute("definicaoTaxaJuros", definicaoTaxaJuros);
        model.addAttribute("orgaos", orgaos);
        model.addAttribute("servicos", servicos);
        model.addAttribute("funcoes", funcoes);
        model.addAttribute("aplicarRegraCETTaxaJuros", aplicarRegraCETTaxaJuros);
        
        boolean exibeCETMinMax = ParamSist.getBoolParamSist(CodedValues.TPC_CSA_PODE_CADASTRAR_CET_MININO_E_MAXIMO, responsavel);
        model.addAttribute("exibeCETMinMax", exibeCETMinMax);

        return viewRedirect("jsp/manterTaxas/alterarRegraTaxaJuros", request, session, model, responsavel);
    }

    private String getLinkAction() {
        return "../v3/editarRegraTaxaJuros?acao=consultar";
    }

    @RequestMapping(params = { "acao=novo" })
    public String novaDefinicaoTaxaJuros(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final boolean aplicarRegraCETTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_APLICAR_REGRAS_LIMITE_VARIACAO_CET_PARA_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        // Recuperando o csaCodigo
        String csaCodigo;
        if (responsavel.isCsa()) {
            csaCodigo = responsavel.getCsaCodigo();
            model.addAttribute("csaCodigo", csaCodigo);
        } else if (responsavel.isSup()) {
            csaCodigo = request.getParameter("CSA_CODIGO");
            model.addAttribute("csaCodigo", csaCodigo);
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        carregarListaServico(request, session, model, responsavel);

        List<TransferObject> orgaos = null;
        List<TransferObject> servicos = null;
        List<TransferObject> limitesTaxa = null;
        boolean temLimiteTaxa = false;
        List<TransferObject> funcoes = null;

        try {
            //Carregando lista de serviços vinculados a CSA
            servicos = simulacaoController.lstServicosParaCadastroTaxas(csaCodigo, responsavel);

            //Carregando lista de orgaos vinculados a CSA
            final String corCodigo = (responsavel.isCor() && !responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) ? responsavel.getCodigoEntidade() : null;
            orgaos = convenioController.getOrgCnvAtivo(csaCodigo, corCodigo, responsavel);

            final String tpaFunCodigos = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_FUNCOES_PARA_DEFINICAO_TAXA_JUROS, responsavel);
            funcoes = usuarioController.findFuncoesRegraTaxa(tpaFunCodigos, responsavel);

        } catch (SimulacaoControllerException | ConvenioControllerException | UsuarioControllerException | ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (aplicarRegraCETTaxaJuros) {
            try {
                //Carregando limites de cada serviço
                limitesTaxa = limiteTaxaJurosController.listaLimiteTaxaJurosPorServico(servicos, null, null, null, responsavel);
                temLimiteTaxa = !TextHelper.isNull(limitesTaxa);
            } catch (final LimiteTaxaJurosControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            model.addAttribute("temLimiteTaxa", temLimiteTaxa);
            model.addAttribute("limitesTaxa", limitesTaxa);
        }

        model.addAttribute("podeEditar", true);
        model.addAttribute("novo", true);
        model.addAttribute("definicaoTaxaJuros", new DefinicaoTaxaJuros());
        model.addAttribute("dtjCodigo", "");
        model.addAttribute("orgaos", orgaos);
        model.addAttribute("servicos", servicos);
        model.addAttribute("funcoes", funcoes);
        model.addAttribute("aplicarRegraCETTaxaJuros", aplicarRegraCETTaxaJuros);
        
        boolean exibeCETMinMax = ParamSist.getBoolParamSist(CodedValues.TPC_CSA_PODE_CADASTRAR_CET_MININO_E_MAXIMO, responsavel);
        model.addAttribute("exibeCETMinMax", exibeCETMinMax);

        return viewRedirect("jsp/manterTaxas/alterarRegraTaxaJuros", request, session, model, responsavel);
    }

    @RequestMapping(method = { RequestMethod.POST }, value = { "/getTaxaLimite" })
    @ResponseBody
    public ResponseEntity<String> getTaxaLimite(@RequestParam(value = "taxaJuros", required = true) BigDecimal taxaJuros, @RequestParam(value = "faixaPrazoInicial", required = true) Short faixaPrazoInicial, @RequestParam(value = "faixaPrazoFinal", required = true) Short faixaPrazoFinal, @RequestParam(value = "svcCodigo", required = true) String svcCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws LimiteTaxaJurosControllerException {
        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final List<TransferObject> svcCodigos = new ArrayList<>();
        final ServicoTransferObject svc = new ServicoTransferObject(svcCodigo);
        svcCodigos.add(svc);

        final List<TransferObject> limitesTaxa = limiteTaxaJurosController.listaLimiteTaxaJurosPorServico(svcCodigos, taxaJuros, faixaPrazoInicial, faixaPrazoFinal, responsavel);

        // Constrói o objeto Raiz do JSON
        final JsonArrayBuilder arrayLimitesTaxa = Json.createArrayBuilder();
        final JsonObjectBuilder jsonLimiteTaxa = Json.createObjectBuilder();

        for (final TransferObject limiteTaxa : limitesTaxa) {
            addValueToJsonObject(jsonLimiteTaxa, Columns.LTJ_CODIGO, limiteTaxa.getAttribute(Columns.LTJ_CODIGO));
            addValueToJsonObject(jsonLimiteTaxa, Columns.LTJ_SVC_CODIGO, limiteTaxa.getAttribute(Columns.LTJ_SVC_CODIGO));
            addValueToJsonObject(jsonLimiteTaxa, Columns.SVC_DESCRICAO, limiteTaxa.getAttribute(Columns.SVC_DESCRICAO));
            addValueToJsonObject(jsonLimiteTaxa, Columns.LTJ_PRAZO_REF, limiteTaxa.getAttribute(Columns.LTJ_PRAZO_REF));
            addValueToJsonObject(jsonLimiteTaxa, Columns.LTJ_JUROS_MAX, limiteTaxa.getAttribute(Columns.LTJ_JUROS_MAX));
            addValueToJsonObject(jsonLimiteTaxa, Columns.LTJ_VLR_REF, limiteTaxa.getAttribute(Columns.LTJ_VLR_REF));
            arrayLimitesTaxa.add(jsonLimiteTaxa);
        }

        return new ResponseEntity<>(arrayLimitesTaxa.build().toString(), HttpStatus.OK);
    }

    private void addValueToJsonObject(JsonObjectBuilder json, String nome, Object valor) {
        if (!TextHelper.isNull(valor)) {
            if (valor instanceof BigDecimal) {
                json.add(nome, (BigDecimal) valor);
            } else if (valor instanceof Short) {
                json.add(nome, (Short) valor);
            } else if (valor instanceof String) {
                json.add(nome, (String) valor);
            } else {
                json.add(nome, valor.toString());
            }
        } else {
            json.addNull(nome);
        }
    }

}
