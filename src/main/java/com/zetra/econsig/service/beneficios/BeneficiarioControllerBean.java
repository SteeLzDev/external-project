package com.zetra.econsig.service.beneficios;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTextoZip;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.dao.BeneficiarioDAO;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.entity.AnexoBeneficiario;
import com.zetra.econsig.persistence.entity.AnexoBeneficiarioHome;
import com.zetra.econsig.persistence.entity.AnexoBeneficiarioId;
import com.zetra.econsig.persistence.entity.Beneficiario;
import com.zetra.econsig.persistence.entity.BeneficiarioHome;
import com.zetra.econsig.persistence.entity.ContratoBeneficio;
import com.zetra.econsig.persistence.entity.ContratoBeneficioHome;
import com.zetra.econsig.persistence.entity.GrauParentesco;
import com.zetra.econsig.persistence.entity.GrauParentescoHome;
import com.zetra.econsig.persistence.entity.MotivoDependencia;
import com.zetra.econsig.persistence.entity.MotivoDependenciaHome;
import com.zetra.econsig.persistence.entity.Nacionalidade;
import com.zetra.econsig.persistence.entity.NacionalidadeHome;
import com.zetra.econsig.persistence.entity.OcorrenciaBeneficiarioHome;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.persistence.entity.ServidorHome;
import com.zetra.econsig.persistence.entity.StatusBeneficiario;
import com.zetra.econsig.persistence.entity.TipoBeneficiario;
import com.zetra.econsig.persistence.entity.TipoBeneficiarioHome;
import com.zetra.econsig.persistence.entity.TipoOcorrencia;
import com.zetra.econsig.persistence.entity.TipoOcorrenciaHome;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.persistence.entity.UsuarioHome;
import com.zetra.econsig.persistence.query.beneficios.ListarAnexoBeneficiariosQuery;
import com.zetra.econsig.persistence.query.beneficios.ListarBeneficiariosQuery;
import com.zetra.econsig.persistence.query.beneficios.ListarCountBeneficiosPorBeneficiariosQuery;
import com.zetra.econsig.persistence.query.beneficios.ListarNacionalidadeQuery;
import com.zetra.econsig.persistence.query.beneficios.beneficiario.ListarBeneficiariosPorTipoQuery;
import com.zetra.econsig.persistence.query.beneficios.beneficiario.ListarGrauParentescoQuery;
import com.zetra.econsig.persistence.query.beneficios.beneficiario.ListarMotivoDependenciaQuery;
import com.zetra.econsig.persistence.query.beneficios.subsidio.ListarBeneficiariosEscolhidosQuery;
import com.zetra.econsig.persistence.query.servidor.ListaEstadoCivilQuery;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.GrauParentescoEnum;
import com.zetra.econsig.values.StatusBeneficiarioEnum;
import com.zetra.econsig.values.TipoBeneficiarioEnum;
import com.zetra.econsig.web.filter.XSSPreventionFilter;

