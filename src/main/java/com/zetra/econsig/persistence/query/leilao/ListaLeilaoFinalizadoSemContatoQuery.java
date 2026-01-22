package com.zetra.econsig.persistence.query.leilao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: ListaLeilaoFinalizadoSemContatoQuery</p>
 * <p>Description: Lista leilões finalizados que não possuem informações de contato, para que o servidor possa informar.</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaLeilaoFinalizadoSemContatoQuery extends HQuery {

    public boolean count = false;

    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String tisCodigo = TipoSolicitacaoEnum.SOLICITACAO_PROPOSTA_LEILAO_VIA_SIMULACAO.getCodigo();
        String ssoCodigo = StatusSolicitacaoEnum.FINALIZADA.getCodigo();
        List<String> tdaCodigos = new ArrayList<String>();
        tdaCodigos.add(CodedValues.TDA_CONFIRMACAO_DADOS_TEL_LEILAO);
        tdaCodigos.add(CodedValues.TDA_CONFIRMACAO_DADOS_DDD_TEL_LEILAO);
        tdaCodigos.add(CodedValues.TDA_CONFIRMACAO_DADOS_EMAIL_LEILAO);
        tdaCodigos.add(CodedValues.TDA_RECUSA_CONFIRMACAO_DADOS_LEILAO);

        StringBuilder corpoBuilder = new StringBuilder();

        if (count) {
            corpoBuilder.append("select count(distinct ade.adeCodigo)");

        } else {
            corpoBuilder.append("select");
            corpoBuilder.append(" soa.soaData,");
            corpoBuilder.append(" soa.soaDataValidade,");
            corpoBuilder.append(" ade.adeCodigo,");
            corpoBuilder.append(" ade.adeNumero,");
            corpoBuilder.append(" ade.adeTipoVlr,");
            corpoBuilder.append(" ade.adeVlrLiquido,");
            corpoBuilder.append(" ade.adeVlr,");
            corpoBuilder.append(" ade.adePrazo,");

            corpoBuilder.append(" rse.rseMatricula,");
            corpoBuilder.append(" rse.rseCodigo,");
            corpoBuilder.append(" ser.serNome,");
            corpoBuilder.append(" rse.rsePontuacao,");
            corpoBuilder.append(" ser.serCpf,");
            corpoBuilder.append(" csa.csaIdentificador,");
            corpoBuilder.append(" csa.csaNome");
        }

        corpoBuilder.append(" from SolicitacaoAutorizacao soa");
        corpoBuilder.append(" inner join soa.autDesconto ade");
        corpoBuilder.append(" inner join ade.registroServidor rse");
        corpoBuilder.append(" inner join rse.servidor ser");
        corpoBuilder.append(" inner join ade.verbaConvenio vco");
        corpoBuilder.append(" inner join vco.convenio cnv");
        corpoBuilder.append(" inner join cnv.consignataria csa");

        corpoBuilder.append(" where soa.tipoSolicitacao.tisCodigo ").append(criaClausulaNomeada("tisCodigo", tisCodigo));
        corpoBuilder.append(" and soa.statusSolicitacao.ssoCodigo ").append(criaClausulaNomeada("ssoCodigo", ssoCodigo));

        // Adiciona cláusula de matricula e cpf
        if (responsavel.isSer()) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("rse.rseCodigo", "rseCodigo", responsavel.getRseCodigo()));
        } else {
            throw new HQueryException("mensagem.usoIncorretoSistema", (AcessoSistema) null);
        }

        corpoBuilder.append(" and not exists (select 1 from DadosAutorizacaoDesconto dad ");
        corpoBuilder.append(" where dad.autDesconto.adeCodigo = ade.adeCodigo");
        corpoBuilder.append(" and dad.tipoDadoAdicional.tdaCodigo ").append(criaClausulaNomeada("tdaCodigos", tdaCodigos));
        corpoBuilder.append(")");

        if (!count) {
            corpoBuilder.append(" group by soa.soaData, soa.soaDataValidade, ade.adeCodigo, ade.adeNumero, ade.adeTipoVlr, ade.adeVlrLiquido, ade.adeVlr, ade.adePrazo,");
            corpoBuilder.append("rse.rseMatricula, ser.serNome, rse.rsePontuacao, ser.serCpf");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("tisCodigo", tisCodigo, query);
        defineValorClausulaNomeada("ssoCodigo", ssoCodigo, query);
        // Só chegará até aqui ser for servidor.
        defineValorClausulaNomeada("rseCodigo", responsavel.getRseCodigo(), query);
        defineValorClausulaNomeada("tdaCodigos", tdaCodigos, query);
        return query;
    }

    @Override
    protected String[] getFields() {
        String[] fields = new String[] {
                Columns.SOA_DATA,
                Columns.SOA_DATA_VALIDADE,
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_TIPO_VLR,
                Columns.ADE_VLR_LIQUIDO,
                Columns.ADE_VLR,
                Columns.ADE_PRAZO,
                Columns.RSE_MATRICULA,
                Columns.RSE_CODIGO,
                Columns.SER_NOME,
                Columns.RSE_PONTUACAO,
                Columns.SER_CPF,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME
        };

        return fields;
    }
}
