package com.zetra.econsig.persistence.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: HistoricoProcessamentoHome</p>
 * <p>Description: Classe Home para manutenção de histórico de processamento.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoProcessamentoHome extends AbstractEntityHome {

    public static HistoricoProcessamento create(HistoricoProcessamento historico, String tipoEntidade, String codigoEntidade) throws CreateException {
        String orgCodigo = (tipoEntidade != null && tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) ? codigoEntidade : null);
        String estCodigo = (tipoEntidade != null && tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST) ? codigoEntidade : null);

        return create(historico.getHprPeriodo(), historico.getHprArquivoMargem(), historico.getHprConfEntradaMargem(), historico.getHprConfTradutorMargem(), historico.getHprLinhasArquivoMargem(),
                historico.getHprArquivoRetorno(), historico.getHprConfEntradaRetorno(), historico.getHprConfTradutorRetorno(), historico.getHprLinhasArquivoRetorno(),
                historico.getHprChaveIdentificacao(), historico.getHprOrdemExcCamposChave(), estCodigo, orgCodigo);
    }

    public static HistoricoProcessamento create(Date hprPeriodo, String hprArquivoMargem, String hprConfEntradaMargem, String hprConfTradutorMargem, Integer hprLinhasArquivoMargem,
            String hprArquivoRetorno, String hprConfEntradaRetorno, String hprConfTradutorRetorno, Integer hprLinhasArquivoRetorno,
            String hprChaveIdentificacao, String hprOrdemExcCamposChave, String estCodigo, String orgCodigo) throws CreateException {

        HistoricoProcessamento bean = new HistoricoProcessamento();
        Session session = SessionUtil.getSession();

        try {
            bean.setHprCodigo(DBHelper.getNextId());
            bean.setHprPeriodo(hprPeriodo);
            bean.setHprDataIni(DateHelper.getSystemDatetime());

            bean.setHprArquivoMargem(hprArquivoMargem);
            bean.setHprConfEntradaMargem(hprConfEntradaMargem);
            bean.setHprConfTradutorMargem(hprConfTradutorMargem);
            bean.setHprLinhasArquivoMargem(hprLinhasArquivoMargem);

            bean.setHprArquivoRetorno(hprArquivoRetorno);
            bean.setHprConfEntradaRetorno(hprConfEntradaRetorno);
            bean.setHprConfTradutorRetorno(hprConfTradutorRetorno);
            bean.setHprLinhasArquivoRetorno(hprLinhasArquivoRetorno);

            bean.setHprChaveIdentificacao(hprChaveIdentificacao);
            bean.setHprOrdemExcCamposChave(hprOrdemExcCamposChave);

            if (!TextHelper.isNull(estCodigo)) {
                bean.setEstabelecimento(session.getReference(Estabelecimento.class, estCodigo));
            }
            if (!TextHelper.isNull(orgCodigo)) {
                bean.setOrgao(session.getReference(Orgao.class, orgCodigo));
            }

            bean = create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static List<HistoricoProcessamento> findProcessamentosNaoFinalizados() throws FindException {
        String query = "FROM HistoricoProcessamento hpr WHERE hpr.hprDataFim IS NULL ORDER BY hpr.hprDataIni";
        return findByQuery(query, null);
    }
}
