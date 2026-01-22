package com.zetra.econsig.service.consignacao;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParcelaDescontoTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParcelaControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.BatchManager;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.ConsignatariaHome;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.Orgao;
import com.zetra.econsig.persistence.entity.OrgaoHome;
import com.zetra.econsig.persistence.entity.ParcelaDesconto;
import com.zetra.econsig.persistence.entity.ParcelaDescontoHome;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodo;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodoHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacao;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoId;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.persistence.entity.ServicoHome;
import com.zetra.econsig.persistence.entity.StatusAutorizacaoDesconto;
import com.zetra.econsig.persistence.entity.StatusConvenio;
import com.zetra.econsig.persistence.entity.StatusConvenioHome;
import com.zetra.econsig.persistence.entity.VerbaConvenio;
import com.zetra.econsig.persistence.entity.VerbaConvenioHome;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoRelacionamentoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaRelAutorizacaoInconsTransfQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaTransfereAdeQuery;
import com.zetra.econsig.persistence.query.consignacao.VerificaAdeProvisionamentoQuery;
import com.zetra.econsig.persistence.query.distribuirconsignacao.ListaConsignacaoParaDistribuicaoQuery;
import com.zetra.econsig.persistence.query.distribuirconsignacao.ListaServidorVerbaComConsignacaoParaDistribuicaoQuery;
import com.zetra.econsig.persistence.query.distribuirconsignacao.ListaVerbaConvenioParaDistribuicaoQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicoQuery;
import com.zetra.econsig.persistence.query.servidor.ObtemConvenioMesmoSvcTransfQuery;
import com.zetra.econsig.persistence.query.servidor.ObtemConvenioOutroSvcTransfQuery;
import com.zetra.econsig.persistence.query.servidor.ObtemRelacionamentoInconsistenteTransferenciaQuery;
import com.zetra.econsig.persistence.query.servidor.ServidorPossuiBloqCnvSvcQuery;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.parcela.ParcelaController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: TransferirConsignacaoControllerBean</p>
 * <p>Description: Session Bean para operação de transferência de consignações.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class TransferirConsignacaoControllerBean extends AutorizacaoControllerBean implements TransferirConsignacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TransferirConsignacaoControllerBean.class);

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ParcelaController parcelaController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private SuspenderConsignacaoController suspenderConsignacaoController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Autowired
    private MargemController margemController;

    @Override
    public int countAdeTransferencia(String csaCodigoOrigem, String csaCodigoDestino, String svcCodigoOrigem, String svcCodigoDestino, String orgCodigo, List<String> sadCodigo, java.util.Date periodoIni, java.util.Date periodoFim, List<Long> adeNumero, String rseMatricula, String serCpf, boolean somenteConveniosAtivos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            ListaTransfereAdeQuery query = new ListaTransfereAdeQuery();
            query.count = true;
            query.csaCodigoOrigem = csaCodigoOrigem;
            query.csaCodigoDestino = csaCodigoDestino;
            query.svcCodigoOrigem = svcCodigoOrigem;
            query.svcCodigoDestino = svcCodigoDestino;
            query.orgCodigo = orgCodigo;
            query.sadCodigo = sadCodigo;
            query.periodoIni = periodoIni;
            query.periodoFim = periodoFim;
            query.adeNumero = adeNumero;
            query.rseMatricula = rseMatricula;
            query.serCpf = serCpf;
            query.somenteConveniosAtivos = somenteConveniosAtivos;

            return query.executarContador();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> listarAdeTransferencia(String csaCodigoOrigem, String csaCodigoDestino, String svcCodigoOrigem, String svcCodigoDestino, String orgCodigo, List<String> sadCodigo, java.util.Date periodoIni, java.util.Date periodoFim, List<Long> adeNumero, String rseMatricula, String serCpf, int offset, int count, boolean somenteConveniosAtivos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            ListaTransfereAdeQuery query = new ListaTransfereAdeQuery();
            query.csaCodigoOrigem = csaCodigoOrigem;
            query.csaCodigoDestino = csaCodigoDestino;
            query.svcCodigoOrigem = svcCodigoOrigem;
            query.svcCodigoDestino = svcCodigoDestino;
            query.orgCodigo = orgCodigo;
            query.sadCodigo = sadCodigo;
            query.periodoIni = periodoIni;
            query.periodoFim = periodoFim;
            query.adeNumero = adeNumero;
            query.rseMatricula = rseMatricula;
            query.serCpf = serCpf;
            query.somenteConveniosAtivos = somenteConveniosAtivos;

            if (count != -1) {
                query.maxResults = count;
                query.firstResult = offset;
            }

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Transfere os contratos de uma consignatária para outra - Menu -> Sistema -> Administrador -> Trasnferir consignação geral
     * @param csaCodigoOrigem
     * @param csaCodigoDestino
     * @param svcCodigoOrigem
     * @param svcCodigoDestino
     * @param orgCodigo
     * @param sadCodigo
     * @param periodoIni
     * @param periodoFim
     * @param adeNumero
     * @param rseMatricula
     * @param serCpf
     * @param ocaObs
     * @param atualizarAdeIncMargem
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    @Override
    public void transfereAde(String csaCodigoOrigem, String csaCodigoDestino, String svcCodigoOrigem, String svcCodigoDestino, String orgCodigo, List<String> sadCodigo, java.util.Date periodoIni, java.util.Date periodoFim, List<Long> adeNumero, String rseMatricula, String serCpf, String ocaObs, boolean atualizarAdeIncMargem, boolean somenteConveniosAtivos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            if (TextHelper.isNull(svcCodigoDestino) && TextHelper.isNull(csaCodigoDestino)) {
                throw new AutorizacaoControllerException("mensagem.erro.transf.contratos.csa.svc.destino.nulos", responsavel);
            }

            boolean validarQtdContratos = ParamSist.paramEquals(CodedValues.TPC_VALIDA_QTD_CONTRATOS_TRANSFERENCIA, CodedValues.TPC_SIM, responsavel);

            LogDelegate log = new LogDelegate(responsavel, Log.TRANSFERENCIA_AUTORIZACAO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setConsignataria(csaCodigoOrigem);
            log.setConsignatariaDestino(csaCodigoDestino);
            log.setServico(svcCodigoOrigem);
            log.setServicoDestino(svcCodigoDestino);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.transferencia.contratos", responsavel));
            log.write();

            ListaTransfereAdeQuery query = new ListaTransfereAdeQuery();
            query.csaCodigoOrigem = csaCodigoOrigem;
            query.csaCodigoDestino = csaCodigoDestino;
            query.svcCodigoOrigem = svcCodigoOrigem;
            query.svcCodigoDestino = svcCodigoDestino;
            query.orgCodigo = orgCodigo;
            query.sadCodigo = sadCodigo;
            query.periodoIni = periodoIni;
            query.periodoFim = periodoFim;
            query.adeNumero = adeNumero;
            query.rseMatricula = rseMatricula;
            query.serCpf = serCpf;
            if(somenteConveniosAtivos) {
                query.somenteConveniosAtivos = true;
            }

            // DESENV-10310 : Faz a limpeza da session (por causa da incapacidade do hibernate).
            BatchManager batman = new BatchManager(SessionUtil.getSession());

            // Lista de registros servidores para recálculo de margem
            List<String> rseCodigos = new ArrayList<>();

            List<TransferObject> transfere = query.executarDTO();
            for (TransferObject next : transfere) {
                String adeCodigo = next.getAttribute(Columns.ADE_CODIGO).toString();
                String rseCodigo = next.getAttribute(Columns.RSE_CODIGO).toString();
                String csaIdentificadorAntigo = next.getAttribute(Columns.CSA_IDENTIFICADOR).toString();
                String svcIdentificadorAntigo = next.getAttribute(Columns.SVC_IDENTIFICADOR).toString();
                String vcoCodigoNovo = next.getAttribute("VCO_CODIGO_NEW").toString();
                String adeIncMargemOld = next.getAttribute("INC_MARGEM_OLD").toString();
                String adeIncMargemNew = next.getAttribute("INC_MARGEM_NEW").toString();
                String sadCodigoAtual = next.getAttribute(Columns.SAD_CODIGO).toString();
                String sadDescricao = (String) next.getAttribute(Columns.SAD_DESCRICAO);

                if (!atualizarAdeIncMargem || adeIncMargemOld.equals(adeIncMargemNew)) {
                    // Se não atualiza a incidência de margem ou já são iguais, atualiza apenas a ligação
                    // de consignatária/serviço através do verba convênio
                    AutDescontoHome.updateVerbaConvenio(adeCodigo, vcoCodigoNovo);
                } else {
                    // Se deve atualizar incidência e são diferentes, então atualiza verba convenio e a incidência
                    AutDescontoHome.updateVerbaConvenioIncideMargem(adeCodigo, vcoCodigoNovo, Short.valueOf(adeIncMargemNew));
                    rseCodigos.add(rseCodigo);
                }

                // Atualiza csa_codigo de origem e destino quando existir relacionamento de compra para o contrato transferido
                if (!TextHelper.isNull(csaCodigoDestino)) {
                    RelacionamentoAutorizacaoHome.updateCsaCodigo(adeCodigo, csaCodigoDestino);
                }

                // Cria ocorrência de informação de transferência de contrato
                String ocorrenciaObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.consignacao.transferida.consignataria.arg0.servico.arg1", responsavel, csaIdentificadorAntigo, svcIdentificadorAntigo);
                if (!TextHelper.isNull(ocaObs)) {
                    ocorrenciaObs += " " + ocaObs;
                }
                criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ocorrenciaObs, responsavel);

                // Verifica caso TPC_VALIDA_QTD_CONTRATOS_TRANSFERENCIA=S se a consignação ultrapassa algum dos limites de contrato
                if (validarQtdContratos) {
                    LOG.debug("VALIDANDO LIMITES NA TRANSFERÊNCIA DE CONTRATO");
                    try {
                        LOG.debug("VERIFICA LIMITES DO CONTRATO '" + adeCodigo + "'");
                        verificaLimiteAoTransferirContrato(adeCodigo, responsavel);
                    } catch (AutorizacaoControllerException e) {
                        LOG.debug("CONTRATO '" + adeCodigo + "' FORA DOS LIMITES PARA O REGISTRO SERVIDOR '" + rseCodigo + "' SERÁ SUSPENSO. " + e.getMessage());
                        try {
                            // Contrato em estoque pode ser suspenso por consignante ou suporte
                            boolean suspendeEstoque = (responsavel.isCseSup() || responsavel.isCsa()) && (sadCodigoAtual.equals(CodedValues.SAD_ESTOQUE) || sadCodigoAtual.equals(CodedValues.SAD_ESTOQUE_MENSAL) || sadCodigoAtual.equals(CodedValues.SAD_ESTOQUE_NAO_LIBERADO));

                            if (!sadCodigoAtual.equals(CodedValues.SAD_DEFERIDA) && !sadCodigoAtual.equals(CodedValues.SAD_EMANDAMENTO) && !suspendeEstoque) {
                                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.erro.autorizacao.nao.pode.ser.suspensa.situacao.atual.dela.nao.permite.esta.operacao", responsavel, sadDescricao));
                                continue;
                            }

                            // Suspende consignação que extrapola algum dos limites de contrato
                            suspenderConsignacaoController.suspender(adeCodigo, null, null, responsavel);
                            criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, e.getMessage(), responsavel);

                            LOG.debug("CONTRATO '" + adeCodigo + "' SUSPENSO PARA O REGISTRO SERVIDOR '" + rseCodigo + "'");
                        } catch (AutorizacaoControllerException e1) {
                            LOG.debug("CONTRATO NÃO SERÁ SUSPENSO PORQUE NÃO FOI POSSÍVEL REALIZAR A OPERAÇÃO '" + adeCodigo + "' PARA O REGISTRO SERVIDOR '" + rseCodigo + "'. " + e1.getMessage());
                        }
                    }
                }

                // DESENV-10310 : Faz a limpeza da session (por causa da incapacidade do hibernate).
                batman.iterate();
            }

            if (atualizarAdeIncMargem && !rseCodigos.isEmpty()) {
                // Recalcula margem após atualização da incidência
                margemController.recalculaMargemComHistorico("RSE", rseCodigos, responsavel);
            }

        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Transfere os contratos de códigos presentes em 'adeCodigos' para o novo registro servidor
     * representado por 'rseCodigoNov'.
     * @param adeCodigos : Lista de contratos a serem transferidos
     * @param rseCodigoNov : Código do registro servidor novo
     * @param rseCodigoAnt : Código do registro servidor antigo
     * @param rseMatriculaAntiga : Matrícula do registro servidor antigo
     * @param orgIdentificadorAntigo : Ident. do órgão do registro servidor antigo
     * @param validaBloqSer : TRUE se deve validar bloqueio de cnv/svc no servidor (TPC_BLOQ_TRANSF_ADE_COM_BLOQ_CNV_SVC_SERVIDOR)
     * @param responsavel : Usuário responsável pela operação
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    public List<String> transfereAde(List<String> adeCodigos, String rseCodigoNov, String rseCodigoAnt, String rseMatriculaAntiga, String orgIdentificadorAntigo, boolean validaBloqSer, boolean verificaRelacionamentos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            String ocaObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.consignacao.transferida.matricula.arg0.orgao.arg2", responsavel, rseMatriculaAntiga, orgIdentificadorAntigo);
            String tocCodigo = CodedValues.TOC_INFORMACAO;

            // Habilita criação de convênio na transferência de contratos quando este não existe
            boolean criaConvenio = ParamSist.paramEquals(CodedValues.TPC_CRIA_CNV_TRANSFERENCIA_SERVIDOR, CodedValues.TPC_SIM, responsavel);
            // Permite transferência de contratos entre serviços de mesma natureza
            boolean permiteTransfEntreSvc = ParamSist.paramEquals(CodedValues.TPC_TRANSF_ADE_SVC_MSM_NATUREZA_TRANSF_SER, CodedValues.TPC_SIM, responsavel);
            // Bloqueia transferência caso o servidor de destino possua bloqueio de convênio/serviço
            boolean bloqTransfSerBloqCnvSvc = ParamSist.paramEquals(CodedValues.TPC_BLOQ_TRANSF_ADE_COM_BLOQ_CNV_SVC_SERVIDOR, CodedValues.TPC_SIM, responsavel);
            // Cria uma nova consignação no destino da transferência
            boolean criaNovaAdeTransferencia = ParamSist.paramEquals(CodedValues.TPC_CRIAR_NOVA_ADE_TRANSFERENCIA, CodedValues.TPC_SIM, responsavel);

            // Ordena a lista de ADE_CODIGO por antiguidade, transferindo primeiro as mais antigas
            adeCodigos = pesquisarConsignacaoController.ordenarContratosPorDataCrescente(adeCodigos, responsavel);

            // Desabilita a troca de serviço caso o RSE de destino não tenha consignações
            if (permiteTransfEntreSvc) {
                int count = pesquisarConsignacaoController.obtemTotalConsignacaoPorRse(rseCodigoNov, responsavel);
                if (count == 0) {
                    permiteTransfEntreSvc = false;
                }
            }

            // Lista de consignações não transferidas
            List<String> adeCodigosTransferidos = new ArrayList<>();
            adeCodigosTransferidos.addAll(adeCodigos);

            // Lista com as críticas encontradas
            List<String> criticas = new ArrayList<>();

            RegistroServidor rseBean = RegistroServidorHome.findByPrimaryKey(rseCodigoNov);
            String orgCodigoNov = rseBean.getOrgao().getOrgCodigo();

            String vcoCodigoAnt = null;
            String vcoCodigoNov = null;
            String adeNumero = null;
            String svcCodigo = null;
            String csaCodigo = null;
            String verba = null;

            // DESENV-10310 : Faz a limpeza da session (por causa da incapacidade do hibernate).
            BatchManager batman = new BatchManager(SessionUtil.getSession());

            for (String adeCodigo : adeCodigos) {
                boolean criaOcorrencia = false;
                /*
                 * Busca o código do novo convênio, caso exista, para a transferência do contrato:
                 * verifica parametrização que permite transferência para outro serviço de mesma natureza (TPC 312).
                 * Caso esteja habilitado, o novo convênio será o convênio ativo, com menor quantidade de contratos,
                 * que possui um contrato com menor data de término, de serviços da mesma natureza do serviço origem.
                 * Caso não esteja habilitado, o novo convênio será o convênio do mesmo serviço do contrato origem.
                 * Caso não exista o convênio, seja o do mesmo serviço ou outro de mesma natureza, verifica se o
                 * convênio deve ser criado para a transferência (TPC 311). Caso não exista e não possa criar, então
                 * retorna crítica da transferência. Caso possa criar, o convênio será criado para o mesmo serviço
                 * do contrato origem, mesma consignatária e órgão do servidor destino, sendo criado bloqueado.
                 */
                List<TransferObject> result = null;
                if (permiteTransfEntreSvc) {
                    ObtemConvenioOutroSvcTransfQuery query = new ObtemConvenioOutroSvcTransfQuery();
                    query.adeCodigo = adeCodigo;
                    query.orgCodigo = orgCodigoNov;
                    query.rseCodigo = rseCodigoNov;
                    query.bloqTransfSerBloqCnvSvc = bloqTransfSerBloqCnvSvc;
                    result = query.executarDTO();
                } else {
                    ObtemConvenioMesmoSvcTransfQuery query = new ObtemConvenioMesmoSvcTransfQuery();
                    query.adeCodigo = adeCodigo;
                    query.orgCodigo = orgCodigoNov;
                    result = query.executarDTO();
                }

                // Se não retornar nada, significa que o ade_codigo é inválido, já que mesmo que
                // o convênio não exista, a query retorna resultado já que é usado left join
                if (result != null && result.size() > 0) {
                        TransferObject dados = result.get(0);
                        vcoCodigoNov = (String) dados.getAttribute("VCO_CODIGO_NOVO");
                        vcoCodigoAnt = (String) dados.getAttribute("VCO_CODIGO");
                        adeNumero = dados.getAttribute("ADE_NUMERO").toString();
                        svcCodigo = dados.getAttribute("SVC_CODIGO").toString();
                        csaCodigo = dados.getAttribute("CSA_CODIGO").toString();
                        verba = (String) dados.getAttribute("VERBA_NOVO");

                    //Se o codigo de verba estiver null informa ao usuário que essa consignação não pode ser transferida.
                    if (!TextHelper.isNull(vcoCodigoNov) && TextHelper.isNull(verba)) {

                        Orgao orgao = OrgaoHome.findByPrimaryKey(orgCodigoNov);
                        String orgIdentificador = orgao.getOrgIdentificador();

                        Consignataria csa = ConsignatariaHome.findByPrimaryKey(csaCodigo);
                        String csaIdentidentificador = csa.getCsaIdentificador();

                        Servico svc = ServicoHome.findByPrimaryKey(svcCodigo);
                        String svcIdentifificador = svc.getSvcIdentificador();

                        LOG.debug("Verba do convênio destino está vazio " + ", VERBA CONVÊNIO: " + vcoCodigoNov);
                        criticas.add(ApplicationResourcesHelper.getMessage("mensagem.erro.transferencia.verba.destino.vazio", responsavel, adeNumero, csaIdentidentificador, orgIdentificador, svcIdentifificador));
                        adeCodigosTransferidos.remove(adeCodigo);
                        continue;
                    }

                    // Se o convênio não foi encontrado mesmo procurando por um convênio de um serviço de mesma natureza e pode criar convênio
                    if (TextHelper.isNull(vcoCodigoNov) && criaConvenio) {
                        try {
                            AutDesconto adeBean = AutDescontoHome.findByPrimaryKey(adeCodigo);
                            VerbaConvenio verbaBean = VerbaConvenioHome.findByPrimaryKey(adeBean.getVerbaConvenio().getVcoCodigo());
                            Convenio convenioBean = ConvenioHome.findByPrimaryKey(verbaBean.getConvenio().getCnvCodigo());

                            String cnvCodVerbaModelo = convenioBean.getCnvCodVerba();
                            String cnvCodVerbaRefModelo = convenioBean.getCnvCodVerbaRef();
                            String cnvCodVerbaFeriasModelo = convenioBean.getCnvCodVerbaFerias();

                            List<String> cnvCodigosNovos = convenioController.createConvenio(svcCodigo, csaCodigo, orgCodigoNov, cnvCodVerbaModelo, cnvCodVerbaRefModelo, cnvCodVerbaFeriasModelo, responsavel);

                            // Seta a nova verba que deverá ser utilizada
                            if (cnvCodigosNovos != null && !cnvCodigosNovos.isEmpty()) {
                                Convenio cnvNovo = ConvenioHome.findByPrimaryKey(cnvCodigosNovos.get(0));
                                // Seta a o novoconvênio para inativo
                                StatusConvenio statusConvenio = StatusConvenioHome.findByPrimaryKey(CodedValues.SCV_INATIVO);
                                cnvNovo.setStatusConvenio(statusConvenio);
                                ConvenioHome.update(cnvNovo);

                                VerbaConvenio verbaNova = VerbaConvenioHome.findAtivoByConvenio(cnvNovo.getCnvCodigo());
                                verbaNova.setVcoAtivo(CodedValues.STS_INATIVO);
                                VerbaConvenioHome.update(verbaNova);
                                vcoCodigoNov = verbaNova.getVcoCodigo();
                            }
                        } catch (ConvenioControllerException e) {
                            LOG.debug("Não foi possível criar convênio faltante na transferência de contratos do servidor.", e);
                        }
                    }

                    // O novo convênio não existe, então busca os objetos para enviar mensagem de erro para o usuário
                    if (TextHelper.isNull(vcoCodigoNov)) {
                        Orgao orgaoBean = OrgaoHome.findByPrimaryKey(orgCodigoNov);
                        String orgIdent = orgaoBean.getOrgIdentificador();

                        Consignataria csaBean = ConsignatariaHome.findByPrimaryKey(csaCodigo);
                        String csaIdent = csaBean.getCsaIdentificador();

                        Servico svcBean = ServicoHome.findByPrimaryKey(svcCodigo);
                        String svcIdent = svcBean.getSvcIdentificador();

                        if (criaConvenio) {
                            criticas.add(ApplicationResourcesHelper.getMessage("mensagem.erro.consignacao.arg0.nao.pode.ser.transferida.nao.foi.possivel.criar.convenio", responsavel, adeNumero));
                            adeCodigosTransferidos.remove(adeCodigo);
                        } else {
                            LOG.debug("Novo convênio não existe --> CSA: " + csaIdent + ", ORG: " + orgIdent + ", SVC: " + svcIdent);
                            criticas.add(ApplicationResourcesHelper.getMessage("mensagem.erro.consignacao.arg0.nao.pode.ser.transferida.novo.convenio.nao.existe.consignataria.arg1.orgao.arg2.servico.arg3", responsavel, adeNumero, csaIdent, orgIdent, svcIdent));
                            adeCodigosTransferidos.remove(adeCodigo);
                        }

                    } else {
                        if (bloqTransfSerBloqCnvSvc && validaBloqSer) {
                            // Se é importação de transferidos e não pode transferir quando o servidor
                            // possui bloqueios, verifica se o serviço/convênio de destino possui
                            // quantidade de contratos permitidos igual a zero.
                            ServidorPossuiBloqCnvSvcQuery bloqSerQuery = new ServidorPossuiBloqCnvSvcQuery();
                            bloqSerQuery.rseCodigo = rseCodigoNov;
                            bloqSerQuery.vcoCodigo = vcoCodigoNov;
                            if (bloqSerQuery.executarContador() > 0) {
                                criticas.add(ApplicationResourcesHelper.getMessage("mensagem.erro.consignacao.arg0.nao.pode.ser.transferida.novo.convenio.possui.bloqueio.de.servico.para.este.servidor", responsavel, adeNumero));
                                adeCodigosTransferidos.remove(adeCodigo);
                                continue;
                            }
                        }

                        LOG.debug("TRANSFERE O CONTRATO '" + adeCodigo + "' PARA O REGISTRO SERVIDOR '" + rseCodigoNov + "'");

                        if (criaNovaAdeTransferencia) {
                            AutDesconto adeBean = AutDescontoHome.findByPrimaryKey(adeCodigo);
                            String sadCodigo = adeBean.getStatusAutorizacaoDesconto().getSadCodigo();

                            // Consignações na situação inativa devem permanecer na matrícula antiga
                            if (!CodedValues.SAD_CODIGOS_INATIVOS.contains(sadCodigo)) {
                                List<ParcelaDesconto> parcelas = ParcelaDescontoHome.findByAutDesconto(adeCodigo);
                                List<ParcelaDescontoPeriodo> parcelasPeriodo = ParcelaDescontoPeriodoHome.findByAutDesconto(adeCodigo);

                                // Consignações abertas que não possuem parcela gerada, devem ser movidas para a nova matrícula
                                if ((parcelas == null || parcelas.isEmpty()) && (parcelasPeriodo == null || parcelasPeriodo.isEmpty())) {
                                    // O novo convênio existe, então atualiza o contrato
                                    adeBean.setRegistroServidor(RegistroServidorHome.findByPrimaryKey(rseCodigoNov));
                                    adeBean.setVerbaConvenio(VerbaConvenioHome.findByPrimaryKey(vcoCodigoNov));
                                    AutDescontoHome.update(adeBean);

                                    // Grava log da transferência, registrando origem e destino da transferência
                                    LogDelegate log = new LogDelegate(responsavel, Log.TRANSFERENCIA_AUTORIZACAO, Log.UPDATE, Log.LOG_INFORMACAO);
                                    log.setAutorizacaoDesconto(adeCodigo);
                                    log.setRegistroServidor(rseCodigoAnt);
                                    log.setVerbaConvenio(vcoCodigoAnt);
                                    log.setRegistroServidorDestino(rseCodigoNov);
                                    log.setVerbaConvenioDestino(vcoCodigoNov);
                                    log.write();

                                } else {
                                    // Consignações abertas que possuem parcelas, devem ter seu status alterado para Liquidado
                                    AutDesconto adeLiquidada = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
                                    adeLiquidada.setStatusAutorizacaoDesconto(new StatusAutorizacaoDesconto(CodedValues.SAD_LIQUIDADA));
                                    AutDescontoHome.update(adeLiquidada);

                                    // Incluir ocorrência de liquidação no período atual de lançamento
                                    Date periodoAtual = null;
                                    try{
                                        periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigoNov, responsavel);
                                    } catch (PeriodoException ex) {
                                        criticas.add(ApplicationResourcesHelper.getMessage("mensagem.erro.consignacao.arg0.nao.pode.calcular.periodo.inicial", responsavel, adeNumero));
                                        adeCodigosTransferidos.remove(adeCodigo);
                                        continue;
                                    }

                                    criaOcorrenciaADE(adeCodigo, CodedValues.TOC_TARIF_LIQUIDACAO, ApplicationResourcesHelper.getMessage("mensagem.informacao.ptf.liquidacao.contrato", responsavel), null, null, null, periodoAtual, null, responsavel);

                                    Integer adePrazo = adeBean.getAdePrazo();

                                    if (adePrazo != null && adeBean.getAdePrdPagas() != null) {
                                        adePrazo -= adeBean.getAdePrdPagas();
                                    }

                                    try {
                                        List<ParcelaDescontoTO> parcelasEmProcessamento = parcelaController.findParcelas(adeBean.getAdeCodigo(), new ArrayList<>(Arrays.asList(CodedValues.SPD_EMPROCESSAMENTO)), responsavel);

                                        // Se tiver parcelas em processamento conta como se já estivessem pagas
                                        if (adePrazo != null && parcelasEmProcessamento != null) {
                                            adePrazo -= parcelasEmProcessamento.size();
                                        }
                                    } catch (ParcelaControllerException ex) {
                                        LOG.error(ex.getMessage(), ex);
                                    }

                                    if (adePrazo != null && adePrazo <= 0) {
                                        LOG.debug("TRANSFERENCIA DE CONTRATO '" + adeCodigo + "' PARA O REGISTRO SERVIDOR '" + rseCodigoNov + "' ABORTADA. PRAZO INVALIDO: " + adePrazo);
                                        criticas.add(ApplicationResourcesHelper.getMessage("mensagem.qtdParcelasInvalida", responsavel));
                                        adeCodigosTransferidos.remove(adeCodigo);
                                        continue;
                                    }

                                    Date adeAnoMesIni = periodoAtual;
                                    Date adeAnoMesFim = null;

                                    try {
                                        adeAnoMesFim = PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigoNov, adeAnoMesIni, adePrazo, adeBean.getAdePeriodicidade(), responsavel);
                                    } catch (PeriodoException ex) {
                                        criticas.add(ApplicationResourcesHelper.getMessage("mensagem.erro.consignacao.arg0.nao.pode.calcular.periodo.final", responsavel, adeNumero));
                                        adeCodigosTransferidos.remove(adeCodigo);
                                        continue;
                                    }

                                    AutDesconto novaAde = null;
                                    try {
                                        Timestamp adeDataHoraOcorrencia = adeBean.getAdeDataHoraOcorrencia() != null ? new Timestamp(adeBean.getAdeDataHoraOcorrencia().getTime()) : null;
                                        Date adeAnoMesIniRef = adeBean.getAdeAnoMesIniRef() != null ? new Date(adeBean.getAdeAnoMesIniRef().getTime()) : null;
                                        Date adeAnoMesFimRef = adeBean.getAdeAnoMesFimRef() != null ? new Date(adeBean.getAdeAnoMesFimRef().getTime()) : null;
                                        String corCodigo = adeBean.getCorrespondente() != null ? adeBean.getCorrespondente().getCorCodigo() : null;
                                        String usuCodigo = adeBean.getUsuario() != null ? adeBean.getUsuario().getUsuCodigo() : null;
                                        String cidCodigo = adeBean.getCidade() != null ? adeBean.getCidade().getCidCodigo() : null;
                                        // Se consignação está em andamento, o novo status será Deferida
                                        String novaSituacao = adeBean.getStatusAutorizacaoDesconto().getSadCodigo().equals(CodedValues.SAD_EMANDAMENTO) ? CodedValues.SAD_DEFERIDA : adeBean.getStatusAutorizacaoDesconto().getSadCodigo();

                                        // Criar um novo com o restante do prazo na nova matricula
                                        novaAde = AutDescontoHome.create(novaSituacao, vcoCodigoNov, rseCodigoNov, corCodigo, usuCodigo,
                                                adeBean.getAdeIdentificador(), adeBean.getAdeIndice(), adeBean.getAdeCodReg(), adePrazo, Integer.valueOf(0),
                                                adeAnoMesIni, adeAnoMesFim, adeAnoMesIniRef, adeAnoMesFimRef, adeBean.getAdeVlr(), adeBean.getAdeVlrTac(),
                                                adeBean.getAdeVlrIof(), adeBean.getAdeVlrLiquido(), adeBean.getAdeVlrMensVinc(), adeBean.getAdeTaxaJuros(),
                                                adeBean.getAdeVlrSegPrestamista(), adeBean.getAdeTipoVlr(), adeBean.getAdeIntFolha(), adeBean.getAdeIncMargem(),
                                                Integer.valueOf(0), adeBean.getAdeCarenciaFinal(), adeDataHoraOcorrencia, adeBean.getAdeVlrSdoMov(),
                                                adeBean.getAdeVlrSdoRet(), adeBean.getAdeBanco(), adeBean.getAdeAgencia(), adeBean.getAdeConta(), adeBean.getAdeTipoTaxa(),
                                                adeBean.getAdeVlrPercentual(), adeBean.getAdePeriodicidade(), cidCodigo);

                                        // Cria ocorrência de inclusão de reserva
                                        criaOcorrenciaADE(novaAde.getAdeCodigo(), CodedValues.TOC_TARIF_RESERVA, ApplicationResourcesHelper.getMessage("mensagem.informacao.ptf.inclusao.reserva", responsavel), new BigDecimal("0.00"), adeBean.getAdeVlr(), null, adeAnoMesIni, null, responsavel);
                                    } catch (CreateException ex) {
                                        criticas.add(ApplicationResourcesHelper.getMessage("mensagem.erro.consignacao.arg0.nao.possivel.criar.nova.ade", responsavel, adeNumero));
                                        adeCodigosTransferidos.remove(adeCodigo);
                                        continue;
                                    }

                                    try {
                                        // Cria relacionamento entre a consignação antiga e a nova
                                        RelacionamentoAutorizacaoHome.create(adeBean.getAdeCodigo(), novaAde.getAdeCodigo(), CodedValues.TNT_TRANSFERENCIA_CONTRATO, responsavel.getUsuCodigo());
                                    } catch (CreateException ex) {
                                        criticas.add(ApplicationResourcesHelper.getMessage("mensagem.erro.consignacao.arg0.nao.possivel.criar.novo.relacionamento", responsavel, adeNumero));
                                        adeCodigosTransferidos.remove(adeCodigo);
                                        continue;
                                    }

                                    // Grava log da transferência, registrando origem e destino da transferência
                                    LogDelegate log = new LogDelegate(responsavel, Log.TRANSFERENCIA_AUTORIZACAO, Log.CREATE, Log.LOG_INFORMACAO);
                                    log.setAutorizacaoDesconto(novaAde.getAdeCodigo());
                                    log.setRegistroServidor(rseCodigoNov);
                                    log.setVerbaConvenio(vcoCodigoNov);
                                    log.write();

                                    // Adiciona nova ade na lista de transferidos
                                    adeCodigosTransferidos.add(novaAde.getAdeCodigo());
                                    // Remove ade antiga na lista de transferidos
                                    adeCodigosTransferidos.remove(adeCodigo);
                                }

                                criaOcorrencia = true;
                            }
                        } else {
                            // O novo convênio existe, então atualiza o contrato
                            AutDesconto adeBean = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
                            adeBean.setRegistroServidor(RegistroServidorHome.findByPrimaryKey(rseCodigoNov));
                            adeBean.setVerbaConvenio(VerbaConvenioHome.findByPrimaryKey(vcoCodigoNov));
                            AutDescontoHome.update(adeBean);
                            criaOcorrencia = true;

                            // Grava log da transferência, registrando origem e destino da transferência
                            LogDelegate log = new LogDelegate(responsavel, Log.TRANSFERENCIA_AUTORIZACAO, Log.UPDATE, Log.LOG_INFORMACAO);
                            log.setAutorizacaoDesconto(adeCodigo);
                            log.setRegistroServidor(rseCodigoAnt);
                            log.setVerbaConvenio(vcoCodigoAnt);
                            log.setRegistroServidorDestino(rseCodigoNov);
                            log.setVerbaConvenioDestino(vcoCodigoNov);
                            log.write();
                        }

                        // Cria Ocorrencia de Informacao sobre a transferencia do contrato
                        if (criaOcorrencia) {
                            criaOcorrenciaADE(adeCodigo, tocCodigo, ocaObs, responsavel);
                        }

                    }
                    // DESENV-10310 : Faz a limpeza da session (por causa da incapacidade do hibernate).
                    batman.iterate();
                }
            }

            if (criaNovaAdeTransferencia) {
                // Para cada nova consignação criada na matrícula nova, verificar se o antigo possui relacionamento com consignações
                // movidas para a matrícula nova. Caso existam, atualizar o relacionamento para a nova consignação criada.

                ObtemRelacionamentoInconsistenteTransferenciaQuery query = new ObtemRelacionamentoInconsistenteTransferenciaQuery();
                List<TransferObject> inconsistentes = query.executarDTO();

                for (TransferObject inconsistente : inconsistentes) {
                    String adeCodigoOrigem = (String) inconsistente.getAttribute("origem_atual");
                    String adeCodigoDestino = (String) inconsistente.getAttribute("destino_atual");
                    String tntCodigo = (String) inconsistente.getAttribute("tnt_codigo");
                    String novaOrigem = (String) inconsistente.getAttribute("nova_origem");
                    String novaDestino = (String) inconsistente.getAttribute("novo_destino");

                    RelacionamentoAutorizacaoId radIdInconsistente = new RelacionamentoAutorizacaoId(adeCodigoOrigem, adeCodigoDestino, tntCodigo);
                    RelacionamentoAutorizacao relacionamentoInconsistente = RelacionamentoAutorizacaoHome.findByPrimaryKey(radIdInconsistente);

                    // Removemos a antiga relacionamento.
                    RelacionamentoAutorizacaoHome.remove(relacionamentoInconsistente);

                    // Analisamos qual ade foi alterada nesse momento.
                    if (!TextHelper.isNull(novaOrigem)) {
                        relacionamentoInconsistente.setAdeCodigoOrigem(novaOrigem);
                    } else if(!TextHelper.isNull(novaDestino)) {
                        relacionamentoInconsistente.setAdeCodigoDestino(novaDestino);
                    }

                    // Bom esse update é uma pog porque o RelacionamentoAutorizacao pode ter mais campos, o update no fundo vai criar um novo objeto
                    // Porque nosso update utiliza o session.merge
                    // TODO: Criar no RelacionamentoAutorizacaoHome um create com todos os parametros para evitar essa confussão com o update.
                    RelacionamentoAutorizacaoHome.update(relacionamentoInconsistente);
                }
            }

            if (verificaRelacionamentos) {
                // Pesquisa se a transferência entre o rseCodigoAnt e o rseCodigoNov gerou
                // alguma inconsistência com relação aos contratos em relacionamento, já que
                // naturezas podem estar configuradas para não permitir exportação, e os
                // bloquieos de verba e serviço também podem impedir a transferência
                List<String> adeInconsistentes = new ArrayList<>();
                ListaRelAutorizacaoInconsTransfQuery relIncosQuery = new ListaRelAutorizacaoInconsTransfQuery();
                relIncosQuery.rseCodigoAnt = rseCodigoAnt;
                relIncosQuery.rseCodigoNov = rseCodigoNov;
                // Pesquisa o antigo na origem do relacionamento
                relIncosQuery.origem = true;
                adeInconsistentes.addAll(relIncosQuery.executarLista());
                // Pesquisa o antigo no destino do relacionamento
                relIncosQuery.origem = false;
                adeInconsistentes.addAll(relIncosQuery.executarLista());

                // Caso existam contratos inconsistentes, então executa novamente a rotina de transferência, porém
                // sem validar bloqueios de convênio e verba, e sem respeitar a configuração da natureza,
                // já que a query de listagem acima não possui esta cláusula.
                if (!adeInconsistentes.isEmpty()) {
                    criticas.addAll(transfereAde(adeInconsistentes, rseCodigoNov, rseCodigoAnt, rseMatriculaAntiga, orgIdentificadorAntigo, false, true, responsavel));
                }
            }

            // Verifica caso TPC_VALIDA_QTD_CONTRATOS_TRANSFERENCIA=S se a consignação ultrapassa algum dos limites de contrato
            if (ParamSist.paramEquals(CodedValues.TPC_VALIDA_QTD_CONTRATOS_TRANSFERENCIA, CodedValues.TPC_SIM, responsavel)) {
                for (String adeCodigo : adeCodigosTransferidos) {
                    LOG.debug("VALIDANDO LIMITES NA TRANSFERÊNCIA DE CONTRATO");
                    try {
                        LOG.debug("VERIFICA LIMITES DO CONTRATO '" + adeCodigo + "'");
                        verificaLimiteAoTransferirContrato(adeCodigo, responsavel);
                    } catch (AutorizacaoControllerException e) {
                        AutDesconto adeBean = AutDescontoHome.findByPrimaryKey(adeCodigo);
                        String rseCodigo = adeBean.getRegistroServidor().getRseCodigo();
                        String sadCodigo = adeBean.getStatusAutorizacaoDesconto().getSadCodigo();

                        LOG.debug("CONTRATO '" + adeCodigo + "' FORA DOS LIMITES PARA O REGISTRO SERVIDOR '" + rseCodigo + "' SERÁ SUSPENSO. " + e.getMessage());
                        try {
                            // Contrato em estoque pode ser suspenso por consignante ou suporte
                            boolean suspendeEstoque = (responsavel.isCseSup() || responsavel.isCsa()) && (sadCodigo.equals(CodedValues.SAD_ESTOQUE) || sadCodigo.equals(CodedValues.SAD_ESTOQUE_MENSAL) || sadCodigo.equals(CodedValues.SAD_ESTOQUE_NAO_LIBERADO));

                            if (!sadCodigo.equals(CodedValues.SAD_DEFERIDA) && !sadCodigo.equals(CodedValues.SAD_EMANDAMENTO) && !suspendeEstoque) {
                                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.erro.autorizacao.nao.pode.ser.suspensa.situacao.atual.dela.nao.permite.esta.operacao", responsavel, adeBean.getStatusAutorizacaoDesconto().getSadDescricao()));
                                continue;
                            }

                            // Suspende consignação que extrapola algum dos limites de contrato
                            suspenderConsignacaoController.suspender(adeCodigo, null, null, responsavel);
                            criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, e.getMessage(), responsavel);

                            LOG.debug("CONTRATO '" + adeCodigo + "' SUSPENSO PARA O REGISTRO SERVIDOR '" + rseCodigo + "'");
                        } catch (AutorizacaoControllerException e1) {
                            LOG.debug("CONTRATO NÃO SERÁ SUSPENSO PORQUE NÃO FOI POSSÍVEL REALIZAR A OPERAÇÃO '" + adeCodigo + "' PARA O REGISTRO SERVIDOR '" + rseCodigo + "'. " + e1.getMessage());
                        }
                    }
                    // Faz a limpeza da session (por causa da incapacidade do hibernate).
                    batman.iterate();
                }
            }

            return criticas;
        } catch (HQueryException | LogControllerException | UpdateException | FindException | RemoveException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Transfere uma lista de contratos entre os Servidores. Os contratos relacionados aos que
     * estao na lista tambem devem ser transferidos. Depois verifica se ficou alguma inconsistencia
     * nos relacionamentos de provisionamento de margem.
     * @param adeCodigoList Lista inicial de contratos a serem transferidos.
     * @param rseCodigoOrigem Codigo do registro servidor de origem dos contratos.
     * @param rseCodigoDestino Codigo do registro servidor destinatario dos contratos.
     * @param rseMatriculaOrigem Matricula do registro servidor de origem.
     * @param orgIdentificadorOrigem Orgao do registro servidor de origem.
     * @param responsavel Responsavel pela transacao.
     * @throws AutorizacaoControllerException Excecao padrao desse Facade.
     */
    @Override
    public boolean transfereAdeServidores(List<String> adeCodigoList, String rseCodigoOrigem,
            String rseCodigoDestino, String rseMatriculaOrigem, String orgIdentificadorOrigem, CustomTransferObject tipoMotivoOperacao, boolean comSenhaServidor, AcessoSistema responsavel) throws AutorizacaoControllerException {

        try {
            List<String> adeCodigoListFinal = obterListaAdeTransferencia(adeCodigoList, adeCodigoList, responsavel);

            VerificaAdeProvisionamentoQuery chkProvisionamento = new VerificaAdeProvisionamentoQuery();
            chkProvisionamento.adeCodigos = adeCodigoListFinal;

            List<TransferObject> adesVerificados = chkProvisionamento.executarDTO();

            boolean possuiProvisionamento = false;
            for (TransferObject adesVerificado: adesVerificados) {
                String svcCodigoOrigem = (String) adesVerificado.getAttribute("SVC_CODIGO_ORIGEM");
                String svcCodigoDestino = (String) adesVerificado.getAttribute("SVC_CODIGO_DESTINO");

                if (!TextHelper.isNull(svcCodigoOrigem) || !TextHelper.isNull(svcCodigoDestino)) {
                    possuiProvisionamento = true;
                    break;
                }
            }

            // faz a validação de provisionamento apenas se algum dos contratos a transferir for de serviço de provisionamento
            if (possuiProvisionamento) {
                // Verifica se existe algum relacionamento para provisionamento de margem inconsistente.
                try {
                    verificaProvisionamentoMargem(rseCodigoOrigem, adeCodigoListFinal, true, responsavel);
                } catch (AutorizacaoControllerException ex) {
                    throw new AutorizacaoControllerException("mensagem.erro.servidor.origem.nao.possui.valor.provisionado.suficiente.para.lancamentos", responsavel, ex);
                }

                try {
                    verificaProvisionamentoMargem(rseCodigoDestino, adeCodigoListFinal, false, responsavel);
                } catch (AutorizacaoControllerException ex) {
                    throw new AutorizacaoControllerException("mensagem.erro.servidor.destino.nao.possui.valor.provisionado.suficiente.para.lancamentos", responsavel, ex);
                }
            }

            // Transfere os contratos
            List<String> criticas = transfereAde(adeCodigoListFinal, rseCodigoDestino, rseCodigoOrigem, rseMatriculaOrigem, orgIdentificadorOrigem, false, false, responsavel);
            if (criticas != null && criticas.size() > 0) {
            	// Se tem alguma crítica, levanta exceção com as mensagens de erro
            	throw AutorizacaoControllerException.byMessage(TextHelper.join(criticas, "<br>"));
            }

            if (tipoMotivoOperacao != null) {
                for (String adeCodigo: adeCodigoListFinal) {
                    tipoMotivoOperacao.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                    tipoMotivoOperacaoController.gravarMotivoOperacaoConsignacao(tipoMotivoOperacao, responsavel);
                }
            }
            if (comSenhaServidor) {
                for (String adeCodigo: adeCodigoListFinal) {
                    criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AUTORIZACAO_VIA_SENHA_SERVIDOR, ApplicationResourcesHelper.getMessage("mensagem.informacao.autorizacao.pela.senha.do.servidor", responsavel), responsavel);
                }
            }

            return adeCodigoListFinal.size() > adeCodigoList.size();

        } catch (AutorizacaoControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(ex);
        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void transfereAdeNovoOrgao(String rseCodigo, String orgCodigoNovo, String tmoCodigo, String obs, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            List<AutDesconto> ades = AutDescontoHome.findByRseCodigo(rseCodigo);
            if (ades != null && !ades.isEmpty()) {
                for (AutDesconto ade : ades) {
                    String vcoCodigoOld = ade.getVerbaConvenio().getVcoCodigo();

                    // Busca o novo convênio e verba convênio pela CSA/SVC antigos e ORG novo
                    String svcCodigo = ade.getVerbaConvenio().getConvenio().getServico().getSvcCodigo();
                    String csaCodigo = ade.getVerbaConvenio().getConvenio().getConsignataria().getCsaCodigo();
                    Convenio cnvNovo = ConvenioHome.findByChave(svcCodigo, csaCodigo, orgCodigoNovo);
                    VerbaConvenio vcoNovo = VerbaConvenioHome.findAtivoByConvenio(cnvNovo.getCnvCodigo());

                    // Atualiza o verba convenio da consignação
                    ade.setVerbaConvenio(vcoNovo);
                    AutDescontoHome.update(ade);

                    // Cria ocorrência de informação na consignação registrando a alteração de órgão
                    criaOcorrenciaADE(ade.getAdeCodigo(), CodedValues.TOC_INFORMACAO, obs, tmoCodigo, responsavel);

                    // Grava log da transferência, registrando origem e destino da transferência
                    LogDelegate log = new LogDelegate(responsavel, Log.TRANSFERENCIA_AUTORIZACAO, Log.UPDATE, Log.LOG_INFORMACAO);
                    log.setAutorizacaoDesconto(ade.getAdeCodigo());
                    log.setRegistroServidor(rseCodigo);
                    log.setVerbaConvenio(vcoCodigoOld);
                    log.setVerbaConvenioDestino(vcoNovo.getVcoCodigo());
                    log.write();
                }
            }
        } catch (FindException | UpdateException | LogControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Metodo que obtem recursivamente os contratos relacionados para transferencia.
     * @param adeCodigoListRel Lista de contratos cujos relacionamentos devem ser pesquisados.
     * @param adeCodigoList Lista de contratos a serem transferidos.
     * @param responsavel Responsavel pela transacao.
     * @return Lista final de contratos a serem transferidos.
     * @throws AutorizacaoControllerException Excecao padrao desse Facade.
     */
    private List<String> obterListaAdeTransferencia(List<String> adeCodigoListRel, List<String> adeCodigoList, AcessoSistema responsavel) throws AutorizacaoControllerException {

        List<String> listaFinal = new ArrayList<>(adeCodigoList);

        try {
            // Pesquisa relacionamentos no banco permitindo repeticao
            List<TransferObject> listaBanco = pesquisarConsignacaoRelacionamento(adeCodigoListRel, responsavel);

            // Cria uma lista com os relacionamentos sem repeticao
            List<String> relacionamentos = new ArrayList<>();
            for (TransferObject ades : listaBanco) {
                String ade = ades.getAttribute(Columns.RAD_ADE_CODIGO_ORIGEM).toString();
                if (!listaFinal.contains(ade)) {
                    relacionamentos.add(ade);
                    listaFinal.add(ade);
                }
                ade = ades.getAttribute(Columns.RAD_ADE_CODIGO_DESTINO).toString();
                if (!listaFinal.contains(ade)) {
                    relacionamentos.add(ade);
                    listaFinal.add(ade);
                }
            }

            // Pesquisa relacionamentos dos relacionamentos recursivamente
            if (!relacionamentos.isEmpty()) {
                return obterListaAdeTransferencia(relacionamentos, listaFinal, responsavel);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        return listaFinal;
    }

    @Override
    public List<TransferObject> pesquisarConsignacaoRelacionamento(List<String> adeCodigoList, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            ListaConsignacaoRelacionamentoQuery query = new ListaConsignacaoRelacionamentoQuery();
            query.adeCodigoList = adeCodigoList;
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> distribuirConsignacoesPorServicos(String svcCodigoOrigem, List<String> svcCodigosDestino, List<String> csaCodigos, String rseMatricula, String serCpf, String tmoCodigo, String ocaObs, boolean validar, AcessoSistema responsavel) throws AutorizacaoControllerException {
        List<TransferObject> relatorioFinal = new ArrayList<>();
        String svcDescricaoOrigem = null;

        // Valida se o serviço de origem foi informado, pois é obrigatório
        if (TextHelper.isNull(svcCodigoOrigem)) {
            throw new AutorizacaoControllerException("mensagem.distribuir.consignacoes.selecione.servico.origem", responsavel);
        }

        // Valida se 2 ou mais serviços de destino foram informados
        if (svcCodigosDestino == null || svcCodigosDestino.size() < 2) {
            throw new AutorizacaoControllerException("mensagem.distribuir.consignacoes.selecione.servico.destino", responsavel);
        }

        // Valida se os serviços de origem e destino são da mesma natureza
        try {
            List<String> svcCodigos = new ArrayList<>();
            svcCodigos.add(svcCodigoOrigem);
            svcCodigos.addAll(svcCodigosDestino);

            TransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.SVC_CODIGO, svcCodigos);

            ListaServicoQuery lstServicosQuery = new ListaServicoQuery();
            lstServicosQuery.svcCodigos = svcCodigos;
            List<TransferObject> servicos = lstServicosQuery.executarDTO();

            String nseCodigoOrigem = null;
            for (TransferObject servico : servicos) {
                if (svcCodigoOrigem.equals(servico.getAttribute(Columns.SVC_CODIGO))) {
                    svcDescricaoOrigem = (String) servico.getAttribute(Columns.SVC_DESCRICAO);
                    nseCodigoOrigem = (String) servico.getAttribute(Columns.NSE_CODIGO);
                    break;
                }
            }
            for (TransferObject servico : servicos) {
                String nseCodigoDestino = (String) servico.getAttribute(Columns.NSE_CODIGO);
                if (nseCodigoOrigem == null || nseCodigoDestino == null || !nseCodigoOrigem.equals(nseCodigoDestino)) {
                    throw new AutorizacaoControllerException("mensagem.erro.distribuir.consignacoes.servicos.natureza.diferente", responsavel);
                }
            }
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        try {
            Session session = SessionUtil.getSession();
            BatchManager batman = new BatchManager(session);

            // Lista os candidatos a distribuição
            ListaServidorVerbaComConsignacaoParaDistribuicaoQuery lstServidorVerbaQuery = new ListaServidorVerbaComConsignacaoParaDistribuicaoQuery();
            lstServidorVerbaQuery.svcCodigoOrigem = svcCodigoOrigem;
            lstServidorVerbaQuery.csaCodigos = csaCodigos;
            lstServidorVerbaQuery.rseMatricula = rseMatricula;
            lstServidorVerbaQuery.serCPF = serCpf;
            List<TransferObject> candidatos = lstServidorVerbaQuery.executarDTO();

            for (TransferObject candidato : candidatos) {
                String rseCodigo = candidato.getAttribute(Columns.RSE_CODIGO).toString();
                String vcoCodigo = candidato.getAttribute(Columns.VCO_CODIGO).toString();
                String orgCodigo = candidato.getAttribute(Columns.ORG_CODIGO).toString();
                String csaCodigo = candidato.getAttribute(Columns.CSA_CODIGO).toString();

                ListaConsignacaoParaDistribuicaoQuery lstConsignacaoQuery = new ListaConsignacaoParaDistribuicaoQuery();
                lstConsignacaoQuery.rseCodigo = rseCodigo;
                lstConsignacaoQuery.vcoCodigo = vcoCodigo;
                List<TransferObject> consignacoes = lstConsignacaoQuery.executarDTO();

                if (consignacoes != null && consignacoes.size() > 0) {
                    // Busca a verba convênios dos serviços de destino para a distribuição
                    ListaVerbaConvenioParaDistribuicaoQuery lstVerbasConvenioQuery = new ListaVerbaConvenioParaDistribuicaoQuery();
                    lstVerbasConvenioQuery.svcCodigosDestino = svcCodigosDestino;
                    lstVerbasConvenioQuery.orgCodigo = orgCodigo;
                    lstVerbasConvenioQuery.csaCodigo = csaCodigo;
                    List<TransferObject> lstVerbasConvenio = lstVerbasConvenioQuery.executarDTO();

                    if (lstVerbasConvenio != null && lstVerbasConvenio.size() > 0) {
                        for (int i = 0; i < consignacoes.size(); i++) {
                            TransferObject consignacao = consignacoes.get(i);
                            TransferObject verbaConvenio = lstVerbasConvenio.get(i % lstVerbasConvenio.size());
                            String svcCodigoDestino = verbaConvenio.getAttribute(Columns.SVC_CODIGO).toString();
                            String vcoCodigoDestino = verbaConvenio.getAttribute(Columns.VCO_CODIGO).toString();
                            String adeCodigo = consignacao.getAttribute(Columns.ADE_CODIGO).toString();
                            String observacao = null;

                            LOG.debug("ADE: " + adeCodigo);

                            if (consignacoes.size() == 1) {
                                observacao = ApplicationResourcesHelper.getMessage("mensagem.distribuir.consignacoes.unica.ade.mantida", responsavel, svcDescricaoOrigem);

                            } else if (lstVerbasConvenio.size() == 1) {
                                observacao = ApplicationResourcesHelper.getMessage("mensagem.distribuir.consignacoes.unico.svc.mantido", responsavel, svcDescricaoOrigem);

                            } else if (svcCodigoOrigem.equals(svcCodigoDestino)) {
                                observacao = ApplicationResourcesHelper.getMessage("mensagem.distribuir.consignacoes.multipla.ade.mantida", responsavel, svcDescricaoOrigem);

                            } else {
                                observacao = ApplicationResourcesHelper.getMessage("mensagem.distribuir.consignacoes.multipla.ade.movida", responsavel, svcDescricaoOrigem, verbaConvenio.getAttribute(Columns.SVC_DESCRICAO).toString());

                                if (!validar) {
                                    // Atualiza a consignação apontando para a verba convênio retornada pela pesquisa
                                    AutDesconto autDesconto = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
                                    autDesconto.setVerbaConvenio(session.getReference(VerbaConvenio.class, vcoCodigoDestino));
                                    AutDescontoHome.update(autDesconto);

                                    // Cria ocorrência de transferência da ADE
                                    String textoObservacao = observacao;
                                    if (!TextHelper.isNull(ocaObs)) {
                                        textoObservacao += " " + ocaObs;
                                    }
                                    criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, textoObservacao, tmoCodigo, responsavel);

                                    // Grava log da transferência, registrando origem e destino da transferência
                                    LogDelegate log = new LogDelegate(responsavel, Log.TRANSFERENCIA_AUTORIZACAO, Log.UPDATE, Log.LOG_INFORMACAO);
                                    log.setAutorizacaoDesconto(adeCodigo);
                                    log.setVerbaConvenio(vcoCodigo);
                                    log.setVerbaConvenioDestino(vcoCodigoDestino);
                                    log.write();
                                }
                            }

                            consignacao.setAttribute("OBS", observacao);
                            relatorioFinal.add(consignacao);
                            batman.iterate();
                        }
                    }
                }
            }

            return relatorioFinal;
        } catch (HQueryException | FindException | UpdateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (LogControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(ex);
        }
    }
}
