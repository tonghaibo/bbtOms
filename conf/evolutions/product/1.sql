# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table admin (
  id                        bigint auto_increment not null,
  username                  varchar(16) not null,
  realname                  varchar(16),
  passwd                    varchar(32) not null,
  active                    tinyint(1) DEFAULT 1  not null,
  `date_add`                datetime(6),
  lastip                    varchar(32),
  date_login                datetime(6),
  login_count               int(10)  DEFAULT 0 ,
  headIcon                  varchar(256) DEFAULT '' ,
  adminType                 varchar(10) DEFAULT '1' ,
  uid                       int(11)  DEFAULT 0 ,
  roleId                    int(10)  DEFAULT 0 ,
  constraint uq_admin_username unique (username),
  constraint pk_admin primary key (id))
;

create table versioninfo (
  id                        integer auto_increment not null,
  ostype                     varchar(2) DEFAULT '' ,
  latestver                  varchar(16) DEFAULT '' ,
  isforced                   varchar(2) DEFAULT '' ,
  remind_time               varchar(255),
  title                      varchar(255) DEFAULT '' ,
  message                   varchar(255),
  url                        varchar(255) DEFAULT '' ,
  sta                        varchar(2) DEFAULT '',
  date_new                  datetime(6),
  date_upd                  datetime(6),
  marketcode                 varchar(255) DEFAULT '' ,
  constraint pk_versioninfo primary key (id))
;

create table balance (
  id                        integer auto_increment not null,
  uid                        int(11) DEFAULT 0 ,
  balance                    int(11) DEFAULT 0 ,
  canuse                     int(11) DEFAULT 0 ,
  withdraw                   int(11) DEFAULT 0 ,
  remark                     varchar(32) '' ,
  phone                      varchar(16) '' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_balance primary key (id))
;

create table balance_income (
  id                        integer auto_increment not null,
  uid                        int(11) DEFAULT 0 ,
  amount                     int(11) DEFAULT 0 ,
  title                      varchar(32) '' ,
  remark                     varchar(256) '' ,
  out_trade_no               varchar(64) '' ,
  src                        varchar(16) '' ,
  phone                      varchar(16) '' ,
  sta                        varchar(2) '0' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_balance_income primary key (id))
;

create table balance_withdraw (
  id                        integer auto_increment not null,
  uid                        int(11) DEFAULT 0 ,
  amount                     int(11) DEFAULT 0 ,
  remark                     varchar(256) '' ,
  oper                       varchar(16) '' ,
  oper_remark                varchar(256) '' ,
  sta                        varchar(2) '0' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_balance_withdraw primary key (id))
;

create table count_h5 (
  id                        bigint auto_increment not null,
  createTime                datetime(6),
  ip                        varchar(255),
  url                       varchar(255),
  shareType                 varchar(2) DEFAULT '',
  iswx                      varchar(2) DEFAULT '0',
  channel                   varchar(255),
  userId                    bigint,
  openid                    varchar(255),
  constraint pk_count_h5 primary key (id))
;

create table deviceuser (
  id                        integer auto_increment not null,
  deviceid                   varchar(64) ,
  uid                         int(11) DEFAULT 0 ,
  ostype                     varchar(2) ,
  osversion                  varchar(32) ,
  model                      varchar(32) ,
  pushToken                  varchar(32) ,
  solution                   varchar(32) ,
  appversion                 varchar(32) ,
  marketcode                 varchar(8) ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_deviceuser primary key (id))
;

create table magazineinfo (
  id                        integer auto_increment not null,
  magid                     integer,
  imgurl                     varchar(256) DEFAULT '' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_magazineinfo primary key (id))
;

create table magazine_user (
  id                        integer auto_increment not null,
  magid                     integer,
  uid                       integer,
  date_new                  datetime(6),
  constraint pk_magazine_user primary key (id))
;

create table magazinelist (
  id                        integer auto_increment not null,
  title                      varchar(64) DEFAULT '' ,
  remark                     varchar(256) DEFAULT '' ,
  girlname                   varchar(16) DEFAULT '' ,
  girlage                    varchar(16) DEFAULT '' ,
  girlheight                 varchar(16) DEFAULT '' ,
  imgurl                     varchar(256) DEFAULT '' ,
  typ                        varchar(2) DEFAULT '0' ,
  nsort                     integer,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_magazinelist primary key (id))
;

