-- DESENV-16082
-- INSERINDO MAIS PAPEIS ALÉM DOS EXISTENTES 
-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('3', '460');
-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('6', '460'); 

-- INCLUSAO DO ACESSO RECURSO PARA OS NOVOS PAPEIS

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16427', '3', '460', '/v3/listarRelatorio', 'tipo', 'historico_desconto_ser', '1', 'S', 'S', '222', 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16428', '3', '460', '/v3/executarRelatorio', 'tipoRelatorio', 'historico_desconto_ser', '1', 'S', 'S', NULL, 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16429', '3', '460', '/v3/excluirArquivo', 'subtipo', 'historico_desconto_ser', '1', 'S', 'S', NULL, 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16430', '3', '460', '/v3/downloadArquivo', 'subtipo', 'historico_desconto_ser', '1', 'S', 'S', NULL, 'N', 2);


INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16431', '6', '460', '/v3/listarRelatorio', 'tipo', 'historico_desconto_ser', '1', 'S', 'S', '222', 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16432', '6', '460', '/v3/executarRelatorio', 'tipoRelatorio', 'historico_desconto_ser', '1', 'S', 'S', NULL, 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16433', '6', '460', '/v3/excluirArquivo', 'subtipo', 'historico_desconto_ser', '1', 'S', 'S', NULL, 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16434', '6', '460', '/v3/downloadArquivo', 'subtipo', 'historico_desconto_ser', '1', 'S', 'S', NULL, 'N', 2);

-- INSERINDO UM NOVO FILTRO

INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO)
VALUES ('campo_ade_numero_lista', 'N. ADE LISTA', '/relatorios/campos_relatorio/campo_ade_numero_lista.jsp', 'N');

