package com.fidypay.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = true)
public class ApplicationProperties {

    private final Configs configs = new Configs();

    public Configs getConfigs() {
        return configs;
    }
    public static class Configs {

        private Integer indexPageSize;
        private Integer exportRowsCount;

        private String communityURL;

        public Integer getIndexPageSize() {
            return indexPageSize != null ? indexPageSize : 200;
        }

        public void setIndexPageSize(Integer indexPageSize) {
            this.indexPageSize = indexPageSize;
        }

        public Integer getExportRowsCount() {
            return exportRowsCount != null ? exportRowsCount : 2000;
        }

        public void setExportRowsCount(Integer exportRowsCount) { this.exportRowsCount = exportRowsCount; }


        public String getCommunityURL() { return communityURL; }

        public void setCommunityURL(String communityURL) { this.communityURL = communityURL; }
    }
}
