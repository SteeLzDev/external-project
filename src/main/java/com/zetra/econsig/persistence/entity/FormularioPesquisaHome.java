package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: FormularioPesquisaHome</p>
 * <p>Description: Classe Home para a entidade Formulário de Pesquisa</p>s
 * <p>Copyright: Copyright (c) 2025</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FormularioPesquisaHome extends AbstractEntityHome {

    public static FormularioPesquisa findByPrimaryKey(String fpeCodigo) throws FindException {
        FormularioPesquisa formularioPesquisa = new FormularioPesquisa();
        formularioPesquisa.setFpeCodigo(fpeCodigo);
        return find(formularioPesquisa, fpeCodigo);
    }

    public static FormularioPesquisa createFormularioPesquisa(FormularioPesquisa formularioPesquisa) throws CreateException {
        try {
            formularioPesquisa.setFpeCodigo(DBHelper.getNextId());
            return create(formularioPesquisa);
        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }
    }

    public static void updateFormularioPesquisa(FormularioPesquisa formularioPesquisa) throws UpdateException {
        update(formularioPesquisa);
    }

    public static void removeFormularioPesquisa(FormularioPesquisa formularioPesquisa) throws RemoveException {
        remove(formularioPesquisa);
    }
}
