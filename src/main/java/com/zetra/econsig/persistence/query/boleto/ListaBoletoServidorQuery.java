package com.zetra.econsig.persistence.query.boleto;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: ListaBoletoServidorQuery</p>
 * <p>Description: Lista boletos do servidor</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaBoletoServidorQuery extends HQuery {

    public boolean count = false;
    public String serCpf;
    public String serNome;
    public boolean listarSomenteNaoBaixados = false;
    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String tarCodigo = TipoArquivoEnum.ARQUIVO_BOLETO_PARCELA_EM_ATRASO.getCodigo();

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ");

        if (!count) {
            corpoBuilder.append("bos.bosCodigo, ");
            corpoBuilder.append("ser.serCodigo, ");
            corpoBuilder.append("ser.serNome, ");
            corpoBuilder.append("ser.serCpf, ");
            corpoBuilder.append("csa.csaCodigo, ");
            corpoBuilder.append("csa.csaNome, ");
            corpoBuilder.append("arq.arqCodigo, ");
            corpoBuilder.append("arq.arqConteudo, ");
            corpoBuilder.append("tar.tarCodigo, ");
            corpoBuilder.append("tar.tarDescricao, ");
            corpoBuilder.append("bos.bosDataUpload, ");
            corpoBuilder.append("bos.bosDataDownload, ");
            corpoBuilder.append("bos.bosDataExclusao ");
        } else {
            corpoBuilder.append("count(*) as total ");
        }

        corpoBuilder.append("from BoletoServidor bos ");
        corpoBuilder.append("inner join bos.servidor ser ");
        corpoBuilder.append("inner join bos.arquivo arq ");
        corpoBuilder.append("inner join arq.tipoArquivo tar ");
        corpoBuilder.append("inner join bos.consignataria csa ");

        if (responsavel.isSer()) {
            corpoBuilder.append("inner join ser.registroServidorSet rse ");
        }

        corpoBuilder.append("where 1 = 1 ");
        corpoBuilder.append(" and tar.tarCodigo ").append(criaClausulaNomeada("tarCodigo", tarCodigo));

        if (!TextHelper.isNull(serCpf)) {
            corpoBuilder.append(" and ser.serCpf ").append(criaClausulaNomeada("serCpf", serCpf));
        }
        if (!TextHelper.isNull(serNome)) {
            corpoBuilder.append(" and ser.serNome").append(criaClausulaNomeada("serNome", serNome));
        }
        if (responsavel.isCsa()) {
            corpoBuilder.append(" and csa.csaCodigo").append(criaClausulaNomeada("csaCodigo", responsavel.getCsaCodigo()));
        }
        if (responsavel.isSer()) {
            corpoBuilder.append(" and rse.rseCodigo").append(criaClausulaNomeada("rseCodigo", responsavel.getRseCodigo()));
        }
        if (listarSomenteNaoBaixados) {
            // Consultar BOS_DATA_DOWNLOAD nulo, ou seja ainda não foi visto
            corpoBuilder.append(" and bos.bosDataDownload ").append(criaClausulaNomeada("bosDataDownload", CodedValues.IS_NULL_KEY));
        }

        if (!count) {
            corpoBuilder.append(" order by bos.bosDataUpload desc ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("tarCodigo", tarCodigo, query);

        if (!TextHelper.isNull(serCpf)) {
            defineValorClausulaNomeada("serCpf", serCpf, query);
        }
        if (!TextHelper.isNull(serNome)) {
            defineValorClausulaNomeada("serNome", serNome, query);
        }
        if (responsavel.isCsa()) {
            defineValorClausulaNomeada("csaCodigo", responsavel.getCsaCodigo(), query);
        }
        if (responsavel.isSer()) {
            defineValorClausulaNomeada("rseCodigo", responsavel.getRseCodigo(), query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.BOS_CODIGO,
                Columns.SER_CODIGO,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.CSA_CODIGO,
                Columns.CSA_NOME,
                Columns.BOS_ARQ_CODIGO,
                Columns.ARQ_CONTEUDO,
                Columns.TAR_CODIGO,
                Columns.TAR_DESCRICAO,
                Columns.BOS_DATA_UPLOAD,
                Columns.BOS_DATA_DOWNLOAD,
                Columns.BOS_DATA_EXCLUSAO
            };
    }
}
