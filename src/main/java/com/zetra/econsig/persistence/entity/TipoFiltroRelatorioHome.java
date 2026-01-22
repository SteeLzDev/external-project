package com.zetra.econsig.persistence.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: TipoFiltroRelatorioHome</p>
 * <p>Description: Classe Home para a entidade TipoFiltroRelatorio</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoFiltroRelatorioHome extends AbstractEntityHome {

    public static TipoFiltroRelatorio findByPrimaryKey(String tfrCodigo) throws FindException {
        TipoFiltroRelatorio bean = new TipoFiltroRelatorio();
        bean.setTfrCodigo(tfrCodigo);
        return find(bean, tfrCodigo);
    }

    public static Collection<TipoFiltroRelatorio> findAllTipoFiltroRelatorio() throws FindException {
        String query = "FROM TipoFiltroRelatorio tfr";

        return findByQuery(query, null);
    }

    public static Collection<TipoFiltroRelatorio> findTipoFiltroRelatorioEditavel() throws FindException {
        String query = "FROM TipoFiltroRelatorio tfr where tfrExibeEdicao = :tfrExibeEdicao";

        String tfrExibeEdicao = CodedValues.TPC_SIM;
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("tfrExibeEdicao", tfrExibeEdicao);

        return findByQuery(query, parameters);
    }

    public static TipoFiltroRelatorio create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema)null);
    }
}
