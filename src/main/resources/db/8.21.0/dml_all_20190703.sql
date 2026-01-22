-- DESENV-11806
-- Apaga acessos recursos que não são mais utilizados
DELETE FROM tb_acesso_usuario WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE ACR_RECURSO IN ('/login/login_funcionario.jsp', '/login/solicitacao_externa.jsp', '/principal/rodape_servidor.jsp', '/topo/topo_consignante_pb.jsp', '/topo/topo_consignataria_pb.jsp', '/topo/topo_servidor_pb.jsp', '/termo_adesao/termo_adesao.jsp', '/lote/lst_arq_importacao.jsp', '/consignataria/ins_observacao_bloqueio.jsp'));
DELETE FROM tb_ajuda WHERE ACR_CODIGO IN (SELECT ACR_CODIGO FROM tb_acesso_recurso WHERE ACR_RECURSO IN ('/login/login_funcionario.jsp', '/login/solicitacao_externa.jsp', '/principal/rodape_servidor.jsp', '/topo/topo_consignante_pb.jsp', '/topo/topo_consignataria_pb.jsp', '/topo/topo_servidor_pb.jsp', '/termo_adesao/termo_adesao.jsp', '/lote/lst_arq_importacao.jsp', '/consignataria/ins_observacao_bloqueio.jsp'));
DELETE FROM tb_acesso_recurso WHERE ACR_RECURSO IN ('/login/login_funcionario.jsp', '/login/solicitacao_externa.jsp', '/principal/rodape_servidor.jsp', '/topo/topo_consignante_pb.jsp', '/topo/topo_consignataria_pb.jsp', '/topo/topo_servidor_pb.jsp', '/termo_adesao/termo_adesao.jsp', '/lote/lst_arq_importacao.jsp', '/consignataria/ins_observacao_bloqueio.jsp');

-- Corrige acesso recurso de acesso ao REST de CEP
UPDATE tb_acesso_recurso SET ACR_RECURSO = '/v3/buscaCep' WHERE ACR_RECURSO = '/cep/buscaCep.jsp';