create table operate_log (
  id                        bigint auto_increment not null,
  `date_add`                datetime(6) not null,
  adminid                   bigint not null,
  adminname                 varchar(16) DEFAULT '' ,
  typ                       varchar(32) DEFAULT '' ,
  remark                    varchar(512) DEFAULT '' ,
  opType                    int(11)  not null,
  ip                        varchar(255) not null,
  constraint ck_operate_log_opType check (opType in (0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22)),
  constraint pk_operate_log primary key (id))
;

create table post_order (
  id                        integer auto_increment not null,
  uid                       integer,
  ordercode                 varchar(255),
  userlong                  double,
  userlat                   double,
  username                  varchar(255),
  phone                     varchar(255),
  address                   varchar(255),
  receivelong               double,
  receivelat                double,
  receivename               varchar(255),
  receivephone              varchar(255),
  receiveaddress            varchar(255),
  subjectname               varchar(255),
  subjecttyp                integer,
  subjectremark             varchar(255),
  distance                  bigint,
  weight                    integer,
  paytyp                    varchar(255),
  goodsfee                  integer,
  freight                   integer,
  totalfee                  integer,
  award                     integer,
  status                    integer,
  gettyp                    varchar(255),
  gettime                   datetime(6),
  realgettime               datetime(6),
  overtime                  datetime(6),
  date_new                  datetime(6),
  date_upd                  datetime(6),
  islooked                  varchar(255),
  ordertyp                  varchar(255),
  remark                    varchar(255),
  reasonid                  integer,
  constraint pk_post_order primary key (id))
;

create table post_order_user (
  id                        integer auto_increment not null,
  orderid                   integer,
  postmanid                 integer,
  status                    integer,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_post_order_user primary key (id))
;

create table post_reward (
  id                        integer auto_increment not null,
  typ                        int(11) DEFAULT 0 ,
  btm                        varchar(32) DEFAULT '' ,
  etm                        varchar(16) DEFAULT '' ,
  commision                  int(11) DEFAULT 0 ,
  commisiontyp               int(11) DEFAULT 0 ,
  randomval                  int(11) DEFAULT 0 ,
  congratulation            varchar(255),
  remarktxt                 varchar(255),
  ruletxt                   varchar(255),
  min_num                   integer,
  max_num                   integer,
  flg                        int(11) DEFAULT 0 ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_post_reward primary key (id))
;

create table postcompany (
  id                        integer auto_increment not null,
  companyname                varchar(32) DEFAULT '' ,
  companycode                varchar(34) DEFAULT '' ,
  sta                        varchar(2) DEFAULT '1' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_postcompany primary key (id))
;

create table postcontent (
  id                        integer auto_increment not null,
  typ                        int(11),
  typname                    varchar(32) '' ,
  typicon                    varchar(256) '' ,
  title                      varchar(64) '' ,
  subtitle                   varchar(256) '' ,
  content                    text  ,
  expectamount               int(11),
  amount                     varchar(256) DEFAULT '',
  tips                       varchar(64) '' ,
  linkurl                    varchar(256) '' ,
  dateremark                 varchar(64) '' ,
  start_tim                 varchar(255),
  end_tim                   varchar(255),
  nsort                      int(11) DEFAULT 0 ,
  sta                        varchar(2) '0' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_postcontent primary key (id))
;

create table postcontent_detail (
  id                        integer auto_increment not null,
  pcid                       int(11),
  pccontent                  varchar(32) '' ,
  date_new                  datetime(6),
  constraint pk_postcontent_detail primary key (id))
;

create table postcontent_img (
  id                        bigint auto_increment not null,
  pcid                       int(11),
  imgurl                     varchar(256) '0' ,
  date_new                  datetime(6),
  magid                      int(11),
  constraint pk_postcontent_img primary key (id))
;

create table postcontent_user (
  id                        integer auto_increment not null,
  pcid                      integer,
  uid                       integer,
  magid                     integer,
  rewardid                  integer,
  sta                        varchar(2) default '0' ,
  date_new                  datetime(6),
  constraint pk_postcontent_user primary key (id))
;

