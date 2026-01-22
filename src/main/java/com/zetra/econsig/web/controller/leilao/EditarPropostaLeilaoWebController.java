package com.zetra.econsig.web.controller.leilao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.dto.web.EditarPropostaLeilaoModel;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.LeilaoSolicitacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.margem.ExibeMargem;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.RiscoRegistroServidorEnum;
import com.zetra.econsig.values.StatusPropostaEnum;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: EditarPropostaLeilaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Editar proposta de Leilão reverso.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: fagner.luiz $
 * $Revision: 28168 $
 * $Date: 2019-11-05 17:02:21 -0300 (ter, 05 nov 2019) $
 */
@Controller
@RequestMapping(value = { "/v3/editarPropostaLeilao" })
public class EditarPropostaLeilaoWebController extends AbstractWebController {

    private static final String DECREMENTO_INICIAL = "0,01";

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarPropostaLeilaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private LeilaoSolicitacaoController leilaoSolicitacaoController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        String adeCodigo = JspHelper.verificaVarQryStr(request, "ADE_CODIGO");
        String filtro = JspHelper.verificaVarQryStr(request, "filtro");

        boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

        // Verifica se está habilitado módulo de leilão de solicitação, e se a consignação informada possui solicitação de leilão pendente
        if (!ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Busca os dados do contrato

        String dddTelefoneContato = null;
        String telefoneContato = null;
        String emailContato = null;
        try {
            TransferObject ade = recuperarAde(responsavel, adeCodigo);
            TransferObject adeOrigem = recuperaAdeOrigem(responsavel, adeCodigo);

            List<TransferObject> dadosConsignacao = autorizacaoController.lstDadoAutDesconto(adeCodigo, null, VisibilidadeTipoDadoAdicionalEnum.WEB, responsavel);
            if (dadosConsignacao != null && !dadosConsignacao.isEmpty()) {
                for (TransferObject dado : dadosConsignacao) {
                    if (dado.getAttribute(Columns.TDA_CODIGO).equals(CodedValues.TDA_CONFIRMACAO_DADOS_DDD_TEL_LEILAO)) {
                        dddTelefoneContato = (String) dado.getAttribute(Columns.DAD_VALOR);
                    }
                    if (dado.getAttribute(Columns.TDA_CODIGO).equals(CodedValues.TDA_CONFIRMACAO_DADOS_TEL_LEILAO)) {
                        telefoneContato = (String) dado.getAttribute(Columns.DAD_VALOR);
                    }
                    if (dado.getAttribute(Columns.TDA_CODIGO).equals(CodedValues.TDA_CONFIRMACAO_DADOS_EMAIL_LEILAO)) {
                        emailContato = (String) dado.getAttribute(Columns.DAD_VALOR);
                    }
                }
            }

            String rseCodigo = ade.getAttribute(Columns.RSE_CODIGO).toString();
            // Consignatária deverá sempre ser a do usuário, e não a do contrato
            String csaCodigo = responsavel.getCsaCodigo();

            String svcCodigo = JspHelper.verificaVarQryStr(request, "svcCodigo");
            String prazo = JspHelper.verificaVarQryStr(request, "prazo");
            String valorParcela = JspHelper.verificaVarQryStr(request, "valorParcela");
            String valorLiberado = JspHelper.verificaVarQryStr(request, "valorLiberado");
            String taxaJuros = JspHelper.verificaVarQryStr(request, "adeTaxaJuros");
            String decremento = DECREMENTO_INICIAL; // --> Fixo: DESENV-6965
            String taxaMin = JspHelper.verificaVarQryStr(request, "taxaMin");
            String email = JspHelper.verificaVarQryStr(request, "email");
            String txtContatoCsa = JspHelper.verificaVarQryStr(request, "txtContatoCsa");

            // Busca os convênios da consignatária
            List<TransferObject> convenio = null;
            try {
                convenio = convenioController.lstCnvEntidade(responsavel.getCodigoEntidade(), responsavel.getTipoEntidade(), "leilao", responsavel);
            } catch (ConvenioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                convenio = new ArrayList<>();
            }
            if (convenio.size() == 0 && TextHelper.isNull(session.getAttribute(CodedValues.MSG_ERRO))) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.nenhumConvenioAtivo", responsavel));
            }

            List<TransferObject> propostas = null;
            // Pesquisa por proposta, caso já tenha cadastrado
            if (responsavel.isSer()) {
                propostas = leilaoSolicitacaoController.lstPropostaLeilaoSolicitacao(adeCodigo, csaCodigo, StatusPropostaEnum.AGUARDANDO_APROVACAO.getCodigo(), false, responsavel);
            } else if (responsavel.isCsaCor()) {
                propostas = leilaoSolicitacaoController.lstPropostaLeilaoSolicitacao(adeCodigo, null, null, false, responsavel);
            }

            boolean plsCsaAprovada = false;
            boolean plsCsaRejeitada = false;
            if (propostas != null && propostas.size() > 0) {
                for (TransferObject proposta : propostas) {
                    if (proposta.getAttribute(Columns.CSA_CODIGO).equals(csaCodigo)) {
                        // Obtém os valores da proposta para edição
                        prazo = proposta.getAttribute(Columns.PLS_PRAZO).toString();
                        valorParcela = NumberHelper.format(((BigDecimal) proposta.getAttribute(Columns.PLS_VALOR_PARCELA)).doubleValue(), NumberHelper.getLang());
                        valorLiberado = NumberHelper.format(((BigDecimal) proposta.getAttribute(Columns.PLS_VALOR_LIBERADO)).doubleValue(), NumberHelper.getLang());
                        svcCodigo = proposta.getAttribute(Columns.SVC_CODIGO).toString();
                        plsCsaAprovada = proposta.getAttribute(Columns.STP_CODIGO).equals(StatusPropostaEnum.APROVADA.getCodigo());
                        plsCsaRejeitada = proposta.getAttribute(Columns.STP_CODIGO).equals(StatusPropostaEnum.REJEITADA.getCodigo());
                        taxaJuros = NumberHelper.format(((BigDecimal) proposta.getAttribute(Columns.PLS_TAXA_JUROS)).doubleValue(), NumberHelper.getLang());
                        if (proposta.getAttribute(Columns.PLS_OFERTA_AUT_DECREMENTO) != null) {
                            decremento = NumberHelper.format(((BigDecimal) proposta.getAttribute(Columns.PLS_OFERTA_AUT_DECREMENTO)).doubleValue(), NumberHelper.getLang());
                        }
                        if (proposta.getAttribute(Columns.PLS_OFERTA_AUT_TAXA_MIN) != null) {
                            taxaMin = NumberHelper.format(((BigDecimal) proposta.getAttribute(Columns.PLS_OFERTA_AUT_TAXA_MIN)).doubleValue(), NumberHelper.getLang());
                        }
                        email = (String) proposta.getAttribute(Columns.PLS_OFERTA_AUT_EMAIL);
                        txtContatoCsa = (String) proposta.getAttribute(Columns.PLS_TXT_CONTATO_CSA);
                        break;
                    }
                }

                // se a proposta desta CSA/COR foi rejeitada, exibe apenas a proposta vencedora do leilão
                if (plsCsaRejeitada) {
                    List<TransferObject> lstPlsVencedora = new ArrayList<>();
                    for (TransferObject objPls : propostas) {
                        if (objPls.getAttribute(Columns.STP_CODIGO).equals(StatusPropostaEnum.APROVADA.getCodigo())) {
                            lstPlsVencedora.add(0, objPls);
                            break;
                        }
                    }

                    propostas = lstPlsVencedora;
                }
            }

            boolean soaExpirada = false;
            if (responsavel.isCsaCor()) {
                try {
                    soaExpirada = leilaoSolicitacaoController.temSolicitacaoLeilaoExpirada(adeCodigo, responsavel);
                } catch (LeilaoSolicitacaoControllerException lex) {
                    LOG.error(lex.getMessage(), lex);
                }
            }

            boolean podeEditarProposta = true;
            if (plsCsaRejeitada || soaExpirada) {
                podeEditarProposta = false;
            }

            String soaData, soaDataValidade;
            TransferObject criteriosPesquisa = new CustomTransferObject();
            criteriosPesquisa.setAttribute("ADE_CODIGO", adeCodigo);
            criteriosPesquisa.setAttribute("filtro", filtro);
            List<TransferObject> leiloes = leilaoSolicitacaoController.acompanharLeilaoSolicitacao(criteriosPesquisa, -1, -1, responsavel);
            if (leiloes == null || leiloes.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            TransferObject resultado = leiloes.get(0);
            soaData = DateHelper.reformat(resultado.getAttribute(Columns.SOA_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
            if (resultado.getAttribute(Columns.SOA_DATA_VALIDADE) != null) {
                Date dtValidade = (Date) resultado.getAttribute(Columns.SOA_DATA_VALIDADE);
                Date dataAtual = Calendar.getInstance().getTime();
                long diff = dtValidade.getTime() - dataAtual.getTime();
                if (diff > 0) {
                    long diffSeconds = diff / 1000 % 60;
                    long diffMinutes = diff / (60 * 1000) % 60;
                    long diffHours = diff / (60 * 60 * 1000);
                    soaDataValidade = StringUtils.leftPad(Long.toString(diffHours), 2, "0") + ":" + StringUtils.leftPad(Long.toString(diffMinutes), 2, "0") + ":" + StringUtils.leftPad(Long.toString(diffSeconds), 2, "0");
                } else {
                    soaDataValidade = "00:00:00";
                }
            } else {
                soaDataValidade = ApplicationResourcesHelper.getMessage("rotulo.indeterminado.abreviado", responsavel);
            }

            if (TextHelper.isNull(prazo)) {
                prazo = ((Integer) ade.getAttribute(Columns.ADE_PRAZO)).toString();
            }

            if (TextHelper.isNull(valorLiberado)) {
                valorLiberado = NumberHelper.format(((BigDecimal) ade.getAttribute(Columns.ADE_VLR_LIQUIDO)).doubleValue(), NumberHelper.getLang());
            }

            if (podeEditarProposta && TextHelper.isNull(email) && TextHelper.isNull(taxaJuros)) {
                // Se está cadastrando a proposta, e não tem e-mail, carrega o e-mail do usuário
                UsuarioTransferObject usuario = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);
                email = (usuario.getUsuEmail() != null ? usuario.getUsuEmail() : "");
            }

            if (responsavel.isCsaCor()) {
                String tpaPermiteOferecerProposta = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_PERMITE_OFERECER_PROPOSTA_LEILAO, responsavel);
                boolean bloqueioProposta = (TextHelper.isNull(tpaPermiteOferecerProposta) || tpaPermiteOferecerProposta.equals("S")) ? false : true;
                podeEditarProposta = podeEditarProposta && !bloqueioProposta;
                if (tpaPermiteOferecerProposta != null && tpaPermiteOferecerProposta.equalsIgnoreCase(CodedValues.TPA_NAO)) {
                    session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.proposta.leilao.csa.bloqueada.para.proposta", responsavel));
                }
            }

            RegistroServidorTO rseTO = servidorController.findRegistroServidor(rseCodigo, false, responsavel);
            String bcoDesc = null;
            if (!TextHelper.isNull(rseTO.getRseBancoSal())) {
                try {
                    TransferObject bcoTO = servidorController.recuperarDadosBanco(Short.parseShort(rseTO.getRseBancoSal()), responsavel);
                    bcoDesc = (String) bcoTO.getAttribute(Columns.BCO_DESCRICAO);
                } catch (NumberFormatException | ServidorControllerException ex) {
                    bcoDesc = null;
                }
            }

            // Parametro para exibição de análise de risco cadastrado pela CSA
            boolean temRiscoPelaCsa = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_RISCO_SERVIDOR_CSA, responsavel);

            String arrRisco = "";

            if (responsavel.isCsa() && temRiscoPelaCsa) {
                TransferObject risco = leilaoSolicitacaoController.obterAnaliseDeRiscoRegistroServidor(rseCodigo, responsavel);
                arrRisco = (String) (!TextHelper.isNull(risco) ? risco.getAttribute(Columns.ARR_RISCO) : "");
                arrRisco = RiscoRegistroServidorEnum.recuperaDescricaoRisco(arrRisco, responsavel);
            }

            // Caso serviço não tenha sido selecionado, busca serviço da consignação de leilão
            if (TextHelper.isNull(svcCodigo)) {
                svcCodigo = ade.getAttribute(Columns.SVC_CODIGO).toString();
            }

            ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            // Verifica se pode mostrar margem
            Short incMargem = paramSvcCse.getTpsIncideMargem();
            // DESENV-13349 : Não passar svcCodigo para a consulta da margem, pois caso a CSA/COR não tenha convênio
            // com o serviço, não ficará impedido de ver a margem. Ele deve ter pelo menos um convênio em serviço que
            // incide na mesma margem da solicitação original
            MargemDisponivel margemDisponivel = new MargemDisponivel(rseCodigo, null, null, incMargem, responsavel);
            String tipoVlrMargemDisponivel = ParamSvcTO.getDescricaoTpsTipoVlr(margemDisponivel.getTipoVlr());
            ExibeMargem exibeMargem = margemDisponivel.getExibeMargem();

            BigDecimal rseMargemRest = margemDisponivel.getMargemRestante();
            String margemConsignavel = rseMargemRest.toString();

            EditarPropostaLeilaoModel editarPropostaLeilaoModel = new EditarPropostaLeilaoModel();
            editarPropostaLeilaoModel.setAdeCodigo(adeCodigo);
            editarPropostaLeilaoModel.setFiltro(filtro);
            editarPropostaLeilaoModel.setTemCET(temCET);
            editarPropostaLeilaoModel.setAde(ade);
            editarPropostaLeilaoModel.setAdeOreigem(adeOrigem);
            editarPropostaLeilaoModel.setTemRiscoPelaCsa(temRiscoPelaCsa);
            editarPropostaLeilaoModel.setArrRisco(arrRisco);
            editarPropostaLeilaoModel.setTipoVlrMargemDisponivel(tipoVlrMargemDisponivel);
            editarPropostaLeilaoModel.setExibeMargem(exibeMargem);
            editarPropostaLeilaoModel.setRseMargemRest(rseMargemRest);
            editarPropostaLeilaoModel.setMargemConsignavel(margemConsignavel);
            editarPropostaLeilaoModel.setBcoDesc(bcoDesc);
            editarPropostaLeilaoModel.setPropostas(propostas);
            editarPropostaLeilaoModel.setSoaData(soaData);
            editarPropostaLeilaoModel.setSoaDataValidade(soaDataValidade);
            editarPropostaLeilaoModel.setPodeEditarProposta(podeEditarProposta);
            editarPropostaLeilaoModel.setConvenio(convenio);
            editarPropostaLeilaoModel.setSvcCodigo(svcCodigo);
            editarPropostaLeilaoModel.setTaxaJuros(taxaJuros);
            editarPropostaLeilaoModel.setValorLiberado(valorLiberado);
            editarPropostaLeilaoModel.setPrazo(prazo);
            editarPropostaLeilaoModel.setValorParcela(valorParcela);
            editarPropostaLeilaoModel.setTxtContatoCsa(txtContatoCsa);
            editarPropostaLeilaoModel.setPlsCsaAprovada(plsCsaAprovada);
            editarPropostaLeilaoModel.setTelefoneContato(telefoneContato);
            editarPropostaLeilaoModel.setEmailContato(emailContato);
            editarPropostaLeilaoModel.setDddTelefoneContato(dddTelefoneContato);
            editarPropostaLeilaoModel.setDecremento(decremento);
            editarPropostaLeilaoModel.setTaxaMin(taxaMin);
            editarPropostaLeilaoModel.setEmail(email);

            model.addAttribute("editarPropostaLeilaoModel", editarPropostaLeilaoModel);

            return viewRedirect("jsp/leilao/editarPropostaLeilao", request, session, model, responsavel);

        } catch (AutorizacaoControllerException | LeilaoSolicitacaoControllerException | UsuarioControllerException | ParametroControllerException | ServidorControllerException | ViewHelperException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (ParseException e) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    private TransferObject recuperarAde(AcessoSistema responsavel, String adeCodigo) throws AutorizacaoControllerException {
        TransferObject ade = null;
        List<String> adeCodigos = new ArrayList<>();
        adeCodigos.add(adeCodigo);
        // validaPermissao = false pois o servidor solicitou à terceiros a informação de propostas
        List<TransferObject> autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigos, false, responsavel);

        if (autdes != null && !autdes.isEmpty()) {
            ade = autdes.get(0);
            ade = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) ade, null, responsavel);
        }
        return ade;
    }

    private TransferObject recuperaAdeOrigem(AcessoSistema responsavel, String adeCodigo) throws AutorizacaoControllerException {
        TransferObject adeOrigem = null;

        List<TransferObject> autOrigem = pesquisarConsignacaoController.pesquisaAdeOrigem(adeCodigo, responsavel);

        if(autOrigem != null && !autOrigem.isEmpty()){
            adeOrigem = autOrigem.get(0);
        }

        return adeOrigem;
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request, true)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

        // valida campo taxa juros
        String taxaJuros = JspHelper.verificaVarQryStr(request, "adeTaxaJuros");
        if (TextHelper.isNull(taxaJuros)) {
            String msgErroTaxa = null;
            if (temCET) {
                msgErroTaxa = "mensagem.informe.proposta.leilao.solicitacao.cet";
            } else {
                msgErroTaxa = "mensagem.informe.proposta.leilao.solicitacao.taxa.juros";
            }
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(msgErroTaxa, responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (NumberHelper.parseDecimal(taxaJuros).compareTo(BigDecimal.ZERO) <= 0) {
            String msgErroTaxa = null;
            if (temCET) {
                msgErroTaxa = "mensagem.erro.cet.proposta.leilao.solicitacao.incorreto";
            } else {
                msgErroTaxa = "mensagem.erro.taxa.juros.proposta.leilao.solicitacao.incorreto";
            }
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(msgErroTaxa, responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            String taxaMin = JspHelper.verificaVarQryStr(request, "taxaMin");
            String decremento = DECREMENTO_INICIAL; // --> Fixo: DESENV-6965
            String adeCodigo = JspHelper.verificaVarQryStr(request, "ADE_CODIGO");
            String email = JspHelper.verificaVarQryStr(request, "email");
            String svcCodigo = JspHelper.verificaVarQryStr(request, "svcCodigo");
            String txtContatoCsa = JspHelper.verificaVarQryStr(request, "txtContatoCsa");

            BigDecimal taxaMinOfertaAut = (!TextHelper.isNull(taxaMin) ? NumberHelper.parseDecimal(taxaMin) : null);
            BigDecimal decrementoOfertaAut = (!TextHelper.isNull(decremento) ? NumberHelper.parseDecimal(decremento) : null);

            TransferObject ade = recuperarAde(responsavel, adeCodigo);
            String rseCodigo = ade.getAttribute(Columns.RSE_CODIGO).toString();

            // Salva as propostas para o contrato
            leilaoSolicitacaoController.informarPropostaLeilaoSolicitacao(adeCodigo, svcCodigo, null, NumberHelper.parseDecimal(taxaJuros), taxaMinOfertaAut, decrementoOfertaAut, email, txtContatoCsa, true, rseCodigo, null, true, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.proposta.leilao.solicitacao.proposta.informada.sucesso", responsavel));

        } catch (LeilaoSolicitacaoControllerException | AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        // Redireciona para página que chamou
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL("../v3/acompanharLeilao?acao=iniciar", request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=calcularValorPrestacao" }, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> calcularValorPrestacao(HttpServletRequest request, Model model) throws SQLException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String adeCodigo = JspHelper.verificaVarQryStr(request, "adeCodigo");
        String taxaJuros = JspHelper.verificaVarQryStr(request, "taxaJuros");

        if (!TextHelper.isNull(adeCodigo) && !TextHelper.isNull(taxaJuros)) {
            try {
                BigDecimal vlrPrestacao = leilaoSolicitacaoController.calcularValorPrestacao(adeCodigo, taxaJuros, responsavel);

                JsonObjectBuilder result = Json.createObjectBuilder();
                result.add("valor", NumberHelper.format((vlrPrestacao).doubleValue(), NumberHelper.getLang()));
                return new ResponseEntity<>(result.build().toString(), HttpStatus.OK);
            } catch (LeilaoSolicitacaoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        return null;
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=aprovarProposta" })
    public String aprovarProposta(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String adeCodigo = request.getParameter("ADE_CODIGO") != null ? request.getParameter("ADE_CODIGO") : request.getParameter("ade");
        String plsCodigo = JspHelper.verificaVarQryStr(request, "pls");

        if (!SynchronizerToken.isTokenValid(request, true) || !responsavel.isSer() || TextHelper.isNull(adeCodigo) || TextHelper.isNull(plsCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            String adeCodigoNovo = leilaoSolicitacaoController.aprovarPropostaLeilaoSolicitacao(null, adeCodigo, plsCodigo, responsavel);
            if (!TextHelper.isNull(adeCodigoNovo)) {
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.aprovar.proposta.leilao.solicitacao.concluido.sucesso", responsavel));
                request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL("../v3/consultarConsignacao?acao=detalharConsignacao&ADE_CODIGO=\" + adeCodigoNovo + \"&back=1", request)));
                return "jsp/redirecionador/redirecionar";
            }
        } catch (LeilaoSolicitacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        request.setAttribute("url64", TextHelper.encode64("../v3/acompanharLeilao?acao=iniciar"));
        return "jsp/redirecionador/redirecionar";
    }
}
