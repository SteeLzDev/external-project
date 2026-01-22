package com.zetra.econsig.web.controller.consignacao;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.consignacao.ReservaMargemHelper;
import com.zetra.econsig.helper.criptografia.JCryptOld;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AlongarConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso AlongarConsignacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/alongarConsignacao" })
public class AlongarConsignacaoWebController extends AbstractIncluirConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AlongarConsignacaoWebController.class);

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
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private SimulacaoController simulacaoController;

    @Override
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        String retorno = super.iniciar(request, response, session, model);

        // Não omite campo de ADE_NUMERO
        model.addAttribute("omitirAdeNumero", Boolean.FALSE);
        model.addAttribute("lstConsignataria", null);
        model.addAttribute("lstServico", null);

        return retorno;
    }

    @RequestMapping(params = {"acao=detalhar"}, method = { RequestMethod.POST })
    public String detalhar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (request.getParameter("ADE_CODIGO") != null && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String adeCodigo = JspHelper.verificaVarQryStr(request, "ADE_CODIGO");
        if (adeCodigo.equals("")) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        boolean temAlongamento = ParamSist.paramEquals(CodedValues.TPC_TEM_ALONGAMENTO_CONTRATO, CodedValues.TPC_SIM, responsavel);
        if (!temAlongamento) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.sistemaNaoPermiteAlongamento", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        //Verifica se permite a escolha de periodicidade da folha diferente da que está configurada no sistema
        boolean permiteEscolherPeriodicidade = ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODICIDADE_FOLHA, CodedValues.TPC_SIM, responsavel);

        ParamSession paramSession = ParamSession.getParamSession(session);
        CustomTransferObject autdes = null;

        try {
            autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            session.setAttribute(CodedValues.MSG_ERRO, "");
        } catch (AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        }

        String cnvCodigo = autdes.getAttribute(Columns.CNV_CODIGO).toString();
        String csaCodigo = autdes.getAttribute(Columns.CSA_CODIGO).toString();
        String orgCodigo = autdes.getAttribute(Columns.ORG_CODIGO).toString();
        String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
        String rseCodigo = autdes.getAttribute(Columns.RSE_CODIGO).toString();

        String svcCodigoAlongamento = null;
        String cnvCodigoAlongamento = null;
        String svcDescricaoAlongamento = null;
        String svcIdentificadorAlongamento = null;
        String cnvCodVerbaAlongamento = null;
        CustomTransferObject convenio = null;
        ParamSvcTO paramSvcCse = null;

        // Recupera o codigo do serviço de alongamento através do relacionamento de serviços.
        // O serviço atual deve ser destino de um relacionamento de alongamento
        try {
            List<TransferObject> svcCodigosRel = parametroController.getRelacionamentoSvc(CodedValues.TNT_ALONGAMENTO, null, svcCodigo, responsavel);
            if (svcCodigosRel == null || svcCodigosRel.size() == 0) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.semRelAlongamento", responsavel));
                request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                return "jsp/redirecionador/redirecionar";
            } else {
                CustomTransferObject to = (CustomTransferObject) svcCodigosRel.get(0);
                svcCodigoAlongamento = to.getAttribute(Columns.RSV_SVC_CODIGO_ORIGEM).toString();

                // Busca o convênio da CSA com o serviço de alongamento
                List<TransferObject> convenios = convenioController.lstConvenios(null, csaCodigo, svcCodigoAlongamento, orgCodigo, true, responsavel);
                Iterator<TransferObject> ite = convenios.iterator();
                if (ite.hasNext()) {
                    // Pega os dados do convênio para o alongamento
                    CustomTransferObject cto = (CustomTransferObject) ite.next();
                    cnvCodigoAlongamento = (cto.getAttribute(Columns.CNV_CODIGO) != null) ? cto.getAttribute(Columns.CNV_CODIGO).toString() : "";
                    svcDescricaoAlongamento = cto.getAttribute(Columns.SVC_DESCRICAO).toString();
                    svcIdentificadorAlongamento = cto.getAttribute(Columns.SVC_IDENTIFICADOR).toString();
                    cnvCodVerbaAlongamento = cto.getAttribute(Columns.CNV_COD_VERBA).toString();
                } else {
                    // A consignatária não tem convênio com o serviço de alongamento
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.semRelAlongamento", responsavel));
                    request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                    return "jsp/redirecionador/redirecionar";
                }

                // Busca os parâmetros do convênio
                convenio = convenioController.getParamCnv(cnvCodigoAlongamento, responsavel);

                // Busca os parâmetros de serviço
                paramSvcCse = parametroController.getParamSvcCseTO(svcCodigoAlongamento, responsavel);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        }

        try {
            // Parâmetros de serviço necessários
            Short adeIncMargem = paramSvcCse.getTpsIncideMargem(); // Incide na margem 1, 2 ou 3
            String tipoVlr = paramSvcCse.getTpsTipoVlr(); // Tipo do valor: F (Fixo) / P (Percentual) / T (Total da Margem)
            String labelTipoVlr = ParamSvcTO.getDescricaoTpsTipoVlr(tipoVlr);
            String adeVlrPadrao = paramSvcCse.getTpsAdeVlr() != null ? paramSvcCse.getTpsAdeVlr() : ""; // Valor da prestação fixo para o serviço
            boolean alteraAdeVlr = paramSvcCse.isTpsAlteraAdeVlr(); // Habilita ou nao campo de valor da reserva, campo ja vem preenchido
            int maxPrazo = (paramSvcCse.getTpsMaxPrazo() != null && !paramSvcCse.getTpsMaxPrazo().equals("")) ? Integer.parseInt(paramSvcCse.getTpsMaxPrazo()) : -1;
            String vlrLimite = (paramSvcCse.getTpsVlrLimiteAdeSemMargem() != null && !paramSvcCse.getTpsVlrLimiteAdeSemMargem().equals("")) ? NumberHelper.reformat(paramSvcCse.getTpsVlrLimiteAdeSemMargem(), "en", NumberHelper.getLang()) : "0";
            int carenciaMinCse = paramSvcCse.getTpsCarenciaMinima() != null && !paramSvcCse.getTpsCarenciaMinima().equals("") ? Integer.parseInt(paramSvcCse.getTpsCarenciaMinima()) : 0;
            int carenciaMaxCse = paramSvcCse.getTpsCarenciaMaxima() != null && !paramSvcCse.getTpsCarenciaMaxima().equals("") ? Integer.parseInt(paramSvcCse.getTpsCarenciaMaxima()) : 99;
            boolean permiteCadVlrTac = paramSvcCse.isTpsCadValorTac();
            boolean permiteCadVlrIof = paramSvcCse.isTpsCadValorIof();
            boolean permiteCadVlrLiqLib = paramSvcCse.isTpsCadValorLiquidoLiberado();
            boolean permiteCadVlrMensVinc = paramSvcCse.isTpsCadValorMensalidadeVinc();
            String perMaxParc = paramSvcCse.getTpsVlrPercMaximoParcelaAlongamento() != null && !paramSvcCse.getTpsVlrPercMaximoParcelaAlongamento().equals("") ? NumberHelper.reformat(paramSvcCse.getTpsVlrPercMaximoParcelaAlongamento(), "en", NumberHelper.getLang()) : "1";
            boolean serSenhaObrigatoria = parametroController.senhaServidorObrigatoriaReserva(rseCodigo, svcCodigoAlongamento, csaCodigo, responsavel);
            boolean validarDataNasc = paramSvcCse.isTpsValidarDataNascimentoNaReserva();
            boolean serInfBancariaObrigatoria = paramSvcCse.isTpsInfBancariaObrigatoria();
            boolean validarInfBancaria = paramSvcCse.isTpsValidarInfBancariaNaReserva();
            String mascaraAdeIdentificador = paramSvcCse.getTpsMascaraIdentificadorAde();
            boolean anexoInclusaoContratosObrigatorio = parametroController.isObrigatorioAnexoInclusao(svcCodigoAlongamento, responsavel);
            boolean anexoObrigatorio = anexoInclusaoContratosObrigatorio;

            // Parâmetros de convênio necessários
            boolean permitePrazoMaiorContSer = (convenio.getAttribute("PERMITE_PRAZO_MAIOR_RSE_PRAZO") != null && convenio.getAttribute("PERMITE_PRAZO_MAIOR_RSE_PRAZO").equals("S"));
            int carenciaMinima = (convenio.getAttribute("CARENCIA_MINIMA") != null && !convenio.getAttribute("CARENCIA_MINIMA").equals("")) ? Integer.parseInt(convenio.getAttribute("CARENCIA_MINIMA").toString()) : 0;
            int carenciaMaxima = (convenio.getAttribute("CARENCIA_MAXIMA") != null && !convenio.getAttribute("CARENCIA_MAXIMA").equals("")) ? Integer.parseInt(convenio.getAttribute("CARENCIA_MAXIMA").toString()) : 99;
            String vlrIndice = (convenio.getAttribute("VLR_INDICE") != null && !convenio.getAttribute("VLR_INDICE").equals("")) ? convenio.getAttribute("VLR_INDICE").toString() : "";

            // Define os valores de carência mínimo e máximo
            int[] carenciaPermitida = ReservaMargemHelper.getCarenciaPermitida(carenciaMinima, carenciaMaxima, carenciaMinCse, carenciaMaxCse);
            int carenciaMinPermitida = carenciaPermitida[0];
            int carenciaMaxPermitida = carenciaPermitida[1];

            // Parâmetro de identificador ADE obrigatório
            boolean identificadorAdeObrigatorio = (!TextHelper.isNull(convenio.getAttribute("IDENTIFICADOR_ADE_OBRIGATORIO")) ? convenio.getAttribute("IDENTIFICADOR_ADE_OBRIGATORIO").equals("S") : paramSvcCse.isTpsIdentificadorAdeObrigatorio());

            // Se existe simulação de consignação, então adiciona combo para seleção
            // de prazos para a autorização, se houverem prazos cadastrados
            Set<Integer> prazosPossiveisMensal = new TreeSet<>();
            Set<Integer> prazosPossiveisPeriodicidadeFolha = new TreeSet<>();

            boolean temSimulacaoConsignacao = ParamSist.getBoolParamSist(CodedValues.TPC_SIMULACAO_CONSIGNACAO, responsavel);
            if (temSimulacaoConsignacao || paramSvcCse.isTpsValidarTaxaJuros()) {
                // Seleciona prazos ativos.
                try {
                    int dia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

                    List<TransferObject> prazos = simulacaoController.getPrazoCoeficiente(svcCodigoAlongamento, csaCodigo, orgCodigo, dia, responsavel);
                    if (prazos != null && !prazos.isEmpty()) {
                        prazos.forEach(p -> prazosPossiveisMensal.add(Integer.valueOf(p.getAttribute(Columns.PRZ_VLR).toString())));
                        if (!PeriodoHelper.folhaMensal(responsavel)) {
                            prazosPossiveisPeriodicidadeFolha = PeriodoHelper.converterListaPrazoMensalEmPeriodicidade(prazos, responsavel);
                        }
                    }
                } catch (Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }
            }

            // Verifica se sistema permite cadastro de índice para o serviço
            boolean permiteCadIndice = ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_CAD_INDICE, responsavel) != null && ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_CAD_INDICE, responsavel).toString().equals(CodedValues.TPC_SIM) ? true : false;
            // Índice cadastrado automaticamente
            boolean indiceSomenteAutomatico = ParamSist.paramEquals(CodedValues.TPC_INDICE_SOMENTE_AUTOMATICO, CodedValues.TPC_SIM, responsavel);
            // Verifica se sistema o cadastro de índice é numérico ou alfanumérico
            boolean indiceNumerico = ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_NUMERICO, responsavel) != null && ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_NUMERICO, responsavel).toString().equals(CodedValues.TPC_SIM) ? true : false;

            //Busca status se mensagem de margem comprometida esta ativa
            boolean mensagemMargemComprometida = ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_MSG_MARGEM_COMPROMET, responsavel) != null && ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_MSG_MARGEM_COMPROMET, responsavel).toString().equals("S") ? true : false;

            // Verifica se pode mostrar margem
            MargemDisponivel margemDisponivel = null;
            try {
                margemDisponivel = new MargemDisponivel(rseCodigo, csaCodigo, svcCodigo, adeIncMargem, responsavel);
            } catch (ViewHelperException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                return "jsp/redirecionador/redirecionar";
            }

            boolean podeMostrarMargem = margemDisponivel.getExibeMargem().isExibeValor();
            BigDecimal margemRestOld = margemDisponivel.getMargemRestante();

            // Margem restante atualizada
            BigDecimal adeVlrOld = (BigDecimal) autdes.getAttribute(Columns.ADE_VLR);
            BigDecimal margemRestNew = (!adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) ? margemRestOld.add(adeVlrOld) : margemRestOld;

            String mensagem = "";
            if (autdes.getAttribute(Columns.PRD_ADE_CODIGO) != null) {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.consignacao.possui.prd.processamento.folha", responsavel) + "\n";
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.consignacao.possui.prd.processamento.folha", responsavel));
            }

            CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);

            String serDataNasc = servidor.getAttribute(Columns.SER_DATA_NASC) != null ? DateHelper.reformat(servidor.getAttribute(Columns.SER_DATA_NASC).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern()) : "";
            String numBanco = JCryptOld.crypt("IB", servidor.getAttribute(Columns.RSE_BANCO_SAL) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_BANCO_SAL).toString()) : "");
            String numAgencia = JCryptOld.crypt("IB", servidor.getAttribute(Columns.RSE_AGENCIA_SAL) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_AGENCIA_SAL).toString()) : "");
            String numConta = servidor.getAttribute(Columns.RSE_CONTA_SAL) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_CONTA_SAL).toString()) : "";

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

            //Conta salário alternativa
            String numBancoAlt = JCryptOld.crypt("IB", servidor.getAttribute(Columns.RSE_BANCO_SAL_2) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_BANCO_SAL_2).toString()) : "");
            String numAgenciaAlt = JCryptOld.crypt("IB", servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2).toString()) : "");
            String numContaAlt = servidor.getAttribute(Columns.RSE_CONTA_SAL_2) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_CONTA_SAL_2).toString()) : "";

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

            boolean rseTemInfBancaria = ((!TextHelper.isNull(servidor.getAttribute(Columns.RSE_BANCO_SAL)) &&
                    !TextHelper.isNull(servidor.getAttribute(Columns.RSE_AGENCIA_SAL)) &&
                    !TextHelper.isNull(servidor.getAttribute(Columns.RSE_CONTA_SAL))) ||
                    (!TextHelper.isNull(servidor.getAttribute(Columns.RSE_BANCO_SAL_2)) &&
                            !TextHelper.isNull(servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2)) &&
                            !TextHelper.isNull(servidor.getAttribute(Columns.RSE_CONTA_SAL_2))));

            List<TransferObject> tdaList = autorizacaoController.lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.WEB, svcCodigo, csaCodigo, responsavel);

            for(TransferObject tda : tdaList){
                String valorOriginal = autorizacaoController.getValorDadoAutDesconto(adeCodigo, (String) tda.getAttribute(Columns.TDA_CODIGO), responsavel);
                tda.setAttribute("VALOR_ORIGINAL", valorOriginal);
            }

            model.addAttribute("serInfBancariaObrigatoria", serInfBancariaObrigatoria);
            model.addAttribute("validarInfBancaria", validarInfBancaria);
            model.addAttribute("validarDataNasc", validarDataNasc);
            model.addAttribute("mensagemMargemComprometida", mensagemMargemComprometida);
            model.addAttribute("permiteCadVlrTac", permiteCadVlrTac);
            model.addAttribute("permiteCadVlrIof", permiteCadVlrIof);
            model.addAttribute("permiteCadVlrLiqLib", permiteCadVlrLiqLib);
            model.addAttribute("permiteCadVlrMensVinc", permiteCadVlrMensVinc);
            model.addAttribute("mensagem", mensagem);
            model.addAttribute("carenciaMinPermitida", carenciaMinPermitida);
            model.addAttribute("carenciaMaxPermitida", carenciaMaxPermitida);
            model.addAttribute("serSenhaObrigatoria", serSenhaObrigatoria);
            model.addAttribute("anexoInclusaoContratosObrigatorio", anexoInclusaoContratosObrigatorio);
            model.addAttribute("serDataNasc", serDataNasc);
            model.addAttribute("rseTemInfBancaria", rseTemInfBancaria);
            model.addAttribute("numBanco", numBanco);
            model.addAttribute("numAgencia", numAgencia);
            model.addAttribute("numConta1", numConta1);
            model.addAttribute("numConta2", numConta2);
            model.addAttribute("numBancoAlt", numBancoAlt);
            model.addAttribute("numAgenciaAlt", numAgenciaAlt);
            model.addAttribute("numContaAlt2", numContaAlt2);
            model.addAttribute("numContaAlt1", numContaAlt1);
            model.addAttribute("prazosPossiveisMensal", prazosPossiveisMensal);
            model.addAttribute("autdes", autdes);
            model.addAttribute("cnvCodVerbaAlongamento", cnvCodVerbaAlongamento);
            model.addAttribute("svcIdentificadorAlongamento", svcIdentificadorAlongamento);
            model.addAttribute("svcDescricaoAlongamento", svcDescricaoAlongamento);
            model.addAttribute("podeMostrarMargem", podeMostrarMargem);
            model.addAttribute("margemDisponivel", margemDisponivel);
            model.addAttribute("margemRestNew", margemRestNew);
            model.addAttribute("labelTipoVlr", labelTipoVlr);
            model.addAttribute("adeVlrPadrao", adeVlrPadrao);
            model.addAttribute("alteraAdeVlr", alteraAdeVlr);
            model.addAttribute("permiteEscolherPeriodicidade", permiteEscolherPeriodicidade);
            model.addAttribute("maxPrazo", maxPrazo);
            model.addAttribute("permiteCadIndice", permiteCadIndice);
            model.addAttribute("indiceSomenteAutomatico", indiceSomenteAutomatico);
            model.addAttribute("vlrIndice", vlrIndice);
            model.addAttribute("indiceNumerico", indiceNumerico);
            model.addAttribute("identificadorAdeObrigatorio", identificadorAdeObrigatorio);
            model.addAttribute("mascaraAdeIdentificador", mascaraAdeIdentificador);
            model.addAttribute("cnvCodigoAlongamento", cnvCodigoAlongamento);
            model.addAttribute("tdaList", tdaList);
            model.addAttribute("adeCodigo", adeCodigo);
            model.addAttribute("anexoObrigatorio", anexoObrigatorio);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("cnvCodigo", cnvCodigo);
            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("orgCodigo", orgCodigo);
            model.addAttribute("svcCodigoAlongamento", svcCodigoAlongamento);
            model.addAttribute("vlrLimite", vlrLimite);
            model.addAttribute("perMaxParc", perMaxParc);
            model.addAttribute("permitePrazoMaiorContSer", permitePrazoMaiorContSer);
            model.addAttribute("prazosPossiveisPeriodicidadeFolha", prazosPossiveisPeriodicidadeFolha);

            return viewRedirect("jsp/alongarConsignacao/alongarConsignacao", request, session, model, responsavel);

        } catch (ParseException | ParametroControllerException | ServidorControllerException | AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.alongar.consignacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/alongarConsignacao");
        model.addAttribute("imageHeader", "i-operacional");
    }

    @Override
    protected List<String> definirSvcCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> svcCodigos = new ArrayList<>();

        if (ParamSist.paramEquals(CodedValues.TPC_TEM_ALONGAMENTO_CONTRATO, CodedValues.TPC_SIM, responsavel)) {
            try {
                // Lista todos os relacionamentos de alongamento
                List<TransferObject> relacionamento = parametroController.getRelacionamentoSvc(CodedValues.TNT_ALONGAMENTO, null, null, responsavel);
                if (relacionamento != null && relacionamento.size() > 0) {
                    // Se encontrou algum relacionamento de alongamento, pega os códigos dos
                    // serviços que podem ser alongados, e adiciona a lista de busca
                    Iterator<TransferObject> itRel = relacionamento.iterator();
                    TransferObject nextRel = null;
                    while (itRel.hasNext()) {
                        nextRel = itRel.next();
                        svcCodigos.add((String) nextRel.getAttribute(Columns.RSV_SVC_CODIGO_DESTINO));
                    }
                } else {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.semRelAlongamento", responsavel));
                }
            } catch (ParametroControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.sistemaNaoPermiteAlongamento", responsavel));
        }

        return svcCodigos;
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigos.add(CodedValues.SAD_ESTOQUE);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        String link = "../v3/alongarConsignacao?acao=detalhar";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.alongar.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.alongar", responsavel);
        String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.alongar.consignacao.clique.aqui", responsavel);
        String msgConfirmacao = "";
        String msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("ALONGAR_CONTRATO", CodedValues.FUN_ALONGAR_CONTRATO, descricao, descricaoCompleta, "alongar_contrato.gif", "btnAlongarConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null,null));

        // Adiciona o editar consignação
        link = "../v3/alongarConsignacao?acao=detalharConsignacao";
        descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.abreviado", responsavel);
        descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar", responsavel);
        msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
        msgConfirmacao = "";
        msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta,"editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null,null));

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "alongar");
        return criterio;
    }

    @Override
    protected String executarFuncaoAposDuplicidade(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, Model model) {
        String rseCodigo = request.getParameter("RSE_CODIGO");
        return incluirReserva(rseCodigo, request, response, session, model);
    }

    @Override
    protected String getFunCodigo() {
        return CodedValues.FUN_ALONGAR_CONTRATO;
    }

    @Override
    protected String validarServicoOperacao(String svcCodigo, String rseCodigo, Map<String, String> parametrosPlano, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(svcCodigo)) {
            svcCodigo = request.getParameter("SVC_CODIGO");
        }
        if (TextHelper.isNull(svcCodigo)) {
            throw new ViewHelperException("mensagem.erro.servico.nao.informado", responsavel);
        }
        return svcCodigo;
    }

    @Override
    protected String continuarOperacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "ADE_NUMERO", required = true, defaultValue = "") String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return pesquisarConsignacao(rseCodigo, adeNumero, request, response, session, model);
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        return "pesquisarConsignacao";
    }

}