create table postmanuser (
  id                         varchar(128) DEFAULT ''  auto_increment not null,
  staffid                    varchar(32) DEFAULT '' ,
  nickname                   varchar(16) DEFAULT '' ,
  phone                      varchar(16) DEFAULT '' ,
  headicon                   varchar(256) DEFAULT '' ,
  companyname                varchar(32) DEFAULT '' ,
  cardidno                   varchar(32) DEFAULT '' ,
  substation                 varchar(32) DEFAULT '' ,
  companyid                  int DEFAULT 0 ,
  shopurl                    varchar(128) DEFAULT ''  ,
  alipay_account             varchar(64) DEFAULT '' ,
  token                      varchar(32) DEFAULT '' ,
  bbttoken                   varchar(32) DEFAULT '' ,
  sta                        varchar(2) DEFAULT '1' ,
  spreadticket               varchar(256) DEFAULT '' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  lat                        numeric(9,4) ,
  lon                        numeric(9,4) ,
  height                     numeric(9,4) ,
  addr                       varchar(256) DEFAULT '' ,
  addrdes                    varchar(512) DEFAULT '' ,
  constraint pk_postmanuser primary key (id))
;

create table postmanuser_location_log (
  id                        bigint auto_increment not null,
  postmanid                 integer,
  staffid                    varchar(32) DEFAULT '' ,
  nickname                   varchar(16) DEFAULT '' ,
  phone                      varchar(16) DEFAULT '' ,
  companyname                varchar(32) DEFAULT '' ,
  substation                 varchar(32) DEFAULT '' ,
  companyid                  int DEFAULT 0 ,
  latitude                   numeric(9,4) ,
  lontitude                  numeric(9,4) ,
  height                     numeric(9,4) ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_postmanuser_location_log primary key (id))
;

create table postmanuser_temp (
  id                        integer auto_increment not null,
  postmanid                 integer,
  orderid                   integer,
  constraint pk_postmanuser_temp primary key (id))
;

create table pushinfo (
  id                        integer auto_increment not null,
  title                      varchar(256) DEFAULT '' ,
  content                    varchar(512) DEFAULT '' ,
  logo                       varchar(64) DEFAULT '' ,
  logourl                    varchar(128) DEFAULT '' ,
  url                        varchar(128) DEFAULT '' ,
  pushtoken                  varchar(128) DEFAULT '' ,
  flg                        varchar(2) DEFAULT '0' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_pushinfo primary key (id))
;

create table reddot (
  id                        integer auto_increment not null,
  uid                        int(11) DEFAULT 0 ,
  myfav                      varchar(2) DEFAULT '0' ,
  upgrade                   varchar(2) DEFAULT '0' ,
  wallet_withdraw            varchar(2) DEFAULT '0'  ,
  wallet_incoming            varchar(2) DEFAULT '0'  ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_reddot primary key (id))
;

create table smsinfo (
  id                        integer auto_increment not null,
  phone                     varchar(255),
  tpl_id                     varchar(8) DEFAULT '' ,
  args                      varchar(255),
  flg                       varchar(255),
  typ                        varchar(2) DEFAULT '1' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_smsinfo primary key (id))
;

create table stat_daily_delivery_order (
  id                        integer auto_increment not null,
  stat_date                  varchar(11) '' ,
  source                     varchar(50) '' ,
  new_order_cnt              int(11) DEFAULT 0 ,
  new_complete_cnt           int(11) DEFAULT 0 ,
  new_suspend_cnt            int(11) DEFAULT 0 ,
  new_reject_cnt             int(11) DEFAULT 0 ,
  complete_cnt               int(11) DEFAULT 0 ,
  suspend_cnt                int(11) DEFAULT 0 ,
  reject_cnt                 int(11) DEFAULT 0 ,
  create_time               datetime(6),
  constraint pk_stat_daily_delivery_order primary key (id))
;

create table stat_daily_delivery_route (
  id                        integer auto_increment not null,
  stat_date                  varchar(11) '' ,
  app_scan_pv                int(11) DEFAULT 0 ,
  app_scan_uv                int(11) DEFAULT 0 ,
  app_search_pv              int(11) DEFAULT 0 ,
  app_search_uv              int(11) DEFAULT 0 ,
  app_order_list_pv          int(11) DEFAULT 0 ,
  app_order_list_uv          int(11) DEFAULT 0 ,
  app_order_info_pv          int(11) DEFAULT 0 ,
  app_order_info_uv          int(11) DEFAULT 0 ,
  widget_order_list_pv       int(11) DEFAULT 0 ,
  widget_order_list_uv       int(11) DEFAULT 0 ,
  widget_order_info_pv       int(11) DEFAULT 0 ,
  widget_order_info_uv       int(11) DEFAULT 0 ,
  create_time               datetime(6),
  constraint pk_stat_daily_delivery_route primary key (id))
;

