package com.zetra.econsig.delegate;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.RegraConvenioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.regraconvenio.RegraConvenioController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: CompraContratoDelegate</p>
 * <p>Description: Delegate para manipulacao da Regra Convênio.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraConvenioDelegate extends AbstractDelegate {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RegraConvenioDelegate.class);

    private RegraConvenioController controller = null;

    public RegraConvenioDelegate() throws RegraConvenioControllerException {
        try {
            controller = ApplicationContextProvider.getApplicationContext().getBean(RegraConvenioController.class);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RegraConvenioControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    public List<TransferObject> listaRegrasConvenioByCsa(String csaCodigo, AcessoSistema responsavel) throws RegraConvenioControllerException {
        return controller.listaRegrasConvenioByCsa(csaCodigo, responsavel);
    }
}
