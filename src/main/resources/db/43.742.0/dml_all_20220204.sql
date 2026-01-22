-- DESENV-17469
UPDATE tb_tipo_param_svc SET TPS_DESCRICAO = 'Dias de vigência para o CET' WHERE TPS_CODIGO = '314';
DELETE FROM tb_texto_sistema WHERE TEX_CHAVE in ('rotulo.param.svc.dia.vingencia.cet', 'mensagem.erro.dias.vingencia.cet.negativo.ou.zero');