/**
 * <p>Title: BeneficiarioControllerBean</p>
 * <p>Description: Session Bean para a operações relacionadas a beneficiários</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class BeneficiarioControllerBean implements BeneficiarioController {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BeneficiarioControllerBean.class);

    public static final int TAMANHO_MSG_ERRO_DEFAULT = 100;

    public static final String COMPLEMENTO_DEFAULT = " ";

    @Autowired
    private CalcularSubsidioBeneficioController calcularSubsidioBeneficioController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private ServidorController servidorController;

    @Override
    public void calcularOrdemDependenciaBeneficiario(String tipoEntidade, List<String> entCodigos, List<String> bfcCodigos, boolean simular, AcessoSistema responsavel) throws BeneficioControllerException {
        calcularOrdemDependenciaBeneficiario(tipoEntidade, entCodigos, false, bfcCodigos, simular, responsavel);
    }

    /**
     * Realiza o cálculo da ordem de dependência dos beneficiários.
     *
     * Se for usar essa chamada foram do Calculo de Subsidido lembrar que tem que chamar obtemPeriodoBeneficio salvando o periodo
     *
     * @param tipoEntidade : RSE, ORG ou EST
     * @param entCodigos   : Códigos das entidades, sejam RSE_CODIGO, ORG_CODIGO ou EST_CODIGO
     * @param responsavel  : Responsável pela operação
     * @throws BeneficioControllerException
     */
    private void calcularOrdemDependenciaBeneficiario(String tipoEntidade, List<String> entCodigos, boolean OrdemBeneficiario, List<String> bfcCodigos, boolean simulacao, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            Usuario usuario = null;
            try {
                usuario = UsuarioHome.findByPrimaryKey(responsavel.getUsuCodigo());
            } catch (FindException ex) {
                throw new BeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }

            boolean calcularSubsidioInativos = ParamSist.paramEquals(CodedValues.TPC_APLICA_SUBSIDIO_FAMILIA_SERVIDOR_INATIVO, CodedValues.TPC_SIM, responsavel);

            List<String> srsCodigos = new ArrayList<>();
            srsCodigos.add(CodedValues.SRS_ATIVO);
            if (calcularSubsidioInativos) {
                srsCodigos.addAll(CodedValues.SRS_BLOQUEADOS);
            }

            List<String> tibCodigosTitular = new ArrayList<>();
            tibCodigosTitular.add(CodedValues.TIB_TITULAR);

            List<String> tibCodigosTodos = new ArrayList<>();
            tibCodigosTodos.add(CodedValues.TIB_TITULAR);
            tibCodigosTodos.add(CodedValues.TIB_DEPENDENTE);
            tibCodigosTodos.add(CodedValues.TIB_AGREGADO);

            ListarBeneficiariosPorTipoQuery queryTitulares = new ListarBeneficiariosPorTipoQuery();
            queryTitulares.srsCodigos = srsCodigos;
            queryTitulares.tipoEntidade = tipoEntidade;
            queryTitulares.entCodigos = entCodigos;
            queryTitulares.tibCodigos = tibCodigosTitular;
            queryTitulares.simulacao = simulacao;
            if (simulacao && bfcCodigos != null && !bfcCodigos.isEmpty()) {
                queryTitulares.bfcCodigos = bfcCodigos;
            }

            List<TransferObject> servidores = queryTitulares.executarDTO();
            for (TransferObject servidor : servidores) {
                SessionUtil.clearSession(SessionUtil.getSession());

                String serCodigo = (String) servidor.getAttribute(Columns.RSE_SER_CODIGO);

                // Lista dependentes e agregados ordenados pelas regras de prioridade
                ListarBeneficiariosPorTipoQuery queryDepAgr = new ListarBeneficiariosPorTipoQuery();
                queryDepAgr.srsCodigos = srsCodigos;
                queryDepAgr.tipoEntidade = tipoEntidade;
                queryDepAgr.entCodigos = entCodigos;
                queryDepAgr.tibCodigos = tibCodigosTodos;
                queryDepAgr.serCodigo = serCodigo;
                queryDepAgr.aplicarRegrasDeOrdemDependencia = true;
                queryDepAgr.simulacao = simulacao;
                if (simulacao && bfcCodigos != null && !bfcCodigos.isEmpty()) {
                    queryDepAgr.bfcCodigos = bfcCodigos;
                }

                List<TransferObject> beneficiarios = queryDepAgr.executarDTO();
                short ordemDependencia = 0;
                for (TransferObject beneficiario : beneficiarios) {
                    String bfcCodigo = beneficiario.getAttribute(Columns.BFC_CODIGO).toString();

                    // Atualiza a ordem de dependência do beneficiário
                    Beneficiario bfc = BeneficiarioHome.findByPrimaryKey(bfcCodigo);
                    if (bfc.getBfcOrdemDependencia() != ordemDependencia) {
                        bfc.setBfcOrdemDependencia(ordemDependencia);
                        BeneficiarioHome.update(bfc);
                        // Cria ocorrência de alteração de ordem de dependência
                        OcorrenciaBeneficiarioHome.create(TipoOcorrenciaHome.findByPrimaryKey(CodedValues.TOC_ALTERACAO_BENFICIARIO), usuario, bfc, null, null, ApplicationResourcesHelper.getMessage("mensagem.beneficio.ocorrencia.beneficiario.alteracao.ordem.dependencia", responsavel), responsavel.getIpUsuario());
                    }
                    ordemDependencia++;
                }
            }
        } catch (FindException | UpdateException | CreateException | HQueryException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new BeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (BeneficioControllerException ex) {
            LOG.error(ex.getMessage());
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw ex;
        }
    }

    @Override
    public List<TransferObject> listarBeneficiarios(TransferObject criterio, int offset, int size, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            ListarBeneficiariosQuery query = new ListarBeneficiariosQuery();

            if (criterio != null) {
                query.rseCodigo = (String) criterio.getAttribute(Columns.RSE_CODIGO);
                query.rseMatricula = (String) criterio.getAttribute(Columns.RSE_MATRICULA);
                query.bfcCpf = (String) criterio.getAttribute(Columns.BFC_CPF);
                query.cbeNumero = (String) criterio.getAttribute(Columns.CBE_NUMERO);
                query.tibCodigo = (String) criterio.getAttribute(Columns.TIB_CODIGO);

                if (criterio.getAttribute("filtro_tipo") != null) {
                    if (!criterio.getAttribute("filtro_tipo").equals("-1")) {
                        query.filtro = criterio.getAttribute("filtro").toString();
                        query.filtro_tipo = criterio.getAttribute("filtro_tipo").toString();
                    } else {
                        query.filtro_tipo = criterio.getAttribute("filtro_tipo").toString();
                    }
                }
            }

            query.count = false;
            if(offset != -1) {
                query.firstResult = offset;
            }
            if(size != -1) {
                query.maxResults = size;
            }

            return query.executarDTO();
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listarBeneficiarios(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException {
        return listarBeneficiarios(criterio, -1, -1, responsavel);
    }

    @Override
    public List<TransferObject> listarBeneficiariosFiltradorEOrdenadoSimulador(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            if (criterio == null) {
                LOG.error("Nenhum criterio informado para realziar a listagem");
                throw new BeneficioControllerException("mensagem.erroInternoSistema", responsavel);
            }

            if (criterio.getAttribute(Columns.RSE_CODIGO) == null) {
                throw new BeneficioControllerException("mensagem.erroInternoSistema", responsavel);
            }

            String rseCodigo = (String) criterio.getAttribute(Columns.RSE_CODIGO);

            boolean ordemPrioridadeGpFamiliarDependencia = ParamSist.paramEquals(CodedValues.TPC_ORDEM_PRIORIDADE_SUBSIDIO_GRUPO_FAMILIAR, CodedValues.ORDEM_PRIORIDADE_SUBSIDIO_GP_FAMILIAR_DEPENDENCIA, responsavel);
            boolean ordemPrioridadeGpFamiliarParentesco = ParamSist.paramEquals(CodedValues.TPC_ORDEM_PRIORIDADE_SUBSIDIO_GRUPO_FAMILIAR, CodedValues.ORDEM_PRIORIDADE_SUBSIDIO_GP_FAMILIAR_GRAU_PARENTESCO, responsavel);

            List<String> sbeCodigos = Arrays.asList(StatusBeneficiarioEnum.ATIVO.sbeCodigo, StatusBeneficiarioEnum.INATIVO.sbeCodigo);

            if (ordemPrioridadeGpFamiliarDependencia) {
                LOG.info("Iniciando o calculo da ordem de dependencia do beneficiarios");
                calcularOrdemDependenciaBeneficiario("RSE", Arrays.asList(rseCodigo), null, true, responsavel);
                LOG.info("Fim do calculo da ordem de dependencia do beneficiarios");
            }

            ListarBeneficiariosEscolhidosQuery query = new ListarBeneficiariosEscolhidosQuery();
            query.rseCodigo = rseCodigo;
            query.sbeCodigos = sbeCodigos;

            if (criterio.getAttribute(Columns.SCB_CODIGO) != null) {
                query.notScbCodigos = (List<String>) criterio.getAttribute(Columns.SCB_CODIGO);
            }

            if (criterio.getAttribute(Columns.NSE_CODIGO) != null) {
                query.nseCodigo = (String) criterio.getAttribute(Columns.NSE_CODIGO);
            }

            if (criterio.getAttribute(Columns.BEN_CODIGO) != null) {
                query.benCodigo = (String) criterio.getAttribute(Columns.BEN_CODIGO);
            }

            List<TransferObject> beneficiarios = query.executarDTO();

            // Se o usuario que estiver realizando essa ação for um servidor temos que filtar
            if (responsavel.isSer()) {
                // Se foi informado o SVC codigo vamos add no TransferObject os dados do serviço para ser usado nos metodos de filtrar e ordenas.
                if (criterio.getAttribute(Columns.SVC_CODIGO) != null) {
                    String svcCodigo = (String) criterio.getAttribute(Columns.SVC_CODIGO);
                    // Busca os parâmetros de serviço
                    ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

                    for (TransferObject beneficiario : beneficiarios) {
                        beneficiario.setAttribute(Columns.PSE_VLR + CodedValues.TPS_IDADE_MAX_DEPENDENTE_EST_SUBSIDIO, paramSvcCse.getTpsIdadeMaxDependenteEstSubsidio());
                        beneficiario.setAttribute(Columns.PSE_VLR + CodedValues.TPS_IDADE_MAX_DEPENDENTE_DIREITO_SUBSIDIO, paramSvcCse.getTpsIdadeMaxDependenteDireitoSubsidio());
                        beneficiario.setAttribute(Columns.PSE_VLR + CodedValues.TPS_AGREGADO_PODE_TER_SUBSIDIO, paramSvcCse.getTpsAgregadoPodeTerSubsidio());
                        beneficiario.setAttribute(Columns.PSE_VLR + CodedValues.TPS_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO, paramSvcCse.getTpsPaiMaeTitularesDivorciadosSubsidio());
                        beneficiario.setAttribute(Columns.PSE_VLR + CodedValues.TPS_DATA_LIMITE_VIGENCIA_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO, paramSvcCse.getTpsDataLimiteVigenciaPaiMaeTitularesDivorciadosSubsidio());
                    }

                }
                //  Ordenando os beneficiarios com direito a sub
                beneficiarios = calcularSubsidioBeneficioController.ordenarBeneficiariosDireitoSubsidio(beneficiarios, ordemPrioridadeGpFamiliarDependencia, ordemPrioridadeGpFamiliarParentesco, responsavel);
            }

            return beneficiarios;
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public int listarCountBeneficiarios(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            ListarBeneficiariosQuery query = new ListarBeneficiariosQuery();

            if (criterio != null) {
                query.rseCodigo = (String) criterio.getAttribute(Columns.RSE_CODIGO);
                if (!criterio.getAttribute("filtro_tipo").equals("-1")) {
                    query.filtro = criterio.getAttribute("filtro").toString();
                    query.filtro_tipo = criterio.getAttribute("filtro_tipo").toString();
                } else {
                    query.filtro_tipo = criterio.getAttribute("filtro_tipo").toString();
                }
            }

            query.count = true;

            return query.executarContador();
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public Beneficiario findBeneficiarioByCodigo(String bfcCodigo, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            return BeneficiarioHome.findByPrimaryKey(bfcCodigo);
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listaGrauParentesco(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            ListarGrauParentescoQuery query = new ListarGrauParentescoQuery();

            if (criterio != null) {
                query.grpCodigo = (String) criterio.getAttribute(Columns.GRP_CODIGO);
            }

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage());
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listarNacionalidade(AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            ListarNacionalidadeQuery query = new ListarNacionalidadeQuery();

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage());
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listaEstadoCivil(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            ListaEstadoCivilQuery query = new ListaEstadoCivilQuery();

            if (criterio != null) {
                query.estCvlCodigo = (String) criterio.getAttribute(Columns.EST_CIVIL_CODIGO);
            }

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage());
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listarMotivoDependencia(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            ListarMotivoDependenciaQuery query = new ListarMotivoDependenciaQuery();

            if (criterio != null) {
                query.mdeCodigo = (String) criterio.getAttribute(Columns.MDE_CODIGO);
            }

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage());
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public Beneficiario create(Servidor servidor, TipoBeneficiario tipoBeneficiario, MotivoDependencia motivoDependencia, Short ordemDependencia, String nome, String cpf, String rg, String sexo, String telefone, String celular, String nomeMae, GrauParentesco grauParantesco, Date dataNascimento, String estadoCivil, String subsidioConcedido, String subsidioConcedidoMotivo, Date bfcExcecaoDependenciaIni, Date bfcExcecaoDependenciaFim, StatusBeneficiario statusBeneficiario, Nacionalidade nacionalidade, Date bfcDataCasamento, Date bfcDataObito, AcessoSistema responsavel) throws BeneficioControllerException {
        return create(servidor, tipoBeneficiario, motivoDependencia, ordemDependencia, nome, cpf, rg, sexo, telefone, celular, nomeMae, grauParantesco, dataNascimento, estadoCivil, subsidioConcedido, subsidioConcedidoMotivo, bfcExcecaoDependenciaIni, bfcExcecaoDependenciaFim, statusBeneficiario, nacionalidade, bfcDataCasamento, bfcDataObito, null, null, null, responsavel);
    }

    @Override
    public Beneficiario create(Servidor servidor, TipoBeneficiario tipoBeneficiario, MotivoDependencia motivoDependencia, Short ordemDependencia, String nome, String cpf, String rg, String sexo, String telefone, String celular, String nomeMae, GrauParentesco grauParantesco, Date dataNascimento, String estadoCivil, String subsidioConcedido, String subsidioConcedidoMotivo, Date bfcExcecaoDependenciaIni, Date bfcExcecaoDependenciaFim, StatusBeneficiario statusBeneficiario, Nacionalidade nacionalidade, Date bfcDataCasamento, Date bfcDataObito, String bfcIdentificador, String rseCodigo, String bfcClassificacao, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            Beneficiario beneficiario = BeneficiarioHome.create(servidor, tipoBeneficiario, motivoDependencia, ordemDependencia, nome, cpf, rg, sexo, telefone, celular, nomeMae, grauParantesco, dataNascimento, estadoCivil, subsidioConcedido, subsidioConcedidoMotivo, bfcExcecaoDependenciaIni, bfcExcecaoDependenciaFim, statusBeneficiario, nacionalidade, bfcDataCasamento, bfcDataObito, bfcIdentificador, rseCodigo, bfcClassificacao);

            try {
                analiseRegrasNegocioBeneficiario(beneficiario, responsavel);
            } catch (BeneficioControllerException ex) {
                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                LOG.error(ex.getMessage(), ex);
                throw new BeneficioControllerException(ex);
            }

            LogDelegate log = new LogDelegate(responsavel, Log.BENEFICIARIO, Log.CREATE, Log.LOG_INFORMACAO);
            log.write();

            // Criando ocorrência de inclusão de beneficiário
            TipoOcorrencia tipoOcorrencia = new TipoOcorrencia();
            tipoOcorrencia.setTocCodigo(CodedValues.TOC_INCLUSAO_BENFICIARIO);
            Usuario usuario = new Usuario();
            usuario.setUsuCodigo(responsavel.getUsuCodigo());
            OcorrenciaBeneficiarioHome.create(tipoOcorrencia, usuario, beneficiario, null, null, ApplicationResourcesHelper.getMessage("mensagem.informacao.beneficiario.inclusao", responsavel), responsavel.getIpUsuario());

            return beneficiario;
        } catch (CreateException | LogControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public void update(Beneficiario beneficiario, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            Beneficiario beneficiarioAntigo = BeneficiarioHome.findByPrimaryKey(beneficiario.getBfcCodigo());
            LogDelegate log = new LogDelegate(responsavel, Log.BENEFICIARIO, Log.UPDATE, Log.LOG_INFORMACAO);
            boolean teveAlteracao = false;

            try {
                analiseRegrasNegocioBeneficiario(beneficiario, responsavel);
            } catch (BeneficioControllerException ex) {
                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                LOG.error(ex.getMessage(), ex);
                throw new BeneficioControllerException(ex);
            }

            if ((beneficiarioAntigo.getTipoBeneficiario() == null && beneficiario.getTipoBeneficiario() != null) || (beneficiarioAntigo.getTipoBeneficiario() != null && beneficiario.getTipoBeneficiario() == null) || ((beneficiarioAntigo.getTipoBeneficiario() != null && beneficiario.getTipoBeneficiario() != null) && (beneficiario.getTipoBeneficiario().getTibCodigo().compareTo(beneficiarioAntigo.getTipoBeneficiario().getTibCodigo()) != 0))) {
                log.addChangedField(Columns.BFC_TIB_CODIGO, beneficiario.getTipoBeneficiario() != null ? beneficiario.getTipoBeneficiario().getTibCodigo() : null);
                teveAlteracao = true;
            }
            if ((beneficiarioAntigo.getBfcNome() == null && beneficiario.getBfcNome() != null) || (beneficiarioAntigo.getBfcNome() != null && beneficiario.getBfcNome() == null) || ((beneficiarioAntigo.getBfcNome() != null && beneficiario.getBfcNome() != null) && (beneficiario.getBfcNome().compareTo(beneficiarioAntigo.getBfcNome()) != 0))) {
                log.addChangedField(Columns.BFC_NOME, beneficiario.getBfcNome());
                teveAlteracao = true;
            }
            if ((beneficiarioAntigo.getBfcCpf() == null && beneficiario.getBfcCpf() != null) || (beneficiarioAntigo.getBfcCpf() != null && beneficiario.getBfcCpf() == null) || ((beneficiarioAntigo.getBfcCpf() != null && beneficiario.getBfcCpf() != null) && (beneficiario.getBfcCpf().compareTo(beneficiarioAntigo.getBfcCpf()) != 0))) {
                log.addChangedField(Columns.BFC_CPF, beneficiario.getBfcCpf());
                teveAlteracao = true;
            }
            if ((beneficiarioAntigo.getBfcRg() == null && beneficiario.getBfcRg() != null) || (beneficiarioAntigo.getBfcRg() != null && beneficiario.getBfcRg() == null) || ((beneficiarioAntigo.getBfcRg() != null && beneficiario.getBfcRg() != null) && (beneficiario.getBfcRg().compareTo(beneficiarioAntigo.getBfcRg()) != 0))) {
                log.addChangedField(Columns.BFC_RG, beneficiario.getBfcRg());
                teveAlteracao = true;
            }
            if ((beneficiarioAntigo.getBfcSexo() == null && beneficiario.getBfcSexo() != null) || (beneficiarioAntigo.getBfcSexo() != null && beneficiario.getBfcSexo() == null) || ((beneficiarioAntigo.getBfcSexo() != null && beneficiario.getBfcSexo() != null) && (beneficiario.getBfcSexo().compareTo(beneficiarioAntigo.getBfcSexo()) != 0))) {
                log.addChangedField(Columns.BFC_SEXO, beneficiario.getBfcSexo());
                teveAlteracao = true;
            }
            if ((beneficiarioAntigo.getBfcTelefone() == null && beneficiario.getBfcTelefone() != null) || (beneficiarioAntigo.getBfcTelefone() != null && beneficiario.getBfcTelefone() == null) || ((beneficiarioAntigo.getBfcTelefone() != null && beneficiario.getBfcTelefone() != null) && (beneficiario.getBfcTelefone().compareTo(beneficiarioAntigo.getBfcTelefone()) != 0))) {
                log.addChangedField(Columns.BFC_TELEFONE, beneficiario.getBfcTelefone());
                teveAlteracao = true;
            }
            if ((beneficiarioAntigo.getBfcCelular() == null && beneficiario.getBfcCelular() != null) || (beneficiarioAntigo.getBfcCelular() != null && beneficiario.getBfcCelular() == null) || ((beneficiarioAntigo.getBfcCelular() != null && beneficiario.getBfcCelular() != null) && (beneficiario.getBfcCelular().compareTo(beneficiarioAntigo.getBfcCelular()) != 0))) {
                log.addChangedField(Columns.BFC_CELULAR, beneficiario.getBfcCelular());
                teveAlteracao = true;
            }
            if ((beneficiarioAntigo.getBfcNomeMae() == null && beneficiario.getBfcNomeMae() != null) || (beneficiarioAntigo.getBfcNomeMae() != null && beneficiario.getBfcNomeMae() == null) || ((beneficiarioAntigo.getBfcNomeMae() != null && beneficiario.getBfcNomeMae() != null) && (beneficiario.getBfcNomeMae().compareTo(beneficiarioAntigo.getBfcNomeMae()) != 0))) {
                log.addChangedField(Columns.BFC_NOME_MAE, beneficiario.getBfcNomeMae());
                teveAlteracao = true;
            }
            if ((beneficiario.getGrauParentesco() == null && beneficiarioAntigo.getGrauParentesco() != null) || (beneficiario.getGrauParentesco() != null && beneficiarioAntigo.getGrauParentesco() == null) || (beneficiarioAntigo.getGrauParentesco() != null && beneficiario.getGrauParentesco() != null) || (beneficiario.getGrauParentesco() != null && !beneficiario.getGrauParentesco().getGrpCodigo().equals(beneficiarioAntigo.getGrauParentesco().getGrpCodigo()))) {
                log.addChangedField(Columns.BFC_GRP_CODIGO, beneficiario.getGrauParentesco() != null ? beneficiario.getGrauParentesco().getGrpCodigo() : null);
                teveAlteracao = true;
            }
            if ((beneficiario.getNacionalidade() == null && beneficiarioAntigo.getNacionalidade() != null)
                        || (beneficiario.getNacionalidade() != null && beneficiarioAntigo.getNacionalidade() == null)
                        || (beneficiarioAntigo.getNacionalidade() != null && beneficiario.getNacionalidade() != null)
                        || (beneficiario.getNacionalidade() != null && !beneficiario.getNacionalidade().getNacCodigo().equals(beneficiarioAntigo.getNacionalidade().getNacCodigo()))) {
                log.addChangedField(Columns.BFC_NAC_CODIGO, beneficiario.getNacionalidade() != null ? beneficiario.getNacionalidade().getNacCodigo() : null);
                teveAlteracao = true;
            }
            if ((beneficiarioAntigo.getBfcDataNascimento() == null && beneficiario.getBfcDataNascimento() != null) || (beneficiarioAntigo.getBfcDataNascimento() != null && beneficiario.getBfcDataNascimento() == null) || ((beneficiarioAntigo.getBfcDataNascimento() != null && beneficiario.getBfcDataNascimento() != null) && (beneficiario.getBfcDataNascimento().compareTo(beneficiarioAntigo.getBfcDataNascimento()) != 0))) {
                log.addChangedField(Columns.BFC_DATA_NASCIMENTO, beneficiario.getBfcDataNascimento());
                teveAlteracao = true;
            }
            if ((beneficiarioAntigo.getBfcEstadoCivil() == null && beneficiario.getBfcEstadoCivil() != null) || (beneficiarioAntigo.getBfcEstadoCivil() != null && beneficiario.getBfcEstadoCivil() == null) || ((beneficiarioAntigo.getBfcEstadoCivil() != null && beneficiario.getBfcEstadoCivil() != null) && (beneficiario.getBfcEstadoCivil().compareTo(beneficiarioAntigo.getBfcEstadoCivil()) != 0))) {
                log.addChangedField(Columns.BFC_ESTADO_CIVIL, beneficiario.getBfcEstadoCivil());
                teveAlteracao = true;
            }
            if ((beneficiarioAntigo.getMotivoDependencia() == null && beneficiario.getMotivoDependencia() != null) || (beneficiarioAntigo.getMotivoDependencia() != null && beneficiario.getMotivoDependencia() == null) || ((beneficiarioAntigo.getMotivoDependencia() != null && beneficiario.getMotivoDependencia() != null) && (!beneficiario.getMotivoDependencia().getMdeCodigo().equals(beneficiarioAntigo.getMotivoDependencia().getMdeCodigo())))) {
                log.addChangedField(Columns.BFC_MDE_CODIGO, beneficiario.getMotivoDependencia() != null ? beneficiario.getMotivoDependencia().getMdeCodigo() : null);
                teveAlteracao = true;
            }
            if ((beneficiarioAntigo.getBfcExcecaoDependenciaIni() == null && beneficiario.getBfcExcecaoDependenciaIni() != null) || (beneficiarioAntigo.getBfcExcecaoDependenciaIni() != null && beneficiario.getBfcExcecaoDependenciaIni() == null) || ((beneficiarioAntigo.getBfcExcecaoDependenciaIni() != null && beneficiario.getBfcExcecaoDependenciaIni() != null) && (beneficiario.getBfcExcecaoDependenciaIni().compareTo(beneficiarioAntigo.getBfcExcecaoDependenciaIni())) != 0)) {
                log.addChangedField(Columns.BFC_EXCECAO_DEPENDENCIA_INI, beneficiario.getBfcExcecaoDependenciaIni());
                teveAlteracao = true;
            }
            if ((beneficiarioAntigo.getBfcExcecaoDependenciaFim() == null && beneficiario.getBfcExcecaoDependenciaFim() != null) || (beneficiarioAntigo.getBfcExcecaoDependenciaFim() != null && beneficiario.getBfcExcecaoDependenciaFim() == null) || ((beneficiarioAntigo.getBfcExcecaoDependenciaFim() != null && beneficiario.getBfcExcecaoDependenciaFim() != null) && (beneficiario.getBfcExcecaoDependenciaFim().compareTo(beneficiarioAntigo.getBfcExcecaoDependenciaFim())) != 0)) {
                log.addChangedField(Columns.BFC_EXCECAO_DEPENDENCIA_FIM, beneficiario.getBfcExcecaoDependenciaFim());
                teveAlteracao = true;
            }
            if ((beneficiarioAntigo.getBfcSubsidioConcedido() == null && beneficiario.getBfcSubsidioConcedido() != null) || (beneficiarioAntigo.getBfcSubsidioConcedido() != null && beneficiario.getBfcSubsidioConcedido() == null) || ((beneficiarioAntigo.getBfcSubsidioConcedido() != null && beneficiario.getBfcSubsidioConcedido() != null) && (beneficiario.getBfcSubsidioConcedido().compareTo(beneficiarioAntigo.getBfcSubsidioConcedido())) != 0)) {
                log.addChangedField(Columns.BFC_SUBSIDIO_CONCEDIDO, beneficiario.getBfcSubsidioConcedido());
                teveAlteracao = true;
            }
            if ((beneficiarioAntigo.getBfcSubsidioConcedidoMotivo() == null && beneficiario.getBfcSubsidioConcedidoMotivo() != null) || (beneficiarioAntigo.getBfcSubsidioConcedidoMotivo() != null && beneficiario.getBfcSubsidioConcedidoMotivo() == null) || ((beneficiarioAntigo.getBfcSubsidioConcedidoMotivo() != null && beneficiario.getBfcSubsidioConcedidoMotivo() != null) && (beneficiario.getBfcSubsidioConcedidoMotivo().compareTo(beneficiarioAntigo.getBfcSubsidioConcedidoMotivo())) != 0)) {
                log.addChangedField(Columns.BFC_SUBSIDIO_CONCEDIDO_MOTIVO, beneficiario.getBfcSubsidioConcedidoMotivo());
                teveAlteracao = true;
            }
            if ((beneficiarioAntigo.getStatusBeneficiario() == null && beneficiario.getStatusBeneficiario() != null) || (beneficiarioAntigo.getStatusBeneficiario() != null && beneficiario.getStatusBeneficiario() == null) || ((beneficiarioAntigo.getStatusBeneficiario() != null && beneficiario.getStatusBeneficiario() != null) && (beneficiario.getStatusBeneficiario().getSbeCodigo().compareTo(beneficiarioAntigo.getStatusBeneficiario().getSbeCodigo()) != 0))) {
                log.addChangedField(Columns.BFC_SBE_CODIGO, beneficiario.getStatusBeneficiario().getSbeCodigo());
                teveAlteracao = true;
            }
            if ((beneficiarioAntigo.getBfcDataCasamento() == null && beneficiario.getBfcDataCasamento() != null) || (beneficiarioAntigo.getBfcDataCasamento() != null && beneficiario.getBfcDataCasamento() == null) || ((beneficiarioAntigo.getBfcDataCasamento() != null && beneficiario.getBfcDataCasamento() != null) && (beneficiario.getBfcDataCasamento().compareTo(beneficiarioAntigo.getBfcDataCasamento()) != 0))) {
                log.addChangedField(Columns.BFC_DATA_CASAMENTO, beneficiario.getBfcDataCasamento());
                teveAlteracao = true;
            }
            if ((beneficiarioAntigo.getBfcDataObito() == null && beneficiario.getBfcDataObito() != null) || (beneficiarioAntigo.getBfcDataObito() != null && beneficiario.getBfcDataObito() == null) || ((beneficiarioAntigo.getBfcDataObito() != null && beneficiario.getBfcDataObito() != null) && (beneficiario.getBfcDataObito().compareTo(beneficiarioAntigo.getBfcDataObito()) != 0))) {
                log.addChangedField(Columns.BFC_DATA_OBITO, beneficiario.getBfcDataObito());
                teveAlteracao = true;
            }
            if ((beneficiarioAntigo.getBfcIdentificador() == null && beneficiario.getBfcIdentificador() != null) || (beneficiarioAntigo.getBfcIdentificador() != null && beneficiario.getBfcIdentificador() == null) || ((beneficiarioAntigo.getBfcIdentificador() != null && beneficiario.getBfcIdentificador() != null) && (beneficiario.getBfcIdentificador().compareTo(beneficiarioAntigo.getBfcIdentificador()) != 0))) {
                log.addChangedField(Columns.BFC_IDENTIFICADOR, beneficiario.getBfcIdentificador());
                teveAlteracao = true;
            }

            if ((beneficiarioAntigo.getBfcClassificacao() == null && beneficiario.getBfcClassificacao() != null) || (beneficiarioAntigo.getBfcClassificacao() != null && beneficiario.getBfcClassificacao() == null) || ((beneficiarioAntigo.getBfcClassificacao() != null && beneficiario.getBfcClassificacao() != null) && (beneficiario.getBfcClassificacao().compareTo(beneficiarioAntigo.getBfcClassificacao()) != 0))) {
                log.addChangedField(Columns.BFC_CLASSIFICACAO, beneficiario.getBfcClassificacao());
                teveAlteracao = true;
            }

            if (teveAlteracao) {
                BeneficiarioHome.update(beneficiario);

                log.write();

                // Criando ocorrência de alteração de beneficiário
                TipoOcorrencia tipoOcorrencia = new TipoOcorrencia();
                tipoOcorrencia.setTocCodigo(CodedValues.TOC_ALTERACAO_BENFICIARIO);
                Usuario usuario = new Usuario();
                usuario.setUsuCodigo(responsavel.getUsuCodigo());
                OcorrenciaBeneficiarioHome.create(tipoOcorrencia, usuario, beneficiario, null, null, ApplicationResourcesHelper.getMessage("mensagem.informacao.beneficiario.alteracao", responsavel), responsavel.getIpUsuario());
            }

        } catch (UpdateException | LogControllerException | CreateException | FindException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new BeneficioControllerException(ex);
        }
    }

    /**
     * Acresentando as regras para validação da regra de negocio.
     * @param beneficiario
     * @param responsavel
     * @throws BeneficioControllerException
     */
    private void analiseRegrasNegocioBeneficiario(Beneficiario beneficiario, AcessoSistema responsavel) throws BeneficioControllerException {
        Date agora = new Date(System.currentTimeMillis());

        if (beneficiario.getBfcDataObito() != null && beneficiario.getBfcDataObito().after(agora)) {
            throw new BeneficioControllerException("mensagem.erro.data.obito.data.futura", responsavel);
        }

        if (beneficiario.getBfcDataNascimento() != null && beneficiario.getBfcDataNascimento().after(agora)) {
            throw new BeneficioControllerException("mensagem.erro.data.nascimento.data.futura", responsavel);
        }

        if (beneficiario.getBfcDataCasamento() != null && beneficiario.getBfcDataCasamento().after(agora)) {
            throw new BeneficioControllerException("mensagem.erro.data.casamento.data.futura", responsavel);
        }

        if(beneficiario.getGrauParentesco() != null
                && !GrauParentescoEnum.permiteEdicaoDataCasamento(beneficiario.getGrauParentesco().getGrpCodigo())
                && beneficiario.getBfcDataCasamento() != null) {
            throw new BeneficioControllerException("mensagem.erro.data.casamento.invalida.para.grau.parentesco.selecionado", responsavel);
        }

        if(beneficiario.getGrauParentesco() != null
                && GrauParentescoEnum.permiteEdicaoDataCasamento(beneficiario.getGrauParentesco().getGrpCodigo())
                && beneficiario.getBfcDataCasamento() == null) {
            throw new BeneficioControllerException("mensagem.erro.data.casamento.nao.informada.para.grau.parentesco.selecionado", responsavel);
        }
    }

    @Override
    public void remove(Beneficiario beneficiario, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            beneficiario.setStatusBeneficiario(new StatusBeneficiario(StatusBeneficiarioEnum.EXCLUIDO.sbeCodigo));
            BeneficiarioHome.update(beneficiario);
            LogDelegate log = new LogDelegate(responsavel, Log.BENEFICIARIO, Log.DELETE, Log.LOG_INFORMACAO);
            log.write();

            // Criando ocorrência de exclusão de beneficiário
            TipoOcorrencia tipoOcorrencia = new TipoOcorrencia();
            tipoOcorrencia.setTocCodigo(CodedValues.TOC_EXCLUSAO_BENEFICIARIO);
            Usuario usuario = new Usuario();
            usuario.setUsuCodigo(responsavel.getUsuCodigo());
            OcorrenciaBeneficiarioHome.create(tipoOcorrencia, usuario, beneficiario, null, null, ApplicationResourcesHelper.getMessage("mensagem.informacao.beneficiario.exclusao", responsavel), responsavel.getIpUsuario());

        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public List<ContratoBeneficio> findContratoBeneficioByBeneficiarioAndTntCodigoAndSadCodigo(String bfcCodigo, List<String> tntCodigo, List<String> sadCodigo, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            return ContratoBeneficioHome.findByBfcCodigoAndTntCodigoAndSadCodigo(bfcCodigo, tntCodigo, sadCodigo);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public int listarCountAnexosBeneficiario(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            ListarAnexoBeneficiariosQuery query = new ListarAnexoBeneficiariosQuery();

            if (criterio != null) {
                query.bfcCodigo = (String) criterio.getAttribute(Columns.BFC_CODIGO);
            }

            query.count = true;

            return query.executarContador();
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listarAnexosBeneficiario(CustomTransferObject criterio, int offset, int size, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            ListarAnexoBeneficiariosQuery query = new ListarAnexoBeneficiariosQuery();

            if (criterio != null) {
                query.bfcCodigo = (String) criterio.getAttribute(Columns.BFC_CODIGO);
            }

            query.count = false;
            query.firstResult = offset;
            query.maxResults = size;

            return query.executarDTO();
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public AnexoBeneficiario findAnexoBeneficiarioByPrimaryKey(AnexoBeneficiarioId id, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            return AnexoBeneficiarioHome.findByAnexoBeneficiarioId(id);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public void createAnexoBeneficiario(AnexoBeneficiario anexo, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            AnexoBeneficiarioHome.create(anexo.getBeneficiario(), anexo.getUsuario(), anexo.getTipoArquivo(), anexo.getAbfNome(), anexo.getAbfDescricao(), anexo.getAbfAtivo(), anexo.getAbfData(), anexo.getAbfDataValidade(), anexo.getAbfIpAcesso());
            LogDelegate log = new LogDelegate(responsavel, Log.ANEXO_BENEFICIARIO, Log.CREATE, Log.LOG_INFORMACAO);
            log.setFuncao(responsavel.getFunCodigo());
            log.write();
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public void removeAnexoBeneficiario(AnexoBeneficiario anexo, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            AnexoBeneficiarioHome.remove(anexo);
            LogDelegate log = new LogDelegate(responsavel, Log.ANEXO_BENEFICIARIO, Log.DELETE, Log.LOG_INFORMACAO);
            log.setFuncao(responsavel.getFunCodigo());
            log.write();
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public void updateAnexoBeneficiario(AnexoBeneficiario anexo, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            AnexoBeneficiarioHome.update(anexo);
            LogDelegate log = new LogDelegate(responsavel, Log.ANEXO_BENEFICIARIO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setFuncao(responsavel.getFunCodigo());
            log.write();
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new BeneficioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listarCountBeneficiosPorBeneficiarios(TransferObject criterio, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            ListarCountBeneficiosPorBeneficiariosQuery query = new ListarCountBeneficiosPorBeneficiariosQuery();

            if (criterio != null) {
                query.rseCodigo = (String) criterio.getAttribute(Columns.RSE_CODIGO);
            }

            return query.executarDTO();
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new BeneficioControllerException(ex);
        }
    }
    @Override
    public String importaBeneficiariosDependentes(String arquivoEntrada, AcessoSistema responsavel) throws BeneficioControllerException {
        if (!ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, CodedValues.TPC_SIM, responsavel)) {
            throw new BeneficioControllerException("mensagem.erro.sistema.arquivos.importacao.beneficiarios.dependentes.param.nao.habilitado", responsavel);
        }
        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        String pathArqBeneficiarioDependente = absolutePath + File.separatorChar + "beneficiarios" + File.separatorChar + "cse" + File.separatorChar;

        // Verifica se o caminho existe existe
        File dir = new java.io.File(pathArqBeneficiarioDependente);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new BeneficioControllerException("mensagem.erro.criacao.diretorio", responsavel);
        }

        //Recupera parâmetros de configuração do sistema
        String pathConf = absolutePath + File.separatorChar + "conf" + File.separatorChar;

        // Recupera layout de importação do saldo devedor
        String entradaImpArqXml = pathConf + "imp_beneficiario_dependente_entrada.xml";
        String tradutorImpArqXml = pathConf + "imp_beneficiario_dependente_tradutor.xml";

        File arqConfEntradaDefault = new File(entradaImpArqXml);
        File arqConfTradutorDefault = new File(tradutorImpArqXml);
        if (!arqConfEntradaDefault.exists() || !arqConfTradutorDefault.exists()) {
            throw new BeneficioControllerException("mensagem.erro.sistema.arquivos.importacao.beneficiarios.dependentes.conf", responsavel);
        }

        String fileName = pathArqBeneficiarioDependente + arquivoEntrada;

        // Verifica se o arquivo existe
        File arqEntrada = new File(fileName);
        if (!arqEntrada.exists()) {
            throw new BeneficioControllerException("mensagem.erro.arquivo.nao.encontrado", responsavel, arquivoEntrada);
        }

        // Renomeia o arquivo antes de iniciar a importação
        FileHelper.rename(fileName, fileName + ".prc");
        fileName += ".prc";

        LeitorArquivoTexto leitor;
        Escritor escritor;
        Tradutor tradutor;

        // Configura o leitor de acordo com o arquivo de entrada
        if (fileName.toLowerCase().endsWith(".zip") || fileName.toLowerCase().endsWith(".zip.prc")) {
            LOG.debug("nome do arquivo ... " + fileName);
            leitor = new LeitorArquivoTextoZip(entradaImpArqXml, fileName);
        } else {
            LOG.debug("nome do arquivo ... " + fileName);
            leitor = new LeitorArquivoTexto(entradaImpArqXml, fileName);
        }

        // Hash que recebe os dados do que serão lidos do arquivo de entrada
        HashMap<String, Object> entrada = new HashMap<>();

        // Escritor e tradutor
        escritor = new EscritorMemoria(entrada);
        tradutor = new Tradutor(tradutorImpArqXml, leitor, escritor);

        // Processamento das linhas do arquivo.
        String delimitador = leitor.getDelimitador() == null ? "" : leitor.getDelimitador();
        List<String> critica = new ArrayList<>();

        try {
            tradutor.iniciaTraducao(true);
        } catch (ParserException ex) {
            LOG.error("Erro em iniciar tradução.", ex);
            throw new BeneficioControllerException(ex);
        }

        boolean proximo = true;
        try {
            BeneficiarioDAO beneficiarioDAO = DAOFactory.getDAOFactory().getBeneficiarioDAO();
            beneficiarioDAO.criaTabelaImportaBeneficiarios();
            beneficiarioDAO.iniciarCargaBeneficiarios();
            // Faz o loop de cada linha do arquivo para realizar as traduções
            while (proximo) {
                try {
                    proximo = tradutor.traduzProximo();
                    if (!proximo) {
                        break;
                    }

                    if (entrada.get("LINHA_INVALIDA") == null || entrada.get("LINHA_INVALIDA").toString().equals("N")) {
                        // Realiza a validação de segurança contra ataque de XSS nos campos do lote
                        for (String key : entrada.keySet()) {
                            Object value = entrada.get(key);
                            if (value instanceof String) {
                                // Se for String, realiza o tratamento anti-XSS
                                entrada.put(key, XSSPreventionFilter.stripXSS((String) value));
                            }
                        }

                        String tibCodigo = (String) entrada.get("TIB_CODIGO");
                        String mdeCodigo = (String) entrada.get("MDE_CODIGO");
                        String serCpf = (String) entrada.get("SER_CPF");
                        String bfcIdentificador = (String) entrada.get("BFC_IDENTIFICADOR");
                        String bfcNome = (String) entrada.get("BFC_NOME");
                        String bfcCpf = (String) entrada.get("BFC_CPF");
                        String bfcRg = (String) entrada.get("BFC_RG");
                        String bfcSexo = !TextHelper.isNull(entrada.get("BFC_SEXO")) ? entrada.get("BFC_SEXO").toString() : "";
                        String bfcTelefone = (String) entrada.get("BFC_TELEFONE");
                        String bfcNomeMae = (String) entrada.get("BFC_NOME_MAE");
                        String bfcDataNascimento = (String) entrada.get("BFC_DATA_NASCIMENTO");
                        String bfcEstadoCivil = !TextHelper.isNull(entrada.get("BFC_ESTADO_CIVIL")) ? entrada.get("BFC_ESTADO_CIVIL").toString() : "";
                        String bfcCelular = (String) entrada.get("BFC_CELULAR");
                        String grpCodigo = (String) entrada.get("GRP_CODIGO");
                        String nacCodigo = (String) entrada.get("NAC_CODIGO");
                        String bfcDataCasamento = (String) entrada.get("BFC_DATA_CASAMENTO");
                        String rseMatricula = (String) entrada.get("RSE_MATRICULA");
                        String bfcClassificacao = (String) entrada.get("BFC_CLASSIFICACAO");

                        if (TextHelper.isNull(serCpf) && TextHelper.isNull(bfcIdentificador)) {
                            LOG.debug("O ser_cpf e o bfc_identificador são obrigatório para o cadastro");
                            throw new BeneficioControllerException("mensagem.informe.ser.cpf.e.bfc.identificador", responsavel);
                        }

                        List<Servidor> lstServidor = ServidorHome.findByCPF(serCpf);

                        if (lstServidor == null || lstServidor.isEmpty()) {
                            throw new BeneficioControllerException("mensagem.nenhumServidorEncontrado", responsavel);
                        } else if (lstServidor.size() > 1) {
                            throw new BeneficioControllerException("mensagem.multiplosServidoresEncontrados", responsavel);
                        }

                        Servidor servidor = lstServidor.get(0);

                        TransferObject registroServidor = null;
                        String rseCodigo = null;

                        if (!TextHelper.isNull(rseMatricula)) {
                            registroServidor = servidorController.getRegistroServidorPelaMatricula(servidor.getSerCodigo(), null, null, rseMatricula, responsavel);
                        }

                        if (!TextHelper.isNull(rseMatricula) && registroServidor == null ) {
                            LOG.debug("Não foi encontrado o registro servidor para a matrícula informada: "+ rseMatricula);
                            throw new BeneficioControllerException("mensagem.erro.registro.servidor.nao.encontrado.beneficiarios.dependentes", responsavel);
                        } else if (registroServidor != null) {
                            rseCodigo = (String) registroServidor.getAttribute(Columns.RSE_CODIGO);
                        }

                        if (!TextHelper.isNull(bfcClassificacao) && !CodedValues.BFC_CLASSIFICACOES.stream().anyMatch(bfcClassificacao::contains)) {
                            LOG.debug("A classificação do beneficiário informada não possui mapeamento: "+ bfcClassificacao);
                            throw new BeneficioControllerException("mensagem.erro.bfc.classificacao.nao.encontrado.beneficiarios.dependentes", responsavel);
                        }

                        TipoBeneficiario tipoBeneficiario = new TipoBeneficiario();

                        GrauParentesco grauParentesco = null;
                        StatusBeneficiario statusBeneficiario = new StatusBeneficiario();
                        statusBeneficiario.setSbeCodigo(StatusBeneficiarioEnum.ATIVO.sbeCodigo);

                        MotivoDependencia motivoDependencia = null;
                        Nacionalidade nacionalidade = null;

                        if (!TextHelper.isNull(tibCodigo)) {
                            tipoBeneficiario = TipoBeneficiarioHome.findByPrimaryKey(tibCodigo);
                        } else {
                            tipoBeneficiario.setTibCodigo(TipoBeneficiarioEnum.DEPENDENTE.tibCodigo);
                        }

                        if (!TextHelper.isNull(mdeCodigo)) {
                            motivoDependencia = MotivoDependenciaHome.findByPrimaryKey(mdeCodigo);
                        }

                        if (!TextHelper.isNull(grpCodigo)) {
                            grauParentesco = GrauParentescoHome.findByPrimaryKey(grpCodigo);
                        }

                        if (!TextHelper.isNull(nacCodigo)) {
                            nacionalidade = NacionalidadeHome.findByPrimaryKey(nacCodigo);
                        }

                        Beneficiario beneficiario = new Beneficiario();

                        if (!TextHelper.isNull(bfcCpf) && TextHelper.isNull(rseCodigo)) {
                            beneficiario = BeneficiarioHome.findByCpfEServidor(bfcCpf, servidor.getSerCodigo());
                        } else if (!TextHelper.isNull(bfcIdentificador) && TextHelper.isNull(rseCodigo)) {
                            beneficiario = BeneficiarioHome.findByIdentificadorEServidor(bfcIdentificador, servidor.getSerCodigo());
                        } else if (!TextHelper.isNull(bfcCpf) && !TextHelper.isNull(rseCodigo)) {
                            beneficiario = BeneficiarioHome.findByCpfEServidorERseCodigo(bfcCpf, servidor.getSerCodigo(), rseCodigo);
                        } else if (!TextHelper.isNull(bfcIdentificador) && !TextHelper.isNull(rseCodigo)) {
                            beneficiario = BeneficiarioHome.findByIdentificadorEServidorERseCodigo(bfcIdentificador, servidor.getSerCodigo(), rseCodigo);
                        }

                        if (!TextHelper.isNull(beneficiario)) {
                            beneficiario.setTipoBeneficiario(tipoBeneficiario);
                            beneficiario.setMotivoDependencia(motivoDependencia);
                            beneficiario.setGrauParentesco(grauParentesco);
                            beneficiario.setNacionalidade(nacionalidade);
                            beneficiario.setBfcNome(bfcNome);
                            beneficiario.setBfcRg(bfcRg);
                            beneficiario.setBfcSexo(bfcSexo);
                            beneficiario.setBfcTelefone(bfcTelefone);
                            beneficiario.setBfcCelular(bfcCelular);
                            beneficiario.setBfcNomeMae(bfcNomeMae);
                            beneficiario.setBfcDataNascimento(!TextHelper.isNull(bfcDataNascimento) ? DateHelper.parse(bfcDataNascimento, "yyyy-MM-dd") : null);
                            beneficiario.setBfcEstadoCivil(bfcEstadoCivil);
                            beneficiario.setBfcDataCasamento(!TextHelper.isNull(bfcDataCasamento) ? DateHelper.parse(bfcDataCasamento, "yyyy-MM-dd") : null);
                            beneficiario.setBfcIdentificador(bfcIdentificador);
                            beneficiario.setStatusBeneficiario(statusBeneficiario);
                            beneficiario.setBfcClassificacao(bfcClassificacao);
                            update(beneficiario, responsavel);
                        } else {
                            create(servidor, tipoBeneficiario, motivoDependencia, Short.MAX_VALUE, bfcNome, bfcCpf, bfcRg, bfcSexo, bfcTelefone, bfcCelular, bfcNomeMae, grauParentesco, !TextHelper.isNull(bfcDataNascimento) ? DateHelper.parse(bfcDataNascimento, "yyyy-MM-dd") : null, bfcEstadoCivil, null, null, null, null, statusBeneficiario, nacionalidade, !TextHelper.isNull(bfcDataCasamento) ? DateHelper.parse(bfcDataCasamento, "yyyy-MM-dd") : null, null, bfcIdentificador, rseCodigo, bfcClassificacao, responsavel);
                        }
                        beneficiarioDAO.incluiBeneficiarios(arquivoEntrada, servidor.getSerCodigo(), bfcCpf, bfcIdentificador, rseCodigo);
                    }
                } catch (BeneficioControllerException | ParserException | NumberFormatException | ParseException | FindException | ServidorControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    StringBuilder mensagem = new StringBuilder(ex.getMessage());
                    critica.add(gerarLinhaArquivoSaida(leitor.getLinha(), delimitador, mensagem.toString()));
                }
            }

            beneficiarioDAO.encerrarCargaBeneficiarios();
            beneficiarioDAO.excluiBeneficiarios();
        } catch (DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new BeneficioControllerException(ex);
        } finally {
            try {
                tradutor.encerraTraducao();
            } catch (ParserException ex) {
                LOG.error(ex.getMessage());
            }
        }

        //Em caso de problemas no processamento, criar arquivo de crítica com as linhas que não foram processadas.
        String nomeArqSaida;
        String nomeArqSaidaTxt;
        String nomeArqSaidaZip;
        try {
            if (!critica.isEmpty()) {
                // Grava arquivo contendo as parcelas não encontradas no sistema
                LOG.debug("ARQUIVOS CRITICA: " + DateHelper.getSystemDatetime());

                nomeArqSaida = pathArqBeneficiarioDependente + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", responsavel);

                nomeArqSaida += arquivoEntrada + "_" + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss");
                nomeArqSaidaTxt = nomeArqSaida + ".txt";
                PrintWriter arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaidaTxt)));
                LOG.debug("nomeArqSaidaTxt: " + nomeArqSaidaTxt);

                if (leitor.getLinhaHeader() != null && !leitor.getLinhaHeader().trim().equals("")) {
                    // Imprime a linha de header no arquivo
                    arqSaida.println(gerarLinhaArquivoSaida(leitor.getLinhaHeader(), delimitador, null));
                }
                // Imprime as linhas de critica no arquivo
                arqSaida.println(TextHelper.join(critica, System.lineSeparator()));
                if (leitor.getLinhaFooter() != null && !leitor.getLinhaFooter().trim().equals("")) {
                    // Imprime a linha de footer no arquivo
                    arqSaida.println(gerarLinhaArquivoSaida(leitor.getLinhaFooter(), delimitador, null));
                }
                arqSaida.close();

                LOG.debug("FIM ARQUIVOS CRITICA: " + DateHelper.getSystemDatetime());
                // Compacta os arquvivos gerados em apenas um
                LOG.debug("compacta os arquivos: " + DateHelper.getSystemDatetime());
                nomeArqSaidaZip = nomeArqSaida + ".zip";
                FileHelper.zip(nomeArqSaidaTxt, nomeArqSaidaZip);
                LOG.debug("fim - compacta os arquivos: " + DateHelper.getSystemDatetime());
                FileHelper.delete(nomeArqSaidaTxt);

                return nomeArqSaidaZip;
            }
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new BeneficioControllerException(ex);
        }

        LOG.debug("FIM IMPORTACAO: " + DateHelper.getSystemDatetime());

        return null;
    }

    private static String formataMsgErro(String mensagem, String complemento, int tamanho, boolean alinhaEsquerda) {
        mensagem = (mensagem == null ? "" : mensagem);
        return TextHelper.removeAccent(TextHelper.formataMensagem(mensagem, complemento, tamanho, alinhaEsquerda)).toUpperCase();
    }

    private static String gerarLinhaArquivoSaida(String linha, String delimitador, String mensagem) {
        // Concatena a mensagem de erro no final da linha de entrada
        mensagem = (mensagem == null ? "" : mensagem);
        return (linha + delimitador + formataMsgErro(mensagem, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
    }

    @Override
    public Beneficiario buscaBeneficiarioSerRseIdentificacdor(String serCodigo, String rseCodigo, String bfcIdentificador, AcessoSistema resposavel) throws BeneficioControllerException {
        Beneficiario beneficiario = null;
        try {
            if(!TextHelper.isNull(serCodigo) && !TextHelper.isNull(rseCodigo) && !TextHelper.isNull(bfcIdentificador)) {
                    beneficiario = BeneficiarioHome.findByIdentificadorEServidorERseCodigo(bfcIdentificador, serCodigo, rseCodigo);
            }
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new BeneficioControllerException(ex);
        }
        return beneficiario;
    }

    @Override
    public Beneficiario buscaBeneficiarioAdeCodigo(String adeCodigo, AcessoSistema responsavel) throws BeneficioControllerException {
        Beneficiario beneficiario = null;
        try {
            if (!TextHelper.isNull(adeCodigo)) {
                    beneficiario = BeneficiarioHome.findDetachedByAdeCodigo(adeCodigo, responsavel);
            }
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new BeneficioControllerException(ex);
        }
        return beneficiario;
    }

    @Override
    public Beneficiario buscaBeneficiarioBfcCodigo(String bfcCodigo) throws BeneficioControllerException {
        Beneficiario beneficiario = null;
        try {
            if( !TextHelper.isNull(bfcCodigo)) {
                beneficiario = BeneficiarioHome.findByPrimaryKey(bfcCodigo);
            }
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new BeneficioControllerException(ex);
        }
        return beneficiario;
    }
}
