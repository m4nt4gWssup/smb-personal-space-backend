package ru.dis.personalspace.config.properties;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ru.disgroup")
public class SmbPersonalSpaceProperties {
    private SmbPersonalSpace smbPersonalSpace = new SmbPersonalSpace();

    @Bean
    public SmbPersonalSpace opsTpIntegration() {
        return smbPersonalSpace;
    }

    @Data
    public static class SmbPersonalSpace {
        private Integer pageSize = 20;
        private List<Long> availableGroups = new ArrayList<>();
        private String articleLink;
    }
}
