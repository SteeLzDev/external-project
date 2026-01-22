package com.zetra.econsig.web.controller.servico;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcCseTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.ParamTarifCseTO;
import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.dto.web.GrupoParametroServico;
import com.zetra.econsig.dto.web.ParametroServico;
import com.zetra.econsig.dto.web.RelacionamentoServico;
import com.zetra.econsig.exception.CoeficienteCorrecaoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.coeficiente.CoeficienteCorrecaoController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ManterServicoWebController</p>
 * <p>Description: Web Controller para manutenção de serviço.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(value = { "/v3/manterServico" }, method = { RequestMethod.POST })
public class ManterServicoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterServicoWebController.class);

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private CoeficienteCorrecaoController coeficienteCorrecaoController;

    @Autowired
    private MargemController margemController;

    @Autowired
    private SimulacaoController simulacaoController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private ServidorController servidorController;

    @RequestMapping(params = { "acao=iniciar" })
    private String listarServicos(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            SynchronizerToken.saveToken(request);
        }

        final boolean podeConsultarSvc = responsavel.temPermissao(CodedValues.FUN_CONS_SERVICOS);
        final boolean podeEditarSvc = responsavel.temPermissao(CodedValues.FUN_EDT_SERVICOS);
        final boolean podeConsultarPrazo = responsavel.temPermissao(CodedValues.FUN_CONS_PRAZO);
        final boolean podeEditarPrazo = responsavel.temPermissao(CodedValues.FUN_EDT_PRAZO);
        final boolean podeExcluirSvc = responsavel.temPermissao(CodedValues.FUN_EXCL_SERVICO);
        final boolean podeConsCoef = responsavel.temPermissao(CodedValues.FUN_CONS_COEFICIENTES);
        final boolean podeConsTaxaJuros = responsavel.temPermissao(CodedValues.FUN_CONS_TAXA_JUROS);
        final boolean podeConsLimiteTaxaJuros = responsavel.temPermissao(CodedValues.FUN_CONSULTAR_LIMITE_TAXA);
        final boolean exigeMotivoOperacao = FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EDT_SERVICOS, responsavel);
        final boolean temSimulacaoConsignacao = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel);
        final boolean permitePriorizarServico = ParamSist.paramEquals(CodedValues.TPC_PERMITE_PRIORIZAR_SERVICO, CodedValues.TPC_SIM, responsavel);
        final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
        final boolean podeEditarCnv = responsavel.temPermissao(CodedValues.FUN_EDT_CONVENIOS);
        final boolean podeConsultarCnv = responsavel.temPermissao(CodedValues.FUN_CONS_CONVENIOS);

        try {
            StringBuilder titulo = new StringBuilder().append(ApplicationResourcesHelper.getMessage("rotulo.servico.lista", responsavel));
            final String subTitulo = JspHelper.verificaVarQryStr(request, "titulo");
            final String codGrupo = JspHelper.verificaVarQryStr(request, "grupo");

            if (!"".equals(subTitulo)) {
                titulo.append(" - ").append(subTitulo);
            }
            final String parametros = "grupo=" + codGrupo + "&titulo=" + subTitulo;
            List<TransferObject> servicos = null;

            final String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
            int filtro_tipo = -1;

            try {
                filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
            } catch (final Exception ex1) {
            }

            try {
                final CustomTransferObject criterio = new CustomTransferObject();
                if (!"".equals(codGrupo)) {
                    criterio.setAttribute(Columns.SVC_TGS_CODIGO, codGrupo);
                }
                // -------------- Seta Criterio da Listagem ------------------
                // Bloqueado
                if (filtro_tipo == 0) {
                    criterio.setAttribute(Columns.SVC_ATIVO, CodedValues.STS_INATIVO);
                    // Desbloqueado
                } else if (filtro_tipo == 1) {
                    criterio.setAttribute(Columns.SVC_ATIVO, CodedValues.STS_ATIVO);
                    // Outros
                } else if (!"".equals(filtro) && (filtro_tipo != -1)) {
                    String campo = null;

                    switch (filtro_tipo) {
                        case 2:
                            campo = Columns.SVC_IDENTIFICADOR;
                            break;
                        case 3:
                            campo = Columns.SVC_DESCRICAO;
                            break;
                        default:
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                    criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
                }
                // ---------------------------------------
                final int total = convenioController.countServicos(criterio, responsavel);
                final int size = JspHelper.LIMITE;
                int offset = 0;
                try {
                    offset = Integer.parseInt(request.getParameter("offset"));
                } catch (final Exception ex) {
                }

                servicos = convenioController.lstServicos(criterio, offset, size, responsavel);
                model.addAttribute("servicos", servicos);

                final String linkListagem = "../v3/manterServico?acao=iniciar&FILTRO=" + filtro + "&FILTRO_TIPO=" + filtro_tipo;
                configurarPaginador(linkListagem, "rotulo.convenio.manutencao.titulo", total, size, null, false, request, model);

            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                LOG.error(ex.getMessage(), ex);
                servicos = null;
            }

            // Exibe Botao que leva ao rodapé
            final boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);

            model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);

            // Adiciona ao model para recuperar na página os objetos necessários
            model.addAttribute("podeConsultarSvc", podeConsultarSvc);
            model.addAttribute("podeEditarSvc", podeEditarSvc);
            model.addAttribute("podeConsultarPrazo", podeConsultarPrazo);
            model.addAttribute("podeEditarPrazo", podeEditarPrazo);
            model.addAttribute("podeExcluirSvc", podeExcluirSvc);
            model.addAttribute("podeConsCoef", podeConsCoef);
            model.addAttribute("podeConsTaxaJuros", podeConsTaxaJuros);
            model.addAttribute("podeConsLimiteTaxaJuros", podeConsLimiteTaxaJuros);
            model.addAttribute("exigeMotivoOperacao", exigeMotivoOperacao);
            model.addAttribute("temSimulacaoConsignacao", temSimulacaoConsignacao);
            model.addAttribute("permitePriorizarServico", permitePriorizarServico);
            model.addAttribute("temCET", temCET);
            model.addAttribute("titulo", titulo.toString());
            model.addAttribute("codGrupo", codGrupo);
            model.addAttribute("parametros", parametros);
            model.addAttribute("filtro", filtro);
            model.addAttribute("filtro_tipo", filtro_tipo);
            model.addAttribute("podeEditarCnv", podeEditarCnv);
            model.addAttribute("podeConsultarCnv", podeConsultarCnv);

        } catch (final Exception e) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            LOG.error(e.getMessage(), e);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return viewRedirect("jsp/manterServico/listarServicos", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=consultarServico" })
    public String consultarServico(@RequestParam(value = "svc", required = true, defaultValue = "") String svcCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String svc_codigo = JspHelper.verificaVarQryStr(request, "svc");
        final boolean podeEditarSvc = responsavel.temPermissao(CodedValues.FUN_EDT_SERVICOS);

        // Busca os parâmetros de sistema necessários ------------------------------------------------
        final boolean permiteCadIndice = ParamSist.paramEquals(CodedValues.TPC_PERMITE_CAD_INDICE, CodedValues.TPC_SIM, responsavel);
        final boolean temSimulacaoConsignacao = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel);
        final boolean temRelacionamentoCompTaxas = ParamSist.paramEquals(CodedValues.TPC_TEM_COMPARTILHAMENTO_TAXAS_POR_SVC, CodedValues.TPC_SIM, responsavel);
        final boolean temAlongamento = ParamSist.paramEquals(CodedValues.TPC_TEM_ALONGAMENTO_CONTRATO, CodedValues.TPC_SIM, responsavel);
        final boolean temModuloCompra = ParamSist.paramEquals(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, CodedValues.TPC_SIM, responsavel);
        final boolean temBloqueioCompraSerNovo = ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_COMPRA_SERVIDOR_NOVO, CodedValues.TPC_SIM, responsavel);
        final boolean temCarenciaDesbAutCsa = ParamSist.paramEquals(CodedValues.TPC_PERMITE_CARENCIA_DESBL_AUT_CSA_COMPRA, CodedValues.TPC_SIM, responsavel);
        final boolean temModuloFinancDividaCartao = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel);
        final boolean temModuloDescontoFila = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_DESCONTO_EM_FILA, CodedValues.TPC_SIM, responsavel);
        final boolean temModuloBeneficio = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, CodedValues.TPC_SIM, responsavel);
        final boolean permitePriorizarServico = ParamSist.paramEquals(CodedValues.TPC_PERMITE_PRIORIZAR_SERVICO, CodedValues.TPC_SIM, responsavel);
        final boolean temControleCompulsorios = ParamSist.paramEquals(CodedValues.TPC_TEM_CONTROLE_DE_ESTOQUE, CodedValues.TPC_SIM, responsavel) &&
                                          ParamSist.paramEquals(CodedValues.TPC_TEM_CONTROLE_DE_COMPULSORIOS, CodedValues.TPC_SIM, responsavel);
        // Parâmetro para exibir botão responsável por levar para o Rodapé da pagina
        final boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);

        try {
            // Busca os dados do serviço
            final ServicoTransferObject servico = convenioController.findServico(svcCodigo, responsavel);

            // Busca as naturezas de serviço
            final List<TransferObject> naturezas = servicoController.lstNaturezasServicos(false);

            // Busca os parâmetros de tarifação
            final List<TransferObject> paramTarif = parametroController.selectParamTarifCse(svcCodigo, responsavel);

            // Busca os parâmetros de serviço
            final List<TransferObject> lstParamSvcCse = parametroController.selectParamSvcCse(svcCodigo, CodedValues.TPC_SIM, responsavel);

            // Monta o Map de parâmetros de serviço com os valores existentes
            final Map<String, String> paramSvcCseMap = new HashMap<>();
            for (final TransferObject ctoParamSvcCse : lstParamSvcCse) {
                final String strTpsCodigo = ctoParamSvcCse.getAttribute(Columns.TPS_CODIGO) != null ? ctoParamSvcCse.getAttribute(Columns.TPS_CODIGO).toString() : "";
                String strPseVls = ctoParamSvcCse.getAttribute(Columns.PSE_VLR) != null ? ctoParamSvcCse.getAttribute(Columns.PSE_VLR).toString().trim() : "";
                if (CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE.equals(strTpsCodigo) ||
                        CodedValues.TPS_PERCENTUAL_LIMITE_DESCONTO.equals(strTpsCodigo) ||
                        CodedValues.TPS_QTD_ANEXOS_MIN_ADE_FEITA_POR_CSA_COR.equals(strTpsCodigo) ||
                        CodedValues.TPS_MARGEM_ERRO_LIMITE_SALDO_DEVEDOR.equals(strTpsCodigo) ||
                        CodedValues.TPS_MARGEM_ERRO_LIMITE_SALDO_ANT_RENEG.equals(strTpsCodigo) ||
                        CodedValues.TPS_MARGEM_ERRO_LIMITE_SALDO_ANT_COMPRA.equals(strTpsCodigo) ||
                        CodedValues.TPS_QTD_PROPOSTAS_PAGAMENTO_PARCEL_SALDO.equals(strTpsCodigo) ||
                        (CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO.equals(strTpsCodigo) && temSimulacaoConsignacao) ||
                        (CodedValues.TPS_ACAO_PARA_NAO_INF_SALDO_DV.equals(strTpsCodigo) && temCarenciaDesbAutCsa) ||
                        (CodedValues.TPS_ACAO_PARA_NAO_INF_PGT_SALDO.equals(strTpsCodigo) && temCarenciaDesbAutCsa) ||
                        (CodedValues.TPS_ACAO_PARA_NAO_LIQUIDACAO_ADE.equals(strTpsCodigo) && temCarenciaDesbAutCsa) ||
                        (CodedValues.TPS_ACAO_PARA_NAO_APR_SALDO_DV.equals(strTpsCodigo))) {
                    if ("".equals(strPseVls)) {
                        strPseVls = "-1";
                    }
                    strPseVls += ";" + (!TextHelper.isNull(ctoParamSvcCse.getAttribute(Columns.PSE_VLR_REF)) ? ctoParamSvcCse.getAttribute(Columns.PSE_VLR_REF).toString().trim() : "-1");
                }
                if (CodedValues.TPS_DATA_ABERTURA_TAXA.equals(strTpsCodigo)) {
                    if ("".equals(strPseVls)) {
                        strPseVls = "-1";
                    }
                    strPseVls += ";" + (!TextHelper.isNull(ctoParamSvcCse.getAttribute(Columns.PSE_VLR_REF)) ? ctoParamSvcCse.getAttribute(Columns.PSE_VLR_REF).toString().trim() : "U");
                }
                paramSvcCseMap.put(strTpsCodigo, strPseVls);
            }

            // Analisando se existe no request o novoNseCodigo, se existe significa que o usuario alterou no selectBox a natureza do serviço
            String svcNseCodigo = JspHelper.verificaVarQryStr(request, "novoNseCodigo");
            if (TextHelper.isNull(svcNseCodigo)) {
                svcNseCodigo = (!TextHelper.isNull(servico.getSvcNseCodigo()) ? servico.getSvcNseCodigo() : "");
            }

            final List<RelacionamentoServico> relacionamentoSvc = new ArrayList<>();

            // Recupera o relacionamento de um tipo de natureza com o tipo natureza
            final List<TransferObject> tipoNaturezaRelac = parametroController.selectTipoNaturezaEditavelServico(svcNseCodigo, responsavel);
            final Iterator<TransferObject> iterator = tipoNaturezaRelac.iterator();
            TransferObject ctoTnt = null;
            while (iterator.hasNext()) {
                ctoTnt = iterator.next();

                // Recuperando o tnt_codigo
                final String tntCodigo = ctoTnt.getAttribute(Columns.TNT_CODIGO) != null ? ctoTnt.getAttribute(Columns.TNT_CODIGO).toString() : null;

                // Recuperando a tnt_descrição
                final String tntDescricao = ctoTnt.getAttribute(Columns.TNT_DESCRICAO) != null ? ctoTnt.getAttribute(Columns.TNT_DESCRICAO).toString() : null;

                // Recupera os relacionamento já feitos.
                final List<TransferObject> relacionamentos = parametroController.lstRelacionamento(tntCodigo, svcCodigo, responsavel);

                // Definido o nome dinamicamente.
                final String hlRelacionamentoServicoNome = "svc_destino_tnt_".concat(tntCodigo);

                // Analisando a permissão
                String tntAltera = "N";
                if (responsavel.isCse()) {
                    tntAltera = ctoTnt.getAttribute(Columns.TNT_CSE_ALTERA) != null ? ctoTnt.getAttribute(Columns.TNT_CSE_ALTERA).toString() : "N";
                } else if (responsavel.isSup()) {
                    tntAltera = ctoTnt.getAttribute(Columns.TNT_SUP_ALTERA) != null ? ctoTnt.getAttribute(Columns.TNT_SUP_ALTERA).toString() : "N";
                }

                boolean exibeNaTela = true;

                // Conjutos de if para analisar se devemos exibir na tela o relacionamento ou não.
                if (((relacionamentos == null) || (relacionamentos.size() == 0)) || (CodedValues.TNT_RELACIONAMENTO_MODULO_BENEFICIO.contains(tntCodigo) && !temModuloBeneficio)) {
                    exibeNaTela = false;
                } else if (CodedValues.TNT_COMPRA.equals(tntCodigo) && !temModuloCompra) {
                    exibeNaTela = false;
                } else if (CodedValues.TNT_ALONGAMENTO.equals(tntCodigo) && !temAlongamento) {
                    exibeNaTela = false;
                } else if (CodedValues.TNT_CONTRATO_PREEXISTENTE_LIBERA_SIMULACAO.equals(tntCodigo) && !temSimulacaoConsignacao) {
                    exibeNaTela = false;
                } else if (CodedValues.TNT_COMPARTILHA_CADASTRO_TAXAS.equals(tntCodigo) && !temSimulacaoConsignacao && !temRelacionamentoCompTaxas) {
                    exibeNaTela = false;
                } else if (CodedValues.TNT_CONTRATO_PREEXISTENTE_LIBERA_COMPRA.equals(tntCodigo) && !temBloqueioCompraSerNovo && !temModuloCompra) {
                    exibeNaTela = false;
                } else if (CodedValues.TNT_UNICIDADE_CAD_INDICE.equals(tntCodigo) && !permiteCadIndice) {
                    exibeNaTela = false;
                } else if (CodedValues.TNT_FINANCIAMENTO_DIVIDA.equals(tntCodigo) && !temModuloFinancDividaCartao) {
                    exibeNaTela = false;
                } else if (CodedValues.TNT_SERVICOS_CONTROLE_DESCONTO_EM_FILA.equals(tntCodigo) && !temModuloDescontoFila) {
                    exibeNaTela = false;
                }

                final boolean isDisabled = !podeEditarSvc || CodedValues.TPC_NAO.equals(tntAltera);

                if (exibeNaTela) {
                    relacionamentoSvc.add(new RelacionamentoServico(tntCodigo, hlRelacionamentoServicoNome, tntDescricao, isDisabled, relacionamentos));
                }
            }

            final List<TransferObject> svcRelacionamentoCorrecao = parametroController.lstRelacionamentoSvcCorrecao(svc_codigo, responsavel);
            final List<TransferObject> lstGrupo = convenioController.lstGrupoServicos(false, responsavel);
            final List<TransferObject> servicos = convenioController.lstServicos(null, responsavel);
            final List<MargemTO> margens = margemController.lstMargemRaiz(responsavel);

            // Adiciona ao model para recuperar na página os objetos necessários
            model.addAttribute("servico", servico);
            model.addAttribute("naturezas", naturezas);
            model.addAttribute("paramTarif", paramTarif);
            model.addAttribute("paramSvcCseMap", paramSvcCseMap);
            model.addAttribute("relacionamentoSvc", relacionamentoSvc);
            model.addAttribute("permitePriorizarServico", permitePriorizarServico);
            model.addAttribute("temControleCompulsorios", temControleCompulsorios);
            model.addAttribute("podeEditarSvc", podeEditarSvc);
            model.addAttribute("svcNseCodigo", svcNseCodigo);
            model.addAttribute("svc_codigo", svc_codigo);
            model.addAttribute("svcRelacionamentoCorrecao", svcRelacionamentoCorrecao);
            model.addAttribute("lstGrupo", lstGrupo);
            model.addAttribute("servicos", servicos);
            model.addAttribute("margens", margens);
            model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);

            // Carrega os grupos de parâmetros de serviço
            final List<GrupoParametroServico> gruposParametros = new ArrayList<>();
            gruposParametros.add(carregarGrupoInformacoesBasicas(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoInformacoesAdicionais(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoRestricoesServico(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoRestricoesRenegociacaoCompra(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoRestricoesAlongamento(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoPrazoCancelamentoAut(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoControleCompra(paramSvcCseMap, svcNseCodigo, responsavel));
            gruposParametros.add(carregarGrupoSenhaServidor(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoTaxaJuros(paramSvcCseMap, svcCodigo, responsavel));
            gruposParametros.add(carregarGrupoRestricaoSaldoDevedor(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoControleCorrecaoSaldoDevedor(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoControleCompulsorios(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoCorrecaoValorPresente(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoControleTetoDesconto(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoControleGAP(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoSimuladorConsignacao(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoModuloBeneficios(paramSvcCseMap, responsavel));
            model.addAttribute("gruposParametros", gruposParametros);

            final boolean validaTaxaRenegociacao = paramSvcCseMap.containsKey(CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_RENEGOCIACAO);
            final boolean validaTaxaPortabilidade = paramSvcCseMap.containsKey(CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_PORTABILIDADE);

            model.addAttribute("validaTaxaRenegociacao", validaTaxaRenegociacao);
            model.addAttribute("validaTaxaPortabilidade", validaTaxaPortabilidade);

            final int total = servicoController.countOcorrenciaServico(svcCodigo, null, responsavel);
            if (total > 0) {
                final int size = JspHelper.LIMITE;
                int offset = 0;
                try {
                    offset = Integer.parseInt(request.getParameter("offset"));
                } catch (final Exception ex) {
                }

                final List<TransferObject> lstOcorrencias = servicoController.lstOcorrenciaServico(svcCodigo, null, offset, size, responsavel);
                model.addAttribute("lstOcorrencias", lstOcorrencias);

                final String linkListagem = request.getRequestURI() + "?acao=consultarServico&svc=" + svcCodigo;
                configurarPaginador(linkListagem, "rotulo.convenio.manutencao.titulo", total, size, null, false, request, model);
            }

            // Redireciona para a página de manutenção
            return viewRedirect("jsp/manterServico/editarServico", request, session, model, responsavel);
        } catch (final ZetraException e) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            LOG.error(e.getMessage(), e);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=editarServico" })
    public String editarServico(@RequestParam(value = "svc", required = true, defaultValue = "") String svcCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String svc_codigo = JspHelper.verificaVarQryStr(request, "svc");
        final boolean podeEditarSvc = responsavel.temPermissao(CodedValues.FUN_EDT_SERVICOS);

        // Busca os parâmetros de sistema necessários ------------------------------------------------
        final boolean permiteCadIndice = ParamSist.paramEquals(CodedValues.TPC_PERMITE_CAD_INDICE, CodedValues.TPC_SIM, responsavel);
        final boolean temSimulacaoConsignacao = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel);
        final boolean temRelacionamentoCompTaxas = ParamSist.paramEquals(CodedValues.TPC_TEM_COMPARTILHAMENTO_TAXAS_POR_SVC, CodedValues.TPC_SIM, responsavel);
        final boolean temAlongamento = ParamSist.paramEquals(CodedValues.TPC_TEM_ALONGAMENTO_CONTRATO, CodedValues.TPC_SIM, responsavel);
        final boolean temModuloCompra = ParamSist.paramEquals(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, CodedValues.TPC_SIM, responsavel);
        final boolean temBloqueioCompraSerNovo = ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_COMPRA_SERVIDOR_NOVO, CodedValues.TPC_SIM, responsavel);
        final boolean temCarenciaDesbAutCsa = ParamSist.paramEquals(CodedValues.TPC_PERMITE_CARENCIA_DESBL_AUT_CSA_COMPRA, CodedValues.TPC_SIM, responsavel);
        final boolean temModuloFinancDividaCartao = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel);
        final boolean temModuloDescontoFila = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_DESCONTO_EM_FILA, CodedValues.TPC_SIM, responsavel);
        final boolean temModuloBeneficio = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, CodedValues.TPC_SIM, responsavel);
        final boolean permitePriorizarServico = ParamSist.paramEquals(CodedValues.TPC_PERMITE_PRIORIZAR_SERVICO, CodedValues.TPC_SIM, responsavel);
        final boolean temControleCompulsorios = ParamSist.paramEquals(CodedValues.TPC_TEM_CONTROLE_DE_ESTOQUE, CodedValues.TPC_SIM, responsavel) &&
                                          ParamSist.paramEquals(CodedValues.TPC_TEM_CONTROLE_DE_COMPULSORIOS, CodedValues.TPC_SIM, responsavel);
        // Parâmetro para exibir botão responsável por levar para o Rodapé da pagina
        final boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);

        try {
            // Busca os dados do serviço
            final ServicoTransferObject servico = convenioController.findServico(svcCodigo, responsavel);

            // Busca as naturezas de serviço
            final List<TransferObject> naturezas = servicoController.lstNaturezasServicos(false);

            // Busca os parâmetros de tarifação
            final List<TransferObject> paramTarif = parametroController.selectParamTarifCse(svcCodigo, responsavel);

            // Busca os parâmetros de serviço
            final List<TransferObject> lstParamSvcCse = parametroController.selectParamSvcCse(svcCodigo, CodedValues.TPC_SIM, responsavel);

            // Monta o Map de parâmetros de serviço com os valores existentes
            final Map<String, String> paramSvcCseMap = new HashMap<>();
            for (final TransferObject ctoParamSvcCse : lstParamSvcCse) {
                final String strTpsCodigo = ctoParamSvcCse.getAttribute(Columns.TPS_CODIGO) != null ? ctoParamSvcCse.getAttribute(Columns.TPS_CODIGO).toString() : "";
                String strPseVls = ctoParamSvcCse.getAttribute(Columns.PSE_VLR) != null ? ctoParamSvcCse.getAttribute(Columns.PSE_VLR).toString().trim() : "";
                if (CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE.equals(strTpsCodigo) ||
                        CodedValues.TPS_PERCENTUAL_LIMITE_DESCONTO.equals(strTpsCodigo) ||
                        CodedValues.TPS_QTD_ANEXOS_MIN_ADE_FEITA_POR_CSA_COR.equals(strTpsCodigo) ||
                        CodedValues.TPS_MARGEM_ERRO_LIMITE_SALDO_DEVEDOR.equals(strTpsCodigo) ||
                        CodedValues.TPS_MARGEM_ERRO_LIMITE_SALDO_ANT_RENEG.equals(strTpsCodigo) ||
                        CodedValues.TPS_MARGEM_ERRO_LIMITE_SALDO_ANT_COMPRA.equals(strTpsCodigo) ||
                        CodedValues.TPS_QTD_PROPOSTAS_PAGAMENTO_PARCEL_SALDO.equals(strTpsCodigo) ||
                        (CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO.equals(strTpsCodigo) && temSimulacaoConsignacao) ||
                        (CodedValues.TPS_ACAO_PARA_NAO_INF_SALDO_DV.equals(strTpsCodigo) && temCarenciaDesbAutCsa) ||
                        (CodedValues.TPS_ACAO_PARA_NAO_INF_PGT_SALDO.equals(strTpsCodigo) && temCarenciaDesbAutCsa) ||
                        (CodedValues.TPS_ACAO_PARA_NAO_LIQUIDACAO_ADE.equals(strTpsCodigo) && temCarenciaDesbAutCsa) ||
                        (CodedValues.TPS_ACAO_PARA_NAO_APR_SALDO_DV.equals(strTpsCodigo))) {
                    if ("".equals(strPseVls)) {
                        strPseVls = "-1";
                    }
                    strPseVls += ";" + (!TextHelper.isNull(ctoParamSvcCse.getAttribute(Columns.PSE_VLR_REF)) ? ctoParamSvcCse.getAttribute(Columns.PSE_VLR_REF).toString().trim() : "-1");
                }
                if (CodedValues.TPS_DATA_ABERTURA_TAXA.equals(strTpsCodigo)) {
                    if ("".equals(strPseVls)) {
                        strPseVls = "-1";
                    }
                    strPseVls += ";" + (!TextHelper.isNull(ctoParamSvcCse.getAttribute(Columns.PSE_VLR_REF)) ? ctoParamSvcCse.getAttribute(Columns.PSE_VLR_REF).toString().trim() : "U");
                }
                paramSvcCseMap.put(strTpsCodigo, strPseVls);
            }

            // Analisando se existe no request o novoNseCodigo, se existe significa que o usuario alterou no selectBox a natureza do serviço
            String svcNseCodigo = JspHelper.verificaVarQryStr(request, "novoNseCodigo");
            if (TextHelper.isNull(svcNseCodigo)) {
                svcNseCodigo = (!TextHelper.isNull(servico.getSvcNseCodigo()) ? servico.getSvcNseCodigo() : "");
            }

            final List<RelacionamentoServico> relacionamentoSvc = new ArrayList<>();

            // Recupera o relacionamento de um tipo de natureza com o tipo natureza
            final List<TransferObject> tipoNaturezaRelac = parametroController.selectTipoNaturezaEditavelServico(svcNseCodigo, responsavel);
            final Iterator<TransferObject> iterator = tipoNaturezaRelac.iterator();
            TransferObject ctoTnt = null;
            while (iterator.hasNext()) {
                ctoTnt = iterator.next();

                // Recuperando o tnt_codigo
                final String tntCodigo = ctoTnt.getAttribute(Columns.TNT_CODIGO) != null ? ctoTnt.getAttribute(Columns.TNT_CODIGO).toString() : null;

                // Recuperando a tnt_descrição
                final String tntDescricao = ctoTnt.getAttribute(Columns.TNT_DESCRICAO) != null ? ctoTnt.getAttribute(Columns.TNT_DESCRICAO).toString() : null;

                // Recupera os relacionamento já feitos.
                final List<TransferObject> relacionamentos = parametroController.lstRelacionamento(tntCodigo, svcCodigo, responsavel);

                // Definido o nome dinamicamente.
                final String hlRelacionamentoServicoNome = "svc_destino_tnt_".concat(tntCodigo);

                // Analisando a permissão
                String tntAltera = "N";
                if (responsavel.isCse()) {
                    tntAltera = ctoTnt.getAttribute(Columns.TNT_CSE_ALTERA) != null ? ctoTnt.getAttribute(Columns.TNT_CSE_ALTERA).toString() : "N";
                } else if (responsavel.isSup()) {
                    tntAltera = ctoTnt.getAttribute(Columns.TNT_SUP_ALTERA) != null ? ctoTnt.getAttribute(Columns.TNT_SUP_ALTERA).toString() : "N";
                }

                boolean exibeNaTela = true;

                // Conjutos de if para analisar se devemos exibir na tela o relacionamento ou não.
                if (((relacionamentos == null) || (relacionamentos.size() == 0)) || (CodedValues.TNT_RELACIONAMENTO_MODULO_BENEFICIO.contains(tntCodigo) && !temModuloBeneficio)) {
                    exibeNaTela = false;
                } else if (CodedValues.TNT_COMPRA.equals(tntCodigo) && !temModuloCompra) {
                    exibeNaTela = false;
                } else if (CodedValues.TNT_ALONGAMENTO.equals(tntCodigo) && !temAlongamento) {
                    exibeNaTela = false;
                } else if (CodedValues.TNT_CONTRATO_PREEXISTENTE_LIBERA_SIMULACAO.equals(tntCodigo) && !temSimulacaoConsignacao) {
                    exibeNaTela = false;
                } else if (CodedValues.TNT_COMPARTILHA_CADASTRO_TAXAS.equals(tntCodigo) && !temSimulacaoConsignacao && !temRelacionamentoCompTaxas) {
                    exibeNaTela = false;
                } else if (CodedValues.TNT_CONTRATO_PREEXISTENTE_LIBERA_COMPRA.equals(tntCodigo) && !temBloqueioCompraSerNovo && !temModuloCompra) {
                    exibeNaTela = false;
                } else if (CodedValues.TNT_UNICIDADE_CAD_INDICE.equals(tntCodigo) && !permiteCadIndice) {
                    exibeNaTela = false;
                } else if (CodedValues.TNT_FINANCIAMENTO_DIVIDA.equals(tntCodigo) && !temModuloFinancDividaCartao) {
                    exibeNaTela = false;
                } else if (CodedValues.TNT_SERVICOS_CONTROLE_DESCONTO_EM_FILA.equals(tntCodigo) && !temModuloDescontoFila) {
                    exibeNaTela = false;
                }

                final boolean isDisabled = !podeEditarSvc || CodedValues.TPC_NAO.equals(tntAltera);

                if (exibeNaTela) {
                    relacionamentoSvc.add(new RelacionamentoServico(tntCodigo, hlRelacionamentoServicoNome, tntDescricao, isDisabled, relacionamentos));
                }
            }

            final List<TransferObject> svcRelacionamentoCorrecao = parametroController.lstRelacionamentoSvcCorrecao(svc_codigo, responsavel);
            final List<TransferObject> lstGrupo = convenioController.lstGrupoServicos(false, responsavel);
            final List<TransferObject> servicos = convenioController.lstServicos(null, responsavel);
            final List<MargemTO> margens = margemController.lstMargemRaiz(responsavel);

            // Adiciona ao model para recuperar na página os objetos necessários
            model.addAttribute("servico", servico);
            model.addAttribute("naturezas", naturezas);
            model.addAttribute("paramTarif", paramTarif);
            model.addAttribute("paramSvcCseMap", paramSvcCseMap);
            model.addAttribute("relacionamentoSvc", relacionamentoSvc);
            model.addAttribute("permitePriorizarServico", permitePriorizarServico);
            model.addAttribute("temControleCompulsorios", temControleCompulsorios);
            model.addAttribute("podeEditarSvc", podeEditarSvc);
            model.addAttribute("svcNseCodigo", svcNseCodigo);
            model.addAttribute("svc_codigo", svc_codigo);
            model.addAttribute("svcRelacionamentoCorrecao", svcRelacionamentoCorrecao);
            model.addAttribute("lstGrupo", lstGrupo);
            model.addAttribute("servicos", servicos);
            model.addAttribute("margens", margens);
            model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);

            final boolean validaTaxaRenegociacao = paramSvcCseMap.containsKey(CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_RENEGOCIACAO);
            final boolean validaTaxaPortabilidade = paramSvcCseMap.containsKey(CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_PORTABILIDADE);

            model.addAttribute("validaTaxaRenegociacao", validaTaxaRenegociacao);
            model.addAttribute("validaTaxaPortabilidade", validaTaxaPortabilidade);

            // Carrega os grupos de parâmetros de serviço
            final List<GrupoParametroServico> gruposParametros = new ArrayList<>();
            gruposParametros.add(carregarGrupoInformacoesBasicas(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoInformacoesAdicionais(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoRestricoesServico(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoRestricoesRenegociacaoCompra(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoRestricoesAlongamento(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoPrazoCancelamentoAut(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoControleCompra(paramSvcCseMap, svcNseCodigo, responsavel));
            gruposParametros.add(carregarGrupoSenhaServidor(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoTaxaJuros(paramSvcCseMap, svcCodigo, responsavel));
            gruposParametros.add(carregarGrupoRestricaoSaldoDevedor(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoControleCorrecaoSaldoDevedor(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoControleCompulsorios(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoCorrecaoValorPresente(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoControleTetoDesconto(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoControleGAP(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoSimuladorConsignacao(paramSvcCseMap, responsavel));
            gruposParametros.add(carregarGrupoModuloBeneficios(paramSvcCseMap, responsavel));
            model.addAttribute("gruposParametros", gruposParametros);

            final int total = servicoController.countOcorrenciaServico(svcCodigo, null, responsavel);
            if (total > 0) {
                final int size = JspHelper.LIMITE;
                int offset = 0;
                try {
                    offset = Integer.parseInt(request.getParameter("offset"));
                } catch (final Exception ex) {
                }

                final List<TransferObject> lstOcorrencias = servicoController.lstOcorrenciaServico(svcCodigo, null, offset, size, responsavel);
                model.addAttribute("lstOcorrencias", lstOcorrencias);

                final String linkListagem = request.getRequestURI() + "?acao=consultarServico&svc=" + svcCodigo;
                configurarPaginador(linkListagem, "rotulo.convenio.manutencao.titulo", total, size, null, false, request, model);
            }

            // Redireciona para a página de manutenção
            return viewRedirect("jsp/manterServico/editarServico", request, session, model, responsavel);
        } catch (final ZetraException e) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            LOG.error(e.getMessage(), e);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=consultarServicoParamSobrepoe" })
    public String consultarServicoParamSobrepoe(@RequestParam(value = "svc", required = true, defaultValue = "") String svcCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ParametroControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final boolean podeEditarSvc = responsavel.temPermissao(CodedValues.FUN_EDT_CNV_REG_SERVIDOR);

        final String svc_codigo = JspHelper.verificaVarQryStr(request, "svc");
        final String svc_identificador = JspHelper.verificaVarQryStr(request, "SVC_IDENTIFICADOR");
        final String svc_descricao = JspHelper.verificaVarQryStr(request, "SVC_DESCRICAO");
        final String rseCodigo = JspHelper.verificaVarQryStr(request, "rseCodigo");
        String bloqIncAdeMesma = "";
        // Busca os tipos de parâmetro de serviço disponíveis.
        final List<TransferObject> tiposParams = parametroController.lstTipoParamSvcSobrepoe(responsavel);
        final HashMap<Object, Boolean> parametrosSvcSobrepoe = new HashMap<>();
        final Iterator<TransferObject> itParam = tiposParams.iterator();
        while (itParam.hasNext()) {
            final CustomTransferObject paramSvc = (CustomTransferObject) itParam.next();
            if (responsavel.isCse()) {
                parametrosSvcSobrepoe.put(paramSvc.getAttribute(Columns.TPS_CODIGO), Boolean.valueOf((paramSvc.getAttribute(Columns.TPS_CSE_ALTERA) == null) || "".equals(paramSvc.getAttribute(Columns.TPS_CSE_ALTERA)) || CodedValues.TPC_SIM.equals(paramSvc.getAttribute(Columns.TPS_CSE_ALTERA))));
            } else if (responsavel.isSup()) {
                parametrosSvcSobrepoe.put(paramSvc.getAttribute(Columns.TPS_CODIGO), Boolean.valueOf((paramSvc.getAttribute(Columns.TPS_SUP_ALTERA) == null) || "".equals(paramSvc.getAttribute(Columns.TPS_SUP_ALTERA)) || CodedValues.TPC_SIM.equals(paramSvc.getAttribute(Columns.TPS_SUP_ALTERA))));
            }
        }

        // DESENV-11866 - Será tratado apenas o Paramêtro de Serviço 245 (Bloqueia inclusão no serviço caso o servidor possua parcela rejeitada da mesma natureza)
        final List<String> tpsCodigos = new ArrayList<>();
        if (parametrosSvcSobrepoe.containsKey(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA) && parametrosSvcSobrepoe.get(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA);
        }

        try {
            // Pega os valores gravados na base de dados.
            final List<TransferObject> parametros = parametroController.selectParamSvcSobrepoe(svc_codigo, rseCodigo, tpsCodigos, responsavel);
            final Iterator<TransferObject> it2 = parametros.iterator();
            CustomTransferObject next = null;
            while (it2.hasNext()) {
                next = (CustomTransferObject) it2.next();
                if (CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA.equals(next.getAttribute(Columns.PSR_TPS_CODIGO))) {
                    bloqIncAdeMesma = (next.getAttribute(Columns.PSR_VLR).toString());
                }
            }

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        final ParamSvcTO paramCse = parametroController.getParamSvcCseTO(svc_codigo, responsavel);
        boolean bloqIncAdeMesmaCse = paramCse.isTpsBloqIncAdeMesmaNsePrdRejeitada();

        final boolean isDisabled = !podeEditarSvc;

        final ParamSession paramSession = ParamSession.getParamSession(session);
        final String voltar = TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));
        model.addAttribute("voltar", voltar);

        model.addAttribute("svc_codigo", svc_codigo);
        model.addAttribute("svc_identificador", svc_identificador);
        model.addAttribute("svc_descricao", svc_descricao);
        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("bloqIncAdeMesma", bloqIncAdeMesma);
        model.addAttribute("bloqIncAdeMesmaCse", bloqIncAdeMesmaCse);
        model.addAttribute("isDisabled", isDisabled);
        model.addAttribute("parametrosSvcSobrepoe", parametrosSvcSobrepoe);

        return viewRedirect("jsp/editarServidor/editarServicoServidor", request, session, model, responsavel);
    }

    private GrupoParametroServico carregarGrupoInformacoesBasicas(Map<String, String> paramSvcCseMap, AcessoSistema responsavel) {
        final GrupoParametroServico grupo = new GrupoParametroServico(ApplicationResourcesHelper.getMessage("rotulo.servico.dados.basicos.contratos", responsavel));

        if (paramSvcCseMap.containsKey(CodedValues.TPS_INTEGRA_FOLHA)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_INTEGRA_FOLHA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.contrato.integra.folha", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_INTEGRA_FOLHA));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_SIM);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_INCIDE_MARGEM)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_INCIDE_MARGEM);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.contrato.incide.margem", responsavel));
            param.setCustom(true);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_VLR_LIMITE_ADE_SEM_MARGEM)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_VLR_LIMITE_ADE_SEM_MARGEM);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.limite.sem.margem.inclusao", responsavel));
            param.setDominio("FLOAT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_VLR_LIMITE_ADE_SEM_MARGEM));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_VLR_LIMITE_ADE_SEM_MARGEM_ALTER)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_VLR_LIMITE_ADE_SEM_MARGEM_ALTER);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.limite.sem.margem.alteracao", responsavel));
            param.setDominio("FLOAT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_VLR_LIMITE_ADE_SEM_MARGEM_ALTER));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIBE_TEXTO_EXPLICATIVO_VALOR_PRESTACAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIBE_TEXTO_EXPLICATIVO_VALOR_PRESTACAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.txt.explicativo.vlr", responsavel));
            param.setDominio("ALFA");
            param.setSize(300);
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIBE_TEXTO_EXPLICATIVO_VALOR_PRESTACAO));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_TIPO_VLR) && paramSvcCseMap.containsKey(CodedValues.TPS_ADE_VLR) && paramSvcCseMap.containsKey(CodedValues.TPS_ALTERA_ADE_VLR)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_TIPO_VLR);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.valor.autorizacao", responsavel) + "&nbsp;(" + ParamSvcTO.getDescricaoTpsTipoVlr(paramSvcCseMap.get(CodedValues.TPS_TIPO_VLR)) + ")");
            param.setCustom(true);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_MAX_PRAZO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_MAX_PRAZO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.qtde.parcelas", responsavel));
            param.setCustom(true);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.qtde.parcelas.renegociacao", responsavel));
            param.setCustom(true);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_MAX_PRAZO_RELATIVO_AOS_RESTANTES)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_MAX_PRAZO_RELATIVO_AOS_RESTANTES);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.max.prazo.relativo.restantes", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_MAX_PRAZO_RELATIVO_AOS_RESTANTES));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_VLR_MINIMO_CONTRATO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_VLR_MINIMO_CONTRATO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.vlr.minimo.contrato", responsavel) + " (" + ParamSvcTO.getDescricaoTpsTipoVlr(paramSvcCseMap.get(CodedValues.TPS_TIPO_VLR)) + ")");
            param.setDominio("FLOAT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_VLR_MINIMO_CONTRATO));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_VLR_MAXIMO_CONTRATO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_VLR_MAXIMO_CONTRATO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.vlr.maximo.contrato", responsavel) + " (" + ParamSvcTO.getDescricaoTpsTipoVlr(paramSvcCseMap.get(CodedValues.TPS_TIPO_VLR)) + ")");
            param.setDominio("FLOAT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_VLR_MAXIMO_CONTRATO));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.limite.aumento.valor.ade", responsavel));
            param.setDominio("COMPOSTO{" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.limite.aumento.valor.ade.valor", responsavel) + ":FLOAT|" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.limite.aumento.valor.ade.data", responsavel) + ":DATA}");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_LIMITE_AUMENTO_VALOR_ADE));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_SERVICO_TRATAMENTO_ESPECIAL_MARGEM)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_SERVICO_TRATAMENTO_ESPECIAL_MARGEM);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.servico.tratamento.especial.margem", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_SERVICO_TRATAMENTO_ESPECIAL_MARGEM));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PULA_INFORMACAO_VALOR_PRAZO_FLUXO_RESERVA)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PULA_INFORMACAO_VALOR_PRAZO_FLUXO_RESERVA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.pula.informacao.valor.prazo.solicitacao", responsavel));
            param.setDominio("SN");
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PULA_INFORMACAO_VALOR_PRAZO_FLUXO_RESERVA));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_DESCONTO_VIA_BOLETO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITE_DESCONTO_VIA_BOLETO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permite.desconto.via.boleto", responsavel));
            param.setDominio("ESCOLHA[" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.pagamento.via.boleto.escolha", responsavel) + "]");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_DESCONTO_VIA_BOLETO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPC_ENVIA_OTP_RECUPERACAO_SENHA_USU)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPC_ENVIA_OTP_RECUPERACAO_SENHA_USU);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.enviar.otp.via.email.ou.sms", responsavel));
            param.setDominio("ESCOLHA[" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.enviar.otp.via.email.ou.sms.escolha", responsavel) + "]");
            param.setValor(paramSvcCseMap.get(CodedValues.TPC_ENVIA_OTP_RECUPERACAO_SENHA_USU));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPC_ENVIA_OTP_RECUPERACAO_SENHA_SERVIDOR)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPC_ENVIA_OTP_RECUPERACAO_SENHA_SERVIDOR);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.enviar.otp.via.email.ou.sms", responsavel));
            param.setDominio("ESCOLHA[" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.enviar.otp.via.email.ou.sms.escolha", responsavel) + "]");
            param.setValor(paramSvcCseMap.get(CodedValues.TPC_ENVIA_OTP_RECUPERACAO_SENHA_SERVIDOR));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PARTICIPA_DA_CONTAGEM_DE_INCLUSAO_POR_DIA_CSA)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PARTICIPA_DA_CONTAGEM_DE_INCLUSAO_POR_DIA_CSA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.participa.contagem.inclusao.csa", responsavel));
            param.setDominio("SN");
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PARTICIPA_DA_CONTAGEM_DE_INCLUSAO_POR_DIA_CSA));
            grupo.addParametros(param);
        }

        return grupo;
    }

    private GrupoParametroServico carregarGrupoInformacoesAdicionais(Map<String, String> paramSvcCseMap, AcessoSistema responsavel) {
        final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
        final boolean permiteCadInfFin = ParamSist.paramEquals(CodedValues.TPC_PER_CAD_INF_FINANCEIRAS, CodedValues.TPC_SIM, responsavel);

        final GrupoParametroServico grupo = new GrupoParametroServico(ApplicationResourcesHelper.getMessage("rotulo.servico.dados.adicionais.contratos", responsavel));

        if (paramSvcCseMap.containsKey(CodedValues.TPS_CARENCIA_MINIMA)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_CARENCIA_MINIMA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.carencia.minima", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_CARENCIA_MINIMA));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_CARENCIA_MAXIMA)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_CARENCIA_MAXIMA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.carencia.maxima", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_CARENCIA_MAXIMA));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_CARENCIA_FINAL)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_CARENCIA_FINAL);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.margem.final", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_CARENCIA_FINAL));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PRAZO_CARENCIA_FINAL)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PRAZO_CARENCIA_FINAL);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.prazo.margem.final", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PRAZO_CARENCIA_FINAL));
            grupo.addParametros(param);
        }
        if (!temCET && paramSvcCseMap.containsKey(CodedValues.TPS_VALOR_MAX_TAC)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_VALOR_MAX_TAC);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.valor.tac.maximo", responsavel));
            param.setDominio("FLOAT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_VALOR_MAX_TAC));
            grupo.addParametros(param);
        }
        if (!temCET && permiteCadInfFin && paramSvcCseMap.containsKey(CodedValues.TPS_CAD_VALOR_TAC)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_CAD_VALOR_TAC);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.valor.tac.cadastro", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_CAD_VALOR_TAC));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (!temCET && permiteCadInfFin && paramSvcCseMap.containsKey(CodedValues.TPS_CAD_VALOR_IOF)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_CAD_VALOR_IOF);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.valor.iof.cadastro", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_CAD_VALOR_IOF));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (permiteCadInfFin && paramSvcCseMap.containsKey(CodedValues.TPS_CAD_VALOR_LIQUIDO_LIBERADO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_CAD_VALOR_LIQUIDO_LIBERADO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.cadastra.vlr.liquido.liberado", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_CAD_VALOR_LIQUIDO_LIBERADO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (!temCET && permiteCadInfFin && paramSvcCseMap.containsKey(CodedValues.TPS_CAD_VALOR_MENSALIDADE_VINC)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_CAD_VALOR_MENSALIDADE_VINC);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.mensalidade.vinculada.cadastro", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_CAD_VALOR_MENSALIDADE_VINC));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (!temCET && paramSvcCseMap.containsKey(CodedValues.TPS_EXIGE_SEGURO_PRESTAMISTA)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIGE_SEGURO_PRESTAMISTA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.seguro.prestamista", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIGE_SEGURO_PRESTAMISTA));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIBE_CAPITAL_DEVIDO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIBE_CAPITAL_DEVIDO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.capital.devido", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIBE_CAPITAL_DEVIDO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_NUMERAR_CONTRATOS_SERVIDOR)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_NUMERAR_CONTRATOS_SERVIDOR);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.exibe.ordem.inclusao.ade.svc", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_NUMERAR_CONTRATOS_SERVIDOR));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.informacao.bancaria.requer", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_VALIDAR_INF_BANCARIA_NA_RESERVA)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_VALIDAR_INF_BANCARIA_NA_RESERVA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.informacao.bancaria.validar", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_VALIDAR_INF_BANCARIA_NA_RESERVA));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIBE_INF_BANCARIA_SERVIDOR)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIBE_INF_BANCARIA_SERVIDOR);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.informacao.bancaria.exibir", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIBE_INF_BANCARIA_SERVIDOR));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIBE_TABELA_PRICE)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIBE_TABELA_PRICE);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.exibe.tabela.price", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIBE_TABELA_PRICE));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERCENTUAL_LIMITE_DESCONTO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERCENTUAL_LIMITE_DESCONTO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.percentual.limite.padrao", responsavel));
            param.setDominio("COMPOSTO{"
                    + ApplicationResourcesHelper.getMessage("rotulo.param.svc.percentual.limite.padrao.sem.saldo.residual", responsavel) + ":FLOAT|"
                    + ApplicationResourcesHelper.getMessage("rotulo.param.svc.percentual.limite.padrao.com.saldo.residual", responsavel) + ":FLOAT}");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERCENTUAL_LIMITE_DESCONTO));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.identificador.obrigatorio", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_IDENTIFICADOR_ADE_OBRIGATORIO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_MASCARA_IDENTIFICADOR_ADE)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_MASCARA_IDENTIFICADOR_ADE);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.mascara.identificador", responsavel));
            param.setDominio("ALFA");
            param.setSize(10);
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_MASCARA_IDENTIFICADOR_ADE));
            grupo.addParametros(param);
        }

        return grupo;
    }

    private GrupoParametroServico carregarGrupoRestricoesServico(Map<String, String> paramSvcCseMap, AcessoSistema responsavel) throws ServidorControllerException {
        final boolean temAnexoInclusaoContratos = ParamSist.paramEquals(CodedValues.TPC_PERMITE_ANEXO_INCLUSAO_CONTRATOS, CodedValues.TPC_SIM, responsavel);
        final boolean temAnexoConfirmarReserva = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_INCLUSAO_ANEXO_CONFIRMAR_RESERVA, responsavel);
        final boolean temTermoAdesao = ParamSist.paramEquals(CodedValues.TPC_TEM_TERMO_ADESAO, CodedValues.TPC_SIM, responsavel);
        final boolean temModuloDescontoFila = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_DESCONTO_EM_FILA, CodedValues.TPC_SIM, responsavel);
        final boolean exigeTermoAdesao = CodedValues.PSE_BOOLEANO_SIM.equals(paramSvcCseMap.get(CodedValues.TPS_EXIGE_ACEITE_TERMO_ADESAO));
        final boolean possibilitaReconhecimentoFacial = ParamSist.paramEquals(CodedValues.TPC_EXIGE_RECONHECIMENTO_FACIL_SOLICITACAO_SERVIDOR, CodedValues.TPC_SIM, responsavel);

        final GrupoParametroServico grupo = new GrupoParametroServico(ApplicationResourcesHelper.getMessage("rotulo.servico.restricao", responsavel));

        if (paramSvcCseMap.containsKey(CodedValues.TPS_REQUER_DEFERIMENTO_RESERVAS)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_REQUER_DEFERIMENTO_RESERVAS);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.requer.deferimento.reservas", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_REQUER_DEFERIMENTO_RESERVAS));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PODE_INCLUIR_NOVOS_CONTRATOS)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PODE_INCLUIR_NOVOS_CONTRATOS);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.novo.contrato", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PODE_INCLUIR_NOVOS_CONTRATOS));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_SIM);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.bloqueia.novo.contrato.prd.rejeitada.mesma.natureza", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_ALTERACAO_CONTRATOS)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITE_ALTERACAO_CONTRATOS);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.alterar.contrato", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_ALTERACAO_CONTRATOS));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_SIM);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_RENEGOCIACAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITE_RENEGOCIACAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.renegociar.contrato", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_RENEGOCIACAO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_SIM);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_CANCELAR_CONTRATOS)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITE_CANCELAR_CONTRATOS);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.cancelar.contrato", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_CANCELAR_CONTRATOS));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_SIM);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_LIQUIDAR_CONTRATOS)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITE_LIQUIDAR_CONTRATOS);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.liquidar.contrato", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_LIQUIDAR_CONTRATOS));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_SIM);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_LIQUIDAR_PARCELA)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITE_LIQUIDAR_PARCELA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.liquidar.parcela", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_LIQUIDAR_PARCELA));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_SIM);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_AUM_VLR_PRZ_CONSIGNACAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITE_AUM_VLR_PRZ_CONSIGNACAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.aumento.valor.prazo", responsavel));
            param.setDominio("ESCOLHA[" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.aumento.valor.prazo.escolha", responsavel) + "]");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_AUM_VLR_PRZ_CONSIGNACAO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_IMPORTACAO_LOTE)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITE_IMPORTACAO_LOTE);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.importacao.lote", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_IMPORTACAO_LOTE));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_DATA_RETROATIVA_IMP_LOTE)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITE_DATA_RETROATIVA_IMP_LOTE);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.data.retroativa", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_DATA_RETROATIVA_IMP_LOTE));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIBE_MARGEM_CRITICA_LOTE)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIBE_MARGEM_CRITICA_LOTE);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.exibe.margem.disponivel.importacao.lote", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIBE_MARGEM_CRITICA_LOTE));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_INCLUI_ALTERANDO_MESMO_PERIODO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_INCLUI_ALTERANDO_MESMO_PERIODO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.alterar.contrato.mesmo.periodo", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_INCLUI_ALTERANDO_MESMO_PERIODO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_INCLUI_ALTERANDO_QUALQUER_PERIODO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_INCLUI_ALTERANDO_QUALQUER_PERIODO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.alterar.contrato.independente.periodo", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_INCLUI_ALTERANDO_QUALQUER_PERIODO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_INCLUI_ALTERANDO_SOMENTE_MESMO_PRAZO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_INCLUI_ALTERANDO_SOMENTE_MESMO_PRAZO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.alterar.contrato.prazo.igual", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_INCLUI_ALTERANDO_SOMENTE_MESMO_PRAZO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_INSERIR_VIRA_ALTERAR_POR_ADE_INDENTIFICADOR_IGUAL)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_INSERIR_VIRA_ALTERAR_POR_ADE_INDENTIFICADOR_IGUAL);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.insere.altera.ade.adeidentificador.igual", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_INSERIR_VIRA_ALTERAR_POR_ADE_INDENTIFICADOR_IGUAL));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_OP_INSERE_ALTERA_USA_MAIOR_PRAZO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_OP_INSERE_ALTERA_USA_MAIOR_PRAZO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.alterar.contrato.usa.maior.prazo", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_OP_INSERE_ALTERA_USA_MAIOR_PRAZO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PRESERVA_DATA_ALTERACAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PRESERVA_DATA_ALTERACAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.preservar.prioridade.alteracao", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PRESERVA_DATA_ALTERACAO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_SIM);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PRESERVA_DATA_RENEGOCIACAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PRESERVA_DATA_RENEGOCIACAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.preservar.data.inicial.renegociacao", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PRESERVA_DATA_RENEGOCIACAO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PRESERVA_DATA_MAIS_ANTIGA_RENEG)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PRESERVA_DATA_MAIS_ANTIGA_RENEG);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.preservar.data.inicial.mais.antiga.renegociacao", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PRESERVA_DATA_MAIS_ANTIGA_RENEG));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PER_RESTRICAO_CAD_NOVA_ADE_CNV_RSE)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PER_RESTRICAO_CAD_NOVA_ADE_CNV_RSE);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.periodo.restricao.novas.consignacoes", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PER_RESTRICAO_CAD_NOVA_ADE_CNV_RSE));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_BLOQUEIA_INCLUSAO_ADE_MESMO_PERIODO_NSE_RSE)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_BLOQUEIA_INCLUSAO_ADE_MESMO_PERIODO_NSE_RSE);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.bloqueia.inclusao.ade.mesmo.periodo.nse.rse", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_BLOQUEIA_INCLUSAO_ADE_MESMO_PERIODO_NSE_RSE));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.periodo.restricao.novas.consignacoes.duplicidade", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITIR_DUPLICIDADE_WEB_MOTIVADA_USUARIO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITIR_DUPLICIDADE_WEB_MOTIVADA_USUARIO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.duplicidade.motivada.usuario.web", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITIR_DUPLICIDADE_WEB_MOTIVADA_USUARIO));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_QTE_DIAS_BLOQUEAR_RESERVA_APOS_LIQ)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_QTE_DIAS_BLOQUEAR_RESERVA_APOS_LIQ);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.qtde.dias.bloquear.reserva.apos.liquidacao", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_QTE_DIAS_BLOQUEAR_RESERVA_APOS_LIQ));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_QUANTIDADE_MAXIMA_CONTRATOS_SVC)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_QUANTIDADE_MAXIMA_CONTRATOS_SVC);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.quantidade.maxima.contrato", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_QUANTIDADE_MAXIMA_CONTRATOS_SVC));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIBE_CONTRATO_SERVIDOR)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIBE_CONTRATO_SERVIDOR);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.nao.exibir.para.servidor", responsavel));
            param.setDominio("ESCOLHA["
                    + CodedValues.PSE_EXIBIR_TODOS_CONTRATOS_SERVIDOR + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.nao.exibir.para.servidor.sim", responsavel) + ";"
                    + CodedValues.PSE_EXIBIR_SOMENTE_CONTRATOS_ATIVOS_SERVIDOR + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.nao.exibir.para.servidor.ativos", responsavel) + ";"
                    + CodedValues.PSE_NAO_EXIBIR_CONTRATOS_SERVIDOR  + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.nao.exibir.para.servidor.nao", responsavel) + "]");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIBE_CONTRATO_SERVIDOR));
            param.setValorPadrao(CodedValues.PSE_EXIBIR_TODOS_CONTRATOS_SERVIDOR);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_SERVIDOR_SOLICITAR)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITE_SERVIDOR_SOLICITAR);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.servidor.pode.solicitar.contrato", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_SERVIDOR_SOLICITAR));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_DEFERE_AUT_CONTRATO_INCLUIDO_SER)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_DEFERE_AUT_CONTRATO_INCLUIDO_SER);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.deferimento.automatico.servidor", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_DEFERE_AUT_CONTRATO_INCLUIDO_SER));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_SERVIDOR_LIQUIDA_CONTRATO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_SERVIDOR_LIQUIDA_CONTRATO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.servidor.liquidar.contrato", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_SERVIDOR_LIQUIDA_CONTRATO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_SERVIDOR_ALTERA_CONTRATO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_SERVIDOR_ALTERA_CONTRATO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.servidor.alterar.contrato", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_SERVIDOR_ALTERA_CONTRATO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_SER_BLOQUEAR_SERVICO_VERBA)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITE_SER_BLOQUEAR_SERVICO_VERBA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.servidor.bloquear.servico", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_SER_BLOQUEAR_SERVICO_VERBA));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (temTermoAdesao && paramSvcCseMap.containsKey(CodedValues.TPS_EXIGE_ACEITE_TERMO_ADESAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIGE_ACEITE_TERMO_ADESAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.exigir.termo.adesao", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIGE_ACEITE_TERMO_ADESAO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (temTermoAdesao && exigeTermoAdesao && paramSvcCseMap.containsKey(CodedValues.TPS_EXIGE_TERMO_DE_ADESAO_RESERVAR_MARGEM_ANTES_DE_VALORES_OPERACAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIGE_TERMO_DE_ADESAO_RESERVAR_MARGEM_ANTES_DE_VALORES_OPERACAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.exigir.termo.adesao.antes", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIGE_TERMO_DE_ADESAO_RESERVAR_MARGEM_ANTES_DE_VALORES_OPERACAO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_MSG_EXIBIR_SOLICITACAO_SERVIDOR)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_MSG_EXIBIR_SOLICITACAO_SERVIDOR);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.mensagem.termo.adesao", responsavel));
            param.setDominio("ALFA");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_MSG_EXIBIR_SOLICITACAO_SERVIDOR));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_MSG_EXIBIR_INCLUSAO_ALTERACAO_ADE_CSA)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_MSG_EXIBIR_INCLUSAO_ALTERACAO_ADE_CSA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.mensagem.inclusao.ade.csa", responsavel));
            param.setDominio("ALFA");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_MSG_EXIBIR_INCLUSAO_ALTERACAO_ADE_CSA));
            param.setMaxSize(500);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_BUSCA_BOLETO_EXTERNO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_BUSCA_BOLETO_EXTERNO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.boleto.personalizado", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_BUSCA_BOLETO_EXTERNO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_VALIDAR_DATA_NASCIMENTO_NA_RESERVA)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_VALIDAR_DATA_NASCIMENTO_NA_RESERVA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.validar.data.nascimento", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_VALIDAR_DATA_NASCIMENTO_NA_RESERVA));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_ATUALIZA_ADE_VLR_NO_RETORNO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_ATUALIZA_ADE_VLR_NO_RETORNO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.atualizar.valor.parcela.retorno", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_ATUALIZA_ADE_VLR_NO_RETORNO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.atualizar.valor.parcela.alteracao.margem", responsavel));
            param.setDominio("ESCOLHA["
                    + CodedValues.PSE_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM_NUNCA    + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.atualizar.valor.parcela.alteracao.margem.nunca", responsavel) + ";"
                    + CodedValues.PSE_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM_POSITIVA + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.atualizar.valor.parcela.alteracao.margem.positiva", responsavel) + ";"
                    + CodedValues.PSE_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM_NEGATIVA + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.atualizar.valor.parcela.alteracao.margem.negativa", responsavel) + ";"
                    + CodedValues.PSE_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM_SEMPRE   + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.atualizar.valor.parcela.alteracao.margem.sempre", responsavel) + "]");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM));
            param.setValorPadrao(CodedValues.PSE_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM_NUNCA);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_ALTERAR_ADE_COM_BLOQUEIO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITE_ALTERAR_ADE_COM_BLOQUEIO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.alterar.contrato.convenio.bloqueado", responsavel));
            param.setDominio("ESCOLHA["
                    + CodedValues.PSE_PERMITE_ALTERAR_ADE_COM_BLOQUEIO + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.alterar.contrato.convenio.bloqueado.sim", responsavel) + ";"
                    + CodedValues.PSE_PERMITE_ALTERAR_ADE_COM_BLOQUEIO_APENAS_REDUCAO  + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.alterar.contrato.convenio.bloqueado.sim.apenas.reducao", responsavel) + ";"
                    + CodedValues.PSE_NAO_PERMITE_ALTERAR_ADE_COM_BLOQUEIO + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.alterar.contrato.convenio.bloqueado.nao", responsavel) + "]");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_ALTERAR_ADE_COM_BLOQUEIO));
            param.setValorPadrao(CodedValues.PSE_NAO_PERMITE_ALTERAR_ADE_COM_BLOQUEIO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_INCLUIR_ADE_RSE_BLOQUEADO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITE_INCLUIR_ADE_RSE_BLOQUEADO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.incluir.contrato.servidor.bloqueado", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_INCLUIR_ADE_RSE_BLOQUEADO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_ALTERAR_ADE_RSE_BLOQUEADO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITE_ALTERAR_ADE_RSE_BLOQUEADO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.alterar.contrato.servidor.bloqueado", responsavel));
            param.setDominio("ESCOLHA["
                    + CodedValues.PSE_PERMITE_ALTERAR_ADE_RSE_BLOQUEADO + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.alterar.contrato.servidor.bloqueado.sim", responsavel) + ";"
                    + CodedValues.PSE_PERMITE_ALTERAR_ADE_RSE_BLOQUEADO_APENAS_REDUCAO  + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.alterar.contrato.servidor.bloqueado.sim.apenas.reducao", responsavel) + ";"
                    + CodedValues.PSE_NAO_PERMITE_ALTERAR_ADE_RSE_BLOQUEADO + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.alterar.contrato.servidor.bloqueado.nao", responsavel) + "]");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_ALTERAR_ADE_RSE_BLOQUEADO));
            param.setValorPadrao(CodedValues.PSE_NAO_PERMITE_ALTERAR_ADE_RSE_BLOQUEADO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_ALTERAR_ADE_RSE_EXCLUIDO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITE_ALTERAR_ADE_RSE_EXCLUIDO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.alterar.contrato.servidor.excluido", responsavel));
            param.setDominio("ESCOLHA["
                    + CodedValues.PSE_PERMITE_ALTERAR_ADE_RSE_EXCLUIDO_APENAS_REDUCAO  + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.alterar.contrato.servidor.excluido.sim.apenas.reducao", responsavel) + ";"
                    + CodedValues.PSE_NAO_PERMITE_ALTERAR_ADE_RSE_EXCLUIDO + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.alterar.contrato.servidor.excluido.nao", responsavel) + "]");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_ALTERAR_ADE_RSE_EXCLUIDO));
            param.setValorPadrao(CodedValues.PSE_NAO_PERMITE_ALTERAR_ADE_RSE_EXCLUIDO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_ALTERAR_VLR_LIBERADO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITE_ALTERAR_VLR_LIBERADO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.alterar.valor.liberado", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_ALTERAR_VLR_LIBERADO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_SIM);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_LIMITA_CAPITAL_DEVIDO_A_BASE_CALCULO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_LIMITA_CAPITAL_DEVIDO_A_BASE_CALCULO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.limitar.capital.devido.valor.base", responsavel));
            param.setDominio(("ESCOLHA["
                    + CodedValues.PSE_VLR_SEM_LIMITACAO + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.nao.aplica", responsavel) + ";"
                    + CodedValues.PSE_VLR_LIMITA_BASE_CALCULO_VLR_LIBERADO + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.limita.base.vlr.liberado", responsavel) +  ";"
                    + CodedValues.PSE_VLR_LIMITA_BASE_CALCULO_CAPITAL_DEVIDO + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.limita.base.capital.devido", responsavel) + "]"));
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_LIMITA_CAPITAL_DEVIDO_A_BASE_CALCULO));
            param.setValorPadrao(CodedValues.PSE_VLR_SEM_LIMITACAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_RETEM_MARGEM_SVC_PERCENTUAL)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_RETEM_MARGEM_SVC_PERCENTUAL);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.descontar.valor.absoluto.margem", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_RETEM_MARGEM_SVC_PERCENTUAL));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PRENDE_MARGEM_LIQ_ADE_PRZ_INDET_ATE_CARGA_MARGEM)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PRENDE_MARGEM_LIQ_ADE_PRZ_INDET_ATE_CARGA_MARGEM);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.prender.margem.ade.liquidada.prz.indeterminado.ate.carga.margem", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PRENDE_MARGEM_LIQ_ADE_PRZ_INDET_ATE_CARGA_MARGEM));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_BASE_CALC_RETENCAO_SVC_PERCENTUAL)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_BASE_CALC_RETENCAO_SVC_PERCENTUAL);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.servico.base.calculo.retencao.valor.absoluto", responsavel));
            param.setCombo(true);
            param.setComboValues(servidorController.lstTipoBaseCalculo(responsavel));
            param.setCampoValor(Columns.TBC_CODIGO);
            param.setCampoLabel(Columns.TBC_DESCRICAO);
            param.setLabelNaoSelecionado(ApplicationResourcesHelper.getMessage("rotulo.campo.base.calculo", responsavel));
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_BASE_CALC_RETENCAO_SVC_PERCENTUAL));
            grupo.addParametros(param);
        }
        if (temAnexoInclusaoContratos && paramSvcCseMap.containsKey(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_CSE_ORG_SUP)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_CSE_ORG_SUP);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.exigir.anexar.arquivo.inclusao", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_CSE_ORG_SUP));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (temAnexoInclusaoContratos && paramSvcCseMap.containsKey(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_CSA_COR)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_CSA_COR);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.exigir.anexar.arquivo.inclusao.csa.cor", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_CSA_COR));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (temAnexoInclusaoContratos && paramSvcCseMap.containsKey(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_SER)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_SER);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.exigir.anexar.arquivo.inclusao.ser", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_SER));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (temAnexoInclusaoContratos && paramSvcCseMap.containsKey(CodedValues.TPS_QUANTIDADE_MINIMA_ANEXO_INCLUSAO_CONTRATOS)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_QUANTIDADE_MINIMA_ANEXO_INCLUSAO_CONTRATOS);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.quantidade.minima.anexo.inclusao", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_QUANTIDADE_MINIMA_ANEXO_INCLUSAO_CONTRATOS));
            grupo.addParametros(param);
        }
        if (temAnexoConfirmarReserva && paramSvcCseMap.containsKey(CodedValues.TPS_ANEXO_CONFIRMACAO_RESERVA_OBRIGATORIO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_ANEXO_CONFIRMACAO_RESERVA_OBRIGATORIO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.exigir.anexar.arquivo.confirmacao.reserva", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_ANEXO_CONFIRMACAO_RESERVA_OBRIGATORIO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_LIQUIDAR_ADE_SUSPENSA)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITE_LIQUIDAR_ADE_SUSPENSA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitir.liquidar.ade.suspensa", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_LIQUIDAR_ADE_SUSPENSA));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (temModuloDescontoFila) {
            if (paramSvcCseMap.containsKey(CodedValues.TPS_BASE_CALC_DESCONTO_EM_FILA)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_BASE_CALC_DESCONTO_EM_FILA);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.base.calculo.desconto.fila", responsavel));
                param.setCombo(true);
                param.setComboValues(servidorController.lstTipoBaseCalculo(responsavel));
                param.setCampoValor(Columns.TBC_CODIGO);
                param.setCampoLabel(Columns.TBC_DESCRICAO);
                param.setLabelNaoSelecionado(ApplicationResourcesHelper.getMessage("rotulo.campo.base.calculo", responsavel));
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_BASE_CALC_DESCONTO_EM_FILA));
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_PERCENTUAL_BASE_CALC_DESCONTO_EM_FILA)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_PERCENTUAL_BASE_CALC_DESCONTO_EM_FILA);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.percentual.base.calculo.desconto.fila", responsavel));
                param.setDominio("FLOAT");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERCENTUAL_BASE_CALC_DESCONTO_EM_FILA));
                grupo.addParametros(param);
            }
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIBE_MSG_RESERVA_MESMA_VERBA_CSA_COR)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIBE_MSG_RESERVA_MESMA_VERBA_CSA_COR);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.exibe.mensagem.contrato.ativo.mesmo.servidor.verba", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIBE_MSG_RESERVA_MESMA_VERBA_CSA_COR));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_ALTERA_ADE_COM_MARGEM_NEGATIVA)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_ALTERA_ADE_COM_MARGEM_NEGATIVA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.altera.vlr.prazo.ade.mrg.negativa", responsavel));
            param.setDominio("ESCOLHA[" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.altera.vlr.prazo.ade.mrg.negativa.escolha", responsavel) + "]");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_ALTERA_ADE_COM_MARGEM_NEGATIVA));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_VALOR_MAX_ENVIO_PARA_DESCONTO_FOLHA)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_VALOR_MAX_ENVIO_PARA_DESCONTO_FOLHA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.limite.valor.maximo.desconto.folha", responsavel));
            param.setDominio("FLOAT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_VALOR_MAX_ENVIO_PARA_DESCONTO_FOLHA));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA_NO_EXERCICIO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA_NO_EXERCICIO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.concluir.contratos.nao.pagos.no.exercicio", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA_NO_EXERCICIO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_QTD_ANEXOS_MIN_ADE_FEITA_POR_CSA_COR)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_QTD_ANEXOS_MIN_ADE_FEITA_POR_CSA_COR);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.config.num.anexos.min.ade.feitos.csa.cor", responsavel));
            param.setDominio("COMPOSTO{"
                    + ApplicationResourcesHelper.getMessage("rotulo.param.svc.qtd.dias.carregar.anexos.ade.feitos.por.csa.cor", responsavel) + ":INT|"
                    + ApplicationResourcesHelper.getMessage("rotulo.param.svc.numero.min.anexos.ade.feitos.por.csa.cor", responsavel) + ":INT}");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_QTD_ANEXOS_MIN_ADE_FEITA_POR_CSA_COR));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_DESCONSIDERAR_VALOR_APROVISIONADO_PERIODOS_PASSADOS)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_DESCONSIDERAR_VALOR_APROVISIONADO_PERIODOS_PASSADOS);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.desconsiderar.valor.aprovisionado.periodos.passados", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_DESCONSIDERAR_VALOR_APROVISIONADO_PERIODOS_PASSADOS));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_TEMPO_LIMITE_PARA_CANCELAMENTO_ULTIMA_ADE_SERVIDOR)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_TEMPO_LIMITE_PARA_CANCELAMENTO_ULTIMA_ADE_SERVIDOR);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.tempo.limite.para.cancelamento.ultima.ade.servidor", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_TEMPO_LIMITE_PARA_CANCELAMENTO_ULTIMA_ADE_SERVIDOR));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_OCULTAR_MENU_SERVIDOR)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_OCULTAR_MENU_SERVIDOR);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.ocultar.menu.servidor", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_OCULTAR_MENU_SERVIDOR));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_IMPEDIR_LIQUIDACAO_CONSIGNACAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_IMPEDIR_LIQUIDACAO_CONSIGNACAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.impedir.liquidacao.consignacao", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_IMPEDIR_LIQUIDACAO_CONSIGNACAO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }

        if (possibilitaReconhecimentoFacial && paramSvcCseMap.containsKey(CodedValues.TPS_EXIGE_RECONHECIMENTO_FACIAL_SERVIDOR_SOLICITACAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIGE_RECONHECIMENTO_FACIAL_SERVIDOR_SOLICITACAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.requer.reconhecimento.facial.servidor", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIGE_RECONHECIMENTO_FACIAL_SERVIDOR_SOLICITACAO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }

        return grupo;
    }

    private GrupoParametroServico carregarGrupoRestricoesRenegociacaoCompra(Map<String, String> paramSvcCseMap, AcessoSistema responsavel) {
        final boolean temModuloCompra = ParamSist.paramEquals(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, CodedValues.TPC_SIM, responsavel);

        final GrupoParametroServico grupo = new GrupoParametroServico(ApplicationResourcesHelper.getMessage("rotulo.restricoes.renegociacao.compra", responsavel));

        if (paramSvcCseMap.containsKey(CodedValues.TPS_VLR_MAX_RENEG_IGUAL_SOMA_CONTRATOS)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_VLR_MAX_RENEG_IGUAL_SOMA_CONTRATOS);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.renegociacao.valor.contrato.inferior.igual.soma", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_VLR_MAX_RENEG_IGUAL_SOMA_CONTRATOS));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_SIM);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PRZ_MAX_RENEG_IGUAL_MAIOR_CONTRATOS)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PRZ_MAX_RENEG_IGUAL_MAIOR_CONTRATOS);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.renegociacao.prazo.contrato.inferior.igual.restante", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PRZ_MAX_RENEG_IGUAL_MAIOR_CONTRATOS));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_VALIDAR_REGRA_RENEGOCIACAO_CONTRATO_FRUTO_PORTABILIDADE)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_VALIDAR_REGRA_RENEGOCIACAO_CONTRATO_FRUTO_PORTABILIDADE);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.valida.regra.renegociacao.contrato.fruto.portabilidade", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_VALIDAR_REGRA_RENEGOCIACAO_CONTRATO_FRUTO_PORTABILIDADE));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERCENTUAL_MINIMO_PRD_PAGAS_RENEG)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERCENTUAL_MINIMO_PRD_PAGAS_RENEG);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.renegociacao.percentual.minimo.parcelas.pagas", responsavel));
            param.setDominio("FLOAT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERCENTUAL_MINIMO_PRD_PAGAS_RENEG));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_MINIMO_PRD_PAGAS_RENEGOCIACAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_MINIMO_PRD_PAGAS_RENEGOCIACAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.renegociacao.minimo.parcelas.pagas", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_MINIMO_PRD_PAGAS_RENEGOCIACAO));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_RENEG)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_RENEG);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.renegociacao.percentual.minimo.vigencia", responsavel));
            param.setDominio("FLOAT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_RENEG));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_MINIMO_VIGENCIA_RENEG)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_MINIMO_VIGENCIA_RENEG);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.renegociacao.quantidade.minimo.vigencia", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_MINIMO_VIGENCIA_RENEG));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_QTDE_MAX_ADE_RENEGOCIACAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_QTDE_MAX_ADE_RENEGOCIACAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.renegociacao.quantidade.maxima.contratos", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_QTDE_MAX_ADE_RENEGOCIACAO));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PRAZO_DIAS_CANCELAMENTO_RENEGOCIACAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PRAZO_DIAS_CANCELAMENTO_RENEGOCIACAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.renegociacao.prazo.cancelamento.dias", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PRAZO_DIAS_CANCELAMENTO_RENEGOCIACAO));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_LIMITA_SALDO_DEVEDOR_ANTERIOR_RENEG)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_LIMITA_SALDO_DEVEDOR_ANTERIOR_RENEG);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.renegociacao.limitar.valor.liberado.somatorio.saldo.devedor", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_LIMITA_SALDO_DEVEDOR_ANTERIOR_RENEG));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_MARGEM_ERRO_LIMITE_SALDO_ANT_RENEG)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_MARGEM_ERRO_LIMITE_SALDO_ANT_RENEG);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.renegociacao.percentual.erro.limitar.valor.liberado", responsavel));
            param.setDominio("COMPOSTO{"
                    + ApplicationResourcesHelper.getMessage("rotulo.param.svc.renegociacao.percentual.erro.limitar.valor.liberado.positiva", responsavel) + ":FLOAT|"
                    + ApplicationResourcesHelper.getMessage("rotulo.param.svc.renegociacao.percentual.erro.limitar.valor.liberado.negativa", responsavel) + ":FLOAT}");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_MARGEM_ERRO_LIMITE_SALDO_ANT_RENEG));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_VLR_MAX_COMPRA_IGUAL_SOMA_CONTRATOS)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_VLR_MAX_COMPRA_IGUAL_SOMA_CONTRATOS);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.contrato.menor.igual.soma.contratos", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_VLR_MAX_COMPRA_IGUAL_SOMA_CONTRATOS));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_SIM);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PRZ_MAX_COMPRA_IGUAL_MAIOR_CONTRATOS)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PRZ_MAX_COMPRA_IGUAL_MAIOR_CONTRATOS);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.prazo.contrato.menor.igual.maior.prazo.restante", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PRZ_MAX_COMPRA_IGUAL_MAIOR_CONTRATOS));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERCENTUAL_MINIMO_PRD_PAGAS_COMPRA) && temModuloCompra) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERCENTUAL_MINIMO_PRD_PAGAS_COMPRA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.percentual.minimo.parcelas.pagas.disponibilizar.contratos", responsavel));
            param.setDominio("FLOAT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERCENTUAL_MINIMO_PRD_PAGAS_COMPRA));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_MINIMO_PRD_PAGAS_COMPRA) && temModuloCompra) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_MINIMO_PRD_PAGAS_COMPRA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.quantidade.minima.parcelas.pagas.disponibilizar.contratos", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_MINIMO_PRD_PAGAS_COMPRA));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_COMPRA) && temModuloCompra) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_COMPRA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.percentual.minimo.vigencia.disponibilizar.contratos", responsavel));
            param.setDominio("FLOAT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_COMPRA));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_MINIMO_VIGENCIA_COMPRA) && temModuloCompra) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_MINIMO_VIGENCIA_COMPRA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.quantidade.minima.vigencia.disponibilizar.contratos.compra", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_MINIMO_VIGENCIA_COMPRA));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_QTDE_MAX_ADE_COMPRA) && temModuloCompra) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_QTDE_MAX_ADE_COMPRA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.quantidade.maxima.contratos", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_QTDE_MAX_ADE_COMPRA));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_LIMITA_SALDO_DEVEDOR_ANTERIOR_COMPRA)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_LIMITA_SALDO_DEVEDOR_ANTERIOR_COMPRA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.limita.valor.liberado.somatorio.saldo.devedor", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_LIMITA_SALDO_DEVEDOR_ANTERIOR_COMPRA));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_MARGEM_ERRO_LIMITE_SALDO_ANT_COMPRA)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_MARGEM_ERRO_LIMITE_SALDO_ANT_COMPRA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.limite.valor.liberado.percentual.margem.erro", responsavel));
            param.setDominio("COMPOSTO{"
                    + ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.limite.valor.liberado.percentual.margem.erro.positiva", responsavel) + ":FLOAT|"
                    + ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.limite.valor.liberado.percentual.margem.erro.negativa", responsavel) + ":FLOAT}");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_MARGEM_ERRO_LIMITE_SALDO_ANT_COMPRA));
            grupo.addParametros(param);
        }

        if (paramSvcCseMap.containsKey(CodedValues.TPS_CONDICIONA_OPERACAO_PORTABILIDADE)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_CONDICIONA_OPERACAO_PORTABILIDADE);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.restringe.portabilidade", responsavel));
            param.setDominio("ESCOLHA["
                    + CodedValues.TPC_NAO    + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.restringe.portabilidade.nao", responsavel) + ";"
                    + CodedValues.RESTRINGE_PORTABILIDADE_MARGEM_NEGATIVA + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.restringe.portabilidade.margem", responsavel) + ";"
                    + CodedValues.RESTRINGE_PORTABILIDADE_PARCELA_REJEITADA + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.restringe.portabilidade.parcela", responsavel) + ";"
                    + CodedValues.RESTRINGE_PORTABILIDADE_MARGEM_NEGATIVA_E_PARCELA_REJEITADA + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.restringe.portabilidade.parcela.e.margem", responsavel) + ";"
                    + CodedValues.RESTRINGE_PORTABILIDADE_MARGEM_NEGATIVA_OU_PARCELA_REJEITADA   + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.restringe.portabilidade.parcela.ou.margem", responsavel) + "]");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_CONDICIONA_OPERACAO_PORTABILIDADE));
            param.setValorPadrao(CodedValues.TPS_CONDICIONA_OPERACAO_PORTABILIDADE);
            grupo.addParametros(param);
        }

        if (paramSvcCseMap.containsKey(CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_PORTABILIDADE)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_PORTABILIDADE);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.percentual.valor.max.nova.parcela.portabilidade", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_PORTABILIDADE));
            grupo.addParametros(param);
        }

        if (paramSvcCseMap.containsKey(CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_RENEGOCIACAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_RENEGOCIACAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.percentual.valor.max.nova.parcela.renegociacao", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_RENEGOCIACAO));
            grupo.addParametros(param);
        }

        return grupo;
    }

    private GrupoParametroServico carregarGrupoRestricoesAlongamento(Map<String, String> paramSvcCseMap, AcessoSistema responsavel) {
        final boolean temAlongamento = ParamSist.paramEquals(CodedValues.TPC_TEM_ALONGAMENTO_CONTRATO, CodedValues.TPC_SIM, responsavel);

        final GrupoParametroServico grupo = new GrupoParametroServico(ApplicationResourcesHelper.getMessage("rotulo.servico.restricoes.alongamento", responsavel));

        if (temAlongamento && paramSvcCseMap.containsKey(CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_ALONGAMENTO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_ALONGAMENTO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.alongamento.percentual.maximo.parcela", responsavel));
            param.setDominio("FLOAT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_ALONGAMENTO));
            grupo.addParametros(param);
        }
        if (temAlongamento && paramSvcCseMap.containsKey(CodedValues.TPS_LIMITA_CAPITAL_DEVIDO_ALONGAMENTO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_LIMITA_CAPITAL_DEVIDO_ALONGAMENTO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.alongamento.limita.capital.devido.ao.atual", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_LIMITA_CAPITAL_DEVIDO_ALONGAMENTO));
            param.setValorPadrao(CodedValues.PSE_BOOLEANO_NAO);
            grupo.addParametros(param);
        }

        return grupo;
    }

    private GrupoParametroServico carregarGrupoPrazoCancelamentoAut(Map<String, String> paramSvcCseMap, AcessoSistema responsavel) {
        final boolean temModuloCompra = ParamSist.paramEquals(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, CodedValues.TPC_SIM, responsavel);
        final boolean temSimulacaoConsignacao = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel);

        final GrupoParametroServico grupo = new GrupoParametroServico(ApplicationResourcesHelper.getMessage("rotulo.servico.prazo.cancelamento.nao.concluido", responsavel));

        if (paramSvcCseMap.containsKey(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.desbloqueio.automatico.reservas.nao.confirmadas.dias", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF));
            grupo.addParametros(param);
        }
        if (temSimulacaoConsignacao && paramSvcCseMap.containsKey(CodedValues.TPS_DIAS_DESBL_SOLICITACAO_NAO_CONF)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_DIAS_DESBL_SOLICITACAO_NAO_CONF);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.desbloqueio.automatico.solicitacoes.nao.confirmadas.dias", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_DIAS_DESBL_SOLICITACAO_NAO_CONF));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_DIAS_DESBL_COMP_NAO_CONF) && temModuloCompra) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_DIAS_DESBL_COMP_NAO_CONF);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.desbloqueio.automatico.negociacao.compra.nao.concluidas.dias", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_DIAS_DESBL_COMP_NAO_CONF));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_DIAS_DESBL_CONSIG_NAO_DEF)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_DIAS_DESBL_CONSIG_NAO_DEF);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.desbloqueio.automatico.consignacoes.nao.deferidas.dias", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_DIAS_DESBL_CONSIG_NAO_DEF));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_DIAS_DEFER_AUT_CONSIG_NAO_DEFERIDAS)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_DIAS_DEFER_AUT_CONSIG_NAO_DEFERIDAS);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.deferimento.automatico.consignacoes.nao.deferidas.dias", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_DIAS_DEFER_AUT_CONSIG_NAO_DEFERIDAS));
            grupo.addParametros(param);
        }

        return grupo;
    }

    private GrupoParametroServico carregarGrupoControleCompra(Map<String, String> paramSvcCseMap, String nseCodigo, AcessoSistema responsavel) {
        final GrupoParametroServico grupo = new GrupoParametroServico(ApplicationResourcesHelper.getMessage("rotulo.servico.modulo.controle.compra", responsavel));

        final boolean temModuloCompra = ParamSist.paramEquals(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, CodedValues.TPC_SIM, responsavel);
        final boolean temControleCompra = ParamSist.paramEquals(CodedValues.TPC_CONTROLE_DETALHADO_PROCESSO_COMPRA, CodedValues.TPC_SIM, responsavel);
        final boolean temCarenciaDesbAutCsa = ParamSist.paramEquals(CodedValues.TPC_PERMITE_CARENCIA_DESBL_AUT_CSA_COMPRA, CodedValues.TPC_SIM, responsavel);
        final boolean temEtapaAprSaldoCompra = ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavel);
        final boolean sistExibeHistLiqAntecipadas = ParamSist.paramEquals(CodedValues.TPC_EXIBE_HISTORICO_LIQUIDACOES_ANTECIPADAS, CodedValues.TPC_SIM, responsavel);
        final boolean permitePortabilidadeCartao = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_PORTABILIDADE_CARTAO, responsavel);

        if (temModuloCompra && temControleCompra) {
            if (paramSvcCseMap.containsKey(CodedValues.TPS_DIAS_INF_SALDO_DV_CONTROLE_COMPRA)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_DIAS_INF_SALDO_DV_CONTROLE_COMPRA);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.dias.informacao.saldo.devedor", responsavel));
                param.setDominio("INT");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_DIAS_INF_SALDO_DV_CONTROLE_COMPRA));
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_ACAO_PARA_NAO_INF_SALDO_DV)) {
                final String dominio152 = (temCarenciaDesbAutCsa ? "COMPOSTO{"+
                        "ESCOLHA["
                            + "0="+ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.nao.aplica", responsavel)
                            +";2="+ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.cancela.processo", responsavel)
                            +";1="+ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.bloqueia.consignataria", responsavel)
                            +"]|" +ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.carencia.desbloqueio", responsavel)+":INT}"
                      : "ESCOLHA["
                            + "0="+ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.nao.aplica", responsavel)
                            +";1="+ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.bloqueia.consignataria", responsavel)
                            +";2="+ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.cancela.processo", responsavel)
                            +"]");

                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_ACAO_PARA_NAO_INF_SALDO_DV);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.nao.informacao.saldo.devedor", responsavel));
                param.setDominio(dominio152);
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_ACAO_PARA_NAO_INF_SALDO_DV));
                param.setValorPadrao("0");
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_DIAS_APR_SALDO_DV_CONTROLE_COMPRA) && temEtapaAprSaldoCompra) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_DIAS_APR_SALDO_DV_CONTROLE_COMPRA);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.dias.aprovacao.rejeicao.saldo.devedor", responsavel));
                param.setDominio("INT");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_DIAS_APR_SALDO_DV_CONTROLE_COMPRA));
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_ACAO_PARA_NAO_APR_SALDO_DV) && temEtapaAprSaldoCompra) {
                final String dominio194 = "COMPOSTO{ESCOLHA["
                        + "0=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.nao.aplica", responsavel)
                        +";1=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.aprova.automaticamente", responsavel)
                        +";2=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.cancela.processo", responsavel)
                        +"]|"  + ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.dias.bloqueio.novas.compras", responsavel)
                        +":INT}";
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_ACAO_PARA_NAO_APR_SALDO_DV);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.aprovacao.rejeicao.saldo.devedor", responsavel));
                param.setDominio(dominio194);
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_ACAO_PARA_NAO_APR_SALDO_DV));
                param.setValorPadrao("0");
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_DIAS_INF_PGT_SALDO_CONTROLE_COMPRA)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_DIAS_INF_PGT_SALDO_CONTROLE_COMPRA);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.dias.informacao.pagamento.saldo.devedor", responsavel));
                param.setDominio("INT");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_DIAS_INF_PGT_SALDO_CONTROLE_COMPRA));
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_ACAO_PARA_NAO_INF_PGT_SALDO)) {
                final String dominio153 = (temCarenciaDesbAutCsa ? "COMPOSTO{ESCOLHA["
                        + "0="+ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.nao.aplica", responsavel)
                        +";2="+ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.cancela.processo", responsavel)
                        +";1="+ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.bloqueia.consignataria", responsavel)
                        +"]|" +ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.carencia.desbloqueio", responsavel)+":INT}"
                        : "ESCOLHA["
                        + "0="+ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.nao.aplica", responsavel)
                        +";1="+ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.bloqueia.consignataria", responsavel)
                        +";2="+ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.cancela.processo", responsavel)
                        +"]");

                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_ACAO_PARA_NAO_INF_PGT_SALDO);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.nao.informacao.pagamento.saldo.devedor", responsavel));
                param.setDominio(dominio153);
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_ACAO_PARA_NAO_INF_PGT_SALDO));
                param.setValorPadrao("0");
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_DIAS_LIQUIDACAO_ADE_CONTROLE_COMPRA)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_DIAS_LIQUIDACAO_ADE_CONTROLE_COMPRA);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.dias.liquidacao.contrato", responsavel));
                param.setDominio("INT");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_DIAS_LIQUIDACAO_ADE_CONTROLE_COMPRA));
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_CONSIDERA_DATA_INF_SALDO_LIQUIDACAO_ADE_CONTROLE_COMPRA)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_CONSIDERA_DATA_INF_SALDO_LIQUIDACAO_ADE_CONTROLE_COMPRA);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.considera.data.informacao.saldo.liquidacao", responsavel));
                param.setDominio("SN");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_CONSIDERA_DATA_INF_SALDO_LIQUIDACAO_ADE_CONTROLE_COMPRA));
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_ACAO_PARA_NAO_LIQUIDACAO_ADE)) {
                final String dominio154 = (temCarenciaDesbAutCsa ? "COMPOSTO{ESCOLHA["
                        + "0="+ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.nao.aplica", responsavel)
                        +";2="+ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.cancela.processo", responsavel)
                        +";1="+ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.bloqueia.consignataria", responsavel)
                        +"]|" +ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.carencia.desbloqueio", responsavel)+":INT}"
                        : "ESCOLHA["
                        + "0="+ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.nao.aplica", responsavel)
                        +";1="+ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.bloqueia.consignataria", responsavel)
                        +";2="+ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.cancela.processo", responsavel)+"]");

                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_ACAO_PARA_NAO_LIQUIDACAO_ADE);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.nao.liquidacao.contrato", responsavel));
                param.setDominio(dominio154);
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_ACAO_PARA_NAO_LIQUIDACAO_ADE));
                param.setValorPadrao("0");
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_REJEICAO_PGT_SDV_BLOQUEIA_AMBAS_CSAS)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_REJEICAO_PGT_SDV_BLOQUEIA_AMBAS_CSAS);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.penalidade.rejeicao.pagamento.bloqueia.ambas", responsavel));
                param.setDominio("SN");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_REJEICAO_PGT_SDV_BLOQUEIA_AMBAS_CSAS));
                param.setValorPadrao("0");
                grupo.addParametros(param);
            }
            if (sistExibeHistLiqAntecipadas && paramSvcCseMap.containsKey(CodedValues.TPS_NUM_ADE_HIST_LIQUIDACOES_ANTECIPADAS)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_NUM_ADE_HIST_LIQUIDACOES_ANTECIPADAS);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.quantidade.contratos.historico.liquidacoes.antecipadas", responsavel));
                param.setDominio("INT");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_NUM_ADE_HIST_LIQUIDACOES_ANTECIPADAS));
                grupo.addParametros(param);
            }
            if (sistExibeHistLiqAntecipadas && paramSvcCseMap.containsKey(CodedValues.TPS_NUM_ADE_HIST_SUSPENSOES)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_NUM_ADE_HIST_SUSPENSOES);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.quantidade.contratos.historico.suspensoes", responsavel));
                param.setDominio("INT");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_NUM_ADE_HIST_SUSPENSOES));
                grupo.addParametros(param);
            }
            if (permitePortabilidadeCartao && paramSvcCseMap.containsKey(CodedValues.TPS_QUANTIDADE_PERIODOS_SEM_LANCAMENTO_PERMITE_PORTABILIDADE) && CodedValues.NSE_CARTAO.equals(nseCodigo)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_QUANTIDADE_PERIODOS_SEM_LANCAMENTO_PERMITE_PORTABILIDADE);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.quantidade.periodos.sem.lancamentos.permite.portabilidade.cartao", responsavel));
                param.setDominio("INT");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_QUANTIDADE_PERIODOS_SEM_LANCAMENTO_PERMITE_PORTABILIDADE));
                grupo.addParametros(param);
            }
            if (permitePortabilidadeCartao && paramSvcCseMap.containsKey(CodedValues.TPS_SOMENTE_CONTRATOS_SEM_LANCAMENTO_PERMITE_PORTABILIDADE) && CodedValues.NSE_CARTAO.equals(nseCodigo)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_SOMENTE_CONTRATOS_SEM_LANCAMENTO_PERMITE_PORTABILIDADE);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.somente.contratos.sem.lancamento.disponibilizados.cartao", responsavel));
                param.setDominio("SN");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_SOMENTE_CONTRATOS_SEM_LANCAMENTO_PERMITE_PORTABILIDADE));
                grupo.addParametros(param);
            }
        }

        return grupo;
    }

    private GrupoParametroServico carregarGrupoSenhaServidor(Map<String, String> paramSvcCseMap, AcessoSistema responsavel) {
        final boolean usaSenhaAutorizacaoSer = ParamSist.getBoolParamSist(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, responsavel);
        final boolean usaSenhaAutorizacaoTodasOpe = ParamSist.getBoolParamSist(CodedValues.TPC_USA_SENHA_AUTORIZACAO_TODAS_OPERACOES, responsavel);

        final GrupoParametroServico grupo = new GrupoParametroServico(ApplicationResourcesHelper.getMessage("rotulo.servico.senha.servidor", responsavel));

        if (paramSvcCseMap.containsKey(CodedValues.TPS_SER_SENHA_OBRIGATORIA_SER)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_SER_SENHA_OBRIGATORIA_SER);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.senha.servidor.obrigatoria.ser.reserva", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_SER_SENHA_OBRIGATORIA_SER));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.senha.servidor.obrigatoria.csa.reserva", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSA));
            param.setValorPadrao("1");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.exige.senha.servidor.lote", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE));
            param.setValorPadrao("1");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_SER_SENHA_OPCIONAL_RESERVA_MARGEM_TEM_ADE)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_SER_SENHA_OPCIONAL_RESERVA_MARGEM_TEM_ADE);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.senha.servidor.opcional.csa.reserva.tem.ade", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_SER_SENHA_OPCIONAL_RESERVA_MARGEM_TEM_ADE));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSE)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSE);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.senha.servidor.obrigatoria.cse.reserva", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_SER_SENHA_OBRIGATORIA_CSE));
            param.setValorPadrao("1");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIGE_SENHA_ALTERACAO_CONTRATOS)) {
            final String dominioSenhaServidorCsaAlteracao =  "ESCOLHA" +
                    "[1=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.senha.servidor.obrigatoria.csa.alteracao.sim.todos", responsavel) +
                    ";2=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.senha.servidor.obrigatoria.csa.alteracao.sim.valor.prazo.maiores", responsavel) +
                    ";3=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.senha.servidor.obrigatoria.csa.alteracao.sim.capital.devido.maior", responsavel) +
                    ";0=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.senha.servidor.obrigatoria.csa.alteracao.nao", responsavel) +
                    "]";

            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIGE_SENHA_ALTERACAO_CONTRATOS);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.senha.servidor.obrigatoria.csa.alteracao", responsavel));
            param.setDominio(dominioSenhaServidorCsaAlteracao);
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIGE_SENHA_ALTERACAO_CONTRATOS));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_ALTERAR_COM_LIMITACAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITE_ALTERAR_COM_LIMITACAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.senha.servidor.percentual.limite.alteracao.valor", responsavel));
            param.setDominio("FLOAT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_ALTERAR_COM_LIMITACAO));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIGE_SENHA_CONFIRMACAO_SOLICITACAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIGE_SENHA_CONFIRMACAO_SOLICITACAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.senha.servidor.obrigatoria.csa.confirmacao", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIGE_SENHA_CONFIRMACAO_SOLICITACAO));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIBE_CIDADE_CONFIRMACAO_SOLICITACAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIBE_CIDADE_CONFIRMACAO_SOLICITACAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.exibe.cidade.confirmacao.solicitacao", responsavel));
            param.setDominio("ESCOLHA["
                    + CodedValues.NAO_EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.exibe.cidade.confirmacao.solicitacao.nao.disponivel", responsavel) + ";"
                    + CodedValues.EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OPCIONAL + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.exibe.cidade.confirmacao.solicitacao.opcional", responsavel) + ";"
                    + CodedValues.EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OBRIGATORIO + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.exibe.cidade.confirmacao.solicitacao.obrigatorio", responsavel) + ";"
                    + CodedValues.EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OBRIGATORIO_LEILAO  + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.exibe.cidade.confirmacao.solicitacao.obrigatorio.leilao", responsavel) + "]");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIBE_CIDADE_CONFIRMACAO_SOLICITACAO));
            param.setValorPadrao(CodedValues.NAO_EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIGE_SENHA_SER_CANCEL_RENEGOCIACAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIGE_SENHA_SER_CANCEL_RENEGOCIACAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.senha.servidor.obrigatoria.csa.cancelamento.renegociacao", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIGE_SENHA_SER_CANCEL_RENEGOCIACAO));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (usaSenhaAutorizacaoSer && !usaSenhaAutorizacaoTodasOpe && paramSvcCseMap.containsKey(CodedValues.TPS_USA_SENHA_CONSULTA_RESERVA_MARGEM)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_USA_SENHA_CONSULTA_RESERVA_MARGEM);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.senha.servidor.obrigatoria.consulta.margem", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_USA_SENHA_CONSULTA_RESERVA_MARGEM));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIGE_SENHA_SER_SUSPENDER_CONSIGNACAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIGE_SENHA_SER_SUSPENDER_CONSIGNACAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.senha.servidor.obrigatoria.csa.suspender.consignacao", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIGE_SENHA_SER_SUSPENDER_CONSIGNACAO));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIGE_SENHA_SER_CANCELAR_CONSIGNACAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIGE_SENHA_SER_CANCELAR_CONSIGNACAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.senha.servidor.obrigatoria.csa.cancelar.consignacao", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIGE_SENHA_SER_CANCELAR_CONSIGNACAO));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIGE_SENHA_SER_REATIVAR_CONSIGNACAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIGE_SENHA_SER_REATIVAR_CONSIGNACAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.senha.servidor.obrigatoria.csa.reativar.consignacao", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIGE_SENHA_SER_REATIVAR_CONSIGNACAO));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIGE_SENHA_SER_LIQUIDAR_CONSIGNACAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIGE_SENHA_SER_LIQUIDAR_CONSIGNACAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.senha.servidor.obrigatoria.csa.liquidar.consignacao", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIGE_SENHA_SER_LIQUIDAR_CONSIGNACAO));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }

        return grupo;
    }

    private GrupoParametroServico carregarGrupoTaxaJuros(Map<String, String> paramSvcCseMap, String svcCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
        final boolean permiteCadTxJuros = ParamSist.paramEquals(CodedValues.TPC_PER_CAD_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);

        final GrupoParametroServico grupo = new GrupoParametroServico(ApplicationResourcesHelper.getMessage((temCET ? "rotulo.cet.singular" : "rotulo.taxa.juros.singular"), responsavel));

        if (permiteCadTxJuros) {
            if (paramSvcCseMap.containsKey(CodedValues.TPS_VLR_LIQ_TAXA_JUROS)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_VLR_LIQ_TAXA_JUROS);
                param.setDescricao(ApplicationResourcesHelper.getMessage((temCET ? "rotulo.param.svc.cet" : "rotulo.param.svc.taxa.juros"), responsavel));
                param.setDominio("SN");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_VLR_LIQ_TAXA_JUROS));
                param.setValorPadrao("0");
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_VALIDAR_TAXA_JUROS)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_VALIDAR_TAXA_JUROS);
                param.setDescricao(ApplicationResourcesHelper.getMessage((temCET ? "rotulo.param.svc.validar.cet.reserva" : "rotulo.param.svc.validar.taxa.reserva"), responsavel));
                param.setDominio("SN");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_VALIDAR_TAXA_JUROS));
                param.setValorPadrao("0");
                grupo.addParametros(param);
            }
            if (!temCET && paramSvcCseMap.containsKey(CodedValues.TPS_ADD_VALOR_TAC_VAL_TAXA_JUROS)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_ADD_VALOR_TAC_VAL_TAXA_JUROS);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.adiciona.tac.vlr.liberado", responsavel));
                param.setDominio("SN");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_ADD_VALOR_TAC_VAL_TAXA_JUROS));
                param.setValorPadrao("0");
                grupo.addParametros(param);
            }
            if (!temCET && paramSvcCseMap.containsKey(CodedValues.TPS_ADD_VALOR_IOF_VAL_TAXA_JUROS)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_ADD_VALOR_IOF_VAL_TAXA_JUROS);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.adiciona.iof.vlr.liberado", responsavel));
                param.setDominio("SN");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_ADD_VALOR_IOF_VAL_TAXA_JUROS));
                param.setValorPadrao("0");
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIBE_RANKING_CONFIRMACAO_RESERVA)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_EXIBE_RANKING_CONFIRMACAO_RESERVA);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.exibir.ranking.confirmacao.reserva", responsavel));
                param.setDominio("SN");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIBE_RANKING_CONFIRMACAO_RESERVA));
                param.setValorPadrao("1");
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_EXCEDENTE_MONETARIO_TX_JUROS)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_EXCEDENTE_MONETARIO_TX_JUROS);
                param.setDescricao(ApplicationResourcesHelper.getMessage((temCET ? "rotulo.param.svc.excedente.monetario.cet" : "rotulo.param.svc.excedente.monetario"), responsavel));
                param.setDominio("FLOAT");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXCEDENTE_MONETARIO_TX_JUROS));
                grupo.addParametros(param);
            }
            if (!temCET && paramSvcCseMap.containsKey(CodedValues.TPS_VALIDA_MENS_VINC_VAL_TAXA_JUROS)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_VALIDA_MENS_VINC_VAL_TAXA_JUROS);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.validar.mensalidade.vinculada.taxa.juros", responsavel));
                param.setDominio("SN");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_VALIDA_MENS_VINC_VAL_TAXA_JUROS));
                param.setValorPadrao("0");
                grupo.addParametros(param);
            }
            if (!temCET && paramSvcCseMap.containsKey(CodedValues.TPS_CALC_TAC_IOF_VALIDA_TAXA_JUROS)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_CALC_TAC_IOF_VALIDA_TAXA_JUROS);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.calcular.iof.taxa.juros", responsavel));
                param.setDominio("SN");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_CALC_TAC_IOF_VALIDA_TAXA_JUROS));
                param.setValorPadrao("0");
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_ORDENACAO_CADASTRO_TAXAS)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_ORDENACAO_CADASTRO_TAXAS);
                param.setDescricao(ApplicationResourcesHelper.getMessage((temCET ? "rotulo.param.svc.ordenacao.cadastro.cet" : "rotulo.param.svc.ordenacao.cadastro.taxa.juros"), responsavel));
                param.setDominio("ESCOLHA[0=" + ApplicationResourcesHelper.getMessage("rotulo.nao.se.aplica", responsavel) +
                        ";1=" + ApplicationResourcesHelper.getMessage("rotulo.crescente", responsavel) +
                        ";2=" + ApplicationResourcesHelper.getMessage("rotulo.decrescente", responsavel) +  "]");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_ORDENACAO_CADASTRO_TAXAS));
                param.setValorPadrao("0");
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_DATA_LIMITE_DIGIT_TAXA)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_DATA_LIMITE_DIGIT_TAXA);
                param.setDescricao(ApplicationResourcesHelper.getMessage((temCET ? "rotulo.param.svc.data.limite.cet" : "rotulo.param.svc.data.limite.taxa.juros"), responsavel));
                param.setDominio("DIA");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_DATA_LIMITE_DIGIT_TAXA));
                grupo.addParametros(param);
            }
            if(paramSvcCseMap.containsKey(CodedValues.TPS_DATA_ABERTURA_TAXA)) {
                final String dominioTpsDataAbertura = "COMPOSTO{DIA|SELECAO[" +
                        "D=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.data.abertura.dia.mes", responsavel) +
                        ";S=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.data.abertura.dia.semana", responsavel) +
                        ";U=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.data.abertura.dia.util", responsavel) + "]}";

                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_DATA_ABERTURA_TAXA);
                param.setDescricao(ApplicationResourcesHelper.getMessage((temCET ? "rotulo.param.svc.data.abertura.cet" : "rotulo.param.svc.data.abertura.taxa.juros"), responsavel));
                param.setDominio(dominioTpsDataAbertura);
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_DATA_ABERTURA_TAXA));
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_CSA_LIMITE_SUPERIOR_TABELA_JUROS_CET)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_CSA_LIMITE_SUPERIOR_TABELA_JUROS_CET);
                param.setDescricao(temCET ? ApplicationResourcesHelper.getMessage("rotulo.param.svc.csa.limite.cet", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.param.svc.csa.limite.taxa.juros", responsavel));
                param.setCombo(true);
                param.setComboValues(simulacaoController.lstConsignatariasComTaxasAtivas(svcCodigo, responsavel));
                param.setCampoValor(Columns.CSA_CODIGO);
                param.setCampoLabel(Columns.CSA_IDENTIFICADOR + ";" + Columns.CSA_NOME);
                param.setLabelNaoSelecionado(ApplicationResourcesHelper.getMessage("rotulo.campo.csa.uma", responsavel));
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_CSA_LIMITE_SUPERIOR_TABELA_JUROS_CET));
                grupo.addParametros(param);

            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_DIAS_VIGENCIA_TAXAS_SUPERIOR_LIMITE)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_DIAS_VIGENCIA_TAXAS_SUPERIOR_LIMITE);
                param.setDescricao(ApplicationResourcesHelper.getMessage((temCET ? "rotulo.param.svc.vigencia.superior.limite.cet" : "rotulo.param.svc.vigencia.superior.limite.taxa.juros"), responsavel));
                param.setDominio("INT");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_DIAS_VIGENCIA_TAXAS_SUPERIOR_LIMITE));
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_VALOR_MINIMO_PERMITIDO_TAXA_JUROS)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_VALOR_MINIMO_PERMITIDO_TAXA_JUROS);
                param.setDescricao(ApplicationResourcesHelper.getMessage((temCET ? "rotulo.param.svc.minimo.permitido.cet" : "rotulo.param.svc.minimo.permitido.taxa.juros"), responsavel));
                param.setDominio("FLOAT");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_VALOR_MINIMO_PERMITIDO_TAXA_JUROS));
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_VALIDA_TAXA_ALTERACAO_ADE_ANDAMENTO)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_VALIDA_TAXA_ALTERACAO_ADE_ANDAMENTO);
                param.setDescricao(ApplicationResourcesHelper.getMessage((temCET ? "rotulo.param.svc.validar.cet.alteracao.ade.andamento" : "rotulo.param.svc.validar.taxa.alteracao.ade.andamento"), responsavel));
                param.setDominio("SN");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_VALIDA_TAXA_ALTERACAO_ADE_ANDAMENTO));
                param.setValorPadrao("0");
                grupo.addParametros(param);
            }
            if (temCET && paramSvcCseMap.containsKey(CodedValues.TPS_DIAS_VIGENCIA_CET)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_DIAS_VIGENCIA_CET);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.dia.vigencia.cet", responsavel));
                param.setDominio("INT");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_DIAS_VIGENCIA_CET));
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIGE_CADASTRO_TAXA_JUROS_PARA_CET)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_EXIGE_CADASTRO_TAXA_JUROS_PARA_CET);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.exige.cadastro.taxa.juros.prazo.cet", responsavel));
                param.setDominio("SN");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIGE_CADASTRO_TAXA_JUROS_PARA_CET));
                param.setValorPadrao("0");
                grupo.addParametros(param);
            }
        }

        return grupo;
    }

    private GrupoParametroServico carregarGrupoRestricaoSaldoDevedor(Map<String, String> paramSvcCseMap, AcessoSistema responsavel) throws CoeficienteCorrecaoControllerException {
        final boolean temModuloFinancDividaCartao = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel);
        final boolean temModuloSaldoDevedorExclusao = ParamSist.paramEquals(CodedValues.TPC_HABILITA_SALDO_DEVEDOR_EXCLUSAO_SERVIDOR, CodedValues.TPC_SIM, responsavel);

        final GrupoParametroServico grupo = new GrupoParametroServico(ApplicationResourcesHelper.getMessage("rotulo.servico.solicitacao.saldo.devedor", responsavel));

        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.cadastro.saldo.devedor", responsavel));
            param.setDominio("ESCOLHA["
                    + CodedValues.USUARIO_CADASTRA_SALDO_DEVEDOR    + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.cadastro.saldo.devedor.csa", responsavel) + ";"
                    + CodedValues.SISTEMA_CALCULA_SALDO_DEVEDOR     + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.cadastro.saldo.devedor.sistema", responsavel) + ";"
                    + CodedValues.CADASTRA_E_CALCULA_SALDO_DEVEDOR  + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.cadastro.saldo.devedor.ambos", responsavel) + ";"
                    + CodedValues.NAO_POSSUI_CADASTRO_SALDO_DEVEDOR + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.cadastro.saldo.devedor.nao", responsavel) + "]");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR));
            param.setValorPadrao(CodedValues.NAO_POSSUI_CADASTRO_SALDO_DEVEDOR);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_CALCULA_SALDO_SOMENTE_VINCENDO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_CALCULA_SALDO_SOMENTE_VINCENDO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.calcula.saldo.devedor.vincendo", responsavel));
            param.setDominio("ESCOLHA["
                    + CodedValues.CALCULA_SALDO_SOMENTE_VINCENDO + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.calcula.saldo.devedor.vincendo.sim.ignora.vencidas", responsavel) + ";"
                    + CodedValues.CALCULA_SALDO_VINCENDO_SOMANDO_VENCIDAS + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.calcula.saldo.devedor.vincendo.sim.soma.vencidas", responsavel) + ";"
                    + CodedValues.CALCULA_SALDO_TUDO_EM_ABERTO   + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.calcula.saldo.devedor.vincendo.nao", responsavel) + "]");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_CALCULA_SALDO_SOMENTE_VINCENDO));
            param.setValorPadrao(CodedValues.CALCULA_SALDO_TUDO_EM_ABERTO);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_VISUALIZA_VALOR_LIBERADO_CALC)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_VISUALIZA_VALOR_LIBERADO_CALC);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.visualiza.saldo.devedor.calculado", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_VISUALIZA_VALOR_LIBERADO_CALC));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_TABELA_CORRECAO_CALCULO_SPREAD)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_TABELA_CORRECAO_CALCULO_SPREAD);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.servico.tabela.correcao.spread.correcao.saldo.devedor", responsavel));
            param.setCombo(true);
            param.setComboValues(coeficienteCorrecaoController.lstTipoCoeficienteCorrecao());
            param.setCampoValor(Columns.TCC_CODIGO);
            param.setCampoLabel(Columns.TCC_DESCRICAO);
            param.setLabelNaoSelecionado(ApplicationResourcesHelper.getMessage("rotulo.campo.tabela.correcao", responsavel));
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_TABELA_CORRECAO_CALCULO_SPREAD));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_LIMITA_SALDO_DEVEDOR_CADASTRADO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_LIMITA_SALDO_DEVEDOR_CADASTRADO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.limita.saldo.devedor.csa", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_LIMITA_SALDO_DEVEDOR_CADASTRADO));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_LIMITA_SALDO_DEVEDOR_CAD_CSE_ORG_SUP)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_LIMITA_SALDO_DEVEDOR_CAD_CSE_ORG_SUP);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.limita.saldo.devedor.cse.org.sup", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_LIMITA_SALDO_DEVEDOR_CAD_CSE_ORG_SUP));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_SALDO_FORA_FAIXA_LIMITE)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERMITE_SALDO_FORA_FAIXA_LIMITE);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permite.saldo.devedor.fora.faixa", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_SALDO_FORA_FAIXA_LIMITE));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_USA_CAPITAL_DEVIDO_BASE_LIMITE_SALDO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_USA_CAPITAL_DEVIDO_BASE_LIMITE_SALDO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.limita.saldo.devedor.capital.devido", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_USA_CAPITAL_DEVIDO_BASE_LIMITE_SALDO));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_MARGEM_ERRO_LIMITE_SALDO_DEVEDOR)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_MARGEM_ERRO_LIMITE_SALDO_DEVEDOR);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.percentual.erro.saldo.devedor", responsavel));
            param.setDominio("COMPOSTO{"
                    + ApplicationResourcesHelper.getMessage("rotulo.param.svc.percentual.erro.saldo.devedor.positivo", responsavel) + ":FLOAT|"
                    + ApplicationResourcesHelper.getMessage("rotulo.param.svc.percentual.erro.saldo.devedor.negativo", responsavel) + ":FLOAT}");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_MARGEM_ERRO_LIMITE_SALDO_DEVEDOR));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PRAZO_ATEND_SOLICIT_SALDO_DEVEDOR)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PRAZO_ATEND_SOLICIT_SALDO_DEVEDOR);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.prazo.atendimento.solicitacao.saldo.devedor", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PRAZO_ATEND_SOLICIT_SALDO_DEVEDOR));
            grupo.addParametros(param);
        }
        if (temModuloSaldoDevedorExclusao && paramSvcCseMap.containsKey(CodedValues.TPS_PRAZO_ATEND_SALDO_DEVEDOR_EXCLUSAO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PRAZO_ATEND_SALDO_DEVEDOR_EXCLUSAO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.prazo.atendimento.solicitacao.saldo.devedor.exclusao.servidor", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PRAZO_ATEND_SALDO_DEVEDOR_EXCLUSAO));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PRAZO_ENTRE_SOLICITACOES_SDO_DEVEDOR)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PRAZO_ENTRE_SOLICITACOES_SDO_DEVEDOR);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.dias.entre.solicitacoes.saldo.devedor", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PRAZO_ENTRE_SOLICITACOES_SDO_DEVEDOR));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIGE_NUMERO_CONTRATO_SALDO_DEVEDOR)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIGE_NUMERO_CONTRATO_SALDO_DEVEDOR);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.exigir.numero.contrato.cadastro.saldo.devedor.compra", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIGE_NUMERO_CONTRATO_SALDO_DEVEDOR));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIGE_NRO_CONTRATO_INF_SALDO_SOLIC)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_EXIGE_NRO_CONTRATO_INF_SALDO_SOLIC);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.exigir.numero.contrato.cadastro.saldo.devedor.soliticacao.saldo", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIGE_NRO_CONTRATO_INF_SALDO_SOLIC));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (temModuloFinancDividaCartao) {
            if (paramSvcCseMap.containsKey(CodedValues.TPS_PERMITE_SOLICITAR_SALDO_BENEFICIARIO)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_PERMITE_SOLICITAR_SALDO_BENEFICIARIO);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permite.solicitacao.saldo.devedor.beneficiarios", responsavel));
                param.setDominio("SN");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERMITE_SOLICITAR_SALDO_BENEFICIARIO));
                param.setValorPadrao("0");
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_PERCENTUAL_MINIMO_DESCONTO_VLR_SALDO)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_PERCENTUAL_MINIMO_DESCONTO_VLR_SALDO);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.percentual.minimo.desconto.saldo.devedor", responsavel));
                param.setDominio("FLOAT");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERCENTUAL_MINIMO_DESCONTO_VLR_SALDO));
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_QTD_PROPOSTAS_PAGAMENTO_PARCEL_SALDO)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_QTD_PROPOSTAS_PAGAMENTO_PARCEL_SALDO);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.quantidade.propostas.pagamento.parcelado.saldo.devedor", responsavel));
                param.setDominio("COMPOSTO{"
                        + ApplicationResourcesHelper.getMessage("rotulo.param.svc.quantidade.propostas.pagamento.parcelado.saldo.devedor.minimo", responsavel) + ":INT|"
                        + ApplicationResourcesHelper.getMessage("rotulo.param.svc.quantidade.propostas.pagamento.parcelado.saldo.devedor.maximo", responsavel) + ":INT}");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_QTD_PROPOSTAS_PAGAMENTO_PARCEL_SALDO));
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_DIAS_VALIDADE_PROPOSTAS_PGTO_SALDO)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_DIAS_VALIDADE_PROPOSTAS_PGTO_SALDO);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.validade.propostas.pagamento.saldo.devedor", responsavel));
                param.setDominio("INT");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_DIAS_VALIDADE_PROPOSTAS_PGTO_SALDO));
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_DIAS_SOLICITAR_PROPOSTAS_PGTO_SALDO)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_DIAS_SOLICITAR_PROPOSTAS_PGTO_SALDO);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.dias.solicitacao.propostas.pagamento.saldo.devedor.terceiros", responsavel));
                param.setDominio("INT");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_DIAS_SOLICITAR_PROPOSTAS_PGTO_SALDO));
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_DIAS_INF_PROPOSTAS_PGTO_SALDO_TERCEI)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_DIAS_INF_PROPOSTAS_PGTO_SALDO_TERCEI);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.dias.informacao.propostas.pagamento.saldo.devedor.terceiros", responsavel));
                param.setDominio("INT");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_DIAS_INF_PROPOSTAS_PGTO_SALDO_TERCEI));
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_PERCENTUAL_MINIMO_MANTER_VALOR_RENEG)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_PERCENTUAL_MINIMO_MANTER_VALOR_RENEG);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.percentual.minimo.valor.contratos.renegociados", responsavel));
                param.setDominio("FLOAT");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERCENTUAL_MINIMO_MANTER_VALOR_RENEG));
                grupo.addParametros(param);
            }

        }
        if (ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_BLOQ_CSA_N_LIQ_ADE_SALDO_PAGO_SER, responsavel) && paramSvcCseMap.containsKey(CodedValues.TPS_QTD_DIAS_BLOQ_CSA_APOS_INF_SALDO_SER)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_QTD_DIAS_BLOQ_CSA_APOS_INF_SALDO_SER);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.dias.bloqueio.csa.informacao.pagamento.saldo.devedor.servidor", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_QTD_DIAS_BLOQ_CSA_APOS_INF_SALDO_SER));
            grupo.addParametros(param);
        }

        return grupo;
    }

    private GrupoParametroServico carregarGrupoControleCorrecaoSaldoDevedor(Map<String, String> paramSvcCseMap, AcessoSistema responsavel) throws CoeficienteCorrecaoControllerException {
        final GrupoParametroServico grupo = new GrupoParametroServico(ApplicationResourcesHelper.getMessage("rotulo.servico.controle.saldo.devedor", responsavel));

        if (paramSvcCseMap.containsKey(CodedValues.TPS_CONTROLA_SALDO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_CONTROLA_SALDO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.controla.saldo.devedor", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_CONTROLA_SALDO));
            param.setValorPadrao("0");
            param.setOnClick("controlaCamposSaldoDevedor()");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_CONTROLA_VLR_MAX_DESCONTO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_CONTROLA_VLR_MAX_DESCONTO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.controla.valor.maximo.desconto", responsavel));
            param.setDominio("ESCOLHA["
                    + CodedValues.CONTROLA_VLR_MAX_DESCONTO_PELO_CARGO + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.controla.valor.maximo.desconto.cargo.servidor", responsavel) + ";"
                    + CodedValues.CONTROLA_VLR_MAX_DESCONTO_PELA_PARCELA + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.controla.valor.maximo.desconto.valor.parcela", responsavel) + ";"
                    + CodedValues.NAO_CONTROLA_VLR_MAX_DESCONTO + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.controla.valor.maximo.desconto.nao", responsavel) + "]");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_CONTROLA_VLR_MAX_DESCONTO));
            param.setValorPadrao(CodedValues.NAO_CONTROLA_VLR_MAX_DESCONTO);
            grupo.addParametros(param);
            param.setOnClick("controlaCamposSaldoDevedor()");
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.possui.correcao.valor.saldo.devedor", responsavel));
            param.setDominio("ESCOLHA["
                    + CodedValues.NAO_POSSUI_CORRECAO_SALDO_DEVEDOR + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.possui.correcao.valor.saldo.devedor.nao", responsavel) + ";"
                    + CodedValues.CORRECAO_SALDO_DEVEDOR_PROPRIO_SERVICO + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.possui.correcao.valor.saldo.devedor.proprio.servico", responsavel) + ";"
                    + CodedValues.CORRECAO_SALDO_DEVEDOR_EM_OUTRO_SERVICO + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.possui.correcao.valor.saldo.devedor.servico.abaixo", responsavel) +  "]");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR));
            param.setValorPadrao(CodedValues.NAO_POSSUI_CORRECAO_SALDO_DEVEDOR);
            param.setOnClick("controlaCamposSaldoDevedor()");
            grupo.addParametros(param);

            final ParametroServico param2 = new ParametroServico();
            param2.setCodigo("svcRelacionamentoCorrecao");
            param2.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.servico.relacionado.correcao.saldo.devedor", responsavel));
            param2.setCustom(true);
            grupo.addParametros(param2);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_FORMA_CALCULO_CORRECAO_SALDO_DV)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_FORMA_CALCULO_CORRECAO_SALDO_DV);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.servico.forma.calculo.correcao.saldo.devedor", responsavel));
            param.setCombo(true);
            param.setComboValues(coeficienteCorrecaoController.lstTipoCoeficienteCorrecao());
            param.setCampoValor(Columns.TCC_CODIGO);
            param.setCampoLabel(Columns.TCC_DESCRICAO);
            param.setLabelNaoSelecionado(ApplicationResourcesHelper.getMessage("rotulo.campo.forma.correcao", responsavel));
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_FORMA_CALCULO_CORRECAO_SALDO_DV));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_CORRECAO_SOBRE_TOTAL_SALDO_DV)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_CORRECAO_SOBRE_TOTAL_SALDO_DV);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.correcao.valor.saldo.devedor.sobre", responsavel));
            param.setDominio("ESCOLHA["
                    + CodedValues.CORRECAO_SOBRE_SALDO_PARCELAS + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.correcao.valor.saldo.devedor.sobre.parcela", responsavel) + ";"
                    + CodedValues.CORRECAO_SOBRE_TOTAL_SALDO_DEVEDOR + "=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.correcao.valor.saldo.devedor.sobre.saldo.devedor", responsavel) + "]");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_CORRECAO_SOBRE_TOTAL_SALDO_DV));
            param.setValorPadrao(CodedValues.CORRECAO_SOBRE_SALDO_PARCELAS);
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_CORRECAO_ENVIADA_APOS_PRINCIPAL)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_CORRECAO_ENVIADA_APOS_PRINCIPAL);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.correcao.valor.saldo.devedor.enviada.folha.final.desconto", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_CORRECAO_ENVIADA_APOS_PRINCIPAL));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }

        return grupo;
    }

    private GrupoParametroServico carregarGrupoControleCompulsorios(Map<String, String> paramSvcCseMap, AcessoSistema responsavel) {
        final boolean temControleCompulsorios = ParamSist.paramEquals(CodedValues.TPC_TEM_CONTROLE_DE_ESTOQUE, CodedValues.TPC_SIM, responsavel) &&
                ParamSist.paramEquals(CodedValues.TPC_TEM_CONTROLE_DE_COMPULSORIOS, CodedValues.TPC_SIM, responsavel);

        final GrupoParametroServico grupo = new GrupoParametroServico(ApplicationResourcesHelper.getMessage("rotulo.servico.controle.compulsorio", responsavel));

        if (temControleCompulsorios) {
            if (paramSvcCseMap.containsKey(CodedValues.TPS_SERVICO_COMPULSORIO)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_SERVICO_COMPULSORIO);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.servico.compulsorio", responsavel));
                param.setDominio("SN");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_SERVICO_COMPULSORIO));
                param.setValorPadrao("0");
                param.setOnClick("controlaCamposSvcCompulsorio()");
                grupo.addParametros(param);
            }

            if (paramSvcCseMap.containsKey(CodedValues.TPS_CONSIDERA_MARGEM_REST_RESERVA_COMPULSORIO)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_CONSIDERA_MARGEM_REST_RESERVA_COMPULSORIO);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.servico.compulsorio.considera.margem.rest.atual", responsavel));
                param.setDominio("SN");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_CONSIDERA_MARGEM_REST_RESERVA_COMPULSORIO));
                param.setValorPadrao("0");
                grupo.addParametros(param);
            }

            if (paramSvcCseMap.containsKey(CodedValues.TPS_RETIRAVEL_POR_SVC_COMP_PRIORITARIO)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_RETIRAVEL_POR_SVC_COMP_PRIORITARIO);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.permitido.retirar.folha.por.compulsorio.prioritario", responsavel));
                param.setDominio("SN");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_RETIRAVEL_POR_SVC_COMP_PRIORITARIO));
                param.setValorPadrao("0");
                grupo.addParametros(param);
            }
        }

        return grupo;
    }

    private GrupoParametroServico carregarGrupoCorrecaoValorPresente(Map<String, String> paramSvcCseMap, AcessoSistema responsavel) throws CoeficienteCorrecaoControllerException {
        final GrupoParametroServico grupo = new GrupoParametroServico(ApplicationResourcesHelper.getMessage("rotulo.servico.correcao.valor.presente", responsavel));

        if (paramSvcCseMap.containsKey(CodedValues.TPS_POSSUI_CORRECAO_VALOR_PRESENTE)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_POSSUI_CORRECAO_VALOR_PRESENTE);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.correcao.valor.presente", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_POSSUI_CORRECAO_VALOR_PRESENTE));
            param.setValorPadrao("0");
            param.setOnClick("controlaCamposCorrecaoVlrPresente()");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_CALCULO_VALOR_ACUMULADO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_CALCULO_VALOR_ACUMULADO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.calculo.coeficiente.valor.acumulado", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_CALCULO_VALOR_ACUMULADO));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_FORMA_CALCULO_CORRECAO_VLR_PRESENTE)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_FORMA_CALCULO_CORRECAO_VLR_PRESENTE);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.servico.forma.calculo.correcao.valor.presente", responsavel));
            param.setCombo(true);
            param.setComboValues(coeficienteCorrecaoController.lstTipoCoeficienteCorrecao());
            param.setCampoValor(Columns.TCC_CODIGO);
            param.setCampoLabel(Columns.TCC_DESCRICAO);
            param.setLabelNaoSelecionado(ApplicationResourcesHelper.getMessage("rotulo.campo.forma.correcao", responsavel));
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_FORMA_CALCULO_CORRECAO_VLR_PRESENTE));
            grupo.addParametros(param);
        }

        return grupo;
    }

    private GrupoParametroServico carregarGrupoControleTetoDesconto(Map<String, String> paramSvcCseMap, AcessoSistema responsavel) {
        final GrupoParametroServico grupo = new GrupoParametroServico(ApplicationResourcesHelper.getMessage("rotulo.servico.controle.teto.desconto", responsavel));

        if (paramSvcCseMap.containsKey(CodedValues.TPS_POSSUI_CONTROLE_TETO_DESCONTO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_POSSUI_CONTROLE_TETO_DESCONTO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.controle.teto.desconto", responsavel));
            param.setDominio("ESCOLHA[" +
                    "1=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.controle.teto.desconto.sim.cargo.servidor", responsavel) + ";" +
                    "0=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.controle.teto.desconto.nao", responsavel) + "]");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_POSSUI_CONTROLE_TETO_DESCONTO));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PERCENTUAL_MAXIMO_PERMITIDO_VLR_REF)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PERCENTUAL_MAXIMO_PERMITIDO_VLR_REF);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.controle.teto.percentual.maximo", responsavel));
            param.setDominio("FLOAT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PERCENTUAL_MAXIMO_PERMITIDO_VLR_REF));
            grupo.addParametros(param);
        }

        return grupo;
    }

    private GrupoParametroServico carregarGrupoControleGAP(Map<String, String> paramSvcCseMap, AcessoSistema responsavel) {
        final GrupoParametroServico grupo = new GrupoParametroServico(ApplicationResourcesHelper.getMessage("rotulo.servico.controle.acordo.gap", responsavel));

        if (paramSvcCseMap.containsKey(CodedValues.TPS_SERVICO_TIPO_GAP)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_SERVICO_TIPO_GAP);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.acordo.gap", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_SERVICO_TIPO_GAP));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_MES_INICIO_DESCONTO_GAP)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_MES_INICIO_DESCONTO_GAP);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.acordo.gap.mes.inicio", responsavel));
            param.setDominio("MES");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_MES_INICIO_DESCONTO_GAP));
            grupo.addParametros(param);
        }

        return grupo;
    }

    private GrupoParametroServico carregarGrupoSimuladorConsignacao(Map<String, String> paramSvcCseMap, AcessoSistema responsavel) {
        final boolean temSimulacaoConsignacao = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel);

        final GrupoParametroServico grupo = new GrupoParametroServico(ApplicationResourcesHelper.getMessage("rotulo.servico.simulador.consignacao", responsavel));

        if (temSimulacaoConsignacao) {
            if (paramSvcCseMap.containsKey(CodedValues.TPS_QTD_CSA_PERMITIDAS_SIMULADOR)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_QTD_CSA_PERMITIDAS_SIMULADOR);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.quantidade.csa.simulador", responsavel));
                param.setDominio("INT");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_QTD_CSA_PERMITIDAS_SIMULADOR));
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_BLOQUEIA_RESERVA_LIMITE_SIMULADOR) && paramSvcCseMap.containsKey(CodedValues.TPS_QTD_CSA_PERMITIDAS_SIMULADOR)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_BLOQUEIA_RESERVA_LIMITE_SIMULADOR);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.bloqueia.reserva.limite.simulador", responsavel));
                param.setDominio("SN");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_BLOQUEIA_RESERVA_LIMITE_SIMULADOR));
                param.setValorPadrao("0");
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIGE_CODIGO_AUTORIZACAO_CONF_SOLIC)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_EXIGE_CODIGO_AUTORIZACAO_CONF_SOLIC);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.exige.codigo.autorizacao.confirmar.solicitacao", responsavel));
                param.setDominio("SN");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIGE_CODIGO_AUTORIZACAO_CONF_SOLIC));
                param.setValorPadrao("0");
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.idade.necessaria.solicitar.contratos", responsavel));
                param.setDominio("COMPOSTO{"
                        + ApplicationResourcesHelper.getMessage("rotulo.param.svc.idade.necessaria.solicitar.contratos.minima", responsavel) + ":INT|"
                        + ApplicationResourcesHelper.getMessage("rotulo.param.svc.idade.necessaria.solicitar.contratos.maxima", responsavel) + ":INT}");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO));
                grupo.addParametros(param);
            }
            if (paramSvcCseMap.containsKey(CodedValues.TPS_EXIGENCIA_CONFIRMACAO_LEITURA_SERVIDOR)) {
                final ParametroServico param = new ParametroServico();
                param.setCodigo(CodedValues.TPS_EXIGENCIA_CONFIRMACAO_LEITURA_SERVIDOR);
                param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.exigencia.confirmacao.leitura.servidor", responsavel));
                param.setDominio("SN");
                param.setValorPadrao("0");
                param.setValor(paramSvcCseMap.get(CodedValues.TPS_EXIGENCIA_CONFIRMACAO_LEITURA_SERVIDOR));
                grupo.addParametros(param);
            }
        }

        return grupo;
    }

    private GrupoParametroServico carregarGrupoModuloBeneficios(Map<String, String> paramSvcCseMap, AcessoSistema responsavel) {
        final GrupoParametroServico grupo = new GrupoParametroServico(ApplicationResourcesHelper.getMessage("rotulo.servico.beneficios", responsavel));

        if (paramSvcCseMap.containsKey(CodedValues.TPS_TEM_SUBSIDIO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_TEM_SUBSIDIO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.tem.subsidio", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_TEM_SUBSIDIO));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_ORDEM_PRIORIDADE_SUBSIDIO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_ORDEM_PRIORIDADE_SUBSIDIO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.ordem.prioridade.aplicacao.subsidio", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_ORDEM_PRIORIDADE_SUBSIDIO));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.pai.mae.titulares.solteiros.divorciados.subsidio", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_TIPO_CALCULO_SUBSIDIO)) {
            final String dominioTpsCalculoSubsidio = "SELECAO[" +
                    "V=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.calculo.subsidio.valor", responsavel) +
                    ";S=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.calculo.subsidio.percentual.salario", responsavel) +
                    ";B=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.calculo.subsidio.percentual.valor", responsavel) +
                    ";D=" + ApplicationResourcesHelper.getMessage("rotulo.param.svc.calculo.subsidio.percentual.desconto.salario", responsavel) + "]";

            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_TIPO_CALCULO_SUBSIDIO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.calculo.subsidio", responsavel));
            param.setDominio(dominioTpsCalculoSubsidio);
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_TIPO_CALCULO_SUBSIDIO));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_AGREGADO_PODE_TER_SUBSIDIO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_AGREGADO_PODE_TER_SUBSIDIO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.agregado.pode.ter.subsidio", responsavel));
            param.setDominio("SN");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_AGREGADO_PODE_TER_SUBSIDIO));
            param.setValorPadrao("0");
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_QTDE_SUBSIDIO_POR_NATUREZA)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_QTDE_SUBSIDIO_POR_NATUREZA);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.qtde.subisio.mesma.natureza", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_QTDE_SUBSIDIO_POR_NATUREZA));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_IDADE_MAX_DEPENDENTE_DIREITO_SUBSIDIO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_IDADE_MAX_DEPENDENTE_DIREITO_SUBSIDIO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.idade.maxima.dependente.subsidio", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_IDADE_MAX_DEPENDENTE_DIREITO_SUBSIDIO));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_IDADE_MAX_DEPENDENTE_EST_SUBSIDIO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_IDADE_MAX_DEPENDENTE_EST_SUBSIDIO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.idade.maxima.dependente.estudante.subsidio", responsavel));
            param.setDominio("INT");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_IDADE_MAX_DEPENDENTE_EST_SUBSIDIO));
            grupo.addParametros(param);
        }
        if (paramSvcCseMap.containsKey(CodedValues.TPS_DATA_LIMITE_VIGENCIA_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO)) {
            final ParametroServico param = new ParametroServico();
            param.setCodigo(CodedValues.TPS_DATA_LIMITE_VIGENCIA_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO);
            param.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.param.svc.data.limite.pai.mae.titulares.solteiros.divorciados.subsidio", responsavel));
            param.setDominio("DATA");
            param.setValor(paramSvcCseMap.get(CodedValues.TPS_DATA_LIMITE_VIGENCIA_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO));
            grupo.addParametros(param);
        }

        return grupo;
    }

    @RequestMapping(params = { "acao=listarServicosCsa" })
    private String listarServicosCsa(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        StringBuilder titulo = new StringBuilder().append(ApplicationResourcesHelper.getMessage("rotulo.convenio.lista.servicos.titulo", responsavel));
        final String subTitulo = JspHelper.verificaVarQryStr(request, "titulo");
        if (!"".equals(subTitulo)) {
            titulo.append(" - ").append(subTitulo);
        }

        final String org_codigo = (responsavel.isOrg()) ? responsavel.getCodigoEntidade() : null;

        // Obtem o parametro codigo da consignataria
        final String csa_codigo = JspHelper.verificaVarQryStr(request, "csa");
        final String csa_nome_link = JspHelper.verificaVarQryStr(request, "titulo");

        if (TextHelper.isNull(org_codigo) && TextHelper.isNull(csa_codigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        /* servico*/
        final boolean podeEditarCsa = responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNATARIAS);
        final boolean podeEditarSvc = responsavel.temPermissao(CodedValues.FUN_EDT_SERVICOS);
        final boolean podeConsultarSvc = responsavel.temPermissao(CodedValues.FUN_CONS_SERVICOS);
        final boolean podeEditarPrazo = responsavel.temPermissao(CodedValues.FUN_EDT_PRAZO);
        final boolean podeConsultarPrazo = responsavel.temPermissao(CodedValues.FUN_CONS_PRAZO);
        final boolean podeEditarCnv = responsavel.temPermissao(CodedValues.FUN_EDT_CONVENIOS);
        final boolean podeConsultarCnv = responsavel.temPermissao(CodedValues.FUN_CONS_CONVENIOS);
        final boolean podeEditarIndices = responsavel.temPermissao(CodedValues.FUN_EDT_INDICES);
        final boolean podeBloquearPostoCsaSvc = responsavel.temPermissao(CodedValues.FUN_BLOQUEAR_POSTO_POR_CSA_SVC);

        // Verifica se sistema permite cadastro de índice para o serviço
        final boolean permiteCadIndice = ParamSist.paramEquals(CodedValues.TPC_PERMITE_CAD_INDICE, CodedValues.TPC_SIM, responsavel);
        final boolean csePodeEditarParamCnv = ParamSist.paramEquals(CodedValues.TPC_CSE_CONFIG_PARAM_SVC_CSA, CodedValues.TPC_SIM, responsavel);

        List<TransferObject> servicos = null;

        final String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
        int filtro_tipo = -1;
        try {
            filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
        } catch (final Exception ex1) {
        }

        try {
            final CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.CNV_ORG_CODIGO, org_codigo);
            criterio.setAttribute(Columns.CNV_CSA_CODIGO, csa_codigo);

            // -------------- Seta Criterio da Listagem ------------------
            // Bloqueado
            if (filtro_tipo == 0) {
                criterio.setAttribute(Columns.CNV_SCV_CODIGO, CodedValues.SCV_INATIVO);
                // Desbloqueado
            } else if (filtro_tipo == 1) {
                criterio.setAttribute(Columns.CNV_SCV_CODIGO, CodedValues.NOT_EQUAL_KEY + CodedValues.SCV_INATIVO);
                // Existe Consignação
            } else if (filtro_tipo == 4) {
                criterio.setAttribute(CodedValues.SERVICO_TEM_ADE, CodedValues.TPC_SIM);
                // Não existe Consignação
            } else if (filtro_tipo == 5) {
                criterio.setAttribute(CodedValues.SERVICO_TEM_ADE, CodedValues.TPC_NAO);
                // Outros
            } else if (!"".equals(filtro) && (filtro_tipo != -1)) {
                String campo = null;

                switch (filtro_tipo) {
                    case 2:
                        campo = Columns.SVC_IDENTIFICADOR;
                        break;
                    case 3:
                        campo = Columns.SVC_DESCRICAO;
                        break;
                    default:
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
                criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
            }
            // ---------------------------------------

            final int total = convenioController.countCnvScvCodigo(criterio, responsavel);
            final int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (final Exception ex) {
            }

            // Monta lista de parâmetros através dos parâmetros de request
            final Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
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

            servicos = convenioController.listCnvScvCodigo(criterio, offset, size, responsavel);

            String linkListagem = request.getRequestURI() + "?acao=listarServicosCsa&" + SynchronizerToken.generateToken4URL(request);
            linkListagem = linkListagem + "&tipo=consultar&csa=" + csa_codigo + "&titulo=" + java.net.URLEncoder.encode(csa_nome_link, "ISO-8859-1");
            configurarPaginador(linkListagem, "rotulo.servico.lista", total, size, requestParams, false, request, model);

            model.addAttribute("servicos", servicos);
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            LOG.error(ex.getMessage(), ex);
            servicos = null;
        }

        //parâmetro e função que diz se utiliza bloqueio na reserva de margem por vinculo do servidor
        boolean utiBloqVincSer = ((ParamSist.getInstance().getParam(CodedValues.TPC_UTILIZA_BLOQ_VINC_SERVIDOR, responsavel) != null) && CodedValues.TPC_SIM.equals(ParamSist.getInstance().getParam(CodedValues.TPC_UTILIZA_BLOQ_VINC_SERVIDOR, responsavel).toString()));
        utiBloqVincSer = utiBloqVincSer && (responsavel.isSup() || responsavel.isCsa()) && responsavel.temPermissao(CodedValues.FUN_EDT_BLOQUEIO_VINCULO_SERVIDOR);

        model.addAttribute("titulo", titulo.toString());
        model.addAttribute("filtro", filtro);
        model.addAttribute("filtro_tipo", filtro_tipo);
        model.addAttribute("podeEditarCsa", podeEditarCsa);
        model.addAttribute("podeEditarSvc", podeEditarSvc);
        model.addAttribute("podeConsultarSvc", podeConsultarSvc);
        model.addAttribute("podeEditarPrazo", podeEditarPrazo);
        model.addAttribute("podeConsultarPrazo", podeConsultarPrazo);
        model.addAttribute("podeEditarCnv", podeEditarCnv);
        model.addAttribute("podeConsultarCnv", podeConsultarCnv);
        model.addAttribute("podeEditarIndices", podeEditarIndices);
        model.addAttribute("podeBloquearPostoCsaSvc", podeBloquearPostoCsaSvc);
        model.addAttribute("permiteCadIndice", permiteCadIndice);
        model.addAttribute("csePodeEditarParamCnv", csePodeEditarParamCnv);
        model.addAttribute("org_codigo", org_codigo);
        model.addAttribute("csa_codigo", csa_codigo);
        model.addAttribute("csa_nome_link", csa_nome_link);
        model.addAttribute("utiBloqVincSer", utiBloqVincSer);

        return viewRedirect("jsp/manterServico/listarServicosCsa", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=consultarCampo" })
    private String consultarCampo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        boolean podeEditar = responsavel.temPermissao(CodedValues.FUN_EDT_SERVICOS);

        final String svc_codigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
        final String cnv_codigo = JspHelper.verificaVarQryStr(request, "CNV_CODIGO");

        ServicoTransferObject servico = new ServicoTransferObject();
        if ((svc_codigo != null) && !"".equals(svc_codigo)) {
            try {
                servico = convenioController.findServico(svc_codigo, responsavel);
                final String svcIdentificador = (servico.getAttribute(Columns.SVC_IDENTIFICADOR) != null ? servico.getAttribute(Columns.SVC_IDENTIFICADOR).toString() : "");
                final String svcDescricao = (servico.getAttribute(Columns.SVC_DESCRICAO) != null ? servico.getAttribute(Columns.SVC_DESCRICAO).toString() : "");
                final String svcPrioridade = (servico.getAttribute(Columns.SVC_PRIORIDADE) != null ? servico.getAttribute(Columns.SVC_PRIORIDADE).toString() : "");
                model.addAttribute("svcIdentificador", svcIdentificador);
                model.addAttribute("svcDescricao", svcDescricao);
                model.addAttribute("svcPrioridade", svcPrioridade);
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                LOG.error(ex.getMessage(), ex);
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        TransferObject to = new CustomTransferObject();
        if ((cnv_codigo != null) && !"".equals(cnv_codigo)) {
            podeEditar = responsavel.temPermissao(CodedValues.FUN_EDT_CONVENIOS);
            try {
                final List<TransferObject> lstConvenio = convenioController.lstCnvBySvcCodigo(svc_codigo, cnv_codigo, -1, -1, responsavel);
                if (!lstConvenio.isEmpty()) {
                    to = lstConvenio.get(0);
                }
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                LOG.error(ex.getMessage(), ex);
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            final String cnvCodVerba = (to.getAttribute(Columns.CNV_COD_VERBA) != null ? to.getAttribute(Columns.CNV_COD_VERBA).toString() : "");
            final String cnvPrioridade = (to.getAttribute(Columns.CNV_PRIORIDADE) != null ? to.getAttribute(Columns.CNV_PRIORIDADE).toString() : "");
            final String csaIdentificador = (to.getAttribute(Columns.CSA_IDENTIFICADOR) != null ? to.getAttribute(Columns.CSA_IDENTIFICADOR).toString() : "");
            final String csaNome = (to.getAttribute(Columns.CSA_NOME) != null ? to.getAttribute(Columns.CSA_NOME).toString() : "");

            model.addAttribute("cnvCodVerba", cnvCodVerba);
            model.addAttribute("cnvPrioridade", cnvPrioridade);
            model.addAttribute("csaIdentificador", csaIdentificador);
            model.addAttribute("csaNome", csaNome);
        }
        model.addAttribute("podeEditar", podeEditar);
        model.addAttribute("svc_codigo", svc_codigo);
        model.addAttribute("cnv_codigo", cnv_codigo);

        return viewRedirect("jsp/manterServico/editarCampo", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editarCampo" })
    private String editarCampo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        boolean podeEditar = responsavel.temPermissao(CodedValues.FUN_EDT_SERVICOS);

        final String svc_codigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
        final String cnv_codigo = JspHelper.verificaVarQryStr(request, "CNV_CODIGO");

        //Atualização campos serviço
        if ((svc_codigo != null) && ((cnv_codigo == null) || "".equals(cnv_codigo))) {
            try {
                final ServicoTransferObject stoServico = new ServicoTransferObject(svc_codigo);
                final String prioridade = JspHelper.verificaVarQryStr(request, "SVC_PRIORIDADE");
                if ((prioridade != null) && !"".equals(prioridade)) {
                    stoServico.setSvcPrioridade(Integer.valueOf(prioridade));
                } else {
                    stoServico.setSvcPrioridade(null);
                }
                convenioController.updateServico(stoServico, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.salvar.servico.sucesso", responsavel));
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                LOG.error(ex.getMessage(), ex);
            }
        }

        //Atualização campos convenio
        try {
            final String cnv_cod_verba = JspHelper.verificaVarQryStr(request, "CNV_COD_VERBA");
            final String cnv_prioridade = JspHelper.verificaVarQryStr(request, "CNV_PRIORIDADE");
            if ((cnv_cod_verba != null) && (cnv_prioridade != null) && !"".equals(cnv_prioridade)) {
                convenioController.setCnvPrioridade(cnv_cod_verba, cnv_prioridade, responsavel);
            } else {
                convenioController.setCnvPrioridade(cnv_cod_verba, null, responsavel);
            }
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.endereco.alteracoes.salvas.sucesso", responsavel));
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            LOG.error(ex.getMessage(), ex);
        }

        ServicoTransferObject servico = new ServicoTransferObject();
        if ((svc_codigo != null) && !"".equals(svc_codigo)) {
            try {
                servico = convenioController.findServico(svc_codigo, responsavel);
                final String svcIdentificador = (servico.getAttribute(Columns.SVC_IDENTIFICADOR) != null ? servico.getAttribute(Columns.SVC_IDENTIFICADOR).toString() : "");
                final String svcDescricao = (servico.getAttribute(Columns.SVC_DESCRICAO) != null ? servico.getAttribute(Columns.SVC_DESCRICAO).toString() : "");
                final String svcPrioridade = (servico.getAttribute(Columns.SVC_PRIORIDADE) != null ? servico.getAttribute(Columns.SVC_PRIORIDADE).toString() : "");
                model.addAttribute("svcIdentificador", svcIdentificador);
                model.addAttribute("svcDescricao", svcDescricao);
                model.addAttribute("svcPrioridade", svcPrioridade);
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                LOG.error(ex.getMessage(), ex);
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        TransferObject to = new CustomTransferObject();
        if ((cnv_codigo != null) && !"".equals(cnv_codigo)) {
            podeEditar = responsavel.temPermissao(CodedValues.FUN_EDT_CONVENIOS);
            try {
                final List<TransferObject> lstConvenio = convenioController.lstCnvBySvcCodigo(svc_codigo, cnv_codigo, -1, -1, responsavel);
                if (!lstConvenio.isEmpty()) {
                    to = lstConvenio.get(0);
                }
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                LOG.error(ex.getMessage(), ex);
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

            }
            final String cnvCodVerba = (to.getAttribute(Columns.CNV_COD_VERBA) != null ? to.getAttribute(Columns.CNV_COD_VERBA).toString() : "");
            final String cnvPrioridade = (to.getAttribute(Columns.CNV_PRIORIDADE) != null ? to.getAttribute(Columns.CNV_PRIORIDADE).toString() : "");
            final String csaIdentificador = (to.getAttribute(Columns.CSA_IDENTIFICADOR) != null ? to.getAttribute(Columns.CSA_IDENTIFICADOR).toString() : "");
            final String csaNome = (to.getAttribute(Columns.CSA_NOME) != null ? to.getAttribute(Columns.CSA_NOME).toString() : "");

            model.addAttribute("cnvCodVerba", cnvCodVerba);
            model.addAttribute("cnvPrioridade", cnvPrioridade);
            model.addAttribute("csaIdentificador", csaIdentificador);
            model.addAttribute("csaNome", csaNome);
        }
        model.addAttribute("podeEditar", podeEditar);
        model.addAttribute("svc_codigo", svc_codigo);
        model.addAttribute("cnv_codigo", cnv_codigo);

        return viewRedirect("jsp/manterServico/editarCampo", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=consultarServicoOrg" })
    private String consultarServicoOrg(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String orgao = JspHelper.verificaVarQryStr(request, "org");
        final String servico = JspHelper.verificaVarQryStr(request, "svc");
        String cnv_consolida_descontos = JspHelper.verificaVarQryStr(request, "CNV_CONSOLIDA_DESCONTOS");

        final boolean podeEditarSvc = responsavel.temPermissao(CodedValues.FUN_EDT_SERVICOS);

        CustomTransferObject convenio = null;
        try {
            final List<TransferObject> convenios = convenioController.getCnvConsolidaDescontos(servico, orgao, responsavel);
            convenio = (CustomTransferObject) convenios.get(0);
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String svc_descricao = (String) convenio.getAttribute(Columns.SVC_DESCRICAO);
        final String svc_identificador = (String) convenio.getAttribute(Columns.SVC_IDENTIFICADOR);
        final String org_nome = (String) convenio.getAttribute(Columns.ORG_NOME);
        cnv_consolida_descontos = (String) convenio.getAttribute(Columns.CNV_CONSOLIDA_DESCONTOS);

      if ((cnv_consolida_descontos == null) || "".equals(cnv_consolida_descontos)) {
        cnv_consolida_descontos = (String)ParamSist.getInstance().getParam(CodedValues.TPC_CONSOLIDA_DESCONTOS_MOVIMENTO, responsavel);
      }
      model.addAttribute("podeEditarSvc", podeEditarSvc);
      model.addAttribute("orgao", orgao);
      model.addAttribute("servico", servico);
      model.addAttribute("cnv_consolida_descontos", cnv_consolida_descontos);
      model.addAttribute("svc_descricao", svc_descricao);
      model.addAttribute("svc_identificador", svc_identificador);
      model.addAttribute("org_nome", org_nome);

        return viewRedirect("jsp/manterServico/editarServicoOrg", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editarServicoOrg" })
    private String editarServicoOrg(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String orgao = JspHelper.verificaVarQryStr(request, "org");
        final String servico = JspHelper.verificaVarQryStr(request, "svc");
        final String MM_update = JspHelper.verificaVarQryStr(request, "MM_update");
        String cnv_consolida_descontos = JspHelper.verificaVarQryStr(request, "CNV_CONSOLIDA_DESCONTOS");

        final boolean podeEditarSvc = responsavel.temPermissao(CodedValues.FUN_EDT_SERVICOS);

        try {
            if (!"".equals(orgao) && !"".equals(servico) && "form1".equals(MM_update) && !"".equals(cnv_consolida_descontos)) {
                convenioController.setCnvConsolidaDescontos(servico, orgao, cnv_consolida_descontos, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.salvar.servico.sucesso", responsavel));
            }
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servico.atualizar", responsavel, ex.getMessage()));
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

        }
        CustomTransferObject convenio = null;

        try {
            final List<TransferObject> convenios = convenioController.getCnvConsolidaDescontos(servico, orgao, responsavel);
            convenio = (CustomTransferObject) convenios.get(0);
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String svc_descricao = (String) convenio.getAttribute(Columns.SVC_DESCRICAO);
        final String svc_identificador = (String) convenio.getAttribute(Columns.SVC_IDENTIFICADOR);
        final String org_nome = (String) convenio.getAttribute(Columns.ORG_NOME);
        cnv_consolida_descontos = (String) convenio.getAttribute(Columns.CNV_CONSOLIDA_DESCONTOS);

        if ((cnv_consolida_descontos == null) || "".equals(cnv_consolida_descontos)) {
            cnv_consolida_descontos = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CONSOLIDA_DESCONTOS_MOVIMENTO, responsavel);
        }
        model.addAttribute("podeEditarSvc", podeEditarSvc);
        model.addAttribute("orgao", orgao);
        model.addAttribute("servico", servico);
        model.addAttribute("MM_update", MM_update);
        model.addAttribute("cnv_consolida_descontos", cnv_consolida_descontos);
        model.addAttribute("svc_descricao", svc_descricao);
        model.addAttribute("svc_identificador", svc_identificador);
        model.addAttribute("org_nome", org_nome);

        return viewRedirect("jsp/manterServico/editarServicoOrg", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=efetivarAcaoServico" })
    private String efetivarAcaoServico(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        ServicoTransferObject servico = null;
        final String svcCodigo = JspHelper.verificaVarQryStr(request, "codigo");
        final String operacao = JspHelper.verificaVarQryStr(request, "operacao");
        try {
            servico = convenioController.findServico(svcCodigo, responsavel);
            final String svcIdentificador = (servico.getAttribute(Columns.SVC_IDENTIFICADOR) != null ? servico.getAttribute(Columns.SVC_IDENTIFICADOR).toString() : "");
            final String svcDescricao = (servico.getAttribute(Columns.SVC_DESCRICAO) != null ? servico.getAttribute(Columns.SVC_DESCRICAO).toString() : "");
            model.addAttribute("svcIdentificador", svcIdentificador);
            model.addAttribute("svcDescricao", svcDescricao);
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        final String reqColumnsStr = "TMO_CODIGO";
        final String msgErro = JspHelper.verificaCamposForm(request, session, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");

        final Map<String, String[]> parametros = new HashMap<>();
        parametros.put("acao", new String[] { "bloquearServico" });
        parametros.put("codigo", new String[] { svcCodigo });
        parametros.put("_skip_history_", new String[] { "true" });

        String funCodigo = "";
        String tituloPagina = "";
        String msgConfirmacao = "";

        if ("bloquear".equals(operacao)) {
            final String status = JspHelper.verificaVarQryStr(request, "status");
            funCodigo = CodedValues.FUN_EDT_SERVICOS;
            tituloPagina = "1".equals(status) ? ApplicationResourcesHelper.getMessage("rotulo.efetiva.acao.servico.bloquear", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.efetiva.acao.servico.desbloquear", responsavel);
            msgConfirmacao = "1".equals(status) ? ApplicationResourcesHelper.getMessage("mensagem.confirmacao.bloqueio.servico", responsavel, servico.getSvcDescricao()) : ApplicationResourcesHelper.getMessage("mensagem.confirmacao.desbloqueio.servico", responsavel, servico.getSvcDescricao());
            parametros.put("status", new String[] { status });
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("operacao", operacao);
        model.addAttribute("parametros", parametros);
        model.addAttribute("reqColumnsStr", reqColumnsStr);
        model.addAttribute("svcCodigo", svcCodigo);
        model.addAttribute("tituloPagina", tituloPagina);
        model.addAttribute("msgConfirmacao", msgConfirmacao);
        model.addAttribute("msgErro", msgErro);

        // Se não exige motivo de operação, redireciona para a ação final
        if (!FuncaoExigeMotivo.getInstance().exists(funCodigo, responsavel)) {
            try {
                // Repassa o token salvo, pois o método irá revalidar o token
                request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                return bloquearServico(request, response, session, model);
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                LOG.error(ex.getMessage(), ex);
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        return viewRedirect("jsp/manterServico/efetivarAcaoServico", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=inserirServico" })
    private String inserirServico(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            final List<TransferObject> naturezas = servicoController.lstNaturezasServicos(false);
            final List<TransferObject> servicos = convenioController.lstServicos(null, responsavel);
            final String sim = CodedValues.TPC_SIM;

            final String voltarAoComeco = request.getRequestURI() + "?acao=iniciar";
            final String voltar = TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(voltarAoComeco, request));

            model.addAttribute("voltar", voltar);

            model.addAttribute("naturezas", naturezas);
            model.addAttribute("servicos", servicos);
            model.addAttribute("sim", sim);
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return viewRedirect("jsp/manterServico/inserirServico", request, session, model, responsavel);
    }

    //=====================================================================================================================================================================
    //GRAVA AS AÇÕES DA MANUTENÇÃO DE SERVIÇO.
    //=====================================================================================================================================================================
    @RequestMapping(params = { "acao=recarregarNseCodigo" })
    private String recarregarNseCodigo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Metodo para fazer a atualização do nse_codigo quando o usuario realiza a troca
        final String svcNseCodigo = JspHelper.verificaVarQryStr(request, "NSE_CODIGO");
        final String svcCodigo = JspHelper.verificaVarQryStr(request, "codigo");

        final String linkRetorno = request.getRequestURI() + "?acao=editarServico&svc=" + svcCodigo + "&novoNseCodigo=" + svcNseCodigo + "&SVC_CODIGO=" + svcCodigo + "&back=1";
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(linkRetorno, request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(params = { "acao=salvarServico" })
    private String salvarServico(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ParseException, ParametroControllerException, ConvenioControllerException, ServletException, IOException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String[] camposRequeridos = { "SVC_IDENTIFICADOR", "SVC_DESCRICAO", "NSE_CODIGO" };
        final String[] mensagensErro = {
                  ApplicationResourcesHelper.getMessage("mensagem.informe.servico.codigo", responsavel)
                  , ApplicationResourcesHelper.getMessage("mensagem.informe.servico.descricao", responsavel)
                  , ApplicationResourcesHelper.getMessage("mensagem.informe.servico.natureza", responsavel)
                };
        final String msgErro = JspHelper.verificaCamposForm(request, camposRequeridos, mensagensErro);
        if (msgErro.length() > 0) {
            session.setAttribute(CodedValues.MSG_ERRO, msgErro);

        } else {
            final String svcCodigo = JspHelper.verificaVarQryStr(request, "codigo");
            final String copiaSvc = JspHelper.verificaVarQryStr(request, "copia_para_svc_corrente");

            List<ParamTarifCseTO> listaParamTarifCse = null;
            List<ParamSvcCseTO> listaParamSvcCse = null;
            Map<String, List<String>> relacionamentos = null;

            // Se não vai haver cópia de parâmetros de outro serviço para este, continua com a atualização de parâmetros
            if ((copiaSvc == null) || "".equals(copiaSvc)) {
                // Salva os parametros de tarifação eventualmente existentes na tela
                final StringTokenizer stn = new StringTokenizer(JspHelper.verificaVarQryStr(request, "PCV_CAMPOS"), ";");
                listaParamTarifCse = new ArrayList<>();
                while (stn.hasMoreTokens()) {
                    boolean inserir = false;
                    final String pcv_campo = stn.nextToken();
                    final String tpt_codigo = pcv_campo.substring(pcv_campo.indexOf('-') + 1, pcv_campo.length());
                    final String pcv_codigo = JspHelper.verificaVarQryStr(request, "PCV_CODIGO-" + tpt_codigo);
                    final String pcv_vlr = JspHelper.verificaVarQryStr(request, "PCV_VLR-" + tpt_codigo);
                    final String pcv_forma_calc = JspHelper.verificaVarQryStr(request, "PCV_FORMA_CALC-" + tpt_codigo);
                    final String pcv_base_calc = JspHelper.verificaVarQryStr(request, "PCV_BASE_CALC-" + tpt_codigo);
                    final String pcv_ativo = JspHelper.verificaVarQryStr(request, "PCV_ATIVO-" + tpt_codigo);

                    if ("".equals(pcv_codigo)) {
                        inserir = true;
                    }

                    ParamTarifCseTO param = null;
                    if (inserir) {
                        param = new ParamTarifCseTO();
                    } else {
                        param = new ParamTarifCseTO(pcv_codigo);
                    }
                    param.setPcvAtivo("".equals(pcv_ativo) ? null : Short.valueOf(pcv_ativo));
                    param.setPcvBaseCalc("".equals(pcv_base_calc) ? null : Integer.valueOf(pcv_base_calc));
                    param.setPcvFormaCalc("".equals(pcv_forma_calc) ? null : Integer.valueOf(pcv_forma_calc));
                    param.setPcvVlr("".equals(pcv_vlr) ? null : new BigDecimal(NumberHelper.reformat(pcv_vlr, NumberHelper.getLang(), "en")));
                    param.setSvcCodigo(svcCodigo);
                    param.setTptCodigo(tpt_codigo);
                    param.setCseCodigo(CodedValues.CSE_CODIGO_SISTEMA);
                    listaParamTarifCse.add(param);
                }

                // Salva os parametros de serviço
                final StringTokenizer stn2 = new StringTokenizer(JspHelper.verificaVarQryStr(request, "PSE_CAMPOS"), ";");

                listaParamSvcCse = new ArrayList<>();
                while (stn2.hasMoreTokens()) {
                    final String pse_campo = stn2.nextToken();

                    String pse_vlr = "";
                    String pse_vlr_ref = null;
                    if (JspHelper.verificaVarQryStr(request, pse_campo).indexOf(";") > 0) {
                        final String[] valores = JspHelper.verificaVarQryStr(request, pse_campo).split(";");
                        pse_vlr = (valores.length > 0) && !"-1".equals(valores[0]) ? valores[0].replace(',', '.').replace("\'", "\'\'") : "";
                        pse_vlr_ref = (valores.length > 1) && !"-1".equals(valores[1]) ? valores[1].replace(',', '.').replace("\'", "\'\'") : null;
                    } else {
                        pse_vlr = JspHelper.verificaVarQryStr(request, pse_campo).replace(',', '.').replace('\'', '\'');
                    }

                    if (("PSEVLR_303".equals(pse_campo) || "PSEVLR_304".equals(pse_campo)) && (!TextHelper.isNull(pse_vlr) && (Integer.parseInt(pse_vlr) >= 100))) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.param.svc.compra.percentual.valor.max.nova.parcela.maior.cem", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }

                    final String tps_codigo = pse_campo.substring(pse_campo.indexOf('_') + 1, pse_campo.length());

                    final ParamSvcCseTO paramSvcCse = new ParamSvcCseTO();
                    paramSvcCse.setCseCodigo(CodedValues.CSE_CODIGO_SISTEMA);
                    paramSvcCse.setPseVlr(pse_vlr);
                    paramSvcCse.setPseVlrRef(pse_vlr_ref);
                    paramSvcCse.setSvcCodigo(svcCodigo);
                    paramSvcCse.setTpsCodigo(tps_codigo);

                    listaParamSvcCse.add(paramSvcCse);
                }

                /** SALVA OS RELACIONAMENTOS DESTE SERVIÇO *******************************************************************************/
                //String svcNseCodigo = JspHelper.verificaVarQryStr(request, "NSE_CODIGO");
                relacionamentos = new HashMap<>();
                final Map<String, String> valoresRelacionamentos = new HashMap<>();

                final List<TransferObject> tipoNaturezaRelac = parametroController.lstTipoNatureza(responsavel);
                final Iterator<TransferObject> iterator = tipoNaturezaRelac.iterator();
                CustomTransferObject ctoTnt = null;
                while (iterator.hasNext()) {
                    ctoTnt = (CustomTransferObject) iterator.next();
                    // Recuperando o tnt_codigo
                    final String tntCodigo = ctoTnt.getAttribute(Columns.TNT_CODIGO) != null ? ctoTnt.getAttribute(Columns.TNT_CODIGO).toString() : null;

                    // Montando da mesma forme que montamos na tela de edição
                    final String hlRelacionamentoServicoNome = "svc_destino_tnt_".concat(tntCodigo);

                    valoresRelacionamentos.put(tntCodigo, hlRelacionamentoServicoNome);
                }

                for (final String chave : valoresRelacionamentos.keySet()) {
                    final String valor = valoresRelacionamentos.get(chave).toString();

                    final String[] svcDestino = request.getParameterValues(valor);
                    final List<String> svcCodigoDestino = new ArrayList<>();

                    if (svcDestino != null) {
                        svcCodigoDestino.addAll(Arrays.asList(svcDestino));
                    }
                    relacionamentos.put(chave, svcCodigoDestino);
                }

                // Salva o relacionamento de correção de saldo devedor
                final String tpsCorrecaoSdv = request.getParameter("PSEVLR_" + CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR);
                if (tpsCorrecaoSdv != null) {
                    final List<String> svcCodigoDestino = new ArrayList<>();
                    if ("2".equals(tpsCorrecaoSdv)) {
                        // Correção de saldo devedor em outro serviço
                        final String svcRelacionamentoCorrecao = request.getParameter("svcRelacionamentoCorrecao");
                        if (!TextHelper.isNull(svcRelacionamentoCorrecao)) {
                            svcCodigoDestino.add(svcRelacionamentoCorrecao);
                        }
                    }
                    relacionamentos.put(CodedValues.TNT_CORRECAO_SALDO, svcCodigoDestino);
                }

                /** FIM DOS RELACIONAMENTOS DESTE SERVIÇO *******************************************************************************/

            }

            // Salva os campos do serviço
            try {
                final ServicoTransferObject servico = new ServicoTransferObject(svcCodigo);
                servico.setSvcDescricao(JspHelper.verificaVarQryStr(request, "SVC_DESCRICAO"));
                servico.setSvcIdentificador(JspHelper.verificaVarQryStr(request, "SVC_IDENTIFICADOR"));
                servico.setSvcObs(JspHelper.verificaVarQryStr(request, "SVC_OBS"));

                // Se os parâmetros não foram copiados de outro serviço, continua a atualizar os demais campos
                if ((copiaSvc == null) || "".equals(copiaSvc)) {
                    // Salva codigo do grupo de serviço
                    final String strGrpServico = JspHelper.verificaVarQryStr(request, "TGS_CODIGO");
                    if ((strGrpServico != null) && !"".equals(strGrpServico)) {
                        servico.setSvcTgsCodigo(strGrpServico);
                    } else {
                        servico.setSvcTgsCodigo(null);
                    }

                    final String strNseServico = JspHelper.verificaVarQryStr(request, "NSE_CODIGO");
                    if (!TextHelper.isNull(strNseServico)) {
                        servico.setSvcNseCodigo(strNseServico);
                    } else {
                        throw new ZetraException("mensagem.informe.servico.natureza", responsavel);
                    }

                    // Define prioridade de desconto para o serviço
                    final boolean permitePriorizarServico = ParamSist.paramEquals(CodedValues.TPC_PERMITE_PRIORIZAR_SERVICO, CodedValues.TPC_SIM, responsavel);
                    if (permitePriorizarServico) {
                        final String strSvcPrioridade = JspHelper.verificaVarQryStr(request, "SVC_PRIORIDADE");
                        if ((strSvcPrioridade != null) && !"".equals(strSvcPrioridade)) {
                            servico.setSvcPrioridade(Integer.valueOf(strSvcPrioridade));
                        } else {
                            servico.setSvcPrioridade(null);
                        }
                    }
                    convenioController.updateServico(servico, listaParamSvcCse, listaParamTarifCse, relacionamentos, responsavel);
                    ParamSvcTO.removeParamSvcTO(svcCodigo);

                } else // Copia os parâmetros do serviço selecionado para o serviço atual
                if (((copiaSvc != null) && !"".equals(copiaSvc)) && !svcCodigo.equals(copiaSvc)) {
                    convenioController.copiaServico(copiaSvc, servico, responsavel);
                    ParamSvcTO.removeParamSvcTO(copiaSvc);
                }

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.servico.sucesso", responsavel));

            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            // Copia os parâmetros do serviço atual para os serviços selecionados
            final String[] svcsDestino = request.getParameterValues("copia_svc_corrente");
            if ((svcsDestino != null) && (svcsDestino.length > 0)) {
                for (final String element : svcsDestino) {
                    // Copia os parâmetros do serviço atual para o serviço selecionado
                    if (!svcCodigo.equals(element)) {
                        final ServicoTransferObject servico = new ServicoTransferObject(element);
                        convenioController.copiaServico(svcCodigo, servico, responsavel);
                    }
                }
            }
        }

        final ParamSession paramSession = ParamSession.getParamSession(session);
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(params = { "acao=salvarServicoSobrepoe" })
    public String salvarServicoSobre(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException, ServicoControllerException, ParametroControllerException, SimulacaoControllerException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String svc_codigo = JspHelper.verificaVarQryStr(request, "svc");
        final String rseCodigo = JspHelper.verificaVarQryStr(request, "rseCodigo");

        // Busca os tipos de parâmetro de serviço disponíveis.
        final List<TransferObject> tiposParams = parametroController.lstTipoParamSvcSobrepoe(responsavel);
        final HashMap<Object, Boolean> parametrosSvcSobrepoe = new HashMap<>();
        final Iterator<TransferObject> itParam = tiposParams.iterator();
        while (itParam.hasNext()) {
            final CustomTransferObject paramSvcSobrepoe = (CustomTransferObject) itParam.next();
            if (responsavel.isCse()) {
                parametrosSvcSobrepoe.put(paramSvcSobrepoe.getAttribute(Columns.TPS_CODIGO), Boolean.valueOf((paramSvcSobrepoe.getAttribute(Columns.TPS_CSE_ALTERA) == null) || "".equals(paramSvcSobrepoe.getAttribute(Columns.TPS_CSE_ALTERA)) || CodedValues.TPC_SIM.equals(paramSvcSobrepoe.getAttribute(Columns.TPS_CSE_ALTERA))));
            } else if (responsavel.isSup()) {
                parametrosSvcSobrepoe.put(paramSvcSobrepoe.getAttribute(Columns.TPS_CODIGO), Boolean.valueOf((paramSvcSobrepoe.getAttribute(Columns.TPS_SUP_ALTERA) == null) || "".equals(paramSvcSobrepoe.getAttribute(Columns.TPS_SUP_ALTERA)) || CodedValues.TPC_SIM.equals(paramSvcSobrepoe.getAttribute(Columns.TPS_SUP_ALTERA))));
            }
        }

        final List<String> tpsCodigos = new ArrayList<>();
        if (parametrosSvcSobrepoe.containsKey(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA) && parametrosSvcSobrepoe.get(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA);
        }

        if (!"".equals(svc_codigo)) {
            // Salva os parâmetros do serviço
            try {
                final List<TransferObject> parametros = new ArrayList<>();
                final List<TransferObject> tpsCodigosIgualCse = new ArrayList<>();
                for (final String element : tpsCodigos) {
                    final CustomTransferObject cto = new CustomTransferObject();
                    String psr_vlr = JspHelper.verificaVarQryStr(request, "tps_" + element);
                    if ((CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA.equals(element) || CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA.equals(element)) && !"".equals(psr_vlr)) {
                        psr_vlr = Integer.valueOf(JspHelper.verificaVarQryStr(request, "tps_" + element)).toString();
                    }

                    cto.setAttribute(Columns.PSR_TPS_CODIGO, element);
                    cto.setAttribute(Columns.PSR_SVC_CODIGO, svc_codigo);
                    cto.setAttribute(Columns.PSR_RSE_CODIGO, rseCodigo);
                    cto.setAttribute(Columns.PSR_VLR, !"".equals(psr_vlr) ? psr_vlr : "");
                    parametros.add(cto);

                    if ("1".equals(JspHelper.verificaVarQryStr(request, "check_" + element)) || "".equals(cto.getAttribute(Columns.PSR_VLR))) {
                        tpsCodigosIgualCse.add(cto);
                    }
                }

                parametroController.updateParamSvcSobrepoe(parametros, responsavel);
                parametroController.deleteParamIgualCseRse(tpsCodigosIgualCse, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alteracoes.salvas.sucesso", responsavel));
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        final ParamSession paramSession = ParamSession.getParamSession(session);
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(params = { "acao=excluirServico" })
    private String excluirServico(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        if ((request.getParameter("excluir") != null) && (request.getParameter("codigo") != null)) {
            try {
                final ServicoTransferObject svcRem = new ServicoTransferObject(request.getParameter("codigo"));
                convenioController.removeServico(svcRem, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.excluir.servico.sucesso", responsavel));
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }
        return listarServicos(request, response, session, model);
    }

    @RequestMapping(params = { "acao=bloquearServico" })
    private String bloquearServico(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        if ((request.getParameter("status") != null) && (request.getParameter("codigo") != null)) {
            try {
                String ativo = request.getParameter("status");
                ativo = "1".equals(ativo) ? "0" : "1";
                String mensagem;
                if ("1".equals(ativo)) {
                    mensagem = ApplicationResourcesHelper.getMessage("mensagem.desbloquear.servico.sucesso", responsavel);
                } else {
                    mensagem = ApplicationResourcesHelper.getMessage("mensagem.bloquear.servico.sucesso", responsavel);
                }
                final ServicoTransferObject svcBloq = new ServicoTransferObject(request.getParameter("codigo"));
                svcBloq.setSvcAtivo(Short.valueOf(ativo));

                // Motivo e observação da operação
                if (FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EDT_SERVICOS, responsavel)) {
                    svcBloq.setTmoCodigo(request.getParameter("TMO_CODIGO"));
                    svcBloq.setOseObs(request.getParameter("ADE_OBS"));
                }
                convenioController.updateServico(svcBloq, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, mensagem);
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }

        return listarServicos(request, response, session, model);
    }

    @RequestMapping(params = { "acao=incluirServico" })
    private String incluirServico(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final ParamSession paramSession = ParamSession.getParamSession(session);
        final String link = paramSession.getLastHistory();
        model.addAttribute("link", link);

        final String[] camposRequeridos = { "SVC_IDENTIFICADOR", "SVC_DESCRICAO", "NSE_CODIGO" };
        final String[] mensagensErro = { ApplicationResourcesHelper.getMessage("mensagem.informe.servico.codigo", responsavel), ApplicationResourcesHelper.getMessage("mensagem.informe.servico.descricao", responsavel), ApplicationResourcesHelper.getMessage("mensagem.informe.servico.natureza", responsavel) };
        final String msgErro = JspHelper.verificaCamposForm(request, camposRequeridos, mensagensErro);
        if (msgErro.length() > 0) {
            session.setAttribute(CodedValues.MSG_ERRO, msgErro);

        } else {
            try {
                final ServicoTransferObject servico = new ServicoTransferObject();
                servico.setSvcIdentificador(JspHelper.verificaVarQryStr(request, "SVC_IDENTIFICADOR"));
                servico.setSvcDescricao(JspHelper.verificaVarQryStr(request, "SVC_DESCRICAO"));
                servico.setSvcNseCodigo(JspHelper.verificaVarQryStr(request, "NSE_CODIGO"));
                servico.setSvcAtivo(CodedValues.STS_ATIVO);

                // Código do serviço a ser utilizado para cópia dos parâmetros
                final String copiaSvc = (JspHelper.verificaVarQryStr(request, "copia_svc"));
                // Código de serviço a ser utilizado para cópia dos convenios, parâmetro de csa, bloqueio de verbas e bloqueio de serviços
                final String copiaCnv = (JspHelper.verificaVarQryStr(request, "copia_cnv"));
                final String copiaParamSvcCsa = (JspHelper.verificaVarQryStr(request, "copia_param_svc_csa"));
                final String copiaBloqueioCnv = (JspHelper.verificaVarQryStr(request, "copia_bloqueio_cnv"));
                final String copiaBloqueioSvc = (JspHelper.verificaVarQryStr(request, "copia_bloqueio_svc"));

                final long ini = Calendar.getInstance().getTimeInMillis();
                convenioController.createServico(servico, copiaSvc, copiaCnv, copiaParamSvcCsa, copiaBloqueioCnv, copiaBloqueioSvc, responsavel);
                final long fim = Calendar.getInstance().getTimeInMillis();

                LOG.info("TEMPO PARA CRIAR SERVICO (ms): " + (fim - ini));

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.servico.sucesso", responsavel));

            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        return listarServicos(request, response, session, model);
    }

}
