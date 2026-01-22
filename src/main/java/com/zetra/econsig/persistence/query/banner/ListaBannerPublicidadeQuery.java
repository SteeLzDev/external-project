package com.zetra.econsig.persistence.query.banner;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;

public class ListaBannerPublicidadeQuery extends HQuery {

    public boolean count = false;
    public String exibeMobile;
    public String nseCodigo;
    public String bpuCodigo;
    public AcessoSistema responsavel;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {

        String tarCodigo = TipoArquivoEnum.ARQUIVO_IMAGEM_BANNER_PUBLICIDADE.getCodigo();

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ");

        if (!count) {
            corpoBuilder.append("bpu.bpuCodigo, ");
            corpoBuilder.append("arq.arqCodigo, ");
            corpoBuilder.append("arq.arqConteudo, ");
            corpoBuilder.append("tar.tarCodigo, ");
            corpoBuilder.append("tar.tarDescricao, ");
            corpoBuilder.append("nse.nseCodigo, ");
            corpoBuilder.append("nse.nseDescricao, ");
            corpoBuilder.append("bpu.bpuDescricao, ");
            corpoBuilder.append("bpu.bpuUrlSaida, ");
            corpoBuilder.append("bpu.bpuOrdem, ");
            corpoBuilder.append("bpu.bpuData ");
        } else {
            corpoBuilder.append("count(*) as total ");
        }

        corpoBuilder.append("from BannerPublicidade bpu ");
        corpoBuilder.append("inner join bpu.arquivo arq ");
        corpoBuilder.append("inner join arq.tipoArquivo tar ");
        corpoBuilder.append("left join bpu.naturezaServico nse ");

        corpoBuilder.append("where 1 = 1 ");
        corpoBuilder.append("and bpu.bpuData is not null ");

        corpoBuilder.append(" and tar.tarCodigo ").append(criaClausulaNomeada("tarCodigo", tarCodigo));

        if (!TextHelper.isNull(nseCodigo)) {
            corpoBuilder.append(" and nse.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }

        if (!TextHelper.isNull(exibeMobile)) {
            corpoBuilder.append(" and bpu.bpuExibeMobile ").append(criaClausulaNomeada("exibeMobile", exibeMobile));
        } else {
            corpoBuilder.append(" and bpu.bpuExibeMobile ='").append(CodedValues.TPC_NAO).append("'");
        }

        if (!TextHelper.isNull(bpuCodigo)) {
            corpoBuilder.append(" and bpu.bpuCodigo ").append(criaClausulaNomeada("bpuCodigo", bpuCodigo));
        }

        corpoBuilder.append(" and bpu.bpuUrlSaida is not null");
        corpoBuilder.append(" order by bpu.bpuOrdem");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(exibeMobile)) {
            defineValorClausulaNomeada("exibeMobile", exibeMobile, query);
        }
        defineValorClausulaNomeada("tarCodigo", tarCodigo, query);
        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }
        if (!TextHelper.isNull(bpuCodigo)) {
            defineValorClausulaNomeada("bpuCodigo", bpuCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.BPU_CODIGO,
                Columns.BPU_ARQ_CODIGO,
                Columns.ARQ_CONTEUDO,
                Columns.TAR_CODIGO,
                Columns.TAR_DESCRICAO,
                Columns.BPU_NSE_CODIGO,
                Columns.NSE_DESCRICAO,
                Columns.BPU_DESCRICAO,
                Columns.BPU_URL_SAIDA,
                Columns.BPU_ORDEM,
                Columns.BPU_DATA
            };
    }
}