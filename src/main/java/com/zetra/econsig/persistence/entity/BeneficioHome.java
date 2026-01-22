package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: BeneficioHome </p>
 * <p>Description: Classe Home da entidade Beneficio.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class BeneficioHome extends AbstractEntityHome {

    public static Beneficio findByPrimaryKey(String benCodigo) throws FindException {
        Beneficio beneficio = new Beneficio();
        beneficio.setBenCodigo(benCodigo);

        return find(beneficio, benCodigo);
    }

    public static Beneficio findByConsignatariaECodigoRegistro(String csaCodigo, String codigoRegistro) throws FindException {
        String query = "FROM Beneficio ben WHERE ben.consignataria.csaCodigo = :consignataria and ben.benCodigoRegistro = :codigoRegistro";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("consignataria", csaCodigo);
        parameters.put("codigoRegistro", codigoRegistro);

        List<Beneficio> beneficios = findByQuery(query, parameters);

        if (beneficios == null || beneficios.size() == 0) {
            return null;
        } else if (beneficios.size() == 1) {
            return beneficios.get(0);
        } else {
            throw new FindException("mensagem.erro.mais.de.um.resultado.encontrado", (AcessoSistema) null);
        }
    }

    public static Beneficio create(Consignataria consignataria, NaturezaServico naturezaServico, String descricao, String codigoPlano, String codigoRegistro, String codigoContrato) throws CreateException {
        Beneficio beneficio = new Beneficio();

        try {
            beneficio.setBenCodigo(DBHelper.getNextId());
            beneficio.setConsignataria(consignataria);
            beneficio.setNaturezaServico(naturezaServico);
            beneficio.setBenDescricao(descricao);
            beneficio.setBenCodigoPlano(codigoPlano);
            beneficio.setBenCodigoRegistro(codigoRegistro);
            beneficio.setBenCodigoContrato(codigoContrato);
            beneficio.setBenAtivo(CodedValues.STS_ATIVO);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }

        create(beneficio);

        return beneficio;
    }

    public static Beneficio findByBenCodigo(String benCodigo) throws FindException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT distinct ben ");
        query.append("FROM Beneficio ben ");
        query.append("JOIN FETCH ben.naturezaServico nse ");
        query.append("JOIN FETCH ben.consignataria csa ");
        query.append("WHERE ben.benCodigo = :benCodigo ");

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("benCodigo", benCodigo);

        List<Object> beneficio = findByQuery(query.toString(), parameters);

        if (beneficio == null) {
            return null;
        } else {
            return (Beneficio) beneficio.get(0);
        }
    }

    public static Beneficio findBeneficioFetchBeneficioServicoByCodigo(String benCodigo) throws FindException {

        String query = "SELECT DISTINCT ben FROM Beneficio ben left join fetch ben.beneficioServicoSet rel left join fetch rel.servico left join fetch rel.tipoBeneficiario where ben.benCodigo = :benCodigo ORDER BY rel.bseOrdem";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("benCodigo", benCodigo);

        List<Object> beneficio = findByQuery(query.toString(), parameters);

        if (beneficio == null) {
            return null;
        } else {
            return (Beneficio) beneficio.get(0);
        }
    }

    public static Beneficio findByConsignatariaServicoTipoBeneficiario(String csaCodigo, String svcCodigo, String tibCodigo) throws FindException {

        String query = "SELECT DISTINCT ben FROM Beneficio ben INNER JOIN ben.beneficioServicoSet rel WHERE rel.servico.svcCodigo = :svcCodigo AND ben.consignataria.csaCodigo = :csaCodigo AND rel.tipoBeneficiario.tibCodigo= :tibCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("svcCodigo", svcCodigo);
        parameters.put("csaCodigo", csaCodigo);
        parameters.put("tibCodigo", tibCodigo);

        List<Object> beneficio = findByQuery(query.toString(), parameters);

        if (beneficio == null || beneficio.isEmpty()) {
            return null;
        } else {
            return (Beneficio) beneficio.get(0);
        }
    }
}
