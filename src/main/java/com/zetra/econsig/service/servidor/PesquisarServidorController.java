package com.zetra.econsig.service.servidor;


import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.ImagemServidor;

/**
 * <p>Title: ServidorControllerBean</p>
 * <p>Description: Session Bean para a operação de Pesquisa de Servidor.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface PesquisarServidorController {

    public CustomTransferObject buscaServidor(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public CustomTransferObject buscaServidor(String rseCodigo, boolean retornaMargem, AcessoSistema responsavel) throws ServidorControllerException;

    public CustomTransferObject buscaServidor(String rseCodigo, String serCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public CustomTransferObject buscaServidor(String rseCodigo, String serCodigo, boolean retornaMargem, boolean retornaUsuLogin, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, AcessoSistema responsavel, boolean validaCpfMatricula, boolean validaPermissionario) throws ServidorControllerException;

    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario) throws ServidorControllerException;

    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario, String orgCodigo) throws ServidorControllerException;

    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, int offset, int count, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, int offset, int count, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario) throws ServidorControllerException;

    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, int offset, int count, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario, String orgCodigo) throws ServidorControllerException;

    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, int offset, int count, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario, String orgCodigo, TransferObject criterios) throws ServidorControllerException;

    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, int offset, int count, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario, String orgCodigo, TransferObject criterios, String vrsCodigo, boolean retornaUsuLogin, Boolean filtroVinculo) throws ServidorControllerException;

    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, Boolean filtroVinculo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> pesquisaServidorExato(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> pesquisaServidorExato(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, TransferObject criterios, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> pesquisaServidorExato(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, String numerContratoBeneficio, boolean buscaServidorBeneficio, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> pesquisaServidorExato(String tipo, String codigo, String estIdentificador, String orgIdentificador, List<String> listaMatricula, AcessoSistema responsavel) throws ServidorControllerException;

    public int countPesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario, String orgCodigo) throws ServidorControllerException;

    public int countPesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario, String orgCodigo, TransferObject criterios) throws ServidorControllerException;

    public int countPesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario, String orgCodigo, TransferObject criterios, String vrsCodigo) throws ServidorControllerException;

    public CustomTransferObject buscaUsuarioServidor(String usuCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public CustomTransferObject buscaUsuarioServidorBySerCodigo(String serCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public CustomTransferObject buscaUsuarioServidor(String usuCodigo, String usuLogin, String rseMatricula, String orgIdentificador, String estIdentificador, AcessoSistema responsavel) throws ServidorControllerException;

    public TransferObject sorteiaServidor(List<TransferObject> sorteados, String rseMatricula, AcessoSistema responsavel) throws ServidorControllerException;

    public List<String> listarCpfServidoresAtivos(AcessoSistema responsavel) throws ServidorControllerException;

    public List<String> listarEmailServidoresAtivos(AcessoSistema responsavel) throws ServidorControllerException;

    public int contarServidorPendente(TransferObject criterio, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> pesquisarServidorPendente(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> listarCodigoServidorConsignacaoAtivaRetorno(List<Integer> diasParam, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> listarServidorConsignacaoPendenteReativacao(AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> listarServidorMargemFolha(List<String> estCodigo, List<String> orgCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public CustomTransferObject getImagemServidor(String cpfServidor, AcessoSistema responsavel) throws ServidorControllerException;

    public String salvarImagemServidor(ImagemServidor imgServidor, AcessoSistema responsavel) throws ServidorControllerException;

    public boolean updateImagemServidor(ImagemServidor imgServidor, AcessoSistema responsavel) throws ServidorControllerException;
}
