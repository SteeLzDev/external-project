package com.zetra.econsig.service.sdp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.DespesaIndividualControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.PermissionarioControllerException;
import com.zetra.econsig.exception.PlanoDescontoControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.ConsignatariaHome;
import com.zetra.econsig.persistence.entity.EnderecoConjHabitacional;
import com.zetra.econsig.persistence.entity.EnderecoConjuntoHabitacionalHome;
import com.zetra.econsig.persistence.entity.OcorrenciaPermissionario;
import com.zetra.econsig.persistence.entity.OcorrenciaPermissionarioHome;
import com.zetra.econsig.persistence.entity.Permissionario;
import com.zetra.econsig.persistence.entity.PermissionarioHome;
import com.zetra.econsig.persistence.entity.Plano;
import com.zetra.econsig.persistence.entity.PlanoHome;
import com.zetra.econsig.persistence.entity.PostoRegistroServidor;
import com.zetra.econsig.persistence.entity.PostoRegistroServidorHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.query.sdp.despesacomum.ListaDespesaComumEnderecoQuery;
import com.zetra.econsig.persistence.query.sdp.despesacomum.ObtemConvenioDespesaComumQuery;
import com.zetra.econsig.persistence.query.sdp.permissionario.ListaHistoricoPermissionarioQuery;
import com.zetra.econsig.persistence.query.sdp.permissionario.ListaPermissionarioByEnderecoQuery;
import com.zetra.econsig.persistence.query.sdp.permissionario.ListaPermissionarioQuery;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: PermissionarioControllerBean</p>
 * <p>Description: Session Bean para a operações relacionada a Permissionario.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class PermissionarioControllerBean implements PermissionarioController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(PermissionarioControllerBean.class);

    @Autowired
    private DespesaIndividualController despesaIndividualController;

    @Autowired
    private PlanoDescontoController planoDescontoController;

    @Autowired
    private ParametroController parametroController;

    private TransferObject setPermissionarioTO(Permissionario permissionario) {
        TransferObject result = new CustomTransferObject();
        result.setAttribute(Columns.PRM_CODIGO, permissionario.getPrmCodigo());
        result.setAttribute(Columns.PRM_CSA_CODIGO, permissionario.getConsignataria().getCsaCodigo());
        result.setAttribute(Columns.PRM_ECH_CODIGO, permissionario.getEnderecoConjHabitacional().getEchCodigo());
        result.setAttribute(Columns.PRM_COMPL_ENDERECO, permissionario.getPrmComplEndereco());
        result.setAttribute(Columns.PRM_DATA_CADASTRO, permissionario.getPrmDataCadastro());
        result.setAttribute(Columns.PRM_DATA_OCUPACAO, permissionario.getPrmDataOcupacao());
        result.setAttribute(Columns.PRM_DATA_DESOCUPACAO, permissionario.getPrmDataDesocupacao());
        result.setAttribute(Columns.PRM_EM_TRANSFERENCIA, permissionario.getPrmEmTransferencia());
        result.setAttribute(Columns.PRM_EMAIL, permissionario.getPrmEmail());
        result.setAttribute(Columns.PRM_TELEFONE, permissionario.getPrmTelefone());
        result.setAttribute(Columns.PRM_RSE_CODIGO, permissionario.getRegistroServidor().getRseCodigo());

        return result;
    }

    @Override
    public String createPermissionario(TransferObject permissionario, AcessoSistema responsavel) throws PermissionarioControllerException {
        try {
            String rseCodigo = permissionario.getAttribute(Columns.PRM_RSE_CODIGO).toString();
            String csaCodigo = responsavel.getCsaCodigo();
            String echCodigo = permissionario.getAttribute(Columns.PRM_ECH_CODIGO).toString();
            String prmTelefone = permissionario.getAttribute(Columns.PRM_TELEFONE).toString();
            String prmEmail = permissionario.getAttribute(Columns.PRM_EMAIL).toString();
            String prmComplEndereco = permissionario.getAttribute(Columns.PRM_COMPL_ENDERECO).toString();
            Date prmDataOcupacao = (Date) permissionario.getAttribute(Columns.PRM_DATA_OCUPACAO);
            Date prmDataDesocupacao = (Date) permissionario.getAttribute(Columns.PRM_DATA_DESOCUPACAO);
            String prmEmTransferencia = permissionario.getAttribute(Columns.PRM_EM_TRANSFERENCIA).toString();

            // Verifica se existe mais de um permissionário cadastrado para o mesmo registro servidor e consignatária
            Collection<Permissionario> permissionarios = PermissionarioHome.findAtivoByRseCodigoAndCsaCodigo(rseCodigo, csaCodigo);
            Boolean incluiDespesas = true;
            //se o registro servidor estiver alocado em outro endereço não fazer inclusão das despesas automáticas
            if (permissionarios != null && !permissionarios.isEmpty()) {
                incluiDespesas = false;
                if (permissionarios.size() > 1) {
                    throw new PermissionarioControllerException("mensagem.erro.permissionario.nao.pode.ocupar.mais.dois.imoveis", responsavel);
                }
            }

            // Verifica se o servidor está ativo
            RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKey(rseCodigo);
            if (!registroServidor.getStatusRegistroServidor().getSrsCodigo().equals(CodedValues.SRS_ATIVO)) {
                throw new PermissionarioControllerException("mensagem.permissionario.bloqueado.excluido", responsavel);
            }

            Permissionario bean = PermissionarioHome.create(rseCodigo, csaCodigo, echCodigo, prmTelefone, prmEmail, prmComplEndereco, prmDataOcupacao, prmDataDesocupacao, prmEmTransferencia);
            String prmCodigo = bean.getPrmCodigo();

            if (incluiDespesas) {
                // lançar despesas comuns automáticas para o permissionário
                lancaDespesaComumPermissionario(prmCodigo, echCodigo, rseCodigo, responsavel);
                // Lancamento de taxa de uso
                lancaTaxaUso(prmCodigo, prmDataOcupacao, responsavel);
            }

            // Cria ocorrência de inclusão de permissionário
            criaOcorrencia(prmCodigo, CodedValues.TOC_INCLUSAO_PERMISSIONARIO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ope.obs.inclusao.permissionario", responsavel), responsavel);

            LogDelegate logDelegate = new LogDelegate(responsavel, Log.PERMISSIONARIO, Log.CREATE, Log.LOG_INFORMACAO);
            logDelegate.setPermissionario(prmCodigo);
            logDelegate.getUpdatedFields(permissionario.getAtributos(), null);
            logDelegate.write();

            return prmCodigo;
        } catch (LogControllerException | CreateException | FindException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new PermissionarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (PermissionarioControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw ex;
        }
    }

    @Override
    public TransferObject findPermissionario(String prmCodigo, AcessoSistema responsavel) throws PermissionarioControllerException {
        try {
            ListaPermissionarioQuery query = new ListaPermissionarioQuery();
            query.prmCodigo = prmCodigo;
            query.retornaPrmExcluido = true;

            List<TransferObject> permissionarios = query.executarDTO();

            if (permissionarios == null || permissionarios.isEmpty()) {
                throw new PermissionarioControllerException("mensagem.erro.uso.incorreto.permissionario.nao.encontrado", responsavel);
            }

            LogDelegate logDelegate = new LogDelegate(responsavel, Log.PERMISSIONARIO, Log.FIND, Log.LOG_INFORMACAO);
            logDelegate.setPermissionario(prmCodigo);
            logDelegate.write();

            return permissionarios.get(0);

        } catch (LogControllerException | HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PermissionarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public TransferObject findPermissionarioAtivoByRseCodigo(String rseCodigo, AcessoSistema responsavel) throws PermissionarioControllerException {
        try {
            Permissionario permissionario = PermissionarioHome.findAtivoByRseCodigo(rseCodigo);

            LogDelegate logDelegate = new LogDelegate(responsavel, Log.PERMISSIONARIO, Log.FIND, Log.LOG_INFORMACAO);
            logDelegate.setPermissionario(permissionario.getPrmCodigo());
            logDelegate.setRegistroServidor(rseCodigo);
            logDelegate.write();

            return setPermissionarioTO(permissionario);
        } catch (FindException ex) {
            return null;
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PermissionarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void updatePermissionario(TransferObject permissionario, AcessoSistema responsavel) throws PermissionarioControllerException {
        updatePermissionario(permissionario, false, responsavel);
    }

    @Override
    public void updatePermissionario(TransferObject permissionario, boolean reativacao, AcessoSistema responsavel) throws PermissionarioControllerException {
        try {
            Permissionario prmBean = PermissionarioHome.findByPrimaryKey(permissionario.getAttribute(Columns.PRM_CODIGO).toString());
            String prmCodigo = prmBean.getPrmCodigo();
            LogDelegate log = new LogDelegate(responsavel, Log.PERMISSIONARIO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setPermissionario(prmCodigo);

            if (!prmBean.getConsignataria().getCsaCodigo().equals(responsavel.getCsaCodigo())) {
                throw new PermissionarioControllerException("mensagem.erro.permissionario.nao.pertence.consignataria", responsavel);
            }

            /* Compara a versão do cache com a passada por parâmetro */
            TransferObject permissionarioCache = setPermissionarioTO(prmBean);
            CustomTransferObject merge = log.getUpdatedFields(permissionario.getAtributos(), permissionarioCache.getAtributos());

            if (merge.getAtributos().containsKey(Columns.PRM_CSA_CODIGO)) {
                prmBean.setConsignataria(ConsignatariaHome.findByPrimaryKey((String) merge.getAttribute(Columns.PRM_CSA_CODIGO)));
            }
            if (merge.getAtributos().containsKey(Columns.PRM_DATA_OCUPACAO)) {
                prmBean.setPrmDataOcupacao((Timestamp) merge.getAttribute(Columns.PRM_DATA_OCUPACAO));
            }
            if (merge.getAtributos().containsKey(Columns.PRM_DATA_DESOCUPACAO)) {
                prmBean.setPrmDataDesocupacao((Timestamp) merge.getAttribute(Columns.PRM_DATA_DESOCUPACAO));
            }
            if (merge.getAtributos().containsKey(Columns.PRM_EM_TRANSFERENCIA)) {
                prmBean.setPrmEmTransferencia((String) merge.getAttribute(Columns.PRM_EM_TRANSFERENCIA));
            }
            if (merge.getAtributos().containsKey(Columns.PRM_EMAIL)) {
                prmBean.setPrmEmail((String) merge.getAttribute(Columns.PRM_EMAIL));
            }
            if (merge.getAtributos().containsKey(Columns.PRM_RSE_CODIGO)) {
                prmBean.setRegistroServidor(RegistroServidorHome.findByPrimaryKey((String) merge.getAttribute(Columns.PRM_RSE_CODIGO)));
            }
            if (merge.getAtributos().containsKey(Columns.PRM_TELEFONE)) {
                prmBean.setPrmTelefone((String) merge.getAttribute(Columns.PRM_TELEFONE));
            }
            if (merge.getAtributos().containsKey(Columns.PRM_ATIVO) && merge.getAttribute(Columns.PRM_ATIVO) != null) {
                prmBean.setPrmAtivo(Short.valueOf(merge.getAttribute(Columns.PRM_ATIVO).toString()));
            }

            PermissionarioHome.update(prmBean);

            // se é uma reativação, lança as despesas comuns automáticas e taxas de uso
            if (reativacao) {
                String echCodigo = (String) permissionario.getAttribute(Columns.PRM_ECH_CODIGO);
                String rseCodigo = (String) permissionario.getAttribute(Columns.PRM_RSE_CODIGO);
                // lançar despesas comuns automáticas para o permissionário
                lancaDespesaComumPermissionario(prmCodigo, echCodigo, rseCodigo, responsavel);
                // Lancamento de taxa de uso
                lancaTaxaUso(prmCodigo, prmBean.getPrmDataOcupacao(), responsavel);
            }

            // Cria ocorrência de alteração de permissionário
            criaOcorrencia(prmCodigo, CodedValues.TOC_ALTERACAO_PERMISSIONARIO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ope.obs.alteracao.permissionario", responsavel), responsavel);

            log.write();
        } catch (LogControllerException | FindException | UpdateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PermissionarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (PermissionarioControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw ex;
        }
    }

    @Override
    public void removePermissionario(String prmCodigo, Date prmDataDesocupacao, AcessoSistema responsavel) throws PermissionarioControllerException {
        try {
            // Faz exclusão lógica do permissionário
            Permissionario prmBean = PermissionarioHome.findByPrimaryKey(prmCodigo);

            if (prmBean.isExcluido()) {
                throw new PermissionarioControllerException("mensagem.erro.permissionario.ja.excluido", responsavel);
            }

            if (!prmBean.getConsignataria().getCsaCodigo().equals(responsavel.getCsaCodigo())) {
                throw new PermissionarioControllerException("mensagem.erro.permissionario.nao.pertence.consignataria", responsavel);
            }

            // Cancela possíveis despesas vinculadas ao permissionário
            despesaIndividualController.cancelaDespesasIndividuais(prmCodigo, responsavel);

            prmBean.setPrmAtivo(CodedValues.STS_INDISP);
            prmBean.setPrmDataDesocupacao(prmDataDesocupacao);

            PermissionarioHome.update(prmBean);

            // Cria ocorrência de exclusão de permissionário
            criaOcorrencia(prmCodigo, CodedValues.TOC_EXCLUSAO_PERMISSIONARIO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ope.obs.exclusao.permissionario", responsavel), responsavel);

            LogDelegate logDelegate = new LogDelegate(responsavel, Log.PERMISSIONARIO, Log.DELETE, Log.LOG_INFORMACAO);
            logDelegate.setPermissionario(prmCodigo);
            logDelegate.write();

            //Se existir outro permissionario alocado em um endereço lançar as despesas
            Collection<Permissionario> permissionarios = PermissionarioHome.findAtivoByRseCodigoAndCsaCodigo(prmBean.getRegistroServidor().getRseCodigo(), responsavel.getCsaCodigo());
            if (permissionarios != null && !permissionarios.isEmpty()) {
                Permissionario permissionario = permissionarios.iterator().next();
                // se o permissionário for mais novo do que o que foi excluído
                if(permissionario.getPrmDataCadastro().after(prmBean.getPrmDataCadastro())){
                    // lançar despesas comuns automáticas para o permissionário
                    lancaDespesaComumPermissionario(permissionario.getPrmCodigo(), permissionario.getEnderecoConjHabitacional().getEchCodigo(), permissionario.getRegistroServidor().getRseCodigo(), responsavel);
                    // Lancamento de taxa de uso
                    lancaTaxaUso(permissionario.getPrmCodigo(), permissionario.getPrmDataOcupacao(), responsavel);
                }
            }

        } catch (LogControllerException | FindException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PermissionarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (UpdateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PermissionarioControllerException("mensagem.erro.excluir.permissionario.dependencia", responsavel);
        } catch (DespesaIndividualControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PermissionarioControllerException("mensagem.erro.excluir.permissionario.arg0", responsavel, ex.getMessage());
        }
    }

    @Override
    public void movePermissionario(String rseCodigoOrigem, String rseCodigoDestino, AcessoSistema responsavel) throws PermissionarioControllerException {
        Permissionario prmBean = null;
        try {
            prmBean = PermissionarioHome.findByRseCodigo(rseCodigoOrigem);
        } catch (FindException ex) {
            // O registro servidor não possui permissionário
            return;
        }

        try {
            // Atualiza o registro servidor no permissionário
            prmBean.setRegistroServidor(RegistroServidorHome.findByPrimaryKey(rseCodigoDestino));
            PermissionarioHome.update(prmBean);

            // Cria ocorrência de alteração de permissionário
            criaOcorrencia(prmBean.getPrmCodigo(), CodedValues.TOC_ALTERACAO_PERMISSIONARIO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ope.obs.alteracao.permissionario", responsavel), responsavel);

            // Grava log da alteração
            LogDelegate log = new LogDelegate(responsavel, Log.PERMISSIONARIO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setPermissionario(prmBean.getPrmCodigo());
            log.setRegistroServidor(rseCodigoDestino);
            log.write();

        } catch (LogControllerException | FindException | UpdateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PermissionarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (PermissionarioControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw ex;
        }
    }

    @Override
    public int countPermissionarios(TransferObject criterio, AcessoSistema responsavel) throws PermissionarioControllerException {
        try {
            ListaPermissionarioQuery query = new ListaPermissionarioQuery();
            query.count = true;

            query.csaCodigo = responsavel.getCsaCodigo();

            if (criterio != null) {
                query.prmCodigo = (String) criterio.getAttribute(Columns.PRM_CODIGO);
                query.posCodigo = (String) criterio.getAttribute(Columns.POS_CODIGO);
                query.rseCodigo = (String) criterio.getAttribute(Columns.RSE_CODIGO);
                query.rseMatricula = (String) criterio.getAttribute(Columns.RSE_MATRICULA);
                query.serCpf = (String) criterio.getAttribute(Columns.SER_CPF);
                query.serNome = (String) criterio.getAttribute(Columns.SER_NOME);
                query.echCodigo = (String) criterio.getAttribute(Columns.ECH_CODIGO);
                query.echDescricao = (String) criterio.getAttribute(Columns.ECH_DESCRICAO);
                query.endereco = (String) criterio.getAttribute("ENDERECO");
                query.decDataRetroativa = (Date) criterio.getAttribute(Columns.DEC_DATA);
            }

            return query.executarContador();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PermissionarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstPermissionarios(TransferObject criterio, int offset, int size, AcessoSistema responsavel) throws PermissionarioControllerException {
        try {
            ListaPermissionarioQuery query = new ListaPermissionarioQuery();

            if (size != -1) {
                query.firstResult = offset;
                query.maxResults = size;
            }

            query.csaCodigo = responsavel.getCsaCodigo();

            if (criterio != null) {
                query.prmCodigo = (String) criterio.getAttribute(Columns.PRM_CODIGO);
                query.posCodigo = (String) criterio.getAttribute(Columns.POS_CODIGO);
                query.rseCodigo = (String) criterio.getAttribute(Columns.RSE_CODIGO);
                query.rseMatricula = (String) criterio.getAttribute(Columns.RSE_MATRICULA);
                query.serCpf = (String) criterio.getAttribute(Columns.SER_CPF);
                query.serNome = (String) criterio.getAttribute(Columns.SER_NOME);
                query.echCodigo = (String) criterio.getAttribute(Columns.ECH_CODIGO);
                query.echDescricao = (String) criterio.getAttribute(Columns.ECH_DESCRICAO);
                query.endereco = (String) criterio.getAttribute("ENDERECO");
                query.decDataRetroativa = (Date) criterio.getAttribute(Columns.DEC_DATA);
                if (!TextHelper.isNull(criterio.getAttribute(Columns.CSA_CODIGO))) {
                    query.csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
                }
            }

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PermissionarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    protected String criaOcorrencia(String prmCodigo, String tocCodigo, String opeObs, AcessoSistema responsavel) throws PermissionarioControllerException {
        try {
            OcorrenciaPermissionario ocorrencia = OcorrenciaPermissionarioHome.create(prmCodigo, tocCodigo, responsavel.getUsuCodigo(), responsavel.getIpUsuario(), opeObs);
            return ocorrencia.getOpeCodigo();
        } catch (CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new PermissionarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstHistoricoPermissionario(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws PermissionarioControllerException {
        try {
            ListaHistoricoPermissionarioQuery query = new ListaHistoricoPermissionarioQuery();
            if (offset != -1) {
                query.firstResult = offset;
            }
            if (count != -1) {
                query.maxResults = count;
            }
            query.prmCodigo = (String) criterio.getAttribute(Columns.OPE_PRM_CODIGO);
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PermissionarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int countOcorrenciaPermisionario(TransferObject criterio, AcessoSistema responsavel) throws PermissionarioControllerException {
        try {
            ListaHistoricoPermissionarioQuery query = new ListaHistoricoPermissionarioQuery();
            query.count = true;
            query.prmCodigo = (String) criterio.getAttribute(Columns.OPE_PRM_CODIGO);
            return query.executarContador();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PermissionarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void lancaDespesaComumPermissionario(String prmCodigo, String echCodigo, String rseCodigo, AcessoSistema responsavel) throws PermissionarioControllerException {
        try {
            // Lista despesas configuradas para desconto automático
            ListaDespesaComumEnderecoQuery query = new ListaDespesaComumEnderecoQuery();
            query.echCodigo = echCodigo;
            query.tppCodigo = CodedValues.TPP_DESCONTO_AUTOMATICO;
            query.pplValor = CodedValues.TPP_SIM;

            // retorna lista de despesas comuns ativas para o endereço
            List<TransferObject> lstDespesaComum = query.executarDTO();

            for (TransferObject despesaComum : lstDespesaComum) {
                String decCodigo = (String) despesaComum.getAttribute(Columns.DEC_CODIGO);
                String decIdentificador = !TextHelper.isNull(despesaComum.getAttribute(Columns.DEC_IDENTIFICADOR)) ? despesaComum.getAttribute(Columns.DEC_IDENTIFICADOR).toString() : "";
                ObtemConvenioDespesaComumQuery cnvQuery = new ObtemConvenioDespesaComumQuery();
                cnvQuery.decCodigo = decCodigo;
                String plaCodigo = (String) despesaComum.getAttribute(Columns.PLA_CODIGO);

                TransferObject cnv = cnvQuery.executarDTO().get(0);

                String cnvCodigo = (String) cnv.getAttribute(Columns.CNV_CODIGO);

                // recupera período atual
                String orgCodigo = (String) cnv.getAttribute(Columns.ORG_CODIGO);
                Date prazoIni = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
                Date prazoIniDespesa = (Date) despesaComum.getAttribute(Columns.DEC_DATA_INI);
                Date prazoFim = (Date) despesaComum.getAttribute(Columns.DEC_DATA_FIM);

                Integer adeCarencia = Integer.valueOf(0);
                // Se a data de início de despesa é maior que o período atual, então calcula a carência
                if (prazoIniDespesa.after(prazoIni)) {
                    adeCarencia = PeriodoHelper.getInstance().calcularCarencia(orgCodigo, prazoIniDespesa, null, responsavel);
                    prazoIni = prazoIniDespesa;
                }

                Integer novoPrazo = null;
                if (!TextHelper.isNull(despesaComum.getAttribute(Columns.DEC_PRAZO))) {
                    // Calcula o prazo em relação à diferença entre a data inicial e final.
                    novoPrazo = PeriodoHelper.getInstance().calcularPrazo(orgCodigo, prazoIni, prazoFim, null, responsavel);

                    // se a data de término da despesa comum é menor que o período atual, esta não é considerada para o novo permissionário
                    if (novoPrazo == 0) {
                        continue;
                    }
                }

                try {
                    Plano plano = PlanoHome.findByPrimaryKey(plaCodigo);

                    // Verifica se o plano está ativo
                    if (!plano.getPlaAtivo().equals(CodedValues.STS_ATIVO)) {
                        throw new PermissionarioControllerException("mensagem.erro.incluir.despesa.comum.plano.bloqueado", responsavel);
                    }

                    // Seleciona os parâmetros do plano
                    Map<String, String> parametrosPlano = parametroController.getParamPlano(plaCodigo, responsavel);

                    // Seleciona parâmetros do serviço
                    String svcCodigo = plano.getServico().getSvcCodigo();
                    ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

                    // verifica se a despesa é por posto e, caso seja, se o permissionário é do posto indicado na despesa
                    boolean descontoPosto = parametrosPlano.containsKey(CodedValues.TPP_DESCONTO_POR_POSTO) && parametrosPlano.get(CodedValues.TPP_DESCONTO_POR_POSTO).equals(CodedValues.PLANO_DESCONTO_POR_POSTO_SIM);
                    if (descontoPosto) {
                        // recupera o posto da despesa
                        String posCodigoDec = (String) despesaComum.getAttribute(Columns.DEC_POS_CODIGO);
                        // recupera o posto do permissionário
                        TransferObject permissionario = findPermissionario(prmCodigo, responsavel);
                        String posCodigoPrm = (String) permissionario.getAttribute(Columns.POS_CODIGO);
                        if (!posCodigoPrm.equals(posCodigoDec)) {
                            continue;
                        }
                    }

                    // Cadastro de indices
                    String indicePadrao = parametrosPlano.containsKey(CodedValues.TPP_INDICE_PLANO) && !TextHelper.isNull(parametrosPlano.get(CodedValues.TPP_INDICE_PLANO)) ? parametrosPlano.get(CodedValues.TPP_INDICE_PLANO).toString() : "";
                    // Setar dados consignação
                    BigDecimal adeVlr = null;

                    // Verifica se tem rateio por unidade
                    String tipoRateioPlano = parametrosPlano.containsKey(CodedValues.TPP_TIPO_RATEIO_PLANO) && !TextHelper.isNull(parametrosPlano.get(CodedValues.TPP_TIPO_RATEIO_PLANO)) ? parametrosPlano.get(CodedValues.TPP_TIPO_RATEIO_PLANO).toString() : "";
                    if (tipoRateioPlano.equals(CodedValues.PLANO_RATEIO_POR_UNIDADE)) {
                        // valor rateio
                        adeVlr = (BigDecimal) despesaComum.getAttribute(Columns.DEC_VALOR_RATEIO);
                    } else {
                        // valor despesa
                        adeVlr = (BigDecimal) despesaComum.getAttribute(Columns.DEC_VALOR);
                    }

                    Integer adePrazo = novoPrazo;
                    // TODO Índice não é setado na tela de criação de permissionário, por isso está sendo setado com o índice padrão
                    String adeIndice = indicePadrao;

                    // Validar parâmetros do plano
                    parametroController.validaParametrosPlanoDesconto(plaCodigo, svcCodigo, adeVlr, adePrazo, adeIndice, responsavel);

                    TransferObject despesaIndividual = new CustomTransferObject();
                    despesaIndividual.setAttribute(Columns.DEI_DEC_CODIGO, decCodigo);
                    despesaIndividual.setAttribute(Columns.DEI_PLA_CODIGO, plaCodigo);

                    ReservarMargemParametros margemParam = new ReservarMargemParametros();
                    margemParam.setAdePrazo(adePrazo);
                    margemParam.setAdeCarencia(adeCarencia);
                    margemParam.setComSerSenha(Boolean.FALSE);
                    margemParam.setAdeTipoVlr(paramSvcCse.getTpsTipoVlr());
                    margemParam.setAdeIntFolha(paramSvcCse.getTpsIntegraFolha());
                    margemParam.setAdeIncMargem(paramSvcCse.getTpsIncideMargem());
                    margemParam.setAdeIndice(adeIndice);
                    margemParam.setAdeIdentificador(decIdentificador);
                    margemParam.setValidar(Boolean.FALSE);
                    margemParam.setPermitirValidacaoTaxa(Boolean.TRUE);
                    margemParam.setSerAtivo(Boolean.TRUE);
                    margemParam.setCnvAtivo(Boolean.TRUE);
                    margemParam.setSerCnvAtivo(Boolean.TRUE);
                    margemParam.setSvcAtivo(Boolean.TRUE);
                    margemParam.setCsaAtivo(Boolean.TRUE);
                    margemParam.setOrgAtivo(Boolean.TRUE);
                    margemParam.setEstAtivo(Boolean.TRUE);
                    margemParam.setCseAtivo(Boolean.TRUE);
                    margemParam.setAcao("RESERVAR");
                    margemParam.setNomeResponsavel(responsavel.getUsuNome());
                    margemParam.setRseCodigo(rseCodigo);
                    margemParam.setAdeVlr(adeVlr);
                    margemParam.setCnvCodigo(cnvCodigo);

                    // lança despesa individual proporcional ao dia do período de inclusão
                    if (adeCarencia == 0 || DateHelper.getDay(DateHelper.getSystemDate()) != (PeriodoHelper.getInstance().getProximoDiaCorte(orgCodigo, responsavel) + 1)) {
                        boolean lancouProporcional = lancarDespesaIndividualProporcional(prmCodigo, plaCodigo, decCodigo, orgCodigo, adeVlr, margemParam, responsavel);
                        if (lancouProporcional) {
                            margemParam.setAdeCarencia(1);
                        }
                    }
                    despesaIndividual.setAttribute(Columns.DEI_PRM_CODIGO, prmCodigo);

                    despesaIndividualController.createDespesaIndividual(despesaIndividual, margemParam, responsavel);

                } catch (FindException | ParametroControllerException | DespesaIndividualControllerException ex) {
                    TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                    LOG.error(ex.getMessage(), ex);
                    throw new PermissionarioControllerException("mensagem.erro.incluir.permissionario.pendencias.arg0", responsavel, ex, ex.getMessage());
                } catch (PermissionarioControllerException ex) {
                    TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                    throw ex;
                }
            }
        } catch (HQueryException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PermissionarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (PeriodoException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PermissionarioControllerException("mensagem.erro.incluir.despesa.comum.periodo.nao.encontrado", responsavel, ex);
        }
    }

    /**
     * lança uma despesa individual para despesas comuns automáticas proporcional ao dia de inclusão do permissionário até o próximo dia de corte.
     * @param prmCodigo
     * @param plaCodigo
     * @param decCodigo
     * @param orgCodigo
     * @param adeVlr
     * @param margemParam
     * @param responsavel
     * @return false se não ocorrer lançamento proporcional, true caso contrário
     * @throws PermissionarioControllerException
     */
    private boolean lancarDespesaIndividualProporcional (String prmCodigo, String plaCodigo, String decCodigo, String orgCodigo, BigDecimal adeVlr, ReservarMargemParametros margemParam, AcessoSistema responsavel) throws PermissionarioControllerException {
        Date hoje = DateHelper.getSystemDate();
        Date dataFimPeriodo = null;
        try {
            dataFimPeriodo = PeriodoHelper.getInstance().getDataFimPeriodoAtual(orgCodigo, responsavel);
        } catch (PeriodoException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PermissionarioControllerException ("mensagem.erro.recuperar.proximo.dia.corte", responsavel);
        }

        // diferença em dias entre o próximo dia de corte e o dia atual para calcular o valor proporcional
        // da despesa individual
        BigDecimal proporcao = new BigDecimal(DateHelper.dayDiff(dataFimPeriodo, hoje));

        BigDecimal mes = new BigDecimal("30");

        // o valor original do lançamento é dividido por 30 (mês) e multiplicado pela proporção para
        // se alcançar o valor do lançamento proporcional.
        BigDecimal adeVlrProporcional = adeVlr.divide(mes, 2, java.math.RoundingMode.DOWN).multiply(proporcao);

        // se o valor proporcional for 0 ou igual à parcela do lançamento, significa que este lançamento
        // foi executado no dia de corte ou no dia após, não sendo necessário um lançamento proporcional.
        if (adeVlrProporcional.compareTo(new BigDecimal("0")) == 0 || adeVlrProporcional.compareTo(adeVlr) == 0) {
            return false;
        }

        TransferObject despesaIndividual = new CustomTransferObject();
        despesaIndividual.setAttribute(Columns.DEI_DEC_CODIGO, decCodigo);
        despesaIndividual.setAttribute(Columns.DEI_PLA_CODIGO, plaCodigo);

        ReservarMargemParametros margemParamProporcional = new ReservarMargemParametros();
        margemParamProporcional.setAdePrazo(1);
        margemParamProporcional.setAdeCarencia(0);
        margemParamProporcional.setComSerSenha(Boolean.FALSE);
        margemParamProporcional.setAdeTipoVlr(margemParam.getAdeTipoVlr());
        margemParamProporcional.setAdeIntFolha(margemParam.getAdeIntFolha());
        margemParamProporcional.setAdeIncMargem(margemParam.getAdeIncMargem());
        margemParamProporcional.setAdeIndice(margemParam.getAdeIndice());
        margemParamProporcional.setAdeIdentificador(margemParam.getAdeIdentificador());
        margemParamProporcional.setValidar(Boolean.FALSE);
        margemParamProporcional.setPermitirValidacaoTaxa(Boolean.TRUE);
        margemParamProporcional.setSerAtivo(Boolean.TRUE);
        margemParamProporcional.setCnvAtivo(Boolean.TRUE);
        margemParamProporcional.setSerCnvAtivo(Boolean.TRUE);
        margemParamProporcional.setSvcAtivo(Boolean.TRUE);
        margemParamProporcional.setCsaAtivo(Boolean.TRUE);
        margemParamProporcional.setOrgAtivo(Boolean.TRUE);
        margemParamProporcional.setEstAtivo(Boolean.TRUE);
        margemParamProporcional.setCseAtivo(Boolean.TRUE);
        margemParamProporcional.setAcao("RESERVAR");
        margemParamProporcional.setNomeResponsavel(responsavel.getUsuNome());
        margemParamProporcional.setRseCodigo(margemParam.getRseCodigo());
        margemParamProporcional.setAdeVlr(adeVlrProporcional);
        margemParamProporcional.setCnvCodigo(margemParam.getCnvCodigo());

        despesaIndividual.setAttribute(Columns.DEI_PRM_CODIGO, prmCodigo);

        try {
            despesaIndividualController.createDespesaIndividual(despesaIndividual, margemParamProporcional, responsavel);
        } catch (DespesaIndividualControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PermissionarioControllerException("mensagem.erro.incluir.permissionario.pendencias.arg0", responsavel, ex, ex.getMessage());
        }

        return true;
    }

    private void lancaTaxaUso(String prmCodigo, Date prmDataOcupacao, AcessoSistema responsavel) throws PermissionarioControllerException {
        try {
            // Verifica o permissionário não foi encontrado
            Permissionario permissionario = PermissionarioHome.findByPrimaryKey(prmCodigo);
            if (permissionario == null) {
                throw new PermissionarioControllerException("mensagem.erro.uso.incorreto.permissionario.nao.encontrado", responsavel);
            }

            String csaCodigo = responsavel.getCsaCodigo();
            String echCodigo = permissionario.getEnderecoConjHabitacional().getEchCodigo();
            String rseCodigo = permissionario.getRegistroServidor().getRseCodigo();

            // Verifica se o servidor está ativo
            RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKey(rseCodigo);
            if (!registroServidor.getStatusRegistroServidor().getSrsCodigo().equals(CodedValues.SRS_ATIVO)) {
                throw new PermissionarioControllerException("mensagem.permissionario.bloqueado.excluido", responsavel);
            }

            EnderecoConjHabitacional endereco = EnderecoConjuntoHabitacionalHome.findByPrimaryKey(echCodigo);
            if (!permissionario.getEnderecoConjHabitacional().getEchCodigo().equals(echCodigo)) {
                throw new PermissionarioControllerException("mensagem.erro.endereco.conjunto.habitacional.nao.encontrado", responsavel);
            }

            // Verifica se a consignatária do responsável é a mesma do permissionário e do endereço informado
            if (TextHelper.isNull(csaCodigo) || !permissionario.getConsignataria().getCsaCodigo().equals(csaCodigo) || !endereco.getConsignataria().getCsaCodigo().equals(csaCodigo)) {
                throw new PermissionarioControllerException("mensagem.erro.incluir.permissionario.usuario.sem.permissao", responsavel);
            }

            // Verifica se existe taxa de uso para ser lançada
            List<TransferObject> lstPlanoTaxaUso = planoDescontoController.lstPlanoDescontoTaxaUso(csaCodigo, null, rseCodigo, responsavel);

            // Se não existe plano de data de uso, levanta exceção
            if (lstPlanoTaxaUso == null || lstPlanoTaxaUso.isEmpty()) {
                throw new PermissionarioControllerException("mensagem.erro.incluir.permissionario.plano.taxa.uso.ausente", responsavel);
            }

            // Lançamento é realizado somente para a primeira taxa de uso encontrada
            TransferObject planoTO = lstPlanoTaxaUso.get(0);
            String plaCodigo = planoTO.getAttribute(Columns.PLA_CODIGO).toString();
            String svcCodigo = planoTO.getAttribute(Columns.SVC_CODIGO).toString();
            String cnvCodigo = planoTO.getAttribute(Columns.CNV_CODIGO).toString();

            // Verifica se o registro servidor está associado ao posto
            String posCodigo = null;
            try {
                posCodigo = registroServidor.getPostoRegistroServidor().getPosCodigo();
                if (TextHelper.isNull(posCodigo)) {
                    throw new PermissionarioControllerException("mensagem.erro.incluir.permissionario.posto.ausente", responsavel);
                }
            } catch (Exception e) {
                throw new PermissionarioControllerException("mensagem.erro.incluir.permissionario.posto.ausente", responsavel);
            }

            PostoRegistroServidor posto = PostoRegistroServidorHome.findByPrimaryKey(posCodigo);
            BigDecimal valorSoldo = posto.getPosVlrSoldo();

            // Calculo do valor da taxa de uso deve ser considerado o posto do permissionario
            // Se o endereço for condomínio, utiliza a taxa de uso de condomínio
            BigDecimal taxaUso = endereco.getEchCondominio().equals(CodedValues.TPC_SIM) ? posto.getPosPercTxUsoCond() : posto.getPosPercTxUso();

            BigDecimal valorTaxaUso = valorSoldo.multiply(taxaUso).divide(new BigDecimal(100), 2, java.math.RoundingMode.DOWN);
            if (valorTaxaUso.compareTo(BigDecimal.ZERO) < 0) {
                throw new PermissionarioControllerException("mensagem.erro.incluir.permissionario.erro.calculo.taxa.uso", responsavel);
            }

            // Seleciona parâmetros do serviço
            ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            // Cadastro de indices
            Map<String, String> parametrosPlano = parametroController.getParamPlano(plaCodigo, responsavel);
            String indicePadrao = parametrosPlano.containsKey(CodedValues.TPP_INDICE_PLANO) && !TextHelper.isNull(parametrosPlano.get(CodedValues.TPP_INDICE_PLANO)) ? parametrosPlano.get(CodedValues.TPP_INDICE_PLANO).toString() : "";

            // Setar dados consignação
            Integer adeCarencia = Integer.valueOf(0);
            Integer adePrazo = null; // TODO Indeterminado?
            // TODO Índice não é setado na tela de criação de permissionário, por isso está sendo setado com o índice padrão
            String adeIndice = indicePadrao;

            // Validar parâmetros do plano
            parametroController.validaParametrosPlanoDesconto(plaCodigo, svcCodigo, valorTaxaUso, adePrazo, adeIndice, responsavel);

            // Lança Taxa de Uso
            TransferObject despesaIndividual = new CustomTransferObject();
            despesaIndividual.setAttribute(Columns.DEI_PLA_CODIGO, plaCodigo);
            despesaIndividual.setAttribute(Columns.DEI_PRM_CODIGO, prmCodigo);

            ReservarMargemParametros margemParam = new ReservarMargemParametros();
            margemParam.setAdePrazo(adePrazo);
            margemParam.setAdeCarencia(adeCarencia);
            margemParam.setComSerSenha(Boolean.FALSE);
            margemParam.setAdeTipoVlr(paramSvcCse.getTpsTipoVlr());
            margemParam.setAdeIntFolha(paramSvcCse.getTpsIntegraFolha());
            margemParam.setAdeIncMargem(paramSvcCse.getTpsIncideMargem());
            margemParam.setAdeIndice(adeIndice);
            margemParam.setAdeIdentificador("");
            margemParam.setValidar(Boolean.FALSE);
            margemParam.setPermitirValidacaoTaxa(Boolean.TRUE);
            margemParam.setSerAtivo(Boolean.TRUE);
            margemParam.setCnvAtivo(Boolean.TRUE);
            margemParam.setSerCnvAtivo(Boolean.TRUE);
            margemParam.setSvcAtivo(Boolean.TRUE);
            margemParam.setCsaAtivo(Boolean.TRUE);
            margemParam.setOrgAtivo(Boolean.TRUE);
            margemParam.setEstAtivo(Boolean.TRUE);
            margemParam.setCseAtivo(Boolean.TRUE);
            margemParam.setAcao("RESERVAR");
            margemParam.setNomeResponsavel(responsavel.getUsuNome());
            margemParam.setRseCodigo(rseCodigo);
            margemParam.setAdeVlr(valorTaxaUso);
            margemParam.setCnvCodigo(cnvCodigo);

            // lança taxa de uso proporcional do dia de ocupação em relação ao período de inclusão
            String orgCodigo = registroServidor.getOrgao().getOrgCodigo();

            // Se data de ocupação for futura ou não for informada, assume o dia de hoje.
            prmDataOcupacao = (prmDataOcupacao != null && DateHelper.dayDiff(prmDataOcupacao, DateHelper.getSystemDate()) < 0) ? prmDataOcupacao : DateHelper.getSystemDate();

            if (adeCarencia == 0) {
                lancaTaxaUsoProporcional(prmCodigo, plaCodigo, orgCodigo, valorTaxaUso, margemParam, prmDataOcupacao, responsavel);
            }

            despesaIndividualController.createDespesaIndividual(despesaIndividual, margemParam, responsavel);

        } catch (FindException | ParametroControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PermissionarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (PlanoDescontoControllerException | DespesaIndividualControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PermissionarioControllerException("mensagem.erro.incluir.permissionario.arg0", responsavel, ex, ex.getMessage());
        } catch (PermissionarioControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw ex;
        }
    }

    private boolean lancaTaxaUsoProporcional (String prmCodigo, String plaCodigo, String orgCodigo, BigDecimal vlrTaxa, ReservarMargemParametros margemParam, Date prmDataOcupacao, AcessoSistema responsavel) throws PermissionarioControllerException {
        Date dataOcupacao = prmDataOcupacao != null ? prmDataOcupacao : DateHelper.getSystemDate();
        Date dataFimPeriodo = null;
        try {
            dataFimPeriodo = PeriodoHelper.getInstance().getDataFimPeriodoAtual(orgCodigo, responsavel);
        } catch (PeriodoException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PermissionarioControllerException ("mensagem.erro.recuperar.proximo.dia.corte", responsavel);
        }

        // diferença em dias entre o próximo dia de corte e o dia atual para calcular o valor proporcional
        // da despesa individual
        BigDecimal proporcao = new BigDecimal(DateHelper.dayDiff(dataFimPeriodo, dataOcupacao));

        BigDecimal mes = new BigDecimal("30");

        // o valor original do lançamento é dividido por 30 (mês) e multiplicado pela proporção para
        // se alcançar o valor do lançamento proporcional.
        BigDecimal vlrTaxaUsoProporcional = vlrTaxa.divide(mes, 2, java.math.RoundingMode.DOWN).multiply(proporcao);

        // Valor da taxa proporcional não é criado se hoje é dia de corte ou se a ocupação é superior a um mês no passado
        if (vlrTaxaUsoProporcional.compareTo(new BigDecimal("0")) == 0 || vlrTaxaUsoProporcional.compareTo(vlrTaxa) >= 0) {
            return false;
        }

        // Lança Taxa de Uso Proporcional
        TransferObject despesaIndividual = new CustomTransferObject();
        despesaIndividual.setAttribute(Columns.DEI_PLA_CODIGO, plaCodigo);
        despesaIndividual.setAttribute(Columns.DEI_PRM_CODIGO, prmCodigo);

        ReservarMargemParametros margemParamProporcional = new ReservarMargemParametros();
        margemParamProporcional.setAdePrazo(1);
        margemParamProporcional.setAdeCarencia(0);
        margemParamProporcional.setComSerSenha(Boolean.FALSE);
        margemParamProporcional.setAdeTipoVlr(margemParam.getAdeTipoVlr());
        margemParamProporcional.setAdeIntFolha(margemParam.getAdeIntFolha());
        margemParamProporcional.setAdeIncMargem(margemParam.getAdeIncMargem());
        margemParamProporcional.setAdeIndice(CodedValues.INDICE_PADRAO_TAXA_USO_PROPORCIONAL);
        margemParamProporcional.setAdeIdentificador("");
        margemParamProporcional.setValidar(Boolean.FALSE);
        margemParamProporcional.setPermitirValidacaoTaxa(Boolean.TRUE);
        margemParamProporcional.setSerAtivo(Boolean.TRUE);
        margemParamProporcional.setCnvAtivo(Boolean.TRUE);
        margemParamProporcional.setSerCnvAtivo(Boolean.TRUE);
        margemParamProporcional.setSvcAtivo(Boolean.TRUE);
        margemParamProporcional.setCsaAtivo(Boolean.TRUE);
        margemParamProporcional.setOrgAtivo(Boolean.TRUE);
        margemParamProporcional.setEstAtivo(Boolean.TRUE);
        margemParamProporcional.setCseAtivo(Boolean.TRUE);
        margemParamProporcional.setAcao("RESERVAR");
        margemParamProporcional.setNomeResponsavel(responsavel.getUsuNome());
        margemParamProporcional.setRseCodigo(margemParam.getRseCodigo());
        margemParamProporcional.setAdeVlr(vlrTaxaUsoProporcional);
        margemParamProporcional.setCnvCodigo(margemParam.getCnvCodigo());

        try {
            despesaIndividualController.createDespesaIndividual(despesaIndividual, margemParamProporcional, responsavel);
        } catch (DespesaIndividualControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PermissionarioControllerException("mensagem.erro.incluir.permissionario.arg0", responsavel, ex, ex.getMessage());
        }

        return true;
    }

    @Override
    public TransferObject findPermissionarioPorEndereco(TransferObject criterio, AcessoSistema responsavel) throws PermissionarioControllerException {
        try {
            ListaPermissionarioByEnderecoQuery query = new ListaPermissionarioByEnderecoQuery();
            query.echCodigo = (String) criterio.getAttribute(Columns.PRM_ECH_CODIGO);
            query.prmComplEndereco = (String) criterio.getAttribute(Columns.PRM_COMPL_ENDERECO);
            query.csaCodigo = responsavel.getCsaCodigo();

            List<TransferObject> permissionarios = query.executarDTO();

            return permissionarios != null && !permissionarios.isEmpty() ? permissionarios.get(0) : null;

        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PermissionarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}