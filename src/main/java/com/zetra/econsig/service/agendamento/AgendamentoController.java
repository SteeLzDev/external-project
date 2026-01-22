package com.zetra.econsig.service.agendamento;


import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AgendamentoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: AgendamentoController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface AgendamentoController {

    /**
     * Insere um agendamento e seus parâmetros caso existam.
     * Se o objeto de parâmetro informado for nulo ou vazio, nenhum parâmetro será adicionado.
     * Insere uma ocorrência de inclusão de agendamento.
     *
     * @param transferObject Valores para inclusão do agendamento
     * @param parametros Parâmetros do agendamento
     * @param periodicidade Quantida de vezes que o agendamento será agendado. Se a periodicidade for 5, para o tipo de agendamento diário serão incluídos agendamentos para os próximos 5 dias a partir da data prevista de execução, para o semanal serão inseridos para as próximas 5 semanas e para o mensal pelos próximos 5 meses.
     * @param responsavel Responsável pela operação
     * @throws AgendamentoControllerException
     */
    public void insereAgendamento(TransferObject transferObject, Map<String, List<String>> parametros, int periodicidade, AcessoSistema responsavel) throws AgendamentoControllerException;

    /**
     * Cancela o agendamento informado.
     *
     * @param agdCodigo Código do agendamento que deve ser cancelado.
     * @param responsavel Responsável pela operação
     * @throws AgendamentoControllerException
     */
    public void cancelaAgendamento(String agdCodigo, AcessoSistema responsavel) throws AgendamentoControllerException;

    /**
     * Conclui o agendamento informado.
     *
     * @param agdCodigo Código do agendamento que deve ser concluído.
     * @param dataInicio Data de inicio da conclusão do agendamento, caso seja informado valor nulo, será setada a data atual do sistema.
     * @param dataFim Data fim da conclusão do agendamento, caso seja informado valor nulo, será setada a data atual do sistema.
     * @param responsavel Responsável pela operação
     * @throws AgendamentoControllerException
     */
    public void concluiAgendamento(String agdCodigo, Date dataInicio, Date dataFim, AcessoSistema responsavel) throws AgendamentoControllerException;

    /**
     * Retorna a quantidade de agendamentos encontrados de acordo com os filtros informados.
     *
     * @param agdCodigos Códigos dos agendamentos que devem ser retornados
     * @param sagCodigos Status dos agendamentos que devem ser retornados
     * @param tagCodigos Tipos dos agendamentos que devem ser retornados
     * @param classe Classe agendada dos agendamentos que devem ser retornados
     * @param tipoEntidade Tipo da entidade do usuário, pelo qual os agendamentos devem ser filtrados
     * @param codigoEntidade Código da entidade do usuário, pelo qual os agendamentos devem ser filtrados
     * @param usuCodigo Código do usuário dos agendamentos que devem ser retornados
     * @param relCodigo Código do relatório agendado
     * @param responsavel Responsável pela operação
     * @return Retorna uma lista com os agendamentos encontrados.
     * @throws AgendamentoControllerException
     */
    public int countAgendamentos(List<String> agdCodigos, List<String> sagCodigos, List<String> tagCodigos, String classe, String tipoEntidade, String codigoEntidade, String usuCodigo, String relCodigo, AcessoSistema responsavel) throws AgendamentoControllerException;

    /**
     * Lista os agendamentos encontrados de acordo com os filtros informados.
     *
     * @param agdCodigos Códigos dos agendamentos que devem ser retornados
     * @param sagCodigos Status dos agendamentos que devem ser retornados
     * @param tagCodigos Tipos dos agendamentos que devem ser retornados
     * @param classe Classe agendada dos agendamentos que devem ser retornados
     * @param tipoEntidade Tipo da entidade do usuário, pelo qual os agendamentos devem ser filtrados
     * @param codigoEntidade Código da entidade do usuário, pelo qual os agendamentos devem ser filtrados
     * @param usuCodigo Código do usuário dos agendamentos que devem ser retornados
     * @param relCodigo Código do relatório agendado
     * @param responsavel Responsável pela operação
     * @return Retorna uma lista com os agendamentos encontrados.
     * @throws AgendamentoControllerException
     */
    public List<TransferObject> lstAgendamentos(List<String> agdCodigos, List<String> sagCodigos, List<String> tagCodigos, String classe, String tipoEntidade, String codigoEntidade, String usuCodigo, String relCodigo, AcessoSistema responsavel) throws AgendamentoControllerException;

    /**
     * Lista os agendamentos encontrados de acordo com os filtros informados.
     *
     * @param agdCodigos Códigos dos agendamentos que devem ser retornados
     * @param sagCodigos Status dos agendamentos que devem ser retornados
     * @param tagCodigos Tipos dos agendamentos que devem ser retornados
     * @param classe Classe agendada dos agendamentos que devem ser retornados
     * @param tipoEntidade Tipo da entidade do usuário, pelo qual os agendamentos devem ser filtrados
     * @param codigoEntidade Código da entidade do usuário, pelo qual os agendamentos devem ser filtrados
     * @param usuCodigo Código do usuário dos agendamentos que devem ser retornados
     * @param relCodigo Código do relatório agendado
     * @param offset Primeiro resultado que deverá ser retornado.
     * @param count Quantidade de resultados que deverão ser retornados.
     * @param responsavel Responsável pela operação
     * @return Retorna uma lista com os agendamentos encontrados.
     * @throws AgendamentoControllerException
     */
    public List<TransferObject> lstAgendamentos(List<String> agdCodigos, List<String> sagCodigos, List<String> tagCodigos, String classe, String tipoEntidade, String codigoEntidade, String usuCodigo, String relCodigo, int offset, int count, AcessoSistema responsavel) throws AgendamentoControllerException;

    /**
     * Recupera todos os agendamentos que não foram executados no dia para execução.
     *
     * @param responsavel Responsável pela operação.
     * @return Retorna todos os agendamentos que não foram executados para execução.
     * @throws AgendamentoControllerException
     */
    public List<TransferObject> lstAgendamentosParaExecucao(AcessoSistema responsavel) throws AgendamentoControllerException;

    /**
     * Recupera todos os agendamentos instantâneos para execução.
     *
     * @param responsavel Responsável pela operação.
     * @return Retorna todos os agendamentos instantâneos para execução.
     * @throws AgendamentoControllerException
     */
    public List<TransferObject> lstAgendamentosInstantaneosParaExecucao(AcessoSistema responsavel) throws AgendamentoControllerException;

    /**
     * Recupera os parâmetros do agendamento.
     *
     * @param agdCodigo Código do agendamento que terá o seus parâmetros recuperados.
     * @param responsavel Responsável pela operação
     * @return Retorna os parâmetros do agendamento.
     * @throws AgendamentoControllerException
     */
    public Map<String, List<String>> lstParametrosAgendamento(String agdCodigo, AcessoSistema responsavel) throws AgendamentoControllerException;

    /**
     * Recupera um agendamento de acordo com o código passado.
     *
     * @param agdCodigo Código do agendamento
     * @param responsavel Responsavel pela operação
     * @return Retorna o agendamento encontrado ou nulo caso não seja encontrado.
     * @throws AgendamentoControllerException
     */
    public TransferObject findAgendamento(String agdCodigo, AcessoSistema responsavel) throws AgendamentoControllerException;

    /**
     * Insere ocorrência para um agendamento.
     *
     * @param agdCodigo Código do agendamento.
     * @param tocCodigo Tipo de ocorrencia de agendamento.
     * @param dataInicio Data de inicio da ocorrência de agendamento, caso seja informado valor nulo, será setada a data atual do sistema.
     * @param dataFim Data fim da ocorrência de agendamento, caso seja informado valor nulo, será setada a data atual do sistema.
     * @param oagObs Observação da ocorrência de agendamento.
     * @param responsavel Responsável pela operação
     * @return Retorna o código da ocorrência inserida.
     * @throws AgendamentoControllerException
     */
    public String insereOcorrencia(String agdCodigo, String tocCodigo, Date dataInicio, Date dataFim, String oagObs, AcessoSistema responsavel) throws AgendamentoControllerException;

    /**
     * Recupera os tipos de agendamento cadastrados.
     *
     * @param tagCodigos Códigos dos tipos de agendamento que serão retornados.
     * @param responsavel Responsável pela operação.
     * @return Retorna uma lista de tipos de agendamento cadastrados.
     * @throws AgendamentoControllerException
     */
    public List<TransferObject> lstTipoAgendamento(List<String> tagCodigos, AcessoSistema responsavel) throws AgendamentoControllerException;

    /**
     * Retorna a quantidade de ocorrências de agendamentos encontrados com erro de execução
     *
     * @param agdCodigos Códigos dos agendamentos que devem ser retornados
     * @param sagCodigos Status dos agendamentos que devem ser retornados
     * @param tagCodigos Tipos dos agendamentos que devem ser retornados
     * @param horasLimite Quantidade de horas limite para execução de agendamentos
     * @param responsavel Responsável pela operação
     * @return Retorna uma lista com os agendamentos encontrados.
     * @throws AgendamentoControllerException
     */
    public int countOcorrenciaAgendamentoComErro(List<String> agdCodigos, List<String> sagCodigos, List<String> tagCodigos, int horasLimite, AcessoSistema responsavel) throws AgendamentoControllerException;

    /**
     * Retorna lista de ocorrência de um agendamento por um determinado período e tipos de ocorrências
     * @param agdCodigo
     * @param dataInicio
     * @param dataFim
     * @param tocCodigos
     * @return
     * @throws AgendamentoControllerException
     */
    public List<String> lstOcorrenciaPorIntervalo(String agdCodigo, Date dataInicio, Date dataFim, List<String> tocCodigos, AcessoSistema responsavel) throws AgendamentoControllerException;

    /**
     * Retorna lista de ocorrência de sucesso de um agendamento por um determinado período e tipos de ocorrências
     * @param agdCodigo
     * @param dataInicio
     * @param dataFim
     * @param responsavel
     * @return
     * @throws AgendamentoControllerException
     */
    public List<String> lstOcorrenciaSucessoPorIntervalo(String agdCodigo, Date dataInicio, Date dataFim, AcessoSistema responsavel) throws AgendamentoControllerException;

    /**
     * Deleta ocorrências no histórico de agendamento por status, tipo agendamento, tipo ocorrencia por determinado quantidade de dias existente
     * @param sagCodigos
     * @param tagCodigos
     * @param tocCodigo
     * @param quantidadeDias
     * @param responsavel
     * @return
     * @throws AgendamentoControllerException
     */
    public void excluiHistoricoOcorrenciaAgendamentoExpiradaBySagCodigoByTagCodigo(List<String> sagCodigos, List<String> tagCodigos, String tocCodigo, int quantidadeDias, AcessoSistema responsavel) throws AgendamentoControllerException;
}
