-- DESENV-12384

-- 1) ATUALIZAR O GRUPO DE PARAMETROS NOS NOVOS PARAMETROS: 

update tb_tipo_param_sist_consignante set  gps_codigo = '2' where tpc_codigo in ('650','9');
update tb_tipo_param_sist_consignante set  gps_codigo = '3' where tpc_codigo in ('641','646','651','652','653','654','655');


-- 2) CONCEDER A  PERMISSAO DE VISUALIZAR E ALTERAR PARA USUÁRIOS SUP: 

update tb_tipo_param_sist_consignante set tpc_sup_consulta = 'S', tpc_sup_altera = 'S' where tpc_codigo in ('641','646','650','651','652','653','654','655');

-- 3) VINCULAR OS PARAMETROS AOS PERFIS: 

insert ignore into tb_perfil_param_sist_cse (tpc_codigo, per_codigo)
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-IMP_AVANCADO')
where tpc.tpc_codigo in ('641','646','650','651','652','653','654','655','9');

insert ignore into tb_perfil_param_sist_cse (tpc_codigo, per_codigo)
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-MANUTENCAO')
where tpc.tpc_codigo in ('641','646','650','651','652','653','654','655','9');

insert ignore into tb_perfil_param_sist_cse (tpc_codigo, per_codigo)
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-PRODUCAO')
where tpc.tpc_codigo in ('650');

insert ignore into tb_perfil_param_sist_cse (tpc_codigo, per_codigo)
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-PRODUCAO_AVANCADO')
where tpc.tpc_codigo in ('650');
