-- DESENV-9468

INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR) 
VALUES ('429', '20', 'Iniciar Processamento de Arquivos de Integração', 'N', 'N', 'S', 'N', 'N', 'N', 'N', 'N', 'N');

UPDATE tb_acesso_recurso SET FUN_CODIGO = '429' WHERE ACR_RECURSO = '/v3/integrarFolha' AND ACR_PARAMETRO = 'acao' AND ACR_OPERACAO = 'processar';

INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) 
SELECT PAP_CODIGO, '429' FROM tb_papel_funcao WHERE FUN_CODIGO = '428';

INSERT INTO tb_funcao_perfil (FUN_CODIGO, PER_CODIGO) 
SELECT '429', PER_CODIGO FROM tb_funcao_perfil WHERE FUN_CODIGO = '428';

INSERT INTO tb_funcao_perfil_cse (CSE_CODIGO, USU_CODIGO, FUN_CODIGO) 
SELECT CSE_CODIGO, USU_CODIGO, '429' FROM tb_funcao_perfil_cse WHERE FUN_CODIGO = '428';

-- chaves a serem recriadas (texto atualizado)
DELETE FROM tb_texto_sistema WHERE tex_chave IN ('mensagem.erro.arquivo.nao.encontrado', 'mensagem.erro.processamento.geracao.historico', 'mensagem.erro.processamento.exportacao.movimento', 'mensagem.sucesso.processamento.exportacao.movimento', 'mensagem.sucesso.processamento.geracao.historico', 'mensagem.sucesso.processamento.retorno');

-- chaves desnecessárias
DELETE FROM tb_texto_sistema WHERE tex_chave IN ('rotulo.integracao.orientada.dashboard.excluir.arquivo.ajuda');

-- --------------------------------------------------------------------------------------------------------------------
-- somente nas bases do euConsigoMais
-- --------------------------------------------------------------------------------------------------------------------

-- Ajusta calculo do período
-- UPDATE tb_param_sist_consignante SET psi_vlr = '31' WHERE tpc_codigo = '133';
-- UPDATE tb_param_sist_consignante SET psi_vlr = '31' WHERE tpc_codigo = '192';

-- Não força o nome a ser enviado.
-- DELETE FROM tb_param_validacao_arq_cse WHERE tva_codigo = '3';
-- DELETE FROM tb_param_validacao_arq_cse WHERE tva_codigo = '15';

-- Não valida a variação da carga de margem
-- UPDATE tb_param_sist_consignante SET psi_vlr = '0.00' WHERE tpc_codigo IN ('241', '242', '396');

-- --------------------------------------------------------------------------------------------------------------------
