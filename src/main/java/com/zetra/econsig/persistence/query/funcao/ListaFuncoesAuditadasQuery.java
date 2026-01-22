package com.zetra.econsig.persistence.query.funcao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ListaFuncoesAuditadasQuery</p>
 * <p>Description: Lista as funções marcadas como auditáveis no sistema.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaFuncoesAuditadasQuery extends HQuery {
    public String tipo;
    public String codigoEntidade;
    public boolean count;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(tipo)) {
            throw new HQueryException("mensagem.erro.informe.tipo.entidade.recuperar.funcoes.auditaveis", (AcessoSistema) null);
        }

        String corpo = "";
        if (count) {
            corpo = "select count(*) from ";
        } else {
            corpo = "select funAudit.funCodigo from ";
        }

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        if (AcessoSistema.ENTIDADE_CSE.equals(tipo)) {
            corpoBuilder.append(" FuncaoAuditavelCse funAudit where funAudit.cseCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
        } else if (AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
            corpoBuilder.append(" FuncaoAuditavelCsa funAudit where funAudit.csaCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
        } else if (AcessoSistema.ENTIDADE_COR.equals(tipo)) {
            corpoBuilder.append(" FuncaoAuditavelCor funAudit where funAudit.corCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
        } else if (AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
            corpoBuilder.append(" FuncaoAuditavelOrg funAudit where funAudit.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
        }else if (AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
            corpoBuilder.append(" FuncaoAuditavelSup funAudit where funAudit.cseCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(codigoEntidade)) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        return query;
    }

}
