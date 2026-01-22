package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: HistoricoArquivoHome</p>
 * <p>Description: Classe Home para a entidade HistoricoArquivo</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoArquivoHome extends AbstractEntityHome {

    public static HistoricoArquivo findByPrimaryKey(Long harCodigo) throws FindException {
        HistoricoArquivo historicoArquivo = new HistoricoArquivo();
        historicoArquivo.setHarCodigo(harCodigo);
        return find(historicoArquivo, harCodigo);
    }

    public static HistoricoArquivo create(String usuCodigo, TipoArquivoEnum tipoArquivo, String harNomeArquivo, String harObs,
                                          Date harDataProc, Date harPeriodo, Integer harQtdLinhas, String harResultadoProc, String funCodigo) throws CreateException {

        Session session = SessionUtil.getSession();
        HistoricoArquivo bean = new HistoricoArquivo();

        try {
            if (harDataProc == null) {
                harDataProc = DateHelper.getSystemDatetime();
            }
            bean.setHarDataProc(harDataProc);
            bean.setUsuario((Usuario) session.getReference(Usuario.class, usuCodigo));
            bean.setTipoArquivo((TipoArquivo) session.getReference(TipoArquivo.class, tipoArquivo.getCodigo()));
            bean.setHarNomeArquivo(harNomeArquivo);
            bean.setHarObs(harObs);
            bean.setHarPeriodo(harPeriodo);
            bean.setHarQtdLinhas(harQtdLinhas);
            bean.setHarResultadoProc(harResultadoProc);
            
            if (!TextHelper.isNull(funCodigo)) { 
            	bean.setFuncao((Funcao) session.getReference(Funcao.class, funCodigo));
            }

            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

}
