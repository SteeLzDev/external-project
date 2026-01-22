package com.zetra.econsig.persistence.entity;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p> Title: ArquivoFaturamentoBeneficioHome</p>
 * <p> Description: Classe Home para a entidade ArquivoFaturamentoBen</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft </p>
 * $Author$ $Revision$ $Date$
 */
public class ArquivoFaturamentoBeneficioHome extends AbstractEntityHome {

	public static ArquivoFaturamentoBen findByPrimaryKey(Integer afbCodigo) throws FindException {
		ArquivoFaturamentoBen tipoLancamento = new ArquivoFaturamentoBen();
		tipoLancamento.setAfbCodigo(afbCodigo);
		return find(tipoLancamento, afbCodigo);
	}

	public static ArquivoFaturamentoBen save(ArquivoFaturamentoBen bean) throws CreateException, UpdateException {

		Session session = SessionUtil.getSession();

		try {
			if (bean.getAfbCodigo() == null) {
				create(bean, session);
			} else {
				update(bean);
			}
		} finally {
			SessionUtil.closeSession(session);
		}

		return bean;

	}
}
