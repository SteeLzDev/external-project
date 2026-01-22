package com.zetra.econsig.delegate;

import java.util.Collection;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.CompraContratoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.compra.CompraContratoController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: CompraContratoDelegate</p>
 * <p>Description: Delegate para manipulacao de Compra de Contrato.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CompraContratoDelegate extends AbstractDelegate {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CompraContratoDelegate.class);

    private CompraContratoController controller = null;

    public CompraContratoDelegate() throws CompraContratoControllerException {
        try {
            controller = ApplicationContextProvider.getApplicationContext().getBean(CompraContratoController.class);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CompraContratoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    public void aplicarPenalidadesPrazosExcedidos(AcessoSistema responsavel) throws CompraContratoControllerException {
        controller.aplicarPenalidadesPrazosExcedidos(responsavel);
    }

    public void retirarContratoCompra(String adeCodigo, String textoObservacao, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws CompraContratoControllerException {
        controller.retirarContratoCompra(adeCodigo, textoObservacao, tipoMotivoOperacao, responsavel);
    }

    public void executarDesbloqueioAutomaticoConsignatarias(Collection<String> adeCodigos, AcessoSistema responsavel) throws CompraContratoControllerException {
        controller.executarDesbloqueioAutomaticoConsignatarias(adeCodigos, responsavel);
    }

    public List<String> recuperarAdesCodigosDestinoCompra(String adeCodigoOrigem) throws CompraContratoControllerException {
        return controller.recuperarAdesCodigosDestinoCompra(adeCodigoOrigem);
    }

    public void liquidarAdeCompraNaoLiquidada(int diasLiqAutomatica, AcessoSistema responsavel) throws CompraContratoControllerException {
        controller.liquidarAdeCompraNaoLiquidada(diasLiqAutomatica, responsavel);
    }
}
