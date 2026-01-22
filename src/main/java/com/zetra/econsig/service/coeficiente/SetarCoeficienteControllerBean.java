package com.zetra.econsig.service.coeficiente;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CoeficienteControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.persistence.entity.CoeficienteAtivo;
import com.zetra.econsig.persistence.entity.CoeficienteAtivoHome;
import com.zetra.econsig.persistence.query.coeficiente.ListaServicoPrazoAtivoQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: SetarCoeficienteController</p>
 * <p>Description: Session Façade para rotina de definição de Coeficientes.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class SetarCoeficienteControllerBean implements SetarCoeficienteController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SetarCoeficienteControllerBean.class);

    @Override
    public void setarCoeficienteDiario(List<TransferObject> coeficientes, AcessoSistema responsavel) throws CoeficienteControllerException {
        try {
            LogDelegate log = new LogDelegate(responsavel, Log.COEFICIENTE_ATIVO, Log.UNLOCK, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.editando.coeficiente.diario", responsavel));
            log.write();
            setarCoeficiente(CodedValues.CFT_DIARIO, coeficientes);
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CoeficienteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void setarCoeficienteMensal(List<TransferObject> coeficientes, AcessoSistema responsavel) throws CoeficienteControllerException {
        try {
            LogDelegate log = new LogDelegate(responsavel, Log.COEFICIENTE_ATIVO, Log.UNLOCK, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.editando.coeficiente.mensal", responsavel));
            log.write();
            setarCoeficiente(CodedValues.CFT_MENSAL, coeficientes);
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CoeficienteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void setarCoeficiente(String tipo, List<TransferObject> coeficientes) throws CoeficienteControllerException {
        if (!tipo.equals(CodedValues.CFT_DIARIO) && !tipo.equals(CodedValues.CFT_MENSAL)) {
            throw new CoeficienteControllerException("mensagem.erro.coeficiente.parametro.invalido", (AcessoSistema) null);
        }

        try {
            Map<Short, String> prazos = null;
            String cftCodigo;
            BigDecimal cftVlr;
            Short cftDia;
            Iterator<TransferObject> it = coeficientes.iterator();
            while (it.hasNext()) {
                TransferObject cto = it.next();
                cftCodigo = cto.getAttribute(Columns.CFT_CODIGO).toString();
                cftVlr = new BigDecimal(NumberHelper.reformat(cto.getAttribute(Columns.CFT_VLR).toString(), NumberHelper.getLang(), "en", 2, 8));

                if (cftCodigo != null && !cftCodigo.equals("")) {
                    CoeficienteAtivo cfaBean = CoeficienteAtivoHome.findByPrimaryKey(cftCodigo);
                    cfaBean.setCftVlr(cftVlr);
                    CoeficienteAtivoHome.update(cfaBean);
                } else {
                    if (prazos == null) {
                        ListaServicoPrazoAtivoQuery query = new ListaServicoPrazoAtivoQuery();
                        query.csaCodigo = cto.getAttribute(Columns.PZC_CSA_CODIGO).toString();
                        query.svcCodigo = cto.getAttribute(Columns.PRZ_SVC_CODIGO).toString();
                        query.prazo = true;

                        prazos = query.executarMapa();
                    }
                    if (tipo.equals(CodedValues.CFT_DIARIO)) {
                        cftDia = Short.valueOf(cto.getAttribute(Columns.CFT_DIA).toString());
                    } else {
                        cftDia = Short.valueOf("0");
                    }
                    CoeficienteAtivoHome.create(prazos.get(cto.getAttribute(Columns.PRZ_VLR)).toString(), cftDia, cftVlr, null, null, null, null);
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CoeficienteControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
