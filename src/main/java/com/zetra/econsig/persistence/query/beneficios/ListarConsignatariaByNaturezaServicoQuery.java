package com.zetra.econsig.persistence.query.beneficios;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusConsignatariaEnum;
import com.zetra.econsig.values.TipoFiltroPesquisaFluxoEnum;

/**
 * <p>Title: ListarConsignatariaByNaturezaServicoQuery</p>
 * <p>Description: Query busca consignatarias por natureza de servico, como também os dados de provedor de servico.</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarConsignatariaByNaturezaServicoQuery extends HQuery {

    public String nseCodigo;
    public TipoFiltroPesquisaFluxoEnum tipoFiltro;
    public String filtro;
    public String orgCodigo; //DESENV-13623: Se orgCodigo definido, retorna apenas os provedores que tenham benefícios ligados a convênios ativos para o órgão via a natureza de serviço

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder sql = new StringBuilder();
        StringBuilder where = new StringBuilder();

        sql.append(" SELECT distinct (csa.csaCodigo), ");
        sql.append(" csa.csaNome, ");
        sql.append(" pro.proCodigo, ");
        sql.append(" pro.proTextoCardBeneficio, ");
        sql.append(" pro.proImagemBeneficio, ");
        sql.append(" pro.proAgrupa, ");
        sql.append(" pro.correspondente.corCodigo ");
        sql.append(" FROM ProvedorBeneficio pro ");
        sql.append(" INNER JOIN pro.consignataria csa ");
        sql.append(" INNER JOIN csa.beneficioSet ben ");

        where.append(" WHERE pro.naturezaServico.nseCodigo = :nseCodigo ");
        where.append(" and ben.naturezaServico.nseCodigo = :nseCodigo ");
        where.append(" and csa.csaAtivo = " + StatusConsignatariaEnum.ATIVO.getCodigo());

        if (!TextHelper.isNull(orgCodigo)) {
            where.append(" and exists (select 1 from Convenio cnv INNER JOIN cnv.servico svc WHERE cnv.consignataria.csaCodigo = csa.csaCodigo").append(" and cnv.orgao.orgCodigo ");
            where.append(criaClausulaNomeada("orgCodigo", orgCodigo)).append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'").append(" and svc.naturezaServico.nseCodigo ");
            where.append(criaClausulaNomeada("nseCodigo", nseCodigo)).append(")");
        }

        String[] split = {};

        if (!TextHelper.isNull(filtro)) {
            split = filtro.trim().split("\\s+");
        }

        if (ArrayUtils.isNotEmpty(split)) {

            if (TipoFiltroPesquisaFluxoEnum.FILTRO_TUDO.equals(tipoFiltro)) {

                sql.append(" LEFT OUTER JOIN csa.enderecoConsignatariaSet enc ");
                sql.append(" LEFT OUTER JOIN ben.modalidadeBeneficio mbe ");

                where.append(" and ( ");

                for (int i = 0; i < split.length; i++) {

                    if (i != 0) {
                        where.append(" and ");
                    }

                    where.append(" ( ");

                    where.append(" ben.benDescricao like :filtro").append(i);
                    where.append(" or csa.csaNome like :filtro").append(i);
                    where.append(" or enc.encMunicipio like :filtro").append(i);
                    where.append(" or enc.encBairro like :filtro").append(i);
                    where.append(" or mbe.mbeDescricao like :filtro").append(i);

                    where.append(" ) ");
                }

                where.append(" or exists ( select 1 from PalavraChaveBeneficio pcb1 ");
                where.append(" INNER JOIN pcb1.palavraChave pch1 where pcb1.beneficio.benCodigo = ben.benCodigo and (");

                for (int i = 0; i < split.length; i++) {

                    if (i != 0) {
                        where.append(" or ");
                    }

                    where.append(" pch1.pchPalavra like :filtroPalavraChave").append(i);
                }

                where.append(" ) ");
                where.append(" ) ");
                where.append(" ) ");

            } else if (TipoFiltroPesquisaFluxoEnum.FILTRO_CIDADE.equals(tipoFiltro)) {
                sql.append(" LEFT OUTER JOIN csa.enderecoConsignatariaSet enc ");
                where.append(" and ( ");

                for (int i = 0; i < split.length; i++) {

                    if (i != 0) {
                        where.append(" and ");
                    }

                    where.append(" enc.encMunicipio like :filtro").append(i);

                }

                where.append(" ) ");
            } else if (TipoFiltroPesquisaFluxoEnum.FILTRO_BAIRRO.equals(tipoFiltro)) {
                sql.append(" LEFT OUTER JOIN csa.enderecoConsignatariaSet enc ");

                where.append(" and ( ");

                for (int i = 0; i < split.length; i++) {

                    if (i != 0) {
                        where.append(" and ");
                    }

                    where.append(" enc.encBairro like :filtro").append(i);

                }

                where.append(" ) ");

            } else if (TipoFiltroPesquisaFluxoEnum.FILTRO_BENEFICIO.equals(tipoFiltro)) {

                sql.append(" LEFT OUTER JOIN ben.modalidadeBeneficio mbe ");

                where.append(" and ( ");

                for (int i = 0; i < split.length; i++) {

                    if (i != 0) {
                        where.append(" and ");
                    }

                    where.append(" ( ");

                    where.append(" ben.benDescricao like :filtro").append(i);
                    where.append(" or csa.csaNome like :filtro").append(i);
                    where.append(" or mbe.mbeDescricao like :filtro").append(i);

                    where.append(" ) ");

                }

                where.append(" or exists ( select 1 from PalavraChaveBeneficio pcb1 ");
                where.append(" INNER JOIN pcb1.palavraChave pch1 where pcb1.beneficio.benCodigo = ben.benCodigo and (");

                for (int i = 0; i < split.length; i++) {

                    if (i != 0) {
                        where.append(" or ");
                    }

                    where.append(" pch1.pchPalavra like :filtroPalavraChave").append(i);
                }

                where.append(" ) ");
                where.append(" ) ");
                where.append(" ) ");

            }

        }

        sql.append(where);

        sql.append(" ORDER BY csa.csaIdentificador ASC, pro.proAgrupa DESC ");

        Query<Object[]> query = instanciarQuery(session, sql.toString());

        defineValorClausulaNomeada("nseCodigo", nseCodigo, query);

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        if (ArrayUtils.isNotEmpty(split)) {

            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                defineValorClausulaNomeada("filtro" + i, CodedValues.LIKE_MULTIPLO + s + CodedValues.LIKE_MULTIPLO, query);
            }

            if (TipoFiltroPesquisaFluxoEnum.FILTRO_TUDO.equals(tipoFiltro) ||
                    TipoFiltroPesquisaFluxoEnum.FILTRO_BENEFICIO.equals(tipoFiltro)) {
                for (int i = 0; i < split.length; i++) {
                    String s = split[i];
                    defineValorClausulaNomeada("filtroPalavraChave" + i,  s + CodedValues.LIKE_MULTIPLO, query);
                }
            }

        }

        return query;

    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_NOME,
                Columns.PRO_CODIGO,
                Columns.PRO_TEXTO_CARD_BENEFICIO,
                Columns.PRO_IMAGEM_BENEFICIO,
                Columns.PRO_AGRUPA,
                Columns.PRO_COR_CODIGO
        };
    }

}
