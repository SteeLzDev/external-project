package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: CalculoBeneficioHome </p>
 * <p>Description: Classe Home da entidade CalculoBeneficio.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class CalculoBeneficioHome extends AbstractEntityHome {

    public static CalculoBeneficio findByPrimaryKey(String clbCodigo) throws FindException {
        CalculoBeneficio calculoBeneficio = new CalculoBeneficio();
        calculoBeneficio.setClbCodigo(clbCodigo);

        return find(calculoBeneficio, clbCodigo);
    }

    public static CalculoBeneficio findCalculoBeneficioByCodigo(String clbCodigo) throws FindException {
        String query = "SELECT clb FROM CalculoBeneficio clb "
                + "left join fetch clb.orgao org "
                + "join fetch clb.beneficio ben "
                + "left join fetch clb.tipoBeneficiario tib "
                + "left join fetch clb.grauParentesco grp "
                + "left join fetch clb.motivoDependencia mde "
                + "WHERE clb.clbCodigo = :clbCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("clbCodigo", clbCodigo);


        List<CalculoBeneficio> calculoBeneficio = findByQuery(query, parameters);
        if (calculoBeneficio == null || calculoBeneficio.size() == 0) {
            return null;
        } else if (calculoBeneficio.size() == 1) {
            return calculoBeneficio.get(0);
        } else {
            throw new FindException("mensagem.erro.mais.de.um.resultado.encontrado", (AcessoSistema) null);
        }
    }

    public static List<CalculoBeneficio> listaCalculoBeneficioObject(Integer regra, AcessoSistema responsavel) throws FindException {
        String query = "SELECT clb FROM CalculoBeneficio clb " +
                "inner join clb.beneficio ben " +
                "left join clb.tipoBeneficiario tib " +
                "inner join ben.consignataria csa " +
                "left join clb.orgao org " +
                "left join clb.grauParentesco grp " +
                "left join clb.motivoDependencia mde ";

        if (regra.equals(Integer.valueOf(1))) {
            query += "where clb.clbVigenciaIni is null and clb.clbVigenciaFim is null ";
        } else if (regra.equals(Integer.valueOf(2))){
            query += "where clb.clbVigenciaIni is not null and clb.clbVigenciaFim is null ";
        }

        List<CalculoBeneficio> calculoBeneficio = findByQuery(query, null);
        if (calculoBeneficio == null || calculoBeneficio.size() == 0) {
            return null;
        } else {
            return calculoBeneficio;
        }
    }

    public static CalculoBeneficio create(TipoBeneficiario tipoBeneficio, Orgao orgao, Beneficio beneficio, GrauParentesco grauParentesco, MotivoDependencia motivoDependencia, Date vigenciaIni, Date vigenciaFim, BigDecimal valorMensalidade, BigDecimal valorSubsidio, Short faixaEtariaIni, Short faixaEtariaFim, BigDecimal fixaSalarialIni, BigDecimal fixaSalarialFim) throws CreateException {
        CalculoBeneficio calculoBeneficio = new CalculoBeneficio();

        try {
            calculoBeneficio.setClbCodigo(DBHelper.getNextId());
            calculoBeneficio.setTipoBeneficiario(tipoBeneficio);
            calculoBeneficio.setOrgao(orgao);
            calculoBeneficio.setBeneficio(beneficio);
            calculoBeneficio.setGrauParentesco(grauParentesco);
            calculoBeneficio.setMotivoDependencia(motivoDependencia);
            calculoBeneficio.setClbVigenciaIni(vigenciaIni);
            calculoBeneficio.setClbVigenciaFim(vigenciaFim);
            calculoBeneficio.setClbValorMensalidade(valorMensalidade);
            calculoBeneficio.setClbValorSubsidio(valorSubsidio);
            calculoBeneficio.setClbFaixaEtariaIni(faixaEtariaIni);
            calculoBeneficio.setClbFaixaEtariaFim(faixaEtariaFim);
            calculoBeneficio.setClbFaixaSalarialIni(fixaSalarialIni);
            calculoBeneficio.setClbFaixaSalarialFim(fixaSalarialFim);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }

        create(calculoBeneficio);
        return calculoBeneficio;
    }

    public static List<CalculoBeneficio> listarTabelaIniciada() throws FindException, RemoveException, InterruptedException {
        String query = "FROM CalculoBeneficio clb WHERE clb.clbVigenciaIni IS NULL and clb.clbVigenciaFim IS NULL";
        List<CalculoBeneficio> lista = findByQuery(query, null);
        if (lista == null || lista.size() == 0) {
            return null;
        } else {
            return lista;
        }
    }

    public static void deletarTabelaIniciada() {
        Session session = SessionUtil.getSession();
        String queryDelete = "DELETE FROM CalculoBeneficio clb " +
                "WHERE clb.clbVigenciaIni IS NULL and clb.clbVigenciaFim IS NULL";
        MutationQuery query = session.createMutationQuery(queryDelete);
        query.executeUpdate();
        SessionUtil.closeSession(session);
    }

    public static void ativarTabela() {
        Session session = SessionUtil.getSession();
        String queryUpdate = "UPDATE CalculoBeneficio clb " +
                "SET clb.clbVigenciaFim = :dataAtual " +
                "WHERE clb.clbVigenciaIni IS NOT NULL and clb.clbVigenciaFim IS NULL";

        MutationQuery query = session.createMutationQuery(queryUpdate);
        Date dataFim = DateHelper.addSeconds(new Date(), -1);
        query.setParameter("dataAtual", dataFim);

        query.executeUpdate();

        queryUpdate = "UPDATE CalculoBeneficio clb " +
                "SET clb.clbVigenciaIni = :dataAtual " +
                "WHERE clb.clbVigenciaIni IS NULL and clb.clbVigenciaFim IS NULL";

        query = session.createMutationQuery(queryUpdate);
        query.setParameter("dataAtual", new Date());
        query.executeUpdate();
        SessionUtil.closeSession(session);
    }
}
