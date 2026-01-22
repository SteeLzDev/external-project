package com.zetra.econsig.service.lote;

import static com.zetra.econsig.helper.lote.LoteHelper.ALTERACAO;
import static com.zetra.econsig.helper.lote.LoteHelper.CONFIRMACAO;
import static com.zetra.econsig.helper.lote.LoteHelper.EXCLUSAO;
import static com.zetra.econsig.helper.lote.LoteHelper.INCLUSAO;
import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_CONSIGNATARIA_NAO_INFORMADA;
import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_ERRO_INTERNO_SISTEMA;
import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_ERRO_NAO_POSSIVEL_LOCALIZAR_MENSALIDADE_BENEFICIOS;
import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_ERRO_RELACIONAMENTO_TIPO_LANCAMENTO_BENEFICIOS_NAO_ENCONTRADO;
import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_ERRO_TIPO_LANCAMENTO_BENEFICIOS_NAO_ENCONTRADO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.google.gson.Gson;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.AutorizacaoHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.xml.XmlHelper;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.LeitorArquivoFebraban;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTextoZip;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.parser.config.DocumentoTipo;
import com.zetra.econsig.persistence.BatchManager;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.BlocoProcessamentoLote;
import com.zetra.econsig.persistence.entity.BlocoProcessamentoLoteHome;
import com.zetra.econsig.persistence.entity.ControleProcessamentoLote;
import com.zetra.econsig.persistence.entity.ControleProcessamentoLoteHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.persistence.entity.ServicoHome;
import com.zetra.econsig.persistence.entity.TipoLancamento;
import com.zetra.econsig.persistence.entity.TipoLancamentoHome;
import com.zetra.econsig.persistence.query.beneficios.contrato.ListarContratosMensalidadeBeneficioQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoRenegociavelNativeQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemConsignacaoPorCnvSerQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConveniosQuery;
import com.zetra.econsig.persistence.query.lote.ListaAdeAbertaParaRenegociacaoQuery;
import com.zetra.econsig.persistence.query.lote.ListaAdeLiquidadaParaRenegociacaoQuery;
import com.zetra.econsig.persistence.query.lote.ListarBlocosProcessamentoLoteQuery;
import com.zetra.econsig.persistence.query.lote.ListarLotesEmProcessamentoQuery;
import com.zetra.econsig.persistence.query.prazo.ListaPrazoCoeficienteQuery;
import com.zetra.econsig.persistence.query.servico.ListaRelacionamentosServicoQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicoAdeAbertaParaRenegociacaoQuery;
import com.zetra.econsig.persistence.query.servico.ObtemServicoByCodVerbaSvcIdentificadorQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorPorCnvQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorQuery;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusBlocoProcessamentoEnum;
import com.zetra.econsig.web.filter.XSSPreventionFilter;

