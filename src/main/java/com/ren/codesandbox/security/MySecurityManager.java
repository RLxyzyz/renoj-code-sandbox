package com.ren.codesandbox.security;

import java.io.FileDescriptor;
import java.security.Permission;

/**
 * @author 任磊
 * @version 1.0
 * @project renoj-code-sandbox
 * @description进行操作的权限校验
 * @date 2024/2/1 22:17:02
 */
public class MySecurityManager extends SecurityManager {
    @Override
    public void checkPermission(Permission perm) {
        super.checkPermission(perm);
    }

    @Override
    public void checkExec(String cmd) {
        throw new SecurityException("权限不足:"+cmd);
    }

    @Override
    public void checkRead(FileDescriptor fd) {
        throw new SecurityException("权限不足:"+fd);
    }

    @Override
    public void checkRead(String file) {
        throw new SecurityException("权限不足:"+file);
    }

    @Override
    public void checkRead(String file, Object context) {
        throw new SecurityException("权限不足:"+file+context);
    }

    @Override
    public void checkWrite(FileDescriptor fd) {
        throw new SecurityException("权限不足:"+fd);
    }

    @Override
    public void checkWrite(String file) {
        throw new SecurityException("权限不足:"+file);
    }

    @Override
    public void checkDelete(String file) {
        throw new SecurityException("权限不足:"+file);
    }

    @Override
    public void checkConnect(String host, int port) {
        throw new SecurityException("权限不足:"+host+port);
    }

    @Override
    public void checkConnect(String host, int port, Object context) {
        throw new SecurityException("权限不足:"+host+port+context);
    }
}
