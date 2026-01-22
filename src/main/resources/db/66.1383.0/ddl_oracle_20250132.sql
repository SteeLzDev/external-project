/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     19/12/2024 19:04:14                          */
/*==============================================================*/

alter table tb_termo_adesao rename to tb_termo_adesao_servico;

/*==============================================================*/
/* Table: tb_leitura_termo_usuario                            */
/*==============================================================*/
create table tb_leitura_termo_usuario  (
   ltu_codigo           varchar2(32)                    not null,
   usu_codigo           varchar2(32)                    not null,
   tad_codigo           varchar2(32)                    not null,
   ltu_data             date                            not null,
   ltu_termo_aceito     char(1)                        default 'N' not null,
   ltu_canal            char(1)                        default '1' not null,
   ltu_ip_acesso        varchar2(45)                    not null,
   ltu_porta            integer                         not null,
   ltu_obs              clob                            not null,
   constraint pk_tb_leitura_termo_usuario primary key (ltu_codigo)
);

/*==============================================================*/
/* Index: r_972_fk                                              */
/*==============================================================*/
create index r_972_fk on tb_leitura_termo_usuario (
   tad_codigo asc
);

/*==============================================================*/
/* Index: r_973_fk                                              */
/*==============================================================*/
create index r_973_fk on tb_leitura_termo_usuario (
   usu_codigo asc
);

/*==============================================================*/
/* Table: tb_termo_adesao_servico                             */
/*==============================================================*/
create table tb_termo_adesao  (
   tad_codigo           varchar2(32)                    not null,
   usu_codigo           varchar2(32)                    not null,
   fun_codigo           varchar2(32),
   tad_titulo           varchar2(100)                   not null,
   tad_texto            clob                            not null,
   tad_data             date                            not null,
   tad_sequencia        integer                        default 0 not null,
   tad_exibe_cse        char(1)                        default 'N' not null,
   tad_exibe_org        char(1)                        default 'N' not null,
   tad_exibe_csa        char(1)                        default 'N' not null,
   tad_exibe_cor        char(1)                        default 'N' not null,
   tad_exibe_ser        char(1)                        default 'N' not null,
   tad_exibe_sup        char(1)                        default 'N' not null,
   tad_html             char(1)                         not null,
   tad_permite_recusar  char(1)                        default 'S' not null,
   tad_permite_ler_depois char(1)                        default 'N' not null,
   constraint pk_tb_termo_adesao_servico primary key (tad_codigo)
);

/*==============================================================*/
/* Index: r_970_fk                                              */
/*==============================================================*/
create index r_970_fk on tb_termo_adesao (
   usu_codigo asc
);

/*==============================================================*/
/* Index: r_971_fk                                              */
/*==============================================================*/
create index r_971_fk on tb_termo_adesao (
   fun_codigo asc
);

alter table tb_termo_adesao_servico rename column ter_ads_texto to tas_texto;
 
alter table tb_leitura_termo_usuario
   add constraint fk_tb_leitu_r_972_tb_termo foreign key (tad_codigo)
      references tb_termo_adesao (tad_codigo);

alter table tb_leitura_termo_usuario
   add constraint fk_tb_leitu_r_973_tb_usuar foreign key (usu_codigo)
      references tb_usuario (usu_codigo);

alter table tb_termo_adesao
   add constraint fk_tb_termo_r_970_tb_usuar foreign key (usu_codigo)
      references tb_usuario (usu_codigo);

alter table tb_termo_adesao
   add constraint fk_tb_termo_r_971_tb_funca foreign key (fun_codigo)
      references tb_funcao (fun_codigo);
