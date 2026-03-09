package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Spring Boot アプリケーションのエントリポイント。
 *
 * <p>旧プロジェクトとの違い:</p>
 * <ul>
 *   <li>旧: web.xml に DispatcherServlet を手動定義、application-context.xml に Bean 定義</li>
 *   <li>新: @SpringBootApplication により Spring MVC / Jackson / コンポーネントスキャンを自動設定</li>
 * </ul>
 *
 * <p>SpringBootServletInitializer を継承する理由:</p>
 * <ul>
 *   <li>外部 Tomcat（Tomcat 10.x）に WAR をデプロイするためのエントリポイント</li>
 *   <li>Tomcat が WAR を読み込む際にこのクラスの configure() を呼び出し、
 *       Spring ApplicationContext を初期化する</li>
 *   <li>旧プロジェクトでは web.xml の &lt;servlet&gt; 定義が同等の役割を担っていた</li>
 * </ul>
 *
 * <p>組み込み Tomcat での直接起動（java -jar）も main() から可能。</p>
 */
@SpringBootApplication
public class ServerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    /**
     * 外部サーブレットコンテナ（Tomcat 等）から起動する際に呼ばれるメソッド。
     *
     * <p>Tomcat が WAR を読み込む際、このメソッドで Spring アプリケーションの
     * エントリポイント（ServerApplication）を登録する。</p>
     *
     * @param application SpringApplicationBuilder
     * @return 設定済み SpringApplicationBuilder
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ServerApplication.class);
    }
}
