package com.zetra.econsig.service.coeficiente;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CoeficienteControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Coeficiente;
import com.zetra.econsig.persistence.entity.CoeficienteDesconto;
import com.zetra.econsig.persistence.entity.CoeficienteDescontoHome;
import com.zetra.econsig.persistence.entity.CoeficienteHome;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: CoeficienteControllerBean</p>
 * <p>Description: Interface remota do Session Bean para manipulacao de Coeficientes</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class CoeficienteControllerBean implements CoeficienteController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CoeficienteControllerBean.class);

    @Override
    public String createCoeficiente(TransferObject coeficiente, AcessoSistema responsavel) throws CoeficienteControllerException {
        String cftCodigo = null;
        try {
            // Cria o coeficiente de correção para o mês/ano informado
            Coeficiente cftBean = CoeficienteHome.create((String) coeficiente.getAttribute(Columns.CFT_CODIGO),
                                                         (String) coeficiente.getAttribute(Columns.CFT_PRZ_CSA_CODIGO),
                                                         (Short) coeficiente.getAttribute(Columns.CFT_DIA),
                                                         new BigDecimal(coeficiente.getAttribute(Columns.CFT_VLR).toString()),
                                                         (Date) coeficiente.getAttribute(Columns.CFT_DATA_INI_VIG),
                                                         (Date) coeficiente.getAttribute(Columns.CFT_DATA_FIM_VIG),
                                                         (Date) coeficiente.getAttribute(Columns.CFT_DATA_CADASTRO),
                                                         coeficiente.getAttribute(Columns.CFT_VLR_REF) != null ? new BigDecimal(coeficiente.getAttribute(Columns.CFT_VLR_REF).toString()) : null,
                                                         coeficiente.getAttribute(Columns.CFT_VLR_MINIMO) != null ? new BigDecimal(coeficiente.getAttribute(Columns.CFT_VLR_MINIMO).toString()) : null);
            cftCodigo = cftBean.getCftCodigo();
        } catch (CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new CoeficienteControllerException("mensagem.erro.nao.possivel.criar.este.coeficiente.motivo", responsavel, ex.getMessage() );
        }
        return cftCodigo;
    }

    @Override
    public TransferObject getCoeficiente(String cftCodigo, AcessoSistema responsavel) throws CoeficienteControllerException {
        return setCoeficienteValues(findCoeficienteBean(cftCodigo));
    }

    private Coeficiente findCoeficienteBean(String cftCodigo) throws CoeficienteControllerException {
        Coeficiente cftBean = null;
        try {
            cftBean = CoeficienteHome.findByPrimaryKey(cftCodigo);
        } catch (FindException ex) {
            throw new CoeficienteControllerException("mensagem.erro.nenhum.coeficiente.encontrado", (AcessoSistema) null);
        }
        return cftBean;
    }

    private TransferObject setCoeficienteValues(Coeficiente cftBean) {
        TransferObject coeficiente = new CustomTransferObject();
        coeficiente.setAttribute(Columns.CFT_CODIGO, cftBean.getCftCodigo());
        coeficiente.setAttribute(Columns.CFT_DATA_CADASTRO, cftBean.getCftDataCadastro());
        coeficiente.setAttribute(Columns.CFT_DATA_FIM_VIG, cftBean.getCftDataFimVig());
        coeficiente.setAttribute(Columns.CFT_DATA_INI_VIG, cftBean.getCftDataIniVig());
        coeficiente.setAttribute(Columns.CFT_DIA, cftBean.getCftDia());
        coeficiente.setAttribute(Columns.CFT_PRZ_CSA_CODIGO, cftBean.getPrazoConsignataria() != null ? cftBean.getPrazoConsignataria().getPrzCsaCodigo() : null);
        coeficiente.setAttribute(Columns.CFT_VLR, cftBean.getCftVlr());

        return coeficiente;
    }

    @Override
    public BigDecimal getCftVlrByAdeCodigo(String adeCodigo, AcessoSistema responsavel) throws CoeficienteControllerException {
        try {
            CoeficienteDesconto cdeBean = CoeficienteDescontoHome.findByAdeCodigo(adeCodigo);
            Coeficiente cftBean = CoeficienteHome.findByPrimaryKey(cdeBean.getCoeficiente().getCftCodigo());
            return cftBean.getCftVlr();
        } catch (FindException e) {
            return null;
        }
    }

    @Override
    public String insertCoeficiente(TransferObject coeficiente, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            coeficiente.setAttribute(Columns.CFT_DATA_CADASTRO, null);
            coeficiente.setAttribute(Columns.CFT_DIA, (short) 0);
            coeficiente.setAttribute(Columns.CFT_VLR, NumberHelper.reformat(coeficiente.getAttribute(Columns.CFT_VLR).toString(), NumberHelper.getLang(), "en", 2, 8));

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            coeficiente.setAttribute(Columns.CFT_DATA_INI_VIG, cal.getTime());

            cal.add(Calendar.DAY_OF_MONTH, -1);
            coeficiente.setAttribute(Columns.CFT_DATA_FIM_VIG, cal.getTime());

            String cftCodigo = createCoeficiente(coeficiente, responsavel);

            LogDelegate log = new LogDelegate(responsavel, Log.COEFICIENTE, Log.CREATE, Log.LOG_INFORMACAO);
            log.setCoeficiente(cftCodigo);
            if (!TextHelper.isNull(coeficiente.getAttribute(Columns.CFT_PRZ_CSA_CODIGO))) {
                log.setPrazoConsignataria((String) coeficiente.getAttribute(Columns.CFT_PRZ_CSA_CODIGO));
            }
            log.getUpdatedFields(coeficiente.getAtributos(), null);
            log.write();
            return cftCodigo;
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

}