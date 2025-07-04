package com.indiegeeker.context;

import com.indiegeeker.context.application.UserContextHolder;
import com.indiegeeker.context.domain.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户上下文功能测试
 * 
 * Author: wen
 * Date: 2025/6/30
 */
@DisplayName("用户上下文功能测试")
class UserContextTest {

    private UserContext testUserContext;

    @BeforeEach
    void setUp() {
        // 清理之前的上下文
        UserContextHolder.clear();
        
        // 创建测试用户上下文
        testUserContext = UserContext.builder()
                .userId("test-user-001")
                .username("testuser")
                .nickname("测试用户")
                .mobile("13800138000")
                .roles(List.of("USER", "ADMIN"))
                .permissions(Set.of("read", "write", "delete"))
                .tenantId("tenant-001")
                .clientType("web")
                .requestIp("127.0.0.1")
                .userAgent("Mozilla/5.0 (Test Browser)")
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(2))
                .build();
    }

    @AfterEach
    void tearDown() {
        // 清理上下文
        UserContextHolder.clear();
    }

    @Test
    @DisplayName("测试设置和获取用户上下文")
    void testSetAndGetContext() {
        // 设置上下文
        UserContextHolder.setContext(testUserContext);
        
        // 获取上下文
        UserContext context = UserContextHolder.getContext();
        
        // 验证
        assertNotNull(context);
        assertEquals("test-user-001", context.getUserId());
        assertEquals("testuser", context.getUsername());
        assertEquals("测试用户", context.getNickname());
    }

    @Test
    @DisplayName("测试便捷方法")
    void testConvenienceMethods() {
        // 设置上下文
        UserContextHolder.setContext(testUserContext);
        
        // 测试便捷方法
        assertEquals("test-user-001", UserContextHolder.getCurrentUserId());
        assertEquals("testuser", UserContextHolder.getCurrentUsername());
        assertEquals("测试用户", UserContextHolder.getCurrentNickname());
        assertEquals("tenant-001", UserContextHolder.getCurrentTenantId());
        assertTrue(UserContextHolder.isAuthenticated());
    }

    @Test
    @DisplayName("测试角色检查")
    void testRoleCheck() {
        // 设置上下文
        UserContextHolder.setContext(testUserContext);
        
        // 测试角色检查
        assertTrue(UserContextHolder.hasRole("USER"));
        assertTrue(UserContextHolder.hasRole("ADMIN"));
        assertFalse(UserContextHolder.hasRole("SUPER_ADMIN"));
    }

    @Test
    @DisplayName("测试权限检查")
    void testPermissionCheck() {
        // 设置上下文
        UserContextHolder.setContext(testUserContext);
        
        // 测试权限检查
        assertTrue(UserContextHolder.hasPermission("read"));
        assertTrue(UserContextHolder.hasPermission("write"));
        assertTrue(UserContextHolder.hasPermission("delete"));
        assertFalse(UserContextHolder.hasPermission("execute"));
    }

    @Test
    @DisplayName("测试清除上下文")
    void testClearContext() {
        // 设置上下文
        UserContextHolder.setContext(testUserContext);
        assertNotNull(UserContextHolder.getContext());
        
        // 清除上下文
        UserContextHolder.clear();
        
        // 验证清除结果
        assertNull(UserContextHolder.getContext());
        assertNull(UserContextHolder.getCurrentUserId());
        assertFalse(UserContextHolder.isAuthenticated());
    }

    @Test
    @DisplayName("测试无上下文时的行为")
    void testNoContext() {
        // 确保没有上下文
        UserContextHolder.clear();
        
        // 验证无上下文时的行为
        assertNull(UserContextHolder.getContext());
        assertNull(UserContextHolder.getCurrentUserId());
        assertNull(UserContextHolder.getCurrentUsername());
        assertFalse(UserContextHolder.isAuthenticated());
        assertFalse(UserContextHolder.hasRole("USER"));
        assertFalse(UserContextHolder.hasPermission("read"));
    }

    @Test
    @DisplayName("测试上下文复制")
    void testCopyContext() {
        // 设置上下文
        UserContextHolder.setContext(testUserContext);
        
        // 复制上下文
        UserContext copiedContext = UserContextHolder.copyContext();
        
        // 验证复制结果
        assertNotNull(copiedContext);
        assertEquals(testUserContext.getUserId(), copiedContext.getUserId());
        assertEquals(testUserContext.getUsername(), copiedContext.getUsername());
        assertEquals(testUserContext.getRoles(), copiedContext.getRoles());
        assertEquals(testUserContext.getPermissions(), copiedContext.getPermissions());
        
        // 验证是独立的对象
        assertNotSame(testUserContext, copiedContext);
    }

    @Test
    @DisplayName("测试在指定上下文中执行操作")
    void testExecuteWithContext() {
        // 创建另一个用户上下文
        UserContext anotherContext = UserContext.builder()
                .userId("another-user-002")
                .username("anotheruser")
                .nickname("另一个用户")
                .build();
        
        // 设置初始上下文
        UserContextHolder.setContext(testUserContext);
        assertEquals("test-user-001", UserContextHolder.getCurrentUserId());
        
        // 在指定上下文中执行操作
        String result = UserContextHolder.executeWithContext(anotherContext, () -> {
            assertEquals("another-user-002", UserContextHolder.getCurrentUserId());
            return "执行成功";
        });
        
        // 验证执行结果
        assertEquals("执行成功", result);
        
        // 验证原始上下文恢复
        assertEquals("test-user-001", UserContextHolder.getCurrentUserId());
    }

    @Test
    @DisplayName("测试用户上下文对象的方法")
    void testUserContextMethods() {
        // 测试角色检查方法
        assertTrue(testUserContext.hasRole("USER"));
        assertTrue(testUserContext.hasAnyRole("USER", "ADMIN"));
        assertTrue(testUserContext.hasAnyRole("SUPER_ADMIN", "USER"));
        assertFalse(testUserContext.hasAnyRole("SUPER_ADMIN", "GUEST"));
        
        // 测试权限检查方法
        assertTrue(testUserContext.hasPermission("read"));
        assertTrue(testUserContext.hasAnyPermission("read", "write"));
        assertTrue(testUserContext.hasAnyPermission("execute", "read"));
        assertFalse(testUserContext.hasAnyPermission("execute", "create"));
        
        // 测试过期检查
        assertFalse(testUserContext.isExpired());
        
        // 测试过期的情况
        UserContext expiredContext = UserContext.builder()
                .userId("expired-user")
                .expiresAt(LocalDateTime.now().minusHours(1))
                .build();
        assertTrue(expiredContext.isExpired());
    }

    @Test
    @DisplayName("测试线程安全性")
    void testThreadSafety() throws InterruptedException {
        // 设置上下文
        UserContextHolder.setContext(testUserContext);
        
        // 在启动新线程之前就复制上下文
        UserContext copiedContext = UserContextHolder.copyContext();
        assertNotNull(copiedContext, "复制的上下文不应为null");
        
        // 使用 CountDownLatch 确保线程同步
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        java.util.concurrent.atomic.AtomicReference<Exception> threadException = 
            new java.util.concurrent.atomic.AtomicReference<>();
        
        // 在另一个线程中验证上下文
        Thread thread = new Thread(() -> {
            try {
                // 新线程中应该没有上下文
                assertNull(UserContextHolder.getContext());
                
                // 直接使用预先复制的上下文
                UserContextHolder.setContext(copiedContext);
                
                // 验证上下文设置成功
                assertNotNull(UserContextHolder.getContext());
                assertEquals("test-user-001", UserContextHolder.getCurrentUserId());
                
                // 清理
                UserContextHolder.clear();
                
                // 通知主线程测试完成
                latch.countDown();
            } catch (Exception e) {
                threadException.set(e);
                latch.countDown();
            }
        });
        
        thread.start();
        latch.await(); // 等待线程完成
        
        // 检查是否有异常
        if (threadException.get() != null) {
            throw new RuntimeException("线程测试失败", threadException.get());
        }
        
        // 主线程的上下文应该保持不变
        assertNotNull(UserContextHolder.getContext());
        assertEquals("test-user-001", UserContextHolder.getCurrentUserId());
    }
} 