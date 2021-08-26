package com.hocztms.service;

import com.hocztms.entity.AdminEntity;
import com.hocztms.entity.AdminRoleEntity;

import java.util.List;

public interface AdminService {

    AdminEntity insertAdmin(AdminEntity adminEntity);

    AdminRoleEntity insertAdminRole(AdminRoleEntity adminRoleEntity);

    List<AdminRoleEntity> findAdminRolesByUsername(String username);

    AdminEntity findAdminByUsername(String username);

    void deleteAdminByUsername(String username);
}
