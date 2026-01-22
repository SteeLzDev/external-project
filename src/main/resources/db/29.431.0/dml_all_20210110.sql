-- DESENV-14097
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('801', 'Quantidade de dias após exclusão de servidor para arquivamento na importação de margem', 'INT', '0', 'N', 'N', 'N', 'N', NULL);

-- INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) VALUES ('801', '1', '0'); 

DELETE FROM tb_texto_sistema WHERE TEX_CHAVE IN ('mensagem.erro.cadMargem.arquivo.xml.configuracao.saida.carga.margem.fora.padrao', 'mensagem.erro.cadMargem.arquivo.xml.modificar.arquivo.xml.saida.carga.margem.validacao', 'mensagem.erro.cadMargem.arquivo.xml.configuracao.saida.carga.margem.nao.encontrado', 'mensagem.erro.cadMargem.localizar.datasource.validacao.margem');
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE LIKE 'tb_servidor_validacao%';
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE LIKE 'tb_registro_servidor_validacao%';
