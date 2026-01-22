package com.zetra.econsig.service.juros;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CoeficienteControllerException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.LimiteTaxaJurosControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.LimiteTaxaJuros;
import com.zetra.econsig.persistence.entity.LimiteTaxaJurosHome;
import com.zetra.econsig.persistence.query.juros.ListaLimiteTaxaJurosPorServicoQuery;
import com.zetra.econsig.persistence.query.juros.ListaLimiteTaxaJurosQuery;
import com.zetra.econsig.service.coeficiente.CoeficienteAtivoController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.Columns;

import jakarta.mail.MessagingException;

/**
 * <p>Title: LimiteTaxaJurosControllerBean</p>
 * <p>Description: Session Bean para manipulação de limite de taxa de juros.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: $
 * $Date: $
 */
@Service
@Transactional
public class LimiteTaxaJurosControllerBean implements LimiteTaxaJurosController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LimiteTaxaJurosControllerBean.class);

    @Autowired
    private CoeficienteAtivoController coeficienteAtivoController;

    @Autowired
    private SimulacaoController simulacaoController;

    @Autowired
    private DefinicaoTaxaJurosController definicaoTaxaJurosController;

    // Obtem a lista de limite de taxa de juros
    @Override
    public List<TransferObject> listaLimiteTaxaJuros(CustomTransferObject criterio, int offset, int size, AcessoSistema responsavel) throws LimiteTaxaJurosControllerException {
        try {

            final ListaLimiteTaxaJurosQuery query = new ListaLimiteTaxaJurosQuery();

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (size != -1) {
                query.maxResults = size;
            }

            if (criterio != null) {
                query.svcCodigo = (String) criterio.getAttribute(Columns.LTJ_SVC_CODIGO);
                query.ltjPrazoRef = (Short) criterio.getAttribute(Columns.LTJ_PRAZO_REF);
            }

            return query.executarDTO();

        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LimiteTaxaJurosControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int countLimiteTaxaJuros(CustomTransferObject criterio, AcessoSistema responsavel) throws LimiteTaxaJurosControllerException {
        try {
            final ListaLimiteTaxaJurosQuery query = new ListaLimiteTaxaJurosQuery();
            query.count = true;

            if (criterio != null) {
                query.svcCodigo = (String) criterio.getAttribute(Columns.LTJ_SVC_CODIGO);
                query.ltjPrazoRef = (Short) criterio.getAttribute(Columns.LTJ_PRAZO_REF);
            }

            return query.executarContador();

        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LimiteTaxaJurosControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public TransferObject findLimiteTaxaJuros(TransferObject limiteTaxaJuros, AcessoSistema responsavel) throws LimiteTaxaJurosControllerException {
        return setLimiteTaxaJurosValues(findLimiteTaxaJurosBean(limiteTaxaJuros, responsavel));
    }

    private TransferObject setLimiteTaxaJurosValues(LimiteTaxaJuros limiteBean) {
        final TransferObject limiteTO = new CustomTransferObject();

        limiteTO.setAttribute(Columns.LTJ_CODIGO, limiteBean.getLtjCodigo());
        limiteTO.setAttribute(Columns.LTJ_SVC_CODIGO, limiteBean.getServico().getSvcCodigo());
        limiteTO.setAttribute(Columns.LTJ_PRAZO_REF, limiteBean.getLtjPrazoRef());
        limiteTO.setAttribute(Columns.LTJ_JUROS_MAX, limiteBean.getLtjJurosMax());
        limiteTO.setAttribute(Columns.LTJ_VLR_REF, limiteBean.getLtjVlrRef());

        return limiteTO;
    }

    private LimiteTaxaJuros findLimiteTaxaJurosBean(TransferObject limiteTaxaJuros, AcessoSistema responsavel) throws LimiteTaxaJurosControllerException {
        LimiteTaxaJuros limiteBean = null;
        try {
            if (limiteTaxaJuros.getAttribute(Columns.LTJ_CODIGO) != null) {
                limiteBean = LimiteTaxaJurosHome.findByPrimaryKey((String) limiteTaxaJuros.getAttribute(Columns.LTJ_CODIGO));
            } else {
                throw new LimiteTaxaJurosControllerException("mensagem.erro.limite.taxa.juros.nao.encontrado", responsavel);
            }
        } catch (final FindException e) {
            throw new LimiteTaxaJurosControllerException("mensagem.erro.limite.taxa.juros.nao.encontrado", responsavel);
        }
        return limiteBean;
    }

    @Override
    public void removeLimiteTaxaJuros(CustomTransferObject limiteTaxaJuros, AcessoSistema responsavel) throws LimiteTaxaJurosControllerException {
        try {
            final LimiteTaxaJuros limiteBean = findLimiteTaxaJurosBean(limiteTaxaJuros, responsavel);
            final String ltjCodigo = limiteBean.getLtjCodigo();

            AbstractEntityHome.remove(limiteBean);

            final LogDelegate log = new LogDelegate(responsavel, Log.LIMITE_TAXA_JUROS, Log.DELETE, Log.LOG_INFORMACAO);
            log.setLimiteTaxaJuros(ltjCodigo);
            log.write();
        } catch (LogControllerException | com.zetra.econsig.exception.RemoveException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new LimiteTaxaJurosControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public String createLimiteTaxaJuros(CustomTransferObject limiteTaxaJuros, AcessoSistema responsavel) throws LimiteTaxaJurosControllerException {
        String codigo = null;
        try {
            validaLimiteTaxaJuros(limiteTaxaJuros, responsavel);

            final String svcCodigo = (String) limiteTaxaJuros.getAttribute(Columns.LTJ_SVC_CODIGO);
            final Short prazo = (Short) limiteTaxaJuros.getAttribute(Columns.LTJ_PRAZO_REF);
            final BigDecimal juros = (BigDecimal) limiteTaxaJuros.getAttribute(Columns.LTJ_JUROS_MAX);
            final String cftDataFimVigencia = !TextHelper.isNull(limiteTaxaJuros.getAttribute(Columns.CFT_DATA_FIM_VIG)) ? limiteTaxaJuros.getAttribute(Columns.CFT_DATA_FIM_VIG).toString() : null;
            final BigDecimal limiteTaxaCET = (BigDecimal) limiteTaxaJuros.getAttribute(Columns.LTJ_VLR_REF);
            final boolean aplicarRegraCETTaxaJuros = !TextHelper.isNull(limiteTaxaJuros.getAttribute("PARAM_TPC_CET_REGRA_TAXA")) ? (boolean) limiteTaxaJuros.getAttribute("PARAM_TPC_CET_REGRA_TAXA") : false;

            codigo = LimiteTaxaJurosHome.create(svcCodigo, prazo, juros, limiteTaxaCET);

            // Altera data de vigência dos coeficientes ativos que possuem taxa maior que o limite criado
            if (!TextHelper.isNull(cftDataFimVigencia)) {
                List<TransferObject> lstRegraJurosSuperior = null;
                final List<TransferObject> lstTaxaSuperior = simulacaoController.getTaxaSuperiorTaxaLimite(svcCodigo, prazo, juros, responsavel);

                if(!TextHelper.isNull(lstTaxaSuperior)) {
                  coeficienteAtivoController.modificaDataFimVigencia(cftDataFimVigencia, lstTaxaSuperior, responsavel);
                }
                if(aplicarRegraCETTaxaJuros) {
                    lstRegraJurosSuperior = simulacaoController.getRegraJurosTaxaLimite(svcCodigo, prazo, juros, responsavel);
                    definicaoTaxaJurosController.modificaDataFimVigencia(cftDataFimVigencia, lstRegraJurosSuperior, responsavel);
                }

                // Envia email para consignatária
                final Date cftDataFimVig = DateHelper.parse(cftDataFimVigencia + " 23:59:59", LocaleHelper.getDateTimePattern());
                EnviaEmailHelper.enviarEmailTaxaSuperiorLimite(lstTaxaSuperior, limiteTaxaJuros, lstRegraJurosSuperior, svcCodigo, cftDataFimVig, aplicarRegraCETTaxaJuros, responsavel);
            }

            if(!TextHelper.isNull(limiteTaxaJuros.getAttribute("PARAM_TPC_CET_REGRA_TAXA"))) {
                limiteTaxaJuros.remove("PARAM_TPC_CET_REGRA_TAXA");
            }

            final LogDelegate log = new LogDelegate(responsavel, Log.LIMITE_TAXA_JUROS, Log.CREATE, Log.LOG_INFORMACAO);
            log.setLimiteTaxaJuros(codigo);
            log.setServico(svcCodigo);
            log.getUpdatedFields(limiteTaxaJuros.getAtributos(), null);
            log.write();

        } catch (final com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new LimiteTaxaJurosControllerException("mensagem.erro.nao.possivel.inserir.limite.taxa.juros", responsavel, ex);
        } catch (SimulacaoControllerException | CoeficienteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LimiteTaxaJurosControllerException(ex);
        } catch (final LimiteTaxaJurosControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        } catch (final ParseException | MessagingException | ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LimiteTaxaJurosControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
        return codigo;
    }

    @Override
    public void updateLimiteTaxaJuros(CustomTransferObject limiteTaxaJuros, AcessoSistema responsavel) throws LimiteTaxaJurosControllerException {
        try {
            validaLimiteTaxaJuros(limiteTaxaJuros, responsavel);

            final LimiteTaxaJuros limiteBean = findLimiteTaxaJurosBean(limiteTaxaJuros, responsavel);
            final String ltjCodigo = limiteBean.getLtjCodigo();
            final String cftDataFimVigencia = !TextHelper.isNull(limiteTaxaJuros.getAttribute(Columns.CFT_DATA_FIM_VIG)) ? limiteTaxaJuros.getAttribute(Columns.CFT_DATA_FIM_VIG).toString() : null;
            final boolean aplicarRegraCETTaxaJuros = !TextHelper.isNull(limiteTaxaJuros.getAttribute("PARAM_TPC_CET_REGRA_TAXA")) ? (boolean) limiteTaxaJuros.getAttribute("PARAM_TPC_CET_REGRA_TAXA") : false;

            final LogDelegate log = new LogDelegate(responsavel, Log.LIMITE_TAXA_JUROS, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setLimiteTaxaJuros(ltjCodigo);

            if(!TextHelper.isNull(limiteTaxaJuros.getAttribute("PARAM_TPC_CET_REGRA_TAXA"))) {
                limiteTaxaJuros.remove("PARAM_TPC_CET_REGRA_TAXA");
            }

            // Compara a versão do cache com a versão passada por parâmetro
            final TransferObject limiteCache = setLimiteTaxaJurosValues(limiteBean);
            final CustomTransferObject merge = log.getUpdatedFields(limiteTaxaJuros.getAtributos(), limiteCache.getAtributos());

            if (merge.getAtributos().containsKey(Columns.LTJ_PRAZO_REF)) {
                limiteBean.setLtjPrazoRef((Short) merge.getAttribute(Columns.LTJ_PRAZO_REF));
            }

            if (merge.getAtributos().containsKey(Columns.LTJ_JUROS_MAX)) {
                limiteBean.setLtjJurosMax((BigDecimal) merge.getAttribute(Columns.LTJ_JUROS_MAX));
            }

            if (merge.getAtributos().containsKey(Columns.LTJ_VLR_REF)) {
                limiteBean.setLtjVlrRef((BigDecimal) merge.getAttribute(Columns.LTJ_VLR_REF));
            }

            AbstractEntityHome.update(limiteBean);

            // Altera data de vigência dos coeficientes ativos que possuem taxa maior que o limite criado
            if (!TextHelper.isNull(cftDataFimVigencia)) {
                List<TransferObject> lstRegraJurosSuperior = null;
                final String svcCodigo = limiteBean.getServico().getSvcCodigo();
                final List<TransferObject> lstTaxaSuperior = simulacaoController.getTaxaSuperiorTaxaLimite(svcCodigo, limiteBean.getLtjPrazoRef(), limiteBean.getLtjJurosMax(), responsavel);

                if(!TextHelper.isNull(lstTaxaSuperior)) {
                    coeficienteAtivoController.modificaDataFimVigencia(cftDataFimVigencia, lstTaxaSuperior, responsavel);
                }

                if(aplicarRegraCETTaxaJuros) {
                    lstRegraJurosSuperior = simulacaoController.getRegraJurosTaxaLimite(svcCodigo, limiteBean.getLtjPrazoRef(), limiteBean.getLtjJurosMax(), responsavel);
                    definicaoTaxaJurosController.modificaDataFimVigencia(cftDataFimVigencia, lstRegraJurosSuperior, responsavel);
                }

                // Envia email para consignatária
                final Date cftDataFimVig = DateHelper.parse(cftDataFimVigencia + " 23:59:59", LocaleHelper.getDateTimePattern());
                EnviaEmailHelper.enviarEmailTaxaSuperiorLimite(lstTaxaSuperior, limiteTaxaJuros, lstRegraJurosSuperior, svcCodigo, cftDataFimVig, aplicarRegraCETTaxaJuros, responsavel);
            }

            log.write();
        } catch (final com.zetra.econsig.exception.UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new LimiteTaxaJurosControllerException("mensagem.erro.nao.possivel.atualizar.limite.taxa.juros.erro.interno", responsavel, ex.getMessage());
        } catch (final SimulacaoControllerException | CoeficienteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LimiteTaxaJurosControllerException(ex);
        } catch (final LimiteTaxaJurosControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        } catch (final ParseException | ZetraException | MessagingException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LimiteTaxaJurosControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void validaLimiteTaxaJuros(TransferObject limiteTaxaJuros, AcessoSistema responsavel) throws LimiteTaxaJurosControllerException {
        if (limiteTaxaJuros != null) {
            if (TextHelper.isNull(limiteTaxaJuros.getAttribute(Columns.LTJ_PRAZO_REF))) {
                throw new LimiteTaxaJurosControllerException("mensagem.informe.prazo.referencia.deve.ser.informado", responsavel);
            }

            if (TextHelper.isNull(limiteTaxaJuros.getAttribute(Columns.LTJ_JUROS_MAX))) {
                throw new LimiteTaxaJurosControllerException("mensagem.informe.valor.maximo.para.juros.deve.ser.informado", responsavel);
            }
        }
        try {
            final ListaLimiteTaxaJurosQuery query = new ListaLimiteTaxaJurosQuery();
            query.count = true;
            query.svcCodigo = (String) limiteTaxaJuros.getAttribute(Columns.LTJ_SVC_CODIGO);
            query.ltjPrazoRef = (Short) limiteTaxaJuros.getAttribute(Columns.LTJ_PRAZO_REF);
            query.notLtjCodigo = (String) limiteTaxaJuros.getAttribute(Columns.LTJ_CODIGO);
            final int total = query.executarContador();
            if (total > 0) {
                throw new LimiteTaxaJurosControllerException("mensagem.erro.nao.possivel.salvar.limite.taxa.juros.existe.outro.mesmo.prazo", responsavel);
            }
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LimiteTaxaJurosControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> listaLimiteTaxaJurosPorServico(List<TransferObject> servicos, BigDecimal taxaJuros, Short faixaPrazoInicial, Short faixaPrazoFinal, AcessoSistema responsavel) throws LimiteTaxaJurosControllerException {
        try {
            final ListaLimiteTaxaJurosPorServicoQuery query = new ListaLimiteTaxaJurosPorServicoQuery();
            final List<String> svcCodigoLimites = new ArrayList<>();

            for(final TransferObject servico : servicos) {
                svcCodigoLimites.add((String) servico.getAttribute(Columns.SVC_CODIGO));
            }

            query.taxaJuros = taxaJuros;
            query.svcCodigos = svcCodigoLimites;
            query.faixaPrazoInicial = faixaPrazoInicial;
            query.faixaPrazoFinal = faixaPrazoFinal;

            return query.executarDTO();

        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LimiteTaxaJurosControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
