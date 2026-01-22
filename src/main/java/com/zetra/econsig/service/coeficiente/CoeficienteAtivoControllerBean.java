package com.zetra.econsig.service.coeficiente;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CoeficienteControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Coeficiente;
import com.zetra.econsig.persistence.entity.CoeficienteAtivo;
import com.zetra.econsig.persistence.entity.CoeficienteAtivoHome;
import com.zetra.econsig.persistence.entity.CoeficienteHome;
import com.zetra.econsig.persistence.entity.OcorrenciaCoeficiente;
import com.zetra.econsig.persistence.entity.OcorrenciaCoeficienteHome;
import com.zetra.econsig.persistence.query.coeficiente.ListaCoeficientesAtivosQuery;
import com.zetra.econsig.persistence.query.coeficiente.ListaCoeficientesInativosQuery;
import com.zetra.econsig.persistence.query.coeficiente.ListaOcorrenciaCoeficienteQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: CoeficienteAtivoControllerBean</p>
 * <p>Description: Interface remota do Session Bean para manipulacao de Coeficientes ativos</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class CoeficienteAtivoControllerBean implements CoeficienteAtivoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CoeficienteAtivoControllerBean.class);

    @Autowired
    private CoeficienteController coeficienteController;

    /**
     * Salva os dados de um novo coeficiente.
     * @param coeficiente
     * @param coeficienteOld
     * @param responsavel
     * @return
     * @throws CoeficienteControllerException
     */
    @Override
    public String createCoeficienteAtivo(TransferObject coeficiente, TransferObject coeficienteOld, AcessoSistema responsavel) throws CoeficienteControllerException {
        String cftCodigo = null;
        try {
        	BigDecimal cftVlrMinimoNew = !TextHelper.isNull(coeficiente.getAttribute(Columns.CFT_VLR_MINIMO)) ? new BigDecimal(coeficiente.getAttribute(Columns.CFT_VLR_MINIMO).toString()) : null;
            BigDecimal cftVlrNew = new BigDecimal(coeficiente.getAttribute(Columns.CFT_VLR).toString());
            CoeficienteAtivo cfaBean = CoeficienteAtivoHome.create((String) coeficiente.getAttribute(Columns.CFT_PRZ_CSA_CODIGO),
                                                 (Short) coeficiente.getAttribute(Columns.CFT_DIA),
                                                 cftVlrNew,
                                                 (Date) coeficiente.getAttribute(Columns.CFT_DATA_INI_VIG),
                                                 (Date) coeficiente.getAttribute(Columns.CFT_DATA_FIM_VIG),
                                                 !TextHelper.isNull(coeficiente.getAttribute(Columns.CFT_VLR_REF)) ? new BigDecimal(coeficiente.getAttribute(Columns.CFT_VLR_REF).toString()) : null,
                                                 cftVlrMinimoNew);
            cftCodigo = cfaBean.getCftCodigo();

            BigDecimal cftVlrMinimoOld = (coeficienteOld != null && !TextHelper.isNull(coeficienteOld.getAttribute(Columns.CFT_VLR_MINIMO))) ? new BigDecimal(coeficienteOld.getAttribute(Columns.CFT_VLR_MINIMO).toString()) : null;
            String strCftVlrMinimoOld = !TextHelper.isNull(cftVlrMinimoOld) ? NumberHelper.format(cftVlrMinimoOld.doubleValue(), NumberHelper.getLang()) : "";

            // Se o valor antigo não existir ou se for diferente do novo, é porque o coeficiente mínimo foi alterado e deve ser gerada ocorrência
            if ((cftVlrMinimoOld == null && cftVlrMinimoNew != null) || cftVlrMinimoOld != null && cftVlrMinimoNew != null && cftVlrMinimoOld.compareTo(cftVlrMinimoNew) != 0) {
                String ocfObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.alteracao.coeficiente.minimo", responsavel, coeficiente.getAttribute(Columns.PRZ_VLR).toString(), strCftVlrMinimoOld, NumberHelper.format(cftVlrMinimoNew.doubleValue(), NumberHelper.getLang()));
                criaOcorrenciaCft(ocfObs, coeficiente, responsavel);
            }
            
            BigDecimal cftVlrOld = (coeficienteOld != null && !TextHelper.isNull(coeficienteOld.getAttribute(Columns.CFT_VLR))) ? new BigDecimal(coeficienteOld.getAttribute(Columns.CFT_VLR).toString()) : null;
            String strCftVlrOld = !TextHelper.isNull(cftVlrOld) ? NumberHelper.format(cftVlrOld.doubleValue(), NumberHelper.getLang()) : "";

            // Se o valor antigo não existir ou se for diferente do novo, é porque o coeficiente foi alterado e deve ser gerada ocorrência
            if (cftVlrOld == null || cftVlrOld.compareTo(cftVlrNew) != 0) {
                String ocfObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.alteracao.coeficiente", responsavel, coeficiente.getAttribute(Columns.PRZ_VLR).toString(), strCftVlrOld, NumberHelper.format(cftVlrNew.doubleValue(), NumberHelper.getLang()));
                criaOcorrenciaCft(ocfObs, coeficiente, responsavel);
            }

            LogDelegate log = new LogDelegate(responsavel, Log.COEFICIENTE_ATIVO, Log.CREATE, Log.LOG_INFORMACAO);
            log.setCoeficiente(cftCodigo);
            log.setPrazoConsignataria((String) coeficiente.getAttribute(Columns.CFT_PRZ_CSA_CODIGO));
            log.getUpdatedFields(coeficiente.getAtributos(), null);
            log.write();
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        } catch (CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new CoeficienteControllerException("mensagem.erro.nao.possivel.criar.este.coeficiente.motivo", responsavel, ex.getMessage());
        }
        return cftCodigo;
    }
    
    private void criaOcorrenciaCft(String ocfObs, TransferObject coeficiente, AcessoSistema responsavel) throws CoeficienteControllerException {
    	TransferObject ocorrencia = new CustomTransferObject();
        ocorrencia.setAttribute(Columns.OCF_SVC_CODIGO, coeficiente.getAttribute(Columns.SVC_CODIGO));
        ocorrencia.setAttribute(Columns.OCF_CSA_CODIGO, coeficiente.getAttribute(Columns.CSA_CODIGO));
        ocorrencia.setAttribute(Columns.OCF_USU_CODIGO, responsavel.getUsuCodigo());
        ocorrencia.setAttribute(Columns.OCF_TOC_CODIGO, CodedValues.TOC_ALTERACAO_COEFICIENTE);
        ocorrencia.setAttribute(Columns.OCF_DATA_INICIO_VIG, coeficiente.getAttribute(Columns.CFT_DATA_INI_VIG));
        ocorrencia.setAttribute(Columns.OCF_DATA_FIM_VIG, coeficiente.getAttribute(Columns.CFT_DATA_FIM_VIG));
        ocorrencia.setAttribute(Columns.OCF_OBS, ocfObs);
        ocorrencia.setAttribute(Columns.OCF_IP_ACESSO, responsavel.getIpUsuario());

        createOcorrenciaCoeficiente(ocorrencia, responsavel);
    }

    /**
     * Exclui o coeficiente.
     * @param coeficienteAtivo
     * @param responsavel
     * @throws CoeficienteControllerException
     */
    @Override
    public void removeCoeficienteAtivo(TransferObject coeficienteAtivo, AcessoSistema responsavel) throws CoeficienteControllerException {
        try {
            String cftCodigo = (String) coeficienteAtivo.getAttribute(Columns.CFT_CODIGO);
            CoeficienteAtivo cfaBean = findCoeficienteAtivoBean(cftCodigo);
            CoeficienteAtivoHome.remove(cfaBean);
            LogDelegate logDelegate = new LogDelegate(responsavel, Log.COEFICIENTE_ATIVO, Log.DELETE, Log.LOG_INFORMACAO);
            logDelegate.setCoeficiente(cftCodigo);
            logDelegate.write();

        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CoeficienteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (RemoveException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CoeficienteControllerException("mensagem.erro.nao.possivel.excluir.coeficiente", responsavel);
        }
    }

    /**
     * Salva as modificações do coeficiente ativo.
     * @param coeficienteAtivo
     * @param responsavel
     * @throws CoeficienteControllerException
     */
    @Override
    public void updateCoeficienteAtivo(TransferObject coeficienteAtivo, AcessoSistema responsavel) throws CoeficienteControllerException {
        try {
            String cftCodigo = (String) coeficienteAtivo.getAttribute(Columns.CFT_CODIGO);
            CoeficienteAtivo cfaBean = findCoeficienteAtivoBean(cftCodigo);
            LogDelegate log = new LogDelegate(responsavel, Log.COEFICIENTE_ATIVO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setCoeficiente(cfaBean.getCftCodigo());

            /* Compara a versão do cache com a passada por parâmetro */
            TransferObject cftCache = setCoeficienteAtivoValues(cfaBean);
            TransferObject merge = log.getUpdatedFields(coeficienteAtivo.getAtributos(), cftCache.getAtributos());

            if (merge.getAtributos().containsKey(Columns.CFT_VLR)) {
                cfaBean.setCftVlr((BigDecimal) merge.getAttribute(Columns.CFT_VLR));
            }
            if (merge.getAtributos().containsKey(Columns.CFT_VLR_MINIMO)) {
                cfaBean.setCftVlr((BigDecimal) merge.getAttribute(Columns.CFT_VLR_MINIMO));
            }
            if (merge.getAtributos().containsKey(Columns.CFT_DATA_INI_VIG)) {
                cfaBean.setCftDataIniVig((Date) merge.getAttribute(Columns.CFT_DATA_INI_VIG));
            }
            if (merge.getAtributos().containsKey(Columns.CFT_DATA_FIM_VIG)) {
                cfaBean.setCftDataFimVig((Date) merge.getAttribute(Columns.CFT_DATA_FIM_VIG));
            }

            CoeficienteAtivoHome.update(cfaBean);

            log.write();
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CoeficienteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new CoeficienteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Recupera um coeficiente ativo a partir de seu código.
     * @param cftCodigo
     * @return
     * @throws CoeficienteControllerException
     */
    @Override
    public TransferObject getCoeficienteAtivo(String cftCodigo) throws CoeficienteControllerException {
        return setCoeficienteAtivoValues(findCoeficienteAtivoBean(cftCodigo));
    }

    /**
     * Move os registros inativos da tabela de coeficientes ativos para a tabela de histórico.
     * @param responsavel
     * @throws CoeficienteControllerException
     */
    @Override
    public void limparCoeficientesInativos(AcessoSistema responsavel) throws CoeficienteControllerException {
        try {
            // Recupera os coeficientes inativos presente na tabela de coeficientes ativos.
            ListaCoeficientesInativosQuery query = new ListaCoeficientesInativosQuery();
            List<TransferObject> coeficientesInativos = query.executarDTO();

            // Se não há nenhum coeficiente inativo, não há nada a fazer.
            if (coeficientesInativos == null || coeficientesInativos.size() == 0) {
                return;
            }

            TransferObject coeficienteInativo;
            Coeficiente coeficiente;
            CoeficienteAtivo coeficienteAtivo;
            Iterator<TransferObject> itCoeficientesInativos = coeficientesInativos.iterator();
            while (itCoeficientesInativos.hasNext()) {
                coeficienteInativo = itCoeficientesInativos.next();

                try {
                    // Atualiza as datas de início e término de vigência do coeficiente, caso ele já exista
                    // na tabela de histórico.
                    coeficiente = CoeficienteHome.findByPrimaryKey((String) coeficienteInativo.getAttribute(Columns.CFA_CODIGO));
                    coeficiente.setCftDataIniVig((Date) coeficienteInativo.getAttribute(Columns.CFA_DATA_INI_VIG));
                    coeficiente.setCftDataFimVig((Date) coeficienteInativo.getAttribute(Columns.CFA_DATA_FIM_VIG));

                    try {
                        CoeficienteHome.update(coeficiente);
                        LogDelegate log = new LogDelegate(responsavel, Log.COEFICIENTE_ATIVO, Log.UPDATE, Log.LOG_INFORMACAO);
                        log.setCoeficiente((String) coeficienteInativo.getAttribute(Columns.CFA_CODIGO));
                        log.getUpdatedFields(coeficienteInativo.getAtributos(), null);
                        log.write();
                    } catch (UpdateException ex) {
                        LOG.error(ex.getMessage(), ex);
                    } catch (LogControllerException ex) {
                        throw new CoeficienteControllerException(ex);
                    }
                } catch (FindException ex) {
                    // Se o coeficiente não existe, cria um novo.
                    TransferObject novoCoeficiente = new CustomTransferObject();
                    novoCoeficiente.setAttribute(Columns.CFT_CODIGO, coeficienteInativo.getAttribute(Columns.CFA_CODIGO));
                    novoCoeficiente.setAttribute(Columns.CFT_PRZ_CSA_CODIGO, coeficienteInativo.getAttribute(Columns.CFA_PRZ_CSA_CODIGO));
                    novoCoeficiente.setAttribute(Columns.CFT_DIA, coeficienteInativo.getAttribute(Columns.CFA_DIA));
                    novoCoeficiente.setAttribute(Columns.CFT_VLR, coeficienteInativo.getAttribute(Columns.CFA_VLR));
                    novoCoeficiente.setAttribute(Columns.CFT_DATA_INI_VIG, coeficienteInativo.getAttribute(Columns.CFA_DATA_INI_VIG));
                    novoCoeficiente.setAttribute(Columns.CFT_DATA_FIM_VIG, coeficienteInativo.getAttribute(Columns.CFA_DATA_FIM_VIG));
                    novoCoeficiente.setAttribute(Columns.CFT_DATA_CADASTRO, coeficienteInativo.getAttribute(Columns.CFA_DATA_CADASTRO));
                    novoCoeficiente.setAttribute(Columns.CFT_VLR_REF, coeficienteInativo.getAttribute(Columns.CFT_VLR_REF));
                    novoCoeficiente.setAttribute(Columns.CFT_VLR_MINIMO, coeficienteInativo.getAttribute(Columns.CFT_VLR_MINIMO));

                    // Salva na tabela de coeficientes inativos.
                    coeficienteController.createCoeficiente(novoCoeficiente, responsavel);
                }

                // Remove o registro da tabela de coeficientes ativos.
                try {
                    coeficienteAtivo = CoeficienteAtivoHome.findByPrimaryKey((String) coeficienteInativo.getAttribute(Columns.CFA_CODIGO));
                    CoeficienteAtivoHome.remove(coeficienteAtivo);
                } catch (FindException e) {
                    throw new CoeficienteControllerException(e);
                } catch (RemoveException e) {
                    throw new CoeficienteControllerException(e);
                }
            }
        } catch (CoeficienteControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw ex;
        } catch (HQueryException ex) {
            throw new CoeficienteControllerException(ex);
        }
    }

    /**
     * Pesquisa por um coeficiente ativo a partir de seu código.
     * @param cftCodigo
     * @return
     * @throws CoeficienteControllerException
     */
    private CoeficienteAtivo findCoeficienteAtivoBean(String cftCodigo) throws CoeficienteControllerException {
        CoeficienteAtivo cfaBean = null;
        try {
            cfaBean = CoeficienteAtivoHome.findByPrimaryKey(cftCodigo);
        } catch (FindException ex) {
            throw new CoeficienteControllerException("mensagem.erro.nenhum.coeficiente.encontrado", (AcessoSistema) null);
        }
        return cfaBean;
    }

    /**
     * Converte um bean de coeficiente em um TransferObject
     * @param cftBean
     * @return
     */
    private TransferObject setCoeficienteAtivoValues(CoeficienteAtivo cftBean) {
        TransferObject coeficienteAtivo = new CustomTransferObject();
        coeficienteAtivo.setAttribute(Columns.CFT_CODIGO, cftBean.getCftCodigo());
        coeficienteAtivo.setAttribute(Columns.CFT_DATA_CADASTRO, cftBean.getCftDataCadastro());
        coeficienteAtivo.setAttribute(Columns.CFT_DATA_FIM_VIG, cftBean.getCftDataFimVig());
        coeficienteAtivo.setAttribute(Columns.CFT_DATA_INI_VIG, cftBean.getCftDataIniVig());
        coeficienteAtivo.setAttribute(Columns.CFT_DIA, cftBean.getCftDia());
        coeficienteAtivo.setAttribute(Columns.CFT_PRZ_CSA_CODIGO, cftBean.getPrazoConsignataria() != null ? cftBean.getPrazoConsignataria().getPrzCsaCodigo() : null);
        coeficienteAtivo.setAttribute(Columns.CFT_VLR, cftBean.getCftVlr());

        return coeficienteAtivo;
    }

    /**
     * Antecipa a data de vigência de um coeficiente que esteja com data inicial futura.
     * @param cftData Nova data de inicialização.
     * @param csaCodigo Código da consignatária.
     * @param svcCodigo Código do serviço.
     * @param responsavel Responsável pela operação.
     * @throws CoeficienteControllerException Exceção padrão.
     */
    @Override
    public void anteciparDataInicioCoeficiente(String cftData, String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws CoeficienteControllerException {
        try {
            // Altera data fim de vigencia das taxas a vencer
            Date cftDataFimVig = DateHelper.parse(cftData + " 23:59:59", LocaleHelper.getDateTimePattern());
            cftDataFimVig = DateHelper.dateAdd(cftDataFimVig, "DIA", -1);

            // Pesquisa elementos da tabela que possuem data fim futura e atualiza
            ListaCoeficientesAtivosQuery query = new ListaCoeficientesAtivosQuery();
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;
            query.possuiDataFim = true;
            query.verificaIniVigPass = true;
            List<TransferObject> lstTaxas = query.executarDTO();
            Iterator<TransferObject> itTaxas = lstTaxas.iterator();
            while (itTaxas.hasNext()) {
                TransferObject cto = itTaxas.next();
                cto.setAttribute(Columns.CFT_DATA_FIM_VIG, cftDataFimVig);
                updateCoeficienteAtivo(cto, responsavel);
            }

            // Altera data inicio de vigencia das taxas futuras
            Date cftDataIniVig = DateHelper.parse(cftData + " 00:00:00", LocaleHelper.getDateTimePattern());

            // Pesquisa elementos da tabela que nao possuem data fim e atualiza
            query.possuiDataFim = false;
            query.verificaIniVigFut = true;
            query.verificaIniVigPass = false;
            itTaxas = query.executarDTO().iterator();
            while (itTaxas.hasNext()) {
                TransferObject cto = itTaxas.next();
                cto.setAttribute(Columns.CFT_DATA_INI_VIG, cftDataIniVig);
                String tpsDiasCet = (String) cto.getAttribute(Columns.PSE_VLR);
                if (TextHelper.isNum(tpsDiasCet)) {
                    cto.setAttribute(Columns.CFT_DATA_FIM_VIG, DateHelper.addDays(cftDataIniVig, Integer.parseInt(tpsDiasCet)));
                }
                updateCoeficienteAtivo(cto, responsavel);
            }
        } catch (ParseException | HQueryException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CoeficienteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Altera data fim de vigência dos coeficientes passados como parâmetro
     * @param cftDataFim
     * @param lstTaxas
     * @param responsavel
     * @throws CoeficienteControllerException
     */
    @Override
    public void modificaDataFimVigencia(String cftDataFim, List<TransferObject> lstTaxas, AcessoSistema responsavel) throws CoeficienteControllerException {
        try {
            // Altera data fim de vigencia das taxas a vencer
            Date cftDataFimVig = DateHelper.parse(cftDataFim + " 23:59:59", LocaleHelper.getDateTimePattern());

            for(TransferObject cto : lstTaxas) {
                cto.setAttribute(Columns.CFT_DATA_FIM_VIG, cftDataFimVig);
                updateCoeficienteAtivo(cto, responsavel);
            }

        } catch (ParseException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CoeficienteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (ZetraException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CoeficienteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int countOcorrenciaCoeficiente(String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws CoeficienteControllerException {
        try {
            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCsaCodigo();
            }

            if (TextHelper.isNull(csaCodigo)) {
                throw new CoeficienteControllerException("mensagem.erroInternoSistema", responsavel);
            }

            if (TextHelper.isNull(svcCodigo)) {
                throw new CoeficienteControllerException("mensagem.erroInternoSistema", responsavel);
            }

            ListaOcorrenciaCoeficienteQuery query = new ListaOcorrenciaCoeficienteQuery();
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;
            query.count = true;

            return query.executarContador();
        } catch (HQueryException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CoeficienteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> listarOcorrenciaCoeficiente(String csaCodigo, String svcCodigo, int offset, int count, AcessoSistema responsavel) throws CoeficienteControllerException {
        try {
            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCsaCodigo();
            }

            if (TextHelper.isNull(csaCodigo)) {
                throw new CoeficienteControllerException("mensagem.erroInternoSistema", responsavel);
            }

            if (TextHelper.isNull(svcCodigo)) {
                throw new CoeficienteControllerException("mensagem.erroInternoSistema", responsavel);
            }

            ListaOcorrenciaCoeficienteQuery query = new ListaOcorrenciaCoeficienteQuery();
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;

            if (count != -1) {
                query.maxResults = count;
                query.firstResult = offset;
            }

            return query.executarDTO();
        } catch (HQueryException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CoeficienteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private String createOcorrenciaCoeficiente(TransferObject ocorrencia, AcessoSistema responsavel) throws CoeficienteControllerException {
        try {
            // Cria o coeficiente de correção para o mês/ano informado
            OcorrenciaCoeficiente bean = OcorrenciaCoeficienteHome.create((String) ocorrencia.getAttribute(Columns.OCF_SVC_CODIGO),
                    (String) ocorrencia.getAttribute(Columns.OCF_CSA_CODIGO),
                    (String) ocorrencia.getAttribute(Columns.OCF_USU_CODIGO),
                    (String) ocorrencia.getAttribute(Columns.OCF_TOC_CODIGO),
                    (Date) ocorrencia.getAttribute(Columns.OCF_DATA),
                    (Date) ocorrencia.getAttribute(Columns.OCF_DATA_INICIO_VIG),
                    (Date) ocorrencia.getAttribute(Columns.OCF_DATA_FIM_VIG),
                    (String) ocorrencia.getAttribute(Columns.OCF_OBS),
                    (String) ocorrencia.getAttribute(Columns.OCF_IP_ACESSO));

            return bean.getOcfCodigo();

        } catch (CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new CoeficienteControllerException("mensagem.erro.nao.possivel.criar.este.coeficiente.motivo", responsavel, ex.getMessage() );
        }
    }
}