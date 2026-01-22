package com.zetra.econsig.service.coeficiente;

import java.util.Calendar;
import java.util.Iterator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CoeficienteControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.persistence.entity.CoeficienteAtivo;
import com.zetra.econsig.persistence.entity.CoeficienteAtivoHome;
import com.zetra.econsig.persistence.query.coeficiente.ListaAtivarCoeficienteQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: AtivarCoeficienteController</p>
 * <p>Description: Session Façade para rotina de ativação de Coeficientes.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class AtivarCoeficienteControllerBean implements AtivarCoeficienteController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AtivarCoeficienteControllerBean.class);

    @Override
    public void ativarCoeficienteDiario(String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws CoeficienteControllerException {
        ativarCoeficienteDiario(csaCodigo, svcCodigo, 0, responsavel);
    }

    @Override
    public void ativarCoeficienteDiario(String csaCodigo, String svcCodigo, int prazo, AcessoSistema responsavel) throws CoeficienteControllerException {
        try {
            LogDelegate log = new LogDelegate(responsavel, Log.COEFICIENTE_ATIVO, Log.UNLOCK, Log.LOG_INFORMACAO);
            log.setServico(svcCodigo);
            log.setConsignataria(csaCodigo);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.ativando.coeficiente.diario", responsavel));
            log.write();
            ativarCoeficiente(CodedValues.CFT_DIARIO, csaCodigo, svcCodigo, prazo);
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CoeficienteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void ativarCoeficienteMensal(String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws CoeficienteControllerException {
        ativarCoeficienteMensal(csaCodigo, svcCodigo, 0, responsavel);
    }

    @Override
    public void ativarCoeficienteMensal(String csaCodigo, String svcCodigo, int prazo, AcessoSistema responsavel) throws CoeficienteControllerException {
        try {
            LogDelegate log = new LogDelegate(responsavel, Log.COEFICIENTE_ATIVO, Log.UNLOCK, Log.LOG_INFORMACAO);
            log.setServico(svcCodigo);
            log.setConsignataria(csaCodigo);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.ativando.coeficiente.mensal", responsavel));
            log.write();
            ativarCoeficiente(CodedValues.CFT_MENSAL, csaCodigo, svcCodigo, prazo);
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CoeficienteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void ativarCoeficiente(String tipo, String csaCodigo, String svcCodigo, int prazo) throws CoeficienteControllerException {
        try {
            ListaAtivarCoeficienteQuery query = new ListaAtivarCoeficienteQuery();
            query.tipo = tipo;
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;
            query.prazo = prazo;

            //0 - apaga os coeficientes com data_ini maior do que hoje, ou seja aqueles que ainda
            // não estão em vigor e ainda não foram usados
            query.filtro = 0;

            Iterator<TransferObject> itCftCandidatos = query.executarDTO().iterator();
            while (itCftCandidatos.hasNext()) {
                TransferObject cto = itCftCandidatos.next();
                CoeficienteAtivo cfaBean = CoeficienteAtivoHome.findByPrimaryKey(cto.getAttribute(Columns.CFT_CODIGO).toString());
                CoeficienteAtivoHome.remove(cfaBean);
            }

            //1 - seta data_ini para amanhã onde data_ini == null
            query.filtro = 1;

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.add(Calendar.DAY_OF_MONTH, 1);

            itCftCandidatos = query.executarDTO().iterator();
            while (itCftCandidatos.hasNext()) {
                CustomTransferObject cto = (CustomTransferObject) itCftCandidatos.next();
                CoeficienteAtivo cftBean = CoeficienteAtivoHome.findByPrimaryKey(cto.getAttribute(Columns.CFT_CODIGO).toString());
                cftBean.setCftDataIniVig(cal.getTime());
                CoeficienteAtivoHome.update(cftBean);
            }

            //2 - seta data_fim para hoje 23:59:59 onde data_ini != null && data_fim == null
            query.cftDataIniVig = cal.getTime();
            query.filtro = 2;

            cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);

            itCftCandidatos = query.executarDTO().iterator();
            while (itCftCandidatos.hasNext()) {
                CustomTransferObject cto = (CustomTransferObject) itCftCandidatos.next();
                CoeficienteAtivo cftBean = CoeficienteAtivoHome.findByPrimaryKey(cto.getAttribute(Columns.CFT_CODIGO).toString());
                cftBean.setCftDataFimVig(cal.getTime());
                CoeficienteAtivoHome.update(cftBean);
            }

            //3 - Desativa coeficientes marcados incorretamente como ativos.
            cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.add(Calendar.DAY_OF_MONTH, 1);

            query.cftDataIniVig = cal.getTime();
            query.filtro = 3;

            itCftCandidatos = query.executarDTO().iterator();
            while (itCftCandidatos.hasNext()) {
                CustomTransferObject cto = (CustomTransferObject) itCftCandidatos.next();
                CoeficienteAtivo cftBean = CoeficienteAtivoHome.findByPrimaryKey(cto.getAttribute(Columns.CFT_CODIGO).toString());
                cftBean.setCftDataIniVig(null);
                CoeficienteAtivoHome.update(cftBean);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CoeficienteControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
