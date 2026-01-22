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

public class RelatorioOcorrenciaServidorQuery extends ReportHQuery {

    public String dataIni;
    public String dataFim;
    public List<String>  orgCodigos;
    public String serCpf;
    public String estCodigo;
    public String rseMatricula;
    public String opLogin;
    public List<String> tmoCodigos;
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
                        "           when usu.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(usu.usuTipoBloq, '*'), ''), usu.usuLogin)" +
                        "           else usu.usuLogin" +
                        " end as usu_login, " +
                        "org.orgNome as ent_nome," +
                        " case " +
                        "           when op.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(op.usuTipoBloq, '*'), ''), op.usuLogin)" +
                        "           else op.usuLogin" +
                        " end as oplogin, " +
                        "srs.srsDescricao as srs_descricao,"+
                        " concatenar(text_to_string(ous.ousObs), case when tmo.tmoDescricao is NULL then '' else concatenar('  MOTIVO: ',tmo.tmoDescricao) end) as toc_descricao, " +
                        "to_locale_datetime(ous.ousData) as ous_data," +
                        "ous.ousIpAcesso as ous_ip_acesso," +
                        "usu.usuNome as usu_nome," +
                        " usuarioCsa.csaCodigo as csa_codigo, " +
                        " usuarioCse.cseCodigo as cse_codigo, " +
                        " usuarioCor.corCodigo as cor_codigo, " +
                        " usuarioOrg.orgCodigo as org_codigo, " +
                        " usuarioSer.serCodigo as ser_codigo, " +
                        " usuarioSup.cseCodigo as sup_cse_codigo, " +
                        "rse.rseTipo as rse_tipo";

        StringBuilder corpoBuilder = new StringBuilder(fields);

        corpoBuilder.append(" from OcorrenciaUsuario ous ");
        corpoBuilder.append(" inner join ous.tipoOcorrencia toc ");
        corpoBuilder.append(" inner join ous.usuarioByUsuCodigo usu ");
        corpoBuilder.append(" inner join ous.usuarioByOusUsuCodigo op ");
        corpoBuilder.append(" inner join usu.usuarioSerSet usrSer ");
        corpoBuilder.append(" inner join usrSer.servidor ser ");
        corpoBuilder.append(" inner join ser.registroServidorSet rse ");
        corpoBuilder.append(" inner join rse.orgao org ");
        corpoBuilder.append(" inner join rse.statusRegistroServidor srs ");

        corpoBuilder.append(" LEFT JOIN usu.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCorSet usuarioCor ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSerSet usuarioSer ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSupSet usuarioSup ");

        corpoBuilder.append(" left outer join ous.tipoMotivoOperacao tmo ");

        corpoBuilder.append(" where ");
        corpoBuilder.append(" ous.ousData between :dataIni and :dataFim");

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
            corpoBuilder.append(" and op.usuLogin ").append(criaClausulaNomeada("usuLogins", usuLogins));
        }

        if (tmoCodigos != null && !tmoCodigos.isEmpty()) {
            corpoBuilder.append(" and tmo.tmoCodigo ").append(criaClausulaNomeada("tmoCodigo", tmoCodigos));
        }

        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            corpoBuilder.append(" and toc.tocCodigo ").append(criaClausulaNomeada("tocCodigo", tocCodigos));
        }

        corpoBuilder.append(" order by ous.ousData");

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

        if (tmoCodigos != null && !tmoCodigos.isEmpty()) {
            defineValorClausulaNomeada("tmoCodigo", tmoCodigos, query);
        }

        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            defineValorClausulaNomeada("tocCodigo", tocCodigos, query);
        }

        if (usuLogins != null && !usuLogins.isEmpty()) {
            defineValorClausulaNomeada("usuLogins", usuLogins, query);
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
        tmoCodigos = (List<String>) criterio.getAttribute(Columns.TMO_CODIGO);
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
                Columns.OUS_DATA,
                Columns.OUS_IP_ACESSO,
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
