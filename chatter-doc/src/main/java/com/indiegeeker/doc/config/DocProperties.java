package com.indiegeeker.doc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文档配置属性
 *
 * @author indiegeeker
 */
@ConfigurationProperties(prefix = "chatter.doc")
public class DocProperties {

    /**
     * 是否启用文档
     */
    private boolean enabled = true;

    /**
     * 文档标题
     */
    private String title = "Chatter API 文档";

    /**
     * 文档版本
     */
    private String version = "1.0.0";

    /**
     * 文档描述
     */
    private String description = "Chatter 项目 API 接口文档";

    /**
     * 服务条款URL
     */
    private String termsOfServiceUrl = "";

    /**
     * 联系人信息
     */
    private Contact contact = new Contact();

    /**
     * 许可证信息
     */
    private License license = new License();

    /**
     * 服务器信息
     */
    private Server server = new Server();

    /**
     * 分组配置
     */
    private GroupConfig group = new GroupConfig();

    public static class Contact {
        /**
         * 联系人姓名
         */
        private String name = "IndieGeeker";

        /**
         * 联系人邮箱
         */
        private String email = "contact@indiegeeker.com";

        /**
         * 联系人URL
         */
        private String url = "https://indiegeeker.com";

        // getter 和 setter
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class License {
        /**
         * 许可证名称
         */
        private String name = "Apache 2.0";

        /**
         * 许可证URL
         */
        private String url = "https://www.apache.org/licenses/LICENSE-2.0";

        // getter 和 setter
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Server {
        /**
         * 服务器URL
         */
        private String url = "http://localhost:8080";

        /**
         * 服务器描述
         */
        private String description = "本地开发环境";

        // getter 和 setter
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class GroupConfig {
        /**
         * 分组名称
         */
        private String name = "default";

        /**
         * 扫描包路径
         */
        private String basePackage = "com.indiegeeker";

        /**
         * 分组描述
         */
        private String description = "默认分组";

        // getter 和 setter
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBasePackage() {
            return basePackage;
        }

        public void setBasePackage(String basePackage) {
            this.basePackage = basePackage;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    // getter 和 setter
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTermsOfServiceUrl() {
        return termsOfServiceUrl;
    }

    public void setTermsOfServiceUrl(String termsOfServiceUrl) {
        this.termsOfServiceUrl = termsOfServiceUrl;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public GroupConfig getGroup() {
        return group;
    }

    public void setGroup(GroupConfig group) {
        this.group = group;
    }
} 