package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: FormularioPesquisaRespostaHome</p>
 * <p>Description: Classe Home para a entidade Formulário de Pesquisa Resposta</p>s
 * <p>Copyright: Copyright (c) 2025</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FormularioPesquisaRespostaHome extends AbstractEntityHome {

    public static FormularioPesquisaResposta findByPrimaryKey(String fprCodigo) throws FindException {
        FormularioPesquisaResposta formularioPesquisaResposta = new FormularioPesquisaResposta();
        formularioPesquisaResposta.setFprCodigo(fprCodigo);
        return find(formularioPesquisaResposta, fprCodigo);
    }

    public static FormularioPesquisaResposta createFormularioPesquisaResposta(FormularioPesquisaResposta formularioPesquisaResposta) throws CreateException {
        try {
            formularioPesquisaResposta.setFprCodigo(DBHelper.getNextId());
            return create(formularioPesquisaResposta);
        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }
    }

    public static void updateFormularioPesquisaResposta(FormularioPesquisaResposta formularioPesquisaResposta) throws UpdateException {
        update(formularioPesquisaResposta);
    }

}