-- ADICIONAR OS NOVOS FILTROS

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('historico_desconto_ser', 'campo_status_contrato', '1', '1', '1', '1', '1', 13, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('historico_desconto_ser', 'campo_svc', '1', '1', '1', '1', '1', 6, '', '1');

-- ALTERAR O FILTRO DO RELATÓRIO  

UPDATE tb_relatorio_filtro
SET TFR_CODIGO = 'campo_ade_numero_lista'
WHERE REL_CODIGO = 'historico_desconto_ser' AND TFR_CODIGO = 'campo_ade_numero';

-- ALTERAÇÃO PARA ATENDER OS REQUISITOS

UPDATE tb_relatorio_filtro
SET RFI_EXIBE_CSE = '1', RFI_EXIBE_CSA = '1', RFI_EXIBE_COR = '1', RFI_EXIBE_ORG = '1', RFI_EXIBE_SER = '1', RFI_SEQUENCIA = '1', RFI_EXIBE_SUP = '1'
WHERE REL_CODIGO = 'historico_desconto_ser' AND TFR_CODIGO = 'campo_ade_numero_lista';

UPDATE tb_relatorio_filtro
SET RFI_EXIBE_CSE = '1', RFI_EXIBE_CSA = '0', RFI_EXIBE_COR = '0', RFI_EXIBE_ORG = '0', RFI_EXIBE_SER = '0', RFI_SEQUENCIA = '8', RFI_EXIBE_SUP = '0'
WHERE REL_CODIGO = 'historico_desconto_ser' AND TFR_CODIGO = 'campo_agendado';

UPDATE tb_relatorio_filtro
SET RFI_EXIBE_CSE = '0', RFI_EXIBE_CSA = '1', RFI_EXIBE_COR = '0', RFI_EXIBE_ORG = '0', RFI_EXIBE_SER = '0', RFI_SEQUENCIA = '5', RFI_EXIBE_SUP = '0'
WHERE REL_CODIGO = 'historico_desconto_ser' AND TFR_CODIGO = 'campo_cor';

UPDATE tb_relatorio_filtro
SET RFI_EXIBE_CSE = '1', RFI_EXIBE_CSA = '1', RFI_EXIBE_COR = '1', RFI_EXIBE_ORG = '1', RFI_EXIBE_SER = '0', RFI_SEQUENCIA = '3', RFI_EXIBE_SUP = '1'
WHERE REL_CODIGO = 'historico_desconto_ser' AND TFR_CODIGO = 'campo_cpf';

UPDATE tb_relatorio_filtro
SET RFI_EXIBE_CSE = '1', RFI_EXIBE_CSA = '0', RFI_EXIBE_COR = '0', RFI_EXIBE_ORG = '1', RFI_EXIBE_SER = '0', RFI_SEQUENCIA = '4', RFI_EXIBE_SUP = '1'
WHERE REL_CODIGO = 'historico_desconto_ser' AND TFR_CODIGO = 'campo_csa';


UPDATE tb_relatorio_filtro
SET RFI_EXIBE_CSE = '1', RFI_EXIBE_CSA = '0', RFI_EXIBE_COR = '0', RFI_EXIBE_ORG = '0', RFI_EXIBE_SER = '0', RFI_SEQUENCIA = '9', RFI_EXIBE_SUP = '0'
WHERE REL_CODIGO = 'historico_desconto_ser' AND TFR_CODIGO = 'campo_data_execucao';

UPDATE tb_relatorio_filtro
SET RFI_EXIBE_CSE = '1', RFI_EXIBE_CSA = '0', RFI_EXIBE_COR = '0', RFI_EXIBE_ORG = '0', RFI_EXIBE_SER = '0', RFI_SEQUENCIA = '12', RFI_EXIBE_SUP = '0'
WHERE REL_CODIGO = 'historico_desconto_ser' AND TFR_CODIGO = 'campo_envio_email';

UPDATE tb_relatorio_filtro
SET RFI_EXIBE_CSE = '2', RFI_EXIBE_CSA = '2', RFI_EXIBE_COR = '2', RFI_EXIBE_ORG = '2', RFI_EXIBE_SER = '2', RFI_SEQUENCIA = '14', RFI_EXIBE_SUP = '2'
WHERE REL_CODIGO = 'historico_desconto_ser' AND TFR_CODIGO = 'campo_formato_relatorio';

UPDATE tb_relatorio_filtro
SET RFI_EXIBE_CSE = '1', RFI_EXIBE_CSA = '1', RFI_EXIBE_COR = '1', RFI_EXIBE_ORG = '1', RFI_EXIBE_SER = '0', RFI_SEQUENCIA = '2', RFI_EXIBE_SUP = '1'
WHERE REL_CODIGO = 'historico_desconto_ser' AND TFR_CODIGO = 'campo_matricula';


UPDATE tb_relatorio_filtro
SET RFI_EXIBE_CSE = '1', RFI_EXIBE_CSA = '0', RFI_EXIBE_COR = '0', RFI_EXIBE_ORG = '0', RFI_EXIBE_SER = '0', RFI_SEQUENCIA = '11', RFI_EXIBE_SUP = '0'
WHERE REL_CODIGO = 'historico_desconto_ser' AND TFR_CODIGO = 'campo_periodicidade';

UPDATE tb_relatorio_filtro
SET RFI_EXIBE_CSE = '1', RFI_EXIBE_CSA = '0', RFI_EXIBE_COR = '0', RFI_EXIBE_ORG = '0', RFI_EXIBE_SER = '0', RFI_SEQUENCIA = '10', RFI_EXIBE_SUP = '0'
WHERE REL_CODIGO = 'historico_desconto_ser' AND TFR_CODIGO = 'campo_tipo_agendamento';

UPDATE tb_relatorio_filtro
SET RFI_EXIBE_CSE = '1', RFI_EXIBE_CSA = '1', RFI_EXIBE_COR = '1', RFI_EXIBE_ORG = '1', RFI_EXIBE_SER = '0', RFI_SEQUENCIA = '7', RFI_EXIBE_SUP = '1'
WHERE REL_CODIGO = 'historico_desconto_ser' AND TFR_CODIGO = 'campo_verba';
