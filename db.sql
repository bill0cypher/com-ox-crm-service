INSERT INTO public.privilege (id, name) VALUES ('564e1f73-a1d9-4b25-a5dc-fb3b96cf5e19', 'READ');
INSERT INTO public.privilege (id, name) VALUES ('564e2f73-a1d9-4b25-a5dc-fb3b96cf5e19', 'WRITE');
INSERT INTO public.privilege (id, name) VALUES ('564e3f73-a1d9-4b25-a5dc-fb3b96cf5e19', 'DELETE');
INSERT INTO public.role (id, name) VALUES ('b151e763-351f-4312-9304-3faab0cba81f', 'GUEST');
INSERT INTO public.role (id, name) VALUES ('b152e763-351f-4312-9304-3faab0cba81f', 'ROLE_USER');
INSERT INTO public.role (id, name) VALUES ('b153e763-351f-4312-9304-3faab0cba81f', 'ROLE_ADMIN');
INSERT INTO public.roles_privileges (privilege_id, role_id) VALUES ('564e1f73-a1d9-4b25-a5dc-fb3b96cf5e19', 'b152e763-351f-4312-9304-3faab0cba81f');
INSERT INTO public.roles_privileges (privilege_id, role_id) VALUES ('564e2f73-a1d9-4b25-a5dc-fb3b96cf5e19', 'b152e763-351f-4312-9304-3faab0cba81f');
INSERT INTO public.roles_privileges (privilege_id, role_id) VALUES ('564e1f73-a1d9-4b25-a5dc-fb3b96cf5e19', 'b153e763-351f-4312-9304-3faab0cba81f');
INSERT INTO public.roles_privileges (privilege_id, role_id) VALUES ('564e2f73-a1d9-4b25-a5dc-fb3b96cf5e19', 'b153e763-351f-4312-9304-3faab0cba81f');
INSERT INTO public.roles_privileges (privilege_id, role_id) VALUES ('564e3f73-a1d9-4b25-a5dc-fb3b96cf5e19', 'b153e763-351f-4312-9304-3faab0cba81f');