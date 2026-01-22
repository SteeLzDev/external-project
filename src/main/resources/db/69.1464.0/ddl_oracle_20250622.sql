/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     13/06/2025 11:45:00                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_destinatario_email_csa_svc                         */
/*==============================================================*/
create table tb_destinatario_email_csa_svc  (
   csa_codigo           varchar2(32)                    not null,
   svc_codigo           varchar2(32)                    not null,
   constraint pk_destinatario_email_csa_svc primary key (csa_codigo, svc_codigo)
);

/*==============================================================*/
/* Index: idx_csa_dcs_1                                         */
/*==============================================================*/
create index idx_csa_dcs_1 on tb_destinatario_email_csa_svc (
   csa_codigo asc
);

/*==============================================================*/
/* Index: idx_svc_dcs_2                                         */
/*==============================================================*/
create index idx_svc_dcs_2 on tb_destinatario_email_csa_svc (
   svc_codigo asc
);

alter table tb_destinatario_email_csa_svc
   add constraint fk_csa_dcs_1 foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

alter table tb_destinatario_email_csa_svc
   add constraint fk_svc_dcs_2 foreign key (svc_codigo)
      references tb_servico (svc_codigo);

alter table tb_termo_adesao add tad_classe_acao varchar2(100) default 'N' null;

alter table tb_termo_adesao add tad_exibe_apos_leitura char(1) default 'N' null;

alter table tb_termo_adesao add tad_envia_api_consentimento char(1) default 'N' null;

alter table tb_coeficiente add cft_vlr_minimo number(13,8) null;

alter table tb_coeficiente_ativo add cft_vlr_minimo number(13,8) null;

