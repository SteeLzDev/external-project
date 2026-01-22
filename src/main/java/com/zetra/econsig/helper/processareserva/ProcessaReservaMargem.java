package com.zetra.econsig.helper.processareserva;

import jakarta.servlet.http.HttpServletRequest;

import com.zetra.econsig.exception.ViewHelperException;

/**
 * <p>Title: ProcessaReservaMargem</p>
 * <p>Description: Interface para criação de classes para acompanhamento,
 * processamento ou validação da reserva de margem, específicos para cada
 * consignante. Um parâmetro de serviço irá determinar qual classe deve
 * ser executada, ou se nenhuma é necessária.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ProcessaReservaMargem {

    /**
     * Retorna uma breve descrição da classe implementadora.
     * Esta descrição poderá ser utilizada futuramente na tela
     * de edição dos parâmetros de serviço. 
     * @return Descrição da classe
     */
    public String getDescricao();

    /**
     * Método executado pelo primeiro passo da reserva de margem.
     * Retorna uma String com códigos HTML's que devem fazer parte
     * da página de reserva.
     * @param request : Requisição de usuário
     * @return Código HTML para inclusão na página de reserva
     */
    public String incluirPasso1(HttpServletRequest request);

    /**
     * Método executado pelo segundo passo da reserva de margem, 
     * para validar o que foi incluido pelo método anterior, 
     * no primeiro passo da reserva. Lança exceção com mensagem
     * específica para o usuário caso a validação falhe.
     * @param request : Requisição de usuário
     * @return TRUE caso a validação tenha sucesso
     * @throws ViewHelperException
     */
    public boolean validarPasso1(HttpServletRequest request) throws ViewHelperException;

    /**
     * Método executado pelo segundo passo da reserva de margem.
     * Retorna uma String com códigos HTML's que devem fazer parte
     * da página de confirmação de reserva.
     * @param request : Requisição de usuário
     * @return Código HTML para inclusão na página de confirmação reserva
     */
    public String incluirPasso2(HttpServletRequest request);

    /**
     * Método executado pelo último passo da reserva de margem, 
     * para validar o que foi incluido pelo método anterior, 
     * no segundo passo da reserva. Lança exceção com mensagem
     * específica para o usuário caso a validação falhe.
     * @param request : Requisição de usuário
     * @return
     * @throws ViewHelperException
     */
    public boolean validarPasso2(HttpServletRequest request) throws ViewHelperException;

    /**
     * Método executado pelo último passo da reserva de margem, 
     * após a inclusão do contrato, executando um pós-processamento 
     * da reserva. Só será executado se o contrato for incluído
     * com sucesso.
     * @param request   : Requisição de usuário
     * @param adeCodigo : Código do novo contrato incluído
     * @return
     * @throws ViewHelperException
     */
    public void finalizar(HttpServletRequest request, String adeCodigo) throws ViewHelperException;
}
