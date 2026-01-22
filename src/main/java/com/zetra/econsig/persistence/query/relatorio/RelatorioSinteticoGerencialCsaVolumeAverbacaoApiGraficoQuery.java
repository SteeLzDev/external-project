package com.zetra.econsig.persistence.query.relatorio;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: RelatorioVolumeAverbacaoCsaQuery</p>
 * <p>Description: Recuperar voluma averbação por tipo gráfico</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoGerencialCsaVolumeAverbacaoApiGraficoQuery extends ReportHQuery {

    public AcessoSistema responsavel;
    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final List<String> funCodigos = new ArrayList<>();
        funCodigos.add(CodedValues.FUN_CONS_CONSIGNACAO);
        funCodigos.add(CodedValues.FUN_RES_MARGEM);
        funCodigos.add(CodedValues.FUN_CONS_MARGEM);

        final String logCanalSoap = CanalEnum.SOAP.getCodigo();

        final StringBuilder corpo = new StringBuilder();
        corpo.append(" select fun.funDescricao tipo,");
        corpo.append("   to_decimal(COUNT(*) / SUM(COUNT(*)) OVER() * 100, 5, 2) AS percentual");
        corpo.append(" from Log tlog");
        corpo.append(" inner join tlog.funcao fun");
        corpo.append(" INNER JOIN tlog.usuario usu");
        corpo.append(" INNER JOIN usu.usuarioCsaSet usuCsa");
        corpo.append(" where fun.funCodigo ").append(criaClausulaNomeada("funCodigos", funCodigos));
        corpo.append(" and tlog.logCanal ").append(criaClausulaNomeada("logCanalSoap", logCanalSoap));
        corpo.append(" and tlog.logData between add_month(:periodoIni, -6 ) and :periodoFim ");
        corpo.append(" AND usuCsa.csaCodigo = :csaCodigo");
        corpo.append(" group by fun.funCodigo ");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("funCodigos", funCodigos, query);
        defineValorClausulaNomeada("logCanalSoap", logCanalSoap, query);

        try {
        	defineValorClausulaNomeada("periodoIni", DateHelper.parse(DateHelper.format(DateHelper.getSystemDatetime(), "yyyy-MM-dd") + " 00:00:00", "yyyy-MM-dd HH:mm:ss"), query);
        	defineValorClausulaNomeada("periodoFim", DateHelper.parse(DateHelper.format(DateHelper.getSystemDatetime(), "yyyy-MM-dd") + " 23:59:59", "yyyy-MM-dd HH:mm:ss"), query);
        } catch (ParseException ex) {
            throw new HQueryException("mensagem.erro.data.fim.parse.invalido",  (AcessoSistema) null);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
            "TIPO",
            "PERCENTUAL"
        };
    }
}
