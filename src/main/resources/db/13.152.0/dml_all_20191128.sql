-- DESENV-12902

-- 1) ATUALIZAR O GRUPO DE PARAMETROS NOS NOVOS PARAMETROS: 

update tb_tipo_param_sist_consignante set  gps_codigo = '1' where tpc_codigo in ('670','671','674');


-- 2) CONCEDER A  PERMISSAO DE VISUALIZAR E ALTERAR PARA USUÁRIOS SUP: 

update tb_tipo_param_sist_consignante set tpc_sup_consulta = 'S', tpc_sup_altera = 'S' where tpc_codigo in ('674');


-- 3) VINCULAR OS PARAMETROS AOS PERFIS: 

insert ignore into tb_perfil_param_sist_cse (tpc_codigo, per_codigo)
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-IMP_AVANCADO')
where tpc.tpc_codigo in ('674');

insert ignore into tb_perfil_param_sist_cse (tpc_codigo, per_codigo)
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-MANUTENCAO')
where tpc.tpc_codigo in ('674');

insert ignore into tb_perfil_param_sist_cse (tpc_codigo, per_codigo)
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-SEGURANCA_BASICO')
where tpc.tpc_codigo in ('670','671','674');
