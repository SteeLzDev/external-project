package com.zetra.econsig.persistence.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: BeneficiarioHome</p>
 * <p>Description: Classe home da entidade Beneficiario</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class BeneficiarioHome extends AbstractEntityHome {

    public static Beneficiario findByPrimaryKey(String bfcCodigo) throws FindException {
        Beneficiario beneficiario = new Beneficiario();
        beneficiario.setBfcCodigo(bfcCodigo);

        return find(beneficiario, bfcCodigo);
    }

    public static List<Beneficiario> findByServidor(String serCodigo) throws FindException {
        String query = "SELECT DISTINCT bfc FROM Beneficiario bfc INNER JOIN bfc.servidor ser WHERE ser.serCodigo = :serCodigo";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("serCodigo", serCodigo);

        return findByQuery(query, parameters);
    }

    public static Beneficiario findByCpfEServidor(String bfcCpf, String serCodigo) throws FindException {
        String query = "SELECT DISTINCT bfc FROM Beneficiario bfc "
                + " INNER JOIN bfc.servidor ser "
                + " WHERE bfc.bfcCpf = :bfcCpf "
                + "AND ser.serCodigo = :serCodigo";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("bfcCpf", bfcCpf);
        parameters.put("serCodigo", serCodigo);

        List<Beneficiario> beneficiarios = findByQuery(query, parameters);
        if (beneficiarios == null || beneficiarios.size() == 0) {
            return null;
        } else if (beneficiarios.size() == 1) {
            return beneficiarios.get(0);
        } else {
            throw new FindException("mensagem.erro.mais.de.um.resultado.encontrado.beneficiario", (AcessoSistema) null);
        }
    }

    public static Beneficiario findByCpfEServidorENome(String bfcCpf, String serCodigo, String bfcNome) throws FindException {
        String query = "SELECT DISTINCT bfc FROM Beneficiario bfc "
                + " INNER JOIN bfc.servidor ser "
                + " WHERE bfc.bfcCpf = :bfcCpf "
                + "AND bfc.bfcNome = :bfcNome "
                + "AND ser.serCodigo = :serCodigo";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("bfcCpf", bfcCpf);
        parameters.put("bfcNome", bfcNome);
        parameters.put("serCodigo", serCodigo);

        List<Beneficiario> beneficiarios = findByQuery(query, parameters);
        if (beneficiarios == null || beneficiarios.size() == 0) {
            return null;
        } else if (beneficiarios.size() == 1) {
            return beneficiarios.get(0);
        } else {
            throw new FindException("mensagem.erro.mais.de.um.resultado.encontrado.beneficiario", (AcessoSistema) null);
        }
    }

    public static Beneficiario create(Servidor servidor, TipoBeneficiario tipoBeneficiario, MotivoDependencia motivoDependencia, Short ordemDependencia,
            String nome, String cpf, String rg, String sexo, String telefone, String celular, String nomeMae, GrauParentesco grauParantesco,
            Date dataNascimento, String estadoCivil, String subsidioConcedido, String subsidioConcedidoMotivo, Date bfcExcecaoDependenciaIni,
            Date bfcExcecaoDependenciaFim, StatusBeneficiario statusBeneficiario, Nacionalidade nacionalidade, Date bfcDataCasamento, Date bfcDataObito, String bfcIdentificador, String rseCodigo, String bfcClassificacao) throws CreateException {
        Beneficiario beneficiario = new Beneficiario();

        try {
            beneficiario.setBfcCodigo(DBHelper.getNextId());
            beneficiario.setServidor(servidor);
            beneficiario.setTipoBeneficiario(tipoBeneficiario);
            beneficiario.setMotivoDependencia(motivoDependencia);
            beneficiario.setBfcOrdemDependencia(ordemDependencia);
            beneficiario.setBfcNome(nome);
            beneficiario.setBfcCpf(cpf);
            beneficiario.setBfcRg(rg);
            beneficiario.setBfcSexo(sexo);
            beneficiario.setBfcTelefone(telefone);
            beneficiario.setBfcCelular(celular);
            beneficiario.setBfcNomeMae(nomeMae);
            beneficiario.setGrauParentesco(grauParantesco);
            beneficiario.setBfcDataNascimento(dataNascimento);
            beneficiario.setBfcEstadoCivil(estadoCivil);
            beneficiario.setBfcSubsidioConcedido(subsidioConcedido);
            beneficiario.setBfcSubsidioConcedidoMotivo(subsidioConcedidoMotivo);
            beneficiario.setBfcExcecaoDependenciaIni(bfcExcecaoDependenciaIni);
            beneficiario.setBfcExcecaoDependenciaFim(bfcExcecaoDependenciaFim);
            beneficiario.setStatusBeneficiario(statusBeneficiario);
            beneficiario.setNacionalidade(nacionalidade);
            beneficiario.setBfcDataCasamento(bfcDataCasamento);
            beneficiario.setBfcDataObito(bfcDataObito);
            beneficiario.setBfcIdentificador(bfcIdentificador);
            beneficiario.setRseCodigo(rseCodigo);
            beneficiario.setBfcClassificacao(bfcClassificacao);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }

        create(beneficiario);

        return beneficiario;
    }

    public static Beneficiario findDetachedByAdeCodigo(String adeCodigo, AcessoSistema responsavel) throws FindException {
        String query = "SELECT DISTINCT bfc FROM Beneficiario bfc "
                + "INNER JOIN bfc.contratoBeneficioSet cbe "
                + "INNER JOIN cbe.autDescontoSet aut "
                + "LEFT JOIN FETCH bfc.grauParentesco grp "
                + "WHERE aut.adeCodigo = :adeCodigo ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);

        List<Beneficiario> beneficiarios = findByQuery(query, parameters);
        if (beneficiarios == null || beneficiarios.size() == 0) {
            return null;
        } else if (beneficiarios.size() == 1) {
            SessionUtil.getSession().evict(beneficiarios.get(0));
            return beneficiarios.get(0);
        } else {
            throw new FindException("mensagem.erro.mais.de.um.resultado.encontrado.beneficiario", responsavel);
        }
    }

    public static Beneficiario findByIdentificadorEServidor(String bfcIdentificador, String serCodigo) throws FindException {
        String query = "SELECT DISTINCT bfc FROM Beneficiario bfc "
                + " INNER JOIN bfc.servidor ser "
                + " WHERE bfc.bfcIdentificador = :bfcIdentificador "
                + "AND ser.serCodigo = :serCodigo";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("bfcIdentificador", bfcIdentificador);
        parameters.put("serCodigo", serCodigo);

        List<Beneficiario> beneficiarios = findByQuery(query, parameters);
        if (beneficiarios == null || beneficiarios.size() == 0) {
            return null;
        } else if (beneficiarios.size() == 1) {
            return beneficiarios.get(0);
        } else {
            throw new FindException("mensagem.erro.mais.de.um.resultado.encontrado.beneficiario", (AcessoSistema) null);
        }
    }

    public static Beneficiario findByCpfEServidorERseCodigo(String bfcCpf, String serCodigo, String rseCodigo) throws FindException {
        String query = "SELECT DISTINCT bfc FROM Beneficiario bfc "
                + " INNER JOIN bfc.servidor ser "
                + " WHERE bfc.bfcCpf = :bfcCpf "
                + " AND ser.serCodigo = :serCodigo"
                + " AND bfc.rseCodigo = :rseCodigo";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("bfcCpf", bfcCpf);
        parameters.put("serCodigo", serCodigo);
        parameters.put("rseCodigo", rseCodigo);

        List<Beneficiario> beneficiarios = findByQuery(query, parameters);
        if (beneficiarios == null || beneficiarios.size() == 0) {
            return null;
        } else if (beneficiarios.size() == 1) {
            return beneficiarios.get(0);
        } else {
            throw new FindException("mensagem.erro.mais.de.um.resultado.encontrado.beneficiario", (AcessoSistema) null);
        }
    }

    public static Beneficiario findByIdentificadorEServidorERseCodigo(String bfcIdentificador, String serCodigo, String rseCodigo) throws FindException {
        String query = "SELECT DISTINCT bfc FROM Beneficiario bfc "
                + " INNER JOIN bfc.servidor ser "
                + " WHERE bfc.bfcIdentificador = :bfcIdentificador "
                + " AND ser.serCodigo = :serCodigo"
                + " AND bfc.rseCodigo = :rseCodigo";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("bfcIdentificador", bfcIdentificador);
        parameters.put("serCodigo", serCodigo);
        parameters.put("rseCodigo", rseCodigo);

        List<Beneficiario> beneficiarios = findByQuery(query, parameters);
        if (beneficiarios == null || beneficiarios.size() == 0) {
            return null;
        } else if (beneficiarios.size() == 1) {
            return beneficiarios.get(0);
        } else {
            throw new FindException("mensagem.erro.mais.de.um.resultado.encontrado.beneficiario", (AcessoSistema) null);
        }
    }
}
