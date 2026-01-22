package com.zetra.econsig.persistence.query.beneficios;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListBeneficioCsaOperadoraQuery</p>
 * <p>Description: Listagem de beneficios</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarBeneficioCsaOperadoraQuery extends HQuery{

    public Object ncaCodigo = null;
    public String filtro = null;
    public String filtro_tipo = null;

    //Executar count ao invez da query
    public Boolean count = false;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();

        if(!count) {
            corpo.append("SELECT ben.benCodigo as ben_codigo,"
                + "csa.csaNome as csa_nome, "
                + "nse.nseDescricao as nse_descricao, "
                + "ben.benDescricao as ben_descricao, "
                + "ben.benCodigoPlano as ben_codigo_plano, "
                + "ben.benCodigoRegistro as ben_codigo_registro, "
                + "ben.benCodigoContrato as ben_codigo_ontrato, "
                + "ben.benAtivo as ben_ativo ");
        }else {
            corpo.append("SELECT count(*) as total ");
        }
        corpo.append("FROM Beneficio ben ");
        corpo.append("INNER JOIN ben.consignataria csa ");
        corpo.append("INNER JOIN ben.naturezaServico nse ");
        corpo.append("INNER JOIN csa.naturezaConsignataria nca ");
        corpo.append("WHERE nca.ncaCodigo = :ncaCodigo ");

        switch (filtro_tipo) {
            case "2":
                corpo.append("and csa.csaNome like :filtro ");
                break;
            case "3":
                corpo.append("and nse.nseDescricao like :filtro ");
                break;
            case "4":
                corpo.append("and ben.benDescricao like :filtro ");
                break;
            case "5":
                corpo.append("and ben.benCodigoPlano like :filtro ");
                break;
            case "6":
                corpo.append("and ben.benCodigoRegistro like :filtro ");
                break;
            case "7":
                corpo.append("and ben.benCodigoContrato like :filtro ");
                break;
        }

        corpo.append("ORDER BY csa.csaNome, "
                + "nse.nseDescricao, "
                + "ben.benDescricao, "
                + "ben.benCodigoPlano, "
                + "ben.benCodigoRegistro, "
                + "ben.benCodigoContrato ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(ncaCodigo)) {
            defineValorClausulaNomeada("ncaCodigo", ncaCodigo, query);
        }

        if(!filtro_tipo.equals("-1")) {
            defineValorClausulaNomeada("filtro", "%" + filtro + "%", query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.BEN_CODIGO,
                Columns.CSA_NOME,
                Columns.NSE_DESCRICAO,
                Columns.BEN_DESCRICAO,
                Columns.BEN_CODIGO_PLANO,
                Columns.BEN_CODIGO_REGISTRO,
                Columns.BEN_CODIGO_CONTRATO,
                Columns.BEN_ATIVO
        };
    }
}
