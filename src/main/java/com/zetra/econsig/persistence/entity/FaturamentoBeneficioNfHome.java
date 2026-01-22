package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: FaturamentoBeneficioNfHome</p>
 * <p>Description: Classe Home para a entidade FaturamentoBeneficioNf</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FaturamentoBeneficioNfHome extends AbstractEntityHome {

    public static FaturamentoBeneficioNf findByPrimaryKey(String fnfCodigo) throws FindException {
        FaturamentoBeneficioNf faturamentoBeneficioNf = new FaturamentoBeneficioNf();
        faturamentoBeneficioNf.setFnfCodigo(fnfCodigo);
        return find(faturamentoBeneficioNf, fnfCodigo);
    }

    public static FaturamentoBeneficioNf save(FaturamentoBeneficioNf faturamentoBeneficioNf) throws CreateException {

        Session session = SessionUtil.getSession();

        try {

            if (TextHelper.isNull(faturamentoBeneficioNf.getFnfCodigo())) {
                String objectId = DBHelper.getNextId();
                faturamentoBeneficioNf.setFnfCodigo(objectId);
                create(faturamentoBeneficioNf);
            } else {
                update(faturamentoBeneficioNf);
            }

            return faturamentoBeneficioNf;

        } catch (MissingPrimaryKeyException | UpdateException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

    }

    public static List<FaturamentoBeneficioNf> findByFatCodigo(String fatCodigo) throws FindException {
        String query = "FROM FaturamentoBeneficioNf fnf join fetch fnf.tipoNotaFiscal WHERE fnf.faturamentoBeneficio.fatCodigo = :fatCodigo";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fatCodigo", fatCodigo);
        return findByQuery(query.toString(), parameters);
    }

	public static List<FaturamentoBeneficioNf> listar() throws FindException {
		String query = "from FaturamentoBeneficioNf";
		return findByQuery(query, null);
	}

}

