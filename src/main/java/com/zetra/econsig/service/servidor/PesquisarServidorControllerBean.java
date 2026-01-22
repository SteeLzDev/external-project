package com.zetra.econsig.service.servidor;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.ImagemServidor;
import com.zetra.econsig.persistence.entity.ImagemServidorHome;
import com.zetra.econsig.persistence.query.margem.ListaMargemRegistroServidoresQuery;
import com.zetra.econsig.persistence.query.servidor.ListaCpfServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ListaEmailServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorCadastroQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorPendenteQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorPendenteValidacaoMargemFolhaQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ListarCodigoServidoresRetornoQuery;
import com.zetra.econsig.persistence.query.servidor.ListarServidoresConsignacaoPendenteReativacaoQuery;
import com.zetra.econsig.persistence.query.servidor.ObtemImagemServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ObtemServidorQuery;
import com.zetra.econsig.persistence.query.usuario.ObtemUsuarioServidorQuery;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ServidorControllerBean</p>
 * <p>Description: Session Bean para a operação de Pesquisa de Servidor.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class PesquisarServidorControllerBean implements PesquisarServidorController {
    private static final String VARIACAO = "VARIACAO";

	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(PesquisarServidorControllerBean.class);

    @Autowired
    private ParametroController parametroController;

    @Override
    public CustomTransferObject buscaServidor(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return buscaServidor(rseCodigo, null, false, false, responsavel);
    }

    @Override
    public CustomTransferObject buscaServidor(String rseCodigo, boolean retornaMargem, AcessoSistema responsavel) throws ServidorControllerException {
        return buscaServidor(rseCodigo, null, retornaMargem, false, responsavel);
    }

    @Override
    public CustomTransferObject buscaServidor(String rseCodigo, String serCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return buscaServidor(rseCodigo, serCodigo, false, false, responsavel);
    }

    @Override
    public CustomTransferObject buscaServidor(String rseCodigo, String serCodigo, boolean retornaMargem, boolean retornaUsuLogin, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            CustomTransferObject servidor = null;

            ObtemServidorQuery query = new ObtemServidorQuery();
            query.rseCodigo = rseCodigo;
            query.serCodigo = serCodigo;
            query.retornaMargem = retornaMargem;
            query.retornaUsuLogin = retornaUsuLogin;
            List<TransferObject> result = query.executarDTO();
            if (result != null && result.size() > 0) {
                servidor = (CustomTransferObject) result.get(0);
            }
            if (servidor == null) {
                throw new ServidorControllerException("mensagem.erro.nenhum.servidor.encontrado", responsavel);
            }

            return servidor;
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public CustomTransferObject buscaUsuarioServidor(String usuCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            CustomTransferObject servidor = null;

            ObtemUsuarioServidorQuery query = new ObtemUsuarioServidorQuery();
            query.usuCodigo = usuCodigo;
            query.permiteExcluidoFalecido = true;
            List<TransferObject> result = query.executarDTO();
            if (result != null && result.size() > 0) {
                servidor = (CustomTransferObject) result.stream().filter(ser -> !CodedValues.SRS_INATIVOS.contains(ser.getAttribute(Columns.SRS_CODIGO))).findFirst().orElse(result.get(0));

                //servidor = (CustomTransferObject) result.get(0);
                if (servidor.getAttribute(Columns.SRS_CODIGO).equals(CodedValues.SRS_EXCLUIDO)) {
                    throw new ServidorControllerException("mensagem.erro.servidor.excluido", responsavel);
                } else if (servidor.getAttribute(Columns.SRS_CODIGO).equals(CodedValues.SRS_FALECIDO)) {
                    throw new ServidorControllerException("mensagem.erro.servidor.falecido", responsavel);
                }
            }
            if (servidor == null) {
                throw new ServidorControllerException("mensagem.erro.nenhum.servidor.encontrado", responsavel);
            }

            return servidor;
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public CustomTransferObject buscaUsuarioServidorBySerCodigo(String serCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            CustomTransferObject servidor = null;

            ObtemUsuarioServidorQuery query = new ObtemUsuarioServidorQuery();
            query.serCodigo = serCodigo;
            List<TransferObject> result = query.executarDTO();
            if (result != null && result.size() > 0) {
                servidor = (CustomTransferObject) result.get(0);
            }
            if (servidor == null) {
                throw new ServidorControllerException("mensagem.erro.nenhum.servidor.encontrado", responsavel);
            }

            return servidor;
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public CustomTransferObject buscaUsuarioServidor(String usuCodigo, String usuLogin, String rseMatricula, String orgIdentificador, String estIdentificador, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            CustomTransferObject servidor = null;

            ObtemUsuarioServidorQuery query = new ObtemUsuarioServidorQuery();
            query.usuCodigo = usuCodigo;
            query.usuLogin = usuLogin;
            query.rseMatricula = rseMatricula;
            query.orgIdentificador = orgIdentificador;
            query.estIdentificador = estIdentificador;
            List<TransferObject> result = query.executarDTO();
            if (result != null && result.size() > 0) {
                servidor = (CustomTransferObject) result.get(0);
            }
            if (servidor == null) {
                throw new ServidorControllerException("mensagem.erro.nenhum.servidor.encontrado", responsavel);
            }

            return servidor;
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, AcessoSistema responsavel) throws ServidorControllerException {
        return pesquisaServidor(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, -1, -1, responsavel, true, null, false, null, null);
    }

    @Override
    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, AcessoSistema responsavel, boolean validaCpfMatricula, boolean validaPermissionario) throws ServidorControllerException {
        return pesquisaServidor(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, -1, -1, responsavel, validaCpfMatricula, null, validaPermissionario, null, null);
    }

    @Override
    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario) throws ServidorControllerException {
        return pesquisaServidor(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, -1, -1, responsavel, validaCpfMatricula, rseSrsCodigo, validaPermissionario, null, null);
    }

    @Override
    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario, String orgCodigo) throws ServidorControllerException {
        return pesquisaServidor(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, -1, -1, responsavel, validaCpfMatricula, rseSrsCodigo, validaPermissionario, orgCodigo, null);
    }

    @Override
    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, int offset, int count, AcessoSistema responsavel) throws ServidorControllerException {
        return pesquisaServidor(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, offset, count, responsavel, true, null, false, null, null);
    }

    @Override
    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, int offset, int count, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario) throws ServidorControllerException {
        return pesquisaServidor(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, offset, count, responsavel, validaCpfMatricula, rseSrsCodigo, validaPermissionario, null, null);
    }

    @Override
    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, int offset, int count, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario, String orgCodigo) throws ServidorControllerException {
        return pesquisaServidor(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, offset, count, responsavel, validaCpfMatricula, rseSrsCodigo, validaPermissionario, orgCodigo, null);
    }

    @Override
    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, int offset, int count, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario, String orgCodigo, TransferObject criterios) throws ServidorControllerException {
        return pesquisaServidor(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, offset, count, responsavel, validaCpfMatricula, rseSrsCodigo, validaPermissionario, orgCodigo, criterios, null, false, null);
    }

    @Override
    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, Boolean filtroVinculo, AcessoSistema responsavel) throws ServidorControllerException {
        return pesquisaServidor(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, -1, -1, responsavel, true, null, false, null, null, null, false, filtroVinculo);
    }


    @Override
    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, int offset, int count, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario, String orgCodigo, TransferObject criterios, String vrsCodigo, boolean retornaUsuLogin, Boolean filtroVinculo) throws ServidorControllerException {
        try {
            // Seta os critérios da query
            ListaServidorQuery query = new ListaServidorQuery(responsavel);
            query.tipo = tipo;
            query.codigo = codigo;
            query.estIdentificador = estIdentificador;
            query.orgIdentificador = orgIdentificador;
            query.orgCodigo = orgCodigo;
            query.rseMatricula = rseMatricula;
            query.serCPF = serCPF;
            query.validaPermissionario = validaPermissionario;
            query.vrsCodigo = vrsCodigo;
            query.retornaUsuLogin = retornaUsuLogin;

            if (filtroVinculo != null) {
                query.filtroVinculo = filtroVinculo;
            }

            if (criterios != null && criterios.getAtributos() != null && !criterios.getAtributos().isEmpty()) {
                query.setCriterio(criterios);
            }

            boolean matriculaExataSoap = false;
            if (responsavel.getCanal().equals(CanalEnum.SOAP) && responsavel.isCsa()){
                String param = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_PESQUISA_MATRICULA_EXATA_VIA_SOAP, AcessoSistema.getAcessoUsuarioSistema());
                matriculaExataSoap = param != null && param.equals(CodedValues.TPA_SIM);
            }

            // Verifica se o sistema tem busca de matricula exata
            query.pesquisaExata = matriculaExataSoap || ParamSist.paramEquals(CodedValues.TPC_PESQUISA_MATRICULA_INTEIRA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

            if (count != -1) {
                query.maxResults = count;
                query.firstResult = offset;
            }

            if (rseSrsCodigo != null && rseSrsCodigo.size() > 0) {
                query.rseSrsCodigo = new ArrayList<>(rseSrsCodigo);
            }

            // Lista os resultados
            List<TransferObject> result = query.executarDTO();

            if (result.size() == 0 && !validaCpfMatricula) {

                boolean requerMatriculaCpf = parametroController.requerMatriculaCpf(responsavel);
                boolean validaCpfPesqServidor = !ParamSist.paramEquals(CodedValues.TPC_VALIDA_CPF_PESQ_SERVIDOR, CodedValues.TPC_NAO, responsavel);

                if ((!validaCpfPesqServidor) && (requerMatriculaCpf) &&
                        (rseMatricula != null && !rseMatricula.equals(""))) {
                    // Se requer ambos matrícula e cpf, mas a pesquisa não encontrou nada
                    // então faz a pesquisa apenas pela matrícula
                    query.rseMatricula = rseMatricula;
                    query.serCPF = null;

                    // Reexecuta a pesquisa
                    result = query.executarDTO();

                    // Se ainda não encontrou nada, tenta pesquisar apenas pelo CPF
                    if ((result.size() == 0) && (serCPF != null && !serCPF.equals(""))) {
                        query.rseMatricula = null;
                        query.serCPF = serCPF;

                        // Reexecuta a pesquisa
                        result = query.executarDTO();
                    }
                }
            }

            // Grava log de pesquisa de servidor
            LogDelegate log = new LogDelegate(responsavel, Log.SERVIDOR, Log.SELECT, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.log.matricula.cpf", responsavel, rseMatricula, serCPF));
            log.write();

            return result;
        } catch (ParametroControllerException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (HQueryException ex) {
            throw new ServidorControllerException(ex);
        }
    }

    @Override
    public int countPesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario, String orgCodigo) throws ServidorControllerException {
        return countPesquisaServidor(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, responsavel, validaCpfMatricula, rseSrsCodigo, validaPermissionario, orgCodigo, null);
    }

    @Override
    public int countPesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario, String orgCodigo, TransferObject criterios) throws ServidorControllerException {
        return countPesquisaServidor(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, responsavel, validaCpfMatricula, rseSrsCodigo, validaPermissionario, orgCodigo, criterios, null);
    }

    @Override
    public int countPesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario, String orgCodigo, TransferObject criterios, String vrsCodigo) throws ServidorControllerException {
        try {
            // Seta os critérios da query
            ListaServidorQuery query = new ListaServidorQuery(responsavel);
            query.count = true;
            query.tipo = tipo;
            query.codigo = codigo;
            query.estIdentificador = estIdentificador;
            query.orgIdentificador = orgIdentificador;
            query.orgCodigo = orgCodigo;
            query.rseMatricula = rseMatricula;
            query.serCPF = serCPF;
            query.validaPermissionario = validaPermissionario;
            query.vrsCodigo = vrsCodigo;

            if (criterios != null && criterios.getAtributos() != null && !criterios.getAtributos().isEmpty()) {
                query.setCriterio(criterios);
            }

            // Verifica se o sistema tem busca de matricula exata
            boolean matriculaExata = ParamSist.paramEquals(CodedValues.TPC_PESQUISA_MATRICULA_INTEIRA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            query.pesquisaExata = matriculaExata;

            if (rseSrsCodigo != null && rseSrsCodigo.size() > 0){
                query.rseSrsCodigo = new ArrayList<>(rseSrsCodigo);
            }

            // Lista os resultados
            int total = query.executarContador();

            if (total == 0 && !validaCpfMatricula) {

                boolean requerMatriculaCpf = parametroController.requerMatriculaCpf(responsavel);
                boolean validaCpfPesqServidor = !ParamSist.paramEquals(CodedValues.TPC_VALIDA_CPF_PESQ_SERVIDOR, CodedValues.TPC_NAO, responsavel);

                if ((!validaCpfPesqServidor) && (requerMatriculaCpf) &&
                        (rseMatricula != null && !rseMatricula.equals(""))) {
                    // Se requer ambos matrícula e cpf, mas a pesquisa não encontrou nada
                    // então faz a pesquisa apenas pela matrícula
                    query.rseMatricula = rseMatricula;
                    query.serCPF = null;

                    // Reexecuta a pesquisa
                    total = query.executarContador();

                    // Se ainda não encontrou nada, tenta pesquisar apenas pelo CPF
                    if ((total == 0) && (serCPF != null && !serCPF.equals(""))) {
                        query.rseMatricula = null;
                        query.serCPF = serCPF;

                        // Reexecuta a pesquisa
                        total = query.executarContador();
                    }
                }
            }

            // Grava log de pesquisa de servidor
            LogDelegate log = new LogDelegate(responsavel, Log.SERVIDOR, Log.SELECT, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.log.matricula.cpf", responsavel, rseMatricula, serCPF));
            log.write();

            return total;
        } catch (ParametroControllerException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (HQueryException ex) {
            throw new ServidorControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> pesquisaServidorExato(String tipo, String codigo, String estIdentificador, String orgIdentificador,
            String rseMatricula, String serCPF, AcessoSistema responsavel) throws ServidorControllerException {
        return pesquisaServidorExato(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, null, false, null, responsavel);
    }

    @Override
    public List<TransferObject> pesquisaServidorExato(String tipo, String codigo, String estIdentificador, String orgIdentificador,
            String rseMatricula, String serCPF, TransferObject criterios, AcessoSistema responsavel) throws ServidorControllerException {
        return pesquisaServidorExato(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, null, false, criterios, responsavel);
    }

    @Override
    public List<TransferObject> pesquisaServidorExato(String tipo, String codigo, String estIdentificador, String orgIdentificador,
            String rseMatricula, String serCPF, String numerContratoBeneficio, boolean buscaBenificiario, AcessoSistema responsavel) throws ServidorControllerException {
        return pesquisaServidorExato(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, numerContratoBeneficio, buscaBenificiario, null, responsavel);
    }

    /**
     * pesquisaServidorExato - Se informar o numerContratoBeneficio é o parametro TPC_HABILITA_MODULO_BENEFICIOS_SAUDE estiver ativado
     * o serCPF não será pesquisado na tb_servidor, será pesquisado na tb_beneficiario
     * @param tipo
     * @param codigo
     * @param estIdentificador
     * @param orgIdentificador
     * @param rseMatricula
     * @param serCPF
     * @param numerContratoBeneficio
     * @param buscaBenificiario
     * @param criterios
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    private List<TransferObject> pesquisaServidorExato(String tipo, String codigo, String estIdentificador, String orgIdentificador,
            String rseMatricula, String serCPF, String numerContratoBeneficio, boolean buscaBenificiario, TransferObject criterios, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            // Seta os critérios da query
            ListaServidorQuery query = new ListaServidorQuery(responsavel);
            query.tipo = tipo;
            query.codigo = codigo;
            query.estIdentificador = estIdentificador;
            query.orgIdentificador = orgIdentificador;
            query.rseMatricula = rseMatricula;
            query.serCPF = serCPF;
            query.numerContratoBeneficio = numerContratoBeneficio;
            query.buscaBeneficiario = buscaBenificiario;
            query.pesquisaExata = true;

            if (criterios != null && criterios.getAtributos() != null && !criterios.getAtributos().isEmpty()) {
                query.setCriterio(criterios);
            }

            // Lista os resultados
            List<TransferObject> result = query.executarDTO();

            // Grava log de pesquisa de servidor
            LogDelegate log = new LogDelegate(responsavel, Log.SERVIDOR, Log.SELECT, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.log.matricula.cpf", responsavel, rseMatricula, serCPF));
            log.write();

            return result;
        } catch (HQueryException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> pesquisaServidorExato(String tipo, String codigo, String estIdentificador, String orgIdentificador, List<String> listaMatricula, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            // Seta os critérios da query
            ListaServidorQuery query = new ListaServidorQuery(responsavel);
            query.tipo = tipo;
            query.codigo = codigo;
            query.estIdentificador = estIdentificador;
            query.orgIdentificador = orgIdentificador;
            query.listaMatricula = listaMatricula;
            query.pesquisaExata = true;

            // Lista os resultados
            List<TransferObject> result = query.executarDTO();

            // Grava log de pesquisa de servidor
            LogDelegate log = new LogDelegate(responsavel, Log.SERVIDOR, Log.SELECT, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.log.matricula", responsavel, TextHelper.join(listaMatricula, ",")));
            log.write();

            return result;
        } catch (HQueryException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public TransferObject sorteiaServidor(List<TransferObject> sorteados, String rseMatricula, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            /*
             * Atualiza a quantidade de servidores disponíveis para seleção.
             * A cada iteração são excluídos os servidores que possuem os mesmos dados dos servidores já selecionados.
             */
            ListaServidorCadastroQuery query = new ListaServidorCadastroQuery();
            query.responsavel = responsavel;
            query.count = true;
            query.excecoes = sorteados;
            query.firstResult = null;
            query.maxResults = null;

            int totalServidores = query.executarContador();

            int matriculaNumerica = 0;
        	String matricula = "";

            if (!TextHelper.isNull(rseMatricula)) {
            	matricula = Pattern.compile("[^0-9]").matcher(rseMatricula).replaceAll("");
            	if(matricula.length() >= 9) {
            		matriculaNumerica = Integer.parseInt(matricula.substring(0, 9));
            	}else {
            		matriculaNumerica = Integer.parseInt(matricula);
            	}
            }

            query.count = false;
            query.excecoes = sorteados;

            //tratamento para não gerar erro na tela para o usuário servidor.
            if (totalServidores > 0) {
                query.firstResult = NumberHelper.getRandomNumber(totalServidores - 1, matriculaNumerica);
                query.maxResults = 1;
            }

            List<TransferObject> lista = query.executarDTO();

            // Se nenhum for encontrado, retorna nulo
            if (lista == null || lista.isEmpty()) {
                return null;
            }

            return lista.get(0);

        } catch (HQueryException | NoSuchAlgorithmException | NoSuchProviderException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    @Override
    public List<String> listarCpfServidoresAtivos(AcessoSistema responsavel) throws ServidorControllerException {
        try {
            return new ListaCpfServidorQuery().executarLista();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    @Override
    public List<String> listarEmailServidoresAtivos(AcessoSistema responsavel) throws ServidorControllerException {
        try {
            return new ListaEmailServidorQuery().executarLista();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    @Override
    public int contarServidorPendente(TransferObject criterio, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            ListaServidorPendenteQuery query = new ListaServidorPendenteQuery();
            query.count = true;
            query.criterio = criterio;
            query.responsavel = responsavel;
            return query.executarContador();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    @Override
    public List<TransferObject> pesquisarServidorPendente(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            ListaServidorPendenteQuery query = new ListaServidorPendenteQuery();
            query.criterio = criterio;
            query.responsavel = responsavel;

            if (count != -1) {
                query.maxResults = count;
                query.firstResult = offset;
            }

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    @Override
    public List<TransferObject> listarCodigoServidorConsignacaoAtivaRetorno(List<Integer> diasParam, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            ListarCodigoServidoresRetornoQuery query = new ListarCodigoServidoresRetornoQuery(diasParam);
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    @Override
    public List<TransferObject> listarServidorConsignacaoPendenteReativacao(AcessoSistema responsavel) throws ServidorControllerException {
        try {
            ListarServidoresConsignacaoPendenteReativacaoQuery query = new ListarServidoresConsignacaoPendenteReativacaoQuery();
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    @Override
    public List<TransferObject> listarServidorMargemFolha(List<String> estCodigo, List<String> orgCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            ListaServidorPendenteValidacaoMargemFolhaQuery query = new ListaServidorPendenteValidacaoMargemFolhaQuery(estCodigo, orgCodigo);
            List<TransferObject> servidores = query.executarDTO();

            if (servidores != null && !servidores.isEmpty()) {
            	BigDecimal percVariacaoMargemRse = ParamSist.getFloatParamSist(CodedValues.TPC_PERC_VARIACAO_MARGEM_SERVIDOR, BigDecimal.ZERO, responsavel);

                for (TransferObject servidor : servidores) {
                    BigDecimal maiorVariacao = BigDecimal.ZERO;
                    Map<Short, MargemTO> margens = new HashMap<>();
                    List<String> rseCodigos = new ArrayList<>();
                    rseCodigos.add(servidor.getAttribute(Columns.RSE_CODIGO).toString());
                    List<Short> margensAcimaMedia = new ArrayList<>();

                    try {
                        ListaMargemRegistroServidoresQuery lstMargem = new ListaMargemRegistroServidoresQuery();
                        lstMargem.margensComSvcAtivo = true;
                        lstMargem.rseCodigo = rseCodigos;
                        List<MargemTO> lstMargens = lstMargem.executarDTO(MargemTO.class);

                        for (MargemTO margem : lstMargens) {
                            Short codigoMargem = margem.getMarCodigo();

                            if (codigoMargem.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                                margem.setMrsMargem(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_MARGEM)) ? new BigDecimal(servidor.getAttribute(Columns.RSE_MARGEM).toString()) : null);
                                margem.setMrsMargemRest(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_MARGEM_REST)) ? new BigDecimal(servidor.getAttribute(Columns.RSE_MARGEM_REST).toString()) : null);
                                margem.setMrsMargemUsada(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_MARGEM_USADA)) ? new BigDecimal(servidor.getAttribute(Columns.RSE_MARGEM_USADA).toString()) : null);
                                margem.setMrsMediaMargem(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_MEDIA_MARGEM)) ? new BigDecimal(servidor.getAttribute(Columns.RSE_MEDIA_MARGEM).toString()) : null);
                            } else if (codigoMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                                margem.setMrsMargem(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_MARGEM_2)) ? new BigDecimal(servidor.getAttribute(Columns.RSE_MARGEM_2).toString()) : null);
                                margem.setMrsMargemRest(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_MARGEM_REST_2)) ? new BigDecimal(servidor.getAttribute(Columns.RSE_MARGEM_REST_2).toString()) : null);
                                margem.setMrsMargemUsada(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_MARGEM_USADA_2)) ? new BigDecimal(servidor.getAttribute(Columns.RSE_MARGEM_USADA_2).toString()) : null);
                                margem.setMrsMediaMargem(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_MEDIA_MARGEM_2)) ? new BigDecimal(servidor.getAttribute(Columns.RSE_MEDIA_MARGEM_2).toString()) : null);
                            } else if (codigoMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                                margem.setMrsMargem(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_MARGEM_3)) ? new BigDecimal(servidor.getAttribute(Columns.RSE_MARGEM_3).toString()) : null);
                                margem.setMrsMargemRest(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_MARGEM_REST_3)) ? new BigDecimal(servidor.getAttribute(Columns.RSE_MARGEM_REST_3).toString()) : null);
                                margem.setMrsMargemUsada(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_MARGEM_USADA_3)) ? new BigDecimal(servidor.getAttribute(Columns.RSE_MARGEM_USADA_3).toString()) : null);
                                margem.setMrsMediaMargem(!TextHelper.isNull(servidor.getAttribute(Columns.RSE_MEDIA_MARGEM_3)) ? new BigDecimal(servidor.getAttribute(Columns.RSE_MEDIA_MARGEM_3).toString()) : null);
                            }

                            mapMargensAcimaMedia(margensAcimaMedia, percVariacaoMargemRse, lstMargens.size(), margem);

                            if (!TextHelper.isNull(margem.getMrsMargem())) {
                                if (maiorVariacao.compareTo(margem.getVariacaoMediaMargem()) < 0) {
                                    maiorVariacao = margem.getVariacaoMediaMargem();
                                }
                                margens.put(margem.getMarCodigo(), margem);
                            }
                        }

                    } catch (HQueryException ex) {
                        LOG.error(ex.getMessage(), ex);
                        throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }

                    // Mantém maior percentual de variação para ordenar pela variação
                    servidor.setAttribute(VARIACAO, maiorVariacao);
                    servidor.setAttribute("MARGENS", margens);
                    servidor.setAttribute("MARGENS_ACIMA_MEDIA", margensAcimaMedia);
                }

                // Ordena pela variacao mais alta
                Collections.sort(servidores, (o1, o2) -> {
                        BigDecimal one = (BigDecimal) o1.getAttribute(VARIACAO);
                        BigDecimal two = (BigDecimal) o2.getAttribute(VARIACAO);

                        return two.compareTo(one);
                });
            }

            return servidores;

        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

	private void mapMargensAcimaMedia(List<Short> margensAcimaMedia, BigDecimal percVariacaoMargemRse, int numMargens, MargemTO margem) {

		BigDecimal media = margem.getMrsMediaMargem();
		BigDecimal margemRse = margem.getMrsMargem();
		if (!TextHelper.isNull(media) && media.compareTo(BigDecimal.ZERO) > 0) {
		    BigDecimal variacaoMargem = media.multiply(percVariacaoMargemRse).divide(new BigDecimal(100)).add(media);
		    if (!TextHelper.isNull(margemRse) && margemRse.compareTo(variacaoMargem) > 0) {

		    	if (margensAcimaMedia == null) {
		    		margensAcimaMedia = new ArrayList<>(numMargens);
		    		margensAcimaMedia.add(margem.getMarCodigo());
		    	} else {
		    		margensAcimaMedia.add(margem.getMarCodigo());
		    	}
		    }
		}
	}
    @Override
    public CustomTransferObject getImagemServidor(String cpfServidor, AcessoSistema responsavel) throws ServidorControllerException{
        try {
            ObtemImagemServidorQuery imagemServidor = new ObtemImagemServidorQuery();
            imagemServidor.cpfServidor = cpfServidor;

            CustomTransferObject servidorImagem = null;
            List<TransferObject> result = imagemServidor.executarDTO();
            if (result != null && result.size() > 0){
                servidorImagem = (CustomTransferObject) result.get(0);
            }


            return servidorImagem;
        } catch (HQueryException ex){
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    @Override
    public String salvarImagemServidor(ImagemServidor imgServidor, AcessoSistema responsavel) throws ServidorControllerException {
        try {
           final ImagemServidor img = ImagemServidorHome.create(imgServidor.getCpf(), imgServidor.getNomeArquivo());

           return img.getNomeArquivo();
        } catch (Exception ex){
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public boolean updateImagemServidor(ImagemServidor imgServidor, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            return ImagemServidorHome.updateImagemServidor(imgServidor.getCpf(), imgServidor.getNomeArquivo());
        } catch (UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
