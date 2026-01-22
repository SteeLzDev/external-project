package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: CoeficienteCorrecaoHome</p>
 * <p>Description: Classe Home para a entidade CoeficienteCorrecao</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CoeficienteCorrecaoHome extends AbstractEntityHome {

    public static CoeficienteCorrecao findByPrimaryKey(CoeficienteCorrecaoId id) throws FindException {
        CoeficienteCorrecao coeficienteCorrecao = new CoeficienteCorrecao();
        coeficienteCorrecao.setId(id);
        return find(coeficienteCorrecao, id);
    }

    public static List<CoeficienteCorrecao> findByTccCodigo(String tccCodigo) throws FindException {
        String query = "FROM CoeficienteCorrecao ccr WHERE ccr.tipoCoeficienteCorrecao.tccCodigo = :tccCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("tccCodigo", tccCodigo);

        return findByQuery(query, parameters);
    }

    public static CoeficienteCorrecao create(String tccCodigo, BigDecimal ccrVlr, short ccrMes, short ccrAno, BigDecimal ccrVlrAcumulado) throws CreateException {
        CoeficienteCorrecao bean = new CoeficienteCorrecao();

        bean.setId(new CoeficienteCorrecaoId(tccCodigo, ccrMes, ccrAno));
        bean.setCcrVlr(ccrVlr);
        bean.setCcrVlrAcumulado(ccrVlrAcumulado);
        create(bean);
        return bean;
    }
}
