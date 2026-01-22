package com.zetra.econsig.persistence.entity;

import java.util.Date;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CanalEnum;

/**
 * <p>Title: LogHome</p>
 * <p>Description: Classe Home para a entidade Log</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LogHome extends AbstractEntityHome {

    public static Log findByPrimaryKey(String logCodigo) throws FindException {
        throw new FindException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }

    public static Log create(String logCodEnt00, String logCodEnt01, String logCodEnt02, String logCodEnt03, String logCodEnt04, String logCodEnt05,
                             String logCodEnt06, String logCodEnt07, String logCodEnt08, String logCodEnt09, String logCodEnt10,
                             Date logDate, String logObs, String logIp, Integer logPortaLogica, Usuario usuario, TipoEntidade tipoEntidade, TipoLog tipoLog, Funcao funcao, CanalEnum logCanal) throws CreateException {
        Log bean = new Log();

        if (!TextHelper.isNull(logCodEnt00)) {
            bean.setLogCodEnt00(logCodEnt00);
        }
        if (!TextHelper.isNull(logCodEnt01)) {
            bean.setLogCodEnt01(logCodEnt01);
        }
        if (!TextHelper.isNull(logCodEnt02)) {
            bean.setLogCodEnt02(logCodEnt02);
        }
        if (!TextHelper.isNull(logCodEnt03)) {
            bean.setLogCodEnt03(logCodEnt03);
        }
        if (!TextHelper.isNull(logCodEnt04)) {
            bean.setLogCodEnt04(logCodEnt04);
        }
        if (!TextHelper.isNull(logCodEnt05)) {
            bean.setLogCodEnt05(logCodEnt05);
        }
        if (!TextHelper.isNull(logCodEnt06)) {
            bean.setLogCodEnt06(logCodEnt06);
        }
        if (!TextHelper.isNull(logCodEnt07)) {
            bean.setLogCodEnt07(logCodEnt07);
        }
        if (!TextHelper.isNull(logCodEnt08)) {
            bean.setLogCodEnt08(logCodEnt08);
        }
        if (!TextHelper.isNull(logCodEnt09)) {
            bean.setLogCodEnt09(logCodEnt09);
        }
        if (!TextHelper.isNull(logCodEnt10)) {
            bean.setLogCodEnt10(logCodEnt10);
        }

        bean.setLogData(logDate);
        bean.setLogObs(logObs);
        if (logIp != null) {
            bean.setLogIp(logIp);
        }
        if (logPortaLogica != null) {
            bean.setLogPortaLogica(logPortaLogica);
        }
        if (usuario != null) {
            bean.setUsuario(usuario);
        }
        if (tipoEntidade != null) {
            bean.setTipoEntidade(tipoEntidade);
        }
        if (funcao != null) {
            bean.setFuncao(funcao);
        }
        bean.setTipoLog(tipoLog);
        
        if (logCanal != null) {
            bean.setLogCanal(logCanal.getCodigo());
        } else {
			bean.setLogCanal(CanalEnum.WEB.getCodigo());
		}

        bean = create(bean);

        // Removendo o Bean Log do cache do Hibernate para evitar problemas do tipo:
        // org.hibernate.NonUniqueObjectException
        SessionUtil.getSession().evict(bean);
        return bean;
    }

}
