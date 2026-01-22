/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     19/07/2022 14:29:32                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_bloqueio_posto_csa_svc                             */
/*==============================================================*/
create table tb_bloqueio_posto_csa_svc  (
   csa_codigo           varchar2(32)                   not null,
   svc_codigo           varchar2(32)                   not null,
   pos_codigo           varchar2(32)                   not null,
   bpc_bloq_solicitacao char(1)                        default 'N' not null,
   bpc_bloq_reserva     char(1)                        default 'N' not null,
   constraint pk_tb_bloqueio_posto_csa_svc primary key (csa_codigo, svc_codigo, pos_codigo)
);

/*==============================================================*/
/* Index: r_888_fk                                              */
/*==============================================================*/
create index r_888_fk on tb_bloqueio_posto_csa_svc (
   csa_codigo asc
);

/*==============================================================*/
/* Index: r_889_fk                                              */
/*==============================================================*/
create index r_889_fk on tb_bloqueio_posto_csa_svc (
   svc_codigo asc
);

/*==============================================================*/
/* Index: r_890_fk                                              */
/*==============================================================*/
create index r_890_fk on tb_bloqueio_posto_csa_svc (
   pos_codigo asc
);

alter table tb_bloqueio_posto_csa_svc
   add constraint fk_tb_bloqu_r_888_tb_consi foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

alter table tb_bloqueio_posto_csa_svc
   add constraint fk_tb_bloqu_r_889_tb_servi foreign key (svc_codigo)
      references tb_servico (svc_codigo);

alter table tb_bloqueio_posto_csa_svc
   add constraint fk_tb_bloqu_r_890_tb_posto foreign key (pos_codigo)
      references tb_posto_registro_servidor (pos_codigo);

