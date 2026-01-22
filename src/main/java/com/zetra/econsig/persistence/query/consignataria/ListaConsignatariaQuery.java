package com.zetra.econsig.persistence.query.consignataria;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignatariaQuery</p>
 * <p>Description: Listagem de Consignatárias</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignatariaQuery extends HQuery {

    public String csaIdentificador;

    public String csaNome;

    public String csaNomeAbrev;

    public String csaCodigo;

    public Object csaAtivo;

    public List<String> csaCodigos;

    public Date dataExpiracao;

    public String csaIdentificadorInterno;

    public String csaProjetoInadimplencia;

    public String ncaExibeSer;

    public String ncaCodigo;

    public String cnvCodVerba;

    public boolean count = false;

    public boolean csaConsultaMargemSemSenha;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo = "select consignataria.csaCodigo," +
                    "consignataria.csaIdentificador," +
                    "consignataria.csaNome," +
                    "consignataria.csaNomeAbrev," +
                    "consignataria.csaAtivo, " +
                    "consignataria.csaCnpj," +
                    "consignataria.csaTel," +
                    "consignataria.csaEmail, " +
                    "consignataria.csaDataDesbloqAutomatico, " +
                    "consignataria.csaRespTelefone, " +
                    "consignataria.csaContato, " +
                    "consignataria.csaDataExpiracao, " +
                    "consignataria.naturezaConsignataria.ncaCodigo, " +
                    "consignataria.naturezaConsignataria.ncaDescricao, " +
                    "tmb.tmbDescricao ";
        } else {
            corpo = "select count(*) as total ";
        }

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Consignataria as consignataria");

        if (!TextHelper.isNull(ncaExibeSer)) {
            corpoBuilder.append(" inner join consignataria.naturezaConsignataria as ncsa");
        }

        if (!count) {
            corpoBuilder.append(" left outer join consignataria.tipoMotivoBloqueio as tmb");
        }

        corpoBuilder.append(" WHERE 1=1 ");

        if (!TextHelper.isNull(csaIdentificador)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("consignataria.csaIdentificador", "csaIdentificador", csaIdentificador));
        }

        if (!TextHelper.isNull(csaIdentificadorInterno)) {
            corpoBuilder.append(" and consignataria.csaIdentificadorInterno ").append(criaClausulaNomeada("csaIdentificadorInterno", csaIdentificadorInterno));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        } else if ((csaCodigos != null) && (csaCodigos.size() > 0)) {
            corpoBuilder.append(" and consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigos));
        }

        if (csaAtivo != null) {
            corpoBuilder.append(" and consignataria.csaAtivo ").append(criaClausulaNomeada("csaAtivo", csaAtivo));
        }

        if (!TextHelper.isNull(ncaExibeSer)) {
            corpoBuilder.append(" and ncsa.ncaExibeSer = :ncaExibeSer");
        }

        if (dataExpiracao != null) {
            corpoBuilder.append(" and (consignataria.csaDataExpiracao <= :csaDataExpiracao or consignataria.csaDataExpiracaoCadastral <= :csaDataExpiracao)");
            corpoBuilder.append(" and consignataria.csaCodigo not in (select pca.consignataria.csaCodigo from ParamConsignataria pca where pca.tipoParamConsignataria.tpaCodigo = '" + CodedValues.TPA_NAO_BLOQUEIA_POR_DATA_EXPIRACAO + "' and pca.pcsVlr = '" + CodedValues.TPA_SIM + "') ");
        }

        if (!TextHelper.isNull(csaNome) && !TextHelper.isNull(csaNomeAbrev)) {
            corpoBuilder.append(" and (").append(criaClausulaNomeada("consignataria.csaNome", "csaNome", csaNome));
            corpoBuilder.append(" or ").append(criaClausulaNomeada("consignataria.csaNomeAbrev", "csaNomeAbrev", csaNomeAbrev)).append(")");
        } else if (!TextHelper.isNull(csaNome)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("consignataria.csaNome", "csaNome", csaNome));
        } else if (!TextHelper.isNull(csaNomeAbrev)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("consignataria.csaNomeAbrev", "csaNomeAbrev", csaNomeAbrev));
        }

        if (!TextHelper.isNull(csaProjetoInadimplencia)) {
            corpoBuilder.append(" and consignataria.csaProjetoInadimplencia ").append(criaClausulaNomeada("csaProjetoInadimplencia", csaProjetoInadimplencia));
        }

        if (!TextHelper.isNull(ncaCodigo)) {
            corpoBuilder.append(" and consignataria.naturezaConsignataria.ncaCodigo ").append(criaClausulaNomeada("ncaCodigo", ncaCodigo));
        }

        if (!TextHelper.isNull(cnvCodVerba)) {
            corpoBuilder.append(" and EXISTS (select cnv.cnvCodVerba from Convenio as cnv WHERE cnv.consignataria.csaCodigo = consignataria.csaCodigo AND cnv.cnvCodVerba ").append(criaClausulaNomeada("cnvCodVerba", cnvCodVerba)).append(")");
        }

        if (csaConsultaMargemSemSenha) {
            corpoBuilder.append(" and consignataria.csaConsultaMargemSemSenha = :csaConsultaMargemSemSenha");
        }

        if (!count) {
            corpoBuilder.append(" order by consignataria.csaNome");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaIdentificador)) {
            defineValorClausulaNomeada("csaIdentificador", csaIdentificador, query);
        }

        if (!TextHelper.isNull(csaIdentificadorInterno)) {
            defineValorClausulaNomeada("csaIdentificadorInterno", csaIdentificadorInterno, query);
        }

        if (!TextHelper.isNull(csaNome)) {
            defineValorClausulaNomeada("csaNome", csaNome, query);
        }

        if (!TextHelper.isNull(csaNomeAbrev)) {
            defineValorClausulaNomeada("csaNomeAbrev", csaNomeAbrev, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        } else if ((csaCodigos != null) && (csaCodigos.size() > 0)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigos, query);
        }

        if (csaAtivo != null) {
            defineValorClausulaNomeada("csaAtivo", csaAtivo, query);
        }

        if (!TextHelper.isNull(ncaExibeSer)) {
            defineValorClausulaNomeada("ncaExibeSer", ncaExibeSer, query);
        }

        if (dataExpiracao != null) {
            defineValorClausulaNomeada("csaDataExpiracao", dataExpiracao, query);
        }

        if (!TextHelper.isNull(csaProjetoInadimplencia)) {
            defineValorClausulaNomeada("csaProjetoInadimplencia", csaProjetoInadimplencia, query);
        }

        if (!TextHelper.isNull(ncaCodigo)) {
            defineValorClausulaNomeada("ncaCodigo", ncaCodigo, query);
        }

        if (!TextHelper.isNull(cnvCodVerba)) {
            defineValorClausulaNomeada("cnvCodVerba", cnvCodVerba, query);
        }

        if (csaConsultaMargemSemSenha) {
            defineValorClausulaNomeada("csaConsultaMargemSemSenha", CodedValues.TPC_SIM, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                              Columns.CSA_CODIGO,
                              Columns.CSA_IDENTIFICADOR,
                              Columns.CSA_NOME,
                              Columns.CSA_NOME_ABREV,
                              Columns.CSA_ATIVO,
                              Columns.CSA_CNPJ,
                              Columns.CSA_TEL,
                              Columns.CSA_EMAIL,
                              Columns.CSA_DATA_DESBLOQ_AUTOMATICO,
                              Columns.CSA_RESP_TELEFONE,
                              Columns.CSA_CONTATO,
                              Columns.CSA_DATA_EXPIRACAO,
                              Columns.NCA_CODIGO,
                              Columns.NCA_DESCRICAO,
                              Columns.TMB_DESCRICAO
        };
    }
}
