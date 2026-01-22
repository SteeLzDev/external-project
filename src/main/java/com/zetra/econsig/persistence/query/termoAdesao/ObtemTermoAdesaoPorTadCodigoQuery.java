package com.zetra.econsig.persistence.query.termoAdesao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemTermoAdesaoPorTadCodigoQuery</p>
 * <p>Description: Retornar o termo de adesão a partir de seu tad_codigo.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTermoAdesaoPorTadCodigoQuery extends HQuery {

    public String tadCodigo;
    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder query = new StringBuilder();
        query.append(" SELECT tad.tadCodigo, tad.tadTitulo, tad.tadTexto, tad.tadHtml, tad.tadPermiteRecusar, tad.tadPermiteLerDepois, tad.funCodigo,  ltu.ltuTermoAceito, tad.tadVersaoTermo, tad.tadEnviaApiConsentimento ");
        query.append(", tad.tadClasseAcao, tad.tadExibeAposLeitura ");
        query.append(" FROM TermoAdesao tad ");
        query.append(" LEFT JOIN tad.leituraTermoUsuarioSet ltu ");
        query.append(" WHERE tad.tadCodigo = :tadCodigo ");
        query.append(" ORDER BY ltu.ltuData DESC ");

        final Query<Object[]> bean = instanciarQuery(session, query.toString());

        defineValorClausulaNomeada("tadCodigo", tadCodigo, bean);

        return bean;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TAD_CODIGO,
                Columns.TAD_TITULO,
                Columns.TAD_TEXTO,
                Columns.TAD_HTML,
                Columns.TAD_PERMITE_RECUSAR,
                Columns.TAD_PERMITE_LER_DEPOIS,
                Columns.FUN_CODIGO,
                Columns.LTU_TERMO_ACEITO,
                Columns.TAD_VERSAO_TERMO,
                Columns.TAD_ENVIA_API_CONSENTIMENTO,
                Columns.TAD_CLASSE_ACAO,
                Columns.TAD_EXIBE_APOS_LEITURA
        };
    }
}
