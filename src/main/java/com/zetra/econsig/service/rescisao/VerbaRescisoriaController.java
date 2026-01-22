package com.zetra.econsig.service.rescisao;

import java.math.BigDecimal;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.VerbaRescisoriaControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.VerbaRescisoriaRse;

/**
 * <p>Title: VerbaRescisoriaController</p>
 * <p>Description: Interface para o session bean VerbaRescisoria</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public interface VerbaRescisoriaController {

    public List<TransferObject> listarVerbaRescisoriaRse(List<String> svrCodigos, AcessoSistema responsavel) throws VerbaRescisoriaControllerException;

    public int countVerbaRescisoriaRse(List<String> svrCodigos, AcessoSistema responsavel) throws VerbaRescisoriaControllerException;

    public List<TransferObject> listarVerbaRescisoriaRse(CustomTransferObject criterio, int offset, int size, AcessoSistema responsavel) throws VerbaRescisoriaControllerException;

    public void removerVerbaRescisoriaRse(String vrrCodigo, AcessoSistema responsavel) throws VerbaRescisoriaControllerException;

    public int countColaboradoresReterVerbaRescisoria(AcessoSistema responsavel) throws VerbaRescisoriaControllerException;

    public List<TransferObject> listarColaboradoresReterVerbaRescisoria(int offset, int size, AcessoSistema responsavel) throws VerbaRescisoriaControllerException;

    public void incluirCandidatoVerbaRescisoria(String rseCodigo, AcessoSistema responsavel) throws VerbaRescisoriaControllerException;

    public String processarInclusaoColaborador(TransferObject colaborador, AcessoSistema responsavel) throws VerbaRescisoriaControllerException;

    public List<TransferObject> listarContratosReterVerbaRescisoria(String vrrCodigo, AcessoSistema responsavel) throws VerbaRescisoriaControllerException;

    public List<TransferObject> calcularPreviaPagamentoVerbaRescisoria(String vrrCodigo, BigDecimal vrrValor, AcessoSistema responsavel) throws VerbaRescisoriaControllerException;

    public void confirmarVerbaRescisoria(String vrrCodigo, BigDecimal vrrValor, AcessoSistema responsavel) throws VerbaRescisoriaControllerException;

    public List<TransferObject> listarContratosVerbaRescisoriaConcluida(String vrrCodigo, AcessoSistema responsavel) throws VerbaRescisoriaControllerException;

    public List<TransferObject> listarContratosSaldoDevedorPendente(String vrrCodigo, AcessoSistema responsavel) throws VerbaRescisoriaControllerException;

    public void createVerbaRescisoriaLote(VerbaRescisoriaRse verbaRescisoriaRse) throws VerbaRescisoriaControllerException;

    public List<AutDesconto> listarConsignacoesReterVerbaRescisoriaSaldoInsuficiente(String rseCodigo, AcessoSistema responsavel) throws VerbaRescisoriaControllerException;

    public void confirmarVerbaRescisoria(BigDecimal sdvValor, String adeCodigo, AcessoSistema responsavel) throws VerbaRescisoriaControllerException;
    
    public void concluirVerbaRescisoria(List<VerbaRescisoriaRse> verbaRescisoriaRse, AcessoSistema responsavel) throws VerbaRescisoriaControllerException;
}