create table stat_daily_postman_order (
  id                        integer auto_increment not null,
  stat_date                  varchar(11) '' ,
  order_cnt                  int(11) DEFAULT 0 ,
  paid_order_cnt             int(11) DEFAULT 0 ,
  paid_order_amount          int(11) DEFAULT 0 ,
  total_order_cnt            int(11) DEFAULT 0 ,
  total_paid_order_amount    int(11) DEFAULT 0 ,
  app_reg_user_cnt           int(11) DEFAULT 0 ,
  create_time               datetime(6),
  constraint pk_stat_daily_postman_order primary key (id))
;

create table stat_daily_postman_order_detail (
  id                        integer auto_increment not null,
  stat_date                  varchar(11) '' ,
  goods_id                   int(11) DEFAULT 0 ,
  goods_name                 varchar(200) '' ,
  sold_cnt                   int(11) DEFAULT 0 ,
  create_time               datetime(6),
  constraint pk_stat_daily_postman_order_detail primary key (id))
;

create table stat_daily_postman_shop (
  id                        integer auto_increment not null,
  stat_date                  varchar(11) '' ,
  scan_pv                    int(11) DEFAULT 0 ,
  scan_uv                    int(11) DEFAULT 0 ,
  new_follow_cnt             int(11) DEFAULT 0 ,
  active_follow_cnt          int(11) DEFAULT 0 ,
  total_follow_cnt           int(11) DEFAULT 0 ,
  shop_list_pv               int(11) DEFAULT 0 ,
  shop_list_uv               int(11) DEFAULT 0 ,
  shop_detail_pv             int(11) DEFAULT 0 ,
  shop_detail_uv             int(11) DEFAULT 0 ,
  shop_buy_pv                int(11) DEFAULT 0 ,
  shop_buy_uv                int(11) DEFAULT 0 ,
  shop_confirm_pv            int(11) DEFAULT 0 ,
  shop_confirm_uv            int(11) DEFAULT 0 ,
  shop_pay_pv                int(11) DEFAULT 0 ,
  shop_pay_uv                int(11) DEFAULT 0 ,
  create_time               datetime(6),
  constraint pk_stat_daily_postman_shop primary key (id))
;

create table stat_daily_postman_task (
  id                        integer auto_increment not null,
  stat_date                  varchar(11) '' ,
  app_task_list_pv           int(11) DEFAULT 0 ,
  app_task_list_uv           int(11) DEFAULT 0 ,
  app_task_detail_pv         int(11) DEFAULT 0 ,
  app_task_detail_uv         int(11) DEFAULT 0 ,
  widget_task_list_pv        int(11) DEFAULT 0 ,
  widget_task_list_uv        int(11) DEFAULT 0 ,
  widget_task_detail_pv      int(11) DEFAULT 0 ,
  widget_task_detail_uv      int(11) DEFAULT 0 ,
  task_complete_user_cnt     int(11) DEFAULT 0 ,
  task_complete_cnt          int(11) DEFAULT 0 ,
  create_time               datetime(6),
  constraint pk_stat_daily_postman_task primary key (id))
;

create table stat_daily_postman_task_detail (
  id                        integer auto_increment not null,
  stat_date                  varchar(11) '' ,
  task_id                    int(11) DEFAULT 0 ,
  task_name                  varchar(200) DEFAULT 0 ,
  task_view_pv               int(11) DEFAULT 0 ,
  task_view_uv               int(11) DEFAULT 0 ,
  task_complete_cnt          int(11) DEFAULT 0 ,
  task_complete_user_cnt     int(11) DEFAULT 0 ,
  create_time               datetime(6),
  constraint pk_stat_daily_postman_task_detail primary key (id))
;

create table stat_daily_user (
  id                        integer auto_increment not null,
  stat_date                  varchar(11) '' ,
  user_cnt                   int(11) DEFAULT 0 ,
  total_user_cnt             int(11) DEFAULT 0 ,
  active_user_cnt            int(11) DEFAULT 0 ,
  active_user_launch_cnt     int(11) DEFAULT 0 ,
  create_time               datetime(6),
  constraint pk_stat_daily_user primary key (id))
;

create table stat_daily_widget (
  id                        integer auto_increment not null,
  stat_date                  varchar(11) '' ,
  pv                         int(11) DEFAULT 0 ,
  uv                         int(11) DEFAULT 0 ,
  click_pv                   int(11) DEFAULT 0 ,
  click_uv                   int(11) DEFAULT 0 ,
  create_time               datetime(6),
  constraint pk_stat_daily_widget primary key (id))
