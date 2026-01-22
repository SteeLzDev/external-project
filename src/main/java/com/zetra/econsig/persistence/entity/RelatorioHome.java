package com.zetra.econsig.persistence.entity;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: RelatorioHome</p>
 * <p>Description: Classe Home para a entidade Relatório</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioHome extends AbstractEntityHome {

    public static Relatorio findByPrimaryKey(String relCodigo) throws FindException {
        Relatorio relatorio = new Relatorio();
        relatorio.setRelCodigo(relCodigo);
        return find(relatorio, relCodigo);
    }

    public static Relatorio create(String relCodigo, String funCodigo, String tagCodigo, String relTitulo, Short relAtivo, String relAgendado,
                                   String relClasseRelatorio, String relClasseProcesso, String relClasseAgendamento, String relTemplateJasper,
                                   String relTemplateDinamico, String relTemplateSubrelatorio, String relTemplateSql, Short relQtdDiasLimpeza,
                                   String relCustomizado, String relAgrupamento) throws CreateException {

        Session session = SessionUtil.getSession();
        Relatorio bean = new Relatorio();

        try {
            bean.setRelCodigo(relCodigo);
            bean.setFuncao((Funcao) session.getReference(Funcao.class, funCodigo));
            if (!TextHelper.isNull(tagCodigo)) {
                bean.setTipoAgendamento((TipoAgendamento) session.getReference(TipoAgendamento.class, tagCodigo));
            }
            bean.setRelTitulo(relTitulo);
            bean.setRelAtivo(relAtivo);
            bean.setRelAgendado(relAgendado);
            bean.setRelClasseRelatorio(relClasseRelatorio);
            bean.setRelClasseProcesso(relClasseProcesso);
            bean.setRelClasseAgendamento(relClasseAgendamento);
            bean.setRelTemplateJasper(relTemplateJasper);
            bean.setRelTemplateDinamico(relTemplateDinamico);
            bean.setRelTemplateSubrelatorio(relTemplateSubrelatorio);
            bean.setRelTemplateSql(relTemplateSql);
            bean.setRelQtdDiasLimpeza(relQtdDiasLimpeza);
            bean.setRelCustomizado(relCustomizado);
            bean.setRelAgrupamento(relAgrupamento);
            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
