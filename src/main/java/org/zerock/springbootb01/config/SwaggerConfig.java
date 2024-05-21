package org.zerock.springbootb01.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration

public class SwaggerConfig {
//
//  @Bean
//  public GroupedOpenApi publicApi() {
//    return GroupedOpenApi.builder()
//        .group("springshop-public")
//        .pathsToMatch("/public/**")
//        .build();
//  }

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("레스트 API 테스트 ")
            .description("댓글을 이용해서  REST 방식 테스트 하겠다.")
            .version("1.0.0"));
  }
}