;

create table stat_daily_x7 (
  id                        integer auto_increment not null,
  stat_date                  varchar(11) '' ,
  reg_device_cnt             int(11) DEFAULT 0 ,
  active_device_cnt          int(11) DEFAULT 0 ,
  total_device_cnt           int(11) DEFAULT 0 ,
  total_reg_device_cnt       int(11) DEFAULT 0 ,
  create_time               datetime(6),
  constraint pk_stat_daily_x7 primary key (id))
;

create table user_address (
  id                        integer auto_increment not null,
  uid                       integer,
  longs                     double,
  lat                       double,
  username                  varchar(255),
  phone                     varchar(255),
  address                   varchar(255),
  typ                       integer,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_user_address primary key (id))
;

create table user_balance (
  id                        integer auto_increment not null,
  uid                       integer,
  balance                   integer,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_user_balance primary key (id))
;

create table user_balance_log (
  id                        integer auto_increment not null,
  uid                       integer,
  orderid                   varchar(255),
  changemoney               integer,
  beforebalance             integer,
  endbalance                integer,
  remark                    varchar(255),
  typ                       integer,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_user_balance_log primary key (id))
;

create table user_info (
  uid                       integer auto_increment not null,
  openid                    varchar(255),
  nickname                  varchar(255),
  date_new                  datetime(6),
  date_upd                  datetime(6),
  typ                       integer,
  lat                       double,
  lng                       double,
  constraint pk_user_info primary key (uid))
;

create table versioninfo (
  id                        integer auto_increment not null,
  ostype                     varchar(2) DEFAULT '' ,
  latestver                  varchar(16) DEFAULT '' ,
  isforced                   varchar(2) DEFAULT '' ,
  remind_time                varchar(2) DEFAULT '' ,
  message                   longtext,
  url                        varchar(256) DEFAULT '' ,
  sta                        varchar(2) DEFAULT '1' ,
  marketcode                 varchar(256) DEFAULT '' ,
  date_new                  datetime(6),
  date_upd                  datetime(6),
  constraint pk_versioninfo primary key (id))
;

create table wx_user (
  id                        bigint auto_increment not null,
  date_new                  datetime(6),
  unionid                   varchar(255) DEFAULT '' ,
  nickname                  varchar(255) DEFAULT '' ,
  headicon                  varchar(255) DEFAULT '' ,
  fromuid                   varchar(255) DEFAULT '' ,
  openid                    varchar(255) DEFAULT '' ,
  userid                    varchar(255) DEFAULT '' ,
  tmpflg                    varchar(10) DEFAULT '' ,
  constraint pk_wx_user primary key (id))
;

create table wx_user_PriceBack (
  id                        bigint auto_increment not null,
  date_add                  datetime(6),
  uid                       varchar(255) DEFAULT '' ,
  postmanid                 varchar(255) DEFAULT '' ,
  balance                   varchar(255) DEFAULT '' ,
  sta                       varchar(255) DEFAULT '' ,
  typ                       varchar(255) DEFAULT '' ,
  orderid                   varchar(255) DEFAULT '' ,
  orderCode                 varchar(255),
  constraint pk_wx_user_PriceBack primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table admin;

drop table versioninfo;

drop table balance;

drop table balance_income;

drop table balance_withdraw;

drop table count_h5;

drop table deviceuser;

drop table magazineinfo;

drop table magazine_user;

drop table magazinelist;

drop table operate_log;

drop table post_order;

drop table post_order_user;

drop table post_reward;

drop table postcompany;

drop table postcontent;

drop table postcontent_detail;

drop table postcontent_img;

drop table postcontent_user;

drop table postmanuser;

drop table postmanuser_location_log;

drop table postmanuser_temp;

drop table pushinfo;

drop table reddot;

drop table smsinfo;

drop table stat_daily_delivery_order;

drop table stat_daily_delivery_route;

drop table stat_daily_postman_order;

drop table stat_daily_postman_order_detail;

drop table stat_daily_postman_shop;

drop table stat_daily_postman_task;

drop table stat_daily_postman_task_detail;

drop table stat_daily_user;

drop table stat_daily_widget;

drop table stat_daily_x7;

drop table user_address;

drop table user_balance;

drop table user_balance_log;

drop table user_info;

drop table versioninfo;

drop table wx_user;

drop table wx_user_PriceBack;

SET FOREIGN_KEY_CHECKS=1;

