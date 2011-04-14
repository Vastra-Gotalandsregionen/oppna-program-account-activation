insert into vgr_activation_account (used, vgrId, expire, activationCode, customUrl) values (false, 'ex_valid-vgrid1', '2111-02-28 00:00:00', 'valid1', 'http://example.com');
insert into vgr_activation_account (used, vgrId, expire, activationCode, customUrl) values (false, 'ex_valid-vgrid2', '2111-02-28 00:00:00', 'valid2', 'http://example.com');
insert into vgr_activation_account (used, vgrId, expire, activationCode, customUrl) values (false, 'ex_inactive-vgrid', '2011-01-28 00:00:00', 'inactive', '');
insert into vgr_activation_account (used, vgrId, expire, activationCode, customUrl) values (true, 'ex_invalid-vgrid', '2111-02-28 00:00:00', 'invalid', '');
