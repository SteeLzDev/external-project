package com.zetra.econsig.service.coeficiente;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.CoeficienteCorrecaoTransferObject;
import com.zetra.econsig.exception.CoeficienteCorrecaoControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.CoeficienteCorrecao;
import com.zetra.econsig.persistence.entity.CoeficienteCorrecaoHome;
import com.zetra.econsig.persistence.entity.CoeficienteCorrecaoId;
import com.zetra.econsig.persistence.entity.TipoCoeficienteCorrecao;
import com.zetra.econsig.persistence.entity.TipoCoeficienteCorrecaoHome;
import com.zetra.econsig.persistence.query.correcao.ListaCoeficienteCorrecaoQuery;
import com.zetra.econsig.persistence.query.correcao.ListaTipoCoeficienteCorrecaoQuery;
import com.zetra.econsig.persistence.query.correcao.ObtemCoeficienteCorrecaoQuery;
import com.zetra.econsig.persistence.query.correcao.ObtemTipoCoeficienteCorrecaoQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: CoeficienteCorrecaoControllerBean</p>
 * <p>Description: Interface remota do Session Bean para manipulacao de Coeficientes de Correcao</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class CoeficienteCorrecaoControllerBean implements CoeficienteCorrecaoController {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CoeficienteCorrecaoControllerBean.class);

    // Verifica se ja existe algum coeficiente com a mesma descricao
    private boolean verificaDescricao(CoeficienteCorrecaoTransferObject coeficiente) throws CoeficienteCorrecaoControllerException {
        try {
            ObtemTipoCoeficienteCorrecaoQuery query = new ObtemTipoCoeficienteCorrecaoQuery();
            query.count = true;
            query.notTccCodigo = coeficiente.getTccCodigo();
            query.tccDescricao = coeficiente.getTccDescricao();
            int total = query.executarContador();
            if (total > 0) {
                throw new CoeficienteCorrecaoControllerException("mensagem.erro.nao.possivel.salvar.coeficiente.correcao.existe.outro.mesma.descricao", (AcessoSistema) null);
            }
        } catch (HQueryException ex) {
            throw new CoeficienteCorrecaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
        return true;
    }

    @Override
    public String createCoeficienteCorrecao(CoeficienteCorrecaoTransferObject coeficiente, AcessoSistema responsavel) throws CoeficienteCorrecaoControllerException {
        String tccCodigo = null;
        try {
            if (!TextHelper.isNull(coeficiente.getTccCodigo())) {
                boolean existe = false;
                try {
                    findCoeficienteCorrecaoBean(coeficiente);
                    existe = true;
                } catch (CoeficienteCorrecaoControllerException ex) {
                }
                if (existe) {
                    throw new CoeficienteCorrecaoControllerException("mensagem.erro.nao.possivel.criar.coeficiente.correcao.existe.outro.mesmo.mes.ano", responsavel);
                }
            }

            // Verifica se tem outro coeficiente com a mesma descricao
            if (verificaDescricao(coeficiente)) {
                tccCodigo = coeficiente.getTccCodigo();

                // Verifica se ja existe tipo de coeficiente
                if (TextHelper.isNull(coeficiente.getTccCodigo())) {
                    TipoCoeficienteCorrecao tccBean = TipoCoeficienteCorrecaoHome.create(coeficiente.getTccDescricao(), coeficiente.getTccFormaCalc());
                    tccCodigo = tccBean.getTccCodigo();
                }

                // Cria o coeficiente de correção para o mês/ano informado
                CoeficienteCorrecaoHome.create(tccCodigo, coeficiente.getCcrVlr(), coeficiente.getCcrMes(), coeficiente.getCcrAno(), coeficiente.getCcrVlrAcumulado());

                LogDelegate log = new LogDelegate(responsavel, Log.COEFICIENTE_CORRECAO, Log.CREATE, Log.LOG_INFORMACAO);
                log.setTipoCoeficienteCorrecao(tccCodigo);
                log.getUpdatedFields(coeficiente.getAtributos(), null);
                log.write();
            }
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        } catch (CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            CoeficienteCorrecaoControllerException excecao = new CoeficienteCorrecaoControllerException("mensagem.erro.nao.possivel.criar.este.coeficiente.correcao.motivo", responsavel, ex.getMessage());
            if (ex.getMessage().indexOf("Invalid argument value") != -1) {
                excecao = new CoeficienteCorrecaoControllerException("mensagem.erro.nao.possivel.criar.coeficiente.correcao.existe.outro.mesmo.mes.ano", responsavel);
            }
            throw excecao;
        }
        return tccCodigo;
    }

    private CoeficienteCorrecao findCoeficienteCorrecaoBean(CoeficienteCorrecaoTransferObject coeficiente) throws CoeficienteCorrecaoControllerException {
        Short ccrMes = coeficiente.getCcrMes() != null ? coeficiente.getCcrMes().shortValue() : null;
        Short ccrAno = coeficiente.getCcrAno() != null ? coeficiente.getCcrAno().shortValue() : null;

        // Os campos de mês e ano não podem ser nulos.
        if (ccrMes == null || ccrAno == null) {
            throw new CoeficienteCorrecaoControllerException("mensagem.erro.nenhum.coeficiente.correcao.encontrado", (AcessoSistema) null);
        }

        CoeficienteCorrecaoId coeficienteCorrecaoID = new CoeficienteCorrecaoId(coeficiente.getTccCodigo(), ccrMes, ccrAno);
        CoeficienteCorrecao ccrBean = null;

        try {
            ccrBean = CoeficienteCorrecaoHome.findByPrimaryKey(coeficienteCorrecaoID);
        } catch (FindException ex) {
            throw new CoeficienteCorrecaoControllerException("mensagem.erro.nenhum.coeficiente.correcao.encontrado", (AcessoSistema) null);
        }

        return ccrBean;
    }

    private TipoCoeficienteCorrecao findTipoCoeficienteCorrecaoBean(CoeficienteCorrecaoTransferObject coeficiente) throws CoeficienteCorrecaoControllerException {
        TipoCoeficienteCorrecao tccBean = null;
        if (coeficiente.getTccCodigo() != null) {
            try {
                tccBean = TipoCoeficienteCorrecaoHome.findByPrimaryKey(coeficiente.getTccCodigo());
            } catch (FindException ex) {
                throw new CoeficienteCorrecaoControllerException("mensagem.erro.nenhum.tipo.coeficiente.correcao.encontrado", (AcessoSistema) null);
            }
        } else {
            throw new CoeficienteCorrecaoControllerException("mensagem.erro.nenhum.codigo.passado", (AcessoSistema) null);
        }
        return tccBean;
    }

    @Override
    public void removeCoeficienteCorrecao(CoeficienteCorrecaoTransferObject coeficiente, AcessoSistema responsavel) throws CoeficienteCorrecaoControllerException {
        try {
            String ccrTccCodigo = coeficiente.getTccCodigo();
            CoeficienteCorrecao ccrBean;
            TipoCoeficienteCorrecao tccBean;

            // exclui todos os coeficientes que tem o mesmo codigo
            if (coeficiente.getCcrMes() == null || coeficiente.getCcrAno() == null) {
                tccBean = TipoCoeficienteCorrecaoHome.findByPrimaryKey(ccrTccCodigo);
                List<CoeficienteCorrecao> list = CoeficienteCorrecaoHome.findByTccCodigo(ccrTccCodigo);
                if (list != null && list.size() > 0) {
                    Iterator<CoeficienteCorrecao> it = list.iterator();

                    //remove os coeficientes da lista
                    while (it.hasNext()){
                        ccrBean = it.next();
                        CoeficienteCorrecaoHome.remove(ccrBean);
                    }
                }
                //remove o tipo de coeficiente de correcao
                TipoCoeficienteCorrecaoHome.remove(tccBean);
            } else {
                ccrBean = findCoeficienteCorrecaoBean(coeficiente);
                CoeficienteCorrecaoHome.remove(ccrBean);
            }
            LogDelegate logDelegate = new LogDelegate(responsavel, Log.COEFICIENTE_CORRECAO, Log.DELETE, Log.LOG_INFORMACAO);
            logDelegate.setTipoCoeficienteCorrecao(ccrTccCodigo);
            logDelegate.write();

        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CoeficienteCorrecaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (FindException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CoeficienteCorrecaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (RemoveException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CoeficienteCorrecaoControllerException("mensagem.erro.nao.possivel.excluir.coeficiente.correcao", responsavel);
        }
    }

    private CoeficienteCorrecaoTransferObject setCoeficienteCorrecaoValues(CoeficienteCorrecao ccrBean) {
        if (ccrBean == null) {
            return null;
        }

        CoeficienteCorrecaoTransferObject coeficiente = new CoeficienteCorrecaoTransferObject(ccrBean.getTccCodigo());
        coeficiente.setCcrVlr(ccrBean.getCcrVlr());
        coeficiente.setCcrMes(ccrBean.getCcrMes());
        coeficiente.setCcrAno(ccrBean.getCcrAno());
        coeficiente.setCcrVlrAcumulado(ccrBean.getCcrVlrAcumulado());

        return coeficiente;
    }

    @Override
    public void updateCoeficienteCorrecao(CoeficienteCorrecaoTransferObject coeficiente, AcessoSistema responsavel) throws CoeficienteCorrecaoControllerException {
        try {
            CoeficienteCorrecao ccrBean = findCoeficienteCorrecaoBean(coeficiente);
            TipoCoeficienteCorrecao tccBean = findTipoCoeficienteCorrecaoBean(coeficiente);
            LogDelegate log = new LogDelegate(responsavel, Log.COEFICIENTE_CORRECAO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setTipoCoeficienteCorrecao(ccrBean != null ? ccrBean.getTccCodigo() : null);

            /* Compara a versão do cache com a passada por parâmetro */
            CoeficienteCorrecaoTransferObject ccrCache = setCoeficienteCorrecaoValues(ccrBean);
            CustomTransferObject merge = log.getUpdatedFields(coeficiente.getAtributos(), ccrCache.getAtributos());

            if (verificaDescricao(coeficiente)) {
                if (merge.getAtributos().containsKey(Columns.TCC_DESCRICAO)) {
                    tccBean.setTccDescricao((String) merge.getAttribute(Columns.TCC_DESCRICAO));
                }
                if (merge.getAtributos().containsKey(Columns.TCC_FORMA_CALC)) {
                    tccBean.setTccFormaCalc((String) merge.getAttribute(Columns.TCC_FORMA_CALC));
                }
            }
            if (merge.getAtributos().containsKey(Columns.CCR_VLR)) {
                ccrBean.setCcrVlr((BigDecimal) merge.getAttribute(Columns.CCR_VLR));
            }
            if (merge.getAtributos().containsKey(Columns.CCR_VLR_ACUMULADO)) {
                ccrBean.setCcrVlrAcumulado((BigDecimal) merge.getAttribute(Columns.CCR_VLR_ACUMULADO));
            }

            CoeficienteCorrecaoHome.update(ccrBean);

            log.write();

        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CoeficienteCorrecaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new CoeficienteCorrecaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void updateTipoCoeficienteCorrecao(CoeficienteCorrecaoTransferObject coeficiente, AcessoSistema responsavel) throws CoeficienteCorrecaoControllerException {
        // atribui tais valores a um tipo de coeficiente de correcao
        TipoCoeficienteCorrecao tcc = findTipoCoeficienteCorrecaoBean(coeficiente);

        // seta novos valores, antes testa se ja nao existe tipo de coeficiente com mesmo nome
        if (verificaDescricao(coeficiente)) {
            tcc.setTccDescricao(coeficiente.getTccDescricao());
        }
        tcc.setTccFormaCalc(coeficiente.getTccFormaCalc());

        try {
            TipoCoeficienteCorrecaoHome.update(tcc);
        } catch (UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new CoeficienteCorrecaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void updateCoeficienteCorrecaoValorAcumulado(CoeficienteCorrecaoTransferObject coeficiente, Boolean exclusao, AcessoSistema responsavel) throws CoeficienteCorrecaoControllerException {
        try {
            Short mes = coeficiente.getCcrMes().shortValue() == 1 ? (short) 12 : (short) (coeficiente.getCcrMes().shortValue() - 1);
            Short ano = coeficiente.getCcrMes().shortValue() == 1 ? (short) (coeficiente.getCcrAno().shortValue() - 1) : coeficiente.getCcrAno();

            ListaCoeficienteCorrecaoQuery query = new ListaCoeficienteCorrecaoQuery();
            query.tccCodigo = coeficiente.getTccCodigo();
            query.mes = mes;
            query.ano = ano;
            List<CoeficienteCorrecaoTransferObject> lista = query.executarDTO(CoeficienteCorrecaoTransferObject.class);
            Iterator<CoeficienteCorrecaoTransferObject> it = lista.iterator();

            CoeficienteCorrecao ccrBean;
            CoeficienteCorrecaoTransferObject ccto;
            BigDecimal vlrAcumulado, vlrAcumuladoMesAnterior;

            // Evita erro interno com inserção incorreta fora da sequência sendo removida
            if (it.hasNext()) {
                ccto = it.next();
            } else {
                return;
            }

            //exclusao = false: Usado ao incluir a primeira entrada da lista
            //exclusao = true: Usado ao remover a primeira entrada da lista
            if (ccto.getCcrAno().equals(coeficiente.getCcrAno()) && ccto.getCcrMes().equals(coeficiente.getCcrMes())) {
                ccrBean = findCoeficienteCorrecaoBean(coeficiente);
                ccrBean.setCcrVlrAcumulado(new BigDecimal(1));
                vlrAcumuladoMesAnterior = ccrBean.getCcrVlrAcumulado();

                CoeficienteCorrecaoHome.update(ccrBean);
            } else if (exclusao
                    && ccto.getCcrAno().equals(coeficiente.getCcrMes().shortValue() == 12 ? (short) (coeficiente.getCcrAno().shortValue() + 1) : coeficiente.getCcrAno())
                    && ccto.getCcrMes().equals(coeficiente.getCcrMes().shortValue() == 12 ? (short) 1 : (short) (coeficiente.getCcrMes().shortValue()))) {
                ccrBean = findCoeficienteCorrecaoBean(ccto);
                ccrBean.setCcrVlrAcumulado(new BigDecimal(1));
                vlrAcumuladoMesAnterior = ccrBean.getCcrVlrAcumulado();

                CoeficienteCorrecaoHome.update(ccrBean);
            } else {
                vlrAcumuladoMesAnterior = ccto.getCcrVlrAcumulado();
            }

            while (it.hasNext()){
                ccto = it.next();
                ccrBean = findCoeficienteCorrecaoBean(ccto);
                vlrAcumulado = (ccrBean.getCcrVlr().multiply(vlrAcumuladoMesAnterior)).add(vlrAcumuladoMesAnterior);
                ccrBean.setCcrVlrAcumulado(vlrAcumulado.setScale(9, java.math.RoundingMode.DOWN));
                vlrAcumuladoMesAnterior = ccrBean.getCcrVlrAcumulado();

                CoeficienteCorrecaoHome.update(ccrBean);
            }

            LogDelegate log = new LogDelegate(responsavel, Log.COEFICIENTE_CORRECAO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setTipoCoeficienteCorrecao(coeficiente.getTccCodigo());
            log.getUpdatedFields(coeficiente.getAtributos(), null);
            log.write();
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        } catch (DAOException ex) {
            throw new CoeficienteCorrecaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (ArithmeticException ex) {
            LOG.error(ex.getMessage(), ex);
        } catch (UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new CoeficienteCorrecaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<CoeficienteCorrecaoTransferObject> lstCoeficienteCorrecao(String ccrTccCodigo) throws CoeficienteCorrecaoControllerException {
        try {
            ListaCoeficienteCorrecaoQuery query = new ListaCoeficienteCorrecaoQuery();
            query.tccCodigo = ccrTccCodigo;
            return query.executarDTO(CoeficienteCorrecaoTransferObject.class);
        } catch (HQueryException ex) {
            throw new CoeficienteCorrecaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public List<TransferObject> lstTipoCoeficienteCorrecao() throws CoeficienteCorrecaoControllerException {
        try {
            ListaTipoCoeficienteCorrecaoQuery query = new ListaTipoCoeficienteCorrecaoQuery();
            return query.executarDTO();
        } catch (HQueryException ex) {
            throw new CoeficienteCorrecaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public BigDecimal getCoeficienteCorrecao(String ccrTccCodigo, int mes, int ano) throws CoeficienteCorrecaoControllerException {
        return getCoeficienteCorrecao(ccrTccCodigo, mes, ano, false, false, null);
    }

    @Override
    public BigDecimal getPrimeiroCoeficienteCorrecao(String ccrTccCodigo, int mes, int ano, String strCorrecaoVlr) throws CoeficienteCorrecaoControllerException {
        return getCoeficienteCorrecao(ccrTccCodigo, mes, ano, true, false, strCorrecaoVlr);
    }

    @Override
    public BigDecimal getPrimeiroCoeficienteCorrecao(String ccrTccCodigo, int mes, int ano) throws CoeficienteCorrecaoControllerException {
        return getPrimeiroCoeficienteCorrecao(ccrTccCodigo, mes, ano, null);
    }

    @Override
    public BigDecimal getUltimoCoeficienteCorrecao(String ccrTccCodigo, int mes, int ano, String strCorrecaoVlr) throws CoeficienteCorrecaoControllerException {
        return getCoeficienteCorrecao(ccrTccCodigo, mes, ano, false, true, strCorrecaoVlr);
    }

    @Override
    public BigDecimal getUltimoCoeficienteCorrecao(String ccrTccCodigo, int mes, int ano) throws CoeficienteCorrecaoControllerException {
        return getUltimoCoeficienteCorrecao(ccrTccCodigo, mes, ano, null);
    }

    private BigDecimal getCoeficienteCorrecao(String tccCodigo, int mes, int ano, boolean primeiro, boolean ultimo, String correcaoVlr) throws CoeficienteCorrecaoControllerException {
        try {
            ObtemCoeficienteCorrecaoQuery query = new ObtemCoeficienteCorrecaoQuery();
            query.tccCodigo = tccCodigo;
            query.mes = mes;
            query.ano = ano;
            query.primeiro = primeiro;
            query.ultimo = ultimo;
            query.correcaoVlr = correcaoVlr;

            List<BigDecimal> values = query.executarLista();
            if (values != null && values.size() > 0 && values.get(0) != null) {
                return values.get(0);
            }
            return null;
        } catch (HQueryException ex) {
            throw new CoeficienteCorrecaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}