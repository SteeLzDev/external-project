package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: EstabelecimentoHome</p>
 * <p>Description: Classe Home para a entidade EnderecoConjHabitacional</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author: junio $
 * $Revision:  $
 * $Date: 2012-11-28 17:25:00 -0300 (qua, 28 nov 2012) $
 */
public class EnderecoConjuntoHabitacionalHome extends AbstractEntityHome {

    public static EnderecoConjHabitacional findByPrimaryKey(String echCodigo) throws FindException {
    	EnderecoConjHabitacional enderecoConjHabitacional = new EnderecoConjHabitacional();
    	enderecoConjHabitacional.setEchCodigo(echCodigo);
        return find(enderecoConjHabitacional, echCodigo);
    }

	public static EnderecoConjHabitacional create(String consignataria, String echIdentificador,
													String echDescricao, String echCondominio,
													Short echQtdUnidades) throws CreateException {

        Session session = SessionUtil.getSession();
	    EnderecoConjHabitacional bean = new EnderecoConjHabitacional();

	    try {
	    	bean.setEchCodigo(DBHelper.getNextId());
	    	bean.setConsignataria(session.getReference(Consignataria.class, consignataria));
	    	bean.setEchIdentificador(echIdentificador);
	    	bean.setEchDescricao(echDescricao);
	    	bean.setEchCondominio(echCondominio);
	    	bean.setEchQtdUnidades(echQtdUnidades);
	    	bean.setEchCondominio(echCondominio);

            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
	}

	public static EnderecoConjHabitacional findByIdn(String echIdentificador, String csaCodigo) throws FindException {
		return findByIdn(echIdentificador, csaCodigo, null);
	}

    public static EnderecoConjHabitacional findByIdn(String echIdentificador, String csaCodigo, String echCodigo) throws FindException {
        String query = "FROM EnderecoConjHabitacional ech WHERE ech.echIdentificador = :echIdentificador AND ech.consignataria.csaCodigo = :csaCodigo ";

        if(!TextHelper.isNull(echCodigo)){
        	query += "AND ech.echCodigo <> :echCodigo";
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("echIdentificador", echIdentificador);
        parameters.put("csaCodigo", csaCodigo);

        if(!TextHelper.isNull(echCodigo)){
        	parameters.put("echCodigo", echCodigo);
        }


        List<EnderecoConjHabitacional> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }
}