/**
 * <p>Title: LoteControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class LoteControllerBean implements LoteController {

    private static final String OPERACAO = "OPERACAO";

    private static final String RSE_BANCO_SAL = "RSE_BANCO_SAL";

    private static final String RSE_AGENCIA_SAL = "RSE_AGENCIA_SAL";

    private static final String RSE_CONTA_SAL = "RSE_CONTA_SAL";

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LoteControllerBean.class);

    @Autowired
    private AutorizacaoController autorizacaoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    /**
     * Metodo para verificar as informações traduzidas do arquivo de lote,
     * formatar e validar valores. Apenas verifica a formatação e informações minimas necessarias
     * @param entrada - Map da tradução de um arquivo
     * @throws AutorizacaoControllerException
     */
    @Override
    @SuppressWarnings("java:S1192")
    public Map<String, Object> validaEntrada(Map<String, Object> entrada) throws AutorizacaoControllerException {
        final Map<String, Object> retorno = new HashMap<>(entrada);
        /**
         * Valores que se estiverem no Map serão convertidos para data
         */
        final String[][] vlrData = { { "PERIODO_FOLHA", "ADE_ANO_MES_INI", "ADE_ANO_MES_FIM", "ADE_ANO_MES_INI_REF", "ADE_ANO_MES_FIM_REF" },
                {ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.periodo.atual", (AcessoSistema) null),
            ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.data.inicial", (AcessoSistema) null),
            ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.data.final", (AcessoSistema) null),
            ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.data.inicial.referencia", (AcessoSistema) null),
            ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.data.final.referencia", (AcessoSistema) null)}
        };

        /**
         * Valores que se estiverem no Map serão convertidos para BigDecimal
         */
        final String[][] vlrBigDecimal = { { "ADE_VLR_VERIFICAR", "ADE_VLR", "ADE_VLR_TAC", "ADE_VLR_IOF", "ADE_VLR_LIQUIDO", "ADE_VLR_MENS_VINC", "ADE_TAXA_JUROS" },
                {ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.valor.busca", (AcessoSistema) null),
            ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.valor.encontrado", (AcessoSistema) null),
            ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.valor.tac", (AcessoSistema) null),
            ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.valor.iof", (AcessoSistema) null),
            ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.valor.liquido", (AcessoSistema) null),
            ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.valor.mensalidade.vinculada", (AcessoSistema) null),
            ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.valor.taxa.juros", (AcessoSistema) null)}
        };

        /**
         * Valores que se estiverem no Map serão convertidos para Integer
         */
        final String[][] vlrInteger = { { "ADE_CARENCIA", "ADE_PRAZO", "ADE_INC_MARGEM", "ADE_INT_FOLHA" },
                {ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.carencia", (AcessoSistema) null),
            ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.prazo", (AcessoSistema) null),
            ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.incide.margem", (AcessoSistema) null),
            ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.integra.folha", (AcessoSistema) null)}
        };

        /**
         * Valores que devem existir no Map
         */
        final String[][] vlrStrObrigatorio = { { OPERACAO }, { ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.integra.operacao", (AcessoSistema) null) } };

        for (int i = 0; i < vlrStrObrigatorio[0].length; i++) {
            if ((retorno.get(vlrStrObrigatorio[0][i]) == null) || "".equals(retorno.get(vlrStrObrigatorio[0][i]).toString())) {
                LOG.debug("Erro de Parser -> " + vlrStrObrigatorio[1][i] + ": " + retorno.get(vlrStrObrigatorio[0][i]));
                throw new AutorizacaoControllerException("mensagem.erro.valor.linha.arg0.nao.informado", (AcessoSistema) null, vlrStrObrigatorio[1][i]);
            }
            if (OPERACAO.equals(vlrStrObrigatorio[0][i]) &&
                    !INCLUSAO.equals(retorno.get(vlrStrObrigatorio[0][i]).toString()) &&
                    !ALTERACAO.equals(retorno.get(vlrStrObrigatorio[0][i]).toString()) &&
                    !EXCLUSAO.equals(retorno.get(vlrStrObrigatorio[0][i]).toString()) &&
                    !CONFIRMACAO.equals(retorno.get(vlrStrObrigatorio[0][i]).toString())) {
                LOG.debug("Erro de Parser -> OPERACAO: " + retorno.get(OPERACAO));
                throw new AutorizacaoControllerException("mensagem.operacaoInvalida", (AcessoSistema) null);
            }
        }

        for (int i = 0; i < vlrData[0].length; i++) {
            if (retorno.get(vlrData[0][i]) != null) {
                if (!"".equals(retorno.get(vlrData[0][i]).toString())) {
                    try {
                        retorno.put(vlrData[0][i], DateHelper.toPeriodDate(DateHelper.parse(retorno.get(vlrData[0][i]).toString(), "yyyy-MM-dd")));
                    } catch (final ParseException e) {
                        LOG.debug("Erro de Parser -> " + vlrData[0][i] + ": " + e.getMessage());
                        throw new AutorizacaoControllerException("mensagem.erro.valor.informado.para.campo.invalido", (AcessoSistema) null, vlrData[1][i]);
                    }
                } else {
                    retorno.remove(vlrData[0][i]);
                }
            }
        }

        for (int i = 0; i < vlrInteger[0].length; i++) {
            if (retorno.get(vlrInteger[0][i]) != null) {
                if (!"".equals(retorno.get(vlrInteger[0][i]).toString())) {
                    try {
                        final int vlr = Integer.parseInt(retorno.get(vlrInteger[0][i]).toString());
                        if (Integer.compare(vlr, Integer.valueOf("0")) < 0) {
                            LOG.debug("Erro de Parser -> " + vlrInteger[0][i] + ": VALOR INFORMADO NEGATIVO");
                            throw new AutorizacaoControllerException("mensagem.erro.valor.informado.para.campo.nao.pode.ser.negativo", (AcessoSistema) null, vlrInteger[1][i]);
                        }
                        retorno.put(vlrInteger[0][i], Integer.valueOf(retorno.get(vlrInteger[0][i]).toString()));
                    } catch (final NumberFormatException e) {
                        LOG.debug("Erro de Parser -> " + vlrInteger[0][i] + ": " + e.getMessage());
                        throw new AutorizacaoControllerException("mensagem.erro.valor.informado.para.campo.invalido", (AcessoSistema) null, vlrInteger[1][i]);
                    }
                } else {
                    retorno.remove(vlrInteger[0][i]);
                }
            }
        }

        for (int i = 0; i < vlrBigDecimal[0].length; i++) {
            if (retorno.get(vlrBigDecimal[0][i]) != null) {
                if (!"".equals(retorno.get(vlrBigDecimal[0][i]).toString())) {
                    try {
                        final BigDecimal vlr = new BigDecimal(retorno.get(vlrBigDecimal[0][i]).toString());
                        // Verifica se o valor informado é positivo.
                        if (vlr.compareTo(new BigDecimal("0")) < 0) {
                            LOG.debug("Erro de Parser -> " + vlrBigDecimal[0][i] + ": VALOR INFORMADO NEGATIVO");
                            throw new AutorizacaoControllerException("mensagem.erro.valor.informado.para.campo.nao.pode.ser.negativo", (AcessoSistema) null, vlrBigDecimal[1][i]);
                        }
                        retorno.put(vlrBigDecimal[0][i], vlr);
                    } catch (final Exception ex) {
                        LOG.debug("Erro de Parser -> " + vlrBigDecimal[0][i] + ": " + ex.getMessage());
                        throw new AutorizacaoControllerException("mensagem.erro.valor.informado.para.campo.invalido", (AcessoSistema) null, vlrBigDecimal[1][i]);
                    }
                } else {
                    retorno.remove(vlrBigDecimal[0][i]);
                }
            }

            // ade_vlr é obrigatório
            if ("ADE_VLR".equals(vlrBigDecimal[0][i]) && (INCLUSAO.equals(retorno.get(OPERACAO).toString()) || ALTERACAO.equals(retorno.get(OPERACAO).toString())) && ((retorno.get("ADE_VLR") == null) || "".equals(retorno.get("ADE_VLR").toString()))) {
                LOG.debug("Erro de Parser -> ADE_VLR: " + retorno.get("ADE_VLR"));
                throw new AutorizacaoControllerException("mensagem.erro.valor.contrato.nao.informado", (AcessoSistema) null);
            }
        }

        return retorno;
    }

    /**
     * Metodo utilizado para busca de servidor na rotina de lote, verifica se é necessario cpf e matricula,
     * aqui ainda não é verificado as informações bancarias.
     * @param operacao - I, A ou E
     * @param tipo - tipo de usuario
     * @param tipoCodigo - codigo da entidade
     * @param est - estabelecimento
     * @param org - orgao
     * @param matricula
     * @param cpf
     * @param serAtivo
     * @param responsavel
     * @return DTO contendo as informações do servidor
     * @throws AutorizacaoControllerException
     */
    @Override
    public List<TransferObject> buscaServidor(String operacao, String tipo, String tipoCodigo, String est, String org, String matricula, String cpf, boolean serAtivo, AcessoSistema responsavel) throws AutorizacaoControllerException {

        List<TransferObject> servidor = null;
        try {
            // Seta os critérios da query
            final ListaServidorQuery query = new ListaServidorQuery(responsavel);
            query.tipo = tipo;
            query.codigo = tipoCodigo;
            query.estIdentificador = est;
            query.orgIdentificador = org;
            query.rseMatricula = matricula;
            query.serCPF = cpf;
            query.pesquisaExata = true;

            // Lista os resultados
            servidor = query.executarDTO();

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.nenhumServidorEncontrado", responsavel, ex);
        }

        boolean serCpfIgual = true;
        if ((servidor != null) && !servidor.isEmpty()) {
            String primeiroCpf = null;
            for (final TransferObject ser : servidor) {
                if (primeiroCpf == null) {
                    primeiroCpf = ser.getAttribute(Columns.SER_CPF).toString();
                }
                serCpfIgual = serCpfIgual && primeiroCpf.equals(ser.getAttribute(Columns.SER_CPF).toString());
            }
        }

        /**
         * Se serAtivo e não é operação de exclusão com servidores de CPF diferente,
         * então verifica a situação dos servidores, para determinar se estão
         * excluídos ou bloqueados, ou se nenhum foi encontrado.
         */

        String msgServidor = "";
        boolean excluido = false;

        if (serAtivo && (!EXCLUSAO.equalsIgnoreCase(operacao) || serCpfIgual) && ((servidor != null) && !servidor.isEmpty())) {
                final List<TransferObject> servidorCandidato = new ArrayList<>();
                for (final TransferObject ser : servidor) {
                    final String srsCodigo = ser.getAttribute(Columns.SRS_CODIGO).toString();
                    if (CodedValues.SRS_INATIVOS.contains(srsCodigo)) {
                        msgServidor = "mensagem.servidorExcluido";
                        excluido = true;
                    } else if (INCLUSAO.equalsIgnoreCase(operacao) && CodedValues.SRS_BLOQUEADOS.contains(srsCodigo)) {
                        msgServidor = "mensagem.servidorBloqueado";
                    } else {
                        servidorCandidato.add(ser);
                    }
                }
                servidor = servidorCandidato;
        }

        if ((servidor != null) && !servidor.isEmpty()) {
            return servidor;
        } else if ((!"".equals(msgServidor) && excluido) || (!"".equals(msgServidor) && !excluido)) {
            throw new AutorizacaoControllerException(msgServidor, responsavel);
        } else {
            throw new AutorizacaoControllerException("mensagem.nenhumServidorEncontrado", responsavel);
        }
    }

    /**
     * Metodo para validar se o convenio existe e se pode ou não processar lote
     * @param verba
     * @param orgao
     * @param csaCodigo
     * @param cnvAtivo
     * @param nseCodigo
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    public void verificaConvenioProcessaLote(String verba, List<String> orgao, String csaCodigo, String svcIdentificador, boolean cnvAtivo, String nseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ObtemServicoByCodVerbaSvcIdentificadorQuery svcByCodVerbaIdnt = new ObtemServicoByCodVerbaSvcIdentificadorQuery();
            svcByCodVerbaIdnt.cnvCodVerba = verba;
            svcByCodVerbaIdnt.orgCodigos = orgao;
            svcByCodVerbaIdnt.csaCodigo = csaCodigo;
            svcByCodVerbaIdnt.svcIdentificador = svcIdentificador;
            svcByCodVerbaIdnt.nseCodigo = nseCodigo;
            svcByCodVerbaIdnt.ativo = cnvAtivo;
            final List<TransferObject> convenios = svcByCodVerbaIdnt.executarDTO();

            if ((convenios == null) || convenios.isEmpty()) {
                LOG.debug("Erro: nenhum convênio encontrado.");
                throw new AutorizacaoControllerException("mensagem.convenioNaoEncontrado", responsavel);
            }

            boolean permiteProcLote = false;
            for (final TransferObject cnvCto : convenios) {
                final String svcCodigo = cnvCto.getAttribute(Columns.SVC_CODIGO).toString();
                final CustomTransferObject paramSvc = parametroController.getParamSvcCse(svcCodigo, CodedValues.TPS_PERMITE_IMPORTACAO_LOTE, responsavel);

                if ((paramSvc != null) && (paramSvc.getAttribute(Columns.PSE_VLR) != null) && "1".equals(paramSvc.getAttribute(Columns.PSE_VLR).toString())) {
                    permiteProcLote = true;
                    break;
                }
            }

            if (!permiteProcLote) {
                LOG.debug("Erro: convênio não pode ser processado via lote.");
                throw new AutorizacaoControllerException("mensagem.tipoServicoInvalido", responsavel);
            }
        } catch (HQueryException | ParametroControllerException ex) {
            throw new AutorizacaoControllerException(ex);
        }
    }

    @Override
    public void validaInfBancariaObrigatoria(Map<String, Object> dadosServidorConvenio, Map<String, Object> paramCnv, Map<String, Object> entrada) throws AutorizacaoControllerException {
        //DESENV-17581: parâmetro de serviço por consignatária exigindo informações bancárias. tem precedência sobre o parâmetro de serviço por consignante.
        final String paramInfBancSvcCsa = (String) paramCnv.get(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA_CSA);

        if ((!TextHelper.isNull(paramInfBancSvcCsa) && CodedValues.PSC_BOOLEANO_SIM.equals(paramInfBancSvcCsa)) ||
                (TextHelper.isNull(paramInfBancSvcCsa) && paramCnv.containsKey(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA) && (paramCnv.get(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA) != null) && "1".equals(paramCnv.get(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA).toString()))) {
            verificaInfoBancariasEntrada(dadosServidorConvenio, entrada);
        }
    }

    private void verificaInfoBancariasEntrada(Map<String, Object> dadosServidorConvenio, Map<String, Object> entrada) throws AutorizacaoControllerException {
        if ((entrada.get(RSE_CONTA_SAL) == null) || (entrada.get(RSE_AGENCIA_SAL) == null) || (entrada.get(RSE_BANCO_SAL) == null)) {
            throw new AutorizacaoControllerException("mensagem.informacaoBancariaObrigatoria", (AcessoSistema) null);
        }
        final String banco = JspHelper.removePadrao(dadosServidorConvenio.get(Columns.RSE_BANCO_SAL) != null ? dadosServidorConvenio.get(Columns.RSE_BANCO_SAL).toString() : "", "0", JspHelper.ESQ);
        final String agencia = JspHelper.removePadrao(dadosServidorConvenio.get(Columns.RSE_AGENCIA_SAL) != null ? dadosServidorConvenio.get(Columns.RSE_AGENCIA_SAL).toString() : "", "0", JspHelper.ESQ);
        final String conta = JspHelper.removePadrao(dadosServidorConvenio.get(Columns.RSE_CONTA_SAL) != null ? dadosServidorConvenio.get(Columns.RSE_CONTA_SAL).toString() : "", "0", JspHelper.ESQ);

        final String rseBancoSal = JspHelper.removePadrao(entrada.get(RSE_BANCO_SAL) != null ? entrada.get(RSE_BANCO_SAL).toString() : "", "0", JspHelper.ESQ);
        final String rseAgenciaSal = JspHelper.removePadrao(entrada.get(RSE_AGENCIA_SAL) != null ? entrada.get(RSE_AGENCIA_SAL).toString() : "", "0", JspHelper.ESQ);
        final String rseContaSal = JspHelper.removePadrao(entrada.get(RSE_CONTA_SAL) != null ? entrada.get(RSE_CONTA_SAL).toString() : "", "0", JspHelper.ESQ);

        if (!conta.equals(rseContaSal) || !agencia.equals(rseAgenciaSal) || !banco.equals(rseBancoSal)) {
            throw new AutorizacaoControllerException("mensagem.informacaoBancariaIncorreta", (AcessoSistema) null);
        }
    }

    @Override
    public void validaInfObrigatoriaCnv(Map<String, Object> paramCnv, Map<String, Object> entrada) throws AutorizacaoControllerException {
        // Verifica se o sistema está configurado para trabalhar com o CET.
        final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        final boolean permiteCadVlrTac = !temCET && paramCnv.containsKey(CodedValues.TPS_CAD_VALOR_TAC) && (paramCnv.get(CodedValues.TPS_CAD_VALOR_TAC) != null) && "1".equals(paramCnv.get(CodedValues.TPS_CAD_VALOR_TAC).toString());
        final boolean permiteCadVlrIof = !temCET && paramCnv.containsKey(CodedValues.TPS_CAD_VALOR_IOF) && (paramCnv.get(CodedValues.TPS_CAD_VALOR_IOF) != null) && "1".equals(paramCnv.get(CodedValues.TPS_CAD_VALOR_IOF).toString());
        final boolean permiteCadVlrLiqLib = paramCnv.containsKey(CodedValues.TPS_CAD_VALOR_LIQUIDO_LIBERADO) && (paramCnv.get(CodedValues.TPS_CAD_VALOR_LIQUIDO_LIBERADO) != null) && "1".equals(paramCnv.get(CodedValues.TPS_CAD_VALOR_LIQUIDO_LIBERADO).toString());
        final boolean permiteCadVlrMensVinc = !temCET && paramCnv.containsKey(CodedValues.TPS_CAD_VALOR_MENSALIDADE_VINC) && (paramCnv.get(CodedValues.TPS_CAD_VALOR_MENSALIDADE_VINC) != null) && "1".equals(paramCnv.get(CodedValues.TPS_CAD_VALOR_MENSALIDADE_VINC).toString());

        if (permiteCadVlrTac && ((entrada.get("ADE_VLR_TAC") == null) || "".equals(entrada.get("ADE_VLR_TAC").toString()))) {
            throw new AutorizacaoControllerException("mensagem.informe.valor.tac", (AcessoSistema) null);
        }
        if (permiteCadVlrIof && ((entrada.get("ADE_VLR_IOF") == null) || "".equals(entrada.get("ADE_VLR_IOF").toString()))) {
            throw new AutorizacaoControllerException("mensagem.informe.valor.iof", (AcessoSistema) null);
        }
        if (permiteCadVlrLiqLib && ((entrada.get("ADE_VLR_LIQUIDO") == null) || "".equals(entrada.get("ADE_VLR_LIQUIDO").toString()))) {
            throw new AutorizacaoControllerException("mensagem.informe.valor.liquido.liberado", (AcessoSistema) null);
        }
        if (permiteCadVlrMensVinc && ((entrada.get("ADE_VLR_MENS_VINC") == null) || "".equals(entrada.get("ADE_VLR_MENS_VINC").toString()))) {
            throw new AutorizacaoControllerException("mensagem.informe.valor.mensalidade.vinculada", (AcessoSistema) null);
        }
    }

    @Override
    public List<TransferObject> buscaPrazoCoeficiente(String svcCodigo, String csaCodigo, String orgCodigo) throws AutorizacaoControllerException {
        try {
            final ListaPrazoCoeficienteQuery query = new ListaPrazoCoeficienteQuery();
            query.svcCodigo = svcCodigo;
            query.csaCodigo = csaCodigo;
            query.orgCodigo = orgCodigo;
            query.dia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.localizar.prazos.deste.servico", (AcessoSistema) null, ex);
        }
    }

    @Override
    public String validaMotivoOperacao(Map<String, Object> entrada, String funCodigo, boolean permiteAlterarAdeSemMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        final boolean exigeMotivoOperacao = ParamSist.paramEquals(CodedValues.TPC_EXIGE_TIPO_MOTIVO_CANC, CodedValues.TPC_SIM, responsavel);

        // Pelo parâmetro de sistema, o motivo de operação está habilitado. Então verifica
        // se a operação de liquidar contrato exige o motivo
        if (exigeMotivoOperacao && FuncaoExigeMotivo.getInstance().exists(funCodigo, responsavel)) {
            String tmoCodigo = (String) entrada.get("TMC_CODIGO");
            String tmoIdentificador = (String) entrada.get("TMC_IDENTIFICADOR");

            if (TextHelper.isNull(tmoCodigo) && TextHelper.isNull(tmoIdentificador)) {
                tmoCodigo = (String) entrada.get("TMO_CODIGO");
                tmoIdentificador = (String) entrada.get("TMO_IDENTIFICADOR");
            }

            if (TextHelper.isNull(tmoCodigo) && TextHelper.isNull(tmoIdentificador)) {
                // Verifica parâmetro de consignatária se pode fazer alteração via lote sem informar motivo de operação
                if (CodedValues.FUN_ALT_CONSIGNACAO.equals(funCodigo) && permiteAlterarAdeSemMotivoOperacao) {
                    // Motivo não informado, e é permitido não informar, então retorna nulo.
                    return null;
                }
                // Nos demais cenários, reporta erro de motivo não informado
                throw new AutorizacaoControllerException("mensagem.erro.informacao.motivo.operacao.ausente", responsavel);
            } else {
                try {
                    // Recupera tipo motivo cancelamento pelo código ou pelo identificador
                    TipoMotivoOperacaoTransferObject tmoTO = null;
                    if (!TextHelper.isNull(tmoCodigo)) {
                        tmoTO = tipoMotivoOperacaoController.findMotivoOperacao(tmoCodigo, responsavel);
                    } else if (!TextHelper.isNull(tmoIdentificador)) {
                        tmoTO = tipoMotivoOperacaoController.findMotivoOperacaoByCodIdent(tmoIdentificador, responsavel);
                    }

                    // Seta o código do motivo no hash de entrada
                    return tmoTO.getTmoCodigo();
                } catch (final TipoMotivoOperacaoControllerException ex) {
                    throw new AutorizacaoControllerException(ex);
                }
            }
        }
        return null;
    }

    /**
     * String numerContratoBeneficio, boolean buscaBenificiario
     * @param codVerba
     * @param csaCodigo
     * @param tipo
     * @param tipoCodigo
     * @param estIdentificador
     * @param orgIdentificador
     * @param matricula
     * @param cpf
     * @param cnvAtivo
     * @param serAtivo
     * @param svcIdentificador
     * @param nseCodigo
     * @param inclusao
     * @param renegociacao
     * @param ignorarRseCodigo
     * @param numerContratoBeneficio
     * @param buscaBenificiario
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    public List<Map<String, Object>> lstServidorPorCnv(String codVerba, String csaCodigo, String tipo, String tipoCodigo, String estIdentificador,
            String orgIdentificador, String matricula, String cpf, boolean cnvAtivo, boolean serAtivo, String svcIdentificador, String nseCodigo,
            boolean inclusao, boolean renegociacao, List<String> ignorarRseCodigo, String numerContratoBeneficio, boolean buscaBenificiario, AcessoSistema responsavel) throws AutorizacaoControllerException {

        List<TransferObject> servidorCnvRegs = null;
        try {
            final ListaServidorPorCnvQuery lstServidorCnvQuery = new ListaServidorPorCnvQuery();
            lstServidorCnvQuery.codVerba = codVerba;
            lstServidorCnvQuery.csaCodigo = csaCodigo;
            lstServidorCnvQuery.tipo = tipo;
            lstServidorCnvQuery.tipoCodigo = tipoCodigo;
            lstServidorCnvQuery.estIdentificador = estIdentificador;
            lstServidorCnvQuery.orgIdentificador = orgIdentificador;
            lstServidorCnvQuery.rseMatricula = matricula;
            lstServidorCnvQuery.serCpf = cpf;
            lstServidorCnvQuery.cnvAtivo = cnvAtivo;
            lstServidorCnvQuery.svcIdentificador = svcIdentificador;
            lstServidorCnvQuery.nseCodigo = nseCodigo;
            lstServidorCnvQuery.inclusao = inclusao;
            lstServidorCnvQuery.renegociacao = renegociacao;
            lstServidorCnvQuery.ignorarRseCodigo = ignorarRseCodigo;

            lstServidorCnvQuery.numeroContratoBeneficio = numerContratoBeneficio;
            lstServidorCnvQuery.buscaBeneficiario = buscaBenificiario;

            if (CanalEnum.SOAP.equals(responsavel.getCanal()) && responsavel.isCsa()) {
                final String param = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_PESQUISA_MATRICULA_EXATA_VIA_SOAP, responsavel);
                lstServidorCnvQuery.matriculaExata = (param != null) && CodedValues.TPA_SIM.equals(param);
            } else if (ParamSist.paramEquals(CodedValues.TPC_PESQUISA_MATRICULA_INTEIRA, CodedValues.TPC_SIM, responsavel)) {
                lstServidorCnvQuery.matriculaExata = true;
            }

            servidorCnvRegs = lstServidorCnvQuery.executarDTO();
        } catch (HQueryException | ParametroControllerException ex) {
            throw new AutorizacaoControllerException(ex);
        }

        // Remove registros ligados a servidores excluídos ou bloqueados.
        servidorCnvRegs = removeSerExcluidos(servidorCnvRegs, inclusao, serAtivo, buscaBenificiario, responsavel);

        // Transforma a lista de TO para Map e retorna
        final List<Map<String, Object>> serCnvCandidatos = new ArrayList<>();
        final Iterator<TransferObject> serCnvIt = servidorCnvRegs.iterator();
        while (serCnvIt.hasNext()) {
            serCnvCandidatos.add(new HashMap<>(serCnvIt.next().getAtributos()));
        }

        return serCnvCandidatos;
    }


    private List<TransferObject> removeSerExcluidos(List<TransferObject> serCnvList, boolean inclusao, boolean serAtivo, boolean buscaBenificiario, AcessoSistema responsavel) {
        if ((serCnvList != null) && !serCnvList.isEmpty() && serAtivo) {
            // Verifica se todos são do mesmo CPF, pois caso não sejam é mais seguro
            // não remover os excluídos, pois o lote pode estar referenciando alguém
            // excluído e não o ativo.
            boolean serCpfIgual = true;
            String primeiroCpf = null;

            String columnsCPF = null;
            if (buscaBenificiario) {
                columnsCPF = Columns.BFC_CPF;
            } else {
                columnsCPF = Columns.SER_CPF;
            }

            for (final TransferObject ser : serCnvList) {
                if (primeiroCpf == null) {
                    primeiroCpf = ser.getAttribute(columnsCPF).toString();
                }
                serCpfIgual = serCpfIgual && primeiroCpf.equals(ser.getAttribute(columnsCPF).toString());
            }

            // Se todos os registros são do mesmo CPF, então remove aqueles
            // registros de matrículas bloqueadas/excluídas.
            if (serCpfIgual) {
                final List<TransferObject> servidorCandidato = new ArrayList<>();
                for (final TransferObject ser : serCnvList) {
                    final String srsCodigo = ser.getAttribute(Columns.SRS_CODIGO).toString();
                    // Se não é inativo (excluído ou falecido), verifica se pode ser retornado como candidato
                    if (!CodedValues.SRS_INATIVOS.contains(srsCodigo)) {
                        if (CodedValues.SRS_BLOQUEADOS.contains(srsCodigo)) {
                            // Se é bloqueado, verifica se pode incluir consignação para servidor bloqueado
                            if (inclusao) {
                                try {
                                    final String svcCodigo = ser.getAttribute(Columns.SVC_CODIGO).toString();
                                    final CustomTransferObject paramCTO = parametroController.getParamSvcCse(svcCodigo, CodedValues.TPS_PERMITE_INCLUIR_ADE_RSE_BLOQUEADO, responsavel);
                                    if ((paramCTO != null) && (paramCTO.getAttribute(Columns.PSE_VLR) != null) && "1".equals(paramCTO.getAttribute(Columns.PSE_VLR))) {
                                        servidorCandidato.add(ser);
                                    }
                                } catch (final ParametroControllerException ex) {
                                    LOG.error(ex.getMessage(), ex);
                                }
                            }
                        } else {
                            servidorCandidato.add(ser);
                        }
                    }
                }
                serCnvList = servidorCandidato;
            }
        }
        return serCnvList;
    }

    @Override
    public List<Map<String, Object>> buscaConsignacaoPorCnvSer(String operacao, String codVerba, String csaCodigo, String rseMatricula, String serCpf, String orgIdentificador, String svcIdentificador, String estIdentificador, boolean cnvAtivo, TransferObject criterio, String nseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        final List<String> sadCodigos = new ArrayList<>();
        /* Contratos aguardando confirmação: Só podem ser confirmados */
        sadCodigos.add(CodedValues.SAD_SOLICITADO);
        sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
        /* Contratos em Aberto: Podem ser liquidados e/ou alterados */
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        /* Contratos em Aberto: Só podem ser liquidados */
        sadCodigos.add(CodedValues.SAD_EMCARENCIA);
        sadCodigos.add(CodedValues.SAD_ESTOQUE);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
        sadCodigos.add(CodedValues.SAD_SUSPENSA);
        sadCodigos.add(CodedValues.SAD_SUSPENSA_CSE);
        /* Contratos aguardando liquidação: Só podem ser liquidados */
        sadCodigos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);
        sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
        /* Contratos finalizados: não podem ser alterados */
        sadCodigos.add(CodedValues.SAD_LIQUIDADA);
        sadCodigos.add(CodedValues.SAD_CONCLUIDO);
        /* Contratos aguardando deferimento: só podem ser confirmados*/
        sadCodigos.add(CodedValues.SAD_AGUARD_DEFER);

        final ObtemConsignacaoPorCnvSerQuery adesPorCnvSer = new ObtemConsignacaoPorCnvSerQuery();
        adesPorCnvSer.codVerba = codVerba;
        adesPorCnvSer.csaCodigo = csaCodigo;
        adesPorCnvSer.rseMatricula = rseMatricula;
        adesPorCnvSer.serCpf = serCpf;
        adesPorCnvSer.orgIdentificador = orgIdentificador;
        adesPorCnvSer.svcIdentificador = svcIdentificador;
        adesPorCnvSer.estIdentificador = estIdentificador;
        adesPorCnvSer.sadCodigos = sadCodigos;
        adesPorCnvSer.criterio = criterio;
        adesPorCnvSer.nseCodigo = nseCodigo;
        // Pesquisa todos convênior, e verifica a quantidade retornada
        adesPorCnvSer.cnvAtivo = false;

        List<TransferObject> ades = null;
        List<Map<String, Object>> retornoList = new ArrayList<>();
        final List<Map<String, Object>> retornoCnvInativos = new ArrayList<>();

        try {
            ades = adesPorCnvSer.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }

        // Verifica os status dos contratos encontrados
        final Iterator<TransferObject> it = ades.iterator();
        TransferObject next = null;
        String sadCodigo = null;
        String scvCodigo = null;

        int abertos = 0;
        int abertosCnvInativos = 0;
        int statusNaoPermitido = 0;
        String sadDescricao = null;

        while (it.hasNext()) {
            next = it.next();

            sadCodigo = (String) next.getAttribute(Columns.ADE_SAD_CODIGO);
            scvCodigo = (String) next.getAttribute(Columns.CNV_SCV_CODIGO);

            if ((sadCodigo != null) && (scvCodigo != null)) {
                if (    // Alteração via lote somente em contratos Deferidos ou Em Andamento
                        (ALTERACAO.equalsIgnoreCase(operacao) && CodedValues.SAD_DEFERIDA.equals(sadCodigo)) ||
                        (ALTERACAO.equalsIgnoreCase(operacao) && CodedValues.SAD_EMANDAMENTO.equals(sadCodigo)) ||
                        // Exclusão via lote em contratos abertos que permite exclusão
                        (EXCLUSAO.equalsIgnoreCase(operacao) && CodedValues.SAD_DEFERIDA.equals(sadCodigo)) ||
                        (EXCLUSAO.equalsIgnoreCase(operacao) && CodedValues.SAD_EMANDAMENTO.equals(sadCodigo)) ||
                        (EXCLUSAO.equalsIgnoreCase(operacao) && CodedValues.SAD_EMCARENCIA.equals(sadCodigo)) ||
                        (EXCLUSAO.equalsIgnoreCase(operacao) && CodedValues.SAD_ESTOQUE.equals(sadCodigo)) ||
                        (EXCLUSAO.equalsIgnoreCase(operacao) && CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo)) ||
                        (EXCLUSAO.equalsIgnoreCase(operacao) && CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo)) ||
                        (EXCLUSAO.equalsIgnoreCase(operacao) && CodedValues.SAD_SUSPENSA.equals(sadCodigo)) ||
                        (EXCLUSAO.equalsIgnoreCase(operacao) && CodedValues.SAD_SUSPENSA_CSE.equals(sadCodigo)) ||
                        (EXCLUSAO.equalsIgnoreCase(operacao) && CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(sadCodigo)) ||
                        // Confirmação via lote somente em contratos Solicitação e Aguard. Confirmação
                        (CONFIRMACAO.equalsIgnoreCase(operacao) && CodedValues.SAD_SOLICITADO.equals(sadCodigo)) ||
                        (CONFIRMACAO.equalsIgnoreCase(operacao) && CodedValues.SAD_AGUARD_CONF.equals(sadCodigo)) ||
                        (CONFIRMACAO.equalsIgnoreCase(operacao) && CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo))
                        ) {

                    if (CodedValues.SCV_INATIVO.equals(scvCodigo) && cnvAtivo) {
                        // Se o convênio está inativo e deveria retornar apenas de ativos,
                        // adiciona em uma lista separada para verificar o total encontrado
                        abertosCnvInativos++;
                        retornoCnvInativos.add(new HashMap<>(next.getAtributos()));
                    } else {
                        abertos++;
                        retornoList.add(new HashMap<>(next.getAtributos()));
                    }

                } else {
                    statusNaoPermitido++;
                    sadDescricao = (String) next.getAttribute(Columns.SAD_DESCRICAO);
                }
            }
        }

        if ((abertos == 0) && (abertosCnvInativos > 0) && EXCLUSAO.equalsIgnoreCase(operacao)) {
            // Se os abertos são de convênios inativos, então retorna os contratos de convênios
            // inativos mesmo, pois estes podem ser liquidados. Para alteração, estes não podem ser listados.
            retornoList = retornoCnvInativos;
            abertos = abertosCnvInativos;
        }

        if ((abertos == 0) && (statusNaoPermitido == 0)) {
            // Se não tem abertos nem liquidados, então retorna mensagem específica
            throw new AutorizacaoControllerException("mensagem.nenhumaConsignacaoEncontrada", responsavel);
        } else if (abertos > 0) {
            // Se tem um em aberto não importa quantos liquidados tenham
            return retornoList;
        } else {
            // Se não tem contratos em aberto, mas tem contratos liquidados
            throw new AutorizacaoControllerException("mensagem.situacaoConsignacaoInvalida", responsavel, sadDescricao);
        }
    }

    /**
     * Busca as consignações para o servidor e convênio (csaCodigo / svcCodigo) que estão
     * abertas porém marcadas com o identificador da nova reserva para associar à inclusão via lote,
     * efetuando assim um comando de renegociação.
     * @param rseCodigo
     * @param csaCodigo
     * @param svcCodigo
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    public List<String> buscaConsignacaoAbertaParaRenegociacao(String rseCodigo, String csaCodigo, String svcCodigo, String adeIdentificador, boolean fixaServico, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final String hoje = DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMdd");
            if (TextHelper.isNull(adeIdentificador) || ("LOTE " + hoje).equals(adeIdentificador)) {
                return null;
            }

            final ListaAdeAbertaParaRenegociacaoQuery query = new ListaAdeAbertaParaRenegociacaoQuery();
            query.rseCodigo = rseCodigo;
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;
            query.adeIdentificador = adeIdentificador;
            query.fixaServico = fixaServico;

            final List<String> adeCodigos = query.executarLista();

            // se não encontrar nenhum contrato, verifica se isto se dá pela falta de relacionamento de renegociação entre os
            // serviços. caso sim, retorna mensagem de erro para linha
            if ((adeCodigos == null) || adeCodigos.isEmpty()) {
                final ListaServicoAdeAbertaParaRenegociacaoQuery lstServicosAdeAbertas = new ListaServicoAdeAbertaParaRenegociacaoQuery();
                lstServicosAdeAbertas.rseCodigo = rseCodigo;
                lstServicosAdeAbertas.csaCodigo = csaCodigo;
                lstServicosAdeAbertas.adeIdentificador = adeIdentificador;
                if (fixaServico) {
                    lstServicosAdeAbertas.svcCodigo = svcCodigo;
                }
                final List<String> svcList = lstServicosAdeAbertas.executarLista();

                if ((svcList != null) && !svcList.isEmpty()) {
                    final ListaRelacionamentosServicoQuery lstRelacionamentos = new ListaRelacionamentosServicoQuery();
                    lstRelacionamentos.svcCodigoOrigem = svcCodigo;
                    lstRelacionamentos.svcCodigoDestino = svcList;
                    lstRelacionamentos.tntCodigo = CodedValues.TNT_RENEGOCIACAO;

                    final List<TransferObject> svcsOrigem = lstRelacionamentos.executarDTO();

                    if ((svcsOrigem == null) || svcsOrigem.isEmpty()) {
                        throw new AutorizacaoControllerException("mensagem.erro.nao.ha.relacionamento.renegociacao.entre.servicos.envolvidos", responsavel);
                    }
                }
            }

            // Filtra os contratos que podem ser negociados
            final ListaConsignacaoRenegociavelNativeQuery lcrQuery = new ListaConsignacaoRenegociavelNativeQuery();
            lcrQuery.adeCodigos = adeCodigos;
            lcrQuery.tipoOperacao = "renegociar";
            lcrQuery.csaCodigo = csaCodigo;
            lcrQuery.svcCodigo = svcCodigo;
            lcrQuery.responsavel = responsavel;

            return lcrQuery.executarLista();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.localizar.consignacoes.para.renegociacao", responsavel);
        }
    }

    /**
     * Busca as consignações para o servidor e convênio (csaCodigo / svcCodigo) que foram
     * liquidadas dentro de uma janela de horário para associar à uma inclusão via lote,
     * efetuando assim um comando de renegociação via lote.
     * @param rseCodigo
     * @param csaCodigo
     * @param svcCodigo
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    @SuppressWarnings("java:S1141")
    public List<String> buscaConsignacaoLiquidadaParaRenegociacao(String rseCodigo, String csaCodigo, String svcCodigo, boolean fixaServico, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // Obtém parâmetro de sistema que informa a quantidade de dias
            // que um contrato liquidado pode ser considerado uma exclusão
            // da inclusão que está sendo realizada, caracterizando uma RENEGOCIAÇÃO.
            int qtdDiasRenegViaLote = 0;
            try {
                final Object paramQtdDiasRenegViaLote = ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_APOS_LIQUI_PARA_RENEG_VIA_LOTE, responsavel);
                qtdDiasRenegViaLote = !TextHelper.isNull(paramQtdDiasRenegViaLote) ? Integer.parseInt((String) paramQtdDiasRenegViaLote) : 0;
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException(ex);
            }

            if (qtdDiasRenegViaLote <= 0) {
                return null;
            }

            // Define o intervalo para pesquisa
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -1 * qtdDiasRenegViaLote);
            final Date ocaData = cal.getTime();

            final ListaAdeLiquidadaParaRenegociacaoQuery query = new ListaAdeLiquidadaParaRenegociacaoQuery();
            query.rseCodigo = rseCodigo;
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;
            query.ocaData = ocaData;
            query.fixaServico = fixaServico;

            // Filtra os contratos que podem ser negociados
            final ListaConsignacaoRenegociavelNativeQuery lcrQuery = new ListaConsignacaoRenegociavelNativeQuery();
            lcrQuery.adeCodigos = query.executarLista();
            lcrQuery.tipoOperacao = "renegociar";
            lcrQuery.csaCodigo = csaCodigo;
            lcrQuery.svcCodigo = svcCodigo;
            lcrQuery.fixaServico = fixaServico;
            lcrQuery.responsavel = responsavel;

            return lcrQuery.executarLista();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.localizar.consignacoes.para.renegociacao", responsavel);
        }
    }

    /**
     * Cria o relacionamento entre as consignações liquidadas anteriormente via lote
     * para a nova inclusão também via lote, efetuando assim um comando de renegociação via lote.
     * @param adeCodigoDestino
     * @param adeCodigosOrigem
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    @Override
    public void relacionarRenegociacaoViaLote(String adeCodigoDestino, List<String> adeCodigosOrigem, String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            if ((adeCodigosOrigem != null) && !adeCodigosOrigem.isEmpty()) {
                final AutDesconto autdesDestino = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigoDestino);
                BigDecimal vlrTotalRenegociacao = new BigDecimal("0.00");
                final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

                final Short adeIncMargem = paramSvcCse.getTpsIncideMargem();
                for (final String adeCodigo: adeCodigosOrigem) {
                    final AutDesconto autdes = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);

                    if (AutorizacaoHelper.valorMargemDisponivelRenegociacao(adeIncMargem, autdes.getAdeIncMargem(), responsavel)) {
                        vlrTotalRenegociacao = vlrTotalRenegociacao.add(autdes.getAdeVlr());
                    }

                    RelacionamentoAutorizacaoHome.create(adeCodigo, adeCodigoDestino, CodedValues.TNT_CONTROLE_RENEGOCIACAO, responsavel.getUsuCodigo());
                }

                if (!CodedValues.SAD_AGUARD_CONF.equals(autdesDestino.getStatusAutorizacaoDesconto().getSadCodigo()) &&
                    !autorizacaoController.podeConfirmarRenegociacao(autdesDestino.getAdeVlr(), svcCodigo, csaCodigo, vlrTotalRenegociacao, responsavel)) {
                    autorizacaoController.modificaSituacaoADE(autdesDestino, CodedValues.SAD_AGUARD_CONF, responsavel);
                }
            }
        } catch (com.zetra.econsig.exception.CreateException | FindException | ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.relacionar.consignacoes.renegociacao.com.nova.inclusao", responsavel);
        }
    }

    /**
     * Busca dados da consignação de mensalidade de benefício para incluir consignações secundárias no processamento de lote
     * por consignatária, por carteirinha e tipo de lançamento
     * @param csaCodigo
     * @param cbeNumero
     * @param tlaCodigo
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    public List<TransferObject> buscarConsignacaoMensalidadeBeneficio(String csaCodigo, String cbeNumero, String tlaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // recupera o tipo de lançamento
            final TipoLancamento tipoLancamento = TipoLancamentoHome.findByPrimaryKey(tlaCodigo);
            // recupera o laçamento de mensalidade
            final ListarContratosMensalidadeBeneficioQuery query = new ListarContratosMensalidadeBeneficioQuery();
            query.csaCodigo = csaCodigo;
            query.cbeNumero = cbeNumero;
            query.tlaCodigoMensalidade = !TextHelper.isNull(tipoLancamento.getTlaCodigoPai()) ? tipoLancamento.getTlaCodigoPai() : tlaCodigo;
            final List<TransferObject> adeCodigos = query.executarDTO();
            if ((adeCodigos == null) || adeCodigos.isEmpty() || (adeCodigos.size() > 1)) {
                throw new AutorizacaoControllerException(MENSAGEM_ERRO_NAO_POSSIVEL_LOCALIZAR_MENSALIDADE_BENEFICIOS, responsavel);
            }
            return adeCodigos;
        } catch (final FindException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException(MENSAGEM_ERRO_TIPO_LANCAMENTO_BENEFICIOS_NAO_ENCONTRADO, responsavel);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(MENSAGEM_ERRO_NAO_POSSIVEL_LOCALIZAR_MENSALIDADE_BENEFICIOS, responsavel);
        }
    }

    /**
     * Busca dados da consignação de mensalidade de benefício para incluir consignações secundárias no processamento de lote
     * por consignatária, por carteirinha e código de verba destino
     * @param csaCodigo
     * @param cbeNumero
     * @param cnvCodVerba
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    public List<TransferObject> buscarConsignacaoMensalidadeBeneficioVerbaDestino(String csaCodigo, String cbeNumero, String cnvCodVerba, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ArrayList<String> svcCodigosOrigem = new ArrayList<>();
            final List<TransferObject> relSvc = buscarRelacionamentoServicoVerbaDestino(csaCodigo, cnvCodVerba, responsavel);
            if ((relSvc == null) || relSvc.isEmpty()) {
                throw new AutorizacaoControllerException(MENSAGEM_ERRO_RELACIONAMENTO_TIPO_LANCAMENTO_BENEFICIOS_NAO_ENCONTRADO, responsavel);
            } else {
                for (final TransferObject svc : relSvc) {
                    svcCodigosOrigem.add((String) svc.getAttribute(Columns.RSV_SVC_CODIGO_ORIGEM));
                }
            }
            // recupera o laçamento de mensalidade
            final ListarContratosMensalidadeBeneficioQuery query = new ListarContratosMensalidadeBeneficioQuery();
            query.csaCodigo = csaCodigo;
            query.cbeNumero = cbeNumero;
            query.svcCodigos = svcCodigosOrigem;
            final List<TransferObject> adeCodigos = query.executarDTO();
            if ((adeCodigos == null) || adeCodigos.isEmpty() || (adeCodigos.size() > 1)) {
                throw new AutorizacaoControllerException(MENSAGEM_ERRO_NAO_POSSIVEL_LOCALIZAR_MENSALIDADE_BENEFICIOS, responsavel);
            }
            return adeCodigos;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(MENSAGEM_ERRO_NAO_POSSIVEL_LOCALIZAR_MENSALIDADE_BENEFICIOS, responsavel);
        }
    }

    /**
     * Busca dados de relacionamento de serviço de benefícios pelo código do serviço de origem e pelo tipo de lançamento
     * por consignatária
     * @param svcCodigoOrigem
     * @param tlaCodigo
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    public Servico buscarServicoDestinoRelacionamentoBeneficio(String svcCodigoOrigem, String tlaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // recupera o tipo de lançamento
            final TipoLancamento tipoLancamento = TipoLancamentoHome.findByPrimaryKey(tlaCodigo);
            // recupera o relacionamento
            final ListaRelacionamentosServicoQuery query = new ListaRelacionamentosServicoQuery();
            query.tntCodigo = tipoLancamento.getTipoNatureza().getTntCodigo();
            query.svcCodigoOrigem = svcCodigoOrigem;
            final List<TransferObject> relSvc = query.executarDTO();
            if ((relSvc == null) || relSvc.isEmpty() || (relSvc.size() > 1)) {
                throw new AutorizacaoControllerException(MENSAGEM_ERRO_RELACIONAMENTO_TIPO_LANCAMENTO_BENEFICIOS_NAO_ENCONTRADO, responsavel);
            }
            return ServicoHome.findByPrimaryKey((String) relSvc.get(0).getAttribute(Columns.RSV_SVC_CODIGO_DESTINO));
        } catch (final FindException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException(MENSAGEM_ERRO_TIPO_LANCAMENTO_BENEFICIOS_NAO_ENCONTRADO, responsavel);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(MENSAGEM_ERRO_RELACIONAMENTO_TIPO_LANCAMENTO_BENEFICIOS_NAO_ENCONTRADO, responsavel);
        }
    }

    /**
     * Cria o relacionamento entre os lançamentos de mensalidade e os lançamentos secundários de benefícios via lote
     * @param adeCodigoDestino
     * @param adeCodigoOrigem
     * @param tlaCodigo
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    @Override
    public void relacionarMensalidadeViaLote(String adeCodigoDestino, String adeCodigoOrigem, String tlaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // recupera o tipo de lançamento
            final TipoLancamento tipoLancamento = TipoLancamentoHome.findByPrimaryKey(tlaCodigo);
            // Cria o relacionamento
            RelacionamentoAutorizacaoHome.create(adeCodigoOrigem, adeCodigoDestino, tipoLancamento.getTipoNatureza().getTntCodigo() ,responsavel.getUsuCodigo());
        } catch (final FindException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException(MENSAGEM_ERRO_TIPO_LANCAMENTO_BENEFICIOS_NAO_ENCONTRADO, responsavel);
        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.relacionar.consignacoes.mensalidade.com.nova.inclusao", responsavel);
        }
    }

    /**
     * Busca dados de relacionamento de serviços pelo código de verba
     * @param csaCodigo
     * @param cnvCodVerba
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    public List<TransferObject> buscarRelacionamentoServicoVerbaDestino(String csaCodigo, String cnvCodVerba, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            List<TransferObject> relSvc = null;
            final ArrayList<String> svcCodigosDestino = new ArrayList<>();
            final ArrayList<String> tntCodigos = new ArrayList<>();
            // recupera os serviços do código de verba informado
            final ListaConveniosQuery cnvQuery = new ListaConveniosQuery();
            cnvQuery.cnvCodVerba = cnvCodVerba;
            cnvQuery.csaCodigo = csaCodigo;
            cnvQuery.ativo = true;
            final List<TransferObject> cnvCodigos = cnvQuery.executarDTO();
            if ((cnvCodigos == null) || cnvCodigos.isEmpty()) {
                throw new AutorizacaoControllerException("mensagem.convenioNaoEncontrado", responsavel);
            } else {
                // buscar o relacionamento dos servicos encontrados
                for (final TransferObject cnv : cnvCodigos) {
                    final String svcDest = (String) cnv.getAttribute(Columns.CNV_SVC_CODIGO);
                    if (!svcCodigosDestino.contains(svcDest)) {
                        svcCodigosDestino.add(svcDest);
                        final ListaRelacionamentosServicoQuery query = new ListaRelacionamentosServicoQuery();
                        query.svcCodigoDestino = cnv.getAttribute(Columns.CNV_SVC_CODIGO);
                        relSvc = query.executarDTO();
                        if ((relSvc == null) || relSvc.isEmpty()) {
                            throw new AutorizacaoControllerException("mensagem.erro.relacionamento.servico.nao.encontrado", responsavel);
                        } else {
                            for (final TransferObject rel : relSvc) {
                                final String tntCodigo = (String) rel.getAttribute(Columns.RSV_TNT_CODIGO);
                                if (!tntCodigos.contains(tntCodigo)) {
                                    tntCodigos.add(tntCodigo);
                                }
                            }
                            if (tntCodigos.isEmpty() || (tntCodigos.size() > 1)) {
                                throw new AutorizacaoControllerException("mensagem.erro.relacionamento.tipo.natureza.nao.encontrado", responsavel);
                            }
                        }
                    }
                }
            }
            return relSvc;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(MENSAGEM_ERRO_NAO_POSSIVEL_LOCALIZAR_MENSALIDADE_BENEFICIOS, responsavel);
        }
    }

    /**
     * Busca o tipo de lançamento pelo tla_codigo
     * @param tlaCodigo
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    @Override
    public TipoLancamento buscarTipoLancamentoPorTntCodigo(String tntCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // recupera o tipo de lançamento
            return TipoLancamentoHome.findByTntCodigo(tntCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(MENSAGEM_ERRO_TIPO_LANCAMENTO_BENEFICIOS_NAO_ENCONTRADO, responsavel);
        }
    }

    /**
     * Busca o tipo de lançamento pelo tla_codigo
     * @param tlaCodigo
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    @Override
    public TipoLancamento buscarTipoLancamentoPorTlaCodigo(String tlaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // recupera o tipo de lançamento
            return TipoLancamentoHome.findByPrimaryKey(tlaCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(MENSAGEM_ERRO_TIPO_LANCAMENTO_BENEFICIOS_NAO_ENCONTRADO, responsavel);
        }
    }

    // Métodos utilizados no controle de processamento de lote via SOAP

    @Override
    public ControleProcessamentoLote findProcessamentoByArquivoCentralizador(String arquivoCentralizador) {
        return ControleProcessamentoLoteHome.findProcessamentoByArquivoCentralizador(arquivoCentralizador);
    }

    @Override
    public ControleProcessamentoLote findProcessamentoByArquivoeConsig(String arquivoeConsig) {
        return ControleProcessamentoLoteHome.findProcessamentoByArquivoeConsig(arquivoeConsig);
    }

    @Override
    public ControleProcessamentoLote incluirProcessamento(String arquivoCentralizador, String arquivoeConsig, Short status, AcessoSistema responsavel) throws ZetraException {
        try {
            return ControleProcessamentoLoteHome.create(arquivoCentralizador, arquivoeConsig, status, responsavel.getCanal(), responsavel.getUsuCodigo());
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ZetraException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel);
        }
    }

    @Override
    public void excluirProcessamento(ControleProcessamentoLote controleProcessamentoLote, AcessoSistema responsavel) throws ZetraException {
        try {
            // Remove os blocos de processamento associados a este controle processamento
            BlocoProcessamentoLoteHome.removerBlocos(controleProcessamentoLote.getCplArquivoEconsig());
            // Remove o registro de controle do processamento
            AbstractEntityHome.remove(controleProcessamentoLote);
        } catch (final RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ZetraException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel);
        }
    }

    @Override
    public void alterarProcessamento(ControleProcessamentoLote controleProcessamentoLote, AcessoSistema responsavel) throws ZetraException {
        try {
            AbstractEntityHome.update(controleProcessamentoLote);
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ZetraException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel);
        }
    }

    @Override
    public void carregarBlocosLote(String nomeArqEntrada, String nomeArqConfEntrada, String nomeArqConfTradutor, String csaCodigo, Date bplPeriodo, ControleProcessamentoLote controleProcessamentoLote, AcessoSistema responsavel) throws ZetraException {
        boolean loteFebraban = false;

        final File arqEntrada = new File(nomeArqEntrada);
        if (!arqEntrada.exists()) {
            LOG.error("Arquivo não encontrado: \"" + nomeArqEntrada + "\"");
            throw new ZetraException("mensagem.erro.lote.arquivo.nao.encontrado", responsavel);
        }

        // Verifica o identificador do arquivo de configuração de entrada, para determinar
        // se é um arquivo no padrão FEBRABAN
        try {
            final DocumentoTipo docEntrada = XmlHelper.unmarshal(new FileInputStream(nomeArqConfEntrada));
            if ((docEntrada != null) && (docEntrada.getID() != null) && CodedValues.CODIGO_ID_FEBRABAN.equalsIgnoreCase(docEntrada.getID())) {
                loteFebraban = true;
            }

            // Configura o leitor de acordo com o arquivo de entrada
            final LeitorArquivoTexto leitor;
            if (loteFebraban) {
                leitor = new LeitorArquivoFebraban(nomeArqConfEntrada, nomeArqEntrada);
            } else if (nomeArqEntrada.toLowerCase().endsWith(".zip") || nomeArqEntrada.toLowerCase().endsWith(".zip.prc")) {
                leitor = new LeitorArquivoTextoZip(nomeArqConfEntrada, nomeArqEntrada);
            } else {
                leitor = new LeitorArquivoTexto(nomeArqConfEntrada, nomeArqEntrada);
            }

            // Hash que recebe os dados do que serão lidos do arquivo de entrada
            final HashMap<String, Object> entrada = new HashMap<>();

            // Escritor e tradutor
            final EscritorMemoria escritor = new EscritorMemoria(entrada);
            final Tradutor tradutor = new Tradutor(nomeArqConfTradutor, leitor, escritor);

            // Data de inclusão dos blocos de processamento
            final Date bplDataInclusao = DateHelper.getSystemDatetime();

            // Objeto usado para converter o Map com campos de entrada em JSON
            final Gson gson = new Gson();

            // Contador de linhas
            int contador = 0;

            // Gerenciador de sessão do Hibernate para processos em batch
            final Session session = SessionUtil.getSession();
            final BatchManager batman = new BatchManager(session);

            // Caso seja um lote de múltiplas consignatárias, cria um Map de CSA_IDENTIFICADOR por CSA_CODIGO para mapear as linhas
            final Map<String, String> csaIdentificadorMap = (TextHelper.isNull(csaCodigo) ? consignatariaController.getCsaIdentificadorMap(responsavel) : null);

            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.traducao.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            tradutor.iniciaTraducao();
            while (tradutor.traduzProximo()) {
                contador++;
                if ((contador % 1000) == 0) {
                    LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.linhas.lidas.arg0", responsavel, String.valueOf(contador)));
                }

                // Realiza a validação de segurança contra ataque de XSS nos campos do lote
                for (final Entry<String, Object> entry : entrada.entrySet()) {
                    final Object value = entry.getValue();
                    if (value instanceof String) {
                        // Se for String, realiza o tratamento anti-XSS
                        entrada.put(entry.getKey(), XSSPreventionFilter.stripXSS((String) value));
                    }
                }

                // Se csaCodigo não preenchido significa importação de lote de múltiplas consignatárias, buscando a consignatária
                // linha-a-linha, pelo campo CSA_IDENTIFICADOR
                String csaCodigoLinha = csaCodigo;
                if (TextHelper.isNull(csaCodigoLinha)) {
                    final String csaIdentificador = (String) entrada.get("CSA_IDENTIFICADOR");
                    if (TextHelper.isNull(csaIdentificador)) {
                        LOG.warn("Lote de múltiplas consignatárias deve informar o campo CSA_IDENTIFICADOR na entrada.");
                        throw new ZetraException(MENSAGEM_CONSIGNATARIA_NAO_INFORMADA, responsavel);
                    }
                    if ((csaIdentificadorMap == null) || !csaIdentificadorMap.containsKey(csaIdentificador)) {
                        LOG.warn("Campos CSA_IDENTIFICADOR informado na entrada não foi encontrado.");
                        throw new ZetraException(MENSAGEM_CONSIGNATARIA_NAO_INFORMADA, responsavel);
                    }

                    csaCodigoLinha = csaIdentificadorMap.get(csaIdentificador);
                }

                // Coloca colchetes ao redor para evitar que espaços ao final sejam perdidos
                final String linhaEntrada = "[" + leitor.getLinha() + "]";
                // Transforma mapa de campos em um JSON
                final String camposEntrada = gson.toJson(entrada);

                // Cria o bloco processamento lote com status dos blocos em preparação
                BlocoProcessamentoLoteHome.create(controleProcessamentoLote, StatusBlocoProcessamentoEnum.PREPARANDO, csaCodigoLinha, leitor.getNumeroLinha(), bplDataInclusao, bplPeriodo, linhaEntrada, camposEntrada, session);

                batman.iterate();
            }

            batman.finish();
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.total.linhas.lidas.arg0", responsavel, String.valueOf(contador)));

            tradutor.encerraTraducao();
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.traducao.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

        } catch (FileNotFoundException | CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ZetraException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);

        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw ex;
        }
    }

    @Override
    public List<TransferObject> lstBlocoProcessamentoLote(String nomeArquivo, String csaCodigo, StatusBlocoProcessamentoEnum status, AcessoSistema responsavel) throws ZetraException {
        try {
            final ListarBlocosProcessamentoLoteQuery query = new ListarBlocosProcessamentoLoteQuery(nomeArquivo, status, csaCodigo);
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public void atualizarStatusBlocos(String nomeArquivo, String csaCodigo, StatusBlocoProcessamentoEnum statusOrigem, StatusBlocoProcessamentoEnum statusDestino, AcessoSistema responsavel) throws ZetraException {
        try {
            BlocoProcessamentoLoteHome.atualizarStatusBlocos(statusOrigem, statusDestino, nomeArquivo, responsavel.getCsaCodigo());
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstLotesEmProcessamento(AcessoSistema responsavel) throws ZetraException {
        try {
            final ListarLotesEmProcessamentoQuery query = new ListarLotesEmProcessamentoQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public void atualizarBlocoLote(String nomeArquivo, Integer numLinha, StatusBlocoProcessamentoEnum novoStatus, String critica, AcessoSistema responsavel) throws ZetraException {
        try {
            final BlocoProcessamentoLote bpl = BlocoProcessamentoLoteHome.findByPrimaryKey(nomeArquivo, numLinha);
            bpl.setBplDataProcessamento(DateHelper.getSystemDatetime());

            if (novoStatus != null) {
                bpl.setSbpCodigo(novoStatus.getCodigo());
            }
            if (critica != null) {
                bpl.setBplCritica(critica);
            }

            AbstractEntityHome.update(bpl);

        } catch (FindException | UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }
}
