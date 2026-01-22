package com.zetra.econsig.persistence.query.relatorio;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

public class RelatorioOcorrenciaRegistroServidorQuery extends ReportHQuery {

    public String dataIni;
    public String dataFim;
    public List<String> orgCodigos;
    public String serCpf;
    public String estCodigo;
    public String rseMatricula;
    public String opLogin;
    public List<String> tocCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        List<String> usuLogins = null;
        if (!TextHelper.isNull(opLogin)) {
            String[] usuarios = TextHelper.split(opLogin.replaceAll(" ", ""), ",");
            if (usuarios != null && usuarios.length > 0) {
                usuLogins = Arrays.asList(usuarios);
            }
        }

        String fields = "select " +
                        " case " +
                        "           when u.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(u.usuTipoBloq, '*'), ''), u.usuLogin)" +
                        "           else u.usuLogin" +
                        " end as usu_login, " +
                        "org.orgNome as ent_nome," +
                        " case " +
                        "           when usu.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(usu.usuTipoBloq, '*'), ''), usu.usuLogin)" +
                        "           else usu.usuLogin" +
                        " end as oplogin, " +
                        " srs.srsDescricao as srs_descricao,"+
                        " concatenar(ors.orsObs, case when toc.tocDescricao is NULL then '' else concatenar('  MOTIVO: ',toc.tocDescricao) end) as toc_descricao, " +
                        " to_locale_datetime(ors.orsData) as ors_data," +
                        " ors.orsIpAcesso as ors_ip_acesso," +
                        " u.usuNome as usu_nome," +
                        " usuarioCsa.csaCodigo as csa_codigo, " +
                        " usuarioCse.cseCodigo as cse_codigo, " +
                        " usuarioCor.corCodigo as cor_codigo, " +
                        " usuarioOrg.orgCodigo as org_codigo, " +
                        " usuarioSer.serCodigo as ser_codigo, " +
                        " usuarioSup.cseCodigo as sup_cse_codigo, " +
                        "rse.rseTipo as rse_tipo";

        StringBuilder corpoBuilder = new StringBuilder(fields);


        corpoBuilder.append(" from OcorrenciaRegistroSer ors ");
        corpoBuilder.append(" inner join ors.tipoOcorrencia toc ");

        corpoBuilder.append(" inner join ors.usuario usu ");

        corpoBuilder.append(" inner join ors.registroServidor rse ");
        corpoBuilder.append(" inner join rse.servidor ser ");
        corpoBuilder.append(" inner join ser.usuarioSerSet us ");
        corpoBuilder.append(" inner join us.usuario u ");
        corpoBuilder.append(" inner join rse.orgao org ");
        corpoBuilder.append(" inner join rse.statusRegistroServidor srs ");

        corpoBuilder.append(" LEFT JOIN usu.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCorSet usuarioCor ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSerSet usuarioSer ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSupSet usuarioSup ");

        corpoBuilder.append(" where ");
        corpoBuilder.append(" ors.orsData between :dataIni and :dataFim");

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (!TextHelper.isNull(rseMatricula)) {
            corpoBuilder.append(" and rse.rseMatricula ").append(criaClausulaNomeada("rseMatricula", rseMatricula));
        }

        if (!TextHelper.isNull(serCpf)) {
            corpoBuilder.append(" and ser.serCpf ").append(criaClausulaNomeada("serCpf", serCpf));
        }

        if (!TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" and org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        }

        if (!TextHelper.isNull(usuLogins)) {
            corpoBuilder.append(" and usu.usuLogin ").append(criaClausulaNomeada("usuLogins", usuLogins));
        }

        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            corpoBuilder.append(" and toc.tocCodigo ").append(criaClausulaNomeada("tocCodigo", tocCodigos));
        }

        corpoBuilder.append(" order by ors.orsData");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(dataIni)) {
            defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), query);
        }

        if (!TextHelper.isNull(dataFim)) {
            defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
        }

        if (!TextHelper.isNull(serCpf)) {
            defineValorClausulaNomeada("serCpf", serCpf, query);
        }

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }

        if (usuLogins != null && !usuLogins.isEmpty()) {
            defineValorClausulaNomeada("usuLogins", usuLogins, query);
        }

        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            defineValorClausulaNomeada("tocCodigo", tocCodigos, query);
        }

        return query;
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        dataIni = (String) criterio.getAttribute("DATA_INI");
        dataFim = (String) criterio.getAttribute("DATA_FIM");
        orgCodigos = (List<String>) criterio.getAttribute("ORG_CODIGO");
        rseMatricula = (String) criterio.getAttribute("MATRICULA");
        serCpf = (String) criterio.getAttribute("CPF");
        opLogin = (String) criterio.getAttribute("OP_LOGIN");
        estCodigo = (String) criterio.getAttribute("EST_CODIGO");
        tocCodigos = (List<String>) criterio.getAttribute(Columns.TOC_CODIGO);
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_LOGIN,
                Columns.SER_NOME,
                "oplogin",
                Columns.SRS_DESCRICAO,
                Columns.TOC_DESCRICAO,
                Columns.ORS_DATA,
                Columns.ORS_IP_ACESSO,
                Columns.UCA_CSA_CODIGO,
                Columns.UCE_CSE_CODIGO,
                Columns.UCO_COR_CODIGO,
                Columns.UOR_ORG_CODIGO,
                Columns.USE_SER_CODIGO,
                Columns.USP_CSE_CODIGO,
                Columns.RSE_TIPO
        };
    }

}
