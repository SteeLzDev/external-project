package com.zetra.econsig.job;

import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: AbstractJob</p>
 * <p>Description: Classe abstrata que encapsula o métodos para execução de trabalho.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class AbstractJob {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AbstractJob.class);

    private String agdCodigo;
    private String usuCodigo;
    private AcessoSistema responsavel;

    public String getAgdCodigo() {
        return agdCodigo;
    }

    public void setAgdCodigo(String agdCodigo) {
        this.agdCodigo = agdCodigo;
    }

    public String getUsuCodigo() {
        return usuCodigo;
    }

    public void setUsuCodigo(String usuCodigo) {
        this.usuCodigo = usuCodigo;

        try {
            if (TextHelper.isNull(usuCodigo) || usuCodigo.equals(CodedValues.USU_CODIGO_SISTEMA)) {
                responsavel = AcessoSistema.getAcessoUsuarioSistema();
            } else {
                responsavel = AcessoSistema.recuperaAcessoSistema(usuCodigo.toString(), null, null);
                UsuarioDelegate usuDelegate = new UsuarioDelegate();
                responsavel.setPermissoes(usuDelegate.selectFuncoes(responsavel.getUsuCodigo(), responsavel.getCodigoEntidade(), responsavel.getTipoEntidade(), responsavel));
            }
        } catch (ZetraException e) {
            LOG.error("Não foi possível recuperar o responsável. " + e.getMessage());
        }
    }

    public AcessoSistema getResponsavel() {
        return responsavel != null ? responsavel : AcessoSistema.getAcessoUsuarioSistema();
    }

    public abstract void executar();
}
