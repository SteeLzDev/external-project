package com.zetra.econsig.service.beneficios;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CalculoBeneficioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.Beneficio;
import com.zetra.econsig.persistence.entity.CalculoBeneficio;
import com.zetra.econsig.persistence.entity.GrauParentesco;
import com.zetra.econsig.persistence.entity.MotivoDependencia;
import com.zetra.econsig.persistence.entity.Orgao;
import com.zetra.econsig.persistence.entity.TipoBeneficiario;

/**
 * <p>Title: CalculoBeneficioController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface CalculoBeneficioController {

    public List<TransferObject> listaCalculoBeneficio(TransferObject criterio, int offset, int size, AcessoSistema responsavel) throws CalculoBeneficioControllerException;

    public CalculoBeneficio create(TipoBeneficiario tipoBeneficio, Orgao orgao, Beneficio beneficio, GrauParentesco grauParentesco, MotivoDependencia motivoDependencia, Date vigenciaIni, Date vigenciaFim, BigDecimal valorMensalidade, BigDecimal valorSubsidio, Short faixaEtariaIni, Short faixaEtariaFim, BigDecimal fixaSalarialIni, BigDecimal fixaSalarialFim, AcessoSistema responsavel)throws CalculoBeneficioControllerException;

    public void update(CalculoBeneficio calculoBeneficio, AcessoSistema responsavel) throws CalculoBeneficioControllerException;

    public CalculoBeneficio findCalculoBeneficioByCodigo(String clbCodigo, AcessoSistema responsavel) throws CalculoBeneficioControllerException;

    public void remove(CalculoBeneficio calculoBeneficio, AcessoSistema responsavel)throws CalculoBeneficioControllerException;

    public void excluirTabelaIniciada(AcessoSistema responsavel)throws CalculoBeneficioControllerException;

    public int lstCountCalculoBeneficio(TransferObject criterio, AcessoSistema responsavel) throws CalculoBeneficioControllerException;

    public void iniciarTabelaVigente(AcessoSistema responsavel) throws CalculoBeneficioControllerException;

    public void ativarTabelaIniciada(AcessoSistema responsavel) throws CalculoBeneficioControllerException;

}