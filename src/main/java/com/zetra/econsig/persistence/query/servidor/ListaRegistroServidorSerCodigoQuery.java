package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaRegistroServidorUsuarioSer</p>
 * <p>Description: Lista os registros do servidor pelo cpf.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaRegistroServidorSerCodigoQuery extends HQuery {
    public String serCpf;
    public String serCodigo;
    public boolean count = false;
    public boolean recuperaRseExcluido = true;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (count) {
            corpo = "select count(distinct rse.rseCodigo) as total ";
        } else {
            corpo =
                "select distinct " +
                "rse.rseCodigo, " +
                "rse.rseMatricula, " +
                "rse.srsCodigo, " +
                "srs.srsDescricao, " +
                "ser.serCodigo, " +
                "ser.serNome, " +
                "ser.serCpf, " +
                "org.orgCodigo, " +
                "org.orgIdentificador, " +
                "org.orgNome, " +
                "est.estCodigo, " +
                "est.estIdentificador, " +
                "est.estNome, " +
                "usu.usuCodigo ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from RegistroServidor rse ");
        corpoBuilder.append(" inner join rse.orgao org ");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        corpoBuilder.append(" inner join rse.servidor ser ");
        corpoBuilder.append(" inner join ser.usuarioSerSet usuSer ");
        corpoBuilder.append(" inner join usuSer.usuario usu ");
        corpoBuilder.append(" inner join rse.statusRegistroServidor srs");
        corpoBuilder.append(" where 1=1 ");

        if (ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append(" and usu.usuLogin = concat(concat(concat(concat(est.estIdentificador, '-'), org.orgIdentificador), '-'), rse.rseMatricula) ");
        } else  {
            corpoBuilder.append(" and usu.usuLogin = concat(concat(est.estIdentificador, '-'), rse.rseMatricula) ");
        }


        if (!TextHelper.isNull(serCodigo)) {
            corpoBuilder.append(" AND rse.servidor.serCodigo ").append(criaClausulaNomeada("serCodigo", serCodigo));
        }

        // gera cláusula de CPF
        if (!TextHelper.isNull(serCpf)) {
            corpoBuilder.append(" AND ser.serCpf ").append(criaClausulaNomeada("serCpf", serCpf));
        }

        boolean ignoraServExcluidos = ParamSist.paramEquals(CodedValues.TPC_IGNORA_SERVIDORES_EXCLUIDOS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        if (ignoraServExcluidos || !recuperaRseExcluido) {
            corpoBuilder.append(" AND srs.srsCodigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')");
        }

        if (!count) {
            // Ordenação pelo status, para que os ativos venham na frete dos excluídos
            corpoBuilder.append(" order by rse.srsCodigo, ser.serNome");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());


        if (!TextHelper.isNull(serCodigo)) {
            defineValorClausulaNomeada("serCodigo", serCodigo, query);
        }
        if (!TextHelper.isNull(serCpf)) {
            defineValorClausulaNomeada("serCpf", serCpf, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.SRS_CODIGO,
                Columns.SRS_DESCRICAO,
                Columns.SER_CODIGO,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.ORG_CODIGO,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_NOME,
                Columns.EST_CODIGO,
                Columns.EST_IDENTIFICADOR,
                Columns.EST_NOME,
                Columns.USU_CODIGO
        };
    }
}