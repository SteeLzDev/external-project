-- DESENV-13515
-- Nova função
INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR)
VALUES ('474', '1', 'Incluir Termo de Garantia de Aluguel', 'N', 'S', 'S', 'N', 'N', 'N', 'N', 'N', 'N');

-- Inserindo função para papel CSE/ORG/SUP:
-- INSERT INTO tb_papel_funcao VALUES ('1','474');
-- INSERT INTO tb_papel_funcao VALUES ('3','474');
-- INSERT INTO tb_papel_funcao VALUES ('7','474');

INSERT INTO tb_item_menu (ITM_CODIGO, MNU_CODIGO, TEX_CHAVE, ITM_CODIGO_PAI, ITM_DESCRICAO, ITM_ATIVO, ITM_SEQUENCIA, ITM_SEPARADOR, ITM_CENTRALIZADOR, ITM_IMAGEM)
VALUES ('235', '1', NULL, NULL, 'Termo de Garantia de Aluguel', 1, 83, 'N', 'S', NULL);

-- Acesso recursos para utilizar métodos da herança
-- iniciar
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15589', '1', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'iniciar', 1, 'S', 'S', '235', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15590', '3', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'iniciar', 1, 'S', 'S', '235', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15591', '7', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'iniciar', 1, 'S', 'S', '235', 'N', '2');

-- validarDigital
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15592', '1', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'validarDigital', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15593', '3', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'validarDigital', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15594', '7', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'validarDigital', 1, 'S', 'S', null, 'N', '2');

-- reservarMargem
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15595', '1', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'reservarMargem', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15596', '3', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'reservarMargem', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15597', '7', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'reservarMargem', 1, 'S', 'S', null, 'N', '2');

-- pesquisarServidor
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15598', '1', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'pesquisarServidor', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15599', '3', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'pesquisarServidor', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15600', '7', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'pesquisarServidor', 1, 'S', 'S', null, 'N', '2');

-- listarServicos
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15601', '1', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'listarServicos', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15602', '3', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'listarServicos', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15603', '7', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'listarServicos', 1, 'S', 'S', null, 'N', '2');

-- listarHistLiquidacoesAntecipadas
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15604', '1', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'listarHistLiquidacoesAntecipadas', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15605', '3', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'listarHistLiquidacoesAntecipadas', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15606', '7', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'listarHistLiquidacoesAntecipadas', 1, 'S', 'S', null, 'N', '2');

-- incluirReserva
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15607', '1', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'incluirReserva', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15608', '3', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'incluirReserva', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15609', '7', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'incluirReserva', 1, 'S', 'S', null, 'N', '2');

-- emitirBoletoExterno
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15610', '1', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'emitirBoletoExterno', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15611', '3', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'emitirBoletoExterno', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15612', '7', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'emitirBoletoExterno', 1, 'S', 'S', null, 'N', '2');

-- emitirBoleto
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15613', '1', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'emitirBoleto', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15614', '3', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'emitirBoleto', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15615', '7', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'emitirBoleto', 1, 'S', 'S', null, 'N', '2');

-- detalharConsignacao
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15616', '1', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'detalharConsignacao', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15617', '3', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'detalharConsignacao', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15618', '7', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'detalharConsignacao', 1, 'S', 'S', null, 'N', '2');

-- confirmarDuplicidade
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15619', '1', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'confirmarDuplicidade', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15620', '3', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'confirmarDuplicidade', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15621', '7', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'confirmarDuplicidade', 1, 'S', 'S', null, 'N', '2');

-- autorizarReserva
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15622', '1', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'autorizarReserva', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15623', '3', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'autorizarReserva', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15624', '7', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'autorizarReserva', 1, 'S', 'S', null, 'N', '2');

-- aceitarTermoAdesao
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15625', '1', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'aceitarTermoAdesao', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15626', '3', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'aceitarTermoAdesao', 1, 'S', 'S', null, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('15627', '7', '474', '/v3/incluirTermoGarantiaAluguel', 'acao', 'aceitarTermoAdesao', 1, 'S', 'S', null, 'N', '2');
