package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.texto.DateHelper;

/**
 * <p>Title: DbOcorrenciaHome</p>
 * <p>Description: Gerenciamento da entidade do mapeamento da tabela tb_db_ocorrencia.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DbOcorrenciaHome extends AbstractEntityHome {

    public static DbOcorrencia findByPrimaryKey(Integer dboCodigo) throws FindException {
        return find(new DbOcorrencia(dboCodigo), dboCodigo);
    }

    public static List<DbOcorrencia> findByArquivo(String dboArquivo) throws FindException {
        String query = "FROM DbOcorrencia dbo WHERE dbo.dboArquivo LIKE :dboArquivo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("dboArquivo", "%" + dboArquivo + "%");

        return findByQuery(query, parameters);
    }

    public static DbOcorrencia create(String dboArquivo) throws CreateException {
        DbOcorrencia dbo = new DbOcorrencia(DateHelper.getSystemDatetime(), dboArquivo);
        create(dbo);
        return dbo;
    }
}
