package com.zetra.econsig.service.beneficios;


import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.AnexoBeneficiario;
import com.zetra.econsig.persistence.entity.AnexoBeneficiarioId;
import com.zetra.econsig.persistence.entity.Beneficiario;
import com.zetra.econsig.persistence.entity.ContratoBeneficio;
import com.zetra.econsig.persistence.entity.GrauParentesco;
import com.zetra.econsig.persistence.entity.MotivoDependencia;
import com.zetra.econsig.persistence.entity.Nacionalidade;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.persistence.entity.StatusBeneficiario;
import com.zetra.econsig.persistence.entity.TipoBeneficiario;

/**
 * <p>Title: BeneficiarioController</p>
 * <p>Description: Interface para operações realizadas nos beneficiários</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface BeneficiarioController {

    public void calcularOrdemDependenciaBeneficiario(String tipoEntidade, List<String> entCodigos, List<String> bfcCodigos, boolean simulacao, AcessoSistema responsavel) throws BeneficioControllerException;

    public List<TransferObject> listarBeneficiarios(TransferObject criterio, int offset, int size, AcessoSistema responsavel) throws BeneficioControllerException;

    public List<TransferObject> listarBeneficiarios(TransferObject criterio,  AcessoSistema responsavel) throws BeneficioControllerException;

    public List<TransferObject> listarBeneficiariosFiltradorEOrdenadoSimulador(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException;

    public int listarCountBeneficiarios(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException;

    public Beneficiario findBeneficiarioByCodigo(String bfcCodigo, AcessoSistema responsavel) throws BeneficioControllerException;

    public List<TransferObject> listaGrauParentesco(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException;

    public List<TransferObject> listaEstadoCivil(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException;

    public List<TransferObject> listarMotivoDependencia(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException;

    public Beneficiario create(Servidor servidor, TipoBeneficiario tipoBeneficiario, MotivoDependencia motivoDependencia, Short ordemDependencia,
            String nome, String cpf, String rg, String sexo, String telefone, String celular, String nomeMae, GrauParentesco grauParantesco,
            Date dataNascimento, String estadoCivil, String subsidioConcedido, String subsidioConcedidoMotivo, Date bfcExcecaoDependenciaIni,
            Date bfcExcecaoDependenciaFim, StatusBeneficiario statusBeneficiario, Nacionalidade nacionalidade, Date bfcDataCasamento, Date bfcDataObito,
            String bfcIdentificador, String rseCodigo, String bfcClassificacao, AcessoSistema responsavel) throws BeneficioControllerException;

    public Beneficiario create(Servidor servidor, TipoBeneficiario tipoBeneficiario, MotivoDependencia motivoDependencia, Short ordemDependencia,
            String nome, String cpf, String rg, String sexo, String telefone, String celular, String nomeMae, GrauParentesco grauParantesco,
            Date dataNascimento, String estadoCivil, String subsidioConcedido, String subsidioConcedidoMotivo, Date bfcExcecaoDependenciaIni,
            Date bfcExcecaoDependenciaFim, StatusBeneficiario statusBeneficiario, Nacionalidade nacionalidade, Date bfcDataCasamento, Date bfcDataObito, AcessoSistema responsavel) throws BeneficioControllerException;

    public void update(Beneficiario beneficiario, AcessoSistema responsavel) throws BeneficioControllerException;

    public void remove(Beneficiario beneficiario, AcessoSistema responsavel) throws BeneficioControllerException;

    public List<ContratoBeneficio> findContratoBeneficioByBeneficiarioAndTntCodigoAndSadCodigo(String bfcCodigo, List<String> tntCodigo, List<String> sadCodigo, AcessoSistema responsavel) throws BeneficioControllerException;

    public int listarCountAnexosBeneficiario(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException;

    public List<TransferObject> listarAnexosBeneficiario(CustomTransferObject criterio, int offset, int size, AcessoSistema responsavel) throws BeneficioControllerException;

    public AnexoBeneficiario findAnexoBeneficiarioByPrimaryKey(AnexoBeneficiarioId id, AcessoSistema responsavel) throws BeneficioControllerException;

    public void createAnexoBeneficiario(AnexoBeneficiario anexo, AcessoSistema responsavel) throws BeneficioControllerException;

    public void removeAnexoBeneficiario(AnexoBeneficiario anexo, AcessoSistema responsavel) throws BeneficioControllerException;

    public void updateAnexoBeneficiario(AnexoBeneficiario anexo, AcessoSistema responsavel) throws BeneficioControllerException;

    public List<TransferObject> listarNacionalidade(AcessoSistema responsavel) throws BeneficioControllerException;

    public List<TransferObject> listarCountBeneficiosPorBeneficiarios(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException;

    public String importaBeneficiariosDependentes(String arquivoEntrada, AcessoSistema responsavel) throws BeneficioControllerException;

    public Beneficiario buscaBeneficiarioSerRseIdentificacdor(String serCodigo, String rseCodigo, String bfcIdentificador, AcessoSistema resposavel) throws BeneficioControllerException;

    public Beneficiario buscaBeneficiarioAdeCodigo(String adeCodigo, AcessoSistema responsavel) throws BeneficioControllerException;

    public Beneficiario buscaBeneficiarioBfcCodigo(String bfcCodigo) throws BeneficioControllerException;
}
