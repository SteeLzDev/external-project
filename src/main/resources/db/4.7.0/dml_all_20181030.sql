-- DESENV-9512
UPDATE tb_funcao SET FUN_DESCRICAO = 'Consultar Contrato de Benefício', GRF_CODIGO = '23' WHERE FUN_CODIGO = '424';

UPDATE tb_item_menu SET ITM_DESCRICAO = 'Consultar Contrato de Benefício' WHERE ITM_CODIGO = '204';

UPDATE tb_menu SET MNU_SEQUENCIA = '5' WHERE mnu_codigo = '4';

UPDATE tb_menu SET MNU_SEQUENCIA = '4' WHERE mnu_codigo = '5';

UPDATE tb_texto_sistema SET TEX_TEXTO = 'Consultar Contrato de Benefício' WHERE TEX_CHAVE = 'rotulo.relacao.beneficios.titulo' AND TEX_TEXTO = 'Relação de Benefícios';

UPDATE tb_texto_sistema SET TEX_TEXTO = 'Rejeitar solicitação' WHERE TEX_CHAVE = 'rotulo.relacao.beneficios.cancelar.solicitacao' AND TEX_TEXTO = 'Cancelar solicitação';

INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR) 
VALUES ('434', '23', 'Consultar Lançamentos de Contratos de Benefícios', 'N', 'N', 'N', 'S', 'N', 'N', 'N', 'N', 'N');

-- INSERT INTO tb_papel_funcao VALUES ('7', '434');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14952', '7', '424', '/v3/relacaoBeneficios', 'acao', 'listar', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14953', '7', '424', '/v3/relacaoBeneficios', 'acao', 'consultar', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14954', '7', '424', '/v3/relacaoBeneficios', 'acao', 'iniciar', 1, 'S', 'S', '204', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14955', '7', '424', '/v3/relacaoBeneficios', 'acao', 'pesquisarServidor', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14956', '7', '424', '/v3/aprovarSolicitacao', 'acao', 'salvar', 1, 'S', 'S', null, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14957', '7', '424', '/v3/aprovarSolicitacao', 'acao', 'aprovar', 1, 'S', 'N', null, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14958', '7', '424', '/v3/aprovarSolicitacao', 'acao', 'cancelar', 1, 'S', 'N', null, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14959', '7', '424', '/v3/aprovarSolicitacao', 'acao', 'rejeitar ', 1, 'S', 'N', null, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14960', '7', '434', '/v3/listarLancamentosContratosBeneficios', 'acao', 'listar ', 1, 'S', 'S', null, 'N', '2');
