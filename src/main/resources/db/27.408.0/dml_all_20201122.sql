-- DESENV-14405
INSERT INTO tb_tipo_param_svc (TPS_CODIGO, TPS_DESCRICAO, TPS_CSE_ALTERA, TPS_CSA_ALTERA, TPS_SUP_ALTERA)
VALUES ('291', 'Bloqueia inclusão via lote de servidores de categorias específicas', 'N', 'N', 'N');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('796', 'Expressão regular para bloquear inclusão via lote de servidores nas categorias definidas', 'ALFA', NULL, 'N', 'N', 'N', 'N', '1');
