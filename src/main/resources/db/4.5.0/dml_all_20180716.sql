-- DESENV-9139

INSERT INTO tb_item_menu (ITM_CODIGO, MNU_CODIGO, ITM_CODIGO_PAI, ITM_DESCRICAO, ITM_ATIVO, ITM_SEQUENCIA, ITM_SEPARADOR)
VALUES ('203', '1', NULL, 'Histórico de Margem', 1, 77, 'N');

-- Versão 4.4.X 
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14858', '6', '146', '/margem/historico.jsp', NULL, NULL, 1, 'S', 'S', '203', 'S', '2');

-- Versão 4.5.X
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/visualizarHistorico', ACR_PARAMETRO = 'acao', ACR_OPERACAO = 'iniciar' WHERE ACR_CODIGO = '14858';

-- Concede permissão aos usuários servidores
-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('6', '146');
-- INSERT INTO tb_funcao_perfil_ser (SER_CODIGO, USU_CODIGO, FUN_CODIGO) SELECT SER_CODIGO, USU_CODIGO, '146' FROM tb_usuario_ser;
