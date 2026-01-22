-- DESENV-18810
INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR)
VALUES ('537', '1', 'Iniciar Processo de Credenciamento', 'N', 'N', 'S', 'N', 'N', 'N', 'N', 'N', 'N');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16689', '1', '537', '/v3/manterConsignataria', 'acao', 'iniciarCredenciamentoCsa', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16690', '7', '537', '/v3/manterConsignataria', 'acao', 'iniciarCredenciamentoCsa', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_modelo_email (MEM_CODIGO,MEM_TITULO,MEM_TEXTO) 
VALUES ('enviarEmailDocCredenciamentoCsa', '<@nome_sistema> - <@nome_consignante>: Situacao Credenciamento ', 'Prezada <@csa_nome>,<br> a documentação do seu credenciamento foi <@situacao>, gentileza acessar o sistema.');

