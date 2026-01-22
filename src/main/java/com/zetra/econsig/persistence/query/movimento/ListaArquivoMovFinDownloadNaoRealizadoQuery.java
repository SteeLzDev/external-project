package com.zetra.econsig.persistence.query.movimento;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: ListaArquivoMovFinDownloadNaoRealizadoQuery</p>
 * <p>Description: Lista os arquivos de movimento financeiro que não foram realizados download.</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaArquivoMovFinDownloadNaoRealizadoQuery extends HQuery {

    public Date dataInicio;
    public List<Integer> diasEnvioEmail;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String tipoArquivo = TipoArquivoEnum.ARQUIVO_MOVIMENTO_FINANCEIRO.getCodigo();
        String tipoArquivoDownload = TipoArquivoEnum.ARQUIVO_MOVIMENTO_FINANCEIRO_DOWNLOAD.getCodigo();

        StringBuilder corpo = new StringBuilder();

        corpo.append(" select ");
        corpo.append(" har.harNomeArquivo, min(har.harDataProc) AS DATA_PROC ");

        corpo.append(" from HistoricoArquivo har ");
        corpo.append(" inner join har.tipoArquivo tar ");
        corpo.append(" inner join har.historicoArquivoCseSet hac  ");

        corpo.append(" where tar.tarCodigo ").append(criaClausulaNomeada("tipoArquivo", tipoArquivo));
        corpo.append(" and har.harDataProc > :dataInicio ");
        corpo.append(" and date_diff(data_corrente(), har.harDataProc) ").append(criaClausulaNomeada("diasEnvioEmail", diasEnvioEmail));

        corpo.append(" and not exists (select 1 ");
        corpo.append(" from HistoricoArquivo har2 ");
        corpo.append(" where har.harNomeArquivo = har2.harNomeArquivo ");
        corpo.append(" and har2.tipoArquivo.tarCodigo ").append(criaClausulaNomeada("tipoArquivoDownload", tipoArquivoDownload)).append(") ");

        corpo.append(" group by har.harNomeArquivo");
        corpo.append(" order by 2");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        defineValorClausulaNomeada("tipoArquivo", tipoArquivo, query);
        defineValorClausulaNomeada("tipoArquivoDownload", tipoArquivoDownload, query);
        defineValorClausulaNomeada("dataInicio", DateHelper.toSQLDate(dataInicio), query);
        defineValorClausulaNomeada("diasEnvioEmail", diasEnvioEmail, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.HAR_NOME_ARQUIVO,
                "DATA_PROC"
            };
    }
}
