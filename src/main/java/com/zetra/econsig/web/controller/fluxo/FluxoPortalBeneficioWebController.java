package com.zetra.econsig.web.controller.fluxo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.consignacao.SolicitacaoServidorHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.NaturezaServico;
import com.zetra.econsig.service.banner.BannerPublicidadeController;
import com.zetra.econsig.service.beneficios.BeneficioController;
import com.zetra.econsig.service.beneficios.ProvedorBeneficioController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.servico.NaturezaServicoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoFiltroPesquisaFluxoEnum;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.web.controller.paginainicial.VisualizarPaginaInicialWebController;

/**
 * <p>Title: FluxoPortalBeneficioWebController</p>
 * <p>Description: Web Controller para fluxo de beneficios para portal publico.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST })
public class FluxoPortalBeneficioWebController extends AbstractWebController {
    public static final int NUM_ITENS_PG_CAROUSEL_BANNERS = 3;

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(FluxoPortalBeneficioWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private NaturezaServicoController naturezaServicoController;

    @Autowired
    private BannerPublicidadeController bannerPublicidadeController;

    @Autowired
    private ProvedorBeneficioController provedorBeneficioController;

    @Autowired
    private BeneficioController beneficioController;

    @RequestMapping(value = { "/v3/fluxoPortal" })
    public String iniciar(@RequestParam("nse_codigo") String nseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return exibirProvedoresBeneficio(nseCodigo, null, null, request, response, session, model);
    }

    @RequestMapping(value = { "/v3/fluxoPortal" }, params = { "acao=pesquisar" })
    public String pesquisar(@RequestParam("nse_codigo") String nseCodigo, @RequestParam(value = "nFiltrarPor") String nFiltrarPor, @RequestParam(value = "nFiltro") String nFiltro, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Evitar pesquisas maior do que 100 caracteres
        if (nFiltro.length() > 100) {
            nFiltro = nFiltro.substring(0, 100);
        }

        return exibirProvedoresBeneficio(nseCodigo, nFiltrarPor, nFiltro, request, response, session, model);

    }

    private String exibirProvedoresBeneficio(String nseCodigo, String nFiltrarPor, String nFiltro, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            SynchronizerToken.saveToken(request);

            // verifica portal de benefícios habilitado
            boolean portalBeneficiosHabilitado = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_PORTAL_BENEFICIOS, responsavel);

            // verifica módulo de benefícios de saúde habilitado
            boolean moduloBeneficiosSaudeHabilitado = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, responsavel);

            if (!portalBeneficiosHabilitado && !(moduloBeneficiosSaudeHabilitado && CodedValues.NSE_BENEFICIO_SAUDE.equals(nseCodigo))) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.status.erro.acesso.negado", responsavel));
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.parametro.desabilitado", responsavel, CodedValues.TPC_HABILITA_PORTAL_BENEFICIOS));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            model.addAttribute("provedorLink", definirLinkOperacao(nseCodigo, null, null,null,model, session, responsavel));

            NaturezaServico nse = naturezaServicoController.buscaNaturezaServico(nseCodigo, responsavel);

            byte[] nseImagem = nse.getNseImagem();
            String nseDescricaoPortal = nse.getNseDescricaoPortal();
            String nseTituloDetalheTopo = nse.getNseTituloDetalheTopo();
            String nseTextoDetalheTopo = nse.getNseTextoDetalheTopo();
            String nseTituloDetalheRodape = nse.getNseTituloDetalheRodape();
            String nseTextoDetalheRodape = nse.getNseTextoDetalheRodape();
            String nseTituloCarouselProvedor = nse.getNseTituloCarouselProvedor();
            boolean existeDescricaoNatureza = !TextHelper.isNull(nseTituloDetalheTopo) || !TextHelper.isNull(nseTextoDetalheTopo) || !TextHelper.isNull(nseTituloDetalheRodape) || !TextHelper.isNull(nseTextoDetalheRodape) || !TextHelper.isNull(nseTituloCarouselProvedor);

            if (existeDescricaoNatureza) {
                model.addAttribute("nseImagem", nseImagem);
                model.addAttribute("nseDescricaoPortal", nseDescricaoPortal);
                model.addAttribute("nseTituloDetalheTopo", nseTituloDetalheTopo);
                model.addAttribute("nseTextoDetalheTopo", nseTextoDetalheTopo);
                model.addAttribute("nseTituloDetalheRodape", nseTituloDetalheRodape);
                model.addAttribute("nseTextoDetalheRodape", nseTextoDetalheRodape);
                model.addAttribute("nseTituloCarouselProvedor", nseTituloCarouselProvedor);
                model.addAttribute("nse_codigo", nseCodigo);

                CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.BPU_NSE_CODIGO, nse.getNseCodigo());
                int total = bannerPublicidadeController.countBannerPublicidade(criterio, responsavel);
                List<TransferObject> itensBannersCarousel = bannerPublicidadeController.listarBannerPublicidade(criterio, 0, total, responsavel);

                if (itensBannersCarousel != null && !itensBannersCarousel.isEmpty()) {
                    Collections.sort(itensBannersCarousel, (o1, o2) -> {
                        return ((Short) o1.getAttribute(Columns.BPU_ORDEM))
                                .compareTo((Short) o2.getAttribute(Columns.BPU_ORDEM));
                    });

                    int numPgCarouselBanners = itensBannersCarousel.size() / NUM_ITENS_PG_CAROUSEL_BANNERS;
                    numPgCarouselBanners = (numPgCarouselBanners == 0) ? 1 : ((itensBannersCarousel.size() - (numPgCarouselBanners * NUM_ITENS_PG_CAROUSEL_BANNERS)) > 0) ? numPgCarouselBanners + 1 : numPgCarouselBanners;
                    model.addAttribute("itensBannersCarousel", itensBannersCarousel);
                    model.addAttribute("numPgCarouselBanners", numPgCarouselBanners);
                    model.addAttribute("itensPorPgCarouselBanners", NUM_ITENS_PG_CAROUSEL_BANNERS);
                }

                return viewRedirect("jsp/fluxo/fluxoPropagandaPortalPublico", request, session, model, responsavel);
            }

            List<NaturezaServico> naturezasFilhas = naturezaServicoController.listaNaturezasByNseCodigoPai(responsavel.getOrgCodigo(), nse.getNseCodigo(), true, responsavel);

            if (naturezasFilhas == null || naturezasFilhas.isEmpty()) {
                List<TransferObject> consignatarias = consignatariaController.lstConsignatariaPorNaturezaServico(responsavel.getOrgCodigo(), nseCodigo, TipoFiltroPesquisaFluxoEnum.recuperaTipo(nFiltrarPor), nFiltro);
                List<TransferObject> consignatariasFiltroAgrupamento = new ArrayList<>();
                String csaCodigoAgrupa = null;

                for (TransferObject csa : consignatarias) {
                    String csaCodigo = (String) csa.getAttribute(Columns.CSA_CODIGO);
                    String corCodigo = (String) csa.getAttribute(Columns.PRO_COR_CODIGO);
                    boolean csaAgrupa = csa.getAttribute(Columns.PRO_AGRUPA) != null && csa.getAttribute(Columns.PRO_AGRUPA).toString().equals("S");

                    if(csaAgrupa && TextHelper.isNull(corCodigo)) {
                        csaCodigoAgrupa = csaCodigo;
                        consignatariasFiltroAgrupamento.add(csa);
                    } else if (!TextHelper.isNull(csaCodigoAgrupa) && !(csaCodigoAgrupa.equals(csaCodigo) && !TextHelper.isNull(corCodigo))) {
                        consignatariasFiltroAgrupamento.add(csa);
                    } else if (TextHelper.isNull(csaCodigoAgrupa) && TextHelper.isNull(corCodigo)){
                        consignatariasFiltroAgrupamento.add(csa);
                    } else if (!csaAgrupa && !TextHelper.isNull(corCodigo) && TextHelper.isNull(csaCodigoAgrupa)) {
                        consignatariasFiltroAgrupamento.add(csa);
                    }
                }

                model.addAttribute("consignatarias", consignatariasFiltroAgrupamento);
                model.addAttribute("nseDescricao", nse.getNseDescricao());
                model.addAttribute("nse_codigo", nseCodigo);

                return viewRedirect("jsp/fluxo/fluxoPortalPublico", request, session, model, responsavel);
            } else {
                session.setAttribute("naturezasFilhas", naturezasFilhas);
                session.setAttribute("nse", nse);
                ParamSession paramSession = ParamSession.getParamSession(session);
                paramSession.halfBack();
                return "forward:/v3/fluxoPortal?acao=visualizarSubCategorias&" + SynchronizerToken.generateToken4URL(request) + "&nseCodigo=" + nse.getNseCodigo();
            }

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(value = { "/v3/fluxoPortal" }, params = { "acao=visualizarSubCategorias" })
    public String visualizarSubCategorias(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String nseCodigo = request.getParameter("nseCodigo");

        // verifica portal de benefícios habilitado
        boolean portalBeneficiosHabilitado = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_PORTAL_BENEFICIOS, responsavel);

        // verifica módulo de benefícios de saúde habilitado
        boolean moduloBeneficiosSaudeHabilitado = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, responsavel);

        if (!portalBeneficiosHabilitado && !(moduloBeneficiosSaudeHabilitado && CodedValues.NSE_BENEFICIO_SAUDE.equals(nseCodigo))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.status.erro.acesso.negado", responsavel));
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.parametro.desabilitado", responsavel, CodedValues.TPC_HABILITA_PORTAL_BENEFICIOS));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<NaturezaServico> naturezasFilhas = (List<NaturezaServico>) session.getAttribute("naturezasFilhas");
        NaturezaServico nse = (NaturezaServico) session.getAttribute("nse");
        session.removeAttribute("naturezasFilhas");
        session.removeAttribute("nse");

        if (naturezasFilhas != null && !naturezasFilhas.isEmpty()) {
            Collections.sort(naturezasFilhas, (o1, o2) -> {
                return (o1.getNseOrdemBeneficio() != null ? o1.getNseOrdemBeneficio() : Short.valueOf("0")).compareTo(o2.getNseOrdemBeneficio() != null ? o2.getNseOrdemBeneficio() : Short.valueOf("0"));
            });

            int numPgCarousel = naturezasFilhas.size() / VisualizarPaginaInicialWebController.NUM_ITENS_PG_CAROUSEL_NSE;
            numPgCarousel = (numPgCarousel == 0) ? 1 : ((naturezasFilhas.size() - (numPgCarousel * VisualizarPaginaInicialWebController.NUM_ITENS_PG_CAROUSEL_NSE)) > 0) ? numPgCarousel + 1 : numPgCarousel;
            model.addAttribute("numPgCarousel", numPgCarousel);
            model.addAttribute("itensPorPgCarousel", VisualizarPaginaInicialWebController.NUM_ITENS_PG_CAROUSEL_NSE);
            model.addAttribute("tituloCarousel", ApplicationResourcesHelper.getMessage("rotulo.beneficio.conheca.beneficios.disponiveis", responsavel));
            model.addAttribute("nseDescricaoPai", nse.getNseDescricao());

            model.addAttribute("exibeSimulacaoPlano", responsavel.temPermissao(CodedValues.FUN_SIMULACAO_CONTRATO_BENEFICIO));
            model.addAttribute("exibiConsultaExtratoPlanoSaude", responsavel.temPermissao(CodedValues.FUN_CONSULTAR_RELACAO_BENEFICIOS));
            model.addAttribute("exibirSimularAlteracao", responsavel.temPermissao(CodedValues.FUN_SIMULACAO_ALTERACAO_CONTRATO_BENEFICIO));
        }
        model.addAttribute("isBeneficioSaude", CodedValues.NSE_BENEFICIO_SAUDE.equals(nseCodigo));
        model.addAttribute("isModuloBeneficiosSaudeHabilitado", moduloBeneficiosSaudeHabilitado);
        model.addAttribute("isPortalBeneficiosHabilitado", portalBeneficiosHabilitado);

        List<NaturezaServico> nseSaude = naturezasFilhas != null ? naturezasFilhas.stream().filter(nseFiltrado -> (nseFiltrado.getNseCodigo().equals(CodedValues.NSE_PLANO_DE_SAUDE) || nseFiltrado.getNseCodigo().equals(CodedValues.NSE_PLANO_ODONTOLOGICO))).collect(Collectors.toList()) : null;

        if (nseSaude != null && !nseSaude.isEmpty()) {
            model.addAttribute("isBeneficioSaude", true);
        }

        model.addAttribute("lstNseTo", naturezasFilhas);
        model.addAttribute("nse", nse);

        return viewRedirect("jsp/visualizarPaginaInicial/visualizarSubCategoriaBeneficios", request, session, model, responsavel);

    }

    @RequestMapping(value = { "/v3/fluxoPortal" }, params = { "acao=detalharBeneficio" })
    public String detalharBeneficio(@RequestParam("nse_codigo") String nseCodigo, @RequestParam("pro_codigo") String proCodigo, @RequestParam("csa_codigo") String csaCodigo, @RequestParam("pro_agrupa") boolean proAgrupa, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            NaturezaServico nse = naturezaServicoController.buscaNaturezaServico(nseCodigo, responsavel);
            TransferObject provedorBeneficio = provedorBeneficioController.buscarProvedorBeneficioPorProCodigo(proCodigo);


            // DESENV-17295 - Quando o provedor tem agrupamento o corCodigo é vazio, então sendo assim é necessário buscar todos os benefícios desta consignatária que existem no provedor com o corCodigo
            // preenchido e também buscar o beneficio com o corCodigo correspondente para fazer o agrupamento.
            if(proAgrupa) {
                List<TransferObject> provedoresCorrespondentes = provedorBeneficioController.buscarProvedorBeneficioPorCsaCodigoAgrupa(csaCodigo);

                if(provedoresCorrespondentes == null || provedoresCorrespondentes.isEmpty()) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.portal.beneficio.nao.existe.correspondentes", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                List<String> corCodigos = new ArrayList<>();
                for (TransferObject provedorCorrespondente : provedoresCorrespondentes) {
                    corCodigos.add((String) provedorCorrespondente.getAttribute(Columns.COR_CODIGO));
                }

                List<TransferObject> beneficiosCorrespondentes = beneficioController.lstBeneficioByCsaCodigoAndNaturezaServicoCorrespondentes(csaCodigo, corCodigos, nseCodigo, responsavel);
                List<TransferObject> benCorrrespondentes = new ArrayList<>();
                for(TransferObject beneficioCor : beneficiosCorrespondentes) {
                    String svcCodigo = (String) beneficioCor.getAttribute(Columns.SVC_CODIGO);
                    String corCodigo = (String) beneficioCor.getAttribute(Columns.COR_CODIGO);
                    String link = definirLinkOperacao(nseCodigo, csaCodigo, corCodigo, svcCodigo, model, session, responsavel);
                    beneficioCor.setAttribute("benLinkSimulaReserva", link);
                    benCorrrespondentes.add(beneficioCor);
                }
                model.addAttribute("provedorCorrespondentes", provedoresCorrespondentes);
                model.addAttribute("beneficiosCorrespondentes", benCorrrespondentes);

            } else {
                String corCodigo = (String) provedorBeneficio.getAttribute(Columns.COR_CODIGO);
                List<TransferObject> beneficios = beneficioController.lstBeneficioByCsaCodigoAndNaturezaServico(csaCodigo, corCodigo, nseCodigo, responsavel);
                model.addAttribute("provedorLink",definirLinkOperacao(nseCodigo, csaCodigo, corCodigo, null, model, session, responsavel));
                model.addAttribute("beneficios", beneficios);
            }

            model.addAttribute("provedorBeneficio", provedorBeneficio);
            model.addAttribute("nseDescricao", nse.getNseDescricao());
            model.addAttribute("provedorAgrupa", proAgrupa);

            return viewRedirect("jsp/fluxo/detalharBeneficio", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    private String definirLinkOperacao(String nseCodigo, String csaCodigo, String corCodigo, String svcCodigo, Model model, HttpSession session, AcessoSistema responsavel) throws ViewHelperException {
        boolean moduloBeneficiosSaudeHabilitado = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, responsavel);

        String link = "";

        switch (nseCodigo) {
            case CodedValues.NSE_PLANO_DE_SAUDE:
                if (moduloBeneficiosSaudeHabilitado) {
                    link = "../v3/simulacaoBeneficios?acao=planoSaude";
                }
                break;

            case CodedValues.NSE_PLANO_ODONTOLOGICO:
                if (moduloBeneficiosSaudeHabilitado) {
                    link = "../v3/simulacaoBeneficios?acao=planoSaude";
                }
                break;

            default:
                if (!TextHelper.isNull(csaCodigo)) {
                    String orgCodigo = responsavel.getOrgCodigo();
                    boolean temPermissaoSimulacao = responsavel.temPermissao(CodedValues.FUN_SIM_CONSIGNACAO);
                    boolean temPermissaoReserva = responsavel.temPermissao(CodedValues.FUN_RES_MARGEM);
                    boolean temPermissaoSolicitacao = responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO);
                    List<TransferObject> servicosReserva = SolicitacaoServidorHelper.lstServicos(orgCodigo, svcCodigo, csaCodigo, temPermissaoSimulacao, temPermissaoReserva, temPermissaoSolicitacao, false, nseCodigo,corCodigo, responsavel);
                    if (!servicosReserva.isEmpty()) {
                        boolean multiplosServicos = (servicosReserva.size() > 1);
                        if (!multiplosServicos) {
                            boolean fluxoReservaMargem = false;
                            fluxoReservaMargem = (boolean) servicosReserva.get(0).getAttribute("fluxoReservaMargem");
                            if (fluxoReservaMargem) {
                                String rseCodigo = responsavel.getRseCodigo();
                                String rseMatricula = responsavel.getRseMatricula();
                                String svcCodigoServico = (String) servicosReserva.get(0).getAttribute("svcCodigo");
                                link = "../v3/reservarMargem?acao=reservarMargem&" + "&SVC_CODIGO=" + svcCodigoServico + "&RSE_CODIGO=" + rseCodigo  + "&RSE_MATRICULA=" + rseMatricula + "&PORTAL_BENEFICIO=true";
                                link += !TextHelper.isNull(corCodigo) ? "&COR_CODIGO=" + corCodigo : "&CSA_CODIGO=" + csaCodigo +"&_skip_history_=true";
                            } else {
                                link = (String) servicosReserva.get(0).getAttribute("link");
                            }
                        } else if(!nseCodigo.equals(CodedValues.NSE_EMPRESTIMO)) {
                            link = "../v3/reservarMargem?acao=selecionarServico&NSE_CODIGO=" + nseCodigo;
                            link += !TextHelper.isNull(corCodigo) ? "&COR_CODIGO=" + corCodigo : "&CSA_CODIGO=" + csaCodigo;
                        } else {
                            link = "../v3/simularConsignacao?acao=listarServicos&RSE_CODIGO=" + responsavel.getRseCodigo();
                            link += !TextHelper.isNull(corCodigo) ? "&COR_CODIGO=" + corCodigo : "&CSA_CODIGO=" + csaCodigo;
                        }
                    }
                }
                break;
        }
        return link;
    }
}
