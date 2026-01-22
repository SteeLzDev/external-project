package com.zetra.econsig.service.folha;

import static com.zetra.econsig.helper.folha.HistoricoHelper.COMPLEMENTO_DEFAULT;
import static com.zetra.econsig.helper.folha.HistoricoHelper.TAMANHO_MSG_ERRO_DEFAULT;
import static com.zetra.econsig.helper.lote.LoteHelper.ALTERACAO;
import static com.zetra.econsig.helper.lote.LoteHelper.CONFIRMACAO;
import static com.zetra.econsig.helper.lote.LoteHelper.EXCLUSAO;
import static com.zetra.econsig.helper.lote.LoteHelper.INCLUSAO;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.parametros.AlterarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.ReservaMargemHelper;
import com.zetra.econsig.helper.lote.LoteHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacaoHome;
import com.zetra.econsig.persistence.entity.OcorrenciaParcelaHome;
import com.zetra.econsig.persistence.entity.ParcelaDesconto;
import com.zetra.econsig.persistence.entity.ParcelaDescontoHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.persistence.entity.ServidorHome;
import com.zetra.econsig.persistence.query.convenio.ListaConvenioAtivoHistoricoQuery;
import com.zetra.econsig.service.consignacao.AlterarConsignacaoController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.ConfirmarConsignacaoController;
import com.zetra.econsig.service.consignacao.LiquidarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignacao.ReservarMargemController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.sdp.DespesaIndividualController;
import com.zetra.econsig.service.sdp.PermissionarioController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ImportaHistoricoControllerBean</p>
 * <p>Description: Session Façade para Rotina de Importação de Arquivo de Histórico</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ImportaHistoricoControllerBean implements ImportaHistoricoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaHistoricoControllerBean.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    @Qualifier("reservarMargemController")
    private ReservarMargemController reservarController;

    @Autowired
    private AlterarConsignacaoController alterarConsignacaoController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private DespesaIndividualController despesaIndividualController;

    @Autowired
    private ImpRetornoController impRetornoController;

    @Autowired
    private PermissionarioController permissionarioController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private ConfirmarConsignacaoController confirmarConsignacaoController;

    @Autowired
    private LiquidarConsignacaoController liquidarConsignacaoController;

    @Autowired
    private ServidorController servidorController;


    /**
     * Importa linha a linha
     * @param entrada
     * @param adeInsereParcelas
     * @param critica
     * @param linha
     * @param serDelegate
     * @param cnvDelegate
     * @param parDelegate
     * @param csgDelegete
     * @param adeDelegate
     * @param retDelegate
     * @param desDelegate
     * @param atualizacaoContratoStat
     * @param cseCodigo
     * @param hoje
     * @param validaReserva
     * @param permitirValidacaoTaxa
     * @param serAtivo
     * @param cnvAtivo
     * @param svcAtivo
     * @param serCnvAtivo
     * @param csaAtivo
     * @param orgAtivo
     * @param estAtivo
     * @param cseAtivo
     * @param importacaoSemProcessamento
     * @param selecionaPrimeiroCnvDisponivel - se true, escolhe o primeiro CNV disponível se retornar mais de um
     * @param cacheConvenio
     * @param paramAvancados
     * @param cachePlanos  Se nulo, SDP não esta habilitado, se não nulo, SDP esta habilitado
     * @param responsavel
     */
    @Override
    public void importaLinha(Map<String, Object> entrada, List<Map<String, Object>> adeInsereParcelas, List<String> critica, String linha,
                                      String cseCodigo, String hoje,
                                      boolean validaReserva, boolean permitirValidacaoTaxa,
                                      boolean serAtivo, boolean cnvAtivo, boolean svcAtivo, boolean serCnvAtivo,
                                      boolean csaAtivo, boolean orgAtivo, boolean estAtivo, boolean cseAtivo,
                                      boolean importaDadosAde, boolean retornaAdeNum,
                                      boolean importacaoSemProcessamento, boolean selecionaPrimeiroCnvDisponivel, Map<String, Map<String, Object>> cacheConvenio,
                                      ReservarMargemParametros paramAvancados, HashMap<String, TransferObject> cachePlanos, AcessoSistema responsavel) {

        String rseMatricula = (String) entrada.get("RSE_MATRICULA");
        String serCpf = (String) entrada.get("SER_CPF");
        String cnvCodVerba = (String) entrada.get("CNV_COD_VERBA");
        String cnvCodVerbaRef = (String) entrada.get("CNV_COD_VERBA_REF");
        String estIdentificador = (String) entrada.get("EST_IDENTIFICADOR");
        String orgIdentificador = (String) entrada.get("ORG_IDENTIFICADOR");
        String svcIdentificador = (String) entrada.get("SVC_IDENTIFICADOR");
        String csaIdentificador = (String) entrada.get("CSA_IDENTIFICADOR");
        String codReg = entrada.get("COD_REG") != null && !entrada.get("COD_REG").toString().equals("") ? entrada.get("COD_REG").toString() : "";

        String tmoCodigo = (paramAvancados != null ? paramAvancados.getTmoCodigo() : null);
        String ocaObs = (paramAvancados != null ? paramAvancados.getOcaObs() : null);

        boolean sdp = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_SDP, CodedValues.TPC_SIM, responsavel);
        boolean despIndividual = (sdp && entrada.get("DESP_INDIVIDUAL") != null && entrada.get("DESP_INDIVIDUAL").toString().equalsIgnoreCase("S"));
        String plaIdentificador = (String) entrada.get("PLA_IDENTIFICADOR");
        TransferObject plano = null;

        // Se SDP habilitado (cachePlanos != null), verifica PLA_IDENTIFICADOR
        if (despIndividual) {
            if (TextHelper.isNull(csaIdentificador)) {
                critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.erro.identificador.csa.necessario", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                return;
            }

            String chavePlano = csaIdentificador.trim() + ";" + plaIdentificador.trim();
            if (TextHelper.isNull(plaIdentificador) || !cachePlanos.containsKey(chavePlano)) {
                critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.erro.identificador.pla.invalido", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                return;
            } else {
                plano = cachePlanos.get(chavePlano);
            }
        }

        // Verifica valores obrigatórios
        if ((TextHelper.isNull(cnvCodVerba) && TextHelper.isNull(cnvCodVerbaRef)) || (TextHelper.isNull(rseMatricula) && TextHelper.isNull(serCpf))) {
            critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.impHistorico.critica.valores.requeridos.ausentes", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
            return;
        }

        // Faz a pesquisa de servidor
        TransferObject servidor = null;
        List<TransferObject> servidores = new ArrayList<>();
        List<TransferObject> candidatos = null;
        int excluidos = 0;

        try {
            candidatos = pesquisarServidorController.pesquisaServidorExato("CSE", cseCodigo, estIdentificador, orgIdentificador, rseMatricula, serCpf, responsavel);
        } catch (ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            critica.add(linha + formataMsgErro(ex.getMessage(), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
            return;
        }

        if (serAtivo) {
            // Verifica se os servidores candidatos não estão excluidos
            if (candidatos.size() > 0) {
                Iterator<TransferObject> it = candidatos.iterator();
                while (it.hasNext()) {
                    servidor = it.next();
                    if (!CodedValues.SRS_INATIVOS.contains(servidor.getAttribute(Columns.SRS_CODIGO))) {
                        servidores.add(servidor);
                    } else {
                        excluidos++;
                    }
                }
            }
        } else {
            servidores.addAll(candidatos);
        }

        if (servidores.size() == 0) {
            if (excluidos > 0) {
                critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.servidorExcluido", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                return;
            } else {
                critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                return;
            }
        } else if (servidores.size() > 1) {
            critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.multiplo", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
            return;
        } else {
            servidor = servidores.get(0);
            String orgCodigo = servidor.getAttribute(Columns.ORG_CODIGO).toString();
            String rseCodigo = servidor.getAttribute(Columns.RSE_CODIGO).toString();
            String serCodigo = servidor.getAttribute(Columns.SER_CODIGO).toString();

            // Determina se o convênio está no cache
            String chave = "[" + cnvCodVerba + "," + orgCodigo + "," + csaIdentificador + "," + svcIdentificador + "," + cnvCodVerbaRef + "]";
            Map<String, Object> valoresConvenio = null;
            if (cacheConvenio.get(chave) != null) {
                valoresConvenio = cacheConvenio.get(chave);
            } else {
                try {
                    valoresConvenio = buscaConvenio(rseCodigo, entrada, cnvCodVerba, cnvCodVerbaRef, orgCodigo,
                                                    csaIdentificador, svcIdentificador, serCodigo,
                                                    cnvAtivo, csaAtivo, svcAtivo,
                                                    orgAtivo, estAtivo, cseAtivo,
                                                    importacaoSemProcessamento, selecionaPrimeiroCnvDisponivel, responsavel);
                    // Salva o cache de convênio.
                    cacheConvenio.put(chave, valoresConvenio);
                } catch (ConvenioControllerException | ParametroControllerException | AutorizacaoControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    critica.add(linha + formataMsgErro(ex.getMessage(), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    return;
                }
            }

            // SDP
            String plaCodigo = null;
            String prmCodigo = null;
            TransferObject permissionario = null;
            if (despIndividual) {
                try {
                    TransferObject criterio = new CustomTransferObject();
                    criterio.setAttribute(Columns.PRM_ATIVO, CodedValues.STS_ATIVO);
                    criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
                    criterio.setAttribute(Columns.SER_CPF, serCpf);
                    String csaCodigo = ((TransferObject) valoresConvenio.get("paramConvenio")).getAttribute(Columns.CNV_CSA_CODIGO).toString();
                    criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);
                    List<TransferObject> permissionarios = permissionarioController.lstPermissionarios(criterio, -1, -1, responsavel);
                    if (permissionarios.size() == 1) {
                        permissionario = permissionarios.get(0);
                        if (permissionario != null) {
                            if (!((String)plano.getAttribute(Columns.CSA_CODIGO)).equals(permissionario.getAttribute(Columns.CSA_CODIGO))) {
                                critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.erro.endereco.permissionario.nao.pertence.csa", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                                return;
                            } else {
                                plaCodigo = (String) plano.getAttribute(Columns.PLA_CODIGO);
                                prmCodigo = (String) permissionario.getAttribute(Columns.PRM_CODIGO);
                            }
                        }
                    } else {
                        critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.erro.obter.permissionario", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                        return;
                    }
                } catch (Exception e) {
                    critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.erro.obter.permissionario", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    return;
                }
            }


            String cnvCodigo = (String) valoresConvenio.get("cnvCodigo");
            CustomTransferObject paramConvenio = (CustomTransferObject) valoresConvenio.get("paramConvenio");
            ParamSvcTO paramSvcCse = (ParamSvcTO) valoresConvenio.get("paramSvcCse");
            String msgErroConvenio = (String) valoresConvenio.get("msgErroConvenio");
            csaAtivo = ((Boolean) valoresConvenio.get("csaAtivo") != null ? ((Boolean) valoresConvenio.get("csaAtivo")).booleanValue() : csaAtivo);

            if (msgErroConvenio != null) {
                critica.add(linha + msgErroConvenio);
                return;
            }

            String operacao = entrada.get("OPERACAO") != null ? entrada.get("OPERACAO").toString() : INCLUSAO; // I, A, E, C
            String adeVlr = (entrada.get("ADE_VLR") != null && !entrada.get("ADE_VLR").toString().equals("")) ? entrada.get("ADE_VLR").toString() : null;
            String adeVlrFolha = (entrada.get("ADE_VLR_FOLHA") != null && !entrada.get("ADE_VLR_FOLHA").toString().equals("")) ? entrada.get("ADE_VLR_FOLHA").toString() : null;
            String adeVlrBusca = (entrada.get("ADE_VLR_VERIFICAR") != null && !entrada.get("ADE_VLR_VERIFICAR").toString().equals("")) ? entrada.get("ADE_VLR_VERIFICAR").toString() : null;
            String adeVlrParcelaFolha = (entrada.get("ADE_VLR_PARCELA_FOLHA") != null && !entrada.get("ADE_VLR_PARCELA_FOLHA").toString().equals("")) ? entrada.get("ADE_VLR_PARCELA_FOLHA").toString() : null;
            String adePrazo = (entrada.get("ADE_PRAZO") != null && !entrada.get("ADE_PRAZO").toString().equals("")) ? entrada.get("ADE_PRAZO").toString() : null;
            String adePrazoRef = (entrada.get("ADE_PRAZO_REF") != null && !entrada.get("ADE_PRAZO_REF").toString().equals("")) ? entrada.get("ADE_PRAZO_REF").toString() : null;
            String adeCarencia = (entrada.get("ADE_CARENCIA") != null && !entrada.get("ADE_CARENCIA").toString().equals("")) ? entrada.get("ADE_CARENCIA").toString() : "0";
            String adeNumero = (String) entrada.get("ADE_NUMERO");
            String adeIdentificador = (entrada.get("ADE_IDENTIFICADOR") != null && !entrada.get("ADE_IDENTIFICADOR").equals("")) ? entrada.get("ADE_IDENTIFICADOR").toString() : "IMPORTADO " + hoje;
            String adeIdentificadorBusca = (String) entrada.get("ADE_IDENTIFICADOR_VERIFICAR");
            String situacao = (String) entrada.get("SITUACAO"); // Resultado da operação I (Indeferida), D (Deferida), Q (Quitação)
            String spdCodigo = (String) entrada.get("SPD_CODIGO");
            String ocpObs = (String) entrada.get("OCP_OBS");
            String adePeriodicidade = (String) entrada.get("ADE_PERIODICIDADE");

            if (TextHelper.isNull(spdCodigo)) {
                spdCodigo = (situacao != null && situacao.toString().equals(INCLUSAO)) ? CodedValues.SPD_REJEITADAFOLHA : CodedValues.SPD_LIQUIDADAFOLHA;
            }

            // Data de inclusão do contrato
            java.util.Date adeData = null;
            if (!TextHelper.isNull(entrada.get("ADE_DATA"))) {
                try {
                    adeData = DateHelper.parse(entrada.get("ADE_DATA").toString(), "yyyy-MM-dd HH:mm:ss");
                } catch (ParseException ex) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.parse.data.inclusao.arg0", responsavel, ex.getMessage()));
                }
            }

            // Data de inclusão do contrato Ref.
            java.util.Date adeDataRef = null;
            if (!TextHelper.isNull(entrada.get("ADE_DATA_REF"))) {
                try {
                    adeDataRef = DateHelper.parse(entrada.get("ADE_DATA_REF").toString(), "yyyy-MM-dd HH:mm:ss");
                } catch (ParseException ex) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.parse.data.inclusao.ref.arg0", responsavel, ex.getMessage()));
                }
            }

            // Cadastro Data de Ocorrência
            Timestamp adeDtHrOcorrencia = null;
            if (!TextHelper.isNull(entrada.get("ADE_DATA_HORA_OCORRENCIA"))) {
                try {
                    adeDtHrOcorrencia = new Timestamp(DateHelper.parse(entrada.get("ADE_DATA_HORA_OCORRENCIA").toString(), "yyyy-MM-dd HH:mm:ss").getTime());
                } catch (ParseException ex) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.parse.data.hora.ocorrencia.arg0", responsavel, ex.getMessage()));
                }
            }

            // Cadastro de informações financeiras : TAC
            BigDecimal adeVlrTac = null;
            if (!TextHelper.isNull(entrada.get("ADE_VLR_TAC"))) {
                adeVlrTac = new BigDecimal(entrada.get("ADE_VLR_TAC").toString());
            }

            // Cadastro de informações financeiras : IOF
            BigDecimal adeVlrIof = null;
            if (!TextHelper.isNull(entrada.get("ADE_VLR_IOF"))) {
                adeVlrIof = new BigDecimal(entrada.get("ADE_VLR_IOF").toString());
            }

            // Cadastro de informações financeiras : Valor Liberado
            BigDecimal adeVlrLiquido = null;
            if (!TextHelper.isNull(entrada.get("ADE_VLR_LIQUIDO"))) {
                adeVlrLiquido = new BigDecimal(entrada.get("ADE_VLR_LIQUIDO").toString());
            }

            // Cadastro de informações financeiras : Valor Mensalidade Vinculada
            BigDecimal adeVlrMensVinc = null;
            if (!TextHelper.isNull(entrada.get("ADE_VLR_MENS_VINC"))) {
                adeVlrMensVinc = new BigDecimal(entrada.get("ADE_VLR_MENS_VINC").toString());
            }

            // Cadastro de informações financeiras : Valor Seguro Prestamista
            BigDecimal adeVlrSegPrestamista = null;
            if (!TextHelper.isNull(entrada.get("ADE_SEGURO_PRESTAMISTA"))) {
                adeVlrSegPrestamista = new BigDecimal(entrada.get("ADE_SEGURO_PRESTAMISTA").toString());
            }

            // Cadastro de informações financeiras : Taxa de Juros
            BigDecimal adeTaxaJuros = null;
            if (!TextHelper.isNull(entrada.get("ADE_TAXA_JUROS"))) {
                adeTaxaJuros = new BigDecimal(entrada.get("ADE_TAXA_JUROS").toString());
            }

            // Cadastro de índice
            String adeIndice = (!TextHelper.isNull(entrada.get("ADE_INDICE"))) ? entrada.get("ADE_INDICE").toString() : null;
            String adeIndiceExp = (!TextHelper.isNull(entrada.get("ADE_INDICE_EXP"))) ? entrada.get("ADE_INDICE_EXP").toString() : null;

            // Data inicial de desconto
            Date anoMesIni = null;
            try {
                anoMesIni = (!TextHelper.isNull(entrada.get("ADE_ANO_MES_INI"))
                        ? DateHelper.parse(entrada.get("ADE_ANO_MES_INI").toString(), "yyyy-MM-dd") : null);
            } catch (ParseException ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.parse.data.inicio.arg0", responsavel, ex.getMessage()));
            }

            // Data final de desconto
            Date anoMesFim = null;
            try {
                anoMesFim = (!TextHelper.isNull(entrada.get("ADE_ANO_MES_FIM")) && !entrada.get("ADE_ANO_MES_FIM").equals("2999-12-01")
                        ? DateHelper.parse(entrada.get("ADE_ANO_MES_FIM").toString(), "yyyy-MM-dd") : null);
            } catch (ParseException ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.parse.data.fim.arg0", responsavel, ex.getMessage()));
            }

            // Data inicial de referência
            java.sql.Date adeAnoMesIniRef = null;
            try {
                adeAnoMesIniRef = (!TextHelper.isNull(entrada.get("ADE_ANO_MES_INI_REF"))
                        ? DateHelper.toSQLDate(DateHelper.parse(entrada.get("ADE_ANO_MES_INI_REF").toString(), "yyyy-MM-dd")) : null);
            } catch (ParseException ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.parse.data.inicio.referencia.arg0", responsavel, ex.getMessage()));
            }
            // Se nula, seta para a data ini
            adeAnoMesIniRef = (adeAnoMesIniRef == null && anoMesIni != null) ? DateHelper.toSQLDate(anoMesIni) : adeAnoMesIniRef;

            // Data final de referência
            java.sql.Date adeAnoMesFimRef = null;
            try {
                adeAnoMesFimRef = (!TextHelper.isNull(entrada.get("ADE_ANO_MES_FIM_REF")) && !entrada.get("ADE_ANO_MES_FIM_REF").equals("2999-12-01")
                        ? DateHelper.toSQLDate(DateHelper.parse(entrada.get("ADE_ANO_MES_FIM_REF").toString(), "yyyy-MM-dd")) : null);
            } catch (ParseException ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.parse.data.fim.referencia.arg0", responsavel, ex.getMessage()));
            }
            // Se nula, seta para a data fim
            adeAnoMesFimRef = (adeAnoMesFimRef == null && anoMesFim != null) ? DateHelper.toSQLDate(anoMesFim) : adeAnoMesFimRef;

            // Data inicial folha
            java.sql.Date adeAnoMesIniFolha = null;
            try {
                adeAnoMesIniFolha = (!TextHelper.isNull(entrada.get("ADE_ANO_MES_INI_FOLHA"))
                        ? DateHelper.toSQLDate(DateHelper.parse(entrada.get("ADE_ANO_MES_INI_FOLHA").toString(), "yyyy-MM-dd")) : null);
            } catch (ParseException ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.parse.data.inicio.referencia.arg0", responsavel, ex.getMessage()));
            }
            // Se nula, seta para a data ini
            adeAnoMesIniFolha = (adeAnoMesIniFolha == null && anoMesIni != null) ? DateHelper.toSQLDate(anoMesIni) : adeAnoMesIniFolha;

            // Data final folha
            java.sql.Date adeAnoMesFimFolha = null;
            try {
                adeAnoMesFimFolha = (!TextHelper.isNull(entrada.get("ADE_ANO_MES_FIM_FOLHA")) && !entrada.get("ADE_ANO_MES_FIM_FOLHA").equals("2999-12-01")
                        ? DateHelper.toSQLDate(DateHelper.parse(entrada.get("ADE_ANO_MES_FIM_FOLHA").toString(), "yyyy-MM-dd")) : null);
            } catch (ParseException ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.parse.data.fim.referencia.arg0", responsavel, ex.getMessage()));
            }
            // Se nula, seta para a data fim
            adeAnoMesFimFolha = (adeAnoMesFimFolha == null && anoMesFim != null) ? DateHelper.toSQLDate(anoMesFim) : adeAnoMesFimFolha;

            // ADE Número
            Long intAdeNumero = null;
            if (TextHelper.isNum(adeNumero)) {
                intAdeNumero = Long.valueOf(adeNumero);
            }

            // Prazo do Contrato
            Integer intAdePrazo = null;
            try {
                // "0" (zero) no maxPrazo significa que o serviço é indeterminado.
                if ("0".equals(paramSvcCse.getTpsMaxPrazo())) {
                    // Zera o campo anoMesFim para evitar que seja calculado o adePrazo posteriormente.
                    anoMesFim = null;
                } else {
                    intAdePrazo = (adePrazo != null && Integer.parseInt(adePrazo) > 0) ? Integer.valueOf(adePrazo) : null;
                }
            } catch (NumberFormatException ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.parse.numero.parcelas.arg0", responsavel, ex.getMessage()));
                critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.impHistorico.critica.prazo.informado.formato.incorreto", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                return;
            }

            // Prazo do Contrato de Ref.
            Integer intAdePrazoRef = null;
            try {
                intAdePrazoRef = (adePrazoRef != null && Integer.parseInt(adePrazoRef) > 0) ? Integer.valueOf(adePrazoRef) : null;
            } catch (NumberFormatException ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.parse.numero.parcelas.ref.arg0", responsavel, ex.getMessage()));
                critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.impHistorico.critica.prazo.ref.informado.formato.incorreto", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                return;
            }

            // Parcelas pagas
            int adePrdPagas = 0;
            try {
                adePrdPagas = (!TextHelper.isNull(entrada.get("ADE_PRD_PAGAS")) ? Integer.parseInt(entrada.get("ADE_PRD_PAGAS").toString()) : 0);
            } catch (NumberFormatException ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.parse.numero.parcelas.pagas.arg0", responsavel, ex.getMessage()));
                critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.impHistorico.critica.numero.parcelas.pagas.formato.incorreto", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                return;
            }

            // Verifica se a quantidade de pagas é maior que o prazo
            if (adePrdPagas > 0 && intAdePrazo != null && adePrdPagas > intAdePrazo) {
                critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.impHistorico.critica.numero.parcelas.pagas.maior.prazo", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                return;
            }

            // Verifica se o valor da parcela foi informado
            if (TextHelper.isNull(adeVlr)) {
                critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.impHistorico.critica.valor.parcela.deve.ser.informado", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                return;
            }

            // Verifica se o valor da parcela não e zero ou negativo
            if (Double.parseDouble(adeVlr) <= 0) {
                critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.impHistorico.critica.valor.parcela.zero.ou.negativo", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                return;
            }

            BigDecimal decimalAdeVlr = new BigDecimal(adeVlr);
            BigDecimal decimalAdeVlrFolha = null;
            if (!TextHelper.isNull(adeVlrFolha)) {
                if (TextHelper.isDecimalNum(adeVlrFolha)) {
                    decimalAdeVlrFolha = new BigDecimal(adeVlrFolha);
                } else {
                    LOG.warn("Campo ADE_VLR_FOLHA no formato incorreto.");
                    critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.impHistorico.critica.valor.folha.informado.formato.incorreto", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    return;
                }
            }
            if (decimalAdeVlrFolha == null) {
                decimalAdeVlrFolha = decimalAdeVlr;
            }

            BigDecimal decimalAdeVlrParcelaFolha = null;
            if (!TextHelper.isNull(adeVlrParcelaFolha)) {
                if (TextHelper.isDecimalNum(adeVlrParcelaFolha)) {
                    decimalAdeVlrParcelaFolha = new BigDecimal(adeVlrParcelaFolha);
                } else {
                    LOG.warn("Campo ADE_VLR_PARCELA_FOLHA no formato incorreto.");
                    critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.impHistorico.critica.valor.folha.informado.formato.incorreto", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    return;
                }
            }
            /**
             * DESENV-17988
             * ATENÇÃO: O CAMPO ADE_VLR_PARCELA_FOLHA NÃO DEVE SER INICIALIZADO COM O ADE_VLR, POIS ESTE CAMPO
             * É USADO EM ALTERAÇÕES AVANÇADAS QUANDO O VALOR DA PARCELA É REDUZIDO PORÉM O CONSUMO DA MARGEM
             * SE MANTÉM. ESTE VALOR SOBREPÕE O ADE_VLR AO CRIAR PARCELAS, PORTANTO NÃO DEVE SER INICIALIZADO
             * POR PADRÃO, SOMENTE SE O ARQUIVO DE HISTÓRICO ENVIAR O CAMPO.
             *
            if (decimalAdeVlrParcelaFolha == null) {
                decimalAdeVlrParcelaFolha = decimalAdeVlr;
            }
            */

            // Inclusão de contrato
            if (operacao.equals(INCLUSAO)) {
                if (paramConvenio == null) {
                    critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.erro.convenio.nao.encontrado", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    return;
                } else {
                    try {
                        if (importacaoSemProcessamento) {
                            // Calcula datas inicial e final, prazo e qtde de parcelas pagas.
                            Date periodoAtual = null;
                            try {
                                periodoAtual = impRetornoController.getUltimoPeriodoRetorno(orgCodigo, null, responsavel);
                            } catch (Exception ex) {
                                critica.add(linha + formataMsgErro(ex.getMessage(), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                                return;
                            }

                            if (anoMesIni == null) {
                                anoMesIni = periodoAtual;
                            }

                            // Calcula qtde de parcelas pagas
                            if (TextHelper.isNull(entrada.get("ADE_PRD_PAGAS"))) {
                                adePrdPagas = PeriodoHelper.getInstance().calcularPrazo(orgCodigo, anoMesIni, periodoAtual, null, responsavel);
                            }

                            if (anoMesFim != null && intAdePrazo == null) {
                                intAdePrazo = PeriodoHelper.getInstance().calcularPrazo(orgCodigo, anoMesIni, anoMesFim, null, responsavel);
                            }
                        }

                        // Valida as configurações do convênio e insere a reserva
                        Short incMargem  = paramSvcCse.getTpsIncideMargem();
                        Short intFolha   = paramSvcCse.getTpsIntegraFolha();
                        String tipoVlr   = paramSvcCse.getTpsTipoVlr();

                        String adeCodigo = null;

                        int adeCarenciaInt = parametroController.calcularAdeCarenciaDiaCorteCsa(Integer.valueOf(adeCarencia), ((TransferObject) valoresConvenio.get("paramConvenio")).getAttribute(Columns.CNV_CSA_CODIGO).toString(), orgCodigo, responsavel);

                        // Monta o objeto de parâmetro da reserva
                        ReservarMargemParametros reservaParam = new ReservarMargemParametros();

                        reservaParam.setRseCodigo(rseCodigo);
                        reservaParam.setAdeVlr(decimalAdeVlr);
                        reservaParam.setAdePrazo(intAdePrazo);
                        reservaParam.setAdeAnoMesIni(anoMesIni);
                        reservaParam.setAdeAnoMesFim(anoMesFim);
                        reservaParam.setAdeCarencia(adeCarenciaInt);
                        reservaParam.setAdeIdentificador(adeIdentificador);
                        reservaParam.setCnvCodigo(cnvCodigo);
                        reservaParam.setSadCodigo(CodedValues.SAD_DEFERIDA);
                        reservaParam.setSerSenha(null);
                        reservaParam.setComSerSenha(Boolean.FALSE);
                        reservaParam.setAdeTipoVlr(tipoVlr);
                        reservaParam.setAdeIntFolha(intFolha);
                        /*
                         * ATUALIZA O INDICE POSTERIORMENTE PARA PODER IGNORAR
                         * O PARAMETRO QUE BLOQUEIA REPETIÇÃO DE INDICE
                        reservaParam.setAdeIndice(adeIndice);
                        */
                        if (sdp) { // Para módulo SDP o índice diferencia o plano dentro do serviço.
                            reservaParam.setAdeIndice(adeIndice);
                        }
                        reservaParam.setAdeVlrTac(adeVlrTac);
                        reservaParam.setAdeVlrIof(adeVlrIof);
                        reservaParam.setAdeVlrLiquido(adeVlrLiquido);
                        reservaParam.setAdeVlrMensVinc(adeVlrMensVinc);
                        reservaParam.setValidar(Boolean.FALSE);
                        reservaParam.setValidaAnexo(Boolean.FALSE);
                        reservaParam.setPermitirValidacaoTaxa(Boolean.valueOf(permitirValidacaoTaxa));
                        reservaParam.setAdeAnoMesIniRef(adeAnoMesIniRef);
                        reservaParam.setAdeAnoMesFimRef(adeAnoMesFimRef);
                        reservaParam.setAcao("RESERVAR");
                        reservaParam.setAdeTaxaJuros(adeTaxaJuros);
                        reservaParam.setAdeVlrSegPrestamista(adeVlrSegPrestamista);
                        reservaParam.setAdeDtHrOcorrencia(adeDtHrOcorrencia);
                        reservaParam.setAdePeriodicidade(adePeriodicidade);
                        if (!TextHelper.isNull(codReg)) {
                            reservaParam.setAdeCodReg(codReg);
                        }

                        // opções de inclusão avançada
                        if (paramAvancados != null) {
                            reservaParam.setValidaMargem(paramAvancados.getValidaMargem());
                            reservaParam.setValidaTaxaJuros(paramAvancados.getValidaTaxaJuros());
                            reservaParam.setValidaPrazo(paramAvancados.getValidaPrazo());
                            reservaParam.setValidaDadosBancarios(paramAvancados.getValidaDadosBancarios());
                            reservaParam.setValidaSenhaServidor(paramAvancados.getValidaSenhaServidor());
                            reservaParam.setValidaDataNascimento(paramAvancados.getValidaDataNascimento());
                            reservaParam.setValidaLimiteAde(paramAvancados.getValidaLimiteAde());
                            reservaParam.setTmoCodigo(paramAvancados.getTmoCodigo());
                            reservaParam.setOcaObs(paramAvancados.getOcaObs());
                        }

                        if (validaReserva) {
                            // Se valida a reserva, então chama função específica e
                            // insere a reserva com o incide margem do convenio
                            CustomTransferObject reserva = new CustomTransferObject();
                            reserva.setAttribute("ADE_PRAZO", intAdePrazo);
                            reserva.setAttribute("ADE_PERIODICIDADE", adePeriodicidade);
                            reserva.setAttribute("ADE_CARENCIA", adeCarenciaInt);
                            reserva.setAttribute("RSE_PRAZO", servidor.getAttribute(Columns.RSE_PRAZO));
                            reserva.setAttribute("ADE_VLR", adeVlr);
                            reserva.setAttribute("RSE_CODIGO", rseCodigo);
                            reserva.setAttribute("CSE_CODIGO", cseCodigo);

                            if (paramAvancados != null && !paramAvancados.getValidaMargem()) {
                                reserva.setAttribute(CodedValues.PARAM_INC_AVANCADA_VALIDA_MARGEM, Boolean.FALSE);
                            }

                            if (paramAvancados != null && !paramAvancados.getValidaPrazo()) {
                                reserva.setAttribute(CodedValues.PARAM_INC_AVANCADA_VALIDA_PRAZO, Boolean.FALSE);
                            }

                            ReservaMargemHelper.validaReserva(paramConvenio, reserva, responsavel, true, true, serAtivo);

                            reservaParam.setAdeIncMargem(incMargem);
                            reservaParam.setSerAtivo(Boolean.TRUE);
                            reservaParam.setCnvAtivo(Boolean.TRUE);
                            reservaParam.setSerCnvAtivo(Boolean.TRUE);
                            reservaParam.setSvcAtivo(Boolean.TRUE);
                            reservaParam.setCsaAtivo(Boolean.TRUE);
                            reservaParam.setOrgAtivo(Boolean.TRUE);
                            reservaParam.setEstAtivo(Boolean.TRUE);
                            reservaParam.setCseAtivo(Boolean.TRUE);

                        } else {
                            // Se não é para validar a reserva, insere o contrato com incide margem
                            // igual a NAO e depois altera para o valor correto do convenio
                            reservaParam.setAdeIncMargem(CodedValues.INCIDE_MARGEM_NAO);
                            reservaParam.setSerAtivo(Boolean.valueOf(serAtivo));
                            reservaParam.setCnvAtivo(Boolean.valueOf(cnvAtivo));
                            reservaParam.setSerCnvAtivo(Boolean.valueOf(serCnvAtivo));
                            reservaParam.setSvcAtivo(Boolean.valueOf(svcAtivo));
                            reservaParam.setCsaAtivo(Boolean.valueOf(csaAtivo));
                            reservaParam.setOrgAtivo(Boolean.valueOf(orgAtivo));
                            reservaParam.setEstAtivo(Boolean.valueOf(estAtivo));
                            reservaParam.setCseAtivo(Boolean.valueOf(cseAtivo));
                        }

                        if (despIndividual) {
                            TransferObject despesaIndividual = new CustomTransferObject();
                            despesaIndividual.setAttribute(Columns.DEI_PLA_CODIGO, plaCodigo);
                            despesaIndividual.setAttribute(Columns.DEI_PRM_CODIGO, prmCodigo);
                            adeCodigo = despesaIndividualController.createDespesaIndividual(despesaIndividual, reservaParam, responsavel);
                        } else {
                            adeCodigo = reservarController.reservarMargem(reservaParam, responsavel);
                        }

                        // Cria ocorrencia de importação
                        autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.impHistorico.ocorrencia.ade.importacao.contratos", responsavel), responsavel);

                        // Importa os dados complementares de autorização
                        if (importaDadosAde) {
                            importarDadosAutorizacao(adeCodigo, entrada, responsavel);
                        }

                        // Realiza alterações pós-reserva de margem
                        String sadCodigo = CodedValues.SAD_DEFERIDA;
                        if (!TextHelper.isNull(entrada.get("SAD_CODIGO"))) {
                            sadCodigo = entrada.get("SAD_CODIGO").toString();
                        } else {
                            sadCodigo = (intAdePrazo != null && adePrdPagas == intAdePrazo.intValue()) ? CodedValues.SAD_CONCLUIDO :
                                                   ((adePrdPagas == 0) ? CodedValues.SAD_DEFERIDA : CodedValues.SAD_EMANDAMENTO);
                        }

                        try {
                            AutDescontoHome.updateImpHistorico(adeCodigo, sadCodigo, intAdeNumero, spdCodigo.equals(CodedValues.SPD_REJEITADAFOLHA) ? 0 : adePrdPagas,
                                    intAdePrazoRef, incMargem, adeData, adeDataRef, adeAnoMesIniFolha, adeAnoMesFimFolha, decimalAdeVlrFolha, decimalAdeVlrParcelaFolha, adeIndice, adeIndiceExp);

                        } catch (UpdateException ex) {
                            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.executar.atualizacao.contrato.pos.reserva.arg0", responsavel, ex.getMessage()));
                            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                            critica.add(linha + formataMsgErro(ex.getMessage(), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                            return;
                        }

                        // Se o contrato não foi criado como Deferido ou Em andamento, inclui ocorrências específicas dos eventos
                        if (!sadCodigo.equals(CodedValues.SAD_DEFERIDA) && !sadCodigo.equals(CodedValues.SAD_EMANDAMENTO)) {
                            // Cria ocorrência de mudança de status
                            autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.impHistorico.ocorrencia.ade.situacao.alterada.de.5.para.arg0", responsavel, sadCodigo), responsavel);

                            // Cria ocorrência especifica pelo status do contrato
                            if (sadCodigo.equals(CodedValues.SAD_LIQUIDADA)) {
                                autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_TARIF_LIQUIDACAO, ApplicationResourcesHelper.getMessage("mensagem.impHistorico.ocorrencia.ade.ptf.liquidacao.contrato", responsavel), responsavel);
                            } if (sadCodigo.equals(CodedValues.SAD_SUSPENSA_CSE)) {
                                autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_SUSPENSAO_CONTRATO, ApplicationResourcesHelper.getMessage("mensagem.impHistorico.ocorrencia.ade.consignacao.suspensa.pale.cse", responsavel), responsavel);
                            } if (sadCodigo.equals(CodedValues.SAD_SUSPENSA)) {
                                autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_SUSPENSAO_CONTRATO, ApplicationResourcesHelper.getMessage("mensagem.impHistorico.ocorrencia.ade.consignacao.suspensa", responsavel), responsavel);
                            }
                        }

                        LOG.debug("I: " + adeCodigo);

                        Map<String, Object> ade = new HashMap<>();
                        ade.put("adeCodigo", adeCodigo);
                        ade.put("adePrdPagas", Integer.valueOf(adePrdPagas));
                        ade.put("adeAnoMesIni", anoMesIni);
                        ade.put("adeVlr", adeVlr);
                        ade.put("adePeriodicidade", adePeriodicidade);
                        ade.put("spdCodigo", spdCodigo);
                        ade.put("orgCodigo", orgCodigo);
                        ade.put("ocpObs", ocpObs);
                        adeInsereParcelas.add(ade);

                        if (retornaAdeNum) {
                            try {
                                TransferObject adeTO = pesquisarConsignacaoController.findAutDesconto(adeCodigo, responsavel);
                                Long adeNumeroLinhaCritica = (Long) adeTO.getAttribute(Columns.ADE_NUMERO);
                                critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.impHistorico.critica.inserido.ade.numero.arg0", responsavel, adeNumeroLinhaCritica.toString()), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                            } catch (AutorizacaoControllerException ex) {
                                LOG.error(ex.getMessage(), ex);
                            }
                        }

                    } catch (ZetraException ex) {
                        LOG.error(ex.getMessage(), ex);
                        TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                        critica.add(linha + formataMsgErro(ex.getMessage(), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                        return;
                    } catch (NumberFormatException ex) {
                        LOG.error(ex.getMessage(), ex);
                        TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                        critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.impHistorico.critica.formato.entrada.dados.incorreto", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                        return;
                    }
                }
            } else if (operacao.equals(ALTERACAO) || operacao.equals(EXCLUSAO) || operacao.equals(CONFIRMACAO)) {
                List<String> sadCodigos = new ArrayList<>();
                if (operacao.equals(CONFIRMACAO)) {
                    sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
                } else if (operacao.equals(ALTERACAO)) {
                    sadCodigos.add(CodedValues.SAD_DEFERIDA);
                    sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
                } else if (operacao.equals(EXCLUSAO)) {
                    sadCodigos.add(CodedValues.SAD_DEFERIDA);
                    sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
                    sadCodigos.add(CodedValues.SAD_EMCARENCIA);
                    sadCodigos.add(CodedValues.SAD_ESTOQUE);
                    sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
                    sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
                    sadCodigos.add(CodedValues.SAD_SUSPENSA);
                    sadCodigos.add(CodedValues.SAD_SUSPENSA_CSE);
                    sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
                }

                // Pesquisa os contratos para a matricula informada
                List<TransferObject> ades = null;

                try {
                    CustomTransferObject criterio = new CustomTransferObject();
                    criterio.setAttribute(Columns.ADE_NUMERO, adeNumero);
                    criterio.setAttribute(Columns.ADE_IDENTIFICADOR, adeIdentificadorBusca);
                    criterio.setAttribute(Columns.ADE_INDICE, adeIndice);
                    criterio.setAttribute(Columns.ADE_ANO_MES_INI, DateHelper.format(anoMesIni, "yyyy-MM-dd"));
                    criterio.setAttribute(Columns.ADE_VLR, adeVlrBusca);
                    criterio.setAttribute(Columns.ADE_COD_REG, codReg);

                    ades = autorizacaoController.obtemConsignacaoPorCnvSerQuery(cnvCodigo, rseCodigo, sadCodigos, criterio, responsavel);
                } catch (AutorizacaoControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    critica.add(linha + formataMsgErro(ex.getMessage(), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    return;
                }

                if (ades.size() == 0) {
                    critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.nenhumaConsignacaoEncontrada", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    return;
                } else if (ades.size() > 1) {
                    critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.maisDeUmaConsignacaoEncontrada", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    return;
                } else {
                    TransferObject consig = ades.get(0);
                    String adeCodigo = consig.getAttribute(Columns.ADE_CODIGO).toString();

                    CustomTransferObject tipoMotivoOperacao = null;
                    if (!TextHelper.isNull(tmoCodigo)) {
                        tipoMotivoOperacao = new CustomTransferObject();
                        tipoMotivoOperacao.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                        tipoMotivoOperacao.setAttribute(Columns.TMO_CODIGO, tmoCodigo);
                        tipoMotivoOperacao.setAttribute(Columns.OCA_OBS, ocaObs);
                    }

                    try {
                        if (operacao.equals(ALTERACAO)) {
                            AlterarConsignacaoParametros alterarParam = new AlterarConsignacaoParametros(adeCodigo, decimalAdeVlr, intAdePrazo,
                                                                                                       adeIdentificador, adeIndice, adeVlrTac, adeVlrIof,
                                                                                                       adeVlrLiquido, adeVlrMensVinc, adeTaxaJuros,
                                                                                                       adeVlrSegPrestamista, serAtivo, false, cnvAtivo, svcAtivo,
                                                                                                       csaAtivo, orgAtivo, estAtivo, cseAtivo, null, null, null);
                            alterarParam.setTmoCodigo(tmoCodigo);
                            alterarParam.setOcaObs(ocaObs);
                            alterarParam.setAdePeriodicidade(adePeriodicidade);
                            alterarConsignacaoController.alterar(alterarParam, responsavel);

                            // Importa os dados complementares de autorização
                            if (importaDadosAde) {
                                importarDadosAutorizacao(adeCodigo, entrada, responsavel);
                            }

                            LOG.debug("A: " + adeCodigo);

                        } else if (operacao.equals(EXCLUSAO)) {
                            liquidarConsignacaoController.liquidar(adeCodigo, tipoMotivoOperacao, null, responsavel);
                            LOG.debug("E: " + adeCodigo);

                        } else if (operacao.equals(CONFIRMACAO)) {
                            confirmarConsignacaoController.confirmar(adeCodigo, tipoMotivoOperacao, responsavel);
                            LOG.debug("C: " + adeCodigo);
                        }
                    } catch (AutorizacaoControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                        TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                        critica.add(linha + formataMsgErro(ex.getMessage(), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                        return;
                    }
                }
            } else {
                critica.add(linha + formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.operacaoInvalida", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                return;
            }
        }
    }

    private Map<String, Object> buscaConvenio(String rseCodigo,
                                         Map<String, Object> entradaValida, String cnvCodVerba, String cnvCodVerbaRef, String orgCodigo,
                                         String csaIdentificador, String svcIdentificador, String serCodigo,
                                         boolean cnvAtivo, boolean csaAtivo, boolean svcAtivo,
                                         boolean orgAtivo, boolean estAtivo, boolean cseAtivo,
                                         boolean importacaoSemProcessamento, boolean selecionaPrimeiroCnvDisponivel, AcessoSistema responsavel)
            throws ConvenioControllerException, ParametroControllerException, AutorizacaoControllerException {
        String msgErroConvenio;
        Map<String, Object> valoresConvenio = new HashMap<>();

        // Busca o convênio
        List<TransferObject> conveniosCandidatos = convenioController.lstConvenios(cnvCodVerba, null, null, orgCodigo, cnvAtivo, responsavel);

        if (conveniosCandidatos == null || conveniosCandidatos.size() == 0) {
            msgErroConvenio = formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.nenhumConvenioEncontrado", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true);
            valoresConvenio.put("msgErroConvenio", msgErroConvenio);
            return valoresConvenio;
        }

        List<TransferObject> convenios = new ArrayList<>();
        if (conveniosCandidatos.size() > 1 && (csaIdentificador != null || svcIdentificador != null || cnvCodVerbaRef != null)) {
            // Se o id da CSA ou do SVC foi passado, navega nos convênios até encontrar o desejado
            Iterator<TransferObject> itCnv = conveniosCandidatos.iterator();
            TransferObject cnv = null;
            String csa = null, svc = null, verbaRef = null;
            while (itCnv.hasNext()) {
                cnv = itCnv.next();
                csa = (String) cnv.getAttribute(Columns.CSA_IDENTIFICADOR);
                svc = (String) cnv.getAttribute(Columns.SVC_IDENTIFICADOR);
                verbaRef = (String) cnv.getAttribute(Columns.CNV_COD_VERBA_REF);
                if (verbaRef != null && cnvCodVerbaRef != null && verbaRef.equals(cnvCodVerbaRef)) {
                    convenios.add(cnv);
                } else if (csa != null && csaIdentificador != null && csa.equals(csaIdentificador) &&
                    svc != null && svcIdentificador != null && svc.equals(svcIdentificador)) {
                    convenios.add(cnv);
                } else if (csa != null && csaIdentificador != null && csa.equals(csaIdentificador) && svcIdentificador == null) {
                    convenios.add(cnv);
                } else if (svc != null && svcIdentificador != null && svc.equals(svcIdentificador) && csaIdentificador == null) {
                    convenios.add(cnv);
                }
            }

            if (convenios == null || convenios.size() == 0) {
                msgErroConvenio = formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.nenhumConvenioEncontrado", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true);
                valoresConvenio.put("msgErroConvenio", msgErroConvenio);
                return valoresConvenio;
            } else if (convenios.size() > 1) {
                if (selecionaPrimeiroCnvDisponivel) {
                    convenios = new ArrayList<>();
                    convenios = filtraConveniosPermitidos(rseCodigo, entradaValida, responsavel, conveniosCandidatos);
                } else {
                    msgErroConvenio = formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.impHistorico.maisDeUmConvenioEncontrado", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true);
                    valoresConvenio.put("msgErroConvenio", msgErroConvenio);
                    return valoresConvenio;
                }
            }

        } else if (conveniosCandidatos.size() > 1) {
            if (selecionaPrimeiroCnvDisponivel) {
                convenios = new ArrayList<>();
                convenios.addAll(filtraConveniosPermitidos(rseCodigo, entradaValida, responsavel, conveniosCandidatos));
            } else {
                msgErroConvenio = formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.impHistorico.maisDeUmConvenioEncontrado", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true);
                valoresConvenio.put("msgErroConvenio", msgErroConvenio);
                return valoresConvenio;
            }
        } else if (conveniosCandidatos.size() == 1) {
            convenios.addAll(conveniosCandidatos);
        }

        TransferObject convenio = convenios.get(0);
        String cnvCodigo = convenio.getAttribute(Columns.CNV_CODIGO).toString();
        String svcCodigo = convenio.getAttribute(Columns.CNV_SVC_CODIGO).toString();
        String csaCodigo = convenio.getAttribute(Columns.CNV_CSA_CODIGO).toString();

        valoresConvenio.put("cnvCodigo", cnvCodigo);

        // Busca os parâmetros do convênio
        TransferObject paramConvenio = convenioController.getParamCnv(cnvCodigo, cnvAtivo, svcAtivo, responsavel);
        valoresConvenio.put("paramConvenio", paramConvenio);

        // Busca os parâmetros de serviço
        ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
        valoresConvenio.put("paramSvcCse", paramSvcCse);

        // Determina parâmetros de importação
        if (importacaoSemProcessamento) {
            // Busca o parâmetro de serviço que determina se importa ou não.
            boolean svcImporta = paramSvcCse.isTpsImportaContratosSemProcessamento();

            // Busca o parâmetro de consignatária que determina como avaliar os parâmetros de sistema e serviço
            List<TransferObject> paramCsa = parametroController.selectParamCsa(csaCodigo, CodedValues.TPA_IMPORTA_SEM_PROCESSAMENTO, responsavel);
            if (paramCsa != null && paramCsa.size() > 0) {
                String csaImporta = (String) (paramCsa.get(0)).getAttribute(Columns.PCS_VLR);

                if (csaImporta == null) {
                    // csaAtivo fica inalterado, pois é ele que determinará a importação.
                } else if (csaImporta.equals(CodedValues.IMP_IMPEDE_IMPORTACAO)) {
                    msgErroConvenio = formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.erro.csa.nao.permite.importacao", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true);
                    valoresConvenio.put("msgErroConvenio", msgErroConvenio);
                    return valoresConvenio;
                } else if (csaImporta.equals(CodedValues.IMP_FORCA_IMPORTACAO)) {
                    csaAtivo = false;
                } else if (csaImporta.equals(CodedValues.IMP_SVC_DETERMINA)) {
                    csaAtivo = false;
                    if (!svcImporta) {
                        msgErroConvenio = formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.erro.servico.nao.permite.importacao", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true);
                        valoresConvenio.put("msgErroConvenio", msgErroConvenio);
                        return valoresConvenio;
                    }
                } else {
                    msgErroConvenio = formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.impHistorico.erro.parametro.importacao.csa.invalido", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true);
                    valoresConvenio.put("msgErroConvenio", msgErroConvenio);
                    return valoresConvenio;
                }
            }
        }
        valoresConvenio.put("csaAtivo", Boolean.valueOf(csaAtivo));

        return valoresConvenio;
    }

    private List<TransferObject> filtraConveniosPermitidos(String rseCodigo, Map<String, Object> entradaValida, AcessoSistema responsavel, List<TransferObject> conveniosCandidatos) throws AutorizacaoControllerException {
        List<TransferObject> convenios = new ArrayList<>();
        List<Map<String, Object>> cnvRegisters = new ArrayList<>();
        for (TransferObject cnvTO: conveniosCandidatos) {
            Map<String, Object> cnvMap = cnvTO.getAtributos();
            Map<String, Object> mapAux = new HashMap<>();
            mapAux.putAll(cnvMap);
            mapAux.put(Columns.RSE_CODIGO, rseCodigo);
            mapAux.put(Columns.SVC_CODIGO, cnvMap.get(Columns.CNV_SVC_CODIGO));
            mapAux.put(Columns.ORG_CODIGO, cnvMap.get(Columns.CNV_ORG_CODIGO));
            mapAux.put(Columns.CSA_CODIGO, cnvMap.get(Columns.CNV_CSA_CODIGO));
            cnvRegisters.add(mapAux);
        }

        BigDecimal adeVlr = (entradaValida.get("ADE_VLR") != null && !entradaValida.get("ADE_VLR").toString().equals("")) ? new BigDecimal(entradaValida.get("ADE_VLR").toString()) : null;
        BigDecimal adeVlrLiquido = (entradaValida.get("ADE_VLR_LIQUIDO") != null && !entradaValida.get("ADE_VLR_LIQUIDO").toString().equals("")) ? new BigDecimal(entradaValida.get("ADE_VLR_LIQUIDO").toString()) : null;
        Integer adePrazo = (entradaValida.get("ADE_PRAZO") != null && !entradaValida.get("ADE_PRAZO").toString().equals("")) ? Integer.valueOf(entradaValida.get("ADE_PRAZO").toString()) : null;
        if (adePrazo != null) {
            entradaValida.replace("ADE_PRAZO", adePrazo);
        }
        Integer adeCarencia = (entradaValida.get("ADE_CARENCIA") != null && !entradaValida.get("ADE_CARENCIA").toString().equals("")) ? Integer.valueOf(entradaValida.get("ADE_CARENCIA").toString()) : Integer.valueOf("0");
        String adePeriodicidade = (String) entradaValida.get("ADE_PERIODICIDADE");

        LoteHelper loteHelper = new LoteHelper(null, null, false, true, true, false, false, null, responsavel);

        Map<String, Object> entradaAux = tiparEntrada(entradaValida);

        cnvRegisters = loteHelper.filtraReservasPermitidas(entradaAux, cnvRegisters, adeVlr, adeVlrLiquido, adePrazo, adeCarencia, adePeriodicidade);

        convenios.addAll(cnvRegisters.stream().map(cnvMap -> {TransferObject cnvTO = new CustomTransferObject(); cnvTO.setAtributos(cnvMap); return cnvTO;}).collect(Collectors.toList()));

        // ordena pela descrição do serviço
        Collections.sort(convenios, (o1, o2) -> ((String) o2.getAttribute(Columns.SVC_DESCRICAO)).compareTo((String) o1.getAttribute(Columns.SVC_DESCRICAO)));

        return convenios;
    }

    private Map<String, Object> tiparEntrada(Map<String, Object> entrada) throws AutorizacaoControllerException {
        Map<String, Object> retorno = new HashMap<>();
        retorno.putAll(entrada);

        /**
         * Valores que se estiverem no Map serão convertidos para data
         */
        String[][] vlrData = { { "PERIODO_FOLHA", "ADE_ANO_MES_INI", "ADE_ANO_MES_FIM", "ADE_ANO_MES_INI_REF", "ADE_ANO_MES_FIM_REF" },
                {ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.periodo.atual", (AcessoSistema) null),
            ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.data.inicial", (AcessoSistema) null),
            ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.data.final", (AcessoSistema) null),
            ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.data.inicial.referencia", (AcessoSistema) null),
            ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.data.final.referencia", (AcessoSistema) null)}
        };

        /**
         * Valores que se estiverem no Map serão convertidos para BigDecimal
         */
        String[][] vlrBigDecimal = { { "ADE_VLR_VERIFICAR", "ADE_VLR", "ADE_VLR_TAC", "ADE_VLR_IOF", "ADE_VLR_LIQUIDO", "ADE_VLR_MENS_VINC", "ADE_TAXA_JUROS" },
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
        String[][] vlrInteger = { { "ADE_CARENCIA", "ADE_PRAZO", "ADE_INC_MARGEM", "ADE_INT_FOLHA" },
                {ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.carencia", (AcessoSistema) null),
            ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.prazo", (AcessoSistema) null),
            ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.incide.margem", (AcessoSistema) null),
            ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.integra.folha", (AcessoSistema) null)}
        };

        for (int i = 0; i < vlrData[0].length; i++) {
            if (retorno.get(vlrData[0][i]) != null) {
                if (!retorno.get(vlrData[0][i]).toString().equals("")) {
                    try {
                        retorno.put(vlrData[0][i], DateHelper.toPeriodDate(DateHelper.parse(retorno.get(vlrData[0][i]).toString(), "yyyy-MM-dd")));
                    } catch (ParseException e) {
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
                if (!retorno.get(vlrInteger[0][i]).toString().equals("")) {
                    try {
                        retorno.put(vlrInteger[0][i], Integer.valueOf(retorno.get(vlrInteger[0][i]).toString()));
                    } catch (NumberFormatException e) {
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
                if (!retorno.get(vlrBigDecimal[0][i]).toString().equals("")) {
                    try {
                        BigDecimal vlr = new BigDecimal(retorno.get(vlrBigDecimal[0][i]).toString());
                        retorno.put(vlrBigDecimal[0][i], vlr);
                    } catch (Exception ex) {
                        LOG.debug("Erro de Parser -> " + vlrBigDecimal[0][i] + ": " + ex.getMessage());
                        throw new AutorizacaoControllerException("mensagem.erro.valor.informado.para.campo.invalido", (AcessoSistema) null, vlrBigDecimal[1][i]);
                    }
                } else {
                    retorno.remove(vlrBigDecimal[0][i]);
                }
            }
        }

        return retorno;
    }

    /**
     * Cria os dados de autorização desconto informados no arquivo de entrada, com campos
     * que possue o prefixo "DAD_VALOR_" no nome e o final é o código do tipo de dados
     * @param adeDelegate
     * @param adeCodigo
     * @param entrada
     * @param responsavel
     */
    private void importarDadosAutorizacao(String adeCodigo, Map<String, Object> entrada, AcessoSistema responsavel) {
        try {
            List<TransferObject> tdasTO = autorizacaoController.lstTodosTipoDadoAdicional(responsavel);

            if (tdasTO != null && tdasTO.size() > 0) {
                for (TransferObject tdaTo : tdasTO) {
                    String tdaCodigo = (String) tdaTo.getAttribute(Columns.TDA_CODIGO);
                    String dadValor = (String) entrada.get("DAD_VALOR_" + tdaCodigo);
                    if (!TextHelper.isNull(dadValor)) {
                        autorizacaoController.setDadoAutDesconto(adeCodigo, tdaCodigo, dadValor, responsavel);
                    }
                }
            }
        } catch (AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private static String formataMsgErro(String mensagem, String complemento, int tamanho, boolean alinhaEsquerda) {
        return TextHelper.removeAccent(TextHelper.formataMensagem(mensagem, complemento, tamanho, alinhaEsquerda)).toUpperCase();
    }

    /**
     * Gera histórico randômico, contendo total de "qtdRse" registros servidores (1 matricula por cpf)
     * e "qtdAde" de contratos. Se "qtdAde > qtdRse", mais de um contrato será incluído para cada matrícula.
     * Se "qtdAde < qtdRse", algumas matrículas ficarão sem contratos. O parâmetro "matriculaInicial"
     * determina o número inicial, progressivo, das matrículas que serão inseridas.
     * @param qtdRse : Quantidade de registros servidores a serem criados (cada um ligado a um servidor que também será criado)
     * @param qtdAde : Quantidade de contratos que serão criados
     * @param matriculaInicial : Matrícula numérica inicial, será incrementada de 1 a cada novo registro servidor
     * @param criarParcelas : Indica se devem ser criadas as parcelas já pagas dos contratos, com ocorrência de pagamento
     * @param nseCodigo : Natureza de serviço onde serão incluídos os contratos
     * @param responsavel : Usuário responsável pela criação do histórico de teste
     * @throws ConsignanteControllerException
     */
    @Override
    public void gerarHistoricoTeste(int qtdRse, int qtdAde, int matriculaInicial, boolean criarParcelas, String nseCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            String usuCodigo = responsavel.getUsuCodigo();
            java.sql.Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(null, responsavel);

            String periodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);
            if (TextHelper.isNull(periodicidade)) {
                periodicidade = CodedValues.PERIODICIDADE_FOLHA_MENSAL;
            }

            // Busca os convênios ativos, juntamente com os órgãos: os servidores e os contratos
            // serão incluídos sequencialmente nestas entidade.
            ListaConvenioAtivoHistoricoQuery query = new ListaConvenioAtivoHistoricoQuery();
            query.nseCodigo = nseCodigo;
            List<TransferObject> convenios = query.executarDTO();

            if (convenios.size() == 0) {
                throw new ConsignanteControllerException("mensagem.nenhumConvenioEncontrado", responsavel);
            }

            // Determina a quantidade de contratos por servidor
            int qtdAdeIncluidos = 0;
            int qtdAdePorRse = 1;
            if (qtdAde > qtdRse) {
                qtdAdePorRse = (int) Math.ceil((double) qtdAde / qtdRse);
            }

            for (int i = 0; i < qtdRse; i++) {
                TransferObject convenio = convenios.get(i % convenios.size());
                String orgCodigo = (String) convenio.getAttribute(Columns.ORG_CODIGO);
                String vcoCodigo = (String) convenio.getAttribute(Columns.VCO_CODIGO);

                // Determina os dados do servidor
                String rseMatricula = String.valueOf(matriculaInicial + i);

                String serCpf = TextHelper.formataMensagem(rseMatricula, "0", 9, false);
                int[] dvs = TextHelper.calculaDvCpf(serCpf);
                serCpf = TextHelper.format(serCpf + dvs[0] + dvs[1], "###.###.###-##");

                String serNome = "SERVIDOR NOME " + rseMatricula;
                Date dataNasc = DateHelper.addDays(DateHelper.parse("01/01/1950", LocaleHelper.getDatePattern()), (int) Math.round(Math.random() * 15000));
                java.sql.Date serDataNasc = DateHelper.toSQLDate(dataNasc);

                // A margem restante e usada deverão ser recalculadas após a execução
                BigDecimal rseMargem = new BigDecimal(Math.random() * 1500.00).setScale(2, java.math.RoundingMode.DOWN);
                BigDecimal rseMargemRest = rseMargem;
                BigDecimal rseMargemUsada = BigDecimal.ZERO;

                // Insere o Servidor
                Servidor servidor = ServidorHome.create(serCpf, serDataNasc, "", "", serNome);

                // Insere o Registro Servidor
                RegistroServidor registroServidor = RegistroServidorHome.create(servidor.getSerCodigo(), orgCodigo, CodedValues.SRS_ATIVO, rseMatricula, rseMargem, rseMargemRest, rseMargemUsada);

                // Insere os contratos
                for (int j = 0; j < qtdAdePorRse && qtdAdeIncluidos < qtdAde; j++) {
                    // Busca as configurações do serviço ligados ao convênio onde o contrato será incluído
                    Short adeIncMargem = Short.valueOf(convenio.getAttribute(Columns.ADE_INC_MARGEM).toString());
                    Short adeIntFolha = Short.valueOf(convenio.getAttribute(Columns.ADE_INT_FOLHA).toString());
                    String adeTipoVlr = convenio.getAttribute(Columns.ADE_TIPO_VLR).toString();
                    String paramMaxPrazo = convenio.getAttribute(Columns.ADE_PRAZO).toString();
                    int maxPrazo = (!paramMaxPrazo.equals("")) ? Integer.parseInt(paramMaxPrazo) : -1;

                    // Determina os dados do contrato: valor e prazo randômicos
                    BigDecimal adeVlr = rseMargem.divide(new BigDecimal(1 + Math.random()), 2, java.math.RoundingMode.DOWN).max(BigDecimal.ONE);
                    Integer adePrazo = Math.max((int) Math.round(Math.random() * Math.max(84, maxPrazo)), 1);
                    Integer adePrdPagas = (int) Math.round(Math.random() * adePrazo);

                    // Se o prazo é limitado, então verifica os valores randômicos
                    if (maxPrazo > 0) {
                        adePrazo = Math.min(adePrazo, maxPrazo);
                        adePrdPagas = Math.min(adePrazo, adePrdPagas);
                    }

                    String sadCodigo = (adePrdPagas == 0) ? CodedValues.SAD_DEFERIDA :
                        (adePrdPagas == adePrazo ? CodedValues.SAD_CONCLUIDO : CodedValues.SAD_EMANDAMENTO);

                    Date dataIni = DateHelper.addMonths(periodoAtual, -1 * adePrdPagas);
                    Date dataFim = DateHelper.addMonths(dataIni, adePrazo - 1);
                    java.sql.Date adeAnoMesIni = DateHelper.toSQLDate(dataIni);
                    java.sql.Date adeAnoMesFim = DateHelper.toSQLDate(dataFim);

                    // Se é prazo indeterminado, então grava null no prazo e na data final
                    if (maxPrazo == 0) {
                        adePrazo = null;
                        adeAnoMesFim = null;
                    }

                    // Insere o contrato de acordo com os dados do convênio
                    AutDesconto autDesconto = AutDescontoHome.create(sadCodigo, vcoCodigo, registroServidor.getRseCodigo(), null, usuCodigo,
                            "", null, CodedValues.COD_REG_DESCONTO, adePrazo, adePrdPagas, adeAnoMesIni, adeAnoMesFim, null, null, adeVlr,
                            null, null, null, null, null, null, adeTipoVlr, adeIntFolha, adeIncMargem,
                            null, null, null, null, null, null, null, null, null, null, periodicidade, null);

                    String adeCodigo = autDesconto.getAdeCodigo();

                    // Insere ocorrência de inclusão de contrato
                    OcorrenciaAutorizacaoHome.create(adeCodigo, CodedValues.TOC_TARIF_RESERVA, usuCodigo,
                            ApplicationResourcesHelper.getMessage("mensagem.impHistorico.ocorrencia.ade.ptf.inclusao.contrato", responsavel), BigDecimal.ZERO, adeVlr, responsavel.getIpUsuario(), null, periodoAtual, null);

                    LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.impHistorico.inserido.ade.arg0", responsavel, adeCodigo));
                    qtdAdeIncluidos++;

                    if (criarParcelas) {
                        // Se é para criar parcelas, então faz loop para incluir sequencialmente
                        // as parcelas e suas ocorrências de retorno
                        for (int k = 0; k < adePrdPagas; k++) {
                            Integer prdNumero = Integer.valueOf(k + 1);
                            Date prdData = DateHelper.addMonths(dataIni, k);

                            ParcelaDesconto prdBean = ParcelaDescontoHome.create(adeCodigo, prdNumero.shortValue(), null, CodedValues.SPD_LIQUIDADAFOLHA, prdData, prdData, adeVlr, adeVlr);
                            OcorrenciaParcelaHome.create(prdBean.getPrdCodigo(), CodedValues.TOC_RETORNO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.com.retorno", responsavel), usuCodigo, prdData);
                        }
                    }
                }
            }

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Gera histórico randômico, contendo "qtdAdePorRse" contratos por registro servidor,
     * sendo que serão usados "pctRse" porcento dos servidores ativos.
     * determina o número inicial, progressivo, das matrículas que serão inseridas.
     * @param pctRse : Percentual de registros servidores a serem usados
     * @param qtdAdePorRse : Quantidade de contratos por servidor que serão criados
     * @param criarParcelas : Indica se devem ser criadas as parcelas já pagas dos contratos, com ocorrência de pagamento
     * @param nseCodigo : Natureza de serviço onde serão incluídos os contratos
     * @param responsavel : Usuário responsável pela criação do histórico de teste
     * @throws ConsignanteControllerException
     */
    @Override
    public void gerarHistoricoTesteOrientado(int pctRse, int qtdAdePorRse, boolean criarParcelas, String nseCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            String usuCodigo = responsavel.getUsuCodigo();
            java.sql.Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAnterior(null, responsavel);

            String periodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);
            if (TextHelper.isNull(periodicidade)) {
                periodicidade = CodedValues.PERIODICIDADE_FOLHA_MENSAL;
            }

            List<TransferObject> rseList = servidorController.lstRegistroServidor(CodedValues.SRS_ATIVOS, null, null, responsavel);
            int qtdRse = rseList.size() / (100 / pctRse);

            for (int i = 0; i < qtdRse; i++) {
                String rseCodigo = rseList.get(i).getAttribute(Columns.RSE_CODIGO).toString();
                RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKey(rseCodigo);
                BigDecimal rseMargem = registroServidor.getRseMargem();

                // Busca os convênios ativos, juntamente com os órgãos: os servidores e os contratos
                // serão incluídos sequencialmente nestas entidade.
                ListaConvenioAtivoHistoricoQuery query = new ListaConvenioAtivoHistoricoQuery();
                query.nseCodigo = nseCodigo;
                query.orgCodigo = registroServidor.getOrgao().getOrgCodigo();
                List<TransferObject> convenios = query.executarDTO();

                if (convenios.size() == 0) {
                    throw new ConsignanteControllerException("mensagem.nenhumConvenioEncontrado", responsavel);
                }

                // Insere os contratos
                for (int j = 0; j < qtdAdePorRse; j++) {
                    TransferObject convenio = convenios.get(j % convenios.size());
                    String vcoCodigo = (String) convenio.getAttribute(Columns.VCO_CODIGO);

                    // Busca as configurações do serviço ligados ao convênio onde o contrato será incluído
                    Short adeIncMargem = Short.valueOf(convenio.getAttribute(Columns.ADE_INC_MARGEM).toString());
                    Short adeIntFolha = Short.valueOf(convenio.getAttribute(Columns.ADE_INT_FOLHA).toString());
                    String adeTipoVlr = convenio.getAttribute(Columns.ADE_TIPO_VLR).toString();
                    String paramMaxPrazo = convenio.getAttribute(Columns.ADE_PRAZO).toString();
                    int maxPrazo = (!paramMaxPrazo.equals("")) ? Integer.parseInt(paramMaxPrazo) : -1;

                    // Determina os dados do contrato: valor e prazo randômicos
                    BigDecimal adeVlr = rseMargem.divide(new BigDecimal(1 + Math.random()), 2, java.math.RoundingMode.DOWN).max(BigDecimal.ONE);
                    Integer adePrazo = Math.max((int) Math.round(Math.random() * Math.max(84, maxPrazo)), 1);
                    Integer adePrdPagas = (int) Math.round(Math.random() * adePrazo);

                    // Se o prazo é limitado, então verifica os valores randômicos
                    if (maxPrazo > 0) {
                        adePrazo = Math.min(adePrazo, maxPrazo);
                        adePrdPagas = Math.min(adePrazo, adePrdPagas);
                    }

                    String sadCodigo = (adePrdPagas == 0) ? CodedValues.SAD_DEFERIDA :
                                                            (adePrdPagas == adePrazo ? CodedValues.SAD_CONCLUIDO : CodedValues.SAD_EMANDAMENTO);

                    Date dataIni = DateHelper.addMonths(periodoAtual, -1 * adePrdPagas);
                    Date dataFim = DateHelper.addMonths(dataIni, adePrazo - 1);
                    java.sql.Date adeAnoMesIni = DateHelper.toSQLDate(dataIni);
                    java.sql.Date adeAnoMesFim = DateHelper.toSQLDate(dataFim);

                    // Se é prazo indeterminado, então grava null no prazo e na data final
                    if (maxPrazo == 0) {
                        adePrazo = null;
                        adeAnoMesFim = null;
                    }

                    // Insere o contrato de acordo com os dados do convênio
                    AutDesconto autDesconto = AutDescontoHome.create(sadCodigo, vcoCodigo, registroServidor.getRseCodigo(), null, usuCodigo,
                            "", null, CodedValues.COD_REG_DESCONTO, adePrazo, adePrdPagas, adeAnoMesIni, adeAnoMesFim, null, null, adeVlr,
                            null, null, null, null, null, null, adeTipoVlr, adeIntFolha, adeIncMargem,
                            null, null, null, null, null, null, null, null, null, null, periodicidade, null);

                    String adeCodigo = autDesconto.getAdeCodigo();

                    // Insere ocorrência de inclusão de contrato
                    OcorrenciaAutorizacaoHome.create(adeCodigo, CodedValues.TOC_TARIF_RESERVA, usuCodigo,
                            ApplicationResourcesHelper.getMessage("mensagem.impHistorico.ocorrencia.ade.ptf.inclusao.contrato", responsavel), BigDecimal.ZERO, adeVlr, responsavel.getIpUsuario(), null, periodoAtual, null);

                    LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.impHistorico.inserido.ade.arg0", responsavel, adeCodigo));

                    if (criarParcelas) {
                        // Se é para criar parcelas, então faz loop para incluir sequencialmente
                        // as parcelas e suas ocorrências de retorno
                        for (int k = 0; k < adePrdPagas; k++) {
                            Integer prdNumero = Integer.valueOf(k + 1);
                            Date prdData = DateHelper.addMonths(dataIni, k);

                            ParcelaDesconto prdBean = ParcelaDescontoHome.create(adeCodigo, prdNumero.shortValue(), null, CodedValues.SPD_LIQUIDADAFOLHA, prdData, prdData, adeVlr, adeVlr);
                            OcorrenciaParcelaHome.create(prdBean.getPrdCodigo(), CodedValues.TOC_RETORNO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.com.retorno", responsavel), usuCodigo, prdData);
                        }
                    }
                }
            }

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     *
     * @param responsavel
     * @throws ConsignanteControllerException
     */
    @Override
    public void apagarHistoricoTesteOrientado(AcessoSistema responsavel) throws ConsignanteControllerException {
        Session session = SessionUtil.getSession();
        try (session) {
            MutationQuery q = session.createMutationQuery("delete HistoricoMargemRse");
            int linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete OcorrenciaParcela");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete ParcelaDesconto");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete OcorrenciaParcelaPeriodo");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete ParcelaDescontoPeriodo");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete OcorrenciaAutorizacao");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete RelacionamentoAutorizacao");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete HtRelacionamentoAdeOrigem");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete HtRelacionamentoAdeDestino");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete AnexoAutorizacaoDesconto");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete CoeficienteDesconto");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete ParamServicoAutorizacao");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete SolicitacaoAutorizacao");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete PropostaLeilaoSolicitacao");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete OcorrenciaDadosAde");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete DadosAutorizacaoDesconto");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete SaldoDevedor");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete OcorrenciaDespIndividual");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete DespesaIndividual");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete AutDesconto");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete HistoricoConclusaoRetorno");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
            q = session.createMutationQuery("delete HistoricoExportacao");
            linhasAfetadas = q.executeUpdate();
            LOG.debug("Linhas Afetadas: " + linhasAfetadas);
        }
    }
}